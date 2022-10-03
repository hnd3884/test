package org.glassfish.jersey.server.spi;

import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface Container
{
    public static final int DEFAULT_HTTP_PORT = 80;
    public static final int DEFAULT_HTTPS_PORT = 443;
    
    ResourceConfig getConfiguration();
    
    ApplicationHandler getApplicationHandler();
    
    void reload();
    
    void reload(final ResourceConfig p0);
}
