package com.me.mdm.apps.handler;

import java.util.List;
import org.json.JSONObject;

public interface AppAutoDeploymentHandler
{
    JSONObject getAgentAppData(final Long p0);
    
    int getPlatformType();
    
    int getSupportedDevices();
    
    String getBundleIdentifier();
    
    String replaceDynamicVariables(final String p0, final Long p1, final String p2);
    
    List filterDevices(final List p0);
}
