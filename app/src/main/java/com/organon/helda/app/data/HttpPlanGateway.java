package com.organon.helda.app.data;
import java.lang.reflect.Type;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.organon.helda.core.entities.Plan;
import com.organon.helda.core.entities.Task;
import com.organon.helda.core.gateways.PlanGateway;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpPlanGateway implements PlanGateway {
    @Override
    public Plan getPlan(int planId) {
        StringBuilder url = new StringBuilder(NetworkConstants.BASE_URL);
        url.append(String.format(NetworkConstants.GET_PLAN, planId));

        Map<String, String> params = new HashMap<>();
        NetworkManager networkManager = NetworkManager.getInstance();
        JSONObject res = networkManager.getSync(url.toString(), params);
        if(res == null) return null;

        try {
            if(res.has("error")) {
                boolean error = res.getBoolean("error");
                if (error) return null;
            }

            int id = res.getInt("id");
            String locale = res.getString("locale");
            String model = res.getString("model");
            Date modified = jsonToDate(res.getJSONObject("modified"));
            List<Task> tasksWorkerA = jsonToTaskList(res.getJSONArray("tasksWorkerA"));
            List<Task> tasksWorkerB = jsonToTaskList(res.getJSONArray("tasksWorkerB"));

            return new Plan(id)
                    .setLocale(locale)
                    .setModel(model)
                    .setModified(modified)
                    .setTasksWorkerA(tasksWorkerA)
                    .setTasksWorkerB(tasksWorkerB);
        }
        catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Date jsonToDate(JSONObject datetime) throws JSONException {
        JSONObject date = datetime.getJSONObject("date");
        JSONObject time = datetime.getJSONObject("time");
        return new Date(date.getInt("year"),
                date.getInt("month"),
                date.getInt("day"),
                time.getInt("hour"),
                time.getInt("minute"),
                time.getInt("second"));
    }

    private List<Task> jsonToTaskList(JSONArray tasks) throws JSONException {
        List<Task> result = new ArrayList<>();
        for (int i = 0; i < tasks.length(); ++i) {
            JSONObject task = tasks.getJSONObject(i);

            String descripton = task.getString("description");
            int duration = task.getInt("duration");

            result.add(new Task()
                    .setDescription(descripton)
                    .setDuration(duration));
        }
        return result;
    }
}
