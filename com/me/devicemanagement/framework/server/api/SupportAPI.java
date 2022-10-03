package com.me.devicemanagement.framework.server.api;

import java.util.Properties;
import java.util.HashMap;

public interface SupportAPI
{
    String getServerOS() throws Exception;
    
    void logComputerEvents(final String p0, final String p1, final String p2) throws Exception;
    
    HashMap<String, String> getSupportParam() throws Exception;
    
    Properties getSupportUploadState() throws Exception;
}
