package com.me.devicemanagement.onpremise.start.util;

import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.net.ConnectException;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class ClientUtil
{
    private static final Logger LOGGER;
    public static String urlStr;
    static Properties authorization;
    private String servlet;
    private static final int MAX_COUNT = 3;
    static int currentCount;
    
    public ClientUtil() {
        this.servlet = "/servlet/ServerStatusServlet";
    }
    
    public static void main(final String[] args) {
        if (args.length < 2) {
            ClientUtil.LOGGER.log(Level.INFO, "Usage : java com.adventnet.sym.server.utils.ClientUtil <server.xml path> <timeout in second> ");
            System.exit(1);
        }
        long timeout = 0L;
        try {
            final String server_name = WebServerUtil.getWebServerSettings().getProperty("server.ip");
            ClientUtil.urlStr = "http://" + server_name + ":";
        }
        catch (final Exception e) {
            ClientUtil.LOGGER.log(Level.WARNING, "Exception while getting webserver properties.. ", e);
            ClientUtil.urlStr = "http://localhost:";
        }
        final String filePath = args[0];
        final int webPort = InstallUtil.getWebServerPort();
        ClientUtil.urlStr += webPort;
        try {
            timeout = Long.parseLong(args[1]);
        }
        catch (final NumberFormatException ne) {
            ClientUtil.LOGGER.log(Level.INFO, "Timeout should be in integer value");
            System.exit(1);
        }
        final ClientUtil statusChecker = new ClientUtil();
        long startTime;
        boolean status;
        for (startTime = System.currentTimeMillis(), timeout *= 1000L, status = CheckServerStatus.getInstance().isServerRunningURL(); !status && System.currentTimeMillis() - startTime < timeout; status = CheckServerStatus.getInstance().isServerRunningURL()) {
            try {
                Thread.sleep(1000L);
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
        }
        if (status) {
            System.exit(webPort);
        }
        else {
            System.exit(-2);
        }
        System.exit(webPort);
    }
    
    public boolean isServerAlreadyRunning(final String url) {
        BufferedInputStream is = null;
        HttpURLConnection connection = null;
        ByteArrayOutputStream bos = null;
        boolean toReturn = false;
        try {
            connection = this.openConnection(url + this.servlet);
            if (connection == null) {
                return false;
            }
            connection.setRequestMethod("GET");
            connection.setRequestProperty("authToken", getInternalAuthKey());
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
                    ClientUtil.currentCount = 0;
                    if (resultString.equals("OpsymServerRunning")) {
                        toReturn = true;
                    }
                }
            }
        }
        catch (final ConnectException ex) {}
        catch (final IOException ie) {
            ++ClientUtil.currentCount;
            if (this.canCheckAgain()) {
                try {
                    Thread.sleep(500L);
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
                toReturn = this.isServerAlreadyRunning(url);
            }
        }
        catch (final Exception e2) {
            e2.printStackTrace();
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
            catch (final Exception ex2) {}
        }
        return toReturn;
    }
    
    private String getReqURL(final String serverAddress, final int serverPortNo, final String path) {
        final StringBuffer sb = new StringBuffer(30);
        sb.append("http://");
        sb.append(serverAddress);
        sb.append(":");
        sb.append(serverPortNo);
        sb.append(path);
        return sb.toString();
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
        return ClientUtil.currentCount < 3;
    }
    
    public static String getInternalAuthKey() {
        final String auKeyPath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "Tomcat" + File.separator + ".internalKey";
        try {
            if (ClientUtil.authorization == null) {
                ClientUtil.authorization = StartupUtil.getProperties(auKeyPath);
            }
        }
        catch (final Exception e) {
            ClientUtil.LOGGER.log(Level.WARNING, "Exception while generating authentication key.", e);
        }
        return (ClientUtil.authorization.getProperty("auKey") != null) ? ClientUtil.authorization.getProperty("auKey") : "";
    }
    
    static {
        LOGGER = Logger.getLogger(ClientUtil.class.getName());
        ClientUtil.authorization = null;
        ClientUtil.currentCount = 0;
    }
}
