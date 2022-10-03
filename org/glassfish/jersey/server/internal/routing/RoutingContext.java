package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.RuntimeResource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.internal.process.Endpoint;
import org.glassfish.jersey.uri.UriTemplate;
import java.util.regex.MatchResult;
import org.glassfish.jersey.server.ExtendedUriInfo;
import javax.ws.rs.container.ResourceInfo;

public interface RoutingContext extends ResourceInfo, ExtendedUriInfo
{
    void pushMatchResult(final MatchResult p0);
    
    void pushMatchedResource(final Object p0);
    
    Object peekMatchedResource();
    
    void pushTemplates(final UriTemplate p0, final UriTemplate p1);
    
    String getFinalMatchingGroup();
    
    void pushLeftHandPath();
    
    void setEndpoint(final Endpoint p0);
    
    Endpoint getEndpoint();
    
    void setMatchedResourceMethod(final ResourceMethod p0);
    
    void pushMatchedLocator(final ResourceMethod p0);
    
    void pushMatchedRuntimeResource(final RuntimeResource p0);
    
    void pushLocatorSubResource(final Resource p0);
    
    void setMappedThrowable(final Throwable p0);
}
