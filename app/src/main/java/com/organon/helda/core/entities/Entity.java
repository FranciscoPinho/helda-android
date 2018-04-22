package com.organon.helda.core.entities;

import java.io.Serializable;

public abstract class Entity implements Serializable {
    private Integer id = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

