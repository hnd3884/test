package javax.ws.rs.core;

import java.net.URI;
import java.util.List;

public interface UriInfo
{
    String getPath();
    
    String getPath(final boolean p0);
    
    List<PathSegment> getPathSegments();
    
    List<PathSegment> getPathSegments(final boolean p0);
    
    URI getRequestUri();
    
    UriBuilder getRequestUriBuilder();
    
    URI getAbsolutePath();
    
    UriBuilder getAbsolutePathBuilder();
    
    URI getBaseUri();
    
    UriBuilder getBaseUriBuilder();
    
    MultivaluedMap<String, String> getPathParameters();
    
    MultivaluedMap<String, String> getPathParameters(final boolean p0);
    
    MultivaluedMap<String, String> getQueryParameters();
    
    MultivaluedMap<String, String> getQueryParameters(final boolean p0);
    
    List<String> getMatchedURIs();
    
    List<String> getMatchedURIs(final boolean p0);
    
    List<Object> getMatchedResources();
    
    URI resolve(final URI p0);
    
    URI relativize(final URI p0);
}
