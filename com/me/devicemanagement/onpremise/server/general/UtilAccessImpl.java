package com.me.devicemanagement.onpremise.server.general;

import javax.net.ssl.SSLSession;
import com.me.ems.onpremise.security.securegatewayserver.proxy.SGSDynamicProxyDataSyncHandler;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.onpremise.start.util.InstallUtil;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.onpremise.server.util.EMSProductUtil;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.properties.util.GeneralPropertiesLoader;
import com.me.devicemanagement.onpremise.server.license.handler.CommonOnpremiseServicehandler;
import java.util.Arrays;
import java.util.List;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.framework.server.util.DCMetaDataUtil;
import java.io.DataOutputStream;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.net.InetAddress;
import com.adventnet.ds.query.Criteria;
import java.util.Map;
import com.adventnet.i18n.MultiplePropertiesResourceBundleControl;
import java.util.ResourceBundle;
import java.util.Locale;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.onpremise.server.downloadmgr.SSLUtil;
import java.io.IOException;
import sun.misc.BASE64Encoder;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import com.me.devicemanagement.framework.server.util.CreatorDataPost;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.io.File;
import javax.net.ssl.HostnameVerifier;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.general.UtilAccessAPI;

public class UtilAccessImpl implements UtilAccessAPI
{
    public static final String SERVER_HOME_KEY = "server.home";
    private static final Logger LOGGER;
    private static Boolean isMSP;
    private static String serverType;
    public static final String SUMMARY = "SUMMARY";
    public static final String PROBE = "PROBE";
    static final HostnameVerifier DO_NOT_VERIFY;
    
    public String getServerHome() {
        String serverHome = System.getProperty("server.home");
        if (serverHome == null) {
            serverHome = "..";
            System.setProperty("server.home", serverHome);
        }
        return System.getProperty("server.home");
    }
    
    public String getServerBinUrl() {
        return this.getServerHome() + File.separator + "bin";
    }
    
    public Long getCustomerID() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public String getServerURL() {
        final String sourceMethod = "getServerURL()";
        String protocolType = "http://";
        String serverName = SyMUtil.getServerName();
        try {
            final String enableHttps = com.me.devicemanagement.framework.server.util.SyMUtil.getSyMParameter("ENABLE_HTTPS");
            if (enableHttps != null && enableHttps.equalsIgnoreCase("true")) {
                protocolType = "https://";
            }
            String pathType = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("USE_FQDN_PATH_EMAIL");
            if (pathType == null) {
                com.me.devicemanagement.framework.server.util.SyMUtil.setCustomParamValues("USE_FQDN_PATH_EMAIL");
                pathType = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("USE_FQDN_PATH_EMAIL");
            }
            if (pathType != null && pathType.equalsIgnoreCase("true")) {
                serverName = SyMUtil.getServerFQDNName();
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(UtilAccessImpl.LOGGER, "UtilAccessImpl", sourceMethod, "Exception Occurred : ", (Throwable)ex);
        }
        final int serverPort = this.getWebServerPort();
        final String baseURLStr = protocolType + serverName + ":" + serverPort;
        return baseURLStr;
    }
    
    public String getServerURLForMailNotification() {
        final String sourceMethod = "getServerURLForMailNotification()";
        String url = "";
        try {
            final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
            final String natAddress = natProps.getProperty("NAT_ADDRESS", "");
            final boolean isNATConfigured = !natAddress.equals("");
            if (isNATConfigured) {
                final boolean isFwsConfigured = Boolean.parseBoolean(SyMUtil.getSyMParameter("forwarding_server_config"));
                final boolean sgsUIEnabled = Boolean.parseBoolean(WebServerUtil.getWebServerSettings().getProperty("ui.fws.enabled"));
                if (isFwsConfigured && sgsUIEnabled) {
                    url = this.getUrlFromNATProps(natProps);
                }
                else if (isFwsConfigured) {
                    url = this.getLocalServerUrl();
                }
                else {
                    url = this.getUrlFromNATProps(natProps);
                }
            }
            else {
                url = this.getLocalServerUrl();
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(UtilAccessImpl.LOGGER, "UtilAccessImpl", sourceMethod, "Exception Occurred : ", (Throwable)ex);
        }
        return url;
    }
    
    private String getLocalServerUrl() throws Exception {
        final String sourceMethod = "getLocalServerUrl";
        String url = "";
        try {
            final Properties serverProps = SyMUtil.getDCServerInfo();
            final String serverName = String.valueOf(((Hashtable<K, Object>)serverProps).get("SERVER_MAC_NAME"));
            final String enableHttps = com.me.devicemanagement.framework.server.util.SyMUtil.getSyMParameter("ENABLE_HTTPS");
            String protocolType;
            String serverPort;
            if (enableHttps != null && enableHttps.equalsIgnoreCase("true")) {
                protocolType = "https://";
                serverPort = String.valueOf(((Hashtable<K, Object>)serverProps).get("HTTPS_PORT"));
            }
            else {
                protocolType = "http://";
                serverPort = String.valueOf(((Hashtable<K, Object>)serverProps).get("SERVER_PORT"));
            }
            url = protocolType + serverName + ":" + serverPort;
        }
        catch (final Exception ex) {
            SyMLogger.error(UtilAccessImpl.LOGGER, "UtilAccessImpl", sourceMethod, "Exception Occurred : ", (Throwable)ex);
            throw ex;
        }
        return url;
    }
    
    private String getUrlFromNATProps(final Properties natProps) {
        final String protocolType = "https://";
        final String natAddress = natProps.getProperty("NAT_ADDRESS", "");
        final String portNumber = String.valueOf(((Hashtable<K, Object>)natProps).get("NAT_HTTPS_PORT"));
        return protocolType + natAddress + ":" + portNumber;
    }
    
    public String getStaticServerURL() {
        throw new UnsupportedOperationException("Not Yet Supported");
    }
    
    public String getStaticClientURL() {
        return "";
    }
    
    public int getWebServerPort() {
        return SyMUtil.getSSLPort();
    }
    
    public int getSSLPort() {
        return SyMUtil.getSSLPort();
    }
    
    public int getHttpServerPort() {
        return SyMUtil.getSSLPort();
    }
    
    public String getHttpServerPingURL() {
        String url = null;
        final Hashtable ipHash = SyMUtil.getServerIPs();
        if (ipHash.size() > 0) {
            final String primaryIp = ipHash.get("primaryIP");
            url = "http://" + primaryIp + ":" + this.getHttpServerPort();
        }
        return url;
    }
    
    public HttpsURLConnection getCreatorConnection(final URL url, final String contentType, final boolean doInput, final boolean doOutput, final int timeOut, final String proxyDefined, final Properties proxyProps) {
        final String sourceMethod = "getCreatorConnection";
        HttpsURLConnection uc = null;
        try {
            CreatorDataPost.createSSLSocketFactory();
            if (proxyDefined != null && proxyDefined.equalsIgnoreCase("true")) {
                final String proxyHost = ((Hashtable<K, String>)proxyProps).get("proxyHost");
                final String proxyPort = ((Hashtable<K, String>)proxyProps).get("proxyPort");
                if (proxyHost != null && proxyPort != null) {
                    final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
                    uc = (HttpsURLConnection)url.openConnection(proxy);
                    final String proxyUser = ((Hashtable<K, String>)proxyProps).get("proxyUser");
                    final String proxyPass = ((Hashtable<K, String>)proxyProps).get("proxyPass");
                    if (proxyUser != null && proxyPass != null) {
                        final BASE64Encoder encoder = new BASE64Encoder();
                        final String encodedUserPwd = encoder.encode((proxyUser + ":" + proxyPass).getBytes());
                        uc.setRequestProperty("Proxy-Authorization", "Basic " + encodedUserPwd);
                    }
                }
            }
            if (uc == null) {
                uc = (HttpsURLConnection)url.openConnection();
            }
            uc.setRequestProperty("Cookie", "metrack=true");
            if (contentType != null) {
                uc.setRequestProperty("Content-Type", contentType);
            }
            uc.setDoOutput(doOutput);
            uc.setDoInput(doInput);
            uc.setConnectTimeout(timeOut);
        }
        catch (final IOException e) {
            SyMLogger.error(UtilAccessImpl.LOGGER, "UtilAccessImpl", sourceMethod, "IOException Occurred : ", (Throwable)e);
        }
        catch (final Exception e2) {
            SyMLogger.error(UtilAccessImpl.LOGGER, "UtilAccessImpl", sourceMethod, "Exception Occurred : ", (Throwable)e2);
        }
        return uc;
    }
    
    public void initSSLUtil() {
        SSLUtil.initSSLFactory();
        SSLUtil.removeEncodingModule();
        SSLUtil.initSecurityProperties();
    }
    
    public String getSecret(final String key) {
        throw new UnsupportedOperationException("Not Supported Yet");
    }
    
    public String getStaticServerVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public String getTrustRootCertificateFilePath() throws Exception {
        return SSLCertificateUtil.getInstance().getServerCertificateFilePath();
    }
    
    public Properties getWebServerSettings() throws Exception {
        return WebServerUtil.getWebServerSettings();
    }
    
    public ResourceBundle newCombinedBundle(final Locale locale, final ClassLoader loader, final boolean reload) throws IOException {
        return new MultiplePropertiesResourceBundleControl().newCombinedBundle(locale, loader, reload);
    }
    
    public MultiplePropertiesResourceBundleControl getMultipleResourceBundleControl() {
        return new MultiplePropertiesResourceBundleControl();
    }
    
    public void addAdditionalProductMetaProps(final Map<String, Object> productMeta) {
        productMeta.put("isWebSocketGatewayEnabled", WebServerUtil.isWebSocketGatewayEnabled());
    }
    
    public String getServerName() {
        String serverName = null;
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get("DCServerInfo", (Criteria)null);
            if (!dataObject.isEmpty() && dataObject.containsTable("DCServerInfo")) {
                serverName = (String)dataObject.getFirstValue("DCServerInfo", "SERVER_MAC_NAME");
            }
            else {
                serverName = InetAddress.getLocalHost().getHostName();
            }
        }
        catch (final Exception ex) {
            UtilAccessImpl.LOGGER.log(Level.WARNING, "Exception while getting DC Server Name");
        }
        return serverName;
    }
    
    public String setUserVariables(final String queryString) {
        return queryString;
    }
    
    public String getCustomerRelatedInfo(final String browserVersion, final DataOutputStream out, final String boundary, final Properties properties) {
        return "";
    }
    
    public String getCustomerRelatedInfoForHugeFile(final String browserVersion, final DataOutputStream out, final String boundary, final Properties productProps, final boolean withoutPIData, final boolean isDebugFileUpload) {
        return "";
    }
    
    private static void writeParam(final String name, final String value, final DataOutputStream out, final String boundary) {
        try {
            out.writeBytes("content-disposition: form-data; name=\"" + name + "\"\r\n\r\n");
            out.writeBytes(value);
            out.writeBytes("\r\n--" + boundary + "\r\n");
        }
        catch (final Exception e) {
            UtilAccessImpl.LOGGER.log(Level.WARNING, "SupportFileUploader -> Problem occurred in writeParam ");
        }
    }
    
    public String getRebrandLogoPath() {
        final String isRebranded = SyMUtil.getSyMParameter("IS_REBRANDED");
        if (isRebranded != null && isRebranded.equalsIgnoreCase("true")) {
            return DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "images" + File.separator + "" + SyMUtil.getInstance().getTheme() + File.separator + "rebranded-logo.gif";
        }
        final String product = ProductUrlLoader.getInstance().getGeneralProperites().getProperty("productcode");
        if (product == null) {
            return DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "images" + File.separator + SyMUtil.getInstance().getTheme() + File.separator + "DCEE" + File.separator + "logo.gif";
        }
        return DCMetaDataUtil.getInstance().getClientDataParentDir() + File.separator + "images" + File.separator + SyMUtil.getInstance().getTheme() + File.separator + product + File.separator + "logo.gif";
    }
    
    public String getServerFQDNName() {
        return SyMUtil.getServerFQDNName();
    }
    
    public boolean isMSP() {
        if (UtilAccessImpl.isMSP == null) {
            UtilAccessImpl.LOGGER.log(Level.FINEST, "-----Inside isMSP method, isMSP value null. Hence reading from msp_properties file and set the value");
            final String val = CustomerInfoUtil.getProperty("isMSP");
            if (val != null && val.equals("true")) {
                UtilAccessImpl.isMSP = Boolean.TRUE;
            }
            else {
                UtilAccessImpl.isMSP = Boolean.FALSE;
            }
        }
        return UtilAccessImpl.isMSP;
    }
    
    public List<String> getProductList() {
        return Arrays.asList(new String[0]);
    }
    
    public String getCustomerProduct() {
        return null;
    }
    
    public String dbRangeCriteriaReplaceString(final String tableName, final String tableAlais) throws Exception {
        return " 1=1 ";
    }
    
    public void migrationHandling() {
        CommonOnpremiseServicehandler.getInstance().migrationHandling();
    }
    
    public Properties getGeneralProperties() {
        return GeneralPropertiesLoader.getInstance().getProperties();
    }
    
    public Properties getGeneralProperties(final String value) {
        final ArrayList arrayList = new ArrayList();
        arrayList.add(value);
        return GeneralPropertiesLoader.getInstance().getPropsBasedOnProduct(arrayList);
    }
    
    public ArrayList getEMSProductCode() {
        return EMSProductUtil.getEMSProductCode();
    }
    
    public void truncateTable(final String tableName) throws Exception {
        RelationalAPI.getInstance().truncateTable(tableName);
    }
    
    public void updateWebServerSettings(final Properties newProps, final boolean append) {
        final String webSettingsFile = this.getServerHome() + File.separator + "conf" + File.separator + "websettings.conf";
        StartupUtil.storeProperties(newProps, webSettingsFile, append);
    }
    
    public void removeWebServerSettings(final ArrayList<String> keys) {
        final String webSettingsFile = this.getServerHome() + File.separator + "conf" + File.separator + "websettings.conf";
        StartupUtil.removeProperties((ArrayList)keys, webSettingsFile);
    }
    
    public String getRootCertificatePath() throws Exception {
        return InstallUtil.getServerRootCertificateFilePath();
    }
    
    public boolean isSummaryServer() {
        if (UtilAccessImpl.serverType == null) {
            populateServerType();
        }
        return "SUMMARY".equalsIgnoreCase(UtilAccessImpl.serverType);
    }
    
    public static void setServerType(final String value) {
        UtilAccessImpl.serverType = value;
    }
    
    public String getServerType() {
        if (UtilAccessImpl.serverType == null) {
            populateServerType();
        }
        return UtilAccessImpl.serverType;
    }
    
    public static void populateServerType() {
        try {
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            final String serverConf = serverHome + File.separator + "conf" + File.separator + "server_properties.conf";
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(serverConf)) {
                final Properties serverProps = FileAccessUtil.readProperties(serverConf);
                if (serverProps.containsKey("server_type")) {
                    setServerType(serverProps.getProperty("server_type"));
                }
            }
        }
        catch (final Exception e) {
            UtilAccessImpl.LOGGER.log(Level.SEVERE, "Exception Occurred on checking server type ", e);
        }
    }
    
    public boolean isProbeServer() {
        if (UtilAccessImpl.serverType == null) {
            populateServerType();
        }
        return "PROBE".equalsIgnoreCase(UtilAccessImpl.serverType);
    }
    
    public void invokeOnpremiseComponents() {
        SGSDynamicProxyDataSyncHandler.triggerSGSProxyDataSync();
    }
    
    public String getCrsBaseUrl() {
        return SyMUtil.getSyMParameter("STATIC_FILE_SERVER_BASE_URL");
    }
    
    public String getCurrentSyncingComponentCacheName() {
        return "DMSStaticServerSync";
    }
    
    static {
        LOGGER = Logger.getLogger(UtilAccessImpl.class.getName());
        UtilAccessImpl.isMSP = null;
        UtilAccessImpl.serverType = null;
        DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(final String hostname, final SSLSession session) {
                return true;
            }
        };
    }
}
