package com.organon.helda.core.usecases.gettask;

import com.organon.helda.core.usecases.ResponseMessage;

public class GetTaskResponseMessage extends ResponseMessage<GetTaskResponseMessage> {
    public String description;
    public int duration;

    public GetTaskResponseMessage() {}

    public GetTaskResponseMessage(String description, int duration) {
        this.description = description;
        this.duration = duration;
    }
}
