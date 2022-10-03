package com.me.tools.zcutil;

import com.adventnet.persistence.DataObject;
import com.adventnet.taskengine.util.PersistenceUtil;
import java.sql.Timestamp;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;

public class CreateMickeyLiteScheduler
{
    public void addScheduler(final long hours) {
        try {
            final DataObject dobject = DataAccess.constructDataObject();
            final Row schedRow = new Row("Schedule");
            schedRow.set("SCHEDULE_NAME", (Object)"METrack");
            schedRow.set("DESCRIPTION", (Object)"To send data to Zoho Creator");
            final Row perRow = new Row("Periodic");
            perRow.set("SCHEDULE_ID", schedRow.get("SCHEDULE_ID"));
            perRow.set("TIME_PERIOD", (Object)new Long(24L));
            perRow.set("UNIT_OF_TIME", (Object)"Hours");
            dobject.addRow(schedRow);
            dobject.addRow(perRow);
            final Row taskEngineRow = new Row("TaskEngine_Task");
            taskEngineRow.set("TASK_NAME", (Object)"METrackTask");
            taskEngineRow.set("CLASS_NAME", (Object)"com.me.tools.zcutil.mickeylite.MickeLiteZCSchedule");
            dobject.addRow(taskEngineRow);
            final Row scheduleTaskRow = new Row("Scheduled_Task");
            scheduleTaskRow.set("SCHEDULE_ID", schedRow.get("SCHEDULE_ID"));
            scheduleTaskRow.set("TASK_ID", taskEngineRow.get("TASK_ID"));
            scheduleTaskRow.set("TRANSACTION_TIME", (Object)new Integer(-1));
            dobject.addRow(scheduleTaskRow);
            final Long timeLimit = new Long(System.currentTimeMillis() + 3600000L * hours);
            final Row taskInputRow = new Row("Task_Input");
            taskInputRow.set("SCHEDULE_ID", scheduleTaskRow.get("SCHEDULE_ID"));
            taskInputRow.set("TASK_ID", scheduleTaskRow.get("TASK_ID"));
            taskInputRow.set("SCHEDULE_TIME", (Object)new Timestamp(timeLimit));
            dobject.addRow(taskInputRow);
            PersistenceUtil.addSchedule(dobject);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
