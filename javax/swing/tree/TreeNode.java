package javax.swing.tree;

import java.util.Enumeration;

public interface TreeNode
{
    TreeNode getChildAt(final int p0);
    
    int getChildCount();
    
    TreeNode getParent();
    
    int getIndex(final TreeNode p0);
    
    boolean getAllowsChildren();
    
    boolean isLeaf();
    
    Enumeration children();
}
