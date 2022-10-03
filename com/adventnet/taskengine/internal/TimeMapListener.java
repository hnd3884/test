package com.adventnet.taskengine.internal;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import javax.transaction.Transaction;
import javax.transaction.RollbackException;
import java.util.logging.Level;
import javax.transaction.Synchronization;
import com.adventnet.taskengine.util.PersistenceUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.taskengine.TaskContext;
import java.util.logging.Logger;
import java.util.Map;

public class TimeMapListener
{
    private static Map txTable;
    private static Logger logger;
    
    public boolean taskAdded(final TaskContext ctx) throws Exception {
        return true;
    }
    
    public boolean gotTaskForExecution(final TaskContext ctx) throws Exception {
        return true;
    }
    
    public void newTaskAdded(final DataObject taskInput) throws Exception {
        final Transaction txn = PersistenceUtil.getTransactionManager().getTransaction();
        if (txn != null) {
            Synchronizer sync = TimeMapListener.txTable.get(txn);
            if (sync == null) {
                sync = new Synchronizer();
                try {
                    txn.registerSynchronization((Synchronization)sync);
                    sync.putInTable(txn);
                }
                catch (final RollbackException excp) {
                    TimeMapListener.logger.log(Level.INFO, "OptimizedPublisher: Already set for rollback. Dropping notification to ");
                }
            }
            sync.addTaskInput(taskInput);
        }
    }
    
    protected void notifyAddToSynchronizerList(final Map map) throws Exception {
    }
    
    protected boolean notifyRemoveFromSynchronizerList(final Map map) throws Exception {
        return true;
    }
    
    static {
        TimeMapListener.txTable = new Hashtable();
        TimeMapListener.logger = Logger.getLogger(TimeMapListener.class.getName());
    }
    
    private class Synchronizer implements Synchronization
    {
        private List taskInputs;
        private Transaction txn;
        
        public Synchronizer() {
            this.taskInputs = new ArrayList();
        }
        
        void putInTable(final Transaction txn) {
            this.txn = txn;
            TimeMapListener.txTable.put(txn, this);
        }
        
        void addTaskInput(final DataObject taskInput) throws Exception {
            final HashMap map = new HashMap();
            map.put("TaskInput", taskInput);
            TimeMapListener.this.notifyAddToSynchronizerList(map);
            this.taskInputs.add(map);
        }
        
        public void afterCompletion(final int status) {
            try {
                TimeMapListener.txTable.remove(this.txn);
                if (status == 3) {
                    for (int size = this.taskInputs.size(), i = 0; i < size; ++i) {
                        TimeMapListener.logger.log(Level.FINE, "About to add into TimeMap");
                        final Map map = this.taskInputs.get(i);
                        final DataObject taskInput = map.get("TaskInput");
                        final boolean bool = TimeMapListener.this.notifyRemoveFromSynchronizerList(map);
                        if (bool) {
                            final Long poolId = (Long)taskInput.getFirstRow("Task_Input").get("POOL_ID");
                            String poolname = null;
                            if (poolId != null) {
                                poolname = PersistenceUtil.getPoolName(poolId);
                            }
                            TimeMap.getInstance(poolname).addToTimeMap(taskInput);
                        }
                    }
                }
            }
            catch (final Exception excp) {
                TimeMapListener.logger.log(Level.INFO, "Exception occurred in TimeMapListener.Synchronizer.afterCompletion method :: {0}", excp);
            }
        }
        
        public void beforeCompletion() {
        }
    }
}
