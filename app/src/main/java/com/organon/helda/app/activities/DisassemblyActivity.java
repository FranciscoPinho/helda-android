package com.organon.helda.app.activities;

import android.app.Dialog;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.SystemClock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Chronometer;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.organon.helda.R;
import com.organon.helda.app.HeldaApp;
import com.organon.helda.app.data.HttpTaskTimeGateway;
import com.organon.helda.app.data.NetworkManager;
import com.organon.helda.app.fragments.SettingsFragment;
import com.organon.helda.app.utils.Utils;
import com.organon.helda.app.services.TaskTimeService;

import com.organon.helda.core.entities.Plan;
import com.organon.helda.core.entities.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class DisassemblyActivity extends AppCompatActivity implements RecognitionListener, TextToSpeech.OnInitListener {

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "keywords";

    /* Keywords for voice commands */
    private static final String KWS_NEXT = "adelante";
    private static final String KWS_REVERT = "reaparecer";
    private static final String KWS_PAUSE = "interrumpir";
    private static final String KWS_STOP_PAUSE = "reanudar";
    private static final String KWS_ANOMALY = "irregularidad";

    /* Menu options */
    private static final int SIMPLE_MENU_OPTION_ID = 1;
    private static final int GO_BACK_TO_SKIPPED_TASK_OPTION_ID = 2;

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private SpeechRecognizer recognizer;

    private List<Task> tasks;
    private static int task = 0;

    private Plan plan;
    private TextToSpeech repeatTTS;
    private int disassemblyID;

    //Used to create a pop window when pause button is clicked
    private Dialog pauseDialog;

    private Chronometer taskChronometer;
    private Chronometer pauseChronometer;

    //helps taskChronometer in pauses
    private long pauseInitialTime = 0;

    private boolean pause = false;

    private Map<Integer, Integer> taskTimeList = new HashMap<>();

    private HeldaApp app;

    private static ViewSwitcher viewSwitcher;
    private static ConstraintLayout detailedView;
    private static ConstraintLayout simpleView;

    MediaPlayer player;

    /* Menu items */
    private MenuItem modeChangeItem;
    private MenuItem goBackToSkippedTaskItem;

    private static final int PICK_SKIPPED_TASK_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disassembly);
        final Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        viewSwitcher =   findViewById(R.id.viewSwitcher);
        detailedView= findViewById(R.id.detailedMode);
        simpleView = findViewById(R.id.simpleMode);
        pauseDialog = new Dialog(this);
        app = (HeldaApp) getApplication();

        taskChronometer = findViewById(R.id.taskChronometer);

        plan=(Plan)getIntent().getSerializableExtra("currentPlan");
        disassemblyID = (int) getIntent().getSerializableExtra("disassemblyID");
        String worker = getIntent().getStringExtra("worker");
        if (worker.equals("A")) tasks = plan.getTasksWorkerA();
        if (worker.equals("B")) tasks = plan.getTasksWorkerB();

        repeatTTS = new TextToSpeech(this, this);
        repeatTTS.setLanguage(new Locale("es", "ES"));
        // Super important, this must be called on application startup
        NetworkManager.getInstance(this);

        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new DisassemblyActivity.SetupTask(this).execute();

        initializeMediaPlayer();


        TextView operarioViewer = findViewById(R.id.OperarioView);
        operarioViewer.setText("Model: " + plan.getModel().toString());
        operarioViewer.setGravity(Gravity.CENTER);
        final TextView taskViewer = findViewById(R.id.taskViewer);
        taskViewer.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {}

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (repeatTTS == null) return;
                if(repeatTTS.isSpeaking())
                    repeatTTS.stop();
                repeatTTS.speak(getCurrentTask().getDescription(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        taskViewer.setGravity(Gravity.CENTER);
        Button simpleListo = findViewById(R.id.simpleListoButton);
        simpleListo.setText(KWS_NEXT);
        final Button listoButton = findViewById(R.id.listoButton);
        listoButton.setText(KWS_NEXT);
        listoButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                taskChronometer.stop();

                //Task time in miliseconds
                int taskTimeMiliss = (int) (SystemClock.elapsedRealtime() - taskChronometer.getBase());
                taskTimeList.put(task, taskTimeMiliss);

                //at the end of the tasks, store all timetasks in database
                if(task == (tasks.size() - 1)){
                    int aux = 0;
                    while(aux <= task){
                        TaskTimeService.insertTaskTime(1, (aux+1), taskTimeList.get(aux), new HttpTaskTimeGateway(), new TaskTimeService.Listener() {
                            @Override
                            public void onComplete(Object response) {
                                if (response == null) {
                                    TextView textView = findViewById(R.id.textView3);
                                    textView.setText("Erro en registro del tiempo de la tarea");
                                }
                            }
                        });
                        aux++;
                    }

                }
                getCurrentTask().setState(Task.State.DONE);
                nextTask();

                //Reset and Start chronometer for new task
                taskChronometer.setBase(SystemClock.elapsedRealtime());
                taskChronometer.start();

                String planStr = getCurrentTask().getDescription();
                taskViewer.setText(planStr);
            }
        });

        simpleListo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listoButton.performClick();
            }
        });

        Button atrasButton = findViewById(R.id.atrasButton);
        atrasButton.setText(KWS_REVERT);
        atrasButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (task != 0) {
                    previousTask();
                    getCurrentTask().setState(Task.State.UNDONE);
                }
                String planStr = getCurrentTask().getDescription();

                taskChronometer.stop();
                //Reset and Start chronometer for new task
                taskChronometer.setBase(SystemClock.elapsedRealtime());
                taskChronometer.start();

                taskViewer.setText(planStr);
            }
        });

        Button anomaliaButton = findViewById(R.id.anomaliaButton);
        anomaliaButton.setText(KWS_ANOMALY);
        anomaliaButton.setOnClickListener(new OnClickListener() {
              public void onClick(View v) {
                  repeatTTS.stop();
                  recognizer.cancel();
                  recognizer.shutdown();
                  Intent anomalyActivity = new Intent(DisassemblyActivity.this, AnomalyActivity.class);
                  anomalyActivity.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                  anomalyActivity.putExtra("currentPlan", plan);
                  anomalyActivity.putExtra("disassemblyID", disassemblyID);
                  anomalyActivity.putExtra("task", tasks.get(task).getId());
                  startActivity(anomalyActivity);
              }
        });

              
        Button paradaButton = findViewById(R.id.paradaButton);
        paradaButton.setText(KWS_PAUSE);
        paradaButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (viewSwitcher.getCurrentView() != detailedView) {
                    viewSwitcher.showPrevious();
                    modeChangeItem.setTitle(R.string.simpleModeOption);
                }
                if(repeatTTS.isSpeaking())
                    repeatTTS.stop();
                pauseDialog.setContentView(R.layout.activity_pause);

                pause = true;

                pauseDialog.setCanceledOnTouchOutside(false);
                pauseDialog.setCancelable(false);

                Button backButton;
                backButton = (Button) pauseDialog.findViewById(R.id.reanudarButton);
                backButton.setText(KWS_STOP_PAUSE);

                pauseChronometer = (Chronometer) pauseDialog.findViewById(R.id.pauseChronometer);

                pauseInitialTime = SystemClock.elapsedRealtime();
                taskChronometer.stop();

                //Reset and Start pause Chronometer
                pauseChronometer.setBase(SystemClock.elapsedRealtime());
                pauseChronometer.start();


                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        taskChronometer.setBase(taskChronometer.getBase() + SystemClock.elapsedRealtime() - pauseInitialTime);
                        pauseInitialTime = 0;
                        taskChronometer.start();
                        repeatTTS.speak(getCurrentTask().getDescription(), TextToSpeech.QUEUE_FLUSH, null);

                        pauseDialog.dismiss();
                        pause = false;
                    }
                });
                pauseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                pauseDialog.show();

            }
        });
    }


    @Override
    public void onResume(){
        super.onResume();
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        if(repeatTTS==null && recognizer==null) {
            repeatTTS = new TextToSpeech(this, this);
            repeatTTS.setLanguage(new Locale("es", "ES"));
            new DisassemblyActivity.SetupTask(this).execute();
        }

        initializeMediaPlayer();

        switch (app.anomalyDecision) {
            case "STOP":
                findViewById(R.id.paradaButton).performClick();
                break;
            case "SKIP":
                getCurrentTask().setState(Task.State.SKIPPED);
                nextTask();
                break;
        }

        app.anomalyDecision="";
    }
    @Override
    public void onInit(int i) {

        TextView taskviewer = findViewById(R.id.taskViewer);
        taskviewer.setText(getCurrentTask().getDescription());
        taskChronometer.setBase(SystemClock.elapsedRealtime());
        taskChronometer.start();
        repeatTTS.speak(getCurrentTask().getDescription(), TextToSpeech.QUEUE_FLUSH, null);
    }

    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<DisassemblyActivity> activityReference;
        SetupTask(DisassemblyActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }
        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                activityReference.get().setupRecognizer(assetDir);
            } catch (IOException e) {
                return e;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                ((TextView) activityReference.get().findViewById(R.id.taskViewer))
                        .setText("Failed to init recognizer " + result);
            } else {
                activityReference.get().recognizer.startListening(KWS_SEARCH);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                new DisassemblyActivity.SetupTask(this).execute();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(repeatTTS != null) {
            repeatTTS.shutdown();
        }
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
        if(player != null){
            player.release();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(repeatTTS != null) {
            repeatTTS.shutdown();
            repeatTTS=null;
        }
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
            recognizer=null;
        }
        if(player != null){
            player.release();
        }
    }
    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;
        String text = hypothesis.getHypstr();

        switch (text.trim()) {
            case KWS_NEXT:
                if(!pause) {
                    playClip();
                    Button listoButton = findViewById(R.id.listoButton);
                    listoButton.performClick();
                }
                break;
            case KWS_REVERT:
                if(!pause) {
                    playClip();
                    Button atrasButton = findViewById(R.id.atrasButton);
                    atrasButton.performClick();
                }
                break;
            case KWS_PAUSE:
                if(!pause) {
                    playClip();
                    Button paradaButton = findViewById(R.id.paradaButton);
                    paradaButton.performClick();
                }
                break;
            case KWS_ANOMALY:
                if(!pause) {
                    playClip();
                    Button anomalyButton = findViewById(R.id.anomaliaButton);
                    anomalyButton.performClick();
                }
                break;
            case KWS_STOP_PAUSE:
                if(pause) {
                    playClip();
                    Button backButton = pauseDialog.findViewById(R.id.reanudarButton);
                    backButton.performClick();
                }
                break;
            default:
                break;
        }
        recognizer.stop();
    }

    public void playClip() {
        if(player.isPlaying())
            player.stop();
        player.start();
    }

    public void initializeMediaPlayer() {
        try{
            Assets assets = new Assets(this);
            File assetDir = assets.syncAssets();
            File soundClip = new File(assetDir,"blip.wav");
            player = MediaPlayer.create(this, Uri.parse(soundClip.getAbsolutePath()));
            player.setLooping(false);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        recognizer.startListening(KWS_SEARCH);
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        return;
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "spa-eu-ptm"))
                .setDictionary(new File(assetsDir, "es.dict"))
                .getRecognizer();
        recognizer.addListener(this);

        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.

        File keywordsGrammar = new File(assetsDir, "keywords.gram");
        recognizer.addKeywordSearch(KWS_SEARCH, keywordsGrammar);
    }

    @Override
    public void onError(Exception error) {
        return;
    }

    @Override
    public void onTimeout() {
        return;
    }

    private Task getCurrentTask() {
        return tasks.get(task);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        modeChangeItem = menu.add(0, SIMPLE_MENU_OPTION_ID, 0, R.string.simpleModeOption);
        modeChangeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        goBackToSkippedTaskItem = menu.add(0, GO_BACK_TO_SKIPPED_TASK_OPTION_ID, 0, R.string.goBackToSkippedTaskOption);
        goBackToSkippedTaskItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .addToBackStack("settings")
                    .commit();
            return true;
        }

        if (id == SIMPLE_MENU_OPTION_ID){
            if (viewSwitcher.getCurrentView() != detailedView){
                item.setTitle(R.string.simpleModeOption);
                viewSwitcher.showPrevious();
            } else if (viewSwitcher.getCurrentView() != simpleView){
                item.setTitle(R.string.detailedModeOption);
                viewSwitcher.showNext();
            }
            return true;
        }

        if (id == GO_BACK_TO_SKIPPED_TASK_OPTION_ID) {
            ArrayList<Task> tasks = (ArrayList<Task>) getSkippedTasks();
            if (tasks.isEmpty()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Ninguna tarea fue omitida", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Intent intent = new Intent(this, PickSkippedTaskActivity.class);
                intent.putExtra("tasks", tasks);
                startActivityForResult(intent, PICK_SKIPPED_TASK_REQUEST);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Utils.resetSystemProperties(sharedPref);
            getFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_SKIPPED_TASK_REQUEST) {
            if (resultCode == RESULT_OK) {
                int skippedTask = data.getIntExtra("task", -1);
                if (task < 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Algo fue mal", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                task = skippedTask;

                taskChronometer.setBase(SystemClock.elapsedRealtime());
                taskChronometer.start();
                TextView taskViewer = findViewById(R.id.taskViewer);
                String planStr = getCurrentTask().getDescription();
                taskViewer.setText(planStr);
            }
        }
    }

    private void nextTask() {
        task++;
    }

    private void previousTask() {
        task--;
    }

    private List<Task> getSkippedTasks() {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getState() == Task.State.SKIPPED) {
                result.add(task);
            }
        }
        return result;
    }
}
