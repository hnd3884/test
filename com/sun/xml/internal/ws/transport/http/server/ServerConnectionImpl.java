package com.sun.xml.internal.ws.transport.http.server;

import com.sun.xml.internal.ws.util.ReadAllStream;
import java.io.FilterInputStream;
import java.net.URI;
import com.sun.net.httpserver.HttpsExchange;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import java.security.Principal;
import com.sun.xml.internal.ws.api.message.Packet;
import java.io.IOException;
import java.io.FilterOutputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.Iterator;
import com.sun.net.httpserver.Headers;
import java.util.Collection;
import java.util.ArrayList;
import com.oracle.webservices.internal.api.message.PropertySet;
import java.util.List;
import java.util.Map;
import com.sun.istack.internal.NotNull;
import com.oracle.webservices.internal.api.message.BasePropertySet;
import java.io.OutputStream;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.transport.http.WSHTTPConnection;

final class ServerConnectionImpl extends WSHTTPConnection implements WebServiceContextDelegate
{
    private final HttpExchange httpExchange;
    private int status;
    private final HttpAdapter adapter;
    private LWHSInputStream in;
    private OutputStream out;
    private static final PropertyMap model;
    
    public ServerConnectionImpl(@NotNull final HttpAdapter adapter, @NotNull final HttpExchange httpExchange) {
        this.adapter = adapter;
        this.httpExchange = httpExchange;
    }
    
    @PropertySet.Property({ "javax.xml.ws.http.request.headers", "com.sun.xml.internal.ws.api.message.packet.inbound.transport.headers" })
    @NotNull
    @Override
    public Map<String, List<String>> getRequestHeaders() {
        return this.httpExchange.getRequestHeaders();
    }
    
    @Override
    public String getRequestHeader(final String headerName) {
        return this.httpExchange.getRequestHeaders().getFirst(headerName);
    }
    
    @Override
    public void setResponseHeaders(final Map<String, List<String>> headers) {
        final Headers r = this.httpExchange.getResponseHeaders();
        r.clear();
        for (final Map.Entry<String, List<String>> entry : headers.entrySet()) {
            final String name = entry.getKey();
            final List<String> values = entry.getValue();
            if (!"Content-Length".equalsIgnoreCase(name) && !"Content-Type".equalsIgnoreCase(name)) {
                r.put(name, (List<String>)new ArrayList<String>(values));
            }
        }
    }
    
    @Override
    public void setResponseHeader(final String key, final List<String> value) {
        this.httpExchange.getResponseHeaders().put(key, value);
    }
    
    @Override
    public Set<String> getRequestHeaderNames() {
        return this.httpExchange.getRequestHeaders().keySet();
    }
    
    @Override
    public List<String> getRequestHeaderValues(final String headerName) {
        return this.httpExchange.getRequestHeaders().get((Object)headerName);
    }
    
    @PropertySet.Property({ "javax.xml.ws.http.response.headers", "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers" })
    @Override
    public Map<String, List<String>> getResponseHeaders() {
        return this.httpExchange.getResponseHeaders();
    }
    
    @Override
    public void setContentTypeResponseHeader(@NotNull final String value) {
        this.httpExchange.getResponseHeaders().set("Content-Type", value);
    }
    
    @Override
    public void setStatus(final int status) {
        this.status = status;
    }
    
    @PropertySet.Property({ "javax.xml.ws.http.response.code" })
    @Override
    public int getStatus() {
        return this.status;
    }
    
    @NotNull
    @Override
    public InputStream getInput() {
        if (this.in == null) {
            this.in = new LWHSInputStream(this.httpExchange.getRequestBody());
        }
        return this.in;
    }
    
    @NotNull
    @Override
    public OutputStream getOutput() throws IOException {
        if (this.out == null) {
            final String lenHeader = this.httpExchange.getResponseHeaders().getFirst("Content-Length");
            final int length = (lenHeader != null) ? Integer.parseInt(lenHeader) : 0;
            this.httpExchange.sendResponseHeaders(this.getStatus(), length);
            this.out = new FilterOutputStream(this.httpExchange.getResponseBody()) {
                boolean closed;
                
                @Override
                public void close() throws IOException {
                    if (!this.closed) {
                        this.closed = true;
                        ServerConnectionImpl.this.in.readAll();
                        try {
                            super.close();
                        }
                        catch (final IOException ex) {}
                    }
                }
                
                @Override
                public void write(final byte[] buf, final int start, final int len) throws IOException {
                    this.out.write(buf, start, len);
                }
            };
        }
        return this.out;
    }
    
    @NotNull
    @Override
    public WebServiceContextDelegate getWebServiceContextDelegate() {
        return this;
    }
    
    @Override
    public Principal getUserPrincipal(final Packet request) {
        return this.httpExchange.getPrincipal();
    }
    
    @Override
    public boolean isUserInRole(final Packet request, final String role) {
        return false;
    }
    
    @NotNull
    @Override
    public String getEPRAddress(final Packet request, final WSEndpoint endpoint) {
        final PortAddressResolver resolver = this.adapter.owner.createPortAddressResolver(this.getBaseAddress(), endpoint.getImplementationClass());
        final String address = resolver.getAddressFor(endpoint.getServiceName(), endpoint.getPortName().getLocalPart());
        if (address == null) {
            throw new WebServiceException(WsservletMessages.SERVLET_NO_ADDRESS_AVAILABLE(endpoint.getPortName()));
        }
        return address;
    }
    
    @Override
    public String getWSDLAddress(@NotNull final Packet request, @NotNull final WSEndpoint endpoint) {
        final String eprAddress = this.getEPRAddress(request, endpoint);
        if (this.adapter.getEndpoint().getPort() != null) {
            return eprAddress + "?wsdl";
        }
        return null;
    }
    
    @Override
    public boolean isSecure() {
        return this.httpExchange instanceof HttpsExchange;
    }
    
    @PropertySet.Property({ "javax.xml.ws.http.request.method" })
    @NotNull
    @Override
    public String getRequestMethod() {
        return this.httpExchange.getRequestMethod();
    }
    
    @PropertySet.Property({ "javax.xml.ws.http.request.querystring" })
    @Override
    public String getQueryString() {
        final URI requestUri = this.httpExchange.getRequestURI();
        final String query = requestUri.getQuery();
        if (query != null) {
            return query;
        }
        return null;
    }
    
    @PropertySet.Property({ "javax.xml.ws.http.request.pathinfo" })
    @Override
    public String getPathInfo() {
        final URI requestUri = this.httpExchange.getRequestURI();
        final String reqPath = requestUri.getPath();
        final String ctxtPath = this.httpExchange.getHttpContext().getPath();
        if (reqPath.length() > ctxtPath.length()) {
            return reqPath.substring(ctxtPath.length());
        }
        return null;
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.http.exchange" })
    public HttpExchange getExchange() {
        return this.httpExchange;
    }
    
    @NotNull
    @Override
    public String getBaseAddress() {
        final StringBuilder strBuf = new StringBuilder();
        strBuf.append((this.httpExchange instanceof HttpsExchange) ? "https" : "http");
        strBuf.append("://");
        final String hostHeader = this.httpExchange.getRequestHeaders().getFirst("Host");
        if (hostHeader != null) {
            strBuf.append(hostHeader);
        }
        else {
            strBuf.append(this.httpExchange.getLocalAddress().getHostName());
            strBuf.append(":");
            strBuf.append(this.httpExchange.getLocalAddress().getPort());
        }
        return strBuf.toString();
    }
    
    @Override
    public String getProtocol() {
        return this.httpExchange.getProtocol();
    }
    
    @Override
    public void setContentLengthResponseHeader(final int value) {
        this.httpExchange.getResponseHeaders().set("Content-Length", "" + value);
    }
    
    @Override
    public String getRequestURI() {
        return this.httpExchange.getRequestURI().toString();
    }
    
    @Override
    public String getRequestScheme() {
        return (this.httpExchange instanceof HttpsExchange) ? "https" : "http";
    }
    
    @Override
    public String getServerName() {
        return this.httpExchange.getLocalAddress().getHostName();
    }
    
    @Override
    public int getServerPort() {
        return this.httpExchange.getLocalAddress().getPort();
    }
    
    @Override
    protected PropertyMap getPropertyMap() {
        return ServerConnectionImpl.model;
    }
    
    static {
        model = BasePropertySet.parse(ServerConnectionImpl.class);
    }
    
    private static class LWHSInputStream extends FilterInputStream
    {
        boolean closed;
        boolean readAll;
        
        LWHSInputStream(final InputStream in) {
            super(in);
        }
        
        void readAll() throws IOException {
            if (!this.closed && !this.readAll) {
                final ReadAllStream all = new ReadAllStream();
                all.readAll(this.in, 4000000L);
                this.in.close();
                this.in = all;
                this.readAll = true;
            }
        }
        
        @Override
        public void close() throws IOException {
            if (!this.closed) {
                this.readAll();
                super.close();
                this.closed = true;
            }
        }
    }
}
