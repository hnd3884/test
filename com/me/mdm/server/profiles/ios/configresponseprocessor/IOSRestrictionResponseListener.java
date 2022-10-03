package com.me.mdm.server.profiles.ios.configresponseprocessor;

import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMProfileResponseListener;

public class IOSRestrictionResponseListener implements MDMProfileResponseListener
{
    private static final Logger LOGGER;
    boolean isNotify;
    
    public IOSRestrictionResponseListener() {
        this.isNotify = false;
    }
    
    @Override
    public JSONObject successHandler(final JSONObject params) {
        return null;
    }
    
    @Override
    public JSONObject failureHandler(final JSONObject params) {
        return null;
    }
    
    @Override
    public boolean isNotify(final JSONObject params) {
        return this.isNotify;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
