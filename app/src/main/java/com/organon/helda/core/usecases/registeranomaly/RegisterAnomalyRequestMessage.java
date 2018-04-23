package com.organon.helda.core.usecases.registeranomaly;

import com.organon.helda.app.utils.Utils;

/**
 * Created by marga on 4/14/2018.
 */

public class RegisterAnomalyRequestMessage {
    public int disassembly;
    public String anomalyDate;
    public String description;
    public int task;
    public Utils.State state;
}

