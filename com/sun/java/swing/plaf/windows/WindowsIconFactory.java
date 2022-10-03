package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.ButtonUI;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JCheckBoxMenuItem;
import sun.swing.MenuItemCheckIconFactory;
import java.awt.Graphics2D;
import javax.swing.JMenuItem;
import javax.swing.plaf.UIResource;
import javax.swing.JCheckBox;
import java.awt.Dimension;
import javax.swing.UIManager;
import javax.swing.ButtonModel;
import java.awt.Color;
import javax.swing.SwingUtilities;
import javax.swing.JInternalFrame;
import javax.swing.AbstractButton;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.Icon;
import java.io.Serializable;

public class WindowsIconFactory implements Serializable
{
    private static Icon frame_closeIcon;
    private static Icon frame_iconifyIcon;
    private static Icon frame_maxIcon;
    private static Icon frame_minIcon;
    private static Icon frame_resizeIcon;
    private static Icon checkBoxIcon;
    private static Icon radioButtonIcon;
    private static Icon checkBoxMenuItemIcon;
    private static Icon radioButtonMenuItemIcon;
    private static Icon menuItemCheckIcon;
    private static Icon menuItemArrowIcon;
    private static Icon menuArrowIcon;
    private static VistaMenuItemCheckIconFactory menuItemCheckIconFactory;
    
    public static Icon getMenuItemCheckIcon() {
        if (WindowsIconFactory.menuItemCheckIcon == null) {
            WindowsIconFactory.menuItemCheckIcon = new MenuItemCheckIcon();
        }
        return WindowsIconFactory.menuItemCheckIcon;
    }
    
    public static Icon getMenuItemArrowIcon() {
        if (WindowsIconFactory.menuItemArrowIcon == null) {
            WindowsIconFactory.menuItemArrowIcon = new MenuItemArrowIcon();
        }
        return WindowsIconFactory.menuItemArrowIcon;
    }
    
    public static Icon getMenuArrowIcon() {
        if (WindowsIconFactory.menuArrowIcon == null) {
            WindowsIconFactory.menuArrowIcon = new MenuArrowIcon();
        }
        return WindowsIconFactory.menuArrowIcon;
    }
    
    public static Icon getCheckBoxIcon() {
        if (WindowsIconFactory.checkBoxIcon == null) {
            WindowsIconFactory.checkBoxIcon = new CheckBoxIcon();
        }
        return WindowsIconFactory.checkBoxIcon;
    }
    
    public static Icon getRadioButtonIcon() {
        if (WindowsIconFactory.radioButtonIcon == null) {
            WindowsIconFactory.radioButtonIcon = new RadioButtonIcon();
        }
        return WindowsIconFactory.radioButtonIcon;
    }
    
    public static Icon getCheckBoxMenuItemIcon() {
        if (WindowsIconFactory.checkBoxMenuItemIcon == null) {
            WindowsIconFactory.checkBoxMenuItemIcon = new CheckBoxMenuItemIcon();
        }
        return WindowsIconFactory.checkBoxMenuItemIcon;
    }
    
    public static Icon getRadioButtonMenuItemIcon() {
        if (WindowsIconFactory.radioButtonMenuItemIcon == null) {
            WindowsIconFactory.radioButtonMenuItemIcon = new RadioButtonMenuItemIcon();
        }
        return WindowsIconFactory.radioButtonMenuItemIcon;
    }
    
    static synchronized VistaMenuItemCheckIconFactory getMenuItemCheckIconFactory() {
        if (WindowsIconFactory.menuItemCheckIconFactory == null) {
            WindowsIconFactory.menuItemCheckIconFactory = new VistaMenuItemCheckIconFactory();
        }
        return WindowsIconFactory.menuItemCheckIconFactory;
    }
    
    public static Icon createFrameCloseIcon() {
        if (WindowsIconFactory.frame_closeIcon == null) {
            WindowsIconFactory.frame_closeIcon = new FrameButtonIcon(TMSchema.Part.WP_CLOSEBUTTON);
        }
        return WindowsIconFactory.frame_closeIcon;
    }
    
    public static Icon createFrameIconifyIcon() {
        if (WindowsIconFactory.frame_iconifyIcon == null) {
            WindowsIconFactory.frame_iconifyIcon = new FrameButtonIcon(TMSchema.Part.WP_MINBUTTON);
        }
        return WindowsIconFactory.frame_iconifyIcon;
    }
    
    public static Icon createFrameMaximizeIcon() {
        if (WindowsIconFactory.frame_maxIcon == null) {
            WindowsIconFactory.frame_maxIcon = new FrameButtonIcon(TMSchema.Part.WP_MAXBUTTON);
        }
        return WindowsIconFactory.frame_maxIcon;
    }
    
    public static Icon createFrameMinimizeIcon() {
        if (WindowsIconFactory.frame_minIcon == null) {
            WindowsIconFactory.frame_minIcon = new FrameButtonIcon(TMSchema.Part.WP_RESTOREBUTTON);
        }
        return WindowsIconFactory.frame_minIcon;
    }
    
    public static Icon createFrameResizeIcon() {
        if (WindowsIconFactory.frame_resizeIcon == null) {
            WindowsIconFactory.frame_resizeIcon = new ResizeIcon();
        }
        return WindowsIconFactory.frame_resizeIcon;
    }
    
    private static class FrameButtonIcon implements Icon, Serializable
    {
        private TMSchema.Part part;
        
        private FrameButtonIcon(final TMSchema.Part part) {
            this.part = part;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final int iconWidth = this.getIconWidth();
            final int iconHeight = this.getIconHeight();
            final XPStyle xp = XPStyle.getXP();
            if (xp != null) {
                final XPStyle.Skin skin = xp.getSkin(component, this.part);
                final AbstractButton abstractButton = (AbstractButton)component;
                final ButtonModel model = abstractButton.getModel();
                final JInternalFrame internalFrame = (JInternalFrame)SwingUtilities.getAncestorOfClass(JInternalFrame.class, abstractButton);
                TMSchema.State state;
                if (internalFrame != null && internalFrame.isSelected()) {
                    if (!model.isEnabled()) {
                        state = TMSchema.State.DISABLED;
                    }
                    else if (model.isArmed() && model.isPressed()) {
                        state = TMSchema.State.PUSHED;
                    }
                    else if (model.isRollover()) {
                        state = TMSchema.State.HOT;
                    }
                    else {
                        state = TMSchema.State.NORMAL;
                    }
                }
                else if (!model.isEnabled()) {
                    state = TMSchema.State.INACTIVEDISABLED;
                }
                else if (model.isArmed() && model.isPressed()) {
                    state = TMSchema.State.INACTIVEPUSHED;
                }
                else if (model.isRollover()) {
                    state = TMSchema.State.INACTIVEHOT;
                }
                else {
                    state = TMSchema.State.INACTIVENORMAL;
                }
                skin.paintSkin(graphics, 0, 0, iconWidth, iconHeight, state);
            }
            else {
                graphics.setColor(Color.black);
                int n3 = iconWidth / 12 + 2;
                final int n4 = iconHeight / 5;
                final int n5 = iconHeight - n4 * 2 - 1;
                int n6 = iconWidth * 3 / 4 - 3;
                final int max = Math.max(iconHeight / 8, 2);
                final int max2 = Math.max(iconWidth / 15, 1);
                if (this.part == TMSchema.Part.WP_CLOSEBUTTON) {
                    int n7;
                    if (iconWidth > 47) {
                        n7 = 6;
                    }
                    else if (iconWidth > 37) {
                        n7 = 5;
                    }
                    else if (iconWidth > 26) {
                        n7 = 4;
                    }
                    else if (iconWidth > 16) {
                        n7 = 3;
                    }
                    else if (iconWidth > 12) {
                        n7 = 2;
                    }
                    else {
                        n7 = 1;
                    }
                    int n8 = iconHeight / 12 + 2;
                    if (n7 == 1) {
                        if (n6 % 2 == 1) {
                            ++n3;
                            ++n6;
                        }
                        graphics.drawLine(n3, n8, n3 + n6 - 2, n8 + n6 - 2);
                        graphics.drawLine(n3 + n6 - 2, n8, n3, n8 + n6 - 2);
                    }
                    else if (n7 == 2) {
                        if (n6 > 6) {
                            ++n3;
                            --n6;
                        }
                        graphics.drawLine(n3, n8, n3 + n6 - 2, n8 + n6 - 2);
                        graphics.drawLine(n3 + n6 - 2, n8, n3, n8 + n6 - 2);
                        graphics.drawLine(n3 + 1, n8, n3 + n6 - 1, n8 + n6 - 2);
                        graphics.drawLine(n3 + n6 - 1, n8, n3 + 1, n8 + n6 - 2);
                    }
                    else {
                        n3 += 2;
                        ++n8;
                        n6 -= 2;
                        graphics.drawLine(n3, n8, n3 + n6 - 1, n8 + n6 - 1);
                        graphics.drawLine(n3 + n6 - 1, n8, n3, n8 + n6 - 1);
                        graphics.drawLine(n3 + 1, n8, n3 + n6 - 1, n8 + n6 - 2);
                        graphics.drawLine(n3 + n6 - 2, n8, n3, n8 + n6 - 2);
                        graphics.drawLine(n3, n8 + 1, n3 + n6 - 2, n8 + n6 - 1);
                        graphics.drawLine(n3 + n6 - 1, n8 + 1, n3 + 1, n8 + n6 - 1);
                        for (int i = 4; i <= n7; ++i) {
                            graphics.drawLine(n3 + i - 2, n8, n3 + n6 - 1, n8 + n6 - i + 1);
                            graphics.drawLine(n3, n8 + i - 2, n3 + n6 - i + 1, n8 + n6 - 1);
                            graphics.drawLine(n3 + n6 - i + 1, n8, n3, n8 + n6 - i + 1);
                            graphics.drawLine(n3 + n6 - 1, n8 + i - 2, n3 + i - 2, n8 + n6 - 1);
                        }
                    }
                }
                else if (this.part == TMSchema.Part.WP_MINBUTTON) {
                    graphics.fillRect(n3, n4 + n5 - max, n6 - n6 / 3, max);
                }
                else if (this.part == TMSchema.Part.WP_MAXBUTTON) {
                    graphics.fillRect(n3, n4, n6, max);
                    graphics.fillRect(n3, n4, max2, n5);
                    graphics.fillRect(n3 + n6 - max2, n4, max2, n5);
                    graphics.fillRect(n3, n4 + n5 - max2, n6, max2);
                }
                else if (this.part == TMSchema.Part.WP_RESTOREBUTTON) {
                    graphics.fillRect(n3 + n6 / 3, n4, n6 - n6 / 3, max);
                    graphics.fillRect(n3 + n6 / 3, n4, max2, n5 / 3);
                    graphics.fillRect(n3 + n6 - max2, n4, max2, n5 - n5 / 3);
                    graphics.fillRect(n3 + n6 - n6 / 3, n4 + n5 - n5 / 3 - max2, n6 / 3, max2);
                    graphics.fillRect(n3, n4 + n5 / 3, n6 - n6 / 3, max);
                    graphics.fillRect(n3, n4 + n5 / 3, max2, n5 - n5 / 3);
                    graphics.fillRect(n3 + n6 - n6 / 3 - max2, n4 + n5 / 3, max2, n5 - n5 / 3);
                    graphics.fillRect(n3, n4 + n5 - max2, n6 - n6 / 3, max2);
                }
            }
        }
        
        @Override
        public int getIconWidth() {
            int n;
            if (XPStyle.getXP() != null) {
                n = UIManager.getInt("InternalFrame.titleButtonHeight") - 2;
                final Dimension partSize = XPStyle.getPartSize(TMSchema.Part.WP_CLOSEBUTTON, TMSchema.State.NORMAL);
                if (partSize != null && partSize.width != 0 && partSize.height != 0) {
                    n = (int)(n * (float)partSize.width / partSize.height);
                }
            }
            else {
                n = UIManager.getInt("InternalFrame.titleButtonWidth") - 2;
            }
            if (XPStyle.getXP() != null) {
                n -= 2;
            }
            return n;
        }
        
        @Override
        public int getIconHeight() {
            return UIManager.getInt("InternalFrame.titleButtonHeight") - 4;
        }
    }
    
    private static class ResizeIcon implements Icon, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            graphics.setColor(UIManager.getColor("InternalFrame.resizeIconHighlight"));
            graphics.drawLine(0, 11, 11, 0);
            graphics.drawLine(4, 11, 11, 4);
            graphics.drawLine(8, 11, 11, 8);
            graphics.setColor(UIManager.getColor("InternalFrame.resizeIconShadow"));
            graphics.drawLine(1, 11, 11, 1);
            graphics.drawLine(2, 11, 11, 2);
            graphics.drawLine(5, 11, 11, 5);
            graphics.drawLine(6, 11, 11, 6);
            graphics.drawLine(9, 11, 11, 9);
            graphics.drawLine(10, 11, 11, 10);
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
    
    private static class CheckBoxIcon implements Icon, Serializable
    {
        static final int csize = 13;
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final JCheckBox checkBox = (JCheckBox)component;
            final ButtonModel model = checkBox.getModel();
            final XPStyle xp = XPStyle.getXP();
            if (xp != null) {
                TMSchema.State state;
                if (model.isSelected()) {
                    state = TMSchema.State.CHECKEDNORMAL;
                    if (!model.isEnabled()) {
                        state = TMSchema.State.CHECKEDDISABLED;
                    }
                    else if (model.isPressed() && model.isArmed()) {
                        state = TMSchema.State.CHECKEDPRESSED;
                    }
                    else if (model.isRollover()) {
                        state = TMSchema.State.CHECKEDHOT;
                    }
                }
                else {
                    state = TMSchema.State.UNCHECKEDNORMAL;
                    if (!model.isEnabled()) {
                        state = TMSchema.State.UNCHECKEDDISABLED;
                    }
                    else if (model.isPressed() && model.isArmed()) {
                        state = TMSchema.State.UNCHECKEDPRESSED;
                    }
                    else if (model.isRollover()) {
                        state = TMSchema.State.UNCHECKEDHOT;
                    }
                }
                xp.getSkin(component, TMSchema.Part.BP_CHECKBOX).paintSkin(graphics, n, n2, state);
            }
            else {
                if (!checkBox.isBorderPaintedFlat()) {
                    graphics.setColor(UIManager.getColor("CheckBox.shadow"));
                    graphics.drawLine(n, n2, n + 11, n2);
                    graphics.drawLine(n, n2 + 1, n, n2 + 11);
                    graphics.setColor(UIManager.getColor("CheckBox.highlight"));
                    graphics.drawLine(n + 12, n2, n + 12, n2 + 12);
                    graphics.drawLine(n, n2 + 12, n + 11, n2 + 12);
                    graphics.setColor(UIManager.getColor("CheckBox.darkShadow"));
                    graphics.drawLine(n + 1, n2 + 1, n + 10, n2 + 1);
                    graphics.drawLine(n + 1, n2 + 2, n + 1, n2 + 10);
                    graphics.setColor(UIManager.getColor("CheckBox.light"));
                    graphics.drawLine(n + 1, n2 + 11, n + 11, n2 + 11);
                    graphics.drawLine(n + 11, n2 + 1, n + 11, n2 + 10);
                    if ((model.isPressed() && model.isArmed()) || !model.isEnabled()) {
                        graphics.setColor(UIManager.getColor("CheckBox.background"));
                    }
                    else {
                        graphics.setColor(UIManager.getColor("CheckBox.interiorBackground"));
                    }
                    graphics.fillRect(n + 2, n2 + 2, 9, 9);
                }
                else {
                    graphics.setColor(UIManager.getColor("CheckBox.shadow"));
                    graphics.drawRect(n + 1, n2 + 1, 10, 10);
                    if ((model.isPressed() && model.isArmed()) || !model.isEnabled()) {
                        graphics.setColor(UIManager.getColor("CheckBox.background"));
                    }
                    else {
                        graphics.setColor(UIManager.getColor("CheckBox.interiorBackground"));
                    }
                    graphics.fillRect(n + 2, n2 + 2, 9, 9);
                }
                if (model.isEnabled()) {
                    graphics.setColor(UIManager.getColor("CheckBox.foreground"));
                }
                else {
                    graphics.setColor(UIManager.getColor("CheckBox.shadow"));
                }
                if (model.isSelected()) {
                    graphics.drawLine(n + 9, n2 + 3, n + 9, n2 + 3);
                    graphics.drawLine(n + 8, n2 + 4, n + 9, n2 + 4);
                    graphics.drawLine(n + 7, n2 + 5, n + 9, n2 + 5);
                    graphics.drawLine(n + 6, n2 + 6, n + 8, n2 + 6);
                    graphics.drawLine(n + 3, n2 + 7, n + 7, n2 + 7);
                    graphics.drawLine(n + 4, n2 + 8, n + 6, n2 + 8);
                    graphics.drawLine(n + 5, n2 + 9, n + 5, n2 + 9);
                    graphics.drawLine(n + 3, n2 + 5, n + 3, n2 + 5);
                    graphics.drawLine(n + 3, n2 + 6, n + 4, n2 + 6);
                }
            }
        }
        
        @Override
        public int getIconWidth() {
            final XPStyle xp = XPStyle.getXP();
            if (xp != null) {
                return xp.getSkin(null, TMSchema.Part.BP_CHECKBOX).getWidth();
            }
            return 13;
        }
        
        @Override
        public int getIconHeight() {
            final XPStyle xp = XPStyle.getXP();
            if (xp != null) {
                return xp.getSkin(null, TMSchema.Part.BP_CHECKBOX).getHeight();
            }
            return 13;
        }
    }
    
    private static class RadioButtonIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final AbstractButton abstractButton = (AbstractButton)component;
            final ButtonModel model = abstractButton.getModel();
            final XPStyle xp = XPStyle.getXP();
            if (xp != null) {
                final XPStyle.Skin skin = xp.getSkin(abstractButton, TMSchema.Part.BP_RADIOBUTTON);
                TMSchema.State state;
                if (model.isSelected()) {
                    state = TMSchema.State.CHECKEDNORMAL;
                    if (!model.isEnabled()) {
                        state = TMSchema.State.CHECKEDDISABLED;
                    }
                    else if (model.isPressed() && model.isArmed()) {
                        state = TMSchema.State.CHECKEDPRESSED;
                    }
                    else if (model.isRollover()) {
                        state = TMSchema.State.CHECKEDHOT;
                    }
                }
                else {
                    state = TMSchema.State.UNCHECKEDNORMAL;
                    if (!model.isEnabled()) {
                        state = TMSchema.State.UNCHECKEDDISABLED;
                    }
                    else if (model.isPressed() && model.isArmed()) {
                        state = TMSchema.State.UNCHECKEDPRESSED;
                    }
                    else if (model.isRollover()) {
                        state = TMSchema.State.UNCHECKEDHOT;
                    }
                }
                skin.paintSkin(graphics, n, n2, state);
            }
            else {
                if ((model.isPressed() && model.isArmed()) || !model.isEnabled()) {
                    graphics.setColor(UIManager.getColor("RadioButton.background"));
                }
                else {
                    graphics.setColor(UIManager.getColor("RadioButton.interiorBackground"));
                }
                graphics.fillRect(n + 2, n2 + 2, 8, 8);
                graphics.setColor(UIManager.getColor("RadioButton.shadow"));
                graphics.drawLine(n + 4, n2 + 0, n + 7, n2 + 0);
                graphics.drawLine(n + 2, n2 + 1, n + 3, n2 + 1);
                graphics.drawLine(n + 8, n2 + 1, n + 9, n2 + 1);
                graphics.drawLine(n + 1, n2 + 2, n + 1, n2 + 3);
                graphics.drawLine(n + 0, n2 + 4, n + 0, n2 + 7);
                graphics.drawLine(n + 1, n2 + 8, n + 1, n2 + 9);
                graphics.setColor(UIManager.getColor("RadioButton.highlight"));
                graphics.drawLine(n + 2, n2 + 10, n + 3, n2 + 10);
                graphics.drawLine(n + 4, n2 + 11, n + 7, n2 + 11);
                graphics.drawLine(n + 8, n2 + 10, n + 9, n2 + 10);
                graphics.drawLine(n + 10, n2 + 9, n + 10, n2 + 8);
                graphics.drawLine(n + 11, n2 + 7, n + 11, n2 + 4);
                graphics.drawLine(n + 10, n2 + 3, n + 10, n2 + 2);
                graphics.setColor(UIManager.getColor("RadioButton.darkShadow"));
                graphics.drawLine(n + 4, n2 + 1, n + 7, n2 + 1);
                graphics.drawLine(n + 2, n2 + 2, n + 3, n2 + 2);
                graphics.drawLine(n + 8, n2 + 2, n + 9, n2 + 2);
                graphics.drawLine(n + 2, n2 + 3, n + 2, n2 + 3);
                graphics.drawLine(n + 1, n2 + 4, n + 1, n2 + 7);
                graphics.drawLine(n + 2, n2 + 8, n + 2, n2 + 8);
                graphics.setColor(UIManager.getColor("RadioButton.light"));
                graphics.drawLine(n + 2, n2 + 9, n + 3, n2 + 9);
                graphics.drawLine(n + 4, n2 + 10, n + 7, n2 + 10);
                graphics.drawLine(n + 8, n2 + 9, n + 9, n2 + 9);
                graphics.drawLine(n + 9, n2 + 8, n + 9, n2 + 8);
                graphics.drawLine(n + 10, n2 + 7, n + 10, n2 + 4);
                graphics.drawLine(n + 9, n2 + 3, n + 9, n2 + 3);
                if (model.isSelected()) {
                    if (model.isEnabled()) {
                        graphics.setColor(UIManager.getColor("RadioButton.foreground"));
                    }
                    else {
                        graphics.setColor(UIManager.getColor("RadioButton.shadow"));
                    }
                    graphics.fillRect(n + 4, n2 + 5, 4, 2);
                    graphics.fillRect(n + 5, n2 + 4, 2, 4);
                }
            }
        }
        
        @Override
        public int getIconWidth() {
            final XPStyle xp = XPStyle.getXP();
            if (xp != null) {
                return xp.getSkin(null, TMSchema.Part.BP_RADIOBUTTON).getWidth();
            }
            return 13;
        }
        
        @Override
        public int getIconHeight() {
            final XPStyle xp = XPStyle.getXP();
            if (xp != null) {
                return xp.getSkin(null, TMSchema.Part.BP_RADIOBUTTON).getHeight();
            }
            return 13;
        }
    }
    
    private static class CheckBoxMenuItemIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, int n2) {
            if (((AbstractButton)component).getModel().isSelected()) {
                n2 -= this.getIconHeight() / 2;
                graphics.drawLine(n + 9, n2 + 3, n + 9, n2 + 3);
                graphics.drawLine(n + 8, n2 + 4, n + 9, n2 + 4);
                graphics.drawLine(n + 7, n2 + 5, n + 9, n2 + 5);
                graphics.drawLine(n + 6, n2 + 6, n + 8, n2 + 6);
                graphics.drawLine(n + 3, n2 + 7, n + 7, n2 + 7);
                graphics.drawLine(n + 4, n2 + 8, n + 6, n2 + 8);
                graphics.drawLine(n + 5, n2 + 9, n + 5, n2 + 9);
                graphics.drawLine(n + 3, n2 + 5, n + 3, n2 + 5);
                graphics.drawLine(n + 3, n2 + 6, n + 4, n2 + 6);
            }
        }
        
        @Override
        public int getIconWidth() {
            return 9;
        }
        
        @Override
        public int getIconHeight() {
            return 9;
        }
    }
    
    private static class RadioButtonMenuItemIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final AbstractButton abstractButton = (AbstractButton)component;
            abstractButton.getModel();
            if (abstractButton.isSelected()) {
                graphics.fillRoundRect(n + 3, n2 + 3, this.getIconWidth() - 6, this.getIconHeight() - 6, 4, 4);
            }
        }
        
        @Override
        public int getIconWidth() {
            return 12;
        }
        
        @Override
        public int getIconHeight() {
            return 12;
        }
    }
    
    private static class MenuItemCheckIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
        }
        
        @Override
        public int getIconWidth() {
            return 9;
        }
        
        @Override
        public int getIconHeight() {
            return 9;
        }
    }
    
    private static class MenuItemArrowIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
        }
        
        @Override
        public int getIconWidth() {
            return 4;
        }
        
        @Override
        public int getIconHeight() {
            return 8;
        }
    }
    
    private static class MenuArrowIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final XPStyle xp = XPStyle.getXP();
            if (WindowsMenuItemUI.isVistaPainting(xp)) {
                TMSchema.State normal = TMSchema.State.NORMAL;
                if (component instanceof JMenuItem) {
                    normal = (((JMenuItem)component).getModel().isEnabled() ? TMSchema.State.NORMAL : TMSchema.State.DISABLED);
                }
                final XPStyle.Skin skin = xp.getSkin(component, TMSchema.Part.MP_POPUPSUBMENU);
                if (WindowsGraphicsUtils.isLeftToRight(component)) {
                    skin.paintSkin(graphics, n, n2, normal);
                }
                else {
                    final Graphics2D graphics2D = (Graphics2D)graphics.create();
                    graphics2D.translate(n + skin.getWidth(), n2);
                    graphics2D.scale(-1.0, 1.0);
                    skin.paintSkin(graphics2D, 0, 0, normal);
                    graphics2D.dispose();
                }
            }
            else {
                graphics.translate(n, n2);
                if (WindowsGraphicsUtils.isLeftToRight(component)) {
                    graphics.drawLine(0, 0, 0, 7);
                    graphics.drawLine(1, 1, 1, 6);
                    graphics.drawLine(2, 2, 2, 5);
                    graphics.drawLine(3, 3, 3, 4);
                }
                else {
                    graphics.drawLine(4, 0, 4, 7);
                    graphics.drawLine(3, 1, 3, 6);
                    graphics.drawLine(2, 2, 2, 5);
                    graphics.drawLine(1, 3, 1, 4);
                }
                graphics.translate(-n, -n2);
            }
        }
        
        @Override
        public int getIconWidth() {
            final XPStyle xp = XPStyle.getXP();
            if (WindowsMenuItemUI.isVistaPainting(xp)) {
                return xp.getSkin(null, TMSchema.Part.MP_POPUPSUBMENU).getWidth();
            }
            return 4;
        }
        
        @Override
        public int getIconHeight() {
            final XPStyle xp = XPStyle.getXP();
            if (WindowsMenuItemUI.isVistaPainting(xp)) {
                return xp.getSkin(null, TMSchema.Part.MP_POPUPSUBMENU).getHeight();
            }
            return 8;
        }
    }
    
    static class VistaMenuItemCheckIconFactory implements MenuItemCheckIconFactory
    {
        private static final int OFFSET = 3;
        
        @Override
        public Icon getIcon(final JMenuItem menuItem) {
            return new VistaMenuItemCheckIcon(menuItem);
        }
        
        @Override
        public boolean isCompatible(final Object o, final String s) {
            return o instanceof VistaMenuItemCheckIcon && ((VistaMenuItemCheckIcon)o).type == getType(s);
        }
        
        public Icon getIcon(final String s) {
            return new VistaMenuItemCheckIcon(s);
        }
        
        static int getIconWidth() {
            final XPStyle xp = XPStyle.getXP();
            return ((xp != null) ? xp.getSkin(null, TMSchema.Part.MP_POPUPCHECK).getWidth() : 16) + 6;
        }
        
        private static Class<? extends JMenuItem> getType(final Component component) {
            Class<? extends JMenuItem> clazz = null;
            if (component instanceof JCheckBoxMenuItem) {
                clazz = JCheckBoxMenuItem.class;
            }
            else if (component instanceof JRadioButtonMenuItem) {
                clazz = JRadioButtonMenuItem.class;
            }
            else if (component instanceof JMenu) {
                clazz = JMenu.class;
            }
            else if (component instanceof JMenuItem) {
                clazz = JMenuItem.class;
            }
            return clazz;
        }
        
        private static Class<? extends JMenuItem> getType(final String s) {
            Serializable s2;
            if (s == "CheckBoxMenuItem") {
                s2 = JCheckBoxMenuItem.class;
            }
            else if (s == "RadioButtonMenuItem") {
                s2 = JRadioButtonMenuItem.class;
            }
            else if (s == "Menu") {
                s2 = JMenu.class;
            }
            else if (s == "MenuItem") {
                s2 = JMenuItem.class;
            }
            else {
                s2 = JMenuItem.class;
            }
            return (Class<? extends JMenuItem>)s2;
        }
        
        private static class VistaMenuItemCheckIcon implements Icon, UIResource, Serializable
        {
            private final JMenuItem menuItem;
            private final Class<? extends JMenuItem> type;
            
            VistaMenuItemCheckIcon(final JMenuItem menuItem) {
                this.type = getType(menuItem);
                this.menuItem = menuItem;
            }
            
            VistaMenuItemCheckIcon(final String s) {
                this.type = getType(s);
                this.menuItem = null;
            }
            
            @Override
            public int getIconHeight() {
                final Icon laFIcon = this.getLaFIcon();
                if (laFIcon != null) {
                    return laFIcon.getIconHeight();
                }
                final Icon icon = this.getIcon();
                int n;
                if (icon != null) {
                    n = icon.getIconHeight();
                }
                else {
                    final XPStyle xp = XPStyle.getXP();
                    if (xp != null) {
                        n = xp.getSkin(null, TMSchema.Part.MP_POPUPCHECK).getHeight();
                    }
                    else {
                        n = 16;
                    }
                }
                n += 6;
                return n;
            }
            
            @Override
            public int getIconWidth() {
                final Icon laFIcon = this.getLaFIcon();
                if (laFIcon != null) {
                    return laFIcon.getIconWidth();
                }
                final Icon icon = this.getIcon();
                int iconWidth;
                if (icon != null) {
                    iconWidth = icon.getIconWidth() + 6;
                }
                else {
                    iconWidth = VistaMenuItemCheckIconFactory.getIconWidth();
                }
                return iconWidth;
            }
            
            @Override
            public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
                final Icon laFIcon = this.getLaFIcon();
                if (laFIcon != null) {
                    laFIcon.paintIcon(component, graphics, n, n2);
                    return;
                }
                assert component == this.menuItem;
                final Icon icon = this.getIcon();
                if ((this.type == JCheckBoxMenuItem.class || this.type == JRadioButtonMenuItem.class) && ((AbstractButton)component).isSelected()) {
                    final TMSchema.Part mp_POPUPCHECKBACKGROUND = TMSchema.Part.MP_POPUPCHECKBACKGROUND;
                    final TMSchema.Part mp_POPUPCHECK = TMSchema.Part.MP_POPUPCHECK;
                    TMSchema.State disabledpushed;
                    TMSchema.State state;
                    if (isEnabled(component, null)) {
                        disabledpushed = ((icon != null) ? TMSchema.State.BITMAP : TMSchema.State.NORMAL);
                        state = ((this.type == JRadioButtonMenuItem.class) ? TMSchema.State.BULLETNORMAL : TMSchema.State.CHECKMARKNORMAL);
                    }
                    else {
                        disabledpushed = TMSchema.State.DISABLEDPUSHED;
                        state = ((this.type == JRadioButtonMenuItem.class) ? TMSchema.State.BULLETDISABLED : TMSchema.State.CHECKMARKDISABLED);
                    }
                    final XPStyle xp = XPStyle.getXP();
                    if (xp != null) {
                        xp.getSkin(component, mp_POPUPCHECKBACKGROUND).paintSkin(graphics, n, n2, this.getIconWidth(), this.getIconHeight(), disabledpushed);
                        if (icon == null) {
                            xp.getSkin(component, mp_POPUPCHECK).paintSkin(graphics, n + 3, n2 + 3, state);
                        }
                    }
                }
                if (icon != null) {
                    icon.paintIcon(component, graphics, n + 3, n2 + 3);
                }
            }
            
            private static WindowsMenuItemUIAccessor getAccessor(final JMenuItem menuItem) {
                WindowsMenuItemUIAccessor windowsMenuItemUIAccessor = null;
                final ButtonUI buttonUI = (menuItem != null) ? menuItem.getUI() : null;
                if (buttonUI instanceof WindowsMenuItemUI) {
                    windowsMenuItemUIAccessor = ((WindowsMenuItemUI)buttonUI).accessor;
                }
                else if (buttonUI instanceof WindowsMenuUI) {
                    windowsMenuItemUIAccessor = ((WindowsMenuUI)buttonUI).accessor;
                }
                else if (buttonUI instanceof WindowsCheckBoxMenuItemUI) {
                    windowsMenuItemUIAccessor = ((WindowsCheckBoxMenuItemUI)buttonUI).accessor;
                }
                else if (buttonUI instanceof WindowsRadioButtonMenuItemUI) {
                    windowsMenuItemUIAccessor = ((WindowsRadioButtonMenuItemUI)buttonUI).accessor;
                }
                return windowsMenuItemUIAccessor;
            }
            
            private static boolean isEnabled(final Component component, TMSchema.State state) {
                if (state == null && component instanceof JMenuItem) {
                    final WindowsMenuItemUIAccessor accessor = getAccessor((JMenuItem)component);
                    if (accessor != null) {
                        state = accessor.getState((JMenuItem)component);
                    }
                }
                if (state == null) {
                    return component == null || component.isEnabled();
                }
                return state != TMSchema.State.DISABLED && state != TMSchema.State.DISABLEDHOT && state != TMSchema.State.DISABLEDPUSHED;
            }
            
            private Icon getIcon() {
                final Icon icon = null;
                if (this.menuItem == null) {
                    return icon;
                }
                final WindowsMenuItemUIAccessor accessor = getAccessor(this.menuItem);
                final TMSchema.State state = (accessor != null) ? accessor.getState(this.menuItem) : null;
                Icon icon2;
                if (isEnabled(this.menuItem, null)) {
                    if (state == TMSchema.State.PUSHED) {
                        icon2 = this.menuItem.getPressedIcon();
                    }
                    else {
                        icon2 = this.menuItem.getIcon();
                    }
                }
                else {
                    icon2 = this.menuItem.getDisabledIcon();
                }
                return icon2;
            }
            
            private Icon getLaFIcon() {
                Icon icon = (Icon)UIManager.getDefaults().get(typeToString(this.type));
                if (icon instanceof VistaMenuItemCheckIcon && ((VistaMenuItemCheckIcon)icon).type == this.type) {
                    icon = null;
                }
                return icon;
            }
            
            private static String typeToString(final Class<? extends JMenuItem> clazz) {
                assert clazz == JRadioButtonMenuItem.class;
                final StringBuilder sb = new StringBuilder(clazz.getName());
                sb.delete(0, sb.lastIndexOf("J") + 1);
                sb.append(".checkIcon");
                return sb.toString();
            }
        }
    }
}
