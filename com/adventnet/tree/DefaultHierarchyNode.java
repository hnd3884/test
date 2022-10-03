package com.adventnet.tree;

import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.MutableTreeNode;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import javax.swing.tree.DefaultMutableTreeNode;

public class DefaultHierarchyNode extends DefaultMutableTreeNode implements HierarchyNode
{
    private Row key;
    private Row parentKey;
    private long nodeID;
    private DataObject dataObject;
    private long[] rootPath;
    boolean accessible;
    
    public DefaultHierarchyNode() {
        this.nodeID = -10L;
        this.rootPath = null;
        this.accessible = true;
    }
    
    public DefaultHierarchyNode(final DataObject dataObject) {
        this.nodeID = -10L;
        this.rootPath = null;
        this.accessible = true;
        this.setDataObject(dataObject);
    }
    
    @Override
    public void setNodeID(final long nodeID) {
        this.nodeID = nodeID;
    }
    
    @Override
    public long[] getRootPath() {
        return this.rootPath;
    }
    
    @Override
    public void setRootPath(final long[] rootPath) {
        this.rootPath = rootPath;
        if (rootPath != null && rootPath.length > 0) {
            this.nodeID = rootPath[rootPath.length - 1];
        }
    }
    
    @Override
    public Row getKey() {
        return this.key;
    }
    
    @Override
    public void setKey(final Row key) {
        this.key = key;
    }
    
    @Override
    public Row getParentKey() {
        return this.parentKey;
    }
    
    @Override
    public void setParentKey(final Row key) {
        this.parentKey = key;
    }
    
    @Override
    public long getNodeID() {
        return this.nodeID;
    }
    
    @Override
    public DataObject getDataObject() {
        return this.dataObject;
    }
    
    @Override
    public void setDataObject(final DataObject dataObject) {
        this.dataObject = dataObject;
    }
    
    @Override
    public void add(final MutableTreeNode node) {
        super.add(node);
        ((HierarchyNode)node).setParentKey(this.getKey());
    }
    
    @Override
    public String toString() {
        String keyString = "";
        for (int i = 0; i < this.getKey().getPKColumns().size(); ++i) {
            final String columnName = this.getKey().getPKColumns().get(i);
            final Object value = this.getKey().get(columnName);
            keyString = keyString + columnName + " = " + value + ",";
        }
        if (this.rootPath != null) {
            return "[" + this.key.getTableName() + " : " + keyString + " NODEID = " + this.getNodeID() + " ]";
        }
        return "[" + this.key.getTableName() + " : " + keyString + " NODEID = " + this.getNodeID() + " ]";
    }
    
    @Override
    public boolean isAccessible() {
        return this.accessible;
    }
    
    @Override
    public void makeUnAccessible() {
        this.accessible = false;
    }
    
    @Override
    public Object clone() {
        final DefaultHierarchyNode dhn = (DefaultHierarchyNode)super.clone();
        dhn.setKey(this.key);
        dhn.setParentKey(this.parentKey);
        dhn.nodeID = this.nodeID;
        dhn.dataObject = this.dataObject;
        dhn.rootPath = this.rootPath;
        for (int i = 0; i < this.getChildCount(); ++i) {
            final HierarchyNode childNode = (HierarchyNode)this.getChildAt(i);
            dhn.add((MutableTreeNode)childNode.clone());
        }
        return dhn;
    }
    
    private List asList(final long[] rootPath) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < rootPath.length; ++i) {
            list.add(new Long(rootPath[i]));
        }
        return list;
    }
}
