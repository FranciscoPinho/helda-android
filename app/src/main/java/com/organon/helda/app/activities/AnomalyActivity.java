package com.organon.helda.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.organon.helda.R;
import com.organon.helda.core.entities.Plan;

public class AnomalyActivity extends AppCompatActivity {


    private Button registerAnomalyButton;
    private EditText anomalyText;
    private Plan plan;
    private int task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anomaly);
        plan=(Plan)getIntent().getSerializableExtra("currentPlan");
        task=(int)getIntent().getSerializableExtra("task");
        anomalyText = findViewById(R.id.AnomaliaInputText);
        anomalyText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        registerAnomalyButton = findViewById(R.id.registerAnomalyButton);
        registerAnomalyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String anomaly = anomalyText.getText().toString();
                Intent intent = new Intent(AnomalyActivity.this, AnomalyProcessActivity.class);
                intent.putExtra("anomalyText", anomaly);
                intent.putExtra("currentPlan", plan);
                intent.putExtra("task", task);
                finish();
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}