package com.adventnet.sym.server.mdm.config;

import java.util.Hashtable;
import org.json.simple.JSONArray;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Map;
import javax.transaction.TransactionManager;
import com.adventnet.db.api.RelationalAPI;
import com.me.idps.core.util.DirectoryUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.UpdateQuery;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.sql.Connection;
import com.adventnet.ds.query.GroupByClause;
import java.util.Arrays;
import com.me.idps.core.util.DirectoryQueryutil;
import com.me.mdm.core.management.ManagementUtil;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.CaseExpression;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import java.util.List;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;

public class ResourceSummaryHandler
{
    private static final int DOC_QUERY = 1;
    private static final int MEMBER_QUERY = 2;
    private static final int COLLECTION_QUERY = 3;
    private static final String REQUESTED_AT = "REQUESTED_AT";
    private static ResourceSummaryHandler resourceSummaryHandler;
    private static final Logger LOGGER;
    private static final int[] QUERY_TYPES;
    
    public static ResourceSummaryHandler getInstance() {
        if (ResourceSummaryHandler.resourceSummaryHandler == null) {
            ResourceSummaryHandler.resourceSummaryHandler = new ResourceSummaryHandler();
        }
        return ResourceSummaryHandler.resourceSummaryHandler;
    }
    
    private Criteria getCriteria(final int resType, Criteria criteria) {
        Criteria baseCri = null;
        if (resType == 101) {
            baseCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0);
        }
        else if (resType == 120) {
            baseCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Integer[] { 120, 121 }, 8);
        }
        else if (resType == 2) {
            baseCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0);
        }
        if (criteria != null) {
            criteria = criteria.and(baseCri);
        }
        else {
            criteria = baseCri;
        }
        return criteria;
    }
    
    private List<Join> getJoin(final int resType, final List<Join> joins) {
        final List<Join> retJoins = new ArrayList<Join>();
        retJoins.add(new Join("Resource", "MDMResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        if (resType == 101) {
            retJoins.add(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        }
        else if (resType == 120) {
            retJoins.add(new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        }
        if (joins != null) {
            retJoins.addAll(joins);
        }
        return retJoins;
    }
    
    private HashMap<String, Column> getCGsummaryColMap(final int queryType) {
        final HashMap<String, Column> cgColMap = new HashMap<String, Column>();
        cgColMap.put("RESOURCE_ID", Column.getColumn("CustomGroup", "RESOURCE_ID", "inner_cg.RES_ID" + String.valueOf(queryType)));
        switch (queryType) {
            case 2: {
                final Criteria memberCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0).or(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)null, 0));
                final CaseExpression appDistinctCountExp = new CaseExpression("RESOURCETOPROFILESUMMARY.MEMBER_COUNT");
                appDistinctCountExp.addWhen(memberCri, (Object)Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
                final Column memberDistinctCountCol = (Column)Column.createFunction("COUNT", new Object[] { appDistinctCountExp });
                memberDistinctCountCol.setType(4);
                memberDistinctCountCol.setColumnAlias("RESOURCETOPROFILESUMMARY.MEMBER_COUNT");
                cgColMap.put("MEMBER_COUNT", memberDistinctCountCol);
                break;
            }
            case 1: {
                final Column docCountCol = (Column)Column.createFunction("COUNT", new Object[] { new Column("DocumentToDeviceGroup", "DOC_ID") });
                docCountCol.setType(4);
                docCountCol.setColumnAlias("RESOURCETOPROFILESUMMARY.DOC_COUNT");
                cgColMap.put("DOC_COUNT", docCountCol);
                break;
            }
            case 3: {
                final Criteria appCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0).and(new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)Boolean.FALSE, 0, (boolean)Boolean.FALSE));
                final CaseExpression appDistinctCountExp = new CaseExpression("RESOURCETOPROFILESUMMARY.APP_COUNT");
                appDistinctCountExp.addWhen(appCri, (Object)Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
                final Column appDistinctCountCol = (Column)Column.createFunction("COUNT", new Object[] { appDistinctCountExp });
                appDistinctCountCol.setType(4);
                appDistinctCountCol.setColumnAlias("RESOURCETOPROFILESUMMARY.APP_COUNT");
                final Criteria profileCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)new int[] { 10, 1 }, 8).and(new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)Boolean.FALSE, 0, (boolean)Boolean.FALSE));
                final CaseExpression profileDistinctCountExp = new CaseExpression("RESOURCETOPROFILESUMMARY.PROFILE_COUNT");
                profileDistinctCountExp.addWhen(profileCri, (Object)Column.getColumn("RecentProfileForGroup", "PROFILE_ID"));
                final Column profileDistinctCountCol = (Column)Column.createFunction("COUNT", new Object[] { profileDistinctCountExp });
                profileDistinctCountCol.setType(4);
                profileDistinctCountCol.setColumnAlias("RESOURCETOPROFILESUMMARY.PROFILE_COUNT");
                cgColMap.put("APP_COUNT", appDistinctCountCol);
                cgColMap.put("PROFILE_COUNT", profileDistinctCountCol);
                break;
            }
        }
        return cgColMap;
    }
    
    private HashMap<String, Column> getDeviceSummaryColMap(final int queryType) {
        final HashMap<String, Column> mdColMap = new HashMap<String, Column>();
        mdColMap.put("RESOURCE_ID", Column.getColumn("ManagedDevice", "RESOURCE_ID", "inner_md.RES_ID" + String.valueOf(queryType)));
        switch (queryType) {
            case 1: {
                final Criteria docCri = new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)Boolean.TRUE, 0);
                final CaseExpression docCountExp = new CaseExpression("RESOURCETOPROFILESUMMARY.DOC_COUNT");
                docCountExp.addWhen(docCri, (Object)Column.getColumn("DocumentManagedDeviceRel", "DOC_ID"));
                final Column docCountCol = (Column)Column.createFunction("COUNT", new Object[] { docCountExp });
                docCountCol.setType(4);
                docCountCol.setColumnAlias("RESOURCETOPROFILESUMMARY.DOC_COUNT");
                mdColMap.put("DOC_COUNT", docCountCol);
                break;
            }
            case 3: {
                final Criteria appCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0).and(new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)Boolean.FALSE, 0, (boolean)Boolean.FALSE));
                final CaseExpression appDistinctCountExp = new CaseExpression("RESOURCETOPROFILESUMMARY.APP_COUNT");
                appDistinctCountExp.addWhen(appCri, (Object)Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
                final Column appDistinctCountCol = (Column)Column.createFunction("COUNT", new Object[] { appDistinctCountExp });
                appDistinctCountCol.setType(4);
                appDistinctCountCol.setColumnAlias("RESOURCETOPROFILESUMMARY.APP_COUNT");
                final Criteria profileCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)new int[] { 1, 10 }, 8).and(new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)Boolean.FALSE, 0, (boolean)Boolean.FALSE));
                final CaseExpression profileDistinctCountExp = new CaseExpression("RESOURCETOPROFILESUMMARY.PROFILE_COUNT");
                profileDistinctCountExp.addWhen(profileCri, (Object)Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
                final Column profileDistinctCountCol = (Column)Column.createFunction("COUNT", new Object[] { profileDistinctCountExp });
                profileDistinctCountCol.setType(4);
                profileDistinctCountCol.setColumnAlias("RESOURCETOPROFILESUMMARY.PROFILE_COUNT");
                mdColMap.put("APP_COUNT", appDistinctCountCol);
                mdColMap.put("PROFILE_COUNT", profileDistinctCountCol);
                break;
            }
            case 2: {
                return null;
            }
        }
        return mdColMap;
    }
    
    private HashMap<String, Column> getUsersSummaryColMap(final int queryType) {
        final HashMap<String, Column> userColMap = new HashMap<String, Column>();
        userColMap.put("RESOURCE_ID", Column.getColumn("MDMResource", "RESOURCE_ID", "inner_u.RES_ID" + String.valueOf(queryType)));
        switch (queryType) {
            case 3: {
                final Criteria appCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0).and(new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)Boolean.FALSE, 0, (boolean)Boolean.FALSE));
                final CaseExpression appDistinctCountExp = new CaseExpression("RESOURCETOPROFILESUMMARY.APP_COUNT");
                appDistinctCountExp.addWhen(appCri, (Object)Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
                final Column appDistinctCountCol = (Column)Column.createFunction("COUNT", new Object[] { appDistinctCountExp });
                appDistinctCountCol.setType(4);
                appDistinctCountCol.setColumnAlias("RESOURCETOPROFILESUMMARY.APP_COUNT");
                final Criteria profileCri = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)new int[] { 1, 10 }, 8).and(new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)Boolean.FALSE, 0, (boolean)Boolean.FALSE));
                final CaseExpression profileDistinctCountExp = new CaseExpression("RESOURCETOPROFILESUMMARY.PROFILE_COUNT");
                profileDistinctCountExp.addWhen(profileCri, (Object)Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
                final Column profileDistinctCountCol = (Column)Column.createFunction("COUNT", new Object[] { profileDistinctCountExp });
                profileDistinctCountCol.setType(4);
                profileDistinctCountCol.setColumnAlias("RESOURCETOPROFILESUMMARY.PROFILE_COUNT");
                userColMap.put("APP_COUNT", appDistinctCountCol);
                userColMap.put("PROFILE_COUNT", profileDistinctCountCol);
                break;
            }
            case 2: {
                final Criteria userNotInTrashCriteria = new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1);
                final Criteria managedCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
                final CaseExpression managedDeviceCountExp = new CaseExpression("RESOURCETOPROFILESUMMARY.MEMBER_COUNT");
                managedDeviceCountExp.addWhen(managedCri.and(userNotInTrashCriteria), (Object)new Column("ManagedDevice", "RESOURCE_ID"));
                final Column managedDeviceCountCol = (Column)Column.createFunction("COUNT", new Object[] { managedDeviceCountExp });
                managedDeviceCountCol.setType(4);
                managedDeviceCountCol.setColumnAlias("RESOURCETOPROFILESUMMARY.MEMBER_COUNT");
                userColMap.put("MEMBER_COUNT", managedDeviceCountCol);
                break;
            }
            case 1: {
                final Criteria docCri = new Criteria(Column.getColumn("DocumentToMDMResource", "ASSOCIATE"), (Object)Boolean.TRUE, 0);
                final CaseExpression docUserCountExp = new CaseExpression("RESOURCETOPROFILESUMMARY.DOC_COUNT");
                docUserCountExp.addWhen(docCri, (Object)new Column("DocumentToMDMResource", "DOC_ID"));
                final Column docCountCol = (Column)Column.createFunction("COUNT", new Object[] { docUserCountExp });
                docCountCol.setType(4);
                docCountCol.setColumnAlias("RESOURCETOPROFILESUMMARY.DOC_COUNT");
                userColMap.put("DOC_COUNT", docCountCol);
                break;
            }
        }
        return userColMap;
    }
    
    private HashMap<String, Column> getResSummaryColMap(final int resType, final int queryType) {
        if (resType == 101) {
            return this.getCGsummaryColMap(queryType);
        }
        if (resType == 120) {
            return this.getDeviceSummaryColMap(queryType);
        }
        if (resType == 2) {
            return this.getUsersSummaryColMap(queryType);
        }
        return null;
    }
    
    private SelectQuery getResSummaryQuery(final int resType, final List<Join> joins, final Criteria criteria) {
        final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        if (joins != null) {
            for (final Join join : joins) {
                subQuery.addJoin(join);
            }
        }
        if (resType == 101) {
            subQuery.addJoin(new Join("CustomGroup", "ResourceToProfileSummary", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        }
        else if (resType == 120) {
            subQuery.addJoin(new Join("ManagedDevice", "ResourceToProfileSummary", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        }
        else if (resType == 2) {
            subQuery.addJoin(new Join("MDMResource", "ResourceToProfileSummary", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        }
        if (criteria != null) {
            subQuery.setCriteria(criteria);
        }
        subQuery.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "APP_COUNT"));
        subQuery.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "DOC_COUNT"));
        subQuery.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "RESOURCE_ID"));
        subQuery.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "MEMBER_COUNT"));
        subQuery.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "PROFILE_COUNT"));
        return subQuery;
    }
    
    private SelectQuery getResSummaryQuery(final int resType, final List<Join> joins, final Criteria criteria, final HashMap<String, Column> cgColMap, final int queryType) {
        SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        if (joins != null) {
            for (final Join join : joins) {
                subQuery.addJoin(join);
            }
        }
        if (resType == 101) {
            switch (queryType) {
                case 1: {
                    subQuery.addJoin(new Join("CustomGroup", "DocumentToDeviceGroup", new String[] { "RESOURCE_ID" }, new String[] { "CUSTOMGROUP_ID" }, 1));
                    break;
                }
                case 2: {
                    subQuery.addJoin(new Join("CustomGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1));
                    subQuery.addJoin(new Join("CustomGroupMemberRel", "ManagedDevice", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                    break;
                }
                case 3: {
                    subQuery.addJoin(new Join("CustomGroup", "RecentProfileForGroup", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_ID" }, 1));
                    subQuery.addJoin(new Join("RecentProfileForGroup", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
                    break;
                }
            }
        }
        else if (resType == 120) {
            Criteria deviceJoinCri = new Criteria(Column.getColumn("RecentProfileForResource", "PROFILE_ID"), (Object)Column.getColumn("Profile", "PROFILE_ID"), 0);
            final Criteria pltFormJoinCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)Column.getColumn("Profile", "PLATFORM_TYPE"), 0).or(new Criteria(Column.getColumn("Profile", "PLATFORM_TYPE"), (Object)new Integer[] { 6, 7 }, 8));
            deviceJoinCri = deviceJoinCri.and(pltFormJoinCriteria);
            switch (queryType) {
                case 1: {
                    subQuery.addJoin(new Join("ManagedDevice", "DocumentManagedDeviceRel", new String[] { "RESOURCE_ID" }, new String[] { "MANAGEDDEVICE_ID" }, 1));
                    subQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 1));
                    break;
                }
                case 3: {
                    subQuery.addJoin(new Join("ManagedDevice", "RecentProfileForResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                    subQuery.addJoin(new Join("RecentProfileForResource", "Profile", ManagementUtil.generateCriteria(deviceJoinCri, 1), 1));
                    break;
                }
            }
        }
        else if (resType == 2) {
            switch (queryType) {
                case 3: {
                    subQuery.addJoin(new Join("MDMResource", "RecentProfileForResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                    subQuery.addJoin(new Join("RecentProfileForResource", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
                    break;
                }
                case 1: {
                    subQuery.addJoin(new Join("MDMResource", "DocumentToMDMResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                    break;
                }
                case 2: {
                    subQuery.addJoin(new Join("MDMResource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
                    subQuery.addJoin(new Join("ManagedUser", "ManagedUserToDevice", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
                    subQuery.addJoin(new Join("ManagedUserToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                    break;
                }
            }
        }
        if (criteria != null) {
            subQuery.setCriteria(criteria);
        }
        subQuery = DirectoryQueryutil.getInstance().getSelectQuery(subQuery, (HashMap)cgColMap);
        subQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(cgColMap.get("RESOURCE_ID")))));
        return subQuery;
    }
    
    private int insertResourceToProfileSummary(final Connection connection, final List<Join> joins, final Criteria criteria, final boolean inTransactionWhenForceUpdate) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        if (joins != null) {
            for (final Join join : joins) {
                selectQuery.addJoin(join);
            }
        }
        selectQuery.setCriteria(criteria.and(new Criteria(Column.getColumn("ResourceToProfileSummary", "RESOURCE_ID"), (Object)null, 0)));
        selectQuery.addJoin(new Join("Resource", "ResourceToProfileSummary", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        final HashMap<String, Column> colMap = new HashMap<String, Column>();
        colMap.put("RESOURCE_ID", Column.getColumn("Resource", "RESOURCE_ID"));
        final int insertedRows = DirectoryQueryutil.getInstance().executeInsertQuery(connection, selectQuery, "ResourceToProfileSummary", (HashMap)colMap, (HashMap)null, inTransactionWhenForceUpdate);
        return insertedRows;
    }
    
    private void addTaskToQueue(final int resType) throws Exception {
        final Long currentTime = System.currentTimeMillis();
        final DCQueueData queueData = new DCQueueData();
        queueData.postTime = currentTime;
        queueData.fileName = String.valueOf(currentTime);
        final DCQueue queue = DCQueueHandler.getQueue("mdm-res-summary");
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put((Object)"REQUESTED_AT", (Object)currentTime);
        jsonObject.put((Object)"RESOURCE_TYPE", (Object)resType);
        jsonObject.put((Object)"Task", (Object)"ResourceToProfileSummary");
        queue.addToQueue(queueData, jsonObject.toString());
    }
    
    private String getCacheKey(final int resType) {
        return "ResourceToProfileSummary_" + resType + "_UPDATED_AT";
    }
    
    private boolean isSummaryComputationRequired(final String cacheKey, final long requestedAt) {
        final Long lastResSummaryUpdatedAt = (Long)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheKey, 2);
        return lastResSummaryUpdatedAt == null || (lastResSummaryUpdatedAt != null && lastResSummaryUpdatedAt <= requestedAt);
    }
    
    public void updateResSummary(final org.json.JSONObject qNode) throws Exception {
        if (qNode.has("RESOURCE_TYPE") && qNode.has("REQUESTED_AT")) {
            final Long requestedAt = qNode.getLong("REQUESTED_AT");
            final int resType = qNode.getInt("RESOURCE_TYPE");
            final String cacheKey = this.getCacheKey(resType);
            if (requestedAt != null) {
                if (this.isSummaryComputationRequired(cacheKey, requestedAt)) {
                    final long lastResSummaryUpdatedAt = System.currentTimeMillis();
                    this.updateResSummary(resType, true, true);
                    ApiFactoryProvider.getCacheAccessAPI().putCache(cacheKey, (Object)lastResSummaryUpdatedAt, 2, 60);
                }
                else {
                    ResourceSummaryHandler.LOGGER.log(Level.FINE, "skipping res summary update");
                }
            }
        }
    }
    
    private UpdateQuery setUpdateValAndCri(final UpdateQuery updateQuery, final String colName, final Column updateCol) {
        final Criteria cri = new Criteria(Column.getColumn("ResourceToProfileSummary", colName), (Object)updateCol, 1);
        Criteria queryCri = updateQuery.getCriteria();
        if (queryCri != null) {
            queryCri = queryCri.or(cri);
        }
        else {
            queryCri = cri;
        }
        updateQuery.setUpdateColumn(colName, (Object)updateCol);
        updateQuery.setCriteria(queryCri);
        return updateQuery;
    }
    
    private void updateResSummaryNow(final Connection connection, final int resType, final boolean inTransactionWhenForceUpdate) throws Exception {
        List<Join> joins = null;
        joins = this.getJoin(resType, joins);
        Criteria criteria = null;
        criteria = this.getCriteria(resType, criteria);
        this.insertResourceToProfileSummary(connection, joins, criteria, inTransactionWhenForceUpdate);
        UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("ResourceToProfileSummary");
        for (final int queryType : ResourceSummaryHandler.QUERY_TYPES) {
            final HashMap<String, Column> colMap = this.getResSummaryColMap(resType, queryType);
            if (colMap != null) {
                final SelectQuery subQuery = this.getResSummaryQuery(resType, joins, criteria, colMap, queryType);
                final DerivedTable dtab = new DerivedTable("innerTable_" + String.valueOf(queryType), (Query)subQuery);
                switch (queryType) {
                    case 3: {
                        updateQuery.addJoin(new Join(Table.getTable("ResourceToProfileSummary"), (Table)dtab, new String[] { "RESOURCE_ID" }, new String[] { colMap.get("RESOURCE_ID").getColumnAlias() }, 2));
                        updateQuery = this.setUpdateValAndCri(updateQuery, "APP_COUNT", Column.getColumn(dtab.getTableAlias(), colMap.get("APP_COUNT").getColumnAlias()));
                        updateQuery = this.setUpdateValAndCri(updateQuery, "PROFILE_COUNT", Column.getColumn(dtab.getTableAlias(), colMap.get("PROFILE_COUNT").getColumnAlias()));
                        break;
                    }
                    case 1: {
                        updateQuery.addJoin(new Join(Table.getTable("ResourceToProfileSummary"), (Table)dtab, new String[] { "RESOURCE_ID" }, new String[] { colMap.get("RESOURCE_ID").getColumnAlias() }, 2));
                        updateQuery = this.setUpdateValAndCri(updateQuery, "DOC_COUNT", Column.getColumn(dtab.getTableAlias(), colMap.get("DOC_COUNT").getColumnAlias()));
                        break;
                    }
                    case 2: {
                        updateQuery.addJoin(new Join(Table.getTable("ResourceToProfileSummary"), (Table)dtab, new String[] { "RESOURCE_ID" }, new String[] { colMap.get("RESOURCE_ID").getColumnAlias() }, 2));
                        updateQuery = this.setUpdateValAndCri(updateQuery, "MEMBER_COUNT", Column.getColumn(dtab.getTableAlias(), colMap.get("MEMBER_COUNT").getColumnAlias()));
                        break;
                    }
                }
            }
        }
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, inTransactionWhenForceUpdate);
    }
    
    private void updateResSummary(final Connection connection, final int resType, final boolean force, final boolean fromQueue) throws Exception {
        if (!force) {
            this.checkAndInsertDefaultRowImmediately(connection, resType);
            this.addTaskToQueue(resType);
        }
        else if (fromQueue) {
            this.updateResSummaryNow(connection, resType, false);
        }
        else {
            this.updateResSummaryNow(connection, resType, true);
        }
    }
    
    public void updateResSummary(final Connection connection, final int resType, final boolean force) throws Exception {
        this.updateResSummary(connection, resType, force, false);
    }
    
    public void updateResSummary(final Connection connection, final int resType) throws Exception {
        this.updateResSummary(connection, resType, false);
    }
    
    public void updateResSummary(final Connection connection) throws Exception {
        this.updateResSummary(connection, 2);
        this.updateResSummary(connection, 101);
        this.updateResSummary(connection, 120);
    }
    
    private void updateResSummary(final int resType, final boolean force, final boolean fromQueue) throws Exception {
        int tmStatus = 0;
        try {
            final TransactionManager tm = SyMUtil.getUserTransaction();
            if (tm != null) {
                tmStatus = tm.getStatus();
            }
        }
        catch (final Exception ex) {
            ResourceSummaryHandler.LOGGER.log(Level.SEVERE, null, ex);
        }
        if (tmStatus == 6) {
            Connection connection = null;
            try {
                if (force) {
                    DirectoryUtil.getInstance().clearActiveTransactionsIfAnyWithoutException();
                    connection = RelationalAPI.getInstance().getConnection();
                }
                this.updateResSummary(connection, resType, force, fromQueue);
            }
            catch (final Exception ex2) {
                throw ex2;
            }
            finally {
                if (connection != null) {
                    try {
                        connection.close();
                    }
                    catch (final Exception ex3) {
                        ResourceSummaryHandler.LOGGER.log(Level.SEVERE, "exception in closing connection", ex3);
                    }
                }
            }
        }
        else {
            ResourceSummaryHandler.LOGGER.log(Level.WARNING, "skipping res summay count updation as it is being invoked from a transaction!!! This method should be called only outside a transaction at the end of your workflow");
        }
    }
    
    public void updateResSummary(final int resType, final boolean force) throws Exception {
        this.updateResSummary(resType, force, false);
    }
    
    public void updateResSummary(final int resType) throws Exception {
        this.updateResSummary(resType, false);
    }
    
    public void updateResSummary() throws Exception {
        this.updateResSummary(2, false);
        this.updateResSummary(101, false);
        this.updateResSummary(120, false);
    }
    
    public Map getResSummary(final int resType, List<Join> joins, Criteria criteria) throws Exception {
        Connection connection = null;
        try {
            connection = RelationalAPI.getInstance().getConnection();
            this.updateResSummary(connection, resType);
            joins = this.getJoin(resType, joins);
            criteria = this.getCriteria(resType, criteria);
            final SelectQuery summaryQuery = this.getResSummaryQuery(resType, joins, criteria);
            final JSONArray jsonArray = MDMUtil.executeSelectQuery(connection, summaryQuery);
            final HashMap<Long, Properties> summaryMap = new HashMap<Long, Properties>();
            for (int i = 0; i < jsonArray.size(); ++i) {
                final JSONObject jsObject = (JSONObject)jsonArray.get(i);
                final Long resID = (Long)jsObject.get((Object)"RESOURCE_ID");
                final Properties properties = new Properties();
                ((Hashtable<String, Integer>)properties).put("APP_COUNT", Integer.valueOf(String.valueOf(jsObject.get((Object)"APP_COUNT"))));
                ((Hashtable<String, Integer>)properties).put("DOC_COUNT", Integer.valueOf(String.valueOf(jsObject.get((Object)"DOC_COUNT"))));
                ((Hashtable<String, Integer>)properties).put("MEMBER_COUNT", Integer.valueOf(String.valueOf(jsObject.get((Object)"MEMBER_COUNT"))));
                ((Hashtable<String, Integer>)properties).put("PROFILE_COUNT", Integer.valueOf(String.valueOf(jsObject.get((Object)"PROFILE_COUNT"))));
                summaryMap.put(resID, properties);
            }
            return summaryMap;
        }
        catch (final Exception ex) {
            throw ex;
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
    
    private void checkAndInsertDefaultRowImmediately(Connection connection, final int resType) {
        int tmStatus = 0;
        try {
            final TransactionManager tm = SyMUtil.getUserTransaction();
            if (tm != null) {
                tmStatus = tm.getStatus();
            }
        }
        catch (final Exception ex) {
            ResourceSummaryHandler.LOGGER.log(Level.SEVERE, null, ex);
        }
        if (tmStatus == 6) {
            boolean connectionOpened = false;
            final List<Join> joins = this.getJoin(resType, null);
            final Criteria criteria = this.getCriteria(resType, null);
            final String cacheKey = "ResourceToProfileSummary_" + resType + "_ADDED_AT";
            try {
                final Boolean onGoingInsertionsForOrg = Boolean.valueOf(String.valueOf(ApiFactoryProvider.getCacheAccessAPI().getCache(cacheKey, 2)));
                if (!onGoingInsertionsForOrg) {
                    ApiFactoryProvider.getCacheAccessAPI().putCache(cacheKey, (Object)Boolean.TRUE, 2);
                    if (connection == null) {
                        connectionOpened = true;
                        connection = RelationalAPI.getInstance().getConnection();
                    }
                    this.insertResourceToProfileSummary(connection, joins, criteria, false);
                }
            }
            catch (final Exception ex2) {
                ResourceSummaryHandler.LOGGER.log(Level.SEVERE, null, ex2);
            }
            finally {
                try {
                    if (connectionOpened && connection != null) {
                        connection.close();
                    }
                }
                catch (final Exception ex3) {
                    ResourceSummaryHandler.LOGGER.log(Level.SEVERE, null, ex3);
                }
                ApiFactoryProvider.getCacheAccessAPI().putCache(cacheKey, (Object)Boolean.FALSE, 2);
            }
        }
        else {
            ResourceSummaryHandler.LOGGER.log(Level.SEVERE, "skipping as it is being invoked from a transaction!!! This method should be called only outside a transaction at the end of your workflow");
        }
    }
    
    static {
        ResourceSummaryHandler.resourceSummaryHandler = null;
        LOGGER = Logger.getLogger("MDMLogger");
        QUERY_TYPES = new int[] { 1, 2, 3 };
    }
}
