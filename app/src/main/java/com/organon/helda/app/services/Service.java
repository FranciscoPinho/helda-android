package com.organon.helda.app.services;

import android.os.AsyncTask;

import com.organon.helda.core.Context;

public abstract class Service extends AsyncTask<Void, Void, Object> {
    private final Context context;

    public Service(Context context) {
        this.context = context;
    }

    abstract protected Object run();

    abstract protected void onComplete(Object o);

    @Override
    protected Object doInBackground(Void... voids) {
        return run();
    }

    @Override
    protected void onPostExecute(Object o) {
        onComplete(o);
    }
}
