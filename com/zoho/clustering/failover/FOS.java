package com.zoho.clustering.failover;

import com.zoho.clustering.failover.logging.FOSConsoleFormatter;
import java.util.logging.LogRecord;
import java.io.IOException;
import com.zoho.clustering.util.HttpMethod;
import com.zoho.clustering.util.UrlUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FOS implements Runnable
{
    private static Logger logger;
    private Mode mode;
    private FOSConfig config;
    private FOSHandler fosHandler;
    private Thread fosThread;
    
    public FOS(final Mode mode, final FOSConfig config, final FOSHandler fosHandler) {
        this.mode = mode;
        this.config = config;
        this.fosHandler = fosHandler;
    }
    
    public Mode getMode() {
        return this.mode;
    }
    
    public FOSConfig getConfig() {
        return this.config;
    }
    
    public FOSHandler getFosListener() {
        return this.fosHandler;
    }
    
    public void start() {
        if (this.fosThread != null) {
            throw new IllegalStateException("FOS is already running");
        }
        (this.fosThread = new Thread(this, "FOS")).start();
    }
    
    @Override
    public void run() {
        Console.out("Starting the FOS service ...");
        if (!this.doPreChecks()) {
            boolean stopService = true;
            if (this.mode == Mode.MASTER) {
                Console.out("Starting as MASTER failed.. Trying to start as SLAVE.");
                this.mode = Mode.SLAVE;
                stopService = !this.doPreChecks();
                this.fosHandler = FOSMain.reinitialize(this, this.mode, this.config);
            }
            if (stopService) {
                this.cleanup();
                Console.out("FOS Service stopped");
                return;
            }
        }
        this.fosHandler.onStart(this.mode);
        Console.out("FOS service started");
        try {
            if (this.mode == Mode.MASTER) {
                this.runInMasterMode();
            }
            if (this.mode == Mode.SLAVE) {
                this.runInSlaveMode();
                this.masterModePreCheck();
                this.mode = Mode.MASTER;
                this.fosHandler.onSlaveTakeover();
                Console.out("This node has taken over as MASTER");
                this.runInMasterMode();
            }
        }
        catch (final InterruptedException ex) {}
        catch (final FOSException exp) {
            FOS.logger.log(Level.SEVERE, "", exp);
            Console.out("Stopping the FOS service. Error Code: [" + exp.getErrCode() + "]");
            this.fosHandler.onStop(this.mode, exp.getErrCode());
        }
        catch (final RuntimeException exp2) {
            FOS.logger.log(Level.SEVERE, "", exp2);
            Console.out("Stopping the FOS service. Error Code: [ERROR_GENERAL]");
            this.fosHandler.onStop(this.mode, ErrorCode.ERROR_GENERAL);
        }
        this.cleanup();
        Console.out("FOS Service stopped");
    }
    
    private boolean doPreChecks() {
        try {
            if (this.mode == Mode.MASTER) {
                this.masterModePreCheck();
            }
            else {
                this.slaveModePreCheck();
            }
            return true;
        }
        catch (final InterruptedException ex) {}
        catch (final RuntimeException exp) {
            FOS.logger.log(Level.SEVERE, "", exp);
        }
        return false;
    }
    
    public void stop() {
        if (this.fosThread != null && this.fosThread.isAlive()) {
            Console.out("Stopping the FOS service. Interrupt signal received");
            this.fosThread.interrupt();
            this.fosHandler.onStop(this.mode, ErrorCode.ERROR_INTERRUPT);
            this.fosThread = null;
        }
    }
    
    private void cleanup() {
        if (this.mode == Mode.MASTER) {
            _deleteIP(this.config.publicIP());
        }
    }
    
    private void masterModePreCheck() throws InterruptedException {
        if (isOtherNodePresentWithIP(this.config.publicIP())) {
            throw new FOSException(ErrorCode.ERROR_IP_CLASH, "Another node present in network with 'public_ip' binded to it");
        }
    }
    
    private void runInMasterMode() throws InterruptedException {
        int consecutiveFailureCount = 0;
        while (true) {
            if (_addIP(this.config.publicIP(), this.config.publicIPIfName(), this.config.publicIPNetMask())) {
                consecutiveFailureCount = 0;
            }
            else {
                ++consecutiveFailureCount;
            }
            if (consecutiveFailureCount >= this.config.ipCheckRetryCount()) {
                break;
            }
            Thread.sleep(this.config.ipCheckIntervalInSecs() * 1000);
        }
        if (!isInterfaceUp(this.config.publicIPIfName())) {
            throw new FOSException(ErrorCode.ERROR_IF_DOWN, "Network Interface Card is NOT working");
        }
        throw new FOSException(ErrorCode.ERROR_IP_BINDING, "Not able to bind the 'public_ip' to this node");
    }
    
    private static boolean isOtherNodePresentWithIP(final String ipAddr) throws InterruptedException {
        _deleteIP(ipAddr);
        for (int i = 0; i < 4; ++i) {
            final boolean isPresent = FOSUtil.getInst().ping(ipAddr);
            if (!isPresent) {
                return false;
            }
            ++i;
            Thread.sleep(1000L);
        }
        return true;
    }
    
    private static boolean isInterfaceUp(final String ifName) throws InterruptedException {
        for (int i = 0; i < 4; ++i) {
            final boolean isUp = FOSUtil.getInst().isInterfaceUp(ifName);
            if (isUp) {
                return true;
            }
            ++i;
            Thread.sleep(1000L);
        }
        return false;
    }
    
    private static boolean _addIP(final String ipAddr, final String ifName, final String netmask) {
        try {
            final int status = FOSUtil.getInst().addIP(ipAddr, ifName, netmask);
            return status == 0;
        }
        catch (final RuntimeException ignored) {
            FOS.logger.log(Level.SEVERE, "", ignored);
            return false;
        }
    }
    
    private static boolean _deleteIP(final String ipAddr) {
        try {
            final int status = FOSUtil.getInst().deleteIP(ipAddr);
            return status == 0;
        }
        catch (final RuntimeException ignored) {
            FOS.logger.log(Level.SEVERE, "", ignored);
            return false;
        }
    }
    
    private void slaveModePreCheck() {
        final int status = FOSUtil.getInst().deleteIP(this.config.publicIP());
        if (status != 0) {
            throw new FOSException(ErrorCode.ERROR_IP_UNBINDING, "Problem while starting FOS. Unbinding of public_ip failed with status [" + status + "]");
        }
    }
    
    private void runInSlaveMode() throws InterruptedException {
        int consecutiveFailureCount = 0;
        while (true) {
            if (this.doHttpPing()) {
                consecutiveFailureCount = 0;
            }
            else {
                ++consecutiveFailureCount;
            }
            if (consecutiveFailureCount > this.config.httpPollRetryCount()) {
                break;
            }
            Thread.sleep(this.config.httpPollIntervalInSecs() * 1000);
        }
        if (!isInterfaceUp(this.config.publicIPIfName())) {
            throw new FOSException(ErrorCode.ERROR_IF_DOWN, "NIC is not operational.Hence cannot takeover as Master.");
        }
        FOS.logger.log(Level.INFO, "The 'poll_url' is no longer responding.It seems the 'Master' is DOWN");
    }
    
    private boolean doHttpPing() {
        final String url = UrlUtil.createURL(this.config.masterURL(), this.config.httpPollURI()) + "?slaveId=" + this.config.nodeId();
        final HttpMethod httpMeth = new HttpMethod(url);
        httpMeth.setConnectionTimeout(this.config.httpPollConnTimeout());
        httpMeth.setReadTimeout(this.config.httpPollReadTimeout());
        httpMeth.setHostnameVerifierClassName(this.config.hostnameVerifierClassName());
        int httpStatus;
        try {
            httpStatus = httpMeth.execute();
        }
        catch (final IOException exp) {
            FOS.logger.log(Level.WARNING, "IO ERROR. http_poll request. URL: {0}. Exception: {1}", new Object[] { httpMeth.getURL(), exp.toString() });
            return false;
        }
        if (httpStatus >= 400 && httpStatus < 500) {
            throw new RuntimeException("HTTP ERROR [" + httpStatus + "]. http_poll request [" + httpMeth.getURL() + "]. Fix the http_poll url.");
        }
        if (httpStatus == 503 || httpStatus == 502 || httpStatus == 504) {
            FOS.logger.log(Level.WARNING, "HTTP ERROR [{0}]. http_poll request. URL: {1}", new Object[] { httpStatus, httpMeth.getURL() });
            return false;
        }
        return true;
    }
    
    static {
        FOS.logger = Logger.getLogger(FOS.class.getName());
    }
    
    public enum Mode
    {
        MASTER, 
        SLAVE;
    }
    
    public static class Console
    {
        public static void out(final String message) {
            final LogRecord lr = new LogRecord(Level.INFO, message);
            FOS.logger.log(lr);
            System.out.print(FOSConsoleFormatter.getInst().format(lr));
        }
        
        public static void out(final String message, final Throwable thrown) {
            final LogRecord lr = new LogRecord(Level.INFO, message);
            lr.setThrown(thrown);
            FOS.logger.log(lr);
            System.out.print(FOSConsoleFormatter.getInst().format(lr));
        }
    }
}
