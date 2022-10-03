package javax.swing.tree;

public interface MutableTreeNode extends TreeNode
{
    void insert(final MutableTreeNode p0, final int p1);
    
    void remove(final int p0);
    
    void remove(final MutableTreeNode p0);
    
    void setUserObject(final Object p0);
    
    void removeFromParent();
    
    void setParent(final MutableTreeNode p0);
}
