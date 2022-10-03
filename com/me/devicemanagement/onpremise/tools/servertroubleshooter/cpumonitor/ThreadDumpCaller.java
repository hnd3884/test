package com.me.devicemanagement.onpremise.tools.servertroubleshooter.cpumonitor;

import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.STSToolUtil;
import java.io.File;
import java.util.Properties;
import java.net.URLConnection;
import com.me.devicemanagement.onpremise.start.util.ClientUtil;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadDumpCaller
{
    private static final Logger LOGGER;
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String CONF_FODLER_NAME = "conf";
    private static final String WS_FILENAME = "websettings.conf";
    private static final String SERVER_IP_KEY = "server.ip";
    private static final String HTTP_PORT_KEY = "http.port";
    private static final String THREADDUMP_SERVLET_URL = "/ThreadDump";
    
    public void logThreadDump() {
        URLConnection myURLConnection = null;
        try {
            ThreadDumpCaller.LOGGER.log(Level.INFO, "Calling ThreadDump...");
            final URL myURL = new URL(this.getThreadDumpURL());
            myURLConnection = myURL.openConnection();
            myURLConnection.setRequestProperty("authToken", ClientUtil.getInternalAuthKey());
            myURLConnection.connect();
            myURLConnection.getContent();
            ThreadDumpCaller.LOGGER.log(Level.INFO, "ThreadDump called");
        }
        catch (final Exception e) {
            ThreadDumpCaller.LOGGER.log(Level.SEVERE, "Exception while calling servlet to take threaddump", e);
        }
    }
    
    private String getThreadDumpURL() {
        String urlAsString = null;
        final StringBuffer url = new StringBuffer();
        url.append("http").append("://").append(this.getHostName()).append(":").append(this.getPort()).append("/ThreadDump");
        urlAsString = url.toString();
        ThreadDumpCaller.LOGGER.log(Level.INFO, "ThreadDump URL : " + urlAsString);
        return urlAsString;
    }
    
    private String getHostName() {
        String hostName = this.getWSProps().getProperty("server.ip");
        if (hostName == null) {
            hostName = "localhost";
        }
        return hostName;
    }
    
    private String getPort() {
        String port = this.getWSProps().getProperty("http.port");
        if (port == null) {
            port = "8020";
        }
        return port;
    }
    
    private Properties getWSProps() {
        ThreadDumpCaller.LOGGER.log(Level.FINE, "Reading {0} file...", "websettings.conf");
        Properties wsProps = null;
        final String wsFilePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "websettings.conf";
        try {
            wsProps = STSToolUtil.getPropsFromFile(wsFilePath);
        }
        catch (final Exception ex) {
            ThreadDumpCaller.LOGGER.log(Level.WARNING, "Caught exception in loading WS props : ", ex);
        }
        return wsProps;
    }
    
    static {
        LOGGER = Logger.getLogger("CPUUsageLogger");
    }
}
