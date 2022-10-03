package org.glassfish.jersey.server.internal.routing;

import java.util.LinkedList;
import org.glassfish.jersey.uri.PathPattern;
import java.util.List;

final class PathMatchingRouterBuilder implements PathToRouterBuilder
{
    private final List<Route> acceptedRoutes;
    private List<Router> currentRouters;
    
    static PathToRouterBuilder newRoute(final PathPattern pattern) {
        final PathMatchingRouterBuilder builder = new PathMatchingRouterBuilder();
        builder.startNewRoute(pattern);
        return builder;
    }
    
    private PathMatchingRouterBuilder() {
        this.acceptedRoutes = new LinkedList<Route>();
    }
    
    private void startNewRoute(final PathPattern pattern) {
        this.currentRouters = new LinkedList<Router>();
        this.acceptedRoutes.add(Route.of(pattern, this.currentRouters));
    }
    
    protected List<Route> acceptedRoutes() {
        return this.acceptedRoutes;
    }
    
    @Override
    public PathMatchingRouterBuilder to(final Router router) {
        this.currentRouters.add(router);
        return this;
    }
    
    public PathToRouterBuilder route(final PathPattern pattern) {
        this.startNewRoute(pattern);
        return this;
    }
    
    public PathMatchingRouter build() {
        return new PathMatchingRouter(this.acceptedRoutes());
    }
}
