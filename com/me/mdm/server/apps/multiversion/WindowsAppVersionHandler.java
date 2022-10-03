package com.me.mdm.server.apps.multiversion;

import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.me.mdm.api.APIUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.Map;
import java.util.Properties;
import java.util.List;

public class WindowsAppVersionHandler extends BaseAppVersionHandler
{
    @Override
    public Map<String, List<Long>> removeDevicesContainingLatestVersionOfApp(final List<Long> deviceList, final Long appProfileId, final Long collectionId, final Properties collnProps) throws Exception {
        final Boolean isStoreApp = AppsUtil.getInstance().isBusinessStoreApp(collectionId);
        if (isStoreApp) {
            final List<Long> clonedDeviceList = new ArrayList<Long>(deviceList);
            final List<Long> removedListOfResources = new ArrayList<Long>();
            final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
            retMap.put("modifiedDeviceList", clonedDeviceList);
            retMap.put("removedDeviceList", removedListOfResources);
            return retMap;
        }
        return super.removeDevicesContainingLatestVersionOfApp(deviceList, appProfileId, collectionId, collnProps);
    }
    
    @Override
    public Map<String, List<Long>> removeGroupsContainingLatestVersionOfApp(final List<Long> groupList, final Long appProfileId, final Long collectionId, final Properties collnProps) throws Exception {
        final Boolean isStoreApp = AppsUtil.getInstance().isBusinessStoreApp(collectionId);
        if (isStoreApp) {
            final List<Long> clonedGroupList = new ArrayList<Long>(groupList);
            final List<Long> removedListOfGroups = new ArrayList<Long>();
            final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
            retMap.put("modifiedGroupList", clonedGroupList);
            retMap.put("removedGroupList", removedListOfGroups);
            return retMap;
        }
        return super.removeGroupsContainingLatestVersionOfApp(groupList, appProfileId, collectionId, collnProps);
    }
    
    @Override
    public Map<String, List<Long>> removeUsersContainingLatestVersionOfApp(final List<Long> userList, final Long appProfileId, final Long collectionId, final Properties collnProps) throws Exception {
        final Boolean isStoreApp = AppsUtil.getInstance().isBusinessStoreApp(collectionId);
        if (isStoreApp) {
            final List<Long> clonedUserList = new ArrayList<Long>(userList);
            final List<Long> removedListOfResources = new ArrayList<Long>();
            final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
            retMap.put("modifiedUserList", clonedUserList);
            retMap.put("removedUserList", removedListOfResources);
            return retMap;
        }
        return super.removeUsersContainingLatestVersionOfApp(userList, appProfileId, collectionId, collnProps);
    }
    
    @Override
    @Deprecated
    public void checkIfChannelAllowedToBeMerged(final JSONObject mergeRequestJSON) throws Exception {
        AppVersionDBUtil.getInstance().checkIfChannelAllowedToBeMerged(mergeRequestJSON);
    }
    
    @Override
    public Criteria getDistributedDeviceListForAppCriteria(final Long collectionID, final Long profileID) throws Exception {
        Criteria distributedDeviceCriteria = null;
        final Boolean isStroreApp = AppsUtil.getInstance().isBusinessStoreApp(collectionID);
        if (isStroreApp) {
            final Criteria appAlreadyDistributedCriteria = new Criteria(new Column("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionID, 0);
            final Long collectionsReleaseLabelId = AppVersionDBUtil.getInstance().getReleaseLabelIdForAppCollectionId(collectionID);
            final Criteria releaseChannelCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)collectionsReleaseLabelId, 0);
            final Criteria updateCriteria = releaseChannelCriteria.and(new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionID, 1));
            distributedDeviceCriteria = appAlreadyDistributedCriteria.or(updateCriteria);
            return appAlreadyDistributedCriteria;
        }
        return super.getDistributedDeviceListForAppCriteria(collectionID, profileID);
    }
    
    @Override
    public Criteria getDistributedGroupListForAppCriteria(final Long collectionID, final Long profileID) throws Exception {
        Criteria distributedGroupListCriteria = null;
        final Boolean isStoreApp = AppsUtil.getInstance().isBusinessStoreApp(collectionID);
        if (isStoreApp) {
            final Criteria appAlreadyDistributedCriteria = new Criteria(new Column("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionID, 0);
            final Long collectionReleaseLabelID = AppVersionDBUtil.getInstance().getReleaseLabelIdForAppCollectionId(collectionID);
            final Criteria releaseChannelCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)collectionReleaseLabelID, 0);
            final Criteria updateCriteria = releaseChannelCriteria.and(new Criteria(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionID, 1));
            distributedGroupListCriteria = updateCriteria.or(appAlreadyDistributedCriteria);
            return appAlreadyDistributedCriteria;
        }
        return super.getDistributedGroupListForAppCriteria(collectionID, profileID);
    }
    
    @Override
    protected Criteria getBundleIdCriteria(final String identifier) {
        return new Criteria(new Column("MdAppGroupDetails", "IDENTIFIER"), (Object)identifier, 10, (boolean)Boolean.FALSE);
    }
    
    @Override
    protected Criteria getPlatformCriteria() {
        return new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)3, 0);
    }
    
    @Override
    public void handleDBChangesForAppApproval(final JSONObject requestJSON) throws Exception {
        final Long collectionID = requestJSON.getLong("COLLECTION_ID");
        final Boolean isBusinessStoreApp = AppsUtil.getInstance().isBusinessStoreApp(collectionID);
        if (isBusinessStoreApp) {
            throw new UnsupportedOperationException();
        }
        super.handleDBChangesForAppApproval(requestJSON);
    }
    
    @Override
    public JSONObject getLowerVersionAppsThanGiveApp(final JSONObject requestJSON) throws Exception {
        final Long packageId = APIUtil.getResourceID(requestJSON, "app_id");
        final Long labelId = APIUtil.getResourceID(requestJSON, "label_id");
        final HashMap appDetailsMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageId, labelId);
        final Boolean isPurchasedFromPortal = appDetailsMap.get("IS_PURCHASED_FROM_PORTAL");
        if (isPurchasedFromPortal) {
            throw new UnsupportedOperationException();
        }
        return super.getLowerVersionAppsThanGiveApp(requestJSON);
    }
    
    @Override
    public JSONObject getHigherVersionAppsThanGivenApps(final JSONObject requestJSON) throws Exception {
        final Long packageId = APIUtil.getResourceID(requestJSON, "app_id");
        final Long labelId = APIUtil.getResourceID(requestJSON, "label_id");
        final HashMap appDetailsMap = MDMAppMgmtHandler.getInstance().getAppDetailsMap(packageId, labelId);
        final Boolean isPurchasedFromPortal = appDetailsMap.get("IS_PURCHASED_FROM_PORTAL");
        if (isPurchasedFromPortal) {
            throw new UnsupportedOperationException();
        }
        return super.getHigherVersionAppsThanGivenApps(requestJSON);
    }
    
    @Override
    public void validateAppVersionForUpload(final JSONObject uploadedAppDetails) throws Exception {
        final Boolean isMSI = uploadedAppDetails.optBoolean("is_msi", (boolean)Boolean.FALSE);
        if (!isMSI) {
            super.validateAppVersionForUpload(uploadedAppDetails);
        }
    }
}
