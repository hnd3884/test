package com.adventnet.sym.server.util;

import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerInfo;

public class SchedulerUtil extends SchedulerInfo
{
    private Logger logger;
    private static SchedulerUtil schedule;
    public static final int RESUME = 3;
    public static final int SUSPEND = 4;
    
    public SchedulerUtil() {
        this.logger = Logger.getLogger(SchedulerUtil.class.getName());
    }
    
    public static SchedulerUtil getInstance() {
        if (SchedulerUtil.schedule == null) {
            SchedulerUtil.schedule = new SchedulerUtil();
        }
        return SchedulerUtil.schedule;
    }
    
    public String getSchedulerName(final Long scheduleID) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("Schedule", "SCHEDULE_ID"), (Object)scheduleID, 0);
            final String schedulerName = (String)SyMUtil.getPersistence().get("Schedule", criteria).getRow("Schedule").get("SCHEDULE_NAME");
            return schedulerName;
        }
        catch (final Exception e) {
            e.printStackTrace();
            this.logger.log(Level.WARNING, "Exception Occurred : ", e);
            return null;
        }
    }
    
    static {
        SchedulerUtil.schedule = null;
    }
}
