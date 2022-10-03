package com.google.api.client.googleapis.apache.v2;

import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import com.google.api.client.util.SslUtils;
import com.google.api.client.googleapis.GoogleUtils;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.api.client.util.Beta;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import java.net.ProxySelector;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.config.Registry;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import java.util.concurrent.TimeUnit;
import com.google.api.client.googleapis.mtls.MtlsProvider;
import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.client.googleapis.mtls.MtlsUtils;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;

public final class GoogleApacheHttpTransport
{
    public static ApacheHttpTransport newTrustedTransport() throws GeneralSecurityException, IOException {
        return newTrustedTransport(MtlsUtils.getDefaultMtlsProvider());
    }
    
    @Beta
    public static ApacheHttpTransport newTrustedTransport(final MtlsProvider mtlsProvider) throws GeneralSecurityException, IOException {
        final SocketFactoryRegistryHandler handler = new SocketFactoryRegistryHandler(mtlsProvider);
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager((Registry)handler.getSocketFactoryRegistry(), (HttpConnectionFactory)null, (SchemePortResolver)null, (DnsResolver)null, -1L, TimeUnit.MILLISECONDS);
        connectionManager.setValidateAfterInactivity(-1);
        final HttpClient client = (HttpClient)HttpClientBuilder.create().useSystemProperties().setMaxConnTotal(200).setMaxConnPerRoute(20).setRoutePlanner((HttpRoutePlanner)new SystemDefaultRoutePlanner(ProxySelector.getDefault())).setConnectionManager((HttpClientConnectionManager)connectionManager).disableRedirectHandling().disableAutomaticRetries().build();
        return new ApacheHttpTransport(client, handler.isMtls());
    }
    
    private GoogleApacheHttpTransport() {
    }
    
    @VisibleForTesting
    static class SocketFactoryRegistryHandler
    {
        private final Registry<ConnectionSocketFactory> socketFactoryRegistry;
        private final boolean isMtls;
        
        public SocketFactoryRegistryHandler(final MtlsProvider mtlsProvider) throws GeneralSecurityException, IOException {
            KeyStore mtlsKeyStore = null;
            String mtlsKeyStorePassword = null;
            if (mtlsProvider.useMtlsClientCertificate()) {
                mtlsKeyStore = mtlsProvider.getKeyStore();
                mtlsKeyStorePassword = mtlsProvider.getKeyStorePassword();
            }
            final KeyStore trustStore = GoogleUtils.getCertificateTrustStore();
            final SSLContext sslContext = SslUtils.getTlsSslContext();
            if (mtlsKeyStore != null && mtlsKeyStorePassword != null) {
                this.isMtls = true;
                SslUtils.initSslContext(sslContext, trustStore, SslUtils.getPkixTrustManagerFactory(), mtlsKeyStore, mtlsKeyStorePassword, SslUtils.getDefaultKeyManagerFactory());
            }
            else {
                this.isMtls = false;
                SslUtils.initSslContext(sslContext, trustStore, SslUtils.getPkixTrustManagerFactory());
            }
            final LayeredConnectionSocketFactory socketFactory = (LayeredConnectionSocketFactory)new SSLConnectionSocketFactory(sslContext);
            this.socketFactoryRegistry = (Registry<ConnectionSocketFactory>)RegistryBuilder.create().register("http", (Object)PlainConnectionSocketFactory.getSocketFactory()).register("https", (Object)socketFactory).build();
        }
        
        public Registry<ConnectionSocketFactory> getSocketFactoryRegistry() {
            return this.socketFactoryRegistry;
        }
        
        public boolean isMtls() {
            return this.isMtls;
        }
    }
}
