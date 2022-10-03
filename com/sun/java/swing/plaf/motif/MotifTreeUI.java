package com.sun.java.swing.plaf.motif;

import java.awt.Component;
import javax.swing.UIManager;
import java.awt.Color;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.plaf.ComponentUI;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTreeUI;

public class MotifTreeUI extends BasicTreeUI
{
    static final int HALF_SIZE = 7;
    static final int SIZE = 14;
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
    }
    
    @Override
    protected void paintVerticalLine(final Graphics graphics, final JComponent component, final int n, final int n2, final int n3) {
        if (this.tree.getComponentOrientation().isLeftToRight()) {
            graphics.fillRect(n, n2, 2, n3 - n2 + 2);
        }
        else {
            graphics.fillRect(n - 1, n2, 2, n3 - n2 + 2);
        }
    }
    
    @Override
    protected void paintHorizontalLine(final Graphics graphics, final JComponent component, final int n, final int n2, final int n3) {
        graphics.fillRect(n2, n, n3 - n2 + 1, 2);
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new MotifTreeUI();
    }
    
    public TreeCellRenderer createDefaultCellRenderer() {
        return new MotifTreeCellRenderer();
    }
    
    public static class MotifExpandedIcon implements Icon, Serializable
    {
        static Color bg;
        static Color fg;
        static Color highlight;
        static Color shadow;
        
        public MotifExpandedIcon() {
            MotifExpandedIcon.bg = UIManager.getColor("Tree.iconBackground");
            MotifExpandedIcon.fg = UIManager.getColor("Tree.iconForeground");
            MotifExpandedIcon.highlight = UIManager.getColor("Tree.iconHighlight");
            MotifExpandedIcon.shadow = UIManager.getColor("Tree.iconShadow");
        }
        
        public static Icon createExpandedIcon() {
            return new MotifExpandedIcon();
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            graphics.setColor(MotifExpandedIcon.highlight);
            graphics.drawLine(n, n2, n + 14 - 1, n2);
            graphics.drawLine(n, n2 + 1, n, n2 + 14 - 1);
            graphics.setColor(MotifExpandedIcon.shadow);
            graphics.drawLine(n + 14 - 1, n2 + 1, n + 14 - 1, n2 + 14 - 1);
            graphics.drawLine(n + 1, n2 + 14 - 1, n + 14 - 1, n2 + 14 - 1);
            graphics.setColor(MotifExpandedIcon.bg);
            graphics.fillRect(n + 1, n2 + 1, 12, 12);
            graphics.setColor(MotifExpandedIcon.fg);
            graphics.drawLine(n + 3, n2 + 7 - 1, n + 14 - 4, n2 + 7 - 1);
            graphics.drawLine(n + 3, n2 + 7, n + 14 - 4, n2 + 7);
        }
        
        @Override
        public int getIconWidth() {
            return 14;
        }
        
        @Override
        public int getIconHeight() {
            return 14;
        }
    }
    
    public static class MotifCollapsedIcon extends MotifExpandedIcon
    {
        public static Icon createCollapsedIcon() {
            return new MotifCollapsedIcon();
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            super.paintIcon(component, graphics, n, n2);
            graphics.drawLine(n + 7 - 1, n2 + 3, n + 7 - 1, n2 + 10);
            graphics.drawLine(n + 7, n2 + 3, n + 7, n2 + 10);
        }
    }
}
