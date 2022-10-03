package com.me.mdm.server.metracker;

import java.util.Hashtable;
import org.json.simple.JSONArray;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.me.mdm.server.tracker.MDMCoreQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.APNSImpl;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.ios.apns.APNsCertificateHandler;
import java.util.HashMap;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAgentSettingsHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackerIosImpl extends MEMDMTrackerConstants
{
    private Properties mdmTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerIosImpl() {
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
            this.addAPNSWakeUpDetails();
            this.addIosNativeAgentDetails();
            this.addIosNativeAgentInstalledCount();
            this.addReEstablishFeatureStatus();
            this.addSupervisedDeviceCount();
            this.addIosVersionCount();
            this.addDEPSettings();
            this.addMDMAppAnalyticData();
            this.addDEPAccountType();
            this.addIOSAppTypes();
            this.addScepCertCountDetails();
            this.setMacAccountConfigDetails();
            this.addIOSVppDetails();
            this.addRecoveryLockEnabledCount();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.mdmTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MDMTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.mdmTrackerProperties;
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
    
    private void addIosVersionCount() {
        try {
            final JSONObject osDetails = InventoryUtil.getInstance().getOSVersionCountDetails(1);
            this.mdmTrackerProperties.setProperty("iOS_OS_Version_Summary", osDetails.toString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIosPolicyCountDetails", "Exception : ", (Throwable)e);
        }
    }
    
    private void addScepCertCountDetails() {
        try {
            final JSONObject scepCertCountJson = InventoryUtil.getInstance().getScepCertCountDetails(1);
            final int scepCertCount = scepCertCountJson.getInt("ScepCertificateCount");
            this.mdmTrackerProperties.setProperty("ScepCertificateCount", String.valueOf(scepCertCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidScepCount", "Exception : ", (Throwable)e);
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
    
    private void addAPNSWakeUpDetails() {
        final JSONObject json = new JSONObject();
        try {
            final String error = String.valueOf(ApiFactoryProvider.getCacheAccessAPI().getCache("ApnsConnectionError", 1));
            json.put("APNSWakeupError", (Object)(error.equals("null") ? "None" : error));
            json.put("APNSLegacyAPI", APNSImpl.isApnsAPIFallback());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "MEMDMTrackerIosImpl addAPNSWakeUpDetails() error ", e);
        }
        this.mdmTrackerProperties.setProperty("APNSWakeupDetails", json.toString());
    }
    
    private void addDEPSettings() {
        this.mdmTrackerProperties.setProperty("DEP_Configure_Status", String.valueOf(MEMDMTrackerUtil.getDEPSettingStatus()));
    }
    
    private void addMDMAppAnalyticData() {
        this.mdmTrackerProperties.setProperty("MDMAppAnalytics", String.valueOf(MEMDMTrackerUtil.getMDMAppAnalyticData()));
    }
    
    private void addDEPAccountType() {
        DMDataSetWrapper dataSet = null;
        try {
            final SelectQuery query = MDMCoreQuery.mdmCoreQuery.getMDMQueryMap("DEP_QUERY");
            final JSONObject depJSON = new JSONObject();
            dataSet = DMDataSetWrapper.executeQuery((Object)query);
            if (dataSet.next()) {
                if (dataSet.getValue("ENTERPRISE_DEP_COUNT") != null) {
                    depJSON.put("ENTERPRISE_DEP_COUNT", (Object)String.valueOf(dataSet.getValue("ENTERPRISE_DEP_COUNT")));
                }
                if (dataSet.getValue("EDUCATION_DEP_COUNT") != null) {
                    depJSON.put("EDUCATION_DEP_COUNT", (Object)String.valueOf(dataSet.getValue("EDUCATION_DEP_COUNT")));
                }
                if (dataSet.getValue("SHARED_DEP_SERVER_COUNT") != null) {
                    depJSON.put("SHARED_DEP_SERVER_COUNT", (Object)String.valueOf(dataSet.getValue("SHARED_DEP_SERVER_COUNT")));
                }
                if (dataSet.getValue("SHARED_DEP_QUOTA_SIZE_SERVER_COUNT") != null) {
                    depJSON.put("SHARED_DEP_QUOTA_SIZE_SERVER_COUNT", (Object)String.valueOf(dataSet.getValue("SHARED_DEP_QUOTA_SIZE_SERVER_COUNT")));
                }
                if (dataSet.getValue("SHARED_DEP_RESIDENT_USER_SERVER_COUNT") != null) {
                    depJSON.put("SHARED_DEP_RESIDENT_USER_SERVER_COUNT", (Object)String.valueOf(dataSet.getValue("SHARED_DEP_RESIDENT_USER_SERVER_COUNT")));
                }
                if (dataSet.getValue("SHARED_DEP_USER_CONTROLLED_SERVER_COUNT") != null) {
                    depJSON.put("SHARED_DEP_USER_CONTROLLED_SERVER_COUNT", (Object)String.valueOf(dataSet.getValue("SHARED_DEP_USER_CONTROLLED_SERVER_COUNT")));
                }
                if (dataSet.getValue("DEP_SELF_ENROLL_SERVERS_COUNT") != null) {
                    depJSON.put("DEP_SELF_ENROLL_SERVERS_COUNT", (Object)String.valueOf(dataSet.getValue("DEP_SELF_ENROLL_SERVERS_COUNT")));
                }
                depJSON.put("DEP_APPLE_UI_FEATURE", !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotUseDepWebView"));
                this.mdmTrackerProperties.setProperty("DEP_DETAILS", depJSON.toString());
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(MEMDMTrackerIosImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void addOSVersionCount() {
        final JSONObject osVersionSummary = new JSONObject();
        try {
            final SelectQuery selectQuery = MDMCoreQuery.getInstance().getOSSummaryDetails();
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSetWrapper.next()) {
                MDMUtil.getInstance();
                final List<String> columns = MDMUtil.getColumnNamesFromQuery(selectQuery);
                columns.remove("CUSTOMER_ID");
                for (final String columnName : columns) {
                    if (columnName.contains("IOS_") || columnName.contains("MAC_") || columnName.contains("TV_")) {
                        final Integer osVersionCount = (Integer)dataSetWrapper.getValue(columnName);
                        osVersionSummary.put(columnName, (Object)osVersionCount);
                    }
                }
            }
            this.mdmTrackerProperties.setProperty("APPLE_Device_OSVersion_Summary", osVersionSummary.toString());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in osversion count", e);
        }
    }
    
    private void setMacAccountConfigDetails() {
        try {
            final SelectQuery query = MDMCoreQuery.getInstance().getMDMQueryMap("ACCOUNT_CONFIGURED_QUERY");
            final JSONArray resultJSONArray = MDMUtil.executeSelectQuery(query);
            final JSONObject tempJSON = new JSONObject();
            for (int i = 0; i < resultJSONArray.size(); ++i) {
                final org.json.simple.JSONObject resultJSON = (org.json.simple.JSONObject)resultJSONArray.get(i);
                tempJSON.put("macOS_Account_Config_Associate_Fail_Count", (Object)((resultJSON.get((Object)"macOS_Account_Config_Associate_Fail_Count") == null) ? Integer.valueOf(0) : resultJSON.get((Object)"macOS_Account_Config_Associate_Fail_Count").toString()));
                tempJSON.put("macOS_Account_Config_Associate_Success_Count", (Object)((resultJSON.get((Object)"macOS_Account_Config_Associate_Success_Count") == null) ? Integer.valueOf(0) : resultJSON.get((Object)"macOS_Account_Config_Associate_Success_Count").toString()));
                this.mdmTrackerProperties.setProperty("macOS_Account_Config_Count", tempJSON.toString());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "MEMDMTrackerProfileImpl: Exception while getting macOS account config details ", e);
        }
    }
    
    public void addIOSAppTypes() {
        final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("APPS_QUERY");
        DMDataSetWrapper ds = null;
        final JSONObject enterpriseAppTypeCount = new JSONObject();
        final JSONObject iOSAppsTypes = new JSONObject();
        try {
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                final int adhocAppCount = (int)ds.getValue("iOS_Adhoc_Provisioned_App_Count");
                final int developerAppCount = (int)ds.getValue("iOS_Developer_Provisioned_App_Count");
                final int distributionAppCount = (int)ds.getValue("iOS_Distribution_Provisioned_App_Count");
                enterpriseAppTypeCount.put("iOS_Adhoc_Provisioned_App_Count", adhocAppCount);
                enterpriseAppTypeCount.put("iOS_Developer_Provisioned_App_Count", developerAppCount);
                enterpriseAppTypeCount.put("iOS_Distribution_Provisioned_App_Count", distributionAppCount);
                this.mdmTrackerProperties.setProperty("iOS_Enterprise_App_Types", enterpriseAppTypeCount.toString());
                iOSAppsTypes.put("iPhone_Enterprise_Apps_Count", (Object)ds.getValue("iPhone_Enterprise_Apps_Count"));
                iOSAppsTypes.put("iPad_Enterprise_Apps_Count", (Object)ds.getValue("iPad_Enterprise_Apps_Count"));
                iOSAppsTypes.put("iPod_Enterprise_Apps_Count", (Object)ds.getValue("iPod_Enterprise_Apps_Count"));
                iOSAppsTypes.put("appleTV_Enterprise_Apps_Count", (Object)ds.getValue("appleTV_Enterprise_Apps_Count"));
                iOSAppsTypes.put("macOS_Enterprise_Apps_Count", (Object)ds.getValue("macOS_Enterprise_Apps_Count"));
                iOSAppsTypes.put("iPhone_App_Store_Apps_Count", (Object)ds.getValue("iPhone_App_Store_Apps_Count"));
                iOSAppsTypes.put("iPad_App_Store_Apps_Count", (Object)ds.getValue("iPad_App_Store_Apps_Count"));
                iOSAppsTypes.put("iPod_App_Store_Apps_Count", (Object)ds.getValue("iPod_App_Store_Apps_Count"));
                iOSAppsTypes.put("appleTV_App_Store_Apps_Count", (Object)ds.getValue("appleTV_App_Store_Apps_Count"));
                iOSAppsTypes.put("macOS_App_Store_Apps_Count", (Object)ds.getValue("macOS_App_Store_Apps_Count"));
                iOSAppsTypes.put("iPhone_Vpp_Apps_Count", (Object)ds.getValue("iPhone_Vpp_Apps_Count"));
                iOSAppsTypes.put("iPad_Vpp_Apps_Count", (Object)ds.getValue("iPad_Vpp_Apps_Count"));
                iOSAppsTypes.put("iPod_Vpp_Apps_Count", (Object)ds.getValue("iPod_Vpp_Apps_Count"));
                iOSAppsTypes.put("appleTV_Vpp_Apps_Count", (Object)ds.getValue("appleTV_Vpp_Apps_Count"));
                iOSAppsTypes.put("macOS_Vpp_Apps_Count", (Object)ds.getValue("macOS_Vpp_Apps_Count"));
                ((Hashtable<String, JSONObject>)this.mdmTrackerProperties).put("iOS_App_Types", iOSAppsTypes);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addIOSAppTypes", "Exception : ", (Throwable)ex);
        }
    }
    
    public void addIOSVppDetails() {
        try {
            final JSONObject iOSVPPTokenDetails = new JSONObject();
            final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("IOS_VPP_TOKEN_DETAILS_QUERY");
            DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                if (ds.getValue("iOS_Total_Vpp_Tokens_Count") != null) {
                    iOSVPPTokenDetails.put("iOS_Total_Vpp_Tokens_Count", (int)ds.getValue("iOS_Total_Vpp_Tokens_Count") + iOSVPPTokenDetails.optInt("iOS_Total_Vpp_Tokens_Count", 0));
                }
                if (ds.getValue("iOS_Device_Based_Vpp_Tokens_Count") != null) {
                    iOSVPPTokenDetails.put("iOS_Device_Based_Vpp_Tokens_Count", (int)ds.getValue("iOS_Device_Based_Vpp_Tokens_Count") + iOSVPPTokenDetails.optInt("iOS_Device_Based_Vpp_Tokens_Count", 0));
                }
                if (ds.getValue("iOS_User_Based_Vpp_Tokens_Count") != null) {
                    iOSVPPTokenDetails.put("iOS_User_Based_Vpp_Tokens_Count", (int)ds.getValue("iOS_User_Based_Vpp_Tokens_Count") + iOSVPPTokenDetails.optInt("iOS_User_Based_Vpp_Tokens_Count", 0));
                }
                if (ds.getValue("iOS_Device_Based_Vpp_Apps_Count") != null) {
                    iOSVPPTokenDetails.put("iOS_Device_Based_Vpp_Apps_Count", (int)ds.getValue("iOS_Device_Based_Vpp_Apps_Count") + iOSVPPTokenDetails.optInt("iOS_Device_Based_Vpp_Apps_Count", 0));
                }
                if (ds.getValue("iOS_User_Based_Vpp_Apps_Count") != null) {
                    iOSVPPTokenDetails.put("iOS_User_Based_Vpp_Apps_Count", (int)ds.getValue("iOS_User_Based_Vpp_Apps_Count") + iOSVPPTokenDetails.optInt("iOS_User_Based_Vpp_Apps_Count", 0));
                }
            }
            ds = DMDataSetWrapper.executeQuery((Object)MDMCoreQuery.getInstance().getMDMQueryMap("IOS_APPS_IN_MULTIPLE_VPP_TOKEN_QUERY"));
            while (ds.next()) {
                if (ds.getValue("iOS_Apps_In_Multiple_Vpp_Token") != null) {
                    iOSVPPTokenDetails.put("iOS_Apps_In_Multiple_Vpp_Token", (int)ds.getValue("iOS_Apps_In_Multiple_Vpp_Token") + iOSVPPTokenDetails.optInt("iOS_Apps_In_Multiple_Vpp_Token", 0));
                }
            }
            ds = DMDataSetWrapper.executeQuery((Object)MDMCoreQuery.getInstance().getMDMQueryMap("MULTIPLE_VPP_MANAGING_TECH_QUERY"));
            while (ds.next()) {
                if (ds.getValue("Multiple_Vpp_Managing_Tech_Count") != null) {
                    iOSVPPTokenDetails.put("Multiple_Vpp_Managing_Tech_Count", (int)ds.getValue("Multiple_Vpp_Managing_Tech_Count") + iOSVPPTokenDetails.optInt("Multiple_Vpp_Managing_Tech_Count", 0));
                }
            }
            ds = DMDataSetWrapper.executeQuery((Object)MDMCoreQuery.getInstance().getMDMQueryMap("MULTIPLE_VPP_MANAGING_RBDA_TECH_QUERY"));
            while (ds.next()) {
                if (ds.getValue("Multiple_Vpp_Managing_RBDA_Tech_Count") != null) {
                    iOSVPPTokenDetails.put("Multiple_Vpp_Managing_RBDA_Tech_Count", (int)ds.getValue("Multiple_Vpp_Managing_RBDA_Tech_Count") + iOSVPPTokenDetails.optInt("Multiple_Vpp_Managing_RBDA_Tech_Count", 0));
                }
            }
            ds = DMDataSetWrapper.executeQuery((Object)MDMCoreQuery.getInstance().getMDMQueryMap("DEVICES_WITH_MULTIPLE_VPP_ASSOCIATION_QUERY"));
            while (ds.next()) {
                if (ds.getValue("Devices_With_Multiple_Vpp_Associations") != null) {
                    iOSVPPTokenDetails.put("Devices_With_Multiple_Vpp_Associations", (int)ds.getValue("Devices_With_Multiple_Vpp_Associations") + iOSVPPTokenDetails.optInt("Devices_With_Multiple_Vpp_Associations", 0));
                }
            }
            ds = DMDataSetWrapper.executeQuery((Object)MDMCoreQuery.getInstance().getMDMQueryMap("GROUPS_WITH_MULTIPLE_VPP_ASSOCIATION_QUERY"));
            while (ds.next()) {
                if (ds.getValue("Groups_With_Multiple_Vpp_Associations") != null) {
                    iOSVPPTokenDetails.put("Groups_With_Multiple_Vpp_Associations", (int)ds.getValue("Groups_With_Multiple_Vpp_Associations") + iOSVPPTokenDetails.optInt("Groups_With_Multiple_Vpp_Associations", 0));
                }
            }
            ((Hashtable<String, JSONObject>)this.mdmTrackerProperties).put("iOS_Vpp_Details", iOSVPPTokenDetails);
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addIOSBusinessStoreDetails", "Exception : ", (Throwable)ex);
        }
    }
    
    public void addRecoveryLockEnabledCount() {
        final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("RECOVERY_LOCK_ENABLED_COUNT");
        try {
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dataSet.next()) {
                final Object recoveryLockCount = dataSet.getValue("RECOVERY_LOCK_ENABLED_COUNT");
                final Object firmwarePasswordCount = dataSet.getValue("FIRMWARE_PASSWORD_ENABLED_COUNT");
                final int recoveryLockEnabled = (int)((recoveryLockCount == null) ? 0 : recoveryLockCount);
                final int firmwarePasswordEnabled = (int)((firmwarePasswordCount == null) ? 0 : firmwarePasswordCount);
                final JSONObject recoveryLockDetails = new JSONObject();
                recoveryLockDetails.put("RECOVERY_LOCK_ENABLED_COUNT", recoveryLockEnabled);
                recoveryLockDetails.put("FIRMWARE_PASSWORD_ENABLED_COUNT", firmwarePasswordEnabled);
                this.mdmTrackerProperties.setProperty("RECOVERY_LOCK_DETAILS", recoveryLockDetails.toString());
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "getRecoveryLockEnabledCount", "Exception : ", (Throwable)ex);
        }
    }
    
    private void addReEstablishFeatureStatus() {
        this.mdmTrackerProperties.setProperty("IOS_RE_ESTABLISH_COUNT", MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotUseDepWebView").toString());
    }
}
