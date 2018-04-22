package com.organon.helda.core.usecases.getplan;


import com.organon.helda.core.Context;
import com.organon.helda.core.entities.Plan;
import com.organon.helda.core.entities.Task;
import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.RequestHandler;

public class GetPlan extends RequestHandler<GetPlanRequestMessage, GetPlanResponseMessage> {

    public GetPlan(Context context) {
        super(context);
    }

    @Override
    protected boolean isValid(GetPlanRequestMessage request) {
        if (request.planId <= 0) {
            setValidationError("Plan id must be a positive integer");
            return false;
        }
        return true;
    }

    @Override
    protected GetPlanResponseMessage onValid(GetPlanRequestMessage request) {
        Plan plan = context.planGateway.getPlan(request.planId);
        if (plan == null) {
            return new GetPlanResponseMessage().error("Plan not found.", ErrorCode.NOT_FOUND);
        }

        return new GetPlanResponseMessage(plan);
    }

    @Override
    protected GetPlanResponseMessage onValidationError(GetPlanRequestMessage GetPlanRequestMessage) {
        return new GetPlanResponseMessage().error(getValidationError(), ErrorCode.VALIDATION_ERROR);
    }
}
