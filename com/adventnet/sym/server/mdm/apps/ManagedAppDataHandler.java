package com.adventnet.sym.server.mdm.apps;

import java.util.Hashtable;
import java.util.Set;
import com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue.AssociationQueueHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.HashSet;
import java.util.Properties;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.dd.plist.NSObject;
import com.adventnet.sym.server.mdm.apps.ios.AppleAppLicenseMgmtHandler;
import com.dd.plist.NSDictionary;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Iterator;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.mdm.server.apps.IOSAppVersionChecker;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import java.util.Collection;
import com.adventnet.sym.server.mdm.apps.ios.IOSModifiedEnterpriseAppsUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.List;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;
import org.json.JSONArray;
import com.adventnet.persistence.DataObject;

public class ManagedAppDataHandler
{
    public static String managedAppList;
    public static String resourceId;
    public static String customerId;
    public static String needToSync;
    private DataObject installedManagedAppsDO;
    private DataObject appCatalogDO;
    private DataObject finalAppListDO;
    JSONArray appDetailsArrayList;
    int appCollectionStatus;
    String appCollectionRemarks;
    Long appCollectionErrCode;
    Logger logger;
    
    public ManagedAppDataHandler() {
        this.installedManagedAppsDO = null;
        this.appCatalogDO = null;
        this.finalAppListDO = null;
        this.appDetailsArrayList = null;
        this.appCollectionRemarks = null;
        this.appCollectionErrCode = null;
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    private void getInstalledManagedAppsDO(final Long resourceID) {
        try {
            SelectQuery appQuery = null;
            appQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdInstalledAppResourceRel"));
            final Join appJoin = new Join("MdInstalledAppResourceRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join appGroupJoin = new Join("MdAppDetails", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join appPackageJoin = new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join appCatalogJoin = new Join("MdAppToGroupRel", "MdAppCatalogToResource", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join mdPublishedAppToCollectionJoin = new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "PUBLISHED_APP_ID" }, new String[] { "APP_ID" }, 1);
            final Join appGroupToCollection = new Join("MdAppToGroupRel", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1);
            appQuery.addJoin(appJoin);
            appQuery.addJoin(appGroupJoin);
            appQuery.addJoin(appPackageJoin);
            appQuery.addJoin(appCatalogJoin);
            appQuery.addJoin(mdPublishedAppToCollectionJoin);
            appQuery.addJoin(appGroupToCollection);
            appQuery.addSelectColumn(Column.getColumn("MdInstalledAppResourceRel", "*"));
            appQuery.addSelectColumn(Column.getColumn("MdAppDetails", "*"));
            appQuery.addSelectColumn(Column.getColumn("MdAppToGroupRel", "*"));
            appQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
            appQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "*"));
            appQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "COLLECTION_ID"));
            appQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "APP_ID"));
            appQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"));
            appQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID"));
            final Criteria appCatalogresCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceID, 0);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceID, 0);
            appQuery.setCriteria(resourceCriteria.and(appCatalogresCriteria));
            this.installedManagedAppsDO = MDMUtil.getPersistence().get(appQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception in installedManagedAppsDO{0}", ex);
        }
    }
    
    private void getAppCatalogDO(final Long resourceID) {
        try {
            SelectQuery appQuery = null;
            appQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToResource"));
            final Join appJoin = new Join("MdAppCatalogToResource", "MdAppDetails", new String[] { "PUBLISHED_APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join appPackageJoin = new Join("MdAppCatalogToResource", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join appCatalogToResScopeJoin = new Join("MdAppCatalogToResource", "MdAppCatalogToResourceScope", new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, new String[] { "APP_GROUP_ID", "RESOURCE_ID" }, 2);
            appQuery.addJoin(appJoin);
            appQuery.addJoin(appPackageJoin);
            appQuery.addJoin(appCatalogToResScopeJoin);
            appQuery.addJoin(new Join("MdAppDetails", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            appQuery.addJoin(new Join("MdPackageToAppGroup", "AppGroupToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            appQuery.addJoin(new Join("MdAppDetails", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            appQuery.addJoin(new Join("MdAppCatalogToResource", "MdInstalledAppResourceRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            appQuery.addJoin(new Join("MdAppCatalogToResource", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            appQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 1));
            appQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "*"));
            appQuery.addSelectColumn(Column.getColumn("MdAppDetails", "*"));
            appQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
            appQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResourceScope", "*"));
            appQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "*"));
            appQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "*"));
            appQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "*"));
            appQuery.addSelectColumn(Column.getColumn("MdInstalledAppResourceRel", "*"));
            appQuery.addSelectColumn(Column.getColumn("MdDeviceInfo", "*"));
            appQuery.addSelectColumn(Column.getColumn("MdModelInfo", "*"));
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceID, 0);
            appQuery.setCriteria(resourceCriteria);
            this.appCatalogDO = MDMUtil.getPersistence().get(appQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception in appCatalogDO {0}", ex);
        }
    }
    
    private JSONArray processManagedAppsList(final JSONObject managedAppListDetails) throws DataAccessException, JSONException, SyMException, Exception {
        this.finalAppListDO = SyMUtil.getPersistence().constructDataObject();
        this.appDetailsArrayList = new JSONArray();
        final Long resourceID = managedAppListDetails.optLong(ManagedAppDataHandler.resourceId);
        final Long customerID = managedAppListDetails.optLong(ManagedAppDataHandler.customerId);
        final ArrayList managedAppsList = (ArrayList)managedAppListDetails.opt(ManagedAppDataHandler.managedAppList);
        final boolean isSyncNeeded = managedAppListDetails.optBoolean(ManagedAppDataHandler.needToSync);
        ArrayList appGroupIdsList = null;
        this.getInstalledManagedAppsDO(resourceID);
        this.getAppCatalogDO(resourceID);
        appGroupIdsList = this.updateAppCatalog(managedAppsList, resourceID);
        if (isSyncNeeded) {
            this.syncManagedAppWithAppCatalog(appGroupIdsList);
        }
        final List resIdList = new ArrayList();
        resIdList.add(resourceID);
        AppsUtil.getInstance().addOrUpdateAppCatalogSync(resIdList);
        MDMUtil.getPersistence().update(this.finalAppListDO);
        return this.appDetailsArrayList;
    }
    
    private void setAppDetailsJosnToUpdateColln(final Long appIdToChangeCollnStatus) throws JSONException {
        final JSONObject appDetails = new JSONObject();
        appDetails.put("APP_ID", (Object)appIdToChangeCollnStatus);
        appDetails.put("STATUS", this.appCollectionStatus);
        appDetails.put("REMARKS", (Object)this.appCollectionRemarks);
        appDetails.put("ERROR_CODE", (Object)this.appCollectionErrCode);
        this.logger.log(Level.INFO, "Setting appDetails to change collnStatus. appId = {0}, appCollectionStatus ={1}, appCollectionRemarks = {2}, collErrorCode = {3}", new Object[] { appIdToChangeCollnStatus, this.appCollectionStatus, this.appCollectionRemarks, this.appCollectionErrCode });
        this.appDetailsArrayList.put((Object)appDetails);
    }
    
    private ArrayList updateAppCatalog(ArrayList managedAppsList, final Long resourceId) throws DataAccessException, JSONException, SyMException, Exception {
        String bundleIdentifier = null;
        Long appIdToChangeCollnStatus = null;
        final ArrayList appGroupIdsList = new ArrayList();
        managedAppsList = (ArrayList)this.modifyAppsJSONObjectList(managedAppsList);
        for (int i = 0; i < managedAppsList.size(); ++i) {
            final JSONObject managedAppJson = (JSONObject)managedAppsList.get(i);
            bundleIdentifier = managedAppJson.get("IDENTIFIER").toString();
            final String deviceStatus = managedAppJson.get("DeviceStatus").toString();
            final int appStatus = Integer.parseInt(managedAppJson.get("AppStatus").toString());
            this.appCollectionStatus = Integer.parseInt(managedAppJson.get("CollectionStatus").toString());
            this.appCollectionRemarks = managedAppJson.get("Remarks").toString();
            this.appCollectionErrCode = (Long)managedAppJson.opt("ErrorCode");
            this.logger.log(Level.INFO, "AppCollectionDetails for resource: {0} after processing MAL response: appIdentifier ={1} appCollnStatus = {2}, appCollRemarks: {3}, appErrorCode: {4}", new Object[] { resourceId, bundleIdentifier, this.appCollectionStatus, this.appCollectionRemarks, this.appCollectionErrCode });
            if (bundleIdentifier.equalsIgnoreCase("com.manageengine.mdm.iosagent") && appStatus == 2 && MDMiOSEntrollmentUtil.getInstance().isIOSWebClipAppCatalogInstalled(resourceId) && !MDMUtil.getInstance().isMacDevice(resourceId)) {
                DeviceCommandRepository.getInstance().addDefaultAppCatalogCommand(resourceId, "DefaultRemoveAppCatalogWebClips");
            }
            final HashMap appMap = this.getManagedAppDetailsFromIdentifier(bundleIdentifier, appStatus);
            if (!appMap.isEmpty()) {
                final Long appGroupId = appMap.get("APP_GROUP_ID");
                final Long appId = appMap.get("APP_ID");
                final Integer packageType = appMap.get("PACKAGE_TYPE");
                appGroupIdsList.add(appGroupId);
                appIdToChangeCollnStatus = this.updateAppStatusForManagedApp(appMap, appStatus, deviceStatus, this.appCollectionRemarks);
                if (appIdToChangeCollnStatus != null) {
                    this.setAppDetailsJosnToUpdateColln(appIdToChangeCollnStatus);
                }
            }
            else {
                this.logger.log(Level.WARNING, "ManagedApp details is empty , so not updating ! - Identifier{0}", bundleIdentifier);
            }
        }
        return appGroupIdsList;
    }
    
    private List modifyAppsJSONObjectList(final List managedAppList) {
        try {
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS") && managedAppList != null && !managedAppList.isEmpty()) {
                final List modifiedEnterpriseAppManagedList = new ArrayList();
                for (int i = 0; i < managedAppList.size(); ++i) {
                    final JSONObject managedAppJson = managedAppList.get(i);
                    String tempBundleIdentifier = managedAppJson.get("IDENTIFIER").toString();
                    final Criteria bundleIdentifierCriteria = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)IOSModifiedEnterpriseAppsUtil.getCustomBundleIDForEnterpriseApp(tempBundleIdentifier), 0);
                    final Row appRow = this.appCatalogDO.getRow("MdAppDetails", bundleIdentifierCriteria);
                    if (appRow != null) {
                        tempBundleIdentifier = (String)appRow.get("IDENTIFIER");
                        final JSONObject tempManagedJSON = new JSONObject();
                        tempManagedJSON.put("IDENTIFIER", (Object)tempBundleIdentifier);
                        tempManagedJSON.put("DeviceStatus", managedAppJson.get("DeviceStatus"));
                        tempManagedJSON.put("AppStatus", managedAppJson.get("AppStatus"));
                        tempManagedJSON.put("CollectionStatus", managedAppJson.get("CollectionStatus"));
                        tempManagedJSON.put("Remarks", managedAppJson.get("Remarks"));
                        tempManagedJSON.put("ErrorCode", managedAppJson.opt("ErrorCode"));
                        this.logger.log(Level.SEVERE, "App: {0} available in App catalog of device: {1}. Hence adding in ManagedAppList", new Object[] { tempBundleIdentifier, ManagedAppDataHandler.resourceId });
                        modifiedEnterpriseAppManagedList.add(tempManagedJSON);
                    }
                }
                managedAppList.addAll(modifiedEnterpriseAppManagedList);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in modifyAppsJSONObjectList", e);
        }
        return managedAppList;
    }
    
    private Long updateAppStatusForManagedApp(final HashMap appDetailsMap, final int installationStatus, final String deviceStatus, final String remarks) throws DataAccessException {
        return this.updateAppStatusForManagedApp(appDetailsMap, installationStatus, deviceStatus, remarks, 0);
    }
    
    private Long updateAppStatusForManagedApp(final HashMap appDetailsMap, final int installationStatus, final String deviceStatus, final String remarks, final int scope) throws DataAccessException {
        Long appIdToChangeCollnStatus = null;
        try {
            this.logger.log(Level.INFO, "Going to Update appCatalog detals for app with identifier {0}", new Object[] { appDetailsMap.get("IDENTIFIER") });
            final Long appGroupId = appDetailsMap.get("APP_GROUP_ID");
            final Long appId = appDetailsMap.get("APP_ID");
            final Integer packageType = appDetailsMap.get("PACKAGE_TYPE");
            final String appVersion = appDetailsMap.get("APP_VERSION");
            final String appShortVersion = appDetailsMap.getOrDefault("APP_NAME_SHORT_VERSION", "");
            final Long deviceInstalledExternalId = appDetailsMap.get("EXTERNAL_APP_VERSION_ID");
            DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
            final ArrayList<String> tableNames = new ArrayList<String>();
            tableNames.add("MdAppDetails");
            tableNames.add("MdAppCatalogToResource");
            tableNames.add("MdAppCatalogToResourceScope");
            tableNames.add("MdPackageToAppGroup");
            tableNames.add("MdPackageToAppData");
            tableNames.add("MdInstalledAppResourceRel");
            tableNames.add("MdDeviceInfo");
            tableNames.add("MdModelInfo");
            Row appRelRow = null;
            Row appRelScopeRow = null;
            final Criteria appGroupCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
            if (!this.appCatalogDO.isEmpty()) {
                final Row appDetailsRow = this.appCatalogDO.getRow("MdAppCatalogToResource", appGroupCri);
                if (appDetailsRow != null) {
                    dataObject = this.appCatalogDO.getDataObject((List)tableNames, appDetailsRow);
                }
            }
            if (!dataObject.isEmpty()) {
                appRelRow = dataObject.getFirstRow("MdAppCatalogToResource");
                final Long resourceId = (Long)appRelRow.get("RESOURCE_ID");
                final Long prevUpdatedTimeInMilliSec = (Long)appRelRow.get("UPDATED_AT");
                final Long publishedAppId = (Long)appRelRow.get("PUBLISHED_APP_ID");
                final Long installedAppId = (Long)appRelRow.get("INSTALLED_APP_ID");
                final String publishedAppVersion = (String)dataObject.getRow("MdAppDetails", new Criteria(new Column("MdAppDetails", "APP_ID"), (Object)publishedAppId, 0)).get("APP_VERSION");
                final Long publishedExternalID = (Long)dataObject.getRow("MdAppDetails", new Criteria(new Column("MdAppDetails", "APP_ID"), (Object)publishedAppId, 0)).get("EXTERNAL_APP_VERSION_ID");
                final String appStoreURL = (String)dataObject.getRow("MdPackageToAppData", new Criteria(new Column("MdPackageToAppData", "APP_ID"), (Object)publishedAppId, 0)).get("STORE_URL");
                final String minimumOS = (String)dataObject.getRow("MdPackageToAppData", new Criteria(new Column("MdPackageToAppData", "APP_ID"), (Object)publishedAppId, 0)).get("MIN_OS");
                final Integer supportedDevice = (Integer)dataObject.getRow("MdPackageToAppData", new Criteria(new Column("MdPackageToAppData", "APP_ID"), (Object)publishedAppId, 0)).get("SUPPORTED_DEVICES");
                final Row mdInstalledAppResourceRelRow = dataObject.getRow("MdInstalledAppResourceRel", new Criteria(new Column("MdInstalledAppResourceRel", "APP_ID"), (Object)appId, 0));
                Object hasUpdateObject = null;
                if (mdInstalledAppResourceRelRow != null) {
                    hasUpdateObject = mdInstalledAppResourceRelRow.get("HAS_UPDATE_AVAILABLE");
                }
                final Long modelID = (Long)dataObject.getRow("MdDeviceInfo", new Criteria(new Column("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0)).get("MODEL_ID");
                final Integer modelType = (Integer)dataObject.getRow("MdModelInfo", new Criteria(new Column("MdModelInfo", "MODEL_ID"), (Object)modelID, 0)).get("MODEL_TYPE");
                final boolean hasUpdateAvailable = hasUpdateObject != null && (boolean)hasUpdateObject;
                appRelScopeRow = dataObject.getFirstRow("MdAppCatalogToResourceScope");
                appRelScopeRow.set("SCOPE", (Object)scope);
                dataObject.updateRow(appRelScopeRow);
                final int statusInDb = (int)appRelRow.get("STATUS");
                final boolean ifStatusChanged = (installationStatus != statusInDb && (statusInDb != 0 || installedAppId == null || installedAppId != (long)appId || publishedAppId.equals(installedAppId))) || (deviceStatus.equalsIgnoreCase("ManagedButUninstalled") && installedAppId != null);
                boolean isForceUpdateStatus = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ForceSyncIOSManagedAppStatus");
                if (installationStatus == 2 && deviceInstalledExternalId != null && publishedExternalID != null && !deviceInstalledExternalId.equals(publishedExternalID) && !hasUpdateAvailable) {
                    isForceUpdateStatus = false;
                }
                if (ifStatusChanged || !isForceUpdateStatus) {
                    appIdToChangeCollnStatus = publishedAppId;
                    final Long newUpdatedTimeInMilliSec = System.currentTimeMillis();
                    if (ifStatusChanged) {
                        this.logger.log(Level.INFO, "Status changed for app {0}", new Object[] { appDetailsMap.get("IDENTIFIER") });
                        appRelRow.set("UPDATED_AT", (Object)System.currentTimeMillis());
                    }
                    if (isForceUpdateStatus) {
                        this.logger.log(Level.INFO, "Force sync enabled for app {0}", new Object[] { appDetailsMap.get("IDENTIFIER") });
                    }
                    appRelRow.set("REMARKS", (Object)remarks);
                    if (packageType == 2 || packageType == 0 || packageType == 1) {
                        final boolean isVersionKeyCompareEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowVersionKeyCompareForIOSApps");
                        if (installationStatus == 2 && deviceInstalledExternalId != null && publishedExternalID != null) {
                            if (deviceInstalledExternalId.equals(publishedExternalID)) {
                                appRelRow.set("INSTALLED_APP_ID", (Object)appId);
                                appRelRow.set("STATUS", (Object)installationStatus);
                                this.logger.log(Level.INFO, "App Installed : Updating the app catalog with installedAppId = {0}, installationStatus = {1}", new Object[] { appId, installationStatus });
                            }
                            else if (!deviceInstalledExternalId.equals(publishedAppId)) {
                                appRelRow.set("INSTALLED_APP_ID", (Object)appId);
                                if (hasUpdateAvailable) {
                                    appRelRow.set("STATUS", (Object)0);
                                    appRelRow.set("REMARKS", (Object)"dc.db.mdm.apps.status.UpgradeApp");
                                    this.appCollectionRemarks = I18N.getMsg("dc.db.mdm.apps.status.UpgradeApp", new Object[0]);
                                    this.appCollectionStatus = 12;
                                    if (statusInDb == 1) {
                                        this.appCollectionRemarks = I18N.getMsg("dc.db.mdm.apps.status.Failed", new Object[0]);
                                        appRelRow.set("REMARKS", (Object)"dc.db.mdm.apps.status.Failed");
                                    }
                                }
                                else if (deviceInstalledExternalId < publishedExternalID && !hasUpdateAvailable) {
                                    appRelRow.set("STATUS", (Object)2);
                                    final String remark = MDMUtil.replaceProductUrlLoaderValuesinText(this.getMinimumOSRemarks(supportedDevice, minimumOS, modelType), appStoreURL);
                                    appRelRow.set("REMARKS", (Object)remark);
                                    this.appCollectionRemarks = remark;
                                    this.appCollectionStatus = 6;
                                }
                                else {
                                    appRelRow.set("STATUS", (Object)installationStatus);
                                    this.logger.log(Level.INFO, "App Installed : Updating the app catalog with installedAppId = {0}, installationStatus = {1}", new Object[] { appId, installationStatus });
                                }
                            }
                        }
                        else if (installationStatus == 2 && (publishedAppId == (long)appId || new IOSAppVersionChecker().isVersionNumberGreater(appVersion, publishedAppVersion) || (isVersionKeyCompareEnabled && new VersionChecker().isEqual(appShortVersion, publishedAppVersion)))) {
                            appRelRow.set("INSTALLED_APP_ID", (Object)appId);
                            appRelRow.set("STATUS", (Object)installationStatus);
                            this.logger.log(Level.INFO, "App Installed : Updating the app catalog with installedAppId = {0}, installationStatus = {1}", new Object[] { appId, installationStatus });
                        }
                        else if (installationStatus == 2 && new IOSAppVersionChecker().isVersionNumberGreater(publishedAppVersion, appVersion)) {
                            appRelRow.set("INSTALLED_APP_ID", (Object)appId);
                            if (hasUpdateObject != null && hasUpdateAvailable) {
                                appRelRow.set("STATUS", (Object)0);
                                appRelRow.set("REMARKS", (Object)"dc.db.mdm.apps.status.UpgradeApp");
                                this.appCollectionStatus = 12;
                                this.appCollectionRemarks = I18N.getMsg("dc.db.mdm.apps.status.UpgradeApp", new Object[0]);
                            }
                            else if (hasUpdateObject != null && !hasUpdateAvailable) {
                                appRelRow.set("STATUS", (Object)2);
                                final String remark = MDMUtil.replaceProductUrlLoaderValuesinText(this.getMinimumOSRemarks(supportedDevice, minimumOS, modelType), appStoreURL);
                                appRelRow.set("REMARKS", (Object)remark);
                                this.appCollectionRemarks = remark;
                                this.appCollectionStatus = 6;
                            }
                            else {
                                appRelRow.set("STATUS", (Object)0);
                                appRelRow.set("REMARKS", (Object)"dc.db.mdm.apps.status.UpgradeApp");
                                this.appCollectionStatus = 12;
                                this.appCollectionRemarks = I18N.getMsg("dc.db.mdm.apps.status.UpgradeApp", new Object[0]);
                            }
                            if (statusInDb == 1) {
                                appRelRow.set("INSTALLED_APP_ID", (Object)appId);
                                appRelRow.set("STATUS", (Object)0);
                                this.appCollectionStatus = 12;
                                this.appCollectionRemarks = I18N.getMsg("dc.db.mdm.apps.status.Failed", new Object[0]);
                                appRelRow.set("REMARKS", (Object)"dc.db.mdm.apps.status.Failed");
                            }
                            this.logger.log(Level.INFO, "App need to be upgraded/ Failed to install: Updating the app catalog with installedAppId = {0}, installationStatus = {1}", new Object[] { appId, 0 });
                        }
                        else if (deviceStatus.equalsIgnoreCase("ManagedButUninstalled")) {
                            if (newUpdatedTimeInMilliSec - prevUpdatedTimeInMilliSec < 120000L && statusInDb == 1) {
                                appRelRow.set("UPDATED_AT", (Object)prevUpdatedTimeInMilliSec);
                                appRelRow.set("REMARKS", (Object)"dc.db.mdm.apps.status.Installing");
                                appRelRow.set("STATUS", (Object)1);
                                this.appCollectionStatus = 3;
                                this.appCollectionRemarks = I18N.getMsg("dc.db.mdm.apps.status.Installing", new Object[0]);
                                this.logger.log(Level.INFO, "App - ManagedButUninstalled(Installing) Updating the app catalog with installationStatus = {0}", new Object[] { appId, 2 });
                            }
                            else {
                                appRelRow.set("INSTALLED_APP_ID", (Object)null);
                                appRelRow.set("STATUS", (Object)installationStatus);
                                this.logger.log(Level.INFO, "Updating the app catalog with installedAppId = {0}, installationStatus = {1}", new Object[] { null, installationStatus });
                            }
                        }
                        else {
                            this.logger.log(Level.INFO, "Updating app catalog with new status: {0}", new Object[] { installationStatus });
                            appRelRow.set("STATUS", (Object)installationStatus);
                        }
                    }
                    appRelRow.set("APP_GROUP_ID", (Object)appGroupId);
                    dataObject.updateRow(appRelRow);
                }
                this.finalAppListDO.merge(dataObject);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in updateAppStatusForManagedApp...", ex);
        }
        return appIdToChangeCollnStatus;
    }
    
    private String getMinimumOSRemarks(final Integer supportedDevice, final String minimumOS, final Integer modelType) throws Exception {
        String remarks = I18N.getMsg("dc.db.mdm.apps.status.NotUpgradable_Generic", new Object[0]);
        Integer device = null;
        final List<Integer> primarilyIOS = Arrays.asList(2, 4, 6, 7, 15, 31);
        final List<Integer> primarilyIpadOS = Arrays.asList(9, 17, 13, 29, 25);
        if (modelType != null) {
            if (modelType.equals(1)) {
                device = 2;
            }
            else if (modelType.equals(2)) {
                device = 1;
            }
            else if (modelType.equals(5)) {
                device = 8;
            }
            else if (modelType.equals(4) || modelType.equals(4)) {
                device = 16;
            }
            if ((primarilyIOS.contains(supportedDevice) && device.equals(2)) || (primarilyIpadOS.contains(supportedDevice) && device.equals(1)) || (supportedDevice.equals(8) && device.equals(8)) || (supportedDevice.equals(16) && device.equals(16))) {
                remarks = I18N.getMsg("dc.db.mdm.apps.status.NotUpgradable", new Object[0]);
            }
        }
        return remarks;
    }
    
    private HashMap getManagedAppDetailsFromIdentifier(final String appIdentifier, final int appStatus) {
        this.logger.log(Level.INFO, "getManagedAppDetailsFromIdentifier : appIdentifier: {0} ", appIdentifier);
        final HashMap appMap = new HashMap();
        try {
            DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
            final Criteria criteria = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)appIdentifier, 0);
            final ArrayList<String> tableNames = new ArrayList<String>();
            tableNames.add("MdAppDetails");
            tableNames.add("MdAppToGroupRel");
            tableNames.add("MdAppCatalogToResource");
            tableNames.add("MdPackageToAppGroup");
            if (appStatus == 2) {
                this.logger.log(Level.INFO, "AppStatus for appIdentifier {0} in Installed. Hence getting installedResRel", new Object[] { appIdentifier });
                if (!this.installedManagedAppsDO.isEmpty()) {
                    final Row appDetailsRow = this.installedManagedAppsDO.getRow("MdAppDetails", criteria);
                    if (appDetailsRow != null) {
                        dataObject = this.installedManagedAppsDO.getDataObject((List)tableNames, appDetailsRow);
                    }
                    else {
                        this.logger.log(Level.INFO, "App[{0}] is not managed already for  this device", appIdentifier);
                    }
                }
            }
            else if (!this.appCatalogDO.isEmpty()) {
                final Row appDetailsRow = this.appCatalogDO.getRow("MdAppDetails", criteria);
                if (appDetailsRow != null) {
                    dataObject = this.appCatalogDO.getDataObject((List)tableNames, appDetailsRow);
                }
            }
            if (!dataObject.isEmpty()) {
                final Row appDetailsRow = dataObject.getRow("MdAppDetails");
                final Row mdPackageToAppGroupRow = dataObject.getRow("MdPackageToAppGroup");
                appMap.put("APP_ID", appDetailsRow.get("APP_ID"));
                appMap.put("PLATFORM_TYPE", appDetailsRow.get("PLATFORM_TYPE"));
                appMap.put("PACKAGE_TYPE", mdPackageToAppGroupRow.get("PACKAGE_TYPE"));
                appMap.put("APP_GROUP_ID", mdPackageToAppGroupRow.get("APP_GROUP_ID"));
                appMap.put("APP_VERSION", appDetailsRow.get("APP_VERSION"));
                appMap.put("APP_NAME_SHORT_VERSION", appDetailsRow.get("APP_NAME_SHORT_VERSION"));
                appMap.put("IDENTIFIER", appIdentifier);
                appMap.put("EXTERNAL_APP_VERSION_ID", appDetailsRow.get("EXTERNAL_APP_VERSION_ID"));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception in getManagedAppDetailsFromIdentifier ", ex);
        }
        this.logger.log(Level.INFO, "Managed App details: {0}", new Object[] { appMap });
        return appMap;
    }
    
    private void syncNotInstalledApps(final ArrayList appGroupIdsList) {
        try {
            this.logger.log(Level.INFO, "Going to sync not installed apps");
            final DataObject cloneAppCatalogDO = (DataObject)this.appCatalogDO.clone();
            final DataObject diffDo = this.installedManagedAppsDO.diff(cloneAppCatalogDO);
            final Criteria appCatalogInstalledStatus = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)2, 0);
            final Criteria appCatalogInstallingStatus = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)1, 0);
            final Criteria appCatalogUpdateStatus = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)0, 0);
            final Criteria installedAppIdNotNull = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)null, 1);
            final Criteria installedAppIdNotAsPublishedAppId = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "PUBLISHED_APP_ID"), 1);
            final Criteria updateAppStatus = appCatalogUpdateStatus.and(installedAppIdNotNull).and(installedAppIdNotAsPublishedAppId);
            final Criteria notInManagedAppListCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupIdsList.toArray(), 9);
            final Criteria InstalledStatusCriteria = notInManagedAppListCriteria.and(appCatalogInstalledStatus.or(appCatalogInstallingStatus).or(updateAppStatus));
            if (!diffDo.isEmpty()) {
                this.appCollectionStatus = 12;
                this.appCollectionRemarks = I18N.getMsg("dc.mdm.android.app_installation_from_appcatalog", new Object[0]);
                final Iterator it2 = diffDo.getRows("MdAppCatalogToResource", InstalledStatusCriteria);
                while (it2.hasNext()) {
                    final Row appCatalogRow = it2.next();
                    final Long resourceId = (Long)appCatalogRow.get("RESOURCE_ID");
                    final Long publishedAppid = (Long)appCatalogRow.get("PUBLISHED_APP_ID");
                    final Long appGroupId = (Long)appCatalogRow.get("APP_GROUP_ID");
                    final Row mdAppToCollnRow = this.appCatalogDO.getRow("MdAppToCollection", new Criteria(Column.getColumn("MdAppToCollection", "APP_ID"), (Object)publishedAppid, 0));
                    Long collectionId = null;
                    if (mdAppToCollnRow != null) {
                        collectionId = (Long)mdAppToCollnRow.get("COLLECTION_ID");
                    }
                    else {
                        final Row appGroupToCollnRow = this.appCatalogDO.getRow("AppGroupToCollection", new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupId, 0));
                        if (appGroupToCollnRow != null) {
                            collectionId = (Long)appGroupToCollnRow.get("COLLECTION_ID");
                        }
                    }
                    if (collectionId != null) {
                        final String commandUUID = "InstallApplication;Collection=" + collectionId;
                        final DeviceCommandRepository deviceCommandRepository = DeviceCommandRepository.getInstance();
                        final int status = (int)appCatalogRow.get("STATUS");
                        if ((status != 1 || deviceCommandRepository.checkCommandAvailableForDevice(commandUUID, resourceId)) && status != 2) {
                            continue;
                        }
                        appCatalogRow.set("STATUS", (Object)0);
                        appCatalogRow.set("REMARKS", (Object)I18N.getMsg("dc.mdm.android.app_installation_from_appcatalog", new Object[0]));
                        appCatalogRow.set("UPDATED_AT", (Object)System.currentTimeMillis());
                        appCatalogRow.set("INSTALLED_APP_ID", (Object)null);
                        this.appCatalogDO.updateRow(appCatalogRow);
                        this.logger.log(Level.INFO, "Updating AppCatalog for appGroupID {0} with status = 0, instaledAppID = null", new Object[] { appGroupId });
                        this.setAppDetailsJosnToUpdateColln(publishedAppid);
                    }
                    else {
                        this.logger.log(Level.SEVERE, "installedManagedAppsDO does not have a row in MdAppToCollection for published_app_id {0}", new Object[] { publishedAppid });
                    }
                }
                this.finalAppListDO.merge(this.appCatalogDO);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "exception {0}", ex);
        }
    }
    
    private void syncInstalledApps(final ArrayList appGroupIdsList) throws DataAccessException {
        try {
            this.logger.log(Level.INFO, "Syncing Installed apps");
            final Criteria notInManagedAppListCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupIdsList.toArray(), 9);
            final Criteria appCatalogInstallStatus = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)0, 0);
            final Criteria installedAppIDNull = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)null, 0);
            final Criteria installAppCriteria = appCatalogInstallStatus.and(installedAppIDNull);
            final Criteria appCatalogInstallingStatus = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)1, 0);
            final Criteria remarksToBeChangedForInstalledApp;
            final Criteria appCatalogInstalledStatus = remarksToBeChangedForInstalledApp = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)2, 0);
            final Criteria appCatalogStatusChange = notInManagedAppListCriteria.and(installAppCriteria.or(appCatalogInstallingStatus).or(remarksToBeChangedForInstalledApp));
            if (!this.installedManagedAppsDO.isEmpty()) {
                final Iterator it = this.installedManagedAppsDO.getRows("MdAppCatalogToResource", appCatalogStatusChange);
                while (it.hasNext()) {
                    final Row appCatalogRow = it.next();
                    final Long publishedAppid = (Long)appCatalogRow.get("PUBLISHED_APP_ID");
                    final Long resId = (Long)appCatalogRow.get("RESOURCE_ID");
                    final Long installedAppidInAppCatalogRow = (Long)appCatalogRow.get("INSTALLED_APP_ID");
                    final Long appGroupId = (Long)appCatalogRow.get("APP_GROUP_ID");
                    if (installedAppidInAppCatalogRow == null) {
                        final ArrayList tblList = new ArrayList();
                        tblList.add("MdInstalledAppResourceRel");
                        tblList.add("MdAppDetails");
                        tblList.add("MdAppToGroupRel");
                        tblList.add("MdAppCatalogToResource");
                        final DataObject dataObject = this.installedManagedAppsDO.getDataObject((List)tblList, appCatalogRow);
                        final Row r = dataObject.getRow("MdInstalledAppResourceRel");
                        final Long installedAppId = (Long)r.get("APP_ID");
                        appCatalogRow.set("INSTALLED_APP_ID", (Object)installedAppId);
                    }
                    appCatalogRow.set("STATUS", (Object)5);
                    appCatalogRow.set("UPDATED_AT", (Object)System.currentTimeMillis());
                    final Row packageRow = this.appCatalogDO.getRow("MdPackageToAppGroup", new Criteria(new Column("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)appGroupId, 0));
                    Integer packageType = null;
                    if (packageRow != null) {
                        packageType = (Integer)packageRow.get("PACKAGE_TYPE");
                    }
                    final String iOSVersion = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resId, "OS_VERSION");
                    final String helpUrl = AppsUtil.getInstance().getSilentInstallAppHelpUrl(packageType, 1);
                    String remarks = "";
                    if (!new VersionChecker().isGreaterOrEqual(iOSVersion, "9") || iOSVersion.startsWith("9.3")) {
                        remarks = "dc.db.mdm.collection.App_already_installed_ios";
                    }
                    else {
                        remarks = "mdm.app.appAlreadyInstalled@@@<a target='blank' href=\"" + helpUrl + "\">@@@</a>";
                    }
                    appCatalogRow.set("REMARKS", (Object)remarks);
                    this.installedManagedAppsDO.updateRow(appCatalogRow);
                    final Row collectionRow = this.installedManagedAppsDO.getRow("MdAppToCollection", new Criteria(new Column("MdAppToCollection", "APP_ID"), (Object)publishedAppid, 0));
                    this.appCollectionStatus = 12;
                    this.appCollectionRemarks = remarks;
                    this.setAppDetailsJosnToUpdateColln(publishedAppid);
                }
                this.finalAppListDO.merge(this.installedManagedAppsDO);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "exception {0}", ex);
        }
    }
    
    private void syncManagedAppWithAppCatalog(final ArrayList appGroupIdsList) throws DataAccessException, Exception {
        this.syncNotInstalledApps(appGroupIdsList);
        this.syncInstalledApps(appGroupIdsList);
    }
    
    public JSONArray processIOSManagedAppsList(final JSONObject managedApps) throws Exception {
        final NSDictionary managedAppListDict = (NSDictionary)managedApps.opt(ManagedAppDataHandler.managedAppList);
        this.logger.log(Level.FINE, "processManagedAppsList()");
        if (managedAppListDict == null) {
            return new JSONArray();
        }
        final ArrayList appGroupList = this.processIOSManagedAppDict(managedAppListDict);
        final JSONObject managedAppListDetails = new JSONObject();
        managedAppListDetails.put(ManagedAppDataHandler.resourceId, managedApps.optLong(ManagedAppDataHandler.resourceId));
        managedAppListDetails.put(ManagedAppDataHandler.customerId, managedApps.optLong(ManagedAppDataHandler.customerId));
        managedAppListDetails.put(ManagedAppDataHandler.managedAppList, (Object)appGroupList);
        managedAppListDetails.put(ManagedAppDataHandler.needToSync, managedApps.optBoolean(ManagedAppDataHandler.needToSync));
        final JSONArray AppDetailsArrayList = this.processManagedAppsList(managedAppListDetails);
        return AppDetailsArrayList;
    }
    
    private Long getCollectionAppID(final Long appId) {
        final Long collAppId = appId;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("MdAppToGroupRel"));
            sq.addJoin(new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            sq.addJoin(new Join("MdPackageToAppGroup", "MdPackageToAppData", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2));
            sq.addJoin(new Join("MdPackageToAppData", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            sq.setCriteria(new Criteria(new Column("MdAppToGroupRel", "APP_ID"), (Object)appId, 0));
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dO = MDMUtil.getPersistence().get(sq);
            if (!dO.isEmpty()) {
                final Long collectionId = (Long)dO.getValue("MdAppToCollection", "COLLECTION_ID", new Criteria(new Column("MdAppToCollection", "APP_ID"), (Object)appId, 0));
                if (collectionId == null) {
                    return (Long)dO.getFirstRow("MdAppToCollection").get("APP_ID");
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return collAppId;
    }
    
    public boolean isAppPurchasedFromPortal(final Long appGroupId) {
        boolean ispurchased = false;
        try {
            ispurchased = (boolean)DBUtil.getValueFromDB("MdPackageToAppGroup", "APP_GROUP_ID", (Object)appGroupId, "IS_PURCHASED_FROM_PORTAL");
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while checking app purchasedfrom porta", ex);
        }
        return ispurchased;
    }
    
    public boolean appsPurchasedFromPortal(final int platform) {
        boolean isPurchased = false;
        final Criteria isAppPurchasedFromPortal = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
        final Criteria platformCrit = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platform, 0);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        final Join appGroupJoin = new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        selectQuery.addJoin(appGroupJoin);
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        selectQuery.setCriteria(isAppPurchasedFromPortal.and(platformCrit));
        try {
            final DataObject appDO = MDMUtil.getPersistence().get(selectQuery);
            if (!appDO.isEmpty()) {
                isPurchased = true;
            }
        }
        catch (final DataAccessException e) {
            e.printStackTrace();
        }
        return isPurchased;
    }
    
    public ArrayList processIOSManagedAppDict(final NSDictionary managedAppListDict) {
        final ArrayList appGroupList = new ArrayList();
        for (int i = 0; i < managedAppListDict.allKeys().length; ++i) {
            final JSONObject managedAppJson = new JSONObject();
            try {
                final String bundleIdentifier = managedAppListDict.allKeys()[i];
                managedAppJson.put("IDENTIFIER", (Object)bundleIdentifier);
                final NSDictionary nsSubDict = (NSDictionary)managedAppListDict.objectForKey(bundleIdentifier);
                final String status = nsSubDict.objectForKey("Status").toString();
                String unUsedRedemptionCodes = null;
                final NSObject nsUnusedRedemptionCode = nsSubDict.objectForKey("UnusedRedemptionCode");
                if (nsUnusedRedemptionCode != null) {
                    unUsedRedemptionCodes = nsUnusedRedemptionCode.toString();
                    if (!unUsedRedemptionCodes.equalsIgnoreCase("VPP_DUMMY_CODE")) {
                        new AppleAppLicenseMgmtHandler().revokeLicenseForDevices(unUsedRedemptionCodes);
                    }
                }
                this.logger.log(Level.INFO, "processManagedAppsList: bundleIdentifier : {0} status: {1} ", new Object[] { bundleIdentifier, status });
                String appStatusRemarks = "--";
                Long errorCode = null;
                int appStatus = 0;
                int collectionStatus = 12;
                if (status != null && !status.isEmpty()) {
                    if (status.equalsIgnoreCase("Prompting") || status.equalsIgnoreCase("PromptingForLogin") || status.equalsIgnoreCase("PromptingForUpdate") || status.equalsIgnoreCase("PromptingForUpdateLogin")) {
                        appStatus = 1;
                        collectionStatus = 3;
                        appStatusRemarks = "dc.db.mdm.apps.status.Prompting";
                    }
                    else if (status.equalsIgnoreCase("Installing")) {
                        appStatus = 1;
                        collectionStatus = 3;
                        appStatusRemarks = "dc.db.mdm.apps.status.Installing";
                    }
                    else if (status.equalsIgnoreCase("NeedsRedmption")) {
                        appStatus = 1;
                        collectionStatus = 3;
                        appStatusRemarks = "dc.db.mdm.apps.status.NeedsRedmption";
                    }
                    else if (status.equalsIgnoreCase("Reedeming")) {
                        appStatus = 1;
                        collectionStatus = 3;
                        appStatusRemarks = "dc.db.mdm.apps.status.Reedeming";
                    }
                    else if (status.equalsIgnoreCase("Managed")) {
                        appStatus = 2;
                        collectionStatus = 6;
                        appStatusRemarks = "dc.db.mdm.collection.Successfully_installed_the_app";
                    }
                    else if (status.equalsIgnoreCase("ManagedButUninstalled")) {
                        appStatus = 0;
                        collectionStatus = 12;
                        appStatusRemarks = "dc.db.mdm.apps.status.ManagedButUninstalled";
                    }
                    else if (status.equalsIgnoreCase("UserRejected") || status.equalsIgnoreCase("UpdateRejected")) {
                        appStatus = 0;
                        collectionStatus = 12;
                        appStatusRemarks = MDMUtil.replaceProductUrlLoaderValuesinText(I18N.getMsg("dc.db.mdm.apps.status.UserRejected", new Object[0]), "UserRejected");
                    }
                    else if (status.equalsIgnoreCase("Failed")) {
                        appStatus = 0;
                        collectionStatus = 7;
                        errorCode = 9006L;
                        appStatusRemarks = "dc.db.mdm.apps.status.Failed";
                    }
                    else if (status.equalsIgnoreCase("Unknown")) {
                        appStatus = 0;
                        collectionStatus = 12;
                        appStatusRemarks = "dc.db.mdm.apps.status.Unknown";
                    }
                    else if (status.equalsIgnoreCase("UserInstalledApp")) {
                        appStatus = 5;
                        collectionStatus = 12;
                        appStatusRemarks = "dc.db.mdm.collection.App_already_installed_ios";
                    }
                    else if (status.equalsIgnoreCase("ManagementRejected")) {
                        appStatus = 5;
                        collectionStatus = 12;
                        appStatusRemarks = "dc.mdm.android.app_installation_from_appcatalog";
                    }
                    else if (status.equalsIgnoreCase("PromptingForManagement")) {
                        appStatus = 1;
                        collectionStatus = 3;
                        appStatusRemarks = "mdm.apps.status.PromptingManagement";
                    }
                    else if (status.equalsIgnoreCase("Updating")) {
                        appStatus = 1;
                        collectionStatus = 3;
                        appStatusRemarks = "mdm.apps.status.Updating";
                    }
                }
                managedAppJson.put("DeviceStatus", (Object)status);
                managedAppJson.put("AppStatus", appStatus);
                managedAppJson.put("CollectionStatus", collectionStatus);
                managedAppJson.put("Remarks", (Object)appStatusRemarks);
                managedAppJson.put("ErrorCode", (Object)errorCode);
                appGroupList.add(managedAppJson);
                this.logger.log(Level.INFO, "processManagedAppsList: collectionStatus: {0} appStatus: {1}", new Object[] { collectionStatus, appStatus });
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception occurred in processManagedAppsList()", ex);
            }
        }
        return appGroupList;
    }
    
    public void bringUnmanagedAppAsManaged(final Long resourceId) {
        try {
            final Boolean isDeviceEligible = ManagedDeviceHandler.getInstance().isSupervisedAndEqualOrAboveVersion(resourceId, "9.0");
            final List collectionList = new ArrayList();
            final List profileList = new ArrayList();
            final List resourceList = new ArrayList();
            resourceList.add(resourceId);
            final HashMap profileCollnMap = new HashMap();
            if (isDeviceEligible) {
                SelectQuery selectQuery = null;
                selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToResource"));
                final Join appToCollJoin = new Join("MdAppCatalogToResource", "MdAppToCollection", new String[] { "PUBLISHED_APP_ID" }, new String[] { "APP_ID" }, 2);
                final Join packageJoin = new Join("MdAppCatalogToResource", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
                final Join collToProfileJoin = new Join("MdAppToCollection", "RecentProfileForResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
                selectQuery.addJoin(appToCollJoin);
                selectQuery.addJoin(packageJoin);
                selectQuery.addJoin(collToProfileJoin);
                selectQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "*"));
                selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "*"));
                selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
                Criteria appCatalogresCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceId, 0);
                final Criteria recentPubResCri = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceId, 0);
                final Criteria markForDeleteCri = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
                if (MDMUtil.getInstance().isMacDevice(resourceId)) {
                    appCatalogresCriteria = appCatalogresCriteria.and(new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 1));
                }
                final Criteria appCatalogStatusCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "STATUS"), (Object)5, 0);
                selectQuery.setCriteria(appCatalogresCriteria.and(appCatalogStatusCri).and(recentPubResCri).and(markForDeleteCri));
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    final Iterator iter = dataObject.getRows("RecentProfileForResource");
                    while (iter.hasNext()) {
                        final Row profileToCOllnRow = iter.next();
                        final Long profileId = (Long)profileToCOllnRow.get("PROFILE_ID");
                        final Long collnId = (Long)profileToCOllnRow.get("COLLECTION_ID");
                        profileCollnMap.put(profileId, collnId);
                        profileList.add(profileId);
                        collectionList.add(collnId);
                    }
                    final Properties taskProps = new Properties();
                    final HashMap deviceMap = new HashMap();
                    final Set resSet = new HashSet();
                    resSet.addAll(resourceList);
                    deviceMap.put(1, resSet);
                    ((Hashtable<String, HashMap>)taskProps).put("deviceMap", deviceMap);
                    final HashMap collectionToPlatformMap = new HashMap();
                    collectionToPlatformMap.put(1, collectionList);
                    ((Hashtable<String, HashMap>)taskProps).put("collectionToPlatformMap", collectionToPlatformMap);
                    final HashMap profileToPlatformMap = new HashMap();
                    profileToPlatformMap.put(1, profileList);
                    ((Hashtable<String, HashMap>)taskProps).put("profileToPlatformMap", profileToPlatformMap);
                    ((Hashtable<String, List>)taskProps).put("resourceList", resourceList);
                    ((Hashtable<String, List>)taskProps).put("collectionList", collectionList);
                    ((Hashtable<String, HashMap>)taskProps).put("profileCollnMap", profileCollnMap);
                    final Long customerIdAsLong = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
                    ((Hashtable<String, Long>)taskProps).put("customerId", customerIdAsLong);
                    ((Hashtable<String, String>)taskProps).put("commandName", "ManageApplication");
                    ((Hashtable<String, Integer>)taskProps).put("commandType", 1);
                    final HashMap taskInfoMap = new HashMap();
                    taskInfoMap.put("taskName", "AssignDeviceCommandTask");
                    taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                    taskInfoMap.put("poolName", "mdmPool");
                    this.logger.log(Level.INFO, "Task is going to be called for adding ManageApplication command for resource {0} : collection {1}", new Object[] { resourceId, collectionList });
                    AssociationQueueHandler.getInstance().executeTask(taskInfoMap, taskProps);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred in bringUnmanagedAppAsManaged()", ex);
        }
    }
    
    static {
        ManagedAppDataHandler.managedAppList = "managedAppList";
        ManagedAppDataHandler.resourceId = "resourceId";
        ManagedAppDataHandler.customerId = "customerId";
        ManagedAppDataHandler.needToSync = "needToSync";
    }
}
