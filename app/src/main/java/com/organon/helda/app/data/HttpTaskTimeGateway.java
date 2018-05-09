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
    public int insertUpdateTaskTime(int disassembly, int task, int duration, String role) {
        Map<String, String> params = new HashMap<>();
        params.put("disassembly", String.valueOf(disassembly));
        params.put("task", String.valueOf(task));
        params.put("duration", String.valueOf(duration));
        // TODO: Use the actual worker ID once workers are in the database
        params.put("worker", String.valueOf(1));
        params.put("role", String.valueOf(1));
        NetworkManager networkManager = NetworkManager.getInstance();
        JSONObject res = networkManager.getSync(NetworkConstants.BASE_URL + NetworkConstants.REGISTER_TASKTIME, params);

        if (res == null) {
            System.out.println("Response from server is null");
            return -1;
        }

        try {
            if (res.has("error")) {
                boolean error = res.getBoolean("error");
                if (error)
                    return -1;
            }
            return 1;
        }
        catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
