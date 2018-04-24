package com.organon.helda.app.data;

public class NetworkConstants {
    public static final String BASE_URL = "http://"+System.getProperty("serverIP")+":"+System.getProperty("port")+"/";

    public static final String REGISTER_ANOMALY = "anomaly/";
    public static final String UPLOAD_RECORDING = "anomaly/audio";
    public static final String GET_PLAN = "plans/%s";
    public static final String CREATE_DISASSEMBLY = "disassemblies/";
    public static final String START_DISASSEMBLY = "disassemblies/%s";
    public static final String REGISTER_TASKTIME = "task_time/";

    public static final int TIMEOUT = 10;

}
