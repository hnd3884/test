package com.me.tools.zcutil.mickeylite;

import com.adventnet.taskengine.TaskExecutionException;
import com.me.tools.zcutil.METrack;
import java.util.logging.Level;
import com.adventnet.taskengine.TaskContext;
import java.util.logging.Logger;
import com.adventnet.taskengine.Task;

public class MickeLiteZCSchedule implements Task
{
    private static final Logger LOGGER;
    
    public void executeTask(final TaskContext context) throws TaskExecutionException {
        try {
            MickeLiteZCSchedule.LOGGER.log(Level.INFO, "ZCSchedule --- ");
            METrack.ZCScheduler();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stopTask() throws TaskExecutionException {
        MickeLiteZCSchedule.LOGGER.log(Level.INFO, "Stop Task MickeyLite ZCSchedule --- ");
    }
    
    static {
        LOGGER = Logger.getLogger(MickeLiteZCSchedule.class.getName());
    }
}
