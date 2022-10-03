package javax.swing.plaf.nimbus;

import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Graphics;
import javax.swing.Painter;
import javax.swing.plaf.UIResource;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import javax.swing.JInternalFrame;
import java.awt.Component;
import java.lang.reflect.Constructor;
import javax.swing.plaf.ColorUIResource;
import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.BorderFactory;
import javax.swing.plaf.DimensionUIResource;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.UIDefaults;
import java.awt.Font;
import sun.font.FontUtilities;
import java.util.WeakHashMap;
import java.util.HashMap;
import java.beans.PropertyChangeListener;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import sun.swing.plaf.synth.DefaultSynthStyle;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.JComponent;
import java.util.List;
import javax.swing.plaf.synth.Region;
import java.util.Map;

final class NimbusDefaults
{
    private Map<Region, List<LazyStyle>> m;
    private Map<String, Region> registeredRegions;
    private Map<JComponent, Map<Region, SynthStyle>> overridesCache;
    private DefaultSynthStyle defaultStyle;
    private FontUIResource defaultFont;
    private ColorTree colorTree;
    private DefaultsListener defaultsListener;
    private Map<DerivedColor, DerivedColor> derivedColors;
    
    void initialize() {
        UIManager.addPropertyChangeListener(this.defaultsListener);
        UIManager.getDefaults().addPropertyChangeListener(this.colorTree);
    }
    
    void uninitialize() {
        UIManager.removePropertyChangeListener(this.defaultsListener);
        UIManager.getDefaults().removePropertyChangeListener(this.colorTree);
    }
    
    NimbusDefaults() {
        this.registeredRegions = new HashMap<String, Region>();
        this.overridesCache = new WeakHashMap<JComponent, Map<Region, SynthStyle>>();
        this.colorTree = new ColorTree();
        this.defaultsListener = new DefaultsListener();
        this.derivedColors = new HashMap<DerivedColor, DerivedColor>();
        this.m = new HashMap<Region, List<LazyStyle>>();
        this.defaultFont = FontUtilities.getFontConfigFUIR("sans", 0, 12);
        (this.defaultStyle = new DefaultSynthStyle()).setFont(this.defaultFont);
        this.register(Region.ARROW_BUTTON, "ArrowButton");
        this.register(Region.BUTTON, "Button");
        this.register(Region.TOGGLE_BUTTON, "ToggleButton");
        this.register(Region.RADIO_BUTTON, "RadioButton");
        this.register(Region.CHECK_BOX, "CheckBox");
        this.register(Region.COLOR_CHOOSER, "ColorChooser");
        this.register(Region.PANEL, "ColorChooser:\"ColorChooser.previewPanelHolder\"");
        this.register(Region.LABEL, "ColorChooser:\"ColorChooser.previewPanelHolder\":\"OptionPane.label\"");
        this.register(Region.COMBO_BOX, "ComboBox");
        this.register(Region.TEXT_FIELD, "ComboBox:\"ComboBox.textField\"");
        this.register(Region.ARROW_BUTTON, "ComboBox:\"ComboBox.arrowButton\"");
        this.register(Region.LABEL, "ComboBox:\"ComboBox.listRenderer\"");
        this.register(Region.LABEL, "ComboBox:\"ComboBox.renderer\"");
        this.register(Region.SCROLL_PANE, "\"ComboBox.scrollPane\"");
        this.register(Region.FILE_CHOOSER, "FileChooser");
        this.register(Region.INTERNAL_FRAME_TITLE_PANE, "InternalFrameTitlePane");
        this.register(Region.INTERNAL_FRAME, "InternalFrame");
        this.register(Region.INTERNAL_FRAME_TITLE_PANE, "InternalFrame:InternalFrameTitlePane");
        this.register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"");
        this.register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"");
        this.register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"");
        this.register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"");
        this.register(Region.DESKTOP_ICON, "DesktopIcon");
        this.register(Region.DESKTOP_PANE, "DesktopPane");
        this.register(Region.LABEL, "Label");
        this.register(Region.LIST, "List");
        this.register(Region.LABEL, "List:\"List.cellRenderer\"");
        this.register(Region.MENU_BAR, "MenuBar");
        this.register(Region.MENU, "MenuBar:Menu");
        this.register(Region.MENU_ITEM_ACCELERATOR, "MenuBar:Menu:MenuItemAccelerator");
        this.register(Region.MENU_ITEM, "MenuItem");
        this.register(Region.MENU_ITEM_ACCELERATOR, "MenuItem:MenuItemAccelerator");
        this.register(Region.RADIO_BUTTON_MENU_ITEM, "RadioButtonMenuItem");
        this.register(Region.MENU_ITEM_ACCELERATOR, "RadioButtonMenuItem:MenuItemAccelerator");
        this.register(Region.CHECK_BOX_MENU_ITEM, "CheckBoxMenuItem");
        this.register(Region.MENU_ITEM_ACCELERATOR, "CheckBoxMenuItem:MenuItemAccelerator");
        this.register(Region.MENU, "Menu");
        this.register(Region.MENU_ITEM_ACCELERATOR, "Menu:MenuItemAccelerator");
        this.register(Region.POPUP_MENU, "PopupMenu");
        this.register(Region.POPUP_MENU_SEPARATOR, "PopupMenuSeparator");
        this.register(Region.OPTION_PANE, "OptionPane");
        this.register(Region.SEPARATOR, "OptionPane:\"OptionPane.separator\"");
        this.register(Region.PANEL, "OptionPane:\"OptionPane.messageArea\"");
        this.register(Region.LABEL, "OptionPane:\"OptionPane.messageArea\":\"OptionPane.label\"");
        this.register(Region.PANEL, "Panel");
        this.register(Region.PROGRESS_BAR, "ProgressBar");
        this.register(Region.SEPARATOR, "Separator");
        this.register(Region.SCROLL_BAR, "ScrollBar");
        this.register(Region.ARROW_BUTTON, "ScrollBar:\"ScrollBar.button\"");
        this.register(Region.SCROLL_BAR_THUMB, "ScrollBar:ScrollBarThumb");
        this.register(Region.SCROLL_BAR_TRACK, "ScrollBar:ScrollBarTrack");
        this.register(Region.SCROLL_PANE, "ScrollPane");
        this.register(Region.VIEWPORT, "Viewport");
        this.register(Region.SLIDER, "Slider");
        this.register(Region.SLIDER_THUMB, "Slider:SliderThumb");
        this.register(Region.SLIDER_TRACK, "Slider:SliderTrack");
        this.register(Region.SPINNER, "Spinner");
        this.register(Region.PANEL, "Spinner:\"Spinner.editor\"");
        this.register(Region.FORMATTED_TEXT_FIELD, "Spinner:Panel:\"Spinner.formattedTextField\"");
        this.register(Region.ARROW_BUTTON, "Spinner:\"Spinner.previousButton\"");
        this.register(Region.ARROW_BUTTON, "Spinner:\"Spinner.nextButton\"");
        this.register(Region.SPLIT_PANE, "SplitPane");
        this.register(Region.SPLIT_PANE_DIVIDER, "SplitPane:SplitPaneDivider");
        this.register(Region.TABBED_PANE, "TabbedPane");
        this.register(Region.TABBED_PANE_TAB, "TabbedPane:TabbedPaneTab");
        this.register(Region.TABBED_PANE_TAB_AREA, "TabbedPane:TabbedPaneTabArea");
        this.register(Region.TABBED_PANE_CONTENT, "TabbedPane:TabbedPaneContent");
        this.register(Region.TABLE, "Table");
        this.register(Region.LABEL, "Table:\"Table.cellRenderer\"");
        this.register(Region.TABLE_HEADER, "TableHeader");
        this.register(Region.LABEL, "TableHeader:\"TableHeader.renderer\"");
        this.register(Region.TEXT_FIELD, "\"Table.editor\"");
        this.register(Region.TEXT_FIELD, "\"Tree.cellEditor\"");
        this.register(Region.TEXT_FIELD, "TextField");
        this.register(Region.FORMATTED_TEXT_FIELD, "FormattedTextField");
        this.register(Region.PASSWORD_FIELD, "PasswordField");
        this.register(Region.TEXT_AREA, "TextArea");
        this.register(Region.TEXT_PANE, "TextPane");
        this.register(Region.EDITOR_PANE, "EditorPane");
        this.register(Region.TOOL_BAR, "ToolBar");
        this.register(Region.BUTTON, "ToolBar:Button");
        this.register(Region.TOGGLE_BUTTON, "ToolBar:ToggleButton");
        this.register(Region.TOOL_BAR_SEPARATOR, "ToolBarSeparator");
        this.register(Region.TOOL_TIP, "ToolTip");
        this.register(Region.TREE, "Tree");
        this.register(Region.TREE_CELL, "Tree:TreeCell");
        this.register(Region.LABEL, "Tree:\"Tree.cellRenderer\"");
        this.register(Region.ROOT_PANE, "RootPane");
    }
    
    void initializeDefaults(final UIDefaults uiDefaults) {
        this.addColor(uiDefaults, "text", 0, 0, 0, 255);
        this.addColor(uiDefaults, "control", 214, 217, 223, 255);
        this.addColor(uiDefaults, "nimbusBase", 51, 98, 140, 255);
        this.addColor(uiDefaults, "nimbusBlueGrey", "nimbusBase", 0.032459438f, -0.52518797f, 0.19607842f, 0);
        this.addColor(uiDefaults, "nimbusOrange", 191, 98, 4, 255);
        this.addColor(uiDefaults, "nimbusGreen", 176, 179, 50, 255);
        this.addColor(uiDefaults, "nimbusRed", 169, 46, 34, 255);
        this.addColor(uiDefaults, "nimbusBorder", "nimbusBlueGrey", 0.0f, -0.017358616f, -0.11372548f, 0);
        this.addColor(uiDefaults, "nimbusSelection", "nimbusBase", -0.010750473f, -0.04875779f, -0.007843137f, 0);
        this.addColor(uiDefaults, "nimbusInfoBlue", 47, 92, 180, 255);
        this.addColor(uiDefaults, "nimbusAlertYellow", 255, 220, 35, 255);
        this.addColor(uiDefaults, "nimbusFocus", 115, 164, 209, 255);
        this.addColor(uiDefaults, "nimbusSelectedText", 255, 255, 255, 255);
        this.addColor(uiDefaults, "nimbusSelectionBackground", 57, 105, 138, 255);
        this.addColor(uiDefaults, "nimbusDisabledText", 142, 143, 145, 255);
        this.addColor(uiDefaults, "nimbusLightBackground", 255, 255, 255, 255);
        this.addColor(uiDefaults, "infoText", "text", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "info", 242, 242, 189, 255);
        this.addColor(uiDefaults, "menuText", "text", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "menu", "nimbusBase", 0.021348298f, -0.6150531f, 0.39999998f, 0);
        this.addColor(uiDefaults, "scrollbar", "nimbusBlueGrey", -0.006944418f, -0.07296763f, 0.09019607f, 0);
        this.addColor(uiDefaults, "controlText", "text", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "controlHighlight", "nimbusBlueGrey", 0.0f, -0.07333623f, 0.20392156f, 0);
        this.addColor(uiDefaults, "controlLHighlight", "nimbusBlueGrey", 0.0f, -0.098526314f, 0.2352941f, 0);
        this.addColor(uiDefaults, "controlShadow", "nimbusBlueGrey", -0.0027777553f, -0.0212406f, 0.13333333f, 0);
        this.addColor(uiDefaults, "controlDkShadow", "nimbusBlueGrey", -0.0027777553f, -0.0018306673f, -0.02352941f, 0);
        this.addColor(uiDefaults, "textHighlight", "nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "textHighlightText", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "textInactiveText", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "desktop", "nimbusBase", -0.009207249f, -0.13984653f, -0.07450983f, 0);
        this.addColor(uiDefaults, "activeCaption", "nimbusBlueGrey", 0.0f, -0.049920253f, 0.031372547f, 0);
        this.addColor(uiDefaults, "inactiveCaption", "nimbusBlueGrey", -0.00505054f, -0.055526316f, 0.039215684f, 0);
        uiDefaults.put("defaultFont", new FontUIResource(this.defaultFont));
        uiDefaults.put("InternalFrame.titleFont", new DerivedFont("defaultFont", 1.0f, true, null));
        this.addColor(uiDefaults, "textForeground", "text", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "textBackground", "nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "background", "control", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("TitledBorder.position", "ABOVE_TOP");
        uiDefaults.put("FileView.fullRowSelection", Boolean.TRUE);
        uiDefaults.put("ArrowButton.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("ArrowButton.size", new Integer(16));
        uiDefaults.put("ArrowButton[Disabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ArrowButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(10, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("ArrowButton[Enabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ArrowButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(10, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Button.contentMargins", new InsetsUIResource(6, 14, 6, 14));
        uiDefaults.put("Button.defaultButtonFollowsFocus", Boolean.FALSE);
        uiDefaults.put("Button[Default].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 1, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("Button[Default+Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 2, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("Button[Default+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 3, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("Button[Default+Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 4, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        this.addColor(uiDefaults, "Button[Default+Pressed].textForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("Button[Default+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 5, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("Button[Default+Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 6, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        this.addColor(uiDefaults, "Button[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("Button[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 7, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("Button[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 8, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("Button[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 9, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("Button[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 10, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("Button[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 11, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("Button[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 12, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("Button[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ButtonPainter", 13, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ToggleButton.contentMargins", new InsetsUIResource(6, 14, 6, 14));
        this.addColor(uiDefaults, "ToggleButton[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("ToggleButton[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 1, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ToggleButton[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 2, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ToggleButton[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 3, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ToggleButton[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 4, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ToggleButton[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 5, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ToggleButton[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 6, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ToggleButton[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 7, new Insets(7, 7, 7, 7), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ToggleButton[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 8, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ToggleButton[Focused+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 9, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ToggleButton[Pressed+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 10, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ToggleButton[Focused+Pressed+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 11, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ToggleButton[MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 12, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ToggleButton[Focused+MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 13, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        this.addColor(uiDefaults, "ToggleButton[Disabled+Selected].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("ToggleButton[Disabled+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToggleButtonPainter", 14, new Insets(7, 7, 7, 7), new Dimension(72, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("RadioButton.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        this.addColor(uiDefaults, "RadioButton[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("RadioButton[Disabled].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 3, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButton[Enabled].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 4, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButton[Focused].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 5, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButton[MouseOver].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 6, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButton[Focused+MouseOver].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 7, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButton[Pressed].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 8, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButton[Focused+Pressed].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 9, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButton[Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 10, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButton[Focused+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 11, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButton[Pressed+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 12, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButton[Focused+Pressed+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 13, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButton[MouseOver+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 14, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButton[Focused+MouseOver+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 15, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButton[Disabled+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonPainter", 16, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButton.icon", new NimbusIcon("RadioButton", "iconPainter", 18, 18));
        uiDefaults.put("CheckBox.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        this.addColor(uiDefaults, "CheckBox[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("CheckBox[Disabled].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 3, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBox[Enabled].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 4, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBox[Focused].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 5, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBox[MouseOver].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 6, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBox[Focused+MouseOver].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 7, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBox[Pressed].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 8, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBox[Focused+Pressed].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 9, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBox[Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 10, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBox[Focused+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 11, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBox[Pressed+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 12, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBox[Focused+Pressed+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 13, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBox[MouseOver+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 14, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBox[Focused+MouseOver+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 15, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBox[Disabled+Selected].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxPainter", 16, new Insets(5, 5, 5, 5), new Dimension(18, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBox.icon", new NimbusIcon("CheckBox", "iconPainter", 18, 18));
        uiDefaults.put("ColorChooser.contentMargins", new InsetsUIResource(5, 0, 0, 0));
        this.addColor(uiDefaults, "ColorChooser.swatchesDefaultRecentColor", 255, 255, 255, 255);
        uiDefaults.put("ColorChooser:\"ColorChooser.previewPanelHolder\".contentMargins", new InsetsUIResource(0, 5, 10, 5));
        uiDefaults.put("ColorChooser:\"ColorChooser.previewPanelHolder\":\"OptionPane.label\".contentMargins", new InsetsUIResource(0, 10, 10, 10));
        uiDefaults.put("ComboBox.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("ComboBox.States", "Enabled,MouseOver,Pressed,Selected,Disabled,Focused,Editable");
        uiDefaults.put("ComboBox.Editable", new ComboBoxEditableState());
        uiDefaults.put("ComboBox.forceOpaque", Boolean.TRUE);
        uiDefaults.put("ComboBox.buttonWhenNotEditable", Boolean.TRUE);
        uiDefaults.put("ComboBox.rendererUseListColors", Boolean.FALSE);
        uiDefaults.put("ComboBox.pressedWhenPopupVisible", Boolean.TRUE);
        uiDefaults.put("ComboBox.squareButton", Boolean.FALSE);
        uiDefaults.put("ComboBox.popupInsets", new InsetsUIResource(-2, 2, 0, 2));
        uiDefaults.put("ComboBox.padding", new InsetsUIResource(3, 3, 3, 3));
        uiDefaults.put("ComboBox[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 1, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox[Disabled+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 2, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 3, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 4, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 5, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 6, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 7, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 8, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox[Enabled+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 9, new Insets(8, 9, 8, 19), new Dimension(83, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox[Disabled+Editable].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 10, new Insets(6, 5, 6, 17), new Dimension(79, 21), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox[Editable+Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 11, new Insets(6, 5, 6, 17), new Dimension(79, 21), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox[Editable+Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 12, new Insets(5, 5, 5, 5), new Dimension(142, 27), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox[Editable+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 13, new Insets(4, 5, 5, 17), new Dimension(79, 21), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox[Editable+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxPainter", 14, new Insets(4, 5, 5, 17), new Dimension(79, 21), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox:\"ComboBox.textField\".contentMargins", new InsetsUIResource(0, 6, 0, 3));
        this.addColor(uiDefaults, "ComboBox:\"ComboBox.textField\"[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("ComboBox:\"ComboBox.textField\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxTextFieldPainter", 1, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox:\"ComboBox.textField\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxTextFieldPainter", 2, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        this.addColor(uiDefaults, "ComboBox:\"ComboBox.textField\"[Selected].textForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("ComboBox:\"ComboBox.textField\"[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxTextFieldPainter", 3, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\".States", "Enabled,MouseOver,Pressed,Disabled,Editable");
        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\".Editable", new ComboBoxArrowButtonEditableState());
        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\".size", new Integer(19));
        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Disabled+Editable].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 5, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 6, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Editable+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 7, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 8, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Editable+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 9, new Insets(8, 1, 8, 8), new Dimension(20, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Enabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 10, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[MouseOver].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 11, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Disabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 12, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Pressed].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 13, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox:\"ComboBox.arrowButton\"[Selected].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ComboBoxArrowButtonPainter", 14, new Insets(6, 9, 6, 10), new Dimension(24, 19), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ComboBox:\"ComboBox.listRenderer\".contentMargins", new InsetsUIResource(2, 4, 2, 4));
        uiDefaults.put("ComboBox:\"ComboBox.listRenderer\".opaque", Boolean.TRUE);
        this.addColor(uiDefaults, "ComboBox:\"ComboBox.listRenderer\".background", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "ComboBox:\"ComboBox.listRenderer\"[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "ComboBox:\"ComboBox.listRenderer\"[Selected].textForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "ComboBox:\"ComboBox.listRenderer\"[Selected].background", "nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("ComboBox:\"ComboBox.renderer\".contentMargins", new InsetsUIResource(2, 4, 2, 4));
        this.addColor(uiDefaults, "ComboBox:\"ComboBox.renderer\"[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "ComboBox:\"ComboBox.renderer\"[Selected].textForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "ComboBox:\"ComboBox.renderer\"[Selected].background", "nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("\"ComboBox.scrollPane\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("FileChooser.contentMargins", new InsetsUIResource(10, 10, 10, 10));
        uiDefaults.put("FileChooser.opaque", Boolean.TRUE);
        uiDefaults.put("FileChooser.usesSingleFilePane", Boolean.TRUE);
        uiDefaults.put("FileChooser[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 1, new Insets(0, 0, 0, 0), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("FileChooser[Enabled].fileIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 2, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("FileChooser.fileIcon", new NimbusIcon("FileChooser", "fileIconPainter", 16, 16));
        uiDefaults.put("FileChooser[Enabled].directoryIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 3, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("FileChooser.directoryIcon", new NimbusIcon("FileChooser", "directoryIconPainter", 16, 16));
        uiDefaults.put("FileChooser[Enabled].upFolderIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 4, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("FileChooser.upFolderIcon", new NimbusIcon("FileChooser", "upFolderIconPainter", 16, 16));
        uiDefaults.put("FileChooser[Enabled].newFolderIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 5, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("FileChooser.newFolderIcon", new NimbusIcon("FileChooser", "newFolderIconPainter", 16, 16));
        uiDefaults.put("FileChooser[Enabled].hardDriveIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 7, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("FileChooser.hardDriveIcon", new NimbusIcon("FileChooser", "hardDriveIconPainter", 16, 16));
        uiDefaults.put("FileChooser[Enabled].floppyDriveIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 8, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("FileChooser.floppyDriveIcon", new NimbusIcon("FileChooser", "floppyDriveIconPainter", 16, 16));
        uiDefaults.put("FileChooser[Enabled].homeFolderIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 9, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("FileChooser.homeFolderIcon", new NimbusIcon("FileChooser", "homeFolderIconPainter", 16, 16));
        uiDefaults.put("FileChooser[Enabled].detailsViewIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 10, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("FileChooser.detailsViewIcon", new NimbusIcon("FileChooser", "detailsViewIconPainter", 16, 16));
        uiDefaults.put("FileChooser[Enabled].listViewIconPainter", new LazyPainter("javax.swing.plaf.nimbus.FileChooserPainter", 11, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("FileChooser.listViewIcon", new NimbusIcon("FileChooser", "listViewIconPainter", 16, 16));
        uiDefaults.put("InternalFrameTitlePane.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("InternalFrameTitlePane.maxFrameIconSize", new DimensionUIResource(18, 18));
        uiDefaults.put("InternalFrame.contentMargins", new InsetsUIResource(1, 6, 6, 6));
        uiDefaults.put("InternalFrame.States", "Enabled,WindowFocused");
        uiDefaults.put("InternalFrame.WindowFocused", new InternalFrameWindowFocusedState());
        uiDefaults.put("InternalFrame[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFramePainter", 1, new Insets(25, 6, 6, 6), new Dimension(25, 36), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame[Enabled+WindowFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFramePainter", 2, new Insets(25, 6, 6, 6), new Dimension(25, 36), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane.contentMargins", new InsetsUIResource(3, 0, 3, 0));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane.States", "Enabled,WindowFocused");
        uiDefaults.put("InternalFrame:InternalFrameTitlePane.WindowFocused", new InternalFrameTitlePaneWindowFocusedState());
        uiDefaults.put("InternalFrame:InternalFrameTitlePane.titleAlignment", "CENTER");
        this.addColor(uiDefaults, "InternalFrame:InternalFrameTitlePane[Enabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,WindowNotFocused");
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".WindowNotFocused", new InternalFrameTitlePaneMenuButtonWindowNotFocusedState());
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".test", "am InternalFrameTitlePane.menuButton");
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Enabled].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 1, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Disabled].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[MouseOver].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Pressed].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 4, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Enabled+WindowNotFocused].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 5, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[MouseOver+WindowNotFocused].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 6, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"[Pressed+WindowNotFocused].iconPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMenuButtonPainter", 7, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\".icon", new NimbusIcon("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"", "iconPainter", 19, 18));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\".contentMargins", new InsetsUIResource(9, 9, 9, 9));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,WindowNotFocused");
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\".WindowNotFocused", new InternalFrameTitlePaneIconifyButtonWindowNotFocusedState());
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 1, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 4, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Enabled+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 5, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[MouseOver+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 6, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"[Pressed+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneIconifyButtonPainter", 7, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\".contentMargins", new InsetsUIResource(9, 9, 9, 9));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,WindowNotFocused,WindowMaximized");
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\".WindowNotFocused", new InternalFrameTitlePaneMaximizeButtonWindowNotFocusedState());
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\".WindowMaximized", new InternalFrameTitlePaneMaximizeButtonWindowMaximizedState());
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Disabled+WindowMaximized].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 1, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled+WindowMaximized].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver+WindowMaximized].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed+WindowMaximized].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 4, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled+WindowMaximized+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 5, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver+WindowMaximized+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 6, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed+WindowMaximized+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 7, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 8, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 9, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 10, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 11, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Enabled+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 12, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[MouseOver+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 13, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"[Pressed+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneMaximizeButtonPainter", 14, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\".contentMargins", new InsetsUIResource(9, 9, 9, 9));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,WindowNotFocused");
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\".WindowNotFocused", new InternalFrameTitlePaneCloseButtonWindowNotFocusedState());
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 1, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 2, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 3, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 4, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Enabled+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 5, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[MouseOver+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 6, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"[Pressed+WindowNotFocused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.InternalFrameTitlePaneCloseButtonPainter", 7, new Insets(0, 0, 0, 0), new Dimension(19, 18), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("DesktopIcon.contentMargins", new InsetsUIResource(4, 6, 5, 4));
        uiDefaults.put("DesktopIcon[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.DesktopIconPainter", 1, new Insets(5, 5, 5, 5), new Dimension(28, 26), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("DesktopPane.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("DesktopPane.opaque", Boolean.TRUE);
        uiDefaults.put("DesktopPane[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.DesktopPanePainter", 1, new Insets(0, 0, 0, 0), new Dimension(300, 232), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("Label.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        this.addColor(uiDefaults, "Label[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("List.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("List.opaque", Boolean.TRUE);
        this.addColor(uiDefaults, "List.background", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("List.rendererUseListColors", Boolean.FALSE);
        uiDefaults.put("List.rendererUseUIBorder", Boolean.TRUE);
        uiDefaults.put("List.cellNoFocusBorder", new BorderUIResource(BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        uiDefaults.put("List.focusCellHighlightBorder", new BorderUIResource(new PainterBorder("Tree:TreeCell[Enabled+Focused].backgroundPainter", new Insets(2, 5, 2, 5))));
        this.addColor(uiDefaults, "List.dropLineColor", "nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "List[Selected].textForeground", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "List[Selected].textBackground", "nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "List[Disabled+Selected].textBackground", "nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "List[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("List:\"List.cellRenderer\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("List:\"List.cellRenderer\".opaque", Boolean.TRUE);
        this.addColor(uiDefaults, "List:\"List.cellRenderer\"[Selected].textForeground", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "List:\"List.cellRenderer\"[Selected].background", "nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "List:\"List.cellRenderer\"[Disabled+Selected].background", "nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "List:\"List.cellRenderer\"[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("MenuBar.contentMargins", new InsetsUIResource(2, 6, 2, 6));
        uiDefaults.put("MenuBar[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuBarPainter", 1, new Insets(1, 0, 0, 0), new Dimension(18, 22), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("MenuBar[Enabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuBarPainter", 2, new Insets(0, 0, 1, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("MenuBar:Menu.contentMargins", new InsetsUIResource(1, 4, 2, 4));
        this.addColor(uiDefaults, "MenuBar:Menu[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "MenuBar:Menu[Enabled].textForeground", 35, 35, 36, 255);
        this.addColor(uiDefaults, "MenuBar:Menu[Selected].textForeground", 255, 255, 255, 255);
        uiDefaults.put("MenuBar:Menu[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuBarMenuPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("MenuBar:Menu:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("MenuItem.contentMargins", new InsetsUIResource(1, 12, 2, 13));
        uiDefaults.put("MenuItem.textIconGap", new Integer(5));
        this.addColor(uiDefaults, "MenuItem[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "MenuItem[Enabled].textForeground", 35, 35, 36, 255);
        this.addColor(uiDefaults, "MenuItem[MouseOver].textForeground", 255, 255, 255, 255);
        uiDefaults.put("MenuItem[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuItemPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("MenuItem:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        this.addColor(uiDefaults, "MenuItem:MenuItemAccelerator[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "MenuItem:MenuItemAccelerator[MouseOver].textForeground", 255, 255, 255, 255);
        uiDefaults.put("RadioButtonMenuItem.contentMargins", new InsetsUIResource(1, 12, 2, 13));
        uiDefaults.put("RadioButtonMenuItem.textIconGap", new Integer(5));
        this.addColor(uiDefaults, "RadioButtonMenuItem[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "RadioButtonMenuItem[Enabled].textForeground", 35, 35, 36, 255);
        this.addColor(uiDefaults, "RadioButtonMenuItem[MouseOver].textForeground", 255, 255, 255, 255);
        uiDefaults.put("RadioButtonMenuItem[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        this.addColor(uiDefaults, "RadioButtonMenuItem[MouseOver+Selected].textForeground", 255, 255, 255, 255);
        uiDefaults.put("RadioButtonMenuItem[MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 4, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("RadioButtonMenuItem[Disabled+Selected].checkIconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 5, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButtonMenuItem[Enabled+Selected].checkIconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 6, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButtonMenuItem[MouseOver+Selected].checkIconPainter", new LazyPainter("javax.swing.plaf.nimbus.RadioButtonMenuItemPainter", 7, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("RadioButtonMenuItem.checkIcon", new NimbusIcon("RadioButtonMenuItem", "checkIconPainter", 9, 10));
        uiDefaults.put("RadioButtonMenuItem:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        this.addColor(uiDefaults, "RadioButtonMenuItem:MenuItemAccelerator[MouseOver].textForeground", 255, 255, 255, 255);
        uiDefaults.put("CheckBoxMenuItem.contentMargins", new InsetsUIResource(1, 12, 2, 13));
        uiDefaults.put("CheckBoxMenuItem.textIconGap", new Integer(5));
        this.addColor(uiDefaults, "CheckBoxMenuItem[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "CheckBoxMenuItem[Enabled].textForeground", 35, 35, 36, 255);
        this.addColor(uiDefaults, "CheckBoxMenuItem[MouseOver].textForeground", 255, 255, 255, 255);
        uiDefaults.put("CheckBoxMenuItem[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        this.addColor(uiDefaults, "CheckBoxMenuItem[MouseOver+Selected].textForeground", 255, 255, 255, 255);
        uiDefaults.put("CheckBoxMenuItem[MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 4, new Insets(0, 0, 0, 0), new Dimension(100, 3), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("CheckBoxMenuItem[Disabled+Selected].checkIconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 5, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBoxMenuItem[Enabled+Selected].checkIconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 6, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBoxMenuItem[MouseOver+Selected].checkIconPainter", new LazyPainter("javax.swing.plaf.nimbus.CheckBoxMenuItemPainter", 7, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("CheckBoxMenuItem.checkIcon", new NimbusIcon("CheckBoxMenuItem", "checkIconPainter", 9, 10));
        uiDefaults.put("CheckBoxMenuItem:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        this.addColor(uiDefaults, "CheckBoxMenuItem:MenuItemAccelerator[MouseOver].textForeground", 255, 255, 255, 255);
        uiDefaults.put("Menu.contentMargins", new InsetsUIResource(1, 12, 2, 5));
        uiDefaults.put("Menu.textIconGap", new Integer(5));
        this.addColor(uiDefaults, "Menu[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "Menu[Enabled].textForeground", 35, 35, 36, 255);
        this.addColor(uiDefaults, "Menu[Enabled+Selected].textForeground", 255, 255, 255, 255);
        uiDefaults.put("Menu[Enabled+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuPainter", 3, new Insets(0, 0, 0, 0), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("Menu[Disabled].arrowIconPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuPainter", 4, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Menu[Enabled].arrowIconPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuPainter", 5, new Insets(5, 5, 5, 5), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Menu[Enabled+Selected].arrowIconPainter", new LazyPainter("javax.swing.plaf.nimbus.MenuPainter", 6, new Insets(1, 1, 1, 1), new Dimension(9, 10), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Menu.arrowIcon", new NimbusIcon("Menu", "arrowIconPainter", 9, 10));
        uiDefaults.put("Menu:MenuItemAccelerator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        this.addColor(uiDefaults, "Menu:MenuItemAccelerator[MouseOver].textForeground", 255, 255, 255, 255);
        uiDefaults.put("PopupMenu.contentMargins", new InsetsUIResource(6, 1, 6, 1));
        uiDefaults.put("PopupMenu.opaque", Boolean.TRUE);
        uiDefaults.put("PopupMenu.consumeEventOnClose", Boolean.TRUE);
        uiDefaults.put("PopupMenu[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.PopupMenuPainter", 1, new Insets(9, 0, 11, 0), new Dimension(220, 313), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("PopupMenu[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.PopupMenuPainter", 2, new Insets(11, 2, 11, 2), new Dimension(220, 313), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("PopupMenuSeparator.contentMargins", new InsetsUIResource(1, 0, 2, 0));
        uiDefaults.put("PopupMenuSeparator[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.PopupMenuSeparatorPainter", 1, new Insets(1, 1, 1, 1), new Dimension(3, 3), true, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("OptionPane.contentMargins", new InsetsUIResource(15, 15, 15, 15));
        uiDefaults.put("OptionPane.opaque", Boolean.TRUE);
        uiDefaults.put("OptionPane.buttonOrientation", new Integer(4));
        uiDefaults.put("OptionPane.messageAnchor", new Integer(17));
        uiDefaults.put("OptionPane.separatorPadding", new Integer(0));
        uiDefaults.put("OptionPane.sameSizeButtons", Boolean.FALSE);
        uiDefaults.put("OptionPane:\"OptionPane.separator\".contentMargins", new InsetsUIResource(1, 0, 0, 0));
        uiDefaults.put("OptionPane:\"OptionPane.messageArea\".contentMargins", new InsetsUIResource(0, 0, 10, 0));
        uiDefaults.put("OptionPane:\"OptionPane.messageArea\":\"OptionPane.label\".contentMargins", new InsetsUIResource(0, 10, 10, 10));
        uiDefaults.put("OptionPane:\"OptionPane.messageArea\":\"OptionPane.label\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.OptionPaneMessageAreaOptionPaneLabelPainter", 1, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("OptionPane[Enabled].errorIconPainter", new LazyPainter("javax.swing.plaf.nimbus.OptionPanePainter", 2, new Insets(0, 0, 0, 0), new Dimension(48, 48), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("OptionPane.errorIcon", new NimbusIcon("OptionPane", "errorIconPainter", 48, 48));
        uiDefaults.put("OptionPane[Enabled].informationIconPainter", new LazyPainter("javax.swing.plaf.nimbus.OptionPanePainter", 3, new Insets(0, 0, 0, 0), new Dimension(48, 48), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("OptionPane.informationIcon", new NimbusIcon("OptionPane", "informationIconPainter", 48, 48));
        uiDefaults.put("OptionPane[Enabled].questionIconPainter", new LazyPainter("javax.swing.plaf.nimbus.OptionPanePainter", 4, new Insets(0, 0, 0, 0), new Dimension(48, 48), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("OptionPane.questionIcon", new NimbusIcon("OptionPane", "questionIconPainter", 48, 48));
        uiDefaults.put("OptionPane[Enabled].warningIconPainter", new LazyPainter("javax.swing.plaf.nimbus.OptionPanePainter", 5, new Insets(0, 0, 0, 0), new Dimension(48, 48), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("OptionPane.warningIcon", new NimbusIcon("OptionPane", "warningIconPainter", 48, 48));
        uiDefaults.put("Panel.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("Panel.opaque", Boolean.TRUE);
        uiDefaults.put("ProgressBar.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("ProgressBar.States", "Enabled,Disabled,Indeterminate,Finished");
        uiDefaults.put("ProgressBar.Indeterminate", new ProgressBarIndeterminateState());
        uiDefaults.put("ProgressBar.Finished", new ProgressBarFinishedState());
        uiDefaults.put("ProgressBar.tileWhenIndeterminate", Boolean.TRUE);
        uiDefaults.put("ProgressBar.tileWidth", new Integer(27));
        uiDefaults.put("ProgressBar.paintOutsideClip", Boolean.TRUE);
        uiDefaults.put("ProgressBar.rotateText", Boolean.TRUE);
        uiDefaults.put("ProgressBar.vertictalSize", new DimensionUIResource(19, 150));
        uiDefaults.put("ProgressBar.horizontalSize", new DimensionUIResource(150, 19));
        uiDefaults.put("ProgressBar.cycleTime", new Integer(250));
        uiDefaults.put("ProgressBar.minBarSize", new DimensionUIResource(6, 6));
        uiDefaults.put("ProgressBar.glowWidth", new Integer(2));
        uiDefaults.put("ProgressBar[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 1, new Insets(5, 5, 5, 5), new Dimension(29, 19), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        this.addColor(uiDefaults, "ProgressBar[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("ProgressBar[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 2, new Insets(5, 5, 5, 5), new Dimension(29, 19), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("ProgressBar[Enabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 3, new Insets(3, 3, 3, 3), new Dimension(27, 19), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ProgressBar[Enabled+Finished].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 4, new Insets(3, 3, 3, 3), new Dimension(27, 19), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ProgressBar[Enabled+Indeterminate].progressPadding", new Integer(3));
        uiDefaults.put("ProgressBar[Enabled+Indeterminate].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 5, new Insets(3, 3, 3, 3), new Dimension(30, 13), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ProgressBar[Disabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 6, new Insets(3, 3, 3, 3), new Dimension(27, 19), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ProgressBar[Disabled+Finished].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 7, new Insets(3, 3, 3, 3), new Dimension(27, 19), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ProgressBar[Disabled+Indeterminate].progressPadding", new Integer(3));
        uiDefaults.put("ProgressBar[Disabled+Indeterminate].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ProgressBarPainter", 8, new Insets(3, 3, 3, 3), new Dimension(30, 13), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("Separator.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("Separator[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SeparatorPainter", 1, new Insets(0, 40, 0, 40), new Dimension(100, 3), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("ScrollBar.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("ScrollBar.opaque", Boolean.TRUE);
        uiDefaults.put("ScrollBar.incrementButtonGap", new Integer(-8));
        uiDefaults.put("ScrollBar.decrementButtonGap", new Integer(-8));
        uiDefaults.put("ScrollBar.thumbHeight", new Integer(15));
        uiDefaults.put("ScrollBar.minimumThumbSize", new DimensionUIResource(29, 29));
        uiDefaults.put("ScrollBar.maximumThumbSize", new DimensionUIResource(1000, 1000));
        uiDefaults.put("ScrollBar:\"ScrollBar.button\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("ScrollBar:\"ScrollBar.button\".size", new Integer(25));
        uiDefaults.put("ScrollBar:\"ScrollBar.button\"[Enabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarButtonPainter", 1, new Insets(1, 1, 1, 1), new Dimension(25, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("ScrollBar:\"ScrollBar.button\"[Disabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarButtonPainter", 2, new Insets(1, 1, 1, 1), new Dimension(25, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("ScrollBar:\"ScrollBar.button\"[MouseOver].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarButtonPainter", 3, new Insets(1, 1, 1, 1), new Dimension(25, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("ScrollBar:\"ScrollBar.button\"[Pressed].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarButtonPainter", 4, new Insets(1, 1, 1, 1), new Dimension(25, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("ScrollBar:ScrollBarThumb.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("ScrollBar:ScrollBarThumb[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarThumbPainter", 2, new Insets(0, 15, 0, 15), new Dimension(38, 15), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ScrollBar:ScrollBarThumb[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarThumbPainter", 4, new Insets(0, 15, 0, 15), new Dimension(38, 15), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ScrollBar:ScrollBarThumb[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarThumbPainter", 5, new Insets(0, 15, 0, 15), new Dimension(38, 15), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ScrollBar:ScrollBarTrack.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("ScrollBar:ScrollBarTrack[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarTrackPainter", 1, new Insets(5, 5, 5, 5), new Dimension(18, 15), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("ScrollBar:ScrollBarTrack[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollBarTrackPainter", 2, new Insets(5, 10, 5, 9), new Dimension(34, 15), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("ScrollPane.contentMargins", new InsetsUIResource(3, 3, 3, 3));
        uiDefaults.put("ScrollPane.useChildTextComponentFocus", Boolean.TRUE);
        uiDefaults.put("ScrollPane[Enabled+Focused].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollPanePainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("ScrollPane[Enabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.ScrollPanePainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("Viewport.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("Viewport.opaque", Boolean.TRUE);
        uiDefaults.put("Slider.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("Slider.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,ArrowShape");
        uiDefaults.put("Slider.ArrowShape", new SliderArrowShapeState());
        uiDefaults.put("Slider.thumbWidth", new Integer(17));
        uiDefaults.put("Slider.thumbHeight", new Integer(17));
        uiDefaults.put("Slider.trackBorder", new Integer(0));
        uiDefaults.put("Slider.paintValue", Boolean.FALSE);
        this.addColor(uiDefaults, "Slider.tickColor", 35, 40, 48, 255);
        uiDefaults.put("Slider:SliderThumb.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("Slider:SliderThumb.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,ArrowShape");
        uiDefaults.put("Slider:SliderThumb.ArrowShape", new SliderThumbArrowShapeState());
        uiDefaults.put("Slider:SliderThumb[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 1, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Slider:SliderThumb[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 2, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Slider:SliderThumb[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 3, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Slider:SliderThumb[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 4, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Slider:SliderThumb[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 5, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Slider:SliderThumb[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 6, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Slider:SliderThumb[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 7, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Slider:SliderThumb[ArrowShape+Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 8, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Slider:SliderThumb[ArrowShape+Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 9, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Slider:SliderThumb[ArrowShape+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 10, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Slider:SliderThumb[ArrowShape+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 11, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Slider:SliderThumb[ArrowShape+Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 12, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Slider:SliderThumb[ArrowShape+Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 13, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Slider:SliderThumb[ArrowShape+Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderThumbPainter", 14, new Insets(5, 5, 5, 5), new Dimension(17, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Slider:SliderTrack.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("Slider:SliderTrack.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,ArrowShape");
        uiDefaults.put("Slider:SliderTrack.ArrowShape", new SliderTrackArrowShapeState());
        uiDefaults.put("Slider:SliderTrack[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderTrackPainter", 1, new Insets(6, 5, 6, 5), new Dimension(23, 17), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, 2.0));
        uiDefaults.put("Slider:SliderTrack[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SliderTrackPainter", 2, new Insets(6, 5, 6, 5), new Dimension(23, 17), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("Spinner:\"Spinner.editor\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\".contentMargins", new InsetsUIResource(6, 6, 5, 6));
        this.addColor(uiDefaults, "Spinner:Panel:\"Spinner.formattedTextField\"[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 1, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 2, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 3, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        this.addColor(uiDefaults, "Spinner:Panel:\"Spinner.formattedTextField\"[Selected].textForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 4, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        this.addColor(uiDefaults, "Spinner:Panel:\"Spinner.formattedTextField\"[Focused+Selected].textForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("Spinner:Panel:\"Spinner.formattedTextField\"[Focused+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPanelSpinnerFormattedTextFieldPainter", 5, new Insets(5, 3, 3, 1), new Dimension(64, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("Spinner:\"Spinner.previousButton\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("Spinner:\"Spinner.previousButton\".size", new Integer(20));
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 1, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 2, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 3, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 4, new Insets(3, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 5, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 6, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 7, new Insets(0, 1, 6, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Disabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 8, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Enabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 9, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Focused].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 10, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Focused+MouseOver].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 11, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Focused+Pressed].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 12, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[MouseOver].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 13, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.previousButton\"[Pressed].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerPreviousButtonPainter", 14, new Insets(3, 6, 5, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\".size", new Integer(20));
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 1, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 2, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 3, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 4, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 5, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 6, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 7, new Insets(7, 1, 1, 7), new Dimension(20, 12), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Disabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 8, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Enabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 9, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Focused].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 10, new Insets(3, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Focused+MouseOver].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 11, new Insets(3, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Focused+Pressed].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 12, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[MouseOver].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 13, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Spinner:\"Spinner.nextButton\"[Pressed].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SpinnerNextButtonPainter", 14, new Insets(5, 6, 3, 9), new Dimension(20, 12), true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("SplitPane.contentMargins", new InsetsUIResource(1, 1, 1, 1));
        uiDefaults.put("SplitPane.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,Vertical");
        uiDefaults.put("SplitPane.Vertical", new SplitPaneVerticalState());
        uiDefaults.put("SplitPane.size", new Integer(10));
        uiDefaults.put("SplitPane.dividerSize", new Integer(10));
        uiDefaults.put("SplitPane.centerOneTouchButtons", Boolean.TRUE);
        uiDefaults.put("SplitPane.oneTouchButtonOffset", new Integer(30));
        uiDefaults.put("SplitPane.oneTouchExpandable", Boolean.FALSE);
        uiDefaults.put("SplitPane.continuousLayout", Boolean.TRUE);
        uiDefaults.put("SplitPane:SplitPaneDivider.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("SplitPane:SplitPaneDivider.States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,Vertical");
        uiDefaults.put("SplitPane:SplitPaneDivider.Vertical", new SplitPaneDividerVerticalState());
        uiDefaults.put("SplitPane:SplitPaneDivider[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SplitPaneDividerPainter", 1, new Insets(3, 0, 3, 0), new Dimension(68, 10), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("SplitPane:SplitPaneDivider[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SplitPaneDividerPainter", 2, new Insets(3, 0, 3, 0), new Dimension(68, 10), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("SplitPane:SplitPaneDivider[Enabled].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SplitPaneDividerPainter", 3, new Insets(0, 24, 0, 24), new Dimension(68, 10), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("SplitPane:SplitPaneDivider[Enabled+Vertical].foregroundPainter", new LazyPainter("javax.swing.plaf.nimbus.SplitPaneDividerPainter", 4, new Insets(5, 0, 5, 0), new Dimension(10, 38), true, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TabbedPane.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("TabbedPane.tabAreaStatesMatchSelectedTab", Boolean.TRUE);
        uiDefaults.put("TabbedPane.nudgeSelectedLabel", Boolean.FALSE);
        uiDefaults.put("TabbedPane.tabRunOverlay", new Integer(2));
        uiDefaults.put("TabbedPane.tabOverlap", new Integer(-1));
        uiDefaults.put("TabbedPane.extendTabsToBase", Boolean.TRUE);
        uiDefaults.put("TabbedPane.useBasicArrows", Boolean.TRUE);
        this.addColor(uiDefaults, "TabbedPane.shadow", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "TabbedPane.darkShadow", "text", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "TabbedPane.highlight", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("TabbedPane:TabbedPaneTab.contentMargins", new InsetsUIResource(2, 8, 3, 8));
        uiDefaults.put("TabbedPane:TabbedPaneTab[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 1, new Insets(7, 7, 1, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TabbedPane:TabbedPaneTab[Enabled+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 2, new Insets(7, 7, 1, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TabbedPane:TabbedPaneTab[Enabled+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 3, new Insets(7, 6, 1, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        this.addColor(uiDefaults, "TabbedPane:TabbedPaneTab[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("TabbedPane:TabbedPaneTab[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 4, new Insets(6, 7, 1, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TabbedPane:TabbedPaneTab[Disabled+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 5, new Insets(7, 7, 0, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TabbedPane:TabbedPaneTab[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 6, new Insets(7, 7, 0, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TabbedPane:TabbedPaneTab[MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 7, new Insets(7, 9, 0, 9), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        this.addColor(uiDefaults, "TabbedPane:TabbedPaneTab[Pressed+Selected].textForeground", 255, 255, 255, 255);
        uiDefaults.put("TabbedPane:TabbedPaneTab[Pressed+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 8, new Insets(7, 9, 0, 9), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TabbedPane:TabbedPaneTab[Focused+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 9, new Insets(7, 7, 3, 7), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TabbedPane:TabbedPaneTab[Focused+MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 10, new Insets(7, 9, 3, 9), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        this.addColor(uiDefaults, "TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].textForeground", 255, 255, 255, 255);
        uiDefaults.put("TabbedPane:TabbedPaneTab[Focused+Pressed+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabPainter", 11, new Insets(7, 9, 3, 9), new Dimension(44, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TabbedPane:TabbedPaneTabArea.contentMargins", new InsetsUIResource(3, 10, 4, 10));
        uiDefaults.put("TabbedPane:TabbedPaneTabArea[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabAreaPainter", 1, new Insets(0, 5, 6, 5), new Dimension(5, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabAreaPainter", 2, new Insets(0, 5, 6, 5), new Dimension(5, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TabbedPane:TabbedPaneTabArea[Enabled+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabAreaPainter", 3, new Insets(0, 5, 6, 5), new Dimension(5, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TabbedPane:TabbedPaneTabArea[Enabled+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TabbedPaneTabAreaPainter", 4, new Insets(0, 5, 6, 5), new Dimension(5, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TabbedPane:TabbedPaneContent.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("Table.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("Table.opaque", Boolean.TRUE);
        this.addColor(uiDefaults, "Table.textForeground", 35, 35, 36, 255);
        this.addColor(uiDefaults, "Table.background", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("Table.showGrid", Boolean.FALSE);
        uiDefaults.put("Table.intercellSpacing", new DimensionUIResource(0, 0));
        this.addColor(uiDefaults, "Table.alternateRowColor", "nimbusLightBackground", 0.0f, 0.0f, -0.05098039f, 0, false);
        uiDefaults.put("Table.rendererUseTableColors", Boolean.TRUE);
        uiDefaults.put("Table.rendererUseUIBorder", Boolean.TRUE);
        uiDefaults.put("Table.cellNoFocusBorder", new BorderUIResource(BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        uiDefaults.put("Table.focusCellHighlightBorder", new BorderUIResource(new PainterBorder("Tree:TreeCell[Enabled+Focused].backgroundPainter", new Insets(2, 5, 2, 5))));
        this.addColor(uiDefaults, "Table.dropLineColor", "nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "Table.dropLineShortColor", "nimbusOrange", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "Table[Enabled+Selected].textForeground", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0, false);
        this.addColor(uiDefaults, "Table[Enabled+Selected].textBackground", "nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0, false);
        this.addColor(uiDefaults, "Table[Disabled+Selected].textBackground", "nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0, false);
        uiDefaults.put("Table:\"Table.cellRenderer\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("Table:\"Table.cellRenderer\".opaque", Boolean.TRUE);
        this.addColor(uiDefaults, "Table:\"Table.cellRenderer\".background", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0, false);
        uiDefaults.put("TableHeader.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("TableHeader.opaque", Boolean.TRUE);
        uiDefaults.put("TableHeader.rightAlignSortArrow", Boolean.TRUE);
        uiDefaults.put("TableHeader[Enabled].ascendingSortIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderPainter", 1, new Insets(0, 0, 0, 2), new Dimension(7, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Table.ascendingSortIcon", new NimbusIcon("TableHeader", "ascendingSortIconPainter", 7, 7));
        uiDefaults.put("TableHeader[Enabled].descendingSortIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderPainter", 2, new Insets(0, 0, 0, 0), new Dimension(7, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Table.descendingSortIcon", new NimbusIcon("TableHeader", "descendingSortIconPainter", 7, 7));
        uiDefaults.put("TableHeader:\"TableHeader.renderer\".contentMargins", new InsetsUIResource(2, 5, 4, 5));
        uiDefaults.put("TableHeader:\"TableHeader.renderer\".opaque", Boolean.TRUE);
        uiDefaults.put("TableHeader:\"TableHeader.renderer\".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,Sorted");
        uiDefaults.put("TableHeader:\"TableHeader.renderer\".Sorted", new TableHeaderRendererSortedState());
        uiDefaults.put("TableHeader:\"TableHeader.renderer\"[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 1, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 2, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 3, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TableHeader:\"TableHeader.renderer\"[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 4, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TableHeader:\"TableHeader.renderer\"[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 5, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Sorted].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 6, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TableHeader:\"TableHeader.renderer\"[Enabled+Focused+Sorted].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 7, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TableHeader:\"TableHeader.renderer\"[Disabled+Sorted].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableHeaderRendererPainter", 8, new Insets(5, 5, 5, 5), new Dimension(22, 20), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("\"Table.editor\".contentMargins", new InsetsUIResource(3, 5, 3, 5));
        uiDefaults.put("\"Table.editor\".opaque", Boolean.TRUE);
        this.addColor(uiDefaults, "\"Table.editor\".background", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "\"Table.editor\"[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("\"Table.editor\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableEditorPainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("\"Table.editor\"[Enabled+Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TableEditorPainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        this.addColor(uiDefaults, "\"Table.editor\"[Selected].textForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("\"Tree.cellEditor\".contentMargins", new InsetsUIResource(2, 5, 2, 5));
        uiDefaults.put("\"Tree.cellEditor\".opaque", Boolean.TRUE);
        this.addColor(uiDefaults, "\"Tree.cellEditor\".background", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "\"Tree.cellEditor\"[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("\"Tree.cellEditor\"[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TreeCellEditorPainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("\"Tree.cellEditor\"[Enabled+Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TreeCellEditorPainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        this.addColor(uiDefaults, "\"Tree.cellEditor\"[Selected].textForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("TextField.contentMargins", new InsetsUIResource(6, 6, 6, 6));
        this.addColor(uiDefaults, "TextField.background", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "TextField[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("TextField[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 1, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TextField[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        this.addColor(uiDefaults, "TextField[Selected].textForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("TextField[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        this.addColor(uiDefaults, "TextField[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("TextField[Disabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 4, new Insets(5, 3, 3, 3), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TextField[Focused].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 5, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TextField[Enabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.TextFieldPainter", 6, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("FormattedTextField.contentMargins", new InsetsUIResource(6, 6, 6, 6));
        this.addColor(uiDefaults, "FormattedTextField[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("FormattedTextField[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 1, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("FormattedTextField[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        this.addColor(uiDefaults, "FormattedTextField[Selected].textForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("FormattedTextField[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        this.addColor(uiDefaults, "FormattedTextField[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("FormattedTextField[Disabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 4, new Insets(5, 3, 3, 3), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("FormattedTextField[Focused].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 5, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("FormattedTextField[Enabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.FormattedTextFieldPainter", 6, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("PasswordField.contentMargins", new InsetsUIResource(6, 6, 6, 6));
        this.addColor(uiDefaults, "PasswordField[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("PasswordField[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 1, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("PasswordField[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        this.addColor(uiDefaults, "PasswordField[Selected].textForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("PasswordField[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        this.addColor(uiDefaults, "PasswordField[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("PasswordField[Disabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 4, new Insets(5, 3, 3, 3), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("PasswordField[Focused].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 5, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("PasswordField[Enabled].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.PasswordFieldPainter", 6, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TextArea.contentMargins", new InsetsUIResource(6, 6, 6, 6));
        uiDefaults.put("TextArea.States", "Enabled,MouseOver,Pressed,Selected,Disabled,Focused,NotInScrollPane");
        uiDefaults.put("TextArea.NotInScrollPane", new TextAreaNotInScrollPaneState());
        this.addColor(uiDefaults, "TextArea[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("TextArea[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 1, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("TextArea[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 2, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        this.addColor(uiDefaults, "TextArea[Disabled+NotInScrollPane].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("TextArea[Disabled+NotInScrollPane].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 3, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("TextArea[Enabled+NotInScrollPane].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 4, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        this.addColor(uiDefaults, "TextArea[Selected].textForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("TextArea[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 5, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        this.addColor(uiDefaults, "TextArea[Disabled+NotInScrollPane].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("TextArea[Disabled+NotInScrollPane].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 6, new Insets(5, 3, 3, 3), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TextArea[Focused+NotInScrollPane].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 7, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TextArea[Enabled+NotInScrollPane].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.TextAreaPainter", 8, new Insets(5, 5, 5, 5), new Dimension(122, 24), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        uiDefaults.put("TextPane.contentMargins", new InsetsUIResource(4, 6, 4, 6));
        uiDefaults.put("TextPane.opaque", Boolean.TRUE);
        this.addColor(uiDefaults, "TextPane[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("TextPane[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextPanePainter", 1, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("TextPane[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextPanePainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        this.addColor(uiDefaults, "TextPane[Selected].textForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("TextPane[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TextPanePainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("EditorPane.contentMargins", new InsetsUIResource(4, 6, 4, 6));
        uiDefaults.put("EditorPane.opaque", Boolean.TRUE);
        this.addColor(uiDefaults, "EditorPane[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("EditorPane[Disabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.EditorPanePainter", 1, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("EditorPane[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.EditorPanePainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        this.addColor(uiDefaults, "EditorPane[Selected].textForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("EditorPane[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.EditorPanePainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("ToolBar.contentMargins", new InsetsUIResource(2, 2, 2, 2));
        uiDefaults.put("ToolBar.opaque", Boolean.TRUE);
        uiDefaults.put("ToolBar.States", "North,East,West,South");
        uiDefaults.put("ToolBar.North", new ToolBarNorthState());
        uiDefaults.put("ToolBar.East", new ToolBarEastState());
        uiDefaults.put("ToolBar.West", new ToolBarWestState());
        uiDefaults.put("ToolBar.South", new ToolBarSouthState());
        uiDefaults.put("ToolBar[North].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 1, new Insets(0, 0, 1, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("ToolBar[South].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 2, new Insets(1, 0, 0, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("ToolBar[East].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 3, new Insets(1, 0, 0, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("ToolBar[West].borderPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 4, new Insets(0, 0, 1, 0), new Dimension(30, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("ToolBar[Enabled].handleIconPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarPainter", 5, new Insets(5, 5, 5, 5), new Dimension(11, 38), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar.handleIcon", new NimbusIcon("ToolBar", "handleIconPainter", 11, 38));
        uiDefaults.put("ToolBar:Button.contentMargins", new InsetsUIResource(4, 4, 4, 4));
        uiDefaults.put("ToolBar:Button[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 2, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:Button[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 3, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:Button[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 4, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:Button[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 5, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:Button[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarButtonPainter", 6, new Insets(5, 5, 5, 5), new Dimension(104, 33), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:ToggleButton.contentMargins", new InsetsUIResource(4, 4, 4, 4));
        uiDefaults.put("ToolBar:ToggleButton[Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 2, new Insets(5, 5, 5, 5), new Dimension(104, 34), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:ToggleButton[MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 3, new Insets(5, 5, 5, 5), new Dimension(104, 34), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:ToggleButton[Focused+MouseOver].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 4, new Insets(5, 5, 5, 5), new Dimension(104, 34), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:ToggleButton[Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 5, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:ToggleButton[Focused+Pressed].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 6, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:ToggleButton[Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 7, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:ToggleButton[Focused+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 8, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:ToggleButton[Pressed+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 9, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:ToggleButton[Focused+Pressed+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 10, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:ToggleButton[MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 11, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBar:ToggleButton[Focused+MouseOver+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 12, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        this.addColor(uiDefaults, "ToolBar:ToggleButton[Disabled+Selected].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("ToolBar:ToggleButton[Disabled+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolBarToggleButtonPainter", 13, new Insets(5, 5, 5, 5), new Dimension(72, 25), false, AbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE, 2.0, Double.POSITIVE_INFINITY));
        uiDefaults.put("ToolBarSeparator.contentMargins", new InsetsUIResource(2, 0, 3, 0));
        this.addColor(uiDefaults, "ToolBarSeparator.textForeground", "nimbusBorder", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("ToolTip.contentMargins", new InsetsUIResource(4, 4, 4, 4));
        uiDefaults.put("ToolTip[Enabled].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.ToolTipPainter", 1, new Insets(1, 1, 1, 1), new Dimension(10, 10), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("Tree.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("Tree.opaque", Boolean.TRUE);
        this.addColor(uiDefaults, "Tree.textForeground", "text", 0.0f, 0.0f, 0.0f, 0, false);
        this.addColor(uiDefaults, "Tree.textBackground", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0, false);
        this.addColor(uiDefaults, "Tree.background", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("Tree.rendererFillBackground", Boolean.FALSE);
        uiDefaults.put("Tree.leftChildIndent", new Integer(12));
        uiDefaults.put("Tree.rightChildIndent", new Integer(4));
        uiDefaults.put("Tree.drawHorizontalLines", Boolean.FALSE);
        uiDefaults.put("Tree.drawVerticalLines", Boolean.FALSE);
        uiDefaults.put("Tree.showRootHandles", Boolean.FALSE);
        uiDefaults.put("Tree.rendererUseTreeColors", Boolean.TRUE);
        uiDefaults.put("Tree.repaintWholeRow", Boolean.TRUE);
        uiDefaults.put("Tree.rowHeight", new Integer(0));
        uiDefaults.put("Tree.rendererMargins", new InsetsUIResource(2, 0, 1, 5));
        this.addColor(uiDefaults, "Tree.selectionForeground", "nimbusSelectedText", 0.0f, 0.0f, 0.0f, 0, false);
        this.addColor(uiDefaults, "Tree.selectionBackground", "nimbusSelectionBackground", 0.0f, 0.0f, 0.0f, 0, false);
        this.addColor(uiDefaults, "Tree.dropLineColor", "nimbusFocus", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("Tree:TreeCell.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        this.addColor(uiDefaults, "Tree:TreeCell[Enabled].background", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0);
        this.addColor(uiDefaults, "Tree:TreeCell[Enabled+Focused].background", "nimbusLightBackground", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("Tree:TreeCell[Enabled+Focused].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TreeCellPainter", 2, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        this.addColor(uiDefaults, "Tree:TreeCell[Enabled+Selected].textForeground", 255, 255, 255, 255);
        uiDefaults.put("Tree:TreeCell[Enabled+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TreeCellPainter", 3, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        this.addColor(uiDefaults, "Tree:TreeCell[Focused+Selected].textForeground", 255, 255, 255, 255);
        uiDefaults.put("Tree:TreeCell[Focused+Selected].backgroundPainter", new LazyPainter("javax.swing.plaf.nimbus.TreeCellPainter", 4, new Insets(5, 5, 5, 5), new Dimension(100, 30), false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0));
        uiDefaults.put("Tree:\"Tree.cellRenderer\".contentMargins", new InsetsUIResource(0, 0, 0, 0));
        this.addColor(uiDefaults, "Tree:\"Tree.cellRenderer\"[Disabled].textForeground", "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        uiDefaults.put("Tree[Enabled].leafIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TreePainter", 4, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Tree.leafIcon", new NimbusIcon("Tree", "leafIconPainter", 16, 16));
        uiDefaults.put("Tree[Enabled].closedIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TreePainter", 5, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Tree.closedIcon", new NimbusIcon("Tree", "closedIconPainter", 16, 16));
        uiDefaults.put("Tree[Enabled].openIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TreePainter", 6, new Insets(5, 5, 5, 5), new Dimension(16, 16), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Tree.openIcon", new NimbusIcon("Tree", "openIconPainter", 16, 16));
        uiDefaults.put("Tree[Enabled].collapsedIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TreePainter", 7, new Insets(5, 5, 5, 5), new Dimension(18, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Tree[Enabled+Selected].collapsedIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TreePainter", 8, new Insets(5, 5, 5, 5), new Dimension(18, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Tree.collapsedIcon", new NimbusIcon("Tree", "collapsedIconPainter", 18, 7));
        uiDefaults.put("Tree[Enabled].expandedIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TreePainter", 9, new Insets(5, 5, 5, 5), new Dimension(18, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Tree[Enabled+Selected].expandedIconPainter", new LazyPainter("javax.swing.plaf.nimbus.TreePainter", 10, new Insets(5, 5, 5, 5), new Dimension(18, 7), false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0));
        uiDefaults.put("Tree.expandedIcon", new NimbusIcon("Tree", "expandedIconPainter", 18, 7));
        uiDefaults.put("RootPane.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        uiDefaults.put("RootPane.opaque", Boolean.TRUE);
        this.addColor(uiDefaults, "RootPane.background", "control", 0.0f, 0.0f, 0.0f, 0);
    }
    
    void register(final Region region, final String s) {
        if (region == null || s == null) {
            throw new IllegalArgumentException("Neither Region nor Prefix may be null");
        }
        final List list = this.m.get(region);
        if (list == null) {
            final LinkedList list2 = new LinkedList();
            list2.add(new LazyStyle(s));
            this.m.put(region, list2);
        }
        else {
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                if (s.equals(((LazyStyle)iterator.next()).prefix)) {
                    return;
                }
            }
            list.add(new LazyStyle(s));
        }
        this.registeredRegions.put(region.getName(), region);
    }
    
    SynthStyle getStyle(final JComponent component, final Region region) {
        if (component == null || region == null) {
            throw new IllegalArgumentException("Neither comp nor r may be null");
        }
        final List list = this.m.get(region);
        if (list == null || list.size() == 0) {
            return this.defaultStyle;
        }
        LazyStyle lazyStyle = null;
        for (final LazyStyle lazyStyle2 : list) {
            if (lazyStyle2.matches(component) && (lazyStyle == null || lazyStyle.parts.length < lazyStyle2.parts.length || (lazyStyle.parts.length == lazyStyle2.parts.length && lazyStyle.simple && !lazyStyle2.simple))) {
                lazyStyle = lazyStyle2;
            }
        }
        return (lazyStyle == null) ? this.defaultStyle : lazyStyle.getStyle(component, region);
    }
    
    public void clearOverridesCache(final JComponent component) {
        this.overridesCache.remove(component);
    }
    
    private void addColor(final UIDefaults uiDefaults, final String s, final int n, final int n2, final int n3, final int n4) {
        final ColorUIResource colorUIResource = new ColorUIResource(new Color(n, n2, n3, n4));
        this.colorTree.addColor(s, colorUIResource);
        uiDefaults.put(s, colorUIResource);
    }
    
    private void addColor(final UIDefaults uiDefaults, final String s, final String s2, final float n, final float n2, final float n3, final int n4) {
        this.addColor(uiDefaults, s, s2, n, n2, n3, n4, true);
    }
    
    private void addColor(final UIDefaults uiDefaults, final String s, final String s2, final float n, final float n2, final float n3, final int n4, final boolean b) {
        uiDefaults.put(s, this.getDerivedColor(s, s2, n, n2, n3, n4, b));
    }
    
    public DerivedColor getDerivedColor(final String s, final float n, final float n2, final float n3, final int n4, final boolean b) {
        return this.getDerivedColor(null, s, n, n2, n3, n4, b);
    }
    
    private DerivedColor getDerivedColor(final String s, final String s2, final float n, final float n2, final float n3, final int n4, final boolean b) {
        DerivedColor derivedColor;
        if (b) {
            derivedColor = new DerivedColor.UIResource(s2, n, n2, n3, n4);
        }
        else {
            derivedColor = new DerivedColor(s2, n, n2, n3, n4);
        }
        if (this.derivedColors.containsKey(derivedColor)) {
            return this.derivedColors.get(derivedColor);
        }
        this.derivedColors.put(derivedColor, derivedColor);
        derivedColor.rederiveColor();
        this.colorTree.addColor(s, derivedColor);
        return derivedColor;
    }
    
    static final class DerivedFont implements UIDefaults.ActiveValue
    {
        private float sizeOffset;
        private Boolean bold;
        private Boolean italic;
        private String parentKey;
        
        public DerivedFont(final String parentKey, final float sizeOffset, final Boolean bold, final Boolean italic) {
            if (parentKey == null) {
                throw new IllegalArgumentException("You must specify a key");
            }
            this.parentKey = parentKey;
            this.sizeOffset = sizeOffset;
            this.bold = bold;
            this.italic = italic;
        }
        
        @Override
        public Object createValue(final UIDefaults uiDefaults) {
            final Font font = uiDefaults.getFont(this.parentKey);
            if (font != null) {
                final float n = (float)Math.round(font.getSize2D() * this.sizeOffset);
                int style = font.getStyle();
                if (this.bold != null) {
                    if (this.bold) {
                        style |= 0x1;
                    }
                    else {
                        style &= 0xFFFFFFFE;
                    }
                }
                if (this.italic != null) {
                    if (this.italic) {
                        style |= 0x2;
                    }
                    else {
                        style &= 0xFFFFFFFD;
                    }
                }
                return font.deriveFont(style, n);
            }
            return null;
        }
    }
    
    private static final class LazyPainter implements UIDefaults.LazyValue
    {
        private int which;
        private AbstractRegionPainter.PaintContext ctx;
        private String className;
        
        LazyPainter(final String className, final int which, final Insets insets, final Dimension dimension, final boolean b) {
            if (className == null) {
                throw new IllegalArgumentException("The className must be specified");
            }
            this.className = className;
            this.which = which;
            this.ctx = new AbstractRegionPainter.PaintContext(insets, dimension, b);
        }
        
        LazyPainter(final String className, final int which, final Insets insets, final Dimension dimension, final boolean b, final AbstractRegionPainter.PaintContext.CacheMode cacheMode, final double n, final double n2) {
            if (className == null) {
                throw new IllegalArgumentException("The className must be specified");
            }
            this.className = className;
            this.which = which;
            this.ctx = new AbstractRegionPainter.PaintContext(insets, dimension, b, cacheMode, n, n2);
        }
        
        @Override
        public Object createValue(final UIDefaults uiDefaults) {
            try {
                Object o;
                if (uiDefaults == null || !((o = uiDefaults.get("ClassLoader")) instanceof ClassLoader)) {
                    o = Thread.currentThread().getContextClassLoader();
                    if (o == null) {
                        o = ClassLoader.getSystemClassLoader();
                    }
                }
                final Constructor<?> constructor = Class.forName(this.className, true, (ClassLoader)o).getConstructor(AbstractRegionPainter.PaintContext.class, Integer.TYPE);
                if (constructor == null) {
                    throw new NullPointerException("Failed to find the constructor for the class: " + this.className);
                }
                return constructor.newInstance(this.ctx, this.which);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
    
    private final class LazyStyle
    {
        private String prefix;
        private boolean simple;
        private Part[] parts;
        private NimbusStyle style;
        
        private LazyStyle(final String prefix) {
            this.simple = true;
            if (prefix == null) {
                throw new IllegalArgumentException("The prefix must not be null");
            }
            this.prefix = prefix;
            String substring = prefix;
            if (substring.endsWith("cellRenderer\"") || substring.endsWith("renderer\"") || substring.endsWith("listRenderer\"")) {
                substring = substring.substring(substring.lastIndexOf(":\"") + 1);
            }
            final List<String> split = this.split(substring);
            this.parts = new Part[split.size()];
            for (int i = 0; i < this.parts.length; ++i) {
                this.parts[i] = new Part((String)split.get(i));
                if (this.parts[i].named) {
                    this.simple = false;
                }
            }
        }
        
        SynthStyle getStyle(final JComponent component, final Region region) {
            if (component.getClientProperty("Nimbus.Overrides") != null) {
                Map map = NimbusDefaults.this.overridesCache.get(component);
                SynthStyle synthStyle = null;
                if (map == null) {
                    map = new HashMap();
                    NimbusDefaults.this.overridesCache.put(component, map);
                }
                else {
                    synthStyle = (SynthStyle)map.get(region);
                }
                if (synthStyle == null) {
                    synthStyle = new NimbusStyle(this.prefix, component);
                    map.put(region, synthStyle);
                }
                return synthStyle;
            }
            if (this.style == null) {
                this.style = new NimbusStyle(this.prefix, null);
            }
            return this.style;
        }
        
        boolean matches(final JComponent component) {
            return this.matches(component, this.parts.length - 1);
        }
        
        private boolean matches(final Component component, final int n) {
            if (n < 0) {
                return true;
            }
            if (component == null) {
                return false;
            }
            final String name = component.getName();
            if (this.parts[n].named && this.parts[n].s.equals(name)) {
                return this.matches(component.getParent(), n - 1);
            }
            if (!this.parts[n].named) {
                final Class access$900 = this.parts[n].c;
                if (access$900 != null && access$900.isAssignableFrom(component.getClass())) {
                    return this.matches(component.getParent(), n - 1);
                }
                if (access$900 == null && NimbusDefaults.this.registeredRegions.containsKey(this.parts[n].s)) {
                    final Region region = NimbusDefaults.this.registeredRegions.get(this.parts[n].s);
                    Component internalFrame = region.isSubregion() ? component : component.getParent();
                    if (region == Region.INTERNAL_FRAME_TITLE_PANE && internalFrame != null && internalFrame instanceof JInternalFrame.JDesktopIcon) {
                        internalFrame = ((JInternalFrame.JDesktopIcon)internalFrame).getInternalFrame();
                    }
                    return this.matches(internalFrame, n - 1);
                }
            }
            return false;
        }
        
        private List<String> split(final String s) {
            final ArrayList list = new ArrayList();
            int n = 0;
            boolean b = false;
            int n2 = 0;
            for (int i = 0; i < s.length(); ++i) {
                final char char1 = s.charAt(i);
                if (char1 == '[') {
                    ++n;
                }
                else if (char1 == '\"') {
                    b = !b;
                }
                else if (char1 == ']') {
                    if (--n < 0) {
                        throw new RuntimeException("Malformed prefix: " + s);
                    }
                }
                else if (char1 == ':' && !b && n == 0) {
                    list.add(s.substring(n2, i));
                    n2 = i + 1;
                }
            }
            if (n2 < s.length() - 1 && !b && n == 0) {
                list.add(s.substring(n2));
            }
            return list;
        }
        
        private final class Part
        {
            private String s;
            private boolean named;
            private Class c;
            
            Part(final String s) {
                this.named = (s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"');
                if (this.named) {
                    this.s = s.substring(1, s.length() - 1);
                }
                else {
                    this.s = s;
                    try {
                        this.c = Class.forName("javax.swing.J" + s);
                    }
                    catch (final Exception ex) {}
                    try {
                        this.c = Class.forName(s.replace("_", "."));
                    }
                    catch (final Exception ex2) {}
                }
            }
        }
    }
    
    private class ColorTree implements PropertyChangeListener
    {
        private Node root;
        private Map<String, Node> nodes;
        
        private ColorTree() {
            this.root = new Node(null, null);
            this.nodes = new HashMap<String, Node>();
        }
        
        public Color getColor(final String s) {
            return this.nodes.get(s).color;
        }
        
        public void addColor(final String s, final Color color) {
            final Node parentNode = this.getParentNode(color);
            final Node node = new Node(color, parentNode);
            parentNode.children.add(node);
            if (s != null) {
                this.nodes.put(s, node);
            }
        }
        
        private Node getParentNode(final Color color) {
            Node root = this.root;
            if (color instanceof DerivedColor) {
                final Node node = this.nodes.get(((DerivedColor)color).getUiDefaultParentName());
                if (node != null) {
                    root = node;
                }
            }
            return root;
        }
        
        public void update() {
            this.root.update();
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final Node node = this.nodes.get(propertyChangeEvent.getPropertyName());
            if (node != null) {
                node.parent.children.remove(node);
                final Color color = (Color)propertyChangeEvent.getNewValue();
                final Node parentNode = this.getParentNode(color);
                node.set(color, parentNode);
                parentNode.children.add(node);
                node.update();
            }
        }
        
        class Node
        {
            Color color;
            Node parent;
            List<Node> children;
            
            Node(final Color color, final Node node) {
                this.children = new LinkedList<Node>();
                this.set(color, node);
            }
            
            public void set(final Color color, final Node parent) {
                this.color = color;
                this.parent = parent;
            }
            
            public void update() {
                if (this.color instanceof DerivedColor) {
                    ((DerivedColor)this.color).rederiveColor();
                }
                final Iterator<Node> iterator = this.children.iterator();
                while (iterator.hasNext()) {
                    iterator.next().update();
                }
            }
        }
    }
    
    private class DefaultsListener implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if ("lookAndFeel".equals(propertyChangeEvent.getPropertyName())) {
                NimbusDefaults.this.colorTree.update();
            }
        }
    }
    
    private static final class PainterBorder implements Border, UIResource
    {
        private Insets insets;
        private Painter painter;
        private String painterKey;
        
        PainterBorder(final String painterKey, final Insets insets) {
            this.insets = insets;
            this.painterKey = painterKey;
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            if (this.painter == null) {
                this.painter = (Painter)UIManager.get(this.painterKey);
                if (this.painter == null) {
                    return;
                }
            }
            graphics.translate(n, n2);
            if (graphics instanceof Graphics2D) {
                this.painter.paint((Graphics2D)graphics, component, n3, n4);
            }
            else {
                final BufferedImage bufferedImage = new BufferedImage(n3, n4, 2);
                final Graphics2D graphics2 = bufferedImage.createGraphics();
                this.painter.paint(graphics2, component, n3, n4);
                graphics2.dispose();
                graphics.drawImage(bufferedImage, n, n2, null);
            }
            graphics.translate(-n, -n2);
        }
        
        @Override
        public Insets getBorderInsets(final Component component) {
            return (Insets)this.insets.clone();
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
