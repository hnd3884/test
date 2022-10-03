package com.google.api.client.http.apache.v2;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpEntity;
import com.google.api.client.util.Preconditions;
import org.apache.http.HttpEntityEnclosingRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import java.io.IOException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.HttpClient;
import com.google.api.client.http.LowLevelHttpRequest;

final class ApacheHttpRequest extends LowLevelHttpRequest
{
    private final HttpClient httpClient;
    private final HttpRequestBase request;
    private RequestConfig.Builder requestConfig;
    
    ApacheHttpRequest(final HttpClient httpClient, final HttpRequestBase request) {
        this.httpClient = httpClient;
        this.request = request;
        this.requestConfig = RequestConfig.custom().setRedirectsEnabled(false).setNormalizeUri(false).setStaleConnectionCheckEnabled(false);
    }
    
    public void addHeader(final String name, final String value) {
        this.request.addHeader(name, value);
    }
    
    public void setTimeout(final int connectTimeout, final int readTimeout) throws IOException {
        this.requestConfig.setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout);
    }
    
    public LowLevelHttpResponse execute() throws IOException {
        if (this.getStreamingContent() != null) {
            Preconditions.checkState(this.request instanceof HttpEntityEnclosingRequest, "Apache HTTP client does not support %s requests with content.", new Object[] { this.request.getRequestLine().getMethod() });
            final ContentEntity entity = new ContentEntity(this.getContentLength(), this.getStreamingContent());
            entity.setContentEncoding(this.getContentEncoding());
            entity.setContentType(this.getContentType());
            if (this.getContentLength() == -1L) {
                entity.setChunked(true);
            }
            ((HttpEntityEnclosingRequest)this.request).setEntity((HttpEntity)entity);
        }
        this.request.setConfig(this.requestConfig.build());
        return new ApacheHttpResponse(this.request, this.httpClient.execute((HttpUriRequest)this.request));
    }
}
