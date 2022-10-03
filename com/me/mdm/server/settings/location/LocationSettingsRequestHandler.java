package com.me.mdm.server.settings.location;

import java.util.Hashtable;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.HashMap;
import com.me.mdm.agent.handlers.MacMDMAgentHandler;
import org.json.JSONObject;
import java.util.logging.Logger;

public class LocationSettingsRequestHandler
{
    private static LocationSettingsRequestHandler locSettings;
    public static Logger logger;
    
    public static LocationSettingsRequestHandler getInstance() {
        if (LocationSettingsRequestHandler.locSettings == null) {
            LocationSettingsRequestHandler.locSettings = new LocationSettingsRequestHandler();
        }
        return LocationSettingsRequestHandler.locSettings;
    }
    
    public void handleLocationSettingsUpdate(final JSONObject locationSettingsJSON) {
        this.handleiOSLocationSettings(locationSettingsJSON);
        LocationSettingsDataHandler.getInstance().handleLocationSettingsResource(locationSettingsJSON);
        final int trackingStatus = locationSettingsJSON.optInt("TRACKING_STATUS");
        final int historyEnabled = locationSettingsJSON.optInt("LOCATION_HISTORY_STATUS");
        if (trackingStatus == 1) {
            this.locationSettingsCommandHandling(locationSettingsJSON);
            new MacMDMAgentHandler().installAgentToDevices(locationSettingsJSON);
        }
        try {
            final Long customerId = locationSettingsJSON.getLong("CUSTOMER_ID");
            this.handleLocationSettingChanges(customerId, locationSettingsJSON);
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "DeleteUnwantedLocations");
            taskInfoMap.put("poolName", "asynchThreadPool");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            final Properties taskProperties = new Properties();
            ((Hashtable<String, String>)taskProperties).put("Customer_id", customerId.toString());
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.location.DeleteUnwantedLocationTask", taskInfoMap, taskProperties, "asynchThreadPool");
        }
        catch (final Exception ex) {
            LocationSettingsRequestHandler.logger.log(Level.WARNING, "Exception occured while location settings added");
        }
    }
    
    private void handleLocationSettingChanges(final Long customerId, final JSONObject settingsData) {
        try {
            final LocationSettingsDataHandler locationSettingsDataHandler = LocationSettingsDataHandler.getInstance();
            final List<Long> androidResList = locationSettingsDataHandler.getLocationSettingsApplicableDevices(customerId, 2);
            final List<Long> iOSResList = locationSettingsDataHandler.getLocationSettingsApplicableDevices(customerId, 1);
            if (!androidResList.isEmpty()) {
                DeviceCommandRepository.getInstance().addLocationConfigurationCommand(androidResList, 1);
                NotificationHandler.getInstance().SendNotification(androidResList, 2);
            }
            if (!iOSResList.isEmpty()) {
                DeviceCommandRepository.getInstance().addLocationConfigurationCommand(iOSResList, 2);
            }
        }
        catch (final Exception ex) {
            LocationSettingsRequestHandler.logger.log(Level.WARNING, "Exception occurred while handleLocationSettingChanges", ex);
        }
    }
    
    public void handleiOSLocationSettings(final JSONObject locationSettingsJSON) {
        try {
            final Long customerID = locationSettingsJSON.getLong("CUSTOMER_ID");
            final int locationTrackingStatus = locationSettingsJSON.optInt("TRACKING_STATUS");
            final boolean isAppExists = AppsUtil.getInstance().isAppExistsInPackage("com.manageengine.mdm.iosagent", 1, customerID);
            if (locationTrackingStatus == 1 && isAppExists) {
                MessageProvider.getInstance().hideMessage("LOCATION_SETTINGS", customerID);
                final MDMAgentSettingsHandler agentHandler = new MDMAgentSettingsHandler();
                final JSONObject iosData = new JSONObject();
                iosData.put("CUSTOMER_ID", (Object)customerID);
                iosData.put("IS_NATIVE_APP_ENABLE", true);
                agentHandler.processiOSSettings(iosData);
            }
            else {
                MessageProvider.getInstance().unhideMessage("LOCATION_SETTINGS", customerID);
            }
        }
        catch (final Exception ex) {
            LocationSettingsRequestHandler.logger.log(Level.WARNING, "Exception occurred in handleiOSLocationSettings() method : {0}", ex);
        }
    }
    
    public org.json.simple.JSONObject getLocationResourceCriteriaJSON(final JSONObject locationSettingsJSON) {
        org.json.simple.JSONObject resourceJSON = null;
        try {
            Long resourceId = null;
            int resourceType = -1;
            String resourceName = null;
            String style = "";
            final Long loginId = locationSettingsJSON.optLong("loginId");
            final Long userId = locationSettingsJSON.optLong("userId");
            final Boolean hasAllManagedMobileDevicesRole = DMUserHandler.isUserInRole(loginId, "All_Managed_Mobile_Devices");
            final SelectQuery locationResourceQuery = LocationSettingsDataHandler.getInstance().getLocationResourceCriteriaQuery(locationSettingsJSON);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(locationResourceQuery);
            resourceJSON = new org.json.simple.JSONObject();
            org.json.simple.JSONObject resourcePropJSON = null;
            String resourceIdStr = "";
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("Resource");
                while (iterator.hasNext()) {
                    final Row resourceRow = iterator.next();
                    Boolean shouldAllowDelete = Boolean.FALSE;
                    final org.json.simple.JSONObject userDataJSON = new org.json.simple.JSONObject();
                    resourceId = Long.parseLong(resourceRow.get("RESOURCE_ID").toString());
                    resourceType = (int)resourceRow.get("RESOURCE_TYPE");
                    Long customGroupCreatedBy = null;
                    Long userCutomGroupLoginId = null;
                    if (resourceType == 101) {
                        resourceName = resourceRow.get("NAME").toString();
                        final Row customGroupExtn = dataObject.getRow("CustomGroupExtn", new Criteria(Column.getColumn("CustomGroupExtn", "RESOURCE_ID"), (Object)resourceId, 0));
                        customGroupCreatedBy = (Long)customGroupExtn.get("CREATED_BY");
                        final Row userCustomGroupMappingRow = dataObject.getRow("UserCustomGroupMapping", new Criteria(Column.getColumn("UserCustomGroupMapping", "GROUP_RESOURCE_ID"), (Object)resourceId, 0));
                        if (userCustomGroupMappingRow != null) {
                            userCutomGroupLoginId = (Long)userCustomGroupMappingRow.get("LOGIN_ID");
                        }
                        style = "background-color: #eee;color: #000; font:12px 'Lato', 'Roboto', sans-serif; text-decoration:none;";
                    }
                    else {
                        final Row managedDeviceExtnRow = dataObject.getRow("ManagedDeviceExtn", new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)resourceId, 0));
                        resourceName = managedDeviceExtnRow.get("NAME").toString();
                        style = "background-color: #edf8e7; color: #478727; font:12px 'Lato', 'Roboto', sans-serif; text-decoration:none;";
                    }
                    final org.json.simple.JSONObject groupJSON = (org.json.simple.JSONObject)resourceJSON.get((Object)String.valueOf(resourceId));
                    Boolean checkShouldAllowDelete = Boolean.FALSE;
                    if (groupJSON != null) {
                        checkShouldAllowDelete = (Boolean)((org.json.simple.JSONObject)groupJSON.get((Object)"USERDATA")).get((Object)"shouldAllowDelete");
                    }
                    if (hasAllManagedMobileDevicesRole || loginId.equals(userCutomGroupLoginId) || userId.equals(customGroupCreatedBy) || checkShouldAllowDelete) {
                        shouldAllowDelete = Boolean.TRUE;
                    }
                    userDataJSON.put((Object)"shouldAllowDelete", (Object)shouldAllowDelete);
                    resourcePropJSON = new org.json.simple.JSONObject();
                    resourcePropJSON.put((Object)"NODE_ID", (Object)resourceId);
                    resourcePropJSON.put((Object)"NODE_NAME", (Object)resourceName);
                    resourcePropJSON.put((Object)"NODE_STYLE", (Object)style);
                    resourcePropJSON.put((Object)"USERDATA", (Object)userDataJSON);
                    resourceIdStr = String.valueOf(resourceId);
                    resourceJSON.put((Object)resourceIdStr, (Object)resourcePropJSON);
                }
            }
        }
        catch (final DataAccessException e) {
            LocationSettingsRequestHandler.logger.log(Level.WARNING, "Exception occurred in getLocationResourceCriteriaJSON() method : {0}", (Throwable)e);
        }
        return resourceJSON;
    }
    
    public void locationSettingsCommandHandling(final JSONObject locationSettingsJSON) {
        try {
            final DataObject locationDeviceCommandDO = LocationSettingsDataHandler.getInstance().getLocationSettingsCommandDO(locationSettingsJSON);
            final ArrayList<Long> deviceIdList = new ArrayList<Long>();
            final List iOSDeviceIdList = new ArrayList();
            final List androidDeviceIdList = new ArrayList();
            final List windowsDeviceIdList = new ArrayList();
            Long deviceId = null;
            final List profileTypeResourceList = new ArrayList();
            final List nativeTypeResourceList = new ArrayList();
            final Long commandId = DeviceCommandRepository.getInstance().addCommand("SyncAgentSettings");
            final ArrayList<Long> commandList = new ArrayList<Long>();
            commandList.add(commandId);
            final int startIndex = 0;
            final int size = 500;
            int platformType = 1;
            if (!locationDeviceCommandDO.isEmpty()) {
                final Iterator locationDeviceStatusItr = locationDeviceCommandDO.getRows("ManagedDevice");
                Row locationSettingsRow = null;
                while (locationDeviceStatusItr.hasNext()) {
                    locationSettingsRow = locationDeviceStatusItr.next();
                    deviceId = (Long)locationSettingsRow.get("RESOURCE_ID");
                    deviceIdList.add(deviceId);
                }
            }
            while (!deviceIdList.isEmpty()) {
                ArrayList<Long> tempList;
                if (deviceIdList.size() > size) {
                    tempList = new ArrayList<Long>(deviceIdList.subList(startIndex, size));
                }
                else {
                    tempList = new ArrayList<Long>(deviceIdList);
                }
                deviceIdList.removeAll(tempList);
                final ArrayList<DeviceDetails> deviceDetailsList = new DeviceDetails().getDeviceDetails(tempList);
                for (final DeviceDetails deviceDetails : deviceDetailsList) {
                    platformType = deviceDetails.platform;
                    final boolean isIOSNativeAppRegistered = deviceDetails.nativeAgentInstalled;
                    if (platformType == 1 && isIOSNativeAppRegistered) {
                        iOSDeviceIdList.add(deviceId);
                        nativeTypeResourceList.add(deviceDetails.resourceId);
                    }
                    else if (platformType == 2) {
                        androidDeviceIdList.add(deviceId);
                        if (deviceDetails.profileOwner) {
                            profileTypeResourceList.add(deviceDetails.resourceId);
                        }
                        else {
                            nativeTypeResourceList.add(deviceDetails.resourceId);
                        }
                    }
                    else {
                        if (platformType != 3 || !isIOSNativeAppRegistered || deviceDetails.agentVersion == null || !deviceDetails.agentVersion.startsWith("9.2.") || !ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(deviceDetails.osVersion, 8.1f)) {
                            continue;
                        }
                        windowsDeviceIdList.add(deviceId);
                        nativeTypeResourceList.add(deviceDetails.resourceId);
                    }
                }
            }
            DeviceCommandRepository.getInstance().assignCommandToDevicesInChunck(profileTypeResourceList, commandList, 1);
            DeviceCommandRepository.getInstance().assignCommandToDevicesInChunck(nativeTypeResourceList, commandList, 2);
            final HashMap notificationMap = new HashMap();
            notificationMap.put(2, androidDeviceIdList);
            notificationMap.put(303, windowsDeviceIdList);
            NotificationHandler.getInstance().SendNotification(notificationMap);
        }
        catch (final Exception ex) {
            LocationSettingsRequestHandler.logger.log(Level.WARNING, "Exception occurred in locationSettingsCommandHandling() method : {0}", ex);
        }
    }
    
    public JSONObject getiOSLocationSettingPayloadJSON(final DeviceDetails device) {
        final JSONObject requestData = new JSONObject();
        try {
            final JSONObject locationJsonObj = LocationSettingsDataHandler.getInstance().getLocationConfigCommandData(device);
            requestData.put("LocationSettings", (Object)locationJsonObj);
        }
        catch (final JSONException ex) {
            LocationSettingsRequestHandler.logger.log(Level.SEVERE, "Exception while getting sync agent settings ", (Throwable)ex);
        }
        return requestData;
    }
    
    public JSONObject getAndroidLocationSettingPayloadJSON(final DeviceDetails device) {
        final JSONObject locationJsonObj = new JSONObject();
        try {
            final JSONObject locationSettingsJSON = LocationSettingsDataHandler.getInstance().getLocationSettingsJSON(device.customerId);
            final boolean isLocationTrackingDeviceEnable = LocationSettingsDataHandler.getInstance().isLocationTrackingEnabledforDevice(device.resourceId);
            locationJsonObj.put("LocationServices", isLocationTrackingDeviceEnable);
            locationJsonObj.put("ContactInterval", locationSettingsJSON.get("LOCATION_INTERVAL"));
            locationJsonObj.put("LocationAccuracy", locationSettingsJSON.get("LOCATION_ACCURACY"));
        }
        catch (final JSONException ex) {
            LocationSettingsRequestHandler.logger.log(Level.SEVERE, "Exception while getting sync agent settings ", (Throwable)ex);
        }
        return locationJsonObj;
    }
    
    public void disableLocationOnEditionChange() throws DataAccessException {
        final List customerList = LocationSettingsDataHandler.getInstance().disableLocationOnEditionChange();
        for (final Long customerId : customerList) {
            this.handleLocationSettingChanges(customerId, new JSONObject());
        }
    }
    
    static {
        LocationSettingsRequestHandler.locSettings = null;
        LocationSettingsRequestHandler.logger = Logger.getLogger("MDMLogger");
    }
}
