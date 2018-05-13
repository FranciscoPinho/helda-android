package com.organon.helda.core.gateways;

import com.organon.helda.core.entities.Worker;

import java.util.List;

public interface WorkerGateway {
    int workerLogin(String username, byte[] attempt);
    List<Worker> listWorkers();
}
