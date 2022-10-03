package com.me.mdm.api.internaltool;

import org.json.JSONObject;

public interface InternalToolInterface
{
    void createDemoUsers(final JSONObject p0) throws Exception;
    
    JSONObject refreshQueue(final JSONObject p0) throws Exception;
    
    JSONObject suspendQueue(final JSONObject p0) throws Exception;
    
    JSONObject resumeQueue(final JSONObject p0) throws Exception;
    
    JSONObject simulateDevices(final JSONObject p0) throws Exception;
    
    JSONObject simulateGroups(final JSONObject p0) throws Exception;
    
    JSONObject simulateScanDevices(final JSONObject p0) throws Exception;
}
