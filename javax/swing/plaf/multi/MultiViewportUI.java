package javax.swing.plaf.multi;

import javax.accessibility.Accessible;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import java.util.Vector;
import javax.swing.plaf.ViewportUI;

public class MultiViewportUI extends ViewportUI
{
    protected Vector uis;
    
    public MultiViewportUI() {
        this.uis = new Vector();
    }
    
    public ComponentUI[] getUIs() {
        return MultiLookAndFeel.uisToArray(this.uis);
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
        final MultiViewportUI multiViewportUI = new MultiViewportUI();
        return MultiLookAndFeel.createUIs(multiViewportUI, multiViewportUI.uis, component);
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
