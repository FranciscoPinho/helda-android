package com.organon.helda.app.services;

import com.organon.helda.app.utils.Utils;
import com.organon.helda.core.Context;
import com.organon.helda.core.gateways.AnomalyGateway;
import com.organon.helda.core.usecases.registerAnomaly.RegisterAnomaly;
import com.organon.helda.core.usecases.registerAnomaly.RegisterAnomalyRequestMessage;
import com.organon.helda.core.usecases.registerAnomaly.RegisterAnomalyResponseMessage;

public class AnomalyService {

    public interface Listener {
        void onComplete(Object response);
    }

    public static void insertAnomaly(final int dissasembly, final int plan, final String anomaly_date, final String description, final int task, final Utils.State state, final AnomalyGateway gateway, final AnomalyService.Listener listener) {
        ServiceHelper helper = new ServiceHelper(new ServiceHelper.Runnable() {
            @Override
            public Object run() {
                RegisterAnomalyRequestMessage request = new RegisterAnomalyRequestMessage();
                request.disassembly = dissasembly;
                request.anomalyDate = anomaly_date;
                request.description = description;
                request.task = task;
                request.state = state;
                request.plan = plan;
                Context executionContext = new Context();
                executionContext.anomalyGateway = gateway;
                RegisterAnomaly interactor = new RegisterAnomaly(executionContext);
                return interactor.handle(request);
            }
        }, new ServiceHelper.Listener() {
            @Override
            public void onComplete(Object o) {
                RegisterAnomalyResponseMessage response = (RegisterAnomalyResponseMessage)o;
                if (response.isError()){
                    listener.onComplete(null);
                }
                else listener.onComplete(response.id);
            }
        });
        helper.execute();
    }
}
