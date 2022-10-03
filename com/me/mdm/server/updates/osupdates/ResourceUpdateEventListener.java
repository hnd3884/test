package com.me.mdm.server.updates.osupdates;

import java.util.Iterator;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ResourceUpdateEventListener implements ResourcesMissingUpdatesListener
{
    Long resID;
    private final Logger logger;
    
    public ResourceUpdateEventListener() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void onResourcesMissingUpdate(final ArrayList<Long> resources, final Long detectedUpdateID) {
        try {
            for (final Long res : resources) {
                final ResourceOSUpdateDataHandler dataHandler = new ResourceOSUpdateDataHandler();
                final JSONObject json = new JSONObject();
                json.put("RESOURCE_ID", (Object)res);
                json.put("UPDATE_ID", (Object)detectedUpdateID);
                json.put("STATUS", (Object)OSUpdateConstants.DeviceStatus.AVAILABLE);
                json.put("I18N_REMARKS", (Object)"An OS update is available for the device");
                dataHandler.addOrModifyDeviceAvailableUpdate(json);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred while processing resources missing update: ", e);
        }
    }
}
