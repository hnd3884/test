package com.me.devicemanagement.framework.server.queue;

import com.adventnet.sym.logging.LoggingThreadLocal;
import java.io.File;
import java.util.Map;
import redis.clients.jedis.Jedis;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.Hashtable;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Collection;
import com.me.devicemanagement.framework.server.redis.RedisServerUtil;
import java.util.ArrayList;
import java.util.logging.Logger;

public abstract class BulkQDataProcessor implements Runnable, DCQueueConstants
{
    public Logger logger;
    public Logger qErrorLogger;
    public Long sleepBetweenProcess;
    public String queueName;
    public Boolean isRedis;
    public String queueFolderName;
    public ArrayList<DCQueueData> qDataList;
    public ArrayList<DCQueueData> qUpdatedDataList;
    public ArrayList<DCQueueData> qProcessDataList;
    public String queueTable;
    public String priorityQRefTable;
    public String queueExtnTable;
    public DCQueueHelper qHelper;
    public DCQueueMetaData qMetaData;
    
    public BulkQDataProcessor() {
        this.logger = null;
        this.qErrorLogger = null;
        this.sleepBetweenProcess = 0L;
        this.queueName = null;
        this.isRedis = true;
        this.queueFolderName = null;
        this.qDataList = null;
        this.qUpdatedDataList = null;
        this.qProcessDataList = null;
        this.queueTable = null;
        this.priorityQRefTable = null;
        this.queueExtnTable = null;
        this.qHelper = null;
        this.qMetaData = null;
    }
    
    @Override
    public void run() {
        try {
            final int maxBulkSize = RedisServerUtil.maxBulkSize;
            while (this.qDataList.size() > 0) {
                int count = 0;
                this.qUpdatedDataList = new ArrayList<DCQueueData>();
                this.qProcessDataList = new ArrayList<DCQueueData>();
                final ArrayList<DCQueueData> tempQDataList = new ArrayList<DCQueueData>(this.qDataList);
                final List<Long> refIDList = new ArrayList<Long>();
                for (final DCQueueData qData : tempQDataList) {
                    boolean needToProcessQData = false;
                    if (count >= maxBulkSize) {
                        break;
                    }
                    final long refID = qData.priorityQRefTableData.get("REFERENCE_ID");
                    if (refIDList.contains(refID)) {
                        continue;
                    }
                    refIDList.add(refID);
                    if (qData.priority) {
                        final String keyName = this.queueTable + "QREF_ID" + refID;
                        Jedis jedis = null;
                        final String fileName = qData.fileName;
                        try {
                            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
                            final String redisFileNameSet = "FILENAMEQ_" + this.queueName;
                            while (jedis.llen(keyName) > 0L) {
                                final String fileNameToProcess = jedis.rpop(keyName);
                                if (fileNameToProcess == null || fileNameToProcess.equalsIgnoreCase(fileName)) {
                                    needToProcessQData = false;
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
                                    final String cacheName = this.queueTable + "_" + "PROCESSED_PRIORITY_QUEUE_REDIS";
                                    List<String> processedPriorityQList = (List<String>)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName);
                                    if (processedPriorityQList == null) {
                                        processedPriorityQList = new ArrayList<String>();
                                    }
                                    processedPriorityQList.add(fileNameToProcess);
                                    ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, processedPriorityQList);
                                }
                                if (mainQueueExtn != null) {
                                    final Map<String, Object> tableMap = new Hashtable<String, Object>();
                                    for (final String key : mainQueueExtn.keySet()) {
                                        final Object value = mainQueueExtn.get((Object)key);
                                        tableMap.put(key, value);
                                    }
                                    qDataFromRedis.queueExtnTableData = tableMap;
                                }
                                this.qUpdatedDataList.add(qDataFromRedis);
                                if (++count >= maxBulkSize) {
                                    needToProcessQData = true;
                                    break;
                                }
                            }
                        }
                        catch (final Exception e) {
                            this.logger.log(Level.WARNING, "Exception while finding queue data from same computer before processing priority queue data", e);
                        }
                        finally {
                            if (jedis != null) {
                                jedis.close();
                            }
                        }
                    }
                    if (needToProcessQData) {
                        continue;
                    }
                    this.qUpdatedDataList.add(qData);
                    this.qDataList.remove(qData);
                    ++count;
                }
                this.logger.log(Level.FINE, "Update QData List :" + this.qUpdatedDataList);
                this.processQData(this.qUpdatedDataList);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught exception while processing data: " + this.qDataList, ex);
        }
    }
    
    public abstract void processData(final ArrayList<DCQueueData> p0);
    
    public void processQData(final ArrayList<DCQueueData> qDataListForProcessing) {
        try {
            for (final DCQueueData qDataForProcessing : qDataListForProcessing) {
                final String filePath = this.queueFolderName + File.separator + qDataForProcessing.fileName;
                this.logger.log(Level.INFO, "Processing started for: " + filePath);
                boolean fileReadErrorOccurred = false;
                if (qDataForProcessing.queueData == null) {
                    this.logger.log(Level.INFO, "Data is not set in DCQData. Going to read from file: " + filePath);
                    String dataStr = null;
                    try {
                        dataStr = this.qHelper.readFile(filePath);
                    }
                    catch (final Exception ex) {
                        fileReadErrorOccurred = true;
                        final String cacheName = this.queueTable + "_" + "PROCESSED_PRIORITY_QUEUE";
                        final String cacheNameRedis = this.queueTable + "_" + "PROCESSED_PRIORITY_QUEUE_REDIS";
                        final List<Long> processedPriorityQList = (List<Long>)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName);
                        final List<Long> processedPriorityQListRedis = (List<Long>)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheNameRedis);
                        if (!qDataForProcessing.isRedis && processedPriorityQList != null && processedPriorityQList.contains(qDataForProcessing.queueDataId)) {
                            processedPriorityQList.remove(qDataForProcessing.queueDataId);
                            ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, processedPriorityQList);
                            this.logger.log(Level.INFO, "Data already processed while processing Priority data");
                        }
                        else if (qDataForProcessing.isRedis && processedPriorityQListRedis != null && processedPriorityQListRedis.contains(qDataForProcessing.fileName)) {
                            processedPriorityQListRedis.remove(qDataForProcessing.fileName);
                            ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, processedPriorityQListRedis);
                            this.logger.log(Level.INFO, "Data already processed while processing Priority data");
                        }
                        else {
                            final File checkFile = new File(filePath);
                            if (!checkFile.exists()) {
                                this.logger.log(Level.WARNING, "Unable to read data from file: " + filePath + " File does not exist. Going to delete the DB entry.", ex);
                                this.qHelper.deleteDBEntry(qDataForProcessing, Boolean.FALSE, this.qMetaData);
                                this.qErrorLogger.log(Level.INFO, "File deletion failed ==> Q : " + this.queueName + "||" + "File Exists : " + Boolean.FALSE + "||" + "File Name : " + qDataForProcessing.fileName);
                            }
                            else {
                                this.logger.log(Level.WARNING, "Unable to read data from file: " + filePath + " But the file exists.", ex);
                                this.qErrorLogger.log(Level.INFO, "File deletion failed ==> Q : " + this.queueName + "||" + "File Locked : " + Boolean.TRUE + "||" + "File Name : " + qDataForProcessing.fileName);
                            }
                        }
                    }
                    qDataForProcessing.queueData = dataStr;
                }
                else if (qDataForProcessing.isCompressed) {
                    qDataForProcessing.queueData = this.qHelper.unCompressString(qDataForProcessing);
                }
                if (!fileReadErrorOccurred) {
                    this.qProcessDataList.add(qDataForProcessing);
                }
            }
            if (this.qProcessDataList.size() > 0) {
                final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
                if ((this.isRedis && !isShutdownTriggered) || !this.isRedis) {
                    this.logger.log(Level.FINE, "Is Redis Shutdown Triggered :" + isShutdownTriggered);
                    this.processData(this.qProcessDataList);
                    for (final DCQueueData qData : this.qProcessDataList) {
                        final String filePath2 = this.queueFolderName + File.separator + qData.fileName;
                        final boolean isFileDeleted = this.qHelper.deleteFile(filePath2);
                        this.qHelper.deleteDBEntry(qData, isFileDeleted, this.qMetaData);
                        this.logger.log(Level.INFO, "Processing completed for: " + qData.fileName);
                    }
                }
            }
            if (this.sleepBetweenProcess > 0L) {
                Thread.sleep(this.sleepBetweenProcess);
            }
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Caught exception while processing data: " + this.qDataList, ex2);
        }
        finally {
            LoggingThreadLocal.clearLoggingId();
        }
    }
}
