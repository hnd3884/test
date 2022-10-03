package com.me.mdm.core.enrollment;

import java.util.Hashtable;
import java.util.List;
import java.util.HashMap;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.Join;
import java.util.Iterator;
import java.util.Properties;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AdminDeviceHandler
{
    public static Logger logger;
    
    public void addOrUpdateAdminDevice(final JSONObject deviceInfo) {
        try {
            final String udid = String.valueOf(deviceInfo.get("UDID"));
            final Long loginId = deviceInfo.getLong("LOGIN_ID");
            this.handleDuplicateAdminDevice(deviceInfo);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AndroidAdminDeviceDetails"));
            final Criteria udidCriteria = new Criteria(new Column("AndroidAdminDeviceDetails", "UDID"), (Object)udid, 0);
            final Criteria loginIdCriteria = new Criteria(new Column("AndroidAdminDeviceDetails", "LOGIN_ID"), (Object)loginId, 0);
            sQuery.setCriteria(udidCriteria);
            sQuery.addSelectColumn(new Column("AndroidAdminDeviceDetails", "ADMIN_DEVICE_ID"));
            sQuery.addSelectColumn(new Column("AndroidAdminDeviceDetails", "AGENT_VERSION"));
            sQuery.addSelectColumn(new Column("AndroidAdminDeviceDetails", "AGENT_VERSION_CODE"));
            sQuery.addSelectColumn(new Column("AndroidAdminDeviceDetails", "IMEI"));
            sQuery.addSelectColumn(new Column("AndroidAdminDeviceDetails", "SERIAL_NUMBER"));
            sQuery.addSelectColumn(new Column("AndroidAdminDeviceDetails", "LAST_CONTACTED_TIME"));
            sQuery.addSelectColumn(new Column("AndroidAdminDeviceDetails", "LAST_SYNC_TIME"));
            sQuery.addSelectColumn(new Column("AndroidAdminDeviceDetails", "LOGIN_ID"));
            sQuery.addSelectColumn(new Column("AndroidAdminDeviceDetails", "MODEL_NAME"));
            sQuery.addSelectColumn(new Column("AndroidAdminDeviceDetails", "NOTIFIED_AGENT_VERSION"));
            sQuery.addSelectColumn(new Column("AndroidAdminDeviceDetails", "REGISTERED_TIME"));
            sQuery.addSelectColumn(new Column("AndroidAdminDeviceDetails", "UDID"));
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            if (!dO.isEmpty()) {
                Row deviceRow = dO.getRow("AndroidAdminDeviceDetails", loginIdCriteria);
                if (deviceRow != null) {
                    dO.updateRow(this.getAndroidAdminDetailRow(deviceInfo, deviceRow));
                }
                else {
                    this.removeAdminDevice(udid);
                    deviceRow = this.getAndroidAdminDetailRow(deviceInfo, null);
                    deviceRow.set("REGISTERED_TIME", (Object)System.currentTimeMillis());
                    deviceRow.set("LAST_CONTACTED_TIME", (Object)System.currentTimeMillis());
                    deviceRow.set("LAST_SYNC_TIME", (Object)System.currentTimeMillis());
                    dO.addRow(deviceRow);
                }
            }
            else {
                final Row deviceRow = this.getAndroidAdminDetailRow(deviceInfo, null);
                deviceRow.set("REGISTERED_TIME", (Object)System.currentTimeMillis());
                deviceRow.set("LAST_CONTACTED_TIME", (Object)System.currentTimeMillis());
                deviceRow.set("LAST_SYNC_TIME", (Object)System.currentTimeMillis());
                dO.addRow(deviceRow);
            }
            MDMUtil.getPersistence().update(dO);
        }
        catch (final Exception ex) {
            AdminDeviceHandler.logger.log(Level.SEVERE, "Exception while adding or updating Admin device details", ex);
        }
    }
    
    private Row getAndroidAdminDetailRow(final JSONObject deviceInfo, Row deviceRow) throws JSONException {
        final String udid = String.valueOf(deviceInfo.get("UDID"));
        final Long loginId = deviceInfo.optLong("LOGIN_ID", -1L);
        String imei = deviceInfo.optString("IMEI", (String)null);
        final String serialNumber = deviceInfo.optString("SERIAL_NUMBER", (String)null);
        final String modelName = deviceInfo.optString("MODEL_NAME", (String)null);
        final String version = deviceInfo.optString("AGENT_VERSION", (String)null);
        final String notifiedVersion = deviceInfo.optString("NOTIFIED_AGENT_VERSION", (String)null);
        final Integer versionCode = deviceInfo.optInt("AGENT_VERSION_CODE", -1);
        if (deviceRow == null) {
            deviceRow = new Row("AndroidAdminDeviceDetails");
        }
        deviceRow.set("UDID", (Object)udid);
        if (loginId != null && loginId != -1L) {
            deviceRow.set("LOGIN_ID", (Object)loginId);
        }
        if (imei != null) {
            imei = imei.replace(" ", "");
            deviceRow.set("IMEI", (Object)imei);
        }
        if (serialNumber != null) {
            deviceRow.set("SERIAL_NUMBER", (Object)serialNumber);
        }
        if (modelName != null) {
            deviceRow.set("MODEL_NAME", (Object)modelName);
        }
        if (version != null) {
            deviceRow.set("AGENT_VERSION", (Object)version);
        }
        if (versionCode != null && versionCode != -1) {
            deviceRow.set("AGENT_VERSION_CODE", (Object)versionCode);
        }
        if (notifiedVersion != null) {
            deviceRow.set("NOTIFIED_AGENT_VERSION", (Object)notifiedVersion);
        }
        return deviceRow;
    }
    
    public boolean isDeviceRegisteredForAdmin(final String udid, final Long loginId) throws Exception {
        final Long loginIdFromDB = (Long)DBUtil.getValueFromDB("AndroidAdminDeviceDetails", "UDID", (Object)udid, "LOGIN_ID");
        return loginIdFromDB != null && loginIdFromDB.equals(loginId);
    }
    
    public String getAdminDeviceModelName(final String udid) throws Exception {
        final String modelName = (String)DBUtil.getValueFromDB("AndroidAdminDeviceDetails", "UDID", (Object)udid, "MODEL_NAME");
        return modelName;
    }
    
    public boolean isDeviceRegistered(final String udid) throws Exception {
        final Long loginIdFromDB = (Long)DBUtil.getValueFromDB("AndroidAdminDeviceDetails", "UDID", (Object)udid, "LOGIN_ID");
        return loginIdFromDB != null;
    }
    
    public Long getLoggedInUserId(final String udid) throws Exception {
        final Long loginIdFromDB = (Long)DBUtil.getValueFromDB("AndroidAdminDeviceDetails", "UDID", (Object)udid, "LOGIN_ID");
        return loginIdFromDB;
    }
    
    public void removeAdminDevice(final String udid) throws DataAccessException {
        final Criteria udidCriteria = new Criteria(new Column("AndroidAdminDeviceDetails", "UDID"), (Object)udid, 0);
        DataAccess.delete("AndroidAdminDeviceDetails", udidCriteria);
    }
    
    public void updateAdminDeviceLastContactedTime(final String udid) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("AndroidAdminDeviceDetails");
            uQuery.setUpdateColumn("LAST_CONTACTED_TIME", (Object)System.currentTimeMillis());
            uQuery.setCriteria(new Criteria(new Column("AndroidAdminDeviceDetails", "UDID"), (Object)udid, 0));
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final DataAccessException ex) {
            AdminDeviceHandler.logger.log(Level.SEVERE, "Exception in updateAdminDeviceLastContactedTime {0}", (Throwable)ex);
        }
    }
    
    public int getAdminDeviceCount(final Long loginId) throws Exception {
        try {
            return DBUtil.getRecordActualCount("AndroidAdminDeviceDetails", "LOGIN_ID", new Criteria(Column.getColumn("AndroidAdminDeviceDetails", "LOGIN_ID"), (Object)loginId, 0));
        }
        catch (final Exception ex) {
            AdminDeviceHandler.logger.log(Level.SEVERE, "Exception in getAdminDeviceCount {0}", ex);
            throw ex;
        }
    }
    
    public String getAdminAgentURL() throws Exception {
        final String serverUrl = MDMEnrollmentUtil.getInstance().getServerBaseURL();
        return serverUrl + "/mdm/admin/app";
    }
    
    public String getAdminDeviceModel(final Long loginId) throws DataAccessException {
        final DataObject dobj = MDMUtil.getPersistence().get("AndroidAdminDeviceDetails", new Criteria(Column.getColumn("AndroidAdminDeviceDetails", "LOGIN_ID"), (Object)loginId, 0));
        if (!dobj.isEmpty()) {
            return (String)dobj.getFirstValue("AndroidAdminDeviceDetails", "MODEL_NAME");
        }
        return null;
    }
    
    public void updateAdminDeviceLastSyncTime(final Long loginId, final Long syncTime) {
        try {
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("AndroidAdminDeviceDetails");
            uQuery.setUpdateColumn("LAST_SYNC_TIME", (Object)syncTime);
            uQuery.setCriteria(new Criteria(new Column("AndroidAdminDeviceDetails", "LOGIN_ID"), (Object)loginId, 0));
            MDMUtil.getPersistence().update(uQuery);
        }
        catch (final DataAccessException ex) {
            AdminDeviceHandler.logger.log(Level.SEVERE, "Exception in updateAdminDeviceLastSyncTime {0}", (Throwable)ex);
        }
    }
    
    public Long getLastSyncTime(final String udid) throws Exception {
        final Long lastSyncTime = (Long)DBUtil.getValueFromDB("AndroidAdminDeviceDetails", "UDID", (Object)udid, "LAST_SYNC_TIME");
        if (lastSyncTime != null) {
            return lastSyncTime;
        }
        return -1L;
    }
    
    public ArrayList getDevicesYetToNotifyForUpgrade() {
        final Properties prop = MDMUtil.getMDMServerInfo();
        final String agentVersion = ((Hashtable<K, String>)prop).get("ANDROID_ADMIN_VERSION");
        final Long agentVersionCode = ((Hashtable<K, Long>)prop).get("ANDROID_ADMIN_VERSION_CODE");
        final Criteria cAgentVersion = new Criteria(new Column("AndroidAdminDeviceDetails", "AGENT_VERSION"), (Object)agentVersion, 1);
        final Criteria cEmptyVersion = new Criteria(new Column("AndroidAdminDeviceDetails", "AGENT_VERSION"), (Object)"--", 1);
        final Criteria cAgentNotVersion = new Criteria(new Column("AndroidAdminDeviceDetails", "NOTIFIED_AGENT_VERSION"), (Object)agentVersion, 1);
        final Criteria cAgentVersionCode = new Criteria(new Column("AndroidAdminDeviceDetails", "AGENT_VERSION_CODE"), (Object)agentVersionCode, 7);
        final Criteria criteria = cAgentVersionCode.and(cAgentVersion).and(cAgentNotVersion).and(cEmptyVersion);
        return this.getAdminDeviceUDID(criteria);
    }
    
    private ArrayList getAdminDeviceUDID(final Criteria criteria) {
        final ArrayList<String> udids = new ArrayList<String>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AndroidAdminDeviceDetails"));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("AndroidAdminDeviceDetails");
                while (iterator.hasNext()) {
                    final Row managedDeviceRow = iterator.next();
                    udids.add((String)managedDeviceRow.get("UDID"));
                }
            }
        }
        catch (final Exception ex) {
            AdminDeviceHandler.logger.log(Level.WARNING, "Exception in getAdminDeviceUDID methods : {0}", ex);
        }
        return udids;
    }
    
    private void handleDuplicateAdminDevice(final JSONObject adminDeviceJSON) {
        final String udid = adminDeviceJSON.optString("UDID", (String)null);
        final String imei = adminDeviceJSON.optString("IMEI", (String)null);
        final String serialNo = adminDeviceJSON.optString("SERIAL_NUMBER", (String)null);
        if (udid == null) {
            return;
        }
        if ((imei == null || imei.isEmpty() || imei.equalsIgnoreCase("--")) && (serialNo == null || serialNo.isEmpty() || serialNo.equalsIgnoreCase("--"))) {
            return;
        }
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AndroidAdminDeviceDetails"));
            sQuery.addSelectColumn(new Column((String)null, "*"));
            Criteria criteria = null;
            if (serialNo != null && !serialNo.isEmpty() && !serialNo.equalsIgnoreCase("--")) {
                final Criteria serialCriteria = criteria = new Criteria(new Column("AndroidAdminDeviceDetails", "SERIAL_NUMBER"), (Object)serialNo, 0);
            }
            if (imei != null && !imei.isEmpty() && !imei.equalsIgnoreCase("--")) {
                final Criteria imeiCriteria = new Criteria(new Column("AndroidAdminDeviceDetails", "IMEI"), (Object)imei, 0);
                if (criteria == null) {
                    criteria = imeiCriteria;
                }
                else {
                    criteria = criteria.or(imeiCriteria);
                }
            }
            sQuery.setCriteria(criteria);
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            if (!dO.isEmpty()) {
                final Row row = dO.getFirstRow("AndroidAdminDeviceDetails");
                final String oldUdid = (String)row.get("UDID");
                row.set("UDID", (Object)udid);
                dO.updateRow(row);
                MDMUtil.getPersistence().update(dO);
                AdminDeviceHandler.logger.log(Level.INFO, "Admin Duplicate Device Found changing the UDID from {0} to {1}", new Object[] { oldUdid, udid });
            }
        }
        catch (final Exception ex) {
            AdminDeviceHandler.logger.log(Level.WARNING, "Exception occurred while converting enrollment data to JSON object into handleDuplicateAndroidDevice", ex);
        }
    }
    
    public void setTemplateTokens(final JSONObject responseJSON, final Long loginId) {
        final String lastSyncTimeKEY = "LastSyncTime";
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AndroidAdminEnrollmentTemplate"));
            final Join enrollmentTemplateJoin = new Join("AndroidAdminEnrollmentTemplate", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2);
            sQuery.addJoin(enrollmentTemplateJoin);
            sQuery.setCriteria(new Criteria(new Column("AndroidAdminEnrollmentTemplate", "LOGIN_ID"), (Object)loginId, 0));
            sQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            if (!dO.isEmpty()) {
                final List<HashMap> customerList = CustomerInfoUtil.getInstance().getCustomerDetailsForUser(DMUserHandler.getDCUserID(loginId));
                final JSONArray responseArray = new JSONArray();
                for (int i = 0; i < customerList.size(); ++i) {
                    final Long customerId = customerList.get(i).get("CUSTOMER_ID");
                    final JSONObject customerJSON = new JSONObject();
                    customerJSON.put("CustomerID", (Object)String.valueOf(customerId));
                    customerJSON.put("CustomerName", (Object)customerList.get(i).get("CUSTOMER_NAME"));
                    final JSONArray templateArray = new JSONArray();
                    final Iterator it = dO.getRows("EnrollmentTemplate", new Criteria(new Column("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerId, 0));
                    while (it.hasNext()) {
                        final Row enrollRow = it.next();
                        final JSONObject templateJSON = new JSONObject();
                        templateJSON.put("TemplateID", (Object)String.valueOf(enrollRow.get("TEMPLATE_ID")));
                        templateJSON.put("TemplateName", (Object)enrollRow.get("TEMPLATE_NAME"));
                        templateJSON.put("TemplateToken", (Object)enrollRow.get("TEMPLATE_TOKEN"));
                        templateArray.put((Object)templateJSON);
                    }
                    customerJSON.put("TemplateDetails", (Object)templateArray);
                    responseArray.put((Object)customerJSON);
                }
                responseJSON.put("CustomerDetails", (Object)responseArray);
            }
            else {
                responseJSON.put("ErrorMsg", (Object)"No Customer/No Template Token Found");
                responseJSON.put("ErrorCode", -4001);
            }
            responseJSON.put(lastSyncTimeKEY, System.currentTimeMillis());
        }
        catch (final Exception e) {
            AdminDeviceHandler.logger.log(Level.WARNING, "Cannot fetch template tokens", e);
        }
    }
    
    static {
        AdminDeviceHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
