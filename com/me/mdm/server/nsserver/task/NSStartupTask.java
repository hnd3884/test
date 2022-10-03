package com.me.mdm.server.nsserver.task;

import com.me.mdm.server.nsserver.NSUtil;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.start.util.NSStartUpUtil;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class NSStartupTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public NSStartupTask() {
        this.logger = Logger.getLogger("NSControllerLogger");
    }
    
    public void executeTask(final Properties taskProps) {
        try {
            final boolean bNSServiceRestart = NSStartUpUtil.isNSServiceRunning();
            final int stopResult = NSStartUpUtil.stopNSServer();
            this.logger.log(Level.INFO, "Result of Stop NS Service ( Error Code ): {0}", stopResult);
            int StartRetryCount = 0;
            Integer nsstatus = new Integer(0);
            int portstatus = -1;
            nsstatus = NSUtil.getInstance().getNSStatus();
            final int portNumber = NSUtil.getInstance().getNSPort();
            if (portNumber == 0) {
                this.logger.log(Level.INFO, "Since NS Port is obtained as zero (0) from table, we are not starting the NS.");
            }
            else {
                portstatus = NSUtil.getInstance().getNSPortStatus(portNumber);
                if (bNSServiceRestart && portstatus == 1) {
                    this.logger.log(Level.INFO, "Port {0} has not been closed since NS restart...", portNumber);
                    NSUtil.getInstance().updateNSStatus(NSUtil.NS_STATUS_PORT_EXP, portNumber);
                    final int nsMaxPortCheckRetryCount = NSUtil.getInstance().getNSMaxPortCheckRetryCountFromDB();
                    final long nsPortCheckRetryInterval = NSUtil.getInstance().getNsPortCheckRetryIntervalFromDB();
                    while (StartRetryCount <= nsMaxPortCheckRetryCount && portstatus == 1) {
                        this.logger.log(Level.INFO, "Port in timed_wait state..Sleeping for {0} seconds before rechecking port status", nsPortCheckRetryInterval / 1000L);
                        Thread.sleep(nsPortCheckRetryInterval);
                        portstatus = NSUtil.getInstance().getNSPortStatus(portNumber);
                        ++StartRetryCount;
                    }
                }
                NSUtil.getInstance().generateNSEchoSettingsConf();
                if (portstatus == 1) {
                    this.logger.log(Level.INFO, "Port is occupied by other application .. {0}", portNumber);
                    nsstatus = NSUtil.NS_STATUS_PORT_EXP;
                }
                else {
                    final int startResult = NSStartUpUtil.startNSServer();
                    this.logger.log(Level.INFO, "Result of Start NS Service ( Error Code ): {0}", startResult);
                    boolean bNSServiceRunning = true;
                    bNSServiceRunning = NSStartUpUtil.isNSServiceRunning();
                    if (startResult != 0 && !bNSServiceRunning) {
                        nsstatus = NSUtil.NS_STATUS_STOPPED;
                    }
                    else {
                        nsstatus = NSUtil.NS_STATUS_STARTED;
                        final NSMonitorTask monitorTask = NSMonitorTask.getInstance();
                        monitorTask.start();
                    }
                }
                NSUtil.getInstance().updateNSStatus(nsstatus, portNumber);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception occurred while starting the NSServer", exp);
        }
    }
}
