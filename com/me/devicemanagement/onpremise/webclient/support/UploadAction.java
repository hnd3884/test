package com.me.devicemanagement.onpremise.webclient.support;

import java.util.Hashtable;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.net.HttpURLConnection;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import java.net.UnknownHostException;
import java.net.URL;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Properties;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.util.Map;
import com.me.devicemanagement.onpremise.server.util.FwsUtil;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.adventnet.persistence.fos.FOS;
import com.me.devicemanagement.onpremise.properties.util.GeneralPropertiesLoader;
import com.adventnet.i18n.I18N;
import java.io.DataOutputStream;
import java.net.URLConnection;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.onpremise.server.service.DMOnPremisetHandler;
import java.io.File;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.webclient.support.DMUploadAction;
import java.util.logging.Logger;

public class UploadAction
{
    private static String className;
    private static Logger logg;
    private static String toAddress;
    private static boolean isDbLocked;
    private static boolean isDebugFileUpload;
    private static boolean isFosTrailLicense;
    private static boolean isFwsTrailLicense;
    private static boolean withoutPIData;
    private static DMUploadAction dmUploadAction;
    private static byte[] buffer;
    
    public static boolean uploadSupportFile(final String userAgent, final String supportFile, final String fromAddress, final String userMessage) throws Exception {
        return upload(supportFile, fromAddress, userMessage, userAgent);
    }
    
    public static synchronized boolean uploadSupportFile(final HttpServletRequest request, final String supportFile, final String fromAddress, final String userMessage) throws Exception {
        String userAgent = "";
        try {
            userAgent = request.getHeader("User-Agent");
        }
        catch (final UnsupportedOperationException var9) {
            userAgent = request.getAttribute("User-Agent").toString();
        }
        return upload(supportFile, fromAddress, userMessage, userAgent);
    }
    
    public static boolean upload(final String supportFile, final String fromAddress, final String userMessage, final String userAgent) throws Exception {
        boolean upload = false;
        final String browserVersion = getBrowserInfo(userAgent);
        final long fileSize = ApiFactoryProvider.getFileAccessAPI().getFileSize(supportFile);
        if (fileSize < UploadAction.dmUploadAction.getLogSizeThreshold()) {
            UploadAction.logg.log(Level.INFO, "Logs Size is small. So moving to URLConnection Method doUpload.");
            upload = doUpload(supportFile, fromAddress, userMessage, browserVersion);
        }
        else {
            UploadAction.logg.log(Level.INFO, "Logs Size is Huge. So moving to HTTPUrlConnection Method doUploadBigSize");
            upload = doUploadBigSize(supportFile, fromAddress, userMessage, browserVersion);
        }
        return upload;
    }
    
    public static synchronized boolean doUpload(String supportFile, final String fromAddress, final String userMessage, final String browserVersion, final boolean acknowledgment) throws Exception {
        if (!acknowledgment) {
            UploadAction.isDbLocked = true;
        }
        supportFile = ".." + File.separator + "logs" + File.separator + supportFile;
        return doUpload(supportFile, fromAddress, userMessage, browserVersion);
    }
    
    public static synchronized boolean doUpload(String supportFile, final String fromAddress, final String toaddress, final String userMessage, final String browserVersion) throws Exception {
        if (toaddress != null) {
            UploadAction.toAddress = toaddress;
        }
        supportFile = ".." + File.separator + "logs" + File.separator + supportFile;
        return doUpload(supportFile, fromAddress, userMessage, browserVersion);
    }
    
    public static synchronized boolean doUpload(final String fromAddress, final Boolean acknowledgement) throws Exception {
        return doUpload(fromAddress, acknowledgement, false);
    }
    
    public static synchronized boolean doUpload(final String fromAddress, final Boolean fosAcknowledgement, final Boolean fwsAcknowledgement) throws Exception {
        checkUploadAccess();
        DMOnPremisetHandler.createServerInfoConfWithoutPI();
        final String supportFile = ".." + File.separator + "logs" + File.separator + "server_info_withoutPI.props";
        if (fosAcknowledgement) {
            UploadAction.isFosTrailLicense = true;
        }
        if (fwsAcknowledgement) {
            UploadAction.isFwsTrailLicense = true;
        }
        final boolean returnValue = doUpload(supportFile, fromAddress, "", "");
        DMOnPremisetHandler.deleteServerInfoConfWithoutPI();
        return returnValue;
    }
    
    public static synchronized boolean doUpload(final String supportFile, final String fromAddress, final String userMessage, final String browserVersion) throws Exception {
        DataOutputStream out = null;
        BufferedInputStream in = null;
        ByteArrayOutputStream bos = null;
        URLConnection conn = null;
        try {
            final Properties productProps = SyMUtil.getProductInfo();
            final String somProps = ((Hashtable<K, String>)productProps).get("som");
            final String[] somPropsArray = somProps.split("metrId");
            ((Hashtable<String, String>)productProps).put("som", somPropsArray[0]);
            final HashMap<String, Object> connectionData = UploadAction.dmUploadAction.openBonitasConnection();
            conn = connectionData.get("URLConnection");
            final String boundary = connectionData.get("boundary");
            out = new DataOutputStream(conn.getOutputStream());
            UploadAction.logg.log(Level.INFO, "Writing Mail Related Information");
            out.writeBytes("--" + boundary + "\r\n");
            writeParam("fromAddress", fromAddress, out, boundary);
            writeParam("toAddress", I18N.getMsg(UploadAction.toAddress, new Object[0]), out, boundary);
            writeParam("todo", "upload", out, boundary);
            writeParam("userMessage", userMessage, out, boundary);
            if (UploadAction.isDbLocked) {
                writeParam("informcustomer", "no", out, boundary);
                writeParam("subject", "DB_LOCK_REPORT_DETAILS", out, boundary);
                UploadAction.isDbLocked = Boolean.FALSE;
            }
            if (UploadAction.isFosTrailLicense) {
                final String productDisplayName = GeneralPropertiesLoader.getInstance().getProperties().getProperty("displayname");
                writeParam("informcustomer", "no", out, boundary);
                writeParam("subject", productDisplayName.toUpperCase() + " - FAILOVER SUPPORT", out, boundary);
                UploadAction.isFosTrailLicense = Boolean.FALSE;
            }
            if (UploadAction.isFwsTrailLicense) {
                final String productDisplayName = GeneralPropertiesLoader.getInstance().getProperties().getProperty("displayname");
                writeParam("informcustomer", "no", out, boundary);
                writeParam("subject", productDisplayName.toUpperCase() + " - FORWARDING SERVER SUPPORT", out, boundary);
                UploadAction.isFwsTrailLicense = Boolean.FALSE;
            }
            if (FOS.isEnabled()) {
                writeParam("failoverEnabled", "yes", out, boundary);
                final String failoverStatus = SupportFileCreation.getInstance().getFailoverStatus();
                if (failoverStatus == "") {
                    writeParam("failover logs uploaded", "yes", out, boundary);
                }
                else {
                    writeParam("failover logs uploaded", "no", out, boundary);
                }
            }
            UploadAction.logg.log(Level.INFO, "Finished writing Mail Related Information, Now Uploading File Starts");
            writeFile("uploadfile", supportFile, out, boundary);
            UploadAction.logg.log(Level.INFO, "Writing Customer Details Starts.");
            final String buildnumber = productProps.getProperty("buildnumber");
            if (buildnumber != null) {
                writeParam("Build Number", buildnumber, out, boundary);
            }
            final String licenseType = productProps.getProperty("licenseType");
            if (licenseType != null) {
                writeParam("License Type", licenseType, out, boundary);
            }
            final String productType = productProps.getProperty("productType");
            if (productType != null) {
                writeParam("Product Type", productType, out, boundary);
            }
            if (licenseType.equals("R")) {
                final String licensedTo = productProps.getProperty("licensedTo");
                if (licensedTo != null) {
                    writeParam("Licensed To", licensedTo, out, boundary);
                }
            }
            String som = productProps.getProperty("som");
            if (browserVersion != null) {
                som = som + "|br-" + browserVersion;
            }
            if (som != null) {
                writeParam("som", som, out, boundary);
            }
            final String pkg = SyMUtil.getInstallationProperty("pkg");
            if (pkg != null && pkg.length() > 0) {
                writeParam("pkg", pkg, out, boundary);
            }
            final String lang = productProps.getProperty("lang");
            if (lang != null) {
                writeParam("lang", lang, out, boundary);
            }
            final String sdp = productProps.getProperty("sdp");
            if (sdp != null) {
                writeParam("sdp", sdp, out, boundary);
            }
            final String mdm = productProps.getProperty("mdm");
            if (mdm != null) {
                writeParam("mdm", mdm, out, boundary);
            }
            final String installationdateInLong = productProps.getProperty("it");
            if (installationdateInLong != null && !installationdateInLong.equals("")) {
                final long instDate = new Long(installationdateInLong);
                final String dateStr = SYMClientUtil.getTimeString(instDate, "MMM dd,yyyy hh:mm a");
                writeParam("Installation Date", dateStr, out, boundary);
                writeParam("ID", installationdateInLong, out, boundary);
            }
            final String dbName = productProps.getProperty("db");
            if (dbName != null) {
                writeParam("DB", dbName, out, boundary);
            }
            final String isFSServerConfigured = FwsUtil.isForwardingServerConfigured();
            if (isFSServerConfigured != null) {
                writeParam("forwarding_server_config", isFSServerConfigured, out, boundary);
            }
            final HashMap<String, String> supportParams = ApiFactoryProvider.getSupportAPI().getSupportParam();
            for (final Map.Entry<String, String> entry : supportParams.entrySet()) {
                writeParam(entry.getKey(), entry.getValue(), out, boundary);
            }
            writeParam("Connection Type", "URLConnection", out, boundary);
            out.flush();
            UploadAction.logg.log(Level.INFO, "Finished writing Customer Related Info. Now Reading From Server");
            final InputStream stream = conn.getInputStream();
            in = new BufferedInputStream(stream);
            bos = new ByteArrayOutputStream();
            int i = 0;
            while ((i = in.read()) != -1) {
                bos.write(i);
            }
            UploadAction.logg.log(Level.INFO, "Upload Completed in URLConnection Method");
        }
        catch (final Exception e) {
            UploadAction.logg.log(Level.WARNING, "UploadAction -> Problem occurred in doUpload ", e);
            return false;
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (bos != null) {
                    bos.close();
                }
            }
            catch (final Exception ex) {}
        }
        return true;
    }
    
    private static void writeParam(final String name, final String value, final DataOutputStream out, final String boundary) {
        try {
            out.writeBytes("content-disposition: form-data; name=\"" + name + "\"\r\n\r\n");
            out.writeBytes(value);
            out.writeBytes("\r\n--" + boundary + "\r\n");
        }
        catch (final Exception e) {
            UploadAction.logg.log(Level.WARNING, "SupportFileUploader -> Problem occurred in writeParam ");
        }
    }
    
    private static void writeFile(final String name, final String filePath, final DataOutputStream out, final String boundary) throws Exception {
        FileInputStream fis = null;
        try {
            out.writeBytes("content-disposition: form-data; name=\"" + name + "\"; filename=\"" + filePath + "\"\r\n");
            out.writeBytes("content-type: application/octet-stream\r\n\r\n");
            fis = new FileInputStream(filePath);
            while (true) {
                synchronized (UploadAction.buffer) {
                    final int amountRead = fis.read(UploadAction.buffer);
                    if (amountRead == -1) {
                        break;
                    }
                    out.write(UploadAction.buffer, 0, amountRead);
                }
            }
            out.writeBytes("\r\n--" + boundary + "\r\n");
            UploadAction.logg.log(Level.INFO, "Finished writing file content.");
        }
        catch (final Exception e) {
            UploadAction.logg.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred ", e);
            try {
                fis.close();
            }
            catch (final Exception e) {
                UploadAction.logg.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred ", e);
            }
        }
        finally {
            try {
                fis.close();
            }
            catch (final Exception e2) {
                UploadAction.logg.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred ", e2);
            }
        }
    }
    
    public static boolean checkUploadAccess() throws Exception {
        try {
            final URL servlet = new URL(UploadAction.dmUploadAction.getUploadUrl());
            final URLConnection conn = servlet.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
        }
        catch (final UnknownHostException e) {
            UploadAction.logg.log(Level.SEVERE, "No access for uploading file");
            return false;
        }
        catch (final Exception ex) {
            UploadAction.logg.log(Level.SEVERE, "Unable to create connection to for uploading support file");
            return false;
        }
        return true;
    }
    
    private static String getBrowserInfo(final String browser) {
        String browsername = "";
        String browserversion = "";
        if (browser.contains("MSIE")) {
            final String subsString = browser.substring(browser.indexOf("MSIE"));
            final String[] Info = subsString.split(";")[0].split(" ");
            browsername = Info[0];
            browserversion = Info[1];
        }
        else if (browser.contains("Firefox")) {
            final String subsString = browser.substring(browser.indexOf("Firefox"));
            final String[] Info = subsString.split(" ")[0].split("/");
            browsername = Info[0];
            browserversion = Info[1];
        }
        else if (browser.contains("Chrome")) {
            final String subsString = browser.substring(browser.indexOf("Chrome"));
            final String[] Info = subsString.split(" ")[0].split("/");
            browsername = Info[0];
            browserversion = Info[1];
        }
        else if (browser.contains("Opera")) {
            final String subsString = browser.substring(browser.indexOf("Opera"));
            final String[] Info = subsString.split(" ")[0].split("/");
            browsername = Info[0];
            browserversion = Info[1];
        }
        else if (browser.contains("Safari")) {
            final String subsString = browser.substring(browser.indexOf("Safari"));
            final String[] Info = subsString.split(" ")[0].split("/");
            browsername = Info[0];
            browserversion = Info[1];
        }
        return browsername + "-" + browserversion;
    }
    
    public static synchronized boolean doUploadBigSize(final String supportFile, final String fromAddress, final String userMessage, final String browserVersion, final boolean acknowledgement, final boolean piData) throws Exception {
        if (!acknowledgement) {
            UploadAction.isDebugFileUpload = Boolean.TRUE;
        }
        final boolean withoutPIDataOldValue = UploadAction.withoutPIData;
        UploadAction.withoutPIData = piData;
        final boolean returnValue = doUploadBigSize(supportFile, fromAddress, userMessage, browserVersion);
        UploadAction.isDebugFileUpload = Boolean.FALSE;
        UploadAction.withoutPIData = withoutPIDataOldValue;
        return returnValue;
    }
    
    public static synchronized boolean doUploadBigSize(final String supportFile, final String fromAddress, final String userMessage, final String browserVersion) throws Exception {
        DataOutputStream out = null;
        BufferedInputStream in = null;
        ByteArrayOutputStream bos = null;
        try {
            final Properties proxyDetailsProp = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
            if (!proxyDetailsProp.isEmpty()) {
                String userName = null;
                final DownloadManager downloadMgr = DownloadManager.getInstance();
                final int proxyType = DownloadManager.proxyType;
                if (proxyType == 4) {
                    final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(UploadAction.dmUploadAction.getUploadUrl(), proxyDetailsProp);
                    System.setProperty("proxyHost", ((Hashtable<K, String>)pacProps).get("proxyHost"));
                    System.setProperty("proxyPort", "" + ((Hashtable<K, Object>)pacProps).get("proxyPort"));
                }
                else {
                    System.setProperty("proxyHost", ((Hashtable<K, String>)proxyDetailsProp).get("proxyHost"));
                    System.setProperty("proxyPort", "" + ((Hashtable<K, Object>)proxyDetailsProp).get("proxyPort"));
                }
                userName = ((Hashtable<K, String>)proxyDetailsProp).get("proxyUser");
                if (userName != null && !userName.equals("")) {
                    System.setProperty("userName", ((Hashtable<K, String>)proxyDetailsProp).get("proxyUser"));
                    final String passwordhttp = ((Hashtable<K, String>)proxyDetailsProp).get("proxyPass");
                    System.setProperty("password", passwordhttp);
                }
            }
            final URL servlet = new URL(UploadAction.dmUploadAction.getUploadUrl());
            final HttpURLConnection conn = (HttpURLConnection)servlet.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            final String mailInfo = getMailRelatedInfo(fromAddress, I18N.getMsg(UploadAction.toAddress, new Object[0]), userMessage, supportFile);
            final String customerInfo = getCustomerRelatedInfo(browserVersion);
            final String boundary = "----WebKitFormBoundaryByYUQEjK0I3eWnwl";
            final String endFile = "\r\n--" + boundary + "\r\n";
            final File f = new File(supportFile);
            final long maxsize = mailInfo.length() + f.length() + endFile.length() + customerInfo.length();
            UploadAction.logg.log(Level.INFO, "Size predicted Earlier and set:" + maxsize);
            conn.setFixedLengthStreamingMode(maxsize);
            conn.setRequestProperty("Content-type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("Content-Length", "" + maxsize);
            conn.setRequestProperty("Cache-Control", "no-cache");
            out = new DataOutputStream(conn.getOutputStream());
            UploadAction.logg.log(Level.INFO, "Mail Related Information Written to Server.");
            out.writeBytes(mailInfo);
            out.flush();
            UploadAction.logg.log(Level.INFO, "Finished writing Mail Related Info, Now Uploading File Starts");
            writeHugeFile(supportFile, out, boundary);
            UploadAction.logg.log(Level.INFO, "File Upload Over. Writing Customer Related Information.");
            out.writeBytes(customerInfo);
            UploadAction.logg.log(Level.INFO, "Written " + out.size() + " to Bonitas server.Finished Uploading. Now Reading From Server");
            final InputStream stream = conn.getInputStream();
            in = new BufferedInputStream(stream);
            bos = new ByteArrayOutputStream();
            int i = 0;
            while ((i = in.read()) != -1) {
                bos.write(i);
            }
            UploadAction.logg.log(Level.INFO, "Upload Completed in HttpURLConnection Method");
        }
        catch (final Exception e) {
            UploadAction.logg.log(Level.WARNING, "UploadAction -> Problem occurred in doUpload ", e);
            return false;
        }
        finally {
            try {
                out.close();
                in.close();
                bos.close();
            }
            catch (final Exception e2) {
                UploadAction.logg.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred in HTTPUrlConnection method upload.");
            }
        }
        return true;
    }
    
    public static String getMailRelatedInfo(final String from, final String to, final String msg, final String filePath) {
        UploadAction.logg.log(Level.INFO, "Get Mail Related Info Started.");
        final String boundary = "----WebKitFormBoundaryByYUQEjK0I3eWnwl";
        final StringBuilder content = new StringBuilder();
        content.append("--" + boundary + "\r\n");
        content.append(getParams("fromAddress", from, boundary));
        content.append(getParams("toAddress", to, boundary));
        content.append(getParams("todo", "upload", boundary));
        content.append(getParams("userMessage", msg, boundary));
        if (UploadAction.isDebugFileUpload) {
            content.append(getParams("informcustomer", "no", boundary));
            content.append(getParams("subject", "ENDPOINT CENTRAL LOGS FOR DIAGNOSIS", boundary));
        }
        content.append("content-disposition: form-data; name=\"uploadfile\"; filename=\"" + filePath + "\"\r\n");
        content.append("content-type: application/octet-stream\r\n\r\n");
        UploadAction.logg.log(Level.INFO, "Mail Related Info Retrieved");
        return content.toString();
    }
    
    public static String getCustomerRelatedInfo(final String browserVersion) {
        UploadAction.logg.log(Level.INFO, "Get Customer Related Info Started");
        final Properties productProps = SyMUtil.getProductInfo();
        final StringBuilder content = new StringBuilder();
        final String boundary = "----WebKitFormBoundaryByYUQEjK0I3eWnwl";
        final String buildnumber = productProps.getProperty("buildnumber");
        if (buildnumber != null) {
            content.append(getParams("Build Number", buildnumber, boundary));
        }
        final String licenseType = productProps.getProperty("licenseType");
        if (licenseType != null) {
            content.append(getParams("License Type", licenseType, boundary));
        }
        final String productType = productProps.getProperty("productType");
        if (productType != null) {
            content.append(getParams("Product Type", productType, boundary));
        }
        if (!UploadAction.withoutPIData && licenseType.equals("R")) {
            final String licensedTo = productProps.getProperty("licensedTo");
            if (licensedTo != null) {
                content.append(getParams("Licensed To", licensedTo, boundary));
            }
        }
        if (!UploadAction.isDebugFileUpload) {
            String som = productProps.getProperty("som");
            final String[] somPropsArray = som.split("metrId");
            ((Hashtable<String, String>)productProps).put("som", somPropsArray[0]);
            if (browserVersion != null) {
                som = som + "|br-" + browserVersion;
            }
            if (som != null) {
                content.append(getParams("som", som, boundary));
            }
        }
        final String pkg = SyMUtil.getInstallationProperty("pkg");
        if (pkg != null && pkg.length() > 0) {
            content.append(getParams("pkg", pkg, boundary));
        }
        final String lang = productProps.getProperty("lang");
        if (lang != null) {
            content.append(getParams("lang", lang, boundary));
        }
        if (!UploadAction.isDebugFileUpload) {
            final String sdp = productProps.getProperty("sdp");
            if (sdp != null) {
                content.append(getParams("sdp", sdp, boundary));
            }
        }
        if (!UploadAction.isDebugFileUpload) {
            final String mdm = productProps.getProperty("mdm");
            if (mdm != null) {
                content.append(getParams("mdm", mdm, boundary));
            }
        }
        if (!UploadAction.isDebugFileUpload) {
            final String installationdateInLong = productProps.getProperty("it");
            if (installationdateInLong != null && !installationdateInLong.equals("")) {
                final long instDate = new Long(installationdateInLong);
                final String dateStr = SYMClientUtil.getTimeString(instDate, "MMM dd,yyyy hh:mm a");
                content.append(getParams("Installation Date", dateStr, boundary));
                content.append(getParams("ID", installationdateInLong, boundary));
            }
        }
        final String dbName = productProps.getProperty("db");
        if (dbName != null) {
            content.append(getParams("DB", dbName, boundary));
        }
        try {
            final HashMap<String, String> supportParams = ApiFactoryProvider.getSupportAPI().getSupportParam();
            for (final Map.Entry<String, String> entry : supportParams.entrySet()) {
                content.append(getParams(entry.getKey(), entry.getValue(), boundary));
            }
        }
        catch (final Exception ex) {
            UploadAction.logg.log(Level.INFO, "Exception in appending the params to file upload..");
        }
        content.append(getParams("Connection Type", "httpURLConnection", boundary));
        UploadAction.logg.log(Level.INFO, "Customer Related Information Retrieved");
        return content.toString();
    }
    
    private static String getParams(final String name, final String value, final String boundary) {
        final StringBuilder content = new StringBuilder();
        try {
            content.append("content-disposition: form-data; name=\"" + name + "\"\r\n\r\n");
            content.append(value);
            content.append("\r\n--" + boundary + "\r\n");
        }
        catch (final Exception e) {
            UploadAction.logg.log(Level.WARNING, "SupportFileUploader -> Problem occurred in writeParam ");
        }
        return content.toString();
    }
    
    private static void writeHugeFile(final String filePath, final DataOutputStream out, final String boundary) throws Exception {
        FileInputStream fis = null;
        try {
            int counter = 1;
            fis = new FileInputStream(filePath);
            while (true) {
                synchronized (UploadAction.buffer) {
                    final int amountRead = fis.read(UploadAction.buffer);
                    if (amountRead == -1) {
                        break;
                    }
                    out.write(UploadAction.buffer, 0, amountRead);
                    if (counter % 10240 == 0) {
                        out.flush();
                        UploadAction.logg.log(Level.FINE, "Flush 10Mb.Size written:" + out.size());
                    }
                    ++counter;
                }
            }
            out.writeBytes("\r\n--" + boundary + "\r\n");
            out.flush();
        }
        catch (final Exception e) {
            UploadAction.logg.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred", e);
            try {
                fis.close();
            }
            catch (final Exception e) {
                UploadAction.logg.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred", e);
            }
        }
        finally {
            try {
                fis.close();
            }
            catch (final Exception e2) {
                UploadAction.logg.log(Level.WARNING, "SupportFileUploader-> writeFile-> Problem occurred", e2);
            }
        }
    }
    
    static {
        UploadAction.className = UploadAction.class.getName();
        UploadAction.logg = Logger.getLogger(UploadAction.className);
        UploadAction.toAddress = ProductUrlLoader.getInstance().getValue("supportmailid");
        UploadAction.isDbLocked = false;
        UploadAction.isDebugFileUpload = false;
        UploadAction.isFosTrailLicense = false;
        UploadAction.isFwsTrailLicense = false;
        UploadAction.withoutPIData = true;
        UploadAction.dmUploadAction = DMUploadAction.getInstance();
        UploadAction.buffer = new byte[1024];
    }
}
