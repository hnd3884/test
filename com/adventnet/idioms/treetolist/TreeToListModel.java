package com.adventnet.idioms.treetolist;

import javax.swing.DefaultListModel;
import javax.swing.tree.TreeNode;

public interface TreeToListModel
{
    boolean isNodeAddable(final TreeNode p0);
    
    Object getObjectToAdd(final TreeNode p0);
    
    String getNameForNode(final TreeNode p0);
    
    DefaultListModel getListModel();
    
    void addOption(final TreeNode p0);
    
    void removeOption(final Object p0);
}
