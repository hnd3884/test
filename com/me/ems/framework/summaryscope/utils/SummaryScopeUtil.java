package com.me.ems.framework.summaryscope.utils;

import com.adventnet.persistence.WritableDataObject;
import java.util.Arrays;
import com.me.ems.framework.summaryscope.listener.SummaryScopeEvent;
import com.adventnet.persistence.DataAccess;
import java.sql.Connection;
import com.adventnet.ds.query.DataSet;
import java.util.Collections;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.UpdateQuery;
import java.util.Iterator;
import com.adventnet.ds.query.DeleteQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import java.util.HashMap;
import com.me.ems.framework.summaryscope.listener.SummaryScopeListener;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SummaryScopeUtil
{
    private Logger logger;
    private static String sourceClass;
    private static SummaryScopeUtil summaryScopeUtil;
    private static ArrayList<SummaryScopeListener> summaryScopeListeners;
    
    public SummaryScopeUtil() {
        this.logger = Logger.getLogger("SummaryScopeLogger");
    }
    
    public static SummaryScopeUtil getInstance() {
        if (SummaryScopeUtil.summaryScopeUtil == null) {
            SummaryScopeUtil.summaryScopeUtil = new SummaryScopeUtil();
        }
        return SummaryScopeUtil.summaryScopeUtil;
    }
    
    public HashMap<String, String> getScopeTableValues(final Integer scopeType) {
        String tableName = "TechnicianScopeRel";
        String columnName = "TECH_ID";
        if (scopeType.equals(SummaryScopeConstants.CUSTOM_GROUP)) {
            tableName = "CGScopeRel";
            columnName = "GROUP_RESOURCE_ID";
        }
        else if (scopeType.equals(SummaryScopeConstants.REMOTE_OFFICE)) {
            tableName = "ROScopeRel";
            columnName = "BRANCH_OFFICE_ID";
        }
        else if (scopeType.equals(SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP)) {
            tableName = "CustomerScopeRel";
            columnName = "CUSTOMER_ID";
        }
        final HashMap<String, String> scopeMap = new HashMap<String, String>();
        scopeMap.put("tableName", tableName);
        scopeMap.put("columnName", columnName);
        return scopeMap;
    }
    
    public List<Long> clearScopeRel(final Long valueID, final Integer scopeType) {
        final HashMap<String, String> scopeMap = this.getScopeTableValues(scopeType);
        final String tableName = scopeMap.get("tableName");
        final String columnName = scopeMap.get("columnName");
        List<Long> affectedScopeIDs = new ArrayList<Long>();
        try {
            affectedScopeIDs = this.getAllSummaryScopeIDs(valueID, scopeType);
            Criteria criteria = new Criteria(Column.getColumn(tableName, columnName), (Object)valueID, 0);
            DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tableName);
            deleteQuery.setCriteria(criteria);
            SyMUtil.getPersistence().delete(deleteQuery);
            if ((scopeType.equals(SummaryScopeConstants.CUSTOM_GROUP) || scopeType.equals(SummaryScopeConstants.REMOTE_OFFICE)) && affectedScopeIDs.size() > 0) {
                for (final Long affectedScopeID : affectedScopeIDs) {
                    final List<Long> valueIDs = this.getSummaryScopeValueIDs(affectedScopeID, scopeType);
                    if (valueIDs.size() > 0) {
                        final Long existingScopeID = this.getSimilarScopeForTechnician(valueIDs, scopeType, affectedScopeID);
                        if (existingScopeID.equals(-1L)) {
                            continue;
                        }
                        final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("TechnicianScopeRel");
                        query.setCriteria(new Criteria(Column.getColumn("TechnicianScopeRel", "SUMMARY_SCOPE_ID"), (Object)affectedScopeID, 0));
                        query.setUpdateColumn("SUMMARY_SCOPE_ID", (Object)existingScopeID);
                        SyMUtil.getPersistence().update(query);
                        deleteQuery = (DeleteQuery)new DeleteQueryImpl("SummaryScopeDefn");
                        criteria = new Criteria(Column.getColumn("SummaryScopeDefn", "SUMMARY_SCOPE_ID"), (Object)affectedScopeID, 0);
                        deleteQuery.setCriteria(criteria);
                        SyMUtil.getPersistence().delete(deleteQuery);
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception:: while deleting the existing entries of tableName:" + tableName + ", columnName:" + columnName + ". Stacktrace::", ex);
        }
        this.clearUnusedScopes();
        return affectedScopeIDs;
    }
    
    private void clearUnusedScopes() {
        final List<Long> summaryScopeIDs = new ArrayList<Long>();
        try {
            final Integer[] array;
            final Integer[] summaryScopeArray = array = new Integer[] { SummaryScopeConstants.TECHNICIAN, SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP, SummaryScopeConstants.CUSTOM_GROUP, SummaryScopeConstants.REMOTE_OFFICE };
            for (final Integer summaryScopeType : array) {
                final HashMap<String, String> targetScopeMap = this.getScopeTableValues(summaryScopeType);
                final String targetTableName = targetScopeMap.get("tableName");
                final String targetColumnName = targetScopeMap.get("columnName");
                final DeleteQuery query = (DeleteQuery)new DeleteQueryImpl("SummaryScopeDefn");
                query.addJoin(new Join("SummaryScopeDefn", targetTableName, new String[] { "SUMMARY_SCOPE_ID" }, new String[] { "SUMMARY_SCOPE_ID" }, 1));
                Criteria criteria = new Criteria(Column.getColumn(targetTableName, targetColumnName), (Object)null, 0);
                criteria = criteria.and(new Criteria(Column.getColumn("SummaryScopeDefn", "SCOPE_TYPE"), (Object)summaryScopeType, 0));
                query.setCriteria(criteria);
                this.logger.log(Level.INFO, RelationalAPI.getInstance().getDeleteSQL(query));
                SyMUtil.getPersistence().delete(query);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while clearing unused scopes. Regenerate all the summary scopes again.", ex);
        }
    }
    
    public void mapToExistingScope(final Long valueID, final Long scopeID, final Integer scopeType) {
        if (scopeID != -1L) {
            final HashMap<String, String> scopeMap = this.getScopeTableValues(scopeType);
            final String tableName = scopeMap.get("tableName");
            final String columnName = scopeMap.get("columnName");
            try {
                Criteria criteria = new Criteria(Column.getColumn(tableName, columnName), (Object)valueID, 0);
                criteria = criteria.and(new Criteria(Column.getColumn(tableName, "SUMMARY_SCOPE_ID"), (Object)scopeID, 0));
                DataObject dataObject = SyMUtil.getPersistence().get(tableName, criteria);
                if (dataObject == null || dataObject.isEmpty()) {
                    final Row row = new Row(tableName);
                    row.set(columnName, (Object)valueID);
                    row.set("SUMMARY_SCOPE_ID", (Object)scopeID);
                    dataObject = SyMUtil.getPersistence().constructDataObject();
                    dataObject.addRow(row);
                    SyMUtil.getPersistence().update(dataObject);
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception while mapping the existing scopeID:" + scopeID + " to new value:" + valueID + "of scopetype:" + scopeType, ex);
            }
        }
    }
    
    private Long createAllManagedScope() {
        Long summaryScopeID = -1L;
        final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
        if (customerID != null) {
            final List<Long> techIDs = DMUserHandler.getLoginIDsForAAARoleName("All_Managed_Computer", customerID);
            summaryScopeID = this.createSummaryScope(customerID, SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP);
            for (final Long techID : techIDs) {
                this.mapToExistingScope(techID, summaryScopeID, SummaryScopeConstants.TECHNICIAN);
            }
        }
        return summaryScopeID;
    }
    
    public Long getOrCreateAllManagedScope() {
        Long summaryScopeID = -1L;
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerScopeRel"));
            query.addJoin(new Join("CustomerScopeRel", "SummaryScopeDefn", new String[] { "SUMMARY_SCOPE_ID" }, new String[] { "SUMMARY_SCOPE_ID" }, 2));
            Criteria criteria = new Criteria(Column.getColumn("SummaryScopeDefn", "SCOPE_TYPE"), (Object)SummaryScopeConstants.CUSTOMER_OR_ALL_MANAGED_COMP, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("CustomerScopeRel", "CUSTOMER_ID"), (Object)customerID, 0));
            query.addSelectColumn(Column.getColumn("SummaryScopeDefn", "*"));
            query.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("SummaryScopeDefn");
                summaryScopeID = (Long)row.get("SUMMARY_SCOPE_ID");
            }
            else {
                summaryScopeID = this.createAllManagedScope();
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while fetching the all managed scope", ex);
        }
        return summaryScopeID;
    }
    
    public Long createTechSummaryScope(final Long techID, final List<Long> scopeList, final Integer scopeType) {
        Long summaryScopeID = -1L;
        try {
            final HashMap<String, String> scopeMap = this.getScopeTableValues(scopeType);
            final String tableName = scopeMap.get("tableName");
            final String columnName = scopeMap.get("columnName");
            summaryScopeID = this.createSummaryScope(techID, SummaryScopeConstants.TECHNICIAN);
            if (summaryScopeID != -1L) {
                final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
                for (final Long valueID : scopeList) {
                    final Row row = new Row(tableName);
                    row.set("SUMMARY_SCOPE_ID", (Object)summaryScopeID);
                    row.set(columnName, (Object)valueID);
                    dataObject.addRow(row);
                }
                SyMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception:: Unable to create summary scope for technicianIDL " + techID + ". Stacktrace::", ex);
        }
        return summaryScopeID;
    }
    
    public Long createSummaryScope(final Long valueID, final Integer scopeType) {
        Long summaryScopeID = -1L;
        try {
            final HashMap<String, String> scopeMap = this.getScopeTableValues(scopeType);
            final String tableName = scopeMap.get("tableName");
            final String columnName = scopeMap.get("columnName");
            summaryScopeID = this.createSummaryScopeDefn(scopeType);
            if (summaryScopeID != -1L) {
                final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
                final Row row = new Row(tableName);
                row.set("SUMMARY_SCOPE_ID", (Object)summaryScopeID);
                row.set(columnName, (Object)valueID);
                if (tableName.equals("CustomerScopeRel")) {
                    row.set("DB_UPDATED_TIME", (Object)System.currentTimeMillis());
                }
                dataObject.addRow(row);
                SyMUtil.getPersistence().update(dataObject);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception:: Unable to create summary scope for scopeType:" + scopeType + ". Stacktrace::", ex);
        }
        return summaryScopeID;
    }
    
    private Long createSummaryScopeDefn(final Integer scopeType) {
        Long summaryScopeID = -1L;
        try {
            final DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
            final Row row = new Row("SummaryScopeDefn");
            row.set("SCOPE_TYPE", (Object)scopeType);
            dataObject.addRow(row);
            SyMUtil.getPersistence().update(dataObject);
            summaryScopeID = (Long)dataObject.getRow("SummaryScopeDefn").get("SUMMARY_SCOPE_ID");
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception:: Unable to create summary scope defn for scopeType:" + scopeType + ". Stacktrace::", ex);
        }
        return summaryScopeID;
    }
    
    public Long getSimilarScopeForTechnician(final List<Long> valueIDs, final Integer scopeType) {
        return this.getSimilarScopeForTechnician(valueIDs, scopeType, -1L);
    }
    
    private Long getSimilarScopeForTechnician(final List<Long> valueIDs, final Integer scopeType, final Long excludeScopeID) {
        Long summaryScopeID = -1L;
        DataSet ds = null;
        Connection conn = null;
        try {
            final HashMap<String, String> scopeMap = this.getScopeTableValues(scopeType);
            final String tableName = scopeMap.get("tableName");
            final String columnName = scopeMap.get("columnName");
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
            Criteria criteria = new Criteria(Column.getColumn(tableName, columnName), (Object)valueIDs.toArray(), 8);
            if (excludeScopeID.equals(-1L)) {
                criteria = criteria.and(new Criteria(Column.getColumn("SummaryScopeDefn", "SCOPE_TYPE"), (Object)SummaryScopeConstants.TECHNICIAN, 0));
            }
            final Join join = new Join(tableName, "SummaryScopeDefn", new String[] { "SUMMARY_SCOPE_ID" }, new String[] { "SUMMARY_SCOPE_ID" }, 2);
            query.addJoin(join);
            query.setCriteria(criteria);
            final Column scopeCol = Column.getColumn("SummaryScopeDefn", "SUMMARY_SCOPE_ID");
            final Column valueCol = Column.getColumn(tableName, columnName).count();
            query.addSelectColumn(scopeCol);
            query.addSelectColumn(valueCol);
            final List groupColList = new ArrayList();
            groupColList.add(scopeCol);
            final GroupByClause groupByClause = new GroupByClause(groupColList);
            query.setGroupByClause(groupByClause);
            query.setCriteria(criteria);
            conn = RelationalAPI.getInstance().getConnection();
            ds = RelationalAPI.getInstance().executeQuery((Query)query, conn);
            final String sql = RelationalAPI.getInstance().getSelectSQL((Query)query);
            if (ds != null) {
                boolean flag = false;
                while (ds.next() && !flag) {
                    final Long id = (Long)ds.getValue(1);
                    final Integer count = (Integer)ds.getValue(2);
                    if (count.equals(valueIDs.size())) {
                        final List<Long> scopeRes = getInstance().getSummaryScopeValueIDs(id, scopeType);
                        Collections.sort(scopeRes);
                        Collections.sort(valueIDs);
                        if (!scopeRes.equals(valueIDs) || id.equals(excludeScopeID)) {
                            continue;
                        }
                        summaryScopeID = id;
                        flag = true;
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception:: while fetching the similar technician scope for scopeType:" + scopeType + ". Stacktrace::", ex);
            try {
                if (ds != null) {
                    ds.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Exception is occurred at LiveConnectAction. Exception:{0}", ex);
            }
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex2) {
                this.logger.log(Level.WARNING, "Exception is occurred at LiveConnectAction. Exception:{0}", ex2);
            }
        }
        return summaryScopeID;
    }
    
    public List<Long> getAllSummaryScopeIDs(final Long valueID, final Integer scopeType) {
        final List<Long> summaryScopeIDs = new ArrayList<Long>();
        try {
            final HashMap<String, String> scopeMap = this.getScopeTableValues(scopeType);
            final String tableName = scopeMap.get("tableName");
            final String columnName = scopeMap.get("columnName");
            final Criteria criteria = new Criteria(Column.getColumn(tableName, columnName), (Object)valueID, 0);
            final DataObject dataObject = DataAccess.get(tableName, criteria);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows(tableName);
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long summaryScopeID = (Long)row.get("SUMMARY_SCOPE_ID");
                    summaryScopeIDs.add(summaryScopeID);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception:: While fetching the summary scope ID of valueID:" + valueID + ", scopeType:" + scopeType + ". Stacktrace::", ex);
        }
        return summaryScopeIDs;
    }
    
    public Long getMatchedSummaryScopeID(final Long valueID, final Integer scopeType) {
        Long summaryScopeID = -1L;
        try {
            final HashMap<String, String> scopeMap = this.getScopeTableValues(scopeType);
            final String tableName = scopeMap.get("tableName");
            final String columnName = scopeMap.get("columnName");
            final Criteria criteria = new Criteria(Column.getColumn(tableName, columnName), (Object)valueID, 0);
            final DataObject dataObject = DataAccess.get(tableName, criteria);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow(tableName);
                summaryScopeID = (Long)row.get("SUMMARY_SCOPE_ID");
            }
            return summaryScopeID;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception:: While fetching the summary scope ID of valueID:" + valueID + ", scopeType:" + scopeType + ". Stacktrace::", ex);
            return summaryScopeID;
        }
    }
    
    public void addSummaryScopeListener(final SummaryScopeListener listener) {
        SummaryScopeUtil.summaryScopeListeners.add(listener);
    }
    
    public static void removeSummaryScopeListener(final SummaryScopeListener listener) {
        SummaryScopeUtil.summaryScopeListeners.remove(listener);
    }
    
    public void invokeSummaryScopeListeners(final SummaryScopeEvent summaryScopeEvent, final int operation) {
        final int listenerCount = SummaryScopeUtil.summaryScopeListeners.size();
        if (operation == SummaryScopeConstants.EVENT_SCOPE_ADDED) {
            for (int i = 0; i < listenerCount; ++i) {
                final SummaryScopeListener listener = SummaryScopeUtil.summaryScopeListeners.get(i);
                listener.scopeAdded(summaryScopeEvent);
            }
        }
        else if (operation == SummaryScopeConstants.EVENT_SCOPE_MODIFIED) {
            for (int i = 0; i < listenerCount; ++i) {
                final SummaryScopeListener listener = SummaryScopeUtil.summaryScopeListeners.get(i);
                listener.scopeModified(summaryScopeEvent);
            }
        }
        else if (operation == SummaryScopeConstants.EVENT_SCOPE_DELETED) {
            for (int i = 0; i < listenerCount; ++i) {
                final SummaryScopeListener listener = SummaryScopeUtil.summaryScopeListeners.get(i);
                listener.scopeDeleted(summaryScopeEvent);
            }
        }
        else if (operation == SummaryScopeConstants.EVENT_INVOKE_AMC_SUMMARY) {
            for (int i = 0; i < listenerCount; ++i) {
                final SummaryScopeListener listener = SummaryScopeUtil.summaryScopeListeners.get(i);
                listener.invokeSummaryForAllManaged(summaryScopeEvent);
            }
        }
    }
    
    public Integer getSummaryScopeType(final Long scopeID) {
        Integer scopeType = -1;
        try {
            final Criteria criteria = new Criteria(Column.getColumn("SummaryScopeDefn", "SUMMARY_SCOPE_ID"), (Object)scopeID, 0);
            final DataObject dataObject = DataAccess.get("SummaryScopeDefn", criteria);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getFirstRow("SummaryScopeDefn");
                scopeType = (Integer)row.get("SCOPE_TYPE");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while trying to fetch the summary scope type for summaryscopeID:" + scopeID + "Exception::", ex);
        }
        return scopeType;
    }
    
    public List<Long> getSummaryScopeValueIDs(final Long summaryScopeID, final Integer scopeType) {
        final List<Long> valueIDs = new ArrayList<Long>();
        try {
            final HashMap<String, String> scopeMap = this.getScopeTableValues(scopeType);
            final String tableName = scopeMap.get("tableName");
            final String columnName = scopeMap.get("columnName");
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("SummaryScopeDefn"));
            final Criteria criteria = new Criteria(Column.getColumn(tableName, "SUMMARY_SCOPE_ID"), (Object)summaryScopeID, 0);
            query.addJoin(new Join("SummaryScopeDefn", tableName, new String[] { "SUMMARY_SCOPE_ID" }, new String[] { "SUMMARY_SCOPE_ID" }, 2));
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn(tableName, "*"));
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows(tableName);
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long valueID = (Long)row.get(columnName);
                    valueIDs.add(valueID);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception:: While fetching the summary scope ID of valueIDs:" + Arrays.toString(valueIDs.toArray()) + ", scopeType:" + scopeType + ". Stacktrace::", ex);
        }
        return valueIDs;
    }
    
    public List<Long> getAllSummaryScopeIDs(final Long[] valueIDs, final Integer scopeType) {
        final List<Long> summaryScopeIDs = new ArrayList<Long>();
        try {
            final HashMap<String, String> scopeMap = this.getScopeTableValues(scopeType);
            final String tableName = scopeMap.get("tableName");
            final String columnName = scopeMap.get("columnName");
            final Criteria criteria = new Criteria(Column.getColumn(tableName, columnName), (Object)valueIDs, 8);
            final DataObject dataObject = DataAccess.get(tableName, criteria);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows(tableName);
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Long summaryScopeID = (Long)row.get("SUMMARY_SCOPE_ID");
                    summaryScopeIDs.add(summaryScopeID);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception:: While fetching the summary scope ID of valueIDs:" + valueIDs + ", scopeType:" + scopeType + ". Stacktrace::", ex);
        }
        return summaryScopeIDs;
    }
    
    public void fetchandCreateSummaryScopeMapping(final Long[] valueIDs, final Integer scopeType, final Long targetValueID, final Integer targetScopeType) {
        try {
            final HashMap<String, String> scopeMap = this.getScopeTableValues(scopeType);
            final String tableName = scopeMap.get("tableName");
            final String columnName = scopeMap.get("columnName");
            final HashMap<String, String> targetScopeMap = this.getScopeTableValues(targetScopeType);
            final String targetTableName = targetScopeMap.get("tableName");
            final String targetColumnName = targetScopeMap.get("columnName");
            final DataObject targetDataObject = (DataObject)new WritableDataObject();
            final Criteria criteria = new Criteria(Column.getColumn(tableName, columnName), (Object)valueIDs, 8);
            final DataObject dataObject = DataAccess.get(tableName, criteria);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows(tableName);
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Row targetRow = new Row(targetTableName);
                    targetRow.set("SUMMARY_SCOPE_ID", row.get("SUMMARY_SCOPE_ID"));
                    targetRow.set(targetColumnName, (Object)targetValueID);
                    if (targetTableName.equals("CustomerScopeRel")) {
                        targetRow.set("DB_UPDATED_TIME", (Object)System.currentTimeMillis());
                    }
                    targetDataObject.addRow(targetRow);
                }
                SyMUtil.getPersistence().add(targetDataObject);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception:: While fetchandCreateSummaryScopeMapping the summary scope ID of valueIDs:" + valueIDs + ", scopeType:" + scopeType + ". Stacktrace::", ex);
        }
    }
    
    public List<Long> getAllScopeIdsByScopeType(final Integer scopeType) {
        final List<Long> scopeList = new ArrayList<Long>();
        final String methodName = "getAllScopeIdsByScopeType";
        try {
            final HashMap<String, String> scopeMap = this.getScopeTableValues(scopeType);
            final String tableName = scopeMap.get("tableName");
            final String columnName = scopeMap.get("columnName");
            Criteria joinCriteria = new Criteria(Column.getColumn("SummaryScopeDefn", "SUMMARY_SCOPE_ID"), (Object)Column.getColumn(tableName, columnName), 0);
            joinCriteria = joinCriteria.and(new Criteria(Column.getColumn("SummaryScopeDefn", "SCOPE_TYPE"), (Object)scopeType, 0));
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SummaryScopeDefn"));
            selectQuery.addJoin(new Join("SummaryScopeDefn", tableName, joinCriteria, 2));
            selectQuery.addSelectColumn(Column.getColumn("SummaryScopeDefn", "SCOPE_TYPE"));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Iterator<Row> summaryScopeRowIterator = dataObject.getRows("SummaryScopeDefn");
                while (summaryScopeRowIterator.hasNext()) {
                    final Row summaryScopeRow = summaryScopeRowIterator.next();
                    scopeList.add((Long)summaryScopeRow.get("SUMMARY_SCOPE_ID"));
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception occurred in Class :: " + SummaryScopeUtil.sourceClass + ".Method :: " + "getAllScopeIdsByScopeType" + ".  Exception is " + exp.getMessage());
        }
        return scopeList;
    }
    
    static {
        SummaryScopeUtil.sourceClass = SummaryScopeUtil.class.getName();
        SummaryScopeUtil.summaryScopeUtil = null;
        SummaryScopeUtil.summaryScopeListeners = new ArrayList<SummaryScopeListener>();
    }
}
