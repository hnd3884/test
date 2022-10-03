package com.me.ems.onpremise.summaryserver.probe.probeadministration.task;

import com.me.ems.onpremise.summaryserver.probe.probeadministration.SummaryServerReachabilityChecker;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class SummaryServerLiveStatusUpdator implements SchedulerExecutionInterface
{
    Logger logger;
    
    public SummaryServerLiveStatusUpdator() {
        this.logger = Logger.getLogger("probeActionsLogger");
    }
    
    public void executeTask(final Properties props) {
        this.logger.log(Level.INFO, "Summary Live status Task Update Started");
        SummaryServerReachabilityChecker.checkAndUpdateLiveStatus();
        this.logger.log(Level.INFO, "Summary Live status Task Update Ended");
    }
}
