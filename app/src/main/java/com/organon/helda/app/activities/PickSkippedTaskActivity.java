package com.organon.helda.app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.organon.helda.R;
import com.organon.helda.app.adapters.PickSkippedTaskListAdapter;
import com.organon.helda.core.entities.Task;

import java.util.List;

public class PickSkippedTaskActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_skipped_task);

        Intent intent = getIntent();
        List<Task> tasks = (List<Task>)intent.getSerializableExtra("tasks");

        RecyclerView list = findViewById(R.id.pick_skipped_task_list);
        PickSkippedTaskListAdapter adapter = new PickSkippedTaskListAdapter(tasks);

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        // list.addItemDecoration(new DividerItemDecoration(list.getContext(), DividerItemDecoration.VERTICAL));
    }
}
