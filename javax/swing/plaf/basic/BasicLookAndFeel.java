package javax.swing.plaf.basic;

import java.awt.Point;
import javax.swing.MenuElement;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import java.awt.Window;
import javax.swing.SwingUtilities;
import java.awt.Component;
import javax.swing.MenuSelectionManager;
import java.awt.event.MouseEvent;
import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Line;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import javax.sound.sampled.LineListener;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import javax.swing.Action;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.UIManager;
import javax.swing.ActionMap;
import javax.swing.plaf.ComponentUI;
import sun.awt.SunToolkit;
import java.awt.Toolkit;
import java.awt.Dimension;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.DefaultListCellRenderer;
import sun.swing.SwingUtilities2;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.InsetsUIResource;
import sun.swing.SwingLazyValue;
import java.util.Locale;
import javax.swing.plaf.ColorUIResource;
import java.awt.SystemColor;
import java.awt.Color;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.awt.AppContext;
import java.beans.PropertyChangeEvent;
import javax.swing.UIDefaults;
import java.beans.PropertyChangeListener;
import javax.sound.sampled.Clip;
import java.io.Serializable;
import javax.swing.LookAndFeel;

public abstract class BasicLookAndFeel extends LookAndFeel implements Serializable
{
    static boolean needsEventHelper;
    private transient Object audioLock;
    private Clip clipPlaying;
    AWTEventHelper invocator;
    private PropertyChangeListener disposer;
    
    public BasicLookAndFeel() {
        this.audioLock = new Object();
        this.invocator = null;
        this.disposer = null;
    }
    
    @Override
    public UIDefaults getDefaults() {
        final UIDefaults uiDefaults = new UIDefaults(610, 0.75f);
        this.initClassDefaults(uiDefaults);
        this.initSystemColorDefaults(uiDefaults);
        this.initComponentDefaults(uiDefaults);
        return uiDefaults;
    }
    
    @Override
    public void initialize() {
        if (BasicLookAndFeel.needsEventHelper) {
            this.installAWTEventListener();
        }
    }
    
    void installAWTEventListener() {
        if (this.invocator == null) {
            this.invocator = new AWTEventHelper();
            BasicLookAndFeel.needsEventHelper = true;
            this.disposer = new PropertyChangeListener() {
                @Override
                public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
                    BasicLookAndFeel.this.uninitialize();
                }
            };
            AppContext.getAppContext().addPropertyChangeListener("guidisposed", this.disposer);
        }
    }
    
    @Override
    public void uninitialize() {
        final AppContext appContext = AppContext.getAppContext();
        synchronized (BasicPopupMenuUI.MOUSE_GRABBER_KEY) {
            final Object value = appContext.get(BasicPopupMenuUI.MOUSE_GRABBER_KEY);
            if (value != null) {
                ((BasicPopupMenuUI.MouseGrabber)value).uninstall();
            }
        }
        synchronized (BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY) {
            final Object value2 = appContext.get(BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY);
            if (value2 != null) {
                ((BasicPopupMenuUI.MenuKeyboardHelper)value2).uninstall();
            }
        }
        if (this.invocator != null) {
            AccessController.doPrivileged((PrivilegedAction<Object>)this.invocator);
            this.invocator = null;
        }
        if (this.disposer != null) {
            appContext.removePropertyChangeListener("guidisposed", this.disposer);
            this.disposer = null;
        }
    }
    
    protected void initClassDefaults(final UIDefaults uiDefaults) {
        uiDefaults.putDefaults(new Object[] { "ButtonUI", "javax.swing.plaf.basic.BasicButtonUI", "CheckBoxUI", "javax.swing.plaf.basic.BasicCheckBoxUI", "ColorChooserUI", "javax.swing.plaf.basic.BasicColorChooserUI", "FormattedTextFieldUI", "javax.swing.plaf.basic.BasicFormattedTextFieldUI", "MenuBarUI", "javax.swing.plaf.basic.BasicMenuBarUI", "MenuUI", "javax.swing.plaf.basic.BasicMenuUI", "MenuItemUI", "javax.swing.plaf.basic.BasicMenuItemUI", "CheckBoxMenuItemUI", "javax.swing.plaf.basic.BasicCheckBoxMenuItemUI", "RadioButtonMenuItemUI", "javax.swing.plaf.basic.BasicRadioButtonMenuItemUI", "RadioButtonUI", "javax.swing.plaf.basic.BasicRadioButtonUI", "ToggleButtonUI", "javax.swing.plaf.basic.BasicToggleButtonUI", "PopupMenuUI", "javax.swing.plaf.basic.BasicPopupMenuUI", "ProgressBarUI", "javax.swing.plaf.basic.BasicProgressBarUI", "ScrollBarUI", "javax.swing.plaf.basic.BasicScrollBarUI", "ScrollPaneUI", "javax.swing.plaf.basic.BasicScrollPaneUI", "SplitPaneUI", "javax.swing.plaf.basic.BasicSplitPaneUI", "SliderUI", "javax.swing.plaf.basic.BasicSliderUI", "SeparatorUI", "javax.swing.plaf.basic.BasicSeparatorUI", "SpinnerUI", "javax.swing.plaf.basic.BasicSpinnerUI", "ToolBarSeparatorUI", "javax.swing.plaf.basic.BasicToolBarSeparatorUI", "PopupMenuSeparatorUI", "javax.swing.plaf.basic.BasicPopupMenuSeparatorUI", "TabbedPaneUI", "javax.swing.plaf.basic.BasicTabbedPaneUI", "TextAreaUI", "javax.swing.plaf.basic.BasicTextAreaUI", "TextFieldUI", "javax.swing.plaf.basic.BasicTextFieldUI", "PasswordFieldUI", "javax.swing.plaf.basic.BasicPasswordFieldUI", "TextPaneUI", "javax.swing.plaf.basic.BasicTextPaneUI", "EditorPaneUI", "javax.swing.plaf.basic.BasicEditorPaneUI", "TreeUI", "javax.swing.plaf.basic.BasicTreeUI", "LabelUI", "javax.swing.plaf.basic.BasicLabelUI", "ListUI", "javax.swing.plaf.basic.BasicListUI", "ToolBarUI", "javax.swing.plaf.basic.BasicToolBarUI", "ToolTipUI", "javax.swing.plaf.basic.BasicToolTipUI", "ComboBoxUI", "javax.swing.plaf.basic.BasicComboBoxUI", "TableUI", "javax.swing.plaf.basic.BasicTableUI", "TableHeaderUI", "javax.swing.plaf.basic.BasicTableHeaderUI", "InternalFrameUI", "javax.swing.plaf.basic.BasicInternalFrameUI", "DesktopPaneUI", "javax.swing.plaf.basic.BasicDesktopPaneUI", "DesktopIconUI", "javax.swing.plaf.basic.BasicDesktopIconUI", "FileChooserUI", "javax.swing.plaf.basic.BasicFileChooserUI", "OptionPaneUI", "javax.swing.plaf.basic.BasicOptionPaneUI", "PanelUI", "javax.swing.plaf.basic.BasicPanelUI", "ViewportUI", "javax.swing.plaf.basic.BasicViewportUI", "RootPaneUI", "javax.swing.plaf.basic.BasicRootPaneUI" });
    }
    
    protected void initSystemColorDefaults(final UIDefaults uiDefaults) {
        this.loadSystemColors(uiDefaults, new String[] { "desktop", "#005C5C", "activeCaption", "#000080", "activeCaptionText", "#FFFFFF", "activeCaptionBorder", "#C0C0C0", "inactiveCaption", "#808080", "inactiveCaptionText", "#C0C0C0", "inactiveCaptionBorder", "#C0C0C0", "window", "#FFFFFF", "windowBorder", "#000000", "windowText", "#000000", "menu", "#C0C0C0", "menuText", "#000000", "text", "#C0C0C0", "textText", "#000000", "textHighlight", "#000080", "textHighlightText", "#FFFFFF", "textInactiveText", "#808080", "control", "#C0C0C0", "controlText", "#000000", "controlHighlight", "#C0C0C0", "controlLtHighlight", "#FFFFFF", "controlShadow", "#808080", "controlDkShadow", "#000000", "scrollbar", "#E0E0E0", "info", "#FFFFE1", "infoText", "#000000" }, this.isNativeLookAndFeel());
    }
    
    protected void loadSystemColors(final UIDefaults uiDefaults, final String[] array, final boolean b) {
        if (b) {
            for (int i = 0; i < array.length; i += 2) {
                Color black = Color.black;
                try {
                    black = (Color)SystemColor.class.getField(array[i]).get(null);
                }
                catch (final Exception ex) {}
                uiDefaults.put(array[i], new ColorUIResource(black));
            }
        }
        else {
            for (int j = 0; j < array.length; j += 2) {
                Color color = Color.black;
                try {
                    color = Color.decode(array[j + 1]);
                }
                catch (final NumberFormatException ex2) {
                    ex2.printStackTrace();
                }
                uiDefaults.put(array[j], new ColorUIResource(color));
            }
        }
    }
    
    private void initResourceBundle(final UIDefaults uiDefaults) {
        uiDefaults.setDefaultLocale(Locale.getDefault());
        uiDefaults.addResourceBundle("com.sun.swing.internal.plaf.basic.resources.basic");
    }
    
    protected void initComponentDefaults(final UIDefaults uiDefaults) {
        this.initResourceBundle(uiDefaults);
        final Integer n = new Integer(500);
        final Long n2 = new Long(1000L);
        final Integer n3 = new Integer(12);
        final Integer n4 = new Integer(0);
        final Integer n5 = new Integer(1);
        final SwingLazyValue swingLazyValue = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "Dialog", n4, n3 });
        final SwingLazyValue swingLazyValue2 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "Serif", n4, n3 });
        final SwingLazyValue swingLazyValue3 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "SansSerif", n4, n3 });
        final SwingLazyValue swingLazyValue4 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "Monospaced", n4, n3 });
        final SwingLazyValue swingLazyValue5 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "Dialog", n5, n3 });
        final ColorUIResource colorUIResource = new ColorUIResource(Color.red);
        final ColorUIResource colorUIResource2 = new ColorUIResource(Color.black);
        final ColorUIResource colorUIResource3 = new ColorUIResource(Color.white);
        final ColorUIResource colorUIResource4 = new ColorUIResource(Color.yellow);
        final ColorUIResource colorUIResource5 = new ColorUIResource(Color.gray);
        final ColorUIResource colorUIResource6 = new ColorUIResource(Color.lightGray);
        final ColorUIResource colorUIResource7 = new ColorUIResource(Color.darkGray);
        final ColorUIResource colorUIResource8 = new ColorUIResource(224, 224, 224);
        final Color color = uiDefaults.getColor("control");
        final Color color2 = uiDefaults.getColor("controlDkShadow");
        final Color color3 = uiDefaults.getColor("controlHighlight");
        final Color color4 = uiDefaults.getColor("controlLtHighlight");
        final Color color5 = uiDefaults.getColor("controlShadow");
        final Color color6 = uiDefaults.getColor("controlText");
        final Color color7 = uiDefaults.getColor("menu");
        final Color color8 = uiDefaults.getColor("menuText");
        final Color color9 = uiDefaults.getColor("textHighlight");
        final Color color10 = uiDefaults.getColor("textHighlightText");
        final Color color11 = uiDefaults.getColor("textInactiveText");
        final Color color12 = uiDefaults.getColor("textText");
        final Color color13 = uiDefaults.getColor("window");
        final InsetsUIResource insetsUIResource = new InsetsUIResource(0, 0, 0, 0);
        final InsetsUIResource insetsUIResource2 = new InsetsUIResource(2, 2, 2, 2);
        final InsetsUIResource insetsUIResource3 = new InsetsUIResource(3, 3, 3, 3);
        final SwingLazyValue swingLazyValue6 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders$MarginBorder");
        final SwingLazyValue swingLazyValue7 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getEtchedBorderUIResource");
        final SwingLazyValue swingLazyValue8 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getLoweredBevelBorderUIResource");
        final SwingLazyValue swingLazyValue9 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getInternalFrameBorder");
        final SwingLazyValue swingLazyValue10 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getBlackLineBorderUIResource");
        final SwingLazyValue swingLazyValue11 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", null, new Object[] { colorUIResource4 });
        final BorderUIResource.EmptyBorderUIResource emptyBorderUIResource = new BorderUIResource.EmptyBorderUIResource(1, 1, 1, 1);
        final SwingLazyValue swingLazyValue12 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$BevelBorderUIResource", null, new Object[] { new Integer(0), color4, color, color2, color5 });
        final SwingLazyValue swingLazyValue13 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getButtonBorder");
        final SwingLazyValue swingLazyValue14 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getToggleButtonBorder");
        final SwingLazyValue swingLazyValue15 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getRadioButtonBorder");
        final Object icon = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/NewFolder.gif");
        final Object icon2 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/UpFolder.gif");
        final Object icon3 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/HomeFolder.gif");
        final Object icon4 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/DetailsView.gif");
        final Object icon5 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/ListView.gif");
        final Object icon6 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/Directory.gif");
        final Object icon7 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/File.gif");
        final Object icon8 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/Computer.gif");
        final Object icon9 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/HardDrive.gif");
        final Object icon10 = SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/FloppyDrive.gif");
        final SwingLazyValue swingLazyValue16 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getInternalFrameBorder");
        final UIDefaults.ActiveValue activeValue = new UIDefaults.ActiveValue() {
            @Override
            public Object createValue(final UIDefaults uiDefaults) {
                return new DefaultListCellRenderer.UIResource();
            }
        };
        final SwingLazyValue swingLazyValue17 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getMenuBarBorder");
        final SwingLazyValue swingLazyValue18 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getMenuItemCheckIcon");
        final SwingLazyValue swingLazyValue19 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getMenuItemArrowIcon");
        final SwingLazyValue swingLazyValue20 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getMenuArrowIcon");
        final SwingLazyValue swingLazyValue21 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getCheckBoxIcon");
        final SwingLazyValue swingLazyValue22 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getRadioButtonIcon");
        final SwingLazyValue swingLazyValue23 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getCheckBoxMenuItemIcon");
        final SwingLazyValue swingLazyValue24 = new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "getRadioButtonMenuItemIcon");
        final String s = "+";
        final DimensionUIResource dimensionUIResource = new DimensionUIResource(262, 90);
        final Integer n6 = new Integer(0);
        final SwingLazyValue swingLazyValue25 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$EmptyBorderUIResource", new Object[] { n6, n6, n6, n6 });
        final Integer n7 = new Integer(10);
        final SwingLazyValue swingLazyValue26 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$EmptyBorderUIResource", new Object[] { n7, n7, n3, n7 });
        final SwingLazyValue swingLazyValue27 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$EmptyBorderUIResource", new Object[] { new Integer(6), n6, n6, n6 });
        final SwingLazyValue swingLazyValue28 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getProgressBarBorder");
        final DimensionUIResource dimensionUIResource2 = new DimensionUIResource(8, 8);
        final DimensionUIResource dimensionUIResource3 = new DimensionUIResource(4096, 4096);
        final InsetsUIResource insetsUIResource4 = insetsUIResource2;
        final DimensionUIResource dimensionUIResource4 = new DimensionUIResource(10, 10);
        final SwingLazyValue swingLazyValue29 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getSplitPaneBorder");
        final SwingLazyValue swingLazyValue30 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getSplitPaneDividerBorder");
        final InsetsUIResource insetsUIResource5 = new InsetsUIResource(0, 4, 1, 4);
        final InsetsUIResource insetsUIResource6 = new InsetsUIResource(2, 2, 2, 1);
        final InsetsUIResource insetsUIResource7 = new InsetsUIResource(3, 2, 0, 2);
        final InsetsUIResource insetsUIResource8 = new InsetsUIResource(2, 2, 3, 3);
        final SwingLazyValue swingLazyValue31 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getTextFieldBorder");
        final InsetsUIResource insetsUIResource9 = insetsUIResource3;
        final Integer n8 = n;
        final Integer n9 = new Integer(4);
        final Object[] array = { "CheckBoxMenuItem.commandSound", "InternalFrame.closeSound", "InternalFrame.maximizeSound", "InternalFrame.minimizeSound", "InternalFrame.restoreDownSound", "InternalFrame.restoreUpSound", "MenuItem.commandSound", "OptionPane.errorSound", "OptionPane.informationSound", "OptionPane.questionSound", "OptionPane.warningSound", "PopupMenu.popupSound", "RadioButtonMenuItem.commandSound" };
        uiDefaults.putDefaults(new Object[] { "AuditoryCues.cueList", array, "AuditoryCues.allAuditoryCues", array, "AuditoryCues.noAuditoryCues", { "mute" }, "AuditoryCues.playList", null, "Button.defaultButtonFollowsFocus", Boolean.TRUE, "Button.font", swingLazyValue, "Button.background", color, "Button.foreground", color6, "Button.shadow", color5, "Button.darkShadow", color2, "Button.light", color3, "Button.highlight", color4, "Button.border", swingLazyValue13, "Button.margin", new InsetsUIResource(2, 14, 2, 14), "Button.textIconGap", n9, "Button.textShiftOffset", n6, "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released", "ENTER", "pressed", "released ENTER", "released" }), "ToggleButton.font", swingLazyValue, "ToggleButton.background", color, "ToggleButton.foreground", color6, "ToggleButton.shadow", color5, "ToggleButton.darkShadow", color2, "ToggleButton.light", color3, "ToggleButton.highlight", color4, "ToggleButton.border", swingLazyValue14, "ToggleButton.margin", new InsetsUIResource(2, 14, 2, 14), "ToggleButton.textIconGap", n9, "ToggleButton.textShiftOffset", n6, "ToggleButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "RadioButton.font", swingLazyValue, "RadioButton.background", color, "RadioButton.foreground", color6, "RadioButton.shadow", color5, "RadioButton.darkShadow", color2, "RadioButton.light", color3, "RadioButton.highlight", color4, "RadioButton.border", swingLazyValue15, "RadioButton.margin", insetsUIResource2, "RadioButton.textIconGap", n9, "RadioButton.textShiftOffset", n6, "RadioButton.icon", swingLazyValue22, "RadioButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released", "RETURN", "pressed" }), "CheckBox.font", swingLazyValue, "CheckBox.background", color, "CheckBox.foreground", color6, "CheckBox.border", swingLazyValue15, "CheckBox.margin", insetsUIResource2, "CheckBox.textIconGap", n9, "CheckBox.textShiftOffset", n6, "CheckBox.icon", swingLazyValue21, "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "FileChooser.useSystemExtensionHiding", Boolean.FALSE, "ColorChooser.font", swingLazyValue, "ColorChooser.background", color, "ColorChooser.foreground", color6, "ColorChooser.swatchesSwatchSize", new Dimension(10, 10), "ColorChooser.swatchesRecentSwatchSize", new Dimension(10, 10), "ColorChooser.swatchesDefaultRecentColor", color, "ComboBox.font", swingLazyValue3, "ComboBox.background", color13, "ComboBox.foreground", color12, "ComboBox.buttonBackground", color, "ComboBox.buttonShadow", color5, "ComboBox.buttonDarkShadow", color2, "ComboBox.buttonHighlight", color4, "ComboBox.selectionBackground", color9, "ComboBox.selectionForeground", color10, "ComboBox.disabledBackground", color, "ComboBox.disabledForeground", color11, "ComboBox.timeFactor", n2, "ComboBox.isEnterSelectablePopup", Boolean.FALSE, "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", "ENTER", "enterPressed" }), "ComboBox.noActionOnKeyNavigation", Boolean.FALSE, "FileChooser.newFolderIcon", icon, "FileChooser.upFolderIcon", icon2, "FileChooser.homeFolderIcon", icon3, "FileChooser.detailsViewIcon", icon4, "FileChooser.listViewIcon", icon5, "FileChooser.readOnly", Boolean.FALSE, "FileChooser.usesSingleFilePane", Boolean.FALSE, "FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancelSelection", "F5", "refresh" }), "FileView.directoryIcon", icon6, "FileView.fileIcon", icon7, "FileView.computerIcon", icon8, "FileView.hardDriveIcon", icon9, "FileView.floppyDriveIcon", icon10, "InternalFrame.titleFont", swingLazyValue5, "InternalFrame.borderColor", color, "InternalFrame.borderShadow", color5, "InternalFrame.borderDarkShadow", color2, "InternalFrame.borderHighlight", color4, "InternalFrame.borderLight", color3, "InternalFrame.border", swingLazyValue16, "InternalFrame.icon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/JavaCup16.png"), "InternalFrame.maximizeIcon", new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "createEmptyFrameIcon"), "InternalFrame.minimizeIcon", new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "createEmptyFrameIcon"), "InternalFrame.iconifyIcon", new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "createEmptyFrameIcon"), "InternalFrame.closeIcon", new SwingLazyValue("javax.swing.plaf.basic.BasicIconFactory", "createEmptyFrameIcon"), "InternalFrame.closeSound", null, "InternalFrame.maximizeSound", null, "InternalFrame.minimizeSound", null, "InternalFrame.restoreDownSound", null, "InternalFrame.restoreUpSound", null, "InternalFrame.activeTitleBackground", uiDefaults.get("activeCaption"), "InternalFrame.activeTitleForeground", uiDefaults.get("activeCaptionText"), "InternalFrame.inactiveTitleBackground", uiDefaults.get("inactiveCaption"), "InternalFrame.inactiveTitleForeground", uiDefaults.get("inactiveCaptionText"), "InternalFrame.windowBindings", { "shift ESCAPE", "showSystemMenu", "ctrl SPACE", "showSystemMenu", "ESCAPE", "hideSystemMenu" }, "InternalFrameTitlePane.iconifyButtonOpacity", Boolean.TRUE, "InternalFrameTitlePane.maximizeButtonOpacity", Boolean.TRUE, "InternalFrameTitlePane.closeButtonOpacity", Boolean.TRUE, "DesktopIcon.border", swingLazyValue16, "Desktop.minOnScreenInsets", insetsUIResource3, "Desktop.background", uiDefaults.get("desktop"), "Desktop.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl F5", "restore", "ctrl F4", "close", "ctrl F7", "move", "ctrl F8", "resize", "RIGHT", "right", "KP_RIGHT", "right", "shift RIGHT", "shrinkRight", "shift KP_RIGHT", "shrinkRight", "LEFT", "left", "KP_LEFT", "left", "shift LEFT", "shrinkLeft", "shift KP_LEFT", "shrinkLeft", "UP", "up", "KP_UP", "up", "shift UP", "shrinkUp", "shift KP_UP", "shrinkUp", "DOWN", "down", "KP_DOWN", "down", "shift DOWN", "shrinkDown", "shift KP_DOWN", "shrinkDown", "ESCAPE", "escape", "ctrl F9", "minimize", "ctrl F10", "maximize", "ctrl F6", "selectNextFrame", "ctrl TAB", "selectNextFrame", "ctrl alt F6", "selectNextFrame", "shift ctrl alt F6", "selectPreviousFrame", "ctrl F12", "navigateNext", "shift ctrl F12", "navigatePrevious" }), "Label.font", swingLazyValue, "Label.background", color, "Label.foreground", color6, "Label.disabledForeground", colorUIResource3, "Label.disabledShadow", color5, "Label.border", null, "List.font", swingLazyValue, "List.background", color13, "List.foreground", color12, "List.selectionBackground", color9, "List.selectionForeground", color10, "List.noFocusBorder", emptyBorderUIResource, "List.focusCellHighlightBorder", swingLazyValue11, "List.dropLineColor", color5, "List.border", null, "List.cellRenderer", activeValue, "List.timeFactor", n2, "List.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "HOME", "selectFirstRow", "shift HOME", "selectFirstRowExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRowChangeLead", "END", "selectLastRow", "shift END", "selectLastRowExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRowChangeLead", "PAGE_UP", "scrollUp", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDown", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), "List.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl shift LEFT", "selectNextColumnExtendSelection", "ctrl shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl LEFT", "selectNextColumnChangeLead", "ctrl KP_LEFT", "selectNextColumnChangeLead", "RIGHT", "selectPreviousColumn", "KP_RIGHT", "selectPreviousColumn", "shift RIGHT", "selectPreviousColumnExtendSelection", "shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl RIGHT", "selectPreviousColumnChangeLead", "ctrl KP_RIGHT", "selectPreviousColumnChangeLead" }), "MenuBar.font", swingLazyValue, "MenuBar.background", color7, "MenuBar.foreground", color8, "MenuBar.shadow", color5, "MenuBar.highlight", color4, "MenuBar.border", swingLazyValue17, "MenuBar.windowBindings", { "F10", "takeFocus" }, "MenuItem.font", swingLazyValue, "MenuItem.acceleratorFont", swingLazyValue, "MenuItem.background", color7, "MenuItem.foreground", color8, "MenuItem.selectionForeground", color10, "MenuItem.selectionBackground", color9, "MenuItem.disabledForeground", null, "MenuItem.acceleratorForeground", color8, "MenuItem.acceleratorSelectionForeground", color10, "MenuItem.acceleratorDelimiter", s, "MenuItem.border", swingLazyValue6, "MenuItem.borderPainted", Boolean.FALSE, "MenuItem.margin", insetsUIResource2, "MenuItem.checkIcon", swingLazyValue18, "MenuItem.arrowIcon", swingLazyValue19, "MenuItem.commandSound", null, "RadioButtonMenuItem.font", swingLazyValue, "RadioButtonMenuItem.acceleratorFont", swingLazyValue, "RadioButtonMenuItem.background", color7, "RadioButtonMenuItem.foreground", color8, "RadioButtonMenuItem.selectionForeground", color10, "RadioButtonMenuItem.selectionBackground", color9, "RadioButtonMenuItem.disabledForeground", null, "RadioButtonMenuItem.acceleratorForeground", color8, "RadioButtonMenuItem.acceleratorSelectionForeground", color10, "RadioButtonMenuItem.border", swingLazyValue6, "RadioButtonMenuItem.borderPainted", Boolean.FALSE, "RadioButtonMenuItem.margin", insetsUIResource2, "RadioButtonMenuItem.checkIcon", swingLazyValue24, "RadioButtonMenuItem.arrowIcon", swingLazyValue19, "RadioButtonMenuItem.commandSound", null, "CheckBoxMenuItem.font", swingLazyValue, "CheckBoxMenuItem.acceleratorFont", swingLazyValue, "CheckBoxMenuItem.background", color7, "CheckBoxMenuItem.foreground", color8, "CheckBoxMenuItem.selectionForeground", color10, "CheckBoxMenuItem.selectionBackground", color9, "CheckBoxMenuItem.disabledForeground", null, "CheckBoxMenuItem.acceleratorForeground", color8, "CheckBoxMenuItem.acceleratorSelectionForeground", color10, "CheckBoxMenuItem.border", swingLazyValue6, "CheckBoxMenuItem.borderPainted", Boolean.FALSE, "CheckBoxMenuItem.margin", insetsUIResource2, "CheckBoxMenuItem.checkIcon", swingLazyValue23, "CheckBoxMenuItem.arrowIcon", swingLazyValue19, "CheckBoxMenuItem.commandSound", null, "Menu.font", swingLazyValue, "Menu.acceleratorFont", swingLazyValue, "Menu.background", color7, "Menu.foreground", color8, "Menu.selectionForeground", color10, "Menu.selectionBackground", color9, "Menu.disabledForeground", null, "Menu.acceleratorForeground", color8, "Menu.acceleratorSelectionForeground", color10, "Menu.border", swingLazyValue6, "Menu.borderPainted", Boolean.FALSE, "Menu.margin", insetsUIResource2, "Menu.checkIcon", swingLazyValue18, "Menu.arrowIcon", swingLazyValue20, "Menu.menuPopupOffsetX", new Integer(0), "Menu.menuPopupOffsetY", new Integer(0), "Menu.submenuPopupOffsetX", new Integer(0), "Menu.submenuPopupOffsetY", new Integer(0), "Menu.shortcutKeys", { SwingUtilities2.getSystemMnemonicKeyMask() }, "Menu.crossMenuMnemonic", Boolean.TRUE, "Menu.cancelMode", "hideLastSubmenu", "Menu.preserveTopLevelSelection", Boolean.FALSE, "PopupMenu.font", swingLazyValue, "PopupMenu.background", color7, "PopupMenu.foreground", color8, "PopupMenu.border", swingLazyValue9, "PopupMenu.popupSound", null, "PopupMenu.selectedWindowInputMapBindings", { "ESCAPE", "cancel", "DOWN", "selectNext", "KP_DOWN", "selectNext", "UP", "selectPrevious", "KP_UP", "selectPrevious", "LEFT", "selectParent", "KP_LEFT", "selectParent", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "ENTER", "return", "ctrl ENTER", "return", "SPACE", "return" }, "PopupMenu.selectedWindowInputMapBindings.RightToLeft", { "LEFT", "selectChild", "KP_LEFT", "selectChild", "RIGHT", "selectParent", "KP_RIGHT", "selectParent" }, "PopupMenu.consumeEventOnClose", Boolean.FALSE, "OptionPane.font", swingLazyValue, "OptionPane.background", color, "OptionPane.foreground", color6, "OptionPane.messageForeground", color6, "OptionPane.border", swingLazyValue26, "OptionPane.messageAreaBorder", swingLazyValue25, "OptionPane.buttonAreaBorder", swingLazyValue27, "OptionPane.minimumSize", dimensionUIResource, "OptionPane.errorIcon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/Error.gif"), "OptionPane.informationIcon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/Inform.gif"), "OptionPane.warningIcon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/Warn.gif"), "OptionPane.questionIcon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/Question.gif"), "OptionPane.windowBindings", { "ESCAPE", "close" }, "OptionPane.errorSound", null, "OptionPane.informationSound", null, "OptionPane.questionSound", null, "OptionPane.warningSound", null, "OptionPane.buttonClickThreshhold", n, "Panel.font", swingLazyValue, "Panel.background", color, "Panel.foreground", color12, "ProgressBar.font", swingLazyValue, "ProgressBar.foreground", color9, "ProgressBar.background", color, "ProgressBar.selectionForeground", color, "ProgressBar.selectionBackground", color9, "ProgressBar.border", swingLazyValue28, "ProgressBar.cellLength", new Integer(1), "ProgressBar.cellSpacing", n6, "ProgressBar.repaintInterval", new Integer(50), "ProgressBar.cycleTime", new Integer(3000), "ProgressBar.horizontalSize", new DimensionUIResource(146, 12), "ProgressBar.verticalSize", new DimensionUIResource(12, 146), "Separator.shadow", color5, "Separator.highlight", color4, "Separator.background", color4, "Separator.foreground", color5, "ScrollBar.background", colorUIResource8, "ScrollBar.foreground", color, "ScrollBar.track", uiDefaults.get("scrollbar"), "ScrollBar.trackHighlight", color2, "ScrollBar.thumb", color, "ScrollBar.thumbHighlight", color4, "ScrollBar.thumbDarkShadow", color2, "ScrollBar.thumbShadow", color5, "ScrollBar.border", null, "ScrollBar.minimumThumbSize", dimensionUIResource2, "ScrollBar.maximumThumbSize", dimensionUIResource3, "ScrollBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }), "ScrollBar.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement" }), "ScrollBar.width", new Integer(16), "ScrollPane.font", swingLazyValue, "ScrollPane.background", color, "ScrollPane.foreground", color6, "ScrollPane.border", swingLazyValue31, "ScrollPane.viewportBorder", null, "ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd" }), "ScrollPane.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "ctrl PAGE_UP", "scrollRight", "ctrl PAGE_DOWN", "scrollLeft" }), "Viewport.font", swingLazyValue, "Viewport.background", color, "Viewport.foreground", color12, "Slider.font", swingLazyValue, "Slider.foreground", color, "Slider.background", color, "Slider.highlight", color4, "Slider.tickColor", Color.black, "Slider.shadow", color5, "Slider.focus", color2, "Slider.border", null, "Slider.horizontalSize", new Dimension(200, 21), "Slider.verticalSize", new Dimension(21, 200), "Slider.minimumHorizontalSize", new Dimension(36, 21), "Slider.minimumVerticalSize", new Dimension(21, 36), "Slider.focusInsets", insetsUIResource4, "Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "PAGE_UP", "positiveBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }), "Slider.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement" }), "Slider.onlyLeftMouseButtonDrag", Boolean.TRUE, "Spinner.font", swingLazyValue4, "Spinner.background", color, "Spinner.foreground", color, "Spinner.border", swingLazyValue31, "Spinner.arrowButtonBorder", null, "Spinner.arrowButtonInsets", null, "Spinner.arrowButtonSize", new Dimension(16, 5), "Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "Spinner.editorBorderPainted", Boolean.FALSE, "Spinner.editorAlignment", 11, "SplitPane.background", color, "SplitPane.highlight", color4, "SplitPane.shadow", color5, "SplitPane.darkShadow", color2, "SplitPane.border", swingLazyValue29, "SplitPane.dividerSize", new Integer(7), "SplitPaneDivider.border", swingLazyValue30, "SplitPaneDivider.draggingColor", colorUIResource7, "SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward" }), "TabbedPane.font", swingLazyValue, "TabbedPane.background", color, "TabbedPane.foreground", color6, "TabbedPane.highlight", color4, "TabbedPane.light", color3, "TabbedPane.shadow", color5, "TabbedPane.darkShadow", color2, "TabbedPane.selected", null, "TabbedPane.focus", color6, "TabbedPane.textIconGap", n9, "TabbedPane.tabsOverlapBorder", Boolean.FALSE, "TabbedPane.selectionFollowsFocus", Boolean.TRUE, "TabbedPane.labelShift", 1, "TabbedPane.selectedLabelShift", -1, "TabbedPane.tabInsets", insetsUIResource5, "TabbedPane.selectedTabPadInsets", insetsUIResource6, "TabbedPane.tabAreaInsets", insetsUIResource7, "TabbedPane.contentBorderInsets", insetsUIResource8, "TabbedPane.tabRunOverlay", new Integer(2), "TabbedPane.tabsOpaque", Boolean.TRUE, "TabbedPane.contentOpaque", Boolean.TRUE, "TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent" }), "TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus" }), "Table.font", swingLazyValue, "Table.foreground", color6, "Table.background", color13, "Table.selectionForeground", color10, "Table.selectionBackground", color9, "Table.dropLineColor", color5, "Table.dropLineShortColor", colorUIResource2, "Table.gridColor", colorUIResource5, "Table.focusCellBackground", color13, "Table.focusCellForeground", color6, "Table.focusCellHighlightBorder", swingLazyValue11, "Table.scrollPaneBorder", swingLazyValue8, "Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "HOME", "selectFirstColumn", "shift HOME", "selectFirstColumnExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRow", "END", "selectLastColumn", "shift END", "selectLastColumnExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRow", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollLeftExtendSelection", "ctrl PAGE_UP", "scrollLeftChangeSelection", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollRightExtendSelection", "ctrl PAGE_DOWN", "scrollRightChangeSelection", "TAB", "selectNextColumnCell", "shift TAB", "selectPreviousColumnCell", "ENTER", "selectNextRowCell", "shift ENTER", "selectPreviousRowCell", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ESCAPE", "cancel", "F2", "startEditing", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo", "F8", "focusHeader" }), "Table.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "selectPreviousColumn", "KP_RIGHT", "selectPreviousColumn", "shift RIGHT", "selectPreviousColumnExtendSelection", "shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl RIGHT", "selectPreviousColumnChangeLead", "ctrl KP_RIGHT", "selectPreviousColumnChangeLead", "LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl shift LEFT", "selectNextColumnExtendSelection", "ctrl shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl LEFT", "selectNextColumnChangeLead", "ctrl KP_LEFT", "selectNextColumnChangeLead", "ctrl PAGE_UP", "scrollRightChangeSelection", "ctrl PAGE_DOWN", "scrollLeftChangeSelection", "ctrl shift PAGE_UP", "scrollRightExtendSelection", "ctrl shift PAGE_DOWN", "scrollLeftExtendSelection" }), "Table.ascendingSortIcon", new SwingLazyValue("sun.swing.icon.SortArrowIcon", null, new Object[] { Boolean.TRUE, "Table.sortIconColor" }), "Table.descendingSortIcon", new SwingLazyValue("sun.swing.icon.SortArrowIcon", null, new Object[] { Boolean.FALSE, "Table.sortIconColor" }), "Table.sortIconColor", color5, "TableHeader.font", swingLazyValue, "TableHeader.foreground", color6, "TableHeader.background", color, "TableHeader.cellBorder", swingLazyValue12, "TableHeader.focusCellBackground", uiDefaults.getColor("text"), "TableHeader.focusCellForeground", null, "TableHeader.focusCellBorder", null, "TableHeader.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "toggleSortOrder", "LEFT", "selectColumnToLeft", "KP_LEFT", "selectColumnToLeft", "RIGHT", "selectColumnToRight", "KP_RIGHT", "selectColumnToRight", "alt LEFT", "moveColumnLeft", "alt KP_LEFT", "moveColumnLeft", "alt RIGHT", "moveColumnRight", "alt KP_RIGHT", "moveColumnRight", "alt shift LEFT", "resizeLeft", "alt shift KP_LEFT", "resizeLeft", "alt shift RIGHT", "resizeRight", "alt shift KP_RIGHT", "resizeRight", "ESCAPE", "focusTable" }), "TextField.font", swingLazyValue3, "TextField.background", color13, "TextField.foreground", color12, "TextField.shadow", color5, "TextField.darkShadow", color2, "TextField.light", color3, "TextField.highlight", color4, "TextField.inactiveForeground", color11, "TextField.inactiveBackground", color, "TextField.selectionBackground", color9, "TextField.selectionForeground", color10, "TextField.caretForeground", color12, "TextField.caretBlinkRate", n8, "TextField.border", swingLazyValue31, "TextField.margin", insetsUIResource, "FormattedTextField.font", swingLazyValue3, "FormattedTextField.background", color13, "FormattedTextField.foreground", color12, "FormattedTextField.inactiveForeground", color11, "FormattedTextField.inactiveBackground", color, "FormattedTextField.selectionBackground", color9, "FormattedTextField.selectionForeground", color10, "FormattedTextField.caretForeground", color12, "FormattedTextField.caretBlinkRate", n8, "FormattedTextField.border", swingLazyValue31, "FormattedTextField.margin", insetsUIResource, "FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "PasswordField.font", swingLazyValue4, "PasswordField.background", color13, "PasswordField.foreground", color12, "PasswordField.inactiveForeground", color11, "PasswordField.inactiveBackground", color, "PasswordField.selectionBackground", color9, "PasswordField.selectionForeground", color10, "PasswordField.caretForeground", color12, "PasswordField.caretBlinkRate", n8, "PasswordField.border", swingLazyValue31, "PasswordField.margin", insetsUIResource, "PasswordField.echoChar", '*', "TextArea.font", swingLazyValue4, "TextArea.background", color13, "TextArea.foreground", color12, "TextArea.inactiveForeground", color11, "TextArea.selectionBackground", color9, "TextArea.selectionForeground", color10, "TextArea.caretForeground", color12, "TextArea.caretBlinkRate", n8, "TextArea.border", swingLazyValue6, "TextArea.margin", insetsUIResource, "TextPane.font", swingLazyValue2, "TextPane.background", colorUIResource3, "TextPane.foreground", color12, "TextPane.selectionBackground", color9, "TextPane.selectionForeground", color10, "TextPane.caretForeground", color12, "TextPane.caretBlinkRate", n8, "TextPane.inactiveForeground", color11, "TextPane.border", swingLazyValue6, "TextPane.margin", insetsUIResource9, "EditorPane.font", swingLazyValue2, "EditorPane.background", colorUIResource3, "EditorPane.foreground", color12, "EditorPane.selectionBackground", color9, "EditorPane.selectionForeground", color10, "EditorPane.caretForeground", color12, "EditorPane.caretBlinkRate", n8, "EditorPane.inactiveForeground", color11, "EditorPane.border", swingLazyValue6, "EditorPane.margin", insetsUIResource9, "html.pendingImage", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/image-delayed.png"), "html.missingImage", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/image-failed.png"), "TitledBorder.font", swingLazyValue, "TitledBorder.titleColor", color6, "TitledBorder.border", swingLazyValue7, "ToolBar.font", swingLazyValue, "ToolBar.background", color, "ToolBar.foreground", color6, "ToolBar.shadow", color5, "ToolBar.darkShadow", color2, "ToolBar.light", color3, "ToolBar.highlight", color4, "ToolBar.dockingBackground", color, "ToolBar.dockingForeground", colorUIResource, "ToolBar.floatingBackground", color, "ToolBar.floatingForeground", colorUIResource7, "ToolBar.border", swingLazyValue7, "ToolBar.separatorSize", dimensionUIResource4, "ToolBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight" }), "ToolTip.font", swingLazyValue3, "ToolTip.background", uiDefaults.get("info"), "ToolTip.foreground", uiDefaults.get("infoText"), "ToolTip.border", swingLazyValue10, "ToolTipManager.enableToolTipMode", "allWindows", "Tree.paintLines", Boolean.TRUE, "Tree.lineTypeDashed", Boolean.FALSE, "Tree.font", swingLazyValue, "Tree.background", color13, "Tree.foreground", color12, "Tree.hash", colorUIResource5, "Tree.textForeground", color12, "Tree.textBackground", uiDefaults.get("text"), "Tree.selectionForeground", color10, "Tree.selectionBackground", color9, "Tree.selectionBorderColor", colorUIResource2, "Tree.dropLineColor", color5, "Tree.editorBorder", swingLazyValue10, "Tree.leftChildIndent", new Integer(7), "Tree.rightChildIndent", new Integer(13), "Tree.rowHeight", new Integer(16), "Tree.scrollsOnExpand", Boolean.TRUE, "Tree.openIcon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/TreeOpen.gif"), "Tree.closedIcon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/TreeClosed.gif"), "Tree.leafIcon", SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/TreeLeaf.gif"), "Tree.expandedIcon", null, "Tree.collapsedIcon", null, "Tree.changeSelectionWithFocus", Boolean.TRUE, "Tree.drawsFocusBorderAroundIcon", Boolean.FALSE, "Tree.timeFactor", n2, "Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPrevious", "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", "ctrl shift UP", "selectPreviousExtendSelection", "ctrl shift KP_UP", "selectPreviousExtendSelection", "ctrl UP", "selectPreviousChangeLead", "ctrl KP_UP", "selectPreviousChangeLead", "DOWN", "selectNext", "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", "shift KP_DOWN", "selectNextExtendSelection", "ctrl shift DOWN", "selectNextExtendSelection", "ctrl shift KP_DOWN", "selectNextExtendSelection", "ctrl DOWN", "selectNextChangeLead", "ctrl KP_DOWN", "selectNextChangeLead", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "LEFT", "selectParent", "KP_LEFT", "selectParent", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "HOME", "selectFirst", "shift HOME", "selectFirstExtendSelection", "ctrl shift HOME", "selectFirstExtendSelection", "ctrl HOME", "selectFirstChangeLead", "END", "selectLast", "shift END", "selectLastExtendSelection", "ctrl shift END", "selectLastExtendSelection", "ctrl END", "selectLastChangeLead", "F2", "startEditing", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ctrl LEFT", "scrollLeft", "ctrl KP_LEFT", "scrollLeft", "ctrl RIGHT", "scrollRight", "ctrl KP_RIGHT", "scrollRight", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), "Tree.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "selectParent", "KP_RIGHT", "selectParent", "LEFT", "selectChild", "KP_LEFT", "selectChild" }), "Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancel" }), "RootPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "shift F10", "postPopup", "CONTEXT_MENU", "postPopup" }), "RootPane.defaultButtonWindowKeyBindings", { "ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release" } });
    }
    
    static int getFocusAcceleratorKeyMask() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (defaultToolkit instanceof SunToolkit) {
            return ((SunToolkit)defaultToolkit).getFocusAcceleratorKeyMask();
        }
        return 8;
    }
    
    static Object getUIOfType(final ComponentUI componentUI, final Class clazz) {
        if (clazz.isInstance(componentUI)) {
            return componentUI;
        }
        return null;
    }
    
    protected ActionMap getAudioActionMap() {
        ActionMap actionMap = (ActionMap)UIManager.get("AuditoryCues.actionMap");
        if (actionMap == null) {
            final Object[] array = (Object[])UIManager.get("AuditoryCues.cueList");
            if (array != null) {
                actionMap = new ActionMapUIResource();
                for (int i = array.length - 1; i >= 0; --i) {
                    actionMap.put(array[i], this.createAudioAction(array[i]));
                }
            }
            UIManager.getLookAndFeelDefaults().put("AuditoryCues.actionMap", actionMap);
        }
        return actionMap;
    }
    
    protected Action createAudioAction(final Object o) {
        if (o != null) {
            return new AudioAction((String)o, (String)UIManager.get(o));
        }
        return null;
    }
    
    private byte[] loadAudioData(final String s) {
        if (s == null) {
            return null;
        }
        final byte[] array = AccessController.doPrivileged((PrivilegedAction<byte[]>)new PrivilegedAction<byte[]>() {
            @Override
            public byte[] run() {
                try {
                    final InputStream resourceAsStream = BasicLookAndFeel.this.getClass().getResourceAsStream(s);
                    if (resourceAsStream == null) {
                        return null;
                    }
                    final BufferedInputStream bufferedInputStream = new BufferedInputStream(resourceAsStream);
                    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                    final byte[] array = new byte[1024];
                    int read;
                    while ((read = bufferedInputStream.read(array)) > 0) {
                        byteArrayOutputStream.write(array, 0, read);
                    }
                    bufferedInputStream.close();
                    byteArrayOutputStream.flush();
                    return byteArrayOutputStream.toByteArray();
                }
                catch (final IOException ex) {
                    System.err.println(ex.toString());
                    return null;
                }
            }
        });
        if (array == null) {
            System.err.println(this.getClass().getName() + "/" + s + " not found.");
            return null;
        }
        if (array.length == 0) {
            System.err.println("warning: " + s + " is zero-length");
            return null;
        }
        return array;
    }
    
    protected void playSound(final Action action) {
        if (action != null) {
            final Object[] array = (Object[])UIManager.get("AuditoryCues.playList");
            if (array != null) {
                final HashSet set = new HashSet();
                final Object[] array2 = array;
                for (int length = array2.length, i = 0; i < length; ++i) {
                    set.add(array2[i]);
                }
                final String s = (String)action.getValue("Name");
                if (set.contains(s)) {
                    action.actionPerformed(new ActionEvent(this, 1001, s));
                }
            }
        }
    }
    
    static void installAudioActionMap(final ActionMap actionMap) {
        final LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        if (lookAndFeel instanceof BasicLookAndFeel) {
            actionMap.setParent(((BasicLookAndFeel)lookAndFeel).getAudioActionMap());
        }
    }
    
    static void playSound(final JComponent component, final Object o) {
        final LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        if (lookAndFeel instanceof BasicLookAndFeel) {
            final ActionMap actionMap = component.getActionMap();
            if (actionMap != null) {
                final Action value = actionMap.get(o);
                if (value != null) {
                    ((BasicLookAndFeel)lookAndFeel).playSound(value);
                }
            }
        }
    }
    
    private class AudioAction extends AbstractAction implements LineListener
    {
        private String audioResource;
        private byte[] audioBuffer;
        
        public AudioAction(final String s, final String audioResource) {
            super(s);
            this.audioResource = audioResource;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (this.audioBuffer == null) {
                this.audioBuffer = BasicLookAndFeel.this.loadAudioData(this.audioResource);
            }
            if (this.audioBuffer != null) {
                this.cancelCurrentSound(null);
                try {
                    final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(this.audioBuffer));
                    final Clip clip = (Clip)AudioSystem.getLine(new DataLine.Info(Clip.class, audioInputStream.getFormat()));
                    clip.open(audioInputStream);
                    clip.addLineListener(this);
                    synchronized (BasicLookAndFeel.this.audioLock) {
                        BasicLookAndFeel.this.clipPlaying = clip;
                    }
                    clip.start();
                }
                catch (final Exception ex) {}
            }
        }
        
        @Override
        public void update(final LineEvent lineEvent) {
            if (lineEvent.getType() == LineEvent.Type.STOP) {
                this.cancelCurrentSound((Clip)lineEvent.getLine());
            }
        }
        
        private void cancelCurrentSound(final Clip clip) {
            Line access$200 = null;
            synchronized (BasicLookAndFeel.this.audioLock) {
                if (clip == null || clip == BasicLookAndFeel.this.clipPlaying) {
                    access$200 = BasicLookAndFeel.this.clipPlaying;
                    BasicLookAndFeel.this.clipPlaying = null;
                }
            }
            if (access$200 != null) {
                access$200.removeLineListener(this);
                access$200.close();
            }
        }
    }
    
    class AWTEventHelper implements AWTEventListener, PrivilegedAction<Object>
    {
        AWTEventHelper() {
            AccessController.doPrivileged((PrivilegedAction<Object>)this);
        }
        
        @Override
        public Object run() {
            final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            if (BasicLookAndFeel.this.invocator == null) {
                defaultToolkit.addAWTEventListener(this, 16L);
            }
            else {
                defaultToolkit.removeAWTEventListener(BasicLookAndFeel.this.invocator);
            }
            return null;
        }
        
        @Override
        public void eventDispatched(final AWTEvent awtEvent) {
            final int id = awtEvent.getID();
            if (((long)id & 0x10L) != 0x0L) {
                final MouseEvent mouseEvent = (MouseEvent)awtEvent;
                if (mouseEvent.isPopupTrigger()) {
                    final MenuElement[] selectedPath = MenuSelectionManager.defaultManager().getSelectedPath();
                    if (selectedPath != null && selectedPath.length != 0) {
                        return;
                    }
                    final Object source = mouseEvent.getSource();
                    JComponent component = null;
                    if (source instanceof JComponent) {
                        component = (JComponent)source;
                    }
                    else if (source instanceof BasicSplitPaneDivider) {
                        component = (JComponent)((BasicSplitPaneDivider)source).getParent();
                    }
                    if (component != null && component.getComponentPopupMenu() != null) {
                        Point point = component.getPopupLocation(mouseEvent);
                        if (point == null) {
                            point = SwingUtilities.convertPoint((Component)source, mouseEvent.getPoint(), component);
                        }
                        component.getComponentPopupMenu().show(component, point.x, point.y);
                        mouseEvent.consume();
                    }
                }
            }
            if (id == 501) {
                final Object source2 = awtEvent.getSource();
                if (!(source2 instanceof Component)) {
                    return;
                }
                final Component component2 = (Component)source2;
                if (component2 != null) {
                    for (Component parent = component2; parent != null && !(parent instanceof Window); parent = parent.getParent()) {
                        if (parent instanceof JInternalFrame) {
                            try {
                                ((JInternalFrame)parent).setSelected(true);
                            }
                            catch (final PropertyVetoException ex) {}
                        }
                    }
                }
            }
        }
    }
}
