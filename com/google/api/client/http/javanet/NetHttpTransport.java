package com.google.api.client.http.javanet;

import com.google.api.client.util.Beta;
import javax.net.ssl.SSLContext;
import com.google.api.client.util.SslUtils;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import com.google.api.client.util.SecurityUtils;
import java.io.InputStream;
import com.google.api.client.http.LowLevelHttpRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import com.google.api.client.util.Preconditions;
import java.util.Arrays;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import com.google.api.client.http.HttpTransport;

public final class NetHttpTransport extends HttpTransport
{
    private static final String[] SUPPORTED_METHODS;
    private static final String SHOULD_USE_PROXY_FLAG = "com.google.api.client.should_use_proxy";
    private final ConnectionFactory connectionFactory;
    private final SSLSocketFactory sslSocketFactory;
    private final HostnameVerifier hostnameVerifier;
    private final boolean isMtls;
    
    private static Proxy defaultProxy() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(System.getProperty("https.proxyHost"), Integer.parseInt(System.getProperty("https.proxyPort"))));
    }
    
    public NetHttpTransport() {
        this((ConnectionFactory)null, null, null, false);
    }
    
    NetHttpTransport(final Proxy proxy, final SSLSocketFactory sslSocketFactory, final HostnameVerifier hostnameVerifier, final boolean isMtls) {
        this(new DefaultConnectionFactory(proxy), sslSocketFactory, hostnameVerifier, isMtls);
    }
    
    NetHttpTransport(final ConnectionFactory connectionFactory, final SSLSocketFactory sslSocketFactory, final HostnameVerifier hostnameVerifier, final boolean isMtls) {
        this.connectionFactory = this.getConnectionFactory(connectionFactory);
        this.sslSocketFactory = sslSocketFactory;
        this.hostnameVerifier = hostnameVerifier;
        this.isMtls = isMtls;
    }
    
    private ConnectionFactory getConnectionFactory(final ConnectionFactory connectionFactory) {
        if (connectionFactory != null) {
            return connectionFactory;
        }
        if (System.getProperty("com.google.api.client.should_use_proxy") != null) {
            return new DefaultConnectionFactory(defaultProxy());
        }
        return new DefaultConnectionFactory();
    }
    
    @Override
    public boolean supportsMethod(final String method) {
        return Arrays.binarySearch(NetHttpTransport.SUPPORTED_METHODS, method) >= 0;
    }
    
    @Override
    public boolean isMtls() {
        return this.isMtls;
    }
    
    @Override
    protected NetHttpRequest buildRequest(final String method, final String url) throws IOException {
        Preconditions.checkArgument(this.supportsMethod(method), "HTTP method %s not supported", method);
        final URL connUrl = new URL(url);
        final HttpURLConnection connection = this.connectionFactory.openConnection(connUrl);
        connection.setRequestMethod(method);
        if (connection instanceof HttpsURLConnection) {
            final HttpsURLConnection secureConnection = (HttpsURLConnection)connection;
            if (this.hostnameVerifier != null) {
                secureConnection.setHostnameVerifier(this.hostnameVerifier);
            }
            if (this.sslSocketFactory != null) {
                secureConnection.setSSLSocketFactory(this.sslSocketFactory);
            }
        }
        return new NetHttpRequest(connection);
    }
    
    static {
        Arrays.sort(SUPPORTED_METHODS = new String[] { "DELETE", "GET", "HEAD", "OPTIONS", "POST", "PUT", "TRACE" });
    }
    
    public static final class Builder
    {
        private SSLSocketFactory sslSocketFactory;
        private HostnameVerifier hostnameVerifier;
        private Proxy proxy;
        private ConnectionFactory connectionFactory;
        private boolean isMtls;
        
        public Builder setProxy(final Proxy proxy) {
            this.proxy = proxy;
            return this;
        }
        
        public Builder setConnectionFactory(final ConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
            return this;
        }
        
        public Builder trustCertificatesFromJavaKeyStore(final InputStream keyStoreStream, final String storePass) throws GeneralSecurityException, IOException {
            final KeyStore trustStore = SecurityUtils.getJavaKeyStore();
            SecurityUtils.loadKeyStore(trustStore, keyStoreStream, storePass);
            return this.trustCertificates(trustStore);
        }
        
        public Builder trustCertificatesFromStream(final InputStream certificateStream) throws GeneralSecurityException, IOException {
            final KeyStore trustStore = SecurityUtils.getJavaKeyStore();
            trustStore.load(null, null);
            SecurityUtils.loadKeyStoreFromCertificates(trustStore, SecurityUtils.getX509CertificateFactory(), certificateStream);
            return this.trustCertificates(trustStore);
        }
        
        public Builder trustCertificates(final KeyStore trustStore) throws GeneralSecurityException {
            final SSLContext sslContext = SslUtils.getTlsSslContext();
            SslUtils.initSslContext(sslContext, trustStore, SslUtils.getPkixTrustManagerFactory());
            return this.setSslSocketFactory(sslContext.getSocketFactory());
        }
        
        @Beta
        public Builder trustCertificates(final KeyStore trustStore, final KeyStore mtlsKeyStore, final String mtlsKeyStorePassword) throws GeneralSecurityException {
            if (mtlsKeyStore != null && mtlsKeyStore.size() > 0) {
                this.isMtls = true;
            }
            final SSLContext sslContext = SslUtils.getTlsSslContext();
            SslUtils.initSslContext(sslContext, trustStore, SslUtils.getPkixTrustManagerFactory(), mtlsKeyStore, mtlsKeyStorePassword, SslUtils.getDefaultKeyManagerFactory());
            return this.setSslSocketFactory(sslContext.getSocketFactory());
        }
        
        @Beta
        public Builder doNotValidateCertificate() throws GeneralSecurityException {
            this.hostnameVerifier = SslUtils.trustAllHostnameVerifier();
            this.sslSocketFactory = SslUtils.trustAllSSLContext().getSocketFactory();
            return this;
        }
        
        public SSLSocketFactory getSslSocketFactory() {
            return this.sslSocketFactory;
        }
        
        public Builder setSslSocketFactory(final SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }
        
        public HostnameVerifier getHostnameVerifier() {
            return this.hostnameVerifier;
        }
        
        public Builder setHostnameVerifier(final HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }
        
        public NetHttpTransport build() {
            if (System.getProperty("com.google.api.client.should_use_proxy") != null) {
                this.setProxy(defaultProxy());
            }
            return (this.proxy == null) ? new NetHttpTransport(this.connectionFactory, this.sslSocketFactory, this.hostnameVerifier, this.isMtls) : new NetHttpTransport(this.proxy, this.sslSocketFactory, this.hostnameVerifier, this.isMtls);
        }
    }
}
