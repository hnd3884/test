package com.sun.xml.internal.ws.transport.http.server;

import java.net.URISyntaxException;
import com.sun.istack.internal.NotNull;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.server.WebModule;
import java.net.URI;
import java.util.logging.Level;
import com.sun.xml.internal.ws.api.server.Module;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;

public final class ServerAdapter extends HttpAdapter implements BoundEndpoint
{
    final String name;
    private static final Logger LOGGER;
    
    protected ServerAdapter(final String name, final String urlPattern, final WSEndpoint endpoint, final ServerAdapterList owner) {
        super(endpoint, owner, urlPattern);
        this.name = name;
        final Module module = endpoint.getContainer().getSPI(Module.class);
        if (module == null) {
            ServerAdapter.LOGGER.log(Level.WARNING, "Container {0} doesn''t support {1}", new Object[] { endpoint.getContainer(), Module.class });
        }
        else {
            module.getBoundEndpoints().add(this);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    @NotNull
    @Override
    public URI getAddress() {
        final WebModule webModule = this.endpoint.getContainer().getSPI(WebModule.class);
        if (webModule == null) {
            throw new WebServiceException("Container " + this.endpoint.getContainer() + " doesn't support " + WebModule.class);
        }
        return this.getAddress(webModule.getContextPath());
    }
    
    @NotNull
    @Override
    public URI getAddress(final String baseAddress) {
        final String adrs = baseAddress + this.getValidPath();
        try {
            return new URI(adrs);
        }
        catch (final URISyntaxException e) {
            throw new WebServiceException("Unable to compute address for " + this.endpoint, e);
        }
    }
    
    public void dispose() {
        this.endpoint.dispose();
    }
    
    public String getUrlPattern() {
        return this.urlPattern;
    }
    
    @Override
    public String toString() {
        return super.toString() + "[name=" + this.name + ']';
    }
    
    static {
        LOGGER = Logger.getLogger(ServerAdapter.class.getName());
    }
}
