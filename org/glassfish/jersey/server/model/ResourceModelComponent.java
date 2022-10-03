package org.glassfish.jersey.server.model;

import java.util.List;

public interface ResourceModelComponent
{
    void accept(final ResourceModelVisitor p0);
    
    List<? extends ResourceModelComponent> getComponents();
}
