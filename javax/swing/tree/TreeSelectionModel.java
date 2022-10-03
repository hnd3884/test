package javax.swing.tree;

import javax.swing.event.TreeSelectionListener;
import java.beans.PropertyChangeListener;

public interface TreeSelectionModel
{
    public static final int SINGLE_TREE_SELECTION = 1;
    public static final int CONTIGUOUS_TREE_SELECTION = 2;
    public static final int DISCONTIGUOUS_TREE_SELECTION = 4;
    
    void setSelectionMode(final int p0);
    
    int getSelectionMode();
    
    void setSelectionPath(final TreePath p0);
    
    void setSelectionPaths(final TreePath[] p0);
    
    void addSelectionPath(final TreePath p0);
    
    void addSelectionPaths(final TreePath[] p0);
    
    void removeSelectionPath(final TreePath p0);
    
    void removeSelectionPaths(final TreePath[] p0);
    
    TreePath getSelectionPath();
    
    TreePath[] getSelectionPaths();
    
    int getSelectionCount();
    
    boolean isPathSelected(final TreePath p0);
    
    boolean isSelectionEmpty();
    
    void clearSelection();
    
    void setRowMapper(final RowMapper p0);
    
    RowMapper getRowMapper();
    
    int[] getSelectionRows();
    
    int getMinSelectionRow();
    
    int getMaxSelectionRow();
    
    boolean isRowSelected(final int p0);
    
    void resetRowSelection();
    
    int getLeadSelectionRow();
    
    TreePath getLeadSelectionPath();
    
    void addPropertyChangeListener(final PropertyChangeListener p0);
    
    void removePropertyChangeListener(final PropertyChangeListener p0);
    
    void addTreeSelectionListener(final TreeSelectionListener p0);
    
    void removeTreeSelectionListener(final TreeSelectionListener p0);
}
