package com.organon.helda.app.services;

import com.organon.helda.app.utils.Utils;
import com.organon.helda.core.Context;
import com.organon.helda.core.gateways.AnomalyGateway;
import com.organon.helda.core.gateways.TaskTimeTableGateway;
import com.organon.helda.core.usecases.registerAnomaly.RegisterAnomalyResponseMessage;
import com.organon.helda.core.usecases.registerTaskTime.registerTaskTime;
import com.organon.helda.core.usecases.registerTaskTime.registerTaskTimeRequestMessage;
import com.organon.helda.core.usecases.registerTaskTime.registerTaskTimeResponseMessage;
import com.organon.helda.core.usecases.uploadRecording.UploadRecording;
import com.organon.helda.core.usecases.uploadRecording.UploadRecordingRequestMessage;
import com.organon.helda.core.usecases.uploadRecording.UploadRecordingResponseMessage;

import java.io.File;

public class TaskTimeService {

    public interface Listener {
        void onComplete(Object response);
    }

    public static void insertTaskTime(final int disasembly, final int task_id, final int task_time, final TaskTimeTableGateway gateway, final TaskTimeService.Listener listener) {
        ServiceHelper helper = new ServiceHelper(new ServiceHelper.Runnable() {
            @Override
            public Object run() {
                registerTaskTimeRequestMessage request = new registerTaskTimeRequestMessage();
                request.disassembly = disasembly;
                request.task_id = task_id;
                request.task_time = task_time;
                Context executionContext = new Context();
                executionContext.taskTimeTableGateway = gateway;
                registerTaskTime interactor = new registerTaskTime(executionContext);
                return interactor.handle(request);
            }
        }, new ServiceHelper.Listener() {
            @Override
            public void onComplete(Object o) {
                registerTaskTimeResponseMessage response = (registerTaskTimeResponseMessage)o;
                if (response.isError()){
                    listener.onComplete(null);
                }
                else listener.onComplete(response);
            }
        });
        helper.execute();
    }
}
