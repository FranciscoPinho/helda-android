package com.organon.helda.app.services;

import com.organon.helda.core.Context;
import com.organon.helda.core.usecases.startdisassembly.StartDisassembly;
import com.organon.helda.core.usecases.startdisassembly.StartDisassemblyRequestMessage;
import com.organon.helda.core.usecases.startdisassembly.StartDisassemblyResponseMessage;

public class DisassemblyService {
    private final Context context;

    public DisassemblyService(Context context) {
        this.context = context;
    }

    public void startDisassembly(final String vin,
                                 final ServiceHelper.Listener<StartDisassemblyResponseMessage> listener) {
        new ServiceHelper(new ServiceHelper.Runnable() {
            @Override
            public Object run() {
                StartDisassemblyRequestMessage request = new StartDisassemblyRequestMessage();
                request.vin = vin;
                StartDisassembly interactor = new StartDisassembly(context);
                return interactor.handle(request);
            }
        }, new ServiceHelper.Listener() {
            @Override
            public void onComplete(Object o) {
                StartDisassemblyResponseMessage response = (StartDisassemblyResponseMessage)o;
                listener.onComplete(response);
            }
        }).execute();
    }
}
