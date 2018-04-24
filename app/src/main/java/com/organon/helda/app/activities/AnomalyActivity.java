package com.organon.helda.app.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.organon.helda.R;
import com.organon.helda.app.data.HttpAnomalyGateway;
import com.organon.helda.app.services.AnomalyService;
import com.organon.helda.app.utils.Utils;
import com.organon.helda.core.entities.Plan;

import java.io.File;
import java.io.IOException;

public class AnomalyActivity extends AppCompatActivity {


    private Button registerAnomalyButton,recordAnomalyButton;
    private EditText anomalyText;
    private BroadcastReceiver connectivity_receiver;
    private Boolean connectivity;
    private Plan plan;
    private Integer task;
    private Integer disassemblyID;
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
        disassemblyID=(int)getIntent().getSerializableExtra("disassemblyID");
        connectivity= Utils.isNetworkAvailable(this);
        connectivity_receiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                connectivity= Utils.isNetworkAvailable(context);
            }
        };
        registerReceiver(connectivity_receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        mFileName = getFilesDir().getAbsolutePath();
        //include disassembly ID when Perspectives US done
        mFileName += "/"+task+"-"+plan.getModel()+".mp4";
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        anomalyText = findViewById(R.id.AnomaliaInputText);
        setupRecordingButtonListeners();

    }

    private void setupRecordingButtonListeners(){
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
                    recordAnomalyButton.setText("Terminar la grabación e enviar");
                    registerAnomalyButton.setEnabled(false);
                } else {
                    recordAnomalyButton.setText("Uploading...");
                    uploadRecording();
                }
                mStartRecording = !mStartRecording;
            }
        });
    }

    public void uploadRecording(){
        if(connectivity) {
            AnomalyService.uploadRecording(1,new File(mFileName), new HttpAnomalyGateway(), new AnomalyService.Listener() {
                @Override
                public void onComplete(Object response) {
                    if (response == null) {
                        TextView textView = findViewById(R.id.textView3);
                        textView.setText("Erro en enviar la grabación");
                        recordAnomalyButton.setText("Grabar voz");
                        registerAnomalyButton.setEnabled(true);
                    }
                    else processAnomaly();
                }
            });
        }
        else {
            TextView textView = findViewById(R.id.textView3);
            textView.setText(R.string.noConnection);
        }
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
        intent.putExtra("disassemblyID", disassemblyID);
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
                File recording = new File(mFileName);
                switch(what){
                    case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                        recordAnomalyButton.performClick();
                        break;
                    case MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN:
                        if(recording.exists())
                            recording.delete();
                        break;
                }
            }
        });
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setMaxDuration(60000);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("AudioRecord", "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();
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
        unregisterReceiver(connectivity_receiver);
        if(mRecorder!=null)
            stopRecording();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

}