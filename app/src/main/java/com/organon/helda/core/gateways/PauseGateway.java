package com.organon.helda.core.gateways;

import com.organon.helda.app.utils.Utils;

import java.io.File;

public interface PauseGateway {

    int insertPause(int disassembly, int duration, String role);
}