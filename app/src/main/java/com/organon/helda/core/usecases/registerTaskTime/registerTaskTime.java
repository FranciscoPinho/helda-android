package com.organon.helda.core.usecases.registerTaskTime;

import com.organon.helda.core.Context;
import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.RequestHandler;

import com.organon.helda.core.Context;
import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.RequestHandler;

public class registerTaskTime extends RequestHandler<registerTaskTimeRequestMessage, registerTaskTimeResponseMessage> {

    public registerTaskTime(Context context) {
        super(context);
    }

    @Override
    protected boolean isValid(registerTaskTimeRequestMessage request) {
        if(request.disassembly <= 0) {
            setValidationError("Disassembly must be a positive integer!");
            return false;
        }

        if(request.task_id <= 0) {
            setValidationError("Task must be a positive integer!");
            return false;
        }

        if(request.task_time <= 0) {
            setValidationError("Time must be a positive integer!");
            return false;
        }

        return true;
    }

    @Override
    protected registerTaskTimeResponseMessage onValid(registerTaskTimeRequestMessage request) {
        context.taskTimeTableGateway.insertTaskTime(request.disassembly, request.task_id, request.task_time);
        registerTaskTimeResponseMessage responseMessage = new registerTaskTimeResponseMessage();
        return responseMessage;
    }

    @Override
    protected registerTaskTimeResponseMessage onValidationError(registerTaskTimeRequestMessage request) {
        return new registerTaskTimeResponseMessage().error(getValidationError(), ErrorCode.VALIDATION_ERROR);
    }

}

