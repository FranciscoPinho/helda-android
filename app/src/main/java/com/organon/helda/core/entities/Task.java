package com.organon.helda.core.entities;

import java.io.Serializable;

public class Task extends Entity implements Serializable{
    public enum State {
        DONE,
        UNDONE,
        SKIPPED,
    }

    private String description;
    private State state;
    private int duration;
    private int offset;

    public Task(int id) {
        setId(id);
        state = State.UNDONE;
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

    public State getState() {
        return state;
    }

    public Task setState(State state) {
        this.state = state;
        return this;
    }

    public int getOffset() {
        return offset;
    }

    public Task setOffset(int offset) {
        this.offset = offset;
        return this;
    }
}
