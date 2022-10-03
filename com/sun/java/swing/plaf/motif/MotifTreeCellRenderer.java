package com.sun.java.swing.plaf.motif;

import java.awt.Graphics;
import java.awt.Component;
import javax.swing.UIManager;
import java.awt.Color;
import java.io.Serializable;
import javax.swing.plaf.IconUIResource;
import javax.swing.Icon;
import javax.swing.tree.DefaultTreeCellRenderer;

public class MotifTreeCellRenderer extends DefaultTreeCellRenderer
{
    static final int LEAF_SIZE = 13;
    static final Icon LEAF_ICON;
    
    public static Icon loadLeafIcon() {
        return MotifTreeCellRenderer.LEAF_ICON;
    }
    
    static {
        LEAF_ICON = new IconUIResource(new TreeLeafIcon());
    }
    
    public static class TreeLeafIcon implements Icon, Serializable
    {
        Color bg;
        Color shadow;
        Color highlight;
        
        public TreeLeafIcon() {
            this.bg = UIManager.getColor("Tree.iconBackground");
            this.shadow = UIManager.getColor("Tree.iconShadow");
            this.highlight = UIManager.getColor("Tree.iconHighlight");
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, int n2) {
            graphics.setColor(this.bg);
            n2 -= 3;
            graphics.fillRect(n + 4, n2 + 7, 5, 5);
            graphics.drawLine(n + 6, n2 + 6, n + 6, n2 + 6);
            graphics.drawLine(n + 3, n2 + 9, n + 3, n2 + 9);
            graphics.drawLine(n + 6, n2 + 12, n + 6, n2 + 12);
            graphics.drawLine(n + 9, n2 + 9, n + 9, n2 + 9);
            graphics.setColor(this.highlight);
            graphics.drawLine(n + 2, n2 + 9, n + 5, n2 + 6);
            graphics.drawLine(n + 3, n2 + 10, n + 5, n2 + 12);
            graphics.setColor(this.shadow);
            graphics.drawLine(n + 6, n2 + 13, n + 10, n2 + 9);
            graphics.drawLine(n + 9, n2 + 8, n + 7, n2 + 6);
        }
        
        @Override
        public int getIconWidth() {
            return 13;
        }
        
        @Override
        public int getIconHeight() {
            return 13;
        }
    }
}
