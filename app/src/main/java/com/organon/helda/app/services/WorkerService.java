package com.organon.helda.app.services;

import com.organon.helda.core.Context;
import com.organon.helda.core.usecases.workerLogin.WorkerLogin;
import com.organon.helda.core.usecases.workerLogin.WorkerLoginRequestMessage;
import com.organon.helda.core.usecases.workerLogin.WorkerLoginResponseMessage;

public class WorkerService {
    private Context context;

    public WorkerService(Context context) {
        this.context = context;
    }

    public void workerLogin(final String username, final String password, final ServiceHelper.Listener<WorkerLoginResponseMessage> listener) {
        ServiceHelper helper = new ServiceHelper(new ServiceHelper.Runnable() {
            @Override
            public Object run() {
                WorkerLoginRequestMessage request = new WorkerLoginRequestMessage();
                request.username = username;
                request.password = password;
                WorkerLogin interactor = new WorkerLogin(context);
                return interactor.handle(request);
            }
        }, new ServiceHelper.Listener() {
            @Override
            public void onComplete(Object o) {
                WorkerLoginResponseMessage response = (WorkerLoginResponseMessage)o;
                listener.onComplete(response);
            }
        });
        helper.execute();
    }
}
