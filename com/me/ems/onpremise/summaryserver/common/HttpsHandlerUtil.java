package com.me.ems.onpremise.summaryserver.common;

import java.util.Hashtable;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStoreException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.security.cert.Certificate;
import java.security.KeyStore;
import java.io.FileInputStream;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import org.json.JSONArray;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.OutputStreamWriter;
import org.json.JSONObject;
import java.io.IOException;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import sun.misc.BASE64Encoder;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.Properties;
import java.net.URL;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;
import org.apache.commons.io.FileUtils;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.security.cert.CertificateException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import java.util.HashMap;
import java.io.File;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import java.security.cert.X509Certificate;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import javax.net.ssl.HttpsURLConnection;
import java.util.logging.Logger;

public class HttpsHandlerUtil
{
    public static Logger logger;
    public static final String SERVER_HOME;
    public static final String TRUST_STORE_PATH;
    private static final String ALIAS_CERT_NAME = "sscert";
    
    public static synchronized void processCertificateFromServer(final String urlStr, final Long probeId) {
        try {
            final HttpsURLConnection httpsURLConnection = (HttpsURLConnection)getServerUrlConnection(urlStr);
            processCertificateFromServer(httpsURLConnection, probeId);
        }
        catch (final Exception e) {
            HttpsHandlerUtil.logger.log(Level.SEVERE, "Exception while processCertificateFromServer", e);
        }
    }
    
    public static synchronized void processCertificateFromServer(final HttpsURLConnection httpsURLConnection, final Long probeId) {
        try {
            copycacertsFile();
            skipCertificateCheck(httpsURLConnection);
            httpsURLConnection.connect();
            final X509Certificate[] array;
            final X509Certificate[] certificates = array = (X509Certificate[])httpsURLConnection.getServerCertificates();
            for (final X509Certificate certificate : array) {
                HttpsHandlerUtil.logger.log(Level.INFO, "CERT------!!!", certificate.getIssuerDN() + "-----" + certificate.toString());
            }
            validateAndStoreCertificates(probeId, certificates);
            httpsURLConnection.disconnect();
        }
        catch (final Exception e) {
            HttpsHandlerUtil.logger.log(Level.SEVERE, "Exception while processCertificateFromServer", e);
        }
    }
    
    public static void validateAndStoreCertificates(final Long probeId, final X509Certificate[] certificates) throws Exception {
        final SSLCertificateUtil sslCertificateUtil = SSLCertificateUtil.getInstance();
        X509Certificate selfSignedCertficate = null;
        if (!sslCertificateUtil.isThirdPartySSLInstalled()) {
            selfSignedCertficate = SSLCertificateUtil.getCertificate(sslCertificateUtil.getServerCACertificateFilePath());
        }
        for (int i = 0; i < certificates.length; ++i) {
            final X509Certificate certificate = certificates[i];
            try {
                certificate.checkValidity();
                if (selfSignedCertficate != null) {
                    if (selfSignedCertficate.getSubjectDN().equals(certificate.getIssuerDN())) {
                        final String alias = getAliasCertName(probeId) + "-" + i;
                        addCertificateToCaCerts(certificate, new File(HttpsHandlerUtil.TRUST_STORE_PATH), alias);
                    }
                    else {
                        HttpsHandlerUtil.logger.log(Level.SEVERE, "Not a Certificate from ManageEngine, Couldn't trust this");
                    }
                }
                else {
                    final HashMap certificateDetails = (HashMap)sslCertificateUtil.getCertificateDetails(certificate);
                    final String organizationName = certificateDetails.get("OrganizationName");
                    final String organizationalUnit = certificateDetails.get("OrganizationalUnit");
                    if ("Zoho".equalsIgnoreCase(organizationName) && "ManageEngine".equalsIgnoreCase(organizationalUnit)) {
                        final String alias2 = getAliasCertName(probeId) + "-" + i;
                        addCertificateToCaCerts(certificate, new File(HttpsHandlerUtil.TRUST_STORE_PATH), alias2);
                    }
                    else {
                        HttpsHandlerUtil.logger.log(Level.SEVERE, "Not a Certificate from ManageEngine, Couldn't trust this");
                    }
                }
            }
            catch (final CertificateExpiredException | CertificateNotYetValidException certificateException) {
                HttpsHandlerUtil.logger.log(Level.SEVERE, "certificate is expired or not valid", certificateException);
            }
            catch (final Exception e) {
                HttpsHandlerUtil.logger.log(Level.SEVERE, "Exception while verifying certificate", e);
            }
        }
    }
    
    private static void copycacertsFile() {
        try {
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            final String cacerts = File.separator + "lib" + File.separator + "security" + File.separator + "cacerts";
            final String source = serverHome + File.separator + "jre" + cacerts;
            final String destination = serverHome + File.separator + "jre" + File.separator + "lib" + File.separator + "security" + File.separator + "jssecacerts";
            final File sourceFile = new File(source);
            final File destFile = new File(destination);
            if (sourceFile.exists() && !destFile.exists()) {
                FileUtils.copyFile(sourceFile, destFile);
            }
        }
        catch (final Exception e) {
            HttpsHandlerUtil.logger.log(Level.SEVERE, "error while loading copying cacertsFile", e);
        }
    }
    
    private static String getAliasCertName(final Long probeId) {
        if (probeId != null) {
            return "sscert" + probeId;
        }
        return "sscert";
    }
    
    public static HttpURLConnection skipCertificateCheck(final HttpURLConnection conn) {
        try {
            final TrustManager[] trustAllCerts = { new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    
                    @Override
                    public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
                    }
                    
                    @Override
                    public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
                    }
                } };
            SSLContext sc = null;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            final HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(final String hostname, final SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            ((HttpsURLConnection)conn).setSSLSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory());
            ((HttpsURLConnection)conn).setHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
    
    public static HttpURLConnection getServerUrlConnection(final String url) {
        HttpURLConnection conn = null;
        try {
            final DownloadManager downloadManager = DownloadManager.getInstance();
            String proxyDefined = "false";
            final int proxyType = DownloadManager.proxyType;
            Properties proxyProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
            if (proxyType == 4) {
                proxyProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(url, proxyProps);
            }
            if (isServerProxyEnabled()) {
                proxyDefined = SyMUtil.getSyMParameter("proxy_defined");
            }
            final URL urlObj = new URL(url);
            conn = getProxyAppliedConnection(urlObj, proxyDefined, proxyProps);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
    
    private static boolean isServerProxyEnabled() {
        try {
            final String serverHome = ApiFactoryProvider.getUtilAccessAPI().getServerHome();
            final String serverProxyConf = serverHome + File.separator + "conf" + File.separator + "server_properties.conf";
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(serverProxyConf)) {
                final Properties serverProxyProps = FileAccessUtil.readProperties(serverProxyConf);
                if (serverProxyProps.containsKey("enableServerProxy")) {
                    return serverProxyProps.getProperty("enableServerProxy").equalsIgnoreCase("true");
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static HttpURLConnection getProxyAppliedConnection(final URL url, final String proxyDefined, final Properties proxyProps) {
        final String sourceMethod = "getProxyAppliedConnection";
        HttpURLConnection uc = null;
        try {
            if (proxyDefined != null && proxyDefined.equalsIgnoreCase("true")) {
                final String proxyHost = ((Hashtable<K, String>)proxyProps).get("proxyHost");
                final String proxyPort = ((Hashtable<K, String>)proxyProps).get("proxyPort");
                if (proxyHost != null && proxyPort != null) {
                    final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
                    if (url.toString().contains("https")) {
                        uc = (HttpsURLConnection)url.openConnection(proxy);
                    }
                    else {
                        uc = (HttpURLConnection)url.openConnection(proxy);
                    }
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
                if (url.toString().contains("https")) {
                    uc = (HttpsURLConnection)url.openConnection();
                }
                else {
                    uc = (HttpURLConnection)url.openConnection();
                }
            }
        }
        catch (final IOException e) {
            SyMLogger.error(HttpsHandlerUtil.logger, "HttpsHandlerUtil", sourceMethod, "IOException Occurred : ", (Throwable)e);
        }
        catch (final Exception e2) {
            SyMLogger.error(HttpsHandlerUtil.logger, "HttpsHandlerUtil", sourceMethod, "Exception Occurred : ", (Throwable)e2);
        }
        return uc;
    }
    
    private static void getAndStoreCertificatesFromServer(final String baseUrl, final Long probeId) {
        try {
            final String url = baseUrl + "mdm/DiscoveryService";
            HttpURLConnection conn = getServerUrlConnection(url);
            if (baseUrl.contains("https")) {
                conn = skipCertificateCheck(conn);
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("MessageType", (Object)"CertificateRequest");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            final OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            final String requestBody = requestJSON.toString();
            if (requestBody != null) {
                wr.write(requestJSON.toString());
            }
            wr.flush();
            wr.close();
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            final String response = rd.readLine();
            final JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("MessageResponse")) {
                final JSONObject messageResponse = (JSONObject)jsonObject.get("MessageResponse");
                if (messageResponse.has("IntermediateCertificate")) {
                    final JSONArray intermediateCertResp = (JSONArray)messageResponse.get("IntermediateCertificate");
                    for (int i = 0; i < intermediateCertResp.length(); ++i) {
                        final String cert = (String)intermediateCertResp.get(i);
                        final String alias = getAliasCertName(probeId) + "-" + i;
                        processCertFromEncodedString(cert, alias);
                    }
                }
                if (messageResponse.has("ServerCertificate")) {
                    final String serverCertificateResp = (String)messageResponse.get("ServerCertificate");
                    final String alias2 = getAliasCertName(probeId) + "-ServerCertificate";
                    processCertFromEncodedString(serverCertificateResp, alias2);
                }
            }
        }
        catch (final Exception e) {
            HttpsHandlerUtil.logger.log(Level.SEVERE, "Exception Occurred : getCertificateFromServer ", e);
        }
    }
    
    private static void processCertFromEncodedString(final String encodedCert, final String alias) {
        try {
            final X509Certificate certificate = CertificateUtils.loadX509CertificateFromBuffer(encodedCert);
            addCertificateToCaCerts(certificate, new File(HttpsHandlerUtil.TRUST_STORE_PATH), alias);
        }
        catch (final Exception e) {
            HttpsHandlerUtil.logger.log(Level.SEVERE, "Exception Occurred : storeCertificate ", e);
        }
    }
    
    private static void addCertificateToCaCerts(final X509Certificate certificate, final File truststoreFile, final String alias) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        try {
            final InputStream in = new FileInputStream(truststoreFile);
            final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(in, "changeit".toCharArray());
            in.close();
            final X509Certificate cert = certificate;
            ks.setCertificateEntry(alias, cert);
            final OutputStream out = new FileOutputStream(truststoreFile);
            ks.store(out, "changeit".toCharArray());
            out.close();
        }
        catch (final Exception e) {
            HttpsHandlerUtil.logger.log(Level.SEVERE, "Exception Occurred : addCertificateToCaCerts ", e);
        }
    }
    
    static {
        HttpsHandlerUtil.logger = Logger.getLogger("probeActionsLogger");
        SERVER_HOME = System.getProperty("server.home");
        TRUST_STORE_PATH = HttpsHandlerUtil.SERVER_HOME + File.separator + "jre" + File.separator + "lib" + File.separator + "security" + File.separator + "jssecacerts";
    }
}
