package com.organon.helda.core.usecases;

import com.organon.helda.core.entities.Task;
import com.organon.helda.core.gateways.TaskGateway;

public class GetTask implements RequestHandler<GetTaskRequestMessage, GetTaskResponseMessage> {
    private TaskGateway taskGateway;
    private Validator<GetTaskRequestMessage> validator;

    public GetTask(Validator<GetTaskRequestMessage> validator, TaskGateway taskGateway) {
        this.validator = validator;
        this.taskGateway = taskGateway;
    }

    @Override
    public GetTaskResponseMessage handle(GetTaskRequestMessage request) {
        if (!validator.isValid(request)) {
            return new GetTaskResponseMessage().error(validator.getMessage());
        }
        Task task = taskGateway.getTask(request.planModel, request.taskNumber, request.locale);
        if (task == null) {
            return new GetTaskResponseMessage().error("Task not found.");
        }
        return new GetTaskResponseMessage(task.getDescription(), task.getDuration());
    }
}
