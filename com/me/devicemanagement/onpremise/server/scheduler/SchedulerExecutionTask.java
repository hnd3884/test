package com.me.devicemanagement.onpremise.server.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DecimalFormat;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;
import com.adventnet.taskengine.TaskExecutionException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.TaskInfo;
import com.adventnet.taskengine.TaskContext;
import java.text.DateFormat;
import java.util.logging.Logger;
import com.adventnet.taskengine.Task;

public class SchedulerExecutionTask implements Task
{
    private Logger logger;
    private static Logger schedulerStatusLogger;
    public final String methodName = "executeTask";
    private static DateFormat simpleDateFormat;
    private static final int SYNC_TASK = 1;
    private static final int ASYN_TASK = 2;
    
    public SchedulerExecutionTask() {
        this.logger = Logger.getLogger(SchedulerExecutionTask.class.getName());
    }
    
    public void executeTask(final TaskContext tc) throws TaskExecutionException {
        final TaskInfo taskinfo = (TaskInfo)tc.getUserObject();
        String scheduleName = null;
        if (taskinfo == null) {
            try {
                final Properties taskProps = new Properties();
                String className = null;
                final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("SchedulerClasses"));
                sq.addJoin(new Join("SchedulerClasses", "Schedule", new String[] { "SCHEDULER_NAME" }, new String[] { "SCHEDULE_NAME" }, 2));
                sq.setCriteria(new Criteria(Column.getColumn("Schedule", "SCHEDULE_ID"), (Object)tc.getScheduleID(), 0));
                sq.addSelectColumn(Column.getColumn("SchedulerClasses", "SCHEDULER_CLASS_ID"));
                sq.addSelectColumn(Column.getColumn("SchedulerClasses", "SCHEDULER_NAME"));
                sq.addSelectColumn(Column.getColumn("SchedulerClasses", "CLASS_NAME"));
                final DataObject dobj = SyMUtil.getPersistence().get(sq);
                if (!dobj.isEmpty()) {
                    taskProps.setProperty("schedulerClassID", dobj.getFirstValue("SchedulerClasses", "SCHEDULER_CLASS_ID") + "");
                    taskProps.setProperty("scheduleName", (String)dobj.getFirstValue("SchedulerClasses", "SCHEDULER_NAME"));
                    scheduleName = taskProps.getProperty("scheduleName");
                    className = (String)dobj.getFirstValue("SchedulerClasses", "CLASS_NAME");
                }
                this.logger.log(Level.FINE, "Class to get Invoked is : {0}", new Object[] { className });
                final long startTime = System.currentTimeMillis();
                this.invokeMethodInClass(className, taskProps);
                final long endTime = System.currentTimeMillis();
                this.printSchedulerLogger(1, scheduleName, className, startTime, endTime);
            }
            catch (final Exception ex) {
                Logger.getLogger(SchedulerExecutionTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else {
            final Properties props = taskinfo.userProps;
            final String className = props.getProperty("actual_className");
            this.logger.log(Level.FINE, "Class to get Invoked is : {0}", new Object[] { className });
            final long startTime2 = System.currentTimeMillis();
            this.invokeMethodInClass(className, props);
            final long endTime2 = System.currentTimeMillis();
            final String[] classesWithPackages = className.split("\\.");
            final String taskName = classesWithPackages[classesWithPackages.length - 1].split("Task")[0];
            this.printSchedulerLogger(2, taskName, className, startTime2, endTime2);
        }
    }
    
    private void invokeMethodInClass(final String classname, final Properties props) {
        this.logger.log(Level.FINE, "Class to get Invoked is : {0}", new Object[] { classname });
        try {
            final SchedulerExecutionInterface schedulerInterface = (SchedulerExecutionInterface)Class.forName(classname).newInstance();
            schedulerInterface.executeTask(props);
        }
        catch (final ClassNotFoundException ex) {
            Logger.getLogger(SchedulerExecutionTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception occured while invoking executeTask Method in class", e);
        }
    }
    
    public void stopTask() throws TaskExecutionException {
    }
    
    public void printSchedulerLogger(final int syncOrAsyn, final String taskName, final String className, final long startTime, final long endTime) {
        final DecimalFormat df = new DecimalFormat("#.###");
        SchedulerExecutionTask.schedulerStatusLogger.log(Level.INFO, "{0}||{1}||{2}||{3}||{4}||{5}|", new Object[] { String.format("%-5s", syncOrAsyn), String.format("%-50s", taskName), String.format("%-90s", className), String.format("%-25s", SchedulerExecutionTask.simpleDateFormat.format(new Date(startTime))), String.format("%-25s", SchedulerExecutionTask.simpleDateFormat.format(new Date(endTime))), String.format("%-10s", df.format((endTime - startTime) / 1000.0)) });
    }
    
    static {
        SchedulerExecutionTask.schedulerStatusLogger = Logger.getLogger("schedulerStatusLogger");
        SchedulerExecutionTask.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    }
}
