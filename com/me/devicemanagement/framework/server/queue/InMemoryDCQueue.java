package com.me.devicemanagement.framework.server.queue;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.io.Reader;
import com.adventnet.sym.logging.LoggingThreadLocal;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class InMemoryDCQueue implements DCQueue, DCQueueConstants, DCQueueHelper
{
    private DCQueueMetaData qMetaData;
    private String sourceClass;
    Logger logger;
    Logger qErrorLogger;
    
    public InMemoryDCQueue(final DCQueueMetaData qMetaData) {
        this.qMetaData = null;
        this.sourceClass = "InMemoryDCQueue";
        this.logger = null;
        this.qErrorLogger = null;
        this.qMetaData = qMetaData;
        this.sourceClass = "InMemoryDCQueue:" + qMetaData.queueName;
        this.logger = Logger.getLogger(qMetaData.loggerName);
        this.qErrorLogger = Logger.getLogger(qMetaData.qErrorLoggerName);
        final String sourceMethod = "InMemoryDCQueue";
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "\n------------------InMemoryDCQueue------------------------");
        SyMLogger.info(this.logger, this.sourceClass, sourceMethod, "Queue meta data: " + qMetaData);
    }
    
    @Override
    public void start() throws Exception {
    }
    
    @Override
    public void shutdownQueue() throws Exception {
    }
    
    @Override
    public void addToQueue(final DCQueueData qData) throws Exception {
        try {
            this.addToQueue(qData, (String)null);
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addToQueue", "Exception while adding data to queue.", ex);
        }
    }
    
    @Override
    public void addToQueue(final DCQueueData qData, final String qContent) throws Exception {
        try {
            qData.loggingId = LoggingThreadLocal.getLoggingId();
            if (qContent != null) {
                qData.queueData = qContent;
            }
            final DCQueueDataProcessor proc = (DCQueueDataProcessor)Class.forName(this.qMetaData.processorClassName).newInstance();
            proc.qHelper = this;
            proc.logger = this.logger;
            proc.qErrorLogger = this.qErrorLogger;
            proc.queueName = this.qMetaData.queueName;
            proc.qData = qData;
            this.processQueueData(proc);
        }
        catch (final Exception e) {
            SyMLogger.error(this.logger, this.sourceClass, "addToQueue - String", "Exception while adding data to queue.", e);
        }
    }
    
    @Override
    public void addToQueue(final DCQueueData qData, final Reader reader) throws Exception {
        try {
            this.addToQueue(qData, SyMUtil.getReaderContent(reader).toString());
        }
        catch (final Exception ex) {
            SyMLogger.error(this.logger, this.sourceClass, "addToQueue - HttpServletRequest", "Exception while adding data to queue.", ex);
        }
    }
    
    @Override
    public void suspendQExecution() throws Exception {
    }
    
    @Override
    public void resumeQExecution() throws Exception {
    }
    
    @Override
    public boolean isQueueSuspended() throws Exception {
        return false;
    }
    
    @Override
    public int getQueueDataCount(final int qState) {
        return 0;
    }
    
    @Override
    public void processQueueData(final DCQueueDataProcessor proc) throws Exception {
        proc.run();
    }
    
    @Override
    public String getQueueFolderPath() throws Exception {
        return null;
    }
    
    @Override
    public String readFile(final String filePath) throws Exception {
        return null;
    }
    
    @Override
    public boolean deleteFile(final String filePath) throws Exception {
        return true;
    }
    
    @Override
    public void deleteDBEntry(final DCQueueData dcQData, final boolean isFileDeleted, final DCQueueMetaData metaData) throws Exception {
    }
    
    @Override
    public String unCompressString(final DCQueueData dcQData) {
        return null;
    }
}
