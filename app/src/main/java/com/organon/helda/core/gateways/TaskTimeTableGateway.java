package com.organon.helda.core.gateways;

import com.organon.helda.app.utils.Utils;

import java.io.File;

public interface TaskTimeTableGateway {
    int insertUpdateTaskTime(int disassembly, int taskId, int taskTime, String role);
}


