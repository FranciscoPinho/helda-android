package com.organon.helda.app.data;

import com.organon.helda.core.entities.Worker;
import com.organon.helda.core.gateways.WorkerGateway;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpWorkerGateway implements WorkerGateway {
    @Override
    public int workerLogin(String username, byte[] attempt) {
        StringBuilder url = new StringBuilder(NetworkConstants.BASE_URL);
        url.append(NetworkConstants.WORKER_LOGIN);

        Map<String, String> params = new HashMap<>();
        params.put("username",username);
        try {
            params.put("password",new String(attempt, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        NetworkManager networkManager = NetworkManager.getInstance();
        JSONObject res = networkManager.postSync(url.toString(), params);
        if(res == null) return -1;

        try {
            if (res.has("error")) {
                boolean error = res.getBoolean("error");
                if (error) return -1;
            }

            int id = res.getInt("workerID");
            return id;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public List<Worker> listWorkers() {
        return null;
    }
}
