package com.organon.helda.core.entities;

public class Disassembly extends Entity {
    private int plan;
    private boolean workerA;
    private boolean workerB;

    public Disassembly(int id) {
        setId(id);
    }

    public int getPlan() {
        return plan;
    }

    public Disassembly setPlan(int plan) {
        this.plan = plan;
        return this;
    }

    public boolean getWorkerA() {
        return workerA;
    }

    public Disassembly setWorkerA(boolean workerA) {
        this.workerA = workerA;
        return this;
    }

    public boolean getWorkerB() {
        return workerB;
    }

    public Disassembly setWorkerB(boolean workerB) {
        this.workerB = workerB;
        return this;
    }
}
