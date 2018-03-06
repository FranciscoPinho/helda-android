package com.organon.helda.app.services;

import com.organon.helda.app.data.HttpTaskGateway;
import com.organon.helda.core.usecases.GetTask;
import com.organon.helda.core.usecases.GetTaskRequestMessage;
import com.organon.helda.core.usecases.GetTaskResponseMessage;
import com.organon.helda.core.usecases.GetTaskValidator;

public class TaskService {
    public interface Listener {
        void onComplete(Object response);
    }

    public static void getTaskDescription(final String planModel, final int taskNumber, final String locale, final Listener listener) {
        ServiceHelper helper = new ServiceHelper(new ServiceHelper.Runnable() {
            @Override
            public Object run() {
                GetTaskRequestMessage request = new GetTaskRequestMessage();
                request.planModel = planModel;
                request.taskNumber = taskNumber;
                request.locale = locale;

                GetTask interactor = new GetTask(new GetTaskValidator(), new HttpTaskGateway());
                return interactor.handle(request);
            }
        }, new ServiceHelper.Listener() {
            @Override
            public void onComplete(Object o) {
                GetTaskResponseMessage response = (GetTaskResponseMessage)o;
                if (response.isError()) listener.onComplete(response.getMessage());
                else listener.onComplete(response.taskDescription);
            }
        });
        helper.execute();
    }
}
