package org.apache.axiom.core;

public interface CoreNamespaceDeclaration extends CoreAttribute
{
    String coreGetDeclaredPrefix();
    
    void coreSetDeclaredNamespace(final String p0, final String p1);
    
    NodeType coreGetNodeType();
    
     <T> void init(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
}
