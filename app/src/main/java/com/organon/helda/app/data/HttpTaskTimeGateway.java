package com.organon.helda.app.data;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.organon.helda.app.utils.Utils;
import com.organon.helda.core.gateways.AnomalyGateway;
import com.organon.helda.core.gateways.TaskTimeTableGateway;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpTaskTimeGateway implements TaskTimeTableGateway {
    private static final String BASE = "http://"+System.getProperty("serverIP")+":4567/";
    private static String REGISTER_TASKTIME = "task_time/";

    public int insertTaskTime(int disassembly, int task_id, int task_time) {
        Map<String, String> params = new HashMap<>();
        params.put("disassembly", String.valueOf(disassembly));
        params.put("task_id", String.valueOf(task_id));
        params.put("task_time", String.valueOf(task_time));
        NetworkManager networkManager = NetworkManager.getInstance();
        JSONObject res = networkManager.getSync(BASE + REGISTER_TASKTIME, params, Request.Method.POST);

        if (res == null) {
            System.out.println("Response HTTPAnamoly from server is null");
            return -1;
        }

        try {
            if (res.has("error")) {
                boolean error = res.getBoolean("error");
                if (error)
                    return -1;
            }
            return res.getInt("id");
        }
        catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
