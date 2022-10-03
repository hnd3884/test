package com.me.devicemanagement.framework.server.queue;

import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import java.util.Date;
import java.text.DecimalFormat;
import java.io.File;
import com.adventnet.sym.logging.LoggingThreadLocal;
import java.util.Map;
import redis.clients.jedis.Jedis;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Hashtable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.List;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.text.DateFormat;
import java.util.logging.Logger;

public abstract class DCQueueDataProcessor implements Runnable, DCQueueConstants
{
    public String queueName;
    public String queueFolderName;
    public DCQueueData qData;
    public DCQueueHelper qHelper;
    public Logger logger;
    public Logger qErrorLogger;
    private static Logger dcQueueDataProcessorOneLineLogger;
    private static DateFormat simpleDateFormat;
    private static Logger queueLogger;
    public Long sleepBetweenProcess;
    public String queueTable;
    public String priorityQRefTable;
    public String queueExtnTable;
    public DCQueueMetaData qMetaData;
    private boolean isCompleted;
    
    public DCQueueDataProcessor() {
        this.queueName = null;
        this.queueFolderName = null;
        this.qData = null;
        this.qHelper = null;
        this.logger = null;
        this.qErrorLogger = null;
        this.sleepBetweenProcess = 0L;
        this.queueTable = null;
        this.priorityQRefTable = null;
        this.queueExtnTable = null;
        this.qMetaData = null;
        this.isCompleted = false;
    }
    
    public long getPartitionFeedId(final DCQueueData qData) {
        return -1L;
    }
    
    public boolean isParallelProcessingQueue() {
        return false;
    }
    
    @Override
    public void run() {
        try {
            final DCQueue queue = DCQueueHandler.getQueue(this.queueName);
            final boolean isEligibleForMonitor = queue.isQueueEligibleForMonitor();
            if (isEligibleForMonitor) {
                queue.monitorQueue(this);
            }
        }
        catch (final Exception e) {
            Logger.getLogger("SysStatusLogger").log(Level.SEVERE, "exception in starting timer in queue processing", e);
        }
        try {
            if (this.qData.priority) {
                if (!this.qData.isRedis) {
                    final Table baseTable = Table.getTable(this.queueTable);
                    final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
                    if (this.priorityQRefTable != null) {
                        final Table baserPriorityTable = Table.getTable(this.priorityQRefTable);
                        query.addJoin(new Join(baseTable, baserPriorityTable, new String[] { "QINFO_ID" }, new String[] { "QINFO_ID" }, 2));
                        Criteria criteria = new Criteria(Column.getColumn(this.priorityQRefTable, "REFERENCE_ID"), this.qData.priorityQRefTableData.get("REFERENCE_ID"), 0);
                        criteria = criteria.and(new Criteria(Column.getColumn(this.queueTable, "QINFO_ID"), (Object)this.qData.queueDataId, 7));
                        query.setCriteria(criteria);
                        query.addSelectColumn(Column.getColumn(this.queueTable, "QINFO_ID"));
                        query.addSelectColumn(Column.getColumn(this.queueTable, "DATA_FILE_NAME"));
                        query.addSelectColumn(Column.getColumn(this.queueTable, "POST_TIME"));
                        query.addSelectColumn(Column.getColumn(this.queueTable, "QUEUE_DATA_TYPE"));
                        query.addSelectColumn(Column.getColumn(this.queueTable, "REQUEST_ID"));
                        query.addSelectColumn(Column.getColumn(this.queueTable, "QUEUE_DATA_STATE"));
                        query.addSortColumn(new SortColumn(Column.getColumn(this.queueTable, "POST_TIME"), true));
                        final DataObject queueDO = SyMUtil.getPersistenceLite().get(query);
                        final Iterator rows = queueDO.getRows(this.queueTable);
                        while (rows.hasNext()) {
                            final Row row = rows.next();
                            final DCQueueData qDataFromDB = new DCQueueData();
                            qDataFromDB.queueDataId = (Long)row.get("QINFO_ID");
                            qDataFromDB.fileName = (String)row.get("DATA_FILE_NAME");
                            qDataFromDB.postTime = (long)row.get("POST_TIME");
                            qDataFromDB.queueDataType = (int)row.get("QUEUE_DATA_TYPE");
                            qDataFromDB.loggingId = (String)row.get("REQUEST_ID");
                            qDataFromDB.queueData = null;
                            this.processQData(qDataFromDB);
                            if ((int)row.get("QUEUE_DATA_STATE") == 1) {
                                final String cacheName = this.queueTable + "_" + "PROCESSED_PRIORITY_QUEUE";
                                List<Long> processedPriorityQList = (List<Long>)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName);
                                if (processedPriorityQList == null) {
                                    processedPriorityQList = new ArrayList<Long>();
                                }
                                processedPriorityQList.add(qDataFromDB.queueDataId);
                                ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, processedPriorityQList);
                            }
                        }
                    }
                }
                else {
                    Jedis jedis = null;
                    try {
                        final long refID = this.qData.priorityQRefTableData.get("REFERENCE_ID");
                        final String keyName = this.queueTable + "QREF_ID" + refID;
                        final String fileName = this.qData.fileName;
                        jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
                        final String redisFileNameSet = "FILENAMEQ_" + this.queueName;
                        while (jedis.llen(keyName) > 0L) {
                            final String fileNameToProcess = jedis.rpop(keyName);
                            if (fileNameToProcess == null || fileNameToProcess.equalsIgnoreCase(fileName)) {
                                break;
                            }
                            final String queueData = jedis.hget(redisFileNameSet, fileNameToProcess);
                            if (queueData == null) {
                                continue;
                            }
                            final JSONParser parser = new JSONParser();
                            final JSONObject qDataJson = (JSONObject)parser.parse(queueData);
                            final JSONObject mainQueue = (JSONObject)qDataJson.get((Object)"MainQueue");
                            final JSONObject mainQueueExtn = qDataJson.containsKey((Object)this.queueExtnTable) ? ((JSONObject)qDataJson.get((Object)this.queueExtnTable)) : null;
                            final DCQueueData qDataFromRedis = new DCQueueData();
                            if (mainQueue != null) {
                                qDataFromRedis.fileName = (String)mainQueue.get((Object)"DATA_FILE_NAME");
                                qDataFromRedis.postTime = (long)mainQueue.get((Object)"POST_TIME");
                                qDataFromRedis.queueDataType = ((Long)mainQueue.get((Object)"QUEUE_DATA_TYPE")).intValue();
                                qDataFromRedis.queueData = null;
                                qDataFromRedis.loggingId = (String)mainQueue.get((Object)"REQUEST_ID");
                                final String cacheName2 = this.queueTable + "_" + "PROCESSED_PRIORITY_QUEUE_REDIS";
                                List<String> processedPriorityQList2 = (List<String>)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName2);
                                if (processedPriorityQList2 == null) {
                                    processedPriorityQList2 = new ArrayList<String>();
                                }
                                processedPriorityQList2.add(fileNameToProcess);
                                ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName2, processedPriorityQList2);
                            }
                            if (mainQueueExtn != null) {
                                final Map<String, Object> tableMap = new Hashtable<String, Object>();
                                for (final String key : mainQueueExtn.keySet()) {
                                    final Object value = mainQueueExtn.get((Object)key);
                                    tableMap.put(key, value);
                                }
                                qDataFromRedis.queueExtnTableData = tableMap;
                            }
                            this.processQData(qDataFromRedis);
                        }
                    }
                    catch (final Exception e2) {
                        this.logger.log(Level.WARNING, "Exception in finding preceding data for same computer before processing priority queue data: ", e2);
                    }
                    finally {
                        if (jedis != null) {
                            jedis.close();
                        }
                    }
                }
            }
            this.processQData(this.qData);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in processing data {0} : {1}", new Object[] { this.qData, ex });
        }
        finally {
            this.isCompleted = true;
            Logger.getLogger("SysStatusLogger").log(Level.INFO, "Queue: {0} Data Processed successfully", this.queueName);
        }
    }
    
    public boolean preProcessQueueData(final DCQueueData qData) {
        return true;
    }
    
    public boolean initiateQRetry() {
        return false;
    }
    
    public abstract void processData(final DCQueueData p0);
    
    private boolean isServerLoggingID(final String loggingId) {
        try {
            if (loggingId != null) {
                if (loggingId.contains("SERVER") || loggingId.contains("server")) {
                    return true;
                }
                final char startChar = loggingId.charAt(0);
                for (int i = 0; i < 9; ++i) {
                    if (startChar == i) {
                        return true;
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in isServerLoggingID: ", ex);
        }
        return false;
    }
    
    public void processQData(final DCQueueData qDataForProcessing) {
        try {
            if (!this.isServerLoggingID(qDataForProcessing.loggingId)) {
                LoggingThreadLocal.setLoggingId(qDataForProcessing.loggingId);
            }
            final long startTime = System.currentTimeMillis();
            final String filePath = this.queueFolderName + File.separator + qDataForProcessing.fileName;
            this.logger.log(Level.INFO, "Processing started for: " + filePath);
            boolean fileReadErrorOccurred = false;
            if (qDataForProcessing.queueData == null) {
                this.logger.log(Level.INFO, "Data is not set in DCQData. Going to read from file: " + filePath);
                String dataStr = null;
                try {
                    if (this.maxFileSizeReached(filePath)) {
                        fileReadErrorOccurred = true;
                        final boolean isFileDeleted = this.qHelper.deleteFile(filePath);
                        this.qHelper.deleteDBEntry(qDataForProcessing, isFileDeleted, this.qMetaData);
                        this.logger.log(Level.INFO, "File reached the max size. Dropping the file : " + filePath);
                    }
                    else {
                        dataStr = this.qHelper.readFile(filePath);
                    }
                }
                catch (final Exception ex) {
                    fileReadErrorOccurred = true;
                    final String cacheName = this.queueTable + "_" + "PROCESSED_PRIORITY_QUEUE";
                    final String cacheNameRedis = this.queueTable + "_" + "PROCESSED_PRIORITY_QUEUE_REDIS";
                    final List<Long> processedPriorityQList = (List<Long>)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName);
                    final List<Long> processedPriorityQListRedis = (List<Long>)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheNameRedis);
                    if (!this.qData.isRedis && processedPriorityQList != null && processedPriorityQList.contains(qDataForProcessing.queueDataId)) {
                        processedPriorityQList.remove(qDataForProcessing.queueDataId);
                        ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, processedPriorityQList);
                        this.logger.log(Level.INFO, "Data already processed while processing Priority data");
                    }
                    else if (this.qData.isRedis && processedPriorityQListRedis != null && processedPriorityQListRedis.contains(qDataForProcessing.fileName)) {
                        processedPriorityQListRedis.remove(qDataForProcessing.fileName);
                        ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, processedPriorityQListRedis);
                        this.logger.log(Level.INFO, "Data already processed while processing Priority data");
                    }
                    else {
                        final File checkFile = new File(filePath);
                        if (!checkFile.exists()) {
                            this.logger.log(Level.WARNING, "Unable to read data from file: " + filePath + " File does not exist. Going to delete the DB entry.", ex);
                            this.qHelper.deleteDBEntry(qDataForProcessing, Boolean.FALSE, this.qMetaData);
                            this.qErrorLogger.log(Level.INFO, "File deletion failed ==> Q : " + this.queueName + "||" + "File Exists : " + Boolean.FALSE + "||" + "File Name : " + this.qData.fileName);
                        }
                        else {
                            this.logger.log(Level.WARNING, "Unable to read data from file: " + filePath + " But the file exists.", ex);
                            this.qErrorLogger.log(Level.INFO, "File deletion failed ==> Q : " + this.queueName + "||" + "File Locked : " + Boolean.TRUE + "||" + "File Name : " + this.qData.fileName);
                        }
                    }
                }
                qDataForProcessing.queueData = dataStr;
            }
            else if (qDataForProcessing.isCompressed) {
                qDataForProcessing.queueData = this.qHelper.unCompressString(qDataForProcessing);
            }
            if (!fileReadErrorOccurred) {
                final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
                if ((this.qData.isRedis && !isShutdownTriggered) || !this.qData.isRedis) {
                    this.logger.log(Level.FINE, "Is Redis Shutdown Triggered :" + isShutdownTriggered);
                    this.logger.log(Level.INFO, "File read success. Going to transfer processing to individual class");
                    if (!this.preProcessQueueData(qDataForProcessing) && this.initiateQRetry()) {
                        return;
                    }
                    this.processData(qDataForProcessing);
                    final boolean isFileDeleted = this.qHelper.deleteFile(filePath);
                    this.qHelper.deleteDBEntry(qDataForProcessing, isFileDeleted, this.qMetaData);
                }
                if (this.sleepBetweenProcess > 0L) {
                    Thread.sleep(this.sleepBetweenProcess);
                }
            }
            this.logger.log(Level.INFO, "Processing completed for: " + filePath);
            final long endTime = System.currentTimeMillis();
            final DecimalFormat df = new DecimalFormat("#.###");
            DCQueueDataProcessor.dcQueueDataProcessorOneLineLogger.log(Level.INFO, "||{0}||{1}||{2}||{3}||{4}||", new Object[] { String.format("%-30s", this.queueName), String.format("%-60s", qDataForProcessing.fileName), String.format("%-25s", DCQueueDataProcessor.simpleDateFormat.format(new Date(startTime))), String.format("%-25s", DCQueueDataProcessor.simpleDateFormat.format(new Date(endTime))), String.format("%-15s", df.format((endTime - startTime) / 1000.0)) });
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Exception in processing data {0} : {1}", new Object[] { this.qData, ex2 });
        }
        finally {
            LoggingThreadLocal.clearLoggingId();
        }
    }
    
    private boolean maxFileSizeReached(final String filePath) {
        try {
            if (new File(filePath).length() > this.getQueueFileSizeLimitInBytes()) {
                return Boolean.TRUE;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in checking file max size", ex);
        }
        return Boolean.FALSE;
    }
    
    private long getQueueFileSizeLimitInBytes() {
        final String sourceMethod = "getQueueFileSizeLimit";
        long queueFileSizeLimit = 52428800L;
        try {
            final Object queueFileSizeLimitFromCache = ApiFactoryProvider.getCacheAccessAPI().getCache("QUEUE_MAX_FILE_SIZE", 1);
            if (queueFileSizeLimitFromCache != null) {
                queueFileSizeLimit = (long)queueFileSizeLimitFromCache;
            }
            else {
                final String queueFileSizeLimitFromFile = (String)FrameworkConfigurations.getSpecificPropertyIfExists("Queue_Configurations", "filesize.limit", (Object)"50");
                if (queueFileSizeLimitFromFile != null) {
                    queueFileSizeLimit = Long.parseLong(queueFileSizeLimitFromFile) * 1048576L;
                }
                ApiFactoryProvider.getCacheAccessAPI().putCache("QUEUE_MAX_FILE_SIZE", queueFileSizeLimit, 1);
                DCQueueDataProcessor.queueLogger.log(Level.INFO, "Setting Queue max file size as " + queueFileSizeLimit);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getting queueFileSizeLimit", ex);
        }
        return queueFileSizeLimit;
    }
    
    public boolean isCompleted() {
        return this.isCompleted;
    }
    
    static {
        DCQueueDataProcessor.dcQueueDataProcessorOneLineLogger = Logger.getLogger("DCQueueDataProcessorOneLineLogger");
        DCQueueDataProcessor.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        DCQueueDataProcessor.queueLogger = Logger.getLogger("DCQueueLogger");
    }
}
