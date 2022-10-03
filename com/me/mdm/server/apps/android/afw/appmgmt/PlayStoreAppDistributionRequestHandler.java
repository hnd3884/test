package com.me.mdm.server.apps.android.afw.appmgmt;

import com.me.mdm.server.apps.android.afw.usermgmt.GooglePlayDevicesSyncRequestHandler;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.apps.android.afw.AFWAccountErrorHandler;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleManagedAccountHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.apps.FilterDevices;
import com.adventnet.sym.server.mdm.apps.FilterUsers;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.HashMap;
import java.util.Map;
import com.me.mdm.server.apps.usermgmt.StoreAccountManagementHandler;
import com.me.mdm.server.apps.android.afw.AFWAccountStatusHandler;
import org.json.JSONArray;
import com.me.mdm.server.apps.android.afw.GoogleApiRetryHandler;
import java.util.HashSet;
import java.util.List;
import com.adventnet.sym.server.mdm.apps.AppInstallationStatusHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Collection;
import com.me.mdm.server.apps.android.afw.GooglePlayEnterpriseBusinessStore;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.logging.Logger;

public class PlayStoreAppDistributionRequestHandler
{
    public Logger logger;
    public static final String AFW_ACCREADY_APP_REDISTRIBUTION_DELAY_TIME = "AFWAccAppRedistributionDelayTime";
    public static final int NO_DELAY = 0;
    private static final String APP_PERMISSION_NOT_ACCEPTED = "app permissions haven't been accepted";
    private static final String NO_DEVICE_FOUND = "No device was found";
    private static final String NO_ASSOCIATED_ENTERPRISE = "not associated with any enrolled enterprise";
    private static final String NO_ENTITLEMENT_FOUND = "No entitlement was found";
    private static final String AFW_TOS_NOT_ACCEPTED = "admin of the enterprise has not accepted the Managed Google Play Terms of Service";
    
    public PlayStoreAppDistributionRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public JSONObject distributeAppsByUsers(final ArrayList collectionList, final ArrayList resourceList, final Long customerId) {
        GooglePlayEnterpriseBusinessStore ebs = null;
        final JSONObject failedJSON = new JSONObject();
        try {
            final JSONObject playStoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
            ebs = new GooglePlayEnterpriseBusinessStore(playStoreDetails);
            for (int j = 0; j < collectionList.size(); ++j) {
                final Long collectionId = collectionList.get(j);
                try {
                    final List successList = this.assignEntitlement(new ArrayList(resourceList), collectionId, playStoreDetails);
                    final List failedResList = new ArrayList(resourceList);
                    failedResList.removeAll(successList);
                    failedJSON.put(collectionId.toString(), (Object)JSONUtil.getInstance().convertListToJSONArray(failedResList));
                    this.logger.log(Level.INFO, "distributeAppsByUsers:failedResList: {0}", failedResList);
                }
                catch (final Exception exp) {
                    this.logger.log(Level.SEVERE, exp, () -> "Exception when distributeAppsByUsers for collection Id " + n + " for resources " + list);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Exception while initialising distributeAppsByUsers for collections " + list2 + " for resources " + list3);
            for (int j = 0; j < collectionList.size(); ++j) {
                try {
                    final Long collectionId = collectionList.get(j);
                    failedJSON.put(collectionId.toString(), (Collection)resourceList);
                    final String remarks = "mdm.agent.payload.webcontentfilter.error.unknown";
                    final AppInstallationStatusHandler appInstallationStatusHandler = new AppInstallationStatusHandler();
                    appInstallationStatusHandler.updateAppStatus(this.remarksToResListMap(remarks, resourceList), collectionId, 7);
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, "Exception while populating failed list", e);
                }
            }
        }
        return failedJSON;
    }
    
    public JSONObject installAppsByDevices(final ArrayList resourceList, final ArrayList collectionList, final Long customerId, final Boolean afwAccountReadyHandling) {
        final JSONObject failedJSON = new JSONObject();
        final JSONObject failedJSONDetails = new JSONObject();
        try {
            final JSONObject playStoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
            final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(playStoreDetails);
            final AppInstallationStatusHandler appInstallationStatusHandler = new AppInstallationStatusHandler();
            HashSet<Long> managedAccountYetToRecogDevices = new HashSet<Long>();
            final Boolean isDelayRedistributionAllowed = new GoogleApiRetryHandler().validateIfDelayRedistributionNeeded(afwAccountReadyHandling, customerId);
            final JSONObject extraParamsForStatusUpdate = new JSONObject();
            extraParamsForStatusUpdate.put("is_afwAccountReadyHandling", (Object)afwAccountReadyHandling);
            failedJSONDetails.put("DoNotProceedWithFurtherCollection", false);
            for (int j = 0; j < collectionList.size(); ++j) {
                final Long collectionId = collectionList.get(j);
                try {
                    final JSONObject appDetailsJSON = this.getPortalAppDetails(collectionId);
                    final List successList = this.assignEntitlement(new ArrayList(resourceList), collectionId, playStoreDetails);
                    final List failedEntitlement = new ArrayList(resourceList);
                    failedEntitlement.removeAll(successList);
                    final JSONObject usersListJSON = this.getEBSDeviceDetails((ArrayList)successList);
                    final JSONArray availableUsersJSON = usersListJSON.getJSONArray("UsersAvailableList");
                    final JSONArray unavailableUsersJSON = usersListJSON.getJSONArray("UsersNotAvailableList");
                    final JSONObject appDistribution = this.createAppDistributionObjectByUser(appDetailsJSON, availableUsersJSON);
                    final JSONObject installStatusJSON = ebs.installAppsToDevices(appDistribution);
                    final List failedResList = this.updateFailureStatusForEntitlement(installStatusJSON, false, extraParamsForStatusUpdate);
                    if (isDelayRedistributionAllowed) {
                        managedAccountYetToRecogDevices = this.getGoogleYetToRecognizeDevices(installStatusJSON, managedAccountYetToRecogDevices);
                    }
                    successList.removeAll(failedResList);
                    final String remarks = "mdm.appmgmt.afw.install_initiated@@@<l>$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)&$(did)&src=appRemark#playstore";
                    appInstallationStatusHandler.updateAppStatus(this.remarksToResListMap(remarks, successList), collectionId, 3);
                    failedResList.addAll(failedEntitlement);
                    failedJSON.put(collectionId.toString(), (Object)JSONUtil.getInstance().convertListToJSONArray(failedResList));
                    this.logger.log(Level.INFO, "installAppsByDevices Install status :{0}", installStatusJSON);
                }
                catch (final Exception exp) {
                    this.logger.log(Level.SEVERE, exp, () -> "Exception when installAppsByDevices for collection Id " + n + " for resources " + list);
                }
            }
            if (isDelayRedistributionAllowed && !managedAccountYetToRecogDevices.isEmpty()) {
                if (managedAccountYetToRecogDevices.size() == 1) {
                    this.logger.log(Level.SEVERE, "[MANAGED_ACCOUNT_DELAY_REDISTRIBUTION] Going to initiate app redistribution for device {0}", managedAccountYetToRecogDevices);
                    final Boolean doNotProceedWithFurtherCollection = new GoogleApiRetryHandler().delayRedistributionForNotRecogDevices(managedAccountYetToRecogDevices, customerId, afwAccountReadyHandling);
                    failedJSONDetails.put("DoNotProceedWithFurtherCollection", (Object)doNotProceedWithFurtherCollection);
                }
                else {
                    this.logger.log(Level.SEVERE, "[MANAGED_ACCOUNT_DELAY_REDISTRIBUTION] As Managed Account handling will have only one resourceId, redistribution will not initiated for devices {0}", managedAccountYetToRecogDevices);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Exception while initialising installAppsByDevices for collections " + list2 + " for resources " + list3);
            for (int i = 0; i < collectionList.size(); ++i) {
                try {
                    final Long collectionId2 = collectionList.get(i);
                    failedJSON.put(collectionId2.toString(), (Collection)resourceList);
                    final String remarks2 = "mdm.agent.payload.webcontentfilter.error.unknown";
                    final AppInstallationStatusHandler appInstallationStatusHandler2 = new AppInstallationStatusHandler();
                    appInstallationStatusHandler2.updateAppStatus(this.remarksToResListMap(remarks2, resourceList), collectionId2, 7);
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, "Exception while populating failed list", e);
                }
            }
        }
        failedJSONDetails.put("failedJSON", (Object)failedJSON);
        return failedJSONDetails;
    }
    
    private List assignEntitlement(final ArrayList resourceList, final Long collectionId, final JSONObject playStoreDetails) throws Exception {
        final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(playStoreDetails);
        final JSONObject appDetailsJSON = this.getPortalAppDetails(collectionId);
        JSONObject usersListJSON = this.getEBSUserDetails(resourceList);
        JSONArray availableUsersJSON = usersListJSON.getJSONArray("UsersAvailableList");
        final JSONArray unavailableUsersJSON = usersListJSON.getJSONArray("UsersNotAvailableList");
        this.updateFailureStatusForBSUserNotAvailableRes(unavailableUsersJSON, collectionId, playStoreDetails);
        if (playStoreDetails.get("ENTERPRISE_TYPE").equals(GoogleForWorkSettings.ENTERPRISE_TYPE_EMM)) {
            usersListJSON = new AFWAccountStatusHandler().getAccountStatusDetails(availableUsersJSON);
            this.logger.log(Level.INFO, "Managed account user status :{0}", usersListJSON);
            this.updateRemarksForAccountNotAdded(usersListJSON.getJSONArray("AccountNotAddedList"), collectionId);
            availableUsersJSON = usersListJSON.getJSONArray("AccountAddedList");
        }
        final JSONObject appDistribution = this.createAppDistributionObjectByUser(appDetailsJSON, availableUsersJSON);
        final JSONObject installStatusJSON = ebs.assignAppsToUsers(appDistribution);
        final List failedResList = this.updateFailureStatusForEntitlement(installStatusJSON, false, new JSONObject());
        final List successList = new ArrayList();
        if (availableUsersJSON != null) {
            for (int i = 0; i < availableUsersJSON.length(); ++i) {
                successList.add(availableUsersJSON.getJSONObject(i).getLong("MANAGED_DEVICE_ID"));
            }
        }
        successList.removeAll(failedResList);
        final JSONArray storeUserArr = new JSONArray();
        for (int j = 0; j < successList.size(); ++j) {
            storeUserArr.put((Object)new StoreAccountManagementHandler().getStoreAccountUserByDevice(successList.get(j)));
        }
        new StoreAppsLicenseHandler().addOrUpdateStoreAppsLicenseToBSUsers(playStoreDetails.getLong("BUSINESSSTORE_ID"), appDetailsJSON.getLong("APP_GROUP_ID"), storeUserArr);
        this.logger.log(Level.INFO, "App Entitlement assigned status :{0}", installStatusJSON);
        return successList;
    }
    
    public void disAssociateAppsByUsers(final ArrayList resourceList, final Map<Long, JSONObject> portalAppDetails, final GooglePlayEnterpriseBusinessStore ebs, final Long businessStoreId, final HashMap<Long, List> collnToApplicableResources) {
        try {
            final long startTime = System.currentTimeMillis();
            this.logger.log(Level.INFO, "DisassociateAppsByUsers started for resourceList {0}", new Object[] { resourceList });
            final List<Long> collectionList = new ArrayList<Long>(portalAppDetails.keySet());
            final Map<Long, JSONObject> devicesToBStoreUserIdMap = new StoreAccountManagementHandler().getStoreAccountUsersByDevicesMap(resourceList);
            ebs.initializeBatch();
            final JSONArray responseArray = new JSONArray();
            for (int j = 0; j < collectionList.size(); ++j) {
                try {
                    final Long collectionId = collectionList.get(j);
                    final JSONObject appDetailsJSON = portalAppDetails.get(collectionId);
                    final Map<Long, JSONObject> applicableResourceDetails = new HashMap<Long, JSONObject>(devicesToBStoreUserIdMap);
                    if (collnToApplicableResources != null) {
                        applicableResourceDetails.keySet().retainAll(collnToApplicableResources.get(collectionId));
                    }
                    final List<JSONObject> userList = new ArrayList<JSONObject>(applicableResourceDetails.values());
                    JSONArray availableUsersJSON = JSONUtil.getInstance().convertListToJSONArray(userList);
                    availableUsersJSON = this.checkStoreUserMatchWithOtherRes(appDetailsJSON.getLong("APP_GROUP_ID"), availableUsersJSON);
                    if (availableUsersJSON.length() > 0) {
                        final JSONObject appDistribution = this.createAppDistributionObjectByUser(appDetailsJSON, availableUsersJSON);
                        ebs.removeAppsToUsers(appDistribution, responseArray);
                    }
                }
                catch (final Exception exp) {
                    this.logger.log(Level.WARNING, "Exception", exp);
                }
            }
            final JSONObject restOfBatchResponse = ebs.clearBatch("removeAppsToUsers");
            if (restOfBatchResponse.length() > 0) {
                responseArray.put((Object)restOfBatchResponse);
            }
            this.logger.log(Level.INFO, "Afw app removal status {0}", responseArray);
            final PlayStoreAppDistributionRequestHandler playStoreAppDistributionRequestHandler = new PlayStoreAppDistributionRequestHandler();
            for (int i = 0; i < responseArray.length(); ++i) {
                final JSONObject appJSON = (JSONObject)responseArray.get(i);
                playStoreAppDistributionRequestHandler.updateFailureStatusForEntitlement(appJSON, true, new JSONObject());
            }
            this.logger.log(Level.INFO, "DisassociateAppsByUsers ended for resourceList {0} - timeTaken {1} ", new Object[] { resourceList, System.currentTimeMillis() - startTime });
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception", ex);
        }
    }
    
    public void removeAppsByDevices(final ArrayList resourceList, final Map<Long, JSONObject> portalAppDetails, final GooglePlayEnterpriseBusinessStore ebs, final Map<Long, List> collectionToApplicableRes) {
        try {
            final StoreAccountManagementHandler storeHandler = new StoreAccountManagementHandler();
            final Map<Long, JSONObject> deviceToBstoreAccountUserMap = storeHandler.getStoreAccountUserDeviceDetailsByDevicesMap(resourceList);
            ebs.initializeBatch();
            final Map<Long, List> portalAppToApplicableRes = new HashMap<Long, List>(collectionToApplicableRes);
            portalAppToApplicableRes.keySet().retainAll(portalAppDetails.keySet());
            final List<Long> portalCollectionList = new ArrayList<Long>(portalAppToApplicableRes.keySet());
            for (int i = 0; i < portalCollectionList.size(); ++i) {
                final Long collectionId = portalCollectionList.get(i);
                final List<Long> applicableResources = collectionToApplicableRes.get(collectionId);
                final Map<Long, JSONObject> applicableResourcesDetails = new HashMap<Long, JSONObject>(deviceToBstoreAccountUserMap);
                applicableResourcesDetails.keySet().retainAll(applicableResources);
                final List<JSONObject> availableUserList = new ArrayList<JSONObject>(applicableResourcesDetails.values());
                if (!availableUserList.isEmpty()) {
                    final JSONObject appDetails = portalAppDetails.get(collectionId);
                    final JSONArray availableUsersJSON = JSONUtil.getInstance().convertListToJSONArray(availableUserList);
                    final JSONObject appDistribution = this.createAppDistributionObjectByDevices(appDetails, availableUsersJSON);
                    ebs.removeAppsToDevices(appDistribution);
                }
            }
            ebs.clearBatch("Device app removal execute from rest of the batch");
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception", ex);
        }
    }
    
    public void updateAvailableProductSet(final ArrayList resourceList, final Long customerId) throws DataAccessException, JSONException, Exception {
        final JSONObject playStoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(playStoreDetails);
        final JSONObject userDistribution = this.createAvailableProductSetByUser(resourceList);
        final JSONObject productSetStatusJSON = ebs.updateAvailableProductSet(userDistribution);
        this.logger.log(Level.INFO, "updateAvailableProductSet response : {0}", productSetStatusJSON.toString());
    }
    
    JSONObject createAppDistributionObjectByUser(final JSONObject appDetailsJSON, final JSONArray distributionUserList) throws Exception {
        final JSONArray appsList = new JSONArray();
        final JSONObject appListElement = new JSONObject();
        appListElement.put("IDENTIFIER", appDetailsJSON.get("IDENTIFIER"));
        appListElement.put("APP_ID", appDetailsJSON.get("APP_ID"));
        appListElement.put("COLLECTION_ID", appDetailsJSON.get("COLLECTION_ID"));
        appListElement.put("Users", (Object)distributionUserList);
        appsList.put((Object)appListElement);
        final JSONObject appDistributon = new JSONObject();
        appDistributon.put("apps", (Object)appsList);
        return appDistributon;
    }
    
    JSONObject createAvailableProductSetByUser(final ArrayList<Long> distributionList) throws JSONException, SyMException {
        final JSONArray userArr = new JSONArray();
        for (int i = 0; i < distributionList.size(); ++i) {
            final String userId = this.getEBSUserId(distributionList.get(i));
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(distributionList.get(i)));
            if (userId != null) {
                final JSONArray appPackageNameArr = AppsUtil.getInstance().getPortalAppsAssociatedForDevice(distributionList.get(i));
                final JSONObject userJSONObject = new JSONObject();
                userJSONObject.put("apps", (Object)appPackageNameArr);
                userJSONObject.put("userId", (Object)userId);
                userJSONObject.put("resourceId", (Object)distributionList.get(i));
                userArr.put((Object)userJSONObject);
            }
            else {
                this.logger.log(Level.WARNING, "User id not available so must report as failed  ");
            }
        }
        final JSONObject userDistribution = new JSONObject();
        userDistribution.put("Users", (Object)userArr);
        return userDistribution;
    }
    
    JSONObject createAppDistributionObjectByDevices(final JSONObject appDetailsJSON, final JSONArray distributionList) throws Exception {
        final JSONArray appsList = new JSONArray();
        final JSONObject appListElement = new JSONObject();
        appListElement.put("IDENTIFIER", appDetailsJSON.get("IDENTIFIER"));
        appListElement.put("APP_ID", appDetailsJSON.get("APP_ID"));
        appListElement.put("UsersAndDevices", (Object)distributionList);
        appsList.put((Object)appListElement);
        final JSONObject appDistributon = new JSONObject();
        appDistributon.put("apps", (Object)appsList);
        return appDistributon;
    }
    
    JSONObject createAppDistributionObjectListByDevices(final Map<Long, JSONObject> collntoAppDetails, final JSONArray distributionList) throws Exception {
        final JSONArray appsList = new JSONArray();
        final JSONObject appDistributon = new JSONObject();
        for (final Long collectionId : collntoAppDetails.keySet()) {
            final JSONObject appListElement = new JSONObject();
            final JSONObject appDetailsJSON = collntoAppDetails.get(collectionId);
            appListElement.put("IDENTIFIER", appDetailsJSON.get("IDENTIFIER"));
            appListElement.put("APP_ID", appDetailsJSON.get("APP_ID"));
            appListElement.put("UsersAndDevices", (Object)distributionList);
            appsList.put((Object)appListElement);
            appDistributon.put("apps", (Object)appsList);
        }
        return appDistributon;
    }
    
    JSONObject getEBSUserDetails(final ArrayList distributionList) throws JSONException {
        final StoreAccountManagementHandler storeHandler = new StoreAccountManagementHandler();
        final JSONObject resourceDetailsJSON = storeHandler.getStoreAccountUsersByDevices(distributionList);
        return resourceDetailsJSON;
    }
    
    String getEBSUserId(final Long resourceId) {
        final StoreAccountManagementHandler storeHandler = new StoreAccountManagementHandler();
        return storeHandler.getStoreAccountUserByDevice(resourceId);
    }
    
    JSONObject getEBSDeviceDetails(final ArrayList distributionList) throws JSONException {
        final StoreAccountManagementHandler storeHandler = new StoreAccountManagementHandler();
        final JSONObject deviceJSON = storeHandler.getStoreAccountUserDeviceDetailsByDevices(distributionList);
        return deviceJSON;
    }
    
    @Deprecated
    JSONObject getPortalAppDetails(final Long collectionID) {
        final JSONObject appsMap = new JSONObject();
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppToCollection"));
            sQuery.addJoin(new Join("MdAppToCollection", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
            sQuery.addJoin(new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            sQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            sQuery.addJoin(new Join("MdAppGroupDetails", "MdLicenseToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
            sQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "*"));
            sQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "*"));
            sQuery.addSelectColumn(Column.getColumn("MdPackageToAppGroup", "*"));
            sQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "*"));
            final Criteria collectionCriteira = new Criteria(Column.getColumn("MdAppToCollection", "COLLECTION_ID"), (Object)collectionID, 0);
            sQuery.setCriteria(collectionCriteira);
            final DataObject dObj = DataAccess.get(sQuery);
            if (!dObj.isEmpty()) {
                final Row appRow = dObj.getFirstRow("MdPackageToAppData");
                final Row appGroupRow = dObj.getFirstRow("MdPackageToAppGroup");
                final Row appGroupDetailsRow = dObj.getFirstRow("MdAppGroupDetails");
                appsMap.put("COLLECTION_ID", (Object)collectionID);
                appsMap.put("APP_ID", appRow.get("APP_ID"));
                appsMap.put("APP_GROUP_ID", appRow.get("APP_GROUP_ID"));
                appsMap.put("PACKAGE_ID", appRow.get("PACKAGE_ID"));
                appsMap.put("SUPPORTED_DEVICES", appRow.get("SUPPORTED_DEVICES"));
                appsMap.put("PACKAGE_TYPE", appGroupRow.get("PACKAGE_TYPE"));
                appsMap.put("IS_PAID_APP", appGroupRow.get("IS_PAID_APP"));
                appsMap.put("IS_PURCHASED_FROM_PORTAL", appGroupRow.get("IS_PURCHASED_FROM_PORTAL"));
                appsMap.put("IDENTIFIER", appGroupDetailsRow.get("IDENTIFIER"));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception", ex);
        }
        return appsMap;
    }
    
    ArrayList filterUsers(final JSONObject jsonObject, final ArrayList resourceList) {
        final FilterUsers filter = new FilterUsers();
        filter.filterUsersByStatus(jsonObject, resourceList);
        return null;
    }
    
    ArrayList filterDevices(final JSONObject jsonObject, final ArrayList resourceList) {
        final FilterDevices filter = new FilterDevices();
        final ArrayList distributionList = (ArrayList)resourceList.clone();
        ArrayList filteredList = filter.filterDevicesByModels(jsonObject, resourceList);
        distributionList.removeAll(filteredList);
        filteredList = filter.filterDevicesByOperatingSystem(jsonObject, resourceList);
        distributionList.removeAll(filteredList);
        filteredList = filter.filterDevicesByLicense(jsonObject, resourceList);
        distributionList.removeAll(filteredList);
        return distributionList;
    }
    
    ArrayList filterDevicesByGroup(final JSONObject jsonObject, final ArrayList resourceList, final ArrayList groupList) {
        final FilterDevices filter = new FilterDevices();
        final ArrayList distributionList = (ArrayList)resourceList.clone();
        ArrayList filteredList = filter.filterGroupMembersByModels(jsonObject, groupList);
        distributionList.removeAll(filteredList);
        filteredList = filter.filterGroupMembersByOperatingSystem(jsonObject, groupList);
        distributionList.removeAll(filteredList);
        filteredList = filter.filterGroupMembersByLicense(jsonObject, groupList);
        distributionList.removeAll(filteredList);
        return distributionList;
    }
    
    private JSONArray checkStoreUserMatchWithOtherRes(final Long appGroupId, final JSONArray userArrList) throws DataAccessException, Exception {
        final long startTime = System.currentTimeMillis();
        final List<JSONObject> userArr = JSONUtil.getInstance().convertJSONArrayTOList(userArrList);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppCatalogToResource"));
        selectQuery.setCriteria(new Criteria(new Column("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)appGroupId, 0));
        selectQuery.addSelectColumn(new Column("MdAppCatalogToResource", "*"));
        final DataObject dO = DataAccess.get(selectQuery);
        if (!dO.isEmpty()) {
            final List<Long> resList = DBUtil.getColumnValuesAsList(dO.getRows("MdAppCatalogToResource"), "RESOURCE_ID");
            final Map<Long, String> storeUserIds = new StoreAccountManagementHandler().getStoreAccountUserByDevice(resList);
            for (final Long resourceId : resList) {
                final String storeUserId = storeUserIds.get(resourceId);
                for (int i = 0; i < userArr.size(); ++i) {
                    if (!String.valueOf(userArr.get(i).get("MANAGED_DEVICE_ID")).equals(resourceId.toString()) && storeUserId != null && storeUserId.equals(userArr.get(i).get("BS_STORE_ID"))) {
                        userArr.remove(i);
                        --i;
                    }
                }
            }
        }
        this.logger.log(Level.INFO, "checkStoreUserMatch for single App - Time taken {0}", System.currentTimeMillis() - startTime);
        return JSONUtil.getInstance().convertListToJSONArray(userArr);
    }
    
    private void updateFailureStatusForBSUserNotAvailableRes(final JSONArray userNotAvaiableJSONArr, final Long collectionId, final JSONObject playstoreDetails) throws Exception {
        final List<Long> resList = JSONUtil.getInstance().convertJSONArrayTOList(userNotAvaiableJSONArr);
        final List<Long> accNotAvailableResList = new ArrayList<Long>();
        final List<Long> agentToBeUpgradedList = new ArrayList<Long>();
        final List<Long> toBeDeviceOwnerList = new ArrayList<Long>();
        final int enterpriseType = (int)playstoreDetails.get("ENTERPRISE_TYPE");
        final Long customerID = (Long)playstoreDetails.get("CUSTOMER_ID");
        final Boolean isPaidApp = MDMUtil.getInstance().getAppPackageDataDetails(MDMUtil.getInstance().getAppIDFromCollection(collectionId)).get("IS_PAID_APP");
        final int status = isPaidApp ? 7 : 12;
        final AppInstallationStatusHandler appInstallationStatusHandler = new AppInstallationStatusHandler();
        for (final Long resourceId : resList) {
            final int afwCompatiblity = new GoogleManagedAccountHandler().isAndWhyDeviceNotAFWCompatible(resourceId);
            if (afwCompatiblity == 0) {
                accNotAvailableResList.add(resourceId);
            }
            else if (afwCompatiblity == 1) {
                agentToBeUpgradedList.add(resourceId);
            }
            else {
                if (afwCompatiblity != 2) {
                    continue;
                }
                toBeDeviceOwnerList.add(resourceId);
            }
        }
        if (!agentToBeUpgradedList.isEmpty()) {
            final String remarks = "mdm.appmgmt.afw.agent_upgrade_for_install";
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(agentToBeUpgradedList.get(0)));
            appInstallationStatusHandler.updateAppStatus(this.remarksToResListMap(remarks, agentToBeUpgradedList), collectionId, status);
            this.logger.log(Level.INFO, "Need agent upgrade for app distribution {0}", agentToBeUpgradedList);
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Android_Module", "afwAppUpgradeNeeded");
        }
        if (!toBeDeviceOwnerList.isEmpty()) {
            final Long customerId2 = CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(toBeDeviceOwnerList.get(0)));
            final String remarks2 = "mdm.appmgmt.afw.device_owner_for_install@@@<l>$(mdmUrl)/help/android_for_work/mdm_android_for_work_introduction.html?$(traceurl)&$(did)&src=appRemark#Device_Owner";
            appInstallationStatusHandler.updateAppStatus(this.remarksToResListMap(remarks2, toBeDeviceOwnerList), collectionId, status);
            this.logger.log(Level.INFO, "Need to enroll ad device owner for app distribution {0}", toBeDeviceOwnerList);
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId2, "Android_Module", "afwEnrollAsSupervised");
        }
        if (!accNotAvailableResList.isEmpty()) {
            String remarks;
            String trackKey;
            if (enterpriseType == GoogleForWorkSettings.ENTERPRISE_TYPE_GOOGLE) {
                final String domainName = String.valueOf(playstoreDetails.get("MANAGED_DOMAIN_NAME"));
                remarks = "mdm.appmgmt.afw.add_gsuite_account@@@" + domainName + "@@@<l>" + "$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)&$(did)&src=appRemark#gsuite" + "@@@<l>" + "$(mdmUrl)/help/android_for_work/mdm_afw_prerequisites.html?$(traceurl)&$(did)&src=appRemark#using_google_account";
                trackKey = "afwGSuiteUserAccNeeded";
            }
            else {
                for (final Long resourceID : accNotAvailableResList) {
                    final String udid = (String)DBUtil.getValueFromDB("ManagedDevice", "RESOURCE_ID", (Object)resourceID, "UDID");
                    new GoogleManagedAccountHandler().addAFWAccountAdditionCmd(resourceID, udid, customerID);
                }
                remarks = "mdm.appmgmt.afw.account_addition_initiated@@@<l>$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)&$(did)&src=appRemark#without_gsuite";
                trackKey = "afwInitMissedAccAdd";
            }
            final Long customerId3 = CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(accNotAvailableResList.get(0)));
            appInstallationStatusHandler.updateAppStatus(this.remarksToResListMap(remarks, accNotAvailableResList), collectionId, status);
            this.logger.log(Level.INFO, "Account not available. Initaited if afw2 {0}", accNotAvailableResList);
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId3, "Android_Module", trackKey);
        }
    }
    
    private void updateRemarksForAccountNotAdded(final JSONArray accountNotAddedList, final Long collectionId) throws Exception {
        final int totalRes = accountNotAddedList.length();
        final List<Long> accInitiatedList = new ArrayList<Long>();
        final HashMap<String, List> errCodeRemarksMap = new HashMap<String, List>();
        final AppInstallationStatusHandler appInstallationStatusHandler = new AppInstallationStatusHandler();
        for (int i = 0; i < totalRes; ++i) {
            final JSONObject failureDetails = accountNotAddedList.getJSONObject(i);
            final long resID = failureDetails.getLong("resourceID");
            final int status = failureDetails.getInt("status");
            final int errorCode = failureDetails.getInt("errorCode");
            switch (status) {
                case 1:
                case 4: {
                    accInitiatedList.add(resID);
                    break;
                }
                case 3: {
                    final String remarks = new AFWAccountErrorHandler().getAppDistributionRemarksForErrorCode(errorCode, resID);
                    if (errCodeRemarksMap.containsKey(remarks)) {
                        final List resourceList = errCodeRemarksMap.get(remarks);
                        resourceList.add(resID);
                        errCodeRemarksMap.put(remarks, resourceList);
                    }
                    else {
                        errCodeRemarksMap.put(remarks, this.newList(resID));
                    }
                    final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(resID));
                    MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Android_Module", "afwAccountFailed");
                    break;
                }
            }
        }
        appInstallationStatusHandler.updateAppStatus(errCodeRemarksMap, collectionId, 7);
        if (!accInitiatedList.isEmpty()) {
            final Long customerId2 = CustomerInfoUtil.getInstance().getCustomerIDForResID(Long.valueOf(accInitiatedList.get(0)));
            final String remarks2 = "mdm.appmgmt.afw.account_addition_initiated@@@<l>$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)&$(did)&src=appRemark#without_gsuite";
            appInstallationStatusHandler.updateAppStatus(this.remarksToResListMap(remarks2, accInitiatedList), collectionId, 12);
            this.logger.log(Level.INFO, "Account addition is inititated for app distribution {0}", accInitiatedList);
            MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId2, "Android_Module", "afwWaitingForAccAdd");
        }
    }
    
    public List updateFailureStatusForEntitlement(final JSONObject responseJSON, final boolean removal, JSONObject extraParamsJSON) throws Exception {
        if (extraParamsJSON == null) {
            extraParamsJSON = new JSONObject();
        }
        final List failedResList = new ArrayList();
        final Iterator jsonIter = responseJSON.keys();
        HashMap<String, List> remarksResMap = new HashMap<String, List>();
        final HashMap<Long, HashMap<String, List>> collnToResRemarks = new HashMap<Long, HashMap<String, List>>();
        while (jsonIter.hasNext()) {
            final Long appId = Long.parseLong(jsonIter.next());
            final Long collectionId = MDMUtil.getInstance().getCollectionIDfromAppID(appId);
            final JSONObject appStatusJSON = responseJSON.getJSONObject(appId.toString());
            final JSONArray resJSONArr = appStatusJSON.optJSONArray("FailedList");
            if (resJSONArr != null) {
                remarksResMap = new HashMap<String, List>();
                for (int i = 0; i < resJSONArr.length(); ++i) {
                    final Long resourceID = resJSONArr.getJSONObject(i).getLong("resourceId");
                    try {
                        final String remarks = this.getRemarksForEntitlementFailure(appId, resourceID, String.valueOf(resJSONArr.getJSONObject(i).get("ErrorMessage")), extraParamsJSON);
                        if (remarksResMap.containsKey(remarks)) {
                            final List resourceList = remarksResMap.get(remarks);
                            resourceList.add(resourceID);
                            remarksResMap.replace(remarks, resourceList);
                        }
                        else {
                            remarksResMap.put(remarks, this.newList(resourceID));
                        }
                        failedResList.add(resJSONArr.getJSONObject(i).get("resourceId"));
                    }
                    catch (final Exception e) {
                        this.logger.log(Level.WARNING, e, () -> "Exception when trying to update remarks " + n + " " + n2);
                    }
                }
            }
            if (collnToResRemarks.containsKey(collectionId)) {
                remarksResMap.putAll(collnToResRemarks.get(collectionId));
                collnToResRemarks.replace(collectionId, remarksResMap);
            }
            else {
                collnToResRemarks.put(collectionId, remarksResMap);
            }
        }
        final List collectionList = new ArrayList(collnToResRemarks.keySet());
        final AppInstallationStatusHandler appInstallationStatusHandler = new AppInstallationStatusHandler();
        for (int j = 0; j < collectionList.size(); ++j) {
            remarksResMap = collnToResRemarks.get(collectionList.get(j));
            final Long collectionID = collectionList.get(j);
            appInstallationStatusHandler.updateAppStatus(remarksResMap, collectionID, 7, removal);
        }
        this.logger.log(Level.INFO, "Failed resources details {0}", responseJSON);
        return failedResList;
    }
    
    private String getRemarksForEntitlementFailure(final Long appId, final Long resourceID, final String errorMessage, final JSONObject extraParamsJSON) throws Exception {
        final StoreAccountManagementHandler storeAccHandler = new StoreAccountManagementHandler();
        String remarks = errorMessage;
        String trackKey = "afwEntitlementFailure";
        if (errorMessage.contains("app permissions haven't been accepted")) {
            remarks = "mdm.appmgmt.afw.pending_approval_remarks@@@<l>https://play.google.com/work/apps/details?id=" + AppsUtil.getInstance().getAppDetailsJson(appId).get("IDENTIFIER");
            trackKey = "afwAppUpdateApprovalNeeded";
        }
        else if (errorMessage.contains(I18N.getMsg("No device was found", new Object[0]))) {
            final String bsStoreID = storeAccHandler.getStoreAccountUserByDevice(resourceID);
            final JSONArray userList = new JSONArray();
            userList.put((Object)bsStoreID);
            new GooglePlayDevicesSyncRequestHandler(CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID)).syncDevices(userList);
            final Boolean isBSDeviceAvailable = storeAccHandler.isBSDeviceAvailableForManagedDevice(resourceID);
            if (isBSDeviceAvailable) {
                remarks = "mdm.appmgmt.afw.no_device_contact_support@@@<l>$(mdmUrl)/how-to/logs-how-to.html?$(traceurl)&$(did)&src=appRemarks";
                trackKey = "afwNoDeviceCount";
            }
            else {
                final Boolean is_afwAccountReadyHandling = extraParamsJSON.optBoolean("is_afwAccountReadyHandling", false);
                this.logger.log(Level.INFO, "AFW Account added successfully in device, but not yet recognised by Google and is_afwAccountReadyHandling: {0}", is_afwAccountReadyHandling);
                remarks = "Contact support if app is not pushed for long time. Error code: DEVICE_NOT_YET_RECOGNISED_BY_GOOGLE.";
                trackKey = "afwTermsAcceptNeeded";
            }
        }
        else if (errorMessage.contains(I18N.getMsg("No enterprise was found", new Object[0])) || errorMessage.contains(I18N.getMsg("not associated with any enrolled enterprise", new Object[0]))) {
            remarks = "mdm.appmgmt.afw.no_enterprise_remarks@@@<l>/webclient#/uems/mdm/manage/appRepo/appMgmt/android/managedGooglePlay";
            trackKey = "afwNoEnterprise";
        }
        else if (errorMessage.contains("No entitlement was found")) {
            remarks = "mdm.appmgmt.afw.no_entitlement_remarks@@@<l>https://play.google.com/work/apps/details?id=" + AppsUtil.getInstance().getAppDetailsJson(appId).get("IDENTIFIER");
        }
        else if (errorMessage.contains("admin of the enterprise has not accepted the Managed Google Play Terms of Service")) {
            remarks = "mdm.appmgmt.afw.tos_not_accepted@@@<l>https://play.google.com/work/termsofservice";
        }
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
        MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "Android_Module", trackKey);
        return remarks;
    }
    
    private <T> List<T> newList(final T... list) {
        final List<T> newList = new ArrayList<T>();
        for (final T a : list) {
            newList.add(a);
        }
        return newList;
    }
    
    private HashMap<String, List> remarksToResListMap(final String remark, final List resList) {
        final HashMap<String, List> remarksToRes = new HashMap<String, List>();
        remarksToRes.put(remark, resList);
        return remarksToRes;
    }
    
    private HashSet<Long> getGoogleYetToRecognizeDevices(final JSONObject responseJSON, final HashSet<Long> managedAccountYetToRecogDevices) {
        try {
            final Iterator jsonIter = responseJSON.keys();
            while (jsonIter.hasNext()) {
                final String appId = jsonIter.next();
                final JSONObject appStatusJSON = responseJSON.getJSONObject(appId);
                final JSONArray failedList = appStatusJSON.optJSONArray("FailedList");
                if (failedList != null) {
                    for (int i = 0; i < failedList.length(); ++i) {
                        final Long resourceID = failedList.getJSONObject(i).getLong("resourceId");
                        final String errorMsg = String.valueOf(failedList.getJSONObject(i).get("ErrorMessage"));
                        if (errorMsg.contains("No device was found")) {
                            managedAccountYetToRecogDevices.add(resourceID);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "[MANAGED_ACCOUNT_DELAY_REDISTRIBUTION]Exception occurred while getting devices for App redistribution  after AF", ex);
        }
        this.logger.log(Level.INFO, "[MANAGED_ACCOUNT_DELAY_REDISTRIBUTION]Recourse list identified as no device {0}", managedAccountYetToRecogDevices);
        return managedAccountYetToRecogDevices;
    }
    
    @Deprecated
    public void removeLicenseForPaidApps(final List<Long> resourceList, final Long businessStoreId, final Long appGroupId) {
        try {
            final Map<Long, String> storeUser = new StoreAccountManagementHandler().getStoreAccountUserByDevice(resourceList);
            final JSONArray storeUserArr = JSONUtil.getInstance().convertListToJSONArray(new ArrayList(storeUser.values()));
            new StoreAppsLicenseHandler().removeStoreAppsLicenseToBSUsers(businessStoreId, appGroupId, storeUserArr);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Could remove license for paid apps {0}", e);
        }
    }
}
