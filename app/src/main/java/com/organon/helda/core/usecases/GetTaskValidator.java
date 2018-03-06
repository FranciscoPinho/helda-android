package com.organon.helda.core.usecases;

public class GetTaskValidator implements Validator<GetTaskRequestMessage> {
    private String message = "";

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isValid(GetTaskRequestMessage request) {
        if (request.planModel == null || request.planModel.equals("")) {
            message = "Model must be a non-empty string.";
            return false;
        }
        if (request.locale == null || request.planModel.equals("")) {
            message = "Locale must be a non-empty string.";
            return false;
        }
        return true;
    }
}
