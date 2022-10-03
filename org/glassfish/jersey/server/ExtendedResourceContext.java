package org.glassfish.jersey.server;

import org.glassfish.jersey.server.model.ResourceModel;
import javax.ws.rs.container.ResourceContext;

public interface ExtendedResourceContext extends ResourceContext
{
    ResourceModel getResourceModel();
}
