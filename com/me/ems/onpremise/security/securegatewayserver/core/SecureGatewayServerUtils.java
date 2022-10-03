package com.me.ems.onpremise.security.securegatewayserver.core;

import java.util.Hashtable;
import javax.net.ssl.SSLSession;
import java.security.cert.CertificateException;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import com.me.ems.onpremise.security.securegatewayserver.api.v1.model.SecureGatewayServerSecurityConfiguration;
import com.me.devicemanagement.onpremise.server.certificate.client.ClientCertAuthBean;
import com.me.devicemanagement.onpremise.server.certificate.client.ClientCertificateGenerator;
import com.me.devicemanagement.onpremise.server.certificate.client.ClientCertificateUtil;
import java.nio.file.LinkOption;
import com.me.ems.onpremise.security.securegatewayserver.api.v1.model.SecureGatewayServerCertificate;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.regex.Pattern;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.net.SocketAddress;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.InputStream;
import java.security.KeyStore;
import java.io.FileInputStream;
import java.io.File;
import javax.net.ssl.SSLSocketFactory;
import java.net.ProtocolException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Client;
import com.me.ems.onpremise.security.securegatewayserver.entityreader.CertificateEntityReader;
import javax.ws.rs.client.ClientBuilder;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import com.me.devicemanagement.framework.server.mailmanager.DCTrustManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import com.me.devicemanagement.onpremise.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.io.IOException;
import java.net.ConnectException;
import com.me.devicemanagement.onpremise.webclient.admin.certificate.CertificateUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecureGatewayServerUtils
{
    private static final Logger LOGGER;
    private static String serverHome;
    public static final String IS_SGS_UI_ACCESS_ALLOWED = "IS_SGS_UI_ACCESS_ALLOWED";
    private static final String SERVER_KEYSTORE_PATH;
    
    public static void generateserverKeystore() {
        try {
            SecureGatewayServerUtils.LOGGER.log(Level.INFO, "generateserverKeystore: Generating server.keystore ");
            CertificateUtil.getInstance().generateServerKeystoreFile(SecureGatewayServerUtils.SERVER_KEYSTORE_PATH, "tomcat", "changeit");
        }
        catch (final Exception ex) {
            SecureGatewayServerUtils.LOGGER.log(Level.SEVERE, "generateserverKeystore: Exception while generating keystore", ex);
        }
    }
    
    public static Boolean isSecureGatewayServerRunning() {
        Boolean isServerUp = Boolean.FALSE;
        try {
            final String responseString = (String)getResponseFromSecureGatewayServer("/", String.class, "text/html");
            if (responseString != null) {
                isServerUp = Boolean.TRUE;
            }
        }
        catch (final ConnectException e) {
            isServerUp = Boolean.FALSE;
            SecureGatewayServerUtils.LOGGER.log(Level.SEVERE, "Connection Exception in isServerUp(){0}", e);
        }
        catch (final IOException exp) {
            isServerUp = Boolean.FALSE;
            SecureGatewayServerUtils.LOGGER.log(Level.SEVERE, "IOException in isServerUp(){0}", exp);
        }
        catch (final Exception e2) {
            isServerUp = Boolean.FALSE;
            SecureGatewayServerUtils.LOGGER.log(Level.SEVERE, "Exception in isServerUp(){0}", e2);
        }
        return isServerUp;
    }
    
    public static Boolean isSecureGatewayServerConfigured() {
        try {
            final String fsconfigured = SyMUtil.getSyMParameter("forwarding_server_config");
            if (fsconfigured != null) {
                return Boolean.valueOf(fsconfigured);
            }
        }
        catch (final Exception e) {
            SecureGatewayServerUtils.LOGGER.log(Level.WARNING, "Exception in getForwardingServerConfigured", e);
        }
        return Boolean.FALSE;
    }
    
    public static Boolean getCertSyncDetails() throws Exception {
        final CertificateUtil crtUtilInstance = CertificateUtil.getInstance();
        final String serverCrtFilePath = SSLCertificateUtil.getInstance().getServerCertificateFilePath();
        final X509Certificate certificate = generateCertificate((String)getResponseFromSecureGatewayServer("/getCertificate", String.class, "text/plain"));
        final Boolean value = certificate.equals(crtUtilInstance.generateCertificateFromFile(serverCrtFilePath));
        return value;
    }
    
    private static Object getServerResponseSecured(final String serverName, final int port, final String urlSuffix, final Class entityClass, final String mimeType, final String authToken) throws Exception {
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { (TrustManager)new DCTrustManager(SecureGatewayServerUtils.SERVER_KEYSTORE_PATH) }, null);
        final Client client = ((ClientBuilder)ClientBuilder.newBuilder().sslContext(sslContext).hostnameVerifier((hostname, session) -> true).register((Class)CertificateEntityReader.class)).build();
        final WebTarget webTarget = client.target("https://" + serverName + ":" + port).path(urlSuffix);
        final Invocation.Builder invocationBuilder = webTarget.request(new String[] { mimeType });
        invocationBuilder.header("Authorization", (Object)authToken);
        final Response response = invocationBuilder.get();
        if (response.getStatus() == 200) {
            final Object responseObject = response.readEntity(entityClass);
            SecureGatewayServerUtils.LOGGER.info("The Response from Secure Gateway Server " + serverName + " is Ok with response." + responseObject);
            return responseObject;
        }
        SecureGatewayServerUtils.LOGGER.info("The Response from Secure Gateway Server " + serverName + " is " + response.getStatusInfo() + " response code " + response.getStatus());
        return null;
    }
    
    public static Object getResponseFromSecureGatewayServer(final String urlSuffix, final Class entityClass, final String mimeType) throws Exception {
        final Properties natProps = NATHandler.getNATConfigurationProperties();
        String serverName;
        if (SecureGatewayServerPropertiesUtils.getSGSProperty("publicIP") != null) {
            serverName = SecureGatewayServerPropertiesUtils.getSGSProperty("publicIP");
        }
        else {
            serverName = natProps.getProperty("NAT_ADDRESS");
        }
        final int httpsPort = ((Hashtable<K, Integer>)natProps).get("NAT_HTTPS_PORT");
        String authToken = SecurityUtil.getAdvancedSecurityDetail("SGS_AUTH_TOKEN");
        if (authToken == null || authToken.isEmpty()) {
            SecureGatewayServerUtils.LOGGER.log(Level.WARNING, "No Authtoken found while invoking following request : " + urlSuffix);
            authToken = "";
        }
        return getServerResponseSecured(serverName, httpsPort, urlSuffix, entityClass, mimeType, authToken);
    }
    
    public static HashMap getPortReachabilityFromServer(final String serverName, final int tcpPort, final int wssPort, final int ftpPort) throws Exception {
        final HashMap portReachablityStatus = new HashMap();
        Boolean tcpReachable = Boolean.TRUE;
        Boolean wssReachable = Boolean.TRUE;
        Boolean ftpReachable = Boolean.TRUE;
        if (SecureGatewayServerPropertiesUtils.getSGSProperty("wssForwardingEnabled").equalsIgnoreCase("true")) {
            wssReachable = isServerPortListening(serverName, wssPort);
        }
        if (SecureGatewayServerPropertiesUtils.getSGSProperty("tcpForwardingEnabled").equalsIgnoreCase("true")) {
            tcpReachable = isServerPortListening(serverName, tcpPort);
        }
        if (SecureGatewayServerPropertiesUtils.getSGSProperty("ftpForwardingEnabled").equalsIgnoreCase("true")) {
            ftpReachable = isServerPortListening(serverName, ftpPort);
        }
        portReachablityStatus.put("tcpReachable", tcpReachable);
        portReachablityStatus.put("wssReachable", wssReachable);
        portReachablityStatus.put("ftpReachable", ftpReachable);
        return portReachablityStatus;
    }
    
    public static Map getSecureGatewayServerStatus() {
        Map secureGatewayServerStatus = null;
        try {
            secureGatewayServerStatus = (Map)getResponseFromSecureGatewayServer("/getStatus", Map.class, "application/json");
            return secureGatewayServerStatus;
        }
        catch (final MalformedURLException e) {
            SecureGatewayServerUtils.LOGGER.log(Level.SEVERE, "MalformedURLException in getSecureGatewayServerStatus()", e);
        }
        catch (final ProtocolException e2) {
            SecureGatewayServerUtils.LOGGER.log(Level.SEVERE, "ProtocolException in getSecureGatewayServerStatus()", e2);
        }
        catch (final IOException e3) {
            SecureGatewayServerUtils.LOGGER.log(Level.SEVERE, "IOException in getSecureGatewayServerStatus()", e3);
        }
        catch (final Exception e4) {
            SecureGatewayServerUtils.LOGGER.log(Level.SEVERE, "IOException in getSecureGatewayServerStatus()", e4);
        }
        return secureGatewayServerStatus;
    }
    
    public static SSLSocketFactory getSSLSocketFactory() throws Exception {
        final File crtFile = new File(SecureGatewayServerUtils.SERVER_KEYSTORE_PATH);
        if (crtFile.exists()) {
            final InputStream in = new FileInputStream(crtFile);
            final char[] passphrase = "changeit".toCharArray();
            final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(in, passphrase);
            in.close();
            final SSLContext context = SSLContext.getInstance("TLS");
            final DCTrustManager tm = new DCTrustManager(SecureGatewayServerUtils.SERVER_KEYSTORE_PATH);
            context.init(null, new TrustManager[] { (TrustManager)tm }, null);
            final SSLSocketFactory factory = context.getSocketFactory();
            return factory;
        }
        return null;
    }
    
    public static boolean isServerPortListening(final String host, final int port) {
        final SocketAddress sc = new InetSocketAddress(host, port);
        final Socket s = new Socket();
        final int timeout = 2000;
        Boolean isAlive;
        try {
            s.connect(sc, timeout);
            s.close();
            isAlive = Boolean.TRUE;
        }
        catch (final Exception e) {
            return false;
        }
        finally {
            if (s != null) {
                try {
                    s.close();
                }
                catch (final Exception ex) {}
            }
        }
        return isAlive;
    }
    
    public static Map enableUIAccess(final Map<String, Boolean> enableUI) throws Exception {
        Map enableUIAccessStatus = null;
        SecureGatewayServerUtils.LOGGER.log(Level.INFO, "Request to change enableUIAccess option " + enableUI);
        if (enableUI.containsKey("enableUIAccess")) {
            final Boolean valueToUpdate = enableUI.get("enableUIAccess");
            if (valueToUpdate && !isSGSUIAccessAllowed()) {
                enableUIAccessStatus.put("status", Boolean.FALSE);
                return enableUIAccessStatus;
            }
            WebServerUtil.addOrUpdateWebServerProps("ui.fws.enabled", Boolean.toString(valueToUpdate));
            initiateSyncTask();
            enableUIAccessStatus = new HashMap();
            final Boolean status = Boolean.valueOf(WebServerUtil.getWebServerSettings().getProperty("ui.fws.enabled"));
            SecureGatewayServerUtils.LOGGER.info("Enable UI access option has been changed to " + status);
            enableUIAccessStatus.put("status", status);
        }
        return enableUIAccessStatus;
    }
    
    public static Boolean initiateSyncTask() {
        try {
            SecureGatewayServerUtils.LOGGER.log(Level.INFO, "com.me.ems.security.securegatewayserver.core.SecureGatewayServerUtils.initiateSyncTask started");
            final Map result = (Map)getResponseFromSecureGatewayServer("/syncDataServlet", Map.class, "application/json");
            SecureGatewayServerUtils.LOGGER.log(Level.INFO, "Response from Secure Gateway Server : " + result);
            if (result.containsKey("uiEnabled")) {
                return Boolean.valueOf(String.valueOf(result.get("uiEnabled")));
            }
        }
        catch (final Exception e) {
            SecureGatewayServerUtils.LOGGER.log(Level.SEVERE, "Exception occured while Synchronising data to the Secure Gateway Server .", e);
        }
        return null;
    }
    
    public static boolean isSecureGatewayServerUpToDate() {
        SecureGatewayServerUtils.LOGGER.log(Level.INFO, "Going Check whether the Secure Gateway server is up-to-date.");
        final String compatibleBuildNumberFromDB = SecurityUtil.getSecurityParameter("COMPATIBLE_SGS_BUILD_NUMBER");
        if (compatibleBuildNumberFromDB == null) {
            SecureGatewayServerUtils.LOGGER.log(Level.SEVERE, "Unable to retrieve Compatible SGS build number from Security params table.");
            return false;
        }
        if (!Pattern.matches("^[\\d]*$", compatibleBuildNumberFromDB)) {
            SecureGatewayServerUtils.LOGGER.log(Level.SEVERE, "Invalid number format");
            return false;
        }
        final Long compatibleSGSBuildNumber = new Long(compatibleBuildNumberFromDB);
        final Long existingSGSBuildNumber = new Long(SecureGatewayServerPropertiesUtils.getSGSProperty("buildNumber"));
        SecureGatewayServerUtils.LOGGER.log(Level.INFO, "Existing Secure Gateway Server build " + existingSGSBuildNumber + ".Compatible SGS build " + compatibleSGSBuildNumber);
        return compatibleSGSBuildNumber <= existingSGSBuildNumber;
    }
    
    public static void updateSGSinstallEventInServerInfoProps(final Boolean isInstalled) {
        final Properties serverInfoProps = new Properties();
        ((Hashtable<String, String>)serverInfoProps).put("isSecureGatewayServerInstalled", Boolean.toString(isInstalled));
        if (isInstalled) {
            ((Hashtable<String, String>)serverInfoProps).put("SGSBuildNumber", SecureGatewayServerPropertiesUtils.getSGSProperty("buildNumber"));
        }
        SecureGatewayServerUtils.LOGGER.log(Level.INFO, "Props to going to update in server_info.props is " + serverInfoProps);
        final String serverInfoConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "server_info.props";
        try {
            FileAccessUtil.storeProperties(serverInfoProps, serverInfoConfFile, true);
        }
        catch (final Exception e) {
            SecureGatewayServerUtils.LOGGER.log(Level.SEVERE, "Exception occured while updating Secure Gateway Server installation status to server_info.props");
        }
    }
    
    public static String getProxyFile() {
        String proxyFileData = new String();
        try {
            final String filePath = WebServerUtil.getServerHomeCanonicalPath() + File.separator + "conf" + File.separator + "security" + File.separator + "nginx-proxy.conf.template";
            final byte[] fileContent = Files.readAllBytes(Paths.get(filePath, new String[0]));
            proxyFileData = new String(fileContent);
        }
        catch (final Exception e) {
            SecureGatewayServerUtils.LOGGER.log(Level.WARNING, "Exception while reading proxy file", e);
        }
        return proxyFileData;
    }
    
    public static boolean getUIAccessEnableData() {
        boolean uiEnable = false;
        try {
            final Properties webServerProps = WebServerUtil.getWebServerSettings();
            uiEnable = (webServerProps.containsKey("ui.fws.enabled") && webServerProps.getProperty("ui.fws.enabled").equalsIgnoreCase("true"));
        }
        catch (final Exception e) {
            SecureGatewayServerUtils.LOGGER.log(Level.SEVERE, "SecureGatewayServerAPI.getUIAccessEnableData - ", e);
        }
        SecureGatewayServerUtils.LOGGER.log(Level.INFO, "UI Access Data SYNC - " + uiEnable);
        return uiEnable;
    }
    
    public static Map getExceptionMap(final Exception exception) {
        return new HashMap<String, String>() {
            {
                this.put(" Exception ", exception.toString());
            }
        };
    }
    
    public static SecureGatewayServerCertificate getCertificateFromServer() throws Exception {
        final SecureGatewayServerCertificate secureGatewayServerCertificate = new SecureGatewayServerCertificate();
        final String webServerName = WebServerUtil.getWebServerName().trim();
        final String serverHome = System.getProperty("server.home");
        final String serverCertificate = serverHome + File.separator + webServerName + File.separator + "conf" + File.separator + "server.crt";
        final String serverKey = serverHome + File.separator + webServerName + File.separator + "conf" + File.separator + "server.key";
        final String intermediateCert = serverHome + File.separator + webServerName + File.separator + "conf" + File.separator + "intermediate.crt";
        secureGatewayServerCertificate.setWebServerName(webServerName);
        secureGatewayServerCertificate.setServerCertificate(new String(Files.readAllBytes(Paths.get(serverCertificate, new String[0]))));
        secureGatewayServerCertificate.setServerKey(new String(Files.readAllBytes(Paths.get(serverKey, new String[0]))));
        if (webServerName.equalsIgnoreCase("apache") && Files.exists(Paths.get(intermediateCert, new String[0]), new LinkOption[0])) {
            secureGatewayServerCertificate.setIntermediateCertificate(new String(Files.readAllBytes(Paths.get(intermediateCert, new String[0]))));
        }
        else {
            secureGatewayServerCertificate.setIntermediateCertificate(null);
        }
        secureGatewayServerCertificate.setClientCertificateVerificationEnabled(ClientCertificateUtil.getInstance().isClientCertAuthEnabledFromWebSettings());
        secureGatewayServerCertificate.setClientRootCACertificate(new String(Files.readAllBytes(ClientCertificateUtil.getInstance().getClientRootCACertificatePath())));
        secureGatewayServerCertificate.setSgsClientCertificateKeyPair(ClientCertificateGenerator.getInstance().getClientCertificateAndKeyForSecureGatewayServer());
        final Map httpHeadersMap = ClientCertAuthBean.getInstance().getClientCertAuthConfig().get("httpHeaders");
        secureGatewayServerCertificate.setClientCertificateHeaderName(httpHeadersMap.get("inSecureGatewayServer").toString());
        SecureGatewayServerUtils.LOGGER.info("Server certificates " + secureGatewayServerCertificate);
        return secureGatewayServerCertificate;
    }
    
    public static SecureGatewayServerSecurityConfiguration getSecurityConfigurations() throws Exception {
        final SecureGatewayServerSecurityConfiguration secureGatewayServerSecurityConfigurations = new SecureGatewayServerSecurityConfiguration();
        final Properties webServerProps = WebServerUtil.getWebServerSettings();
        if (webServerProps.containsKey("IsTLSV2Enabled")) {
            secureGatewayServerSecurityConfigurations.setTLSv2Enabled(Boolean.valueOf(webServerProps.getProperty("IsTLSV2Enabled")));
        }
        else {
            secureGatewayServerSecurityConfigurations.setTLSv2Enabled(Boolean.FALSE);
        }
        if (webServerProps.containsKey("webserver.cipheroption")) {
            if (((Hashtable<K, Object>)webServerProps).get("webserver.cipheroption").equals("webserver.commoncipher")) {
                secureGatewayServerSecurityConfigurations.setCiphers(((Hashtable<K, Object>)webServerProps).get("webserver.commoncipher").toString());
            }
            else if (((Hashtable<K, Object>)webServerProps).get("webserver.cipheroption").equals("webserver.winxpcipher")) {
                secureGatewayServerSecurityConfigurations.setCiphers(((Hashtable<K, Object>)webServerProps).get("webserver.winxpcipher").toString());
            }
        }
        if (webServerProps.containsKey("apache.sslhonorcipherorder")) {
            secureGatewayServerSecurityConfigurations.setSSLHonorCipherOrder(((Hashtable<K, Object>)webServerProps).get("apache.sslhonorcipherorder").toString());
        }
        SecureGatewayServerUtils.LOGGER.log(Level.INFO, "Security Config SYNC - " + secureGatewayServerSecurityConfigurations);
        return secureGatewayServerSecurityConfigurations;
    }
    
    private static X509Certificate generateCertificate(final String certStr) throws CertificateException {
        return (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certStr.getBytes()));
    }
    
    public static Boolean isSGSUIAccessAllowed() {
        try {
            return Boolean.valueOf(SecurityUtil.getSecurityParameter("IS_SGS_UI_ACCESS_ALLOWED"));
        }
        catch (final Exception exception) {
            SecureGatewayServerUtils.LOGGER.log(Level.SEVERE, "isSGSUIAccessAllowed(): exception while finding showUIAccess in feature param.", exception);
            return Boolean.FALSE;
        }
    }
    
    static {
        LOGGER = Logger.getLogger("SecurityLogger");
        SecureGatewayServerUtils.serverHome = System.getProperty("server.home");
        SERVER_KEYSTORE_PATH = SecureGatewayServerUtils.serverHome + File.separator + "conf" + File.separator + "server" + ".keystore";
    }
}
