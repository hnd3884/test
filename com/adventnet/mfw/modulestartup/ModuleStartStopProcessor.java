package com.adventnet.mfw.modulestartup;

public interface ModuleStartStopProcessor
{
    void initialize() throws Exception;
    
    void preStartProcess() throws Exception;
    
    void postStartProcess() throws Exception;
    
    void stopProcess() throws Exception;
}
