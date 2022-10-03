package org.apache.axiom.core;

public interface CoreEntityReference extends CoreLeafNode
{
    String coreGetName();
    
    NodeType coreGetNodeType();
    
    String coreGetReplacementText();
    
    void coreSetName(final String p0);
    
    void coreSetReplacementText(final String p0);
    
     <T> void init(final ClonePolicy<T> p0, final T p1, final CoreNode p2);
}
