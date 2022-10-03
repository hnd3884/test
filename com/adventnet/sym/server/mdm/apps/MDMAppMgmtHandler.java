package com.adventnet.sym.server.mdm.apps;

import java.util.Hashtable;
import com.me.mdm.server.apps.appupdatepolicy.AppUpdatePolicyDBHandler;
import com.adventnet.sym.server.mdm.config.task.AssignCommandTaskProcessor;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.mdm.server.deployment.DeploymentBean;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.deployment.policy.AppDeploymentPolicyImpl;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.mdm.server.deployment.app.noui.AppDeploymentPolicyNonUiManager;
import com.adventnet.persistence.WritableDataObject;
import com.me.mdm.api.error.APIHTTPException;
import javax.servlet.http.HttpServletRequest;
import com.dd.plist.NSObject;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import java.util.Set;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import java.util.HashSet;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.CaseExpression;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.SortColumn;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.IOSAppDatahandler;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import com.me.mdm.server.util.MDMTransactionManager;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.config.ProfileHandler;
import com.adventnet.sym.server.mdm.message.MDMMessageHandler;
import com.me.mdm.server.apps.tracks.AppTrackUtil;
import com.me.mdm.server.apps.config.AppConfigDataHandler;
import com.me.mdm.server.apps.AppDependencyHandler;
import java.util.Collection;
import com.me.mdm.server.apps.BaseAppAdditionDataProvider;
import com.me.mdm.server.apps.AppTrashModeHandler;
import com.adventnet.sym.server.mdm.inv.AppDataHandler;
import com.me.mdm.files.FileFacade;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.mdm.server.windows.apps.WpCompanyHubAppHandler;
import com.adventnet.sym.server.mdm.util.MDMAgentBuildVersionsUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.me.mdm.server.apps.multiversion.AppVersionHandler;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.apps.ios.ContentMetaDataAppDetails;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Map;
import java.util.Iterator;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreAssetUtil;
import org.json.JSONObject;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppMgmtHandler;
import com.adventnet.i18n.I18N;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.io.File;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MDMAppMgmtHandler
{
    public Logger logger;
    public Logger profileDistributionLog;
    public Logger mdmAppMgmtLogger;
    public static final int APP_STORE_COLLECTION_TYPE = 4;
    public static final int ENTERPRISE_COLLECTION_TYPE = 5;
    public static final int FREE_APP_PACKAGE_TYPE = 0;
    public static final int PAID_APP_PACKAGE_TYPE = 1;
    public static final int ENTERPRISE_APP_PACKAGE_TYPE = 2;
    public static final int APP_ADDED_SCANNING_TYPE = 0;
    public static final int APP_ADDED_PACKAGING_TYPE = 1;
    public static final int APP_ADDED_SCANNING_PACKAGING_TYPE = 2;
    public static final int IOS_PLATFORM_TYPE = 1;
    public static final int ANDROID_PLATFORM_TYPE = 2;
    public static final int WINDOWS_PLATFORM_TYPE = 3;
    public static final int IPAD_ONLY_SUPPORT = 1;
    public static final int IPHONE_ONLY_SUPPORT = 2;
    public static final int IPOD_ONLY_SUPPORT = 4;
    public static final int APPLE_TV_ONLY_SUPPORT = 8;
    public static final int MAC_ONLY_SUPPORT = 16;
    public static final int SMART_PHONE_TABLET_SUPPORT = 1;
    public static final int SMART_PHONE_ONLY_SUPPORT = 2;
    public static final int TABLET_ONLY_SUPPORT = 3;
    public static final int WIN_SMART_PHONE_TABLET_SUPPORT = 24;
    public static final int WIN_SMART_PHONE_ONLY_SUPPORT = 8;
    public static final int WIN_TABLET_ONLY_SUPPORT = 16;
    public static final int APP_STORE_IDENTIFIER = 1;
    public static final int IOS_ENTERPRISE_IDENTIFIER = 2;
    public static final int PLAY_STORE_IDENTIFIER = 3;
    public static final int ANDROID_ENTERPRISE_IDENTIFIER = 4;
    public static final int WINDOWS_BUSINESS_STORE_IDENTIFIER = 5;
    public static final int IOS_APP_STORE_APP = 0;
    public static final int IOS_ENTERPRISE_APP = 1;
    public static final int ANDROID_PLAY_STORE_APP = 2;
    public static final int ANDROID_ENTERPRISE_APP = 3;
    public static final int ANDROID_PRIVATE_APP = 10;
    public static final int WINDOWS_STORE_APP = 4;
    public static final int WINDOWS_ENTERPRISE_APP = 5;
    public static final int WINDOWS_MSI_APP = 6;
    public static final int WINDOWS_ENTERPRISE_IDENTIFIER = 6;
    public static final int NON_BUSINESS_STORE_APPS = 0;
    public static final int CHROME_STORE_APP = 8;
    public static final int CHROME_CUSTOM_APP = 9;
    public static final int CHROME_CHROMEBOOK_SUPPORT = 1;
    public static final int PORTAL_APPROVED_APPS = 1;
    public static final int NON_PORTAL_APPS = 2;
    public static final int PORTAL_AND_NONPORTAL_APPS = 3;
    public static final int DEPRECATED_VERSION = 0;
    public static final int APPLE_ENTERPRISE_PROVISIONTYPE_ADHOC_APP_PROVISIONING = 1;
    public static final int APPLE_ENTERPRISE_PROVISIONTYPE_DEVELOPER_PROVISIONING = 2;
    public static final int APPLE_ENTERPRISE_PROVISIONTYPE_DISTRIBUTION_PROVISIONING = 3;
    public static final String DISTRIBUTED_DEVICE_COUNT = "DISTRIBUTED_DEVICE_COUNT";
    public static final String YET_TO_UPDATE_DEVICE_COUNT = "YET_TO_UPDATE_DEVICE_COUNT";
    public static final String APP_INSTALLATION_PROGRESS_DEVICE_COUNT = "APP_INSTALLATION_PROGRESS_DEVICE_COUNT";
    public static final String APP_INSTALLATION_FAILED_DEVICE_COUNT = "APP_INSTALLATION_FAILED_DEVICE_COUNT";
    public static final String APP_DISTRIBUTED_GROUP_COUNT = "APP_DISTRIBUTED_GROUP_COUNT";
    public static final String APP_UPDATE_SCHEDULED = "APP_UPDATE_SCHEDULED";
    private static final String PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=";
    private static final String WORK_PLAYSTORE_URL = "https://play.google.com/work/apps/details?id=";
    private static MDMAppMgmtHandler mdmHandler;
    
    public MDMAppMgmtHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.profileDistributionLog = Logger.getLogger("MDMProfileDistributionLog");
        this.mdmAppMgmtLogger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    public static MDMAppMgmtHandler getInstance() {
        if (MDMAppMgmtHandler.mdmHandler == null) {
            MDMAppMgmtHandler.mdmHandler = new MDMAppMgmtHandler();
        }
        return MDMAppMgmtHandler.mdmHandler;
    }
    
    public void deletePackageDetails(final Long[] packageIds) {
        this.logger.log(Level.FINE, "Inside deletePackageDetails()");
        this.logger.log(Level.INFO, "Package Ids to be Deleted  :  {0}", Arrays.toString(packageIds));
        try {
            final Criteria packIdCri = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageIds, 8);
            DataAccess.delete("MdPackage", packIdCri);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in deletePackageDetails...", ex);
        }
    }
    
    public void deleteAppRepositoryFiles(final Long customerId, final Long[] packageId, final Long[] appGroupId) {
        this.logger.log(Level.FINE, "Inside deleteAppRepositoryFiles()");
        this.logger.log(Level.INFO, "Package Ids of Customer Id {0} to be Deleted  :  {1}", new Object[] { customerId, Arrays.toString(appGroupId) });
        Long groupId = null;
        Long appPackageId = null;
        try {
            for (int i = 0; i < packageId.length; ++i) {
                appPackageId = packageId[i];
                final String appRepositoryFilePath = this.getAppRepositoryFolderPath(customerId, appPackageId);
                ApiFactoryProvider.getFileAccessAPI().deleteDirectory(appRepositoryFilePath);
                if (MDMApiFactoryProvider.getCloudFileStorageAPI() != null) {
                    MDMApiFactoryProvider.getCloudFileStorageAPI().deleteCloudFileStorageDetails(appRepositoryFilePath);
                }
            }
            for (int i = 0; i < appGroupId.length; ++i) {
                groupId = appGroupId[i];
                final String appRepositoryFilePath = this.getAppRepositoryLicensePath(customerId, groupId);
                ApiFactoryProvider.getFileAccessAPI().deleteDirectory(appRepositoryFilePath);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in deleteAppRepositoryFiles...", ex);
        }
    }
    
    public boolean isAppDeleteSafe(final List appGroupList, final Boolean excludeInactiveDevice) {
        boolean isAppDeleteSafe = false;
        try {
            final SelectQuery appDeleteSafeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            appDeleteSafeQuery.addJoin(new Join("MdAppGroupDetails", "MdAppCatalogToResource", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            appDeleteSafeQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            appDeleteSafeQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            appDeleteSafeQuery.addJoin(new Join("MdAppCatalogToResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            Criteria appGrpCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupList.toArray(), 8);
            final Criteria allCustomerAppCriteria = new Criteria(Column.getColumn("MdPackage", "APP_SHARED_SCOPE"), (Object)1, 0).and(new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)AppsUtil.getDerivedColumnOfAppIdentifiersForGivenAppGroupIds(appGroupList), 8));
            appGrpCriteria = appGrpCriteria.or(allCustomerAppCriteria);
            final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), 0);
            final Criteria installedNullCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)null, 0);
            appDeleteSafeQuery.setCriteria(appGrpCriteria.and(ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria()).and(platformCriteria).and(installedNullCriteria.negate()));
            appDeleteSafeQuery.addSelectColumn(new Column("MdAppGroupDetails", "*"));
            if (excludeInactiveDevice) {
                int inactive_period = 7;
                try {
                    final String inactive = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("inactive_period");
                    inactive_period = ((inactive == null) ? 7 : Integer.parseInt(inactive));
                }
                catch (final Exception e) {
                    inactive_period = 7;
                }
                final List inactiveDevices = ManagedDeviceHandler.getInstance().getInActiveDevice(null, inactive_period);
                final Criteria inactiveCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)inactiveDevices.toArray(), 9);
                appDeleteSafeQuery.setCriteria(appDeleteSafeQuery.getCriteria().and(inactiveCriteria));
            }
            final DataObject dObj = MDMUtil.getPersistence().get(appDeleteSafeQuery);
            if (dObj.isEmpty()) {
                this.logger.log(Level.INFO, "App is delete safe.AppGroupId:{0}", new Object[] { appGroupList });
                isAppDeleteSafe = true;
            }
            else {
                final Long resourceID = (Long)dObj.getRow("ManagedDevice").get("RESOURCE_ID");
                final String bundleID = (String)dObj.getRow("MdAppGroupDetails").get("IDENTIFIER");
                this.logger.log(Level.INFO, "AppgroupList {0} not delete safe. Device count with apps distributed {1}, first device {2}-app{3}", new Object[] { appGroupList, dObj.size("ManagedDevice"), resourceID, bundleID });
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in isAppDeleteSafe...", ex);
        }
        this.logger.log(Level.INFO, "App group Ids validation for Delete Safe : {0} - {1}", new Object[] { Arrays.toString(appGroupList.toArray()), isAppDeleteSafe });
        return isAppDeleteSafe;
    }
    
    public void deleteProfileforPackage(final Long[] profileIds, final Long customerId) {
        this.logger.log(Level.FINE, "Inside deleteProfileforPackage()");
        this.logger.log(Level.INFO, "Profile Ids deleted during App deletion :  {0}", Arrays.toString(profileIds));
        try {
            MDMUtil.getUserTransaction().begin();
            ProfileConfigHandler.deleteConfiguration(profileIds, customerId);
            final Criteria profileDelCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileIds, 8);
            DataAccess.delete("Profile", profileDelCri);
            MDMUtil.getUserTransaction().commit();
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in deleteProfileforPackage...", ex);
            try {
                MDMUtil.getUserTransaction().rollback();
            }
            catch (final Exception exp) {
                this.logger.log(Level.SEVERE, "Exception while rolling back the transaction", ex);
            }
        }
    }
    
    protected int getPackageType(final boolean isPaidApp) {
        this.logger.log(Level.FINE, "Inside getPackageType()");
        int packageType = 0;
        if (isPaidApp) {
            packageType = 1;
        }
        else {
            packageType = 0;
        }
        return packageType;
    }
    
    public Long addorUpdateAppCategory(final String sAppCategoryName, final int platformType) throws DataAccessException {
        Long appCategoryId = null;
        final Criteria catNameCri = new Criteria(Column.getColumn("AppCategory", "APP_CATEGORY_NAME"), (Object)sAppCategoryName, 0);
        final Criteria platformCri = new Criteria(Column.getColumn("AppCategory", "PLATFORM_TYPE"), (Object)platformType, 0);
        final Criteria cri = catNameCri.and(platformCri);
        final DataObject dObj = DataAccess.get("AppCategory", cri);
        Row appCatRow = null;
        if (dObj.isEmpty()) {
            this.logger.log(Level.INFO, "App Category Name to be added  :  {0}", sAppCategoryName);
            appCatRow = new Row("AppCategory");
            appCatRow.set("APP_CATEGORY_NAME", (Object)sAppCategoryName);
            appCatRow.set("APP_CATEGORY_LABEL", (Object)sAppCategoryName);
            appCatRow.set("PLATFORM_TYPE", (Object)platformType);
            dObj.addRow(appCatRow);
            MDMUtil.getPersistence().add(dObj);
            ApiFactoryProvider.getCacheAccessAPI().removeCache("MDM_API_UTIL_API_TO_CATEGORY_CACHE", 2);
        }
        else {
            dObj.getRow("AppCategory");
        }
        appCategoryId = (Long)dObj.getValue("AppCategory", "APP_CATEGORY_ID", catNameCri);
        return appCategoryId;
    }
    
    public HashMap copyAppRepositoryFiles(final File file, final String destFilePath, final String destFileDCPath, String destFileName, final boolean isautoRename, final boolean isLocal) {
        this.logger.log(Level.FINE, "Inside copyAppRepositoryFiles()");
        this.logger.log(Level.INFO, "Destination File Path  :  {0}", destFilePath);
        this.logger.log(Level.INFO, "Destination File Path  DC:  {0}", destFileDCPath);
        String destDCFileName = "";
        final InputStream stream = null;
        final FileOutputStream fos = null;
        final BufferedInputStream bis = null;
        HashMap destFileMap = null;
        String fileCheckSum = null;
        try {
            String fileName;
            if (isautoRename) {
                final Calendar cal = Calendar.getInstance();
                fileName = cal.getTimeInMillis() + "_" + file.getName();
            }
            else {
                fileName = file.getName();
            }
            if (destFileName == null) {
                destFileName = fileName;
            }
            if (destFileName != null && !destFileName.equalsIgnoreCase("")) {
                destDCFileName = destFileDCPath + File.separator + destFileName;
                destFileName = destFilePath + File.separator + destFileName;
            }
            fileCheckSum = SecurityUtil.getSHA256HashFromInputStream((InputStream)new FileInputStream(file));
            if (isLocal) {
                this.logger.log(Level.WARNING, "Writing File to Local {0}", destFileName);
                FileAccessUtil.writeFileInServer(destFileName, (InputStream)new FileInputStream(file));
            }
            else {
                this.logger.log(Level.WARNING, "Writing File to DFS {0}", destFileName);
                ApiFactoryProvider.getFileAccessAPI().writeFile(destFileName, (InputStream)new FileInputStream(file));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in copyAppRepositoryFiles...", ex);
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception closing FileOutputStream fos", ex);
            }
            try {
                if (bis != null) {
                    bis.close();
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception closing BufferedInputStream bis", ex);
            }
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception closing InputStream stream", ex);
            }
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (final Exception ex2) {
                this.logger.log(Level.WARNING, "Exception closing FileOutputStream fos", ex2);
            }
            try {
                if (bis != null) {
                    bis.close();
                }
            }
            catch (final Exception ex2) {
                this.logger.log(Level.WARNING, "Exception closing BufferedInputStream bis", ex2);
            }
            try {
                if (stream != null) {
                    stream.close();
                }
            }
            catch (final Exception ex2) {
                this.logger.log(Level.WARNING, "Exception closing InputStream stream", ex2);
            }
        }
        destFileMap = new HashMap();
        destFileMap.put("destFileName", destFileName);
        destFileMap.put("destDCFileName", destDCFileName);
        destFileMap.put("fileCheckSum", fileCheckSum);
        return destFileMap;
    }
    
    public HashMap copyAppRepositoryFiles(final File file, final String destFilePath, final String destFileDCPath, final boolean isautoRename, final boolean isLocal) {
        return this.copyAppRepositoryFiles(file, destFilePath, destFileDCPath, null, isautoRename, isLocal);
    }
    
    public HashMap getAppDetailsMap(final Long packageId, final Long releaseLabel) {
        return this.getAppDetailsMap(packageId, releaseLabel, null);
    }
    
    public HashMap getAppDetailsMap(final Long packageId, final Long releaseLabel, Long businessStoreID) {
        final HashMap appDetailsMap = new HashMap();
        try {
            final DataObject DO = this.getAppRepositoryDetails(packageId, releaseLabel);
            if (!DO.isEmpty()) {
                final Row mdPackageRow = DO.getFirstRow("MdPackage");
                final Row packageRow = DO.getFirstRow("MdPackageToAppGroup");
                final Row appGroupRow = DO.getFirstRow("MdAppGroupDetails");
                final Row appPackageRow = DO.getFirstRow("MdPackageToAppData");
                final Row appToCollectionRow = DO.getFirstRow("MdAppToCollection");
                final Row appGroupCollectionRow = DO.getFirstRow("AppGroupToCollection");
                final Row appReleaseLabelRow = DO.getFirstRow("AppReleaseLabel");
                Row appTrackRow = null;
                Row appDetailsRow = null;
                if (DO.containsTable("MdAppDetails")) {
                    appDetailsRow = DO.getFirstRow("MdAppDetails");
                    appDetailsMap.put("APP_TITLE", appDetailsRow.get("APP_TITLE"));
                }
                final Row appCategoryRow = DO.getFirstRow("AppCategory");
                Row appConfigPolicyRow = null;
                Iterator appDependencyRow = null;
                Iterator appRequiredDependencyRow = null;
                if (DO.containsTable("AppConfigPolicy")) {
                    appConfigPolicyRow = DO.getFirstRow("AppConfigPolicy");
                }
                if (DO.containsTable("AppDependency")) {
                    appDependencyRow = DO.getRows("AppDependency");
                }
                if (DO.containsTable("AppToRequiredDependency")) {
                    appRequiredDependencyRow = DO.getRows("AppToRequiredDependency");
                }
                Row appConfTemplateRow = null;
                if (DO.containsTable("AppConfigTemplate")) {
                    appConfTemplateRow = DO.getFirstRow("AppConfigTemplate");
                }
                Row configDataItemRow = null;
                if (DO.containsTable("ConfigDataItem")) {
                    configDataItemRow = DO.getFirstRow("ConfigDataItem");
                }
                Row profileToCollectionRow = null;
                if (DO.containsTable("ProfileToCollection")) {
                    profileToCollectionRow = DO.getFirstRow("ProfileToCollection");
                    appDetailsMap.put("COLLECTION_ID", profileToCollectionRow.get("COLLECTION_ID"));
                    appDetailsMap.put("PROFILE_ID", profileToCollectionRow.get("PROFILE_ID"));
                }
                if (DO.containsTable("MdAppGroupCategoryRel")) {
                    final Row categoryRow = DO.getFirstRow("MdAppGroupCategoryRel");
                    appDetailsMap.put("APP_CATEGORY_ID", categoryRow.get("APP_CATEGORY_ID"));
                }
                if (DO.containsTable("MdPackagePolicy")) {
                    final Row policyRow = DO.getFirstRow("MdPackagePolicy");
                    appDetailsMap.put("REMOVE_APP_WITH_PROFILE", policyRow.get("REMOVE_APP_WITH_PROFILE"));
                    appDetailsMap.put("PREVENT_BACKUP", policyRow.get("PREVENT_BACKUP"));
                }
                final Row profileRow = DO.getFirstRow("Profile");
                Row mdAppDetailsRow = null;
                mdAppDetailsRow = DO.getFirstRow("MdAppDetails");
                final Long appGroupId = (Long)packageRow.get("APP_GROUP_ID");
                final HashMap multiVersionDetails = this.getMultiversionLatestAppDetails(appGroupId, releaseLabel);
                if (multiVersionDetails.containsKey("MultiVersion") && multiVersionDetails.get("MultiVersion")) {
                    if (multiVersionDetails.containsKey("SmartPhoneVersion")) {
                        appDetailsMap.put("SMARTPHONE_VERSION", multiVersionDetails.get("SmartPhoneVersion"));
                    }
                    if (multiVersionDetails.containsKey("Tablet")) {
                        appDetailsMap.put("TABLET_VERSION", multiVersionDetails.get("Tablet"));
                    }
                }
                final int packageType = (int)packageRow.get("PACKAGE_TYPE");
                final Long packageID = (Long)packageRow.get("PACKAGE_ID");
                final Boolean isPurchasedFromPortal = (Boolean)packageRow.get("IS_PURCHASED_FROM_PORTAL");
                final Boolean isPaidApp = (Boolean)packageRow.get("IS_PAID_APP");
                String packageTypeName = "";
                final Integer platformType = (Integer)mdPackageRow.get("PLATFORM_TYPE");
                appDetailsMap.put("STORE_URL", appPackageRow.get("STORE_URL"));
                boolean isDistributable = true;
                if (platformType != null) {
                    final Long customerID = this.getCustomerIDForAppGroupID(appGroupId);
                    if (platformType == 1 && (packageType == 0 || packageType == 1)) {
                        packageTypeName = I18N.getMsg("dc.mdm.actionlog.appmgmt.appStoreApp", new Object[0]);
                        final Boolean hasError = VPPAppMgmtHandler.getInstance().isVPPAppHasError(customerID, appPackageRow.get("STORE_ID").toString());
                        appDetailsMap.put("HAS_MIGRATION_ERROR", hasError);
                    }
                    else if (platformType == 1 && packageType == 2) {
                        packageTypeName = I18N.getMsg("dc.mdm.actionlog.appmgmt.enterpriseApp", new Object[0]);
                    }
                    else if (platformType == 2 && (packageType == 0 || packageType == 1)) {
                        packageTypeName = I18N.getMsg("dc.mdm.actionlog.appmgmt.playStoreApp", new Object[0]);
                        final String playStoreUrl = this.getPlayStoreUrl((String)appGroupRow.get("IDENTIFIER"), isPurchasedFromPortal);
                        appDetailsMap.put("PLAY_STORE_URL", playStoreUrl);
                        appDetailsMap.put("STORE_URL", playStoreUrl);
                    }
                    else if (platformType == 2 && packageType == 2) {
                        packageTypeName = I18N.getMsg("dc.mdm.actionlog.appmgmt.android_enterpriseApp", new Object[0]);
                    }
                    else if (platformType == 3 && (packageType == 0 || packageType == 1)) {
                        packageTypeName = I18N.getMsg("dc.mdm.actionlog.appmgmt.windows_businessStoreApp", new Object[0]);
                        final Row windowsAppDetails = DO.getFirstRow("WindowsAppDetails");
                        appDetailsMap.put("IMG_BG", windowsAppDetails.get("IMG_BG"));
                        appDetailsMap.put("STORE_ID", windowsAppDetails.get("PRODUCT_ID"));
                        appDetailsMap.put("IS_OFFLINE_APP", windowsAppDetails.get("IS_OFFLINE_APP"));
                    }
                    else if (platformType == 3 && packageType == 2) {
                        packageTypeName = I18N.getMsg("dc.mdm.actionlog.appmgmt.windows_enterpriseApp", new Object[0]);
                    }
                    else if (platformType == 4 && (packageType == 0 || packageType == 1)) {
                        packageTypeName = I18N.getMsg("Chrome Web Store App", new Object[0]);
                        final String playStoreUrl = "https://chrome.google.com/webstore/detail/" + (String)appGroupRow.get("IDENTIFIER");
                        appDetailsMap.put("PLAY_STORE_URL", playStoreUrl);
                        appDetailsMap.put("DISTRIBUTED_USER_COUNT", this.getAppDistributedUserCount(appGroupId));
                    }
                    else if (platformType == 4 && packageType == 2) {
                        packageTypeName = I18N.getMsg("Chrome Custom App", new Object[0]);
                        appDetailsMap.put("PLAY_STORE_URL", appPackageRow.get("APP_FILE_LOC"));
                        appDetailsMap.put("DISTRIBUTED_USER_COUNT", this.getAppDistributedUserCount(appGroupId));
                    }
                }
                final Long profileID = (Long)profileRow.get("PROFILE_ID");
                appDetailsMap.put("PACKAGE_TYPE_NAME", packageTypeName);
                appDetailsMap.put("SUPPORTED_ARCH", appPackageRow.get("SUPPORTED_ARCH"));
                appDetailsMap.put("PLATFORM_TYPE", platformType);
                appDetailsMap.put("DESCRIPTION", appPackageRow.get("DESCRIPTION"));
                appDetailsMap.put("COMMAND_LINE", appPackageRow.get("COMMAND_LINE"));
                appDetailsMap.put("PACKAGE_TYPE", packageType);
                appDetailsMap.put("APP_GROUP_ID", appPackageRow.get("APP_GROUP_ID"));
                appDetailsMap.put("IS_PURCHASED_FROM_PORTAL", isPurchasedFromPortal);
                appDetailsMap.put("IS_PAID_APP", isPaidApp);
                appDetailsMap.put("APP_FILE_LOC", appPackageRow.get("APP_FILE_LOC"));
                appDetailsMap.put("APP_NAME", appGroupRow.get("GROUP_DISPLAY_NAME"));
                appDetailsMap.put("PROFILE_NAME", profileRow.get("PROFILE_NAME"));
                appDetailsMap.put("PROFILE_ID", profileID);
                appDetailsMap.put("IS_MOVED_TO_TRASH", profileRow.get("IS_MOVED_TO_TRASH"));
                appDetailsMap.put("IDENTIFIER", appGroupRow.get("IDENTIFIER"));
                appDetailsMap.put("BUNDLE_IDENTIFIER", appGroupRow.get("IDENTIFIER"));
                if (appDetailsMap.get("STORE_ID") == null) {
                    appDetailsMap.put("STORE_ID", appPackageRow.get("STORE_ID"));
                }
                appDetailsMap.put("DISPLAY_IMAGE_LOC", appPackageRow.get("DISPLAY_IMAGE_LOC"));
                appDetailsMap.put("APP_ID", appToCollectionRow.get("APP_ID"));
                appDetailsMap.put("FULL_IMAGE_LOC", appPackageRow.get("FULL_IMAGE_LOC"));
                appDetailsMap.put("APP_VERSION", mdAppDetailsRow.get("APP_VERSION"));
                appDetailsMap.put("APP_NAME_SHORT_VERSION", mdAppDetailsRow.get("APP_NAME_SHORT_VERSION"));
                appDetailsMap.put("BUNDLE_SIZE", mdAppDetailsRow.get("BUNDLE_SIZE"));
                appDetailsMap.put("LATEST_APP_ID", mdAppDetailsRow.get("APP_ID"));
                appDetailsMap.put("APP_FILE_LOC", appPackageRow.get("APP_FILE_LOC"));
                appDetailsMap.put("CUSTOMIZED_APP_URL", appPackageRow.get("CUSTOMIZED_APP_URL"));
                appDetailsMap.put("COMMAND_LINE", appPackageRow.get("COMMAND_LINE"));
                appDetailsMap.put("SUPPORTED_DEVICES", appPackageRow.get("SUPPORTED_DEVICES"));
                appDetailsMap.put("MIN_OS", appPackageRow.get("MIN_OS"));
                appDetailsMap.put("APP_GROUP_ID", appGroupId);
                appDetailsMap.put("PACKAGE_ID", packageID);
                appDetailsMap.put("PLATFORM_TYPE", appGroupRow.get("PLATFORM_TYPE"));
                appDetailsMap.put("PACKAGE_ADDED_TIME", mdPackageRow.get("PACKAGE_ADDED_TIME"));
                appDetailsMap.put("PACKAGE_MODIFIED_TIME", mdPackageRow.get("PACKAGE_MODIFIED_TIME"));
                appDetailsMap.put("APP_SHARED_SCOPE", mdPackageRow.get("APP_SHARED_SCOPE"));
                appDetailsMap.put("APP_CATEGORY_NAME", appCategoryRow.get("APP_CATEGORY_NAME"));
                appDetailsMap.put("APP_TITLE", mdAppDetailsRow.get("APP_TITLE"));
                appDetailsMap.put("IS_PAID_APP", packageRow.get("IS_PAID_APP"));
                appDetailsMap.put("DESCRIPTION", appPackageRow.get("DESCRIPTION"));
                appDetailsMap.put("COMMAND_LINE", appPackageRow.get("COMMAND_LINE"));
                appDetailsMap.put("FILE_UPLOAD_SIZE", appPackageRow.get("FILE_UPLOAD_SIZE"));
                appDetailsMap.put("RELEASE_LABEL_ID", appReleaseLabelRow.get("RELEASE_LABEL_ID"));
                appDetailsMap.put("RELEASE_LABEL_DISPLAY_NAME", I18N.getMsg((String)appReleaseLabelRow.get("RELEASE_LABEL_DISPLAY_NAME"), new Object[0]));
                appDetailsMap.put("APP_VERSION_STATUS", appGroupCollectionRow.get("APP_VERSION_STATUS"));
                appDetailsMap.put("APP_CHECKSUM", appPackageRow.get("APP_CHECKSUM"));
                if (appConfigPolicyRow != null) {
                    appDetailsMap.put("APP_CONFIG_ID", appConfigPolicyRow.get("APP_CONFIG_ID"));
                }
                if (appConfTemplateRow != null) {
                    appDetailsMap.put("APP_CONFIG_TEMPLATE_ID", appConfTemplateRow.get("APP_CONFIG_TEMPLATE_ID"));
                }
                if (configDataItemRow != null) {
                    appDetailsMap.put("CONFIG_DATA_ITEM_ID", configDataItemRow.get("CONFIG_DATA_ITEM_ID"));
                }
                if (DO.containsTable("ReleaseLabelToAppTrack")) {
                    appTrackRow = DO.getFirstRow("ReleaseLabelToAppTrack");
                    isDistributable = ((int)appTrackRow.get("STATUS") != 0);
                }
                appDetailsMap.put("is_distributable", isDistributable);
                final JSONArray appDependency = new JSONArray();
                if (appDependencyRow != null) {
                    while (appDependencyRow.hasNext()) {
                        final Row row = appDependencyRow.next();
                        final JSONObject jsonObject = new JSONObject();
                        jsonObject.put("dependencyID", row.get("DEPENDENCY_ID"));
                        jsonObject.put("name", row.get("DEPENDENCY_NAME"));
                        jsonObject.put("version", row.get("VERSION"));
                        jsonObject.put("architecture", row.get("SUPPORTED_ARCH"));
                        appDependency.put((Object)jsonObject);
                    }
                }
                final JSONArray reqDep = new JSONArray();
                if (appRequiredDependencyRow != null) {
                    while (appRequiredDependencyRow.hasNext()) {
                        final Row row2 = appRequiredDependencyRow.next();
                        final JSONObject dependencyObject = new JSONObject();
                        dependencyObject.put("min_version", row2.get("DEPENDENCY_MIN_VERSION"));
                        dependencyObject.put("name", row2.get("DEPENDENCY_IDENTIFIER"));
                        reqDep.put((Object)dependencyObject);
                    }
                }
                if (appDependency.length() != 0) {
                    appDetailsMap.put("dependency", appDependency);
                }
                if (reqDep.length() != 0) {
                    appDetailsMap.put("dependencyList", reqDep);
                }
                appDetailsMap.put("APP_VERSION", mdAppDetailsRow.get("APP_VERSION"));
                appDetailsMap.put("COLLECTION_ID", appToCollectionRow.get("COLLECTION_ID"));
                if (businessStoreID == null && platformType == 1) {
                    final List businessStoreIDs = MDBusinessStoreAssetUtil.getBusinessStoresWithApp(appGroupId);
                    if (businessStoreIDs != null && !businessStoreIDs.isEmpty() && businessStoreIDs.size() == 1) {
                        businessStoreID = businessStoreIDs.get(0);
                    }
                }
                final JSONObject appDistributedCountJson = this.getAppDistributedCount(appGroupId, releaseLabel, profileID, businessStoreID);
                if (appDistributedCountJson != null) {
                    appDetailsMap.put("DISTRIBUTED_DEVICE_COUNT", appDistributedCountJson.optInt("DISTRIBUTED_DEVICE_COUNT", 0));
                    appDetailsMap.put("YET_TO_UPDATE_DEVICE_COUNT", appDistributedCountJson.optInt("YET_TO_UPDATE_DEVICE_COUNT", 0));
                    appDetailsMap.put("APP_INSTALLATION_PROGRESS_DEVICE_COUNT", appDistributedCountJson.optInt("APP_INSTALLATION_PROGRESS_DEVICE_COUNT", 0));
                    appDetailsMap.put("APP_INSTALLATION_FAILED_DEVICE_COUNT", appDistributedCountJson.optInt("APP_INSTALLATION_FAILED_DEVICE_COUNT", 0));
                    appDetailsMap.put("APP_DISTRIBUTED_GROUP_COUNT", appDistributedCountJson.optInt("APP_DISTRIBUTED_GROUP_COUNT", 0));
                    appDetailsMap.put("APP_UPDATE_SCHEDULED", appDistributedCountJson.optInt("APP_UPDATE_SCHEDULED", 0));
                }
                final Map<Long, Map<String, String>> availableRelLabelDetails = AppVersionDBUtil.getInstance().getAvailableReleaseLabelsForSpecificPackage(packageId);
                appDetailsMap.put("releaseLabelDetails", availableRelLabelDetails);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getAppDetailsMap", ex);
        }
        return appDetailsMap;
    }
    
    private Long getCustomerIDForAppGroupID(final Long appGroupID) {
        Long customerID = null;
        try {
            final Row appGrpRow = DBUtil.getRowFromDB("MdAppGroupDetails", "APP_GROUP_ID", (Object)appGroupID);
            if (appGrpRow != null) {
                customerID = (Long)appGrpRow.get("CUSTOMER_ID");
            }
        }
        catch (final Exception ex) {}
        return customerID;
    }
    
    public void updateAppCatalogStatus(final String syncAppCatalogData) {
        Long collectionId = null;
        int collectionStatus = -1;
        JSONObject jsonMsg = null;
        JSONArray jsonArr = null;
        boolean isMarkedForDelete = false;
        String remarks = "";
        try {
            this.logger.log(Level.INFO, "Received App Catalog Status from Agent: {0}", new Object[] { syncAppCatalogData });
            final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(syncAppCatalogData));
            final String strUDID = hmap.get("UDID");
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
            final String message = hmap.get("Message");
            final String msgType = hmap.get("MessageType");
            jsonArr = new JSONArray(message);
            for (int arrayLen = jsonArr.length(), i = 0; i < arrayLen; ++i) {
                jsonMsg = jsonArr.getJSONObject(i);
                if (msgType.equalsIgnoreCase("ManagedAppStatus")) {
                    collectionId = jsonMsg.getLong("CollectionID");
                    final Long appId = MDMUtil.getInstance().getAppIdFromCollectionId(collectionId);
                    final Long appGroupId = MDMUtil.getInstance().getAppGroupIDFromCollection(collectionId);
                    final String appStatus = String.valueOf(jsonMsg.get("Status"));
                    final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
                    if (appStatus != null && appStatus.equalsIgnoreCase("Managed")) {
                        collectionStatus = 6;
                        remarks = "dc.db.mdm.collection.Successfully_installed_the_app";
                        handler.updateAppInstallationStatus(resourceID, appGroupId, appId, 2, remarks, 0);
                        MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId.toString(), collectionStatus, remarks);
                    }
                    if (appStatus != null && appStatus.equalsIgnoreCase("UserInstalledApp")) {
                        collectionStatus = 6;
                        remarks = "dc.db.mdm.collection.App_already_installed";
                        MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId.toString(), collectionStatus, remarks);
                        handler.updateAppInstallationStatus(resourceID, appGroupId, appId, 2, remarks, 0);
                    }
                    else if (appStatus != null && (appStatus.equalsIgnoreCase("Removed") || appStatus.equalsIgnoreCase("UserRejectedUninstallation") || appStatus.equalsIgnoreCase("ManagedButUninstalled"))) {
                        isMarkedForDelete = MDMUtil.getInstance().getMarkedForDeleteStatus(resourceID, collectionId);
                        if (isMarkedForDelete && appStatus.equalsIgnoreCase("Removed")) {
                            collectionStatus = 6;
                            remarks = "dc.db.mdm.collection.Successfully_removed_the_app";
                            AppsUtil.getInstance().deleteAppResourceRel(resourceID, appGroupId);
                            new MDDeviceInstalledAppsHandler().removeInstalledAppResourceRelation(resourceID, appGroupId);
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId.toString(), collectionStatus, remarks);
                            ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceID, collectionId);
                            final List resourceList = new ArrayList();
                            resourceList.add(resourceID);
                            ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
                        }
                        else if (!isMarkedForDelete && (appStatus.equalsIgnoreCase("Removed") || appStatus.equalsIgnoreCase("ManagedButUninstalled"))) {
                            handler.updateAppInstallationStatus(resourceID, appGroupId, null, 0, "dc.db.mdm.apps.status.ManagedButUninstalled", 0);
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId.toString(), 12, "dc.db.mdm.apps.status.ManagedButUninstalled");
                        }
                        else if (isMarkedForDelete && appStatus.equalsIgnoreCase("UserRejectedUninstallation")) {
                            collectionStatus = 7;
                            remarks = "dc.db.mdm.collection.Successfully_user_remove_cancell_the_app";
                            handler.updateAppInstallationStatus(resourceID, appGroupId, appId, 2, remarks, 0);
                            MDMCollectionStatusUpdate.getInstance().updateMdmConfigStatus(resourceID, collectionId.toString(), collectionStatus, remarks);
                        }
                    }
                }
                MDMCollectionStatusUpdate.getInstance().updateCollnToResErrorCode(resourceID, collectionId, null);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in updateAppCatalogStatus", ex);
        }
    }
    
    public String getFileNameFromFilePath(final String filePath) {
        this.logger.log(Level.FINE, "Inside getFileNameFromFilePath()");
        String fileName = "";
        try {
            if (filePath != null && !filePath.equals("")) {
                final int lastIndex = filePath.lastIndexOf(File.separator);
                fileName = filePath.substring(lastIndex + 1, filePath.length());
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getFileNameFromFilePath", ex);
        }
        return fileName;
    }
    
    public DataObject getAppRepositoryDetails(final Long packageid, final Long releaseLabelId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        sQuery.addJoin(new Join("MdPackage", "MdPackagePolicy", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        sQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        sQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, 2));
        sQuery.addJoin(new Join("MdPackageToAppData", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        sQuery.addJoin(new Join("MdPackageToAppData", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        sQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        sQuery.addJoin(new Join("AppGroupToCollection", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        sQuery.addJoin(new Join("MdAppGroupDetails", "MdAppGroupCategoryRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        sQuery.addJoin(new Join("MdAppGroupCategoryRel", "AppCategory", new String[] { "APP_CATEGORY_ID" }, new String[] { "APP_CATEGORY_ID" }, 2));
        sQuery.addJoin(new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        sQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        sQuery.addJoin(new Join("ProfileToCollection", "CfgDataToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "ConfigDataItem", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "InstallAppPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("InstallAppPolicy", "AppConfigPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        sQuery.addJoin(new Join("AppConfigPolicy", "ManagedAppConfiguration", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 1));
        sQuery.addJoin(new Join("ManagedAppConfiguration", "ManagedAppConfigurationData", new String[] { "APP_CONFIG_ID" }, new String[] { "APP_CONFIG_ID" }, 1));
        final Criteria platformCriteria = new Criteria(new Column("MdPackage", "PLATFORM_TYPE"), (Object)new int[] { 2, 4 }, 8);
        final Criteria androidChromeTemplateCriteria = new Criteria(new Column("MdAppToCollection", "APP_ID"), (Object)new Column("AppConfigTemplate", "APP_ID"), 0);
        final Criteria otherOSTemplateCriteria = new Criteria(new Column("ManagedAppConfiguration", "APP_CONFIG_TEMPLATE_ID"), (Object)new Column("AppConfigTemplate", "APP_CONFIG_TEMPLATE_ID"), 0);
        final Criteria finalCriteria = platformCriteria.and(androidChromeTemplateCriteria).or(otherOSTemplateCriteria);
        sQuery.addJoin(new Join("ManagedAppConfiguration", "AppConfigTemplate", finalCriteria, 1));
        sQuery.addJoin(new Join("AppConfigTemplate", "AppConfigTemplateExtn", new String[] { "APP_CONFIG_TEMPLATE_ID" }, new String[] { "APP_CONFIG_TEMPLATE_ID" }, 1));
        sQuery.addJoin(new Join("MdAppDetails", "WindowsAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
        sQuery.addJoin(new Join("InstallAppPolicy", "AppDependencyPolicy", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 1));
        sQuery.addJoin(new Join("AppDependencyPolicy", "AppDependency", new String[] { "DEPENDENCY_ID" }, new String[] { "DEPENDENCY_ID" }, 1));
        sQuery.addJoin(new Join("MdAppDetails", "AppToRequiredDependency", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
        sQuery.setCriteria(new Criteria(new Column("MdPackage", "PACKAGE_ID"), (Object)packageid, 0));
        final Criteria releaseLabelIdCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        Criteria collectionCriteria = AppVersionDBUtil.getInstance().getCriteriaForCollectionIdWithMdAppToCollection();
        collectionCriteria = collectionCriteria.and(releaseLabelIdCriteria);
        sQuery.setCriteria(sQuery.getCriteria().and(collectionCriteria));
        sQuery.addJoin(new Join("AppGroupToCollection", "ReleaseLabelToAppTrack", new String[] { "APP_GROUP_ID", "RELEASE_LABEL_ID" }, new String[] { "APP_GROUP_ID", "RELEASE_LABEL_ID" }, 1));
        sQuery.addSelectColumn(new Column("MdPackage", "*"));
        sQuery.addSelectColumn(new Column("MdPackagePolicy", "*"));
        sQuery.addSelectColumn(new Column("MdPackageToAppData", "*"));
        sQuery.addSelectColumn(new Column("MdAppDetails", "*"));
        sQuery.addSelectColumn(new Column("MdPackageToAppGroup", "*"));
        sQuery.addSelectColumn(new Column("MdAppGroupCategoryRel", "*"));
        sQuery.addSelectColumn(new Column("AppCategory", "*"));
        sQuery.addSelectColumn(new Column("MdAppGroupDetails", "*"));
        sQuery.addSelectColumn(new Column("MdAppToCollection", "*"));
        sQuery.addSelectColumn(new Column("ProfileToCollection", "*"));
        sQuery.addSelectColumn(new Column("Profile", "*"));
        sQuery.addSelectColumn(new Column("AppGroupToCollection", "COLLECTION_ID"));
        sQuery.addSelectColumn(new Column("AppGroupToCollection", "APP_VERSION_STATUS"));
        sQuery.addSelectColumn(new Column("AppReleaseLabel", "*"));
        sQuery.addSelectColumn(new Column("AppConfigPolicy", "*"));
        sQuery.addSelectColumn(new Column("AppConfigTemplate", "*"));
        sQuery.addSelectColumn(new Column("AppConfigTemplateExtn", "*"));
        sQuery.addSelectColumn(new Column("ManagedAppConfigurationData", "*"));
        sQuery.addSelectColumn(new Column("ConfigDataItem", "*"));
        sQuery.addSelectColumn(new Column("WindowsAppDetails", "*"));
        sQuery.addSelectColumn(new Column("AppDependency", "*"));
        sQuery.addSelectColumn(new Column("AppToRequiredDependency", "*"));
        sQuery.addSelectColumn(new Column("ReleaseLabelToAppTrack", "*"));
        final DataObject DO = MDMUtil.getPersistence().get(sQuery);
        return DO;
    }
    
    @Deprecated
    public Long getIOSNativeAgentAppId(final Long customerId) {
        this.logger.log(Level.FINE, "Inside getIOSNativeAgentAppId() for customer:{0}", customerId);
        Long appId = null;
        try {
            final Criteria bundleIdentifierCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.iosagent", 0);
            final Criteria platformCri = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria customerCri = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria cri = bundleIdentifierCri.and(platformCri).and(customerCri).and(AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria());
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            sQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            sQuery.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            sQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "APP_ID"));
            sQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "COLLECTION_ID"));
            sQuery.setCriteria(cri);
            final DataObject dObj = MDMUtil.getPersistence().get(sQuery);
            if (!dObj.isEmpty()) {
                final Row appRow = dObj.getFirstRow("MdAppToCollection");
                appId = (Long)appRow.get("APP_ID");
            }
            else {
                this.logger.log(Level.WARNING, "No entry in MDAPPGROUPDETAILS->APPGROUPTOCOLLECTION for iOS Native APP");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getIOSNativeAgentAppId", ex);
        }
        return appId;
    }
    
    public Long getNativeAgentAppId(final Long customerID, final int platform, final String bundleID) {
        Long appId = null;
        try {
            final Criteria bundleIdentifierCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)bundleID, 0);
            final Criteria platformCri = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platform, 0);
            final Criteria customerCri = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            final Criteria cri = bundleIdentifierCri.and(platformCri).and(customerCri);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            sQuery.addJoin(new Join("MdAppGroupDetails", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            sQuery.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            sQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "*"));
            sQuery.setCriteria(cri);
            final DataObject dObj = MDMUtil.getPersistence().get(sQuery);
            if (!dObj.isEmpty()) {
                final Row appRow = dObj.getFirstRow("MdAppToCollection");
                appId = (Long)appRow.get("APP_ID");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getting the appID of the agent", ex);
        }
        return appId;
    }
    
    public Long getIOSNativeAgentAppGroupId(final Long customerId) {
        this.logger.log(Level.FINE, "Inside getIOSNativeAgentAppId()");
        Long appGroupId = null;
        try {
            final Criteria bundleIdentifierCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.iosagent", 0);
            final Criteria platformCri = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)1, 0);
            final Criteria customerCri = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria cri = bundleIdentifierCri.and(platformCri).and(customerCri);
            final DataObject dObj = MDMUtil.getPersistence().get("MdAppGroupDetails", cri);
            if (!dObj.isEmpty()) {
                appGroupId = (Long)dObj.getValue("MdAppGroupDetails", "APP_GROUP_ID", cri);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getIOSNativeAgentAppId", ex);
        }
        return appGroupId;
    }
    
    private int getAppPackageType(final Object price) {
        final Double sPrice = (Double)price;
        if (sPrice == 0.0) {
            return 0;
        }
        return 1;
    }
    
    public JSONObject createiOSAppJson(final HashMap appDetails, final Long customerID, final Long userId, final boolean isAppPurchasedFromPortal) {
        final JSONObject jsonObject = new JSONObject();
        final ContentMetaDataAppDetails appDetailsObject = appDetails.get("appDetailsObject");
        try {
            jsonObject.put("APP_NAME", (Object)appDetailsObject.getAppName());
            jsonObject.put("APP_VERSION", (Object)appDetailsObject.getAppVersion());
            jsonObject.put("PLATFORM_TYPE", 1);
            jsonObject.put("EXTERNAL_APP_VERSION_ID", (Object)appDetailsObject.getExternalAppVersionID());
            jsonObject.put("APP_TITLE", (Object)"");
            jsonObject.put("BUNDLE_IDENTIFIER", (Object)appDetailsObject.getBundleId());
            jsonObject.put("APP_CATEGORY_NAME", (Object)appDetailsObject.getPrimaryGenreName());
            jsonObject.put("COUNTRY_CODE", (Object)appDetails.get("COUNTRY_CODE"));
            jsonObject.put("PACKAGE_TYPE", this.getAppPackageType(appDetailsObject.getAppPrice()));
            final Integer licenseType = appDetails.get("licenseType");
            if (licenseType != null) {
                jsonObject.put("licenseType", (Object)licenseType);
            }
            jsonObject.put("CUSTOMER_ID", (Object)customerID);
            final JSONObject packageAppDataJSON = new JSONObject();
            packageAppDataJSON.put("STORE_ID", (Object)appDetailsObject.getAdamId());
            packageAppDataJSON.put("SUPPORTED_DEVICES", (Object)appDetailsObject.getSupportDevice());
            final String storeUrl = appDetailsObject.getAppStoreURL();
            if (storeUrl != null) {
                packageAppDataJSON.put("STORE_URL", (Object)storeUrl);
            }
            String description = appDetailsObject.getAppDescription();
            if (MDMStringUtils.isEmpty(description)) {
                packageAppDataJSON.put("DESCRIPTION", (Object)"");
            }
            else {
                if (description.length() > 4800) {
                    description = description.substring(0, 4795).concat("...");
                }
                packageAppDataJSON.put("DESCRIPTION", (Object)description);
            }
            final String minOS = appDetailsObject.getMinimumOSVersion();
            if (minOS != null) {
                packageAppDataJSON.put("MIN_OS", (Object)minOS);
            }
            final String displayImage = appDetailsObject.getAppIconImageURL();
            if (displayImage != null) {
                jsonObject.put("DISPLAY_IMAGE_LOC", (Object)displayImage);
            }
            final JSONObject packageAppGroupJSON = new JSONObject();
            packageAppGroupJSON.put("IS_PAID_APP", (Object)appDetailsObject.getIsPaidApp());
            packageAppGroupJSON.put("IS_PURCHASED_FROM_PORTAL", isAppPurchasedFromPortal);
            final Integer private_app_type = appDetails.get("privateApp");
            if (private_app_type != null) {
                packageAppGroupJSON.put("PRIVATE_APP_TYPE", (Object)private_app_type);
            }
            final JSONObject packagePolicyJSON = new JSONObject();
            packagePolicyJSON.put("REMOVE_APP_WITH_PROFILE", true);
            packagePolicyJSON.put("PREVENT_BACKUP", true);
            final JSONObject appPolicyJSON = new JSONObject();
            appPolicyJSON.put("CONFIG_NAME", (Object)"APP_POLICY");
            jsonObject.put("CURRENT_CONFIG", (Object)"APP_POLICY");
            appPolicyJSON.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.MDMDefaultFormBean");
            appPolicyJSON.put("TABLE_NAME", (Object)"InstallAppPolicy");
            final JSONObject appAssignableDetails = new JSONObject();
            if (appDetails.containsKey("appCountDetails") && appDetails.get("appCountDetails").containsKey("DEVICE_ASSIGNABLE")) {
                final String isDeviceAssignable = ((Hashtable<K, Object>)appDetails.get("appCountDetails")).get("DEVICE_ASSIGNABLE").toString();
                appAssignableDetails.put("IS_DEVICE_ASSIGNABLE", (Object)isDeviceAssignable);
                appAssignableDetails.put("APP_ASSIGNABLE_TYPE", appDetails.get("APP_ASSIGNABLE_TYPE"));
                jsonObject.put("MdAppAssignableDetailsForm", (Object)appAssignableDetails);
            }
            jsonObject.put("MdPackageToAppDataFrom", (Object)packageAppDataJSON);
            jsonObject.put("MDPackageToAppGroupForm", (Object)packageAppGroupJSON);
            jsonObject.put("PackagePolicyForm", (Object)packagePolicyJSON);
            jsonObject.put("APP_POLICY", (Object)appPolicyJSON);
            jsonObject.put("PROFILE_ID", -1);
            jsonObject.put("CREATED_BY", (Object)userId);
            jsonObject.put("PROFILE_NAME", appDetails.get("APP_NAME"));
            jsonObject.put("PROFILE_DESCRIPTION", (Object)"");
            jsonObject.put("PROFILE_TYPE", 2);
            jsonObject.put("SECURITY_TYPE", -1);
            jsonObject.put("APP_CONFIG", true);
            jsonObject.put("PACKAGE_ADDED_BY", (Object)userId);
            final String bundledId = appDetailsObject.getBundleId();
            final Boolean isCurrentPackageNew = AppVersionHandler.getInstance(1).isCurrentPackageNewToAppRepo(bundledId, customerID);
            if (isCurrentPackageNew) {
                jsonObject.put("APP_VERSION_STATUS", (Object)AppMgmtConstants.APP_VERSION_APPROVED);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in createiOSAppJson", ex);
        }
        return jsonObject;
    }
    
    public JSONObject createiOSNativeAgentJSON(final long customerID, final long userId) throws SyMException {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("PLATFORM_TYPE", 1);
            jsonObject.put("APP_TITLE", (Object)"");
            jsonObject.put("BUNDLE_IDENTIFIER", (Object)"com.manageengine.mdm.iosagent");
            jsonObject.put("APP_CATEGORY_NAME", (Object)"Business");
            jsonObject.put("COUNTRY_CODE", (Object)"US");
            jsonObject.put("PACKAGE_TYPE", 0);
            jsonObject.put("CUSTOMER_ID", customerID);
            jsonObject.put("PACKAGE_ADDED_BY", userId);
            final AppleAppStoreSearchHandler appSearch = new AppleAppStoreSearchHandler();
            final HashMap appMap = appSearch.getCompleteAppDetails(Integer.parseInt("720111835"), customerID);
            final String appName = appMap.get("trackName");
            if (appName != null) {
                jsonObject.put("APP_NAME", (Object)appName);
            }
            else {
                jsonObject.put("APP_NAME", (Object)"ME MDM");
            }
            final JSONObject packageAppDataJSON = new JSONObject();
            packageAppDataJSON.put("STORE_ID", (Object)"720111835");
            packageAppDataJSON.put("SUPPORTED_DEVICES", 7);
            final String storeUrl = appMap.get("trackViewUrl");
            if (storeUrl != null) {
                packageAppDataJSON.put("STORE_URL", (Object)storeUrl);
            }
            else {
                packageAppDataJSON.put("STORE_URL", (Object)"https://itunes.apple.com/us/app/manageengine-mdm/id720111835?mt=8");
            }
            final String displayImage = appMap.get("artworkUrl100");
            if (displayImage != null) {
                jsonObject.put("DISPLAY_IMAGE_LOC", (Object)displayImage);
            }
            else {
                jsonObject.put("DISPLAY_IMAGE_LOC", (Object)"http://is5.mzstatic.com/image/thumb/Purple69/v4/b0/da/ad/b0daadcc-dbff-dd45-4428-83b7bfebbaf2/source/60x60bb.jpg");
            }
            final String version = appMap.get("version");
            if (version != null) {
                jsonObject.put("APP_VERSION", (Object)version);
            }
            else {
                jsonObject.put("APP_VERSION", (Object)"1.0");
            }
            final JSONObject packageAppGroupJSON = new JSONObject();
            packageAppGroupJSON.put("IS_PAID_APP", false);
            final JSONObject packagePolicyJSON = new JSONObject();
            packagePolicyJSON.put("REMOVE_APP_WITH_PROFILE", true);
            packagePolicyJSON.put("PREVENT_BACKUP", true);
            final JSONObject appPolicyJSON = new JSONObject();
            appPolicyJSON.put("CONFIG_NAME", (Object)"APP_POLICY");
            jsonObject.put("CURRENT_CONFIG", (Object)"APP_POLICY");
            appPolicyJSON.put("BEAN_NAME", (Object)"com.me.mdm.webclient.formbean.MDMDefaultFormBean");
            appPolicyJSON.put("TABLE_NAME", (Object)"InstallAppPolicy");
            jsonObject.put("MdPackageToAppDataFrom", (Object)packageAppDataJSON);
            jsonObject.put("MDPackageToAppGroupForm", (Object)packageAppGroupJSON);
            jsonObject.put("PackagePolicyForm", (Object)packagePolicyJSON);
            jsonObject.put("APP_POLICY", (Object)appPolicyJSON);
            jsonObject.put("PROFILE_ID", -1);
            if (appName != null) {
                jsonObject.put("PROFILE_NAME", (Object)appName);
            }
            else {
                jsonObject.put("PROFILE_NAME", (Object)"ME MDM");
            }
            jsonObject.put("PROFILE_DESCRIPTION", (Object)"iOS Native App");
            jsonObject.put("PROFILE_TYPE", 2);
            jsonObject.put("SECURITY_TYPE", -1);
            jsonObject.put("APP_CONFIG", true);
            final Boolean isCurrentPackageNew = AppVersionHandler.getInstance(1).isCurrentPackageNewToAppRepo("com.manageengine.mdm.iosagent", customerID);
            if (isCurrentPackageNew) {
                jsonObject.put("APP_VERSION_STATUS", (Object)AppMgmtConstants.APP_VERSION_APPROVED);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in createiOSNativeAgentJSON", ex);
        }
        return jsonObject;
    }
    
    public boolean isWpLatestAvailable(final Long customerId) {
        String windowsAgentVersionDB = null;
        boolean isLatestAvailable = false;
        try {
            final String windowsAgentVersionLatest = MDMAgentBuildVersionsUtil.getMDMAgentInfo("windowsagentversion");
            final Long wpNativeAppId = WpCompanyHubAppHandler.getInstance().getWPCompanyHubAppId(customerId);
            final HashMap appsMap = MDMUtil.getInstance().getAppDetails(wpNativeAppId);
            if (appsMap != null) {
                windowsAgentVersionDB = appsMap.get("APP_VERSION");
            }
            MessageProvider.getInstance().hideMessage("NEW_WINDOWS_APP", customerId);
            if (windowsAgentVersionDB != null && !windowsAgentVersionDB.trim().equalsIgnoreCase(windowsAgentVersionLatest.trim())) {
                isLatestAvailable = true;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in isWpLatestAvailable", ex);
        }
        return isLatestAvailable;
    }
    
    public void latestWindowsAppHandling() {
        try {
            final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            if (customerIds != null) {
                for (final Long customerId : customerIds) {
                    this.isWpLatestAvailable(customerId);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in latestWindowsAppHandling", ex);
        }
    }
    
    public HashMap copyAppRepositoryFiles(final String sourceFileName, final String destFilePath, final String destFileDCPath, final boolean isautoRename, final boolean deleteOnCopy) {
        this.logger.log(Level.FINE, "Inside copyAppRepositoryFiles");
        this.logger.log(Level.INFO, "Destination File Path  :  {0}", destFilePath);
        this.logger.log(Level.INFO, "Destination File Path  DC:  {0}", destFileDCPath);
        this.logger.log(Level.INFO, "sourceFileName File Path  DC:  {0}", sourceFileName);
        String destFileName = "";
        String destDCFileName = "";
        final HashMap destFileMap = new HashMap();
        String fileName = "";
        try {
            final File file = new File(sourceFileName);
            final File destFile = new File(destFilePath);
            if (!destFile.exists()) {
                if (!FileFacade.getInstance().testForPathTraversal(destFilePath)) {
                    this.logger.log(Level.SEVERE, "Path traversal character found in destFilePath {0}", new Object[] { destFilePath });
                    throw new SecurityException();
                }
                destFile.mkdirs();
            }
            if (isautoRename) {
                final Calendar cal = Calendar.getInstance();
                fileName = cal.getTimeInMillis() + "_" + file.getName();
            }
            else {
                fileName = file.getName();
            }
            if (fileName != null && !fileName.equalsIgnoreCase("")) {
                destFileName = destFilePath + File.separator + fileName;
                destDCFileName = destFileDCPath + File.separator + fileName;
            }
            if (destFile.exists() && !sourceFileName.equalsIgnoreCase("")) {
                if (ApiFactoryProvider.getFileAccessAPI().isFileExists(sourceFileName)) {
                    final HashMap copyDetails = ApiFactoryProvider.getFileAccessAPI().copyFileWithResponse(sourceFileName, destFileName);
                    destFileMap.put("destDCFile_id", copyDetails.get("file_id"));
                    if (deleteOnCopy) {
                        this.logger.log(Level.INFO, "Going to delete:{0}", file.getParent());
                        ApiFactoryProvider.getFileAccessAPI().deleteDirectory(file.getParent());
                    }
                }
                else {
                    this.logger.log(Level.INFO, "File is not found in location{0} so nopt copying it to {1}", new Object[] { sourceFileName, destDCFileName });
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in copyAppRepositoryFiles...", ex);
        }
        destFileMap.put("destFileName", destFileName);
        destFileMap.put("destDCFileName", destDCFileName);
        return destFileMap;
    }
    
    private DataObject getMdPackageAppGroupDO(final Long packageId, final Long appGroupId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        sQuery.addJoin(new Join("MdPackage", "MdPackagePolicy", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        sQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        final Criteria cPackageId = new Criteria(new Column("MdPackage", "PACKAGE_ID"), (Object)packageId, 0);
        final Criteria cAppGroupId = new Criteria(new Column("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupId, 0);
        sQuery.setCriteria(cPackageId.and(cAppGroupId));
        sQuery.addSelectColumn(new Column("MdPackage", "*"));
        sQuery.addSelectColumn(new Column("MdPackagePolicy", "*"));
        sQuery.addSelectColumn(new Column("MdPackageToAppGroup", "*"));
        final DataObject DO = MDMUtil.getPersistence().get(sQuery);
        return DO;
    }
    
    private DataObject getMdPackageAppDataDO(final Long packageId, final Long appGroupId, final Long appId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        sQuery.addJoin(new Join("MdPackage", "MdPackagePolicy", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        sQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        final Join appDataJoin = new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, new String[] { "PACKAGE_ID", "APP_GROUP_ID" }, 2);
        appDataJoin.setCriteria(new Criteria(new Column("MdPackageToAppData", "APP_ID"), (Object)appId, 0));
        sQuery.addJoin(appDataJoin);
        final Criteria cPackageId = new Criteria(new Column("MdPackage", "PACKAGE_ID"), (Object)packageId, 0);
        final Criteria cAppGroupId = new Criteria(new Column("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupId, 0);
        sQuery.setCriteria(cPackageId.and(cAppGroupId));
        sQuery.addSelectColumn(new Column("MdPackage", "*"));
        sQuery.addSelectColumn(new Column("MdPackagePolicy", "*"));
        sQuery.addSelectColumn(new Column("MdPackageToAppGroup", "*"));
        sQuery.addSelectColumn(new Column("MdPackageToAppData", "*"));
        final DataObject appDo = MDMUtil.getPersistence().get(sQuery);
        return appDo;
    }
    
    public void addOrUpdatePackageInRepository(JSONObject jsonObject) throws Exception {
        this.sanitizeAppRepoInput(jsonObject);
        final Long customerIdFromJSON = JSONUtil.optLong(jsonObject, "CUSTOMER_ID", -1L);
        final String categoryName = jsonObject.optString("APP_CATEGORY_NAME");
        final int platformType = jsonObject.optInt("PLATFORM_TYPE");
        if (categoryName != null && !categoryName.equals("")) {
            final Long categoryId = getInstance().addorUpdateAppCategory(categoryName, platformType);
            jsonObject.put("APP_CATEGORY_ID", (Object)categoryId);
        }
        final String countryCode = jsonObject.optString("COUNTRY_CODE", "US");
        HashMap appRepMap = new HashMap();
        final Iterator keys = jsonObject.keys();
        while (keys.hasNext()) {
            final String keyName = keys.next();
            final Object keyValue = jsonObject.get(keyName);
            if (keyValue != null) {
                appRepMap.put(keyName, keyValue);
            }
        }
        this.logger.log(Level.INFO, "Inside addOrUpdatePackageInRepository: appRepMap = {0}", new Object[] { appRepMap });
        final AppsUtil appsUtil = AppsUtil.getInstance();
        appsUtil.addOrUpdateAppStoreRegionValue(countryCode);
        final boolean multiApp = jsonObject.has("MDAPPS");
        final int appFileCnt = multiApp ? jsonObject.getJSONArray("MDAPPS").length() : 1;
        Long appGroupId = null;
        Boolean isNewVersionAppDetected = Boolean.FALSE;
        Boolean isAppVersionNewToAppRepo = Boolean.FALSE;
        Long userId = jsonObject.optLong("PACKAGE_ADDED_BY", -1L);
        userId = ((userId == -1L) ? ApiFactoryProvider.getAuthUtilAccessAPI().getUserID() : userId);
        userId = ((userId != null && userId != -1L) ? userId : jsonObject.getLong("user_id"));
        final long packageID = jsonObject.optLong("PACKAGE_ID", -1L);
        boolean isPackageDeleted = false;
        final List appIdList = new ArrayList();
        final List appUpdateList = new ArrayList();
        final boolean isNewVersionFoundOnTrack = jsonObject.optBoolean("isNewVersionFoundOnTrack");
        for (int i = 0; i < appFileCnt; ++i) {
            final AppDataHandler appHandler = new AppDataHandler();
            if (multiApp) {
                final JSONObject curApp = (JSONObject)jsonObject.getJSONArray("MDAPPS").get(i);
                appRepMap.put("MdPackageToAppDataFrom", curApp.get("MdPackageToAppDataFrom"));
                appRepMap.put("packageIdentifier", curApp.get("packageIdentifier"));
                appRepMap.put("APP_VERSION", curApp.optString("APP_VERSION", "--"));
                appRepMap.put("APP_NAME_SHORT_VERSION", curApp.optString("APP_NAME_SHORT_VERSION", "--"));
                jsonObject.put("MdPackageToAppDataFrom", curApp.get("MdPackageToAppDataFrom"));
            }
            appRepMap = appHandler.processAppRepositoryData(appRepMap);
            appGroupId = appRepMap.get("APP_GROUP_ID");
            final long appId = appRepMap.get("APP_ID");
            appIdList.add(appId);
            final Long customerId = appRepMap.get("CUSTOMER_ID");
            final Long oldCollectionId = appsUtil.getCollectionFromIdentifierForCloningAppConfigurationAndPermissions(appRepMap.get("BUNDLE_IDENTIFIER"), platformType, customerId);
            jsonObject.put("oldCollectionId", (Object)oldCollectionId);
            final Boolean isCurrentPackageNewToAppRepo = !this.isAppExistsInCurrentLivePackage(appId);
            final Boolean isCurrentPackageNew = !this.isAppExistsInPackage(appId);
            if (isCurrentPackageNew) {
                appUpdateList.add(appId);
            }
            isNewVersionAppDetected = isCurrentPackageNew;
            isAppVersionNewToAppRepo = (isCurrentPackageNewToAppRepo || isNewVersionFoundOnTrack);
            if (packageID == -1L) {
                final AppTrashModeHandler appTrashModeHandler = new AppTrashModeHandler();
                isPackageDeleted = appTrashModeHandler.getDeletedAppPackageDetails(appGroupId, jsonObject);
                if (!isPackageDeleted) {
                    this.pickProfileIDForAppGroup(appGroupId, jsonObject);
                    this.logger.log(Level.INFO, "Picked Package ID :{0}, appGroupID: {1}, profileID: {2}", new Object[] { jsonObject.optLong("PACKAGE_ID", -1L), appGroupId, jsonObject.optLong("PROFILE_ID", -1L) });
                }
                else {
                    this.logger.log(Level.INFO, "App: {0} is already in trash", new Object[] { appGroupId });
                    final Long customerID = (Long)jsonObject.opt("CUSTOMER_ID");
                    if (customerID != null) {
                        appTrashModeHandler.incrementTrashAppAddCount(customerID);
                    }
                }
            }
            String appName = appRepMap.get("APP_NAME");
            if (appName.length() > 100) {
                appName = appName.substring(0, 99);
            }
            final Boolean isNativeAgent = jsonObject.optBoolean("IS_NATIVE_AGENT", (boolean)Boolean.FALSE);
            jsonObject.put("PACKAGE_ADDED_BY", (Object)userId);
            final Long loggedinUser = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final Long lastModifiedBy = (loggedinUser != null && loggedinUser != -1L) ? loggedinUser : userId;
            jsonObject.put("LAST_MODIFIED_BY", (Object)lastModifiedBy);
            jsonObject.put("PROFILE_NAME", (Object)appName);
            if (isNativeAgent) {
                jsonObject.put("PROFILE_TYPE", 7);
            }
            else {
                jsonObject.put("PROFILE_TYPE", 2);
            }
            jsonObject.put("isNewVersionAppDetected", (Object)isCurrentPackageNew);
            jsonObject.put("APP_ID", appId);
            jsonObject.put("APP_GROUP_ID", (Object)appGroupId);
            try {
                if (isNewVersionAppDetected) {
                    jsonObject = BaseAppAdditionDataProvider.getInstance(platformType).modifyAppAdditionData(jsonObject);
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception when requesting additional data for app addition", ex);
            }
            this.addOrModifyPackage(jsonObject);
            appsUtil.updateSyncTimeforApp(appId);
        }
        final boolean restoreDeleted = jsonObject.optBoolean("doNotRestore", (boolean)Boolean.FALSE);
        jsonObject.put("AppIDList", (Object)new JSONArray((Collection)appIdList));
        jsonObject.put("CREATED_BY", (Object)userId);
        jsonObject.put("isNewVersionAppDetected", (Object)isNewVersionAppDetected);
        this.logger.log(Level.INFO, "If isNewVersionAppDetected : {0}", new Object[] { isNewVersionAppDetected });
        jsonObject.put("isPackageDeleted", isPackageDeleted);
        jsonObject.put("IS_MOVED_TO_TRASH", isPackageDeleted && restoreDeleted);
        this.addOrModifyAppProfileCollection(jsonObject);
        jsonObject.put("newCollectionId", jsonObject.optLong("COLLECTION_ID", -1L));
        this.logger.log(Level.INFO, "Updated  CollectionID: {0} for AppIDs: {1} of AppGroup: {2}", new Object[] { jsonObject.optLong("COLLECTION_ID"), appIdList, appGroupId });
        ProfileConfigHandler.addOrModifyConfiguration(jsonObject);
        AppsUtil.addOrUpdateAppToCollectionRelation(appIdList, jsonObject.getLong("COLLECTION_ID"));
        final Long appReleaseLabelID = BaseAppAdditionDataProvider.getInstance(platformType).getReleaseLabel(jsonObject);
        final JSONObject appReleaseHistoryTableData = new JSONObject();
        appReleaseHistoryTableData.put("COLLECTION_ID", jsonObject.getLong("COLLECTION_ID"));
        appReleaseHistoryTableData.put("RELEASE_LABEL_ID", (Object)appReleaseLabelID);
        appReleaseHistoryTableData.put("USER_ID", (Object)userId);
        final Integer appVersionStatus = jsonObject.optInt("APP_VERSION_STATUS", -1);
        AppsUtil.addOrUpdateAppCollectionToReleaseLabelHistory(appReleaseHistoryTableData);
        AppsUtil.addOrUpdateAppGrpToCollectionRelation(appGroupId, jsonObject.getLong("COLLECTION_ID"), appReleaseLabelID, appVersionStatus);
        if (jsonObject.has("RELEASE_LABEL_DISPLAY_NAME")) {
            AppVersionDBUtil.getInstance().updateChannel(appReleaseLabelID, jsonObject.getString("RELEASE_LABEL_DISPLAY_NAME"));
        }
        final Long appId2 = AppVersionDBUtil.getInstance().getAppIdFromConfigDataItemId(jsonObject.getLong("CONFIG_DATA_ITEM_ID"));
        final Boolean isSameVersionReUpload = jsonObject.optBoolean("hasAppFile", (boolean)Boolean.FALSE) && !isAppVersionNewToAppRepo;
        if (isAppVersionNewToAppRepo || isSameVersionReUpload) {
            appsUtil.setAppUpdateForApps(appGroupId, appReleaseLabelID, isSameVersionReUpload);
            if (isAppVersionNewToAppRepo) {
                appsUtil.setApprovedAppIdForResource(appReleaseLabelID, appGroupId, appId2);
            }
        }
        new AppDependencyHandler(platformType).handleDependencyMapping(jsonObject);
        this.updatePlatformSpecificTableForApp(jsonObject, platformType);
        jsonObject.put("LAST_MODIFIED_BY", (Object)userId);
        jsonObject.put("AppConfigTemplate.APP_ID", (Object)appId2);
        if (!jsonObject.has("APP_CONFIG_TEMPLATE_ID")) {
            jsonObject.put("APP_CONFIG_TEMPLATE_ID", (Object)new AppConfigDataHandler().getAppConfigTemplateIDFromConfigDataItemID(jsonObject.getLong("CONFIG_DATA_ITEM_ID")));
        }
        new MDMAppAuxiliaryDetailsHandler().addAuxillaryDetails(jsonObject, appGroupId, userId, appIdList);
        jsonObject.put("IS_MOVED_TO_TRASH", isPackageDeleted && restoreDeleted);
        jsonObject.put("RELEASE_LABEL_ID", (Object)appReleaseLabelID);
        ProfileConfigHandler.publishProfile(jsonObject);
        if (jsonObject.has("applicable_versions")) {
            new AppTrackUtil().fillLatestApplicableTrackVersions(jsonObject.optLong("PACKAGE_ID"), appGroupId, jsonObject.getLong("CUSTOMER_ID"), jsonObject.getJSONArray("applicable_versions"), appReleaseLabelID);
        }
        MDMMessageHandler.getInstance().messageAction("NO_APP_ADDED", jsonObject.optLong("CUSTOMER_ID"));
    }
    
    public void addOrModifyAppProfileCollection(final JSONObject jsonObject) throws SyMException, Exception {
        try {
            final Long profileID = jsonObject.optLong("PROFILE_ID", -1L);
            if (profileID == -1L) {
                ProfileConfigHandler.addProfileCollection(jsonObject);
            }
            else {
                this.modifyAppProfileCollection(jsonObject);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception of addOrModifyAppProfileCollection", e);
            throw e;
        }
    }
    
    private void modifyAppProfileCollection(final JSONObject jsonObject) throws JSONException, Exception {
        final Long appID = jsonObject.getLong("APP_ID");
        Long collectionID = (Long)DBUtil.getValueFromDB("MdAppToCollection", "APP_ID", (Object)appID, "COLLECTION_ID");
        final Long modifiedUserID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        jsonObject.put("LAST_MODIFIED_BY", (Object)modifiedUserID);
        final AppTrashModeHandler appTrashModeHandler = new AppTrashModeHandler();
        Long profileID = jsonObject.getLong("PROFILE_ID");
        jsonObject.put("IS_MOVED_TO_TRASH", appTrashModeHandler.isAppMovedToTrash(profileID));
        profileID = ProfileHandler.addOrUpdateProfile(jsonObject);
        this.logger.log(Level.INFO, "Inside modifyAppProfileCollection: appID: {0}, profileID: {1}, collectionID: {2}", new Object[] { appID, profileID, collectionID });
        if (jsonObject.optBoolean("isNewVersionAppDetected")) {
            jsonObject.remove("COLLECTION_ID");
            final Long clonedCollectionID = ProfileHandler.addOrUpdateProfileCollectionDO(jsonObject);
            ProfileConfigHandler.cloneConfigurations(collectionID, clonedCollectionID);
            jsonObject.put("COLLECTION_ID", (Object)clonedCollectionID);
        }
        else {
            jsonObject.put("COLLECTION_ID", (Object)collectionID);
        }
        collectionID = (Long)jsonObject.get("COLLECTION_ID");
        final HashMap configuredStatus = ProfileConfigHandler.getConfiguredStatus(collectionID);
        final JSONObject configuredStatusJSON = new JSONObject((Map)configuredStatus);
        jsonObject.put("configuredStatus", (Object)configuredStatusJSON);
    }
    
    public void addOrModifyPackage(final JSONObject jsonObject) throws Exception {
        final MDMTransactionManager mdmTransactionManager = new MDMTransactionManager();
        try {
            mdmTransactionManager.begin();
            DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
            Long packageId = jsonObject.optLong("PACKAGE_ID", -1L);
            final Long appGroupId = jsonObject.getLong("APP_GROUP_ID");
            final Long appId = jsonObject.getLong("APP_ID");
            final Integer appSharedScope = jsonObject.optInt("APP_SHARED_SCOPE", 0);
            Long updatedAppGroupId = null;
            final boolean isNewVersionAppDetected = jsonObject.optBoolean("isNewVersionAppDetected", false);
            this.logger.log(Level.INFO, "Inside addOrModifyPackage - appID: {0}, appGroupID:{1}, packageID: {2}", new Object[] { appId, appGroupId, packageId });
            JSONObject packagePolicyJSON = jsonObject.optJSONObject("PackagePolicyForm");
            JSONObject packageAppDataJSON = jsonObject.optJSONObject("MdPackageToAppDataFrom");
            JSONObject packageAppGroupJSON = jsonObject.optJSONObject("MDPackageToAppGroupForm");
            packageAppGroupJSON = ((packageAppGroupJSON == null) ? new JSONObject() : packageAppGroupJSON);
            packageAppDataJSON = ((packageAppDataJSON == null) ? new JSONObject() : packageAppDataJSON);
            packagePolicyJSON = ((packagePolicyJSON == null) ? new JSONObject() : packagePolicyJSON);
            final boolean isPaidApp = packageAppGroupJSON.optBoolean("IS_PAID_APP", false);
            final int platformType = (int)jsonObject.get("PLATFORM_TYPE");
            final boolean isPurchasedFromPortal = packageAppGroupJSON.optBoolean("IS_PURCHASED_FROM_PORTAL", false);
            this.logger.log(Level.INFO, "MDMAppMgmtHandler:addOrModifyPackage() package ID {0}", packageId);
            if (packageId < 0L) {
                final Row packageRow = new Row("MdPackage");
                packageRow.set("CUSTOMER_ID", (Object)jsonObject.optLong("CUSTOMER_ID"));
                packageRow.set("PACKAGE_ADDED_BY", (Object)jsonObject.getLong("PACKAGE_ADDED_BY"));
                packageRow.set("PACKAGE_MODIFIED_BY", (Object)jsonObject.getLong("PACKAGE_ADDED_BY"));
                packageRow.set("PLATFORM_TYPE", (Object)platformType);
                packageRow.set("PACKAGE_ADDED_TIME", (Object)System.currentTimeMillis());
                packageRow.set("PACKAGE_MODIFIED_TIME", (Object)System.currentTimeMillis());
                packageRow.set("APP_SHARED_SCOPE", (Object)appSharedScope);
                dataObject.addRow(packageRow);
                final Row packageToAppGroupRow = new Row("MdPackageToAppGroup");
                packageToAppGroupRow.set("APP_GROUP_ID", (Object)appGroupId);
                packageToAppGroupRow.set("PACKAGE_ID", packageRow.get("PACKAGE_ID"));
                packageToAppGroupRow.set("PACKAGE_TYPE", (Object)jsonObject.getInt("PACKAGE_TYPE"));
                packageToAppGroupRow.set("IS_PAID_APP", (Object)isPaidApp);
                packageToAppGroupRow.set("IS_PURCHASED_FROM_PORTAL", (Object)packageAppGroupJSON.optBoolean("IS_PURCHASED_FROM_PORTAL", false));
                packageToAppGroupRow.set("PRIVATE_APP_TYPE", (Object)packageAppGroupJSON.optInt("PRIVATE_APP_TYPE", 0));
                dataObject.addRow(packageToAppGroupRow);
                final Row packagePolicyRow = new Row("MdPackagePolicy");
                packagePolicyRow.set("PACKAGE_ID", packageRow.get("PACKAGE_ID"));
                packagePolicyRow.set("REMOVE_APP_WITH_PROFILE", (Object)packagePolicyJSON.optBoolean("REMOVE_APP_WITH_PROFILE", false));
                packagePolicyRow.set("PREVENT_BACKUP", (Object)packagePolicyJSON.optBoolean("PREVENT_BACKUP", false));
                dataObject.addRow(packagePolicyRow);
                final Row packageToAppDataRow = new Row("MdPackageToAppData");
                packageToAppDataRow.set("APP_GROUP_ID", (Object)appGroupId);
                packageToAppDataRow.set("PACKAGE_ID", packageRow.get("PACKAGE_ID"));
                packageToAppDataRow.set("APP_ID", (Object)appId);
                packageToAppDataRow.set("SUPPORTED_DEVICES", (Object)packageAppDataJSON.optInt("SUPPORTED_DEVICES"));
                packageToAppDataRow.set("MIN_OS", (Object)packageAppDataJSON.optString("MIN_OS"));
                packageToAppDataRow.set("SUPPORTED_ARCH", (Object)new Long(packageAppDataJSON.optString("SUPPORTED_ARCH", MDMCommonConstants.ALL_ARCHITECTURE.toString())));
                final Long storeId = packageAppDataJSON.optLong("STORE_ID");
                packageToAppDataRow.set("STORE_ID", (Object)storeId.toString());
                packageToAppDataRow.set("STORE_URL", (Object)packageAppDataJSON.optString("STORE_URL"));
                packageToAppDataRow.set("DESCRIPTION", (Object)packageAppDataJSON.optString("DESCRIPTION"));
                packageToAppDataRow.set("COMMAND_LINE", (Object)packageAppDataJSON.optString("COMMAND_LINE"));
                packageToAppDataRow.set("FILE_UPLOAD_SIZE", (Object)JSONUtil.optLong(packageAppDataJSON, "FILE_UPLOAD_SIZE", -1L));
                packageToAppDataRow.set("CUSTOMIZED_APP_URL", (Object)jsonObject.optString("CUSTOMIZED_APP_URL"));
                packageToAppDataRow.set("APP_CHECKSUM", (Object)packageAppDataJSON.optString("APP_CHECKSUM"));
                dataObject.addRow(packageToAppDataRow);
            }
            else {
                if (isNewVersionAppDetected) {
                    this.logger.log(Level.INFO, "MDMAppMgmtHandler:addOrModifyPackage() New version of app detected");
                    dataObject = this.getMdPackageAppGroupDO(packageId, appGroupId);
                    final Row mdPackageToAppGroupRow = dataObject.getRow("MdPackageToAppGroup");
                    mdPackageToAppGroupRow.set("IS_PAID_APP", (Object)isPaidApp);
                    mdPackageToAppGroupRow.set("PRIVATE_APP_TYPE", (Object)packageAppGroupJSON.optInt("PRIVATE_APP_TYPE", 0));
                    if (platformType == 1) {
                        final int oldAppType = this.getAppType((int)mdPackageToAppGroupRow.get("PACKAGE_TYPE"), platformType);
                        final int newAppType = this.getAppType(jsonObject.getInt("PACKAGE_TYPE"), platformType);
                        if (oldAppType != newAppType && oldAppType == 1 && newAppType == 0) {
                            this.logger.log(Level.INFO, "Enterprise App Type is converted to store App type");
                            mdPackageToAppGroupRow.set("PACKAGE_TYPE", (Object)jsonObject.getInt("PACKAGE_TYPE"));
                        }
                    }
                    dataObject.updateRow(mdPackageToAppGroupRow);
                    final Row packageToAppDataRow2 = new Row("MdPackageToAppData");
                    packageToAppDataRow2.set("APP_GROUP_ID", (Object)appGroupId);
                    packageToAppDataRow2.set("PACKAGE_ID", (Object)packageId);
                    packageToAppDataRow2.set("APP_ID", (Object)appId);
                    packageToAppDataRow2.set("SUPPORTED_DEVICES", (Object)packageAppDataJSON.optInt("SUPPORTED_DEVICES"));
                    final Long storeId2 = packageAppDataJSON.optLong("STORE_ID");
                    packageToAppDataRow2.set("STORE_ID", (Object)storeId2.toString());
                    packageToAppDataRow2.set("STORE_URL", (Object)packageAppDataJSON.optString("STORE_URL"));
                    packageToAppDataRow2.set("DESCRIPTION", (Object)packageAppDataJSON.optString("DESCRIPTION"));
                    packageToAppDataRow2.set("MIN_OS", (Object)packageAppDataJSON.optString("MIN_OS"));
                    packageToAppDataRow2.set("SUPPORTED_ARCH", (Object)new Long(packageAppDataJSON.optString("SUPPORTED_ARCH", MDMCommonConstants.ALL_ARCHITECTURE.toString())));
                    packageToAppDataRow2.set("COMMAND_LINE", (Object)packageAppDataJSON.optString("COMMAND_LINE"));
                    packageToAppDataRow2.set("FILE_UPLOAD_SIZE", (Object)JSONUtil.optLong(packageAppDataJSON, "FILE_UPLOAD_SIZE", -1L));
                    packageToAppDataRow2.set("CUSTOMIZED_APP_URL", (Object)jsonObject.optString("CUSTOMIZED_APP_URL"));
                    packageToAppDataRow2.set("APP_CHECKSUM", (Object)packageAppDataJSON.optString("APP_CHECKSUM"));
                    updatedAppGroupId = appGroupId;
                    dataObject.addRow(packageToAppDataRow2);
                }
                else {
                    dataObject = this.getMdPackageAppDataDO(packageId, appGroupId, appId);
                    final Row packageToAppDataRow3 = dataObject.getRow("MdPackageToAppData");
                    packageToAppDataRow3.set("STORE_URL", (Object)packageAppDataJSON.optString("STORE_URL"));
                    packageToAppDataRow3.set("SUPPORTED_DEVICES", (Object)packageAppDataJSON.optInt("SUPPORTED_DEVICES"));
                    packageToAppDataRow3.set("MIN_OS", (Object)packageAppDataJSON.optString("MIN_OS"));
                    packageToAppDataRow3.set("SUPPORTED_ARCH", (Object)new Long(packageAppDataJSON.optString("SUPPORTED_ARCH", MDMCommonConstants.ALL_ARCHITECTURE.toString())));
                    packageToAppDataRow3.set("SUPPORTED_DEVICES", (Object)packageAppDataJSON.optInt("SUPPORTED_DEVICES"));
                    final Long storeId3 = packageAppDataJSON.optLong("STORE_ID");
                    final String storeIDStr = storeId3.toString();
                    final String oldStoreId = (String)packageToAppDataRow3.get("STORE_ID");
                    if (oldStoreId == null || !oldStoreId.equals(storeIDStr)) {
                        this.logger.log(Level.INFO, "Store_ID: Update: OldStoreID = {0}, newStoreID = {1}, packageID = {1}", new Object[] { oldStoreId, storeIDStr, packageId });
                        packageToAppDataRow3.set("STORE_ID", (Object)storeIDStr);
                    }
                    packageToAppDataRow3.set("DESCRIPTION", (Object)packageAppDataJSON.optString("DESCRIPTION"));
                    packageToAppDataRow3.set("COMMAND_LINE", (Object)packageAppDataJSON.optString("COMMAND_LINE"));
                    packageToAppDataRow3.set("CUSTOMIZED_APP_URL", (Object)jsonObject.optString("CUSTOMIZED_APP_URL"));
                    packageToAppDataRow3.set("APP_CHECKSUM", (Object)packageAppDataJSON.optString("APP_CHECKSUM"));
                    dataObject.updateRow(packageToAppDataRow3);
                    final Row mdPackageToAppGroupRow2 = dataObject.getRow("MdPackageToAppGroup");
                    mdPackageToAppGroupRow2.set("IS_PAID_APP", (Object)isPaidApp);
                    mdPackageToAppGroupRow2.set("PRIVATE_APP_TYPE", (Object)packageAppGroupJSON.optInt("PRIVATE_APP_TYPE", 0));
                    dataObject.updateRow(mdPackageToAppGroupRow2);
                }
                final Row appGrpRow = dataObject.getRow("MdPackageToAppGroup");
                appGrpRow.set("IS_PURCHASED_FROM_PORTAL", (Object)isPurchasedFromPortal);
                final Integer packageType = (Integer)jsonObject.opt("PACKAGE_TYPE");
                if (packageType != null) {
                    appGrpRow.set("PACKAGE_TYPE", (Object)packageType);
                }
                dataObject.updateRow(appGrpRow);
                final Row packageRow2 = dataObject.getRow("MdPackage");
                packageRow2.set("PACKAGE_MODIFIED_BY", (Object)jsonObject.getLong("PACKAGE_ADDED_BY"));
                packageRow2.set("PACKAGE_MODIFIED_TIME", (Object)new Long(System.currentTimeMillis()));
                dataObject.updateRow(packageRow2);
                final Row packagePolicyRow2 = dataObject.getRow("MdPackagePolicy");
                packagePolicyRow2.set("PACKAGE_ID", (Object)packageId);
                packagePolicyRow2.set("REMOVE_APP_WITH_PROFILE", (Object)packagePolicyJSON.optBoolean("REMOVE_APP_WITH_PROFILE", false));
                packagePolicyRow2.set("PREVENT_BACKUP", (Object)packagePolicyJSON.optBoolean("PREVENT_BACKUP", false));
                dataObject.updateRow(packagePolicyRow2);
            }
            MDMUtil.getPersistence().update(dataObject);
            if (packageId < 0L) {
                packageId = (Long)dataObject.getFirstValue("MdPackage", "PACKAGE_ID");
                jsonObject.put("PACKAGE_ID", (Object)packageId);
            }
            if (jsonObject.opt("metaData") != null && (int)jsonObject.get("PLATFORM_TYPE") == 1) {
                new IOSAppDatahandler(jsonObject).saveMetaData(jsonObject);
            }
            final Boolean isPortalApp = (Boolean)dataObject.getFirstValue("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL");
            this.handleAppRepositoryFile(jsonObject);
            final String appFileLoc = jsonObject.optString("APP_FILE_LOC");
            String displayImageLoc = jsonObject.optString("DISPLAY_IMAGE_LOC");
            String fullImageLoc = jsonObject.optString("FULL_IMAGE_LOC");
            final String manifestLoc = jsonObject.optString("MANIFEST_FILE_URL");
            final Boolean isDisplayImageDeleted = jsonObject.optBoolean("isDisplayImageDelete", false);
            if (isDisplayImageDeleted) {
                displayImageLoc = "";
            }
            final Row mdpackagetoAppRow = dataObject.getFirstRow("MdPackageToAppData");
            if (appFileLoc != null && !appFileLoc.equals("")) {
                mdpackagetoAppRow.set("APP_FILE_LOC", (Object)appFileLoc);
            }
            if ((displayImageLoc != null && !displayImageLoc.equals("")) || isDisplayImageDeleted) {
                mdpackagetoAppRow.set("DISPLAY_IMAGE_LOC", (Object)displayImageLoc);
            }
            if (fullImageLoc == null || fullImageLoc.equals("")) {
                fullImageLoc = packageAppDataJSON.optString("DISPLAY_IMAGE_DOWNLOAD_URL", "");
            }
            if (fullImageLoc != null && !fullImageLoc.equals("")) {
                mdpackagetoAppRow.set("FULL_IMAGE_LOC", (Object)fullImageLoc);
            }
            if (manifestLoc != null && !manifestLoc.equals("")) {
                mdpackagetoAppRow.set("MANIFEST_FILE_URL", (Object)manifestLoc);
            }
            dataObject.updateRow(mdpackagetoAppRow);
            MDMUtil.getPersistence().update(dataObject);
            MDMRestAPIFactoryProvider.getMdmAppMgmtHandlerAPI().updateAppFileDetails(jsonObject);
            final Row mdPackage = dataObject.getFirstRow("MdPackage");
            JSONObject appPolicyJSON = jsonObject.optJSONObject("APP_POLICY");
            if (appPolicyJSON == null) {
                appPolicyJSON = new JSONObject();
            }
            appPolicyJSON.put("PACKAGE_ID", mdPackage.get("PACKAGE_ID"));
            appPolicyJSON.put("APP_ID", (Object)appId);
            appPolicyJSON.put("APP_GROUP_ID", (Object)appGroupId);
            appPolicyJSON.put("CONFIG_ID", 301);
            appPolicyJSON.put("CONFIG_NAME", (Object)"APP_POLICY");
            appPolicyJSON.put("CONFIG_TYPE", 3);
            jsonObject.put("APP_POLICY", (Object)appPolicyJSON);
            if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AutoUpdateForAllApps")) {
                final Integer packageType2 = (Integer)jsonObject.opt("PACKAGE_TYPE");
                if (platformType == 1) {
                    if (packageType2 != 1 && packageType2 != 0) {
                        updatedAppGroupId = null;
                    }
                }
                else if (!isPortalApp) {
                    updatedAppGroupId = null;
                }
            }
            jsonObject.put("updatedAppGroupId", (Object)updatedAppGroupId);
            jsonObject.put("PACKAGE_ID", (Object)packageId);
            jsonObject.put("IS_PURCHASED_FROM_PORTAL", isPurchasedFromPortal);
            mdmTransactionManager.commit();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in addOrModifyPackage", ex);
            try {
                mdmTransactionManager.rollBack();
            }
            catch (final Exception exception) {
                this.logger.log(Level.SEVERE, "Exception in transaction rollback in addOrModifyPackage", exception);
            }
            this.logger.log(Level.INFO, "Transaction inside addOrModifyPackage roll backed {0}", new Object[] { mdmTransactionManager.isMDMTransaction() });
            throw ex;
        }
    }
    
    protected void updateAppFileDetails(final JSONObject jsonObject) {
    }
    
    public void handleAppRepositoryFile(final JSONObject jsonObject) throws Exception {
        final Long customerId = (Long)jsonObject.get("CUSTOMER_ID");
        final Long appId = (Long)jsonObject.get("APP_ID");
        final Long packageId = (Long)jsonObject.get("PACKAGE_ID");
        this.logger.log(Level.INFO, "Inside handleAppRepositoryFile for appID: {0} of package: {1}", new Object[] { appId, packageId });
        final String appRepositoryFilePath = this.getAppRepositoryFolderPath(customerId, packageId, appId);
        final String destFileDCPath = this.getAppRepositoryFolderDBPath(customerId, packageId, appId);
        final String appRepFullPath = this.getAppRepositoryFullPath(customerId, packageId, appId);
        String appLocStr = null;
        String displayImageStr = null;
        String fullImageStr = null;
        final Boolean isAppUpgrade = jsonObject.optBoolean("isNewVersionAppDetected", false);
        final JSONObject packageAppDataJSON = jsonObject.optJSONObject("MdPackageToAppDataFrom");
        final String appLocSrc = jsonObject.optString("APP_FILE");
        final HashMap hm = new HashMap();
        hm.put("IS_SERVER", false);
        hm.put("IS_AUTHTOKEN", true);
        if (appLocSrc != null) {
            final File f = new File(appLocSrc);
            appLocStr = f.getName();
            if (appLocStr != null && !appLocStr.equals("")) {
                this.logger.log(Level.INFO, "Copying app file from App Source path to final app repository destination");
                final Long appSourceFileSize = ApiFactoryProvider.getFileAccessAPI().getFileSize(appLocSrc);
                this.logger.log(Level.INFO, "FILESIZELOG: MDMAppMgmtHandler: handleAppRepositoryFile: AppSourceFileSize: {0}", appSourceFileSize);
                final HashMap fileMap = this.copyAppRepositoryFiles(appLocSrc, appRepositoryFilePath, destFileDCPath, false, true);
                jsonObject.put("APP_FILE_LOC", fileMap.get("destDCFileName"));
                jsonObject.put("APP_FILE_ID", fileMap.get("destDCFile_id"));
                appLocStr = appRepFullPath + "/" + appLocStr;
                hm.put("path", appLocStr);
                appLocStr = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthTokenAndUDID(hm);
                final String finalAppPath = this.getAppRepositoryBaseFolderPath() + fileMap.get("destDCFileName");
                final Long appLocFileSize = ApiFactoryProvider.getFileAccessAPI().getFileSize(finalAppPath);
                this.logger.log(Level.INFO, "IPAExtraction-Case0 IPA FileExtracted based on APP_FILE : {0}", appLocStr);
                this.logger.log(Level.INFO, "FILESIZELOG: MDMAppMgmtHandler: handleAppRepositoryFile: FinalAppLocFileSize: {0}", appLocFileSize);
                if (!appLocFileSize.equals(appSourceFileSize)) {
                    this.logger.log(Level.WARNING, "FILESIZELOG: MDMAppMgmtHandler: handleAppRepositoryFile: ***File Size Differs*** -> AppSourceFileSize: {0} FinalAppLocFileSize: {1}", new Object[] { appSourceFileSize, appLocFileSize });
                }
                if (jsonObject.has("ATTACH_ID") && (long)jsonObject.get("ATTACH_ID") > 0L) {
                    final long attachId = (long)jsonObject.get("ATTACH_ID");
                    if (MDMApiFactoryProvider.getCloudFileStorageAPI() != null) {
                        MDMApiFactoryProvider.getCloudFileStorageAPI().updateCloudFileStorageDetails(fileMap, attachId);
                    }
                    this.logger.log(Level.INFO, "IPAExtraction-Case1 IPA FileExtracted based on APP_FILE : {0}", appLocStr);
                }
            }
            else {
                appLocStr = packageAppDataJSON.optString("APP_FILE_LOC");
                if (appLocStr != null && !appLocStr.equals("")) {
                    appLocStr = appLocStr.replace("\\", "/");
                    hm.put("path", appLocStr);
                    appLocStr = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthTokenAndUDID(hm);
                    appLocStr = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + appLocStr;
                    this.logger.log(Level.INFO, "IPAExtraction-Case2 FileExtracted based on APP_FILE_LOC : {0}", appLocStr);
                }
            }
            String displayImageSrc = jsonObject.optString("DISPLAY_IMAGE");
            if (displayImageSrc != null) {
                displayImageStr = ApiFactoryProvider.getFileAccessAPI().getFileName(displayImageSrc);
                if (displayImageStr != null && !displayImageStr.equals("")) {
                    final HashMap fileMap = this.copyAppRepositoryFiles(displayImageSrc, appRepositoryFilePath, destFileDCPath, false, true);
                    jsonObject.put("DISPLAY_IMAGE_LOC", fileMap.get("destDCFileName"));
                    jsonObject.put("DISPLAY_IMAGE_ID", fileMap.get("destDCFile_id"));
                    displayImageStr = appRepFullPath + "/" + displayImageStr;
                    hm.put("path", displayImageStr);
                    displayImageStr = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                    this.logger.log(Level.INFO, "IPAExtraction-Case3 DisplayImage  Constructed based on  DISPLAY_IMAGE : {0}", displayImageStr);
                }
                else if (!jsonObject.optString("DISPLAY_IMAGE_IN_IPA").equals("")) {
                    displayImageSrc = jsonObject.optString("DISPLAY_IMAGE_IN_IPA");
                    displayImageStr = ApiFactoryProvider.getFileAccessAPI().getFileName(displayImageSrc);
                    if (displayImageStr != null && !displayImageStr.equals("") && ApiFactoryProvider.getFileAccessAPI().isFileExists(displayImageSrc)) {
                        final HashMap fileMap = this.copyAppRepositoryFiles(displayImageSrc, appRepositoryFilePath, destFileDCPath, false, false);
                        jsonObject.put("DISPLAY_IMAGE_LOC", fileMap.get("destDCFileName"));
                        jsonObject.put("DISPLAY_IMAGE_ID", fileMap.get("destDCFile_id"));
                        displayImageStr = appRepFullPath + "/" + displayImageStr;
                        hm.put("path", displayImageStr);
                        displayImageStr = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                        this.logger.log(Level.INFO, "IPAExtraction-Case4 DisplayImage  Constructed based on  DISPLAY_IMAGE_IN_IPA : {0}", displayImageStr);
                    }
                }
                else if (isAppUpgrade) {
                    displayImageStr = packageAppDataJSON.optString("DISPLAY_IMAGE_LOC");
                    if (displayImageStr != null && !displayImageStr.equals("") && !displayImageStr.equals("NotAvailable")) {
                        displayImageStr = this.getAppRepositoryBaseFolderPath() + displayImageStr;
                        final String fileName = ApiFactoryProvider.getFileAccessAPI().getFileNameFromFilePath(displayImageStr);
                        final HashMap fileDetails = ApiFactoryProvider.getFileAccessAPI().copyFileWithResponse(displayImageStr, appRepositoryFilePath + File.separator + fileName);
                        displayImageStr = appRepFullPath + "/" + fileName;
                        jsonObject.put("DISPLAY_IMAGE_LOC", (Object)(destFileDCPath + File.separator + fileName));
                        jsonObject.put("DISPLAY_IMAGE_ID", fileDetails.get("file_id"));
                        hm.put("path", displayImageStr);
                        displayImageStr = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                        this.logger.log(Level.INFO, "IPAExtraction-Case5 DisplayImage  Constructed based on  DISPLAY_IMAGE_LOC (AppUpgrade) : {0}", displayImageStr);
                    }
                }
                else {
                    displayImageStr = packageAppDataJSON.optString("DISPLAY_IMAGE_LOC");
                    if (displayImageStr != null && !displayImageStr.equals("")) {
                        displayImageStr = displayImageStr.replace("\\", "/");
                        hm.put("path", displayImageStr);
                        displayImageStr = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                        displayImageStr = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + displayImageStr;
                        this.logger.log(Level.INFO, "IPAExtraction-Case6 DisplayImage  Constructed based on  DISPLAY_IMAGE_LOC (NotAppUpgrade): {0}", displayImageStr);
                    }
                }
            }
            String fullImageSource = jsonObject.optString("FULL_IMAGE");
            if (fullImageSource != null) {
                File fullImageFile = new File(fullImageSource);
                fullImageStr = fullImageFile.getName();
                if (fullImageStr != null && !fullImageStr.equals("")) {
                    final HashMap fileMap2 = this.copyAppRepositoryFiles(fullImageSource, appRepositoryFilePath, destFileDCPath, false, true);
                    jsonObject.put("FULL_IMAGE_LOC", fileMap2.get("destDCFileName"));
                    jsonObject.put("FULL_IMAGE_ID", fileMap2.get("destDCFile_id"));
                    fullImageStr = appRepFullPath + "/" + fullImageStr;
                    hm.put("path", fullImageStr);
                    fullImageStr = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                    this.logger.log(Level.INFO, "IPAExtraction-Case7 FullImage  Constructed based on  FULL_IMAGE :{0}", fullImageStr);
                }
                else if (!jsonObject.optString("FULL_IMAGE_IN_IPA").equals("")) {
                    fullImageSource = jsonObject.optString("FULL_IMAGE_IN_IPA");
                    fullImageFile = new File(fullImageSource);
                    fullImageStr = fullImageFile.getName();
                    if (fullImageStr != null && !fullImageStr.equals("") && fullImageSource != null) {
                        final HashMap fileMap2 = this.copyAppRepositoryFiles(fullImageSource, appRepositoryFilePath, destFileDCPath, false, true);
                        jsonObject.put("FULL_IMAGE_LOC", fileMap2.get("destDCFileName"));
                        jsonObject.put("FULL_IMAGE_ID", fileMap2.get("destDCFile_id"));
                        fullImageStr = appRepFullPath + "/" + fullImageStr;
                        hm.put("path", fullImageStr);
                        fullImageStr = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                        this.logger.log(Level.INFO, "IPAExtraction-Case8 FullImage  Constructed based on  FULL_IMAGE_IN_IPA :{0}", fullImageStr);
                    }
                }
                else if (isAppUpgrade) {
                    fullImageStr = packageAppDataJSON.optString("FULL_IMAGE_LOC");
                    if (fullImageStr != null && !fullImageStr.equals("") && !fullImageStr.startsWith("http")) {
                        fullImageStr = this.getAppRepositoryBaseFolderPath() + fullImageStr;
                        final String fileName2 = ApiFactoryProvider.getFileAccessAPI().getFileNameFromFilePath(fullImageStr);
                        final HashMap copyDetails = ApiFactoryProvider.getFileAccessAPI().copyFileWithResponse(fullImageStr, appRepositoryFilePath + File.separator + fileName2);
                        fullImageStr = appRepFullPath + "/" + fileName2;
                        jsonObject.put("FULL_IMAGE_LOC", (Object)(destFileDCPath + File.separator + fileName2));
                        jsonObject.put("FULL_IMAGE_ID", copyDetails.get("file_id"));
                        hm.put("path", fullImageStr);
                        fullImageStr = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                        this.logger.log(Level.INFO, "IPAExtraction-Case9 FullImage  Constructed based on  FULL_IMAGE_LOC (AppUpgrade) :{0}", fullImageStr);
                    }
                }
                else {
                    fullImageStr = packageAppDataJSON.optString("FULL_IMAGE_LOC");
                    if (fullImageStr != null && !fullImageStr.equals("")) {
                        fullImageStr = fullImageStr.replace("\\", "/");
                        hm.put("path", fullImageStr);
                        fullImageStr = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().getURLWithAuthToken(hm);
                        fullImageStr = MDMiOSEntrollmentUtil.getInstance().getServerBaseURL() + fullImageStr;
                        this.logger.log(Level.INFO, "IPAExtraction-Case10 FullImage  Constructed based on  FULL_IMAGE_LOC(NotAppUpgrade)):{0}", fullImageStr);
                    }
                }
            }
            final int packageType = (int)jsonObject.get("PACKAGE_TYPE");
            final int platformType = (int)jsonObject.get("PLATFORM_TYPE");
            final int supportedDevice = packageAppDataJSON.optInt("SUPPORTED_DEVICES");
            if (platformType == 1 && packageType == 2) {
                final String agentStaticServerURL = jsonObject.optString("STATIC_SERVER_URL", (String)null);
                if (supportedDevice == 16) {
                    final String packageSHA = String.valueOf(jsonObject.get("PACKAGE_SHA"));
                    final String packageMD5 = String.valueOf(jsonObject.get("PACKAGE_MD5"));
                    final String packageSize = String.valueOf(jsonObject.get("PACKAGE_SIZE"));
                    String packageLocation;
                    if (agentStaticServerURL != null) {
                        packageLocation = agentStaticServerURL;
                    }
                    else {
                        packageLocation = appLocStr.replaceAll(" ", "%20");
                    }
                    final String manifestFilePath = appRepositoryFilePath + File.separator + "manifest.plist";
                    this.generateMacEnterpriseAppManifestFile(manifestFilePath, packageLocation, packageMD5, packageSHA, packageSize);
                    final String appRepositoryFolderPath = this.getAppRepositoryFolderDBPath(customerId, packageId, appId);
                    final String manifestRelativeFilePath = appRepositoryFolderPath + File.separator + "manifest.plist";
                    jsonObject.put("MANIFEST_FILE_URL", (Object)manifestRelativeFilePath);
                }
                else {
                    final String bundelIdentifier = (String)jsonObject.get("BUNDLE_IDENTIFIER");
                    final String appVersion = (String)jsonObject.get("APP_VERSION");
                    final String appTitle = (String)jsonObject.get("APP_TITLE");
                    final String manifestFilePath2 = appRepositoryFilePath + File.separator + "manifest.plist";
                    final String customizedAppURL = jsonObject.optString("CUSTOMIZED_APP_URL", (String)null);
                    final File destFile = new File(appRepositoryFilePath);
                    if (!destFile.exists()) {
                        destFile.mkdirs();
                    }
                    if (!MDMStringUtils.isEmpty(customizedAppURL)) {
                        appLocStr = customizedAppURL;
                    }
                    MDMRestAPIFactoryProvider.getAppsUtilAPI().generateEnterpriseAppManifestFile(manifestFilePath2, appLocStr.replaceAll(" ", "%20"), fullImageStr.replaceAll(" ", "%20"), displayImageStr.replaceAll(" ", "%20"), Boolean.TRUE, Boolean.TRUE, bundelIdentifier, appVersion, appTitle, appTitle);
                    final String appRepositoryFolderPath2 = this.getAppRepositoryFolderDBPath(customerId, packageId, appId);
                    final String manifestRelativeFilePath2 = appRepositoryFolderPath2 + File.separator + "manifest.plist";
                    jsonObject.put("MANIFEST_FILE_URL", (Object)manifestRelativeFilePath2);
                }
            }
        }
        this.logger.log(Level.INFO, "AppRepository files processed successfully for appID: {0} of package: {1}", new Object[] { appId, packageId });
    }
    
    private boolean isAppExistsInCurrentLivePackage(final Long appId) throws DataAccessException {
        boolean isAppExists = false;
        final Criteria packageAppCri = new Criteria(Column.getColumn("MdPackageToAppData", "APP_ID"), (Object)appId, 0);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppData"));
        selectQuery.addJoin(new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.setCriteria(packageAppCri);
        selectQuery.addSelectColumn(new Column("AppGroupToCollection", "COLLECTION_ID"));
        final DataObject dObj = MDMUtil.getPersistence().get(selectQuery);
        if (!dObj.isEmpty()) {
            isAppExists = true;
        }
        return isAppExists;
    }
    
    private boolean isAppExistsInPackage(final Long appId) throws DataAccessException {
        boolean isAppExists = false;
        final Criteria packageAppCri = new Criteria(Column.getColumn("MdPackageToAppData", "APP_ID"), (Object)appId, 0);
        final DataObject dObj = MDMUtil.getPersistence().get("MdPackageToAppData", packageAppCri);
        if (!dObj.isEmpty()) {
            isAppExists = true;
        }
        return isAppExists;
    }
    
    public String getAppRepositoryFolderDBPath(final Long customerId, final Long packageId, final Long appId) throws Exception {
        final String appRepositoryFolder = File.separator + "MDM" + File.separator + "apprepository" + File.separator + customerId + File.separator + "appupload" + File.separator + packageId + File.separator + appId;
        return appRepositoryFolder;
    }
    
    public String getAppRepositoryBaseFolderPath() {
        String appBaseDir;
        if (CustomerInfoUtil.isSAS()) {
            appBaseDir = new File(ApiFactoryProvider.getUtilAccessAPI().getServerHome()).getParent();
        }
        else {
            appBaseDir = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "agent-files";
        }
        return appBaseDir;
    }
    
    public String getAppRepositoryFolderPath(final Long customerId, final Long packageId, final Long appId) throws Exception {
        final String appBaseDir = this.getAppRepositoryBaseFolderPath();
        final String appRepositoryFolder = appBaseDir + File.separator + "MDM" + File.separator + "apprepository" + File.separator + customerId + File.separator + "appupload" + File.separator + packageId + File.separator + appId;
        return appRepositoryFolder;
    }
    
    private String getAppRepositoryFolderPath(final Long customerId, final Long packageId) throws Exception {
        final String webappsDir = this.getAppRepositoryBaseFolderPath();
        final String appRepositoryFolder = webappsDir + File.separator + "MDM" + File.separator + "apprepository" + File.separator + customerId + File.separator + "appupload" + File.separator + packageId;
        return appRepositoryFolder;
    }
    
    protected String getAppRepositoryFullPath(final Long customerId, final Long packageId, final Long appId) throws Exception {
        final String appRepositoryFolder = "/MDM/apprepository/" + customerId + "/" + "appupload" + "/" + packageId + "/" + appId;
        return appRepositoryFolder;
    }
    
    public String getAppRepositoryLicenseDBPath(final Long customerId, final Long appGroupId) {
        final String appRepositoryFolder = File.separator + "MDM" + File.separator + "apprepository" + File.separator + customerId + File.separator + "applicense" + File.separator + appGroupId;
        return appRepositoryFolder;
    }
    
    public String getAppRepositoryLicensePath(final Long customerId, final Long appGroupId) {
        final String webappsDir = this.getAppRepositoryBaseFolderPath();
        final String appRepositoryFolder = webappsDir + File.separator + "MDM" + File.separator + "apprepository" + File.separator + customerId + File.separator + "applicense" + File.separator + appGroupId;
        return appRepositoryFolder;
    }
    
    public String getAppRepositoryTempSourceFolderPath(final Long customerId) {
        final Calendar cal = Calendar.getInstance();
        final String sourceFolder = "AppSource_" + cal.getTimeInMillis();
        final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
        final String appRepositoryFolder = webappsDir + File.separator + "MDM" + File.separator + customerId + File.separator + "temp" + File.separator + sourceFolder;
        return appRepositoryFolder;
    }
    
    public static String getDynamicServerBaseURL() {
        return "https://%ServerName%:%ServerPort%";
    }
    
    public void deleteAppAssignableDetails(final Long[] appGroupIds) {
        this.logger.log(Level.INFO, "Deleting the app assignable detials for :  {0}", Arrays.toString(appGroupIds));
        try {
            final Criteria appGroupIdsCri = new Criteria(Column.getColumn("MDAppAssignableDetails", "APP_GROUP_ID"), (Object)appGroupIds, 8);
            DataAccess.delete("MDAppAssignableDetails", appGroupIdsCri);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in deleteAppAssignableDetails...", ex);
        }
    }
    
    public void setMdAppAssignableDetails(final Long customerId, final Long appGroupId, final JSONObject mdAppAssignableDetails) {
        if (mdAppAssignableDetails != null) {
            final Boolean isDeviceAssignable = Boolean.parseBoolean(mdAppAssignableDetails.opt("IS_DEVICE_ASSIGNABLE").toString());
            Object appAssignableTypeObj = isDeviceAssignable ? mdAppAssignableDetails.opt("APP_ASSIGNABLE_TYPE") : Integer.valueOf(1);
            if (appAssignableTypeObj == null || appAssignableTypeObj.toString().equalsIgnoreCase("0")) {
                appAssignableTypeObj = VPPAppMgmtHandler.getInstance().getVppGlobalAssignmentType(customerId);
            }
            final Integer appAssignableType = Integer.parseInt(appAssignableTypeObj.toString());
            this.addorUpdateMdAppAssignableDetails(appGroupId, appAssignableType, isDeviceAssignable);
        }
    }
    
    private void addorUpdateMdAppAssignableDetails(final Long appGroupID, final Integer appAssignableType, final Boolean deviceAssignable) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MDAppAssignableDetails"));
            final Criteria appAssignableCriteria = new Criteria(Column.getColumn("MDAppAssignableDetails", "APP_GROUP_ID"), (Object)appGroupID, 0);
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            selectQuery.setCriteria(appAssignableCriteria);
            final DataObject mdAppAssignableDetailsDO = MDMUtil.getPersistence().get(selectQuery);
            Row appAssignableDetailsRow = null;
            appAssignableDetailsRow = mdAppAssignableDetailsDO.getRow("MDAppAssignableDetails");
            if (appAssignableDetailsRow == null) {
                appAssignableDetailsRow = new Row("MDAppAssignableDetails");
                appAssignableDetailsRow.set("APP_GROUP_ID", (Object)appGroupID);
                appAssignableDetailsRow.set("APP_ASSIGNABLE_TYPE", (Object)appAssignableType);
                appAssignableDetailsRow.set("IS_DEVICE_ASSIGNABLE", (Object)deviceAssignable);
                mdAppAssignableDetailsDO.addRow(appAssignableDetailsRow);
                this.logger.log(Level.INFO, "Added App Assignable details for  appGroupID{0} APP_ASSIGNABLE_TYPE{1} IS_DEVICE_ASSIGNABLE:{2}", new Object[] { appGroupID, appAssignableType, deviceAssignable });
            }
            else {
                if (appAssignableType != null) {
                    appAssignableDetailsRow.set("APP_ASSIGNABLE_TYPE", (Object)appAssignableType);
                    this.logger.log(Level.INFO, "Setting APP_ASSIGNABLE_TYPE  App Assignable details for appGroupID{0} APP_ASSIGNABLE_TYPE:{1}", new Object[] { appGroupID, appAssignableType });
                }
                if (deviceAssignable != null) {
                    appAssignableDetailsRow.set("IS_DEVICE_ASSIGNABLE", (Object)deviceAssignable);
                    this.logger.log(Level.INFO, "Setting IS_DEVICE_ASSIGNABLE  App Assignable details for appGroupID{0} IS_DEVICE_ASSIGNABLE:{1}", new Object[] { appGroupID, deviceAssignable });
                }
                mdAppAssignableDetailsDO.updateRow(appAssignableDetailsRow);
            }
            MDMUtil.getPersistence().update(mdAppAssignableDetailsDO);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in addorUpdateMdAppAssignableDetails {0}", ex);
        }
    }
    
    public JSONObject getAppInformation(final List<Long> appGroupID) {
        final JSONObject containerApps = new JSONObject();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            final Join appToGroupJoin = new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join appJoin = new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join appToClnJoin = new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1);
            final Join profColnJoin = new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1);
            final Join profileJoin = new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1);
            final Join packageJoin = new Join("MdAppGroupDetails", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
            selectQuery.addJoin(appToGroupJoin);
            selectQuery.addJoin(appJoin);
            selectQuery.addJoin(appToClnJoin);
            selectQuery.addJoin(profColnJoin);
            selectQuery.addJoin(profileJoin);
            selectQuery.addJoin(packageJoin);
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
            final Column profileColumn = new Column("Profile", "PROFILE_NAME").maximum();
            profileColumn.setColumnAlias("PROFILE_NAME");
            final SortColumn sortColumn = new SortColumn(profileColumn, true);
            selectQuery.addSortColumn(sortColumn);
            selectQuery.setGroupByClause(new GroupByClause(selectQuery.getSelectColumns()));
            final Criteria criteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupID.toArray(), 8);
            selectQuery.setCriteria(criteria);
            selectQuery.addSelectColumn(profileColumn);
            DMDataSetWrapper ds = null;
            try {
                ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
                final List<String> addedIdentifier = new ArrayList<String>();
                while (ds.next()) {
                    final JSONObject appData = new JSONObject();
                    final Object appGroupObj = ds.getValue("APP_GROUP_ID");
                    final Object identifierObj = ds.getValue("IDENTIFIER");
                    final Object groupDisplyNameObj = ds.getValue("GROUP_DISPLAY_NAME");
                    final Object profileNameObj = ds.getValue("PROFILE_NAME");
                    final Object displayFilePath = ds.getValue("DISPLAY_IMAGE_LOC");
                    final String identifier = (String)identifierObj;
                    if (!addedIdentifier.contains(identifier)) {
                        final String appName = (String)((profileNameObj != null) ? profileNameObj : ((String)groupDisplyNameObj));
                        appData.put("APP_GROUP_ID", (Object)appGroupObj);
                        appData.put("IDENTIFIER", (Object)identifier);
                        appData.put("GROUP_DISPLAY_NAME", (Object)appName);
                        appData.put("DISPLAY_IMAGE_LOC", displayFilePath);
                        containerApps.put(appGroupObj + "", (Object)appData);
                        addedIdentifier.add(identifier);
                    }
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception occurred while executing query on getSearchApps", ex);
            }
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
        }
        return containerApps;
    }
    
    public SelectQuery getMDPackageAppDataQuery() {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackage"));
        sQuery.addJoin(new Join("MdPackage", "MdPackagePolicy", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        sQuery.addJoin(new Join("MdPackage", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        sQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
        sQuery.addJoin(new Join("MdPackageToAppData", "InstallAppPolicy", new String[] { "PACKAGE_ID", "APP_GROUP_ID", "APP_ID" }, new String[] { "PACKAGE_ID", "APP_GROUP_ID", "APP_ID" }, 2));
        sQuery.addJoin(new Join("InstallAppPolicy", "ConfigDataItem", new String[] { "CONFIG_DATA_ITEM_ID" }, new String[] { "CONFIG_DATA_ITEM_ID" }, 2));
        sQuery.addJoin(new Join("ConfigDataItem", "ConfigData", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("ConfigData", "CfgDataToCollection", new String[] { "CONFIG_DATA_ID" }, new String[] { "CONFIG_DATA_ID" }, 2));
        sQuery.addJoin(new Join("CfgDataToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        sQuery.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria approvedAppVersionCriteria = AppVersionDBUtil.getInstance().getApprovedAppVersionCriteria();
        final Criteria appIdCriteria = new Criteria(Column.getColumn("MdPackageToAppData", "APP_ID"), (Object)Column.getColumn("MdAppToCollection", "APP_ID"), 0);
        Criteria queryCriteria = sQuery.getCriteria();
        if (queryCriteria == null) {
            queryCriteria = appIdCriteria;
        }
        else {
            queryCriteria = queryCriteria.and(appIdCriteria).and(approvedAppVersionCriteria);
        }
        sQuery.setCriteria(queryCriteria);
        return sQuery;
    }
    
    public Integer getAppLicenseType(final String bundleId) {
        final String appType = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
            final Join appGroupToPackageJoin = new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join appAssignableDetailsJoin = new Join("MdAppGroupDetails", "MDAppAssignableDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            selectQuery.addJoin(appGroupToPackageJoin);
            selectQuery.addJoin(appAssignableDetailsJoin);
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
            selectQuery.addSelectColumn(Column.getColumn("MDAppAssignableDetails", "*"));
            final Criteria bundleIdCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)bundleId, 0);
            selectQuery.setCriteria(bundleIdCriteria);
            final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
            if (DO != null && !DO.isEmpty()) {
                final Row mdPackageToAppGroupRow = DO.getRow("MdPackageToAppGroup");
                final Boolean isPurchasedFromProtal = (Boolean)mdPackageToAppGroupRow.get("IS_PURCHASED_FROM_PORTAL");
                if (isPurchasedFromProtal) {
                    final Row mdAppAssignableDetailsRow = DO.getRow("MDAppAssignableDetails");
                    final Integer mdAppAssignableType = (Integer)mdAppAssignableDetailsRow.get("APP_ASSIGNABLE_TYPE");
                    return mdAppAssignableType;
                }
                return 0;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAppLicenseType {0}", ex);
        }
        return 0;
    }
    
    public int getAppsNotPurchasedFromPortal(final Integer platformType, final Long customerId) {
        int nonBusinessStoreAppCount = 0;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            sQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria isPurchasedFromPortalCri = new Criteria(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)false, 0);
            final Criteria isNonEnterpriseApps = new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 1);
            final Criteria customerIdCri = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            Criteria nonBusinessStoreAppCri = isPurchasedFromPortalCri;
            Criteria platformCri = null;
            if (platformType != null) {
                platformCri = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
            }
            nonBusinessStoreAppCri = isPurchasedFromPortalCri.and(platformCri).and(isNonEnterpriseApps).and(customerIdCri);
            sQuery.setCriteria(nonBusinessStoreAppCri);
            nonBusinessStoreAppCount = DBUtil.getRecordActualCount(sQuery, "MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAppsNotPurchasedFromPortal :{0}", ex);
        }
        return nonBusinessStoreAppCount;
    }
    
    public void ifIosNativeAppHideMeMdmPromoMsg(final String bundleId, final Long customerId) {
        if (bundleId.equalsIgnoreCase("com.manageengine.mdm.iosagent")) {
            MessageProvider.getInstance().hideMessage("SILENT_INSTALL_ME_MDM_APPS", customerId);
        }
    }
    
    public int getAppType(final int packageType, final int platformType) {
        int appType = 0;
        if (platformType == 1 && (packageType == 0 || packageType == 1)) {
            appType = 0;
        }
        else if (platformType == 1 && packageType == 2) {
            appType = 1;
        }
        else if (platformType == 2 && (packageType == 0 || packageType == 1)) {
            appType = 2;
        }
        else if (platformType == 2 && packageType == 2) {
            appType = 3;
        }
        else if (platformType == 3 && packageType == 2) {
            appType = 5;
        }
        return appType;
    }
    
    private JSONObject getAppDistributedCount(final Long appGroupId, final Long releaseLabelId, final Long profileID, final Long businessStoreID) throws QueryConstructionException, DataAccessException, SQLException {
        int distributtedAppCount = 0;
        int yetToUpdateAppCount = 0;
        int installationFailedCount = 0;
        int installationInprogressCount = 0;
        int distributedGroupCount = 0;
        int appUpdateScheduledCount = 0;
        DMDataSetWrapper ds = null;
        try {
            SelectQuery mdAppCatalogToResQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
            final Join managedDeviceJoin = new Join("MdAppCatalogToResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join mdAppCatalogExtnJoin = new Join("MdAppCatalogToResource", "MdAppCatalogToResourceExtn", new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, 2);
            final Join mdAppToCollectionJoin = new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join appCollectionReleaseLabelHistoryJoin = new Join("MdAppToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Join appCollectionReleaseLabelHistoryMaxJoin = AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable();
            mdAppCatalogToResQuery.addJoin(managedDeviceJoin);
            mdAppCatalogToResQuery.addJoin(mdAppCatalogExtnJoin);
            mdAppCatalogToResQuery.addJoin(mdAppToCollectionJoin);
            mdAppCatalogToResQuery.addJoin(appCollectionReleaseLabelHistoryJoin);
            mdAppCatalogToResQuery.addJoin(appCollectionReleaseLabelHistoryMaxJoin);
            final Criteria appGroupCri = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria cManagedStatus = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
            Criteria finalCrit = appGroupCri.and(cManagedStatus).and(releaseLabelCriteria);
            if (businessStoreID != null) {
                final Criteria cri = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), 0);
                final Criteria businessStoreIDCri = new Criteria(new Column("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
                final Criteria profileIDCriteria = new Criteria(new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileID, 0);
                final Join deploymentConfigJoin = new Join("ManagedDevice", "MDMResourceToDeploymentConfigs", cri.and(profileIDCriteria).and(businessStoreIDCri), 2);
                mdAppCatalogToResQuery.addJoin(deploymentConfigJoin);
                finalCrit = finalCrit.and(businessStoreIDCri);
            }
            mdAppCatalogToResQuery.setCriteria(finalCrit);
            mdAppCatalogToResQuery = RBDAUtil.getInstance().getRBDAQuery(mdAppCatalogToResQuery);
            final Criteria noUpdateAvailableCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "IS_UPDATE_AVAILABLE"), (Object)false, 0);
            final Criteria appUpdateScheduledCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APPROVED_VERSION_STATUS"), (Object)2, 0);
            final Criteria appUpdateNotScheduledCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APPROVED_VERSION_STATUS"), (Object)2, 1);
            Column associatedColumn = Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID");
            associatedColumn = associatedColumn.distinct().count();
            associatedColumn.setColumnAlias("DISTRIBUTED_DEVICE_COUNT");
            associatedColumn.setType(4);
            mdAppCatalogToResQuery.addSelectColumn(associatedColumn);
            final Criteria yetToUpdateAppGroupCri = new Criteria(new Column("MdAppCatalogToResourceExtn", "IS_UPDATE_AVAILABLE"), (Object)Boolean.TRUE, 0);
            final CaseExpression distributedAppcountExp = new CaseExpression("YET_TO_UPDATE_DEVICE_COUNT");
            distributedAppcountExp.addWhen(yetToUpdateAppGroupCri.and(appUpdateNotScheduledCriteria), (Object)new Column("MdAppCatalogToResource", "RESOURCE_ID"));
            final Column selectColumn = (Column)Column.createFunction("COUNT", new Object[] { distributedAppcountExp });
            selectColumn.setType(4);
            selectColumn.setColumnAlias("YET_TO_UPDATE_DEVICE_COUNT");
            mdAppCatalogToResQuery.addSelectColumn(selectColumn);
            final CaseExpression appUpdateScheduled = new CaseExpression("APP_UPDATE_SCHEDULED");
            appUpdateScheduled.addWhen(yetToUpdateAppGroupCri.and(appUpdateScheduledCriteria), (Object)Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"));
            final Column scheduleCountColumn = (Column)Column.createFunction("COUNT", new Object[] { appUpdateScheduled });
            scheduleCountColumn.setType(4);
            scheduleCountColumn.setColumnAlias("APP_UPDATE_SCHEDULED");
            mdAppCatalogToResQuery.addSelectColumn(scheduleCountColumn);
            final Criteria appInstallationFailedCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)7, 0);
            final Criteria installationFailedCriteria = noUpdateAvailableCriteria.and(appInstallationFailedCriteria);
            final CaseExpression appInstallationFailed = new CaseExpression("APP_INSTALLATION_FAILED_DEVICE_COUNT");
            appInstallationFailed.addWhen(installationFailedCriteria, (Object)new Column("MdAppCatalogToResource", "RESOURCE_ID"));
            final Column installationFailedColumn = (Column)Column.createFunction("COUNT", new Object[] { appInstallationFailed });
            installationFailedColumn.setColumnAlias("APP_INSTALLATION_FAILED_DEVICE_COUNT");
            installationFailedColumn.setType(4);
            mdAppCatalogToResQuery.addSelectColumn(installationFailedColumn);
            final Criteria appInstallationInProgressCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)1, 0);
            final Criteria installationInprogressCriteria = noUpdateAvailableCriteria.and(appInstallationInProgressCriteria);
            final CaseExpression appInstallationInProgress = new CaseExpression("APP_INSTALLATION_PROGRESS_DEVICE_COUNT");
            appInstallationInProgress.addWhen(installationInprogressCriteria, (Object)new Column("MdAppCatalogToResource", "RESOURCE_ID"));
            final Column installationInProgressCount = (Column)Column.createFunction("COUNT", new Object[] { appInstallationInProgress });
            installationInProgressCount.setColumnAlias("APP_INSTALLATION_PROGRESS_DEVICE_COUNT");
            installationInProgressCount.setType(4);
            mdAppCatalogToResQuery.addSelectColumn(installationInProgressCount);
            ds = DMDataSetWrapper.executeQuery((Object)mdAppCatalogToResQuery);
            while (ds.next()) {
                distributtedAppCount = (int)ds.getValue("DISTRIBUTED_DEVICE_COUNT");
                yetToUpdateAppCount = (int)ds.getValue("YET_TO_UPDATE_DEVICE_COUNT");
                installationInprogressCount = (int)ds.getValue("APP_INSTALLATION_PROGRESS_DEVICE_COUNT");
                installationFailedCount = (int)ds.getValue("APP_INSTALLATION_FAILED_DEVICE_COUNT");
                appUpdateScheduledCount = (int)ds.getValue("APP_UPDATE_SCHEDULED");
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToGroup"));
            selectQuery.addJoin(new Join("MdAppCatalogToGroup", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
            selectQuery.addJoin(new Join("MdAppCatalogToGroup", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"), (Object)appGroupId, 0);
            if (businessStoreID != null) {
                final Criteria cri2 = new Criteria(new Column("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)new Column("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), 0);
                final Criteria profileIDCriteria2 = new Criteria(new Column("MDMResourceToDeploymentConfigs", "PROFILE_ID"), (Object)profileID, 0);
                final Criteria businessStoreIDCri2 = new Criteria(new Column("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
                final Join deploymentConfigJoin2 = new Join("MdAppCatalogToGroup", "MDMResourceToDeploymentConfigs", cri2.and(profileIDCriteria2).and(businessStoreIDCri2), 2);
                selectQuery.addJoin(deploymentConfigJoin2);
            }
            final Criteria releaseLabelCri = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
            selectQuery.setCriteria(appGroupCriteria.and(releaseLabelCri));
            RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            Column countCol = Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID");
            countCol = countCol.distinct();
            countCol = countCol.count();
            countCol.setType(4);
            selectQuery.addSelectColumn(countCol);
            distributedGroupCount = DBUtil.getRecordCount(selectQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in querying data in getAppDistributedCount :{0}", ex);
        }
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("DISTRIBUTED_DEVICE_COUNT", distributtedAppCount);
            json.put("YET_TO_UPDATE_DEVICE_COUNT", yetToUpdateAppCount);
            json.put("APP_INSTALLATION_FAILED_DEVICE_COUNT", installationFailedCount);
            json.put("APP_INSTALLATION_PROGRESS_DEVICE_COUNT", installationInprogressCount);
            json.put("APP_DISTRIBUTED_GROUP_COUNT", distributedGroupCount);
            json.put("APP_UPDATE_SCHEDULED", appUpdateScheduledCount);
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in setting json in getAppDistributedCount :{0}", ex2);
        }
        return json;
    }
    
    @Deprecated
    private JSONObject getAppDistributedCountForIosAppStoreApp(final Long appGroupId, final Long latestAppId) throws QueryConstructionException, DataAccessException, SQLException {
        int distributtedAppCount = 0;
        int yetToUpdateAppCount = 0;
        DMDataSetWrapper ds = null;
        try {
            SelectQuery mdAppCatalogToResQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
            final Join managedDeviceJoin = new Join("MdAppCatalogToResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            mdAppCatalogToResQuery.addJoin(managedDeviceJoin);
            final Criteria appGroupCri = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria cManagedStatus = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            mdAppCatalogToResQuery.setCriteria(appGroupCri.and(cManagedStatus));
            final Column distributedCount = Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID").count();
            distributedCount.setColumnAlias("DISTRIBUTED_DEVICE_COUNT");
            mdAppCatalogToResQuery.addSelectColumn(distributedCount);
            mdAppCatalogToResQuery = RBDAUtil.getInstance().getRBDAQuery(mdAppCatalogToResQuery);
            final Criteria notInLatestAppGroupCri = new Criteria(new Column("MdAppCatalogToResource", "PUBLISHED_APP_ID"), (Object)latestAppId, 1);
            final CaseExpression distributedAppcountExp = new CaseExpression("YET_TO_UPDATE_DEVICE_COUNT");
            distributedAppcountExp.addWhen(notInLatestAppGroupCri, (Object)new Column("MdAppCatalogToResource", "APP_GROUP_ID"));
            final Column selectColumn = (Column)Column.createFunction("COUNT", new Object[] { distributedAppcountExp });
            selectColumn.setType(4);
            selectColumn.setColumnAlias("YET_TO_UPDATE_DEVICE_COUNT");
            mdAppCatalogToResQuery.addSelectColumn(selectColumn);
            ds = DMDataSetWrapper.executeQuery((Object)mdAppCatalogToResQuery);
            while (ds.next()) {
                distributtedAppCount = (int)ds.getValue("DISTRIBUTED_DEVICE_COUNT");
                yetToUpdateAppCount = (int)ds.getValue("YET_TO_UPDATE_DEVICE_COUNT");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in querying data in getAppDistributedCount :{0}", ex);
        }
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("DISTRIBUTED_DEVICE_COUNT", distributtedAppCount);
            json.put("YET_TO_UPDATE_DEVICE_COUNT", yetToUpdateAppCount);
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in setting json in getAppDistributedCount :{0}", ex2);
        }
        return json;
    }
    
    public HashMap performUpdateAllForAppGroup(final Long appGroupID, final Long customerID, final Long loggedinUser, final Properties properties) throws DataAccessException {
        final Long releaseLabelId = ((Hashtable<K, Long>)properties).get("RELEASE_LABEL_ID");
        ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
        ((Hashtable<String, Long>)properties).put("customerId", customerID);
        final HashMap<Long, Long> profileCollectionMap = this.getLatestProfileCollectionMapForAppGroup(appGroupID, releaseLabelId);
        ((Hashtable<String, HashMap<Long, Long>>)properties).put("profileCollectionMap", profileCollectionMap);
        ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
        HashMap resourceMap = null;
        final Boolean isScheduledApps = properties.get("forceUpdate") != null && ((Hashtable<K, Boolean>)properties).get("forceUpdate");
        resourceMap = this.getYetToUpdateDeviceList(appGroupID, releaseLabelId, isScheduledApps);
        final List groupList = resourceMap.get("groupList");
        final List resList = resourceMap.get("resourceList");
        ((Hashtable<String, Boolean>)properties).put("isGroup", true);
        ((Hashtable<String, List>)properties).put("resourceList", groupList);
        ((Hashtable<String, Integer>)properties).put("groupType", 6);
        ((Hashtable<String, Long>)properties).put("loggedOnUser", loggedinUser);
        ((Hashtable<String, String>)properties).put("loggedOnUserName", DMUserHandler.getDCUser(DMUserHandler.getLoginIdForUserId(loggedinUser)));
        this.logger.log(Level.INFO, "Update-All has triggered update for app {0} with properties (Group) {1}", new Object[] { appGroupID, properties });
        ProfileAssociateHandler.getInstance().associateCollectionForGroup(properties);
        ((Hashtable<String, Boolean>)properties).put("isGroup", false);
        ((Hashtable<String, List>)properties).put("resourceList", resList);
        ((Hashtable<String, Boolean>)properties).put("profileOrigin", false);
        ProfileAssociateHandler.getInstance().associateCollectionForResource(properties);
        this.logger.log(Level.INFO, "Update-All has triggered update for app {0} with properties (Resource) {1}", new Object[] { appGroupID, properties });
        return resourceMap;
    }
    
    private HashMap<Long, Long> getLatestProfileCollectionMapForAppGroup(final Long appGroupID, final Long releaseLabelId) throws DataAccessException {
        final HashMap<Long, Long> profileCollectionMap = new HashMap<Long, Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppGroupToCollection"));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
        final Criteria appGroupCri = new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupID, 0);
        final Criteria appReleaseLabelCri = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        selectQuery.setCriteria(appGroupCri.and(appReleaseLabelCri));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("ProfileToCollection");
            final Long profileID = (Long)row.get("PROFILE_ID");
            final Long collectionID = (Long)row.get("COLLECTION_ID");
            profileCollectionMap.put(profileID, collectionID);
        }
        return profileCollectionMap;
    }
    
    public JSONObject getLatestAndroidAppDetailsForAppReleaseLabel(final Long appGroupID, final Long releaseLabelId) throws DataAccessException, JSONException {
        final JSONObject appVersionDetails = new JSONObject();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppGroupToCollection"));
        final Join appCollnJoin = new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join appDetailsJoin = new Join("MdAppToCollection", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        selectQuery.addJoin(appCollnJoin);
        selectQuery.addJoin(appDetailsJoin);
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupID, 0);
        final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        selectQuery.setCriteria(appGroupCriteria.and(releaseLabelCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getRow("MdAppDetails");
            appVersionDetails.put("APP_ID", row.get("APP_ID"));
            appVersionDetails.put("APP_VERSION", row.get("APP_VERSION"));
            appVersionDetails.put("APP_NAME_SHORT_VERSION", row.get("APP_NAME_SHORT_VERSION"));
        }
        return appVersionDetails;
    }
    
    public HashMap getYetToUpdateDeviceList(final Long appGroupId, final Long releaseLabelId, final Boolean isScheduledApps) {
        final HashMap hashMap = new HashMap();
        final Set groupResourceList = new HashSet();
        final Set resourceList = new HashSet();
        final Set groupList = new HashSet();
        try {
            final SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToGroup"));
            groupQuery.addJoin(new Join("MdAppCatalogToGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1));
            groupQuery.addJoin(new Join("MdAppCatalogToGroup", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            groupQuery.addJoin(new Join("MdAppCatalogToGroup", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, 2));
            groupQuery.addJoin(new Join("MdAppToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            groupQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
            RBDAUtil.getInstance().getRBDAQuery(groupQuery);
            groupQuery.addJoin(new Join("MdAppCatalogToGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            groupQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            groupQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
            groupQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
            final Criteria grpAppGroupCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria grpUpdateCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "IS_UPDATE_AVAILABLE"), (Object)true, 0);
            final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
            final Criteria grpApprovedVersionStatusCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "APPROVED_VERSION_STATUS"), (Object)2, 0);
            Criteria groupCriteria = grpUpdateCriteria.and(grpAppGroupCriteria).and(releaseLabelCriteria);
            if (isScheduledApps) {
                groupCriteria = groupCriteria.and(grpApprovedVersionStatusCriteria);
            }
            groupQuery.setCriteria(MDMDBUtil.andCriteria(groupQuery.getCriteria(), groupCriteria));
            DataObject dataObject = MDMUtil.getPersistence().get(groupQuery);
            Iterator iterator = dataObject.getRows("CustomGroupMemberRel");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long resID = (Long)row.get("MEMBER_RESOURCE_ID");
                final Long grpID = (Long)row.get("GROUP_RESOURCE_ID");
                groupResourceList.add(resID);
                groupList.add(grpID);
            }
            final Iterator grpIterator = dataObject.getRows("CustomGroup");
            while (grpIterator.hasNext()) {
                final Row row2 = grpIterator.next();
                final Long grpId = (Long)row2.get("RESOURCE_ID");
                groupList.add(grpId);
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResourceExtn"));
            selectQuery.addJoin(new Join("MdAppCatalogToResourceExtn", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppCatalogToResourceExtn", "MdAppCatalogToResource", new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppToCollection", "AppCollnToReleaseLabelHistory", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(AppVersionDBUtil.getInstance().getJoinForCollectionsLatestAppReleaseLabelFromHistoryTable());
            final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria updateCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "IS_UPDATE_AVAILABLE"), (Object)true, 0);
            final Criteria approvedAppVersionCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APPROVED_VERSION_STATUS"), (Object)2, 0);
            selectQuery.setCriteria(appGroupCriteria.and(updateCriteria).and(managedDeviceCriteria).and(releaseLabelCriteria));
            if (isScheduledApps) {
                selectQuery.setCriteria(selectQuery.getCriteria().and(approvedAppVersionCriteria));
            }
            selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResourceExtn", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"));
            RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            dataObject = MDMUtil.getPersistence().get(selectQuery);
            iterator = dataObject.getRows("MdAppCatalogToResourceExtn");
            while (iterator.hasNext()) {
                final Row row3 = iterator.next();
                final Long resID2 = (Long)row3.get("RESOURCE_ID");
                resourceList.add(resID2);
            }
            final List induvidualList = new ArrayList();
            induvidualList.addAll(resourceList);
            induvidualList.removeAll(groupResourceList);
            hashMap.put("groupList", new ArrayList(groupList));
            hashMap.put("resourceList", new ArrayList(induvidualList));
            hashMap.put("totalResourceList", new ArrayList(resourceList));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getYetToUpdateDeivceList :{0}", ex);
        }
        return hashMap;
    }
    
    @Deprecated
    public HashMap getYetToUpdateDeivceListForIosAppStoreApps(final Long appGroupId) {
        final HashMap hashMap = new HashMap();
        final Set groupResourceList = new HashSet();
        final Set resourceList = new HashSet();
        final Set groupList = new HashSet();
        try {
            final SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToGroup"));
            groupQuery.addJoin(new Join("MdAppCatalogToGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1));
            groupQuery.addJoin(new Join("MdAppCatalogToGroup", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            RBDAUtil.getInstance().getRBDAQuery(groupQuery);
            groupQuery.addJoin(new Join("MdAppCatalogToGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            groupQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            groupQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
            groupQuery.addSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
            final Criteria grpAppGroupCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria grpUpdateCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "IS_UPDATE_AVAILABLE"), (Object)true, 0);
            Criteria criteria = groupQuery.getCriteria();
            if (criteria != null) {
                criteria = criteria.and(grpUpdateCriteria.and(grpAppGroupCriteria));
            }
            else {
                criteria = grpUpdateCriteria.and(grpAppGroupCriteria);
            }
            groupQuery.setCriteria(criteria);
            DataObject dataObject = MDMUtil.getPersistence().get(groupQuery);
            Iterator iterator = dataObject.getRows("CustomGroupMemberRel");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final Long resID = (Long)row.get("MEMBER_RESOURCE_ID");
                final Long grpID = (Long)row.get("GROUP_RESOURCE_ID");
                groupResourceList.add(resID);
                if (!groupList.contains(grpID)) {
                    groupList.add(grpID);
                }
            }
            final Iterator grpIterator = dataObject.getRows("CustomGroup");
            while (grpIterator.hasNext()) {
                final Row row2 = grpIterator.next();
                final Long grpId = (Long)row2.get("RESOURCE_ID");
                groupList.add(grpId);
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResourceExtn"));
            selectQuery.addJoin(new Join("MdAppCatalogToResourceExtn", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria updateCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "IS_UPDATE_AVAILABLE"), (Object)true, 0);
            final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            selectQuery.setCriteria(appGroupCriteria.and(updateCriteria).and(managedDeviceCriteria));
            selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResourceExtn", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"));
            RBDAUtil.getInstance().getRBDAQuery(selectQuery);
            dataObject = MDMUtil.getPersistence().get(selectQuery);
            iterator = dataObject.getRows("MdAppCatalogToResourceExtn");
            while (iterator.hasNext()) {
                final Row row3 = iterator.next();
                final Long resID2 = (Long)row3.get("RESOURCE_ID");
                resourceList.add(resID2);
            }
            final List induvidualList = new ArrayList();
            induvidualList.addAll(resourceList);
            induvidualList.removeAll(groupResourceList);
            hashMap.put("groupList", new ArrayList(groupList));
            hashMap.put("resourceList", new ArrayList(induvidualList));
            hashMap.put("totalResourceList", new ArrayList(resourceList));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getYetToUpdateDeivceList :{0}", ex);
        }
        return hashMap;
    }
    
    public void generateMacEnterpriseAppManifestFile(final String manifestPath, final String packageURL, final String md5, final String sha, final String size) throws Exception {
        final NSDictionary root = new NSDictionary();
        final NSArray arrayDict = new NSArray(1);
        final NSDictionary payload = new NSDictionary();
        payload.put("assets", (NSObject)this.getMacOSAssetsArray(packageURL, md5, sha, size));
        arrayDict.setValue(0, (Object)payload);
        root.put("items", (NSObject)arrayDict);
        final String fileContent = root.toXMLPropertyList();
        final byte[] b = fileContent.getBytes();
        ApiFactoryProvider.getFileAccessAPI().writeFile(manifestPath, b);
        this.logger.log(Level.INFO, "AppsUtil: generate Enterprise App Manifest File :: Success :: ");
    }
    
    private NSArray getMacOSAssetsArray(final String url, final String md5, final String sha, final String size) {
        final NSArray arrayDict = new NSArray(1);
        arrayDict.setValue(0, (Object)this.getSWPackageDict(url, md5, sha, size));
        return arrayDict;
    }
    
    private NSDictionary getSWPackageDict(final String swPackageUrl, final String md5, final String sha, final String size) {
        final NSDictionary payload = new NSDictionary();
        payload.put("kind", (Object)"software-package");
        payload.put("url", (Object)swPackageUrl);
        try {
            payload.put("md5-size", (Object)Integer.parseInt(size));
            payload.put("sha256-size", (Object)Integer.parseInt(size));
        }
        catch (final Exception e) {
            payload.put("md5-size", (Object)size);
            payload.put("sha256-size", (Object)size);
        }
        payload.put("sha256s", (NSObject)this.getHashArray(sha));
        payload.put("md5s", (NSObject)this.getHashArray(md5));
        return payload;
    }
    
    private NSArray getHashArray(final String hash) {
        final NSArray arrayDict = new NSArray(1);
        arrayDict.setValue(0, (Object)hash);
        return arrayDict;
    }
    
    private void setAppGroupIdentifier(final HttpServletRequest request, final DataObject dataObject) {
        if (!dataObject.isEmpty()) {
            try {
                final Row row = dataObject.getFirstRow("MdAppGroupDetails");
                if (row != null) {
                    final String identifier = (String)row.get("IDENTIFIER");
                    if (identifier != null) {
                        request.setAttribute("AppGroupIdentifier", (Object)identifier);
                    }
                }
            }
            catch (final DataAccessException e) {
                this.logger.log(Level.WARNING, "cannot get App group identifier", (Throwable)e);
            }
        }
    }
    
    private String getPlayStoreUrl(final String bundleId, final Boolean purchasedFromPortal) {
        String playStoreURL = purchasedFromPortal ? "https://play.google.com/work/apps/details?id=" : "https://play.google.com/store/apps/details?id=";
        playStoreURL += bundleId;
        return playStoreURL;
    }
    
    private boolean pickProfileIDForAppGroup(final Long appGrpID, final JSONObject jsonObject) {
        boolean isPackageFound = false;
        try {
            final SelectQuery appQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
            final Join grouppackageJoin = new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join packageAppJoin = new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2);
            final Join appCollectionJoin = new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join collectionProfileJoin = new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            final Join profileNameJoin = new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Join profileCustomerJoin = new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
            final Criteria appGrpCriteria = new Criteria(new Column("MdPackageToAppData", "APP_GROUP_ID"), (Object)appGrpID, 0);
            appQuery.addJoin(grouppackageJoin);
            appQuery.addJoin(packageAppJoin);
            appQuery.addJoin(appCollectionJoin);
            appQuery.addJoin(collectionProfileJoin);
            appQuery.addJoin(profileNameJoin);
            appQuery.addJoin(profileCustomerJoin);
            appQuery.setCriteria(appGrpCriteria);
            appQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"));
            appQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"));
            appQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_ID"));
            appQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "COLLECTION_ID"));
            appQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "APP_ID"));
            appQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
            final DataObject resObj = MDMUtil.getPersistence().get(appQuery);
            if (!resObj.isEmpty()) {
                final Row r = resObj.getFirstRow("MdPackageToAppData");
                final Long packageID = (Long)r.get("PACKAGE_ID");
                if (packageID != null && packageID != -1L) {
                    isPackageFound = true;
                    final Row profRow = resObj.getFirstRow("Profile");
                    jsonObject.put("PACKAGE_ID", (Object)packageID);
                    jsonObject.put("PROFILE_ID", (Object)profRow.get("PROFILE_ID"));
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in getAppProfileName....", ex);
        }
        return isPackageFound;
    }
    
    public HashMap getMultiversionLatestAppDetails(final Long appGroupID, final Long releaseLabel) throws DataAccessException, SQLException, QueryConstructionException {
        final HashMap hashMap = new HashMap();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppGroupToCollection"));
        final Join appJoin = new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        final Join packageJoin = new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        final Join appDetialsJoin = new Join("MdPackageToAppData", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
        selectQuery.addJoin(appJoin);
        selectQuery.addJoin(packageJoin);
        selectQuery.addJoin(appDetialsJoin);
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppDetails", "APP_NAME_SHORT_VERSION"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "SUPPORTED_DEVICES"));
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupID, 0);
        final Criteria releaseLabelCriteria = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabel, 0);
        selectQuery.setCriteria(appGroupCriteria.and(releaseLabelCriteria));
        try {
            final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dataSet != null) {
                while (dataSet.next()) {
                    final int supportedDevice = (int)dataSet.getValue("SUPPORTED_DEVICES");
                    final String version = (String)dataSet.getValue("APP_VERSION");
                    final String versionCode = (String)dataSet.getValue("APP_NAME_SHORT_VERSION");
                    if (supportedDevice == 24) {
                        hashMap.put("MultiVersion", Boolean.TRUE);
                        hashMap.put("SmartPhoneVersion", version);
                        hashMap.put("SmartPhoneVersionCode", versionCode);
                        hashMap.put("Tablet", version);
                        hashMap.put("TabletVersionCode", versionCode);
                        break;
                    }
                    if (supportedDevice == 8) {
                        hashMap.put("MultiVersion", Boolean.TRUE);
                        hashMap.put("SmartPhoneVersion", version);
                        hashMap.put("SmartPhoneVersionCode", versionCode);
                    }
                    else {
                        if (supportedDevice != 16) {
                            continue;
                        }
                        hashMap.put("MultiVersion", Boolean.TRUE);
                        hashMap.put("Tablet", version);
                        hashMap.put("TabletVersionCode", versionCode);
                    }
                }
            }
            else {
                hashMap.put("MultiVersion", Boolean.FALSE);
            }
            if (!hashMap.containsKey("MultiVersion")) {
                hashMap.put("MultiVersion", Boolean.FALSE);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "error getMultiversionLatestAppDetails()  ", e);
        }
        return hashMap;
    }
    
    private void updatePlatformSpecificTableForApp(final JSONObject jsonObject, final int platforType) throws DataAccessException, JSONException {
        if (platforType == 3) {
            this.updateWindowsTableForApp(jsonObject);
        }
    }
    
    private void updateWindowsTableForApp(final JSONObject jsonObject) throws DataAccessException, JSONException {
        final Long appID = jsonObject.getLong("APP_ID");
        final String AUMID = jsonObject.optString("AUMID", (String)null);
        final String phoneID = jsonObject.optString("PhoneID", (String)null);
        final String bundelIdentifier = (String)jsonObject.get("BUNDLE_IDENTIFIER");
        if (!MDMStringUtils.isEmpty(AUMID)) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("WindowsAppDetails"));
            selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "APP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("WindowsAppDetails", "AUMID"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("WindowsAppDetails", "APP_ID"), (Object)appID, 0));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("WindowsAppDetails");
                row.set("AUMID", (Object)(bundelIdentifier + "!" + AUMID));
                if (!MDMStringUtils.isEmpty(phoneID)) {
                    row.set("PHONE_PRODUCT_ID", (Object)phoneID);
                }
                dataObject.updateRow(row);
            }
            else {
                final Row row = new Row("WindowsAppDetails");
                row.set("APP_ID", (Object)appID);
                row.set("AUMID", (Object)(bundelIdentifier + "!" + AUMID));
                if (phoneID != null) {
                    row.set("PHONE_PRODUCT_ID", (Object)phoneID);
                }
                dataObject.addRow(row);
            }
            MDMUtil.getPersistence().update(dataObject);
        }
    }
    
    private void sanitizeAppRepoInput(final JSONObject jsonObject) {
        try {
            String bundleIdentifier = String.valueOf(jsonObject.get("BUNDLE_IDENTIFIER"));
            bundleIdentifier = bundleIdentifier.replaceAll("[\\{\\}]", "");
            jsonObject.put("BUNDLE_IDENTIFIER", (Object)bundleIdentifier);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in cleaning app rpo input", e);
        }
    }
    
    public List getAccountApps(final int platform, final Long customerID, final boolean isTrash) {
        final Set set = new HashSet();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria accountCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platform, 0);
        final Criteria trashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)isTrash, 0);
        selectQuery.setCriteria(customerCriteria.and(accountCriteria).and(platformCriteria).and(trashCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            final Iterator iterator = dataObject.getRows("MdPackageToAppGroup");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                set.add(row.get("APP_GROUP_ID"));
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Exception in fetching account apps list");
        }
        return new ArrayList(set);
    }
    
    private int getAppDistributedUserCount(final Long appGroupId) {
        int count = 0;
        try {
            final SelectQuery userCountQuery = (SelectQuery)new SelectQueryImpl(new Table("AppGroupToCollection"));
            final Join managedDeviceJoin = new Join("AppGroupToCollection", "RecentProfileForMDMResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
            userCountQuery.addJoin(managedDeviceJoin);
            final Criteria appGroupCri = new Criteria(new Column("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupId, 0);
            final Criteria markForDelete = new Criteria(new Column("RecentProfileForMDMResource", "MARKED_FOR_DELETE"), (Object)false, 0);
            userCountQuery.setCriteria(appGroupCri.and(markForDelete));
            final Column appCountColumn = Column.getColumn("AppGroupToCollection", "APP_GROUP_ID").count();
            appCountColumn.setColumnAlias("APP_COUNT");
            userCountQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"));
            userCountQuery.addSelectColumn(appCountColumn);
            final List groupByColumns = new ArrayList();
            groupByColumns.add(new Column("AppGroupToCollection", "APP_GROUP_ID"));
            final GroupByClause grouping = new GroupByClause(groupByColumns);
            userCountQuery.setGroupByClause(grouping);
            final HashMap countMap = DBUtil.executeCountQuery(userCountQuery);
            final Iterator itr = countMap.entrySet().iterator();
            if (itr.hasNext()) {
                final Map.Entry pairs = itr.next();
                count = pairs.getValue();
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAppDistributedUserCount {0}", ex);
        }
        return count;
    }
    
    public JSONObject getMaxDistributedCountForBusinessStoreApps(final Long[] appGroupId, final Long businessStoreID) {
        DMDataSetWrapper ds = null;
        int deviceCount = 0;
        int distributed_apps = 0;
        final Set appGroupSet = new HashSet();
        final JSONObject responseJSON = new JSONObject();
        try {
            SelectQuery mdAppCatalogToResQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
            mdAppCatalogToResQuery.addJoin(new Join("MdAppCatalogToResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            mdAppCatalogToResQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppCatalogToResourceExtn", new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, 2));
            mdAppCatalogToResQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "PUBLISHED_APP_ID" }, new String[] { "APP_ID" }, 2));
            Criteria recProfileForResJoinCriteria = new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), 0);
            recProfileForResJoinCriteria = recProfileForResJoinCriteria.and(new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), 0));
            mdAppCatalogToResQuery.addJoin(new Join("MdAppToCollection", "RecentProfileForResource", recProfileForResJoinCriteria, 2));
            mdAppCatalogToResQuery.addJoin(new Join("RecentProfileForResource", "MDMResourceToDeploymentConfigs", new String[] { "PROFILE_ID", "RESOURCE_ID" }, new String[] { "PROFILE_ID", "RESOURCE_ID" }, 2));
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria appGroupCri = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 8);
            final Criteria cManagedStatus = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria finalCrit = appGroupCri.and(cManagedStatus).and(businessStoreCriteria);
            mdAppCatalogToResQuery.setCriteria(finalCrit);
            mdAppCatalogToResQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"));
            mdAppCatalogToResQuery = RBDAUtil.getInstance().getRBDAQuery(mdAppCatalogToResQuery);
            ds = DMDataSetWrapper.executeQuery((Object)mdAppCatalogToResQuery);
            if (ds != null) {
                while (ds.next()) {
                    appGroupSet.add(ds.getValue("APP_GROUP_ID"));
                }
            }
            distributed_apps = appGroupSet.size();
            mdAppCatalogToResQuery.removeSelectColumn(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"));
            deviceCount = MDMDBUtil.getDistinctCount(mdAppCatalogToResQuery, "MdAppCatalogToResource", "RESOURCE_ID").optInt("DISTINCT_COUNT", 0);
            responseJSON.put("distributed_apps_count", distributed_apps);
            responseJSON.put("distributed_devices_count", deviceCount);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getMaxDistributedCountForBusinessStoreApps", e);
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
        return responseJSON;
    }
    
    public JSONObject getMaxDistributedCountForAccountApps(final Long[] appGroupId) {
        DMDataSetWrapper ds = null;
        int deviceCount = 0;
        int distributed_apps = 0;
        final Set appGroupSet = new HashSet();
        final JSONObject responseJSON = new JSONObject();
        try {
            SelectQuery mdAppCatalogToResQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
            final Join managedDeviceJoin = new Join("MdAppCatalogToResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join mdAppCatalogExtnJoin = new Join("MdAppCatalogToResource", "MdAppCatalogToResourceExtn", new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, 2);
            mdAppCatalogToResQuery.addJoin(managedDeviceJoin);
            mdAppCatalogToResQuery.addJoin(mdAppCatalogExtnJoin);
            final Criteria appGroupCri = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 8);
            final Criteria cManagedStatus = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria finalCrit = appGroupCri.and(cManagedStatus);
            mdAppCatalogToResQuery.setCriteria(finalCrit);
            mdAppCatalogToResQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"));
            mdAppCatalogToResQuery = RBDAUtil.getInstance().getRBDAQuery(mdAppCatalogToResQuery);
            ds = DMDataSetWrapper.executeQuery((Object)mdAppCatalogToResQuery);
            if (ds != null) {
                while (ds.next()) {
                    appGroupSet.add(ds.getValue("APP_GROUP_ID"));
                }
            }
            distributed_apps = appGroupSet.size();
            mdAppCatalogToResQuery.removeSelectColumn(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"));
            deviceCount = MDMDBUtil.getDistinctCount(mdAppCatalogToResQuery, "MdAppCatalogToResource", "RESOURCE_ID").optInt("DISTINCT_COUNT", 0);
            responseJSON.put("distributed_apps_count", distributed_apps);
            responseJSON.put("distributed_devices_count", deviceCount);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Issue in getting max distributed count for account apps");
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
        return responseJSON;
    }
    
    private Boolean isAppGroupIosNonEnterpriseApp(final Long appGroupId) throws DataAccessException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        sQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        sQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        final Criteria platformCrit = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)1, 0);
        final Criteria packageTypeCrit = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 1);
        final Criteria appGroupIdCrit = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0);
        sQuery.setCriteria(platformCrit.and(packageTypeCrit).and(appGroupIdCrit));
        final DataObject dao = MDMUtil.getPersistenceLite().get(sQuery);
        Boolean retVal = Boolean.FALSE;
        if (!dao.isEmpty() && dao.getRows("MdAppGroupDetails", appGroupIdCrit).hasNext()) {
            retVal = Boolean.TRUE;
        }
        return retVal;
    }
    
    public Long addAutoAppUpdateConfig(final JSONObject jsonObject) throws Exception {
        final DataObject dataObject = (DataObject)new WritableDataObject();
        try {
            final Long customerId = jsonObject.getLong("customerId");
            final Long userId = jsonObject.getLong("userId");
            final Boolean silentInstall = jsonObject.optBoolean("silent_install");
            final Boolean notifyUser = jsonObject.optBoolean("notify_user_via_email");
            final Boolean allPackages = jsonObject.optBoolean("all_apps");
            final Boolean allResources = jsonObject.optBoolean("all_resources");
            final Boolean packageExclusion = jsonObject.optBoolean("exclude_app");
            final Boolean resourceExclusion = jsonObject.optBoolean("exclude_resource");
            final JSONArray resourceArr = jsonObject.optJSONArray("resource_ids");
            final JSONArray packageArr = jsonObject.optJSONArray("app_ids");
            final String description = jsonObject.optString("description");
            List<Long> resourceList = null;
            List<Long> packageList = null;
            if (resourceArr != null) {
                resourceList = new ArrayList<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(jsonObject.getJSONArray("resource_ids")));
            }
            if (packageArr != null) {
                packageList = new ArrayList<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(jsonObject.getJSONArray("app_ids")));
            }
            final JSONObject deploymentJSON = new JSONObject();
            deploymentJSON.put("EMAIL_NOTIFY_END_USER", (Object)notifyUser);
            deploymentJSON.put("FORCE_APP_INSTALL", (Object)silentInstall);
            Long deploymentConfigId = null;
            if (!packageExclusion && !resourceExclusion) {
                deploymentConfigId = new AppDeploymentPolicyNonUiManager().getDeploymentConfigIdForAppDeployment(customerId, userId, deploymentJSON);
            }
            final Row userconfigrow = new Row("AutoAppUpdateConfigDetails");
            userconfigrow.set("CREATION_TIME", (Object)System.currentTimeMillis());
            userconfigrow.set("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
            userconfigrow.set("LAST_MODIFIED_BY", (Object)userId);
            userconfigrow.set("CUSTOMER_ID", (Object)customerId);
            userconfigrow.set("DESCRIPTION", (Object)description);
            dataObject.addRow(userconfigrow);
            if (deploymentConfigId != null) {
                final Row deployConfRow = new Row("AutoAppUpdateDeploymentSettings");
                deployConfRow.set("APP_UPDATE_CONF_ID", userconfigrow.get("APP_UPDATE_CONF_ID"));
                deployConfRow.set("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
                dataObject.addRow(deployConfRow);
            }
            final Row packageConfigRow = new Row("AutoAppUpdatePackageConfig");
            packageConfigRow.set("APP_UPDATE_CONF_ID", userconfigrow.get("APP_UPDATE_CONF_ID"));
            packageConfigRow.set("INCLUSION_FLAG", (Object)!packageExclusion);
            if (allPackages) {
                packageConfigRow.set("ALL_APPS", (Object)Boolean.TRUE);
                dataObject.addRow(packageConfigRow);
            }
            else {
                packageConfigRow.set("ALL_APPS", (Object)Boolean.FALSE);
                dataObject.addRow(packageConfigRow);
                for (final Long packageId : packageList) {
                    final Row packageRowList = new Row("AutoAppUpdatePackageList");
                    packageRowList.set("APP_UPDATE_CONF_ID", userconfigrow.get("APP_UPDATE_CONF_ID"));
                    packageRowList.set("PACKAGE_ID", (Object)packageId);
                    dataObject.addRow(packageRowList);
                }
            }
            final Row resourceConfigRow = new Row("AutoAppUpdateResourceConfig");
            resourceConfigRow.set("APP_UPDATE_CONF_ID", userconfigrow.get("APP_UPDATE_CONF_ID"));
            resourceConfigRow.set("INCLUSION_FLAG", (Object)!resourceExclusion);
            if (allResources) {
                resourceConfigRow.set("ALL_RESOURCES", (Object)Boolean.TRUE);
                dataObject.addRow(resourceConfigRow);
            }
            else {
                resourceConfigRow.set("ALL_RESOURCES", (Object)Boolean.FALSE);
                dataObject.addRow(resourceConfigRow);
                for (final Long resourceId : resourceList) {
                    final Row resourceRowList = new Row("AutoAppUpdateResourceList");
                    resourceRowList.set("APP_UPDATE_CONF_ID", userconfigrow.get("APP_UPDATE_CONF_ID"));
                    resourceRowList.set("RESOURCE_ID", (Object)resourceId);
                    dataObject.addRow(resourceRowList);
                }
            }
            MDMUtil.getPersistence().add(dataObject);
            final Row confIdRow = dataObject.getRow("AutoAppUpdateConfigDetails");
            final Long confId = (Long)confIdRow.get("APP_UPDATE_CONF_ID");
            return confId;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in adding auto app update configuration", ex);
            throw ex;
        }
    }
    
    public void deleteAutoAppUpdateConfig(final JSONObject jsonObject) throws Exception {
        try {
            final Long customerId = jsonObject.getLong("customerId");
            final Long appUpdateConfigId = jsonObject.getLong("appUpdateConfId");
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("AutoAppUpdateConfigDetails");
            final Criteria custcriteria = new Criteria(new Column("AutoAppUpdateConfigDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria appUpdtConfCritiera = new Criteria(new Column("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"), (Object)appUpdateConfigId, 0);
            deleteQuery.setCriteria(custcriteria.and(appUpdtConfCritiera));
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in deleting auto app update configuration {0}", ex);
            throw ex;
        }
    }
    
    public JSONObject getAutoAppUpdateConfig(final JSONObject jsonObject) throws Exception {
        final Long appUpdateConfigId = JSONUtil.optLong(jsonObject, "appUpdateConfId", -1L);
        return this.getAutoAppUpdateConfigList(jsonObject, appUpdateConfigId);
    }
    
    private List<Long> getResourcesConfiguredForAppUpdate(final Long appUpdateConfId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdateResourceList"));
        selectQuery.setCriteria(new Criteria(new Column("AutoAppUpdateResourceList", "APP_UPDATE_CONF_ID"), (Object)appUpdateConfId, 0));
        selectQuery.addSelectColumn(new Column("AutoAppUpdateResourceList", "RESOURCE_ID"));
        final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final List resourceList = new ArrayList();
        if (dataSet != null) {
            while (dataSet.next()) {
                resourceList.add(dataSet.getValue("RESOURCE_ID"));
            }
        }
        return resourceList;
    }
    
    private List<Long> getAppsConfiguredForAppUpdate(final Long appUpdateConfId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdatePackageList"));
        selectQuery.setCriteria(new Criteria(new Column("AutoAppUpdatePackageList", "APP_UPDATE_CONF_ID"), (Object)appUpdateConfId, 0));
        selectQuery.addSelectColumn(new Column("AutoAppUpdatePackageList", "PACKAGE_ID"));
        final DMDataSetWrapper dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
        final List packageList = new ArrayList();
        if (dataSet != null) {
            while (dataSet.next()) {
                packageList.add(dataSet.getValue("PACKAGE_ID"));
            }
        }
        return packageList;
    }
    
    public JSONObject getUpdateConfigurationForApp(final Long packageId, final Long customerId) throws Exception {
        final JSONObject globalLevel = this.getGlobalLevelConfigurationForPackage(packageId, customerId);
        final JSONObject appConf = new JSONObject();
        final Set<Long> tobeExcludedResources = new HashSet<Long>();
        if (globalLevel != null && globalLevel.has("global_resources")) {
            appConf.put("global_resources", (Object)globalLevel.getJSONObject("global_resources"));
            if (globalLevel.getJSONObject("global_resources").has("excluded_resources")) {
                tobeExcludedResources.addAll(JSONUtil.getInstance().convertLongJSONArrayTOList(new JSONArray(String.valueOf(globalLevel.getJSONObject("global_resources").get("excluded_resources")))));
            }
        }
        final JSONObject resourceLevel = this.getResourceLevelConfigurationForPackage(packageId);
        if (resourceLevel != null && resourceLevel.has("app_update_configurations")) {
            final JSONObject appUpdtConf = resourceLevel.getJSONObject("app_update_configurations");
            if (appUpdtConf.has("never_update_resources")) {
                tobeExcludedResources.addAll(JSONUtil.getInstance().convertLongJSONArrayTOList(new JSONArray(String.valueOf(appUpdtConf.get("never_update_resources")))));
            }
            if (appUpdtConf.has("global_resources")) {
                appConf.put("global_resources", (Object)appUpdtConf.getJSONObject("global_resources"));
            }
            if (appUpdtConf.has("specific_included_resources")) {
                appConf.put("specific_included_resources", (Object)appUpdtConf.getJSONArray("specific_included_resources"));
            }
        }
        if (resourceLevel != null && resourceLevel.has("totalIncluded")) {
            tobeExcludedResources.removeAll(JSONUtil.getInstance().convertLongJSONArrayTOList(new JSONArray(String.valueOf(resourceLevel.get("totalIncluded")))));
        }
        if (tobeExcludedResources.size() > 0) {
            appConf.put("never_update_resources", (Collection)JSONUtil.convertListToJSONArray((Long[])tobeExcludedResources.toArray(new Long[tobeExcludedResources.size()])));
        }
        if (appConf.length() > 0) {
            return JSONUtil.toJSON("app_update_configurations", appConf).put("customer_id", (Object)customerId);
        }
        return null;
    }
    
    private HashMap getYetToUpdateResource(final Long appgroupId, final String includeList, final String excludeList, final Long appReleaseLabelID) throws JSONException {
        final HashMap hashMap = new HashMap();
        final Set groupResourceList = new HashSet();
        final Set resourceList = new HashSet();
        final Set groupList = new HashSet();
        List includedResources = new ArrayList();
        List excludedResources = new ArrayList();
        JSONArray includeArray = new JSONArray();
        JSONArray excludeArray = new JSONArray();
        if (includeList != null && !includeList.isEmpty()) {
            includeArray = new JSONArray(includeList);
        }
        if (excludeList != null && !excludeList.isEmpty()) {
            excludeArray = new JSONArray(excludeList);
        }
        if (includeArray != null && includeArray.length() > 0) {
            includedResources = JSONUtil.getInstance().convertLongJSONArrayTOList(includeArray);
        }
        if (excludeArray != null && excludeArray.length() > 0) {
            excludedResources = JSONUtil.getInstance().convertLongJSONArrayTOList(excludeArray);
        }
        try {
            final SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToGroup"));
            groupQuery.addJoin(new Join("MdAppCatalogToGroup", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            groupQuery.addJoin(new Join("MdAppCatalogToGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            groupQuery.addJoin(new Join("MdAppCatalogToGroup", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            groupQuery.addJoin(new Join("MdPackageToAppGroup", "MdPackage", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            groupQuery.addJoin(new Join("MdAppCatalogToGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1));
            groupQuery.addJoin(new Join("MdAppCatalogToGroup", "MDMResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            groupQuery.addJoin(new Join("MdAppCatalogToGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            groupQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            groupQuery.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"));
            groupQuery.addSelectColumn(Column.getColumn("MdAppCatalogToGroup", "RESOURCE_ID"));
            groupQuery.addSelectColumn(Column.getColumn("MdAppCatalogToGroup", "APP_GROUP_ID"));
            final Criteria grpAppGroupCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appgroupId, 0);
            final Criteria grpUpdateCriteria = new Criteria(Column.getColumn("MdAppCatalogToGroup", "IS_UPDATE_AVAILABLE"), (Object)true, 0);
            final Criteria labelCriteria = new Criteria(new Column("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)appReleaseLabelID, 0);
            Criteria criteria = grpUpdateCriteria.and(grpAppGroupCriteria).and(labelCriteria);
            if (includedResources.size() > 0) {
                criteria = criteria.and(new Criteria(new Column("MDMResource", "RESOURCE_ID"), (Object)includedResources.toArray(), 8));
            }
            if (excludedResources.size() > 0) {
                criteria = criteria.and(new Criteria(new Column("MDMResource", "RESOURCE_ID"), (Object)excludedResources.toArray(), 9));
            }
            groupQuery.setCriteria(criteria);
            DataObject dataObject = MDMUtil.getPersistence().get(groupQuery);
            final Iterator groupIterator = dataObject.getRows("MdAppCatalogToGroup");
            while (groupIterator.hasNext()) {
                final Row row = groupIterator.next();
                final Long grpID = (Long)row.get("RESOURCE_ID");
                groupList.add(grpID);
            }
            Iterator iterator = dataObject.getRows("CustomGroupMemberRel");
            while (iterator.hasNext()) {
                final Row row2 = iterator.next();
                final Long resID = (Long)row2.get("MEMBER_RESOURCE_ID");
                final Long grpID2 = (Long)row2.get("GROUP_RESOURCE_ID");
                groupResourceList.add(resID);
                groupList.add(grpID2);
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResourceExtn"));
            selectQuery.addJoin(new Join("MdAppCatalogToResourceExtn", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppCatalogToResourceExtn", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdAppCatalogToResourceExtn", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria packageCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appgroupId, 0);
            final Criteria updateCriteria = new Criteria(Column.getColumn("MdAppCatalogToResourceExtn", "IS_UPDATE_AVAILABLE"), (Object)true, 0);
            final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            final Criteria resourceToUpdatedCriteria = packageCriteria.and(updateCriteria).and(managedDeviceCriteria).and(labelCriteria);
            selectQuery.setCriteria(resourceToUpdatedCriteria);
            if (includedResources.size() > 0) {
                selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)includedResources.toArray(), 8)));
            }
            if (excludedResources.size() > 0) {
                selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)excludedResources.toArray(), 9)));
            }
            selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResourceExtn", "RESOURCE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResourceExtn", "APP_GROUP_ID"));
            dataObject = MDMUtil.getPersistence().get(selectQuery);
            iterator = dataObject.getRows("MdAppCatalogToResourceExtn");
            while (iterator.hasNext()) {
                final Row row3 = iterator.next();
                final Long resID2 = (Long)row3.get("RESOURCE_ID");
                resourceList.add(resID2);
            }
            final List induvidualList = new ArrayList();
            induvidualList.addAll(resourceList);
            induvidualList.removeAll(groupResourceList);
            hashMap.put("groupList", new ArrayList(groupList));
            hashMap.put("resourceList", induvidualList);
            hashMap.put("totalResourceList", resourceList);
            hashMap.put("excludeList", excludedResources);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getYetoToUpdateResource :{0}", ex);
        }
        return hashMap;
    }
    
    public JSONObject getAutoAppUpdateConfigList(final JSONObject jsonObject) throws Exception {
        return this.getAutoAppUpdateConfigList(jsonObject, null);
    }
    
    private JSONObject getAutoAppUpdateConfigList(final JSONObject jsonObject, final Long appUpdateId) throws Exception {
        JSONObject responseJSON = null;
        final JSONArray configList = new JSONArray();
        try {
            final Long customerId = jsonObject.getLong("customerId");
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdateConfigDetails"));
            final Join packageConfJoin = new Join("AutoAppUpdateConfigDetails", "AutoAppUpdatePackageConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2);
            final Join resourceConfJoin = new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateResourceConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2);
            final Join deploymentConfJoin = new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateDeploymentSettings", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1);
            final Criteria customerCri = new Criteria(new Column("AutoAppUpdateConfigDetails", "CUSTOMER_ID"), (Object)customerId, 0);
            selectQuery.addJoin(packageConfJoin);
            selectQuery.addJoin(resourceConfJoin);
            selectQuery.addJoin(deploymentConfJoin);
            selectQuery.setCriteria(customerCri);
            if (appUpdateId != null) {
                final Criteria appUpdateConfigCri = new Criteria(new Column("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"), (Object)appUpdateId, 0);
                selectQuery.setCriteria(customerCri.and(appUpdateConfigCri));
            }
            selectQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> userConfRows = dataObject.getRows("AutoAppUpdateConfigDetails");
                while (userConfRows.hasNext()) {
                    responseJSON = new JSONObject();
                    final Row userConfRow = userConfRows.next();
                    final Long appUpdateConfigId = (Long)userConfRow.get("APP_UPDATE_CONF_ID");
                    responseJSON.put("app_update_conf_id", userConfRow.get("APP_UPDATE_CONF_ID"));
                    responseJSON.put("created_time", userConfRow.get("CREATION_TIME"));
                    responseJSON.put("last_modified_time", userConfRow.get("LAST_MODIFIED_TIME"));
                    responseJSON.put("last_modified_by", userConfRow.get("LAST_MODIFIED_BY"));
                    responseJSON.put("customer_id", userConfRow.get("CUSTOMER_ID"));
                    responseJSON.put("description", userConfRow.get("DESCRIPTION"));
                    final Criteria confCriteria = new Criteria(new Column("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"), (Object)appUpdateConfigId, 0);
                    final Row deployRow = dataObject.getRow("AutoAppUpdateDeploymentSettings", confCriteria);
                    if (deployRow != null) {
                        final Long deploymentConfigId = (Long)deployRow.get("DEPLOYMENT_CONFIG_ID");
                        if (deploymentConfigId != null) {
                            final JSONObject policyJSON = new AppDeploymentPolicyImpl().getAppDeploymentPolicy(deploymentConfigId);
                            if (policyJSON != null) {
                                responseJSON.put("silent_install", (Object)policyJSON.optBoolean("silent_install"));
                                responseJSON.put("notify_user_via_email", (Object)policyJSON.optBoolean("notify_user_via_email"));
                            }
                        }
                    }
                    final Row packageConfRow = dataObject.getRow("AutoAppUpdatePackageConfig", confCriteria);
                    responseJSON.put("exclude_app", !(boolean)packageConfRow.get("INCLUSION_FLAG"));
                    final Boolean allApps = (Boolean)packageConfRow.get("ALL_APPS");
                    responseJSON.put("all_apps", (Object)allApps);
                    if (!allApps) {
                        final List<Long> appList = this.getAppsConfiguredForAppUpdate(appUpdateConfigId);
                        responseJSON.put("app_ids", (Collection)JSONUtil.convertListToJSONArray((Long[])appList.toArray(new Long[appList.size()])));
                    }
                    final Row resourceConfRow = dataObject.getRow("AutoAppUpdateResourceConfig", confCriteria);
                    responseJSON.put("exclude_resource", !(boolean)resourceConfRow.get("INCLUSION_FLAG"));
                    final Boolean allResources = (Boolean)resourceConfRow.get("ALL_RESOURCES");
                    responseJSON.put("all_resources", (Object)allResources);
                    if (!allResources) {
                        final List<Long> resourceList = this.getResourcesConfiguredForAppUpdate(appUpdateConfigId);
                        responseJSON.put("resource_ids", (Collection)JSONUtil.convertListToJSONArray((Long[])resourceList.toArray(new Long[resourceList.size()])));
                    }
                    configList.put((Object)responseJSON);
                }
            }
            if (configList.length() > 0) {
                return new JSONObject().put("config_list", (Object)configList);
            }
            return new JSONObject().put("config_list", (Object)new JSONArray());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getting auto app update configuration {0}", ex);
            throw ex;
        }
    }
    
    public void performAutoAppUpdate(final Long appGroupID, final Long customerID, final JSONObject jsonObject) throws Exception {
        final Properties properties = new Properties();
        ((Hashtable<String, Boolean>)properties).put("isSilentInstall", jsonObject.optBoolean("silent_install"));
        ((Hashtable<String, Boolean>)properties).put("isNotify", jsonObject.optBoolean("notify_user_via_email"));
        ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
        ((Hashtable<String, Long>)properties).put("customerId", customerID);
        final Long releaseLabelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(customerID);
        final HashMap<Long, Long> profileCollectionMap = this.getLatestProfileCollectionMapForAppGroup(appGroupID, releaseLabelId);
        ((Hashtable<String, HashMap<Long, Long>>)properties).put("profileCollectionMap", profileCollectionMap);
        ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
        ((Hashtable<String, Integer>)properties).put("toBeAssociatedAppSource", MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_AUTO_UPDATE);
        final HashMap resourceMap = this.getYetToUpdateResource(appGroupID, jsonObject.optString("include_resources"), jsonObject.optString("excluded_resources"), releaseLabelId);
        final List groupList = resourceMap.get("groupList");
        final List resList = resourceMap.get("resourceList");
        final List excludeList = resourceMap.get("excludeList");
        final List totalResourceList = new ArrayList(resourceMap.get("totalResourceList"));
        final Long packageId = jsonObject.getLong("package_id");
        final Long addedByUser = (Long)DBUtil.getValueFromDB("MdPackage", "PACKAGE_ID", (Object)packageId, "PACKAGE_MODIFIED_BY");
        ((Hashtable<String, Long>)properties).put("loggedOnUser", addedByUser);
        ((Hashtable<String, List>)properties).put("excludeList", excludeList);
        final ArrayList<Long> profileList = new ArrayList<Long>(profileCollectionMap.keySet());
        final Long profileId = profileList.get(0);
        final List removableGroupList = this.getResourcesWithProfileTobeRemoved(profileId, groupList, true);
        final List removableResourceList = this.getResourcesWithProfileTobeRemoved(profileId, resList, false);
        groupList.removeAll(removableGroupList);
        resList.removeAll(removableResourceList);
        totalResourceList.removeAll(removableGroupList);
        totalResourceList.removeAll(removableResourceList);
        final String sEventLogRemarks = "dc.mdm.actionlog.appmgmt.autoupdate_started";
        final int eventLogConstant = 2031;
        if (!jsonObject.optBoolean("silent_install", false)) {
            final Properties groupProperties = (Properties)properties.clone();
            ((Hashtable<String, Boolean>)groupProperties).put("isGroup", true);
            ((Hashtable<String, List>)groupProperties).put("resourceList", groupList);
            ((Hashtable<String, Integer>)groupProperties).put("groupType", 6);
            this.logger.log(Level.INFO, "Auto Update has been triggered update for app with properties (Group) {0}", groupProperties);
            ProfileAssociateHandler.getInstance().associateCollectionForGroup(groupProperties);
            final Properties resourceProperties = (Properties)properties.clone();
            ((Hashtable<String, Boolean>)resourceProperties).put("isGroup", false);
            ((Hashtable<String, List>)resourceProperties).put("resourceList", resList);
            ((Hashtable<String, Boolean>)resourceProperties).put("profileOrigin", false);
            ProfileAssociateHandler.getInstance().associateCollectionForResource(resourceProperties);
            this.logger.log(Level.INFO, "Auto Update has been triggered update for app with properties (Resource) {0}", resourceProperties);
        }
        else {
            final AppDeploymentPolicyImpl appDeploymentPolicy = new AppDeploymentPolicyImpl();
            final List<Long> silentGroupInstallList = appDeploymentPolicy.getSilentInstallDeployedResources(groupList, profileId, null);
            final List<Long> silentResInstallList = appDeploymentPolicy.getSilentInstallDeployedResources(resList, profileId, null);
            final Properties silentGroupProperties = (Properties)properties.clone();
            ((Hashtable<String, Boolean>)silentGroupProperties).put("isGroup", true);
            ((Hashtable<String, Boolean>)silentGroupProperties).put("isSilentInstall", Boolean.TRUE);
            ((Hashtable<String, List<Long>>)silentGroupProperties).put("resourceList", silentGroupInstallList);
            ((Hashtable<String, Integer>)silentGroupProperties).put("groupType", 6);
            this.logger.log(Level.INFO, "Auto Update - Silent has been triggered update for app with properties (Group) {0}", silentGroupProperties);
            ProfileAssociateHandler.getInstance().associateCollectionForGroup(silentGroupProperties);
            final Properties catalogGroupProperties = (Properties)properties.clone();
            groupList.removeAll(silentGroupInstallList);
            ((Hashtable<String, Boolean>)catalogGroupProperties).put("isGroup", true);
            ((Hashtable<String, Boolean>)catalogGroupProperties).put("isSilentInstall", Boolean.FALSE);
            ((Hashtable<String, List>)catalogGroupProperties).put("resourceList", groupList);
            ((Hashtable<String, Integer>)catalogGroupProperties).put("groupType", 6);
            this.logger.log(Level.INFO, "Auto Update - catalog has been triggered update for app with properties (Group) {0}", catalogGroupProperties);
            ProfileAssociateHandler.getInstance().associateCollectionForGroup(catalogGroupProperties);
            final Properties silentDevicesProperties = (Properties)properties.clone();
            ((Hashtable<String, Boolean>)silentDevicesProperties).put("isGroup", false);
            ((Hashtable<String, Boolean>)silentDevicesProperties).put("profileOrigin", false);
            final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
            final List<Long> alreadyInstalledDevices = handler.getAppInstalledDevices(appGroupID, totalResourceList);
            silentResInstallList.addAll(alreadyInstalledDevices);
            ((Hashtable<String, Boolean>)silentDevicesProperties).put("isSilentInstall", Boolean.TRUE);
            ((Hashtable<String, List<Long>>)silentDevicesProperties).put("resourceList", silentResInstallList);
            ProfileAssociateHandler.getInstance().associateCollectionForResource(silentDevicesProperties);
            this.logger.log(Level.INFO, "Auto Update - Silent has been triggered update for app with properties (Resource) {0}", silentDevicesProperties);
            resList.removeAll(silentResInstallList);
            final Properties catalogDevicesProperties = (Properties)properties.clone();
            ((Hashtable<String, Boolean>)catalogDevicesProperties).put("isGroup", false);
            ((Hashtable<String, Boolean>)catalogDevicesProperties).put("profileOrigin", false);
            ((Hashtable<String, Boolean>)catalogDevicesProperties).put("isSilentInstall", Boolean.FALSE);
            ((Hashtable<String, List>)catalogDevicesProperties).put("resourceList", resList);
            ProfileAssociateHandler.getInstance().associateCollectionForResource(catalogDevicesProperties);
            this.logger.log(Level.INFO, "Auto Update - catalog has been triggered update for app with properties (Resource) {0}", catalogDevicesProperties);
        }
        MDMEventLogHandler.getInstance().MDMEventLogEntry(eventLogConstant, null, null, sEventLogRemarks, this.getAppName(packageId), customerID);
    }
    
    public void deleteUpdateConfFromApp(final Long customerId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdateConfigDetails"));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdatePackageConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdatePackageList", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateConfigToCollection", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
        final Criteria customerCriteria = new Criteria(new Column("AutoAppUpdateConfigDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria nonAllAppsCriteria = new Criteria(new Column("AutoAppUpdatePackageConfig", "ALL_APPS"), (Object)Boolean.FALSE, 0);
        final Criteria nonEnterpriseAppPolicy = new Criteria(new Column("AutoAppUpdateConfigToCollection", "COLLECTION_ID"), (Object)null, 0);
        selectQuery.setCriteria(customerCriteria.and(nonAllAppsCriteria).and(nonEnterpriseAppPolicy));
        selectQuery.addSelectColumn(new Column("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"));
        selectQuery.addSelectColumn(new Column("AutoAppUpdatePackageList", "APP_UPDATE_CONF_ID"));
        selectQuery.addSelectColumn(new Column("AutoAppUpdatePackageList", "PACKAGE_ID"));
        final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdateConfigDetails"));
        selectQuery2.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdatePackageList", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
        selectQuery2.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdatePackageConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        selectQuery2.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateConfigToCollection", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
        selectQuery2.setCriteria(customerCriteria.and(nonAllAppsCriteria).and(nonEnterpriseAppPolicy));
        selectQuery2.addSelectColumn(new Column("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"));
        selectQuery2.addSelectColumn(new Column("AutoAppUpdatePackageList", "APP_UPDATE_CONF_ID"));
        selectQuery2.addSelectColumn(new Column("AutoAppUpdatePackageList", "PACKAGE_ID"));
        try {
            final DataObject dmObject = MDMUtil.getPersistence().get(selectQuery);
            final DataObject dmObject2 = MDMUtil.getPersistence().get(selectQuery2);
            final DataObject diffObject = dmObject2.diff(dmObject);
            MDMUtil.getPersistence().update(diffObject);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in deleteUpdateConfFromApp", (Throwable)e);
            throw e;
        }
    }
    
    public void deleteUpdateConfFromResource(final Long customerId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdateConfigDetails"));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateResourceConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        selectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateResourceList", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        final Criteria customerCriteria = new Criteria(new Column("AutoAppUpdateConfigDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria nonAllResCriteria = new Criteria(new Column("AutoAppUpdateResourceConfig", "ALL_RESOURCES"), (Object)Boolean.FALSE, 0);
        selectQuery.setCriteria(customerCriteria.and(nonAllResCriteria));
        selectQuery.addSelectColumn(new Column("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"));
        selectQuery.addSelectColumn(new Column("AutoAppUpdateResourceList", "APP_UPDATE_CONF_ID"));
        selectQuery.addSelectColumn(new Column("AutoAppUpdateResourceList", "RESOURCE_ID"));
        final SelectQuery selectQuery2 = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdateConfigDetails"));
        selectQuery2.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateResourceList", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
        selectQuery2.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateResourceConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        selectQuery2.setCriteria(customerCriteria.and(nonAllResCriteria));
        selectQuery2.addSelectColumn(new Column("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"));
        selectQuery2.addSelectColumn(new Column("AutoAppUpdateResourceList", "APP_UPDATE_CONF_ID"));
        selectQuery2.addSelectColumn(new Column("AutoAppUpdateResourceList", "RESOURCE_ID"));
        try {
            final DataObject dmObject = MDMUtil.getPersistence().get(selectQuery);
            final DataObject dmObject2 = MDMUtil.getPersistence().get(selectQuery2);
            final DataObject diffObject = dmObject2.diff(dmObject);
            MDMUtil.getPersistence().update(diffObject);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in deleteUpdateConfFromResource", (Throwable)e);
            throw e;
        }
    }
    
    private JSONObject getResourceLevelConfigurationForPackage(final Long packageId) throws Exception {
        final JSONObject configurationJSON = new JSONObject();
        final Set excludeSpecificResources = new HashSet();
        final Criteria allAppsCri = new Criteria(new Column("AutoAppUpdatePackageConfig", "ALL_APPS"), (Object)Boolean.TRUE, 0);
        final Criteria includeResourcesCri = new Criteria(new Column("AutoAppUpdateResourceConfig", "INCLUSION_FLAG"), (Object)Boolean.TRUE, 0);
        final Criteria allResourcesCri = new Criteria(new Column("AutoAppUpdateResourceConfig", "ALL_RESOURCES"), (Object)Boolean.TRUE, 0);
        final SelectQuery specificAppQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdatePackageConfig"));
        specificAppQuery.addJoin(new Join("AutoAppUpdatePackageConfig", "AutoAppUpdatePackageList", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
        specificAppQuery.addJoin(new Join("AutoAppUpdatePackageConfig", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        specificAppQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateResourceConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        specificAppQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateDeploymentSettings", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
        final Criteria packageCriteria = new Criteria(new Column("AutoAppUpdatePackageList", "PACKAGE_ID"), (Object)packageId, 0);
        final Criteria includeCriteria = new Criteria(new Column("AutoAppUpdatePackageConfig", "INCLUSION_FLAG"), (Object)Boolean.TRUE, 0);
        specificAppQuery.setCriteria(packageCriteria.and(includeCriteria).and(allResourcesCri).and(includeResourcesCri));
        specificAppQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject specificAllResDO = MDMUtil.getPersistence().get(specificAppQuery);
        if (!specificAllResDO.isEmpty()) {
            final JSONObject allRes = new JSONObject();
            specificAppQuery.setCriteria(packageCriteria.or(allAppsCri).and(includeCriteria).and(allResourcesCri.negate()).and(includeResourcesCri.negate()));
            final DataObject excludeResList = MDMUtil.getPersistence().get(specificAppQuery);
            if (!excludeResList.isEmpty()) {
                final Iterator<Row> excludeRes = excludeResList.getRows("AutoAppUpdateResourceConfig");
                while (excludeRes.hasNext()) {
                    final Row excludeRow = excludeRes.next();
                    excludeSpecificResources.addAll(this.getResourcesConfiguredForAppUpdate((Long)excludeRow.get("APP_UPDATE_CONF_ID")));
                }
            }
            if (!excludeSpecificResources.isEmpty()) {
                allRes.put("excluded_resources", (Collection)Arrays.asList(excludeSpecificResources.toArray()));
            }
            final Iterator criiterator = specificAllResDO.getRows("AutoAppUpdateConfigDetails", includeResourcesCri);
            if (criiterator.hasNext()) {
                final Long appUpdtId = (Long)criiterator.next().get("APP_UPDATE_CONF_ID");
                allRes.put("deployment_config", DBUtil.getValueFromDB("AutoAppUpdateDeploymentSettings", "APP_UPDATE_CONF_ID", (Object)appUpdtId, "DEPLOYMENT_CONFIG_ID"));
            }
            allRes.put("deploy_for_all_resources", true);
            configurationJSON.put("global_resources", (Object)allRes);
        }
        specificAppQuery.setCriteria(packageCriteria.and(includeCriteria).and(allResourcesCri.negate()).and(includeResourcesCri));
        final DataObject specificAppRes = MDMUtil.getPersistence().get(specificAppQuery);
        final Map<Long, Set> deploytoResources = new HashMap<Long, Set>();
        final Set specificIncludeList = new HashSet();
        if (!specificAppRes.isEmpty()) {
            final Iterator<Row> iter = specificAppRes.getRows("AutoAppUpdateDeploymentSettings");
            while (iter.hasNext()) {
                final Row confRow = iter.next();
                final Long deployConf = (Long)confRow.get("DEPLOYMENT_CONFIG_ID");
                if (!deploytoResources.containsKey(deployConf)) {
                    deploytoResources.put(deployConf, new HashSet());
                }
                final List resourceList = new ArrayList(this.getResourcesConfiguredForAppUpdate((Long)confRow.get("APP_UPDATE_CONF_ID")));
                specificIncludeList.addAll(resourceList);
                deploytoResources.get(deployConf).addAll(resourceList);
            }
        }
        final SelectQuery allAppsSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdatePackageConfig"));
        allAppsSelectQuery.addJoin(new Join("AutoAppUpdatePackageConfig", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        allAppsSelectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateResourceConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        allAppsSelectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateDeploymentSettings", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        allAppsSelectQuery.setCriteria(allAppsCri.and(includeResourcesCri).and(allResourcesCri.negate()));
        allAppsSelectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject allAppsSpecificResource = MDMUtil.getPersistence().get(allAppsSelectQuery);
        if (!allAppsSpecificResource.isEmpty()) {
            final Iterator<Row> iter2 = allAppsSpecificResource.getRows("AutoAppUpdateDeploymentSettings");
            while (iter2.hasNext()) {
                final Row confRow2 = iter2.next();
                final Long deployConf2 = (Long)confRow2.get("DEPLOYMENT_CONFIG_ID");
                if (!deploytoResources.containsKey(deployConf2)) {
                    deploytoResources.put(deployConf2, new HashSet());
                }
                final List resourceList2 = new ArrayList(this.getResourcesConfiguredForAppUpdate((Long)confRow2.get("APP_UPDATE_CONF_ID")));
                specificIncludeList.addAll(resourceList2);
                deploytoResources.get(deployConf2).addAll(resourceList2);
            }
        }
        final JSONArray includeConfArray = new JSONArray();
        final List deploymentSet = new ArrayList(deploytoResources.keySet());
        for (int i = 0; i < deploymentSet.size(); ++i) {
            final JSONObject deployResources = new JSONObject();
            final Long deploymentId = deploymentSet.get(i);
            final List<Long> arrayList = new ArrayList<Long>();
            arrayList.addAll(deploytoResources.get(deploymentId));
            deployResources.put("include_resources", (Collection)JSONUtil.convertListToJSONArray((Long[])arrayList.toArray(new Long[arrayList.size()])));
            deployResources.put("deployment_config", (Object)deploymentId);
            includeConfArray.put((Object)deployResources);
        }
        if (includeConfArray.length() > 0) {
            configurationJSON.put("specific_included_resources", (Object)includeConfArray);
        }
        specificAppQuery.setCriteria(packageCriteria.and(includeCriteria).and(allResourcesCri.negate()).and(includeResourcesCri.negate()));
        final DataObject specificAppResExclude = MDMUtil.getPersistence().get(specificAppQuery);
        final Set specificAppSpecificResExclude = new HashSet();
        if (!specificAppResExclude.isEmpty()) {
            final Iterator<Row> iter3 = specificAppResExclude.getRows("AutoAppUpdateConfigDetails");
            while (iter3.hasNext()) {
                final Row confRow3 = iter3.next();
                specificAppSpecificResExclude.addAll(this.getResourcesConfiguredForAppUpdate((Long)confRow3.get("APP_UPDATE_CONF_ID")));
            }
        }
        if (!specificIncludeList.isEmpty()) {
            specificAppSpecificResExclude.removeAll(specificIncludeList);
        }
        if (!specificAppSpecificResExclude.isEmpty()) {
            configurationJSON.put("never_update_resources", (Collection)specificAppSpecificResExclude);
        }
        if (configurationJSON.length() > 0) {
            return JSONUtil.toJSON("app_update_configurations", configurationJSON).put("totalIncluded", (Collection)specificIncludeList);
        }
        return null;
    }
    
    private JSONObject getGlobalLevelConfigurationForPackage(final Long packageId, final Long customerId) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        final SelectQuery allAppsSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdatePackageConfig"));
        allAppsSelectQuery.addJoin(new Join("AutoAppUpdatePackageConfig", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        allAppsSelectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateResourceConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        allAppsSelectQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateDeploymentSettings", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
        final Criteria allAppsCri = new Criteria(new Column("AutoAppUpdatePackageConfig", "ALL_APPS"), (Object)Boolean.TRUE, 0);
        final Criteria includeAllAppsCri = new Criteria(new Column("AutoAppUpdatePackageConfig", "INCLUSION_FLAG"), (Object)Boolean.TRUE, 0);
        final Criteria includeResourcesCri = new Criteria(new Column("AutoAppUpdateResourceConfig", "INCLUSION_FLAG"), (Object)Boolean.TRUE, 0);
        final Criteria allResourcesCri = new Criteria(new Column("AutoAppUpdateResourceConfig", "ALL_RESOURCES"), (Object)Boolean.TRUE, 0);
        final Criteria customerCri = new Criteria(new Column("AutoAppUpdateConfigDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        allAppsSelectQuery.setCriteria(allAppsCri.and(includeAllAppsCri).and(includeResourcesCri).and(allResourcesCri).and(customerCri));
        allAppsSelectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject allAppsAllResources = MDMUtil.getPersistence().get(allAppsSelectQuery);
        final Set<Long> excludeResources = new HashSet<Long>();
        if (allAppsAllResources.isEmpty()) {
            return null;
        }
        final SelectQuery specificAppExcludeQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdatePackageList"));
        specificAppExcludeQuery.addJoin(new Join("AutoAppUpdatePackageList", "AutoAppUpdatePackageConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        specificAppExcludeQuery.addJoin(new Join("AutoAppUpdatePackageConfig", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        specificAppExcludeQuery.addJoin(new Join("AutoAppUpdateConfigDetails", "AutoAppUpdateResourceConfig", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
        final Criteria specificAppCri = new Criteria(new Column("AutoAppUpdatePackageList", "PACKAGE_ID"), (Object)packageId, 0);
        final Criteria excludeAppCri = new Criteria(new Column("AutoAppUpdatePackageConfig", "INCLUSION_FLAG"), (Object)Boolean.FALSE, 0);
        specificAppExcludeQuery.setCriteria(specificAppCri.and(excludeAppCri).and(includeResourcesCri).and(allResourcesCri).and(customerCri));
        specificAppExcludeQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject specificAppExcludeAllResources = MDMUtil.getPersistence().get(specificAppExcludeQuery);
        if (!specificAppExcludeAllResources.isEmpty()) {
            return null;
        }
        specificAppExcludeQuery.setCriteria(customerCri.and(specificAppCri).and(excludeAppCri).and(includeResourcesCri).and(allResourcesCri.negate()));
        final DataObject specificAppExcludeFewResources = MDMUtil.getPersistence().get(specificAppExcludeQuery);
        if (!specificAppExcludeFewResources.isEmpty()) {
            final Iterator excludeResourceList = specificAppExcludeFewResources.getRows("AutoAppUpdateResourceConfig");
            while (excludeResourceList.hasNext()) {
                final Row resourceConfRow = excludeResourceList.next();
                excludeResources.addAll(this.getResourcesConfiguredForAppUpdate((Long)resourceConfRow.get("APP_UPDATE_CONF_ID")));
            }
        }
        allAppsSelectQuery.setCriteria(customerCri.and(allAppsCri).and(includeAllAppsCri).and(includeResourcesCri.negate()).and(allResourcesCri.negate()));
        final DataObject allappsExcludeResource = MDMUtil.getPersistence().get(allAppsSelectQuery);
        if (!allappsExcludeResource.isEmpty()) {
            final Iterator excludeResourceList2 = allappsExcludeResource.getRows("AutoAppUpdateResourceConfig");
            while (excludeResourceList2.hasNext()) {
                final Row resourceConfRow2 = excludeResourceList2.next();
                excludeResources.addAll(this.getResourcesConfiguredForAppUpdate((Long)resourceConfRow2.get("APP_UPDATE_CONF_ID")));
            }
        }
        if (excludeResources != null && !excludeResources.isEmpty()) {
            responseJSON.put("excluded_resources", (Collection)JSONUtil.convertListToJSONArray((Long[])excludeResources.toArray(new Long[excludeResources.size()])));
        }
        responseJSON.put("deploy_for_all_resources", true);
        responseJSON.put("deployment_config", allAppsAllResources.getFirstValue("AutoAppUpdateDeploymentSettings", "DEPLOYMENT_CONFIG_ID"));
        return JSONUtil.toJSON("global_resources", responseJSON);
    }
    
    public void updateAutoAppUpdateConfig(final JSONObject messageBody) throws Exception {
        try {
            final JSONObject existingConfig = this.getAutoAppUpdateConfig(messageBody);
            final Long customerId = messageBody.getLong("customerId");
            final Long userId = messageBody.getLong("userId");
            final Long appUpdateConfigId = messageBody.getLong("appUpdateConfId");
            final Criteria confIdCriteria = new Criteria(new Column("AutoAppUpdateConfigDetails", "APP_UPDATE_CONF_ID"), (Object)appUpdateConfigId, 0);
            if (existingConfig.optBoolean("all_apps") && messageBody.has("app_ids")) {
                final UpdateQuery updateApps = (UpdateQuery)new UpdateQueryImpl("AutoAppUpdatePackageConfig");
                updateApps.addJoin(new Join("AutoAppUpdatePackageConfig", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
                updateApps.setCriteria(confIdCriteria);
                updateApps.setUpdateColumn("ALL_APPS", (Object)Boolean.FALSE);
                MDMUtil.getPersistence().update(updateApps);
                final DataObject dataObject = (DataObject)new WritableDataObject();
                final List<Long> packageList = new ArrayList<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(messageBody.getJSONArray("app_ids")));
                for (final Long packageId : packageList) {
                    final Row packageRowList = new Row("AutoAppUpdatePackageList");
                    packageRowList.set("APP_UPDATE_CONF_ID", (Object)appUpdateConfigId);
                    packageRowList.set("PACKAGE_ID", (Object)packageId);
                    dataObject.addRow(packageRowList);
                }
                MDMUtil.getPersistence().add(dataObject);
            }
            else if (!existingConfig.optBoolean("all_apps") && messageBody.has("all_apps")) {
                final UpdateQuery updateApps = (UpdateQuery)new UpdateQueryImpl("AutoAppUpdatePackageConfig");
                updateApps.addJoin(new Join("AutoAppUpdatePackageConfig", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
                updateApps.setCriteria(confIdCriteria);
                updateApps.setUpdateColumn("ALL_APPS", (Object)Boolean.TRUE);
                MDMUtil.getPersistence().update(updateApps);
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("AutoAppUpdatePackageList");
                deleteQuery.setCriteria(confIdCriteria);
                MDMUtil.getPersistence().delete(deleteQuery);
            }
            else if (!existingConfig.optBoolean("all_apps") && messageBody.has("app_ids")) {
                final DeleteQuery deleteQuery2 = (DeleteQuery)new DeleteQueryImpl("AutoAppUpdatePackageList");
                deleteQuery2.setCriteria(confIdCriteria);
                MDMUtil.getPersistence().delete(deleteQuery2);
                final DataObject dataObject = (DataObject)new WritableDataObject();
                final List<Long> packageList = new ArrayList<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(messageBody.getJSONArray("app_ids")));
                for (final Long packageId : packageList) {
                    final Row packageRowList = new Row("AutoAppUpdatePackageList");
                    packageRowList.set("APP_UPDATE_CONF_ID", (Object)appUpdateConfigId);
                    packageRowList.set("PACKAGE_ID", (Object)packageId);
                    dataObject.addRow(packageRowList);
                }
                MDMUtil.getPersistence().add(dataObject);
            }
            if (existingConfig.optBoolean("all_resources") && messageBody.has("resource_ids")) {
                final UpdateQuery updateRes = (UpdateQuery)new UpdateQueryImpl("AutoAppUpdatePackageConfig");
                updateRes.addJoin(new Join("AutoAppUpdateResourceConfig", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
                updateRes.setCriteria(confIdCriteria);
                updateRes.setUpdateColumn("ALL_RESOURCES", (Object)Boolean.FALSE);
                MDMUtil.getPersistence().update(updateRes);
                final DataObject dataObject = (DataObject)new WritableDataObject();
                final List<Long> resourceList = new ArrayList<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(messageBody.getJSONArray("resource_ids")));
                for (final Long resourceId : resourceList) {
                    final Row resourceRowList = new Row("AutoAppUpdateResourceList");
                    resourceRowList.set("APP_UPDATE_CONF_ID", (Object)appUpdateConfigId);
                    resourceRowList.set("RESOURCE_ID", (Object)resourceId);
                    dataObject.addRow(resourceRowList);
                }
                MDMUtil.getPersistence().add(dataObject);
            }
            else if (!existingConfig.optBoolean("all_resources") && messageBody.has("all_resources")) {
                final UpdateQuery updateResources = (UpdateQuery)new UpdateQueryImpl("AutoAppUpdateResourceConfig");
                updateResources.addJoin(new Join("AutoAppUpdateResourceConfig", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
                updateResources.setCriteria(confIdCriteria);
                updateResources.setUpdateColumn("ALL_RESOURCES", (Object)Boolean.TRUE);
                MDMUtil.getPersistence().update(updateResources);
                final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("AutoAppUpdateResourceList");
                deleteQuery.setCriteria(confIdCriteria);
                MDMUtil.getPersistence().delete(deleteQuery);
            }
            else if (!existingConfig.optBoolean("all_resources") && messageBody.has("resource_ids")) {
                final DeleteQuery deleteQuery2 = (DeleteQuery)new DeleteQueryImpl("AutoAppUpdateResourceList");
                deleteQuery2.setCriteria(confIdCriteria);
                MDMUtil.getPersistence().delete(deleteQuery2);
                final DataObject dataObject = (DataObject)new WritableDataObject();
                final List<Long> resourceList = new ArrayList<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(messageBody.getJSONArray("resource_ids")));
                for (final Long resId : resourceList) {
                    final Row resourceRowList = new Row("AutoAppUpdateResourceList");
                    resourceRowList.set("APP_UPDATE_CONF_ID", (Object)appUpdateConfigId);
                    resourceRowList.set("RESOURCE_ID", (Object)resId);
                    dataObject.addRow(resourceRowList);
                }
                MDMUtil.getPersistence().add(dataObject);
            }
            if (messageBody.has("exclude_resource")) {
                final UpdateQuery updateResourceFlag = (UpdateQuery)new UpdateQueryImpl("AutoAppUpdateResourceConfig");
                updateResourceFlag.addJoin(new Join("AutoAppUpdateResourceConfig", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
                updateResourceFlag.setCriteria(confIdCriteria);
                updateResourceFlag.setUpdateColumn("INCLUSION_FLAG", (Object)!messageBody.getBoolean("exclude_resource"));
                MDMUtil.getPersistence().update(updateResourceFlag);
            }
            if (messageBody.has("exclude_app")) {
                final UpdateQuery updatePackageFlag = (UpdateQuery)new UpdateQueryImpl("AutoAppUpdatePackageConfig");
                updatePackageFlag.addJoin(new Join("AutoAppUpdatePackageConfig", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
                updatePackageFlag.setCriteria(confIdCriteria);
                updatePackageFlag.setUpdateColumn("INCLUSION_FLAG", (Object)!messageBody.getBoolean("exclude_app"));
                MDMUtil.getPersistence().update(updatePackageFlag);
            }
            if (messageBody.optBoolean("exclude_app") || messageBody.optBoolean("exclude_resource")) {
                final DeleteQuery deleteQuery2 = (DeleteQuery)new DeleteQueryImpl("AutoAppUpdateDeploymentSettings");
                deleteQuery2.addJoin(new Join("AutoAppUpdateDeploymentSettings", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
                deleteQuery2.setCriteria(confIdCriteria);
                MDMUtil.getPersistence().delete(deleteQuery2);
            }
            else {
                boolean isAppInclude = Boolean.FALSE;
                boolean isResInclude = Boolean.FALSE;
                if (messageBody.has("exclude_app") && !messageBody.getBoolean("exclude_app")) {
                    isAppInclude = Boolean.TRUE;
                }
                else if (!messageBody.has("exclude_app") && !existingConfig.optBoolean("exclude_app")) {
                    isAppInclude = Boolean.TRUE;
                }
                if (messageBody.has("exclude_resource") && !messageBody.getBoolean("exclude_resource")) {
                    isResInclude = Boolean.TRUE;
                }
                else if (!messageBody.has("exclude_resource") && !existingConfig.optBoolean("exclude_resource")) {
                    isResInclude = Boolean.TRUE;
                }
                if (isAppInclude && isResInclude) {
                    final Long existingId = (Long)DBUtil.getValueFromDB("AutoAppUpdateDeploymentSettings", "APP_UPDATE_CONF_ID", (Object)appUpdateConfigId, "DEPLOYMENT_CONFIG_ID");
                    if (existingId == null) {
                        final Boolean silentInstall = messageBody.optBoolean("silent_install");
                        final Boolean notifyUser = messageBody.optBoolean("notify_user_via_email");
                        final JSONObject deploymentJSON = new JSONObject();
                        deploymentJSON.put("EMAIL_NOTIFY_END_USER", (Object)notifyUser);
                        deploymentJSON.put("FORCE_APP_INSTALL", (Object)silentInstall);
                        final Long deploymentConfigId = new AppDeploymentPolicyNonUiManager().getDeploymentConfigIdForAppDeployment(customerId, userId, deploymentJSON);
                        final DataObject dataObject2 = (DataObject)new WritableDataObject();
                        final Row deployConfRow = new Row("AutoAppUpdateDeploymentSettings");
                        deployConfRow.set("APP_UPDATE_CONF_ID", (Object)appUpdateConfigId);
                        deployConfRow.set("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
                        dataObject2.addRow(deployConfRow);
                        MDMUtil.getPersistence().add(dataObject2);
                    }
                    else {
                        final Boolean silentInstall = messageBody.has("silent_install") ? messageBody.getBoolean("silent_install") : existingConfig.getBoolean("silent_install");
                        final Boolean notifyUser = messageBody.has("notify_user_via_email") ? messageBody.getBoolean("notify_user_via_email") : existingConfig.getBoolean("notify_user_via_email");
                        final JSONObject deploymentJSON = new JSONObject();
                        deploymentJSON.put("EMAIL_NOTIFY_END_USER", (Object)notifyUser);
                        deploymentJSON.put("FORCE_APP_INSTALL", (Object)silentInstall);
                        final Long deploymentConfigId = new AppDeploymentPolicyNonUiManager().getDeploymentConfigIdForAppDeployment(customerId, userId, deploymentJSON);
                        final UpdateQuery depQuery = (UpdateQuery)new UpdateQueryImpl("AutoAppUpdateDeploymentSettings");
                        depQuery.addJoin(new Join("AutoAppUpdateDeploymentSettings", "AutoAppUpdateConfigDetails", new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 2));
                        depQuery.setCriteria(confIdCriteria);
                        depQuery.setUpdateColumn("DEPLOYMENT_CONFIG_ID", (Object)deploymentConfigId);
                        MDMUtil.getPersistence().update(depQuery);
                    }
                }
            }
            final UpdateQuery confQuery = (UpdateQuery)new UpdateQueryImpl("AutoAppUpdateConfigDetails");
            confQuery.setCriteria(confIdCriteria);
            confQuery.setUpdateColumn("LAST_MODIFIED_TIME", (Object)System.currentTimeMillis());
            confQuery.setUpdateColumn("LAST_MODIFIED_BY", (Object)userId);
            if (messageBody.has("description")) {
                confQuery.setUpdateColumn("DESCRIPTION", (Object)messageBody.getString("description"));
            }
            MDMUtil.getPersistence().update(confQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in modifying Auto app update Configuration", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public String getAppName(final Long packageId) {
        String appName = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("Profile", "PROFILE_NAME"));
        selectQuery.setCriteria(new Criteria(new Column("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageId, 0));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getRow("Profile");
                appName = (String)row.get("PROFILE_NAME");
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Couldnt fetch app name {0}", (Throwable)e);
        }
        return appName;
    }
    
    private List<Long> getResourcesWithProfileTobeRemoved(final Long profileId, final List<Long> resourceList, final boolean isGroup) throws DataAccessException {
        final List<Long> removableList = new ArrayList<Long>();
        if (isGroup) {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
            final Criteria profileCriteria = new Criteria(new Column("RecentProfileForGroup", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria groupList = new Criteria(new Column("RecentProfileForGroup", "GROUP_ID"), (Object)resourceList.toArray(), 8);
            final Criteria deleteCriteria = new Criteria(new Column("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)Boolean.TRUE, 0);
            selectQuery.setCriteria(profileCriteria.and(groupList.and(deleteCriteria)));
            selectQuery.addSelectColumn(new Column("RecentProfileForGroup", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("RecentProfileForGroup", "GROUP_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("RecentProfileForGroup");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    removableList.add((Long)row.get("GROUP_ID"));
                }
            }
        }
        else {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
            final Criteria profileCriteria = new Criteria(new Column("RecentProfileForResource", "PROFILE_ID"), (Object)profileId, 0);
            final Criteria groupList = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria deleteCriteria = new Criteria(new Column("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)Boolean.TRUE, 0);
            selectQuery.setCriteria(profileCriteria.and(groupList.and(deleteCriteria)));
            selectQuery.addSelectColumn(new Column("RecentProfileForResource", "PROFILE_ID"));
            selectQuery.addSelectColumn(new Column("RecentProfileForResource", "RESOURCE_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("RecentProfileForResource");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    removableList.add((Long)row.get("RESOURCE_ID"));
                }
            }
        }
        return removableList;
    }
    
    public void performAutoAppUpdate(final List<Long> appGroupList, final Long customerID) throws Exception {
        final Long startTime = System.currentTimeMillis();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdateConfigDetails"));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (dataObject.isEmpty()) {
            return;
        }
        final Long releaseLabelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(customerID);
        final HashMap<Long, HashMap<Long, Long>> appGroupProfileCollectionMap = this.getLatestProfileCollectionMapForAppGroup(appGroupList, releaseLabelId);
        final Long addedByUser = (Long)dataObject.getFirstValue("AutoAppUpdateConfigDetails", "LAST_MODIFIED_BY");
        final String sEventLogRemarks = "dc.mdm.actionlog.appmgmt.autoupdate_started";
        final int eventLogConstant = 2031;
        final HashMap<Long, Long> appGroupToPackageMap = AppsUtil.getInstance().getAppGroupToPackageMap(appGroupList);
        final List<String> appNameList = this.getAppName(appGroupList);
        for (final String appName : appNameList) {
            MDMEventLogHandler.getInstance().MDMEventLogEntry(eventLogConstant, null, null, sEventLogRemarks, appName, customerID);
        }
        this.autoUpdateGlobalConfigPush(appGroupList, customerID, addedByUser, appGroupProfileCollectionMap, appGroupToPackageMap);
        this.autoUpdateSpecificConfigPush(appGroupList, customerID, addedByUser, appGroupProfileCollectionMap, appGroupToPackageMap);
        this.logger.log(Level.INFO, "Total completion of performAutoAppUpdate method: {0}", System.currentTimeMillis() - startTime);
    }
    
    private void autoUpdateGlobalConfigPush(final List<Long> appGroupList, final Long customerID, final Long addedByUser, final HashMap<Long, HashMap<Long, Long>> appGroupProfileCollectionMap, final HashMap<Long, Long> appGroupToPackageMap) throws Exception {
        final Map<Long, Properties> appToDeployment = new HashMap<Long, Properties>();
        final Long startTime = System.currentTimeMillis();
        for (final Long appGroupID : appGroupList) {
            final Long packageId = appGroupToPackageMap.get(appGroupID);
            final JSONObject packageObj = this.getGlobalAutoUpdateAppObject(packageId, customerID);
            if (packageObj != null) {
                if (packageObj.length() <= 0) {
                    continue;
                }
                final Properties appObject = new Properties();
                final Long releaseLabelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(customerID);
                final HashMap resourceMap = this.getYetToUpdateResource(appGroupID, packageObj.optString("include_resources"), packageObj.optString("excluded_resources"), releaseLabelId);
                final ArrayList<Long> groupList = new ArrayList<Long>(resourceMap.get("groupList"));
                final ArrayList<Long> resList = new ArrayList<Long>(resourceMap.get("resourceList"));
                final ArrayList<Long> excludeList = new ArrayList<Long>(resourceMap.get("excludeList"));
                final ArrayList<Long> totalResourceList = new ArrayList<Long>(resourceMap.get("totalResourceList"));
                final HashMap<Long, Long> profileCollectionMap = appGroupProfileCollectionMap.get(appGroupID);
                final Long profileId = new ArrayList<Long>(profileCollectionMap.keySet()).get(0);
                final Long collectionId = profileCollectionMap.get(profileId);
                final List<Long> removableGroupList = this.getResourcesWithProfileTobeRemoved(profileId, groupList, true);
                final List<Long> removableResourceList = this.getResourcesWithProfileTobeRemoved(profileId, resList, false);
                groupList.removeAll(removableGroupList);
                resList.removeAll(removableResourceList);
                totalResourceList.removeAll(removableGroupList);
                totalResourceList.removeAll(removableResourceList);
                ((Hashtable<String, ArrayList<Long>>)appObject).put("groupList", groupList);
                ((Hashtable<String, ArrayList<Long>>)appObject).put("excludeList", excludeList);
                ((Hashtable<String, JSONObject>)appObject).put("packageObj", packageObj);
                ((Hashtable<String, ArrayList<Long>>)appObject).put("resList", resList);
                ((Hashtable<String, Long>)appObject).put("profileId", profileId);
                ((Hashtable<String, Long>)appObject).put("collectionId", collectionId);
                ((Hashtable<String, HashMap<Long, Long>>)appObject).put("profileCollectionMap", profileCollectionMap);
                appToDeployment.put(appGroupID, appObject);
            }
        }
        this.autoPushToAppCatalog(customerID, addedByUser, appToDeployment);
        this.autoAppSilentInstall(customerID, addedByUser, appToDeployment);
        this.logger.log(Level.INFO, "Total completion of autoUpdateGlobalConfigPush method : {0}", System.currentTimeMillis() - startTime);
    }
    
    private void autoUpdateSpecificConfigPush(final List<Long> appGroupList, final Long customerID, final Long addedByUser, final HashMap<Long, HashMap<Long, Long>> appGroupProfileCollectionMap, final HashMap<Long, Long> appGroupToPackageMap) throws Exception {
        final List<Properties> specificObjList = new ArrayList<Properties>();
        final Long startTime = System.currentTimeMillis();
        for (final Long appGroupID : appGroupList) {
            final Long packageId = appGroupToPackageMap.get(appGroupID);
            final JSONArray packageArray = this.getSpecificResourceAutoAppUpdateObject(packageId, customerID);
            if (packageArray != null) {
                if (packageArray.length() <= 0) {
                    continue;
                }
                for (final JSONObject jsonObject : packageArray) {
                    final Properties properties = new Properties();
                    ((Hashtable<String, Long>)properties).put("packageId", packageId);
                    ((Hashtable<String, Long>)properties).put("appGroupId", appGroupID);
                    ((Hashtable<String, Long>)properties).put("customerId", customerID);
                    final Long releaseLabelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(customerID);
                    final HashMap resourceMap = this.getYetToUpdateResource(appGroupID, jsonObject.optString("include_resources"), jsonObject.optString("excluded_resources"), releaseLabelId);
                    final ArrayList<Long> groupList = new ArrayList<Long>(resourceMap.get("groupList"));
                    final ArrayList<Long> resList = new ArrayList<Long>(resourceMap.get("resourceList"));
                    final ArrayList<Long> excludeList = new ArrayList<Long>(resourceMap.get("excludeList"));
                    final ArrayList<Long> totalResourceList = new ArrayList<Long>(resourceMap.get("totalResourceList"));
                    final HashMap<Long, Long> profileCollectionMap = appGroupProfileCollectionMap.get(appGroupID);
                    final Long profileId = new ArrayList<Long>(profileCollectionMap.keySet()).get(0);
                    final Long collectionId = profileCollectionMap.get(profileId);
                    final List<Long> removableGroupList = this.getResourcesWithProfileTobeRemoved(profileId, groupList, true);
                    final List<Long> removableResourceList = this.getResourcesWithProfileTobeRemoved(profileId, resList, false);
                    groupList.removeAll(removableGroupList);
                    resList.removeAll(removableResourceList);
                    totalResourceList.removeAll(removableGroupList);
                    totalResourceList.removeAll(removableResourceList);
                    ((Hashtable<String, ArrayList<Long>>)properties).put("groupList", groupList);
                    ((Hashtable<String, ArrayList<Long>>)properties).put("excludeList", excludeList);
                    final JSONObject packageObj = new JSONObject().put("silent_install", jsonObject.getBoolean("silent_install")).put("notify_user_via_email", jsonObject.getBoolean("notify_user_via_email"));
                    ((Hashtable<String, JSONObject>)properties).put("packageObj", packageObj);
                    ((Hashtable<String, ArrayList<Long>>)properties).put("resList", resList);
                    ((Hashtable<String, Long>)properties).put("profileId", profileId);
                    ((Hashtable<String, Long>)properties).put("collectionId", collectionId);
                    ((Hashtable<String, HashMap<Long, Long>>)properties).put("profileCollectionMap", profileCollectionMap);
                    specificObjList.add(properties);
                }
                this.autoPushToAppCatalog(customerID, addedByUser, specificObjList);
                this.autoAppSilentInstall(customerID, addedByUser, specificObjList);
            }
        }
        this.logger.log(Level.INFO, "Total completion of autoUpdateSpecificConfigPush method : {0}", System.currentTimeMillis() - startTime);
    }
    
    private HashMap<Long, HashMap<Long, Long>> getLatestProfileCollectionMapForAppGroup(final List<Long> appGroupList, final Long releaseLabelId) throws DataAccessException {
        final HashMap<Long, HashMap<Long, Long>> appGroupProfileCollectionMap = new HashMap<Long, HashMap<Long, Long>>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppGroupToCollection"));
        selectQuery.addJoin(new Join("AppGroupToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("ProfileToCollection", "*"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "*"));
        final Criteria appGroupCri = new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupList.toArray(), 8);
        final Criteria appReleaseLabelCri = new Criteria(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)releaseLabelId, 0);
        selectQuery.setCriteria(appGroupCri.and(appReleaseLabelCri));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> rows = dataObject.getRows("ProfileToCollection");
            while (rows.hasNext()) {
                final Row row = rows.next();
                final HashMap<Long, Long> profileCollectionMap = new HashMap<Long, Long>();
                final Long profileID = (Long)row.get("PROFILE_ID");
                final Long collectionID = (Long)row.get("COLLECTION_ID");
                final Row appGroupRow = dataObject.getRow("AppGroupToCollection", new Criteria(new Column("AppGroupToCollection", "COLLECTION_ID"), (Object)collectionID, 0));
                final Long appGroupId = (Long)appGroupRow.get("APP_GROUP_ID");
                profileCollectionMap.put(profileID, collectionID);
                appGroupProfileCollectionMap.put(appGroupId, profileCollectionMap);
            }
        }
        return appGroupProfileCollectionMap;
    }
    
    JSONObject getGlobalAutoUpdateAppObject(final Long packageId, final long customerId) {
        JSONObject jsonObject = null;
        try {
            final JSONObject appUpdateJSON = getInstance().getUpdateConfigurationForApp(packageId, customerId);
            if (appUpdateJSON != null && appUpdateJSON.length() > 0) {
                this.logger.log(Level.INFO, "Entering auto update handling for app {0}", packageId);
                final JSONObject appUpdateConfig = appUpdateJSON.getJSONObject("app_update_configurations");
                final JSONObject globalConfig = appUpdateConfig.optJSONObject("global_resources");
                Set<Long> excludeList = null;
                if (appUpdateConfig.has("never_update_resources")) {
                    excludeList = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(new JSONArray(String.valueOf(appUpdateConfig.get("never_update_resources")))));
                }
                if (globalConfig != null && globalConfig.length() > 0 && globalConfig.optBoolean("deploy_for_all_resources") && globalConfig.optBoolean("deploy_for_all_resources")) {
                    final Long deploymentConfId = globalConfig.getLong("deployment_config");
                    jsonObject = new AppDeploymentPolicyImpl().getAppDeploymentPolicy(deploymentConfId);
                    if (excludeList == null) {
                        excludeList = new HashSet<Long>();
                    }
                    if (globalConfig.has("excluded_resources")) {
                        excludeList.addAll(JSONUtil.getInstance().convertLongJSONArrayTOList(globalConfig.getJSONArray("excluded_resources")));
                    }
                    jsonObject.put("excluded_resources", (Collection)JSONUtil.convertListToJSONArray((Long[])excludeList.toArray(new Long[excludeList.size()])));
                    jsonObject.put("deploy_for_all_resources", globalConfig.optBoolean("deploy_for_all_resources"));
                    jsonObject.put("package_id", (Object)packageId);
                    jsonObject.put("customer_id", customerId);
                }
            }
            return jsonObject;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occurred in getGlobalAutoUpdateAppObject : ", e);
            return null;
        }
    }
    
    JSONArray getSpecificResourceAutoAppUpdateObject(final Long packageId, final Long customerId) {
        final JSONArray jsonArray = new JSONArray();
        try {
            final JSONObject appUpdateJSON = getInstance().getUpdateConfigurationForApp(packageId, customerId);
            if (appUpdateJSON != null && appUpdateJSON.length() > 0) {
                this.logger.log(Level.INFO, "Entering auto update handling for app {0}", packageId);
                final JSONObject appUpdateConfig = appUpdateJSON.getJSONObject("app_update_configurations");
                final JSONArray specificConfig = appUpdateConfig.optJSONArray("specific_included_resources");
                Set<Long> excludeList = null;
                if (appUpdateConfig.has("never_update_resources")) {
                    excludeList = new HashSet<Long>(JSONUtil.getInstance().convertLongJSONArrayTOList(new JSONArray(String.valueOf(appUpdateConfig.get("never_update_resources")))));
                }
                if (specificConfig != null && specificConfig.length() > 0) {
                    for (int i = 0; i < specificConfig.length(); ++i) {
                        final JSONObject confObject = specificConfig.getJSONObject(i);
                        final Long deploymentConfId = confObject.getLong("deployment_config");
                        final JSONObject jsonObject = new AppDeploymentPolicyImpl().getAppDeploymentPolicy(deploymentConfId);
                        final Set<Long> includeList = new HashSet<Long>();
                        if (confObject.has("include_resources")) {
                            includeList.addAll(JSONUtil.getInstance().convertLongJSONArrayTOList(new JSONArray(String.valueOf(confObject.get("include_resources")))));
                            jsonObject.put("include_resources", (Collection)JSONUtil.convertListToJSONArray((Long[])includeList.toArray(new Long[includeList.size()])));
                            jsonObject.put("package_id", (Object)packageId);
                            jsonObject.put("customer_id", (Object)customerId);
                            if (excludeList == null) {
                                excludeList = new HashSet<Long>();
                            }
                            jsonObject.put("excluded_resources", (Collection)JSONUtil.convertListToJSONArray((Long[])excludeList.toArray(new Long[excludeList.size()])));
                        }
                        jsonArray.put((Object)jsonObject);
                    }
                }
            }
            return jsonArray;
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occurred in getSpecificResourceAutoAppUpdateObject : ", e);
            return null;
        }
    }
    
    public List<String> getAppName(final List<Long> appGroupList) {
        final List<String> appName = new ArrayList<String>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addSelectColumn(new Column("Profile", "PROFILE_ID"));
        selectQuery.addSelectColumn(new Column("Profile", "PROFILE_NAME"));
        selectQuery.setCriteria(new Criteria(new Column("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupList.toArray(), 8));
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Iterator<Row> iterator = dataObject.getRows("Profile");
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    appName.add(row.get("PROFILE_NAME").toString());
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Couldnt fetch app name {0}", (Throwable)e);
        }
        return appName;
    }
    
    private void autoPushToAppCatalog(final Long customerID, final Long addedByUser, final List<Properties> appToDeploy) throws DataAccessException {
        final List<DeploymentBean> groupDeploymentBeanSet = new ArrayList<DeploymentBean>();
        final List<DeploymentBean> resourceDeploymentBeanSet = new ArrayList<DeploymentBean>();
        final Properties properties = new Properties();
        ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
        ((Hashtable<String, Long>)properties).put("customerId", customerID);
        ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
        ((Hashtable<String, Integer>)properties).put("toBeAssociatedAppSource", MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_AUTO_UPDATE);
        ((Hashtable<String, Long>)properties).put("loggedOnUser", addedByUser);
        final Long startTime = System.currentTimeMillis();
        for (final Properties appObject : appToDeploy) {
            final JSONObject packageObj = ((Hashtable<K, JSONObject>)appObject).get("packageObj");
            final Long appGroupID = ((Hashtable<K, Long>)appObject).get("appGroupId");
            if (packageObj.optBoolean("silent_install")) {
                continue;
            }
            final DeploymentBean groupdeploymentBean = new DeploymentBean();
            final DeploymentBean resourcedeploymentBean = new DeploymentBean();
            groupdeploymentBean.setCustomerId(customerID);
            groupdeploymentBean.setIncludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("groupList"));
            groupdeploymentBean.setExcludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("excludeList"));
            groupdeploymentBean.setSilentInstall(packageObj.optBoolean("silent_install"));
            groupdeploymentBean.setNotifyUserViaEmail(packageObj.optBoolean("notify_user_via_email"));
            resourcedeploymentBean.setIncludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("resList"));
            resourcedeploymentBean.setCustomerId(customerID);
            resourcedeploymentBean.setExcludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("excludeList"));
            resourcedeploymentBean.setSilentInstall(packageObj.optBoolean("silent_install"));
            resourcedeploymentBean.setNotifyUserViaEmail(packageObj.optBoolean("notify_user_via_email"));
            final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)appObject).get("profileCollectionMap");
            this.putDeploymentBean(groupDeploymentBeanSet, groupdeploymentBean, appGroupID, profileCollectionMap, appObject);
            this.putDeploymentBean(resourceDeploymentBeanSet, resourcedeploymentBean, appGroupID, profileCollectionMap, appObject);
        }
        this.associateAutoUpdateToGroup(groupDeploymentBeanSet, properties);
        this.associateAutoUpdateToDevice(resourceDeploymentBeanSet, properties);
        this.logger.log(Level.INFO, "Total completion of autoPushToAppCatalog method(1) : {0}", System.currentTimeMillis() - startTime);
    }
    
    private void autoPushToAppCatalog(final Long customerID, final Long addedByUser, final Map<Long, Properties> appToDeploy) throws DataAccessException {
        final List<DeploymentBean> groupDeploymentBeanSet = new ArrayList<DeploymentBean>();
        final List<DeploymentBean> resourceDeploymentBeanSet = new ArrayList<DeploymentBean>();
        final Properties properties = new Properties();
        ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
        ((Hashtable<String, Long>)properties).put("customerId", customerID);
        ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
        ((Hashtable<String, Integer>)properties).put("toBeAssociatedAppSource", MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_AUTO_UPDATE);
        ((Hashtable<String, Long>)properties).put("loggedOnUser", addedByUser);
        final Long startTime = System.currentTimeMillis();
        for (final Long appGroupID : appToDeploy.keySet()) {
            final Properties appObject = appToDeploy.get(appGroupID);
            final JSONObject packageObj = ((Hashtable<K, JSONObject>)appObject).get("packageObj");
            if (packageObj.optBoolean("silent_install")) {
                continue;
            }
            final DeploymentBean groupdeploymentBean = new DeploymentBean();
            final DeploymentBean resourcedeploymentBean = new DeploymentBean();
            groupdeploymentBean.setCustomerId(customerID);
            groupdeploymentBean.setIncludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("groupList"));
            groupdeploymentBean.setExcludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("excludeList"));
            groupdeploymentBean.setSilentInstall(packageObj.optBoolean("silent_install"));
            groupdeploymentBean.setNotifyUserViaEmail(packageObj.optBoolean("notify_user_via_email"));
            resourcedeploymentBean.setIncludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("resList"));
            resourcedeploymentBean.setCustomerId(customerID);
            resourcedeploymentBean.setExcludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("excludeList"));
            resourcedeploymentBean.setSilentInstall(packageObj.optBoolean("silent_install"));
            resourcedeploymentBean.setNotifyUserViaEmail(packageObj.optBoolean("notify_user_via_email"));
            final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)appObject).get("profileCollectionMap");
            this.putDeploymentBean(groupDeploymentBeanSet, groupdeploymentBean, appGroupID, profileCollectionMap, appObject);
            this.putDeploymentBean(resourceDeploymentBeanSet, resourcedeploymentBean, appGroupID, profileCollectionMap, appObject);
        }
        this.associateAutoUpdateToGroup(groupDeploymentBeanSet, properties);
        this.associateAutoUpdateToDevice(resourceDeploymentBeanSet, properties);
        this.logger.log(Level.INFO, "Total completion of autoPushToAppCatalog method(2) : {0}", System.currentTimeMillis() - startTime);
    }
    
    private void autoAppSilentInstall(final Long customerID, final Long addedByUser, final List<Properties> appToDeploy) throws Exception {
        final AppDeploymentPolicyImpl appDeploymentPolicy = new AppDeploymentPolicyImpl();
        final List<DeploymentBean> silentGroupDeploymentBeanSet = new ArrayList<DeploymentBean>();
        final List<DeploymentBean> silentResourceDeploymentBeanSet = new ArrayList<DeploymentBean>();
        final List<DeploymentBean> catalogGroupDeploymentBeanSet = new ArrayList<DeploymentBean>();
        final List<DeploymentBean> catalogResourceDeploymentBeanSet = new ArrayList<DeploymentBean>();
        final Properties properties = new Properties();
        ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
        ((Hashtable<String, Long>)properties).put("customerId", customerID);
        ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
        ((Hashtable<String, Integer>)properties).put("toBeAssociatedAppSource", MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_AUTO_UPDATE);
        ((Hashtable<String, Long>)properties).put("loggedOnUser", addedByUser);
        ((Hashtable<String, Boolean>)properties).put("isSchedule", Boolean.TRUE);
        final List<Properties> appDistributedDeploy = new ArrayList<Properties>();
        final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
        final Long startTime = System.currentTimeMillis();
        for (final Properties appObject : appToDeploy) {
            final Long appGroupID = ((Hashtable<K, Long>)appObject).get("appGroupId");
            final Long profileId = ((Hashtable<K, Long>)appObject).get("profileId");
            final ArrayList<Long> totalGroupList = ((Hashtable<K, ArrayList<Long>>)appObject).get("groupList");
            final ArrayList<Long> totalResourceList = ((Hashtable<K, ArrayList<Long>>)appObject).get("resList");
            final ArrayList<Long> silentGroupInstallList = appDeploymentPolicy.getSilentInstallDeployedResources(totalGroupList, profileId, null);
            final ArrayList<Long> silentResInstallList = appDeploymentPolicy.getSilentInstallDeployedResources(totalResourceList, profileId, null);
            final List<Long> alreadyInstalledDevices = handler.getAppInstalledDevices(appGroupID, totalResourceList);
            silentResInstallList.addAll(alreadyInstalledDevices);
            final ArrayList<Long> catalogGroupInstallList = new ArrayList<Long>(totalGroupList);
            catalogGroupInstallList.removeAll(silentGroupInstallList);
            final ArrayList<Long> catalogResInstallList = new ArrayList<Long>(totalResourceList);
            catalogResInstallList.removeAll(silentResInstallList);
            final Properties updateBean = (Properties)appObject.clone();
            ((Hashtable<String, ArrayList<Long>>)updateBean).put("silentGroupInstallList", silentGroupInstallList);
            ((Hashtable<String, ArrayList<Long>>)updateBean).put("silentResInstallList", silentResInstallList);
            ((Hashtable<String, ArrayList<Long>>)updateBean).put("catalogGroupInstallList", catalogGroupInstallList);
            ((Hashtable<String, ArrayList<Long>>)updateBean).put("catalogResInstallList", catalogResInstallList);
            appDistributedDeploy.add(updateBean);
        }
        for (final Properties appObject : appDistributedDeploy) {
            final JSONObject packageObj = ((Hashtable<K, JSONObject>)appObject).get("packageObj");
            final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)appObject).get("profileCollectionMap");
            final Long appGroupID2 = ((Hashtable<K, Long>)appObject).get("appGroupId");
            final DeploymentBean silentGroupdeploymentBean = new DeploymentBean();
            final DeploymentBean silentResourcedeploymentBean = new DeploymentBean();
            final DeploymentBean catalogGroupdeploymentBean = new DeploymentBean();
            final DeploymentBean catalogResourcedeploymentBean = new DeploymentBean();
            silentGroupdeploymentBean.setCustomerId(customerID);
            silentGroupdeploymentBean.setIncludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("silentGroupInstallList"));
            silentGroupdeploymentBean.setExcludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("excludeList"));
            silentGroupdeploymentBean.setNotifyUserViaEmail(packageObj.optBoolean("notify_user_via_email"));
            silentGroupdeploymentBean.setSilentInstall(true);
            silentResourcedeploymentBean.setIncludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("silentResInstallList"));
            silentResourcedeploymentBean.setCustomerId(customerID);
            silentResourcedeploymentBean.setExcludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("excludeList"));
            silentResourcedeploymentBean.setNotifyUserViaEmail(packageObj.optBoolean("notify_user_via_email"));
            silentResourcedeploymentBean.setSilentInstall(true);
            catalogGroupdeploymentBean.setCustomerId(customerID);
            catalogGroupdeploymentBean.setIncludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("catalogGroupInstallList"));
            catalogGroupdeploymentBean.setExcludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("excludeList"));
            catalogGroupdeploymentBean.setNotifyUserViaEmail(packageObj.optBoolean("silent_install"));
            catalogGroupdeploymentBean.setSilentInstall(false);
            catalogResourcedeploymentBean.setIncludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("catalogResInstallList"));
            catalogResourcedeploymentBean.setCustomerId(customerID);
            catalogResourcedeploymentBean.setExcludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("excludeList"));
            catalogResourcedeploymentBean.setSilentInstall(false);
            catalogResourcedeploymentBean.setNotifyUserViaEmail(packageObj.optBoolean("notify_user_via_email"));
            this.putDeploymentBean(silentGroupDeploymentBeanSet, silentGroupdeploymentBean, appGroupID2, profileCollectionMap, appObject);
            this.putDeploymentBean(silentResourceDeploymentBeanSet, silentResourcedeploymentBean, appGroupID2, profileCollectionMap, appObject);
            this.putDeploymentBean(catalogGroupDeploymentBeanSet, catalogGroupdeploymentBean, appGroupID2, profileCollectionMap, appObject);
            this.putDeploymentBean(catalogResourceDeploymentBeanSet, catalogResourcedeploymentBean, appGroupID2, profileCollectionMap, appObject);
        }
        this.associateAutoUpdateToGroup(silentGroupDeploymentBeanSet, properties);
        this.associateAutoUpdateToGroup(catalogGroupDeploymentBeanSet, properties);
        this.associateAutoUpdateToDevice(silentResourceDeploymentBeanSet, properties);
        this.associateAutoUpdateToDevice(catalogResourceDeploymentBeanSet, properties);
        this.logger.log(Level.INFO, "Total completion of autoAppSilentInstall method(1) : {0}", System.currentTimeMillis() - startTime);
    }
    
    private void autoAppSilentInstall(final Long customerID, final Long addedByUser, final Map<Long, Properties> appToDeploy) throws Exception {
        final AppDeploymentPolicyImpl appDeploymentPolicy = new AppDeploymentPolicyImpl();
        final List<DeploymentBean> silentGroupDeploymentBeanSet = new ArrayList<DeploymentBean>();
        final List<DeploymentBean> silentResourceDeploymentBeanSet = new ArrayList<DeploymentBean>();
        final List<DeploymentBean> catalogGroupDeploymentBeanSet = new ArrayList<DeploymentBean>();
        final List<DeploymentBean> catalogResourceDeploymentBeanSet = new ArrayList<DeploymentBean>();
        final Long startTime = System.currentTimeMillis();
        final Properties properties = new Properties();
        ((Hashtable<String, Boolean>)properties).put("isAppConfig", true);
        ((Hashtable<String, Long>)properties).put("customerId", customerID);
        ((Hashtable<String, Boolean>)properties).put("profileOrigin", true);
        ((Hashtable<String, Integer>)properties).put("toBeAssociatedAppSource", MDMCommonConstants.ASSOCIATED_APP_SOURCE_BY_AUTO_UPDATE);
        ((Hashtable<String, Long>)properties).put("loggedOnUser", addedByUser);
        final Map<Long, Properties> appDistributedDeploy = new HashMap<Long, Properties>();
        final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
        for (final Long appGroupID : appToDeploy.keySet()) {
            final Properties appObject = appToDeploy.get(appGroupID);
            final Long profileId = ((Hashtable<K, Long>)appObject).get("profileId");
            final ArrayList<Long> totalGroupList = ((Hashtable<K, ArrayList<Long>>)appObject).get("groupList");
            final ArrayList<Long> totalResourceList = ((Hashtable<K, ArrayList<Long>>)appObject).get("resList");
            final ArrayList<Long> silentGroupInstallList = appDeploymentPolicy.getSilentInstallDeployedResources(totalGroupList, profileId, null);
            final ArrayList<Long> silentResInstallList = appDeploymentPolicy.getSilentInstallDeployedResources(totalResourceList, profileId, null);
            final ArrayList<Long> catalogGroupInstallList = new ArrayList<Long>(totalGroupList);
            catalogGroupInstallList.removeAll(silentGroupInstallList);
            final List<Long> totalGroupMemberList = MDMGroupHandler.getMemberIdListForGroups(catalogGroupInstallList, 120);
            totalResourceList.addAll(totalGroupMemberList);
            final List<Long> alreadyInstalledDevices = handler.getAppInstalledDevices(appGroupID, totalResourceList);
            silentResInstallList.addAll(alreadyInstalledDevices);
            final ArrayList<Long> catalogResInstallList = new ArrayList<Long>(totalResourceList);
            catalogResInstallList.removeAll(silentResInstallList);
            final Properties updateBean = (Properties)appObject.clone();
            ((Hashtable<String, ArrayList<Long>>)updateBean).put("silentGroupInstallList", silentGroupInstallList);
            ((Hashtable<String, ArrayList<Long>>)updateBean).put("silentResInstallList", silentResInstallList);
            ((Hashtable<String, ArrayList<Long>>)updateBean).put("catalogGroupInstallList", catalogGroupInstallList);
            ((Hashtable<String, ArrayList<Long>>)updateBean).put("catalogResInstallList", catalogResInstallList);
            appDistributedDeploy.put(appGroupID, updateBean);
        }
        for (final Long appGroupID : appDistributedDeploy.keySet()) {
            final Properties appObject = appDistributedDeploy.get(appGroupID);
            final JSONObject packageObj = ((Hashtable<K, JSONObject>)appObject).get("packageObj");
            final HashMap profileCollectionMap = ((Hashtable<K, HashMap>)appObject).get("profileCollectionMap");
            final DeploymentBean silentGroupdeploymentBean = new DeploymentBean();
            final DeploymentBean silentResourcedeploymentBean = new DeploymentBean();
            final DeploymentBean catalogGroupdeploymentBean = new DeploymentBean();
            final DeploymentBean catalogResourcedeploymentBean = new DeploymentBean();
            silentGroupdeploymentBean.setCustomerId(customerID);
            silentGroupdeploymentBean.setIncludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("silentGroupInstallList"));
            silentGroupdeploymentBean.setExcludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("excludeList"));
            silentGroupdeploymentBean.setNotifyUserViaEmail(packageObj.optBoolean("notify_user_via_email"));
            silentGroupdeploymentBean.setSilentInstall(true);
            silentResourcedeploymentBean.setIncludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("silentResInstallList"));
            silentResourcedeploymentBean.setCustomerId(customerID);
            silentResourcedeploymentBean.setExcludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("excludeList"));
            silentResourcedeploymentBean.setNotifyUserViaEmail(packageObj.optBoolean("notify_user_via_email"));
            silentResourcedeploymentBean.setSilentInstall(true);
            catalogGroupdeploymentBean.setCustomerId(customerID);
            catalogGroupdeploymentBean.setIncludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("catalogGroupInstallList"));
            catalogGroupdeploymentBean.setExcludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("excludeList"));
            catalogGroupdeploymentBean.setNotifyUserViaEmail(packageObj.optBoolean("notify_user_via_email"));
            catalogGroupdeploymentBean.setSilentInstall(false);
            catalogResourcedeploymentBean.setIncludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("catalogResInstallList"));
            catalogResourcedeploymentBean.setCustomerId(customerID);
            catalogResourcedeploymentBean.setExcludedResourceList(((Hashtable<K, ArrayList<Long>>)appObject).get("excludeList"));
            catalogResourcedeploymentBean.setSilentInstall(false);
            catalogResourcedeploymentBean.setNotifyUserViaEmail(packageObj.optBoolean("notify_user_via_email"));
            this.putDeploymentBean(silentGroupDeploymentBeanSet, silentGroupdeploymentBean, appGroupID, profileCollectionMap, appObject);
            this.putDeploymentBean(silentResourceDeploymentBeanSet, silentResourcedeploymentBean, appGroupID, profileCollectionMap, appObject);
            this.putDeploymentBean(catalogGroupDeploymentBeanSet, catalogGroupdeploymentBean, appGroupID, profileCollectionMap, appObject);
            this.putDeploymentBean(catalogResourceDeploymentBeanSet, catalogResourcedeploymentBean, appGroupID, profileCollectionMap, appObject);
        }
        this.associateAutoUpdateToGroup(silentGroupDeploymentBeanSet, properties);
        this.associateAutoUpdateToGroup(catalogGroupDeploymentBeanSet, properties);
        this.associateAutoUpdateToDevice(silentResourceDeploymentBeanSet, properties);
        this.associateAutoUpdateToDevice(catalogResourceDeploymentBeanSet, properties);
        this.logger.log(Level.INFO, "Total completion of autoAppSilentInstall method(2) : {0}", System.currentTimeMillis() - startTime);
    }
    
    public void putDeploymentBean(final List<DeploymentBean> deploymentList, final DeploymentBean deploymentBean, final Long appGroupID, final HashMap profileCollectionMap, final Properties appObject) {
        if (deploymentList.contains(deploymentBean)) {
            final DeploymentBean existingdeploymentBean = deploymentList.get(deploymentList.indexOf(deploymentBean));
            existingdeploymentBean.getAppGroupList().add(appGroupID);
            existingdeploymentBean.getProfileCollectionMap().put(((Hashtable<K, Long>)appObject).get("profileId"), ((Hashtable<K, Long>)appObject).get("collectionId"));
        }
        else {
            final List<Long> resourceList = deploymentBean.getIncludedResourceList();
            if (resourceList != null && !resourceList.isEmpty()) {
                final ArrayList<Long> appGrouplist = new ArrayList<Long>();
                appGrouplist.add(appGroupID);
                deploymentBean.setAppGroupList(appGrouplist);
                final HashMap newProfCollMap = new HashMap(profileCollectionMap);
                deploymentBean.setProfileCollectionMap(newProfCollMap);
                deploymentList.add(deploymentBean);
            }
        }
    }
    
    private void associateAutoUpdateToGroup(final List<DeploymentBean> groupDeploymentBeanSet, final Properties properties) {
        for (final DeploymentBean deploymentBean : groupDeploymentBeanSet) {
            final Properties newProperties = (Properties)properties.clone();
            ((Hashtable<String, Boolean>)newProperties).put("isSilentInstall", deploymentBean.isSilentInstall());
            ((Hashtable<String, Boolean>)newProperties).put("isNotify", deploymentBean.isNotifyUserViaEmail());
            ((Hashtable<String, Boolean>)newProperties).put("isGroup", true);
            ((Hashtable<String, ArrayList<Long>>)newProperties).put("resourceList", deploymentBean.getIncludedResourceList());
            ((Hashtable<String, Integer>)newProperties).put("groupType", 6);
            ((Hashtable<String, ArrayList<Long>>)newProperties).put("excludeList", deploymentBean.getExcludedResourceList());
            ((Hashtable<String, HashMap<Long, Long>>)newProperties).put("profileCollectionMap", deploymentBean.getProfileCollectionMap());
            this.profileDistributionLog.log(Level.INFO, "Auto Update has been triggered update for app with properties (Group) {0}", newProperties);
            ((Hashtable<String, Boolean>)newProperties).put("isNewApp", true);
            ProfileAssociateHandler.getInstance().associateCollectionForGroup(newProperties);
        }
    }
    
    private void associateAutoUpdateToDevice(final List<DeploymentBean> resourceDeploymentBeanSet, final Properties properties) {
        for (final DeploymentBean deploymentBean : resourceDeploymentBeanSet) {
            final Properties newProperties = (Properties)properties.clone();
            ((Hashtable<String, Boolean>)newProperties).put("isSilentInstall", deploymentBean.isSilentInstall());
            ((Hashtable<String, Boolean>)newProperties).put("isNotify", deploymentBean.isNotifyUserViaEmail());
            ((Hashtable<String, Boolean>)newProperties).put("isGroup", false);
            ((Hashtable<String, ArrayList<Long>>)newProperties).put("resourceList", deploymentBean.getIncludedResourceList());
            ((Hashtable<String, ArrayList<Long>>)newProperties).put("excludeList", deploymentBean.getExcludedResourceList());
            ((Hashtable<String, Boolean>)newProperties).put("profileOrigin", false);
            ((Hashtable<String, HashMap<Long, Long>>)newProperties).put("profileCollectionMap", deploymentBean.getProfileCollectionMap());
            ((Hashtable<String, Boolean>)newProperties).put("isNewApp", true);
            ProfileAssociateHandler.getInstance().associateCollectionForResource(newProperties);
            this.profileDistributionLog.log(Level.INFO, "Auto Update has been triggered update for app with properties (Resource) {0}", newProperties);
        }
    }
    
    public JSONObject getPortalAndNonPortalAppsForCustomer(final Long customerId, final int platformType, final int portalOrNonPortal) throws Exception {
        final JSONObject portalAndNonPortalAppsForCustomer = new JSONObject();
        List portalApps = new ArrayList();
        List nonPortalApps = new ArrayList();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        sQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria customerIdCri = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria platformCri = new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
        sQuery.addSelectColumn(new Column("MdAppGroupDetails", "APP_GROUP_ID"));
        sQuery.addSelectColumn(new Column("MdAppGroupDetails", "PLATFORM_TYPE"));
        sQuery.addSelectColumn(new Column("MdAppGroupDetails", "CUSTOMER_ID"));
        sQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        sQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
        sQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
        Criteria criteria = customerIdCri.and(platformCri);
        if (portalOrNonPortal == 1) {
            final Criteria portalAppCriteria = new Criteria(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)Boolean.TRUE, 0);
            criteria = criteria.and(portalAppCriteria);
        }
        else if (portalOrNonPortal == 2) {
            final Criteria nonPortalAppCriteria = new Criteria(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)Boolean.FALSE, 0);
            criteria = criteria.and(nonPortalAppCriteria);
        }
        sQuery.setCriteria(criteria);
        final DataObject appsDo = MDMUtil.getPersistence().get(sQuery);
        if (appsDo != null && !appsDo.isEmpty()) {
            if (portalOrNonPortal == 1) {
                final Iterator<Row> portalAppsRows = appsDo.getRows("MdPackageToAppGroup");
                portalApps = DBUtil.getColumnValuesAsList((Iterator)portalAppsRows, "APP_GROUP_ID");
            }
            else if (portalOrNonPortal == 2) {
                final Iterator<Row> nonPortalAppsRows = appsDo.getRows("MdPackageToAppGroup");
                nonPortalApps = DBUtil.getColumnValuesAsList((Iterator)nonPortalAppsRows, "APP_GROUP_ID");
            }
            else if (portalOrNonPortal == 3) {
                final Criteria portalAppCriteria2 = new Criteria(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)Boolean.TRUE, 0);
                final Iterator<Row> portalAppsRows2 = appsDo.getRows("MdPackageToAppGroup", portalAppCriteria2);
                portalApps = DBUtil.getColumnValuesAsList((Iterator)portalAppsRows2, "APP_GROUP_ID");
                final Criteria nonPortalAppCriteria2 = new Criteria(new Column("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)Boolean.FALSE, 0);
                final Iterator<Row> nonPortalAppsRows2 = appsDo.getRows("MdPackageToAppGroup", nonPortalAppCriteria2);
                nonPortalApps = DBUtil.getColumnValuesAsList((Iterator)nonPortalAppsRows2, "APP_GROUP_ID");
            }
        }
        portalAndNonPortalAppsForCustomer.put("portalApps", (Collection)portalApps);
        portalAndNonPortalAppsForCustomer.put("nonPortalApps", (Collection)nonPortalApps);
        return portalAndNonPortalAppsForCustomer;
    }
    
    public List<String> getPortalApprovedAppsIdentifier(final Long customerId, final int platformType) throws Exception {
        List<String> portalApps = new ArrayList<String>();
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
        final Join appToGroupJoin = new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        final Criteria portalPurchasedCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)Boolean.TRUE, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platformType, 0);
        sQuery.addJoin(appToGroupJoin);
        sQuery.setCriteria(customerCriteria.and(portalPurchasedCriteria).and(platformCriteria));
        sQuery.addSelectColumn(new Column("MdAppGroupDetails", "APP_GROUP_ID"));
        sQuery.addSelectColumn(new Column("MdAppGroupDetails", "PLATFORM_TYPE"));
        sQuery.addSelectColumn(new Column("MdAppGroupDetails", "CUSTOMER_ID"));
        sQuery.addSelectColumn(new Column("MdAppGroupDetails", "IDENTIFIER"));
        sQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"));
        sQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"));
        sQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"));
        final DataObject appsDo = MDMUtil.getPersistence().get(sQuery);
        if (appsDo != null && !appsDo.isEmpty()) {
            final Iterator<Row> portalAppsRows = appsDo.getRows("MdAppGroupDetails");
            portalApps = DBUtil.getColumnValuesAsList((Iterator)portalAppsRows, "IDENTIFIER");
        }
        return portalApps;
    }
    
    public void bulkDistributeAppsToDevices(final List<DeploymentBean> resourceDeploymentBeanSet, final Properties properties, final int platformType) {
        for (final DeploymentBean deploymentBean : resourceDeploymentBeanSet) {
            final List deviceList = deploymentBean.getIncludedResourceList();
            final Set resSet = new HashSet();
            resSet.addAll(deviceList);
            final HashMap deviceMap = new HashMap();
            deviceMap.put(platformType, resSet);
            final HashMap<Long, Long> profileCollectionMap = deploymentBean.getProfileCollectionMap();
            final Set<Long> profileSet = profileCollectionMap.keySet();
            final List<Long> profileList = new ArrayList<Long>(profileSet);
            final Collection<Long> collectionIds = profileCollectionMap.values();
            final List<Long> collectionList = new ArrayList<Long>(collectionIds);
            final HashMap collectionToPlatformMap = new HashMap();
            collectionToPlatformMap.put(platformType, collectionList);
            final HashMap profileToPlatformMap = new HashMap();
            profileToPlatformMap.put(platformType, profileList);
            final Properties newProperties = (Properties)properties.clone();
            ((Hashtable<String, HashMap>)newProperties).put("deviceMap", deviceMap);
            ((Hashtable<String, HashMap>)newProperties).put("collectionToPlatformMap", collectionToPlatformMap);
            ((Hashtable<String, HashMap>)newProperties).put("profileToPlatformMap", profileToPlatformMap);
            ((Hashtable<String, Boolean>)newProperties).put("isSilentInstall", deploymentBean.isSilentInstall());
            ((Hashtable<String, Boolean>)newProperties).put("isNotify", deploymentBean.isNotifyUserViaEmail());
            ((Hashtable<String, Boolean>)newProperties).put("isGroup", false);
            ((Hashtable<String, Long>)newProperties).put("customerId", deploymentBean.getCustomerId());
            ((Hashtable<String, HashMap<Long, Long>>)newProperties).put("profileCollnMap", profileCollectionMap);
            this.logger.log(Level.SEVERE, "Initiating app distribution with props : {0}", newProperties);
            AssignCommandTaskProcessor.getTaskProcessor().assignDeviceCommandTask(newProperties);
        }
    }
    
    public void executeScheduledAppUpdate(final Long autoUpdatePolicyId, final Long autoUpdateCollectionId, final Long customerId) {
        final List<Properties> specificObjList = new ArrayList<Properties>();
        final List<Long> appGroupIdList = new ArrayList<Long>();
        final List<Long> totalGroupList = new ArrayList<Long>();
        final SelectQuery appScheduledUpdateQuery = AppUpdatePolicyDBHandler.getInstance().getPackageToAppUpdateConfigQuery(autoUpdatePolicyId);
        final SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForGroup"));
        final Criteria collnCriteria = new Criteria(new Column("RecentProfileForGroup", "COLLECTION_ID"), (Object)autoUpdateCollectionId, 0);
        final Criteria markedForDeleteCriteria = new Criteria(new Column("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)Boolean.FALSE, 0);
        groupQuery.setCriteria(collnCriteria.and(markedForDeleteCriteria));
        groupQuery.addSelectColumn(new Column("RecentProfileForGroup", "*"));
        final Map profileDetails = MDMUtil.getInstance().getProfileDetailsForCollectionId(autoUpdateCollectionId);
        final Long addedByUser = profileDetails.get("LAST_MODIFIED_BY");
        try {
            final DataObject groupQueryObject = MDMUtil.getPersistence().get(groupQuery);
            totalGroupList.addAll(MDMDBUtil.getColumnValuesAsSet(groupQueryObject.getRows("RecentProfileForGroup"), "GROUP_ID"));
            if (totalGroupList.isEmpty()) {
                this.mdmAppMgmtLogger.log(Level.INFO, "Policy not associated to any targets");
                return;
            }
            final DataObject appScheduledUpdateObject = MDMUtil.getPersistence().get(appScheduledUpdateQuery);
            final Row deploymentPolicyRow = appScheduledUpdateObject.getRow("AppUpdateDeploymentPolicy");
            boolean silentInstall = true;
            boolean notifyUser = false;
            if (deploymentPolicyRow != null) {
                silentInstall = (boolean)deploymentPolicyRow.get("IS_SILENT_INSTALL");
                notifyUser = (boolean)deploymentPolicyRow.get("IS_NOTIFY_USER");
            }
            final Row packageConfigDetails = appScheduledUpdateObject.getRow("AutoAppUpdatePackageConfig");
            if (packageConfigDetails.get("ALL_APPS")) {
                final DataObject appGroupList = this.getAppsAvailableForGroup(totalGroupList);
                appScheduledUpdateObject.merge(appGroupList);
                final Iterator<Row> iterator = appGroupList.getRows("MdAppCatalogToGroup");
                appGroupIdList.addAll(MDMDBUtil.getColumnValuesAsSet(iterator, "APP_GROUP_ID"));
            }
            else {
                final Iterator<Row> iterator2 = appScheduledUpdateObject.getRows("MdPackageToAppGroup");
                appGroupIdList.addAll(MDMDBUtil.getColumnValuesAsSet(iterator2, "APP_GROUP_ID"));
            }
            this.mdmAppMgmtLogger.log(Level.INFO, "Apps configured in the policy {0}, policy associated to the resource {1}", new Object[] { appGroupIdList, totalGroupList });
            for (final Long appGroupID : appGroupIdList) {
                final Row mdPackageRow = appScheduledUpdateObject.getRow("MdPackageToAppGroup", new Criteria(new Column("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupID, 0));
                final Long packageId = (Long)mdPackageRow.get("PACKAGE_ID");
                final Properties properties = new Properties();
                ((Hashtable<String, Long>)properties).put("packageId", packageId);
                ((Hashtable<String, Long>)properties).put("appGroupId", appGroupID);
                ((Hashtable<String, Long>)properties).put("customerId", customerId);
                final Long releaseLabelId = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(customerId);
                final HashMap resourceMap = this.getYetToUpdateResource(appGroupID, JSONUtil.getInstance().convertListToJSONArray(totalGroupList).toString(), null, releaseLabelId);
                this.mdmAppMgmtLogger.log(Level.INFO, "Yet to update resource {0} for app group {1}", new Object[] { resourceMap, appGroupID });
                final ArrayList<Long> groupList = new ArrayList<Long>(resourceMap.get("groupList"));
                final ArrayList<Long> excludeList = new ArrayList<Long>(resourceMap.get("excludeList"));
                final Map<Long, List<Long>> appCollnToGroups = this.getApprovedCollnForGroups(appGroupID, groupList);
                final Long profileId = AppsUtil.getInstance().getProfileIdForPackage(packageId, customerId);
                this.mdmAppMgmtLogger.log(Level.INFO, "Group to associate collection map {0} for app group {1} for profile {2}", new Object[] { appCollnToGroups, appGroupID, profileId });
                for (final Long collectionId : appCollnToGroups.keySet()) {
                    final Properties approvedAppToGroupProperties = (Properties)properties.clone();
                    ((Hashtable<String, List<Long>>)approvedAppToGroupProperties).put("groupList", appCollnToGroups.get(collectionId));
                    ((Hashtable<String, ArrayList<Long>>)approvedAppToGroupProperties).put("excludeList", excludeList);
                    final JSONObject packageObj = new JSONObject().put("silent_install", silentInstall).put("notify_user_via_email", notifyUser);
                    ((Hashtable<String, JSONObject>)approvedAppToGroupProperties).put("packageObj", packageObj);
                    ((Hashtable<String, ArrayList>)approvedAppToGroupProperties).put("resList", new ArrayList());
                    ((Hashtable<String, Long>)approvedAppToGroupProperties).put("profileId", profileId);
                    ((Hashtable<String, Long>)approvedAppToGroupProperties).put("collectionId", collectionId);
                    final HashMap profileToCollectionMap = new HashMap();
                    profileToCollectionMap.put(profileId, collectionId);
                    ((Hashtable<String, HashMap>)approvedAppToGroupProperties).put("profileCollectionMap", profileToCollectionMap);
                    specificObjList.add(approvedAppToGroupProperties);
                }
            }
            if (!specificObjList.isEmpty()) {
                if (silentInstall) {
                    this.autoAppSilentInstall(customerId, addedByUser, specificObjList);
                }
                else {
                    this.autoPushToAppCatalog(customerId, addedByUser, specificObjList);
                }
            }
            else {
                this.logger.log(Level.INFO, "No apps available for update now ");
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Cannot fetch scheduled app update object ", (Throwable)e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Cannot distribute scheduled app updates ", e2);
        }
    }
    
    private DataObject getAppsAvailableForGroup(final List<Long> totalGroupList) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppCatalogToGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppCatalogToGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria groupListCriteria = new Criteria(new Column("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)totalGroupList.toArray(), 8);
        final Criteria updateAvailable = new Criteria(new Column("MdAppCatalogToGroup", "IS_UPDATE_AVAILABLE"), (Object)true, 0);
        selectQuery.setCriteria(groupListCriteria.and(updateAvailable));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject appGroupList = MDMUtil.getPersistence().get(selectQuery);
        return appGroupList;
    }
    
    private Map<Long, List<Long>> getApprovedCollnForGroups(final Long appGroupId, final List<Long> groupList) throws Exception {
        final Map<Long, List<Long>> appCollnToGroups = new HashMap<Long, List<Long>>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToGroup"));
        selectQuery.addJoin(new Join("MdAppCatalogToGroup", "MdAppToCollection", new String[] { "APPROVED_APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria appGroupCriteria = new Criteria(new Column("MdAppCatalogToGroup", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria groupListCriteria = new Criteria(new Column("MdAppCatalogToGroup", "RESOURCE_ID"), (Object)groupList.toArray(), 8);
        selectQuery.setCriteria(appGroupCriteria.and(groupListCriteria));
        selectQuery.addSelectColumn(new Column("MdAppCatalogToGroup", "*"));
        selectQuery.addSelectColumn(new Column("ProfileToCollection", "*"));
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (dmDataSetWrapper.next()) {
            final Long collectionId = (Long)dmDataSetWrapper.getValue("COLLECTION_ID");
            final Long groupId = (Long)dmDataSetWrapper.getValue("RESOURCE_ID");
            if (!appCollnToGroups.containsKey(collectionId)) {
                appCollnToGroups.put(collectionId, new ArrayList<Long>());
            }
            appCollnToGroups.get(collectionId).add(groupId);
        }
        return appCollnToGroups;
    }
    
    public void updateAppRepoRemarksFromCollection(final Collection resourceList, final Collection collectionList) throws DataAccessException {
        if (resourceList != null && !resourceList.isEmpty()) {
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MdAppCatalogToResource");
            updateQuery.addJoin(new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "PUBLISHED_APP_ID" }, new String[] { "APP_ID" }, 2));
            final Criteria resCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)new Column("CollnToResources", "RESOURCE_ID"), 0);
            final Criteria collnCriteria = new Criteria(new Column("MdAppToCollection", "COLLECTION_ID"), (Object)new Column("CollnToResources", "COLLECTION_ID"), 0);
            updateQuery.addJoin(new Join("MdAppCatalogToResource", "CollnToResources", resCriteria.and(collnCriteria), 2));
            final Criteria failedResCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            final Criteria collnListCriteria = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
            updateQuery.setUpdateColumn("REMARKS", (Object)Column.getColumn("CollnToResources", "REMARKS"));
            updateQuery.setCriteria(failedResCriteria.and(collnListCriteria));
            MDMUtil.getPersistence().update(updateQuery);
        }
    }
    
    public void deleteAppTrackDetails(final Long[] appGroupIds) {
        try {
            this.logger.log(Level.INFO, "Going to delete app tracks for {0}", Arrays.toString(appGroupIds));
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("ReleaseLabelToAppTrack");
            deleteQuery.setCriteria(new Criteria(new Column("ReleaseLabelToAppTrack", "APP_GROUP_ID"), (Object)appGroupIds, 8));
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "Cannot delete app track details {0}", (Throwable)e);
        }
    }
    
    static {
        MDMAppMgmtHandler.mdmHandler = null;
    }
}
