package com.organon.helda.core.usecases.startdisassembly;

import com.organon.helda.core.Context;
import com.organon.helda.core.entities.Disassembly;
import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.RequestHandler;

public class StartDisassembly extends RequestHandler<StartDisassemblyRequestMessage, StartDisassemblyResponseMessage> {
    public StartDisassembly(Context context) {
        super(context);
    }

    @Override
    protected boolean isValid(StartDisassemblyRequestMessage request) {
        if (request.vin == null) {
            setValidationError("Vin must be a non-empty string");
            return false;
        }
        return true;
    }

    @Override
    protected StartDisassemblyResponseMessage onValid(StartDisassemblyRequestMessage request) {
        Disassembly disassembly = context.disassemblyGateway.startDisassembly(request.vin);
        if (disassembly == null) {
            return new StartDisassemblyResponseMessage().error("Failed to start disassembly process", ErrorCode.UNKNOWN_ERROR);
        }
        StartDisassemblyResponseMessage response = new StartDisassemblyResponseMessage();
        response.planId = disassembly.getPlanId();
        response.disassemblyId = disassembly.getId();
        return response;
    }

    @Override
    protected StartDisassemblyResponseMessage onValidationError(StartDisassemblyRequestMessage request) {
        return new StartDisassemblyResponseMessage().error(getValidationError(), ErrorCode.VALIDATION_ERROR);
    }
}
