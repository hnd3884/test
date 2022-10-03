package com.me.mdm.api.command.schedule;

import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.List;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScheduleMapperHandler
{
    private static ScheduleMapperHandler scheduleMapperHandler;
    private static Logger logger;
    
    public static ScheduleMapperHandler getInstance() {
        if (ScheduleMapperHandler.scheduleMapperHandler == null) {
            ScheduleMapperHandler.scheduleMapperHandler = new ScheduleMapperHandler();
        }
        return ScheduleMapperHandler.scheduleMapperHandler;
    }
    
    public void deleteMappedSchedule(final Long executionScheduleId) {
        try {
            ScheduleMapperHandler.logger.log(Level.INFO, "deleting ScheduleMapper entry for the executionID{0}", executionScheduleId);
            final Criteria c = new Criteria(new Column("ScheduleMapper", "EXECUTION_SCHEDULE_ID"), (Object)executionScheduleId, 0);
            DataAccess.delete("ScheduleMapper", c);
        }
        catch (final Exception e) {
            ScheduleMapperHandler.logger.log(Level.SEVERE, "Exception in deleteMappedSchedule", e);
        }
    }
    
    public List getPreScheduleIdsForExecScheduleIds(final List execScheduleIDs) throws DataAccessException {
        ScheduleMapperHandler.logger.log(Level.INFO, "getting preschedule Ids for the given executionIDs{0}", execScheduleIDs);
        final Criteria execScheduleCriteria = new Criteria(new Column("ScheduleMapper", "EXECUTION_SCHEDULE_ID"), (Object)execScheduleIDs.toArray(), 8);
        final DataObject dataObject = MDMUtil.getPersistence().get("ScheduleMapper", execScheduleCriteria);
        return DBUtil.getColumnValuesAsList(dataObject.getRows("ScheduleMapper"), "SETUP_SCHEDULE_ID");
    }
    
    public Long getExecutionScheduleId(final Long preScheduleId) throws Exception {
        ScheduleMapperHandler.logger.log(Level.INFO, "getting execution schedule Id for the given preScheduleID{0}", preScheduleId);
        return (Long)DBUtil.getValueFromDB("ScheduleMapper", "SETUP_SCHEDULE_ID", (Object)preScheduleId, "EXECUTION_SCHEDULE_ID");
    }
    
    public Long getPreScheduleId(final Long executionScheduleId) throws Exception {
        ScheduleMapperHandler.logger.log(Level.INFO, "Getting preScheduleID for executionScheduleID:{0}", executionScheduleId);
        return (Long)DBUtil.getValueFromDB("ScheduleMapper", "EXECUTION_SCHEDULE_ID", (Object)executionScheduleId, "SETUP_SCHEDULE_ID");
    }
    
    public void addOrUpdateScheduleMapping(final Long scheduleId, final Long preScheduleId) {
        try {
            ScheduleMapperHandler.logger.log(Level.INFO, "Adding a scheduleMapper entry with scheduleID:{0} and preScheduleID{1}", new Object[] { scheduleId, preScheduleId });
            final Row r = new Row("ScheduleMapper");
            r.set("EXECUTION_SCHEDULE_ID", (Object)scheduleId);
            r.set("SETUP_SCHEDULE_ID", (Object)preScheduleId);
            final DataObject dataObject = (DataObject)new WritableDataObject();
            dataObject.addRow(r);
            MDMUtil.getPersistence().add(dataObject);
        }
        catch (final DataAccessException e) {
            ScheduleMapperHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateScheduleMapping", (Throwable)e);
        }
    }
    
    static {
        ScheduleMapperHandler.scheduleMapperHandler = null;
        ScheduleMapperHandler.logger = Logger.getLogger("ActionsLogger");
    }
}
