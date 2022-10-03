package com.zoho.clustering.failover.handlers;

import com.zoho.clustering.failover.FOSUtil;
import com.zoho.clustering.failover.ErrorCode;
import java.util.logging.Level;
import com.zoho.clustering.failover.FOS;
import java.util.logging.Logger;
import com.zoho.clustering.failover.FOSHandler;

public class CoreHandler implements FOSHandler
{
    private static Logger logger;
    private String appServiceName;
    private int startWaitTimeInSecs;
    private int stopWaitTimeInSecs;
    
    public CoreHandler(final String appServiceName, final int startWaitTimeInSecs, final int stopWaitTimeInSecs) {
        this.startWaitTimeInSecs = 0;
        this.stopWaitTimeInSecs = 0;
        this.appServiceName = appServiceName;
        this.startWaitTimeInSecs = startWaitTimeInSecs;
        this.stopWaitTimeInSecs = stopWaitTimeInSecs;
    }
    
    @Override
    public void onStart(final FOS.Mode mode) {
        CoreHandler.logger.log(Level.FINE, "CoreHandler.onStart() invoked");
        if (mode == FOS.Mode.MASTER) {
            this.startApplication();
        }
    }
    
    @Override
    public void onStop(final FOS.Mode mode, final ErrorCode errorCode) {
        FOS.Console.out("CoreHandler.onStop() invoked");
        if (mode == FOS.Mode.MASTER) {
            this.stopApplication();
        }
    }
    
    @Override
    public void onSlaveTakeover() {
        CoreHandler.logger.log(Level.FINE, "CoreHandler.onSlaveTakeover() invoked");
        this.startApplication();
    }
    
    private void startApplication() {
        final int status = FOSUtil.getInst().startService(this.appServiceName);
        if (status != 0) {
            throw new RuntimeException("START Service [" + this.appServiceName + "] failed with exit-status [" + status + "]");
        }
        if (this.startWaitTimeInSecs > 0) {
            CoreHandler.logger.log(Level.INFO, "StartApp [{0}]: doing sleep({1} secs)", new Object[] { this.appServiceName, this.startWaitTimeInSecs });
            this.sleep(this.startWaitTimeInSecs);
        }
        CoreHandler.logger.log(Level.INFO, "StartApp [{0}]: done", this.appServiceName);
    }
    
    private void stopApplication() {
        final int status = FOSUtil.getInst().stopService(this.appServiceName);
        if (status != 0) {
            throw new RuntimeException("STOP Service [" + this.appServiceName + "] failed with exit-status [" + status + "]");
        }
        if (this.stopWaitTimeInSecs > 0) {
            CoreHandler.logger.log(Level.INFO, "StopApp [{0}]: doing sleep({1} secs)", new Object[] { this.appServiceName, this.stopWaitTimeInSecs });
            this.sleep(this.stopWaitTimeInSecs);
        }
        CoreHandler.logger.log(Level.INFO, "StopApp [{0}]: done", this.stopWaitTimeInSecs);
    }
    
    private void sleep(final int timeInSecs) {
        try {
            Thread.sleep(timeInSecs * 1000);
        }
        catch (final InterruptedException ex) {}
    }
    
    static {
        CoreHandler.logger = Logger.getLogger(FOSHandler.class.getName());
    }
}
