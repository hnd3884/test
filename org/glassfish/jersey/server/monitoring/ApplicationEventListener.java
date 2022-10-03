package org.glassfish.jersey.server.monitoring;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface ApplicationEventListener
{
    void onEvent(final ApplicationEvent p0);
    
    RequestEventListener onRequest(final RequestEvent p0);
}
