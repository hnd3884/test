package org.apache.axiom.core;

public interface CoreCDATASection extends CoreChildNode, CoreCharacterDataContainingParentNode
{
    NodeType coreGetNodeType();
    
     <T> void init(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
}
