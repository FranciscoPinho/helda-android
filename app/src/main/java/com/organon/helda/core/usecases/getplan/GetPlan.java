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
        if (request.model == null || request.model.equals("")) {
            setValidationError("Model must be a non-empty string.");
            return false;
        }
        if (request.locale == null || request.locale.equals("")) {
            setValidationError("Locale must be a non-empty string.");
            return false;
        }
        return true;
    }

    @Override
    protected GetPlanResponseMessage onValid(GetPlanRequestMessage request) {
        Plan plan = context.planGateway.getPlan(request.model, request.locale);
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
