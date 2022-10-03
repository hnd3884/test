package com.me.mdm.server.settings.wifi;

import com.adventnet.ds.query.DeleteQuery;
import java.util.Hashtable;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.List;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import java.util.ArrayList;
import com.me.mdm.server.settings.battery.MdDeviceBatteryDetailsDBHandler;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.logging.Logger;

public class MdDeviceWifiSSIDDBHandler
{
    public static Logger logger;
    public static final String unSupportedVersion = "unSupportedVersion";
    public static final String appNotInstalled = "appNotInstalled";
    public static final String privacyNotEnabled = "privacyNotEnabled";
    public static MdDeviceWifiSSIDDBHandler mdDeviceWifiSSIDDBHandler;
    private final String WIFI_SSID_NAME = "SsidName";
    private final String WIFI_SSID_ERROR_CODE = "WifissidErrorCode";
    private static HashMap<Integer, Integer> errorCodeHandling;
    
    public static MdDeviceWifiSSIDDBHandler getInstance() {
        if (MdDeviceWifiSSIDDBHandler.mdDeviceWifiSSIDDBHandler == null) {
            MdDeviceWifiSSIDDBHandler.mdDeviceWifiSSIDDBHandler = new MdDeviceWifiSSIDDBHandler();
        }
        return MdDeviceWifiSSIDDBHandler.mdDeviceWifiSSIDDBHandler;
    }
    
    public void addOrUpdateWifiSSIDDetails(final Long resourceId, final JSONArray wifiSsidArray) {
        try {
            Row latestWifiRow = this.getLatestWifiSSIDDetails(resourceId);
            Long milliSec = null;
            if (latestWifiRow != null) {
                final String latestLocalTimeString = (String)latestWifiRow.get("DEVICE_LOCAL_TIME");
                milliSec = MdDeviceBatteryDetailsDBHandler.convertDateToMilliseconds(latestLocalTimeString);
            }
            final JSONArray applicableArray = this.getApplicableWifiArray(wifiSsidArray, milliSec);
            latestWifiRow = this.addWiFiSSIDDetails(applicableArray, resourceId);
            if (latestWifiRow != null) {
                String wifiSSID = (String)latestWifiRow.get("WIFI_SSID");
                final String latestLocalTimeString2 = (String)latestWifiRow.get("DEVICE_LOCAL_TIME");
                final Integer errorCode = this.getWifiSSIDErrorCode(wifiSsidArray, latestLocalTimeString2);
                if (errorCode != null && errorCode == 35104) {
                    wifiSSID = "Not Connected to Wi-Fi";
                }
                this.updateDeviceInfo(resourceId, wifiSSID);
                final List<Long> resourceIds = new ArrayList<Long>();
                resourceIds.add(resourceId);
                this.updateWifiErrorCode(resourceIds, errorCode);
            }
        }
        catch (final Exception ex) {
            MdDeviceWifiSSIDDBHandler.logger.log(Level.SEVERE, "Exception in addorupdate wifi ssid", ex);
        }
    }
    
    private Integer getWifiSSIDErrorCode(final JSONArray wifiSsidArray, final String latestLocalTimeString) {
        for (int i = 0; i < wifiSsidArray.length(); ++i) {
            final JSONObject wifiArrayJSONObject = wifiSsidArray.getJSONObject(i);
            final String localTime = wifiArrayJSONObject.getString("DEVICE_LOCAL_TIME");
            final String wifiSSID = wifiArrayJSONObject.getString("SsidName");
            if (latestLocalTimeString.equals(localTime) && wifiArrayJSONObject.has("WifissidErrorCode")) {
                return MdDeviceWifiSSIDDBHandler.errorCodeHandling.get(wifiArrayJSONObject.getInt("WifissidErrorCode"));
            }
        }
        return null;
    }
    
    public void updateWifiErrorCode(final List<Long> resourceIds, final Integer errorCode) {
        try {
            MdDeviceWifiSSIDDBHandler.logger.log(Level.INFO, "Going to update the wifi error code:{0}", new Object[] { errorCode });
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceToErrCode"));
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            selectQuery.setCriteria(new Criteria(new Column("MdDeviceToErrCode", "RESOURCE_ID"), (Object)resourceIds.toArray(), 8));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            for (final Long resourceId : resourceIds) {
                final Criteria criteria = new Criteria(new Column("MdDeviceToErrCode", "RESOURCE_ID"), (Object)resourceId, 0);
                final Iterator iterator = dataObject.getRows("MdDeviceToErrCode", criteria);
                Row row = null;
                while (iterator.hasNext()) {
                    row = iterator.next();
                    row.set("WIFI_SSID_ERROR_CODE", (Object)errorCode);
                    dataObject.updateRow(row);
                }
                if (row == null) {
                    row = new Row("MdDeviceToErrCode");
                    row.set("RESOURCE_ID", (Object)resourceId);
                    row.set("WIFI_SSID_ERROR_CODE", (Object)errorCode);
                    dataObject.addRow(row);
                }
            }
            MDMUtil.getPersistenceLite().update(dataObject);
        }
        catch (final Exception ex) {
            MdDeviceWifiSSIDDBHandler.logger.log(Level.SEVERE, "Exception in updating the wifi error code", ex);
        }
    }
    
    private void updateDeviceInfo(final Long resourceId, final String wifiSSID) {
        try {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdNetworkInfo");
            updateQuery.setCriteria(new Criteria(new Column("MdNetworkInfo", "RESOURCE_ID"), (Object)resourceId, 0));
            updateQuery.setUpdateColumn("WIFI_SSID", (Object)wifiSSID);
            MDMUtil.getPersistence().update(updateQuery);
        }
        catch (final Exception e) {
            MdDeviceWifiSSIDDBHandler.logger.log(Level.SEVERE, "Exception in updating device info in network table", e);
        }
    }
    
    private Row addWiFiSSIDDetails(final JSONArray applicableArray, final Long resourceId) throws Exception {
        Row latestRow = null;
        MdDeviceWifiSSIDDBHandler.logger.log(Level.INFO, "Adding wifi SSID details.Applicable Array:{0}", new Object[] { applicableArray });
        if (applicableArray.length() > 0) {
            final DataObject dataObject = (DataObject)new WritableDataObject();
            for (int i = 0; i < applicableArray.length(); ++i) {
                final JSONObject wifiObject = applicableArray.getJSONObject(i);
                final Row wifiRow = new Row("MdDeviceWifiSSIDDetails");
                wifiRow.set("WIFI_SSID", (Object)wifiObject.getString("WIFI_SSID"));
                wifiRow.set("DEVICE_LOCAL_TIME", (Object)wifiObject.getString("DEVICE_LOCAL_TIME"));
                wifiRow.set("DEVICE_LOCAL_TIME_DIFFERENCE", (Object)wifiObject.getLong("DEVICE_LOCAL_TIME_DIFFERENCE"));
                wifiRow.set("DEVICE_UTC_TIME", (Object)wifiObject.getLong("DEVICE_UTC_TIME"));
                wifiRow.set("RESOURCE_ID", (Object)resourceId);
                dataObject.addRow(wifiRow);
            }
            MDMUtil.getPersistence().add(dataObject);
            dataObject.sortRows("MdDeviceWifiSSIDDetails", new SortColumn[] { new SortColumn(new Column("MdDeviceWifiSSIDDetails", "DEVICE_UTC_TIME"), false) });
            latestRow = dataObject.getFirstRow("MdDeviceWifiSSIDDetails");
        }
        return latestRow;
    }
    
    private JSONArray getApplicableWifiArray(final JSONArray wifiArray, final Long milliseconds) {
        final JSONArray applicableArray = new JSONArray();
        try {
            for (int i = 0; i < wifiArray.length(); ++i) {
                final JSONObject wifiArrayJSONObject = wifiArray.getJSONObject(i);
                if (wifiArrayJSONObject.has("DEVICE_LOCAL_TIME")) {
                    final String deviceLocalTime_date = wifiArrayJSONObject.getString("DEVICE_LOCAL_TIME");
                    Long deviceLocalTime = MdDeviceBatteryDetailsDBHandler.convertDateToMilliseconds(deviceLocalTime_date);
                    if (milliseconds == null || deviceLocalTime > milliseconds) {
                        final Long localTimeDifference = Long.parseLong(wifiArrayJSONObject.optString("DEVICE_LOCAL_TIME_DIFFERENCE", "0"));
                        final String wifiSSID = wifiArrayJSONObject.getString("SsidName");
                        final JSONObject wifiObject = new JSONObject();
                        wifiObject.put("WIFI_SSID", (Object)wifiSSID);
                        wifiObject.put("DEVICE_LOCAL_TIME", (Object)deviceLocalTime_date);
                        wifiObject.put("DEVICE_LOCAL_TIME_DIFFERENCE", (Object)localTimeDifference);
                        if (localTimeDifference != 0L) {
                            deviceLocalTime += localTimeDifference;
                        }
                        wifiObject.put("DEVICE_UTC_TIME", (Object)deviceLocalTime);
                        applicableArray.put((Object)wifiObject);
                    }
                }
            }
        }
        catch (final Exception e) {
            return wifiArray;
        }
        return applicableArray;
    }
    
    public Row getLatestWifiSSIDDetails(final Long resourceId) {
        Row latestRow = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceWifiSSIDDetails"));
            final Criteria criteria = new Criteria(new Column("MdDeviceWifiSSIDDetails", "RESOURCE_ID"), (Object)resourceId, 0);
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(new Column("MdDeviceWifiSSIDDetails", "*"));
            final Column dateColumn = new Column("MdDeviceWifiSSIDDetails", "DEVICE_UTC_TIME");
            selectQuery.addSortColumn(new SortColumn(dateColumn, false));
            final Range range = new Range(0, 1);
            selectQuery.setRange(range);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                latestRow = dataObject.getFirstRow("MdDeviceWifiSSIDDetails");
            }
        }
        catch (final Exception e) {
            MdDeviceWifiSSIDDBHandler.logger.log(Level.SEVERE, "Exception in getting latest wifi ssid", e);
        }
        return latestRow;
    }
    
    public boolean isWifiSSIDEnabledForCustomer(final DeviceDetails deviceDetails) {
        MdDeviceWifiSSIDDBHandler.logger.log(Level.INFO, "Device privacy details:{0}", new Object[] { deviceDetails.privacySettingsJSON });
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableFetchWifiSSSID")) {
            final JSONObject privacyJson = deviceDetails.privacySettingsJSON;
            final int fetchWifiSSID = privacyJson.optInt("fetch_wifi_ssid");
            if (fetchWifiSSID == 0) {
                return true;
            }
        }
        else {
            MdDeviceWifiSSIDDBHandler.logger.log(Level.INFO, "Enable Fetch wifi is disabled");
        }
        return false;
    }
    
    public void wifiSSIDHistoryDeletionTask(final Long[] customersList) throws Exception {
        final Hashtable ht = DateTimeUtil.determine_From_To_Times("today");
        if (customersList != null && ht != null) {
            for (final Long customer : customersList) {
                final int num_of_days = 7;
                MdDeviceWifiSSIDDBHandler.logger.log(Level.INFO, "Beginning the deletion of wifi ssid details older than 7 days for customer: {0}", new Object[] { customer });
                final Long today = ht.get("date1");
                final Long lastDate = today - num_of_days * 24 * 60 * 60 * 1000L;
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdDeviceWifiSSIDDetails");
                final Join deviceJoin = new Join("MdDeviceWifiSSIDDetails", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
                final Criteria customerCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customer, 0);
                deleteQuery.addJoin(deviceJoin);
                deleteQuery.setCriteria(customerCriteria.and(new Criteria(new Column("MdDeviceWifiSSIDDetails", "DEVICE_UTC_TIME"), (Object)lastDate, 6)));
                final int detailsDeleted = DataAccess.delete(deleteQuery);
                MdDeviceWifiSSIDDBHandler.logger.log(Level.INFO, "Successfully deleted {0} old wifi details for customer: {1}", new Object[] { detailsDeleted, customer });
            }
        }
    }
    
    public String getDeviceErrorRemarks(final Long deviceId) {
        String errorRemarks = "";
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceToErrCode"));
            selectQuery.addJoin(new Join("MdDeviceToErrCode", "ErrorCode", new String[] { "WIFI_SSID_ERROR_CODE" }, new String[] { "ERROR_CODE" }, 2));
            selectQuery.setCriteria(new Criteria(new Column("MdDeviceToErrCode", "RESOURCE_ID"), (Object)deviceId, 0).and(new Criteria(new Column("MdDeviceToErrCode", "WIFI_SSID_ERROR_CODE"), (Object)35104, 1)));
            selectQuery.addSelectColumn(new Column("ErrorCode", "DETAILED_DESC"));
            selectQuery.addSelectColumn(new Column("ErrorCode", "ERROR_CODE"));
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row errorRow = dataObject.getRow("ErrorCode");
                errorRemarks = (String)errorRow.get("DETAILED_DESC");
            }
        }
        catch (final Exception e) {
            MdDeviceWifiSSIDDBHandler.logger.log(Level.SEVERE, "Exception in getDeviceErrorCode", e);
        }
        return errorRemarks;
    }
    
    static {
        MdDeviceWifiSSIDDBHandler.logger = Logger.getLogger("MDMLogger");
        MdDeviceWifiSSIDDBHandler.errorCodeHandling = new HashMap<Integer, Integer>() {
            {
                this.put(1, 35104);
                this.put(2, 35103);
                this.put(3, 35102);
            }
        };
    }
}
