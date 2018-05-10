package com.organon.helda.app.data;

import android.util.Log;

import com.organon.helda.core.entities.Disassembly;
import com.organon.helda.core.entities.Plan;
import com.organon.helda.core.entities.Task;
import com.organon.helda.core.gateways.DisassemblyGateway;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.organon.helda.app.utils.GatewayUtils.jsonToDate;
import static com.organon.helda.app.utils.GatewayUtils.jsonToTaskList;

public class HttpDisassemblyGateway implements DisassemblyGateway {
    @Override
    public Disassembly createDisassembly(String vin) {
        Map<String, String> params = new HashMap<>();
        params.put("vin", vin);

        NetworkManager networkManager = NetworkManager.getInstance();

        String url = NetworkConstants.BASE_URL + NetworkConstants.CREATE_DISASSEMBLY;
        JSONObject res = networkManager.postSync(url, params);
        if (res == null) {
            return null;
        }
        try {
            if (res.has("error")) {
                boolean error = res.getBoolean("error");
                if (error) return null;
            }

            JSONObject disassembly = res.getJSONObject("disassembly");
            int id = disassembly.getInt("id");
            int plan = disassembly.getInt("plan");
            boolean workerA = disassembly.getBoolean("workerA");
            boolean workerB = disassembly.getBoolean("workerB");

            return new Disassembly(id)
                    .setPlan(plan)
                    .setWorkerA(workerA)
                    .setWorkerB(workerB);
        }
        catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Plan startDisassembly(int id, String worker) {
        Map<String, String> params = new HashMap<>();
        params.put("worker", worker);

        NetworkManager networkManager = NetworkManager.getInstance();

        String url = NetworkConstants.BASE_URL + String.format(NetworkConstants.START_DISASSEMBLY, id);
        JSONObject res = networkManager.postSync(url, params);
        if (res == null) return null;

        try {
            if (res.has("error")) {
                boolean error = res.getBoolean("error");
                if (error) return null;
            }

            JSONObject plan = res.getJSONObject("plan");
            int planId = plan.getInt("id");
            String locale = plan.getString("locale");
            String model = plan.getString("model");
            Date modified = jsonToDate(plan.getJSONObject("modified"));
            List<Task> tasksWorkerA = jsonToTaskList(plan.getJSONArray("tasksWorkerA"));
            List<Task> tasksWorkerB = jsonToTaskList(plan.getJSONArray("tasksWorkerB"));

            return new Plan(planId)
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

    @Override
    public Disassembly completeDisassembly(int id, String worker) {
        Map<String, String> params = new HashMap<>();
        params.put("worker", worker);

        NetworkManager networkManager = NetworkManager.getInstance();

        String url = NetworkConstants.BASE_URL + String.format(NetworkConstants.COMPLETE_DISASSEMBLY, id);
        JSONObject res = networkManager.postSync(url, params);
        if (res == null) return null;

        try {
            if (res.has("error")) {
                boolean error = res.getBoolean("error");
                if (error) return null;
            }

            JSONObject disassembly = res.getJSONObject("disassembly");
            int Disassemblyid = disassembly.getInt("id");
            int plan = disassembly.getInt("plan");
            boolean workerA = disassembly.getBoolean("workerA");
            boolean workerB = disassembly.getBoolean("workerB");

            return new Disassembly(Disassemblyid)
                    .setPlan(plan)
                    .setWorkerA(workerA)
                    .setWorkerB(workerB);
        }
        catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
