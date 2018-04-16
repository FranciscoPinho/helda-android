package com.organon.helda.app.data;

import com.android.volley.Request;
import com.organon.helda.app.utils.Utils;
import com.organon.helda.core.gateways.AnomalyGateway;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class HTTPAnomalyGateway implements AnomalyGateway {
    private static final String BASE = "http://"+System.getProperty("serverIP")+":4567/";
    private static String REGISTER_ANOMALY = "/anomaly/";

    public int insertAnomaly(int disassembly, int plan, String anomalyDate, String description, int task, Utils.State state){
        Map<String, String> params = new HashMap<>();
        params.put("disassembly", String.valueOf(disassembly));
        params.put("description", description);
        params.put("anomalydate", anomalyDate);
        params.put("task", String.valueOf(task));
        params.put("plan", String.valueOf(plan));
        params.put("state", String.valueOf(state));

        NetworkManager networkManager = NetworkManager.getInstance();
        JSONObject res = networkManager.getSync(BASE + REGISTER_ANOMALY, params, Request.Method.POST);

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
