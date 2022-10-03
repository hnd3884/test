package com.sun.xml.internal.ws.transport.http;

import java.security.Principal;
import com.sun.istack.internal.Nullable;
import java.util.Set;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import com.sun.istack.internal.NotNull;
import java.util.List;
import java.util.Map;
import com.oracle.webservices.internal.api.message.BasePropertySet;

public abstract class WSHTTPConnection extends BasePropertySet
{
    public static final int OK = 200;
    public static final int ONEWAY = 202;
    public static final int UNSUPPORTED_MEDIA = 415;
    public static final int MALFORMED_XML = 400;
    public static final int INTERNAL_ERR = 500;
    private volatile boolean closed;
    
    public abstract void setResponseHeaders(@NotNull final Map<String, List<String>> p0);
    
    public void setResponseHeader(final String key, final String value) {
        this.setResponseHeader(key, Collections.singletonList(value));
    }
    
    public abstract void setResponseHeader(final String p0, final List<String> p1);
    
    public abstract void setContentTypeResponseHeader(@NotNull final String p0);
    
    public abstract void setStatus(final int p0);
    
    public abstract int getStatus();
    
    @NotNull
    public abstract InputStream getInput() throws IOException;
    
    @NotNull
    public abstract OutputStream getOutput() throws IOException;
    
    @NotNull
    public abstract WebServiceContextDelegate getWebServiceContextDelegate();
    
    @NotNull
    public abstract String getRequestMethod();
    
    @NotNull
    @Deprecated
    public abstract Map<String, List<String>> getRequestHeaders();
    
    @NotNull
    @Deprecated
    public abstract Set<String> getRequestHeaderNames();
    
    public abstract Map<String, List<String>> getResponseHeaders();
    
    @Nullable
    public abstract String getRequestHeader(@NotNull final String p0);
    
    @Nullable
    public abstract List<String> getRequestHeaderValues(@NotNull final String p0);
    
    @Nullable
    public abstract String getQueryString();
    
    @Nullable
    public abstract String getPathInfo();
    
    @NotNull
    public abstract String getRequestURI();
    
    @NotNull
    public abstract String getRequestScheme();
    
    @NotNull
    public abstract String getServerName();
    
    public abstract int getServerPort();
    
    @NotNull
    public String getContextPath() {
        return "";
    }
    
    public Object getContext() {
        return null;
    }
    
    @NotNull
    public String getBaseAddress() {
        throw new UnsupportedOperationException();
    }
    
    public abstract boolean isSecure();
    
    public Principal getUserPrincipal() {
        return null;
    }
    
    public boolean isUserInRole(final String role) {
        return false;
    }
    
    public Object getRequestAttribute(final String key) {
        return null;
    }
    
    public void close() {
        this.closed = true;
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    public String getProtocol() {
        return "HTTP/1.1";
    }
    
    public String getCookie(final String name) {
        return null;
    }
    
    public void setCookie(final String name, final String value) {
    }
    
    public void setContentLengthResponseHeader(final int value) {
    }
}
