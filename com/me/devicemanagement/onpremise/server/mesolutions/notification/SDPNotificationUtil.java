package com.me.devicemanagement.onpremise.server.mesolutions.notification;

import java.io.File;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.authentication.util.AuthDBUtil;
import java.io.IOException;
import javax.net.ssl.SSLSocketFactory;
import java.net.HttpURLConnection;
import java.io.FileNotFoundException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.admin.DMSDPIntegration;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import com.me.devicemanagement.onpremise.server.sdp.SDPSSLSocketFactoryUtil;
import java.net.Proxy;
import javax.net.ssl.HttpsURLConnection;
import java.net.URLConnection;
import java.net.URL;
import com.me.devicemanagement.onpremise.server.mesolutions.util.SolutionUtil;
import java.util.Properties;
import java.util.logging.Logger;

public class SDPNotificationUtil
{
    private static SDPNotificationUtil handler;
    private static Logger logger;
    private static String className;
    public static final String DC_SERVER_HOME;
    public static final String SDP_KEYSTORE_DIR;
    public static final String SDP_KEYSTORE_PATH;
    public static final char[] SDP_KEYSTORE_PASSPHRASE;
    
    public static SDPNotificationUtil getInstance() {
        if (SDPNotificationUtil.handler == null) {
            SDPNotificationUtil.handler = new SDPNotificationUtil();
        }
        return SDPNotificationUtil.handler;
    }
    
    public static String getServiceDeskBaseURL() throws Exception {
        final Properties props = getServiceDeskProperty();
        return getServiceDeskBaseURL(props);
    }
    
    public static String getAssetExplorerBaseURL() throws Exception {
        final Properties props = getAssetExplorerProperty();
        return getServiceDeskBaseURL(props);
    }
    
    public static Properties getServiceDeskProperty() throws Exception {
        final Properties props = getApplicationServerProperty("HelpDesk");
        return props;
    }
    
    public static Properties getAssetExplorerProperty() throws Exception {
        final Properties props = getApplicationServerProperty("AssetExplorer");
        return props;
    }
    
    public static String getApplicationServerURL(final String appname) throws Exception {
        final Properties props = getApplicationServerProperty(appname);
        return getServiceDeskBaseURL(props);
    }
    
    private static Properties getApplicationServerProperty(final String appname) throws Exception {
        final Properties props = SolutionUtil.getInstance().getServerSettings(appname);
        return props;
    }
    
    public static String getServiceDeskBaseURL(final Properties props) throws Exception {
        String host = null;
        String port = null;
        String protocol = null;
        String baseURL = null;
        if (props != null) {
            if (props.getProperty("SERVER") != null) {
                host = props.getProperty("SERVER");
            }
            if (props.getProperty("PORT") != null) {
                port = props.getProperty("PORT");
            }
            if (props.getProperty("PROTOCOL") != null) {
                protocol = props.getProperty("PROTOCOL");
            }
            baseURL = protocol + "://" + host + ":" + port + "/";
            if (port == null || port.trim().length() == 0) {
                baseURL = protocol + "://" + host;
            }
            else {
                baseURL = protocol + "://" + host + ":" + port;
            }
        }
        return baseURL;
    }
    
    public URLConnection createSDPURLConnection(final URL url, final String contentType, final boolean doInput, final boolean doOutput, final boolean isItForDataPost) throws IOException {
        if (url == null) {
            return null;
        }
        String protocol = "";
        protocol = url.getProtocol();
        if (protocol.equalsIgnoreCase("https")) {
            HttpsURLConnection uc = (HttpsURLConnection)url.openConnection(Proxy.NO_PROXY);
            final String keystorePath = SDPNotificationUtil.SDP_KEYSTORE_PATH;
            final char[] keystorePassphrase = SDPNotificationUtil.SDP_KEYSTORE_PASSPHRASE;
            SSLSocketFactory sdpSSLSocketFactory = SDPSSLSocketFactoryUtil.getInstance().getSSLSocketFactory(keystorePath, keystorePassphrase);
            uc = (HttpsURLConnection)url.openConnection(Proxy.NO_PROXY);
            if (sdpSSLSocketFactory != null) {
                uc.setSSLSocketFactory(sdpSSLSocketFactory);
            }
            else if (sdpSSLSocketFactory == null) {
                try {
                    final String[] classNames = ProductClassLoader.getMultiImplProductClass("DM_SDP_CLASS");
                    if (classNames.length != 0) {
                        for (final String className : classNames) {
                            final DMSDPIntegration dmsdpIntegration = (DMSDPIntegration)Class.forName(className).newInstance();
                            dmsdpIntegration.handleKeyStoreFile();
                            sdpSSLSocketFactory = SDPSSLSocketFactoryUtil.getInstance().getSSLSocketFactory(keystorePath, keystorePassphrase);
                            uc = (HttpsURLConnection)url.openConnection(Proxy.NO_PROXY);
                            if (sdpSSLSocketFactory != null) {
                                uc.setSSLSocketFactory(sdpSSLSocketFactory);
                            }
                        }
                    }
                }
                catch (final Exception e) {
                    SDPNotificationUtil.logger.log(Level.SEVERE, "Exception while creating connection with SDP :", e);
                }
            }
            uc.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(final String hostname, final SSLSession session) {
                    return true;
                }
            });
            if (contentType != null) {
                uc.setRequestProperty("Content-Type", contentType);
            }
            uc.setDoOutput(doOutput);
            uc.setDoInput(doInput);
            uc.setConnectTimeout(30000);
            uc.setReadTimeout(30000);
            uc.addRequestProperty("User-Agent", "Desktopcentral");
            if (!isItForDataPost && uc.getResponseCode() == 404) {
                throw new FileNotFoundException();
            }
            return uc;
        }
        else {
            final HttpURLConnection uc2 = (HttpURLConnection)url.openConnection(Proxy.NO_PROXY);
            if (contentType != null) {
                uc2.setRequestProperty("Content-Type", contentType);
            }
            uc2.setDoOutput(doOutput);
            uc2.setDoInput(doInput);
            uc2.setConnectTimeout(30000);
            uc2.setReadTimeout(30000);
            uc2.addRequestProperty("User-Agent", "Desktopcentral");
            if (!isItForDataPost && uc2.getResponseCode() == 404) {
                throw new FileNotFoundException();
            }
            return uc2;
        }
    }
    
    public static String getPasswordWithEncryption() throws Exception {
        String password = null;
        try {
            final AuthDBUtil adb = new AuthDBUtil();
            final String loginName = "admin";
            final String serviceName = "System";
            final DataObject accountDO = AuthDBUtil.getAccountPasswordDO(loginName, serviceName);
            final String message = null;
            final long now = System.currentTimeMillis();
            if (accountDO.isEmpty()) {
                SDPNotificationUtil.logger.log(Level.FINEST, SDPNotificationUtil.className + "Error in getting password DO");
            }
            final Row passRow = accountDO.getFirstRow("currentPass");
            final String salt = (String)passRow.get("SALT");
            final String algorithm = (String)passRow.get("ALGORITHM");
            password = (String)passRow.get("PASSWORD");
        }
        catch (final Exception e) {
            SDPNotificationUtil.logger.log(Level.WARNING, SDPNotificationUtil.className + "Exception while in getting Password......: ");
            throw e;
        }
        return password;
    }
    
    static {
        SDPNotificationUtil.handler = null;
        SDPNotificationUtil.logger = Logger.getLogger("SDPIntegrationLog");
        SDPNotificationUtil.className = "SDPNotificationUtil | ";
        DC_SERVER_HOME = System.getProperty("server.home");
        SDP_KEYSTORE_DIR = SDPNotificationUtil.DC_SERVER_HOME + File.separator + "conf" + File.separator + "sslcerts";
        SDP_KEYSTORE_PATH = SDPNotificationUtil.SDP_KEYSTORE_DIR + File.separator + "servicedeskplus.keystore";
        SDP_KEYSTORE_PASSPHRASE = "sdppassphrase".toCharArray();
    }
}
