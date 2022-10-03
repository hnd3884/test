package com.me.mdm.server.profiles.mac.configresponseprocessor;

import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMProfileResponseListener;

public class MacFileVaultRemoveSuccessHandler implements MDMProfileResponseListener
{
    Logger logger;
    boolean isNotify;
    
    public MacFileVaultRemoveSuccessHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.isNotify = false;
    }
    
    @Override
    public JSONObject successHandler(final JSONObject params) {
        final JSONObject listenerResponse = new JSONObject();
        try {
            final Long resourceId = params.optLong("resourceId");
            final JSONArray commandArray = new JSONArray();
            commandArray.put((Object)"SecurityInfo");
            commandArray.put((Object)"ProfileList");
            this.logger.log(Level.INFO, "FileVaultLog: SecurityInfo,ProfileList Command added to device to update inventory about FV status on profile removal, deviceID{0}", resourceId);
            final JSONObject commandObject = new JSONObject();
            commandObject.put(String.valueOf(1), (Object)commandArray);
            listenerResponse.put("commandUUIDs", (Object)commandObject);
            this.isNotify = true;
        }
        catch (final JSONException e) {
            this.logger.log(Level.INFO, "FileVaultLog: Exception in MacFileVaultRemoveSuccessHandler.successHandler()", (Throwable)e);
        }
        return listenerResponse;
    }
    
    @Override
    public JSONObject failureHandler(final JSONObject params) {
        this.logger.log(Level.INFO, "FileVaultLog: Failurehandler for MacFilevault not implemented...");
        return null;
    }
    
    @Override
    public boolean isNotify(final JSONObject params) {
        return this.isNotify;
    }
}
