package javax.swing.plaf.metal;

import javax.swing.ButtonModel;
import javax.swing.AbstractButton;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.plaf.IconUIResource;
import sun.swing.PrintColorUIResource;
import javax.swing.Icon;
import sun.swing.SwingUtilities2;
import java.util.List;
import java.awt.Insets;
import java.awt.Color;
import java.util.Arrays;
import sun.swing.SwingLazyValue;
import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;

public class OceanTheme extends DefaultMetalTheme
{
    private static final ColorUIResource PRIMARY1;
    private static final ColorUIResource PRIMARY2;
    private static final ColorUIResource PRIMARY3;
    private static final ColorUIResource SECONDARY1;
    private static final ColorUIResource SECONDARY2;
    private static final ColorUIResource SECONDARY3;
    private static final ColorUIResource CONTROL_TEXT_COLOR;
    private static final ColorUIResource INACTIVE_CONTROL_TEXT_COLOR;
    private static final ColorUIResource MENU_DISABLED_FOREGROUND;
    private static final ColorUIResource OCEAN_BLACK;
    private static final ColorUIResource OCEAN_DROP;
    
    @Override
    public void addCustomEntriesToTable(final UIDefaults uiDefaults) {
        final SwingLazyValue swingLazyValue = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[] { this.getPrimary1() });
        final List<Object> list = Arrays.asList(new Float(0.3f), new Float(0.0f), new ColorUIResource(14543091), this.getWhite(), this.getSecondary2());
        final ColorUIResource colorUIResource = new ColorUIResource(13421772);
        final ColorUIResource colorUIResource2 = new ColorUIResource(14342874);
        final ColorUIResource colorUIResource3 = new ColorUIResource(13164018);
        final Object iconResource = this.getIconResource("icons/ocean/directory.gif");
        final Object iconResource2 = this.getIconResource("icons/ocean/file.gif");
        final List<Object> list2 = Arrays.asList(new Float(0.3f), new Float(0.2f), colorUIResource3, this.getWhite(), new ColorUIResource(OceanTheme.SECONDARY2));
        uiDefaults.putDefaults(new Object[] { "Button.gradient", list, "Button.rollover", Boolean.TRUE, "Button.toolBarBorderBackground", OceanTheme.INACTIVE_CONTROL_TEXT_COLOR, "Button.disabledToolBarBorderBackground", colorUIResource, "Button.rolloverIconType", "ocean", "CheckBox.rollover", Boolean.TRUE, "CheckBox.gradient", list, "CheckBoxMenuItem.gradient", list, "FileChooser.homeFolderIcon", this.getIconResource("icons/ocean/homeFolder.gif"), "FileChooser.newFolderIcon", this.getIconResource("icons/ocean/newFolder.gif"), "FileChooser.upFolderIcon", this.getIconResource("icons/ocean/upFolder.gif"), "FileView.computerIcon", this.getIconResource("icons/ocean/computer.gif"), "FileView.directoryIcon", iconResource, "FileView.hardDriveIcon", this.getIconResource("icons/ocean/hardDrive.gif"), "FileView.fileIcon", iconResource2, "FileView.floppyDriveIcon", this.getIconResource("icons/ocean/floppy.gif"), "Label.disabledForeground", this.getInactiveControlTextColor(), "Menu.opaque", Boolean.FALSE, "MenuBar.gradient", Arrays.asList(new Float(1.0f), new Float(0.0f), this.getWhite(), colorUIResource2, new ColorUIResource(colorUIResource2)), "MenuBar.borderColor", colorUIResource, "InternalFrame.activeTitleGradient", list, "InternalFrame.closeIcon", new UIDefaults.LazyValue() {
                @Override
                public Object createValue(final UIDefaults uiDefaults) {
                    return new IFIcon(OceanTheme.this.getHastenedIcon("icons/ocean/close.gif", uiDefaults), OceanTheme.this.getHastenedIcon("icons/ocean/close-pressed.gif", uiDefaults));
                }
            }, "InternalFrame.iconifyIcon", new UIDefaults.LazyValue() {
                @Override
                public Object createValue(final UIDefaults uiDefaults) {
                    return new IFIcon(OceanTheme.this.getHastenedIcon("icons/ocean/iconify.gif", uiDefaults), OceanTheme.this.getHastenedIcon("icons/ocean/iconify-pressed.gif", uiDefaults));
                }
            }, "InternalFrame.minimizeIcon", new UIDefaults.LazyValue() {
                @Override
                public Object createValue(final UIDefaults uiDefaults) {
                    return new IFIcon(OceanTheme.this.getHastenedIcon("icons/ocean/minimize.gif", uiDefaults), OceanTheme.this.getHastenedIcon("icons/ocean/minimize-pressed.gif", uiDefaults));
                }
            }, "InternalFrame.icon", this.getIconResource("icons/ocean/menu.gif"), "InternalFrame.maximizeIcon", new UIDefaults.LazyValue() {
                @Override
                public Object createValue(final UIDefaults uiDefaults) {
                    return new IFIcon(OceanTheme.this.getHastenedIcon("icons/ocean/maximize.gif", uiDefaults), OceanTheme.this.getHastenedIcon("icons/ocean/maximize-pressed.gif", uiDefaults));
                }
            }, "InternalFrame.paletteCloseIcon", new UIDefaults.LazyValue() {
                @Override
                public Object createValue(final UIDefaults uiDefaults) {
                    return new IFIcon(OceanTheme.this.getHastenedIcon("icons/ocean/paletteClose.gif", uiDefaults), OceanTheme.this.getHastenedIcon("icons/ocean/paletteClose-pressed.gif", uiDefaults));
                }
            }, "List.focusCellHighlightBorder", swingLazyValue, "MenuBarUI", "javax.swing.plaf.metal.MetalMenuBarUI", "OptionPane.errorIcon", this.getIconResource("icons/ocean/error.png"), "OptionPane.informationIcon", this.getIconResource("icons/ocean/info.png"), "OptionPane.questionIcon", this.getIconResource("icons/ocean/question.png"), "OptionPane.warningIcon", this.getIconResource("icons/ocean/warning.png"), "RadioButton.gradient", list, "RadioButton.rollover", Boolean.TRUE, "RadioButtonMenuItem.gradient", list, "ScrollBar.gradient", list, "Slider.altTrackColor", new ColorUIResource(13820655), "Slider.gradient", list2, "Slider.focusGradient", list2, "SplitPane.oneTouchButtonsOpaque", Boolean.FALSE, "SplitPane.dividerFocusColor", colorUIResource3, "TabbedPane.borderHightlightColor", this.getPrimary1(), "TabbedPane.contentAreaColor", colorUIResource3, "TabbedPane.contentBorderInsets", new Insets(4, 2, 3, 3), "TabbedPane.selected", colorUIResource3, "TabbedPane.tabAreaBackground", colorUIResource2, "TabbedPane.tabAreaInsets", new Insets(2, 2, 0, 6), "TabbedPane.unselectedBackground", OceanTheme.SECONDARY3, "Table.focusCellHighlightBorder", swingLazyValue, "Table.gridColor", OceanTheme.SECONDARY1, "TableHeader.focusCellBackground", colorUIResource3, "ToggleButton.gradient", list, "ToolBar.borderColor", colorUIResource, "ToolBar.isRollover", Boolean.TRUE, "Tree.closedIcon", iconResource, "Tree.collapsedIcon", new UIDefaults.LazyValue() {
                @Override
                public Object createValue(final UIDefaults uiDefaults) {
                    return new COIcon(OceanTheme.this.getHastenedIcon("icons/ocean/collapsed.gif", uiDefaults), OceanTheme.this.getHastenedIcon("icons/ocean/collapsed-rtl.gif", uiDefaults));
                }
            }, "Tree.expandedIcon", this.getIconResource("icons/ocean/expanded.gif"), "Tree.leafIcon", iconResource2, "Tree.openIcon", iconResource, "Tree.selectionBorderColor", this.getPrimary1(), "Tree.dropLineColor", this.getPrimary1(), "Table.dropLineColor", this.getPrimary1(), "Table.dropLineShortColor", OceanTheme.OCEAN_BLACK, "Table.dropCellBackground", OceanTheme.OCEAN_DROP, "Tree.dropCellBackground", OceanTheme.OCEAN_DROP, "List.dropCellBackground", OceanTheme.OCEAN_DROP, "List.dropLineColor", this.getPrimary1() });
    }
    
    @Override
    boolean isSystemTheme() {
        return true;
    }
    
    @Override
    public String getName() {
        return "Ocean";
    }
    
    @Override
    protected ColorUIResource getPrimary1() {
        return OceanTheme.PRIMARY1;
    }
    
    @Override
    protected ColorUIResource getPrimary2() {
        return OceanTheme.PRIMARY2;
    }
    
    @Override
    protected ColorUIResource getPrimary3() {
        return OceanTheme.PRIMARY3;
    }
    
    @Override
    protected ColorUIResource getSecondary1() {
        return OceanTheme.SECONDARY1;
    }
    
    @Override
    protected ColorUIResource getSecondary2() {
        return OceanTheme.SECONDARY2;
    }
    
    @Override
    protected ColorUIResource getSecondary3() {
        return OceanTheme.SECONDARY3;
    }
    
    @Override
    protected ColorUIResource getBlack() {
        return OceanTheme.OCEAN_BLACK;
    }
    
    @Override
    public ColorUIResource getDesktopColor() {
        return MetalTheme.white;
    }
    
    @Override
    public ColorUIResource getInactiveControlTextColor() {
        return OceanTheme.INACTIVE_CONTROL_TEXT_COLOR;
    }
    
    @Override
    public ColorUIResource getControlTextColor() {
        return OceanTheme.CONTROL_TEXT_COLOR;
    }
    
    @Override
    public ColorUIResource getMenuDisabledForeground() {
        return OceanTheme.MENU_DISABLED_FOREGROUND;
    }
    
    private Object getIconResource(final String s) {
        return SwingUtilities2.makeIcon(this.getClass(), OceanTheme.class, s);
    }
    
    private Icon getHastenedIcon(final String s, final UIDefaults uiDefaults) {
        return (Icon)((UIDefaults.LazyValue)this.getIconResource(s)).createValue(uiDefaults);
    }
    
    static {
        PRIMARY1 = new ColorUIResource(6521535);
        PRIMARY2 = new ColorUIResource(10729676);
        PRIMARY3 = new ColorUIResource(12111845);
        SECONDARY1 = new ColorUIResource(8030873);
        SECONDARY2 = new ColorUIResource(12111845);
        SECONDARY3 = new ColorUIResource(15658734);
        CONTROL_TEXT_COLOR = new PrintColorUIResource(3355443, Color.BLACK);
        INACTIVE_CONTROL_TEXT_COLOR = new ColorUIResource(10066329);
        MENU_DISABLED_FOREGROUND = new ColorUIResource(10066329);
        OCEAN_BLACK = new PrintColorUIResource(3355443, Color.BLACK);
        OCEAN_DROP = new ColorUIResource(13822463);
    }
    
    private static class COIcon extends IconUIResource
    {
        private Icon rtl;
        
        public COIcon(final Icon icon, final Icon rtl) {
            super(icon);
            this.rtl = rtl;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            if (MetalUtils.isLeftToRight(component)) {
                super.paintIcon(component, graphics, n, n2);
            }
            else {
                this.rtl.paintIcon(component, graphics, n, n2);
            }
        }
    }
    
    private static class IFIcon extends IconUIResource
    {
        private Icon pressed;
        
        public IFIcon(final Icon icon, final Icon pressed) {
            super(icon);
            this.pressed = pressed;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final ButtonModel model = ((AbstractButton)component).getModel();
            if (model.isPressed() && model.isArmed()) {
                this.pressed.paintIcon(component, graphics, n, n2);
            }
            else {
                super.paintIcon(component, graphics, n, n2);
            }
        }
    }
}
