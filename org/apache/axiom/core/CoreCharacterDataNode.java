package org.apache.axiom.core;

public interface CoreCharacterDataNode extends CoreLeafNode, CoreCharacterDataContainer
{
    Object coreGetCharacterData();
    
    NodeType coreGetNodeType();
    
    boolean coreIsIgnorable();
    
    void coreSetCharacterData(final Object p0);
    
    void coreSetCharacterData(final Object p0, final Semantics p1);
    
    void coreSetIgnorable(final boolean p0);
    
     <T> void init(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
}
