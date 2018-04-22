package com.organon.helda.core.usecases.startdisassembly;

import com.organon.helda.core.Context;
import com.organon.helda.core.entities.Plan;
import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.RequestHandler;

public class StartDisassembly extends RequestHandler<StartDisassemblyRequestMessage, StartDisassemblyResponseMessage> {
    public StartDisassembly(Context context) {
        super(context);
    }

    @Override
    protected boolean isValid(StartDisassemblyRequestMessage request) {
        if (request.disassemblyId <= 0) {
            setValidationError("Disassembly ID must be a positive integer");
            return false;
        }
        if (!request.worker.equals("A") && !request.worker.equals("B")) {
            setValidationError("Worker must be either 'A' or 'B'");
            return false;
        }
        return true;
    }

    @Override
    protected StartDisassemblyResponseMessage onValid(StartDisassemblyRequestMessage request) {
        Plan plan = context.disassemblyGateway.startDisassembly(request.disassemblyId, request.worker);
        if (plan == null) {
            return new StartDisassemblyResponseMessage().error("Could not start disassembly", ErrorCode.UNKNOWN_ERROR);
        }
        StartDisassemblyResponseMessage response = new StartDisassemblyResponseMessage();
        response.plan = plan;
        return response;
    }

    @Override
    protected StartDisassemblyResponseMessage onValidationError(StartDisassemblyRequestMessage startDisassemblyRequestMessage) {
        return new StartDisassemblyResponseMessage().error(getValidationError(), ErrorCode.VALIDATION_ERROR);
    }
}
