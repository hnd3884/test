package com.adventnet.tree;

public class DefaultOrderedHierarchyNode extends DefaultHierarchyNode implements OrderedHierarchyNode
{
    int nodeIndex;
    
    @Override
    public void setNodeIndex(final int nodeIndex) {
        this.nodeIndex = nodeIndex;
    }
    
    @Override
    public int getNodeIndex() {
        return this.nodeIndex;
    }
    
    @Override
    public Object clone() {
        final DefaultOrderedHierarchyNode dhn = (DefaultOrderedHierarchyNode)super.clone();
        dhn.nodeIndex = this.nodeIndex;
        return dhn;
    }
}
