package org.jfree.layout;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Container;
import java.awt.Component;
import java.io.Serializable;
import java.awt.LayoutManager;

public class LCBLayout implements LayoutManager, Serializable
{
    private static final long serialVersionUID = -2531780832406163833L;
    private static final int COLUMNS = 3;
    private int[] colWidth;
    private int[] rowHeight;
    private int labelGap;
    private int buttonGap;
    private int vGap;
    
    public LCBLayout(final int maxrows) {
        this.labelGap = 10;
        this.buttonGap = 6;
        this.vGap = 2;
        this.colWidth = new int[3];
        this.rowHeight = new int[maxrows];
    }
    
    public void addLayoutComponent(final Component comp) {
    }
    
    public void addLayoutComponent(final String name, final Component comp) {
    }
    
    public void layoutContainer(final Container parent) {
        synchronized (parent.getTreeLock()) {
            final Insets insets = parent.getInsets();
            final int ncomponents = parent.getComponentCount();
            final int nrows = ncomponents / 3;
            for (int c = 0; c < 3; ++c) {
                for (int r = 0; r < nrows; ++r) {
                    final Component component = parent.getComponent(r * 3 + c);
                    final Dimension d = component.getPreferredSize();
                    if (this.colWidth[c] < d.width) {
                        this.colWidth[c] = d.width;
                    }
                    if (this.rowHeight[r] < d.height) {
                        this.rowHeight[r] = d.height;
                    }
                }
            }
            int totalHeight = this.vGap * (nrows - 1);
            for (int r2 = 0; r2 < nrows; ++r2) {
                totalHeight += this.rowHeight[r2];
            }
            final int totalWidth = this.colWidth[0] + this.colWidth[1] + this.colWidth[2];
            final int available = parent.getWidth() - insets.left - insets.right - this.labelGap - this.buttonGap;
            this.colWidth[1] += available - totalWidth;
            int x = insets.left;
            for (int c2 = 0; c2 < 3; ++c2) {
                int y = insets.top;
                for (int r3 = 0; r3 < nrows; ++r3) {
                    final int i = r3 * 3 + c2;
                    if (i < ncomponents) {
                        final Component component2 = parent.getComponent(i);
                        final Dimension d2 = component2.getPreferredSize();
                        final int h = d2.height;
                        final int adjust = (this.rowHeight[r3] - h) / 2;
                        parent.getComponent(i).setBounds(x, y + adjust, this.colWidth[c2], h);
                    }
                    y = y + this.rowHeight[r3] + this.vGap;
                }
                x += this.colWidth[c2];
                if (c2 == 0) {
                    x += this.labelGap;
                }
                if (c2 == 1) {
                    x += this.buttonGap;
                }
            }
            monitorexit(parent.getTreeLock());
        }
    }
    
    public Dimension minimumLayoutSize(final Container parent) {
        synchronized (parent.getTreeLock()) {
            final Insets insets = parent.getInsets();
            final int ncomponents = parent.getComponentCount();
            final int nrows = ncomponents / 3;
            for (int c = 0; c < 3; ++c) {
                for (int r = 0; r < nrows; ++r) {
                    final Component component = parent.getComponent(r * 3 + c);
                    final Dimension d = component.getMinimumSize();
                    if (this.colWidth[c] < d.width) {
                        this.colWidth[c] = d.width;
                    }
                    if (this.rowHeight[r] < d.height) {
                        this.rowHeight[r] = d.height;
                    }
                }
            }
            int totalHeight = this.vGap * (nrows - 1);
            for (int r2 = 0; r2 < nrows; ++r2) {
                totalHeight += this.rowHeight[r2];
            }
            final int totalWidth = this.colWidth[0] + this.labelGap + this.colWidth[1] + this.buttonGap + this.colWidth[2];
            final Dimension dimension = new Dimension(insets.left + insets.right + totalWidth + this.labelGap + this.buttonGap, insets.top + insets.bottom + totalHeight + this.vGap);
            monitorexit(parent.getTreeLock());
            return dimension;
        }
    }
    
    public Dimension preferredLayoutSize(final Container parent) {
        synchronized (parent.getTreeLock()) {
            final Insets insets = parent.getInsets();
            final int ncomponents = parent.getComponentCount();
            final int nrows = ncomponents / 3;
            for (int c = 0; c < 3; ++c) {
                for (int r = 0; r < nrows; ++r) {
                    final Component component = parent.getComponent(r * 3 + c);
                    final Dimension d = component.getPreferredSize();
                    if (this.colWidth[c] < d.width) {
                        this.colWidth[c] = d.width;
                    }
                    if (this.rowHeight[r] < d.height) {
                        this.rowHeight[r] = d.height;
                    }
                }
            }
            int totalHeight = this.vGap * (nrows - 1);
            for (int r2 = 0; r2 < nrows; ++r2) {
                totalHeight += this.rowHeight[r2];
            }
            final int totalWidth = this.colWidth[0] + this.labelGap + this.colWidth[1] + this.buttonGap + this.colWidth[2];
            final Dimension dimension = new Dimension(insets.left + insets.right + totalWidth + this.labelGap + this.buttonGap, insets.top + insets.bottom + totalHeight + this.vGap);
            monitorexit(parent.getTreeLock());
            return dimension;
        }
    }
    
    public void removeLayoutComponent(final Component comp) {
    }
    
    public void removeLayoutComponent(final String name, final Component comp) {
    }
}
