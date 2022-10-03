package org.glassfish.jersey.server.internal.routing;

import java.util.regex.MatchResult;
import org.glassfish.jersey.uri.PathPattern;
import java.util.Iterator;
import org.glassfish.jersey.server.internal.ServerTraceEvent;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.message.internal.TracingLogger;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import java.util.List;

final class PathMatchingRouter implements Router
{
    private final List<Route> acceptedRoutes;
    
    PathMatchingRouter(final List<Route> routes) {
        this.acceptedRoutes = routes;
    }
    
    @Override
    public Continuation apply(final RequestProcessingContext context) {
        final RoutingContext rc = context.routingContext();
        final String path = rc.getFinalMatchingGroup();
        final TracingLogger tracingLogger = TracingLogger.getInstance((PropertiesDelegate)context.request());
        tracingLogger.log((TracingLogger.Event)ServerTraceEvent.MATCH_PATH_FIND, new Object[] { path });
        Continuation result = null;
        final Iterator<Route> iterator = this.acceptedRoutes.iterator();
        while (iterator.hasNext()) {
            final Route acceptedRoute = iterator.next();
            final PathPattern routePattern = acceptedRoute.routingPattern();
            final MatchResult m = routePattern.match((CharSequence)path);
            if (m != null) {
                rc.pushMatchResult(m);
                result = Continuation.of(context, acceptedRoute.next());
                tracingLogger.log((TracingLogger.Event)ServerTraceEvent.MATCH_PATH_SELECTED, new Object[] { routePattern.getRegex() });
                break;
            }
            tracingLogger.log((TracingLogger.Event)ServerTraceEvent.MATCH_PATH_NOT_MATCHED, new Object[] { routePattern.getRegex() });
        }
        if (tracingLogger.isLogEnabled((TracingLogger.Event)ServerTraceEvent.MATCH_PATH_SKIPPED)) {
            while (iterator.hasNext()) {
                tracingLogger.log((TracingLogger.Event)ServerTraceEvent.MATCH_PATH_SKIPPED, new Object[] { iterator.next().routingPattern().getRegex() });
            }
        }
        if (result == null) {
            return Continuation.of(context);
        }
        return result;
    }
}
