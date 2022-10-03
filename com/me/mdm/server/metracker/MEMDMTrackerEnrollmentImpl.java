package com.me.mdm.server.metracker;

import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.logging.Level;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.ArrayList;
import com.me.mdm.server.enrollment.EnrollmentSettingsHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackerEnrollmentImpl extends MEMDMTrackerConstants
{
    private Properties mdmTrackerProperties;
    private Logger logger;
    private String sourceClass;
    private int androidPlayStoreApp;
    private int androidEnterpriseApp;
    
    public MEMDMTrackerEnrollmentImpl() {
        this.mdmTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCTrackerMDMPEnrollmentImpl";
        this.androidPlayStoreApp = 0;
        this.androidEnterpriseApp = 2;
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDMP Enrollment implementation starts...");
            if (!this.mdmTrackerProperties.isEmpty()) {
                this.mdmTrackerProperties = new Properties();
            }
            this.addModelWiseManagedDeviceEnrollmentTypeCount();
            this.addAndroidCount();
            this.addChromeCount();
            this.addBYODSummary();
            this.addBulkEnrollmentUsage();
            this.addDeviceEnrollReqCount();
            this.addUnManagedDevicesCount();
            this.addTrashedDevicesCount();
            this.addEnrolledDevicesCount();
            this.addIosDeviceCount();
            this.addMacOSDeviceCount();
            this.addSharedIpadDeviceCount();
            this.addSelfEnrolledDeviceCount();
            this.addWindowsDeviceCount();
            this.addAddedEnrollmentRequestCount();
            this.addEnrollmentSettings();
            this.addInvitationEnrollmentCount();
            this.addTotalEnrollementRequestFailedCount();
            this.addAndroidEnrollementRequestFailedCount();
            this.addWindowsEnrollementRequestFailedCount();
            this.addIOSEnrollementRequestFailedCount();
            this.addErrorCodeDetails();
            this.addAdminEnrollmentCount();
            this.isAppleConfigurator2Configured();
            this.addFirstandLastEnrollmentTime();
            this.addLicensePromotionMessageClickedTime();
            this.addDEPTokenUploadTime();
            this.getNoOfDevicesManagedPerUserCount();
            this.checkIfUsersShareSameEmail();
            this.deprovisionCount();
            this.InstockRetireTracking();
            this.addTermsOfUseTracking();
            this.addSelfEnrollLimitTracking();
            this.deprovisionRetiredDurationCount();
            this.addDeviceWithoutRequestTracking();
            this.addMigrationData();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.mdmTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MDMTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.mdmTrackerProperties;
    }
    
    private void addModelWiseManagedDeviceEnrollmentTypeCount() {
        try {
            final JSONObject modelWiseMangedDeviceEnrollmentTypeCount = ManagedDeviceHandler.getInstance().getModelWiseManagedDeviceEnrollmentTypeCount();
            this.mdmTrackerProperties.setProperty("ModelWise_Managed_Device_Enrollment_Type", String.valueOf(modelWiseMangedDeviceEnrollmentTypeCount.toString()));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addModelWiseManagedDeviceEnrollmentTypeCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addAndroidCount() {
        try {
            final int androidCount = ManagedDeviceHandler.getInstance().getAndroidManagedDeviceCount();
            this.mdmTrackerProperties.setProperty("Android_Device_Count", String.valueOf(androidCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addChromeCount() {
        try {
            final int chromeCount = ManagedDeviceHandler.getInstance().getChromeManagedDeviceCount();
            final JSONObject deviceCount = new JSONObject();
            deviceCount.put("Chrome_Device_Count", (Object)String.valueOf(chromeCount));
            this.mdmTrackerProperties.setProperty("Device_Count_Summary", deviceCount.toString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addChromeCount", "Exception : ", (Throwable)e);
        }
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
    
    private void addTrashedDevicesCount() {
        try {
            final Criteria cri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(7), 0);
            final int trashedDeviceCount = DBUtil.getRecordCount("ManagedDevice", "RESOURCE_ID", cri);
            this.mdmTrackerProperties.setProperty("Total_Trashed_Devices", String.valueOf(trashedDeviceCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addEnrolledDevicesCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addUnManagedDevicesCount() {
        try {
            final Criteria cri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)new Integer(4), 0);
            final int unmanagedDeviceCount = DBUtil.getRecordCount("ManagedDevice", "RESOURCE_ID", cri);
            this.mdmTrackerProperties.setProperty("Total_UnManaged_Devices", String.valueOf(unmanagedDeviceCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addUnManagedDevicesCount", "Exception : ", (Throwable)e);
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
    
    private void addAddedEnrollmentRequestCount() {
        try {
            this.mdmTrackerProperties.setProperty("Android_Enrollment_Request_Count", String.valueOf(MDMEnrollmentRequestHandler.getInstance().getAddedEnrollmentRequestCount(2)));
            this.mdmTrackerProperties.setProperty("Win_Enrollment_Request_Count", String.valueOf(MDMEnrollmentRequestHandler.getInstance().getAddedEnrollmentRequestCount(3)));
            this.mdmTrackerProperties.setProperty("iOS_Enrollment_Request_Count", String.valueOf(MDMEnrollmentRequestHandler.getInstance().getAddedEnrollmentRequestCount(1)));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getAddedEnrollmentRequestCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addIosDeviceCount() {
        try {
            final int iOSCount = ManagedDeviceHandler.getInstance().getAppleManagedDeviceCount();
            this.mdmTrackerProperties.setProperty("iOS_Device_Count", String.valueOf(iOSCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIosDeviceCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addMacOSDeviceCount() {
        try {
            final int macOSCount = ManagedDeviceHandler.getInstance().getMacOSManagedDeviceCount();
            this.mdmTrackerProperties.setProperty("MacOS_Device_Count", String.valueOf(macOSCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addMacDeviceCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addSharedIpadDeviceCount() {
        try {
            final int sharedIpadCount = ManagedDeviceHandler.getInstance().getSharedIpadManagedDeviceCount();
            this.mdmTrackerProperties.setProperty("SHARED_IPAD_COUNT", String.valueOf(sharedIpadCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addSharedIpadDeviceCount", "Exception : ", (Throwable)e);
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
    
    private void addWindowsDeviceCount() {
        try {
            final int windowsPhoneCount = ManagedDeviceHandler.getInstance().getWindowsManagedDeviceCount();
            this.mdmTrackerProperties.setProperty("Windows_Device_Count", String.valueOf(windowsPhoneCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addWindowsDeviceCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addEnrollmentSettings() {
        try {
            final ArrayList enrollmentSetting = this.getAuthenticationType();
            this.mdmTrackerProperties.setProperty("Self_Enrollment", String.valueOf(enrollmentSetting.get(1)));
            this.mdmTrackerProperties.setProperty("Auth_mode", String.valueOf(enrollmentSetting.get(0)));
            this.mdmTrackerProperties.setProperty("Device_Unmanaged_Notify", String.valueOf(EnrollmentSettingsHandler.getInstance().isDeviceUnmanagedNotifyEnabledInSetup()));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addEnrollmentSettings", "Exception : ", (Throwable)e);
        }
    }
    
    private void addInvitationEnrollmentCount() {
        DMDataSetWrapper invitationDS = null;
        try {
            final JSONObject json = new JSONObject();
            final SelectQuery query = MDMEnrollmentRequestHandler.getInstance().getInvitationEnrollRequestQuery();
            invitationDS = DMDataSetWrapper.executeQuery((Object)query);
            while (invitationDS.next()) {
                json.put("Invitation_To_Myself", (Object)invitationDS.getValue("inviteMyselfCount").toString());
                json.put("Invitation_To_User", (Object)invitationDS.getValue("inviteUserCount").toString());
                json.put("Invitation_Enrolled_By_Myself", (Object)invitationDS.getValue("enrolledMyselfCount").toString());
                json.put("Invitation_Enrolled_By_User", (Object)invitationDS.getValue("enrolledUserCount").toString());
            }
            this.mdmTrackerProperties.setProperty("Smtp_Elimination", json.toString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addInvitationEnrollmentCount", "Exception : ", (Throwable)e);
        }
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
    
    private void addAndroidEnrollementRequestFailedCount() {
        try {
            this.mdmTrackerProperties.setProperty("ANDROID_ENROLLMENT_REQUEST_FAILED_COUNT", MEMDMTrackerUtil.addAndroidEnrollementRequestFailedCount());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addEnrollmentSettings", "Exception : ", (Throwable)e);
        }
    }
    
    private void addWindowsEnrollementRequestFailedCount() {
        try {
            this.mdmTrackerProperties.setProperty("WINDOWS_ENROLLMENT_REQUEST_FAILED_COUNT", MEMDMTrackerUtil.addWindowsEnrollementRequestFailedCount());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addEnrollmentSettings", "Exception : ", (Throwable)e);
        }
    }
    
    private void addIOSEnrollementRequestFailedCount() {
        try {
            this.mdmTrackerProperties.setProperty("IOS_ENROLLMENT_REQUEST_FAILED_COUNT", MEMDMTrackerUtil.addIOSEnrollementRequestFailedCount());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addEnrollmentSettings", "Exception : ", (Throwable)e);
        }
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
    
    private void addAdminEnrollmentCount() {
        this.mdmTrackerProperties.setProperty("Admin_Enrollment_Count", MEMDMTrackerUtil.getAdminEnrollmentData().toString());
    }
    
    private void isAppleConfigurator2Configured() {
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final boolean isAppleConfiguratorConfigured = Boolean.valueOf(SyMUtil.getSyMParameter("APPLE_CONFIG_2_CONFIGURED"));
            try {
                this.mdmTrackerProperties.setProperty("Apple_Configurator_2_Configured", Boolean.toString(isAppleConfiguratorConfigured));
            }
            catch (final Exception e) {
                SyMLogger.error(this.logger, this.sourceClass, "addEnrollmentSettings", "Exception : ", (Throwable)e);
            }
        }
    }
    
    private void addLicensePromotionMessageClickedTime() {
        try {
            final String timeStamp = SyMUtil.getSyMParameter("LicensePromoMessageClickedTime");
            if (timeStamp != null) {
                this.mdmTrackerProperties.setProperty("LicensePromoMessageClickedTime", timeStamp);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "LicensePromoMsg_Clicked_Time", "Exception : ", (Throwable)e);
        }
    }
    
    private void addFirstandLastEnrollmentTime() {
        try {
            final String latestEnrollmentTime = SyMUtil.getSyMParameter("LATESTENROLLTIME");
            if (latestEnrollmentTime != null) {
                this.mdmTrackerProperties.setProperty("Latest_Device_Enrollment_Time", latestEnrollmentTime);
            }
            final String firstDeviceEnrollmetTime = SyMUtil.getSyMParameter("FIRSTENROLLTIME");
            if (firstDeviceEnrollmetTime != null) {
                this.mdmTrackerProperties.setProperty("First_Device_Enrollment_Time", firstDeviceEnrollmetTime);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addFirstandLastEnrollmentTime", "Exception :", (Throwable)ex);
        }
    }
    
    private void deprovisionCount() {
        final SelectQuery deprovisionQuery = MDMCoreQuery.getInstance().getMDMQueryMap("DEPROVISION_TRACKING_QUERY");
        DMDataSetWrapper ds = null;
        try {
            final String sql = RelationalAPI.getInstance().getSelectSQL((Query)deprovisionQuery);
            SyMLogger.info(this.logger, sql, "getProperties", "MDM deprovision tracking starts");
            ds = DMDataSetWrapper.executeQuery((Object)deprovisionQuery);
            while (ds.next()) {
                final JSONObject deprovisionTrackingJson = new JSONObject();
                deprovisionTrackingJson.put("Deprovision_count", (Object)ds.getValue("Deprovision_count").toString());
                deprovisionTrackingJson.put("Deprovision_repair_count", (Object)ds.getValue("Deprovision_repair_count").toString());
                deprovisionTrackingJson.put("Deprovision_retire_count", (Object)ds.getValue("Deprovision_retire_count").toString());
                deprovisionTrackingJson.put("Deprovision_employee_left_count", (Object)ds.getValue("Deprovision_employee_left_count").toString());
                deprovisionTrackingJson.put("Deprovision_Others_count", (Object)ds.getValue("Deprovision_Others_count").toString());
                this.mdmTrackerProperties.setProperty("Deprovision_Tracking", deprovisionTrackingJson.toString());
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "deprovisionCount", "Exception : ", (Throwable)ex);
        }
    }
    
    private void InstockRetireTracking() {
        final SelectQuery InstocRetireTrackingQuery = MDMCoreQuery.getInstance().getMDMQueryMap("IN_STOCK_RETIRE_TRACKING_QUERY");
        DMDataSetWrapper ds = null;
        try {
            final String sql = RelationalAPI.getInstance().getSelectSQL((Query)InstocRetireTrackingQuery);
            SyMLogger.info(this.logger, sql, "getProperties", "MDM deprovision tracking starts");
            ds = DMDataSetWrapper.executeQuery((Object)InstocRetireTrackingQuery);
            while (ds.next()) {
                this.mdmTrackerProperties.setProperty("RETIRE_COUNT", ds.getValue("RETIRE_COUNT").toString());
                this.mdmTrackerProperties.setProperty("IN_STOCK_COUNT", ds.getValue("IN_STOCK_COUNT").toString());
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "In Stock and Retire device count", "Exception : ", (Throwable)ex);
        }
    }
    
    private void addDEPTokenUploadTime() {
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            try {
                final Long depTokenUploadTime = (Long)DBUtil.getValueFromDB("DEPTokenDetails", "CUSTOMER_ID", (Object)customerID, "TOKEN_ADDED_TIME");
                if (depTokenUploadTime != null) {
                    this.mdmTrackerProperties.setProperty("DEP_Token_Upload_Time", String.valueOf(depTokenUploadTime));
                }
            }
            catch (final Exception ex) {
                SyMLogger.error(this.logger, this.sourceClass, "addDEPTokenUploadTime", "Exception : ", (Throwable)ex);
            }
        }
    }
    
    private void getNoOfDevicesManagedPerUserCount() {
        final JSONObject json = new JSONObject();
        this.mdmTrackerProperties.setProperty("Devices_Managed_Per_User_Count", json.toString());
        final int count = 0;
        DMDataSetWrapper ds = null;
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
        sQuery.addJoin(new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        sQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        sQuery.setCriteria(new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0).and(userNotInTrashCriteria));
        sQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        final Column countColumn = Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID").count();
        countColumn.setColumnAlias("MANAGED_DEVICE_ID");
        sQuery.addSelectColumn(countColumn);
        sQuery.addGroupByColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        try {
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            while (ds.next()) {
                final Integer noOfDeviceManagedPerUser = (Integer)ds.getValue("MANAGED_DEVICE_ID");
                Integer noOfUsers = json.optInt(String.valueOf(noOfDeviceManagedPerUser) + " devices");
                json.put(String.valueOf(noOfDeviceManagedPerUser) + " devices", (noOfUsers == null) ? 1 : ((int)(++noOfUsers)));
            }
            this.mdmTrackerProperties.setProperty("Devices_Managed_Per_User_Count", json.toString());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Unable to get managed device status and count", ex);
        }
    }
    
    private void checkIfUsersShareSameEmail() {
        this.mdmTrackerProperties.setProperty("MultipleUsersShareSameEmail", "false");
        DMDataSetWrapper ds = null;
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedUser"));
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        sQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
        sQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID").count());
        final List<Column> glist = new ArrayList<Column>();
        glist.add(new Column("ManagedUser", "EMAIL_ADDRESS"));
        sQuery.setGroupByClause(new GroupByClause((List)glist, new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID").count(), (Object)1, 5)));
        sQuery.setCriteria(new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)"-", 1).and(new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)"--", 1)).and(new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)"", 1)).and(new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)null, 1).and(userNotInTrashCriteria)));
        try {
            ds = DMDataSetWrapper.executeQuery((Object)sQuery);
            Boolean UsersWithSameEmailPresent = false;
            if (ds.next()) {
                UsersWithSameEmailPresent = true;
            }
            this.mdmTrackerProperties.setProperty("MultipleUsersShareSameEmail", String.valueOf(UsersWithSameEmailPresent));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Unable to check if Users Share Same Email Count", ex);
        }
    }
    
    private void addTermsOfUseTracking() {
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery sq = MDMCoreQuery.getInstance().getMDMQueryMap("TERMS_QUERY");
            final RelationalAPI relapi = RelationalAPI.getInstance();
            int count = 0;
            boolean termsConfigured = false;
            final String sql = relapi.getSelectSQL((Query)sq);
            SyMLogger.info(this.logger, sql, "getProperties", "MDM Terms of Use tracking starts");
            ds = DMDataSetWrapper.executeQuery((Object)sq);
            while (ds.next()) {
                count = (int)ds.getValue("TERMS_COUNT");
            }
            if (count > 0) {
                termsConfigured = true;
            }
            this.mdmTrackerProperties.setProperty("Terms_Configured", String.valueOf(termsConfigured));
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addTermsOfUseTracking", "Exception : ", (Throwable)ex);
        }
    }
    
    private void addSelfEnrollLimitTracking() {
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery sq = MDMCoreQuery.getInstance().getMDMQueryMap("SELF_ENROLL_LIMIT_TRACKING_QUERY");
            int selfEnrollLimitConfiguredCount = 0;
            final String sql = RelationalAPI.getInstance().getSelectSQL((Query)sq);
            SyMLogger.info(this.logger, sql, "getProperties", "Self enrollment limit device tracking starts");
            ds = DMDataSetWrapper.executeQuery((Object)sq);
            while (ds.next()) {
                selfEnrollLimitConfiguredCount = (int)ds.getValue("SELF_ENROLL_LIMIT_CONFIGURED_COUNT");
            }
            this.mdmTrackerProperties.setProperty("SELF_ENROLL_LIMIT_CONFIGURED_COUNT", String.valueOf(selfEnrollLimitConfiguredCount));
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addSelfEnrollLimitTracking", "Exception : ", (Throwable)ex);
        }
    }
    
    private void deprovisionRetiredDurationCount() {
        final SelectQuery deprovisionQuery = MDMCoreQuery.getInstance().getMDMQueryMap("DEPROVISION_RETIRED_DURATION_TRACKING_QUERY");
        DMDataSetWrapper ds = null;
        try {
            final String sql = RelationalAPI.getInstance().getSelectSQL((Query)deprovisionQuery);
            SyMLogger.info(this.logger, sql, "getProperties", "MDM deprovision tracking starts");
            ds = DMDataSetWrapper.executeQuery((Object)deprovisionQuery);
            while (ds.next()) {
                final JSONObject deprovisionTrackingJson = new JSONObject();
                deprovisionTrackingJson.put("Retired_15_Days_Count", (Object)ds.getValue("Retired_15_Days_Count").toString());
                deprovisionTrackingJson.put("Retired_30_Days_Count", (Object)ds.getValue("Retired_30_Days_Count").toString());
                deprovisionTrackingJson.put("Retired_45_Days_Count", (Object)ds.getValue("Retired_45_Days_Count").toString());
                deprovisionTrackingJson.put("Retired_60_Days_Count", (Object)ds.getValue("Retired_60_Days_Count").toString());
                this.mdmTrackerProperties.setProperty("Deprovision_Retired_Duration_Tracking", deprovisionTrackingJson.toString());
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "deprovisionCount", "Exception : ", (Throwable)ex);
        }
    }
    
    private void addDeviceWithoutRequestTracking() {
        final SelectQuery query = MDMCoreQuery.getInstance().getMDMQueryMap("DEVICE_WITHOUT_REQUEST_TRACKING_QUERY");
        DMDataSetWrapper ds = null;
        try {
            final String sql = RelationalAPI.getInstance().getSelectSQL((Query)query);
            SyMLogger.info(this.logger, sql, "getProperties", "Device without request tracking starts");
            ds = DMDataSetWrapper.executeQuery((Object)query);
            while (ds.next()) {
                final JSONObject trackingJson = new JSONObject();
                trackingJson.put("ENROLLED_DEVICE_REQUEST_NOT_PRESENT_COUNT", (Object)ds.getValue("ENROLLED_DEVICE_REQUEST_NOT_PRESENT_COUNT").toString());
                trackingJson.put("UNMANAGED_DEVICE_REQUEST_NOT_PRESENT_COUNT", (Object)ds.getValue("UNMANAGED_DEVICE_REQUEST_NOT_PRESENT_COUNT").toString());
                trackingJson.put("WAITING_FOR_USER_ASSIGN_DEVICE_REQUEST_NOT_PRESENT_COUNT", (Object)ds.getValue("WAITING_FOR_USER_ASSIGN_DEVICE_REQUEST_NOT_PRESENT_COUNT").toString());
                trackingJson.put("FAILED_DEVICE_REQUEST_NOT_PRESENT_COUNT", (Object)ds.getValue("FAILED_DEVICE_REQUEST_NOT_PRESENT_COUNT").toString());
                trackingJson.put("TOTAL_DEVICE_REQUEST_NOT_PRESENT_COUNT", (Object)ds.getValue("TOTAL_DEVICE_REQUEST_NOT_PRESENT_COUNT").toString());
                this.mdmTrackerProperties.setProperty("DEVICE_REQUEST_NOT_PRESENT_COUNT_TRACKING", trackingJson.toString());
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addDeviceWithoutRequestTracking", "Exception : ", (Throwable)ex);
        }
    }
    
    private void addMigrationData() {
        final SelectQuery query = MDMCoreQuery.getInstance().getMDMQueryMap("MIGRATION_SUMMARY_COUNT");
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)query);
            while (ds.next()) {
                final JSONObject trackingJson = new JSONObject();
                trackingJson.put("YET_TO_APPLY_COUNT", (Object)ds.getValue("YET_TO_APPLY_COUNT").toString());
                trackingJson.put("COMMAND_ADDED_COUNT", (Object)ds.getValue("COMMAND_ADDED_COUNT").toString());
                trackingJson.put("INITIATED_COUNT", (Object)ds.getValue("INITIATED_COUNT").toString());
                trackingJson.put("INITIATED_FAILED_COUNT", (Object)ds.getValue("INITIATED_FAILED_COUNT").toString());
                trackingJson.put("FAILURE_COUNT", (Object)ds.getValue("FAILURE_COUNT").toString());
                trackingJson.put("SUCCESS_COUNT", (Object)ds.getValue("SUCCESS_COUNT").toString());
                this.mdmTrackerProperties.setProperty("MIGRATION_SUMMARY_COUNT", trackingJson.toString());
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addMigrationData", "Exception : ", (Throwable)ex);
        }
    }
}
