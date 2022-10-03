package com.adventnet.db.migration.adapter;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Set;
import java.util.Collections;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import java.util.Queue;
import com.adventnet.db.persistence.metadata.IndexDefinition;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DBMigrationRetryQueryQueue
{
    private static final Logger LOGGER;
    private static Map<String, List<String>> tableNameNeedsFKTriggers;
    private static Map<String, List<IndexDefinition>> tableNameVsFKIdx;
    private static Map<String, Integer> fkTriggersRequesterVsRequestCount;
    private static Map<String, Queue<ForeignKeyDefinition>> retryFK;
    private static int retryRequests;
    
    public static void initialize() {
        DBMigrationRetryQueryQueue.tableNameNeedsFKTriggers = new ConcurrentHashMap<String, List<String>>();
        DBMigrationRetryQueryQueue.tableNameVsFKIdx = new ConcurrentHashMap<String, List<IndexDefinition>>();
        DBMigrationRetryQueryQueue.fkTriggersRequesterVsRequestCount = new ConcurrentHashMap<String, Integer>();
        DBMigrationRetryQueryQueue.retryFK = new ConcurrentHashMap<String, Queue<ForeignKeyDefinition>>();
    }
    
    public static void addToFKTriggerQueue(final String tableName, final String requesterTableName) {
        final List<String> arrayList = DBMigrationRetryQueryQueue.tableNameNeedsFKTriggers.containsKey(tableName) ? DBMigrationRetryQueryQueue.tableNameNeedsFKTriggers.get(tableName) : Collections.synchronizedList(new ArrayList<String>());
        arrayList.add(requesterTableName);
        DBMigrationRetryQueryQueue.tableNameNeedsFKTriggers.put(tableName, arrayList);
        final Integer count = DBMigrationRetryQueryQueue.fkTriggersRequesterVsRequestCount.containsKey(requesterTableName) ? (DBMigrationRetryQueryQueue.fkTriggersRequesterVsRequestCount.get(requesterTableName) + 1) : 1;
        DBMigrationRetryQueryQueue.fkTriggersRequesterVsRequestCount.put(requesterTableName, count);
    }
    
    public static Set<String> getTableNamesHasFKRetryQuries() {
        return Collections.unmodifiableSet((Set<? extends String>)DBMigrationRetryQueryQueue.tableNameNeedsFKTriggers.keySet());
    }
    
    public static List<String> getFKTriggerRequesterNames(final String tableName) {
        return DBMigrationRetryQueryQueue.tableNameNeedsFKTriggers.get(tableName);
    }
    
    public static void processedFKTriggerCreation(final String tableName) {
        DBMigrationRetryQueryQueue.tableNameNeedsFKTriggers.remove(tableName);
    }
    
    public static synchronized void servedFKTriggerRequest(final String requesterName) {
        Integer requestCount = DBMigrationRetryQueryQueue.fkTriggersRequesterVsRequestCount.get(requesterName);
        if (requestCount == 1) {
            DBMigrationRetryQueryQueue.LOGGER.info("Request served for " + requesterName);
            DBMigrationRetryQueryQueue.fkTriggersRequesterVsRequestCount.remove(requesterName);
        }
        else {
            --requestCount;
            DBMigrationRetryQueryQueue.LOGGER.info("Request served hence decremented request count to " + requestCount + " for " + requesterName);
            DBMigrationRetryQueryQueue.fkTriggersRequesterVsRequestCount.put(requesterName, requestCount);
        }
    }
    
    public static synchronized boolean hasAnyPendingFKTriggerRequest(final String requesterName) {
        DBMigrationRetryQueryQueue.LOGGER.fine(DBMigrationRetryQueryQueue.fkTriggersRequesterVsRequestCount.toString());
        return DBMigrationRetryQueryQueue.fkTriggersRequesterVsRequestCount.containsKey(requesterName);
    }
    
    public static Set<String> getTableNamesHasFKIndexRetryQuries() {
        return Collections.unmodifiableSet((Set<? extends String>)DBMigrationRetryQueryQueue.tableNameVsFKIdx.keySet());
    }
    
    public static List<IndexDefinition> getFKIndexes(final String tableName) {
        final List<IndexDefinition> list = DBMigrationRetryQueryQueue.tableNameVsFKIdx.get(tableName);
        return (list == null) ? Collections.EMPTY_LIST : list;
    }
    
    public static void addToFKIndexQueue(final String tableName, final IndexDefinition idxDef) {
        List<IndexDefinition> list = DBMigrationRetryQueryQueue.tableNameVsFKIdx.get(tableName);
        if (list != null) {
            list.add(idxDef);
        }
        else {
            list = new ArrayList<IndexDefinition>();
            list.add(idxDef);
            DBMigrationRetryQueryQueue.tableNameVsFKIdx.put(tableName, list);
        }
    }
    
    public static void addToFKRetryQuery(final String tableName, final ForeignKeyDefinition fkDef) {
        ++DBMigrationRetryQueryQueue.retryRequests;
        Queue<ForeignKeyDefinition> retryQueue = DBMigrationRetryQueryQueue.retryFK.get(tableName);
        if (retryQueue != null) {
            retryQueue.add(fkDef);
        }
        else {
            retryQueue = new ConcurrentLinkedQueue<ForeignKeyDefinition>();
            retryQueue.add(fkDef);
            DBMigrationRetryQueryQueue.retryFK.put(tableName, retryQueue);
        }
    }
    
    public static Map<String, Queue<ForeignKeyDefinition>> getRetryFKQueue() {
        return DBMigrationRetryQueryQueue.retryFK;
    }
    
    public static int getTotalRetryFKCount() {
        return DBMigrationRetryQueryQueue.retryRequests;
    }
    
    static {
        LOGGER = Logger.getLogger(DBMigrationRetryQueryQueue.class.getName());
        DBMigrationRetryQueryQueue.tableNameNeedsFKTriggers = null;
        DBMigrationRetryQueryQueue.tableNameVsFKIdx = null;
        DBMigrationRetryQueryQueue.fkTriggersRequesterVsRequestCount = null;
        DBMigrationRetryQueryQueue.retryFK = null;
        DBMigrationRetryQueryQueue.retryRequests = 0;
    }
}
