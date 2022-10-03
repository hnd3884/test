package com.adventnet.authentication.scheduler;

import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.taskengine.TaskExecutionException;
import java.util.logging.Level;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.Criteria;
import java.util.concurrent.TimeUnit;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.taskengine.TaskContext;
import java.util.logging.Logger;
import com.adventnet.taskengine.Task;

public class DeleteUnusedRememberMeTokens implements Task
{
    private static final int DAY_COUNT;
    private static final Logger LOGGER;
    
    public void executeTask(final TaskContext taskContext) throws TaskExecutionException {
        final DeleteQuery delQuery = (DeleteQuery)new DeleteQueryImpl("AAARememberMeInfo");
        final Criteria cri = new Criteria(Column.getColumn("AAARememberMeInfo", "UPDATEDTIME"), (Object)(System.currentTimeMillis() - TimeUnit.DAYS.toSeconds(DeleteUnusedRememberMeTokens.DAY_COUNT)), 6);
        delQuery.setCriteria(cri);
        try {
            ((Persistence)BeanUtil.lookup("Persistence")).delete(delQuery);
        }
        catch (final Exception e) {
            DeleteUnusedRememberMeTokens.LOGGER.log(Level.INFO, "Exception occured while deleting unused remember me tokens :: " + e.getMessage());
            e.printStackTrace();
            throw new TaskExecutionException("Exception occured while unused unused remember me tokens", (Throwable)e);
        }
    }
    
    public void stopTask() throws TaskExecutionException {
    }
    
    static {
        DAY_COUNT = ((PersistenceInitializer.getConfigurationValue("DaysToRemoveUnusedRememberMeTokens") != null) ? Integer.parseInt(PersistenceInitializer.getConfigurationValue("DaysToRemoveUnusedRememberMeTokens")) : 3);
        LOGGER = Logger.getLogger(DeleteUnusedRememberMeTokens.class.getName());
    }
}
