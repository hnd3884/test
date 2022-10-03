package com.me.mdm.server.adep;

import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import java.util.logging.Level;
import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;
import java.util.logging.Logger;

public class DeviceConfiguredCommandHandler
{
    private static DeviceConfiguredCommandHandler handler;
    private Logger logger;
    
    public DeviceConfiguredCommandHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public static DeviceConfiguredCommandHandler getInstance() {
        if (DeviceConfiguredCommandHandler.handler == null) {
            DeviceConfiguredCommandHandler.handler = new DeviceConfiguredCommandHandler();
        }
        return DeviceConfiguredCommandHandler.handler;
    }
    
    public NSDictionary getDeviceConfiguredPlist() {
        final NSDictionary root = new NSDictionary();
        root.put("CommandUUID", (Object)"DeviceConfigured");
        final NSDictionary commandDict = new NSDictionary();
        commandDict.put("RequestType", (Object)"DeviceConfigured");
        root.put("Command", (NSObject)commandDict);
        return root;
    }
    
    public String getDeviceConfiguredCommandAsString() {
        return this.getDeviceConfiguredPlist().toXMLPropertyList();
    }
    
    public void addDeviceConfiguredCommand(final Long resourceID) {
        this.logger.log(Level.INFO, "Going to send Device Configured command to resource: {0}", resourceID);
        final List<Long> resourceList = new ArrayList<Long>();
        resourceList.add(resourceID);
        try {
            final Long commandID = DeviceCommandRepository.getInstance().addCommand("DeviceConfigured");
            DeviceCommandRepository.getInstance().assignCommandToDevices(commandID, resourceList);
            NotificationHandler.getInstance().SendNotification(resourceList);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in sending device configured command to the device.", e);
        }
    }
    
    public void addInProgressStatus(final Long resourceID) throws Exception {
        this.logger.log(Level.INFO, "Device Configured Command In Progress for {0}", resourceID);
        MDMDBUtil.addOrUpdateAndPersist("AppleDeviceConfigStatus", new Object[][] { { "RESOURCE_ID", resourceID }, { "STATUS", 1 } });
    }
    
    public void addDeviceConfiguredCommand(final String resourceUDID) {
        this.logger.log(Level.INFO, "Going to send Device Configured command to resource: {0}", resourceUDID);
        try {
            final Long commandID = DeviceCommandRepository.getInstance().addCommand("DeviceConfigured");
            DeviceCommandRepository.getInstance().assignCommandToDevice(commandID, resourceUDID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in sending device configured command to the device.", e);
        }
    }
    
    static {
        DeviceConfiguredCommandHandler.handler = null;
    }
}
