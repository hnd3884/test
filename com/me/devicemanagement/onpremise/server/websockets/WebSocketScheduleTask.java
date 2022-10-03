package com.me.devicemanagement.onpremise.server.websockets;

import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.DeleteQueryImpl;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class WebSocketScheduleTask implements SchedulerExecutionInterface
{
    private static Logger wsFrameworkLogger;
    
    public void executeTask(final Properties properties) {
        WebSocketScheduleTask.wsFrameworkLogger.log(Level.INFO, "################# Inside scheduler execute task method ##################");
        final Long currentTime = System.currentTimeMillis();
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTime);
        cal.add(12, -1);
        final Long validTicketTime = cal.getTimeInMillis();
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("WebSocketTicketDetails");
            final Criteria criteria = new Criteria(Column.getColumn("WebSocketTicketDetails", "CREATED_TIME"), (Object)validTicketTime, 7);
            deleteQuery.setCriteria(criteria);
            SyMUtil.getPersistenceLite().delete(deleteQuery);
        }
        catch (final DataAccessException e) {
            WebSocketScheduleTask.wsFrameworkLogger.log(Level.SEVERE, "Exception occurred while deleting row/rows of ticket", (Throwable)e);
        }
    }
    
    static {
        WebSocketScheduleTask.wsFrameworkLogger = Logger.getLogger("WSFrameworkLogger");
    }
}
