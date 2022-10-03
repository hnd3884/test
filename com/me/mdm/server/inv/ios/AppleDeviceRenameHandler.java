package com.me.mdm.server.inv.ios;

import java.util.List;
import org.json.JSONObject;
import com.me.mdm.server.seqcommands.SeqCmdRepository;
import java.util.logging.Level;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.logging.Logger;

public class AppleDeviceRenameHandler
{
    protected static final Logger LOGGER;
    
    public Long getDeviceRenameCommand() {
        final Long commandId = DeviceCommandRepository.getInstance().addCommand("DeviceName");
        return commandId;
    }
    
    public static AppleDeviceRenameHandler getDeviceRenameHandler(final int modelType) {
        AppleDeviceRenameHandler renameHandler = null;
        switch (modelType) {
            case 0:
            case 1:
            case 2: {
                renameHandler = new IOSDeviceRenameHandler();
                break;
            }
            case 3:
            case 4: {
                renameHandler = new AppleDeviceRenameHandler();
                break;
            }
            case 5: {
                renameHandler = new IOSDeviceRenameHandler();
                break;
            }
            default: {
                renameHandler = new AppleDeviceRenameHandler();
                break;
            }
        }
        return renameHandler;
    }
    
    public void addDeviceRenameCommand(final Long resourceId) {
        try {
            final Long commandId = this.getDeviceRenameCommand();
            final List<Long> commandIdList = new ArrayList<Long>();
            commandIdList.add(commandId);
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.add(resourceId);
            AppleDeviceRenameHandler.LOGGER.log(Level.INFO, "Device Rename done for the resourceID:{0}", new Object[] { resourceList });
            SeqCmdRepository.getInstance().executeSequentially(resourceList, commandIdList, new JSONObject());
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandIdList, resourceList);
        }
        catch (final Exception ex) {
            AppleDeviceRenameHandler.LOGGER.log(Level.SEVERE, "Exception while adding device command", ex);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
