package com.organon.helda.core.gateways;

import com.organon.helda.app.utils.Utils;

import java.io.File;

public interface AnomalyGateway {

    int insertAnomaly(int disassembly, String anomaly_date, String description, int task, String workerID, Utils.State state);
    Boolean sendRecording(File payload, int disassembly);
}
