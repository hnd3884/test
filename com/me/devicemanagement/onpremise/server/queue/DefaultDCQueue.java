package com.me.devicemanagement.onpremise.server.queue;

import java.util.HashMap;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.devicemanagement.framework.utils.JsonUtils;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.io.FileOutputStream;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.Collection;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.queue.DCQueueHelper;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;
import java.util.Hashtable;
import java.io.File;
import com.adventnet.sym.logging.LoggingThreadLocal;
import java.io.Reader;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Map;
import com.me.devicemanagement.onpremise.server.entity.QueueTimerConfigurations;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import com.me.devicemanagement.framework.server.queue.DCQueueMetaData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueConstants;
import com.me.devicemanagement.framework.server.queue.DCQueue;

public class DefaultDCQueue extends Thread implements DCQueue, DCQueueConstants
{
    private final Integer qTableInAccess;
    private final Integer taskQListInAccess;
    private final Integer priorityQMemorySize;
    private final String readerContentKeyword = "READER_CONTENT";
    private final String fileWrittenKeyword = "FILE_WRITTEN";
    Logger logger;
    Logger qErrorLogger;
    private DCQueueMetaData qMetaData;
    private String qFolderPath;
    private String queueName;
    private String queueTable;
    private String queueExtnTable;
    private String priorityQRefTable;
    private String sourceClass;
    private int queueState;
    private ThreadPoolExecutor executor;
    private boolean qLoadingInProgress;
    private boolean isQueueInitialized;
    private ArrayBlockingQueue suspendedTaskQueue;
    private int blockedCount;
    private static QueueTimerConfigurations queueTimerConfigurations;
    private static final String THREAD_PROPERTIES_FILE_PATH;
    private static final Map<String, Integer> BLOCKED_QUEUE_SUMMARY;
    
    public DefaultDCQueue(final DCQueueMetaData qMetaData) {
        this.qTableInAccess = new Integer(1);
        this.taskQListInAccess = new Integer(1);
        this.priorityQMemorySize = new Integer(50);
        this.logger = null;
        this.qErrorLogger = null;
        this.qMetaData = null;
        this.qFolderPath = null;
        this.queueName = null;
        this.queueTable = null;
        this.queueExtnTable = null;
        this.priorityQRefTable = null;
        this.sourceClass = "DefaultDCQueue";
        this.queueState = 100;
        this.qLoadingInProgress = false;
        this.isQueueInitialized = false;
        this.suspendedTaskQueue = null;
        this.blockedCount = 0;
        this.qMetaData = qMetaData;
        this.sourceClass = "DefaultDCQueue:" + qMetaData.queueName;
        this.logger = Logger.getLogger(qMetaData.loggerName);
        this.qErrorLogger = Logger.getLogger(qMetaData.qErrorLoggerName);
        final String sourceMethod = "DefaultDCQueue";
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("----------------------------------------------------------").append("\n");
        logBuilder.append("----------------------CREATING QUEUE----------------------").append("\n");
        logBuilder.append("----------------------------------------------------------").append("\n");
        logBuilder.append("Creating Queue for given meta data: ").append(qMetaData).append("\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
        logBuilder = new StringBuilder();
        this.queueName = qMetaData.queueName;
        this.queueTable = qMetaData.queueTableName;
        this.queueExtnTable = qMetaData.queueExtnTableName;
        this.priorityQRefTable = qMetaData.priorityQRefTableName;
        this.qFolderPath = this.createQueueFolder();
        this.executor = this.createThreadPool();
        try {
            final int recCntInDB = this.getQueueDataCount(3);
            logBuilder.append("Number of records found in DB is: ").append(recCntInDB).append(" for the Queue: ").append(qMetaData.queueName).append("\n");
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while finding number of records exists in the DB for the Queue: " + qMetaData.queueName, (Throwable)ex);
        }
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
                this.resetQDataStateInDB();
                this.isQueueInitialized = true;
                final int recCntInDB = this.getQueueDataCount(3);
                logBuilder.append("Number of records found in DB is: ").append(recCntInDB).append(" for the Queue: ").append(this.qMetaData.queueName).append("\n");
                logBuilder.append("Initialize the Queue: ").append(this.qMetaData.queueName).append(" isQueueInitialized=").append(this.isQueueInitialized).append("\n");
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while initializing the queue: " + this.qMetaData.queueName, (Throwable)ex);
        }
        super.start();
        logBuilder.append("Queue has been started. Queue meta data: ").append(this.qMetaData).append("\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
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
        StringBuilder logBuilder = new StringBuilder();
        final StringBuilder logBuilder2 = new StringBuilder();
        logBuilder.append("-------------------------------------------------------------------------").append("\n");
        logBuilder.append("Start of addToQueue() with file name: ").append(qData.fileName).append("\n");
        logBuilder.append("-------------------------------------------------------------------------").append("\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
        logBuilder = new StringBuilder();
        SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Start of addToQueue() with qData: {0}", new Object[] { qData });
        boolean fileWritten = false;
        boolean dbWritten = false;
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
                dbWritten = this.writeQDataStateInDB(qData, 2);
            }
            else {
                final int qDataCntInDB = this.getQueueDataCount(2);
                final long qDataCntInMemory = this.getPendingTaskCount();
                dbWritten = this.addDataToQueue(qData, qDataCntInDB, qDataCntInMemory, qData.priority);
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
            SyMLogger.info(this.qErrorLogger, this.sourceClass, sourceMethod, logBuilder2.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while adding data to queue.", (Throwable)ex);
        }
        finally {
            SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "File Written status: " + fileWritten + " DB written status: " + dbWritten + " for file name: " + qData.fileName);
            QueueDataMETracking.checkAndUpdateDB();
        }
        logBuilder.append("-------------------------------------------------------------------------").append("\n");
        logBuilder.append("End of addToQueue() with file name: ").append(qData.fileName).append("\n");
        logBuilder.append("-------------------------------------------------------------------------").append("\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
    }
    
    private boolean writeQDataInFile(final DCQueueData qData, final String filePath, final Reader reader, final String qContent) {
        final String sourceMethod = "checkAndWriteQDataInFile";
        boolean fileWritten = false;
        final StringBuilder logBuilder = new StringBuilder();
        if (this.qMetaData.retainQDataInMemory) {
            if (reader != null) {
                final Hashtable hash = this.writeDataInFile(filePath, reader);
                fileWritten = Boolean.valueOf(hash.get("FILE_WRITTEN").toString());
                qData.queueData = hash.get("READER_CONTENT").toString();
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
            fileWritten = ((reader != null) ? Boolean.valueOf(this.writeDataInFile(filePath, reader).get("FILE_WRITTEN").toString()) : this.writeDataInFile(filePath, qContent));
        }
        logBuilder.append("File Written result :").append(fileWritten).append(" for file: ").append(filePath).append("\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
        if (!fileWritten) {
            logBuilder.append("Unable to write the file: ").append(filePath).append("\n");
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
            return false;
        }
        return fileWritten;
    }
    
    private boolean addDataToQueue(final DCQueueData qData, final int qDataCntInDB, final long qDataCntInMemory, final boolean isPriority) {
        boolean dbWritten = false;
        try {
            final long memorySizeCheck = isPriority ? (this.qMetaData.qMaxSize + this.priorityQMemorySize) : this.qMetaData.qMaxSize;
            if (qDataCntInDB == 0 && qDataCntInMemory < memorySizeCheck) {
                dbWritten = this.loadAndProcessData(qData);
            }
            else {
                dbWritten = this.writeQDataStateInDB(qData, 2);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addDataToQueue", "Exception in addDataToQueue : ", (Throwable)e);
        }
        return dbWritten;
    }
    
    private boolean loadAndProcessData(final DCQueueData qData) {
        final String sourceMethod = "loadAndProcessData";
        boolean dbWritten = false;
        try {
            final DCQueueDataProcessor proc = (DCQueueDataProcessor)Class.forName(this.qMetaData.processorClassName).newInstance();
            proc.qHelper = (DCQueueHelper)Class.forName(this.qMetaData.qHelperClassName).newInstance();
            proc.logger = this.logger;
            proc.qErrorLogger = this.qErrorLogger;
            proc.queueFolderName = this.qFolderPath;
            proc.queueName = this.qMetaData.queueName;
            proc.qData = qData;
            proc.sleepBetweenProcess = this.qMetaData.delayBetweenProcessing;
            proc.queueTable = this.queueTable;
            proc.priorityQRefTable = this.priorityQRefTable;
            proc.queueExtnTable = this.queueExtnTable;
            proc.qMetaData = this.qMetaData;
            dbWritten = this.writeQDataStateInDB(qData, 1);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Directly adding to the memory queue: " + qData.fileName);
            this.processQueueData(proc);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception in loadAndProcessData : ", (Throwable)e);
        }
        return dbWritten;
    }
    
    private void populateExtensionTableData(final DCQueueData dcQueueData) throws Exception {
        final String sourceMethod = "populateExtensionTableData";
        try {
            if (dcQueueData.queueExtnTableData != null && dcQueueData.queueDataId != null && dcQueueData.queueDataId != -1L && this.qMetaData.queueExtnTableName != null) {
                final Row relationRow = new Row(this.qMetaData.queueExtnTableName);
                relationRow.set("QINFO_ID", (Object)dcQueueData.queueDataId);
                final Map<String, Object> relationTableColumns = dcQueueData.queueExtnTableData;
                relationRow.setAll((Map)relationTableColumns);
                final DataObject relationDO = SyMUtil.getPersistenceLite().constructDataObject();
                relationDO.addRow(relationRow);
                SyMUtil.getPersistenceLite().add(relationDO);
            }
        }
        catch (final DataAccessException e) {
            SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "Exception occurred while populating extension table data in : " + this.qMetaData.queueExtnTableName);
            throw e;
        }
        catch (final Exception e2) {
            SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "Exception occurred while populating extension table data in : " + this.qMetaData.queueExtnTableName);
            throw e2;
        }
    }
    
    private void populatePriorityQRefTableData(final DCQueueData dcQueueData) throws Exception {
        final String sourceMethod = "populatePriorityTableData";
        try {
            if (dcQueueData.priorityQRefTableData != null && dcQueueData.queueDataId != null && dcQueueData.queueDataId != -1L && this.qMetaData.priorityQRefTableName != null) {
                final Row relationRow = new Row(this.qMetaData.priorityQRefTableName);
                relationRow.set("QINFO_ID", (Object)dcQueueData.queueDataId);
                final Map<String, Object> relationTableColumns = dcQueueData.priorityQRefTableData;
                relationRow.setAll((Map)relationTableColumns);
                final DataObject relationDO = SyMUtil.getPersistenceLite().constructDataObject();
                relationDO.addRow(relationRow);
                SyMUtil.getPersistenceLite().add(relationDO);
            }
        }
        catch (final DataAccessException e) {
            SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "Exception occurred while populating PriorityQRef table data in : " + this.qMetaData.priorityQRefTableName);
            throw e;
        }
        catch (final Exception e2) {
            SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "Exception occurred while populating PriorityQRef table data in : " + this.qMetaData.priorityQRefTableName);
            throw e2;
        }
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
    
    @Override
    public void run() {
        final String sourceMethod = "run";
        final StringBuilder logBuilder = new StringBuilder();
        try {
            boolean queueCountLoggedForZeroCount = false;
        Label_0013_Outer:
            while (true) {
                while (true) {
                    try {
                        while (true) {
                            if (this.queueState == 101) {
                                Thread.currentThread();
                                Thread.sleep(this.qMetaData.sleepBeweenQueueSizeCheck);
                            }
                            else {
                                long qDataCntInMemory = this.getPendingTaskCount();
                                queueCountLoggedForZeroCount = this.logQCountInMemory(qDataCntInMemory, queueCountLoggedForZeroCount);
                                qDataCntInMemory = this.checkAndLoadPriorityData(qDataCntInMemory);
                                this.checkAndLoadNormalData(qDataCntInMemory);
                                Thread.currentThread();
                                Thread.sleep(this.qMetaData.sleepBeweenQueueSizeCheck);
                            }
                        }
                    }
                    catch (final Exception ex) {
                        SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while loading data to queue.", (Throwable)ex);
                        continue Label_0013_Outer;
                    }
                    continue;
                }
            }
        }
        catch (final Exception ex2) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while processing the queue with meta data: " + this.qMetaData + ". This is critical as the entire queue processing will get affected.", (Throwable)ex2);
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
    
    private long checkAndLoadPriorityData(long qDataCntInMemory) {
        final String sourceMethod = "checkAndLoadPriorityData";
        try {
            final StringBuilder logBuilder = new StringBuilder();
            if (this.isPriorityDataPresent() && qDataCntInMemory <= this.qMetaData.qMaxSize + this.priorityQMemorySize / 2) {
                logBuilder.append("Before Priority Queue data loaded from DB: qDataCntInMemory= ").append(qDataCntInMemory).append("\n");
                synchronized (this.taskQListInAccess) {
                    this.loadQDataFromDB(this.priorityQMemorySize + this.qMetaData.qMaxSize - qDataCntInMemory, 2, true);
                }
                qDataCntInMemory = this.getPendingTaskCount();
                logBuilder.append("-------------------------------------------------------------------------").append("\n");
                logBuilder.append("After Priority Queue data loaded from DB: qDataCntInMemory= ").append(qDataCntInMemory).append("\n");
                logBuilder.append("-------------------------------------------------------------------------").append("\n");
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception in checkAndLoadPriorityData : ", (Throwable)e);
        }
        return qDataCntInMemory;
    }
    
    private void checkAndLoadNormalData(final long qDataCntInMemory) throws Exception {
        final String sourceMethod = "checkAndLoadNormalData";
        try {
            final StringBuilder logBuilder = new StringBuilder();
            if (qDataCntInMemory <= this.qMetaData.qMinSize) {
                final int qDataCntInDB = this.getQueueDataCount(2);
                if (qDataCntInDB > 0) {
                    logBuilder.append("Before Queue data loaded from DB: qDataCntInMemory= ").append(qDataCntInMemory).append(" qDataCntInDB=").append(qDataCntInDB).append("\n");
                    synchronized (this.taskQListInAccess) {
                        this.loadQDataFromDB(this.qMetaData.qMaxSize, 2, false);
                    }
                    final long qDataCntInMemoryAfterLoading = this.getPendingTaskCount();
                    final int qDataCntInDBAfterLoading = this.getQueueDataCount(2);
                    logBuilder.append("-------------------------------------------------------------------------").append("\n");
                    logBuilder.append("After Queue data loaded from DB: qDataCntInMemory= ").append(qDataCntInMemoryAfterLoading).append(" qDataCntInDB=").append(qDataCntInDBAfterLoading).append("\n");
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
    
    private void loadQDataFromDB(final long numOfRecords, final int queueDataState, final boolean priorityStatus) throws Exception {
        final String sourceMethod = "loadQDataFromDB";
        final StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("loadQDataFromDB invoked with numOfRecords=").append(numOfRecords).append(" queueDataState=").append(queueDataState).append("\n");
        if (this.queueState == 101) {
            logBuilder.append("loadQDataFromDB() returns without loading the records to the queue as the queue state is QUEUE_SUSPENDED").append("\n");
            return;
        }
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
        this.qLoadingInProgress = true;
        try {
            final SelectQuery query = this.getSelectQueryToLoadFromDB(numOfRecords, queueDataState, priorityStatus);
            synchronized (this.qTableInAccess) {
                DataObject queueDO = SyMUtil.getPersistenceLite().get(query);
                if (queueDO.isEmpty()) {
                    this.updatePriorityStatusInCache(false);
                }
                else {
                    final Iterator rows = queueDO.getRows(this.queueTable);
                    while (rows.hasNext()) {
                        final Row row = rows.next();
                        this.processQueueData(this.getQueueDataProcessorRow(queueDO, row));
                        SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Moved data from DB to Memory Q for file name: " + row.get("DATA_FILE_NAME"));
                        row.set("QUEUE_DATA_STATE", (Object)new Integer(1));
                        queueDO.updateRow(row);
                    }
                    queueDO = SyMUtil.getPersistenceLite().update(queueDO);
                    SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Going to synchronize IN_DB data count between Cache and DB");
                    this.updateDBQueueDataCountInCache(this.getQueueDataCountFromDB(2));
                    SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Updated Status for DB entries after moving the data into memory queue. Updated DO: {0}", new Object[] { queueDO });
                    this.printQueueStats();
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while moving entry from DB to memory Queue : ", (Throwable)ex);
            throw ex;
        }
        finally {
            this.qLoadingInProgress = false;
        }
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "End of loadQDataFromDB() with numOfRecords=" + numOfRecords + " queueDataState=" + queueDataState);
    }
    
    private SelectQuery getSelectQueryToLoadFromDB(final long numOfRecords, final int queueDataState, final boolean priorityStatus) throws Exception {
        final String sourceMethod = "getSelectQueryToLoadFromDB";
        try {
            final Table baseTable = Table.getTable(this.queueTable);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
            if (this.queueExtnTable != null) {
                final Table baseExtnTable = Table.getTable(this.queueExtnTable);
                query.addJoin(new Join(baseTable, baseExtnTable, new String[] { "QINFO_ID" }, new String[] { "QINFO_ID" }, 1));
                this.addAllColumns(query, this.queueExtnTable);
            }
            if (this.priorityQRefTable != null) {
                final Table basePriorityTable = Table.getTable(this.priorityQRefTable);
                query.addJoin(new Join(baseTable, basePriorityTable, new String[] { "QINFO_ID" }, new String[] { "QINFO_ID" }, 1));
                this.addAllColumns(query, this.priorityQRefTable);
            }
            if (queueDataState != 3) {
                Criteria criteria = new Criteria(Column.getColumn(this.queueTable, "QUEUE_DATA_STATE"), (Object)new Integer(queueDataState), 0);
                criteria = criteria.and(new Criteria(Column.getColumn(this.queueTable, "IS_PRIORITY"), (Object)priorityStatus, 0));
                query.setCriteria(criteria);
            }
            query.addSelectColumn(Column.getColumn(this.queueTable, "QINFO_ID", "qInfoID"));
            query.addSelectColumn(Column.getColumn(this.queueTable, "DATA_FILE_NAME"));
            query.addSelectColumn(Column.getColumn(this.queueTable, "POST_TIME"));
            query.addSelectColumn(Column.getColumn(this.queueTable, "QUEUE_DATA_TYPE"));
            query.addSelectColumn(Column.getColumn(this.queueTable, "QUEUE_DATA_STATE"));
            query.addSelectColumn(Column.getColumn(this.queueTable, "REQUEST_ID"));
            query.addSelectColumn(Column.getColumn(this.queueTable, "IS_PRIORITY"));
            query.addSortColumn(new SortColumn(Column.getColumn(this.queueTable, "POST_TIME"), true));
            final Range range = new Range(0, (int)numOfRecords);
            query.setRange(range);
            return query;
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception in getSelectQueryToLoadFromDB : ", (Throwable)e);
            throw e;
        }
    }
    
    private void addAllColumns(final SelectQuery query, final String queueExtnTable) {
        final String sourceMethod = "addAllColumns";
        try {
            final List<String> columnNameList = MetaDataUtil.getTableDefinitionByName(queueExtnTable).getColumnNames();
            final List<Column> columnList = new ArrayList<Column>();
            for (final String columnName : columnNameList) {
                columnList.add(Column.getColumn(queueExtnTable, columnName, queueExtnTable + "_" + columnName));
            }
            query.addSelectColumns((List)columnList);
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Added columns : " + columnList);
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while adding columns : ", (Throwable)ex);
        }
    }
    
    private DCQueueDataProcessor getQueueDataProcessorRow(final DataObject queueDO, final Row row) throws Exception {
        final String sourceMethod = "getQueueDataProcessorRow";
        try {
            final DCQueueDataProcessor proc = (DCQueueDataProcessor)Class.forName(this.qMetaData.processorClassName).newInstance();
            proc.qHelper = (DCQueueHelper)Class.forName(this.qMetaData.qHelperClassName).newInstance();
            proc.logger = this.logger;
            proc.qErrorLogger = this.qErrorLogger;
            proc.queueFolderName = this.qFolderPath;
            proc.queueName = this.qMetaData.queueName;
            proc.qData = this.getDCQueueDataToLoad(queueDO, row);
            proc.sleepBetweenProcess = this.qMetaData.delayBetweenProcessing;
            proc.queueTable = this.queueTable;
            proc.priorityQRefTable = this.priorityQRefTable;
            proc.queueExtnTable = this.queueExtnTable;
            proc.qMetaData = this.qMetaData;
            return proc;
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception in getQueueDataProcessorRow : ", (Throwable)e);
            throw e;
        }
    }
    
    private DCQueueData getDCQueueDataToLoad(final DataObject queueDO, final Row row) throws Exception {
        final String sourceMethod = "getDCQueueDataToLoad";
        try {
            final DCQueueData qData = new DCQueueData();
            qData.queueDataId = (Long)row.get("QINFO_ID");
            qData.fileName = (String)row.get("DATA_FILE_NAME");
            qData.postTime = (long)row.get("POST_TIME");
            qData.queueDataType = (int)row.get("QUEUE_DATA_TYPE");
            qData.queueData = null;
            qData.loggingId = (String)row.get("REQUEST_ID");
            qData.priority = (boolean)row.get("IS_PRIORITY");
            qData.queueExtnTableData = this.getAdditionalTableData(queueDO, qData.queueDataId, this.queueExtnTable);
            qData.priorityQRefTableData = this.getAdditionalTableData(queueDO, qData.queueDataId, this.priorityQRefTable);
            return qData;
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception in getDCQueueDataToLoad : ", (Throwable)e);
            throw e;
        }
    }
    
    private Map<String, Object> getAdditionalTableData(final DataObject queueDO, final Long qInfoID, final String tableName) {
        final String sourceMethod = "getAdditionalTableData";
        if (tableName != null) {
            try {
                Map<String, Object> tableMap = null;
                final Criteria criteria = new Criteria(Column.getColumn(tableName, "QINFO_ID"), (Object)qInfoID, 0);
                final Row tableRow = queueDO.getRow(tableName, criteria);
                final List pkColumn = MetaDataUtil.getTableDefinitionByName(tableName).getPrimaryKey().getColumnList();
                if (tableRow != null) {
                    tableMap = new Hashtable<String, Object>();
                    final List columns = tableRow.getColumns();
                    final List values = tableRow.getValues();
                    for (int index = 0; index < columns.size(); ++index) {
                        final String columnName = columns.get(index).toString();
                        final Object value;
                        if ((value = values.get(index)) != null && !pkColumn.contains(columnName)) {
                            tableMap.put(columnName, value);
                        }
                    }
                }
                return tableMap;
            }
            catch (final Exception e) {
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Exception while fetching data for additional queue table : " + tableName);
            }
        }
        return null;
    }
    
    public void processQueueData(final DCQueueDataProcessor proc) {
        this.executor.execute((Runnable)proc);
    }
    
    public String getQueueFolderPath() throws Exception {
        return this.qFolderPath;
    }
    
    private long getPendingTaskCount() {
        return this.executor.getQueue().size();
    }
    
    private ThreadPoolExecutor createThreadPool() {
        if (this.executor == null) {
            this.logger.info("Creating new thread pool for Queue: " + this.qMetaData.queueName);
            (this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>((int)this.qMetaData.qMinSize, (Comparator<? super Runnable>)new Comparator<DCQueueDataProcessor>() {
                @Override
                public int compare(final DCQueueDataProcessor p1, final DCQueueDataProcessor p2) {
                    return (p1.qData.priority == p2.qData.priority) ? Long.valueOf(p1.qData.queueDataId).compareTo(p2.qData.queueDataId) : (p1.qData.priority ? -1 : 1);
                }
            }))).setCorePoolSize(this.qMetaData.processThreadCount);
            this.executor.setMaximumPoolSize(this.qMetaData.processThreadMaxCount);
            this.executor.setKeepAliveTime(this.qMetaData.keepAliveTimeout, TimeUnit.SECONDS);
            this.executor.allowCoreThreadTimeOut(this.qMetaData.timeoutAllThreads);
        }
        return this.executor;
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
    
    private boolean writeDataInFile(final String filePath, final String qContent) {
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
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while writing the status update data in file ", (Throwable)ex);
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
    
    private Hashtable writeDataInFile(final String filePath, final Reader reader) {
        final String sourceMethod = "writeDataInFile with HttpServletRequest";
        Writer writer = null;
        final StringBuilder logBuilder = new StringBuilder();
        final StringBuilder readerContent = new StringBuilder();
        final Hashtable returnHash = new Hashtable();
        returnHash.put("FILE_WRITTEN", Boolean.FALSE);
        try {
            logBuilder.append("Start of writeDataInFile with HttpServletRequest : ").append(filePath).append("\n");
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), Charset.forName("UTF-8").newEncoder()));
            int read = 0;
            final char[] chBuf = new char[500];
            while ((read = reader.read(chBuf)) > -1) {
                writer.write(chBuf, 0, read);
                if (this.qMetaData.retainQDataInMemory) {
                    readerContent.append(chBuf, 0, read);
                }
            }
            logBuilder.append("End of writeDataInFile with Reader : ").append(filePath).append("\n");
            returnHash.put("FILE_WRITTEN", Boolean.TRUE);
            returnHash.put("READER_CONTENT", readerContent);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
            return returnHash;
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while fetching content for queue.. ", (Throwable)e);
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (final Exception ex) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while closing writer .. ", (Throwable)ex);
            }
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (final Exception ex2) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while closing writer .. ", (Throwable)ex2);
            }
        }
        return returnHash;
    }
    
    private boolean writeQDataStateInDB(final DCQueueData qData, final int dataState) {
        final String sourceMethod = "writeQDataStateInDB";
        boolean isOverwritten = false;
        boolean isDBWritten = false;
        final StringBuilder logBuilder = new StringBuilder();
        try {
            logBuilder.append("Start of writeQDataStateInDB with file name: ").append(qData.fileName).append(" with overwrite: ").append(qData.overwriteFile).append("\n");
            DataObject dobj = SyMUtil.getPersistence().constructDataObject();
            if (qData.overwriteFile) {
                final Criteria cri = new Criteria(Column.getColumn(this.queueTable, "DATA_FILE_NAME"), (Object)qData.fileName, 0, false);
                dobj = SyMUtil.getPersistenceLite().get(this.queueTable, cri);
            }
            if (dobj.isEmpty()) {
                Row row = new Row(this.queueTable);
                row.set("POST_TIME", (Object)new Long(qData.postTime));
                row.set("DATA_FILE_NAME", (Object)qData.fileName);
                row.set("QUEUE_DATA_TYPE", (Object)new Integer(qData.queueDataType));
                row.set("QUEUE_DATA_STATE", (Object)new Integer(dataState));
                row.set("REQUEST_ID", (Object)qData.loggingId);
                row.set("IS_PRIORITY", (Object)qData.priority);
                dobj.addRow(row);
                synchronized (this.qTableInAccess) {
                    dobj = SyMUtil.getPersistenceLite().add(dobj);
                    row = dobj.getRow(this.queueTable);
                    qData.queueDataId = (Long)row.get("QINFO_ID");
                    this.populateExtensionTableData(qData);
                    this.populatePriorityQRefTableData(qData);
                    isDBWritten = true;
                }
                final String cacheName = this.queueTable + "_" + dataState;
                ApiFactoryProvider.getCacheAccessAPI().incrementCache(cacheName, 1);
            }
            else {
                Row row = dobj.getRow(this.queueTable);
                row.set("POST_TIME", (Object)new Long(qData.postTime));
                row.set("QUEUE_DATA_TYPE", (Object)new Integer(qData.queueDataType));
                row.set("REQUEST_ID", (Object)qData.loggingId);
                row.set("IS_PRIORITY", (Object)qData.priority);
                dobj.updateRow(row);
                synchronized (this.qTableInAccess) {
                    dobj = SyMUtil.getPersistenceLite().update(dobj);
                    row = dobj.getRow(this.queueTable);
                    qData.queueDataId = (Long)row.get("QINFO_ID");
                    SyMUtil.getPersistenceLite().delete(new Criteria(Column.getColumn(this.queueExtnTable, "QINFO_ID"), (Object)qData.queueDataId, 0));
                    SyMUtil.getPersistenceLite().delete(new Criteria(Column.getColumn(this.priorityQRefTable, "QINFO_ID"), (Object)qData.queueDataId, 0));
                    this.populateExtensionTableData(qData);
                    this.populatePriorityQRefTableData(qData);
                    isDBWritten = true;
                }
                isOverwritten = true;
            }
            if (qData.priority && dataState == 2) {
                final String cacheName2 = this.queueTable + "_" + "QUEUE_CONTAINS_PRIORITY_DATA";
                ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName2, (Object)true);
            }
            logBuilder.append("End of writeQDataStateInDB: ").append(qData.fileName).append(" isOverwritten: ").append(isOverwritten).append("\n");
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
            QueueDataMETracking.incrementTrackingMap(this.qMetaData.queueName);
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while writing the Q data status in DB for data: " + qData, (Throwable)ex);
        }
        return isDBWritten;
    }
    
    private void resetQDataStateInDB() {
        final String sourceMethod = "resetQDataStateInDB";
        final StringBuilder logBuilder = new StringBuilder();
        try {
            logBuilder.append("Start of resetQDataStateInDB()...").append("\n");
            final UpdateQuery resetQuery = (UpdateQuery)new UpdateQueryImpl(this.queueTable);
            final Criteria stateCri = new Criteria(Column.getColumn(this.queueTable, "QUEUE_DATA_STATE"), (Object)new Integer(1), 0);
            resetQuery.setCriteria(stateCri);
            resetQuery.setUpdateColumn("QUEUE_DATA_STATE", (Object)new Integer(2));
            logBuilder.append("Query to be executed in resetQDataStateInDB(): ").append(resetQuery).append("\n");
            synchronized (this.qTableInAccess) {
                SyMUtil.getPersistenceLite().update(resetQuery);
            }
            logBuilder.append("End of resetQDataStateInDB()...").append("\n");
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while reseting the Q data status in DB", (Throwable)ex);
        }
    }
    
    public int getQueueDataCount(final int qState) {
        int recordCount = -1;
        if (qState == 2) {
            recordCount = this.getQueueDataCountInDBFromCache();
            if (recordCount < 0) {
                this.updateDBQueueDataCountInCache(this.getQueueDataCountFromDB(qState));
            }
        }
        else {
            recordCount = this.getQueueDataCountFromDB(qState);
        }
        return recordCount;
    }
    
    private int getQueueDataCountInDBFromCache() {
        final String sourceMethod = "getQueueDataCountFromCache";
        final String cacheName = this.queueTable + "_" + 2;
        try {
            final Object queueCountFromCache = ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName);
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "queue count from cache for : " + cacheName + " = " + queueCountFromCache);
            if (queueCountFromCache != null) {
                return (int)queueCountFromCache;
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while getting queue count from cache for : " + cacheName, (Throwable)e);
        }
        return -1;
    }
    
    private int getQueueDataCountFromDB(final int qState) {
        final String sourceMethod = "getQueueDataCountFromDB";
        int recordCount = 0;
        final Table baseTable = Table.getTable(this.queueTable);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(baseTable);
        if (qState != 3) {
            final Criteria cri = new Criteria(Column.getColumn(this.queueTable, "QUEUE_DATA_STATE"), (Object)new Integer(qState), 0);
            query.setCriteria(cri);
        }
        Column selCol = new Column(this.queueTable, "QINFO_ID");
        selCol = selCol.count();
        query.addSelectColumn(selCol);
        SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "SelectQuery after adding select column {0}: ", new Object[] { query });
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                final Object value = ds.getValue(1);
                if (value != null) {
                    recordCount = (int)value;
                }
            }
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Queue Data count from DB : " + recordCount);
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while executing the query: " + query, (Throwable)ex);
            try {
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final Exception ex2) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while closing DataSet: ", (Throwable)ex2);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex3) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while closing Connection: ", (Throwable)ex3);
            }
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final Exception ex4) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while closing DataSet: ", (Throwable)ex4);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex5) {
                SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while closing Connection: ", (Throwable)ex5);
            }
        }
        return recordCount;
    }
    
    private void updateDBQueueDataCountInCache(final int qCountInDB) {
        final String cacheName = this.queueTable + "_" + 2;
        ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, (Object)qCountInDB);
    }
    
    public boolean isPriorityDataPresent() {
        final String sourceMethod = "isPriorityDataPresent";
        try {
            Object priorityStatus = -1;
            priorityStatus = this.getPriorityStatusFromCache();
            if (priorityStatus == Integer.valueOf(-1)) {
                priorityStatus = this.updatePriorityStatusInCache(this.isPriorityDataPresentInDB());
            }
            return (boolean)priorityStatus;
        }
        catch (final Exception e) {
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Cannot retrieve Priority Status");
            return false;
        }
    }
    
    private Object getPriorityStatusFromCache() {
        final String sourceMethod = "getPriorityStatusFromCache";
        final String cacheName = this.queueTable + "_" + "QUEUE_CONTAINS_PRIORITY_DATA";
        try {
            final Object priorityStatusFromCache = ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName);
            SyMLogger.debug(this.logger, this.sourceClass, sourceMethod, "Priority Status from cache for : " + cacheName + " = " + priorityStatusFromCache);
            if (priorityStatusFromCache != null) {
                return priorityStatusFromCache;
            }
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception while getting Priority Status from cache for : " + cacheName, (Throwable)e);
        }
        return -1;
    }
    
    private boolean isPriorityDataPresentInDB() throws Exception {
        final String sourceMethod = "isPriorityDataPresentInDB";
        try {
            Criteria cri = new Criteria(Column.getColumn(this.queueTable, "IS_PRIORITY"), (Object)true, 0);
            cri = cri.and(new Criteria(Column.getColumn(this.queueTable, "QUEUE_DATA_STATE"), (Object)2, 0));
            return !DataAccess.get(this.queueTable, cri).isEmpty();
        }
        catch (final DataAccessException e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception in isPriorityDataPresentInDB : ", (Throwable)e);
            throw e;
        }
    }
    
    private Object updatePriorityStatusInCache(final Object priorityStatus) throws Exception {
        final String sourceMethod = "updatePriorityStatusInCache";
        try {
            final String cacheName = this.queueTable + "_" + "QUEUE_CONTAINS_PRIORITY_DATA";
            ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, priorityStatus);
            return priorityStatus;
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception in updatePriorityStatusInCache : ", (Throwable)e);
            throw e;
        }
    }
    
    private void printQueueStats() {
        final String sourceMethod = "printQueueStats";
        final String cacheName = this.queueTable + "_" + 2;
        final long queueCountInMemory = this.getPendingTaskCount();
        final StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("============================ Queue Statistics ==============================").append("\n");
        logBuilder.append("DB Q Count in DB : ").append(this.getQueueDataCountFromDB(2)).append("\n");
        logBuilder.append("DB Q Count in Cache : ").append(ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName)).append("\n");
        logBuilder.append("Memory Q Count : ").append(queueCountInMemory).append("\n");
        logBuilder.append("============================ Queue Statistics ==============================").append("\n");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, logBuilder.toString());
    }
    
    public boolean isQueueEligibleForMonitor() {
        final boolean isLoaded = loadConfigJson();
        if (!isLoaded) {
            return false;
        }
        final List<String> allowedQueues = DefaultDCQueue.queueTimerConfigurations.getAllowedQueues();
        return DefaultDCQueue.queueTimerConfigurations.isFeatureEnabled() && allowedQueues.contains(this.queueName) && this.blockedCount < DefaultDCQueue.queueTimerConfigurations.getThreadThreshold();
    }
    
    public void monitorQueue(final DCQueueDataProcessor proc) {
        final DCQueueTimer dcQueueTimer = new DCQueueTimer(DefaultDCQueue.queueTimerConfigurations);
        dcQueueTimer.startTimer(proc);
    }
    
    public void respawnThread(final DCQueueDataProcessor proc, final Object properties) {
        final Logger logger = Logger.getLogger("SysStatusLogger");
        final QueueTimerConfigurations threadProperties = (QueueTimerConfigurations)properties;
        final List<String> allowedQueues = threadProperties.getAllowedQueues();
        final int threadThreshold = threadProperties.getThreadThreshold();
        logger.log(Level.INFO, "current queue name: {0} | current blocked count: {1}", new Object[] { proc.queueName, this.blockedCount });
        if (!allowedQueues.contains(proc.queueName)) {
            logger.severe("this queue is not allowed for respawning thread");
            return;
        }
        if (proc.isCompleted()) {
            logger.info("Queue processor is completed. Terminating to create new thread");
            return;
        }
        if (this.blockedCount >= threadThreshold) {
            logger.severe("Blocked Thread count limit is reached");
            return;
        }
        BlockingQueue tempQueue = null;
        final int queueSize = this.executor.getQueue().size();
        if (queueSize != 0) {
            tempQueue = new ArrayBlockingQueue(queueSize);
            this.executor.getQueue().drainTo(tempQueue);
        }
        logger.log(Level.SEVERE, "****** KILLING REFERENCE OF OLD EXECUTOR ! RESPAWNING THREAD ***** | Queue Name: {0}", this.queueName);
        this.executor.shutdownNow();
        this.executor = null;
        this.createThreadPool();
        this.executor.execute((Runnable)proc);
        ++this.blockedCount;
        DefaultDCQueue.BLOCKED_QUEUE_SUMMARY.put(this.queueName, this.blockedCount);
        if (queueSize != 0) {
            tempQueue.drainTo(this.executor.getQueue());
            logger.log(Level.FINE, "queue size: Before = {0} | After = {1}", new Object[] { queueSize, this.executor.getQueue().size() });
        }
    }
    
    private static boolean loadConfigJson() {
        if (DefaultDCQueue.queueTimerConfigurations != null) {
            return true;
        }
        try {
            final File jsonFilePath = new File(DefaultDCQueue.THREAD_PROPERTIES_FILE_PATH);
            if (!jsonFilePath.exists()) {
                Logger.getLogger("SysStatusLogger").log(Level.INFO, "queue timer configuration file does not exist");
                return false;
            }
            final JSONObject threadConfigurations = JsonUtils.loadJsonFile(jsonFilePath);
            final ObjectMapper objectMapper = new ObjectMapper();
            DefaultDCQueue.queueTimerConfigurations = (QueueTimerConfigurations)objectMapper.readValue(threadConfigurations.toString(), (Class)QueueTimerConfigurations.class);
        }
        catch (final Exception e) {
            Logger.getLogger("SysStatusLogger").log(Level.SEVERE, "Exception raised in reading json file", e);
            return false;
        }
        return true;
    }
    
    public static Map<String, Integer> getQueueBlockedSummary() {
        return DefaultDCQueue.BLOCKED_QUEUE_SUMMARY;
    }
    
    static {
        DefaultDCQueue.queueTimerConfigurations = null;
        THREAD_PROPERTIES_FILE_PATH = System.getProperty("server.home") + File.separator + "conf" + File.separator + "DeviceManagementFramework" + File.separator + "scalability" + File.separator + "queue-timer-configuration.json";
        BLOCKED_QUEUE_SUMMARY = new HashMap<String, Integer>();
    }
}
