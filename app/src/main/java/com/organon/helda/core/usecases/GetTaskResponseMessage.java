package com.organon.helda.core.usecases;

public class GetTaskResponseMessage extends ResponseMessage<GetTaskResponseMessage> {
    public String taskDescription;

    public int taskDuration;

    public GetTaskResponseMessage() {}

    public GetTaskResponseMessage(String taskDescription, int taskDuration) {
        this.taskDescription = taskDescription;
        this.taskDuration = taskDuration;
    }
}
