package com.me.mdm.onpremise.server.metracker;

import java.util.Hashtable;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONException;
import com.me.mdm.onpremise.server.integration.MDMIntegrationUtil;
import com.me.mdm.onpremise.server.integration.sdp.MDMSDPIntegrationUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;
import com.me.mdm.server.metracker.MEMDMTrackerConstants;

public class MEMDMTrackerIntegrationImpl extends MEMDMTrackerConstants implements MEDMTracker
{
    private Properties integrationTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerIntegrationImpl() {
        this.integrationTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEMDMTrackerIntegrationImpl";
    }
    
    public Properties getTrackerProperties() {
        SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDMP integration starts...");
        return this.getIntegrationTrackerProperties();
    }
    
    private Properties getIntegrationTrackerProperties() {
        final com.me.mdm.server.metracker.MEMDMTrackerIntegrationImpl coreImpl = new com.me.mdm.server.metracker.MEMDMTrackerIntegrationImpl();
        this.integrationTrackerProperties = coreImpl.getTrackerProperties();
        this.updateIntegrationProperties();
        return this.integrationTrackerProperties;
    }
    
    private void updateIntegrationProperties() {
        try {
            if (!this.integrationTrackerProperties.isEmpty()) {
                this.integrationTrackerProperties = new Properties();
            }
            this.addAPIKeyDetails();
            this.addSDPIntegrationDetails();
            this.addAEIntegrationDetails();
            this.addAnalyticPlusDetails();
            this.addJiraIntegrationDetails();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.integrationTrackerProperties);
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDMP integration ends");
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "updateIntegrationProperties", "Exception : ", (Throwable)e);
        }
    }
    
    private void addAPIKeyDetails() {
        final JSONObject apiDetailsJSON = new JSONObject();
        try {
            final int apiKeyCount = DBUtil.getRecordCount("APIKeyInfo", "API_KEY_ID", (Criteria)null);
            apiDetailsJSON.put("API_KEY_COUNT", apiKeyCount);
            this.integrationTrackerProperties.setProperty("API_DETAILS", apiDetailsJSON.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addAPIKeyDetails", "Exception : ", (Throwable)ex);
        }
    }
    
    private void addSDPIntegrationDetails() {
        final JSONObject sdpIntegJSON = new JSONObject();
        try {
            boolean isSDPConfigured = false;
            final Properties sdpSettingsProps = MDMSDPIntegrationUtil.getInstance().getServerSettings("HelpDesk");
            if (sdpSettingsProps != null) {
                isSDPConfigured = ((Hashtable<K, Boolean>)sdpSettingsProps).get("IS_ENABLED");
                final String sdpMdmAsset = MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_INV_INTEGRATION");
                final String sdpMdmAssetDeleteAction = MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_ASSET_DEL_VALUE");
                final String sdpMdmAssetPostOwner = MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_POST_OWNER");
                final String sdpMdmHelpDeskAlert = MDMIntegrationUtil.getInstance().getIntegrationParamValue("SDP_MDM_HELPDESK_ALERT");
                final boolean isSDPMDMAssetEnabled = sdpMdmAsset != null && sdpMdmAsset.equalsIgnoreCase("true");
                sdpIntegJSON.put("SDP_MDM_ASSET_INTEGRATION", isSDPMDMAssetEnabled);
                sdpIntegJSON.put("SDP_MDM_ASSET_DELETE_ACTION", (Object)sdpMdmAssetDeleteAction);
                sdpIntegJSON.put("SDP_MDM_ASSET_POST_OWNER", (Object)sdpMdmAssetPostOwner);
                sdpIntegJSON.put("SDP_MDM_HELPDESK_ALERT", (Object)sdpMdmHelpDeskAlert);
            }
            sdpIntegJSON.put("SDP_INTEGRATION_CONFIGURED", isSDPConfigured);
            this.addSDPActionIntegrationDetails(sdpIntegJSON);
            this.integrationTrackerProperties.setProperty("SDP_INTEGRATION_DETAILS", sdpIntegJSON.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addSDPIntegrationDetails", "Exception : ", (Throwable)ex);
        }
    }
    
    private void addJiraIntegrationDetails() {
        final JSONObject jiraIntegJSON = new JSONObject();
        try {
            final JSONObject jiraActionIntegJSON = new JSONObject();
            this.setIntegParam(jiraActionIntegJSON, "MDMP_JIRA_ASSIGN_PROFILE_GROUP_COUNT");
            this.setIntegParam(jiraActionIntegJSON, "MDMP_JIRA_ASSIGN_PROFILE_DEVICE_COUNT");
            this.setIntegParam(jiraActionIntegJSON, "MDMP_JIRA_ASSIGN_APP_GROUP_COUNT");
            this.setIntegParam(jiraActionIntegJSON, "MDMP_JIRA_ASSIGN_APP_DEVICE_COUNT");
            this.setIntegParam(jiraActionIntegJSON, "MDMP_JIRA_LOCATE_DEVICE_COUNT");
            this.setIntegParam(jiraActionIntegJSON, "MDMP_JIRA_DEPROVISION_DEVICE_COUNT");
            this.setIntegParam(jiraActionIntegJSON, "MDMP_JIRA_LOCK_DEVICE_COUNT");
            this.setIntegParam(jiraActionIntegJSON, "MDMP_JIRA_ALARM_DEVICE_COUNT");
            this.setIntegParam(jiraActionIntegJSON, "MDMP_JIRA_CORPORATE_WIPE_DEVICE_COUNT");
            this.setIntegParam(jiraActionIntegJSON, "MDMP_JIRA_COMPLETE_WIPE_DEVICE_COUNT");
            this.setIntegParam(jiraActionIntegJSON, "MDMP_JIRA_CLEAR_PASSCODE_DEVICE_COUNT");
            this.setIntegParam(jiraActionIntegJSON, "MDMP_JIRA_REMOTE_CONTROL_COUNT");
            jiraIntegJSON.put("JIRA_MDM_ACTION_INTEGRATION_PARAMS", (Object)jiraActionIntegJSON);
            this.integrationTrackerProperties.setProperty("JIRA_INTEGRATION_DETAILS", jiraIntegJSON.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addJiraIntegrationDetails", "Exception : ", (Throwable)ex);
        }
    }
    
    private void addSDPActionIntegrationDetails(final JSONObject sdpIntegJSON) {
        try {
            final JSONObject sdpActionIntegJSON = new JSONObject();
            this.setIntegParam(sdpActionIntegJSON, "MDMP_SDP_ASSIGN_GROUP_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "MDMP_SDP_ASSIGN_PROFILE_GROUP_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "MDMP_SDP_ASSIGN_PROFILE_DEVICE_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "MDMP_SDP_ASSIGN_APP_GROUP_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "MDMP_SDP_ASSIGN_APP_DEVICE_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "MDMP_SDP_ASSIGN_STAGED_DEVICE_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "MDMP_SDP_DEPROVISION_DEVICE_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "MDMP_SDP_LOCATE_DEVICE_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "MDMP_SDP_LOCK_DEVICE_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "MDMP_SDP_ALARM_DEVICE_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "MDMP_SDP_CORPORATE_WIPE_DEVICE_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "MDMP_SDP_COMPLETE_WIPE_DEVICE_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "MDMP_SDP_CLEAR_PASSCODE_DEVICE_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "MDMP_SDP_LOST_MODE_DEVICE_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "ASSET_EXPLORER_SETTINGS_PAGE_VISIT_COUNT");
            this.setIntegParam(sdpActionIntegJSON, "SDP_SETTINGS_PAGE_VISIT_COUNT");
            sdpIntegJSON.put("SDP_MDM_ACTION_INTEGRATION_PARAMS", (Object)sdpActionIntegJSON);
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addSDPActionIntegrationDetails", "Exception : ", (Throwable)ex);
        }
    }
    
    private void setIntegParam(final JSONObject integJSON, final String integName) throws JSONException {
        integJSON.put(integName, (Object)MDMIntegrationUtil.getInstance().getIntegrationParamValue(integName));
    }
    
    private void addAEIntegrationDetails() {
        final JSONObject aeIntegJSON = new JSONObject();
        try {
            boolean isAEConfigured = false;
            final Properties aeSettingsProps = MDMSDPIntegrationUtil.getInstance().getServerSettings("AssetExplorer");
            if (aeSettingsProps != null) {
                isAEConfigured = ((Hashtable<K, Boolean>)aeSettingsProps).get("IS_ENABLED");
                final String aeMDMAsset = MDMIntegrationUtil.getInstance().getIntegrationParamValue("AE_MDM_INV_INTEGRATION");
                final boolean isAEMDMAssetEnabled = aeMDMAsset != null && aeMDMAsset.equalsIgnoreCase("true");
                aeIntegJSON.put("AE_MDM_INV_INTEGRATION", isAEMDMAssetEnabled);
            }
            aeIntegJSON.put("AE_INTEGRATION_CONFIGURED", isAEConfigured);
            this.integrationTrackerProperties.setProperty("AE_INTEGRATION_DETAILS", aeIntegJSON.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addAEIntegrationDetails", "Exception : ", (Throwable)ex);
        }
    }
    
    private void addAnalyticPlusDetails() {
        SyMLogger.info(this.logger, this.sourceClass, "addAnalyticPlusDetails", "Add analytic plus details to metracking");
        final JSONObject analyticIntegJSON = new JSONObject();
        try {
            boolean isAnalyticKeyGen = false;
            boolean isAnalyticPlusInteg = false;
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("APIKeyInfo"));
            selectQuery.addJoin(new Join("APIKeyInfo", "IntegrationService", new String[] { "SERVICE_ID" }, new String[] { "SERVICE_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "API_KEY_ID"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("IntegrationService", "NAME"), (Object)"AnalyticsPlus", 0));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                isAnalyticKeyGen = true;
            }
            final Properties analyticSettingsProps = MDMSDPIntegrationUtil.getInstance().getServerSettings("AnalyticPlus");
            if (analyticSettingsProps != null) {
                isAnalyticPlusInteg = ((Hashtable<K, Boolean>)analyticSettingsProps).get("IS_ENABLED");
            }
            analyticIntegJSON.put("ANALYTIC_API_GENERATED", isAnalyticKeyGen);
            analyticIntegJSON.put("ANALYTIC_PLUS_INTEGRATED", isAnalyticPlusInteg);
            this.integrationTrackerProperties.setProperty("ANALYTIC_INTEGRATION_DETAILS", analyticIntegJSON.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addAnalyticPlusDetails", "Exception while adding analytic plus details : ", (Throwable)ex);
        }
    }
}
