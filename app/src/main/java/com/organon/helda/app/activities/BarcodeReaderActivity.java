package com.organon.helda.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.organon.helda.app.data.InMemoryPlanGateway;
import com.organon.helda.app.services.PlanService;
import com.organon.helda.core.entities.Plan;
import com.organon.helda.R;

public class BarcodeReaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlanService.getPlan("somemodel", "es", new InMemoryPlanGateway(), new PlanService.Listener() {
                    @Override
                    public void onComplete(Object response) {
                        Intent intent = new Intent(BarcodeReaderActivity.this,DisassemblyActivity.class);
                        intent.putExtra("currentPlan", (Plan)response);
                        startActivity(intent);
                    }
                });
            }
        });
    }

}
