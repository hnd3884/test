package com.me.ems.onpremise.common.queuecount.core;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.queue.RedisQueueUtil;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import com.adventnet.mfw.bean.BeanUtil;
import java.util.logging.Logger;
import com.adventnet.persistence.Persistence;

public class QueueCountUtil
{
    private static Persistence queuePersistence;
    private static Logger logger;
    
    public static void addOrUpdateQueueCountTable(final long queueId, final long count, final long memoryCount, final long processTime, final long lastDataTime) throws Exception {
        try {
            QueueCountUtil.queuePersistence = (Persistence)BeanUtil.lookup("Persistence");
            final Row qCountRow = new Row("DCQueueSummary");
            final Criteria criteria = new Criteria(new Column("DCQueueSummary", "Q_METADATA_ID"), (Object)queueId, 0);
            DataObject dataObject = QueueCountUtil.queuePersistence.get("DCQueueSummary", criteria);
            qCountRow.set("Q_COUNT", (Object)count);
            qCountRow.set("Q_METADATA_ID", (Object)queueId);
            qCountRow.set("Q_SIZE_IN_MEMORY", (Object)memoryCount);
            if (processTime != 0L) {
                qCountRow.set("DATA_POSTED_TIME_LOW", (Object)processTime);
            }
            if (lastDataTime != 0L) {
                qCountRow.set("DATA_POSTED_TIME_HIGH", (Object)lastDataTime);
            }
            final long countDiff = Math.abs(count - memoryCount);
            qCountRow.set("QUEUE_SIZE_PENDING_IN_DB", (Object)countDiff);
            if (dataObject.isEmpty()) {
                dataObject = (DataObject)new WritableDataObject();
                dataObject.addRow(qCountRow);
                QueueCountUtil.queuePersistence.add(dataObject);
            }
            else {
                dataObject.updateRow(qCountRow);
                QueueCountUtil.queuePersistence.update(dataObject);
            }
        }
        catch (final Exception e) {
            QueueCountUtil.logger.log(Level.WARNING, "Exception in addOrUpdateQueueCountTable: ", e);
            throw e;
        }
    }
    
    public static long getQueueCount(final String tableName) throws Exception {
        long count = 0L;
        try {
            final TableDefinition tDef = MetaDataUtil.getTableDefinitionByName(tableName);
            if (tDef != null) {
                final Table qSubTable = new Table(tableName);
                final SelectQuery qsSelect = (SelectQuery)new SelectQueryImpl(qSubTable);
                final Column countCal = new Column((String)null, "*").count();
                countCal.setColumnAlias("QC_COUNT");
                qsSelect.addSelectColumn(countCal);
                count = Long.valueOf(DBUtil.getFirstValue(RelationalAPI.getInstance().getSelectSQL((Query)qsSelect)).toString());
            }
            else {
                QueueCountUtil.logger.log(Level.INFO, "Following table does not exist: " + tableName);
            }
        }
        catch (final Exception e) {
            QueueCountUtil.logger.log(Level.WARNING, "Exception in getQueueCount: ", e);
            throw e;
        }
        return count;
    }
    
    public static long getProcessTime(final String tableName, final int minMax) throws Exception {
        long procTime = 0L;
        try {
            final TableDefinition tDef = MetaDataUtil.getTableDefinitionByName(tableName);
            if (tDef != null) {
                final Table qSubTable = new Table(tableName);
                final SelectQuery qsSelect = (SelectQuery)new SelectQueryImpl(qSubTable);
                final Column postTime = new Column(tableName, "POST_TIME");
                if (minMax == 0) {
                    final Column procCol = postTime.minimum();
                    procCol.setColumnAlias("DATA_POSTED_TIME_LOW");
                    qsSelect.addSelectColumn(procCol);
                }
                else if (minMax == 1) {
                    final Column procLastCol = postTime.maximum();
                    procLastCol.setColumnAlias("DATA_POSTED_TIME_HIGH");
                    qsSelect.addSelectColumn(procLastCol);
                }
                final Object selQObj = DBUtil.getFirstValue(RelationalAPI.getInstance().getSelectSQL((Query)qsSelect));
                if (selQObj != null) {
                    final String selQuery = selQObj.toString();
                    procTime = Long.valueOf(selQuery);
                }
            }
            else {
                QueueCountUtil.logger.log(Level.INFO, "Following table does not exist: " + tableName);
            }
        }
        catch (final Exception e) {
            QueueCountUtil.logger.log(Level.WARNING, "Exception in getProcessTime: ", e);
            throw e;
        }
        return procTime;
    }
    
    public static void refreshAllQueue() throws Exception {
        final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
        final boolean isAdmin = DMUserHandler.isUserInAdminRole(loginID);
        if (isAdmin) {
            long count = 0L;
            long processTime = 0L;
            long lastDataTime = 0L;
            long memoryCount = 0L;
            final DataObject dObjQTab = getAllQTables();
            final Iterator itr = dObjQTab.getRows("DCQueueMetaData");
            final boolean isRedis = Boolean.parseBoolean(SyMUtil.getSyMParameter("enableRedis"));
            while (itr.hasNext()) {
                try {
                    final Row rowQueue = itr.next();
                    final String qTabName = (String)rowQueue.get("QUEUE_TABLE_NAME");
                    final String qName = (String)rowQueue.get("QUEUE_NAME");
                    final long qTabId = Long.valueOf(rowQueue.get("Q_METADATA_ID").toString());
                    if (!isRedis) {
                        count = getQueueCount(qTabName);
                        processTime = getProcessTime(qTabName, 0);
                        lastDataTime = getProcessTime(qTabName, 1);
                        memoryCount = new DCQueueHandler().getMemoryCount(qName);
                    }
                    else {
                        final Properties props = RedisQueueUtil.getRedisQueueDetails(qName);
                        if (props != null) {
                            memoryCount = ((Hashtable<K, Long>)props).get("memoryCount");
                            count = ((Hashtable<K, Long>)props).get("totalCount");
                            processTime = ((Hashtable<K, Long>)props).get("firstTime");
                            lastDataTime = ((Hashtable<K, Long>)props).get("lastTime");
                        }
                    }
                    addOrUpdateQCountTable(qTabId, count, memoryCount, processTime, lastDataTime);
                }
                catch (final Exception ex) {
                    QueueCountUtil.logger.log(Level.WARNING, "Exception in refreshAllQueue: ", ex);
                }
            }
        }
    }
    
    public static void addOrUpdateQCountTable(final long qTabId, final long count, final long memoryCount, final long processTime, final long lastDataTime) throws Exception {
        try {
            final Persistence persistence = (Persistence)BeanUtil.lookup("Persistence");
            final Row qCountRow = new Row("DCQueueSummary");
            final Criteria criteria = new Criteria(new Column("DCQueueSummary", "Q_METADATA_ID"), (Object)qTabId, 0);
            DataObject dataObject = persistence.get("DCQueueSummary", criteria);
            qCountRow.set("Q_COUNT", (Object)count);
            qCountRow.set("Q_METADATA_ID", (Object)qTabId);
            qCountRow.set("Q_SIZE_IN_MEMORY", (Object)memoryCount);
            if (processTime != 0L) {
                qCountRow.set("DATA_POSTED_TIME_LOW", (Object)processTime);
            }
            if (lastDataTime != 0L) {
                qCountRow.set("DATA_POSTED_TIME_HIGH", (Object)lastDataTime);
            }
            final long countDiff = Math.abs(count - memoryCount);
            qCountRow.set("QUEUE_SIZE_PENDING_IN_DB", (Object)countDiff);
            if (dataObject.isEmpty()) {
                dataObject = (DataObject)new WritableDataObject();
                dataObject.addRow(qCountRow);
                persistence.add(dataObject);
            }
            else {
                dataObject.updateRow(qCountRow);
                persistence.update(dataObject);
            }
        }
        catch (final Exception e) {
            QueueCountUtil.logger.log(Level.WARNING, "Exception in addOrUpdateQCountTable: ", e);
            throw e;
        }
    }
    
    public static DataObject getAllQTables() throws Exception {
        DataObject queueTables;
        try {
            final Table qMetaTab = new Table("DCQueueMetaData");
            final SelectQuery queueSelectName = (SelectQuery)new SelectQueryImpl(qMetaTab);
            final Column queueName = new Column("DCQueueMetaData", "*");
            queueSelectName.addSelectColumn(queueName);
            queueTables = SyMUtil.getPersistence().get(queueSelectName);
        }
        catch (final Exception e) {
            QueueCountUtil.logger.log(Level.WARNING, "Exception in getAllQTables: ", e);
            throw e;
        }
        return queueTables;
    }
    
    static {
        QueueCountUtil.logger = Logger.getLogger("DCQueueLogger");
    }
}
