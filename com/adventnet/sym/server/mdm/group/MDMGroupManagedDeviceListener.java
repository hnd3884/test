package com.adventnet.sym.server.mdm.group;

import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.me.mdm.server.customgroup.AllManagedDeviceGroupHandler;
import com.me.mdm.server.resource.MDMResourceDataProvider;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class MDMGroupManagedDeviceListener extends ManagedDeviceListener
{
    public Logger logger;
    
    public MDMGroupManagedDeviceListener() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public void devicePreRegister(final DeviceEvent oldDeviceEvent) {
        MDMGroupManagedDeviceListener.mdmlogger.info("Entering MDMGroupManagedDeviceListener:devicePreRegister");
        this.devicePreDelete(oldDeviceEvent);
        MDMGroupManagedDeviceListener.mdmlogger.info("Exiting MDMGroupManagedDeviceListener:devicePreRegister");
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        MDMGroupManagedDeviceListener.mdmlogger.info("Entering MDMGroupManagedDeviceListener:deviceManaged");
        try {
            final Long[] resourceIds = { deviceEvent.resourceID };
            final List<Long> groupId = MDMEnrollmentRequestHandler.getInstance().getGroupEnrollmentId(deviceEvent.enrollmentRequestId);
            if (MDMGroupHandler.getInstance().checkGroupsCategory(groupId, 5)) {
                final List<Long> staticUniqueGroupId = MDMGroupHandler.getInstance().getStaticUniqueGroupsForResourceId(deviceEvent.resourceID);
                if (staticUniqueGroupId != null && !staticUniqueGroupId.isEmpty()) {
                    for (final Long staticgid : staticUniqueGroupId) {
                        if (!groupId.contains(staticgid)) {
                            MDMGroupHandler.getInstance().removeMemberfromGroup(staticgid, resourceIds);
                        }
                    }
                }
            }
            if (groupId != null && !groupId.isEmpty()) {
                Long userId = null;
                if (deviceEvent.resourceJSON != null && deviceEvent.resourceJSON.has("DeviceReassignJson") && deviceEvent.resourceJSON.getJSONObject("DeviceReassignJson").has("TechUserID")) {
                    userId = deviceEvent.resourceJSON.getJSONObject("DeviceReassignJson").getLong("TechUserID");
                }
                else if (deviceEvent.resourceJSON != null && deviceEvent.resourceJSON.has("addedUserID")) {
                    userId = deviceEvent.resourceJSON.optLong("addedUserID");
                }
                MDMGroupHandler.getInstance().addMembertoMultipleGroups(groupId, resourceIds, deviceEvent.customerID, userId);
            }
            if (MDMResourceDataProvider.getResourceType(deviceEvent.resourceID) != 121) {
                new AllManagedDeviceGroupHandler().addMemberToAllDeviceGroup("ALL_MANAGED_MOBILE_DEVICE_GROUP", deviceEvent.customerID, deviceEvent.resourceID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception on device add listener of group", e);
        }
        MDMGroupManagedDeviceListener.mdmlogger.info("Exiting MDMGroupManagedDeviceListener:deviceManaged");
    }
    
    @Override
    public void devicePreDelete(final DeviceEvent deviceEvent) {
        MDMGroupManagedDeviceListener.mdmlogger.info("Entering MDMGroupManagedDeviceListener:devicePreDelete");
        try {
            final Criteria customGroupRelCriteria = new Criteria(new Column("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)deviceEvent.resourceID, 0);
            DataAccess.delete("CustomGroupMemberRel", customGroupRelCriteria);
            final List resourceList = new ArrayList();
            resourceList.add(deviceEvent.resourceID);
            ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception on device remove listener of group", e);
        }
        MDMGroupManagedDeviceListener.mdmlogger.info("Exiting MDMGroupManagedDeviceListener:devicePreDelete");
    }
}
