package com.organon.helda.app.data;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
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

    public JSONObject getSync(String url, Map<String, String> params,int type) {
        url = buildUrl(url, params);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(type,url, null, future, future);
        requestQueue.add(request);
        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Converts the checked exception to an unchecked exception
            // @Todo: Change the runtime exception to a custom exception probably ?
            throw new RuntimeException(e.getMessage());
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
