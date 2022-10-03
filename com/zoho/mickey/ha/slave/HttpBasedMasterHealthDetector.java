package com.zoho.mickey.ha.slave;

import com.zoho.mickey.ha.HAErrorCode;
import com.zoho.mickey.ha.HttpPollUtil;
import java.util.logging.Level;
import com.zoho.mickey.ha.HAException;
import java.util.logging.Logger;
import com.zoho.mickey.ha.HAConfig;

public class HttpBasedMasterHealthDetector implements MasterHealthDetector
{
    private HAConfig config;
    private static final Logger LOG;
    
    @Override
    public void initialize(final HAConfig config) throws HAException {
        this.config = config;
    }
    
    @Override
    public boolean isMasterDown(final String masterIP) throws HAException {
        HttpBasedMasterHealthDetector.LOG.log(Level.INFO, "invoking Http based Master health detector");
        try {
            return !HttpPollUtil.getInst().isAlive();
        }
        catch (final Exception e) {
            if (e.getMessage().startsWith("HTTP ERROR")) {
                this.config.errorHandler().handleError(new HAException(HAErrorCode.ERROR_INCORRECT_POLL_URL, e.getMessage()));
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
