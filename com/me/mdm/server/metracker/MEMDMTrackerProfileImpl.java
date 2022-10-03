package com.me.mdm.server.metracker;

import java.util.Hashtable;
import com.me.mdm.server.tracker.MDMTrackerUtil;
import com.me.mdm.server.tracker.MDMCoreQuery;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.DMDataSetWrapper;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackerProfileImpl extends MEMDMTrackerConstants
{
    private Properties mdmTrackerProperties;
    private Logger logger;
    private String sourceClass;
    
    public MEMDMTrackerProfileImpl() {
        this.mdmTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCTrackerMDMPProfileImpl";
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDMP Profile implementation starts...");
            if (!this.mdmTrackerProperties.isEmpty()) {
                this.mdmTrackerProperties = new Properties();
            }
            this.addAllPolicyCountDetails();
            this.addModelWisePolicyCountDetails();
            this.addModelWiseSuccessfullyDistributedPolicyCountDetails();
            this.addModelWiseUemPolicyCountDetails();
            this.addModelWiseSuccessfullyDistributedUemPolicyCountDetails();
            this.addProfileCount();
            this.setKioskUsageDetails();
            this.addWebPolicyCountDetails();
            this.setProfileDetailsPageVisitCount();
            this.setProfileAddedDistributedDetails();
            this.setExchangeActiveSyncPolicyUsage();
            this.setRestrictionUsage();
            this.setProfileCertificateDetails();
            this.getContainerPasscodeCount();
            this.addVPNTypes();
            this.addCustomProfile();
            this.addAppTrashData();
            this.setEFRPMailIdCount();
            this.addMorePolicyCountDetails();
            this.addAppConfigurationProfileCount();
            this.addADMXBackedConfigDetails();
            this.addAppNotificationsPolicyUsage();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.mdmTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MDMTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.mdmTrackerProperties;
    }
    
    private void addAllPolicyCountDetails() {
        try {
            final JSONObject policyJson = ProfileUtil.getInstance().getPolicyCountJson(null);
            this.mdmTrackerProperties.setProperty("All_Profile_Policy_Summary", policyJson.toString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addAllPolicyCountDetails", "Exception : ", (Throwable)e);
        }
    }
    
    private void addModelWisePolicyCountDetails() {
        try {
            final JSONObject policyJson = ProfileUtil.getInstance().getModelWisePolicyCountJson(Boolean.FALSE);
            this.mdmTrackerProperties.setProperty("ModelWise_Policy_Summary", policyJson.toString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addModelWisePolicyCountDetails", "Exception : ", (Throwable)e);
        }
    }
    
    private void addModelWiseSuccessfullyDistributedPolicyCountDetails() {
        try {
            final JSONObject policyJson = ProfileUtil.getInstance().getModelWiseSuccessfullyDistributedPolicyCountJson(Boolean.FALSE);
            this.mdmTrackerProperties.setProperty("ModelWise_Successfully_Distributed_Policy_Summary", policyJson.toString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addModelWiseSuccessfullyDistributedPolicyCountDetails", "Exception : ", (Throwable)e);
        }
    }
    
    private void addModelWiseUemPolicyCountDetails() {
        try {
            final JSONObject policyJson = ProfileUtil.getInstance().getModelWisePolicyCountJson(Boolean.TRUE);
            this.mdmTrackerProperties.setProperty("ModelWise_Uem_Policy_Summary", policyJson.toString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addModelWiseUemPolicyCountDetails", "Exception : ", (Throwable)e);
        }
    }
    
    private void addModelWiseSuccessfullyDistributedUemPolicyCountDetails() {
        try {
            final JSONObject policyJson = ProfileUtil.getInstance().getModelWiseSuccessfullyDistributedPolicyCountJson(Boolean.TRUE);
            this.mdmTrackerProperties.setProperty("ModelWise_Successfully_Distributed_Uem_Policy_Summary", policyJson.toString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addModelWiseSuccessfullyDistributedUemPolicyCountDetails", "Exception : ", (Throwable)e);
        }
    }
    
    private void addMorePolicyCountDetails() {
        try {
            final JSONObject policyJson = ProfileUtil.getInstance().getAddMorePolicyCountJson();
            this.mdmTrackerProperties.setProperty("Add_More_Policy_Summary", policyJson.toString());
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addMorePolicyCountDetails", "Exception : ", (Throwable)e);
        }
    }
    
    private void addProfileCount() {
        DMDataSetWrapper ds = null;
        try {
            final SelectQuery query = this.profileCountQuery();
            ds = DMDataSetWrapper.executeQuery((Object)query);
            final JSONObject trashProfileCount = new JSONObject();
            while (ds.next()) {
                this.mdmTrackerProperties.setProperty("Android_Profile_Count", ds.getValue("ANDROID_COUNT").toString());
                this.mdmTrackerProperties.setProperty("iOS_Profile_Count", ds.getValue("IOS_COUNT").toString());
                this.mdmTrackerProperties.setProperty("Windows_Profile_Count", ds.getValue("WINDOWS_COUNT").toString());
                this.mdmTrackerProperties.setProperty("macOS_Profile_Count", ds.getValue("MACOS_COUNT").toString());
                this.mdmTrackerProperties.setProperty("tvOS_Profile_Count", ds.getValue("TVOS_COUNT").toString());
                final JSONObject profileCount = new JSONObject();
                profileCount.put("Chrome_Profile_Count", (Object)ds.getValue("CHROME_COUNT").toString());
                profileCount.put("Chrome_User_Profile_count", (Object)ds.getValue("CHROME_USER_PROFILE_COUNT").toString());
                this.mdmTrackerProperties.setProperty("Profile_Count_Summary", profileCount.toString());
                final int totalCount = (int)ds.getValue("ANDROID_COUNT") + (int)ds.getValue("IOS_COUNT") + (int)ds.getValue("WINDOWS_COUNT") + (int)ds.getValue("CHROME_COUNT") + (int)ds.getValue("MACOS_COUNT") + (int)ds.getValue("TVOS_COUNT");
                this.mdmTrackerProperties.setProperty("Total_Profile_Count", String.valueOf(totalCount));
                trashProfileCount.put("android_profile_in_trash", (Object)ds.getValue("ANDROID_TRASH_COUNT").toString());
                trashProfileCount.put("ios_profile_in_trash", (Object)ds.getValue("IOS_TRASH_COUNT").toString());
                trashProfileCount.put("windows_profile_in_trash", (Object)ds.getValue("WINDOWS_TRASH_COUNT").toString());
                trashProfileCount.put("chrome_profile_in_trash", (Object)ds.getValue("CHROME_TRASH_COUNT").toString());
                trashProfileCount.put("macos_profile_in_trash", (Object)ds.getValue("MACOS_TRASH_COUNT").toString());
                trashProfileCount.put("tvos_profile_in_trash", (Object)ds.getValue("TVOS_TRASH_COUNT").toString());
                this.mdmTrackerProperties.setProperty("Trash_Profile_Count_Details", trashProfileCount.toString());
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred while getting Profile count", ex);
        }
    }
    
    private SelectQuery profileCountQuery() {
        final SelectQuery profileCountQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        final Criteria isAndroidPlatform = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)2, 0);
        final Criteria isPublishedProfile = new Criteria(new Column("Profile", "PROFILE_ID"), (Object)new Column("RecentPubProfileToColln", "PROFILE_ID"), 0);
        final Criteria isIosPlatform = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)1, 0);
        final Criteria isWindowsPlatform = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)3, 0);
        final Criteria isChromePlatform = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)4, 0);
        final Criteria isMacPlatform = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)6, 0);
        final Criteria isTvOSPlatform = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)7, 0);
        final Criteria notInTrash = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
        final Criteria inTrash = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)true, 0);
        final Criteria profileType = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)1, 0);
        final Criteria notDeviceScope = new Criteria(Column.getColumn("Profile", "SCOPE"), (Object)0, 1);
        final CaseExpression androidcount = new CaseExpression("ANDROID_COUNT");
        androidcount.addWhen(isAndroidPlatform.and(notInTrash).and(profileType).and(isPublishedProfile), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression androidTrashCount = new CaseExpression("ANDROID_TRASH_COUNT");
        androidTrashCount.addWhen(isAndroidPlatform.and(inTrash).and(profileType), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression ioscount = new CaseExpression("IOS_COUNT");
        ioscount.addWhen(isIosPlatform.and(notInTrash).and(profileType).and(isPublishedProfile), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression chromeCount = new CaseExpression("CHROME_COUNT");
        chromeCount.addWhen(isChromePlatform.and(notInTrash).and(profileType).and(isPublishedProfile), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression macCount = new CaseExpression("MACOS_COUNT");
        macCount.addWhen(isMacPlatform.and(notInTrash).and(profileType).and(isPublishedProfile), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression tvOSCount = new CaseExpression("TVOS_COUNT");
        tvOSCount.addWhen(isTvOSPlatform.and(notInTrash).and(profileType).and(isPublishedProfile), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iosTrashCount = new CaseExpression("IOS_TRASH_COUNT");
        iosTrashCount.addWhen(isIosPlatform.and(inTrash).and(profileType), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression windowscount = new CaseExpression("WINDOWS_COUNT");
        windowscount.addWhen(isWindowsPlatform.and(notInTrash).and(profileType).and(isPublishedProfile), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression windowsTrashCount = new CaseExpression("WINDOWS_TRASH_COUNT");
        windowsTrashCount.addWhen(isWindowsPlatform.and(inTrash).and(profileType), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression chromeTrashCount = new CaseExpression("CHROME_TRASH_COUNT");
        chromeTrashCount.addWhen(isChromePlatform.and(inTrash).and(profileType), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression macTrashCount = new CaseExpression("MACOS_TRASH_COUNT");
        macTrashCount.addWhen(isMacPlatform.and(inTrash).and(profileType), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression tvOSTrashCount = new CaseExpression("TVOS_TRASH_COUNT");
        tvOSTrashCount.addWhen(isTvOSPlatform.and(inTrash).and(profileType), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression chromeUserProfileCount = new CaseExpression("CHROME_USER_PROFILE_COUNT");
        chromeUserProfileCount.addWhen(isChromePlatform.and(notDeviceScope).and(profileType), (Object)new Column("Profile", "PROFILE_ID"));
        profileCountQuery.addJoin(new Join("Profile", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
        profileCountQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(androidcount));
        profileCountQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(androidTrashCount));
        profileCountQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(ioscount));
        profileCountQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(iosTrashCount));
        profileCountQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(windowscount));
        profileCountQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(windowsTrashCount));
        profileCountQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(chromeCount));
        profileCountQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(chromeTrashCount));
        profileCountQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(chromeUserProfileCount));
        profileCountQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(macCount));
        profileCountQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(macTrashCount));
        profileCountQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(tvOSCount));
        profileCountQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(tvOSTrashCount));
        return profileCountQuery;
    }
    
    private void setKioskUsageDetails() {
        try {
            final JSONObject json = new JSONObject();
            MEMDMTrackerUtil.getIOSKioskPayloadCount(json);
            JSONObject object = MEMDMTrackerUtil.getConflictingKioskPayloadErrorCount();
            json.put("IOSKioskConflictErrorCount", (Object)object.optString("errorCount"));
            json.put("iOSKioskAppNotInstalledError", (Object)object.optString("noAppErrorCount"));
            MEMDMTrackerUtil.getAndroidMultipleAppKioskPayloadCount(json);
            object = MEMDMTrackerUtil.getKioskPauseResumeCount();
            json.put("AndroidKioskPauseCommandCount", (Object)object.optString("androidKioskPauseCount"));
            json.put("AndroidKioskResumeCommandCount", (Object)object.optString("androidKioskResumeCount"));
            json.put("iOSKioskPauseCommandCount", (Object)object.optString("iOSKioskPauseCount"));
            json.put("iOSKioskResumeCommandCount", (Object)object.optString("iOSKioskResumeCount"));
            object = MEMDMTrackerUtil.getAndroidKioskLauncherCount();
            json.put("AndroidMdmLauncherCount", (Object)object.optString("mdmLauncher"));
            json.put("AndroidDeviceLauncherCount", (Object)object.optString("deviceLauncher"));
            this.mdmTrackerProperties.setProperty("KioskDetails", json.toString());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "MEMDMTrackerProfileImpl: Exception while setKioskUsageDetails() ", e);
        }
    }
    
    public void addWebPolicyCountDetails() {
        DMDataSetWrapper webContentDS = null;
        try {
            int managedDomainCount = 0;
            final JSONObject webPolicyJson = new JSONObject();
            final JSONObject iOSCount = new JSONObject();
            final JSONObject androidCount = new JSONObject();
            final SelectQuery webContentQuery = this.getWebContentQuery();
            webContentDS = DMDataSetWrapper.executeQuery((Object)webContentQuery);
            while (webContentDS.next()) {
                iOSCount.put("Whitelist", (Object)webContentDS.getValue("IOSwhitelistcount").toString());
                iOSCount.put("Blacklist", (Object)webContentDS.getValue("IOSblacklistcount").toString());
                androidCount.put("Whitelist", (Object)webContentDS.getValue("Androidwhitelistcount").toString());
                androidCount.put("Blacklist", (Object)webContentDS.getValue("Androidblacklistcount").toString());
            }
            final SelectQuery managedDomainQuery = this.getManagedDomainQuery();
            final DataObject managedDomain = DataAccess.get(managedDomainQuery);
            final SelectQuery webClipQuery = this.getWebClipQuery();
            final DataObject webClip = DataAccess.get(webClipQuery);
            final Iterator managedDomainIterator = managedDomain.getRows("ManagedWebDomainURLDetails");
            while (managedDomainIterator.hasNext()) {
                final Row managedDomainRow = managedDomainIterator.next();
                String url = null;
                url = managedDomainRow.get("URL").toString();
                if (url != null) {
                    url += "*";
                    final Criteria mdContains = new Criteria(new Column("WebClipsPolicy", "WEBCLIP_URL"), (Object)url, 2);
                    final Iterator webClipCount = webClip.getRows("WebClipsPolicy", mdContains);
                    managedDomainCount += DBUtil.getIteratorSize(webClipCount);
                }
            }
            iOSCount.put("WebclipcontainsMD", managedDomainCount);
            webPolicyJson.put("iOS", (Object)iOSCount);
            webPolicyJson.put("Android", (Object)androidCount);
            ((Hashtable<String, JSONObject>)this.mdmTrackerProperties).put("WebPolicy", webPolicyJson);
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMConfigLogger").log(Level.INFO, "Unable to track the webpolciy count details", ex.getMessage());
        }
    }
    
    private void setProfileDetailsPageVisitCount() {
        try {
            final JSONObject json = new JSONObject();
            json.put("associatedGroups", (Object)MEMDMTrackerUtil.getViewVisitCount("mdmGroupsAssociatedwithProfile").toString());
            json.put("associatedDevices", (Object)MEMDMTrackerUtil.getViewVisitCount("mdmDevicesAssociatedwithProfile").toString());
            this.mdmTrackerProperties.setProperty("profileDetailsPageCount", json.toString());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "MEMDMTrackerProfileImpl: Exception while setProfileDetailsPageVisitCount() ", ex);
        }
    }
    
    private void setProfileAddedDistributedDetails() {
        final JSONObject json = MEMDMTrackerUtil.getProfileAddedDistributedData(1);
        this.mdmTrackerProperties.setProperty("profile_Distributed_Time", json.optString("profile_Distributed_Time", "-1"));
        this.mdmTrackerProperties.setProperty("profile_Added_Time", json.optString("profile_Added_Time", "-1"));
        this.mdmTrackerProperties.setProperty("profile_Distributed_Count", json.optString("profile_Distributed_Count", "0"));
    }
    
    private void setExchangeActiveSyncPolicyUsage() {
        try {
            this.mdmTrackerProperties.setProperty("ExchangeActiveSyncDetails", MEMDMTrackerUtil.getExchangeActiveSyncDetailsJSON().toString());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "MEMDMTrackerProfileImpl: Exception while exchangeActiveSyncPolicyUsage() ", e);
        }
    }
    
    private SelectQuery getWebContentQuery() {
        final SelectQuery webContentQuery = this.getRecentProfileQuery();
        webContentQuery.addJoin(new Join("ConfigDataItem", "IOSWebContentPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        webContentQuery.addJoin(new Join("IOSWebContentPolicy", "URLRestrictionDetails", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        webContentQuery.addJoin(new Join("URLRestrictionDetails", "URLDetails", new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, 2));
        final Criteria iOSPlatform = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)1, 0);
        final Criteria androidPlatform = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)2, 0);
        final Criteria Whitelisted = new Criteria(new Column("IOSWebContentPolicy", "URL_FILTER_TYPE"), (Object)true, 0);
        final Criteria Blacklisted = new Criteria(new Column("IOSWebContentPolicy", "URL_FILTER_TYPE"), (Object)false, 0);
        final CaseExpression IOSwhitelistcount = new CaseExpression("IOSwhitelistcount");
        IOSwhitelistcount.addWhen(iOSPlatform.and(Whitelisted), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression IOSblacklistcount = new CaseExpression("IOSblacklistcount");
        IOSblacklistcount.addWhen(iOSPlatform.and(Blacklisted), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression Androidwhitelistcount = new CaseExpression("Androidwhitelistcount");
        Androidwhitelistcount.addWhen(androidPlatform.and(Whitelisted), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression Androidblacklistcount = new CaseExpression("Androidblacklistcount");
        Androidblacklistcount.addWhen(androidPlatform.and(Blacklisted), (Object)new Column("Profile", "PROFILE_ID"));
        webContentQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(IOSwhitelistcount));
        webContentQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(IOSblacklistcount));
        webContentQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(Androidwhitelistcount));
        webContentQuery.addSelectColumn(MEMDMTrackerUtil.getDistinctIntegerCountOfCaseExpression(Androidblacklistcount));
        return webContentQuery;
    }
    
    private SelectQuery getManagedDomainQuery() {
        final SelectQuery managedDomainQuery = this.getRecentProfileQuery();
        managedDomainQuery.addJoin(new Join("ConfigDataItem", "ManagedWebDomainPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        managedDomainQuery.addJoin(new Join("ManagedWebDomainPolicy", "ManagedWebDomainURLDetails", new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, 2));
        managedDomainQuery.addSelectColumn(new Column((String)null, "*"));
        return managedDomainQuery;
    }
    
    private void getContainerPasscodeCount() {
        try {
            final SelectQuery profilequery = this.getRecentProfileQuery();
            profilequery.addJoin(new Join("ConfigDataItem", "AndroidPasscodePolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
            final Criteria containerPascode = new Criteria(new Column("AndroidPasscodePolicy", "SCOPE_FOR_PASSCODE"), (Object)1, 0);
            profilequery.setCriteria(containerPascode);
            final int containerPasscodeCount = DBUtil.getRecordCount(profilequery, "Profile", "PROFILE_ID");
            this.mdmTrackerProperties.setProperty("Passcode_For_Container_Count", String.valueOf(containerPasscodeCount));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "MEMDMTrackerProfileImpl: Exception while getting container passcode count() ", e);
        }
    }
    
    private SelectQuery getWebClipQuery() {
        final SelectQuery webclipquery = this.getRecentProfileQuery();
        webclipquery.addJoin(new Join("ConfigDataItem", "WebClipsPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        webclipquery.addSelectColumn(new Column((String)null, "*"));
        return webclipquery;
    }
    
    private SelectQuery getRecentProfileQuery() {
        final SelectQuery recentprofilequery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        recentprofilequery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        recentprofilequery.addJoin(new Join("RecentProfileToColln", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        recentprofilequery.addJoin(new Join("CfgDataToCollection", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        return recentprofilequery;
    }
    
    private void setRestrictionUsage() {
        try {
            this.mdmTrackerProperties.setProperty("RestrictionDetails", MEMDMTrackerUtil.getRestrictionUsage1().toString());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "MEMDMTrackerProfileImpl: Exception while setRestrictionUsage ", e);
        }
    }
    
    private void setProfileCertificateDetails() {
        try {
            this.mdmTrackerProperties.setProperty("CertificateDetails", MEMDMTrackerUtil.getProfileCertificateDetails().toString());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "MEMDMTrackerProfileImpl: Exception while setRestrictionUsage ", e);
        }
    }
    
    private void addVPNTypes() {
        this.mdmTrackerProperties.setProperty("VpnTypes", MEMDMTrackerUtil.getVPNType().toString());
    }
    
    private void addCustomProfile() {
        this.mdmTrackerProperties.setProperty("CustomPayloads", MEMDMTrackerUtil.getCustomProfileTrackingDetails().toString());
    }
    
    private void addAppTrashData() {
        this.mdmTrackerProperties.setProperty("AppTrash", MEMDMTrackerUtil.getAppTrashData().toString());
    }
    
    private void setEFRPMailIdCount() {
        this.mdmTrackerProperties.setProperty("EFRP_MAIL_ID_COUNT", String.valueOf(MEMDMTrackerUtil.getERFPIdCount()));
    }
    
    private void addADMXBackedConfigDetails() {
        final String[] admxGPNames = new String[0];
        final String[] admxConfigDataIds = { "OSActiveDirectoryBackup_Name", "FDVActiveDirectoryBackup_Name" };
        this.mdmTrackerProperties.setProperty("ADMXBackedPolicy", MEMDMTrackerUtil.getADMXConfigCount(admxGPNames).toString());
        this.mdmTrackerProperties.setProperty("ADMXBackedPolicyData", MEMDMTrackerUtil.getADMXConfigDataCount(admxConfigDataIds).toString());
    }
    
    private void addAppConfigurationProfileCount() {
        try {
            final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("APP_CONFIG_POLICY_QUERY");
            this.mdmTrackerProperties.setProperty("APP_CONFIG_PROFILE", MEMDMTrackerUtil.getAppConfigPolicyCount(selectQuery));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in adding app configuration profile count", ex);
        }
    }
    
    private void addAppNotificationsPolicyUsage() {
        final JSONObject jsonObject = new JSONObject();
        try {
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)this.getAppNotificationsQuery());
            while (dataSetWrapper.next()) {
                jsonObject.put("IOSAppNotificationsIsEnabled", (Object)dataSetWrapper.getValue("IOSAppNotificationsIsEnabled").toString());
                jsonObject.put("IOSAppNotificationsIsDisabled", (Object)dataSetWrapper.getValue("IOSAppNotificationsIsDisabled").toString());
                jsonObject.put("IOSAppNotificationsIsPreviewHidden", (Object)dataSetWrapper.getValue("IOSAppNotificationsIsPreviewHidden").toString());
                jsonObject.put("IOSAppNotificationsIsAlertEnabled", (Object)dataSetWrapper.getValue("IOSAppNotificationsIsAlertEnabled").toString());
                jsonObject.put("MacOSAppNotificationsIsEnabled", (Object)dataSetWrapper.getValue("MacOSAppNotificationsIsEnabled").toString());
                jsonObject.put("MacOSAppNotificationsIsDisabled", (Object)dataSetWrapper.getValue("MacOSAppNotificationsIsDisabled").toString());
                jsonObject.put("MacOSAppNotificationsIsAlertEnabled", (Object)dataSetWrapper.getValue("MacOSAppNotificationsIsAlertEnabled").toString());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "MEMDMTrackerProfileImpl: Exception while addAppNotificationsPolicyUsage() ", e);
        }
        this.mdmTrackerProperties.setProperty("AppNotificationsDetails", jsonObject.toString());
    }
    
    private SelectQuery getAppNotificationsQuery() {
        final Criteria iOSPlatformCriteria = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)1, 0);
        final Criteria macOSPlatformCriteria = new Criteria(new Column("Profile", "PLATFORM_TYPE"), (Object)6, 0);
        final Criteria iOSAppNotificationsPolicyCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)528, 0);
        final Criteria macOSAppNotificationPolicyCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)775, 0);
        final CaseExpression iOSAppNotificationsEnabledCount = new CaseExpression("IOSAppNotificationsEnabled");
        final Criteria appNotificationsEnabledCriteria = new Criteria(Column.getColumn("MdmAppNotificationPolicy", "NOTIFICATIONS_ENABLED"), (Object)true, 0);
        iOSAppNotificationsEnabledCount.addWhen(iOSPlatformCriteria.and(iOSAppNotificationsPolicyCriteria).and(appNotificationsEnabledCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iOSAppNotificationsDisabledCount = new CaseExpression("IOSAppNotificationsDisabled");
        final Criteria appNotificationsDisabledCriteria = new Criteria(Column.getColumn("MdmAppNotificationPolicy", "NOTIFICATIONS_ENABLED"), (Object)false, 0);
        iOSAppNotificationsDisabledCount.addWhen(iOSPlatformCriteria.and(iOSAppNotificationsPolicyCriteria).and(appNotificationsDisabledCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iOSAppNotificationsPreviewTypeWhenUnlocked = new CaseExpression("IOSAppNotificationsPreviewTypeWhenUnlocked");
        final Criteria appNotificationsPreviewWhenUnlockedCriteria = new Criteria(Column.getColumn("MdmAppNotificationPolicy", "PREVIEW_TYPE"), (Object)1, 0);
        iOSAppNotificationsPreviewTypeWhenUnlocked.addWhen(iOSPlatformCriteria.and(iOSAppNotificationsPolicyCriteria).and(appNotificationsEnabledCriteria).and(appNotificationsPreviewWhenUnlockedCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iOSAppNotificationsPreviewTypeNever = new CaseExpression("IOSAppNotificationsPreviewTypeNever");
        final Criteria appNotificationsPreviewNeverCriteria = new Criteria(Column.getColumn("MdmAppNotificationPolicy", "PREVIEW_TYPE"), (Object)2, 0);
        iOSAppNotificationsPreviewTypeNever.addWhen(iOSPlatformCriteria.and(iOSAppNotificationsPolicyCriteria).and(appNotificationsEnabledCriteria).and(appNotificationsPreviewNeverCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iOSAppNotificationsTemporaryBanner = new CaseExpression("IOSAppNotificationsTemporaryBanner");
        final Criteria appNotificationsTemporaryBannerCriteria = new Criteria(Column.getColumn("MdmAppNotificationPolicy", "ALERT_TYPE"), (Object)1, 0);
        iOSAppNotificationsTemporaryBanner.addWhen(iOSPlatformCriteria.and(iOSAppNotificationsPolicyCriteria).and(appNotificationsEnabledCriteria).and(appNotificationsTemporaryBannerCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression iOSAppNotificationsPermanentBanner = new CaseExpression("IOSAppNotificationsPermanentBanner");
        final Criteria appNotificationsPermanentBannerCriteria = new Criteria(Column.getColumn("MdmAppNotificationPolicy", "ALERT_TYPE"), (Object)2, 0);
        iOSAppNotificationsPermanentBanner.addWhen(iOSPlatformCriteria.and(iOSAppNotificationsPolicyCriteria).and(appNotificationsEnabledCriteria).and(appNotificationsPermanentBannerCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression macOSAppNotificationsEnabled = new CaseExpression("MacOSAppNotificationsEnabled");
        macOSAppNotificationsEnabled.addWhen(macOSPlatformCriteria.and(macOSAppNotificationPolicyCriteria).and(appNotificationsEnabledCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression macOSAppNotificationsDisabled = new CaseExpression("MacOSAppNotificationsDisabled");
        macOSAppNotificationsDisabled.addWhen(macOSPlatformCriteria.and(macOSAppNotificationPolicyCriteria).and(appNotificationsDisabledCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression macOSAppNotificationsTemporaryAlert = new CaseExpression("MacOSAppNotificationsTemporaryBanner");
        macOSAppNotificationsTemporaryAlert.addWhen(macOSPlatformCriteria.and(macOSAppNotificationPolicyCriteria).and(appNotificationsEnabledCriteria).and(appNotificationsTemporaryBannerCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final CaseExpression macOSAppNotificationsPermanentAlert = new CaseExpression("MacOSAppNotificationsPermanentBanner");
        macOSAppNotificationsPermanentAlert.addWhen(macOSPlatformCriteria.and(macOSAppNotificationPolicyCriteria).and(appNotificationsEnabledCriteria).and(appNotificationsPermanentBannerCriteria), (Object)new Column("Profile", "PROFILE_ID"));
        final SelectQuery configurationQuery = this.getRecentProfileQuery();
        final MDMTrackerUtil trackerUtil = new MDMTrackerUtil();
        configurationQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        configurationQuery.addJoin(new Join("ConfigDataItem", "MdmAppNotificationPolicyToConfigRel", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        configurationQuery.addJoin(new Join("MdmAppNotificationPolicyToConfigRel", "MdmAppNotificationPolicy", new String[] { "MDM_APP_NOTIFICATION_POLICY_ID" }, new String[] { "MDM_APP_NOTIFICATION_POLICY_ID" }, 1));
        final Column appNotificationsEnabledCount = trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iOSAppNotificationsEnabledCount);
        final CaseExpression isiOSAppNotificationsEnabled = new CaseExpression("IOSAppNotificationsIsEnabled");
        isiOSAppNotificationsEnabled.addWhen(new Criteria(appNotificationsEnabledCount, (Object)0, 5), (Object)true);
        isiOSAppNotificationsEnabled.elseVal((Object)false);
        final Column appNotificationsDisabledCount = trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iOSAppNotificationsDisabledCount);
        final CaseExpression isiOSAppNotificationsDisabled = new CaseExpression("IOSAppNotificationsIsDisabled");
        isiOSAppNotificationsDisabled.addWhen(new Criteria(appNotificationsDisabledCount, (Object)0, 5), (Object)true);
        isiOSAppNotificationsDisabled.elseVal((Object)false);
        final Column appNotificationsPreviewTypeNeverCount = trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iOSAppNotificationsPreviewTypeNever);
        final Column appNotificationsPreviewTypeWhenUnlockedCount = trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iOSAppNotificationsPreviewTypeWhenUnlocked);
        final Criteria isAppNotificationsPreviewTypeNeverCountGreaterThanZero = new Criteria(appNotificationsPreviewTypeNeverCount, (Object)0, 5);
        final Criteria isAppNotificationsPreviewTypeWhenUnlockedCountGreaterThanZero = new Criteria(appNotificationsPreviewTypeWhenUnlockedCount, (Object)0, 5);
        final CaseExpression isiOSAppNotificationsPreviewHidden = new CaseExpression("IOSAppNotificationsIsPreviewHidden");
        isiOSAppNotificationsPreviewHidden.addWhen(isAppNotificationsPreviewTypeNeverCountGreaterThanZero.or(isAppNotificationsPreviewTypeWhenUnlockedCountGreaterThanZero), (Object)true);
        isiOSAppNotificationsPreviewHidden.elseVal((Object)false);
        final Column appNotificationsTemporaryBannerCount = trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iOSAppNotificationsTemporaryBanner);
        final Column appNotificationsPermanentBannerCount = trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)iOSAppNotificationsPermanentBanner);
        final Criteria isAppNotificationsTemporaryBannerCountGreaterThanZero = new Criteria(appNotificationsTemporaryBannerCount, (Object)0, 5);
        final Criteria isAppNotificationsPermanentBannerCountGreaterThanZero = new Criteria(appNotificationsPermanentBannerCount, (Object)0, 5);
        final CaseExpression isiOSAppNotificationsAlertsEnabled = new CaseExpression("IOSAppNotificationsIsAlertEnabled");
        isiOSAppNotificationsAlertsEnabled.addWhen(isAppNotificationsTemporaryBannerCountGreaterThanZero.or(isAppNotificationsPermanentBannerCountGreaterThanZero), (Object)true);
        isiOSAppNotificationsAlertsEnabled.elseVal((Object)false);
        final Column macOSAppNotificationsEnabledCount = trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)macOSAppNotificationsEnabled);
        final CaseExpression isMacOSAppNotificationsEnabled = new CaseExpression("MacOSAppNotificationsIsEnabled");
        isMacOSAppNotificationsEnabled.addWhen(new Criteria(macOSAppNotificationsEnabledCount, (Object)0, 5), (Object)true);
        isMacOSAppNotificationsEnabled.elseVal((Object)false);
        final Column macOSAppNotificationsDisabledCount = trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)macOSAppNotificationsDisabled);
        final CaseExpression isMacOSAppNotificationsDisabled = new CaseExpression("MacOSAppNotificationsIsDisabled");
        isMacOSAppNotificationsDisabled.addWhen(new Criteria(macOSAppNotificationsDisabledCount, (Object)0, 5), (Object)true);
        isMacOSAppNotificationsDisabled.elseVal((Object)false);
        final Column macOSAppNotificationsTemporaryBannerCount = trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)macOSAppNotificationsTemporaryAlert);
        final Column macOSAppNotificationsPermanentBannerCount = trackerUtil.getDistinctIntegerCountCaseExpressionColumn((Column)macOSAppNotificationsPermanentAlert);
        final Criteria isMacOSAppNotificationsTemporaryBannerCountGreaterThanZero = new Criteria(macOSAppNotificationsTemporaryBannerCount, (Object)0, 5);
        final Criteria isMacOSAppNotificationsPermanentBannerCountGreaterThanZero = new Criteria(macOSAppNotificationsPermanentBannerCount, (Object)0, 5);
        final CaseExpression isMacOSAppNotificationsAlertsEnabled = new CaseExpression("MacOSAppNotificationsIsAlertEnabled");
        isMacOSAppNotificationsAlertsEnabled.addWhen(isMacOSAppNotificationsTemporaryBannerCountGreaterThanZero.or(isMacOSAppNotificationsPermanentBannerCountGreaterThanZero), (Object)true);
        isMacOSAppNotificationsAlertsEnabled.elseVal((Object)false);
        configurationQuery.addSelectColumn((Column)isiOSAppNotificationsEnabled);
        configurationQuery.addSelectColumn((Column)isiOSAppNotificationsDisabled);
        configurationQuery.addSelectColumn((Column)isiOSAppNotificationsPreviewHidden);
        configurationQuery.addSelectColumn((Column)isiOSAppNotificationsAlertsEnabled);
        configurationQuery.addSelectColumn((Column)isMacOSAppNotificationsEnabled);
        configurationQuery.addSelectColumn((Column)isMacOSAppNotificationsDisabled);
        configurationQuery.addSelectColumn((Column)isMacOSAppNotificationsAlertsEnabled);
        return configurationQuery;
    }
}
