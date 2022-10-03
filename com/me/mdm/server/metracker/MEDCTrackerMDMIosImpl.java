package com.me.mdm.server.metracker;

import com.me.mdm.server.ios.apns.APNsCertificateHandler;
import java.util.HashMap;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAgentSettingsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEDCTrackerMDMIosImpl extends MEMDMTrackerConstants
{
    private Properties mdmTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEDCTrackerMDMIosImpl() {
        this.mdmTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCTrackerMDMIosImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDM IOS implementation starts...");
            if (!this.mdmTrackerProperties.isEmpty()) {
                this.mdmTrackerProperties = new Properties();
            }
            this.addAPNSCertificateDetails();
            this.addIosDeviceCount();
            this.addIosNativeAgentDetails();
            this.addIosNativeAgentInstalledCount();
            this.addDEPSettings();
            this.addSupervisedDeviceCount();
            this.addIosAppGroupCount();
            this.addIosVersionCount();
            this.addIosProfileCount();
            this.addIosPolicyCountDetails();
            this.addIosAppRepositoryCount();
            this.addIOSEnterpriseAppCount();
            this.addIOSPaidAppCount();
            this.addVppDetails();
            this.addIosGroupCount();
            this.getAddedEnrollmentRequestCount();
            this.addIOSEnrollementRequestFailedCount();
            this.isAppleConfigurator2Configured();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.mdmTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MDMTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.mdmTrackerProperties;
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
    
    private void addIosProfileCount() {
        try {
            final int profileCount = ProfileUtil.getInstance().getProfileCount(1);
            this.mdmTrackerProperties.setProperty("iOS_Profile_Count", String.valueOf(profileCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIosProfileCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addIosAppGroupCount() {
        try {
            final int appCount = AppsUtil.getInstance().getAppGroupCount(1);
            this.mdmTrackerProperties.setProperty("iOS_App_Group_Count", String.valueOf(appCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIosAppCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addIosGroupCount() {
        try {
            final Criteria iosTypeCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)3, 0);
            final int group = DBUtil.getRecordCount("CustomGroup", "RESOURCE_ID", iosTypeCri);
            this.mdmTrackerProperties.setProperty("iOS_Group_Count", String.valueOf(group - 2));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIosGroupCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addIosNativeAgentDetails() {
        try {
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            Boolean isNativeAgentEnable = Boolean.FALSE;
            if (!isMsp) {
                final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
                isNativeAgentEnable = IosNativeAgentSettingsHandler.getInstance().isIOSNativeAgentEnable(customerId);
            }
            this.mdmTrackerProperties.setProperty("iOS_MEMDM_App_Enabled", String.valueOf(isNativeAgentEnable));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIosNativeAgentDetails", "Exception : ", (Throwable)e);
        }
    }
    
    private void addIosNativeAgentInstalledCount() {
        try {
            final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria cnative = new Criteria(new Column("IOSNativeAppStatus", "INSTALLATION_STATUS"), (Object)1, 0);
            final List nativeAgentList = IosNativeAppHandler.getInstance().getiOSNativeAgentResourceList(cnative.and(cPlatform));
            this.mdmTrackerProperties.setProperty("iOS_MEMDM_App_Install_Count", String.valueOf(nativeAgentList.size()));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIosNativeAgentInstalledCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addSupervisedDeviceCount() {
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdDeviceInfo"));
            sQuery.addJoin(new Join("MdDeviceInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria cSupervised = new Criteria(new Column("MdDeviceInfo", "IS_SUPERVISED"), (Object)Boolean.TRUE, 0);
            final Criteria cPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
            sQuery.setCriteria(cPlatform.and(cSupervised));
            final int supervicedCount = DBUtil.getRecordCount(sQuery, "ManagedDevice", "RESOURCE_ID");
            this.mdmTrackerProperties.setProperty("iOS_Supervised_Device_Count", String.valueOf(supervicedCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addSupervisedDeviceCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addIosAppRepositoryCount() {
        try {
            final int appRepCount = AppsUtil.getInstance().getAppRepositoryAppCount(1);
            this.mdmTrackerProperties.setProperty("iOS_App_Repository_Count", String.valueOf(appRepCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIosAppRepositoryCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addIOSPaidAppCount() {
        try {
            final int appRepCount = AppsUtil.getInstance().getAppRepositoryAppCount(1, 1);
            this.mdmTrackerProperties.setProperty("iOS_Paid_App_Count", String.valueOf(appRepCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIOSEnterpriseAppCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addIOSEnterpriseAppCount() {
        try {
            final int appRepCount = AppsUtil.getInstance().getIOSEnterpriseAppCount();
            this.mdmTrackerProperties.setProperty("iOS_Enterprise_App_Count", String.valueOf(appRepCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIOSEnterpriseAppCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addIosPolicyCountDetails() {
        try {
            final JSONObject policyJson = ProfileUtil.getInstance().getPolicyCountJson(1);
            this.mdmTrackerProperties.setProperty("iOS_Profile_Policy_Summary", policyJson.toString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIosPolicyCountDetails", "Exception : ", (Throwable)e);
        }
    }
    
    private void addIosVersionCount() {
        try {
            final JSONObject osDetails = InventoryUtil.getInstance().getOSVersionCountDetails(1);
            this.mdmTrackerProperties.setProperty("iOS_OS_Version_Summary", osDetails.toString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIosPolicyCountDetails", "Exception : ", (Throwable)e);
        }
    }
    
    private void getAddedEnrollmentRequestCount() {
        try {
            final int enrollReqCount = MDMEnrollmentRequestHandler.getInstance().getAddedEnrollmentRequestCount(1);
            this.mdmTrackerProperties.setProperty("iOS_Enrollment_Request_Count", String.valueOf(enrollReqCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getAddedEnrollmentRequestCount", "Exception : ", (Throwable)e);
        }
    }
    
    private JSONObject getAPNSRemovalDetails(final JSONObject apnsDetails) throws JSONException {
        String apnsRemoveded = "false";
        String removal_time = "";
        try {
            final String isApnsUploaded = apnsDetails.get("APNS_Uploaded").toString();
            if (isApnsUploaded.equals("false")) {
                removal_time = SyMUtil.getSyMParameter("apns_removal_time");
                if (removal_time != null) {
                    apnsRemoveded = "true";
                }
                else {
                    removal_time = "";
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getAPNSRemovalTime", "Exception : ", (Throwable)e);
        }
        apnsDetails.put("APNS_Removed", (Object)apnsRemoveded);
        apnsDetails.put("APNS_Removal_Time", (Object)removal_time);
        return apnsDetails;
    }
    
    private JSONObject getAPNSUploadedDetails(final JSONObject apnsDetails) throws JSONException {
        String apnsUploaded = "false";
        String sapnsCreationTime = "";
        try {
            final HashMap apnsCertificateInfo = (HashMap)APNsCertificateHandler.getAPNSCertificateDetails();
            if (!apnsCertificateInfo.isEmpty()) {
                apnsUploaded = "true";
                final Long apnsCreationTime = apnsCertificateInfo.get("CREATION_DATE");
                sapnsCreationTime = apnsCreationTime.toString();
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAPNSCertificateDetails", "Exception : ", (Throwable)e);
        }
        apnsDetails.put("APNS_Uploaded", (Object)apnsUploaded);
        apnsDetails.put("APNS_Created_Time", (Object)sapnsCreationTime);
        return apnsDetails;
    }
    
    private void addAPNSCertificateDetails() throws JSONException {
        JSONObject apnsDetails = new JSONObject();
        apnsDetails = this.getAPNSUploadedDetails(apnsDetails);
        apnsDetails = this.getAPNSRemovalDetails(apnsDetails);
        this.mdmTrackerProperties.setProperty("APNS_Details", apnsDetails.toString());
    }
    
    private void addDEPSettings() {
        this.mdmTrackerProperties.setProperty("DEP_Configure_Status", String.valueOf(MEMDMTrackerUtil.getDEPSettingStatus()));
    }
    
    private void addIOSEnrollementRequestFailedCount() {
        try {
            this.mdmTrackerProperties.setProperty("IOS_ENROLLMENT_REQUEST_FAILED_COUNT", MEMDMTrackerUtil.addIOSEnrollementRequestFailedCount());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addEnrollmentSettings", "Exception : ", (Throwable)e);
        }
    }
    
    private void addVppDetails() {
        final JSONObject vppDetails = MEMDMTrackerUtil.getVppDetails();
        this.mdmTrackerProperties.setProperty("vppDetails", vppDetails.toString());
    }
    
    private void isAppleConfigurator2Configured() {
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final boolean isAppleConfiguratorConfigured = Boolean.valueOf(SyMUtil.getSyMParameter("APPLE_CONFIG_2_CONFIGURED"));
            try {
                this.mdmTrackerProperties.setProperty("Apple_Configurator_2_Configured", Boolean.toString(isAppleConfiguratorConfigured));
            }
            catch (final Exception e) {
                SyMLogger.error(this.logger, this.sourceClass, "isAppleConfigurator2Configured", "Exception : ", (Throwable)e);
            }
        }
    }
}
