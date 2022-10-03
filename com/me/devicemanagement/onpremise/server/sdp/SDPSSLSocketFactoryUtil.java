package com.me.devicemanagement.onpremise.server.sdp;

import java.io.File;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.logging.Level;
import javax.net.ssl.SSLSocketFactory;
import java.util.logging.Logger;

public class SDPSSLSocketFactoryUtil
{
    private static Logger logger;
    private static SSLSocketFactory sdpSSLSocketFactory;
    private static SDPSSLSocketFactoryUtil sdpSSLSocketFactoryUtil;
    
    public SDPSSLSocketFactoryUtil() {
        try {
            this.setSDPSSLSocketFactory();
        }
        catch (final Exception ex) {
            SDPSSLSocketFactoryUtil.logger.log(Level.WARNING, "Problem while setting SDP SSL Socket Factory", ex);
        }
    }
    
    public static SDPSSLSocketFactoryUtil getInstance() {
        if (SDPSSLSocketFactoryUtil.sdpSSLSocketFactoryUtil == null) {
            SDPSSLSocketFactoryUtil.sdpSSLSocketFactoryUtil = new SDPSSLSocketFactoryUtil();
        }
        return SDPSSLSocketFactoryUtil.sdpSSLSocketFactoryUtil;
    }
    
    public SSLSocketFactory getSDPSSLSocketFactory() {
        return SDPSSLSocketFactoryUtil.sdpSSLSocketFactory;
    }
    
    public SSLSocketFactory getSSLSocketFactory(final String keyStoreFilePath, final char[] keyStorePassPhrase) {
        SSLSocketFactory sdpSSLSocketFactory = null;
        try {
            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(new FileInputStream(keyStoreFilePath), keyStorePassPhrase);
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, keyStorePassPhrase);
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            final TrustManager[] tm = tmf.getTrustManagers();
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tm, null);
            sdpSSLSocketFactory = sslContext.getSocketFactory();
        }
        catch (final Exception ex) {
            SDPSSLSocketFactoryUtil.logger.log(Level.WARNING, "Exception while getting SSL socket factory{0}", ex);
        }
        return sdpSSLSocketFactory;
    }
    
    public void setSDPSSLSocketFactory() throws Exception {
        final File file = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + "SDPSSL" + File.separator + "https.truststore");
        if (file.exists()) {
            final KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(file), "storepw".toCharArray());
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, "storepw".toCharArray());
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            final TrustManager[] tm = tmf.getTrustManagers();
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tm, null);
            SDPSSLSocketFactoryUtil.sdpSSLSocketFactory = sslContext.getSocketFactory();
        }
    }
    
    public void resetSDPSSLSocketFactory() {
        SDPSSLSocketFactoryUtil.sdpSSLSocketFactory = null;
    }
    
    static {
        SDPSSLSocketFactoryUtil.logger = Logger.getLogger("SDPIntegrationLog");
        SDPSSLSocketFactoryUtil.sdpSSLSocketFactory = null;
        SDPSSLSocketFactoryUtil.sdpSSLSocketFactoryUtil = null;
    }
}
