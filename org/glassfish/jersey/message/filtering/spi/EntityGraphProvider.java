package org.glassfish.jersey.message.filtering.spi;

import java.util.Set;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface EntityGraphProvider
{
    EntityGraph getOrCreateEntityGraph(final Class<?> p0, final boolean p1);
    
    EntityGraph getOrCreateEmptyEntityGraph(final Class<?> p0, final boolean p1);
    
    boolean containsEntityGraph(final Class<?> p0, final boolean p1);
    
    ObjectGraph createObjectGraph(final Class<?> p0, final Set<String> p1, final boolean p2);
}
