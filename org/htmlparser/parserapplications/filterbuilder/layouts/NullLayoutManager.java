package org.htmlparser.parserapplications.filterbuilder.layouts;

import java.awt.Point;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Container;
import java.io.Serializable;
import java.awt.LayoutManager2;

public class NullLayoutManager implements LayoutManager2, Serializable
{
    public Dimension minimumLayoutSize(final Container target) {
        return this.preferredLayoutSize(target);
    }
    
    public Dimension preferredLayoutSize(final Container target) {
        Dimension ret;
        synchronized (target.getTreeLock()) {
            final int count = target.getComponentCount();
            if (0 == count) {
                ret = target.getSize();
                final Container parent = target.getParent();
                if (null != parent) {
                    final Insets insets = parent.getInsets();
                    ret = parent.getSize();
                    ret.setSize(ret.width - insets.left - insets.right, ret.height - insets.top - insets.bottom);
                }
            }
            else {
                ret = new Dimension(0, 0);
                for (int i = 0; i < count; ++i) {
                    final Component component = target.getComponent(i);
                    if (component.isVisible()) {
                        final Point point = component.getLocation();
                        final Dimension dimension = component.getPreferredSize();
                        ret.width = Math.max(ret.width, point.x + dimension.width);
                        ret.height = Math.max(ret.height, point.y + dimension.height);
                    }
                }
                final Insets insets = target.getInsets();
                final Dimension dimension2 = ret;
                dimension2.width += insets.left + insets.right;
                final Dimension dimension3 = ret;
                dimension3.height += insets.top + insets.bottom;
            }
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
            for (int count = target.getComponentCount(), i = 0; i < count; ++i) {
                final Component component = target.getComponent(i);
                if (component.isVisible()) {
                    final Dimension dimension = component.getPreferredSize();
                    component.setSize(dimension.width, dimension.height);
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
