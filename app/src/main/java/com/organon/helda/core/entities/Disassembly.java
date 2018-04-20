package com.organon.helda.core.entities;

public class Disassembly extends Entity {
    private int planId;

    public Disassembly(int id) {
        setId(id);
    }

    public int getPlanId() {
        return planId;
    }

    public Disassembly setPlanId(int planId) {
        this.planId = planId;
        return this;
    }
}
