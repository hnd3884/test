package com.me.devicemanagement.onpremise.server.util;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.general.InstallationTrackingAPI;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.util.regex.Pattern;
import java.util.Arrays;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.text.DateFormat;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.zoho.framework.utils.crypto.EnDecrypt;
import com.adventnet.persistence.DataAccessException;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.zoho.framework.utils.crypto.EnDecryptAES256Impl;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataObject;
import java.net.SocketAddress;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.io.InputStream;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import com.me.devicemanagement.framework.server.mailmanager.DCTrustManager;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.io.FileInputStream;
import javax.net.ssl.SSLSocketFactory;
import org.json.JSONObject;
import java.util.Map;
import java.util.HashMap;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.cert.X509Certificate;
import java.net.ProtocolException;
import java.net.MalformedURLException;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.me.devicemanagement.onpremise.server.certificate.SSLCertificateUtil;
import java.io.IOException;
import java.net.SocketTimeoutException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;
import java.net.Proxy;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.onpremise.webclient.admin.certificate.CertificateUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.security.CommonCryptoImpl;
import java.util.logging.Logger;
import java.util.Properties;

public class FwsUtil
{
    private static final String GET_CERTIFICATE_URL = "/getCertificate";
    private static final String SYNC_DATA_URL = "/syncDataServlet";
    private static final String UNINSTALL_FWS_URL = "/uninstallFws";
    public static Properties fsProps;
    public static final String UI_FWS_ENABLED = "ui.fws.enabled";
    private static final Logger OUT;
    private static CommonCryptoImpl crypt;
    public static String fsPropsFile;
    private static String serverHome;
    private static final String SERVER_KEYSTORE_FILENAME = "server";
    private static final String SERVER_KEYSTORE_PATH;
    private static final String SERVER_KEYSTORE_ALIAS = "tomcat";
    private static final String SERVER_KEYSTORE_PASSWORD = "changeit";
    public static final String TRIAL_PERIOD = "60";
    
    public static void generateserverKeystore() {
        try {
            FwsUtil.OUT.log(Level.INFO, "generateserverKeystore: Generating server.keystore ");
            CertificateUtil.getInstance().generateServerKeystoreFile(FwsUtil.SERVER_KEYSTORE_PATH, "tomcat", "changeit");
        }
        catch (final Exception ex) {
            FwsUtil.OUT.log(Level.SEVERE, "generateserverKeystore: Exception while generating keystore", ex);
        }
    }
    
    public static void regenerateProps() {
        try {
            final String path = System.getProperty("server.home");
            FwsUtil.fsProps = FileAccessUtil.readProperties(path + File.separator + FwsUtil.fsPropsFile);
        }
        catch (final Exception e) {
            FwsUtil.OUT.log(Level.SEVERE, "Exception while intiailizing properties", e);
        }
    }
    
    public static boolean isServerUp(final String serverName, final int httpsPort) {
        boolean isServerUp = false;
        try {
            final String fwsServerURL = "https://" + serverName + ":" + httpsPort;
            final URL todcurl = new URL(fwsServerURL);
            final HttpsURLConnection fsconnect = (HttpsURLConnection)todcurl.openConnection(Proxy.NO_PROXY);
            fsconnect.setRequestMethod("GET");
            fsconnect.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(final String hostname, final SSLSession session) {
                    return true;
                }
            });
            fsconnect.setSSLSocketFactory(getSSLSocketFactory());
            fsconnect.setDoOutput(true);
            fsconnect.setDoInput(true);
            if (fsconnect.getContent() != null) {
                isServerUp = true;
                FwsUtil.OUT.log(Level.INFO, "Rec8eived Content-Type from server : {0} , {1} ", new Object[] { fsconnect.getContentType(), serverName });
                FwsUtil.OUT.log(Level.INFO, "Server is running fine in remote machine : {0}", new Object[] { serverName });
            }
            else {
                isServerUp = false;
                FwsUtil.OUT.log(Level.INFO, "Connection to server failed ");
            }
        }
        catch (final SocketTimeoutException e) {
            isServerUp = false;
            FwsUtil.OUT.log(Level.SEVERE, "SocketTimeoutException in isServerUp(){0}", e);
        }
        catch (final IOException exp) {
            isServerUp = false;
            FwsUtil.OUT.log(Level.SEVERE, "IOException in isServerUp(){0}", exp);
        }
        catch (final Exception e2) {
            isServerUp = false;
            FwsUtil.OUT.log(Level.SEVERE, "Exception in isServerUp(){0}", e2);
        }
        return isServerUp;
    }
    
    public static String isForwardingServerConfigured() {
        try {
            final String fsconfigured = SyMUtil.getSyMParameter("forwarding_server_config");
            if (fsconfigured != null) {
                return fsconfigured;
            }
        }
        catch (final Exception e) {
            FwsUtil.OUT.log(Level.WARNING, "Exception in getForwardingServerConfigured", e);
        }
        return "false";
    }
    
    public static Boolean getCertSyncDetails(final String serverName, final int port) {
        Boolean value = false;
        final CertificateUtil crtUtilInstance = CertificateUtil.getInstance();
        try {
            final String serverCrtFilePath = SSLCertificateUtil.getInstance().getServerCertificateFilePath();
            String authToken = SecurityUtil.getAdvancedSecurityDetail("SGS_AUTH_TOKEN");
            if (authToken == null || authToken.isEmpty()) {
                Logger.getLogger("SecurityLogger").log(Level.WARNING, "No Authtoken found while invoking following request : /getCertificate");
                authToken = "";
            }
            final String responseText = getServerResponseSecured(serverName, port, "/getCertificate", authToken);
            final X509Certificate crt = generateCrt(responseText);
            value = crt.equals(crtUtilInstance.generateCertificateFromFile(serverCrtFilePath));
            return value;
        }
        catch (final MalformedURLException e) {
            Logger.getLogger(FwsUtil.class.getName()).log(Level.SEVERE, "MalformedURLException in getCertSyncDetails()", e);
            value = false;
        }
        catch (final ProtocolException e2) {
            Logger.getLogger(FwsUtil.class.getName()).log(Level.SEVERE, "ProtocolException in getCertSyncDetails()", e2);
            value = false;
        }
        catch (final IOException e3) {
            Logger.getLogger(FwsUtil.class.getName()).log(Level.SEVERE, "IOException in getCertSyncDetails()", e3);
            value = false;
        }
        catch (final Exception e4) {
            Logger.getLogger(FwsUtil.class.getName()).log(Level.SEVERE, "Exception in getCertSyncDetails()", e4);
            value = false;
        }
        return value;
    }
    
    private static String getServerResponseSecured(final String serverName, final int port, final String urlSuffix, final String authToken) throws Exception {
        final String connectionURL = "https://" + serverName + ":" + port + urlSuffix;
        final URL todcurl = new URL(connectionURL);
        final HttpsURLConnection dcconnect = (HttpsURLConnection)todcurl.openConnection(Proxy.NO_PROXY);
        dcconnect.setRequestMethod("GET");
        dcconnect.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(final String hostname, final SSLSession session) {
                return true;
            }
        });
        dcconnect.setSSLSocketFactory(getSSLSocketFactory());
        dcconnect.setRequestProperty("Authorization", authToken);
        dcconnect.setDoOutput(true);
        dcconnect.setDoInput(true);
        final BufferedReader in = new BufferedReader(new InputStreamReader(dcconnect.getInputStream()));
        if (dcconnect.getResponseCode() != 200) {
            return null;
        }
        final StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
            content.append("\n");
        }
        in.close();
        return content.toString();
    }
    
    public static String getResponseFromSecureGatewayServer(final String urlSuffix) throws Exception {
        final Properties natProps = NATHandler.getNATConfigurationProperties();
        String serverName;
        if (FwsUtil.fsProps.getProperty("publicIP") != null) {
            serverName = FwsUtil.fsProps.getProperty("publicIP");
        }
        else {
            serverName = natProps.getProperty("NAT_ADDRESS");
        }
        final int httpsPort = ((Hashtable<K, Integer>)natProps).get("NAT_HTTPS_PORT");
        String authToken = SecurityUtil.getAdvancedSecurityDetail("SGS_AUTH_TOKEN");
        if (authToken == null || authToken.isEmpty()) {
            Logger.getLogger("SecurityLogger").log(Level.WARNING, "No Authtoken found while invoking following request : " + urlSuffix);
            authToken = "";
        }
        return getServerResponseSecured(serverName, httpsPort, urlSuffix, authToken);
    }
    
    public static Boolean uninstallForwardingServer() throws Exception {
        final Properties natProps = NATHandler.getNATConfigurationProperties();
        String serverName = null;
        if (FwsUtil.fsProps.getProperty("publicIP") != null) {
            serverName = FwsUtil.fsProps.getProperty("publicIP");
        }
        else {
            serverName = natProps.getProperty("NAT_ADDRESS");
        }
        final int httpsPort = ((Hashtable<K, Integer>)natProps).get("NAT_HTTPS_PORT");
        final Map fwspropsMap = new HashMap();
        fwspropsMap.put("fws_status", "false");
        FileAccessUtil.writeMapAsPropertiesIntoFile(fwspropsMap, FwsUtil.fsPropsFile, "");
        regenerateProps();
        SyMUtil.updateSyMParameter("forwarding_server_config", "false");
        return uninstallForwardingServer(serverName, httpsPort);
    }
    
    public static Boolean uninstallForwardingServer(final String serverName, final int port) {
        try {
            final String dcUrl = "https://" + serverName + ":" + port + "/uninstallFws";
            final URL todcurl = new URL(dcUrl);
            final HttpsURLConnection dcconnect = (HttpsURLConnection)todcurl.openConnection(Proxy.NO_PROXY);
            dcconnect.setRequestMethod("GET");
            dcconnect.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(final String hostname, final SSLSession session) {
                    return true;
                }
            });
            dcconnect.setSSLSocketFactory(getSSLSocketFactory());
            dcconnect.setDoOutput(true);
            dcconnect.setDoInput(true);
            if (dcconnect.getResponseCode() != 200) {
                return true;
            }
        }
        catch (final Exception e) {
            Logger.getLogger(FwsUtil.class.getName()).log(Level.SEVERE, "MalformedURLException in uninstallForwardingServer()", e);
            return true;
        }
        return false;
    }
    
    public static JSONObject getPortReachabilityFromServer(final String serverName, final int tcpPort, final int wssPort, final int ftpPort) throws Exception {
        final JSONObject responseJSON = new JSONObject();
        Boolean tcpReachable = Boolean.TRUE;
        Boolean wssReachable = Boolean.TRUE;
        Boolean ftpReachable = Boolean.TRUE;
        if (FwsUtil.fsProps.getProperty("wssForwardingEnabled").equalsIgnoreCase("true")) {
            wssReachable = isServerListening(serverName, wssPort);
        }
        if (FwsUtil.fsProps.getProperty("tcpForwardingEnabled").equalsIgnoreCase("true")) {
            tcpReachable = isServerListening(serverName, tcpPort);
        }
        if (FwsUtil.fsProps.getProperty("ftpForwardingEnabled").equalsIgnoreCase("true")) {
            ftpReachable = isServerListening(serverName, ftpPort);
        }
        responseJSON.put("tcpReachable", (Object)tcpReachable);
        responseJSON.put("wssReachable", (Object)wssReachable);
        responseJSON.put("ftpReachable", (Object)ftpReachable);
        return responseJSON;
    }
    
    public static JSONObject getFwsServerStatus(final String serverName, final int port) {
        JSONObject responseJSON = null;
        try {
            String authToken = SecurityUtil.getAdvancedSecurityDetail("SGS_AUTH_TOKEN");
            if (authToken == null || authToken.isEmpty()) {
                Logger.getLogger("SecurityLogger").log(Level.WARNING, "No Authtoken found while invoking following request : /getStatus");
                authToken = "";
            }
            responseJSON = new JSONObject(getServerResponseSecured(serverName, port, "/getStatus", authToken));
            return responseJSON;
        }
        catch (final MalformedURLException e) {
            Logger.getLogger(FwsUtil.class.getName()).log(Level.SEVERE, "MalformedURLException in getFwsServerStatus()", e);
        }
        catch (final ProtocolException e2) {
            Logger.getLogger(FwsUtil.class.getName()).log(Level.SEVERE, "ProtocolException in getFwsServerStatus()", e2);
        }
        catch (final IOException e3) {
            Logger.getLogger(FwsUtil.class.getName()).log(Level.SEVERE, "IOException in getFwsServerStatus()", e3);
        }
        catch (final Exception e4) {
            Logger.getLogger(FwsUtil.class.getName()).log(Level.SEVERE, "IOException in getFwsServerStatus()", e4);
        }
        return responseJSON;
    }
    
    public static SSLSocketFactory getSSLSocketFactory() throws Exception {
        InputStream in = null;
        final File crtFile = new File(FwsUtil.SERVER_KEYSTORE_PATH);
        if (crtFile.exists()) {
            in = new FileInputStream(crtFile);
            final char[] passphrase = "changeit".toCharArray();
            final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(in, passphrase);
            in.close();
            final SSLContext context = SSLContext.getInstance("TLS");
            final DCTrustManager tm = new DCTrustManager(FwsUtil.SERVER_KEYSTORE_PATH);
            context.init(null, new TrustManager[] { (TrustManager)tm }, null);
            final SSLSocketFactory factory = context.getSocketFactory();
            return factory;
        }
        return null;
    }
    
    public static X509Certificate generateCrt(final String certStr) throws CertificateException {
        return (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certStr.getBytes()));
    }
    
    public static boolean isServerListening(final String host, final int port) {
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
    
    public static DataObject getEmailAddDO(final String module) throws Exception {
        final Column col = Column.getColumn("EMailAddr", "MODULE");
        final Criteria crit = new Criteria(col, (Object)module, 0, false);
        final DataObject mailDObj = SyMUtil.getPersistence().get("EMailAddr", crit);
        return mailDObj;
    }
    
    public static synchronized void addOrUpdateEmailAddr(final String module, final boolean emailStatus, final String emailAddress) throws Exception {
        final String sourceMethod = "addOrUpdateEmailAddr";
        try {
            final DataObject mailDObj = getEmailAddDO(module);
            if (!mailDObj.isEmpty()) {
                final Row row = mailDObj.getRow("EMailAddr");
                row.set("SEND_MAIL", (Object)emailStatus);
                row.set("EMAIL_ADDR", (Object)emailAddress);
                mailDObj.updateRow(row);
            }
            else {
                final Row row = new Row("EMailAddr");
                row.set("SEND_MAIL", (Object)emailStatus);
                row.set("MODULE", (Object)module);
                row.set("EMAIL_ADDR", (Object)emailAddress);
                mailDObj.addRow(row);
            }
            SyMUtil.getPersistence().update(mailDObj);
        }
        catch (final Exception ex) {
            FwsUtil.OUT.log(Level.WARNING, "Exception occured : \n", ex);
        }
    }
    
    public static synchronized void addOrUpdateEmailAddr(final String module, final boolean emailStatus) throws Exception {
        final String sourceMethod = "addOrUpdateEmailAddr";
        try {
            final DataObject mailDObj = getEmailAddDO(module);
            if (!mailDObj.isEmpty()) {
                final Row row = mailDObj.getRow("EMailAddr");
                row.set("SEND_MAIL", (Object)emailStatus);
                mailDObj.updateRow(row);
            }
            else {
                final Row row = new Row("EMailAddr");
                row.set("SEND_MAIL", (Object)emailStatus);
                row.set("MODULE", (Object)module);
                row.set("EMAIL_ADDR", (Object)"");
                mailDObj.addRow(row);
            }
            SyMUtil.getPersistence().update(mailDObj);
        }
        catch (final Exception ex) {
            FwsUtil.OUT.log(Level.WARNING, "Exception occured : \n", ex);
        }
    }
    
    public static void setAndReloadFwsEntries(final Map props) throws Exception {
        final String confFilePath = System.getProperty("server.home") + File.separator + FwsUtil.fsPropsFile;
        if (!new File(confFilePath).exists()) {
            new File(confFilePath).createNewFile();
        }
        FileAccessUtil.writeMapAsPropertiesIntoFile(props, confFilePath, "");
        regenerateProps();
    }
    
    public static void cleanUpFwsEntries() {
        try {
            final EnDecrypt ed = (EnDecrypt)new EnDecryptAES256Impl();
            CryptoUtil.setEnDecryptInstance(ed);
            final Map fwspropsMap = new HashMap();
            fwspropsMap.put("fws_status", "false");
            final String fwsTrialValidity = FwsUtil.fsProps.getProperty("FwsTrialValidity");
            if (fwsTrialValidity != null) {
                fwspropsMap.put("FwsTrialValidity", fwsTrialValidity);
            }
            final String isFwsTrialed = FwsUtil.fsProps.getProperty("isFwsTrialed");
            if (isFwsTrialed != null) {
                fwspropsMap.put("isFwsTrialed", isFwsTrialed);
            }
            else {
                fwspropsMap.put("isFwsTrialed", CryptoUtil.encrypt("false"));
            }
            final String fwsTrialPeriod = FwsUtil.fsProps.getProperty("FwsTrialPeriod");
            if (fwsTrialPeriod != null) {
                fwspropsMap.put("FwsTrialPeriod", fwsTrialPeriod);
            }
            else {
                fwspropsMap.put("FwsTrialPeriod", CryptoUtil.encrypt("60"));
            }
            setAndReloadFwsEntries(fwspropsMap);
        }
        catch (final DataAccessException ex) {
            FwsUtil.OUT.log(Level.SEVERE, "Exception while deleting fws details", (Throwable)ex);
        }
        catch (final Exception ex2) {
            FwsUtil.OUT.log(Level.SEVERE, null, ex2);
        }
    }
    
    public static Boolean canOptTrial() {
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        if (licenseType.equalsIgnoreCase("R")) {
            return !isFwsTrialFlagEnabled();
        }
        return Boolean.FALSE;
    }
    
    public static Boolean isFwsTrialFlagEnabled() {
        try {
            if (FwsUtil.fsProps != null) {
                final String isFwsTrialed = FwsUtil.fsProps.getProperty("isFwsTrialed");
                if (isFwsTrialed != null) {
                    if (FwsUtil.crypt.decrypt(isFwsTrialed).equalsIgnoreCase("true")) {
                        FwsUtil.OUT.log(Level.INFO, " trial license available - true");
                        return Boolean.TRUE;
                    }
                    FwsUtil.OUT.log(Level.INFO, " trial license available - false");
                    return Boolean.FALSE;
                }
            }
        }
        catch (final Exception exc) {
            FwsUtil.OUT.log(Level.WARNING, " error while checking FWS trial enabled flag");
        }
        return Boolean.FALSE;
    }
    
    public static void resetFwsTrialFlag() {
        FwsUtil.fsProps.setProperty("isFwsTrialed", FwsUtil.crypt.encrypt("expired"));
        try {
            setAndReloadFwsEntries(FwsUtil.fsProps);
        }
        catch (final Exception e) {
            FwsUtil.OUT.log(Level.SEVERE, "Exception while resettings trial flag");
        }
    }
    
    public static boolean getFwsTrial(final HttpServletRequest request) {
        try {
            FwsUtil.OUT.log(Level.INFO, " FWS Trial license adapter");
            cleanUpFwsEntries();
            if (FwsUtil.fsProps != null && FwsUtil.fsProps.getProperty("FwsTrialPeriod") != null && FwsUtil.fsProps.getProperty("isFwsTrialed") != null) {
                FwsUtil.fsProps.setProperty("isFwsTrialed", FwsUtil.crypt.encrypt("true"));
                final String encTrialperiod = FwsUtil.fsProps.getProperty("FwsTrialPeriod");
                final int trialdays = Integer.parseInt(FwsUtil.crypt.decrypt(encTrialperiod));
                final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                final Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(5, trialdays);
                final String dateString = sdf.format(c.getTime());
                FwsUtil.fsProps.setProperty("FwsTrialValidity", FwsUtil.crypt.encrypt(dateString));
                setAndReloadFwsEntries(FwsUtil.fsProps);
                FwsUtil.OUT.log(Level.INFO, "Trial Opted - validity updated " + FwsUtil.fsProps.toString());
                return Boolean.TRUE;
            }
            FwsUtil.OUT.log(Level.INFO, " FWS properties trialPeriod not available");
        }
        catch (final Exception exc) {
            FwsUtil.OUT.log(Level.WARNING, " error while getting FWS trial service " + exc.getMessage());
        }
        return Boolean.FALSE;
    }
    
    public static String getFwsTrialValidity() throws Exception {
        String fwsTrialValidity = null;
        if (FwsUtil.fsProps != null) {
            fwsTrialValidity = FwsUtil.fsProps.getProperty("FwsTrialValidity");
            if (fwsTrialValidity != null) {
                return FwsUtil.crypt.decrypt(fwsTrialValidity);
            }
        }
        return fwsTrialValidity;
    }
    
    public static long getFwsTrialExpiryPeriod() {
        long dateDiff = -1L;
        try {
            if (FwsUtil.fsProps != null) {
                final String fwsTrialValidity = getFwsTrialValidity();
                if (fwsTrialValidity != null) {
                    final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    final Date date = formatter.parse(fwsTrialValidity);
                    final Date today = Calendar.getInstance().getTime();
                    dateDiff = SyMUtil.getDateDiff(today.getTime(), date.getTime());
                    FwsUtil.OUT.log(Level.INFO, "FWS Trial validity " + dateDiff);
                    return dateDiff;
                }
            }
        }
        catch (final Exception exc) {
            FwsUtil.OUT.log(Level.WARNING, " error while fetch FWS trial license period " + exc);
        }
        FwsUtil.OUT.log(Level.INFO, " DB FWS Trial expiry period " + dateDiff);
        return dateDiff;
    }
    
    private static DataObject getServerParamsDO(final String paramName) throws Exception {
        final Column col = Column.getColumn("ServerParams", "PARAM_NAME");
        final Criteria crit = new Criteria(col, (Object)paramName, 2, false);
        final DataObject serverParamsDObj = com.me.devicemanagement.framework.server.util.SyMUtil.getPersistence().get("ServerParams", crit);
        return serverParamsDObj;
    }
    
    public static String enableUIAccess(final String enableUIAccess) {
        String valueToUpdate = "false";
        if (enableUIAccess != null && enableUIAccess.equalsIgnoreCase("true")) {
            valueToUpdate = "true";
        }
        String status;
        try {
            WebServerUtil.addOrUpdateWebServerProps("ui.fws.enabled", enableUIAccess);
            status = (String)initiateSyncTask();
            WebServerUtil.addOrUpdateWebServerProps("ui.fws.enabled", status);
        }
        catch (final Exception e) {
            FwsUtil.OUT.log(Level.SEVERE, "Exception while enabling UI in Forwarding server : " + e);
            status = "false";
        }
        return Arrays.toString(new Boolean[] { Boolean.valueOf(status), Boolean.valueOf(valueToUpdate) });
    }
    
    public static Object initiateSyncTask() throws Exception {
        FwsUtil.OUT.log(Level.INFO, "com.me.devicemanagement.onpremise.server.util.FwsUtil.initiateSyncTask started");
        final String result = getResponseFromSecureGatewayServer("/syncDataServlet");
        FwsUtil.OUT.log(Level.INFO, "Response from Secure Gateway Server : " + result);
        final JSONObject resultValue = new JSONObject(result);
        if (resultValue.has("uiEnabled")) {
            return resultValue.get("uiEnabled");
        }
        return "false";
    }
    
    public static boolean isSecureGatewayServerUpToDate() {
        FwsUtil.OUT.log(Level.INFO, "Going Check whether the Secure Gateway server is up-to-date.");
        final String compatibleBuildNumberFromDB = SecurityUtil.getSecurityParameter("COMPATIBLE_SGS_BUILD_NUMBER");
        if (compatibleBuildNumberFromDB == null) {
            FwsUtil.OUT.log(Level.SEVERE, "Unable to retrieve Compatible SGS build number from Security params table.");
            return false;
        }
        if (!Pattern.matches("^[\\d]*$", compatibleBuildNumberFromDB)) {
            FwsUtil.OUT.log(Level.SEVERE, "Invalid number format");
            return false;
        }
        final Long compatibleSGSBuildNumber = new Long(compatibleBuildNumberFromDB);
        final Long existingSGSBuildNumber = new Long(FwsUtil.fsProps.getProperty("buildNumber"));
        FwsUtil.OUT.log(Level.INFO, "Existing Secure Gateway Server build " + existingSGSBuildNumber + ".Compatible SGS build " + compatibleSGSBuildNumber);
        return compatibleSGSBuildNumber <= existingSGSBuildNumber;
    }
    
    public static void updateSGSinstallEventInServerInfoProps(final Boolean isInstalled) {
        final InstallationTrackingAPI installationTracking = ApiFactoryProvider.installationTrackingAPIImpl();
        final Properties serverInfoProps = new Properties();
        final Properties fwsProps = FwsUtil.fsProps;
        ((Hashtable<String, String>)serverInfoProps).put("isSecureGatewayServerInstalled", Boolean.toString(isInstalled));
        if (isInstalled && fwsProps.containsKey("buildNumber")) {
            ((Hashtable<String, String>)serverInfoProps).put("SGSBuildNumber ", fwsProps.getProperty("buildNumber"));
        }
        FwsUtil.OUT.log(Level.INFO, "Props to going to update in server_info.props is " + serverInfoProps);
        final String serverInfoConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "server_info.props";
        try {
            FileAccessUtil.storeProperties(serverInfoProps, serverInfoConfFile, true);
        }
        catch (final Exception e) {
            FwsUtil.OUT.log(Level.SEVERE, "Exception occured while updating Secure Gateway Server installation status to server_info.props");
        }
    }
    
    static {
        FwsUtil.fsProps = new Properties();
        OUT = Logger.getLogger(FwsUtil.class.getName());
        FwsUtil.crypt = new CommonCryptoImpl();
        FwsUtil.fsPropsFile = "conf" + File.separator + "fwsSettings.conf";
        FwsUtil.serverHome = System.getProperty("server.home");
        SERVER_KEYSTORE_PATH = FwsUtil.serverHome + File.separator + "conf" + File.separator + "server" + ".keystore";
        try {
            final String path = System.getProperty("server.home");
            FwsUtil.fsProps = FileAccessUtil.readProperties(path + File.separator + FwsUtil.fsPropsFile);
            FwsUtil.OUT.log(Level.INFO, "Forwarding server properties loaded" + FwsUtil.fsProps);
        }
        catch (final Exception e) {
            FwsUtil.OUT.log(Level.SEVERE, "Exception while intiailizing properties", e);
        }
    }
}
