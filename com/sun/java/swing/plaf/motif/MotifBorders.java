package com.sun.java.swing.plaf.motif;

import java.awt.FontMetrics;
import java.awt.Point;
import sun.swing.SwingUtilities2;
import javax.swing.JPopupMenu;
import java.awt.Font;
import javax.swing.JInternalFrame;
import java.awt.Rectangle;
import javax.swing.JComponent;
import java.awt.Dimension;
import javax.swing.JMenuBar;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.AbstractButton;
import java.awt.Insets;
import java.awt.Component;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.border.AbstractBorder;
import java.awt.Color;
import java.awt.Graphics;

public class MotifBorders
{
    public static void drawBezel(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final boolean b, final boolean b2, final Color color, final Color color2, final Color color3, final Color color4) {
        final Color color5 = graphics.getColor();
        graphics.translate(n, n2);
        if (b) {
            if (b2) {
                graphics.setColor(color4);
                graphics.drawRect(0, 0, n3 - 1, n4 - 1);
            }
            graphics.setColor(color);
            graphics.drawRect(1, 1, n3 - 3, n4 - 3);
            graphics.setColor(color2);
            graphics.drawLine(2, n4 - 3, n3 - 3, n4 - 3);
            graphics.drawLine(n3 - 3, 2, n3 - 3, n4 - 4);
        }
        else {
            if (b2) {
                graphics.setColor(color4);
                graphics.drawRect(0, 0, n3 - 1, n4 - 1);
                graphics.setColor(color2);
                graphics.drawLine(1, 1, 1, n4 - 3);
                graphics.drawLine(2, 1, n3 - 4, 1);
                graphics.setColor(color);
                graphics.drawLine(2, n4 - 3, n3 - 3, n4 - 3);
                graphics.drawLine(n3 - 3, 1, n3 - 3, n4 - 4);
                graphics.setColor(color3);
                graphics.drawLine(1, n4 - 2, n3 - 2, n4 - 2);
                graphics.drawLine(n3 - 2, n4 - 2, n3 - 2, 1);
            }
            else {
                graphics.setColor(color2);
                graphics.drawLine(1, 1, 1, n4 - 3);
                graphics.drawLine(2, 1, n3 - 4, 1);
                graphics.setColor(color);
                graphics.drawLine(2, n4 - 3, n3 - 3, n4 - 3);
                graphics.drawLine(n3 - 3, 1, n3 - 3, n4 - 4);
                graphics.setColor(color3);
                graphics.drawLine(1, n4 - 2, n3 - 2, n4 - 2);
                graphics.drawLine(n3 - 2, n4 - 2, n3 - 2, 0);
            }
            graphics.translate(-n, -n2);
        }
        graphics.setColor(color5);
    }
    
    public static class BevelBorder extends AbstractBorder implements UIResource
    {
        private Color darkShadow;
        private Color lightShadow;
        private boolean isRaised;
        
        public BevelBorder(final boolean isRaised, final Color darkShadow, final Color lightShadow) {
            this.darkShadow = UIManager.getColor("controlShadow");
            this.lightShadow = UIManager.getColor("controlLtHighlight");
            this.isRaised = isRaised;
            this.darkShadow = darkShadow;
            this.lightShadow = lightShadow;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            graphics.setColor(this.isRaised ? this.lightShadow : this.darkShadow);
            graphics.drawLine(n, n2, n + n3 - 1, n2);
            graphics.drawLine(n, n2 + n4 - 1, n, n2 + 1);
            graphics.setColor(this.isRaised ? this.darkShadow : this.lightShadow);
            graphics.drawLine(n + 1, n2 + n4 - 1, n + n3 - 1, n2 + n4 - 1);
            graphics.drawLine(n + n3 - 1, n2 + n4 - 1, n + n3 - 1, n2 + 1);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(1, 1, 1, 1);
            return insets;
        }
        
        public boolean isOpaque(final Component component) {
            return true;
        }
    }
    
    public static class FocusBorder extends AbstractBorder implements UIResource
    {
        private Color focus;
        private Color control;
        
        public FocusBorder(final Color control, final Color focus) {
            this.control = control;
            this.focus = focus;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (component.hasFocus()) {
                graphics.setColor(this.focus);
                graphics.drawRect(n, n2, n3 - 1, n4 - 1);
            }
            else {
                graphics.setColor(this.control);
                graphics.drawRect(n, n2, n3 - 1, n4 - 1);
            }
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(1, 1, 1, 1);
            return insets;
        }
    }
    
    public static class ButtonBorder extends AbstractBorder implements UIResource
    {
        protected Color focus;
        protected Color shadow;
        protected Color highlight;
        protected Color darkShadow;
        
        public ButtonBorder(final Color shadow, final Color highlight, final Color darkShadow, final Color focus) {
            this.focus = UIManager.getColor("activeCaptionBorder");
            this.shadow = UIManager.getColor("Button.shadow");
            this.highlight = UIManager.getColor("Button.light");
            this.shadow = shadow;
            this.highlight = highlight;
            this.darkShadow = darkShadow;
            this.focus = focus;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            boolean b = false;
            int n5 = 0;
            boolean defaultCapable = false;
            boolean defaultButton = false;
            if (component instanceof AbstractButton) {
                final AbstractButton abstractButton = (AbstractButton)component;
                final ButtonModel model = abstractButton.getModel();
                b = (model.isArmed() && model.isPressed());
                n5 = (((model.isArmed() && b) || (abstractButton.isFocusPainted() && abstractButton.hasFocus())) ? 1 : 0);
                if (abstractButton instanceof JButton) {
                    defaultCapable = ((JButton)abstractButton).isDefaultCapable();
                    defaultButton = ((JButton)abstractButton).isDefaultButton();
                }
            }
            int n6 = n + 1;
            int n7 = n2 + 1;
            int n8 = n + n3 - 2;
            int n9 = n2 + n4 - 2;
            if (defaultCapable) {
                if (defaultButton) {
                    graphics.setColor(this.shadow);
                    graphics.drawLine(n + 3, n2 + 3, n + 3, n2 + n4 - 4);
                    graphics.drawLine(n + 3, n2 + 3, n + n3 - 4, n2 + 3);
                    graphics.setColor(this.highlight);
                    graphics.drawLine(n + 4, n2 + n4 - 4, n + n3 - 4, n2 + n4 - 4);
                    graphics.drawLine(n + n3 - 4, n2 + 3, n + n3 - 4, n2 + n4 - 4);
                }
                n6 += 6;
                n7 += 6;
                n8 -= 6;
                n9 -= 6;
            }
            if (n5 != 0) {
                graphics.setColor(this.focus);
                if (defaultButton) {
                    graphics.drawRect(n, n2, n3 - 1, n4 - 1);
                }
                else {
                    graphics.drawRect(n6 - 1, n7 - 1, n8 - n6 + 2, n9 - n7 + 2);
                }
            }
            graphics.setColor(b ? this.shadow : this.highlight);
            graphics.drawLine(n6, n7, n8, n7);
            graphics.drawLine(n6, n7, n6, n9);
            graphics.setColor(b ? this.highlight : this.shadow);
            graphics.drawLine(n8, n7 + 1, n8, n9);
            graphics.drawLine(n6 + 1, n9, n8, n9);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            final int n = (component instanceof JButton && ((JButton)component).isDefaultCapable()) ? 8 : 2;
            insets.set(n, n, n, n);
            return insets;
        }
    }
    
    public static class ToggleButtonBorder extends ButtonBorder
    {
        public ToggleButtonBorder(final Color color, final Color color2, final Color color3, final Color color4) {
            super(color, color2, color3, color4);
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (component instanceof AbstractButton) {
                final AbstractButton abstractButton = (AbstractButton)component;
                final ButtonModel model = abstractButton.getModel();
                if ((model.isArmed() && model.isPressed()) || model.isSelected()) {
                    MotifBorders.drawBezel(graphics, n, n2, n3, n4, model.isPressed() || model.isSelected(), abstractButton.isFocusPainted() && abstractButton.hasFocus(), this.shadow, this.highlight, this.darkShadow, this.focus);
                }
                else {
                    MotifBorders.drawBezel(graphics, n, n2, n3, n4, false, abstractButton.isFocusPainted() && abstractButton.hasFocus(), this.shadow, this.highlight, this.darkShadow, this.focus);
                }
            }
            else {
                MotifBorders.drawBezel(graphics, n, n2, n3, n4, false, false, this.shadow, this.highlight, this.darkShadow, this.focus);
            }
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(2, 2, 3, 3);
            return insets;
        }
    }
    
    public static class MenuBarBorder extends ButtonBorder
    {
        public MenuBarBorder(final Color color, final Color color2, final Color color3, final Color color4) {
            super(color, color2, color3, color4);
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!(component instanceof JMenuBar)) {
                return;
            }
            final JMenuBar menuBar = (JMenuBar)component;
            if (menuBar.isBorderPainted()) {
                final Dimension size = menuBar.getSize();
                MotifBorders.drawBezel(graphics, n, n2, size.width, size.height, false, false, this.shadow, this.highlight, this.darkShadow, this.focus);
            }
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(6, 6, 6, 6);
            return insets;
        }
    }
    
    public static class FrameBorder extends AbstractBorder implements UIResource
    {
        JComponent jcomp;
        Color frameHighlight;
        Color frameColor;
        Color frameShadow;
        public static final int BORDER_SIZE = 5;
        
        public FrameBorder(final JComponent jcomp) {
            this.jcomp = jcomp;
        }
        
        public void setComponent(final JComponent jcomp) {
            this.jcomp = jcomp;
        }
        
        public JComponent component() {
            return this.jcomp;
        }
        
        protected Color getFrameHighlight() {
            return this.frameHighlight;
        }
        
        protected Color getFrameColor() {
            return this.frameColor;
        }
        
        protected Color getFrameShadow() {
            return this.frameShadow;
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.set(5, 5, 5, 5);
            return insets;
        }
        
        protected boolean drawTopBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!graphics.getClipBounds().intersects(new Rectangle(n, n2, n3, 5))) {
                return false;
            }
            final int n5 = n3 - 1;
            final int n6 = 4;
            graphics.setColor(this.frameColor);
            graphics.drawLine(n, n2 + 2, n5 - 2, n2 + 2);
            graphics.drawLine(n, n2 + 3, n5 - 2, n2 + 3);
            graphics.drawLine(n, n2 + 4, n5 - 2, n2 + 4);
            graphics.setColor(this.frameHighlight);
            graphics.drawLine(n, n2, n5, n2);
            graphics.drawLine(n, n2 + 1, n5, n2 + 1);
            graphics.drawLine(n, n2 + 2, n, n2 + 4);
            graphics.drawLine(n + 1, n2 + 2, n + 1, n2 + 4);
            graphics.setColor(this.frameShadow);
            graphics.drawLine(n + 4, n2 + 4, n5 - 4, n2 + 4);
            graphics.drawLine(n5, n2 + 1, n5, n6);
            graphics.drawLine(n5 - 1, n2 + 2, n5 - 1, n6);
            return true;
        }
        
        protected boolean drawLeftBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!graphics.getClipBounds().intersects(new Rectangle(0, 0, this.getBorderInsets(component).left, n4))) {
                return false;
            }
            final int n5 = 5;
            graphics.setColor(this.frameHighlight);
            graphics.drawLine(n, n5, n, n4 - 1);
            graphics.drawLine(n + 1, n5, n + 1, n4 - 2);
            graphics.setColor(this.frameColor);
            graphics.fillRect(n + 2, n5, n + 2, n4 - 3);
            graphics.setColor(this.frameShadow);
            graphics.drawLine(n + 4, n5, n + 4, n4 - 5);
            return true;
        }
        
        protected boolean drawRightBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!graphics.getClipBounds().intersects(new Rectangle(n3 - this.getBorderInsets(component).right, 0, this.getBorderInsets(component).right, n4))) {
                return false;
            }
            final int n5 = n3 - this.getBorderInsets(component).right;
            final int n6 = 5;
            graphics.setColor(this.frameColor);
            graphics.fillRect(n5 + 1, n6, 2, n4 - 1);
            graphics.setColor(this.frameShadow);
            graphics.fillRect(n5 + 3, n6, 2, n4 - 1);
            graphics.setColor(this.frameHighlight);
            graphics.drawLine(n5, n6, n5, n4 - 1);
            return true;
        }
        
        protected boolean drawBottomBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!graphics.getClipBounds().intersects(new Rectangle(0, n4 - this.getBorderInsets(component).bottom, n3, this.getBorderInsets(component).bottom))) {
                return false;
            }
            final int n5 = n4 - this.getBorderInsets(component).bottom;
            graphics.setColor(this.frameShadow);
            graphics.drawLine(n + 1, n4 - 1, n3 - 1, n4 - 1);
            graphics.drawLine(n + 2, n4 - 2, n3 - 2, n4 - 2);
            graphics.setColor(this.frameColor);
            graphics.fillRect(n + 2, n5 + 1, n3 - 4, 2);
            graphics.setColor(this.frameHighlight);
            graphics.drawLine(n + 5, n5, n3 - 5, n5);
            return true;
        }
        
        protected boolean isActiveFrame() {
            return this.jcomp.hasFocus();
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (this.isActiveFrame()) {
                this.frameColor = UIManager.getColor("activeCaptionBorder");
            }
            else {
                this.frameColor = UIManager.getColor("inactiveCaptionBorder");
            }
            this.frameHighlight = this.frameColor.brighter();
            this.frameShadow = this.frameColor.darker().darker();
            this.drawTopBorder(component, graphics, n, n2, n3, n4);
            this.drawLeftBorder(component, graphics, n, n2, n3, n4);
            this.drawRightBorder(component, graphics, n, n2, n3, n4);
            this.drawBottomBorder(component, graphics, n, n2, n3, n4);
        }
    }
    
    public static class InternalFrameBorder extends FrameBorder
    {
        JInternalFrame frame;
        public static final int CORNER_SIZE = 24;
        
        public InternalFrameBorder(final JInternalFrame frame) {
            super(frame);
            this.frame = frame;
        }
        
        public void setFrame(final JInternalFrame frame) {
            this.frame = frame;
        }
        
        public JInternalFrame frame() {
            return this.frame;
        }
        
        public int resizePartWidth() {
            if (!this.frame.isResizable()) {
                return 0;
            }
            return 5;
        }
        
        @Override
        protected boolean drawTopBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (super.drawTopBorder(component, graphics, n, n2, n3, n4) && this.frame.isResizable()) {
                graphics.setColor(this.getFrameShadow());
                graphics.drawLine(23, n2 + 1, 23, n2 + 4);
                graphics.drawLine(n3 - 24 - 1, n2 + 1, n3 - 24 - 1, n2 + 4);
                graphics.setColor(this.getFrameHighlight());
                graphics.drawLine(24, n2, 24, n2 + 4);
                graphics.drawLine(n3 - 24, n2, n3 - 24, n2 + 4);
                return true;
            }
            return false;
        }
        
        @Override
        protected boolean drawLeftBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (super.drawLeftBorder(component, graphics, n, n2, n3, n4) && this.frame.isResizable()) {
                graphics.setColor(this.getFrameHighlight());
                final int n5 = n2 + 24;
                graphics.drawLine(n, n5, n + 4, n5);
                final int n6 = n4 - 24;
                graphics.drawLine(n + 1, n6, n + 5, n6);
                graphics.setColor(this.getFrameShadow());
                graphics.drawLine(n + 1, n5 - 1, n + 5, n5 - 1);
                graphics.drawLine(n + 1, n6 - 1, n + 5, n6 - 1);
                return true;
            }
            return false;
        }
        
        @Override
        protected boolean drawRightBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (super.drawRightBorder(component, graphics, n, n2, n3, n4) && this.frame.isResizable()) {
                final int n5 = n3 - this.getBorderInsets(component).right;
                graphics.setColor(this.getFrameHighlight());
                final int n6 = n2 + 24;
                graphics.drawLine(n5, n6, n3 - 2, n6);
                final int n7 = n4 - 24;
                graphics.drawLine(n5 + 1, n7, n5 + 3, n7);
                graphics.setColor(this.getFrameShadow());
                graphics.drawLine(n5 + 1, n6 - 1, n3 - 2, n6 - 1);
                graphics.drawLine(n5 + 1, n7 - 1, n5 + 3, n7 - 1);
                return true;
            }
            return false;
        }
        
        @Override
        protected boolean drawBottomBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (super.drawBottomBorder(component, graphics, n, n2, n3, n4) && this.frame.isResizable()) {
                final int n5 = n4 - this.getBorderInsets(component).bottom;
                graphics.setColor(this.getFrameShadow());
                graphics.drawLine(23, n5 + 1, 23, n4 - 1);
                graphics.drawLine(n3 - 24, n5 + 1, n3 - 24, n4 - 1);
                graphics.setColor(this.getFrameHighlight());
                graphics.drawLine(24, n5, 24, n4 - 2);
                graphics.drawLine(n3 - 24 + 1, n5, n3 - 24 + 1, n4 - 2);
                return true;
            }
            return false;
        }
        
        @Override
        protected boolean isActiveFrame() {
            return this.frame.isSelected();
        }
    }
    
    public static class MotifPopupMenuBorder extends AbstractBorder implements UIResource
    {
        protected Font font;
        protected Color background;
        protected Color foreground;
        protected Color shadowColor;
        protected Color highlightColor;
        protected static final int TEXT_SPACING = 2;
        protected static final int GROOVE_HEIGHT = 2;
        
        public MotifPopupMenuBorder(final Font font, final Color background, final Color foreground, final Color shadowColor, final Color highlightColor) {
            this.font = font;
            this.background = background;
            this.foreground = foreground;
            this.shadowColor = shadowColor;
            this.highlightColor = highlightColor;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (!(component instanceof JPopupMenu)) {
                return;
            }
            final Font font = graphics.getFont();
            final Color color = graphics.getColor();
            final JPopupMenu popupMenu = (JPopupMenu)component;
            final String label = popupMenu.getLabel();
            if (label == null) {
                return;
            }
            graphics.setFont(this.font);
            final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(popupMenu, graphics, this.font);
            final int height = fontMetrics.getHeight();
            final int descent = fontMetrics.getDescent();
            final int ascent = fontMetrics.getAscent();
            final Point point = new Point();
            final int stringWidth = SwingUtilities2.stringWidth(popupMenu, fontMetrics, label);
            point.y = n2 + ascent + 2;
            point.x = n + (n3 - stringWidth) / 2;
            graphics.setColor(this.background);
            graphics.fillRect(point.x - 2, point.y - (height - descent), stringWidth + 4, height - descent);
            graphics.setColor(this.foreground);
            SwingUtilities2.drawString(popupMenu, graphics, label, point.x, point.y);
            MotifGraphicsUtils.drawGroove(graphics, n, point.y + 2, n3, 2, this.shadowColor, this.highlightColor);
            graphics.setFont(font);
            graphics.setColor(color);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            if (!(component instanceof JPopupMenu)) {
                return insets;
            }
            int descent = 0;
            int ascent = 16;
            if (((JPopupMenu)component).getLabel() == null) {
                final int n = 0;
                insets.bottom = n;
                insets.right = n;
                insets.top = n;
                insets.left = n;
                return insets;
            }
            final FontMetrics fontMetrics = component.getFontMetrics(this.font);
            if (fontMetrics != null) {
                descent = fontMetrics.getDescent();
                ascent = fontMetrics.getAscent();
            }
            insets.top += ascent + descent + 2 + 2;
            return insets;
        }
    }
}
