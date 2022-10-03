package com.sun.java.swing.plaf.windows;

import java.awt.image.RGBImageFilter;
import java.awt.Container;
import sun.swing.DefaultLayoutStyle;
import javax.swing.plaf.BorderUIResource;
import sun.font.FontUtilities;
import javax.swing.plaf.FontUIResource;
import java.awt.HeadlessException;
import java.awt.Font;
import javax.swing.LookAndFeel;
import java.awt.Graphics;
import javax.swing.plaf.UIResource;
import javax.swing.ImageIcon;
import sun.awt.shell.ShellFolder;
import java.awt.Image;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import sun.swing.StringUIClientPropertyKey;
import sun.swing.ImageIconUIResource;
import java.awt.image.ImageProducer;
import java.awt.image.ImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import javax.swing.JRootPane;
import javax.swing.Action;
import java.awt.Component;
import javax.swing.event.ChangeListener;
import javax.swing.MenuSelectionManager;
import java.awt.Dimension;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.border.EmptyBorder;
import java.awt.Insets;
import sun.swing.SwingUtilities2;
import javax.swing.plaf.ColorUIResource;
import java.awt.Color;
import sun.swing.SwingLazyValue;
import javax.swing.UIDefaults;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import javax.swing.UIManager;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.awt.OSInfo;
import javax.swing.LayoutStyle;
import javax.swing.plaf.basic.BasicLookAndFeel;

public class WindowsLookAndFeel extends BasicLookAndFeel
{
    static final Object HI_RES_DISABLED_ICON_CLIENT_KEY;
    private boolean updatePending;
    private boolean useSystemFontSettings;
    private boolean useSystemFontSizeSettings;
    private DesktopProperty themeActive;
    private DesktopProperty dllName;
    private DesktopProperty colorName;
    private DesktopProperty sizeName;
    private DesktopProperty aaSettings;
    private transient LayoutStyle style;
    private int baseUnitX;
    private int baseUnitY;
    private static boolean isMnemonicHidden;
    private static boolean isClassicWindows;
    
    public WindowsLookAndFeel() {
        this.updatePending = false;
        this.useSystemFontSettings = true;
    }
    
    @Override
    public String getName() {
        return "Windows";
    }
    
    @Override
    public String getDescription() {
        return "The Microsoft Windows Look and Feel";
    }
    
    @Override
    public String getID() {
        return "Windows";
    }
    
    @Override
    public boolean isNativeLookAndFeel() {
        return OSInfo.getOSType() == OSInfo.OSType.WINDOWS;
    }
    
    @Override
    public boolean isSupportedLookAndFeel() {
        return this.isNativeLookAndFeel();
    }
    
    @Override
    public void initialize() {
        super.initialize();
        if (OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_95) <= 0) {
            WindowsLookAndFeel.isClassicWindows = true;
        }
        else {
            WindowsLookAndFeel.isClassicWindows = false;
            XPStyle.invalidateStyle();
        }
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("swing.useSystemFontSettings"));
        this.useSystemFontSettings = (s == null || Boolean.valueOf(s));
        if (this.useSystemFontSettings) {
            final Object value = UIManager.get("Application.useSystemFontSettings");
            this.useSystemFontSettings = (value == null || Boolean.TRUE.equals(value));
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(WindowsRootPaneUI.altProcessor);
    }
    
    @Override
    protected void initClassDefaults(final UIDefaults uiDefaults) {
        super.initClassDefaults(uiDefaults);
        uiDefaults.putDefaults(new Object[] { "ButtonUI", "com.sun.java.swing.plaf.windows.WindowsButtonUI", "CheckBoxUI", "com.sun.java.swing.plaf.windows.WindowsCheckBoxUI", "CheckBoxMenuItemUI", "com.sun.java.swing.plaf.windows.WindowsCheckBoxMenuItemUI", "LabelUI", "com.sun.java.swing.plaf.windows.WindowsLabelUI", "RadioButtonUI", "com.sun.java.swing.plaf.windows.WindowsRadioButtonUI", "RadioButtonMenuItemUI", "com.sun.java.swing.plaf.windows.WindowsRadioButtonMenuItemUI", "ToggleButtonUI", "com.sun.java.swing.plaf.windows.WindowsToggleButtonUI", "ProgressBarUI", "com.sun.java.swing.plaf.windows.WindowsProgressBarUI", "SliderUI", "com.sun.java.swing.plaf.windows.WindowsSliderUI", "SeparatorUI", "com.sun.java.swing.plaf.windows.WindowsSeparatorUI", "SplitPaneUI", "com.sun.java.swing.plaf.windows.WindowsSplitPaneUI", "SpinnerUI", "com.sun.java.swing.plaf.windows.WindowsSpinnerUI", "TabbedPaneUI", "com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI", "TextAreaUI", "com.sun.java.swing.plaf.windows.WindowsTextAreaUI", "TextFieldUI", "com.sun.java.swing.plaf.windows.WindowsTextFieldUI", "PasswordFieldUI", "com.sun.java.swing.plaf.windows.WindowsPasswordFieldUI", "TextPaneUI", "com.sun.java.swing.plaf.windows.WindowsTextPaneUI", "EditorPaneUI", "com.sun.java.swing.plaf.windows.WindowsEditorPaneUI", "TreeUI", "com.sun.java.swing.plaf.windows.WindowsTreeUI", "ToolBarUI", "com.sun.java.swing.plaf.windows.WindowsToolBarUI", "ToolBarSeparatorUI", "com.sun.java.swing.plaf.windows.WindowsToolBarSeparatorUI", "ComboBoxUI", "com.sun.java.swing.plaf.windows.WindowsComboBoxUI", "TableHeaderUI", "com.sun.java.swing.plaf.windows.WindowsTableHeaderUI", "InternalFrameUI", "com.sun.java.swing.plaf.windows.WindowsInternalFrameUI", "DesktopPaneUI", "com.sun.java.swing.plaf.windows.WindowsDesktopPaneUI", "DesktopIconUI", "com.sun.java.swing.plaf.windows.WindowsDesktopIconUI", "FileChooserUI", "com.sun.java.swing.plaf.windows.WindowsFileChooserUI", "MenuUI", "com.sun.java.swing.plaf.windows.WindowsMenuUI", "MenuItemUI", "com.sun.java.swing.plaf.windows.WindowsMenuItemUI", "MenuBarUI", "com.sun.java.swing.plaf.windows.WindowsMenuBarUI", "PopupMenuUI", "com.sun.java.swing.plaf.windows.WindowsPopupMenuUI", "PopupMenuSeparatorUI", "com.sun.java.swing.plaf.windows.WindowsPopupMenuSeparatorUI", "ScrollBarUI", "com.sun.java.swing.plaf.windows.WindowsScrollBarUI", "RootPaneUI", "com.sun.java.swing.plaf.windows.WindowsRootPaneUI" });
    }
    
    @Override
    protected void initSystemColorDefaults(final UIDefaults uiDefaults) {
        this.loadSystemColors(uiDefaults, new String[] { "desktop", "#005C5C", "activeCaption", "#000080", "activeCaptionText", "#FFFFFF", "activeCaptionBorder", "#C0C0C0", "inactiveCaption", "#808080", "inactiveCaptionText", "#C0C0C0", "inactiveCaptionBorder", "#C0C0C0", "window", "#FFFFFF", "windowBorder", "#000000", "windowText", "#000000", "menu", "#C0C0C0", "menuPressedItemB", "#000080", "menuPressedItemF", "#FFFFFF", "menuText", "#000000", "text", "#C0C0C0", "textText", "#000000", "textHighlight", "#000080", "textHighlightText", "#FFFFFF", "textInactiveText", "#808080", "control", "#C0C0C0", "controlText", "#000000", "controlHighlight", "#C0C0C0", "controlLtHighlight", "#FFFFFF", "controlShadow", "#808080", "controlDkShadow", "#000000", "scrollbar", "#E0E0E0", "info", "#FFFFE1", "infoText", "#000000" }, this.isNativeLookAndFeel());
    }
    
    private void initResourceBundle(final UIDefaults uiDefaults) {
        uiDefaults.addResourceBundle("com.sun.java.swing.plaf.windows.resources.windows");
    }
    
    @Override
    protected void initComponentDefaults(final UIDefaults uiDefaults) {
        super.initComponentDefaults(uiDefaults);
        this.initResourceBundle(uiDefaults);
        final Integer value = 12;
        final Integer value2 = 0;
        final Integer value3 = 1;
        final SwingLazyValue swingLazyValue = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "Dialog", value2, value });
        final SwingLazyValue swingLazyValue2 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "SansSerif", value2, value });
        final SwingLazyValue swingLazyValue3 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "Monospaced", value2, value });
        final SwingLazyValue swingLazyValue4 = new SwingLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { "Dialog", value3, value });
        final ColorUIResource colorUIResource = new ColorUIResource(Color.red);
        final ColorUIResource colorUIResource2 = new ColorUIResource(Color.black);
        final ColorUIResource colorUIResource3 = new ColorUIResource(Color.white);
        final ColorUIResource colorUIResource4 = new ColorUIResource(Color.gray);
        final ColorUIResource colorUIResource5 = new ColorUIResource(Color.darkGray);
        WindowsLookAndFeel.isClassicWindows = (OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_95) <= 0);
        final Icon expandedIcon = WindowsTreeUI.ExpandedIcon.createExpandedIcon();
        final Icon collapsedIcon = WindowsTreeUI.CollapsedIcon.createCollapsedIcon();
        final UIDefaults.LazyInputMap lazyInputMap = new UIDefaults.LazyInputMap(new Object[] { "control C", "copy-to-clipboard", "control V", "paste-from-clipboard", "control X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "control A", "select-all", "control BACK_SLASH", "unselect", "shift LEFT", "selection-backward", "shift RIGHT", "selection-forward", "control LEFT", "caret-previous-word", "control RIGHT", "caret-next-word", "control shift LEFT", "selection-previous-word", "control shift RIGHT", "selection-next-word", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "control shift O", "toggle-componentOrientation" });
        final UIDefaults.LazyInputMap lazyInputMap2 = new UIDefaults.LazyInputMap(new Object[] { "control C", "copy-to-clipboard", "control V", "paste-from-clipboard", "control X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "control A", "select-all", "control BACK_SLASH", "unselect", "shift LEFT", "selection-backward", "shift RIGHT", "selection-forward", "control LEFT", "caret-begin-line", "control RIGHT", "caret-end-line", "control shift LEFT", "selection-begin-line", "control shift RIGHT", "selection-end-line", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "control shift O", "toggle-componentOrientation" });
        final UIDefaults.LazyInputMap lazyInputMap3 = new UIDefaults.LazyInputMap(new Object[] { "control C", "copy-to-clipboard", "control V", "paste-from-clipboard", "control X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift RIGHT", "selection-forward", "control LEFT", "caret-previous-word", "control RIGHT", "caret-next-word", "control shift LEFT", "selection-previous-word", "control shift RIGHT", "selection-next-word", "control A", "select-all", "control BACK_SLASH", "unselect", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "control HOME", "caret-begin", "control END", "caret-end", "control shift HOME", "selection-begin", "control shift END", "selection-end", "UP", "caret-up", "DOWN", "caret-down", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "PAGE_UP", "page-up", "PAGE_DOWN", "page-down", "shift PAGE_UP", "selection-page-up", "shift PAGE_DOWN", "selection-page-down", "ctrl shift PAGE_UP", "selection-page-left", "ctrl shift PAGE_DOWN", "selection-page-right", "shift UP", "selection-up", "shift DOWN", "selection-down", "ENTER", "insert-break", "TAB", "insert-tab", "control T", "next-link-action", "control shift T", "previous-link-action", "control SPACE", "activate-link-action", "control shift O", "toggle-componentOrientation" });
        final String s = "+";
        final DesktopProperty desktopProperty = new DesktopProperty("win.3d.backgroundColor", uiDefaults.get("control"));
        final DesktopProperty desktopProperty2 = new DesktopProperty("win.3d.lightColor", uiDefaults.get("controlHighlight"));
        final DesktopProperty desktopProperty3 = new DesktopProperty("win.3d.highlightColor", uiDefaults.get("controlLtHighlight"));
        final DesktopProperty desktopProperty4 = new DesktopProperty("win.3d.shadowColor", uiDefaults.get("controlShadow"));
        final DesktopProperty desktopProperty5 = new DesktopProperty("win.3d.darkShadowColor", uiDefaults.get("controlDkShadow"));
        final DesktopProperty desktopProperty6 = new DesktopProperty("win.button.textColor", uiDefaults.get("controlText"));
        final DesktopProperty desktopProperty7 = new DesktopProperty("win.menu.backgroundColor", uiDefaults.get("menu"));
        final DesktopProperty desktopProperty8 = new DesktopProperty("win.menubar.backgroundColor", uiDefaults.get("menu"));
        final DesktopProperty desktopProperty9 = new DesktopProperty("win.menu.textColor", uiDefaults.get("menuText"));
        final DesktopProperty desktopProperty10 = new DesktopProperty("win.item.highlightColor", uiDefaults.get("textHighlight"));
        final DesktopProperty desktopProperty11 = new DesktopProperty("win.item.highlightTextColor", uiDefaults.get("textHighlightText"));
        final DesktopProperty desktopProperty12 = new DesktopProperty("win.frame.backgroundColor", uiDefaults.get("window"));
        final DesktopProperty desktopProperty13 = new DesktopProperty("win.frame.textColor", uiDefaults.get("windowText"));
        final DesktopProperty desktopProperty14 = new DesktopProperty("win.frame.sizingBorderWidth", 1);
        final DesktopProperty desktopProperty15 = new DesktopProperty("win.frame.captionHeight", 18);
        final DesktopProperty desktopProperty16 = new DesktopProperty("win.frame.captionButtonWidth", 16);
        final DesktopProperty desktopProperty17 = new DesktopProperty("win.frame.captionButtonHeight", 16);
        final DesktopProperty desktopProperty18 = new DesktopProperty("win.text.grayedTextColor", uiDefaults.get("textInactiveText"));
        final DesktopProperty desktopProperty19 = new DesktopProperty("win.scrollbar.backgroundColor", uiDefaults.get("scrollbar"));
        final FocusColorProperty focusColorProperty = new FocusColorProperty();
        final XPColorValue xpColorValue = new XPColorValue(TMSchema.Part.EP_EDIT, null, TMSchema.Prop.FILLCOLOR, desktopProperty12);
        final DesktopProperty desktopProperty20 = desktopProperty;
        final DesktopProperty desktopProperty21 = desktopProperty;
        Object desktopFontValue = swingLazyValue;
        Object desktopFontValue2 = swingLazyValue3;
        Object desktopFontValue3 = swingLazyValue;
        Object desktopFontValue4 = swingLazyValue;
        Object desktopFontValue5 = swingLazyValue4;
        Object desktopFontValue6 = swingLazyValue2;
        Object desktopFontValue7 = desktopFontValue3;
        final DesktopProperty desktopProperty22 = new DesktopProperty("win.scrollbar.width", 16);
        final DesktopProperty desktopProperty23 = new DesktopProperty("win.menu.height", null);
        final DesktopProperty desktopProperty24 = new DesktopProperty("win.item.hotTrackingOn", true);
        final DesktopProperty desktopProperty25 = new DesktopProperty("win.menu.keyboardCuesOn", Boolean.TRUE);
        if (this.useSystemFontSettings) {
            desktopFontValue = this.getDesktopFontValue("win.menu.font", desktopFontValue);
            desktopFontValue2 = this.getDesktopFontValue("win.ansiFixed.font", desktopFontValue2);
            desktopFontValue3 = this.getDesktopFontValue("win.defaultGUI.font", desktopFontValue3);
            desktopFontValue4 = this.getDesktopFontValue("win.messagebox.font", desktopFontValue4);
            desktopFontValue5 = this.getDesktopFontValue("win.frame.captionFont", desktopFontValue5);
            desktopFontValue7 = this.getDesktopFontValue("win.icon.font", desktopFontValue7);
            desktopFontValue6 = this.getDesktopFontValue("win.tooltip.font", desktopFontValue6);
            uiDefaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, SwingUtilities2.AATextInfo.getAATextInfo(true));
            this.aaSettings = new FontDesktopProperty("awt.font.desktophints");
        }
        if (this.useSystemFontSizeSettings) {
            desktopFontValue = new WindowsFontSizeProperty("win.menu.font.height", "Dialog", 0, 12);
            desktopFontValue2 = new WindowsFontSizeProperty("win.ansiFixed.font.height", "Monospaced", 0, 12);
            desktopFontValue3 = new WindowsFontSizeProperty("win.defaultGUI.font.height", "Dialog", 0, 12);
            desktopFontValue4 = new WindowsFontSizeProperty("win.messagebox.font.height", "Dialog", 0, 12);
            desktopFontValue5 = new WindowsFontSizeProperty("win.frame.captionFont.height", "Dialog", 1, 12);
            desktopFontValue6 = new WindowsFontSizeProperty("win.tooltip.font.height", "SansSerif", 0, 12);
            desktopFontValue7 = new WindowsFontSizeProperty("win.icon.font.height", "Dialog", 0, 12);
        }
        if (!(this instanceof WindowsClassicLookAndFeel) && OSInfo.getOSType() == OSInfo.OSType.WINDOWS && OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_XP) >= 0 && AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("swing.noxp")) == null) {
            this.themeActive = new TriggerDesktopProperty("win.xpstyle.themeActive");
            this.dllName = new TriggerDesktopProperty("win.xpstyle.dllName");
            this.colorName = new TriggerDesktopProperty("win.xpstyle.colorName");
            this.sizeName = new TriggerDesktopProperty("win.xpstyle.sizeName");
        }
        uiDefaults.putDefaults(new Object[] { "AuditoryCues.playList", null, "Application.useSystemFontSettings", this.useSystemFontSettings, "TextField.focusInputMap", lazyInputMap, "PasswordField.focusInputMap", lazyInputMap2, "TextArea.focusInputMap", lazyInputMap3, "TextPane.focusInputMap", lazyInputMap3, "EditorPane.focusInputMap", lazyInputMap3, "Button.font", desktopFontValue3, "Button.background", desktopProperty, "Button.foreground", desktopProperty6, "Button.shadow", desktopProperty4, "Button.darkShadow", desktopProperty5, "Button.light", desktopProperty2, "Button.highlight", desktopProperty3, "Button.disabledForeground", desktopProperty18, "Button.disabledShadow", desktopProperty3, "Button.focus", focusColorProperty, "Button.dashedRectGapX", new XPValue(3, 5), "Button.dashedRectGapY", new XPValue(3, 4), "Button.dashedRectGapWidth", new XPValue(6, 10), "Button.dashedRectGapHeight", new XPValue(6, 8), "Button.textShiftOffset", new XPValue(0, 1), "Button.showMnemonics", desktopProperty25, "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "Caret.width", new DesktopProperty("win.caret.width", null), "CheckBox.font", desktopFontValue3, "CheckBox.interiorBackground", desktopProperty12, "CheckBox.background", desktopProperty, "CheckBox.foreground", desktopProperty13, "CheckBox.shadow", desktopProperty4, "CheckBox.darkShadow", desktopProperty5, "CheckBox.light", desktopProperty2, "CheckBox.highlight", desktopProperty3, "CheckBox.focus", focusColorProperty, "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "CheckBox.totalInsets", new Insets(4, 4, 4, 4), "CheckBoxMenuItem.font", desktopFontValue, "CheckBoxMenuItem.background", desktopProperty7, "CheckBoxMenuItem.foreground", desktopProperty9, "CheckBoxMenuItem.selectionForeground", desktopProperty11, "CheckBoxMenuItem.selectionBackground", desktopProperty10, "CheckBoxMenuItem.acceleratorForeground", desktopProperty9, "CheckBoxMenuItem.acceleratorSelectionForeground", desktopProperty11, "CheckBoxMenuItem.commandSound", "win.sound.menuCommand", "ComboBox.font", desktopFontValue3, "ComboBox.background", desktopProperty12, "ComboBox.foreground", desktopProperty13, "ComboBox.buttonBackground", desktopProperty, "ComboBox.buttonShadow", desktopProperty4, "ComboBox.buttonDarkShadow", desktopProperty5, "ComboBox.buttonHighlight", desktopProperty3, "ComboBox.selectionBackground", desktopProperty10, "ComboBox.selectionForeground", desktopProperty11, "ComboBox.editorBorder", new XPValue(new EmptyBorder(1, 2, 1, 1), new EmptyBorder(1, 4, 1, 4)), "ComboBox.disabledBackground", new XPColorValue(TMSchema.Part.CP_COMBOBOX, TMSchema.State.DISABLED, TMSchema.Prop.FILLCOLOR, desktopProperty21), "ComboBox.disabledForeground", new XPColorValue(TMSchema.Part.CP_COMBOBOX, TMSchema.State.DISABLED, TMSchema.Prop.TEXTCOLOR, desktopProperty18), "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", "DOWN", "selectNext2", "KP_DOWN", "selectNext2", "UP", "selectPrevious2", "KP_UP", "selectPrevious2", "ENTER", "enterPressed", "F4", "togglePopup", "alt DOWN", "togglePopup", "alt KP_DOWN", "togglePopup", "alt UP", "togglePopup", "alt KP_UP", "togglePopup" }), "Desktop.background", new DesktopProperty("win.desktop.backgroundColor", uiDefaults.get("desktop")), "Desktop.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl F5", "restore", "ctrl F4", "close", "ctrl F7", "move", "ctrl F8", "resize", "RIGHT", "right", "KP_RIGHT", "right", "LEFT", "left", "KP_LEFT", "left", "UP", "up", "KP_UP", "up", "DOWN", "down", "KP_DOWN", "down", "ESCAPE", "escape", "ctrl F9", "minimize", "ctrl F10", "maximize", "ctrl F6", "selectNextFrame", "ctrl TAB", "selectNextFrame", "ctrl alt F6", "selectNextFrame", "shift ctrl alt F6", "selectPreviousFrame", "ctrl F12", "navigateNext", "shift ctrl F12", "navigatePrevious" }), "DesktopIcon.width", 160, "EditorPane.font", desktopFontValue3, "EditorPane.background", desktopProperty12, "EditorPane.foreground", desktopProperty13, "EditorPane.selectionBackground", desktopProperty10, "EditorPane.selectionForeground", desktopProperty11, "EditorPane.caretForeground", desktopProperty13, "EditorPane.inactiveForeground", desktopProperty18, "EditorPane.inactiveBackground", desktopProperty12, "EditorPane.disabledBackground", desktopProperty21, "FileChooser.homeFolderIcon", new LazyWindowsIcon(null, "icons/HomeFolder.gif"), "FileChooser.listFont", desktopFontValue7, "FileChooser.listViewBackground", new XPColorValue(TMSchema.Part.LVP_LISTVIEW, null, TMSchema.Prop.FILLCOLOR, desktopProperty12), "FileChooser.listViewBorder", new XPBorderValue(TMSchema.Part.LVP_LISTVIEW, new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getLoweredBevelBorderUIResource")), "FileChooser.listViewIcon", new LazyWindowsIcon("fileChooserIcon ListView", "icons/ListView.gif"), "FileChooser.listViewWindowsStyle", Boolean.TRUE, "FileChooser.detailsViewIcon", new LazyWindowsIcon("fileChooserIcon DetailsView", "icons/DetailsView.gif"), "FileChooser.viewMenuIcon", new LazyWindowsIcon("fileChooserIcon ViewMenu", "icons/ListView.gif"), "FileChooser.upFolderIcon", new LazyWindowsIcon("fileChooserIcon UpFolder", "icons/UpFolder.gif"), "FileChooser.newFolderIcon", new LazyWindowsIcon("fileChooserIcon NewFolder", "icons/NewFolder.gif"), "FileChooser.useSystemExtensionHiding", Boolean.TRUE, "FileChooser.usesSingleFilePane", Boolean.TRUE, "FileChooser.noPlacesBar", new DesktopProperty("win.comdlg.noPlacesBar", Boolean.FALSE), "FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancelSelection", "F2", "editFileName", "F5", "refresh", "BACK_SPACE", "Go Up" }), "FileView.directoryIcon", SwingUtilities2.makeIcon(this.getClass(), WindowsLookAndFeel.class, "icons/Directory.gif"), "FileView.fileIcon", SwingUtilities2.makeIcon(this.getClass(), WindowsLookAndFeel.class, "icons/File.gif"), "FileView.computerIcon", SwingUtilities2.makeIcon(this.getClass(), WindowsLookAndFeel.class, "icons/Computer.gif"), "FileView.hardDriveIcon", SwingUtilities2.makeIcon(this.getClass(), WindowsLookAndFeel.class, "icons/HardDrive.gif"), "FileView.floppyDriveIcon", SwingUtilities2.makeIcon(this.getClass(), WindowsLookAndFeel.class, "icons/FloppyDrive.gif"), "FormattedTextField.font", desktopFontValue3, "InternalFrame.titleFont", desktopFontValue5, "InternalFrame.titlePaneHeight", desktopProperty15, "InternalFrame.titleButtonWidth", desktopProperty16, "InternalFrame.titleButtonHeight", desktopProperty17, "InternalFrame.titleButtonToolTipsOn", desktopProperty24, "InternalFrame.borderColor", desktopProperty, "InternalFrame.borderShadow", desktopProperty4, "InternalFrame.borderDarkShadow", desktopProperty5, "InternalFrame.borderHighlight", desktopProperty3, "InternalFrame.borderLight", desktopProperty2, "InternalFrame.borderWidth", desktopProperty14, "InternalFrame.minimizeIconBackground", desktopProperty, "InternalFrame.resizeIconHighlight", desktopProperty2, "InternalFrame.resizeIconShadow", desktopProperty4, "InternalFrame.activeBorderColor", new DesktopProperty("win.frame.activeBorderColor", uiDefaults.get("windowBorder")), "InternalFrame.inactiveBorderColor", new DesktopProperty("win.frame.inactiveBorderColor", uiDefaults.get("windowBorder")), "InternalFrame.activeTitleBackground", new DesktopProperty("win.frame.activeCaptionColor", uiDefaults.get("activeCaption")), "InternalFrame.activeTitleGradient", new DesktopProperty("win.frame.activeCaptionGradientColor", uiDefaults.get("activeCaption")), "InternalFrame.activeTitleForeground", new DesktopProperty("win.frame.captionTextColor", uiDefaults.get("activeCaptionText")), "InternalFrame.inactiveTitleBackground", new DesktopProperty("win.frame.inactiveCaptionColor", uiDefaults.get("inactiveCaption")), "InternalFrame.inactiveTitleGradient", new DesktopProperty("win.frame.inactiveCaptionGradientColor", uiDefaults.get("inactiveCaption")), "InternalFrame.inactiveTitleForeground", new DesktopProperty("win.frame.inactiveCaptionTextColor", uiDefaults.get("inactiveCaptionText")), "InternalFrame.maximizeIcon", WindowsIconFactory.createFrameMaximizeIcon(), "InternalFrame.minimizeIcon", WindowsIconFactory.createFrameMinimizeIcon(), "InternalFrame.iconifyIcon", WindowsIconFactory.createFrameIconifyIcon(), "InternalFrame.closeIcon", WindowsIconFactory.createFrameCloseIcon(), "InternalFrame.icon", new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsInternalFrameTitlePane$ScalableIconUIResource", new Object[][] { { SwingUtilities2.makeIcon(this.getClass(), BasicLookAndFeel.class, "icons/JavaCup16.png"), SwingUtilities2.makeIcon(this.getClass(), WindowsLookAndFeel.class, "icons/JavaCup32.png") } }), "InternalFrame.closeSound", "win.sound.close", "InternalFrame.maximizeSound", "win.sound.maximize", "InternalFrame.minimizeSound", "win.sound.minimize", "InternalFrame.restoreDownSound", "win.sound.restoreDown", "InternalFrame.restoreUpSound", "win.sound.restoreUp", "InternalFrame.windowBindings", { "shift ESCAPE", "showSystemMenu", "ctrl SPACE", "showSystemMenu", "ESCAPE", "hideSystemMenu" }, "Label.font", desktopFontValue3, "Label.background", desktopProperty, "Label.foreground", desktopProperty13, "Label.disabledForeground", desktopProperty18, "Label.disabledShadow", desktopProperty3, "List.font", desktopFontValue3, "List.background", desktopProperty12, "List.foreground", desktopProperty13, "List.selectionBackground", desktopProperty10, "List.selectionForeground", desktopProperty11, "List.lockToPositionOnScroll", Boolean.TRUE, "List.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "HOME", "selectFirstRow", "shift HOME", "selectFirstRowExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRowChangeLead", "END", "selectLastRow", "shift END", "selectLastRowExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRowChangeLead", "PAGE_UP", "scrollUp", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDown", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), "PopupMenu.font", desktopFontValue, "PopupMenu.background", desktopProperty7, "PopupMenu.foreground", desktopProperty9, "PopupMenu.popupSound", "win.sound.menuPopup", "PopupMenu.consumeEventOnClose", Boolean.TRUE, "Menu.font", desktopFontValue, "Menu.foreground", desktopProperty9, "Menu.background", desktopProperty7, "Menu.useMenuBarBackgroundForTopLevel", Boolean.TRUE, "Menu.selectionForeground", desktopProperty11, "Menu.selectionBackground", desktopProperty10, "Menu.acceleratorForeground", desktopProperty9, "Menu.acceleratorSelectionForeground", desktopProperty11, "Menu.menuPopupOffsetX", 0, "Menu.menuPopupOffsetY", 0, "Menu.submenuPopupOffsetX", -4, "Menu.submenuPopupOffsetY", -3, "Menu.crossMenuMnemonic", Boolean.FALSE, "Menu.preserveTopLevelSelection", Boolean.TRUE, "MenuBar.font", desktopFontValue, "MenuBar.background", new XPValue(desktopProperty8, desktopProperty7), "MenuBar.foreground", desktopProperty9, "MenuBar.shadow", desktopProperty4, "MenuBar.highlight", desktopProperty3, "MenuBar.height", desktopProperty23, "MenuBar.rolloverEnabled", desktopProperty24, "MenuBar.windowBindings", { "F10", "takeFocus" }, "MenuItem.font", desktopFontValue, "MenuItem.acceleratorFont", desktopFontValue, "MenuItem.foreground", desktopProperty9, "MenuItem.background", desktopProperty7, "MenuItem.selectionForeground", desktopProperty11, "MenuItem.selectionBackground", desktopProperty10, "MenuItem.disabledForeground", desktopProperty18, "MenuItem.acceleratorForeground", desktopProperty9, "MenuItem.acceleratorSelectionForeground", desktopProperty11, "MenuItem.acceleratorDelimiter", s, "MenuItem.commandSound", "win.sound.menuCommand", "MenuItem.disabledAreNavigable", Boolean.TRUE, "RadioButton.font", desktopFontValue3, "RadioButton.interiorBackground", desktopProperty12, "RadioButton.background", desktopProperty, "RadioButton.foreground", desktopProperty13, "RadioButton.shadow", desktopProperty4, "RadioButton.darkShadow", desktopProperty5, "RadioButton.light", desktopProperty2, "RadioButton.highlight", desktopProperty3, "RadioButton.focus", focusColorProperty, "RadioButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "RadioButton.totalInsets", new Insets(4, 4, 4, 4), "RadioButtonMenuItem.font", desktopFontValue, "RadioButtonMenuItem.foreground", desktopProperty9, "RadioButtonMenuItem.background", desktopProperty7, "RadioButtonMenuItem.selectionForeground", desktopProperty11, "RadioButtonMenuItem.selectionBackground", desktopProperty10, "RadioButtonMenuItem.disabledForeground", desktopProperty18, "RadioButtonMenuItem.acceleratorForeground", desktopProperty9, "RadioButtonMenuItem.acceleratorSelectionForeground", desktopProperty11, "RadioButtonMenuItem.commandSound", "win.sound.menuCommand", "OptionPane.font", desktopFontValue4, "OptionPane.messageFont", desktopFontValue4, "OptionPane.buttonFont", desktopFontValue4, "OptionPane.background", desktopProperty, "OptionPane.foreground", desktopProperty13, "OptionPane.buttonMinimumWidth", new XPDLUValue(50, 50, 3), "OptionPane.messageForeground", desktopProperty6, "OptionPane.errorIcon", new LazyWindowsIcon("optionPaneIcon Error", "icons/Error.gif"), "OptionPane.informationIcon", new LazyWindowsIcon("optionPaneIcon Information", "icons/Inform.gif"), "OptionPane.questionIcon", new LazyWindowsIcon("optionPaneIcon Question", "icons/Question.gif"), "OptionPane.warningIcon", new LazyWindowsIcon("optionPaneIcon Warning", "icons/Warn.gif"), "OptionPane.windowBindings", { "ESCAPE", "close" }, "OptionPane.errorSound", "win.sound.hand", "OptionPane.informationSound", "win.sound.asterisk", "OptionPane.questionSound", "win.sound.question", "OptionPane.warningSound", "win.sound.exclamation", "FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "FormattedTextField.inactiveBackground", desktopProperty20, "FormattedTextField.disabledBackground", desktopProperty21, "Panel.font", desktopFontValue3, "Panel.background", desktopProperty, "Panel.foreground", desktopProperty13, "PasswordField.font", desktopFontValue3, "PasswordField.background", xpColorValue, "PasswordField.foreground", desktopProperty13, "PasswordField.inactiveForeground", desktopProperty18, "PasswordField.inactiveBackground", desktopProperty20, "PasswordField.disabledBackground", desktopProperty21, "PasswordField.selectionBackground", desktopProperty10, "PasswordField.selectionForeground", desktopProperty11, "PasswordField.caretForeground", desktopProperty13, "PasswordField.echoChar", new XPValue(new Character('\u25cf'), new Character('*')), "ProgressBar.font", desktopFontValue3, "ProgressBar.foreground", desktopProperty10, "ProgressBar.background", desktopProperty, "ProgressBar.shadow", desktopProperty4, "ProgressBar.highlight", desktopProperty3, "ProgressBar.selectionForeground", desktopProperty, "ProgressBar.selectionBackground", desktopProperty10, "ProgressBar.cellLength", 7, "ProgressBar.cellSpacing", 2, "ProgressBar.indeterminateInsets", new Insets(3, 3, 3, 3), "RootPane.defaultButtonWindowKeyBindings", { "ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release" }, "ScrollBar.background", desktopProperty19, "ScrollBar.foreground", desktopProperty, "ScrollBar.track", colorUIResource3, "ScrollBar.trackForeground", desktopProperty19, "ScrollBar.trackHighlight", colorUIResource2, "ScrollBar.trackHighlightForeground", colorUIResource5, "ScrollBar.thumb", desktopProperty, "ScrollBar.thumbHighlight", desktopProperty3, "ScrollBar.thumbDarkShadow", desktopProperty5, "ScrollBar.thumbShadow", desktopProperty4, "ScrollBar.width", desktopProperty22, "ScrollBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", "ctrl PAGE_DOWN", "positiveBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", "ctrl PAGE_UP", "negativeBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }), "ScrollPane.font", desktopFontValue3, "ScrollPane.background", desktopProperty, "ScrollPane.foreground", desktopProperty6, "ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd" }), "Separator.background", desktopProperty3, "Separator.foreground", desktopProperty4, "Slider.font", desktopFontValue3, "Slider.foreground", desktopProperty, "Slider.background", desktopProperty, "Slider.highlight", desktopProperty3, "Slider.shadow", desktopProperty4, "Slider.focus", desktopProperty5, "Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "PAGE_UP", "positiveBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }), "Spinner.font", desktopFontValue3, "Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "SplitPane.background", desktopProperty, "SplitPane.highlight", desktopProperty3, "SplitPane.shadow", desktopProperty4, "SplitPane.darkShadow", desktopProperty5, "SplitPane.dividerSize", 5, "SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward" }), "TabbedPane.tabsOverlapBorder", new XPValue(Boolean.TRUE, Boolean.FALSE), "TabbedPane.tabInsets", new XPValue(new InsetsUIResource(1, 4, 1, 4), new InsetsUIResource(0, 4, 1, 4)), "TabbedPane.tabAreaInsets", new XPValue(new InsetsUIResource(3, 2, 2, 2), new InsetsUIResource(3, 2, 0, 2)), "TabbedPane.font", desktopFontValue3, "TabbedPane.background", desktopProperty, "TabbedPane.foreground", desktopProperty6, "TabbedPane.highlight", desktopProperty3, "TabbedPane.light", desktopProperty2, "TabbedPane.shadow", desktopProperty4, "TabbedPane.darkShadow", desktopProperty5, "TabbedPane.focus", desktopProperty6, "TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent" }), "TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl TAB", "navigateNext", "ctrl shift TAB", "navigatePrevious", "ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus" }), "Table.font", desktopFontValue3, "Table.foreground", desktopProperty6, "Table.background", desktopProperty12, "Table.highlight", desktopProperty3, "Table.light", desktopProperty2, "Table.shadow", desktopProperty4, "Table.darkShadow", desktopProperty5, "Table.selectionForeground", desktopProperty11, "Table.selectionBackground", desktopProperty10, "Table.gridColor", colorUIResource4, "Table.focusCellBackground", desktopProperty12, "Table.focusCellForeground", desktopProperty6, "Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "HOME", "selectFirstColumn", "shift HOME", "selectFirstColumnExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRow", "END", "selectLastColumn", "shift END", "selectLastColumnExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRow", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollLeftExtendSelection", "ctrl PAGE_UP", "scrollLeftChangeSelection", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollRightExtendSelection", "ctrl PAGE_DOWN", "scrollRightChangeSelection", "TAB", "selectNextColumnCell", "shift TAB", "selectPreviousColumnCell", "ENTER", "selectNextRowCell", "shift ENTER", "selectPreviousRowCell", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ESCAPE", "cancel", "F2", "startEditing", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo", "F8", "focusHeader" }), "Table.sortIconHighlight", desktopProperty4, "Table.sortIconLight", colorUIResource3, "TableHeader.font", desktopFontValue3, "TableHeader.foreground", desktopProperty6, "TableHeader.background", desktopProperty, "TableHeader.focusCellBackground", new XPValue(XPValue.NULL_VALUE, desktopProperty12), "TextArea.font", desktopFontValue2, "TextArea.background", desktopProperty12, "TextArea.foreground", desktopProperty13, "TextArea.inactiveForeground", desktopProperty18, "TextArea.inactiveBackground", desktopProperty12, "TextArea.disabledBackground", desktopProperty21, "TextArea.selectionBackground", desktopProperty10, "TextArea.selectionForeground", desktopProperty11, "TextArea.caretForeground", desktopProperty13, "TextField.font", desktopFontValue3, "TextField.background", xpColorValue, "TextField.foreground", desktopProperty13, "TextField.shadow", desktopProperty4, "TextField.darkShadow", desktopProperty5, "TextField.light", desktopProperty2, "TextField.highlight", desktopProperty3, "TextField.inactiveForeground", desktopProperty18, "TextField.inactiveBackground", desktopProperty20, "TextField.disabledBackground", desktopProperty21, "TextField.selectionBackground", desktopProperty10, "TextField.selectionForeground", desktopProperty11, "TextField.caretForeground", desktopProperty13, "TextPane.font", desktopFontValue3, "TextPane.background", desktopProperty12, "TextPane.foreground", desktopProperty13, "TextPane.selectionBackground", desktopProperty10, "TextPane.selectionForeground", desktopProperty11, "TextPane.inactiveBackground", desktopProperty12, "TextPane.disabledBackground", desktopProperty21, "TextPane.caretForeground", desktopProperty13, "TitledBorder.font", desktopFontValue3, "TitledBorder.titleColor", new XPColorValue(TMSchema.Part.BP_GROUPBOX, null, TMSchema.Prop.TEXTCOLOR, desktopProperty13), "ToggleButton.font", desktopFontValue3, "ToggleButton.background", desktopProperty, "ToggleButton.foreground", desktopProperty6, "ToggleButton.shadow", desktopProperty4, "ToggleButton.darkShadow", desktopProperty5, "ToggleButton.light", desktopProperty2, "ToggleButton.highlight", desktopProperty3, "ToggleButton.focus", desktopProperty6, "ToggleButton.textShiftOffset", 1, "ToggleButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "ToolBar.font", desktopFontValue, "ToolBar.background", desktopProperty, "ToolBar.foreground", desktopProperty6, "ToolBar.shadow", desktopProperty4, "ToolBar.darkShadow", desktopProperty5, "ToolBar.light", desktopProperty2, "ToolBar.highlight", desktopProperty3, "ToolBar.dockingBackground", desktopProperty, "ToolBar.dockingForeground", colorUIResource, "ToolBar.floatingBackground", desktopProperty, "ToolBar.floatingForeground", colorUIResource5, "ToolBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight" }), "ToolBar.separatorSize", null, "ToolTip.font", desktopFontValue6, "ToolTip.background", new DesktopProperty("win.tooltip.backgroundColor", uiDefaults.get("info")), "ToolTip.foreground", new DesktopProperty("win.tooltip.textColor", uiDefaults.get("infoText")), "ToolTipManager.enableToolTipMode", "activeApplication", "Tree.selectionBorderColor", colorUIResource2, "Tree.drawDashedFocusIndicator", Boolean.TRUE, "Tree.lineTypeDashed", Boolean.TRUE, "Tree.font", desktopFontValue3, "Tree.background", desktopProperty12, "Tree.foreground", desktopProperty13, "Tree.hash", colorUIResource4, "Tree.leftChildIndent", 8, "Tree.rightChildIndent", 11, "Tree.textForeground", desktopProperty13, "Tree.textBackground", desktopProperty12, "Tree.selectionForeground", desktopProperty11, "Tree.selectionBackground", desktopProperty10, "Tree.expandedIcon", expandedIcon, "Tree.collapsedIcon", collapsedIcon, "Tree.openIcon", new ActiveWindowsIcon("win.icon.shellIconBPP", "shell32Icon 5", "icons/TreeOpen.gif"), "Tree.closedIcon", new ActiveWindowsIcon("win.icon.shellIconBPP", "shell32Icon 4", "icons/TreeClosed.gif"), "Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ADD", "expand", "SUBTRACT", "collapse", "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPrevious", "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", "ctrl shift UP", "selectPreviousExtendSelection", "ctrl shift KP_UP", "selectPreviousExtendSelection", "ctrl UP", "selectPreviousChangeLead", "ctrl KP_UP", "selectPreviousChangeLead", "DOWN", "selectNext", "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", "shift KP_DOWN", "selectNextExtendSelection", "ctrl shift DOWN", "selectNextExtendSelection", "ctrl shift KP_DOWN", "selectNextExtendSelection", "ctrl DOWN", "selectNextChangeLead", "ctrl KP_DOWN", "selectNextChangeLead", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "LEFT", "selectParent", "KP_LEFT", "selectParent", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "HOME", "selectFirst", "shift HOME", "selectFirstExtendSelection", "ctrl shift HOME", "selectFirstExtendSelection", "ctrl HOME", "selectFirstChangeLead", "END", "selectLast", "shift END", "selectLastExtendSelection", "ctrl shift END", "selectLastExtendSelection", "ctrl END", "selectLastChangeLead", "F2", "startEditing", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ctrl LEFT", "scrollLeft", "ctrl KP_LEFT", "scrollLeft", "ctrl RIGHT", "scrollRight", "ctrl KP_RIGHT", "scrollRight", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), "Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancel" }), "Viewport.font", desktopFontValue3, "Viewport.background", desktopProperty, "Viewport.foreground", desktopProperty13 });
        uiDefaults.putDefaults(this.getLazyValueDefaults());
        this.initVistaComponentDefaults(uiDefaults);
    }
    
    static boolean isOnVista() {
        return OSInfo.getOSType() == OSInfo.OSType.WINDOWS && OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_VISTA) >= 0;
    }
    
    private void initVistaComponentDefaults(final UIDefaults uiDefaults) {
        if (!isOnVista()) {
            return;
        }
        final String[] array = { "MenuItem", "Menu", "CheckBoxMenuItem", "RadioButtonMenuItem" };
        final Object[] array2 = new Object[array.length * 2];
        int i = 0;
        int n = 0;
        while (i < array.length) {
            final String string = array[i] + ".opaque";
            final Object value = uiDefaults.get(string);
            array2[n++] = string;
            array2[n++] = new XPValue(Boolean.FALSE, value);
            ++i;
        }
        uiDefaults.putDefaults(array2);
        int j = 0;
        int n2 = 0;
        while (j < array.length) {
            final String string2 = array[j] + ".acceleratorSelectionForeground";
            final Object value2 = uiDefaults.get(string2);
            array2[n2++] = string2;
            array2[n2++] = new XPValue(uiDefaults.getColor(array[j] + ".acceleratorForeground"), value2);
            ++j;
        }
        uiDefaults.putDefaults(array2);
        final WindowsIconFactory.VistaMenuItemCheckIconFactory menuItemCheckIconFactory = WindowsIconFactory.getMenuItemCheckIconFactory();
        int k = 0;
        int n3 = 0;
        while (k < array.length) {
            final String string3 = array[k] + ".checkIconFactory";
            final Object value3 = uiDefaults.get(string3);
            array2[n3++] = string3;
            array2[n3++] = new XPValue(menuItemCheckIconFactory, value3);
            ++k;
        }
        uiDefaults.putDefaults(array2);
        int l = 0;
        int n4 = 0;
        while (l < array.length) {
            final String string4 = array[l] + ".checkIcon";
            final Object value4 = uiDefaults.get(string4);
            array2[n4++] = string4;
            array2[n4++] = new XPValue(menuItemCheckIconFactory.getIcon(array[l]), value4);
            ++l;
        }
        uiDefaults.putDefaults(array2);
        int n5 = 0;
        int n6 = 0;
        while (n5 < array.length) {
            final String string5 = array[n5] + ".evenHeight";
            final Object value5 = uiDefaults.get(string5);
            array2[n6++] = string5;
            array2[n6++] = new XPValue(Boolean.TRUE, value5);
            ++n5;
        }
        uiDefaults.putDefaults(array2);
        final InsetsUIResource insetsUIResource = new InsetsUIResource(0, 0, 0, 0);
        int n7 = 0;
        int n8 = 0;
        while (n7 < array.length) {
            final String string6 = array[n7] + ".margin";
            final Object value6 = uiDefaults.get(string6);
            array2[n8++] = string6;
            array2[n8++] = new XPValue(insetsUIResource, value6);
            ++n7;
        }
        uiDefaults.putDefaults(array2);
        final Integer value7 = 0;
        int n9 = 0;
        int n10 = 0;
        while (n9 < array.length) {
            final String string7 = array[n9] + ".checkIconOffset";
            final Object value8 = uiDefaults.get(string7);
            array2[n10++] = string7;
            array2[n10++] = new XPValue(value7, value8);
            ++n9;
        }
        uiDefaults.putDefaults(array2);
        final Integer value9 = WindowsPopupMenuUI.getSpanBeforeGutter() + WindowsPopupMenuUI.getGutterWidth() + WindowsPopupMenuUI.getSpanAfterGutter();
        int n11 = 0;
        int n12 = 0;
        while (n11 < array.length) {
            final String string8 = array[n11] + ".afterCheckIconGap";
            final Object value10 = uiDefaults.get(string8);
            array2[n12++] = string8;
            array2[n12++] = new XPValue(value9, value10);
            ++n11;
        }
        uiDefaults.putDefaults(array2);
        final UIDefaults.ActiveValue activeValue = new UIDefaults.ActiveValue() {
            @Override
            public Object createValue(final UIDefaults uiDefaults) {
                return WindowsIconFactory.VistaMenuItemCheckIconFactory.getIconWidth() + WindowsPopupMenuUI.getSpanBeforeGutter() + WindowsPopupMenuUI.getGutterWidth() + WindowsPopupMenuUI.getSpanAfterGutter();
            }
        };
        int n13 = 0;
        int n14 = 0;
        while (n13 < array.length) {
            final String string9 = array[n13] + ".minimumTextOffset";
            final Object value11 = uiDefaults.get(string9);
            array2[n14++] = string9;
            array2[n14++] = new XPValue(activeValue, value11);
            ++n13;
        }
        uiDefaults.putDefaults(array2);
        uiDefaults.put("PopupMenu.border", new XPBorderValue(TMSchema.Part.MENU, new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getInternalFrameBorder"), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        uiDefaults.put("Table.ascendingSortIcon", new XPValue(new SkinIcon(TMSchema.Part.HP_HEADERSORTARROW, TMSchema.State.SORTEDDOWN), new SwingLazyValue("sun.swing.plaf.windows.ClassicSortArrowIcon", null, new Object[] { Boolean.TRUE })));
        uiDefaults.put("Table.descendingSortIcon", new XPValue(new SkinIcon(TMSchema.Part.HP_HEADERSORTARROW, TMSchema.State.SORTEDUP), new SwingLazyValue("sun.swing.plaf.windows.ClassicSortArrowIcon", null, new Object[] { Boolean.FALSE })));
    }
    
    private Object getDesktopFontValue(final String s, final Object o) {
        if (this.useSystemFontSettings) {
            return new WindowsFontProperty(s, o);
        }
        return null;
    }
    
    private Object[] getLazyValueDefaults() {
        final XPBorderValue xpBorderValue = new XPBorderValue(TMSchema.Part.BP_PUSHBUTTON, new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getButtonBorder"));
        final XPBorderValue xpBorderValue2 = new XPBorderValue(TMSchema.Part.EP_EDIT, new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getTextFieldBorder"));
        final XPValue xpValue = new XPValue(new InsetsUIResource(2, 2, 2, 2), new InsetsUIResource(1, 1, 1, 1));
        final XPBorderValue xpBorderValue3 = new XPBorderValue(TMSchema.Part.EP_EDIT, xpBorderValue2, new EmptyBorder(2, 2, 2, 2));
        final XPValue xpValue2 = new XPValue(new InsetsUIResource(1, 1, 1, 1), null);
        final XPBorderValue xpBorderValue4 = new XPBorderValue(TMSchema.Part.CP_COMBOBOX, xpBorderValue2);
        final SwingLazyValue swingLazyValue = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getFocusCellHighlightBorder");
        final SwingLazyValue swingLazyValue2 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getEtchedBorderUIResource");
        final SwingLazyValue swingLazyValue3 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getInternalFrameBorder");
        final SwingLazyValue swingLazyValue4 = new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getLoweredBevelBorderUIResource");
        final SwingLazyValue swingLazyValue5 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders$MarginBorder");
        final SwingLazyValue swingLazyValue6 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getMenuBarBorder");
        final XPBorderValue xpBorderValue5 = new XPBorderValue(TMSchema.Part.MENU, new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getInternalFrameBorder"));
        final SwingLazyValue swingLazyValue7 = new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getProgressBarBorder");
        final SwingLazyValue swingLazyValue8 = new SwingLazyValue("javax.swing.plaf.basic.BasicBorders", "getRadioButtonBorder");
        return new Object[] { "Button.border", xpBorderValue, "CheckBox.border", swingLazyValue8, "ComboBox.border", xpBorderValue4, "DesktopIcon.border", swingLazyValue3, "FormattedTextField.border", xpBorderValue2, "FormattedTextField.margin", xpValue, "InternalFrame.border", swingLazyValue3, "List.focusCellHighlightBorder", swingLazyValue, "Table.focusCellHighlightBorder", swingLazyValue, "Menu.border", swingLazyValue5, "MenuBar.border", swingLazyValue6, "MenuItem.border", swingLazyValue5, "PasswordField.border", xpBorderValue2, "PasswordField.margin", xpValue, "PopupMenu.border", xpBorderValue5, "ProgressBar.border", swingLazyValue7, "RadioButton.border", swingLazyValue8, "ScrollPane.border", new XPBorderValue(TMSchema.Part.LBP_LISTBOX, xpBorderValue2), "Spinner.border", xpBorderValue3, "Spinner.arrowButtonInsets", xpValue2, "Spinner.arrowButtonSize", new Dimension(17, 9), "Table.scrollPaneBorder", new XPBorderValue(TMSchema.Part.LBP_LISTBOX, swingLazyValue4), "TableHeader.cellBorder", new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getTableHeaderBorder"), "TextArea.margin", xpValue, "TextField.border", xpBorderValue2, "TextField.margin", xpValue, "TitledBorder.border", new XPBorderValue(TMSchema.Part.BP_GROUPBOX, swingLazyValue2), "ToggleButton.border", swingLazyValue8, "ToolBar.border", new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsBorders", "getToolBarBorder"), "ToolTip.border", new SwingLazyValue("javax.swing.plaf.BorderUIResource", "getBlackLineBorderUIResource"), "CheckBox.icon", new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getCheckBoxIcon"), "Menu.arrowIcon", new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getMenuArrowIcon"), "MenuItem.checkIcon", new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getMenuItemCheckIcon"), "MenuItem.arrowIcon", new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getMenuItemArrowIcon"), "RadioButton.icon", new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getRadioButtonIcon"), "RadioButtonMenuItem.checkIcon", new SwingLazyValue("com.sun.java.swing.plaf.windows.WindowsIconFactory", "getRadioButtonMenuItemIcon"), "InternalFrame.layoutTitlePaneAtOrigin", new XPValue(Boolean.TRUE, Boolean.FALSE), "Table.ascendingSortIcon", new XPValue(new SwingLazyValue("sun.swing.icon.SortArrowIcon", null, new Object[] { Boolean.TRUE, "Table.sortIconColor" }), new SwingLazyValue("sun.swing.plaf.windows.ClassicSortArrowIcon", null, new Object[] { Boolean.TRUE })), "Table.descendingSortIcon", new XPValue(new SwingLazyValue("sun.swing.icon.SortArrowIcon", null, new Object[] { Boolean.FALSE, "Table.sortIconColor" }), new SwingLazyValue("sun.swing.plaf.windows.ClassicSortArrowIcon", null, new Object[] { Boolean.FALSE })) };
    }
    
    @Override
    public void uninitialize() {
        super.uninitialize();
        if (WindowsPopupMenuUI.mnemonicListener != null) {
            MenuSelectionManager.defaultManager().removeChangeListener(WindowsPopupMenuUI.mnemonicListener);
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventPostProcessor(WindowsRootPaneUI.altProcessor);
        DesktopProperty.flushUnreferencedProperties();
    }
    
    public static void setMnemonicHidden(final boolean isMnemonicHidden) {
        if (UIManager.getBoolean("Button.showMnemonics")) {
            WindowsLookAndFeel.isMnemonicHidden = false;
        }
        else {
            WindowsLookAndFeel.isMnemonicHidden = isMnemonicHidden;
        }
    }
    
    public static boolean isMnemonicHidden() {
        if (UIManager.getBoolean("Button.showMnemonics")) {
            WindowsLookAndFeel.isMnemonicHidden = false;
        }
        return WindowsLookAndFeel.isMnemonicHidden;
    }
    
    public static boolean isClassicWindows() {
        return WindowsLookAndFeel.isClassicWindows;
    }
    
    @Override
    public void provideErrorFeedback(final Component component) {
        super.provideErrorFeedback(component);
    }
    
    @Override
    public LayoutStyle getLayoutStyle() {
        LayoutStyle style = this.style;
        if (style == null) {
            style = new WindowsLayoutStyle();
            this.style = style;
        }
        return style;
    }
    
    @Override
    protected Action createAudioAction(final Object o) {
        if (o != null) {
            return new AudioAction((String)o, (String)UIManager.get(o));
        }
        return null;
    }
    
    static void repaintRootPane(Component parent) {
        Component component = null;
        while (parent != null) {
            if (parent instanceof JRootPane) {
                component = parent;
            }
            parent = parent.getParent();
        }
        if (component != null) {
            component.repaint();
        }
        else {
            parent.repaint();
        }
    }
    
    private int dluToPixels(final int n, final int n2) {
        if (this.baseUnitX == 0) {
            this.calculateBaseUnits();
        }
        if (n2 == 3 || n2 == 7) {
            return n * this.baseUnitX / 4;
        }
        assert n2 == 5;
        return n * this.baseUnitY / 8;
    }
    
    private void calculateBaseUnits() {
        final FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(UIManager.getFont("Button.font"));
        this.baseUnitX = fontMetrics.stringWidth("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        this.baseUnitX = (this.baseUnitX / 26 + 1) / 2;
        this.baseUnitY = fontMetrics.getAscent() + fontMetrics.getDescent() - 1;
    }
    
    @Override
    public Icon getDisabledIcon(final JComponent component, final Icon icon) {
        if (icon != null && component != null && Boolean.TRUE.equals(component.getClientProperty(WindowsLookAndFeel.HI_RES_DISABLED_ICON_CLIENT_KEY)) && icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            final BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconWidth(), 2);
            icon.paintIcon(component, bufferedImage.getGraphics(), 0, 0);
            return new ImageIconUIResource(component.createImage(new FilteredImageSource(bufferedImage.getSource(), new RGBGrayFilter())));
        }
        return super.getDisabledIcon(component, icon);
    }
    
    static {
        HI_RES_DISABLED_ICON_CLIENT_KEY = new StringUIClientPropertyKey("WindowsLookAndFeel.generateHiResDisabledIcon");
        WindowsLookAndFeel.isMnemonicHidden = true;
        WindowsLookAndFeel.isClassicWindows = false;
    }
    
    private static class AudioAction extends AbstractAction
    {
        private Runnable audioRunnable;
        private String audioResource;
        
        public AudioAction(final String s, final String audioResource) {
            super(s);
            this.audioResource = audioResource;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (this.audioRunnable == null) {
                this.audioRunnable = (Runnable)Toolkit.getDefaultToolkit().getDesktopProperty(this.audioResource);
            }
            if (this.audioRunnable != null) {
                new Thread(this.audioRunnable).start();
            }
        }
    }
    
    private static class LazyWindowsIcon implements UIDefaults.LazyValue
    {
        private String nativeImage;
        private String resource;
        
        LazyWindowsIcon(final String nativeImage, final String resource) {
            this.nativeImage = nativeImage;
            this.resource = resource;
        }
        
        @Override
        public Object createValue(final UIDefaults uiDefaults) {
            if (this.nativeImage != null) {
                final Image image = (Image)ShellFolder.get(this.nativeImage);
                if (image != null) {
                    return new ImageIcon(image);
                }
            }
            return SwingUtilities2.makeIcon(this.getClass(), WindowsLookAndFeel.class, this.resource);
        }
    }
    
    private class ActiveWindowsIcon implements UIDefaults.ActiveValue
    {
        private Icon icon;
        private String nativeImageName;
        private String fallbackName;
        private DesktopProperty desktopProperty;
        
        ActiveWindowsIcon(final String s, final String nativeImageName, final String fallbackName) {
            this.nativeImageName = nativeImageName;
            this.fallbackName = fallbackName;
            if (OSInfo.getOSType() == OSInfo.OSType.WINDOWS && OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_XP) < 0) {
                this.desktopProperty = new TriggerDesktopProperty(s) {
                    @Override
                    protected void updateUI() {
                        ActiveWindowsIcon.this.icon = null;
                        super.updateUI();
                    }
                };
            }
        }
        
        @Override
        public Object createValue(final UIDefaults uiDefaults) {
            if (this.icon == null) {
                final Image image = (Image)ShellFolder.get(this.nativeImageName);
                if (image != null) {
                    this.icon = new ImageIconUIResource(image);
                }
            }
            if (this.icon == null && this.fallbackName != null) {
                this.icon = (Icon)((UIDefaults.LazyValue)SwingUtilities2.makeIcon(WindowsLookAndFeel.class, BasicLookAndFeel.class, this.fallbackName)).createValue(uiDefaults);
            }
            return this.icon;
        }
    }
    
    private static class SkinIcon implements Icon, UIResource
    {
        private final TMSchema.Part part;
        private final TMSchema.State state;
        
        SkinIcon(final TMSchema.Part part, final TMSchema.State state) {
            this.part = part;
            this.state = state;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
            final XPStyle xp = XPStyle.getXP();
            assert xp != null;
            if (xp != null) {
                xp.getSkin(null, this.part).paintSkin(graphics, n, n2, this.state);
            }
        }
        
        @Override
        public int getIconWidth() {
            int width = 0;
            final XPStyle xp = XPStyle.getXP();
            assert xp != null;
            if (xp != null) {
                width = xp.getSkin(null, this.part).getWidth();
            }
            return width;
        }
        
        @Override
        public int getIconHeight() {
            int height = 0;
            final XPStyle xp = XPStyle.getXP();
            if (xp != null) {
                height = xp.getSkin(null, this.part).getHeight();
            }
            return height;
        }
    }
    
    private static class WindowsFontProperty extends DesktopProperty
    {
        WindowsFontProperty(final String s, final Object o) {
            super(s, o);
        }
        
        @Override
        public void invalidate(final LookAndFeel lookAndFeel) {
            if ("win.defaultGUI.font.height".equals(this.getKey())) {
                ((WindowsLookAndFeel)lookAndFeel).style = null;
            }
            super.invalidate(lookAndFeel);
        }
        
        @Override
        protected Object configureValue(final Object o) {
            if (o instanceof Font) {
                Object compositeFontUIResource = o;
                if ("MS Sans Serif".equals(((Font)compositeFontUIResource).getName())) {
                    int n = ((Font)compositeFontUIResource).getSize();
                    int screenResolution;
                    try {
                        screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
                    }
                    catch (final HeadlessException ex) {
                        screenResolution = 96;
                    }
                    if (Math.round(n * 72.0f / screenResolution) < 8) {
                        n = Math.round(8 * screenResolution / 72.0f);
                    }
                    final FontUIResource fontUIResource = new FontUIResource("Microsoft Sans Serif", ((Font)compositeFontUIResource).getStyle(), n);
                    if (fontUIResource.getName() != null && fontUIResource.getName().equals(fontUIResource.getFamily())) {
                        compositeFontUIResource = fontUIResource;
                    }
                    else if (n != ((Font)compositeFontUIResource).getSize()) {
                        compositeFontUIResource = new FontUIResource("MS Sans Serif", ((Font)compositeFontUIResource).getStyle(), n);
                    }
                }
                if (FontUtilities.fontSupportsDefaultEncoding((Font)compositeFontUIResource)) {
                    if (!(compositeFontUIResource instanceof UIResource)) {
                        compositeFontUIResource = new FontUIResource((Font)compositeFontUIResource);
                    }
                }
                else {
                    compositeFontUIResource = FontUtilities.getCompositeFontUIResource((Font)compositeFontUIResource);
                }
                return compositeFontUIResource;
            }
            return super.configureValue(o);
        }
    }
    
    private static class WindowsFontSizeProperty extends DesktopProperty
    {
        private String fontName;
        private int fontSize;
        private int fontStyle;
        
        WindowsFontSizeProperty(final String s, final String fontName, final int fontStyle, final int fontSize) {
            super(s, null);
            this.fontName = fontName;
            this.fontSize = fontSize;
            this.fontStyle = fontStyle;
        }
        
        @Override
        protected Object configureValue(Object o) {
            if (o == null) {
                o = new FontUIResource(this.fontName, this.fontStyle, this.fontSize);
            }
            else if (o instanceof Integer) {
                o = new FontUIResource(this.fontName, this.fontStyle, (int)o);
            }
            return o;
        }
    }
    
    private static class XPValue implements UIDefaults.ActiveValue
    {
        protected Object classicValue;
        protected Object xpValue;
        private static final Object NULL_VALUE;
        
        XPValue(final Object xpValue, final Object classicValue) {
            this.xpValue = xpValue;
            this.classicValue = classicValue;
        }
        
        @Override
        public Object createValue(final UIDefaults uiDefaults) {
            Object o = null;
            if (XPStyle.getXP() != null) {
                o = this.getXPValue(uiDefaults);
            }
            if (o == null) {
                o = this.getClassicValue(uiDefaults);
            }
            else if (o == XPValue.NULL_VALUE) {
                o = null;
            }
            return o;
        }
        
        protected Object getXPValue(final UIDefaults uiDefaults) {
            return this.recursiveCreateValue(this.xpValue, uiDefaults);
        }
        
        protected Object getClassicValue(final UIDefaults uiDefaults) {
            return this.recursiveCreateValue(this.classicValue, uiDefaults);
        }
        
        private Object recursiveCreateValue(Object value, final UIDefaults uiDefaults) {
            if (value instanceof UIDefaults.LazyValue) {
                value = ((UIDefaults.LazyValue)value).createValue(uiDefaults);
            }
            if (value instanceof UIDefaults.ActiveValue) {
                return ((UIDefaults.ActiveValue)value).createValue(uiDefaults);
            }
            return value;
        }
        
        static {
            NULL_VALUE = new Object();
        }
    }
    
    private static class XPBorderValue extends XPValue
    {
        private final Border extraMargin;
        
        XPBorderValue(final TMSchema.Part part, final Object o) {
            this(part, o, null);
        }
        
        XPBorderValue(final TMSchema.Part part, final Object o, final Border extraMargin) {
            super(part, o);
            this.extraMargin = extraMargin;
        }
        
        public Object getXPValue(final UIDefaults uiDefaults) {
            final XPStyle xp = XPStyle.getXP();
            final Border border = (xp != null) ? xp.getBorder(null, (TMSchema.Part)this.xpValue) : null;
            if (border != null && this.extraMargin != null) {
                return new BorderUIResource.CompoundBorderUIResource(border, this.extraMargin);
            }
            return border;
        }
    }
    
    private static class XPColorValue extends XPValue
    {
        XPColorValue(final TMSchema.Part part, final TMSchema.State state, final TMSchema.Prop prop, final Object o) {
            super(new XPColorValueKey(part, state, prop), o);
        }
        
        public Object getXPValue(final UIDefaults uiDefaults) {
            final XPColorValueKey xpColorValueKey = (XPColorValueKey)this.xpValue;
            final XPStyle xp = XPStyle.getXP();
            return (xp != null) ? xp.getColor(xpColorValueKey.skin, xpColorValueKey.prop, null) : null;
        }
        
        private static class XPColorValueKey
        {
            XPStyle.Skin skin;
            TMSchema.Prop prop;
            
            XPColorValueKey(final TMSchema.Part part, final TMSchema.State state, final TMSchema.Prop prop) {
                this.skin = new XPStyle.Skin(part, state);
                this.prop = prop;
            }
        }
    }
    
    private class XPDLUValue extends XPValue
    {
        private int direction;
        
        XPDLUValue(final int n, final int n2, final int direction) {
            super(n, n2);
            this.direction = direction;
        }
        
        public Object getXPValue(final UIDefaults uiDefaults) {
            return WindowsLookAndFeel.this.dluToPixels((int)this.xpValue, this.direction);
        }
        
        public Object getClassicValue(final UIDefaults uiDefaults) {
            return WindowsLookAndFeel.this.dluToPixels((int)this.classicValue, this.direction);
        }
    }
    
    private class TriggerDesktopProperty extends DesktopProperty
    {
        TriggerDesktopProperty(final String s) {
            super(s, null);
            this.getValueFromDesktop();
        }
        
        @Override
        protected void updateUI() {
            super.updateUI();
            this.getValueFromDesktop();
        }
    }
    
    private class FontDesktopProperty extends TriggerDesktopProperty
    {
        FontDesktopProperty(final String s) {
            super(s);
        }
        
        @Override
        protected void updateUI() {
            UIManager.getLookAndFeelDefaults().put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, SwingUtilities2.AATextInfo.getAATextInfo(true));
            super.updateUI();
        }
    }
    
    private class WindowsLayoutStyle extends DefaultLayoutStyle
    {
        @Override
        public int getPreferredGap(final JComponent component, final JComponent component2, final ComponentPlacement componentPlacement, final int n, final Container container) {
            super.getPreferredGap(component, component2, componentPlacement, n, container);
            switch (componentPlacement) {
                case INDENT: {
                    if (n != 3 && n != 7)
                    final int indent = this.getIndent(component, n);
                    if (indent > 0) {
                        return indent;
                    }
                    return 10;
                }
                case RELATED: {
                    if (this.isLabelAndNonlabel(component, component2, n)) {
                        return this.getButtonGap(component, component2, n, WindowsLookAndFeel.this.dluToPixels(3, n));
                    }
                    return this.getButtonGap(component, component2, n, WindowsLookAndFeel.this.dluToPixels(4, n));
                }
                case UNRELATED: {
                    return this.getButtonGap(component, component2, n, WindowsLookAndFeel.this.dluToPixels(7, n));
                }
                default: {
                    return 0;
                }
            }
        }
        
        @Override
        public int getContainerGap(final JComponent component, final int n, final Container container) {
            super.getContainerGap(component, n, container);
            return this.getButtonGap(component, n, WindowsLookAndFeel.this.dluToPixels(7, n));
        }
    }
    
    private static class RGBGrayFilter extends RGBImageFilter
    {
        public RGBGrayFilter() {
            this.canFilterIndexColorModel = true;
        }
        
        @Override
        public int filterRGB(final int n, final int n2, final int n3) {
            final float n4 = ((n3 >> 16 & 0xFF) / 255.0f + (n3 >> 8 & 0xFF) / 255.0f + (n3 & 0xFF) / 255.0f) / 3.0f;
            final float n5 = (n3 >> 24 & 0xFF) / 255.0f;
            final float min = Math.min(1.0f, (1.0f - n4) / 2.857143f + n4);
            return (int)(n5 * 255.0f) << 24 | (int)(min * 255.0f) << 16 | (int)(min * 255.0f) << 8 | (int)(min * 255.0f);
        }
    }
    
    private static class FocusColorProperty extends DesktopProperty
    {
        public FocusColorProperty() {
            super("win.3d.backgroundColor", Color.BLACK);
        }
        
        @Override
        protected Object configureValue(final Object o) {
            final Object desktopProperty = Toolkit.getDefaultToolkit().getDesktopProperty("win.highContrast.on");
            if (desktopProperty == null || !(boolean)desktopProperty) {
                return Color.BLACK;
            }
            return Color.BLACK.equals(o) ? Color.WHITE : Color.BLACK;
        }
    }
}
