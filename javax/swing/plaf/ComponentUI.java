package javax.swing.plaf;

import javax.accessibility.Accessible;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;

public abstract class ComponentUI
{
    public void installUI(final JComponent component) {
    }
    
    public void uninstallUI(final JComponent component) {
    }
    
    public void paint(final Graphics graphics, final JComponent component) {
    }
    
    public void update(final Graphics graphics, final JComponent component) {
        if (component.isOpaque()) {
            graphics.setColor(component.getBackground());
            graphics.fillRect(0, 0, component.getWidth(), component.getHeight());
        }
        this.paint(graphics, component);
    }
    
    public Dimension getPreferredSize(final JComponent component) {
        return null;
    }
    
    public Dimension getMinimumSize(final JComponent component) {
        return this.getPreferredSize(component);
    }
    
    public Dimension getMaximumSize(final JComponent component) {
        return this.getPreferredSize(component);
    }
    
    public boolean contains(final JComponent component, final int n, final int n2) {
        return component.inside(n, n2);
    }
    
    public static ComponentUI createUI(final JComponent component) {
        throw new Error("ComponentUI.createUI not implemented.");
    }
    
    public int getBaseline(final JComponent component, final int n, final int n2) {
        if (component == null) {
            throw new NullPointerException("Component must be non-null");
        }
        if (n < 0 || n2 < 0) {
            throw new IllegalArgumentException("Width and height must be >= 0");
        }
        return -1;
    }
    
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        if (component == null) {
            throw new NullPointerException("Component must be non-null");
        }
        return Component.BaselineResizeBehavior.OTHER;
    }
    
    public int getAccessibleChildrenCount(final JComponent component) {
        return SwingUtilities.getAccessibleChildrenCount(component);
    }
    
    public Accessible getAccessibleChild(final JComponent component, final int n) {
        return SwingUtilities.getAccessibleChild(component, n);
    }
}
