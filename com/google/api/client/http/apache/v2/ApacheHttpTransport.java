package com.google.api.client.http.apache.v2;

import com.google.api.client.http.LowLevelHttpRequest;
import java.io.IOException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import java.net.ProxySelector;
import java.util.concurrent.TimeUnit;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import com.google.api.client.util.Beta;
import org.apache.http.client.HttpClient;
import com.google.api.client.http.HttpTransport;

public final class ApacheHttpTransport extends HttpTransport
{
    private final HttpClient httpClient;
    private final boolean isMtls;
    
    public ApacheHttpTransport() {
        this(newDefaultHttpClient(), false);
    }
    
    public ApacheHttpTransport(final HttpClient httpClient) {
        this.httpClient = httpClient;
        this.isMtls = false;
    }
    
    @Beta
    public ApacheHttpTransport(final HttpClient httpClient, final boolean isMtls) {
        this.httpClient = httpClient;
        this.isMtls = isMtls;
    }
    
    public static HttpClient newDefaultHttpClient() {
        return (HttpClient)newDefaultHttpClientBuilder().build();
    }
    
    public static HttpClientBuilder newDefaultHttpClientBuilder() {
        return HttpClientBuilder.create().useSystemProperties().setSSLSocketFactory((LayeredConnectionSocketFactory)SSLConnectionSocketFactory.getSocketFactory()).setMaxConnTotal(200).setMaxConnPerRoute(20).setConnectionTimeToLive(-1L, TimeUnit.MILLISECONDS).setRoutePlanner((HttpRoutePlanner)new SystemDefaultRoutePlanner(ProxySelector.getDefault())).disableRedirectHandling().disableAutomaticRetries();
    }
    
    public boolean supportsMethod(final String method) {
        return true;
    }
    
    protected ApacheHttpRequest buildRequest(final String method, final String url) {
        HttpRequestBase requestBase;
        if (method.equals("DELETE")) {
            requestBase = (HttpRequestBase)new HttpDelete(url);
        }
        else if (method.equals("GET")) {
            requestBase = (HttpRequestBase)new HttpGet(url);
        }
        else if (method.equals("HEAD")) {
            requestBase = (HttpRequestBase)new HttpHead(url);
        }
        else if (method.equals("PATCH")) {
            requestBase = (HttpRequestBase)new HttpPatch(url);
        }
        else if (method.equals("POST")) {
            requestBase = (HttpRequestBase)new HttpPost(url);
        }
        else if (method.equals("PUT")) {
            requestBase = (HttpRequestBase)new HttpPut(url);
        }
        else if (method.equals("TRACE")) {
            requestBase = (HttpRequestBase)new HttpTrace(url);
        }
        else if (method.equals("OPTIONS")) {
            requestBase = (HttpRequestBase)new HttpOptions(url);
        }
        else {
            requestBase = (HttpRequestBase)new HttpExtensionMethod(method, url);
        }
        return new ApacheHttpRequest(this.httpClient, requestBase);
    }
    
    public void shutdown() throws IOException {
        if (this.httpClient instanceof CloseableHttpClient) {
            ((CloseableHttpClient)this.httpClient).close();
        }
    }
    
    public HttpClient getHttpClient() {
        return this.httpClient;
    }
    
    public boolean isMtls() {
        return this.isMtls;
    }
}
