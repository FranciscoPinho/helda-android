package com.organon.helda.core.entities;

import java.util.ArrayList;
import java.util.List;

public class Plan extends Entity {
    private String model;

    private String locale;

    private List<Task> tasks = new ArrayList<>();

    public Plan(String model, String locale) {
        this.model = model;
        this.locale = locale;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public Task getTask(int number) {
        if (number < tasks.size()) return tasks.get(number);
        return null;
    }

    public String getModel() {
        return model;
    }

    public String getLocale() {
        return locale;
    }
}
