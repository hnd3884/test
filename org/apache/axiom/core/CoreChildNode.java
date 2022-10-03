package org.apache.axiom.core;

import org.apache.axiom.om.OMException;

public interface CoreChildNode extends CoreNode
{
     <T> CoreNode coreClone(final ClonePolicy<T> p0, final T p1, final CoreParentNode p2);
    
    void coreDetach(final Semantics p0);
    
    CoreChildNode coreGetNextSibling() throws OMException;
    
    CoreChildNode coreGetNextSibling(final NodeFilter p0);
    
    CoreChildNode coreGetNextSiblingIfAvailable();
    
    CoreParentNode coreGetParent();
    
    CoreElement coreGetParentElement();
    
    CoreChildNode coreGetPreviousSibling();
    
    CoreChildNode coreGetPreviousSibling(final NodeFilter p0);
    
    boolean coreHasParent();
    
    void coreInsertSiblingAfter(final CoreChildNode p0);
    
    void coreInsertSiblingBefore(final CoreChildNode p0);
    
    void coreInsertSiblingsBefore(final CoreDocumentFragment p0);
    
    void coreReplaceWith(final CoreChildNode p0, final Semantics p1);
    
    void coreSetNextSibling(final CoreChildNode p0);
    
    void coreSetOwnerDocument(final CoreDocument p0);
    
    void coreSetPreviousSibling(final CoreChildNode p0);
    
    CoreNode getRootOrOwnerDocument();
    
    void internalSetParent(final CoreParentNode p0);
    
    void internalUnsetParent(final CoreDocument p0);
}
