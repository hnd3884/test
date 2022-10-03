package org.apache.axiom.core;

public interface AttributeMatcher
{
    boolean matches(final CoreAttribute p0, final String p1, final String p2);
    
    String getNamespaceURI(final CoreAttribute p0);
    
    String getName(final CoreAttribute p0);
    
    CoreAttribute createAttribute(final CoreElement p0, final String p1, final String p2, final String p3, final String p4);
    
    void update(final CoreAttribute p0, final String p1, final String p2);
}
