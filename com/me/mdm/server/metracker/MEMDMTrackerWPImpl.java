package com.me.mdm.server.metracker;

import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.windows.apps.WpCompanyHubAppHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackerWPImpl extends MEMDMTrackerConstants
{
    private Properties mdmWPTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerWPImpl() {
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
            this.addWindowsCompanyHubAppUpload();
            this.addWindowsDistributedAppVersion();
            this.addWindowsCompanyHubAppDistributionType();
            this.addWindowsVersionCount();
            this.addMEMDMAPPSettings();
            this.addWindowsPageReferralTrackingCounts();
            this.addWindowsAppConfigData();
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
    
    private void addWindowsVersionCount() {
        try {
            final JSONObject osVersionSummary = InventoryUtil.getInstance().getOSVersionCountDetails(3, Boolean.TRUE);
            final String jsonString = osVersionSummary.toString();
            this.mdmWPTrackerProperties.setProperty("Windows_Device_OSVersion_Summary", jsonString);
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addWindowsVersionSummary", "Exception : ", (Throwable)ex);
        }
    }
    
    private void addMEMDMAPPSettings() throws DataAccessException {
        final org.json.simple.JSONObject WPSettings = new org.json.simple.JSONObject();
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            try {
                WPSettings.put((Object)"Allow_App_Uninstall", (Object)this.getWindowsUnenrollSetting(customerID));
                WPSettings.put((Object)"WindowsAppBasedEnrollment", (Object)SyMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone"));
                final String service = this.getWindowsNotificationServiceType();
                if (service != null) {
                    WPSettings.put((Object)"Windows_Communication_Mode", (Object)service);
                }
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
    
    private String getWindowsNotificationServiceType() throws Exception {
        final SelectQuery windowsNotifServiceTypeQuery = MDMCoreQuery.getInstance().getMDMQueryMap("WINDOWS_MEMDM_SETTINGS_QUERY");
        DMDataSetWrapper ds = null;
        try {
            ds = DMDataSetWrapper.executeQuery((Object)windowsNotifServiceTypeQuery);
            if (ds.next()) {
                final int windowsNotification = (int)ds.getValue("WINDOWS_NOTIFICATION_SERVICE_TYPE");
                final String service = (windowsNotification == 2) ? "Polling" : "WNS";
                return service;
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "getWindowsNotificationServiceType", "Exception : ", (Throwable)ex);
        }
        return null;
    }
    
    private void addWindowsAppConfigData() {
        final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("APPS_QUERY");
        DMDataSetWrapper ds = null;
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            try {
                ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
                while (ds.next()) {
                    final int windowsAppConfigCount = (int)ds.getValue("WINDOWS_APP_CONFIG_COUNT");
                    this.mdmWPTrackerProperties.setProperty("WINDOWS_APP_CONFIG_COUNT", windowsAppConfigCount + "");
                }
                final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
                String jsonAdded = CustomerParamsHandler.getInstance().getParameterValue("JSON_CONFIGURED", (long)customerID);
                if (jsonAdded == null) {
                    jsonAdded = "0";
                }
                String xmlAdded = CustomerParamsHandler.getInstance().getParameterValue("XML_CONFIGURED", (long)customerID);
                if (xmlAdded == null) {
                    xmlAdded = "0";
                }
                String maxLen = CustomerParamsHandler.getInstance().getParameterValue("CONFIG_MAX_LENGTH", (long)customerID);
                if (maxLen == null) {
                    maxLen = "0";
                }
                final JSONObject params = new JSONObject();
                params.put("JSON_APPCONFIG_ADDED", (Object)jsonAdded);
                params.put("XML_APPCONFIG_ADDED", (Object)xmlAdded);
                params.put("APP_CONFIG_MAX_LENGTH", (Object)maxLen);
                this.mdmWPTrackerProperties.setProperty("AppConfigDetails", params.toString());
            }
            catch (final Exception ex) {
                SyMLogger.error(this.logger, this.sourceClass, "addWindowsAppConfigData", "Exception : ", (Throwable)ex);
            }
        }
    }
    
    private void addWindowsPageReferralTrackingCounts() throws Exception {
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final String icdEnrollPageFromNewFeatureMessage = CustomerParamsHandler.getInstance().getParameterValue("Win10_Laptop_Support_Link_To_ICDEnroll_Count", (long)CustomerInfoUtil.getInstance().getDefaultCustomer());
            final String icdEnrollPageFromCorporateDeviceEnrollInfoMessage = CustomerParamsHandler.getInstance().getParameterValue("Win10_CorporateDevice_To_ICDEnroll_Count", (long)CustomerInfoUtil.getInstance().getDefaultCustomer());
            final JSONObject wpPageReferralClickCount = new JSONObject();
            wpPageReferralClickCount.put("Win10_Laptop_Support_Link_To_ICDEnroll_Count", (Object)icdEnrollPageFromNewFeatureMessage);
            wpPageReferralClickCount.put("Win10_CorporateDevice_To_ICDEnroll_Count", (Object)icdEnrollPageFromCorporateDeviceEnrollInfoMessage);
            this.mdmWPTrackerProperties.setProperty("WindowsPageReferralClickCount", wpPageReferralClickCount.toString());
        }
    }
}
