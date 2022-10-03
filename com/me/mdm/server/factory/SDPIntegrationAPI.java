package com.me.mdm.server.factory;

import java.util.Properties;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public interface SDPIntegrationAPI
{
    void postMDMDataToSDP(final DCQueueData p0, final int p1);
    
    void handleSDPAlerts(final Properties p0, final String p1);
}
