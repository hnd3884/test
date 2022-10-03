package com.google.api.client.http.apache;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpEntity;
import com.google.api.client.util.Preconditions;
import org.apache.http.HttpEntityEnclosingRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import java.io.IOException;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.HttpClient;
import com.google.api.client.http.LowLevelHttpRequest;

final class ApacheHttpRequest extends LowLevelHttpRequest
{
    private final HttpClient httpClient;
    private final HttpRequestBase request;
    
    ApacheHttpRequest(final HttpClient httpClient, final HttpRequestBase request) {
        this.httpClient = httpClient;
        this.request = request;
    }
    
    @Override
    public void addHeader(final String name, final String value) {
        this.request.addHeader(name, value);
    }
    
    @Override
    public void setTimeout(final int connectTimeout, final int readTimeout) throws IOException {
        final HttpParams params = this.request.getParams();
        ConnManagerParams.setTimeout(params, (long)connectTimeout);
        HttpConnectionParams.setConnectionTimeout(params, connectTimeout);
        HttpConnectionParams.setSoTimeout(params, readTimeout);
    }
    
    @Override
    public LowLevelHttpResponse execute() throws IOException {
        if (this.getStreamingContent() != null) {
            Preconditions.checkState(this.request instanceof HttpEntityEnclosingRequest, "Apache HTTP client does not support %s requests with content.", this.request.getRequestLine().getMethod());
            final ContentEntity entity = new ContentEntity(this.getContentLength(), this.getStreamingContent());
            entity.setContentEncoding(this.getContentEncoding());
            entity.setContentType(this.getContentType());
            if (this.getContentLength() == -1L) {
                entity.setChunked(true);
            }
            ((HttpEntityEnclosingRequest)this.request).setEntity((HttpEntity)entity);
        }
        return new ApacheHttpResponse(this.request, this.httpClient.execute((HttpUriRequest)this.request));
    }
}
