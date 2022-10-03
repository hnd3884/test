package com.me.mdm.server.tree.apidatahandler;

import com.adventnet.ds.query.Range;
import org.json.JSONException;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.ArrayList;
import com.adventnet.ds.query.GroupByColumn;
import org.json.JSONArray;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import java.util.List;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.core.management.ManagementUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.me.mdm.api.paging.PagingUtil;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public abstract class ApiListViewDataHandler
{
    public static final String ASSOCIATED_STRING = "associated";
    public static final String AVAILABLE_STRING = "all";
    public static final String YET_TO_APPLY = "yet_to_apply";
    public static final String SUCCESSFULL_APPLIED = "successfull_applied";
    public static final int DOCUMENT_FILTER_VALUE = 101;
    public static final int GROUP_FILTER_VALUE = 102;
    public static final int PICKLIST_APP_FILTER_VALUE = 103;
    protected static Logger logger;
    private static ApiListViewDataHandler apiListViewDataHandler;
    protected SelectQuery selectQuery;
    protected JSONObject requestJson;
    
    public ApiListViewDataHandler() {
        this.selectQuery = null;
        this.requestJson = null;
    }
    
    public static ApiListViewDataHandler getInstance(final Integer filterType) {
        switch (filterType) {
            case 1: {
                ApiListViewDataHandler.apiListViewDataHandler = new ApiProfileListViewDataHandler();
                break;
            }
            case 2: {
                ApiListViewDataHandler.apiListViewDataHandler = new ApiAppListViewDataHandler();
                break;
            }
            case 3: {
                ApiListViewDataHandler.apiListViewDataHandler = new ApiOsUpdateListViewDataHandler();
                break;
            }
            case 4: {
                ApiListViewDataHandler.apiListViewDataHandler = new ApiBlackListListViewDataHandler();
                break;
            }
            case 5: {
                ApiListViewDataHandler.apiListViewDataHandler = new ApiGeoFenceViewDataHandler();
                break;
            }
            case 101: {
                ApiListViewDataHandler.apiListViewDataHandler = new ApiDocListViewDataHandler();
                break;
            }
            case 102: {
                ApiListViewDataHandler.apiListViewDataHandler = new ApiGroupListViewDataHandler();
                break;
            }
            case 103: {
                ApiListViewDataHandler.apiListViewDataHandler = new ApiKioskAppListViewDataHandler();
                break;
            }
            default: {
                throw new APIHTTPException("COM0008", new Object[] { filterType });
            }
        }
        return ApiListViewDataHandler.apiListViewDataHandler;
    }
    
    public JSONObject getFilterValues(final JSONObject message, final PagingUtil pagingUtil) throws APIHTTPException {
        ApiListViewDataHandler.logger.log(Level.INFO, "Get Filtered values for the Profile with messages {0}", message);
        this.requestJson = message;
        try {
            final Boolean selectAllValue = this.requestJson.optBoolean("selectAll");
            this.getSelectQuery();
            this.setCriteria();
            final SelectQuery countQuery = (SelectQuery)this.selectQuery.clone();
            final Column countColumn = countQuery.getSelectColumns().get(0);
            while (countQuery.getSelectColumns().size() > 0) {
                countQuery.removeSelectColumn(0);
            }
            while (countQuery.getSortColumns().size() > 0) {
                countQuery.removeSortColumn(0);
            }
            countQuery.setGroupByClause((GroupByClause)null);
            int count = DBUtil.getRecordCount(countQuery, countColumn.getTableAlias(), countColumn.getColumnName());
            this.setRange(message);
            final JSONObject resultJSON = this.fetchResultObject();
            if (resultJSON.has("count")) {
                count = resultJSON.getInt("count");
                resultJSON.remove("count");
            }
            if (count > 0) {
                final JSONObject meta = new JSONObject();
                meta.put("total_record_count", count);
                resultJSON.put("metadata", (Object)meta);
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                if (pagingJSON != null && !selectAllValue) {
                    resultJSON.put("paging", (Object)pagingJSON);
                }
            }
            return resultJSON;
        }
        catch (final Exception ex) {
            ApiListViewDataHandler.logger.log(Level.SEVERE, "Exception while getting filtered values of profile for distribution", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    protected SelectQuery getSelectQuery() {
        final Table profileTab = Table.getTable("Profile");
        (this.selectQuery = (SelectQuery)new SelectQueryImpl(profileTab)).addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        this.selectQuery.addJoin(new Join("Profile", "RecentProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        this.selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_NAME"));
        this.selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_ID"));
        this.selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_TYPE"));
        this.selectQuery.addSelectColumn(Column.getColumn("Profile", "LAST_MODIFIED_BY"));
        this.selectQuery.addSelectColumn(Column.getColumn("Profile", "CREATION_TIME"));
        this.selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_DESCRIPTION"));
        this.selectQuery.addSelectColumn(Column.getColumn("Profile", "PLATFORM_TYPE"));
        this.selectQuery.addSelectColumn(Column.getColumn("Profile", "SCOPE"));
        this.selectQuery.addSelectColumn(Column.getColumn("Profile", "PROFILE_SHARED_SCOPE"));
        final SortColumn sortCol = new SortColumn(Column.getColumn("Profile", "PROFILE_NAME"), true);
        this.selectQuery.addSortColumn(sortCol);
        ManagementUtil.modifySelectQueryForManagement(this.selectQuery);
        return this.selectQuery;
    }
    
    protected SelectQuery setCriteria() {
        try {
            final Boolean isGroup = this.requestJson.optBoolean("isGroup");
            final JSONArray groupIdArray = this.requestJson.optJSONArray("groupIds");
            final List<Long> groupIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(groupIdArray);
            final JSONArray deviceIdArray = this.requestJson.optJSONArray("deviceIds");
            final List<Long> deviceIdList = JSONUtil.getInstance().convertLongJSONArrayTOList(deviceIdArray);
            final JSONArray platformArray = this.requestJson.optJSONArray("platform");
            final List<Long> platformTypeList = JSONUtil.getInstance().convertLongJSONArrayTOList(platformArray);
            final Long customerId = this.requestJson.optLong("customerId");
            final String searchValue = this.requestJson.optString("searchValue");
            SelectQuery subQuery;
            if (isGroup) {
                if (groupIdList.isEmpty()) {
                    throw new APIHTTPException("COM0005", new Object[] { "group_ids" });
                }
                subQuery = this.getProfileGroupSubQuery(groupIdList);
            }
            else {
                if (deviceIdList.isEmpty()) {
                    throw new APIHTTPException("COM0005", new Object[] { "device_ids" });
                }
                subQuery = this.getProfileDeviceSubQuery(deviceIdList);
            }
            final Table profileTab = Table.getTable("Profile");
            final DerivedTable profileDerievedTab = new DerivedTable("derivedProfileTable", (Query)subQuery);
            final Criteria joinCri = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)Column.getColumn("derivedProfileTable", "derived_profile_id"), 0);
            this.selectQuery.addJoin(new Join(profileTab, (Table)profileDerievedTab, joinCri, 1));
            this.selectQuery.addSelectColumn(Column.getColumn("derivedProfileTable", "derived_profile_id"));
            this.selectQuery.addSelectColumn(Column.getColumn("derivedProfileTable", "executed_profile_version"));
            Criteria criteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0);
            criteria = criteria.and(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            if (!platformTypeList.isEmpty()) {
                criteria = criteria.and(this.getPlatformTypeCriteria(platformTypeList));
            }
            if (searchValue != null && !searchValue.isEmpty()) {
                final Criteria searchProfileCri = new Criteria(Column.getColumn("Profile", "PROFILE_NAME"), (Object)searchValue, 12, false);
                criteria = criteria.and(searchProfileCri);
            }
            this.selectQuery.setCriteria(criteria);
        }
        catch (final Exception ex) {
            ApiListViewDataHandler.logger.log(Level.SEVERE, "Exception while fetching profile for distribution", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return this.selectQuery;
    }
    
    protected abstract JSONObject fetchResultObject();
    
    private SelectQuery getProfileGroupSubQuery(final List groupIds) {
        final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        final Join profileJoin = new Join("RecentProfileForGroup", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2);
        final Join profileToCollnJoin = new Join("RecentProfileForGroup", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2);
        subQuery.addJoin(profileJoin);
        subQuery.addJoin(profileToCollnJoin);
        final Column versionCol = new Column("ProfileToCollection", "PROFILE_VERSION").minimum();
        versionCol.setColumnAlias("executed_profile_version");
        subQuery.addSelectColumn(versionCol);
        subQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "PROFILE_ID", "derived_profile_id"));
        final Column countCol = new Column("RecentProfileForGroup", "GROUP_ID").count();
        final Criteria recentGroupCri = new Criteria(Column.getColumn("RecentProfileForGroup", "GROUP_ID"), (Object)groupIds.toArray(), 8);
        subQuery.setCriteria(recentGroupCri);
        final GroupByColumn groupByProfileCol = new GroupByColumn(new Column("RecentProfileForGroup", "PROFILE_ID"), true);
        final List<GroupByColumn> groupByList = new ArrayList<GroupByColumn>();
        groupByList.add(groupByProfileCol);
        final GroupByClause groupByClause = new GroupByClause((List)groupByList, new Criteria(countCol, (Object)groupIds.size(), 4));
        subQuery.setGroupByClause(groupByClause);
        return subQuery;
    }
    
    private SelectQuery getProfileDeviceSubQuery(final List deviceIds) {
        final SelectQuery profileSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
        profileSQ.addJoin(new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        profileSQ.addJoin(new Join("RecentProfileForResource", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        profileSQ.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID", "derived_profile_id"));
        final Column versionCol = new Column("ProfileToCollection", "PROFILE_VERSION").minimum();
        versionCol.setColumnAlias("executed_profile_version");
        profileSQ.addSelectColumn(versionCol);
        final Column collectionCol = new Column("RecentProfileForResource", "COLLECTION_ID").minimum();
        collectionCol.setColumnAlias("derived_collection_id");
        profileSQ.addSelectColumn(collectionCol);
        final Column countCol = new Column("RecentProfileForResource", "RESOURCE_ID").count();
        final Criteria recentGroupCri = new Criteria(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"), (Object)deviceIds.toArray(), 8);
        profileSQ.setCriteria(recentGroupCri);
        final GroupByColumn groupByProfileCol = new GroupByColumn(new Column("RecentProfileForResource", "PROFILE_ID"), true);
        final List<GroupByColumn> groupByList = new ArrayList<GroupByColumn>();
        groupByList.add(groupByProfileCol);
        final GroupByClause groupByClause = new GroupByClause((List)groupByList, new Criteria(countCol, (Object)deviceIds.size(), 4));
        profileSQ.setGroupByClause(groupByClause);
        return profileSQ;
    }
    
    protected Criteria getPlatformTypeCriteria(final List platformType) {
        final Criteria platformTypeCri = new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)platformType.toArray(), 8);
        return platformTypeCri;
    }
    
    protected Criteria getProfileTypeCriteria(final Integer profileType) {
        final Criteria profileTypeCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)profileType, 0);
        return profileTypeCri;
    }
    
    protected JSONObject setBasicProfileValues(final DMDataSetWrapper dataSetWrapper) throws JSONException {
        final Long profileId = (Long)dataSetWrapper.getValue("PROFILE_ID");
        final String profileName = (String)dataSetWrapper.getValue("PROFILE_NAME");
        final int platform = (int)dataSetWrapper.getValue("PLATFORM_TYPE");
        final int profileType = (int)dataSetWrapper.getValue("PROFILE_TYPE");
        final Integer profileSharedScope = (Integer)dataSetWrapper.getValue("PROFILE_SHARED_SCOPE");
        final Boolean isForAllCustomers = profileSharedScope == 1;
        final JSONObject profileObject = new JSONObject();
        profileObject.put("profile_id", (Object)profileId);
        profileObject.put("name", (Object)profileName);
        profileObject.put("platform_type", platform);
        profileObject.put("isUpgrade", false);
        profileObject.put("isEnabled", true);
        profileObject.put("profile_type", profileType);
        profileObject.put("is_for_all_customers", (Object)isForAllCustomers);
        return profileObject;
    }
    
    protected Criteria getProfileFilterButtonCriteria(final String filterButtonVal) {
        Criteria profileFilterButonCri = null;
        if (filterButtonVal.equalsIgnoreCase("all")) {
            Criteria selectGroupCri = new Criteria(Column.getColumn("derivedProfileTable", "derived_profile_id"), (Object)null, 0);
            final Criteria profileVersionNotNullCri = new Criteria(Column.getColumn("derivedProfileTable", "executed_profile_version"), (Object)null, 1);
            final Criteria profileVersionCri = new Criteria(Column.getColumn("derivedProfileTable", "executed_profile_version"), (Object)Column.getColumn("ProfileToCollection", "PROFILE_VERSION"), 1);
            selectGroupCri = (profileFilterButonCri = selectGroupCri.or(profileVersionNotNullCri.and(profileVersionCri)));
        }
        else if (filterButtonVal.equalsIgnoreCase("associated")) {
            final Criteria selectGroupCri = profileFilterButonCri = new Criteria(Column.getColumn("derivedProfileTable", "derived_profile_id"), (Object)null, 1);
        }
        return profileFilterButonCri;
    }
    
    protected SelectQuery setRange(final JSONObject requestJson) {
        final Integer startIndex = requestJson.optInt("startIndex");
        final Integer noOfObj = requestJson.optInt("noOfObj");
        final Boolean selectAllValue = requestJson.optBoolean("selectAll");
        if (!selectAllValue) {
            final Range profileRange = new Range((int)startIndex, (int)noOfObj);
            this.selectQuery.setRange(profileRange);
        }
        return this.selectQuery;
    }
    
    static {
        ApiListViewDataHandler.logger = Logger.getLogger("MDMApiLogger");
        ApiListViewDataHandler.apiListViewDataHandler = null;
    }
}
