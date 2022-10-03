package com.adventnet.persistence.fos.slave;

import java.util.List;
import java.util.logging.Level;
import com.adventnet.persistence.fos.DBUtil;
import com.adventnet.persistence.fos.FOSErrorCode;
import com.adventnet.persistence.fos.FOSException;
import com.adventnet.persistence.fos.FOSConfig;
import java.util.logging.Logger;

public class CoreMasterHealthDetector implements MasterHealthDetector
{
    private static final Logger LOG;
    private FOSConfig config;
    
    @Override
    public void initialize(final FOSConfig configuration) throws FOSException {
        this.config = configuration;
    }
    
    @Override
    public boolean isMasterDown(final String masterIP) throws FOSException {
        try {
            return this.checkMasterStatus(masterIP, false);
        }
        catch (final Exception exp) {
            throw new FOSException(FOSErrorCode.ERROR_MISC, exp.getMessage());
        }
    }
    
    public boolean checkMasterStatus(final String ip, final boolean returnOnMasterAlive) throws Exception {
        long prevCounterValue = Long.MAX_VALUE;
        int consecutiveFailureCount = -1;
        while (consecutiveFailureCount < this.config.pollFailureRetryCount()) {
            final List<String> healthStatus = DBUtil.getHealthStatus(ip);
            final String lastSeenStatus = healthStatus.get(0);
            final long curCounterValue = Long.parseLong(healthStatus.get(1));
            if (lastSeenStatus == null || lastSeenStatus.equals("down")) {
                CoreMasterHealthDetector.LOG.log(Level.INFO, "Peer status became down or peer entry is missing");
                return true;
            }
            if (curCounterValue > prevCounterValue) {
                consecutiveFailureCount = 0;
                CoreMasterHealthDetector.LOG.log(Level.FINER, "peer count incremented. previous value : [ {0} ] current value : [ {1} ]", new Object[] { prevCounterValue, curCounterValue });
                if (returnOnMasterAlive) {
                    CoreMasterHealthDetector.LOG.log(Level.INFO, "Master is alive..");
                    return false;
                }
            }
            else {
                ++consecutiveFailureCount;
                CoreMasterHealthDetector.LOG.log(Level.SEVERE, "Peer counter not incremented.. Retrying after {0} seconds", new Object[] { this.config.pollInterval() });
            }
            prevCounterValue = curCounterValue;
            Thread.sleep(this.config.pollInterval() * 1000);
        }
        if (consecutiveFailureCount >= this.config.pollFailureRetryCount()) {
            CoreMasterHealthDetector.LOG.log(Level.SEVERE, "Peer Counter has not been updated for more than {0} seconds, Hence Peer is assumed to be down", new Object[] { this.config.pollInterval() * this.config.pollFailureRetryCount() });
            return true;
        }
        return false;
    }
    
    @Override
    public String getName() {
        return "CoreMasterHealthDetector";
    }
    
    static {
        LOG = Logger.getLogger(CoreMasterHealthDetector.class.getName());
    }
}
