package org.apache.axiom.core;

public interface CoreDocumentTypeDeclaration extends CoreLeafNode
{
    String coreGetInternalSubset();
    
    NodeType coreGetNodeType();
    
    String coreGetPublicId();
    
    String coreGetRootName();
    
    String coreGetSystemId();
    
    void coreSetInternalSubset(final String p0);
    
    void coreSetPublicId(final String p0);
    
    void coreSetRootName(final String p0);
    
    void coreSetSystemId(final String p0);
    
     <T> void init(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
}
