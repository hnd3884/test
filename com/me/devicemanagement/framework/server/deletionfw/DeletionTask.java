package com.me.devicemanagement.framework.server.deletionfw;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DeletionTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public DeletionTask() {
        this.logger = DeletionTaskUtil.getDeletionFwLogger();
    }
    
    @Override
    public void executeTask(final Properties props) {
        this.executeTask(false);
    }
    
    public boolean executeTask(final boolean isForceCleanup) {
        final long startTime = System.currentTimeMillis();
        boolean isCleanupSuccessful = true;
        try {
            try {
                final List<DeleteDataDetails> propsFromDB = DeletionTaskUtil.getDataToDeleteFromDB();
                propsFromDB.addAll(DeletionTaskUtil.getDependentDataToBeDeleted());
                this.logger.log(Level.INFO, "----------------------------------Deletion Task Schedule Started--------------------------");
                this.logger.log(Level.INFO, () -> "StartTime : " + System.currentTimeMillis());
                this.logger.log(Level.INFO, () -> "Total No. of tasks to execute Schedule : " + list.size());
                for (final DeleteDataDetails deleteDataDetails : propsFromDB) {
                    try {
                        this.logger.log(Level.INFO, () -> "------------Processing task with id [" + deleteDataDetails2.taskID + "]");
                        new DeletionDataProcessor().processData(deleteDataDetails);
                    }
                    catch (final Exception e) {
                        isCleanupSuccessful = false;
                        this.logger.log(Level.SEVERE, e, () -> "Exception while processing task [" + deleteDataDetails3.taskID + "]");
                    }
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception while doing deletion", e2);
            }
            this.logger.log(Level.INFO, "----------------------------------Deletion Cleanup Task Started--------------------------");
            try {
                DeletionTaskUtil.doInDependentDataCleanup();
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception while doing deletion cleanup", e2);
                isCleanupSuccessful = false;
            }
            try {
                if (DeletionTaskUtil.doOrphanRowCleanup(isForceCleanup)) {
                    isCleanupSuccessful = true;
                    this.logger.log(Level.INFO, "Orphan Cleanup Successful");
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception while performing orphan cleanup", e2);
            }
            try {
                DeletionTaskUtil.doDeletionInfoDBCleanup();
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception while performing DB History cleanup", e2);
            }
        }
        finally {
            final long totalTime = System.currentTimeMillis() - startTime;
            this.logger.info(() -> "Finished! processing task. Time taken : " + n);
            this.logger.log(Level.INFO, "----------------------------------Deletion Task Schedule Ended--------------------------");
        }
        return isCleanupSuccessful;
    }
}
