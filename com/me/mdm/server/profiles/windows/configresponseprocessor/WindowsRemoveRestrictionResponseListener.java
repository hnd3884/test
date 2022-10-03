package com.me.mdm.server.profiles.windows.configresponseprocessor;

import java.util.List;
import java.util.logging.Level;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.DeviceDetails;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMProfileResponseListener;

public class WindowsRemoveRestrictionResponseListener implements MDMProfileResponseListener
{
    private static final Logger LOGGER;
    boolean isNotify;
    
    public WindowsRemoveRestrictionResponseListener() {
        this.isNotify = false;
    }
    
    @Override
    public JSONObject successHandler(final JSONObject params) {
        final JSONObject listenerResponse = new JSONObject();
        try {
            final Long resourceId = params.optLong("resourceId");
            final DeviceDetails deviceDetails = new DeviceDetails(resourceId);
            DeviceCommandRepository.getInstance().addRestrictionCommandToDevice(deviceDetails);
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            this.isNotify = true;
        }
        catch (final Exception ex) {
            WindowsRemoveRestrictionResponseListener.LOGGER.log(Level.SEVERE, "Exception while handling windows restriction response listener", ex);
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
