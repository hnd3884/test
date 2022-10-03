package com.adventnet.persistence.fos.slave;

import com.adventnet.persistence.fos.FOSErrorCode;
import com.adventnet.persistence.fos.HttpPollUtil;
import java.util.logging.Level;
import com.adventnet.persistence.fos.FOSException;
import java.util.logging.Logger;
import com.adventnet.persistence.fos.FOSConfig;

public class HttpBasedMasterHealthDetector implements MasterHealthDetector
{
    private FOSConfig config;
    private static final Logger LOG;
    
    @Override
    public void initialize(final FOSConfig config) throws FOSException {
        this.config = config;
    }
    
    @Override
    public boolean isMasterDown(final String masterIP) throws FOSException {
        HttpBasedMasterHealthDetector.LOG.log(Level.INFO, "invoking Http based Master health detector");
        try {
            return !HttpPollUtil.getInst().isAlive();
        }
        catch (final Exception e) {
            if (e.getMessage().startsWith("HTTP ERROR")) {
                this.config.errorHandler().handleError(new FOSException(FOSErrorCode.ERROR_INCORRECT_POLL_URL, e.getMessage()));
            }
            else {
                e.printStackTrace();
            }
            return true;
        }
    }
    
    @Override
    public String getName() {
        return "HttpBasedMasterHealthDetector";
    }
    
    static {
        LOG = Logger.getLogger(HttpBasedMasterHealthDetector.class.getName());
    }
}
