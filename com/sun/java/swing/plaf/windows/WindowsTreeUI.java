package com.sun.java.swing.plaf.windows;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Component;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.tree.TreeCellRenderer;
import java.awt.Rectangle;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTreeUI;

public class WindowsTreeUI extends BasicTreeUI
{
    protected static final int HALF_SIZE = 4;
    protected static final int SIZE = 9;
    
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsTreeUI();
    }
    
    @Override
    protected void ensureRowsAreVisible(final int n, final int n2) {
        if (this.tree != null && n >= 0 && n2 < this.getRowCount(this.tree)) {
            final Rectangle visibleRect = this.tree.getVisibleRect();
            if (n == n2) {
                final Rectangle pathBounds = this.getPathBounds(this.tree, this.getPathForRow(this.tree, n));
                if (pathBounds != null) {
                    pathBounds.x = visibleRect.x;
                    pathBounds.width = visibleRect.width;
                    this.tree.scrollRectToVisible(pathBounds);
                }
            }
            else {
                final Rectangle pathBounds2 = this.getPathBounds(this.tree, this.getPathForRow(this.tree, n));
                if (pathBounds2 != null) {
                    Rectangle pathBounds3 = pathBounds2;
                    final int y = pathBounds2.y;
                    final int n3 = y + visibleRect.height;
                    for (int i = n + 1; i <= n2; ++i) {
                        pathBounds3 = this.getPathBounds(this.tree, this.getPathForRow(this.tree, i));
                        if (pathBounds3 != null && pathBounds3.y + pathBounds3.height > n3) {
                            i = n2;
                        }
                    }
                    if (pathBounds3 == null) {
                        return;
                    }
                    this.tree.scrollRectToVisible(new Rectangle(visibleRect.x, y, 1, pathBounds3.y + pathBounds3.height - y));
                }
            }
        }
    }
    
    @Override
    protected TreeCellRenderer createDefaultCellRenderer() {
        return new WindowsTreeCellRenderer();
    }
    
    public static class ExpandedIcon implements Icon, Serializable
    {
        public static Icon createExpandedIcon() {
            return new ExpandedIcon();
        }
        
        XPStyle.Skin getSkin(final Component component) {
            final XPStyle xp = XPStyle.getXP();
            return (xp != null) ? xp.getSkin(component, TMSchema.Part.TVP_GLYPH) : null;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final XPStyle.Skin skin = this.getSkin(component);
            if (skin != null) {
                skin.paintSkin(graphics, n, n2, TMSchema.State.OPENED);
                return;
            }
            final Color background = component.getBackground();
            if (background != null) {
                graphics.setColor(background);
            }
            else {
                graphics.setColor(Color.white);
            }
            graphics.fillRect(n, n2, 8, 8);
            graphics.setColor(Color.gray);
            graphics.drawRect(n, n2, 8, 8);
            graphics.setColor(Color.black);
            graphics.drawLine(n + 2, n2 + 4, n + 6, n2 + 4);
        }
        
        @Override
        public int getIconWidth() {
            final XPStyle.Skin skin = this.getSkin(null);
            return (skin != null) ? skin.getWidth() : 9;
        }
        
        @Override
        public int getIconHeight() {
            final XPStyle.Skin skin = this.getSkin(null);
            return (skin != null) ? skin.getHeight() : 9;
        }
    }
    
    public static class CollapsedIcon extends ExpandedIcon
    {
        public static Icon createCollapsedIcon() {
            return new CollapsedIcon();
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final XPStyle.Skin skin = this.getSkin(component);
            if (skin != null) {
                skin.paintSkin(graphics, n, n2, TMSchema.State.CLOSED);
            }
            else {
                super.paintIcon(component, graphics, n, n2);
                graphics.drawLine(n + 4, n2 + 2, n + 4, n2 + 6);
            }
        }
    }
    
    public class WindowsTreeCellRenderer extends DefaultTreeCellRenderer
    {
        @Override
        public Component getTreeCellRendererComponent(final JTree tree, final Object o, final boolean b, final boolean b2, final boolean b3, final int n, final boolean b4) {
            super.getTreeCellRendererComponent(tree, o, b, b2, b3, n, b4);
            if (!tree.isEnabled()) {
                this.setEnabled(false);
                if (b3) {
                    this.setDisabledIcon(this.getLeafIcon());
                }
                else if (b) {
                    this.setDisabledIcon(this.getOpenIcon());
                }
                else {
                    this.setDisabledIcon(this.getClosedIcon());
                }
            }
            else {
                this.setEnabled(true);
                if (b3) {
                    this.setIcon(this.getLeafIcon());
                }
                else if (b) {
                    this.setIcon(this.getOpenIcon());
                }
                else {
                    this.setIcon(this.getClosedIcon());
                }
            }
            return this;
        }
    }
}
