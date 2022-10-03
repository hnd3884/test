package com.me.mdm.api.command.schedule;

import java.util.logging.Level;
import java.util.Map;
import java.util.logging.Logger;

public class ScheduledActionsMessageHandler
{
    protected static Logger logger;
    private static ScheduledActionsMessageHandler androidHandler;
    
    public static ScheduledActionsMessageHandler getInstance() {
        if (ScheduledActionsMessageHandler.androidHandler == null) {
            ScheduledActionsMessageHandler.androidHandler = new ScheduledActionsMessageHandler();
        }
        return ScheduledActionsMessageHandler.androidHandler;
    }
    
    public void processAndroidMessage(final Long resourceID, final Map requestMap) {
        ScheduledActionsMessageHandler.logger.log(Level.INFO, "*****Processing message for android device{0} with requestParams{1}*****", new Object[] { resourceID, requestMap });
        try {
            final Long collectionID = Long.parseLong(requestMap.get("CollectionID"));
            final String strStatus = requestMap.get("Status");
            Integer commandStatus = null;
            if (strStatus.equalsIgnoreCase("Acknowledged")) {
                commandStatus = 2;
            }
            else if (strStatus.equalsIgnoreCase("Error")) {
                commandStatus = 0;
            }
            GroupActionScheduleUtils.updateAndroidDeviceScheduledCommandStatus(resourceID, commandStatus, collectionID);
        }
        catch (final Exception e) {
            ScheduledActionsMessageHandler.logger.log(Level.SEVERE, "Error while updating the status for android device for scheduled actions with exception", e);
        }
    }
    
    static {
        ScheduledActionsMessageHandler.logger = Logger.getLogger("ActionsLogger");
        ScheduledActionsMessageHandler.androidHandler = null;
    }
}
