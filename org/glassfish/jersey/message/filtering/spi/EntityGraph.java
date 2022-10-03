package org.glassfish.jersey.message.filtering.spi;

import java.util.Map;
import java.util.Set;

public interface EntityGraph
{
    EntityGraph addField(final String p0);
    
    EntityGraph addField(final String p0, final String... p1);
    
    EntityGraph addField(final String p0, final Set<String> p1);
    
    EntityGraph addSubgraph(final String p0, final Class<?> p1);
    
    EntityGraph addSubgraph(final String p0, final Class<?> p1, final String... p2);
    
    EntityGraph addSubgraph(final String p0, final Class<?> p1, final Set<String> p2);
    
    EntityGraph addFilteringScopes(final Set<String> p0);
    
    boolean presentInScope(final String p0, final String p1);
    
    boolean presentInScopes(final String p0);
    
    Class<?> getEntityClass();
    
    Set<String> getFields(final String p0);
    
    Set<String> getFields(final String... p0);
    
    Set<String> getFields(final Set<String> p0);
    
    Set<String> getFilteringScopes();
    
    Set<String> getClassFilteringScopes();
    
    Map<String, Class<?>> getSubgraphs(final String p0);
    
    Map<String, Class<?>> getSubgraphs(final String... p0);
    
    Map<String, Class<?>> getSubgraphs(final Set<String> p0);
    
    EntityGraph remove(final String p0);
}
