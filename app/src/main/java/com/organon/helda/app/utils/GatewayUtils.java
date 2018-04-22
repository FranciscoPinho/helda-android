package com.organon.helda.app.utils;

import com.organon.helda.core.entities.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GatewayUtils {
    public static Date jsonToDate(JSONObject datetime) throws JSONException {
        JSONObject date = datetime.getJSONObject("date");
        JSONObject time = datetime.getJSONObject("time");
        return new Date(date.getInt("year"),
                date.getInt("month"),
                date.getInt("day"),
                time.getInt("hour"),
                time.getInt("minute"),
                time.getInt("second"));
    }

    public static List<Task> jsonToTaskList(JSONArray tasks) throws JSONException {
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
