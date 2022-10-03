package com.me.devicemanagement.framework.webclient.support;

import java.util.Hashtable;
import java.net.UnknownHostException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.util.Properties;
import java.net.URLConnection;
import sun.misc.BASE64Encoder;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;

public class DMUploadAction
{
    private static Logger logger;
    private static long logSizeThreshold;
    public static final String UPLOAD_URL = "https://bonitas.zohocorp.com/upload_file/";
    private final byte[] buffer;
    private static DMUploadAction dmUploadAction;
    
    private DMUploadAction() {
        this.buffer = new byte[1024];
    }
    
    public static DMUploadAction getInstance() {
        if (DMUploadAction.dmUploadAction == null) {
            DMUploadAction.dmUploadAction = new DMUploadAction();
        }
        return DMUploadAction.dmUploadAction;
    }
    
    public HashMap<String, Object> openBonitasConnection() throws Exception {
        final HashMap<String, Object> connectionData = new HashMap<String, Object>();
        URLConnection conn = null;
        String proxyHost = "";
        int proxyPort = 0;
        String userName = "";
        String passwordhttp = "";
        final URL url = new URL("https://bonitas.zohocorp.com/upload_file/");
        final Properties proxyDetailsProp = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        if (!proxyDetailsProp.isEmpty()) {
            final DownloadManager downloadMgr = DownloadManager.getInstance();
            final int proxyType = DownloadManager.proxyType;
            if (proxyType == 4) {
                final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration("https://bonitas.zohocorp.com/upload_file/", proxyDetailsProp);
                proxyHost = ((Hashtable<K, String>)pacProps).get("proxyHost");
                proxyPort = Integer.parseInt(((Hashtable<K, String>)pacProps).get("proxyPort"));
            }
            else {
                proxyHost = ((Hashtable<K, String>)proxyDetailsProp).get("proxyHost");
                proxyPort = Integer.parseInt(((Hashtable<K, String>)proxyDetailsProp).get("proxyPort"));
            }
            userName = ((Hashtable<K, String>)proxyDetailsProp).get("proxyUser");
            if (userName != null && !userName.equals("")) {
                userName = ((Hashtable<K, String>)proxyDetailsProp).get("proxyUser");
                passwordhttp = ((Hashtable<K, String>)proxyDetailsProp).get("proxyPass");
            }
            final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            conn = url.openConnection(proxy);
        }
        else {
            conn = url.openConnection();
        }
        if (userName != null && !userName.equals("")) {
            final BASE64Encoder encoder = new BASE64Encoder();
            final String productType = encoder.encode((userName + ":" + passwordhttp).getBytes());
            conn.setRequestProperty("Proxy-Authorization", "Basic " + productType);
        }
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        final String boundary = "----WebKitFormBoundaryByYUQEjK0I3eWnwl";
        conn.setRequestProperty("Content-type", "multipart/form-data; boundary=" + boundary);
        conn.setRequestProperty("Cache-Control", "no-cache");
        connectionData.put("URLConnection", conn);
        connectionData.put("boundary", boundary);
        return connectionData;
    }
    
    public HashMap<String, Object> openBonitasConnectionHTTP() throws Exception {
        final HashMap<String, Object> connectionData = new HashMap<String, Object>();
        HttpURLConnection conn = null;
        String proxyHost = "";
        int proxyPort = 0;
        String userName = "";
        String passwordhttp = "";
        final URL url = new URL("https://bonitas.zohocorp.com/upload_file/");
        final Properties proxyDetailsProp = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        if (!proxyDetailsProp.isEmpty()) {
            final DownloadManager downloadMgr = DownloadManager.getInstance();
            final int proxyType = DownloadManager.proxyType;
            if (proxyType == 4) {
                final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration("https://bonitas.zohocorp.com/upload_file/", proxyDetailsProp);
                proxyHost = ((Hashtable<K, String>)pacProps).get("proxyHost");
                proxyPort = Integer.parseInt(((Hashtable<K, String>)pacProps).get("proxyPort"));
            }
            else {
                proxyHost = ((Hashtable<K, String>)proxyDetailsProp).get("proxyHost");
                proxyPort = Integer.parseInt(((Hashtable<K, String>)proxyDetailsProp).get("proxyPort"));
            }
            userName = ((Hashtable<K, String>)proxyDetailsProp).get("proxyUser");
            if (userName != null && !userName.equals("")) {
                userName = ((Hashtable<K, String>)proxyDetailsProp).get("proxyUser");
                passwordhttp = ((Hashtable<K, String>)proxyDetailsProp).get("proxyPass");
            }
            final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            conn = (HttpURLConnection)url.openConnection(proxy);
        }
        else {
            conn = (HttpURLConnection)url.openConnection();
        }
        if (userName != null && !userName.equals("")) {
            final BASE64Encoder encoder = new BASE64Encoder();
            final String productType = encoder.encode((userName + ":" + passwordhttp).getBytes());
            conn.setRequestProperty("Proxy-Authorization", "Basic " + productType);
        }
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        final String boundary = "----WebKitFormBoundaryByYUQEjK0I3eWnwl";
        conn.setRequestProperty("Content-type", "multipart/form-data; boundary=" + boundary);
        conn.setRequestProperty("Cache-Control", "no-cache");
        connectionData.put("HttpURLConnection", conn);
        connectionData.put("boundary", boundary);
        return connectionData;
    }
    
    public void writeParam(final String name, final String value, final DataOutputStream out, final String boundary) {
        try {
            out.writeBytes("content-disposition: form-data; name=\"" + name + "\"\r\n\r\n");
            out.writeBytes(value);
            out.writeBytes("\r\n--" + boundary + "\r\n");
        }
        catch (final Exception e) {
            DMUploadAction.logger.log(Level.WARNING, "SupportFileUploader -> Problem occurred in writeParam ", e);
        }
    }
    
    public void writeAdditionalInfo(final DataOutputStream out, final String boundary, final Properties additionalInfo) {
        final Set propSet = additionalInfo.keySet();
        for (final String key : propSet) {
            final String value = additionalInfo.getProperty(key);
            this.writeParam(key, value, out, boundary);
        }
    }
    
    public void writeFile(final String name, final String filePath, final DataOutputStream out, final String boundary) throws Exception {
        InputStream fis = null;
        try {
            out.writeBytes("content-disposition: form-data; name=\"" + name + "\"; filename=\"" + filePath + "\"\r\n");
            out.writeBytes("content-type: application/octet-stream\r\n\r\n");
            fis = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
            while (true) {
                synchronized (this.buffer) {
                    final int amountRead = fis.read(this.buffer);
                    if (amountRead == -1) {
                        break;
                    }
                    out.write(this.buffer, 0, amountRead);
                }
            }
            out.writeBytes("\r\n--" + boundary + "\r\n");
            DMUploadAction.logger.log(Level.INFO, "Finished writing file content.");
        }
        catch (final Exception e) {
            DMUploadAction.logger.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred", e);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e) {
                DMUploadAction.logger.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred");
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e2) {
                DMUploadAction.logger.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred");
            }
        }
    }
    
    public void writeHugeFile(final String name, final String filePath, final DataOutputStream out, final String boundary) throws Exception {
        InputStream fis = null;
        try {
            out.writeBytes("content-disposition: form-data; name=\"" + name + "\"; filename=\"" + filePath + "\"\r\n");
            out.writeBytes("content-type: application/octet-stream\r\n\r\n");
            int counter = 1;
            fis = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
            while (true) {
                synchronized (this.buffer) {
                    final int amountRead = fis.read(this.buffer);
                    if (amountRead == -1) {
                        break;
                    }
                    out.write(this.buffer, 0, amountRead);
                    if (counter % 10240 == 0) {
                        out.flush();
                        DMUploadAction.logger.log(Level.FINE, "Flush 10Mb.Size written:" + out.size());
                    }
                    ++counter;
                }
            }
            out.writeBytes("\r\n--" + boundary + "\r\n");
            out.flush();
        }
        catch (final Exception e) {
            DMUploadAction.logger.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred", e);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e) {
                DMUploadAction.logger.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred");
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception e2) {
                DMUploadAction.logger.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred");
            }
        }
    }
    
    public boolean checkUploadAccess() throws Exception {
        try {
            final URL servlet = new URL("https://bonitas.zohocorp.com/upload_file/");
            final URLConnection conn = servlet.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
        }
        catch (final UnknownHostException e) {
            DMUploadAction.logger.log(Level.SEVERE, "No access for uploading file");
            return false;
        }
        catch (final Exception ex) {
            DMUploadAction.logger.log(Level.SEVERE, "Unable to create connection to for uploading support file");
            return false;
        }
        return true;
    }
    
    public long getLogSizeThreshold() {
        return DMUploadAction.logSizeThreshold;
    }
    
    public String getUploadUrl() {
        return "https://bonitas.zohocorp.com/upload_file/";
    }
    
    static {
        DMUploadAction.logger = Logger.getLogger(DMUploadAction.class.getCanonicalName());
        DMUploadAction.logSizeThreshold = 41943040L;
        DMUploadAction.dmUploadAction = null;
    }
}
