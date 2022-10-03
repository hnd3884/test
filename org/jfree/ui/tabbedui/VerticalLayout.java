package org.jfree.ui.tabbedui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.Container;
import java.awt.Component;
import java.awt.LayoutManager;

public class VerticalLayout implements LayoutManager
{
    private final boolean useSizeFromParent;
    
    public VerticalLayout() {
        this(true);
    }
    
    public VerticalLayout(final boolean useParent) {
        this.useSizeFromParent = useParent;
    }
    
    public void addLayoutComponent(final String name, final Component comp) {
    }
    
    public boolean isUseSizeFromParent() {
        return this.useSizeFromParent;
    }
    
    public void layoutContainer(final Container parent) {
        synchronized (parent.getTreeLock()) {
            final Insets ins = parent.getInsets();
            final int insHorizontal = ins.left + ins.right;
            int width;
            if (this.isUseSizeFromParent()) {
                final Rectangle bounds = parent.getBounds();
                width = bounds.width - insHorizontal;
            }
            else {
                width = this.preferredLayoutSize(parent).width - insHorizontal;
            }
            final Component[] comps = parent.getComponents();
            int y = ins.top;
            for (int i = 0; i < comps.length; ++i) {
                final Component c = comps[i];
                if (c.isVisible()) {
                    final Dimension dim = c.getPreferredSize();
                    c.setBounds(ins.left, y, width, dim.height);
                    y += dim.height;
                }
            }
            monitorexit(parent.getTreeLock());
        }
    }
    
    public Dimension minimumLayoutSize(final Container parent) {
        synchronized (parent.getTreeLock()) {
            final Insets ins = parent.getInsets();
            final Component[] comps = parent.getComponents();
            int height = ins.top + ins.bottom;
            int width = ins.left + ins.right;
            for (int i = 0; i < comps.length; ++i) {
                if (comps[i].isVisible()) {
                    final Dimension min = comps[i].getMinimumSize();
                    height += min.height;
                    if (min.width > width) {
                        width = min.width;
                    }
                }
            }
            final Dimension dimension = new Dimension(width + ins.left + ins.right, height + ins.top + ins.bottom);
            monitorexit(parent.getTreeLock());
            return dimension;
        }
    }
    
    public Dimension preferredLayoutSize(final Container parent) {
        synchronized (parent.getTreeLock()) {
            final Insets ins = parent.getInsets();
            final Component[] comps = parent.getComponents();
            int height = ins.top + ins.bottom;
            int width = ins.left + ins.right;
            for (int i = 0; i < comps.length; ++i) {
                if (comps[i].isVisible()) {
                    final Dimension pref = comps[i].getPreferredSize();
                    height += pref.height;
                    if (pref.width > width) {
                        width = pref.width;
                    }
                }
            }
            final Dimension dimension = new Dimension(width + ins.left + ins.right, height + ins.top + ins.bottom);
            monitorexit(parent.getTreeLock());
            return dimension;
        }
    }
    
    public void removeLayoutComponent(final Component comp) {
    }
}
