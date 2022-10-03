package org.apache.axiom.core;

public interface CoreAttribute extends NonDeferringParentNode, CoreCharacterDataContainingParentNode
{
    CoreAttribute coreGetNextAttribute();
    
    CoreElement coreGetOwnerElement();
    
    CoreAttribute coreGetPreviousAttribute();
    
    boolean coreGetSpecified();
    
    boolean coreHasOwnerElement();
    
    boolean coreRemove(final Semantics p0);
    
    void coreSetOwnerDocument(final CoreDocument p0);
    
    void coreSetSpecified(final boolean p0);
    
    CoreNode getRootOrOwnerDocument();
    
    boolean internalRemove(final Semantics p0, final CoreElement p1);
    
    void internalSetNextAttribute(final CoreAttribute p0);
    
    void internalSetOwnerElement(final CoreElement p0);
    
    void internalUnsetOwnerElement(final CoreDocument p0);
}
