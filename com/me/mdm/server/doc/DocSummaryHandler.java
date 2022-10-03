package com.me.mdm.server.doc;

import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;
import com.me.idps.core.util.DirectoryQueryutil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.adventnet.sym.server.mdm.config.ResourceSummaryHandler;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import org.json.simple.JSONArray;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import org.json.simple.JSONObject;
import com.adventnet.db.api.RelationalAPI;
import java.util.Iterator;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQuery;
import com.me.idps.core.factory.TransactionExecutionImpl;
import java.util.Properties;
import java.util.HashMap;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.sql.Connection;
import com.me.idps.core.factory.TransactionExecutionInterface;

public class DocSummaryHandler implements TransactionExecutionInterface
{
    private static final String QUERY = "QUERY";
    private static final String INSERT_TABLE = "INSERT_TABLE";
    private static final String SELECTION = "SELECTION";
    private static final String QUERY_TYPE = "QUERY_TYPE";
    private static final String CONNECTION = "CONNECTION";
    private static final String UPDATE_QUERY = "UPDATE_QUERY";
    private static final String INSERT_QUERY = "INSERT_QUERY";
    private static DocSummaryHandler docSummaryHandler;
    
    public static DocSummaryHandler getInstance() {
        if (DocSummaryHandler.docSummaryHandler == null) {
            DocSummaryHandler.docSummaryHandler = new DocSummaryHandler();
        }
        return DocSummaryHandler.docSummaryHandler;
    }
    
    private void insertIntoDocSummary(final Connection connection) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentDetails"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DocumentSummary", "DOC_ID"), (Object)null, 0));
        selectQuery.addJoin(new Join("DocumentDetails", "DocumentSummary", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1));
        final HashMap<String, Column> colMap = new HashMap<String, Column>();
        colMap.put("DOC_ID", Column.getColumn("DocumentDetails", "DOC_ID"));
        final Properties properties = new Properties();
        ((Hashtable<String, HashMap<String, Column>>)properties).put("SELECTION", colMap);
        ((Hashtable<String, SelectQuery>)properties).put("QUERY", selectQuery);
        ((Hashtable<String, Connection>)properties).put("CONNECTION", connection);
        ((Hashtable<String, String>)properties).put("INSERT_TABLE", "DocumentSummary");
        ((Hashtable<String, String>)properties).put("QUERY_TYPE", "INSERT_QUERY");
        TransactionExecutionImpl.getInstance().performTaskInTransactionMode("com.me.mdm.server.doc.DocSummaryHandler", properties);
    }
    
    private void updateDocSummary(final Connection connection) throws Exception {
        final HashMap<String, Column> docSummaryColMap = new HashMap<String, Column>();
        final Column tagDocIDcol = Column.getColumn("DocumentSummary", "DOC_ID", "inner_TAG_DOC.DOC_ID");
        final Column tagCountCol = MDMUtil.getInstance().getDistinctIntegerCountCaseExpressionColumn(Column.getColumn("DocumentTagRel", "TAG_ID", "DOC_TAG_COUNT"));
        final SelectQuery docTagSubQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentSummary"));
        docTagSubQuery.addJoin(new Join("DocumentSummary", "DocumentTagRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1));
        docTagSubQuery.addSelectColumns((List)new ArrayList(Arrays.asList(tagDocIDcol, tagCountCol)));
        docTagSubQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(tagDocIDcol))));
        final DerivedTable docTagDt = new DerivedTable("innerTagDocDT", (Query)docTagSubQuery);
        docSummaryColMap.put("DocumentTagRel", new Column(docTagDt.getTableAlias(), tagDocIDcol.getColumnAlias()));
        docSummaryColMap.put("TAG_COUNT", new Column(docTagDt.getTableAlias(), tagCountCol.getColumnAlias(), tagCountCol.getColumnAlias()));
        final Column userDocIDcol = Column.getColumn("DocumentSummary", "DOC_ID", "inner_USR_DOC.DOC_ID");
        final Column userCountCol = MDMUtil.getInstance().getDistinctIntegerCountCaseExpressionColumn(Column.getColumn("DocumentToMDMResource", "RESOURCE_ID", "DOC_USR_COUNT"));
        final SelectQuery docUserSubQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentSummary"));
        docUserSubQuery.addJoin(new Join("DocumentSummary", "DocumentToMDMResource", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1));
        docUserSubQuery.addSelectColumns((List)new ArrayList(Arrays.asList(userDocIDcol, userCountCol)));
        docUserSubQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(userDocIDcol))));
        final DerivedTable docUserDt = new DerivedTable("innerUserDocDT", (Query)docUserSubQuery);
        docSummaryColMap.put("DocumentToMDMResource", new Column(docUserDt.getTableAlias(), userDocIDcol.getColumnAlias()));
        docSummaryColMap.put("USER_COUNT", new Column(docUserDt.getTableAlias(), userCountCol.getColumnAlias(), userCountCol.getColumnAlias()));
        final Column groupDocIDcol = Column.getColumn("DocumentSummary", "DOC_ID", "inner_GRP_DOC.DOC_ID");
        final Column groupCountCol = MDMUtil.getInstance().getDistinctIntegerCountCaseExpressionColumn(Column.getColumn("DocumentToDeviceGroup", "CUSTOMGROUP_ID", "DOC_GRP_COUNT"));
        final SelectQuery docGroupSubQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentSummary"));
        docGroupSubQuery.addJoin(new Join("DocumentSummary", "DocumentToDeviceGroup", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1));
        docGroupSubQuery.addSelectColumns((List)new ArrayList(Arrays.asList(groupDocIDcol, groupCountCol)));
        docGroupSubQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(groupDocIDcol))));
        final DerivedTable docGroupDt = new DerivedTable("innerGroupDocDT", (Query)docGroupSubQuery);
        docSummaryColMap.put("DocumentToDeviceGroup", new Column(docGroupDt.getTableAlias(), groupDocIDcol.getColumnAlias()));
        docSummaryColMap.put("GROUP_COUNT", new Column(docGroupDt.getTableAlias(), groupCountCol.getColumnAlias(), groupCountCol.getColumnAlias()));
        final Criteria deviceCri = new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)Boolean.TRUE, 0);
        final CaseExpression docDeviceCountExp = new CaseExpression("DOC_DVC_COUNT");
        docDeviceCountExp.addWhen(deviceCri, (Object)Column.getColumn("DocumentManagedDeviceInfo", "DOC_MD_ID"));
        final Column deviceCountCol = MDMUtil.getInstance().getDistinctIntegerCountCaseExpressionColumn((Column)docDeviceCountExp);
        final Column deviceDocIDcol = Column.getColumn("DocumentSummary", "DOC_ID", "inner_DVC_DOC.DOC_ID");
        final SelectQuery docDeviceSubQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentSummary"));
        docDeviceSubQuery.addJoin(new Join("DocumentSummary", "DocumentManagedDeviceRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1));
        docDeviceSubQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 1));
        docDeviceSubQuery.addSelectColumns((List)new ArrayList(Arrays.asList(deviceDocIDcol, deviceCountCol)));
        docDeviceSubQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(deviceDocIDcol))));
        final DerivedTable docDeviceDt = new DerivedTable("innerDeviceDocDT", (Query)docDeviceSubQuery);
        docSummaryColMap.put("DocumentManagedDeviceRel", new Column(docDeviceDt.getTableAlias(), deviceDocIDcol.getColumnAlias()));
        docSummaryColMap.put("DEVICE_COUNT", new Column(docDeviceDt.getTableAlias(), deviceCountCol.getColumnAlias(), deviceCountCol.getColumnAlias()));
        final Table docSummaryTable = Table.getTable("DocumentSummary");
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DocumentSummary");
        updateQuery.addJoin(new Join(docSummaryTable, (Table)docTagDt, new String[] { "DOC_ID" }, new String[] { docSummaryColMap.get("DocumentTagRel").getColumnName() }, 2));
        updateQuery.addJoin(new Join(docSummaryTable, (Table)docUserDt, new String[] { "DOC_ID" }, new String[] { docSummaryColMap.get("DocumentToMDMResource").getColumnName() }, 2));
        updateQuery.addJoin(new Join(docSummaryTable, (Table)docGroupDt, new String[] { "DOC_ID" }, new String[] { docSummaryColMap.get("DocumentToDeviceGroup").getColumnName() }, 2));
        updateQuery.addJoin(new Join(docSummaryTable, (Table)docDeviceDt, new String[] { "DOC_ID" }, new String[] { docSummaryColMap.get("DocumentManagedDeviceRel").getColumnName() }, 2));
        updateQuery.setUpdateColumn("TAG_COUNT", (Object)new Column(docTagDt.getTableAlias(), docSummaryColMap.get("TAG_COUNT").getColumnAlias()));
        updateQuery.setUpdateColumn("USER_COUNT", (Object)new Column(docUserDt.getTableAlias(), docSummaryColMap.get("USER_COUNT").getColumnAlias()));
        updateQuery.setUpdateColumn("GROUP_COUNT", (Object)new Column(docGroupDt.getTableAlias(), docSummaryColMap.get("GROUP_COUNT").getColumnAlias()));
        updateQuery.setUpdateColumn("DEVICE_COUNT", (Object)new Column(docDeviceDt.getTableAlias(), docSummaryColMap.get("DEVICE_COUNT").getColumnAlias()));
        final Properties properties = new Properties();
        ((Hashtable<String, UpdateQuery>)properties).put("QUERY", updateQuery);
        ((Hashtable<String, Connection>)properties).put("CONNECTION", connection);
        ((Hashtable<String, String>)properties).put("QUERY_TYPE", "UPDATE_QUERY");
        TransactionExecutionImpl.getInstance().performTaskInTransactionMode("com.me.mdm.server.doc.DocSummaryHandler", properties);
    }
    
    private void upsertDocSummary(final Connection connection) throws Exception {
        DocMgmt.logger.log(Level.INFO, "updating doc summary");
        final String cacheKey = "DocumentSummaryCACHE_FLAG";
        final boolean isResSummaryQueryAlreadyInExec = Boolean.valueOf(String.valueOf(ApiFactoryProvider.getCacheAccessAPI().getCache(cacheKey, 2)));
        if (!isResSummaryQueryAlreadyInExec) {
            ApiFactoryProvider.getCacheAccessAPI().putCache(cacheKey, (Object)true, 2, 240);
            try {
                this.insertIntoDocSummary(connection);
                this.updateDocSummary(connection);
            }
            catch (final Exception e) {
                throw e;
            }
            finally {
                ApiFactoryProvider.getCacheAccessAPI().removeCache(cacheKey, 2);
            }
        }
        else {
            DocMgmt.logger.log(Level.INFO, "skipped because doc summary updation already in exec");
        }
        DocMgmt.logger.log(Level.INFO, "updated doc summary");
    }
    
    private void insertIntoDocPolicySummary(final Connection connection) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CMDeploymentPolicy"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("CMDeploymentPolicySummary", "DEPLOYMENT_POLICY_ID"), (Object)null, 0));
        selectQuery.addJoin(new Join("CMDeploymentPolicy", "CMDeploymentPolicySummary", new String[] { "DEPLOYMENT_POLICY_ID" }, new String[] { "DEPLOYMENT_POLICY_ID" }, 1));
        final HashMap<String, Column> colMap = new HashMap<String, Column>();
        colMap.put("DEPLOYMENT_POLICY_ID", Column.getColumn("CMDeploymentPolicy", "DEPLOYMENT_POLICY_ID"));
        final Properties properties = new Properties();
        ((Hashtable<String, HashMap<String, Column>>)properties).put("SELECTION", colMap);
        ((Hashtable<String, SelectQuery>)properties).put("QUERY", selectQuery);
        ((Hashtable<String, String>)properties).put("INSERT_TABLE", "CMDeploymentPolicySummary");
        ((Hashtable<String, Connection>)properties).put("CONNECTION", connection);
        ((Hashtable<String, String>)properties).put("QUERY_TYPE", "INSERT_QUERY");
        TransactionExecutionImpl.getInstance().performTaskInTransactionMode("com.me.mdm.server.doc.DocSummaryHandler", properties);
    }
    
    private void updateDocPolicySummary(final Connection connection) throws Exception {
        final HashMap<String, Column> docSummaryColMap = new HashMap<String, Column>();
        final Criteria docCri = new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1);
        final CaseExpression policyDocCountExp = new CaseExpression("POLICY_DOC_COUNT");
        policyDocCountExp.addWhen(docCri, (Object)Column.getColumn("DocumentDetails", "DOC_ID"));
        final Column docCountCol = MDMUtil.getInstance().getDistinctIntegerCountCaseExpressionColumn((Column)policyDocCountExp);
        final Column docPolicyIDcol = Column.getColumn("CMDeploymentPolicySummary", "DEPLOYMENT_POLICY_ID", "inner_POLICY_DOC.POLICY_ID");
        final SelectQuery policyDocSubQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CMDeploymentPolicySummary"));
        policyDocSubQuery.addJoin(new Join("CMDeploymentPolicySummary", "DocumentPolicyResourceRel", new String[] { "DEPLOYMENT_POLICY_ID" }, new String[] { "DEPLOYMENT_POLICY_ID" }, 1));
        policyDocSubQuery.addJoin(new Join("DocumentPolicyResourceRel", "DocumentDetails", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 1));
        policyDocSubQuery.addSelectColumns((List)new ArrayList(Arrays.asList(docPolicyIDcol, docCountCol)));
        policyDocSubQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(docPolicyIDcol))));
        final DerivedTable policyDocDt = new DerivedTable("innerPolicyDocDT", (Query)policyDocSubQuery);
        docSummaryColMap.put("DocumentDetails", new Column(policyDocDt.getTableAlias(), docPolicyIDcol.getColumnAlias()));
        docSummaryColMap.put("DOCS_COUNT", new Column(policyDocDt.getTableAlias(), docCountCol.getColumnAlias(), docCountCol.getColumnAlias()));
        final Table docPolicySummaryTable = Table.getTable("CMDeploymentPolicySummary");
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("CMDeploymentPolicySummary");
        updateQuery.addJoin(new Join(docPolicySummaryTable, (Table)policyDocDt, new String[] { "DEPLOYMENT_POLICY_ID" }, new String[] { docSummaryColMap.get("DocumentDetails").getColumnName() }, 2));
        updateQuery.setUpdateColumn("DOCS_COUNT", (Object)new Column(policyDocDt.getTableAlias(), docSummaryColMap.get("DOCS_COUNT").getColumnAlias()));
        final Properties properties = new Properties();
        ((Hashtable<String, UpdateQuery>)properties).put("QUERY", updateQuery);
        ((Hashtable<String, Connection>)properties).put("CONNECTION", connection);
        ((Hashtable<String, String>)properties).put("QUERY_TYPE", "UPDATE_QUERY");
        TransactionExecutionImpl.getInstance().performTaskInTransactionMode("com.me.mdm.server.doc.DocSummaryHandler", properties);
    }
    
    private void upsertDocPolicySummary(final Connection connection) throws Exception {
        DocMgmt.logger.log(Level.INFO, "updating doc policy summary");
        final String cacheKey = "CMDeploymentPolicySummaryCACHE_FLAG";
        final boolean isResSummaryQueryAlreadyInExec = Boolean.valueOf(String.valueOf(ApiFactoryProvider.getCacheAccessAPI().getCache(cacheKey, 2)));
        if (!isResSummaryQueryAlreadyInExec) {
            ApiFactoryProvider.getCacheAccessAPI().putCache(cacheKey, (Object)true, 2, 240);
            try {
                this.insertIntoDocPolicySummary(connection);
                this.updateDocPolicySummary(connection);
            }
            catch (final Exception e) {
                throw e;
            }
            finally {
                ApiFactoryProvider.getCacheAccessAPI().removeCache(cacheKey, 2);
            }
        }
        else {
            DocMgmt.logger.log(Level.INFO, "skipped because doc summary updation already in exec");
        }
        DocMgmt.logger.log(Level.INFO, "updated doc policy summary");
    }
    
    public void reviseDocSummary(final List<Long> docList) {
        try {
            this.addDocStatusTaskToQueue(docList, null);
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private Column getDistinctCountCEcol(final String ceAlias, final String tableAlias, final String statusColAlias, final String resIDcolAlias) {
        final boolean success = !ceAlias.contains("NotSucc");
        final CaseExpression ce = new CaseExpression(ceAlias);
        ce.addWhen(new Criteria(new Column(tableAlias, statusColAlias), (Object)6, (int)(success ? 0 : 1)), (Object)new Column(tableAlias, resIDcolAlias));
        final Column countCol = (Column)Column.createFunction("COUNT", new Object[] { Column.createFunction("DISTINCT", new Object[] { ce }) });
        countCol.setType(4);
        countCol.setColumnAlias(ceAlias);
        return countCol;
    }
    
    private void updateGroupSummary(final Connection connection, final Long[] docGroups) throws Exception {
        for (final Long cgID : docGroups) {
            if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("FlatUserGroupDistribution")) {
                final List<Long> groupMembers = new ArrayList<Long>();
                final List<Properties> groupMemberProps = DocMgmtDataHandler.getInstance().getGroupMembers(cgID, 101);
                if (groupMemberProps != null && !groupMemberProps.isEmpty()) {
                    for (final Properties memberProps : groupMemberProps) {
                        groupMembers.add(((Hashtable<K, Long>)memberProps).get("MEMBER_RESOURCE_ID"));
                    }
                }
                if (!groupMembers.isEmpty()) {
                    this.updateGroupSummary(connection, groupMembers.toArray(new Long[groupMembers.size()]));
                }
            }
            this.updateGroupSummary(connection, cgID);
        }
    }
    
    private Properties getStatusSubQueries(final int resType, final Join cgMemberJoin, final Criteria cgBaseCri) {
        switch (resType) {
            case 2: {
                final Column memberUserDocIDCol = Column.getColumn("DocumentToMDMResource", "DOC_ID", "memberUserDocIDCol");
                final Column memberUserSucceededCount = this.getDistinctCountCEcol("memberUserSucceededCount", "DocumentToMDMResource", "STATUS_ID", "RESOURCE_ID");
                final Column memberUserNotSucceededCount = this.getDistinctCountCEcol("memberUserNotSucceededCount", "DocumentToMDMResource", "STATUS_ID", "RESOURCE_ID");
                final SelectQuery memberUserQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
                memberUserQuery.addJoin(cgMemberJoin);
                memberUserQuery.addJoin(new Join("CustomGroupMemberRel", "DocumentToMDMResource", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                memberUserQuery.setCriteria(cgBaseCri);
                memberUserQuery.addSelectColumn(memberUserDocIDCol);
                memberUserQuery.addSelectColumn(memberUserSucceededCount);
                memberUserQuery.addSelectColumn(memberUserNotSucceededCount);
                memberUserQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(memberUserDocIDCol))));
                final DerivedTable memberUserDT = new DerivedTable("memberUserDT", (Query)memberUserQuery);
                final Properties userProps = new Properties();
                ((Hashtable<String, DerivedTable>)userProps).put("DT", memberUserDT);
                ((Hashtable<String, Column>)userProps).put("docIDcol", new Column(memberUserDT.getTableAlias(), memberUserDocIDCol.getColumnAlias(), memberUserDocIDCol.getColumnAlias()));
                ((Hashtable<String, Column>)userProps).put("SucceededCount", new Column(memberUserDT.getTableAlias(), memberUserSucceededCount.getColumnAlias(), memberUserSucceededCount.getColumnAlias()));
                ((Hashtable<String, Column>)userProps).put("NotSucceededCount", new Column(memberUserDT.getTableAlias(), memberUserNotSucceededCount.getColumnAlias(), memberUserNotSucceededCount.getColumnAlias()));
                return userProps;
            }
            case 101: {
                final Column memberGroupDocIDCol = Column.getColumn("DocumentToDeviceGroup", "DOC_ID", "memberGroupDocIDCol");
                final Column memberGroupSucceededCount = this.getDistinctCountCEcol("memberGroupSucceededCount", "DocumentToDeviceGroup", "STATUS_ID", "CUSTOMGROUP_ID");
                final Column memberGroupNotSucceededCount = this.getDistinctCountCEcol("memberGroupNotSucceededCount", "DocumentToDeviceGroup", "STATUS_ID", "CUSTOMGROUP_ID");
                final SelectQuery memberGroupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
                memberGroupQuery.addJoin(cgMemberJoin);
                memberGroupQuery.addJoin(new Join("CustomGroupMemberRel", "DocumentToDeviceGroup", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "CUSTOMGROUP_ID" }, 1));
                memberGroupQuery.setCriteria(cgBaseCri);
                memberGroupQuery.addSelectColumn(memberGroupDocIDCol);
                memberGroupQuery.addSelectColumn(memberGroupSucceededCount);
                memberGroupQuery.addSelectColumn(memberGroupNotSucceededCount);
                memberGroupQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(memberGroupDocIDCol))));
                final DerivedTable memberGroupDT = new DerivedTable("memberGroupDT", (Query)memberGroupQuery);
                final Properties groupProps = new Properties();
                ((Hashtable<String, DerivedTable>)groupProps).put("DT", memberGroupDT);
                ((Hashtable<String, Column>)groupProps).put("docIDcol", new Column(memberGroupDT.getTableAlias(), memberGroupDocIDCol.getColumnAlias(), memberGroupDocIDCol.getColumnAlias()));
                ((Hashtable<String, Column>)groupProps).put("SucceededCount", new Column(memberGroupDT.getTableAlias(), memberGroupSucceededCount.getColumnAlias(), memberGroupSucceededCount.getColumnAlias()));
                ((Hashtable<String, Column>)groupProps).put("NotSucceededCount", new Column(memberGroupDT.getTableAlias(), memberGroupNotSucceededCount.getColumnAlias(), memberGroupNotSucceededCount.getColumnAlias()));
                return groupProps;
            }
            case 120: {
                final Column memberDeviceDocIDCol = Column.getColumn("DocumentManagedDeviceRel", "DOC_ID", "memberDeviceDocIDCol");
                final Column memberDeviceSucceededCount = this.getDistinctCountCEcol("memberDeviceSucceededCount", "DocumentManagedDeviceInfo", "STATUS_ID", "DOC_MD_ID");
                final Column memberDeviceNotSucceededCount = this.getDistinctCountCEcol("memberDeviceNotSucceededCount", "DocumentManagedDeviceInfo", "STATUS_ID", "DOC_MD_ID");
                final SelectQuery memberDeviceQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
                memberDeviceQuery.addJoin(cgMemberJoin);
                memberDeviceQuery.addJoin(new Join("CustomGroupMemberRel", "DocumentManagedDeviceRel", new String[] { "MEMBER_RESOURCE_ID" }, new String[] { "MANAGEDDEVICE_ID" }, 1));
                memberDeviceQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 1));
                memberDeviceQuery.setCriteria(cgBaseCri);
                memberDeviceQuery.addSelectColumn(memberDeviceDocIDCol);
                memberDeviceQuery.addSelectColumn(memberDeviceSucceededCount);
                memberDeviceQuery.addSelectColumn(memberDeviceNotSucceededCount);
                memberDeviceQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(memberDeviceDocIDCol))));
                final DerivedTable memberDeviceDT = new DerivedTable("memberDeviceDT", (Query)memberDeviceQuery);
                final Properties deviceProps = new Properties();
                ((Hashtable<String, DerivedTable>)deviceProps).put("DT", memberDeviceDT);
                ((Hashtable<String, Column>)deviceProps).put("docIDcol", new Column(memberDeviceDT.getTableAlias(), memberDeviceDocIDCol.getColumnAlias(), memberDeviceDocIDCol.getColumnAlias()));
                ((Hashtable<String, Column>)deviceProps).put("SucceededCount", new Column(memberDeviceDT.getTableAlias(), memberDeviceSucceededCount.getColumnAlias(), memberDeviceSucceededCount.getColumnAlias()));
                ((Hashtable<String, Column>)deviceProps).put("NotSucceededCount", new Column(memberDeviceDT.getTableAlias(), memberDeviceNotSucceededCount.getColumnAlias(), memberDeviceNotSucceededCount.getColumnAlias()));
                return deviceProps;
            }
            default: {
                return null;
            }
        }
    }
    
    private void updateGroupSummary(final Connection connection, final Long cgID) throws Exception {
        final Criteria cgBaseCri = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)cgID, 0);
        final Join docCGjoin = new Join("CustomGroup", "DocumentToDeviceGroup", new String[] { "RESOURCE_ID" }, new String[] { "CUSTOMGROUP_ID" }, 2);
        final Join cgMemberJoin = new Join("CustomGroup", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "GROUP_RESOURCE_ID" }, 1);
        final Properties userProps = this.getStatusSubQueries(2, cgMemberJoin, cgBaseCri);
        final Properties cgProps = this.getStatusSubQueries(101, cgMemberJoin, cgBaseCri);
        final Properties deviceProps = this.getStatusSubQueries(120, cgMemberJoin, cgBaseCri);
        final DerivedTable memberUserDT = ((Hashtable<K, DerivedTable>)userProps).get("DT");
        final DerivedTable memberGroupDT = ((Hashtable<K, DerivedTable>)cgProps).get("DT");
        final DerivedTable memberDeviceDT = ((Hashtable<K, DerivedTable>)deviceProps).get("DT");
        final Column dtMemberGroupDocIDCol = ((Hashtable<K, Column>)cgProps).get("docIDcol");
        final Column dtMemberUserDocIDCol = ((Hashtable<K, Column>)userProps).get("docIDcol");
        final Column dtMemberDeviceDocIDCol = ((Hashtable<K, Column>)deviceProps).get("docIDcol");
        final Column memberGroupSucceededCount = ((Hashtable<K, Column>)cgProps).get("SucceededCount");
        final Column memberUserSucceededCount = ((Hashtable<K, Column>)userProps).get("SucceededCount");
        final Column memberDeviceSucceededCount = ((Hashtable<K, Column>)deviceProps).get("SucceededCount");
        final Column memberGroupNotSucceededCount = ((Hashtable<K, Column>)cgProps).get("NotSucceededCount");
        final Column memberUserNotSucceededCount = ((Hashtable<K, Column>)userProps).get("NotSucceededCount");
        final Column memberDeviceNotSucceededCount = ((Hashtable<K, Column>)deviceProps).get("NotSucceededCount");
        final CaseExpression ce = new CaseExpression("DOC_GROUP_STATUS");
        ce.addWhen(new Criteria(memberUserSucceededCount, (Object)null, 0).or(new Criteria(memberUserSucceededCount, (Object)0, 0)).and(new Criteria(memberDeviceSucceededCount, (Object)null, 0).or(new Criteria(memberDeviceSucceededCount, (Object)0, 0))).and(new Criteria(memberGroupSucceededCount, (Object)null, 0).or(new Criteria(memberGroupSucceededCount, (Object)0, 0))).and(new Criteria(memberUserNotSucceededCount, (Object)0, 5).or(new Criteria(memberDeviceNotSucceededCount, (Object)0, 5)).or(new Criteria(memberGroupNotSucceededCount, (Object)0, 5))), (Object)12);
        ce.addWhen(new Criteria(memberUserSucceededCount, (Object)0, 5).or(new Criteria(memberDeviceSucceededCount, (Object)0, 5)).or(new Criteria(memberGroupSucceededCount, (Object)0, 5)).and(new Criteria(memberUserNotSucceededCount, (Object)0, 5).or(new Criteria(memberDeviceNotSucceededCount, (Object)0, 5)).or(new Criteria(memberGroupNotSucceededCount, (Object)0, 5))), (Object)3);
        ce.addWhen(new Criteria(memberUserNotSucceededCount, (Object)null, 0).or(new Criteria(memberUserNotSucceededCount, (Object)0, 0)).and(new Criteria(memberDeviceNotSucceededCount, (Object)null, 0).or(new Criteria(memberDeviceNotSucceededCount, (Object)0, 0))).and(new Criteria(memberGroupNotSucceededCount, (Object)null, 0).or(new Criteria(memberGroupNotSucceededCount, (Object)0, 0))), (Object)6);
        final Table memberTable = Table.getTable("CustomGroupMemberRel");
        final Column cgDocIDcol = Column.getColumn("DocumentToDeviceGroup", "DOC_ID", "middleDTdOCUMENTTODEVICEGROUP.DOC_ID");
        final Column maxCeCol = (Column)Column.createFunction("MAX", new Object[] { ce });
        maxCeCol.setType(4);
        maxCeCol.setColumnAlias(ce.getColumnAlias());
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroup"));
        selectQuery.addJoin(docCGjoin);
        selectQuery.addJoin(cgMemberJoin);
        selectQuery.addJoin(new Join(memberTable, (Table)memberUserDT, new Criteria(cgDocIDcol, (Object)dtMemberUserDocIDCol, 0), 1));
        selectQuery.addJoin(new Join(memberTable, (Table)memberGroupDT, new Criteria(cgDocIDcol, (Object)dtMemberGroupDocIDCol, 0), 1));
        selectQuery.addJoin(new Join(memberTable, (Table)memberDeviceDT, new Criteria(cgDocIDcol, (Object)dtMemberDeviceDocIDCol, 0), 1));
        selectQuery.setCriteria(cgBaseCri);
        selectQuery.addSelectColumn(cgDocIDcol);
        selectQuery.addSelectColumn(maxCeCol);
        selectQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(cgDocIDcol))));
        final DerivedTable middleDT = new DerivedTable("middleDT", (Query)selectQuery);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DocumentToDeviceGroup");
        updateQuery.setCriteria(new Criteria(Column.getColumn("DocumentToDeviceGroup", "CUSTOMGROUP_ID"), (Object)cgID, 0));
        updateQuery.addJoin(new Join(Table.getTable("DocumentToDeviceGroup"), (Table)middleDT, new String[] { "DOC_ID" }, new String[] { cgDocIDcol.getColumnAlias() }, 2));
        updateQuery.setUpdateColumn("STATUS_ID", (Object)new Column(middleDT.getTableAlias(), maxCeCol.getColumnAlias()));
        String updateSqlStr = RelationalAPI.getInstance().getUpdateSQL(updateQuery);
        updateSqlStr = updateSqlStr.replace("memberUserDT.memberUserSucceededCount", "memberUserDT.\"memberUserSucceededCount\"");
        updateSqlStr = updateSqlStr.replace("memberGroupDT.memberGroupSucceededCount", "memberGroupDT.\"memberGroupSucceededCount\"");
        updateSqlStr = updateSqlStr.replace("memberDeviceDT.memberDeviceSucceededCount", "memberDeviceDT.\"memberDeviceSucceededCount\"");
        updateSqlStr = updateSqlStr.replace("memberUserDT.memberUserNotSucceededCount", "memberUserDT.\"memberUserNotSucceededCount\"");
        updateSqlStr = updateSqlStr.replace("memberGroupDT.memberGroupNotSucceededCount", "memberGroupDT.\"memberGroupNotSucceededCount\"");
        updateSqlStr = updateSqlStr.replace("memberDeviceDT.memberDeviceNotSucceededCount", "memberDeviceDT.\"memberDeviceNotSucceededCount\"");
        updateSqlStr = updateSqlStr.replace("middleDT.DOC_GROUP_STATUS", "middleDT.\"DOC_GROUP_STATUS\"");
        updateSqlStr = updateSqlStr.replace("memberUserDT.memberUserDocIDCol", "memberUserDT.\"memberUserDocIDCol\"");
        updateSqlStr = updateSqlStr.replace("memberGroupDT.memberGroupDocIDCol", "memberGroupDT.\"memberGroupDocIDCol\"");
        updateSqlStr = updateSqlStr.replace("memberDeviceDT.memberDeviceDocIDCol", "memberDeviceDT.\"memberDeviceDocIDCol\"");
        RelationalAPI.getInstance().execute(connection, updateSqlStr);
    }
    
    private void updateGroupDocSummary(final Connection connection, final List<Long> docList) throws Exception {
        final Column distGroupCol = Column.getColumn("DocumentToDeviceGroup", "CUSTOMGROUP_ID").distinct();
        distGroupCol.setColumnAlias("distGroupCol");
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentToDeviceGroup"));
        query.setCriteria(new Criteria(Column.getColumn("DocumentToDeviceGroup", "DOC_ID"), (Object)docList.toArray(new Long[docList.size()]), 8));
        query.addSelectColumn(distGroupCol);
        final JSONArray jsAr = MDMUtil.executeSelectQuery(connection, query);
        final List<Long> docGroupsLong = new ArrayList<Long>();
        for (int i = 0; jsAr != null && i < jsAr.size(); ++i) {
            final JSONObject jsObj = (JSONObject)jsAr.get(i);
            docGroupsLong.add(Long.valueOf(String.valueOf(jsObj.get((Object)distGroupCol.getColumnAlias()))));
        }
        if (!MDMGroupHandler.getInstance().isInCycle(docGroupsLong)) {
            this.updateGroupSummary(connection, docGroupsLong.toArray(new Long[docGroupsLong.size()]));
        }
    }
    
    private void updateUserSummary(final Connection connection, final List<Long> docList) throws QueryConstructionException, SQLException {
        final Column innerDocCol = new Column("DocumentToMDMResource", "DOC_ID", "inner_DOCUMENTTOMDMRESOURCE.DOC_ID");
        final Column innerResCol = new Column("DocumentToMDMResource", "RESOURCE_ID", "inner_DOCUMENTTOMDMRESOURCE.RESOURCE_ID");
        final SelectQuery innerQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentToMDMResource"));
        innerQuery.setCriteria(new Criteria(Column.getColumn("DocumentToMDMResource", "DOC_ID"), (Object)docList.toArray(new Long[docList.size()]), 8));
        innerQuery.addSelectColumn(innerResCol);
        innerQuery.addSelectColumn(innerDocCol);
        final DerivedTable innerTable = new DerivedTable("innerDT", (Query)innerQuery);
        final SelectQuery middleQuery = (SelectQuery)new SelectQueryImpl((Table)innerTable);
        middleQuery.addJoin(new Join((Table)innerTable, Table.getTable("DocumentDetails"), new String[] { innerDocCol.getColumnAlias() }, new String[] { "DOC_ID" }, 2));
        middleQuery.addJoin(new Join((Table)innerTable, Table.getTable("ManagedUserToDevice"), new String[] { innerResCol.getColumnAlias() }, new String[] { "MANAGED_USER_ID" }, 1));
        middleQuery.addJoin(new Join(Table.getTable("ManagedUserToDevice"), Table.getTable("DocumentManagedDeviceRel"), new Criteria(Column.getColumn("ManagedUserToDevice", "MANAGED_DEVICE_ID"), (Object)Column.getColumn("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"), 0).and(new Criteria(new Column(innerTable.getTableAlias(), innerDocCol.getColumnAlias()), (Object)Column.getColumn("DocumentManagedDeviceRel", "DOC_ID"), 0)), 1));
        middleQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 1));
        final Column docDeviceStatusCol = Column.getColumn("DocumentManagedDeviceInfo", "STATUS_ID");
        final CaseExpression notSuccededCE = new CaseExpression("notSucceededCount");
        notSuccededCE.addWhen(new Criteria(docDeviceStatusCol, (Object)null, 1).and(new Criteria(docDeviceStatusCol, (Object)6, 1)), (Object)Column.getColumn("DocumentManagedDeviceInfo", "DOC_MD_ID"));
        final Column notSuccededCountCol = (Column)Column.createFunction("COUNT", new Object[] { notSuccededCE });
        notSuccededCountCol.setType(4);
        notSuccededCountCol.setColumnAlias("notSuccededCountCol");
        final CaseExpression succededCE = new CaseExpression("succeededCount");
        succededCE.addWhen(new Criteria(docDeviceStatusCol, (Object)null, 0).or(new Criteria(docDeviceStatusCol, (Object)6, 0)), (Object)Column.getColumn("DocumentManagedDeviceInfo", "DOC_MD_ID"));
        final Column succededCountCol = (Column)Column.createFunction("COUNT", new Object[] { succededCE });
        succededCountCol.setType(4);
        succededCountCol.setColumnAlias("succededCountCol");
        final CaseExpression ce = new CaseExpression("USER_DOC_STATUS");
        ce.addWhen(new Criteria(succededCountCol, (Object)null, 0).or(new Criteria(succededCountCol, (Object)0, 0)).and(new Criteria(notSuccededCountCol, (Object)0, 5)), (Object)12);
        ce.addWhen(new Criteria(succededCountCol, (Object)0, 5).and(new Criteria(notSuccededCountCol, (Object)0, 5)), (Object)3);
        ce.addWhen(new Criteria(notSuccededCountCol, (Object)null, 0).or(new Criteria(notSuccededCountCol, (Object)0, 0)), (Object)6);
        final Column userDocStatus = (Column)Column.createFunction("DISTINCT", new Object[] { ce });
        userDocStatus.setType(4);
        userDocStatus.setColumnAlias("USER_DOC_STATUS");
        final Column docIDCol = new Column(innerTable.getTableAlias(), innerDocCol.getColumnAlias(), "docIDcol");
        final Column userCol = new Column(innerTable.getTableAlias(), innerResCol.getColumnAlias(), "userCol");
        middleQuery.addSelectColumn(userDocStatus);
        middleQuery.addSelectColumn(userCol);
        middleQuery.addSelectColumn(docIDCol);
        middleQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(userCol, docIDCol))));
        final DerivedTable middleTable = new DerivedTable("middleDT", (Query)middleQuery);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DocumentToMDMResource");
        updateQuery.addJoin(new Join(Table.getTable("DocumentToMDMResource"), (Table)middleTable, new String[] { "RESOURCE_ID", "DOC_ID" }, new String[] { userCol.getColumnAlias(), docIDCol.getColumnAlias() }, 2));
        updateQuery.setUpdateColumn("STATUS_ID", (Object)Column.getColumn(middleTable.getTableAlias(), userDocStatus.getColumnAlias()));
        String updateSqlStr = RelationalAPI.getInstance().getUpdateSQL(updateQuery);
        updateSqlStr = updateSqlStr.replace("=middleDT.userCol", "=middleDT.\"userCol\"");
        updateSqlStr = updateSqlStr.replace("=middleDT.USER_DOC_STATUS", "=middleDT.\"USER_DOC_STATUS\"");
        updateSqlStr = updateSqlStr.replace("middleDT.docIDcol", "middleDT.\"docIDcol\"");
        RelationalAPI.getInstance().execute(connection, updateSqlStr);
    }
    
    void updateDocStatus(final JSONObject qDataObj) {
        Connection connection = null;
        try {
            connection = RelationalAPI.getInstance().getConnection();
            final List<Long> docList = this.extractDocIDlist(connection, qDataObj);
            DocMgmt.logger.log(Level.INFO, "updating doc summary");
            this.upsertDocSummary(connection);
            DocMgmt.logger.log(Level.INFO, "updated doc summary");
            DocMgmt.logger.log(Level.INFO, "updating doc-policy summary");
            this.upsertDocPolicySummary(connection);
            DocMgmt.logger.log(Level.INFO, "updated doc-policy summary");
            DocMgmt.logger.log(Level.INFO, "updating resource summary");
            ResourceSummaryHandler.getInstance().updateResSummary(connection);
            DocMgmt.logger.log(Level.INFO, "updated resource summary");
            if (docList != null && !docList.isEmpty()) {
                DocMgmt.logger.log(Level.INFO, "updating user-doc status");
                this.updateUserSummary(connection, docList);
                DocMgmt.logger.log(Level.INFO, "updated user-doc status");
                DocMgmt.logger.log(Level.INFO, "updating group-doc status");
                this.updateGroupDocSummary(connection, docList);
                DocMgmt.logger.log(Level.INFO, "updated group-doc status");
            }
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex) {
                DocMgmt.logger.log(Level.SEVERE, null, ex);
            }
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex2) {
                DocMgmt.logger.log(Level.SEVERE, null, ex2);
            }
        }
    }
    
    private void addDocStatusTaskToQueue(List<Long> docsLongList, final List<Long> docMDlongList) {
        try {
            if (docsLongList == null) {
                docsLongList = new ArrayList<Long>();
            }
            final Long currentTime = System.currentTimeMillis();
            final DCQueueData queueData = new DCQueueData();
            queueData.postTime = currentTime;
            queueData.fileName = String.valueOf(currentTime);
            final DCQueue queue = DCQueueHandler.getQueue("doc-mgmt");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put((Object)"Task", (Object)"STATUS_UPDATE_TASK");
            if (docsLongList != null) {
                jsonObject.put((Object)"DOC_ID", (Object)JSONUtil.convertListToJSONArray((Long[])docsLongList.toArray(new Long[docsLongList.size()])));
            }
            if (docMDlongList != null) {
                jsonObject.put((Object)"DOC_MD_ID", (Object)JSONUtil.convertListToJSONArray((Long[])docMDlongList.toArray(new Long[docMDlongList.size()])));
            }
            queue.addToQueue(queueData, jsonObject.toString());
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public Object executeTxTask(final Properties properties) throws Exception {
        final String queryType = ((Hashtable<K, String>)properties).get("QUERY_TYPE");
        if (!SyMUtil.isStringEmpty(queryType)) {
            if (queryType.equalsIgnoreCase("INSERT_QUERY")) {
                final SelectQuery selectQuery = ((Hashtable<K, SelectQuery>)properties).get("QUERY");
                final HashMap<String, Column> colMap = ((Hashtable<K, HashMap<String, Column>>)properties).get("SELECTION");
                DirectoryQueryutil.getInstance().executeInsertQuery((Connection)((Hashtable<K, Connection>)properties).get("CONNECTION"), selectQuery, (String)((Hashtable<K, String>)properties).get("INSERT_TABLE"), (HashMap)colMap, (HashMap)null, false);
            }
            else if (queryType.equalsIgnoreCase("UPDATE_QUERY")) {
                final UpdateQuery updateQuery = ((Hashtable<K, UpdateQuery>)properties).get("QUERY");
                DirectoryQueryutil.getInstance().executeUpdateQuery((Connection)((Hashtable<K, Connection>)properties).get("CONNECTION"), updateQuery, false);
            }
        }
        return null;
    }
    
    public void reviseAgentDocSummary(final ArrayList<Long> authroizedDocMDids) {
        try {
            this.addDocStatusTaskToQueue(null, authroizedDocMDids);
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private List<Long> extractDocIDlist(final Connection connection, final JSONObject qDataObj) throws Exception {
        final List<Long> docList = new ArrayList<Long>();
        if (qDataObj.containsKey((Object)"DOC_ID")) {
            docList.addAll(JSONUtil.convertJSONArrayToArrayList((JSONArray)qDataObj.get((Object)"DOC_ID")));
        }
        if (qDataObj.containsKey((Object)"DOC_MD_ID")) {
            final List<Long> docMDList = JSONUtil.convertJSONArrayToArrayList((JSONArray)qDataObj.get((Object)"DOC_MD_ID"));
            final Set<Long> docSet = new HashSet<Long>();
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentManagedDeviceRel"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("DocumentManagedDeviceRel", "DOC_MD_ID"), (Object)docMDList.toArray(new Long[docMDList.size()]), 8));
            selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "DOC_ID"));
            final JSONArray jsonArray = MDMUtil.executeSelectQuery(connection, selectQuery);
            for (int i = 0; i < jsonArray.size(); ++i) {
                final JSONObject jsObject = (JSONObject)jsonArray.get(i);
                final Long docID = (Long)jsObject.get((Object)"DOC_ID");
                if (docID != null) {
                    docSet.add(docID);
                }
            }
            docList.addAll(docSet);
        }
        return docList;
    }
    
    static {
        DocSummaryHandler.docSummaryHandler = null;
    }
}
