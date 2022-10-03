package org.glassfish.jersey.server.internal.routing;

import java.util.List;
import org.glassfish.jersey.uri.PathPattern;

final class Route
{
    private final PathPattern routingPattern;
    private final List<Router> routers;
    
    static Route of(final PathPattern routingPattern, final List<Router> routers) {
        return new Route(routingPattern, routers);
    }
    
    private Route(final PathPattern routingPattern, final List<Router> routers) {
        this.routingPattern = routingPattern;
        this.routers = routers;
    }
    
    public PathPattern routingPattern() {
        return this.routingPattern;
    }
    
    public List<Router> next() {
        return this.routers;
    }
}
