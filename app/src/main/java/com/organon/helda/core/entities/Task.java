package com.organon.helda.core.entities;

import java.io.Serializable;

public class Task extends Entity implements Serializable{
    private String description;

    private int duration;

    public Task(String description, int duration) {
        this.description = description;
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String toString(){
        return this.description;
    }
}
