package com.adventnet.sym.server.mdm.inv;

import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.sym.server.mdm.config.ProfileAssociateDataHandler;
import com.me.mdm.server.command.kiosk.KioskPauseResumeManager;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONArray;
import java.util.Iterator;
import java.util.ArrayList;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.sym.server.mdm.featuresettings.battery.MDMBatterySettingsDBHandler;
import com.me.devicemanagement.framework.server.util.Utils;
import org.json.JSONException;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class InventoryUtil
{
    private static InventoryUtil inventoryUtil;
    public Logger out;
    private Set<String> excludedRestrictionEntries;
    
    public InventoryUtil() {
        this.out = Logger.getLogger(InventoryUtil.class.getName());
        (this.excludedRestrictionEntries = new HashSet<String>()).addAll(Arrays.asList("DEVICE_ADMIN_ENABLED", "ALLOW_USB_MEDIA_PLAYER", "ALLOW_BACKGROUND_PROCESS_LIMIT", "ALLOW_CELLULAR_DATA", "ALLOW_USER_MOBILE_DATA_LIMIT", "ALLOW_STATUSBAR_EXPANSION", "ALLOW_LOCK_SCREEN_VIEW", "ALLOW_DISABLING_CELLULAR_DATA", "ALLOW_HOME_KEY", "ALLOW_KEYGUARD_CAMERA", "ALLOW_TRUST_AGENTS"));
    }
    
    public static InventoryUtil getInstance() {
        if (InventoryUtil.inventoryUtil == null) {
            InventoryUtil.inventoryUtil = new InventoryUtil();
        }
        return InventoryUtil.inventoryUtil;
    }
    
    public HashMap getScanStatusInfo(final long resourceID) {
        HashMap scanStatusHash = null;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("MdDeviceScanStatus", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject dataObject = SyMUtil.getPersistence().get("MdDeviceScanStatus", criteria);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("MdDeviceScanStatus");
                scanStatusHash = new HashMap();
                scanStatusHash.put("RESOURCE_ID", row.get("RESOURCE_ID"));
                scanStatusHash.put("SCAN_START_TIME", row.get("SCAN_START_TIME"));
                scanStatusHash.put("SCAN_END_TIME", row.get("SCAN_END_TIME"));
                scanStatusHash.put("LAST_SUCCESSFUL_SCAN", row.get("LAST_SUCCESSFUL_SCAN"));
                scanStatusHash.put("SCAN_STATUS", row.get("SCAN_STATUS"));
                scanStatusHash.put("REMARKS", row.get("REMARKS"));
                return scanStatusHash;
            }
        }
        catch (final Exception ex) {
            this.out.log(Level.WARNING, "MDM Inventory : Exception while getting scan status info", ex);
        }
        return null;
    }
    
    public String getInProgressDeviceCount(final Long customerID) {
        String inProgress = "false";
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceScanStatus"));
            query.addJoin(new Join("MdDeviceScanStatus", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("MdDeviceScanStatus", "RESOURCE_ID"));
            Criteria criteria = new Criteria(Column.getColumn("MdDeviceScanStatus", "SCAN_STATUS"), (Object)4, 0);
            if (customerID != null) {
                criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
            }
            query.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                inProgress = "true";
            }
        }
        catch (final Exception ex) {
            this.out.log(Level.WARNING, "Exception while getting InProgress count...", ex);
        }
        return inProgress;
    }
    
    private JSONObject getJSONfromDO(final DataObject dataObject, final JSONObject json, final String key) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getJSONfromDO()");
        try {
            JSONObject subJSON = new JSONObject();
            final List tableList = dataObject.getTableNames();
            for (int i = 0; i < tableList.size(); ++i) {
                final String tableName = tableList.get(i);
                final Row row = dataObject.getFirstRow(tableName);
                if (subJSON.length() == 0) {
                    subJSON = this.getJSONFromRow(row);
                }
                else {
                    JSONUtil.getInstance();
                    JSONUtil.putAll(subJSON, this.getJSONFromRow(row));
                }
            }
            if (key == null) {
                JSONUtil.getInstance();
                JSONUtil.putAll(json, subJSON);
            }
            else {
                json.put(key, (Object)subJSON);
            }
            this.out.log(Level.FINE, "Finished Exwcuting InventoryUtil.getJSONfromDO()");
            return json;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "DataAccessException while getting Device info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
        catch (final Exception exp2) {
            this.out.log(Level.WARNING, "Exception while creating hash from dataObject...", exp2);
            throw new SyMException(1002, (Throwable)exp2);
        }
    }
    
    private JSONObject getJSONfromRestrictionDO(final DataObject dataObject, final JSONObject json, final String key) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getJSONfromRestrictionDO()");
        try {
            JSONObject subJSON = new JSONObject();
            final List tableList = dataObject.getTableNames();
            for (int i = 0; i < tableList.size(); ++i) {
                final String tableName = tableList.get(i);
                final Row row = dataObject.getFirstRow(tableName);
                if (subJSON.length() == 0) {
                    subJSON = this.getJSONFromRestrictionRow(row);
                }
                else {
                    JSONUtil.getInstance();
                    JSONUtil.putAll(subJSON, this.getJSONFromRestrictionRow(row));
                }
            }
            if (key == null) {
                JSONUtil.getInstance();
                JSONUtil.putAll(json, subJSON);
            }
            else {
                json.put(key, (Object)subJSON);
            }
            this.out.log(Level.FINE, "Finished Exwcuting InventoryUtil.getJSONfromRestrictionDO()");
            return json;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "DataAccessException while getting Device info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
        catch (final Exception exp2) {
            this.out.log(Level.WARNING, "Exception while creating hash from dataObject...", exp2);
            throw new SyMException(1002, (Throwable)exp2);
        }
    }
    
    private JSONObject getJSONFromRow(final Row row) throws Exception {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getHashFromRow()");
        final JSONObject subJSON = new JSONObject();
        final List columnList = row.getColumns();
        for (int j = 0; j < columnList.size(); ++j) {
            final String columnName = columnList.get(j);
            final Object obj = row.get(columnName);
            if (obj != null) {
                subJSON.put(columnName, obj.toString().equals("") ? "--" : obj);
            }
            else {
                subJSON.put(columnName, (Object)"--");
            }
        }
        this.out.log(Level.FINE, "Finished Exwcuting InventoryUtil.getHashFromRow()");
        return subJSON;
    }
    
    private JSONObject getJSONFromRestrictionRow(final Row row) throws Exception {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getJSONFromRestrictionRow()");
        final JSONObject subJSON = new JSONObject();
        final List columnList = row.getColumns();
        for (int j = 0; j < columnList.size(); ++j) {
            final String columnName = columnList.get(j);
            final Object obj = row.get(columnName);
            if (obj != null) {
                if (obj instanceof Boolean) {
                    if (!columnName.equalsIgnoreCase("RESOURCE_ID") && !this.excludedRestrictionEntries.contains(columnName)) {
                        subJSON.put(columnName, obj);
                    }
                }
                else if (obj instanceof Integer && !columnName.equalsIgnoreCase("RESOURCE_ID") && (int)obj != -1 && !this.excludedRestrictionEntries.contains(columnName)) {
                    subJSON.put(columnName, obj.toString().equals("") ? "--" : obj);
                }
            }
        }
        this.out.log(Level.FINE, "Finished Exwcuting InventoryUtil.getJSONFromRestrictionRow()");
        return subJSON;
    }
    
    private HashMap getHashFromDO(final DataObject dataObject, HashMap detailHash, final String type) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getHashFromDO()");
        try {
            final List tableList = dataObject.getTableNames();
            for (int i = 0; i < tableList.size(); ++i) {
                final String tableName = tableList.get(i);
                final Row row = dataObject.getFirstRow(tableName);
                detailHash = this.getHashFromRow(tableName, row, detailHash, type);
            }
            this.out.log(Level.FINE, "Finished Exwcuting InventoryUtil.getHashFromDO()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "DataAccessException while getting Device info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
        catch (final Exception exp2) {
            this.out.log(Level.WARNING, "Exception while creating hash from dataObject...", exp2);
            throw new SyMException(1002, (Throwable)exp2);
        }
    }
    
    private HashMap getHashFromRow(final String tableName, final Row row, final HashMap detailHash, final String type) throws SyMException, DataAccessException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getHashFromRow()");
        final List columnList = row.getColumns();
        for (int j = 0; j < columnList.size(); ++j) {
            String columnName = columnList.get(j);
            final Object obj = row.get(columnName);
            columnName = type + "_" + tableName + "__" + columnName;
            if (obj != null) {
                detailHash.put(columnName, obj.toString().equals("") ? "--" : obj);
            }
            else {
                detailHash.put(columnName, "--");
            }
        }
        this.out.log(Level.FINE, "Finished Exwcuting InventoryUtil.getHashFromRow()");
        return detailHash;
    }
    
    public org.json.simple.JSONObject getJsonFromDO(final DataObject dataObject) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getJsonFromDO()");
        try {
            org.json.simple.JSONObject detailJson = null;
            final List tableList = dataObject.getTableNames();
            for (int i = 0; i < tableList.size(); ++i) {
                final String tableName = tableList.get(i);
                final Row row = dataObject.getFirstRow(tableName);
                detailJson = this.getJsonFromRow(row);
            }
            this.out.log(Level.FINE, "Finished Executing JSONUtil.getJsonFromDO()");
            return detailJson;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "DataAccessException while getting Device info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
        catch (final Exception exp2) {
            this.out.log(Level.WARNING, "Exception while creating json from dataObject...", exp2);
            throw new SyMException(1002, (Throwable)exp2);
        }
    }
    
    public org.json.simple.JSONObject getJsonFromRow(final Row row) throws SyMException, DataAccessException, JSONException {
        final org.json.simple.JSONObject detailJson = new org.json.simple.JSONObject();
        final List columnList = row.getColumns();
        for (int j = 0; j < columnList.size(); ++j) {
            String columnName = columnList.get(j);
            final Object obj = row.get(columnName);
            columnName = columnName;
            if (obj != null) {
                detailJson.put((Object)columnName, obj.toString().equals("") ? "--" : obj);
            }
            else {
                detailJson.put((Object)columnName, (Object)"--");
            }
        }
        this.out.log(Level.FINE, "Finished Executing JSONUtil.getJsonFromRow()");
        return detailJson;
    }
    
    public DataObject getDeviceInfo(final long resourceID) throws SyMException, DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        query.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("Resource", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        query.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("Resource", "NAME"));
        query.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_ID"));
        query.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_TYPE"));
        query.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL"));
        query.addSelectColumn(Column.getColumn("MdModelInfo", "MODEL_NAME"));
        query.addSelectColumn(Column.getColumn("MdModelInfo", "MANUFACTURER"));
        query.addSelectColumn(Column.getColumn("MdModelInfo", "PRODUCT_NAME"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "IMEI"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_NAME"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "FIRMWARE_VERSION"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_SUPERVISED"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_PROFILEOWNER"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_DEVICE_LOCATOR_ENABLED"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_ACTIVATION_LOCK_ENABLED"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_DND_IN_EFFECT"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "IS_ITUNES_ACCOUNT_ACTIVE"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "EAS_DEVICE_IDENTIFIER"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID", "ManagedDevice.RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "UNREGISTERED_TIME"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "REGISTERED_TIME"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "AGENT_VERSION"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "*"));
        final Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return MDMUtil.getPersistence().get(query);
    }
    
    public HashMap getDeviceInfo(final long resourceID, HashMap detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getDeviceInfo()");
        try {
            final DataObject dataObject = this.getDeviceInfo(resourceID);
            detailHash = this.getHashFromDO(dataObject, detailHash, "Device");
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Device info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getDeviceInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Device info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getDeviceInfo(final long resourceID, JSONObject detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getDeviceInfo()");
        try {
            final DataObject dataObject = this.getDeviceInfo(resourceID);
            detailHash = this.getJSONfromDO(dataObject, detailHash, null);
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Device info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getDeviceInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Device info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    private void convertNeededValuesInInvDetailsHash(final HashMap detailHash, final String type) {
        final String columnName = type + "_" + "MdDeviceInfo" + "__" + "LAST_CLOUD_BACKUP_DATE";
        final Object lastCloudBackUpDate = detailHash.get(columnName);
        if (lastCloudBackUpDate != null && !lastCloudBackUpDate.toString().equals("--")) {
            final Long lastCloudBackUpDateLong = (Long)lastCloudBackUpDate;
            final String time = Utils.getTime(lastCloudBackUpDateLong);
            detailHash.put(columnName, time);
        }
    }
    
    public DataObject getDeviceDetailedInfo(final long resourceID) throws SyMException, DataAccessException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getDeviceDetailedInfo()");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        query.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("Resource", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        query.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        query.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        query.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("Resource", "NAME"));
        query.addSelectColumn(Column.getColumn("MdModelInfo", "*"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "*"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "*"));
        query.addSelectColumn(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY", "DEVICE_OWNED_BY"));
        final Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        return dataObject;
    }
    
    public HashMap getDeviceDetailedInfo(final long resourceID, HashMap detailHash) throws SyMException {
        try {
            final DataObject dataObject = this.getDeviceDetailedInfo(resourceID);
            detailHash = this.getHashFromDO(dataObject, detailHash, "Device");
            this.convertNeededValuesInInvDetailsHash(detailHash, "Device");
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Device Detailed info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getDeviceDetailedInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Device Detailed info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getDeviceDetailedInfo(final long resourceID, JSONObject detailHash) throws SyMException {
        try {
            this.out.log(Level.INFO, "Going to get device detailed info");
            final DataObject dataObject = this.getDeviceDetailedInfo(resourceID);
            detailHash = this.getJSONfromDO(dataObject, detailHash, null);
            try {
                this.out.log(Level.INFO, "Going to fetch battery last sync time");
                final Long deviceTime = MDMBatterySettingsDBHandler.getBatteryLastUpdatedTime(resourceID);
                detailHash.put("device_last_sync_time", (Object)deviceTime);
                this.out.log(Level.INFO, "Successfully added battery last sync time");
            }
            catch (final Exception e) {
                this.out.log(Level.WARNING, "Exception while adding battery last sync time", e);
            }
            if (detailHash.opt("EXTERNAL_CAPACITY").equals(0.0f)) {
                detailHash.remove("EXTERNAL_CAPACITY");
                detailHash.remove("AVAILABLE_EXTERNAL_CAPACITY");
                detailHash.remove("USED_EXTERNAL_SPACE");
            }
            if (MDMUtil.isStringEmpty(detailHash.optString("MODEM_FIRMWARE_VERSION"))) {
                detailHash.remove("MODEM_FIRMWARE_VERSION");
            }
            if (MDMUtil.isStringEmpty(detailHash.optString("IS_CLOUD_BACKUP_ENABLED"))) {
                detailHash.remove("IS_CLOUD_BACKUP_ENABLED");
                detailHash.remove("LAST_CLOUD_BACKUP_DATE");
            }
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Device Detailed info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getDeviceDetailedInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Device Detailed info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public DataObject getNetworkInfo(final long resourceID) throws SyMException, DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdNetworkInfo"));
        query.addSelectColumn(Column.getColumn("MdNetworkInfo", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("MdNetworkInfo", "BLUETOOTH_MAC"));
        query.addSelectColumn(Column.getColumn("MdNetworkInfo", "WIFI_MAC"));
        query.addSelectColumn(Column.getColumn("MdNetworkInfo", "ETHERNET_MACS"));
        query.addSelectColumn(Column.getColumn("MdNetworkInfo", "IS_PERSONAL_HOTSPOT_ENABLED"));
        final Criteria criteria = new Criteria(Column.getColumn("MdNetworkInfo", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return MDMUtil.getPersistence().get(query);
    }
    
    public HashMap getNetworkInfo(final long resourceID, HashMap detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getNetworkInfo()");
        try {
            detailHash = this.getHashFromDO(this.getNetworkInfo(resourceID), detailHash, "Network");
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Network info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getNetworkInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Network Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getNetworkInfo(final long resourceID, JSONObject detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getNetworkInfo()");
        try {
            detailHash = this.getJSONfromDO(this.getNetworkInfo(resourceID), detailHash, "network");
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Network info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getNetworkInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Network Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public DataObject getSharedDeviceInfo(final long resourceId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceInfo"));
        selectQuery.addJoin(new Join("MdDeviceInfo", "MdSharedDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addSelectColumn(new Column("MdSharedDeviceInfo", "*"));
        final Criteria multiUserCriteria = new Criteria(new Column("MdDeviceInfo", "IS_MULTIUSER"), (Object)true, 0);
        final Criteria resourceCriteria = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0);
        selectQuery.setCriteria(multiUserCriteria.and(resourceCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        return dataObject;
    }
    
    public DataObject getNetworkDetailedInfo(final long resourceID) throws SyMException, DataAccessException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getNetworkDetailedInfo()");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdNetworkInfo"));
        query.addSelectColumn(Column.getColumn("MdNetworkInfo", "*"));
        final Criteria criteria = new Criteria(Column.getColumn("MdNetworkInfo", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return MDMUtil.getPersistence().get(query);
    }
    
    public HashMap getNetworkDetailedInfo(final long resourceID, HashMap detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getNetworkDetailedInfo()");
        try {
            final DataObject dataObject = this.getNetworkDetailedInfo(resourceID);
            detailHash = this.getHashFromDO(dataObject, detailHash, "Network");
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Network Detailed Info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getNetworkDetailedInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Network Detailed Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getNetworkDetailedInfo(final long resourceID, JSONObject detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getNetworkDetailedInfo()");
        try {
            final DataObject dataObject = this.getNetworkDetailedInfo(resourceID);
            detailHash = this.getJSONfromDO(dataObject, detailHash, "network");
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Network Detailed Info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getNetworkDetailedInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Network Detailed Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getWorkDataSecurityInfo(final long resourceID, JSONObject detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getWorkDataSecurityInfo()");
        try {
            final DataObject dataObject = this.getWorkDataSecurityInfoDo(resourceID);
            detailHash = this.getJSONfromDO(dataObject, detailHash, "work_data_security");
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Work Data Security Info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getWorkDataSecurityInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Work Data Security Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public DataObject getWorkDataSecurityInfoDo(final long resourceID) throws SyMException, DataAccessException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getWorkDataSecurityInfoInfo()");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdWorkDataSecurity"));
        query.addSelectColumn(Column.getColumn("MdWorkDataSecurity", "*"));
        final Criteria criteria = new Criteria(Column.getColumn("MdWorkDataSecurity", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return MDMUtil.getPersistence().get(query);
    }
    
    public JSONObject getSharedDeviceDetails(final Long resourceId, JSONObject sharedJSON) throws SyMException {
        try {
            final DataObject dataObject = this.getSharedDeviceInfo(resourceId);
            sharedJSON = this.getJSONfromDO(dataObject, sharedJSON, "shared_device_details");
            return sharedJSON;
        }
        catch (final DataAccessException e) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Shared device Detailed Info...", (Throwable)e);
            throw new SyMException(1001, (Throwable)e);
        }
    }
    
    public DataObject getSimInfo(final long resourceID) throws SyMException, DataAccessException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getSimDetailedInfo()");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdSIMInfo"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "SIM_ID"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "ICCID"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "IMEI"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "IMSI"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "CURRENT_CARRIER_NETWORK"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "SUBSCRIBER_CARRIER_NETWORK"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "PHONE_NUMBER"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "CARRIER_SETTING_VERSION"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "CURRENT_MCC"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "CURRENT_MNC"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "SUBSCRIBER_MCC"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "SUBSCRIBER_MNC"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "IS_ROAMING"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "SLOT"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "LABEL"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "LABEL_ID"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "IS_DATA_PREFERRED"));
        query.addSelectColumn(Column.getColumn("MdSIMInfo", "IS_VOICE_PREFERRED"));
        final Criteria criteria = new Criteria(Column.getColumn("MdSIMInfo", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        final SortColumn sortCol = new SortColumn(Column.getColumn("MdSIMInfo", "SIM_ID"), true);
        query.addSortColumn(sortCol);
        return MDMUtil.getPersistence().get(query);
    }
    
    public HashMap getSimInfo(final long resourceID, final HashMap detailHash) throws SyMException {
        try {
            final DataObject dataObject = this.getSimInfo(resourceID);
            final Iterator simItr = dataObject.getRows("MdSIMInfo");
            HashMap simHash = null;
            final ArrayList simList = new ArrayList();
            while (simItr.hasNext()) {
                simHash = new HashMap();
                final Row simRow = simItr.next();
                simHash = this.getHashFromRow("MdSIMInfo", simRow, simHash, "Sim");
                simList.add(simHash);
            }
            detailHash.put("simList", simList);
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Sim Detailed Info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getSimDetailedInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Sim Detailed Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getSimInfo(final long resourceID, final JSONObject detailHash) throws SyMException {
        try {
            final DataObject dataObject = this.getSimInfo(resourceID);
            final Iterator simItr = dataObject.getRows("MdSIMInfo");
            final JSONArray simList = new JSONArray();
            while (simItr.hasNext()) {
                final Row simRow = simItr.next();
                final JSONObject simHash = this.getJSONFromRow(simRow);
                simList.put((Object)simHash);
            }
            detailHash.put("sims", (Object)simList);
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Sim Detailed Info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getSimDetailedInfo()");
            return detailHash;
        }
        catch (final Exception exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Sim Detailed Info...", exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public DataObject getOSInfo(final long resourceID) throws SyMException, DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        query.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_VERSION"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "BUILD_VERSION"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "OS_NAME"));
        final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return MDMUtil.getPersistence().get(query);
    }
    
    public HashMap getOSInfo(final long resourceID, HashMap detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getOSInfo()");
        try {
            final DataObject dataObject = this.getOSInfo(resourceID);
            detailHash = this.getHashFromDO(dataObject, detailHash, "OS");
            final String osPlatformKey = "OS_ManagedDevice__PLATFORM_TYPE";
            final Object objOSPlatform = detailHash.get(osPlatformKey);
            if (objOSPlatform != null) {
                final int osPlatform = (int)objOSPlatform;
                final String platformName = MDMUtil.getInstance().getPlatformName(osPlatform);
                detailHash.put(osPlatformKey, platformName);
            }
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for OS info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getOSInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting OS Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getOSInfo(final long resourceID, JSONObject detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getOSInfo()");
        try {
            final DataObject dataObject = this.getOSInfo(resourceID);
            detailHash = this.getJSONfromDO(dataObject, detailHash, "os");
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for OS info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getOSInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting OS Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public DataObject getSecurityInfo(final long resourceID) throws SyMException, DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdSecurityInfo"));
        query.addSelectColumn(Column.getColumn("MdSecurityInfo", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("MdSecurityInfo", "HARDWARE_ENCRYPTION_CAPS"));
        query.addSelectColumn(Column.getColumn("MdSecurityInfo", "PASSCODE_COMPLAINT"));
        query.addSelectColumn(Column.getColumn("MdSecurityInfo", "PASSCODE_COMPLAINT_PROFILES"));
        query.addSelectColumn(Column.getColumn("MdSecurityInfo", "PASSCODE_PRESENT"));
        query.addSelectColumn(Column.getColumn("MdSecurityInfo", "DEVICE_ROOTED"));
        query.addSelectColumn(Column.getColumn("MdSecurityInfo", "STORAGE_ENCRYPTION"));
        query.addSelectColumn(Column.getColumn("MdSecurityInfo", "EXTERNAL_STORAGE_ENCRYPTION"));
        query.addSelectColumn(Column.getColumn("MdSecurityInfo", "EFRP_STATUS"));
        query.addSelectColumn(Column.getColumn("MdSecurityInfo", "PLAY_PROTECT"));
        final Criteria criteria = new Criteria(Column.getColumn("MdSecurityInfo", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return MDMUtil.getPersistence().get(query);
    }
    
    public DataObject getAgentContactInfo(final long resourceID) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AgentContact"));
        query.addSelectColumn(Column.getColumn("AgentContact", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"));
        final Criteria criteria = new Criteria(Column.getColumn("AgentContact", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return MDMUtil.getPersistence().get(query);
    }
    
    public DataObject getDeviceUserInfo(final long resourceID) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        final Join device_userid = new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1);
        final Join userdetails = new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1);
        final Join userresource = new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 1);
        query.addJoin(device_userid);
        query.addJoin(userdetails);
        query.addJoin(userresource);
        query.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        query.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
        query.addSelectColumn(Column.getColumn("ManagedUser", "FIRST_NAME"));
        query.addSelectColumn(Column.getColumn("ManagedUser", "MIDDLE_NAME"));
        query.addSelectColumn(Column.getColumn("ManagedUser", "LAST_NAME"));
        query.addSelectColumn(Column.getColumn("ManagedUser", "PHONE_NUMBER"));
        query.addSelectColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"));
        query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("Resource", "NAME"));
        final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1).or(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)null, 0));
        query.setCriteria(criteria.and(userNotInTrashCriteria));
        return MDMUtil.getPersistence().get(query);
    }
    
    public HashMap getSecurityInfo(final long resourceID, HashMap detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getSecurityInfo()");
        try {
            final DataObject dataObject = this.getSecurityInfo(resourceID);
            detailHash = this.getHashFromDO(dataObject, detailHash, "Security");
            try {
                detailHash.put("EFRP_ACCOUNT_DETAILS", this.getEFRPInfoString(resourceID));
            }
            catch (final Exception e) {
                this.out.log(Level.SEVERE, "Exception in fetching EFRP Details ", e);
            }
            try {
                detailHash.put("SAFETYNET_DETAILS", this.getSafetyNetDetailsString(resourceID));
            }
            catch (final Exception e) {
                this.out.log(Level.SEVERE, "Exception in fetching safetynet  Details ", e);
            }
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Security info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getSecurityInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Security Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getSecurityInfo(final long resourceID, JSONObject detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getSecurityInfo()");
        try {
            final DataObject dataObject = this.getSecurityInfo(resourceID);
            detailHash = this.getJSONfromDO(dataObject, detailHash, "security");
            try {
                final JSONObject securityJSON = detailHash.getJSONObject("security");
                securityJSON.put("EFRP_ACCOUNT_DETAILS", (Object)this.getEFRPInfo(resourceID));
                this.getSafetyNetDetails(resourceID, securityJSON);
                detailHash.put("security", (Object)securityJSON);
            }
            catch (final Exception e) {
                this.out.log(Level.SEVERE, "Exception in fetching EFRP Details ", e);
            }
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Security info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getSecurityInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Security Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getAgentContactInfo(final long resourceID, JSONObject detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getAgentContactInfo()");
        try {
            final DataObject dataObject = this.getAgentContactInfo(resourceID);
            detailHash = this.getJSONfromDO(dataObject, detailHash, "agent_contact_info");
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Security info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getAgentContactInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Security Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getDeviceUserInfo(final long resourceID, JSONObject detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getDeviceUserDetails()");
        try {
            final DataObject dataObject = this.getDeviceUserInfo(resourceID);
            detailHash = this.getJSONfromDO(dataObject, detailHash, "user");
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Security info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getDeviceUserDetails()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Security Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public HashMap getDetailedDiskInfo(final Long resourceID) {
        this.out.log(Level.FINE, "Inventory Management : Entered into InvReportsUtil.getDiskInfo()");
        final HashMap diskHash = new HashMap();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "DEVICE_CAPACITY"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "AVAILABLE_DEVICE_CAPACITY"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "USED_DEVICE_SPACE"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "EXTERNAL_CAPACITY"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "AVAILABLE_EXTERNAL_CAPACITY"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "USED_EXTERNAL_SPACE"));
            final Criteria criteria = new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceID, 0);
            if (criteria != null) {
                query.setCriteria(criteria);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final Row row = dataObject.getFirstRow("MdDeviceInfo");
            final float internalTotalSize = (float)row.get("DEVICE_CAPACITY");
            final float internalFreeSpace = (float)row.get("AVAILABLE_DEVICE_CAPACITY");
            final float internalUsedSpace = (float)row.get("USED_DEVICE_SPACE");
            float externalTotalSize = (float)row.get("EXTERNAL_CAPACITY");
            externalTotalSize = ((externalTotalSize > -1.0) ? externalTotalSize : 0.0f);
            float externalFreeSpace = (float)row.get("AVAILABLE_EXTERNAL_CAPACITY");
            externalFreeSpace = ((externalFreeSpace > -1.0) ? externalFreeSpace : 0.0f);
            float externalUsedSpace = (float)row.get("USED_EXTERNAL_SPACE");
            externalUsedSpace = ((externalUsedSpace > -1.0) ? externalUsedSpace : 0.0f);
            final float totalSize = internalTotalSize + externalTotalSize;
            final float usedSpace = internalUsedSpace + externalUsedSpace;
            final float freeSpace = totalSize - usedSpace;
            diskHash.put("internalTotalSize", Math.round((double)internalTotalSize));
            diskHash.put("internalFreeSpace", Math.round((double)internalFreeSpace));
            diskHash.put("internalUsedSpace", Math.round((double)internalUsedSpace));
            diskHash.put("externalTotalSize", Math.round((double)externalTotalSize));
            diskHash.put("externalFreeSpace", Math.round((double)externalFreeSpace));
            diskHash.put("externalUsedSpace", Math.round((double)externalUsedSpace));
            diskHash.put("totalSize", Math.round((double)totalSize));
            diskHash.put("freeSpace", Math.round((double)freeSpace));
            diskHash.put("usedSpace", Math.round((double)usedSpace));
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "Inventory Management : DataAccessException while getting disk info...", (Throwable)exp);
        }
        return diskHash;
    }
    
    public HashMap getDiskInfo(final long resourceID) throws SyMException {
        this.out.log(Level.FINE, "Inventory Management : Entered into InvReportsUtil.getDiskInfo()");
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "DEVICE_CAPACITY"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "AVAILABLE_DEVICE_CAPACITY"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "USED_DEVICE_SPACE"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "EXTERNAL_CAPACITY"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "AVAILABLE_EXTERNAL_CAPACITY"));
            query.addSelectColumn(Column.getColumn("MdDeviceInfo", "USED_EXTERNAL_SPACE"));
            final Criteria criteria = new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceID, 0);
            if (criteria != null) {
                query.setCriteria(criteria);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final HashMap diskHash = new HashMap();
            final Row row = dataObject.getFirstRow("MdDeviceInfo");
            final float internalTotalSize = (float)row.get("DEVICE_CAPACITY");
            final float internalFreeSpace = (float)row.get("AVAILABLE_DEVICE_CAPACITY");
            final float internalUsedSpace = (float)row.get("USED_DEVICE_SPACE");
            float externalTotalSize = (float)row.get("EXTERNAL_CAPACITY");
            externalTotalSize = ((externalTotalSize > -1.0) ? externalTotalSize : 0.0f);
            float externalFreeSpace = (float)row.get("AVAILABLE_EXTERNAL_CAPACITY");
            externalFreeSpace = ((externalFreeSpace > -1.0) ? externalFreeSpace : 0.0f);
            float externalUsedSpace = (float)row.get("USED_EXTERNAL_SPACE");
            externalUsedSpace = ((externalUsedSpace > -1.0) ? externalUsedSpace : 0.0f);
            final long totalSize = Math.round((double)(internalTotalSize + externalTotalSize));
            final long usedSpace = Math.round((double)(internalUsedSpace + externalUsedSpace));
            final long freeSpace = totalSize - usedSpace;
            diskHash.put("totalSize", totalSize);
            diskHash.put("freeSpace", freeSpace);
            diskHash.put("usedSpace", usedSpace);
            this.out.log(Level.FINE, "InventoryUtil : Disk Info Hash : {0}", diskHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getDiskInfo()");
            return diskHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "Inventory Management : DataAccessException while getting disk info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public org.json.simple.JSONObject getRestrictions(final long resourceID, final String tableName) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getDeviceRestrictions()");
        try {
            org.json.simple.JSONObject restDetailJson = new org.json.simple.JSONObject();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
            query.addSelectColumn(Column.getColumn(tableName, "*"));
            final Criteria criteria = new Criteria(Column.getColumn(tableName, "RESOURCE_ID"), (Object)resourceID, 0);
            if (criteria != null) {
                query.setCriteria(criteria);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            restDetailJson = this.getJsonFromDO(dataObject);
            this.out.log(Level.FINE, "InventoryUtil : JSON Data  for Device Restriction info : {0}", restDetailJson);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getDeviceRestrictions()");
            return restDetailJson;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Device Restriction info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public org.json.simple.JSONObject getIOSRestrictionDetails(final long resourceID) throws SyMException {
        this.out.log(Level.INFO, "MDM : Device Details : Request for Restriction details");
        try {
            org.json.simple.JSONObject restDetailJson = null;
            restDetailJson = getInstance().getRestrictions(resourceID, "MdIOSRestriction");
            if (restDetailJson != null && !restDetailJson.isEmpty()) {
                final String movieRating = (String)DBUtil.getValueFromDB("MdMoviesRating", "MOVIES_RATING_VALUE", restDetailJson.get((Object)"MOVIES_RATING_VALUE"), "MOVIES_RATING");
                restDetailJson.put((Object)"MOVIES_RATING_VALUE", (Object)I18N.getMsg(movieRating, new Object[0]));
                final String tvShowRating = (String)DBUtil.getValueFromDB("MdTvShowsRating", "TV_SHOWS_RATING_VALUE", restDetailJson.get((Object)"TV_SHOWS_RATING_VALUE"), "TV_SHOWS_RATING");
                restDetailJson.put((Object)"TV_SHOWS_RATING_VALUE", (Object)I18N.getMsg(tvShowRating, new Object[0]));
                final String appsRating = (String)DBUtil.getValueFromDB("MdAppsRating", "APPS_RATING_VALUE", restDetailJson.get((Object)"APPS_RATING_VALUE"), "APPS_RATING");
                restDetailJson.put((Object)"APPS_RATING_VALUE", (Object)I18N.getMsg(appsRating, new Object[0]));
            }
            this.out.log(Level.INFO, "MDM : detailjson for Restriction info {0}", restDetailJson);
            this.out.log(Level.INFO, "MDM : Finished Executing InventoryComputerDetails.getRestrictionDetails()");
            return restDetailJson;
        }
        catch (final Exception exp) {
            this.out.log(Level.WARNING, "MDM : Exception while getting computer summary details...", exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public HashMap getAndroidDeviceRestrictions(final long resourceID, HashMap restDetailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getDeviceRestrictions()");
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdRestriction"));
            query.addSelectColumn(Column.getColumn("MdRestriction", "*"));
            final Criteria criteria = new Criteria(Column.getColumn("MdRestriction", "RESOURCE_ID"), (Object)resourceID, 0);
            if (criteria != null) {
                query.setCriteria(criteria);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            restDetailHash = this.getHashFromDO(dataObject, restDetailHash, "DeviceRest");
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Device Restriction info : {0}", restDetailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getDeviceRestrictions()");
            return restDetailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Device Restriction info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public DataObject getSafeDeviceRestriction(final long resourceID) throws SyMException, DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdRestriction"));
        query.addSelectColumn(new Column("MdRestriction", "*"));
        final Criteria criteria = new Criteria(Column.getColumn("MdRestriction", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria cScope = new Criteria(Column.getColumn("MdRestriction", "SCOPE"), (Object)0, 0);
        if (criteria != null) {
            query.setCriteria(criteria.and(cScope));
        }
        return MDMUtil.getPersistence().get(query);
    }
    
    public HashMap getSafeDeviceRestriction(final long resourceID, HashMap restDetailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getSafeDeviceRestriction()");
        try {
            final DataObject dataObject = this.getSafeDeviceRestriction(resourceID);
            restDetailHash = this.getHashFromDO(dataObject, restDetailHash, "DeviceRest");
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getSafeDeviceRestriction()");
            return restDetailHash;
        }
        catch (final Exception ex) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Safe Device Restriction info...", ex);
            throw new SyMException(1001, (Throwable)ex);
        }
    }
    
    public JSONObject getSafeDeviceRestriction(final long resourceID, JSONObject restDetailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getSafeDeviceRestriction()");
        try {
            final DataObject dataObject = this.getSafeDeviceRestriction(resourceID);
            restDetailHash = this.getJSONfromRestrictionDO(dataObject, restDetailHash, null);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getSafeDeviceRestriction()");
            return restDetailHash;
        }
        catch (final Exception ex) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Safe Device Restriction info...", ex);
            throw new SyMException(1001, (Throwable)ex);
        }
    }
    
    public DataObject getSafeKnoxDeviceRestriction(final long resourceID) throws SyMException, DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdRestriction"));
        query.addSelectColumn(new Column("MdRestriction", "*"));
        final Criteria criteria = new Criteria(Column.getColumn("MdRestriction", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria cScope = new Criteria(Column.getColumn("MdRestriction", "SCOPE"), (Object)1, 0);
        query.setCriteria(criteria.and(cScope));
        return MDMUtil.getPersistence().get(query);
    }
    
    public HashMap getSafeKnoxDeviceRestriction(final long resourceID, HashMap restDetailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getSafeDeviceRestriction()");
        try {
            final DataObject dataObject = this.getSafeKnoxDeviceRestriction(resourceID);
            restDetailHash = this.getHashFromDO(dataObject, restDetailHash, "DeviceRest");
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getSafeKnoxDeviceRestriction()");
            return restDetailHash;
        }
        catch (final Exception ex) {
            this.out.log(Level.WARNING, "nventoryUtil : DataAccessException while getting Knox Device Restriction info...", ex);
            throw new SyMException(1001, (Throwable)ex);
        }
    }
    
    public JSONObject getSafeKnoxDeviceRestriction(final long resourceID, JSONObject restDetailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getSafeDeviceRestriction()");
        try {
            final DataObject dataObject = this.getSafeKnoxDeviceRestriction(resourceID);
            restDetailHash = this.getJSONfromRestrictionDO(dataObject, restDetailHash, null);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getSafeKnoxDeviceRestriction()");
            return restDetailHash;
        }
        catch (final Exception ex) {
            this.out.log(Level.WARNING, "nventoryUtil : DataAccessException while getting Knox Device Restriction info...", ex);
            throw new SyMException(1001, (Throwable)ex);
        }
    }
    
    public boolean isScanInProgress(final List clientsList) {
        boolean inProgress = false;
        try {
            this.updateScanStatus(4, 0, "dc.db.mdm.scan.remarks.scan_time_out", 9101L);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            query.addJoin(new Join("ManagedDevice", "MdDeviceScanStatus", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            Criteria enrolledCriteria = ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria();
            if (clientsList != null) {
                final Criteria resourceIdCriteria = new Criteria(Column.getColumn("MdDeviceScanStatus", "RESOURCE_ID"), (Object)clientsList.toArray(), 8);
                enrolledCriteria = enrolledCriteria.and(resourceIdCriteria);
            }
            Criteria scanStatusCriteria = new Criteria(Column.getColumn("MdDeviceScanStatus", "SCAN_STATUS"), (Object)1, 0);
            final Criteria scanStatusInProgresCriteria = new Criteria(Column.getColumn("MdDeviceScanStatus", "SCAN_STATUS"), (Object)4, 0);
            scanStatusCriteria = scanStatusCriteria.or(scanStatusInProgresCriteria);
            enrolledCriteria = enrolledCriteria.and(scanStatusCriteria);
            query.setCriteria(enrolledCriteria);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                inProgress = true;
            }
        }
        catch (final Exception exp) {
            this.out.log(Level.WARNING, "InventoryUtil : getScanResult : Exception while getting scan info hash...", exp);
        }
        return inProgress;
    }
    
    private void updateScanStatus(final int scanStateCriteria, final int currentScanStatus, final String remarks, final long errorCode) {
        try {
            final long currentTime = System.currentTimeMillis();
            final long elapsedTime = currentTime - 600000L;
            final UpdateQuery resetQuery = (UpdateQuery)new UpdateQueryImpl("MdDeviceScanStatus");
            Criteria stateCri = new Criteria(Column.getColumn("MdDeviceScanStatus", "SCAN_STATUS"), (Object)new Integer(scanStateCriteria), 0);
            final Criteria timeoutCri = new Criteria(Column.getColumn("MdDeviceScanStatus", "SCAN_START_TIME"), (Object)new Long(elapsedTime), 6);
            stateCri = timeoutCri.and(stateCri);
            final List scanTimeList = DBUtil.getDistinctColumnValue("MdDeviceScanStatus", "RESOURCE_ID", stateCri);
            resetQuery.setCriteria(stateCri);
            resetQuery.setUpdateColumn("SCAN_STATUS", (Object)new Integer(currentScanStatus));
            resetQuery.setUpdateColumn("SCAN_END_TIME", (Object)new Long(currentTime));
            resetQuery.setUpdateColumn("REMARKS", (Object)remarks);
            MDMUtil.getPersistence().update(resetQuery);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public HashMap getScanResult(final Long resourceID) {
        final HashMap scanInfoHash = new HashMap();
        try {
            final Criteria criteria = new Criteria(Column.getColumn("MdDeviceScanStatus", "RESOURCE_ID"), (Object)resourceID, 0);
            final DataObject dobj = MDMUtil.getPersistence().get("MdDeviceScanStatus", criteria);
            String StatusStr = "success";
            final int scanStatus = (int)dobj.getFirstValue("MdDeviceScanStatus", "SCAN_STATUS");
            if (scanStatus == 0) {
                StatusStr = "failed";
            }
            if (scanStatus == 1 || scanStatus == 4) {
                scanInfoHash.put("inProgress", "true");
            }
            else {
                scanInfoHash.put("inProgress", "false");
            }
            final String remarks = (String)dobj.getFirstValue("MdDeviceScanStatus", "REMARKS");
            scanInfoHash.put("SCAN_REMARKS", remarks);
            scanInfoHash.put("SCAN_STATUS", StatusStr);
        }
        catch (final Exception exp) {
            this.out.log(Level.WARNING, "InventoryUtil : getScanResult : Exception while getting scan info hash...", exp);
        }
        return scanInfoHash;
    }
    
    public List getAllApplicationName(final String filterChar, final Long customerID) {
        List appList = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppDetails"));
            query.addJoin(new Join("MdAppDetails", "MdInstalledAppResourceRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            final SortColumn sortCol = new SortColumn("MdAppDetails", "APP_NAME", true);
            query.addSortColumn(sortCol);
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME"));
            Criteria customerCriteria = new Criteria(Column.getColumn("MdAppDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            Criteria filterCharCriteria = null;
            if (!MDMStringUtils.isEmpty(filterChar) && !"all".equals(filterChar)) {
                filterCharCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_NAME"), (Object)filterChar, 10, false);
            }
            if (filterCharCriteria != null) {
                customerCriteria = customerCriteria.and(filterCharCriteria);
            }
            query.setCriteria(customerCriteria);
            final DataObject dobj = MDMUtil.getPersistence().get(query);
            this.out.log(Level.FINE, "DataObject Obtained : {0}", new Object[] { dobj });
            if (!dobj.isEmpty() && dobj.containsTable("MdAppDetails")) {
                final Iterator iterator = dobj.getRows("MdAppDetails");
                appList = new ArrayList();
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final String appName = (String)row.get("APP_NAME");
                    if (!appList.contains(appName)) {
                        appList.add(appName);
                    }
                }
                this.out.log(Level.FINE, "ApplicationList : {0}", new Object[] { appList });
            }
        }
        catch (final Exception ex) {
            this.out.log(Level.WARNING, "Exception while getting Application info hash...", ex);
        }
        return appList;
    }
    
    public HashMap getAppDetails(final Long appID, HashMap restDetailHash) throws Exception {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getDeviceRestrictions()");
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppDetails"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "APP_TYPE"));
            query.addSelectColumn(Column.getColumn("MdAppDetails", "IDENTIFIER"));
            final Criteria criteria = new Criteria(Column.getColumn("MdAppDetails", "APP_ID"), (Object)appID, 0);
            if (criteria != null) {
                query.setCriteria(criteria);
            }
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            restDetailHash = this.getHashFromDO(dataObject, restDetailHash, "App");
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for getAppDetailsFromAppFile info : {0}", restDetailHash);
            return restDetailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting getAppDetailsFromAppFile...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public DataObject getSafeDeviceDetailedInfo(final long resourceID) throws SyMException, DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        query.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("Resource", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        query.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        query.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        query.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("Resource", "NAME"));
        query.addSelectColumn(Column.getColumn("MdModelInfo", "*"));
        query.addSelectColumn(Column.getColumn("MdDeviceInfo", "*"));
        query.addSelectColumn(Column.getColumn("ManagedDevice", "*"));
        query.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "*"));
        final Criteria criteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return MDMUtil.getPersistence().get(query);
    }
    
    public HashMap getSafeDeviceDetailedInfo(final long resourceID, HashMap detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getSafeDeviceDetailedInfo()");
        try {
            final DataObject dataObject = this.getSafeDeviceDetailedInfo(resourceID);
            detailHash = this.getHashFromDO(dataObject, detailHash, "Device");
            this.out.log(Level.INFO, "InventoryUtil : Data Hash for Safe Device Detailed info : {0}", detailHash);
            this.out.log(Level.INFO, "InventoryUtil : Finished Executing InventoryUtil.getSafeDeviceDetailedInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Device Detailed info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getSafeDeviceDetailedInfo(final long resourceID, JSONObject detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getSafeDeviceDetailedInfo()");
        try {
            final DataObject dataObject = this.getSafeDeviceDetailedInfo(resourceID);
            detailHash = this.getJSONfromDO(dataObject, detailHash, null);
            if (detailHash.opt("EXTERNAL_CAPACITY").equals(0.0f)) {
                detailHash.remove("EXTERNAL_CAPACITY");
                detailHash.remove("AVAILABLE_EXTERNAL_CAPACITY");
                detailHash.remove("USED_EXTERNAL_SPACE");
            }
            if (MDMUtil.isStringEmpty(detailHash.optString("MODEM_FIRMWARE_VERSION"))) {
                detailHash.remove("MODEM_FIRMWARE_VERSION");
            }
            if (MDMUtil.isStringEmpty(detailHash.optString("IS_CLOUD_BACKUP_ENABLED"))) {
                detailHash.remove("IS_CLOUD_BACKUP_ENABLED");
                detailHash.remove("LAST_CLOUD_BACKUP_DATE");
            }
            this.out.log(Level.INFO, "InventoryUtil : Data Hash for Safe Device Detailed info : {0}", detailHash);
            this.out.log(Level.INFO, "InventoryUtil : Finished Executing InventoryUtil.getSafeDeviceDetailedInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Device Detailed info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public DataObject getSafeNetworkUsageDetailedInfo(final long resourceID) throws SyMException, DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdNetworkUsageInfo"));
        query.addSelectColumn(new Column("MdNetworkUsageInfo", "*"));
        final Criteria criteria = new Criteria(Column.getColumn("MdNetworkUsageInfo", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return MDMUtil.getPersistence().get(query);
    }
    
    public HashMap getSafeNetworkUsageDetailedInfo(final long resourceID, HashMap detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getSafeNetworkUsageDetailedInfo()");
        try {
            final DataObject dataObject = this.getSafeNetworkUsageDetailedInfo(resourceID);
            detailHash = this.getHashFromDO(dataObject, detailHash, "NetworkUsage");
            this.out.log(Level.INFO, "InventoryUtil : Data Hash for Safe Network Usage Detailed Info : {0}", detailHash);
            this.out.log(Level.INFO, "InventoryUtil : Finished Executing InventoryUtil.getSafeNetworkUsageDetailedInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Safe Network Usage Detailed Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getSafeNetworkUsageDetailedInfo(final long resourceID, JSONObject detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getSafeNetworkUsageDetailedInfo()");
        try {
            final DataObject dataObject = this.getSafeNetworkUsageDetailedInfo(resourceID);
            detailHash = this.getJSONfromDO(dataObject, detailHash, "network_usage");
            this.out.log(Level.INFO, "InventoryUtil : Data Hash for Safe Network Usage Detailed Info : {0}", detailHash);
            this.out.log(Level.INFO, "InventoryUtil : Finished Executing InventoryUtil.getSafeNetworkUsageDetailedInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Safe Network Usage Detailed Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getOSVersionCountDetails(final int platform) {
        return this.getOSVersionCountDetails(platform, Boolean.FALSE);
    }
    
    public JSONObject getOSVersionCountDetails(final int platform, final boolean requireMinorVersion) {
        final JSONObject osVersionJson = new JSONObject();
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
            sQuery.addJoin(new Join("MdDeviceInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0);
            sQuery.setCriteria(cPlatform);
            sQuery.addSelectColumn(new Column("MdDeviceInfo", "OS_VERSION"));
            final Column countColumn = new Column("MdDeviceInfo", "OS_VERSION", "COUNT").count();
            countColumn.setColumnAlias("COUNT");
            sQuery.addSelectColumn(countColumn);
            final List list = new ArrayList();
            final Column groupByCol = Column.getColumn("MdDeviceInfo", "OS_VERSION");
            list.add(groupByCol);
            final GroupByClause groupBy = new GroupByClause(list);
            sQuery.setGroupByClause(groupBy);
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (ds.next()) {
                final Object osVersion = ds.getValue("OS_VERSION");
                final Object osCount = ds.getValue("COUNT");
                if (osVersion != null && osCount != null) {
                    int index = osVersion.toString().indexOf(".");
                    if (requireMinorVersion) {
                        index = osVersion.toString().indexOf(".", index + 1);
                    }
                    String osVersionSplit = "";
                    if (index != -1) {
                        osVersionSplit = osVersion.toString().substring(0, index);
                    }
                    else {
                        osVersionSplit = osVersion.toString();
                    }
                    final int osCountTemp = osVersionJson.optInt(osVersionSplit, 0);
                    osVersionJson.put(osVersionSplit, osCountTemp + (int)osCount);
                }
            }
        }
        catch (final Exception e) {
            this.out.log(Level.SEVERE, "Exception in getPolicyCount", e);
        }
        return osVersionJson;
    }
    
    public Row getWindowsRestriction(final Long resourceID) throws Exception {
        Row restrictionRow = DBUtil.getRowFromDB("MdWindowsDeviceRestriction", "RESOURCE_ID", (Object)resourceID);
        if (restrictionRow == null) {
            restrictionRow = new Row("MdWindowsDeviceRestriction");
        }
        return restrictionRow;
    }
    
    public HashMap getWindowsRestriction(final Long resourceID, HashMap deviceRest) {
        try {
            final Row restrictionRow = this.getWindowsRestriction(resourceID);
            deviceRest = this.getHashFromRow("MdWindowsDeviceRestriction", restrictionRow, deviceRest, "DeviceRest");
        }
        catch (final Exception ex) {
            this.out.log(Level.SEVERE, "InventoryUtil : Exception while getting windows device restrictions", ex);
        }
        return deviceRest;
    }
    
    public JSONObject getWindowsRestriction(final Long resourceID, JSONObject deviceRest) {
        try {
            final String[] win81Above = { "ENFORCE_DEVICE_ENCRIPTION", "DISABLE_SD_CARD", "ALLOW_USE_OF_CAMERA", "ALLOW_SCREEN_CAPTURE", "ALLOW_STORE", "ALLOW_BROWSER", "ALLOW_BLUETOOTH", "ALLOW_TELEMETRY", "ALLOW_NFC", "ALLOW_USB", "ALLOW_CORTANA", "ALLOW_VOICE_RECORDING", "ALLOW_SAVE_AS_OFFICE_FILES", "ALLOW_SHARING_OFFICE_FILES", "ALLOW_SYNC_MY_SETTINGS", "ALLOW_SEARCH_USE_LOCATION", "SAFE_SEARCH_PERMISSIONS", "ALLOW_STORING_IMAGE_SEARCH", "ALLOW_WIFI", "ALLOW_MANUAL_WIFI_CONFIG", "ALLOW_WIFI_HOTSPOT", "ALLOW_AUTO_WIFI_HOTSPOT", "ALLOW_INTERNET_SHARING", "ALLOW_VPN", "ALLOW_VPN_ROAMING", "ALLOW_DATA_ROAMING", "ALLOW_COPY_PASTE", "ALLOW_LOCATION", "ALLOW_MICROSOFT_ACCOUNT", "ALLOW_ADDING_NON_MICROSOFT", "ALLOW_ROOT_CERTIFICATE_INSTALL", "ALLOW_DEVELOPER_UNLOCK", "ALLOW_USER_RESET_PHONE", "ALLOW_ACTION_NOTIFICATION", "RESOURCE_ID" };
            final String[] redStoneMobile = { "DISABLE_SD_CARD", "ENFORCE_DEVICE_ENCRIPTION", "ALLOW_USE_OF_CAMERA", "ALLOW_SCREEN_CAPTURE", "ALLOW_STORE", "ALLOW_TELEMETRY", "ALLOW_USB", "MS_FEEDBACK_NOTIF", "ALLOW_DATE_TIME", "ALLOW_EDIT_DEVICE_NAME", "ALLOW_CORTANA", "ALLOW_VOICE_RECORDING", "ALLOW_SYNC_MY_SETTINGS", "ALLOW_SEARCH_USE_LOCATION", "SAFE_SEARCH_PERMISSIONS", "ALLOW_BROWSER", "BROWSER_ALLOW_COOKIES", "BROWSER_ALLOW_INPRIVATE", "BROWSER_ALLOW_PASSMGR", "BROWSER_ALLOW_SEARCHSUGGEST", "BROWSER_ALLOW_SMARTSCREEN", "BROWSER_SMARTSCREEN_PROMPT", "BROWSER_SMARTSCREEN_FILES", "BROWSER_ALLOW_DONOT_TRACK", "ALLOW_ALL_TRUSTED_APPS", "LIMIT_APPINSTALL_TO_SYS_VOL", "LIMIT_APPDATA_TO_SYS_VOL", "ALLOW_APPSTORE_AUTO_UPDATE", "REQUIRE_PRIVATE_STORE_ONLY", "ALLOW_WIFI", "ALLOW_MANUAL_WIFI_CONFIG", "ALLOW_AUTO_WIFI_HOTSPOT", "ALLOW_INTERNET_SHARING", "ALLOW_VPN_SETTING", "ALLOW_VPN", "ALLOW_VPN_ROAMING", "ALLOW_CELLULAR_DATA", "ALLOW_DATA_ROAMING", "ALLOW_COPY_PASTE", "ALLOW_LOCATION", "ALLOW_MICROSOFT_ACCOUNT", "ALLOW_ADDING_NON_MICROSOFT", "ALLOW_ROOT_CERTIFICATE_INSTALL", "ALLOW_DEVELOPER_UNLOCK", "ALLOW_ACTION_NOTIFICATION", "ALLOW_USER_RESET_PHONE", "ALLOW_TOAST", "ALLOW_FIPS_POLICY", "ALLOW_ADD_PROV_PACKAGE", "ALLOW_REMOVE_PROV_PACKAGE", "ENABLE_ANTI_THEFT_MODE", "ALLOW_NFC", "ALLOW_BLUETOOTH", "ALLOW_BLUETOOTH_DISCOVERABLE", "ALLOW_BLUETOOTH_PREPAIRING", "ALLOW_BLUETOOTH_ADVERTISING", "RESOURCE_ID" };
            final String[] redStoneDesktop = { "DISABLE_SD_CARD", "ALLOW_USE_OF_CAMERA", "ALLOW_TELEMETRY", "MS_FEEDBACK_NOTIF", "ALLOW_DATE_TIME", "ALLOW_SYNC_MY_SETTINGS", "ALLOW_CORTANA", "ALLOW_SEARCH_USE_LOCATION", "BROWSER_ALLOW_COOKIES", "BROWSER_ALLOW_INPRIVATE", "BROWSER_ALLOW_PASSMGR", "BROWSER_ALLOW_ADDRESS_BAR_DROPDOWN", "BROWSER_ALLOW_SEARCHSUGGEST", "BROWSER_ALLOW_SMARTSCREEN", "BROWSER_SMARTSCREEN_PROMPT", "BROWSER_SMARTSCREEN_FILES", "BROWSER_ALLOW_DONOT_TRACK", "BROWSER_ALLOW_EXTENSIONS", "BROWSER_CLEAR_BROWSING_DATA_EXIT", "BROWSER_ABOUT_FLAGS_ACCESS", "BROWSER_ALLOW_FLASH", "BROWSER_RUN_FLASH_AUTOMATICALLY", "BROWSER_ALLOW_AUTOFILL", "BROWSER_ALLOW_POPUPS", "BROWSER_ALLOW_DEVELOPER_TOOLS", "ALLOW_WIFI", "ALLOW_AUTO_WIFI_HOTSPOT", "ALLOW_MANUAL_WIFI_CONFIG", "ALLOW_INTERNET_SHARING", "ALLOW_VPN_SETTING", "ALLOW_VPN", "ALLOW_VPN_ROAMING", "ALLOW_LOCATION", "ALLOW_MICROSOFT_ACCOUNT", "ALLOW_ADDING_NON_MICROSOFT", "ALLOW_USER_RESET_PHONE", "ALLOW_TOAST", "ALLOW_FIPS_POLICY", "ALLOW_ADD_PROV_PACKAGE", "ALLOW_BLUETOOTH_PREPAIRING", "ALLOW_BLUETOOTH_ADVERTISING", "ALLOW_REMOVE_PROV_PACKAGE", "ALLOW_BLUETOOTH", "ALLOW_BLUETOOTH_DISCOVERABLE", "ALLOW_ALL_TRUSTED_APPS", "LIMIT_APPINSTALL_TO_SYS_VOL", "LIMIT_APPDATA_TO_SYS_VOL", "ALLOW_APPSTORE_AUTO_UPDATE", "RESOURCE_ID" };
            final HashMap deviceMap = MDMUtil.getInstance().getMDMDeviceProperties(resourceID);
            final String osVersion = deviceMap.get("OS_VERSION");
            final Boolean isWin10RedstoneAboveResource = ManagedDeviceHandler.getInstance().isWin10RedstoneOrAboveOSVersion(osVersion);
            final Boolean is81Above = ManagedDeviceHandler.getInstance().isWindows81OrAboveDevice(resourceID);
            final Integer modelType = deviceMap.get("MODEL_TYPE");
            final Row restrictionRow = this.getWindowsRestriction(resourceID);
            deviceRest = this.getJSONFromRow(restrictionRow);
            deviceRest = this.convertWindowsServerJSONtoUserJSON(deviceRest);
            if (!isWin10RedstoneAboveResource) {
                return this.addKeysToJSON(deviceRest, win81Above);
            }
            if (modelType == 1) {
                return this.addKeysToJSON(deviceRest, redStoneMobile);
            }
            return this.addKeysToJSON(deviceRest, redStoneDesktop);
        }
        catch (final Exception ex) {
            this.out.log(Level.SEVERE, "InventoryUtil : Exception while getting windows device restrictions", ex);
            return deviceRest;
        }
    }
    
    private JSONObject addKeysToJSON(final JSONObject input, final String[] keysToAdd) throws Exception {
        final JSONObject op = new JSONObject();
        for (final String key : keysToAdd) {
            final Object value = input.opt(key);
            if (value != null) {
                op.put(key, value);
            }
        }
        return op;
    }
    
    private JSONObject convertWindowsServerJSONtoUserJSON(final JSONObject deviceRest) {
        final JSONObject responseJSON = new JSONObject();
        try {
            responseJSON.put("ALLOW_ALL_TRUSTED_APPS", deviceRest.optInt("TRUSTED_APPS_INSTALL", 65535));
            responseJSON.put("BROWSER_ALLOW_POPUPS", deviceRest.optInt("ALLOW_POPUPS", -999));
            responseJSON.put("BROWSER_ALLOW_EXTENSIONS", deviceRest.optInt("ALLOW_EXTENSIONS", -1));
            responseJSON.put("ALLOW_USB", deviceRest.optInt("USB", -1));
            responseJSON.put("ALLOW_ACTION_NOTIFICATION", deviceRest.optInt("ACTION_CENTER_NOTIFICATIONS", -1));
            responseJSON.put("ALLOW_CORTANA", deviceRest.optInt("CORTANA", -1));
            responseJSON.put("ALLOW_DEVELOPER_UNLOCK", deviceRest.optInt("DEVELOPER_UNLOCK", -1));
            responseJSON.put("BROWSER_ALLOW_DONOT_TRACK", (Object)deviceRest.optString("ALLOW_DONOT_TRACK", "--"));
            responseJSON.put("BROWSER_ALLOW_COOKIES", (Object)deviceRest.optString("ALLOW_COOKIES", "--"));
            responseJSON.put("ALLOW_DATE_TIME", deviceRest.optInt("DATE_TIME", -1));
            responseJSON.put("ALLOW_EDIT_DEVICE_NAME", deviceRest.optInt("EDIT_DEVICE_NAME", -1));
            responseJSON.put("ALLOW_COPY_PASTE", deviceRest.optInt("COPY_PASTE", -1));
            responseJSON.put("BROWSER_ALLOW_SEARCHSUGGEST", (Object)deviceRest.optString("ALLOW_SEARCHSUGGEST", "--"));
            responseJSON.put("BROWSER_ALLOW_SMARTSCREEN", (Object)deviceRest.optString("ALLOW_SMARTSCREEN", "--"));
            responseJSON.put("SAFE_SEARCH_PERMISSIONS", deviceRest.optInt("SAFE_SEARCH_PERMISSIONS", -1));
            responseJSON.put("ALLOW_WIFI", deviceRest.optInt("WIFI", -1));
            responseJSON.put("ENFORCE_DEVICE_ENCRIPTION", deviceRest.optInt("DEV_ENCRYPT", -1));
            responseJSON.put("ALLOW_NFC", deviceRest.optInt("NFC", -1));
            responseJSON.put("ALLOW_SYNC_MY_SETTINGS", deviceRest.optInt("SYNC_MY_SETTINGS", -1));
            responseJSON.put("ALLOW_TOAST", deviceRest.optInt("TOAST", -1));
            responseJSON.put("ALLOW_VPN_ROAMING", deviceRest.optInt("VPN_ROAMING", -1));
            responseJSON.put("ALLOW_ROOT_CERTIFICATE_INSTALL", deviceRest.optInt("MANUAL_ROOT_CERT_INSTALL", -1));
            responseJSON.put("ALLOW_FIPS_POLICY", deviceRest.optInt("FIPS_POLICY", -1));
            responseJSON.put("ALLOW_TELEMETRY", deviceRest.optInt("TELEMETRY", 2));
            responseJSON.put("REQUIRE_PRIVATE_STORE_ONLY", deviceRest.optInt("PRIVATE_STORE_ONLY", -1));
            responseJSON.put("ALLOW_USER_RESET_PHONE", deviceRest.optInt("USER_RESET_PHONE", -1));
            responseJSON.put("ALLOW_SEARCH_USE_LOCATION", deviceRest.optInt("SEARCH_USE_LOCATION", -1));
            responseJSON.put("ALLOW_SAVE_AS_OFFICE_FILES", deviceRest.optInt("SAVE_AS_OF_OFFICE_FILES", -1));
            responseJSON.put("USER_UNENROLL", deviceRest.optBoolean("MANUAL_MDM_UNENROLLMENT", true));
            responseJSON.put("BROWSER_CLEAR_BROWSING_DATA_EXIT", deviceRest.optInt("CLEAR_BROWSING_DATA_EXIT", -999));
            responseJSON.put("DISABLE_SD_CARD", deviceRest.optInt("SD_CARD", -1));
            responseJSON.put("ALLOW_MICROSOFT_ACCOUNT", deviceRest.optInt("MS_ACC_CONNECTION", -1));
            responseJSON.put("BROWSER_RUN_FLASH_AUTOMATICALLY", deviceRest.optInt("RUN_FLASH_AUTOMATICALLY", -1));
            responseJSON.put("LIMIT_APPDATA_TO_SYS_VOL", deviceRest.optInt("LIMIT_APPDATA_TO_SYS_VOL", -1));
            responseJSON.put("BROWSER_ALLOW_DEVELOPER_TOOLS", deviceRest.optInt("ALLOW_DEVELOPER_TOOLS", -1));
            responseJSON.put("ALLOW_STORING_IMAGE_SEARCH", deviceRest.optInt("STORE_IMG_FROM_VISION_SEARCH", -1));
            responseJSON.put("ALLOW_DATA_ROAMING", deviceRest.optInt("DATA_ROAMING", -1));
            responseJSON.put("MS_FEEDBACK_NOTIF", deviceRest.optInt("MS_FEEDBACK_NOTIF", 0));
            responseJSON.put("BROWSER_ALLOW_AUTOFILL", deviceRest.optInt("ALLOW_AUTOFILL", -999));
            responseJSON.put("ALLOW_VOICE_RECORDING", deviceRest.optInt("VOICE_RECORDING", -1));
            responseJSON.put("ALLOW_BROWSER", deviceRest.optInt("BROWSER", -1));
            responseJSON.put("ALLOW_MANUAL_WIFI_CONFIG", deviceRest.optInt("MANUAL_WIFI_CONFIG", -1));
            responseJSON.put("ALLOW_USE_OF_CAMERA", deviceRest.optInt("CAMERA", -1));
            responseJSON.put("LIMIT_APPINSTALL_TO_SYS_VOL", deviceRest.optInt("LIMIT_APPINSTALL_TO_SYS_VOL", -1));
            responseJSON.put("BROWSER_ABOUT_FLAGS_ACCESS", deviceRest.optInt("ABOUT_FLAGS_ACCESS", -1));
            responseJSON.put("ALLOW_SHARING_OFFICE_FILES", deviceRest.optInt("SHARE_OFFICE_FILES", -1));
            responseJSON.put("ALLOW_REMOVE_PROV_PACKAGE", deviceRest.optInt("REMOVE_PROV_PACKAGE", -1));
            responseJSON.put("ALLOW_BLUETOOTH_ADVERTISING", deviceRest.optInt("BLUETOOTH_ADVERTISING", -1));
            responseJSON.put("BROWSER_ALLOW_PASSMGR", (Object)deviceRest.optString("ALLOW_PASSMGR", "--"));
            responseJSON.put("ALLOW_VPN_SETTING", deviceRest.optInt("VPN_SETTING", -1));
            responseJSON.put("ALLOW_ADD_PROV_PACKAGE", deviceRest.optInt("ADD_PROV_PACKAGE", -1));
            responseJSON.put("ALLOW_STORE", deviceRest.optInt("STORE", -1));
            responseJSON.put("BROWSER_ALLOW_FLASH", deviceRest.optInt("ALLOW_FLASH", -999));
            responseJSON.put("BROWSER_ALLOW_INPRIVATE", deviceRest.optInt("ALLOW_INPRIVATE", -1));
            responseJSON.put("BROWSER_SMARTSCREEN_PROMPT", (Object)deviceRest.optString("SMARTSCREEN_PROMPT", "--"));
            responseJSON.put("BROWSER_ALLOW_ADDRESS_BAR_DROPDOWN", deviceRest.optInt("ALLOW_ADDRESS_BAR_DROPDOWN", -1));
            responseJSON.put("ENABLE_ANTI_THEFT_MODE", deviceRest.optInt("ANTI_THEFT_MODE", 1));
            responseJSON.put("ALLOW_BLUETOOTH_DISCOVERABLE", deviceRest.optInt("BLUETOOTH_DISCOVERABLE", -1));
            responseJSON.put("ALLOW_CELLULAR_DATA", deviceRest.optInt("CELLULAR_DATA", 1));
            responseJSON.put("ALLOW_APPSTORE_AUTO_UPDATE", deviceRest.optInt("APP_STORE_AUTO_UPDATE", -999));
            responseJSON.put("ALLOW_AUTO_WIFI_HOTSPOT", deviceRest.optInt("AUTO_CONNECT_TO_WIFI_HOTSPOT", -1));
            responseJSON.put("ALLOW_BLUETOOTH", deviceRest.optInt("BLUETOOTH", -1));
            responseJSON.put("ALLOW_VPN", deviceRest.optInt("VPN", -1));
            responseJSON.put("ALLOW_BLUETOOTH_PREPAIRING", deviceRest.optInt("BLUETOOTH_PREPAIRING", -1));
            responseJSON.put("ALLOW_WIFI_HOTSPOT", deviceRest.optInt("WIFI_HOTSPOT_REPORTING", -1));
            responseJSON.put("ALLOW_ADDING_NON_MICROSOFT", deviceRest.optInt("NON_MS_ACC", -1));
            responseJSON.put("ALLOW_SCREEN_CAPTURE", deviceRest.optInt("SCREEN_CAPTURE", -1));
            responseJSON.put("RESOURCE_ID", (Object)JSONUtil.optLongForUVH(deviceRest, "RESOURCE_ID", Long.valueOf(-1L)));
            responseJSON.put("ALLOW_LOCATION", deviceRest.optInt("LOCATION", -1));
            responseJSON.put("ALLOW_INTERNET_SHARING", deviceRest.optInt("NET_SHARING", -1));
            responseJSON.put("BROWSER_SMARTSCREEN_FILES", (Object)deviceRest.optString("SMARTSCREEN_FILES", "--"));
        }
        catch (final JSONException e) {
            this.out.log(Level.SEVERE, "InventoryUtil : Exception in convertWindowsServerJSONtoUserJSON()", (Throwable)e);
        }
        return responseJSON;
    }
    
    public JSONObject getSummaryDetails(final Long resourceID) {
        JSONObject detailHash = new JSONObject();
        try {
            detailHash = getInstance().getDeviceInfo(resourceID, detailHash);
            detailHash = getInstance().getNetworkInfo(resourceID, detailHash);
            detailHash = getInstance().getSimInfo(resourceID, detailHash);
            detailHash = getInstance().getOSInfo(resourceID, detailHash);
            detailHash = getInstance().getSecurityInfo(resourceID, detailHash);
        }
        catch (final SyMException ex) {
            Logger.getLogger(InventoryUtil.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        return detailHash;
    }
    
    public JSONObject getDeviceDetails(final Long resourceID) {
        try {
            JSONObject detailJSON = new JSONObject();
            final int agentType = (int)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "AGENT_TYPE");
            detailJSON = getInstance().getOSInfo(resourceID, detailJSON);
            detailJSON = getInstance().getSecurityInfo(resourceID, detailJSON);
            detailJSON = getInstance().getAgentContactInfo(resourceID, detailJSON);
            detailJSON = getInstance().getDeviceUserInfo(resourceID, detailJSON);
            switch (agentType) {
                case 1:
                case 2:
                case 4:
                case 7:
                case 8: {
                    detailJSON = getInstance().getDeviceDetailedInfo(resourceID, detailJSON);
                    detailJSON = getInstance().getNetworkDetailedInfo(resourceID, detailJSON);
                    detailJSON = getInstance().getSimInfo(resourceID, detailJSON);
                    detailJSON = getInstance().getWorkDataSecurityInfo(resourceID, detailJSON);
                    detailJSON = getInstance().getSharedDeviceDetails(resourceID, detailJSON);
                    return detailJSON;
                }
                case 3: {
                    detailJSON = getInstance().getSafeDeviceDetailedInfo(resourceID, detailJSON);
                    detailJSON = getInstance().getNetworkDetailedInfo(resourceID, detailJSON);
                    detailJSON = getInstance().getSimInfo(resourceID, detailJSON);
                    detailJSON = getInstance().getSafeNetworkUsageDetailedInfo(resourceID, detailJSON);
                    final boolean isKnoxEnabled = KnoxUtil.getInstance().isRegisteredAsKnox(resourceID);
                    detailJSON.put("is_knox_enabled", isKnoxEnabled);
                    if (isKnoxEnabled) {
                        final JSONObject knoxDetails = KnoxUtil.getInstance().getDeviceKnoxDetailsJSON(resourceID);
                        detailJSON.put("knox_details", (Object)knoxDetails);
                    }
                    return detailJSON;
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(InventoryUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new JSONObject();
    }
    
    public HashMap getSystemActivityDetails(final long resourceID, HashMap detailHash) throws SyMException {
        try {
            final DataObject dataObject = this.getSystemActivityDetails(resourceID);
            detailHash = this.getHashFromDO(dataObject, detailHash, "systemActivity");
            return this.formatDateAndTime(detailHash);
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getSystemActivityDetails", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getDeviceHardwareDetails(final Long resourceID) {
        try {
            JSONObject detailJSON = new JSONObject();
            final int agentType = (int)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "AGENT_TYPE");
            switch (agentType) {
                case 1:
                case 2:
                case 4:
                case 8: {
                    detailJSON = getInstance().getDeviceDetailedInfo(resourceID, detailJSON);
                    return detailJSON;
                }
                case 3: {
                    detailJSON = getInstance().getSafeDeviceDetailedInfo(resourceID, detailJSON);
                    return detailJSON;
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(InventoryUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new JSONObject();
    }
    
    public JSONObject getDeviceSIMDetails(final Long resourceID) {
        try {
            return getInstance().getSimInfo(resourceID, new JSONObject());
        }
        catch (final Exception ex) {
            Logger.getLogger(InventoryUtil.class.getName()).log(Level.SEVERE, null, ex);
            return new JSONObject();
        }
    }
    
    public JSONObject getDeviceNetworkDetails(final Long resourceID) {
        try {
            JSONObject detailJSON = new JSONObject();
            detailJSON = getInstance().getNetworkDetailedInfo(resourceID, detailJSON);
            final int agentType = (int)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "AGENT_TYPE");
            if (agentType == 3) {
                detailJSON = getInstance().getSafeNetworkUsageDetailedInfo(resourceID, detailJSON);
            }
            return detailJSON;
        }
        catch (final Exception ex) {
            Logger.getLogger(InventoryUtil.class.getName()).log(Level.SEVERE, null, ex);
            return new JSONObject();
        }
    }
    
    public JSONObject getDeviceRestrictions(final Long resourceID) {
        JSONObject detailJSON = new JSONObject();
        try {
            final int agentType = (int)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "AGENT_TYPE");
            switch (agentType) {
                case 1:
                case 8: {
                    detailJSON = new JSONObject(getInstance().getIOSRestrictionDetails(resourceID).toJSONString());
                    break;
                }
                case 2: {
                    detailJSON = getInstance().getSafeDeviceRestriction(resourceID, detailJSON);
                    break;
                }
                case 4: {
                    detailJSON = this.getWindowsRestriction(resourceID, detailJSON);
                    break;
                }
                case 3: {
                    detailJSON = getInstance().getSafeDeviceRestriction(resourceID, detailJSON);
                    if (KnoxUtil.getInstance().doesContainerActive(resourceID)) {
                        JSONObject knoxRestrictions = new JSONObject();
                        knoxRestrictions = getInstance().getSafeKnoxDeviceRestriction(resourceID, knoxRestrictions);
                        detailJSON.put("knox_restricions", (Object)knoxRestrictions);
                        break;
                    }
                    break;
                }
            }
            final JSONObject response = new JSONObject();
            response.put("restrictions", (Object)detailJSON);
            return response;
        }
        catch (final Exception ex) {
            Logger.getLogger(InventoryUtil.class.getName()).log(Level.SEVERE, null, ex);
            return detailJSON;
        }
    }
    
    public Boolean isSupervisedDevice(final Long resourceId) {
        final List superVisedList = new ArrayList();
        Boolean isSupervised = false;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceInfo"));
            final Criteria cResourceId = new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria cSuperVisedDevice = new Criteria(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)Boolean.TRUE, 0);
            sQuery.setCriteria(cResourceId.and(cSuperVisedDevice));
            sQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (!DO.isEmpty()) {
                isSupervised = true;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return isSupervised;
    }
    
    public boolean isWipedFromServer(final String UDID) {
        Boolean isWipeFromServer = false;
        try {
            final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(UDID);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdCommandsToDevice"));
            selectQuery.addJoin(new Join("MdCommandsToDevice", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 1));
            final Criteria deviceCiteria = new Criteria(Column.getColumn("MdCommandsToDevice", "UDID"), (Object)UDID, 0).or(new Criteria(Column.getColumn("MdCommandsToDevice", "RESOURCE_ID"), (Object)resourceId, 0));
            final Criteria commandCriteria = new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)"CorporateWipe", 0).or(new Criteria(Column.getColumn("MdCommands", "COMMAND_UUID"), (Object)"EraseDevice", 0));
            selectQuery.setCriteria(deviceCiteria.and(commandCriteria));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dobj = MDMUtil.getPersistence().get(selectQuery);
            if (!dobj.isEmpty()) {
                isWipeFromServer = true;
            }
        }
        catch (final DataAccessException ex) {
            this.out.log(Level.SEVERE, "InventoryUtil : Exception getting if device was wiped from server ", (Throwable)ex);
        }
        return isWipeFromServer;
    }
    
    public void updateDeviceBasicInfo(final JSONObject jsonObject, final Long resID) {
        try {
            final String serialNumber = (String)jsonObject.get("SerialNumber");
            String imei = (String)jsonObject.get("IMEI");
            final String easID = (String)jsonObject.get("EASID");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceInfo"));
            selectQuery.addJoin(new Join("MdDeviceInfo", "MdSIMInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "EAS_DEVICE_IDENTIFIER"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "IMEI"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "SERIAL_NUMBER"));
            selectQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdSIMInfo", "SIM_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdSIMInfo", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdSIMInfo", "SLOT"));
            selectQuery.addSelectColumn(Column.getColumn("MdSIMInfo", "IMEI"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)resID, 0));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Row row = dataObject.getFirstRow("MdDeviceInfo");
            if (row != null) {
                if (serialNumber != null && !serialNumber.isEmpty()) {
                    row.set("SERIAL_NUMBER", (Object)serialNumber);
                }
                if (imei != null && !imei.isEmpty()) {
                    imei = imei.replace(" ", "");
                    row.set("IMEI", (Object)imei);
                }
                if (easID != null && !easID.isEmpty()) {
                    row.set("EAS_DEVICE_IDENTIFIER", (Object)easID);
                }
                dataObject.updateRow(row);
            }
            Row simRow = dataObject.getRow("MdSIMInfo", new Criteria(Column.getColumn("MdSIMInfo", "RESOURCE_ID"), (Object)resID, 0));
            if (simRow == null) {
                simRow = new Row("MdSIMInfo");
                simRow.set("RESOURCE_ID", (Object)resID);
                if (imei != null && !imei.equalsIgnoreCase("")) {
                    imei = imei.replace(" ", "");
                    simRow.set("IMEI", (Object)imei);
                }
                dataObject.addRow(simRow);
            }
            MDMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception e) {
            this.out.log(Level.SEVERE, e, () -> "Couldnt add details during enrollemnt for enrollment " + jsonObject2 + " , resourceID:" + n);
        }
    }
    
    public boolean isPasscodeEnableForResource(final Long resourceID) {
        boolean isDeviceLocked = false;
        try {
            JSONObject details = new JSONObject();
            details = this.getSecurityInfo(resourceID, details);
            final JSONObject securityDetails = details.optJSONObject("security");
            isDeviceLocked = securityDetails.optBoolean("PASSCODE_PRESENT");
        }
        catch (final Exception ex) {}
        return isDeviceLocked;
    }
    
    public DataObject getKioskInfo(final long resourceID) throws SyMException, DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceKioskStateInfo"));
        query.addSelectColumn(Column.getColumn("DeviceKioskStateInfo", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("DeviceKioskStateInfo", "CURRENT_KIOSK_STATE"));
        query.addSelectColumn(Column.getColumn("DeviceKioskStateInfo", "REMARKS"));
        final Criteria criteria = new Criteria(Column.getColumn("DeviceKioskStateInfo", "RESOURCE_ID"), (Object)resourceID, 0);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return MDMUtil.getPersistence().get(query);
    }
    
    public HashMap getKioskInfo(final long resourceID, HashMap detailHash) throws SyMException {
        this.out.log(Level.FINE, "Entered into InventoryUtil.getKioskInfo()");
        try {
            final DataObject dataObject = this.getKioskInfo(resourceID);
            detailHash = this.getHashFromDO(dataObject, detailHash, "Kiosk");
            final KioskPauseResumeManager manager = new KioskPauseResumeManager();
            this.out.log(Level.FINE, "InventoryUtil : Data Hash for Kiosk info : {0}", detailHash);
            this.out.log(Level.FINE, "InventoryUtil : Finished Executing InventoryUtil.getKioskInfo()");
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Security Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getSystemActivityDetailsJSON(final long resourceID) throws SyMException, JSONException {
        try {
            final DataObject dataObject = this.getSystemActivityDetails(resourceID);
            JSONObject detailsJSON = new JSONObject();
            detailsJSON = this.getJSONfromDO(dataObject, detailsJSON, null);
            return this.formatDateAndTimeJSON(detailsJSON);
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getSystemActivityDetails", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getSystemLoginActivityDetailsJSON(final long resourceID) throws SyMException, JSONException {
        try {
            final DataObject dataObject = this.getRecentUsersDetails(resourceID);
            final JSONObject responseJSON = new JSONObject();
            if (dataObject.containsTable("MdDeviceRecentUsersInfoExtn")) {
                JSONObject detailsJSON = new JSONObject();
                detailsJSON = this.getJSONfromDO(dataObject, detailsJSON, null);
                detailsJSON = this.formatDateAndTimeJSON(detailsJSON);
                responseJSON.put("login_time", (Object)detailsJSON.optString("LOGIN_TIME"));
                responseJSON.put("login_user_short_name", (Object)detailsJSON.optString("LOGON_USER_NAME"));
                responseJSON.put("login_user_display_name", (Object)detailsJSON.optString("LOGON_USER_DISPLAY_NAME"));
                return responseJSON;
            }
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getSystemActivityDetails", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
        return null;
    }
    
    public DataObject getSystemActivityDetails(final long resourceID) throws SyMException, DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceActivityDetails"));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        final Criteria criteria = new Criteria(Column.getColumn("MdDeviceActivityDetails", "RESOURCE_ID"), (Object)resourceID, 0);
        query.setCriteria(criteria);
        return MDMUtil.getPersistence().get(query);
    }
    
    public HashMap getRecentUsersDetails(final long resourceID, final HashMap detailHash) throws SyMException {
        try {
            final DataObject dataObject = this.getRecentUsersDetails(resourceID);
            final Iterator userItr = dataObject.getRows("MdDeviceRecentUsersInfo");
            HashMap userHash = null;
            final ArrayList userList = new ArrayList();
            while (userItr.hasNext()) {
                userHash = new HashMap();
                final Row userRow = userItr.next();
                userHash = this.getHashFromRow("MdDeviceRecentUsersInfo", userRow, userHash, "MdDeviceRecentUsersInfo");
                if ((int)userRow.get("USER_MANAGEMENT_TYPE") == 1) {
                    final Long userId = (Long)userRow.get("USER_ID");
                    if (userId != null) {
                        final Row directoryUserRow = dataObject.getRow("DirObjRegStrVal", new Criteria(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"), (Object)userId, 0));
                        userHash = this.getHashFromRow("DirObjRegStrVal", directoryUserRow, userHash, "DirObjRegStrVal");
                    }
                }
                userList.add(userHash);
            }
            detailHash.put("userList", userList);
            return detailHash;
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Sim Detailed Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONArray getRecentUsersDetailsJSON(final long resourceID) throws SyMException {
        try {
            final DataObject dataObject = this.getRecentUsersDetails(resourceID);
            final Iterator userItr = dataObject.getRows("MdDeviceRecentUsersInfo");
            final JSONArray userList = new JSONArray();
            while (userItr.hasNext()) {
                final Row userRow = userItr.next();
                JSONObject userJson = this.getJSONFromRow(userRow);
                if ((int)userRow.get("USER_MANAGEMENT_TYPE") == 1) {
                    final Long userId = (Long)userRow.get("USER_ID");
                    if (userId != null) {
                        final Row directoryUserRow = dataObject.getRow("DirObjRegStrVal", new Criteria(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"), (Object)userId, 0));
                        userJson = JSONUtil.mergeJSONObjects(userJson, this.getJSONFromRow(directoryUserRow));
                    }
                }
                userJson.remove("ADDED_AT");
                userJson.remove("RESOURCE_ID");
                userJson.remove("DEVICE_RECENT_USER_ID");
                userJson.remove("ATTR_ID");
                userList.put((Object)userJson);
            }
            return userList;
        }
        catch (final Exception exp) {
            this.out.log(Level.WARNING, "InventoryUtil :error in  getRecentUsersDetailsJSON()...", exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public DataObject getRecentUsersDetails(final long resourceID) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceRecentUsersInfo"));
        final Join join = new Join("MdDeviceRecentUsersInfo", "DirObjRegStrVal", new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)106L, 0).and(new Criteria(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"), (Object)Column.getColumn("MdDeviceRecentUsersInfo", "USER_ID"), 0)), 1);
        final Criteria criteria = new Criteria(Column.getColumn("MdDeviceRecentUsersInfo", "RESOURCE_ID"), (Object)resourceID, 0);
        query.addJoin(new Join("MdDeviceRecentUsersInfo", "MdDeviceRecentUsersInfoExtn", new String[] { "DEVICE_RECENT_USER_ID" }, new String[] { "DEVICE_RECENT_USER_ID" }, 1));
        query.addSelectColumn(Column.getColumn("MdDeviceRecentUsersInfo", "DEVICE_RECENT_USER_ID"));
        query.addSelectColumn(Column.getColumn("MdDeviceRecentUsersInfo", "ORDER"));
        query.addSelectColumn(Column.getColumn("MdDeviceRecentUsersInfo", "USER_MANAGEMENT_TYPE"));
        query.addSelectColumn(Column.getColumn("MdDeviceRecentUsersInfo", "USER_ID"));
        query.addSelectColumn(Column.getColumn("MdDeviceRecentUsersInfoExtn", "DEVICE_RECENT_USER_ID"));
        query.addSelectColumn(Column.getColumn("MdDeviceRecentUsersInfoExtn", "LOGON_USER_DISPLAY_NAME"));
        query.addSelectColumn(Column.getColumn("MdDeviceRecentUsersInfoExtn", "LOGON_USER_NAME"));
        query.addSelectColumn(Column.getColumn("MdDeviceRecentUsersInfoExtn", "LOGIN_TIME"));
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "OBJ_ID"));
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "ATTR_ID"));
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "RESOURCE_ID"));
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "VALUE"));
        query.addSelectColumn(Column.getColumn("DirObjRegStrVal", "ADDED_AT"));
        query.addJoin(join);
        query.setCriteria(criteria);
        final SortColumn sortCol = new SortColumn(Column.getColumn("MdDeviceRecentUsersInfo", "ORDER"), true);
        query.addSortColumn(sortCol);
        return MDMUtil.getPersistence().get(query);
    }
    
    public HashMap getActiveTimeDetails(final long resourceID, final HashMap detailHash) throws SyMException {
        try {
            final DataObject dataObject = this.getActiveTimeDetails(resourceID);
            final Iterator activeTimeItr = dataObject.getRows("MdDeviceActiveTimeDetails");
            HashMap activeTiemMap = null;
            final ArrayList activeTimeList = new ArrayList();
            while (activeTimeItr.hasNext()) {
                activeTiemMap = new HashMap();
                final Row activeTimeRow = activeTimeItr.next();
                activeTiemMap = this.getHashFromRow("MdDeviceActiveTimeDetails", activeTimeRow, activeTiemMap, "MdDeviceActiveTimeDetails");
                activeTimeList.add(activeTiemMap);
            }
            detailHash.put("activeTimeList", activeTimeList);
            return this.formatDateAndTime(detailHash);
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting Sim Detailed Info...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public JSONObject getActiveTimeDetailsJSON(final long resourceID) throws Exception {
        try {
            final DataObject dataObject = this.getActiveTimeDetails(resourceID);
            final Iterator activeTimeItr = dataObject.getRows("MdDeviceActiveTimeDetails");
            final JSONArray activeTimeList = new JSONArray();
            final JSONObject response = new JSONObject();
            while (activeTimeItr.hasNext()) {
                final Row activeTimeRow = activeTimeItr.next();
                final JSONObject activeTimeJSON = this.getJSONFromRow(activeTimeRow);
                activeTimeList.put((Object)activeTimeJSON);
            }
            response.put("active_time_list", (Object)activeTimeList);
            return this.formatDateAndTimeJSON(response);
        }
        catch (final DataAccessException exp) {
            this.out.log(Level.WARNING, "InventoryUtil : Error in getActiveTimeDetailsJSON()...", (Throwable)exp);
            throw new SyMException(1001, (Throwable)exp);
        }
    }
    
    public DataObject getActiveTimeDetails(final long resourceID) throws SyMException, DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MdDeviceActiveTimeDetails"));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        final Criteria criteria = new Criteria(Column.getColumn("MdDeviceActiveTimeDetails", "RESOURCE_ID"), (Object)resourceID, 0);
        query.setCriteria(criteria);
        return MDMUtil.getPersistence().get(query);
    }
    
    private HashMap formatDateAndTime(HashMap detailHash) {
        detailHash = this.replaceMilisWithFormattedDate(detailHash, "systemActivity_MdDeviceActivityDetails__LAST_SYNC_TIME", true);
        final List activeTimeList = (List)detailHash.get("activeTimeList");
        if (activeTimeList != null) {
            final List formatedActiveTimeList = new ArrayList();
            for (final Object activeTime : activeTimeList) {
                HashMap activeTimeMap = (HashMap)activeTime;
                activeTimeMap = this.replaceMilisWithFormattedDate(activeTimeMap, "MdDeviceActiveTimeDetails_MdDeviceActiveTimeDetails__ACTIVE_DATE", false);
                activeTimeMap = this.replaceMilisWithDuration(activeTimeMap, "MdDeviceActiveTimeDetails_MdDeviceActiveTimeDetails__ACTIVE_TIME");
                formatedActiveTimeList.add(activeTimeMap);
            }
            detailHash.put("activeTimeList", formatedActiveTimeList);
        }
        return detailHash;
    }
    
    private JSONObject formatDateAndTimeJSON(final JSONObject detailJSON) throws JSONException {
        if (detailJSON.has("LAST_SYNC_TIME")) {
            detailJSON.put("LAST_SYNC_TIME", (Object)MDMUtil.getDate((Long)detailJSON.get("LAST_SYNC_TIME"), true));
        }
        if (detailJSON.has("LOGIN_TIME")) {
            detailJSON.put("LOGIN_TIME", (Object)MDMUtil.getDate((Long)detailJSON.get("LOGIN_TIME"), true));
        }
        if (detailJSON.has("active_time_list")) {
            final JSONArray activeTimeList = detailJSON.getJSONArray("active_time_list");
            if (activeTimeList != null) {
                final JSONArray formatedActiveTimeList = new JSONArray();
                for (int i = 0; i < activeTimeList.length(); ++i) {
                    final JSONObject activeTimeJSON = activeTimeList.getJSONObject(i);
                    final String duration = MDMUtil.getDuration((Long)activeTimeJSON.get("ACTIVE_TIME"));
                    if (!duration.isEmpty()) {
                        activeTimeJSON.put("ACTIVE_DATE", (Object)MDMUtil.getDate((Long)activeTimeJSON.get("ACTIVE_DATE"), false));
                        activeTimeJSON.put("ACTIVE_TIME", (Object)duration);
                        formatedActiveTimeList.put((Object)activeTimeJSON);
                    }
                }
                detailJSON.put("active_time_list", (Object)formatedActiveTimeList);
            }
        }
        return detailJSON;
    }
    
    private HashMap replaceMilisWithFormattedDate(final HashMap map, final String key, final Boolean isIncludeTime) {
        try {
            if (map.containsKey(key)) {
                map.put(key, MDMUtil.getDate(map.get(key), isIncludeTime));
            }
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "InventoryUtil : Cannot format date in {0}-{1}", new Object[] { map, e });
        }
        return map;
    }
    
    private HashMap replaceMilisWithDuration(final HashMap map, final String key) {
        if (map.containsKey(key)) {
            map.put(key, MDMUtil.getDuration(map.get(key)));
        }
        return map;
    }
    
    public Integer getEFRPStatus(final long resourceID) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdSecurityInfo"));
        final Criteria criteria = new Criteria(new Column("MdSecurityInfo", "RESOURCE_ID"), (Object)resourceID, 0);
        selectQuery.addSelectColumn(new Column("MdSecurityInfo", "RESOURCE_ID"));
        selectQuery.addSelectColumn(new Column("MdSecurityInfo", "EFRP_STATUS"));
        selectQuery.setCriteria(criteria);
        try {
            final DataObject dO = MDMUtil.getPersistence().get(selectQuery);
            if (!dO.isEmpty()) {
                final Row r = dO.getFirstRow("MdSecurityInfo");
                final Integer efrp_status = (Integer)r.get("EFRP_STATUS");
                return efrp_status;
            }
        }
        catch (final DataAccessException e) {
            this.out.log(Level.WARNING, "InventoryUtil : DataAccessException while getting EFRP Enabled Info...", (Throwable)e);
        }
        return 3;
    }
    
    public JSONArray getEFRPInfo(final long resourceID) throws Exception {
        final JSONArray efrpInfoArray = new JSONArray();
        final Integer efrp_status = this.getEFRPStatus(resourceID);
        if (efrp_status == 1) {
            final ProfileAssociateDataHandler profileAssociateDataHandler = new ProfileAssociateDataHandler();
            final SelectQuery query = profileAssociateDataHandler.getProfileAssociatedForResourceQuery();
            query.addJoin(new Join("ConfigDataItem", "AndroidEFRPPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            query.addJoin(new Join("AndroidEFRPPolicy", "EFRPAccDetails", new String[] { "EFRP_ACC_ID" }, new String[] { "EFRP_ACC_ID" }, 2));
            query.addJoin(new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            query.addJoin(new Join("Profile", "AaaUser", new String[] { "LAST_MODIFIED_BY" }, new String[] { "USER_ID" }, 2));
            final Criteria succeededCriteria = new Criteria(new Column("CollnToResources", "STATUS"), (Object)6, 0);
            final Criteria resourceCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)Arrays.asList(resourceID).toArray(), 8);
            final Criteria configCriteria = new Criteria(new Column("ConfigData", "CONFIG_ID"), (Object)565, 0);
            final Criteria associatedCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            final Criteria finalCriteria = succeededCriteria.and(resourceCriteria).and(configCriteria).and(associatedCriteria);
            query.setCriteria(finalCriteria);
            query.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            query.addSelectColumn(new Column("Profile", "PROFILE_NAME"));
            query.addSelectColumn(new Column("Profile", "LAST_MODIFIED_BY"));
            query.addSelectColumn(new Column("AaaUser", "USER_ID"));
            query.addSelectColumn(new Column("AaaUser", "FIRST_NAME"));
            query.addSelectColumn(new Column("RecentProfileForResource", "*"));
            query.addSelectColumn(new Column("AndroidEFRPPolicy", "*"));
            query.addSelectColumn(new Column("EFRPAccDetails", "*"));
            query.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ITEM_ID"));
            query.addSelectColumn(new Column("ConfigDataItem", "CONFIG_DATA_ID"));
            query.addSelectColumn(new Column("CfgDataToCollection", "CONFIG_DATA_ID"));
            query.addSelectColumn(new Column("CfgDataToCollection", "COLLECTION_ID"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(query);
            final Iterator profileIterator = dataObject.getRows("RecentProfileForResource");
            if (!dataObject.isEmpty()) {
                while (profileIterator.hasNext()) {
                    final JSONObject profileData = new JSONObject();
                    final Row profileResourceRow = profileIterator.next();
                    final Long profileID = (Long)profileResourceRow.get("PROFILE_ID");
                    final Row profileDataRow = dataObject.getRow("Profile", new Criteria(new Column("Profile", "PROFILE_ID"), (Object)profileID, 0));
                    if (profileDataRow != null) {
                        profileData.put("profileName", profileDataRow.get("PROFILE_NAME"));
                    }
                    final Long userID = (Long)profileDataRow.get("LAST_MODIFIED_BY");
                    final Row userDataRow = dataObject.getRow("AaaUser", new Criteria(new Column("AaaUser", "USER_ID"), (Object)userID, 0));
                    if (userDataRow != null) {
                        profileData.put("addedBy", userDataRow.get("FIRST_NAME"));
                    }
                    final Row configIdRow = dataObject.getRow("CfgDataToCollection", new Criteria(new Column("CfgDataToCollection", "COLLECTION_ID"), profileResourceRow.get("COLLECTION_ID"), 0));
                    final Long configId = (Long)configIdRow.get("CONFIG_DATA_ID");
                    final Row configDataItemRow = dataObject.getRow("ConfigDataItem", new Criteria(new Column("ConfigDataItem", "CONFIG_DATA_ID"), (Object)configId, 0));
                    if (configDataItemRow != null) {
                        final Long configDataItemId = (Long)configDataItemRow.get("CONFIG_DATA_ITEM_ID");
                        final JSONArray emailIds = new JSONArray();
                        final Iterator efrpAccIterator = dataObject.getRows("AndroidEFRPPolicy", new Criteria(new Column("AndroidEFRPPolicy", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemId, 0));
                        while (efrpAccIterator.hasNext()) {
                            final Row efrpAccDetailRow = efrpAccIterator.next();
                            final Long accountDetailId = (Long)efrpAccDetailRow.get("EFRP_ACC_ID");
                            final Row efrpAccountRow = dataObject.getRow("EFRPAccDetails", new Criteria(new Column("EFRPAccDetails", "EFRP_ACC_ID"), (Object)accountDetailId, 0));
                            if (efrpAccountRow != null) {
                                emailIds.put(efrpAccountRow.get("EMAIL_ID"));
                            }
                        }
                        profileData.put("emailIds", (Object)emailIds);
                    }
                    efrpInfoArray.put((Object)profileData);
                }
            }
        }
        return efrpInfoArray;
    }
    
    public String getEFRPInfoString(final long resourceId) {
        try {
            return this.getEFRPInfo(resourceId).toString();
        }
        catch (final Exception e) {
            this.out.log(Level.SEVERE, "Exception in fetching EFRP Details ", e);
            return null;
        }
    }
    
    public boolean getRemoteControlCapability(final long resourceId) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceRemoteControlCapability"));
            final Criteria resourceCriteria = new Criteria(Column.getColumn("DeviceRemoteControlCapability", "RESOURCE_ID"), (Object)resourceId, 0);
            query.setCriteria(resourceCriteria);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dO = MDMUtil.getPersistence().get(query);
            final Row row = dO.getFirstRow("DeviceRemoteControlCapability");
            return (boolean)row.get("IS_CAPABLE");
        }
        catch (final DataAccessException e) {
            this.out.log(Level.SEVERE, "Exception in fetching RemoteControlCapability Details ", (Throwable)e);
            return false;
        }
    }
    
    public JSONObject getScepCertCountDetails(final int platform) {
        return this.getScepCertCount(platform);
    }
    
    public JSONObject getScepCertCount(final int platform) {
        final JSONObject scepCount = new JSONObject();
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDScepCertificates"));
            sQuery.addJoin(new Join("MDScepCertificates", "MdCertificateResourceRel", new String[] { "CERTIFICATE_ID" }, new String[] { "CERTIFICATE_ID" }, 2));
            sQuery.addJoin(new Join("MdCertificateResourceRel", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            sQuery.setCriteria(new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0));
            final Column countColumn = new Column("MDScepCertificates", "CERTIFICATE_ID", "COUNT").count();
            countColumn.setColumnAlias("COUNT");
            sQuery.addSelectColumn(countColumn);
            final String queryCheck = RelationalAPI.getInstance().getSelectSQL((Query)sQuery);
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            Integer scepCertCount = null;
            while (ds.next()) {
                if (ds.getValue("COUNT") != null) {
                    scepCertCount = (Integer)ds.getValue("COUNT");
                }
                else {
                    scepCertCount = 0;
                }
            }
            scepCount.put("ScepCertificateCount", (Object)scepCertCount);
            return scepCount;
        }
        catch (final DataAccessException e) {
            this.out.log(Level.SEVERE, "Exception in fetching SCEP Count Details ", (Throwable)e);
        }
        catch (final JSONException e2) {
            this.out.log(Level.SEVERE, "Exception in fetching SCEP Count Details ", (Throwable)e2);
        }
        catch (final Exception e3) {
            this.out.log(Level.SEVERE, "Exception in fetching SCEP Count Details ", e3);
        }
        return scepCount;
    }
    
    public void getSafetyNetDetails(final long resId, final JSONObject detailsHash) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("SafetyNetStatus"));
            query.addSelectColumn(Column.getColumn("SafetyNetStatus", "*"));
            final Criteria deviceCrit = new Criteria(Column.getColumn("SafetyNetStatus", "RESOURCE_ID"), (Object)resId, 0);
            query.setCriteria(deviceCrit);
            final DataObject dataObject = DataAccess.get(query);
            if (!dataObject.isEmpty()) {
                final Row details = dataObject.getFirstRow("SafetyNetStatus");
                final boolean safetyNetAvailability = (boolean)details.get("SAFETYNET_AVAILABIITY");
                detailsHash.put("SAFETYNET_AVAILABIITY", (boolean)details.get("SAFETYNET_AVAILABIITY"));
                if (safetyNetAvailability) {
                    detailsHash.put("SAFETYNET_BASIC_INTEGRITY", (boolean)details.get("SAFETYNET_BASIC_INTEGRITY"));
                    detailsHash.put("SAFETYNET_CTS", (boolean)details.get("SAFETYNET_CTS"));
                    detailsHash.put("SAFETYNET_ADVICE", (Object)details.get("SAFETYNET_ADVICE"));
                }
                else {
                    detailsHash.put("SAFETYNET_ERROR_CODE", (Object)details.get("SAFETYNET_ERROR_CODE"));
                    detailsHash.put("SAFETYNET_ERROR_REASON", (Object)details.get("SAFETYNET_ERROR_REASON"));
                }
            }
            else {
                this.out.log(Level.INFO, " The device has no safety net details stored, this is very weird as this should not happen as per the workflow");
            }
        }
        catch (final Exception exp) {
            this.out.log(Level.SEVERE, "Exception while fetching safetynet details", exp);
        }
    }
    
    public String getSafetyNetDetailsString(final long resId) {
        final JSONObject tempJson = new JSONObject();
        this.getSafetyNetDetails(resId, tempJson);
        return tempJson.toString();
    }
    
    static {
        InventoryUtil.inventoryUtil = null;
    }
}
