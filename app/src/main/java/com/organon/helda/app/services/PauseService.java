package com.organon.helda.app.services;

import com.organon.helda.app.utils.Utils;
import com.organon.helda.core.Context;
import com.organon.helda.core.gateways.AnomalyGateway;
import com.organon.helda.core.gateways.PauseGateway;
import com.organon.helda.core.usecases.registerAnomaly.RegisterAnomalyResponseMessage;
import com.organon.helda.core.usecases.registerPause.registerPause;
import com.organon.helda.core.usecases.registerPause.registerPauseRequestMessage;
import com.organon.helda.core.usecases.registerPause.registerPauseResponseMessage;
import com.organon.helda.core.usecases.uploadRecording.UploadRecording;
import com.organon.helda.core.usecases.uploadRecording.UploadRecordingRequestMessage;
import com.organon.helda.core.usecases.uploadRecording.UploadRecordingResponseMessage;

import java.io.File;

public class PauseService {

    public interface Listener {
        void onComplete(Object response);
    }

    public static void insertPause(final int disasembly, final int duration, final String role, final PauseGateway gateway, final PauseService.Listener listener) {
        ServiceHelper helper = new ServiceHelper(new ServiceHelper.Runnable() {
            @Override
            public Object run() {
                registerPauseRequestMessage request = new registerPauseRequestMessage();
                request.disassembly = disasembly;
                request.duration = duration;
                request.role = role;
                Context executionContext = new Context();
                executionContext.pauseGateway = gateway;
                registerPause interactor = new registerPause(executionContext);
                return interactor.handle(request);
            }
        }, new ServiceHelper.Listener() {
            @Override
            public void onComplete(Object o) {
                registerPauseResponseMessage response = (registerPauseResponseMessage)o;
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
