package com.me.mdm.server.privacy;

import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONArray;
import org.json.JSONException;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class PrivacySettingsHandler
{
    Logger logger;
    private DataObject existingDataObject;
    public static final String DEFAULT_DEVICE_NAME_PATTERN = "%username%-%model%";
    
    public PrivacySettingsHandler() {
        this.logger = Logger.getLogger("MDMLogger");
        this.existingDataObject = null;
    }
    
    public void savePrivacySettingsDetails(final JSONObject settingsJSON) {
        try {
            final Long customerId = settingsJSON.getLong("customer_id");
            this.populateExistingDO(customerId);
            this.populatePrivacySettings(settingsJSON, customerId);
            MDMUtil.getPersistence().update(this.existingDataObject);
            final org.json.simple.JSONObject oneLineLogJO = new org.json.simple.JSONObject();
            oneLineLogJO.putAll(settingsJSON.toMap());
            MDMOneLineLogger.log(Level.INFO, "DEVICE_PRIVACY_MODIFIED", oneLineLogJO);
            PrivacySettingListener.getInstance().invokePrivacySettingsChange(settingsJSON, customerId);
            this.handlePrivacySettingsMessages(settingsJSON, customerId);
            final HashMap hash = MDMUtil.getInstance().getCurrentlyLoggenOnUserInfo();
            final String sUserName = hash.get("UserName");
            MDMEventLogHandler.getInstance().MDMEventLogEntry(2070, null, sUserName, "mdm.privacy.actionlog.settings_modified", null, customerId);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while saving privacy settings", e);
        }
    }
    
    private void handlePrivacySettingsMessages(final JSONObject settingsJSON, final Long customerId) {
        try {
            final int fetchLocation = settingsJSON.optInt("fetch_location", -1);
            final int completeWipe = settingsJSON.optInt("disable_wipe", -1);
            final JSONArray applicableFor = settingsJSON.getJSONArray("applicable_for");
            final Boolean isApplicableToAll = this.isApplicableToBothDevices(applicableFor);
            if (fetchLocation == 0) {
                if (isApplicableToAll) {
                    MessageProvider.getInstance().hideMessage("MDM_DEVICE_PRIVACY_DONT_COLLECT_LOCATION", customerId);
                }
            }
            else {
                MessageProvider.getInstance().unhideMessage("MDM_DEVICE_PRIVACY_DONT_COLLECT_LOCATION", customerId);
            }
            if (completeWipe == 0) {
                if (isApplicableToAll) {
                    MessageProvider.getInstance().hideMessage("MDM_DEVICE_PRIVACY_DONT_COMPLETE_WIPE", customerId);
                }
            }
            else {
                MessageProvider.getInstance().unhideMessage("MDM_DEVICE_PRIVACY_DONT_COMPLETE_WIPE", customerId);
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in handlePrivacySettingsMessages", (Throwable)e);
        }
    }
    
    private Boolean isApplicableToBothDevices(final JSONArray applicableFor) {
        if (applicableFor.length() == 2) {
            return true;
        }
        return false;
    }
    
    public JSONObject getPrivacyDetails(final long customerId) {
        JSONObject settingsJson = null;
        try {
            this.populateExistingDO(customerId);
            settingsJson = this.getPrivacyDetailsFromExistingDO();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getPrivacyDetails", e);
        }
        return settingsJson;
    }
    
    public JSONObject getPrivacyDetails(final int ownedBy, final long customerId) {
        JSONObject privacyJSON = null;
        try {
            this.populateExistingDO(customerId, ownedBy);
            privacyJSON = this.getPrivacyDetailsFromExistingDOWithDefaultValue(ownedBy);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getPrivacyDetails", e);
        }
        return privacyJSON;
    }
    
    public HashMap getPrivacySettingsForMdDevices(final Long resourceId) {
        HashMap privacySettings = new HashMap();
        try {
            final JSONObject json = this.getPrivacySettingsJSON(resourceId);
            privacySettings = JSONUtil.getInstance().ConvertJSONObjectToHash(json);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getPrivacySettingsForMdDevices", e);
        }
        return privacySettings;
    }
    
    public JSONObject getPrivacySettingsJSON(final Long resourceId) {
        JSONObject json = new JSONObject();
        try {
            final Criteria cOwnedBy = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY"), (Object)Column.getColumn("MDPrivacyToOwnedBy", "OWNED_BY"), 0);
            final Criteria cCustomer = new Criteria(new Column("MDPrivacyToOwnedBy", "CUSTOMER_ID"), (Object)CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId), 0);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            sQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
            sQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
            sQuery.addJoin(new Join("DeviceEnrollmentRequest", "MDPrivacyToOwnedBy", cCustomer.and(cOwnedBy), 1));
            sQuery.addJoin(new Join("MDPrivacyToOwnedBy", "MDMPrivacySettings", new String[] { "PRIVACY_SETTINGS_ID" }, new String[] { "PRIVACY_SETTINGS_ID" }, 1));
            final Criteria cResource = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
            sQuery.setCriteria(cResource);
            sQuery.addSelectColumn(new Column("MDMPrivacySettings", "*"));
            sQuery.addSelectColumn(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            sQuery.addSelectColumn(new Column("DeviceEnrollmentRequest", "OWNED_BY"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (DO.containsTable("MDMPrivacySettings")) {
                final Row privacyRow = DO.getRow("MDMPrivacySettings");
                this.setPrivacyRowValues(json, privacyRow);
            }
            else {
                json = this.getDefaultPrivacySettings((int)DO.getFirstValue("DeviceEnrollmentRequest", "OWNED_BY"));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getPrivacySettingsForMdDevices", e);
        }
        return json;
    }
    
    public HashMap getPrivacySettingsForEnrollmentRquest(final Long enrollmentReuqest) {
        HashMap privacySettings = new HashMap();
        try {
            final Criteria cOwnedBy = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "OWNED_BY"), (Object)Column.getColumn("MDPrivacyToOwnedBy", "OWNED_BY"), 0);
            final Criteria cCustomer = new Criteria(new Column("MDPrivacyToOwnedBy", "CUSTOMER_ID"), (Object)this.getCustomerIdFormEnrollmentId(enrollmentReuqest), 0);
            JSONObject json = new JSONObject();
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            sQuery.addJoin(new Join("DeviceEnrollmentRequest", "MDPrivacyToOwnedBy", cCustomer.and(cOwnedBy), 1));
            sQuery.addJoin(new Join("MDPrivacyToOwnedBy", "MDMPrivacySettings", new String[] { "PRIVACY_SETTINGS_ID" }, new String[] { "PRIVACY_SETTINGS_ID" }, 1));
            final Criteria cEnrollemnt = new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentReuqest, 0);
            sQuery.setCriteria(cEnrollemnt);
            sQuery.addSelectColumn(new Column("MDMPrivacySettings", "*"));
            sQuery.addSelectColumn(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            sQuery.addSelectColumn(new Column("DeviceEnrollmentRequest", "OWNED_BY"));
            final DataObject DO = MDMUtil.getPersistence().get(sQuery);
            if (DO.containsTable("MDMPrivacySettings")) {
                final Row privacyRow = DO.getRow("MDMPrivacySettings");
                this.setPrivacyRowValues(json, privacyRow);
            }
            else {
                json = this.getDefaultPrivacySettings((int)DO.getFirstValue("DeviceEnrollmentRequest", "OWNED_BY"));
            }
            privacySettings = JSONUtil.getInstance().ConvertJSONObjectToHash(json);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getPrivacySettingsForMdDevices", e);
        }
        return privacySettings;
    }
    
    private void populatePrivacySettings(final JSONObject settingsJSON, final Long customerId) throws DataAccessException, JSONException {
        final List ownedByList = new ArrayList();
        ownedByList.add(1);
        ownedByList.add(2);
        final Row privacyRow = this.addPrivacySettings(settingsJSON);
        final JSONArray ownedByArray = settingsJSON.getJSONArray("applicable_for");
        final JSONArray ownedByArrayInt = new JSONArray();
        final List removalList = new ArrayList();
        for (int i = 0; i < ownedByArray.length(); ++i) {
            final int ownedBy = Integer.parseInt(ownedByArray.get(i).toString());
            ownedByArrayInt.put(ownedBy);
            this.addOrUpdateMDPrivacyToOwnedBy(privacyRow, customerId, ownedBy);
            removalList.add(ownedBy);
        }
        settingsJSON.put("applicable_for", (Object)ownedByArrayInt);
        ownedByList.removeAll(removalList);
        if (!ownedByList.isEmpty()) {
            this.deleteMDPrivacyToOwnedByMapping(ownedByList);
        }
    }
    
    private void deleteMDPrivacyToOwnedByMapping(final List ownedByList) throws DataAccessException {
        if (!this.existingDataObject.isEmpty()) {
            for (final int ownedBy : ownedByList) {
                final Criteria cOwnedBy = this.getOwnedByCriteria(ownedBy);
                final Row mappingRow = this.existingDataObject.getRow("MDPrivacyToOwnedBy", cOwnedBy);
                if (mappingRow != null) {
                    this.existingDataObject.deleteRow(mappingRow);
                }
            }
        }
    }
    
    private void addOrUpdateMDPrivacyToOwnedBy(final Row settingsRow, final Long customerId, final int ownedBy) throws DataAccessException {
        Row mappingRow = null;
        if (!this.existingDataObject.isEmpty()) {
            final Criteria cOwnedBy = this.getOwnedByCriteria(ownedBy);
            final Criteria cCustomerId = this.getCustomerCriteria(customerId);
            mappingRow = this.existingDataObject.getRow("MDPrivacyToOwnedBy", cOwnedBy.and(cCustomerId));
        }
        if (mappingRow == null) {
            mappingRow = new Row("MDPrivacyToOwnedBy");
            mappingRow.set("OWNED_BY", (Object)ownedBy);
            mappingRow.set("CUSTOMER_ID", (Object)customerId);
            mappingRow.set("PRIVACY_SETTINGS_ID", settingsRow.get("PRIVACY_SETTINGS_ID"));
            this.existingDataObject.addRow(mappingRow);
        }
        else {
            mappingRow.set("PRIVACY_SETTINGS_ID", settingsRow.get("PRIVACY_SETTINGS_ID"));
            this.existingDataObject.updateRow(mappingRow);
        }
    }
    
    private Row addPrivacySettings(final JSONObject settingsJSON) throws DataAccessException {
        final Row settingsRow = new Row("MDMPrivacySettings");
        settingsRow.set("FETCH_DEVICE_NAME", (Object)settingsJSON.optInt("fetch_device_name"));
        settingsRow.set("FETCH_PHONE_NUMBER", (Object)settingsJSON.optInt("fetch_phone_number"));
        settingsRow.set("FETCH_IMSI_NUMBER", (Object)settingsJSON.optString("fetch_imsi_number"));
        settingsRow.set("FETCH_INSTALLED_APPS", (Object)settingsJSON.optInt("fetch_installed_app"));
        settingsRow.set("FETCH_LOCATION", (Object)settingsJSON.optInt("fetch_location"));
        settingsRow.set("DISABLE_REMOTE_CONTROL", (Object)settingsJSON.optInt("disable_remote_control"));
        settingsRow.set("DISABLE_WIPE", (Object)settingsJSON.optInt("disable_wipe"));
        settingsRow.set("DISABLE_BUG_REPORT", (Object)settingsJSON.optInt("disable_bug_report", 2));
        settingsRow.set("VIEW_PRIVACY_SETTINGS", (Object)settingsJSON.optBoolean("view_privacy_settings"));
        settingsRow.set("DEVICE_NAME_PATTERN", (Object)settingsJSON.optString("device_name_pattern"));
        settingsRow.set("RECENT_USERS_REPORT", (Object)settingsJSON.optInt("recent_users_report"));
        settingsRow.set("DEVICE_STATE_REPORT", (Object)settingsJSON.optInt("device_state_report"));
        settingsRow.set("FETCH_MAC_ADDRESS", (Object)settingsJSON.optInt("fetch_mac_address"));
        settingsRow.set("FETCH_USER_INSTALLED_CERTS", (Object)settingsJSON.optInt("fetch_user_installed_certs", 0));
        settingsRow.set("DISABLE_CLEAR_PASSCODE", (Object)settingsJSON.optInt("disable_clear_passcode", 0));
        settingsRow.set("FETCH_WIFI_SSID", (Object)settingsJSON.optInt("fetch_wifi_ssid", 2));
        this.existingDataObject.addRow(settingsRow);
        return settingsRow;
    }
    
    private JSONObject getPrivacyDetailsFromExistingDOWithDefaultValue(final int ownedBy) throws DataAccessException, JSONException {
        if (this.existingDataObject == null || this.existingDataObject.isEmpty()) {
            return this.getDefaultPrivacySettings(ownedBy);
        }
        return this.getPrivacyDetailsFromExistingDO();
    }
    
    private JSONObject getPrivacyDetailsFromExistingDO() throws DataAccessException, JSONException {
        JSONObject json = new JSONObject();
        if (this.existingDataObject != null && !this.existingDataObject.isEmpty()) {
            final Row privacySettings = this.existingDataObject.getFirstRow("MDMPrivacySettings");
            json = this.setPrivacyRowValues(json, privacySettings);
            final Iterator ownedByIter = this.existingDataObject.getRows("MDPrivacyToOwnedBy");
            final JSONArray ownedByJSON = new JSONArray();
            while (ownedByIter.hasNext()) {
                final Row ownedBy = ownedByIter.next();
                ownedByJSON.put(ownedBy.get("OWNED_BY"));
            }
            json.put("applicable_for", (Object)ownedByJSON);
        }
        return json;
    }
    
    private JSONObject setPrivacyRowValues(final JSONObject json, final Row privacySettings) throws JSONException {
        json.put("fetch_device_name", privacySettings.get("FETCH_DEVICE_NAME"));
        json.put("fetch_phone_number", privacySettings.get("FETCH_PHONE_NUMBER"));
        json.put("fetch_imsi_number", privacySettings.get("FETCH_IMSI_NUMBER"));
        json.put("fetch_installed_app", privacySettings.get("FETCH_INSTALLED_APPS"));
        json.put("fetch_location", privacySettings.get("FETCH_LOCATION"));
        json.put("disable_wipe", privacySettings.get("DISABLE_WIPE"));
        json.put("disable_bug_report", privacySettings.get("DISABLE_BUG_REPORT"));
        json.put("disable_remote_control", privacySettings.get("DISABLE_REMOTE_CONTROL"));
        json.put("view_privacy_settings", privacySettings.get("VIEW_PRIVACY_SETTINGS"));
        json.put("device_name_pattern", privacySettings.get("DEVICE_NAME_PATTERN"));
        json.put("device_state_report", privacySettings.get("DEVICE_STATE_REPORT"));
        json.put("recent_users_report", privacySettings.get("RECENT_USERS_REPORT"));
        json.put("fetch_mac_address", privacySettings.get("FETCH_MAC_ADDRESS"));
        json.put("fetch_user_installed_certs", privacySettings.get("FETCH_USER_INSTALLED_CERTS"));
        json.put("disable_clear_passcode", privacySettings.get("DISABLE_CLEAR_PASSCODE"));
        json.put("fetch_wifi_ssid", privacySettings.get("FETCH_WIFI_SSID"));
        return json;
    }
    
    private void populateExistingDO(final Criteria cCriteria) throws DataAccessException {
        final SelectQuery sQuery = this.getPrivacySettingsSelectQuery(cCriteria);
        this.existingDataObject = MDMUtil.getPersistence().get(sQuery);
    }
    
    private void populateExistingDO(final Long customerId) throws DataAccessException {
        final Criteria cCustomerId = this.getCustomerCriteria(customerId);
        this.populateExistingDO(cCustomerId);
    }
    
    private void populateExistingDO(final Long customerId, final int ownedBy) throws DataAccessException {
        final Criteria cOwnedBy = this.getOwnedByCriteria(ownedBy);
        final Criteria cCustomerId = this.getCustomerCriteria(customerId);
        this.populateExistingDO(cCustomerId.and(cOwnedBy));
    }
    
    public JSONObject getDefaultPrivacySettings(final int ownedBy) throws JSONException {
        final JSONObject json = new JSONObject();
        if (ownedBy == 1) {
            json.put("fetch_device_name", 0);
            json.put("fetch_phone_number", 0);
            json.put("fetch_imsi_number", 0);
            json.put("fetch_installed_app", 0);
            json.put("fetch_location", 0);
            json.put("disable_wipe", 0);
            json.put("disable_remote_control", 1);
            json.put("disable_bug_report", 1);
            json.put("view_privacy_settings", true);
            json.put("device_state_report", 0);
            json.put("recent_users_report", 0);
            json.put("fetch_mac_address", 0);
            json.put("fetch_user_installed_certs", 0);
            json.put("disable_clear_passcode", 0);
            json.put("fetch_wifi_ssid", 2);
        }
        else {
            json.put("fetch_device_name", 2);
            json.put("fetch_phone_number", 2);
            json.put("fetch_imsi_number", 2);
            json.put("fetch_installed_app", 2);
            json.put("fetch_location", 2);
            json.put("disable_wipe", 2);
            json.put("disable_remote_control", 2);
            json.put("disable_bug_report", 2);
            json.put("view_privacy_settings", true);
            json.put("device_state_report", 2);
            json.put("recent_users_report", 2);
            json.put("fetch_mac_address", 2);
            json.put("fetch_user_installed_certs", 2);
            json.put("disable_clear_passcode", 2);
            json.put("fetch_wifi_ssid", 2);
        }
        return json;
    }
    
    private Criteria getCustomerCriteria(final long customerId) {
        final Criteria cCustomerId = new Criteria(new Column("MDPrivacyToOwnedBy", "CUSTOMER_ID"), (Object)customerId, 0);
        return cCustomerId;
    }
    
    private Criteria getOwnedByCriteria(final int ownedBy) {
        final Criteria cOwnedBy = new Criteria(new Column("MDPrivacyToOwnedBy", "OWNED_BY"), (Object)ownedBy, 0);
        return cOwnedBy;
    }
    
    private SelectQuery getPrivacySettingsSelectQuery(final Criteria cCriteria) {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMPrivacySettings"));
        sQuery.addJoin(new Join("MDMPrivacySettings", "MDPrivacyToOwnedBy", new String[] { "PRIVACY_SETTINGS_ID" }, new String[] { "PRIVACY_SETTINGS_ID" }, 2));
        if (cCriteria != null) {
            sQuery.setCriteria(cCriteria);
        }
        sQuery.addSelectColumn(Column.getColumn("MDMPrivacySettings", "*"));
        sQuery.addSelectColumn(Column.getColumn("MDPrivacyToOwnedBy", "*"));
        return sQuery;
    }
    
    private long getCustomerIdFormEnrollmentId(final Long enrollmentReq) throws Exception {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
        sQuery.addJoin(new Join("DeviceEnrollmentRequest", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.setCriteria(new Criteria(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)enrollmentReq, 0));
        sQuery.addSelectColumn(Column.getColumn("Resource", "*"));
        try {
            final DataObject dObj = MDMUtil.getPersistence().get(sQuery);
            if (!dObj.isEmpty()) {
                final Long customerId = (Long)dObj.getFirstValue("Resource", "CUSTOMER_ID");
                return customerId;
            }
        }
        catch (final Exception e) {
            throw e;
        }
        return 0L;
    }
}
