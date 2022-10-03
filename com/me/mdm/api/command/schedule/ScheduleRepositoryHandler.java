package com.me.mdm.api.command.schedule;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.List;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScheduleRepositoryHandler
{
    private static ScheduleRepositoryHandler scheduleRepositoryHandler;
    private static Logger logger;
    
    public static ScheduleRepositoryHandler getInstance() {
        if (ScheduleRepositoryHandler.scheduleRepositoryHandler == null) {
            ScheduleRepositoryHandler.scheduleRepositoryHandler = new ScheduleRepositoryHandler();
        }
        return ScheduleRepositoryHandler.scheduleRepositoryHandler;
    }
    
    public Integer getScheduleExecutionTypeForSchedule(final Long scheduleID) throws Exception {
        ScheduleRepositoryHandler.logger.log(Level.INFO, "Getting execution type for scheduleID{0}", scheduleID);
        return (Integer)DBUtil.getValueFromDB("ScheduleRepository", "SCHEDULE_ID", (Object)scheduleID, "SCHEDULE_TYPE");
    }
    
    public Long getScheduleID(final String scheduleName) throws Exception {
        ScheduleRepositoryHandler.logger.log(Level.INFO, "Getting scheduleID for the scheduleName{0}", scheduleName);
        return (Long)DBUtil.getValueFromDB("ScheduleRepository", "SCHEDULE_NAME", (Object)scheduleName, "SCHEDULE_ID");
    }
    
    public List getScheduleNames(final List scheduleIDs) throws Exception {
        ScheduleRepositoryHandler.logger.log(Level.INFO, "Getting scheduleNames for the scheduleIDs{0}", scheduleIDs);
        final Criteria scheduleIDCriteria = new Criteria(new Column("ScheduleRepository", "SCHEDULE_ID"), (Object)scheduleIDs.toArray(), 8);
        final DataObject dataObject = MDMUtil.getPersistence().get("ScheduleRepository", scheduleIDCriteria);
        return DBUtil.getColumnValuesAsList(dataObject.getRows("ScheduleRepository"), "SCHEDULE_NAME");
    }
    
    public Long addSchedule(final String scheduleName, final Long nextExecutionTime, final Integer module, final Integer type) {
        ScheduleRepositoryHandler.logger.log(Level.INFO, "Adding schedule with schedule Name:{0} \t Next Execution Time {1} \t module {2} \t type {3}", new Object[] { scheduleName, nextExecutionTime, module, type });
        Long scheduleID = 1L;
        try {
            final Criteria scheduleCriteria = new Criteria(Column.getColumn("ScheduleRepository", "SCHEDULE_NAME"), (Object)scheduleName, 0);
            final DataObject existingDO = DataAccess.get("ScheduleRepository", scheduleCriteria);
            final DataObject dataObject = (DataObject)new WritableDataObject();
            Row row = existingDO.getRow("ScheduleRepository", scheduleCriteria);
            if (row == null) {
                row = new Row("ScheduleRepository");
                row.set("SCHEDULE_NAME", (Object)scheduleName);
                row.set("NEXT_EXECUTION_TIME", (Object)nextExecutionTime);
                row.set("SCHEDULE_MODULE", (Object)module);
                row.set("SCHEDULE_TYPE", (Object)type);
                dataObject.addRow(row);
            }
            else {
                row.set("NEXT_EXECUTION_TIME", (Object)nextExecutionTime);
                dataObject.updateBlindly(row);
            }
            MDMUtil.getPersistence().update(dataObject);
            scheduleID = (Long)row.get("SCHEDULE_ID");
        }
        catch (final Exception e) {
            ScheduleRepositoryHandler.logger.log(Level.SEVERE, "Exception in createSchedule", e);
        }
        return scheduleID;
    }
    
    public void deleteSchedule(final Long scheduleID) {
        ScheduleRepositoryHandler.logger.log(Level.INFO, "Deleting a schedule from scheduleRepository with ScheduleID:{0}", scheduleID);
        try {
            final Criteria c = new Criteria(new Column("ScheduleRepository", "SCHEDULE_ID"), (Object)scheduleID, 0);
            DataAccess.delete("ScheduleRepository", c);
        }
        catch (final Exception e) {
            ScheduleRepositoryHandler.logger.log(Level.SEVERE, "Exception in deleteSchedule", e);
        }
    }
    
    public String getScheduleName(final Long scheduleID) throws Exception {
        ScheduleRepositoryHandler.logger.log(Level.INFO, "Getting schedule Name for the scheduleID{0}", scheduleID);
        return (String)DBUtil.getValueFromDB("ScheduleRepository", "SCHEDULE_ID", (Object)scheduleID, "SCHEDULE_NAME");
    }
    
    static {
        ScheduleRepositoryHandler.scheduleRepositoryHandler = null;
        ScheduleRepositoryHandler.logger = Logger.getLogger("ActionsLogger");
    }
}
