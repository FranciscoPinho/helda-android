package com.organon.helda.app.services;


import com.organon.helda.core.Context;
import com.organon.helda.core.gateways.PlanGateway;
import com.organon.helda.core.usecases.getplan.GetPlan;
import com.organon.helda.core.usecases.getplan.GetPlanRequestMessage;
import com.organon.helda.core.usecases.getplan.GetPlanResponseMessage;


public class PlanService {
    private Context context;

    public PlanService(Context context) {
        this.context = context;
    }

    public void getPlan(final int planId, final ServiceHelper.Listener<GetPlanResponseMessage> listener) {
        ServiceHelper helper = new ServiceHelper(new ServiceHelper.Runnable() {
            @Override
            public Object run() {
                GetPlanRequestMessage request = new GetPlanRequestMessage();
                request.planId = planId;
                GetPlan interactor = new GetPlan(context);
                return interactor.handle(request);
            }
        }, new ServiceHelper.Listener() {
            @Override
            public void onComplete(Object o) {
                GetPlanResponseMessage response = (GetPlanResponseMessage)o;
                listener.onComplete(response);
            }
        });
        helper.execute();
    }


}
