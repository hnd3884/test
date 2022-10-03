package org.apache.http.impl.execchain;

import org.apache.http.params.HttpParams;
import org.apache.http.HeaderIterator;
import org.apache.http.Header;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;

class HttpResponseProxy implements CloseableHttpResponse
{
    private final HttpResponse original;
    private final ConnectionHolder connHolder;
    
    public HttpResponseProxy(final HttpResponse original, final ConnectionHolder connHolder) {
        ResponseEntityProxy.enchance(this.original = original, this.connHolder = connHolder);
    }
    
    public void close() throws IOException {
        if (this.connHolder != null) {
            this.connHolder.close();
        }
    }
    
    public StatusLine getStatusLine() {
        return this.original.getStatusLine();
    }
    
    public void setStatusLine(final StatusLine statusline) {
        this.original.setStatusLine(statusline);
    }
    
    public void setStatusLine(final ProtocolVersion ver, final int code) {
        this.original.setStatusLine(ver, code);
    }
    
    public void setStatusLine(final ProtocolVersion ver, final int code, final String reason) {
        this.original.setStatusLine(ver, code, reason);
    }
    
    public void setStatusCode(final int code) throws IllegalStateException {
        this.original.setStatusCode(code);
    }
    
    public void setReasonPhrase(final String reason) throws IllegalStateException {
        this.original.setReasonPhrase(reason);
    }
    
    public HttpEntity getEntity() {
        return this.original.getEntity();
    }
    
    public void setEntity(final HttpEntity entity) {
        this.original.setEntity(entity);
    }
    
    public Locale getLocale() {
        return this.original.getLocale();
    }
    
    public void setLocale(final Locale loc) {
        this.original.setLocale(loc);
    }
    
    public ProtocolVersion getProtocolVersion() {
        return this.original.getProtocolVersion();
    }
    
    public boolean containsHeader(final String name) {
        return this.original.containsHeader(name);
    }
    
    public Header[] getHeaders(final String name) {
        return this.original.getHeaders(name);
    }
    
    public Header getFirstHeader(final String name) {
        return this.original.getFirstHeader(name);
    }
    
    public Header getLastHeader(final String name) {
        return this.original.getLastHeader(name);
    }
    
    public Header[] getAllHeaders() {
        return this.original.getAllHeaders();
    }
    
    public void addHeader(final Header header) {
        this.original.addHeader(header);
    }
    
    public void addHeader(final String name, final String value) {
        this.original.addHeader(name, value);
    }
    
    public void setHeader(final Header header) {
        this.original.setHeader(header);
    }
    
    public void setHeader(final String name, final String value) {
        this.original.setHeader(name, value);
    }
    
    public void setHeaders(final Header[] headers) {
        this.original.setHeaders(headers);
    }
    
    public void removeHeader(final Header header) {
        this.original.removeHeader(header);
    }
    
    public void removeHeaders(final String name) {
        this.original.removeHeaders(name);
    }
    
    public HeaderIterator headerIterator() {
        return this.original.headerIterator();
    }
    
    public HeaderIterator headerIterator(final String name) {
        return this.original.headerIterator(name);
    }
    
    public HttpParams getParams() {
        return this.original.getParams();
    }
    
    public void setParams(final HttpParams params) {
        this.original.setParams(params);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpResponseProxy{");
        sb.append(this.original);
        sb.append('}');
        return sb.toString();
    }
}
