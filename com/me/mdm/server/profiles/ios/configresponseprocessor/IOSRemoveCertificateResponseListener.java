package com.me.mdm.server.profiles.ios.configresponseprocessor;

import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMProfileResponseListener;

public class IOSRemoveCertificateResponseListener implements MDMProfileResponseListener
{
    private static final Logger LOGGER;
    boolean isNotify;
    
    public IOSRemoveCertificateResponseListener() {
        this.isNotify = false;
    }
    
    @Override
    public JSONObject successHandler(final JSONObject params) {
        final JSONObject listenerResponse = new JSONObject();
        try {
            final JSONArray commandUUIDs = new JSONArray();
            commandUUIDs.put((Object)"CertificateList");
            final JSONObject commandObject = new JSONObject();
            commandObject.put(String.valueOf(1), (Object)commandUUIDs);
            listenerResponse.put("commandUUIDs", (Object)commandObject);
        }
        catch (final Exception ex) {
            IOSRemoveCertificateResponseListener.LOGGER.log(Level.SEVERE, "Exception while handling iOS certificate response listener", ex);
        }
        return listenerResponse;
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
