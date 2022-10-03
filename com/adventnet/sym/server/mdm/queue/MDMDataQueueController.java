package com.adventnet.sym.server.mdm.queue;

import com.me.devicemanagement.framework.server.queue.DCQueue;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class MDMDataQueueController extends DCQueueDataProcessor
{
    Logger queueLogger;
    Logger logger;
    private String seperator;
    private String parsingStarted;
    private String parsingEnded;
    private String addedToQueue;
    private String timeTaken;
    
    public MDMDataQueueController() {
        this.queueLogger = Logger.getLogger("MDMQueueBriefLogger");
        this.logger = Logger.getLogger("MDMLogger");
        this.seperator = "\t";
        this.parsingStarted = "ParsingStarted";
        this.parsingEnded = "ParsingEnded";
        this.addedToQueue = "addingToQueue";
        this.timeTaken = "TimeTakenForQueueFinding";
    }
    
    public void processData(final DCQueueData qData) {
        final Long sysTime = System.currentTimeMillis();
        this.queueLogger.log(Level.INFO, "{0}{1}{2}{3}{4}{5}{6}", new Object[] { MDMDataQueueUtil.getInstance().getPlatformNameForLogging(qData.queueDataType), this.seperator, this.parsingStarted, this.seperator, qData.fileName, this.seperator, String.valueOf(sysTime) });
        final String queueName = QueueControllerHelper.getInstance().getQueueNameForQueueDatatype(qData.queueDataType, (String)qData.queueData);
        this.queueLogger.log(Level.INFO, "{0}{1}{2}{3}{4}{5}{6}{7}{8} - {9}", new Object[] { MDMDataQueueUtil.getInstance().getPlatformNameForLogging(qData.queueDataType), this.seperator, this.parsingEnded, this.seperator, qData.fileName, this.seperator, queueName, this.seperator, this.timeTaken, String.valueOf(System.currentTimeMillis() - sysTime) });
        if (queueName.equalsIgnoreCase(QueueName.OTHERS.getQueueName())) {
            this.printExcessInfo(qData);
        }
        this.addResponseToQueue(qData, queueName);
    }
    
    private void printExcessInfo(final DCQueueData queue) {
        this.queueLogger.log(Level.INFO, "Rogue data spotted, take necessary action {0} - datatype , {1} - filename", new Object[] { queue.queueDataType, queue.fileName });
        this.logger.log(Level.INFO, "Unable to process the queue data with file name {0}, the data is {1} ", new Object[] { queue.fileName, queue.queueData });
    }
    
    private void addResponseToQueue(final DCQueueData qData, final String queueName) {
        this.logger.log(Level.INFO, "Going to add data {0} to {1} queue", new Object[] { qData.fileName, queueName });
        this.queueLogger.log(Level.INFO, "{0}{1}{2}{3}{4}", new Object[] { queueName, this.seperator, this.addedToQueue, this.seperator, qData.fileName });
        final long postTime = System.currentTimeMillis();
        try {
            final DCQueue queue = DCQueueHandler.getQueue(queueName);
            qData.postTime = postTime;
            queue.addToQueue(qData);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Cannot add data to the queue", exp);
        }
    }
}
