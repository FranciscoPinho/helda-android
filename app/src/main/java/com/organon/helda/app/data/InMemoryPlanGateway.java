package com.organon.helda.app.data;


import com.organon.helda.core.entities.Plan;
import com.organon.helda.core.entities.Task;
import com.organon.helda.core.gateways.PlanGateway;


public class InMemoryPlanGateway implements PlanGateway{

    Plan plan;

    public InMemoryPlanGateway(Plan plan){
       this.plan=plan;
    }

    @Override
    public Plan getPlan(String model,String locale){
        if(plan.getModel().equals(model) && plan.getLocale().equals(locale))
            return plan;
        else return null;
    }
}
