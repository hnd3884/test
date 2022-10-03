package com.me.mdm.server.profiles.mac.configresponseprocessor;

import java.util.List;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MacFirmwareSucessHandler
{
    Logger logger;
    boolean isNotify;
    
    public MacFirmwareSucessHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.isNotify = false;
    }
    
    public JSONObject successHandler(final Long resourceID) {
        final List<Long> readIDList = new ArrayList<Long>();
        readIDList.add(resourceID);
        final Long fileVaultSecurityCommandID = DeviceCommandRepository.getInstance().addCommand("FileVaultUserLoginSecurityInfo");
        final List<Long> commandIDList = new ArrayList<Long>();
        commandIDList.add(fileVaultSecurityCommandID);
        this.logger.log(Level.INFO, "FirmwareLog: Going to add FirmwareUserLoginSecurityInfo command to device to automatically update inventory status once machine restarts:{0}", resourceID);
        DeviceCommandRepository.getInstance().assignDeviceCommandToOnUserChannel(commandIDList, readIDList);
        return null;
    }
}
