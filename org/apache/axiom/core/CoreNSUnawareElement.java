package org.apache.axiom.core;

public interface CoreNSUnawareElement extends CoreElement, CoreNSUnawareNamedNode
{
    NodeType coreGetNodeType();
    
    String getImplicitNamespaceURI(final String p0);
    
    String getImplicitPrefix(final String p0);
}
