package com.me.mdm.server.metracker;

import java.util.Hashtable;
import java.util.Map;
import com.adventnet.sym.server.mdm.certificates.scepserver.ScepServerUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.adventnet.ds.query.QueryConstructionException;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.apps.BlacklistWhitelistAppHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackerAppsImpl extends MEMDMTrackerConstants
{
    private Properties mdmTrackerProperties;
    private Logger logger;
    private String sourceClass;
    private int androidPlayStoreApp;
    private int androidEnterpriseApp;
    public static final String BSTORE_SUBSCRIPTION_USER = "bstoreSubscriptionUser";
    
    public MEMDMTrackerAppsImpl() {
        this.mdmTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCTrackerMDMPAppsImpl";
        this.androidPlayStoreApp = 0;
        this.androidEnterpriseApp = 2;
    }
    
    public Properties getTrackerProperties() {
        try {
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDMP Apps implementation starts...");
            if (!this.mdmTrackerProperties.isEmpty()) {
                this.mdmTrackerProperties = new Properties();
            }
            this.addAndroidAppRepositoryCount();
            this.addAppControllDetails();
            this.addBlackListAppCount();
            this.addBlaclistActionForBYOD();
            this.addBlaclistActionForCorporate();
            this.addIOSEnterpriseAppCount();
            this.addTotalAppRepositoryCount();
            this.addIOSPaidAppCount();
            this.addIosAppGroupCount();
            this.addWhiteListAppCount();
            this.addWindowsAETFileUpload();
            this.addWindowsCertFileUpload();
            this.addIosAppRepositoryCount();
            this.addWindowsAppRepositoryCount();
            this.addRepositoryAppCount(2, this.androidPlayStoreApp, "Android_Apps_Playstore_Count");
            this.addRepositoryAppCount(2, this.androidEnterpriseApp, "Android_Apps_Enterprise_Count");
            this.addRepositoryAppCountInJSON(4, this.androidPlayStoreApp, "Chrome_Store_App_Count");
            this.addRepositoryAppCountInJSON(4, this.androidEnterpriseApp, "Chrome_Custom_App_Count");
            this.addVppDetails();
            this.addAppViewSettings();
            this.addManagedAppConfigData();
            this.setAppAddedDistributedDetails();
            this.addBusinessStoreDetails();
            this.addAppsCountForBusinessStore();
            this.setAppDistributionDetails();
            this.setBlacklistTrackingParams();
            this.addAppCustomerParams();
            this.addAppFileSizeData();
            this.addMultiVersionBetaAppDetails();
            this.addAutoAppUpdateDetails();
            this.addCertRepoChanges();
            this.addCertRepoDigiDetails();
            this.addAndroidPrivateAppsCount();
            this.addMultiTrackPlayStoreApps();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary : " + this.mdmTrackerProperties);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "MDMTrackerProperties", "Exception : ", (Throwable)e);
        }
        return this.mdmTrackerProperties;
    }
    
    private void addRepositoryAppCount(final int platform, final int appType, final String typeKey) {
        try {
            final AppsUtil appsUtil = new AppsUtil();
            this.mdmTrackerProperties.setProperty(typeKey, String.valueOf(appsUtil.getAppRepositoryAppCount(platform, appType)));
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addAndroidAppCount " + typeKey, "Exception : ", (Throwable)ex);
        }
    }
    
    private void addRepositoryAppCountInJSON(final int platform, final int appType, final String typeKey) {
        try {
            final AppsUtil appsUtil = new AppsUtil();
            final JSONObject appRepoCount = new JSONObject();
            appRepoCount.put(typeKey, (Object)String.valueOf(appsUtil.getAppRepositoryAppCount(platform, appType)));
            this.mdmTrackerProperties.setProperty("App_Repo_Count", appRepoCount.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addRepositoryAppCountInJSON " + typeKey, "Exception : ", (Throwable)ex);
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
    
    private void addIosAppGroupCount() {
        try {
            final int appCount = AppsUtil.getInstance().getAppGroupCount(1);
            this.mdmTrackerProperties.setProperty("iOS_App_Group_Count", String.valueOf(appCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addIosAppCount", "Exception : ", (Throwable)e);
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
    
    private void addTotalAppRepositoryCount() {
        try {
            final int appCount = AppsUtil.getInstance().getAppRepositoryAppCount(null);
            this.mdmTrackerProperties.setProperty("Total_App_Repository_Count", String.valueOf(appCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addTotalAppRepositoryCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addWindowsAppRepositoryCount() {
        try {
            final int appCount = AppsUtil.getInstance().getAppRepositoryAppCount(3);
            final int msiAppsCount = AppsUtil.getInstance().getMSIAppsCount();
            this.mdmTrackerProperties.setProperty("Windows_App_Repository_Count", String.valueOf(appCount));
            this.mdmTrackerProperties.setProperty("Windows_MsiApp_Repository_Count", String.valueOf(msiAppsCount));
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addWindowsAppCount", "Exception : ", (Throwable)e);
        }
    }
    
    private void addWindowsAETFileUpload() {
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            final boolean isAET = WpAppSettingsHandler.getInstance().isAETUploaded(customerID);
            this.mdmTrackerProperties.setProperty("Is_AET_File_Upload", String.valueOf(isAET));
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
            this.mdmTrackerProperties.setProperty("Is_CSC_File_Upload", String.valueOf(isCert));
        }
    }
    
    private void addVppDetails() {
        final JSONObject vppDetails = MEMDMTrackerUtil.getVppDetails();
        this.mdmTrackerProperties.setProperty("vppDetails", vppDetails.toString());
    }
    
    private void addAppViewSettings() {
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        try {
            if (!isMsp) {
                final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
                this.mdmTrackerProperties.setProperty("AppViewSettings", AppSettingsDataHandler.getInstance().getAppViewSettings(customerID).toString());
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addAppViewSettings", "Exception : ", (Throwable)ex);
        }
    }
    
    private void addManagedAppConfigData() {
        try {
            final JSONObject appConfigData = MEMDMTrackerUtil.getManagedAppConfigurationAppsCount();
            this.mdmTrackerProperties.setProperty("managedConfigAppCount", appConfigData.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addAppConfigData", "Exception : ", (Throwable)ex);
        }
    }
    
    private void setAppAddedDistributedDetails() {
        final JSONObject json = MEMDMTrackerUtil.getProfileAddedDistributedData(2);
        this.mdmTrackerProperties.setProperty("app_Distributed_Time", json.optString("profile_Distributed_Time", "-1"));
        this.mdmTrackerProperties.setProperty("app_Added_Time", json.optString("profile_Added_Time", "-1"));
        this.mdmTrackerProperties.setProperty("app_Distributed_Count", json.optString("profile_Distributed_Count", "0"));
    }
    
    private void setAppDistributionDetails() {
        Connection conn = null;
        DataSet ds = null;
        SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        sQuery = MDMCoreQuery.getInstance().addCollnDistributionQuery(sQuery);
        try {
            conn = RelationalAPI.getInstance().getConnection();
            ds = RelationalAPI.getInstance().executeQuery((Query)sQuery, conn);
            Integer failureCount = 0;
            while (ds.next()) {
                failureCount += (int)ds.getValue("ANDROID_APP_DISTR_FAILED_COUNT");
            }
            this.mdmTrackerProperties.setProperty("ANDROID_APP_DISTR_FAILED_COUNT", failureCount.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "setAppDistributionDetails", "Exception : ", (Throwable)ex);
            if (ds != null) {
                try {
                    ds.close();
                }
                catch (final SQLException ex2) {
                    this.logger.log(Level.SEVERE, "Exception occurred during closing dataset object for setAppDistributionDetails ", ex2);
                }
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e) {
                SyMLogger.error(this.logger, this.sourceClass, "setAppDistributionDetails", "conn close Exception : ", (Throwable)e);
            }
        }
        finally {
            if (ds != null) {
                try {
                    ds.close();
                }
                catch (final SQLException ex3) {
                    this.logger.log(Level.SEVERE, "Exception occurred during closing dataset object for setAppDistributionDetails ", ex3);
                }
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e2) {
                SyMLogger.error(this.logger, this.sourceClass, "setAppDistributionDetails", "conn close Exception : ", (Throwable)e2);
            }
        }
    }
    
    private void setBlacklistTrackingParams() {
        Connection conn = null;
        DataSet ds = null;
        final SelectQuery sQuery = MDMCoreQuery.getInstance().getMDMQueryMap("BLACKLIST_QUERY");
        try {
            conn = RelationalAPI.getInstance().getConnection();
            ds = RelationalAPI.getInstance().executeQuery((Query)sQuery, conn);
            Integer blacklitedCount = 0;
            Integer blacklitedResCount = 0;
            Integer blacklitedGrpCount = 0;
            Integer blacklitedNetCount = 0;
            Integer blacklitedIOSCount = 0;
            Integer blacklitedAndroidCount = 0;
            Integer blacklitedWindowsCount = 0;
            Integer blacklistIOSSystemCount = 0;
            Integer blacklistWindowsSystemCount = 0;
            while (ds.next()) {
                blacklitedCount = (Integer)ds.getValue("BLACKLISTED_COUNT_NEW");
                blacklitedResCount = (Integer)ds.getValue("BLACKLISTED_APP_RESOURCE_COUNT");
                blacklitedGrpCount = (Integer)ds.getValue("BLACKLISTED_APP_GROUP_COUNT");
                blacklitedNetCount = (Integer)ds.getValue("BLACKLISTED_APP_NETWORK_COUNT");
                blacklitedIOSCount = (Integer)ds.getValue("BLACKLISTED_IOS_COUNT");
                blacklitedAndroidCount = (Integer)ds.getValue("BLACKLISTED_ANDROID_COUNT");
                blacklitedWindowsCount = (Integer)ds.getValue("BLACKLISTED_WINDOWS_COUNT");
                blacklistIOSSystemCount = (Integer)ds.getValue("BLACKLISTED_IOS_SYSTEM_APP_COUNT");
                blacklistWindowsSystemCount = (Integer)ds.getValue("BLACKLISTED_WINDOWS_SYSTEM_APP_COUNT");
            }
            this.mdmTrackerProperties.setProperty("BLACKLISTED_COUNT_NEW", blacklitedCount.toString());
            this.mdmTrackerProperties.setProperty("BLACKLISTED_APP_RESOURCE_COUNT", blacklitedResCount.toString());
            this.mdmTrackerProperties.setProperty("BLACKLISTED_APP_GROUP_COUNT", blacklitedGrpCount.toString());
            this.mdmTrackerProperties.setProperty("BLACKLISTED_APP_NETWORK_COUNT", blacklitedNetCount.toString());
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("BLACKLISTED_IOS_COUNT", (Object)blacklitedIOSCount.toString());
            jsonObject.put("BLACKLISTED_ANDROID_COUNT", (Object)blacklitedAndroidCount.toString());
            jsonObject.put("BLACKLISTED_WINDOWS_COUNT", (Object)blacklitedWindowsCount.toString());
            jsonObject.put("BLACKLISTED_IOS_SYSTEM_APP_COUNT", (Object)blacklistIOSSystemCount.toString());
            jsonObject.put("BLACKLISTED_WINDOWS_SYSTEM_APP_COUNT", (Object)blacklistWindowsSystemCount.toString());
            ((Hashtable<String, JSONObject>)this.mdmTrackerProperties).put("BlacklistPlatformData", jsonObject);
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "setAppDistributionDetails", "Exception : ", (Throwable)ex);
            if (ds != null) {
                try {
                    ds.close();
                }
                catch (final SQLException ex2) {
                    this.logger.log(Level.SEVERE, "Exception occurred during closing dataset object for setAppDistributionDetails ", ex2);
                }
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e) {
                SyMLogger.error(this.logger, this.sourceClass, "setAppDistributionDetails", "conn close Exception : ", (Throwable)e);
            }
        }
        finally {
            if (ds != null) {
                try {
                    ds.close();
                }
                catch (final SQLException ex3) {
                    this.logger.log(Level.SEVERE, "Exception occurred during closing dataset object for setAppDistributionDetails ", ex3);
                }
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception e2) {
                SyMLogger.error(this.logger, this.sourceClass, "setAppDistributionDetails", "conn close Exception : ", (Throwable)e2);
            }
        }
    }
    
    private void addAppCustomerParams() throws DataAccessException, JSONException {
        final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
        if (!isMsp) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("CUSTOMER_PARAM_QUERY");
            selectQuery.addSelectColumn(Column.getColumn("CustomerParams", "*"));
            final Criteria customerCriteria = new Criteria(Column.getColumn("CustomerParams", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria defaultCriteria = selectQuery.getCriteria();
            selectQuery.setCriteria(customerCriteria.and(defaultCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("CustomerParams");
            final JSONObject trashProperties = new JSONObject();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String paramName = (String)row.get("PARAM_NAME");
                if (paramName.equals("APP_RESTORE_COUNT")) {
                    trashProperties.put("App_Restore_Count", (Object)row.get("PARAM_VALUE"));
                }
                else if (paramName.equals("APP_ADDED_AGAIN_COUNT")) {
                    trashProperties.put("App_Added_Again_Count", (Object)row.get("PARAM_VALUE"));
                }
                else if (paramName.equals("DELETE_PERMANENT_COUNT")) {
                    trashProperties.put("App_deleted_permanently_Count", (Object)row.get("PARAM_VALUE"));
                }
                else if (paramName.equals("bstoreSubscriptionUser")) {
                    trashProperties.put("bstoreSubscriptionUser", (Object)row.get("PARAM_VALUE"));
                }
                else {
                    if (!paramName.equals("Signature_mismatch_Count")) {
                        continue;
                    }
                    trashProperties.put("Signature_mismatch_Count", (Object)row.get("PARAM_VALUE"));
                }
            }
            this.mdmTrackerProperties.setProperty("App_Trash_Counts", trashProperties.toString());
            this.mdmTrackerProperties.setProperty("App_Customer_Params", trashProperties.toString());
        }
    }
    
    private void addBusinessStoreDetails() {
        Connection conn = null;
        final SelectQuery remoteQuery = MDMCoreQuery.getInstance().getMDMQueryMap("WP_SETTINGS_QUERY");
        final RelationalAPI relapi = RelationalAPI.getInstance();
        DataSet ds = null;
        try {
            conn = relapi.getConnection();
            final String sql = relapi.getSelectSQL((Query)remoteQuery);
            SyMLogger.info(this.logger, sql, "getProperties", "MDM Remote Mgmt impl starts");
            ds = relapi.executeQuery((Query)remoteQuery, conn);
            while (ds.next()) {
                ((Hashtable<String, Object>)this.mdmTrackerProperties).put("Business_Store_Configured", ds.getValue("BSTORE_CONFIGURED"));
            }
        }
        catch (final SQLException e) {
            this.logger.log(Level.WARNING, "Error while fetching Business store params", e);
        }
        catch (final QueryConstructionException e2) {
            this.logger.log(Level.WARNING, "Error while fetching Business store params", (Throwable)e2);
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(conn, ds);
        }
    }
    
    private void addAppsCountForBusinessStore() {
        Connection conn = null;
        DataSet ds = null;
        final SelectQuery remoteQuery = MDMCoreQuery.getInstance().getMDMQueryMap("APPS_QUERY");
        final RelationalAPI relapi = RelationalAPI.getInstance();
        try {
            conn = relapi.getConnection();
            final String sql = relapi.getSelectSQL((Query)remoteQuery);
            SyMLogger.info(this.logger, sql, "getProperties", "MDM Remote Mgmt impl starts");
            ds = relapi.executeQuery((Query)remoteQuery, conn);
            while (ds.next()) {
                ((Hashtable<String, Object>)this.mdmTrackerProperties).put("WindowsOfflineBstoreApp", ds.getValue("WINDOWS_BSTORE_OFFLINE_APP"));
                ((Hashtable<String, Object>)this.mdmTrackerProperties).put("WindowsOnlineBstoreApp", ds.getValue("WINDOWS_BSTORE_ONLINE_APP"));
                ((Hashtable<String, Object>)this.mdmTrackerProperties).put("Windows_MsiApp_Repository_Count", ds.getValue("WINDOWS_MSI_APPLICATION"));
                ((Hashtable<String, Object>)this.mdmTrackerProperties).put("WindowsMsixAppRepoCount", ds.getValue("WINDOWS_MSIX_APPLICATION"));
            }
        }
        catch (final SQLException e) {
            this.logger.log(Level.WARNING, "Error while fetching Business store params", e);
        }
        catch (final QueryConstructionException e2) {
            this.logger.log(Level.WARNING, "Error while fetching Business store params", (Throwable)e2);
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final SQLException e3) {
                this.logger.log(Level.WARNING, "Error while fetching Business store params", e3);
            }
        }
    }
    
    private void addAppFileSizeData() {
        Connection conn = null;
        DataSet ds = null;
        try {
            final SelectQuery appsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppDetails"));
            appsQuery.addJoin(new Join("MdAppDetails", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
            MDMCoreQuery.getInstance().addPackgeTbls(appsQuery);
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)appsQuery, conn);
            final JSONObject json = new JSONObject();
            while (ds.next()) {
                json.put("IOS_MAX_APP_SIZE", ds.getValue("IOS_MAX_APP_SIZE"));
                json.put("IOS_AVG_APP_SIZE", ds.getValue("IOS_AVG_APP_SIZE"));
                json.put("IOS_SD_APP_SIZE", ds.getValue("IOS_SD_APP_SIZE"));
                json.put("IOS_COUNT_VALID_APP_SIZE", ds.getValue("IOS_COUNT_VALID_APP_SIZE"));
                json.put("ANDROID_MAX_APP_SIZE", ds.getValue("ANDROID_MAX_APP_SIZE"));
                json.put("ANDROID_AVG_APP_SIZE", ds.getValue("ANDROID_AVG_APP_SIZE"));
                json.put("ANDROID_SD_APP_SIZE", ds.getValue("ANDROID_SD_APP_SIZE"));
                json.put("ANDROID_COUNT_VALID_APP_SIZE", ds.getValue("ANDROID_COUNT_VALID_APP_SIZE"));
                json.put("WIN_MAX_APP_SIZE", ds.getValue("WIN_MAX_APP_SIZE"));
                json.put("WIN_AVG_APP_SIZE", ds.getValue("WIN_AVG_APP_SIZE"));
                json.put("WIN_SD_APP_SIZE", ds.getValue("WIN_SD_APP_SIZE"));
                json.put("WIN_COUNT_VALID_APP_SIZE", ds.getValue("WIN_COUNT_VALID_APP_SIZE"));
            }
            ((Hashtable<String, JSONObject>)this.mdmTrackerProperties).put("APP_SIZE_DETAILS", json);
        }
        catch (final SQLException e) {
            this.logger.log(Level.SEVERE, "SQL Exception in addAppFileSizeData", e);
        }
        catch (final QueryConstructionException e2) {
            this.logger.log(Level.SEVERE, "QueryConstructionException in addAppFileSizeData", (Throwable)e2);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in addAppFileSizeData", ex);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (final SQLException e3) {
                    this.logger.log(Level.SEVERE, "SQLException while closing connection in addAppFileSizeData", e3);
                }
            }
            if (ds != null) {
                try {
                    ds.close();
                }
                catch (final SQLException e3) {
                    this.logger.log(Level.SEVERE, "SQLException while closing dataset in addAppFileSizeData", e3);
                }
            }
        }
    }
    
    private void addMultiVersionBetaAppDetails() throws Exception {
        final SelectQuery mvaQuery = MDMCoreQuery.getInstance().getMDMQueryMap("APP_REPO_AND_DIST_QUERY");
        final String sql = RelationalAPI.getInstance().getSelectSQL((Query)mvaQuery);
        this.logger.log(Level.INFO, "Query for Mva details-{0}", sql);
        final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)mvaQuery);
        final JSONObject betaDetailsJson = new JSONObject();
        if (ds.next()) {
            betaDetailsJson.put("PUBLISHED_BETA_APP_COUNT", ds.getValue("PUBLISHED_BETA_APP_COUNT"));
            betaDetailsJson.put("ASSOCIATED_BETA_APP_COUNT", ds.getValue("ASSOCIATED_BETA_APP_COUNT"));
            betaDetailsJson.put("CREATED_ANDROID_BETA_APP_REPOSITORY_COUNT", ds.getValue("CREATED_ANDROID_BETA_APP_REPOSITORY_COUNT"));
            betaDetailsJson.put("CREATED_WINDOWS_BETA_APP_REPOSITORY_COUNT", ds.getValue("CREATED_WINDOWS_BETA_APP_REPOSITORY_COUNT"));
            betaDetailsJson.put("CREATED_CHROME_BETA_APP_REPOSITORY_COUNT", ds.getValue("CREATED_CHROME_BETA_APP_REPOSITORY_COUNT"));
            betaDetailsJson.put("CREATED_IOS_BETA_APP_REPOSITORY_COUNT", ds.getValue("CREATED_IOS_BETA_APP_REPOSITORY_COUNT"));
            betaDetailsJson.put("ASSOCIATED_ANDROID_BETA_APP_REPOSITORY_COUNT", ds.getValue("ASSOCIATED_ANDROID_BETA_APP_REPOSITORY_COUNT"));
            betaDetailsJson.put("ASSOCIATED_WINDOWS_BETA_APP_REPOSITORY_COUNT", ds.getValue("ASSOCIATED_WINDOWS_BETA_APP_REPOSITORY_COUNT"));
            betaDetailsJson.put("ASSOCIATED_CHROME_BETA_APP_REPOSITORY_COUNT", ds.getValue("ASSOCIATED_CHROME_BETA_APP_REPOSITORY_COUNT"));
            betaDetailsJson.put("ASSOCIATED_IOS_BETA_APP_REPOSITORY_COUNT", ds.getValue("ASSOCIATED_IOS_BETA_APP_REPOSITORY_COUNT"));
        }
        ((Hashtable<String, JSONObject>)this.mdmTrackerProperties).put("BETA_APP_DETAILS", betaDetailsJson);
    }
    
    private void addAutoAppUpdateDetails() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("APPS_UPDATED", this.addAutoUpdatedAppDetails());
            jsonObject.put("DEVICE_UPDATED", this.addAutoUpdatedDeviceDetails());
            jsonObject.put("AUTO_UPDATE_CONFIGURED", (Object)this.addAutoUpdateDetails());
            ((Hashtable<String, JSONObject>)this.mdmTrackerProperties).put("AUTO_APP_UPDATE_DETAILS", jsonObject);
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in adding auto app update details");
        }
    }
    
    private int addAutoUpdatedAppDetails() {
        final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("AUTO_APP_UPDATE_SUMMARY_QUERY");
        try {
            final DMDataSetWrapper dmSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dmSet.next()) {
                return (int)dmSet.getValue("TOTAL_COUNT");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting automatically updated apps count", e);
        }
        return 0;
    }
    
    private int addAutoUpdatedDeviceDetails() {
        final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("AUTO_APP_DEVICE_UPDATE_SUMMARY_QUERY");
        try {
            final DMDataSetWrapper dmSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dmSet.next()) {
                return (int)dmSet.getValue("TOTAL_COUNT");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting automatically updated devices count", e);
        }
        return 0;
    }
    
    private Boolean addAutoUpdateDetails() {
        final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("AUTO_APP_CONFIGURED_QUERY");
        try {
            final DMDataSetWrapper dmSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dmSet.next()) {
                return Boolean.valueOf(dmSet.getValue("AUTO_UPDATE_CONFIGURED").toString());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting if auto update is configured", e);
        }
        return Boolean.FALSE;
    }
    
    private JSONObject addCertRepoChanges() {
        final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("CERTIFCATE_REPO");
        final JSONObject jsonObject = new JSONObject();
        try {
            final DMDataSetWrapper dmSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dmSet.next()) {
                jsonObject.put("CERTIFICATE_COUNT", dmSet.getValue("CERTIFICATE_COUNT"));
                jsonObject.put("SCEP_SERVER_COUNT", dmSet.getValue("SCEP_SERVER_COUNT"));
                jsonObject.put("SCEP_TRASH_COUNT", dmSet.getValue("SCEP_TRASH_COUNT"));
                jsonObject.put("CERTIFICATE_TRASH_COUNT", dmSet.getValue("CERTIFICATE_TRASH_COUNT"));
                jsonObject.put("SCEP_COUNT", dmSet.getValue("SCEP_COUNT"));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting if auto update is configured", e);
        }
        ((Hashtable<String, JSONObject>)this.mdmTrackerProperties).put("CERTIFICATE_REPO", jsonObject);
        return jsonObject;
    }
    
    private void addCertRepoDigiDetails() {
        final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("CERTIFCATE_REPO_INDIVIDUAL");
        final Map<Integer, String> scepServers = ScepServerUtil.getScepServersAndTypes();
        final JSONObject certRepoDetails = new JSONObject();
        try {
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dmDataSetWrapper.next()) {
                for (final int serverType : scepServers.keySet()) {
                    final String serverName = scepServers.get(serverType);
                    certRepoDetails.put(serverName + "_SCEP_TYPE", dmDataSetWrapper.getValue(serverName + "_SCEP_TYPE"));
                    certRepoDetails.put(serverName + "_SCEP_CONFIGURATIONS", dmDataSetWrapper.getValue(serverName + "_SCEP_CONFIGURATIONS"));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting digicert details: ", e);
        }
        ((Hashtable<String, JSONObject>)this.mdmTrackerProperties).put("CERTIFCATE_REPO_INDIVIDUAL", certRepoDetails);
    }
    
    private void addAndroidPrivateAppsCount() {
        try {
            final Criteria androidPlatform = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)2, 0);
            final Criteria privateApps = new Criteria(new Column("MdPackageToAppGroup", "PRIVATE_APP_TYPE"), (Object)1, 0);
            this.mdmTrackerProperties.setProperty("ANDROID_PRIVATE_APPS_COUNT", String.valueOf(AppsUtil.getInstance().getAppRepositoryCount(androidPlatform.and(privateApps))));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Cannot fetch android private apps count", ex);
        }
    }
    
    private void addMultiTrackPlayStoreApps() {
        final SelectQuery selectQuery = MDMCoreQuery.getInstance().getMDMQueryMap("MULTI_TRACK_PLAYSTORE_APP");
        int multiPlayStoreAppCount = 0;
        try {
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dataSetWrapper.next()) {
                multiPlayStoreAppCount = (int)dataSetWrapper.getValue("APP_COUNT");
            }
            this.mdmTrackerProperties.setProperty("MULTITRACK_PLAYSTORE_APPS_COUNT", String.valueOf(multiPlayStoreAppCount));
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Cannot fetch multi track playstore count", e);
        }
    }
}
