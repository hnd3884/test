package com.google.api.client.googleapis.apache;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import java.net.ProxySelector;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import com.google.api.client.util.SslUtils;
import com.google.api.client.googleapis.GoogleUtils;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import java.util.concurrent.TimeUnit;
import org.apache.http.config.SocketConfig;
import com.google.api.client.http.apache.ApacheHttpTransport;

@Deprecated
public final class GoogleApacheHttpTransport
{
    @Deprecated
    public static ApacheHttpTransport newTrustedTransport() throws GeneralSecurityException, IOException {
        final SocketConfig socketConfig = SocketConfig.custom().setRcvBufSize(8192).setSndBufSize(8192).build();
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(-1L, TimeUnit.MILLISECONDS);
        connectionManager.setValidateAfterInactivity(-1);
        final KeyStore trustStore = GoogleUtils.getCertificateTrustStore();
        final SSLContext sslContext = SslUtils.getTlsSslContext();
        SslUtils.initSslContext(sslContext, trustStore, SslUtils.getPkixTrustManagerFactory());
        final LayeredConnectionSocketFactory socketFactory = (LayeredConnectionSocketFactory)new SSLConnectionSocketFactory(sslContext);
        final HttpClient client = (HttpClient)HttpClientBuilder.create().useSystemProperties().setSSLSocketFactory(socketFactory).setDefaultSocketConfig(socketConfig).setMaxConnTotal(200).setMaxConnPerRoute(20).setRoutePlanner((HttpRoutePlanner)new SystemDefaultRoutePlanner(ProxySelector.getDefault())).setConnectionManager((HttpClientConnectionManager)connectionManager).disableRedirectHandling().disableAutomaticRetries().build();
        return new ApacheHttpTransport(client);
    }
    
    private GoogleApacheHttpTransport() {
    }
}
