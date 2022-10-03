package com.me.mdm.server.profiles.ios.configresponseprocessor;

import org.json.JSONObject;
import com.me.mdm.server.profiles.MDMProfileResponseListener;

public class IOSWifiRestrictionRemovedResponseListener implements MDMProfileResponseListener
{
    @Override
    public JSONObject successHandler(final JSONObject params) {
        return new IOSWifiRestrictionResponseListener().successHandler(params);
    }
    
    @Override
    public JSONObject failureHandler(final JSONObject params) {
        return null;
    }
    
    @Override
    public boolean isNotify(final JSONObject params) {
        return false;
    }
}
