package javax.swing.plaf.multi;

import javax.accessibility.Accessible;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import java.awt.Rectangle;
import javax.swing.tree.TreePath;
import javax.swing.JTree;
import javax.swing.plaf.ComponentUI;
import java.util.Vector;
import javax.swing.plaf.TreeUI;

public class MultiTreeUI extends TreeUI
{
    protected Vector uis;
    
    public MultiTreeUI() {
        this.uis = new Vector();
    }
    
    public ComponentUI[] getUIs() {
        return MultiLookAndFeel.uisToArray(this.uis);
    }
    
    @Override
    public Rectangle getPathBounds(final JTree tree, final TreePath treePath) {
        final Rectangle pathBounds = this.uis.elementAt(0).getPathBounds(tree, treePath);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TreeUI)this.uis.elementAt(i)).getPathBounds(tree, treePath);
        }
        return pathBounds;
    }
    
    @Override
    public TreePath getPathForRow(final JTree tree, final int n) {
        final TreePath pathForRow = this.uis.elementAt(0).getPathForRow(tree, n);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TreeUI)this.uis.elementAt(i)).getPathForRow(tree, n);
        }
        return pathForRow;
    }
    
    @Override
    public int getRowForPath(final JTree tree, final TreePath treePath) {
        final int rowForPath = this.uis.elementAt(0).getRowForPath(tree, treePath);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TreeUI)this.uis.elementAt(i)).getRowForPath(tree, treePath);
        }
        return rowForPath;
    }
    
    @Override
    public int getRowCount(final JTree tree) {
        final int rowCount = this.uis.elementAt(0).getRowCount(tree);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TreeUI)this.uis.elementAt(i)).getRowCount(tree);
        }
        return rowCount;
    }
    
    @Override
    public TreePath getClosestPathForLocation(final JTree tree, final int n, final int n2) {
        final TreePath closestPathForLocation = this.uis.elementAt(0).getClosestPathForLocation(tree, n, n2);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TreeUI)this.uis.elementAt(i)).getClosestPathForLocation(tree, n, n2);
        }
        return closestPathForLocation;
    }
    
    @Override
    public boolean isEditing(final JTree tree) {
        final boolean editing = this.uis.elementAt(0).isEditing(tree);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TreeUI)this.uis.elementAt(i)).isEditing(tree);
        }
        return editing;
    }
    
    @Override
    public boolean stopEditing(final JTree tree) {
        final boolean stopEditing = this.uis.elementAt(0).stopEditing(tree);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TreeUI)this.uis.elementAt(i)).stopEditing(tree);
        }
        return stopEditing;
    }
    
    @Override
    public void cancelEditing(final JTree tree) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((TreeUI)this.uis.elementAt(i)).cancelEditing(tree);
        }
    }
    
    @Override
    public void startEditingAtPath(final JTree tree, final TreePath treePath) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((TreeUI)this.uis.elementAt(i)).startEditingAtPath(tree, treePath);
        }
    }
    
    @Override
    public TreePath getEditingPath(final JTree tree) {
        final TreePath editingPath = this.uis.elementAt(0).getEditingPath(tree);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((TreeUI)this.uis.elementAt(i)).getEditingPath(tree);
        }
        return editingPath;
    }
    
    @Override
    public boolean contains(final JComponent component, final int n, final int n2) {
        final boolean contains = this.uis.elementAt(0).contains(component, n, n2);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).contains(component, n, n2);
        }
        return contains;
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).update(graphics, component);
        }
    }
    
    public static ComponentUI createUI(final JComponent component) {
        final MultiTreeUI multiTreeUI = new MultiTreeUI();
        return MultiLookAndFeel.createUIs(multiTreeUI, multiTreeUI.uis, component);
    }
    
    @Override
    public void installUI(final JComponent component) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).installUI(component);
        }
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).uninstallUI(component);
        }
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        for (int i = 0; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).paint(graphics, component);
        }
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Dimension preferredSize = this.uis.elementAt(0).getPreferredSize(component);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getPreferredSize(component);
        }
        return preferredSize;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        final Dimension minimumSize = this.uis.elementAt(0).getMinimumSize(component);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getMinimumSize(component);
        }
        return minimumSize;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        final Dimension maximumSize = this.uis.elementAt(0).getMaximumSize(component);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getMaximumSize(component);
        }
        return maximumSize;
    }
    
    @Override
    public int getAccessibleChildrenCount(final JComponent component) {
        final int accessibleChildrenCount = this.uis.elementAt(0).getAccessibleChildrenCount(component);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getAccessibleChildrenCount(component);
        }
        return accessibleChildrenCount;
    }
    
    @Override
    public Accessible getAccessibleChild(final JComponent component, final int n) {
        final Accessible accessibleChild = this.uis.elementAt(0).getAccessibleChild(component, n);
        for (int i = 1; i < this.uis.size(); ++i) {
            ((ComponentUI)this.uis.elementAt(i)).getAccessibleChild(component, n);
        }
        return accessibleChild;
    }
}
