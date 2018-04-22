package com.organon.helda.core.entities;

import java.io.Serializable;

public class Task extends Entity implements Serializable{
    private String description;

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
}
