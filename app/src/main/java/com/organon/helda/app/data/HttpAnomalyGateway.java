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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpAnomalyGateway implements AnomalyGateway {


    public int insertAnomaly(int disassembly, int plan, String anomalyDate, String description, int task, Utils.State state){
        Map<String, String> params = new HashMap<>();
        params.put("disassembly", String.valueOf(disassembly));
        params.put("description", description);
        params.put("anomalydate", anomalyDate);
        params.put("task", String.valueOf(task));
        params.put("plan", String.valueOf(plan));
        params.put("state", String.valueOf(state));

        NetworkManager networkManager = NetworkManager.getInstance();
        JSONObject res = networkManager.postSync(NetworkConstants.BASE_URL + NetworkConstants.REGISTER_ANOMALY, params);

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

    public Boolean sendRecording(final File payload, final int disassembly){
        Map<String, String> params = new HashMap<>();
        params.put("disassembly", String.valueOf(disassembly));
        params.put("filename",payload.getName());
        NetworkManager networkManager = NetworkManager.getInstance();
        JSONObject res = networkManager.getSyncRecordingUpload(NetworkConstants.BASE_URL + NetworkConstants.UPLOAD_RECORDING, payload, params);
        if (res == null) {
            return null;
        }
        try {
            if (res.has("error")) {
                boolean error = res.getBoolean("error");
                if (error)
                    return null;
            }
            return true;
        }
        catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
