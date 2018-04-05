package com.organon.helda.core.usecases;

import com.organon.helda.core.Context;

public abstract class RequestHandler<Request, Response> {
    protected Context context;
    private String errorMessage;

    public RequestHandler(Context context) {
        this.context = context;
    }

    protected abstract boolean isValid(Request request);

    protected abstract Response onValid(Request request);

    protected abstract Response onValidationError(Request request);

    public Response handle(Request request) {
        if (!isValid(request)) return onValidationError(request);
        return onValid(request);
    }

    protected String getValidationError() {
        return errorMessage;
    }

    protected void setValidationError(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
