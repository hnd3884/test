package com.adventnet.sym.server.mdm.apps.android;

import java.util.Hashtable;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import com.me.mdm.server.apps.android.afw.appmgmt.AdvPlayStoreAppDistributionHandler;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.server.apps.android.afw.appmgmt.PlayStoreAppDistributionRequestHandler;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.android.knox.KnoxUtil;
import java.util.Enumeration;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.Iterator;
import java.util.Set;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.apps.AppInstallationStatusHandler;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import java.util.HashMap;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import java.util.Map;
import java.util.List;
import java.util.Properties;
import com.adventnet.sym.server.mdm.apps.AppLicenseMgmtHandler;

public class AndroidAppLicenseMgmtHandler extends AppLicenseMgmtHandler
{
    @Override
    public Properties assignAppForDevices(final Properties prop) {
        final List totalResourceList = ((Hashtable<K, List>)prop).get("resourceList");
        final Map<Long, List<Long>> collnToApplicableResourceList = ((Hashtable<K, Map<Long, List<Long>>>)prop).get("collectionToApplicableResource");
        final List collectionList = ((Hashtable<K, List>)prop).get("collectionList");
        final Long customerId = ((Hashtable<K, Long>)prop).get("customerId");
        final Boolean isSilentInstall = prop.get("isSilentInstall") != null && ((Hashtable<K, Boolean>)prop).get("isSilentInstall");
        final Boolean isNotify = prop.get("isNotify") != null && ((Hashtable<K, Boolean>)prop).get("isNotify");
        final Boolean doNotUninstall = prop.get("doNotUninstall") != null && ((Hashtable<K, Boolean>)prop).get("doNotUninstall");
        final Boolean wakeUpDevices = prop.get("wakeUpDevices") == null || ((Hashtable<K, Boolean>)prop).get("wakeUpDevices");
        Integer associatedAppSource = ((Hashtable<K, Integer>)prop).get("toBeAssociatedAppSource");
        if (associatedAppSource == null) {
            associatedAppSource = MDMCommonConstants.ASSOCIATED_APP_SOURCE_UNKNOWN;
        }
        this.profileDistributionLog.log(Level.INFO, "assignAppForDevices(): resourceList: {0} collectionId: {1}", new Object[] { totalResourceList, collectionList });
        this.profileDistributionLog.log(Level.INFO, "collnToApplicableResourceListMap(): {0}", collnToApplicableResourceList);
        final HashMap<Long, String> resAppHash = new HashMap<Long, String>();
        final HashMap<Long, String> resAppUpgradeHash = new HashMap<Long, String>();
        final ProfileAssociateHandler handler = ProfileAssociateHandler.getInstance();
        final AppInstallationStatusHandler appInstallationStatusHandler = new AppInstallationStatusHandler();
        final Properties notAddedResourceList = new Properties();
        final List<Map> appDetailsList = AppsUtil.getInstance().getAppDetailsListFromCollection(collectionList);
        final Map<Integer, List<Long>> resourceSupportDeviceList = ManagedDeviceHandler.getInstance().getAndroidModelDeviceSplit(totalResourceList);
        final List<Map> allDevicesSupportedApps = appDetailsList.stream().filter(map -> map.get("SUPPORTED_DEVICES") == 1).collect((Collector<? super Object, ?, List<Map>>)Collectors.toList());
        final List<Map> mobileSupportedApps = appDetailsList.stream().filter(map -> map.get("SUPPORTED_DEVICES") == 2).collect((Collector<? super Object, ?, List<Map>>)Collectors.toList());
        final List<Map> tabletSupportedApps = appDetailsList.stream().filter(map -> map.get("SUPPORTED_DEVICES") == 3).collect((Collector<? super Object, ?, List<Map>>)Collectors.toList());
        final Set failedResource = new HashSet();
        final HashMap<Long, Properties> appUpdate = new HashMap<Long, Properties>();
        try {
            final Properties properties = new Properties();
            ((Hashtable<String, Boolean>)properties).put("isSilentInstall", isSilentInstall);
            ((Hashtable<String, Long>)properties).put("customerId", customerId);
            ((Hashtable<String, Integer>)properties).put("associatedAppSource", associatedAppSource);
            ((Hashtable<String, Boolean>)properties).put("wakeUpDevices", wakeUpDevices);
            ((Hashtable<String, Boolean>)properties).put("doNotUninstall", doNotUninstall);
            final Boolean afwAccountReadyHandling = ((Hashtable<K, Boolean>)prop).getOrDefault("AFWAccountReadyHandling", false);
            ((Hashtable<String, Boolean>)properties).put("afwAccountReadyHandling", afwAccountReadyHandling);
            failedResource.addAll(this.distributeApps(allDevicesSupportedApps, totalResourceList, collnToApplicableResourceList, properties, appUpdate));
            failedResource.addAll(this.distributeApps(mobileSupportedApps, resourceSupportDeviceList.getOrDefault(2, new ArrayList<Long>()), collnToApplicableResourceList, properties, appUpdate));
            failedResource.addAll(this.distributeApps(tabletSupportedApps, resourceSupportDeviceList.getOrDefault(3, new ArrayList<Long>()), collnToApplicableResourceList, properties, appUpdate));
            ((Hashtable<String, ArrayList>)notAddedResourceList).put(AndroidAppLicenseMgmtHandler.failedResourceList, new ArrayList(failedResource));
            if (!isSilentInstall) {
                final Map<Long, Integer> collnPackageMap = appDetailsList.stream().collect(Collectors.toMap(map -> map.get("COLLECTION_ID"), map -> map.get("PACKAGE_TYPE")));
                for (final Long collectionId : appUpdate.keySet()) {
                    final Properties appDistributionProps = appUpdate.get(collectionId);
                    final int pkgType = collnPackageMap.get(collectionId);
                    final List distributedResList = ((Hashtable<K, List>)appDistributionProps).get("distResList");
                    final List upgradeList = ((Hashtable<K, List>)appDistributionProps).get("updateList");
                    final String distributeResRemarks = handler.getInstallAppFromCatalogRemark(pkgType, 2, false);
                    final String upgradeListResRemarks = handler.getInstallAppFromCatalogRemark(pkgType, 2, true);
                    final HashMap<String, List> remarksToDisResMap = new HashMap<String, List>();
                    remarksToDisResMap.put(distributeResRemarks, distributedResList);
                    remarksToDisResMap.put(upgradeListResRemarks, upgradeList);
                    appInstallationStatusHandler.updateAppStatus(remarksToDisResMap, collectionId, 12);
                    this.profileDistributionLog.log(Level.INFO, "Yet to apply remarks refilled {0} ; upgrade{1}", new Object[] { distributedResList, upgradeList });
                }
            }
            MDMAppMgmtHandler.getInstance().updateAppRepoRemarksFromCollection(failedResource, collectionList);
            final String notSupSmartRemarks = "dc.mdm.device_mgmt.app_not_supported_for_smartphone";
            final String notSupTabletRemarks = "dc.mdm.device_mgmt.app_not_supported_for_tablets";
            final List<Long> mobileColln = mobileSupportedApps.stream().map(map -> map.get("COLLECTION_ID")).collect((Collector<? super Object, ?, List<Long>>)Collectors.toList());
            final List<Long> tabColln = tabletSupportedApps.stream().map(map -> map.get("COLLECTION_ID")).collect((Collector<? super Object, ?, List<Long>>)Collectors.toList());
            final List tabletDevices = resourceSupportDeviceList.get(3);
            final List mobileDevices = resourceSupportDeviceList.get(2);
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(tabletDevices, mobileColln, 8, notSupSmartRemarks);
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(mobileDevices, tabColln, 8, notSupTabletRemarks);
        }
        catch (final Exception e) {
            this.profileDistributionLog.log(Level.SEVERE, "Exception ", e);
        }
        if (isNotify) {
            try {
                for (final Long collectionId2 : appUpdate.keySet()) {
                    final Properties properties2 = appUpdate.get(collectionId2);
                    final List distributedResList2 = ((Hashtable<K, List>)properties2).get("distResList");
                    final List upgradeList2 = ((Hashtable<K, List>)properties2).get("updateList");
                    if (!distributedResList2.isEmpty()) {
                        this.setResAppHashMap(distributedResList2, collectionId2, resAppHash);
                    }
                    if (!upgradeList2.isEmpty()) {
                        this.setResAppHashMap(upgradeList2, collectionId2, resAppUpgradeHash);
                    }
                }
                final boolean isApplicableForMail = (boolean)DBUtil.getValueFromDB("AndroidAgentSettings", "CUSTOMER_ID", (Object)customerId, "HIDE_MDM_APP");
                if (!isApplicableForMail) {
                    this.sendAppDistributionMail(resAppHash, prop);
                    this.sendAppUpgradationMail(resAppUpgradeHash);
                }
            }
            catch (final Exception ex) {
                this.profileDistributionLog.log(Level.SEVERE, null, ex);
            }
        }
        return notAddedResourceList;
    }
    
    @Override
    protected String getRemarksForAppDistribution(final int packageType, final int platformType, final Boolean isAppUpgrade, final Boolean isSilentInstall, final String remarksToBeUpdated) {
        String remarks;
        if (packageType == 0 && isSilentInstall) {
            remarks = "";
        }
        else {
            remarks = ProfileAssociateHandler.getInstance().getInstallAppFromCatalogRemark(packageType, platformType, isAppUpgrade);
        }
        return remarks;
    }
    
    @Override
    protected List<Long> distributeApps(final List<Map> appDetailsList, final List<Long> resourceList, final Map<Long, List<Long>> applicableDevice, final Properties properties, final HashMap<Long, Properties> appUpdate) throws Exception {
        final boolean isSilentInstall = Boolean.valueOf(((Hashtable<K, Object>)properties).get("isSilentInstall").toString());
        final Long customerId = Long.valueOf(((Hashtable<K, Object>)properties).get("customerId").toString());
        final int associatedAppSource = Integer.valueOf(((Hashtable<K, Object>)properties).get("associatedAppSource").toString());
        final boolean wakeUpDevices = Boolean.valueOf(((Hashtable<K, Object>)properties).get("wakeUpDevices").toString());
        final boolean afwAccountReadyHandling = Boolean.valueOf(((Hashtable<K, Object>)properties).get("afwAccountReadyHandling").toString());
        List<Long> failedList = new ArrayList<Long>();
        if (appDetailsList.isEmpty() || resourceList.isEmpty()) {
            return new ArrayList<Long>();
        }
        final Map<Long, Long> appMap = appDetailsList.stream().collect(Collectors.toMap(map -> map.get("APP_GROUP_ID"), map -> map.get("APP_ID")));
        final Map<Long, Long> collnAppMap = appDetailsList.stream().collect(Collectors.toMap(map -> map.get("COLLECTION_ID"), map -> map.get("APP_GROUP_ID")));
        final Map<Long, Boolean> appAccountMap = appDetailsList.stream().collect(Collectors.toMap(map -> map.get("APP_GROUP_ID"), map -> map.get("IS_PURCHASED_FROM_PORTAL")));
        final Map<Long, Integer> appPackageMap = appDetailsList.stream().collect(Collectors.toMap(map -> map.get("APP_GROUP_ID"), map -> map.get("PACKAGE_TYPE")));
        ((Hashtable<String, Integer>)properties).put("platformType", 2);
        ((Hashtable<String, Boolean>)properties).put("wakeUpDevices", wakeUpDevices);
        ((Hashtable<String, Map<Long, Long>>)properties).put("collnAppMap", collnAppMap);
        ((Hashtable<String, Map<Long, Long>>)properties).put("appMap", appMap);
        final Map<Integer, List<ResourceCollectionBean>> appTypeToList = new HashMap<Integer, List<ResourceCollectionBean>>();
        for (final Long collnId : collnAppMap.keySet()) {
            List<Long> deviceList = null;
            if (applicableDevice != null) {
                deviceList = applicableDevice.get(collnId);
            }
            if (deviceList == null) {
                deviceList = new ArrayList<Long>(resourceList);
            }
            else {
                deviceList.retainAll(resourceList);
            }
            final ResourceCollectionBean resourceCollectionBean = new ResourceCollectionBean();
            final Long appGroupId = collnAppMap.get(collnId);
            final Long appId = appMap.get(appGroupId);
            if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableAndroidDeviceAppPolicy")) {
                resourceCollectionBean.setResourceList(this.removeAlreadyAssigned(deviceList, appGroupId, appId));
            }
            else {
                resourceCollectionBean.setResourceList(deviceList);
            }
            final List<Long> colln = new ArrayList<Long>();
            final List<Long> appGroup = new ArrayList<Long>();
            final List<Long> app = new ArrayList<Long>();
            colln.add(collnId);
            appGroup.add(appGroupId);
            app.add(appId);
            resourceCollectionBean.setCollnList(colln);
            resourceCollectionBean.setAppGroupList(appGroup);
            resourceCollectionBean.setAppIdList(app);
            int type = 2;
            type = (appAccountMap.get(appGroupId) ? 1 : type);
            type = ((appPackageMap.get(appGroupId) == 2) ? 3 : type);
            List<ResourceCollectionBean> resourceCollectionBeanList = appTypeToList.get(type);
            if (resourceCollectionBeanList == null) {
                resourceCollectionBeanList = new ArrayList<ResourceCollectionBean>();
            }
            if (resourceCollectionBeanList.contains(resourceCollectionBean)) {
                final int index = resourceCollectionBeanList.indexOf(resourceCollectionBean);
                resourceCollectionBeanList.get(index).getCollnList().add(collnId);
                resourceCollectionBeanList.get(index).getAppGroupList().add(appGroupId);
                resourceCollectionBeanList.get(index).getAppIdList().add(appId);
            }
            else {
                resourceCollectionBeanList.add(resourceCollectionBean);
            }
            appTypeToList.put(type, resourceCollectionBeanList);
        }
        final List<ResourceCollectionBean> portalApps = appTypeToList.getOrDefault(1, new ArrayList<ResourceCollectionBean>());
        final List<ResourceCollectionBean> nonPortalApps = appTypeToList.getOrDefault(2, new ArrayList<ResourceCollectionBean>());
        final List<ResourceCollectionBean> enterpriseApps = appTypeToList.getOrDefault(3, new ArrayList<ResourceCollectionBean>());
        failedList = this.distributeAppsToDevice(customerId, portalApps, isSilentInstall, afwAccountReadyHandling, associatedAppSource);
        this.addAppCatalogDetails(2, portalApps, appMap, collnAppMap, appPackageMap, associatedAppSource, appUpdate, isSilentInstall, -1, "");
        this.addAppCatalogDetails(2, nonPortalApps, appMap, collnAppMap, appPackageMap, associatedAppSource, appUpdate, isSilentInstall, -1, "");
        this.addAppCatalogDetails(2, enterpriseApps, appMap, collnAppMap, appPackageMap, associatedAppSource, appUpdate, isSilentInstall, -1, "");
        if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableAndroidDeviceAppPolicy")) {
            this.updateProductAvailableSet(customerId, portalApps);
        }
        if (isSilentInstall) {
            for (final ResourceCollectionBean resourceCollectionBean2 : nonPortalApps) {
                for (final Long collectionId : resourceCollectionBean2.getCollnList()) {
                    this.updateAppSilentInstallNotPossibleRemarks(2, false, resourceCollectionBean2.getResourceList(), collectionId, customerId);
                }
            }
            for (final ResourceCollectionBean resourceCollectionBean2 : enterpriseApps) {
                this.silentInstallAndUpdateStatus(properties, resourceCollectionBean2.getCollnList(), resourceCollectionBean2.getResourceList());
            }
        }
        return failedList;
    }
    
    @Override
    protected List<Long> removeAlreadyAssigned(final List resourceList, final Long appGroupId, final Long appId) {
        final List<Long> applicableResourceList = new ArrayList<Long>();
        applicableResourceList.addAll(resourceList);
        final Criteria resCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)resourceList.toArray(), 8);
        final Criteria appGroupCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria publishedAppIdCriteria = new Criteria(new Column("MdAppCatalogToResource", "PUBLISHED_APP_ID"), (Object)appId, 0);
        final Criteria installedAppIdCriteria = new Criteria(new Column("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)appId, 0);
        final Criteria statusCriteria = new Criteria(new Column("MdAppCatalogToResource", "STATUS"), (Object)new int[] { 5, 0 }, 9);
        try {
            final DataObject dataObject = MDMUtil.getPersistence().get("MdAppCatalogToResource", resCriteria.and(appGroupCriteria.and(publishedAppIdCriteria.and(installedAppIdCriteria.and(statusCriteria)))));
            final Iterator<Row> alreadyAssignedRow = dataObject.getRows("MdAppCatalogToResource");
            while (alreadyAssignedRow.hasNext()) {
                applicableResourceList.remove(alreadyAssignedRow.next().get("RESOURCE_ID"));
            }
        }
        catch (final DataAccessException e) {
            this.profileDistributionLog.log(Level.SEVERE, "Exception ", (Throwable)e);
        }
        return applicableResourceList;
    }
    
    private void silentInstallAndUpdateStatus(final Properties properties, final List<Long> collnList, final List<Long> resourceList) {
        final int platformtype = ((Hashtable<K, Integer>)properties).get("platformType");
        final Long customerId = ((Hashtable<K, Long>)properties).get("customerId");
        List finalResourceList = new ArrayList();
        final List noSilentInstallList = new ArrayList(resourceList);
        final Properties commandForResProp = new Properties();
        final ManagedDeviceHandler handler = ManagedDeviceHandler.getInstance();
        try {
            if (!collnList.isEmpty()) {
                finalResourceList = handler.getAndroidApkSilentInstallResources(resourceList);
                if (finalResourceList != null && !finalResourceList.isEmpty()) {
                    ((Hashtable<String, List>)commandForResProp).put("InstallApplication", finalResourceList);
                }
                noSilentInstallList.removeAll(finalResourceList);
                for (final Long collectionID : collnList) {
                    this.updateAppSilentInstallNotPossibleRemarks(platformtype, true, noSilentInstallList, collectionID, customerId);
                }
            }
            if (!collnList.isEmpty()) {
                final Enumeration enumeration = commandForResProp.keys();
                List commandList = null;
                while (enumeration.hasMoreElements()) {
                    final String applicableCommand = enumeration.nextElement();
                    final List applicableResourceList = ((Hashtable<K, List>)commandForResProp).get(applicableCommand);
                    commandList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(collnList, applicableCommand);
                    DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, applicableResourceList);
                }
                if (finalResourceList != null && !finalResourceList.isEmpty() && ((Hashtable<K, Boolean>)properties).get("wakeUpDevices")) {
                    this.logger.log(Level.INFO, "Successfully sent Install Application Notification to {0}", finalResourceList.toString());
                    ((Hashtable<String, List<Long>>)properties).put("collectionList", collnList);
                    ((Hashtable<String, List>)properties).put("finalResourceList", finalResourceList);
                    this.collectionStatusForAutoInstall(properties);
                }
            }
        }
        catch (final Exception e) {
            this.profileDistributionLog.log(Level.SEVERE, "Exception ", e);
        }
    }
    
    private void collectionStatusForAutoInstall(final Properties prop) {
        try {
            final List collectionIdList = ((Hashtable<K, List>)prop).get("collectionList");
            final HashMap appMap = ((Hashtable<K, HashMap>)prop).get("appMap");
            final HashMap collnAppMap = ((Hashtable<K, HashMap>)prop).get("collnAppMap");
            final List resourceIdList = ((Hashtable<K, List>)prop).get("finalResourceList");
            for (final Long collectionId : collectionIdList) {
                for (final Long resourceID : resourceIdList) {
                    final Long appGroupId = collnAppMap.get(collectionId);
                    final Long publishingAppId = appMap.get(appGroupId);
                    final MDMCollectionStatusUpdate collnUpdater = MDMCollectionStatusUpdate.getInstance();
                    collnUpdater.updateMdmConfigStatus(resourceID, collectionId.toString(), 18, "dc.db.mdm.apps.status.automatic_install");
                    collnUpdater.updateCollnToResErrorCode(resourceID, collectionId, null);
                    final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
                    handler.updateAppInstallationStatus(resourceID, appGroupId, publishingAppId, 0, "dc.db.mdm.apps.status.automatic_install", 0);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in collectionStatusForAutoInstall", e);
        }
    }
    
    @Override
    protected List<Long> getKnoxResource(final List<Long> resourceList, final Long appGroupId, final int pkyType) {
        List applicableResources = null;
        if (pkyType != 2) {
            try {
                final List<Long> knoxResources = KnoxUtil.getInstance().getResourcesWithKnoxLicense(resourceList);
                final String identifier = String.valueOf(DBUtil.getValueFromDB("MdAppGroupDetails", "APP_GROUP_ID", (Object)appGroupId, "IDENTIFIER"));
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
                final Criteria resCriteria = new Criteria(new Column("MdAppCatalogToResource", "RESOURCE_ID"), (Object)knoxResources.toArray(), 8);
                final Criteria appCriteria = new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0);
                final Criteria statusCriteria = new Criteria(new Column("MdAppCatalogToResource", "STATUS"), (Object)2, 0);
                sQuery.setCriteria(appCriteria.and(resCriteria).and(statusCriteria));
                sQuery.addSelectColumn(new Column((String)null, "*"));
                final DataObject dO = MDMUtil.getPersistence().get(sQuery);
                if (dO.isEmpty()) {
                    applicableResources = new ArrayList();
                    applicableResources.addAll(knoxResources);
                    for (final Long resourceId : knoxResources) {
                        final Integer knoxVersion = (Integer)DBUtil.getValueFromDB("ManagedKNOXContainer", "RESOURCE_ID", (Object)resourceId, "KNOX_VERSION");
                        if (!identifier.startsWith("sec_container_1.") && knoxVersion == 1) {
                            applicableResources.remove(resourceId);
                        }
                    }
                }
                else {
                    applicableResources = AppsUtil.getInstance().getContainerScopeResources(knoxResources, appGroupId);
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Cannot fetch Knox resources", e);
            }
        }
        return applicableResources;
    }
    
    private void updateProductAvailableSet(final Long customerId, final List<ResourceCollectionBean> portalApps) throws Exception {
        for (final ResourceCollectionBean resourceCollectionBean : portalApps) {
            final PlayStoreAppDistributionRequestHandler dist = new PlayStoreAppDistributionRequestHandler();
            dist.updateAvailableProductSet((ArrayList)resourceCollectionBean.getResourceList(), customerId);
        }
    }
    
    private List<Long> distributeAppsToDevice(final Long customerId, final List<ResourceCollectionBean> portalApps, final boolean isSilentInstall, final boolean afwAccountReadyHandling, final int associatedAppSource) throws Exception {
        final List failedList = new ArrayList();
        for (final ResourceCollectionBean resourceCollectionBean : portalApps) {
            final List resourceList = resourceCollectionBean.getResourceList();
            final List collnList = resourceCollectionBean.getCollnList();
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableAndroidDeviceAppPolicy")) {
                failedList.addAll(this.distributeAppsPolicy(resourceList, collnList, customerId, afwAccountReadyHandling, isSilentInstall, associatedAppSource));
            }
            else {
                failedList.addAll(this.distributeAppswithLegacyPolicy(resourceList, collnList, customerId, afwAccountReadyHandling, isSilentInstall));
            }
        }
        return failedList;
    }
    
    private List distributeAppswithLegacyPolicy(final List<Long> resourceList, final List<Long> collnList, final Long customerId, final boolean afwAccountReadyHandling, final boolean isSilentInstall) {
        final PlayStoreAppDistributionRequestHandler dist = new PlayStoreAppDistributionRequestHandler();
        final List failedList = new ArrayList();
        JSONObject failedJSON = new JSONObject();
        if (isSilentInstall) {
            failedJSON = dist.installAppsByDevices(new ArrayList((Collection<? extends E>)resourceList), new ArrayList((Collection<? extends E>)collnList), customerId, afwAccountReadyHandling);
        }
        else {
            failedJSON = dist.distributeAppsByUsers(new ArrayList((Collection<? extends E>)collnList), new ArrayList((Collection<? extends E>)resourceList), customerId);
        }
        for (final Long collectionId : collnList) {
            final JSONArray failArray = failedJSON.optJSONArray(collectionId.toString());
            if (failArray != null) {
                failedList.addAll(JSONUtil.getInstance().convertJSONArrayTOList(failArray));
            }
        }
        return failedList;
    }
    
    private List distributeAppsPolicy(final List<Long> resourceList, final List<Long> collnList, final Long customerId, final boolean afwAccountReadyHandling, final boolean isSilentInstall, final int associatedAppSource) throws Exception {
        final AdvPlayStoreAppDistributionHandler dist = new AdvPlayStoreAppDistributionHandler();
        dist.initialize(customerId, MDBusinessStoreUtil.getBusinessStoreID(customerId, BusinessStoreSyncConstants.BS_SERVICE_AFW));
        final List failedList = new ArrayList();
        JSONObject failedJSON = new JSONObject();
        final JSONObject distProperties = new JSONObject();
        distProperties.put("isSilentInstall", isSilentInstall);
        distProperties.put("associatedAppSource", associatedAppSource);
        failedJSON = dist.installAppsByDevices(new ArrayList(resourceList), new ArrayList(collnList), distProperties);
        for (final Long collectionId : collnList) {
            final JSONArray failArray = failedJSON.optJSONArray(collectionId.toString());
            if (failArray != null) {
                failedList.addAll(JSONUtil.getInstance().convertJSONArrayTOList(failArray));
            }
        }
        return failedList;
    }
}
