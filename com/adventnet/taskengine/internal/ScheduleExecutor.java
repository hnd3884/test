package com.adventnet.taskengine.internal;

import java.sql.Timestamp;
import javax.transaction.TransactionManager;
import com.adventnet.persistence.DataObject;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.taskengine.Scheduler;
import com.adventnet.taskengine.util.PersistenceUtil;
import com.adventnet.taskengine.TaskCompletionHandler;
import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.message.Messenger;
import com.adventnet.taskengine.Task;
import java.util.Properties;
import java.util.logging.Level;
import com.adventnet.taskengine.TaskContext;
import java.util.logging.Logger;
import com.adventnet.mfw.message.MessageListener;

public class ScheduleExecutor implements Runnable, MessageListener
{
    private static Logger out;
    private static int count;
    private int instanceid;
    private String poolName;
    private TaskContext context;
    
    public ScheduleExecutor(final String poolName, final TaskContext taskContext) throws Exception {
        this.instanceid = 0;
        this.context = null;
        this.poolName = poolName;
        this.instanceid = ++ScheduleExecutor.count;
        this.registerForServerShutDownNotification();
        this.context = taskContext;
    }
    
    public void onMessage(final Object notifyObject) {
        try {
            ScheduleExecutor.out.log(Level.FINE, "[{0}] received the SERVER_SHUTDOWN_NOTIFICATION", this);
            if (this.context != null) {
                final Properties properties = (Properties)notifyObject;
                ScheduleExecutor.out.log(Level.FINE, "properties got from notification :: [{0}]", properties);
                final Task task = this.context.getTaskInstance();
                task.stopTask();
                final boolean isSuccess = this.context.isSuccess();
                ScheduleExecutor.out.log(Level.FINE, "Successfully stopped the task [{0}] which was executing.", task);
            }
        }
        catch (final Exception e) {
            ScheduleExecutor.out.log(Level.SEVERE, "Exception occurred while stopping the task in ScheduleExecutor.onMessage method :: [{0}]", e);
        }
    }
    
    private void registerForServerShutDownNotification() throws Exception {
        Messenger.subscribe("SERVER_SHUTDOWN_NOTIFICATION", (MessageListener)this, true, (MessageFilter)null);
    }
    
    private void unregisterForServerShutDownNotification() throws Exception {
        Messenger.unsubscribe("SERVER_SHUTDOWN_NOTIFICATION", (MessageListener)this);
    }
    
    @Override
    public void run() {
        boolean remove = true;
        try {
            ScheduleExecutor.out.log(Level.FINE, "Task assigned for execution : {0}", this.context.getID());
            remove = this.executeNextTask(this.context);
        }
        catch (final Throwable excp) {
            ScheduleExecutor.out.log(Level.SEVERE, "", excp);
            if (remove) {
                this.removeFromExecutingTask(this.context);
            }
            try {
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        final boolean isSuccess = ScheduleExecutor.this.context.isSuccess();
                        try {
                            final TaskCompletionHandler handlerInstance = ScheduleExecutor.this.context.getTaskHandlerInstance();
                            if (handlerInstance != null) {
                                if (isSuccess) {
                                    handlerInstance.onSuccess(ScheduleExecutor.this.context);
                                }
                                else {
                                    handlerInstance.onFailure(ScheduleExecutor.this.context);
                                }
                            }
                        }
                        catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                final SimpleThreadFactory stf = new SimpleThreadFactory("TaskCompletionThread");
                final Thread t = stf.newThread(runnable);
                t.run();
                this.unregisterForServerShutDownNotification();
            }
            catch (final Exception e) {
                e.printStackTrace();
                ScheduleExecutor.out.log(Level.SEVERE, "", e);
            }
        }
        finally {
            if (remove) {
                this.removeFromExecutingTask(this.context);
            }
            try {
                final Runnable runnable2 = new Runnable() {
                    @Override
                    public void run() {
                        final boolean isSuccess = ScheduleExecutor.this.context.isSuccess();
                        try {
                            final TaskCompletionHandler handlerInstance = ScheduleExecutor.this.context.getTaskHandlerInstance();
                            if (handlerInstance != null) {
                                if (isSuccess) {
                                    handlerInstance.onSuccess(ScheduleExecutor.this.context);
                                }
                                else {
                                    handlerInstance.onFailure(ScheduleExecutor.this.context);
                                }
                            }
                        }
                        catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                final SimpleThreadFactory stf2 = new SimpleThreadFactory("TaskCompletionThread");
                final Thread t2 = stf2.newThread(runnable2);
                t2.run();
                this.unregisterForServerShutDownNotification();
            }
            catch (final Exception e2) {
                e2.printStackTrace();
                ScheduleExecutor.out.log(Level.SEVERE, "", e2);
            }
        }
    }
    
    private void removeFromExecutingTask(final TaskContext ctx) {
        try {
            if (ctx != null) {
                TimeMap.getInstance(this.poolName).removeExecutingTask(ctx);
            }
        }
        catch (final Exception excp) {
            ScheduleExecutor.out.log(Level.INFO, "Exception occured while removeExecutingTask :: ", excp);
        }
    }
    
    private boolean executeNextTask(final TaskContext context) throws Throwable {
        boolean toBeRemoved = true;
        Scheduler executorBean = null;
        String beanName = "ScheduleExecutorWT";
        String execStatus = "SUCCESS";
        if (context == null) {
            return toBeRemoved;
        }
        final DataObject taskInputDO = context.getTaskInputDO();
        if (taskInputDO == null || !context.isAdminStatusEnabled()) {
            ScheduleExecutor.out.log(Level.SEVERE, "Not scheduling this TaskInput of instanceID :: [{0}], since it is not found in the DB :: [{1}]", new Object[] { context.getID(), taskInputDO });
            return toBeRemoved;
        }
        int transactionTime = 0;
        transactionTime = context.getTransactionTime();
        ScheduleExecutor.out.log(Level.FINE, "transactionTime :: [{0}]", transactionTime);
        final TransactionManager temp = PersistenceUtil.getTransactionManager();
        temp.setTransactionTimeout(PersistenceUtil.getTransactionTimeOut());
        if (transactionTime > 0) {
            temp.setTransactionTimeout(transactionTime);
        }
        else if (transactionTime < 0) {
            beanName = "ScheduleExecutorNT";
        }
        executorBean = (Scheduler)BeanUtil.lookup(beanName);
        try {
            executorBean.executeTask(context);
            context.setExecutionStatus(true);
        }
        catch (final Throwable exp) {
            execStatus = "FAILURE";
            context.setExecutionStatus(false);
            ScheduleExecutor.out.log(Level.SEVERE, "Exception in the Task:{0}", exp);
        }
        finally {
            this.removeFromExecutingTask(context);
            toBeRemoved = false;
            if (!context.getPoolName().equalsIgnoreCase("asynchThreadPool")) {
                executorBean.reschedule(context, execStatus);
            }
        }
        return toBeRemoved;
    }
    
    public String getPoolName() {
        return this.poolName;
    }
    
    @Override
    public String toString() {
        return "ScheduleExecutor-" + this.poolName + ", id: " + this.context.getID() + ", ScheduledExecutionTime : " + new Timestamp(this.context.getActualScheduleTime()).toString();
    }
    
    static {
        ScheduleExecutor.out = Logger.getLogger(ScheduleExecutor.class.getName());
        ScheduleExecutor.count = 0;
    }
}
