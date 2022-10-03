package com.adventnet.sym.server.mdm.apps;

import java.util.Hashtable;
import java.util.Objects;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.adventnet.ds.query.SortColumn;
import java.util.Enumeration;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.apps.handler.AppsAutoDeployment;
import java.sql.SQLException;
import com.me.mdm.server.alerts.MDMAlertMailGeneratorUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.alerts.MDMAlertConstants;
import com.adventnet.i18n.I18N;
import org.json.JSONArray;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Set;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppAssociationHandler;
import com.me.mdm.server.profiles.kiosk.IOSKioskProfileDataHandler;
import com.me.mdm.server.config.MDMCollectionUtil;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.persistence.Row;
import org.json.JSONObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Collection;
import com.adventnet.sym.server.mdm.apps.ios.IOSAppUtils;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import java.util.Map;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class AppLicenseMgmtHandler
{
    protected static final int PORTAL_APP = 1;
    protected static final int NON_PORTAL_APP = 2;
    protected static final int ENTERPRISE_APP = 3;
    protected Logger logger;
    protected Logger profileDistributionLog;
    protected Logger appMgmtLogger;
    public static String notSupportedDeviceList;
    public static String failedResourceList;
    public static String notAdhocRegisteredDeviceList;
    
    public AppLicenseMgmtHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.profileDistributionLog = Logger.getLogger("MDMProfileDistributionLog");
        this.appMgmtLogger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    public Properties assignAppForDevices(final Properties prop) {
        final Long startTime = System.currentTimeMillis();
        final List totalResourceList = ((Hashtable<K, List>)prop).get("resourceList");
        final Map<Long, List<Long>> collnToApplicableResourceList = ((Hashtable<K, Map<Long, List<Long>>>)prop).get("collectionToApplicableResource");
        final List collectionList = ((Hashtable<K, List>)prop).get("collectionList");
        final int platformtype = ((Hashtable<K, Integer>)prop).get("platformtype");
        final Long customerId = ((Hashtable<K, Long>)prop).get("customerId");
        final Boolean isSilentInstall = prop.get("isSilentInstall") != null && ((Hashtable<K, Boolean>)prop).get("isSilentInstall");
        final Boolean isNotify = prop.get("isNotify") != null && ((Hashtable<K, Boolean>)prop).get("isNotify");
        final Boolean isAppUpgrade = ((Hashtable<K, Boolean>)prop).get("isAppUpgrade");
        final Boolean wakeUpDevices = prop.get("wakeUpDevices") == null || ((Hashtable<K, Boolean>)prop).get("wakeUpDevices");
        final boolean isNewApp = ((Hashtable<K, Boolean>)prop).getOrDefault("isNewApp", false);
        final Boolean afwAccountReadyHandling = ((Hashtable<K, Boolean>)prop).getOrDefault("AFWAccountReadyHandling", false);
        Integer associatedAppSource = ((Hashtable<K, Integer>)prop).get("toBeAssociatedAppSource");
        if (associatedAppSource == null) {
            associatedAppSource = MDMCommonConstants.ASSOCIATED_APP_SOURCE_UNKNOWN;
        }
        Long collectionAppID = null;
        boolean isLicensedApp = false;
        boolean isPurchasedFromPortal = false;
        boolean isPaidApp = false;
        this.profileDistributionLog.log(Level.INFO, "assignAppForDevices(): resourceList: {0} collectionId: {1}", new Object[] { totalResourceList, collectionList });
        this.profileDistributionLog.log(Level.INFO, "collnToApplicableResourceListMap(): {0}", collnToApplicableResourceList);
        final HashMap<Long, String> resAppHash = new HashMap<Long, String>();
        final HashMap<Long, String> resAppUpgradeHash = new HashMap<Long, String>();
        final ProfileAssociateHandler handler = ProfileAssociateHandler.getInstance();
        final AppInstallationStatusHandler appInstallationStatusHandler = new AppInstallationStatusHandler();
        final Properties notAddedResourceList = new Properties();
        Long businessStoreID = null;
        for (int j = 0; j < collectionList.size(); ++j) {
            try {
                final Long collectionId = collectionList.get(j);
                if (prop.containsKey("collectionToBusinessStore")) {
                    final Properties collectionToBusinessStore = ((Hashtable<K, Properties>)prop).get("collectionToBusinessStore");
                    if (!collectionToBusinessStore.isEmpty()) {
                        businessStoreID = ((Hashtable<K, Long>)collectionToBusinessStore).get(collectionId);
                    }
                }
                List<Long> resourceList = totalResourceList;
                if (collnToApplicableResourceList != null) {
                    resourceList = collnToApplicableResourceList.get(collectionId);
                }
                collectionAppID = MDMUtil.getInstance().getAppIdFromCollectionId(collectionId);
                this.profileDistributionLog.log(Level.INFO, "assignAppForDevices: appID: {0}", collectionAppID);
                final int supDevice = MDMUtil.getInstance().getSupportedDevice(collectionAppID);
                final HashMap appDetailMap = MDMUtil.getInstance().getAppPackageDataDetails(collectionAppID);
                this.profileDistributionLog.log(Level.INFO, "assignAppForDevices: appDetailMap: {0}", appDetailMap);
                if (businessStoreID != null) {
                    isLicensedApp = Boolean.TRUE;
                }
                isPurchasedFromPortal = appDetailMap.get("IS_PURCHASED_FROM_PORTAL");
                isPaidApp = appDetailMap.get("IS_PAID_APP");
                final Long appGroupId = appDetailMap.get("APP_GROUP_ID");
                final String storeIdStr = appDetailMap.get("STORE_ID");
                final int pkgType = appDetailMap.get("PACKAGE_TYPE");
                if (platformtype == 1) {
                    isLicensedApp = (businessStoreID != null && isPurchasedFromPortal);
                }
                else if (isPurchasedFromPortal) {
                    isLicensedApp = true;
                }
                final List<Long> expiredList = new ArrayList<Long>();
                long expiryDate = -1L;
                if (platformtype == 1 && supDevice != 16 && pkgType == 2) {
                    expiryDate = new IOSAppUtils().getAppExpiryDate(collectionAppID);
                    if (expiryDate == -1L) {
                        this.logger.log(Level.WARNING, "App expiry date is not found for app with id = {0} ", collectionAppID);
                    }
                    if (expiryDate < System.currentTimeMillis() && expiryDate != -1L) {
                        expiredList.addAll(resourceList);
                        this.appMgmtLogger.log(Level.INFO, "The app with APP_ID {0} is expired which is distributed to resources with ids : {1}", new Object[] { collectionAppID, expiredList });
                    }
                }
                resourceList.removeAll(expiredList);
                final HashMap deviceToApp = this.getAppIDsForResource(resourceList, appGroupId, collectionId);
                this.profileDistributionLog.log(Level.INFO, "Device to App Map , -1 indicates App is not compatible for above logged reasons:{0}", deviceToApp);
                final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
                final List alreadyAssignedList = new ArrayList();
                final List alreadyAppInstalledList = new ArrayList();
                final List appInstalledYetToManage = new ArrayList();
                final DataObject alreadyAssignedDO = MDMUtil.getPersistence().get("MdAppCatalogToResource", appGroupCriteria);
                final List notSupSmartPhoneList = new ArrayList();
                final List notSupTabletList = new ArrayList();
                final List<Long> distributedResList = new ArrayList<Long>();
                final List<Long> applicableResList = new ArrayList<Long>();
                final JSONObject upgradeAppsjson = new JSONObject();
                final List<Long> notSupportedList = new ArrayList<Long>();
                final List<Long> notAdHocRegResList = new ArrayList<Long>();
                for (final Long resID : deviceToApp.keySet()) {
                    if (deviceToApp.get(resID) == -1L) {
                        notSupportedList.add(resID);
                    }
                }
                if (platformtype == 1 && pkgType == 2) {
                    final int provType = new IOSAppUtils().getEnterpriseAppProvisionSignedType(collectionAppID);
                    if (provType == -1) {
                        this.logger.log(Level.WARNING, "App provision details is not available for APP_ID = {0} ", collectionAppID);
                    }
                    if (provType == 1 || provType == 2) {
                        notAdHocRegResList.addAll(resourceList);
                        notAdHocRegResList.removeAll(notSupportedList);
                        notAdHocRegResList.removeAll(new IOSAppUtils().getAdhocProvisionedDevicesForAppleEnterpriseApp(notAdHocRegResList, collectionAppID, customerId));
                        this.logger.log(Level.INFO, "The resources with ids: {0} are not not registerd for the APP_ID", new Object[] { notAdHocRegResList, collectionAppID });
                    }
                }
                for (int i = 0; i < resourceList.size(); ++i) {
                    final Long resourceID = resourceList.get(i);
                    final Long latestVersionAppID = deviceToApp.get(resourceID);
                    final Criteria appCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "PUBLISHED_APP_ID"), (Object)latestVersionAppID, 0);
                    final Criteria resCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceID, 0);
                    final Iterator alreadyAssignedRows = alreadyAssignedDO.getRows("MdAppCatalogToResource", appCriteria.and(resCriteria));
                    if (alreadyAssignedRows.hasNext()) {
                        alreadyAssignedList.add(resourceID);
                    }
                    final Iterator appCatalogRows = alreadyAssignedDO.getRows("MdAppCatalogToResource", resCriteria);
                    if (appCatalogRows.hasNext()) {
                        final Row appCatalogRow = appCatalogRows.next();
                        final Long publishedAppId = (Long)appCatalogRow.get("PUBLISHED_APP_ID");
                        final Long installedAppId = (Long)appCatalogRow.get("INSTALLED_APP_ID");
                        final Long resId = (Long)appCatalogRow.get("RESOURCE_ID");
                        final int status = (int)appCatalogRow.get("STATUS");
                        if (installedAppId != null && latestVersionAppID.compareTo(publishedAppId) != 0 && installedAppId.compareTo(latestVersionAppID) != 0) {
                            upgradeAppsjson.put(resId.toString(), true);
                            this.profileDistributionLog.log(Level.INFO, "Setting App upgrade to true for resouce ID:{0} fro App :{1} , In MDAppCatalogToResource InstalledAppID{2},  and PublishedAppID is{3}", new Object[] { resourceID, appGroupId, installedAppId, publishedAppId });
                        }
                        else {
                            upgradeAppsjson.put(resId.toString(), false);
                        }
                        if (installedAppId != null && installedAppId.compareTo(latestVersionAppID) == 0) {
                            alreadyAppInstalledList.add(resId);
                            if (status == 5 || status == 0) {
                                appInstalledYetToManage.add(resId);
                                this.profileDistributionLog.log(Level.INFO, "App Installed by yet to manage resouce ID:{0} fro App :{1} , Status:{2}", new Object[] { resourceID, appGroupId, status });
                            }
                        }
                    }
                    this.profileDistributionLog.log(Level.INFO, "App already assigned for resource IDs {0}", alreadyAssignedList);
                    this.profileDistributionLog.log(Level.INFO, "App already installed in resource IDs {0}", alreadyAppInstalledList);
                    Boolean addDetails = Boolean.FALSE;
                    int nonIOSmodelType = MDMUtil.getInstance().getModelType(resourceID);
                    if (platformtype != 1) {
                        Boolean isSupAll = Boolean.FALSE;
                        if (supDevice == 1 || supDevice == 24) {
                            isSupAll = Boolean.TRUE;
                        }
                        if (isSupAll) {
                            addDetails = Boolean.TRUE;
                        }
                        else {
                            if (nonIOSmodelType == 0) {
                                nonIOSmodelType = 2;
                            }
                            if (nonIOSmodelType == supDevice) {
                                addDetails = Boolean.TRUE;
                            }
                        }
                        if (supDevice == 8 || supDevice == 16) {
                            addDetails = Boolean.TRUE;
                        }
                    }
                    else {
                        addDetails = Boolean.TRUE;
                    }
                    if (addDetails) {
                        if (!alreadyAssignedList.contains(resourceID) && !notSupportedList.contains(resourceID) && !notAdHocRegResList.contains(resourceID)) {
                            distributedResList.add(resourceID);
                        }
                        applicableResList.add(resourceID);
                    }
                    else if (nonIOSmodelType == 3) {
                        notSupTabletList.add(resourceID);
                    }
                    else {
                        notSupSmartPhoneList.add(resourceID);
                    }
                }
                this.profileDistributionLog.log(Level.INFO, "Final App Not Supported resourceList:{0}", notSupportedList);
                applicableResList.removeAll(notSupportedList);
                ((Hashtable<String, List<Long>>)notAddedResourceList).put(AppLicenseMgmtHandler.notSupportedDeviceList, notSupportedList);
                applicableResList.removeAll(notAdHocRegResList);
                this.profileDistributionLog.log(Level.INFO, "Final Applicable resourceList:{0}", applicableResList);
                final List failedList = new ArrayList();
                Properties iosLicensedAppProp = new Properties();
                Properties iosFailedAppProp = new Properties();
                MDMUtil.getPersistence().update(alreadyAssignedDO);
                if (isLicensedApp && !isNewApp && platformtype == 1 && !applicableResList.isEmpty()) {
                    this.profileDistributionLog.log(Level.INFO, "{0} App is VPP App , going to assign license for device:{1}", new Object[] { appGroupId, applicableResList });
                    iosLicensedAppProp = this.assignLicenseForDevices(applicableResList, appGroupId, storeIdStr, collectionId, customerId, businessStoreID);
                    iosFailedAppProp = ((Hashtable<K, Properties>)iosLicensedAppProp).get("FailedProp");
                    final Set resIdList = iosFailedAppProp.keySet();
                    for (final Long deviceId : resIdList) {
                        failedList.add(deviceId);
                    }
                }
                ((Hashtable<String, List>)notAddedResourceList).put(AppLicenseMgmtHandler.failedResourceList, failedList);
                final List upgradeList = new ArrayList();
                final DataObject mdAppCatalogtoResDO = AppsUtil.getInstance().getMDAppCatalogtoResDO(appGroupId);
                for (int k = 0; k < distributedResList.size(); ++k) {
                    final Long resId2 = distributedResList.get(k);
                    final Long latestVersionAppID2 = deviceToApp.get(resId2);
                    final Criteria cRes = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resId2, 0);
                    Row assignRow = alreadyAssignedDO.getRow("MdAppCatalogToResource", cRes);
                    String remark = handler.getInstallAppFromCatalogRemark(pkgType, platformtype, false);
                    if (assignRow != null) {
                        final Long installedAppId2 = (Long)assignRow.get("INSTALLED_APP_ID");
                        if (new MDMAppUpdateMgmtHandler().isAppCatalogUpgradeAction(installedAppId2, latestVersionAppID2, pkgType, platformtype)) {
                            upgradeList.add(resId2);
                            remark = handler.getInstallAppFromCatalogRemark(pkgType, platformtype, true);
                        }
                        assignRow.set("RESOURCE_ID", (Object)resId2);
                        assignRow.set("APP_GROUP_ID", (Object)appGroupId);
                        assignRow.set("PUBLISHED_APP_ID", (Object)latestVersionAppID2);
                        assignRow.set("APPROVED_APP_ID", (Object)latestVersionAppID2);
                        assignRow.set("APPROVED_VERSION_STATUS", (Object)3);
                        assignRow.set("UPDATED_AT", (Object)System.currentTimeMillis());
                        int appCatStatus = 0;
                        if (iosFailedAppProp.get(resId2) != null && iosFailedAppProp.containsKey(resId2)) {
                            appCatStatus = 7;
                        }
                        assignRow.set("STATUS", (Object)appCatStatus);
                        assignRow.set("REMARKS", (Object)((iosFailedAppProp.get(resId2) != null) ? ((Hashtable<K, String>)((Hashtable<K, Properties>)iosFailedAppProp).get(resId2)).get("REMARKS") : remark));
                        AppsUtil.getInstance().addOrUpdateAppCatalogScopeRel(resId2, appGroupId, AppsUtil.getInstance().getScopeForApp(resId2, appGroupId));
                        alreadyAssignedDO.updateRow(assignRow);
                    }
                    else {
                        assignRow = new Row("MdAppCatalogToResource");
                        assignRow.set("RESOURCE_ID", (Object)resId2);
                        assignRow.set("APP_GROUP_ID", (Object)appGroupId);
                        assignRow.set("PUBLISHED_APP_ID", (Object)latestVersionAppID2);
                        assignRow.set("APPROVED_APP_ID", (Object)latestVersionAppID2);
                        assignRow.set("APPROVED_VERSION_STATUS", (Object)3);
                        assignRow.set("UPDATED_AT", (Object)System.currentTimeMillis());
                        int appCatStatus2 = 0;
                        if (iosFailedAppProp != null && iosFailedAppProp.containsKey(resId2)) {
                            appCatStatus2 = 7;
                        }
                        assignRow.set("STATUS", (Object)appCatStatus2);
                        assignRow.set("REMARKS", (Object)((iosFailedAppProp.get(resId2) != null) ? ((Hashtable<K, String>)((Hashtable<K, Properties>)iosFailedAppProp).get(resId2)).get("REMARKS") : remark));
                        final Row appCatalogScopeRow = new Row("MdAppCatalogToResourceScope");
                        appCatalogScopeRow.set("RESOURCE_ID", (Object)resId2);
                        appCatalogScopeRow.set("APP_GROUP_ID", (Object)appGroupId);
                        appCatalogScopeRow.set("SCOPE", (Object)AppsUtil.getInstance().getScopeForApp(resId2, appGroupId));
                        alreadyAssignedDO.addRow(assignRow);
                        alreadyAssignedDO.addRow(appCatalogScopeRow);
                    }
                    Row mdAppCatalogtoResRow = null;
                    if (mdAppCatalogtoResDO != null && !mdAppCatalogtoResDO.isEmpty()) {
                        final Criteria resCriForAppCatalogExtnTbl = new Criteria(new Column("MdAppCatalogToResourceExtn", "RESOURCE_ID"), (Object)resId2, 0);
                        mdAppCatalogtoResRow = mdAppCatalogtoResDO.getRow("MdAppCatalogToResourceExtn", resCriForAppCatalogExtnTbl);
                    }
                    if (mdAppCatalogtoResRow == null) {
                        mdAppCatalogtoResRow = new Row("MdAppCatalogToResourceExtn");
                        mdAppCatalogtoResRow.set("APP_GROUP_ID", (Object)appGroupId);
                        mdAppCatalogtoResRow.set("RESOURCE_ID", (Object)resId2);
                        mdAppCatalogtoResRow.set("PUBLISHED_APP_SOURCE", (Object)associatedAppSource);
                        mdAppCatalogtoResRow.set("IS_UPDATE_AVAILABLE", (Object)false);
                        mdAppCatalogtoResDO.addRow(mdAppCatalogtoResRow);
                    }
                    else {
                        mdAppCatalogtoResRow.set("PUBLISHED_APP_SOURCE", (Object)associatedAppSource);
                        mdAppCatalogtoResRow.set("IS_UPDATE_AVAILABLE", (Object)false);
                        mdAppCatalogtoResDO.updateRow(mdAppCatalogtoResRow);
                    }
                    this.profileDistributionLog.log(Level.INFO, "[APP] [UPDATE] [AssignAppForDevice] Update avaiable falg cleared for resource  {0} AppGroup : {1}", new Object[] { resId2, appGroupId });
                }
                MDMUtil.getPersistence().update(alreadyAssignedDO);
                MDMUtil.getPersistence().update(mdAppCatalogtoResDO);
                final List resListToInstallApps = new ArrayList(resourceList);
                resListToInstallApps.removeAll(alreadyAppInstalledList);
                resListToInstallApps.removeAll(notSupSmartPhoneList);
                resListToInstallApps.removeAll(notSupTabletList);
                resListToInstallApps.removeAll(notSupportedList);
                resListToInstallApps.removeAll(failedList);
                resListToInstallApps.removeAll(notAdHocRegResList);
                resListToInstallApps.removeAll(expiredList);
                resListToInstallApps.addAll(appInstalledYetToManage);
                this.profileDistributionLog.log(Level.INFO, "App not supported resource IDs:notSupSmartPhoneList {0},notSupTabletList {1}, notSupportedList {2}", new Object[] { notSupSmartPhoneList, notSupTabletList, notSupportedList });
                final String notSupSmartRemarks = "dc.mdm.device_mgmt.app_not_supported_for_smartphone";
                final String notSupTabletRemarks = "dc.mdm.device_mgmt.app_not_supported_for_tablets";
                final String notSupportRemarks = "mdm.windows.app.no_compatible_package";
                final HashMap<String, List> remarksToResMap = new HashMap<String, List>();
                remarksToResMap.put(notSupSmartRemarks, notSupSmartPhoneList);
                remarksToResMap.put(notSupTabletRemarks, notSupTabletList);
                remarksToResMap.put(notSupportRemarks, notSupportedList);
                MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(notSupSmartPhoneList, collectionId, 8, notSupSmartRemarks);
                MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(notSupTabletList, collectionId, 8, notSupTabletRemarks);
                MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(notSupportedList, collectionId, 8, notSupportRemarks);
                if (!notAdHocRegResList.isEmpty()) {
                    MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(notAdHocRegResList, collectionId, 8, "dc.mdm.devicemgmt.device_not_registered_for_adhoc");
                    MDMCollectionUtil.addOrUpdateCollnToResErrorCode(notAdHocRegResList, collectionId, 21013);
                    this.logger.log(Level.INFO, "The app with app_id = {0} was not added to the app catalog of resources with resourceIds = {1} because these are unregistered for adhoc app", new Object[] { collectionAppID, notAdHocRegResList });
                }
                if (!expiredList.isEmpty() && expiryDate != -1L) {
                    MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(expiredList, collectionId, 8, "dc.mdm.devicemgmt.app_expired_distribution_error@@@LongTime:" + expiryDate);
                    MDMCollectionUtil.addOrUpdateCollnToResErrorCode(expiredList, collectionId, 21012);
                    this.logger.log(Level.INFO, "The app wth app_id = {0} was on {1} expired and was not added to the app catalog of resources with resourceIds = {2} ", new Object[] { collectionAppID, MDMUtil.getDate(expiryDate), notAdHocRegResList });
                }
                distributedResList.removeAll(upgradeList);
                if (!distributedResList.isEmpty()) {
                    this.setResAppHashMap(distributedResList, collectionId, resAppHash);
                }
                if (!upgradeList.isEmpty()) {
                    this.setResAppHashMap(upgradeList, collectionId, resAppUpgradeHash);
                }
                final List tempList = ((Hashtable<K, List>)notAddedResourceList).get(AppLicenseMgmtHandler.notSupportedDeviceList);
                tempList.addAll(notSupSmartPhoneList);
                tempList.addAll(notSupTabletList);
                tempList.addAll(notAdHocRegResList);
                final List failedListTemp = ((Hashtable<K, List>)notAddedResourceList).get(AppLicenseMgmtHandler.failedResourceList);
                failedListTemp.addAll(expiredList);
                List notApplicableList = new ArrayList();
                if (platformtype == 1) {
                    final String userId = ((Hashtable<K, String>)prop).get("UserId");
                    final IOSKioskProfileDataHandler kioskHandler = new IOSKioskProfileDataHandler();
                    final JSONObject appObject = new JSONObject();
                    appObject.put("resourceList", (Object)resourceList);
                    appObject.put("collectionId", (Object)collectionId);
                    appObject.put("UserId", (Object)userId);
                    appObject.put("isAppUpgrade", (Object)isAppUpgrade);
                    notApplicableList = kioskHandler.isNeedToAddUpdateKiosk(appObject);
                }
                if (isSilentInstall) {
                    resListToInstallApps.removeAll(notApplicableList);
                    if (!resListToInstallApps.isEmpty()) {
                        final Properties silentInstallAppProp = new Properties();
                        ((Hashtable<String, Long>)silentInstallAppProp).put("collectionId", collectionId);
                        ((Hashtable<String, Long>)silentInstallAppProp).put("appGroupId", appGroupId);
                        ((Hashtable<String, HashMap>)silentInstallAppProp).put("latestVersionAppID", deviceToApp);
                        ((Hashtable<String, List>)silentInstallAppProp).put("resListToSilentlyInstallApps", resListToInstallApps);
                        ((Hashtable<String, Integer>)silentInstallAppProp).put("platformtype", platformtype);
                        ((Hashtable<String, JSONObject>)silentInstallAppProp).put("upgradeAppsjson", upgradeAppsjson);
                        ((Hashtable<String, Boolean>)silentInstallAppProp).put("wakeUpDevices", wakeUpDevices);
                        ((Hashtable<String, Integer>)silentInstallAppProp).put("supportedDevices", supDevice);
                        this.silentInstallApps(silentInstallAppProp, customerId);
                    }
                }
                else {
                    final String distributeResRemarks = handler.getInstallAppFromCatalogRemark(pkgType, platformtype, false);
                    final String upgradeListResRemarks = handler.getInstallAppFromCatalogRemark(pkgType, platformtype, true);
                    final HashMap<String, List> remarksToDisResMap = new HashMap<String, List>();
                    distributedResList.removeAll(iosFailedAppProp.keySet());
                    upgradeList.removeAll(iosFailedAppProp.keySet());
                    remarksToDisResMap.put(distributeResRemarks, distributedResList);
                    remarksToDisResMap.put(upgradeListResRemarks, upgradeList);
                    appInstallationStatusHandler.updateAppStatus(remarksToDisResMap, collectionId, 12);
                    this.profileDistributionLog.log(Level.INFO, "Yet to apply remarks refilled {0} ; upgrade{1}", new Object[] { distributedResList, upgradeList });
                }
                if (iosLicensedAppProp.get("userLicensedDeviceList") != null) {
                    final String helpUrl = "/how-to/silent-installation-ios-apps.html?$(traceurl)&pgSrc=$(pageSource)";
                    final String remark2 = "dc.mdm.vpp.distribute_vpp_apps_with_appleid@@@<a target='blank' href=\"$(mdmUrl)" + helpUrl + "\">@@@</a>";
                    MDMCollectionStatusUpdate.getInstance().updateCollnToResourcesRow(((Hashtable<K, ArrayList>)iosLicensedAppProp).get("userLicensedDeviceList"), collectionId, 12, remark2);
                }
                if (!iosFailedAppProp.isEmpty()) {
                    VPPAppAssociationHandler.getInstance().updateDeviceBasedAsgnmentFailedCollnStatus(iosFailedAppProp, collectionId);
                    this.profileDistributionLog.log(Level.INFO, "iOS Failed remarks {0}", new Object[] { failedList });
                }
            }
            catch (final Exception exp) {
                this.profileDistributionLog.log(Level.WARNING, "Exception occurred assignAppForDevices()", exp);
            }
        }
        this.logger.log(Level.INFO, "Assign old app time taken {0}", System.currentTimeMillis() - startTime);
        if (isNotify) {
            try {
                this.sendAppDistributionMail(resAppHash, prop);
                this.sendAppUpgradationMail(resAppUpgradeHash);
            }
            catch (final Exception ex) {
                this.profileDistributionLog.log(Level.SEVERE, null, ex);
            }
        }
        return notAddedResourceList;
    }
    
    public Properties assignLicenseForDevices(final List<Long> resourceList, final Long appGroupId, final Integer storeId, final Long collectionId, final Long customerId) {
        return null;
    }
    
    public Properties assignLicenseForDevices(final List<Long> resourceList, final Long appGroupId, final String storeId, final Long collectionId, final Long customerId, final Long businessStoreID) {
        return null;
    }
    
    public int getLicenseType(final Long appGroupId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdLicense"));
        final Join licenseRel = new Join("MdLicense", "MdLicenseToAppGroupRel", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
        selectQuery.addJoin(licenseRel);
        final Criteria licDetailCri = new Criteria(Column.getColumn("MdLicenseToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
        selectQuery.setCriteria(licDetailCri);
        selectQuery.addSelectColumn(Column.getColumn("MdLicense", "*"));
        final DataObject DO = MDMUtil.getPersistence().get(selectQuery);
        if (!DO.isEmpty()) {
            final Row licenseRow = DO.getFirstRow("MdLicense");
            if (licenseRow != null) {
                return (int)licenseRow.get("LICENSED_TYPE");
            }
        }
        return 0;
    }
    
    public HashMap getAppLicenseDetails(final Long appGroupId, final Long businessStoreID) {
        return null;
    }
    
    public JSONArray getABMAppLicenseCountSummary(final Long appGroupId, final Long businessStoreID) {
        final JSONArray array = new JSONArray();
        try {
            final SelectQuery vppQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdStoreAssetToAppGroupRel"));
            vppQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            vppQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            vppQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            vppQuery.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            vppQuery.addJoin(new Join("ManagedBusinessStore", "MdBusinessStoreSyncStatus", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 1));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "TOTAL_LICENSE"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "AVAILABLE_LICENSE_COUNT"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "ASSIGNED_LICENSE_COUNT"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "LICENSE_TYPE"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "TOKEN_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "TOKEN_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "TOKEN_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "LOCATION_NAME"));
            vppQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToVppRel", "TOKEN_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "*"));
            vppQuery.addSelectColumn(Column.getColumn("MdBusinessStoreSyncStatus", "BUSINESSSTORE_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdBusinessStoreSyncStatus", "REMARKS"));
            Criteria cri = new Criteria(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
            if (businessStoreID != null) {
                cri = cri.and(new Criteria(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            }
            vppQuery.setCriteria(cri);
            final DataObject dataObject = MDMUtil.getPersistence().get(vppQuery);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("MdStoreAssetToAppGroupRel");
                while (iterator.hasNext()) {
                    final Row assetToAppGroupRow = iterator.next();
                    final Long assetID = (Long)assetToAppGroupRow.get("STORE_ASSET_ID");
                    final Row assetRow = dataObject.getRow("MdVppAsset", new Criteria(new Column("MdVppAsset", "VPP_ASSET_ID"), (Object)assetID, 0));
                    final Long vppTokenID = (Long)assetRow.get("TOKEN_ID");
                    final Row businessStoreToVPPRelRow = dataObject.getRow("MdBusinessStoreToVppRel", new Criteria(new Column("MdBusinessStoreToVppRel", "TOKEN_ID"), (Object)vppTokenID, 0));
                    final Long businessStoreIDthis = (Long)businessStoreToVPPRelRow.get("BUSINESSSTORE_ID");
                    final Row businessStoreSyncStatusRow = dataObject.getRow("MdBusinessStoreSyncStatus", new Criteria(new Column("MdBusinessStoreSyncStatus", "BUSINESSSTORE_ID"), (Object)businessStoreIDthis, 0));
                    final Row vppTokenDetailsRow = dataObject.getRow("MdVPPTokenDetails", new Criteria(new Column("MdVPPTokenDetails", "TOKEN_ID"), (Object)vppTokenID, 0));
                    final JSONObject object = new JSONObject();
                    object.put("TOKEN_ID", (Object)vppTokenID);
                    object.put("BUSINESSSTORE_ID", (Object)businessStoreIDthis);
                    final String remarks = (String)businessStoreSyncStatusRow.get("REMARKS");
                    if (remarks != null && !remarks.equalsIgnoreCase("") && !remarks.equalsIgnoreCase("settingClientContext")) {
                        object.put("syncFailure", true);
                    }
                    else {
                        object.put("syncFailure", false);
                    }
                    object.put("LOCATION_NAME", vppTokenDetailsRow.get("LOCATION_NAME"));
                    object.put("TOTAL_LICENSE", assetRow.get("TOTAL_LICENSE"));
                    object.put("LICENSE_TYPE", assetRow.get("LICENSE_TYPE"));
                    object.put("ASSIGNED_LICENSE_COUNT", assetRow.get("ASSIGNED_LICENSE_COUNT"));
                    object.put("AVAILABLE_LICENSE_COUNT", assetRow.get("AVAILABLE_LICENSE_COUNT"));
                    array.put((Object)object);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getAppLicenseDetails...", ex);
        }
        this.logger.log(Level.INFO, "License details for App GroupID:{0} are: {1}", new Object[] { appGroupId, array });
        return array;
    }
    
    protected void sendAppDistributionMail(final HashMap<Long, String> resAppHash, final Properties taskProps) {
        try {
            final Set resIdSet = resAppHash.keySet();
            final Iterator item = resIdSet.iterator();
            Long lastCustomerID = -1L;
            String appName = I18N.getMsg("dc.mdm.ME_MDM_App", new Object[0]);
            while (item.hasNext()) {
                final Long resId = item.next();
                try {
                    final String[] appIDs = resAppHash.get(resId).split(",");
                    final String sAppNames = this.getAppNames(appIDs);
                    final Properties deviceAndUserProperties = this.getDeviceAndUserProperties(resId);
                    final Properties appDistributionProperties = new Properties();
                    ((Hashtable<String, String>)appDistributionProperties).put("$mdm_agent_name$", I18N.getMsg("dc.mdm.ME_MDM_App", new Object[0]));
                    final Integer platformType = ((Hashtable<K, Integer>)deviceAndUserProperties).get("platform");
                    final Long customerId = ((Hashtable<K, Long>)deviceAndUserProperties).get("customerId");
                    final Long alertType = MDMAlertConstants.GENERIC_APP_DISTRIBUTION_MAIL_TEMPLATE;
                    if (customerId != lastCustomerID) {
                        appName = (String)DBUtil.getValueFromDB("AgentRebranding", "CUSTOMER_ID", (Object)customerId, "MDM_APP_NAME");
                        lastCustomerID = customerId;
                    }
                    ((Hashtable<String, String>)appDistributionProperties).put("$mdm_agent_name$", (appName != null) ? appName : "ME MDM App");
                    final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(this.logger);
                    ((Hashtable<String, Object>)appDistributionProperties).put("$device_name$", ((Hashtable<K, Object>)deviceAndUserProperties).get("deviceName"));
                    ((Hashtable<String, String>)appDistributionProperties).put("$app_name_list$", sAppNames);
                    ((Hashtable<String, Object>)appDistributionProperties).put("$user_name$", ((Hashtable<K, Object>)deviceAndUserProperties).get("userName"));
                    ((Hashtable<String, Object>)appDistributionProperties).put("$user_emailid$", ((Hashtable<K, Object>)deviceAndUserProperties).get("emailAddress"));
                    if (taskProps.containsKey("additionalParams")) {
                        ((Hashtable<String, Object>)appDistributionProperties).put("additionalParams", ((Hashtable<K, Object>)taskProps).get("additionalParams"));
                    }
                    mailGenerator.sendMail(alertType, "MDM", customerId, appDistributionProperties);
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, e, () -> "Exception while sending app distribution mail. Managed user missing for resource : " + n);
                }
            }
            this.logger.log(Level.INFO, "Mail sent successfully for app distribution");
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while sending app distribution mail", e2);
        }
    }
    
    protected void sendAppUpgradationMail(final HashMap<Long, String> resAppHash) {
        try {
            final Set resIdSet = resAppHash.keySet();
            final Iterator item = resIdSet.iterator();
            Long lastCustomerID = -1L;
            String appName = I18N.getMsg("dc.mdm.ME_MDM_App", new Object[0]);
            while (item.hasNext()) {
                final Long resId = item.next();
                try {
                    final String[] appIDs = resAppHash.get(resId).split(",");
                    final String sAppNames = this.getAppNames(appIDs);
                    final Properties deviceAndUserProperties = this.getDeviceAndUserProperties(resId);
                    final Integer platformType = ((Hashtable<K, Integer>)deviceAndUserProperties).get("platform");
                    Long alertType = MDMAlertConstants.IOS_APP_UPGRADATION_MAIL_TEMPLATE;
                    final Long customerId = ((Hashtable<K, Long>)deviceAndUserProperties).get("customerId");
                    final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(this.logger);
                    final Properties appDistributionProperties = new Properties();
                    if (platformType == 2) {
                        alertType = MDMAlertConstants.ANDROID_APP_UPGRADATION_MAIL_TEMPLATE;
                        if (customerId != lastCustomerID) {
                            appName = (String)DBUtil.getValueFromDB("AgentRebranding", "CUSTOMER_ID", (Object)customerId, "MDM_APP_NAME");
                            lastCustomerID = customerId;
                        }
                        ((Hashtable<String, String>)appDistributionProperties).put("$mdm_agent_name$", (appName != null) ? appName : "ME MDM App");
                    }
                    ((Hashtable<String, Object>)appDistributionProperties).put("$device_name$", ((Hashtable<K, Object>)deviceAndUserProperties).get("deviceName"));
                    ((Hashtable<String, String>)appDistributionProperties).put("$app_name_list$", sAppNames);
                    ((Hashtable<String, Object>)appDistributionProperties).put("$user_name$", ((Hashtable<K, Object>)deviceAndUserProperties).get("userName"));
                    ((Hashtable<String, Object>)appDistributionProperties).put("$user_emailid$", ((Hashtable<K, Object>)deviceAndUserProperties).get("emailAddress"));
                    mailGenerator.sendMail(alertType, "MDM", customerId, appDistributionProperties);
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, e, () -> "Exception while sending app distribution mail. Manged user missing for resource : " + n);
                }
            }
            this.logger.log(Level.INFO, "Mail sent successfully for app upgradation");
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while sending app upgradation mail", e2);
        }
    }
    
    private String getAppNames(final String[] appIDs) {
        String sAppNames = "";
        try {
            final Criteria cProfileId = new Criteria(new Column("Collection", "COLLECTION_ID"), (Object)appIDs, 8);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Collection"));
            selectQuery.setCriteria(cProfileId);
            selectQuery.addSelectColumn(Column.getColumn("Collection", "COLLECTION_ID"));
            selectQuery.addSelectColumn(Column.getColumn("Collection", "COLLECTION_NAME"));
            final DataObject dobj = MDMUtil.getPersistence().get(selectQuery);
            final Iterator collectionRows = dobj.getRows("Collection");
            while (collectionRows.hasNext()) {
                final Row collectionRow = collectionRows.next();
                final String appName = (String)collectionRow.get("COLLECTION_NAME");
                sAppNames = sAppNames + "<li>" + appName + "</li>";
            }
            if (!sAppNames.equals("")) {
                sAppNames = "<ul>" + sAppNames + "</ul>";
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, " Exception while getting App Names", ex);
        }
        return sAppNames;
    }
    
    private Properties getDeviceAndUserProperties(final Long deviceResId) throws Exception {
        final Properties deviceAndUserProperties = new Properties();
        final Criteria resId = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)deviceResId, 0);
        final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        final Join managedDeviceResourceJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, "ManagedDevice", "DeviceResource", 2);
        final Join managedUserToDeviceJoin = new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join managedUserJoin = new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
        final Join userResourceJoin = new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUser", "UserResource", 2);
        final Join extnJoin = new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        selectQuery.addJoin(managedDeviceResourceJoin);
        selectQuery.addJoin(managedUserToDeviceJoin);
        selectQuery.addJoin(managedUserJoin);
        selectQuery.addJoin(userResourceJoin);
        selectQuery.addJoin(extnJoin);
        selectQuery.setCriteria(resId.and(userNotInTrashCriteria));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceResource", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("DeviceResource", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("UserResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("UserResource", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID", "MANAGEDDEVICEEXTN.MANAGED_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME", "MANAGEDDEVICEEXTN.NAME"));
        final DataObject dobj = MDMUtil.getPersistence().get(selectQuery);
        if (!dobj.isEmpty()) {
            ((Hashtable<String, Object>)deviceAndUserProperties).put("deviceName", dobj.getFirstValue("ManagedDeviceExtn", "NAME"));
            ((Hashtable<String, Object>)deviceAndUserProperties).put("customerId", dobj.getFirstValue("DeviceResource", "CUSTOMER_ID"));
            ((Hashtable<String, Object>)deviceAndUserProperties).put("userName", dobj.getFirstValue("UserResource", "NAME"));
            ((Hashtable<String, Object>)deviceAndUserProperties).put("platform", dobj.getFirstValue("ManagedDevice", "PLATFORM_TYPE"));
            ((Hashtable<String, Object>)deviceAndUserProperties).put("emailAddress", dobj.getFirstValue("ManagedUser", "EMAIL_ADDRESS"));
        }
        return deviceAndUserProperties;
    }
    
    private Properties getEmailAdrsForDevice(final Long resourceId) throws Exception {
        String email = "";
        final Criteria resId = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)resourceId, 0);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        final Join userDeviceJoin = new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2);
        final Join managedUserJoin = new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2);
        selectQuery.addJoin(userDeviceJoin);
        selectQuery.addJoin(managedUserJoin);
        selectQuery.setCriteria(resId);
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "*"));
        final DataObject dobj = MDMUtil.getPersistence().get(selectQuery);
        final Properties prop = new Properties();
        if (!dobj.isEmpty()) {
            final Row row = dobj.getFirstRow("ManagedUser");
            email = (String)row.get("EMAIL_ADDRESS");
            ((Hashtable<String, String>)prop).put("EMAIL_ADDRESS", email);
            ((Hashtable<String, Object>)prop).put("MANAGED_USER_ID", row.get("MANAGED_USER_ID"));
        }
        return prop;
    }
    
    protected void setResAppHashMap(final List distributedResList, final Long collectionId, final HashMap<Long, String> resAppHash) {
        for (int i = 0; i < distributedResList.size(); ++i) {
            final Long resId = distributedResList.get(i);
            if (resAppHash.containsKey(resId)) {
                final String appId = resAppHash.get(resId);
                resAppHash.put(resId, appId + "," + collectionId);
            }
            else {
                resAppHash.put(resId, collectionId + "");
            }
        }
    }
    
    protected HashMap<Long, Properties> getAppGroupAndCollnDetailsForResList(final List resourceList) throws SQLException {
        final HashMap<Long, Properties> appGrpToResCollnMap = new HashMap<Long, Properties>();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToResource"));
            sQuery.addJoin(new Join("MdAppCatalogToResource", "MdLicenseToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            sQuery.addJoin(new Join("MdLicenseToAppGroupRel", "MdLicense", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2));
            final Criteria resourceListCri = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
            sQuery.setCriteria(resourceListCri);
            sQuery.addSelectColumn(Column.getColumn("MdAppCatalogToResource", "*"));
            final DataObject resourceLicenseAppsDo = MDMUtil.getPersistence().get(sQuery);
            if (!resourceLicenseAppsDo.isEmpty()) {
                final HashMap<Long, ArrayList> appToResourceListMap = new HashMap<Long, ArrayList>();
                final Iterator<Row> iter = resourceLicenseAppsDo.getRows("MdAppCatalogToResource");
                while (iter.hasNext()) {
                    final Row row = iter.next();
                    final Long appGrp = (Long)row.get("APP_GROUP_ID");
                    final Long resId = (Long)row.get("RESOURCE_ID");
                    if (appToResourceListMap.containsKey(appGrp)) {
                        appToResourceListMap.get(appGrp).add(resId);
                    }
                    else {
                        final ArrayList resList = new ArrayList();
                        resList.add(resId);
                        appToResourceListMap.put(appGrp, resList);
                    }
                }
                final Set<Long> appResSet = appToResourceListMap.keySet();
                if (appResSet != null) {
                    for (final Long appGrp2 : appResSet) {
                        final Long collnId = MDMUtil.getInstance().getProdCollectionIdFromAppGroupId(appGrp2);
                        final Properties p = new Properties();
                        ((Hashtable<String, Long>)p).put("COLLECTION_ID", collnId);
                        ((Hashtable<String, ArrayList>)p).put("RESOURCE_LIST", appToResourceListMap.get(appGrp2));
                        appGrpToResCollnMap.put(appGrp2, p);
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getAppGroupListAndCollectionList {0}", ex);
        }
        return appGrpToResCollnMap;
    }
    
    public List getVPPRemptionAppGroupList(final List resourceList) throws Exception {
        final Criteria cRes = new Criteria(new Column("MdAppLicenseToResources", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        final DataObject DO = MDMUtil.getPersistence().get("MdAppLicenseToResources", cRes);
        final List appGroupList = DBUtil.getColumnValuesAsList(DO.getRows("MdAppLicenseToResources"), "APP_GROUP_ID");
        return appGroupList;
    }
    
    protected void silentInstallApps(final Properties prop, final Long customerId) {
        try {
            final Long collectionID = ((Hashtable<K, Long>)prop).get("collectionId");
            final List resourceList = ((Hashtable<K, List>)prop).get("resListToSilentlyInstallApps");
            this.profileDistributionLog.log(Level.INFO, "Install Application called for: collection ID {0} for {1} ", new Object[] { collectionID, resourceList });
            final Integer platformtype = ((Hashtable<K, Integer>)prop).get("platformtype");
            final List collectionList = new ArrayList();
            collectionList.add(collectionID);
            List finalResourceList = new ArrayList();
            final List noSilentInstallList = new ArrayList(resourceList);
            List appsCollection = new ArrayList();
            int pushNotificationType = 0;
            final int agentID = AppsAutoDeployment.getInstance().getAgentIDFromCollectionID(collectionID);
            final Boolean isNativeAgent = agentID != -1;
            final ManagedDeviceHandler handler = ManagedDeviceHandler.getInstance();
            Properties commandForResProp = new Properties();
            if (platformtype == 3) {
                pushNotificationType = 3;
                commandForResProp = AppsUtil.getInstance().getApplicableAppCommandForResources(collectionID, resourceList);
                appsCollection.add(collectionID);
                finalResourceList = handler.getWindows81AboveManagedDeviceResourceIDs(resourceList);
            }
            else if (platformtype == 2) {
                pushNotificationType = 201;
                appsCollection = AppsUtil.getInstance().getEnterpriseAppsCollection(collectionList);
                if (!appsCollection.isEmpty()) {
                    finalResourceList = handler.getAndroidApkSilentInstallResources(resourceList);
                    if (finalResourceList != null && !finalResourceList.isEmpty()) {
                        ((Hashtable<String, List>)commandForResProp).put("InstallApplication", finalResourceList);
                    }
                    noSilentInstallList.removeAll(finalResourceList);
                    this.updateAppSilentInstallNotPossibleRemarks(platformtype, true, noSilentInstallList, collectionID, customerId);
                }
            }
            else if (platformtype == 1) {
                pushNotificationType = 1;
                final AppsUtil appHandler = new AppsUtil();
                final Properties appsDetails = appHandler.isiOSDeviceApplicableForSilentDistribution(collectionID, resourceList, customerId);
                appsCollection = ((Hashtable<K, List>)appsDetails).get("APPSCOLLECTION");
                finalResourceList = ((Hashtable<K, List>)appsDetails).get("RESOURCELIST");
                if (!appsCollection.isEmpty()) {
                    commandForResProp = AppsUtil.getInstance().getAppleAppCommandForResources(isNativeAgent, commandForResProp, finalResourceList);
                }
            }
            this.profileDistributionLog.log(Level.INFO, "Install Application : applicable collection ID {0} for applicable devices {1}", new Object[] { appsCollection, finalResourceList });
            if (!appsCollection.isEmpty()) {
                final Enumeration enumeration = commandForResProp.keys();
                List commandList = null;
                while (enumeration.hasMoreElements()) {
                    final String applicableCommand = enumeration.nextElement();
                    final List applicableResourceList = ((Hashtable<K, List>)commandForResProp).get(applicableCommand);
                    commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collectionList, applicableCommand);
                    DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, applicableResourceList);
                }
                if (finalResourceList != null && !finalResourceList.isEmpty() && ((Hashtable<K, Boolean>)prop).get("wakeUpDevices")) {
                    NotificationHandler.getInstance().SendNotification(finalResourceList, pushNotificationType);
                    this.profileDistributionLog.log(Level.INFO, "Successfully sent Install Application Notification to {0}", finalResourceList.toString());
                    ((Hashtable<String, List>)prop).put("finalResourceList", finalResourceList);
                    ((Hashtable<String, List>)prop).put("collectionList", collectionList);
                    this.collectionStatusForAutomaticInstall(prop);
                }
            }
        }
        catch (final Exception ex) {
            this.profileDistributionLog.log(Level.SEVERE, "Exception in method silentInstallApps", ex);
        }
    }
    
    private void collectionStatusForAutomaticInstall(final Properties prop) {
        try {
            final List collectionIdList = ((Hashtable<K, List>)prop).get("collectionList");
            final Long appGroupId = ((Hashtable<K, Long>)prop).get("appGroupId");
            final HashMap resToApp = ((Hashtable<K, HashMap>)prop).get("latestVersionAppID");
            final List resourceIdList = ((Hashtable<K, List>)prop).get("finalResourceList");
            final JSONObject updateAppJson = ((Hashtable<K, JSONObject>)prop).get("upgradeAppsjson");
            for (final Long collectionId : collectionIdList) {
                for (final Long resourceID : resourceIdList) {
                    final Long publishingAppId = resToApp.get(resourceID);
                    String remarks = "";
                    if (updateAppJson.optBoolean(resourceID.toString(), false)) {
                        remarks = "dc.db.mdm.apps.status.automatic_update";
                    }
                    else {
                        remarks = "dc.db.mdm.apps.status.automatic_install";
                    }
                    final MDMCollectionStatusUpdate collnUpdater = MDMCollectionStatusUpdate.getInstance();
                    collnUpdater.updateMdmConfigStatus(resourceID, collectionId.toString(), 18, remarks);
                    collnUpdater.updateCollnToResErrorCode(resourceID, collectionId, null);
                    final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
                    handler.updateAppInstallationStatus(resourceID, appGroupId, publishingAppId, 0, remarks, 0);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in collectionStatusForAutomaticInstall", e);
        }
    }
    
    public JSONObject getStoreAppLicenseDetails(final Long appGroupID) {
        final JSONObject licenseData = new JSONObject();
        try {
            final Criteria appGroupCrit = new Criteria(Column.getColumn("MdLicenseToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupID, 0);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("StoreAppsLicenseSummary"));
            final Join licesnseToAppGroupJoin = new Join("StoreAppsLicenseSummary", "MdLicenseToAppGroupRel", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
            sQuery.addJoin(licesnseToAppGroupJoin);
            sQuery.addSelectColumn(Column.getColumn("StoreAppsLicenseSummary", "*"));
            sQuery.setCriteria(appGroupCrit);
            final DataObject licDO = MDMUtil.getPersistence().get(sQuery);
            if (licDO != null && !licDO.isEmpty()) {
                final Row licRow = licDO.getFirstRow("StoreAppsLicenseSummary");
                licenseData.put("PURCHASED_COUNT", licRow.get("PURCHASED_COUNT"));
                licenseData.put("PROVISIONED_COUNT", licRow.get("PROVISIONED_COUNT"));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while getStoreAppLicenseDetails ", ex);
        }
        return licenseData;
    }
    
    public Map getStoreAppLicenseDetailsForCustomer(final Long customerID) {
        final Map<Long, JSONObject> appLicenseData = new HashMap<Long, JSONObject>();
        try {
            final Criteria cutomerCriteria = new Criteria(Column.getColumn("MdLicense", "CUSTOMER_ID"), (Object)customerID, 0);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdLicense"));
            final Join storeLicAppJoin = new Join("MdLicense", "StoreAppsLicenseSummary", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
            final Join licAppRelJoin = new Join("StoreAppsLicenseSummary", "MdLicenseToAppGroupRel", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
            selectQuery.addJoin(storeLicAppJoin);
            selectQuery.addJoin(licAppRelJoin);
            selectQuery.setCriteria(cutomerCriteria);
            selectQuery.addSelectColumn(Column.getColumn("MdLicenseToAppGroupRel", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("StoreAppsLicenseSummary", "PURCHASED_COUNT"));
            selectQuery.addSelectColumn(Column.getColumn("StoreAppsLicenseSummary", "PROVISIONED_COUNT"));
            selectQuery.addSelectColumn(Column.getColumn("MdLicenseToAppGroupRel", "LICENSE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("StoreAppsLicenseSummary", "LICENSE_ID"));
            try {
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                if (dataObject != null && !dataObject.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("StoreAppsLicenseSummary");
                    while (iterator.hasNext()) {
                        final Row row = iterator.next();
                        final JSONObject appLicense = new JSONObject();
                        appLicense.put("PURCHASED_COUNT", (Object)row.get("PURCHASED_COUNT"));
                        appLicense.put("PROVISIONED_COUNT", (Object)row.get("PROVISIONED_COUNT"));
                        final Long groupID = (Long)dataObject.getRow("MdLicenseToAppGroupRel", new Criteria(Column.getColumn("StoreAppsLicenseSummary", "LICENSE_ID"), row.get("LICENSE_ID"), 0)).get("APP_GROUP_ID");
                        appLicenseData.put(groupID, appLicense);
                    }
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception occurred while getStoreAppLicenseDetailsForCustomer", ex);
            }
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Exception occurred while getStoreAppLicenseDetailsForCustomer ", ex2);
        }
        return appLicenseData;
    }
    
    public ArrayList<String> getIOSRedemptionCodeAppsStoreIDs() {
        final ArrayList<String> list = new ArrayList<String>();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppData"));
            final Join licenseRelJoin = new Join("MdPackageToAppData", "MdLicenseToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
            final Join licenseJoin = new Join("MdLicenseToAppGroupRel", "MdLicense", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
            final Criteria redemptionC = new Criteria(Column.getColumn("MdLicense", "LICENSED_TYPE"), (Object)1, 0);
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            sq.addJoin(licenseRelJoin);
            sq.addJoin(licenseJoin);
            sq.setCriteria(redemptionC);
            final DataObject dO = MDMUtil.getPersistence().get(sq);
            if (!dO.isEmpty()) {
                final Iterator<Row> iter = dO.getRows("MdPackageToAppData");
                while (iter.hasNext()) {
                    list.add((String)iter.next().get("STORE_ID"));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "AppLicenseMgmtHandler: Exception while getIOSRedemptionCodeAppsStoreIDs() ", e);
        }
        return list;
    }
    
    public HashMap getAppIDsForResource(final List resourceList, final Long appGroupID, final Long collectionID) throws Exception {
        this.logger.log(Level.INFO, "Inside get AppIds for resourceapp grp id = {0}, collnid = {1}, res List = {2}", new Object[] { appGroupID, collectionID, resourceList });
        final HashMap resourceToAppList = new HashMap();
        final Long latestVersionAppID = MDMUtil.getInstance().getAppIdFromCollectionId(collectionID);
        this.logger.log(Level.INFO, "Obtained collection ID for the app grp {0} , collctionID {1} , resource List {2}", new Object[] { appGroupID, collectionID, resourceList });
        final HashMap deviceDetails = MDMUtil.getInstance().getMDMDeviceProperties(resourceList, Boolean.FALSE);
        this.logger.log(Level.INFO, "Device details fetched");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("AppGroupToCollection"));
        selectQuery.addJoin(new Join("AppGroupToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupID, 0));
        final SortColumn sortColumn = new SortColumn(Column.getColumn("MdPackageToAppData", "SUPPORTED_DEVICES"), true);
        selectQuery.addSortColumn(sortColumn);
        this.logger.log(Level.FINE, "Query constructed ");
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        for (int i = 0; i < resourceList.size(); ++i) {
            final Long resID = Long.valueOf(String.valueOf(resourceList.get(i)));
            this.logger.log(Level.INFO, "Getting app for {0}", resID);
            final HashMap curResDetails = deviceDetails.get(resID);
            final Criteria compatibilityCriteria = AppsUtil.getInstance().getCompatibilityCriteria(curResDetails);
            if (compatibilityCriteria != null && AppsUtil.getInstance().getAppPackageType(appGroupID) != 2) {
                final Row row = dataObject.getRow("MdPackageToAppData", compatibilityCriteria);
                if (row != null) {
                    resourceToAppList.put(resID, row.get("APP_ID"));
                }
                else {
                    this.logger.log(Level.INFO, "App not Compatible for reason CompatablityCriteira'' {0}", resID);
                    resourceToAppList.put(resID, -1L);
                }
            }
            else if (curResDetails.get("PLATFORM_TYPE") == 1) {
                final Row row = dataObject.getRow("MdPackageToAppData");
                final Integer supDevice = (Integer)row.get("SUPPORTED_DEVICES");
                final Integer modelType = MDMUtil.getInstance().getiOSDeivceModelType(resID);
                final int result = supDevice & modelType;
                if (result > 0) {
                    resourceToAppList.put(resID, latestVersionAppID);
                }
                else {
                    this.logger.log(Level.INFO, "App not Compatible for reason Supported device for resourceID:{0}", resID);
                    resourceToAppList.put(resID, -1L);
                }
            }
            else {
                resourceToAppList.put(resID, latestVersionAppID);
            }
        }
        return resourceToAppList;
    }
    
    protected void updateAppSilentInstallNotPossibleRemarks(final Integer platformtype, final boolean isEnterpriseApp, final List resourceList, final Long collectionID, final Long customerId) throws DataAccessException, JSONException, Exception {
        if (!resourceList.isEmpty()) {
            String remarks = "";
            String trackKey = "";
            final int status = 12;
            if (platformtype == 2) {
                if (isEnterpriseApp) {
                    if (GoogleForWorkSettings.isAFWSettingsConfigured(customerId)) {
                        remarks = "mdm.appmgmt.afw.add_private_app@@@<l>$(mdmUrl)/help/app_management/android_app_management.html?andsilentapp#private_enterprise_apps@@@<l>$(mdmUrl)/how-to/mdm-silent-installation-android-apps.html?$(traceurl)&$(did)&src=appRemark#enterprise";
                        trackKey = "afwAppPrivateAppCount";
                    }
                    else {
                        remarks = "mdm.appmgmt.afw_for_enterprise_app_silent_install@@@<l>/webclient#/uems/mdm/manage/appRepo/appMgmt/android/managedGooglePlay@@@<l>$(mdmUrl)/help/app_management/android_app_management.html?andsilentapp#private_enterprise_apps@@@<l>$(mdmUrl)/how-to/mdm-silent-installation-android-apps.html?$(traceurl)&$(did)&src=appRemark#enterprise";
                        trackKey = "afwForSilentCount";
                    }
                }
                else {
                    final String bundleID = (String)AppsUtil.getInstance().getAppDetailsJson(MDMUtil.getInstance().getAppIDFromCollection(collectionID)).get("IDENTIFIER");
                    if (GoogleForWorkSettings.isAFWSettingsConfigured(customerId)) {
                        remarks = "mdm.appmgmt.afw.app_not_approved@@@<l>/webclient#/uems/mdm/manage/appRepo/appMgmt/android/managedGooglePlay";
                        trackKey = "afwAppApprovalNeededCount";
                    }
                    else {
                        remarks = "mdm.appmgmt.afw_for_silent_install@@@<l>/webclient#/uems/mdm/manage/appRepo/appMgmt/android/managedGooglePlay";
                        trackKey = "afwForSilentCount";
                    }
                }
            }
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Android_Module", trackKey);
            if (!remarks.isEmpty()) {
                final HashMap<String, List> remarksToResMap = new HashMap<String, List>();
                remarksToResMap.put(remarks, resourceList);
                final AppInstallationStatusHandler appInstallationStatusHandler = new AppInstallationStatusHandler();
                appInstallationStatusHandler.updateAppStatus(remarksToResMap, collectionID, status);
            }
        }
    }
    
    protected List<Long> removeAlreadyAssigned(final List resourceList, final Long appGroupId, final Long appId) {
        return resourceList;
    }
    
    protected List<Long> distributeApps(final List<Map> appDetailsList, final List<Long> resourceList, final Map<Long, List<Long>> applicableDevice, final Properties properties, final HashMap<Long, Properties> appUpdate) throws Exception {
        return null;
    }
    
    protected String getRemarksForAppDistribution(final int packageType, final int platformType, final Boolean isAppUpgrade, final Boolean isSilentInstall, final String remarksToBeUpdated) {
        if (remarksToBeUpdated.isEmpty()) {
            return ProfileAssociateHandler.getInstance().getInstallAppFromCatalogRemark(packageType, platformType, isAppUpgrade);
        }
        return remarksToBeUpdated;
    }
    
    protected int getAppInstallStatus(final int appInstallStatus) {
        if (appInstallStatus == -1) {
            return 0;
        }
        return appInstallStatus;
    }
    
    protected void addAppCatalogDetails(final int platformtype, final List<ResourceCollectionBean> resourceCollectionBeans, final Map<Long, Long> appMap, final Map<Long, Long> collMap, final Map<Long, Integer> appPackageMap, final int associatedAppSource, final HashMap<Long, Properties> appUpdate, final Boolean isSilentInstall, final int appInstallStatus, final String remarks) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: astore          handler
        //     5: aload_2         /* resourceCollectionBeans */
        //     6: invokeinterface java/util/List.iterator:()Ljava/util/Iterator;
        //    11: astore          12
        //    13: aload           12
        //    15: invokeinterface java/util/Iterator.hasNext:()Z
        //    20: ifeq            1165
        //    23: aload           12
        //    25: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //    30: checkcast       Lcom/adventnet/sym/server/mdm/apps/AppLicenseMgmtHandler$ResourceCollectionBean;
        //    33: astore          resourceCollectionBean
        //    35: aload           resourceCollectionBean
        //    37: invokevirtual   com/adventnet/sym/server/mdm/apps/AppLicenseMgmtHandler$ResourceCollectionBean.getCollnList:()Ljava/util/List;
        //    40: astore          collnList
        //    42: aload           resourceCollectionBean
        //    44: invokevirtual   com/adventnet/sym/server/mdm/apps/AppLicenseMgmtHandler$ResourceCollectionBean.getResourceList:()Ljava/util/List;
        //    47: astore          resList
        //    49: iconst_0       
        //    50: istore          j
        //    52: iload           j
        //    54: aload           collnList
        //    56: invokeinterface java/util/List.size:()I
        //    61: if_icmpge       1162
        //    64: new             Ljava/util/ArrayList;
        //    67: dup            
        //    68: invokespecial   java/util/ArrayList.<init>:()V
        //    71: astore          upgradeList
        //    73: new             Ljava/util/ArrayList;
        //    76: dup            
        //    77: aload           resList
        //    79: invokespecial   java/util/ArrayList.<init>:(Ljava/util/Collection;)V
        //    82: astore          distList
        //    84: aload           collnList
        //    86: iload           j
        //    88: invokeinterface java/util/List.get:(I)Ljava/lang/Object;
        //    93: checkcast       Ljava/lang/Long;
        //    96: astore          collnId
        //    98: aload           collMap
        //   100: aload           collnId
        //   102: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   107: checkcast       Ljava/lang/Long;
        //   110: astore          appGroupId
        //   112: aload_3         /* appMap */
        //   113: aload           appGroupId
        //   115: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   120: checkcast       Ljava/lang/Long;
        //   123: astore          latestVersionAppID
        //   125: aload           appPackageMap
        //   127: aload           appGroupId
        //   129: invokeinterface java/util/Map.get:(Ljava/lang/Object;)Ljava/lang/Object;
        //   134: checkcast       Ljava/lang/Integer;
        //   137: invokevirtual   java/lang/Integer.intValue:()I
        //   140: istore          pkgType
        //   142: new             Lcom/adventnet/ds/query/Criteria;
        //   145: dup            
        //   146: ldc             "MdAppCatalogToResource"
        //   148: ldc             "APP_GROUP_ID"
        //   150: invokestatic    com/adventnet/ds/query/Column.getColumn:(Ljava/lang/String;Ljava/lang/String;)Lcom/adventnet/ds/query/Column;
        //   153: aload           appGroupId
        //   155: iconst_0       
        //   156: invokespecial   com/adventnet/ds/query/Criteria.<init>:(Lcom/adventnet/ds/query/Column;Ljava/lang/Object;I)V
        //   159: astore          appGroupCriteria
        //   161: aload_0         /* this */
        //   162: iload           pkgType
        //   164: iload_1         /* platformtype */
        //   165: iconst_0       
        //   166: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
        //   169: aload           isSilentInstall
        //   171: aload           remarks
        //   173: invokevirtual   com/adventnet/sym/server/mdm/apps/AppLicenseMgmtHandler.getRemarksForAppDistribution:(IILjava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/String;)Ljava/lang/String;
        //   176: astore          remark
        //   178: aload_0         /* this */
        //   179: iload           pkgType
        //   181: iload_1         /* platformtype */
        //   182: iconst_1       
        //   183: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
        //   186: aload           isSilentInstall
        //   188: aload           remarks
        //   190: invokevirtual   com/adventnet/sym/server/mdm/apps/AppLicenseMgmtHandler.getRemarksForAppDistribution:(IILjava/lang/Boolean;Ljava/lang/Boolean;Ljava/lang/String;)Ljava/lang/String;
        //   193: astore          updateRemark
        //   195: aload_0         /* this */
        //   196: iload           appInstallStatus
        //   198: invokevirtual   com/adventnet/sym/server/mdm/apps/AppLicenseMgmtHandler.getAppInstallStatus:(I)I
        //   201: istore          appStatus
        //   203: aload_0         /* this */
        //   204: aload           resList
        //   206: aload           appGroupId
        //   208: iload           pkgType
        //   210: invokevirtual   com/adventnet/sym/server/mdm/apps/AppLicenseMgmtHandler.getKnoxResource:(Ljava/util/List;Ljava/lang/Long;I)Ljava/util/List;
        //   213: astore          resToScope
        //   215: invokestatic    com/adventnet/sym/server/mdm/util/MDMUtil.getInstance:()Lcom/adventnet/sym/server/mdm/util/MDMUtil;
        //   218: aload           resList
        //   220: sipush          500
        //   223: invokevirtual   com/adventnet/sym/server/mdm/util/MDMUtil.splitListIntoSubLists:(Ljava/util/List;I)Ljava/util/List;
        //   226: astore          resSubLists
        //   228: aload           resSubLists
        //   230: invokeinterface java/util/List.iterator:()Ljava/util/Iterator;
        //   235: astore          29
        //   237: aload           29
        //   239: invokeinterface java/util/Iterator.hasNext:()Z
        //   244: ifeq            1115
        //   247: aload           29
        //   249: invokeinterface java/util/Iterator.next:()Ljava/lang/Object;
        //   254: checkcast       Ljava/util/List;
        //   257: astore          resources
        //   259: new             Lcom/adventnet/ds/query/Criteria;
        //   262: dup            
        //   263: new             Lcom/adventnet/ds/query/Column;
        //   266: dup            
        //   267: ldc             "MdAppCatalogToResource"
        //   269: ldc             "RESOURCE_ID"
        //   271: invokespecial   com/adventnet/ds/query/Column.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   274: aload           resources
        //   276: invokeinterface java/util/List.toArray:()[Ljava/lang/Object;
        //   281: bipush          8
        //   283: invokespecial   com/adventnet/ds/query/Criteria.<init>:(Lcom/adventnet/ds/query/Column;Ljava/lang/Object;I)V
        //   286: astore          resourceSublistCriteria
        //   288: new             Lcom/adventnet/ds/query/SelectQueryImpl;
        //   291: dup            
        //   292: new             Lcom/adventnet/ds/query/Table;
        //   295: dup            
        //   296: ldc             "MdAppCatalogToResourceExtn"
        //   298: invokespecial   com/adventnet/ds/query/Table.<init>:(Ljava/lang/String;)V
        //   301: invokespecial   com/adventnet/ds/query/SelectQueryImpl.<init>:(Lcom/adventnet/ds/query/Table;)V
        //   304: astore          sQuery
        //   306: aload           sQuery
        //   308: new             Lcom/adventnet/ds/query/Join;
        //   311: dup            
        //   312: ldc             "MdAppCatalogToResourceExtn"
        //   314: ldc             "MdAppCatalogToResource"
        //   316: iconst_2       
        //   317: anewarray       Ljava/lang/String;
        //   320: dup            
        //   321: iconst_0       
        //   322: ldc             "RESOURCE_ID"
        //   324: aastore        
        //   325: dup            
        //   326: iconst_1       
        //   327: ldc             "APP_GROUP_ID"
        //   329: aastore        
        //   330: iconst_2       
        //   331: anewarray       Ljava/lang/String;
        //   334: dup            
        //   335: iconst_0       
        //   336: ldc             "RESOURCE_ID"
        //   338: aastore        
        //   339: dup            
        //   340: iconst_1       
        //   341: ldc             "APP_GROUP_ID"
        //   343: aastore        
        //   344: iconst_2       
        //   345: invokespecial   com/adventnet/ds/query/Join.<init>:(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;I)V
        //   348: invokeinterface com/adventnet/ds/query/SelectQuery.addJoin:(Lcom/adventnet/ds/query/Join;)V
        //   353: new             Lcom/adventnet/ds/query/Criteria;
        //   356: dup            
        //   357: new             Lcom/adventnet/ds/query/Column;
        //   360: dup            
        //   361: ldc             "MdAppCatalogToResourceExtn"
        //   363: ldc             "APP_GROUP_ID"
        //   365: invokespecial   com/adventnet/ds/query/Column.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   368: aload           appGroupId
        //   370: iconst_0       
        //   371: invokespecial   com/adventnet/ds/query/Criteria.<init>:(Lcom/adventnet/ds/query/Column;Ljava/lang/Object;I)V
        //   374: astore          appGroupcriteria
        //   376: aload           sQuery
        //   378: aload           resourceSublistCriteria
        //   380: aload           appGroupcriteria
        //   382: invokevirtual   com/adventnet/ds/query/Criteria.and:(Lcom/adventnet/ds/query/Criteria;)Lcom/adventnet/ds/query/Criteria;
        //   385: invokeinterface com/adventnet/ds/query/SelectQuery.setCriteria:(Lcom/adventnet/ds/query/Criteria;)V
        //   390: aload           sQuery
        //   392: new             Lcom/adventnet/ds/query/Column;
        //   395: dup            
        //   396: ldc             "MdAppCatalogToResourceExtn"
        //   398: ldc             "*"
        //   400: invokespecial   com/adventnet/ds/query/Column.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   403: invokeinterface com/adventnet/ds/query/SelectQuery.addSelectColumn:(Lcom/adventnet/ds/query/Column;)V
        //   408: invokestatic    com/adventnet/sym/server/mdm/util/MDMUtil.getPersistence:()Lcom/adventnet/persistence/Persistence;
        //   411: aload           sQuery
        //   413: invokeinterface com/adventnet/persistence/Persistence.get:(Lcom/adventnet/ds/query/SelectQuery;)Lcom/adventnet/persistence/DataObject;
        //   418: astore          mdAppCatalogtoResDO
        //   420: invokestatic    com/adventnet/sym/server/mdm/util/MDMUtil.getPersistence:()Lcom/adventnet/persistence/Persistence;
        //   423: ldc             "MdAppCatalogToResource"
        //   425: aload           appGroupCriteria
        //   427: aload           resourceSublistCriteria
        //   429: invokevirtual   com/adventnet/ds/query/Criteria.and:(Lcom/adventnet/ds/query/Criteria;)Lcom/adventnet/ds/query/Criteria;
        //   432: invokeinterface com/adventnet/persistence/Persistence.get:(Ljava/lang/String;Lcom/adventnet/ds/query/Criteria;)Lcom/adventnet/persistence/DataObject;
        //   437: astore          alreadyAssignedDO
        //   439: iconst_0       
        //   440: istore          i
        //   442: iload           i
        //   444: aload           resources
        //   446: invokeinterface java/util/List.size:()I
        //   451: if_icmpge       1090
        //   454: aload           resources
        //   456: iload           i
        //   458: invokeinterface java/util/List.get:(I)Ljava/lang/Object;
        //   463: checkcast       Ljava/lang/Long;
        //   466: astore          resId
        //   468: new             Lcom/adventnet/ds/query/Criteria;
        //   471: dup            
        //   472: new             Lcom/adventnet/ds/query/Column;
        //   475: dup            
        //   476: ldc             "MdAppCatalogToResource"
        //   478: ldc             "RESOURCE_ID"
        //   480: invokespecial   com/adventnet/ds/query/Column.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   483: aload           resId
        //   485: iconst_0       
        //   486: invokespecial   com/adventnet/ds/query/Criteria.<init>:(Lcom/adventnet/ds/query/Column;Ljava/lang/Object;I)V
        //   489: astore          cRes
        //   491: aload           alreadyAssignedDO
        //   493: ldc             "MdAppCatalogToResource"
        //   495: aload           cRes
        //   497: invokeinterface com/adventnet/persistence/DataObject.getRow:(Ljava/lang/String;Lcom/adventnet/ds/query/Criteria;)Lcom/adventnet/persistence/Row;
        //   502: astore          assignRow
        //   504: aload           remark
        //   506: astore          remarksToBeUpdated
        //   508: aload           assignRow
        //   510: ifnull          732
        //   513: aload           assignRow
        //   515: ldc             "INSTALLED_APP_ID"
        //   517: invokevirtual   com/adventnet/persistence/Row.get:(Ljava/lang/String;)Ljava/lang/Object;
        //   520: checkcast       Ljava/lang/Long;
        //   523: astore          installedAppId
        //   525: new             Lcom/adventnet/sym/server/mdm/apps/MDMAppUpdateMgmtHandler;
        //   528: dup            
        //   529: invokespecial   com/adventnet/sym/server/mdm/apps/MDMAppUpdateMgmtHandler.<init>:()V
        //   532: aload           installedAppId
        //   534: aload           latestVersionAppID
        //   536: iload           pkgType
        //   538: iload_1         /* platformtype */
        //   539: invokevirtual   com/adventnet/sym/server/mdm/apps/MDMAppUpdateMgmtHandler.isAppCatalogUpgradeAction:(Ljava/lang/Long;Ljava/lang/Long;II)Z
        //   542: ifeq            569
        //   545: aload           upgradeList
        //   547: aload           resId
        //   549: invokeinterface java/util/List.add:(Ljava/lang/Object;)Z
        //   554: pop            
        //   555: aload           distList
        //   557: aload           resId
        //   559: invokeinterface java/util/List.remove:(Ljava/lang/Object;)Z
        //   564: pop            
        //   565: aload           updateRemark
        //   567: astore          remarksToBeUpdated
        //   569: aload           remarksToBeUpdated
        //   571: invokevirtual   java/lang/String.isEmpty:()Z
        //   574: ifeq            604
        //   577: aload           assignRow
        //   579: ldc             "REMARKS"
        //   581: invokevirtual   com/adventnet/persistence/Row.get:(Ljava/lang/String;)Ljava/lang/Object;
        //   584: checkcast       Ljava/lang/String;
        //   587: astore          remarksToBeUpdated
        //   589: aload           assignRow
        //   591: ldc             "STATUS"
        //   593: invokevirtual   com/adventnet/persistence/Row.get:(Ljava/lang/String;)Ljava/lang/Object;
        //   596: checkcast       Ljava/lang/Integer;
        //   599: invokevirtual   java/lang/Integer.intValue:()I
        //   602: istore          appStatus
        //   604: aload           assignRow
        //   606: ldc             "RESOURCE_ID"
        //   608: aload           resId
        //   610: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   613: aload           assignRow
        //   615: ldc             "APP_GROUP_ID"
        //   617: aload           appGroupId
        //   619: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   622: aload           assignRow
        //   624: ldc             "PUBLISHED_APP_ID"
        //   626: aload           latestVersionAppID
        //   628: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   631: aload           assignRow
        //   633: ldc             "APPROVED_APP_ID"
        //   635: aload           latestVersionAppID
        //   637: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   640: aload           assignRow
        //   642: ldc             "APPROVED_VERSION_STATUS"
        //   644: iconst_3       
        //   645: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   648: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   651: aload           assignRow
        //   653: ldc             "UPDATED_AT"
        //   655: invokestatic    java/lang/System.currentTimeMillis:()J
        //   658: invokestatic    java/lang/Long.valueOf:(J)Ljava/lang/Long;
        //   661: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   664: aload           assignRow
        //   666: ldc             "STATUS"
        //   668: iload           appStatus
        //   670: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   673: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   676: aload           assignRow
        //   678: ldc             "REMARKS"
        //   680: aload           remarksToBeUpdated
        //   682: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   685: invokestatic    com/adventnet/sym/server/mdm/apps/AppsUtil.getInstance:()Lcom/adventnet/sym/server/mdm/apps/AppsUtil;
        //   688: aload           resId
        //   690: aload           appGroupId
        //   692: aload           resToScope
        //   694: ifnull          709
        //   697: aload           resToScope
        //   699: aload           resId
        //   701: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
        //   706: ifne            713
        //   709: iconst_0       
        //   710: goto            714
        //   713: iconst_1       
        //   714: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   717: invokevirtual   com/adventnet/sym/server/mdm/apps/AppsUtil.addOrUpdateAppCatalogScopeRel:(Ljava/lang/Long;Ljava/lang/Long;Ljava/lang/Integer;)V
        //   720: aload           alreadyAssignedDO
        //   722: aload           assignRow
        //   724: invokeinterface com/adventnet/persistence/DataObject.updateRow:(Lcom/adventnet/persistence/Row;)V
        //   729: goto            903
        //   732: new             Lcom/adventnet/persistence/Row;
        //   735: dup            
        //   736: ldc             "MdAppCatalogToResource"
        //   738: invokespecial   com/adventnet/persistence/Row.<init>:(Ljava/lang/String;)V
        //   741: astore          assignRow
        //   743: aload           assignRow
        //   745: ldc             "RESOURCE_ID"
        //   747: aload           resId
        //   749: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   752: aload           assignRow
        //   754: ldc             "APP_GROUP_ID"
        //   756: aload           appGroupId
        //   758: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   761: aload           assignRow
        //   763: ldc             "PUBLISHED_APP_ID"
        //   765: aload           latestVersionAppID
        //   767: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   770: aload           assignRow
        //   772: ldc             "APPROVED_APP_ID"
        //   774: aload           latestVersionAppID
        //   776: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   779: aload           assignRow
        //   781: ldc             "APPROVED_VERSION_STATUS"
        //   783: iconst_3       
        //   784: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   787: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   790: aload           assignRow
        //   792: ldc             "UPDATED_AT"
        //   794: invokestatic    java/lang/System.currentTimeMillis:()J
        //   797: invokestatic    java/lang/Long.valueOf:(J)Ljava/lang/Long;
        //   800: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   803: aload           assignRow
        //   805: ldc             "STATUS"
        //   807: iload           appStatus
        //   809: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   812: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   815: aload           assignRow
        //   817: ldc             "REMARKS"
        //   819: aload           remarksToBeUpdated
        //   821: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   824: new             Lcom/adventnet/persistence/Row;
        //   827: dup            
        //   828: ldc             "MdAppCatalogToResourceScope"
        //   830: invokespecial   com/adventnet/persistence/Row.<init>:(Ljava/lang/String;)V
        //   833: astore          appCatalogScopeRow
        //   835: aload           appCatalogScopeRow
        //   837: ldc             "RESOURCE_ID"
        //   839: aload           resId
        //   841: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   844: aload           appCatalogScopeRow
        //   846: ldc             "APP_GROUP_ID"
        //   848: aload           appGroupId
        //   850: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   853: aload           appCatalogScopeRow
        //   855: ldc             "SCOPE"
        //   857: aload           resToScope
        //   859: ifnull          874
        //   862: aload           resToScope
        //   864: aload           resId
        //   866: invokeinterface java/util/List.contains:(Ljava/lang/Object;)Z
        //   871: ifne            878
        //   874: iconst_0       
        //   875: goto            879
        //   878: iconst_1       
        //   879: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //   882: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   885: aload           alreadyAssignedDO
        //   887: aload           assignRow
        //   889: invokeinterface com/adventnet/persistence/DataObject.addRow:(Lcom/adventnet/persistence/Row;)V
        //   894: aload           alreadyAssignedDO
        //   896: aload           appCatalogScopeRow
        //   898: invokeinterface com/adventnet/persistence/DataObject.addRow:(Lcom/adventnet/persistence/Row;)V
        //   903: aconst_null    
        //   904: astore          mdAppCatalogtoResRow
        //   906: aload           mdAppCatalogtoResDO
        //   908: ifnull          957
        //   911: aload           mdAppCatalogtoResDO
        //   913: invokeinterface com/adventnet/persistence/DataObject.isEmpty:()Z
        //   918: ifne            957
        //   921: new             Lcom/adventnet/ds/query/Criteria;
        //   924: dup            
        //   925: new             Lcom/adventnet/ds/query/Column;
        //   928: dup            
        //   929: ldc             "MdAppCatalogToResourceExtn"
        //   931: ldc             "RESOURCE_ID"
        //   933: invokespecial   com/adventnet/ds/query/Column.<init>:(Ljava/lang/String;Ljava/lang/String;)V
        //   936: aload           resId
        //   938: iconst_0       
        //   939: invokespecial   com/adventnet/ds/query/Criteria.<init>:(Lcom/adventnet/ds/query/Column;Ljava/lang/Object;I)V
        //   942: astore          resCriForAppCatalogExtnTbl
        //   944: aload           mdAppCatalogtoResDO
        //   946: ldc             "MdAppCatalogToResourceExtn"
        //   948: aload           resCriForAppCatalogExtnTbl
        //   950: invokeinterface com/adventnet/persistence/DataObject.getRow:(Ljava/lang/String;Lcom/adventnet/ds/query/Criteria;)Lcom/adventnet/persistence/Row;
        //   955: astore          mdAppCatalogtoResRow
        //   957: aload           mdAppCatalogtoResRow
        //   959: ifnonnull       1026
        //   962: new             Lcom/adventnet/persistence/Row;
        //   965: dup            
        //   966: ldc             "MdAppCatalogToResourceExtn"
        //   968: invokespecial   com/adventnet/persistence/Row.<init>:(Ljava/lang/String;)V
        //   971: astore          mdAppCatalogtoResRow
        //   973: aload           mdAppCatalogtoResRow
        //   975: ldc             "APP_GROUP_ID"
        //   977: aload           appGroupId
        //   979: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   982: aload           mdAppCatalogtoResRow
        //   984: ldc             "RESOURCE_ID"
        //   986: aload           resId
        //   988: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //   991: aload           mdAppCatalogtoResRow
        //   993: ldc             "PUBLISHED_APP_SOURCE"
        //   995: iload           associatedAppSource
        //   997: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //  1000: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //  1003: aload           mdAppCatalogtoResRow
        //  1005: ldc             "IS_UPDATE_AVAILABLE"
        //  1007: iconst_0       
        //  1008: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
        //  1011: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //  1014: aload           mdAppCatalogtoResDO
        //  1016: aload           mdAppCatalogtoResRow
        //  1018: invokeinterface com/adventnet/persistence/DataObject.addRow:(Lcom/adventnet/persistence/Row;)V
        //  1023: goto            1058
        //  1026: aload           mdAppCatalogtoResRow
        //  1028: ldc             "PUBLISHED_APP_SOURCE"
        //  1030: iload           associatedAppSource
        //  1032: invokestatic    java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
        //  1035: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //  1038: aload           mdAppCatalogtoResRow
        //  1040: ldc             "IS_UPDATE_AVAILABLE"
        //  1042: iconst_0       
        //  1043: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
        //  1046: invokevirtual   com/adventnet/persistence/Row.set:(Ljava/lang/String;Ljava/lang/Object;)V
        //  1049: aload           mdAppCatalogtoResDO
        //  1051: aload           mdAppCatalogtoResRow
        //  1053: invokeinterface com/adventnet/persistence/DataObject.updateRow:(Lcom/adventnet/persistence/Row;)V
        //  1058: aload_0         /* this */
        //  1059: getfield        com/adventnet/sym/server/mdm/apps/AppLicenseMgmtHandler.profileDistributionLog:Ljava/util/logging/Logger;
        //  1062: getstatic       java/util/logging/Level.INFO:Ljava/util/logging/Level;
        //  1065: ldc             "[APP] [UPDATE] [AssignAppForDevice] Update avaiable falg cleared for resource  {0} AppGroup : {1}"
        //  1067: iconst_2       
        //  1068: anewarray       Ljava/lang/Object;
        //  1071: dup            
        //  1072: iconst_0       
        //  1073: aload           resId
        //  1075: aastore        
        //  1076: dup            
        //  1077: iconst_1       
        //  1078: aload           appGroupId
        //  1080: aastore        
        //  1081: invokevirtual   java/util/logging/Logger.log:(Ljava/util/logging/Level;Ljava/lang/String;[Ljava/lang/Object;)V
        //  1084: iinc            i, 1
        //  1087: goto            442
        //  1090: invokestatic    com/adventnet/sym/server/mdm/util/MDMUtil.getPersistence:()Lcom/adventnet/persistence/Persistence;
        //  1093: aload           alreadyAssignedDO
        //  1095: invokeinterface com/adventnet/persistence/Persistence.update:(Lcom/adventnet/persistence/DataObject;)Lcom/adventnet/persistence/DataObject;
        //  1100: pop            
        //  1101: invokestatic    com/adventnet/sym/server/mdm/util/MDMUtil.getPersistence:()Lcom/adventnet/persistence/Persistence;
        //  1104: aload           mdAppCatalogtoResDO
        //  1106: invokeinterface com/adventnet/persistence/Persistence.update:(Lcom/adventnet/persistence/DataObject;)Lcom/adventnet/persistence/DataObject;
        //  1111: pop            
        //  1112: goto            237
        //  1115: new             Ljava/util/Properties;
        //  1118: dup            
        //  1119: invokespecial   java/util/Properties.<init>:()V
        //  1122: astore          appProperties
        //  1124: aload           appProperties
        //  1126: ldc_w           "distResList"
        //  1129: aload           distList
        //  1131: invokevirtual   java/util/Properties.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1134: pop            
        //  1135: aload           appProperties
        //  1137: ldc_w           "updateList"
        //  1140: aload           upgradeList
        //  1142: invokevirtual   java/util/Properties.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1145: pop            
        //  1146: aload           appUpdate
        //  1148: aload           collnId
        //  1150: aload           appProperties
        //  1152: invokevirtual   java/util/HashMap.put:(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //  1155: pop            
        //  1156: iinc            j, 1
        //  1159: goto            52
        //  1162: goto            13
        //  1165: goto            1185
        //  1168: astore          e
        //  1170: aload_0         /* this */
        //  1171: getfield        com/adventnet/sym/server/mdm/apps/AppLicenseMgmtHandler.logger:Ljava/util/logging/Logger;
        //  1174: getstatic       java/util/logging/Level.INFO:Ljava/util/logging/Level;
        //  1177: ldc_w           "Exception on app catalog addition"
        //  1180: aload           e
        //  1182: invokevirtual   java/util/logging/Logger.log:(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V
        //  1185: return         
        //    Signature:
        //  (ILjava/util/List<Lcom/adventnet/sym/server/mdm/apps/AppLicenseMgmtHandler$ResourceCollectionBean;>;Ljava/util/Map<Ljava/lang/Long;Ljava/lang/Long;>;Ljava/util/Map<Ljava/lang/Long;Ljava/lang/Long;>;Ljava/util/Map<Ljava/lang/Long;Ljava/lang/Integer;>;ILjava/util/HashMap<Ljava/lang/Long;Ljava/util/Properties;>;Ljava/lang/Boolean;ILjava/lang/String;)V
        //    StackMapTable: 00 17 FD 00 0D 07 02 73 07 02 79 FF 00 26 00 11 07 02 6B 01 07 02 6E 07 02 6F 07 02 6F 07 02 6F 01 07 02 72 07 02 70 01 07 02 75 07 02 73 07 02 79 07 03 64 07 02 6E 07 02 6E 01 00 00 FF 00 B8 00 1E 07 02 6B 01 07 02 6E 07 02 6F 07 02 6F 07 02 6F 01 07 02 72 07 02 70 01 07 02 75 07 02 73 07 02 79 07 03 64 07 02 6E 07 02 6E 01 07 02 6E 07 02 6E 07 02 6D 07 02 6D 07 02 6D 01 07 02 76 07 02 75 07 02 75 01 07 02 6E 07 02 6E 07 02 79 00 00 FF 00 CC 00 25 07 02 6B 01 07 02 6E 07 02 6F 07 02 6F 07 02 6F 01 07 02 72 07 02 70 01 07 02 75 07 02 73 07 02 79 07 03 64 07 02 6E 07 02 6E 01 07 02 6E 07 02 6E 07 02 6D 07 02 6D 07 02 6D 01 07 02 76 07 02 75 07 02 75 01 07 02 6E 07 02 6E 07 02 79 07 02 6E 07 02 76 07 02 8E 07 02 76 07 02 77 07 02 77 01 00 00 FF 00 7E 00 2A 07 02 6B 01 07 02 6E 07 02 6F 07 02 6F 07 02 6F 01 07 02 72 07 02 70 01 07 02 75 07 02 73 07 02 79 07 03 64 07 02 6E 07 02 6E 01 07 02 6E 07 02 6E 07 02 6D 07 02 6D 07 02 6D 01 07 02 76 07 02 75 07 02 75 01 07 02 6E 07 02 6E 07 02 79 07 02 6E 07 02 76 07 02 8E 07 02 76 07 02 77 07 02 77 01 07 02 6D 07 02 76 07 02 7A 07 02 75 07 02 6D 00 00 22 FF 00 68 00 2A 07 02 6B 01 07 02 6E 07 02 6F 07 02 6F 07 02 6F 01 07 02 72 07 02 70 01 07 02 75 07 02 73 07 02 79 07 03 64 07 02 6E 07 02 6E 01 07 02 6E 07 02 6E 07 02 6D 07 02 6D 07 02 6D 01 07 02 76 07 02 75 07 02 75 01 07 02 6E 07 02 6E 07 02 79 07 02 6E 07 02 76 07 02 8E 07 02 76 07 02 77 07 02 77 01 07 02 6D 07 02 76 07 02 7A 07 02 75 07 02 6D 00 03 07 03 65 07 02 6D 07 02 6D FF 00 03 00 2A 07 02 6B 01 07 02 6E 07 02 6F 07 02 6F 07 02 6F 01 07 02 72 07 02 70 01 07 02 75 07 02 73 07 02 79 07 03 64 07 02 6E 07 02 6E 01 07 02 6E 07 02 6E 07 02 6D 07 02 6D 07 02 6D 01 07 02 76 07 02 75 07 02 75 01 07 02 6E 07 02 6E 07 02 79 07 02 6E 07 02 76 07 02 8E 07 02 76 07 02 77 07 02 77 01 07 02 6D 07 02 76 07 02 7A 07 02 75 07 02 6D 00 03 07 03 65 07 02 6D 07 02 6D FF 00 00 00 2A 07 02 6B 01 07 02 6E 07 02 6F 07 02 6F 07 02 6F 01 07 02 72 07 02 70 01 07 02 75 07 02 73 07 02 79 07 03 64 07 02 6E 07 02 6E 01 07 02 6E 07 02 6E 07 02 6D 07 02 6D 07 02 6D 01 07 02 76 07 02 75 07 02 75 01 07 02 6E 07 02 6E 07 02 79 07 02 6E 07 02 76 07 02 8E 07 02 76 07 02 77 07 02 77 01 07 02 6D 07 02 76 07 02 7A 07 02 75 07 02 6D 00 04 07 03 65 07 02 6D 07 02 6D 01 FA 00 11 FF 00 8D 00 2A 07 02 6B 01 07 02 6E 07 02 6F 07 02 6F 07 02 6F 01 07 02 72 07 02 70 01 07 02 75 07 02 73 07 02 79 07 03 64 07 02 6E 07 02 6E 01 07 02 6E 07 02 6E 07 02 6D 07 02 6D 07 02 6D 01 07 02 76 07 02 75 07 02 75 01 07 02 6E 07 02 6E 07 02 79 07 02 6E 07 02 76 07 02 8E 07 02 76 07 02 77 07 02 77 01 07 02 6D 07 02 76 07 02 7A 07 02 75 07 02 7A 00 02 07 02 7A 07 02 75 FF 00 03 00 2A 07 02 6B 01 07 02 6E 07 02 6F 07 02 6F 07 02 6F 01 07 02 72 07 02 70 01 07 02 75 07 02 73 07 02 79 07 03 64 07 02 6E 07 02 6E 01 07 02 6E 07 02 6E 07 02 6D 07 02 6D 07 02 6D 01 07 02 76 07 02 75 07 02 75 01 07 02 6E 07 02 6E 07 02 79 07 02 6E 07 02 76 07 02 8E 07 02 76 07 02 77 07 02 77 01 07 02 6D 07 02 76 07 02 7A 07 02 75 07 02 7A 00 02 07 02 7A 07 02 75 FF 00 00 00 2A 07 02 6B 01 07 02 6E 07 02 6F 07 02 6F 07 02 6F 01 07 02 72 07 02 70 01 07 02 75 07 02 73 07 02 79 07 03 64 07 02 6E 07 02 6E 01 07 02 6E 07 02 6E 07 02 6D 07 02 6D 07 02 6D 01 07 02 76 07 02 75 07 02 75 01 07 02 6E 07 02 6E 07 02 79 07 02 6E 07 02 76 07 02 8E 07 02 76 07 02 77 07 02 77 01 07 02 6D 07 02 76 07 02 7A 07 02 75 07 02 7A 00 03 07 02 7A 07 02 75 01 FA 00 17 FC 00 35 07 02 7A FB 00 44 1F FF 00 1F 00 24 07 02 6B 01 07 02 6E 07 02 6F 07 02 6F 07 02 6F 01 07 02 72 07 02 70 01 07 02 75 07 02 73 07 02 79 07 03 64 07 02 6E 07 02 6E 01 07 02 6E 07 02 6E 07 02 6D 07 02 6D 07 02 6D 01 07 02 76 07 02 75 07 02 75 01 07 02 6E 07 02 6E 07 02 79 07 02 6E 07 02 76 07 02 8E 07 02 76 07 02 77 07 02 77 00 00 FF 00 18 00 1D 07 02 6B 01 07 02 6E 07 02 6F 07 02 6F 07 02 6F 01 07 02 72 07 02 70 01 07 02 75 07 02 73 07 02 79 07 03 64 07 02 6E 07 02 6E 01 07 02 6E 07 02 6E 07 02 6D 07 02 6D 07 02 6D 01 07 02 76 07 02 75 07 02 75 01 07 02 6E 07 02 6E 00 00 FF 00 2E 00 0D 07 02 6B 01 07 02 6E 07 02 6F 07 02 6F 07 02 6F 01 07 02 72 07 02 70 01 07 02 75 07 02 73 07 02 79 00 00 FA 00 02 42 07 03 66 10
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                           
        //  -----  -----  -----  -----  -----------------------------------------------
        //  5      1165   1168   1185   Lcom/adventnet/persistence/DataAccessException;
        // 
        // The error that occurred was:
        // 
        // java.lang.UnsupportedOperationException: The requested operation is not supported.
        //     at com.strobel.util.ContractUtils.unsupported(ContractUtils.java:27)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:284)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:279)
        //     at com.strobel.assembler.metadata.TypeReference.makeGenericType(TypeReference.java:154)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:225)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.CoreMetadataFactory$UnresolvedGenericType.accept(CoreMetadataFactory.java:653)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitMethod(TypeSubstitutionVisitor.java:314)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2611)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:782)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:892)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:593)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:405)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void addOrUpdateStatusInAppCatalogResource(final int platformtype, final List resourceList, final List collectionList, final Map<Long, Long> appMap, final Map<Long, Long> collMap, final Map<Long, Integer> appPackageMap, final int associatedAppSource, final Boolean isSilentInstall, final int appInstallStatus, final String remarks) {
        final ResourceCollectionBean resourceCollectionBean = new ResourceCollectionBean();
        resourceCollectionBean.setCollnList(collectionList);
        resourceCollectionBean.setResourceList(resourceList);
        final List<ResourceCollectionBean> resourceCollectionBeans = new ArrayList<ResourceCollectionBean>();
        resourceCollectionBeans.add(resourceCollectionBean);
        this.addAppCatalogDetails(platformtype, resourceCollectionBeans, appMap, collMap, appPackageMap, associatedAppSource, new HashMap<Long, Properties>(), isSilentInstall, appInstallStatus, remarks);
    }
    
    protected List<Long> getKnoxResource(final List<Long> resourceList, final Long appGroupId, final int pkyType) {
        return null;
    }
    
    static {
        AppLicenseMgmtHandler.notSupportedDeviceList = "notsupportedDevice";
        AppLicenseMgmtHandler.failedResourceList = "failed";
        AppLicenseMgmtHandler.notAdhocRegisteredDeviceList = "notAdhocRegisteredDeviceList";
    }
    
    public class ResourceCollectionBean
    {
        private List<Long> resourceList;
        private List<Long> collnList;
        private List<Long> appGroupList;
        private List<Long> appIdList;
        
        public List<Long> getAppGroupList() {
            return this.appGroupList;
        }
        
        public void setAppGroupList(final List<Long> appGroupList) {
            this.appGroupList = appGroupList;
        }
        
        public List<Long> getAppIdList() {
            return this.appIdList;
        }
        
        public void setAppIdList(final List<Long> appIdList) {
            this.appIdList = appIdList;
        }
        
        public List<Long> getCollnList() {
            return this.collnList;
        }
        
        public void setCollnList(final List<Long> collnList) {
            this.collnList = collnList;
        }
        
        public List<Long> getResourceList() {
            return this.resourceList;
        }
        
        public void setResourceList(final List<Long> resourceList) {
            this.resourceList = resourceList;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final ResourceCollectionBean that = (ResourceCollectionBean)o;
            return Objects.equals(this.resourceList, that.resourceList);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.resourceList, this.collnList);
        }
    }
}
