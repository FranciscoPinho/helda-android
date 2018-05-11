package com.organon.helda.app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.organon.helda.R;
import com.organon.helda.app.HeldaApp;
import com.organon.helda.app.services.DisassemblyService;
import com.organon.helda.app.services.ServiceHelper;
import com.organon.helda.core.entities.Disassembly;
import com.organon.helda.core.usecases.startdisassembly.StartDisassemblyResponseMessage;

public class ChooseTasksActivity extends AppCompatActivity {
    private static final String WORKER_A = "A";
    private static final String WORKER_B = "B";

    private Disassembly disassembly;
    private boolean resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_tasks);

        Intent intent = getIntent();
        disassembly = (Disassembly)intent.getSerializableExtra("disassembly");
        resume = intent.getBooleanExtra("resume",false);
        Button workerA = findViewById(R.id.startAsWorkerA);
        workerA.setOnClickListener(new ChooseTasksListener(WORKER_A));
        if (disassembly.getWorkerA() || disassembly.getWorkerADone()) {
            workerA.setVisibility(View.INVISIBLE);
        }

        Button workerB = findViewById(R.id.startAsWorkerB);
        workerB.setOnClickListener(new ChooseTasksListener(WORKER_B));
        if (disassembly.getWorkerB() || disassembly.getWorkerBDone()) {
            workerB.setVisibility(View.INVISIBLE);
        }
    }

    private class ChooseTasksListener implements View.OnClickListener {
        private String worker;

        public ChooseTasksListener(String worker) {
            this.worker = worker;
        }

        @Override
        public void onClick(View view) {
            if(!resume)
                new DisassemblyService(HeldaApp.getContext()).startDisassembly(disassembly.getId(), worker, new ServiceHelper.Listener<StartDisassemblyResponseMessage>() {
                    @Override
                    public void onComplete(StartDisassemblyResponseMessage o) {
                        Intent intent = new Intent(ChooseTasksActivity.this, DisassemblyActivity.class);
                        intent.putExtra("worker", worker);
                        intent.putExtra("currentPlan", o.plan);
                        intent.putExtra("disassemblyID", disassembly.getId());
                        finish();
                        startActivity(intent);
                    }
                });
        }
    }
}
