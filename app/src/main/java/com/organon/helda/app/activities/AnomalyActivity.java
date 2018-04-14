package com.organon.helda.app.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.organon.helda.R;
import com.organon.helda.app.utils.Utils;

public class AnomalyActivity extends AppCompatActivity {

    BroadcastReceiver connectivity_receiver;
    Boolean connectivity;
    Button registerAnomalyButton;
    EditText anomalyText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anomaly);


        anomalyText = findViewById(R.id.AnomaliaInputText);
        anomalyText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        connectivity= Utils.isNetworkAvailable(this);
        connectivity_receiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                connectivity=Utils.isNetworkAvailable(context);
            }
        };
        registerReceiver(connectivity_receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));


        registerAnomalyButton = findViewById(R.id.registerAnomalyButton);
        registerAnomalyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String anomaly = anomalyText.getText().toString();

            }
        });
    }

}