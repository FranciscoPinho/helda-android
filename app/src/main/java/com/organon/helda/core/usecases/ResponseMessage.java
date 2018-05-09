package com.organon.helda.core.usecases;

public abstract class ResponseMessage<Response> {
    private boolean error = false;
    private ErrorCode errorCode = null;
    private String message;

    public Response error(String message, ErrorCode errorCode) {
        this.error = true;
        this.message = message;
        this.errorCode = errorCode;
        return (Response) this;
    }

    public boolean isError() {
        return error;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
