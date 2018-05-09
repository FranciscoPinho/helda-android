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

        if(request.taskId <= 0) {
            setValidationError("Task must be a positive integer!");
            return false;
        }

        if(request.taskTime <= 0) {
            setValidationError("Time must be a positive integer!");
            return false;
        }

        if (request.role != "A" && request.role != "B") {
            setValidationError("Role must be A or B");
            return false;
        }

        return true;
    }

    @Override
    protected registerTaskTimeResponseMessage onValid(registerTaskTimeRequestMessage request) {
        context.taskTimeTableGateway.insertUpdateTaskTime(request.disassembly, request.taskId, request.taskTime, request.role);
        registerTaskTimeResponseMessage responseMessage = new registerTaskTimeResponseMessage();
        return responseMessage;
    }

    @Override
    protected registerTaskTimeResponseMessage onValidationError(registerTaskTimeRequestMessage request) {
        return new registerTaskTimeResponseMessage().error(getValidationError(), ErrorCode.VALIDATION_ERROR);
    }

}

