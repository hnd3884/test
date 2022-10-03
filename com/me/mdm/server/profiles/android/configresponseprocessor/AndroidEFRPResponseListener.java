package com.me.mdm.server.profiles.android.configresponseprocessor;

import java.util.List;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMProfileResponseListener;

public class AndroidEFRPResponseListener implements MDMProfileResponseListener
{
    private static final Logger LOGGER;
    
    @Override
    public JSONObject successHandler(final JSONObject params) {
        final Long resourceId = params.optLong("resourceId");
        AndroidEFRPResponseListener.LOGGER.log(Level.FINE, "SecurityInfo Command Triggered on success of EFRP");
        DeviceCommandRepository.getInstance().addSecurityInfoCommand(Arrays.asList(resourceId), 1);
        return new JSONObject();
    }
    
    @Override
    public JSONObject failureHandler(final JSONObject params) {
        return null;
    }
    
    @Override
    public boolean isNotify(final JSONObject params) {
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
