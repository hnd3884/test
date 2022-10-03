package com.adventnet.sym.server.mdm.featuresettings;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.settings.location.LocationSettingsCGMemberListener;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberListener;

public class MDMFeatureSettingsCGMemberListener implements MDMGroupMemberListener
{
    @Override
    public void groupMemberAdded(final MDMGroupMemberEvent groupEvent) {
        if (groupEvent.groupType != 7) {
            try {
                final Long groupId = groupEvent.groupID;
                final Long[] deviceIds = groupEvent.memberIds;
                final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(groupId);
                final HashSet deviceList = new HashSet();
                deviceList.addAll(Arrays.asList(deviceIds));
                MDMFeatureSettingCGHandler.checkFeatureAndSendCommands(groupId, deviceIds, customerId);
            }
            catch (final Exception e) {
                Logger.getLogger(LocationSettingsCGMemberListener.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    
    @Override
    public void groupMemberRemoved(final MDMGroupMemberEvent groupEvent) {
        if (groupEvent.groupType != 7) {
            try {
                final Long groupId = groupEvent.groupID;
                final Long[] deviceIds = groupEvent.memberIds;
                final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(groupId);
                final HashSet deviceList = new HashSet();
                deviceList.addAll(Arrays.asList(deviceIds));
                MDMFeatureSettingCGHandler.checkFeatureAndSendCommands(groupId, deviceIds, customerId);
            }
            catch (final Exception e) {
                Logger.getLogger(LocationSettingsCGMemberListener.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
}
