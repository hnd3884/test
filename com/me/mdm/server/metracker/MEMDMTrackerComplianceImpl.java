package com.me.mdm.server.metracker;

import com.adventnet.persistence.DataObject;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import java.util.Set;
import org.json.simple.JSONArray;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackerComplianceImpl extends MEMDMTrackerConstants
{
    private Properties mdmTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerComplianceImpl() {
        this.mdmTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCTrackerMDMPComplianceImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getTrackerProperties", "MDMP Device Compliance implementation begins");
            if (!this.mdmTrackerProperties.isEmpty()) {
                this.mdmTrackerProperties = new Properties();
            }
            this.addComplianceCount();
            this.addComplianceCreationCount();
            this.addComplianceDevicesDistributionCount();
            this.addComplianceActionCount();
            this.addComplianceRuleTypeCount();
            this.addComplianceActionTypeCount();
            this.addComplianceGroupsDistributionCount();
            this.addComplianceErrorCount();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.mdmTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getTrackerProperties", "Exception ", (Throwable)e);
        }
        return this.mdmTrackerProperties;
    }
    
    private void addComplianceActionTypeCount() {
        try {
            final SelectQuery query = MDMCoreQuery.getInstance().getMDMQueryMap("DEVICE_COMPLIANCE_ACTION_TYPE_QUERY");
            final JSONArray queryJSONArray = MDMUtil.executeSelectQuery(query);
            final JSONObject tempJSON = new JSONObject();
            for (int i = 0; i < queryJSONArray.size(); ++i) {
                final org.json.simple.JSONObject queryJSON = (org.json.simple.JSONObject)queryJSONArray.get(i);
                final Set keySet = queryJSON.keySet();
                for (final String key : keySet) {
                    tempJSON.put(key, (Object)queryJSON.get((Object)key).toString());
                }
                this.mdmTrackerProperties.setProperty("MDM_COMPLIANCE_ACTION_TYPE_COUNT", tempJSON.toString());
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- addComplianceActionTypeCount()    >   Error", (Throwable)e);
        }
    }
    
    private void addComplianceRuleTypeCount() {
        try {
            final SelectQuery query = MDMCoreQuery.getInstance().getMDMQueryMap("DEVICE_COMPLIANCE_RULE_TYPE_QUERY");
            final JSONArray queryJSONArray = MDMUtil.executeSelectQuery(query);
            final JSONObject tempJSON = new JSONObject();
            for (int i = 0; i < queryJSONArray.size(); ++i) {
                final org.json.simple.JSONObject queryJSON = (org.json.simple.JSONObject)queryJSONArray.get(i);
                final Set keySet = queryJSON.keySet();
                for (final String key : keySet) {
                    tempJSON.put(key, (Object)queryJSON.get((Object)key).toString());
                }
                this.mdmTrackerProperties.setProperty("MDM_COMPLIANCE_RULE_TYPE_COUNT", tempJSON.toString());
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- addComplianceRuleTypeCount()    >   Error", (Throwable)e);
        }
    }
    
    private void addComplianceActionCount() {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MEMDMTrackParams"));
            final List selectList = new ArrayList();
            selectList.add("MDM_COMPLIANCE_REPORT_GENERATION_COUNT");
            selectList.add("MDM_COMPLIANCE_RUN_COMPLIANCE_CHECK_COUNT");
            query.addSelectColumn(new Column("MEMDMTrackParams", "*"));
            query.setCriteria(new Criteria(new Column("MEMDMTrackParams", "PARAM_NAME"), (Object)selectList.toArray(), 8));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final Iterator iterator = dataObject.getRows("MEMDMTrackParams");
            final JSONObject tempJSON = new JSONObject();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String paramName = (String)row.get("PARAM_NAME");
                final String value = (String)row.get("PARAM_VALUE");
                tempJSON.put(paramName, (Object)value);
            }
            this.mdmTrackerProperties.setProperty("MDM_COMPLIANCE_ACTION_COUNT", tempJSON.toString());
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- addComplianceActionCount()    >   Error", e);
        }
    }
    
    private void addComplianceDevicesDistributionCount() {
        try {
            final SelectQuery query = MDMCoreQuery.getInstance().getMDMQueryMap("DEVICE_COMPLIANCE_DEVICES_DISTRIBUTION_QUERY");
            final JSONArray queryJSONArray = MDMUtil.executeSelectQuery(query);
            final JSONObject tempJSON = new JSONObject();
            for (int i = 0; i < queryJSONArray.size(); ++i) {
                final org.json.simple.JSONObject queryJSON = (org.json.simple.JSONObject)queryJSONArray.get(i);
                tempJSON.put("MDM_COMPLIANCE_DISTRIBUTION_ANDROID_COUNT", (Object)((queryJSON.get((Object)"MDM_COMPLIANCE_DISTRIBUTION_ANDROID_COUNT") == null) ? Integer.valueOf(0) : queryJSON.get((Object)"MDM_COMPLIANCE_DISTRIBUTION_ANDROID_COUNT").toString()));
                tempJSON.put("MDM_COMPLIANCE_DISTRIBUTION_IOS_COUNT", (Object)((queryJSON.get((Object)"MDM_COMPLIANCE_DISTRIBUTION_IOS_COUNT") == null) ? Integer.valueOf(0) : queryJSON.get((Object)"MDM_COMPLIANCE_DISTRIBUTION_IOS_COUNT").toString()));
                tempJSON.put("MDM_COMPLIANCE_DISTRIBUTION_WINDOWS_COUNT", (Object)((queryJSON.get((Object)"MDM_COMPLIANCE_DISTRIBUTION_WINDOWS_COUNT") == null) ? Integer.valueOf(0) : queryJSON.get((Object)"MDM_COMPLIANCE_DISTRIBUTION_WINDOWS_COUNT").toString()));
                tempJSON.put("MDM_COMPLIANCE_DEVICE_ASSOCIATION_COUNT", (Object)((queryJSON.get((Object)"MDM_COMPLIANCE_DEVICE_ASSOCIATION_COUNT") == null) ? Integer.valueOf(0) : queryJSON.get((Object)"MDM_COMPLIANCE_DEVICE_ASSOCIATION_COUNT").toString()));
                tempJSON.put("MDM_COMPLIANCE_DEVICE_DISASSOCIATION_COUNT", (Object)((queryJSON.get((Object)"MDM_COMPLIANCE_DEVICE_DISASSOCIATION_COUNT") == null) ? Integer.valueOf(0) : queryJSON.get((Object)"MDM_COMPLIANCE_DEVICE_DISASSOCIATION_COUNT").toString()));
                this.mdmTrackerProperties.setProperty("MDM_COMPLIANCE_DISTRIBUTION_COUNT", tempJSON.toString());
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- addComplianceDevicesDistributionCount()    >   Error", (Throwable)e);
        }
    }
    
    private void addComplianceCreationCount() {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("MEMDMTrackParams"));
            final List selectList = new ArrayList();
            selectList.add("MDM_COMPLIANCE_TAB_CLICK_COUNT");
            selectList.add("MDM_COMPLIANCE_SAVE_CLICK_COUNT");
            query.addSelectColumn(new Column("MEMDMTrackParams", "*"));
            query.setCriteria(new Criteria(new Column("MEMDMTrackParams", "PARAM_NAME"), (Object)selectList.toArray(), 8));
            final DataObject dataObject = MDMUtil.getPersistence().get(query);
            final Iterator iterator = dataObject.getRows("MEMDMTrackParams");
            final JSONObject tempJSON = new JSONObject();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String paramName = (String)row.get("PARAM_NAME");
                final String value = (String)row.get("PARAM_VALUE");
                tempJSON.put(paramName, (Object)value);
            }
            this.mdmTrackerProperties.setProperty("MDM_COMPLIANCE_CREATION", tempJSON.toString());
        }
        catch (final DataAccessException | JSONException e) {
            this.logger.log(Level.SEVERE, " -- addComplianceCreationCount()    >   Error", e);
        }
    }
    
    private void addComplianceCount() {
        try {
            final SelectQuery query = MDMCoreQuery.getInstance().getMDMQueryMap("DEVICE_COMPLIANCE_QUERY");
            final JSONArray queryJSONArray = MDMUtil.executeSelectQuery(query);
            final JSONObject tempJSON = new JSONObject();
            for (int i = 0; i < queryJSONArray.size(); ++i) {
                final org.json.simple.JSONObject queryJSON = (org.json.simple.JSONObject)queryJSONArray.get(i);
                tempJSON.put("MDM_COMPLIANCE_TOTAL_COUNT", (Object)((queryJSON.get((Object)"MDM_COMPLIANCE_TOTAL_COUNT") == null) ? Integer.valueOf(0) : queryJSON.get((Object)"MDM_COMPLIANCE_TOTAL_COUNT").toString()));
                tempJSON.put("MDM_COMPLIANCE_TRASH_COUNT", (Object)((queryJSON.get((Object)"MDM_COMPLIANCE_TRASH_COUNT") == null) ? Integer.valueOf(0) : queryJSON.get((Object)"MDM_COMPLIANCE_TRASH_COUNT").toString()));
                tempJSON.put("MDM_COMPLIANCE_UNUSED_COUNT", (Object)((queryJSON.get((Object)"MDM_COMPLIANCE_UNUSED_COUNT") == null) ? Integer.valueOf(0) : queryJSON.get((Object)"MDM_COMPLIANCE_UNUSED_COUNT").toString()));
                this.mdmTrackerProperties.setProperty("MDM_COMPLIANCE_COUNT", tempJSON.toString());
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- addComplianceCount()    >   Error", (Throwable)e);
        }
    }
    
    private void addComplianceGroupsDistributionCount() {
        try {
            final SelectQuery query = MDMCoreQuery.getInstance().getMDMQueryMap("DEVICE_COMPLIANCE_GROUPS_DISTRIBUTION_QUERY");
            final JSONArray queryJSONArray = MDMUtil.executeSelectQuery(query);
            final JSONObject tempJSON = new JSONObject();
            for (int i = 0; i < queryJSONArray.size(); ++i) {
                final org.json.simple.JSONObject queryJSON = (org.json.simple.JSONObject)queryJSONArray.get(i);
                tempJSON.put("MDM_COMPLIANCE_GROUP_ASSOCIATION_COUNT", (Object)((queryJSON.get((Object)"MDM_COMPLIANCE_GROUP_ASSOCIATION_COUNT") == null) ? Integer.valueOf(0) : queryJSON.get((Object)"MDM_COMPLIANCE_GROUP_ASSOCIATION_COUNT").toString()));
                tempJSON.put("MDM_COMPLIANCE_GROUP_DISASSOCIATION_COUNT", (Object)((queryJSON.get((Object)"MDM_COMPLIANCE_GROUP_DISASSOCIATION_COUNT") == null) ? Integer.valueOf(0) : queryJSON.get((Object)"MDM_COMPLIANCE_GROUP_DISASSOCIATION_COUNT").toString()));
                this.mdmTrackerProperties.setProperty("MDM_COMPLIANCE_DISTRIBUTION_COUNT", tempJSON.toString());
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- addComplianceDevicesDistributionCount()    >   Error", (Throwable)e);
        }
    }
    
    private void addComplianceErrorCount() {
        try {
            final SelectQuery query = MDMCoreQuery.getInstance().getMDMQueryMap("DEVICE_COMPLIANCE_ERROR_QUERY");
            final JSONArray queryJSONArray = MDMUtil.executeSelectQuery(query);
            final JSONObject tempJSON = new JSONObject();
            for (int i = 0; i < queryJSONArray.size(); ++i) {
                final org.json.simple.JSONObject queryJSON = (org.json.simple.JSONObject)queryJSONArray.get(i);
                final Set keySet = queryJSON.keySet();
                for (final String key : keySet) {
                    tempJSON.put(key, (Object)queryJSON.get((Object)key).toString());
                }
                this.mdmTrackerProperties.setProperty("MDM_COMPLIANCE_ERROR_TYPE_COUNT", tempJSON.toString());
            }
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- addComplianceErrorCount()    >   Error", (Throwable)e);
        }
    }
}
