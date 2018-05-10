package com.organon.helda.core.usecases.completedisassembly;

import com.organon.helda.core.Context;
import com.organon.helda.core.entities.Disassembly;
import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.RequestHandler;

public class CompleteDisassembly extends RequestHandler<CompleteDisassemblyRequestMessage, CompleteDisassemblyResponseMessage> {
    public CompleteDisassembly(Context context) {
        super(context);
    }

    @Override
    protected boolean isValid(CompleteDisassemblyRequestMessage request) {
        if (request.disassemblyId <= 0) {
            setValidationError("Disassembly ID must be a positive integer");
            return false;
        }

        if (request.worker == null || request.worker.equals("")) {
            setValidationError("Worker must be a non-empty string");
            return false;
        }
        if (request.worker.equals("A") && request.worker.equals("B")) {
            setValidationError("Worker must be either 'A' or 'B'");
            return false;
        }
        return true;
    }

    @Override
    protected CompleteDisassemblyResponseMessage onValid(CompleteDisassemblyRequestMessage request) {
        try {
            Disassembly disassembly = context.disassemblyGateway.completeDisassembly(request.disassemblyId, request.worker);

            if (disassembly == null) {
                return new CompleteDisassemblyResponseMessage().error("Failed to get disassembly. There may be no disassembly defined for that ID.", ErrorCode.UNKNOWN_ERROR);
            }

            CompleteDisassemblyResponseMessage response = new CompleteDisassemblyResponseMessage();
            response.disassembly = disassembly;
            return response;

        } catch (Exception e) {
            return new CompleteDisassemblyResponseMessage().error(e.getMessage(), ErrorCode.VALIDATION_ERROR);
        }

    }

    @Override
    protected CompleteDisassemblyResponseMessage onValidationError(CompleteDisassemblyRequestMessage request) {
        return null;
    }


}
