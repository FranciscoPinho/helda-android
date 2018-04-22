package com.organon.helda.core;

import com.organon.helda.core.entities.Anomaly;
import com.organon.helda.core.gateways.AnomalyGateway;
import com.organon.helda.core.gateways.PlanGateway;
import com.organon.helda.core.gateways.TaskTimeTableGateway;

public class Context {
    public PlanGateway planGateway;
    public AnomalyGateway anomalyGateway;
    public TaskTimeTableGateway taskTimeTableGateway;
}
