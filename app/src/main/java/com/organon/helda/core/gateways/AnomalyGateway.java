package com.organon.helda.core.gateways;

import com.organon.helda.app.utils.Utils;

import java.security.Timestamp;

public interface AnomalyGateway {

    int insertAnomaly(int disassembly, int plan, String anomaly_date, String description, int task, Utils.State state);

    boolean disassemblyExists(int id);
}
