package com.adventnet.tree.manager;

import com.adventnet.ds.query.SelectQuery;
import java.util.Map;
import java.util.List;
import com.adventnet.tree.query.TreeQuery;
import com.adventnet.tree.TreeException;
import com.adventnet.tree.HierarchyNode;
import com.adventnet.persistence.Row;

public interface TreeManager
{
    HierarchyNode addNode(final Row p0, final HierarchyNode p1) throws TreeException;
    
    HierarchyNode deleteNode(final Row p0, final Row p1) throws TreeException;
    
    HierarchyNode moveNode(final Row p0, final Row p1, final Row p2) throws TreeException;
    
    HierarchyNode getNode(final Row p0, final Row p1, final int p2) throws TreeException;
    
    HierarchyNode getNode(final Row p0, final Row p1) throws TreeException;
    
    HierarchyNode getNode(final TreeQuery p0) throws TreeException;
    
    HierarchyNode getAncestors(final Row p0, final Row p1) throws TreeException;
    
    HierarchyNode getAncestors(final Row p0, final Row p1, final int p2) throws TreeException;
    
    long[] getTreePath(final Row p0, final Row p1) throws TreeException;
    
    int levelOfElement(final Row p0, final Row p1) throws TreeException;
    
    Map getAncestors(final Row p0, final List p1) throws TreeException;
    
    SelectQuery getAncestorQuery(final Row p0, final Row p1) throws TreeException;
}
