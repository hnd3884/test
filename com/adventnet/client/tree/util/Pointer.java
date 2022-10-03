package com.adventnet.client.tree.util;

import java.util.Iterator;
import javax.swing.tree.MutableTreeNode;
import com.adventnet.tree.HierarchyNode;
import com.adventnet.persistence.Row;
import java.util.ArrayList;

class Pointer
{
    private ArrayList list;
    private Row key;
    private TreeCreator creator;
    
    public Pointer(final TreeCreator creator) {
        this.list = null;
        this.key = null;
        this.creator = null;
        this.list = new ArrayList();
        this.creator = creator;
    }
    
    public void setKey(final Row key) {
        this.key = key;
    }
    
    public Row getKey() {
        return this.key;
    }
    
    public ArrayList getList() {
        return this.list;
    }
    
    public void addToList(final HierarchyNode hNode) {
        this.list.add(hNode);
    }
    
    public void addNode(final HierarchyNode hNode) {
        if (this.list.size() > 0) {
            final HierarchyNode oldNode = this.list.get(0);
            for (int length = oldNode.getChildCount(), i = 0; i < length; ++i) {
                final HierarchyNode childNode = (HierarchyNode)oldNode.getChildAt(i);
                final HierarchyNode newChildNode = (HierarchyNode)childNode.clone();
                newChildNode.setParentKey(hNode.getKey());
                this.creator.getPointer(newChildNode.getKey()).addToList(newChildNode);
                hNode.add((MutableTreeNode)newChildNode);
            }
        }
        this.addToList(hNode);
    }
    
    public void addChild(final HierarchyNode hNode, final Pointer pointer) {
        for (final HierarchyNode currentNode : this.list) {
            final HierarchyNode childNode = (HierarchyNode)hNode.clone();
            currentNode.add((MutableTreeNode)childNode);
            pointer.addNode(childNode);
        }
    }
}
