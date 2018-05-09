package com.organon.helda.core.usecases.createdisassembly;

import com.organon.helda.core.Context;
import com.organon.helda.core.entities.Disassembly;
import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.RequestHandler;

public class CreateDisassembly extends RequestHandler<CreateDisassemblyRequestMessage, CreateDisassemblyResponseMessage> {
    public CreateDisassembly(Context context) {
        super(context);
    }

    @Override
    protected boolean isValid(CreateDisassemblyRequestMessage request) {
        if (request.vin == null) {
            setValidationError("Vin must be a non-empty string");
            return false;
        }
        return true;
    }

    @Override
    protected CreateDisassemblyResponseMessage onValid(CreateDisassemblyRequestMessage request) {
        Disassembly disassembly = context.disassemblyGateway.createDisassembly(request.vin);
        if (disassembly == null) {
            return new CreateDisassemblyResponseMessage().error("Failed to start disassembly process", ErrorCode.UNKNOWN_ERROR);
        }
        CreateDisassemblyResponseMessage response = new CreateDisassemblyResponseMessage();
        response.disassembly = disassembly;
        return response;
    }

    @Override
    protected CreateDisassemblyResponseMessage onValidationError(CreateDisassemblyRequestMessage request) {
        return new CreateDisassemblyResponseMessage().error(getValidationError(), ErrorCode.VALIDATION_ERROR);
    }
}
