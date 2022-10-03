package com.sun.java.swing.plaf.windows;

import javax.swing.JInternalFrame;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.border.LineBorder;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Color;
import javax.swing.plaf.UIResource;
import javax.swing.border.AbstractBorder;
import javax.swing.BorderFactory;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.UIDefaults;
import javax.swing.plaf.BorderUIResource;
import javax.swing.border.EmptyBorder;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class WindowsBorders
{
    public static Border getProgressBarBorder() {
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new BorderUIResource.CompoundBorderUIResource(new ProgressBarBorder(lookAndFeelDefaults.getColor("ProgressBar.shadow"), lookAndFeelDefaults.getColor("ProgressBar.highlight")), new EmptyBorder(1, 1, 1, 1));
    }
    
    public static Border getToolBarBorder() {
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new ToolBarBorder(lookAndFeelDefaults.getColor("ToolBar.shadow"), lookAndFeelDefaults.getColor("ToolBar.highlight"));
    }
    
    public static Border getFocusCellHighlightBorder() {
        return new ComplementDashedBorder();
    }
    
    public static Border getTableHeaderBorder() {
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new BorderUIResource.CompoundBorderUIResource(new BasicBorders.ButtonBorder(lookAndFeelDefaults.getColor("Table.shadow"), lookAndFeelDefaults.getColor("Table.darkShadow"), lookAndFeelDefaults.getColor("Table.light"), lookAndFeelDefaults.getColor("Table.highlight")), new BasicBorders.MarginBorder());
    }
    
    public static Border getInternalFrameBorder() {
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        return new BorderUIResource.CompoundBorderUIResource(BorderFactory.createBevelBorder(0, lookAndFeelDefaults.getColor("InternalFrame.borderColor"), lookAndFeelDefaults.getColor("InternalFrame.borderHighlight"), lookAndFeelDefaults.getColor("InternalFrame.borderDarkShadow"), lookAndFeelDefaults.getColor("InternalFrame.borderShadow")), new InternalFrameLineBorder(lookAndFeelDefaults.getColor("InternalFrame.activeBorderColor"), lookAndFeelDefaults.getColor("InternalFrame.inactiveBorderColor"), lookAndFeelDefaults.getInt("InternalFrame.borderWidth")));
    }
    
    public static class ProgressBarBorder extends AbstractBorder implements UIResource
    {
        protected Color shadow;
        protected Color highlight;
        
        public ProgressBarBorder(final Color shadow, final Color highlight) {
            this.highlight = highlight;
            this.shadow = shadow;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            graphics.setColor(this.shadow);
            graphics.drawLine(n, n2, n3 - 1, n2);
            graphics.drawLine(n, n2, n, n4 - 1);
            graphics.setColor(this.highlight);
            graphics.drawLine(n, n4 - 1, n3 - 1, n4 - 1);
            graphics.drawLine(n3 - 1, n2, n3 - 1, n4 - 1);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(1, 1, 1, 1);
            return insets;
        }
    }
    
    public static class ToolBarBorder extends AbstractBorder implements UIResource, SwingConstants
    {
        protected Color shadow;
        protected Color highlight;
        
        public ToolBarBorder(final Color shadow, final Color highlight) {
            this.highlight = highlight;
            this.shadow = shadow;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!(component instanceof JToolBar)) {
                return;
            }
            graphics.translate(n, n2);
            final XPStyle xp = XPStyle.getXP();
            if (xp != null) {
                final Border border = xp.getBorder(component, TMSchema.Part.TP_TOOLBAR);
                if (border != null) {
                    border.paintBorder(component, graphics, 0, 0, n3, n4);
                }
            }
            if (((JToolBar)component).isFloatable()) {
                final boolean b = ((JToolBar)component).getOrientation() == 1;
                if (xp != null) {
                    final XPStyle.Skin skin = xp.getSkin(component, b ? TMSchema.Part.RP_GRIPPERVERT : TMSchema.Part.RP_GRIPPER);
                    int n5;
                    int n6;
                    int width;
                    int height;
                    if (b) {
                        n5 = 0;
                        n6 = 2;
                        width = n3 - 1;
                        height = skin.getHeight();
                    }
                    else {
                        width = skin.getWidth();
                        height = n4 - 1;
                        n5 = (component.getComponentOrientation().isLeftToRight() ? 2 : (n3 - width - 2));
                        n6 = 0;
                    }
                    skin.paintSkin(graphics, n5, n6, width, height, TMSchema.State.NORMAL);
                }
                else if (!b) {
                    if (component.getComponentOrientation().isLeftToRight()) {
                        graphics.setColor(this.shadow);
                        graphics.drawLine(4, 3, 4, n4 - 4);
                        graphics.drawLine(4, n4 - 4, 2, n4 - 4);
                        graphics.setColor(this.highlight);
                        graphics.drawLine(2, 3, 3, 3);
                        graphics.drawLine(2, 3, 2, n4 - 5);
                    }
                    else {
                        graphics.setColor(this.shadow);
                        graphics.drawLine(n3 - 3, 3, n3 - 3, n4 - 4);
                        graphics.drawLine(n3 - 4, n4 - 4, n3 - 4, n4 - 4);
                        graphics.setColor(this.highlight);
                        graphics.drawLine(n3 - 5, 3, n3 - 4, 3);
                        graphics.drawLine(n3 - 5, 3, n3 - 5, n4 - 5);
                    }
                }
                else {
                    graphics.setColor(this.shadow);
                    graphics.drawLine(3, 4, n3 - 4, 4);
                    graphics.drawLine(n3 - 4, 2, n3 - 4, 4);
                    graphics.setColor(this.highlight);
                    graphics.drawLine(3, 2, n3 - 4, 2);
                    graphics.drawLine(3, 2, 3, 3);
                }
            }
            graphics.translate(-n, -n2);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(1, 1, 1, 1);
            if (!(component instanceof JToolBar)) {
                return insets;
            }
            if (((JToolBar)component).isFloatable()) {
                final int top = (XPStyle.getXP() != null) ? 12 : 9;
                if (((JToolBar)component).getOrientation() == 0) {
                    if (component.getComponentOrientation().isLeftToRight()) {
                        insets.left = top;
                    }
                    else {
                        insets.right = top;
                    }
                }
                else {
                    insets.top = top;
                }
            }
            return insets;
        }
    }
    
    public static class DashedBorder extends LineBorder implements UIResource
    {
        public DashedBorder(final Color color) {
            super(color);
        }
        
        public DashedBorder(final Color color, final int n) {
            super(color, n);
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Color color = graphics.getColor();
            graphics.setColor(this.lineColor);
            for (int i = 0; i < this.thickness; ++i) {
                BasicGraphicsUtils.drawDashedRect(graphics, n + i, n2 + i, n3 - i - i, n4 - i - i);
            }
            graphics.setColor(color);
        }
    }
    
    static class ComplementDashedBorder extends LineBorder implements UIResource
    {
        private Color origColor;
        private Color paintColor;
        
        public ComplementDashedBorder() {
            super(null);
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final Color background = component.getBackground();
            if (this.origColor != background) {
                this.origColor = background;
                this.paintColor = new Color(~this.origColor.getRGB());
            }
            graphics.setColor(this.paintColor);
            BasicGraphicsUtils.drawDashedRect(graphics, n, n2, n3, n4);
        }
    }
    
    public static class InternalFrameLineBorder extends LineBorder implements UIResource
    {
        protected Color activeColor;
        protected Color inactiveColor;
        
        public InternalFrameLineBorder(final Color activeColor, final Color inactiveColor, final int n) {
            super(activeColor, n);
            this.activeColor = activeColor;
            this.inactiveColor = inactiveColor;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            JInternalFrame internalFrame;
            if (component instanceof JInternalFrame) {
                internalFrame = (JInternalFrame)component;
            }
            else {
                if (!(component instanceof JInternalFrame.JDesktopIcon)) {
                    return;
                }
                internalFrame = ((JInternalFrame.JDesktopIcon)component).getInternalFrame();
            }
            if (internalFrame.isSelected()) {
                this.lineColor = this.activeColor;
                super.paintBorder(component, graphics, n, n2, n3, n4);
            }
            else {
                this.lineColor = this.inactiveColor;
                super.paintBorder(component, graphics, n, n2, n3, n4);
            }
        }
    }
}
