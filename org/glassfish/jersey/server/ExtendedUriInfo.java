package org.glassfish.jersey.server;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.RuntimeResource;
import javax.ws.rs.core.PathSegment;
import org.glassfish.jersey.uri.UriTemplate;
import java.util.regex.MatchResult;
import java.util.List;
import javax.ws.rs.core.UriInfo;

public interface ExtendedUriInfo extends UriInfo
{
    Throwable getMappedThrowable();
    
    List<MatchResult> getMatchedResults();
    
    List<UriTemplate> getMatchedTemplates();
    
    List<PathSegment> getPathSegments(final String p0);
    
    List<PathSegment> getPathSegments(final String p0, final boolean p1);
    
    List<RuntimeResource> getMatchedRuntimeResources();
    
    ResourceMethod getMatchedResourceMethod();
    
    Resource getMatchedModelResource();
    
    List<ResourceMethod> getMatchedResourceLocators();
    
    List<Resource> getLocatorSubResources();
}
