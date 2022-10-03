package com.me.devicemanagement.framework.server.message;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import java.util.HashSet;
import com.adventnet.persistence.OperationInfo;
import java.util.logging.Level;
import com.me.ems.summaryserver.probe.sync.utils.SyncUtil;
import java.util.Set;
import java.util.logging.Logger;
import com.adventnet.mfw.message.MessageListener;

public class MickeyMessageListener implements MessageListener
{
    Logger logger;
    public static Set<String> checkTableSet;
    
    public MickeyMessageListener() {
        this.logger = Logger.getLogger("ProbeSyncLogger");
        SyncUtil.setSyncObjects();
    }
    
    public void onMessage(final Object msg) {
        this.logger.log(Level.FINE, "Message to be conveyed is: {0}", new Object[] { msg.toString() });
        final OperationInfo oi = (OperationInfo)msg;
        final int operationType = oi.getOperation();
        this.logger.log(Level.FINE, "The Tables affected now are : {0}", new Object[] { oi.getTableNames().toString() });
        final List tabList = oi.getTableNames();
        final List bulkTableList = oi.getBulkTableNames();
        if (bulkTableList.size() > 0) {
            this.logger.log(Level.SEVERE, "deletion operations exceed the notification limit - Some Data may get missed");
        }
        final Set<String> parentDeleteTableList = new HashSet<String>();
        if (operationType == 3) {
            final List inputDeleteCriteria = oi.getInputDeleteCriterias();
            if (inputDeleteCriteria != null) {
                for (final Object inputDeleteCriterion : inputDeleteCriteria) {
                    final Criteria criteria = (Criteria)inputDeleteCriterion;
                    if (criteria != null) {
                        final Column criColumn = criteria.getColumn();
                        if (criColumn != null) {
                            final String tableName = criColumn.getTableAlias();
                            final Table table = new Table(tableName);
                            parentDeleteTableList.add(table.getTableName().toLowerCase());
                        }
                        else {
                            final List<String> tableList = new ArrayList<String>();
                            this.getAllTablesInCriteria(criteria, tableList);
                            parentDeleteTableList.addAll(tableList);
                        }
                    }
                }
            }
            final DeleteQuery inputDeleteQuery = oi.getInputDeleteQuery();
            if (inputDeleteQuery != null) {
                final String tableName2 = inputDeleteQuery.getTableName();
                final Table table2 = new Table(tableName2);
                parentDeleteTableList.add(table2.getTableName().toLowerCase());
            }
        }
        try {
            for (int index = 0; index < tabList.size(); ++index) {
                final String currTableName = tabList.get(index).toString().toLowerCase();
                final String currTableNameForPKId = tabList.get(index).toString();
                if (MickeyMessageListener.checkTableSet.contains(currTableName)) {
                    final String currTime = String.valueOf(System.currentTimeMillis());
                    if (operationType != 3) {
                        ApiFactoryProvider.getRedisHashMap().put(currTableName, currTime, 2);
                        this.logger.log(Level.INFO, "Summary Server Table updated: {0},  Time of update: {1}", new Object[] { currTableName, currTime });
                    }
                    else if (parentDeleteTableList.contains(currTableName)) {
                        final String currTablePKColumn = ApiFactoryProvider.getRedisHashMap().get("PK_COLUMN", currTableName, 2);
                        this.logger.log(Level.INFO, "Operation Type: {0}", new Object[] { operationType });
                        this.logger.log(Level.INFO, "Summary Server Table record deleted: {0}, Time of update: {1}", new Object[] { currTableName, currTime });
                        final DataObject dataObject = oi.getDataObject();
                        if (dataObject == null) {
                            this.logger.log(Level.INFO, "========================================================================================");
                            this.logger.log(Level.INFO, "notification data exceeds configured level");
                            this.logger.log(Level.INFO, "========================================================================================");
                            return;
                        }
                        final List operationList = dataObject.getOperations();
                        this.logger.log(Level.INFO, "Deletion Operation Size : {0}", operationList.size());
                        for (int subIndex = 0; subIndex < operationList.size(); ++subIndex) {
                            this.logger.log(Level.FINE, "Current operationList value: {0}", operationList.get(subIndex));
                            final String currXmlValue = operationList.get(subIndex).toString().replaceAll(",", "");
                            final String moduleId = ApiFactoryProvider.getRedisHashMap().get("MODULE_ID", currTableName, 2);
                            if (moduleId != null) {
                                this.logger.log(Level.FINE, "Got the module Id:{0} ", moduleId);
                                this.logger.log(Level.FINE, "Got the currXmlValue: {0}", currXmlValue);
                                final String deletionValue = SyncUtil.getInstance().getPKValue(currXmlValue, currTableNameForPKId, currTablePKColumn);
                                this.logger.log(Level.FINE, "Got the deletionValue: {0}", deletionValue);
                                if (deletionValue != null) {
                                    SyncUtil.getInstance().addDeletionAudit(currTableNameForPKId, currTablePKColumn, deletionValue, currTime, Long.parseLong(moduleId));
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in capturing table update time ", e);
        }
    }
    
    private void getAllTablesInCriteria(final Criteria criteria, final List<String> tableList) {
        if (criteria != null) {
            if (criteria.getColumn() != null) {
                final String tableName = criteria.getColumn().getTableAlias();
                final Table table = new Table(tableName);
                tableList.add(table.getTableName().toLowerCase());
            }
            this.getAllTablesInCriteria(criteria.getLeftCriteria(), tableList);
            this.getAllTablesInCriteria(criteria.getRightCriteria(), tableList);
        }
    }
    
    static {
        MickeyMessageListener.checkTableSet = new HashSet<String>();
    }
}
