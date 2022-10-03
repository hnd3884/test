package com.me.devicemanagement.framework.server.api;

public interface DemoUtilAPI
{
    boolean isDemoMode();
    
    void loadDemoModeValFromConf();
    
    void updateDemoMode(final String p0);
}
