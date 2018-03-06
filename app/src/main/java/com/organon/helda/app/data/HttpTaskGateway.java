package com.organon.helda.app.data;

import android.os.AsyncTask;

import com.organon.helda.core.entities.Task;
import com.organon.helda.core.gateways.TaskGateway;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpTaskGateway implements TaskGateway {
    private static final String BASE = "https://helda-server.herokuapp.com/";

    private static String GET_TASK = "plans/%s/tasks/%d";

    @Override
    public Task getTask(String model, int taskNumber, String locale) {
        StringBuilder url = new StringBuilder(BASE);
        url.append(String.format(GET_TASK, model, taskNumber));

        Map<String, String> params = new HashMap<>();
        params.put("locale", locale);

        NetworkManager networkManager = NetworkManager.getInstance();
        JSONObject res = networkManager.getSync(url.toString(), params);

        try {
            boolean error = res.getBoolean("error");
            if (error) return null;
            return new Task(res.getString("taskDescription"), res.getInt("taskDuration"));
        } catch (JSONException e) {
            // Convert the checked exception to an unchecked one
            // @Todo: Probably throw some custom exception
            throw new RuntimeException(e.getMessage());
        }
    }
}
