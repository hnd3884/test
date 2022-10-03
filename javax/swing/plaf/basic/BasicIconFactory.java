package javax.swing.plaf.basic;

import java.awt.Polygon;
import javax.swing.AbstractButton;
import javax.swing.plaf.UIResource;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.Icon;
import java.io.Serializable;

public class BasicIconFactory implements Serializable
{
    private static Icon frame_icon;
    private static Icon checkBoxIcon;
    private static Icon radioButtonIcon;
    private static Icon checkBoxMenuItemIcon;
    private static Icon radioButtonMenuItemIcon;
    private static Icon menuItemCheckIcon;
    private static Icon menuItemArrowIcon;
    private static Icon menuArrowIcon;
    
    public static Icon getMenuItemCheckIcon() {
        if (BasicIconFactory.menuItemCheckIcon == null) {
            BasicIconFactory.menuItemCheckIcon = new MenuItemCheckIcon();
        }
        return BasicIconFactory.menuItemCheckIcon;
    }
    
    public static Icon getMenuItemArrowIcon() {
        if (BasicIconFactory.menuItemArrowIcon == null) {
            BasicIconFactory.menuItemArrowIcon = new MenuItemArrowIcon();
        }
        return BasicIconFactory.menuItemArrowIcon;
    }
    
    public static Icon getMenuArrowIcon() {
        if (BasicIconFactory.menuArrowIcon == null) {
            BasicIconFactory.menuArrowIcon = new MenuArrowIcon();
        }
        return BasicIconFactory.menuArrowIcon;
    }
    
    public static Icon getCheckBoxIcon() {
        if (BasicIconFactory.checkBoxIcon == null) {
            BasicIconFactory.checkBoxIcon = new CheckBoxIcon();
        }
        return BasicIconFactory.checkBoxIcon;
    }
    
    public static Icon getRadioButtonIcon() {
        if (BasicIconFactory.radioButtonIcon == null) {
            BasicIconFactory.radioButtonIcon = new RadioButtonIcon();
        }
        return BasicIconFactory.radioButtonIcon;
    }
    
    public static Icon getCheckBoxMenuItemIcon() {
        if (BasicIconFactory.checkBoxMenuItemIcon == null) {
            BasicIconFactory.checkBoxMenuItemIcon = new CheckBoxMenuItemIcon();
        }
        return BasicIconFactory.checkBoxMenuItemIcon;
    }
    
    public static Icon getRadioButtonMenuItemIcon() {
        if (BasicIconFactory.radioButtonMenuItemIcon == null) {
            BasicIconFactory.radioButtonMenuItemIcon = new RadioButtonMenuItemIcon();
        }
        return BasicIconFactory.radioButtonMenuItemIcon;
    }
    
    public static Icon createEmptyFrameIcon() {
        if (BasicIconFactory.frame_icon == null) {
            BasicIconFactory.frame_icon = new EmptyFrameIcon();
        }
        return BasicIconFactory.frame_icon;
    }
    
    private static class EmptyFrameIcon implements Icon, Serializable
    {
        int height;
        int width;
        
        private EmptyFrameIcon() {
            this.height = 16;
            this.width = 14;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
        }
        
        @Override
        public int getIconWidth() {
            return this.width;
        }
        
        @Override
        public int getIconHeight() {
            return this.height;
        }
    }
    
    private static class CheckBoxIcon implements Icon, Serializable
    {
        static final int csize = 13;
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
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
    
    private static class RadioButtonIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
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
    
    private static class CheckBoxMenuItemIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            if (((AbstractButton)component).getModel().isSelected()) {
                graphics.drawLine(n + 7, n2 + 1, n + 7, n2 + 3);
                graphics.drawLine(n + 6, n2 + 2, n + 6, n2 + 4);
                graphics.drawLine(n + 5, n2 + 3, n + 5, n2 + 5);
                graphics.drawLine(n + 4, n2 + 4, n + 4, n2 + 6);
                graphics.drawLine(n + 3, n2 + 5, n + 3, n2 + 7);
                graphics.drawLine(n + 2, n2 + 4, n + 2, n2 + 6);
                graphics.drawLine(n + 1, n2 + 3, n + 1, n2 + 5);
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
                graphics.fillOval(n + 1, n2 + 1, this.getIconWidth(), this.getIconHeight());
            }
        }
        
        @Override
        public int getIconWidth() {
            return 6;
        }
        
        @Override
        public int getIconHeight() {
            return 6;
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
            final Polygon polygon = new Polygon();
            polygon.addPoint(n, n2);
            polygon.addPoint(n + this.getIconWidth(), n2 + this.getIconHeight() / 2);
            polygon.addPoint(n, n2 + this.getIconHeight());
            graphics.fillPolygon(polygon);
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
}
