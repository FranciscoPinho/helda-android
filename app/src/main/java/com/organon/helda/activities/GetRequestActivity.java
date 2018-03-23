package com.organon.helda.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.organon.helda.R;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;


import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;


public class GetRequestActivity extends AppCompatActivity implements RecognitionListener {

    /* Named searches allow to quickly reconfigure the decoder */
    private static String KWS_SEARCH = "adelante";
    private int nr_recognitions = 0;
    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private float recognizer_precision = (float)1e-27;
    private SpeechRecognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_request);

        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new GetRequestActivity.SetupTask( this).execute();
        EditText keyword_filed = findViewById(R.id.keyword);
        keyword_filed.setText(KWS_SEARCH);

        Button button = findViewById(R.id.change_word);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EditText keyword_filed = findViewById(R.id.keyword);
                GetRequestActivity.KWS_SEARCH=keyword_filed.getText().toString();
                nr_recognitions=0;
                TextView textView = findViewById(R.id.textView3);
                textView.setText(R.string.waitRecognizer);
                new GetRequestActivity.SetupTask(GetRequestActivity.this).execute();
            }
        });
        Button less_precision = findViewById(R.id.less_precision);
        less_precision.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                recognizer_precision*=1e-3;
                TextView textView = findViewById(R.id.textView3);
                textView.setText(R.string.waitRecognizer);
                new GetRequestActivity.SetupTask(GetRequestActivity.this).execute();
            }
        });
        Button plus_precision = findViewById(R.id.plus_precision);
        plus_precision.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(recognizer_precision<0.9)
                    recognizer_precision*=1e3;
                TextView textView = findViewById(R.id.textView3);
                textView.setText(R.string.waitRecognizer);
                new GetRequestActivity.SetupTask(GetRequestActivity.this).execute();
            }
        });

    }

    private static class SetupTask extends AsyncTask<Void, Void, Exception> {
        WeakReference<GetRequestActivity> activityReference;
        SetupTask(GetRequestActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }
        @Override
        protected Exception doInBackground(Void... params) {
            try {
                Assets assets = new Assets(activityReference.get());
                File assetDir = assets.syncAssets();
                activityReference.get().setupRecognizer(assetDir);
                activityReference.get().notifyRecognizerReady();
            } catch (IOException e) {
                return e;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Exception result) {
            if (result != null) {
                ((TextView) activityReference.get().findViewById(R.id.textView))
                        .setText(R.string.recognizerFail + "" + result);
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
                new GetRequestActivity.SetupTask(this).execute();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

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

        String text = hypothesis.getHypstr();

        if (text.equals(KWS_SEARCH)){
            recognizer.stop();
        }
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            TextView textView =(TextView) findViewById(R.id.textView);

            nr_recognitions++;
            textView.setText("Detected correct keyword "+nr_recognitions+ " times!");
            recognizer.startListening(KWS_SEARCH);
        }

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


    private void notifyRecognizerReady() {
        //this needs to be done because only the original thread that created a view hierarchy can touch its views.
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                TextView textView =(TextView) findViewById(R.id.textView3);
                textView.setText("Recognizer ready!!!\nKEYWORD: "+ KWS_SEARCH + "\nPRECISION(MAX=1.0): "+recognizer_precision);
            }
        };
        mainHandler.post(myRunnable);

    }
    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them
        if(recognizer!=null){
            recognizer.cancel();
            recognizer.shutdown();
        }

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "spa-eu-ptm"))
                .setDictionary(new File(assetsDir, "es.dict"))
                .setKeywordThreshold(recognizer_precision)
                .getRecognizer();
        recognizer.addListener(this);

        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */
        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KWS_SEARCH);
    }

    @Override
    public void onError(Exception error) {
    }

    @Override
    public void onTimeout() {
    }
}
