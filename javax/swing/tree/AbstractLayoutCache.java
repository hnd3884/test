package javax.swing.tree;

import javax.swing.event.TreeModelEvent;
import java.util.Enumeration;
import java.awt.Rectangle;

public abstract class AbstractLayoutCache implements RowMapper
{
    protected NodeDimensions nodeDimensions;
    protected TreeModel treeModel;
    protected TreeSelectionModel treeSelectionModel;
    protected boolean rootVisible;
    protected int rowHeight;
    
    public void setNodeDimensions(final NodeDimensions nodeDimensions) {
        this.nodeDimensions = nodeDimensions;
    }
    
    public NodeDimensions getNodeDimensions() {
        return this.nodeDimensions;
    }
    
    public void setModel(final TreeModel treeModel) {
        this.treeModel = treeModel;
    }
    
    public TreeModel getModel() {
        return this.treeModel;
    }
    
    public void setRootVisible(final boolean rootVisible) {
        this.rootVisible = rootVisible;
    }
    
    public boolean isRootVisible() {
        return this.rootVisible;
    }
    
    public void setRowHeight(final int rowHeight) {
        this.rowHeight = rowHeight;
    }
    
    public int getRowHeight() {
        return this.rowHeight;
    }
    
    public void setSelectionModel(final TreeSelectionModel treeSelectionModel) {
        if (this.treeSelectionModel != null) {
            this.treeSelectionModel.setRowMapper(null);
        }
        this.treeSelectionModel = treeSelectionModel;
        if (this.treeSelectionModel != null) {
            this.treeSelectionModel.setRowMapper(this);
        }
    }
    
    public TreeSelectionModel getSelectionModel() {
        return this.treeSelectionModel;
    }
    
    public int getPreferredHeight() {
        final int rowCount = this.getRowCount();
        if (rowCount > 0) {
            final Rectangle bounds = this.getBounds(this.getPathForRow(rowCount - 1), null);
            if (bounds != null) {
                return bounds.y + bounds.height;
            }
        }
        return 0;
    }
    
    public int getPreferredWidth(final Rectangle rectangle) {
        if (this.getRowCount() > 0) {
            TreePath treePath;
            int n;
            if (rectangle == null) {
                treePath = this.getPathForRow(0);
                n = Integer.MAX_VALUE;
            }
            else {
                treePath = this.getPathClosestTo(rectangle.x, rectangle.y);
                n = rectangle.height + rectangle.y;
            }
            final Enumeration<TreePath> visiblePaths = this.getVisiblePathsFrom(treePath);
            if (visiblePaths != null && visiblePaths.hasMoreElements()) {
                Rectangle rectangle2 = this.getBounds(visiblePaths.nextElement(), null);
                int max;
                if (rectangle2 != null) {
                    max = rectangle2.x + rectangle2.width;
                    if (rectangle2.y >= n) {
                        return max;
                    }
                }
                else {
                    max = 0;
                }
                while (rectangle2 != null && visiblePaths.hasMoreElements()) {
                    rectangle2 = this.getBounds(visiblePaths.nextElement(), rectangle2);
                    if (rectangle2 != null && rectangle2.y < n) {
                        max = Math.max(max, rectangle2.x + rectangle2.width);
                    }
                    else {
                        rectangle2 = null;
                    }
                }
                return max;
            }
        }
        return 0;
    }
    
    public abstract boolean isExpanded(final TreePath p0);
    
    public abstract Rectangle getBounds(final TreePath p0, final Rectangle p1);
    
    public abstract TreePath getPathForRow(final int p0);
    
    public abstract int getRowForPath(final TreePath p0);
    
    public abstract TreePath getPathClosestTo(final int p0, final int p1);
    
    public abstract Enumeration<TreePath> getVisiblePathsFrom(final TreePath p0);
    
    public abstract int getVisibleChildCount(final TreePath p0);
    
    public abstract void setExpandedState(final TreePath p0, final boolean p1);
    
    public abstract boolean getExpandedState(final TreePath p0);
    
    public abstract int getRowCount();
    
    public abstract void invalidateSizes();
    
    public abstract void invalidatePathBounds(final TreePath p0);
    
    public abstract void treeNodesChanged(final TreeModelEvent p0);
    
    public abstract void treeNodesInserted(final TreeModelEvent p0);
    
    public abstract void treeNodesRemoved(final TreeModelEvent p0);
    
    public abstract void treeStructureChanged(final TreeModelEvent p0);
    
    @Override
    public int[] getRowsForPaths(final TreePath[] array) {
        if (array == null) {
            return null;
        }
        final int length = array.length;
        final int[] array2 = new int[length];
        for (int i = 0; i < length; ++i) {
            array2[i] = this.getRowForPath(array[i]);
        }
        return array2;
    }
    
    protected Rectangle getNodeDimensions(final Object o, final int n, final int n2, final boolean b, final Rectangle rectangle) {
        final NodeDimensions nodeDimensions = this.getNodeDimensions();
        if (nodeDimensions != null) {
            return nodeDimensions.getNodeDimensions(o, n, n2, b, rectangle);
        }
        return null;
    }
    
    protected boolean isFixedRowHeight() {
        return this.rowHeight > 0;
    }
    
    public abstract static class NodeDimensions
    {
        public abstract Rectangle getNodeDimensions(final Object p0, final int p1, final int p2, final boolean p3, final Rectangle p4);
    }
}
