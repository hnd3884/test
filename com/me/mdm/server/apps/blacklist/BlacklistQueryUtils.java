package com.me.mdm.server.apps.blacklist;

import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.ds.query.UnionQuery;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.UnionQueryImpl;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Iterator;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.MDMProfileListViewDataHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.List;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public class BlacklistQueryUtils
{
    public static BlacklistQueryUtils blacklistMailUtils;
    private Logger logger;
    
    public BlacklistQueryUtils() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static BlacklistQueryUtils getInstance() {
        if (BlacklistQueryUtils.blacklistMailUtils == null) {
            BlacklistQueryUtils.blacklistMailUtils = new BlacklistQueryUtils();
        }
        return BlacklistQueryUtils.blacklistMailUtils;
    }
    
    public SelectQuery getViolationQuery(final Long customerID) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppCollectionStatus"));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "ManagedUserToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "ManagedDeviceExtn", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedUserToDevice", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "BlacklistAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria resCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), 0);
        final Criteria appCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "APP_ID"), (Object)Column.getColumn("MdAppDetails", "APP_ID"), 0);
        selectQuery.addJoin(new Join("MdAppDetails", "MdInstalledAppResourceRel", resCriteria.and(appCriteria), 2));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "MANAGED_DEVICE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "NAME"));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "NOTIFIED_COUNT"));
        final Long curTime = System.currentTimeMillis();
        final Long oneDayTime = curTime - 86400000L;
        final Criteria timeCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "LAST_NOTIFIED_TIME"), (Object)oneDayTime, 7);
        final Criteria timeCriteria2 = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "LAST_NOTIFIED_TIME"), (Object)(-1L), 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 1, 3, 9 }, 8);
        final Criteria managedStatusCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        selectQuery.setCriteria(managedStatusCriteria.and(customerCriteria.and(statusCriteria).and(timeCriteria.or(timeCriteria2))));
        selectQuery.addSortColumn(new SortColumn(Column.getColumn("Resource", "NAME"), true));
        return selectQuery;
    }
    
    public SelectQuery getBlacklistInventoryTableQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        final SelectQuery blacklistedCountQuery = this.getBlacklistedCountQuery();
        final SelectQuery tottalDeviceCountQuery = this.getTotalResourceCount();
        final DerivedTable blackListcntTable = new DerivedTable("BlackListCount", (Query)blacklistedCountQuery);
        final DerivedTable totalResourceCountTable = new DerivedTable("TotalCount", (Query)tottalDeviceCountQuery);
        selectQuery.addJoin(new Join(Table.getTable("MdAppGroupDetails"), (Table)blackListcntTable, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        selectQuery.addJoin(new Join(Table.getTable("MdAppGroupDetails"), (Table)totalResourceCountTable, new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("BlackListCount", "BlacklistCount"));
        selectQuery.addSelectColumn(Column.getColumn("TotalCount", "TotalCount"));
        return selectQuery;
    }
    
    private SelectQuery getBlacklistedCountQuery() {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "BlacklistAppCollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "CollnToResources", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Column countCOlumn = new Column("BlacklistAppCollectionStatus", "RESOURCE_ID").count();
        countCOlumn.setColumnAlias("BlacklistCount");
        final List list = new ArrayList();
        list.add(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        final GroupByClause groupByClause = new GroupByClause(list);
        final Criteria statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 4, 3, 1, 2 }, 8);
        final Criteria applicableCriteria = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)8, 1);
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(countCOlumn);
        selectQuery.setCriteria(statusCriteria.and(applicableCriteria));
        selectQuery.setGroupByClause(groupByClause);
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        return selectQuery;
    }
    
    private SelectQuery getTotalResourceCount() {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "MdAppGroupDetails", new String[] { "PLATFORM_TYPE" }, new String[] { "PLATFORM_TYPE" }, 2));
        final Column countCOlumn = new Column("ManagedDevice", "RESOURCE_ID").count();
        countCOlumn.setColumnAlias("TotalCount");
        final List list = new ArrayList();
        list.add(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        final GroupByClause groupByClause = new GroupByClause(list);
        final Criteria statusCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(countCOlumn);
        selectQuery.setCriteria(statusCriteria);
        selectQuery.setGroupByClause(groupByClause);
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        return selectQuery;
    }
    
    public SelectQuery getBlacklistedDevicesForAppGroupID(final Long appGroupID) {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "BlacklistAppCollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "CollnToResources", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Criteria statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 4, 3, 1, 2 }, 8);
        final Criteria applicableCriteria = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)8, 1);
        final Criteria appGrpIDCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupID, 0);
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"));
        selectQuery.setCriteria(statusCriteria.and(applicableCriteria).and(appGrpIDCriteria));
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        return selectQuery;
    }
    
    public SelectQuery getAppsCount(final Long customerID) {
        final DerivedTable derivedTable = (DerivedTable)this.getUserAppGroups();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl((Table)derivedTable);
        selectQuery.addJoin(new Join((Table)derivedTable, Table.getTable("MdAppGroupDetails"), new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join((Table)derivedTable, Table.getTable("MdAppToGroupRel"), new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        final Criteria criteria = new Criteria(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID"), 0);
        final Criteria statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 4, 3, 1, 2, 6, 7, 11, 10 }, 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final Criteria managedNotNullCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 1);
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "BlacklistAppCollectionStatus", criteria.and(statusCriteria), 1));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        final Column blackListcnt = new Column("BlacklistAppCollectionStatus", "COLLECTION_ID").distinct().count();
        blackListcnt.setColumnAlias("blacklistCount");
        final Column discoverdcnt = new Column("MdAppGroupDetails", "APP_GROUP_ID").distinct().count();
        discoverdcnt.setColumnAlias("discoveredCount");
        final Column managedCount = new Column("MdPackageToAppGroup", "PACKAGE_ID").distinct().count();
        managedCount.setColumnAlias("managedCount");
        selectQuery.addSelectColumn(blackListcnt);
        selectQuery.addSelectColumn(discoverdcnt);
        selectQuery.addSelectColumn(managedCount);
        selectQuery.setCriteria(customerCriteria.and(managedCriteria).and(managedNotNullCriteria));
        return selectQuery;
    }
    
    public SelectQuery getDeviceWithBlacklistAppCount(final Long customerID) {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "BlacklistAppCollectionStatus", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "BlacklistAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppToGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria appCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "APP_ID"), (Object)Column.getColumn("MdAppToGroupRel", "APP_ID"), 0);
        final Criteria resourceCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), 0);
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "MdInstalledAppResourceRel", appCriteria.and(resourceCriteria), 2));
        final Criteria managedCriteria = ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria();
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 1, 2, 3, 9 }, 8);
        selectQuery.setCriteria(managedCriteria.and(statusCriteria).and(customerCriteria));
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        return selectQuery;
    }
    
    private Table getUserAppGroups() {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "MdInstalledAppResourceRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MdInstalledAppResourceRel", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        final Column appGroupids = new Column("MdAppToGroupRel", "APP_GROUP_ID").distinct();
        appGroupids.setColumnAlias("APP_GROUP_ID");
        selectQuery.addSelectColumn(appGroupids);
        final DerivedTable derivedTable = new DerivedTable("UserTable", (Query)selectQuery);
        return (Table)derivedTable;
    }
    
    public SelectQuery getAppSummaryData(final Long appGroup) {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "BlacklistAppCollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroup, 0));
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        final List list = new ArrayList();
        list.add(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        list.add(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        list.add(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
        final GroupByClause groupByClause = new GroupByClause(list);
        selectQuery.setGroupByClause(groupByClause);
        final Column count = new Column("BlacklistAppCollectionStatus", "RESOURCE_ID", "DeviceCount").count();
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(count);
        return selectQuery;
    }
    
    public SelectQuery getGroupBlacklistQuery(final HashMap params) {
        final Long customerId = params.get("CUSTOMER_ID");
        final Long loginID = params.get("loginID");
        final Long[] groupIds = params.get("groupIds");
        final String filterButtonVal = params.get("filterButtonVal");
        final String filterTreeParams = params.get("filterTreeParams");
        final String selectedString = params.get("selectedString");
        final String associatedString = params.get("associatedString");
        final String availableString = params.get("availableString");
        final String searchValue = params.get("searchValue");
        final int startIndex = params.get("startIndex");
        final int noOfObj = params.get("noOfObj");
        final String selectAllValue = params.get("selectAllValue");
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        SelectQuery appQuery = null;
        if (loginID != null) {
            final DerivedTable derivedTable = (DerivedTable)this.getUserAppGroups();
            appQuery = (SelectQuery)new SelectQueryImpl((Table)derivedTable);
            appQuery.addJoin(new Join((Table)derivedTable, Table.getTable("MdAppGroupDetails"), new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        }
        else {
            appQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        }
        appQuery.addJoin(new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        appQuery.addJoin(new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        Criteria finalCriteria = null;
        if (filterTreeParams.contains(selectedString) || filterTreeParams.contains(availableString)) {
            finalCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), (Object)null, 0);
            finalCriteria = finalCriteria.or(new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 8, 9 }, 8));
        }
        else if (filterTreeParams.contains(associatedString)) {
            finalCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), (Object)null, 1);
        }
        appQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        appQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        appQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
        appQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        finalCriteria = finalCriteria.and(customerCriteria).and(this.getCommonCriteria(filterTreeParams, searchValue));
        appQuery.setCriteria(finalCriteria);
        return appQuery;
    }
    
    private Criteria getCommonCriteria(final String filterTreeParams, final String searchValue) {
        Criteria commonCri = null;
        if (searchValue != null) {
            final Criteria searchNameCri = new Criteria(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"), (Object)searchValue, 12, false);
            final Criteria searchIdentifierCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)searchValue, 12, false);
            commonCri = ((commonCri != null) ? commonCri.and(searchNameCri.or(searchIdentifierCri)) : searchNameCri.or(searchIdentifierCri));
        }
        if (filterTreeParams != null) {
            try {
                final JSONArray filterTreeJSON = (JSONArray)new JSONParser().parse(filterTreeParams);
                if (filterTreeJSON.size() > 0) {
                    final Criteria filterCri = this.getBlacklistFilterCriteria(filterTreeJSON);
                    commonCri = ((commonCri != null) ? commonCri.and(filterCri) : filterCri);
                }
            }
            catch (final ParseException ex) {
                Logger.getLogger(MDMProfileListViewDataHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        return commonCri;
    }
    
    private Criteria getBlacklistFilterCriteria(final JSONArray filterTreeJSON) {
        final Iterator filterItr = filterTreeJSON.iterator();
        JSONObject filterJSON = null;
        Criteria filterCri = null;
        int filterType = -1;
        long filterMemberId = -1L;
        Criteria platformTypeCri = null;
        while (filterItr.hasNext()) {
            filterJSON = filterItr.next();
            filterType = Integer.parseInt((String)filterJSON.get((Object)"FILTER_TYPE"));
            filterMemberId = Long.parseLong((String)filterJSON.get((Object)"FILTER_MEMBER_ID"));
            switch (filterType) {
                case 1: {
                    final Criteria appTypeNewCri = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)filterMemberId, 0);
                    platformTypeCri = ((platformTypeCri == null) ? appTypeNewCri : platformTypeCri.or(appTypeNewCri));
                    continue;
                }
            }
        }
        filterCri = ((filterCri == null) ? platformTypeCri : filterCri.and(platformTypeCri));
        return filterCri;
    }
    
    public SelectQuery getGroupTreeForAppGroups(final HashMap hashMap) {
        final Long customerID = hashMap.get("cid");
        final String searchValue = hashMap.get("search");
        final String groups = hashMap.getOrDefault("groups", null);
        final boolean countFlag = hashMap.getOrDefault("count", Boolean.FALSE);
        List groupTypeList = new ArrayList();
        if (!MDMStringUtils.isEmpty(groups)) {
            final String[] split;
            final String[] groupTypes = split = groups.split(",");
            for (final String groupCode : split) {
                if (Integer.valueOf(groupCode) == 6) {
                    groupTypeList = MDMGroupHandler.getMDMGroupType();
                }
                else if (Integer.valueOf(groupCode) == 7) {
                    groupTypeList.add(7);
                }
            }
        }
        else {
            groupTypeList = MDMGroupHandler.getMDMGroupType();
            groupTypeList.add(7);
        }
        final int type = Integer.parseInt(hashMap.get("type").toString());
        final String[] appGroupArr = hashMap.get("appGroupIds").toString().split(",");
        final int len = appGroupArr.length;
        final Long[] appGroupids = new Long[len];
        for (int i = 0; i < len; ++i) {
            appGroupids[i] = Long.parseLong(appGroupArr[i].toString());
        }
        SelectQuery customGroupsForUserQuery;
        if (countFlag) {
            customGroupsForUserQuery = MDMGroupHandler.getCustomGroupsCountQuery(groupTypeList, Boolean.FALSE);
            customGroupsForUserQuery.removeSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
        }
        else {
            customGroupsForUserQuery = MDMGroupHandler.getCustomGroupsQuery(groupTypeList, Boolean.FALSE);
            customGroupsForUserQuery.removeSelectColumn(Column.getColumn("CustomGroup", "RESOURCE_ID"));
        }
        final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria groupTypeCri = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupTypeList.toArray(), 8);
        Criteria criteria = getRBDAGroupsCriteriaBasedOnRole(groupTypeList);
        if (criteria != null) {
            criteria = criteria.and(groupTypeCri.and(customerCri));
        }
        else {
            criteria = groupTypeCri.and(customerCri);
        }
        customGroupsForUserQuery.setCriteria(criteria);
        if (searchValue != null) {
            final Criteria searchCri = new Criteria(Column.getColumn("Resource", "NAME"), (Object)searchValue, 12, false);
            customGroupsForUserQuery.setCriteria(customGroupsForUserQuery.getCriteria().and(searchCri));
        }
        final DerivedTable GroupToBlacklistedGroup = this.getBlacklistedAppGroupToGroupTable(appGroupids, type);
        customGroupsForUserQuery.addJoin(new Join(Table.getTable("Resource"), (Table)GroupToBlacklistedGroup, new String[] { "RESOURCE_ID" }, new String[] { "GROUP_ID" }, 1));
        if (type == 1) {
            final Criteria allNotAssocoiatedCriteria = new Criteria(Column.getColumn("GroupToBlacklistedGroup", "GROUP_ID"), (Object)null, 0);
            customGroupsForUserQuery.setCriteria(customGroupsForUserQuery.getCriteria().and(allNotAssocoiatedCriteria));
        }
        else if (type == 2) {
            final Criteria allNotAssocoiatedCriteria = new Criteria(Column.getColumn("GroupToBlacklistedGroup", "GROUP_ID"), (Object)null, 1);
            customGroupsForUserQuery.setCriteria(customGroupsForUserQuery.getCriteria().and(allNotAssocoiatedCriteria));
        }
        return customGroupsForUserQuery;
    }
    
    public SelectQuery getResourceTreeForAppGroups(final HashMap hashMap) {
        final Long customerID = hashMap.get("cid");
        final String searchValue = hashMap.get("search");
        final int type = Integer.parseInt(hashMap.get("type").toString());
        final String[] appGroupArr = hashMap.get("appGroupIds").toString().split(",");
        final List<Integer> platform = hashMap.get("platform");
        final int len = appGroupArr.length;
        final boolean countFlag = hashMap.getOrDefault("count", Boolean.FALSE);
        final Long[] appGroupids = new Long[len];
        for (int i = 0; i < len; ++i) {
            appGroupids[i] = Long.parseLong(appGroupArr[i].toString());
        }
        final SelectQuery resourceQuery = (SelectQuery)new SelectQueryImpl(new Table("Resource"));
        final Criteria customerCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        Criteria macCriteria = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)new Integer[] { 3, 4 }, 8);
        macCriteria = macCriteria.and(new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0));
        macCriteria = macCriteria.negate();
        resourceQuery.setCriteria(customerCri.and(managedCriteria).and(macCriteria));
        if (searchValue != null) {
            final Criteria searchCri = new Criteria(Column.getColumn("ManagedDeviceExtn", "NAME"), (Object)searchValue, 12, false);
            resourceQuery.setCriteria(resourceQuery.getCriteria().and(searchCri));
        }
        if (platform != null && !platform.isEmpty()) {
            final Criteria platformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platform.toArray(), 8);
            resourceQuery.setCriteria(resourceQuery.getCriteria().and(platformCriteria));
        }
        final DerivedTable GroupToBlacklistedGroup = this.getBlacklistedAppGroupToDeviceTable(appGroupids, type);
        resourceQuery.addJoin(new Join(Table.getTable("Resource"), (Table)GroupToBlacklistedGroup, new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        final Criteria managedDeviceCriteria = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
        resourceQuery.addJoin(new Join(Table.getTable("Resource"), Table.getTable("ManagedDevice"), managedDeviceCriteria.and(customerCri), 2));
        resourceQuery.addJoin(new Join(Table.getTable("ManagedDevice"), Table.getTable("ManagedDeviceExtn"), new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        resourceQuery.addJoin(new Join(Table.getTable("ManagedDevice"), Table.getTable("ManagedUserToDevice"), new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        resourceQuery.addJoin(new Join("ManagedUserToDevice", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, "ManagedUserToDevice", "USER_RESOURCE", 2));
        resourceQuery.addJoin(new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        resourceQuery.addJoin(new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
        final DerivedTable platformTable = this.getApplicablePlatforms(appGroupids);
        resourceQuery.addJoin(new Join(Table.getTable("ManagedDevice"), (Table)platformTable, new String[] { "PLATFORM_TYPE" }, new String[] { "PLATFORM_TYPE" }, 2));
        if (countFlag) {
            final Column resColumn = new Column("ManagedDevice", "RESOURCE_ID").distinct().count();
            resColumn.setColumnAlias("RESOURCE_ID");
            resourceQuery.addSelectColumn(resColumn);
        }
        else {
            final Column resColumn = new Column("ManagedDevice", "RESOURCE_ID").distinct();
            resColumn.setColumnAlias("RESOURCE_ID");
            resourceQuery.addSelectColumn(resColumn);
            resourceQuery.addSelectColumn(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"));
            resourceQuery.addSelectColumn(Column.getColumn("ManagedDevice", "MANAGED_STATUS"));
            resourceQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
            resourceQuery.addSelectColumn(Column.getColumn("ManagedDeviceExtn", "*"));
            resourceQuery.addSelectColumn(Column.getColumn("USER_RESOURCE", "RESOURCE_ID", "USER_RESOURCE_ID"));
            resourceQuery.addSelectColumn(Column.getColumn("USER_RESOURCE", "NAME", "USER_RESOURCE_NAME"));
        }
        RBDAUtil.getInstance().getRBDAQuery(resourceQuery);
        if (type == 1) {
            final Criteria allNotAssocoiatedCriteria = new Criteria(Column.getColumn("ResourceToBlacklistedResource", "RESOURCE_ID"), (Object)null, 0);
            resourceQuery.setCriteria(resourceQuery.getCriteria().and(allNotAssocoiatedCriteria));
        }
        else if (type == 2) {
            final Criteria allNotAssocoiatedCriteria = new Criteria(Column.getColumn("ResourceToBlacklistedResource", "RESOURCE_ID"), (Object)null, 1);
            resourceQuery.setCriteria(resourceQuery.getCriteria().and(allNotAssocoiatedCriteria));
        }
        return resourceQuery;
    }
    
    private DerivedTable getBlacklistedAppGroupToGroupTable(final Long[] appGroupID, final int type) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "RecentProfileForGroup", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForGroup", "BlacklistAppCollectionStatus", new String[] { "COLLECTION_ID", "GROUP_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
        final Criteria appGroupCri = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupID, 8);
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForGroup", "GROUP_ID"));
        final List groupByList = new ArrayList();
        groupByList.add(new Column("RecentProfileForGroup", "GROUP_ID"));
        Criteria havingCriteria = null;
        if (type == 1) {
            havingCriteria = new Criteria(new Column("RecentProfileForGroup", "GROUP_ID").count(), (Object)appGroupID.length, 0);
            final Criteria statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 1, 2, 3, 4, 11 }, 8);
            selectQuery.setCriteria(appGroupCri.and(statusCriteria));
        }
        else if (type == 2) {
            havingCriteria = new Criteria(new Column("RecentProfileForGroup", "GROUP_ID").count(), (Object)0, 5);
            final Criteria statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 1, 2, 3, 4, 11 }, 8);
            selectQuery.setCriteria(appGroupCri.and(statusCriteria));
        }
        final GroupByClause groupByClause = new GroupByClause(groupByList, havingCriteria);
        selectQuery.setGroupByClause(groupByClause);
        final DerivedTable derivedTable1 = new DerivedTable("GroupToBlacklistedGroup", (Query)selectQuery);
        return derivedTable1;
    }
    
    private DerivedTable getBlacklistedAppGroupToDeviceTable(final Long[] appGroupID, final int type) {
        final DerivedTable derivedTable = (DerivedTable)this.getUserAppGroups();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "RecentProfileForResource", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "BlacklistAppCollectionStatus", new String[] { "COLLECTION_ID", "RESOURCE_ID" }, new String[] { "COLLECTION_ID", "RESOURCE_ID" }, 2));
        final Criteria appGroupCri = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupID, 8);
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
        final List groupByList = new ArrayList();
        groupByList.add(new Column("RecentProfileForResource", "RESOURCE_ID"));
        Criteria havingCriteria = null;
        if (type == 1) {
            havingCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID").count(), (Object)appGroupID.length, 0);
            final Criteria statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 1, 2, 3, 4, 11, 5 }, 8);
            selectQuery.setCriteria(appGroupCri.and(statusCriteria));
        }
        else if (type == 2) {
            havingCriteria = new Criteria(new Column("RecentProfileForResource", "RESOURCE_ID").count(), (Object)0, 5);
            final Criteria statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 1, 2, 3, 4, 11, 5 }, 8);
            selectQuery.setCriteria(appGroupCri.and(statusCriteria));
        }
        final GroupByClause groupByClause = new GroupByClause(groupByList, havingCriteria);
        selectQuery.setGroupByClause(groupByClause);
        final DerivedTable derivedTable2 = new DerivedTable("ResourceToBlacklistedResource", (Query)selectQuery);
        return derivedTable2;
    }
    
    public SelectQuery getRemoveAllSettings(final Long[] appGroupid) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppToCollection"));
        final Criteria appGrpCriteria = new Criteria(Column.getColumn("BlacklistAppToCollection", "APP_GROUP_ID"), (Object)appGroupid, 8);
        final Criteria globalCriteria = new Criteria(Column.getColumn("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)true, 0);
        selectQuery.setCriteria(globalCriteria.and(appGrpCriteria));
        return selectQuery;
    }
    
    public Criteria getCriteriaforDeviceForApps(final int type) {
        Criteria criteria = null;
        if (type == 1) {
            criteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)null, 1);
            criteria = criteria.and(new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)8, 1));
        }
        else if (type == 2) {
            criteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)null, 0);
            criteria = criteria.or(new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)8, 0));
        }
        else if (type == 3) {
            criteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)4, 1);
        }
        return criteria;
    }
    
    public SelectQuery getNetworkBlacklistCountQuery(final Long customerID) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppToCollection"));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "ProfileToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("ProfileToCollection", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Column column = new Column("BlacklistAppToCollection", "COLLECTION_ID").distinct().count();
        column.setColumnAlias("networkBlacklistCount");
        selectQuery.addSelectColumn(column);
        final Criteria globalCriteria = new Criteria(Column.getColumn("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)true, 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(customerCriteria.and(globalCriteria));
        return selectQuery;
    }
    
    public SelectQuery whitelistPendingCountQuery(final Long customerID) {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppCollectionStatus"));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "BlacklistAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        final Criteria statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 7, 6 }, 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(statusCriteria.and(customerCriteria));
        final Column column = new Column("BlacklistAppCollectionStatus", "RESOURCE_ID").distinct().count();
        column.setColumnAlias("whitelistPendingCount");
        selectQuery.addSelectColumn(column);
        return selectQuery;
    }
    
    private DerivedTable getApplicablePlatforms(final Long[] appGroupIds) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupIds, 8));
        final Column platform = new Column("MdAppGroupDetails", "PLATFORM_TYPE").distinct();
        platform.setColumnAlias("PLATFORM_TYPE");
        selectQuery.addSelectColumn(platform);
        return new DerivedTable("platformTable", (Query)selectQuery);
    }
    
    public SelectQuery getDiscoveredCount(final Long customerID, final Boolean hideOtherApps) {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdInstalledAppResourceRel"));
        selectQuery.addJoin(new Join("MdInstalledAppResourceRel", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MdInstalledAppResourceRel", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdInstalledAppResourceRel", "MdAppCatalogToResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        final Criteria criteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria managedCriteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
        final org.json.JSONObject jsonObject = AppSettingsDataHandler.getInstance().getAppViewSettings(customerID);
        final boolean isShowSystemApp = jsonObject.optBoolean("SHOW_SYSTEM_APPS", false);
        final boolean isShowUserInstalledApp = jsonObject.optBoolean("SHOW_USER_INSTALLED_APPS", true);
        final boolean isShowManagedApp = jsonObject.optBoolean("SHOW_MANAGED_APPS", true);
        Criteria finalCriteria = null;
        if (isShowManagedApp && isShowUserInstalledApp && isShowSystemApp) {
            finalCriteria = criteria.and(managedCriteria);
        }
        else if (isShowManagedApp && isShowUserInstalledApp) {
            final Criteria userInstlledCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 0);
            final Criteria catalogCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 1);
            finalCriteria = criteria.and(managedCriteria).and(userInstlledCriteria.or(catalogCriteria));
        }
        else if (isShowManagedApp && isShowSystemApp) {
            final Criteria userInstlledCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)2, 0);
            final Criteria catalogCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 1);
            finalCriteria = criteria.and(managedCriteria).and(userInstlledCriteria.or(catalogCriteria));
        }
        else if (isShowUserInstalledApp && isShowSystemApp) {
            final Criteria catalogCriteria2 = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 0);
            finalCriteria = criteria.and(managedCriteria).and(catalogCriteria2);
        }
        else if (isShowManagedApp) {
            final Criteria catalogCriteria2 = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 1);
            finalCriteria = criteria.and(managedCriteria).and(catalogCriteria2);
        }
        else if (isShowUserInstalledApp) {
            final Criteria userInstlledCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)1, 0);
            final Criteria catalogCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 0);
            finalCriteria = criteria.and(managedCriteria).and(userInstlledCriteria.and(catalogCriteria));
        }
        else if (isShowSystemApp) {
            final Criteria userInstlledCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "USER_INSTALLED_APPS"), (Object)2, 0);
            final Criteria catalogCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)null, 0);
            finalCriteria = criteria.and(managedCriteria).and(userInstlledCriteria.and(catalogCriteria));
        }
        selectQuery.setCriteria(finalCriteria);
        if (hideOtherApps) {
            selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        }
        return selectQuery;
    }
    
    private static Criteria getRBDAGroupsCriteriaBasedOnRole(final List groupList) {
        Long loginId = null;
        Long userId = null;
        try {
            loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        }
        catch (final Exception ex) {}
        final Criteria groupTypeCriteria = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupList.toArray(), 8);
        final Criteria createdByCriteria = new Criteria(Column.getColumn("CustomGroupExtn", "CREATED_BY"), (Object)userId, 0);
        final Criteria loginIdCriteria = new Criteria(Column.getColumn("UserCustomGroupMapping", "LOGIN_ID"), (Object)loginId, 0);
        Criteria finalCriteriaBasedOnRole = null;
        if (RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginId, false)) {
            finalCriteriaBasedOnRole = groupTypeCriteria;
        }
        else {
            finalCriteriaBasedOnRole = groupTypeCriteria.and(createdByCriteria.or(loginIdCriteria));
        }
        return finalCriteriaBasedOnRole;
    }
    
    public void deleteInstalledAppFromIdentifier(final Long resourceId, final List identifierList) {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdInstalledAppResourceRel");
        deleteQuery.addJoin(new Join("MdInstalledAppResourceRel", "MdAppDetails", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        final Criteria resourceCriteria = new Criteria(new Column("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)resourceId, 0);
        final Criteria identifierCriteria = new Criteria(new Column("MdAppDetails", "IDENTIFIER"), (Object)identifierList.toArray(), 8);
        deleteQuery.setCriteria(resourceCriteria.and(identifierCriteria));
        try {
            MDMUtil.getPersistence().delete(deleteQuery);
            this.logger.log(Level.INFO, "DeleteInstalledBlacklistedApps From MDINSTALLEDAPPRESOURCEREL Apps:{0} Deleted for resourceID:{1}", new Object[] { identifierList, resourceId });
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Could not delete blacklisted apps", (Throwable)e);
        }
    }
    
    public SelectQuery blacklistPendingCountQuery(final Long customerID) {
        return this.blacklistPendingCountQuery(customerID, null);
    }
    
    public SelectQuery blacklistPendingCountQuery(final Long customerID, final Long appGroupId) {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppCollectionStatus"));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "BlacklistAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        final Criteria statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 1, 2, 3, 9 }, 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
        selectQuery.setCriteria(statusCriteria.and(customerCriteria));
        if (appGroupId != null) {
            final Criteria appGroupCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0);
            selectQuery.setCriteria(selectQuery.getCriteria().and(appGroupCriteria));
        }
        final Column column = new Column("BlacklistAppCollectionStatus", "RESOURCE_ID").distinct().count();
        column.setColumnAlias("blacklistPendingCount");
        selectQuery.addSelectColumn(column);
        return selectQuery;
    }
    
    public SelectQuery installAppCount(final Long customerId, final Long appGroupId) {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "MdInstalledAppResourceRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MdInstalledAppResourceRel", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria appCatalogCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), 0);
        final Criteria managedAppCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
        final Criteria installedCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)null, 1);
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppCatalogToResource", appCatalogCriteria.and(managedAppCriteria).and(installedCriteria), 1));
        final Criteria customerCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria appGroupCriteria = new Criteria(new Column("MdAppToGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria managedDeviceCriteria = ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria();
        selectQuery.setCriteria(customerCriteria.and(appGroupCriteria).and(managedDeviceCriteria));
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        final Criteria showAppCriteria = AppsUtil.getInstance().showAppCriteria(customerId);
        if (showAppCriteria != null) {
            selectQuery.setCriteria(selectQuery.getCriteria().and(showAppCriteria));
        }
        final Column countCol = new Column("ManagedDevice", "RESOURCE_ID").distinct().count();
        countCol.setColumnAlias("INSTALL_COUNT");
        selectQuery.addSelectColumn(countCol);
        return selectQuery;
    }
    
    public int getBlacklistStatus(final Long customerId, final Long appGroupId) throws Exception {
        int type = 2;
        final SelectQuery networkQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BlacklistAppToCollection"));
        networkQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria appGroupCriteria = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria blackListCriteria = new Criteria(new Column("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)true, 0);
        networkQuery.setCriteria(appGroupCriteria.and(customerCriteria).and(blackListCriteria));
        networkQuery.addSelectColumn(new Column("BlacklistAppToCollection", "GLOBAL_BLACKLIST"));
        SelectQuery deviceBlacklistQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BlacklistAppToCollection"));
        deviceBlacklistQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        deviceBlacklistQuery.addJoin(new Join("BlacklistAppToCollection", "BlacklistAppCollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        deviceBlacklistQuery.addJoin(new Join("BlacklistAppCollectionStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria deviceBlacklistCriteria = new Criteria(new Column("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)false, 0);
        deviceBlacklistQuery.setCriteria(appGroupCriteria.and(customerCriteria).and(deviceBlacklistCriteria).and(ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria()));
        deviceBlacklistQuery = RBDAUtil.getInstance().getRBDAQuery(deviceBlacklistQuery);
        deviceBlacklistQuery.addSelectColumn(new Column("BlacklistAppToCollection", "GLOBAL_BLACKLIST"));
        final UnionQuery unionQuery = (UnionQuery)new UnionQueryImpl((Query)networkQuery, (Query)deviceBlacklistQuery, false);
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)unionQuery);
        if (dmDataSetWrapper != null && dmDataSetWrapper.next()) {
            final Boolean globalBlacklist = (Boolean)dmDataSetWrapper.getValue("GLOBAL_BLACKLIST");
            if (globalBlacklist != null) {
                type = (int)(Object)!Boolean.valueOf(globalBlacklist.toString());
            }
        }
        return type;
    }
    
    public Long getBlacklistAppCount(final Long customerId) throws APIHTTPException {
        Long count = 0L;
        final Column countColumn = new Column("BlacklistAppToCollection", "APP_GROUP_ID").distinct().count();
        countColumn.setColumnAlias("blacklistappcount");
        final SelectQuery networkQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BlacklistAppToCollection"));
        networkQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria blackListCriteria = new Criteria(new Column("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)true, 0);
        networkQuery.setCriteria(customerCriteria.and(blackListCriteria));
        networkQuery.addSelectColumn(countColumn);
        SelectQuery deviceBlacklistQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BlacklistAppToCollection"));
        deviceBlacklistQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        deviceBlacklistQuery.addJoin(new Join("BlacklistAppToCollection", "BlacklistAppCollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        deviceBlacklistQuery.addJoin(new Join("BlacklistAppCollectionStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria deviceBlacklistCriteria = new Criteria(new Column("BlacklistAppToCollection", "GLOBAL_BLACKLIST"), (Object)false, 0);
        deviceBlacklistQuery.setCriteria(customerCriteria.and(deviceBlacklistCriteria).and(ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria()));
        deviceBlacklistQuery = RBDAUtil.getInstance().getRBDAQuery(deviceBlacklistQuery);
        deviceBlacklistQuery.addSelectColumn(countColumn);
        final UnionQuery unionQuery = (UnionQuery)new UnionQueryImpl((Query)networkQuery, (Query)deviceBlacklistQuery, true);
        try {
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)unionQuery);
            if (dmDataSetWrapper != null) {
                while (dmDataSetWrapper.next()) {
                    final Object blacklistappcount = dmDataSetWrapper.getValue("blacklistappcount");
                    if (blacklistappcount != null) {
                        count += Long.valueOf(blacklistappcount.toString());
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Issue on fetching blacklisted count", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return count;
    }
    
    public SelectQuery installAppsonDeviceCount(final Long customerId, final Long deviceID) {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.addJoin(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("ManagedDevice", "MdInstalledAppResourceRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MdInstalledAppResourceRel", "MdAppToGroupRel", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2));
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        final Criteria appCatalogCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppToGroupRel", "APP_GROUP_ID"), 0);
        final Criteria managedAppCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), (Object)Column.getColumn("ManagedDevice", "RESOURCE_ID"), 0);
        final Criteria installedCriteria = new Criteria(Column.getColumn("MdAppCatalogToResource", "INSTALLED_APP_ID"), (Object)null, 1);
        selectQuery.addJoin(new Join("MdAppToGroupRel", "MdAppCatalogToResource", appCatalogCriteria.and(managedAppCriteria).and(installedCriteria), 1));
        final Criteria customerCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria deviceCriteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)deviceID, 0);
        final Criteria managedDeviceCriteria = ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria();
        selectQuery.setCriteria(customerCriteria.and(deviceCriteria).and(managedDeviceCriteria));
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        final Criteria showAppCriteria = AppsUtil.getInstance().showAppCriteria(customerId);
        if (showAppCriteria != null) {
            selectQuery.setCriteria(selectQuery.getCriteria().and(showAppCriteria));
        }
        final Column countCol = new Column("MdInstalledAppResourceRel", "APP_ID").distinct().count();
        countCol.setColumnAlias("INSTALL_COUNT");
        selectQuery.addSelectColumn(countCol);
        return selectQuery;
    }
    
    public SelectQuery blacklistDeviceCountQuery(final Long customerID, final Long deviceId, final boolean isPending) {
        SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("BlacklistAppCollectionStatus"));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppCollectionStatus", "BlacklistAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
        Criteria statusCriteria = null;
        if (isPending) {
            statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 1, 2, 3, 9 }, 8);
        }
        else {
            statusCriteria = new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"), (Object)new Integer[] { 6, 7, 10, 4, 11 }, 8);
        }
        final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria resourceCriteria = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)deviceId, 0);
        selectQuery.setCriteria(statusCriteria.and(resourceCriteria).and(customerCriteria));
        final Column column = new Column("BlacklistAppToCollection", "APP_GROUP_ID").distinct().count();
        column.setColumnAlias("blacklistPendingCount");
        selectQuery.addSelectColumn(column);
        return selectQuery;
    }
    
    public SelectQuery blocklistedAppsByDeviceSelectQuery(final Long customerId, final Long deviceId, final boolean countFlag) {
        final SelectQuery appViewQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppGroupDetails"));
        appViewQuery.addJoin(new Join("MdAppGroupDetails", "MdPackageToAppData", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        appViewQuery.addJoin(new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        appViewQuery.addJoin(new Join("BlacklistAppToCollection", "BlacklistAppCollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        appViewQuery.addJoin(new Join("BlacklistAppCollectionStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        appViewQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria customerCriteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria deviceCriteria = new Criteria(new Column("ManagedDevice", "RESOURCE_ID"), (Object)deviceId, 0);
        final Criteria blackListedCriteria = new Criteria(new Column("BlacklistAppCollectionStatus", "STATUS"), (Object)new int[] { 4, 11, 10, 7, 6 }, 8);
        appViewQuery.setCriteria(customerCriteria.and(deviceCriteria).and(blackListedCriteria).and(ManagedDeviceHandler.getInstance().getSuccessfullyEnrolledCriteria()));
        if (countFlag) {
            final SelectQuery appViewCountQuery = appViewQuery;
            final Column identifierColumn = new Column("MdAppGroupDetails", "IDENTIFIER").distinct().count();
            appViewCountQuery.addSelectColumn(identifierColumn);
            return appViewCountQuery;
        }
        appViewQuery.addSelectColumn(new Column("MdAppGroupDetails", "APP_GROUP_ID"));
        appViewQuery.addSelectColumn(new Column("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        appViewQuery.addSelectColumn(new Column("MdAppGroupDetails", "PLATFORM_TYPE"));
        appViewQuery.addSelectColumn(new Column("MdAppGroupDetails", "IDENTIFIER"));
        appViewQuery.addSelectColumn(new Column("BlacklistAppCollectionStatus", "SCOPE"));
        appViewQuery.addSelectColumn(new Column("BlacklistAppCollectionStatus", "STATUS"));
        appViewQuery.addSelectColumn(new Column("MdPackageToAppData", "DISPLAY_IMAGE_LOC"));
        return appViewQuery;
    }
    
    static {
        BlacklistQueryUtils.blacklistMailUtils = null;
    }
}
