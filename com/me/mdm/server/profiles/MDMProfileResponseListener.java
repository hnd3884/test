package com.me.mdm.server.profiles;

import org.json.JSONObject;

public interface MDMProfileResponseListener
{
    JSONObject successHandler(final JSONObject p0);
    
    JSONObject failureHandler(final JSONObject p0);
    
    boolean isNotify(final JSONObject p0);
}
