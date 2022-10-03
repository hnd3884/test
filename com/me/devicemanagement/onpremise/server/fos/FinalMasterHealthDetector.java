package com.me.devicemanagement.onpremise.server.fos;

import java.util.List;
import com.adventnet.persistence.fos.DBUtil;
import java.util.logging.Level;
import com.adventnet.persistence.fos.FOSErrorCode;
import com.adventnet.persistence.fos.FOSException;
import com.adventnet.persistence.fos.FOSConfig;
import java.util.logging.Logger;
import com.adventnet.persistence.fos.slave.MasterHealthDetector;

public class FinalMasterHealthDetector implements MasterHealthDetector
{
    private static final Logger LOG;
    private FOSConfig config;
    
    public void initialize(final FOSConfig configuration) throws FOSException {
        this.config = configuration;
    }
    
    public boolean isMasterDown(final String masterIP) throws FOSException {
        try {
            return !this.checkMasterStatus(masterIP, true);
        }
        catch (final Exception exp) {
            throw new FOSException(FOSErrorCode.ERROR_MISC, exp.getMessage());
        }
    }
    
    public boolean checkMasterStatus(final String ip, final boolean returnOnMasterAlive) throws Exception {
        FinalMasterHealthDetector.LOG.log(Level.INFO, "Doing a final DB check, to confirm master status");
        List<String> healthStatus = DBUtil.getHealthStatus(ip);
        final long counterValueAtTheBeginning = Long.parseLong(healthStatus.get(1));
        Thread.sleep(this.config.pollInterval() * 1000);
        healthStatus = DBUtil.getHealthStatus(ip);
        final long counterValueAtTheEnd = Long.parseLong(healthStatus.get(1));
        if (counterValueAtTheEnd > counterValueAtTheBeginning) {
            FinalMasterHealthDetector.LOG.log(Level.INFO, "peer count incremented. previous value : [ {0} ] current value : [ {1} ]", new Object[] { counterValueAtTheBeginning, counterValueAtTheEnd });
            return true;
        }
        FinalMasterHealthDetector.LOG.log(Level.INFO, "peer count not incremented for 10 seconds. previous value : [ {0} ] current value : [ {1} ]", new Object[] { counterValueAtTheBeginning, counterValueAtTheEnd });
        return false;
    }
    
    public String getName() {
        return "FinalMasterHealthDetector";
    }
    
    static {
        LOG = Logger.getLogger("Fos");
    }
}
