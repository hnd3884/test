package com.me.mdm.agent.handlers.macOS;

import com.adventnet.sym.server.mdm.macos.event.ComputerUserLoginEventsHandler;
import com.adventnet.sym.server.mdm.macos.event.ComputerUserLoginEvent;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class MacOSCommandResponseQueueProcessor
{
    public void processCommand(final DCQueueData queueDataObject) throws Exception {
        final String commandResponseData = (String)queueDataObject.queueData;
        final HashMap hashMap = PlistWrapper.getInstance().getHashFromPlist(commandResponseData);
        final String commandUUID = hashMap.get("CommandUUID");
        final String strUDID = hashMap.get("UDID");
        final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
        final HashMap hmap = PlistWrapper.getInstance().getHashFromPlist(commandResponseData);
        hmap.put("CUSTOMER_ID", queueDataObject.customerID + "");
        hmap.put("PLATFORM_TYPE", String.valueOf(1));
        final String s = commandUUID;
        switch (s) {
            case "ManagedUserLoginUpdate": {
                this.processMacUserLoginUpdate(resourceID, queueDataObject.customerID, hmap, queueDataObject.postTime);
                break;
            }
        }
    }
    
    private void processMacUserLoginUpdate(final Long resourceID, final Long customerID, final HashMap<String, String> idleMessageData, final Long postTime) {
        final ComputerUserLoginEvent event = new ComputerUserLoginEvent(resourceID, customerID, idleMessageData.get("UserShortName"), idleMessageData.get("UserLongName"), postTime);
        ComputerUserLoginEventsHandler.getInstance().invokeUserLoggedInComputerListener(event);
    }
}
