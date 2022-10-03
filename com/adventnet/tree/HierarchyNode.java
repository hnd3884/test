package com.adventnet.tree;

import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import javax.swing.tree.MutableTreeNode;

public interface HierarchyNode extends MutableTreeNode, Cloneable
{
    void setNodeID(final long p0);
    
    long getNodeID();
    
    Row getKey();
    
    void setKey(final Row p0);
    
    Row getParentKey();
    
    void setParentKey(final Row p0);
    
    DataObject getDataObject();
    
    void setDataObject(final DataObject p0);
    
    void add(final MutableTreeNode p0);
    
    int getLevel();
    
    boolean isAccessible();
    
    void makeUnAccessible();
    
    Object clone();
    
    long[] getRootPath();
    
    void setRootPath(final long[] p0);
}
