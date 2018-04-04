package com.organon.helda.core.usecases.getplan;


import com.organon.helda.core.entities.Plan;
import com.organon.helda.core.usecases.ResponseMessage;


public class GetPlanResponseMessage extends ResponseMessage<GetPlanResponseMessage> {
    public Plan plan;

    public GetPlanResponseMessage() {}

    public GetPlanResponseMessage(Plan plan) {
        this.plan=plan;

    }
}
