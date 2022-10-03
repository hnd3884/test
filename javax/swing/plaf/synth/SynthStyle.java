package javax.swing.plaf.synth;

import javax.swing.Icon;
import javax.swing.border.Border;
import java.awt.Insets;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import java.util.HashMap;
import javax.swing.LookAndFeel;
import javax.swing.plaf.basic.BasicLookAndFeel;
import java.awt.Color;
import javax.swing.UIDefaults;
import java.util.Map;

public abstract class SynthStyle
{
    private static Map<Object, Object> DEFAULT_VALUES;
    private static final SynthGraphicsUtils SYNTH_GRAPHICS;
    
    private static void populateDefaultValues() {
        final UIDefaults.LazyInputMap lazyInputMap = new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" });
        SynthStyle.DEFAULT_VALUES.put("Button.focusInputMap", lazyInputMap);
        SynthStyle.DEFAULT_VALUES.put("CheckBox.focusInputMap", lazyInputMap);
        SynthStyle.DEFAULT_VALUES.put("RadioButton.focusInputMap", lazyInputMap);
        SynthStyle.DEFAULT_VALUES.put("ToggleButton.focusInputMap", lazyInputMap);
        SynthStyle.DEFAULT_VALUES.put("SynthArrowButton.focusInputMap", lazyInputMap);
        SynthStyle.DEFAULT_VALUES.put("List.dropLineColor", Color.BLACK);
        SynthStyle.DEFAULT_VALUES.put("Tree.dropLineColor", Color.BLACK);
        SynthStyle.DEFAULT_VALUES.put("Table.dropLineColor", Color.BLACK);
        SynthStyle.DEFAULT_VALUES.put("Table.dropLineShortColor", Color.RED);
        final UIDefaults.LazyInputMap lazyInputMap2 = new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "UP", "caret-up", "KP_UP", "caret-up", "DOWN", "caret-down", "KP_DOWN", "caret-down", "PAGE_UP", "page-up", "PAGE_DOWN", "page-down", "shift PAGE_UP", "selection-page-up", "shift PAGE_DOWN", "selection-page-down", "ctrl shift PAGE_UP", "selection-page-left", "ctrl shift PAGE_DOWN", "selection-page-right", "shift UP", "selection-up", "shift KP_UP", "selection-up", "shift DOWN", "selection-down", "shift KP_DOWN", "selection-down", "ENTER", "insert-break", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "TAB", "insert-tab", "ctrl BACK_SLASH", "unselect", "ctrl HOME", "caret-begin", "ctrl END", "caret-end", "ctrl shift HOME", "selection-begin", "ctrl shift END", "selection-end", "ctrl T", "next-link-action", "ctrl shift T", "previous-link-action", "ctrl SPACE", "activate-link-action", "control shift O", "toggle-componentOrientation" });
        SynthStyle.DEFAULT_VALUES.put("EditorPane.focusInputMap", lazyInputMap2);
        SynthStyle.DEFAULT_VALUES.put("TextArea.focusInputMap", lazyInputMap2);
        SynthStyle.DEFAULT_VALUES.put("TextPane.focusInputMap", lazyInputMap2);
        final UIDefaults.LazyInputMap lazyInputMap3 = new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation" });
        SynthStyle.DEFAULT_VALUES.put("TextField.focusInputMap", lazyInputMap3);
        SynthStyle.DEFAULT_VALUES.put("PasswordField.focusInputMap", lazyInputMap3);
        SynthStyle.DEFAULT_VALUES.put("ComboBox.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "hidePopup", "PAGE_UP", "pageUpPassThrough", "PAGE_DOWN", "pageDownPassThrough", "HOME", "homePassThrough", "END", "endPassThrough", "DOWN", "selectNext", "KP_DOWN", "selectNext", "alt DOWN", "togglePopup", "alt KP_DOWN", "togglePopup", "alt UP", "togglePopup", "alt KP_UP", "togglePopup", "SPACE", "spacePopup", "ENTER", "enterPressed", "UP", "selectPrevious", "KP_UP", "selectPrevious" }));
        SynthStyle.DEFAULT_VALUES.put("Desktop.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl F5", "restore", "ctrl F4", "close", "ctrl F7", "move", "ctrl F8", "resize", "RIGHT", "right", "KP_RIGHT", "right", "shift RIGHT", "shrinkRight", "shift KP_RIGHT", "shrinkRight", "LEFT", "left", "KP_LEFT", "left", "shift LEFT", "shrinkLeft", "shift KP_LEFT", "shrinkLeft", "UP", "up", "KP_UP", "up", "shift UP", "shrinkUp", "shift KP_UP", "shrinkUp", "DOWN", "down", "KP_DOWN", "down", "shift DOWN", "shrinkDown", "shift KP_DOWN", "shrinkDown", "ESCAPE", "escape", "ctrl F9", "minimize", "ctrl F10", "maximize", "ctrl F6", "selectNextFrame", "ctrl TAB", "selectNextFrame", "ctrl alt F6", "selectNextFrame", "shift ctrl alt F6", "selectPreviousFrame", "ctrl F12", "navigateNext", "shift ctrl F12", "navigatePrevious" }));
        SynthStyle.DEFAULT_VALUES.put("FileChooser.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancelSelection", "F2", "editFileName", "F5", "refresh", "BACK_SPACE", "Go Up", "ENTER", "approveSelection", "ctrl ENTER", "approveSelection" }));
        SynthStyle.DEFAULT_VALUES.put("FormattedTextField.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy-to-clipboard", "ctrl V", "paste-from-clipboard", "ctrl X", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", "CUT", "cut-to-clipboard", "control INSERT", "copy-to-clipboard", "shift INSERT", "paste-from-clipboard", "shift DELETE", "cut-to-clipboard", "shift LEFT", "selection-backward", "shift KP_LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_RIGHT", "selection-forward", "ctrl LEFT", "caret-previous-word", "ctrl KP_LEFT", "caret-previous-word", "ctrl RIGHT", "caret-next-word", "ctrl KP_RIGHT", "caret-next-word", "ctrl shift LEFT", "selection-previous-word", "ctrl shift KP_LEFT", "selection-previous-word", "ctrl shift RIGHT", "selection-next-word", "ctrl shift KP_RIGHT", "selection-next-word", "ctrl A", "select-all", "HOME", "caret-begin-line", "END", "caret-end-line", "shift HOME", "selection-begin-line", "shift END", "selection-end-line", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", "ctrl DELETE", "delete-next-word", "ctrl BACK_SPACE", "delete-previous-word", "RIGHT", "caret-forward", "LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "ENTER", "notify-field-accept", "ctrl BACK_SLASH", "unselect", "control shift O", "toggle-componentOrientation", "ESCAPE", "reset-field-edit", "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }));
        SynthStyle.DEFAULT_VALUES.put("InternalFrame.icon", LookAndFeel.makeIcon(BasicLookAndFeel.class, "icons/JavaCup16.png"));
        SynthStyle.DEFAULT_VALUES.put("InternalFrame.windowBindings", new Object[] { "shift ESCAPE", "showSystemMenu", "ctrl SPACE", "showSystemMenu", "ESCAPE", "hideSystemMenu" });
        SynthStyle.DEFAULT_VALUES.put("List.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "HOME", "selectFirstRow", "shift HOME", "selectFirstRowExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRowChangeLead", "END", "selectLastRow", "shift END", "selectLastRowExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRowChangeLead", "PAGE_UP", "scrollUp", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDown", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }));
        SynthStyle.DEFAULT_VALUES.put("List.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "LEFT", "selectNextColumn", "KP_LEFT", "selectNextColumn", "shift LEFT", "selectNextColumnExtendSelection", "shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl shift LEFT", "selectNextColumnExtendSelection", "ctrl shift KP_LEFT", "selectNextColumnExtendSelection", "ctrl LEFT", "selectNextColumnChangeLead", "ctrl KP_LEFT", "selectNextColumnChangeLead", "RIGHT", "selectPreviousColumn", "KP_RIGHT", "selectPreviousColumn", "shift RIGHT", "selectPreviousColumnExtendSelection", "shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift RIGHT", "selectPreviousColumnExtendSelection", "ctrl shift KP_RIGHT", "selectPreviousColumnExtendSelection", "ctrl RIGHT", "selectPreviousColumnChangeLead", "ctrl KP_RIGHT", "selectPreviousColumnChangeLead" }));
        SynthStyle.DEFAULT_VALUES.put("MenuBar.windowBindings", new Object[] { "F10", "takeFocus" });
        SynthStyle.DEFAULT_VALUES.put("OptionPane.windowBindings", new Object[] { "ESCAPE", "close" });
        SynthStyle.DEFAULT_VALUES.put("RootPane.defaultButtonWindowKeyBindings", new Object[] { "ENTER", "press", "released ENTER", "release", "ctrl ENTER", "press", "ctrl released ENTER", "release" });
        SynthStyle.DEFAULT_VALUES.put("RootPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "shift F10", "postPopup" }));
        SynthStyle.DEFAULT_VALUES.put("ScrollBar.anecstorInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "positiveUnitIncrement", "KP_DOWN", "positiveUnitIncrement", "PAGE_DOWN", "positiveBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "negativeUnitIncrement", "KP_UP", "negativeUnitIncrement", "PAGE_UP", "negativeBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }));
        SynthStyle.DEFAULT_VALUES.put("ScrollBar.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement" }));
        SynthStyle.DEFAULT_VALUES.put("ScrollPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "unitScrollRight", "KP_RIGHT", "unitScrollRight", "DOWN", "unitScrollDown", "KP_DOWN", "unitScrollDown", "LEFT", "unitScrollLeft", "KP_LEFT", "unitScrollLeft", "UP", "unitScrollUp", "KP_UP", "unitScrollUp", "PAGE_UP", "scrollUp", "PAGE_DOWN", "scrollDown", "ctrl PAGE_UP", "scrollLeft", "ctrl PAGE_DOWN", "scrollRight", "ctrl HOME", "scrollHome", "ctrl END", "scrollEnd" }));
        SynthStyle.DEFAULT_VALUES.put("ScrollPane.ancestorInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "ctrl PAGE_UP", "scrollRight", "ctrl PAGE_DOWN", "scrollLeft" }));
        SynthStyle.DEFAULT_VALUES.put("SplitPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "negativeIncrement", "DOWN", "positiveIncrement", "LEFT", "negativeIncrement", "RIGHT", "positiveIncrement", "KP_UP", "negativeIncrement", "KP_DOWN", "positiveIncrement", "KP_LEFT", "negativeIncrement", "KP_RIGHT", "positiveIncrement", "HOME", "selectMin", "END", "selectMax", "F8", "startResize", "F6", "toggleFocus", "ctrl TAB", "focusOutForward", "ctrl shift TAB", "focusOutBackward" }));
        SynthStyle.DEFAULT_VALUES.put("Spinner.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "UP", "increment", "KP_UP", "increment", "DOWN", "decrement", "KP_DOWN", "decrement" }));
        SynthStyle.DEFAULT_VALUES.put("Slider.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "positiveUnitIncrement", "KP_RIGHT", "positiveUnitIncrement", "DOWN", "negativeUnitIncrement", "KP_DOWN", "negativeUnitIncrement", "PAGE_DOWN", "negativeBlockIncrement", "ctrl PAGE_DOWN", "negativeBlockIncrement", "LEFT", "negativeUnitIncrement", "KP_LEFT", "negativeUnitIncrement", "UP", "positiveUnitIncrement", "KP_UP", "positiveUnitIncrement", "PAGE_UP", "positiveBlockIncrement", "ctrl PAGE_UP", "positiveBlockIncrement", "HOME", "minScroll", "END", "maxScroll" }));
        SynthStyle.DEFAULT_VALUES.put("Slider.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "negativeUnitIncrement", "KP_RIGHT", "negativeUnitIncrement", "LEFT", "positiveUnitIncrement", "KP_LEFT", "positiveUnitIncrement" }));
        SynthStyle.DEFAULT_VALUES.put("TabbedPane.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl PAGE_DOWN", "navigatePageDown", "ctrl PAGE_UP", "navigatePageUp", "ctrl UP", "requestFocus", "ctrl KP_UP", "requestFocus" }));
        SynthStyle.DEFAULT_VALUES.put("TabbedPane.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "navigateRight", "KP_RIGHT", "navigateRight", "LEFT", "navigateLeft", "KP_LEFT", "navigateLeft", "UP", "navigateUp", "KP_UP", "navigateUp", "DOWN", "navigateDown", "KP_DOWN", "navigateDown", "ctrl DOWN", "requestFocusForVisibleComponent", "ctrl KP_DOWN", "requestFocusForVisibleComponent" }));
        SynthStyle.DEFAULT_VALUES.put("Table.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "RIGHT", "selectNextColumn", "KP_RIGHT", "selectNextColumn", "shift RIGHT", "selectNextColumnExtendSelection", "shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl shift RIGHT", "selectNextColumnExtendSelection", "ctrl shift KP_RIGHT", "selectNextColumnExtendSelection", "ctrl RIGHT", "selectNextColumnChangeLead", "ctrl KP_RIGHT", "selectNextColumnChangeLead", "LEFT", "selectPreviousColumn", "KP_LEFT", "selectPreviousColumn", "shift LEFT", "selectPreviousColumnExtendSelection", "shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl shift LEFT", "selectPreviousColumnExtendSelection", "ctrl shift KP_LEFT", "selectPreviousColumnExtendSelection", "ctrl LEFT", "selectPreviousColumnChangeLead", "ctrl KP_LEFT", "selectPreviousColumnChangeLead", "DOWN", "selectNextRow", "KP_DOWN", "selectNextRow", "shift DOWN", "selectNextRowExtendSelection", "shift KP_DOWN", "selectNextRowExtendSelection", "ctrl shift DOWN", "selectNextRowExtendSelection", "ctrl shift KP_DOWN", "selectNextRowExtendSelection", "ctrl DOWN", "selectNextRowChangeLead", "ctrl KP_DOWN", "selectNextRowChangeLead", "UP", "selectPreviousRow", "KP_UP", "selectPreviousRow", "shift UP", "selectPreviousRowExtendSelection", "shift KP_UP", "selectPreviousRowExtendSelection", "ctrl shift UP", "selectPreviousRowExtendSelection", "ctrl shift KP_UP", "selectPreviousRowExtendSelection", "ctrl UP", "selectPreviousRowChangeLead", "ctrl KP_UP", "selectPreviousRowChangeLead", "HOME", "selectFirstColumn", "shift HOME", "selectFirstColumnExtendSelection", "ctrl shift HOME", "selectFirstRowExtendSelection", "ctrl HOME", "selectFirstRow", "END", "selectLastColumn", "shift END", "selectLastColumnExtendSelection", "ctrl shift END", "selectLastRowExtendSelection", "ctrl END", "selectLastRow", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollLeftExtendSelection", "ctrl PAGE_UP", "scrollLeftChangeSelection", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollRightExtendSelection", "ctrl PAGE_DOWN", "scrollRightChangeSelection", "TAB", "selectNextColumnCell", "shift TAB", "selectPreviousColumnCell", "ENTER", "selectNextRowCell", "shift ENTER", "selectPreviousRowCell", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ESCAPE", "cancel", "F2", "startEditing", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo", "F8", "focusHeader" }));
        SynthStyle.DEFAULT_VALUES.put("TableHeader.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "toggleSortOrder", "LEFT", "selectColumnToLeft", "KP_LEFT", "selectColumnToLeft", "RIGHT", "selectColumnToRight", "KP_RIGHT", "selectColumnToRight", "alt LEFT", "moveColumnLeft", "alt KP_LEFT", "moveColumnLeft", "alt RIGHT", "moveColumnRight", "alt KP_RIGHT", "moveColumnRight", "alt shift LEFT", "resizeLeft", "alt shift KP_LEFT", "resizeLeft", "alt shift RIGHT", "resizeRight", "alt shift KP_RIGHT", "resizeRight", "ESCAPE", "focusTable" }));
        SynthStyle.DEFAULT_VALUES.put("Tree.ancestorInputMap", new UIDefaults.LazyInputMap(new Object[] { "ESCAPE", "cancel" }));
        SynthStyle.DEFAULT_VALUES.put("Tree.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "ADD", "expand", "SUBTRACT", "collapse", "ctrl C", "copy", "ctrl V", "paste", "ctrl X", "cut", "COPY", "copy", "PASTE", "paste", "CUT", "cut", "control INSERT", "copy", "shift INSERT", "paste", "shift DELETE", "cut", "UP", "selectPrevious", "KP_UP", "selectPrevious", "shift UP", "selectPreviousExtendSelection", "shift KP_UP", "selectPreviousExtendSelection", "ctrl shift UP", "selectPreviousExtendSelection", "ctrl shift KP_UP", "selectPreviousExtendSelection", "ctrl UP", "selectPreviousChangeLead", "ctrl KP_UP", "selectPreviousChangeLead", "DOWN", "selectNext", "KP_DOWN", "selectNext", "shift DOWN", "selectNextExtendSelection", "shift KP_DOWN", "selectNextExtendSelection", "ctrl shift DOWN", "selectNextExtendSelection", "ctrl shift KP_DOWN", "selectNextExtendSelection", "ctrl DOWN", "selectNextChangeLead", "ctrl KP_DOWN", "selectNextChangeLead", "RIGHT", "selectChild", "KP_RIGHT", "selectChild", "LEFT", "selectParent", "KP_LEFT", "selectParent", "PAGE_UP", "scrollUpChangeSelection", "shift PAGE_UP", "scrollUpExtendSelection", "ctrl shift PAGE_UP", "scrollUpExtendSelection", "ctrl PAGE_UP", "scrollUpChangeLead", "PAGE_DOWN", "scrollDownChangeSelection", "shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl shift PAGE_DOWN", "scrollDownExtendSelection", "ctrl PAGE_DOWN", "scrollDownChangeLead", "HOME", "selectFirst", "shift HOME", "selectFirstExtendSelection", "ctrl shift HOME", "selectFirstExtendSelection", "ctrl HOME", "selectFirstChangeLead", "END", "selectLast", "shift END", "selectLastExtendSelection", "ctrl shift END", "selectLastExtendSelection", "ctrl END", "selectLastChangeLead", "F2", "startEditing", "ctrl A", "selectAll", "ctrl SLASH", "selectAll", "ctrl BACK_SLASH", "clearSelection", "ctrl LEFT", "scrollLeft", "ctrl KP_LEFT", "scrollLeft", "ctrl RIGHT", "scrollRight", "ctrl KP_RIGHT", "scrollRight", "SPACE", "addToSelection", "ctrl SPACE", "toggleAndAnchor", "shift SPACE", "extendTo", "ctrl shift SPACE", "moveSelectionTo" }));
        SynthStyle.DEFAULT_VALUES.put("Tree.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "RIGHT", "selectParent", "KP_RIGHT", "selectParent", "LEFT", "selectChild", "KP_LEFT", "selectChild" }));
    }
    
    private static Object getDefaultValue(final Object o) {
        synchronized (SynthStyle.class) {
            if (SynthStyle.DEFAULT_VALUES == null) {
                SynthStyle.DEFAULT_VALUES = new HashMap<Object, Object>();
                populateDefaultValues();
            }
            Object o2 = SynthStyle.DEFAULT_VALUES.get(o);
            if (o2 instanceof UIDefaults.LazyValue) {
                o2 = ((UIDefaults.LazyValue)o2).createValue(null);
                SynthStyle.DEFAULT_VALUES.put(o, o2);
            }
            return o2;
        }
    }
    
    public SynthGraphicsUtils getGraphicsUtils(final SynthContext synthContext) {
        return SynthStyle.SYNTH_GRAPHICS;
    }
    
    public Color getColor(final SynthContext synthContext, final ColorType colorType) {
        final JComponent component = synthContext.getComponent();
        final Region region = synthContext.getRegion();
        if ((synthContext.getComponentState() & 0x8) != 0x0) {
            if (component instanceof JTextComponent) {
                final Color disabledTextColor = ((JTextComponent)component).getDisabledTextColor();
                if (disabledTextColor == null || disabledTextColor instanceof UIResource) {
                    return this.getColorForState(synthContext, colorType);
                }
            }
            else if (component instanceof JLabel && (colorType == ColorType.FOREGROUND || colorType == ColorType.TEXT_FOREGROUND)) {
                return this.getColorForState(synthContext, colorType);
            }
        }
        Color color = null;
        if (!region.isSubregion()) {
            if (colorType == ColorType.BACKGROUND) {
                color = component.getBackground();
            }
            else if (colorType == ColorType.FOREGROUND) {
                color = component.getForeground();
            }
            else if (colorType == ColorType.TEXT_FOREGROUND) {
                color = component.getForeground();
            }
        }
        if (color == null || color instanceof UIResource) {
            color = this.getColorForState(synthContext, colorType);
        }
        if (color == null) {
            if (colorType == ColorType.BACKGROUND || colorType == ColorType.TEXT_BACKGROUND) {
                return component.getBackground();
            }
            if (colorType == ColorType.FOREGROUND || colorType == ColorType.TEXT_FOREGROUND) {
                return component.getForeground();
            }
        }
        return color;
    }
    
    protected abstract Color getColorForState(final SynthContext p0, final ColorType p1);
    
    public Font getFont(final SynthContext synthContext) {
        final JComponent component = synthContext.getComponent();
        if (synthContext.getComponentState() == 1) {
            return component.getFont();
        }
        final Font font = component.getFont();
        if (font != null && !(font instanceof UIResource)) {
            return font;
        }
        return this.getFontForState(synthContext);
    }
    
    protected abstract Font getFontForState(final SynthContext p0);
    
    public Insets getInsets(final SynthContext synthContext, Insets insets) {
        if (insets == null) {
            insets = new Insets(0, 0, 0, 0);
        }
        final Insets insets2 = insets;
        final Insets insets3 = insets;
        final Insets insets4 = insets;
        final Insets insets5 = insets;
        final int n = 0;
        insets5.right = n;
        insets4.left = n;
        insets3.bottom = n;
        insets2.top = n;
        return insets;
    }
    
    public SynthPainter getPainter(final SynthContext synthContext) {
        return null;
    }
    
    public boolean isOpaque(final SynthContext synthContext) {
        return true;
    }
    
    public Object get(final SynthContext synthContext, final Object o) {
        return getDefaultValue(o);
    }
    
    void installDefaults(final SynthContext synthContext, final SynthUI synthUI) {
        if (!synthContext.isSubregion()) {
            final JComponent component = synthContext.getComponent();
            final Border border = component.getBorder();
            if (border == null || border instanceof UIResource) {
                component.setBorder(new SynthBorder(synthUI, this.getInsets(synthContext, null)));
            }
        }
        this.installDefaults(synthContext);
    }
    
    public void installDefaults(final SynthContext synthContext) {
        if (!synthContext.isSubregion()) {
            final JComponent component = synthContext.getComponent();
            synthContext.getRegion();
            final Font font = component.getFont();
            if (font == null || font instanceof UIResource) {
                component.setFont(this.getFontForState(synthContext));
            }
            final Color background = component.getBackground();
            if (background == null || background instanceof UIResource) {
                component.setBackground(this.getColorForState(synthContext, ColorType.BACKGROUND));
            }
            final Color foreground = component.getForeground();
            if (foreground == null || foreground instanceof UIResource) {
                component.setForeground(this.getColorForState(synthContext, ColorType.FOREGROUND));
            }
            LookAndFeel.installProperty(component, "opaque", this.isOpaque(synthContext));
        }
    }
    
    public void uninstallDefaults(final SynthContext synthContext) {
        if (!synthContext.isSubregion()) {
            final JComponent component = synthContext.getComponent();
            if (component.getBorder() instanceof UIResource) {
                component.setBorder(null);
            }
        }
    }
    
    public int getInt(final SynthContext synthContext, final Object o, final int n) {
        final Object value = this.get(synthContext, o);
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        return n;
    }
    
    public boolean getBoolean(final SynthContext synthContext, final Object o, final boolean b) {
        final Object value = this.get(synthContext, o);
        if (value instanceof Boolean) {
            return (boolean)value;
        }
        return b;
    }
    
    public Icon getIcon(final SynthContext synthContext, final Object o) {
        final Object value = this.get(synthContext, o);
        if (value instanceof Icon) {
            return (Icon)value;
        }
        return null;
    }
    
    public String getString(final SynthContext synthContext, final Object o, final String s) {
        final Object value = this.get(synthContext, o);
        if (value instanceof String) {
            return (String)value;
        }
        return s;
    }
    
    static {
        SYNTH_GRAPHICS = new SynthGraphicsUtils();
    }
}
