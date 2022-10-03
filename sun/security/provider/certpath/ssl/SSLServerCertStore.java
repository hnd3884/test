package sun.security.provider.certpath.ssl;

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
    public Collection<X509Certificate> engineGetCertificates(final CertSelector certSelector) throws CertStoreException {
        try {
            final URLConnection openConnection = this.uri.toURL().openConnection();
            if (openConnection instanceof HttpsURLConnection) {
                if (SSLServerCertStore.socketFactory == null) {
                    throw new CertStoreException("No initialized SSLSocketFactory");
                }
                final HttpsURLConnection httpsURLConnection = (HttpsURLConnection)openConnection;
                httpsURLConnection.setSSLSocketFactory(SSLServerCertStore.socketFactory);
                httpsURLConnection.setHostnameVerifier(SSLServerCertStore.hostnameVerifier);
                synchronized (SSLServerCertStore.trustManager) {
                    try {
                        httpsURLConnection.connect();
                        return getMatchingCerts(SSLServerCertStore.trustManager.serverChain, certSelector);
                    }
                    catch (final IOException ex) {
                        if (SSLServerCertStore.trustManager.exchangedServerCerts) {
                            return getMatchingCerts(SSLServerCertStore.trustManager.serverChain, certSelector);
                        }
                        throw ex;
                    }
                    finally {
                        SSLServerCertStore.trustManager.cleanup();
                    }
                }
            }
        }
        catch (final IOException ex2) {
            throw new CertStoreException(ex2);
        }
        return (Collection<X509Certificate>)Collections.emptySet();
    }
    
    private static List<X509Certificate> getMatchingCerts(final List<X509Certificate> list, final CertSelector certSelector) {
        if (certSelector == null) {
            return list;
        }
        final ArrayList list2 = new ArrayList(list.size());
        for (final X509Certificate x509Certificate : list) {
            if (certSelector.match(x509Certificate)) {
                list2.add(x509Certificate);
            }
        }
        return list2;
    }
    
    @Override
    public Collection<X509CRL> engineGetCRLs(final CRLSelector crlSelector) throws CertStoreException {
        throw new UnsupportedOperationException();
    }
    
    static CertStore getInstance(final URI uri) throws InvalidAlgorithmParameterException {
        return new CS(new SSLServerCertStore(uri), null, "SSLServer", null);
    }
    
    static {
        trustManager = new GetChainTrustManager();
        hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(final String s, final SSLSession sslSession) {
                return true;
            }
        };
        SSLSocketFactory socketFactory2;
        try {
            final SSLContext instance = SSLContext.getInstance("SSL");
            instance.init(null, new TrustManager[] { SSLServerCertStore.trustManager }, null);
            socketFactory2 = instance.getSocketFactory();
        }
        catch (final GeneralSecurityException ex) {
            socketFactory2 = null;
        }
        socketFactory = socketFactory2;
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
        public void checkClientTrusted(final X509Certificate[] array, final String s) throws CertificateException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void checkClientTrusted(final X509Certificate[] array, final String s, final Socket socket) throws CertificateException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void checkClientTrusted(final X509Certificate[] array, final String s, final SSLEngine sslEngine) throws CertificateException {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] array, final String s) throws CertificateException {
            this.exchangedServerCerts = true;
            this.serverChain = ((array == null) ? Collections.emptyList() : Arrays.asList(array));
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] array, final String s, final Socket socket) throws CertificateException {
            this.checkServerTrusted(array, s);
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] array, final String s, final SSLEngine sslEngine) throws CertificateException {
            this.checkServerTrusted(array, s);
        }
        
        void cleanup() {
            this.exchangedServerCerts = false;
            this.serverChain = Collections.emptyList();
        }
    }
    
    private static class CS extends CertStore
    {
        protected CS(final CertStoreSpi certStoreSpi, final Provider provider, final String s, final CertStoreParameters certStoreParameters) {
            super(certStoreSpi, provider, s, certStoreParameters);
        }
    }
}
