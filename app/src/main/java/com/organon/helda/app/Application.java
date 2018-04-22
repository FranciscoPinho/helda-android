package com.organon.helda.app;

import com.organon.helda.app.data.HttpAnomalyGateway;
import com.organon.helda.app.data.HttpDisassemblyGateway;
import com.organon.helda.app.data.HttpPlanGateway;
import com.organon.helda.core.Context;

public class Application {
    private static Context context;

    public static Context getContext() {
        if (context == null) {
            context = new Context();
            context.disassemblyGateway = new HttpDisassemblyGateway();
            context.anomalyGateway = new HttpAnomalyGateway();
            context.planGateway = new HttpPlanGateway();
        }
        return context;
    }
}
