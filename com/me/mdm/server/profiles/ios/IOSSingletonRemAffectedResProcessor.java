package com.me.mdm.server.profiles.ios;

import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSSingletonRemAffectedResProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    private static Logger logger;
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        final JSONObject response = new JSONObject();
        try {
            final String status = params.optString("strStatus");
            if (status.equalsIgnoreCase("Error")) {
                IOSSingletonRemAffectedResProcessor.logger.log(Level.SEVERE, "Remove Affected Singleton command failed: {0}", String.valueOf(resourceID));
            }
            else if (status.equalsIgnoreCase("Acknowledged")) {
                final List<Long> resourceList = new ArrayList<Long>();
                resourceList.add(resourceID);
                Long commandId = DeviceCommandRepository.getInstance().getCommandID("SingletonRestriction");
                try {
                    if (commandId == null) {
                        commandId = DeviceCommandRepository.getInstance().addCommandWithPriority("SingletonRestriction", 40);
                    }
                }
                catch (final Exception ex) {
                    IOSSingletonRemAffectedResProcessor.logger.log(Level.SEVERE, "Exception while adding singleton command to the device", ex);
                }
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, resourceList, 1);
                NotificationHandler.getInstance().SendNotification(resourceList);
            }
        }
        catch (final Exception e) {
            IOSSingletonRemAffectedResProcessor.logger.log(Level.SEVERE, e, () -> "Exception in processing affected Singleton restriction :" + String.valueOf(n));
        }
        return response;
    }
    
    static {
        IOSSingletonRemAffectedResProcessor.logger = Logger.getLogger("MDMLogger");
    }
}
