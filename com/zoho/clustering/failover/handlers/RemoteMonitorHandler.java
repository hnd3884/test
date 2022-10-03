package com.zoho.clustering.failover.handlers;

import com.zoho.clustering.util.HttpMethod;
import com.zoho.clustering.util.UrlUtil;
import com.zoho.clustering.failover.ErrorCode;
import java.util.logging.Level;
import com.zoho.clustering.failover.FOS;
import java.util.logging.Logger;
import com.zoho.clustering.failover.FOSHandler;

public class RemoteMonitorHandler implements FOSHandler
{
    private static Logger logger;
    private Config config;
    
    public RemoteMonitorHandler(final Config config) {
        this.config = config;
    }
    
    @Override
    public void onStart(final FOS.Mode mode) {
        RemoteMonitorHandler.logger.log(Level.FINE, "RemoteMonitorHandler.onStart() invoked ");
    }
    
    @Override
    public void onStop(final FOS.Mode mode, final ErrorCode errorCode) {
        FOS.Console.out("RemoteMonitorHandler.onStop() invoked");
        if (mode == FOS.Mode.SLAVE) {
            final String removeMonitor = UrlUtil.createURL(this.config.masterUrl, this.config.removeURI) + "?slaveId=" + this.config.nodeId;
            if (this.sendHttpRequest(removeMonitor)) {
                RemoteMonitorHandler.logger.log(Level.INFO, "Stopped the RemoteMonintor [" + this.config.nodeId + "] on remote Master");
            }
        }
    }
    
    @Override
    public void onSlaveTakeover() {
        RemoteMonitorHandler.logger.log(Level.FINE, "RemoteMonitorHandler.onSlaveTakeover() invoked ");
    }
    
    private boolean sendHttpRequest(final String url) {
        final HttpMethod meth = new HttpMethod(url);
        meth.setConnectionTimeout(this.config.connTimeoutMillis);
        meth.setReadTimeout(this.config.readTimeoutMillis);
        meth.setHostnameVerifierClassName(this.config.hostnameVerifierClassName);
        int status;
        try {
            status = meth.execute();
        }
        catch (final Exception exp) {
            RemoteMonitorHandler.logger.log(Level.WARNING, "ERROR during RemoteMonitor HTTP request. URL: {0}. Error: {1}", new Object[] { meth.getURL(), exp.toString() });
            RemoteMonitorHandler.logger.log(Level.FINE, "", exp);
            return false;
        }
        if (status == 200) {
            return true;
        }
        RemoteMonitorHandler.logger.log(Level.WARNING, "HTTP ERROR [{0}]. RemoteMonitor HTTP request. URL: {1}", new Object[] { status, meth.getURL() });
        return false;
    }
    
    static {
        RemoteMonitorHandler.logger = Logger.getLogger(FOSHandler.class.getName());
    }
    
    public static class Config
    {
        public final String nodeId;
        public final String masterUrl;
        public final String removeURI;
        public final int connTimeoutMillis;
        public final int readTimeoutMillis;
        public final String hostnameVerifierClassName;
        
        public Config(final String nodeId, final String masterUrl, final String removeURI, final int connTimeout, final int readTimeout, final String hostnameVerifierClassName) {
            this.masterUrl = masterUrl;
            this.nodeId = nodeId;
            this.removeURI = removeURI;
            this.connTimeoutMillis = connTimeout;
            this.readTimeoutMillis = readTimeout;
            this.hostnameVerifierClassName = hostnameVerifierClassName;
        }
    }
}
