package com.zoho.tools.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.Authenticator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkUtil
{
    private static final Logger LOGGER;
    
    public static ProxyProperties getProxy() {
        ProxyProperties proxy = null;
        try {
            proxy = ProxyProperties.read();
        }
        catch (final Exception e) {
            NetworkUtil.LOGGER.log(Level.SEVERE, "Exception occurred while getting proxy..", e);
        }
        return proxy;
    }
    
    private static void authenticate(final String userName, final String password) {
        if (userName != null && !userName.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
            Authenticator.setDefault(new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, password.toCharArray());
                }
            });
        }
    }
    
    public static boolean urlReachable(final String urlString, final ProxyProperties props) {
        HttpURLConnection huc = null;
        try {
            huc = getConnection(urlString, props);
            if (huc == null) {
                return false;
            }
            huc.setRequestMethod("HEAD");
            return huc.getResponseCode() == 200;
        }
        catch (final Exception uhe) {
            NetworkUtil.LOGGER.log(Level.SEVERE, "Exception occurred while checking url reachability.", uhe);
            return false;
        }
        finally {
            if (huc != null) {
                huc.disconnect();
            }
        }
    }
    
    public static HttpURLConnection getConnection(final String address, final ProxyProperties props) throws Exception {
        final URL url = new URL(address);
        Proxy proxy = null;
        String tunnelingProperty = null;
        final String tunnelingPropertyKey = "jdk.http.auth.tunneling.disabledSchemes";
        boolean isPropertyPresent = false;
        HttpURLConnection conn;
        try {
            if (props != null) {
                if (System.getProperties().contains(tunnelingPropertyKey)) {
                    tunnelingProperty = System.getProperty(tunnelingPropertyKey);
                    isPropertyPresent = true;
                }
                System.setProperty(tunnelingPropertyKey, "");
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(props.getProxyAddress(), Integer.parseInt(props.getProxyPort())));
                authenticate(props.getProxyUserName(), props.getProxyPass());
            }
            conn = (HttpURLConnection)url.openConnection((proxy == null) ? Proxy.NO_PROXY : proxy);
            conn.setConnectTimeout(Integer.parseInt(System.getProperty("network.timeout", "5")) * 1000);
        }
        finally {
            if (isPropertyPresent) {
                System.setProperty(tunnelingPropertyKey, tunnelingProperty);
            }
            else {
                System.getProperties().remove(tunnelingPropertyKey);
            }
        }
        return conn;
    }
    
    public static void downloadFile(final String downloadFilePath, final HttpURLConnection conn) throws Exception {
        try (final BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(Paths.get(downloadFilePath, new String[0]).toFile()));
             final InputStream in = conn.getInputStream()) {
            final byte[] buffer = new byte[1024];
            long numWritten = 0L;
            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                output.write(buffer, 0, numRead);
                numWritten += numRead;
            }
            NetworkUtil.LOGGER.log(Level.INFO, "Downloaded {0} of size {1} bytes.", new Object[] { downloadFilePath, numWritten });
        }
    }
    
    static {
        LOGGER = Logger.getLogger(NetworkUtil.class.getName());
    }
}
