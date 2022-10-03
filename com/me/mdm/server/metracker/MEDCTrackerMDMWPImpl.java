package com.me.mdm.server.metracker;

import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.simple.JSONObject;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.windows.apps.WpCompanyHubAppHandler;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
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

public class MEDCTrackerMDMWPImpl extends MEMDMTrackerConstants
{
    private Properties mdmWPTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEDCTrackerMDMWPImpl() {
        this.mdmWPTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCTrackerMDMWPImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDM Windows implementation starts...");
            if (!this.mdmWPTrackerProperties.isEmpty()) {
                this.mdmWPTrackerProperties = new Properties();
            }
            this.addWindowsDeviceCount();
            this.addWindowsProfileCount();
            this.addWindowsAppRepositoryCount();
            this.addWindowsGroupCount();
            this.addWindowsAETFileUpload();
            this.addWindowsCertFileUpload();
            this.addWindowsCompanyHubAppUpload();
            this.addWindowsDistributedAppVersion();
            this.addWindowsCompanyHubAppDistributionType();
            this.addWindowsVersionCount();
            this.getAddedEnrollmentRequestCount();
            this.addWindowsEnrollementRequestFailedCount();
            this.addMEMDMAPPSettings();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.getMDMWPTrackerProperties());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "mdmWPTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.getMDMWPTrackerProperties();
    }
    
    private Properties getMDMWPTrackerProperties() {
        return this.mdmWPTrackerProperties;
    }
    
    private void addWindowsDeviceCount() {
        try {
            final int windowsPhoneCount = ManagedDeviceHandler.getInstance().getWindowsManagedDeviceCount();
            this.mdmWPTrackerProperties.setProperty("Windows_Device_Count", String.valueOf(windowsPhoneCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addWindowsDeviceCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addWindowsProfileCount() {
        try {
            final int profileCount = ProfileUtil.getInstance().getProfileCount(3);
            this.mdmWPTrackerProperties.setProperty("Windows_Profile_Count", String.valueOf(profileCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addWindowsProfileCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addWindowsAppRepositoryCount() {
        try {
            final int appCount = AppsUtil.getInstance().getAppRepositoryAppCount(3);
            this.mdmWPTrackerProperties.setProperty("Windows_App_Repository_Count", String.valueOf(appCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addWindowsAppCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addWindowsGroupCount() {
        try {
            final Criteria windowsTypeCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)5, 0);
            final int group = DBUtil.getRecordCount("CustomGroup", "RESOURCE_ID", windowsTypeCri);
            this.mdmWPTrackerProperties.setProperty("Windows_Group_Count", String.valueOf(group - 2));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIosGroupCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addWindowsAETFileUpload() {
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            final boolean isAET = WpAppSettingsHandler.getInstance().isAETUploaded(customerID);
            this.mdmWPTrackerProperties.setProperty("Is_AET_File_Upload", String.valueOf(isAET));
        }
    }
    
    private void addWindowsCertFileUpload() {
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            Properties aetproperties = new Properties();
            aetproperties = WpAppSettingsHandler.getInstance().getWpAETDetails(customerID);
            boolean isCert = false;
            isCert = (aetproperties != null && aetproperties.get("CERT_FILE_PATH") != null);
            this.mdmWPTrackerProperties.setProperty("Is_CSC_File_Upload", String.valueOf(isCert));
        }
    }
    
    private void addWindowsCompanyHubAppUpload() {
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            final boolean isWCHApp = WpCompanyHubAppHandler.getInstance().isWPCompanyHubAppUpload(customerID);
            this.mdmWPTrackerProperties.setProperty("Is_Company_Hub_App_Upload", String.valueOf(isWCHApp));
        }
    }
    
    private void addWindowsDistributedAppVersion() {
        String wpAppVersion = "NA";
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            final Long wpNativeAppId = WpCompanyHubAppHandler.getInstance().getWPCompanyHubAppId(customerID);
            if (wpNativeAppId != null) {
                final HashMap appsMap = MDMUtil.getInstance().getAppDetails(wpNativeAppId);
                wpAppVersion = appsMap.get("APP_VERSION");
            }
            this.mdmWPTrackerProperties.setProperty("Windows_Distributed_Company_Hub_App_Version", wpAppVersion);
        }
    }
    
    private void addWindowsCompanyHubAppDistributionType() {
        try {
            String distributionTypeStr = "NA";
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            if (!isMsp) {
                final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
                final boolean isWCHApp = WpCompanyHubAppHandler.getInstance().isWPCompanyHubAppUpload(customerID);
                if (isWCHApp) {
                    final int distributionType = (int)DBUtil.getValueFromDB("WpAppSettings", "CUSTOMER_ID", (Object)customerID, "DISTRIBUTE_TYPE");
                    if (distributionType == 0) {
                        distributionTypeStr = "via Mail";
                    }
                    else {
                        distributionTypeStr = "via Silent Install";
                    }
                }
                this.mdmWPTrackerProperties.setProperty("Windows_Company_Hub_App_Distribution_Type", distributionTypeStr);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addWindowsCompanyHubAppDistributionType", "Exception : ", (Throwable)e);
        }
    }
    
    private void getAddedEnrollmentRequestCount() {
        try {
            final int enrollReqCount = MDMEnrollmentRequestHandler.getInstance().getAddedEnrollmentRequestCount(3);
            this.mdmWPTrackerProperties.setProperty("Win_Enrollment_Request_Count", String.valueOf(enrollReqCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "getAddedEnrollmentRequestCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addWindowsEnrollementRequestFailedCount() {
        try {
            this.mdmWPTrackerProperties.setProperty("WINDOWS_ENROLLMENT_REQUEST_FAILED_COUNT", MEMDMTrackerUtil.addWindowsEnrollementRequestFailedCount());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addEnrollmentSettings", "Exception : ", (Throwable)e);
        }
    }
    
    private void addMEMDMAPPSettings() throws DataAccessException {
        final JSONObject WPSettings = new JSONObject();
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            try {
                WPSettings.put((Object)"Allow_App_Uninstall", (Object)this.getWindowsUnenrollSetting(customerID));
                WPSettings.put((Object)"WindowsAppBasedEnrollment", (Object)SyMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone"));
            }
            catch (final Exception exp) {
                SyMLogger.error(this.logger, this.sourceClass, "addMEMDMAPPSettings", "Exception : ", (Throwable)exp);
            }
        }
        this.mdmWPTrackerProperties.setProperty("MEMDM_Agent_Setting", WPSettings.toJSONString());
    }
    
    private boolean getWindowsUnenrollSetting(final Long customerID) throws Exception {
        return MDMAgentSettingsHandler.getInstance().isWPUserUnEnroll(customerID);
    }
    
    private void addWindowsVersionCount() {
        try {
            final org.json.JSONObject osVersionSummary = InventoryUtil.getInstance().getOSVersionCountDetails(3, Boolean.TRUE);
            final String jsonString = osVersionSummary.toString();
            this.mdmWPTrackerProperties.setProperty("Windows_Device_OSVersion_Summary", jsonString);
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addWindowsVersionSummary", "Exception : ", (Throwable)ex);
        }
    }
}
