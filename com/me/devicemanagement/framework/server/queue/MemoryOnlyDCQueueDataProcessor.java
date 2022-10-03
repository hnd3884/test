package com.me.devicemanagement.framework.server.queue;

import java.util.logging.Level;
import com.adventnet.sym.logging.LoggingThreadLocal;

public abstract class MemoryOnlyDCQueueDataProcessor extends DCQueueDataProcessor
{
    @Override
    public void run() {
        try {
            LoggingThreadLocal.setLoggingId(this.qData.loggingId);
            this.processData(this.qData);
            if (this.sleepBetweenProcess > 0L) {
                Thread.sleep(this.sleepBetweenProcess);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "MemoryOnlyDCQueueDataProcessor: Caught exception while processing data: " + this.qData, ex);
        }
    }
}
