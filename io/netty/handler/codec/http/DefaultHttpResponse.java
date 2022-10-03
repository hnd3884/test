package io.netty.handler.codec.http;

import io.netty.util.internal.ObjectUtil;

public class DefaultHttpResponse extends DefaultHttpMessage implements HttpResponse
{
    private HttpResponseStatus status;
    
    public DefaultHttpResponse(final HttpVersion version, final HttpResponseStatus status) {
        this(version, status, true, false);
    }
    
    public DefaultHttpResponse(final HttpVersion version, final HttpResponseStatus status, final boolean validateHeaders) {
        this(version, status, validateHeaders, false);
    }
    
    public DefaultHttpResponse(final HttpVersion version, final HttpResponseStatus status, final boolean validateHeaders, final boolean singleFieldHeaders) {
        super(version, validateHeaders, singleFieldHeaders);
        this.status = ObjectUtil.checkNotNull(status, "status");
    }
    
    public DefaultHttpResponse(final HttpVersion version, final HttpResponseStatus status, final HttpHeaders headers) {
        super(version, headers);
        this.status = ObjectUtil.checkNotNull(status, "status");
    }
    
    @Deprecated
    @Override
    public HttpResponseStatus getStatus() {
        return this.status();
    }
    
    @Override
    public HttpResponseStatus status() {
        return this.status;
    }
    
    @Override
    public HttpResponse setStatus(final HttpResponseStatus status) {
        this.status = ObjectUtil.checkNotNull(status, "status");
        return this;
    }
    
    @Override
    public HttpResponse setProtocolVersion(final HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }
    
    @Override
    public String toString() {
        return HttpMessageUtil.appendResponse(new StringBuilder(256), this).toString();
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.status.hashCode();
        result = 31 * result + super.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DefaultHttpResponse)) {
            return false;
        }
        final DefaultHttpResponse other = (DefaultHttpResponse)o;
        return this.status.equals(other.status()) && super.equals(o);
    }
}
