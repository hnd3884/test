package com.adventnet.taskengine;

import java.util.Hashtable;
import java.util.Collections;
import java.util.Arrays;
import java.util.Locale;
import java.io.File;
import java.util.Properties;
import com.adventnet.taskengine.util.ScheduleUtil;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.sql.Timestamp;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.ActionInfo;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.xml.ConfigurationPopulationException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.logging.Logger;
import com.adventnet.persistence.xml.ConfigurationPopulator;

public class TaskEngineConfigPopulator implements ConfigurationPopulator
{
    private static Logger logger;
    private static List<String> schTableNames;
    
    public void populate(final DataObject dobj) throws ConfigurationPopulationException {
        try {
            this.setScheduleTimeInTaskInputRows(dobj, OperationType.POPULATE);
            this.setDefaultPropsForPostgresWALBackupTask(dobj, OperationType.POPULATE);
            DataAccess.add(dobj);
        }
        catch (final DataAccessException exp) {
            throw new ConfigurationPopulationException((Throwable)exp);
        }
    }
    
    private void setScheduleTimeInTaskInputRows(final DataObject dobj, final OperationType oType) throws DataAccessException, ConfigurationPopulationException {
        final List<Row> addedTaskInputRows = new ArrayList<Row>();
        if (oType == OperationType.UPDATE) {
            final Map insertActionInfos = ((WritableDataObject)dobj).getActionsFor("insert");
            if (insertActionInfos != null) {
                final List taskInputActionInfos = insertActionInfos.get("Task_Input");
                if (taskInputActionInfos != null && taskInputActionInfos.size() > 0) {
                    for (int index = 0; index < taskInputActionInfos.size(); ++index) {
                        final ActionInfo aInfo = taskInputActionInfos.get(index);
                        addedTaskInputRows.add(aInfo.getValue());
                    }
                }
            }
            if (addedTaskInputRows.size() == 0) {
                return;
            }
        }
        final Iterator<Row> schRows = dobj.getRows("Schedule");
        while (schRows.hasNext()) {
            long nextExecTime = -1L;
            final Row schRow = schRows.next();
            final Iterator<Row> taskInputRows = dobj.getRows("Task_Input", new Criteria(Column.getColumn("Task_Input", "SCHEDULE_ID"), schRow.get(1), 0));
            if (!taskInputRows.hasNext()) {
                continue;
            }
            if (nextExecTime == -1L) {
                nextExecTime = this.getNextExecutionTime(schRow, dobj);
            }
            while (taskInputRows.hasNext()) {
                final Row taskInputRow = taskInputRows.next();
                if (oType == OperationType.UPDATE && !addedTaskInputRows.contains(taskInputRow)) {
                    continue;
                }
                taskInputRow.set("SCHEDULE_TIME", (Object)new Timestamp(nextExecTime));
                TaskEngineConfigPopulator.logger.log(Level.INFO, "After setting Schedule-Time [{0}]", taskInputRow);
                final Row scheduledTaskRow = dobj.getRow("Scheduled_Task", taskInputRow);
                final Object retryScheduleID = scheduledTaskRow.get(8);
                if (retryScheduleID == null || scheduledTaskRow.get(11) != null) {
                    continue;
                }
                scheduledTaskRow.set(11, (Object)"com.adventnet.taskengine.internal.ScheduledRetryHandler");
                TaskEngineConfigPopulator.logger.log(Level.INFO, "After setting SCHEDULED_TASK.RETRY_HANDLER :: {0}", scheduledTaskRow);
            }
        }
    }
    
    private long getNextExecutionTime(final Row schRow, final DataObject dobj) throws ConfigurationPopulationException, DataAccessException {
        final DataObject schDO = dobj.getDataObject((List)TaskEngineConfigPopulator.schTableNames, schRow);
        final Long nextExecTime = (schDO == null) ? null : ScheduleUtil.calculateNextScheduleTime(schDO, -1L, false);
        if (nextExecTime == null) {
            throw new ConfigurationPopulationException("Not able to find next-exec-time for Schedule [" + schRow + "]");
        }
        return nextExecTime;
    }
    
    private void setDefaultPropsForPostgresWALBackupTask(final DataObject data, final OperationType opType) throws DataAccessException {
        final Row ttRow = data.getRow("TaskEngine_Task", new Criteria(Column.getColumn("TaskEngine_Task", "CLASS_NAME"), (Object)"com.adventnet.taskengine.backup.PgsqlBackupTask", 0));
        if (ttRow == null) {
            return;
        }
        final Properties defaultProps = new Properties();
        defaultProps.setProperty("backup.directory", ".." + File.separator + "Backup");
        defaultProps.setProperty("fullbackup.interval", "6");
        defaultProps.setProperty("fullbackup.intervaltype", "days");
        defaultProps.setProperty("fullbackup.retaincount", "5");
        final Row tiRow = data.getRow("Task_Input", ttRow);
        Iterator iterator = data.getRows("Default_Task_Input", tiRow);
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final String key = row.get(2).toString().toLowerCase(Locale.ENGLISH);
            final String value = ((String)row.get(4)).toLowerCase(Locale.ENGLISH);
            if (key.equals("backup.directory")) {
                row.set(4, (Object)new File((String)row.get(4)).getAbsolutePath());
            }
            row.set(2, (Object)key);
            if (key.equals("fullbackup.interval")) {
                final int number = Integer.parseInt(value);
                if (number < -1) {
                    throw new IllegalArgumentException("Improper value [" + value + "] set for the parameter [fullbackup.interval]");
                }
                if (number == 0) {
                    row.set(4, (Object)"-1");
                }
            }
            else if (key.equals("fullbackup.retaincount")) {
                final int number = Integer.parseInt(value);
                if (number < -1) {
                    throw new IllegalArgumentException("Improper value [" + value + "] set for the parameter [fullbackup.retaincount]");
                }
                if (number == 0) {
                    row.set(4, (Object)"-1");
                }
            }
            else if (key.equals("fullbackup.intervaltype") && !value.equals("days") && !value.equals("backups")) {
                throw new IllegalArgumentException("Improper value [" + value + "] set for the parameter [fullbackup.intervaltype]");
            }
            if (opType == OperationType.UPDATE) {
                data.updateRow(row);
            }
            defaultProps.remove(key);
        }
        iterator = ((Hashtable<Object, V>)defaultProps).keySet().iterator();
        while (iterator.hasNext()) {
            final String key2 = iterator.next();
            final String value2 = defaultProps.getProperty(key2);
            final Row row2 = new Row("Default_Task_Input");
            row2.set(1, tiRow.get(1));
            row2.set(2, (Object)key2);
            row2.set(3, (Object)"java.lang.String");
            row2.set(4, (Object)value2);
            data.addRow(row2);
        }
    }
    
    public void update(final DataObject data) throws ConfigurationPopulationException {
        try {
            this.setScheduleTimeInTaskInputRows(data, OperationType.UPDATE);
            this.setDefaultPropsForPostgresWALBackupTask(data, OperationType.UPDATE);
            DataAccess.update(data);
        }
        catch (final DataAccessException exp) {
            throw new ConfigurationPopulationException((Throwable)exp);
        }
    }
    
    static {
        TaskEngineConfigPopulator.logger = Logger.getLogger(TaskEngineConfigPopulator.class.getName());
        TaskEngineConfigPopulator.schTableNames = Collections.unmodifiableList((List<? extends String>)Arrays.asList("Schedule", "Periodic", "Calendar", "Composite"));
    }
    
    private enum OperationType
    {
        POPULATE, 
        UPDATE;
    }
}
