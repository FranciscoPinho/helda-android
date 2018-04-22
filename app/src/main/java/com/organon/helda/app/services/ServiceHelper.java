package com.organon.helda.app.services;

import android.os.AsyncTask;

public class ServiceHelper extends AsyncTask<Void, Void, Object>  {
    public interface Runnable {
        Object run();
    }

    public interface Listener<T> {
        void onComplete(T o);
    }

    private Runnable runnable;
    private Listener listener;

    public ServiceHelper(Runnable runnable, Listener listener) {
        this.runnable = runnable;
        this.listener = listener;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        return runnable.run();
    }

    @Override
    protected void onPostExecute(Object o) {
        listener.onComplete(o);
    }
}
