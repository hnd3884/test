package com.me.mdm.server.apps.blacklist;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Collection;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.util.List;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import org.json.JSONObject;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.ArrayList;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.logging.Logger;

public abstract class BaseBlacklistProcessor implements BlacklistProcessorInterface
{
    protected Logger logger;
    protected Integer platformType;
    public static final String BLACKLIST_ACTION = "Action";
    public static final String BLACKLIST_APPS = "BlacklistApps";
    public static final String WHITELIST_APPS = "WhitelistApps";
    public static final String SUCCESS_LIST = "SuccessList";
    public static final String FAILURE_LIST = "FailureList";
    public static final String APP_LIST = "AppList";
    
    public BaseBlacklistProcessor() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    @Override
    public Object processBlackListRequest(final HashMap params) throws Exception {
        final Long resourceID = params.get("RESOURCE_ID");
        Integer scope = params.get("scope");
        if (scope == null) {
            scope = 0;
        }
        final SelectQuery selectQuery = this.getBlackListAppDetailsQuery(resourceID, scope);
        final JSONArray blacklistList = new JSONArray();
        final JSONArray whitelistList = new JSONArray();
        DMDataSetWrapper dataSet = null;
        final List alreadyAddedList = new ArrayList();
        final List collectionList = new ArrayList();
        dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (dataSet.next()) {
            final Long appGroupID = (Long)dataSet.getValue("APP_GROUP_ID");
            final Long collectionID = (Long)dataSet.getValue("COLLECTION_ID");
            final Boolean markedForDelete = (Boolean)dataSet.getValue("MARKED_FOR_DELETE");
            if (!alreadyAddedList.contains(appGroupID)) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("IDENTIFIER", dataSet.getValue("IDENTIFIER"));
                jsonObject.put("IS_MODERN_APP", dataSet.getValue("IS_MODERN_APP"));
                jsonObject.put("STATUS", dataSet.getValue("STATUS"));
                if (markedForDelete) {
                    whitelistList.put((Object)jsonObject);
                }
                else {
                    blacklistList.put((Object)jsonObject);
                }
                alreadyAddedList.add(appGroupID);
                collectionList.add(collectionID);
            }
        }
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("BlacklistAppCollectionStatus");
        final Criteria resCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria collnCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
        final Criteria removalStatusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)6, 0);
        final Criteria succeededStatusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)1, 0);
        final Criteria scopeCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "SCOPE"), (Object)scope, 0);
        updateQuery.setCriteria(collnCriteria.and(resCriteria).and(succeededStatusCriteria).and(scopeCriteria));
        updateQuery.setUpdateColumn("STATUS", (Object)3);
        MDMUtil.getPersistence().update(updateQuery);
        final UpdateQuery removalUpdate = (UpdateQuery)new UpdateQueryImpl("BlacklistAppCollectionStatus");
        removalUpdate.setCriteria(resCriteria.and(removalStatusCriteria).and(scopeCriteria));
        removalUpdate.setUpdateColumn("STATUS", (Object)7);
        MDMUtil.getPersistence().update(removalUpdate);
        final UpdateQuery notApplicableQuery = (UpdateQuery)new UpdateQueryImpl("BlacklistAppCollectionStatus");
        notApplicableQuery.addJoin(new Join("BlacklistAppCollectionStatus", "CollnToResources", new String[] { "RESOURCE_ID", "COLLECTION_ID" }, new String[] { "RESOURCE_ID", "COLLECTION_ID" }, 2));
        notApplicableQuery.setCriteria(resCriteria.and(new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)8, 0)));
        notApplicableQuery.setUpdateColumn("STATUS", (Object)5);
        MDMUtil.getPersistence().update(notApplicableQuery);
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(Arrays.asList(resourceID), collectionList, 3, "--");
        final JSONObject retVal = new JSONObject();
        retVal.put("BlacklistApps", (Object)blacklistList);
        retVal.put("WhitelistApps", (Object)whitelistList);
        this.logger.log(Level.INFO, "Base called for blacklist command generation ResourceID : {0}  returned json :{1}", new Object[] { resourceID, retVal });
        return retVal;
    }
    
    @Override
    public JSONObject processResponse(final Object param) throws Exception {
        final JSONObject response = (JSONObject)param;
        final Long resourceId = response.getLong("RESOURCE_ID");
        final JSONObject blackListApps = response.getJSONObject("BlacklistApps");
        final JSONObject whiteListApps = response.getJSONObject("WhitelistApps");
        final JSONArray blackSuccessList = blackListApps.getJSONArray("SuccessList");
        final JSONArray blackFailureList = blackListApps.getJSONArray("FailureList");
        final JSONArray whiteSuccessList = whiteListApps.getJSONArray("SuccessList");
        final JSONArray whiteFailureList = whiteListApps.getJSONArray("FailureList");
        Integer scope = (Integer)response.opt("scope");
        if (scope == null) {
            scope = 0;
        }
        final SelectQuery selectQuery = this.getBlackListAppDetailsQuery(resourceId, scope);
        final List collectionList = new ArrayList();
        final HashMap collectionHash = new HashMap();
        DMDataSetWrapper dataSet = null;
        dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (dataSet.next()) {
            final String identifier = (String)dataSet.getValue("IDENTIFIER");
            final Long collectionID = (Long)dataSet.getValue("COLLECTION_ID");
            collectionHash.put(identifier, collectionID);
        }
        final List successList = this.getListFromJSONArray(blackSuccessList, collectionHash);
        successList.addAll(this.getListFromJSONArray(whiteSuccessList, collectionHash));
        final List failureList = this.getListFromJSONArray(blackFailureList, collectionHash);
        failureList.addAll(this.getListFromJSONArray(whiteFailureList, collectionHash));
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(Arrays.asList(resourceId), failureList, 7, "--");
        MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(Arrays.asList(resourceId), successList, 6, "--");
        this.updateBlackListCollectionStatus(resourceId, this.getListFromJSONArray(blackSuccessList, collectionHash), 4, scope);
        this.updateBlackListCollectionStatus(resourceId, this.getListFromJSONArray(blackFailureList, collectionHash), 9, scope);
        this.updateBlackListCollectionStatus(resourceId, this.getListFromJSONArray(whiteSuccessList, collectionHash), 8, scope);
        this.updateBlackListCollectionStatus(resourceId, this.getListFromJSONArray(whiteFailureList, collectionHash), 10, scope);
        return response;
    }
    
    private SelectQuery getBlackListAppDetailsQuery(final Long resourceID, final int scope) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Profile"));
        final Criteria resourceCriteria = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)4, 0);
        final Criteria platformCriteria = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)this.platformType, 0);
        final Criteria scopeCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "SCOPE"), (Object)scope, 0);
        selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "BlacklistAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "RecentProfileForResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "BlacklistAppCollectionStatus", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
        selectQuery.setCriteria(resourceCriteria.and(profileTypeCriteria).and(platformCriteria).and(scopeCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IS_MODERN_APP"));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"));
        return selectQuery;
    }
    
    private List getListFromJSONArray(final JSONArray jsonArray, final HashMap blacklistIdentifier) throws Exception {
        final List list = new ArrayList();
        for (int i = 0; i < jsonArray.length(); ++i) {
            final Long collnID = blacklistIdentifier.get(jsonArray.get(i));
            if (collnID != null) {
                list.add(collnID);
            }
        }
        return list;
    }
    
    private void updateBlackListCollectionStatus(final Long resID, final List collectionIDs, final int status, final int scope) throws DataAccessException {
        if (resID != null && collectionIDs.size() != 0) {
            ((DeviceBlacklistHandler)BaseBlacklistAppHandler.getBlacklistHandler(2)).updateResourcetoBlacklistAppStatus(Arrays.asList(resID), collectionIDs, status, scope);
        }
        this.logger.log(Level.INFO, "Status updated for blacklist command resID : {0} , Collection list : {1}, status : {2}", new Object[] { resID, collectionIDs, status });
    }
    
    protected JSONObject getInProgressStatusForResource(final Long resourceID) throws Exception {
        final JSONObject jsonObject = new JSONObject();
        final JSONArray blacklistList = new JSONArray();
        final JSONArray whitelistList = new JSONArray();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppCollectionStatus"));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "BlacklistAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria resCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 3, 7 }, 8);
        selectQuery.setCriteria(resCriteria.and(statusCriteria));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        DMDataSetWrapper dataSet = null;
        try {
            dataSet = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSet.next()) {
                final String identifier = (String)dataSet.getValue("IDENTIFIER");
                final Integer status = (Integer)dataSet.getValue("STATUS");
                if (status == 3) {
                    blacklistList.put((Object)identifier);
                }
                else {
                    whitelistList.put((Object)identifier);
                }
            }
        }
        catch (final SQLException | QueryConstructionException e) {
            this.logger.log(Level.SEVERE, "Exception in getInProgressStatusForResource() -- ", e);
        }
        jsonObject.put("BlacklistApps", (Object)blacklistList);
        jsonObject.put("WhitelistApps", (Object)whitelistList);
        return jsonObject;
    }
    
    private void removeAppFromMDInstalledApp(final Long resID, final List collectionList) throws DataAccessException {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdInstalledAppResourceRel");
        deleteQuery.addJoin(new Join("MdInstalledAppResourceRel", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        deleteQuery.addJoin(new Join("MdAppToGroupRel", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria resCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resID, 0);
        final Criteria collnCriteria = new Criteria(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"), (Object)collectionList.toArray(), 8);
        deleteQuery.setCriteria(resCriteria.and(collnCriteria));
        MDMUtil.getPersistence().delete(deleteQuery);
    }
}
