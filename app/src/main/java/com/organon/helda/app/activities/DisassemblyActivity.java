package com.organon.helda.app.activities;

import android.app.Dialog;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Chronometer;

import com.organon.helda.R;
import com.organon.helda.app.data.NetworkManager;
import com.organon.helda.core.entities.Plan;

import java.io.File;
import java.util.Locale;
import java.io.IOException;
import java.lang.ref.WeakReference;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class DisassemblyActivity extends AppCompatActivity implements RecognitionListener, TextToSpeech.OnInitListener {

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "keywords";
    private static final String KWS_NEXT = "adelante";
    private static final String KWS_REVERT = "volver";
    private static final String KWS_PAUSE = "detener";
    private static final String KWS_STOP_PAUSE = "reanudar";


    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SpeechRecognizer recognizer;

    private static int task = 0;

    private Plan plan;
    private TextToSpeech repeatTTS;

    //Used to create a pop window when pause button is clicked
    private Dialog pauseDialog;

    private Chronometer taskChronometer;
    private Chronometer pauseChronometer;

    //helps taskChronometer in pauses
    private long pauseInitialTime = 0;

    private boolean pause = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disassembly);
        pauseDialog = new Dialog(this);

        taskChronometer = (Chronometer) findViewById(R.id.taskChronometer);

        plan=(Plan)getIntent().getSerializableExtra("currentPlan");
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
                if(repeatTTS.isSpeaking())
                    repeatTTS.stop();
                repeatTTS.speak(plan.getTask(task).toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        taskViewer.setGravity(Gravity.CENTER);

        Button listoButton = findViewById(R.id.listoButton);
        listoButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //to see a string representation of the plan currently in this activity

                task++;
                String planStr = plan.getTask(task).toString();

                taskChronometer.stop();
                //Reset and Start chronometer for new task
                taskChronometer.setBase(SystemClock.elapsedRealtime());
                taskChronometer.start();

                taskViewer.setText(planStr);

            }
        });
        Button atrasButton = findViewById(R.id.atrasButton);
        atrasButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (task != 0) {
                    task--;
                }
                String planStr = plan.getTask(task).toString();

                taskChronometer.stop();
                //Reset and Start chronometer for new task
                taskChronometer.setBase(SystemClock.elapsedRealtime());
                taskChronometer.start();

                taskViewer.setText(planStr);
            }
        });
        Button paradaButton = findViewById(R.id.paradaButton);
        paradaButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                pauseDialog.setContentView(R.layout.activity_pause);

                pause = true;

                pauseDialog.setCanceledOnTouchOutside(false);
                pauseDialog.setCancelable(false);

                Button backButton;
                backButton = (Button) pauseDialog.findViewById(R.id.reanudarButton);

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
    public void onInit(int i) {
        TextView taskviewer = findViewById(R.id.taskViewer);
        taskviewer.setText(plan.getTask(task).toString());
        taskChronometer.setBase(SystemClock.elapsedRealtime());
        taskChronometer.start();
        repeatTTS.speak(plan.getTask(task).toString(), TextToSpeech.QUEUE_FLUSH, null);
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

        TextView textView;
        String planStr;
        String text = hypothesis.getHypstr();
        switch (text) {
            case KWS_NEXT:
                if(!pause) {
                    Button listoButton = findViewById(R.id.listoButton);
                    listoButton.performClick();
                }
                break;
            case KWS_REVERT:
                if(!pause) {
                    Button atrasButton = findViewById(R.id.atrasButton);
                    atrasButton.performClick();
                }
                break;

            case KWS_PAUSE:
                if(!pause) {
                    Button paradaButton = findViewById(R.id.paradaButton);
                    paradaButton.performClick();
                }
                break;

            case KWS_STOP_PAUSE:
                if(pause) {
                    Button backButton = pauseDialog.findViewById(R.id.reanudarButton);
                    backButton.performClick();
                }
                break;

            default:
                break;
        }
        recognizer.stop();
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
}
