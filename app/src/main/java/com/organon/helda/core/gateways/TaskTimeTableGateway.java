package com.organon.helda.core.gateways;

public interface TaskTimeTableGateway {
    int insertUpdateTaskTime(int disassembly, int taskId, int taskTime, String role, String workerID);
}
