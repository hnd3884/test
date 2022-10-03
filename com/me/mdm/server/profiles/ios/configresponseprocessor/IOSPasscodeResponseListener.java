package com.me.mdm.server.profiles.ios.configresponseprocessor;

import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import java.util.List;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMProfileResponseListener;

public class IOSPasscodeResponseListener implements MDMProfileResponseListener
{
    private static final Logger LOGGER;
    private boolean isNotify;
    
    public IOSPasscodeResponseListener() {
        this.isNotify = false;
    }
    
    @Override
    public JSONObject successHandler(final JSONObject params) {
        final JSONObject listenerResponse = new JSONObject();
        try {
            final Long resourceId = params.optLong("resourceId");
            final List configDoList = (List)params.opt("configDoList");
            final JSONArray commandUUIDs = new JSONArray();
            for (int i = 0; i < configDoList.size(); ++i) {
                final DataObject dataObject = configDoList.get(i);
                final Integer configID = (Integer)dataObject.getFirstValue("ConfigData", "CONFIG_ID");
                if (configID.equals(172)) {
                    final Row passcodeRow = dataObject.getFirstRow("PasscodePolicy");
                    if (passcodeRow != null) {
                        final boolean restrictPasscode = (boolean)passcodeRow.get("RESTRICT_PASSCODE");
                        final boolean forcePasscode = (boolean)passcodeRow.get("FORCE_PASSCODE");
                        if (restrictPasscode && !forcePasscode) {
                            commandUUIDs.put((Object)"Restrictions");
                        }
                        if (restrictPasscode && forcePasscode) {
                            MDMUtil.getInstance().scheduleMDMCommand(resourceId, "RestrictPasscode", System.currentTimeMillis() + 3600000L);
                        }
                    }
                }
            }
            final JSONObject commandObject = new JSONObject();
            commandObject.put(String.valueOf(1), (Object)commandUUIDs);
            listenerResponse.put("commandUUIDs", (Object)commandObject);
        }
        catch (final Exception ex) {
            IOSPasscodeResponseListener.LOGGER.log(Level.SEVERE, "Exception in success handler in restriction listener", ex);
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
