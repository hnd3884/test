package com.me.devicemanagement.onpremise.server.queue;

import java.util.concurrent.BlockingQueue;
import java.util.Collection;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.List;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.io.Reader;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;
import com.adventnet.sym.logging.LoggingThreadLocal;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import com.me.devicemanagement.framework.server.queue.DCQueueMetaData;
import com.me.devicemanagement.framework.server.queue.DCQueueConstants;
import com.me.devicemanagement.framework.server.queue.DCQueue;

public class MemoryOnlyDCQueue implements DCQueue, DCQueueConstants
{
    private final Integer taskQListInAccess;
    private String sourceClass;
    private DCQueueMetaData qMetaData;
    private ThreadPoolExecutor executor;
    private ArrayBlockingQueue suspendedTaskQueue;
    private int queueState;
    Logger logger;
    Logger qErrorLogger;
    
    public MemoryOnlyDCQueue(final DCQueueMetaData qMetaData) {
        this.taskQListInAccess = new Integer(1);
        this.sourceClass = "MemoryOnlyDCQueue";
        this.qMetaData = null;
        this.suspendedTaskQueue = null;
        this.queueState = 100;
        this.logger = null;
        this.qErrorLogger = null;
        this.qMetaData = qMetaData;
        this.sourceClass = "MemoryOnlyDCQueue:" + qMetaData.queueName;
        this.logger = Logger.getLogger(qMetaData.loggerName);
        this.qErrorLogger = Logger.getLogger(qMetaData.qErrorLoggerName);
        final String sourceMethod = "MemoryOnlyDCQueue";
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "\n------------------MemoryOnlyDCQueue------------------------");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Queue meta data: " + qMetaData);
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Creating new thread pool for Queue: " + qMetaData.queueName);
        (this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(qMetaData.processThreadCount)).setCorePoolSize(qMetaData.processThreadCount);
        this.executor.setMaximumPoolSize(qMetaData.processThreadMaxCount);
        this.executor.setKeepAliveTime(qMetaData.keepAliveTimeout, TimeUnit.SECONDS);
        this.executor.allowCoreThreadTimeOut(qMetaData.timeoutAllThreads);
    }
    
    public void start() throws Exception {
        if (this.executor == null) {
            this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(this.qMetaData.processThreadCount);
        }
    }
    
    public void addToQueue(final DCQueueData qData) throws Exception {
        final String sourceMethod = "addToQueue";
        try {
            this.addToQueue(qData, (String)null);
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while adding into Queue: " + this.qMetaData.queueName + " QData: " + qData + " Exception: {0} ", (Throwable)ex);
            throw ex;
        }
    }
    
    public void addToQueue(final DCQueueData qData, final String qContent) throws Exception {
        qData.loggingId = LoggingThreadLocal.getLoggingId();
        if (qContent != null) {
            qData.queueData = qContent;
        }
        final DCQueueDataProcessor proc = (DCQueueDataProcessor)Class.forName(this.qMetaData.processorClassName).newInstance();
        proc.logger = this.logger;
        proc.qErrorLogger = this.qErrorLogger;
        proc.queueName = this.qMetaData.queueName;
        proc.qData = qData;
        proc.sleepBetweenProcess = this.qMetaData.delayBetweenProcessing;
        if (this.queueState == 101 && this.suspendedTaskQueue != null) {
            this.suspendedTaskQueue.add(proc);
        }
        else {
            this.executor.execute((Runnable)proc);
        }
    }
    
    public void addToQueue(final DCQueueData qData, final Reader reader) throws Exception {
        final String sourceMethod = "addToQueue";
        try {
            this.addToQueue(qData, SyMUtil.getReaderContent(reader).toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Caught exception while adding into Queue: " + this.qMetaData.queueName + " QData: " + qData + " Exception: {0} ", (Throwable)ex);
            throw ex;
        }
    }
    
    public void shutdownQueue() throws Exception {
        final String sourceMethod = "shutdownQueue";
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Queue is going to be shutdown: " + this.qMetaData.queueName);
        if (this.executor != null) {
            final List pendingTasks = this.executor.shutdownNow();
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Pending tasks while shutdown the Queue: " + this.qMetaData.queueName + " are: " + pendingTasks);
        }
    }
    
    public void suspendQExecution() throws Exception {
        final String sourceMethod = "suspendQExecution";
        try {
            if (this.queueState == 101) {
                SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "Cannot suspend Queue. Already suspended: " + this.qMetaData.queueName);
                throw new SyMException(1002, "Cannot suspend Queue. Already suspended: " + this.qMetaData.queueName, (Throwable)null);
            }
            this.queueState = 101;
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "\n=============================================================");
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Queue is Suspended: " + this.qMetaData.queueName);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "=============================================================\n");
            synchronized (this.taskQListInAccess) {
                final BlockingQueue bq = this.executor.getQueue();
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Queue size before it is getting suspended: " + bq.size() + " for queue: " + this.qMetaData.queueName);
                final int bqsize = bq.size();
                if (bqsize > 0) {
                    bq.drainTo(this.suspendedTaskQueue = new ArrayBlockingQueue(bqsize));
                }
                SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Queue size after it is getting suspended: " + bq.size() + " for queue: " + this.qMetaData.queueName);
            }
        }
        catch (final Exception ex) {
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Exception occurred while suspending the Queue: " + this.qMetaData.queueName);
            throw ex;
        }
    }
    
    public void resumeQExecution() throws Exception {
        final String sourceMethod = "resumeQExecution";
        try {
            if (this.queueState == 100) {
                SyMLogger.warning(this.logger, this.sourceClass, sourceMethod, "Cannot resume Queue Execution. Already active: " + this.qMetaData.queueName);
                throw new SyMException(1002, "Cannot resume Queue Execution. Already active: " + this.qMetaData.queueName, (Throwable)null);
            }
            synchronized (this.taskQListInAccess) {
                if (this.suspendedTaskQueue != null) {
                    final BlockingQueue tpQ = this.executor.getQueue();
                    SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Queue size before it is getting resumed: " + tpQ.size() + " for queue: " + this.qMetaData.queueName);
                    this.suspendedTaskQueue.drainTo(tpQ);
                    SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Queue size after it is getting resumed: " + tpQ.size() + " for queue: " + this.qMetaData.queueName);
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, sourceMethod, "Exception occurred while resuming the Queue: " + this.qMetaData.queueName, (Throwable)ex);
        }
        finally {
            if (this.suspendedTaskQueue != null) {
                this.suspendedTaskQueue.clear();
                this.suspendedTaskQueue = null;
            }
            this.queueState = 100;
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "\n=============================================================");
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Queue is Resumed: " + this.qMetaData.queueName);
            SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "=============================================================\n");
        }
    }
    
    public boolean isQueueSuspended() throws Exception {
        return this.queueState == 101;
    }
    
    public int getQueueDataCount(final int qState) {
        return this.executor.getQueue().size();
    }
    
    public void processQueueData(final DCQueueDataProcessor qProc) throws Exception {
        qProc.run();
    }
    
    public String getQueueFolderPath() throws Exception {
        return null;
    }
}
