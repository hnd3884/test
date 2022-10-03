package com.adventnet.authentication.scheduler;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.taskengine.TaskExecutionException;
import java.util.logging.Level;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.taskengine.TaskContext;
import java.util.logging.Logger;
import com.adventnet.taskengine.Task;

public class DeleteExpiredRememberMeTokens implements Task
{
    private static final Logger LOGGER;
    
    public void executeTask(final TaskContext taskContext) throws TaskExecutionException {
        final DeleteQuery delQuery = (DeleteQuery)new DeleteQueryImpl("AAARememberMeInfo");
        final Criteria cri = new Criteria(Column.getColumn("AAARememberMeInfo", "EXPIRETIME"), (Object)System.currentTimeMillis(), 6);
        delQuery.setCriteria(cri);
        try {
            ((Persistence)BeanUtil.lookup("Persistence")).delete(delQuery);
        }
        catch (final Exception e) {
            DeleteExpiredRememberMeTokens.LOGGER.log(Level.INFO, "Exception occured while deleting expired remember me tokens :: " + e.getMessage());
            e.printStackTrace();
            throw new TaskExecutionException("Exception occured while deleting expired remember me tokens", (Throwable)e);
        }
    }
    
    public void stopTask() throws TaskExecutionException {
    }
    
    static {
        LOGGER = Logger.getLogger(DeleteExpiredRememberMeTokens.class.getName());
    }
}
