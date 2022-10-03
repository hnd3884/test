package com.me.devicemanagement.framework.server.queue;

import java.util.Hashtable;
import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import java.util.Set;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Level;
import org.json.simple.parser.JSONParser;
import redis.clients.jedis.Jedis;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import org.json.simple.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.List;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public class RedisQueueUtil
{
    public static String sourceClass;
    public static RedisQueueUtil redisQueueUtil;
    private static Logger redisLogger;
    
    private RedisQueueUtil() {
    }
    
    public static RedisQueueUtil getInstance() throws Exception {
        if (RedisQueueUtil.redisQueueUtil == null) {
            RedisQueueUtil.redisQueueUtil = new RedisQueueUtil();
        }
        return RedisQueueUtil.redisQueueUtil;
    }
    
    private static void addAllColumns(final SelectQuery query, final String queueTable) {
        final String sourceMethod = "addAllColumns";
        try {
            final List<String> columnNameList = MetaDataUtil.getTableDefinitionByName(queueTable).getColumnNames();
            final List<Column> columnList = new ArrayList<Column>();
            for (final String columnName : columnNameList) {
                columnList.add(Column.getColumn(queueTable, columnName, queueTable + "_" + columnName));
            }
            query.addSelectColumns((List)columnList);
            SyMLogger.debug(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Added columns : " + columnList);
        }
        catch (final Exception ex) {
            SyMLogger.error(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Caught exception while adding columns : ", ex);
        }
    }
    
    public static SelectQuery getSelectQueryForQueueData(final String qTable, final String qExtnTable, final String qPriorityRefTable) {
        final Table baseTable = Table.getTable(qTable);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
        if (qPriorityRefTable != null) {
            final Table baserPriorityTable = Table.getTable(qPriorityRefTable);
            query.addJoin(new Join(baseTable, baserPriorityTable, new String[] { "QINFO_ID" }, new String[] { "QINFO_ID" }, 1));
            addAllColumns(query, qPriorityRefTable);
        }
        if (qExtnTable != null) {
            final Table baseExtnTable = Table.getTable(qExtnTable);
            query.addJoin(new Join(baseTable, baseExtnTable, new String[] { "QINFO_ID" }, new String[] { "QINFO_ID" }, 1));
            addAllColumns(query, qExtnTable);
        }
        query.addSelectColumn(Column.getColumn(qTable, "QINFO_ID"));
        query.addSelectColumn(Column.getColumn(qTable, "DATA_FILE_NAME"));
        query.addSelectColumn(Column.getColumn(qTable, "POST_TIME"));
        query.addSelectColumn(Column.getColumn(qTable, "QUEUE_DATA_TYPE"));
        query.addSelectColumn(Column.getColumn(qTable, "REQUEST_ID"));
        query.addSelectColumn(Column.getColumn(qTable, "IS_PRIORITY"));
        query.addSortColumn(new SortColumn(Column.getColumn(qTable, "POST_TIME"), true));
        return query;
    }
    
    private static JSONObject getAdditionalTableData(final DataObject queueDO, final Long qInfoID, final String tableName) {
        final String sourceMethod = "getAdditionalTableData";
        if (tableName != null) {
            try {
                JSONObject jsonObject = null;
                final Criteria criteria = new Criteria(Column.getColumn(tableName, "QINFO_ID"), (Object)qInfoID, 0);
                final Row tableRow = queueDO.getRow(tableName, criteria);
                final List pkColumn = MetaDataUtil.getTableDefinitionByName(tableName).getPrimaryKey().getColumnList();
                if (tableRow != null) {
                    jsonObject = new JSONObject();
                    final List columns = tableRow.getColumns();
                    for (int index = 0; index < columns.size(); ++index) {
                        final String columnName = String.valueOf(columns.get(index));
                        if (!pkColumn.contains(columnName)) {
                            jsonObject.put((Object)columnName, tableRow.get(columnName));
                        }
                    }
                }
                return jsonObject;
            }
            catch (final Exception e) {
                SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Exception while fetching data for additional queue table : " + tableName);
            }
        }
        return null;
    }
    
    public static void moveDataToRedis(final String qName, final String qTable, final String qExtnTable, final String qPriorityRefTable) {
        final String sourceMethod = "moveDataToRedis";
        Connection con = null;
        Jedis jedis = null;
        final JSONObject queueObj = new JSONObject();
        final JSONObject queueDetailsObj = new JSONObject();
        JSONObject queueExtnDetailsObj = new JSONObject();
        JSONObject queuePriorityDetailsObj = new JSONObject();
        final String redisFilenameQ = "FILENAMEQ_" + qName;
        final String redisInputQueueName = "INPUT_" + qName;
        final String redisInputPriorityQueueName = "PRIORITY_" + qName;
        try {
            con = RelationalAPI.getInstance().getConnection();
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            final SelectQuery query = getSelectQueryForQueueData(qTable, qExtnTable, qPriorityRefTable);
            final DataObject queueDO = SyMUtil.getPersistenceLite().get(query);
            if (queueDO.isEmpty()) {
                SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "No data present in DB to be migrated for queue " + qName);
            }
            else {
                SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Started migration process from DB to Redis for queue " + qName);
                final Iterator queueDS = queueDO.getRows(qTable);
                final int rowCount = queueDO.size(qTable);
                while (queueDS.hasNext()) {
                    final Row row = queueDS.next();
                    final long qid = (long)row.get("QINFO_ID");
                    final String qFileName = (String)row.get("DATA_FILE_NAME");
                    final boolean isPriority = (boolean)row.get("IS_PRIORITY");
                    queueDetailsObj.put((Object)"POST_TIME", (Object)(long)row.get("POST_TIME"));
                    queueDetailsObj.put((Object)"DATA_FILE_NAME", (Object)qFileName);
                    queueDetailsObj.put((Object)"QUEUE_DATA_TYPE", (Object)(int)row.get("QUEUE_DATA_TYPE"));
                    queueDetailsObj.put((Object)"QUEUE_DATA_STATE", (Object)2);
                    queueDetailsObj.put((Object)"REQUEST_ID", (Object)row.get("REQUEST_ID"));
                    queueDetailsObj.put((Object)"IS_PRIORITY", (Object)isPriority);
                    queueExtnDetailsObj = getAdditionalTableData(queueDO, qid, qExtnTable);
                    queuePriorityDetailsObj = getAdditionalTableData(queueDO, qid, qPriorityRefTable);
                    Long rid = null;
                    if (queueExtnDetailsObj != null) {
                        queueObj.put((Object)qExtnTable, (Object)queueExtnDetailsObj);
                        rid = (Long)queueExtnDetailsObj.get((Object)"RESOURCE_ID");
                        jedis.hset(qExtnTable, qFileName, queueExtnDetailsObj.toString());
                    }
                    if (queuePriorityDetailsObj != null) {
                        queueObj.put((Object)qPriorityRefTable, (Object)queuePriorityDetailsObj);
                        rid = (Long)queuePriorityDetailsObj.get((Object)"REFERENCE_ID");
                    }
                    if (rid != null) {
                        final String keyName = qTable + "QREF_ID" + rid;
                        jedis.lpush(keyName, new String[] { qFileName });
                    }
                    queueObj.put((Object)"MainQueue", (Object)queueDetailsObj);
                    jedis.hset(redisFilenameQ, qFileName, queueObj.toString());
                    if (isPriority) {
                        jedis.lpush(redisInputPriorityQueueName, new String[] { qFileName });
                    }
                    else {
                        jedis.lpush(redisInputQueueName, new String[] { qFileName });
                    }
                }
                final long redisInputQueueLen = jedis.llen(redisInputQueueName);
                final long redisPriorityQueueLen = jedis.llen(redisInputPriorityQueueName);
                final long redisQueueLen = redisInputQueueLen + redisPriorityQueueLen;
                SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Queue data present in DB  :" + rowCount);
                SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Queue data moved to Redis :" + redisQueueLen);
                SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Ending migration process from DB to Redis for queue " + qName);
                if (rowCount == redisQueueLen) {
                    DataAccess.delete(qTable, (Criteria)null);
                    SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Deleting queue data  from DB for queue" + qName);
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Exception in migrating data to Redis from DB for queue " + qName, e);
            if (jedis != null) {
                jedis.close();
            }
            try {
                if (con != null) {
                    con.close();
                }
            }
            catch (final SQLException e2) {
                SyMLogger.error(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Exception while closing connection :", e2);
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
            try {
                if (con != null) {
                    con.close();
                }
            }
            catch (final SQLException e3) {
                SyMLogger.error(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Exception while closing connection :", e3);
            }
        }
    }
    
    private static Row addAllColumnsFromJson(final Row row, final JSONObject jsonInput) {
        for (final String key : jsonInput.keySet()) {
            final Object value = jsonInput.get((Object)key);
            row.set(key, value);
        }
        return row;
    }
    
    public static void moveDataToDB(final String redisQueueName, final String qName, final String qTable, final String qExtnTable, final String qPriorityRefTable) {
        Jedis jedis = null;
        String filename = "";
        final String redisFilenameQ = "FILENAMEQ_" + qName;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            final DataObject queueTableDO = SyMUtil.getPersistence().constructDataObject();
            while (jedis.llen(redisQueueName) > 0L) {
                filename = jedis.rpop(redisQueueName);
                if (filename != null) {
                    final String queueData = jedis.hget(redisFilenameQ, filename);
                    jedis.hdel(redisFilenameQ, new String[] { filename });
                    if (queueData == null) {
                        continue;
                    }
                    final JSONParser parser = new JSONParser();
                    final JSONObject queueObj = (JSONObject)parser.parse(queueData);
                    final JSONObject queueDetailsObj = (JSONObject)queueObj.get((Object)"MainQueue");
                    final JSONObject queueExtnDetailsObj = queueObj.containsKey((Object)qExtnTable) ? ((JSONObject)queueObj.get((Object)qExtnTable)) : null;
                    final JSONObject queuePriorityDetailsObj = queueObj.containsKey((Object)qPriorityRefTable) ? ((JSONObject)queueObj.get((Object)qPriorityRefTable)) : null;
                    if (queueDetailsObj == null) {
                        continue;
                    }
                    final Row row = new Row(qTable);
                    row.set("POST_TIME", (Object)(long)queueDetailsObj.get((Object)"POST_TIME"));
                    row.set("DATA_FILE_NAME", (Object)queueDetailsObj.get((Object)"DATA_FILE_NAME"));
                    row.set("QUEUE_DATA_TYPE", (Object)((Long)queueDetailsObj.get((Object)"QUEUE_DATA_TYPE")).intValue());
                    row.set("QUEUE_DATA_STATE", (Object)2);
                    row.set("REQUEST_ID", (Object)queueDetailsObj.get((Object)"REQUEST_ID"));
                    row.set("IS_PRIORITY", (Object)(boolean)queueDetailsObj.get((Object)"IS_PRIORITY"));
                    queueTableDO.addRow(row);
                    Long rid = null;
                    if (queueExtnDetailsObj != null && !queueExtnDetailsObj.isEmpty()) {
                        final Row rowExtn = new Row(qExtnTable);
                        rowExtn.set("QINFO_ID", row.get("QINFO_ID"));
                        rid = (Long)queueExtnDetailsObj.get((Object)"RESOURCE_ID");
                        addAllColumnsFromJson(rowExtn, queueExtnDetailsObj);
                        queueTableDO.addRow(rowExtn);
                        jedis.hdel(qExtnTable, new String[] { filename });
                    }
                    if (queuePriorityDetailsObj != null && !queuePriorityDetailsObj.isEmpty()) {
                        final Row rowPriority = new Row(qPriorityRefTable);
                        rowPriority.set("QINFO_ID", row.get("QINFO_ID"));
                        rid = (Long)queuePriorityDetailsObj.get((Object)"REFERENCE_ID");
                        addAllColumnsFromJson(rowPriority, queuePriorityDetailsObj);
                        queueTableDO.addRow(rowPriority);
                    }
                    if (rid == null) {
                        continue;
                    }
                    final String keyName = qTable + "QREF_ID" + rid;
                    jedis.lrem(keyName, -1L, filename);
                }
            }
            SyMUtil.getPersistenceLite().add(queueTableDO);
        }
        catch (final Exception e) {
            RedisQueueUtil.redisLogger.log(Level.WARNING, "Exception while migrating data ", e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    
    public static void moveDataToDB(final String qName, final String qTable, final String qExtnTable, final String qPriorityRefTable) {
        final String sourceMethod = "moveDataToDB";
        SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Started migration process from Redis to DB for queue " + qName);
        final String redisInputQueueName = "INPUT_" + qName;
        final String redisInputPriorityQueueName = "PRIORITY_" + qName;
        final String redisInputProcessQueueName = "PROCESS_" + qName;
        final String redisExecutionDataSet = "EXECUTION_" + qName;
        final String redisFileNameSet = "FILENAMEQ_" + qName;
        Jedis jedis = null;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            final Set<String> uncompletedQdata = jedis.hkeys(redisExecutionDataSet);
            for (final String qEntry : uncompletedQdata) {
                final String isPriority = jedis.hget(redisExecutionDataSet, qEntry);
                if (isPriority.equalsIgnoreCase("true")) {
                    jedis.rpush(redisInputPriorityQueueName, new String[] { qEntry });
                }
                else {
                    jedis.rpush(redisInputProcessQueueName, new String[] { qEntry });
                }
            }
            if (uncompletedQdata.size() > 0) {
                jedis.del(redisExecutionDataSet);
            }
            final long totalCount = jedis.llen(redisInputPriorityQueueName) + jedis.llen(redisInputProcessQueueName) + jedis.llen(redisInputQueueName);
            if (totalCount > 0L) {
                moveDataToDB(redisInputQueueName, qName, qTable, qExtnTable, qPriorityRefTable);
                moveDataToDB(redisInputPriorityQueueName, qName, qTable, qExtnTable, qPriorityRefTable);
                moveDataToDB(redisInputProcessQueueName, qName, qTable, qExtnTable, qPriorityRefTable);
                final int rowCount = DBUtil.getRecordCount(qTable, "QINFO_ID", null);
                SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Total Queue Data count in Redis :" + totalCount);
                SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Total Queue Data Count in DB    :" + rowCount);
            }
            else {
                SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "No queue data present in Redis to be migrated");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Exception while migrating data", e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Ending migration process from Redis to DB for queue " + qName);
    }
    
    public static void migrateFromDBToRedis() {
        migrateQueuedata(true);
    }
    
    public static void migrateFromRedisToDB() {
        migrateQueuedata(false);
    }
    
    public static void migrateQueuedata(final boolean isRedisEnabled) {
        final String sourceMethod = "migrateQueuedata";
        try {
            final String redisQueueClassName = "com.me.devicemanagement.onpremise.server.queue.RedisQueue";
            final String defaultQueueClassName = "com.me.devicemanagement.onpremise.server.queue.DefaultDCQueue";
            final String qmdTable = "DCQueueMetaData";
            final DataObject qmdDO = SyMUtil.getPersistence().get(qmdTable, (Criteria)null);
            if (qmdDO.isEmpty()) {
                SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "No queue data is found in the table: " + qmdTable);
                return;
            }
            final Iterator qmdRows = qmdDO.getRows(qmdTable);
            while (qmdRows.hasNext()) {
                final Row queueMetaDataRow = qmdRows.next();
                final String queueName = (String)queueMetaDataRow.get("QUEUE_NAME");
                final String queueTableName = (String)queueMetaDataRow.get("QUEUE_TABLE_NAME");
                final String queueClassName = (String)queueMetaDataRow.get("QUEUE_CLASS_NAME");
                final String queueExtnTableName = (String)queueMetaDataRow.get("QUEUE_EXTN_TABLE_NAME");
                final String priorityQRefTableName = (String)queueMetaDataRow.get("PRIORITY_Q_REF_TABLE_NAME");
                if (isRedisEnabled) {
                    if (!queueClassName.equalsIgnoreCase(defaultQueueClassName)) {
                        continue;
                    }
                    moveDataToRedis(queueName, queueTableName, queueExtnTableName, priorityQRefTableName);
                    queueMetaDataRow.set("QUEUE_CLASS_NAME", (Object)redisQueueClassName);
                    qmdDO.updateRow(queueMetaDataRow);
                }
                else {
                    if (!queueClassName.equalsIgnoreCase(redisQueueClassName)) {
                        continue;
                    }
                    moveDataToDB(queueName, queueTableName, queueExtnTableName, priorityQRefTableName);
                    moveAgentDataToFile(queueName);
                    queueMetaDataRow.set("QUEUE_CLASS_NAME", (Object)defaultQueueClassName);
                    qmdDO.updateRow(queueMetaDataRow);
                }
            }
            SyMUtil.getPersistence().update(qmdDO);
        }
        catch (final Exception ex) {
            SyMLogger.error(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Caught exception while migrating data between DB", ex);
        }
    }
    
    private static void moveAgentDataToFile(final String queueName) {
        final String sourceMethod = "moveDataToDB";
        Jedis jedis = null;
        final String redisAgentDataFilenameSet = "FILENAME_" + queueName;
        final String agentDataLocation = "AGENTFILELOCATION_" + queueName;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            String qFolderPath = DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "dc-queue";
            qFolderPath = qFolderPath + File.separator + queueName;
            final long hlength = jedis.hlen(redisAgentDataFilenameSet);
            if (hlength > 0L) {
                SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Started agent data migration process from Redis to DB  " + queueName);
                SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Total no of Agent Data in Redis" + hlength);
                final Set<String> fileNameSet = jedis.hkeys(redisAgentDataFilenameSet);
                for (final String fileName : fileNameSet) {
                    final String fileData = jedis.hget(redisAgentDataFilenameSet, fileName);
                    final String filePath = qFolderPath + File.separator + fileName;
                    FileAccessUtil.writeDataInFile(filePath, fileData);
                }
                SyMLogger.info(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Total no of agentdata migrated from redis to file" + (hlength - jedis.hlen(redisAgentDataFilenameSet)));
                jedis.del(redisAgentDataFilenameSet);
                jedis.del(agentDataLocation);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(RedisQueueUtil.redisLogger, RedisQueueUtil.sourceClass, sourceMethod, "Exception while migrating agent data from redis to DB", e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    
    public static ArrayList getRedisDataForMultipleRID(final String qName, final String qtableName, final String qExtnTableName, final ArrayList<Long> resourceIDs) {
        final ArrayList<JSONObject> qData = new ArrayList<JSONObject>();
        final JSONParser jsonParser = new JSONParser();
        Jedis jedis = null;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            for (final Long resID : resourceIDs) {
                final String keyName = qtableName + "QREF_ID" + resID;
                final String filekey = "FILENAMEQ_" + qName;
                final long qDataSize = jedis.llen(keyName);
                final ArrayList<String> qDataKeyList = (ArrayList<String>)jedis.lrange(keyName, 0L, qDataSize);
                qDataKeyList.toString();
                for (final String qDataFileName : qDataKeyList) {
                    final String qDataList = jedis.hget(filekey, qDataFileName);
                    if (qDataList != null) {
                        final JSONObject qDataJson = (JSONObject)jsonParser.parse(qDataList);
                        final JSONObject mainQueueExtn = qDataJson.containsKey((Object)qExtnTableName) ? ((JSONObject)qDataJson.get((Object)qExtnTableName)) : null;
                        qData.add(mainQueueExtn);
                    }
                }
            }
        }
        catch (final Exception e) {
            RedisQueueUtil.redisLogger.log(Level.INFO, "Exception while getting queue extn table data from redis", e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return qData;
    }
    
    public static ArrayList getRedisData(final String qName, final String qtableName, final String qExtnTableName, final Long resID) {
        final ArrayList qData = new ArrayList();
        final JSONParser jsonParser = new JSONParser();
        Jedis jedis = null;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            final String keyName = qtableName + "QREF_ID" + resID;
            final String filekey = "FILENAMEQ_" + qName;
            final long qDataSize = jedis.llen(keyName);
            final ArrayList<String> qDataKeyList = (ArrayList<String>)jedis.lrange(keyName, 0L, qDataSize);
            for (final String qDataFileName : qDataKeyList) {
                final String qDataList = jedis.hget(filekey, qDataFileName);
                if (qDataList != null) {
                    final JSONObject qDataJson = (JSONObject)jsonParser.parse(qDataList);
                    final JSONObject mainQueueExtn = qDataJson.containsKey((Object)qExtnTableName) ? ((JSONObject)qDataJson.get((Object)qExtnTableName)) : null;
                    qData.add(mainQueueExtn);
                }
            }
        }
        catch (final Exception e) {
            RedisQueueUtil.redisLogger.log(Level.INFO, "Exception while getting queue extn table data from redis", e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return qData;
    }
    
    public static Properties getRedisQueueDetails(final String queueName) {
        final String redisExecutionDataSet = "EXECUTION_" + queueName;
        final String redisFileNameSet = "FILENAMEQ_" + queueName;
        final String redisInputQueueName = "INPUT_" + queueName;
        final String redisProcessingQueueName = "PRIORITY_" + queueName;
        final String redisInputPriorityQueueName = "PROCESS_" + queueName;
        final Properties props = new Properties();
        Jedis jedis = null;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            final Set<String> execQList = jedis.hkeys(redisExecutionDataSet);
            final long memoryCount = execQList.size();
            final long inputQCount = jedis.llen(redisInputQueueName);
            final long processQCount = jedis.llen(redisProcessingQueueName);
            final long priorityQCount = jedis.llen(redisInputPriorityQueueName);
            final long DBCount = inputQCount + processQCount + priorityQCount;
            long totalCount = jedis.hkeys(redisFileNameSet).size();
            final long totalCountFromData = DBCount + memoryCount;
            RedisQueueUtil.redisLogger.log(Level.FINE, "totolCount " + totalCount);
            RedisQueueUtil.redisLogger.log(Level.FINE, "memCount" + memoryCount);
            RedisQueueUtil.redisLogger.log(Level.FINE, "qName" + queueName);
            if (totalCount > totalCountFromData) {
                totalCount = totalCountFromData;
                RedisQueueUtil.redisLogger.log(Level.FINE, "TotalCount after modification " + totalCount);
            }
            long lastTime = 0L;
            long firstTime = 0L;
            final JSONParser p = new JSONParser();
            if (totalCount > 0L && totalCount > memoryCount) {
                final String qName = (inputQCount > processQCount) ? redisInputQueueName : ((processQCount > priorityQCount) ? redisProcessingQueueName : redisInputPriorityQueueName);
                final String fileNameKey = jedis.lindex(qName, 0L);
                if (fileNameKey != null) {
                    final String qEntry = jedis.hget(redisFileNameSet, fileNameKey);
                    if (qEntry != null) {
                        final JSONObject obj = (JSONObject)p.parse(qEntry);
                        final JSONObject mainQ = (JSONObject)obj.get((Object)"MainQueue");
                        lastTime = (firstTime = (long)mainQ.get((Object)"POST_TIME"));
                    }
                }
            }
            final Iterator<String> iterator = execQList.iterator();
            if (iterator.hasNext()) {
                final String fileName;
                final String qExecEntry = fileName = iterator.next();
                final String qEntry2 = jedis.hget(redisFileNameSet, fileName);
                if (qEntry2 != null) {
                    final JSONObject obj2 = (JSONObject)p.parse(qEntry2);
                    final JSONObject mainQ2 = (JSONObject)obj2.get((Object)"MainQueue");
                    firstTime = (long)mainQ2.get((Object)"POST_TIME");
                    if (lastTime == 0L) {
                        lastTime = firstTime;
                    }
                }
            }
            ((Hashtable<String, Long>)props).put("memoryCount", memoryCount);
            ((Hashtable<String, Long>)props).put("totalCount", totalCount);
            ((Hashtable<String, Long>)props).put("lastTime", lastTime);
            ((Hashtable<String, Long>)props).put("firstTime", firstTime);
        }
        catch (final Exception e) {
            RedisQueueUtil.redisLogger.log(Level.WARNING, "Exception while getting redis queue details :", e);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return props;
    }
    
    static {
        RedisQueueUtil.sourceClass = "RedisQueueUtil";
        RedisQueueUtil.redisQueueUtil = null;
        RedisQueueUtil.redisLogger = Logger.getLogger("RedisLogger");
    }
}
