package com.organon.helda.app.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.organon.helda.R;
import com.organon.helda.core.entities.Plan;

import java.io.File;
import java.io.IOException;

public class AnomalyActivity extends AppCompatActivity {


    private Button registerAnomalyButton,recordAnomalyButton;
    private EditText anomalyText;
    private Plan plan;
    private Integer task;
    private MediaRecorder mRecorder = null;
    boolean mStartRecording = true;
    private static String mFileName = null;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anomaly);
        plan=(Plan)getIntent().getSerializableExtra("currentPlan");
        task=(Integer)getIntent().getSerializableExtra("task");
        //REMOVE THIS CODE AFTER US DONE
        if(plan==null && task==null){
            task=1;
            plan=new Plan("TESTE","es");
        }
        //END OF REMOVE
        mFileName = getFilesDir().getAbsolutePath();
        //include disassembly ID when Perspectives US done
        mFileName += "/"+task+"-"+plan.getModel()+".3gp";
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        anomalyText = findViewById(R.id.AnomaliaInputText);
        anomalyText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        registerAnomalyButton = findViewById(R.id.registerAnomalyButton);
        registerAnomalyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                processAnomaly();
            }
        });
        recordAnomalyButton = findViewById(R.id.grabarButton);
        recordAnomalyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    recordAnomalyButton.setText("Terminar la grabación");
                } else {
                    recordAnomalyButton.setText("Grabar voz");
                    //send multipart request here with audio
                    //processAnomaly();
                }
                mStartRecording = !mStartRecording;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    private void processAnomaly(){
        File recording = new File(mFileName);
        if(recording.exists())
            recording.delete();
        String anomaly = anomalyText.getText().toString();
        Intent intent = new Intent(AnomalyActivity.this, AnomalyProcessActivity.class);
        intent.putExtra("anomalyText", anomaly);
        intent.putExtra("currentPlan", plan);
        intent.putExtra("task", task);
        finish();
        startActivity(intent);
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if(what==MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN){
                    File recording = new File(mFileName);
                    if(recording.exists())
                        recording.delete();
                }
            }
        });
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("AudioRecord", "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}