package com.me.mdm.server.metracker;

import com.adventnet.ds.query.DataSet;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.mdm.server.tracker.MDMTrackerUtil;
import com.adventnet.ds.query.CaseExpression;
import com.me.mdm.server.tracker.MDMCoreQuery;
import java.util.logging.Level;
import com.me.mdm.server.settings.MdComplianceRulesHandler;
import java.util.HashMap;
import java.util.List;
import java.sql.Connection;
import java.util.Map;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import org.json.JSONException;
import com.me.mdm.server.android.agentmigrate.AgentMigrationHandler;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.android.knox.KnoxUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackerAndroidImpl extends MEMDMTrackerConstants
{
    private Properties mdmAndroidTrackerProperties;
    private Logger logger;
    private String sourceClass;
    private int androidPlayStoreApp;
    private int androidEnterpriseApp;
    
    public MEMDMTrackerAndroidImpl() {
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
            this.addSAFECount();
            this.addAVersionCount();
            this.addSafeDeviceCountWRTVersion();
            this.addOSVersionSplit();
            this.addELMCapableCount();
            this.addAndroidELMFailureCount();
            this.addKnoxEnableCount();
            this.addKnoxLicenseAppliedCount();
            this.addKnoxLicenseCount();
            this.addKnoxProfileCount();
            this.addPersonalKnoxDevice();
            this.addKnoxVersionCount();
            this.addUnmanagedKnoxLicenseCount();
            this.addMEMDMAppSettings();
            this.addAdminEnrollmentCount();
            this.addProfileOwnerAndDeviceOwnerCount();
            this.addAgentMigrationDetails();
            this.addAFWDetails();
            this.addRootedDeviceDetails();
            this.addAgentVersionDetails();
            this.addScepCertCountDetails();
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
    
    private void addProfileOwnerAndDeviceOwnerCount() {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceInfo"));
            sQuery.addJoin(new Join("MdDeviceInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria cSupervised = new Criteria(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)Boolean.TRUE, 0);
            final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 0);
            sQuery.setCriteria(cPlatform.and(cSupervised));
            final int supervicedCount = DBUtil.getRecordCount(sQuery, "ManagedDevice", "RESOURCE_ID");
            this.mdmAndroidTrackerProperties.setProperty("Android_Device_Owner_Count", String.valueOf(supervicedCount));
            final Criteria cProfileOwner = new Criteria(new Column("MdDeviceInfo", "IS_PROFILEOWNER"), (Object)Boolean.TRUE, 0);
            sQuery.setCriteria(cPlatform.and(cProfileOwner));
            final int profileOwnerCount = DBUtil.getRecordCount(sQuery, "ManagedDevice", "RESOURCE_ID");
            this.mdmAndroidTrackerProperties.setProperty("Android_Profile_Owner_Count", String.valueOf(profileOwnerCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addSupervisedDeviceCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addSafeDeviceCountWRTVersion() {
        try {
            final Criteria safeTypeCri = new Criteria(Column.getColumn("ManagedDevice", "AGENT_TYPE"), (Object)3, 0);
            final Criteria versionSCri = new Criteria(Column.getColumn("ManagedDevice", "AGENT_VERSION"), (Object)"S", 12, false);
            final Criteria versionKCri = new Criteria(Column.getColumn("ManagedDevice", "AGENT_VERSION"), (Object)"K", 12, false);
            final Criteria managedCriteria = ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria();
            final int versionSCount = DBUtil.getRecordCount("ManagedDevice", "RESOURCE_ID", safeTypeCri.and(versionSCri).and(managedCriteria));
            final int versionKCount = DBUtil.getRecordCount("ManagedDevice", "RESOURCE_ID", safeTypeCri.and(versionKCri).and(managedCriteria));
            this.mdmAndroidTrackerProperties.setProperty("Android_S_Version_Count", String.valueOf(versionSCount));
            this.mdmAndroidTrackerProperties.setProperty("Android_K_Version_Count", String.valueOf(versionKCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addSafeDeviceCountWRTVersion", "Exception : ", (Throwable)e);
        }
    }
    
    private void addAVersionCount() {
        try {
            final Criteria platformCri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)2, 12, false);
            final Criteria versionACri = new Criteria(Column.getColumn("ManagedDevice", "AGENT_VERSION"), (Object)"A", 12, false);
            final Criteria managedCriteria = ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria();
            final int versionACount = DBUtil.getRecordCount("ManagedDevice", "RESOURCE_ID", platformCri.and(versionACri).and(managedCriteria));
            this.mdmAndroidTrackerProperties.setProperty("Android_A_Version_Count", String.valueOf(versionACount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addSafeDeviceCountWRTVersion", "Exception : ", (Throwable)e);
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
    
    private void addScepCertCountDetails() {
        try {
            final JSONObject scepCertCountJson = InventoryUtil.getInstance().getScepCertCountDetails(2);
            final int scepCertCount = scepCertCountJson.getInt("ScepCertificateCount");
            this.mdmAndroidTrackerProperties.setProperty("ScepCertificateCount", String.valueOf(scepCertCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidScepCount", "Exception : ", (Throwable)e);
        }
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
    
    private void addMEMDMAppSettings() {
        try {
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            this.mdmAndroidTrackerProperties.setProperty("MEMDM_Agent_Setting", isMsp ? new JSONObject().toString() : MEMDMTrackerUtil.getAndroidMEMDMAppSetting().toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addMEMDMAppSettings", "Exception : ", (Throwable)ex);
        }
    }
    
    private void addAdminEnrollmentCount() {
        final org.json.simple.JSONObject adminEnrollment = new org.json.simple.JSONObject();
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            try {
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("EnrollmentTemplateToRequest"));
                final Join templateJoin = new Join("EnrollmentTemplateToRequest", "AndroidAdminEnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2);
                final Join enrollmentJoin = new Join("EnrollmentTemplateToRequest", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2);
                final Join managedMapJoin = new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1);
                final Join managedJoin = new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1);
                sQuery.addJoin(templateJoin);
                sQuery.addJoin(enrollmentJoin);
                sQuery.addJoin(managedMapJoin);
                sQuery.addJoin(managedJoin);
                sQuery.addSelectColumn(new Column("ManagedDevice", "*"));
                sQuery.addSelectColumn(new Column("EnrollmentTemplateToRequest", "*"));
                final int adminCount = DBUtil.getRecordCount("AndroidAdminDeviceDetails", "LOGIN_ID", (Criteria)null);
                final Criteria unassignedCri = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)3, 0);
                sQuery.setCriteria(unassignedCri);
                Iterator it = MDMUtil.getPersistence().get(sQuery).getRows("ManagedDevice");
                final int unassignedCount = DBUtil.getIteratorSize(it);
                final Criteria enrolledCri = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
                sQuery.setCriteria(enrolledCri);
                it = MDMUtil.getPersistence().get(sQuery).getRows("ManagedDevice");
                final int enrolledCount = DBUtil.getIteratorSize(it);
                adminEnrollment.put((Object)"Admin_Enrollment_NFC_Used", (Object)DBUtil.getValueFromDB("SystemParams", "PARAM_NAME", (Object)"Admin_Enrollment_NFC_Used", "PARAM_VALUE"));
                adminEnrollment.put((Object)"Admin_Enrollment_NFC_Admin_Count", (Object)adminCount);
                adminEnrollment.put((Object)"Admin_Enrollment_NFC_Enrolled_Count", (Object)enrolledCount);
                adminEnrollment.put((Object)"Admin_Enrollment_NFC_Unassigned_Count", (Object)unassignedCount);
            }
            catch (final Exception exp) {
                SyMLogger.error(this.logger, this.sourceClass, "addAdminEnrollmentCount", "Exception : ", (Throwable)exp);
            }
        }
        this.mdmAndroidTrackerProperties.setProperty("Admin_Enrollment_NFC", adminEnrollment.toJSONString());
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
            SyMLogger.error(this.logger, this.sourceClass, "addAgentMigrationDetails", "Exception : ", (Throwable)ex);
        }
    }
    
    private void addAFWDetails() {
        final JSONObject afwJOSN = new JSONObject();
        final Connection connection = null;
        try {
            final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
            final Boolean isAFWConfigured = GoogleForWorkSettings.isAFWSettingsConfigured(customerId);
            afwJOSN.put("AFW_Configured", (Object)isAFWConfigured);
            if (isAFWConfigured) {
                final JSONObject googleESAJSON = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
                final int entepriseType = googleESAJSON.getInt("ENTERPRISE_TYPE");
                SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
                sQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
                final Criteria androidPlatformCriteria = new Criteria(Column.getColumn("MdPackage", "PLATFORM_TYPE"), (Object)2, 0);
                final Criteria paidAppCri = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PAID_APP"), (Object)Boolean.TRUE, 0);
                final Criteria portalCri = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)Boolean.TRUE, 0);
                sQuery.setCriteria(portalCri.and(androidPlatformCriteria));
                final int portalAppsCount = DBUtil.getRecordActualCount(sQuery, "MdPackageToAppGroup", "PACKAGE_ID");
                sQuery.setCriteria(paidAppCri.and(portalCri).and(androidPlatformCriteria));
                final int paidAppsCount = DBUtil.getRecordActualCount(sQuery, "MdPackageToAppGroup", "PACKAGE_ID");
                sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AFWAccountStatus"));
                final Criteria accFailedCri = new Criteria(Column.getColumn("AFWAccountStatus", "ACCOUNT_STATUS"), (Object)3, 0);
                sQuery.setCriteria(accFailedCri);
                final int accAdditionFailedCount = DBUtil.getRecordActualCount(sQuery, "AFWAccountStatus", "RESOURCE_ID");
                sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AFWAccountStatus"));
                final Column countColumn = Column.getColumn("AFWAccountStatus", "RESOURCE_ID").count();
                sQuery.addSelectColumn(Column.getColumn("AFWAccountStatus", "ERROR_CODE"));
                sQuery.addSelectColumn(countColumn);
                sQuery.setCriteria(accFailedCri);
                final List list = new ArrayList();
                final Column groupByCol = Column.getColumn("AFWAccountStatus", "ERROR_CODE");
                list.add(groupByCol);
                final GroupByClause errorCodeGroupBy = new GroupByClause(list);
                sQuery.setGroupByClause(errorCodeGroupBy);
                final JSONObject errorCodeCntJson = new JSONObject();
                final HashMap summaryMap = MDMUtil.getInstance().executeCountQuery(sQuery);
                for (final Map.Entry pairs : summaryMap.entrySet()) {
                    final Integer errorCode = pairs.getKey();
                    final int errorCodeCount = pairs.getValue();
                    errorCodeCntJson.put(errorCode.toString(), errorCodeCount);
                }
                final SelectQuery accountRetrySummaryQ = (SelectQuery)new SelectQueryImpl(Table.getTable("AFWAccountStatus"));
                final Criteria retriedCriteria = new Criteria(Column.getColumn("AFWAccountStatus", "ATTEMPT_COUNT"), (Object)0, 5);
                accountRetrySummaryQ.addSelectColumn(Column.getColumn("AFWAccountStatus", "ACCOUNT_STATUS"));
                accountRetrySummaryQ.addSelectColumn(countColumn);
                accountRetrySummaryQ.setCriteria(retriedCriteria);
                final List retrySummaryGroupBy = new ArrayList();
                final Column statusColumn = Column.getColumn("AFWAccountStatus", "ACCOUNT_STATUS");
                retrySummaryGroupBy.add(statusColumn);
                final GroupByClause retrySummaryGroupByClause = new GroupByClause(retrySummaryGroupBy);
                accountRetrySummaryQ.setGroupByClause(retrySummaryGroupByClause);
                final JSONObject retrySummaryJSON = new JSONObject();
                final HashMap retrySummaryMap = MDMUtil.getInstance().executeCountQuery(accountRetrySummaryQ);
                for (final Map.Entry pairs2 : retrySummaryMap.entrySet()) {
                    final Integer status = pairs2.getKey();
                    final int accountCount = pairs2.getValue();
                    retrySummaryJSON.put(status.toString(), accountCount);
                }
                afwJOSN.put("AFW_Account_Type", entepriseType);
                afwJOSN.put("AFW_DomainName", googleESAJSON.get("MANAGED_DOMAIN_NAME"));
                afwJOSN.put("AFW_UsersCount", DBUtil.getRecordActualCount("BusinessStoreUsers", "BS_USER_ID", new Criteria(new Column("BusinessStoreUsers", "BUSINESSSTORE_ID"), googleESAJSON.get("BUSINESSSTORE_ID"), 0)));
                afwJOSN.put("AFW_AccAdditionFailedCount", accAdditionFailedCount);
                afwJOSN.put("AFW_ApprovedAppsCount", portalAppsCount);
                afwJOSN.put("AFW_PaidAppsCount", paidAppsCount);
                afwJOSN.put("AFW_AppConfigCount", DBUtil.getRecordActualCount("ManagedAppConfiguration", "APP_CONFIG_TEMPLATE_ID", (Criteria)null));
                afwJOSN.put("AFW_PermissionConfigCount", DBUtil.getRecordActualCount("AppPermissionConfig", "APP_PERMISSION_CONFIG_ID", (Criteria)null));
                afwJOSN.put("AFW_AccAdditionFailedSummary", (Object)errorCodeCntJson);
                afwJOSN.put("AFW_AccRetrySummary", (Object)retrySummaryJSON);
            }
            this.mdmAndroidTrackerProperties.setProperty("AFW_Details", afwJOSN.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addAgentMigrationDetails", "Exception : ", (Throwable)ex);
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception e) {
                SyMLogger.error(this.logger, this.sourceClass, "addAgentMigrationDetails", "Exception : ", (Throwable)e);
            }
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception e2) {
                SyMLogger.error(this.logger, this.sourceClass, "addAgentMigrationDetails", "Exception : ", (Throwable)e2);
            }
        }
    }
    
    private void addOSVersionSplit() {
        final JSONObject osVersionCountDetails = InventoryUtil.getInstance().getOSVersionCountDetails(2);
        this.mdmAndroidTrackerProperties.setProperty("Android_OS_Version_Summary", osVersionCountDetails.toString());
    }
    
    private void addRootedDeviceDetails() {
        try {
            final JSONObject rootedDeviceDetails = new JSONObject();
            final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
            rootedDeviceDetails.put("Android_Rooted_Device_Count", MDMUtil.getInstance().getRootedDeviceCount(null));
            rootedDeviceDetails.put("And_Corp_Wipe_Rooted_Device_Setting", MdComplianceRulesHandler.getInstance().getAndroidComplianceRules(customerId).get("CORPORATE_WIPE_ROOTED_DEVICES"));
            final int corpWipeCount = DBUtil.getRecordCount("ManagedDevice", "RESOURCE_ID", new Criteria(Column.getColumn("ManagedDevice", "REMARKS"), (Object)"mdm.enroll.device_rooted", 0));
            rootedDeviceDetails.put("Corp_Wiped_Rooted_Devices_Count", corpWipeCount);
            this.mdmAndroidTrackerProperties.setProperty("Android_Rooted_Device_Info", rootedDeviceDetails.toString());
        }
        catch (final Exception ex) {
            Logger.getLogger(MEMDMTrackerAndroidImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private SelectQuery temporaryManagedDeviceQuery() {
        final Criteria managedCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria androidAgentVersionCriteria = MDMCoreQuery.getInstance().getAndroidAgentVersionCriteria();
        final CaseExpression androidAgentVersionExpr = new CaseExpression("LOWER_AGENT_VERSION_COUNT");
        androidAgentVersionExpr.addWhen(managedCriteria.and(androidAgentVersionCriteria), (Object)new Column("Resource", "RESOURCE_ID"));
        final MDMTrackerUtil trackerUtil = new MDMTrackerUtil();
        final SelectQuery managedDeviceQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentRequest"));
        managedDeviceQuery.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        managedDeviceQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        managedDeviceQuery.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        managedDeviceQuery.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentTemplateToRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
        managedDeviceQuery.addJoin(new Join("EnrollmentTemplateToRequest", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 1));
        managedDeviceQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        managedDeviceQuery.addJoin(new Join("Resource", "CustomerInfo", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
        managedDeviceQuery.addSelectColumn(new Column("CustomerInfo", "CUSTOMER_ID"));
        final Column eridCount = new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID").count();
        eridCount.setColumnAlias("ENROLL_REQ_COUNT");
        managedDeviceQuery.addSelectColumn(eridCount);
        final Column resourceCount = new Column("ManagedDevice", "RESOURCE_ID").count();
        resourceCount.setColumnAlias("RESOURCE_COUNT");
        managedDeviceQuery.addSelectColumn(resourceCount);
        managedDeviceQuery.addSelectColumn(trackerUtil.getDistinctCountCaseExpressionColumn(androidAgentVersionExpr, 4, "LOWER_AGENT_VERSION_COUNT"));
        final ArrayList<Column> groupByColumnsList = new ArrayList<Column>();
        groupByColumnsList.add(new Column("CustomerInfo", "CUSTOMER_ID"));
        final GroupByClause groupByColumn = new GroupByClause((List)groupByColumnsList);
        managedDeviceQuery.setGroupByClause(groupByColumn);
        return managedDeviceQuery;
    }
    
    private void addAgentVersionDetails() {
        Connection conn = null;
        DataSet ds = null;
        try {
            final SelectQuery androidAgentVersionQuery = this.temporaryManagedDeviceQuery();
            conn = RelationalAPI.getInstance().getConnection();
            ds = RelationalAPI.getInstance().executeQuery((Query)androidAgentVersionQuery, conn);
            Integer lesserAgentVersionCount = 0;
            while (ds.next()) {
                lesserAgentVersionCount += (int)ds.getValue("LOWER_AGENT_VERSION_COUNT");
            }
            this.mdmAndroidTrackerProperties.setProperty("Lower_Agent_Version_Count", lesserAgentVersionCount.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addAgentVersionDetails", "Exception : ", (Throwable)ex);
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
    }
}
