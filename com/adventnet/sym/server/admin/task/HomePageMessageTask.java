package com.adventnet.sym.server.admin.task;

import com.adventnet.sym.webclient.common.SYMClientUtil;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class HomePageMessageTask implements SchedulerExecutionInterface
{
    public static final String TASK_NAME = "HomePageMessageTask";
    private Logger logger;
    
    public HomePageMessageTask() {
        (this.logger = Logger.getLogger(HomePageMessageTask.class.getName())).log(Level.INFO, "HomePageMessageTask() instance created.");
    }
    
    public void executeTask(final Properties taskProps) {
        final String activedb = DBUtil.getActiveDBName();
        SYMClientUtil.checkFreeDiskSpaceAvailableStatus(activedb, (boolean)Boolean.FALSE);
        if (activedb.equalsIgnoreCase("mssql")) {
            this.logger.log(Level.INFO, "Going to Check MSSQL DB Transaction Log Size ");
            SYMClientUtil.checkTransactionLogSizeforMSSQL();
        }
    }
}
