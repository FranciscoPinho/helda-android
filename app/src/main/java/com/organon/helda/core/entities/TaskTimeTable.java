package com.organon.helda.core.entities;

import com.organon.helda.app.utils.Utils;

import java.security.Timestamp;


public class TaskTimeTable extends Entity {
    private int disassembly;
    private int task_id;
    private int task_time;

    public TaskTimeTable(int disassembly, int task_id, int task_time) {
        this.disassembly = disassembly;
        this.task_id = task_id;
        this.task_time = task_time;
    }

    public int getTask() {
        return task_id;
    }

    public int getTime() {
        return task_time;
    }


}
