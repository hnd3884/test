package com.adventnet.sym.server.admin.task;

import com.adventnet.taskengine.TaskExecutionException;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.twofactor.TwoFactorPassword;
import com.me.devicemanagement.onpremise.server.dbtuning.MssqlTxLogMaintainanceUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DCGlobalMonthlyTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public DCGlobalMonthlyTask() {
        this.logger = Logger.getLogger(DCGlobalMonthlyTask.class.getName());
    }
    
    public void executeTask(final Properties taskProps) {
        try {
            if (DBUtil.getActiveDBName().equals("mssql")) {
                MssqlTxLogMaintainanceUtil.mssqlTxLogTask();
            }
            final TwoFactorPassword twoFactorPassword = new TwoFactorPassword();
            twoFactorPassword.twoFactorTokenCleanUp();
        }
        catch (final Exception ex) {
            Logger.getLogger(DCGlobalMonthlyTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void stopTask() throws TaskExecutionException {
    }
    
    @Override
    public String toString() {
        return "DCGlobalMonthlyTask";
    }
}
