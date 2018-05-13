package com.organon.helda.core.entities;

public class Worker extends Entity {
    public String name;

    public Worker(int id) {
        setId(id);
    }

    public String getName() {
        return name;
    }

    public Worker setName(String n) {
        this.name = n;
        return this;
    }
}
