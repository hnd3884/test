package javax.swing.plaf.metal;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.Polygon;
import sun.swing.CachedPainter;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.image.ImageObserver;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.awt.Image;
import java.awt.GraphicsConfiguration;
import java.util.Vector;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.plaf.ColorUIResource;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.plaf.UIResource;
import java.awt.Dimension;
import javax.swing.Icon;
import java.io.Serializable;

public class MetalIconFactory implements Serializable
{
    private static Icon fileChooserDetailViewIcon;
    private static Icon fileChooserHomeFolderIcon;
    private static Icon fileChooserListViewIcon;
    private static Icon fileChooserNewFolderIcon;
    private static Icon fileChooserUpFolderIcon;
    private static Icon internalFrameAltMaximizeIcon;
    private static Icon internalFrameCloseIcon;
    private static Icon internalFrameDefaultMenuIcon;
    private static Icon internalFrameMaximizeIcon;
    private static Icon internalFrameMinimizeIcon;
    private static Icon radioButtonIcon;
    private static Icon treeComputerIcon;
    private static Icon treeFloppyDriveIcon;
    private static Icon treeHardDriveIcon;
    private static Icon menuArrowIcon;
    private static Icon menuItemArrowIcon;
    private static Icon checkBoxMenuItemIcon;
    private static Icon radioButtonMenuItemIcon;
    private static Icon checkBoxIcon;
    private static Icon oceanHorizontalSliderThumb;
    private static Icon oceanVerticalSliderThumb;
    public static final boolean DARK = false;
    public static final boolean LIGHT = true;
    private static final Dimension folderIcon16Size;
    private static final Dimension fileIcon16Size;
    private static final Dimension treeControlSize;
    private static final Dimension menuArrowIconSize;
    private static final Dimension menuCheckIconSize;
    private static final int xOff = 4;
    
    public static Icon getFileChooserDetailViewIcon() {
        if (MetalIconFactory.fileChooserDetailViewIcon == null) {
            MetalIconFactory.fileChooserDetailViewIcon = new FileChooserDetailViewIcon();
        }
        return MetalIconFactory.fileChooserDetailViewIcon;
    }
    
    public static Icon getFileChooserHomeFolderIcon() {
        if (MetalIconFactory.fileChooserHomeFolderIcon == null) {
            MetalIconFactory.fileChooserHomeFolderIcon = new FileChooserHomeFolderIcon();
        }
        return MetalIconFactory.fileChooserHomeFolderIcon;
    }
    
    public static Icon getFileChooserListViewIcon() {
        if (MetalIconFactory.fileChooserListViewIcon == null) {
            MetalIconFactory.fileChooserListViewIcon = new FileChooserListViewIcon();
        }
        return MetalIconFactory.fileChooserListViewIcon;
    }
    
    public static Icon getFileChooserNewFolderIcon() {
        if (MetalIconFactory.fileChooserNewFolderIcon == null) {
            MetalIconFactory.fileChooserNewFolderIcon = new FileChooserNewFolderIcon();
        }
        return MetalIconFactory.fileChooserNewFolderIcon;
    }
    
    public static Icon getFileChooserUpFolderIcon() {
        if (MetalIconFactory.fileChooserUpFolderIcon == null) {
            MetalIconFactory.fileChooserUpFolderIcon = new FileChooserUpFolderIcon();
        }
        return MetalIconFactory.fileChooserUpFolderIcon;
    }
    
    public static Icon getInternalFrameAltMaximizeIcon(final int n) {
        return new InternalFrameAltMaximizeIcon(n);
    }
    
    public static Icon getInternalFrameCloseIcon(final int n) {
        return new InternalFrameCloseIcon(n);
    }
    
    public static Icon getInternalFrameDefaultMenuIcon() {
        if (MetalIconFactory.internalFrameDefaultMenuIcon == null) {
            MetalIconFactory.internalFrameDefaultMenuIcon = new InternalFrameDefaultMenuIcon();
        }
        return MetalIconFactory.internalFrameDefaultMenuIcon;
    }
    
    public static Icon getInternalFrameMaximizeIcon(final int n) {
        return new InternalFrameMaximizeIcon(n);
    }
    
    public static Icon getInternalFrameMinimizeIcon(final int n) {
        return new InternalFrameMinimizeIcon(n);
    }
    
    public static Icon getRadioButtonIcon() {
        if (MetalIconFactory.radioButtonIcon == null) {
            MetalIconFactory.radioButtonIcon = new RadioButtonIcon();
        }
        return MetalIconFactory.radioButtonIcon;
    }
    
    public static Icon getCheckBoxIcon() {
        if (MetalIconFactory.checkBoxIcon == null) {
            MetalIconFactory.checkBoxIcon = new CheckBoxIcon();
        }
        return MetalIconFactory.checkBoxIcon;
    }
    
    public static Icon getTreeComputerIcon() {
        if (MetalIconFactory.treeComputerIcon == null) {
            MetalIconFactory.treeComputerIcon = new TreeComputerIcon();
        }
        return MetalIconFactory.treeComputerIcon;
    }
    
    public static Icon getTreeFloppyDriveIcon() {
        if (MetalIconFactory.treeFloppyDriveIcon == null) {
            MetalIconFactory.treeFloppyDriveIcon = new TreeFloppyDriveIcon();
        }
        return MetalIconFactory.treeFloppyDriveIcon;
    }
    
    public static Icon getTreeFolderIcon() {
        return new TreeFolderIcon();
    }
    
    public static Icon getTreeHardDriveIcon() {
        if (MetalIconFactory.treeHardDriveIcon == null) {
            MetalIconFactory.treeHardDriveIcon = new TreeHardDriveIcon();
        }
        return MetalIconFactory.treeHardDriveIcon;
    }
    
    public static Icon getTreeLeafIcon() {
        return new TreeLeafIcon();
    }
    
    public static Icon getTreeControlIcon(final boolean b) {
        return new TreeControlIcon(b);
    }
    
    public static Icon getMenuArrowIcon() {
        if (MetalIconFactory.menuArrowIcon == null) {
            MetalIconFactory.menuArrowIcon = new MenuArrowIcon();
        }
        return MetalIconFactory.menuArrowIcon;
    }
    
    public static Icon getMenuItemCheckIcon() {
        return null;
    }
    
    public static Icon getMenuItemArrowIcon() {
        if (MetalIconFactory.menuItemArrowIcon == null) {
            MetalIconFactory.menuItemArrowIcon = new MenuItemArrowIcon();
        }
        return MetalIconFactory.menuItemArrowIcon;
    }
    
    public static Icon getCheckBoxMenuItemIcon() {
        if (MetalIconFactory.checkBoxMenuItemIcon == null) {
            MetalIconFactory.checkBoxMenuItemIcon = new CheckBoxMenuItemIcon();
        }
        return MetalIconFactory.checkBoxMenuItemIcon;
    }
    
    public static Icon getRadioButtonMenuItemIcon() {
        if (MetalIconFactory.radioButtonMenuItemIcon == null) {
            MetalIconFactory.radioButtonMenuItemIcon = new RadioButtonMenuItemIcon();
        }
        return MetalIconFactory.radioButtonMenuItemIcon;
    }
    
    public static Icon getHorizontalSliderThumbIcon() {
        if (MetalLookAndFeel.usingOcean()) {
            if (MetalIconFactory.oceanHorizontalSliderThumb == null) {
                MetalIconFactory.oceanHorizontalSliderThumb = new OceanHorizontalSliderThumbIcon();
            }
            return MetalIconFactory.oceanHorizontalSliderThumb;
        }
        return new HorizontalSliderThumbIcon();
    }
    
    public static Icon getVerticalSliderThumbIcon() {
        if (MetalLookAndFeel.usingOcean()) {
            if (MetalIconFactory.oceanVerticalSliderThumb == null) {
                MetalIconFactory.oceanVerticalSliderThumb = new OceanVerticalSliderThumbIcon();
            }
            return MetalIconFactory.oceanVerticalSliderThumb;
        }
        return new VerticalSliderThumbIcon();
    }
    
    static {
        folderIcon16Size = new Dimension(16, 16);
        fileIcon16Size = new Dimension(16, 16);
        treeControlSize = new Dimension(18, 18);
        menuArrowIconSize = new Dimension(4, 8);
        menuCheckIconSize = new Dimension(10, 10);
    }
    
    private static class FileChooserDetailViewIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            graphics.translate(n, n2);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
            graphics.drawLine(2, 2, 5, 2);
            graphics.drawLine(2, 3, 2, 7);
            graphics.drawLine(3, 7, 6, 7);
            graphics.drawLine(6, 6, 6, 3);
            graphics.drawLine(2, 10, 5, 10);
            graphics.drawLine(2, 11, 2, 15);
            graphics.drawLine(3, 15, 6, 15);
            graphics.drawLine(6, 14, 6, 11);
            graphics.drawLine(8, 5, 15, 5);
            graphics.drawLine(8, 13, 15, 13);
            graphics.setColor(MetalLookAndFeel.getPrimaryControl());
            graphics.drawRect(3, 3, 2, 3);
            graphics.drawRect(3, 11, 2, 3);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
            graphics.drawLine(4, 4, 4, 5);
            graphics.drawLine(4, 12, 4, 13);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return 18;
        }
        
        @Override
        public int getIconHeight() {
            return 18;
        }
    }
    
    private static class FileChooserHomeFolderIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            graphics.translate(n, n2);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
            graphics.drawLine(8, 1, 1, 8);
            graphics.drawLine(8, 1, 15, 8);
            graphics.drawLine(11, 2, 11, 3);
            graphics.drawLine(12, 2, 12, 4);
            graphics.drawLine(3, 7, 3, 15);
            graphics.drawLine(13, 7, 13, 15);
            graphics.drawLine(4, 15, 12, 15);
            graphics.drawLine(6, 9, 6, 14);
            graphics.drawLine(10, 9, 10, 14);
            graphics.drawLine(7, 9, 9, 9);
            graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
            graphics.fillRect(8, 2, 1, 1);
            graphics.fillRect(7, 3, 3, 1);
            graphics.fillRect(6, 4, 5, 1);
            graphics.fillRect(5, 5, 7, 1);
            graphics.fillRect(4, 6, 9, 2);
            graphics.drawLine(9, 12, 9, 12);
            graphics.setColor(MetalLookAndFeel.getPrimaryControl());
            graphics.drawLine(4, 8, 12, 8);
            graphics.fillRect(4, 9, 2, 6);
            graphics.fillRect(11, 9, 2, 6);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return 18;
        }
        
        @Override
        public int getIconHeight() {
            return 18;
        }
    }
    
    private static class FileChooserListViewIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            graphics.translate(n, n2);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
            graphics.drawLine(2, 2, 5, 2);
            graphics.drawLine(2, 3, 2, 7);
            graphics.drawLine(3, 7, 6, 7);
            graphics.drawLine(6, 6, 6, 3);
            graphics.drawLine(10, 2, 13, 2);
            graphics.drawLine(10, 3, 10, 7);
            graphics.drawLine(11, 7, 14, 7);
            graphics.drawLine(14, 6, 14, 3);
            graphics.drawLine(2, 10, 5, 10);
            graphics.drawLine(2, 11, 2, 15);
            graphics.drawLine(3, 15, 6, 15);
            graphics.drawLine(6, 14, 6, 11);
            graphics.drawLine(10, 10, 13, 10);
            graphics.drawLine(10, 11, 10, 15);
            graphics.drawLine(11, 15, 14, 15);
            graphics.drawLine(14, 14, 14, 11);
            graphics.drawLine(8, 5, 8, 5);
            graphics.drawLine(16, 5, 16, 5);
            graphics.drawLine(8, 13, 8, 13);
            graphics.drawLine(16, 13, 16, 13);
            graphics.setColor(MetalLookAndFeel.getPrimaryControl());
            graphics.drawRect(3, 3, 2, 3);
            graphics.drawRect(11, 3, 2, 3);
            graphics.drawRect(3, 11, 2, 3);
            graphics.drawRect(11, 11, 2, 3);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
            graphics.drawLine(4, 4, 4, 5);
            graphics.drawLine(12, 4, 12, 5);
            graphics.drawLine(4, 12, 4, 13);
            graphics.drawLine(12, 12, 12, 13);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return 18;
        }
        
        @Override
        public int getIconHeight() {
            return 18;
        }
    }
    
    private static class FileChooserNewFolderIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            graphics.translate(n, n2);
            graphics.setColor(MetalLookAndFeel.getPrimaryControl());
            graphics.fillRect(3, 5, 12, 9);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
            graphics.drawLine(1, 6, 1, 14);
            graphics.drawLine(2, 14, 15, 14);
            graphics.drawLine(15, 13, 15, 5);
            graphics.drawLine(2, 5, 9, 5);
            graphics.drawLine(10, 6, 14, 6);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
            graphics.drawLine(2, 6, 2, 13);
            graphics.drawLine(3, 6, 9, 6);
            graphics.drawLine(10, 7, 14, 7);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            graphics.drawLine(11, 3, 15, 3);
            graphics.drawLine(10, 4, 15, 4);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return 18;
        }
        
        @Override
        public int getIconHeight() {
            return 18;
        }
    }
    
    private static class FileChooserUpFolderIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            graphics.translate(n, n2);
            graphics.setColor(MetalLookAndFeel.getPrimaryControl());
            graphics.fillRect(3, 5, 12, 9);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
            graphics.drawLine(1, 6, 1, 14);
            graphics.drawLine(2, 14, 15, 14);
            graphics.drawLine(15, 13, 15, 5);
            graphics.drawLine(2, 5, 9, 5);
            graphics.drawLine(10, 6, 14, 6);
            graphics.drawLine(8, 13, 8, 16);
            graphics.drawLine(8, 9, 8, 9);
            graphics.drawLine(7, 10, 9, 10);
            graphics.drawLine(6, 11, 10, 11);
            graphics.drawLine(5, 12, 11, 12);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
            graphics.drawLine(2, 6, 2, 13);
            graphics.drawLine(3, 6, 9, 6);
            graphics.drawLine(10, 7, 14, 7);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            graphics.drawLine(11, 3, 15, 3);
            graphics.drawLine(10, 4, 15, 4);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return 18;
        }
        
        @Override
        public int getIconHeight() {
            return 18;
        }
    }
    
    public static class PaletteCloseIcon implements Icon, UIResource, Serializable
    {
        int iconSize;
        
        public PaletteCloseIcon() {
            this.iconSize = 7;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final ButtonModel model = ((JButton)component).getModel();
            final ColorUIResource primaryControlHighlight = MetalLookAndFeel.getPrimaryControlHighlight();
            final ColorUIResource primaryControlInfo = MetalLookAndFeel.getPrimaryControlInfo();
            ColorUIResource primaryControlDarkShadow;
            if (model.isPressed() && model.isArmed()) {
                primaryControlDarkShadow = primaryControlInfo;
            }
            else {
                primaryControlDarkShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();
            }
            graphics.translate(n, n2);
            graphics.setColor(primaryControlDarkShadow);
            graphics.drawLine(0, 1, 5, 6);
            graphics.drawLine(1, 0, 6, 5);
            graphics.drawLine(1, 1, 6, 6);
            graphics.drawLine(6, 1, 1, 6);
            graphics.drawLine(5, 0, 0, 5);
            graphics.drawLine(5, 1, 1, 5);
            graphics.setColor(primaryControlHighlight);
            graphics.drawLine(6, 2, 5, 3);
            graphics.drawLine(2, 6, 3, 5);
            graphics.drawLine(6, 6, 6, 6);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return this.iconSize;
        }
        
        @Override
        public int getIconHeight() {
            return this.iconSize;
        }
    }
    
    private static class InternalFrameCloseIcon implements Icon, UIResource, Serializable
    {
        int iconSize;
        
        public InternalFrameCloseIcon(final int iconSize) {
            this.iconSize = 16;
            this.iconSize = iconSize;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final JButton button = (JButton)component;
            final ButtonModel model = button.getModel();
            ColorUIResource primaryControl = MetalLookAndFeel.getPrimaryControl();
            ColorUIResource color = MetalLookAndFeel.getPrimaryControl();
            ColorUIResource colorUIResource = MetalLookAndFeel.getPrimaryControlDarkShadow();
            final ColorUIResource black = MetalLookAndFeel.getBlack();
            ColorUIResource color2 = MetalLookAndFeel.getWhite();
            final ColorUIResource white = MetalLookAndFeel.getWhite();
            if (button.getClientProperty("paintActive") != Boolean.TRUE) {
                primaryControl = (color = MetalLookAndFeel.getControl());
                colorUIResource = MetalLookAndFeel.getControlDarkShadow();
                if (model.isPressed() && model.isArmed()) {
                    color = (color2 = MetalLookAndFeel.getControlShadow());
                    colorUIResource = black;
                }
            }
            else if (model.isPressed() && model.isArmed()) {
                color = (color2 = MetalLookAndFeel.getPrimaryControlShadow());
                colorUIResource = black;
            }
            final int n3 = this.iconSize / 2;
            graphics.translate(n, n2);
            graphics.setColor(primaryControl);
            graphics.fillRect(0, 0, this.iconSize, this.iconSize);
            graphics.setColor(color);
            graphics.fillRect(3, 3, this.iconSize - 6, this.iconSize - 6);
            graphics.setColor(black);
            graphics.drawRect(1, 1, this.iconSize - 3, this.iconSize - 3);
            graphics.drawRect(2, 2, this.iconSize - 5, this.iconSize - 5);
            graphics.setColor(white);
            graphics.drawRect(2, 2, this.iconSize - 3, this.iconSize - 3);
            graphics.setColor(colorUIResource);
            graphics.drawRect(2, 2, this.iconSize - 4, this.iconSize - 4);
            graphics.drawLine(3, this.iconSize - 3, 3, this.iconSize - 3);
            graphics.drawLine(this.iconSize - 3, 3, this.iconSize - 3, 3);
            graphics.setColor(black);
            graphics.drawLine(4, 5, 5, 4);
            graphics.drawLine(4, this.iconSize - 6, this.iconSize - 6, 4);
            graphics.setColor(color2);
            graphics.drawLine(6, this.iconSize - 5, this.iconSize - 5, 6);
            graphics.drawLine(n3, n3 + 2, n3 + 2, n3);
            graphics.drawLine(this.iconSize - 5, this.iconSize - 5, this.iconSize - 4, this.iconSize - 5);
            graphics.drawLine(this.iconSize - 5, this.iconSize - 4, this.iconSize - 5, this.iconSize - 4);
            graphics.setColor(colorUIResource);
            graphics.drawLine(5, 5, this.iconSize - 6, this.iconSize - 6);
            graphics.drawLine(6, 5, this.iconSize - 5, this.iconSize - 6);
            graphics.drawLine(5, 6, this.iconSize - 6, this.iconSize - 5);
            graphics.drawLine(5, this.iconSize - 5, this.iconSize - 5, 5);
            graphics.drawLine(5, this.iconSize - 6, this.iconSize - 6, 5);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return this.iconSize;
        }
        
        @Override
        public int getIconHeight() {
            return this.iconSize;
        }
    }
    
    private static class InternalFrameAltMaximizeIcon implements Icon, UIResource, Serializable
    {
        int iconSize;
        
        public InternalFrameAltMaximizeIcon(final int iconSize) {
            this.iconSize = 16;
            this.iconSize = iconSize;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final JButton button = (JButton)component;
            final ButtonModel model = button.getModel();
            ColorUIResource primaryControl = MetalLookAndFeel.getPrimaryControl();
            ColorUIResource color = MetalLookAndFeel.getPrimaryControl();
            ColorUIResource color2 = MetalLookAndFeel.getPrimaryControlDarkShadow();
            final ColorUIResource black = MetalLookAndFeel.getBlack();
            ColorUIResource colorUIResource = MetalLookAndFeel.getWhite();
            final ColorUIResource white = MetalLookAndFeel.getWhite();
            if (button.getClientProperty("paintActive") != Boolean.TRUE) {
                primaryControl = (color = MetalLookAndFeel.getControl());
                color2 = MetalLookAndFeel.getControlDarkShadow();
                if (model.isPressed() && model.isArmed()) {
                    color = (colorUIResource = MetalLookAndFeel.getControlShadow());
                    color2 = black;
                }
            }
            else if (model.isPressed() && model.isArmed()) {
                color = (colorUIResource = MetalLookAndFeel.getPrimaryControlShadow());
                color2 = black;
            }
            graphics.translate(n, n2);
            graphics.setColor(primaryControl);
            graphics.fillRect(0, 0, this.iconSize, this.iconSize);
            graphics.setColor(color);
            graphics.fillRect(3, 6, this.iconSize - 9, this.iconSize - 9);
            graphics.setColor(black);
            graphics.drawRect(1, 5, this.iconSize - 8, this.iconSize - 8);
            graphics.drawLine(1, this.iconSize - 2, 1, this.iconSize - 2);
            graphics.setColor(white);
            graphics.drawRect(2, 6, this.iconSize - 7, this.iconSize - 7);
            graphics.setColor(colorUIResource);
            graphics.drawRect(3, 7, this.iconSize - 9, this.iconSize - 9);
            graphics.setColor(color2);
            graphics.drawRect(2, 6, this.iconSize - 8, this.iconSize - 8);
            graphics.setColor(colorUIResource);
            graphics.drawLine(this.iconSize - 6, 8, this.iconSize - 6, 8);
            graphics.drawLine(this.iconSize - 9, 6, this.iconSize - 7, 8);
            graphics.setColor(color2);
            graphics.drawLine(3, this.iconSize - 3, 3, this.iconSize - 3);
            graphics.setColor(black);
            graphics.drawLine(this.iconSize - 6, 9, this.iconSize - 6, 9);
            graphics.setColor(primaryControl);
            graphics.drawLine(this.iconSize - 9, 5, this.iconSize - 9, 5);
            graphics.setColor(color2);
            graphics.fillRect(this.iconSize - 7, 3, 3, 5);
            graphics.drawLine(this.iconSize - 6, 5, this.iconSize - 3, 2);
            graphics.drawLine(this.iconSize - 6, 6, this.iconSize - 2, 2);
            graphics.drawLine(this.iconSize - 6, 7, this.iconSize - 3, 7);
            graphics.setColor(black);
            graphics.drawLine(this.iconSize - 8, 2, this.iconSize - 7, 2);
            graphics.drawLine(this.iconSize - 8, 3, this.iconSize - 8, 7);
            graphics.drawLine(this.iconSize - 6, 4, this.iconSize - 3, 1);
            graphics.drawLine(this.iconSize - 4, 6, this.iconSize - 3, 6);
            graphics.setColor(white);
            graphics.drawLine(this.iconSize - 6, 3, this.iconSize - 6, 3);
            graphics.drawLine(this.iconSize - 4, 5, this.iconSize - 2, 3);
            graphics.drawLine(this.iconSize - 4, 8, this.iconSize - 3, 8);
            graphics.drawLine(this.iconSize - 2, 8, this.iconSize - 2, 7);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return this.iconSize;
        }
        
        @Override
        public int getIconHeight() {
            return this.iconSize;
        }
    }
    
    private static class InternalFrameDefaultMenuIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final ColorUIResource windowBackground = MetalLookAndFeel.getWindowBackground();
            final ColorUIResource primaryControl = MetalLookAndFeel.getPrimaryControl();
            final ColorUIResource primaryControlDarkShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();
            graphics.translate(n, n2);
            graphics.setColor(primaryControl);
            graphics.fillRect(0, 0, 16, 16);
            graphics.setColor(windowBackground);
            graphics.fillRect(2, 6, 13, 9);
            graphics.drawLine(2, 2, 2, 2);
            graphics.drawLine(5, 2, 5, 2);
            graphics.drawLine(8, 2, 8, 2);
            graphics.drawLine(11, 2, 11, 2);
            graphics.setColor(primaryControlDarkShadow);
            graphics.drawRect(1, 1, 13, 13);
            graphics.drawLine(1, 0, 14, 0);
            graphics.drawLine(15, 1, 15, 14);
            graphics.drawLine(1, 15, 14, 15);
            graphics.drawLine(0, 1, 0, 14);
            graphics.drawLine(2, 5, 13, 5);
            graphics.drawLine(3, 3, 3, 3);
            graphics.drawLine(6, 3, 6, 3);
            graphics.drawLine(9, 3, 9, 3);
            graphics.drawLine(12, 3, 12, 3);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return 16;
        }
        
        @Override
        public int getIconHeight() {
            return 16;
        }
    }
    
    private static class InternalFrameMaximizeIcon implements Icon, UIResource, Serializable
    {
        protected int iconSize;
        
        public InternalFrameMaximizeIcon(final int iconSize) {
            this.iconSize = 16;
            this.iconSize = iconSize;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final JButton button = (JButton)component;
            final ButtonModel model = button.getModel();
            ColorUIResource primaryControl = MetalLookAndFeel.getPrimaryControl();
            ColorUIResource color = MetalLookAndFeel.getPrimaryControl();
            ColorUIResource colorUIResource = MetalLookAndFeel.getPrimaryControlDarkShadow();
            final ColorUIResource black = MetalLookAndFeel.getBlack();
            ColorUIResource colorUIResource2 = MetalLookAndFeel.getWhite();
            final ColorUIResource white = MetalLookAndFeel.getWhite();
            if (button.getClientProperty("paintActive") != Boolean.TRUE) {
                primaryControl = (color = MetalLookAndFeel.getControl());
                colorUIResource = MetalLookAndFeel.getControlDarkShadow();
                if (model.isPressed() && model.isArmed()) {
                    color = (colorUIResource2 = MetalLookAndFeel.getControlShadow());
                    colorUIResource = black;
                }
            }
            else if (model.isPressed() && model.isArmed()) {
                color = (colorUIResource2 = MetalLookAndFeel.getPrimaryControlShadow());
                colorUIResource = black;
            }
            graphics.translate(n, n2);
            graphics.setColor(primaryControl);
            graphics.fillRect(0, 0, this.iconSize, this.iconSize);
            graphics.setColor(color);
            graphics.fillRect(3, 7, this.iconSize - 10, this.iconSize - 10);
            graphics.setColor(colorUIResource2);
            graphics.drawRect(3, 7, this.iconSize - 10, this.iconSize - 10);
            graphics.setColor(white);
            graphics.drawRect(2, 6, this.iconSize - 7, this.iconSize - 7);
            graphics.setColor(black);
            graphics.drawRect(1, 5, this.iconSize - 7, this.iconSize - 7);
            graphics.drawRect(2, 6, this.iconSize - 9, this.iconSize - 9);
            graphics.setColor(colorUIResource);
            graphics.drawRect(2, 6, this.iconSize - 8, this.iconSize - 8);
            graphics.setColor(black);
            graphics.drawLine(3, this.iconSize - 5, this.iconSize - 9, 7);
            graphics.drawLine(this.iconSize - 6, 4, this.iconSize - 5, 3);
            graphics.drawLine(this.iconSize - 7, 1, this.iconSize - 7, 2);
            graphics.drawLine(this.iconSize - 6, 1, this.iconSize - 2, 1);
            graphics.setColor(colorUIResource2);
            graphics.drawLine(5, this.iconSize - 4, this.iconSize - 8, 9);
            graphics.setColor(white);
            graphics.drawLine(this.iconSize - 6, 3, this.iconSize - 4, 5);
            graphics.drawLine(this.iconSize - 4, 5, this.iconSize - 4, 6);
            graphics.drawLine(this.iconSize - 2, 7, this.iconSize - 1, 7);
            graphics.drawLine(this.iconSize - 1, 2, this.iconSize - 1, 6);
            graphics.setColor(colorUIResource);
            graphics.drawLine(3, this.iconSize - 4, this.iconSize - 3, 2);
            graphics.drawLine(3, this.iconSize - 3, this.iconSize - 2, 2);
            graphics.drawLine(4, this.iconSize - 3, 5, this.iconSize - 3);
            graphics.drawLine(this.iconSize - 7, 8, this.iconSize - 7, 9);
            graphics.drawLine(this.iconSize - 6, 2, this.iconSize - 4, 2);
            graphics.drawRect(this.iconSize - 3, 3, 1, 3);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return this.iconSize;
        }
        
        @Override
        public int getIconHeight() {
            return this.iconSize;
        }
    }
    
    private static class InternalFrameMinimizeIcon implements Icon, UIResource, Serializable
    {
        int iconSize;
        
        public InternalFrameMinimizeIcon(final int iconSize) {
            this.iconSize = 16;
            this.iconSize = iconSize;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final JButton button = (JButton)component;
            final ButtonModel model = button.getModel();
            ColorUIResource primaryControl = MetalLookAndFeel.getPrimaryControl();
            ColorUIResource color = MetalLookAndFeel.getPrimaryControl();
            ColorUIResource colorUIResource = MetalLookAndFeel.getPrimaryControlDarkShadow();
            final ColorUIResource black = MetalLookAndFeel.getBlack();
            ColorUIResource color2 = MetalLookAndFeel.getWhite();
            final ColorUIResource white = MetalLookAndFeel.getWhite();
            if (button.getClientProperty("paintActive") != Boolean.TRUE) {
                primaryControl = (color = MetalLookAndFeel.getControl());
                colorUIResource = MetalLookAndFeel.getControlDarkShadow();
                if (model.isPressed() && model.isArmed()) {
                    color = (color2 = MetalLookAndFeel.getControlShadow());
                    colorUIResource = black;
                }
            }
            else if (model.isPressed() && model.isArmed()) {
                color = (color2 = MetalLookAndFeel.getPrimaryControlShadow());
                colorUIResource = black;
            }
            graphics.translate(n, n2);
            graphics.setColor(primaryControl);
            graphics.fillRect(0, 0, this.iconSize, this.iconSize);
            graphics.setColor(color);
            graphics.fillRect(4, 11, this.iconSize - 13, this.iconSize - 13);
            graphics.setColor(white);
            graphics.drawRect(2, 10, this.iconSize - 10, this.iconSize - 11);
            graphics.setColor(color2);
            graphics.drawRect(3, 10, this.iconSize - 12, this.iconSize - 12);
            graphics.setColor(black);
            graphics.drawRect(1, 8, this.iconSize - 10, this.iconSize - 10);
            graphics.drawRect(2, 9, this.iconSize - 12, this.iconSize - 12);
            graphics.setColor(colorUIResource);
            graphics.drawRect(2, 9, this.iconSize - 11, this.iconSize - 11);
            graphics.drawLine(this.iconSize - 10, 10, this.iconSize - 10, 10);
            graphics.drawLine(3, this.iconSize - 3, 3, this.iconSize - 3);
            graphics.setColor(colorUIResource);
            graphics.fillRect(this.iconSize - 7, 3, 3, 5);
            graphics.drawLine(this.iconSize - 6, 5, this.iconSize - 3, 2);
            graphics.drawLine(this.iconSize - 6, 6, this.iconSize - 2, 2);
            graphics.drawLine(this.iconSize - 6, 7, this.iconSize - 3, 7);
            graphics.setColor(black);
            graphics.drawLine(this.iconSize - 8, 2, this.iconSize - 7, 2);
            graphics.drawLine(this.iconSize - 8, 3, this.iconSize - 8, 7);
            graphics.drawLine(this.iconSize - 6, 4, this.iconSize - 3, 1);
            graphics.drawLine(this.iconSize - 4, 6, this.iconSize - 3, 6);
            graphics.setColor(white);
            graphics.drawLine(this.iconSize - 6, 3, this.iconSize - 6, 3);
            graphics.drawLine(this.iconSize - 4, 5, this.iconSize - 2, 3);
            graphics.drawLine(this.iconSize - 7, 8, this.iconSize - 3, 8);
            graphics.drawLine(this.iconSize - 2, 8, this.iconSize - 2, 7);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return this.iconSize;
        }
        
        @Override
        public int getIconHeight() {
            return this.iconSize;
        }
    }
    
    private static class CheckBoxIcon implements Icon, UIResource, Serializable
    {
        protected int getControlSize() {
            return 13;
        }
        
        private void paintOceanIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final ButtonModel model = ((JCheckBox)component).getModel();
            graphics.translate(n, n2);
            final int iconWidth = this.getIconWidth();
            final int iconHeight = this.getIconHeight();
            if (model.isEnabled()) {
                if (model.isPressed() && model.isArmed()) {
                    graphics.setColor(MetalLookAndFeel.getControlShadow());
                    graphics.fillRect(0, 0, iconWidth, iconHeight);
                    graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                    graphics.fillRect(0, 0, iconWidth, 2);
                    graphics.fillRect(0, 2, 2, iconHeight - 2);
                    graphics.fillRect(iconWidth - 1, 1, 1, iconHeight - 1);
                    graphics.fillRect(1, iconHeight - 1, iconWidth - 2, 1);
                }
                else if (model.isRollover()) {
                    MetalUtils.drawGradient(component, graphics, "CheckBox.gradient", 0, 0, iconWidth, iconHeight, true);
                    graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                    graphics.drawRect(0, 0, iconWidth - 1, iconHeight - 1);
                    graphics.setColor(MetalLookAndFeel.getPrimaryControl());
                    graphics.drawRect(1, 1, iconWidth - 3, iconHeight - 3);
                    graphics.drawRect(2, 2, iconWidth - 5, iconHeight - 5);
                }
                else {
                    MetalUtils.drawGradient(component, graphics, "CheckBox.gradient", 0, 0, iconWidth, iconHeight, true);
                    graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                    graphics.drawRect(0, 0, iconWidth - 1, iconHeight - 1);
                }
                graphics.setColor(MetalLookAndFeel.getControlInfo());
            }
            else {
                graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                graphics.drawRect(0, 0, iconWidth - 1, iconHeight - 1);
            }
            graphics.translate(-n, -n2);
            if (model.isSelected()) {
                this.drawCheck(component, graphics, n, n2);
            }
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            if (MetalLookAndFeel.usingOcean()) {
                this.paintOceanIcon(component, graphics, n, n2);
                return;
            }
            final ButtonModel model = ((JCheckBox)component).getModel();
            final int controlSize = this.getControlSize();
            if (model.isEnabled()) {
                if (model.isPressed() && model.isArmed()) {
                    graphics.setColor(MetalLookAndFeel.getControlShadow());
                    graphics.fillRect(n, n2, controlSize - 1, controlSize - 1);
                    MetalUtils.drawPressed3DBorder(graphics, n, n2, controlSize, controlSize);
                }
                else {
                    MetalUtils.drawFlush3DBorder(graphics, n, n2, controlSize, controlSize);
                }
                graphics.setColor(component.getForeground());
            }
            else {
                graphics.setColor(MetalLookAndFeel.getControlShadow());
                graphics.drawRect(n, n2, controlSize - 2, controlSize - 2);
            }
            if (model.isSelected()) {
                this.drawCheck(component, graphics, n, n2);
            }
        }
        
        protected void drawCheck(final Component component, final Graphics graphics, final int n, final int n2) {
            final int controlSize = this.getControlSize();
            graphics.fillRect(n + 3, n2 + 5, 2, controlSize - 8);
            graphics.drawLine(n + (controlSize - 4), n2 + 3, n + 5, n2 + (controlSize - 6));
            graphics.drawLine(n + (controlSize - 4), n2 + 4, n + 5, n2 + (controlSize - 5));
        }
        
        @Override
        public int getIconWidth() {
            return this.getControlSize();
        }
        
        @Override
        public int getIconHeight() {
            return this.getControlSize();
        }
    }
    
    private static class RadioButtonIcon implements Icon, UIResource, Serializable
    {
        public void paintOceanIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final ButtonModel model = ((JRadioButton)component).getModel();
            final boolean enabled = model.isEnabled();
            final boolean b = enabled && model.isPressed() && model.isArmed();
            final boolean b2 = enabled && model.isRollover();
            graphics.translate(n, n2);
            if (enabled && !b) {
                MetalUtils.drawGradient(component, graphics, "RadioButton.gradient", 1, 1, 10, 10, true);
                graphics.setColor(component.getBackground());
                graphics.fillRect(1, 1, 1, 1);
                graphics.fillRect(10, 1, 1, 1);
                graphics.fillRect(1, 10, 1, 1);
                graphics.fillRect(10, 10, 1, 1);
            }
            else if (b || !enabled) {
                if (b) {
                    graphics.setColor(MetalLookAndFeel.getPrimaryControl());
                }
                else {
                    graphics.setColor(MetalLookAndFeel.getControl());
                }
                graphics.fillRect(2, 2, 8, 8);
                graphics.fillRect(4, 1, 4, 1);
                graphics.fillRect(4, 10, 4, 1);
                graphics.fillRect(1, 4, 1, 4);
                graphics.fillRect(10, 4, 1, 4);
            }
            if (!enabled) {
                graphics.setColor(MetalLookAndFeel.getInactiveControlTextColor());
            }
            else {
                graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
            }
            graphics.drawLine(4, 0, 7, 0);
            graphics.drawLine(8, 1, 9, 1);
            graphics.drawLine(10, 2, 10, 3);
            graphics.drawLine(11, 4, 11, 7);
            graphics.drawLine(10, 8, 10, 9);
            graphics.drawLine(9, 10, 8, 10);
            graphics.drawLine(7, 11, 4, 11);
            graphics.drawLine(3, 10, 2, 10);
            graphics.drawLine(1, 9, 1, 8);
            graphics.drawLine(0, 7, 0, 4);
            graphics.drawLine(1, 3, 1, 2);
            graphics.drawLine(2, 1, 3, 1);
            if (b) {
                graphics.fillRect(1, 4, 1, 4);
                graphics.fillRect(2, 2, 1, 2);
                graphics.fillRect(3, 2, 1, 1);
                graphics.fillRect(4, 1, 4, 1);
            }
            else if (b2) {
                graphics.setColor(MetalLookAndFeel.getPrimaryControl());
                graphics.fillRect(4, 1, 4, 2);
                graphics.fillRect(8, 2, 2, 2);
                graphics.fillRect(9, 4, 2, 4);
                graphics.fillRect(8, 8, 2, 2);
                graphics.fillRect(4, 9, 4, 2);
                graphics.fillRect(2, 8, 2, 2);
                graphics.fillRect(1, 4, 2, 4);
                graphics.fillRect(2, 2, 2, 2);
            }
            if (model.isSelected()) {
                if (enabled) {
                    graphics.setColor(MetalLookAndFeel.getControlInfo());
                }
                else {
                    graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                }
                graphics.fillRect(4, 4, 4, 4);
                graphics.drawLine(4, 3, 7, 3);
                graphics.drawLine(8, 4, 8, 7);
                graphics.drawLine(7, 8, 4, 8);
                graphics.drawLine(3, 7, 3, 4);
            }
            graphics.translate(-n, -n2);
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            if (MetalLookAndFeel.usingOcean()) {
                this.paintOceanIcon(component, graphics, n, n2);
                return;
            }
            final ButtonModel model = ((JRadioButton)component).getModel();
            final boolean selected = model.isSelected();
            final Color background = component.getBackground();
            Color foreground = component.getForeground();
            final ColorUIResource controlShadow = MetalLookAndFeel.getControlShadow();
            ColorUIResource controlDarkShadow = MetalLookAndFeel.getControlDarkShadow();
            Color controlHighlight = MetalLookAndFeel.getControlHighlight();
            Color controlHighlight2 = MetalLookAndFeel.getControlHighlight();
            Color color = background;
            if (!model.isEnabled()) {
                controlHighlight2 = (controlHighlight = background);
                foreground = (controlDarkShadow = controlShadow);
            }
            else if (model.isPressed() && model.isArmed()) {
                color = (controlHighlight = controlShadow);
            }
            graphics.translate(n, n2);
            graphics.setColor(color);
            graphics.fillRect(2, 2, 9, 9);
            graphics.setColor(controlDarkShadow);
            graphics.drawLine(4, 0, 7, 0);
            graphics.drawLine(8, 1, 9, 1);
            graphics.drawLine(10, 2, 10, 3);
            graphics.drawLine(11, 4, 11, 7);
            graphics.drawLine(10, 8, 10, 9);
            graphics.drawLine(9, 10, 8, 10);
            graphics.drawLine(7, 11, 4, 11);
            graphics.drawLine(3, 10, 2, 10);
            graphics.drawLine(1, 9, 1, 8);
            graphics.drawLine(0, 7, 0, 4);
            graphics.drawLine(1, 3, 1, 2);
            graphics.drawLine(2, 1, 3, 1);
            graphics.setColor(controlHighlight);
            graphics.drawLine(2, 9, 2, 8);
            graphics.drawLine(1, 7, 1, 4);
            graphics.drawLine(2, 2, 2, 3);
            graphics.drawLine(2, 2, 3, 2);
            graphics.drawLine(4, 1, 7, 1);
            graphics.drawLine(8, 2, 9, 2);
            graphics.setColor(controlHighlight2);
            graphics.drawLine(10, 1, 10, 1);
            graphics.drawLine(11, 2, 11, 3);
            graphics.drawLine(12, 4, 12, 7);
            graphics.drawLine(11, 8, 11, 9);
            graphics.drawLine(10, 10, 10, 10);
            graphics.drawLine(9, 11, 8, 11);
            graphics.drawLine(7, 12, 4, 12);
            graphics.drawLine(3, 11, 2, 11);
            if (selected) {
                graphics.setColor(foreground);
                graphics.fillRect(4, 4, 4, 4);
                graphics.drawLine(4, 3, 7, 3);
                graphics.drawLine(8, 4, 8, 7);
                graphics.drawLine(7, 8, 4, 8);
                graphics.drawLine(3, 7, 3, 4);
            }
            graphics.translate(-n, -n2);
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
    
    private static class TreeComputerIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            graphics.translate(n, n2);
            graphics.setColor(MetalLookAndFeel.getPrimaryControl());
            graphics.fillRect(5, 4, 6, 4);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
            graphics.drawLine(2, 2, 2, 8);
            graphics.drawLine(13, 2, 13, 8);
            graphics.drawLine(3, 1, 12, 1);
            graphics.drawLine(12, 9, 12, 9);
            graphics.drawLine(3, 9, 3, 9);
            graphics.drawLine(4, 4, 4, 7);
            graphics.drawLine(5, 3, 10, 3);
            graphics.drawLine(11, 4, 11, 7);
            graphics.drawLine(5, 8, 10, 8);
            graphics.drawLine(1, 10, 14, 10);
            graphics.drawLine(14, 10, 14, 14);
            graphics.drawLine(1, 14, 14, 14);
            graphics.drawLine(1, 10, 1, 14);
            graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
            graphics.drawLine(6, 12, 8, 12);
            graphics.drawLine(10, 12, 12, 12);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return 16;
        }
        
        @Override
        public int getIconHeight() {
            return 16;
        }
    }
    
    private static class TreeHardDriveIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            graphics.translate(n, n2);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
            graphics.drawLine(1, 4, 1, 5);
            graphics.drawLine(2, 3, 3, 3);
            graphics.drawLine(4, 2, 11, 2);
            graphics.drawLine(12, 3, 13, 3);
            graphics.drawLine(14, 4, 14, 5);
            graphics.drawLine(12, 6, 13, 6);
            graphics.drawLine(4, 7, 11, 7);
            graphics.drawLine(2, 6, 3, 6);
            graphics.drawLine(1, 7, 1, 8);
            graphics.drawLine(2, 9, 3, 9);
            graphics.drawLine(4, 10, 11, 10);
            graphics.drawLine(12, 9, 13, 9);
            graphics.drawLine(14, 7, 14, 8);
            graphics.drawLine(1, 10, 1, 11);
            graphics.drawLine(2, 12, 3, 12);
            graphics.drawLine(4, 13, 11, 13);
            graphics.drawLine(12, 12, 13, 12);
            graphics.drawLine(14, 10, 14, 11);
            graphics.setColor(MetalLookAndFeel.getControlShadow());
            graphics.drawLine(7, 6, 7, 6);
            graphics.drawLine(9, 6, 9, 6);
            graphics.drawLine(10, 5, 10, 5);
            graphics.drawLine(11, 6, 11, 6);
            graphics.drawLine(12, 5, 13, 5);
            graphics.drawLine(13, 4, 13, 4);
            graphics.drawLine(7, 9, 7, 9);
            graphics.drawLine(9, 9, 9, 9);
            graphics.drawLine(10, 8, 10, 8);
            graphics.drawLine(11, 9, 11, 9);
            graphics.drawLine(12, 8, 13, 8);
            graphics.drawLine(13, 7, 13, 7);
            graphics.drawLine(7, 12, 7, 12);
            graphics.drawLine(9, 12, 9, 12);
            graphics.drawLine(10, 11, 10, 11);
            graphics.drawLine(11, 12, 11, 12);
            graphics.drawLine(12, 11, 13, 11);
            graphics.drawLine(13, 10, 13, 10);
            graphics.setColor(MetalLookAndFeel.getControlHighlight());
            graphics.drawLine(4, 3, 5, 3);
            graphics.drawLine(7, 3, 9, 3);
            graphics.drawLine(11, 3, 11, 3);
            graphics.drawLine(2, 4, 6, 4);
            graphics.drawLine(8, 4, 8, 4);
            graphics.drawLine(2, 5, 3, 5);
            graphics.drawLine(4, 6, 4, 6);
            graphics.drawLine(2, 7, 3, 7);
            graphics.drawLine(2, 8, 3, 8);
            graphics.drawLine(4, 9, 4, 9);
            graphics.drawLine(2, 10, 3, 10);
            graphics.drawLine(2, 11, 3, 11);
            graphics.drawLine(4, 12, 4, 12);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return 16;
        }
        
        @Override
        public int getIconHeight() {
            return 16;
        }
    }
    
    private static class TreeFloppyDriveIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            graphics.translate(n, n2);
            graphics.setColor(MetalLookAndFeel.getPrimaryControl());
            graphics.fillRect(2, 2, 12, 12);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
            graphics.drawLine(1, 1, 13, 1);
            graphics.drawLine(14, 2, 14, 14);
            graphics.drawLine(1, 14, 14, 14);
            graphics.drawLine(1, 1, 1, 14);
            graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
            graphics.fillRect(5, 2, 6, 5);
            graphics.drawLine(4, 8, 11, 8);
            graphics.drawLine(3, 9, 3, 13);
            graphics.drawLine(12, 9, 12, 13);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
            graphics.fillRect(8, 3, 2, 3);
            graphics.fillRect(4, 9, 8, 5);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
            graphics.drawLine(5, 10, 9, 10);
            graphics.drawLine(5, 12, 8, 12);
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return 16;
        }
        
        @Override
        public int getIconHeight() {
            return 16;
        }
    }
    
    static class ImageCacher
    {
        Vector<ImageGcPair> images;
        ImageGcPair currentImageGcPair;
        
        ImageCacher() {
            this.images = new Vector<ImageGcPair>(1, 1);
        }
        
        Image getImage(final GraphicsConfiguration graphicsConfiguration) {
            if (this.currentImageGcPair == null || !this.currentImageGcPair.hasSameConfiguration(graphicsConfiguration)) {
                for (final ImageGcPair currentImageGcPair : this.images) {
                    if (currentImageGcPair.hasSameConfiguration(graphicsConfiguration)) {
                        this.currentImageGcPair = currentImageGcPair;
                        return currentImageGcPair.image;
                    }
                }
                return null;
            }
            return this.currentImageGcPair.image;
        }
        
        void cacheImage(final Image image, final GraphicsConfiguration graphicsConfiguration) {
            final ImageGcPair currentImageGcPair = new ImageGcPair(image, graphicsConfiguration);
            this.images.addElement(currentImageGcPair);
            this.currentImageGcPair = currentImageGcPair;
        }
        
        class ImageGcPair
        {
            Image image;
            GraphicsConfiguration gc;
            
            ImageGcPair(final Image image, final GraphicsConfiguration gc) {
                this.image = image;
                this.gc = gc;
            }
            
            boolean hasSameConfiguration(final GraphicsConfiguration graphicsConfiguration) {
                return (graphicsConfiguration != null && graphicsConfiguration.equals(this.gc)) || (graphicsConfiguration == null && this.gc == null);
            }
        }
    }
    
    public static class FolderIcon16 implements Icon, Serializable
    {
        ImageCacher imageCacher;
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final GraphicsConfiguration graphicsConfiguration = component.getGraphicsConfiguration();
            if (this.imageCacher == null) {
                this.imageCacher = new ImageCacher();
            }
            Image image = this.imageCacher.getImage(graphicsConfiguration);
            if (image == null) {
                if (graphicsConfiguration != null) {
                    image = graphicsConfiguration.createCompatibleImage(this.getIconWidth(), this.getIconHeight(), 2);
                }
                else {
                    image = new BufferedImage(this.getIconWidth(), this.getIconHeight(), 2);
                }
                final Graphics graphics2 = image.getGraphics();
                this.paintMe(component, graphics2);
                graphics2.dispose();
                this.imageCacher.cacheImage(image, graphicsConfiguration);
            }
            graphics.drawImage(image, n, n2 + this.getShift(), null);
        }
        
        private void paintMe(final Component component, final Graphics graphics) {
            final int n = MetalIconFactory.folderIcon16Size.width - 1;
            final int n2 = MetalIconFactory.folderIcon16Size.height - 1;
            graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            graphics.drawLine(n - 5, 3, n, 3);
            graphics.drawLine(n - 6, 4, n, 4);
            graphics.setColor(MetalLookAndFeel.getPrimaryControl());
            graphics.fillRect(2, 7, 13, 8);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
            graphics.drawLine(n - 6, 5, n - 1, 5);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
            graphics.drawLine(0, 6, 0, n2);
            graphics.drawLine(1, 5, n - 7, 5);
            graphics.drawLine(n - 6, 6, n - 1, 6);
            graphics.drawLine(n, 5, n, n2);
            graphics.drawLine(0, n2, n, n2);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
            graphics.drawLine(1, 6, 1, n2 - 1);
            graphics.drawLine(1, 6, n - 7, 6);
            graphics.drawLine(n - 6, 7, n - 1, 7);
        }
        
        public int getShift() {
            return 0;
        }
        
        public int getAdditionalHeight() {
            return 0;
        }
        
        @Override
        public int getIconWidth() {
            return MetalIconFactory.folderIcon16Size.width;
        }
        
        @Override
        public int getIconHeight() {
            return MetalIconFactory.folderIcon16Size.height + this.getAdditionalHeight();
        }
    }
    
    public static class TreeFolderIcon extends FolderIcon16
    {
        @Override
        public int getShift() {
            return -1;
        }
        
        @Override
        public int getAdditionalHeight() {
            return 2;
        }
    }
    
    public static class FileIcon16 implements Icon, Serializable
    {
        ImageCacher imageCacher;
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final GraphicsConfiguration graphicsConfiguration = component.getGraphicsConfiguration();
            if (this.imageCacher == null) {
                this.imageCacher = new ImageCacher();
            }
            Image image = this.imageCacher.getImage(graphicsConfiguration);
            if (image == null) {
                if (graphicsConfiguration != null) {
                    image = graphicsConfiguration.createCompatibleImage(this.getIconWidth(), this.getIconHeight(), 2);
                }
                else {
                    image = new BufferedImage(this.getIconWidth(), this.getIconHeight(), 2);
                }
                final Graphics graphics2 = image.getGraphics();
                this.paintMe(component, graphics2);
                graphics2.dispose();
                this.imageCacher.cacheImage(image, graphicsConfiguration);
            }
            graphics.drawImage(image, n, n2 + this.getShift(), null);
        }
        
        private void paintMe(final Component component, final Graphics graphics) {
            final int n = MetalIconFactory.fileIcon16Size.width - 1;
            final int n2 = MetalIconFactory.fileIcon16Size.height - 1;
            graphics.setColor(MetalLookAndFeel.getWindowBackground());
            graphics.fillRect(4, 2, 9, 12);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
            graphics.drawLine(2, 0, 2, n2);
            graphics.drawLine(2, 0, n - 4, 0);
            graphics.drawLine(2, n2, n - 1, n2);
            graphics.drawLine(n - 1, 6, n - 1, n2);
            graphics.drawLine(n - 6, 2, n - 2, 6);
            graphics.drawLine(n - 5, 1, n - 4, 1);
            graphics.drawLine(n - 3, 2, n - 3, 3);
            graphics.drawLine(n - 2, 4, n - 2, 5);
            graphics.setColor(MetalLookAndFeel.getPrimaryControl());
            graphics.drawLine(3, 1, 3, n2 - 1);
            graphics.drawLine(3, 1, n - 6, 1);
            graphics.drawLine(n - 2, 7, n - 2, n2 - 1);
            graphics.drawLine(n - 5, 2, n - 3, 4);
            graphics.drawLine(3, n2 - 1, n - 2, n2 - 1);
        }
        
        public int getShift() {
            return 0;
        }
        
        public int getAdditionalHeight() {
            return 0;
        }
        
        @Override
        public int getIconWidth() {
            return MetalIconFactory.fileIcon16Size.width;
        }
        
        @Override
        public int getIconHeight() {
            return MetalIconFactory.fileIcon16Size.height + this.getAdditionalHeight();
        }
    }
    
    public static class TreeLeafIcon extends FileIcon16
    {
        @Override
        public int getShift() {
            return 2;
        }
        
        @Override
        public int getAdditionalHeight() {
            return 4;
        }
    }
    
    public static class TreeControlIcon implements Icon, Serializable
    {
        protected boolean isLight;
        ImageCacher imageCacher;
        transient boolean cachedOrientation;
        
        public TreeControlIcon(final boolean isLight) {
            this.cachedOrientation = true;
            this.isLight = isLight;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final GraphicsConfiguration graphicsConfiguration = component.getGraphicsConfiguration();
            if (this.imageCacher == null) {
                this.imageCacher = new ImageCacher();
            }
            Image image = this.imageCacher.getImage(graphicsConfiguration);
            if (image == null || this.cachedOrientation != MetalUtils.isLeftToRight(component)) {
                this.cachedOrientation = MetalUtils.isLeftToRight(component);
                if (graphicsConfiguration != null) {
                    image = graphicsConfiguration.createCompatibleImage(this.getIconWidth(), this.getIconHeight(), 2);
                }
                else {
                    image = new BufferedImage(this.getIconWidth(), this.getIconHeight(), 2);
                }
                final Graphics graphics2 = image.getGraphics();
                this.paintMe(component, graphics2, n, n2);
                graphics2.dispose();
                this.imageCacher.cacheImage(image, graphicsConfiguration);
            }
            if (MetalUtils.isLeftToRight(component)) {
                if (this.isLight) {
                    graphics.drawImage(image, n + 5, n2 + 3, n + 18, n2 + 13, 4, 3, 17, 13, null);
                }
                else {
                    graphics.drawImage(image, n + 5, n2 + 3, n + 18, n2 + 17, 4, 3, 17, 17, null);
                }
            }
            else if (this.isLight) {
                graphics.drawImage(image, n + 3, n2 + 3, n + 16, n2 + 13, 4, 3, 17, 13, null);
            }
            else {
                graphics.drawImage(image, n + 3, n2 + 3, n + 16, n2 + 17, 4, 3, 17, 17, null);
            }
        }
        
        public void paintMe(final Component component, final Graphics graphics, final int n, final int n2) {
            graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
            final int n3 = MetalUtils.isLeftToRight(component) ? 0 : 4;
            graphics.drawLine(n3 + 4, 6, n3 + 4, 9);
            graphics.drawLine(n3 + 5, 5, n3 + 5, 5);
            graphics.drawLine(n3 + 6, 4, n3 + 9, 4);
            graphics.drawLine(n3 + 10, 5, n3 + 10, 5);
            graphics.drawLine(n3 + 11, 6, n3 + 11, 9);
            graphics.drawLine(n3 + 10, 10, n3 + 10, 10);
            graphics.drawLine(n3 + 6, 11, n3 + 9, 11);
            graphics.drawLine(n3 + 5, 10, n3 + 5, 10);
            graphics.drawLine(n3 + 7, 7, n3 + 8, 7);
            graphics.drawLine(n3 + 7, 8, n3 + 8, 8);
            if (this.isLight) {
                if (MetalUtils.isLeftToRight(component)) {
                    graphics.drawLine(12, 7, 15, 7);
                    graphics.drawLine(12, 8, 15, 8);
                }
                else {
                    graphics.drawLine(4, 7, 7, 7);
                    graphics.drawLine(4, 8, 7, 8);
                }
            }
            else {
                graphics.drawLine(n3 + 7, 12, n3 + 7, 15);
                graphics.drawLine(n3 + 8, 12, n3 + 8, 15);
            }
            graphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            graphics.drawLine(n3 + 5, 6, n3 + 5, 9);
            graphics.drawLine(n3 + 6, 5, n3 + 9, 5);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlShadow());
            graphics.drawLine(n3 + 6, 6, n3 + 6, 6);
            graphics.drawLine(n3 + 9, 6, n3 + 9, 6);
            graphics.drawLine(n3 + 6, 9, n3 + 6, 9);
            graphics.drawLine(n3 + 10, 6, n3 + 10, 9);
            graphics.drawLine(n3 + 6, 10, n3 + 9, 10);
            graphics.setColor(MetalLookAndFeel.getPrimaryControl());
            graphics.drawLine(n3 + 6, 7, n3 + 6, 8);
            graphics.drawLine(n3 + 7, 6, n3 + 8, 6);
            graphics.drawLine(n3 + 9, 7, n3 + 9, 7);
            graphics.drawLine(n3 + 7, 9, n3 + 7, 9);
            graphics.setColor(MetalLookAndFeel.getPrimaryControlHighlight());
            graphics.drawLine(n3 + 8, 9, n3 + 9, 9);
            graphics.drawLine(n3 + 9, 8, n3 + 9, 8);
        }
        
        @Override
        public int getIconWidth() {
            return MetalIconFactory.treeControlSize.width;
        }
        
        @Override
        public int getIconHeight() {
            return MetalIconFactory.treeControlSize.height;
        }
    }
    
    private static class MenuArrowIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final JMenuItem menuItem = (JMenuItem)component;
            final ButtonModel model = menuItem.getModel();
            graphics.translate(n, n2);
            if (!model.isEnabled()) {
                graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
            }
            else if (model.isArmed() || (component instanceof JMenu && model.isSelected())) {
                graphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
            }
            else {
                graphics.setColor(menuItem.getForeground());
            }
            if (MetalUtils.isLeftToRight(menuItem)) {
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
        
        @Override
        public int getIconWidth() {
            return MetalIconFactory.menuArrowIconSize.width;
        }
        
        @Override
        public int getIconHeight() {
            return MetalIconFactory.menuArrowIconSize.height;
        }
    }
    
    private static class MenuItemArrowIcon implements Icon, UIResource, Serializable
    {
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
        }
        
        @Override
        public int getIconWidth() {
            return MetalIconFactory.menuArrowIconSize.width;
        }
        
        @Override
        public int getIconHeight() {
            return MetalIconFactory.menuArrowIconSize.height;
        }
    }
    
    private static class CheckBoxMenuItemIcon implements Icon, UIResource, Serializable
    {
        public void paintOceanIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final ButtonModel model = ((JMenuItem)component).getModel();
            final boolean selected = model.isSelected();
            final boolean enabled = model.isEnabled();
            final boolean pressed = model.isPressed();
            final boolean armed = model.isArmed();
            graphics.translate(n, n2);
            if (enabled) {
                MetalUtils.drawGradient(component, graphics, "CheckBoxMenuItem.gradient", 1, 1, 7, 7, true);
                if (pressed || armed) {
                    graphics.setColor(MetalLookAndFeel.getControlInfo());
                    graphics.drawLine(0, 0, 8, 0);
                    graphics.drawLine(0, 0, 0, 8);
                    graphics.drawLine(8, 2, 8, 8);
                    graphics.drawLine(2, 8, 8, 8);
                    graphics.setColor(MetalLookAndFeel.getPrimaryControl());
                    graphics.drawLine(9, 1, 9, 9);
                    graphics.drawLine(1, 9, 9, 9);
                }
                else {
                    graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                    graphics.drawLine(0, 0, 8, 0);
                    graphics.drawLine(0, 0, 0, 8);
                    graphics.drawLine(8, 2, 8, 8);
                    graphics.drawLine(2, 8, 8, 8);
                    graphics.setColor(MetalLookAndFeel.getControlHighlight());
                    graphics.drawLine(9, 1, 9, 9);
                    graphics.drawLine(1, 9, 9, 9);
                }
            }
            else {
                graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
                graphics.drawRect(0, 0, 8, 8);
            }
            if (selected) {
                if (enabled) {
                    if (armed || (component instanceof JMenu && selected)) {
                        graphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
                    }
                    else {
                        graphics.setColor(MetalLookAndFeel.getControlInfo());
                    }
                }
                else {
                    graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
                }
                graphics.drawLine(2, 2, 2, 6);
                graphics.drawLine(3, 2, 3, 6);
                graphics.drawLine(4, 4, 8, 0);
                graphics.drawLine(4, 5, 9, 0);
            }
            graphics.translate(-n, -n2);
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            if (MetalLookAndFeel.usingOcean()) {
                this.paintOceanIcon(component, graphics, n, n2);
                return;
            }
            final JMenuItem menuItem = (JMenuItem)component;
            final ButtonModel model = menuItem.getModel();
            final boolean selected = model.isSelected();
            final boolean enabled = model.isEnabled();
            final boolean pressed = model.isPressed();
            final boolean armed = model.isArmed();
            graphics.translate(n, n2);
            if (enabled) {
                if (pressed || armed) {
                    graphics.setColor(MetalLookAndFeel.getControlInfo());
                    graphics.drawLine(0, 0, 8, 0);
                    graphics.drawLine(0, 0, 0, 8);
                    graphics.drawLine(8, 2, 8, 8);
                    graphics.drawLine(2, 8, 8, 8);
                    graphics.setColor(MetalLookAndFeel.getPrimaryControl());
                    graphics.drawLine(1, 1, 7, 1);
                    graphics.drawLine(1, 1, 1, 7);
                    graphics.drawLine(9, 1, 9, 9);
                    graphics.drawLine(1, 9, 9, 9);
                }
                else {
                    graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                    graphics.drawLine(0, 0, 8, 0);
                    graphics.drawLine(0, 0, 0, 8);
                    graphics.drawLine(8, 2, 8, 8);
                    graphics.drawLine(2, 8, 8, 8);
                    graphics.setColor(MetalLookAndFeel.getControlHighlight());
                    graphics.drawLine(1, 1, 7, 1);
                    graphics.drawLine(1, 1, 1, 7);
                    graphics.drawLine(9, 1, 9, 9);
                    graphics.drawLine(1, 9, 9, 9);
                }
            }
            else {
                graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
                graphics.drawRect(0, 0, 8, 8);
            }
            if (selected) {
                if (enabled) {
                    if (model.isArmed() || (component instanceof JMenu && model.isSelected())) {
                        graphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
                    }
                    else {
                        graphics.setColor(menuItem.getForeground());
                    }
                }
                else {
                    graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
                }
                graphics.drawLine(2, 2, 2, 6);
                graphics.drawLine(3, 2, 3, 6);
                graphics.drawLine(4, 4, 8, 0);
                graphics.drawLine(4, 5, 9, 0);
            }
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return MetalIconFactory.menuCheckIconSize.width;
        }
        
        @Override
        public int getIconHeight() {
            return MetalIconFactory.menuCheckIconSize.height;
        }
    }
    
    private static class RadioButtonMenuItemIcon implements Icon, UIResource, Serializable
    {
        public void paintOceanIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final ButtonModel model = ((JMenuItem)component).getModel();
            final boolean selected = model.isSelected();
            final boolean enabled = model.isEnabled();
            final boolean pressed = model.isPressed();
            final boolean armed = model.isArmed();
            graphics.translate(n, n2);
            if (enabled) {
                MetalUtils.drawGradient(component, graphics, "RadioButtonMenuItem.gradient", 1, 1, 7, 7, true);
                if (pressed || armed) {
                    graphics.setColor(MetalLookAndFeel.getPrimaryControl());
                }
                else {
                    graphics.setColor(MetalLookAndFeel.getControlHighlight());
                }
                graphics.drawLine(2, 9, 7, 9);
                graphics.drawLine(9, 2, 9, 7);
                graphics.drawLine(8, 8, 8, 8);
                if (pressed || armed) {
                    graphics.setColor(MetalLookAndFeel.getControlInfo());
                }
                else {
                    graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                }
            }
            else {
                graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
            }
            graphics.drawLine(2, 0, 6, 0);
            graphics.drawLine(2, 8, 6, 8);
            graphics.drawLine(0, 2, 0, 6);
            graphics.drawLine(8, 2, 8, 6);
            graphics.drawLine(1, 1, 1, 1);
            graphics.drawLine(7, 1, 7, 1);
            graphics.drawLine(1, 7, 1, 7);
            graphics.drawLine(7, 7, 7, 7);
            if (selected) {
                if (enabled) {
                    if (armed || (component instanceof JMenu && model.isSelected())) {
                        graphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
                    }
                    else {
                        graphics.setColor(MetalLookAndFeel.getControlInfo());
                    }
                }
                else {
                    graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
                }
                graphics.drawLine(3, 2, 5, 2);
                graphics.drawLine(2, 3, 6, 3);
                graphics.drawLine(2, 4, 6, 4);
                graphics.drawLine(2, 5, 6, 5);
                graphics.drawLine(3, 6, 5, 6);
            }
            graphics.translate(-n, -n2);
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            if (MetalLookAndFeel.usingOcean()) {
                this.paintOceanIcon(component, graphics, n, n2);
                return;
            }
            final JMenuItem menuItem = (JMenuItem)component;
            final ButtonModel model = menuItem.getModel();
            final boolean selected = model.isSelected();
            final boolean enabled = model.isEnabled();
            final boolean pressed = model.isPressed();
            final boolean armed = model.isArmed();
            graphics.translate(n, n2);
            if (enabled) {
                if (pressed || armed) {
                    graphics.setColor(MetalLookAndFeel.getPrimaryControl());
                    graphics.drawLine(3, 1, 8, 1);
                    graphics.drawLine(2, 9, 7, 9);
                    graphics.drawLine(1, 3, 1, 8);
                    graphics.drawLine(9, 2, 9, 7);
                    graphics.drawLine(2, 2, 2, 2);
                    graphics.drawLine(8, 8, 8, 8);
                    graphics.setColor(MetalLookAndFeel.getControlInfo());
                    graphics.drawLine(2, 0, 6, 0);
                    graphics.drawLine(2, 8, 6, 8);
                    graphics.drawLine(0, 2, 0, 6);
                    graphics.drawLine(8, 2, 8, 6);
                    graphics.drawLine(1, 1, 1, 1);
                    graphics.drawLine(7, 1, 7, 1);
                    graphics.drawLine(1, 7, 1, 7);
                    graphics.drawLine(7, 7, 7, 7);
                }
                else {
                    graphics.setColor(MetalLookAndFeel.getControlHighlight());
                    graphics.drawLine(3, 1, 8, 1);
                    graphics.drawLine(2, 9, 7, 9);
                    graphics.drawLine(1, 3, 1, 8);
                    graphics.drawLine(9, 2, 9, 7);
                    graphics.drawLine(2, 2, 2, 2);
                    graphics.drawLine(8, 8, 8, 8);
                    graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
                    graphics.drawLine(2, 0, 6, 0);
                    graphics.drawLine(2, 8, 6, 8);
                    graphics.drawLine(0, 2, 0, 6);
                    graphics.drawLine(8, 2, 8, 6);
                    graphics.drawLine(1, 1, 1, 1);
                    graphics.drawLine(7, 1, 7, 1);
                    graphics.drawLine(1, 7, 1, 7);
                    graphics.drawLine(7, 7, 7, 7);
                }
            }
            else {
                graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
                graphics.drawLine(2, 0, 6, 0);
                graphics.drawLine(2, 8, 6, 8);
                graphics.drawLine(0, 2, 0, 6);
                graphics.drawLine(8, 2, 8, 6);
                graphics.drawLine(1, 1, 1, 1);
                graphics.drawLine(7, 1, 7, 1);
                graphics.drawLine(1, 7, 1, 7);
                graphics.drawLine(7, 7, 7, 7);
            }
            if (selected) {
                if (enabled) {
                    if (model.isArmed() || (component instanceof JMenu && model.isSelected())) {
                        graphics.setColor(MetalLookAndFeel.getMenuSelectedForeground());
                    }
                    else {
                        graphics.setColor(menuItem.getForeground());
                    }
                }
                else {
                    graphics.setColor(MetalLookAndFeel.getMenuDisabledForeground());
                }
                graphics.drawLine(3, 2, 5, 2);
                graphics.drawLine(2, 3, 6, 3);
                graphics.drawLine(2, 4, 6, 4);
                graphics.drawLine(2, 5, 6, 5);
                graphics.drawLine(3, 6, 5, 6);
            }
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return MetalIconFactory.menuCheckIconSize.width;
        }
        
        @Override
        public int getIconHeight() {
            return MetalIconFactory.menuCheckIconSize.height;
        }
    }
    
    private static class VerticalSliderThumbIcon implements Icon, Serializable, UIResource
    {
        protected static MetalBumps controlBumps;
        protected static MetalBumps primaryBumps;
        
        public VerticalSliderThumbIcon() {
            VerticalSliderThumbIcon.controlBumps = new MetalBumps(6, 10, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlInfo(), MetalLookAndFeel.getControl());
            VerticalSliderThumbIcon.primaryBumps = new MetalBumps(6, 10, MetalLookAndFeel.getPrimaryControl(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlShadow());
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final boolean leftToRight = MetalUtils.isLeftToRight(component);
            graphics.translate(n, n2);
            if (component.hasFocus()) {
                graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
            }
            else {
                graphics.setColor(component.isEnabled() ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
            }
            if (leftToRight) {
                graphics.drawLine(1, 0, 8, 0);
                graphics.drawLine(0, 1, 0, 13);
                graphics.drawLine(1, 14, 8, 14);
                graphics.drawLine(9, 1, 15, 7);
                graphics.drawLine(9, 13, 15, 7);
            }
            else {
                graphics.drawLine(7, 0, 14, 0);
                graphics.drawLine(15, 1, 15, 13);
                graphics.drawLine(7, 14, 14, 14);
                graphics.drawLine(0, 7, 6, 1);
                graphics.drawLine(0, 7, 6, 13);
            }
            if (component.hasFocus()) {
                graphics.setColor(component.getForeground());
            }
            else {
                graphics.setColor(MetalLookAndFeel.getControl());
            }
            if (leftToRight) {
                graphics.fillRect(1, 1, 8, 13);
                graphics.drawLine(9, 2, 9, 12);
                graphics.drawLine(10, 3, 10, 11);
                graphics.drawLine(11, 4, 11, 10);
                graphics.drawLine(12, 5, 12, 9);
                graphics.drawLine(13, 6, 13, 8);
                graphics.drawLine(14, 7, 14, 7);
            }
            else {
                graphics.fillRect(7, 1, 8, 13);
                graphics.drawLine(6, 3, 6, 12);
                graphics.drawLine(5, 4, 5, 11);
                graphics.drawLine(4, 5, 4, 10);
                graphics.drawLine(3, 6, 3, 9);
                graphics.drawLine(2, 7, 2, 8);
            }
            final int n3 = leftToRight ? 2 : 8;
            if (component.isEnabled()) {
                if (component.hasFocus()) {
                    VerticalSliderThumbIcon.primaryBumps.paintIcon(component, graphics, n3, 2);
                }
                else {
                    VerticalSliderThumbIcon.controlBumps.paintIcon(component, graphics, n3, 2);
                }
            }
            if (component.isEnabled()) {
                graphics.setColor(component.hasFocus() ? MetalLookAndFeel.getPrimaryControl() : MetalLookAndFeel.getControlHighlight());
                if (leftToRight) {
                    graphics.drawLine(1, 1, 8, 1);
                    graphics.drawLine(1, 1, 1, 13);
                }
                else {
                    graphics.drawLine(8, 1, 14, 1);
                    graphics.drawLine(1, 7, 7, 1);
                }
            }
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return 16;
        }
        
        @Override
        public int getIconHeight() {
            return 15;
        }
    }
    
    private static class HorizontalSliderThumbIcon implements Icon, Serializable, UIResource
    {
        protected static MetalBumps controlBumps;
        protected static MetalBumps primaryBumps;
        
        public HorizontalSliderThumbIcon() {
            HorizontalSliderThumbIcon.controlBumps = new MetalBumps(10, 6, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlInfo(), MetalLookAndFeel.getControl());
            HorizontalSliderThumbIcon.primaryBumps = new MetalBumps(10, 6, MetalLookAndFeel.getPrimaryControl(), MetalLookAndFeel.getPrimaryControlDarkShadow(), MetalLookAndFeel.getPrimaryControlShadow());
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            graphics.translate(n, n2);
            if (component.hasFocus()) {
                graphics.setColor(MetalLookAndFeel.getPrimaryControlInfo());
            }
            else {
                graphics.setColor(component.isEnabled() ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
            }
            graphics.drawLine(1, 0, 13, 0);
            graphics.drawLine(0, 1, 0, 8);
            graphics.drawLine(14, 1, 14, 8);
            graphics.drawLine(1, 9, 7, 15);
            graphics.drawLine(7, 15, 14, 8);
            if (component.hasFocus()) {
                graphics.setColor(component.getForeground());
            }
            else {
                graphics.setColor(MetalLookAndFeel.getControl());
            }
            graphics.fillRect(1, 1, 13, 8);
            graphics.drawLine(2, 9, 12, 9);
            graphics.drawLine(3, 10, 11, 10);
            graphics.drawLine(4, 11, 10, 11);
            graphics.drawLine(5, 12, 9, 12);
            graphics.drawLine(6, 13, 8, 13);
            graphics.drawLine(7, 14, 7, 14);
            if (component.isEnabled()) {
                if (component.hasFocus()) {
                    HorizontalSliderThumbIcon.primaryBumps.paintIcon(component, graphics, 2, 2);
                }
                else {
                    HorizontalSliderThumbIcon.controlBumps.paintIcon(component, graphics, 2, 2);
                }
            }
            if (component.isEnabled()) {
                graphics.setColor(component.hasFocus() ? MetalLookAndFeel.getPrimaryControl() : MetalLookAndFeel.getControlHighlight());
                graphics.drawLine(1, 1, 13, 1);
                graphics.drawLine(1, 1, 1, 8);
            }
            graphics.translate(-n, -n2);
        }
        
        @Override
        public int getIconWidth() {
            return 15;
        }
        
        @Override
        public int getIconHeight() {
            return 16;
        }
    }
    
    private static class OceanVerticalSliderThumbIcon extends CachedPainter implements Icon, Serializable, UIResource
    {
        private static Polygon LTR_THUMB_SHAPE;
        private static Polygon RTL_THUMB_SHAPE;
        
        OceanVerticalSliderThumbIcon() {
            super(3);
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            if (!(graphics instanceof Graphics2D)) {
                return;
            }
            this.paint(component, graphics, n, n2, this.getIconWidth(), this.getIconHeight(), MetalUtils.isLeftToRight(component), component.hasFocus(), component.isEnabled(), MetalLookAndFeel.getCurrentTheme());
        }
        
        @Override
        protected void paintToImage(final Component component, final Image image, final Graphics graphics, final int n, final int n2, final Object[] array) {
            final Graphics2D graphics2D = (Graphics2D)graphics;
            final boolean booleanValue = (boolean)array[0];
            final boolean booleanValue2 = (boolean)array[1];
            final boolean booleanValue3 = (boolean)array[2];
            final Rectangle clipBounds = graphics2D.getClipBounds();
            if (booleanValue) {
                graphics2D.clip(OceanVerticalSliderThumbIcon.LTR_THUMB_SHAPE);
            }
            else {
                graphics2D.clip(OceanVerticalSliderThumbIcon.RTL_THUMB_SHAPE);
            }
            if (!booleanValue3) {
                graphics2D.setColor(MetalLookAndFeel.getControl());
                graphics2D.fillRect(1, 1, 14, 14);
            }
            else if (booleanValue2) {
                MetalUtils.drawGradient(component, graphics2D, "Slider.focusGradient", 1, 1, 14, 14, false);
            }
            else {
                MetalUtils.drawGradient(component, graphics2D, "Slider.gradient", 1, 1, 14, 14, false);
            }
            graphics2D.setClip(clipBounds);
            if (booleanValue2) {
                graphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            }
            else {
                graphics2D.setColor(booleanValue3 ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
            }
            if (booleanValue) {
                graphics2D.drawLine(1, 0, 8, 0);
                graphics2D.drawLine(0, 1, 0, 13);
                graphics2D.drawLine(1, 14, 8, 14);
                graphics2D.drawLine(9, 1, 15, 7);
                graphics2D.drawLine(9, 13, 15, 7);
            }
            else {
                graphics2D.drawLine(7, 0, 14, 0);
                graphics2D.drawLine(15, 1, 15, 13);
                graphics2D.drawLine(7, 14, 14, 14);
                graphics2D.drawLine(0, 7, 6, 1);
                graphics2D.drawLine(0, 7, 6, 13);
            }
            if (booleanValue2 && booleanValue3) {
                graphics2D.setColor(MetalLookAndFeel.getPrimaryControl());
                if (booleanValue) {
                    graphics2D.drawLine(1, 1, 8, 1);
                    graphics2D.drawLine(1, 1, 1, 13);
                    graphics2D.drawLine(1, 13, 8, 13);
                    graphics2D.drawLine(9, 2, 14, 7);
                    graphics2D.drawLine(9, 12, 14, 7);
                }
                else {
                    graphics2D.drawLine(7, 1, 14, 1);
                    graphics2D.drawLine(14, 1, 14, 13);
                    graphics2D.drawLine(7, 13, 14, 13);
                    graphics2D.drawLine(1, 7, 7, 1);
                    graphics2D.drawLine(1, 7, 7, 13);
                }
            }
        }
        
        @Override
        public int getIconWidth() {
            return 16;
        }
        
        @Override
        public int getIconHeight() {
            return 15;
        }
        
        @Override
        protected Image createImage(final Component component, final int n, final int n2, final GraphicsConfiguration graphicsConfiguration, final Object[] array) {
            if (graphicsConfiguration == null) {
                return new BufferedImage(n, n2, 2);
            }
            return graphicsConfiguration.createCompatibleImage(n, n2, 2);
        }
        
        static {
            OceanVerticalSliderThumbIcon.LTR_THUMB_SHAPE = new Polygon(new int[] { 0, 8, 15, 8, 0 }, new int[] { 0, 0, 7, 14, 14 }, 5);
            OceanVerticalSliderThumbIcon.RTL_THUMB_SHAPE = new Polygon(new int[] { 15, 15, 7, 0, 7 }, new int[] { 0, 14, 14, 7, 0 }, 5);
        }
    }
    
    private static class OceanHorizontalSliderThumbIcon extends CachedPainter implements Icon, Serializable, UIResource
    {
        private static Polygon THUMB_SHAPE;
        
        OceanHorizontalSliderThumbIcon() {
            super(3);
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            if (!(graphics instanceof Graphics2D)) {
                return;
            }
            this.paint(component, graphics, n, n2, this.getIconWidth(), this.getIconHeight(), component.hasFocus(), component.isEnabled(), MetalLookAndFeel.getCurrentTheme());
        }
        
        @Override
        protected Image createImage(final Component component, final int n, final int n2, final GraphicsConfiguration graphicsConfiguration, final Object[] array) {
            if (graphicsConfiguration == null) {
                return new BufferedImage(n, n2, 2);
            }
            return graphicsConfiguration.createCompatibleImage(n, n2, 2);
        }
        
        @Override
        protected void paintToImage(final Component component, final Image image, final Graphics graphics, final int n, final int n2, final Object[] array) {
            final Graphics2D graphics2D = (Graphics2D)graphics;
            final boolean booleanValue = (boolean)array[0];
            final boolean booleanValue2 = (boolean)array[1];
            final Rectangle clipBounds = graphics2D.getClipBounds();
            graphics2D.clip(OceanHorizontalSliderThumbIcon.THUMB_SHAPE);
            if (!booleanValue2) {
                graphics2D.setColor(MetalLookAndFeel.getControl());
                graphics2D.fillRect(1, 1, 13, 14);
            }
            else if (booleanValue) {
                MetalUtils.drawGradient(component, graphics2D, "Slider.focusGradient", 1, 1, 13, 14, true);
            }
            else {
                MetalUtils.drawGradient(component, graphics2D, "Slider.gradient", 1, 1, 13, 14, true);
            }
            graphics2D.setClip(clipBounds);
            if (booleanValue) {
                graphics2D.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
            }
            else {
                graphics2D.setColor(booleanValue2 ? MetalLookAndFeel.getPrimaryControlInfo() : MetalLookAndFeel.getControlDarkShadow());
            }
            graphics2D.drawLine(1, 0, 13, 0);
            graphics2D.drawLine(0, 1, 0, 8);
            graphics2D.drawLine(14, 1, 14, 8);
            graphics2D.drawLine(1, 9, 7, 15);
            graphics2D.drawLine(7, 15, 14, 8);
            if (booleanValue && booleanValue2) {
                graphics2D.setColor(MetalLookAndFeel.getPrimaryControl());
                graphics2D.fillRect(1, 1, 13, 1);
                graphics2D.fillRect(1, 2, 1, 7);
                graphics2D.fillRect(13, 2, 1, 7);
                graphics2D.drawLine(2, 9, 7, 14);
                graphics2D.drawLine(8, 13, 12, 9);
            }
        }
        
        @Override
        public int getIconWidth() {
            return 15;
        }
        
        @Override
        public int getIconHeight() {
            return 16;
        }
        
        static {
            OceanHorizontalSliderThumbIcon.THUMB_SHAPE = new Polygon(new int[] { 0, 14, 14, 7, 0 }, new int[] { 0, 0, 8, 15, 8 }, 5);
        }
    }
}
