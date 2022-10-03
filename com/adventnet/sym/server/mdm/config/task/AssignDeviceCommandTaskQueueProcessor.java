package com.adventnet.sym.server.mdm.config.task;

import java.util.concurrent.TimeUnit;
import com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue.CommandQueueObject;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class AssignDeviceCommandTaskQueueProcessor extends DCQueueDataProcessor
{
    private Logger logger;
    private Logger configLogger;
    private final String separator = "\t";
    
    public AssignDeviceCommandTaskQueueProcessor() {
        this.logger = Logger.getLogger("MDMAsyncQueueAccessLogger");
        this.configLogger = Logger.getLogger("MDMConfigLogger");
    }
    
    public void processData(final DCQueueData qData) {
        try {
            this.configLogger.log(Level.INFO, "Started Async-Queue data processing");
            final CommandQueueObject queueObject = MDMApiFactoryProvider.getAssociationQueueSerializer().deSerializeObject(qData.queueData);
            final Long processStartTime = System.currentTimeMillis();
            this.logger.log(Level.INFO, "{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}{10}", new Object[] { queueObject.getCommandName(), "\t", queueObject.getCustomerId(), "\t", qData.fileName, "\t", "ProcessingStarted", "\t", processStartTime, "\t", this.returnDiffInMinutes(qData.postTime, processStartTime) });
            this.processCommand(queueObject);
            this.logger.log(Level.INFO, "{0}{1}{2}{3}{4}{5}{6}{7}{8}{9}{10}", new Object[] { queueObject.getCommandName(), "\t", queueObject.getCustomerId(), "\t", qData.fileName, "\t", "ProcessingEnded", "\t", System.currentTimeMillis(), "\t", this.returnDiffInMinutes(processStartTime, System.currentTimeMillis()) });
        }
        catch (final Exception e) {
            this.configLogger.log(Level.SEVERE, "Cannot process queue data ", e);
        }
    }
    
    private void processCommand(final CommandQueueObject commandObject) {
        if (commandObject.getCommandType() == 2) {
            this.configLogger.log(Level.INFO, "{0} command received for users", commandObject.getCommandName());
            AssignCommandTaskProcessor.getTaskProcessor().assignUserCommandTask(commandObject.getPropsFile());
        }
        else {
            this.configLogger.log(Level.INFO, "{0} command received for devices", commandObject.getCommandName());
            AssignCommandTaskProcessor.getTaskProcessor().assignDeviceCommandTask(commandObject.getPropsFile());
        }
    }
    
    private String returnDiffInMinutes(final Long startTime, final Long endTime) {
        final Long diff = endTime - startTime;
        final StringBuffer buffer = new StringBuffer();
        buffer.append(TimeUnit.MILLISECONDS.toSeconds(diff));
        buffer.append(" sec\t");
        buffer.append(TimeUnit.MILLISECONDS.toMinutes(diff));
        buffer.append(" Min\t");
        return buffer.toString();
    }
}
