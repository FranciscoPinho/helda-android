package com.organon.helda.app.data;

import com.organon.helda.core.gateways.TaskTimeTableGateway;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpTaskTimeGateway implements TaskTimeTableGateway {
    public int insertUpdateTaskTime(int disassembly, int task, int duration, String role, String workerID) {
        Map<String, String> params = new HashMap<>();
        params.put("disassembly", String.valueOf(disassembly));
        params.put("task", String.valueOf(task));
        params.put("duration", String.valueOf(duration));
        params.put("worker", workerID);
        params.put("role", String.valueOf(role));
        NetworkManager networkManager = NetworkManager.getInstance();
        JSONObject res = networkManager.postSync(NetworkConstants.BASE_URL + NetworkConstants.REGISTER_TASKTIME, params);

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
