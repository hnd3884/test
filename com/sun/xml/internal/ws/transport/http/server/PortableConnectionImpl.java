package com.sun.xml.internal.ws.transport.http.server;

import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import java.security.Principal;
import com.sun.xml.internal.ws.api.message.Packet;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.oracle.webservices.internal.api.message.PropertySet;
import java.util.List;
import java.util.Map;
import com.sun.istack.internal.NotNull;
import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import javax.xml.ws.spi.http.HttpExchange;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.transport.http.WSHTTPConnection;

final class PortableConnectionImpl extends WSHTTPConnection implements WebServiceContextDelegate
{
    private final HttpExchange httpExchange;
    private int status;
    private final HttpAdapter adapter;
    private boolean outputWritten;
    private static final PropertyMap model;
    
    public PortableConnectionImpl(@NotNull final HttpAdapter adapter, @NotNull final HttpExchange httpExchange) {
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
        return this.httpExchange.getRequestHeader(headerName);
    }
    
    @Override
    public void setResponseHeaders(final Map<String, List<String>> headers) {
        final Map<String, List<String>> r = this.httpExchange.getResponseHeaders();
        r.clear();
        for (final Map.Entry<String, List<String>> entry : headers.entrySet()) {
            final String name = entry.getKey();
            final List<String> values = entry.getValue();
            if (!name.equalsIgnoreCase("Content-Length") && !name.equalsIgnoreCase("Content-Type")) {
                r.put(name, new ArrayList<String>(values));
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
        return this.httpExchange.getRequestHeaders().get(headerName);
    }
    
    @PropertySet.Property({ "javax.xml.ws.http.response.headers", "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers" })
    @Override
    public Map<String, List<String>> getResponseHeaders() {
        return this.httpExchange.getResponseHeaders();
    }
    
    @Override
    public void setContentTypeResponseHeader(@NotNull final String value) {
        this.httpExchange.addResponseHeader("Content-Type", value);
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
    public InputStream getInput() throws IOException {
        return this.httpExchange.getRequestBody();
    }
    
    @NotNull
    @Override
    public OutputStream getOutput() throws IOException {
        assert !this.outputWritten;
        this.outputWritten = true;
        this.httpExchange.setStatus(this.getStatus());
        return this.httpExchange.getResponseBody();
    }
    
    @NotNull
    @Override
    public WebServiceContextDelegate getWebServiceContextDelegate() {
        return this;
    }
    
    @Override
    public Principal getUserPrincipal(final Packet request) {
        return this.httpExchange.getUserPrincipal();
    }
    
    @Override
    public boolean isUserInRole(final Packet request, final String role) {
        return this.httpExchange.isUserInRole(role);
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
    
    @PropertySet.Property({ "javax.xml.ws.servlet.context" })
    public Object getServletContext() {
        return this.httpExchange.getAttribute("javax.xml.ws.servlet.context");
    }
    
    @PropertySet.Property({ "javax.xml.ws.servlet.response" })
    public Object getServletResponse() {
        return this.httpExchange.getAttribute("javax.xml.ws.servlet.response");
    }
    
    @PropertySet.Property({ "javax.xml.ws.servlet.request" })
    public Object getServletRequest() {
        return this.httpExchange.getAttribute("javax.xml.ws.servlet.request");
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
        return this.httpExchange.getScheme().equals("https");
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
        return this.httpExchange.getQueryString();
    }
    
    @PropertySet.Property({ "javax.xml.ws.http.request.pathinfo" })
    @Override
    public String getPathInfo() {
        return this.httpExchange.getPathInfo();
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.http.exchange" })
    public HttpExchange getExchange() {
        return this.httpExchange;
    }
    
    @NotNull
    @Override
    public String getBaseAddress() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.httpExchange.getScheme());
        sb.append("://");
        sb.append(this.httpExchange.getLocalAddress().getHostName());
        sb.append(":");
        sb.append(this.httpExchange.getLocalAddress().getPort());
        sb.append(this.httpExchange.getContextPath());
        return sb.toString();
    }
    
    @Override
    public String getProtocol() {
        return this.httpExchange.getProtocol();
    }
    
    @Override
    public void setContentLengthResponseHeader(final int value) {
        this.httpExchange.addResponseHeader("Content-Length", "" + value);
    }
    
    @Override
    public String getRequestURI() {
        return this.httpExchange.getRequestURI().toString();
    }
    
    @Override
    public String getRequestScheme() {
        return this.httpExchange.getScheme();
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
        return PortableConnectionImpl.model;
    }
    
    static {
        model = BasePropertySet.parse(PortableConnectionImpl.class);
    }
}
