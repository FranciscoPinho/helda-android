package com.organon.helda.core;

import com.organon.helda.core.gateways.AnomalyGateway;
import com.organon.helda.core.gateways.DisassemblyGateway;
import com.organon.helda.core.gateways.PauseGateway;
import com.organon.helda.core.gateways.TaskTimeTableGateway;
import com.organon.helda.core.gateways.WorkerGateway;

public class Context {
    public AnomalyGateway anomalyGateway;
    public TaskTimeTableGateway taskTimeTableGateway;
    public DisassemblyGateway disassemblyGateway;
    public WorkerGateway workerGateway;
    public PauseGateway pauseGateway;
}
