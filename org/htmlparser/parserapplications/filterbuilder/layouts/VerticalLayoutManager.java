package org.htmlparser.parserapplications.filterbuilder.layouts;

import java.awt.Insets;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Container;
import java.io.Serializable;
import java.awt.LayoutManager2;

public class VerticalLayoutManager implements LayoutManager2, Serializable
{
    public Dimension minimumLayoutSize(final Container target) {
        return this.preferredLayoutSize(target);
    }
    
    public Dimension preferredLayoutSize(final Container target) {
        final Dimension ret;
        synchronized (target.getTreeLock()) {
            ret = new Dimension(0, 0);
            for (int count = target.getComponentCount(), i = 0; i < count; ++i) {
                final Component component = target.getComponent(i);
                if (component.isVisible()) {
                    final Dimension dimension = component.getPreferredSize();
                    ret.width = Math.max(ret.width, dimension.width);
                    final Dimension dimension2 = ret;
                    dimension2.height += dimension.height;
                }
            }
            final Insets insets = target.getInsets();
            final Dimension dimension3 = ret;
            dimension3.width += insets.left + insets.right;
            final Dimension dimension4 = ret;
            dimension4.height += insets.top + insets.bottom;
        }
        return ret;
    }
    
    public Dimension maximumLayoutSize(final Container target) {
        return this.preferredLayoutSize(target);
    }
    
    public void addLayoutComponent(final String name, final Component comp) {
    }
    
    public void removeLayoutComponent(final Component comp) {
    }
    
    public void layoutContainer(final Container target) {
        synchronized (target.getTreeLock()) {
            final Insets insets = target.getInsets();
            final int x = insets.left;
            int y = insets.top;
            final int count = target.getComponentCount();
            int width = 0;
            for (int i = 0; i < count; ++i) {
                final Component component = target.getComponent(i);
                if (component.isVisible()) {
                    final Dimension dimension = component.getPreferredSize();
                    width = Math.max(width, dimension.width);
                    component.setSize(dimension.width, dimension.height);
                    component.setLocation(x, y);
                    y += dimension.height;
                }
            }
            for (int i = 0; i < count; ++i) {
                final Component component = target.getComponent(i);
                if (component.isVisible()) {
                    final Dimension dimension = component.getSize();
                    component.setSize(dimension.width = width, dimension.height);
                }
            }
        }
    }
    
    public void addLayoutComponent(final Component comp, final Object constraints) {
    }
    
    public float getLayoutAlignmentX(final Container target) {
        return 0.0f;
    }
    
    public float getLayoutAlignmentY(final Container target) {
        return 0.0f;
    }
    
    public void invalidateLayout(final Container target) {
    }
}
