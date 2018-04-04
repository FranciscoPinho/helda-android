package com.organon.helda.core.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Plan extends Entity implements Serializable {
    private String model;
    private String locale;
    private List<Task> tasks;

    public Plan(String model, String locale) {
        this.model = model;
        this.locale = locale;
        this.tasks = new ArrayList<>();
    }

    public Plan(String model,String locale, List<Task> tasks){
        this.model=model;
        this.locale=locale;
        this.tasks=tasks;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public Task getTask(int number) {
        if (number < tasks.size()) return tasks.get(number);
        return null;
    }

    public List<Task> getTasks(){
        return tasks;
    }

    public String getModel() {
        return model;
    }

    public String getLocale() {
        return locale;
    }

    public String toString() {
        String string= "Model: "+this.model+"\nTasks: \n";
        for(Task t:tasks){
            string+=t.toString()+"\n";
        }
        return string;
    }
}
