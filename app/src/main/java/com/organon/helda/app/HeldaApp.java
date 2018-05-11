package com.organon.helda.app;

import android.app.Application;

import com.organon.helda.app.data.HttpAnomalyGateway;
import com.organon.helda.app.data.HttpDisassemblyGateway;
import com.organon.helda.core.Context;


public class HeldaApp extends Application {
    public String anomalyDecision = "";
    public int task = 0;

    private static Context context;

    public static Context getContext() {
        if (context == null) {
            context = new Context();
            context.disassemblyGateway = new HttpDisassemblyGateway();
            context.anomalyGateway = new HttpAnomalyGateway();
        }
        return context;
    }
}
