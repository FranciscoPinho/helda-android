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
import com.organon.helda.core.entities.Task;

import java.util.HashMap;
import java.util.Map;

public class BarcodeReaderActivity extends AppCompatActivity {

    private static final String MODEL = "1PW2A4LKNQ78FKD0";
    private static final String LOCALE = "es";
    private static final Task[] TASKS = {
            new Task("Llevar clip a caja x y la junta a mueble kanban", 0),
            new Task("Quitar insono elemento portador izquierdo", 0),
            new Task("Llevar 2 clips a la caja 5 en mueble Kanban", 0),
    };

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
                Plan plan = new Plan(MODEL, LOCALE);
                for (Task task : TASKS) plan.addTask(task);

                PlanService.getPlan(MODEL, LOCALE, new InMemoryPlanGateway(plan), new PlanService.Listener() {
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
