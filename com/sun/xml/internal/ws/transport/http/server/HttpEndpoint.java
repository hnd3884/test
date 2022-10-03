package com.sun.xml.internal.ws.transport.http.server;

import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;
import com.sun.net.httpserver.HttpHandler;
import java.net.MalformedURLException;
import java.net.URL;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.resources.ServerMessages;
import java.util.concurrent.Executor;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import com.sun.net.httpserver.HttpContext;

public final class HttpEndpoint extends com.sun.xml.internal.ws.api.server.HttpEndpoint
{
    private String address;
    private HttpContext httpContext;
    private final HttpAdapter adapter;
    private final Executor executor;
    
    public HttpEndpoint(final Executor executor, final HttpAdapter adapter) {
        this.executor = executor;
        this.adapter = adapter;
    }
    
    @Override
    public void publish(final String address) {
        this.address = address;
        this.publish(this.httpContext = ServerMgr.getInstance().createContext(address));
    }
    
    public void publish(final Object serverContext) {
        if (serverContext instanceof javax.xml.ws.spi.http.HttpContext) {
            this.setHandler((javax.xml.ws.spi.http.HttpContext)serverContext);
            return;
        }
        if (serverContext instanceof HttpContext) {
            this.setHandler(this.httpContext = (HttpContext)serverContext);
            return;
        }
        throw new ServerRtException(ServerMessages.NOT_KNOW_HTTP_CONTEXT_TYPE(serverContext.getClass(), HttpContext.class, javax.xml.ws.spi.http.HttpContext.class), new Object[0]);
    }
    
    HttpAdapterList getAdapterOwner() {
        return this.adapter.owner;
    }
    
    private String getEPRAddress() {
        if (this.address == null) {
            return this.httpContext.getServer().getAddress().toString();
        }
        try {
            final URL u = new URL(this.address);
            if (u.getPort() == 0) {
                return new URL(u.getProtocol(), u.getHost(), this.httpContext.getServer().getAddress().getPort(), u.getFile()).toString();
            }
        }
        catch (final MalformedURLException ex) {}
        return this.address;
    }
    
    @Override
    public void stop() {
        if (this.httpContext != null) {
            if (this.address == null) {
                this.httpContext.getServer().removeContext(this.httpContext);
            }
            else {
                ServerMgr.getInstance().removeContext(this.httpContext);
            }
        }
        this.adapter.getEndpoint().dispose();
    }
    
    private void setHandler(final HttpContext context) {
        context.setHandler(new WSHttpHandler(this.adapter, this.executor));
    }
    
    private void setHandler(final javax.xml.ws.spi.http.HttpContext context) {
        context.setHandler(new PortableHttpHandler(this.adapter, this.executor));
    }
    
    public <T extends EndpointReference> T getEndpointReference(final Class<T> clazz, final Element... referenceParameters) {
        final String eprAddress = this.getEPRAddress();
        return clazz.cast(this.adapter.getEndpoint().getEndpointReference(clazz, eprAddress, eprAddress + "?wsdl", referenceParameters));
    }
}
