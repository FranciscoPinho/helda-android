package com.organon.helda.core.entities;

import java.io.Serializable;

public class Task extends Entity implements Serializable{
    private static final int STATE_DONE = 1;
    private static final int STATE_UNDONE = 0;
    private static final int STATE_SKIPPED = -1;

    private String description;
    private int state = STATE_UNDONE;

    private int duration;

    public Task() {}

    public Task(int id) {
        setId(id);
    }

    public String getDescription() {
        return description;
    }

    public Task setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public Task setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public int getState() {
        return state;
    }
    public void skiped() {
        state = STATE_SKIPPED;
    }
    public void resumed() {
        state = STATE_UNDONE;
    }
    public void done() {
        state = STATE_DONE;
    }
}
