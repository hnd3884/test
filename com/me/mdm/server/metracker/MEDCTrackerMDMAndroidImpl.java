package com.me.mdm.server.metracker;

import java.util.Hashtable;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.adventnet.sym.server.mdm.android.AndroidAgentSettingsHandler;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.server.android.agentmigrate.AgentMigrationHandler;
import org.json.JSONObject;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEDCTrackerMDMAndroidImpl extends MEMDMTrackerConstants
{
    private Properties mdmAndroidTrackerProperties;
    private Logger logger;
    private String sourceClass;
    private int androidPlayStoreApp;
    private int androidEnterpriseApp;
    
    public MEDCTrackerMDMAndroidImpl() {
        this.mdmAndroidTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCTrackerMDMAndroidImpl";
        this.androidPlayStoreApp = 0;
        this.androidEnterpriseApp = 2;
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDM Android impl starts");
            if (!this.mdmAndroidTrackerProperties.isEmpty()) {
                this.mdmAndroidTrackerProperties = new Properties();
            }
            this.addAndroidCount();
            this.addSAFECount();
            this.addAndroidProfileCount();
            this.addAndroidGroupCount();
            this.addAndroidEnterpriseAppCount();
            this.addAndroidPlaystoreAppCount();
            this.addSafeDeviceCountWRTVersion();
            this.addELMCapableCount();
            this.addAndroidELMFailureCount();
            this.addRepositoryAppCount(2, this.androidPlayStoreApp, "Android_Apps_Playstore_Count");
            this.addRepositoryAppCount(2, this.androidEnterpriseApp, "Android_Apps_Enterprise_Count");
            this.addKnoxEnableCount();
            this.addKnoxLicenseAppliedCount();
            this.addKnoxLicenseCount();
            this.addKnoxProfileCount();
            this.addPersonalKnoxDevice();
            this.addKnoxVersionCount();
            this.addUnmanagedKnoxLicenseCount();
            this.addAgentMigrationDetails();
            this.getAddedEnrollmentRequestCount();
            this.addAndroidEnrollementRequestFailedCount();
            this.addMEMDMAPPSettings();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.getMDMTrackerProperties());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MDMAndroidTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.mdmAndroidTrackerProperties;
    }
    
    private Properties getMDMTrackerProperties() {
        return this.mdmAndroidTrackerProperties;
    }
    
    private void addSAFECount() {
        try {
            final int safeCount = ManagedDeviceHandler.getInstance().getSAFEDeviceCount();
            this.mdmAndroidTrackerProperties.setProperty("SAFE_Device_Count", String.valueOf(safeCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addSAFECount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addAndroidCount() {
        try {
            final int androidCount = ManagedDeviceHandler.getInstance().getAndroidManagedDeviceCount();
            this.mdmAndroidTrackerProperties.setProperty("Android_Device_Count", String.valueOf(androidCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addSafeDeviceCountWRTVersion() {
        try {
            final Criteria safeTypeCri = new Criteria(Column.getColumn("ManagedDevice", "AGENT_TYPE"), (Object)3, 0);
            final Criteria versionSCri = new Criteria(Column.getColumn("ManagedDevice", "AGENT_VERSION"), (Object)"S", 12, false);
            final Criteria versionKCri = new Criteria(Column.getColumn("ManagedDevice", "AGENT_VERSION"), (Object)"K", 12, false);
            final int versionSCount = DBUtil.getRecordCount("ManagedDevice", "RESOURCE_ID", safeTypeCri.and(versionSCri));
            final int versionKCount = DBUtil.getRecordCount("ManagedDevice", "RESOURCE_ID", safeTypeCri.and(versionKCri));
            this.mdmAndroidTrackerProperties.setProperty("Android_S_Version_Count", String.valueOf(versionSCount));
            this.mdmAndroidTrackerProperties.setProperty("Android_K_Version_Count", String.valueOf(versionKCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addSafeDeviceCountWRTVersion", "Exception : ", (Throwable)e);
        }
    }
    
    private void addAndroidGroupCount() {
        try {
            final Criteria androidTypeCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)4, 0);
            final int group = DBUtil.getRecordCount("CustomGroup", "RESOURCE_ID", androidTypeCri);
            this.mdmAndroidTrackerProperties.setProperty("Android_Group_Count", String.valueOf(group));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidGroupCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addAndroidProfileCount() {
        try {
            final int profileCount = ProfileUtil.getInstance().getProfileCount(2);
            this.mdmAndroidTrackerProperties.setProperty("Android_Profile_Count", String.valueOf(profileCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidProfileCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addAndroidPlaystoreAppCount() {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
            final Join mdAppGroupDetailsJoin = new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Criteria platformCriteria = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria playstoreCriteria = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 1);
            sQuery.addJoin(mdAppGroupDetailsJoin);
            sQuery.setCriteria(platformCriteria.and(playstoreCriteria));
            final int playstoreApps = DBUtil.getRecordCount(sQuery, "MdPackageToAppGroup", "PACKAGE_ID");
            this.mdmAndroidTrackerProperties.setProperty("Android_Apps_Playstore_Count", String.valueOf(playstoreApps));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidPlaystoreAppCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addAndroidEnterpriseAppCount() {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
            final Join mdAppGroupDetailsJoin = new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Criteria platformCriteria = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria enterpriseCriteria = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 0);
            sQuery.addJoin(mdAppGroupDetailsJoin);
            sQuery.setCriteria(platformCriteria.and(enterpriseCriteria));
            final int playstoreApps = DBUtil.getRecordCount(sQuery, "MdPackageToAppGroup", "PACKAGE_ID");
            this.mdmAndroidTrackerProperties.setProperty("Android_Apps_Enterprise_Count", String.valueOf(playstoreApps));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidEnterpriseAppCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addRepositoryAppCount(final int platform, final int appType, final String typeKey) {
        try {
            final AppsUtil appsUtil = new AppsUtil();
            this.mdmAndroidTrackerProperties.setProperty(typeKey, String.valueOf(appsUtil.getAppRepositoryAppCount(platform, appType)));
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidAppCount " + typeKey, "Exception : ", (Throwable)ex);
        }
    }
    
    private void addELMCapableCount() {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
            final Join mdAppGroupDetailsJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Criteria agentCriteria = new Criteria(new Column("ManagedDevice", "AGENT_TYPE"), (Object)3, 0);
            final Criteria managedCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            Criteria osCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"2.*", 3);
            osCriteria = osCriteria.and(new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"3.*", 3));
            osCriteria = osCriteria.and(new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.0*", 3));
            osCriteria = osCriteria.and(new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"4.1*", 3));
            sQuery.addJoin(mdAppGroupDetailsJoin);
            sQuery.setCriteria(osCriteria.and(agentCriteria).and(managedCriteria));
            final int elmCapable = DBUtil.getRecordCount(sQuery, "ManagedDevice", "RESOURCE_ID");
            this.mdmAndroidTrackerProperties.setProperty("Android_Safe_ELM_Capable_Count", String.valueOf(elmCapable));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidEnterpriseAppCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addKnoxEnableCount() {
        try {
            final int knoxCapable = DBUtil.getRecordCount("ManagedKNOXContainer", "RESOURCE_ID", (Criteria)null);
            this.mdmAndroidTrackerProperties.setProperty("Knox_Enabled_Device_Count", String.valueOf(knoxCapable));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidEnterpriseAppCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addKnoxJSON() {
    }
    
    private void addKnoxLicenseAppliedCount() {
        try {
            final int knoxLicenseAppliedCount = KnoxUtil.getInstance().getTotalKnoxLicenseAppliedCount();
            this.mdmAndroidTrackerProperties.setProperty("Knox_License_Applied_Count", String.valueOf(knoxLicenseAppliedCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getProperty", "Exception : ", (Throwable)e);
        }
    }
    
    private void addKnoxLicenseCount() {
        try {
            final int knoxLicenseAppliedCount = KnoxUtil.getInstance().getTotalLicenseCount(CustomerInfoUtil.getInstance().getDefaultCustomer());
            this.mdmAndroidTrackerProperties.setProperty("Knox_License_Count", String.valueOf(knoxLicenseAppliedCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getProperty", "Exception : ", (Throwable)e);
        }
    }
    
    private void addKnoxProfileCount() {
        try {
            final Criteria profileCri = new Criteria(new Column("Profile", "PROFILE_TYPE"), (Object)1, 0);
            final Criteria androidProfile = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria knoxProfile = new Criteria(new Column("Profile", "SCOPE"), (Object)1, 0);
            final int profile = DBUtil.getRecordCount("Profile", "PROFILE_ID", profileCri.and(androidProfile).and(knoxProfile));
            this.mdmAndroidTrackerProperties.setProperty("Knox_Profile_Count", String.valueOf(profile));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getProperty", "Exception : ", (Throwable)e);
        }
    }
    
    private void addPersonalKnoxDevice() {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedKNOXContainer"));
            final Join deviceJoin = new Join("ManagedKNOXContainer", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Criteria personalProfile = new Criteria(new Column("ManagedDevice", "OWNED_BY"), (Object)0, 0);
            sQuery.addJoin(deviceJoin);
            sQuery.setCriteria(personalProfile);
            final int personalKnoxCount = DBUtil.getRecordCount(sQuery, "ManagedDevice", "RESOURCE_ID");
            this.mdmAndroidTrackerProperties.setProperty("Knox_Count_In_Personal_Owned", String.valueOf(personalKnoxCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getProperty", "Exception : ", (Throwable)e);
        }
    }
    
    private void addKnoxVersionCount() {
        try {
            final Criteria version1Cri = new Criteria(Column.getColumn("ManagedKNOXContainer", "KNOX_VERSION"), (Object)1, 0);
            final int v1Count = DBUtil.getRecordCount("ManagedKNOXContainer", "RESOURCE_ID", version1Cri);
            final Criteria version2Cri = new Criteria(Column.getColumn("ManagedKNOXContainer", "KNOX_VERSION"), (Object)2, 0);
            final int v2Count = DBUtil.getRecordCount("ManagedKNOXContainer", "RESOURCE_ID", version2Cri);
            this.mdmAndroidTrackerProperties.setProperty("Knox_V1_Device_Count", String.valueOf(v1Count));
            this.mdmAndroidTrackerProperties.setProperty("Knox_V2_Device_Count", String.valueOf(v2Count));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidGroupCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addAndroidELMFailureCount() {
        try {
            final Criteria elmFailureCri = new Criteria(new Column("AndroidELMStatus", "ELM_STATUS"), (Object)0, 0);
            final int filedCount = DBUtil.getRecordCount("AndroidELMStatus", "ENROLLMENT_REQUEST_ID", elmFailureCri);
            this.mdmAndroidTrackerProperties.setProperty("ELM_Failed_Count", String.valueOf(filedCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addBlackListAppCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void getAddedEnrollmentRequestCount() {
        try {
            final int enrollReqCount = MDMEnrollmentRequestHandler.getInstance().getAddedEnrollmentRequestCount(2);
            this.mdmAndroidTrackerProperties.setProperty("Android_Enrollment_Request_Count", String.valueOf(enrollReqCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getAddedEnrollmentRequestCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addUnmanagedKnoxLicenseCount() {
        try {
            final SelectQuery knoxlicenseUsedCountQuery = (SelectQuery)new SelectQueryImpl(new Table("KNOXDeviceToLicenseRel"));
            final Integer usedCount = DBUtil.getRecordCount(knoxlicenseUsedCountQuery, "KNOXDeviceToLicenseRel", "RESOURCE_ID");
            final SelectQuery knoxlicenseUsedCountQueryHistory = (SelectQuery)new SelectQueryImpl(new Table("UnmanagedKNOXDevToLicRel"));
            final Integer usedCountHistory = DBUtil.getRecordCount(knoxlicenseUsedCountQueryHistory, "UnmanagedKNOXDevToLicRel", "RESOURCE_ID");
            final int unmanagedCOunt = usedCountHistory - usedCount;
            this.mdmAndroidTrackerProperties.setProperty("KNOX_Unmanaged_License_Count", String.valueOf(unmanagedCOunt));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addUnmanagedKnoxLicenseCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addAgentMigrationDetails() {
        final JSONObject migrationJOSN = new JSONObject();
        try {
            migrationJOSN.put("Safe_Migration_Allowed", AgentMigrationHandler.getInstance().isMigrationAllowed());
            migrationJOSN.put("Device_Count", AgentMigrationHandler.getInstance().getCount(null, null));
            migrationJOSN.put("Failed_Count", AgentMigrationHandler.getInstance().getCount(AgentMigrationHandler.AGENT_MIGRATE_STATUS_FAILED, null));
            migrationJOSN.put("Inprogress_Count", AgentMigrationHandler.getInstance().getCount(AgentMigrationHandler.AGENT_MIGRATE_STATUS_IN_PROGRESS, null));
            migrationJOSN.put("Success_Count", AgentMigrationHandler.getInstance().getCount(AgentMigrationHandler.AGENT_MIGRATE_STATUS_SUCCESS, null));
            migrationJOSN.put("Failed_MigrationDataProcessing", AgentMigrationHandler.getInstance().getCount(AgentMigrationHandler.AGENT_MIGRATE_STATUS_FAILED, "MigrationDataProcessing"));
            migrationJOSN.put("Failed_NewAgentInstall", AgentMigrationHandler.getInstance().getCount(AgentMigrationHandler.AGENT_MIGRATE_STATUS_FAILED, "NewAgentInstall"));
            migrationJOSN.put("Failed_ELMActivation", AgentMigrationHandler.getInstance().getCount(AgentMigrationHandler.AGENT_MIGRATE_STATUS_FAILED, "ELMActivation"));
            migrationJOSN.put("Inprogress_MigrationDataProcessing", AgentMigrationHandler.getInstance().getCount(AgentMigrationHandler.AGENT_MIGRATE_STATUS_IN_PROGRESS, "MigrationDataProcessing"));
            migrationJOSN.put("Inprogress_ELMActivation", AgentMigrationHandler.getInstance().getCount(AgentMigrationHandler.AGENT_MIGRATE_STATUS_IN_PROGRESS, "ELMActivation"));
            migrationJOSN.put("Inprogress_NewAgentInstall", AgentMigrationHandler.getInstance().getCount(AgentMigrationHandler.AGENT_MIGRATE_STATUS_IN_PROGRESS, "NewAgentInstall"));
            this.mdmAndroidTrackerProperties.setProperty("Android_Migration_Details", migrationJOSN.toString());
        }
        catch (final JSONException ex) {
            Logger.getLogger(MEDCTrackerMDMAndroidImpl.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    private void addAndroidEnrollementRequestFailedCount() {
        try {
            this.mdmAndroidTrackerProperties.setProperty("ANDROID_ENROLLMENT_REQUEST_FAILED_COUNT", MEMDMTrackerUtil.addAndroidEnrollementRequestFailedCount());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addEnrollmentSettings", "Exception : ", (Throwable)e);
        }
    }
    
    private void addMEMDMAPPSettings() throws DataAccessException {
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        this.mdmAndroidTrackerProperties.setProperty("MEMDM_Agent_Setting", isMsp ? new JSONObject().toString() : MEMDMTrackerUtil.getAndroidMEMDMAppSetting().toString());
    }
    
    private Properties getAndroidNativeAgentSetting(final Long customerID) {
        Properties androidAgentSetting = new Properties();
        try {
            androidAgentSetting = AndroidAgentSettingsHandler.getInstance().getAndroidSettings(customerID);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getAndroidNativeAgentSetting", "Exception : ", (Throwable)e);
        }
        return androidAgentSetting;
    }
    
    private Properties getAndroidAgentRebrandingSetting(final Long customerID) {
        final Properties mdmAgentRebranding = new Properties();
        try {
            JSONObject agentRebranding = new JSONObject();
            agentRebranding = MDMAgentSettingsHandler.getInstance().getAgentRebrandingSetting(customerID);
            String appName = "";
            if (!agentRebranding.isNull("MDM_APP_NAME")) {
                appName = (String)agentRebranding.get("MDM_APP_NAME");
            }
            appName = (appName.equals("") ? "ME MDM App" : appName);
            ((Hashtable<String, String>)mdmAgentRebranding).put("MDM_APP_NAME", appName);
            String appIcon = "";
            if (!agentRebranding.isNull("MDM_APP_ICON_FILE_NAME")) {
                appIcon = (String)agentRebranding.get("MDM_APP_ICON_FILE_NAME");
            }
            appIcon = (appIcon.equals("") ? "false" : "true");
            ((Hashtable<String, String>)mdmAgentRebranding).put("MDM_APP_ICON_FILE_NAME", appIcon);
            String appSplash = "";
            if (!agentRebranding.isNull("MDM_APP_SPLASH_IMAGE_FILE_NAME")) {
                appSplash = (String)agentRebranding.get("MDM_APP_SPLASH_IMAGE_FILE_NAME");
            }
            appSplash = (appSplash.equals("") ? "false" : "true");
            ((Hashtable<String, String>)mdmAgentRebranding).put("MDM_APP_SPLASH_IMAGE_FILE_NAME", appSplash);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getAndroidAgentRebrandingSetting", "Exception : ", (Throwable)e);
        }
        return mdmAgentRebranding;
    }
}
