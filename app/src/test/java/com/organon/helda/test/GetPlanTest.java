package com.organon.helda.test;

import com.organon.helda.app.data.InMemoryPlanGateway;
import com.organon.helda.core.Context;
import com.organon.helda.core.entities.Plan;
import com.organon.helda.core.entities.Task;
import com.organon.helda.core.usecases.ErrorCode;
import com.organon.helda.core.usecases.getplan.GetPlan;
import com.organon.helda.core.usecases.getplan.GetPlanRequestMessage;
import com.organon.helda.core.usecases.getplan.GetPlanResponseMessage;

import org.junit.BeforeClass;

/**
 * Created by marga on 3/2/2018.
 */
public class GetPlanTest {
    private static final Context context = new Context();
    private static final String MODEL = "1PW2A4LKNQ78FKD0";
    private static final String LOCALE = "es";
    private static final Task[] TASKS = {
            new Task("Llevar clip a caja x y la junta a mueble kanban", 0),
            new Task("Quitar insono elemento portador izquierdo", 0),
            new Task("Llevar 2 clips a la caja 5 en mueble Kanban", 0),
    };

    @BeforeClass
    public static void SetUpBeforeClass() {
        Plan plan = new Plan(MODEL, LOCALE);
        for (Task task : TASKS) plan.addTask(task);
        context.planGateway = new InMemoryPlanGateway(plan);
    }

    @org.junit.Test
    public void testGetPlan() {
        int task = 0;

        GetPlanRequestMessage request = new GetPlanRequestMessage();
        request.model = MODEL;
        request.locale = LOCALE;

        GetPlan interactor = new GetPlan(context);
        GetPlanResponseMessage response = interactor.handle(request);

        assert(!response.isError());
        assert(response.plan.getLocale().equals(LOCALE));
        assert(response.plan.getModel().equals(MODEL));
    }

    @org.junit.Test
    public void testGetPlanPlanNotFound() {
        int task = 0;

        GetPlanRequestMessage request = new GetPlanRequestMessage();
        request.model = "X";
        request.locale = LOCALE;

        GetPlan interactor = new GetPlan(context);
        GetPlanResponseMessage response = interactor.handle(request);

        assert(response.isError());
        assert(response.getErrorCode() == ErrorCode.NOT_FOUND);
    }


    @org.junit.Test
    public void testGetPlanInvalidLocale() {
        int task = 0;

        GetPlanRequestMessage request = new GetPlanRequestMessage();
        request.model = MODEL;
        request.locale = "";

        GetPlan interactor = new GetPlan(context);
        GetPlanResponseMessage response = interactor.handle(request);

        assert(response.isError());
        assert(response.getErrorCode() == ErrorCode.VALIDATION_ERROR);
    }

    @org.junit.Test
    public void testGetPlanInvalidModel() {
        int task = 0;

        GetPlanRequestMessage request = new GetPlanRequestMessage();
        request.model = "";
        request.locale = LOCALE;

        GetPlan interactor = new GetPlan(context);
        GetPlanResponseMessage response = interactor.handle(request);

        assert(response.isError());
        assert(response.getErrorCode() == ErrorCode.VALIDATION_ERROR);
    }
}