package com.me.devicemanagement.framework.server.httpclient;

import java.util.Hashtable;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.net.PasswordAuthentication;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import java.io.IOException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import java.io.InputStream;
import org.json.JSONObject;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.HttpClient;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class DMHttpClient
{
    static final String CONNECT_TIMEOUT = "connectTimeout";
    static final String READ_TIMEOUT = "readTimeout";
    static final String FOLLOW_REDIRECTS = "followRedirects";
    private boolean useProxyIfConfigured;
    
    public DMHttpClient() {
        this.useProxyIfConfigured = true;
    }
    
    String getproxy(final String connectionURL) throws Exception {
        final Properties proxyConf = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
        String proxyHost = null;
        String proxyPort = null;
        String userName = null;
        String password = null;
        String proxyScript = null;
        if (proxyConf != null) {
            if (proxyConf.containsKey("proxyScriptEna") && ((Hashtable<K, Object>)proxyConf).get("proxyScriptEna").toString().equals("1")) {
                proxyScript = ((Hashtable<K, String>)proxyConf).get("proxyScript");
            }
            else {
                proxyHost = ((Hashtable<K, String>)proxyConf).get("proxyHost");
                proxyPort = ((Hashtable<K, String>)proxyConf).get("proxyPort");
            }
            userName = ((Hashtable<K, String>)proxyConf).get("proxyUser");
            password = ((Hashtable<K, String>)proxyConf).get("proxyPass");
            if (proxyScript != null) {
                final PacProxySelector pacProxySelector = new PacProxySelector((PacScriptSource)new UrlPacScriptSource(proxyScript));
                final List<Proxy> proxyList = pacProxySelector.select(new URI(connectionURL));
                if (proxyList != null && !proxyList.isEmpty()) {
                    for (final Proxy proxy : proxyList) {
                        final SocketAddress address = proxy.address();
                        if (address != null) {
                            proxyHost = ((InetSocketAddress)address).getHostName();
                            proxyPort = Integer.toString(((InetSocketAddress)address).getPort());
                        }
                    }
                }
            }
        }
        String proxy2 = "--,--";
        if (proxyHost != null && !proxyHost.equalsIgnoreCase("") && proxyPort != null && !proxyPort.equalsIgnoreCase("") && this.useProxyIfConfigured) {
            proxy2 = proxyHost + "," + proxyPort;
        }
        if (userName != null && !userName.equalsIgnoreCase("") && password != null && !password.equalsIgnoreCase("")) {
            proxy2 = proxy2 + "," + userName + "," + password;
        }
        else {
            proxy2 += ",--,--";
        }
        return proxy2;
    }
    
    HttpClient getClientWithoutProxy() {
        final HttpClient client = new HttpClient();
        client.setHttpConnectionManager((HttpConnectionManager)new MultiThreadedHttpConnectionManager());
        return client;
    }
    
    HttpClient getClient(final String connectionURL) throws Exception {
        final HttpClient client = new HttpClient();
        client.setHttpConnectionManager((HttpConnectionManager)new MultiThreadedHttpConnectionManager());
        final String[] proxyconfig = this.getproxy(connectionURL).split(",");
        if (!proxyconfig[0].equalsIgnoreCase("--") && !proxyconfig[1].equalsIgnoreCase("--") && this.useProxyIfConfigured) {
            client.getHostConfiguration().setProxy(proxyconfig[0], Integer.parseInt(proxyconfig[1]));
        }
        if (!proxyconfig[2].equalsIgnoreCase("--") && !proxyconfig[3].equalsIgnoreCase("--")) {
            final Credentials credentials = (Credentials)new UsernamePasswordCredentials(proxyconfig[2], proxyconfig[3]);
            client.getState().setProxyCredentials(AuthScope.ANY, credentials);
            client.getParams().setAuthenticationPreemptive(true);
        }
        return client;
    }
    
    public DMHttpResponse execute(final DMHttpRequest request) throws IOException, JSONException, Exception {
        final String method = request.method;
        final String url = request.url;
        final InputStream body = request.getBody();
        final boolean isDelete = "DELETE".equalsIgnoreCase(method);
        final boolean isPost = "POST".equalsIgnoreCase(method);
        final boolean isPut = "PUT".equalsIgnoreCase(method);
        final byte[] excerpt = null;
        HttpMethod httpMethod;
        if (isPost || isPut) {
            final EntityEnclosingMethod entityEnclosingMethod = (EntityEnclosingMethod)(isPost ? new PostMethod(url) : new PutMethod(url));
            if (body != null) {
                final Integer length = (Integer)request.headers.remove("Content-Length");
                entityEnclosingMethod.setRequestEntity((RequestEntity)((length == null) ? new InputStreamRequestEntity(body) : new InputStreamRequestEntity(body, Long.parseLong(length.toString()))));
            }
            httpMethod = (HttpMethod)entityEnclosingMethod;
        }
        else if (isDelete) {
            httpMethod = (HttpMethod)new DeleteMethod(url);
            if (body != null) {
                final DMDeleteMethod dmDeleteMethod = new DMDeleteMethod(url);
                final Integer length = (Integer)request.headers.remove("Content-Length");
                dmDeleteMethod.setRequestEntity((RequestEntity)((length == null) ? new InputStreamRequestEntity(body) : new InputStreamRequestEntity(body, Long.parseLong(length.toString()))));
                httpMethod = (HttpMethod)dmDeleteMethod;
            }
        }
        else {
            httpMethod = (HttpMethod)new GetMethod(url);
        }
        if (request.headers != null) {
            final Iterator iterator = request.headers.keys();
            while (iterator.hasNext()) {
                final String headerName = iterator.next();
                final String headerValue = request.headers.optString(headerName);
                httpMethod.addRequestHeader(headerName, headerValue);
            }
        }
        if (request.parameters != null) {
            final Iterator iterator = request.parameters.keys();
            final NameValuePair[] nvpairs = new NameValuePair[request.parameters.length()];
            int i = 0;
            while (iterator.hasNext()) {
                final String paramName = iterator.next();
                final String paramValue = request.parameters.optString(paramName);
                final NameValuePair nvpair = new NameValuePair(paramName, paramValue);
                nvpairs[i] = nvpair;
                ++i;
            }
            httpMethod.setQueryString(nvpairs);
        }
        if (request.useProxy) {
            this.getClient(httpMethod.getURI().getURI()).executeMethod(httpMethod);
        }
        else {
            this.getClientWithoutProxy().executeMethod(httpMethod);
        }
        final DMHttpResponse dmresponse = new DMHttpResponse();
        dmresponse.responseBodyAsString = httpMethod.getResponseBodyAsString();
        dmresponse.responseBodyAsStream = httpMethod.getResponseBodyAsStream();
        dmresponse.status = httpMethod.getStatusCode();
        final Header[] responseHeaders = httpMethod.getResponseHeaders();
        final JSONObject responseHeadersJSON = new JSONObject();
        for (int j = 0; j < responseHeaders.length; ++j) {
            responseHeadersJSON.put(responseHeaders[j].getName(), (Object)responseHeaders[j].getValue());
        }
        dmresponse.responseHeaders = responseHeadersJSON;
        return dmresponse;
    }
    
    public void setUseProxyIfConfigured(final boolean useProxyIfConfigured) {
        this.useProxyIfConfigured = useProxyIfConfigured;
    }
    
    public DMHttpResponse executeDeleteMethod(final DMHttpRequest request) throws Exception {
        final DMHttpResponse dmresponse = new DMHttpResponse();
        final String url = request.url;
        final URL Url = new URL(url);
        HttpURLConnection httpCon = null;
        final String[] proxyconfig = this.getproxy(url).split(",");
        Proxy proxy = null;
        if (!proxyconfig[0].equalsIgnoreCase("--") && !proxyconfig[1].equalsIgnoreCase("--")) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyconfig[0], Integer.parseInt(proxyconfig[1])));
            httpCon = (HttpURLConnection)Url.openConnection(proxy);
        }
        else {
            httpCon = (HttpURLConnection)Url.openConnection();
        }
        if (!proxyconfig[2].equalsIgnoreCase("--") && !proxyconfig[3].equalsIgnoreCase("--")) {
            final Authenticator authenticator = new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxyconfig[2], proxyconfig[3].toCharArray());
                }
            };
            Authenticator.setDefault(authenticator);
        }
        httpCon.setRequestMethod("DELETE");
        httpCon.setDoOutput(true);
        httpCon.setDoInput(true);
        if (request.headers != null) {
            final Iterator iterator = request.headers.keys();
            while (iterator.hasNext()) {
                final String headerName = iterator.next();
                final String headerValue = request.headers.optString(headerName);
                httpCon.setRequestProperty(headerName, headerValue);
            }
        }
        httpCon.setUseCaches(false);
        httpCon.setInstanceFollowRedirects(false);
        try (final DataOutputStream outstream = new DataOutputStream(httpCon.getOutputStream())) {
            outstream.write(request.data);
        }
        httpCon.connect();
        final BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
        String temp = null;
        final StringBuilder sb = new StringBuilder();
        while ((temp = in.readLine()) != null) {
            sb.append(temp).append(" ");
        }
        final String result = sb.toString();
        in.close();
        httpCon.disconnect();
        dmresponse.responseBodyAsString = result;
        return dmresponse;
    }
}
