package com.organon.helda.core.usecases;

public abstract class ResponseMessage<Response> {
    private boolean error = false;
    private String message;

    public Response error(String message) {
        this.error = true;
        this.message = message;
        return (Response) this;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
