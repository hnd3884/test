package org.glassfish.jersey.client;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.CLIENT)
public interface ClientLifecycleListener
{
    void onInit();
    
    void onClose();
}
