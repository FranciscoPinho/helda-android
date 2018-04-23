package com.organon.helda.core.gateways;

import com.organon.helda.app.utils.Utils;

import java.io.File;

public interface TaskTimeTableGateway {
    int insertTaskTime(int disassembly, int task_id, int task_time);
}
