package javax.swing.plaf.synth;

import java.util.Locale;
import java.util.Iterator;
import javax.swing.UIDefaults;
import javax.swing.JComponent;
import java.util.HashMap;
import sun.awt.AppContext;
import java.util.Map;

public class Region
{
    private static final Object UI_TO_REGION_MAP_KEY;
    private static final Object LOWER_CASE_NAME_MAP_KEY;
    public static final Region ARROW_BUTTON;
    public static final Region BUTTON;
    public static final Region CHECK_BOX;
    public static final Region CHECK_BOX_MENU_ITEM;
    public static final Region COLOR_CHOOSER;
    public static final Region COMBO_BOX;
    public static final Region DESKTOP_PANE;
    public static final Region DESKTOP_ICON;
    public static final Region EDITOR_PANE;
    public static final Region FILE_CHOOSER;
    public static final Region FORMATTED_TEXT_FIELD;
    public static final Region INTERNAL_FRAME;
    public static final Region INTERNAL_FRAME_TITLE_PANE;
    public static final Region LABEL;
    public static final Region LIST;
    public static final Region MENU;
    public static final Region MENU_BAR;
    public static final Region MENU_ITEM;
    public static final Region MENU_ITEM_ACCELERATOR;
    public static final Region OPTION_PANE;
    public static final Region PANEL;
    public static final Region PASSWORD_FIELD;
    public static final Region POPUP_MENU;
    public static final Region POPUP_MENU_SEPARATOR;
    public static final Region PROGRESS_BAR;
    public static final Region RADIO_BUTTON;
    public static final Region RADIO_BUTTON_MENU_ITEM;
    public static final Region ROOT_PANE;
    public static final Region SCROLL_BAR;
    public static final Region SCROLL_BAR_TRACK;
    public static final Region SCROLL_BAR_THUMB;
    public static final Region SCROLL_PANE;
    public static final Region SEPARATOR;
    public static final Region SLIDER;
    public static final Region SLIDER_TRACK;
    public static final Region SLIDER_THUMB;
    public static final Region SPINNER;
    public static final Region SPLIT_PANE;
    public static final Region SPLIT_PANE_DIVIDER;
    public static final Region TABBED_PANE;
    public static final Region TABBED_PANE_TAB;
    public static final Region TABBED_PANE_TAB_AREA;
    public static final Region TABBED_PANE_CONTENT;
    public static final Region TABLE;
    public static final Region TABLE_HEADER;
    public static final Region TEXT_AREA;
    public static final Region TEXT_FIELD;
    public static final Region TEXT_PANE;
    public static final Region TOGGLE_BUTTON;
    public static final Region TOOL_BAR;
    public static final Region TOOL_BAR_CONTENT;
    public static final Region TOOL_BAR_DRAG_WINDOW;
    public static final Region TOOL_TIP;
    public static final Region TOOL_BAR_SEPARATOR;
    public static final Region TREE;
    public static final Region TREE_CELL;
    public static final Region VIEWPORT;
    private final String name;
    private final boolean subregion;
    
    private static Map<String, Region> getUItoRegionMap() {
        final AppContext appContext = AppContext.getAppContext();
        Map map = (Map)appContext.get(Region.UI_TO_REGION_MAP_KEY);
        if (map == null) {
            map = new HashMap();
            map.put("ArrowButtonUI", Region.ARROW_BUTTON);
            map.put("ButtonUI", Region.BUTTON);
            map.put("CheckBoxUI", Region.CHECK_BOX);
            map.put("CheckBoxMenuItemUI", Region.CHECK_BOX_MENU_ITEM);
            map.put("ColorChooserUI", Region.COLOR_CHOOSER);
            map.put("ComboBoxUI", Region.COMBO_BOX);
            map.put("DesktopPaneUI", Region.DESKTOP_PANE);
            map.put("DesktopIconUI", Region.DESKTOP_ICON);
            map.put("EditorPaneUI", Region.EDITOR_PANE);
            map.put("FileChooserUI", Region.FILE_CHOOSER);
            map.put("FormattedTextFieldUI", Region.FORMATTED_TEXT_FIELD);
            map.put("InternalFrameUI", Region.INTERNAL_FRAME);
            map.put("InternalFrameTitlePaneUI", Region.INTERNAL_FRAME_TITLE_PANE);
            map.put("LabelUI", Region.LABEL);
            map.put("ListUI", Region.LIST);
            map.put("MenuUI", Region.MENU);
            map.put("MenuBarUI", Region.MENU_BAR);
            map.put("MenuItemUI", Region.MENU_ITEM);
            map.put("OptionPaneUI", Region.OPTION_PANE);
            map.put("PanelUI", Region.PANEL);
            map.put("PasswordFieldUI", Region.PASSWORD_FIELD);
            map.put("PopupMenuUI", Region.POPUP_MENU);
            map.put("PopupMenuSeparatorUI", Region.POPUP_MENU_SEPARATOR);
            map.put("ProgressBarUI", Region.PROGRESS_BAR);
            map.put("RadioButtonUI", Region.RADIO_BUTTON);
            map.put("RadioButtonMenuItemUI", Region.RADIO_BUTTON_MENU_ITEM);
            map.put("RootPaneUI", Region.ROOT_PANE);
            map.put("ScrollBarUI", Region.SCROLL_BAR);
            map.put("ScrollPaneUI", Region.SCROLL_PANE);
            map.put("SeparatorUI", Region.SEPARATOR);
            map.put("SliderUI", Region.SLIDER);
            map.put("SpinnerUI", Region.SPINNER);
            map.put("SplitPaneUI", Region.SPLIT_PANE);
            map.put("TabbedPaneUI", Region.TABBED_PANE);
            map.put("TableUI", Region.TABLE);
            map.put("TableHeaderUI", Region.TABLE_HEADER);
            map.put("TextAreaUI", Region.TEXT_AREA);
            map.put("TextFieldUI", Region.TEXT_FIELD);
            map.put("TextPaneUI", Region.TEXT_PANE);
            map.put("ToggleButtonUI", Region.TOGGLE_BUTTON);
            map.put("ToolBarUI", Region.TOOL_BAR);
            map.put("ToolTipUI", Region.TOOL_TIP);
            map.put("ToolBarSeparatorUI", Region.TOOL_BAR_SEPARATOR);
            map.put("TreeUI", Region.TREE);
            map.put("ViewportUI", Region.VIEWPORT);
            appContext.put(Region.UI_TO_REGION_MAP_KEY, map);
        }
        return map;
    }
    
    private static Map<Region, String> getLowerCaseNameMap() {
        final AppContext appContext = AppContext.getAppContext();
        Map map = (Map)appContext.get(Region.LOWER_CASE_NAME_MAP_KEY);
        if (map == null) {
            map = new HashMap();
            appContext.put(Region.LOWER_CASE_NAME_MAP_KEY, map);
        }
        return map;
    }
    
    static Region getRegion(final JComponent component) {
        return getUItoRegionMap().get(component.getUIClassID());
    }
    
    static void registerUIs(final UIDefaults uiDefaults) {
        final Iterator<String> iterator = getUItoRegionMap().keySet().iterator();
        while (iterator.hasNext()) {
            uiDefaults.put(iterator.next(), "javax.swing.plaf.synth.SynthLookAndFeel");
        }
    }
    
    private Region(final String name, final boolean subregion) {
        if (name == null) {
            throw new NullPointerException("You must specify a non-null name");
        }
        this.name = name;
        this.subregion = subregion;
    }
    
    protected Region(final String s, final String s2, final boolean b) {
        this(s, b);
        if (s2 != null) {
            getUItoRegionMap().put(s2, this);
        }
    }
    
    public boolean isSubregion() {
        return this.subregion;
    }
    
    public String getName() {
        return this.name;
    }
    
    String getLowerCaseName() {
        final Map<Region, String> lowerCaseNameMap = getLowerCaseNameMap();
        String lowerCase = lowerCaseNameMap.get(this);
        if (lowerCase == null) {
            lowerCase = this.name.toLowerCase(Locale.ENGLISH);
            lowerCaseNameMap.put(this, lowerCase);
        }
        return lowerCase;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    static {
        UI_TO_REGION_MAP_KEY = new Object();
        LOWER_CASE_NAME_MAP_KEY = new Object();
        ARROW_BUTTON = new Region("ArrowButton", false);
        BUTTON = new Region("Button", false);
        CHECK_BOX = new Region("CheckBox", false);
        CHECK_BOX_MENU_ITEM = new Region("CheckBoxMenuItem", false);
        COLOR_CHOOSER = new Region("ColorChooser", false);
        COMBO_BOX = new Region("ComboBox", false);
        DESKTOP_PANE = new Region("DesktopPane", false);
        DESKTOP_ICON = new Region("DesktopIcon", false);
        EDITOR_PANE = new Region("EditorPane", false);
        FILE_CHOOSER = new Region("FileChooser", false);
        FORMATTED_TEXT_FIELD = new Region("FormattedTextField", false);
        INTERNAL_FRAME = new Region("InternalFrame", false);
        INTERNAL_FRAME_TITLE_PANE = new Region("InternalFrameTitlePane", false);
        LABEL = new Region("Label", false);
        LIST = new Region("List", false);
        MENU = new Region("Menu", false);
        MENU_BAR = new Region("MenuBar", false);
        MENU_ITEM = new Region("MenuItem", false);
        MENU_ITEM_ACCELERATOR = new Region("MenuItemAccelerator", true);
        OPTION_PANE = new Region("OptionPane", false);
        PANEL = new Region("Panel", false);
        PASSWORD_FIELD = new Region("PasswordField", false);
        POPUP_MENU = new Region("PopupMenu", false);
        POPUP_MENU_SEPARATOR = new Region("PopupMenuSeparator", false);
        PROGRESS_BAR = new Region("ProgressBar", false);
        RADIO_BUTTON = new Region("RadioButton", false);
        RADIO_BUTTON_MENU_ITEM = new Region("RadioButtonMenuItem", false);
        ROOT_PANE = new Region("RootPane", false);
        SCROLL_BAR = new Region("ScrollBar", false);
        SCROLL_BAR_TRACK = new Region("ScrollBarTrack", true);
        SCROLL_BAR_THUMB = new Region("ScrollBarThumb", true);
        SCROLL_PANE = new Region("ScrollPane", false);
        SEPARATOR = new Region("Separator", false);
        SLIDER = new Region("Slider", false);
        SLIDER_TRACK = new Region("SliderTrack", true);
        SLIDER_THUMB = new Region("SliderThumb", true);
        SPINNER = new Region("Spinner", false);
        SPLIT_PANE = new Region("SplitPane", false);
        SPLIT_PANE_DIVIDER = new Region("SplitPaneDivider", true);
        TABBED_PANE = new Region("TabbedPane", false);
        TABBED_PANE_TAB = new Region("TabbedPaneTab", true);
        TABBED_PANE_TAB_AREA = new Region("TabbedPaneTabArea", true);
        TABBED_PANE_CONTENT = new Region("TabbedPaneContent", true);
        TABLE = new Region("Table", false);
        TABLE_HEADER = new Region("TableHeader", false);
        TEXT_AREA = new Region("TextArea", false);
        TEXT_FIELD = new Region("TextField", false);
        TEXT_PANE = new Region("TextPane", false);
        TOGGLE_BUTTON = new Region("ToggleButton", false);
        TOOL_BAR = new Region("ToolBar", false);
        TOOL_BAR_CONTENT = new Region("ToolBarContent", true);
        TOOL_BAR_DRAG_WINDOW = new Region("ToolBarDragWindow", false);
        TOOL_TIP = new Region("ToolTip", false);
        TOOL_BAR_SEPARATOR = new Region("ToolBarSeparator", false);
        TREE = new Region("Tree", false);
        TREE_CELL = new Region("TreeCell", true);
        VIEWPORT = new Region("Viewport", false);
    }
}
