package com.me.devicemanagement.framework.server.license;

import org.json.JSONObject;

public interface ServiceHandler
{
    void migrate(final JSONObject p0, final License p1, final License p2);
    
    void StartUp();
    
    void reset(final JSONObject p0, final License p1, final License p2);
}
