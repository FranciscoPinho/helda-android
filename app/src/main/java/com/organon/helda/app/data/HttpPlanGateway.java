package com.organon.helda.app.data;
import java.lang.reflect.Type;
import android.os.AsyncTask;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.organon.helda.core.entities.Plan;
import com.organon.helda.core.entities.Task;
import com.organon.helda.core.gateways.PlanGateway;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpPlanGateway implements PlanGateway {
   // private static final String BASE = "https://helda-server.herokuapp.com/";
    private static final String BASE = "http://172.30.17.170:4567/";
    private static String GET_PLAN = "plans/%s";

    @Override
    public Plan getPlan(String model, String locale) {
        StringBuilder url = new StringBuilder(BASE);
        url.append(String.format(GET_PLAN,"TESTE"));
        Map<String, String> params = new HashMap<>();
        params.put("locale", locale);

        NetworkManager networkManager = NetworkManager.getInstance();
        JSONObject res = null;
        res = networkManager.getSync(url.toString(), params, Request.Method.GET);
        if(res==null)
            return null;

        try {

            if(res.has("error")) {
                boolean error = res.getBoolean("error");
                if (error) return null;
            }
            return new Plan(res.getString("model"),locale, parseListTasks(res.getJSONArray("tasksA")));
        }
        catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<Task> parseListTasks(JSONArray tasks){
        Gson gson = new Gson();
        Type type = new TypeToken<List<Task>>() {}.getType();
        return gson.fromJson(tasks.toString(), type);
    }
}
