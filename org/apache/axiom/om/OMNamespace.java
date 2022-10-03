package org.apache.axiom.om;

public interface OMNamespace
{
    boolean equals(final String p0, final String p1);
    
    String getPrefix();
    
    @Deprecated
    String getName();
    
    String getNamespaceURI();
}
