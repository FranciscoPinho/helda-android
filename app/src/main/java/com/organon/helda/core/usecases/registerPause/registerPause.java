package com.organon.helda.core.usecases.registerPause;

import com.organon.helda.core.Context;
import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.RequestHandler;

import com.organon.helda.core.Context;
import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.RequestHandler;

public class registerPause extends RequestHandler<registerPauseRequestMessage, registerPauseResponseMessage> {

    public registerPause(Context context) {
        super(context);
    }

    @Override
    protected boolean isValid(registerPauseRequestMessage request) {
        if(request.disassembly <= 0) {
            setValidationError("Disassembly must be a positive integer!");
            return false;
        }

        if(request.duration <= 0) {
            setValidationError("Duration must be a positive integer!");
            return false;
        }

        if (!request.role.equals("A") && !request.role.equals("B")) {
            setValidationError("Role must be A or B");
            return false;
        }

        return true;
    }

    @Override
    protected registerPauseResponseMessage onValid(registerPauseRequestMessage request) {
        context.pauseGateway.insertPause(request.disassembly, request.duration, request.role);
        registerPauseResponseMessage responseMessage = new registerPauseResponseMessage();
        return responseMessage;
    }

    @Override
    protected registerPauseResponseMessage onValidationError(registerPauseRequestMessage request) {
        return new registerPauseResponseMessage().error(getValidationError(), ErrorCode.VALIDATION_ERROR);
    }

}

