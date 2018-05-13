package com.organon.helda.app.services;

import com.organon.helda.app.utils.Utils;
import com.organon.helda.core.Context;
import com.organon.helda.core.gateways.AnomalyGateway;
import com.organon.helda.core.usecases.registerAnomaly.RegisterAnomaly;
import com.organon.helda.core.usecases.registerAnomaly.RegisterAnomalyRequestMessage;
import com.organon.helda.core.usecases.registerAnomaly.RegisterAnomalyResponseMessage;
import com.organon.helda.core.usecases.uploadRecording.UploadRecording;
import com.organon.helda.core.usecases.uploadRecording.UploadRecordingRequestMessage;
import com.organon.helda.core.usecases.uploadRecording.UploadRecordingResponseMessage;

import java.io.File;

public class AnomalyService {

    public interface Listener {
        void onComplete(Object response);
    }

    public static void insertAnomaly(final int disasembly,final String anomaly_date, final String description, final int task, final String workerID, final Utils.State state, final AnomalyGateway gateway, final AnomalyService.Listener listener) {
        ServiceHelper helper = new ServiceHelper(new ServiceHelper.Runnable() {
            @Override
            public Object run() {
                RegisterAnomalyRequestMessage request = new RegisterAnomalyRequestMessage();
                request.disassembly = disasembly;
                request.anomalyDate = anomaly_date;
                request.description = description;
                request.task = task;
                request.workerID = workerID;
                request.state = state;
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

    public static void uploadRecording(final int disassembly,final File payload, final AnomalyGateway gateway, final AnomalyService.Listener listener) {
        ServiceHelper helper = new ServiceHelper(new ServiceHelper.Runnable() {
            @Override
            public Object run() {
                UploadRecordingRequestMessage request = new UploadRecordingRequestMessage();
                request.payload=payload;
                request.disassembly=disassembly;
                Context executionContext = new Context();
                executionContext.anomalyGateway = gateway;
                UploadRecording interactor = new UploadRecording(executionContext);
                return interactor.handle(request);
            }
        }, new ServiceHelper.Listener() {
            @Override
            public void onComplete(Object o) {
                UploadRecordingResponseMessage response = (UploadRecordingResponseMessage)o;
                if (response.isError()){
                    System.out.println(response.getMessage());
                    listener.onComplete(null);
                }
                else listener.onComplete(response);
            }
        });
        helper.execute();
    }
}
