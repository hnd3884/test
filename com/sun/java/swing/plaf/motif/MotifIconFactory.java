package com.sun.java.swing.plaf.motif;

import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.AbstractButton;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.plaf.UIResource;
import javax.swing.Icon;
import java.io.Serializable;

public class MotifIconFactory implements Serializable
{
    private static Icon checkBoxIcon;
    private static Icon radioButtonIcon;
    private static Icon menuItemCheckIcon;
    private static Icon menuItemArrowIcon;
    private static Icon menuArrowIcon;
    
    public static Icon getMenuItemCheckIcon() {
        return null;
    }
    
    public static Icon getMenuItemArrowIcon() {
        if (MotifIconFactory.menuItemArrowIcon == null) {
            MotifIconFactory.menuItemArrowIcon = new MenuItemArrowIcon();
        }
        return MotifIconFactory.menuItemArrowIcon;
    }
    
    public static Icon getMenuArrowIcon() {
        if (MotifIconFactory.menuArrowIcon == null) {
            MotifIconFactory.menuArrowIcon = new MenuArrowIcon();
        }
        return MotifIconFactory.menuArrowIcon;
    }
    
    public static Icon getCheckBoxIcon() {
        if (MotifIconFactory.checkBoxIcon == null) {
            MotifIconFactory.checkBoxIcon = new CheckBoxIcon();
        }
        return MotifIconFactory.checkBoxIcon;
    }
    
    public static Icon getRadioButtonIcon() {
        if (MotifIconFactory.radioButtonIcon == null) {
            MotifIconFactory.radioButtonIcon = new RadioButtonIcon();
        }
        return MotifIconFactory.radioButtonIcon;
    }
    
    private static class CheckBoxIcon implements Icon, UIResource, Serializable
    {
        static final int csize = 13;
        private Color control;
        private Color foreground;
        private Color shadow;
        private Color highlight;
        private Color lightShadow;
        
        private CheckBoxIcon() {
            this.control = UIManager.getColor("control");
            this.foreground = UIManager.getColor("CheckBox.foreground");
            this.shadow = UIManager.getColor("controlShadow");
            this.highlight = UIManager.getColor("controlHighlight");
            this.lightShadow = UIManager.getColor("controlLightShadow");
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final AbstractButton abstractButton = (AbstractButton)component;
            final ButtonModel model = abstractButton.getModel();
            boolean borderPaintedFlat = false;
            if (abstractButton instanceof JCheckBox) {
                borderPaintedFlat = ((JCheckBox)abstractButton).isBorderPaintedFlat();
            }
            final boolean pressed = model.isPressed();
            final boolean armed = model.isArmed();
            model.isEnabled();
            final boolean selected = model.isSelected();
            final boolean b = (pressed && !armed && selected) || (pressed && armed && !selected);
            final boolean b2 = (pressed && !armed && !selected) || (pressed && armed && selected);
            final boolean b3 = (!pressed && armed && selected) || (!pressed && !armed && selected);
            if (borderPaintedFlat) {
                graphics.setColor(this.shadow);
                graphics.drawRect(n + 2, n2, 12, 12);
                if (b2 || b) {
                    graphics.setColor(this.control);
                    graphics.fillRect(n + 3, n2 + 1, 11, 11);
                }
            }
            if (b) {
                this.drawCheckBezel(graphics, n, n2, 13, true, false, false, borderPaintedFlat);
            }
            else if (b2) {
                this.drawCheckBezel(graphics, n, n2, 13, true, true, false, borderPaintedFlat);
            }
            else if (b3) {
                this.drawCheckBezel(graphics, n, n2, 13, false, false, true, borderPaintedFlat);
            }
            else if (!borderPaintedFlat) {
                this.drawCheckBezelOut(graphics, n, n2, 13);
            }
        }
        
        @Override
        public int getIconWidth() {
            return 13;
        }
        
        @Override
        public int getIconHeight() {
            return 13;
        }
        
        public void drawCheckBezelOut(final Graphics graphics, final int n, final int n2, final int n3) {
            UIManager.getColor("controlShadow");
            final Color color = graphics.getColor();
            graphics.translate(n, n2);
            graphics.setColor(this.highlight);
            graphics.drawLine(0, 0, 0, n3 - 1);
            graphics.drawLine(1, 0, n3 - 1, 0);
            graphics.setColor(this.shadow);
            graphics.drawLine(1, n3 - 1, n3 - 1, n3 - 1);
            graphics.drawLine(n3 - 1, n3 - 1, n3 - 1, 1);
            graphics.translate(-n, -n2);
            graphics.setColor(color);
        }
        
        public void drawCheckBezel(final Graphics graphics, final int n, final int n2, final int n3, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
            final Color color = graphics.getColor();
            graphics.translate(n, n2);
            if (!b4) {
                if (b2) {
                    graphics.setColor(this.control);
                    graphics.fillRect(1, 1, n3 - 2, n3 - 2);
                    graphics.setColor(this.shadow);
                }
                else {
                    graphics.setColor(this.lightShadow);
                    graphics.fillRect(0, 0, n3, n3);
                    graphics.setColor(this.highlight);
                }
                graphics.drawLine(1, n3 - 1, n3 - 2, n3 - 1);
                if (b) {
                    graphics.drawLine(2, n3 - 2, n3 - 3, n3 - 2);
                    graphics.drawLine(n3 - 2, 2, n3 - 2, n3 - 1);
                    if (b2) {
                        graphics.setColor(this.highlight);
                    }
                    else {
                        graphics.setColor(this.shadow);
                    }
                    graphics.drawLine(1, 2, 1, n3 - 2);
                    graphics.drawLine(1, 1, n3 - 3, 1);
                    if (b2) {
                        graphics.setColor(this.shadow);
                    }
                    else {
                        graphics.setColor(this.highlight);
                    }
                }
                graphics.drawLine(n3 - 1, 1, n3 - 1, n3 - 1);
                if (b2) {
                    graphics.setColor(this.highlight);
                }
                else {
                    graphics.setColor(this.shadow);
                }
                graphics.drawLine(0, 1, 0, n3 - 1);
                graphics.drawLine(0, 0, n3 - 1, 0);
            }
            if (b3) {
                graphics.setColor(this.foreground);
                graphics.drawLine(n3 - 2, 1, n3 - 2, 2);
                graphics.drawLine(n3 - 3, 2, n3 - 3, 3);
                graphics.drawLine(n3 - 4, 3, n3 - 4, 4);
                graphics.drawLine(n3 - 5, 4, n3 - 5, 6);
                graphics.drawLine(n3 - 6, 5, n3 - 6, 8);
                graphics.drawLine(n3 - 7, 6, n3 - 7, 10);
                graphics.drawLine(n3 - 8, 7, n3 - 8, 10);
                graphics.drawLine(n3 - 9, 6, n3 - 9, 9);
                graphics.drawLine(n3 - 10, 5, n3 - 10, 8);
                graphics.drawLine(n3 - 11, 5, n3 - 11, 7);
                graphics.drawLine(n3 - 12, 6, n3 - 12, 6);
            }
            graphics.translate(-n, -n2);
            graphics.setColor(color);
        }
    }
    
    private static class RadioButtonIcon implements Icon, UIResource, Serializable
    {
        private Color dot;
        private Color highlight;
        private Color shadow;
        
        private RadioButtonIcon() {
            this.dot = UIManager.getColor("activeCaptionBorder");
            this.highlight = UIManager.getColor("controlHighlight");
            this.shadow = UIManager.getColor("controlShadow");
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final ButtonModel model = ((AbstractButton)component).getModel();
            this.getIconWidth();
            this.getIconHeight();
            final boolean pressed = model.isPressed();
            final boolean armed = model.isArmed();
            model.isEnabled();
            final boolean selected = model.isSelected();
            if ((pressed && !armed && selected) || (pressed && armed && !selected) || (!pressed && armed && selected) || (!pressed && !armed && selected)) {
                graphics.setColor(this.shadow);
                graphics.drawLine(n + 5, n2 + 0, n + 8, n2 + 0);
                graphics.drawLine(n + 3, n2 + 1, n + 4, n2 + 1);
                graphics.drawLine(n + 9, n2 + 1, n + 9, n2 + 1);
                graphics.drawLine(n + 2, n2 + 2, n + 2, n2 + 2);
                graphics.drawLine(n + 1, n2 + 3, n + 1, n2 + 3);
                graphics.drawLine(n, n2 + 4, n, n2 + 9);
                graphics.drawLine(n + 1, n2 + 10, n + 1, n2 + 10);
                graphics.drawLine(n + 2, n2 + 11, n + 2, n2 + 11);
                graphics.setColor(this.highlight);
                graphics.drawLine(n + 3, n2 + 12, n + 4, n2 + 12);
                graphics.drawLine(n + 5, n2 + 13, n + 8, n2 + 13);
                graphics.drawLine(n + 9, n2 + 12, n + 10, n2 + 12);
                graphics.drawLine(n + 11, n2 + 11, n + 11, n2 + 11);
                graphics.drawLine(n + 12, n2 + 10, n + 12, n2 + 10);
                graphics.drawLine(n + 13, n2 + 9, n + 13, n2 + 4);
                graphics.drawLine(n + 12, n2 + 3, n + 12, n2 + 3);
                graphics.drawLine(n + 11, n2 + 2, n + 11, n2 + 2);
                graphics.drawLine(n + 10, n2 + 1, n + 10, n2 + 1);
                graphics.setColor(this.dot);
                graphics.fillRect(n + 4, n2 + 5, 6, 4);
                graphics.drawLine(n + 5, n2 + 4, n + 8, n2 + 4);
                graphics.drawLine(n + 5, n2 + 9, n + 8, n2 + 9);
            }
            else {
                graphics.setColor(this.highlight);
                graphics.drawLine(n + 5, n2 + 0, n + 8, n2 + 0);
                graphics.drawLine(n + 3, n2 + 1, n + 4, n2 + 1);
                graphics.drawLine(n + 9, n2 + 1, n + 9, n2 + 1);
                graphics.drawLine(n + 2, n2 + 2, n + 2, n2 + 2);
                graphics.drawLine(n + 1, n2 + 3, n + 1, n2 + 3);
                graphics.drawLine(n, n2 + 4, n, n2 + 9);
                graphics.drawLine(n + 1, n2 + 10, n + 1, n2 + 10);
                graphics.drawLine(n + 2, n2 + 11, n + 2, n2 + 11);
                graphics.setColor(this.shadow);
                graphics.drawLine(n + 3, n2 + 12, n + 4, n2 + 12);
                graphics.drawLine(n + 5, n2 + 13, n + 8, n2 + 13);
                graphics.drawLine(n + 9, n2 + 12, n + 10, n2 + 12);
                graphics.drawLine(n + 11, n2 + 11, n + 11, n2 + 11);
                graphics.drawLine(n + 12, n2 + 10, n + 12, n2 + 10);
                graphics.drawLine(n + 13, n2 + 9, n + 13, n2 + 4);
                graphics.drawLine(n + 12, n2 + 3, n + 12, n2 + 3);
                graphics.drawLine(n + 11, n2 + 2, n + 11, n2 + 2);
                graphics.drawLine(n + 10, n2 + 1, n + 10, n2 + 1);
            }
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
    
    private static class MenuItemCheckIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
        }
        
        @Override
        public int getIconWidth() {
            return 0;
        }
        
        @Override
        public int getIconHeight() {
            return 0;
        }
    }
    
    private static class MenuItemArrowIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
        }
        
        @Override
        public int getIconWidth() {
            return 0;
        }
        
        @Override
        public int getIconHeight() {
            return 0;
        }
    }
    
    private static class MenuArrowIcon implements Icon, UIResource, Serializable
    {
        private Color focus;
        private Color shadow;
        private Color highlight;
        
        private MenuArrowIcon() {
            this.focus = UIManager.getColor("windowBorder");
            this.shadow = UIManager.getColor("controlShadow");
            this.highlight = UIManager.getColor("controlHighlight");
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final ButtonModel model = ((AbstractButton)component).getModel();
            this.getIconWidth();
            final int iconHeight = this.getIconHeight();
            final Color color = graphics.getColor();
            if (model.isSelected()) {
                if (MotifGraphicsUtils.isLeftToRight(component)) {
                    graphics.setColor(this.shadow);
                    graphics.fillRect(n + 1, n2 + 1, 2, iconHeight);
                    graphics.drawLine(n + 4, n2 + 2, n + 4, n2 + 2);
                    graphics.drawLine(n + 6, n2 + 3, n + 6, n2 + 3);
                    graphics.drawLine(n + 8, n2 + 4, n + 8, n2 + 5);
                    graphics.setColor(this.focus);
                    graphics.fillRect(n + 2, n2 + 2, 2, iconHeight - 2);
                    graphics.fillRect(n + 4, n2 + 3, 2, iconHeight - 4);
                    graphics.fillRect(n + 6, n2 + 4, 2, iconHeight - 6);
                    graphics.setColor(this.highlight);
                    graphics.drawLine(n + 2, n2 + iconHeight, n + 2, n2 + iconHeight);
                    graphics.drawLine(n + 4, n2 + iconHeight - 1, n + 4, n2 + iconHeight - 1);
                    graphics.drawLine(n + 6, n2 + iconHeight - 2, n + 6, n2 + iconHeight - 2);
                    graphics.drawLine(n + 8, n2 + iconHeight - 4, n + 8, n2 + iconHeight - 3);
                }
                else {
                    graphics.setColor(this.highlight);
                    graphics.fillRect(n + 7, n2 + 1, 2, 10);
                    graphics.drawLine(n + 5, n2 + 9, n + 5, n2 + 9);
                    graphics.drawLine(n + 3, n2 + 8, n + 3, n2 + 8);
                    graphics.drawLine(n + 1, n2 + 6, n + 1, n2 + 7);
                    graphics.setColor(this.focus);
                    graphics.fillRect(n + 6, n2 + 2, 2, 8);
                    graphics.fillRect(n + 4, n2 + 3, 2, 6);
                    graphics.fillRect(n + 2, n2 + 4, 2, 4);
                    graphics.setColor(this.shadow);
                    graphics.drawLine(n + 1, n2 + 4, n + 1, n2 + 5);
                    graphics.drawLine(n + 3, n2 + 3, n + 3, n2 + 3);
                    graphics.drawLine(n + 5, n2 + 2, n + 5, n2 + 2);
                    graphics.drawLine(n + 7, n2 + 1, n + 7, n2 + 1);
                }
            }
            else if (MotifGraphicsUtils.isLeftToRight(component)) {
                graphics.setColor(this.highlight);
                graphics.drawLine(n + 1, n2 + 1, n + 1, n2 + iconHeight);
                graphics.drawLine(n + 2, n2 + 1, n + 2, n2 + iconHeight - 2);
                graphics.fillRect(n + 3, n2 + 2, 2, 2);
                graphics.fillRect(n + 5, n2 + 3, 2, 2);
                graphics.fillRect(n + 7, n2 + 4, 2, 2);
                graphics.setColor(this.shadow);
                graphics.drawLine(n + 2, n2 + iconHeight - 1, n + 2, n2 + iconHeight);
                graphics.fillRect(n + 3, n2 + iconHeight - 2, 2, 2);
                graphics.fillRect(n + 5, n2 + iconHeight - 3, 2, 2);
                graphics.fillRect(n + 7, n2 + iconHeight - 4, 2, 2);
                graphics.setColor(color);
            }
            else {
                graphics.setColor(this.highlight);
                graphics.fillRect(n + 1, n2 + 4, 2, 2);
                graphics.fillRect(n + 3, n2 + 3, 2, 2);
                graphics.fillRect(n + 5, n2 + 2, 2, 2);
                graphics.drawLine(n + 7, n2 + 1, n + 7, n2 + 2);
                graphics.setColor(this.shadow);
                graphics.fillRect(n + 1, n2 + iconHeight - 4, 2, 2);
                graphics.fillRect(n + 3, n2 + iconHeight - 3, 2, 2);
                graphics.fillRect(n + 5, n2 + iconHeight - 2, 2, 2);
                graphics.drawLine(n + 7, n2 + 3, n + 7, n2 + iconHeight);
                graphics.drawLine(n + 8, n2 + 1, n + 8, n2 + iconHeight);
                graphics.setColor(color);
            }
        }
        
        @Override
        public int getIconWidth() {
            return 10;
        }
        
        @Override
        public int getIconHeight() {
            return 10;
        }
    }
}
