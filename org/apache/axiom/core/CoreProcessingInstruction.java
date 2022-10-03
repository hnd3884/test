package org.apache.axiom.core;

public interface CoreProcessingInstruction extends CoreChildNode, CoreCharacterDataContainingParentNode
{
    NodeType coreGetNodeType();
    
    String coreGetTarget();
    
    void coreSetTarget(final String p0);
    
     <T> void init(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
}
