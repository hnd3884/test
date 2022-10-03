package com.me.devicemanagement.onpremise.server.redis;

import com.adventnet.taskengine.TaskExecutionException;
import java.util.logging.Level;
import com.adventnet.taskengine.TaskContext;
import java.util.logging.Logger;
import com.adventnet.taskengine.Task;

public class RedisPeriodicBackupScheduler implements Task
{
    private static final Logger LOGGER;
    
    public void executeTask(final TaskContext taskContext) throws TaskExecutionException {
        RedisPeriodicBackupScheduler.LOGGER.log(Level.FINE, " RedisPeriodicBackupScheduler  Begins");
        RedisServerUtil.backupRedisData();
    }
    
    public void stopTask() throws TaskExecutionException {
    }
    
    static {
        LOGGER = Logger.getLogger(RedisPeriodicBackupScheduler.class.getName());
    }
}
