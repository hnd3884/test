package com.me.mdm.server.profiles.ios.response;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSClearRestrictPasscodeResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    private static final Logger LOGGER;
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        try {
            IOSClearRestrictPasscodeResponseProcessor.LOGGER.log(Level.INFO, "Cleared passcode restriction command:{0}", new Object[] { params });
            final Long resourceId = params.getLong("resourceId");
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            final List<Long> commandList = new ArrayList<Long>();
            commandList.add(DeviceCommandRepository.getInstance().getCommandID("ProfileList"));
            commandList.add(DeviceCommandRepository.getInstance().getCommandID("Restrictions"));
            IOSClearRestrictPasscodeResponseProcessor.LOGGER.log(Level.INFO, "Adding restriction and profile list command for clear restrict passcode. Params:{0}", new Object[] { params });
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, resourceList);
            NotificationHandler.getInstance().SendNotification(resourceList, 1);
            MDMUtil.getInstance().scheduleMDMCommand(resourceId, "RestrictPasscode", System.currentTimeMillis() + 3600000L);
        }
        catch (final JSONException e) {
            IOSClearRestrictPasscodeResponseProcessor.LOGGER.log(Level.SEVERE, "Exception in clear restriction passcode", (Throwable)e);
        }
        catch (final Exception e2) {
            IOSClearRestrictPasscodeResponseProcessor.LOGGER.log(Level.SEVERE, "Exception in waking up the device in clear restriction passcode");
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
