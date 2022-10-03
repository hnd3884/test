package org.apache.commons.beanutils.expression;

public interface Resolver
{
    int getIndex(final String p0);
    
    String getKey(final String p0);
    
    String getProperty(final String p0);
    
    boolean hasNested(final String p0);
    
    boolean isIndexed(final String p0);
    
    boolean isMapped(final String p0);
    
    String next(final String p0);
    
    String remove(final String p0);
}
