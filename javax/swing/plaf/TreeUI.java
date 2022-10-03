package javax.swing.plaf;

import java.awt.Rectangle;
import javax.swing.tree.TreePath;
import javax.swing.JTree;

public abstract class TreeUI extends ComponentUI
{
    public abstract Rectangle getPathBounds(final JTree p0, final TreePath p1);
    
    public abstract TreePath getPathForRow(final JTree p0, final int p1);
    
    public abstract int getRowForPath(final JTree p0, final TreePath p1);
    
    public abstract int getRowCount(final JTree p0);
    
    public abstract TreePath getClosestPathForLocation(final JTree p0, final int p1, final int p2);
    
    public abstract boolean isEditing(final JTree p0);
    
    public abstract boolean stopEditing(final JTree p0);
    
    public abstract void cancelEditing(final JTree p0);
    
    public abstract void startEditingAtPath(final JTree p0, final TreePath p1);
    
    public abstract TreePath getEditingPath(final JTree p0);
}
