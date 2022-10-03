package com.adventnet.sym.server.admin;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import com.adventnet.sym.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.util.SchedulerUtil;
import java.util.Iterator;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class DMApplnHandlerForProductChange
{
    private static Logger logger;
    
    public static void changeSchedulerStatusForMDM(final boolean dcStatus) {
        final Properties tasksList = new Properties();
        try {
            String taskFilePath = new File(System.getProperty("server.home")).getCanonicalPath();
            taskFilePath = taskFilePath + File.separator + "conf" + File.separator + "dcscheduletasks.properties";
            final File f = new File(taskFilePath);
            if (!f.exists()) {
                DMApplnHandlerForProductChange.logger.log(Level.INFO, "File does not exist at path : {0}\n SCHEDULER STATUS NOT CHANGED", taskFilePath);
                return;
            }
            tasksList.load(new FileInputStream(taskFilePath));
            for (final String key : tasksList.stringPropertyNames()) {
                changeSchedulerState(key, dcStatus);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static void changeSchedulerState(final String taskName, final boolean dcStatus) throws DataAccessException {
        final SchedulerUtil schedulerUtil = new SchedulerUtil();
        final List schedulerList = DCLicenseHandler.getSchedulerList(taskName);
        DMApplnHandlerForProductChange.logger.log(Level.SEVERE, "SuspendScheduler invoked.. taskName->{0}, scheduler list-{1} ", new Object[] { taskName, schedulerList });
        final int status = dcStatus ? 3 : 4;
        final Iterator list = schedulerList.iterator();
        while (list.hasNext()) {
            final String schedulerName = String.valueOf(list.next());
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("Scheduled_Task"));
            sq.addJoin(new Join("Scheduled_Task", "Schedule", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 2));
            sq.setCriteria(new Criteria(Column.getColumn("Schedule", "SCHEDULE_NAME"), (Object)schedulerName, 0));
            sq.addSelectColumn(Column.getColumn("Scheduled_Task", "*"));
            sq.addSelectColumn(Column.getColumn("Schedule", "*"));
            final DataObject dobj = SyMUtil.getPersistence().get(sq);
            final Row schRow = dobj.getFirstRow("Scheduled_Task");
            schRow.set("ADMIN_STATUS", (Object)status);
            dobj.updateRow(schRow);
            SyMUtil.getPersistence().update(dobj);
        }
    }
    
    static {
        DMApplnHandlerForProductChange.logger = Logger.getLogger(DMApplnHandlerForProductChange.class.getName());
    }
}
