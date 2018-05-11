package com.organon.helda.core.entities;

import com.organon.helda.app.utils.Utils;

import java.security.Timestamp;


public class Pause extends Entity {
    private int disassembly;
    private int duration;
    private String role;

    public Pause(int disassembly, int duration, String role) {
        this.disassembly = disassembly;
        this.duration = duration;
        this.role = role;
    }

    public int getDuration() {
        return duration;
    }

    public String getRole() {
        return role;
    }


}

