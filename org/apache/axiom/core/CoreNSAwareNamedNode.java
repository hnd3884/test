package org.apache.axiom.core;

public interface CoreNSAwareNamedNode extends CoreNamedNode
{
    String coreGetNamespaceURI();
    
    String coreGetPrefix();
    
    void coreSetPrefix(final String p0);
    
    String coreGetLocalName();
    
    void coreSetName(final String p0, final String p1, final String p2);
}
