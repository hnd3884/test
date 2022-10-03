package com.me.mdm.server.metracker;

import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.enrollment.EnrollmentSettingsHandler;
import java.util.ArrayList;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import org.json.simple.JSONObject;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.apps.BlacklistWhitelistAppHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEDCTrackerMDMImpl extends MEMDMTrackerConstants
{
    private Properties mdmTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEDCTrackerMDMImpl() {
        this.mdmTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCTrackerMDMImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDM implementation starts...");
            if (!this.mdmTrackerProperties.isEmpty()) {
                this.mdmTrackerProperties = new Properties();
            }
            this.addEnrolledDevicesCount();
            this.addBYODSummary();
            this.addSelfEnrolledDeviceCount();
            this.addAndroidAppRepositoryCount();
            this.addAppControllDetails();
            this.addBlackListAppCount();
            this.addWhiteListAppCount();
            this.addBlaclistActionForBYOD();
            this.addBlaclistActionForCorporate();
            this.addBulkEnrollmentUsage();
            this.addMDCustomColumnUsage();
            this.addIOSMigratedFlag();
            this.addDeviceEnrollReqCount();
            this.addSettings();
            this.addTotalEnrollementRequestFailedCount();
            this.addErrorCodeDetails();
            this.getIssueFixingRequirementDetails();
            this.addAllPolicyCountDetails();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.getMDMTrackerProperties());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MDMTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.getMDMTrackerProperties();
    }
    
    private void addEnrolledDevicesCount() {
        try {
            final int noOfDevicessEnrolled = ManagedDeviceHandler.getInstance().getManagedDeviceCount();
            this.mdmTrackerProperties.setProperty("Total_Managed_Devices", String.valueOf(noOfDevicessEnrolled));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addEnrolledDevicesCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addBYODSummary() {
        try {
            final HashMap dataHashMap = MDMUtil.getInstance().getMDMByodStatus(Boolean.TRUE);
            if (dataHashMap != null) {
                this.mdmTrackerProperties.setProperty("Owned_By_Personal", String.valueOf(dataHashMap.get("personal")));
                this.mdmTrackerProperties.setProperty("Owned_By_Corporate", String.valueOf(dataHashMap.get("corporate")));
            }
            else {
                this.mdmTrackerProperties.setProperty("Owned_By_Personal", "0");
                this.mdmTrackerProperties.setProperty("Owned_By_Corporate", "0");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addBYODSummary", "Exception : ", (Throwable)e);
        }
    }
    
    private Properties getMDMTrackerProperties() {
        return this.mdmTrackerProperties;
    }
    
    private void addBlaclistActionForBYOD() {
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            try {
                final int byodAction = (int)DBUtil.getValueFromDB("MdAppBlackListSetting", "CUSTOMER_ID", (Object)customerID, "ACTION_ON_BYOD_DEVICE");
                this.mdmTrackerProperties.setProperty("BLACKLIST_ACTION_ON_BYOD", BlacklistWhitelistAppHandler.getInstance().getBlacklistActionText(byodAction));
            }
            catch (final Exception ex) {
                SyMLogger.error(this.logger, this.sourceClass, "getBlaclistActionForBYOD", "Exception : ", (Throwable)ex);
            }
        }
    }
    
    private void addBlaclistActionForCorporate() {
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            try {
                final int corpAction = (int)DBUtil.getValueFromDB("MdAppBlackListSetting", "CUSTOMER_ID", (Object)customerID, "ACTION_ON_CORPORATE_DEVICE");
                this.mdmTrackerProperties.setProperty("BLACKLIST_ACTION_ON_CORPORATE", BlacklistWhitelistAppHandler.getInstance().getBlacklistActionText(corpAction));
            }
            catch (final Exception ex) {
                SyMLogger.error(this.logger, this.sourceClass, "getBlaclistActionForCorporate", "Exception : ", (Throwable)ex);
            }
        }
    }
    
    private void addSelfEnrolledDeviceCount() {
        try {
            this.mdmTrackerProperties.setProperty("Self_Enrolled_Device_Count", String.valueOf(ManagedDeviceHandler.getInstance().getManagedSelfEnrolledDeviceCount()));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addSelfEnrolledDeviceCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addAppControllDetails() {
        String sAppControl = "BlackList";
        try {
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            if (!isMsp) {
                final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
                final Boolean isWhiteList = (Boolean)DBUtil.getValueFromDB("MdAppBlackListSetting", "CUSTOMER_ID", (Object)customerID, "IS_WHITE_LIST");
                if (isWhiteList) {
                    sAppControl = "White list";
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAppControllDetails", "Exception : ", (Throwable)e);
        }
        this.mdmTrackerProperties.setProperty("APP_CONTROL_STATUS", sAppControl);
    }
    
    private void addBlackListAppCount() {
        try {
            final Criteria cNotAllowed = new Criteria(new Column("MdAppControlStatus", "IS_ALLOWED"), (Object)Boolean.FALSE, 0);
            final int blackList = DBUtil.getRecordCount("MdAppControlStatus", "APP_GROUP_ID", cNotAllowed);
            this.mdmTrackerProperties.setProperty("BlackList_AppGroup_Count", String.valueOf(blackList));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addBlackListAppCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addWhiteListAppCount() {
        try {
            final Criteria cNotAllowed = new Criteria(new Column("MdAppControlStatus", "IS_ALLOWED"), (Object)Boolean.TRUE, 0);
            final int whiteList = DBUtil.getRecordCount("MdAppControlStatus", "APP_GROUP_ID", cNotAllowed);
            this.mdmTrackerProperties.setProperty("White_List_AppGroup_Count", String.valueOf(whiteList));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addWhiteListAppCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addAndroidAppRepositoryCount() {
        try {
            final int appCount = AppsUtil.getInstance().getAppRepositoryAppCount(2);
            this.mdmTrackerProperties.setProperty("Android_App_Repository_Count", String.valueOf(appCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidAppCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addIOSMigratedFlag() {
        this.mdmTrackerProperties.setProperty("MDM_IOS_FORM_MIGRATED", String.valueOf(true));
    }
    
    private void addBulkEnrollmentUsage() {
        try {
            String bulkEnrollCount = (String)DBUtil.getValueFromDB("SystemParams", "PARAM_NAME", (Object)"Bulk_Enroll_Device_Count", "PARAM_VALUE");
            if (bulkEnrollCount == null) {
                bulkEnrollCount = "0";
            }
            this.mdmTrackerProperties.setProperty("Bulk_Enroll_Device_Count", bulkEnrollCount);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addBulkEnrollmentUsage", "Exception : ", (Throwable)e);
        }
    }
    
    private void addMDCustomColumnUsage() {
        try {
            final int deviceNameCount = DBUtil.getRecordActualCount("ManagedDeviceExtn", "MANAGED_DEVICE_ID", new Criteria(Column.getColumn("ManagedDeviceExtn", "IS_MODIFIED"), (Object)true, 0));
            final JSONObject json = new JSONObject();
            json.put((Object)"Device_Name", (Object)deviceNameCount);
            this.mdmTrackerProperties.setProperty("CustomColumn_Details", json.toJSONString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addMDCustomColumnCSVImportUsage", "Exception : ", (Throwable)e);
        }
    }
    
    private void addDeviceEnrollReqCount() {
        try {
            final int enrollReqCount = MDMEnrollmentRequestHandler.getInstance().getAddedEnrollmentRequestCount();
            this.mdmTrackerProperties.setProperty("Enrollment_Request_Count", String.valueOf(enrollReqCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addDeviceEnrollReqCount", "Exception : ", (Throwable)e);
        }
    }
    
    private String getForwardingServerConfigured() {
        try {
            final String fsconfigured = MDMUtil.getSyMParameter("forwarding_server_config");
            if (fsconfigured != null) {
                return fsconfigured;
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addForwardingServerConfigured", "Exception : ", (Throwable)e);
        }
        return "false";
    }
    
    private ArrayList getAuthenticationType() {
        final ArrayList erollmentSetting = new ArrayList();
        String sAuthMode = "Unique Passcode";
        String sEnabledSelfEnrollment = "false";
        try {
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            if (!isMsp) {
                final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
                final int authMode = EnrollmentSettingsHandler.getInstance().getInvitationEnrollmentSettings(customerID).getInt("AUTH_MODE");
                if (authMode == 2) {
                    sAuthMode = "AD Authentication";
                }
                else if (authMode == 3) {
                    sAuthMode = "Combined Mode";
                }
                sEnabledSelfEnrollment = String.valueOf(EnrollmentSettingsHandler.getInstance().getSelfEnrollmentSettings(customerID).get("ENABLE_SELF_ENROLLMENT"));
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getAuthenticationType", "Exception : ", (Throwable)e);
        }
        erollmentSetting.add(sAuthMode);
        erollmentSetting.add(sEnabledSelfEnrollment);
        return erollmentSetting;
    }
    
    private void addTotalEnrollementRequestFailedCount() {
        try {
            this.mdmTrackerProperties.setProperty("ENROLLMENT_REQUEST_FAILED_COUNT", MEMDMTrackerUtil.addTotalEnrollementRequestFailedCount());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addEnrollmentSettings", "Exception : ", (Throwable)e);
        }
    }
    
    private void addErrorCodeDetails() {
        try {
            this.mdmTrackerProperties.setProperty("Enrollment_Failure_Error_Codes", MEMDMTrackerUtil.addErrorCodeDetails());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addErrorCodeDetails", "Exception : ", (Throwable)e);
        }
    }
    
    private void addSettings() throws DataAccessException {
        final JSONObject setting = new JSONObject();
        setting.put((Object)"Proxy", (Object)MEMDMTrackerUtil.getProxySettings());
        setting.put((Object)"Fwd_Server", (Object)MEMDMTrackerUtil.getForwardingServerConfigured());
        final ArrayList enrollmentSetting = this.getAuthenticationType();
        setting.put((Object)"Auth_mode", enrollmentSetting.get(0));
        setting.put((Object)"Self_Enrollment", enrollmentSetting.get(1));
        this.mdmTrackerProperties.setProperty("Setting", setting.toJSONString());
    }
    
    private void getIssueFixingRequirementDetails() {
        final JSONObject json = new JSONObject();
        json.put((Object)"Device_Without_Enroll_Request", (Object)String.valueOf(this.getDeviceWithoutEnrollRequestCount()));
        this.mdmTrackerProperties.setProperty("Issue_Fixing_Required", json.toJSONString());
    }
    
    private int getDeviceWithoutEnrollRequestCount() {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            sQuery.addJoin(new Join("ManagedDevice", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 1));
            sQuery.setCriteria(new Criteria(Column.getColumn("EnrollmentRequestToDevice", "ENROLLMENT_REQUEST_ID"), (Object)null, 0));
            final int count = DBUtil.getRecordActualCount(sQuery, "ManagedDevice", "RESOURCE_ID");
            return count;
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getDeviceWithoutEnrollRequestCount", "Exception : ", (Throwable)e);
            return 0;
        }
    }
    
    private void addAllPolicyCountDetails() {
        try {
            final org.json.JSONObject policyJson = ProfileUtil.getInstance().getPolicyCountJson(null);
            this.mdmTrackerProperties.setProperty("All_Profile_Policy_Summary", policyJson.toString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAllPolicyCountDetails", "Exception : ", (Throwable)e);
        }
    }
}
