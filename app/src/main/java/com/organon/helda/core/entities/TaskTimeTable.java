package com.organon.helda.core.entities;

import com.organon.helda.app.utils.Utils;

import java.security.Timestamp;


public class TaskTimeTable extends Entity {
    private int disassembly;
    private int taskId;
    private int taskTime;
    private String role;

    public TaskTimeTable(int disassembly, int taskId, int taskTime, String role) {
        this.disassembly = disassembly;
        this.taskId = taskId;
        this.taskTime = taskTime;
        this.role = role;
    }

    public int getTask() {
        return taskId;
    }

    public int getTime() {
        return taskTime;
    }

    public String getRole() {
        return role;
    }


}

