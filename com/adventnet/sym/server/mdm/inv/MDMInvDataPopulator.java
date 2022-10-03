package com.adventnet.sym.server.mdm.inv;

import java.util.Hashtable;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import java.text.ParseException;
import java.util.TimeZone;
import com.me.mdm.server.settings.location.MDMGeoLocationHandler;
import com.dd.plist.NSObject;
import java.util.Collection;
import com.me.mdm.server.command.CommandStatusHandler;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.Properties;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.me.mdm.server.settings.MdComplianceRulesHandler;
import com.me.mdm.server.settings.battery.MdDeviceBatteryDetailsDBHandler;
import com.me.mdm.server.adep.DeviceConfiguredCommandHandler;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import com.me.mdm.server.inv.InventoryCertificateDataHandler;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import com.dd.plist.NSData;
import org.json.JSONArray;
import com.dd.plist.NSString;
import com.dd.plist.NSDictionary;
import java.util.List;
import com.me.mdm.server.apps.blacklist.BlacklistQueryUtils;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Iterator;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccess;
import com.dd.plist.NSArray;
import com.me.mdm.server.security.mac.MacFirmwareUtil;
import com.me.mdm.server.profiles.mac.MDMFilevaultPersonalRecoveryKeyImport;
import com.adventnet.sym.server.mdm.encryption.ios.filevault.MDMFileVaultRecoveryKeyHander;
import java.util.Map;
import com.me.mdm.server.enrollment.ios.MacBootstrapTokenHandler;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.easmanagement.EASMgmt;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.logging.Logger;

public class MDMInvDataPopulator
{
    public static Logger logger;
    private static MDMInvDataPopulator mdmInvDataPopulator;
    private static final HashMap<String, String> IOS_RESTRICTION_HASH;
    private static final HashMap<String, Integer> IOS_RESTRICTION_DEFAULT_VALUE;
    
    public static MDMInvDataPopulator getInstance() {
        if (MDMInvDataPopulator.mdmInvDataPopulator == null) {
            MDMInvDataPopulator.mdmInvDataPopulator = new MDMInvDataPopulator();
        }
        return MDMInvDataPopulator.mdmInvDataPopulator;
    }
    
    public void updateDeviceInfo(final Long resourceId, final String columnName, final String columnValue) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside addOrUpdateDeviceInfo() ->  resourceID: {0} , columnName: {1} , columnValue: {2}", new Object[] { resourceId, columnName, columnValue });
        if (!MDMUtil.isStringEmpty(columnName) && !MDMUtil.isStringEmpty(columnValue)) {
            final DataObject deviceInfoDO = this.getDeviceInfoDO(resourceId);
            if (!deviceInfoDO.isEmpty()) {
                final Row deviceInfoRow = deviceInfoDO.getFirstRow("MdDeviceInfo");
                deviceInfoRow.set(columnName, (Object)columnValue);
                deviceInfoDO.updateRow(deviceInfoRow);
                MDMUtil.getPersistence().update(deviceInfoDO);
            }
        }
    }
    
    public void addOrUpdateDeviceInfo(final Long resourceID, final HashMap hashDeviceInfo) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside addOrUpdateDeviceInfo() ->  resourceID {0}", resourceID);
        if (hashDeviceInfo != null) {
            final DataObject deviceInfoDO = this.getDeviceInfoDO(resourceID);
            Row deviceInfoRow = null;
            if (deviceInfoDO.isEmpty()) {
                MDMInvDataPopulator.logger.log(Level.INFO, "addOrUpdateDeviceInfo() -> deviceInfoDO is Empty for resourceID {0}", resourceID);
                deviceInfoRow = new Row("MdDeviceInfo");
                deviceInfoRow = this.getDeviceInfoRow(resourceID, hashDeviceInfo, deviceInfoRow);
                deviceInfoDO.addRow(deviceInfoRow);
                MDMUtil.getPersistence().add(deviceInfoDO);
            }
            else {
                MDMInvDataPopulator.logger.log(Level.INFO, "addOrUpdateDeviceInfo() -> deviceInfoDO is not empty for resourceID {0}", resourceID);
                deviceInfoRow = deviceInfoDO.getFirstRow("MdDeviceInfo");
                deviceInfoRow = this.getDeviceInfoRow(resourceID, hashDeviceInfo, deviceInfoRow);
                deviceInfoDO.updateRow(deviceInfoRow);
                MDMUtil.getPersistence().update(deviceInfoDO);
            }
        }
    }
    
    public void updateDeviceName(final Long resourceID, final HashMap hashDeviceInfo, final int platform) {
        try {
            if (hashDeviceInfo != null) {
                String sDeviceName = hashDeviceInfo.get("DeviceName");
                if (sDeviceName != null && sDeviceName.trim().equals("")) {
                    sDeviceName = I18N.getMsg("dc.common.NOT_AVAILABLE", new Object[0]);
                }
                final String deviceNameInDB = ManagedDeviceHandler.getInstance().getDeviceName(resourceID);
                final boolean isCustomDetailsEdited = ManagedDeviceHandler.getInstance().isCustomDetailsModified(resourceID);
                final String sUDIDFromScan = hashDeviceInfo.get("UDID");
                final String sUDIDFromManagedDevice = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceID);
                MDMInvDataPopulator.logger.log(Level.INFO, "device Name: {0} sUDIDFromManagedDevice: {1} sUDIDFromScan: {2}", new Object[] { sDeviceName, sUDIDFromManagedDevice, sUDIDFromScan });
                final DataObject resourceDO = this.getResourceDO(resourceID);
                if (!sDeviceName.equalsIgnoreCase(I18N.getMsg("dc.common.NOT_AVAILABLE", new Object[0])) && !sDeviceName.equalsIgnoreCase(deviceNameInDB) && isCustomDetailsEdited) {
                    final JSONObject deviceObject = new JSONObject();
                    deviceObject.put("NAME", (Object)deviceNameInDB);
                    deviceObject.put("RESOURCE_ID", (Object)resourceID);
                    final DCQueue dcQueue = DCQueueHandler.getQueue("mdm-device-compliance");
                    final DCQueueData dcQueueData = new DCQueueData();
                    final Long postTime = MDMUtil.getCurrentTimeInMillis();
                    dcQueueData.fileName = "166-" + resourceID + "-" + postTime + ".txt";
                    dcQueueData.queueDataType = 166;
                    dcQueueData.queueData = deviceObject.toString();
                    dcQueueData.postTime = postTime;
                    dcQueueData.customerID = CustomerInfoUtil.getInstance().getCustomerId();
                    MDMInvDataPopulator.logger.log(Level.INFO, "Queue data: {0}", dcQueueData.toString());
                    dcQueue.addToQueue(dcQueueData);
                }
                if (!resourceDO.isEmpty() && sUDIDFromScan.equals(sUDIDFromManagedDevice) && (!isCustomDetailsEdited || (platform == 3 && !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotUpdateWinDevName")))) {
                    final Row resourceRow = resourceDO.getFirstRow("Resource");
                    resourceRow.set("RESOURCE_ID", (Object)resourceID);
                    resourceRow.set("NAME", (Object)sDeviceName);
                    resourceDO.updateRow(resourceRow);
                    MDMUtil.getPersistence().update(resourceDO);
                    final org.json.simple.JSONObject singleDeviceDetails = new org.json.simple.JSONObject();
                    singleDeviceDetails.put((Object)"MANAGED_DEVICE_ID", (Object)resourceID);
                    singleDeviceDetails.put((Object)"NAME", (Object)sDeviceName);
                    if (platform == 3) {
                        singleDeviceDetails.put((Object)"IS_MODIFIED", (Object)true);
                    }
                    else {
                        singleDeviceDetails.put((Object)"IS_MODIFIED", (Object)false);
                    }
                    MDMInvDataPopulator.logger.log(Level.FINE, "JSON Object in updateDeviceName:{0}", singleDeviceDetails.toJSONString());
                    MDCustomDetailsRequestHandler.getInstance().addOrUpdateCustomDeviceDetails(singleDeviceDetails);
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private DataObject getDeviceInfoDO(final Long resourceID) throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("MdDeviceInfo"));
        query.addSelectColumn(new Column("MdDeviceInfo", "*"));
        final Criteria criteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceID, 0, false);
        query.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    private DataObject getSharedDeviceInfo(final Long resourceId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdSharedDeviceInfo"));
        selectQuery.addSelectColumn(new Column("MdSharedDeviceInfo", "*"));
        final Criteria criteria = new Criteria(new Column("MdSharedDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0);
        selectQuery.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        return dataObject;
    }
    
    private DataObject getResourceDO(final Long resourceID) throws DataAccessException {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("Resource"));
        query.addSelectColumn(new Column("Resource", "*"));
        final Criteria criteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)resourceID, 0, false);
        query.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    private Row getSharedDeviceInfoRow(final Long resourceId, final HashMap sharedHash, final Row sharedDeviceRow) {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside getSharedDeviceInfoRow() ->  resourceID {0}", resourceId);
        sharedDeviceRow.set("RESOURCE_ID", (Object)resourceId);
        if (sharedHash.get("QUOTA_SIZE") != null) {
            sharedDeviceRow.set("QUOTA_SIZE", (Object)sharedHash.get("QUOTA_SIZE"));
        }
        if (sharedHash.get("ESTIMATED_USER") != null) {
            sharedDeviceRow.set("ESTIMATED_USER", (Object)sharedHash.get("ESTIMATED_USER"));
        }
        if (sharedHash.get("RESIDENT_USER") != null) {
            sharedDeviceRow.set("RESIDENT_USER", (Object)sharedHash.get("ESTIMATED_USER"));
        }
        return sharedDeviceRow;
    }
    
    private Row getDeviceInfoRow(final Long resourceID, final HashMap hashDeviceInfo, final Row deviceInfoRow) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside getDeviceInfoRow() ->  resourceID {0}", resourceID);
        final String sfBatteryLevel = hashDeviceInfo.get("BATTERY_LEVEL");
        float fBatteryLevel = -1.0f;
        if (sfBatteryLevel != null) {
            try {
                fBatteryLevel = Float.valueOf(sfBatteryLevel);
            }
            catch (final Exception ex) {
                fBatteryLevel = -1.0f;
            }
        }
        final Integer platformType = ManagedDeviceHandler.getInstance().getPlatformType(resourceID);
        final String sCellularTechnology = hashDeviceInfo.get("CELLULAR_TECHNOLOGY");
        final String sDeviceCapacity = hashDeviceInfo.get("DEVICE_CAPACITY");
        final String sAvailableDeviceCapacity = hashDeviceInfo.get("AVAILABLE_DEVICE_CAPACITY");
        float fDeviceCapacity = 0.0f;
        float fAvailableDeviceCapacity = 0.0f;
        float fUsedDeviceCapcity = 0.0f;
        if (sDeviceCapacity != null && !sDeviceCapacity.trim().isEmpty()) {
            fDeviceCapacity = Float.valueOf(sDeviceCapacity);
        }
        if (sDeviceCapacity != null && sAvailableDeviceCapacity != null) {
            fAvailableDeviceCapacity = Float.valueOf(sAvailableDeviceCapacity);
            fUsedDeviceCapcity = fDeviceCapacity - fAvailableDeviceCapacity;
        }
        hashDeviceInfo.put("PLATFORM_TYPE", platformType);
        final Long modelId = this.addModelInfo(hashDeviceInfo);
        deviceInfoRow.set("RESOURCE_ID", (Object)resourceID);
        deviceInfoRow.set("OS_VERSION", (Object)hashDeviceInfo.get("OS_VERSION"));
        deviceInfoRow.set("BUILD_VERSION", (Object)hashDeviceInfo.get("BUILD_VERSION"));
        deviceInfoRow.set("SERIAL_NUMBER", (Object)hashDeviceInfo.get("SERIAL_NUMBER"));
        deviceInfoRow.set("MODEL_ID", (Object)modelId);
        deviceInfoRow.set("BATTERY_LEVEL", (Object)fBatteryLevel);
        if (sCellularTechnology != null) {
            deviceInfoRow.set("CELLULAR_TECHNOLOGY", (Object)new Integer(sCellularTechnology));
        }
        String imei = hashDeviceInfo.get("IMEI");
        if (imei == null || MDMStringUtils.isEmpty(imei)) {
            imei = (String)deviceInfoRow.get("IMEI");
        }
        else {
            imei = imei.replace(" ", "");
        }
        deviceInfoRow.set("IMEI", (Object)imei);
        deviceInfoRow.set("MEID", (Object)hashDeviceInfo.get("MEID"));
        deviceInfoRow.set("MODEM_FIRMWARE_VERSION", (Object)hashDeviceInfo.get("MODEM_FIRMWARE_VERSION"));
        deviceInfoRow.set("DEVICE_CAPACITY", (Object)fDeviceCapacity);
        deviceInfoRow.set("AVAILABLE_DEVICE_CAPACITY", (Object)fAvailableDeviceCapacity);
        deviceInfoRow.set("USED_DEVICE_SPACE", (Object)fUsedDeviceCapcity);
        final String externalCapacity = hashDeviceInfo.get("EXTERNAL_CAPACITY");
        final String usedExternalSpace = hashDeviceInfo.get("USED_EXTERNAL_SPACE");
        final String availableExternalCapacity = hashDeviceInfo.get("AVAILABLE_EXTERNAL_CAPACITY");
        final String osName = hashDeviceInfo.get("OS_NAME");
        final String processorSpeed = hashDeviceInfo.get("PROCESSOR_SPEED");
        final String processorType = hashDeviceInfo.get("PROCESSOR_TYPE");
        final String processorArchitecture = hashDeviceInfo.get("PROCESSOR_ARCHITECTURE");
        final String availableRAMMemory = hashDeviceInfo.get("AVAILABLE_RAM_MEMORY");
        final String totalRAMMemory = hashDeviceInfo.get("TOTAL_RAM_MEMORY");
        final String processorName = hashDeviceInfo.get("PROCESSOR_NAME");
        if (externalCapacity != null) {
            deviceInfoRow.set("EXTERNAL_CAPACITY", (Object)Float.valueOf(externalCapacity));
        }
        if (usedExternalSpace != null) {
            deviceInfoRow.set("USED_EXTERNAL_SPACE", (Object)Float.valueOf(usedExternalSpace));
        }
        if (availableExternalCapacity != null) {
            deviceInfoRow.set("AVAILABLE_EXTERNAL_CAPACITY", (Object)Float.valueOf(availableExternalCapacity));
        }
        if (osName != null) {
            deviceInfoRow.set("OS_NAME", (Object)osName);
        }
        deviceInfoRow.set("PROCESSOR_SPEED", (Object)((processorSpeed != null && !processorSpeed.equals("")) ? processorSpeed : "--"));
        deviceInfoRow.set("PROCESSOR_TYPE", (Object)((processorType != null && !processorType.equals("")) ? processorType : "--"));
        deviceInfoRow.set("PROCESSOR_ARCHITECTURE", (Object)((processorArchitecture != null && !processorArchitecture.equals("")) ? processorArchitecture : "--"));
        if (availableRAMMemory != null) {
            deviceInfoRow.set("AVAILABLE_RAM_MEMORY", (Object)Float.valueOf(availableRAMMemory));
        }
        if (totalRAMMemory != null) {
            deviceInfoRow.set("TOTAL_RAM_MEMORY", (Object)Float.valueOf(totalRAMMemory));
        }
        final String isSupervised = hashDeviceInfo.get("IS_SUPERVISED");
        if (isSupervised != null) {
            deviceInfoRow.set("IS_SUPERVISED", (Object)Boolean.parseBoolean(isSupervised));
        }
        final String isMultiUser = hashDeviceInfo.get("IS_MULTIUSER");
        if (isMultiUser != null) {
            deviceInfoRow.set("IS_MULTIUSER", (Object)Boolean.parseBoolean(isMultiUser));
        }
        final String isProfileOwner = hashDeviceInfo.get("IS_PROFILEOWNER");
        if (isProfileOwner != null) {
            deviceInfoRow.set("IS_PROFILEOWNER", (Object)Boolean.parseBoolean(isProfileOwner));
        }
        final String isDeviceLocatorEnabled = hashDeviceInfo.get("IS_DEVICE_LOCATOR_ENABLED");
        if (isDeviceLocatorEnabled != null) {
            deviceInfoRow.set("IS_DEVICE_LOCATOR_ENABLED", (Object)Boolean.parseBoolean(isDeviceLocatorEnabled));
        }
        final String isActiveLockEnabled = hashDeviceInfo.get("IS_ACTIVATION_LOCK_ENABLED");
        if (isActiveLockEnabled != null) {
            deviceInfoRow.set("IS_ACTIVATION_LOCK_ENABLED", (Object)Boolean.parseBoolean(isActiveLockEnabled));
        }
        final String isDNDInEffect = hashDeviceInfo.get("IS_DND_IN_EFFECT");
        if (isDNDInEffect != null) {
            deviceInfoRow.set("IS_DND_IN_EFFECT", (Object)Boolean.parseBoolean(isDNDInEffect));
        }
        final String isItunesAccountActive = hashDeviceInfo.get("IS_ITUNES_ACCOUNT_ACTIVE");
        if (isItunesAccountActive != null) {
            deviceInfoRow.set("IS_ITUNES_ACCOUNT_ACTIVE", (Object)Boolean.parseBoolean(isItunesAccountActive));
        }
        deviceInfoRow.set("EAS_DEVICE_IDENTIFIER", (Object)hashDeviceInfo.get("EAS_DEVICE_IDENTIFIER"));
        final String isIcloudBackupEnabled = hashDeviceInfo.get("IS_CLOUD_BACKUP_ENABLED");
        if (isIcloudBackupEnabled != null) {
            deviceInfoRow.set("IS_CLOUD_BACKUP_ENABLED", (Object)Boolean.parseBoolean(isIcloudBackupEnabled));
        }
        final String lastCloudBackUpDate = hashDeviceInfo.get("LAST_CLOUD_BACKUP_DATE");
        if (lastCloudBackUpDate != null) {
            final SimpleDateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
            final Date lastCloudBackUpDateinGmt = f.parse(lastCloudBackUpDate);
            deviceInfoRow.set("LAST_CLOUD_BACKUP_DATE", (Object)lastCloudBackUpDateinGmt.getTime());
        }
        deviceInfoRow.set("GOOGLE_PLAY_SERVICE_ID", (Object)hashDeviceInfo.get("GOOGLE_PLAY_SERVICE_ID"));
        if (!MDMStringUtils.isEmpty(processorName)) {
            deviceInfoRow.set("PROCESSOR_NAME", (Object)processorName);
        }
        final int coreCount = Integer.parseInt(hashDeviceInfo.getOrDefault("PROCESSOR_CORE_COUNT", "0"));
        deviceInfoRow.set("PROCESSOR_CORE_COUNT", (Object)coreCount);
        return deviceInfoRow;
    }
    
    public void addOrUpdateNetworkInfo(final Long resourceID, final HashMap hashNetworkInfo) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside addOrUpdateNetworkInfo() ->  resourceID {0}", resourceID);
        if (hashNetworkInfo != null) {
            final DataObject networkInfoDO = this.getNetworkInfoDO(resourceID);
            Row networkInfoRow = null;
            if (networkInfoDO.isEmpty()) {
                networkInfoRow = new Row("MdNetworkInfo");
                networkInfoRow = this.getNetworkInfoRow(resourceID, hashNetworkInfo, networkInfoRow);
                networkInfoDO.addRow(networkInfoRow);
                MDMUtil.getPersistence().add(networkInfoDO);
            }
            else {
                networkInfoRow = networkInfoDO.getFirstRow("MdNetworkInfo");
                networkInfoRow = this.getNetworkInfoRow(resourceID, hashNetworkInfo, networkInfoRow);
                networkInfoDO.updateRow(networkInfoRow);
                MDMUtil.getPersistence().update(networkInfoDO);
            }
        }
    }
    
    private DataObject getNetworkInfoDO(final Long resourceID) throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("MdNetworkInfo"));
        query.addSelectColumn(new Column("MdNetworkInfo", "*"));
        final Criteria criteria = new Criteria(new Column("MdNetworkInfo", "RESOURCE_ID"), (Object)resourceID, 0, false);
        query.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    private Row getNetworkInfoRow(final Long resourceID, final HashMap hashNetworkInfo, final Row networkInfoRow) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside getNetworkInfoRow() ->  resourceID {0}", resourceID);
        networkInfoRow.set("RESOURCE_ID", (Object)resourceID);
        networkInfoRow.set("BLUETOOTH_MAC", (Object)((hashNetworkInfo.get("BLUETOOTH_MAC") == null) ? null : hashNetworkInfo.get("BLUETOOTH_MAC")));
        networkInfoRow.set("WIFI_MAC", (Object)hashNetworkInfo.get("WIFI_MAC"));
        networkInfoRow.set("WIFI_IP", (Object)hashNetworkInfo.get("WIFI_IP"));
        networkInfoRow.set("VOICE_ROAMING_ENABLED", (Object)Boolean.parseBoolean(hashNetworkInfo.get("VOICE_ROAMING_ENABLED")));
        networkInfoRow.set("DATA_ROAMING_ENABLED", (Object)Boolean.parseBoolean(hashNetworkInfo.get("DATA_ROAMING_ENABLED")));
        networkInfoRow.set("ETHERNET_MACS", (Object)hashNetworkInfo.get("ETHERNET_MACS"));
        networkInfoRow.set("ETHERNET_IP", (Object)hashNetworkInfo.get("ETHERNET_IP"));
        networkInfoRow.set("WIFI_IP", (Object)hashNetworkInfo.get("WIFI_IP"));
        networkInfoRow.set("IMSI", (Object)hashNetworkInfo.get("IMSI"));
        final String isHotSpotEnabled = hashNetworkInfo.get("IS_PERSONAL_HOTSPOT_ENABLED");
        if (isHotSpotEnabled != null) {
            networkInfoRow.set("IS_PERSONAL_HOTSPOT_ENABLED", (Object)Boolean.parseBoolean(isHotSpotEnabled));
        }
        return networkInfoRow;
    }
    
    public void addOrUpdateNetworkUsageInfo(final Long resourceID, final HashMap hashNetworkUsageInfo) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside addOrUpdateNetworkUsageInfo() ->  resourceID {0}", resourceID);
        if (hashNetworkUsageInfo != null) {
            final DataObject networkUsageInfoDO = this.getNetworkUsageInfoDO(resourceID);
            Row networkUsageInfoRow = null;
            if (networkUsageInfoDO.isEmpty()) {
                networkUsageInfoRow = new Row("MdNetworkUsageInfo");
                networkUsageInfoRow = this.setRowProperties(networkUsageInfoRow, hashNetworkUsageInfo);
                networkUsageInfoDO.addRow(networkUsageInfoRow);
                MDMUtil.getPersistence().add(networkUsageInfoDO);
            }
            else {
                networkUsageInfoRow = networkUsageInfoDO.getFirstRow("MdNetworkUsageInfo");
                networkUsageInfoRow = this.setRowProperties(networkUsageInfoRow, hashNetworkUsageInfo);
                networkUsageInfoDO.updateRow(networkUsageInfoRow);
                MDMUtil.getPersistence().update(networkUsageInfoDO);
            }
        }
    }
    
    private DataObject getNetworkUsageInfoDO(final Long resourceID) throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("MdNetworkUsageInfo"));
        query.addSelectColumn(new Column("MdNetworkUsageInfo", "*"));
        final Criteria criteria = new Criteria(new Column("MdNetworkUsageInfo", "RESOURCE_ID"), (Object)resourceID, 0, false);
        query.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    private Long addModelInfo(final HashMap hashDeviceInfo) throws DataAccessException {
        final Integer platformType = hashDeviceInfo.get("PLATFORM_TYPE");
        String sModelName = hashDeviceInfo.get("MODEL_NAME");
        final String sProductName = hashDeviceInfo.get("PRODUCT_NAME");
        final String sProductManufacturer = hashDeviceInfo.get("MANUFACTURER");
        final String sModel = hashDeviceInfo.get("MODEL");
        final String sModelCode = hashDeviceInfo.get("MODEL_CODE");
        int sModeltype = 11;
        try {
            sModeltype = Integer.parseInt(hashDeviceInfo.get("MODEL_TYPE"));
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.INFO, null, ex);
        }
        if (platformType.equals(1)) {
            sModelName = this.getModelNameForAppleDevices(sModelName, sProductName, sModel, sModeltype);
        }
        if (platformType.equals(2)) {
            sModelName = MDMApiFactoryProvider.getMdmModelNameMappingHandlerAPI().getModelNameForAndroidDevices(sModelCode, sProductManufacturer, sProductName);
        }
        Long modelId = this.updateModelInfo(sModeltype, sProductName, sModel, sProductManufacturer, sModelName, sModelCode);
        if (modelId == -1L) {
            final Row row = new Row("MdModelInfo");
            row.set("MODEL_NAME", (Object)sModelName);
            row.set("PRODUCT_NAME", (Object)sProductName);
            row.set("MODEL", (Object)sModel);
            row.set("MODEL_TYPE", (Object)sModeltype);
            row.set("MANUFACTURER", (Object)sProductManufacturer);
            row.set("MODEL_CODE", (Object)sModelCode);
            final DataObject dobj = MDMUtil.getPersistence().constructDataObject();
            dobj.addRow(row);
            MDMUtil.getPersistence().add(dobj);
            modelId = (Long)row.get("MODEL_ID");
        }
        else if (modelId != -1L && sModelCode != null) {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdModelInfo");
            final Criteria modelCodeCri = new Criteria(new Column("MdModelInfo", "MODEL_ID"), (Object)modelId, 0);
            updateQuery.setCriteria(modelCodeCri);
            updateQuery.setUpdateColumn("MODEL_CODE", (Object)sModelCode);
            updateQuery.setUpdateColumn("MODEL_NAME", (Object)sModelName);
            MDMUtil.getPersistence().update(updateQuery);
        }
        return modelId;
    }
    
    private String getModelNameForAppleDevices(String sModelName, final String sProductName, final String sModel, final int sModelType) throws DataAccessException {
        final String modelName = MDMApiFactoryProvider.getMdmModelNameMappingHandlerAPI().getiOSDeviceSpecificModel(sProductName);
        if (!modelName.equalsIgnoreCase(sModelName)) {
            this.updateIOSDeviceSpecificModelInfo(modelName, sModelType, sProductName, sModel);
            sModelName = modelName;
        }
        return sModelName;
    }
    
    private Long addModelInfo(String sModelName, final String sProductName, final String sModel, final int sModelType, final int platformType, final String manufacturer, final String sModelCode) throws DataAccessException {
        Long modelId = -1L;
        if (platformType == 1) {
            sModelName = this.getModelNameForAppleDevices(sModelName, sProductName, sModel, sModelType);
        }
        if (platformType == 2) {
            sModelName = MDMApiFactoryProvider.getMdmModelNameMappingHandlerAPI().getModelNameForAndroidDevices(sModelCode, manufacturer, sProductName);
        }
        final DataObject modelObject = this.updateModelInfo(sModelType, sProductName, sModel);
        if (!modelObject.isEmpty()) {
            final Row row = modelObject.getRow("MdModelInfo");
            modelId = (Long)row.get("MODEL_ID");
        }
        if (modelId == -1L) {
            final Row row = new Row("MdModelInfo");
            row.set("MODEL_NAME", (Object)sModelName);
            row.set("PRODUCT_NAME", (Object)sProductName);
            row.set("MODEL", (Object)sModel);
            row.set("MODEL_TYPE", (Object)sModelType);
            row.set("MANUFACTURER", (Object)manufacturer);
            row.set("MODEL_CODE", (Object)sModelCode);
            final DataObject dobj = MDMUtil.getPersistence().constructDataObject();
            dobj.addRow(row);
            MDMUtil.getPersistence().add(dobj);
            modelId = (Long)row.get("MODEL_ID");
        }
        return modelId;
    }
    
    private DataObject updateModelInfo(final int sModelType, final String sProductName, final String sModel) throws DataAccessException {
        final Column col1 = Column.getColumn("MdModelInfo", "MODEL_TYPE");
        final Criteria cri1 = new Criteria(col1, (Object)sModelType, 0);
        final Column col2 = Column.getColumn("MdModelInfo", "PRODUCT_NAME");
        final Criteria cri2 = new Criteria(col2, (Object)sProductName, 0);
        final Column col3 = Column.getColumn("MdModelInfo", "MODEL");
        final Criteria cri3 = new Criteria(col3, (Object)sModel, 0);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdModelInfo"));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        query.setCriteria(cri1.and(cri2).and(cri3));
        final DataObject dobj = MDMUtil.getPersistence().get(query);
        return dobj;
    }
    
    private Long updateModelInfo(final int sModelType, final String sProductName, final String sModel, final String sManufacturer, final String sModelName, final String sModelCode) throws DataAccessException {
        Long modelId = new Long(-1L);
        final Column col1 = Column.getColumn("MdModelInfo", "MODEL_TYPE");
        final Criteria cri1 = new Criteria(col1, (Object)sModelType, 0);
        final Column col2 = Column.getColumn("MdModelInfo", "PRODUCT_NAME");
        final Criteria cri2 = new Criteria(col2, (Object)sProductName, 0);
        final Column col3 = Column.getColumn("MdModelInfo", "MODEL");
        final Criteria cri3 = new Criteria(col3, (Object)sModel, 0);
        final Column col4 = Column.getColumn("MdModelInfo", "MANUFACTURER");
        final Criteria cri4 = new Criteria(col4, (Object)sManufacturer, 0);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdModelInfo"));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        Criteria criteria = cri1.and(cri2).and(cri3);
        if (!MDMStringUtils.isEmpty(sManufacturer)) {
            criteria = criteria.and(cri4);
        }
        query.setCriteria(criteria);
        final DataObject dobj = MDMUtil.getPersistence().get(query);
        if (!dobj.isEmpty()) {
            final Row row = dobj.getRow("MdModelInfo");
            modelId = (Long)row.get("MODEL_ID");
        }
        return modelId;
    }
    
    private void checkAndUpdateFilevaultInfo(final Long resourceID, final HashMap fileVaultInfoHash) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: Inside checkAndUpdateFilevaultInfo() for resourceID {0} and values {1}", new Object[] { resourceID, fileVaultInfoHash });
        if (!fileVaultInfoHash.containsKey("FDE_Enabled")) {
            MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: SecurityInfo Response from device does not have Filevault , so not not updating..{0}", resourceID);
            return;
        }
        final DataObject fileVaultInfoDO = this.getMacFileVaultDO(resourceID);
        Row fileVaultInfoRow = null;
        if (fileVaultInfoDO.isEmpty()) {
            MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: No fileVault inventory present for devices , going to add a new row in MDMDEVICEFILEVAULTINFO for {0}", resourceID);
            fileVaultInfoRow = new Row("MDMDeviceFileVaultInfo");
            fileVaultInfoRow = this.getMacFileVaultRow(resourceID, fileVaultInfoHash, fileVaultInfoRow, true);
            fileVaultInfoDO.addRow(fileVaultInfoRow);
            MDMUtil.getPersistence().add(fileVaultInfoDO);
        }
        else {
            MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: Going to update MDMDEVICEFILEVAULTINFO for {0}", resourceID);
            fileVaultInfoRow = fileVaultInfoDO.getFirstRow("MDMDeviceFileVaultInfo");
            fileVaultInfoRow = this.getMacFileVaultRow(resourceID, fileVaultInfoHash, fileVaultInfoRow, false);
            fileVaultInfoDO.updateRow(fileVaultInfoRow);
            MDMUtil.getPersistence().update(fileVaultInfoDO);
        }
    }
    
    public void addOrUpdateIOSSecurityInfo(final Long resourceID, final HashMap hashSecurityInfo) throws Exception {
        final DataObject securityInfoDO = this.getIOSSecurityInfoDO(resourceID);
        Row securityInfoRow = null;
        if (securityInfoDO.isEmpty()) {
            securityInfoRow = new Row("MdSecurityInfo");
            securityInfoRow = this.getSecurityInfoRow(resourceID, hashSecurityInfo, securityInfoRow);
            securityInfoDO.addRow(securityInfoRow);
            MDMUtil.getPersistence().add(securityInfoDO);
        }
        else {
            securityInfoRow = securityInfoDO.getFirstRow("MdSecurityInfo");
            securityInfoRow = this.getSecurityInfoRow(resourceID, hashSecurityInfo, securityInfoRow);
            securityInfoDO.updateRow(securityInfoRow);
            MDMUtil.getPersistence().update(securityInfoDO);
        }
        this.checkAndUpdateFilevaultInfo(resourceID, hashSecurityInfo);
        this.checkAndUpdateFirmwareRow(resourceID, hashSecurityInfo);
        this.checkAndUpdateAppleManagementStatusRow(resourceID, hashSecurityInfo);
        this.checkAndUpdateMacBootstrapTokenSecurityInfo(resourceID, hashSecurityInfo.get("CUSTOMER_ID"), hashSecurityInfo.get("UDID"), hashSecurityInfo);
    }
    
    private void checkAndUpdateMacBootstrapTokenSecurityInfo(final Long resourceID, final Long customerID, final String udid, final HashMap hashSecurityInfo) {
        Logger.getLogger("MDMLogger").log(Level.INFO, "inside checkAndUpdateMacBootstrapTokenSecurityInfo()");
        try {
            if (hashSecurityInfo.containsKey("AuthenticatedRootVolumeEnabled")) {
                final String authRootVolEnabled = hashSecurityInfo.get("AuthenticatedRootVolumeEnabled");
                final String allowedForAuthentication = hashSecurityInfo.get("BootstrapTokenAllowedForAuthentication");
                final String reqForKernalExtensionApprove = hashSecurityInfo.get("BootstrapTokenRequiredForKernelExtensionApproval");
                final String reqForSoftwareUpdate = hashSecurityInfo.get("BootstrapTokenRequiredForSoftwareUpdate");
                final Map bootstrapData = new HashMap();
                bootstrapData.put("AUTH_ROOT_VOL_ENABLED", (authRootVolEnabled == null) ? -1 : ((Boolean.valueOf(authRootVolEnabled) == Boolean.TRUE) ? 1 : 2));
                bootstrapData.put("ALLOWED_FOR_AUTH", (allowedForAuthentication == null) ? 3 : (allowedForAuthentication.equalsIgnoreCase("allowed") ? 1 : (allowedForAuthentication.equalsIgnoreCase("disallowed") ? 2 : -1)));
                bootstrapData.put("REQ_FOR_KERNAL_EXT_APPROVE", (reqForKernalExtensionApprove == null) ? -1 : (Boolean.valueOf(reqForKernalExtensionApprove) ? 1 : 2));
                bootstrapData.put("REQ_FOR_SOFTWARE_UPDATE", (reqForSoftwareUpdate == null) ? -1 : ((Boolean.valueOf(reqForSoftwareUpdate) == Boolean.TRUE) ? 1 : 2));
                Logger.getLogger("MDMLogger").log(Level.INFO, "checkAndUpdateMacBootstrapTokenSecurityInfo():- bootstrapData is={0}", bootstrapData);
                MacBootstrapTokenHandler.getInstance().addOrUpdateMacBootstrapToken(resourceID, customerID, udid, bootstrapData);
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in checkAndUpdateMacBootstrapTokenSecurityInfo():- ", e);
        }
    }
    
    private Row getSecurityInfoRow(final Long resourceID, final HashMap hashSecurityInfo, final Row securityInfoRow) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside getSecurityInfoRow() ->  resourceID {0}", resourceID);
        final String sPasscodePresent = hashSecurityInfo.get("PasscodePresent");
        final String sPasscodeCompliantWithProfiles = hashSecurityInfo.get("PasscodeCompliantWithProfiles");
        String sDeviceRooted = hashSecurityInfo.get("DeviceRooted");
        if (sDeviceRooted == null) {
            sDeviceRooted = Boolean.FALSE + "";
        }
        String sStorageEncryp = hashSecurityInfo.get("StorageEncryption");
        if (hashSecurityInfo.containsKey("FDE_Enabled")) {
            sStorageEncryp = hashSecurityInfo.get("FDE_Enabled");
            MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: Setting ''StorageEncryption'' based on Filevault status {0}", resourceID);
        }
        final String sHardwareEncryptionCaps = hashSecurityInfo.get("HardwareEncryptionCaps");
        final String sPasscodeCompliant = hashSecurityInfo.get("PasscodeCompliant");
        Integer efrpStatus;
        if (hashSecurityInfo.containsKey("EFRPStatus")) {
            efrpStatus = Integer.parseInt(hashSecurityInfo.get("EFRPStatus"));
        }
        else {
            efrpStatus = 3;
        }
        final String playProtectStatus = hashSecurityInfo.containsKey("PlayProtect") ? hashSecurityInfo.get("PlayProtect") : "false";
        securityInfoRow.set("RESOURCE_ID", (Object)resourceID);
        securityInfoRow.set("PASSCODE_PRESENT", (Object)Boolean.parseBoolean(sPasscodePresent));
        securityInfoRow.set("PASSCODE_COMPLAINT_PROFILES", (Object)Boolean.parseBoolean(sPasscodeCompliantWithProfiles));
        securityInfoRow.set("DEVICE_ROOTED", (Object)Boolean.parseBoolean(sDeviceRooted));
        securityInfoRow.set("STORAGE_ENCRYPTION", (Object)Boolean.parseBoolean(sStorageEncryp));
        if (sHardwareEncryptionCaps != null) {
            securityInfoRow.set("HARDWARE_ENCRYPTION_CAPS", (Object)Integer.parseInt(sHardwareEncryptionCaps));
        }
        if (sPasscodeCompliant != null) {
            securityInfoRow.set("PASSCODE_COMPLAINT", (Object)Boolean.parseBoolean(sPasscodeCompliant));
        }
        securityInfoRow.set("EFRP_STATUS", (Object)efrpStatus);
        securityInfoRow.set("PLAY_PROTECT", (Object)Boolean.parseBoolean(playProtectStatus));
        return securityInfoRow;
    }
    
    private DataObject getIOSSecurityInfoDO(final Long resourceID) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside getIOSSecurityInfoDO() ->  resourceID {0}", resourceID);
        final SelectQueryImpl query = new SelectQueryImpl(new Table("MdSecurityInfo"));
        query.addSelectColumn(new Column("MdSecurityInfo", "*"));
        final Criteria criteria = new Criteria(new Column("MdSecurityInfo", "RESOURCE_ID"), (Object)resourceID, 0, false);
        query.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    private Row getMacFileVaultRow(final Long resourceID, final HashMap hashSecurityInfo, final Row fileVaultRow, final boolean isAdd) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: Inside getMacFileVaultRow() ->  resourceID {0}", resourceID);
        int fvstatus = 0;
        final String fdeEnabledStr = hashSecurityInfo.containsKey("FDE_Enabled") ? hashSecurityInfo.get("FDE_Enabled") : "false";
        final String hasPersonalRKeyStr = hashSecurityInfo.containsKey("FDE_HasPersonalRecoveryKey") ? hashSecurityInfo.get("FDE_HasPersonalRecoveryKey") : "false";
        final String hasInstRecoveryKeyStr = hashSecurityInfo.containsKey("FDE_HasInstitutionalRecoveryKey") ? hashSecurityInfo.get("FDE_HasInstitutionalRecoveryKey") : "false";
        final boolean filevaultEnabled = Boolean.parseBoolean(fdeEnabledStr);
        final boolean hasPersonalRKey = Boolean.parseBoolean(hasPersonalRKeyStr);
        final boolean hasInstRecoveryKey = Boolean.parseBoolean(hasInstRecoveryKeyStr);
        final String personalRKeyBlob = hashSecurityInfo.containsKey("FDE_PersonalRecoveryKeyCMS") ? hashSecurityInfo.get("FDE_PersonalRecoveryKeyCMS") : "--";
        if (personalRKeyBlob != "--") {
            MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: MDM has got Personal Recovery Key from device from SecurityInfo response for resourceID {0}", resourceID);
        }
        else {
            MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: MDM did got get PersonalRecoveryKey from device for resourceID {0}", resourceID);
        }
        final String personalIdentificationKey = hashSecurityInfo.containsKey("FDE_PersonalRecoveryKeyDeviceKey") ? hashSecurityInfo.get("FDE_PersonalRecoveryKeyDeviceKey") : "--";
        final String parsedRecoveryKey = MDMStringUtils.isEmpty(personalRKeyBlob) ? "--" : MDMFileVaultRecoveryKeyHander.getDecodedFileVaultRecoveryKeyForDevice(personalRKeyBlob, resourceID);
        if (hashSecurityInfo.containsKey("FDE_Enabled")) {
            fvstatus = (filevaultEnabled ? 20 : 10);
        }
        if (hashSecurityInfo.containsKey("FDE_PersonalRecoveryKeyCMS") || hasInstRecoveryKey) {
            if (hashSecurityInfo.containsKey("FDE_PersonalRecoveryKeyCMS")) {
                if (hasInstRecoveryKey) {
                    fvstatus = 23;
                    MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: FileVault enabled by MDM , Device has both Institutional and Personal Recovery key resourceID:{0}", resourceID);
                }
                else {
                    fvstatus = 21;
                    MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: FileVault enabled by MDM , Device has only Personal recovery key:{0}", resourceID);
                }
            }
            else if (hasInstRecoveryKey) {
                fvstatus = 22;
                MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: FileVault enabled by MDM , Device has only Institutional recovery key:{0}", resourceID);
            }
        }
        if (fvstatus == 20) {
            MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: FileVault enabled by Not by MDM , Device does not have any keys in its response for resourceID:{0}", resourceID);
        }
        else if (fvstatus == 10) {
            MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: FileVault not enabled on device for resourceID :{0}", resourceID);
        }
        fileVaultRow.set("RESOURCE_ID", (Object)resourceID);
        fileVaultRow.set("FILEVAULT_STATUS", (Object)fvstatus);
        fileVaultRow.set("IS_ENCRYPTION_ENABLED", (Object)filevaultEnabled);
        fileVaultRow.set("IS_INSTITUTION_RECOVERY_KEY", (Object)hasInstRecoveryKey);
        fileVaultRow.set("IS_PERSONAL_RECOVERY_KEY", (Object)hasPersonalRKey);
        fileVaultRow.set("PERSONAL_RECOVERY_KEY_IDENTIFIER", (Object)personalIdentificationKey);
        this.updatePersonalRecoveryKeyFilevaultRow(fileVaultRow, resourceID, isAdd, parsedRecoveryKey);
        return fileVaultRow;
    }
    
    public void updatePersonalRecoveryKeyFilevaultRow(final Row fileVaultRow, final Long resourceID, final boolean isAdd, final String newDevicePersonalKey) throws Exception {
        final String previousDevicePersonalRecoveryKey = (String)fileVaultRow.get("PREVIOUS_PERSONAL_RECOVERY_KEY");
        final String currentDevicePersonalRecoveryKey = (String)fileVaultRow.get("PERSONAL_RECOVERY_KEY");
        if (!MDMStringUtils.isEmpty(newDevicePersonalKey)) {
            fileVaultRow.set("PERSONAL_RECOVERY_KEY", (Object)newDevicePersonalKey);
            MDMFilevaultPersonalRecoveryKeyImport.deleteFVKeyImportRotate(resourceID);
            if (MDMStringUtils.isEmpty(currentDevicePersonalRecoveryKey) || !newDevicePersonalKey.equals(currentDevicePersonalRecoveryKey)) {
                MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: Personal recovery key for device has changed for resourceID:{0}", resourceID);
                if (!MDMStringUtils.isEmpty(currentDevicePersonalRecoveryKey)) {
                    fileVaultRow.set("PREVIOUS_PERSONAL_RECOVERY_KEY", (Object)currentDevicePersonalRecoveryKey);
                }
                fileVaultRow.set("RECOVERY_KEY_CHANGED_TIME", (Object)MDMUtil.getCurrentTimeInMillis());
            }
        }
        else {
            if (!MDMStringUtils.isEmpty(currentDevicePersonalRecoveryKey)) {
                MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: MDM has recovery key but new scan does not have recovery key so copying existing recovery key to previous recovery key and marking existing as empty{0}", resourceID);
                fileVaultRow.set("PREVIOUS_PERSONAL_RECOVERY_KEY", (Object)currentDevicePersonalRecoveryKey);
                fileVaultRow.set("PERSONAL_RECOVERY_KEY", (Object)null);
            }
            MDMInvDataPopulator.logger.log(Level.INFO, "FileVaultLog: Unable to retrieve Personal recovery key for device resourceID , so not updating DB with -- to preserve old value:{0}", resourceID);
        }
    }
    
    private void checkAndUpdateFirmwareRow(final Long resourceID, final HashMap hashSecurityInfo) {
        if (!hashSecurityInfo.containsKey("FirmwarePasswordStatus") && !hashSecurityInfo.containsKey("IsRecoveryLockEnabled")) {
            MDMInvDataPopulator.logger.log(Level.INFO, "FirmwareLog: SecurityInfo Response from device does not have FirmwarePasswordStatus or Recovery Lock details, so not not updating..{0}", resourceID);
            return;
        }
        try {
            final DataObject firmwareInfoDO = this.getMacFirmwareDO(resourceID);
            Row firmwareInfoRow = null;
            if (firmwareInfoDO.isEmpty()) {
                MDMInvDataPopulator.logger.log(Level.INFO, "FirmwareLog: No fileVault inventory present for devices , going to add a new row in MDMDEVICEFIRMWAREINFO for {0}", resourceID);
                firmwareInfoRow = new Row("MDMDeviceFirmwareInfo");
                if (hashSecurityInfo.containsKey("IsRecoveryLockEnabled")) {
                    MDMInvDataPopulator.logger.log(Level.INFO, "Adding recovery lock details for device : {0}", new Object[] { resourceID });
                    this.setRecoveryLockDetailsToRow(firmwareInfoRow, resourceID, hashSecurityInfo);
                }
                else {
                    firmwareInfoRow = this.getMacFirmwareRow(resourceID, hashSecurityInfo, firmwareInfoRow);
                }
                firmwareInfoDO.addRow(firmwareInfoRow);
                MDMUtil.getPersistence().add(firmwareInfoDO);
            }
            else {
                MDMInvDataPopulator.logger.log(Level.INFO, "FirmwareLog: Going to update MDMDEVICEFIRMWAREINFO for {0}", resourceID);
                firmwareInfoRow = firmwareInfoDO.getFirstRow("MDMDeviceFirmwareInfo");
                if (hashSecurityInfo.containsKey("IsRecoveryLockEnabled")) {
                    MDMInvDataPopulator.logger.log(Level.INFO, "Updating recovery lock details for device : {0}", new Object[] { resourceID });
                    this.setRecoveryLockDetailsToRow(firmwareInfoRow, resourceID, hashSecurityInfo);
                }
                else {
                    firmwareInfoRow = this.getMacFirmwareRow(resourceID, hashSecurityInfo, firmwareInfoRow);
                }
                firmwareInfoDO.updateRow(firmwareInfoRow);
                MDMUtil.getPersistence().update(firmwareInfoDO);
            }
        }
        catch (final Exception ex) {
            MDMInvDataPopulator.logger.log(Level.INFO, "FirmwareLog: Exception in checkAndUpdateFirmwareRow", ex);
        }
    }
    
    private void setRecoveryLockDetailsToRow(final Row firmwareInfoRow, final Long resourceID, final HashMap hashSecurityInfo) {
        final boolean isRecoveryLockEnabled = Boolean.parseBoolean(hashSecurityInfo.get("IsRecoveryLockEnabled"));
        firmwareInfoRow.set("RESOURCE_ID", (Object)resourceID);
        firmwareInfoRow.set("UDID", (Object)ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceID));
        firmwareInfoRow.set("IS_FIRMWARE_PASSWORD_EXISTS", (Object)isRecoveryLockEnabled);
    }
    
    private Row getMacFirmwareRow(final Long resourceID, final HashMap hashSecurityInfo, final Row firmwareRow) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "FirmwareRow: Inside getMacFirmwareRow() ->  resourceID {0}", resourceID);
        if (hashSecurityInfo.containsKey("FirmwarePasswordStatus")) {
            final HashMap dictKeys = hashSecurityInfo.get("FirmwarePasswordStatus");
            final boolean isChangePending = Boolean.valueOf(dictKeys.get("ChangePending"));
            final boolean isPasswordExists = Boolean.valueOf(dictKeys.get("PasswordExists"));
            firmwareRow.set("RESOURCE_ID", (Object)resourceID);
            firmwareRow.set("UDID", (Object)ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceID));
            firmwareRow.set("IS_FIRMWARE_CHANGE_PENDING", (Object)isChangePending);
            firmwareRow.set("IS_ROMS_ALLOWED", dictKeys.get("AllowOroms"));
            firmwareRow.set("IS_FIRMWARE_PASSWORD_EXISTS", (Object)isPasswordExists);
            firmwareRow.set("FIRMWARE_MODE", (Object)MacFirmwareUtil.getFirmwareModeFromResponse(dictKeys.get("Mode")));
            if (!isChangePending && !isPasswordExists) {
                firmwareRow.set("MANAGED_PASSWORD_ID", (Object)null);
            }
        }
        return firmwareRow;
    }
    
    public DataObject getMacFileVaultDO(final Long resourceID) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside getMacFileVaultDO() ->  resourceID {0}", resourceID);
        final SelectQueryImpl query = new SelectQueryImpl(new Table("MDMDeviceFileVaultInfo"));
        query.addSelectColumn(new Column("MDMDeviceFileVaultInfo", "*"));
        final Criteria criteria = new Criteria(new Column("MDMDeviceFileVaultInfo", "RESOURCE_ID"), (Object)resourceID, 0);
        query.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    private DataObject getAppleManagementStatusDO(final Long resourceID) throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("MDDeviceManagementInfo"));
        query.addSelectColumn(new Column("MDDeviceManagementInfo", "*"));
        final Criteria criteria = new Criteria(new Column("MDDeviceManagementInfo", "RESOURCE_ID"), (Object)resourceID, 0);
        query.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    private void checkAndUpdateAppleManagementStatusRow(final Long resourceID, final HashMap hashSecurityInfo) {
        if (hashSecurityInfo.containsKey("ManagementStatus")) {
            try {
                final DataObject managementStatusDO = this.getAppleManagementStatusDO(resourceID);
                Row applemanagementStatusRow = null;
                if (managementStatusDO.isEmpty()) {
                    applemanagementStatusRow = new Row("MDDeviceManagementInfo");
                    applemanagementStatusRow = this.getAppleManagementStatusRow(resourceID, hashSecurityInfo, applemanagementStatusRow);
                    managementStatusDO.addRow(applemanagementStatusRow);
                    MDMUtil.getPersistence().add(managementStatusDO);
                }
                else {
                    applemanagementStatusRow = managementStatusDO.getFirstRow("MDDeviceManagementInfo");
                    applemanagementStatusRow = this.getAppleManagementStatusRow(resourceID, hashSecurityInfo, applemanagementStatusRow);
                    managementStatusDO.updateRow(applemanagementStatusRow);
                    MDMUtil.getPersistence().update(managementStatusDO);
                }
            }
            catch (final Exception ex) {
                MDMInvDataPopulator.logger.log(Level.INFO, "checkAndUpdateAppleManagementStatusRow: Exception in checkAndUpdateFirmwareRow", ex);
            }
        }
    }
    
    private Row getAppleManagementStatusRow(final Long resourceID, final HashMap hashSecurityInfo, final Row managementStatusRow) throws Exception {
        final HashMap managementStatusDict = hashSecurityInfo.get("ManagementStatus");
        managementStatusRow.set("RESOURCE_ID", (Object)resourceID);
        int manageMentType = -1;
        final boolean isDEPEnrollment = managementStatusDict.containsKey("EnrolledViaDEP") && Boolean.valueOf(managementStatusDict.get("EnrolledViaDEP"));
        final boolean isUserApprovedMDM = managementStatusDict.containsKey("UserApprovedEnrollment") && Boolean.valueOf(managementStatusDict.get("UserApprovedEnrollment"));
        final boolean isUserEnrollment = managementStatusDict.containsKey("IsUserEnrollment") && Boolean.valueOf(managementStatusDict.get("IsUserEnrollment"));
        if (isDEPEnrollment) {
            manageMentType = 1;
        }
        else if (isUserApprovedMDM) {
            manageMentType = 2;
        }
        else if (isUserEnrollment) {
            manageMentType = 3;
        }
        managementStatusRow.set("MANAGEMENT_TYPE", (Object)manageMentType);
        return managementStatusRow;
    }
    
    public DataObject getMacFirmwareDO(final Long resourceID) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside getMacFileVaultDO() ->  resourceID {0}", resourceID);
        final SelectQueryImpl query = new SelectQueryImpl(new Table("MDMDeviceFirmwareInfo"));
        query.addSelectColumn(new Column("MDMDeviceFirmwareInfo", "*"));
        final Criteria criteria = new Criteria(new Column("MDMDeviceFirmwareInfo", "RESOURCE_ID"), (Object)resourceID, 0);
        query.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    public void processSoftwares(final Long resourceID, final NSArray nsarray, final Boolean isAgentOnly) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside processSoftwares() ->  resourceID {0}", resourceID);
        final Long entryTime = MDMUtil.getCurrentTimeInMillis();
        final AppDataHandler appHandler = new AppDataHandler();
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
        appHandler.processIOSSoftwares(resourceID, customerId, nsarray, isAgentOnly);
        final Long exitTime = MDMUtil.getCurrentTimeInMillis();
        MDMInvDataPopulator.logger.log(Level.INFO, "Time taken to process InstalledApplicationList for iOS is: {0}", String.valueOf(exitTime - entryTime));
    }
    
    public void deleteInstalledAppResourceRel(final Long resourceID, final Long appId) {
        MDMInvDataPopulator.logger.log(Level.INFO, "deleteInstalledAppResourceRel(): resourceID:{0}", resourceID);
        MDMInvDataPopulator.logger.log(Level.INFO, "deleteInstalledAppResourceRel(): appId:{0}", appId);
        try {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria appCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "APP_ID"), (Object)appId, 0);
            final Criteria cri = resourceCriteria.and(appCriteria);
            DataAccess.delete("MdInstalledAppResourceRel", cri);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    private DataObject getDeviceInfoDataObject(final Long resourceID) {
        try {
            final Table baseTable = new Table("MdDeviceInfo");
            final Column col = new Column("MdDeviceInfo", "RESOURCE_ID");
            final Criteria criteria = new Criteria(col, (Object)resourceID, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
            query.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            query.addSelectColumn(new Column("MdDeviceInfo", "RESOURCE_ID"));
            query.addSelectColumn(new Column("MdDeviceInfo", "OS_VERSION"));
            query.addSelectColumn(new Column("MdModelInfo", "MODEL_ID"));
            query.addSelectColumn(new Column("MdModelInfo", "PRODUCT_NAME"));
            query.setCriteria(criteria);
            final DataObject dobj = MDMUtil.getPersistence().get(query);
            return dobj;
        }
        catch (final Exception ex) {
            MDMInvDataPopulator.logger.log(Level.INFO, "Exception in getDeviceInfoDataObject(){0}", ex);
            return null;
        }
    }
    
    private HashMap getIOSNotApplicableBasedonOS(final HashMap restrictionHash, final Long deviceOsVersion) throws DataAccessException {
        final int negative = -1;
        final Table baseTable = new Table("MdRestrictionNames");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
        query.addJoin(new Join("MdRestrictionNames", "RestrictionsBasedOnOS", new String[] { "RESTRICTION_ID" }, new String[] { "RESTRICTION_ID" }, 2));
        query.addSelectColumn(new Column("MdRestrictionNames", "*"));
        final Criteria supportedVersion = new Criteria(new Column("RestrictionsBasedOnOS", "SUPPORTED_FROM_VERSION"), (Object)deviceOsVersion, 5);
        final Criteria givenOSVersion = new Criteria(new Column("RestrictionsBasedOnOS", "SUPPORTED_FROM_VERSION"), (Object)deviceOsVersion, 0);
        final Criteria removededVersionIsNegative = new Criteria(new Column("RestrictionsBasedOnOS", "REMOVED_IN_VERSION"), (Object)negative, 0);
        final Criteria removededVersionNotGiven = supportedVersion.and(removededVersionIsNegative);
        final Criteria removededVersionNotNegative = new Criteria(new Column("RestrictionsBasedOnOS", "REMOVED_IN_VERSION"), (Object)negative, 1);
        final Criteria removededVersion = new Criteria(new Column("RestrictionsBasedOnOS", "REMOVED_IN_VERSION"), (Object)deviceOsVersion, 7);
        final Criteria removededOSVersion = removededVersion.or(supportedVersion);
        final Criteria removededVersionGiven = removededVersionNotNegative.and(removededOSVersion);
        final Criteria restrictionNotSupported = removededVersionNotGiven.or(removededVersionGiven);
        query.setCriteria(restrictionNotSupported);
        final DataObject restrictionDO = MDMUtil.getPersistence().get(query);
        if (!restrictionDO.isEmpty()) {
            final Iterator iter = restrictionDO.getRows("MdRestrictionNames");
            while (iter.hasNext()) {
                final Row restriction = iter.next();
                final String restrictionsNotApplicable = (String)restriction.get("RESTRICTION");
                restrictionHash.put(restrictionsNotApplicable, 0);
            }
        }
        return restrictionHash;
    }
    
    private HashMap getIOSNotApplicableBasedonHardware(final HashMap restrictionHash, final String deviceProductName) throws SyMException, DataAccessException {
        Long modelID = null;
        String supportedDeviceName = null;
        final DataObject MdIOSDeviceModelDO = MDMUtil.getData("MdIOSDeviceModel", "MODEL_NAME", (Object)deviceProductName, (String)null, true);
        final Row iosModelRow = MdIOSDeviceModelDO.getRow("MdIOSDeviceModel");
        if (iosModelRow != null) {
            modelID = (Long)iosModelRow.get("MODEL_ID");
            if (deviceProductName.contains("iPhone")) {
                supportedDeviceName = "SUPPORTED_IPNONE";
            }
            else if (deviceProductName.contains("iPad")) {
                supportedDeviceName = "SUPPORTED_IPAD";
            }
            else {
                if (!deviceProductName.contains("iPod")) {
                    return restrictionHash;
                }
                supportedDeviceName = "SUPPORTED_IPOD";
            }
            final Table baseTable = new Table("MdRestrictionNames");
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
            query.addJoin(new Join("MdRestrictionNames", "RestrictionsBasedOnHardware", new String[] { "RESTRICTION_ID" }, new String[] { "RESTRICTION_ID" }, 2));
            query.addSelectColumn(new Column("MdRestrictionNames", "*"));
            final Criteria hardwareRestrictionNotSupported = new Criteria(new Column("RestrictionsBasedOnHardware", supportedDeviceName), (Object)modelID, 5);
            query.setCriteria(hardwareRestrictionNotSupported);
            final DataObject restrictionDO = MDMUtil.getPersistence().get(query);
            if (!restrictionDO.isEmpty()) {
                final Iterator iter = restrictionDO.getRows("MdRestrictionNames");
                while (iter.hasNext()) {
                    final Row restriction = iter.next();
                    final String restrictionsNotApplicable = (String)restriction.get("RESTRICTION");
                    restrictionHash.put(restrictionsNotApplicable, 0);
                }
            }
        }
        return restrictionHash;
    }
    
    private HashMap getIOSNotApplicableValueRestrictionRow(final Long resourceID, HashMap restrictionHash) throws Exception {
        Long deviceOsVersion = null;
        String deviceProductName = "";
        final DataObject deviceInfo = this.getDeviceInfoDataObject(resourceID);
        if (deviceInfo.isEmpty()) {
            return restrictionHash;
        }
        final Row osVersionRow = deviceInfo.getRow("MdDeviceInfo");
        final String[] osVersion = osVersionRow.get("OS_VERSION").toString().split("\\.");
        deviceOsVersion = Long.parseLong(osVersion[0]);
        final Row productNameRow = deviceInfo.getRow("MdModelInfo");
        deviceProductName = productNameRow.get("PRODUCT_NAME").toString();
        restrictionHash = this.getIOSNotApplicableBasedonOS(restrictionHash, deviceOsVersion);
        restrictionHash = this.getIOSNotApplicableBasedonHardware(restrictionHash, deviceProductName);
        return restrictionHash;
    }
    
    public void processIOSRestriction(final Long resourceID, final HashMap restrictionHashfromDevice) throws Exception {
        final int restrictedCookieeValue = 0;
        ArrayList<String> whitelistedBundleIds = new ArrayList<String>();
        ArrayList<String> blacklistedBundleIds = new ArrayList<String>();
        HashMap<String, Integer> restrictionHash = new HashMap<String, Integer>();
        restrictionHash = (HashMap)MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.clone();
        for (final Object key : restrictionHashfromDevice.keySet()) {
            if (MDMInvDataPopulator.IOS_RESTRICTION_HASH.get(key.toString()) != null) {
                final String value = restrictionHashfromDevice.get(key.toString()).toString();
                MDMInvDataPopulator.logger.log(Level.INFO, "resourceID{2} ,restrictionHashfromDevice {1}:{0}", new Object[] { value, key, resourceID });
                restrictionHash.put(MDMInvDataPopulator.IOS_RESTRICTION_HASH.get(key.toString()), value.equals("true") ? 1 : (value.equals("false") ? 2 : Integer.parseInt(value)));
            }
        }
        whitelistedBundleIds = this.getWhitelistedBundleIdsFromDevice(restrictionHashfromDevice);
        blacklistedBundleIds = this.getBlacklistedBundleIdsFromDevice(restrictionHashfromDevice);
        if (!whitelistedBundleIds.isEmpty()) {
            if (!whitelistedBundleIds.contains("com.apple.MobileSMS")) {
                restrictionHash.put("ALLOW_IMESSAGE", 2);
            }
            if (!whitelistedBundleIds.contains("com.apple.gamecenter")) {
                restrictionHash.put("ALLOW_GAME_CENTER", 2);
            }
            if (!whitelistedBundleIds.contains("com.apple.AppStore")) {
                restrictionHash.put("ALLOW_APP_INSTALLATION", 2);
            }
            if (!whitelistedBundleIds.contains("com.apple.MobileStore")) {
                restrictionHash.put("ALLOW_ITUNES", 2);
            }
            if (!whitelistedBundleIds.contains("com.apple.mobilesafari")) {
                restrictionHash.put("ALLOW_SAFARI", 2);
            }
            if (!whitelistedBundleIds.contains("com.apple.news")) {
                restrictionHash.put("ALLOW_NEWS", 2);
            }
            if (!whitelistedBundleIds.contains("com.apple.podcasts")) {
                restrictionHash.put("ALLOW_PODCASTS", 2);
            }
        }
        restrictionHash = this.getIOSNotApplicableValueRestrictionRow(resourceID, restrictionHash);
        if (restrictionHash.get("ALLOW_CAMERA") != null && restrictionHash.get("ALLOW_CAMERA") == 2) {
            restrictionHash.put("ALLOW_FACE_TIME", 2);
        }
        if (restrictionHash.get("ALLOW_GAME_CENTER") != null && restrictionHash.get("ALLOW_GAME_CENTER") == 2) {
            restrictionHash.put("ALLOW_MULTIPLAYER_GAMEING", 2);
            restrictionHash.put("ALLOW_ADDING_GAME_FRIENDS", 2);
        }
        if (restrictionHash.get("ALLOW_SAFARI") != null && restrictionHash.get("ALLOW_SAFARI") == 2) {
            restrictionHash.put("SAFARI_ALLOW_AUTOFILL", 2);
            restrictionHash.put("SAFARI_FORCE_FRAUD_WARNING", 2);
            restrictionHash.put("SAFARI_ALLOW_JAVA_SCRIPT", 2);
            restrictionHash.put("SAFARI_ALLOW_POPUPS", 2);
            restrictionHash.put("SAFARI_ACCEPT_COOKIES", restrictedCookieeValue);
        }
        if (restrictionHash.get("ALLOW_ASSISTANT") != null && restrictionHash.get("ALLOW_ASSISTANT") == 2) {
            restrictionHash.put("ALLOW_ASSIST_WHEN_LOCKED", 2);
            restrictionHash.put("ALLOW_ASSISTANT_USER_CONTENT", 2);
        }
        if (restrictionHash.get("ALLOW_USE_OF_IBOOKSTORE") != null && restrictionHash.get("ALLOW_USE_OF_IBOOKSTORE") == 2) {
            restrictionHash.put("ALLOW_IBOOKSTORE_EROTICA_MEDIA", 2);
        }
        if (restrictionHash.get("ALLOW_DIAGNOSTIC_SUBMISSION") != null && restrictionHash.get("ALLOW_DIAGNOSTIC_SUBMISSION") == 2) {
            restrictionHash.put("ALLOW_DIAG_SUB_MODIFICATION", 2);
        }
        MDMInvDataPopulator.logger.log(Level.INFO, "resourceID:{1} ,restrictionHash {0}", new Object[] { restrictionHash, resourceID });
        MDMInvDataPopulator.logger.log(Level.INFO, "resourceID:{1} ,MultipleAppKioskApps {0}", new Object[] { whitelistedBundleIds, resourceID });
        MDMInvDataPopulator.logger.log(Level.INFO, "resourceID:{1} ,BlacklistedApps {0}", new Object[] { blacklistedBundleIds, resourceID });
        this.addOrUpdateRestrictionDetails(resourceID, restrictionHash, "MdIOSRestriction");
        BlacklistQueryUtils.getInstance().deleteInstalledAppFromIdentifier(resourceID, blacklistedBundleIds);
    }
    
    private ArrayList<String> getWhitelistedBundleIdsFromDevice(final HashMap restrictionHashfromDevice) {
        final ArrayList<String> whitelistedBundleIds = this.getBundleIdsFromResponse("whitelistedAppBundleIDs", restrictionHashfromDevice);
        return whitelistedBundleIds;
    }
    
    private ArrayList<String> getBlacklistedBundleIdsFromDevice(final HashMap restrictionHashfromDevice) {
        final ArrayList<String> blacklistedAppBundleIDs = this.getBundleIdsFromResponse("blacklistedAppBundleIDs", restrictionHashfromDevice);
        return blacklistedAppBundleIDs;
    }
    
    private ArrayList<String> getBundleIdsFromResponse(final String key, final HashMap restrictionHashfromDevice) {
        final ArrayList<String> whitelistedBundleIds = new ArrayList<String>();
        try {
            final Object dictObj = restrictionHashfromDevice.get(key);
            if (dictObj != null) {
                final NSArray nsArray = (NSArray)((NSDictionary)dictObj).get((Object)"values");
                if (nsArray != null) {
                    for (int count = nsArray.count(), i = 0; i < count; ++i) {
                        final String bundleId = ((NSString)nsArray.objectAtIndex(i)).toString();
                        whitelistedBundleIds.add(bundleId);
                    }
                }
            }
        }
        catch (final Exception e) {
            MDMInvDataPopulator.logger.log(Level.SEVERE, "MDMInvDataPopulator exception while getWhitelistedBundleIdsFromDevice {0}", e);
        }
        return whitelistedBundleIds;
    }
    
    private void addOrUpdateRestrictionDetails(final Long resourceID, final Map<String, Integer> hashRestrictionInfo, final String tableName) throws Exception {
        if (hashRestrictionInfo != null) {
            final DataObject restrictionsDO = this.getRestrictionDO(resourceID, tableName);
            if (restrictionsDO.isEmpty()) {
                Row restrictionsRow = new Row(tableName);
                restrictionsRow = this.getRestrictionRow(resourceID, hashRestrictionInfo, restrictionsRow);
                restrictionsDO.addRow(restrictionsRow);
                MDMUtil.getPersistence().add(restrictionsDO);
            }
            else {
                Row restrictionsRow = restrictionsDO.getFirstRow(tableName);
                restrictionsRow = this.getRestrictionRow(resourceID, hashRestrictionInfo, restrictionsRow);
                restrictionsDO.updateRow(restrictionsRow);
                MDMUtil.getPersistence().update(restrictionsDO);
            }
            MDMInvDataPopulator.logger.log(Level.INFO, "Going to add/update restriction details for resource:{0}", new Object[] { resourceID });
        }
    }
    
    private DataObject getRestrictionDO(final Long resourceID, final String tableName) throws Exception {
        final SelectQuery restrictionQuery = (SelectQuery)new SelectQueryImpl(new Table(tableName));
        restrictionQuery.addSelectColumn(new Column(tableName, "*"));
        final Criteria criteria = new Criteria(new Column(tableName, "RESOURCE_ID"), (Object)resourceID, 0);
        restrictionQuery.setCriteria(criteria);
        return MDMUtil.getPersistence().get(restrictionQuery);
    }
    
    private Row getRestrictionRow(final Long resourceId, final Map<String, Integer> hashRestrictionInfo, final Row restrictionsRow) {
        final List columns = restrictionsRow.getColumns();
        for (final Object columnName : columns) {
            restrictionsRow.set(columnName.toString(), (Object)hashRestrictionInfo.get(columnName));
        }
        restrictionsRow.set("RESOURCE_ID", (Object)resourceId);
        return restrictionsRow;
    }
    
    public void addOrUpdateSamsungRestriction(final Long resourceID, final Map hashRestricitonInfo) throws Exception {
        this.addOrUpdateSamsungRestriction(resourceID, hashRestricitonInfo, 0);
    }
    
    public void addOrUpdateSamsungRestriction(final Long resourceID, final Map hashRestricitonInfo, final int scope) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside addOrUpdateSamsungRestriction() ->  resourceID {0}", resourceID);
        if (hashRestricitonInfo != null) {
            final DataObject restrictionListDO = this.getSamsungDeviceRestrictionDO(resourceID, scope);
            Row deviceRestrictionRow = null;
            if (restrictionListDO.isEmpty()) {
                deviceRestrictionRow = new Row("MdRestriction");
                deviceRestrictionRow = this.getSamsungDeviceRestrictionRow(resourceID, hashRestricitonInfo, deviceRestrictionRow, scope);
                restrictionListDO.addRow(deviceRestrictionRow);
                MDMUtil.getPersistence().add(restrictionListDO);
            }
            else {
                deviceRestrictionRow = restrictionListDO.getFirstRow("MdRestriction");
                deviceRestrictionRow = this.getSamsungDeviceRestrictionRow(resourceID, hashRestricitonInfo, deviceRestrictionRow, scope);
                restrictionListDO.updateRow(deviceRestrictionRow);
                MDMUtil.getPersistence().update(restrictionListDO);
            }
        }
    }
    
    private Row getSamsungDeviceRestrictionRow(final Long resourceID, final Map hashDeviceRestriction, final Row deviceRestrictionRow, final int scope) throws Exception {
        deviceRestrictionRow.set("RESOURCE_ID", (Object)resourceID);
        deviceRestrictionRow.set("SCOPE", (Object)scope);
        deviceRestrictionRow.set("ALLOW_CLIPBOARD", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_CLIPBOARD"));
        deviceRestrictionRow.set("ALLOW_GOOGLE_CRASH_REPORT", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_GOOGLE_CRASH_REPORT"));
        deviceRestrictionRow.set("ALLOW_CELLULAR_DATA", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_CELLULAR_DATA"));
        deviceRestrictionRow.set("ALLOW_DISABLING_CELLULAR_DATA", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_DISABLING_CELLULAR_DATA"));
        deviceRestrictionRow.set("ALLOW_DISABLING_GPS", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_DISABLING_GPS"));
        deviceRestrictionRow.set("ALLOW_BLUETOOTH_TETHERING", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_BLUETOOTH_TETHERING"));
        deviceRestrictionRow.set("ALLOW_MOCK_LOCATION", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_MOCK_LOCATION"));
        deviceRestrictionRow.set("ALLOW_MICROPHONE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_MICROPHONE"));
        deviceRestrictionRow.set("ALLOW_WIFI", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_WIFI"));
        deviceRestrictionRow.set("ALLOW_SD_CARD_WRITE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_SD_CARD_WRITE"));
        deviceRestrictionRow.set("ALLOW_BLUETOOTH", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_BLUETOOTH"));
        deviceRestrictionRow.set("ALLOW_BACKGROUND_DATA", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_BACKGROUND_DATA"));
        deviceRestrictionRow.set("ALLOW_POWER_OFF", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_POWER_OFF"));
        deviceRestrictionRow.set("ALLOW_FACTORY_RESET", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_FACTORY_RESET"));
        deviceRestrictionRow.set("ALLOW_SD_CARD", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_SD_CARD"));
        deviceRestrictionRow.set("ALLOW_NFC", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_NFC"));
        deviceRestrictionRow.set("ALLOW_SETTINGS", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_SETTINGS"));
        deviceRestrictionRow.set("ALLOW_TETHERING", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_TETHERING"));
        deviceRestrictionRow.set("ALLOW_WALLPAPER_CHANGE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_WALLPAPER_CHANGE"));
        deviceRestrictionRow.set("ALLOW_USB", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_USB"));
        deviceRestrictionRow.set("ALLOW_USB_TETHERING", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_USB_TETHERING"));
        deviceRestrictionRow.set("ALLOW_USB_DEBUG", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_USB_DEBUG"));
        deviceRestrictionRow.set("ALLOW_GOOGLE_BACKUP", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_GOOGLE_BACKUP"));
        deviceRestrictionRow.set("ALLOW_WIFI_TETHERING", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_WIFI_TETHERING"));
        deviceRestrictionRow.set("ALLOW_USB_MEDIA_PLAYER", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_USB_MEDIA_PLAYER"));
        deviceRestrictionRow.set("ALLOW_STATUSBAR_EXPANSION", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_STATUSBAR_EXPANSION"));
        deviceRestrictionRow.set("ALLOW_OTA_UPGRADE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_OTA_UPGRADE"));
        deviceRestrictionRow.set("ALLOW_VPN", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_VPN"));
        deviceRestrictionRow.set("ALLOW_SCREEN_CAPTURE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_SCREEN_CAPTURE"));
        deviceRestrictionRow.set("ALLOW_CAMERA", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_CAMERA"));
        deviceRestrictionRow.set("ALLOW_ROAMING_SYNC", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_ROAMING_SYNC"));
        deviceRestrictionRow.set("ALLOW_ROAMING_VOICE_CALLS", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_ROAMING_VOICE_CALLS"));
        deviceRestrictionRow.set("ALLOW_ROAMING_PUSH", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_ROAMING_PUSH"));
        deviceRestrictionRow.set("ALLOW_ROAMING_DATA", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_ROAMING_DATA"));
        deviceRestrictionRow.set("BROWSER_ALLOW_FRAUD_WARNING", (Object)this.checkAndReturnValue(hashDeviceRestriction, "BROWSER_ALLOW_FRAUD_WARNING"));
        deviceRestrictionRow.set("BROWSER_ALLOW_AUTOFILL", (Object)this.checkAndReturnValue(hashDeviceRestriction, "BROWSER_ALLOW_AUTOFILL"));
        deviceRestrictionRow.set("BROWSER_ALLOW_POPUPS", (Object)this.checkAndReturnValue(hashDeviceRestriction, "BROWSER_ALLOW_POPUPS"));
        deviceRestrictionRow.set("BROWSER_ALLOW_JAVASCRIPT", (Object)this.checkAndReturnValue(hashDeviceRestriction, "BROWSER_ALLOW_JAVASCRIPT"));
        deviceRestrictionRow.set("BROWSER_ALLOW_COOKIES", (Object)this.checkAndReturnValue(hashDeviceRestriction, "BROWSER_ALLOW_COOKIES"));
        deviceRestrictionRow.set("ALLOW_ANDROID_BROWSER", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_ANDROID_BROWSER"));
        deviceRestrictionRow.set("ALLOW_YOU_TUBE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_YOU_TUBE"));
        deviceRestrictionRow.set("ALLOW_VOICE_DIALER", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_VOICE_DIALER"));
        deviceRestrictionRow.set("ALLOW_INSTALL_APP", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_INSTALL_APP"));
        deviceRestrictionRow.set("ALLOW_UNINSTALL_APP", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_UNINSTALL_APP"));
        deviceRestrictionRow.set("DEVICE_ADMIN_ENABLED", (Object)this.checkAndReturnValue(hashDeviceRestriction, "DEVICE_ADMIN_ENABLED"));
        deviceRestrictionRow.set("ALLOW_ANDROID_MARKET", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_ANDROID_MARKET"));
        deviceRestrictionRow.set("ALLOW_NON_MARKET_APPS", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_NON_MARKET_APPS"));
        deviceRestrictionRow.set("ALLOW_CONTACTS_OUTSIDE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_CONTACTS_OUTSIDE"));
        deviceRestrictionRow.set("ALLOW_OTHER_KEYPAD", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_OTHER_KEYPAD"));
        deviceRestrictionRow.set("ALLOW_SHARELIST", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_SHARELIST"));
        deviceRestrictionRow.set("ALLOW_KNOX_APP_STORE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_KNOX_APP_STORE"));
        deviceRestrictionRow.set("ALLOW_USER_ADD_ACCOUNTS", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_USER_ADD_ACCOUNTS"));
        deviceRestrictionRow.set("ALLOW_AIR_COMMAND", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_AIR_COMMAND"));
        deviceRestrictionRow.set("ALLOW_AIR_VIEW", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_AIR_VIEW"));
        deviceRestrictionRow.set("ALLOW_S_FINDER", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_S_FINDER"));
        deviceRestrictionRow.set("ALLOW_USER_CREATION", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_USER_CREATION"));
        deviceRestrictionRow.set("ALLOW_S_VOICE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_S_VOICE"));
        deviceRestrictionRow.set("ALLOW_STOP_SYSTEM_APP", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_STOP_SYSTEM_APP"));
        deviceRestrictionRow.set("ALLOW_ACTIVATION_LOCK", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_ACTIVATION_LOCK"));
        deviceRestrictionRow.set("ALLOW_AIRPLANE_MODE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_AIRPLANE_MODE"));
        deviceRestrictionRow.set("ALLOW_ANDROID_BEAM", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_ANDROID_BEAM"));
        deviceRestrictionRow.set("ALLOW_BACKGROUND_PROCESS_LIMIT", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_BACKGROUND_PROCESS_LIMIT"));
        deviceRestrictionRow.set("ALLOW_S_BEAM", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_S_BEAM"));
        deviceRestrictionRow.set("ALLOW_WIFI_DIRECT", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_WIFI_DIRECT"));
        deviceRestrictionRow.set("ALLOW_SMART_CLIP_MODE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_SMART_CLIP_MODE"));
        deviceRestrictionRow.set("ALLOW_CLIPBOARD_SHARE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_CLIPBOARD_SHARE"));
        deviceRestrictionRow.set("ALLOW_FIRMWARE_RECOVERY", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_FIRMWARE_RECOVERY"));
        deviceRestrictionRow.set("ALLOW_SDCARD_MOVE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_SDCARD_MOVE"));
        deviceRestrictionRow.set("ALLOW_SAFE_MODE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_SAFE_MODE"));
        deviceRestrictionRow.set("ALLOW_HOME_KEY", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_HOME_KEY"));
        deviceRestrictionRow.set("ALLOW_LOCK_SCREEN_MENU", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_LOCK_SCREEN_MENU"));
        deviceRestrictionRow.set("ALLOW_DEVELOPER_MODE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_DEVELOPER_MODE"));
        deviceRestrictionRow.set("ALLOW_KILL_ACTIVITY_ON_LEAVE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_KILL_ACTIVITY_ON_LEAVE"));
        deviceRestrictionRow.set("ALLOW_LOCK_SCREEN_VIEW", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_LOCK_SCREEN_VIEW"));
        deviceRestrictionRow.set("ALLOW_USER_MOBILE_DATA_LIMIT", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_USER_MOBILE_DATA_LIMIT"));
        deviceRestrictionRow.set("ALLOW_DATE_TIME_CHANGE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_DATE_TIME_CHANGE"));
        deviceRestrictionRow.set("ALLOW_GOOGLE_ACCOUNT_AUTO_SYNC", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_GOOGLE_ACCOUNT_AUTO_SYNC"));
        deviceRestrictionRow.set("ALLOW_USER_PROFILE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_USER_PROFILE"));
        deviceRestrictionRow.set("ALLOW_APP_NOTIFICATION_MODE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_APP_NOTIFICATION_MODE"));
        deviceRestrictionRow.set("ALLOW_AUDIO_RECORD", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_AUDIO_RECORD"));
        deviceRestrictionRow.set("ALLOW_VIDEO_RECORD", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_VIDEO_RECORD"));
        deviceRestrictionRow.set("ALLOW_USB_HOST_STORAGE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_USB_HOST_STORAGE"));
        deviceRestrictionRow.set("ALLOW_USE_NETWORK_TIME", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_USE_NETWORK_TIME"));
        deviceRestrictionRow.set("ALLOW_GMAIL", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_GMAIL"));
        deviceRestrictionRow.set("ALLOW_GOOGLE_MAPS", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_GOOGLE_MAPS"));
        deviceRestrictionRow.set("ALLOW_BT_DISCOVERABLE", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_BT_DISCOVERABLE"));
        deviceRestrictionRow.set("ALLOW_BT_PAIRING", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_BT_PAIRING"));
        deviceRestrictionRow.set("ALLOW_BT_OUTGOING_CALLS", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_BT_OUTGOING_CALLS"));
        deviceRestrictionRow.set("ALLOW_BT_PC_CONNECTION", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_BT_PC_CONNECTION"));
        deviceRestrictionRow.set("ALLOW_BT_DATA_TRANSFER", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_BT_DATA_TRANSFER"));
        deviceRestrictionRow.set("ALLOW_INCOMING_SMS", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_INCOMING_SMS"));
        deviceRestrictionRow.set("ALLOW_INCOMING_MMS", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_INCOMING_MMS"));
        deviceRestrictionRow.set("ALLOW_OUTGOING_MMS", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_OUTGOING_MMS"));
        deviceRestrictionRow.set("ALLOW_OUTGOING_SMS", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_OUTGOING_SMS"));
        deviceRestrictionRow.set("ALLOW_INCOMING_CALL", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_INCOMING_CALL"));
        deviceRestrictionRow.set("ALLOW_OUTGOING_CALL", (Object)this.checkAndReturnValue(hashDeviceRestriction, "ALLOW_OUTGOING_CALL"));
        return deviceRestrictionRow;
    }
    
    private Integer checkAndReturnValue(final Map map, final String key) {
        Integer value = -1;
        try {
            final String valueEx = map.get(key);
            if (valueEx != null) {
                value = Integer.valueOf(valueEx);
            }
        }
        catch (final Exception ex) {}
        return value;
    }
    
    private DataObject getSamsungDeviceRestrictionDO(final Long resourceID) throws Exception {
        return this.getSamsungDeviceRestrictionDO(resourceID, 0);
    }
    
    private DataObject getSamsungDeviceRestrictionDO(final Long resourceID, final int scope) throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("MdRestriction"));
        query.addSelectColumn(new Column("MdRestriction", "*"));
        final Criteria criteria = new Criteria(new Column("MdRestriction", "RESOURCE_ID"), (Object)resourceID, 0, false);
        final Criteria scopeCriteria = new Criteria(new Column("MdRestriction", "SCOPE"), (Object)scope, 0);
        query.setCriteria(criteria.and(scopeCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    public void processCertificates(final Long resourceID, final NSArray nsarray) {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside processCertificates() ->  resourceID {0}", resourceID);
        final JSONArray certificateArray = new JSONArray();
        for (int i = 0; i < nsarray.count(); ++i) {
            try {
                final NSDictionary certificateDict = (NSDictionary)nsarray.objectAtIndex(i);
                final String certificateData = ((NSData)certificateDict.objectForKey("Data")).getBase64EncodedData();
                final JSONObject certificateInfo = CertificateUtils.parseX509Certificate(certificateData);
                certificateInfo.put("CERTIFICATE_CONTENT", (Object)certificateData);
                final String base64EncodedCert = ((NSData)certificateDict.objectForKey("Data")).getBase64EncodedData();
                final String certificateSubjectCN = this.getCertificateSubjectCN(certificateDict, base64EncodedCert);
                final boolean isScepCert = CertificateUtils.loadX509CertificateFromBuffer(base64EncodedCert).getSubjectDN().getName().contains("SCEP_ID");
                certificateInfo.put("IsIdentity", (Object)certificateDict.objectForKey("IsIdentity").toString());
                certificateInfo.put("CommonName", (Object)certificateSubjectCN);
                certificateInfo.put("CERTIFICATE_SIGNATURE", (Object)CertificateUtil.getSignatureFromX509Certificate(CertificateUtils.loadX509CertificateFromBuffer(((NSData)certificateDict.objectForKey("Data")).getBase64EncodedData())));
                certificateInfo.put("IsScepCertificate", isScepCert);
                certificateArray.put((Object)certificateInfo);
            }
            catch (final Exception e) {
                MDMInvDataPopulator.logger.log(Level.SEVERE, e, () -> "Inside processCertificates() CertificateUtils.parseX509Certificate() Exception occurred ->  resourceID : " + n);
            }
        }
        MDMInvDataPopulator.logger.log(Level.INFO, "Parsed all the certificates for ResourceId: {0}", new Object[] { resourceID });
        final JSONObject certificateObject = new JSONObject();
        certificateObject.put("CertificateList", (Object)certificateArray);
        final MDMInvdetails inventoryObject = new MDMInvdetails(resourceID, certificateObject.toString(), 0);
        final InventoryCertificateDataHandler certificateProcessor = new InventoryCertificateDataHandler();
        certificateProcessor.populateInventoryData(inventoryObject);
    }
    
    private String getCertificateSubjectCN(final NSDictionary certificateDict, final String base64EncodedCert) {
        try {
            if (certificateDict.objectForKey("CommonName") != null) {
                return certificateDict.objectForKey("CommonName").toString();
            }
            final X509Certificate x509Certificate = CertificateUtils.loadX509CertificateFromBuffer(base64EncodedCert);
            final String certificateName = CertificateUtil.getCertificateNameFromCertificateSubject(x509Certificate);
            return certificateName;
        }
        catch (final Exception e) {
            MDMInvDataPopulator.logger.log(Level.SEVERE, "Exception while obtaining Subject CN from certificate: {0}", base64EncodedCert);
            return "";
        }
    }
    
    public void addOrUpdateCertificatesInfo(final Long resourceID, final JSONObject hashCertificateInfo, final DataObject dataObject) throws Exception {
        MDMInvDataPopulator.logger.log(Level.FINE, "Inside addOrUpdateCertificates() ->  resourceID {0}", resourceID);
        boolean addRelRow = false;
        if (hashCertificateInfo != null) {
            final String certificateName = (String)hashCertificateInfo.get("CommonName");
            final Criteria certNameCriteria = new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_NAME"), (Object)certificateName, 0);
            final Criteria serialNumberCriteria = new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_SERIAL_NUMBER"), (Object)hashCertificateInfo.get("CERTIFICATE_SERIAL_NUMBER").toString(), 0);
            final Criteria issuerDNCriteria = new Criteria(new Column("MdCertificateInfo", "CERTIFICATE_ISSUER_DN"), (Object)hashCertificateInfo.get("CERTIFICATE_ISSUER_DN").toString(), 0);
            final Criteria identifierCriteria = certNameCriteria.and(serialNumberCriteria).and(issuerDNCriteria);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdCertificateInfo"));
            selectQuery.addSelectColumn(Column.getColumn("MdCertificateInfo", "CERTIFICATE_ID"));
            selectQuery.setCriteria(identifierCriteria);
            final DataObject certificateDO = MDMUtil.getPersistence().get(selectQuery);
            Row certificateRow = null;
            if (certificateDO.isEmpty()) {
                certificateRow = new Row("MdCertificateInfo");
                certificateRow = this.getCertificateInfoRow(resourceID, hashCertificateInfo, certificateRow);
                dataObject.addRow(certificateRow);
                addRelRow = true;
            }
            else {
                certificateRow = certificateDO.getFirstRow("MdCertificateInfo");
                final Long certificateID = (Long)certificateDO.getFirstValue("MdCertificateInfo", "CERTIFICATE_ID");
                final Column appIDColumn = new Column("MdCertificateResourceRel", "CERTIFICATE_ID");
                final Column resIDColumn = new Column("MdCertificateResourceRel", "RESOURCE_ID");
                final Criteria appCriteria = new Criteria(appIDColumn, (Object)certificateID, 0);
                final Criteria rescriteria = new Criteria(resIDColumn, (Object)resourceID, 0);
                final Criteria relcriteria = appCriteria.and(rescriteria);
                final DataObject resourceRelCheckDO = MDMUtil.getPersistence().get("MdCertificateResourceRel", relcriteria);
                if (resourceRelCheckDO.isEmpty()) {
                    final Iterator it = dataObject.getRows("MdCertificateResourceRel", relcriteria);
                    if (!it.hasNext()) {
                        addRelRow = true;
                    }
                }
            }
            if (addRelRow) {
                final Row resourceRelRow = new Row("MdCertificateResourceRel");
                resourceRelRow.set("CERTIFICATE_ID", certificateRow.get("CERTIFICATE_ID"));
                resourceRelRow.set("RESOURCE_ID", (Object)resourceID);
                dataObject.addRow(resourceRelRow);
            }
            final boolean typeScep = hashCertificateInfo.optBoolean("IsScepCertificate", false);
            if (addRelRow && typeScep) {
                final Row scepCertRow = new Row("MDScepCertificates");
                scepCertRow.set("CERTIFICATE_ID", certificateRow.get("CERTIFICATE_ID"));
                dataObject.addRow(scepCertRow);
            }
        }
    }
    
    private Row getCertificateInfoRow(final Long resourceID, final JSONObject hashCertificateInfo, final Row certificateInfoRow) throws Exception {
        certificateInfoRow.set("CERTIFICATE_NAME", (Object)hashCertificateInfo.get("CommonName"));
        certificateInfoRow.set("IDENTIFY", (Object)hashCertificateInfo.optBoolean("IsIdentity"));
        certificateInfoRow.set("CERTIFICATE_TYPE", (Object)hashCertificateInfo.get("CERTIFICATE_TYPE"));
        certificateInfoRow.set("CERTIFICATE_VERSION", (Object)hashCertificateInfo.get("CERTIFICATE_VERSION"));
        certificateInfoRow.set("CERTIFICATE_SERIAL_NUMBER", (Object)hashCertificateInfo.get("CERTIFICATE_SERIAL_NUMBER"));
        certificateInfoRow.set("SIGNATURE_ALGORITHM_OID", (Object)hashCertificateInfo.get("SIGNATURE_ALGORITHM_OID"));
        certificateInfoRow.set("SIGNATURE_ALGORITHM_NAME", (Object)hashCertificateInfo.get("SIGNATURE_ALGORITHM_NAME"));
        certificateInfoRow.set("CERTIFICATE_SIGNATURE", (Object)hashCertificateInfo.get("CERTIFICATE_SIGNATURE"));
        certificateInfoRow.set("CERTIFICATE_EXPIRE", (Object)hashCertificateInfo.get("CERTIFICATE_EXPIRE"));
        certificateInfoRow.set("CERTIFICATE_ISSUER_DN", (Object)hashCertificateInfo.get("CERTIFICATE_ISSUER_DN"));
        certificateInfoRow.set("CERTIFICATE_SUBJECT_DN", (Object)hashCertificateInfo.get("CERTIFICATE_SUBJECT_DN"));
        certificateInfoRow.set("CERTIFICATE_CONTENT", (Object)new ByteArrayInputStream(((String)hashCertificateInfo.get("CERTIFICATE_CONTENT")).getBytes()));
        return certificateInfoRow;
    }
    
    public void updateDeviceScanStus(final long resourceID, final int scanStatus, final String remarks) {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside updateDeviceScanStus() ->  resourceID {0}", resourceID);
        try {
            final Criteria criteria = new Criteria(Column.getColumn("MdDeviceScanStatus", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject statusObject = MDMUtil.getPersistence().get("MdDeviceScanStatus", criteria);
            if (statusObject.isEmpty()) {
                final Row scanStatusRow = new Row("MdDeviceScanStatus");
                scanStatusRow.set("RESOURCE_ID", (Object)resourceID);
                scanStatusRow.set("LAST_SUCCESSFUL_SCAN", (Object)System.currentTimeMillis());
                scanStatusRow.set("SCAN_STATUS", (Object)scanStatus);
                scanStatusRow.set("SCAN_START_TIME", (Object)System.currentTimeMillis());
                scanStatusRow.set("SCAN_END_TIME", (Object)System.currentTimeMillis());
                scanStatusRow.set("REMARKS", (Object)remarks);
                statusObject.addRow(scanStatusRow);
                MDMUtil.getPersistence().add(statusObject);
            }
            else {
                final Row scanStatusRow = statusObject.getFirstRow("MdDeviceScanStatus");
                scanStatusRow.set("RESOURCE_ID", (Object)resourceID);
                scanStatusRow.set("LAST_SUCCESSFUL_SCAN", (Object)System.currentTimeMillis());
                scanStatusRow.set("SCAN_STATUS", (Object)scanStatus);
                scanStatusRow.set("SCAN_END_TIME", (Object)System.currentTimeMillis());
                scanStatusRow.set("REMARKS", (Object)remarks);
                statusObject.updateRow(scanStatusRow);
                MDMUtil.getPersistence().update(statusObject);
            }
        }
        catch (final Exception ex) {
            MDMInvDataPopulator.logger.log(Level.WARNING, "Exception occurred in updateDeviceScanStus(){0}", ex);
        }
    }
    
    public void checkAndUpdateDeviceScanStatus(final Long resourceID, final int whichStatus, final int scanStatus, final String remarks) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdDeviceScanStatus");
            final Criteria resCri = new Criteria(Column.getColumn("MdDeviceScanStatus", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria whichStateCri = new Criteria(Column.getColumn("MdDeviceScanStatus", "SCAN_STATUS"), (Object)new Integer(whichStatus), 0);
            uQuery.setCriteria(resCri.and(whichStateCri));
            uQuery.setUpdateColumn("SCAN_STATUS", (Object)new Integer(scanStatus));
            uQuery.setUpdateColumn("REMARKS", (Object)remarks);
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception ex) {
            MDMInvDataPopulator.logger.log(Level.WARNING, "Exception occurred while check and update the device scan command status", ex);
        }
    }
    
    public void updateDeviceScanToErrorCode(final Long resourceId, final Integer errorCode) {
        final Criteria cRes = new Criteria(new Column("MdDeviceStatusToErrCode", "RESOURCE_ID"), (Object)resourceId, 0);
        try {
            final DataObject Do = MDMUtil.getPersistence().get("MdDeviceStatusToErrCode", cRes);
            if (Do.isEmpty()) {
                final Row errorRow = new Row("MdDeviceStatusToErrCode");
                errorRow.set("RESOURCE_ID", (Object)resourceId);
                errorRow.set("ERROR_CODE", (Object)errorCode);
                Do.addRow(errorRow);
                MDMUtil.getPersistence().add(Do);
            }
            else {
                final Row errorRow = Do.getFirstRow("MdDeviceStatusToErrCode");
                errorRow.set("ERROR_CODE", (Object)errorCode);
                Do.updateRow(errorRow);
                MDMUtil.getPersistence().update(Do);
            }
        }
        catch (final DataAccessException ex) {
            MDMInvDataPopulator.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    public void deleteDeviceScanToErrCode(final Long resourceId) {
        MDMInvDataPopulator.logger.log(Level.INFO, "deleteDeviceScanToErrCode(): resourceID:{0}", resourceId);
        try {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceStatusToErrCode", "RESOURCE_ID"), (Object)resourceId, 0);
            DataAccess.delete("MdDeviceStatusToErrCode", resourceCriteria);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public Integer getDeviceScanToErrCode(final Long resourceId) {
        MDMInvDataPopulator.logger.log(Level.INFO, "deleteDeviceScanToErrCode(): resourceID:{0}", resourceId);
        try {
            return (Integer)DBUtil.getValueFromDB("MdDeviceStatusToErrCode", "RESOURCE_ID", (Object)resourceId, "ERROR_CODE");
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }
    
    public void deleteDeviceScanToErrCode(final List resourceList) {
        MDMInvDataPopulator.logger.log(Level.INFO, "deleteDeviceScanToErrCode(): resource list :{0}", resourceList);
        try {
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceStatusToErrCode", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            DataAccess.delete("MdDeviceStatusToErrCode", resourceCriteria);
        }
        catch (final Exception ex) {
            MDMInvDataPopulator.logger.log(Level.SEVERE, "Exception in deleteDeviceScanToErrCode() ", ex);
        }
    }
    
    public void deleteCertToResourceRelDetails(final Long resourceID) {
        try {
            MDMInvDataPopulator.logger.log(Level.INFO, "Deleting the certificate relation for resource:{0}", new Object[] { resourceID });
            MDMUtil.getPersistence().delete(new Criteria(new Column("MdCertificateResourceRel", "RESOURCE_ID"), (Object)resourceID, 0));
        }
        catch (final DataAccessException ex) {
            MDMInvDataPopulator.logger.log(Level.SEVERE, "Error while deleting MdCertificateToResourceRel table entries", (Throwable)ex);
        }
    }
    
    public void deleteScepCertDetails(final Long resourceID) {
        try {
            MDMInvDataPopulator.logger.log(Level.INFO, "Deleting the certificate relation for resource:{0}", new Object[] { resourceID });
            final DeleteQuery query = (DeleteQuery)new DeleteQueryImpl("MDScepCertificates");
            query.addJoin(new Join("MDScepCertificates", "MdCertificateResourceRel", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_ID" }, 2));
            query.setCriteria(new Criteria(new Column("MdCertificateResourceRel", "RESOURCE_ID"), (Object)resourceID, 0));
            MDMUtil.getPersistenceLite().delete(query);
        }
        catch (final DataAccessException ex) {
            MDMInvDataPopulator.logger.log(Level.SEVERE, "Error while deleting MdScepCertificate table entries", (Throwable)ex);
        }
    }
    
    public void handleLostModeData(final Long resourceID, final Boolean isLostModeCurrently) throws Exception {
        final LostModeDataHandler handler = new LostModeDataHandler();
        final int status = handler.getLostModeStatus(resourceID);
        final Boolean isLostMode = status == 2 || status == 6 || status == 4;
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside handleLostModeData - Lost mode status in Device: {0} ; Lost mode status in server: {1}", new Object[] { isLostModeCurrently, isLostMode });
        if (!isLostModeCurrently && isLostMode) {
            handler.updateLostModeDeActivated(resourceID);
        }
        else if (isLostModeCurrently && !isLostMode) {
            handler.updateLostModeActivated(resourceID);
        }
    }
    
    private void handleAwaitingConfigStatusForAppleDevices(final Long resourceID, final HashMap hsmap) throws Exception {
        final Boolean isDeviceAwaitingConfiguration = Boolean.parseBoolean(hsmap.get("AwaitingConfiguration"));
        final Row row = MDMDBUtil.getFirstRow("AppleDeviceConfigStatus", new Object[][] { { "RESOURCE_ID", resourceID } });
        if (isDeviceAwaitingConfiguration && row == null) {
            DeviceConfiguredCommandHandler.getInstance().addInProgressStatus(resourceID);
        }
        else if (!isDeviceAwaitingConfiguration && row != null) {
            MDMInvDataPopulator.logger.log(Level.INFO, "Device not awaiting configuration, hence deleting device configured status for device {0}", resourceID);
            MDMDBUtil.deleteRows("AppleDeviceConfigStatus", new Object[][] { { "RESOURCE_ID", resourceID } });
        }
    }
    
    private void handleMDMOptionsForMacDevices(final Long resourceID, final Long customerID, final String udid, final HashMap mdmOptionsMap) throws Exception {
        Logger.getLogger("MDMLogger").log(Level.INFO, "handleMDMOptionsForAppleDevices():- for mdmOptionsMap={0}, resourceID={1} ", new Object[] { mdmOptionsMap, resourceID });
        final String bootStrapTokenAllowed = mdmOptionsMap.get("BootstrapTokenAllowed");
        final String promptUserToAllowBootstrapToken = mdmOptionsMap.get("PromptUserToAllowBootstrapTokenForAuthentication");
        final Map bootstrapData = new HashMap();
        bootstrapData.put("BOOTSTRAPTOKEN_ALLOWED", (promptUserToAllowBootstrapToken == null) ? -1 : ((Boolean.valueOf(bootStrapTokenAllowed) == Boolean.TRUE) ? 1 : 2));
        bootstrapData.put("PROMPT_USER_TO_ALLOW_AUTH", (promptUserToAllowBootstrapToken == null) ? -1 : ((Boolean.valueOf(promptUserToAllowBootstrapToken) == Boolean.TRUE) ? 1 : 2));
        Logger.getLogger("MDMLogger").log(Level.INFO, "handleMDMOptionsForMacDevices():- bootstrapData is={0}", bootstrapData);
        MacBootstrapTokenHandler.getInstance().addOrUpdateMacBootstrapToken(resourceID, customerID, udid, bootstrapData);
    }
    
    public void updateIOSInventory(final Long resourceID, final HashMap hsmap, final NSDictionary nsDictionary) throws Exception {
        final String productName = hsmap.get("ProductName");
        final int modelType = getModelType(productName);
        final HashMap hmDeviceInfo = new HashMap();
        hmDeviceInfo.put("OS_VERSION", hsmap.get("OSVersion"));
        hmDeviceInfo.put("BUILD_VERSION", hsmap.get("BuildVersion"));
        hmDeviceInfo.put("SERIAL_NUMBER", hsmap.get("SerialNumber"));
        hmDeviceInfo.put("CELLULAR_TECHNOLOGY", hsmap.get("CellularTechnology"));
        hmDeviceInfo.put("IMEI", hsmap.get("IMEI"));
        hmDeviceInfo.put("MEID", hsmap.get("MEID"));
        hmDeviceInfo.put("MODEL_NAME", hsmap.get("ModelName"));
        hmDeviceInfo.put("MODEL_TYPE", Integer.toString(modelType));
        hmDeviceInfo.put("PRODUCT_NAME", productName);
        hmDeviceInfo.put("MODEL", hsmap.get("Model"));
        hmDeviceInfo.put("DEVICE_CAPACITY", hsmap.get("DeviceCapacity"));
        hmDeviceInfo.put("AVAILABLE_DEVICE_CAPACITY", hsmap.get("AvailableDeviceCapacity"));
        hmDeviceInfo.put("MODEM_FIRMWARE_VERSION", hsmap.get("ModemFirmwareVersion"));
        hmDeviceInfo.put("IS_CLOUD_BACKUP_ENABLED", hsmap.get("IsCloudBackupEnabled"));
        hmDeviceInfo.put("LAST_CLOUD_BACKUP_DATE", hsmap.get("LastCloudBackupDate"));
        this.handleAwaitingConfigStatusForAppleDevices(resourceID, hsmap);
        if (hsmap.containsKey("IsMDMLostModeEnabled")) {
            this.handleLostModeData(resourceID, Boolean.parseBoolean(hsmap.get("IsMDMLostModeEnabled")));
        }
        final String sBatteryLevel = hsmap.get("BatteryLevel");
        try {
            if (sBatteryLevel != null) {
                final Float fBatteryLevel = Float.valueOf(sBatteryLevel) * 100.0f;
                hmDeviceInfo.put("BATTERY_LEVEL", String.valueOf(fBatteryLevel));
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("BATTERY_LEVEL", (Object)String.valueOf(fBatteryLevel));
                final Long deviceTime = hsmap.get("DEVICE_LOCAL_TIME");
                final String deviceLocalTime = MdDeviceBatteryDetailsDBHandler.convertMillisecondsToDate(deviceTime);
                jsonObject.put("DEVICE_LOCAL_TIME", (Object)deviceLocalTime);
                MdDeviceBatteryDetailsDBHandler.addOrUpdateBatteryDetails(resourceID, new JSONArray().put((Object)jsonObject));
            }
        }
        catch (final Exception e) {
            MDMInvDataPopulator.logger.log(Level.WARNING, "Exception while adding battery tracking details", e);
        }
        if (hsmap.containsKey("ServiceSubscriptions")) {
            try {
                int index = 0;
                final ArrayList<HashMap> simDetails = (ArrayList<HashMap>)hsmap.get("ServiceSubscriptions");
                final String simSlot = simDetails.get(index).get("Slot");
                if (!simSlot.equalsIgnoreCase("CTSubscriptionSlotOne")) {
                    index = 1;
                }
                final String meid = simDetails.get(index).get("MEID");
                final String imei = simDetails.get(index).get("IMEI");
                hmDeviceInfo.put("MEID", meid);
                hmDeviceInfo.put("IMEI", imei);
            }
            catch (final Exception ex) {
                MDMInvDataPopulator.logger.log(Level.SEVERE, "Exception while getting MEID and IMEI from serviceSubscription - {0}", ex);
            }
        }
        final String isSupervised = hsmap.get("IsSupervised");
        String isMultiUser = hsmap.containsKey("IsMultiUser") ? hsmap.get("IsMultiUser") : null;
        if ((modelType == 3 || modelType == 4) && isMultiUser == null) {
            isMultiUser = "true";
        }
        hmDeviceInfo.put("IS_SUPERVISED", (isSupervised == null) ? "false" : isSupervised);
        final boolean isMacOS = modelType == 3 || modelType == 4;
        if (isMacOS && hsmap.containsKey("MDMOptions")) {
            final HashMap mdmOptionsMap = hsmap.get("MDMOptions");
            this.handleMDMOptionsForMacDevices(resourceID, hsmap.get("CUSTOMER_ID"), hsmap.get("UDID"), mdmOptionsMap);
        }
        hmDeviceInfo.put("IS_DEVICE_LOCATOR_ENABLED", hsmap.get("IsDeviceLocatorServiceEnabled"));
        hmDeviceInfo.put("IS_ACTIVATION_LOCK_ENABLED", hsmap.get("IsActivationLockEnabled"));
        hmDeviceInfo.put("IS_DND_IN_EFFECT", hsmap.get("IsDoNotDisturbInEffect"));
        hmDeviceInfo.put("IS_ITUNES_ACCOUNT_ACTIVE", hsmap.get("iTunesStoreAccountIsActive"));
        hmDeviceInfo.put("EAS_DEVICE_IDENTIFIER", hsmap.get("EASDeviceIdentifier"));
        if (isMacOS) {
            final String isActivationLockSupported = hsmap.get("IsActivationLockSupported");
            final String isAppleSilicon = hsmap.get("IsAppleSilicon");
            final String supportsIosAppInstalls = hsmap.get("SupportsiOSAppInstalls");
            final ProcessorType processorType = this.getProcessorType(isActivationLockSupported, isAppleSilicon, supportsIosAppInstalls);
            hmDeviceInfo.put("PROCESSOR_TYPE", processorType.alias);
        }
        this.addOrUpdateDeviceInfo(resourceID, hmDeviceInfo);
        final HashMap hmNetworkInfo = new HashMap();
        hmNetworkInfo.put("BLUETOOTH_MAC", hsmap.get("BluetoothMAC"));
        hmNetworkInfo.put("WIFI_MAC", hsmap.get("WiFiMAC"));
        hmNetworkInfo.put("VOICE_ROAMING_ENABLED", hsmap.get("VoiceRoamingEnabled"));
        hmNetworkInfo.put("DATA_ROAMING_ENABLED", hsmap.get("DataRoamingEnabled"));
        try {
            hmNetworkInfo.put("ETHERNET_MACS", hsmap.containsKey("EthernetMACs") ? ((NSArray)nsDictionary.get((Object)"EthernetMACs")).objectAtIndex(0).toString() : (hsmap.containsKey("EthernetMAC") ? hsmap.get("EthernetMAC") : hsmap.get("DeviceID")));
        }
        catch (final Exception e2) {
            MDMInvDataPopulator.logger.log(Level.WARNING, "Exception while adding EthernetMAC details", e2);
        }
        hmNetworkInfo.put("IS_PERSONAL_HOTSPOT_ENABLED", hsmap.get("PersonalHotspotEnabled"));
        this.addOrUpdateNetworkInfo(resourceID, hmNetworkInfo);
        if (modelType == 1 || modelType == 0 || modelType == 2) {
            ArrayList simArrayList = null;
            if (hsmap.get("ServiceSubscriptions") != null) {
                final ArrayList<HashMap> simDetailsHm_List = (ArrayList<HashMap>)hsmap.get("ServiceSubscriptions");
                simArrayList = this.getDualSimDetails(simDetailsHm_List);
            }
            else {
                simArrayList = this.getPrimarySimDetails(hsmap);
            }
            this.addOrUpdateSimInfo(resourceID, simArrayList);
            final HashMap accessibilitySettings = hsmap.get("AccessibilitySettings");
            if (accessibilitySettings != null) {
                final HashMap hmAccessibilitySettings = new HashMap();
                hmAccessibilitySettings.put("BOLD_TEXT_ENABLED", accessibilitySettings.get("BoldTextEnabled"));
                hmAccessibilitySettings.put("INCREASE_CONTRAST_ENABLED", accessibilitySettings.get("IncreaseContrastEnabled"));
                hmAccessibilitySettings.put("REDUCE_MOTION_ENABLED", accessibilitySettings.get("ReduceMotionEnabled"));
                hmAccessibilitySettings.put("REDUCE_TRANSPARENCY_ENABLED", accessibilitySettings.get("ReduceTransparencyEnabled"));
                hmAccessibilitySettings.put("TEXT_SIZE", accessibilitySettings.get("TextSize"));
                hmAccessibilitySettings.put("TOUCH_ACCOMMODATIONS_ENABLED", accessibilitySettings.get("TouchAccommodationsEnabled"));
                hmAccessibilitySettings.put("VOICE_OVER_ENABLED", accessibilitySettings.get("VoiceOverEnabled"));
                hmAccessibilitySettings.put("ZOOM_ENABLED", accessibilitySettings.get("ZoomEnabled"));
                this.addOrUpdateAccessibilitySettings(resourceID, hmAccessibilitySettings);
                MDMInvDataPopulator.logger.log(Level.FINE, "updateIOSInventory - Accessibility Settings parsed -> {0}", hsmap);
            }
        }
        if (modelType == 2 && Boolean.valueOf(isMultiUser)) {
            final HashMap sharedDeviceInfo = new HashMap();
            if (hsmap.containsKey("EstimatedResidentUsers")) {
                sharedDeviceInfo.put("ESTIMATED_USER", Integer.valueOf(hsmap.get("EstimatedResidentUsers")));
                sharedDeviceInfo.put("RESIDENT_USER", Integer.valueOf(hsmap.get("ResidentUsers")));
                sharedDeviceInfo.put("QUOTA_SIZE", hsmap.get("QuotaSize"));
                this.addOrUpdateSharedDeviceInfo(sharedDeviceInfo, resourceID);
            }
        }
    }
    
    private ProcessorType getProcessorType(final String isActivationLockSupportedStr, final String isAppleSiliconStr, final String supportsIOSAppInstalls) {
        final boolean isAppleSilicon = Boolean.parseBoolean(isAppleSiliconStr);
        final boolean isSupportsIosAppInstalls = Boolean.parseBoolean(supportsIOSAppInstalls);
        final boolean isActivationLockSupported = Boolean.parseBoolean(isActivationLockSupportedStr);
        if (isAppleSilicon || isSupportsIosAppInstalls) {
            return ProcessorType.SILICON_M1_MAC;
        }
        if (isActivationLockSupported) {
            return ProcessorType.INTEL_WITH_T2_CHIP;
        }
        return ProcessorType.INTEL_MAC;
    }
    
    protected void addOrUpdateSharedDeviceInfo(final HashMap sharedHash, final Long resourceId) {
        try {
            MDMInvDataPopulator.logger.log(Level.INFO, "Inside addOrUpdateSharedDeviceInfo() ->  resourceID {0}", resourceId);
            if (sharedHash != null) {
                final DataObject sharedDeviceInfoDO = this.getSharedDeviceInfo(resourceId);
                Row sharedDeviceInfoRow = null;
                if (sharedDeviceInfoDO.isEmpty()) {
                    MDMInvDataPopulator.logger.log(Level.INFO, "addOrUpdateSharedDeviceInfo() -> sharedDeviceInfoDO is Empty for resourceID {0}", resourceId);
                    sharedDeviceInfoRow = new Row("MdSharedDeviceInfo");
                    sharedDeviceInfoRow = this.getSharedDeviceInfoRow(resourceId, sharedHash, sharedDeviceInfoRow);
                    sharedDeviceInfoDO.addRow(sharedDeviceInfoRow);
                    MDMUtil.getPersistence().add(sharedDeviceInfoDO);
                }
                else {
                    MDMInvDataPopulator.logger.log(Level.INFO, "addOrUpdateSharedDeviceInfo() -> sharedDeviceInfoDO is Empty for resourceID {0}", resourceId);
                    sharedDeviceInfoRow = sharedDeviceInfoDO.getFirstRow("MdSharedDeviceInfo");
                    sharedDeviceInfoRow = this.getSharedDeviceInfoRow(resourceId, sharedHash, sharedDeviceInfoRow);
                    sharedDeviceInfoDO.updateRow(sharedDeviceInfoRow);
                    MDMUtil.getPersistence().update(sharedDeviceInfoDO);
                }
            }
        }
        catch (final Exception e) {
            MDMInvDataPopulator.logger.log(Level.SEVERE, "Exception in addOrUpdateSharedDeviceInfo", e);
        }
    }
    
    private ArrayList getPrimarySimDetails(final HashMap deviceDetailHm) {
        final HashMap hmSimInfo = new HashMap();
        hmSimInfo.put("ICCID", deviceDetailHm.get("ICCID"));
        hmSimInfo.put("IMEI", deviceDetailHm.get("IMEI"));
        hmSimInfo.put("CURRENT_CARRIER_NETWORK", deviceDetailHm.get("CurrentCarrierNetwork"));
        hmSimInfo.put("SUBSCRIBER_CARRIER_NETWORK", deviceDetailHm.get("SIMCarrierNetwork"));
        hmSimInfo.put("CARRIER_SETTING_VERSION", deviceDetailHm.get("CarrierSettingsVersion"));
        hmSimInfo.put("PHONE_NUMBER", deviceDetailHm.get("PhoneNumber"));
        hmSimInfo.put("IS_ROAMING", deviceDetailHm.get("IsRoaming"));
        hmSimInfo.put("SUBSCRIBER_MCC", deviceDetailHm.get("SubscriberMCC"));
        hmSimInfo.put("SUBSCRIBER_MNC", deviceDetailHm.get("SubscriberMNC"));
        hmSimInfo.put("CURRENT_MCC", deviceDetailHm.get("CurrentMCC"));
        hmSimInfo.put("CURRENT_MNC", deviceDetailHm.get("CurrentMNC"));
        hmSimInfo.put("SLOT", 1);
        final ArrayList simDetail_List = new ArrayList();
        simDetail_List.add(hmSimInfo);
        return simDetail_List;
    }
    
    private ArrayList getDualSimDetails(final ArrayList<HashMap> simDetailsHm_List) {
        final ArrayList simDetails_List = new ArrayList();
        for (int i = 0; i < simDetailsHm_List.size(); ++i) {
            final HashMap simDetailsHm = simDetailsHm_List.get(i);
            final HashMap hmSimInfo = new HashMap();
            hmSimInfo.put("ICCID", simDetailsHm.get("ICCID"));
            hmSimInfo.put("IMEI", simDetailsHm.get("IMEI"));
            hmSimInfo.put("CURRENT_CARRIER_NETWORK", simDetailsHm.get("CurrentCarrierNetwork"));
            hmSimInfo.put("SUBSCRIBER_CARRIER_NETWORK", simDetailsHm.get("SIMCarrierNetwork"));
            hmSimInfo.put("CARRIER_SETTING_VERSION", simDetailsHm.get("CarrierSettingsVersion"));
            hmSimInfo.put("PHONE_NUMBER", simDetailsHm.get("PhoneNumber"));
            hmSimInfo.put("IS_ROAMING", simDetailsHm.get("IsRoaming"));
            hmSimInfo.put("CURRENT_MCC", simDetailsHm.get("CurrentMCC"));
            hmSimInfo.put("CURRENT_MNC", simDetailsHm.get("CurrentMNC"));
            hmSimInfo.put("IS_DATA_PREFERRED", simDetailsHm.get("IsDataPreferred"));
            hmSimInfo.put("IS_VOICE_PREFERRED", simDetailsHm.get("IsVoicePreferred"));
            hmSimInfo.put("LABEL", simDetailsHm.get("Label"));
            hmSimInfo.put("LABEL_ID", simDetailsHm.get("LabelID"));
            if (simDetailsHm.get("Slot") != null) {
                final String slot = simDetailsHm.get("Slot");
                if (slot.equalsIgnoreCase("CTSubscriptionSlotOne")) {
                    hmSimInfo.put("SLOT", 1);
                }
                else if (slot.equalsIgnoreCase("CTSubscriptionSlotTwo")) {
                    hmSimInfo.put("SLOT", 2);
                }
                else {
                    MDMInvDataPopulator.logger.log(Level.WARNING, "Sim Slot unknown");
                }
            }
            simDetails_List.add(hmSimInfo);
        }
        return simDetails_List;
    }
    
    public static int getModelType(final String productName) {
        if (productName.toLowerCase().contains("imac") || productName.toLowerCase().contains("macpro") || productName.toLowerCase().contains("macmini")) {
            return 4;
        }
        if (productName.toLowerCase().contains("mac")) {
            return 3;
        }
        if (productName.toLowerCase().contains("ipad")) {
            return 2;
        }
        if (productName.toLowerCase().contains("iphone")) {
            return 1;
        }
        if (productName.toLowerCase().contains("tv")) {
            return 5;
        }
        return 0;
    }
    
    public void addorUpdateAndroidRestriction(final Long resourceID, final HashMap resHash) throws Exception {
        final DataObject restrictionInfoDO = this.getAndroidRestrictionInfoDO(resourceID);
        Row restrictionInfoRow = null;
        if (restrictionInfoDO.isEmpty()) {
            restrictionInfoRow = new Row("MdRestriction");
            restrictionInfoRow = this.getAndroidRestrictionInfoRow(resourceID, resHash, restrictionInfoRow);
            restrictionInfoDO.addRow(restrictionInfoRow);
            MDMUtil.getPersistence().add(restrictionInfoDO);
        }
        else {
            restrictionInfoRow = restrictionInfoDO.getFirstRow("MdRestriction");
            restrictionInfoRow = this.getAndroidRestrictionInfoRow(resourceID, resHash, restrictionInfoRow);
            restrictionInfoDO.updateRow(restrictionInfoRow);
            MDMUtil.getPersistence().update(restrictionInfoDO);
        }
    }
    
    private DataObject getAndroidRestrictionInfoDO(final Long resourceID) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside getAndroidRestrictionInfoDO() ->  resourceID {0}", resourceID);
        final SelectQueryImpl query = new SelectQueryImpl(new Table("MdRestriction"));
        query.addSelectColumn(new Column("MdRestriction", "*"));
        final Criteria criteria = new Criteria(new Column("MdRestriction", "RESOURCE_ID"), (Object)resourceID, 0, false);
        query.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    private Row getAndroidRestrictionInfoRow(final Long resourceID, final HashMap restrictionInfo, final Row securityInfoRow) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside getAndroidRestrictionInfoRow() ->  resourceID {0}", resourceID);
        securityInfoRow.set("RESOURCE_ID", (Object)resourceID);
        securityInfoRow.set("ALLOW_CAMERA", (Object)this.getNewRestrictionValue(Integer.parseInt(restrictionInfo.get("Camera"))));
        securityInfoRow.set("ALLOW_BACKGROUND_DATA", (Object)this.getNewRestrictionValue(Integer.parseInt(restrictionInfo.get("BackgroundData"))));
        securityInfoRow.set("ALLOW_CELLULAR_DATA", (Object)this.getNewRestrictionValue(Integer.parseInt(restrictionInfo.get("DataNetwork"))));
        securityInfoRow.set("ALLOW_BLUETOOTH", (Object)this.getNewRestrictionValue(Integer.parseInt(restrictionInfo.get("Bluetooth"))));
        String nfc = restrictionInfo.get("NFC");
        if (nfc == null) {
            nfc = "3";
        }
        securityInfoRow.set("ALLOW_NFC", (Object)this.getNewRestrictionValue(Integer.parseInt(nfc)));
        securityInfoRow.set("DEVICE_ADMIN_ENABLED", (Object)this.getNewRestrictionValue(Integer.parseInt(restrictionInfo.get("DeviceAdministratorEnabled"))));
        final String disableCellularData = restrictionInfo.get("disableCellularData");
        final String disableGPS = restrictionInfo.get("disableGPS");
        if (disableCellularData != null) {
            securityInfoRow.set("ALLOW_DISABLING_CELLULAR_DATA", (Object)this.getNewRestrictionValue(Integer.parseInt(disableCellularData)));
        }
        if (disableGPS != null) {
            securityInfoRow.set("ALLOW_DISABLING_GPS", (Object)this.getNewRestrictionValue(Integer.parseInt(disableGPS)));
        }
        return securityInfoRow;
    }
    
    private Integer getNewRestrictionValue(final Integer key) {
        if (key == null) {
            return 2;
        }
        switch (key) {
            case 1: {
                return 1;
            }
            case 2: {
                return 0;
            }
            case 3: {
                return 3;
            }
            case 0: {
                return 2;
            }
            default: {
                return key;
            }
        }
    }
    
    public Row setRowProperties(Row row, final HashMap<String, String> rowValues) {
        for (final String columnName : rowValues.keySet()) {
            final String value = rowValues.get(columnName);
            row = this.setValue(row, columnName, value);
        }
        return row;
    }
    
    private Row setValue(final Row row, final String columnName, final String value) {
        if (value != null) {
            final String columnType = row.getColumnType(columnName);
            final String exceptionMessage = "Exception occurred during cast " + value + " to " + columnType + ". ";
            if (columnType.equalsIgnoreCase("BIGINT")) {
                try {
                    final Long val = Long.parseLong(value);
                    row.set(columnName, (Object)val);
                }
                catch (final Exception ex) {
                    MDMInvDataPopulator.logger.info(exceptionMessage + ex);
                }
            }
            else if (columnType.equalsIgnoreCase("INTEGER")) {
                try {
                    final int val2 = Integer.parseInt(value);
                    row.set(columnName, (Object)val2);
                }
                catch (final Exception ex) {
                    MDMInvDataPopulator.logger.info(exceptionMessage + ex);
                }
            }
            else if (columnType.equalsIgnoreCase("BOOLEAN")) {
                try {
                    final boolean val3 = Boolean.parseBoolean(value);
                    row.set(columnName, (Object)val3);
                }
                catch (final Exception ex) {
                    MDMInvDataPopulator.logger.info(exceptionMessage + ex);
                }
            }
            else if (columnType.equalsIgnoreCase("FLOAT")) {
                try {
                    final float val4 = Float.parseFloat(value);
                    row.set(columnName, (Object)val4);
                }
                catch (final Exception ex) {
                    MDMInvDataPopulator.logger.info(exceptionMessage + ex);
                }
            }
            else if (columnType.equalsIgnoreCase("NCHAR")) {
                row.set(columnName, (Object)value);
            }
        }
        return row;
    }
    
    public void updateJailBrokenInfo(final Long resourceID, final boolean isJailBroken) {
        try {
            final DataObject dataObject = this.getIOSSecurityInfoDO(resourceID);
            final Row securityRow = dataObject.getRow("MdSecurityInfo");
            securityRow.set("DEVICE_ROOTED", (Object)isJailBroken);
            dataObject.updateRow(securityRow);
            MDMUtil.getPersistence().update(dataObject);
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
            final JSONObject complianceRules = MdComplianceRulesHandler.getInstance().getAndroidComplianceRules(customerId);
            final boolean isSecuritySettingsEnabled = (boolean)complianceRules.get("CORPORATE_WIPE_ROOTED_DEVICES");
            if (isJailBroken && isSecuritySettingsEnabled) {
                final Object updatedBy = MdComplianceRulesHandler.getInstance().getComplianceRuleConfigUserId(customerId);
                final DeviceDetails device = new DeviceDetails(resourceID);
                DeviceInvCommandHandler.getInstance().sendCommandToDevice(device, "CorporateWipe", (Long)updatedBy);
                final Properties properties = new Properties();
                ((Hashtable<String, String>)properties).put("REMARKS", I18N.getMsg("mdm.deprovision.corporate_wipe_init", new Object[0]));
                ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", 11);
                ((Hashtable<String, Long>)properties).put("UNREGISTERED_TIME", new Long(System.currentTimeMillis()));
                ((Hashtable<String, Boolean>)properties).put("wipeInitiated", true);
                ((Hashtable<String, Long>)properties).put("RESOURCE_ID", resourceID);
                ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(properties);
                final String sEventLogRemarks = "dc.mdm.actionlog.securitycommands.initiate";
                final Object remarksArgs = I18N.getMsg("mdm.jailbroken.wipeInfo", new Object[0]) + "@@@" + device.name;
                String name = "--";
                if (updatedBy != null) {
                    final Criteria usernameCriteria = new Criteria(Column.getColumn("AaaUser", "USER_ID"), updatedBy, 0);
                    final DataObject userDO = MDMUtil.getPersistence().get("AaaUser", usernameCriteria);
                    Row userRow = null;
                    userRow = userDO.getRow("AaaUser");
                    name = (String)userRow.get("FIRST_NAME");
                }
                MDMEventLogHandler.getInstance().MDMEventLogEntry(2051, device.resourceId, name, sEventLogRemarks, remarksArgs, customerId);
                MDMInvDataPopulator.logger.log(Level.INFO, "Jailbroken device detected: Corporate wipe command sent to device ->  resourceID {0}", resourceID);
                final Row deprovisionRow = new Row("DeprovisionHistory");
                deprovisionRow.set("RESOURCE_ID", (Object)resourceID);
                deprovisionRow.set("DEPROVISION_TYPE", (Object)1);
                deprovisionRow.set("DEPROVISION_REASON", (Object)5);
                deprovisionRow.set("DEPROVISION_TIME", (Object)new Long(System.currentTimeMillis()));
                deprovisionRow.set("USER_ID", (Object)updatedBy);
                deprovisionRow.set("WIPE_PENDING", (Object)Boolean.TRUE);
                deprovisionRow.set("COMMENT", (Object)"Jailbroken Device");
                final DataObject dataobject = (DataObject)new WritableDataObject();
                dataobject.addRow(deprovisionRow);
                MDMUtil.getPersistence().add(dataobject);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void addOrUpdateSimInfo(final Long resourceID, final ArrayList simArrayList) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside addOrUpdateSimInfo() ->  resourceID {0}", resourceID);
        if (simArrayList != null && resourceID != null) {
            final DataObject simInfoDO = this.getSimInfoDO(resourceID);
            final int simArraySize = simArrayList.size();
            Boolean isIMEINull = false;
            final Object[] validSimIDs = new Object[simArraySize];
            for (int i = 0; i < simArraySize; ++i) {
                final HashMap hashSimInfo = simArrayList.get(i);
                if (hashSimInfo != null) {
                    final Integer slot = hashSimInfo.get("SLOT");
                    if (slot != null) {
                        if (i == 1 && isIMEINull && hashSimInfo.get("IMEI") == null) {
                            hashSimInfo.put("IMEI", "");
                        }
                        if (hashSimInfo.get("IMEI") == null) {
                            isIMEINull = true;
                        }
                        Boolean addRow = false;
                        Boolean updateRow = false;
                        Boolean singleSimUpdateRow = false;
                        if (simArraySize == 1) {
                            if (simInfoDO.isEmpty()) {
                                addRow = true;
                            }
                            else if (simInfoDO.size("MdSIMInfo") == 1) {
                                singleSimUpdateRow = true;
                            }
                            else {
                                updateRow = true;
                            }
                        }
                        else if (simInfoDO.isEmpty()) {
                            addRow = true;
                        }
                        else {
                            final int simSlotInDO = (int)simInfoDO.getFirstValue("MdSIMInfo", "SLOT");
                            final int existingDOSize = simInfoDO.size("MdSIMInfo");
                            if (existingDOSize == 1 && simSlotInDO != slot) {
                                addRow = true;
                            }
                            else {
                                updateRow = true;
                            }
                        }
                        Row simInfoRow = null;
                        if (addRow) {
                            simInfoRow = new Row("MdSIMInfo");
                            simInfoRow = this.getSimInfoRow(resourceID, hashSimInfo, simInfoRow);
                            simInfoDO.addRow(simInfoRow);
                        }
                        else if (singleSimUpdateRow) {
                            simInfoRow = simInfoDO.getRow("MdSIMInfo", new Criteria(Column.getColumn("MdSIMInfo", "RESOURCE_ID"), (Object)resourceID, 0));
                            simInfoRow = this.getSimInfoRow(resourceID, hashSimInfo, simInfoRow);
                            simInfoDO.updateRow(simInfoRow);
                        }
                        else if (updateRow) {
                            simInfoRow = simInfoDO.getRow("MdSIMInfo", new Criteria(Column.getColumn("MdSIMInfo", "SLOT"), (Object)slot, 0));
                            if (simInfoRow == null) {
                                simInfoRow = new Row("MdSIMInfo");
                                simInfoRow = this.getSimInfoRow(resourceID, hashSimInfo, simInfoRow);
                                simInfoDO.addRow(simInfoRow);
                            }
                            else {
                                simInfoRow = this.getSimInfoRow(resourceID, hashSimInfo, simInfoRow);
                                simInfoDO.updateRow(simInfoRow);
                            }
                        }
                        MDMUtil.getPersistence().update(simInfoDO);
                        if (simInfoRow != null) {
                            validSimIDs[i] = simInfoRow.get("SIM_ID");
                        }
                    }
                }
            }
            final Criteria resourceCri = new Criteria(Column.getColumn("MdSIMInfo", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria removeSimCri = new Criteria(Column.getColumn("MdSIMInfo", "SIM_ID"), (Object)validSimIDs, 9);
            final Criteria cri = resourceCri.and(removeSimCri);
            MDMUtil.getPersistence().delete(cri);
        }
    }
    
    private DataObject getSimInfoDO(final Long resourceID) throws Exception {
        final SelectQueryImpl query = new SelectQueryImpl(new Table("MdSIMInfo"));
        query.addSelectColumn(new Column("MdSIMInfo", "*"));
        final Criteria resourceCri = new Criteria(new Column("MdSIMInfo", "RESOURCE_ID"), (Object)resourceID, 0, false);
        query.setCriteria(resourceCri);
        final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
        return dataObject;
    }
    
    private Row getSimInfoRow(final Long resourceID, final HashMap hashSimInfo, final Row simInfoRow) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside getSimInfoRow() ->  resourceID {0}", resourceID);
        simInfoRow.set("RESOURCE_ID", (Object)resourceID);
        simInfoRow.set("ICCID", (Object)hashSimInfo.get("ICCID"));
        simInfoRow.set("CURRENT_CARRIER_NETWORK", (Object)hashSimInfo.get("CURRENT_CARRIER_NETWORK"));
        simInfoRow.set("SUBSCRIBER_CARRIER_NETWORK", (Object)hashSimInfo.get("SUBSCRIBER_CARRIER_NETWORK"));
        simInfoRow.set("CARRIER_SETTING_VERSION", (Object)hashSimInfo.get("CARRIER_SETTING_VERSION"));
        if (hashSimInfo.get("IMEI") != null) {
            String imei = hashSimInfo.get("IMEI");
            imei = imei.replace(" ", "");
            simInfoRow.set("IMEI", (Object)imei);
        }
        simInfoRow.set("IMSI", (Object)hashSimInfo.get("IMSI"));
        simInfoRow.set("PHONE_NUMBER", (Object)hashSimInfo.get("PHONE_NUMBER"));
        simInfoRow.set("IS_ROAMING", (Object)Boolean.valueOf(hashSimInfo.get("IS_ROAMING")));
        simInfoRow.set("SUBSCRIBER_MCC", (Object)hashSimInfo.get("SUBSCRIBER_MCC"));
        simInfoRow.set("SUBSCRIBER_MNC", (Object)hashSimInfo.get("SUBSCRIBER_MNC"));
        simInfoRow.set("CURRENT_MCC", (Object)hashSimInfo.get("CURRENT_MCC"));
        simInfoRow.set("CURRENT_MNC", (Object)hashSimInfo.get("CURRENT_MNC"));
        simInfoRow.set("IS_DATA_PREFERRED", hashSimInfo.get("IS_DATA_PREFERRED"));
        simInfoRow.set("IS_VOICE_PREFERRED", hashSimInfo.get("IS_VOICE_PREFERRED"));
        simInfoRow.set("LABEL", hashSimInfo.get("LABEL"));
        simInfoRow.set("LABEL_ID", hashSimInfo.get("LABEL_ID"));
        if (hashSimInfo.get("SLOT") != null) {
            simInfoRow.set("SLOT", hashSimInfo.get("SLOT"));
        }
        return simInfoRow;
    }
    
    public void addOrUpdateWindowsRestriction(final Long resourceID, final Map<String, Integer> hashRestrictionInfo) throws Exception {
        this.addOrUpdateRestrictionDetails(resourceID, hashRestrictionInfo, "MdWindowsDeviceRestriction");
    }
    
    public void checkAndUpdateDeviceScanStatus(final List resIDList, final int whichStatus, final int scanStatus, final String remarks) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdDeviceScanStatus");
            final Criteria resCri = new Criteria(Column.getColumn("MdDeviceScanStatus", "RESOURCE_ID"), (Object)resIDList.toArray(), 8);
            final Criteria whichStateCri = new Criteria(Column.getColumn("MdDeviceScanStatus", "SCAN_STATUS"), (Object)new Integer(whichStatus), 0);
            uQuery.setCriteria(resCri.and(whichStateCri));
            uQuery.setUpdateColumn("SCAN_STATUS", (Object)new Integer(scanStatus));
            uQuery.setUpdateColumn("REMARKS", (Object)remarks);
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final Exception ex) {
            MDMInvDataPopulator.logger.log(Level.WARNING, "Exception occurred while check and update the device scan command status with resource list", ex);
        }
    }
    
    public void updateSecurityCommandStatus(final List resList, final int status, final String remarks) {
        try {
            final JSONObject statusJSON = new JSONObject();
            statusJSON.put("REMARKS", (Object)remarks);
            statusJSON.put("COMMAND_STATUS", status);
            new CommandStatusHandler().updateCommandStatus(statusJSON, (ArrayList)resList, status);
        }
        catch (final Exception ex) {
            MDMInvDataPopulator.logger.log(Level.WARNING, "Exception occurred while updateSecurityCommandStatus", ex);
        }
    }
    
    public void updateScanNotificaitonFailed(final List resIDList, final String remarks) {
        try {
            final List resourceIDList = new ArrayList(resIDList);
            final Criteria resCri = new Criteria(Column.getColumn("MdDeviceScanStatus", "RESOURCE_ID"), (Object)resIDList.toArray(), 8);
            final Criteria initStateCri = new Criteria(Column.getColumn("MdDeviceScanStatus", "SCAN_STATUS"), (Object)1, 0);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceScanStatus"));
            sQuery.setCriteria(resCri);
            sQuery.addSelectColumn(Column.getColumn("MdDeviceScanStatus", "*"));
            final DataObject scanDO = MDMUtil.getPersistence().get(sQuery);
            if (scanDO != null && !scanDO.isEmpty()) {
                Iterator scanIterator = scanDO.getRows("MdDeviceScanStatus", initStateCri);
                while (scanIterator.hasNext()) {
                    final Row row = scanIterator.next();
                    row.set("SCAN_STATUS", (Object)0);
                    row.set("REMARKS", (Object)remarks);
                    scanDO.updateRow(row);
                    resourceIDList.remove(row.get("RESOURCE_ID"));
                }
                if (!resourceIDList.isEmpty()) {
                    scanIterator = scanDO.getRows("MdDeviceScanStatus", new Criteria(Column.getColumn("MdDeviceScanStatus", "SCAN_STATUS"), (Object)1, 1));
                    while (scanIterator.hasNext()) {
                        final Row r = scanIterator.next();
                        resourceIDList.remove(r.get("RESOURCE_ID"));
                    }
                    if (resourceIDList.size() > 0) {
                        for (final Object resID : resourceIDList) {
                            scanDO.addRow(this.constructFailureNotficationRow((Long)resID, remarks));
                        }
                    }
                }
            }
            else {
                for (final Object resID2 : resIDList) {
                    scanDO.addRow(this.constructFailureNotficationRow((Long)resID2, remarks));
                }
            }
            MDMUtil.getPersistence().update(scanDO);
        }
        catch (final Exception ex) {
            MDMInvDataPopulator.logger.log(Level.WARNING, "Exception occurred while check and update the device scan command status with resource list", ex);
        }
    }
    
    private Row constructFailureNotficationRow(final Long resourceID, final String remarks) {
        final Row row = new Row("MdDeviceScanStatus");
        row.set("RESOURCE_ID", (Object)resourceID);
        row.set("LAST_SUCCESSFUL_SCAN", (Object)System.currentTimeMillis());
        row.set("SCAN_STATUS", (Object)0);
        row.set("SCAN_START_TIME", (Object)System.currentTimeMillis());
        row.set("SCAN_END_TIME", (Object)System.currentTimeMillis());
        row.set("REMARKS", (Object)remarks);
        return row;
    }
    
    public void processMDMAppAnalyticData(final NSArray nsarray, final Long resourceId) {
        if (nsarray.count() > 0) {
            final NSDictionary appfeedbackDict = (NSDictionary)nsarray.objectAtIndex(0);
            final NSDictionary feedbackDict = (NSDictionary)appfeedbackDict.objectForKey("Feedback");
            Integer docsSavedCount = null;
            Integer noOfTimesDocViewed = null;
            try {
                if (feedbackDict != null) {
                    final NSObject docsSavedObj = feedbackDict.objectForKey("DocSaveCount");
                    final NSObject docViewedObj = feedbackDict.objectForKey("DocViewCount");
                    if (docsSavedObj != null && docViewedObj != null) {
                        docsSavedCount = Integer.valueOf(docsSavedObj.toString());
                        noOfTimesDocViewed = Integer.valueOf(docViewedObj.toString());
                        final Criteria resourceCri = new Criteria(Column.getColumn("MDMAppAnalyticData", "RESOURCE_ID"), (Object)resourceId, 0);
                        final DataObject dObj = MDMUtil.getPersistence().get("MDMAppAnalyticData", resourceCri);
                        if (dObj.isEmpty()) {
                            final Row mdmAppAnalyticDataRow = new Row("MDMAppAnalyticData");
                            mdmAppAnalyticDataRow.set("RESOURCE_ID", (Object)resourceId);
                            mdmAppAnalyticDataRow.set("DOCS_SAVED_COUNT", (Object)docsSavedCount);
                            mdmAppAnalyticDataRow.set("DOC_VIEWER_USED_COUNT", (Object)noOfTimesDocViewed);
                            dObj.addRow(mdmAppAnalyticDataRow);
                            MDMUtil.getPersistence().add(dObj);
                        }
                        else {
                            final Row mdmAppAnalyticDataRow = dObj.getRow("MDMAppAnalyticData");
                            mdmAppAnalyticDataRow.set("DOCS_SAVED_COUNT", (Object)docsSavedCount);
                            mdmAppAnalyticDataRow.set("DOC_VIEWER_USED_COUNT", (Object)noOfTimesDocViewed);
                            dObj.updateRow(mdmAppAnalyticDataRow);
                            MDMUtil.getPersistence().update(dObj);
                        }
                    }
                }
            }
            catch (final Exception ex) {
                MDMInvDataPopulator.logger.log(Level.WARNING, "Exception in processMDMAppAnalyticData", ex);
            }
        }
    }
    
    public Boolean processIosLocationCommand(final NSArray nsarray, final String udid) throws Exception {
        if (nsarray.count() <= 0) {
            return true;
        }
        final NSDictionary appfeedbackDict = (NSDictionary)nsarray.objectAtIndex(0);
        final NSDictionary feedbackDict = (NSDictionary)appfeedbackDict.objectForKey("Feedback");
        if (feedbackDict == null) {
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            MDMGeoLocationHandler.getInstance().addorUpdateDeviceLocationErrorCode(resourceID, 12136);
            return false;
        }
        Boolean locationSettingsEnabled = false;
        final Object locationSettingsObj = feedbackDict.objectForKey("IsLocationSettingsEnabled");
        if (locationSettingsObj != null) {
            locationSettingsEnabled = locationSettingsObj.toString().equals("1");
        }
        if (locationSettingsEnabled) {
            final boolean allowToUpdateLocation = true;
            final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String addedTimeStr;
            if (feedbackDict.objectForKey("LocationUpdationTime") != null) {
                addedTimeStr = feedbackDict.objectForKey("LocationUpdationTime").toString();
                f.setTimeZone(TimeZone.getTimeZone("GMT"));
            }
            else {
                addedTimeStr = feedbackDict.objectForKey("TimeStamp").toString();
            }
            final Date addedTimeDt = f.parse(addedTimeStr);
            final Long locatedTime = addedTimeDt.getTime();
            final Long resourceID2 = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            final JSONObject joLocation = new JSONObject();
            joLocation.put("Latitude", (Object)feedbackDict.objectForKey("Latitude"));
            joLocation.put("Longitude", (Object)feedbackDict.objectForKey("Longitude"));
            joLocation.put("UDID", (Object)udid);
            joLocation.put("ADDED_TIME", System.currentTimeMillis());
            joLocation.put("LocationUpdationTime", System.currentTimeMillis());
            MDMGeoLocationHandler.getInstance().addOrUpdateDeviceLocationDetails(joLocation, udid);
            return true;
        }
        final Long resourceID3 = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
        MDMGeoLocationHandler.getInstance().addorUpdateDeviceLocationErrorCode(resourceID3, 12136);
        return false;
    }
    
    public void processIosLocationMessage(JSONObject locationData, final String udid) {
        try {
            if (locationData.has("Message")) {
                locationData = locationData.getJSONObject("Message");
            }
            final boolean isLocationSettingsEnabled = locationData.optString("IsLocationSettingsEnabled", "").toString().equals("1");
            final Integer deviceLocationStatus = locationData.optInt("DeviceLocationSettingsStatus");
            final Boolean permissionNotGiven = deviceLocationStatus == 2;
            final Boolean permissionOnlyAppOpen = deviceLocationStatus == 3;
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            if (isLocationSettingsEnabled && !permissionNotGiven) {
                final String locUpdationTime = locationData.optString("LocationUpdationTime", (String)null);
                if (locUpdationTime != null) {
                    final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    f.setTimeZone(TimeZone.getTimeZone("GMT"));
                    final Date addedTime = f.parse(locUpdationTime);
                    locationData.put("ADDED_TIME", System.currentTimeMillis());
                    locationData.put("LocationUpdationTime", addedTime.getTime());
                }
                MDMGeoLocationHandler.getInstance().deleteDeviceLocationErrorCode(resourceID);
                MDMGeoLocationHandler.getInstance().addOrUpdateDeviceLocationDetails(locationData, udid);
            }
            else if (isLocationSettingsEnabled && permissionNotGiven) {
                MDMGeoLocationHandler.getInstance().addorUpdateDeviceLocationErrorCode(resourceID, 12141);
            }
            else if (isLocationSettingsEnabled && permissionOnlyAppOpen) {
                MDMGeoLocationHandler.getInstance().addorUpdateDeviceLocationErrorCode(resourceID, 12142);
            }
            else {
                MDMGeoLocationHandler.getInstance().addorUpdateDeviceLocationErrorCode(resourceID, 12136);
            }
            final String jailBrokenStr = locationData.optString("Jailbroken", "");
            if (!jailBrokenStr.equals("")) {
                final boolean isJailBroken = jailBrokenStr.equals("1");
                getInstance().updateJailBrokenInfo(resourceID, isJailBroken);
            }
        }
        catch (final Exception ex) {
            MDMInvDataPopulator.logger.log(Level.SEVERE, "Exception in processIosLocationMessage", ex);
        }
    }
    
    public void addOrUpdateAndroidSamsungRestriction(final Long resourceID, final Map hashRestricitonInfo, final int scope) throws Exception {
        MDMInvDataPopulator.logger.log(Level.INFO, "Inside addOrUpdateAndroidSamsungRestriction() ->  resourceID {0}", resourceID);
        if (hashRestricitonInfo != null) {
            final DataObject restrictionListDO = this.getSamsungDeviceRestrictionDO(resourceID, scope);
            Row deviceRestrictionRow = null;
            if (restrictionListDO.isEmpty()) {
                deviceRestrictionRow = new Row("MdRestriction");
                deviceRestrictionRow = this.getAndroidSamsungDeviceRestrictionRow(resourceID, hashRestricitonInfo, deviceRestrictionRow, scope);
                restrictionListDO.addRow(deviceRestrictionRow);
                MDMUtil.getPersistence().add(restrictionListDO);
            }
            else {
                deviceRestrictionRow = restrictionListDO.getFirstRow("MdRestriction");
                deviceRestrictionRow = this.getAndroidSamsungDeviceRestrictionRow(resourceID, hashRestricitonInfo, deviceRestrictionRow, scope);
                restrictionListDO.updateRow(deviceRestrictionRow);
                MDMUtil.getPersistence().update(restrictionListDO);
            }
        }
    }
    
    private Row getAndroidSamsungDeviceRestrictionRow(final Long resourceID, final Map hashDeviceRestriction, final Row deviceRestrictionRow, final int scope) throws Exception {
        deviceRestrictionRow.set("RESOURCE_ID", (Object)resourceID);
        deviceRestrictionRow.set("SCOPE", (Object)scope);
        for (final String key : hashDeviceRestriction.keySet()) {
            deviceRestrictionRow.set(key, (Object)this.checkAndReturnValue(hashDeviceRestriction, key));
        }
        return deviceRestrictionRow;
    }
    
    public void addModelDetailsOnEnrollment(final JSONObject modelInfo, final Long managedDeviceID) throws Exception {
        final Integer platformType = ManagedDeviceHandler.getInstance().getPlatformType(managedDeviceID);
        final String modelName = modelInfo.optString("MODEL_NAME", "-");
        final String productName = modelInfo.optString("PRODUCT_NAME", "-");
        final String model = modelInfo.optString("MODEL", "-");
        final int modelType = modelInfo.optInt("MODEL_TYPE", 0);
        final String manufacturer = modelInfo.optString("MANUFACTURER", "-");
        final String modelCode = modelInfo.optString("MODEL_CODE", "-");
        if (managedDeviceID != null && modelType != 0) {
            final Long modelId = this.addModelInfo(modelName, productName, model, modelType, platformType, manufacturer, modelCode);
            final DataObject deviceInfoDO = this.getDeviceInfoDO(managedDeviceID);
            if (deviceInfoDO.isEmpty()) {
                final Row deviceInfoRow = new Row("MdDeviceInfo");
                deviceInfoRow.set("RESOURCE_ID", (Object)managedDeviceID);
                deviceInfoRow.set("MODEL_ID", (Object)modelId);
                deviceInfoDO.addRow(deviceInfoRow);
                MDMUtil.getPersistence().add(deviceInfoDO);
            }
        }
    }
    
    public void addInvDetailsOnEnrollment(final JSONObject modelandDeviceInfo, final Long managedDeviceID) throws Exception {
        final String modelName = modelandDeviceInfo.optString("MODEL_NAME", "-");
        final Integer platformType = ManagedDeviceHandler.getInstance().getPlatformType(managedDeviceID);
        final String productName = modelandDeviceInfo.optString("PRODUCT_NAME", "-");
        final String model = modelandDeviceInfo.optString("MODEL", "-");
        final String modelCode = modelandDeviceInfo.optString("MODEL_CODE", "-");
        final Integer modelType = modelandDeviceInfo.optInt("MODEL_TYPE", -1);
        final String manufacturer = modelandDeviceInfo.optString("MANUFACTURER", "-");
        final String serialNumber = modelandDeviceInfo.optString("SERIAL_NUMBER", (String)null);
        final String osVersion = modelandDeviceInfo.optString("OS_VERSION", (String)null);
        String imei = modelandDeviceInfo.optString("IMEI", (String)null);
        final String gsf_id = modelandDeviceInfo.optString("GOOGLE_PLAY_SERVICE_ID", (String)null);
        final String isItunesAccountActiveStr = modelandDeviceInfo.optString("IS_ITUNES_ACCOUNT_ACTIVE", (String)null);
        final String easId = modelandDeviceInfo.optString("EAS_DEVICE_IDENTIFIER", (String)null);
        final boolean isMultiUser = modelandDeviceInfo.optBoolean("IS_MULTIUSER", false);
        Boolean isItunesAccountActive = null;
        if (isItunesAccountActiveStr != null) {
            isItunesAccountActive = Boolean.parseBoolean(isItunesAccountActiveStr);
        }
        final String isSupervisedStr = modelandDeviceInfo.optString("IS_SUPERVISED", (String)null);
        Boolean isSupervised = null;
        if (isSupervisedStr != null) {
            isSupervised = Boolean.parseBoolean(isSupervisedStr);
        }
        final String isProfileOwnerStr = modelandDeviceInfo.optString("IS_PROFILEOWNER", (String)null);
        Boolean isProfileOwner = null;
        if (isProfileOwnerStr != null) {
            isProfileOwner = Boolean.parseBoolean(isProfileOwnerStr);
        }
        if (!MDMStringUtils.isEmpty(imei)) {
            imei = imei.replace(" ", "");
        }
        if (managedDeviceID != null && modelType != -1) {
            final Long modelId = this.addModelInfo(modelName, productName, model, modelType, platformType, manufacturer, modelCode);
            final DataObject deviceInfoDO = this.getDeviceInfoDO(managedDeviceID);
            if (deviceInfoDO.isEmpty()) {
                final Row deviceInfoRow = new Row("MdDeviceInfo");
                deviceInfoRow.set("RESOURCE_ID", (Object)managedDeviceID);
                deviceInfoRow.set("MODEL_ID", (Object)modelId);
                deviceInfoRow.set("SERIAL_NUMBER", (Object)serialNumber);
                deviceInfoRow.set("OS_VERSION", (Object)osVersion);
                deviceInfoRow.set("IMEI", (Object)imei);
                deviceInfoRow.set("IS_ITUNES_ACCOUNT_ACTIVE", (Object)isItunesAccountActive);
                deviceInfoRow.set("IS_SUPERVISED", (Object)isSupervised);
                deviceInfoRow.set("EAS_DEVICE_IDENTIFIER", (Object)easId);
                deviceInfoRow.set("IS_PROFILEOWNER", (Object)isProfileOwner);
                deviceInfoRow.set("GOOGLE_PLAY_SERVICE_ID", (Object)gsf_id);
                deviceInfoRow.set("IS_MULTIUSER", (Object)isMultiUser);
                deviceInfoDO.addRow(deviceInfoRow);
                MDMUtil.getPersistence().add(deviceInfoDO);
            }
            else {
                final Row deviceInfoRow = deviceInfoDO.getRow("MdDeviceInfo");
                deviceInfoRow.set("RESOURCE_ID", (Object)managedDeviceID);
                deviceInfoRow.set("MODEL_ID", (Object)modelId);
                deviceInfoRow.set("SERIAL_NUMBER", (Object)serialNumber);
                deviceInfoRow.set("OS_VERSION", (Object)osVersion);
                deviceInfoRow.set("IMEI", (Object)imei);
                deviceInfoRow.set("IS_ITUNES_ACCOUNT_ACTIVE", (Object)isItunesAccountActive);
                deviceInfoRow.set("IS_SUPERVISED", (Object)isSupervised);
                deviceInfoRow.set("IS_PROFILEOWNER", (Object)isProfileOwner);
                deviceInfoRow.set("GOOGLE_PLAY_SERVICE_ID", (Object)gsf_id);
                deviceInfoRow.set("IS_MULTIUSER", (Object)isMultiUser);
                deviceInfoDO.updateRow(deviceInfoRow);
                MDMUtil.getPersistence().update(deviceInfoDO);
            }
        }
    }
    
    public void updateModelTypeForModelId(final Long modelID, final int modelType) throws Exception {
        final UpdateQuery updateModelTypeQuery = (UpdateQuery)new UpdateQueryImpl("MdModelInfo");
        updateModelTypeQuery.setCriteria(new Criteria(Column.getColumn("MdModelInfo", "MODEL_ID"), (Object)modelID, 0));
        updateModelTypeQuery.setUpdateColumn("MODEL_TYPE", (Object)modelType);
        MDMInvDataPopulator.logger.log(Level.INFO, "Updating MDMODELINFO.MODEL_TYPE for row with MODEL_ID={0} to MODEL_TYPE={1}", new Object[] { modelID, modelType });
        MDMUtil.getPersistence().update(updateModelTypeQuery);
    }
    
    private void updateIOSDeviceSpecificModelInfo(final String modelName, final int modelType, final String productName, final String model) throws DataAccessException {
        final DataObject modelObject = this.updateModelInfo(modelType, productName, model);
        if (!modelObject.isEmpty()) {
            final Column col2 = Column.getColumn("MdModelInfo", "PRODUCT_NAME");
            final Criteria cri2 = new Criteria(col2, (Object)productName, 0);
            final Criteria modelNameCriteria = new Criteria(new Column("MdModelInfo", "MODEL_NAME"), (Object)modelName, 0);
            Row modelNameRow = modelObject.getRow("MdModelInfo", cri2.and(modelNameCriteria));
            if (modelNameRow == null) {
                modelNameRow = modelObject.getRow("MdModelInfo", cri2);
                if (modelNameRow != null) {
                    modelNameRow.set("MODEL_NAME", (Object)modelName);
                    modelObject.updateRow(modelNameRow);
                    MDMInvDataPopulator.logger.log(Level.INFO, "Updating model name for productname");
                    MDMUtil.getPersistenceLite().update(modelObject);
                }
            }
        }
    }
    
    public void addOrUpdateDeviceSummaryData(final Long resourceId, final HashMap<String, String> systemActivityInfo) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceActivityDetails"));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceActivityDetails", "RESOURCE_ID"), (Object)resourceId, 0);
        query.setCriteria(resourceCriteria);
        final DataObject dO = MDMUtil.getPersistence().get(query);
        Boolean isAdded = false;
        Row row;
        if (dO.isEmpty()) {
            row = new Row("MdDeviceActivityDetails");
            isAdded = true;
        }
        else {
            row = dO.getRow("MdDeviceActivityDetails");
        }
        row.set("RESOURCE_ID", (Object)resourceId);
        final String cpuPerformance = systemActivityInfo.get("CpuPerformance");
        row.set("CPU_PERFORMANCE", (Object)Integer.parseInt(cpuPerformance));
        final String lastSyncTime = systemActivityInfo.get("LastSyncTime");
        Date date = new Date();
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = sdf.parse(lastSyncTime);
        }
        catch (final ParseException ex) {
            Logger.getLogger(MDMInvDataPopulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        row.set("LAST_SYNC_TIME", (Object)date.getTime());
        if (isAdded) {
            dO.addRow(row);
        }
        else {
            dO.updateRow(row);
        }
        MDMUtil.getPersistence().update(dO);
    }
    
    public void addOrUpdateRecentUsersData(final Long customerId, final Long resourceId, final HashMap<String, String> systemActivityInfo) throws DataAccessException, JSONException {
        MDMInvDataPopulator.logger.log(Level.INFO, "Populating recent users for resource ID {0}", resourceId);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceRecentUsersInfo"));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceRecentUsersInfo", "RESOURCE_ID"), (Object)resourceId, 0);
        query.setCriteria(resourceCriteria);
        final DataObject dO = MDMUtil.getPersistence().get(query);
        dO.deleteRows("MdDeviceRecentUsersInfo", (Criteria)null);
        final JSONArray recentUsers = new JSONArray((String)systemActivityInfo.get("RecentUsers"));
        final JSONObject chromeDomain = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_CHROME_MGMT);
        final String domainName = chromeDomain.optString("MANAGED_DOMAIN_NAME", "");
        for (int i = 0; i < recentUsers.length(); ++i) {
            final JSONObject recentUser = recentUsers.getJSONObject(i);
            final Row row = new Row("MdDeviceRecentUsersInfo");
            row.set("RESOURCE_ID", (Object)resourceId);
            row.set("ORDER", (Object)(i + 1));
            final int managementType = recentUser.optString("type").equals("USER_TYPE_MANAGED") ? 1 : 2;
            row.set("USER_MANAGEMENT_TYPE", (Object)managementType);
            if (managementType == 1) {
                final String userEmail = recentUser.optString("email", "");
                if (!userEmail.equals("")) {
                    JSONObject userJSON = new JSONObject();
                    userJSON.put("NAME", (Object)userEmail);
                    userJSON.put("DOMAIN_NETBIOS_NAME", (Object)domainName);
                    Long userId = null;
                    try {
                        userJSON = ManagedUserHandler.getInstance().checkIfValidDomainUserName(customerId, userJSON);
                        userId = userJSON.getLong("RESOURCE_ID");
                    }
                    catch (final SyMException ex) {
                        if (ex.getErrorCode() == 12001) {
                            MDMInvDataPopulator.logger.log(Level.INFO, "Seems like user is not synced yet. So populating without EMail address");
                        }
                    }
                    catch (final Exception ex2) {
                        MDMInvDataPopulator.logger.log(Level.SEVERE, "Exception when getting GSuite usere details", ex2);
                    }
                    row.set("USER_ID", (Object)userId);
                }
            }
            dO.addRow(row);
        }
        MDMUtil.getPersistence().update(dO);
    }
    
    public void addOrUpdateACtiveTimeRange(final Long resourceId, final HashMap<String, String> systemActivityInfo) throws DataAccessException, JSONException, ParseException {
        MDMInvDataPopulator.logger.log(Level.INFO, "Populating active range for {0}", resourceId);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceActiveTimeDetails"));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdDeviceActiveTimeDetails", "RESOURCE_ID"), (Object)resourceId, 0);
        query.setCriteria(resourceCriteria);
        final DataObject dO = MDMUtil.getPersistence().get(query);
        dO.deleteRows("MdDeviceActiveTimeDetails", (Criteria)null);
        final JSONArray activeTimeRanges = new JSONArray((String)systemActivityInfo.get("ActiveTimeRanges"));
        for (int i = 0; i < activeTimeRanges.length(); ++i) {
            final JSONObject activeTimeRange = activeTimeRanges.getJSONObject(i);
            final Long activeDate = activeTimeRange.optJSONObject("date").optLong("value");
            final String activeTime = activeTimeRange.optString("activeTime", "-1");
            final Criteria activeDateCriteria = new Criteria(Column.getColumn("MdDeviceActiveTimeDetails", "ACTIVE_DATE"), (Object)activeDate, 0);
            Row activeTimeRangeRow = dO.getRow("MdDeviceActiveTimeDetails", activeDateCriteria);
            if (activeTimeRangeRow == null) {
                activeTimeRangeRow = new Row("MdDeviceActiveTimeDetails");
                activeTimeRangeRow.set("RESOURCE_ID", (Object)resourceId);
                activeTimeRangeRow.set("ACTIVE_TIME", (Object)activeTime);
                activeTimeRangeRow.set("ACTIVE_DATE", (Object)activeDate);
                dO.addRow(activeTimeRangeRow);
            }
            else {
                activeTimeRangeRow.set("ACTIVE_TIME", (Object)activeTime);
                dO.updateRow(activeTimeRangeRow);
            }
        }
        MDMUtil.getPersistence().update(dO);
    }
    
    public void addOrUpdateCapabilitiesInfo(final Long resourceId, final JSONObject capabilitiesInfo) throws DataAccessException, JSONException {
        MDMInvDataPopulator.logger.log(Level.INFO, "Populating capabilities for {0}", resourceId);
        final JSONObject remoteControlCapablity = capabilitiesInfo.getJSONObject("RemoteControlCapabilitiesInfo");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceRemoteControlCapability"));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("DeviceRemoteControlCapability", "RESOURCE_ID"), (Object)resourceId, 0);
        query.setCriteria(resourceCriteria);
        final DataObject dO = MDMUtil.getPersistence().get(query);
        Row row;
        if (dO.isEmpty()) {
            row = new Row("DeviceRemoteControlCapability");
        }
        else {
            row = dO.getFirstRow("DeviceRemoteControlCapability");
        }
        row.set("RESOURCE_ID", (Object)resourceId);
        row.set("IS_CAPABLE", (Object)remoteControlCapablity.getBoolean("isRemoteControlCapable"));
        row.set("PLUGIN_NEEDED", (Object)remoteControlCapablity.getBoolean("isRemoteControlPluginNeeded"));
        row.set("PLUGIN_INSTALLED", (Object)remoteControlCapablity.getBoolean("isRemoteControlPackageInstalled"));
        row.set("PLUGIN_PACKAGE_NAME", (Object)remoteControlCapablity.optString("RemoteControlPackageName", ""));
        if (dO.isEmpty()) {
            dO.addRow(row);
        }
        else {
            dO.updateRow(row);
        }
        MDMUtil.getPersistence().update(dO);
    }
    
    public void addOrUpdateWorkDataSecurityInfo(final Long resourceID, final JSONObject workDataSecurityInfoJson) throws JSONException, DataAccessException {
        if (workDataSecurityInfoJson != null) {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("MdWorkDataSecurity"));
            query.addSelectColumn(new Column("MdWorkDataSecurity", "*"));
            final Criteria criteria = new Criteria(new Column("MdWorkDataSecurity", "RESOURCE_ID"), (Object)resourceID, 0, false);
            query.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get((SelectQuery)query);
            Row workDataSecurityRow = null;
            if (dataObject.isEmpty()) {
                workDataSecurityRow = new Row("MdWorkDataSecurity");
                workDataSecurityRow.set("RESOURCE_ID", (Object)resourceID);
                workDataSecurityRow.set("ALLOW_SHARE_DOC_TO_WORK_PROFILE", (Object)workDataSecurityInfoJson.optBoolean("ALLOW_SHARE_DOC_TO_WORK_PROFILE", false));
                workDataSecurityRow.set("ALLOW_SHARE_DOC_TO_PERSONAL_APPS", (Object)workDataSecurityInfoJson.optBoolean("ALLOW_SHARE_DOC_TO_PERSONAL_APPS", false));
                workDataSecurityRow.set("ALLOW_PROFILE_CONTENT_TO_OTHER_APPS", (Object)workDataSecurityInfoJson.optBoolean("ALLOW_PROFILE_CONTENT_TO_OTHER_APPS", false));
                workDataSecurityRow.set("ALLOW_CONTACT_ACCESS_TO_PERSONAL_APPS", (Object)workDataSecurityInfoJson.optBoolean("ALLOW_CONTACT_ACCESS_TO_PERSONAL_APPS", false));
                workDataSecurityRow.set("ALLOW_PROFILE_APP_WIDGETS_TO_HOME_SCREEN", (Object)workDataSecurityInfoJson.optBoolean("ALLOW_PROFILE_APP_WIDGETS_TO_HOME_SCREEN", false));
                workDataSecurityRow.set("ALLOW_PROFILE_CONTACT_OVER_BLUETOOTH", (Object)workDataSecurityInfoJson.optBoolean("ALLOW_PROFILE_CONTACT_OVER_BLUETOOTH", false));
                workDataSecurityRow.set("ALLOW_CONTACT_IN_PERSONAL_PROFILE", (Object)workDataSecurityInfoJson.optBoolean("ALLOW_CONTACT_IN_PERSONAL_PROFILE", false));
                dataObject.addRow(workDataSecurityRow);
                MDMUtil.getPersistence().add(dataObject);
            }
            else {
                workDataSecurityRow = dataObject.getFirstRow("MdWorkDataSecurity");
                workDataSecurityRow.set("ALLOW_SHARE_DOC_TO_WORK_PROFILE", (Object)workDataSecurityInfoJson.optBoolean("ALLOW_SHARE_DOC_TO_WORK_PROFILE", false));
                workDataSecurityRow.set("ALLOW_SHARE_DOC_TO_PERSONAL_APPS", (Object)workDataSecurityInfoJson.optBoolean("ALLOW_SHARE_DOC_TO_PERSONAL_APPS", false));
                workDataSecurityRow.set("ALLOW_PROFILE_CONTENT_TO_OTHER_APPS", (Object)workDataSecurityInfoJson.optBoolean("ALLOW_PROFILE_CONTENT_TO_OTHER_APPS", false));
                workDataSecurityRow.set("ALLOW_CONTACT_ACCESS_TO_PERSONAL_APPS", (Object)workDataSecurityInfoJson.optBoolean("ALLOW_CONTACT_ACCESS_TO_PERSONAL_APPS", false));
                workDataSecurityRow.set("ALLOW_PROFILE_APP_WIDGETS_TO_HOME_SCREEN", (Object)workDataSecurityInfoJson.optBoolean("ALLOW_PROFILE_APP_WIDGETS_TO_HOME_SCREEN", false));
                workDataSecurityRow.set("ALLOW_PROFILE_CONTACT_OVER_BLUETOOTH", (Object)workDataSecurityInfoJson.optBoolean("ALLOW_PROFILE_CONTACT_OVER_BLUETOOTH", false));
                workDataSecurityRow.set("ALLOW_CONTACT_IN_PERSONAL_PROFILE", (Object)workDataSecurityInfoJson.optBoolean("ALLOW_CONTACT_IN_PERSONAL_PROFILE", false));
                dataObject.updateRow(workDataSecurityRow);
                MDMUtil.getPersistence().update(dataObject);
            }
        }
    }
    
    public void addOrUpdateAccessibilitySettings(final Long resourceId, final HashMap hmAccessibilitySettings) {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("MdIosAccessibilitySettingsInfo"));
        final Criteria resourceCrit = new Criteria(Column.getColumn("MdIosAccessibilitySettingsInfo", "RESOURCE_ID"), (Object)resourceId, 0);
        sq.setCriteria(resourceCrit);
        sq.addSelectColumn(new Column((String)null, "*"));
        try {
            final DataObject DO = MDMUtil.getPersistence().get(sq);
            Row settingRow = DO.getRow("MdIosAccessibilitySettingsInfo");
            boolean isAdded = false;
            if (settingRow == null) {
                settingRow = new Row("MdIosAccessibilitySettingsInfo");
                isAdded = true;
            }
            settingRow.set("RESOURCE_ID", (Object)resourceId);
            settingRow.set("BOLD_TEXT_ENABLED", (Object)Boolean.parseBoolean(hmAccessibilitySettings.get("BOLD_TEXT_ENABLED")));
            settingRow.set("INCREASE_CONTRAST_ENABLED", (Object)Boolean.parseBoolean(hmAccessibilitySettings.get("INCREASE_CONTRAST_ENABLED")));
            settingRow.set("REDUCE_MOTION_ENABLED", (Object)Boolean.parseBoolean(hmAccessibilitySettings.get("REDUCE_MOTION_ENABLED")));
            settingRow.set("REDUCE_TRANSPARENCY_ENABLED", (Object)Boolean.parseBoolean(hmAccessibilitySettings.get("REDUCE_TRANSPARENCY_ENABLED")));
            final Integer textSize = Integer.parseInt(hmAccessibilitySettings.get("TEXT_SIZE"));
            if (textSize >= 0 && textSize <= 11) {
                settingRow.set("TEXT_SIZE", (Object)textSize);
            }
            settingRow.set("TOUCH_ACCOMMODATIONS_ENABLED", (Object)Boolean.parseBoolean(hmAccessibilitySettings.get("TOUCH_ACCOMMODATIONS_ENABLED")));
            settingRow.set("VOICE_OVER_ENABLED", (Object)Boolean.parseBoolean(hmAccessibilitySettings.get("VOICE_OVER_ENABLED")));
            settingRow.set("ZOOM_ENABLED", (Object)Boolean.parseBoolean(hmAccessibilitySettings.get("ZOOM_ENABLED")));
            if (isAdded) {
                DO.addRow(settingRow);
            }
            else {
                DO.updateRow(settingRow);
            }
            MDMUtil.getPersistence().update(DO);
        }
        catch (final Exception e) {
            MDMInvDataPopulator.logger.log(Level.SEVERE, "Exception in addOrUpdateAccessibilitySettings while populating inventory", e);
        }
    }
    
    static {
        MDMInvDataPopulator.logger = Logger.getLogger("MDMLogger");
        MDMInvDataPopulator.mdmInvDataPopulator = null;
        IOS_RESTRICTION_HASH = new HashMap<String, String>() {
            {
                this.put("allowAppInstallation", "ALLOW_APP_INSTALLATION");
                this.put("allowAppRemoval", "ALLOW_APP_REMOVAL");
                this.put("allowCamera", "ALLOW_CAMERA");
                this.put("allowVideoConferencing", "ALLOW_FACE_TIME");
                this.put("allowScreenShot", "ALLOW_SCREEN_CAPTURE");
                this.put("allowGlobalBackgroundFetchWhenRoaming", "ALLOW_SYNC_WHILE_ROAMING");
                this.put("allowVoiceDialing", "ALLOW_VOICE_DIALING");
                this.put("allowInAppPurchases", "ALLOW_INAPP_PURCHASE");
                this.put("allowGameCenter", "ALLOW_GAME_CENTER");
                this.put("allowMultiplayerGaming", "ALLOW_MULTIPLAYER_GAMEING");
                this.put("allowAddingGameCenterFriends", "ALLOW_ADDING_GAME_FRIENDS");
                this.put("forceEncryptedBackup", "FORCE_ENCRYPTED_BACKUP");
                this.put("allowUIConfigurationProfileInstallation", "ALLOW_PROFILE_INSTALLATION");
                this.put("allowFingerprintForUnlock", "ALLOW_TOUCH_ID");
                this.put("allowLockScreenControlCenter", "SHOW_CONTROL_CENTER");
                this.put("allowLockScreenNotificationsView", "SHOW_NOTIFICATION_CENTER");
                this.put("allowLockScreenTodayView", "SHOW_TODAY_VIEW");
                this.put("allowOTAPKIUpdates", "ALLOW_OTA_PKI_UPDATES");
                this.put("allowPassbookWhileLocked", "ALLOW_PASSBOOK_WHEN_LOCKED");
                this.put("forceITunesStorePasswordEntry", "FORCE_ISTORE_PWD_ENTRY");
                this.put("forceLimitAdTracking", "FORCE_LIMITED_AD_TRACKING");
                this.put("allowiTunes", "ALLOW_ITUNES");
                this.put("allowSafari", "ALLOW_SAFARI");
                this.put("safariAllowAutoFill", "SAFARI_ALLOW_AUTOFILL");
                this.put("safariForceFraudWarning", "SAFARI_FORCE_FRAUD_WARNING");
                this.put("safariAllowJavaScript", "SAFARI_ALLOW_JAVA_SCRIPT");
                this.put("safariAllowPopups", "SAFARI_ALLOW_POPUPS");
                this.put("safariAcceptCookies", "SAFARI_ACCEPT_COOKIES");
                this.put("allowExplicitContent", "ALLOW_EXPLICIT_CONTENT");
                this.put("allowCloudBackup", "ALLOW_CLOUD_BACKUP");
                this.put("allowCloudDocumentSync", "ALLOW_CLOUD_DOCUMENT_SYNC");
                this.put("allowPhotoStream", "ALLOW_PHOTO_STREAM");
                this.put("allowSharedStream", "ALLOW_SHARED_STREAM");
                this.put("allowAssistant", "ALLOW_ASSISTANT");
                this.put("allowAssistantWhileLocked", "ALLOW_ASSIST_WHEN_LOCKED");
                this.put("allowUntrustedTLSPrompt", "ALLOW_UNTRUST_TLS_PROMPT");
                this.put("allowAssistantUserGeneratedContent", "ALLOW_ASSISTANT_USER_CONTENT");
                this.put("allowAirDrop", "ALLOW_AIRDROP");
                this.put("allowBookstore", "ALLOW_USE_OF_IBOOKSTORE");
                this.put("allowBookstoreErotica", "ALLOW_IBOOKSTORE_EROTICA_MEDIA");
                this.put("allowChat", "ALLOW_IMESSAGE");
                this.put("allowOpenFromManagedToUnmanaged", "ALLOW_OPEN_DOC_IN_UNMANAGED");
                this.put("allowOpenFromUnmanagedToManaged", "ALLOW_OPEN_DOC_IN_MANAGED");
                this.put("allowAccountModification", "ALLOW_ACCOUNT_MODIFICATION");
                this.put("allowHostPairing", "ALLOW_HOST_PAIRING");
                this.put("allowAppCellularDataModification", "ALLOW_APP_CELLULAR_DATA");
                this.put("forceAssistantProfanityFilter", "FORCE_ASSIST_PROFANITY_FILTER");
                this.put("allowDiagnosticSubmission", "ALLOW_DIAGNOSTIC_SUBMISSION");
                this.put("ratingMovies", "MOVIES_RATING_VALUE");
                this.put("ratingTVShows", "TV_SHOWS_RATING_VALUE");
                this.put("ratingApps", "APPS_RATING_VALUE");
                this.put("allowFindMyFriendsModification", "ALLOW_FIND_MY_FRIENDS_MOD");
                this.put("allowManagedAppsCloudSync", "ALLOW_MANAGED_APP_CLOUD_SYNC");
                this.put("allowEnterpriseBookBackup", "ALLOW_MANAGED_BOOK_BACKUP");
                this.put("allowEnterpriseBookMetadataSync", "ALLOW_MANAGED_BOOK_SYNC");
                this.put("allowActivityContinuation", "ALLOW_ACTIVITY_CONTINUATION");
                this.put("allowEraseContentAndSettings", "ALLOW_ERASE_CONTENT_SETTINGS");
                this.put("allowEnablingRestrictions", "ALLOW_ENABLING_RESTRICTION");
                this.put("allowSpotlightInternetResults", "ALLOW_SPOTLIGHT_RESULT");
                this.put("allowCloudKeychainSync", "ALLOW_CLOUD_KEYCHAIN_SYNC");
                this.put("forceAirPlayOutgoingRequestsPairingPassword", "FORCE_AIRPLAY_OUTGOING_PWD");
                this.put("forceAirPlayIncomingRequestsPairingPassword", "FORCE_AIRPLAY_INCOMING_PWD");
                this.put("allowPodcasts", "ALLOW_PODCASTS");
                this.put("allowDefinitionLookup", "ALLOW_DICTIONARY_LOOKUP");
                this.put("allowPredictiveKeyboard", "ALLOW_PREDICTIVE_KEYBOARD");
                this.put("allowAutoCorrection", "ALLOW_AUTO_CORRECTION");
                this.put("allowSpellCheck", "ALLOW_SPELLCHECK");
                this.put("forceWatchWristDetection", "FORCE_WATCH_WRIST_DETECT");
                this.put("allowFingerprintModification", "ALLOW_MODIFY_TOUCH_ID");
                this.put("forceAirDropUnmanaged", "FORCE_AIRDROP_UNMANAGED");
                this.put("allowKeyboardShortcuts", "ALLOW_KEYBOARD_SHORTCUT");
                this.put("allowPairedWatch", "ALLOW_PAIRED_WATCH");
                this.put("allowPasscodeModification", "ALLOW_MODIFI_PASSCODE");
                this.put("allowDeviceNameModification", "ALLOW_MODIFI_DEVICE_NAME");
                this.put("allowWallpaperModification", "ALLOW_MODIFI_WALLPAPER");
                this.put("allowAutomaticAppDownloads", "ALLOW_AUTO_APP_DOWNLOAD");
                this.put("allowEnterpriseAppTrust", "ALLOW_MANAGED_APP_TRUST");
                this.put("allowCloudPhotoLibrary", "ALLOW_CLOUD_PHOTO_LIB");
                this.put("allowNews", "ALLOW_NEWS");
                this.put("allowDictation", "ALLOW_DICTATION");
                this.put("allowMusicService", "ALLOW_MUSIC_SERVICE");
                this.put("allowRadioService", "ALLOW_RADIO_SERVICE");
                this.put("allowDiagnosticSubmissionModification", "ALLOW_DIAG_SUB_MODIFICATION");
                this.put("allowBluetoothModification", "ALLOW_BLUETOOTH_MODIFICATION");
                this.put("forceWiFiWhitelisting", "FORCE_WIFI_WHITELISTING");
                this.put("allowAirPrint", "ALLOW_AIRPRINT");
                this.put("allowAirPrintCredentialsStorage", "ALLOW_AIRPRINT_CREDENTIAL_STORAGE");
                this.put("forceAirPrintTrustedTLSRequirement", "FORCE_AIRPRINT_TLS");
                this.put("allowAirPrintiBeaconDiscovery", "ALLOW_AIRPRINT_IBEACON_DISCOVERY");
                this.put("allowVPNCreation", "ALLOW_VPN_CREATION");
                this.put("allowRemoteScreenObservation", "ALLOW_CLASSROOM_REMOTEVIEW");
                this.put("forceClassroomUnpromptedScreenObservation", "FORCE_CLASSROOM_REMOTEVIEW");
                this.put("forceClassroomUnpromptedAppAndDeviceLock", "FORCE_CLASSROOM_APPDEVICELOCK");
                this.put("forceClassroomAutomaticallyJoinClasses", "FORCE_CLASSROOM_AUTO_JOIN");
                this.put("forceAuthenticationBeforeAutoFill", "AUTHENTICATE_BEFORE_AUTOFILL");
                this.put("forceClassroomRequestPermissionToLeaveClasses", "REQUEST_TO_LEAVE_CLASSROOM");
                this.put("allowProximitySetupToNewDevice", "ALLOW_PROXIMITY_FOR_NEWDEVICE");
                this.put("forceAutomaticDateAndTime", "FORCE_DATE_TIME");
                this.put("allowPasswordAutoFill", "ALLOW_PASSWORD_AUTOFILL");
                this.put("allowPasswordProximityRequests", "ALLOW_PASSWORD_PROXIMITY");
                this.put("allowPasswordSharing", "ALLOW_PASSWORD_SHARING");
                this.put("allowManagedToWriteUnmanagedContacts", "ALLOW_MANAGED_WRITE_UNMANAGED_CONTACT");
                this.put("allowUnmanagedToReadManagedContacts", "ALLOW_UNMANAGED_READ_MANAGED_CONTACT");
                this.put("allowUSBRestrictedMode", "ALLOW_USB_RESTRICTION_MODE");
                this.put("allowPersonalHotspotModification", "ALLOW_HOTSPOT_MODIFICATION");
                this.put("allowESIMModification", "ALLOW_ESIM_MODIFICATION");
                this.put("allowSiriServerLogging", "ALLOW_SIRI_LOGGING");
                this.put("allowAirPlayIncomingRequests", "AIRPLAY_INCOMING_REQUEST");
                this.put("allowDeviceSleep", "ALLOW_DEVICE_SLEEP");
                this.put("allowRemoteAppPairing", "ALLOW_REMOTE_APP_PAIRING");
                this.put("allowContinuousPathKeyboard", "ALLOW_CONTINUOUS_PATH_KEYBOARD");
                this.put("allowFindMyFriends", "ALLOW_FIND_MY_FRIEND");
                this.put("allowFindMyDevice", "ALLOW_FIND_MY_DEVICE");
                this.put("forceWiFiPowerOn", "FORCE_WIFI_ON");
                this.put("allowFilesUSBDriveAccess", "ALLOW_USB_FILE_DRIVE");
                this.put("allowSystemAppRemoval", "ALLOW_SYSTEM_APP_REMOVAL");
                this.put("allowNotificationsModification", "ALLOW_NOTIFICATION_MODIFICATION");
                this.put("allowAppClips", "ALLOW_APP_CLIPS");
                this.put("allowApplePersonalizedAdvertising", "ALLOW_APPLE_PERSONALIZED_ADS");
                this.put("allowCellularPlanModification", "ALLOW_CELLULAR_PLAN_MODIFICATION");
                this.put("allowFilesNetworkDriveAccess", "ALLOW_FILE_NETWORK_DRIVE_ACCESS");
                this.put("allowSharedDeviceTemporarySession", "ALLOW_SHARED_DEVICE_GUEST_ACCOUNT");
                this.put("allowNFC", "ALLOW_NFC");
                this.put("allowUnpairedExternalBootToRecovery", "ALLOW_UNPAIRED_EXTERNAL_BOOT_TO_RECOVERY");
                this.put("forceOnDeviceOnlyDictation", "FORCE_ON_DEVICE_ONLY_DICTATION");
                this.put("forceOnDeviceOnlyTranslation", "FORCE_ON_DEVICE_ONLY_TRANSLATION");
                this.put("requireManagedPasteboard", "REQUIRE_MANAGED_PASTEBOARD");
                this.put("allowCloudPrivateRelay", "ALLOW_CLOUD_PRIVATE_RELAY");
                this.put("allowMailPrivacyProtection", "ALLOW_MAIL_PRIVACY_PROTECTION");
                this.put("allowRapidSecurityResponseInstallation", "ALLOW_RAPID_SECURITY_RESPONSE_INSTALLATION");
                this.put("allowRapidSecurityResponseRemoval", "ALLOW_RAPID_SECURITY_RESPONSE_REMOVAL");
            }
        };
        (IOS_RESTRICTION_DEFAULT_VALUE = new HashMap<String, Integer>()).put("ALLOW_APP_INSTALLATION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_APP_REMOVAL", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_CAMERA", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_FACE_TIME", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_SCREEN_CAPTURE", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_SYNC_WHILE_ROAMING", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_VOICE_DIALING", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_INAPP_PURCHASE", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_GAME_CENTER", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_MULTIPLAYER_GAMEING", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_ADDING_GAME_FRIENDS", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_ENCRYPTED_BACKUP", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_PROFILE_INSTALLATION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_TOUCH_ID", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("SHOW_CONTROL_CENTER", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("SHOW_NOTIFICATION_CENTER", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("SHOW_TODAY_VIEW", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_OTA_PKI_UPDATES", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_PASSBOOK_WHEN_LOCKED", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_ISTORE_PWD_ENTRY", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_LIMITED_AD_TRACKING", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_ITUNES", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_SAFARI", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("SAFARI_ALLOW_AUTOFILL", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("SAFARI_FORCE_FRAUD_WARNING", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("SAFARI_ALLOW_JAVA_SCRIPT", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("SAFARI_ALLOW_POPUPS", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("SAFARI_ACCEPT_COOKIES", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_EXPLICIT_CONTENT", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_CLOUD_BACKUP", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_CLOUD_DOCUMENT_SYNC", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_PHOTO_STREAM", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_SHARED_STREAM", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_ASSISTANT", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_ASSIST_WHEN_LOCKED", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_UNTRUST_TLS_PROMPT", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_ASSISTANT_USER_CONTENT", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_AIRDROP", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_USE_OF_IBOOKSTORE", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_IBOOKSTORE_EROTICA_MEDIA", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_IMESSAGE", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_OPEN_DOC_IN_UNMANAGED", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_OPEN_DOC_IN_MANAGED", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_ACCOUNT_MODIFICATION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_HOST_PAIRING", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_APP_CELLULAR_DATA", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_ASSIST_PROFANITY_FILTER", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_DIAGNOSTIC_SUBMISSION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("MOVIES_RATING_VALUE", 1000);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("TV_SHOWS_RATING_VALUE", 1000);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("APPS_RATING_VALUE", 1000);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_FIND_MY_FRIENDS_MOD", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_MANAGED_APP_CLOUD_SYNC", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_MANAGED_BOOK_BACKUP", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_MANAGED_BOOK_SYNC", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_ACTIVITY_CONTINUATION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_ERASE_CONTENT_SETTINGS", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_ENABLING_RESTRICTION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_SPOTLIGHT_RESULT", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_CLOUD_KEYCHAIN_SYNC", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_AIRPLAY_OUTGOING_PWD", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_AIRPLAY_INCOMING_PWD", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_PODCASTS", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_DICTIONARY_LOOKUP", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_PREDICTIVE_KEYBOARD", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_AUTO_CORRECTION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_SPELLCHECK", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_WATCH_WRIST_DETECT", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_MODIFY_TOUCH_ID", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_AIRDROP_UNMANAGED", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_KEYBOARD_SHORTCUT", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_PAIRED_WATCH", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_MODIFI_PASSCODE", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_MODIFI_DEVICE_NAME", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_MODIFI_WALLPAPER", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_AUTO_APP_DOWNLOAD", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_MANAGED_APP_TRUST", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_CLOUD_PHOTO_LIB", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_NEWS", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_MUSIC_SERVICE", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_RADIO_SERVICE", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_DICTATION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_DIAG_SUB_MODIFICATION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_BLUETOOTH_MODIFICATION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_WIFI_WHITELISTING", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_AIRPRINT", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_AIRPRINT_CREDENTIAL_STORAGE", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_AIRPRINT_TLS", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_AIRPRINT_IBEACON_DISCOVERY", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_VPN_CREATION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_CLASSROOM_REMOTEVIEW", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_CLASSROOM_REMOTEVIEW", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_CLASSROOM_APPDEVICELOCK", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_CLASSROOM_AUTO_JOIN", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("AUTHENTICATE_BEFORE_AUTOFILL", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("REQUEST_TO_LEAVE_CLASSROOM", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_PROXIMITY_FOR_NEWDEVICE", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_DATE_TIME", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_PASSWORD_AUTOFILL", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_PASSWORD_PROXIMITY", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_PASSWORD_SHARING", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_MANAGED_WRITE_UNMANAGED_CONTACT", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_UNMANAGED_READ_MANAGED_CONTACT", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_USB_RESTRICTION_MODE", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_HOTSPOT_MODIFICATION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_ESIM_MODIFICATION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_SIRI_LOGGING", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("AIRPLAY_INCOMING_REQUEST", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_DEVICE_SLEEP", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_REMOTE_APP_PAIRING", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_CONTINUOUS_PATH_KEYBOARD", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_FIND_MY_FRIEND", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_FIND_MY_DEVICE", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_WIFI_ON", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_USB_FILE_DRIVE", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_SYSTEM_APP_REMOVAL", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_NOTIFICATION_MODIFICATION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_APP_CLIPS", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_APPLE_PERSONALIZED_ADS", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_CELLULAR_PLAN_MODIFICATION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_FILE_NETWORK_DRIVE_ACCESS", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_SHARED_DEVICE_GUEST_ACCOUNT", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_NFC", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_UNPAIRED_EXTERNAL_BOOT_TO_RECOVERY", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_ON_DEVICE_ONLY_DICTATION", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("FORCE_ON_DEVICE_ONLY_TRANSLATION", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("REQUIRE_MANAGED_PASTEBOARD", 2);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_CLOUD_PRIVATE_RELAY", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_MAIL_PRIVACY_PROTECTION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_RAPID_SECURITY_RESPONSE_INSTALLATION", 1);
        MDMInvDataPopulator.IOS_RESTRICTION_DEFAULT_VALUE.put("ALLOW_RAPID_SECURITY_RESPONSE_REMOVAL", 1);
    }
}
