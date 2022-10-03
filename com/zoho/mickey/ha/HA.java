package com.zoho.mickey.ha;

import com.adventnet.mfw.modulestartup.ModuleStartStopProcessor;

public interface HA extends ModuleStartStopProcessor
{
    public static final String NODE_ALIVE = "alive";
    public static final String NODE_SERVING = "serving";
    public static final String NODE_DOWN = "down";
    public static final String REPL_NO_OP = "none";
    public static final String REPL_READY = "ready";
    public static final String REPL_IN_PROGRESS = "progress";
    
    boolean masterModePrecheck() throws Exception;
    
    boolean slaveModePreCheck() throws Exception;
    
    void runInMasterMode() throws Exception;
    
    void runInSlaveMode() throws Exception;
    
    String getType();
    
    public enum Mode
    {
        MASTER, 
        SLAVE, 
        NEUTRAL;
    }
}
