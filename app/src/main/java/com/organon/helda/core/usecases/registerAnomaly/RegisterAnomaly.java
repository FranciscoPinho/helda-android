package com.organon.helda.core.usecases.registerAnomaly;

import android.content.Context;

import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.RequestHandler;

public class RegisterAnomaly extends RequestHandler<RegisterAnomalyRequestMessage, RegisterAnomalyResponseMessage> {


    public RegisterAnomaly(com.organon.helda.core.Context context) {
        super(context);
    }

    @Override
    protected boolean isValid(RegisterAnomalyRequestMessage request) {
        if(request.task < 0) {
            setValidationError("Task must be a positive integer!");
            return false;
        }
        if(request.description == "" || request.description == null){
            setValidationError("Description must be a non-empty string!");
            return false;
        }
        if(request.anomalyDate == null){
            setValidationError("Date must be a timestamp without time zone!");
            return false;
        }

        if(request.disassembly < 0) {
            setValidationError("Disassembly must be a positive integer!");
            return false;
        }
        return true;
    }

    @Override
    protected RegisterAnomalyResponseMessage onValid(RegisterAnomalyRequestMessage request) {
        int id = context.anomalyGateway.insertAnomaly(1, 1, request.anomalyDate, request.description, 1, request.state);
        RegisterAnomalyResponseMessage response = new RegisterAnomalyResponseMessage();
        response.id = id;
        return response;
    }

    @Override
    protected RegisterAnomalyResponseMessage onValidationError(RegisterAnomalyRequestMessage request) {
        return new RegisterAnomalyResponseMessage().error(getValidationError(), ErrorCode.VALIDATION_ERROR);
    }

}