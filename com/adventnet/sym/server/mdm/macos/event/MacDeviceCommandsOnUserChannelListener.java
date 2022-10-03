package com.adventnet.sym.server.mdm.macos.event;

import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MacDeviceCommandsOnUserChannelListener implements ComputerUserLoginListener
{
    public Logger logger;
    
    public MacDeviceCommandsOnUserChannelListener() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void userLoggedInComputer(final ComputerUserLoginEvent loginEvent) {
        this.logger.log(Level.INFO, "Entering MacDeviceCommandsOnUserChannelListener.userLoggedInComputer");
        final Long resourceID = loginEvent.resourceID;
        final List<Long> resourceIDList = new ArrayList<Long>();
        resourceIDList.add(resourceID);
        final List commandIDs = DeviceCommandRepository.getInstance().getDeviceCommandsOnUserChannel(resourceID);
        if (commandIDs != null && commandIDs.size() > 0) {
            this.logger.log(Level.INFO, "Following commands [{0}]added to notify device and send to device channel for resource {1}", new Object[] { commandIDs, resourceID });
            DeviceCommandRepository.getInstance().clearDeviceCommandsOnUserChannel(resourceID, commandIDs);
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandIDs, resourceIDList);
            try {
                NotificationHandler.getInstance().SendNotification(resourceIDList, 1);
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception in userLoggedInComputer", ex);
            }
        }
        this.logger.log(Level.INFO, "Exiting MacDeviceCommandsOnUserChannelListener.userLoggedInComputer");
    }
}
