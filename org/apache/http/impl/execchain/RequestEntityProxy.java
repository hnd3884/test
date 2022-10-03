package org.apache.http.impl.execchain;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpEntity;

class RequestEntityProxy implements HttpEntity
{
    private final HttpEntity original;
    private boolean consumed;
    
    static void enhance(final HttpEntityEnclosingRequest request) {
        final HttpEntity entity = request.getEntity();
        if (entity != null && !entity.isRepeatable() && !isEnhanced(entity)) {
            request.setEntity((HttpEntity)new RequestEntityProxy(entity));
        }
    }
    
    static boolean isEnhanced(final HttpEntity entity) {
        return entity instanceof RequestEntityProxy;
    }
    
    static boolean isRepeatable(final HttpRequest request) {
        if (request instanceof HttpEntityEnclosingRequest) {
            final HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
            if (entity != null) {
                if (isEnhanced(entity)) {
                    final RequestEntityProxy proxy = (RequestEntityProxy)entity;
                    if (!proxy.isConsumed()) {
                        return true;
                    }
                }
                return entity.isRepeatable();
            }
        }
        return true;
    }
    
    RequestEntityProxy(final HttpEntity original) {
        this.consumed = false;
        this.original = original;
    }
    
    public HttpEntity getOriginal() {
        return this.original;
    }
    
    public boolean isConsumed() {
        return this.consumed;
    }
    
    public boolean isRepeatable() {
        return this.original.isRepeatable();
    }
    
    public boolean isChunked() {
        return this.original.isChunked();
    }
    
    public long getContentLength() {
        return this.original.getContentLength();
    }
    
    public Header getContentType() {
        return this.original.getContentType();
    }
    
    public Header getContentEncoding() {
        return this.original.getContentEncoding();
    }
    
    public InputStream getContent() throws IOException, IllegalStateException {
        return this.original.getContent();
    }
    
    public void writeTo(final OutputStream outStream) throws IOException {
        this.consumed = true;
        this.original.writeTo(outStream);
    }
    
    public boolean isStreaming() {
        return this.original.isStreaming();
    }
    
    public void consumeContent() throws IOException {
        this.consumed = true;
        this.original.consumeContent();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RequestEntityProxy{");
        sb.append(this.original);
        sb.append('}');
        return sb.toString();
    }
}
