package com.me.ems.onpremise.summaryserver.summary.probeadministration.task;

import java.util.Iterator;
import java.util.HashMap;
import com.me.ems.onpremise.summaryserver.summary.probeadministration.ProbeReachabilityChecker;
import com.me.ems.onpremise.summaryserver.summary.proberegistration.ProbeUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ProbeServerLiveStatusUpdator implements SchedulerExecutionInterface
{
    Logger logger;
    
    public ProbeServerLiveStatusUpdator() {
        this.logger = Logger.getLogger("probeActionsLogger");
    }
    
    public void executeTask(final Properties props) {
        try {
            this.logger.log(Level.INFO, "Probe Live Status Task Started");
            final HashMap probeDetails = ProbeUtil.getInstance().getAllProbeDetails();
            if (!probeDetails.isEmpty()) {
                for (final Long probeId : probeDetails.keySet()) {
                    ProbeReachabilityChecker.checkAndUpdateLiveStatus(probeId);
                }
            }
            this.logger.log(Level.INFO, "Probe Live Status Update task ended");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception occured in Probe Live Status Update task ", e);
            e.printStackTrace();
        }
    }
}
