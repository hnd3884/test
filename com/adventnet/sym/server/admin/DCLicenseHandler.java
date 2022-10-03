package com.adventnet.sym.server.admin;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.license.LicenseProvider;

public class DCLicenseHandler extends LicenseProvider
{
    private static Logger logger;
    
    protected static List getSchedulerList(final String taskName) {
        final ArrayList schedulerList = new ArrayList();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("Scheduled_Task"));
            final Join join = new Join("Scheduled_Task", "Schedule", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 2);
            final Join join2 = new Join("Scheduled_Task", "TaskEngine_Task", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 2);
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            sq.addJoin(join);
            sq.addJoin(join2);
            final Criteria crit = new Criteria(Column.getColumn("TaskEngine_Task", "TASK_NAME"), (Object)taskName, 0);
            sq.setCriteria(crit);
            final DataObject schedulerDO = SyMUtil.getPersistence().get(sq);
            if (!schedulerDO.isEmpty()) {
                final Iterator rows = schedulerDO.getRows("Schedule");
                while (rows.hasNext()) {
                    final Row schedulerRow = rows.next();
                    schedulerList.add(schedulerRow.get("SCHEDULE_NAME"));
                }
            }
        }
        catch (final Exception e) {
            DCLicenseHandler.logger.log(Level.SEVERE, "Exception occured while getting scheduler names for corresponding task name.taskName->{0} ,schedulerList->{1}", new Object[] { taskName, schedulerList });
            DCLicenseHandler.logger.log(Level.SEVERE, "", e);
        }
        return schedulerList;
    }
    
    static {
        DCLicenseHandler.logger = Logger.getLogger(DCLicenseHandler.class.getName());
    }
}
