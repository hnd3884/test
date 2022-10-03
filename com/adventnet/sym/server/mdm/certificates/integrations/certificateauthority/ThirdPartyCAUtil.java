package com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.KeyManagerFactory;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Properties;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.adventnet.sym.server.mdm.MDMProxy;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import java.util.logging.Logger;

public class ThirdPartyCAUtil
{
    private static final Logger LOGGER;
    
    private static String getThirdPartyCAFolderPath(final Long customerId) throws Exception {
        final String serverCertificateFilePath = "mdm" + File.separator + "certauth" + File.separator + "integration" + File.separator + "certificateauthority" + File.separator + customerId;
        return serverCertificateFilePath;
    }
    
    public static String getDigicertFolderPath(final Long customerId, final int version) throws Exception {
        final String serverCertificateFilePath = getThirdPartyCAFolderPath(customerId);
        final String digicertFolderPath = serverCertificateFilePath + File.separator + "digicert" + File.separator + "v" + version;
        return digicertFolderPath;
    }
    
    private static String getCAFolderPath() {
        final String caFolderPath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "mdm" + File.separator + "integration" + File.separator + "certificateauthority";
        return caFolderPath;
    }
    
    public static String getDigicertDepedencyFilesPath(final int version) {
        final String digicertDependencyFilesFoldertPath = getCAFolderPath() + File.separator + "digicert" + File.separator + "v" + version;
        return digicertDependencyFilesFoldertPath;
    }
    
    public static MDMProxy getMdmProxy() throws Exception {
        final int proxyType = DownloadManager.proxyType;
        String proxyHost = null;
        String proxyPort = null;
        final Properties proxyDetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        ThirdPartyCAUtil.LOGGER.log(Level.INFO, "ThirdPartyCAUtil: Proxy type - {0}", new Object[] { proxyType });
        if (proxyType == 4) {
            final String url = "";
            final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(url, proxyDetails);
            proxyHost = ((Hashtable<K, String>)pacProps).get("proxyHost");
            proxyPort = ((Hashtable<K, String>)pacProps).get("proxyPort");
        }
        else if (proxyType == 2) {
            proxyHost = ((Hashtable<K, String>)proxyDetails).get("proxyHost");
            proxyPort = ((Hashtable<K, String>)proxyDetails).get("proxyPort");
        }
        final String proxyUsername = ((Hashtable<K, String>)proxyDetails).get("proxyUser");
        final String proxyPassword = ((Hashtable<K, String>)proxyDetails).get("proxyPass");
        if (!MDMStringUtils.isEmpty(proxyHost) && !MDMStringUtils.isEmpty(proxyPort)) {
            ThirdPartyCAUtil.LOGGER.log(Level.INFO, "ThirdPartyCAUtil: Proxy host - {0} and proxy port - {1}", new Object[] { proxyHost, proxyPort });
            final MDMProxy mdmProxy = new MDMProxy(proxyHost, Integer.parseInt(proxyPort));
            if (!MDMStringUtils.isEmpty(proxyUsername) && !MDMStringUtils.isEmpty(proxyPassword)) {
                ThirdPartyCAUtil.LOGGER.log(Level.INFO, "ThirdPartyCAUtil: Proxy username available - {0}", new Object[] { proxyUsername });
                mdmProxy.setProxyUsername(proxyUsername);
                mdmProxy.setProxyPassword(proxyPassword);
            }
            else {
                ThirdPartyCAUtil.LOGGER.log(Level.INFO, "ThirdPartyCAUtil: Proxy username not present. So setting empty username and password.");
                mdmProxy.setProxyUsername("");
                mdmProxy.setProxyPassword("");
            }
            return mdmProxy;
        }
        return null;
    }
    
    public static SSLContext createCustomSslContext(final Certificate[] caCerts, final Certificate[] clientCerts, final PrivateKey privateKey) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, CertificateException, UnrecoverableKeyException {
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore trustStore = null;
        if (caCerts != null) {
            trustStore = CertificateUtil.createTrustStore(caCerts);
        }
        trustManagerFactory.init(trustStore);
        final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        KeyStore keyStore = null;
        if (clientCerts != null && privateKey != null) {
            keyStore = CertificateUtil.createPkcs12KeyStore("ssl", clientCerts, privateKey, "changeit".toCharArray());
        }
        keyManagerFactory.init(keyStore, "changeit".toCharArray());
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        return sslContext;
    }
    
    public static String replaceScepChallengePasswords(String payloadContent, final Long resourceId) {
        final Pattern challengePasswordPattern = Pattern.compile("%challenge_password%(\\d+)");
        for (Matcher challengePasswordMatcher = challengePasswordPattern.matcher(payloadContent); challengePasswordMatcher.find(); challengePasswordMatcher = challengePasswordPattern.matcher(payloadContent)) {
            final String scepId = challengePasswordMatcher.group(1);
            final String challengePassword = ThirdPartyCaDbHandler.getChallengePasscodeForResourceID(Long.valueOf(scepId), resourceId);
            if (challengePassword != null) {
                payloadContent = challengePasswordMatcher.replaceFirst(challengePassword);
            }
        }
        return payloadContent;
    }
    
    static {
        LOGGER = Logger.getLogger("MdmCertificateIntegLogger");
    }
}
