package org.glassfish.jersey.server.monitoring;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface DestroyListener
{
    void onDestroy();
}
