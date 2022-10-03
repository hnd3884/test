package com.adventnet.idioms.treetolist;

import javax.swing.event.ListDataListener;
import javax.swing.tree.TreeNode;
import javax.swing.DefaultListModel;

public class DefaultTreeToListModel implements TreeToListModel
{
    private DefaultListModel listModel;
    
    public DefaultTreeToListModel() {
        this.listModel = null;
        this.listModel = new DefaultListModel();
    }
    
    public void setListModel(final DefaultListModel listModel) {
        this.clearAll();
        this.listModel = listModel;
    }
    
    public DefaultListModel getListModel() {
        return this.listModel;
    }
    
    public void addOption(final TreeNode treeNode) {
        if (treeNode != null && this.isNodeAddable(treeNode) && !this.isNodeAdded(treeNode)) {
            this.listModel.addElement(this.getObjectToAdd(treeNode));
        }
    }
    
    public void removeOption(final Object o) {
        this.listModel.removeElement(o);
    }
    
    public boolean isNodeAdded(final TreeNode treeNode) {
        return this.listModel.contains(this.getObjectToAdd(treeNode));
    }
    
    public void clearAll() {
        this.listModel.removeAllElements();
    }
    
    public void addListDataListener(final ListDataListener listDataListener) {
        this.listModel.addListDataListener(listDataListener);
    }
    
    public void removeListDataListener(final ListDataListener listDataListener) {
        this.listModel.removeListDataListener(listDataListener);
    }
    
    public String getNameForNode(final TreeNode treeNode) {
        return treeNode.toString();
    }
    
    public Object getObjectToAdd(final TreeNode treeNode) {
        return treeNode;
    }
    
    public boolean isNodeAddable(final TreeNode treeNode) {
        return !this.isNodeAdded(treeNode);
    }
}
