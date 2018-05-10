package com.organon.helda.app.services;

import com.organon.helda.core.Context;
import com.organon.helda.core.usecases.completedisassembly.CompleteDisassembly;
import com.organon.helda.core.usecases.completedisassembly.CompleteDisassemblyRequestMessage;
import com.organon.helda.core.usecases.completedisassembly.CompleteDisassemblyResponseMessage;
import com.organon.helda.core.usecases.createdisassembly.CreateDisassembly;
import com.organon.helda.core.usecases.createdisassembly.CreateDisassemblyRequestMessage;
import com.organon.helda.core.usecases.createdisassembly.CreateDisassemblyResponseMessage;
import com.organon.helda.core.usecases.startdisassembly.StartDisassembly;
import com.organon.helda.core.usecases.startdisassembly.StartDisassemblyRequestMessage;
import com.organon.helda.core.usecases.startdisassembly.StartDisassemblyResponseMessage;

public class DisassemblyService {
    private final Context context;

    public DisassemblyService(Context context) {
        this.context = context;
    }

    public void createDisassembly(final String vin,
                                  final ServiceHelper.Listener<CreateDisassemblyResponseMessage> listener) {
        new ServiceHelper(new ServiceHelper.Runnable() {
            @Override
            public Object run() {
                CreateDisassemblyRequestMessage request = new CreateDisassemblyRequestMessage();
                request.vin = vin;
                CreateDisassembly interactor = new CreateDisassembly(context);
                return interactor.handle(request);
            }
        }, new ServiceHelper.Listener() {
            @Override
            public void onComplete(Object o) {
                CreateDisassemblyResponseMessage response = (CreateDisassemblyResponseMessage)o;
                listener.onComplete(response);
            }
        }).execute();
    }

    public void startDisassembly(final int id,
                                 final String worker,
                                 final ServiceHelper.Listener<StartDisassemblyResponseMessage> listener) {
        new ServiceHelper(new ServiceHelper.Runnable() {
            @Override
            public Object run() {
                StartDisassemblyRequestMessage request = new StartDisassemblyRequestMessage();
                request.disassemblyId = id;
                request.worker = worker;
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

    public void completeDisassembly(final int id, final String worker, final ServiceHelper.Listener<CompleteDisassemblyResponseMessage> listener) {
        new ServiceHelper(new ServiceHelper.Runnable() {
            @Override
            public Object run() {
                CompleteDisassemblyRequestMessage request = new CompleteDisassemblyRequestMessage();
                request.disassemblyId = id;
                request.worker = worker;
                CompleteDisassembly interactor = new CompleteDisassembly(context);
                return interactor.handle(request);
            }
        }, new ServiceHelper.Listener() {
            @Override
            public void onComplete(Object o) {
                CompleteDisassemblyResponseMessage response = (CompleteDisassemblyResponseMessage)o;
                listener.onComplete(response);
            }
        }).execute();
    }
}
