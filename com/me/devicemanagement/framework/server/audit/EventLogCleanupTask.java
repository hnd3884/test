package com.me.devicemanagement.framework.server.audit;

import com.adventnet.ds.query.SortColumn;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.BackUpUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Calendar;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class EventLogCleanupTask implements SchedulerExecutionInterface
{
    public static final String TASK_NAME = "EventLogCleanup";
    private Logger logger;
    
    public EventLogCleanupTask() {
        this.logger = Logger.getLogger(EventLogCleanupTask.class.getName());
    }
    
    @Override
    public void executeTask(final Properties props) {
        final Long startTime = new Long(System.currentTimeMillis());
        this.logger.log(Level.INFO, "********** EventLog Cleanup Task is invoked at " + startTime + " **********");
        try {
            final String noOfDays = SyMUtil.getSyMParameter("maintain_event_log");
            if (noOfDays != null && !"".equals(noOfDays) && !"-1".equals(noOfDays)) {
                final int days = Integer.parseInt(noOfDays);
                final Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(startTime);
                cal.add(5, -1 * days);
                final long deletionTime = cal.getTime().getTime();
                final Persistence persistence = SyMUtil.getPersistence();
                final Criteria criteria = new Criteria(Column.getColumn("EventLog", "EVENT_TIMESTAMP"), (Object)new Long(deletionTime), 7);
                final SelectQuery querybackUp = this.getQueryBackUp();
                querybackUp.setCriteria(criteria);
                final BackUpUtil dcClean = ApiFactoryProvider.getBackUpUtil();
                final String filename = dcClean.executeBackUp("ActionLog", "EventLog", querybackUp, criteria, false);
                try {
                    DataAccess.delete("EventLog", criteria);
                }
                catch (final Exception e) {
                    this.logger.log(Level.WARNING, "Error while Deleting EventLog Cleanup Task.", e);
                    if (!filename.equals("")) {
                        dcClean.deleteRecentFile();
                    }
                }
                this.logger.log(Level.INFO, "EventLog entries beyond " + days + " days has been deleted");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught exception while executing EventLog Cleanup Task.", ex);
        }
        final Long endTime = new Long(System.currentTimeMillis());
        this.logger.log(Level.INFO, "********** EventLog Cleanup Task is completed at " + endTime + " **********");
    }
    
    @Override
    public String toString() {
        return "EventLogCleanupTask";
    }
    
    public SelectQuery getQueryBackUp() {
        final SelectQuery querybackUp = (SelectQuery)new SelectQueryImpl(Table.getTable("EventLog"));
        querybackUp.addJoin(new Join("EventLog", "EventCode", new String[] { "EVENT_ID" }, new String[] { "EVENT_ID" }, 1));
        querybackUp.addJoin(new Join("EventLog", "EventTimeDuration", new String[] { "EVENT_LOG_ID" }, new String[] { "EVENT_LOG_ID" }, 1));
        querybackUp.addJoin(new Join("EventLog", "ResourceEventLogRel", new String[] { "EVENT_LOG_ID" }, new String[] { "EVENT_LOG_ID" }, 1));
        querybackUp.addSelectColumn(Column.getColumn("EventLog", "EVENT_TIMESTAMP", "EVENT_TIMESTAMP"));
        querybackUp.addSelectColumn(Column.getColumn("EventCode", "EVENT_MODULE", "EVENT_MODULE"));
        querybackUp.addSelectColumn(Column.getColumn("EventLog", "EVENT_REMARKS", "EVENT_REMARKS"));
        querybackUp.addSelectColumn(Column.getColumn("EventLog", "EVENT_REMARKS_ARGS", "EVENT_REMARKS_ARGS"));
        querybackUp.addSelectColumn(Column.getColumn("EventLog", "EVENT_REMARKS_EN", "EVENT_REMARKS_EN"));
        querybackUp.addSelectColumn(Column.getColumn("EventLog", "EVENT_ID", "EVENT_ID"));
        if (CustomerInfoUtil.getInstance().isMSP()) {
            querybackUp.addJoin(new Join("EventLog", "CustomerEventLog", new String[] { "EVENT_LOG_ID" }, new String[] { "EVENT_LOG_ID" }, 1));
            querybackUp.addJoin(new Join("CustomerEventLog", "CustomerInfo", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
            querybackUp.addSelectColumn(Column.getColumn("CustomerInfo", "CUSTOMER_NAME", "CUSTOMER_NAME"));
        }
        querybackUp.addSelectColumn(Column.getColumn("EventTimeDuration", "EVENT_START_TIME", "EVENT_START_TIME"));
        querybackUp.addSelectColumn(Column.getColumn("EventTimeDuration", "EVENT_END_TIME", "EVENT_END_TIME"));
        querybackUp.addSelectColumn(Column.getColumn("EventTimeDuration", "EVENT_TIME_DURATION", "EVENT_TIME_DURATION"));
        querybackUp.addSelectColumn(Column.getColumn("ResourceEventLogRel", "RESOURCE_NAME", "RESOURCE_NAME"));
        querybackUp.addSelectColumn(Column.getColumn("ResourceEventLogRel", "DOMAIN_NETBIOS_NAME", "DOMAIN_NETBIOS_NAME"));
        querybackUp.addSelectColumn(Column.getColumn("EventLog", "LOGON_USER_NAME", "LOGON_USER_NAME"));
        querybackUp.addSelectColumn(Column.getColumn("EventLog", "EVENT_SOURCE_HOSTNAME", "EVENT_SOURCE_HOSTNAME"));
        querybackUp.addSelectColumn(Column.getColumn("EventLog", "EVENT_SOURCE_IP", "EVENT_SOURCE_IP"));
        final SortColumn sortColumn = new SortColumn(Column.getColumn("EventLog", "EVENT_TIMESTAMP"), false);
        querybackUp.addSortColumn(sortColumn);
        return querybackUp;
    }
}
