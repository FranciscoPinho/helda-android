package com.organon.helda.app.services;

import com.organon.helda.app.data.HttpPlanGateway;
import com.organon.helda.core.Context;
import com.organon.helda.core.gateways.PlanGateway;
import com.organon.helda.core.usecases.getplan.GetPlan;
import com.organon.helda.core.usecases.getplan.GetPlanRequestMessage;
import com.organon.helda.core.usecases.getplan.GetPlanResponseMessage;


public class PlanService {
    public interface Listener {
        void onComplete(Object response);
    }

    public static void getPlan(final String planModel, final String locale, final PlanGateway gateway, final Listener listener) {
        ServiceHelper helper = new ServiceHelper(new ServiceHelper.Runnable() {
            @Override
            public Object run() {
                GetPlanRequestMessage request = new GetPlanRequestMessage();
                request.model = planModel;
                request.locale = locale;
                Context executionContext = new Context();
                executionContext.planGateway = gateway;
                GetPlan interactor = new GetPlan(executionContext);
                return interactor.handle(request);
            }
        }, new ServiceHelper.Listener() {
            @Override
            public void onComplete(Object o) {
                GetPlanResponseMessage response = (GetPlanResponseMessage)o;
                if (response.isError()) listener.onComplete(response.getMessage());
                else listener.onComplete(response.plan);
            }
        });
        helper.execute();
    }
}
