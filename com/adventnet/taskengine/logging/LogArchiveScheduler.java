package com.adventnet.taskengine.logging;

import com.adventnet.taskengine.TaskExecutionException;
import java.util.logging.Level;
import com.adventnet.mfw.logging.LogsArchiver;
import com.adventnet.taskengine.TaskContext;
import java.util.logging.Logger;
import com.adventnet.taskengine.Task;

public class LogArchiveScheduler implements Task
{
    private static final Logger LOGGER;
    
    @Override
    public void executeTask(final TaskContext taskContext) throws TaskExecutionException {
        try {
            LogArchiveScheduler.LOGGER.info("LogArchive task invoked.... ");
            LogsArchiver.archiveLogs();
            LogArchiveScheduler.LOGGER.info("LogArchive task completed.... ");
        }
        catch (final Exception ex) {
            LogArchiveScheduler.LOGGER.log(Level.SEVERE, "Exception during execute LogArchiveScheduler :: {0}", ex);
            throw new TaskExecutionException("Exception occured while archiving old log files", ex);
        }
    }
    
    @Override
    public void stopTask() throws TaskExecutionException {
    }
    
    static {
        LOGGER = Logger.getLogger(LogArchiveScheduler.class.getName());
    }
}
