package com.organon.helda.core.usecases.workerLogin;

import com.organon.helda.core.Context;
import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.RequestHandler;

public class WorkerLogin  extends RequestHandler<WorkerLoginRequestMessage, WorkerLoginResponseMessage> {
    public WorkerLogin(Context context) {
        super(context);
    }

    @Override
    protected boolean isValid(WorkerLoginRequestMessage request) {
        return true;
    }

    @Override
    protected WorkerLoginResponseMessage onValid(WorkerLoginRequestMessage request) {
        WorkerLoginResponseMessage response = new WorkerLoginResponseMessage();
        response.workerID = context.workerGateway.workerLogin(request.username, request.password.getBytes());
        return response;
    }

    @Override
    protected WorkerLoginResponseMessage onValidationError(WorkerLoginRequestMessage request) {
        return new WorkerLoginResponseMessage().error(getValidationError(), ErrorCode.VALIDATION_ERROR);
    }
}
