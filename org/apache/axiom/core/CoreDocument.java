package org.apache.axiom.core;

public interface CoreDocument extends CoreParentNode
{
    CoreElement coreGetDocumentElement();
    
    String coreGetInputEncoding();
    
    NodeType coreGetNodeType();
    
    String coreGetXmlEncoding();
    
    String coreGetXmlVersion();
    
    boolean coreIsStandalone();
    
    void coreSetInputEncoding(final String p0);
    
    void coreSetOwnerDocument(final CoreDocument p0);
    
    void coreSetStandalone(final boolean p0);
    
    void coreSetXmlEncoding(final String p0);
    
    void coreSetXmlVersion(final String p0);
    
    CoreNode getRootOrOwnerDocument();
    
     <T> void init(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
}
