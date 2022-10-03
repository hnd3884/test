package com.me.devicemanagement.onpremise.server.queue;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;
import java.util.concurrent.Executors;
import com.me.devicemanagement.onpremise.server.entity.QueueTimerConfigurations;
import java.util.logging.Logger;
import java.util.concurrent.Executor;
import com.me.devicemanagement.framework.server.queue.DCQueue;

public class DCQueueTimer
{
    private DCQueue dcQueue;
    private final Executor executor;
    private static final Logger logger;
    private final QueueTimerConfigurations queueTimerConfigurations;
    
    public DCQueueTimer(final QueueTimerConfigurations queueTimerConfigurations) {
        this.executor = Executors.newSingleThreadExecutor();
        this.queueTimerConfigurations = queueTimerConfigurations;
    }
    
    public void startTimer(final DCQueueDataProcessor processor) {
        try {
            this.dcQueue = DCQueueHandler.getQueue(processor.queueName);
            final long currentQueueId = processor.qData.queueDataId;
            if (this.queueTimerConfigurations == null) {
                DCQueueTimer.logger.severe("thread threshold properties file is not loaded, terminating to start timer");
                return;
            }
            if (!this.queueTimerConfigurations.isFeatureEnabled()) {
                DCQueueTimer.logger.info("thread monitoring feature is disabled, terminating to start timer");
                return;
            }
            DCQueueTimer.logger.log(Level.FINE, "{0}", new Object[] { this.queueTimerConfigurations });
            final long maxTimeLimit = System.currentTimeMillis() + this.queueTimerConfigurations.getAllowedTimeLimit();
            this.executor.execute(() -> this.initiateTimer(currentQueueId2, maxTimeLimit2, processor2));
        }
        catch (final Exception e) {
            DCQueueTimer.logger.log(Level.SEVERE, "Exception raised in start timer for queue", e);
        }
    }
    
    private void initiateTimer(final long currentQueueId, final long maxTimeLimit, final DCQueueDataProcessor processor) {
        DCQueueTimer.logger.log(Level.FINE, "initiating timer for task: {0}, queue name: {1}", new Object[] { currentQueueId, processor.queueName });
        try {
            long currentTimeMillis = System.currentTimeMillis();
            final long avgProcessingCount = 100L;
            while (currentTimeMillis <= maxTimeLimit) {
                TimeUnit.MILLISECONDS.sleep(avgProcessingCount);
                if (processor.isCompleted()) {
                    break;
                }
                currentTimeMillis = System.currentTimeMillis();
            }
            if (!processor.isCompleted()) {
                DCQueueTimer.logger.log(Level.INFO, "Timeout for current running process: {0}", currentQueueId);
                this.dcQueue.respawnThread(processor, (Object)this.queueTimerConfigurations);
            }
        }
        catch (final InterruptedException e) {
            DCQueueTimer.logger.severe("raised interrupted exception in queue timer thread");
            Thread.currentThread().interrupt();
        }
        catch (final Exception e2) {
            DCQueueTimer.logger.log(Level.SEVERE, "exception raised in initiating timer", e2);
        }
    }
    
    static {
        logger = Logger.getLogger("SysStatusLogger");
    }
}
