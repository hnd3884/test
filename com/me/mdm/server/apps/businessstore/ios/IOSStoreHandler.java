package com.me.mdm.server.apps.businessstore.ios;

import java.util.Hashtable;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.HashSet;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Set;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppDisassociationHandler;
import com.me.mdm.server.deployment.MDMResourceToProfileDeploymentConfigHandler;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.files.FileFacade;
import java.util.Iterator;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.GroupByClause;
import java.util.Collection;
import java.util.Properties;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONArray;
import com.me.mdm.server.apps.businessstore.model.ios.IOSBusinessStoreSyncModel;
import java.util.List;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.me.mdm.server.apps.AppTrashModeHandler;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.mdm.server.apps.ios.vpp.VPPSyncStatusHandler;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppMgmtHandler;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import com.me.mdm.server.apps.businessstore.model.ios.IOSEnterpriseBusinessStoreModel;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAssetsHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import java.util.logging.Logger;
import com.me.mdm.server.apps.businessstore.BaseStoreHandler;

public class IOSStoreHandler extends BaseStoreHandler
{
    Logger logger;
    public static String VPP_STORE_SYNC_PARAM;
    
    public IOSStoreHandler(final Long businessStoreID, final Long customerID) {
        super(businessStoreID, customerID);
        this.logger = Logger.getLogger("MDMBStoreLogger");
        this.platformType = 1;
        this.serviceType = BusinessStoreSyncConstants.BS_SERVICE_VPP;
    }
    
    @Override
    public JSONObject addStoreDetails(final JSONObject message) throws Exception {
        final Long userID = APIUtil.getUserID(message);
        final String userName = APIUtil.getUserName(message);
        final JSONObject requestJson = message.getJSONObject("msg_body");
        JSONObject response = new JSONObject();
        response = this.handleVPPTokenUpload(requestJson, userID);
        String locationName = (String)response.opt("LOCATION_NAME");
        if (locationName == null) {
            locationName = response.getString("ORGANISATION_NAME");
        }
        final int orgType = response.optInt("ORG_TYPE", 0);
        String org = "Apple Business Manager/Apple School Manager";
        if (orgType == 1) {
            org = "Apple Business Manager";
        }
        else if (orgType == 2) {
            org = "Apple School Manager";
        }
        final String remarks = MDMI18N.getI18Nmsg("mdm.vpp.added", new Object[] { org, locationName });
        MDMEventLogHandler.getInstance().MDMEventLogEntry(72503, null, userName, remarks, "", this.customerID);
        return response;
    }
    
    @Override
    public void syncLicense(final JSONObject jsonObject) throws Exception {
        this.validateIfStoreFound();
        final Long appId = jsonObject.getLong("PACKAGE_ID");
        final Long appGroupId = jsonObject.getLong("APP_GROUP_ID");
        final Long userID = jsonObject.getLong("USER_ID");
        this.validateIfAppAvailableForBusinessStore(appId);
        final String storeId = String.valueOf(AppsUtil.getInstance().getStoreId(appId));
        final VPPAssetsHandler vppAssetsHandler = new VPPAssetsHandler();
        final Long assetID = vppAssetsHandler.getAssetIDForAdamID(this.businessStoreID, storeId);
        final int appSyncStatus = vppAssetsHandler.getVppAppSyncStatus(assetID);
        final JSONObject queueJSON = new JSONObject();
        queueJSON.put("USER_ID", (Object)userID);
        queueJSON.put("APP_GROUP_ID", (Object)appGroupId);
        queueJSON.put("ADAM_ID", (Object)storeId);
        queueJSON.put("BUSINESSSTORE_ID", (Object)this.businessStoreID);
        try {
            if (appSyncStatus == 0) {
                vppAssetsHandler.updateVPPAssetSyncStatus(assetID, 1, null);
                this.addVppLicenseSyncTaskToQueue(queueJSON);
            }
            else {
                this.logger.log(Level.INFO, "Cannot sync vpp app with appGroupID {0} -> assetID {1}", new Object[] { appGroupId, assetID });
            }
        }
        catch (final Exception e) {
            vppAssetsHandler.updateVPPAssetSyncStatus(assetID, 0, null);
            this.logger.log(Level.SEVERE, "Exception in adding license sync operation to queue", e);
        }
    }
    
    private void addVppLicenseSyncTaskToQueue(final JSONObject jsonQueueData) throws Exception {
        this.logger.log(Level.INFO, "Adding VPP Single App License Sync Task To Queue");
        jsonQueueData.put("MsgType", (Object)"VppSingleAppLicenseSync");
        final String strData = jsonQueueData.toString();
        final String separator = "\t";
        final String qFileName = "vpp-license-handling-" + System.currentTimeMillis();
        final DCQueueData queueData = new DCQueueData();
        queueData.fileName = qFileName;
        queueData.postTime = System.currentTimeMillis();
        queueData.queueData = strData;
        queueData.customerID = this.customerID;
        this.logger.log(Level.INFO, "QueueName : {0}{1}AddingToQueue{2}{3}{4}{5}", new Object[] { "vpp-license-handling", separator, separator, queueData.fileName, separator, String.valueOf(System.currentTimeMillis()) });
        final DCQueue queue = DCQueueHandler.getQueue("vpp-license-handling");
        queue.addToQueue(queueData);
    }
    
    @Override
    public JSONObject getLicenseSyncStatus(final JSONObject jsonObject) throws Exception {
        this.validateIfStoreFound();
        final JSONObject responseJSON = new JSONObject();
        final Long appId = jsonObject.getLong("app_id");
        final Long appGroupId = jsonObject.getLong("app_group_id");
        this.validateIfAppAvailableForBusinessStore(appId);
        final int statusInt = VPPAssetsHandler.getInstance().getVppAppSyncStatusFromAppGroupID(this.businessStoreID, appGroupId);
        String status = null;
        if (statusInt == 1) {
            status = "inprogress";
        }
        responseJSON.put("status", (Object)status);
        return responseJSON;
    }
    
    @Override
    public JSONObject syncLicenseStatus(final JSONObject jsonObject) throws Exception {
        this.validateIfStoreFound();
        final JSONObject responseJSON = new JSONObject();
        final Long appId = jsonObject.getLong("app_id");
        final Long appGroupId = jsonObject.getLong("app_group_id");
        this.validateIfAppAvailableForBusinessStore(appId);
        final String status = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("SyncStatus_App=" + appGroupId.toString() + "BusinessStore=" + this.businessStoreID.toString(), 2);
        responseJSON.put("status", (Object)status);
        return responseJSON;
    }
    
    @Override
    public Object getStoreDetails(final JSONObject jsonObject) throws Exception {
        final IOSEnterpriseBusinessStoreModel responseModel = new IOSEnterpriseBusinessStoreModel();
        int totalVppAppCount = 0;
        int meMdmAppType = 0;
        if (this.businessStoreID != null) {
            this.validateIfStoreFound();
            JSONObject vppDetails = new JSONObject();
            vppDetails = VPPTokenDataHandler.getInstance().getVppTokenDetails(this.businessStoreID);
            if (vppDetails.length() > 0) {
                responseModel.setBusinessStoreID(this.businessStoreID);
                final Long expiryTime = (Long)vppDetails.get("EXPIRY_DATE");
                final String expiryDate = MDMUtil.getDate(expiryTime, Boolean.TRUE);
                responseModel.setExpiryDate(expiryDate);
                responseModel.setLocationName((String)vppDetails.opt("LOCATION_NAME"));
                responseModel.setOrganizationName((String)vppDetails.get("ORGANISATION_NAME"));
                final String email = MDBusinessStoreUtil.getBusinessStoreAlertMailAddress(this.businessStoreID);
                responseModel.setNotificationMail(email);
                responseModel.setLicenseType((int)vppDetails.opt("LICENSE_ASSIGN_TYPE"));
                final int orgType = vppDetails.optInt("ORG_TYPE");
                String org = "Apple Business Manager/Apple School Manager";
                if (orgType == 1) {
                    org = "Apple Business Manager";
                }
                else if (orgType == 2) {
                    org = "Apple School Manager";
                }
                responseModel.setOrgType(org);
            }
            final int trashedAppsCount = this.getIOSBusinessStoreApps(Boolean.TRUE).size();
            responseModel.setTrashedAppsCount(trashedAppsCount);
            meMdmAppType = VPPAppMgmtHandler.getInstance().getIOSNativeAgentLicenseType(this.businessStoreID);
            totalVppAppCount = VPPSyncStatusHandler.getInstance().getVppAppCount(this.businessStoreID);
            final Row businessDetailsRow = DBUtil.getRowFromDB("MdBusinessStoreSyncDetails", "BUSINESSSTORE_ID", (Object)this.businessStoreID);
            Long lastSyncTime = null;
            Long nextSyncTime = null;
            final Long lastModifiedBy = (Long)vppDetails.opt("LAST_MODIFIED_BY");
            final Long addedTime = (Long)vppDetails.opt("DB_ADDED_TIME");
            final Long addedBy = (Long)vppDetails.opt("BUSINESSSTORE_ADDED_BY");
            final Long last_modified_time = (Long)vppDetails.opt("DB_UPDATED_TIME");
            if (businessDetailsRow != null) {
                lastSyncTime = (Long)businessDetailsRow.get("LAST_SUCCESSFULL_SYNC_TIME");
                nextSyncTime = (Long)businessDetailsRow.get("STORE_NEXT_SYNC");
            }
            responseModel.setTotalAppsCount(totalVppAppCount);
            if (lastSyncTime != null) {
                final String lastSyncDate = MDMUtil.getDate(lastSyncTime, Boolean.TRUE);
                responseModel.setLastSyncTime(lastSyncDate);
            }
            if (nextSyncTime != null) {
                final String nextSyncDate = MDMUtil.getDate(nextSyncTime, Boolean.TRUE);
                responseModel.setNextSyncTime(nextSyncDate);
            }
            if (addedTime != null) {
                responseModel.setAddedTime(MDMUtil.getDate(addedTime, Boolean.TRUE));
            }
            if (lastModifiedBy != null) {
                responseModel.setLastModifiedByUserName(DMUserHandler.getUserNameFromUserID(lastModifiedBy));
            }
            if (last_modified_time != null) {
                responseModel.setLastModifiedTime(MDMUtil.getDate(last_modified_time, Boolean.TRUE));
            }
            if (addedBy != null) {
                responseModel.setAddedByUserName(DMUserHandler.getUserNameFromUserID(addedBy));
            }
        }
        responseModel.setMeMDMAppType(meMdmAppType);
        final int nonVppAppsCount = VPPAppMgmtHandler.getInstance().getVPPAppsCountNotPurchasedFromPortal(this.customerID);
        responseModel.setNonVppAppsCount(nonVppAppsCount);
        return responseModel;
    }
    
    @Override
    public JSONObject getAllStoreDetails() throws Exception {
        final JSONObject response = new JSONObject();
        response.put("VPP_TOKEN_DETAILS", (Object)VPPTokenDataHandler.getInstance().getAllVppTokenDetailsForCustomer(this.customerID));
        final int nonVppAppsCount = VPPAppMgmtHandler.getInstance().getVPPAppsCountNotPurchasedFromPortal(this.customerID);
        response.put("NON_VPP_APP_COUNT", nonVppAppsCount);
        response.put("trash_count", new AppTrashModeHandler().getAccountAppsInTrash(this.platformType, this.customerID));
        return response;
    }
    
    @Override
    public JSONObject verifyAccountRemoval() throws Exception {
        final List<Long> accountApps = this.getIOSBusinessStoreApps(Boolean.FALSE);
        final int count = accountApps.size();
        JSONObject responseJSON = new JSONObject();
        try {
            if (count > 0) {
                final Long[] accountAppsArray = accountApps.toArray(new Long[0]);
                final Long[] profileIds = AppsUtil.getInstance().getProfileIDSFromAppGroup(accountAppsArray);
                final HashMap params = new HashMap();
                params.put("appGroupIds", accountApps);
                params.put("CustomerID", this.customerID);
                params.put("profileIds", Arrays.asList(profileIds));
                params.put("packageIds", new ArrayList());
                final AppTrashModeHandler appTrashModeHandler = new AppTrashModeHandler();
                final JSONObject errorMessages = appTrashModeHandler.checkMoveAppsToTrashFesability(params);
                responseJSON = MDMAppMgmtHandler.getInstance().getMaxDistributedCountForBusinessStoreApps(accountAppsArray, this.businessStoreID);
                if (errorMessages != null && errorMessages.has("ErrorMessage")) {
                    responseJSON.put("error_message", errorMessages.get("ErrorMessage"));
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while fetching verification msg for account removal", e);
        }
        return responseJSON;
    }
    
    @Override
    public JSONObject syncStore(final JSONObject jsonObject) throws Exception {
        final JSONObject response = new JSONObject();
        final JSONObject body = jsonObject.optJSONObject("msg_body");
        Boolean removeVppTokenFromOther = Boolean.FALSE;
        this.validateIfStoreFound();
        if (body != null) {
            removeVppTokenFromOther = body.optBoolean("remove_from_other_mdm");
        }
        if (removeVppTokenFromOther) {
            VPPTokenDataHandler.getInstance().asyncVppAppData(this.businessStoreID, this.customerID, true);
            MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(this.businessStoreID, -1, "settingClientContext", "", null);
        }
        else {
            VPPTokenDataHandler.getInstance().asyncVppAppData(this.businessStoreID, this.customerID, null);
        }
        response.put("message", (Object)"sync initiated");
        return response;
    }
    
    @Override
    public Object getSyncStoreStatus(final JSONObject jsonObject) throws Exception {
        final IOSBusinessStoreSyncModel syncResponse = new IOSBusinessStoreSyncModel();
        this.validateIfStoreFound();
        final JSONObject syncResponseJSON = new VPPSyncStatusHandler().getSyncStatus(this.businessStoreID);
        if (syncResponseJSON.has("status")) {
            final int status = syncResponseJSON.getInt("status");
            syncResponse.setStatus(status);
            syncResponse.setRemarks(syncResponseJSON.optString("remarks", ""));
            if (syncResponseJSON.has("if_sync_failed")) {
                syncResponse.setIfSyncFailed(syncResponseJSON.getBoolean("if_sync_failed"));
            }
            if (syncResponseJSON.optBoolean("if_licenses_insufficient")) {
                syncResponse.setIfLicensesInsufficient(Boolean.TRUE);
                syncResponse.setAppWithInsufficientLicenses(syncResponseJSON.getInt("apps_with_insufficient_licenses"));
            }
            if (syncResponseJSON.has("other_mdm_hostname")) {
                syncResponse.setOtherMDMHostName(syncResponseJSON.getString("other_mdm_hostname"));
            }
            if (syncResponseJSON.opt("last_sync_time") != null) {
                final String lastSyncTime = MDMUtil.getDate(syncResponseJSON.optLong("last_sync_time"), Boolean.TRUE);
                syncResponse.setLastSyncTime(lastSyncTime);
            }
            if (syncResponseJSON.has("free_to_vpp_apps_count")) {
                syncResponse.setFreeToVPPAppsCount(syncResponseJSON.getInt("free_to_vpp_apps_count"));
            }
            final int trashAppsCount = this.getIOSBusinessStoreApps(Boolean.TRUE).size();
            syncResponse.setTrashedAppsCount(trashAppsCount);
            syncResponse.setTotalAppsCount(syncResponseJSON.getInt("total_apps_count"));
            syncResponse.setCompletedAppsCount(syncResponseJSON.getInt("completed_apps_count"));
            syncResponse.setSuccessfulAppsCount(syncResponseJSON.getInt("successful_apps_count"));
            syncResponse.setFailedAppsCount(syncResponseJSON.getInt("failed_apps_count"));
        }
        return syncResponse;
    }
    
    @Override
    public JSONArray getAllStoreSyncStatus() throws Exception {
        final JSONArray allStoreStatus = new JSONArray();
        final List businessStoreIDList = MDBusinessStoreUtil.getBusinessStoreIDs(this.customerID, IOSStoreHandler.BS_SERVICE_VPP);
        for (int i = 0; i < businessStoreIDList.size(); ++i) {
            this.businessStoreID = businessStoreIDList.get(i);
            final JSONObject storeStatusJSON = new VPPSyncStatusHandler().getSyncStatus(this.businessStoreID);
            storeStatusJSON.put("BUSINESSSTORE_ID", (Object)this.businessStoreID);
            allStoreStatus.put((Object)storeStatusJSON);
        }
        return allStoreStatus;
    }
    
    @Override
    public void clearSyncStoreStatus() {
        MDBusinessStoreUtil.updateStoreSyncStatus(this.businessStoreID, 0);
    }
    
    private void validateIfAppAvailableForBusinessStore(final Long packageID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
        selectQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
        selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
        selectQuery.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
        final Criteria appCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "PACKAGE_ID"), (Object)packageID, 0);
        final Criteria businessCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)this.businessStoreID, 0);
        selectQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"));
        selectQuery.setCriteria(appCriteria.and(businessCriteria));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            return;
        }
        this.logger.log(Level.SEVERE, "The app with pkgID {0} is not present in the vpp token with businessStoreID {1}", new Object[] { packageID, businessCriteria });
        throw new APIHTTPException("COM0004", new Object[0]);
    }
    
    @Override
    public void validateAppToBusinessStoreProps(final List packageList, final Properties pkgToBusinessProps, final Properties appToBusinessProps) throws Exception {
        DMDataSetWrapper ds = null;
        final List tempPackageList = new ArrayList(packageList);
        final List availablePackageList = new ArrayList();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
        selectQuery.addJoin(new Join("MdVppAsset", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
        selectQuery.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedBusinessStore", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        Criteria packageToBusinessCrit = null;
        final Column packageColumn = new Column("MdPackageToAppGroup", "PACKAGE_ID");
        final Column profileColumn = new Column("Profile", "PROFILE_ID");
        final Column businessStoreColumn = new Column("ManagedBusinessStore", "BUSINESSSTORE_ID");
        selectQuery.addSelectColumn(packageColumn);
        selectQuery.addSelectColumn(profileColumn);
        selectQuery.addSelectColumn(businessStoreColumn);
        final List groupByList = new ArrayList();
        groupByList.add(packageColumn);
        groupByList.add(profileColumn);
        groupByList.add(businessStoreColumn);
        final GroupByClause groupByClause = new GroupByClause(groupByList);
        selectQuery.setGroupByClause(groupByClause);
        final List<List> packageSplitList = MDMUtil.getInstance().splitListIntoSubLists(packageList, 10);
        for (final List tempList : packageSplitList) {
            for (int i = 0; i < tempList.size(); ++i) {
                final Long packageID = tempList.get(i);
                final Long businessStoreID = ((Hashtable<K, Long>)pkgToBusinessProps).get(packageID);
                if (packageToBusinessCrit == null) {
                    packageToBusinessCrit = new Criteria(packageColumn, (Object)packageID, 0).and(new Criteria(businessStoreColumn, (Object)businessStoreID, 0));
                }
                else {
                    packageToBusinessCrit = packageToBusinessCrit.or(new Criteria(packageColumn, (Object)packageID, 0).and(new Criteria(businessStoreColumn, (Object)businessStoreID, 0)));
                }
            }
            selectQuery.setCriteria(packageToBusinessCrit);
            ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                final Long profileID = (Long)ds.getValue("PROFILE_ID");
                final Long businessStoreID2 = (Long)ds.getValue("BUSINESSSTORE_ID");
                final Long packageID2 = (Long)ds.getValue("PACKAGE_ID");
                ((Hashtable<Long, Long>)appToBusinessProps).put(profileID, businessStoreID2);
                availablePackageList.add(packageID2);
            }
        }
        tempPackageList.removeAll(availablePackageList);
        if (!tempPackageList.isEmpty()) {
            for (int j = 0; j < tempPackageList.size(); ++j) {
                this.logger.log(Level.SEVERE, "The app with packageID = {0} is not available for businessStoreID {1}", new Object[] { tempPackageList.get(j), ((Hashtable<K, Object>)pkgToBusinessProps).get(tempPackageList.get(j)) });
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public JSONObject removeStoreDetails(final JSONObject message) throws Exception {
        JSONObject responseJSON;
        try {
            this.validateIfStoreFound();
            responseJSON = VPPAppMgmtHandler.getInstance().removeVPPTokenDetails(this.businessStoreID, this.customerID);
            final String userName = message.getString("userName");
            final int orgType = responseJSON.getInt("ORG_TYPE");
            String org = "Apple Business Manager/Apple School Manager";
            if (orgType == 1) {
                org = "Apple Business Manager";
            }
            else if (orgType == 2) {
                org = "Apple School Manager";
            }
            String locationName = (String)responseJSON.opt("LOCATION_NAME");
            if (locationName == null) {
                locationName = (String)responseJSON.get("ORGANISATION_NAME");
            }
            final String remarks = MDMI18N.getI18Nmsg("mdm.vpp.removed", new Object[] { org, locationName });
            MDMEventLogHandler.getInstance().MDMEventLogEntry(72505, null, userName, remarks, "", this.customerID);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Couldnt remove VPP account : ", e);
            throw e;
        }
        return responseJSON;
    }
    
    @Override
    public void modifyStoreDetails(final JSONObject message) throws Exception {
        this.validateIfStoreFound();
        final JSONObject msgBody = message.getJSONObject("msg_body");
        final Long userID = message.getLong("userID");
        final String userName = message.getString("userName");
        final JSONObject response = this.handleVPPTokenUpload(msgBody, userID);
        String locationName = (String)response.opt("LOCATION_NAME");
        if (locationName == null) {
            locationName = (String)response.get("ORGANISATION_NAME");
        }
        final int orgType = response.optInt("ORG_TYPE", 0);
        String org = "Apple Business Manager/Apple School Manager";
        if (orgType == 1) {
            org = "Apple Business Manager";
        }
        else if (orgType == 2) {
            org = "Apple School Manager";
        }
        if (response.has("if_new_token_uploaded") && response.getBoolean("if_new_token_uploaded")) {
            final String remarks = MDMI18N.getI18Nmsg("mdm.vpp.replaced", new Object[] { org, locationName });
            MDMEventLogHandler.getInstance().MDMEventLogEntry(72513, null, userName, remarks, "", this.customerID);
        }
        else {
            final String remarks = MDMI18N.getI18Nmsg("mdm.vpp.modified", new Object[] { org, locationName });
            MDMEventLogHandler.getInstance().MDMEventLogEntry(72504, null, userName, remarks, "", this.customerID);
        }
    }
    
    private JSONObject handleVPPTokenUpload(final JSONObject msgBody, final Long userID) throws Exception {
        Properties properties = new Properties();
        final JSONObject response = new JSONObject();
        final FileFacade fileFacade = new FileFacade();
        int licenseType = 0;
        if (msgBody.has("license_assign_type")) {
            licenseType = (int)msgBody.get("license_assign_type");
            if (licenseType != 1 && licenseType != 2) {
                throw new APIHTTPException("COM0005", new Object[] { "License type" });
            }
        }
        if (this.businessStoreID == null && licenseType == 0) {
            licenseType = 2;
        }
        else if (this.businessStoreID != null && licenseType == 0) {
            licenseType = VPPAppMgmtHandler.getInstance().getVppGlobalAssignmentType(this.customerID, this.businessStoreID);
        }
        if (this.businessStoreID == null || msgBody.has("vpp_token_file")) {
            final Long fileId = Long.valueOf(msgBody.get("vpp_token_file").toString());
            final String tempFilePathDM = fileFacade.validateContentTypeGetFilePath(fileId, this.customerID, "text/plain");
            properties = VPPTokenDataHandler.getInstance().decodeAndValidateVPPToken(tempFilePathDM, userID, this.customerID, this.businessStoreID);
            ((Hashtable<String, Long>)properties).put("CUSTOMER_ID", this.customerID);
            final String errorMessage = ((Hashtable<K, String>)properties).get("errorMessage");
            if (errorMessage != null) {
                final JSONObject errorJson = new JSONObject();
                errorJson.put("errorMessage", (Object)errorMessage);
                this.handleVPPErrors(properties, errorMessage);
            }
            else {
                response.put("expired", false);
                ((Hashtable<String, Long>)properties).put("USER_ID", userID);
                if (this.businessStoreID == null) {
                    if (licenseType == 1) {
                        this.logger.log(Level.INFO, "License Type is 1 for businessStore: {0}. Must be internal API support");
                    }
                    ((Hashtable<String, Integer>)properties).put("LICENSE_ASSIGN_TYPE", licenseType);
                }
                else if (licenseType == 1) {
                    this.logger.log(Level.INFO, "License Type changed to 1 for businessStore: {0}. Must be internal API support");
                }
            }
            response.put("if_new_token_uploaded", true);
        }
        else if (this.businessStoreID != null && !msgBody.has("vpp_token_file")) {
            final Row businessStoreRow = DBUtil.getRowFromDB("ManagedBusinessStore", "BUSINESSSTORE_ID", (Object)this.businessStoreID);
            final String uID = (String)businessStoreRow.get("BUSINESSSTORE_IDENTIFICATION");
            if (licenseType == 1) {
                this.logger.log(Level.INFO, "License Type changed to 1 for businessStore: {0}. Must be internal API support");
            }
            ((Hashtable<String, Integer>)properties).put("LICENSE_ASSIGN_TYPE", licenseType);
            ((Hashtable<String, Long>)properties).put("USER_ID", userID);
            ((Hashtable<String, String>)properties).put("UNIQUE_ID", uID);
        }
        String remarks = null;
        String remarksParams = null;
        int errorCode = -1;
        if (properties.containsKey("warning")) {
            final String warning = ((Hashtable<K, Object>)properties).get("warning").toString();
            if (warning.equalsIgnoreCase("differentClientContext")) {
                remarks = ((Hashtable<K, Object>)properties).get("warning").toString();
                remarksParams = ((Hashtable<K, Object>)properties).get("clientContext").toString();
                errorCode = 888801;
            }
        }
        ((Hashtable<String, Long>)properties).put("CUSTOMER_ID", this.customerID);
        if (this.businessStoreID != null) {
            ((Hashtable<String, Long>)properties).put("BUSINESSSTORE_ID", this.businessStoreID);
        }
        VPPTokenDataHandler.getInstance().addorUpdateVppTokenDetails(properties);
        if (this.businessStoreID == null) {
            MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(this.businessStoreID = ((Hashtable<K, Long>)properties).get("BUSINESSSTORE_ID"), errorCode, remarks, remarksParams, 0);
            String schedulerName = "MDMAppSyncTaskScheduler";
            Long nextExecutionTimeForSchedule = ApiFactoryProvider.getSchedulerAPI().getNextExecutionTimeForSchedule(schedulerName);
            if (nextExecutionTimeForSchedule == null) {
                schedulerName = "MDMAppSyncTaskCustomTemplate";
                Label_0675: {
                    if (!CustomerInfoUtil.getInstance().isMSP()) {
                        CustomerInfoUtil.getInstance();
                        if (!CustomerInfoUtil.isSAS()) {
                            break Label_0675;
                        }
                    }
                    schedulerName = schedulerName + "__" + this.customerID;
                }
                nextExecutionTimeForSchedule = ApiFactoryProvider.getSchedulerAPI().getNextExecutionTimeForSchedule(schedulerName);
            }
            MDBusinessStoreUtil.setInitialSyncDetails(this.businessStoreID);
            MDBusinessStoreUtil.updateStoreNextSyncTime(this.businessStoreID, nextExecutionTimeForSchedule);
        }
        if (msgBody.has("email_address")) {
            final String emailId = (String)msgBody.get("email_address");
            if (emailId != null && emailId != "") {
                MDBusinessStoreUtil.addOrUpdateStoreAlertMail(this.businessStoreID, emailId);
            }
        }
        if (this.businessStoreID != null && msgBody.has("vpp_token_file") && errorCode != 888801) {
            VPPTokenDataHandler.getInstance().asyncVppAppData(this.businessStoreID, this.customerID, false);
        }
        final JSONObject vppTokenDetails = VPPTokenDataHandler.getInstance().getVppTokenDetails(this.businessStoreID);
        response.put("LOCATION_NAME", (Object)vppTokenDetails.optString("LOCATION_NAME"));
        response.put("ORGANISATION_NAME", (Object)vppTokenDetails.optString("ORGANISATION_NAME"));
        response.put("ORGANISATION_NAME", ((Hashtable<K, Object>)properties).get("ORGANISATION_NAME"));
        if (vppTokenDetails.has("ORG_TYPE")) {
            response.put("ORG_TYPE", vppTokenDetails.getInt("ORG_TYPE"));
        }
        response.put("BUSINESSSTORE_ID", (Object)this.businessStoreID);
        return response;
    }
    
    private void handleVPPErrors(final Properties properties, final String errorMessage) {
        if (errorMessage.equalsIgnoreCase("mismatch")) {
            final JSONObject oldAndNewVPPDetails = ((Hashtable<K, JSONObject>)properties).get("oldAndNewVPPDetails");
            final String oldAccountName = oldAndNewVPPDetails.optString("oldAccountName");
            final String newAccountName = oldAndNewVPPDetails.optString("newAccountName");
            final String newLocationName = oldAndNewVPPDetails.optString("newLocationName");
            final String oldLocationName = oldAndNewVPPDetails.optString("oldLocationName");
            if (oldLocationName != null) {
                if (newLocationName != null) {
                    throw new APIHTTPException("APP0013", new Object[] { oldLocationName, newLocationName });
                }
                throw new APIHTTPException("APP0009", new Object[] { "unknown error" });
            }
            else {
                if (oldLocationName == null && newLocationName != null) {
                    throw new APIHTTPException("APP0009", new Object[] { "unknown error" });
                }
                throw new APIHTTPException("APP0012", new Object[] { oldAccountName, newAccountName });
            }
        }
        else {
            if (errorMessage.equalsIgnoreCase("expired")) {
                throw new APIHTTPException("APP0010", new Object[] { "expired" });
            }
            if (errorMessage.equalsIgnoreCase("revoked")) {
                throw new APIHTTPException("APP0011", new Object[] { "revoked" });
            }
            if (errorMessage.equalsIgnoreCase("alreadyUsedByOtherCustomer")) {
                throw new APIHTTPException("APP0008", new Object[] { "alreadyUsedByOtherMSPCustomer" });
            }
            if (errorMessage.equalsIgnoreCase("unknownError")) {
                throw new APIHTTPException("APP0009", new Object[] { "unknown error" });
            }
            if (errorMessage.equalsIgnoreCase("alreadyUsedByCustomer")) {
                throw new APIHTTPException("APP0036", new Object[0]);
            }
            if (errorMessage.equalsIgnoreCase("notReachable")) {
                throw new APIHTTPException("APP0037", new Object[0]);
            }
        }
    }
    
    @Override
    public JSONObject getStorePromoStatus(final JSONObject jsonObject) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("promo", false);
        JSONObject tempJSON = new JSONObject();
        tempJSON = this.getAllStoreDetails();
        final JSONArray resultArray = tempJSON.getJSONArray("VPP_TOKEN_DETAILS");
        JSONObject resultJSON = new JSONObject();
        resultJSON = resultArray.getJSONObject(0);
        if (!resultJSON.has("BUSINESSSTORE_ID")) {
            responseJSON.put("promo", true);
        }
        return responseJSON;
    }
    
    public void removeAppLicensesForResources(final JSONObject appToDeviceLicenseDetails, final List<Long> configSourceList, final Long customerID, final Boolean isAllSourceDisassociation) {
        try {
            if (appToDeviceLicenseDetails.length() > 0) {
                final List profileList = new ArrayList();
                profileList.addAll(appToDeviceLicenseDetails.keySet());
                this.logger.log(Level.SEVERE, "Starting to remove licenses for apps with profileIDs = {0}", new Object[] { profileList });
                final Properties profileToAppDetails = ProfileUtil.getInstance().getProfileToAppDetailsMap(profileList);
                for (int i = 0; i < profileList.size(); ++i) {
                    final JSONObject businessDetails = appToDeviceLicenseDetails.getJSONObject((String)profileList.get(i));
                    final Set businessStoreSet = businessDetails.keySet();
                    final Iterator iterator = businessStoreSet.iterator();
                    while (iterator.hasNext()) {
                        final Long businessStoreID = Long.parseLong(iterator.next().toString());
                        final List resourceList = businessDetails.getJSONArray(String.valueOf(businessStoreID)).toList();
                        final Long profileID = Long.parseLong(profileList.get(i).toString());
                        if (!isAllSourceDisassociation) {
                            this.logger.log(Level.INFO, "Checking if the vpp app with profileID {0} with businessStoreID {1} is safe to remove from devices {2},", new Object[] { profileID, businessStoreID, resourceList });
                            final List<Long> tempList = new MDMResourceToProfileDeploymentConfigHandler().getResListForAppWithSameLicenseInOtherSource(profileID, businessStoreID, resourceList, configSourceList);
                            resourceList.removeAll(tempList);
                            if (resourceList.isEmpty() && !tempList.isEmpty()) {
                                this.logger.log(Level.INFO, "License for the app with profileID {0} cannot be removed for the devices {1} as it has same token license businessStoreID {2} in other configs sources(groups)", new Object[] { profileID, resourceList, businessStoreID });
                            }
                        }
                        if (!resourceList.isEmpty()) {
                            final Properties tempProps = ((Hashtable<K, Properties>)profileToAppDetails).get(profileID);
                            final String appStoreID = ((Hashtable<K, Object>)tempProps).get("STORE_ID").toString();
                            final Long appGroupID = Long.parseLong(((Hashtable<K, Object>)tempProps).get("APP_GROUP_ID").toString());
                            final JSONObject disassociateAppDetailsJson = new JSONObject();
                            disassociateAppDetailsJson.put("PROFILE_ID", (Object)profileID);
                            disassociateAppDetailsJson.put("APP_GROUP_ID", (Object)appGroupID);
                            disassociateAppDetailsJson.put("appStoreId", (Object)appStoreID);
                            disassociateAppDetailsJson.put("CUSTOMER_ID", (Object)customerID);
                            disassociateAppDetailsJson.put("notifyDisassociation", true);
                            final Object resourceListObj = resourceList;
                            disassociateAppDetailsJson.put("deviceIdList", resourceListObj);
                            VPPAppDisassociationHandler.getInstance().disassociateVppApps(disassociateAppDetailsJson, businessStoreID);
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in removeLicenseForApps");
        }
    }
    
    private Properties getAssociatedResourceCountForBusinessStoreApps(final int licenseType, final Long businessStoreID) {
        final Properties appProps = new Properties();
        try {
            final SelectQuery selectQuery = AppsUtil.getInstance().getQueryForManagedDeviceListWithAppDistributedInMDM();
            Criteria depConfigJoinCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)Column.getColumn("MDMResourceToDeploymentConfigs", "RESOURCE_ID"), 0);
            depConfigJoinCriteria = depConfigJoinCriteria.and(new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)Column.getColumn("MDMResourceToDeploymentConfigs", "PROFILE_ID"), 0));
            depConfigJoinCriteria = depConfigJoinCriteria.and(new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            selectQuery.addJoin(new Join("RecentProfileForResource", "MDMResourceToDeploymentConfigs", depConfigJoinCriteria, 2));
            selectQuery.addJoin(new Join("MdAppToGroupRel", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdStoreAssetErrorDetails", new String[] { "STORE_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            Criteria businessStoreAssetJoinCri = new Criteria(Column.getColumn("MDMResourceToDeploymentConfigs", "BUSINESSSTORE_ID"), (Object)Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), 0);
            businessStoreAssetJoinCri = businessStoreAssetJoinCri.and(new Criteria(Column.getColumn("MdStoreAssetErrorDetails", "STORE_ASSET_ID"), (Object)Column.getColumn("MdBusinessStoreToAssetRel", "STORE_ASSET_ID"), 0));
            selectQuery.addJoin(new Join("MDMResourceToDeploymentConfigs", "MdBusinessStoreToAssetRel", businessStoreAssetJoinCri, 2));
            selectQuery.addJoin(new Join("MdBusinessStoreToAssetRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            Column resCountColumn = null;
            if (licenseType == 1) {
                selectQuery.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
                resCountColumn = new Column("ManagedUserToDevice", "MANAGED_USER_ID").distinct().count();
            }
            else {
                resCountColumn = new Column("ManagedDevice", "RESOURCE_ID").distinct().count();
            }
            final List groupByList = new ArrayList();
            final Column appGroupColumn = new Column("MdStoreAssetToAppGroupRel", "APP_GROUP_ID");
            final Column totalLicenseColumn = new Column("MdVppAsset", "TOTAL_LICENSE");
            resCountColumn.setColumnAlias("REQUIRED_LICENSE_COUNT");
            groupByList.add(appGroupColumn);
            groupByList.add(totalLicenseColumn);
            selectQuery.addSelectColumn(appGroupColumn);
            selectQuery.addSelectColumn(totalLicenseColumn);
            selectQuery.setGroupByClause(new GroupByClause(groupByList));
            selectQuery.addSelectColumn(resCountColumn);
            final Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
            selectQuery.setCriteria(criteria);
            final DMDataSetWrapper ds = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (ds.next()) {
                final Long appGroupID = (Long)ds.getValue("APP_GROUP_ID");
                final int totalLicensesCount = (int)ds.getValue("TOTAL_LICENSE");
                final int requiredLicensesCount = (int)ds.getValue("REQUIRED_LICENSE_COUNT");
                final Properties insufficientDetails = new Properties();
                ((Hashtable<String, Integer>)insufficientDetails).put("requiredLicensesCount", requiredLicensesCount);
                ((Hashtable<String, Integer>)insufficientDetails).put("totalLicenseCount", totalLicensesCount);
                ((Hashtable<Long, Properties>)appProps).put(appGroupID, insufficientDetails);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getAssociatedResourceCountForBusinessStoreApps", e);
        }
        return appProps;
    }
    
    @Override
    public Object getAppsFailureDetails(final JSONObject jsonObject) throws Exception {
        this.validateIfStoreFound();
        final JSONArray appDetailsArray = new JSONArray();
        DMDataSetWrapper appDetailsDS = null;
        try {
            final int licenseType = VPPAppMgmtHandler.getInstance().getVppGlobalAssignmentType(this.customerID, this.businessStoreID);
            final Properties appProps = this.getAssociatedResourceCountForBusinessStoreApps(licenseType, this.businessStoreID);
            if (!appProps.isEmpty()) {
                final List appList = new ArrayList();
                appList.add(((Hashtable<Object, V>)appProps).keySet());
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppData"));
                final Join packageToAppDataJoin = new Join("MdPackageToAppData", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
                final Join packageToAppGroupJoin = new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
                sQuery.setCriteria(new Criteria(new Column("MdPackageToAppData", "APP_GROUP_ID"), (Object)((Hashtable<Object, V>)appProps).keySet().toArray(), 8));
                sQuery.addJoin(packageToAppDataJoin);
                sQuery.addJoin(packageToAppGroupJoin);
                sQuery.addSelectColumn(new Column("MdAppGroupDetails", "*"));
                sQuery.addSelectColumn(new Column("MdPackageToAppData", "*"));
                sQuery.addSelectColumn(new Column("MdPackageToAppGroup", "*"));
                appDetailsDS = DMDataSetWrapper.executeQuery((Object)sQuery);
                final List tempAppList = new ArrayList();
                if (appDetailsDS != null) {
                    while (appDetailsDS.next()) {
                        final Long appGroupId = (Long)appDetailsDS.getValue("APP_GROUP_ID");
                        if (!tempAppList.contains(appGroupId)) {
                            tempAppList.add(appGroupId);
                            final Long packageID = (Long)appDetailsDS.getValue("PACKAGE_ID");
                            final String displayImgLoc = (String)appDetailsDS.getValue("DISPLAY_IMAGE_LOC");
                            final Integer packageType = (Integer)appDetailsDS.getValue("PACKAGE_TYPE");
                            final String appName = (String)appDetailsDS.getValue("GROUP_DISPLAY_NAME");
                            int licenseCount = 0;
                            int resourceCount = 0;
                            final Properties insufficientDetails = ((Hashtable<K, Properties>)appProps).get(appGroupId);
                            licenseCount = ((Hashtable<K, Integer>)insufficientDetails).get("totalLicenseCount");
                            resourceCount = ((Hashtable<K, Integer>)insufficientDetails).get("requiredLicensesCount");
                            String packageTypeStr = null;
                            final JSONObject appDetails = new JSONObject();
                            appDetails.put("licenseCount", licenseCount);
                            appDetails.put("displayImageLoc", (Object)displayImgLoc);
                            appDetails.put("resourceCount", resourceCount);
                            appDetails.put("appName", (Object)appName);
                            appDetails.put("appGroupId", (Object)appGroupId);
                            appDetails.put("packageID", (Object)packageID);
                            if (packageType == 0) {
                                packageTypeStr = "Free";
                            }
                            else if (packageType == 1) {
                                packageTypeStr = "Paid";
                            }
                            appDetails.put("packageType", (Object)packageTypeStr);
                            appDetailsArray.put((Object)appDetails);
                        }
                    }
                }
            }
            else {
                this.logger.log(Level.INFO, "No error available for apps in businessstore {0}", this.businessStoreID);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getVppAppsMigrationFailureDetails :{0}", ex);
            throw ex;
        }
        if (appDetailsArray != null && appDetailsArray.length() > 0) {
            return JSONUtil.toJSON("apps", appDetailsArray);
        }
        return null;
    }
    
    @Override
    public void addLicenseRemovalTaskToQueue(final JSONObject appToDeviceLicenseDetails, final Long customerID, final List configSourceList, final boolean isAllSourceDisassociation) throws Exception {
        try {
            final JSONObject jsonQueueData = new JSONObject();
            jsonQueueData.put("configSourceList", (Collection)configSourceList);
            jsonQueueData.put("appToDeviceLicenseDetails", (Object)appToDeviceLicenseDetails);
            jsonQueueData.put("MsgType", (Object)"VppAppLicenseRemovalForDevice");
            jsonQueueData.put("isAllSourceDisassociation", isAllSourceDisassociation);
            final String strData = jsonQueueData.toString();
            final String separator = "\t";
            final String qFileName = "vpp-license-handling-" + System.currentTimeMillis();
            final DCQueueData queueData = new DCQueueData();
            queueData.fileName = qFileName;
            queueData.postTime = System.currentTimeMillis();
            queueData.queueData = strData;
            queueData.customerID = customerID;
            this.logger.log(Level.INFO, "QueueName : {0}{1}AddingToQueue{2}{3}{4}{5}", new Object[] { "vpp-license-handling", separator, separator, queueData.fileName, separator, String.valueOf(System.currentTimeMillis()) });
            final DCQueue queue = DCQueueHandler.getQueue("vpp-license-handling");
            queue.addToQueue(queueData);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in addLicenseRemovalTaskToQueue", e);
            throw e;
        }
    }
    
    @Override
    public void updateStoreSyncKey() {
        MDBusinessStoreUtil.addOrUpdateBusinessStoreParam(IOSStoreHandler.VPP_STORE_SYNC_PARAM, "true", this.businessStoreID);
    }
    
    private List getIOSBusinessStoreApps(final boolean isTrash) {
        final Set set = new HashSet();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdPackageToAppGroup"));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppToCollection", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdBusinessStoreToAssetRel", new String[] { "STORE_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)this.customerID, 0);
        final Criteria businessCriteria = new Criteria(Column.getColumn("MdBusinessStoreToAssetRel", "BUSINESSSTORE_ID"), (Object)this.businessStoreID, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)1, 0);
        final Criteria trashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)isTrash, 0);
        final Criteria criteria = customerCriteria.and(platformCriteria).and(trashCriteria).and(businessCriteria);
        selectQuery.setCriteria(criteria);
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
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in getIOSBusinessStoreApps", e);
        }
        return new ArrayList(set);
    }
    
    @Override
    public String getBusinessStoreName(final Long businessStoreID) throws Exception {
        return VPPTokenDataHandler.getInstance().getVppTokenDetails(businessStoreID).optString("LOCATION_NAME");
    }
    
    @Override
    protected Properties createResourceProps(final String bsIdentifier, final String bsIdentifierForResource) throws DataAccessException {
        if (this.businessStoreID != null) {
            final SelectQuery resQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            resQuery.addJoin(new Join("Resource", "ManagedBusinessStore", new String[] { "RESOURCE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            resQuery.addSelectColumn(Column.getColumn("ManagedBusinessStore", "*"));
            resQuery.addSelectColumn(Column.getColumn("Resource", "*"));
            resQuery.setCriteria(new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)this.businessStoreID, 0));
            final DataObject resDO = MDMUtil.getPersistence().get(resQuery);
            if (!resDO.isEmpty()) {
                final Row resRow = resDO.getFirstRow("Resource");
                final Row mdBusinessStoreRow = resDO.getFirstRow("ManagedBusinessStore");
                final String existingBSIdentifier = (String)mdBusinessStoreRow.get("BUSINESSSTORE_IDENTIFICATION");
                if (MDMStringUtils.isEmpty(existingBSIdentifier) || MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotCheckSameVPPTokenReplace")) {
                    resRow.set("NAME", (Object)bsIdentifier);
                    resRow.set("DOMAIN_NETBIOS_NAME", (Object)(bsIdentifierForResource + BaseStoreHandler.getServiceTypeSuffix(this.serviceType)));
                    mdBusinessStoreRow.set("BUSINESSSTORE_IDENTIFICATION", (Object)bsIdentifier);
                    resDO.updateRow(resRow);
                    resDO.updateRow(mdBusinessStoreRow);
                    MDMUtil.getPersistence().update(resDO);
                }
                else if (!existingBSIdentifier.equals(bsIdentifier)) {
                    this.logger.log(Level.SEVERE, "New BSIdentifier doesn''t match with the old BSIdentifier for BusinessStore: {0}", bsIdentifier);
                    throw new APIHTTPException("COM0014", new Object[0]);
                }
            }
        }
        return super.createResourceProps(bsIdentifier, bsIdentifierForResource);
    }
    
    static {
        IOSStoreHandler.VPP_STORE_SYNC_PARAM = "vppFirstSyncPending";
    }
}
