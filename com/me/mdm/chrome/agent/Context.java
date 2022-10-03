package com.me.mdm.chrome.agent;

import java.util.Hashtable;
import java.util.Arrays;
import com.google.api.client.json.jackson2.JacksonFactory;
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
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import java.util.Collection;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import org.json.JSONException;
import com.google.api.client.http.HttpRequestInitializer;
import org.json.JSONObject;
import java.util.List;
import com.google.api.services.directory.Directory;
import com.google.chromedevicemanagement.v1.ChromeDeviceManagement;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

public abstract class Context
{
    private String udid;
    private Long customerid;
    private String enterpriseId;
    private GoogleCredential credential;
    private static final String APPLICATION_NAME = "ManageEngine EMM/1.0";
    private static final JsonFactory JSON_FACTORY;
    private HttpTransport http_Transport;
    private ChromeDeviceManagement chromeDeviceMgmt;
    private Directory directory;
    private static final List<String> SCOPES;
    
    public Context(final String udid, final JSONObject esaDetails, final HttpTransport httpTransport) throws JSONException, Exception {
        try {
            this.udid = udid;
            this.http_Transport = httpTransport;
            this.customerid = esaDetails.getLong("CUSTOMER_ID");
            this.enterpriseId = String.valueOf(esaDetails.get("ENTERPRISE_ID"));
            this.credential = this.getGoogleCredential(esaDetails);
            this.chromeDeviceMgmt = new ChromeDeviceManagement.Builder(this.http_Transport, Context.JSON_FACTORY, (HttpRequestInitializer)this.credential).setApplicationName("ManageEngine EMM/1.0").build();
            this.directory = new Directory.Builder(this.http_Transport, Context.JSON_FACTORY, (HttpRequestInitializer)this.credential).setApplicationName("ManageEngine EMM/1.0").build();
        }
        catch (final Throwable e) {
            e.getCause();
        }
    }
    
    public Context(final String udid, final JSONObject esaDetails) throws JSONException, Exception {
        this(udid, esaDetails, getHttpTransportWithProxyConfigured());
    }
    
    private GoogleCredential getGoogleCredential(final JSONObject jsonObject) throws Exception {
        if (jsonObject.getInt("OAUTH_TYPE") == GoogleForWorkSettings.OAUTH_TYPE_ESA) {
            final String credentialFilePath = String.valueOf(jsonObject.get("ESA_CREDENTIAL_JSON_PATH"));
            final String esaEMailAddress = String.valueOf(jsonObject.get("ESA_EMAIL_ID"));
            final String serviceAccountUser = String.valueOf(jsonObject.get("DOMAIN_ADMIN_EMAIL_ID"));
            return new GoogleCredential.Builder().setJsonFactory(Context.JSON_FACTORY).setServiceAccountId(esaEMailAddress).setServiceAccountPrivateKey(GoogleForWorkSettings.getPrivateKeyFromCredentialJSONFile(credentialFilePath)).setTransport(this.http_Transport).setServiceAccountScopes((Collection)Context.SCOPES).setServiceAccountUser(serviceAccountUser).build();
        }
        final JSONObject bearerJSON = jsonObject.getJSONObject("GoogleBearerTokenDetails");
        final String refreshToken = String.valueOf(bearerJSON.get("REFRESH_TOKEN"));
        final String clientId = String.valueOf(bearerJSON.get("CLIENT_ID"));
        final String clientSecret = String.valueOf(bearerJSON.get("CLIENT_SECRET"));
        final GoogleRefreshTokenRequest request = new GoogleRefreshTokenRequest(this.http_Transport, Context.JSON_FACTORY, refreshToken, clientId, clientSecret);
        return new GoogleCredential().setAccessToken(request.execute().getAccessToken());
    }
    
    private static HttpTransport getHttpTransportWithProxyConfigured() throws Exception {
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
    
    public String getUdid() {
        return this.udid;
    }
    
    public String getEnterpriseId() {
        return this.enterpriseId;
    }
    
    public abstract String getCMPAEnterpriseAndUDID();
    
    public Long getCustomerId() {
        return this.customerid;
    }
    
    public Directory getDirectoryService() {
        return this.directory;
    }
    
    public ChromeDeviceManagement getCMPAService() {
        return this.chromeDeviceMgmt;
    }
    
    static {
        JSON_FACTORY = (JsonFactory)JacksonFactory.getDefaultInstance();
        SCOPES = Arrays.asList("https://www.googleapis.com/auth/admin.directory.user", "https://www.googleapis.com/auth/admin.directory.device.chromeos", "https://www.googleapis.com/auth/chromedevicemanagementapi");
    }
}
