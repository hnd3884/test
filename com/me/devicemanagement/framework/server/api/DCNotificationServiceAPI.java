package com.me.devicemanagement.framework.server.api;

import java.util.Properties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public interface DCNotificationServiceAPI
{
    public static final String DS = "DS";
    public static final String AGENT = "AGENT";
    
    NSResponseAPI sendRequest(final Long p0, final String p1, final String p2) throws Exception;
    
    boolean isNSEnabled();
    
    int getNSPort();
    
    String getNSMessageAlerts();
    
    boolean isNSComponentEnabled();
    
    HashMap getLiveResourceMap();
    
    ArrayList<Long> getLiveResourceList();
    
    HashMap getLiveListFromNS() throws IOException;
    
    boolean getLiveStatusFromNS(final long p0) throws IOException;
    
    void initiatePatchScan(final long p0, final Properties p1, final HashMap p2) throws Exception;
    
    void removeResFromLiveList(final Long p0);
    
    void syncLiveStatus();
    
    void updateLiveStatusForResource(final long p0, final int p1);
}
