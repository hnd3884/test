package javax.swing.tree;

import javax.swing.event.TreeModelListener;

public interface TreeModel
{
    Object getRoot();
    
    Object getChild(final Object p0, final int p1);
    
    int getChildCount(final Object p0);
    
    boolean isLeaf(final Object p0);
    
    void valueForPathChanged(final TreePath p0, final Object p1);
    
    int getIndexOfChild(final Object p0, final Object p1);
    
    void addTreeModelListener(final TreeModelListener p0);
    
    void removeTreeModelListener(final TreeModelListener p0);
}
