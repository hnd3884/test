package com.me.devicemanagement.onpremise.start;

import java.util.logging.Level;
import com.me.devicemanagement.onpremise.start.util.CheckServerStatus;
import com.me.devicemanagement.onpremise.start.util.InstallUtil;
import java.util.Properties;
import com.me.devicemanagement.onpremise.start.util.ClientUtil;
import java.util.logging.Logger;

public class IsServerRunning implements OutputProcesser
{
    private static final Logger LOGGER;
    String urlStr;
    
    public IsServerRunning() {
        this.urlStr = ClientUtil.urlStr;
    }
    
    @Override
    public boolean hasProcessStarted(final Properties additionalParams) {
        try {
            final ClientUtil clientUtil = new ClientUtil();
            final int webPort = InstallUtil.getWebServerPort();
            final String url = this.urlStr;
            this.urlStr += webPort;
            final boolean canIStart = CheckServerStatus.getInstance().isServerRunningURL();
            IsServerRunning.LOGGER.log(Level.INFO, "Is Server Running ::::" + canIStart);
            IsServerRunning.LOGGER.log(Level.INFO, "THE URL STRING FOR START IS ::::" + url + new Integer(webPort));
            return canIStart;
        }
        catch (final Exception e) {
            throw new RuntimeException("Exception occured while trying to check if server is running", e);
        }
    }
    
    @Override
    public boolean processOutput(final String op) {
        return false;
    }
    
    @Override
    public boolean processError(final String op) {
        return false;
    }
    
    @Override
    public void endStringReached() {
    }
    
    @Override
    public void terminated() {
    }
    
    static {
        LOGGER = Logger.getLogger(IsServerRunning.class.getName());
    }
}
