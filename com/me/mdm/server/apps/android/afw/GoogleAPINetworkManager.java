package com.me.mdm.server.apps.android.afw;

import java.util.Hashtable;
import java.util.Properties;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.client.HttpClient;
import com.google.api.client.http.apache.ApacheHttpTransport;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.AuthScope;
import org.apache.http.HttpHost;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.BasicHttpParams;
import com.google.api.client.http.HttpTransport;

public class GoogleAPINetworkManager
{
    private static GoogleAPINetworkManager manager;
    
    private GoogleAPINetworkManager() {
    }
    
    public static GoogleAPINetworkManager getGoogleAPINetworkManager() {
        return GoogleAPINetworkManager.manager;
    }
    
    public HttpTransport getHttpTransportWithProxyConfigured() throws Exception {
        final HttpParams params = (HttpParams)new BasicHttpParams();
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        ConnManagerParams.setMaxTotalConnections(params, 200);
        ConnManagerParams.setMaxConnectionsPerRoute(params, (ConnPerRoute)new ConnPerRouteBean(20));
        final SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", (SocketFactory)PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", (SocketFactory)SSLSocketFactory.getSocketFactory(), 443));
        final ClientConnectionManager connectionManager = (ClientConnectionManager)new ThreadSafeClientConnManager(params, registry);
        final DefaultHttpClient httpclient = new DefaultHttpClient(connectionManager, params);
        httpclient.setHttpRequestRetryHandler((HttpRequestRetryHandler)new DefaultHttpRequestRetryHandler(0, false));
        String proxyHost = null;
        String proxyUsername = null;
        String proxyPassword = null;
        int proxyPort = -1;
        final Properties proxyDetails = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        if (proxyDetails != null) {
            final int proxyType = DownloadManager.proxyType;
            if (proxyType == 4) {
                final String dummyUrl = "https://www.googleapis.com/admin/directory/v1";
                final Properties pacProps = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration(dummyUrl, proxyDetails);
                proxyHost = ((Hashtable<K, String>)pacProps).get("proxyHost");
                proxyPort = Integer.valueOf(((Hashtable<K, String>)pacProps).get("proxyPort"));
            }
            else if (proxyType == 2) {
                proxyHost = ((Hashtable<K, String>)proxyDetails).get("proxyHost");
                proxyPort = Integer.valueOf(((Hashtable<K, String>)proxyDetails).get("proxyPort"));
            }
            proxyUsername = ((Hashtable<K, String>)proxyDetails).get("proxyUser");
            proxyPassword = ((Hashtable<K, String>)proxyDetails).get("proxyPass");
            if (proxyHost != null) {
                final HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");
                if (proxyUsername != null) {
                    httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, (Credentials)new UsernamePasswordCredentials(proxyUsername, proxyPassword));
                }
                httpclient.getParams().setParameter("http.route.default-proxy", (Object)proxy);
            }
        }
        final ApacheHttpTransport transport = new ApacheHttpTransport((HttpClient)httpclient);
        return (HttpTransport)transport;
    }
    
    static {
        GoogleAPINetworkManager.manager = new GoogleAPINetworkManager();
    }
}
