package com.me.mdm.server.apps.android.afw.appmgmt;

import com.me.mdm.server.apps.android.afw.AFWAccountStatusHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleManagedAccountHandler;
import com.me.mdm.server.deployment.policy.AppDeploymentPolicyImpl;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.google.api.services.androidenterprise.model.AutoInstallConstraint;
import com.google.api.services.androidenterprise.model.AutoInstallPolicy;
import java.util.Arrays;
import com.google.api.services.androidenterprise.model.ProductPolicy;
import org.json.JSONException;
import com.me.mdm.server.apps.usermgmt.StoreAccountManagementHandler;
import org.json.JSONArray;
import java.util.Iterator;
import java.util.HashMap;
import com.google.api.services.androidenterprise.model.Device;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.apps.tracks.AppTrackUtil;
import com.adventnet.sym.server.mdm.util.MDMCommonConstants;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.persistence.DataAccessException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.me.mdm.server.apps.android.afw.GoogleAPIErrorHandler;
import com.adventnet.sym.server.mdm.apps.AppInstallationStatusHandler;
import java.util.Map;
import java.util.List;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.mdm.server.apps.android.afw.AdvGooglePlayEnterpriseBusinessStore;
import java.util.logging.Logger;

public class AdvPlayStoreAppDistributionHandler
{
    public Logger logger;
    private AdvGooglePlayEnterpriseBusinessStore ebs;
    private Long customerId;
    private Long businessStoreId;
    
    public AdvPlayStoreAppDistributionHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void initialize(final Long customerId, final Long businessStoreId) throws Exception {
        this.customerId = customerId;
        this.businessStoreId = businessStoreId;
        try {
            final JSONObject afwSettings = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
            (this.ebs = new AdvGooglePlayEnterpriseBusinessStore(businessStoreId, customerId)).getCredential(afwSettings);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Cannot initialize EBS", e);
            throw e;
        }
    }
    
    public Boolean checkAndUpdateEnterpriseActiveRemarks(final List<Long> resourceList, final List<Long> collectionList, final Map<Long, Long> appGroupIdsToAppIds, final Map<Long, Long> collMap, final Map<Long, Integer> appPackageMap, final int associatedAppSource) throws DataAccessException {
        Boolean enterpriseActive = Boolean.FALSE;
        final AppInstallationStatusHandler appInstallationStatusHandler = new AppInstallationStatusHandler();
        try {
            this.ebs.isAccountActive();
            enterpriseActive = Boolean.TRUE;
        }
        catch (final TokenResponseException | GoogleJsonResponseException ex) {
            final String errRemarks = GoogleAPIErrorHandler.getResponseErrorKey((Exception)ex);
            appInstallationStatusHandler.updateAppStatus(resourceList, collectionList, appGroupIdsToAppIds, collMap, appPackageMap, 7, errRemarks, associatedAppSource, 2, Boolean.TRUE);
        }
        catch (final Exception e) {
            final String remarks = "mdm.agent.payload.webcontentfilter.error.unknown";
            appInstallationStatusHandler.updateAppStatus(resourceList, collectionList, appGroupIdsToAppIds, collMap, appPackageMap, 7, remarks, associatedAppSource, 2, Boolean.TRUE);
        }
        return enterpriseActive;
    }
    
    public JSONObject installAppsByDevices(final List resourceList, final List collectionList, final JSONObject props) throws Exception {
        final List<List> resourceSubList = MDMUtil.getInstance().splitListIntoSubLists(resourceList, 500);
        final Iterator<List> resourceSubListIterator = (Iterator<List>)resourceSubList.iterator();
        final Map<Long, JSONObject> portalAppDetails = AppsUtil.getInstance().getPortalAppDetails(collectionList);
        final Map<Long, Long> collnToAppGroupIds = new ArrayList<Object>(portalAppDetails.keySet()).stream().collect(Collectors.toMap(data -> data, data -> map.get(data).getLong("APP_GROUP_ID")));
        final Map<Long, Long> appGroupIdsToAppIds = new ArrayList<Object>(portalAppDetails.keySet()).stream().collect(Collectors.toMap(data -> map2.get(data).getLong("APP_GROUP_ID"), data -> map3.get(data).getLong("APP_ID")));
        final Map<Long, Integer> appGroupIdsToPackageMap = new ArrayList<Object>(portalAppDetails.keySet()).stream().collect(Collectors.toMap(data -> map4.get(data).getLong("APP_GROUP_ID"), data -> 0));
        final Integer associatedAppSource = props.optInt("associatedAppSource", (int)MDMCommonConstants.ASSOCIATED_APP_SOURCE_UNKNOWN);
        final Map<Long, String> appTrackMapping = new AppTrackUtil().getLatestTrackForCollection(collectionList);
        final AppInstallationStatusHandler appInstallationStatusHandler = new AppInstallationStatusHandler();
        if (!this.checkAndUpdateEnterpriseActiveRemarks(resourceList, collectionList, appGroupIdsToAppIds, collnToAppGroupIds, appGroupIdsToPackageMap, associatedAppSource)) {
            return new JSONObject();
        }
        final JSONObject playStoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(this.customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        while (resourceSubListIterator.hasNext()) {
            final List totalDeviceList = resourceSubListIterator.next();
            final JSONObject ebsDevicesJSON = this.getEBSDeviceDetails(totalDeviceList);
            JSONArray availableUserJSON = ebsDevicesJSON.optJSONArray("UsersAvailableList");
            final JSONArray unavailableUserJSON = ebsDevicesJSON.optJSONArray("UsersNotAvailableList");
            final List<Long> unavailableResourceList = JSONUtil.getInstance().convertJSONArrayTOList(unavailableUserJSON);
            this.updateFailureStatusForBSUserNotAvailableRes(unavailableResourceList, collectionList, appGroupIdsToAppIds, collnToAppGroupIds, appGroupIdsToPackageMap, associatedAppSource, playStoreDetails);
            List<Long> availableResourceList = new ArrayList<Long>(totalDeviceList);
            availableResourceList.removeAll(unavailableResourceList);
            if (playStoreDetails.get("ENTERPRISE_TYPE").equals(GoogleForWorkSettings.ENTERPRISE_TYPE_EMM)) {
                final JSONObject resultantJSON = this.checkAndUpdateRemarksForAccntNotAdded(availableUserJSON, availableResourceList, collectionList, appGroupIdsToAppIds, collnToAppGroupIds, appGroupIdsToPackageMap, associatedAppSource, playStoreDetails);
                availableUserJSON = resultantJSON.optJSONArray(PlaystoreAppDistributionConstants.AVAILABLE_USERS_JSON);
                availableResourceList = JSONUtil.getInstance().convertJSONArrayTOList(resultantJSON.optJSONArray(PlaystoreAppDistributionConstants.AVAILABLE_USERS_LIST));
            }
            if (!availableResourceList.isEmpty()) {
                final Map<Long, Device> resToDeviceMap = this.ebs.getDeviceDetails(availableUserJSON);
                final List failedDeviceList = new ArrayList(availableResourceList);
                failedDeviceList.removeAll(resToDeviceMap.keySet());
                for (final Long resId : resToDeviceMap.keySet()) {
                    final Device device = resToDeviceMap.get(resId);
                    this.addProducts(device, portalAppDetails, appTrackMapping, props, new HashMap<Long, Integer>());
                }
                availableUserJSON = this.constructDeviceDetails(availableUserJSON, resToDeviceMap.keySet());
                final JSONObject responseJSON = this.ebs.updateDevicePolicy(resToDeviceMap, availableUserJSON);
                final List updateFailedList = new ArrayList(resToDeviceMap.keySet());
                final JSONArray updateDevicePolicySuccessArr = responseJSON.optJSONArray(PlaystoreAppDistributionConstants.SUCCESS_LIST);
                if (updateDevicePolicySuccessArr != null) {
                    final List<Long> updateDevicePolicySuccessList = JSONUtil.getInstance().convertJSONArrayTOList(updateDevicePolicySuccessArr);
                    updateFailedList.removeAll(updateDevicePolicySuccessList);
                    final String remarks = "mdm.appmgmt.afw.install_initiated@@@<l>$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)&$(did)&src=appRemark#playstore";
                    appInstallationStatusHandler.updateAppStatus(updateDevicePolicySuccessList, collectionList, appGroupIdsToAppIds, collnToAppGroupIds, appGroupIdsToPackageMap, 3, remarks, associatedAppSource, 2, Boolean.TRUE);
                }
                failedDeviceList.addAll(updateFailedList);
                if (failedDeviceList.isEmpty()) {
                    continue;
                }
                final String remarks2 = "mdm.appmgmt.afw.no_device_contact_support@@@<l>$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)&$(did)&src=appRemark#playstore";
                appInstallationStatusHandler.updateAppStatus(failedDeviceList, collectionList, appGroupIdsToAppIds, collnToAppGroupIds, appGroupIdsToPackageMap, 7, remarks2, associatedAppSource, 2, Boolean.TRUE);
            }
        }
        return new JSONObject();
    }
    
    public void removeAppsByDevices(final List resourceList, final List collectionList) throws Exception {
        final List<List> resourceSubList = MDMUtil.getInstance().splitListIntoSubLists(resourceList, 500);
        final Iterator<List> resourceSubListIterator = (Iterator<List>)resourceSubList.iterator();
        final Map<Long, JSONObject> portalAppDetails = AppsUtil.getInstance().getPortalAppDetails(collectionList);
        if (!portalAppDetails.isEmpty()) {
            while (resourceSubListIterator.hasNext()) {
                final JSONArray ebsDeviceList = this.getEBSDeviceDetails(resourceSubListIterator.next()).optJSONArray("UsersAvailableList");
                final Map<Long, Device> resToDeviceMap = this.ebs.getDeviceDetails(ebsDeviceList);
                for (final Long resId : resToDeviceMap.keySet()) {
                    final Device device = resToDeviceMap.get(resId);
                    this.removeProducts(device, portalAppDetails);
                }
                final JSONObject appRemovalObject = new JSONObject();
                appRemovalObject.put("deviceDetails", (Object)ebsDeviceList);
                appRemovalObject.put("appList", (Collection)this.getIdentifiersList(new ArrayList<JSONObject>(portalAppDetails.values())));
                this.ebs.removeAppsToDevices(appRemovalObject);
                this.ebs.updateDevicePolicy(resToDeviceMap, ebsDeviceList);
            }
        }
    }
    
    JSONObject getEBSDeviceDetails(final List distributionList) throws JSONException {
        final StoreAccountManagementHandler storeHandler = new StoreAccountManagementHandler();
        final JSONObject deviceJSON = storeHandler.getStoreAccountUserDeviceDetailsByDevices(distributionList);
        return deviceJSON;
    }
    
    void removeProducts(final Device device, final Map<Long, JSONObject> portalAppDetails) {
        final List<ProductPolicy> productPolicyList = device.getPolicy().getProductPolicy();
        final List<ProductPolicy> resultPolicyList = new ArrayList<ProductPolicy>();
        if (!productPolicyList.isEmpty() && !portalAppDetails.isEmpty()) {
            final List<JSONObject> appsToBeRemoved = new ArrayList<JSONObject>(portalAppDetails.values());
            final List<String> identifiers = this.getIdentifiersList(appsToBeRemoved);
            for (int index = 0; index < productPolicyList.size(); ++index) {
                final ProductPolicy productPolicy = productPolicyList.get(index);
                if (!identifiers.contains(productPolicy.getProductId())) {
                    resultPolicyList.add(productPolicy);
                }
            }
        }
        device.getPolicy().setProductAvailabilityPolicy("whitelist");
        device.getPolicy().setProductPolicy((List)resultPolicyList);
        device.getPolicy().setDeviceReportPolicy("deviceReportEnabled");
    }
    
    void addProducts(final Device device, final Map<Long, JSONObject> portalAppDetails, final Map<Long, String> appTrackDetails, final JSONObject props, final Map<Long, Integer> appUpdateMode) {
        final List<ProductPolicy> productPolicyList = device.getPolicy().getProductPolicy();
        final List<ProductPolicy> resultPolicyList = new ArrayList<ProductPolicy>();
        final boolean isSilentInstall = props.optBoolean("isSilentInstall");
        final boolean doNotUninstall = props.optBoolean("doNotUninstall", isSilentInstall);
        final String autoInstallMode = this.getAutoInstallMode(isSilentInstall, doNotUninstall);
        if (productPolicyList != null && !productPolicyList.isEmpty()) {
            final List<JSONObject> appsToBeAdded = new ArrayList<JSONObject>(portalAppDetails.values());
            resultPolicyList.addAll(this.getExclusiveProductPolicy(productPolicyList, this.getIdentifiersList(appsToBeAdded)));
        }
        final List<Long> collnList = new ArrayList<Long>(portalAppDetails.keySet());
        for (int length = collnList.size(), index = 0; index < length; ++index) {
            final Long collnId = collnList.get(index);
            final JSONObject appProps = portalAppDetails.get(collnId);
            final String appID = (String)appProps.get("IDENTIFIER");
            final String productId = "app:".concat(appID);
            final String trackId = appTrackDetails.getOrDefault(collnId, null);
            final List<String> trackIds = (trackId != null) ? Arrays.asList(trackId) : null;
            final int autoUpdate = appUpdateMode.getOrDefault(collnId, 0);
            resultPolicyList.add(this.createProductPolicy(productId, autoInstallMode, trackIds, autoUpdate));
        }
        device.getPolicy().setProductAvailabilityPolicy("whitelist");
        device.getPolicy().setProductPolicy((List)resultPolicyList);
        device.getPolicy().setDeviceReportPolicy("deviceReportEnabled");
    }
    
    private List<String> getIdentifiersList(final List<JSONObject> appList) {
        final List<String> productIdList = new ArrayList<String>();
        for (int index = 0; index < appList.size(); ++index) {
            final JSONObject appdata = appList.get(index);
            final String identifier = (String)appdata.get("IDENTIFIER");
            productIdList.add("app:".concat(identifier));
        }
        return productIdList;
    }
    
    private String getAutoInstallMode(final boolean isSilentInstall, final boolean doNotUninstall) {
        String autoInstallMode = "doNotAutoInstall";
        if (isSilentInstall && doNotUninstall) {
            autoInstallMode = "forceAutoInstall";
        }
        else if (isSilentInstall) {
            autoInstallMode = "autoInstallOnce";
        }
        return autoInstallMode;
    }
    
    private void setAutoInstallPolicy(final ProductPolicy productPolicy, final String autoInstallMode) {
        final AutoInstallPolicy autoInstallPolicy = new AutoInstallPolicy();
        autoInstallPolicy.setAutoInstallMode(autoInstallMode);
        this.setAutoInstallConstraint(autoInstallPolicy);
        productPolicy.setAutoInstallPolicy(autoInstallPolicy);
    }
    
    private void setAutoInstallConstraint(final AutoInstallPolicy autoInstallPolicy) {
        final AutoInstallConstraint autoInstallConstraint = new AutoInstallConstraint();
        autoInstallConstraint.setChargingStateConstraint("chargingNotRequired");
        autoInstallConstraint.setDeviceIdleStateConstraint("deviceIdleNotRequired");
        autoInstallConstraint.setNetworkTypeConstraint("unmeteredNetwork");
        autoInstallPolicy.setAutoInstallConstraint((List)Arrays.asList(autoInstallConstraint));
    }
    
    private ProductPolicy createProductPolicy(final String identifier, final String autoInstallMode, final List<String> trackIds, final int autoUpdateMode) {
        final ProductPolicy productPolicy = new ProductPolicy();
        productPolicy.setProductId(identifier);
        if (trackIds != null && !trackIds.isEmpty()) {
            productPolicy.setTrackIds((List)trackIds);
        }
        this.setAutoUpdateMode(productPolicy, autoUpdateMode);
        this.setAutoInstallPolicy(productPolicy, autoInstallMode);
        return productPolicy;
    }
    
    private List<ProductPolicy> getExclusiveProductPolicy(final List<ProductPolicy> productPolicyList, final List<String> identifiers) {
        final List<ProductPolicy> resultProductPolicy = new ArrayList<ProductPolicy>();
        for (final ProductPolicy productPolicy : productPolicyList) {
            final String existingBundleId = productPolicy.getProductId();
            if (!identifiers.contains(existingBundleId)) {
                resultProductPolicy.add(productPolicy);
            }
        }
        return resultProductPolicy;
    }
    
    public void migrateToDevicePolicy(final List resourceList, final Long customerId) throws Exception {
        final List<List> resourceSubList = MDMUtil.getInstance().splitListIntoSubLists(resourceList, 500);
        final Iterator<List> resourceSubListIterator = (Iterator<List>)resourceSubList.iterator();
        final Map<Long, JSONObject> profileToApp = AppsUtil.getInstance().getProfileToAppDetailsForCustomer(customerId, 2, true);
        final List<Long> profileList = new ArrayList<Long>(profileToApp.keySet());
        while (resourceSubListIterator.hasNext()) {
            final JSONArray ebsDeviceList = this.getEBSDeviceDetails(resourceSubListIterator.next()).optJSONArray("UsersAvailableList");
            final Map<Long, Device> resToDeviceMap = this.ebs.getDeviceDetails(ebsDeviceList);
            final List<Long> currDeviceList = new ArrayList<Long>(resToDeviceMap.keySet());
            final Map<Long, List<Long>> profileToDevices = ProfileUtil.getInstance().getProfileToDevices(currDeviceList, profileList);
            final List<Long> currProfileList = new ArrayList<Long>(profileToDevices.keySet());
            for (final Long profileId : currProfileList) {
                final JSONObject appObject = profileToApp.get(profileId);
                final String identifier = "app:".concat((String)appObject.get("IDENTIFIER"));
                final List<Long> silentDeployment = new AppDeploymentPolicyImpl().getSilentInstallDeployedResources(profileToDevices.get(profileId), profileId, null);
                final List<Long> totalDevices = new ArrayList<Long>(profileToDevices.get(profileId));
                for (final Long resID : totalDevices) {
                    final Device device = resToDeviceMap.get(resID);
                    final List<ProductPolicy> productPolicyList = device.getPolicy().getProductPolicy();
                    if (productPolicyList == null) {
                        device.getPolicy().setProductPolicy((List)new ArrayList());
                    }
                    final String autoInstallMode = this.getAutoInstallMode(silentDeployment.contains(resID), silentDeployment.contains(resID));
                    device.getPolicy().getProductPolicy().add(this.createProductPolicy(identifier, autoInstallMode, null, 0));
                    device.getPolicy().setProductAvailabilityPolicy("whitelist");
                }
            }
            this.ebs.updateDevicePolicy(resToDeviceMap, ebsDeviceList);
        }
    }
    
    void setAutoUpdateMode(final ProductPolicy productPolicy, final int autoUpdate) {
        switch (autoUpdate) {
            case 1: {
                productPolicy.setAutoUpdateMode("autoUpdateHighPriority");
                break;
            }
            case 2: {
                productPolicy.setAutoUpdateMode("autoUpdatePostponed");
                break;
            }
            default: {
                productPolicy.setAutoUpdateMode("autoUpdateDefault");
                break;
            }
        }
    }
    
    private JSONArray constructDeviceDetails(final JSONArray ebsDeviceDetailsList, final Collection applicableResourceList) {
        final JSONArray deviceDetailsJSONArray = new JSONArray();
        for (int i = 0; i < ebsDeviceDetailsList.length(); ++i) {
            final JSONObject ebsDeviceDetails = (JSONObject)ebsDeviceDetailsList.get(i);
            final Long resId = ebsDeviceDetails.getLong("MANAGED_DEVICE_ID");
            if (applicableResourceList.contains(resId)) {
                final JSONObject deviceDetailsJSON = new JSONObject();
                deviceDetailsJSON.put("MANAGED_DEVICE_ID", (Object)resId);
                deviceDetailsJSON.put("BS_STORE_ID", (Object)ebsDeviceDetails.get("BS_STORE_ID"));
                deviceDetailsJSON.put("GOOGLE_PLAY_SERVICE_ID", (Object)ebsDeviceDetails.get("GOOGLE_PLAY_SERVICE_ID"));
                deviceDetailsJSONArray.put((Object)deviceDetailsJSON);
            }
        }
        return deviceDetailsJSONArray;
    }
    
    private void updateFailureStatusForBSUserNotAvailableRes(final List<Long> userNotAvailableResourceList, final List<Long> collectionList, final Map<Long, Long> appGroupIdsToAppIds, final Map<Long, Long> collMap, final Map<Long, Integer> appPackageMap, final int associatedAppSource, final JSONObject playStoreDetails) throws Exception {
        if (userNotAvailableResourceList != null && !userNotAvailableResourceList.isEmpty() && collectionList != null && !collectionList.isEmpty()) {
            List<Long> accNotAvailableResList = new ArrayList<Long>();
            List<Long> agentToBeUpgradedList = new ArrayList<Long>();
            List<Long> toBeDeviceOwnerList = new ArrayList<Long>();
            final Map<Integer, List<Long>> afwCompatibilityStatusToResourceList = new GoogleManagedAccountHandler().isAndWhyDevicesNotAFWCompatible(userNotAvailableResourceList);
            if (afwCompatibilityStatusToResourceList.get(GoogleManagedAccountHandler.AFW_COMPATIBLE_DEVICE) != null) {
                accNotAvailableResList = afwCompatibilityStatusToResourceList.get(GoogleManagedAccountHandler.AFW_COMPATIBLE_DEVICE);
            }
            if (afwCompatibilityStatusToResourceList.get(GoogleManagedAccountHandler.AFW_COMPATIBLE_AGENT_UPGRADE_NEEDED) != null) {
                agentToBeUpgradedList = afwCompatibilityStatusToResourceList.get(GoogleManagedAccountHandler.AFW_COMPATIBLE_AGENT_UPGRADE_NEEDED);
            }
            if (afwCompatibilityStatusToResourceList.get(GoogleManagedAccountHandler.AFW_COMPATIBLE_DIFFERENT_ENROLLMENT_NEEDED) != null) {
                toBeDeviceOwnerList = afwCompatibilityStatusToResourceList.get(GoogleManagedAccountHandler.AFW_COMPATIBLE_DIFFERENT_ENROLLMENT_NEEDED);
            }
            final int enterpriseType = (int)playStoreDetails.get("ENTERPRISE_TYPE");
            final int status = 12;
            final AppInstallationStatusHandler appInstallationStatusHandler = new AppInstallationStatusHandler();
            if (!agentToBeUpgradedList.isEmpty()) {
                final String remarks = "mdm.appmgmt.afw.agent_upgrade_for_install";
                appInstallationStatusHandler.updateAppStatus(agentToBeUpgradedList, collectionList, appGroupIdsToAppIds, collMap, appPackageMap, status, remarks, associatedAppSource, 2, Boolean.TRUE);
                this.logger.log(Level.INFO, "Need agent upgrade for app distribution {0}", agentToBeUpgradedList);
                MEMDMTrackParamManager.getInstance().incrementTrackValue(this.customerId, "Android_Module", "afwAppUpgradeNeeded");
            }
            if (!toBeDeviceOwnerList.isEmpty()) {
                final String remarks = "mdm.appmgmt.afw.device_owner_for_install@@@<l>$(mdmUrl)/help/android_for_work/mdm_android_for_work_introduction.html?$(traceurl)&$(did)&src=appRemark#Device_Owner";
                appInstallationStatusHandler.updateAppStatus(toBeDeviceOwnerList, collectionList, appGroupIdsToAppIds, collMap, appPackageMap, status, remarks, associatedAppSource, 2, Boolean.TRUE);
                this.logger.log(Level.INFO, "Need to enroll as device owner for app distribution {0}", toBeDeviceOwnerList);
                MEMDMTrackParamManager.getInstance().incrementTrackValue(this.customerId, "Android_Module", "afwEnrollAsSupervised");
            }
            if (!accNotAvailableResList.isEmpty()) {
                String remarks;
                String trackKey;
                if (enterpriseType == GoogleForWorkSettings.ENTERPRISE_TYPE_GOOGLE) {
                    final String domainName = String.valueOf(playStoreDetails.get("MANAGED_DOMAIN_NAME"));
                    remarks = "mdm.appmgmt.afw.add_gsuite_account@@@" + domainName + "@@@<l>" + "$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)&$(did)&src=appRemark#gsuite" + "@@@<l>" + "$(mdmUrl)/help/android_for_work/mdm_afw_prerequisites.html?$(traceurl)&$(did)&src=appRemark#using_google_account";
                    trackKey = "afwGSuiteUserAccNeeded";
                }
                else {
                    for (final Long resourceID : accNotAvailableResList) {
                        final String udid = (String)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "UDID");
                        new GoogleManagedAccountHandler().addAFWAccountAdditionCmd(resourceID, udid, this.customerId);
                    }
                    remarks = "mdm.appmgmt.afw.account_addition_initiated@@@<l>$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)&$(did)&src=appRemark#without_gsuite";
                    trackKey = "afwInitMissedAccAdd";
                }
                appInstallationStatusHandler.updateAppStatus(accNotAvailableResList, collectionList, appGroupIdsToAppIds, collMap, appPackageMap, status, remarks, associatedAppSource, 2, Boolean.TRUE);
                this.logger.log(Level.INFO, "Account not available. Initiated if afw2 {0}", accNotAvailableResList);
                MEMDMTrackParamManager.getInstance().incrementTrackValue(this.customerId, "Android_Module", trackKey);
            }
        }
    }
    
    public JSONObject checkAndUpdateRemarksForAccntNotAdded(JSONArray availableUsersJSON, List<Long> availableResourceList, final List<Long> collectionIds, final Map<Long, Long> appGroupIdsToAppIds, final Map<Long, Long> collMap, final Map<Long, Integer> appPackageMap, final int associatedAppSource, final JSONObject playStoreDetails) throws Exception {
        final JSONObject resultantJSON = new JSONObject();
        if (playStoreDetails.get("ENTERPRISE_TYPE").equals(GoogleForWorkSettings.ENTERPRISE_TYPE_EMM)) {
            final JSONObject responseJSON = new AFWAccountStatusHandler().getAccountStatusDetails(availableUsersJSON);
            this.logger.log(Level.INFO, "Managed account user status :{0}", responseJSON);
            this.updateRemarksForAccountNotAdded(responseJSON.getJSONArray("AccountNotAddedList"), collectionIds, appGroupIdsToAppIds, collMap, appPackageMap, associatedAppSource);
            if (responseJSON.optJSONArray("AccountAddedList") != null) {
                availableUsersJSON = responseJSON.getJSONArray("AccountAddedList");
                availableResourceList = JSONUtil.getInstance().convertJSONArrayTOList(responseJSON.getJSONArray("AccountAddedResourceList"));
                resultantJSON.put(PlaystoreAppDistributionConstants.AVAILABLE_USERS_JSON, (Object)availableUsersJSON);
                resultantJSON.put(PlaystoreAppDistributionConstants.AVAILABLE_USERS_LIST, (Collection)availableResourceList);
            }
        }
        return resultantJSON;
    }
    
    private void updateRemarksForAccountNotAdded(final JSONArray accountNotAddedList, final List<Long> collectionIds, final Map<Long, Long> appGroupIdsToAppIds, final Map<Long, Long> collMap, final Map<Long, Integer> appPackageMap, final int associatedAppSource) throws Exception {
        final int totalRes = accountNotAddedList.length();
        final List<Long> accInitiatedList = new ArrayList<Long>();
        final List<Long> accountAdditionFailedList = new ArrayList<Long>();
        final AppInstallationStatusHandler appInstallationStatusHandler = new AppInstallationStatusHandler();
        for (int i = 0; i < totalRes; ++i) {
            final JSONObject failureDetails = accountNotAddedList.getJSONObject(i);
            final long resID = failureDetails.getLong("resourceID");
            final int status = failureDetails.getInt("status");
            switch (status) {
                case 1:
                case 4: {
                    accInitiatedList.add(resID);
                    break;
                }
                case 3: {
                    accountAdditionFailedList.add(resID);
                    break;
                }
            }
        }
        if (!accountAdditionFailedList.isEmpty()) {
            final String remarks = "mdm.appmgmt.afw.account_failed";
            appInstallationStatusHandler.updateAppStatus(accountAdditionFailedList, collectionIds, appGroupIdsToAppIds, collMap, appPackageMap, 7, remarks, associatedAppSource, 2, Boolean.TRUE);
            this.logger.log(Level.INFO, "Account addition failed so apps cannot be distributed {0}", accInitiatedList);
            MEMDMTrackParamManager.getInstance().incrementTrackValue(this.customerId, "Android_Module", "afwAccountFailed");
        }
        if (!accInitiatedList.isEmpty()) {
            final String remarks = "mdm.appmgmt.afw.account_addition_initiated@@@<l>$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)&$(did)&src=appRemark#without_gsuite";
            appInstallationStatusHandler.updateAppStatus(accInitiatedList, collectionIds, appGroupIdsToAppIds, collMap, appPackageMap, 12, remarks, associatedAppSource, 2, Boolean.TRUE);
            this.logger.log(Level.INFO, "Account addition is initiated for app distribution {0}", accInitiatedList);
            MEMDMTrackParamManager.getInstance().incrementTrackValue(this.customerId, "Android_Module", "afwWaitingForAccAdd");
        }
    }
}
