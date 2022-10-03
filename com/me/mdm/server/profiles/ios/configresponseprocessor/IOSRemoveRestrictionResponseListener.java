package com.me.mdm.server.profiles.ios.configresponseprocessor;

import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMProfileResponseListener;

public class IOSRemoveRestrictionResponseListener implements MDMProfileResponseListener
{
    private static final Logger LOGGER;
    boolean isNotify;
    
    public IOSRemoveRestrictionResponseListener() {
        this.isNotify = false;
    }
    
    @Override
    public JSONObject successHandler(final JSONObject params) {
        final JSONObject listenerResponse = new JSONObject();
        try {
            final JSONArray commandUUIDs = new JSONArray();
            new IOSWifiRestrictionRemovedResponseListener().successHandler(params);
            final JSONObject commandObject = new JSONObject();
            commandObject.put(String.valueOf(1), (Object)commandUUIDs);
            listenerResponse.put("commandUUIDs", (Object)commandObject);
        }
        catch (final Exception ex) {
            IOSRemoveRestrictionResponseListener.LOGGER.log(Level.SEVERE, "Exception in success handler in restriction listener", ex);
        }
        return listenerResponse;
    }
    
    @Override
    public JSONObject failureHandler(final JSONObject params) {
        return new IOSWifiRestrictionRemovedResponseListener().failureHandler(params);
    }
    
    @Override
    public boolean isNotify(final JSONObject params) {
        return this.isNotify;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
