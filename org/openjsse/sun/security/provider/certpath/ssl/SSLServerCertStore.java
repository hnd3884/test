package org.openjsse.sun.security.provider.certpath.ssl;

import java.util.Arrays;
import javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.security.cert.CertificateException;
import javax.net.ssl.X509ExtendedTrustManager;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.security.Provider;
import java.security.cert.CertStore;
import java.security.cert.X509CRL;
import java.security.cert.CRLSelector;
import java.util.Iterator;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.net.URLConnection;
import java.util.Collections;
import java.io.IOException;
import java.util.List;
import java.security.cert.CertStoreException;
import javax.net.ssl.HttpsURLConnection;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.security.cert.CertSelector;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertStoreParameters;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.net.URI;
import java.security.cert.CertStoreSpi;

public final class SSLServerCertStore extends CertStoreSpi
{
    private final URI uri;
    private static final GetChainTrustManager trustManager;
    private static final SSLSocketFactory socketFactory;
    private static final HostnameVerifier hostnameVerifier;
    
    SSLServerCertStore(final URI uri) throws InvalidAlgorithmParameterException {
        super(null);
        this.uri = uri;
    }
    
    @Override
    public Collection<X509Certificate> engineGetCertificates(final CertSelector selector) throws CertStoreException {
        try {
            final URLConnection urlConn = this.uri.toURL().openConnection();
            if (urlConn instanceof HttpsURLConnection) {
                if (SSLServerCertStore.socketFactory == null) {
                    throw new CertStoreException("No initialized SSLSocketFactory");
                }
                final HttpsURLConnection https = (HttpsURLConnection)urlConn;
                https.setSSLSocketFactory(SSLServerCertStore.socketFactory);
                https.setHostnameVerifier(SSLServerCertStore.hostnameVerifier);
                synchronized (SSLServerCertStore.trustManager) {
                    try {
                        https.connect();
                        return getMatchingCerts(SSLServerCertStore.trustManager.serverChain, selector);
                    }
                    catch (final IOException ioe) {
                        if (SSLServerCertStore.trustManager.exchangedServerCerts) {
                            return getMatchingCerts(SSLServerCertStore.trustManager.serverChain, selector);
                        }
                        throw ioe;
                    }
                    finally {
                        SSLServerCertStore.trustManager.cleanup();
                    }
                }
            }
        }
        catch (final IOException ioe2) {
            throw new CertStoreException(ioe2);
        }
        return (Collection<X509Certificate>)Collections.emptySet();
    }
    
    private static List<X509Certificate> getMatchingCerts(final List<X509Certificate> certs, final CertSelector selector) {
        if (selector == null) {
            return certs;
        }
        final List<X509Certificate> matchedCerts = new ArrayList<X509Certificate>(certs.size());
        for (final X509Certificate cert : certs) {
            if (selector.match(cert)) {
                matchedCerts.add(cert);
            }
        }
        return matchedCerts;
    }
    
    @Override
    public Collection<X509CRL> engineGetCRLs(final CRLSelector selector) throws CertStoreException {
        throw new UnsupportedOperationException();
    }
    
    public static CertStore getInstance(final URI uri) throws InvalidAlgorithmParameterException {
        return new CS(new SSLServerCertStore(uri), null, "SSLServer", null);
    }
    
    static {
        trustManager = new GetChainTrustManager();
        hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(final String hostname, final SSLSession session) {
                return true;
            }
        };
        SSLSocketFactory tempFactory;
        try {
            final SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[] { SSLServerCertStore.trustManager }, null);
            tempFactory = context.getSocketFactory();
        }
        catch (final GeneralSecurityException gse) {
            tempFactory = null;
        }
        socketFactory = tempFactory;
    }
    
    private static class GetChainTrustManager extends X509ExtendedTrustManager
    {
        private List<X509Certificate> serverChain;
        private boolean exchangedServerCerts;
        
        private GetChainTrustManager() {
            this.serverChain = Collections.emptyList();
            this.exchangedServerCerts = false;
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
        
        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType, final Socket socket) throws CertificateException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) throws CertificateException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
            this.exchangedServerCerts = true;
            this.serverChain = ((chain == null) ? Collections.emptyList() : Arrays.asList(chain));
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType, final Socket socket) throws CertificateException {
            this.checkServerTrusted(chain, authType);
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType, final SSLEngine engine) throws CertificateException {
            this.checkServerTrusted(chain, authType);
        }
        
        void cleanup() {
            this.exchangedServerCerts = false;
            this.serverChain = Collections.emptyList();
        }
    }
    
    private static class CS extends CertStore
    {
        protected CS(final CertStoreSpi spi, final Provider p, final String type, final CertStoreParameters params) {
            super(spi, p, type, params);
        }
    }
}
