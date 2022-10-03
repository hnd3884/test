package org.jfree.ui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.Container;
import java.awt.Component;
import java.awt.LayoutManager;

public final class OverlayLayout implements LayoutManager
{
    private boolean ignoreInvisible;
    
    public OverlayLayout() {
    }
    
    public OverlayLayout(final boolean ignoreInvisible) {
        this.ignoreInvisible = ignoreInvisible;
    }
    
    public void addLayoutComponent(final String name, final Component comp) {
    }
    
    public void layoutContainer(final Container parent) {
        synchronized (parent.getTreeLock()) {
            final Insets ins = parent.getInsets();
            final Rectangle bounds = parent.getBounds();
            final int width = bounds.width - ins.left - ins.right;
            final int height = bounds.height - ins.top - ins.bottom;
            final Component[] comps = parent.getComponents();
            for (int i = 0; i < comps.length; ++i) {
                final Component c = comps[i];
                if (comps[i].isVisible() || !this.ignoreInvisible) {
                    c.setBounds(ins.left, ins.top, width, height);
                }
            }
            monitorexit(parent.getTreeLock());
        }
    }
    
    public Dimension minimumLayoutSize(final Container parent) {
        synchronized (parent.getTreeLock()) {
            final Insets ins = parent.getInsets();
            final Component[] comps = parent.getComponents();
            int height = 0;
            int width = 0;
            for (int i = 0; i < comps.length; ++i) {
                if (comps[i].isVisible() || !this.ignoreInvisible) {
                    final Dimension pref = comps[i].getMinimumSize();
                    if (pref.height > height) {
                        height = pref.height;
                    }
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
    
    public Dimension preferredLayoutSize(final Container parent) {
        synchronized (parent.getTreeLock()) {
            final Insets ins = parent.getInsets();
            final Component[] comps = parent.getComponents();
            int height = 0;
            int width = 0;
            for (int i = 0; i < comps.length; ++i) {
                if (comps[i].isVisible() || !this.ignoreInvisible) {
                    final Dimension pref = comps[i].getPreferredSize();
                    if (pref.height > height) {
                        height = pref.height;
                    }
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
