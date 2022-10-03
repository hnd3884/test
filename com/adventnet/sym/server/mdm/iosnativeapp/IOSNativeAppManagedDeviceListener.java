package com.adventnet.sym.server.mdm.iosnativeapp;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.logging.Level;
import com.me.mdm.server.apps.AppFacade;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Collection;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.Properties;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.apps.AppTrashModeHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.adventnet.sym.server.mdm.core.DeviceEvent;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.core.ManagedDeviceListener;

public class IOSNativeAppManagedDeviceListener extends ManagedDeviceListener
{
    public Logger logger;
    
    public IOSNativeAppManagedDeviceListener() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public void deviceManaged(final DeviceEvent deviceEvent) {
        IOSNativeAppManagedDeviceListener.mdmlogger.info("Entering IOSNativeAppManagedDeviceListener:deviceManaged");
        if (deviceEvent.platformType == 1) {
            try {
                MEMDMTrackParamManager.getInstance().incrementTrackValue(deviceEvent.customerID, "APPLE_APNS_MODULE", "ENROLLED_APPLE_DEVICES");
                final Boolean isAutoDistribute = IosNativeAgentSettingsHandler.getInstance().isIOSNativeAgentEnable(deviceEvent.customerID);
                final Boolean isAutoInstall = false;
                if (isAutoDistribute) {
                    IOSNativeAppManagedDeviceListener.mdmlogger.info("IOSNativeAppManagedDeviceListener: Auto distribute enabled");
                    if (!MDMUtil.getInstance().isMacDevice(deviceEvent.resourceID)) {
                        final List resourceList = new ArrayList();
                        resourceList.add(deviceEvent.resourceID);
                        DeviceCommandRepository.getInstance().addSyncAgentSettingsCommand(resourceList, 2);
                        final boolean isAppExists = AppsUtil.getInstance().isAppExistsInPackage("com.manageengine.mdm.iosagent", 1, deviceEvent.customerID);
                        final boolean isAppInTrash = new AppTrashModeHandler().isAppInTrash("com.manageengine.mdm.iosagent", deviceEvent.customerID);
                        IOSNativeAppManagedDeviceListener.mdmlogger.info("IOSNativeAppManagedDeviceListener: IsIOSNativeAppExistsInPackage:" + isAppExists);
                        IOSNativeAppManagedDeviceListener.mdmlogger.info("IOSNativeAppManagedDeviceListener: IsIOSNativeAppExistsTrash:" + isAppInTrash);
                        if (isAppExists && !isAppInTrash) {
                            final Long packageID = AppsUtil.getInstance().getPackageId("com.manageengine.mdm.iosagent", 1, deviceEvent.customerID);
                            final Long profileID = AppsUtil.getInstance().getProfileIdForPackage(packageID, deviceEvent.customerID);
                            final Criteria resourceCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceEvent.resourceID, 0);
                            final Criteria profileCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)profileID, 0);
                            final Criteria notMarkedForDelete = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
                            final DataObject recentProfileForResourceDO = ProfileUtil.getRecentProfileForResourceDO(profileCriteria.and(resourceCriteria).and(notMarkedForDelete));
                            if (recentProfileForResourceDO == null || recentProfileForResourceDO.isEmpty()) {
                                final JSONObject createdUserDetailsJSON = ProfileUtil.getCreatedUserDetailsForProfile(profileID);
                                final JSONObject msgBody = new JSONObject();
                                final JSONArray appDetailsArray = new JSONArray();
                                final Long releaseLabelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(deviceEvent.customerID);
                                final JSONObject appDetail = new JSONObject();
                                appDetail.put("app_id", (Object)packageID);
                                appDetail.put("release_label_id", (Object)releaseLabelId);
                                Properties profileToBusinessStore = new Properties();
                                final List profileList = new ArrayList();
                                profileList.add(profileID);
                                profileToBusinessStore = new ProfileAssociateHandler().getPreferredProfileToBusinessStoreMap(profileToBusinessStore, 1, profileList, resourceList);
                                if (!profileToBusinessStore.isEmpty()) {
                                    appDetail.put("businessstore_id", ((Hashtable<K, Object>)profileToBusinessStore).get(profileID));
                                }
                                appDetailsArray.put((Object)appDetail);
                                msgBody.put("device_ids", (Collection)resourceList);
                                msgBody.put("app_details", (Object)appDetailsArray);
                                msgBody.put("silent_install", (Object)Boolean.TRUE);
                                msgBody.put("notify_user_via_email", (Object)Boolean.FALSE);
                                final JSONObject filters = new JSONObject();
                                filters.put("customer_id", (Object)deviceEvent.customerID);
                                filters.put("user_name", (Object)createdUserDetailsJSON.getString("FIRST_NAME"));
                                filters.put("user_id", createdUserDetailsJSON.getLong("USER_ID"));
                                filters.put("login_id", (Object)DMUserHandler.getLoginIdForUserId(Long.valueOf(createdUserDetailsJSON.getLong("USER_ID"))));
                                final JSONObject message = new JSONObject();
                                message.put("msg_body", (Object)msgBody);
                                final JSONObject msgHeader = new JSONObject();
                                msgHeader.put("filters", (Object)filters);
                                msgHeader.put("resource_identifier", (Object)new JSONObject());
                                message.put("msg_header", (Object)msgHeader);
                                new AppFacade().associateAppsToDevices(message);
                            }
                            else {
                                this.logger.log(Level.INFO, "IOSNativeAppManagedDeviceListener: MDM app is already installed");
                            }
                        }
                    }
                    else {
                        this.logger.log(Level.WARNING, "IOSNativeAgent cannot  be distributed to device with resID: {0} since the device is a macOS device", new Object[] { deviceEvent.resourceID });
                    }
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception in IOSNativeAppManagedDeviceListener", e);
            }
        }
        IOSNativeAppManagedDeviceListener.mdmlogger.info("Exiting IOSNativeAppManagedDeviceListener:deviceManaged");
    }
    
    @Override
    public void devicePreDelete(final DeviceEvent deviceEvent) {
        IOSNativeAppManagedDeviceListener.mdmlogger.info("Entering IOSNativeAppManagedDeviceListener:devicePreDelete");
        final List resourceList = new ArrayList();
        resourceList.add(deviceEvent.resourceID);
        ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
        IOSNativeAppManagedDeviceListener.mdmlogger.info("Exiting IOSNativeAppManagedDeviceListener:devicePreDelete");
    }
}
