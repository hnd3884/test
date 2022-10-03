package org.apache.axiom.core;

public interface CoreDocumentFragment extends CoreMixedContentContainer, NonDeferringParentNode
{
    NodeType coreGetNodeType();
    
    void coreSetOwnerDocument(final CoreDocument p0);
    
    CoreNode getRootOrOwnerDocument();
    
     <T> void init(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
}
