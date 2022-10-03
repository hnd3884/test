package com.me.devicemanagement.onpremise.server.queue;

import java.util.List;
import java.util.Collection;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FilenameUtils;
import com.me.devicemanagement.framework.server.queue.DCQueueHelper;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.Set;
import redis.clients.jedis.Jedis;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.TreeMap;
import java.util.Collections;
import java.util.HashSet;
import com.me.devicemanagement.onpremise.server.redis.RedisErrorTracker;
import java.io.File;
import com.adventnet.sym.logging.LoggingThreadLocal;
import java.io.Reader;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.BulkQDataProcessor;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.redis.RedisServerUtil;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import com.me.devicemanagement.framework.server.queue.DCQueueMetaData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueConstants;
import com.me.devicemanagement.framework.server.queue.DCQueue;

public class RedisQueue extends Thread implements DCQueue, DCQueueConstants
{
    private final Integer qTableInAccess;
    private final Integer taskQListInAccess;
    private final Integer priorityQMemorySize;
    private final Integer processQSize;
    private final Integer maxMemoryQSize;
    public final String qErrorLoggerDelimiter = "||";
    private final String cacheNameSeparator = "_";
    private final Long bulkLoadTimeout;
    private Integer maxBulkSize;
    private final String readerContentKeyword = "READER_CONTENT";
    private final String fileWrittenKeyword = "FILE_WRITTEN";
    Logger logger;
    Logger qErrorLogger;
    Logger redisLogger;
    private DCQueueMetaData qMetaData;
    private String qFolderPath;
    private String redisExecutionDataSet;
    private String redisFilenameSet;
    public String agentDataLocation;
    public String redisAgentDataFilenameSet;
    private String redisInputQueueName;
    private String redisInputPriorityQueueName;
    private String redisProcessingQueueName;
    public String queueName;
    public String queueTable;
    public String queueExtnTable;
    private String priorityQRefTable;
    private String sourceClass;
    private int queueState;
    private ThreadPoolExecutor executor;
    private boolean qLoadingInProgress;
    private boolean isQueueInitialized;
    private ArrayBlockingQueue suspendedTaskQueue;
    
    public RedisQueue(final DCQueueMetaData qMetaData) {
        this.qTableInAccess = new Integer(1);
        this.taskQListInAccess = new Integer(1);
        this.priorityQMemorySize = new Integer(50);
        this.processQSize = new Integer(300);
        this.maxMemoryQSize = new Integer(200);
        this.bulkLoadTimeout = 10000L;
        this.maxBulkSize = RedisServerUtil.DEFAULT_REDIS_MAX_BULK_SIZE;
        this.logger = null;
        this.qErrorLogger = null;
        this.redisLogger = null;
        this.qMetaData = null;
        this.qFolderPath = null;
        this.redisExecutionDataSet = null;
        this.redisFilenameSet = null;
        this.agentDataLocation = null;
        this.redisAgentDataFilenameSet = null;
        this.redisInputQueueName = null;
        this.redisInputPriorityQueueName = null;
        this.redisProcessingQueueName = null;
        this.queueName = null;
        this.queueTable = null;
        this.queueExtnTable = null;
        this.priorityQRefTable = null;
        this.sourceClass = "RedisQueue";
        this.queueState = 100;
        this.qLoadingInProgress = false;
        this.isQueueInitialized = false;
        this.suspendedTaskQueue = null;
        this.qMetaData = qMetaData;
        this.sourceClass = "DefaultDCQueue:" + qMetaData.queueName;
        this.logger = Logger.getLogger(qMetaData.loggerName);
        this.redisLogger = Logger.getLogger("RedisLogger");
        this.qErrorLogger = Logger.getLogger(qMetaData.qErrorLoggerName);
        final String sourceMethod = "DefaultDCQueue";
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("----------------------------------------------------------").append("\n");
        logBuilder.append("----------------------CREATING QUEUE----------------------").append("\n");
        logBuilder.append("----------------------------------------------------------").append("\n");
        logBuilder.append("Creating Queue for given meta data: ").append(qMetaData).append("\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
        logBuilder = new StringBuilder();
        this.redisExecutionDataSet = "EXECUTION_" + qMetaData.queueName;
        this.redisAgentDataFilenameSet = "FILENAME_" + qMetaData.queueName;
        this.agentDataLocation = "AGENTFILELOCATION_" + qMetaData.queueName;
        this.redisFilenameSet = "FILENAMEQ_" + qMetaData.queueName;
        this.redisInputQueueName = "INPUT_" + qMetaData.queueName;
        this.redisInputPriorityQueueName = "PRIORITY_" + qMetaData.queueName;
        this.redisProcessingQueueName = "PROCESS_" + qMetaData.queueName;
        this.queueName = qMetaData.queueName;
        this.queueTable = qMetaData.queueTableName;
        this.queueExtnTable = qMetaData.queueExtnTableName;
        this.priorityQRefTable = qMetaData.priorityQRefTableName;
        this.qFolderPath = this.createQueueFolder();
        this.executor = this.createThreadPool();
        final int recCntInDB = this.getQueueDataCount(3);
        logBuilder.append("Number of records found in REDIS  is: ").append(recCntInDB).append(" for the Queue: ").append(qMetaData.queueName).append("\n");
        logBuilder.append("----------------------CREATING QUEUE----------------------").append("\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
    }
    
    @Override
    public void start() {
        final String sourceMethod = "start";
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("----------------------------------------------------------").append("\n");
        logBuilder.append("----------------------STARTING QUEUE----------------------").append("\n");
        logBuilder.append("----------------------------------------------------------").append("\n");
        logBuilder.append("start() being invoked for the queue: ").append(this.qMetaData).append("\n");
        try {
            if (!this.isQueueInitialized) {
                logBuilder.append("Going to initialize the Queue: ").append(this.qMetaData.queueName).append("\n");
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
                logBuilder = new StringBuilder();
                this.resetQDataStateInRedis();
                this.isQueueInitialized = true;
                this.printQueueStats();
                logBuilder.append("Initialize the Queue: ").append(this.qMetaData.queueName).append(" isQueueInitialized=").append(this.isQueueInitialized).append("\n");
                if (this.qMetaData.isBulkProcessor) {
                    final BulkProcessQueue bulkQueue = new BulkProcessQueue();
                    bulkQueue.start();
                }
                else {
                    final ProcessQueue processQueueToMemory = new ProcessQueue();
                    processQueueToMemory.start();
                }
                logBuilder.append("Started processing queue for :" + this.qMetaData.queueName);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while initializing the queue: " + this.qMetaData.queueName, (Throwable)ex);
        }
        super.start();
        logBuilder.append("Queue has been started. Queue meta data: ").append(this.qMetaData).append("\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
    }
    
    public void processQueueData(final DCQueueDataProcessor proc) {
        this.executor.execute((Runnable)proc);
    }
    
    public void processQueueData(final BulkQDataProcessor proc) {
        this.executor.execute((Runnable)proc);
    }
    
    public String getQueueFolderPath() throws Exception {
        return this.qFolderPath;
    }
    
    public void addToQueue(final DCQueueData qData) throws Exception {
        this.addToQueue(qData, null, null);
    }
    
    public void addToQueue(final DCQueueData qData, final String qContent) throws Exception {
        this.addToQueue(qData, null, qContent);
    }
    
    public void addToQueue(final DCQueueData qData, final Reader reader) throws Exception {
        this.addToQueue(qData, reader, null);
    }
    
    private void addToQueue(final DCQueueData qData, final Reader reader, final String qContent) throws Exception {
        final String sourceMethod = "addToQueue";
        final int timeThresholdForQAddition = 5;
        qData.loggingId = LoggingThreadLocal.getLoggingId();
        qData.isRedis = true;
        StringBuilder logBuilder = new StringBuilder();
        final StringBuilder logBuilder2 = new StringBuilder();
        logBuilder.append("-------------------------------------------------------------------------").append("\n");
        logBuilder.append("Start of addToQueue() with file name: ").append(qData.fileName).append("\n");
        logBuilder.append("-------------------------------------------------------------------------").append("\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
        logBuilder = new StringBuilder();
        SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Start of addToQueue() with qData: {0}", new Object[] { qData });
        boolean fileWritten = false;
        boolean isRedisPushed = false;
        String filePath = null;
        try {
            final Long startTime = System.currentTimeMillis();
            final Long startTimeFile = System.currentTimeMillis();
            filePath = this.qFolderPath + File.separator + qData.fileName;
            fileWritten = this.writeQDataInFile(qData, filePath, reader, qContent);
            if (!fileWritten) {
                return;
            }
            final Long endTimeFile = System.currentTimeMillis();
            final Long totalFileTime = (endTimeFile - startTimeFile) / 1000L;
            final Long startTimeDB = System.currentTimeMillis();
            if (this.qLoadingInProgress || !this.isQueueInitialized || this.queueState == 101) {
                isRedisPushed = this.writeQDataToRedisQ(qData, this.redisInputQueueName);
            }
            else {
                final Long qDataCntInDB = (Long)this.getQueueDataCount(2);
                final long qDataCntInMemory = this.getPendingTaskCount();
                isRedisPushed = this.addDataToQueue(qData, qDataCntInDB, qDataCntInMemory, qData.priority);
            }
            final Long endTimeDB = System.currentTimeMillis();
            final Long totalDBTime = (endTimeDB - startTimeDB) / 1000L;
            final Long endTime = System.currentTimeMillis();
            final Long totalTimeinSec = (endTime - startTime) / 1000L;
            if (totalTimeinSec >= timeThresholdForQAddition) {
                QueueDataMETracking.getInstance();
                QueueDataMETracking.updateCount(this.queueName, totalTimeinSec, totalFileTime, totalDBTime);
            }
            logBuilder2.append("TIME-FILE :").append(totalFileTime).append("\t");
            logBuilder2.append("TIME-QUEUE :").append(totalDBTime).append("\t");
            logBuilder2.append("TOTAL TIME TAKEN :").append(totalTimeinSec).append("\t");
            logBuilder2.append("FILE NAME").append(qData.fileName);
            SyMLogger.info(this.redisLogger, this.sourceClass, sourceMethod, logBuilder2.toString());
        }
        catch (final Exception ex) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while adding data to queue.", (Throwable)ex);
                RedisErrorTracker.logRedisErrors(ex);
            }
        }
        finally {
            SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "File Written status: " + fileWritten + " Redis written status: " + isRedisPushed + " for file name: " + qData.fileName);
            final Boolean isShutdownTriggered2 = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered2) {
                QueueDataMETracking.updateRedisQueueDetails(this.queueName, isRedisPushed);
            }
        }
        logBuilder.append("-------------------------------------------------------------------------").append("\n");
        logBuilder.append("End of addToQueue() with file name: ").append(qData.fileName).append("\n");
        logBuilder.append("-------------------------------------------------------------------------").append("\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
    }
    
    private void resetQDataStateInRedis() {
        final String sourceMethod = "resetQDataStateInDB";
        Jedis jedis = null;
        Set<String> uncompletedQdata = new HashSet<String>();
        int failureCounter = 0;
        final Map<String, Long> map = new TreeMap<String, Long>(Collections.reverseOrder());
        final Map<String, Long> priorityMap = new TreeMap<String, Long>(Collections.reverseOrder());
        try {
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Start of resetQDataStateInRedis()...");
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            uncompletedQdata = jedis.hkeys(this.redisExecutionDataSet);
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "UNCOMPLETED QUEUE DATA  :" + uncompletedQdata.toString());
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "UNCOMPLETED QUEUE DATA SIZE FOR QUEUE :" + this.queueName + "IS :" + uncompletedQdata.size());
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Queue statistics before :");
            this.printQueueStats();
            final JSONParser parser = new JSONParser();
            for (final String qEntry : uncompletedQdata) {
                try {
                    final String content = jedis.hget(this.redisFilenameSet, qEntry);
                    if (content == null) {
                        continue;
                    }
                    final JSONObject data = (JSONObject)parser.parse(content);
                    final JSONObject queueDetailsObj = (JSONObject)data.get((Object)"MainQueue");
                    final Long time = (Long)queueDetailsObj.get((Object)"POST_TIME");
                    if (queueDetailsObj.get((Object)"IS_PRIORITY")) {
                        priorityMap.put(qEntry, time);
                    }
                    else {
                        map.put(qEntry, time);
                    }
                }
                catch (final Exception e) {
                    ++failureCounter;
                    SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while adding data from execution set to Queue", (Throwable)e);
                }
            }
            Set set = map.entrySet();
            for (final Map.Entry mapEntry : set) {
                jedis.rpush(this.redisProcessingQueueName, new String[] { mapEntry.getKey() });
            }
            set = priorityMap.entrySet();
            for (final Map.Entry mapEntry : set) {
                jedis.rpush(this.redisInputPriorityQueueName, new String[] { mapEntry.getKey() });
            }
            if (uncompletedQdata.size() > 0) {
                final Long status = jedis.del(this.redisExecutionDataSet);
                SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Deletion status of execution queue: " + status);
            }
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Queue statistics after :");
            this.printQueueStats();
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "End of resetQDataStateInRedis()...");
        }
        catch (final Exception e2) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while reseting qdata in memory to queue", (Throwable)e2);
                RedisErrorTracker.logRedisErrors(e2);
            }
        }
        finally {
            if (failureCounter > 0) {
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Failure Count while resetting data :" + failureCounter);
                QueueDataMETracking.updateResetFailureDetails(this.queueName, failureCounter);
            }
            uncompletedQdata = jedis.hkeys(this.redisExecutionDataSet);
            if (uncompletedQdata.size() > 0) {
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Still buffer has not been cleared due to some unexpected problem.So going to clear now");
                jedis.del(this.redisExecutionDataSet);
                uncompletedQdata = jedis.hkeys(this.redisExecutionDataSet);
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "SIZE AFTER DELETION :" + uncompletedQdata.size());
            }
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    
    private boolean addDataToQueue(final DCQueueData qData, final long qDataCntInInputQ, final long qDataCntInMemory, final boolean isPriority) {
        boolean redisWritten = false;
        try {
            final long memorySizeCheck = this.getMemorySize(isPriority);
            this.qErrorLogger.log(Level.INFO, "Memory Size :" + memorySizeCheck);
            if (qDataCntInInputQ == 0L && qDataCntInMemory < memorySizeCheck) {
                SyMLogger.info(this.qErrorLogger, this.sourceClass, "addDataToQueue", "directly to memory");
                if (this.qMetaData.isBulkProcessor) {
                    redisWritten = this.loadAndProcessDataBulk(qData);
                }
                else {
                    redisWritten = this.loadAndProcessData(qData);
                }
            }
            else {
                SyMLogger.info(this.qErrorLogger, this.sourceClass, "addDataToQueue", "adding to redis");
                redisWritten = this.writeQDataToRedisQ(qData, this.redisInputQueueName);
            }
        }
        catch (final Exception e) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, "addDataToQueue", "Exception in addDataToQueue : ", (Throwable)e);
                RedisErrorTracker.logRedisErrors(e);
            }
        }
        return redisWritten;
    }
    
    private long getMemorySize(final boolean isPriority) {
        long memorySize = 1L;
        if (!this.qMetaData.isBulkProcessor) {
            memorySize = (isPriority ? (this.qMetaData.qMaxSize + this.priorityQMemorySize) : this.qMetaData.qMaxSize);
        }
        return memorySize;
    }
    
    private boolean loadAndProcessDataBulk(final DCQueueData qData) {
        final String sourceMethod = "loadAndProcessData";
        try {
            final ArrayList<DCQueueData> qDataList = new ArrayList<DCQueueData>();
            qDataList.add(qData);
            final BulkQDataProcessor proc = (BulkQDataProcessor)Class.forName(this.qMetaData.processorClassName).newInstance();
            proc.qHelper = (DCQueueHelper)Class.forName(this.qMetaData.qHelperClassName).getConstructor(DCQueueMetaData.class).newInstance(this.qMetaData);
            proc.qMetaData = this.qMetaData;
            proc.logger = this.logger;
            proc.qErrorLogger = this.qErrorLogger;
            proc.queueFolderName = this.qFolderPath;
            proc.queueName = this.qMetaData.queueName;
            proc.qDataList = qDataList;
            proc.sleepBetweenProcess = this.qMetaData.delayBetweenProcessing;
            proc.queueTable = this.queueTable;
            proc.priorityQRefTable = this.priorityQRefTable;
            proc.queueExtnTable = this.queueExtnTable;
            this.writeQDataStateInRedis(qData);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Directly adding to the memory queue: " + qData.fileName);
            this.processQueueData(proc);
        }
        catch (final Exception e) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception in loadAndProcessData : ", (Throwable)e);
                RedisErrorTracker.logRedisErrors(e);
            }
        }
        return true;
    }
    
    private boolean loadAndProcessData(final DCQueueData qData) {
        final String sourceMethod = "loadAndProcessData";
        try {
            final DCQueueDataProcessor proc = (DCQueueDataProcessor)Class.forName(this.qMetaData.processorClassName).newInstance();
            proc.qHelper = (DCQueueHelper)Class.forName(this.qMetaData.qHelperClassName).getConstructor(DCQueueMetaData.class).newInstance(this.qMetaData);
            proc.qMetaData = this.qMetaData;
            proc.logger = this.logger;
            proc.qErrorLogger = this.qErrorLogger;
            proc.queueFolderName = this.qFolderPath;
            proc.queueName = this.qMetaData.queueName;
            proc.qData = qData;
            proc.sleepBetweenProcess = this.qMetaData.delayBetweenProcessing;
            proc.queueTable = this.queueTable;
            proc.priorityQRefTable = this.priorityQRefTable;
            proc.queueExtnTable = this.queueExtnTable;
            this.writeQDataStateInRedis(qData);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Directly adding to the memory queue: " + qData.fileName);
            this.processQueueData(proc);
        }
        catch (final Exception e) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception in loadAndProcessData : ", (Throwable)e);
                RedisErrorTracker.logRedisErrors(e);
            }
        }
        return true;
    }
    
    private void writeQDataStateInRedis(final DCQueueData qData) {
        final String sourceMethod = "writeQDataStateInRedis";
        Jedis jedis = null;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            if (jedis != null) {
                final String formQData = this.formQueueData(qData, jedis);
                if (!formQData.equalsIgnoreCase("Error")) {
                    jedis.hset(this.redisFilenameSet, qData.fileName, formQData);
                    jedis.hset(this.redisExecutionDataSet, qData.fileName, String.valueOf(qData.priority));
                }
            }
        }
        catch (final Exception e) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "Exception while adding data to execution queue " + qData.fileName);
                RedisErrorTracker.logRedisErrors(e);
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    
    private String formQueueData(final DCQueueData qData, final Jedis jedis) {
        final String sourceMethod = "formQueueData";
        final boolean isOverwritten = false;
        final JSONObject queueObj = new JSONObject();
        final JSONObject queueDetailsObj = new JSONObject();
        final JSONObject queueExtnDetailsObj = new JSONObject();
        final JSONObject queuePriorityDetailsObj = new JSONObject();
        final StringBuilder logBuilder = new StringBuilder();
        try {
            logBuilder.append("Start of formQueueData with file name: ").append(qData.fileName).append(" with overwrite: ").append(qData.overwriteFile).append("\n");
            if (qData.overwriteFile) {
                final String isKeyExists = jedis.hget(this.redisFilenameSet, qData.fileName);
                if (isKeyExists == null) {
                    logBuilder.append("Key does not exists for the file :" + qData.fileName).append("\n");
                }
                else {
                    logBuilder.append("Overwriting file :" + qData.fileName).append("\n");
                }
            }
            queueDetailsObj.put((Object)"POST_TIME", (Object)new Long(qData.postTime));
            queueDetailsObj.put((Object)"DATA_FILE_NAME", (Object)qData.fileName);
            queueDetailsObj.put((Object)"QUEUE_DATA_TYPE", (Object)new Integer(qData.queueDataType));
            queueDetailsObj.put((Object)"REQUEST_ID", (Object)qData.loggingId);
            queueDetailsObj.put((Object)"IS_PRIORITY", (Object)qData.priority);
            Long rid = null;
            if (qData.queueExtnTableData != null && this.queueExtnTable != null) {
                final Map<String, Object> relationTableColumns = qData.queueExtnTableData;
                queueExtnDetailsObj.putAll((Map)relationTableColumns);
                queueObj.put((Object)this.queueExtnTable, (Object)queueExtnDetailsObj);
                rid = relationTableColumns.get("RESOURCE_ID");
                try {
                    jedis.hset(this.queueExtnTable, qData.fileName, queueExtnDetailsObj.toString());
                }
                catch (final Exception e) {
                    throw e;
                }
            }
            if (qData.priorityQRefTableData != null && this.priorityQRefTable != null) {
                final Map<String, Object> relationTableColumns = qData.priorityQRefTableData;
                queuePriorityDetailsObj.putAll((Map)relationTableColumns);
                queueObj.put((Object)this.qMetaData.priorityQRefTableName, (Object)queuePriorityDetailsObj);
                rid = relationTableColumns.get("REFERENCE_ID");
            }
            if (rid != null) {
                final String keyName = this.queueTable + "QREF_ID" + rid;
                try {
                    jedis.lpush(keyName, new String[] { qData.fileName });
                }
                catch (final Exception e) {
                    throw e;
                }
            }
            queueObj.put((Object)"MainQueue", (Object)queueDetailsObj);
            logBuilder.append("End of formQueueData for filename : ").append(qData.fileName).append(" isOverwritten: ").append(isOverwritten).append("\n");
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
        }
        catch (final Exception ex) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while writing Extn,Priority Details data if present and  forming Q data for the : " + qData, (Throwable)ex);
                RedisErrorTracker.logRedisErrors(ex);
                return "Error";
            }
        }
        return queueObj.toString();
    }
    
    private boolean writeQDataToRedisQ(final DCQueueData qData, final String queueName) {
        final String sourceMethod = "writeQDataToRedisQ";
        final boolean isOverwritten = false;
        boolean isDataAdded = false;
        final StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("Start of push to queue for filename: ").append(qData.fileName).append("\n");
        Jedis jedis = null;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            if (jedis != null) {
                final String formQData = this.formQueueData(qData, jedis);
                if (!formQData.equalsIgnoreCase("Error")) {
                    if (qData.priority) {
                        jedis.hset(this.redisFilenameSet, qData.fileName, formQData);
                        jedis.lpush(this.redisInputPriorityQueueName, new String[] { qData.fileName });
                        isDataAdded = true;
                    }
                    else {
                        jedis.hset(this.redisFilenameSet, qData.fileName, formQData);
                        jedis.lpush(queueName, new String[] { qData.fileName });
                        isDataAdded = true;
                    }
                }
            }
        }
        catch (final Exception ex) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception in push to queue for data : " + qData, (Throwable)ex);
                RedisErrorTracker.logRedisErrors(ex);
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        logBuilder.append("End of push to queue for filename : ").append(qData.fileName).append(" isOverwritten: ").append(isOverwritten).append(" isPriority: ").append(qData.priority).append("\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
        this.printQueueStats();
        QueueDataMETracking.incrementTrackingMap(this.qMetaData.queueName);
        return isDataAdded;
    }
    
    private boolean writeDataInFile(final String filePath, final Reader reader) {
        final String sourceMethod = "writeDataInFile";
        Jedis jedis = null;
        final String fileName = FilenameUtils.getName(filePath);
        boolean fileWrittenStatus = false;
        try {
            final long redis_max_memory = RedisServerUtil.redisMaxMemory;
            final int maxFileLength = RedisServerUtil.redisMaxFileLength;
            final String readerContent = IOUtils.toString(reader);
            final int fileLength = readerContent.length();
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            if (jedis != null) {
                final long used_memory = this.getRedisUsedMemory(jedis);
                if (RedisServerUtil.isRedisFileWriteEnabled && used_memory < redis_max_memory && fileLength < maxFileLength) {
                    fileWrittenStatus = this.writeDataInRedis(fileName, readerContent, jedis);
                    jedis.hset(this.agentDataLocation, fileName, "redis");
                }
                else {
                    fileWrittenStatus = this.writeDataInDisk(filePath, readerContent);
                    jedis.hset(this.agentDataLocation, fileName, "file");
                }
            }
        }
        catch (final Exception e) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while writing agent data..: " + fileName, (Throwable)e);
                RedisErrorTracker.logRedisErrors(e);
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return fileWrittenStatus;
    }
    
    private boolean writeDataInFile(final String filePath, final String qContent) {
        final String sourceMethod = "writeDataInFile";
        Jedis jedis = null;
        final String fileName = FilenameUtils.getName(filePath);
        boolean fileWrittenStatus = false;
        try {
            final long redis_max_memory = RedisServerUtil.redisMaxMemory;
            final int maxFileLength = RedisServerUtil.redisMaxFileLength;
            final int fileLength = qContent.length();
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            if (jedis != null) {
                final long used_memory = this.getRedisUsedMemory(jedis);
                if (RedisServerUtil.isRedisFileWriteEnabled && used_memory < redis_max_memory && fileLength < maxFileLength) {
                    fileWrittenStatus = this.writeDataInRedis(fileName, qContent, jedis);
                    jedis.hset(this.agentDataLocation, fileName, "redis");
                }
                else {
                    fileWrittenStatus = this.writeDataInDisk(filePath, qContent);
                    jedis.hset(this.agentDataLocation, fileName, "file");
                }
            }
        }
        catch (final Exception e) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while writing Agent data .. " + fileName, (Throwable)e);
                RedisErrorTracker.logRedisErrors(e);
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return fileWrittenStatus;
    }
    
    private boolean writeDataInRedis(final String fileName, final String qContent, final Jedis jedis) {
        final String sourceMethod = "writeDataInRedis";
        final StringBuilder logBuilder = new StringBuilder();
        try {
            if (jedis != null) {
                logBuilder.append("Start of writeDataInRedis: ").append(fileName).append("\n");
                jedis.hset(this.redisAgentDataFilenameSet, fileName, qContent);
                logBuilder.append("End of writeDataInRedis: ").append(fileName).append("\n");
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
                return true;
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while writing the data in Redis ", (Throwable)ex);
        }
        return false;
    }
    
    private boolean writeDataInDisk(final String filePath, final String qContent) {
        final String sourceMethod = "writeDataInFile";
        BufferedWriter bw = null;
        final StringBuilder logBuilder = new StringBuilder();
        try {
            logBuilder.append("Start of writeDataInFile: ").append(filePath).append("\n");
            bw = new BufferedWriter(new FileWriter(filePath));
            bw.write(qContent);
            bw.close();
            logBuilder.append("End of writeDataInFile: ").append(filePath).append("\n");
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
            return true;
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while writing the data in file ", (Throwable)ex);
            if (bw != null) {
                try {
                    bw.close();
                }
                catch (final Exception ex) {
                    SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while closing BufferedWriter.. ", (Throwable)ex);
                }
            }
        }
        finally {
            if (bw != null) {
                try {
                    bw.close();
                }
                catch (final Exception ex2) {
                    SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while closing BufferedWriter.. ", (Throwable)ex2);
                }
            }
        }
        return false;
    }
    
    private boolean writeQDataInFile(final DCQueueData qData, final String filePath, final Reader reader, final String qContent) {
        final String sourceMethod = "checkAndWriteQDataInFile";
        boolean fileWritten = false;
        final StringBuilder logBuilder = new StringBuilder();
        try {
            if (this.qMetaData.retainQDataInMemory) {
                if (reader != null) {
                    fileWritten = this.writeDataInFile(filePath, reader);
                    qData.queueData = IOUtils.toString(reader);
                }
                else if (qContent != null) {
                    fileWritten = this.writeDataInFile(filePath, qContent);
                    qData.queueData = qContent;
                }
                else {
                    fileWritten = this.writeDataInFile(filePath, qData.queueData.toString());
                }
            }
            else {
                fileWritten = ((reader != null) ? this.writeDataInFile(filePath, reader) : this.writeDataInFile(filePath, qContent));
            }
            logBuilder.append("File Written result :").append(fileWritten).append(" for file: ").append(filePath).append("\n");
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
            if (!fileWritten) {
                logBuilder.append("Unable to write the file: ").append(filePath).append("\n");
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
                return false;
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while writting Queue data in file", (Throwable)e);
        }
        return fileWritten;
    }
    
    public long getQueueDataCount(final String queueName) {
        Jedis jedis = null;
        long count = -1L;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            count = jedis.llen(queueName);
        }
        catch (final Exception e) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                this.redisLogger.log(Level.WARNING, "Exception while getting queue count", e);
                RedisErrorTracker.logRedisErrors(e);
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return count;
    }
    
    public int getQueueDataCount(final int qState) {
        final String sourceMethod = "getQueueDataCount";
        long qSize = 0L;
        Jedis jedis = null;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            if (jedis != null) {
                switch (qState) {
                    case 2: {
                        qSize = jedis.llen(this.redisInputQueueName) + jedis.llen(this.redisProcessingQueueName) + jedis.llen(this.redisInputPriorityQueueName);
                        break;
                    }
                    case 1: {
                        qSize = jedis.hkeys(this.redisExecutionDataSet).size();
                        break;
                    }
                    case 3: {
                        qSize = jedis.hkeys(this.redisFilenameSet).size();
                        break;
                    }
                }
            }
        }
        catch (final Exception e) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while getting queue data count :", (Throwable)e);
                RedisErrorTracker.logRedisErrors(e);
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return (int)qSize;
    }
    
    private void printQueueStats() {
        Jedis jedis = null;
        final String sourceMethod = "printQueueStats";
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, this.redisInputQueueName + "\t :" + jedis.llen(this.redisInputQueueName));
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, this.redisInputPriorityQueueName + "\t :" + jedis.llen(this.redisInputPriorityQueueName));
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, this.redisProcessingQueueName + "\t :" + jedis.llen(this.redisProcessingQueueName));
        }
        catch (final Exception e) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "Exception while print queue stats : " + e);
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    
    private String createQueueFolder() {
        final String sourceMethod = "createQueueFolder";
        this.qFolderPath = DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "dc-queue";
        this.qFolderPath = this.qFolderPath + File.separator + this.qMetaData.queueName;
        try {
            this.logger.info("Creating folder for Queue: " + this.qFolderPath);
            new File(this.qFolderPath).mkdirs();
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while creating folder for the queue ", (Throwable)ex);
        }
        return this.qFolderPath;
    }
    
    private long getPendingTaskCount() {
        return this.executor.getQueue().size();
    }
    
    private ThreadPoolExecutor createThreadPool() {
        if (this.executor == null) {
            this.logger.info("Creating new thread pool for Queue: " + this.qMetaData.queueName);
            if (this.qMetaData.isBulkProcessor) {
                this.logger.info("Creating new FIXED thread pool for Queue for bulk processing: " + this.qMetaData.queueName);
                this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(1);
            }
            else {
                this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>((int)this.qMetaData.qMinSize, (Comparator<? super Runnable>)new Comparator<DCQueueDataProcessor>() {
                    @Override
                    public int compare(final DCQueueDataProcessor p1, final DCQueueDataProcessor p2) {
                        return (p1.qData.priority == p2.qData.priority) ? Long.valueOf(p1.qData.postTime).compareTo(p2.qData.postTime) : (p1.qData.priority ? -1 : 1);
                    }
                }));
            }
            this.executor.setCorePoolSize(this.qMetaData.processThreadCount);
            this.executor.setMaximumPoolSize(this.qMetaData.processThreadMaxCount);
            this.executor.setKeepAliveTime(this.qMetaData.keepAliveTimeout, TimeUnit.SECONDS);
            this.executor.allowCoreThreadTimeOut(this.qMetaData.timeoutAllThreads);
        }
        return this.executor;
    }
    
    private BulkQDataProcessor getQueueDataProcessorRow(final ArrayList<String> qdata) throws Exception {
        final String sourceMethod = "getQueueDataProcessorRow";
        try {
            final BulkQDataProcessor proc = (BulkQDataProcessor)Class.forName(this.qMetaData.processorClassName).newInstance();
            proc.qHelper = (DCQueueHelper)Class.forName(this.qMetaData.qHelperClassName).getConstructor(DCQueueMetaData.class).newInstance(this.qMetaData);
            proc.qMetaData = this.qMetaData;
            proc.logger = this.logger;
            proc.qErrorLogger = this.qErrorLogger;
            proc.queueFolderName = this.qFolderPath;
            proc.queueName = this.qMetaData.queueName;
            proc.qDataList = this.getBulkQueueDataToLoad(qdata);
            proc.sleepBetweenProcess = this.qMetaData.delayBetweenProcessing;
            proc.queueTable = this.queueTable;
            proc.priorityQRefTable = this.priorityQRefTable;
            proc.queueExtnTable = this.queueExtnTable;
            proc.isRedis = true;
            return proc;
        }
        catch (final Exception e) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception in getQueueDataProcessorRow : ", (Throwable)e);
            }
            throw e;
        }
    }
    
    private DCQueueDataProcessor getQueueDataProcessorRow(final String messages) throws Exception {
        final String sourceMethod = "getQueueDataProcessorRow";
        try {
            final DCQueueDataProcessor proc = (DCQueueDataProcessor)Class.forName(this.qMetaData.processorClassName).newInstance();
            proc.qHelper = (DCQueueHelper)Class.forName(this.qMetaData.qHelperClassName).getConstructor(DCQueueMetaData.class).newInstance(this.qMetaData);
            proc.qMetaData = this.qMetaData;
            proc.logger = this.logger;
            proc.qErrorLogger = this.qErrorLogger;
            proc.queueFolderName = this.qFolderPath;
            proc.queueName = this.qMetaData.queueName;
            proc.qData = this.getDCQueueDataToLoad(messages);
            proc.sleepBetweenProcess = this.qMetaData.delayBetweenProcessing;
            proc.queueTable = this.queueTable;
            proc.priorityQRefTable = this.priorityQRefTable;
            proc.queueExtnTable = this.queueExtnTable;
            return proc;
        }
        catch (final Exception e) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception in getQueueDataProcessorRow : ", (Throwable)e);
            }
            throw e;
        }
    }
    
    private ArrayList<DCQueueData> getBulkQueueDataToLoad(final ArrayList<String> qData) throws Exception {
        final ArrayList<DCQueueData> queueDataList = new ArrayList<DCQueueData>();
        for (final String qDataEntry : qData) {
            try {
                final DCQueueData qDataArray = this.getDCQueueDataToLoad(qDataEntry);
                queueDataList.add(qDataArray);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return queueDataList;
    }
    
    private DCQueueData getDCQueueDataToLoad(final String queueData) throws Exception {
        try {
            final JSONParser parser = new JSONParser();
            final JSONObject qDataJson = (JSONObject)parser.parse(queueData);
            final JSONObject mainQueue = (JSONObject)qDataJson.get((Object)"MainQueue");
            final JSONObject mainQueueExtn = qDataJson.containsKey((Object)this.qMetaData.queueExtnTableName) ? ((JSONObject)qDataJson.get((Object)this.qMetaData.queueExtnTableName)) : null;
            final JSONObject mainQueuePriority = qDataJson.containsKey((Object)this.qMetaData.priorityQRefTableName) ? ((JSONObject)qDataJson.get((Object)this.qMetaData.priorityQRefTableName)) : null;
            final DCQueueData qData = new DCQueueData();
            if (mainQueue != null) {
                qData.isRedis = true;
                qData.fileName = (String)mainQueue.get((Object)"DATA_FILE_NAME");
                qData.postTime = (long)mainQueue.get((Object)"POST_TIME");
                qData.queueDataType = ((Long)mainQueue.get((Object)"QUEUE_DATA_TYPE")).intValue();
                qData.queueData = null;
                qData.loggingId = (String)mainQueue.get((Object)"REQUEST_ID");
                qData.priority = (boolean)mainQueue.get((Object)"IS_PRIORITY");
                if (mainQueueExtn != null) {
                    qData.queueExtnTableData = this.getAdditionalTableData(mainQueueExtn, this.queueExtnTable);
                }
                if (mainQueuePriority != null) {
                    qData.priorityQRefTableData = this.getAdditionalTableData(mainQueuePriority, this.priorityQRefTable);
                }
            }
            return qData;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    private Map<String, Object> getAdditionalTableData(final JSONObject jsonInput, final String tableName) {
        final String sourceMethod = "getAdditionalTableData";
        Map<String, Object> tableMap = null;
        if (tableName != null) {
            try {
                tableMap = new Hashtable<String, Object>();
                for (final String key : jsonInput.keySet()) {
                    final Object value = jsonInput.get((Object)key);
                    tableMap.put(key, value);
                }
                return tableMap;
            }
            catch (final Exception e) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while fetching data for additional queue table : " + tableName, (Throwable)e);
            }
        }
        return null;
    }
    
    public boolean isQueueSuspended() {
        return this.queueState == 101;
    }
    
    public void suspendQExecution() throws Exception {
        final String sourceMethod = "suspendQExecution";
        final StringBuilder logBuilder = new StringBuilder();
        try {
            if (this.queueState == 101) {
                SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "Cannot suspend Queue. Already suspended: " + this.qMetaData.queueName);
                throw new SyMException(1002, "Cannot suspend Queue. Already suspended: " + this.qMetaData.queueName, (Throwable)null);
            }
            this.queueState = 101;
            logBuilder.append("=============================================================").append("\n");
            logBuilder.append("Queue is Suspended: ").append(this.qMetaData.queueName).append("\n");
            logBuilder.append("=============================================================").append("\n");
            synchronized (this.taskQListInAccess) {
                final BlockingQueue bq = this.executor.getQueue();
                logBuilder.append("Queue size before it is getting suspended: ").append(bq.size()).append(" for queue: ").append(this.qMetaData.queueName).append("\n");
                final int bqsize = bq.size();
                if (bqsize > 0) {
                    bq.drainTo(this.suspendedTaskQueue = new ArrayBlockingQueue(bqsize));
                }
                logBuilder.append("Queue size after it is getting suspended: ").append(bq.size()).append(" for queue: ").append(this.qMetaData.queueName).append("\n");
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
            }
        }
        catch (final Exception ex) {
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Exception occurred while suspending the Queue: " + this.qMetaData.queueName);
            throw ex;
        }
    }
    
    public void resumeQExecution() throws Exception {
        final String sourceMethod = "resumeQExecution";
        StringBuilder logBuilder = new StringBuilder();
        try {
            if (this.queueState == 100) {
                SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "Cannot resume Queue Execution. Already active: " + this.qMetaData.queueName);
                throw new SyMException(1002, "Cannot resume Queue Execution. Already active: " + this.qMetaData.queueName, (Throwable)null);
            }
            synchronized (this.taskQListInAccess) {
                if (this.suspendedTaskQueue != null) {
                    final BlockingQueue tpQ = this.executor.getQueue();
                    logBuilder.append("Queue size before it is getting resumed: ").append(tpQ.size()).append(" for queue: ").append(this.qMetaData.queueName).append("\n");
                    this.suspendedTaskQueue.drainTo(tpQ);
                    logBuilder.append("Queue size after it is getting resumed: ").append(tpQ.size()).append(" for queue: ").append(this.qMetaData.queueName).append("\n");
                    SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
                    logBuilder = new StringBuilder();
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "Exception occurred while resuming the Queue: " + this.qMetaData.queueName);
        }
        finally {
            if (this.suspendedTaskQueue != null) {
                this.suspendedTaskQueue.clear();
                this.suspendedTaskQueue = null;
            }
            this.queueState = 100;
            logBuilder.append("=============================================================").append("\n");
            logBuilder.append("Queue is Resumed: ").append(this.qMetaData.queueName).append("\n");
            logBuilder.append("=============================================================").append("\n");
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
        }
    }
    
    public void shutdownQueue() {
        final String sourceMethod = "shutdownQueue";
        final StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("Queue is going to be shutdown: ").append(this.qMetaData.queueName).append("\n");
        if (this.executor != null) {
            final List pendingTasks = this.executor.shutdownNow();
            logBuilder.append("Pending tasks while shutdown the Queue: ").append(this.qMetaData.queueName).append(" are: ").append(pendingTasks).append("\n");
        }
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
    }
    
    public void pushToProcessQ() throws Exception {
        final String sourceMethod = "pushToProcessQ";
        Jedis jedis = null;
        int total = 0;
        try {
            jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Pushing from input queue to Process Queue for the queue:" + this.qMetaData.queueName);
            this.printQueueStats();
            for (int i = 0; i < this.processQSize && jedis.llen(this.redisInputQueueName) > 0L; ++i) {
                jedis.rpoplpush(this.redisInputQueueName, this.redisProcessingQueueName);
                total = i;
            }
            this.printQueueStats();
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "No of entries pushed from input to process Queue :" + (total + 1));
        }
        catch (final Exception ex) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while pushing data from input to process queue", (Throwable)ex);
                RedisErrorTracker.logRedisErrors(ex);
                throw ex;
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    
    public void loadDataFromTaskQtoProcessQ(final long qDataCntInMemory) throws Exception {
        final String sourceMethod = "loadDataFromTaskQtoProcessQ";
        try {
            final StringBuilder logBuilder = new StringBuilder();
            if (qDataCntInMemory <= this.qMetaData.qMinSize) {
                final Long qDataCntInInputQ = this.getQueueDataCount(this.redisInputQueueName);
                if (qDataCntInInputQ > 0L) {
                    logBuilder.append("Before Queue data loaded from DB: qDataCntInMemory= ").append(qDataCntInMemory).append(" qDataCntInDB=").append(qDataCntInInputQ).append("\n");
                    this.pushToProcessQ();
                    final long qDataCntInMemoryAfterLoading = this.getPendingTaskCount();
                    final int qDataCntInInputQAfterLoading = this.getQueueDataCount(2);
                    logBuilder.append("-------------------------------------------------------------------------").append("\n");
                    logBuilder.append("After Queue data loaded from DB: qDataCntInMemory= ").append(qDataCntInMemoryAfterLoading).append(" qDataCntInDB=").append(qDataCntInInputQAfterLoading).append("\n");
                    logBuilder.append("-------------------------------------------------------------------------").append("\n");
                    SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
                }
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception in checkAndLoadNormalData : ", (Throwable)e);
            throw e;
        }
    }
    
    private boolean logQCountInMemory(final long qDataCntInMemory, boolean queueCountLoggedForZeroCount) {
        final String sourceMethod = "logQCountInMemory";
        final StringBuilder logBuilder = new StringBuilder();
        if (qDataCntInMemory == 0L && !queueCountLoggedForZeroCount) {
            logBuilder.append("QDataCntInMemory= ").append(qDataCntInMemory).append("\n");
            logBuilder.append("Queue is EMPTY now...").append("\n");
            queueCountLoggedForZeroCount = true;
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
        }
        else if (qDataCntInMemory > 0L) {
            logBuilder.append("QDataCntInMemory= ").append(qDataCntInMemory).append("\n");
            queueCountLoggedForZeroCount = false;
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
        }
        return queueCountLoggedForZeroCount;
    }
    
    @Override
    public void run() {
        final String sourceMethod = "run";
        final StringBuilder logBuilder = new StringBuilder();
        try {
            boolean queueCountLoggedForZeroCount = false;
        Label_0014_Outer:
            while (true) {
                while (true) {
                    try {
                        while (true) {
                            if (this.queueState == 101) {
                                Thread.currentThread();
                                Thread.sleep(this.qMetaData.sleepBeweenQueueSizeCheck);
                            }
                            else {
                                final long qDataCntInMemory = this.getPendingTaskCount();
                                queueCountLoggedForZeroCount = this.logQCountInMemory(qDataCntInMemory, queueCountLoggedForZeroCount);
                                this.loadDataFromTaskQtoProcessQ(qDataCntInMemory);
                                Thread.currentThread();
                                Thread.sleep(this.qMetaData.sleepBeweenQueueSizeCheck);
                            }
                        }
                    }
                    catch (final Exception ex) {
                        SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while loading data to queue.", (Throwable)ex);
                        continue Label_0014_Outer;
                    }
                    continue;
                }
            }
        }
        catch (final Exception ex2) {
            final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
            if (!isShutdownTriggered) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while processing the queue with meta data: " + this.qMetaData + ". This is critical as the entire queue processing will get affected.", (Throwable)ex2);
                RedisErrorTracker.logRedisErrors(ex2);
            }
        }
    }
    
    private long getRedisUsedMemory(final Jedis jedis) {
        final String memory_info = jedis.info("memory");
        final String[] splitedWords = memory_info.split("\n");
        final String[] splitedWords2 = splitedWords[1].split(":");
        final String[] splitedWords3 = splitedWords2[1].split("\r");
        final long used_memory = Long.parseLong(splitedWords3[0]);
        return used_memory;
    }
    
    class BulkProcessQueue extends Thread
    {
        public ArrayList<String> getBulkList(final ArrayList<String> list, final Jedis jedis) {
            final ArrayList<String> totalList = new ArrayList<String>();
            for (final String fName : list) {
                final String queueData = jedis.hget(RedisQueue.this.redisFilenameSet, fName);
                RedisQueue.this.redisLogger.log(Level.FINE, "Messages received: " + queueData);
                if (queueData != null) {
                    jedis.hset(RedisQueue.this.redisExecutionDataSet, fName, "true");
                    totalList.add(queueData);
                }
                else {
                    final String cacheNameRedis = RedisQueue.this.queueTable + "_" + "PROCESSED_PRIORITY_QUEUE_REDIS";
                    final List<String> processedPriorityQListRedis = (List<String>)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheNameRedis);
                    if (processedPriorityQListRedis == null || !processedPriorityQListRedis.contains(fName)) {
                        continue;
                    }
                    processedPriorityQListRedis.remove(fName);
                    ApiFactoryProvider.getCacheAccessAPI().putCache(cacheNameRedis, (Object)processedPriorityQListRedis);
                    RedisQueue.this.logger.log(Level.INFO, "Data already processed while processing Priority data" + fName);
                }
            }
            return totalList;
        }
        
        @Override
        public void run() {
            final StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("BulkProcessQueue will be started and going to wait for processing " + RedisQueue.this.redisProcessingQueueName).append("\n");
            Jedis jedis = null;
            try {
                jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
                final int i = 0;
                RedisQueue.this.printQueueStats();
                while (true) {
                    RedisQueue.this.maxBulkSize = RedisServerUtil.getMaxBulkSize();
                    try {
                        final ArrayList<String> priorityQList = new ArrayList<String>();
                        final ArrayList<String> qList = new ArrayList<String>();
                        final ArrayList<String> totalList = new ArrayList<String>();
                        int listCount = 0;
                        if (RedisQueue.this.queueState == 101) {
                            Thread.currentThread();
                            Thread.sleep(RedisQueue.this.qMetaData.sleepBeweenQueueSizeCheck);
                        }
                        else {
                            final int recCntInDB = RedisQueue.this.getQueueDataCount(3);
                            RedisQueue.this.redisLogger.log(Level.FINE, "Total count in queue :" + recCntInDB);
                            final Long startTime = System.currentTimeMillis();
                            while (listCount != RedisQueue.this.maxBulkSize && System.currentTimeMillis() - startTime < RedisQueue.this.bulkLoadTimeout) {
                                while (jedis.llen(RedisQueue.this.redisInputPriorityQueueName) > 0L && listCount != RedisQueue.this.maxBulkSize) {
                                    final String filename = jedis.rpop(RedisQueue.this.redisInputPriorityQueueName);
                                    if (filename != null) {
                                        priorityQList.add(filename);
                                        ++listCount;
                                    }
                                }
                                while (listCount != RedisQueue.this.maxBulkSize) {
                                    final List<String> messages = jedis.brpop(5, RedisQueue.this.redisProcessingQueueName);
                                    if (messages != null) {
                                        final String filename = messages.get(1);
                                        if (filename != null) {
                                            qList.add(filename);
                                            ++listCount;
                                        }
                                    }
                                    if (System.currentTimeMillis() - startTime >= RedisQueue.this.bulkLoadTimeout) {
                                        break;
                                    }
                                }
                            }
                            totalList.addAll(this.getBulkList(priorityQList, jedis));
                            totalList.addAll(this.getBulkList(qList, jedis));
                            if (totalList.size() <= 0) {
                                continue;
                            }
                            RedisQueue.this.processQueueData(RedisQueue.this.getQueueDataProcessorRow(totalList));
                            RedisQueue.this.redisLogger.log(Level.INFO, "Total List :" + totalList);
                        }
                    }
                    catch (final Exception e) {
                        final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
                        if (isShutdownTriggered) {
                            continue;
                        }
                        RedisQueue.this.redisLogger.log(Level.INFO, "Exception while bulk processing", e);
                        RedisErrorTracker.logRedisErrors(e);
                    }
                }
            }
            catch (final Exception ex) {
                RedisQueue.this.logger.log(Level.WARNING, "Caught exception while processing data: ", ex);
            }
        }
    }
    
    class ProcessQueue extends Thread
    {
        @Override
        public void run() {
            final String sourceMethod = "ProcessQueue-run";
            final StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("ProcessQueue will be started and going to wait for processing " + RedisQueue.this.redisProcessingQueueName).append("\n");
            Jedis jedis = null;
            try {
                jedis = ApiFactoryProvider.getRedisQueueAPI().getJedis();
                final int i = 0;
                RedisQueue.this.printQueueStats();
            Label_0067_Outer:
                while (true) {
                    while (true) {
                        try {
                            while (true) {
                                if (RedisQueue.this.queueState == 101) {
                                    Thread.currentThread();
                                    Thread.sleep(RedisQueue.this.qMetaData.sleepBeweenQueueSizeCheck);
                                }
                                else {
                                    final long qDataCntInMemory = RedisQueue.this.getQueueDataCount(1);
                                    final Boolean isShutdownTriggered = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
                                    if (qDataCntInMemory >= RedisQueue.this.maxMemoryQSize || isShutdownTriggered) {
                                        continue Label_0067_Outer;
                                    }
                                    List<String> messages = null;
                                    String filename = null;
                                    logBuilder.setLength(0);
                                    boolean isPriority = false;
                                    if (jedis.llen(RedisQueue.this.redisInputPriorityQueueName) > 0L) {
                                        filename = jedis.rpop(RedisQueue.this.redisInputPriorityQueueName);
                                        isPriority = true;
                                    }
                                    else {
                                        messages = jedis.brpop(5, RedisQueue.this.redisProcessingQueueName);
                                        if (messages != null) {
                                            filename = messages.get(1);
                                        }
                                    }
                                    if (filename == null) {
                                        continue Label_0067_Outer;
                                    }
                                    RedisQueue.this.redisLogger.log(Level.INFO, "POPED from Queue: " + filename);
                                    final String queueData = jedis.hget(RedisQueue.this.redisFilenameSet, filename);
                                    RedisQueue.this.redisLogger.log(Level.FINE, "Messages received: " + queueData);
                                    if (queueData != null) {
                                        jedis.hset(RedisQueue.this.redisExecutionDataSet, filename, String.valueOf(isPriority));
                                        RedisQueue.this.processQueueData(RedisQueue.this.getQueueDataProcessorRow(queueData));
                                    }
                                    else {
                                        final String cacheNameRedis = RedisQueue.this.queueTable + "_" + "PROCESSED_PRIORITY_QUEUE_REDIS";
                                        final List<String> processedPriorityQListRedis = (List<String>)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheNameRedis);
                                        if (processedPriorityQListRedis != null && processedPriorityQListRedis.contains(filename)) {
                                            processedPriorityQListRedis.remove(filename);
                                            ApiFactoryProvider.getCacheAccessAPI().putCache(cacheNameRedis, (Object)processedPriorityQListRedis);
                                            logBuilder.append("Data already processed while processing Priority data");
                                        }
                                    }
                                    SyMLogger.info(RedisQueue.this.qErrorLogger, RedisQueue.this.sourceClass, sourceMethod, logBuilder.toString());
                                    SyMLogger.info(RedisQueue.this.logger, RedisQueue.this.sourceClass, sourceMethod, logBuilder.toString());
                                    RedisQueue.this.printQueueStats();
                                }
                            }
                        }
                        catch (final Exception e) {
                            final Boolean isShutdownTriggered2 = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
                            if (!isShutdownTriggered2) {
                                SyMLogger.error(RedisQueue.this.logger, RedisQueue.this.sourceClass, sourceMethod, "Exception while poping queue elements from process queue - " + RedisQueue.this.redisProcessingQueueName, (Throwable)e);
                                RedisErrorTracker.logRedisErrors(e);
                            }
                            continue Label_0067_Outer;
                        }
                        continue;
                    }
                }
            }
            catch (final Exception e2) {
                final Boolean isShutdownTriggered3 = Boolean.valueOf(System.getProperty("isRedisShutdownTriggered"));
                if (!isShutdownTriggered3) {
                    SyMLogger.error(RedisQueue.this.logger, RedisQueue.this.sourceClass, sourceMethod, "Exception while processing queue thread for " + RedisQueue.this.qMetaData.queueName, (Throwable)e2);
                    RedisErrorTracker.logRedisErrors(e2);
                }
            }
            finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }
}
