package org.glassfish.jersey.server.spi;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface ContainerLifecycleListener
{
    void onStartup(final Container p0);
    
    void onReload(final Container p0);
    
    void onShutdown(final Container p0);
}
