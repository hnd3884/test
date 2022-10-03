package org.apache.http.impl.client;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.Header;
import org.apache.http.ProtocolException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;

@Deprecated
public class EntityEnclosingRequestWrapper extends RequestWrapper implements HttpEntityEnclosingRequest
{
    private HttpEntity entity;
    private boolean consumed;
    
    public EntityEnclosingRequestWrapper(final HttpEntityEnclosingRequest request) throws ProtocolException {
        super((HttpRequest)request);
        this.setEntity(request.getEntity());
    }
    
    public HttpEntity getEntity() {
        return this.entity;
    }
    
    public void setEntity(final HttpEntity entity) {
        this.entity = (HttpEntity)((entity != null) ? new EntityWrapper(entity) : null);
        this.consumed = false;
    }
    
    public boolean expectContinue() {
        final Header expect = this.getFirstHeader("Expect");
        return expect != null && "100-continue".equalsIgnoreCase(expect.getValue());
    }
    
    @Override
    public boolean isRepeatable() {
        return this.entity == null || this.entity.isRepeatable() || !this.consumed;
    }
    
    class EntityWrapper extends HttpEntityWrapper
    {
        EntityWrapper(final HttpEntity entity) {
            super(entity);
        }
        
        public void consumeContent() throws IOException {
            EntityEnclosingRequestWrapper.this.consumed = true;
            super.consumeContent();
        }
        
        public InputStream getContent() throws IOException {
            EntityEnclosingRequestWrapper.this.consumed = true;
            return super.getContent();
        }
        
        public void writeTo(final OutputStream outStream) throws IOException {
            EntityEnclosingRequestWrapper.this.consumed = true;
            super.writeTo(outStream);
        }
    }
}
