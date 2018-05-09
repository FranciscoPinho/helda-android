package com.organon.helda.core.entities;

import com.organon.helda.app.utils.Utils;

import java.security.Timestamp;

/**
 * Created by marga on 4/14/2018.
 */

public class Anomaly extends Entity {
    private int disassembly;
    private String anomalydate;
    private String description;
    private int task;
    private int plan;
    private Utils.State state;

    public Anomaly(int disassembly, int plan, String description, String anomalydate,int task, Utils.State state) {
        this.description = description;
        this.anomalydate = anomalydate;
        this.state = state;
        this.disassembly = disassembly;
        this.task = task;
        this.plan = plan;
    }

    public int getDisassembly() {
        return disassembly;
    }

    public String getAnomalydate() {
        return anomalydate;
    }

    public String getDescription() {
        return description;
    }

    public int getTask() {
        return task;
    }

    public Utils.State getState() {
        return state;
    }
}