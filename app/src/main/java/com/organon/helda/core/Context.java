package com.organon.helda.core;

import com.organon.helda.core.gateways.AnomalyGateway;
import com.organon.helda.core.gateways.DisassemblyGateway;
import com.organon.helda.core.gateways.PauseGateway;
import com.organon.helda.core.gateways.PlanGateway;
import com.organon.helda.core.gateways.TaskTimeTableGateway;

public class Context {
    public AnomalyGateway anomalyGateway;
    public TaskTimeTableGateway taskTimeTableGateway;
    public DisassemblyGateway disassemblyGateway;
    public PauseGateway pauseGateway;
}
