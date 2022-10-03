package com.me.mdm.server.apps.multiversion;

import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.List;
import org.json.JSONObject;

public class ChromeAppVersionHandler extends BaseAppVersionHandler
{
    @Deprecated
    @Override
    public void checkIfChannelAllowedToBeMerged(final JSONObject mergeRequestJSON) throws Exception {
        AppVersionDBUtil.getInstance().checkIfChannelAllowedToBeMerged(mergeRequestJSON);
    }
    
    @Override
    public Map<String, List<Long>> removeDevicesContainingLatestVersionOfApp(final List<Long> deviceList, final Long appProfileId, final Long collectionId, final Properties collnProps) {
        final List<Long> clonedDeviceList = new ArrayList<Long>(deviceList);
        final List<Long> removedListOfResources = new ArrayList<Long>();
        final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
        retMap.put("modifiedDeviceList", clonedDeviceList);
        retMap.put("removedDeviceList", removedListOfResources);
        return retMap;
    }
    
    @Override
    public Map<String, List<Long>> removeGroupsContainingLatestVersionOfApp(final List<Long> groupList, final Long appProfileId, final Long collectionId, final Properties collnProps) {
        final List<Long> clonedGroupList = new ArrayList<Long>(groupList);
        final List<Long> removedListOfGroups = new ArrayList<Long>();
        final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
        retMap.put("modifiedGroupList", clonedGroupList);
        retMap.put("removedGroupList", removedListOfGroups);
        return retMap;
    }
    
    @Override
    public Map<String, List<Long>> removeUsersContainingLatestVersionOfApp(final List<Long> userList, final Long appProfileId, final Long collectionId, final Properties collnProps) {
        final List<Long> clonedUserList = new ArrayList<Long>(userList);
        final List<Long> removedListOfResources = new ArrayList<Long>();
        final Map<String, List<Long>> retMap = new HashMap<String, List<Long>>();
        retMap.put("modifiedUserList", clonedUserList);
        retMap.put("removedUserList", removedListOfResources);
        return retMap;
    }
    
    @Override
    public Criteria getDistributedDeviceListForAppCriteria(final Long collectionID, final Long profileID) throws Exception {
        Criteria distributedDeviceListCriteria = null;
        final Long appID = MDMUtil.getInstance().getAppIDFromCollection(collectionID);
        distributedDeviceListCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)appID, 0);
        final Long collectionsReleaseLabelId = AppVersionDBUtil.getInstance().getReleaseLabelIdForAppCollectionId(collectionID);
        final Criteria releaseChannelCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)collectionsReleaseLabelId, 0);
        final Criteria updateCriteria = releaseChannelCriteria.and(new Criteria(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"), (Object)collectionID, 1));
        return distributedDeviceListCriteria;
    }
    
    @Override
    public Criteria getDistributedGroupListForAppCriteria(final Long collectionID, final Long profileID) throws Exception {
        Criteria distributedGroupListCriteria = null;
        final Criteria collectionIDCriteria = new Criteria(Column.getColumn("GroupToProfileHistory", "COLLECTION_ID"), (Object)collectionID, 0);
        final Criteria distributionStatusCriteria = new Criteria(Column.getColumn("GroupToProfileHistory", "COLLECTION_STATUS"), (Object)MDMUtil.getInstance().getCollectionStatusToBeIgnoredForGroupReDistribution().toArray(), 8);
        final Criteria alreadyDistributedCriteria = collectionIDCriteria.and(distributionStatusCriteria);
        final Long collectionsReleaseLabelId = AppVersionDBUtil.getInstance().getReleaseLabelIdForAppCollectionId(collectionID);
        final Criteria releaseChannelCriteria = new Criteria(Column.getColumn("AppCollnToReleaseLabelHistory", "RELEASE_LABEL_ID"), (Object)collectionsReleaseLabelId, 0);
        final Criteria updateCriteria = releaseChannelCriteria.and(new Criteria(Column.getColumn("RecentProfileForGroup", "COLLECTION_ID"), (Object)collectionID, 1));
        distributedGroupListCriteria = alreadyDistributedCriteria;
        return distributedGroupListCriteria;
    }
    
    @Override
    protected Criteria getPlatformCriteria() {
        return new Criteria(new Column("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)4, 0);
    }
    
    @Override
    public void handleDBChangesForAppApproval(final JSONObject requestJSON) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public JSONObject getLowerVersionAppsThanGiveApp(final JSONObject requestJSON) throws Exception {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public JSONObject getHigherVersionAppsThanGivenApps(final JSONObject requestJSON) throws Exception {
        throw new UnsupportedOperationException();
    }
}
