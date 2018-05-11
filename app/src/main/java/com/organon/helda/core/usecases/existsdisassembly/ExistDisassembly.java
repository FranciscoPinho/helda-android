package com.organon.helda.core.usecases.existsdisassembly;

import com.organon.helda.core.Context;
import com.organon.helda.core.entities.Disassembly;
import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.RequestHandler;

public class ExistDisassembly extends RequestHandler< ExistDisassemblyRequestMessage, ExistDisassemblyResponseMessage> {
    public ExistDisassembly(Context context) {
        super(context);
    }

    @Override
    protected boolean isValid(ExistDisassemblyRequestMessage request) {
        if(request.vin == null || request.vin.equals("")) {
            setValidationError("Vin must be a non-empty string!");
            return false;
        }

        return true;
    }

    @Override
    protected ExistDisassemblyResponseMessage onValid(ExistDisassemblyRequestMessage request) {
        try {
            Disassembly disassembly = context.disassemblyGateway.existDisassembly(request.vin);
            ExistDisassemblyResponseMessage response = new ExistDisassemblyResponseMessage();

            if (disassembly == null) {
                return null;
            }

            response.disassembly = disassembly;
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return new ExistDisassemblyResponseMessage().error(e.getMessage(), ErrorCode.UNKNOWN_ERROR);
        }
    }

    @Override
    protected ExistDisassemblyResponseMessage onValidationError(ExistDisassemblyRequestMessage request) {
        return new ExistDisassemblyResponseMessage().error(getValidationError(), ErrorCode.VALIDATION_ERROR);
    }
}
