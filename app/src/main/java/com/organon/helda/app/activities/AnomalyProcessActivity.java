package com.organon.helda.app.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.organon.helda.R;
import com.organon.helda.app.data.HTTPAnomalyGateway;
import com.organon.helda.app.services.AnomalyService;
import com.organon.helda.app.services.AnomalyService.Listener;
import com.organon.helda.app.services.PlanService;
import com.organon.helda.app.utils.Utils;
import com.organon.helda.core.entities.Plan;

import java.text.SimpleDateFormat;
import java.util.Date;


public class AnomalyProcessActivity extends AppCompatActivity {
    private BroadcastReceiver connectivity_receiver;
    private Boolean connectivity;
    private String anomalyText;
    private Plan plan;
    private int task;
    private Button skipButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anomaly_process);
        plan=(Plan)getIntent().getSerializableExtra("currentPlan");
        anomalyText = (String)getIntent().getSerializableExtra("anomalyText");
        task = (int)getIntent().getSerializableExtra("task");

        connectivity= Utils.isNetworkAvailable(this);
        connectivity_receiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                connectivity= Utils.isNetworkAvailable(context);
            }
        };
        registerReceiver(connectivity_receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        skipButton = findViewById(R.id.skipButton);
        skipButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(connectivity) {
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                    AnomalyService.insertAnomaly(1, 1,  String.valueOf(timestamp), anomalyText,task, Utils.State.skipped, new HTTPAnomalyGateway() {
                        @Override
                        public boolean disassemblyExists(int id) {
                            return false;
                        }
                    }, new Listener() {
                        @Override
                        public void onComplete(Object response) {
                            if (response == null) {
                                TextView textView = findViewById(R.id.textView3);
                                textView.setText("Erro en registro del anomalía");
                            }

                            Intent intent = new Intent(AnomalyProcessActivity.this, DisassemblyActivity.class);
                            intent.putExtra("id", (int)response);
                            intent.putExtra("task",task+1);
                            intent.putExtra("currentPlan", plan);
                            finish();
                            startActivity(intent);

                        }
                    });
                }
            }
        });

        stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(AnomalyProcessActivity.this, BarcodeReaderActivity.class);
                if(connectivity) {
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                    AnomalyService.insertAnomaly(1, 1,  String.valueOf(timestamp), anomalyText,task, Utils.State.stopped, new HTTPAnomalyGateway() {
                        @Override
                        public boolean disassemblyExists(int id) {
                            return false;
                        }
                    }, new Listener() {
                        @Override
                        public void onComplete(Object response) {
                            if (response == null) {
                                TextView textView = findViewById(R.id.textView3);
                                textView.setText("Erro en registro del anomalía");
                            }

                            intent.putExtra("id", (int)response);
                            intent.putExtra("currentPlan", plan);
                            finish();
                            startActivity(intent);

                        }
                    });

                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
