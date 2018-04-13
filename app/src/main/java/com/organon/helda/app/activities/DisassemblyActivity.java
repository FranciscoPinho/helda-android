package com.organon.helda.app.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private static final String KWS_NEXT = "listo";
    private static final String KWS_REVERT = "volver";

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SpeechRecognizer recognizer;

    private static int task = 0;

    private Plan plan;
    private TextToSpeech repeatTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disassembly);

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
                taskViewer.setText(planStr);
            }
        });

        Button anomaliaButton = findViewById(R.id.anomaliaButton);
        anomaliaButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                repeatTTS.stop();
                Intent anomalyActivity = new Intent(DisassemblyActivity.this, AnomalyActivity.class);
                startActivity(anomalyActivity);
            }
        });
    }

    @Override
    public void onInit(int i) {
        TextView taskviewer = findViewById(R.id.taskViewer);
        taskviewer.setText(plan.getTask(task).toString());
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
                textView = findViewById(R.id.taskViewer);
                task++;
                planStr = plan.getTask(task).toString();
                textView.setText(planStr);
                break;
            case KWS_REVERT:
                textView = findViewById(R.id.taskViewer);
                if (task != 0) {
                    task--;
                }
                planStr = plan.getTask(task).toString();
                textView.setText(planStr);
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
