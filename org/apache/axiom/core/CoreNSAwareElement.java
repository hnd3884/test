package org.apache.axiom.core;

public interface CoreNSAwareElement extends CoreElement, CoreNSAwareNamedNode
{
    NodeType coreGetNodeType();
    
    String getImplicitNamespaceURI(final String p0);
    
    String getImplicitPrefix(final String p0);
}
