package com.me.mdm.server.apps.android.afw;

import java.util.Hashtable;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Collection;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.deployment.policy.AppDeploymentPolicyImpl;
import com.adventnet.persistence.Row;
import java.util.Map;
import com.adventnet.persistence.DataAccessException;
import java.util.HashMap;
import com.me.mdm.server.apps.android.afw.appmgmt.PlayStoreAppDistributionRequestHandler;
import com.me.mdm.server.apps.android.afw.appmgmt.AdvPlayStoreAppDistributionHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.Properties;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.List;
import com.google.api.services.androidenterprise.model.DeviceState;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.apps.android.afw.usermgmt.GooglePlayDevicesSyncRequestHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.apps.usermgmt.StoreAccountManagementHandler;

public class GoogleAccountChangeHandler
{
    StoreAccountManagementHandler handler;
    public Logger logger;
    public Logger bslogger;
    
    public GoogleAccountChangeHandler() {
        this.handler = new StoreAccountManagementHandler();
        this.logger = Logger.getLogger("MDMLogger");
        this.bslogger = Logger.getLogger("MDMBStoreLogger");
    }
    
    public synchronized void processGoogleAccountChangeInManagedDevice(final Long resourceId, final JSONObject msg) {
        try {
            final JSONArray changeSet = msg.optJSONArray("AccountChanges");
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
            final JSONObject googleESADetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
            final Boolean isAFWConfigured = googleESADetails.optBoolean("isConfigured", false);
            if (isAFWConfigured && changeSet != null) {
                final String managedDomainName = (String)googleESADetails.get("MANAGED_DOMAIN_NAME");
                final Long bsId = googleESADetails.getLong("BUSINESSSTORE_ID");
                for (int i = 0; i < changeSet.length(); ++i) {
                    final JSONObject data = changeSet.getJSONObject(i);
                    this.logger.log(Level.INFO, "GoogleAccountChangeHandler : process Account change: {0}", data);
                    final String accountName = data.optString("AccountName");
                    final String action = String.valueOf(data.get("Action"));
                    if (!accountName.isEmpty()) {
                        final String domainName = accountName.substring(accountName.indexOf("@") + 1);
                        if (domainName.equalsIgnoreCase(managedDomainName)) {
                            Long bsUserId = this.handler.getBSUserIdFromMDMId(bsId, accountName);
                            if (bsUserId == null) {
                                this.logger.log(Level.INFO, "Account not already synced. so syncing now");
                                new GooglePlayDevicesSyncRequestHandler(customerId).syncUser(accountName);
                                bsUserId = this.handler.getBSUserIdFromMDMId(bsId, accountName);
                            }
                            if (bsUserId != null) {
                                if ("Added".equalsIgnoreCase(action)) {
                                    final JSONObject mappingJSON = new JSONObject();
                                    mappingJSON.put("BS_USER_ID", (Object)bsUserId);
                                    mappingJSON.put("MANAGED_DEVICE_ID", (Object)resourceId);
                                    if (this.handler.getStoreAccountUserByDevice(resourceId) == null) {
                                        mappingJSON.put("ACCOUNT_STATUS", 1);
                                    }
                                    else {
                                        mappingJSON.put("ACCOUNT_STATUS", 2);
                                    }
                                    this.handler.addOrUpdateStoreUserToManagedDevice(mappingJSON);
                                    this.handler.addOrUpdateStoreUserToManagedDeviceAccState(mappingJSON, 0);
                                    final String deviceId = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceId, "GOOGLE_PLAY_SERVICE_ID");
                                    this.primaryAccountAdded(resourceId, bsUserId);
                                    this.logger.log(Level.INFO, "GoogleAccountChangeHandler processGoogleAccountChangeInManagedDevice() New Account Added with Email {0} in device ResourceId {1} [{2}] ", new Object[] { accountName, resourceId, deviceId });
                                }
                                else if ("Removed".equalsIgnoreCase(action)) {
                                    final JSONObject mappingJSON = new JSONObject();
                                    mappingJSON.put("BS_USER_ID", (Object)bsUserId);
                                    mappingJSON.put("MANAGED_DEVICE_ID", (Object)resourceId);
                                    mappingJSON.put("ACCOUNT_STATUS", 3);
                                    this.handler.addOrUpdateStoreUserToManagedDevice(mappingJSON);
                                    this.primaryAccountRemoved(resourceId, bsUserId);
                                    this.logger.log(Level.INFO, "GoogleAccountChangeHandler processGoogleAccountChangeInManagedDevice() Account with Email {0} is Removed in device ResourceId {1} ", new Object[] { accountName, resourceId });
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "GoogleAccountChangeHandler processGoogleAccountChangeInManagedDevice() ", e);
        }
    }
    
    public void enableGooglePlay(final Long resourceId, final Long bsUserId) throws Exception {
        final String storeUserId = (String)DBUtil.getValueFromDB("BusinessStoreUsers", "BS_USER_ID", (Object)bsUserId, "BS_STORE_ID");
        if (MDMStringUtils.isEmpty(storeUserId)) {
            this.logger.log(Level.WARNING, "Account added in device is not in synced user list. Check if sync succeded");
        }
        else {
            final DeviceState state = this.setStateEnable(resourceId, storeUserId);
            final JSONObject bsUserToManagedDeviceJSON = new JSONObject();
            bsUserToManagedDeviceJSON.put("MANAGED_DEVICE_ID", (Object)resourceId);
            bsUserToManagedDeviceJSON.put("BS_USER_ID", (Object)bsUserId);
            this.handler.addOrUpdateStoreUserToManagedDeviceAccState(bsUserToManagedDeviceJSON, 1);
            final List resList = new ArrayList();
            resList.add(resourceId);
            DeviceCommandRepository.getInstance().assignCommandToDevices(DeviceCommandRepository.getInstance().addCommand("DeviceApproval"), resList);
            NotificationHandler.getInstance().SendNotification(resList, 2);
            this.logger.log(Level.INFO, "GoogleAccountChangeHandler::enableGooglePlay() Device ResourceId {0}  as  accountState {1}", new Object[] { resourceId, state.getAccountState() });
            this.redistributeAppAssociation(resourceId, CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId), 0, false);
        }
    }
    
    public synchronized void processGooglePlayActivationMsg(final Long resourceId, final JSONObject msg) {
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
        try {
            final String gsfId = String.valueOf(msg.get("GSFID"));
            final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdDeviceInfo");
            uQuery.setUpdateColumn("GOOGLE_PLAY_SERVICE_ID", (Object)gsfId);
            uQuery.setCriteria(new Criteria(Column.getColumn("MdDeviceInfo", "RESOURCE_ID"), (Object)resourceId, 0));
            MDMUtil.getPersistence().update(uQuery);
            if (GoogleForWorkSettings.isAFWSettingsConfigured(customerId)) {
                final String accountName = String.valueOf(msg.get("AccountName"));
                final Long bsUserId = this.handler.getBSUserIdFromMDMId(GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW).getLong("BUSINESSSTORE_ID"), accountName);
                this.enableGooglePlay(resourceId, bsUserId);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "processGooglePlayActivationMsg::Exception while attempting to enable the state of device " + n);
        }
    }
    
    private void primaryAccountAdded(final Long resourceId, final Long bsUserId) throws Exception {
        this.logger.log(Level.INFO, "GoogleAccountChangeHandler primaryAccountAdded() starts");
        final Properties prop = new Properties();
        prop.setProperty("MANAGED_DEVICE_ID", resourceId.toString());
        prop.setProperty("BS_USER_ID", bsUserId.toString());
        GoogleAccountEnableTask.startAccEnableTaskAfterDelay(4, prop);
    }
    
    private void primaryAccountRemoved(final Long resourceId, final Long bsUserId) throws Exception {
        try {
            final String storeUserId = (String)DBUtil.getValueFromDB("BusinessStoreUsers", "BS_USER_ID", (Object)bsUserId, "BS_STORE_ID");
            if (GoogleForWorkSettings.isAFWSettingsConfigured(CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId))) {
                final JSONObject bsUserToManagedDeviceJSON = new JSONObject();
                bsUserToManagedDeviceJSON.put("MANAGED_DEVICE_ID", (Object)resourceId);
                bsUserToManagedDeviceJSON.put("BS_USER_ID", (Object)bsUserId);
                this.handler.addOrUpdateStoreUserToManagedDeviceAccState(bsUserToManagedDeviceJSON, 0);
                final List collectionList = AppsUtil.getInstance().getCollectionForResource(resourceId, -1, null);
                final ArrayList resourceList = new ArrayList();
                resourceList.add(resourceId);
                final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
                final JSONObject playStoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
                final Map<Long, JSONObject> portalAppDetails = AppsUtil.getInstance().getPortalAppDetails(collectionList);
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableAndroidDeviceAppPolicy")) {
                    final AdvPlayStoreAppDistributionHandler dist = new AdvPlayStoreAppDistributionHandler();
                    dist.initialize(customerId, playStoreDetails.getLong("BUSINESSSTORE_ID"));
                    dist.removeAppsByDevices(resourceList, collectionList);
                }
                else {
                    final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(playStoreDetails);
                    final PlayStoreAppDistributionRequestHandler dist2 = new PlayStoreAppDistributionRequestHandler();
                    dist2.disAssociateAppsByUsers(resourceList, portalAppDetails, ebs, playStoreDetails.getLong("BUSINESSSTORE_ID"), null);
                }
                if (playStoreDetails.getInt("ENTERPRISE_TYPE") == GoogleForWorkSettings.ENTERPRISE_TYPE_GOOGLE) {
                    this.setStateDisable(resourceId, storeUserId);
                }
            }
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    public DeviceState setStateEnable(final Long resourceId, final String bsUserId) throws Exception {
        final String deviceId = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceId, "GOOGLE_PLAY_SERVICE_ID");
        if (!MDMStringUtils.isEmpty(deviceId)) {
            final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(GoogleForWorkSettings.getGoogleForWorkSettings(CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId), GoogleForWorkSettings.SERVICE_TYPE_AFW));
            return ebs.setStateEnabled(bsUserId, deviceId);
        }
        this.logger.log(Level.SEVERE, "GSFId not obtained for device {0}, perform sync and readd account in device", resourceId);
        throw new Exception("GSFId not found");
    }
    
    public DeviceState setStateDisable(final Long resourceId, final String bsUserId) throws Exception {
        final String deviceId = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceId, "GOOGLE_PLAY_SERVICE_ID");
        final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(GoogleForWorkSettings.getGoogleForWorkSettings(CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId), GoogleForWorkSettings.SERVICE_TYPE_AFW));
        return ebs.setStateDisabled(bsUserId, deviceId);
    }
    
    public void redistributeAppAssociation(final Long resourceId, final Long customerId, final int appRedistributionDelay, final Boolean afwAccountReadyHandling) {
        try {
            this.logger.log(Level.INFO, "GoogleAccountChangeHandler redistributeAppAssociation() for resource ID {0}", resourceId);
            final Properties defaultAppSettings = AppsUtil.getInstance().getAppSettings(customerId);
            final boolean defaultSilentInstall = ((Hashtable<K, Boolean>)defaultAppSettings).get("isSilentInstall");
            final SelectQuery profileToResourceQuery = this.getPortalAppsAssociatedWithResource(resourceId, 2);
            final DataObject profileToResourceDobj = MDMUtil.getPersistence().get(profileToResourceQuery);
            if (!profileToResourceDobj.isEmpty()) {
                final Iterator<Row> profileIterator = profileToResourceDobj.getRows("RecentProfileForResource");
                final Map<Long, Long> silentInstallProfileCollnMap = new HashMap<Long, Long>();
                final Map<Long, Long> notSilentInstallProfileCollnMap = new HashMap<Long, Long>();
                final List silentInstallCollectionList = new ArrayList();
                final List notSilentInstallCollectionList = new ArrayList();
                final List silentInstallProfileList = new ArrayList();
                final List notSilentInstallProfileList = new ArrayList();
                while (profileIterator.hasNext()) {
                    final Row profileRow = profileIterator.next();
                    final Long profileID = (Long)profileRow.get("PROFILE_ID");
                    final Long collectionID = (Long)profileRow.get("COLLECTION_ID");
                    final JSONObject appDeploymentPolicy = new AppDeploymentPolicyImpl().getEffectiveDeploymentDataPolicy(resourceId, profileID);
                    boolean silentInstall = defaultSilentInstall;
                    if (appDeploymentPolicy != null && appDeploymentPolicy.has("PolicyDetails")) {
                        final JSONObject depConfigData = appDeploymentPolicy.getJSONObject("PolicyDetails");
                        silentInstall = depConfigData.getBoolean("FORCE_APP_INSTALL");
                    }
                    if (silentInstall) {
                        silentInstallProfileCollnMap.put(profileID, collectionID);
                        silentInstallCollectionList.add(collectionID);
                        silentInstallProfileList.add(profileID);
                    }
                    else {
                        notSilentInstallProfileCollnMap.put(profileID, collectionID);
                        notSilentInstallCollectionList.add(collectionID);
                        notSilentInstallProfileList.add(profileID);
                    }
                }
                this.associateAppForNewAccountAdded(silentInstallProfileList, silentInstallCollectionList, resourceId, true, customerId, silentInstallProfileCollnMap, appRedistributionDelay, afwAccountReadyHandling);
                this.associateAppForNewAccountAdded(notSilentInstallProfileList, notSilentInstallCollectionList, resourceId, false, customerId, notSilentInstallProfileCollnMap, 0, afwAccountReadyHandling);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception when redistributing apps on account addition", ex);
        }
    }
    
    private void associateAppForNewAccountAdded(final List profileList, final List collectionList, final long resourceId, final Boolean silentInstall, final Long customerId, final Map<Long, Long> profileCollnMap, final int appRedistributionDelay, final Boolean afwAccountReadyHandling) throws Exception {
        if (!profileList.isEmpty() && !collectionList.isEmpty() && !profileCollnMap.isEmpty()) {
            this.logger.log(Level.INFO, "Distributing profiles:colln {0} to resource {1} with silent install {2}", new Object[] { profileCollnMap, resourceId, silentInstall });
            final String taskName = "ReassignDeviceCommandTask" + resourceId;
            if (appRedistributionDelay == 0) {
                final List resourceList = new ArrayList();
                resourceList.add(resourceId);
                final JSONObject redistributionParams = new JSONObject();
                redistributionParams.put("REDISTRIBUTION_TYPE", 2);
                redistributionParams.put("taskName", (Object)taskName);
                final JSONObject associateAppsToDevicesParams = new JSONObject();
                associateAppsToDevicesParams.put("PROFILE_LIST", (Collection)profileList);
                associateAppsToDevicesParams.put("COLLECTION_LIST", (Collection)collectionList);
                associateAppsToDevicesParams.put("DEVICE_LIST", (Collection)resourceList);
                associateAppsToDevicesParams.put("IS_SILENT_INSTALL", (Object)silentInstall);
                associateAppsToDevicesParams.put("REDISTRIBUTION_PARAMS", (Object)redistributionParams);
                associateAppsToDevicesParams.put("CUSTOMER_ID", (Object)customerId);
                associateAppsToDevicesParams.put("AFW_ACCOUNT_READY_HANDLING", (Object)afwAccountReadyHandling);
                new GoogleApiRetryHandler().associateAfwAppsToDevices(associateAppsToDevicesParams, profileCollnMap);
            }
            else {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("resourceId", resourceId);
                jsonObject.put("customerId", (Object)customerId);
                jsonObject.put("PRESENT_DELAY", appRedistributionDelay);
                jsonObject.put("NEXT_TASK_NAME", (Object)("ReassignDeviceCommandTask" + resourceId));
                final Properties taskProps = new Properties();
                ((Hashtable<String, JSONObject>)taskProps).put("REQUEST_DATA", jsonObject);
                final HashMap taskInfoMap = new HashMap();
                taskInfoMap.put("poolName", "mdmPool");
                taskInfoMap.put("taskName", "ReassignDeviceCommandTask" + resourceId);
                taskInfoMap.put("schedulerTime", System.currentTimeMillis() + appRedistributionDelay * 1000L);
                ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.server.apps.android.afw.AFWAppRedistributionTask", taskInfoMap, taskProps);
            }
        }
    }
    
    public void removeAccountOnUnmanage(final Long resourceId) throws DataAccessException, Exception {
        final String storeUserId = new StoreAccountManagementHandler().getStoreAccountUserByDevice(resourceId);
        final Long bsUserId = (Long)DBUtil.getValueFromDB("BusinessStoreUsers", "BS_STORE_ID", (Object)storeUserId, "BS_USER_ID");
        this.primaryAccountRemoved(resourceId, bsUserId);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("BSUsersToManagedDevices");
        final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("BSUsersToManagedDevices", "MANAGED_DEVICE_ID"), (Object)resourceId, 0);
        updateQuery.setUpdateColumn("ACCOUNT_STATUS", (Object)3);
        updateQuery.setCriteria(managedDeviceCriteria);
        DataAccess.update(updateQuery);
    }
    
    public SelectQuery getPortalAppsAssociatedWithResource(final Long resourceID, final Integer platformType) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("RecentProfileForResource"));
        selectQuery.addJoin(new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0);
        final Criteria profilePlatformTypeCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platformType, 0);
        final Criteria resourceCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria packageCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_TYPE"), (Object)2, 1);
        final Criteria portalApp = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
        final Criteria deleteApps = new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0);
        selectQuery.setCriteria(profilePlatformTypeCriteria.and(profileTypeCriteria).and(resourceCriteria).and(packageCriteria).and(portalApp).and(deleteApps));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
        return selectQuery;
    }
    
    public void processGoogleAccountAvailable(final Long resourceId, final JSONObject msg) {
        try {
            final JSONArray accountsAvailable = new JSONArray((String)msg.opt("AccountsAvailable"));
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
            final JSONObject googleESADetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
            final Boolean isAFWConfigured = googleESADetails.optBoolean("isConfigured", false);
            if (isAFWConfigured && accountsAvailable != null) {
                final String managedDomainName = (String)googleESADetails.get("MANAGED_DOMAIN_NAME");
                final Long bsId = googleESADetails.getLong("BUSINESSSTORE_ID");
                this.bslogger.log(Level.INFO, "No. of Accounts available on the Android device before Gsuite integration: {0}", accountsAvailable.length());
                for (int i = 0; i < accountsAvailable.length(); ++i) {
                    final String accountName = (String)accountsAvailable.get(i);
                    if (!accountName.isEmpty()) {
                        final String domainName = accountName.substring(accountName.indexOf("@") + 1);
                        if (domainName.equalsIgnoreCase(managedDomainName)) {
                            Long bsUserId = this.handler.getBSUserIdFromMDMId(bsId, accountName);
                            if (bsUserId == null) {
                                this.bslogger.log(Level.INFO, "Account not already synced. so syncing now");
                                new GooglePlayDevicesSyncRequestHandler(customerId).syncUser(accountName);
                                bsUserId = this.handler.getBSUserIdFromMDMId(bsId, accountName);
                            }
                            if (bsUserId != null) {
                                final JSONObject mappingJSON = new JSONObject();
                                mappingJSON.put("BS_USER_ID", (Object)bsUserId);
                                mappingJSON.put("MANAGED_DEVICE_ID", (Object)resourceId);
                                if (this.handler.getStoreAccountUserByDevice(resourceId) == null) {
                                    mappingJSON.put("ACCOUNT_STATUS", 1);
                                }
                                else {
                                    mappingJSON.put("ACCOUNT_STATUS", 2);
                                }
                                this.handler.addOrUpdateStoreUserToManagedDevice(mappingJSON);
                                this.handler.addOrUpdateStoreUserToManagedDeviceAccState(mappingJSON, 0);
                                final String deviceId = (String)DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resourceId, "GOOGLE_PLAY_SERVICE_ID");
                                this.primaryAccountAdded(resourceId, bsUserId);
                                this.bslogger.log(Level.INFO, "GoogleAccountChangeHandler processGoogleAccountAvailable() New Account Added with Email {0} in device ResourceId {1} [{2}] ", new Object[] { accountName, resourceId, deviceId });
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.bslogger.log(Level.WARNING, "GoogleAccountChangeHandler processGoogleAccountAvailable() ", e);
        }
    }
}
