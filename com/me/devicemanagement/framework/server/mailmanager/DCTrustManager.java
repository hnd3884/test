package com.me.devicemanagement.framework.server.mailmanager;

import javax.net.ssl.TrustManager;
import java.util.Iterator;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.TrustManagerFactory;
import java.util.UUID;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

public class DCTrustManager implements X509TrustManager
{
    private X509TrustManager tm;
    public X509Certificate[] chain;
    private final String trustStorePath;
    private List tempCertList;
    
    public DCTrustManager(final String cerPath) throws Exception {
        this.tempCertList = new ArrayList();
        this.trustStorePath = cerPath;
        this.reloadTrustManager();
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        try {
            this.chain = chain;
            this.tm.checkServerTrusted(chain, authType);
        }
        catch (final CertificateException cx) {
            this.addServerCertAndReload(chain[0], false);
            this.tm.checkServerTrusted(chain, authType);
        }
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        this.tm.checkClientTrusted(chain, authType);
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        final X509Certificate[] issuers = this.tm.getAcceptedIssuers();
        return issuers;
    }
    
    public void addServerCertAndReload(final Certificate cert, final boolean permanent) {
        try {
            if (permanent) {
                final ProcessBuilder builder = new ProcessBuilder(new String[] { "keytool", "-importcert", "..." });
                builder.start();
            }
            else {
                this.tempCertList.add(cert);
            }
            this.reloadTrustManager();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void reloadTrustManager() throws Exception {
        final KeyStore ts = KeyStore.getInstance(KeyStore.getDefaultType());
        final InputStream in = new FileInputStream(this.trustStorePath);
        try {
            ts.load(in, null);
        }
        finally {
            in.close();
        }
        for (final Certificate cert : this.tempCertList) {
            ts.setCertificateEntry(UUID.randomUUID().toString(), cert);
        }
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ts);
        final TrustManager[] tms = tmf.getTrustManagers();
        for (int i = 0; i < tms.length; ++i) {
            if (tms[i] instanceof X509TrustManager) {
                this.tm = (X509TrustManager)tms[i];
                return;
            }
        }
        throw new NoSuchAlgorithmException("No X509TrustManager in TrustManagerFactory");
    }
}
