package com.organon.helda.core.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Plan extends Entity {
    private String model;
    private String locale;
    private List<Task> tasksWorkerA;
    private List<Task> tasksWorkerB;
    private Date modified;

    public Plan(int id) {
        setId(id);
    }

    public String getModel() {
        return model;
    }

    public Plan setModel(String model) {
        this.model = model;
        return this;
    }

    public String getLocale() {
        return locale;
    }

    public Plan setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public Date getModified() {
        return modified;
    }

    public Plan setModified(Date modified) {
        this.modified = modified;
        return this;
    }

    public List<Task> getTasksWorkerA() {
        return tasksWorkerA;
    }

    public Plan setTasksWorkerA(List<Task> tasks) {
        tasksWorkerA = tasks;
        return this;
    }

    public List<Task> getTasksWorkerB() {
        return tasksWorkerB;
    }

    public Plan setTasksWorkerB(List<Task> tasks) {
        tasksWorkerB = tasks;
        return this;
    }
}
