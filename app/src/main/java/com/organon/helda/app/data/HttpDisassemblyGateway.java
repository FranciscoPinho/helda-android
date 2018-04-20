package com.organon.helda.app.data;

import com.organon.helda.core.entities.Disassembly;
import com.organon.helda.core.gateways.DisassemblyGateway;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpDisassemblyGateway implements DisassemblyGateway {
    @Override
    public Disassembly startDisassembly(String vin) {
        Map<String, String> params = new HashMap<>();
        params.put("vin", vin);

        NetworkManager networkManager = NetworkManager.getInstance();

        String url = NetworkConstants.BASE_URL + NetworkConstants.START_DISASSEMBLY;
        JSONObject res = networkManager.postSync(url, params);
        if (res == null) {
            return null;
        }
        try {
            if (res.has("error")) {
                boolean error = res.getBoolean("error");
                if (error) return null;
            }
            int id = res.getInt("disassemblyId");
            int planId = res.getInt("planId");
            return new Disassembly(id).setPlanId(planId);
        }
        catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
