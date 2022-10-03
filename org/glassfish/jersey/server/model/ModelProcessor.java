package org.glassfish.jersey.server.model;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface ModelProcessor
{
    ResourceModel processResourceModel(final ResourceModel p0, final Configuration p1);
    
    ResourceModel processSubResource(final ResourceModel p0, final Configuration p1);
}
