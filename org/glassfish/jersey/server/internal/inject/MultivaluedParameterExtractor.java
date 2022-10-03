package org.glassfish.jersey.server.internal.inject;

import javax.ws.rs.core.MultivaluedMap;

public interface MultivaluedParameterExtractor<T>
{
    String getName();
    
    String getDefaultValueString();
    
    T extract(final MultivaluedMap<String, String> p0);
}
