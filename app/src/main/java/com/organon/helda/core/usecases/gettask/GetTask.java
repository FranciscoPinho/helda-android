package com.organon.helda.core.usecases.gettask;

import com.organon.helda.core.Context;
import com.organon.helda.core.entities.Plan;
import com.organon.helda.core.entities.Task;
import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.RequestHandler;

public class GetTask extends RequestHandler<GetTaskRequestMessage, GetTaskResponseMessage> {

    public GetTask(Context context) {
        super(context);
    }

    @Override
    protected boolean isValid(GetTaskRequestMessage request) {
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
    protected GetTaskResponseMessage onValid(GetTaskRequestMessage request) {
        Plan plan = context.planGateway.getPlan(request.model, request.locale);
        if (plan == null) {
            return new GetTaskResponseMessage().error("Plan not found.", ErrorCode.NOT_FOUND);
        }
        Task task = plan.getTask(request.task);
        if (task == null) {
            return new GetTaskResponseMessage().error("Task not found.", ErrorCode.NOT_FOUND);
        }
        return new GetTaskResponseMessage(task.getDescription(), task.getDuration());
    }

    @Override
    protected GetTaskResponseMessage onValidationError(GetTaskRequestMessage getTaskRequestMessage) {
        return new GetTaskResponseMessage().error(getValidationError(), ErrorCode.VALIDATION_ERROR);
    }
}
