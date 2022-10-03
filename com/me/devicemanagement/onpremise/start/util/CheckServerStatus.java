package com.me.devicemanagement.onpremise.start.util;

import java.io.File;
import com.adventnet.persistence.fos.FOS;
import com.adventnet.mfw.Starter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.net.ConnectException;
import java.util.logging.Level;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.util.logging.Logger;

public class CheckServerStatus
{
    private static final Logger LOGGER;
    private static String servlet;
    public static final String WEB_SETTINGS_CONF_FILE;
    private static final int RETRY_COUNT = 3;
    static int currentCount;
    private static CheckServerStatus checkServerStatusInstance;
    
    public static CheckServerStatus getInstance() {
        if (CheckServerStatus.checkServerStatusInstance == null) {
            CheckServerStatus.checkServerStatusInstance = new CheckServerStatus();
        }
        return CheckServerStatus.checkServerStatusInstance;
    }
    
    public boolean checkServerRunning(final String url) {
        BufferedInputStream is = null;
        HttpURLConnection connection = null;
        ByteArrayOutputStream bos = null;
        boolean toReturn = false;
        try {
            connection = this.openConnection(url + CheckServerStatus.servlet);
            if (connection == null) {
                return false;
            }
            connection.setRequestMethod("GET");
            connection.setRequestProperty("authToken", ClientUtil.getInternalAuthKey());
            is = new BufferedInputStream(connection.getInputStream());
            bos = new ByteArrayOutputStream();
            final byte[] buffer = new byte[4096];
            int length = -1;
            if (is.available() > 0) {
                while ((length = is.read(buffer, 0, buffer.length)) != -1) {
                    bos.write(buffer, 0, length);
                }
                final byte[] result = bos.toByteArray();
                if (result.length > 0) {
                    final String resultString = new String(result);
                    CheckServerStatus.currentCount = 0;
                    if (resultString.equals("OpsymServerRunning")) {
                        toReturn = true;
                    }
                }
            }
        }
        catch (final ConnectException ce) {
            CheckServerStatus.LOGGER.log(Level.SEVERE, "Exception in getting connection to URL{0}", ce);
        }
        catch (final IOException ie) {
            ++CheckServerStatus.currentCount;
            if (this.canCheckAgain()) {
                try {
                    Thread.sleep(500L);
                    toReturn = this.checkServerRunning(url);
                }
                catch (final InterruptedException ex) {
                    CheckServerStatus.LOGGER.log(Level.SEVERE, "Exception While checking server status{0}", ex);
                }
            }
        }
        catch (final Exception e) {
            CheckServerStatus.LOGGER.log(Level.WARNING, "Exception while checking server status{0}", e);
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
                this.closeConnection(connection);
                if (bos != null) {
                    bos.close();
                }
            }
            catch (final Exception exp) {
                CheckServerStatus.LOGGER.log(Level.SEVERE, "Exception while closing connnection{0}", exp);
            }
        }
        return toReturn;
    }
    
    private HttpURLConnection openConnection(final String urlStr) throws Exception {
        final URL urlObj = new URL(urlStr);
        final HttpURLConnection connection = (HttpURLConnection)urlObj.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }
    
    private void closeConnection(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }
    
    private boolean canCheckAgain() {
        return CheckServerStatus.currentCount < 3;
    }
    
    public boolean isServerRunning() {
        if (!Starter.checkShutdownListenerPort()) {
            return true;
        }
        try {
            final Boolean isFosEnabled = FOS.isEnabled();
            if (isFosEnabled) {
                return FOS.standAloneMasterHealthCheck();
            }
        }
        catch (final Exception ex) {
            CheckServerStatus.LOGGER.log(Level.SEVERE, "Exception while getting server status..", ex);
        }
        return false;
    }
    
    public String getFOSReqURL() {
        String URL = "";
        try {
            final FOS fosObj = new FOS();
            fosObj.initialize();
            final int portnumber = WebServerUtil.getServerPort();
            URL = "https://" + fosObj.getFOSConfig().publicIP() + ":" + portnumber;
        }
        catch (final Exception ex) {
            Logger.getLogger(CheckServerStatus.class.getName()).log(Level.SEVERE, "Exception in getting URL for Fail Over Service ", ex);
        }
        return URL;
    }
    
    public boolean isServerRunningURL() {
        Boolean isServerRunning = Boolean.FALSE;
        try {
            if (FOS.isEnabled()) {
                isServerRunning = this.checkServerRunning(this.getFOSReqURL());
            }
            else {
                String server_name;
                try {
                    server_name = WebServerUtil.getWebServerSettings().getProperty("server.ip");
                }
                catch (final Exception ex) {
                    server_name = "localhost";
                    CheckServerStatus.LOGGER.log(Level.SEVERE, "Exception while gettting server ip..", ex);
                }
                final String url = "https://" + server_name + ":";
                isServerRunning = this.checkServerRunning(url);
            }
        }
        catch (final Exception ex2) {
            CheckServerStatus.LOGGER.log(Level.SEVERE, "Exception while checking server running status using url..", ex2);
        }
        return isServerRunning;
    }
    
    static {
        LOGGER = Logger.getLogger(CheckServerStatus.class.getName());
        CheckServerStatus.servlet = "/servlet/ServerStatusServlet";
        WEB_SETTINGS_CONF_FILE = "conf" + File.separator + "websettings.conf";
        CheckServerStatus.currentCount = 0;
    }
}
