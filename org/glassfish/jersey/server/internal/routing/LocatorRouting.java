package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.model.ResourceModel;

final class LocatorRouting
{
    final ResourceModel locator;
    final Router router;
    
    LocatorRouting(final ResourceModel locator, final Router router) {
        this.locator = locator;
        this.router = router;
    }
}
