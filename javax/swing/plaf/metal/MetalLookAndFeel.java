package javax.swing.plaf.metal;

import javax.swing.plaf.UIResource;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.JToggleButton;
import java.awt.Container;
import sun.swing.DefaultLayoutStyle;
import java.awt.Frame;
import javax.swing.SwingUtilities;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import javax.swing.LayoutStyle;
import javax.swing.plaf.FontUIResource;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.JComponent;
import sun.awt.AppContext;
import java.awt.Component;
import sun.swing.SwingUtilities2;
import java.awt.Insets;
import sun.swing.SwingLazyValue;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.UIDefaults;
import java.awt.Toolkit;
import javax.swing.UIManager;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;
import java.security.AccessController;
import sun.awt.OSInfo;
import javax.swing.LookAndFeel;
import java.lang.ref.ReferenceQueue;
import javax.swing.plaf.basic.BasicLookAndFeel;

public class MetalLookAndFeel extends BasicLookAndFeel
{
    private static boolean METAL_LOOK_AND_FEEL_INITED;
    private static boolean checkedWindows;
    private static boolean isWindows;
    private static boolean checkedSystemFontSettings;
    private static boolean useSystemFonts;
    static ReferenceQueue<LookAndFeel> queue;
    
    static boolean isWindows() {
        if (!MetalLookAndFeel.checkedWindows) {
            if (AccessController.doPrivileged(OSInfo.getOSTypeAction()) == OSInfo.OSType.WINDOWS) {
                MetalLookAndFeel.isWindows = true;
                final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("swing.useSystemFontSettings"));
                MetalLookAndFeel.useSystemFonts = (s != null && Boolean.valueOf(s));
            }
            MetalLookAndFeel.checkedWindows = true;
        }
        return MetalLookAndFeel.isWindows;
    }
    
    static boolean useSystemFonts() {
        if (!isWindows() || !MetalLookAndFeel.useSystemFonts) {
            return false;
        }
        if (MetalLookAndFeel.METAL_LOOK_AND_FEEL_INITED) {
            final Object value = UIManager.get("Application.useSystemFontSettings");
            return value == null || Boolean.TRUE.equals(value);
        }
        return true;
    }
    
    private static boolean useHighContrastTheme() {
        if (isWindows() && useSystemFonts()) {
            final Boolean b = (Boolean)Toolkit.getDefaultToolkit().getDesktopProperty("win.highContrast.on");
            return b != null && b;
        }
        return false;
    }
    
    static boolean usingOcean() {
        return getCurrentTheme() instanceof OceanTheme;
    }
    
    @Override
    public String getName() {
        return "Metal";
    }
    
    @Override
    public String getID() {
        return "Metal";
    }
    
    @Override
    public String getDescription() {
        return "The Java(tm) Look and Feel";
    }
    
    @Override
    public boolean isNativeLookAndFeel() {
        return false;
    }
    
    @Override
    public boolean isSupportedLookAndFeel() {
        return true;
    }
    
    @Override
    public boolean getSupportsWindowDecorations() {
        return true;
    }
    
    @Override
    protected void initClassDefaults(final UIDefaults uiDefaults) {
        super.initClassDefaults(uiDefaults);
        uiDefaults.putDefaults(new Object[] { "ButtonUI", "javax.swing.plaf.metal.MetalButtonUI", "CheckBoxUI", "javax.swing.plaf.metal.MetalCheckBoxUI", "ComboBoxUI", "javax.swing.plaf.metal.MetalComboBoxUI", "DesktopIconUI", "javax.swing.plaf.metal.MetalDesktopIconUI", "FileChooserUI", "javax.swing.plaf.metal.MetalFileChooserUI", "InternalFrameUI", "javax.swing.plaf.metal.MetalInternalFrameUI", "LabelUI", "javax.swing.plaf.metal.MetalLabelUI", "PopupMenuSeparatorUI", "javax.swing.plaf.metal.MetalPopupMenuSeparatorUI", "ProgressBarUI", "javax.swing.plaf.metal.MetalProgressBarUI", "RadioButtonUI", "javax.swing.plaf.metal.MetalRadioButtonUI", "ScrollBarUI", "javax.swing.plaf.metal.MetalScrollBarUI", "ScrollPaneUI", "javax.swing.plaf.metal.MetalScrollPaneUI", "SeparatorUI", "javax.swing.plaf.metal.MetalSeparatorUI", "SliderUI", "javax.swing.plaf.metal.MetalSliderUI", "SplitPaneUI", "javax.swing.plaf.metal.MetalSplitPaneUI", "TabbedPaneUI", "javax.swing.plaf.metal.MetalTabbedPaneUI", "TextFieldUI", "javax.swing.plaf.metal.MetalTextFieldUI", "ToggleButtonUI", "javax.swing.plaf.metal.MetalToggleButtonUI", "ToolBarUI", "javax.swing.plaf.metal.MetalToolBarUI", "ToolTipUI", "javax.swing.plaf.metal.MetalToolTipUI", "TreeUI", "javax.swing.plaf.metal.MetalTreeUI", "RootPaneUI", "javax.swing.plaf.metal.MetalRootPaneUI" });
    }
    
    @Override
    protected void initSystemColorDefaults(final UIDefaults uiDefaults) {
        final MetalTheme currentTheme = getCurrentTheme();
        final ColorUIResource control = currentTheme.getControl();
        uiDefaults.putDefaults(new Object[] { "desktop", currentTheme.getDesktopColor(), "activeCaption", currentTheme.getWindowTitleBackground(), "activeCaptionText", currentTheme.getWindowTitleForeground(), "activeCaptionBorder", currentTheme.getPrimaryControlShadow(), "inactiveCaption", currentTheme.getWindowTitleInactiveBackground(), "inactiveCaptionText", currentTheme.getWindowTitleInactiveForeground(), "inactiveCaptionBorder", currentTheme.getControlShadow(), "window", currentTheme.getWindowBackground(), "windowBorder", control, "windowText", currentTheme.getUserTextColor(), "menu", currentTheme.getMenuBackground(), "menuText", currentTheme.getMenuForeground(), "text", currentTheme.getWindowBackground(), "textText", currentTheme.getUserTextColor(), "textHighlight", currentTheme.getTextHighlightColor(), "textHighlightText", currentTheme.getHighlightedTextColor(), "textInactiveText", currentTheme.getInactiveSystemTextColor(), "control", control, "controlText", currentTheme.getControlTextColor(), "controlHighlight", currentTheme.getControlHighlight(), "controlLtHighlight", currentTheme.getControlHighlight(), "controlShadow", currentTheme.getControlShadow(), "controlDkShadow", currentTheme.getControlDarkShadow(), "scrollbar", control, "info", currentTheme.getPrimaryControl(), "infoText", currentTheme.getPrimaryControlInfo() });
    }
    
    private void initResourceBundle(final UIDefaults uiDefaults) {
        uiDefaults.addResourceBundle("com.sun.swing.internal.plaf.metal.resources.metal");
    }
    
    @Override
    protected void initComponentDefaults(final UIDefaults uiDefaults) {
        super.initComponentDefaults(uiDefaults);
        this.initResourceBundle(uiDefaults);
        final ColorUIResource acceleratorForeground = getAcceleratorForeground();
        final ColorUIResource acceleratorSelectedForeground = getAcceleratorSelectedForeground();
        final ColorUIResource control = getControl();
        final ColorUIResource controlHighlight = getControlHighlight();
        final ColorUIResource controlShadow = getControlShadow();
        final ColorUIResource controlDarkShadow = getControlDarkShadow();
        final ColorUIResource controlTextColor = getControlTextColor();
        final ColorUIResource focusColor = getFocusColor();
        final ColorUIResource inactiveControlTextColor = getInactiveControlTextColor();
        final ColorUIResource menuBackground = getMenuBackground();
        final ColorUIResource menuSelectedBackground = getMenuSelectedBackground();
        final ColorUIResource menuDisabledForeground = getMenuDisabledForeground();
        final ColorUIResource menuSelectedForeground = getMenuSelectedForeground();
        final ColorUIResource primaryControl = getPrimaryControl();
        final ColorUIResource primaryControlDarkShadow = getPrimaryControlDarkShadow();
        final ColorUIResource primaryControlShadow = getPrimaryControlShadow();
        final ColorUIResource systemTextColor = getSystemTextColor();
        final InsetsUIResource insetsUIResource = new InsetsUIResource(0, 0, 0, 0);
        final Integer value = 0;
        final SwingLazyValue swingLazyValue = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders", "getTextFieldBorder");
        final UIDefaults.LazyValue lazyValue = p0 -> new MetalBorders.DialogBorder();
        final UIDefaults.LazyValue lazyValue2 = p0 -> new MetalBorders.QuestionDialogBorder();
        final UIDefaults.LazyInputMap lazyInputMap = new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation" });
        final UIDefaults.LazyInputMap lazyInputMap2 = new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-begin-line", "ctrl KP_LEFT", "caret-begin-line", "ctrl RIGHT", "caret-end-line", "ctrl KP_RIGHT", "caret-end-line", "ctrl shift LEFT", "selection-begin-line", "ctrl shift KP_LEFT", "selection-begin-line", "ctrl shift RIGHT", "selection-end-line", "ctrl shift KP_RIGHT", "selection-end-line", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation" });
        final UIDefaults.LazyInputMap lazyInputMap3 = new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "UP", "caret-up", "KP_UP", "caret-up", "DOWN", "caret-down", "KP_DOWN", "caret-down", "PAGE_UP", "page-up", "PAGE_DOWN", "page-down", "shift PAGE_UP", "selection-page-up", "shift PAGE_DOWN", "selection-page-down", "ctrl shift PAGE_UP", "selection-page-left", "ctrl shift PAGE_DOWN", "selection-page-right", "shift UP", "selection-up", "shift KP_UP", "selection-up", "shift DOWN", "selection-down", "shift KP_DOWN", "selection-down", "ENTER", "insert-break", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "TAB", "insert-tab", "ctrl BACK_SLASH", "unselect", "ctrl HOME", "caret-begin", "ctrl END", "caret-end", "ctrl shift HOME", "selection-begin", "ctrl shift END", "selection-end", "ctrl T", "next-link-action", "ctrl shift T", "previous-link-action", "ctrl SPACE", "activate-link-action", "control shift O", "toggle-componentOrientation" });
        final SwingLazyValue swingLazyValue2 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$ScrollPaneBorder");
        final SwingLazyValue swingLazyValue3 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders", "getButtonBorder");
        final SwingLazyValue swingLazyValue4 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders", "getToggleButtonBorder");
        final SwingLazyValue swingLazyValue5 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[] { controlShadow });
        final SwingLazyValue swingLazyValue6 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders", "getDesktopIconBorder");
        final SwingLazyValue swingLazyValue7 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$MenuBarBorder");
        final SwingLazyValue swingLazyValue8 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$PopupMenuBorder");
        final SwingLazyValue swingLazyValue9 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$MenuItemBorder");
        final String s = "-";
        final SwingLazyValue swingLazyValue10 = new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$ToolBarBorder");
        final SwingLazyValue swingLazyValue11 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[] { controlDarkShadow, new Integer(1) });
        final SwingLazyValue swingLazyValue12 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[] { primaryControlDarkShadow });
        final SwingLazyValue swingLazyValue13 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[] { controlDarkShadow });
        final SwingLazyValue swingLazyValue14 = new SwingLazyValue("javax.swing.plaf.BorderUIResource$LineBorderUIResource", new Object[] { focusColor });
        final InsetsUIResource insetsUIResource2 = new InsetsUIResource(4, 2, 0, 6);
        final InsetsUIResource insetsUIResource3 = new InsetsUIResource(0, 9, 1, 9);
        final Object[] array = { new Integer(16) };
        final Object[] array2 = { "OptionPane.errorSound", "OptionPane.informationSound", "OptionPane.questionSound", "OptionPane.warningSound" };
        final MetalTheme currentTheme = getCurrentTheme();
        final FontActiveValue fontActiveValue = new FontActiveValue(currentTheme, 3);
        final FontActiveValue fontActiveValue2 = new FontActiveValue(currentTheme, 0);
        final FontActiveValue fontActiveValue3 = new FontActiveValue(currentTheme, 2);
        final FontActiveValue fontActiveValue4 = new FontActiveValue(currentTheme, 4);
        final FontActiveValue fontActiveValue5 = new FontActiveValue(currentTheme, 5);
        uiDefaults.putDefaults(new Object[] { "AuditoryCues.defaultCueList", array2, "AuditoryCues.playList", null, "TextField.border", swingLazyValue, "TextField.font", fontActiveValue3, "PasswordField.border", swingLazyValue, "PasswordField.font", fontActiveValue3, "PasswordField.echoChar", '\u2022', "TextArea.font", fontActiveValue3, "TextPane.background", uiDefaults.get("window"), "TextPane.font", fontActiveValue3, "EditorPane.background", uiDefaults.get("window"), "EditorPane.font", fontActiveValue3, "TextField.focusInputMap", lazyInputMap, "PasswordField.focusInputMap", lazyInputMap2, "TextArea.focusInputMap", lazyInputMap3, "TextPane.focusInputMap", lazyInputMap3, "EditorPane.focusInputMap", lazyInputMap3, "FormattedTextField.border", swingLazyValue, "FormattedTextField.font", fontActiveValue3, "FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "Button.defaultButtonFollowsFocus", Boolean.FALSE, "Button.disabledText", inactiveControlTextColor, "Button.select", controlShadow, "Button.border", swingLazyValue3, "Button.font", fontActiveValue2, "Button.focus", focusColor, "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "CheckBox.disabledText", inactiveControlTextColor, "Checkbox.select", controlShadow, "CheckBox.font", fontActiveValue2, "CheckBox.focus", focusColor, "CheckBox.icon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getCheckBoxIcon"), "CheckBox.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "CheckBox.totalInsets", new Insets(4, 4, 4, 4), "RadioButton.disabledText", inactiveControlTextColor, "RadioButton.select", controlShadow, "RadioButton.icon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getRadioButtonIcon"), "RadioButton.font", fontActiveValue2, "RadioButton.focus", focusColor, "RadioButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "RadioButton.totalInsets", new Insets(4, 4, 4, 4), "ToggleButton.select", controlShadow, "ToggleButton.disabledText", inactiveControlTextColor, "ToggleButton.focus", focusColor, "ToggleButton.border", swingLazyValue4, "ToggleButton.font", fontActiveValue2, "ToggleButton.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }), "FileView.directoryIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"), "FileView.fileIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeLeafIcon"), "FileView.computerIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeComputerIcon"), "FileView.hardDriveIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeHardDriveIcon"), "FileView.floppyDriveIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFloppyDriveIcon"), "FileChooser.detailsViewIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserDetailViewIcon"), "FileChooser.homeFolderIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserHomeFolderIcon"), "FileChooser.listViewIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserListViewIcon"), "FileChooser.newFolderIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserNewFolderIcon"), "FileChooser.upFolderIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getFileChooserUpFolderIcon"), "FileChooser.usesSingleFilePane", Boolean.TRUE, "FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancelSelection", "F2", "editFileName", "F5", "refresh", "BACK_SPACE", "Go Up" }), "ToolTip.font", new FontActiveValue(currentTheme, 1), "ToolTip.border", swingLazyValue12, "ToolTip.borderInactive", swingLazyValue13, "ToolTip.backgroundInactive", control, "ToolTip.foregroundInactive", controlDarkShadow, "ToolTip.hideAccelerator", Boolean.FALSE, "ToolTipManager.enableToolTipMode", "activeApplication", "Slider.font", fontActiveValue2, "Slider.border", null, "Slider.foreground", primaryControlShadow, "Slider.focus", focusColor, "Slider.focusInsets", insetsUIResource, "Slider.trackWidth", new Integer(7), "Slider.majorTickLength", new Integer(6), "Slider.horizontalThumbIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getHorizontalSliderThumbIcon"), "Slider.verticalThumbIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getVerticalSliderThumbIcon"), "Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", "ctrl PAGE_DOWN", "negativeBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "PAGE_UP", "positiveBlockIncrement", "ctrl PAGE_UP", "positiveBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }), "ProgressBar.font", fontActiveValue2, "ProgressBar.foreground", primaryControlShadow, "ProgressBar.selectionBackground", primaryControlDarkShadow, "ProgressBar.border", swingLazyValue11, "ProgressBar.cellSpacing", value, "ProgressBar.cellLength", 1, "ComboBox.background", control, "ComboBox.foreground", controlTextColor, "ComboBox.selectionBackground", primaryControlShadow, "ComboBox.selectionForeground", controlTextColor, "ComboBox.font", fontActiveValue2, "ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", "DOWN", "selectNext", "KP_DOWN", "selectNext", "alt DOWN", "togglePopup", "alt KP_DOWN", "togglePopup", "alt UP", "togglePopup", "alt KP_UP", "togglePopup", "SPACE", "spacePopup", "ENTER", "enterPressed", "UP", "selectPrevious", "KP_UP", "selectPrevious" }), "InternalFrame.icon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameDefaultMenuIcon"), "InternalFrame.border", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$InternalFrameBorder"), "InternalFrame.optionDialogBorder", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$OptionDialogBorder"), "InternalFrame.paletteBorder", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$PaletteBorder"), "InternalFrame.paletteTitleHeight", new Integer(11), "InternalFrame.paletteCloseIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory$PaletteCloseIcon"), "InternalFrame.closeIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameCloseIcon", array), "InternalFrame.maximizeIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameMaximizeIcon", array), "InternalFrame.iconifyIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameMinimizeIcon", array), "InternalFrame.minimizeIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getInternalFrameAltMaximizeIcon", array), "InternalFrame.titleFont", fontActiveValue4, "InternalFrame.windowBindings", null, "InternalFrame.closeSound", "sounds/FrameClose.wav", "InternalFrame.maximizeSound", "sounds/FrameMaximize.wav", "InternalFrame.minimizeSound", "sounds/FrameMinimize.wav", "InternalFrame.restoreDownSound", "sounds/FrameRestoreDown.wav", "InternalFrame.restoreUpSound", "sounds/FrameRestoreUp.wav", "DesktopIcon.border", swingLazyValue6, "DesktopIcon.font", fontActiveValue2, "DesktopIcon.foreground", controlTextColor, "DesktopIcon.background", control, "DesktopIcon.width", 160, "Desktop.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl F5", "restore", "ctrl F4", "close", "ctrl F7", "move", "ctrl F8", "resize", "RIGHT", "right", "KP_RIGHT", "right", "shift RIGHT", "shrinkRight", "shift KP_RIGHT", "shrinkRight", "LEFT", "left", "KP_LEFT", "left", "shift LEFT", "shrinkLeft", "shift KP_LEFT", "shrinkLeft", "UP", "up", "KP_UP", "up", "shift UP", "shrinkUp", "shift KP_UP", "shrinkUp", "DOWN", "down", "KP_DOWN", "down", "shift DOWN", "shrinkDown", "shift KP_DOWN", "shrinkDown", "ESCAPE", "escape", "ctrl F9", "minimize", "ctrl F10", "maximize", "ctrl F6", "selectNextFrame", "ctrl TAB", "selectNextFrame", "ctrl alt F6", "selectNextFrame", "shift ctrl alt F6", "selectPreviousFrame", "ctrl F12", "navigateNext", "shift ctrl F12", "navigatePrevious" }), "TitledBorder.font", fontActiveValue2, "TitledBorder.titleColor", systemTextColor, "TitledBorder.border", swingLazyValue5, "Label.font", fontActiveValue2, "Label.foreground", systemTextColor, "Label.disabledForeground", getInactiveSystemTextColor(), "List.font", fontActiveValue2, "List.focusCellHighlightBorder", swingLazyValue14, "List.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "HOME", "selectFirstRow", "shift HOME", "selectFirstRowExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRowChangeLead", "END", "selectLastRow", "shift END", "selectLastRowExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRowChangeLead", "PAGE_UP", "scrollUp", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDown", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), "ScrollBar.background", control, "ScrollBar.highlight", controlHighlight, "ScrollBar.shadow", controlShadow, "ScrollBar.darkShadow", controlDarkShadow, "ScrollBar.thumb", primaryControlShadow, "ScrollBar.thumbShadow", primaryControlDarkShadow, "ScrollBar.thumbHighlight", primaryControl, "ScrollBar.width", new Integer(17), "ScrollBar.allowsAbsolutePositioning", Boolean.TRUE, "ScrollBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }), "ScrollPane.border", swingLazyValue2, "ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd" }), "TabbedPane.font", fontActiveValue2, "TabbedPane.tabAreaBackground", control, "TabbedPane.background", controlShadow, "TabbedPane.light", control, "TabbedPane.focus", primaryControlDarkShadow, "TabbedPane.selected", control, "TabbedPane.selectHighlight", controlHighlight, "TabbedPane.tabAreaInsets", insetsUIResource2, "TabbedPane.tabInsets", insetsUIResource3, "TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent" }), "TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus" }), "Table.font", fontActiveValue3, "Table.focusCellHighlightBorder", swingLazyValue14, "Table.scrollPaneBorder", swingLazyValue2, "Table.dropLineColor", focusColor, "Table.dropLineShortColor", primaryControlDarkShadow, "Table.gridColor", controlShadow, "Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "HOME", "selectFirstColumn", "shift HOME", "selectFirstColumnExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRow", "END", "selectLastColumn", "shift END", "selectLastColumnExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRow", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollLeftExtendSelection", "ctrl PAGE_UP", "scrollLeftChangeSelection", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollRightExtendSelection", "ctrl PAGE_DOWN", "scrollRightChangeSelection", "TAB", "selectNextColumnCell", "shift TAB", "selectPreviousColumnCell", "ENTER", "selectNextRowCell", "shift ENTER", "selectPreviousRowCell", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ESCAPE", "cancel", "F2", "startEditing", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo", "F8", "focusHeader" }), "Table.ascendingSortIcon", SwingUtilities2.makeIcon(this.getClass(), MetalLookAndFeel.class, "icons/sortUp.png"), "Table.descendingSortIcon", SwingUtilities2.makeIcon(this.getClass(), MetalLookAndFeel.class, "icons/sortDown.png"), "TableHeader.font", fontActiveValue3, "TableHeader.cellBorder", new SwingLazyValue("javax.swing.plaf.metal.MetalBorders$TableHeaderBorder"), "MenuBar.border", swingLazyValue7, "MenuBar.font", fontActiveValue, "MenuBar.windowBindings", { "F10", "takeFocus" }, "Menu.border", swingLazyValue9, "Menu.borderPainted", Boolean.TRUE, "Menu.menuPopupOffsetX", value, "Menu.menuPopupOffsetY", value, "Menu.submenuPopupOffsetX", new Integer(-4), "Menu.submenuPopupOffsetY", new Integer(-3), "Menu.font", fontActiveValue, "Menu.selectionForeground", menuSelectedForeground, "Menu.selectionBackground", menuSelectedBackground, "Menu.disabledForeground", menuDisabledForeground, "Menu.acceleratorFont", fontActiveValue5, "Menu.acceleratorForeground", acceleratorForeground, "Menu.acceleratorSelectionForeground", acceleratorSelectedForeground, "Menu.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemCheckIcon"), "Menu.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuArrowIcon"), "MenuItem.border", swingLazyValue9, "MenuItem.borderPainted", Boolean.TRUE, "MenuItem.font", fontActiveValue, "MenuItem.selectionForeground", menuSelectedForeground, "MenuItem.selectionBackground", menuSelectedBackground, "MenuItem.disabledForeground", menuDisabledForeground, "MenuItem.acceleratorFont", fontActiveValue5, "MenuItem.acceleratorForeground", acceleratorForeground, "MenuItem.acceleratorSelectionForeground", acceleratorSelectedForeground, "MenuItem.acceleratorDelimiter", s, "MenuItem.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemCheckIcon"), "MenuItem.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"), "MenuItem.commandSound", "sounds/MenuItemCommand.wav", "OptionPane.windowBindings", { "ESCAPE", "close" }, "OptionPane.informationSound", "sounds/OptionPaneInformation.wav", "OptionPane.warningSound", "sounds/OptionPaneWarning.wav", "OptionPane.errorSound", "sounds/OptionPaneError.wav", "OptionPane.questionSound", "sounds/OptionPaneQuestion.wav", "OptionPane.errorDialog.border.background", new ColorUIResource(153, 51, 51), "OptionPane.errorDialog.titlePane.foreground", new ColorUIResource(51, 0, 0), "OptionPane.errorDialog.titlePane.background", new ColorUIResource(255, 153, 153), "OptionPane.errorDialog.titlePane.shadow", new ColorUIResource(204, 102, 102), "OptionPane.questionDialog.border.background", new ColorUIResource(51, 102, 51), "OptionPane.questionDialog.titlePane.foreground", new ColorUIResource(0, 51, 0), "OptionPane.questionDialog.titlePane.background", new ColorUIResource(153, 204, 153), "OptionPane.questionDialog.titlePane.shadow", new ColorUIResource(102, 153, 102), "OptionPane.warningDialog.border.background", new ColorUIResource(153, 102, 51), "OptionPane.warningDialog.titlePane.foreground", new ColorUIResource(102, 51, 0), "OptionPane.warningDialog.titlePane.background", new ColorUIResource(255, 204, 153), "OptionPane.warningDialog.titlePane.shadow", new ColorUIResource(204, 153, 102), "Separator.background", getSeparatorBackground(), "Separator.foreground", getSeparatorForeground(), "PopupMenu.border", swingLazyValue8, "PopupMenu.popupSound", "sounds/PopupMenuPopup.wav", "PopupMenu.font", fontActiveValue, "CheckBoxMenuItem.border", swingLazyValue9, "CheckBoxMenuItem.borderPainted", Boolean.TRUE, "CheckBoxMenuItem.font", fontActiveValue, "CheckBoxMenuItem.selectionForeground", menuSelectedForeground, "CheckBoxMenuItem.selectionBackground", menuSelectedBackground, "CheckBoxMenuItem.disabledForeground", menuDisabledForeground, "CheckBoxMenuItem.acceleratorFont", fontActiveValue5, "CheckBoxMenuItem.acceleratorForeground", acceleratorForeground, "CheckBoxMenuItem.acceleratorSelectionForeground", acceleratorSelectedForeground, "CheckBoxMenuItem.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getCheckBoxMenuItemIcon"), "CheckBoxMenuItem.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"), "CheckBoxMenuItem.commandSound", "sounds/MenuItemCommand.wav", "RadioButtonMenuItem.border", swingLazyValue9, "RadioButtonMenuItem.borderPainted", Boolean.TRUE, "RadioButtonMenuItem.font", fontActiveValue, "RadioButtonMenuItem.selectionForeground", menuSelectedForeground, "RadioButtonMenuItem.selectionBackground", menuSelectedBackground, "RadioButtonMenuItem.disabledForeground", menuDisabledForeground, "RadioButtonMenuItem.acceleratorFont", fontActiveValue5, "RadioButtonMenuItem.acceleratorForeground", acceleratorForeground, "RadioButtonMenuItem.acceleratorSelectionForeground", acceleratorSelectedForeground, "RadioButtonMenuItem.checkIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getRadioButtonMenuItemIcon"), "RadioButtonMenuItem.arrowIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getMenuItemArrowIcon"), "RadioButtonMenuItem.commandSound", "sounds/MenuItemCommand.wav", "Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }), "Spinner.arrowButtonInsets", insetsUIResource, "Spinner.border", swingLazyValue, "Spinner.arrowButtonBorder", swingLazyValue3, "Spinner.font", fontActiveValue2, "SplitPane.dividerSize", new Integer(10), "SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward" }), "SplitPane.centerOneTouchButtons", Boolean.FALSE, "SplitPane.dividerFocusColor", primaryControl, "Tree.font", fontActiveValue3, "Tree.textBackground", getWindowBackground(), "Tree.selectionBorderColor", focusColor, "Tree.openIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"), "Tree.closedIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeFolderIcon"), "Tree.leafIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeLeafIcon"), "Tree.expandedIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeControlIcon", new Object[] { false }), "Tree.collapsedIcon", new SwingLazyValue("javax.swing.plaf.metal.MetalIconFactory", "getTreeControlIcon", new Object[] { true }), "Tree.line", primaryControl, "Tree.hash", primaryControl, "Tree.rowHeight", value, "Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ADD", "expand", "SUBTRACT", "collapse", "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPrevious", "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", "ctrl shift UP", "selectPreviousExtendSelection", "ctrl shift KP_UP", "selectPreviousExtendSelection", "ctrl UP", "selectPreviousChangeLead", "ctrl KP_UP", "selectPreviousChangeLead", "DOWN", "selectNext", "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", "shift KP_DOWN", "selectNextExtendSelection", "ctrl shift DOWN", "selectNextExtendSelection", "ctrl shift KP_DOWN", "selectNextExtendSelection", "ctrl DOWN", "selectNextChangeLead", "ctrl KP_DOWN", "selectNextChangeLead", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "LEFT", "selectParent", "KP_LEFT", "selectParent", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "HOME", "selectFirst", "shift HOME", "selectFirstExtendSelection", "ctrl shift HOME", "selectFirstExtendSelection", "ctrl HOME", "selectFirstChangeLead", "END", "selectLast", "shift END", "selectLastExtendSelection", "ctrl shift END", "selectLastExtendSelection", "ctrl END", "selectLastChangeLead", "F2", "startEditing", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ctrl LEFT", "scrollLeft", "ctrl KP_LEFT", "scrollLeft", "ctrl RIGHT", "scrollRight", "ctrl KP_RIGHT", "scrollRight", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }), "Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancel" }), "ToolBar.border", swingLazyValue10, "ToolBar.background", menuBackground, "ToolBar.foreground", getMenuForeground(), "ToolBar.font", fontActiveValue, "ToolBar.dockingBackground", menuBackground, "ToolBar.floatingBackground", menuBackground, "ToolBar.dockingForeground", primaryControlDarkShadow, "ToolBar.floatingForeground", primaryControl, "ToolBar.rolloverBorder", p0 -> MetalBorders.getToolBarRolloverBorder(), "ToolBar.nonrolloverBorder", p0 -> MetalBorders.getToolBarNonrolloverBorder(), "ToolBar.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight" }), "RootPane.frameBorder", p0 -> new MetalBorders.FrameBorder(), "RootPane.plainDialogBorder", lazyValue, "RootPane.informationDialogBorder", lazyValue, "RootPane.errorDialogBorder", p0 -> new MetalBorders.ErrorDialogBorder(), "RootPane.colorChooserDialogBorder", lazyValue2, "RootPane.fileChooserDialogBorder", lazyValue2, "RootPane.questionDialogBorder", lazyValue2, "RootPane.warningDialogBorder", p0 -> new MetalBorders.WarningDialogBorder(), "RootPane.defaultButtonWindowKeyBindings", { "ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release" } });
        if (isWindows() && useSystemFonts() && currentTheme.isSystemTheme()) {
            final MetalFontDesktopProperty metalFontDesktopProperty = new MetalFontDesktopProperty("win.messagebox.font.height", 0);
            uiDefaults.putDefaults(new Object[] { "OptionPane.messageFont", metalFontDesktopProperty, "OptionPane.buttonFont", metalFontDesktopProperty });
        }
        flushUnreferenced();
        uiDefaults.put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, SwingUtilities2.AATextInfo.getAATextInfo(SwingUtilities2.isLocalDisplay()));
        new AATextListener(this);
    }
    
    protected void createDefaultTheme() {
        getCurrentTheme();
    }
    
    @Override
    public UIDefaults getDefaults() {
        MetalLookAndFeel.METAL_LOOK_AND_FEEL_INITED = true;
        this.createDefaultTheme();
        final UIDefaults defaults = super.getDefaults();
        final MetalTheme currentTheme = getCurrentTheme();
        currentTheme.addCustomEntriesToTable(defaults);
        currentTheme.install();
        return defaults;
    }
    
    @Override
    public void provideErrorFeedback(final Component component) {
        super.provideErrorFeedback(component);
    }
    
    public static void setCurrentTheme(final MetalTheme metalTheme) {
        if (metalTheme == null) {
            throw new NullPointerException("Can't have null theme");
        }
        AppContext.getAppContext().put("currentMetalTheme", metalTheme);
    }
    
    public static MetalTheme getCurrentTheme() {
        MetalTheme currentTheme = (MetalTheme)AppContext.getAppContext().get("currentMetalTheme");
        if (currentTheme == null) {
            if (useHighContrastTheme()) {
                currentTheme = new MetalHighContrastTheme();
            }
            else if ("steel".equals(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("swing.metalTheme")))) {
                currentTheme = new DefaultMetalTheme();
            }
            else {
                currentTheme = new OceanTheme();
            }
            setCurrentTheme(currentTheme);
        }
        return currentTheme;
    }
    
    @Override
    public Icon getDisabledIcon(final JComponent component, final Icon icon) {
        if (icon instanceof ImageIcon && usingOcean()) {
            return MetalUtils.getOceanDisabledButtonIcon(((ImageIcon)icon).getImage());
        }
        return super.getDisabledIcon(component, icon);
    }
    
    @Override
    public Icon getDisabledSelectedIcon(final JComponent component, final Icon icon) {
        if (icon instanceof ImageIcon && usingOcean()) {
            return MetalUtils.getOceanDisabledButtonIcon(((ImageIcon)icon).getImage());
        }
        return super.getDisabledSelectedIcon(component, icon);
    }
    
    public static FontUIResource getControlTextFont() {
        return getCurrentTheme().getControlTextFont();
    }
    
    public static FontUIResource getSystemTextFont() {
        return getCurrentTheme().getSystemTextFont();
    }
    
    public static FontUIResource getUserTextFont() {
        return getCurrentTheme().getUserTextFont();
    }
    
    public static FontUIResource getMenuTextFont() {
        return getCurrentTheme().getMenuTextFont();
    }
    
    public static FontUIResource getWindowTitleFont() {
        return getCurrentTheme().getWindowTitleFont();
    }
    
    public static FontUIResource getSubTextFont() {
        return getCurrentTheme().getSubTextFont();
    }
    
    public static ColorUIResource getDesktopColor() {
        return getCurrentTheme().getDesktopColor();
    }
    
    public static ColorUIResource getFocusColor() {
        return getCurrentTheme().getFocusColor();
    }
    
    public static ColorUIResource getWhite() {
        return getCurrentTheme().getWhite();
    }
    
    public static ColorUIResource getBlack() {
        return getCurrentTheme().getBlack();
    }
    
    public static ColorUIResource getControl() {
        return getCurrentTheme().getControl();
    }
    
    public static ColorUIResource getControlShadow() {
        return getCurrentTheme().getControlShadow();
    }
    
    public static ColorUIResource getControlDarkShadow() {
        return getCurrentTheme().getControlDarkShadow();
    }
    
    public static ColorUIResource getControlInfo() {
        return getCurrentTheme().getControlInfo();
    }
    
    public static ColorUIResource getControlHighlight() {
        return getCurrentTheme().getControlHighlight();
    }
    
    public static ColorUIResource getControlDisabled() {
        return getCurrentTheme().getControlDisabled();
    }
    
    public static ColorUIResource getPrimaryControl() {
        return getCurrentTheme().getPrimaryControl();
    }
    
    public static ColorUIResource getPrimaryControlShadow() {
        return getCurrentTheme().getPrimaryControlShadow();
    }
    
    public static ColorUIResource getPrimaryControlDarkShadow() {
        return getCurrentTheme().getPrimaryControlDarkShadow();
    }
    
    public static ColorUIResource getPrimaryControlInfo() {
        return getCurrentTheme().getPrimaryControlInfo();
    }
    
    public static ColorUIResource getPrimaryControlHighlight() {
        return getCurrentTheme().getPrimaryControlHighlight();
    }
    
    public static ColorUIResource getSystemTextColor() {
        return getCurrentTheme().getSystemTextColor();
    }
    
    public static ColorUIResource getControlTextColor() {
        return getCurrentTheme().getControlTextColor();
    }
    
    public static ColorUIResource getInactiveControlTextColor() {
        return getCurrentTheme().getInactiveControlTextColor();
    }
    
    public static ColorUIResource getInactiveSystemTextColor() {
        return getCurrentTheme().getInactiveSystemTextColor();
    }
    
    public static ColorUIResource getUserTextColor() {
        return getCurrentTheme().getUserTextColor();
    }
    
    public static ColorUIResource getTextHighlightColor() {
        return getCurrentTheme().getTextHighlightColor();
    }
    
    public static ColorUIResource getHighlightedTextColor() {
        return getCurrentTheme().getHighlightedTextColor();
    }
    
    public static ColorUIResource getWindowBackground() {
        return getCurrentTheme().getWindowBackground();
    }
    
    public static ColorUIResource getWindowTitleBackground() {
        return getCurrentTheme().getWindowTitleBackground();
    }
    
    public static ColorUIResource getWindowTitleForeground() {
        return getCurrentTheme().getWindowTitleForeground();
    }
    
    public static ColorUIResource getWindowTitleInactiveBackground() {
        return getCurrentTheme().getWindowTitleInactiveBackground();
    }
    
    public static ColorUIResource getWindowTitleInactiveForeground() {
        return getCurrentTheme().getWindowTitleInactiveForeground();
    }
    
    public static ColorUIResource getMenuBackground() {
        return getCurrentTheme().getMenuBackground();
    }
    
    public static ColorUIResource getMenuForeground() {
        return getCurrentTheme().getMenuForeground();
    }
    
    public static ColorUIResource getMenuSelectedBackground() {
        return getCurrentTheme().getMenuSelectedBackground();
    }
    
    public static ColorUIResource getMenuSelectedForeground() {
        return getCurrentTheme().getMenuSelectedForeground();
    }
    
    public static ColorUIResource getMenuDisabledForeground() {
        return getCurrentTheme().getMenuDisabledForeground();
    }
    
    public static ColorUIResource getSeparatorBackground() {
        return getCurrentTheme().getSeparatorBackground();
    }
    
    public static ColorUIResource getSeparatorForeground() {
        return getCurrentTheme().getSeparatorForeground();
    }
    
    public static ColorUIResource getAcceleratorForeground() {
        return getCurrentTheme().getAcceleratorForeground();
    }
    
    public static ColorUIResource getAcceleratorSelectedForeground() {
        return getCurrentTheme().getAcceleratorSelectedForeground();
    }
    
    @Override
    public LayoutStyle getLayoutStyle() {
        return MetalLayoutStyle.INSTANCE;
    }
    
    static void flushUnreferenced() {
        AATextListener aaTextListener;
        while ((aaTextListener = (AATextListener)MetalLookAndFeel.queue.poll()) != null) {
            aaTextListener.dispose();
        }
    }
    
    static {
        MetalLookAndFeel.METAL_LOOK_AND_FEEL_INITED = false;
        MetalLookAndFeel.queue = new ReferenceQueue<LookAndFeel>();
    }
    
    private static class FontActiveValue implements UIDefaults.ActiveValue
    {
        private int type;
        private MetalTheme theme;
        
        FontActiveValue(final MetalTheme theme, final int type) {
            this.theme = theme;
            this.type = type;
        }
        
        @Override
        public Object createValue(final UIDefaults uiDefaults) {
            Object o = null;
            switch (this.type) {
                case 0: {
                    o = this.theme.getControlTextFont();
                    break;
                }
                case 1: {
                    o = this.theme.getSystemTextFont();
                    break;
                }
                case 2: {
                    o = this.theme.getUserTextFont();
                    break;
                }
                case 3: {
                    o = this.theme.getMenuTextFont();
                    break;
                }
                case 4: {
                    o = this.theme.getWindowTitleFont();
                    break;
                }
                case 5: {
                    o = this.theme.getSubTextFont();
                    break;
                }
            }
            return o;
        }
    }
    
    static class AATextListener extends WeakReference<LookAndFeel> implements PropertyChangeListener
    {
        private String key;
        private static boolean updatePending;
        
        AATextListener(final LookAndFeel lookAndFeel) {
            super(lookAndFeel, MetalLookAndFeel.queue);
            this.key = "awt.font.desktophints";
            Toolkit.getDefaultToolkit().addPropertyChangeListener(this.key, this);
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final LookAndFeel lookAndFeel = this.get();
            if (lookAndFeel == null || lookAndFeel != UIManager.getLookAndFeel()) {
                this.dispose();
                return;
            }
            UIManager.getLookAndFeelDefaults().put(SwingUtilities2.AA_TEXT_PROPERTY_KEY, SwingUtilities2.AATextInfo.getAATextInfo(SwingUtilities2.isLocalDisplay()));
            this.updateUI();
        }
        
        void dispose() {
            Toolkit.getDefaultToolkit().removePropertyChangeListener(this.key, this);
        }
        
        private static void updateWindowUI(final Window window) {
            SwingUtilities.updateComponentTreeUI(window);
            final Window[] ownedWindows = window.getOwnedWindows();
            for (int length = ownedWindows.length, i = 0; i < length; ++i) {
                updateWindowUI(ownedWindows[i]);
            }
        }
        
        private static void updateAllUIs() {
            final Frame[] frames = Frame.getFrames();
            for (int length = frames.length, i = 0; i < length; ++i) {
                updateWindowUI(frames[i]);
            }
        }
        
        private static synchronized void setUpdatePending(final boolean updatePending) {
            AATextListener.updatePending = updatePending;
        }
        
        private static synchronized boolean isUpdatePending() {
            return AATextListener.updatePending;
        }
        
        protected void updateUI() {
            if (!isUpdatePending()) {
                setUpdatePending(true);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        updateAllUIs();
                        setUpdatePending(false);
                    }
                });
            }
        }
    }
    
    private static class MetalLayoutStyle extends DefaultLayoutStyle
    {
        private static MetalLayoutStyle INSTANCE;
        
        @Override
        public int getPreferredGap(final JComponent component, final JComponent component2, final ComponentPlacement componentPlacement, final int n, final Container container) {
            super.getPreferredGap(component, component2, componentPlacement, n, container);
            int n2 = 0;
            switch (componentPlacement) {
                case INDENT: {
                    if (n != 3 && n != 7)
                    final int indent = this.getIndent(component, n);
                    if (indent > 0) {
                        return indent;
                    }
                    return 12;
                }
                case RELATED: {
                    if (component.getUIClassID() != "ToggleButtonUI" || component2.getUIClassID() != "ToggleButtonUI") {
                        n2 = 6;
                        break;
                    }
                    final ButtonModel model = ((JToggleButton)component).getModel();
                    final ButtonModel model2 = ((JToggleButton)component2).getModel();
                    if (model instanceof DefaultButtonModel && model2 instanceof DefaultButtonModel && ((DefaultButtonModel)model).getGroup() == ((DefaultButtonModel)model2).getGroup() && ((DefaultButtonModel)model).getGroup() != null) {
                        return 2;
                    }
                    if (MetalLookAndFeel.usingOcean()) {
                        return 6;
                    }
                    return 5;
                }
                case UNRELATED: {
                    n2 = 12;
                    break;
                }
            }
            if (this.isLabelAndNonlabel(component, component2, n)) {
                return this.getButtonGap(component, component2, n, n2 + 6);
            }
            return this.getButtonGap(component, component2, n, n2);
        }
        
        @Override
        public int getContainerGap(final JComponent component, final int n, final Container container) {
            super.getContainerGap(component, n, container);
            return this.getButtonGap(component, n, 12 - this.getButtonAdjustment(component, n));
        }
        
        @Override
        protected int getButtonGap(final JComponent component, final JComponent component2, final int n, int buttonGap) {
            buttonGap = super.getButtonGap(component, component2, n, buttonGap);
            if (buttonGap > 0) {
                int n2 = this.getButtonAdjustment(component, n);
                if (n2 == 0) {
                    n2 = this.getButtonAdjustment(component2, this.flipDirection(n));
                }
                buttonGap -= n2;
            }
            if (buttonGap < 0) {
                return 0;
            }
            return buttonGap;
        }
        
        private int getButtonAdjustment(final JComponent component, final int n) {
            final String uiClassID = component.getUIClassID();
            if (uiClassID == "ButtonUI" || uiClassID == "ToggleButtonUI") {
                if (!MetalLookAndFeel.usingOcean() && (n == 3 || n == 5) && component.getBorder() instanceof UIResource) {
                    return 1;
                }
            }
            else if (n == 5 && (uiClassID == "RadioButtonUI" || uiClassID == "CheckBoxUI") && !MetalLookAndFeel.usingOcean()) {
                return 1;
            }
            return 0;
        }
        
        static {
            MetalLayoutStyle.INSTANCE = new MetalLayoutStyle();
        }
    }
}
