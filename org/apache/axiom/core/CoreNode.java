package org.apache.axiom.core;

public interface CoreNode
{
    void coreSetOwnerDocument(final CoreDocument p0);
    
    NodeFactory coreGetNodeFactory();
    
    NodeType coreGetNodeType();
    
     <T> void init(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
    
     <T> void cloneChildrenIfNecessary(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
    
     <T> CoreNode coreClone(final ClonePolicy<T> p0, final T p1);
    
     <T extends CoreNode> T coreCreateNode(final Class<T> p0);
    
    Class<? extends CoreNode> coreGetNodeClass();
    
    CoreDocument coreGetOwnerDocument(final boolean p0);
    
    boolean coreHasSameOwnerDocument(final CoreNode p0);
    
    CoreNode getRootOrOwnerDocument();
    
     <T> void initAncillaryData(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
    
     <T> CoreNode shallowClone(final ClonePolicy<T> p0, final T p1);
    
    void updateFiliation(final CoreNode p0);
}
