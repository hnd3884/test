package com.me.mdm.onpremise.server.integration;

import javax.net.ssl.KeyManagerFactory;
import java.io.OutputStream;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLSocketFactory;
import java.io.FileOutputStream;
import java.security.cert.Certificate;
import javax.net.ssl.SSLSocket;
import java.util.logging.Level;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.io.File;
import java.util.logging.Logger;

public class MDMSSLHandler
{
    private static final Logger SDPINTEGLOGGER;
    private static MDMSSLHandler sslHandler;
    
    public static MDMSSLHandler getInstance() {
        return MDMSSLHandler.sslHandler = new MDMSSLHandler();
    }
    
    public void handleKeystoreFile(final String host, final int port, final String keyStoreFilePath, final char[] keyStorePassPhrase, final String certificateAlias) throws Exception {
        try {
            final File file = new File(keyStoreFilePath);
            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            if (file.exists() && file.length() != 0L) {
                final InputStream in = new FileInputStream(file);
                keyStore.load(in, keyStorePassPhrase);
                in.close();
            }
            else {
                file.createNewFile();
                keyStore.load(null, keyStorePassPhrase);
            }
            final SSLContext context = SSLContext.getInstance("TLS");
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            final X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
            final MDMTrustManager tm = new MDMTrustManager(defaultTrustManager);
            context.init(null, new TrustManager[] { tm }, null);
            final SSLSocketFactory factory = context.getSocketFactory();
            MDMSSLHandler.SDPINTEGLOGGER.log(Level.INFO, "Opening connection to {0}:{1}...", new Object[] { host, port });
            final SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
            socket.setSoTimeout(10000);
            try {
                MDMSSLHandler.SDPINTEGLOGGER.log(Level.INFO, "Starting SSL handshake...");
                socket.startHandshake();
                socket.close();
                MDMSSLHandler.SDPINTEGLOGGER.log(Level.INFO, "No errors, certificate is already trusted");
            }
            catch (final Exception ex) {
                MDMSSLHandler.SDPINTEGLOGGER.log(Level.WARNING, "Exception in Handshake {0}", ex);
            }
            final X509Certificate[] chain = tm.getCertificateChain();
            final X509Certificate cert = chain[0];
            keyStore.setCertificateEntry(certificateAlias, cert);
            final OutputStream out = new FileOutputStream(keyStoreFilePath);
            keyStore.store(out, keyStorePassPhrase);
            out.close();
            MDMSSLHandler.SDPINTEGLOGGER.log(Level.INFO, "Added certificate to keystore using alias {0}", certificateAlias);
        }
        catch (final Exception ex2) {
            MDMSSLHandler.SDPINTEGLOGGER.log(Level.WARNING, "Exception while creating keystore file {0}", ex2);
        }
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
            MDMSSLHandler.SDPINTEGLOGGER.log(Level.WARNING, "Exception while getting SSL socket factory{0}", ex);
        }
        return sdpSSLSocketFactory;
    }
    
    static {
        SDPINTEGLOGGER = Logger.getLogger("MDMSDPIntegrationLog");
        MDMSSLHandler.sslHandler = null;
    }
}
