package org.glassfish.jersey.server.internal.scanning;

import org.glassfish.jersey.server.ResourceFinder;
import java.net.URI;
import java.util.Set;

interface UriSchemeResourceFinderFactory
{
    Set<String> getSchemes();
    
    ResourceFinder create(final URI p0, final boolean p1);
}
