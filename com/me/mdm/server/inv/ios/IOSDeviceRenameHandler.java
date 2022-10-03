package com.me.mdm.server.inv.ios;

import com.me.mdm.server.seqcommands.ios.IOSSeqCmdUtil;
import com.me.mdm.server.profiles.ios.IOSSingletonRestrictionHandler;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.logging.Logger;

public class IOSDeviceRenameHandler extends AppleDeviceRenameHandler
{
    private static final String RENAME_SEQ_HANDLER = "com.me.mdm.server.inv.ios.DeviceRenameSeqResponseHandler";
    private static final Logger LOGGER;
    
    @Override
    public Long getDeviceRenameCommand() {
        Long commandId = DeviceCommandRepository.getInstance().getCommandID("IOSDEVICENAME");
        if (commandId == null) {
            IOSDeviceRenameHandler.LOGGER.log(Level.FINE, "Creating new IOS rename command");
            commandId = this.createDeviceRenameSeqCmd();
        }
        return commandId;
    }
    
    private Long createDeviceRenameSeqCmd() {
        Long baseCommandId = null;
        try {
            IOSDeviceRenameHandler.LOGGER.log(Level.FINE, "Creating Sequential Command for iOS devices.");
            final JSONArray commandArray = new JSONArray();
            final JSONObject removeSingletonObject = new JSONObject();
            final Long removeSingletonCommandId = DeviceCommandRepository.getInstance().addCommandWithPriority("IOSRemoveDeviceNameRestriction", 40);
            removeSingletonObject.put("cmd_id", (Object)removeSingletonCommandId);
            removeSingletonObject.put("order", 1);
            removeSingletonObject.put("handler", (Object)"com.me.mdm.server.inv.ios.DeviceRenameSeqResponseHandler");
            final JSONObject deviceRenameObject = new JSONObject();
            final Long deviceRenameCommandID = DeviceCommandRepository.getInstance().addCommand("DeviceName");
            deviceRenameObject.put("cmd_id", (Object)deviceRenameCommandID);
            deviceRenameObject.put("order", 2);
            deviceRenameObject.put("handler", (Object)"com.me.mdm.server.inv.ios.DeviceRenameSeqResponseHandler");
            commandArray.put((Object)removeSingletonObject);
            commandArray.put((Object)deviceRenameObject);
            commandArray.put((Object)new IOSSingletonRestrictionHandler().prepareRestrictionSeqCmd(3));
            baseCommandId = DeviceCommandRepository.getInstance().addCommand("IOSDEVICENAME");
            final JSONObject params = new JSONObject();
            IOSSeqCmdUtil.getInstance().addSubCommandsArrayToSeqCmd(commandArray, baseCommandId, 60000L, params);
            IOSDeviceRenameHandler.LOGGER.log(Level.FINE, "Completed sequential command for iOS devices");
        }
        catch (final Exception ex) {
            IOSDeviceRenameHandler.LOGGER.log(Level.SEVERE, "Exception in creating the device rename command", ex);
        }
        return baseCommandId;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
