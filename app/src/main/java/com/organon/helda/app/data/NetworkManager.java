package com.organon.helda.app.data;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NetworkManager
{
    private static NetworkManager instance = null;

    //for Volley API
    private RequestQueue requestQueue;

    private NetworkManager(Context context)
    {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized NetworkManager getInstance(Context context)
    {
        if (null == instance)
            instance = new NetworkManager(context);
        return instance;
    }

    public static synchronized NetworkManager getInstance()
    {
        if (null == instance)
        {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() + " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public RequestQueue getRequestQueue(){
        return requestQueue;
    }


    public JSONObject getSync(String url, Map<String, String> params) {
        url = buildUrl(url, params);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, future, future);
        requestQueue.add(request);
        try {
            return future.get(NetworkConstants.TIMEOUT, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject postSync(String url, Map<String, String> params) {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), future, future){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        requestQueue.add(request);
        try {
            return future.get(NetworkConstants.TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getSyncRecordingUpload(final String url, final File payload, final Map<String, String> params){
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, future,future) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                try {
                    params.put("recording", new DataPart(payload.getName(), com.google.common.io.Files.toByteArray(payload), "audio/mpeg"));
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                return params;
            }
        };
        requestQueue.add(multipartRequest);
        try {
            return future.get(20, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildUrl(String base, Map<String, String> params) {
        StringBuilder url = new StringBuilder(base);
        int index = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (index == 0) url.append("?");
            else url.append("&");
            url.append(entry.getKey());
            url.append("=");
            url.append(entry.getValue());
            index += 1;
        }
        return url.toString();
    }
}
