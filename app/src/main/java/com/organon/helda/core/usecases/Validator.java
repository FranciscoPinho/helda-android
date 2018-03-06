package com.organon.helda.core.usecases;

public interface Validator<Request> {
    boolean isValid(Request request);
    String getMessage();
}
