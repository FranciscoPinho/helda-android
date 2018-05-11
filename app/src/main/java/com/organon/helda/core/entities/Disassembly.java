package com.organon.helda.core.entities;

public class Disassembly extends Entity {
    private int plan;
    private boolean workerA;
    private boolean workerB;
    private boolean workerADone;
    private boolean workerBDone;

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

    public boolean getWorkerBDone() {
        return workerBDone;
    }

    public Disassembly setWorkerBDone(boolean workerBDone) {
        this.workerBDone = workerBDone;
        return this;
    }

    public boolean getWorkerADone() {
        return workerADone;
    }

    public Disassembly setWorkerADone(boolean workerADone) {
        this.workerADone = workerADone;
        return this;
    }
}
