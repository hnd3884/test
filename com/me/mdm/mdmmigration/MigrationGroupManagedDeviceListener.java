package com.me.mdm.mdmmigration;

import org.json.JSONArray;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import org.json.JSONObject;
import java.util.ArrayList;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class MigrationGroupManagedDeviceListener extends ManagedDeviceListener
{
    public Logger logger;
    
    public MigrationGroupManagedDeviceListener() {
        this.logger = Logger.getLogger("MDMMigrationLogger");
    }
    
    @Override
    public void deviceRegistered(final DeviceEvent deviceEvent) {
        try {
            final int platformType = new EnrollmentTemplateHandler().getPlatformForTemplate(50);
            final List<JSONObject> listToAssign = new ArrayList<JSONObject>();
            final JSONObject userDeviceAssociation = new APIServiceDataHandler().getUserDeviceAssociation(deviceEvent.udid, deviceEvent.resourceJSON.getString("SerialNumber"), deviceEvent.customerID);
            final Iterator iterator = userDeviceAssociation.keys();
            while (iterator.hasNext()) {
                final String userId = iterator.next().toString();
                for (int i = 0; i < userDeviceAssociation.getJSONArray(userId).length(); ++i) {
                    final JSONObject toAssign = userDeviceAssociation.getJSONArray(userId).getJSONObject(i);
                    toAssign.put("CustomerId", (Object)deviceEvent.customerID);
                    listToAssign.add(toAssign);
                }
                AdminEnrollmentHandler.assignUser(listToAssign, deviceEvent.resourceJSON.getLong("MANAGED_USER_ID"), 50, "DEVICE_ID", platformType);
            }
            for (int j = 0; j < listToAssign.size(); ++j) {
                final Long resourceId = new APIServiceDataHandler().getUserResourceIdForUserName(listToAssign.get(j).getString("UserName"), deviceEvent.customerID);
                final Long[] resourceIds = { resourceId };
                final List<Long> groupIds = new APIServiceDataHandler().getGroupIdsForUsername(listToAssign.get(j).getString("UserName"), deviceEvent.customerID);
                MDMGroupHandler.getInstance().addMembertoMultipleGroups(groupIds, resourceIds, deviceEvent.customerID, null);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while assigning device to user");
        }
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        MigrationGroupManagedDeviceListener.mdmlogger.info("Entering MigrationGroupManagedDeviceListener:deviceManaged");
        try {
            final Long[] resourceIds = { deviceEvent.resourceID };
            final APIServiceDataHandler apiServiceDataHandler = new APIServiceDataHandler();
            final String serialNo = deviceEvent.resourceJSON.getString("SerialNumber");
            final List<Long> groupId = new APIServiceDataHandler().getGroupIDsForDeviceUDID(deviceEvent.udid, serialNo, deviceEvent.customerID);
            if (groupId != null && !groupId.isEmpty()) {
                MDMGroupHandler.getInstance().addMembertoMultipleGroups(groupId, resourceIds, deviceEvent.customerID, null);
            }
            final String user_id = apiServiceDataHandler.getUserIDForDeviceUDID(deviceEvent.udid);
            final Long login_id = DMUserHandler.getLoginIdForUserId(Long.valueOf(user_id));
            final JSONArray appDetails = apiServiceDataHandler.getAppDetailsForDeviceId(deviceEvent.udid);
            final JSONArray profileIds = apiServiceDataHandler.getProfileIDsForDeviceId(deviceEvent.udid);
            final JSONObject requestJson = new JSONObject();
            requestJson.put("msg_header", (Object)new JSONObject().put("resource_identifier", (Object)new JSONObject().put("udid", (Object)deviceEvent.udid)));
            requestJson.getJSONObject("msg_header").put("filters", (Object)new JSONObject().put("customer_id", (Object)deviceEvent.customerID).put("user_id", (Object)user_id).put("login_id", (Object)login_id));
            if (profileIds != null && profileIds.length() > 0) {
                final JSONObject profileData = new JSONObject();
                profileData.put("profile_ids", (Object)profileIds);
                requestJson.put("msg_body", (Object)profileData);
                new ProfileFacade().associateProfilesToDevices(requestJson);
            }
            if (appDetails != null && appDetails.length() > 0) {
                final JSONObject appData = new JSONObject();
                appData.put("app_details", (Object)appDetails);
                requestJson.put("msg_body", (Object)appData);
                new AppFacade().associateAppsToDevices(requestJson);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception on MigrationGroupManagedDeviceListener", e.toString());
        }
        MigrationGroupManagedDeviceListener.mdmlogger.info("Exiting MigrationGroupManagedDeviceListener:deviceManaged");
    }
}
