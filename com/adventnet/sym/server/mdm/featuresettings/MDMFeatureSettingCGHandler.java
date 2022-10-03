package com.adventnet.sym.server.mdm.featuresettings;

import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MDMFeatureSettingCGHandler
{
    public static void checkFeatureAndSendCommands(final Long groupID, final Long[] devices, final Long customerID) throws Exception {
        Logger.getLogger(MDMFeatureSettingCGHandler.class.getName()).log(Level.INFO, "Checking whether the battery feature is applicable for the device that is added to the group");
        final List<Long> list = new ArrayList<Long>();
        list.add(groupID);
        final boolean isGroupInvolved = MDMFeatureSettingsHandler.isGroupInvolved(customerID, groupID, 1);
        if (isGroupInvolved) {
            Logger.getLogger(MDMFeatureSettingCGHandler.class.getName()).log(Level.INFO, "battery feature is applicable for the device");
            final HashSet<Long> devicesSet = new HashSet<Long>();
            for (int i = 0; i < devices.length; ++i) {
                devicesSet.add(devices[i]);
            }
            final DataObject dataObject = MDMFeatureSettingsHandler.getMdDeviceDo(devicesSet);
            MDMFeatureSettingsHandler.addFeatureSettingsCommandsToCommandRepo(dataObject);
        }
    }
}
