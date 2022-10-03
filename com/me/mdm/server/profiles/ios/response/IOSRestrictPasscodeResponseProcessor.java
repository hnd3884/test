package com.me.mdm.server.profiles.ios.response;

import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSRestrictPasscodeResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    private static final Logger LOGGER;
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        IOSRestrictPasscodeResponseProcessor.LOGGER.log(Level.INFO, "Inside restrict passcode response processor:{0}", params);
        try {
            final Long resourceId = params.optLong("resourceId");
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            final List commandList = new ArrayList();
            commandList.add(DeviceCommandRepository.getInstance().getCommandID("Restrictions"));
            commandList.add(DeviceCommandRepository.getInstance().getCommandID("ProfileList"));
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, resourceList);
            NotificationHandler.getInstance().SendNotification(resourceList, 1);
        }
        catch (final Exception e) {
            IOSRestrictPasscodeResponseProcessor.LOGGER.log(Level.SEVERE, "Exception in processing queued commands", e);
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
