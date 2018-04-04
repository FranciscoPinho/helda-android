package com.organon.helda.app.data;


import com.organon.helda.core.entities.Plan;
import com.organon.helda.core.entities.Task;
import com.organon.helda.core.gateways.PlanGateway;


public class InMemoryPlanGateway implements PlanGateway{
    private static final String MODEL = "1PW2A4LKNQ78FKD0";
    private static final String LOCALE = "es";
    private static final Task[] TASKS = {
            new Task("Llevar clip a caja x y la junta a mueble kanban", 0),
            new Task("Quitar insono elemento portador izquierdo", 0),
            new Task("Llevar 2 clips a la caja 5 en mueble Kanban", 0),
    };
    Plan plan;

    public InMemoryPlanGateway(){
        plan=new Plan(MODEL,LOCALE);
        for(Task task:TASKS)plan.addTask(task);
    }

    @Override
    public Plan getPlan(String model,String locale){
        return plan;
    }

}
