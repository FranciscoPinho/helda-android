package com.organon.helda.core.gateways;

import com.organon.helda.core.entities.Task;

public interface TaskGateway {
    Task getTask(String model, int taskNumber, String locale);
}
