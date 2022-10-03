package com.adventnet.sym.server.mdm.command;

import com.adventnet.sym.server.mdm.core.DeviceEvent;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.me.mdm.server.resource.MDMResourceDataProvider;
import java.util.Collections;
import com.me.mdm.api.command.schedule.ScheduledActionsUtils;
import java.util.Properties;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.mdm.api.command.schedule.DeviceActionToCollectionHandler;
import com.me.mdm.api.command.schedule.GroupActionToCollectionHandler;
import com.me.mdm.api.command.schedule.GroupActionScheduleUtils;
import com.adventnet.ds.query.Join;
import com.me.mdm.server.inv.actions.ClearAppDataHandler;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import org.json.JSONArray;
import com.me.mdm.server.command.CommandStatusHandler;
import java.util.Map;
import com.me.mdm.server.inv.actions.InvActionUtil;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.adventnet.sym.server.mdm.apps.ios.AppleAppLicenseMgmtHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.settings.wifi.MdDeviceWifiSSIDDBHandler;
import com.me.mdm.server.notification.pushnotification.DeviceAppNotificationHandler;
import com.me.mdm.server.notification.pushnotification.DeviceAppNotification;
import java.util.Iterator;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import java.util.Collection;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.List;
import com.me.mdm.api.command.schedule.ScheduleCommandService;
import java.util.logging.Logger;

public class DeviceInvCommandHandler
{
    private static DeviceInvCommandHandler deviceHandler;
    private static Logger logger;
    private final ScheduleCommandService scheduleCommandService;
    private static Logger actionLogger;
    
    public DeviceInvCommandHandler() {
        this.scheduleCommandService = new ScheduleCommandService();
    }
    
    public static DeviceInvCommandHandler getInstance() {
        if (DeviceInvCommandHandler.deviceHandler == null) {
            DeviceInvCommandHandler.deviceHandler = new DeviceInvCommandHandler();
        }
        return DeviceInvCommandHandler.deviceHandler;
    }
    
    public void scanDevice(final List resourceIDs, final Long userId) {
        try {
            final String scanPersonalApps = MDMUtil.getSyMParameter("DoNotScanPersonalApps");
            final List andResourceList = new ArrayList();
            final List iosResourceList = new ArrayList();
            final List chromeResourceList = new ArrayList();
            final List winResourceListNativeApp = new ArrayList();
            final List win810AboveDeviceList = new ArrayList();
            final List androidResourceListNativeApp = new ArrayList();
            final HashMap iOSAgentNotificationMap = new HashMap();
            final ArrayList<Long> scanAllowedDeviceList = this.getScanAllowedList((ArrayList<Long>)resourceIDs);
            DeviceInvCommandHandler.logger.log(Level.INFO, "Eligile for scan - Size: {0}, List: {1}", new Object[] { scanAllowedDeviceList.size(), scanAllowedDeviceList.toString() });
            final int startIndex = 0;
            final int size = 500;
            final String[] deviceScanCommandsArray = { "SecurityInfo", "CertificateList", "InstalledApplicationList", "FetchAppleAgentDetails", "ManagedApplicationList", "Restrictions", "GetLocation", "DeviceInformation", "ProfileList", "ProvisioningProfileList", "AvailableOSUpdates", "AssetScan", "AndroidInvScan", "WmiQuery;ComputerSystem;NetworkAdapterConfig;ComputerSystemProduct", "PersonalAppsInfo", "AssetScan;USER_INVOKED", "AssetScanContainer;USER_INVOKED", "DeviceInformation;USER_INVOKED", "AndroidInvScan;USER_INVOKED", "UserList", "AndroidInvScanContainer;USER_INVOKED" };
            final HashMap commandMap = new HashMap();
            for (int i = 0; i < deviceScanCommandsArray.length; ++i) {
                final DeviceCommand command = new DeviceCommand();
                final String commandUUID = deviceScanCommandsArray[i];
                command.commandUUID = commandUUID;
                if (!commandUUID.contains("USER_INVOKED")) {
                    command.commandType = commandUUID;
                }
                else {
                    command.commandType = commandUUID.split(";")[0];
                }
                command.commandStr = "--";
                commandMap.put(command.commandUUID, command);
            }
            final JSONObject commandUUIDcommandIDJSON = DeviceCommandRepository.getInstance().addCommand(commandMap);
            final JSONObject params = new JSONObject();
            params.put("DoNotScanPersonalApps", (Object)scanPersonalApps);
            params.put("commandUUIDcommandIDMap", (Object)commandUUIDcommandIDJSON);
            params.put("USER_ID".toLowerCase(), (Object)userId);
            while (scanAllowedDeviceList.size() > 0) {
                ArrayList<Long> tempList;
                if (scanAllowedDeviceList.size() > size) {
                    tempList = new ArrayList<Long>(scanAllowedDeviceList.subList(startIndex, size));
                }
                else {
                    tempList = new ArrayList<Long>(scanAllowedDeviceList);
                }
                scanAllowedDeviceList.removeAll(tempList);
                final ArrayList deviceDetailsList = new DeviceDetails().getDeviceDetails(tempList);
                for (final Object object : deviceDetailsList) {
                    final DeviceDetails deviceDetails = (DeviceDetails)object;
                    if (deviceDetails.platform == 3) {
                        if (deviceDetails.nativeAgentInstalled && deviceDetails.locationTrackingEnabled && !ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(deviceDetails.osVersion, 10.0f) && ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(deviceDetails.osVersion, 8.1f) && deviceDetails.osVersion != null && deviceDetails.osVersion.startsWith("9.2.")) {
                            winResourceListNativeApp.add(deviceDetails.resourceId);
                        }
                        if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(deviceDetails.osVersion, 8.1f)) {
                            win810AboveDeviceList.add(deviceDetails.resourceId);
                        }
                    }
                    if (deviceDetails.platform == 2) {
                        andResourceList.add(deviceDetails.resourceId);
                    }
                    if (deviceDetails.platform == 1) {
                        iosResourceList.add(deviceDetails.resourceId);
                        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableFetchWifiSSSID")) {
                            DeviceInvCommandHandler.logger.log(Level.INFO, "Wifi SSID Collection feature enabled for resource:{0}", new Object[] { deviceDetails.resourceId });
                            final JSONObject privacyJson = deviceDetails.privacySettingsJSON;
                            final int fetchWifiSSID = privacyJson.optInt("fetch_wifi_ssid");
                            if (fetchWifiSSID != 0) {
                                List<Long> privacyNotEnabled;
                                if (iOSAgentNotificationMap.containsKey("privacyNotEnabled")) {
                                    privacyNotEnabled = iOSAgentNotificationMap.get("appNotInstalled");
                                }
                                else {
                                    privacyNotEnabled = new ArrayList<Long>();
                                }
                                privacyNotEnabled.add(deviceDetails.resourceId);
                                iOSAgentNotificationMap.put("privacyNotEnabled", privacyNotEnabled);
                            }
                            else if (!deviceDetails.nativeAgentInstalled) {
                                List<Long> appNotInstalled;
                                if (iOSAgentNotificationMap.containsKey("appNotInstalled")) {
                                    appNotInstalled = iOSAgentNotificationMap.get("appNotInstalled");
                                }
                                else {
                                    appNotInstalled = new ArrayList<Long>();
                                }
                                appNotInstalled.add(deviceDetails.resourceId);
                                iOSAgentNotificationMap.put("appNotInstalled", appNotInstalled);
                            }
                            else if (deviceDetails.agentVersionCode > 1559L) {
                                final Long customerId = deviceDetails.customerId;
                                List<Long> notificationList;
                                if (iOSAgentNotificationMap.containsKey(customerId)) {
                                    notificationList = iOSAgentNotificationMap.get(customerId);
                                }
                                else {
                                    notificationList = new ArrayList<Long>();
                                }
                                notificationList.add(deviceDetails.resourceId);
                                iOSAgentNotificationMap.put(customerId, notificationList);
                            }
                            else {
                                List<Long> unSupportedVersionList;
                                if (iOSAgentNotificationMap.containsKey("unSupportedVersion")) {
                                    unSupportedVersionList = iOSAgentNotificationMap.get("unSupportedVersion");
                                }
                                else {
                                    unSupportedVersionList = new ArrayList<Long>();
                                }
                                unSupportedVersionList.add(deviceDetails.resourceId);
                                iOSAgentNotificationMap.put("unSupportedVersion", unSupportedVersionList);
                            }
                        }
                    }
                    if (deviceDetails.platform == 4) {
                        chromeResourceList.add(deviceDetails.resourceId);
                    }
                    if ((scanPersonalApps == null || !scanPersonalApps.equalsIgnoreCase("true")) && deviceDetails.profileOwner && deviceDetails.nativeAgentInstalled) {
                        androidResourceListNativeApp.add(deviceDetails.resourceId);
                    }
                }
                DeviceCommandRepository.getInstance().addDeviceScanCommand(deviceDetailsList, params);
                final int scanStatus = 1;
                final String remarks = "dc.patch.apd.scan_initiated";
                this.addOrUpdateDeviceScanInitiatedStatus(tempList, scanStatus, remarks);
                MDMInvDataPopulator.getInstance().deleteDeviceScanToErrCode(tempList);
            }
            final HashMap notificationMap = new HashMap();
            notificationMap.put(1, iosResourceList);
            notificationMap.put(2, andResourceList);
            notificationMap.put(3, win810AboveDeviceList);
            notificationMap.put(303, winResourceListNativeApp);
            notificationMap.put(201, androidResourceListNativeApp);
            notificationMap.put(4, chromeResourceList);
            NotificationHandler.getInstance().SendNotification(notificationMap);
            this.sendAgentScanNotificationToiOSDevice(iOSAgentNotificationMap);
        }
        catch (final Exception ex) {
            DeviceInvCommandHandler.logger.log(Level.WARNING, "Exception occurred in scanDevice() method : {0}", ex);
        }
    }
    
    private void sendAgentScanNotificationToiOSDevice(final HashMap iOSAgentNotificationMap) {
        if (iOSAgentNotificationMap.isEmpty()) {
            DeviceInvCommandHandler.logger.log(Level.INFO, "Empty list in iOS agent notification so returning");
            return;
        }
        final JSONObject customPayload = new JSONObject();
        customPayload.put("notification_type", (Object)"AGENT_SYNC_INITIATED");
        final DeviceAppNotification appNotification = new DeviceAppNotification(1, 2, customPayload);
        appNotification.setI18nTitle("mdm.agent.notification.scan_for_details");
        appNotification.setI18nMessage("mdm.agent.notification.inventory_scan_inprogress");
        final List<Long> unSupportedVersion = iOSAgentNotificationMap.remove("unSupportedVersion");
        final List<Long> appNotInstalled = iOSAgentNotificationMap.remove("appNotInstalled");
        final List<Long> privacyNotEnabled = iOSAgentNotificationMap.remove("privacyNotEnabled");
        final Iterator iterator = iOSAgentNotificationMap.keySet().iterator();
        List<Long> tokenMissingList = null;
        while (iterator.hasNext()) {
            final Long customerId = iterator.next();
            final List resourceList = iOSAgentNotificationMap.get(customerId);
            tokenMissingList = DeviceAppNotificationHandler.getInstance().addPushNotification(resourceList, 101, appNotification, customerId);
            DeviceInvCommandHandler.logger.log(Level.INFO, "Token Missing List:{0}", new Object[] { tokenMissingList });
        }
        final MdDeviceWifiSSIDDBHandler wifiSSIDDBHandler = MdDeviceWifiSSIDDBHandler.getInstance();
        if (privacyNotEnabled != null && privacyNotEnabled.size() > 0) {
            DeviceInvCommandHandler.logger.log(Level.INFO, "Privacy not enabled for collecting Wifi SSID for resource List{0}", new Object[] { privacyNotEnabled });
            wifiSSIDDBHandler.updateWifiErrorCode(privacyNotEnabled, 35106);
        }
        if (unSupportedVersion != null && unSupportedVersion.size() > 0) {
            DeviceInvCommandHandler.logger.log(Level.INFO, "Unsupported Version for collecting Wifi SSID for resource List{0}", new Object[] { unSupportedVersion });
            wifiSSIDDBHandler.updateWifiErrorCode(unSupportedVersion, 35105);
        }
        if (appNotInstalled != null && appNotInstalled.size() > 0) {
            DeviceInvCommandHandler.logger.log(Level.INFO, "App not installed for collecting Wifi SSID for resource List{0}", new Object[] { appNotInstalled });
            wifiSSIDDBHandler.updateWifiErrorCode(appNotInstalled, 35101);
        }
        if (tokenMissingList != null && tokenMissingList.size() > 0) {
            DeviceInvCommandHandler.logger.log(Level.INFO, "Unsupported Version for collecting Wifi SSID for resource List{0}", new Object[] { unSupportedVersion });
            wifiSSIDDBHandler.updateWifiErrorCode(tokenMissingList, 35102);
        }
    }
    
    public void scanContainer(final Long resourceID) {
        try {
            DeviceInvCommandHandler.logger.log(Level.FINE, "Inside scanContainer()", resourceID);
            final DeviceDetails deviceDetails = new DeviceDetails(resourceID);
            DeviceCommandRepository.getInstance().addContainerScanCommand(deviceDetails);
        }
        catch (final Exception ex) {
            DeviceInvCommandHandler.logger.log(Level.WARNING, "Exception occurred in scanContainer() method : {0}", ex);
        }
    }
    
    public void scanAllDevices(final Long customerID, final Long userId) {
        try {
            DeviceInvCommandHandler.logger.log(Level.INFO, "Inside scanAllDevices()", customerID);
            final ArrayList arrEnrolledResourceIDs = ManagedDeviceHandler.getInstance().getManagedDevicesListForCustomer(customerID);
            this.scanDevice(arrEnrolledResourceIDs, userId);
        }
        catch (final Exception ex) {
            DeviceInvCommandHandler.logger.log(Level.WARNING, "Exception occurred in scanAllDevices() method : {0}", ex);
        }
    }
    
    private ArrayList<Long> getScanAllowedList(final ArrayList<Long> scanDeviceList) throws DataAccessException {
        final ArrayList<Long> resourceList = new ArrayList<Long>(scanDeviceList);
        final ArrayList<Long> scanAllowedDeviceList = new ArrayList<Long>();
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceScanStatus", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceScanStatus"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceScanStatus", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceScanStatus", "SCAN_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("MdDeviceScanStatus", "SCAN_START_TIME"));
        selectQuery.setCriteria(resourceCriteria);
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator iterator = dataObject.getRows("MdDeviceScanStatus");
            while (iterator.hasNext()) {
                final Row mdDeviceScanStatusRow = iterator.next();
                final Integer scanStatus = (Integer)mdDeviceScanStatusRow.get("SCAN_STATUS");
                final Long resourceId = (Long)mdDeviceScanStatusRow.get("RESOURCE_ID");
                resourceList.remove(resourceId);
                boolean isAllowed = false;
                if (scanStatus == null || scanStatus == 2 || scanStatus == 0 || scanStatus == 1 || scanStatus == 4) {
                    isAllowed = true;
                    if (scanStatus != null && (scanStatus == 1 || scanStatus == 4)) {
                        final Long calcTime = System.currentTimeMillis() - 600000L;
                        final Long scanStartedTime = (Long)mdDeviceScanStatusRow.get("SCAN_START_TIME");
                        if (scanStartedTime > calcTime) {
                            isAllowed = false;
                        }
                    }
                }
                if (isAllowed) {
                    scanAllowedDeviceList.add(resourceId);
                }
            }
        }
        scanAllowedDeviceList.addAll(resourceList);
        return scanAllowedDeviceList;
    }
    
    private boolean isScanAllowed(final Long resourceID) {
        boolean isAllowed = false;
        try {
            final Row scanResRow = DBUtil.getRowFromDB("MdDeviceScanStatus", "RESOURCE_ID", (Object)resourceID);
            if (scanResRow == null) {
                return true;
            }
            final Integer curScanStaus = (Integer)scanResRow.get("SCAN_STATUS");
            if (curScanStaus == null || curScanStaus == 2 || curScanStaus == 0 || curScanStaus == 1 || curScanStaus == 4) {
                isAllowed = true;
                if (curScanStaus == 1 || curScanStaus == 4) {
                    final Long calcTime = System.currentTimeMillis() - 600000L;
                    final Long scanStartedTime = (Long)scanResRow.get("SCAN_START_TIME");
                    if (scanStartedTime > calcTime) {
                        isAllowed = false;
                    }
                }
            }
        }
        catch (final Exception ex) {
            DeviceInvCommandHandler.logger.log(Level.INFO, ex, () -> "Exception occurred while checking the device for allowed scan " + n);
        }
        return isAllowed;
    }
    
    public void addOrUpdateDeviceScanInitiatedStatus(final Long resourceID, final Integer scanStatus, final String remarks) {
        try {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("MdDeviceScanStatus"));
            query.addSelectColumn(new Column("MdDeviceScanStatus", "*"));
            final Criteria criteria = new Criteria(new Column("MdDeviceScanStatus", "RESOURCE_ID"), (Object)resourceID, 0, false);
            query.setCriteria(criteria);
            final DataObject deviceScanDO = MDMUtil.getPersistence().get((SelectQuery)query);
            Row deviceScanRow = null;
            if (deviceScanDO.isEmpty()) {
                deviceScanRow = new Row("MdDeviceScanStatus");
                deviceScanRow.set("RESOURCE_ID", (Object)resourceID);
                deviceScanRow.set("SCAN_START_TIME", (Object)System.currentTimeMillis());
                deviceScanRow.set("SCAN_STATUS", (Object)scanStatus);
                deviceScanRow.set("REMARKS", (Object)remarks);
                deviceScanDO.addRow(deviceScanRow);
                MDMUtil.getPersistence().add(deviceScanDO);
            }
            else {
                deviceScanRow = deviceScanDO.getFirstRow("MdDeviceScanStatus");
                deviceScanRow.set("SCAN_START_TIME", (Object)System.currentTimeMillis());
                deviceScanRow.set("SCAN_STATUS", (Object)scanStatus);
                deviceScanRow.set("REMARKS", (Object)remarks);
                deviceScanDO.updateRow(deviceScanRow);
                MDMUtil.getPersistence().update(deviceScanDO);
            }
        }
        catch (final DataAccessException ex) {
            DeviceInvCommandHandler.logger.log(Level.WARNING, "Exception in addOrUpdateDeviceScanInitiatedStatus method : {0}", (Throwable)ex);
        }
    }
    
    public void addOrUpdateDeviceScanInitiatedStatus(final ArrayList<Long> resourceList, final Integer scanStatus, final String remarks) {
        try {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("MdDeviceScanStatus"));
            query.addSelectColumn(new Column("MdDeviceScanStatus", "*"));
            final Criteria criteria = new Criteria(new Column("MdDeviceScanStatus", "RESOURCE_ID"), (Object)resourceList.toArray(), 8, false);
            query.setCriteria(criteria);
            final DataObject deviceScanDO = MDMUtil.getPersistence().get((SelectQuery)query);
            for (final Long resourceID : resourceList) {
                Row deviceScanRow = deviceScanDO.getRow("MdDeviceScanStatus", new Criteria(Column.getColumn("MdDeviceScanStatus", "RESOURCE_ID"), (Object)resourceID, 0));
                if (deviceScanRow == null) {
                    deviceScanRow = new Row("MdDeviceScanStatus");
                    deviceScanRow.set("RESOURCE_ID", (Object)resourceID);
                    deviceScanRow.set("SCAN_START_TIME", (Object)System.currentTimeMillis());
                    deviceScanRow.set("SCAN_STATUS", (Object)scanStatus);
                    deviceScanRow.set("REMARKS", (Object)remarks);
                    deviceScanDO.addRow(deviceScanRow);
                }
                else {
                    deviceScanRow.set("SCAN_START_TIME", (Object)System.currentTimeMillis());
                    deviceScanRow.set("SCAN_STATUS", (Object)scanStatus);
                    deviceScanRow.set("REMARKS", (Object)remarks);
                    deviceScanDO.updateRow(deviceScanRow);
                }
            }
            MDMUtil.getPersistenceLite().update(deviceScanDO);
        }
        catch (final DataAccessException ex) {
            DeviceInvCommandHandler.logger.log(Level.WARNING, "Exception in addOrUpdateDeviceScanInitiatedStatus method : {0}", (Throwable)ex);
        }
    }
    
    public ArrayList updateDeviceScanStatus(final Criteria criteria, final Integer scanStatus) {
        final ArrayList arrUpdatedDeviceIds = new ArrayList();
        try {
            final DataObject dobj = MDMUtil.getPersistence().get("MdDeviceScanStatus", criteria);
            if (!dobj.isEmpty()) {
                final Iterator rowIterator = dobj.getRows("MdDeviceScanStatus");
                Long resourceId = null;
                while (rowIterator.hasNext()) {
                    final Row row = rowIterator.next();
                    resourceId = (Long)row.get("RESOURCE_ID");
                    row.set("SCAN_STATUS", (Object)scanStatus);
                    row.set("REMARKS", (Object)"dc.wc.inv.common.SCAN_FAILED_INTERRUPTED");
                    dobj.updateRow(row);
                    arrUpdatedDeviceIds.add(resourceId);
                }
                MDMUtil.getPersistence().update(dobj);
            }
        }
        catch (final Exception ex) {
            DeviceInvCommandHandler.logger.log(Level.WARNING, "Exception while updating Device Scan Status : {0}", ex);
        }
        return arrUpdatedDeviceIds;
    }
    
    public void removeScanCommandFromCache(final ArrayList arrUpdatedDeviceIds) {
        for (final Long resourceID : arrUpdatedDeviceIds) {
            final String sUDID = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceID);
            DeviceCommandRepository.getInstance().removeDeviceScanCommand(sUDID, 1);
        }
    }
    
    public void SendCommandToContainer(final DeviceDetails device, final String commandName, final Long userId) throws Exception {
        if (commandName.equalsIgnoreCase("ActivateKnoxLicense") || commandName.equalsIgnoreCase("CreateContainer") || commandName.equalsIgnoreCase("ActivateKnox")) {
            final Long resourceId = device.resourceId;
            final String license = KnoxUtil.getInstance().getLicense(resourceId);
            if (license == null) {
                DeviceInvCommandHandler.logger.log(Level.INFO, "Quantity Exhausted So cannot Assign CreateContainer Command to Resource {0}", resourceId);
                return;
            }
        }
        else if (commandName.equalsIgnoreCase("DeactivateKnox")) {
            final Long resourceId = device.resourceId;
            if (KnoxUtil.getInstance().getAssignedLicense(resourceId) == null) {
                DeviceInvCommandHandler.logger.log(Level.INFO, "Container not created so cannot assign RemoveContainer Command to Resource {0}", resourceId);
                return;
            }
        }
        DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, commandName);
        final List resourceList = new ArrayList();
        resourceList.add(device.resourceId);
        NotificationHandler.getInstance().SendNotification(resourceList, device.platform);
        if (commandName.equalsIgnoreCase("DeactivateKnox")) {
            KnoxUtil.getInstance().updateStatus(device.resourceId, -1, "knox.activation.remarks.container.deactivation.initiated", -1);
        }
        this.addOrUpdateCommandInitiatedCommandHistory(device.resourceId, DeviceCommandRepository.getInstance().getCommandID(commandName), userId, commandName);
    }
    
    public void sendCommandToDevice(final DeviceDetails device, final String commandName, final Long userId) throws Exception {
        final List resourceList = new ArrayList();
        resourceList.add(device.resourceId);
        Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandName);
        Label_1091: {
            if (commandName.equalsIgnoreCase("CorporateWipe")) {
                if (device.platform == 1) {
                    ProfileAssociateHandler.getInstance().removeAppsForResource(device.resourceId);
                    if (this.isNonInventoryCommand(commandName)) {
                        DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, resourceList, 1);
                    }
                    else {
                        DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, commandName);
                    }
                    NotificationHandler.getInstance().SendNotification(resourceList, device.platform);
                }
                else if (device.platform == 3) {
                    DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, commandName);
                    NotificationHandler.getInstance().SendNotification(resourceList, device.platform);
                    DeviceCommandRepository.getInstance().addCorporateWipeCommand(device.udid);
                    NotificationHandler.getInstance().SendNotification(resourceList, 303);
                }
                else if (device.platform == 2) {
                    DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, commandName);
                    NotificationHandler.getInstance().SendNotification(resourceList, 2);
                    if (ManagedDeviceHandler.getInstance().isPersonalProfileManaged(device.resourceId)) {
                        DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, commandName, 2);
                        NotificationHandler.getInstance().SendNotification(resourceList, 201);
                    }
                }
            }
            else if (commandName.contains("Announcement") || commandName.contains("DeviceCompliance;Collection=") || commandName.contains("RemoveDeviceCompliance;Collection=")) {
                switch (device.platform) {
                    case 1: {
                        DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, resourceList, 2);
                        break Label_1091;
                    }
                    case 2: {
                        DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, resourceList, 1);
                        break Label_1091;
                    }
                    case 3: {
                        DeviceInvCommandHandler.logger.log(Level.INFO, "Windows device not supported yet");
                        break;
                    }
                }
                DeviceInvCommandHandler.logger.log(Level.SEVERE, " -- sendCommandToDevice()  >   invalid platform    ");
            }
            else if (device.platform == 1 && commandName.contains("EraseDevice")) {
                new AppleAppLicenseMgmtHandler().revokeAllAppLicensesForResource(device.resourceId, device.customerId);
                if (this.isNonInventoryCommand(commandName)) {
                    DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, resourceList, 1);
                }
                else {
                    DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, commandName);
                }
                NotificationHandler.getInstance().SendNotification(resourceList, device.platform);
            }
            else if (device.platform == 2 && commandName.equalsIgnoreCase("EraseDevice")) {
                if (ManagedDeviceHandler.getInstance().isProfileOwner(device.resourceId)) {
                    if (!IosNativeAppHandler.getInstance().isIOSNativeAgentInstalled(device.resourceId)) {
                        throw new SyMException(1001, "Personal Space is not managed by MDM so Remote wipe cannot be performed", (Throwable)null);
                    }
                    DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, commandName, 2);
                    NotificationHandler.getInstance().SendNotification(resourceList, 201);
                }
                else {
                    if (!this.isNonInventoryCommand(commandName)) {
                        DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, commandName, 1);
                    }
                    else {
                        DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, resourceList, 1);
                    }
                    NotificationHandler.getInstance().SendNotification(resourceList, 2);
                }
            }
            else if (device.platform == 2 && (commandName.equalsIgnoreCase("ResetPasscode") || commandName.equalsIgnoreCase("ClearPasscode"))) {
                if (ManagedDeviceHandler.getInstance().isProfileOwner(device.resourceId)) {
                    if (new VersionChecker().isGreaterOrEqual(device.osVersion, "7.0")) {
                        DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, commandName, 1);
                        NotificationHandler.getInstance().SendNotification(resourceList, 2);
                    }
                    else {
                        DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, commandName, 2);
                        NotificationHandler.getInstance().SendNotification(resourceList, 201);
                    }
                }
                else {
                    DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, commandName, 1);
                    NotificationHandler.getInstance().SendNotification(resourceList, 2);
                }
            }
            else if (device.platform == 3 && commandName.equalsIgnoreCase("GetLocation")) {
                int cmdRepType = 2;
                int notificationType = 303;
                if (ManagedDeviceHandler.getInstance().isOsVersionGreaterThanForResource(device.resourceId, 10.0f)) {
                    cmdRepType = 1;
                    notificationType = 3;
                }
                DeviceCommandRepository.getInstance().addLocationCommand(resourceList, cmdRepType);
                NotificationHandler.getInstance().SendNotification(resourceList, notificationType);
            }
            else if (device.platform == 1 && commandName.equalsIgnoreCase("RemoteSession")) {
                final Long commandID = DeviceCommandRepository.getInstance().getCommandID("RemoteSession");
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandID, resourceList, 2);
            }
            else if ((commandName.contains("PlayLostModeSound") || commandName.contains("DeviceRing")) && this.isNonInventoryCommand(commandName)) {
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, resourceList, 1);
                NotificationHandler.getInstance().SendNotification(resourceList, device.platform);
            }
            else if (commandName.contains("UnlockUserAccount")) {
                if (commandId == null) {
                    DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, commandName);
                    commandId = DeviceCommandRepository.getInstance().getCommandID(commandName);
                }
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, resourceList, 1);
                NotificationHandler.getInstance().SendNotification(resourceList, device.platform);
            }
            else if (commandName.contains("LogOutUser")) {
                DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, commandName);
                if (device.privacySettingsJSON.optInt("recent_users_report", -1) != 2) {
                    DeviceInvCommandHandler.logger.log(Level.INFO, "Sending userlist command on logout");
                    DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, "UserList");
                }
                NotificationHandler.getInstance().SendNotification(resourceList, device.platform);
            }
            else {
                DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceId, commandName);
                NotificationHandler.getInstance().SendNotification(resourceList, device.platform);
            }
        }
        if (!commandName.equalsIgnoreCase("RemoteSession") && !commandName.equalsIgnoreCase("ResumeKioskCommand") && !commandName.equalsIgnoreCase("PauseKioskCommand")) {
            if (commandId == null) {
                commandId = DeviceCommandRepository.getInstance().getCommandID(commandName);
            }
            this.addOrUpdateCommandInitiatedCommandHistory(device.resourceId, commandId, userId, commandName);
        }
    }
    
    public boolean isNonInventoryCommand(final String commandName) {
        boolean result = false;
        if (commandName.contains("Collection=")) {
            result = true;
        }
        return result;
    }
    
    private UniqueValueHolder addDeviceActionHistory(final DataObject actionCommandDO, final Long cmdHisID, final String command_name) {
        UniqueValueHolder device_action_id = null;
        try {
            final Row deviceActionRow = new Row("DeviceActionHistory");
            deviceActionRow.set("ACTION_ID", (Object)InvActionUtil.getEquivalentActionType(command_name));
            deviceActionRow.set("COMMAND_HISTORY_ID", (Object)cmdHisID);
            actionCommandDO.addRow(deviceActionRow);
            device_action_id = (UniqueValueHolder)deviceActionRow.get("DEVICE_ACTION_ID");
        }
        catch (final Exception e) {
            DeviceInvCommandHandler.logger.log(Level.WARNING, "Exception occurred in addDeviceActionHistory....", e);
        }
        return device_action_id;
    }
    
    public void addOrUpdateGroupActionsCommandHistory(final List<Long> resourceList, final Long commandID, final Long userId, final String commandName, final Long actionId, final Map info) {
        try {
            final boolean isGroupAction = Boolean.valueOf(info.get("is_group_action").toString());
            final String reasonMsg = info.get("reason_message").toString();
            String remarks = this.getRemarksString(commandName);
            if (info.containsKey("scheduled")) {
                remarks = this.getRemarksForAction(info.get("scheduled"));
            }
            final CommandStatusHandler commandStatusHandler = new CommandStatusHandler();
            final JSONObject commandStatusJSON = new JSONObject();
            for (final Long resourceId : resourceList) {
                final JSONObject commandJSON = new JSONObject();
                final JSONArray commandsJSONArray = new JSONArray();
                commandJSON.put("RESOURCE_ID", (Object)resourceId);
                commandJSON.put("ADDED_BY", (Object)userId);
                commandJSON.put("COMMAND_ID", (Object)commandID);
                if (info.containsKey("scheduled")) {
                    commandJSON.put("COMMAND_STATUS", 7);
                }
                commandJSON.put("REMARKS", (Object)remarks);
                commandsJSONArray.put((Object)commandJSON);
                commandStatusJSON.put(String.valueOf(resourceId), (Object)commandsJSONArray);
            }
            final HashMap criteriaMap = new HashMap();
            final ArrayList<Long> cmdList = new ArrayList<Long>();
            cmdList.add(commandID);
            criteriaMap.put("COMMAND_ID", cmdList);
            final JSONObject commandJSON = commandStatusHandler.populateCommandStatusForDevices(commandStatusJSON, criteriaMap);
            if (isGroupAction) {
                MDMGroupHandler.getInstance().addOrUpdateGroupActionsResourceCount(actionId, resourceList.size());
                this.addGroupToCommandMapping(commandJSON, actionId);
            }
            MDMGroupHandler.getInstance().populateDeviceActionDetails(commandName, reasonMsg, commandJSON);
        }
        catch (final JSONException e) {
            DeviceInvCommandHandler.logger.log(Level.SEVERE, " Exception occurred in  addOrUpdateCommandInitiatedCommandHistory()", (Throwable)e);
        }
    }
    
    private void addGroupToCommandMapping(final JSONObject commandDetailsJSON, final Long groupActionId) {
        try {
            final DataObject actionCommandDO = MDMUtil.getPersistence().constructDataObject();
            final Iterator<String> resourceItr = commandDetailsJSON.keys();
            while (resourceItr.hasNext()) {
                final JSONObject commandJSON = commandDetailsJSON.getJSONArray((String)resourceItr.next()).getJSONObject(0);
                final Long commandHistoryID = commandJSON.getLong("COMMAND_HISTORY_ID");
                final Row actionCommandRow = new Row("GroupActionToCommand");
                actionCommandRow.set("GROUP_ACTION_ID", (Object)groupActionId);
                actionCommandRow.set("COMMAND_HISTORY_ID", (Object)commandHistoryID);
                actionCommandDO.addRow(actionCommandRow);
            }
            MDMUtil.getPersistence().add(actionCommandDO);
        }
        catch (final Exception e) {
            DeviceInvCommandHandler.logger.log(Level.SEVERE, "Exception occurred in  addGroupToCommandMapping()   ", e);
        }
    }
    
    private void addReasonForCommandHistoryMapping(final DataObject actionCommandDO, final Long cmdHisID, final String reasonMsg) {
        try {
            final Row cmdHisReasonRow = new Row("ReasonForCommandHistory");
            cmdHisReasonRow.set("COMMAND_HISTORY_ID", (Object)cmdHisID);
            cmdHisReasonRow.set("REASON_MESSAGE", (Object)reasonMsg);
            actionCommandDO.addRow(cmdHisReasonRow);
        }
        catch (final Exception e) {
            DeviceInvCommandHandler.logger.log(Level.WARNING, "Exception occurred in addReasonForCommandHistoryMapping....", e);
        }
    }
    
    private void addDeviceActionToResetAppsMapping(final DataObject actionCommandDO, final Collection<Long> app_group_ids, final UniqueValueHolder device_action_id) {
        try {
            for (final Long app_group_id : app_group_ids) {
                final Row actionCommandRow = new Row("DeviceActionToResetApps");
                actionCommandRow.set("DEVICE_ACTION_ID", (Object)device_action_id);
                actionCommandRow.set("APP_GROUP_ID", (Object)app_group_id);
                actionCommandDO.addRow(actionCommandRow);
            }
        }
        catch (final Exception e) {
            DeviceInvCommandHandler.logger.log(Level.WARNING, "Exception occurred in addDeviceActionToResetAppsMapping....", e);
        }
    }
    
    public void addOrUpdateCommandInitiatedCommandHistory(final long resourceId, final Long commandId, final Long userId, final String commandName) {
        try {
            final String remarks = this.getRemarksString(commandName);
            final CommandStatusHandler commandStatusHandler = new CommandStatusHandler();
            final JSONObject statusJSON = new JSONObject();
            statusJSON.put("COMMAND_ID", (Object)commandId);
            statusJSON.put("RESOURCE_ID", resourceId);
            statusJSON.put("REMARKS", (Object)remarks);
            statusJSON.put("ADDED_BY", (Object)userId);
            final Long commandHistoryID = commandStatusHandler.populateCommandStatus(statusJSON);
            if (commandName.equals("ShutDownDevice") || commandName.equals("RestartDevice")) {
                final DataObject actionCommandDO = MDMUtil.getPersistence().constructDataObject();
                this.populateDeviceActionMapping(resourceId, commandHistoryID, commandName, "--", actionCommandDO);
                MDMUtil.getPersistence().add(actionCommandDO);
            }
        }
        catch (final JSONException | DataAccessException e) {
            DeviceInvCommandHandler.logger.log(Level.SEVERE, " -- addOrUpdateCommandInitiatedCommandHistory() >   Error   ", e);
        }
    }
    
    public void populateDeviceActionDetails(final String command_name, final String reasonMsg, final JSONObject commandDetailsJSON) {
        try {
            final DataObject actionCommandDO = MDMUtil.getPersistence().constructDataObject();
            final Iterator<String> resourceItr = commandDetailsJSON.keys();
            while (resourceItr.hasNext()) {
                final String resourceID = resourceItr.next();
                final JSONObject commandJSON = commandDetailsJSON.getJSONArray(resourceID).getJSONObject(0);
                final Long commandHistoryID = commandJSON.getLong("COMMAND_HISTORY_ID");
                this.populateDeviceActionMapping(Long.valueOf(resourceID), commandHistoryID, command_name, reasonMsg, actionCommandDO);
            }
            MDMUtil.getPersistence().add(actionCommandDO);
        }
        catch (final Exception e) {
            DeviceInvCommandHandler.logger.log(Level.WARNING, "Exception occurred in populateDeviceActionDetails....", e);
        }
    }
    
    public void populateDeviceActionMapping(final Long resourceId, final Long commandHistoryID, final String command_name, final String reasonMsg, final DataObject actionCommandDO) {
        try {
            final UniqueValueHolder device_action_id = this.addDeviceActionHistory(actionCommandDO, commandHistoryID, command_name);
            this.addReasonForCommandHistoryMapping(actionCommandDO, commandHistoryID, reasonMsg);
            if (command_name.equals("ClearAppData")) {
                final List<Long> app_group_ids = ClearAppDataHandler.getInstance().getAppGroupIdsForResourceId(resourceId);
                this.addDeviceActionToResetAppsMapping(actionCommandDO, app_group_ids, device_action_id);
            }
        }
        catch (final Exception e) {
            DeviceInvCommandHandler.logger.log(Level.SEVERE, " -- populateDeviceActionMapping() >   Error   ", e);
        }
    }
    
    public String getRemarksForAction(final String actionName) {
        switch (actionName) {
            case "scheduled_restart": {
                return "Restart action will happen for the scheduled Time";
            }
            case "scheduled_shutdown": {
                return "Shutdown action will happen for the scheduled Time";
            }
            default: {
                return "No Remarks found for action";
            }
        }
    }
    
    public String getRemarksString(final String commandName) {
        final String commandType = commandName.split(";")[0];
        String remarks = "";
        final String s = commandType;
        switch (s) {
            case "EraseDevice": {
                remarks = "me.mdm.devicecommand.erase_device";
                break;
            }
            case "CorporateWipe": {
                remarks = "me.mdm.devicecommand.corporate_wipe";
                break;
            }
            case "ClearAppData": {
                remarks = "me.mdm.devicecommand.clear_app_data";
                break;
            }
            case "MacFileVaultPersonalKeyRotate": {
                remarks = "me.mdm.devicecommand.filevault_rotate";
                break;
            }
            case "RestartDevice": {
                remarks = "me.mdm.devicecommand.restart";
                break;
            }
            case "ShutDownDevice": {
                remarks = "me.mdm.devicecommand.shutdown";
                break;
            }
        }
        return remarks;
    }
    
    public int uploadAgentLog(final Long resourceID) {
        int counter = 0;
        try {
            DeviceInvCommandHandler.logger.log(Level.FINE, "Inside uploadAgentLog()", resourceID);
            final List resourceList = new ArrayList();
            resourceList.add(resourceID);
            DeviceCommandRepository.getInstance().adduploadAgentLogCommand(resourceID, 1);
            NotificationHandler.getInstance().SendNotification(resourceList, 2);
            ++counter;
        }
        catch (final Exception ex) {
            DeviceInvCommandHandler.logger.log(Level.WARNING, "Exception occurred in uploadAgentLog() method : {0}", ex);
        }
        return counter;
    }
    
    public void suspendCommandForDeviceList(final List<Long> resList, final String command) {
        try {
            final Long currentTime = System.currentTimeMillis();
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("CommandHistory"));
            sQuery.addJoin(new Join("CommandHistory", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
            final Criteria resCriteria = new Criteria(Column.getColumn("CommandHistory", "RESOURCE_ID"), (Object)resList.toArray(), 8);
            final Criteria cmdStatusCriteria = new Criteria(Column.getColumn("CommandHistory", "COMMAND_STATUS"), (Object)new Object[] { 1, 4 }, 8);
            final Criteria cmdCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)command, 0);
            sQuery.setCriteria(resCriteria.and(cmdCriteria).and(cmdStatusCriteria));
            sQuery.addSelectColumn(Column.getColumn("CommandHistory", "*"));
            final DataObject commandDO = MDMUtil.getPersistence().get(sQuery);
            if (!commandDO.isEmpty()) {
                final Iterator<Row> iterator = commandDO.getRows("CommandHistory");
                while (iterator.hasNext()) {
                    final Row commandRow = iterator.next();
                    if (commandRow != null) {
                        final int commandStatus = Integer.valueOf(commandRow.get("COMMAND_STATUS").toString());
                        if (commandStatus == 2) {
                            continue;
                        }
                        commandRow.set("COMMAND_STATUS", (Object)6);
                        commandRow.set("UPDATED_TIME", (Object)currentTime);
                        commandRow.set("REMARKS", (Object)"mdm.bulkaction.device.same_action_suspend");
                        commandDO.updateRow(commandRow);
                    }
                }
                MDMUtil.getPersistence().update(commandDO);
            }
        }
        catch (final Exception ex) {
            DeviceInvCommandHandler.logger.log(Level.WARNING, "Exception occurred in suspendCommandForDeviceList() method : {0}", ex);
        }
    }
    
    public void updateGroupActionManualSuspendRemarks(final Long action_id) {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
            final Criteria actionIdCriteria = new Criteria(Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID"), (Object)action_id, 0);
            final Criteria actionStatusCriteria = new Criteria(Column.getColumn("GroupActionHistory", "ACTION_STATUS"), (Object)new Object[] { 4, 1, 7, 0, 2 }, 8);
            sQuery.setCriteria(actionIdCriteria.and(actionStatusCriteria));
            sQuery.addSelectColumn(Column.getColumn("GroupActionHistory", "*"));
            final DataObject grpActionDO = MDMUtil.getPersistence().get(sQuery);
            if (!grpActionDO.isEmpty()) {
                final Iterator<Row> iterator = grpActionDO.getRows("GroupActionHistory");
                while (iterator.hasNext()) {
                    final Row grpActiomRow = iterator.next();
                    if (grpActiomRow != null) {
                        grpActiomRow.set("ACTION_STATUS", (Object)6);
                        grpActiomRow.set("ACTION_REMARKS", (Object)"mdm.bulkaction.group.manual_suspend");
                        grpActionDO.updateRow(grpActiomRow);
                    }
                }
                MDMUtil.getPersistence().update(grpActionDO);
            }
        }
        catch (final Exception ex) {
            DeviceInvCommandHandler.logger.log(Level.WARNING, "Exception occurred in updateGroupActionManualSuspendRemarks() method : {0}", ex);
        }
    }
    
    public void updateGroupActionSuspendRemarks(final HashMap info, final String commandName, final Long action_id) {
        try {
            if (info.containsKey("isGroupAction")) {
                final Long group_id = Long.valueOf(String.valueOf(info.get("group_id")));
                final int action_type = InvActionUtil.getEquivalentActionType(commandName);
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
                final Criteria grpIdCriteria = new Criteria(Column.getColumn("GroupActionHistory", "GROUP_ID"), (Object)group_id, 0);
                final Criteria actionTypeCriteria = new Criteria(Column.getColumn("GroupActionHistory", "ACTION_ID"), (Object)action_type, 0);
                final Criteria actionIdCriteria = new Criteria(Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID"), (Object)action_id, 1);
                final Criteria actionStatusCriteria = new Criteria(Column.getColumn("GroupActionHistory", "ACTION_STATUS"), (Object)new Object[] { 4, 1 }, 8);
                sQuery.setCriteria(actionTypeCriteria.and(actionIdCriteria).and(actionStatusCriteria).and(grpIdCriteria));
                sQuery.addSelectColumn(Column.getColumn("GroupActionHistory", "*"));
                final DataObject grpActionDO = MDMUtil.getPersistence().get(sQuery);
                if (!grpActionDO.isEmpty()) {
                    final Iterator<Row> iterator = grpActionDO.getRows("GroupActionHistory");
                    while (iterator.hasNext()) {
                        final Row grpActiomRow = iterator.next();
                        if (grpActiomRow != null) {
                            grpActiomRow.set("ACTION_STATUS", (Object)6);
                            grpActiomRow.set("ACTION_REMARKS", (Object)"mdm.bulkaction.group.same_action_suspend");
                            grpActionDO.updateRow(grpActiomRow);
                        }
                    }
                    MDMUtil.getPersistence().update(grpActionDO);
                }
            }
        }
        catch (final Exception ex) {
            DeviceInvCommandHandler.logger.log(Level.WARNING, "Exception occurred in updateGroupActionSuspendRemarks() method : {0}", ex);
        }
    }
    
    public String getScheduledActionNameForCommandName(final String commandName) {
        switch (commandName) {
            case "RestartDevice": {
                return "scheduled_restart";
            }
            case "ShutDownDevice": {
                return "scheduled_shutdown";
            }
            default: {
                return null;
            }
        }
    }
    
    public String getScheduledActionNameForActionID(final int actionID) {
        switch (actionID) {
            case 0: {
                return "scheduled_shutdown";
            }
            case 1: {
                return "scheduled_restart";
            }
            default: {
                return null;
            }
        }
    }
    
    private void modifyScheduledAction(final List resList, final String commandName, final HashMap info, final JSONObject requestJSON) throws Exception {
        DeviceInvCommandHandler.logger.log(Level.INFO, "Modifying scheduled action for resources{0} for the action {1} with params:{2},{3}", new Object[] { resList, commandName, info, requestJSON });
        try {
            final int executionType = requestJSON.optInt("execution_type", -1);
            final Long userID = Long.valueOf(info.get("user_id").toString());
            final String userName = requestJSON.optString("user_name");
            final Long customerID = requestJSON.optLong("customer_id");
            final ArrayList commandList = new ArrayList();
            Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandName);
            if (commandId == null) {
                commandId = DeviceCommandRepository.getInstance().addCommand(commandName);
            }
            final Long collectionId = requestJSON.optLong("collection_id");
            final Long groupActionId = requestJSON.optLong("group_action_id");
            commandList.add(commandId);
            final JSONObject requestParams = new JSONObject();
            requestParams.put("resource_ids", (Collection)resList);
            requestParams.put("command_ids", (Collection)commandList);
            requestParams.put("command_repository_type", 1);
            requestParams.put("expiry", requestJSON.optLong("expiry"));
            requestParams.put("schedule_params", (Object)requestJSON.optJSONObject("schedule_params"));
            requestParams.put("time_zone", (Object)requestJSON.optString("time_zone"));
            requestParams.put("collection_id", (Object)collectionId);
            JSONObject response = new JSONObject();
            final List nonDeletableresourceIDs = GroupActionScheduleUtils.getNonDeletableResourceList(groupActionId, collectionId);
            final List deletableResourceList = new ArrayList();
            deletableResourceList.addAll(resList);
            deletableResourceList.removeAll(nonDeletableresourceIDs);
            if (executionType == 1) {
                requestParams.put("schedule_once_time", requestJSON.get("schedule_once_time"));
                this.scheduleCommandService.deleteScheduledCommand(collectionId, deletableResourceList, customerID, userID);
                response = this.scheduleCommandService.scheduleCommandOnce(requestParams.toMap(), userID, customerID);
            }
            else if (executionType == 2) {
                this.scheduleCommandService.deleteScheduledCommandOnce(collectionId, deletableResourceList, customerID, userID);
                response = this.scheduleCommandService.scheduleCommandsToResources(requestParams.toMap(), customerID, userName, userID);
            }
            final JSONArray collectionArray = response.getJSONArray("scheduled_command_ids");
            for (final Object collectionID : collectionArray.toList()) {
                if (requestJSON.has("group_action_id")) {
                    final Long groupActionID = requestJSON.optLong("group_action_id");
                    GroupActionToCollectionHandler.getInstance().addOrUpdateCollectionForGroupAction(groupActionID, (long)collectionID);
                }
                else {
                    if (!requestJSON.has("device_action_id")) {
                        continue;
                    }
                    final Long deviceActionID = requestJSON.optLong("device_action_id");
                    DeviceActionToCollectionHandler.getInstance().updateCollectionForDeviceAction(deviceActionID, (long)collectionID);
                }
            }
        }
        catch (final Exception e) {
            DeviceInvCommandHandler.logger.log(Level.SEVERE, "Exception in modifyScheduledAction", e);
            throw e;
        }
    }
    
    public void createScheduledAction(final List<Long> resList, final String commandName, final HashMap info, final Long actionId, final JSONObject requestJSON) throws Exception {
        DeviceInvCommandHandler.logger.log(Level.INFO, "Calling the workflow for the scheduled commands with the above params");
        try {
            final ArrayList commandList = new ArrayList();
            final Long commandId = DeviceCommandRepository.getInstance().addCommand(commandName);
            JSONObject response = new JSONObject();
            commandList.add(commandId);
            final Long userID = Long.valueOf(info.get("user_id").toString());
            final String userName = requestJSON.optString("user_name");
            final Long customerID = requestJSON.optLong("customer_id");
            final JSONObject requestParams = new JSONObject();
            requestParams.put("resource_ids", (Collection)resList);
            requestParams.put("command_ids", (Collection)commandList);
            requestParams.put("command_repository_type", 1);
            requestParams.put("expiry", requestJSON.optLong("expiry"));
            requestParams.put("schedule_params", (Object)requestJSON.optJSONObject("schedule_params"));
            requestParams.put("time_zone", (Object)requestJSON.optString("time_zone"));
            requestParams.put("collection_id", 20L);
            final int executionType = requestJSON.optInt("execution_type", -1);
            final long scheduleOnceExecutionTime = requestJSON.optLong("schedule_once_time", -1L);
            info.put("scheduled", this.getScheduledActionNameForCommandName(commandName));
            if (!requestJSON.has("is_empty")) {
                this.addOrUpdateGroupActionsCommandHistory(resList, commandId, userID, commandName, actionId, info);
            }
            if (executionType == 1) {
                requestParams.put("schedule_once_time", scheduleOnceExecutionTime);
                response = this.scheduleCommandService.scheduleCommandOnce(requestParams.toMap(), userID, customerID);
            }
            else if (executionType == 2) {
                response = this.scheduleCommandService.scheduleCommandsToResources(requestParams.toMap(), customerID, userName, userID);
            }
            final JSONArray collectionArray = response.getJSONArray("scheduled_command_ids");
            if (response == null || collectionArray == null || collectionArray.length() == 0) {
                final DeleteQuery dq = (DeleteQuery)new DeleteQueryImpl("GroupActionHistory");
                final Criteria groupActionIDCriteria = new Criteria(new Column("GroupActionHistory", "GROUP_ACTION_ID"), (Object)actionId, 0);
                dq.setCriteria(groupActionIDCriteria);
                MDMUtil.getPersistence().delete(dq);
                final Exception e = new NullPointerException("Error in scheduling commands for the groupAction");
                DeviceInvCommandHandler.logger.log(Level.SEVERE, "Error in scheduling commands for the groupAction", e);
                throw e;
            }
            for (final Object collectionID : collectionArray.toList()) {
                final Boolean containsDuplicate = GroupActionScheduleUtils.checkIfCollectionIsUsedInGroup((Long)collectionID, actionId);
                if (containsDuplicate) {
                    final DeleteQuery dq2 = (DeleteQuery)new DeleteQueryImpl("GroupActionHistory");
                    final Criteria groupActionIDCriteria2 = new Criteria(new Column("GroupActionHistory", "GROUP_ACTION_ID"), (Object)actionId, 0);
                    dq2.setCriteria(groupActionIDCriteria2);
                    MDMUtil.getPersistence().delete(dq2);
                    throw new SyMException(500, (Throwable)null);
                }
                GroupActionToCollectionHandler.getInstance().addOrUpdateCollectionForGroupAction(actionId, (Long)collectionID);
            }
        }
        catch (final Exception e2) {
            DeviceInvCommandHandler.logger.log(Level.SEVERE, "Exception in createScheduledAction", e2);
            throw e2;
        }
    }
    
    public void invokeScheduledBulkCommand(final List<Long> resList, final String commandName, final int platform, final HashMap info, final Long actionId, final JSONObject requestJSON) throws SyMException {
        try {
            DeviceInvCommandHandler.actionLogger.log(Level.INFO, "List of resources:{0} commandName:{1} platform:{2} info:{3} actionID:{4} json:{5}", new Object[] { resList, commandName, platform, info, actionId, requestJSON });
            if (requestJSON.has("group_action_id") || requestJSON.has("device_action_id")) {
                this.modifyScheduledAction(resList, commandName, info, requestJSON);
            }
            else {
                this.createScheduledAction(resList, commandName, info, actionId, requestJSON);
            }
        }
        catch (final Exception ex) {
            DeviceInvCommandHandler.actionLogger.log(Level.SEVERE, "Exception in invokeScheduledBulkCommand", ex);
            if (ex instanceof SyMException && ((SyMException)ex).getErrorCode() == 501) {
                throw new APIHTTPException("CMD0001", new Object[0]);
            }
            throw new SyMException(500, (Throwable)null);
        }
        catch (final Throwable t) {
            DeviceInvCommandHandler.actionLogger.log(Level.SEVERE, "Throwable in invokeScheduledBulkCommand", t.getCause());
            throw t;
        }
    }
    
    public void invokeBulkCommand(final List<Long> resList, final String commandName, final int platform, final HashMap info, final Long actionId) throws SyMException {
        try {
            this.suspendCommandForDeviceList(resList, commandName);
            this.updateGroupActionSuspendRemarks(info, commandName, actionId);
            Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandName);
            if (commandId == null) {
                commandId = DeviceCommandRepository.getInstance().addCommand(commandName);
            }
            final Long user_id = Long.valueOf(info.get("user_id").toString());
            DeviceCommandRepository.getInstance().addSecurityCommand(resList, commandName, 1);
            NotificationHandler.getInstance().SendNotification(resList, platform);
            this.addOrUpdateBulkActionsDetails(resList, commandId, user_id, commandName, actionId, info);
        }
        catch (final Exception ex) {
            Logger.getLogger(DeviceInvCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
            if (ex instanceof SyMException && ((SyMException)ex).getErrorCode() == 501) {
                throw new APIHTTPException("CMD0001", new Object[0]);
            }
            throw new SyMException(500, (Throwable)null);
        }
    }
    
    public void invokeCommand(final List<Long> resList, final String command, final HashMap info) throws SyMException {
        this.invokeCommand(resList, command, Boolean.FALSE, info);
    }
    
    public void invokeCommand(final List<Long> resList, final String command, final Boolean validatePlatform, final HashMap info) throws SyMException {
        try {
            DeviceInvCommandHandler.logger.log(Level.INFO, "Command Invocation Request for Invoke command : {0}, For devices : {1}", new Object[] { command, resList.toString() });
            final String commandDisplayName = CommandUtil.getInstance().getCommandDisplayName(command);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            sQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            sQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            sQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID", "RESOURCE.RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_TYPE"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_VERSION"));
            sQuery.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_VERSION_CODE"));
            sQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
            sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
            sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_MULTIUSER"));
            sQuery.setCriteria(new Criteria(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"), (Object)resList.toArray(new Long[0]), 8));
            final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
            if (!dobj.isEmpty()) {
                final Iterator<Row> iterator = dobj.getRows("Resource");
                while (iterator.hasNext()) {
                    final Row resRow = iterator.next();
                    final DeviceDetails device = new DeviceDetails();
                    device.customerId = (long)resRow.get("CUSTOMER_ID");
                    final Row deviceRow = dobj.getRow("ManagedDevice");
                    device.udid = (String)deviceRow.get("UDID");
                    device.resourceId = (long)deviceRow.get("RESOURCE_ID");
                    device.platform = (int)deviceRow.get("PLATFORM_TYPE");
                    device.agentType = (int)deviceRow.get("AGENT_TYPE");
                    final Row deviceExtnRow = dobj.getRow("ManagedDeviceExtn");
                    device.name = (String)deviceExtnRow.get("NAME");
                    device.agentVersion = (String)deviceRow.get("AGENT_VERSION");
                    device.agentVersionCode = (long)deviceRow.get("AGENT_VERSION_CODE");
                    final Row modelInfoRow = dobj.getRow("MdModelInfo");
                    device.modelType = (int)modelInfoRow.get("MODEL_TYPE");
                    final Row mdDeviceInfoRow = dobj.getRow("MdDeviceInfo");
                    device.osVersion = (String)mdDeviceInfoRow.get("OS_VERSION");
                    device.isMultiUser = (boolean)mdDeviceInfoRow.get("IS_MULTIUSER");
                    final String sEventLogRemarks = "dc.mdm.actionlog.securitycommands.initiate";
                    final Object remarksArgs = commandDisplayName + "@@@" + device.name;
                    if (validatePlatform) {
                        this.validatePlatform(device, command);
                    }
                    Long userId = null;
                    if (info.containsKey("technicianID")) {
                        userId = info.get("technicianID");
                    }
                    else {
                        userId = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
                    }
                    this.sendCommandToDevice(device, command, userId);
                    if (command.equalsIgnoreCase("CorporateWipe") || command.equalsIgnoreCase("EraseDevice")) {
                        MEMDMTrackParamManager.getInstance().incrementTrackValue(CustomerInfoUtil.getInstance().getCustomerId(), "Deprovision_Module", "Inventory_Wipe_Count");
                    }
                    if (info.isEmpty() || info.get("isSilentCommand").equals("false")) {
                        MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, device.resourceId, DMUserHandler.getDCUser(ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID()), sEventLogRemarks, remarksArgs, CustomerInfoUtil.getInstance().getCustomerId());
                    }
                }
            }
            final long SCH_COMMAND_TIME = 120000L;
            MDMUtil.getInstance().scheduleMDMCommand(resList, "CheckCommandTask", MDMUtil.getCurrentTimeInMillis() + SCH_COMMAND_TIME, null);
        }
        catch (final Exception ex) {
            Logger.getLogger(DeviceInvCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
            if (ex instanceof SyMException && ((SyMException)ex).getErrorCode() == 501) {
                throw new APIHTTPException("CMD0001", new Object[0]);
            }
            throw new SyMException(500, (Throwable)null);
        }
    }
    
    public boolean suspendCommandExceution(final List<Long> resList, final String commandName) {
        boolean status = false;
        try {
            final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandName);
            for (final Object res : resList) {
                final JSONObject statusJSON = new CommandStatusHandler().getRecentCommandInfo((Long)res, commandId);
                final int commandStatus = statusJSON.getInt("COMMAND_STATUS");
                if (commandStatus != 2) {
                    statusJSON.put("COMMAND_STATUS", 2);
                    statusJSON.put("REMARKS", (Object)"dc.mdm.general.command.invocation.failed");
                    new CommandStatusHandler().populateCommandStatus(statusJSON);
                    status = true;
                }
            }
            MDMUtil.getInstance().removeDeviceCommandFromCache(resList, commandName);
        }
        catch (final JSONException e) {
            DeviceInvCommandHandler.logger.log(Level.SEVERE, "Exception suspendCommandExceution() -- , ", (Throwable)e);
        }
        return status;
    }
    
    public void suspendBulkCommandExecution(final Long deviceActionId, final Long groupActionId, final String commandName, final Long customerID, final Map info) {
        try {
            SelectQuery sQuery = null;
            final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandName);
            Long groupId = -1L;
            if (deviceActionId != -1L) {
                DeviceInvCommandHandler.logger.log(Level.INFO, "Suspending bulk actions for the deviceActionID{0} for the command{1}", new Object[] { deviceActionId, commandName });
                sQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceActionHistory"));
                sQuery.addJoin(new Join("DeviceActionHistory", "CommandHistory", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2));
                sQuery.addSelectColumn(Column.getColumn("DeviceActionHistory", "COMMAND_HISTORY_ID"));
                sQuery.setCriteria(new Criteria(Column.getColumn("DeviceActionHistory", "DEVICE_ACTION_ID"), (Object)deviceActionId, 0));
            }
            if (groupActionId != -1L) {
                DeviceInvCommandHandler.logger.log(Level.INFO, "Suspending bulk actions for the groupActionID{0} for the command{1}", new Object[] { groupActionId, commandName });
                sQuery = (SelectQuery)new SelectQueryImpl(new Table("GroupActionHistory"));
                sQuery.addJoin(new Join("GroupActionHistory", "GroupActionToCommand", new String[] { "GROUP_ACTION_ID" }, new String[] { "GROUP_ACTION_ID" }, 2));
                sQuery.addJoin(new Join("GroupActionToCommand", "CommandHistory", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 2));
                sQuery.addSelectColumn(Column.getColumn("CommandHistory", "COMMAND_HISTORY_ID"));
                sQuery.setCriteria(new Criteria(Column.getColumn("GroupActionHistory", "GROUP_ACTION_ID"), (Object)groupActionId, 0));
                sQuery.addSelectColumn(Column.getColumn("GroupActionHistory", "*"));
            }
            sQuery.addJoin(new Join("CommandHistory", "CommandError", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 1));
            sQuery.addJoin(new Join("CommandHistory", "CommandAuditRel", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 1));
            sQuery.addJoin(new Join("CommandAuditRel", "AuditInfo", new String[] { "AUDIT_ID" }, new String[] { "AUDIT_ID" }, 1));
            sQuery.addSelectColumn(Column.getColumn("CommandError", "*"));
            sQuery.addSelectColumn(Column.getColumn("AuditInfo", "*"));
            sQuery.addSelectColumn(Column.getColumn("CommandAuditRel", "*"));
            sQuery.addSelectColumn(Column.getColumn("CommandHistory", "*"));
            final Criteria commandCrit = new Criteria(Column.getColumn("CommandHistory", "COMMAND_ID"), (Object)commandId, 0);
            final Criteria commandStatusCrit = new Criteria(Column.getColumn("CommandHistory", "COMMAND_STATUS"), (Object)new Object[] { 1, 4 }, 8);
            sQuery.getCriteria().and(commandCrit.and(commandStatusCrit));
            final DataObject commandDO = MDMUtil.getPersistence().get(sQuery);
            if (!commandDO.isEmpty()) {
                groupId = (Long)commandDO.getValue("GroupActionHistory", "GROUP_ID", (Criteria)null);
            }
            final JSONObject commandStatusJSON = new CommandStatusHandler().getRecentCommandBulkInfo(commandDO);
            final Iterator<String> resourceIterator = commandStatusJSON.keys();
            ArrayList<Long> resList = new ArrayList<Long>();
            final long time = System.currentTimeMillis();
            while (resourceIterator.hasNext()) {
                final JSONArray commandDetailsArray = new JSONArray();
                final String resID = resourceIterator.next();
                resList.add(Long.valueOf(resID));
                final JSONObject commandInfo = commandStatusJSON.getJSONArray(resID).getJSONObject(0);
                if (commandInfo.getInt("COMMAND_STATUS") != 2 || (commandInfo.getInt("COMMAND_STATUS") == 2 && !info.isEmpty())) {
                    commandInfo.put("COMMAND_STATUS", 6);
                    commandInfo.put("REMARKS", (Object)"mdm.bulkaction.device.manual_suspend");
                    commandInfo.put("UPDATED_TIME", time);
                    commandDetailsArray.put((Object)commandInfo);
                    commandStatusJSON.put(resID, (Object)commandDetailsArray);
                }
            }
            final HashMap criteriaList = new HashMap();
            final ArrayList<Long> cmdList = new ArrayList<Long>();
            cmdList.add(commandId);
            criteriaList.put("COMMAND_ID", cmdList);
            new CommandStatusHandler().populateCommandStatusForDevices(commandStatusJSON, criteriaList);
            final List<Long> commandIDList = new ArrayList<Long>();
            commandIDList.add(commandId);
            if (!info.isEmpty()) {
                final Long collectionID = Long.parseLong(info.get("collection_id").toString());
                final List resourceIDs = GroupActionScheduleUtils.getNonDeletableResourceList(groupActionId, collectionID);
                final Long userID = Long.parseLong(info.get("user_id").toString());
                resList.removeAll(resourceIDs);
                final Integer scheduleType = ScheduledActionsUtils.getScheduleExecutionTypeForCollection(collectionID);
                if (groupId == -1L) {
                    groupId = GroupActionScheduleUtils.getGroupIDForGroupActionID(groupActionId);
                }
                if (scheduleType == 2) {
                    DeviceInvCommandHandler.logger.log(Level.INFO, "Calling delete workflow of schedule Type once for the collectionID", new Object[] { collectionID });
                    this.scheduleCommandService.deleteScheduledCommandOnce(collectionID, resList, customerID, userID);
                }
                else if (scheduleType == 1) {
                    DeviceInvCommandHandler.logger.log(Level.INFO, "Calling delete workflow of schedule Type repeat for the collectionID", new Object[] { collectionID });
                    this.scheduleCommandService.deleteScheduledCommand(collectionID, resList, customerID, userID);
                    ScheduledActionsUtils.disableIfScheduleIsUnused(collectionID);
                }
            }
            else {
                DeviceCommandRepository.getInstance().clearDeviceCommand(resList, commandIDList);
            }
            if (commandName.equals("ClearAppData")) {
                ClearAppDataHandler.getInstance().deleteAppGroupIdsInBulk(resList, Collections.emptyList());
            }
            if (groupId != null && groupId != -1L) {
                resList = new ArrayList<Long>();
                resList.add(groupId);
            }
            String remarksText = "mdm.bulkactions.auditlog.suspend.device";
            if (groupId != null && groupId != -1L) {
                remarksText = "mdm.bulkactions.auditlog.suspend.group";
            }
            if (commandName.equalsIgnoreCase("ClearAppData")) {
                remarksText = "mdm.bulkactions.auditlog.suspend.resetapps.device";
                if (groupId != null && groupId != -1L) {
                    remarksText = "mdm.bulkactions.auditlog.suspend.resetapps.group";
                }
            }
            final HashMap resourseVsName = MDMResourceDataProvider.getResourceNames(resList);
            final List<Object> remarksArgsList = new ArrayList<Object>();
            for (Object remarksArgs : resourseVsName.keySet()) {
                if (commandName.equalsIgnoreCase("ClearAppData")) {
                    remarksArgs = resourseVsName.get(remarksArgs);
                }
                else {
                    String remarksCommandText;
                    if (commandName == "RestartDevice") {
                        remarksCommandText = "Remote Restart";
                    }
                    else if (commandName == "RestartDevice") {
                        remarksCommandText = "Remote Shutdown";
                    }
                    else {
                        remarksCommandText = commandName;
                    }
                    remarksArgs = remarksCommandText + "@@@" + resourseVsName.get(remarksArgs);
                }
                remarksArgsList.add(remarksArgs);
            }
            MDMEventLogHandler.getInstance().addEvent(2051, info.get("user_name"), remarksText, remarksArgsList, customerID, new Long(System.currentTimeMillis()));
        }
        catch (final Exception e) {
            DeviceInvCommandHandler.logger.log(Level.SEVERE, "Exception suspendBulkCommandExecution() -- , ", e);
        }
    }
    
    public JSONArray getDeviceCommandStatus(final List<Long> resList, final String commandName) throws Exception {
        final JSONArray deviceStatus = new JSONArray();
        for (final Long resourceID : resList) {
            final Long lastInitTime = (Long)DBUtil.getMaxOfValue("CommandHistory", "ADDED_TIME", (Criteria)null);
            final Long commandID = DeviceCommandRepository.getInstance().getCommandID(commandName);
            final Criteria lastInitTimeCri = new Criteria(Column.getColumn("CommandHistory", "ADDED_TIME"), (Object)lastInitTime, 0);
            final Criteria commandIDCriteria = new Criteria(Column.getColumn("CommandHistory", "COMMAND_ID"), (Object)commandID, 0);
            final HashMap deviceScanInfoHash = CommandUtil.getInstance().getCommandStatus(resourceID, lastInitTimeCri.and(commandIDCriteria));
            final JSONObject deviceJSON = new JSONObject();
            deviceJSON.put("id", (Object)resourceID);
            deviceJSON.put("status", (Object)(deviceScanInfoHash.isEmpty() ? 3 : deviceScanInfoHash.get("COMMAND_STATUS_INT")));
            deviceStatus.put((Object)deviceJSON);
        }
        return deviceStatus;
    }
    
    public JSONArray getDeviceScanStatus(final List<Long> resList) throws Exception {
        final JSONArray deviceStatus = new JSONArray();
        for (final Long resourceID : resList) {
            final Criteria criteria = new Criteria(Column.getColumn("MdDeviceScanStatus", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject dobj = MDMUtil.getPersistence().get("MdDeviceScanStatus", criteria);
            final Integer scanStatus = (Integer)dobj.getFirstValue("MdDeviceScanStatus", "SCAN_STATUS");
            final JSONObject deviceJSON = new JSONObject();
            deviceJSON.put("id", (Object)resourceID);
            deviceJSON.put("status", (scanStatus == null) ? 3 : ((int)scanStatus));
            deviceStatus.put((Object)deviceJSON);
        }
        return deviceStatus;
    }
    
    public void validatePlatform(final DeviceDetails device, final String command) throws SyMException {
        boolean validationFailed = false;
        if (command.equalsIgnoreCase("DeviceLock") || command.equalsIgnoreCase("DeviceRing") || command.equalsIgnoreCase("ResetPasscode")) {
            if (command.equalsIgnoreCase("DeviceRing") && device.platform == 1 && !new LostModeDataHandler().isLostMode(device.resourceId)) {
                throw new APIHTTPException("CMD0003", new Object[0]);
            }
            if (device.platform == 3 && device.modelType != 1) {
                validationFailed = true;
            }
            if (command.equalsIgnoreCase("ResetPasscode") && device.platform == 1) {
                validationFailed = true;
            }
        }
        else if (command.startsWith("RestartDevice")) {
            if (device.platform == 3) {
                final Boolean isWin10OrAbove = ManagedDeviceHandler.getInstance().isOsVersionGreaterThanForResource(device.resourceId, 10.0f);
                if (!isWin10OrAbove) {
                    validationFailed = true;
                }
            }
            else if (device.platform == 2) {
                validationFailed = true;
            }
        }
        else if (command.equalsIgnoreCase("RemoteSession") && device.platform != 2) {
            validationFailed = true;
        }
        else if (command.equalsIgnoreCase("ClearPasscode") && device.platform == 3) {
            validationFailed = true;
        }
        else if (command.equalsIgnoreCase("EnableLostMode") && device.platform == 3) {
            validationFailed = true;
        }
        else if (command.equalsIgnoreCase("DisableLostMode") && device.platform == 3) {
            validationFailed = true;
        }
        else if (command.equalsIgnoreCase("ShutDownDevice") && device.platform == 2) {
            validationFailed = true;
        }
        else if (command.equalsIgnoreCase("UnlockUserAccount") && (device.platform != 1 || (device.modelType != 4 && device.modelType != 3))) {
            validationFailed = true;
        }
        if (validationFailed) {
            throw new SyMException(501, "Command is not supported for the platform", (Throwable)null);
        }
    }
    
    public void addToQueue(final HashMap dataToQueue) {
        try {
            final DCQueue queue = DCQueueHandler.getQueue("bulk-actions-processor");
            final DCQueueData queueData = new DCQueueData();
            queueData.postTime = System.currentTimeMillis();
            queueData.queueData = dataToQueue;
            queue.addToQueue(queueData);
        }
        catch (final Exception e) {
            DeviceInvCommandHandler.logger.log(Level.SEVERE, "Exception in adding to bulk-actions-processor queue", e);
        }
    }
    
    public void addOrUpdateBulkActionsDetails(final List<Long> resourceList, final Long commandID, final Long userId, final String commandName, final Long actionId, final Map info) {
        try {
            String reasonMsg = "--";
            boolean isGroupAction = Boolean.FALSE;
            if (info.containsKey("is_group_action")) {
                isGroupAction = info.get("is_group_action");
            }
            if (info.containsKey("reason_message")) {
                reasonMsg = info.get("reason_message").toString();
            }
            final String remarks = getInstance().getRemarksString(commandName);
            final JSONObject commandJSON = new CommandStatusHandler().populateBulkCommandStatusForDevices(resourceList, commandID, userId, remarks);
            if (isGroupAction) {
                MDMGroupHandler.getInstance().populateGroupActionDetails(actionId, resourceList.size(), commandJSON, commandName, info);
            }
            this.populateDeviceActionDetails(commandName, reasonMsg, commandJSON);
        }
        catch (final JSONException e) {
            DeviceInvCommandHandler.logger.log(Level.SEVERE, " Exception occurred in  addOrUpdateCommandInitiatedCommandHistory()", (Throwable)e);
        }
    }
    
    public void sendCorporateWipeCommandToDevice(final DeviceEvent device, final String commandName, final Long userId) throws Exception {
        final List resourceList = new ArrayList();
        resourceList.add(device.resourceID);
        final Long commandId = DeviceCommandRepository.getInstance().getCommandID(commandName);
        if (commandName.equalsIgnoreCase("CorporateWipe")) {
            if (device.platformType == 1) {
                ProfileAssociateHandler.getInstance().removeAppsForResource(device.resourceID);
                if (this.isNonInventoryCommand(commandName)) {
                    DeviceCommandRepository.getInstance().assignCommandToDevices(commandId, resourceList, 1);
                }
                else {
                    DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceID, commandName);
                }
                NotificationHandler.getInstance().SendNotification(resourceList, device.platformType);
            }
            else if (device.platformType == 3) {
                DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceID, commandName);
                NotificationHandler.getInstance().SendNotification(resourceList, device.platformType);
                DeviceCommandRepository.getInstance().addCorporateWipeCommand(device.udid);
                NotificationHandler.getInstance().SendNotification(resourceList, 303);
            }
            else if (device.platformType == 2) {
                DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceID, commandName);
                NotificationHandler.getInstance().SendNotification(resourceList, 2);
                if (ManagedDeviceHandler.getInstance().isPersonalProfileManaged(device.resourceID)) {
                    DeviceCommandRepository.getInstance().addSecurityCommand(device.resourceID, commandName, 2);
                    NotificationHandler.getInstance().SendNotification(resourceList, 201);
                }
            }
        }
        this.addOrUpdateCommandInitiatedCommandHistory(device.resourceID, commandId, userId, commandName);
    }
    
    static {
        DeviceInvCommandHandler.deviceHandler = null;
        DeviceInvCommandHandler.logger = Logger.getLogger("MDMLogger");
        DeviceInvCommandHandler.actionLogger = Logger.getLogger("ActionsLogger");
    }
}
