package javax.swing.plaf.basic;

import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import javax.swing.JViewport;
import java.awt.Container;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import java.awt.Point;
import javax.swing.plaf.UIResource;
import java.awt.Shape;
import java.awt.Polygon;
import javax.swing.Icon;
import java.awt.Font;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Dimension;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.ActionMap;
import sun.swing.DefaultLookup;
import javax.swing.SwingUtilities;
import java.awt.event.ContainerListener;
import java.awt.event.MouseMotionListener;
import javax.swing.UIManager;
import javax.swing.LookAndFeel;
import javax.swing.JButton;
import java.awt.LayoutManager;
import javax.swing.Action;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.InputMap;
import java.util.Hashtable;
import javax.swing.text.View;
import java.util.Vector;
import java.awt.Component;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import java.awt.Rectangle;
import javax.swing.KeyStroke;
import java.awt.Insets;
import java.awt.Color;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.plaf.TabbedPaneUI;

public class BasicTabbedPaneUI extends TabbedPaneUI implements SwingConstants
{
    protected JTabbedPane tabPane;
    protected Color highlight;
    protected Color lightHighlight;
    protected Color shadow;
    protected Color darkShadow;
    protected Color focus;
    private Color selectedColor;
    protected int textIconGap;
    protected int tabRunOverlay;
    protected Insets tabInsets;
    protected Insets selectedTabPadInsets;
    protected Insets tabAreaInsets;
    protected Insets contentBorderInsets;
    private boolean tabsOverlapBorder;
    private boolean tabsOpaque;
    private boolean contentOpaque;
    @Deprecated
    protected KeyStroke upKey;
    @Deprecated
    protected KeyStroke downKey;
    @Deprecated
    protected KeyStroke leftKey;
    @Deprecated
    protected KeyStroke rightKey;
    protected int[] tabRuns;
    protected int runCount;
    protected int selectedRun;
    protected Rectangle[] rects;
    protected int maxTabHeight;
    protected int maxTabWidth;
    protected ChangeListener tabChangeListener;
    protected PropertyChangeListener propertyChangeListener;
    protected MouseListener mouseListener;
    protected FocusListener focusListener;
    private Insets currentPadInsets;
    private Insets currentTabAreaInsets;
    private Component visibleComponent;
    private Vector<View> htmlViews;
    private Hashtable<Integer, Integer> mnemonicToIndexMap;
    private InputMap mnemonicInputMap;
    private ScrollableTabSupport tabScroller;
    private TabContainer tabContainer;
    protected transient Rectangle calcRect;
    private int focusIndex;
    private Handler handler;
    private int rolloverTabIndex;
    private boolean isRunsDirty;
    private boolean calculatedBaseline;
    private int baseline;
    private static int[] xCropLen;
    private static int[] yCropLen;
    private static final int CROP_SEGMENT = 12;
    
    public BasicTabbedPaneUI() {
        this.tabsOpaque = true;
        this.contentOpaque = true;
        this.tabRuns = new int[10];
        this.runCount = 0;
        this.selectedRun = -1;
        this.rects = new Rectangle[0];
        this.currentPadInsets = new Insets(0, 0, 0, 0);
        this.currentTabAreaInsets = new Insets(0, 0, 0, 0);
        this.calcRect = new Rectangle(0, 0, 0, 0);
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicTabbedPaneUI();
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("navigateNext"));
        lazyActionMap.put(new Actions("navigatePrevious"));
        lazyActionMap.put(new Actions("navigateRight"));
        lazyActionMap.put(new Actions("navigateLeft"));
        lazyActionMap.put(new Actions("navigateUp"));
        lazyActionMap.put(new Actions("navigateDown"));
        lazyActionMap.put(new Actions("navigatePageUp"));
        lazyActionMap.put(new Actions("navigatePageDown"));
        lazyActionMap.put(new Actions("requestFocus"));
        lazyActionMap.put(new Actions("requestFocusForVisibleComponent"));
        lazyActionMap.put(new Actions("setSelectedIndex"));
        lazyActionMap.put(new Actions("selectTabWithFocus"));
        lazyActionMap.put(new Actions("scrollTabsForwardAction"));
        lazyActionMap.put(new Actions("scrollTabsBackwardAction"));
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.tabPane = (JTabbedPane)component;
        this.calculatedBaseline = false;
        this.rolloverTabIndex = -1;
        this.focusIndex = -1;
        component.setLayout(this.createLayoutManager());
        this.installComponents();
        this.installDefaults();
        this.installListeners();
        this.installKeyboardActions();
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallKeyboardActions();
        this.uninstallListeners();
        this.uninstallDefaults();
        this.uninstallComponents();
        component.setLayout(null);
        this.tabPane = null;
    }
    
    protected LayoutManager createLayoutManager() {
        if (this.tabPane.getTabLayoutPolicy() == 1) {
            return new TabbedPaneScrollLayout();
        }
        return new TabbedPaneLayout();
    }
    
    private boolean scrollableTabLayoutEnabled() {
        return this.tabPane.getLayout() instanceof TabbedPaneScrollLayout;
    }
    
    protected void installComponents() {
        if (this.scrollableTabLayoutEnabled() && this.tabScroller == null) {
            this.tabScroller = new ScrollableTabSupport(this.tabPane.getTabPlacement());
            this.tabPane.add(this.tabScroller.viewport);
        }
        this.installTabContainer();
    }
    
    private void installTabContainer() {
        for (int i = 0; i < this.tabPane.getTabCount(); ++i) {
            final Component tabComponent = this.tabPane.getTabComponentAt(i);
            if (tabComponent != null) {
                if (this.tabContainer == null) {
                    this.tabContainer = new TabContainer();
                }
                this.tabContainer.add(tabComponent);
            }
        }
        if (this.tabContainer == null) {
            return;
        }
        if (this.scrollableTabLayoutEnabled()) {
            this.tabScroller.tabPanel.add(this.tabContainer);
        }
        else {
            this.tabPane.add(this.tabContainer);
        }
    }
    
    protected JButton createScrollButton(final int n) {
        if (n != 5 && n != 1 && n != 3 && n != 7) {
            throw new IllegalArgumentException("Direction must be one of: SOUTH, NORTH, EAST or WEST");
        }
        return new ScrollableTabButton(n);
    }
    
    protected void uninstallComponents() {
        this.uninstallTabContainer();
        if (this.scrollableTabLayoutEnabled()) {
            this.tabPane.remove(this.tabScroller.viewport);
            this.tabPane.remove(this.tabScroller.scrollForwardButton);
            this.tabPane.remove(this.tabScroller.scrollBackwardButton);
            this.tabScroller = null;
        }
    }
    
    private void uninstallTabContainer() {
        if (this.tabContainer == null) {
            return;
        }
        this.tabContainer.notifyTabbedPane = false;
        this.tabContainer.removeAll();
        if (this.scrollableTabLayoutEnabled()) {
            this.tabContainer.remove(this.tabScroller.croppedEdge);
            this.tabScroller.tabPanel.remove(this.tabContainer);
        }
        else {
            this.tabPane.remove(this.tabContainer);
        }
        this.tabContainer = null;
    }
    
    protected void installDefaults() {
        LookAndFeel.installColorsAndFont(this.tabPane, "TabbedPane.background", "TabbedPane.foreground", "TabbedPane.font");
        this.highlight = UIManager.getColor("TabbedPane.light");
        this.lightHighlight = UIManager.getColor("TabbedPane.highlight");
        this.shadow = UIManager.getColor("TabbedPane.shadow");
        this.darkShadow = UIManager.getColor("TabbedPane.darkShadow");
        this.focus = UIManager.getColor("TabbedPane.focus");
        this.selectedColor = UIManager.getColor("TabbedPane.selected");
        this.textIconGap = UIManager.getInt("TabbedPane.textIconGap");
        this.tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
        this.selectedTabPadInsets = UIManager.getInsets("TabbedPane.selectedTabPadInsets");
        this.tabAreaInsets = UIManager.getInsets("TabbedPane.tabAreaInsets");
        this.tabsOverlapBorder = UIManager.getBoolean("TabbedPane.tabsOverlapBorder");
        this.contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
        this.tabRunOverlay = UIManager.getInt("TabbedPane.tabRunOverlay");
        this.tabsOpaque = UIManager.getBoolean("TabbedPane.tabsOpaque");
        this.contentOpaque = UIManager.getBoolean("TabbedPane.contentOpaque");
        Object o = UIManager.get("TabbedPane.opaque");
        if (o == null) {
            o = Boolean.FALSE;
        }
        LookAndFeel.installProperty(this.tabPane, "opaque", o);
        if (this.tabInsets == null) {
            this.tabInsets = new Insets(0, 4, 1, 4);
        }
        if (this.selectedTabPadInsets == null) {
            this.selectedTabPadInsets = new Insets(2, 2, 2, 1);
        }
        if (this.tabAreaInsets == null) {
            this.tabAreaInsets = new Insets(3, 2, 0, 2);
        }
        if (this.contentBorderInsets == null) {
            this.contentBorderInsets = new Insets(2, 2, 3, 3);
        }
    }
    
    protected void uninstallDefaults() {
        this.highlight = null;
        this.lightHighlight = null;
        this.shadow = null;
        this.darkShadow = null;
        this.focus = null;
        this.tabInsets = null;
        this.selectedTabPadInsets = null;
        this.tabAreaInsets = null;
        this.contentBorderInsets = null;
    }
    
    protected void installListeners() {
        final PropertyChangeListener propertyChangeListener = this.createPropertyChangeListener();
        this.propertyChangeListener = propertyChangeListener;
        if (propertyChangeListener != null) {
            this.tabPane.addPropertyChangeListener(this.propertyChangeListener);
        }
        if ((this.tabChangeListener = this.createChangeListener()) != null) {
            this.tabPane.addChangeListener(this.tabChangeListener);
        }
        if ((this.mouseListener = this.createMouseListener()) != null) {
            this.tabPane.addMouseListener(this.mouseListener);
        }
        this.tabPane.addMouseMotionListener(this.getHandler());
        if ((this.focusListener = this.createFocusListener()) != null) {
            this.tabPane.addFocusListener(this.focusListener);
        }
        this.tabPane.addContainerListener(this.getHandler());
        if (this.tabPane.getTabCount() > 0) {
            this.htmlViews = this.createHTMLVector();
        }
    }
    
    protected void uninstallListeners() {
        if (this.mouseListener != null) {
            this.tabPane.removeMouseListener(this.mouseListener);
            this.mouseListener = null;
        }
        this.tabPane.removeMouseMotionListener(this.getHandler());
        if (this.focusListener != null) {
            this.tabPane.removeFocusListener(this.focusListener);
            this.focusListener = null;
        }
        this.tabPane.removeContainerListener(this.getHandler());
        if (this.htmlViews != null) {
            this.htmlViews.removeAllElements();
            this.htmlViews = null;
        }
        if (this.tabChangeListener != null) {
            this.tabPane.removeChangeListener(this.tabChangeListener);
            this.tabChangeListener = null;
        }
        if (this.propertyChangeListener != null) {
            this.tabPane.removePropertyChangeListener(this.propertyChangeListener);
            this.propertyChangeListener = null;
        }
        this.handler = null;
    }
    
    protected MouseListener createMouseListener() {
        return this.getHandler();
    }
    
    protected FocusListener createFocusListener() {
        return this.getHandler();
    }
    
    protected ChangeListener createChangeListener() {
        return this.getHandler();
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return this.getHandler();
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected void installKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.tabPane, 1, this.getInputMap(1));
        SwingUtilities.replaceUIInputMap(this.tabPane, 0, this.getInputMap(0));
        LazyActionMap.installLazyActionMap(this.tabPane, BasicTabbedPaneUI.class, "TabbedPane.actionMap");
        this.updateMnemonics();
    }
    
    InputMap getInputMap(final int n) {
        if (n == 1) {
            return (InputMap)DefaultLookup.get(this.tabPane, this, "TabbedPane.ancestorInputMap");
        }
        if (n == 0) {
            return (InputMap)DefaultLookup.get(this.tabPane, this, "TabbedPane.focusInputMap");
        }
        return null;
    }
    
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIActionMap(this.tabPane, null);
        SwingUtilities.replaceUIInputMap(this.tabPane, 1, null);
        SwingUtilities.replaceUIInputMap(this.tabPane, 0, null);
        SwingUtilities.replaceUIInputMap(this.tabPane, 2, null);
        this.mnemonicToIndexMap = null;
        this.mnemonicInputMap = null;
    }
    
    private void updateMnemonics() {
        this.resetMnemonics();
        for (int i = this.tabPane.getTabCount() - 1; i >= 0; --i) {
            final int mnemonic = this.tabPane.getMnemonicAt(i);
            if (mnemonic > 0) {
                this.addMnemonic(i, mnemonic);
            }
        }
    }
    
    private void resetMnemonics() {
        if (this.mnemonicToIndexMap != null) {
            this.mnemonicToIndexMap.clear();
            this.mnemonicInputMap.clear();
        }
    }
    
    private void addMnemonic(final int n, final int n2) {
        if (this.mnemonicToIndexMap == null) {
            this.initMnemonics();
        }
        this.mnemonicInputMap.put(KeyStroke.getKeyStroke(n2, BasicLookAndFeel.getFocusAcceleratorKeyMask()), "setSelectedIndex");
        this.mnemonicToIndexMap.put(n2, n);
    }
    
    private void initMnemonics() {
        this.mnemonicToIndexMap = new Hashtable<Integer, Integer>();
        (this.mnemonicInputMap = new ComponentInputMapUIResource(this.tabPane)).setParent(SwingUtilities.getUIInputMap(this.tabPane, 2));
        SwingUtilities.replaceUIInputMap(this.tabPane, 2, this.mnemonicInputMap);
    }
    
    private void setRolloverTab(final int n, final int n2) {
        this.setRolloverTab(this.tabForCoordinate(this.tabPane, n, n2, false));
    }
    
    protected void setRolloverTab(final int rolloverTabIndex) {
        this.rolloverTabIndex = rolloverTabIndex;
    }
    
    protected int getRolloverTab() {
        return this.rolloverTabIndex;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        return null;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return null;
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, final int n2) {
        super.getBaseline(component, n, n2);
        final int calculateBaselineIfNecessary = this.calculateBaselineIfNecessary();
        if (calculateBaselineIfNecessary != -1) {
            final int tabPlacement = this.tabPane.getTabPlacement();
            final Insets insets = this.tabPane.getInsets();
            final Insets tabAreaInsets = this.getTabAreaInsets(tabPlacement);
            switch (tabPlacement) {
                case 1: {
                    return calculateBaselineIfNecessary + (insets.top + tabAreaInsets.top);
                }
                case 3: {
                    return n2 - insets.bottom - tabAreaInsets.bottom - this.maxTabHeight + calculateBaselineIfNecessary;
                }
                case 2:
                case 4: {
                    return calculateBaselineIfNecessary + (insets.top + tabAreaInsets.top);
                }
            }
        }
        return -1;
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        super.getBaselineResizeBehavior(component);
        switch (this.tabPane.getTabPlacement()) {
            case 1:
            case 2:
            case 4: {
                return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
            }
            case 3: {
                return Component.BaselineResizeBehavior.CONSTANT_DESCENT;
            }
            default: {
                return Component.BaselineResizeBehavior.OTHER;
            }
        }
    }
    
    protected int getBaseline(final int n) {
        if (this.tabPane.getTabComponentAt(n) != null) {
            if (this.getBaselineOffset() != 0) {
                return -1;
            }
            final Component tabComponent = this.tabPane.getTabComponentAt(n);
            final Dimension preferredSize = tabComponent.getPreferredSize();
            final Insets tabInsets = this.getTabInsets(this.tabPane.getTabPlacement(), n);
            return tabComponent.getBaseline(preferredSize.width, preferredSize.height) + (this.maxTabHeight - tabInsets.top - tabInsets.bottom - preferredSize.height) / 2 + tabInsets.top;
        }
        else {
            final View textViewForTab = this.getTextViewForTab(n);
            if (textViewForTab == null) {
                final FontMetrics fontMetrics = this.getFontMetrics();
                return this.maxTabHeight / 2 - fontMetrics.getHeight() / 2 + fontMetrics.getAscent() + this.getBaselineOffset();
            }
            final int n2 = (int)textViewForTab.getPreferredSpan(1);
            final int htmlBaseline = BasicHTML.getHTMLBaseline(textViewForTab, (int)textViewForTab.getPreferredSpan(0), n2);
            if (htmlBaseline >= 0) {
                return this.maxTabHeight / 2 - n2 / 2 + htmlBaseline + this.getBaselineOffset();
            }
            return -1;
        }
    }
    
    protected int getBaselineOffset() {
        switch (this.tabPane.getTabPlacement()) {
            case 1: {
                if (this.tabPane.getTabCount() > 1) {
                    return 1;
                }
                return -1;
            }
            case 3: {
                if (this.tabPane.getTabCount() > 1) {
                    return -1;
                }
                return 1;
            }
            default: {
                return this.maxTabHeight % 2;
            }
        }
    }
    
    private int calculateBaselineIfNecessary() {
        if (!this.calculatedBaseline) {
            this.calculatedBaseline = true;
            this.baseline = -1;
            if (this.tabPane.getTabCount() > 0) {
                this.calculateBaseline();
            }
        }
        return this.baseline;
    }
    
    private void calculateBaseline() {
        final int tabCount = this.tabPane.getTabCount();
        final int tabPlacement = this.tabPane.getTabPlacement();
        this.maxTabHeight = this.calculateMaxTabHeight(tabPlacement);
        this.baseline = this.getBaseline(0);
        if (this.isHorizontalTabPlacement()) {
            for (int i = 1; i < tabCount; ++i) {
                if (this.getBaseline(i) != this.baseline) {
                    this.baseline = -1;
                    break;
                }
            }
        }
        else {
            final int height = this.getFontMetrics().getHeight();
            final int calculateTabHeight = this.calculateTabHeight(tabPlacement, 0, height);
            for (int j = 1; j < tabCount; ++j) {
                if (calculateTabHeight != this.calculateTabHeight(tabPlacement, j, height)) {
                    this.baseline = -1;
                    break;
                }
            }
        }
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final int selectedIndex = this.tabPane.getSelectedIndex();
        final int tabPlacement = this.tabPane.getTabPlacement();
        this.ensureCurrentLayout();
        if (this.tabsOverlapBorder) {
            this.paintContentBorder(graphics, tabPlacement, selectedIndex);
        }
        if (!this.scrollableTabLayoutEnabled()) {
            this.paintTabArea(graphics, tabPlacement, selectedIndex);
        }
        if (!this.tabsOverlapBorder) {
            this.paintContentBorder(graphics, tabPlacement, selectedIndex);
        }
    }
    
    protected void paintTabArea(final Graphics graphics, final int n, final int n2) {
        final int tabCount = this.tabPane.getTabCount();
        final Rectangle rectangle = new Rectangle();
        final Rectangle rectangle2 = new Rectangle();
        final Rectangle clipBounds = graphics.getClipBounds();
        for (int i = this.runCount - 1; i >= 0; --i) {
            final int n3 = this.tabRuns[i];
            final int n4 = this.tabRuns[(i == this.runCount - 1) ? 0 : (i + 1)];
            for (int n5 = (n4 != 0) ? (n4 - 1) : (tabCount - 1), j = n3; j <= n5; ++j) {
                if (j != n2 && this.rects[j].intersects(clipBounds)) {
                    this.paintTab(graphics, n, this.rects, j, rectangle, rectangle2);
                }
            }
        }
        if (n2 >= 0 && this.rects[n2].intersects(clipBounds)) {
            this.paintTab(graphics, n, this.rects, n2, rectangle, rectangle2);
        }
    }
    
    protected void paintTab(final Graphics graphics, final int n, final Rectangle[] array, final int n2, final Rectangle rectangle, final Rectangle rectangle2) {
        final Rectangle rectangle3 = array[n2];
        final boolean b = this.tabPane.getSelectedIndex() == n2;
        if (this.tabsOpaque || this.tabPane.isOpaque()) {
            this.paintTabBackground(graphics, n, n2, rectangle3.x, rectangle3.y, rectangle3.width, rectangle3.height, b);
        }
        this.paintTabBorder(graphics, n, n2, rectangle3.x, rectangle3.y, rectangle3.width, rectangle3.height, b);
        final String title = this.tabPane.getTitleAt(n2);
        final Font font = this.tabPane.getFont();
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.tabPane, graphics, font);
        final Icon iconForTab = this.getIconForTab(n2);
        this.layoutLabel(n, fontMetrics, n2, title, iconForTab, rectangle3, rectangle, rectangle2, b);
        if (this.tabPane.getTabComponentAt(n2) == null) {
            String s = title;
            if (this.scrollableTabLayoutEnabled() && this.tabScroller.croppedEdge.isParamsSet() && this.tabScroller.croppedEdge.getTabIndex() == n2 && this.isHorizontalTabPlacement()) {
                s = SwingUtilities2.clipStringIfNecessary(null, fontMetrics, title, this.tabScroller.croppedEdge.getCropline() - (rectangle2.x - rectangle3.x) - this.tabScroller.croppedEdge.getCroppedSideWidth());
            }
            else if (!this.scrollableTabLayoutEnabled() && this.isHorizontalTabPlacement()) {
                s = SwingUtilities2.clipStringIfNecessary(null, fontMetrics, title, rectangle2.width);
            }
            this.paintText(graphics, n, font, fontMetrics, n2, s, rectangle2, b);
            this.paintIcon(graphics, n, n2, iconForTab, rectangle, b);
        }
        this.paintFocusIndicator(graphics, n, array, n2, rectangle, rectangle2, b);
    }
    
    private boolean isHorizontalTabPlacement() {
        return this.tabPane.getTabPlacement() == 1 || this.tabPane.getTabPlacement() == 3;
    }
    
    private static Polygon createCroppedTabShape(final int n, final Rectangle rectangle, final int n2) {
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        switch (n) {
            case 2:
            case 4: {
                n3 = rectangle.width;
                n4 = rectangle.x;
                n5 = rectangle.x + rectangle.width;
                n6 = rectangle.y + rectangle.height;
                break;
            }
            default: {
                n3 = rectangle.height;
                n4 = rectangle.y;
                n5 = rectangle.y + rectangle.height;
                n6 = rectangle.x + rectangle.width;
                break;
            }
        }
        int n7 = n3 / 12;
        if (n3 % 12 > 0) {
            ++n7;
        }
        final int n8 = 2 + n7 * 8;
        final int[] array = new int[n8];
        final int[] array2 = new int[n8];
        int n9 = 0;
        array[n9] = n6;
        array2[n9++] = n5;
        array[n9] = n6;
        array2[n9++] = n4;
        for (int i = 0; i < n7; ++i) {
            for (int j = 0; j < BasicTabbedPaneUI.xCropLen.length; ++j) {
                array[n9] = n2 - BasicTabbedPaneUI.xCropLen[j];
                array2[n9] = n4 + i * 12 + BasicTabbedPaneUI.yCropLen[j];
                if (array2[n9] >= n5) {
                    array2[n9] = n5;
                    ++n9;
                    break;
                }
                ++n9;
            }
        }
        if (n == 1 || n == 3) {
            return new Polygon(array, array2, n9);
        }
        return new Polygon(array2, array, n9);
    }
    
    private void paintCroppedTabEdge(final Graphics graphics) {
        final int tabIndex = this.tabScroller.croppedEdge.getTabIndex();
        final int cropline = this.tabScroller.croppedEdge.getCropline();
        switch (this.tabPane.getTabPlacement()) {
            case 2:
            case 4: {
                final int x = this.rects[tabIndex].x;
                final int n = cropline;
                int i = x;
                graphics.setColor(this.shadow);
                while (i <= x + this.rects[tabIndex].width) {
                    for (int j = 0; j < BasicTabbedPaneUI.xCropLen.length; j += 2) {
                        graphics.drawLine(i + BasicTabbedPaneUI.yCropLen[j], n - BasicTabbedPaneUI.xCropLen[j], i + BasicTabbedPaneUI.yCropLen[j + 1] - 1, n - BasicTabbedPaneUI.xCropLen[j + 1]);
                    }
                    i += 12;
                }
                break;
            }
            default: {
                final int n2 = cropline;
                int k;
                final int n3 = k = this.rects[tabIndex].y;
                graphics.setColor(this.shadow);
                while (k <= n3 + this.rects[tabIndex].height) {
                    for (int l = 0; l < BasicTabbedPaneUI.xCropLen.length; l += 2) {
                        graphics.drawLine(n2 - BasicTabbedPaneUI.xCropLen[l], k + BasicTabbedPaneUI.yCropLen[l], n2 - BasicTabbedPaneUI.xCropLen[l + 1], k + BasicTabbedPaneUI.yCropLen[l + 1] - 1);
                    }
                    k += 12;
                }
                break;
            }
        }
    }
    
    protected void layoutLabel(final int n, final FontMetrics fontMetrics, final int n2, final String s, final Icon icon, final Rectangle rectangle, final Rectangle rectangle2, final Rectangle rectangle3, final boolean b) {
        final int n3 = 0;
        rectangle2.y = n3;
        rectangle2.x = n3;
        rectangle3.y = n3;
        rectangle3.x = n3;
        final View textViewForTab = this.getTextViewForTab(n2);
        if (textViewForTab != null) {
            this.tabPane.putClientProperty("html", textViewForTab);
        }
        SwingUtilities.layoutCompoundLabel(this.tabPane, fontMetrics, s, icon, 0, 0, 0, 11, rectangle, rectangle2, rectangle3, this.textIconGap);
        this.tabPane.putClientProperty("html", null);
        final int tabLabelShiftX = this.getTabLabelShiftX(n, n2, b);
        final int tabLabelShiftY = this.getTabLabelShiftY(n, n2, b);
        rectangle2.x += tabLabelShiftX;
        rectangle2.y += tabLabelShiftY;
        rectangle3.x += tabLabelShiftX;
        rectangle3.y += tabLabelShiftY;
    }
    
    protected void paintIcon(final Graphics graphics, final int n, final int n2, final Icon icon, final Rectangle rectangle, final boolean b) {
        if (icon != null) {
            icon.paintIcon(this.tabPane, graphics, rectangle.x, rectangle.y);
        }
    }
    
    protected void paintText(final Graphics graphics, final int n, final Font font, final FontMetrics fontMetrics, final int n2, final String s, final Rectangle rectangle, final boolean b) {
        graphics.setFont(font);
        final View textViewForTab = this.getTextViewForTab(n2);
        if (textViewForTab != null) {
            textViewForTab.paint(graphics, rectangle);
        }
        else {
            final int displayedMnemonicIndex = this.tabPane.getDisplayedMnemonicIndexAt(n2);
            if (this.tabPane.isEnabled() && this.tabPane.isEnabledAt(n2)) {
                Color foreground = this.tabPane.getForegroundAt(n2);
                if (b && foreground instanceof UIResource) {
                    final Color color = UIManager.getColor("TabbedPane.selectedForeground");
                    if (color != null) {
                        foreground = color;
                    }
                }
                graphics.setColor(foreground);
                SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, graphics, s, displayedMnemonicIndex, rectangle.x, rectangle.y + fontMetrics.getAscent());
            }
            else {
                graphics.setColor(this.tabPane.getBackgroundAt(n2).brighter());
                SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, graphics, s, displayedMnemonicIndex, rectangle.x, rectangle.y + fontMetrics.getAscent());
                graphics.setColor(this.tabPane.getBackgroundAt(n2).darker());
                SwingUtilities2.drawStringUnderlineCharAt(this.tabPane, graphics, s, displayedMnemonicIndex, rectangle.x - 1, rectangle.y + fontMetrics.getAscent() - 1);
            }
        }
    }
    
    protected int getTabLabelShiftX(final int n, final int n2, final boolean b) {
        final Rectangle rectangle = this.rects[n2];
        final int int1 = DefaultLookup.getInt(this.tabPane, this, "TabbedPane." + (b ? "selectedLabelShift" : "labelShift"), 1);
        switch (n) {
            case 2: {
                return int1;
            }
            case 4: {
                return -int1;
            }
            default: {
                return rectangle.width % 2;
            }
        }
    }
    
    protected int getTabLabelShiftY(final int n, final int n2, final boolean b) {
        final Rectangle rectangle = this.rects[n2];
        final int n3 = b ? DefaultLookup.getInt(this.tabPane, this, "TabbedPane.selectedLabelShift", -1) : DefaultLookup.getInt(this.tabPane, this, "TabbedPane.labelShift", 1);
        switch (n) {
            case 3: {
                return -n3;
            }
            case 2:
            case 4: {
                return rectangle.height % 2;
            }
            default: {
                return n3;
            }
        }
    }
    
    protected void paintFocusIndicator(final Graphics graphics, final int n, final Rectangle[] array, final int n2, final Rectangle rectangle, final Rectangle rectangle2, final boolean b) {
        final Rectangle rectangle3 = array[n2];
        if (this.tabPane.hasFocus() && b) {
            graphics.setColor(this.focus);
            int n3 = 0;
            int n4 = 0;
            int n5 = 0;
            int n6 = 0;
            switch (n) {
                case 2: {
                    n3 = rectangle3.x + 3;
                    n4 = rectangle3.y + 3;
                    n5 = rectangle3.width - 5;
                    n6 = rectangle3.height - 6;
                    break;
                }
                case 4: {
                    n3 = rectangle3.x + 2;
                    n4 = rectangle3.y + 3;
                    n5 = rectangle3.width - 5;
                    n6 = rectangle3.height - 6;
                    break;
                }
                case 3: {
                    n3 = rectangle3.x + 3;
                    n4 = rectangle3.y + 2;
                    n5 = rectangle3.width - 6;
                    n6 = rectangle3.height - 5;
                    break;
                }
                default: {
                    n3 = rectangle3.x + 3;
                    n4 = rectangle3.y + 3;
                    n5 = rectangle3.width - 6;
                    n6 = rectangle3.height - 5;
                    break;
                }
            }
            BasicGraphicsUtils.drawDashedRect(graphics, n3, n4, n5, n6);
        }
    }
    
    protected void paintTabBorder(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final boolean b) {
        graphics.setColor(this.lightHighlight);
        switch (n) {
            case 2: {
                graphics.drawLine(n3 + 1, n4 + n6 - 2, n3 + 1, n4 + n6 - 2);
                graphics.drawLine(n3, n4 + 2, n3, n4 + n6 - 3);
                graphics.drawLine(n3 + 1, n4 + 1, n3 + 1, n4 + 1);
                graphics.drawLine(n3 + 2, n4, n3 + n5 - 1, n4);
                graphics.setColor(this.shadow);
                graphics.drawLine(n3 + 2, n4 + n6 - 2, n3 + n5 - 1, n4 + n6 - 2);
                graphics.setColor(this.darkShadow);
                graphics.drawLine(n3 + 2, n4 + n6 - 1, n3 + n5 - 1, n4 + n6 - 1);
                break;
            }
            case 4: {
                graphics.drawLine(n3, n4, n3 + n5 - 3, n4);
                graphics.setColor(this.shadow);
                graphics.drawLine(n3, n4 + n6 - 2, n3 + n5 - 3, n4 + n6 - 2);
                graphics.drawLine(n3 + n5 - 2, n4 + 2, n3 + n5 - 2, n4 + n6 - 3);
                graphics.setColor(this.darkShadow);
                graphics.drawLine(n3 + n5 - 2, n4 + 1, n3 + n5 - 2, n4 + 1);
                graphics.drawLine(n3 + n5 - 2, n4 + n6 - 2, n3 + n5 - 2, n4 + n6 - 2);
                graphics.drawLine(n3 + n5 - 1, n4 + 2, n3 + n5 - 1, n4 + n6 - 3);
                graphics.drawLine(n3, n4 + n6 - 1, n3 + n5 - 3, n4 + n6 - 1);
                break;
            }
            case 3: {
                graphics.drawLine(n3, n4, n3, n4 + n6 - 3);
                graphics.drawLine(n3 + 1, n4 + n6 - 2, n3 + 1, n4 + n6 - 2);
                graphics.setColor(this.shadow);
                graphics.drawLine(n3 + 2, n4 + n6 - 2, n3 + n5 - 3, n4 + n6 - 2);
                graphics.drawLine(n3 + n5 - 2, n4, n3 + n5 - 2, n4 + n6 - 3);
                graphics.setColor(this.darkShadow);
                graphics.drawLine(n3 + 2, n4 + n6 - 1, n3 + n5 - 3, n4 + n6 - 1);
                graphics.drawLine(n3 + n5 - 2, n4 + n6 - 2, n3 + n5 - 2, n4 + n6 - 2);
                graphics.drawLine(n3 + n5 - 1, n4, n3 + n5 - 1, n4 + n6 - 3);
                break;
            }
            default: {
                graphics.drawLine(n3, n4 + 2, n3, n4 + n6 - 1);
                graphics.drawLine(n3 + 1, n4 + 1, n3 + 1, n4 + 1);
                graphics.drawLine(n3 + 2, n4, n3 + n5 - 3, n4);
                graphics.setColor(this.shadow);
                graphics.drawLine(n3 + n5 - 2, n4 + 2, n3 + n5 - 2, n4 + n6 - 1);
                graphics.setColor(this.darkShadow);
                graphics.drawLine(n3 + n5 - 1, n4 + 2, n3 + n5 - 1, n4 + n6 - 1);
                graphics.drawLine(n3 + n5 - 2, n4 + 1, n3 + n5 - 2, n4 + 1);
                break;
            }
        }
    }
    
    protected void paintTabBackground(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final boolean b) {
        graphics.setColor((!b || this.selectedColor == null) ? this.tabPane.getBackgroundAt(n2) : this.selectedColor);
        switch (n) {
            case 2: {
                graphics.fillRect(n3 + 1, n4 + 1, n5 - 1, n6 - 3);
                break;
            }
            case 4: {
                graphics.fillRect(n3, n4 + 1, n5 - 2, n6 - 3);
                break;
            }
            case 3: {
                graphics.fillRect(n3 + 1, n4, n5 - 3, n6 - 1);
                break;
            }
            default: {
                graphics.fillRect(n3 + 1, n4 + 1, n5 - 3, n6 - 1);
                break;
            }
        }
    }
    
    protected void paintContentBorder(final Graphics graphics, final int n, final int n2) {
        final int width = this.tabPane.getWidth();
        final int height = this.tabPane.getHeight();
        final Insets insets = this.tabPane.getInsets();
        final Insets tabAreaInsets = this.getTabAreaInsets(n);
        int left = insets.left;
        int top = insets.top;
        int n3 = width - insets.right - insets.left;
        int n4 = height - insets.top - insets.bottom;
        switch (n) {
            case 2: {
                left += this.calculateTabAreaWidth(n, this.runCount, this.maxTabWidth);
                if (this.tabsOverlapBorder) {
                    left -= tabAreaInsets.right;
                }
                n3 -= left - insets.left;
                break;
            }
            case 4: {
                n3 -= this.calculateTabAreaWidth(n, this.runCount, this.maxTabWidth);
                if (this.tabsOverlapBorder) {
                    n3 += tabAreaInsets.left;
                    break;
                }
                break;
            }
            case 3: {
                n4 -= this.calculateTabAreaHeight(n, this.runCount, this.maxTabHeight);
                if (this.tabsOverlapBorder) {
                    n4 += tabAreaInsets.top;
                    break;
                }
                break;
            }
            default: {
                top += this.calculateTabAreaHeight(n, this.runCount, this.maxTabHeight);
                if (this.tabsOverlapBorder) {
                    top -= tabAreaInsets.bottom;
                }
                n4 -= top - insets.top;
                break;
            }
        }
        if (this.tabPane.getTabCount() > 0 && (this.contentOpaque || this.tabPane.isOpaque())) {
            final Color color = UIManager.getColor("TabbedPane.contentAreaColor");
            if (color != null) {
                graphics.setColor(color);
            }
            else if (this.selectedColor == null || n2 == -1) {
                graphics.setColor(this.tabPane.getBackground());
            }
            else {
                graphics.setColor(this.selectedColor);
            }
            graphics.fillRect(left, top, n3, n4);
        }
        this.paintContentBorderTopEdge(graphics, n, n2, left, top, n3, n4);
        this.paintContentBorderLeftEdge(graphics, n, n2, left, top, n3, n4);
        this.paintContentBorderBottomEdge(graphics, n, n2, left, top, n3, n4);
        this.paintContentBorderRightEdge(graphics, n, n2, left, top, n3, n4);
    }
    
    protected void paintContentBorderTopEdge(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final Rectangle rectangle = (n2 < 0) ? null : this.getTabBounds(n2, this.calcRect);
        graphics.setColor(this.lightHighlight);
        if (n != 1 || n2 < 0 || rectangle.y + rectangle.height + 1 < n4 || rectangle.x < n3 || rectangle.x > n3 + n5) {
            graphics.drawLine(n3, n4, n3 + n5 - 2, n4);
        }
        else {
            graphics.drawLine(n3, n4, rectangle.x - 1, n4);
            if (rectangle.x + rectangle.width < n3 + n5 - 2) {
                graphics.drawLine(rectangle.x + rectangle.width, n4, n3 + n5 - 2, n4);
            }
            else {
                graphics.setColor(this.shadow);
                graphics.drawLine(n3 + n5 - 2, n4, n3 + n5 - 2, n4);
            }
        }
    }
    
    protected void paintContentBorderLeftEdge(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final Rectangle rectangle = (n2 < 0) ? null : this.getTabBounds(n2, this.calcRect);
        graphics.setColor(this.lightHighlight);
        if (n != 2 || n2 < 0 || rectangle.x + rectangle.width + 1 < n3 || rectangle.y < n4 || rectangle.y > n4 + n6) {
            graphics.drawLine(n3, n4, n3, n4 + n6 - 2);
        }
        else {
            graphics.drawLine(n3, n4, n3, rectangle.y - 1);
            if (rectangle.y + rectangle.height < n4 + n6 - 2) {
                graphics.drawLine(n3, rectangle.y + rectangle.height, n3, n4 + n6 - 2);
            }
        }
    }
    
    protected void paintContentBorderBottomEdge(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final Rectangle rectangle = (n2 < 0) ? null : this.getTabBounds(n2, this.calcRect);
        graphics.setColor(this.shadow);
        if (n != 3 || n2 < 0 || rectangle.y - 1 > n6 || rectangle.x < n3 || rectangle.x > n3 + n5) {
            graphics.drawLine(n3 + 1, n4 + n6 - 2, n3 + n5 - 2, n4 + n6 - 2);
            graphics.setColor(this.darkShadow);
            graphics.drawLine(n3, n4 + n6 - 1, n3 + n5 - 1, n4 + n6 - 1);
        }
        else {
            graphics.drawLine(n3 + 1, n4 + n6 - 2, rectangle.x - 1, n4 + n6 - 2);
            graphics.setColor(this.darkShadow);
            graphics.drawLine(n3, n4 + n6 - 1, rectangle.x - 1, n4 + n6 - 1);
            if (rectangle.x + rectangle.width < n3 + n5 - 2) {
                graphics.setColor(this.shadow);
                graphics.drawLine(rectangle.x + rectangle.width, n4 + n6 - 2, n3 + n5 - 2, n4 + n6 - 2);
                graphics.setColor(this.darkShadow);
                graphics.drawLine(rectangle.x + rectangle.width, n4 + n6 - 1, n3 + n5 - 1, n4 + n6 - 1);
            }
        }
    }
    
    protected void paintContentBorderRightEdge(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final Rectangle rectangle = (n2 < 0) ? null : this.getTabBounds(n2, this.calcRect);
        graphics.setColor(this.shadow);
        if (n != 4 || n2 < 0 || rectangle.x - 1 > n5 || rectangle.y < n4 || rectangle.y > n4 + n6) {
            graphics.drawLine(n3 + n5 - 2, n4 + 1, n3 + n5 - 2, n4 + n6 - 3);
            graphics.setColor(this.darkShadow);
            graphics.drawLine(n3 + n5 - 1, n4, n3 + n5 - 1, n4 + n6 - 1);
        }
        else {
            graphics.drawLine(n3 + n5 - 2, n4 + 1, n3 + n5 - 2, rectangle.y - 1);
            graphics.setColor(this.darkShadow);
            graphics.drawLine(n3 + n5 - 1, n4, n3 + n5 - 1, rectangle.y - 1);
            if (rectangle.y + rectangle.height < n4 + n6 - 2) {
                graphics.setColor(this.shadow);
                graphics.drawLine(n3 + n5 - 2, rectangle.y + rectangle.height, n3 + n5 - 2, n4 + n6 - 2);
                graphics.setColor(this.darkShadow);
                graphics.drawLine(n3 + n5 - 1, rectangle.y + rectangle.height, n3 + n5 - 1, n4 + n6 - 2);
            }
        }
    }
    
    private void ensureCurrentLayout() {
        if (!this.tabPane.isValid()) {
            this.tabPane.validate();
        }
        if (!this.tabPane.isValid()) {
            ((TabbedPaneLayout)this.tabPane.getLayout()).calculateLayoutInfo();
        }
    }
    
    @Override
    public Rectangle getTabBounds(final JTabbedPane tabbedPane, final int n) {
        this.ensureCurrentLayout();
        return this.getTabBounds(n, new Rectangle());
    }
    
    @Override
    public int getTabRunCount(final JTabbedPane tabbedPane) {
        this.ensureCurrentLayout();
        return this.runCount;
    }
    
    @Override
    public int tabForCoordinate(final JTabbedPane tabbedPane, final int n, final int n2) {
        return this.tabForCoordinate(tabbedPane, n, n2, true);
    }
    
    private int tabForCoordinate(final JTabbedPane tabbedPane, final int n, final int n2, final boolean b) {
        if (b) {
            this.ensureCurrentLayout();
        }
        if (this.isRunsDirty) {
            return -1;
        }
        final Point point = new Point(n, n2);
        if (this.scrollableTabLayoutEnabled()) {
            this.translatePointToTabPanel(n, n2, point);
            if (!this.tabScroller.viewport.getViewRect().contains(point)) {
                return -1;
            }
        }
        for (int tabCount = this.tabPane.getTabCount(), i = 0; i < tabCount; ++i) {
            if (this.rects[i].contains(point.x, point.y)) {
                return i;
            }
        }
        return -1;
    }
    
    protected Rectangle getTabBounds(final int n, final Rectangle rectangle) {
        rectangle.width = this.rects[n].width;
        rectangle.height = this.rects[n].height;
        if (this.scrollableTabLayoutEnabled()) {
            final Point location = this.tabScroller.viewport.getLocation();
            final Point viewPosition = this.tabScroller.viewport.getViewPosition();
            rectangle.x = this.rects[n].x + location.x - viewPosition.x;
            rectangle.y = this.rects[n].y + location.y - viewPosition.y;
        }
        else {
            rectangle.x = this.rects[n].x;
            rectangle.y = this.rects[n].y;
        }
        return rectangle;
    }
    
    private int getClosestTab(final int n, final int n2) {
        int i = 0;
        int min;
        final int n3 = min = Math.min(this.rects.length, this.tabPane.getTabCount());
        final int tabPlacement = this.tabPane.getTabPlacement();
        final boolean b = tabPlacement == 1 || tabPlacement == 3;
        final int n4 = b ? n : n2;
        while (i != min) {
            final int n5 = (min + i) / 2;
            int n6;
            int n7;
            if (b) {
                n6 = this.rects[n5].x;
                n7 = n6 + this.rects[n5].width;
            }
            else {
                n6 = this.rects[n5].y;
                n7 = n6 + this.rects[n5].height;
            }
            if (n4 < n6) {
                min = n5;
                if (i == min) {
                    return Math.max(0, n5 - 1);
                }
                continue;
            }
            else {
                if (n4 < n7) {
                    return n5;
                }
                i = n5;
                if (min - i <= 1) {
                    return Math.max(n5 + 1, n3 - 1);
                }
                continue;
            }
        }
        return i;
    }
    
    private Point translatePointToTabPanel(final int n, final int n2, final Point point) {
        final Point location = this.tabScroller.viewport.getLocation();
        final Point viewPosition = this.tabScroller.viewport.getViewPosition();
        point.x = n - location.x + viewPosition.x;
        point.y = n2 - location.y + viewPosition.y;
        return point;
    }
    
    protected Component getVisibleComponent() {
        return this.visibleComponent;
    }
    
    protected void setVisibleComponent(final Component visibleComponent) {
        if (this.visibleComponent != null && this.visibleComponent != visibleComponent && this.visibleComponent.getParent() == this.tabPane && this.visibleComponent.isVisible()) {
            this.visibleComponent.setVisible(false);
        }
        if (visibleComponent != null && !visibleComponent.isVisible()) {
            visibleComponent.setVisible(true);
        }
        this.visibleComponent = visibleComponent;
    }
    
    protected void assureRectsCreated(final int n) {
        final int length = this.rects.length;
        if (n != length) {
            final Rectangle[] rects = new Rectangle[n];
            System.arraycopy(this.rects, 0, rects, 0, Math.min(length, n));
            this.rects = rects;
            for (int i = length; i < n; ++i) {
                this.rects[i] = new Rectangle();
            }
        }
    }
    
    protected void expandTabRunsArray() {
        final int[] tabRuns = new int[this.tabRuns.length + 10];
        System.arraycopy(this.tabRuns, 0, tabRuns, 0, this.runCount);
        this.tabRuns = tabRuns;
    }
    
    protected int getRunForTab(final int n, final int n2) {
        for (int i = 0; i < this.runCount; ++i) {
            final int n3 = this.tabRuns[i];
            final int lastTabInRun = this.lastTabInRun(n, i);
            if (n2 >= n3 && n2 <= lastTabInRun) {
                return i;
            }
        }
        return 0;
    }
    
    protected int lastTabInRun(final int n, final int n2) {
        if (this.runCount == 1) {
            return n - 1;
        }
        final int n3 = (n2 == this.runCount - 1) ? 0 : (n2 + 1);
        if (this.tabRuns[n3] == 0) {
            return n - 1;
        }
        return this.tabRuns[n3] - 1;
    }
    
    protected int getTabRunOverlay(final int n) {
        return this.tabRunOverlay;
    }
    
    protected int getTabRunIndent(final int n, final int n2) {
        return 0;
    }
    
    protected boolean shouldPadTabRun(final int n, final int n2) {
        return this.runCount > 1;
    }
    
    protected boolean shouldRotateTabRuns(final int n) {
        return true;
    }
    
    protected Icon getIconForTab(final int n) {
        return (!this.tabPane.isEnabled() || !this.tabPane.isEnabledAt(n)) ? this.tabPane.getDisabledIconAt(n) : this.tabPane.getIconAt(n);
    }
    
    protected View getTextViewForTab(final int n) {
        if (this.htmlViews != null) {
            return this.htmlViews.elementAt(n);
        }
        return null;
    }
    
    protected int calculateTabHeight(final int n, final int n2, final int n3) {
        final int n4 = 0;
        final Component tabComponent = this.tabPane.getTabComponentAt(n2);
        int n5;
        if (tabComponent != null) {
            n5 = tabComponent.getPreferredSize().height;
        }
        else {
            final View textViewForTab = this.getTextViewForTab(n2);
            if (textViewForTab != null) {
                n5 = n4 + (int)textViewForTab.getPreferredSpan(1);
            }
            else {
                n5 = n4 + n3;
            }
            final Icon iconForTab = this.getIconForTab(n2);
            if (iconForTab != null) {
                n5 = Math.max(n5, iconForTab.getIconHeight());
            }
        }
        final Insets tabInsets = this.getTabInsets(n, n2);
        return n5 + (tabInsets.top + tabInsets.bottom + 2);
    }
    
    protected int calculateMaxTabHeight(final int n) {
        final FontMetrics fontMetrics = this.getFontMetrics();
        final int tabCount = this.tabPane.getTabCount();
        int max = 0;
        final int height = fontMetrics.getHeight();
        for (int i = 0; i < tabCount; ++i) {
            max = Math.max(this.calculateTabHeight(n, i, height), max);
        }
        return max;
    }
    
    protected int calculateTabWidth(final int n, final int n2, final FontMetrics fontMetrics) {
        final Insets tabInsets = this.getTabInsets(n, n2);
        int n3 = tabInsets.left + tabInsets.right + 3;
        final Component tabComponent = this.tabPane.getTabComponentAt(n2);
        int n4;
        if (tabComponent != null) {
            n4 = n3 + tabComponent.getPreferredSize().width;
        }
        else {
            final Icon iconForTab = this.getIconForTab(n2);
            if (iconForTab != null) {
                n3 += iconForTab.getIconWidth() + this.textIconGap;
            }
            final View textViewForTab = this.getTextViewForTab(n2);
            if (textViewForTab != null) {
                n4 = n3 + (int)textViewForTab.getPreferredSpan(0);
            }
            else {
                n4 = n3 + SwingUtilities2.stringWidth(this.tabPane, fontMetrics, this.tabPane.getTitleAt(n2));
            }
        }
        return n4;
    }
    
    protected int calculateMaxTabWidth(final int n) {
        final FontMetrics fontMetrics = this.getFontMetrics();
        final int tabCount = this.tabPane.getTabCount();
        int max = 0;
        for (int i = 0; i < tabCount; ++i) {
            max = Math.max(this.calculateTabWidth(n, i, fontMetrics), max);
        }
        return max;
    }
    
    protected int calculateTabAreaHeight(final int n, final int n2, final int n3) {
        final Insets tabAreaInsets = this.getTabAreaInsets(n);
        final int tabRunOverlay = this.getTabRunOverlay(n);
        return (n2 > 0) ? (n2 * (n3 - tabRunOverlay) + tabRunOverlay + tabAreaInsets.top + tabAreaInsets.bottom) : 0;
    }
    
    protected int calculateTabAreaWidth(final int n, final int n2, final int n3) {
        final Insets tabAreaInsets = this.getTabAreaInsets(n);
        final int tabRunOverlay = this.getTabRunOverlay(n);
        return (n2 > 0) ? (n2 * (n3 - tabRunOverlay) + tabRunOverlay + tabAreaInsets.left + tabAreaInsets.right) : 0;
    }
    
    protected Insets getTabInsets(final int n, final int n2) {
        return this.tabInsets;
    }
    
    protected Insets getSelectedTabPadInsets(final int n) {
        rotateInsets(this.selectedTabPadInsets, this.currentPadInsets, n);
        return this.currentPadInsets;
    }
    
    protected Insets getTabAreaInsets(final int n) {
        rotateInsets(this.tabAreaInsets, this.currentTabAreaInsets, n);
        return this.currentTabAreaInsets;
    }
    
    protected Insets getContentBorderInsets(final int n) {
        return this.contentBorderInsets;
    }
    
    protected FontMetrics getFontMetrics() {
        return this.tabPane.getFontMetrics(this.tabPane.getFont());
    }
    
    protected void navigateSelectedTab(final int n) {
        final int tabPlacement = this.tabPane.getTabPlacement();
        final int n2 = DefaultLookup.getBoolean(this.tabPane, this, "TabbedPane.selectionFollowsFocus", true) ? this.tabPane.getSelectedIndex() : this.getFocusIndex();
        final int tabCount = this.tabPane.getTabCount();
        final boolean leftToRight = BasicGraphicsUtils.isLeftToRight(this.tabPane);
        if (tabCount <= 0) {
            return;
        }
        Label_0410: {
            switch (tabPlacement) {
                case 2:
                case 4: {
                    switch (n) {
                        case 12: {
                            this.selectNextTab(n2);
                            break Label_0410;
                        }
                        case 13: {
                            this.selectPreviousTab(n2);
                            break Label_0410;
                        }
                        case 1: {
                            this.selectPreviousTabInRun(n2);
                            break Label_0410;
                        }
                        case 5: {
                            this.selectNextTabInRun(n2);
                            break Label_0410;
                        }
                        case 7: {
                            this.selectAdjacentRunTab(tabPlacement, n2, this.getTabRunOffset(tabPlacement, tabCount, n2, false));
                            break Label_0410;
                        }
                        case 3: {
                            this.selectAdjacentRunTab(tabPlacement, n2, this.getTabRunOffset(tabPlacement, tabCount, n2, true));
                            break Label_0410;
                        }
                        default: {
                            break Label_0410;
                        }
                    }
                    break;
                }
                default: {
                    switch (n) {
                        case 12: {
                            this.selectNextTab(n2);
                            break Label_0410;
                        }
                        case 13: {
                            this.selectPreviousTab(n2);
                            break Label_0410;
                        }
                        case 1: {
                            this.selectAdjacentRunTab(tabPlacement, n2, this.getTabRunOffset(tabPlacement, tabCount, n2, false));
                            break Label_0410;
                        }
                        case 5: {
                            this.selectAdjacentRunTab(tabPlacement, n2, this.getTabRunOffset(tabPlacement, tabCount, n2, true));
                            break Label_0410;
                        }
                        case 3: {
                            if (leftToRight) {
                                this.selectNextTabInRun(n2);
                                break Label_0410;
                            }
                            this.selectPreviousTabInRun(n2);
                            break Label_0410;
                        }
                        case 7: {
                            if (leftToRight) {
                                this.selectPreviousTabInRun(n2);
                                break Label_0410;
                            }
                            this.selectNextTabInRun(n2);
                            break Label_0410;
                        }
                    }
                    break;
                }
            }
        }
    }
    
    protected void selectNextTabInRun(final int n) {
        int tabCount;
        int n2;
        for (tabCount = this.tabPane.getTabCount(), n2 = this.getNextTabIndexInRun(tabCount, n); n2 != n && !this.tabPane.isEnabledAt(n2); n2 = this.getNextTabIndexInRun(tabCount, n2)) {}
        this.navigateTo(n2);
    }
    
    protected void selectPreviousTabInRun(final int n) {
        int tabCount;
        int n2;
        for (tabCount = this.tabPane.getTabCount(), n2 = this.getPreviousTabIndexInRun(tabCount, n); n2 != n && !this.tabPane.isEnabledAt(n2); n2 = this.getPreviousTabIndexInRun(tabCount, n2)) {}
        this.navigateTo(n2);
    }
    
    protected void selectNextTab(final int n) {
        int n2;
        for (n2 = this.getNextTabIndex(n); n2 != n && !this.tabPane.isEnabledAt(n2); n2 = this.getNextTabIndex(n2)) {}
        this.navigateTo(n2);
    }
    
    protected void selectPreviousTab(final int n) {
        int n2;
        for (n2 = this.getPreviousTabIndex(n); n2 != n && !this.tabPane.isEnabledAt(n2); n2 = this.getPreviousTabIndex(n2)) {}
        this.navigateTo(n2);
    }
    
    protected void selectAdjacentRunTab(final int n, final int n2, final int n3) {
        if (this.runCount < 2) {
            return;
        }
        final Rectangle rectangle = this.rects[n2];
        int n4 = 0;
        switch (n) {
            case 2:
            case 4: {
                n4 = this.tabForCoordinate(this.tabPane, rectangle.x + rectangle.width / 2 + n3, rectangle.y + rectangle.height / 2);
                break;
            }
            default: {
                n4 = this.tabForCoordinate(this.tabPane, rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2 + n3);
                break;
            }
        }
        if (n4 != -1) {
            while (!this.tabPane.isEnabledAt(n4) && n4 != n2) {
                n4 = this.getNextTabIndex(n4);
            }
            this.navigateTo(n4);
        }
    }
    
    private void navigateTo(final int selectedIndex) {
        if (DefaultLookup.getBoolean(this.tabPane, this, "TabbedPane.selectionFollowsFocus", true)) {
            this.tabPane.setSelectedIndex(selectedIndex);
        }
        else {
            this.setFocusIndex(selectedIndex, true);
        }
    }
    
    void setFocusIndex(final int n, final boolean b) {
        if (b && !this.isRunsDirty) {
            this.repaintTab(this.focusIndex);
            this.repaintTab(this.focusIndex = n);
        }
        else {
            this.focusIndex = n;
        }
    }
    
    private void repaintTab(final int n) {
        if (!this.isRunsDirty && n >= 0 && n < this.tabPane.getTabCount()) {
            this.tabPane.repaint(this.getTabBounds(this.tabPane, n));
        }
    }
    
    private void validateFocusIndex() {
        if (this.focusIndex >= this.tabPane.getTabCount()) {
            this.setFocusIndex(this.tabPane.getSelectedIndex(), false);
        }
    }
    
    protected int getFocusIndex() {
        return this.focusIndex;
    }
    
    protected int getTabRunOffset(final int n, final int n2, final int n3, final boolean b) {
        final int runForTab = this.getRunForTab(n2, n3);
        int n4 = 0;
        switch (n) {
            case 2: {
                if (runForTab == 0) {
                    n4 = (b ? (-(this.calculateTabAreaWidth(n, this.runCount, this.maxTabWidth) - this.maxTabWidth)) : (-this.maxTabWidth));
                    break;
                }
                if (runForTab == this.runCount - 1) {
                    n4 = (b ? this.maxTabWidth : (this.calculateTabAreaWidth(n, this.runCount, this.maxTabWidth) - this.maxTabWidth));
                    break;
                }
                n4 = (b ? this.maxTabWidth : (-this.maxTabWidth));
                break;
            }
            case 4: {
                if (runForTab == 0) {
                    n4 = (b ? this.maxTabWidth : (this.calculateTabAreaWidth(n, this.runCount, this.maxTabWidth) - this.maxTabWidth));
                    break;
                }
                if (runForTab == this.runCount - 1) {
                    n4 = (b ? (-(this.calculateTabAreaWidth(n, this.runCount, this.maxTabWidth) - this.maxTabWidth)) : (-this.maxTabWidth));
                    break;
                }
                n4 = (b ? this.maxTabWidth : (-this.maxTabWidth));
                break;
            }
            case 3: {
                if (runForTab == 0) {
                    n4 = (b ? this.maxTabHeight : (this.calculateTabAreaHeight(n, this.runCount, this.maxTabHeight) - this.maxTabHeight));
                    break;
                }
                if (runForTab == this.runCount - 1) {
                    n4 = (b ? (-(this.calculateTabAreaHeight(n, this.runCount, this.maxTabHeight) - this.maxTabHeight)) : (-this.maxTabHeight));
                    break;
                }
                n4 = (b ? this.maxTabHeight : (-this.maxTabHeight));
                break;
            }
            default: {
                if (runForTab == 0) {
                    n4 = (b ? (-(this.calculateTabAreaHeight(n, this.runCount, this.maxTabHeight) - this.maxTabHeight)) : (-this.maxTabHeight));
                    break;
                }
                if (runForTab == this.runCount - 1) {
                    n4 = (b ? this.maxTabHeight : (this.calculateTabAreaHeight(n, this.runCount, this.maxTabHeight) - this.maxTabHeight));
                    break;
                }
                n4 = (b ? this.maxTabHeight : (-this.maxTabHeight));
                break;
            }
        }
        return n4;
    }
    
    protected int getPreviousTabIndex(final int n) {
        final int n2 = (n - 1 >= 0) ? (n - 1) : (this.tabPane.getTabCount() - 1);
        return (n2 >= 0) ? n2 : false;
    }
    
    protected int getNextTabIndex(final int n) {
        return (n + 1) % this.tabPane.getTabCount();
    }
    
    protected int getNextTabIndexInRun(final int n, final int n2) {
        if (this.runCount < 2) {
            return this.getNextTabIndex(n2);
        }
        final int runForTab = this.getRunForTab(n, n2);
        final int nextTabIndex = this.getNextTabIndex(n2);
        if (nextTabIndex == this.tabRuns[this.getNextTabRun(runForTab)]) {
            return this.tabRuns[runForTab];
        }
        return nextTabIndex;
    }
    
    protected int getPreviousTabIndexInRun(final int n, final int n2) {
        if (this.runCount < 2) {
            return this.getPreviousTabIndex(n2);
        }
        final int runForTab = this.getRunForTab(n, n2);
        if (n2 == this.tabRuns[runForTab]) {
            final int n3 = this.tabRuns[this.getNextTabRun(runForTab)] - 1;
            return (n3 != -1) ? n3 : (n - 1);
        }
        return this.getPreviousTabIndex(n2);
    }
    
    protected int getPreviousTabRun(final int n) {
        final int n2 = (n - 1 >= 0) ? (n - 1) : (this.runCount - 1);
        return (n2 >= 0) ? n2 : false;
    }
    
    protected int getNextTabRun(final int n) {
        return (n + 1) % this.runCount;
    }
    
    protected static void rotateInsets(final Insets insets, final Insets insets2, final int n) {
        switch (n) {
            case 2: {
                insets2.top = insets.left;
                insets2.left = insets.top;
                insets2.bottom = insets.right;
                insets2.right = insets.bottom;
                break;
            }
            case 3: {
                insets2.top = insets.bottom;
                insets2.left = insets.left;
                insets2.bottom = insets.top;
                insets2.right = insets.right;
                break;
            }
            case 4: {
                insets2.top = insets.left;
                insets2.left = insets.bottom;
                insets2.bottom = insets.right;
                insets2.right = insets.top;
                break;
            }
            default: {
                insets2.top = insets.top;
                insets2.left = insets.left;
                insets2.bottom = insets.bottom;
                insets2.right = insets.right;
                break;
            }
        }
    }
    
    boolean requestFocusForVisibleComponent() {
        return SwingUtilities2.tabbedPaneChangeFocusTo(this.getVisibleComponent());
    }
    
    private Vector<View> createHTMLVector() {
        final Vector vector = new Vector();
        final int tabCount = this.tabPane.getTabCount();
        if (tabCount > 0) {
            for (int i = 0; i < tabCount; ++i) {
                final String title = this.tabPane.getTitleAt(i);
                if (BasicHTML.isHTMLString(title)) {
                    vector.addElement(BasicHTML.createHTMLView(this.tabPane, title));
                }
                else {
                    vector.addElement(null);
                }
            }
        }
        return vector;
    }
    
    static {
        BasicTabbedPaneUI.xCropLen = new int[] { 1, 1, 0, 0, 1, 1, 2, 2 };
        BasicTabbedPaneUI.yCropLen = new int[] { 0, 3, 3, 6, 6, 9, 9, 12 };
    }
    
    private static class Actions extends UIAction
    {
        static final String NEXT = "navigateNext";
        static final String PREVIOUS = "navigatePrevious";
        static final String RIGHT = "navigateRight";
        static final String LEFT = "navigateLeft";
        static final String UP = "navigateUp";
        static final String DOWN = "navigateDown";
        static final String PAGE_UP = "navigatePageUp";
        static final String PAGE_DOWN = "navigatePageDown";
        static final String REQUEST_FOCUS = "requestFocus";
        static final String REQUEST_FOCUS_FOR_VISIBLE = "requestFocusForVisibleComponent";
        static final String SET_SELECTED = "setSelectedIndex";
        static final String SELECT_FOCUSED = "selectTabWithFocus";
        static final String SCROLL_FORWARD = "scrollTabsForwardAction";
        static final String SCROLL_BACKWARD = "scrollTabsBackwardAction";
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final String name = this.getName();
            final JTabbedPane tabbedPane = (JTabbedPane)actionEvent.getSource();
            final BasicTabbedPaneUI basicTabbedPaneUI = (BasicTabbedPaneUI)BasicLookAndFeel.getUIOfType(tabbedPane.getUI(), BasicTabbedPaneUI.class);
            if (basicTabbedPaneUI == null) {
                return;
            }
            if (name == "navigateNext") {
                basicTabbedPaneUI.navigateSelectedTab(12);
            }
            else if (name == "navigatePrevious") {
                basicTabbedPaneUI.navigateSelectedTab(13);
            }
            else if (name == "navigateRight") {
                basicTabbedPaneUI.navigateSelectedTab(3);
            }
            else if (name == "navigateLeft") {
                basicTabbedPaneUI.navigateSelectedTab(7);
            }
            else if (name == "navigateUp") {
                basicTabbedPaneUI.navigateSelectedTab(1);
            }
            else if (name == "navigateDown") {
                basicTabbedPaneUI.navigateSelectedTab(5);
            }
            else if (name == "navigatePageUp") {
                final int tabPlacement = tabbedPane.getTabPlacement();
                if (tabPlacement == 1 || tabPlacement == 3) {
                    basicTabbedPaneUI.navigateSelectedTab(7);
                }
                else {
                    basicTabbedPaneUI.navigateSelectedTab(1);
                }
            }
            else if (name == "navigatePageDown") {
                final int tabPlacement2 = tabbedPane.getTabPlacement();
                if (tabPlacement2 == 1 || tabPlacement2 == 3) {
                    basicTabbedPaneUI.navigateSelectedTab(3);
                }
                else {
                    basicTabbedPaneUI.navigateSelectedTab(5);
                }
            }
            else if (name == "requestFocus") {
                tabbedPane.requestFocus();
            }
            else if (name == "requestFocusForVisibleComponent") {
                basicTabbedPaneUI.requestFocusForVisibleComponent();
            }
            else if (name == "setSelectedIndex") {
                final String actionCommand = actionEvent.getActionCommand();
                if (actionCommand != null && actionCommand.length() > 0) {
                    int char1 = actionEvent.getActionCommand().charAt(0);
                    if (char1 >= 97 && char1 <= 122) {
                        char1 -= 32;
                    }
                    final Integer n = basicTabbedPaneUI.mnemonicToIndexMap.get(char1);
                    if (n != null && tabbedPane.isEnabledAt(n)) {
                        tabbedPane.setSelectedIndex(n);
                    }
                }
            }
            else if (name == "selectTabWithFocus") {
                final int focusIndex = basicTabbedPaneUI.getFocusIndex();
                if (focusIndex != -1) {
                    tabbedPane.setSelectedIndex(focusIndex);
                }
            }
            else if (name == "scrollTabsForwardAction") {
                if (basicTabbedPaneUI.scrollableTabLayoutEnabled()) {
                    basicTabbedPaneUI.tabScroller.scrollForward(tabbedPane.getTabPlacement());
                }
            }
            else if (name == "scrollTabsBackwardAction" && basicTabbedPaneUI.scrollableTabLayoutEnabled()) {
                basicTabbedPaneUI.tabScroller.scrollBackward(tabbedPane.getTabPlacement());
            }
        }
    }
    
    public class TabbedPaneLayout implements LayoutManager
    {
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            return this.calculateSize(false);
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            return this.calculateSize(true);
        }
        
        protected Dimension calculateSize(final boolean b) {
            final int tabPlacement = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
            final Insets insets = BasicTabbedPaneUI.this.tabPane.getInsets();
            final Insets contentBorderInsets = BasicTabbedPaneUI.this.getContentBorderInsets(tabPlacement);
            final Insets tabAreaInsets = BasicTabbedPaneUI.this.getTabAreaInsets(tabPlacement);
            final Dimension dimension = new Dimension(0, 0);
            final int n = 0;
            final int n2 = 0;
            int max = 0;
            int max2 = 0;
            for (int i = 0; i < BasicTabbedPaneUI.this.tabPane.getTabCount(); ++i) {
                final Component component = BasicTabbedPaneUI.this.tabPane.getComponentAt(i);
                if (component != null) {
                    final Dimension dimension2 = b ? component.getMinimumSize() : component.getPreferredSize();
                    if (dimension2 != null) {
                        max2 = Math.max(dimension2.height, max2);
                        max = Math.max(dimension2.width, max);
                    }
                }
            }
            final int n3 = n2 + max;
            final int n4 = n + max2;
            int max3 = 0;
            int max4 = 0;
            switch (tabPlacement) {
                case 2:
                case 4: {
                    max3 = Math.max(n4, BasicTabbedPaneUI.this.calculateMaxTabHeight(tabPlacement));
                    max4 = n3 + this.preferredTabAreaWidth(tabPlacement, max3 - tabAreaInsets.top - tabAreaInsets.bottom);
                    break;
                }
                default: {
                    max4 = Math.max(n3, BasicTabbedPaneUI.this.calculateMaxTabWidth(tabPlacement));
                    max3 = n4 + this.preferredTabAreaHeight(tabPlacement, max4 - tabAreaInsets.left - tabAreaInsets.right);
                    break;
                }
            }
            return new Dimension(max4 + insets.left + insets.right + contentBorderInsets.left + contentBorderInsets.right, max3 + insets.bottom + insets.top + contentBorderInsets.top + contentBorderInsets.bottom);
        }
        
        protected int preferredTabAreaHeight(final int n, final int n2) {
            final FontMetrics fontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
            final int tabCount = BasicTabbedPaneUI.this.tabPane.getTabCount();
            int calculateTabAreaHeight = 0;
            if (tabCount > 0) {
                int n3 = 1;
                int n4 = 0;
                final int calculateMaxTabHeight = BasicTabbedPaneUI.this.calculateMaxTabHeight(n);
                for (int i = 0; i < tabCount; ++i) {
                    final int calculateTabWidth = BasicTabbedPaneUI.this.calculateTabWidth(n, i, fontMetrics);
                    if (n4 != 0 && n4 + calculateTabWidth > n2) {
                        ++n3;
                        n4 = 0;
                    }
                    n4 += calculateTabWidth;
                }
                calculateTabAreaHeight = BasicTabbedPaneUI.this.calculateTabAreaHeight(n, n3, calculateMaxTabHeight);
            }
            return calculateTabAreaHeight;
        }
        
        protected int preferredTabAreaWidth(final int n, final int n2) {
            final FontMetrics fontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
            final int tabCount = BasicTabbedPaneUI.this.tabPane.getTabCount();
            int calculateTabAreaWidth = 0;
            if (tabCount > 0) {
                int n3 = 1;
                int n4 = 0;
                final int height = fontMetrics.getHeight();
                BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(n);
                for (int i = 0; i < tabCount; ++i) {
                    final int calculateTabHeight = BasicTabbedPaneUI.this.calculateTabHeight(n, i, height);
                    if (n4 != 0 && n4 + calculateTabHeight > n2) {
                        ++n3;
                        n4 = 0;
                    }
                    n4 += calculateTabHeight;
                }
                calculateTabAreaWidth = BasicTabbedPaneUI.this.calculateTabAreaWidth(n, n3, BasicTabbedPaneUI.this.maxTabWidth);
            }
            return calculateTabAreaWidth;
        }
        
        @Override
        public void layoutContainer(final Container container) {
            BasicTabbedPaneUI.this.setRolloverTab(-1);
            final int tabPlacement = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
            final Insets insets = BasicTabbedPaneUI.this.tabPane.getInsets();
            final int selectedIndex = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
            final Component visibleComponent = BasicTabbedPaneUI.this.getVisibleComponent();
            this.calculateLayoutInfo();
            Component component = null;
            if (selectedIndex < 0) {
                if (visibleComponent != null) {
                    BasicTabbedPaneUI.this.setVisibleComponent(null);
                }
            }
            else {
                component = BasicTabbedPaneUI.this.tabPane.getComponentAt(selectedIndex);
            }
            int n = 0;
            int n2 = 0;
            final Insets contentBorderInsets = BasicTabbedPaneUI.this.getContentBorderInsets(tabPlacement);
            boolean b = false;
            if (component != null) {
                if (component != visibleComponent && visibleComponent != null && SwingUtilities.findFocusOwner(visibleComponent) != null) {
                    b = true;
                }
                BasicTabbedPaneUI.this.setVisibleComponent(component);
            }
            final Rectangle bounds = BasicTabbedPaneUI.this.tabPane.getBounds();
            final int componentCount = BasicTabbedPaneUI.this.tabPane.getComponentCount();
            if (componentCount > 0) {
                int n3 = 0;
                int n4 = 0;
                switch (tabPlacement) {
                    case 2: {
                        n = BasicTabbedPaneUI.this.calculateTabAreaWidth(tabPlacement, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
                        n3 = insets.left + n + contentBorderInsets.left;
                        n4 = insets.top + contentBorderInsets.top;
                        break;
                    }
                    case 4: {
                        n = BasicTabbedPaneUI.this.calculateTabAreaWidth(tabPlacement, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
                        n3 = insets.left + contentBorderInsets.left;
                        n4 = insets.top + contentBorderInsets.top;
                        break;
                    }
                    case 3: {
                        n2 = BasicTabbedPaneUI.this.calculateTabAreaHeight(tabPlacement, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
                        n3 = insets.left + contentBorderInsets.left;
                        n4 = insets.top + contentBorderInsets.top;
                        break;
                    }
                    default: {
                        n2 = BasicTabbedPaneUI.this.calculateTabAreaHeight(tabPlacement, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
                        n3 = insets.left + contentBorderInsets.left;
                        n4 = insets.top + n2 + contentBorderInsets.top;
                        break;
                    }
                }
                final int n5 = bounds.width - n - insets.left - insets.right - contentBorderInsets.left - contentBorderInsets.right;
                final int n6 = bounds.height - n2 - insets.top - insets.bottom - contentBorderInsets.top - contentBorderInsets.bottom;
                for (int i = 0; i < componentCount; ++i) {
                    final Component component2 = BasicTabbedPaneUI.this.tabPane.getComponent(i);
                    if (component2 == BasicTabbedPaneUI.this.tabContainer) {
                        final int n7 = (n == 0) ? bounds.width : (n + insets.left + insets.right + contentBorderInsets.left + contentBorderInsets.right);
                        final int n8 = (n2 == 0) ? bounds.height : (n2 + insets.top + insets.bottom + contentBorderInsets.top + contentBorderInsets.bottom);
                        int n9 = 0;
                        int n10 = 0;
                        if (tabPlacement == 3) {
                            n10 = bounds.height - n8;
                        }
                        else if (tabPlacement == 4) {
                            n9 = bounds.width - n7;
                        }
                        component2.setBounds(n9, n10, n7, n8);
                    }
                    else {
                        component2.setBounds(n3, n4, n5, n6);
                    }
                }
            }
            this.layoutTabComponents();
            if (b && !BasicTabbedPaneUI.this.requestFocusForVisibleComponent()) {
                BasicTabbedPaneUI.this.tabPane.requestFocus();
            }
        }
        
        public void calculateLayoutInfo() {
            final int tabCount = BasicTabbedPaneUI.this.tabPane.getTabCount();
            BasicTabbedPaneUI.this.assureRectsCreated(tabCount);
            this.calculateTabRects(BasicTabbedPaneUI.this.tabPane.getTabPlacement(), tabCount);
            BasicTabbedPaneUI.this.isRunsDirty = false;
        }
        
        private void layoutTabComponents() {
            if (BasicTabbedPaneUI.this.tabContainer == null) {
                return;
            }
            final Rectangle rectangle = new Rectangle();
            final Point point = new Point(-BasicTabbedPaneUI.this.tabContainer.getX(), -BasicTabbedPaneUI.this.tabContainer.getY());
            if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
                BasicTabbedPaneUI.this.translatePointToTabPanel(0, 0, point);
            }
            for (int i = 0; i < BasicTabbedPaneUI.this.tabPane.getTabCount(); ++i) {
                final Component tabComponent = BasicTabbedPaneUI.this.tabPane.getTabComponentAt(i);
                if (tabComponent != null) {
                    BasicTabbedPaneUI.this.getTabBounds(i, rectangle);
                    final Dimension preferredSize = tabComponent.getPreferredSize();
                    final Insets tabInsets = BasicTabbedPaneUI.this.getTabInsets(BasicTabbedPaneUI.this.tabPane.getTabPlacement(), i);
                    final int n = rectangle.x + tabInsets.left + point.x;
                    final int n2 = rectangle.y + tabInsets.top + point.y;
                    final int n3 = rectangle.width - tabInsets.left - tabInsets.right;
                    final int n4 = rectangle.height - tabInsets.top - tabInsets.bottom;
                    final int n5 = n + (n3 - preferredSize.width) / 2;
                    final int n6 = n2 + (n4 - preferredSize.height) / 2;
                    final int tabPlacement = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
                    final boolean b = i == BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
                    tabComponent.setBounds(n5 + BasicTabbedPaneUI.this.getTabLabelShiftX(tabPlacement, i, b), n6 + BasicTabbedPaneUI.this.getTabLabelShiftY(tabPlacement, i, b), preferredSize.width, preferredSize.height);
                }
            }
        }
        
        protected void calculateTabRects(final int n, final int n2) {
            final FontMetrics fontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
            final Dimension size = BasicTabbedPaneUI.this.tabPane.getSize();
            final Insets insets = BasicTabbedPaneUI.this.tabPane.getInsets();
            final Insets tabAreaInsets = BasicTabbedPaneUI.this.getTabAreaInsets(n);
            final int height = fontMetrics.getHeight();
            final int selectedIndex = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
            final boolean b = n == 2 || n == 4;
            final boolean leftToRight = BasicGraphicsUtils.isLeftToRight(BasicTabbedPaneUI.this.tabPane);
            int n3 = 0;
            int n4 = 0;
            int n5 = 0;
            switch (n) {
                case 2: {
                    BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(n);
                    n3 = insets.left + tabAreaInsets.left;
                    n4 = insets.top + tabAreaInsets.top;
                    n5 = size.height - (insets.bottom + tabAreaInsets.bottom);
                    break;
                }
                case 4: {
                    BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(n);
                    n3 = size.width - insets.right - tabAreaInsets.right - BasicTabbedPaneUI.this.maxTabWidth;
                    n4 = insets.top + tabAreaInsets.top;
                    n5 = size.height - (insets.bottom + tabAreaInsets.bottom);
                    break;
                }
                case 3: {
                    BasicTabbedPaneUI.this.maxTabHeight = BasicTabbedPaneUI.this.calculateMaxTabHeight(n);
                    n3 = insets.left + tabAreaInsets.left;
                    n4 = size.height - insets.bottom - tabAreaInsets.bottom - BasicTabbedPaneUI.this.maxTabHeight;
                    n5 = size.width - (insets.right + tabAreaInsets.right);
                    break;
                }
                default: {
                    BasicTabbedPaneUI.this.maxTabHeight = BasicTabbedPaneUI.this.calculateMaxTabHeight(n);
                    n3 = insets.left + tabAreaInsets.left;
                    n4 = insets.top + tabAreaInsets.top;
                    n5 = size.width - (insets.right + tabAreaInsets.right);
                    break;
                }
            }
            final int tabRunOverlay = BasicTabbedPaneUI.this.getTabRunOverlay(n);
            BasicTabbedPaneUI.this.runCount = 0;
            BasicTabbedPaneUI.this.selectedRun = -1;
            if (n2 == 0) {
                return;
            }
            for (int i = 0; i < n2; ++i) {
                final Rectangle rectangle = BasicTabbedPaneUI.this.rects[i];
                if (!b) {
                    if (i > 0) {
                        rectangle.x = BasicTabbedPaneUI.this.rects[i - 1].x + BasicTabbedPaneUI.this.rects[i - 1].width;
                    }
                    else {
                        BasicTabbedPaneUI.this.tabRuns[0] = 0;
                        BasicTabbedPaneUI.this.runCount = 1;
                        BasicTabbedPaneUI.this.maxTabWidth = 0;
                        rectangle.x = n3;
                    }
                    rectangle.width = BasicTabbedPaneUI.this.calculateTabWidth(n, i, fontMetrics);
                    BasicTabbedPaneUI.this.maxTabWidth = Math.max(BasicTabbedPaneUI.this.maxTabWidth, rectangle.width);
                    if (rectangle.x != n3 && rectangle.x + rectangle.width > n5) {
                        if (BasicTabbedPaneUI.this.runCount > BasicTabbedPaneUI.this.tabRuns.length - 1) {
                            BasicTabbedPaneUI.this.expandTabRunsArray();
                        }
                        BasicTabbedPaneUI.this.tabRuns[BasicTabbedPaneUI.this.runCount] = i;
                        final BasicTabbedPaneUI this$0 = BasicTabbedPaneUI.this;
                        ++this$0.runCount;
                        rectangle.x = n3;
                    }
                    rectangle.y = n4;
                    rectangle.height = BasicTabbedPaneUI.this.maxTabHeight;
                }
                else {
                    if (i > 0) {
                        rectangle.y = BasicTabbedPaneUI.this.rects[i - 1].y + BasicTabbedPaneUI.this.rects[i - 1].height;
                    }
                    else {
                        BasicTabbedPaneUI.this.tabRuns[0] = 0;
                        BasicTabbedPaneUI.this.runCount = 1;
                        BasicTabbedPaneUI.this.maxTabHeight = 0;
                        rectangle.y = n4;
                    }
                    rectangle.height = BasicTabbedPaneUI.this.calculateTabHeight(n, i, height);
                    BasicTabbedPaneUI.this.maxTabHeight = Math.max(BasicTabbedPaneUI.this.maxTabHeight, rectangle.height);
                    if (rectangle.y != n4 && rectangle.y + rectangle.height > n5) {
                        if (BasicTabbedPaneUI.this.runCount > BasicTabbedPaneUI.this.tabRuns.length - 1) {
                            BasicTabbedPaneUI.this.expandTabRunsArray();
                        }
                        BasicTabbedPaneUI.this.tabRuns[BasicTabbedPaneUI.this.runCount] = i;
                        final BasicTabbedPaneUI this$2 = BasicTabbedPaneUI.this;
                        ++this$2.runCount;
                        rectangle.y = n4;
                    }
                    rectangle.x = n3;
                    rectangle.width = BasicTabbedPaneUI.this.maxTabWidth;
                }
                if (i == selectedIndex) {
                    BasicTabbedPaneUI.this.selectedRun = BasicTabbedPaneUI.this.runCount - 1;
                }
            }
            if (BasicTabbedPaneUI.this.runCount > 1) {
                this.normalizeTabRuns(n, n2, b ? n4 : n3, n5);
                BasicTabbedPaneUI.this.selectedRun = BasicTabbedPaneUI.this.getRunForTab(n2, selectedIndex);
                if (BasicTabbedPaneUI.this.shouldRotateTabRuns(n)) {
                    this.rotateTabRuns(n, BasicTabbedPaneUI.this.selectedRun);
                }
            }
            for (int j = BasicTabbedPaneUI.this.runCount - 1; j >= 0; --j) {
                final int n6 = BasicTabbedPaneUI.this.tabRuns[j];
                final int n7 = BasicTabbedPaneUI.this.tabRuns[(j == BasicTabbedPaneUI.this.runCount - 1) ? 0 : (j + 1)];
                final int n8 = (n7 != 0) ? (n7 - 1) : (n2 - 1);
                if (!b) {
                    for (int k = n6; k <= n8; ++k) {
                        final Rectangle rectangle2 = BasicTabbedPaneUI.this.rects[k];
                        rectangle2.y = n4;
                        final Rectangle rectangle3 = rectangle2;
                        rectangle3.x += BasicTabbedPaneUI.this.getTabRunIndent(n, j);
                    }
                    if (BasicTabbedPaneUI.this.shouldPadTabRun(n, j)) {
                        this.padTabRun(n, n6, n8, n5);
                    }
                    if (n == 3) {
                        n4 -= BasicTabbedPaneUI.this.maxTabHeight - tabRunOverlay;
                    }
                    else {
                        n4 += BasicTabbedPaneUI.this.maxTabHeight - tabRunOverlay;
                    }
                }
                else {
                    for (int l = n6; l <= n8; ++l) {
                        final Rectangle rectangle4 = BasicTabbedPaneUI.this.rects[l];
                        rectangle4.x = n3;
                        final Rectangle rectangle5 = rectangle4;
                        rectangle5.y += BasicTabbedPaneUI.this.getTabRunIndent(n, j);
                    }
                    if (BasicTabbedPaneUI.this.shouldPadTabRun(n, j)) {
                        this.padTabRun(n, n6, n8, n5);
                    }
                    if (n == 4) {
                        n3 -= BasicTabbedPaneUI.this.maxTabWidth - tabRunOverlay;
                    }
                    else {
                        n3 += BasicTabbedPaneUI.this.maxTabWidth - tabRunOverlay;
                    }
                }
            }
            this.padSelectedTab(n, selectedIndex);
            if (!leftToRight && !b) {
                final int n9 = size.width - (insets.right + tabAreaInsets.right);
                for (int n10 = 0; n10 < n2; ++n10) {
                    BasicTabbedPaneUI.this.rects[n10].x = n9 - BasicTabbedPaneUI.this.rects[n10].x - BasicTabbedPaneUI.this.rects[n10].width;
                }
            }
        }
        
        protected void rotateTabRuns(final int n, final int n2) {
            for (int i = 0; i < n2; ++i) {
                final int n3 = BasicTabbedPaneUI.this.tabRuns[0];
                for (int j = 1; j < BasicTabbedPaneUI.this.runCount; ++j) {
                    BasicTabbedPaneUI.this.tabRuns[j - 1] = BasicTabbedPaneUI.this.tabRuns[j];
                }
                BasicTabbedPaneUI.this.tabRuns[BasicTabbedPaneUI.this.runCount - 1] = n3;
            }
        }
        
        protected void normalizeTabRuns(final int n, final int n2, final int n3, final int n4) {
            final boolean b = n == 2 || n == 4;
            int n5 = BasicTabbedPaneUI.this.runCount - 1;
            int i = 1;
            double n6 = 1.25;
            while (i != 0) {
                final int lastTabInRun = BasicTabbedPaneUI.this.lastTabInRun(n2, n5);
                final int lastTabInRun2 = BasicTabbedPaneUI.this.lastTabInRun(n2, n5 - 1);
                int n7;
                int n8;
                if (!b) {
                    n7 = BasicTabbedPaneUI.this.rects[lastTabInRun].x + BasicTabbedPaneUI.this.rects[lastTabInRun].width;
                    n8 = (int)(BasicTabbedPaneUI.this.maxTabWidth * n6);
                }
                else {
                    n7 = BasicTabbedPaneUI.this.rects[lastTabInRun].y + BasicTabbedPaneUI.this.rects[lastTabInRun].height;
                    n8 = (int)(BasicTabbedPaneUI.this.maxTabHeight * n6 * 2.0);
                }
                if (n4 - n7 > n8) {
                    BasicTabbedPaneUI.this.tabRuns[n5] = lastTabInRun2;
                    if (!b) {
                        BasicTabbedPaneUI.this.rects[lastTabInRun2].x = n3;
                    }
                    else {
                        BasicTabbedPaneUI.this.rects[lastTabInRun2].y = n3;
                    }
                    for (int j = lastTabInRun2 + 1; j <= lastTabInRun; ++j) {
                        if (!b) {
                            BasicTabbedPaneUI.this.rects[j].x = BasicTabbedPaneUI.this.rects[j - 1].x + BasicTabbedPaneUI.this.rects[j - 1].width;
                        }
                        else {
                            BasicTabbedPaneUI.this.rects[j].y = BasicTabbedPaneUI.this.rects[j - 1].y + BasicTabbedPaneUI.this.rects[j - 1].height;
                        }
                    }
                }
                else if (n5 == BasicTabbedPaneUI.this.runCount - 1) {
                    i = 0;
                }
                if (n5 - 1 > 0) {
                    --n5;
                }
                else {
                    n5 = BasicTabbedPaneUI.this.runCount - 1;
                    n6 += 0.25;
                }
            }
        }
        
        protected void padTabRun(final int n, final int n2, final int n3, final int n4) {
            final Rectangle rectangle = BasicTabbedPaneUI.this.rects[n3];
            if (n == 1 || n == 3) {
                final float n5 = (n4 - (rectangle.x + rectangle.width)) / (float)(rectangle.x + rectangle.width - BasicTabbedPaneUI.this.rects[n2].x);
                for (int i = n2; i <= n3; ++i) {
                    final Rectangle rectangle2 = BasicTabbedPaneUI.this.rects[i];
                    if (i > n2) {
                        rectangle2.x = BasicTabbedPaneUI.this.rects[i - 1].x + BasicTabbedPaneUI.this.rects[i - 1].width;
                    }
                    final Rectangle rectangle3 = rectangle2;
                    rectangle3.width += Math.round(rectangle2.width * n5);
                }
                rectangle.width = n4 - rectangle.x;
            }
            else {
                final float n6 = (n4 - (rectangle.y + rectangle.height)) / (float)(rectangle.y + rectangle.height - BasicTabbedPaneUI.this.rects[n2].y);
                for (int j = n2; j <= n3; ++j) {
                    final Rectangle rectangle4 = BasicTabbedPaneUI.this.rects[j];
                    if (j > n2) {
                        rectangle4.y = BasicTabbedPaneUI.this.rects[j - 1].y + BasicTabbedPaneUI.this.rects[j - 1].height;
                    }
                    final Rectangle rectangle5 = rectangle4;
                    rectangle5.height += Math.round(rectangle4.height * n6);
                }
                rectangle.height = n4 - rectangle.y;
            }
        }
        
        protected void padSelectedTab(final int n, final int n2) {
            if (n2 >= 0) {
                final Rectangle rectangle = BasicTabbedPaneUI.this.rects[n2];
                final Insets selectedTabPadInsets = BasicTabbedPaneUI.this.getSelectedTabPadInsets(n);
                final Rectangle rectangle2 = rectangle;
                rectangle2.x -= selectedTabPadInsets.left;
                final Rectangle rectangle3 = rectangle;
                rectangle3.width += selectedTabPadInsets.left + selectedTabPadInsets.right;
                final Rectangle rectangle4 = rectangle;
                rectangle4.y -= selectedTabPadInsets.top;
                final Rectangle rectangle5 = rectangle;
                rectangle5.height += selectedTabPadInsets.top + selectedTabPadInsets.bottom;
                if (!BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
                    final Dimension size = BasicTabbedPaneUI.this.tabPane.getSize();
                    final Insets insets = BasicTabbedPaneUI.this.tabPane.getInsets();
                    if (n == 2 || n == 4) {
                        final int n3 = insets.top - rectangle.y;
                        if (n3 > 0) {
                            final Rectangle rectangle6 = rectangle;
                            rectangle6.y += n3;
                            final Rectangle rectangle7 = rectangle;
                            rectangle7.height -= n3;
                        }
                        final int n4 = rectangle.y + rectangle.height + insets.bottom - size.height;
                        if (n4 > 0) {
                            final Rectangle rectangle8 = rectangle;
                            rectangle8.height -= n4;
                        }
                    }
                    else {
                        final int n5 = insets.left - rectangle.x;
                        if (n5 > 0) {
                            final Rectangle rectangle9 = rectangle;
                            rectangle9.x += n5;
                            final Rectangle rectangle10 = rectangle;
                            rectangle10.width -= n5;
                        }
                        final int n6 = rectangle.x + rectangle.width + insets.right - size.width;
                        if (n6 > 0) {
                            final Rectangle rectangle11 = rectangle;
                            rectangle11.width -= n6;
                        }
                    }
                }
            }
        }
    }
    
    private class TabbedPaneScrollLayout extends TabbedPaneLayout
    {
        @Override
        protected int preferredTabAreaHeight(final int n, final int n2) {
            return BasicTabbedPaneUI.this.calculateMaxTabHeight(n);
        }
        
        @Override
        protected int preferredTabAreaWidth(final int n, final int n2) {
            return BasicTabbedPaneUI.this.calculateMaxTabWidth(n);
        }
        
        @Override
        public void layoutContainer(final Container container) {
            BasicTabbedPaneUI.this.setRolloverTab(-1);
            final int tabPlacement = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
            final int tabCount = BasicTabbedPaneUI.this.tabPane.getTabCount();
            final Insets insets = BasicTabbedPaneUI.this.tabPane.getInsets();
            final int selectedIndex = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
            final Component visibleComponent = BasicTabbedPaneUI.this.getVisibleComponent();
            this.calculateLayoutInfo();
            Component component = null;
            if (selectedIndex < 0) {
                if (visibleComponent != null) {
                    BasicTabbedPaneUI.this.setVisibleComponent(null);
                }
            }
            else {
                component = BasicTabbedPaneUI.this.tabPane.getComponentAt(selectedIndex);
            }
            if (BasicTabbedPaneUI.this.tabPane.getTabCount() == 0) {
                BasicTabbedPaneUI.this.tabScroller.croppedEdge.resetParams();
                BasicTabbedPaneUI.this.tabScroller.scrollForwardButton.setVisible(false);
                BasicTabbedPaneUI.this.tabScroller.scrollBackwardButton.setVisible(false);
                return;
            }
            boolean b = false;
            if (component != null) {
                if (component != visibleComponent && visibleComponent != null && SwingUtilities.findFocusOwner(visibleComponent) != null) {
                    b = true;
                }
                BasicTabbedPaneUI.this.setVisibleComponent(component);
            }
            final Insets contentBorderInsets = BasicTabbedPaneUI.this.getContentBorderInsets(tabPlacement);
            final Rectangle bounds = BasicTabbedPaneUI.this.tabPane.getBounds();
            final int componentCount = BasicTabbedPaneUI.this.tabPane.getComponentCount();
            if (componentCount > 0) {
                int n = 0;
                int n2 = 0;
                int n3 = 0;
                int n4 = 0;
                int n5 = 0;
                int n6 = 0;
                int n7 = 0;
                int n8 = 0;
                switch (tabPlacement) {
                    case 2: {
                        n = BasicTabbedPaneUI.this.calculateTabAreaWidth(tabPlacement, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
                        n2 = bounds.height - insets.top - insets.bottom;
                        n3 = insets.left;
                        n4 = insets.top;
                        n5 = n3 + n + contentBorderInsets.left;
                        n6 = n4 + contentBorderInsets.top;
                        n7 = bounds.width - insets.left - insets.right - n - contentBorderInsets.left - contentBorderInsets.right;
                        n8 = bounds.height - insets.top - insets.bottom - contentBorderInsets.top - contentBorderInsets.bottom;
                        break;
                    }
                    case 4: {
                        n = BasicTabbedPaneUI.this.calculateTabAreaWidth(tabPlacement, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabWidth);
                        n2 = bounds.height - insets.top - insets.bottom;
                        n3 = bounds.width - insets.right - n;
                        n4 = insets.top;
                        n5 = insets.left + contentBorderInsets.left;
                        n6 = insets.top + contentBorderInsets.top;
                        n7 = bounds.width - insets.left - insets.right - n - contentBorderInsets.left - contentBorderInsets.right;
                        n8 = bounds.height - insets.top - insets.bottom - contentBorderInsets.top - contentBorderInsets.bottom;
                        break;
                    }
                    case 3: {
                        n = bounds.width - insets.left - insets.right;
                        n2 = BasicTabbedPaneUI.this.calculateTabAreaHeight(tabPlacement, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
                        n3 = insets.left;
                        n4 = bounds.height - insets.bottom - n2;
                        n5 = insets.left + contentBorderInsets.left;
                        n6 = insets.top + contentBorderInsets.top;
                        n7 = bounds.width - insets.left - insets.right - contentBorderInsets.left - contentBorderInsets.right;
                        n8 = bounds.height - insets.top - insets.bottom - n2 - contentBorderInsets.top - contentBorderInsets.bottom;
                        break;
                    }
                    default: {
                        n = bounds.width - insets.left - insets.right;
                        n2 = BasicTabbedPaneUI.this.calculateTabAreaHeight(tabPlacement, BasicTabbedPaneUI.this.runCount, BasicTabbedPaneUI.this.maxTabHeight);
                        n3 = insets.left;
                        n4 = insets.top;
                        n5 = n3 + contentBorderInsets.left;
                        n6 = n4 + n2 + contentBorderInsets.top;
                        n7 = bounds.width - insets.left - insets.right - contentBorderInsets.left - contentBorderInsets.right;
                        n8 = bounds.height - insets.top - insets.bottom - n2 - contentBorderInsets.top - contentBorderInsets.bottom;
                        break;
                    }
                }
                for (int i = 0; i < componentCount; ++i) {
                    final Component component2 = BasicTabbedPaneUI.this.tabPane.getComponent(i);
                    if (BasicTabbedPaneUI.this.tabScroller != null && component2 == BasicTabbedPaneUI.this.tabScroller.viewport) {
                        final Rectangle viewRect = ((JViewport)component2).getViewRect();
                        int n9 = n;
                        int n10 = n2;
                        final Dimension preferredSize = BasicTabbedPaneUI.this.tabScroller.scrollForwardButton.getPreferredSize();
                        switch (tabPlacement) {
                            case 2:
                            case 4: {
                                final int n11 = BasicTabbedPaneUI.this.rects[tabCount - 1].y + BasicTabbedPaneUI.this.rects[tabCount - 1].height;
                                if (n11 <= n2) {
                                    break;
                                }
                                n10 = ((n2 > 2 * preferredSize.height) ? (n2 - 2 * preferredSize.height) : 0);
                                if (n11 - viewRect.y <= n10) {
                                    n10 = n11 - viewRect.y;
                                    break;
                                }
                                break;
                            }
                            default: {
                                final int n12 = BasicTabbedPaneUI.this.rects[tabCount - 1].x + BasicTabbedPaneUI.this.rects[tabCount - 1].width;
                                if (n12 <= n) {
                                    break;
                                }
                                n9 = ((n > 2 * preferredSize.width) ? (n - 2 * preferredSize.width) : 0);
                                if (n12 - viewRect.x <= n9) {
                                    n9 = n12 - viewRect.x;
                                    break;
                                }
                                break;
                            }
                        }
                        component2.setBounds(n3, n4, n9, n10);
                    }
                    else if (BasicTabbedPaneUI.this.tabScroller != null && (component2 == BasicTabbedPaneUI.this.tabScroller.scrollForwardButton || component2 == BasicTabbedPaneUI.this.tabScroller.scrollBackwardButton)) {
                        final Dimension preferredSize2 = component2.getPreferredSize();
                        int n13 = 0;
                        int n14 = 0;
                        final int width = preferredSize2.width;
                        final int height = preferredSize2.height;
                        boolean visible = false;
                        switch (tabPlacement) {
                            case 2:
                            case 4: {
                                if (BasicTabbedPaneUI.this.rects[tabCount - 1].y + BasicTabbedPaneUI.this.rects[tabCount - 1].height > n2) {
                                    visible = true;
                                    n13 = ((tabPlacement == 2) ? (n3 + n - preferredSize2.width) : n3);
                                    n14 = ((component2 == BasicTabbedPaneUI.this.tabScroller.scrollForwardButton) ? (bounds.height - insets.bottom - preferredSize2.height) : (bounds.height - insets.bottom - 2 * preferredSize2.height));
                                    break;
                                }
                                break;
                            }
                            default: {
                                if (BasicTabbedPaneUI.this.rects[tabCount - 1].x + BasicTabbedPaneUI.this.rects[tabCount - 1].width > n) {
                                    visible = true;
                                    n13 = ((component2 == BasicTabbedPaneUI.this.tabScroller.scrollForwardButton) ? (bounds.width - insets.left - preferredSize2.width) : (bounds.width - insets.left - 2 * preferredSize2.width));
                                    n14 = ((tabPlacement == 1) ? (n4 + n2 - preferredSize2.height) : n4);
                                    break;
                                }
                                break;
                            }
                        }
                        component2.setVisible(visible);
                        if (visible) {
                            component2.setBounds(n13, n14, width, height);
                        }
                    }
                    else {
                        component2.setBounds(n5, n6, n7, n8);
                    }
                }
                TabbedPaneLayout.this.layoutTabComponents();
                this.layoutCroppedEdge();
                if (b && !BasicTabbedPaneUI.this.requestFocusForVisibleComponent()) {
                    BasicTabbedPaneUI.this.tabPane.requestFocus();
                }
            }
        }
        
        private void layoutCroppedEdge() {
            BasicTabbedPaneUI.this.tabScroller.croppedEdge.resetParams();
            final Rectangle viewRect = BasicTabbedPaneUI.this.tabScroller.viewport.getViewRect();
            for (int i = 0; i < BasicTabbedPaneUI.this.rects.length; ++i) {
                final Rectangle rectangle = BasicTabbedPaneUI.this.rects[i];
                switch (BasicTabbedPaneUI.this.tabPane.getTabPlacement()) {
                    case 2:
                    case 4: {
                        final int n = viewRect.y + viewRect.height;
                        if (rectangle.y < n && rectangle.y + rectangle.height > n) {
                            BasicTabbedPaneUI.this.tabScroller.croppedEdge.setParams(i, n - rectangle.y - 1, -BasicTabbedPaneUI.this.currentTabAreaInsets.left, 0);
                            break;
                        }
                        break;
                    }
                    default: {
                        final int n2 = viewRect.x + viewRect.width;
                        if (rectangle.x < n2 - 1 && rectangle.x + rectangle.width > n2) {
                            BasicTabbedPaneUI.this.tabScroller.croppedEdge.setParams(i, n2 - rectangle.x - 1, 0, -BasicTabbedPaneUI.this.currentTabAreaInsets.top);
                            break;
                        }
                        break;
                    }
                }
            }
        }
        
        @Override
        protected void calculateTabRects(final int n, final int n2) {
            final FontMetrics fontMetrics = BasicTabbedPaneUI.this.getFontMetrics();
            final Dimension size = BasicTabbedPaneUI.this.tabPane.getSize();
            final Insets insets = BasicTabbedPaneUI.this.tabPane.getInsets();
            final Insets tabAreaInsets = BasicTabbedPaneUI.this.getTabAreaInsets(n);
            final int height = fontMetrics.getHeight();
            final int selectedIndex = BasicTabbedPaneUI.this.tabPane.getSelectedIndex();
            final boolean b = n == 2 || n == 4;
            final boolean leftToRight = BasicGraphicsUtils.isLeftToRight(BasicTabbedPaneUI.this.tabPane);
            final int left = tabAreaInsets.left;
            final int top = tabAreaInsets.top;
            int maxTabWidth = 0;
            int n3 = 0;
            switch (n) {
                case 2:
                case 4: {
                    BasicTabbedPaneUI.this.maxTabWidth = BasicTabbedPaneUI.this.calculateMaxTabWidth(n);
                    break;
                }
                default: {
                    BasicTabbedPaneUI.this.maxTabHeight = BasicTabbedPaneUI.this.calculateMaxTabHeight(n);
                    break;
                }
            }
            BasicTabbedPaneUI.this.runCount = 0;
            BasicTabbedPaneUI.this.selectedRun = -1;
            if (n2 == 0) {
                return;
            }
            BasicTabbedPaneUI.this.selectedRun = 0;
            BasicTabbedPaneUI.this.runCount = 1;
            for (int i = 0; i < n2; ++i) {
                final Rectangle rectangle = BasicTabbedPaneUI.this.rects[i];
                if (!b) {
                    if (i > 0) {
                        rectangle.x = BasicTabbedPaneUI.this.rects[i - 1].x + BasicTabbedPaneUI.this.rects[i - 1].width;
                    }
                    else {
                        BasicTabbedPaneUI.this.tabRuns[0] = 0;
                        BasicTabbedPaneUI.this.maxTabWidth = 0;
                        n3 += BasicTabbedPaneUI.this.maxTabHeight;
                        rectangle.x = left;
                    }
                    rectangle.width = BasicTabbedPaneUI.this.calculateTabWidth(n, i, fontMetrics);
                    maxTabWidth = rectangle.x + rectangle.width;
                    BasicTabbedPaneUI.this.maxTabWidth = Math.max(BasicTabbedPaneUI.this.maxTabWidth, rectangle.width);
                    rectangle.y = top;
                    rectangle.height = BasicTabbedPaneUI.this.maxTabHeight;
                }
                else {
                    if (i > 0) {
                        rectangle.y = BasicTabbedPaneUI.this.rects[i - 1].y + BasicTabbedPaneUI.this.rects[i - 1].height;
                    }
                    else {
                        BasicTabbedPaneUI.this.tabRuns[0] = 0;
                        BasicTabbedPaneUI.this.maxTabHeight = 0;
                        maxTabWidth = BasicTabbedPaneUI.this.maxTabWidth;
                        rectangle.y = top;
                    }
                    rectangle.height = BasicTabbedPaneUI.this.calculateTabHeight(n, i, height);
                    n3 = rectangle.y + rectangle.height;
                    BasicTabbedPaneUI.this.maxTabHeight = Math.max(BasicTabbedPaneUI.this.maxTabHeight, rectangle.height);
                    rectangle.x = left;
                    rectangle.width = BasicTabbedPaneUI.this.maxTabWidth;
                }
            }
            if (BasicTabbedPaneUI.this.tabsOverlapBorder) {
                this.padSelectedTab(n, selectedIndex);
            }
            if (!leftToRight && !b) {
                final int n4 = size.width - (insets.right + tabAreaInsets.right);
                for (int j = 0; j < n2; ++j) {
                    BasicTabbedPaneUI.this.rects[j].x = n4 - BasicTabbedPaneUI.this.rects[j].x - BasicTabbedPaneUI.this.rects[j].width;
                }
            }
            BasicTabbedPaneUI.this.tabScroller.tabPanel.setPreferredSize(new Dimension(maxTabWidth, n3));
            BasicTabbedPaneUI.this.tabScroller.tabPanel.invalidate();
        }
    }
    
    private class ScrollableTabSupport implements ActionListener, ChangeListener
    {
        public ScrollableTabViewport viewport;
        public ScrollableTabPanel tabPanel;
        public JButton scrollForwardButton;
        public JButton scrollBackwardButton;
        public CroppedEdge croppedEdge;
        public int leadingTabIndex;
        private Point tabViewPosition;
        
        ScrollableTabSupport(final int n) {
            this.tabViewPosition = new Point(0, 0);
            this.viewport = new ScrollableTabViewport();
            this.tabPanel = new ScrollableTabPanel();
            this.viewport.setView(this.tabPanel);
            this.viewport.addChangeListener(this);
            this.croppedEdge = new CroppedEdge();
            this.createButtons();
        }
        
        void createButtons() {
            if (this.scrollForwardButton != null) {
                BasicTabbedPaneUI.this.tabPane.remove(this.scrollForwardButton);
                this.scrollForwardButton.removeActionListener(this);
                BasicTabbedPaneUI.this.tabPane.remove(this.scrollBackwardButton);
                this.scrollBackwardButton.removeActionListener(this);
            }
            final int tabPlacement = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
            if (tabPlacement == 1 || tabPlacement == 3) {
                this.scrollForwardButton = BasicTabbedPaneUI.this.createScrollButton(3);
                this.scrollBackwardButton = BasicTabbedPaneUI.this.createScrollButton(7);
            }
            else {
                this.scrollForwardButton = BasicTabbedPaneUI.this.createScrollButton(5);
                this.scrollBackwardButton = BasicTabbedPaneUI.this.createScrollButton(1);
            }
            this.scrollForwardButton.addActionListener(this);
            this.scrollBackwardButton.addActionListener(this);
            BasicTabbedPaneUI.this.tabPane.add(this.scrollForwardButton);
            BasicTabbedPaneUI.this.tabPane.add(this.scrollBackwardButton);
        }
        
        public void scrollForward(final int n) {
            final Dimension viewSize = this.viewport.getViewSize();
            final Rectangle viewRect = this.viewport.getViewRect();
            if (n == 1 || n == 3) {
                if (viewRect.width >= viewSize.width - viewRect.x) {
                    return;
                }
            }
            else if (viewRect.height >= viewSize.height - viewRect.y) {
                return;
            }
            this.setLeadingTabIndex(n, this.leadingTabIndex + 1);
        }
        
        public void scrollBackward(final int n) {
            if (this.leadingTabIndex == 0) {
                return;
            }
            this.setLeadingTabIndex(n, this.leadingTabIndex - 1);
        }
        
        public void setLeadingTabIndex(final int n, final int leadingTabIndex) {
            this.leadingTabIndex = leadingTabIndex;
            final Dimension viewSize = this.viewport.getViewSize();
            final Rectangle viewRect = this.viewport.getViewRect();
            switch (n) {
                case 1:
                case 3: {
                    this.tabViewPosition.x = ((this.leadingTabIndex == 0) ? 0 : BasicTabbedPaneUI.this.rects[this.leadingTabIndex].x);
                    if (viewSize.width - this.tabViewPosition.x < viewRect.width) {
                        this.viewport.setExtentSize(new Dimension(viewSize.width - this.tabViewPosition.x, viewRect.height));
                        break;
                    }
                    break;
                }
                case 2:
                case 4: {
                    this.tabViewPosition.y = ((this.leadingTabIndex == 0) ? 0 : BasicTabbedPaneUI.this.rects[this.leadingTabIndex].y);
                    if (viewSize.height - this.tabViewPosition.y < viewRect.height) {
                        this.viewport.setExtentSize(new Dimension(viewRect.width, viewSize.height - this.tabViewPosition.y));
                        break;
                    }
                    break;
                }
            }
            this.viewport.setViewPosition(this.tabViewPosition);
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            this.updateView();
        }
        
        private void updateView() {
            final int tabPlacement = BasicTabbedPaneUI.this.tabPane.getTabPlacement();
            final int tabCount = BasicTabbedPaneUI.this.tabPane.getTabCount();
            BasicTabbedPaneUI.this.assureRectsCreated(tabCount);
            final Rectangle bounds = this.viewport.getBounds();
            final Dimension viewSize = this.viewport.getViewSize();
            final Rectangle viewRect = this.viewport.getViewRect();
            this.leadingTabIndex = BasicTabbedPaneUI.this.getClosestTab(viewRect.x, viewRect.y);
            if (this.leadingTabIndex + 1 < tabCount) {
                switch (tabPlacement) {
                    case 1:
                    case 3: {
                        if (BasicTabbedPaneUI.this.rects[this.leadingTabIndex].x < viewRect.x) {
                            ++this.leadingTabIndex;
                            break;
                        }
                        break;
                    }
                    case 2:
                    case 4: {
                        if (BasicTabbedPaneUI.this.rects[this.leadingTabIndex].y < viewRect.y) {
                            ++this.leadingTabIndex;
                            break;
                        }
                        break;
                    }
                }
            }
            final Insets contentBorderInsets = BasicTabbedPaneUI.this.getContentBorderInsets(tabPlacement);
            switch (tabPlacement) {
                case 2: {
                    BasicTabbedPaneUI.this.tabPane.repaint(bounds.x + bounds.width, bounds.y, contentBorderInsets.left, bounds.height);
                    this.scrollBackwardButton.setEnabled(viewRect.y > 0 && this.leadingTabIndex > 0);
                    this.scrollForwardButton.setEnabled(this.leadingTabIndex < tabCount - 1 && viewSize.height - viewRect.y > viewRect.height);
                    break;
                }
                case 4: {
                    BasicTabbedPaneUI.this.tabPane.repaint(bounds.x - contentBorderInsets.right, bounds.y, contentBorderInsets.right, bounds.height);
                    this.scrollBackwardButton.setEnabled(viewRect.y > 0 && this.leadingTabIndex > 0);
                    this.scrollForwardButton.setEnabled(this.leadingTabIndex < tabCount - 1 && viewSize.height - viewRect.y > viewRect.height);
                    break;
                }
                case 3: {
                    BasicTabbedPaneUI.this.tabPane.repaint(bounds.x, bounds.y - contentBorderInsets.bottom, bounds.width, contentBorderInsets.bottom);
                    this.scrollBackwardButton.setEnabled(viewRect.x > 0 && this.leadingTabIndex > 0);
                    this.scrollForwardButton.setEnabled(this.leadingTabIndex < tabCount - 1 && viewSize.width - viewRect.x > viewRect.width);
                    break;
                }
                default: {
                    BasicTabbedPaneUI.this.tabPane.repaint(bounds.x, bounds.y + bounds.height, bounds.width, contentBorderInsets.top);
                    this.scrollBackwardButton.setEnabled(viewRect.x > 0 && this.leadingTabIndex > 0);
                    this.scrollForwardButton.setEnabled(this.leadingTabIndex < tabCount - 1 && viewSize.width - viewRect.x > viewRect.width);
                    break;
                }
            }
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final ActionMap actionMap = BasicTabbedPaneUI.this.tabPane.getActionMap();
            if (actionMap != null) {
                String s;
                if (actionEvent.getSource() == this.scrollForwardButton) {
                    s = "scrollTabsForwardAction";
                }
                else {
                    s = "scrollTabsBackwardAction";
                }
                final Action value = actionMap.get(s);
                if (value != null && value.isEnabled()) {
                    value.actionPerformed(new ActionEvent(BasicTabbedPaneUI.this.tabPane, 1001, null, actionEvent.getWhen(), actionEvent.getModifiers()));
                }
            }
        }
        
        @Override
        public String toString() {
            return "viewport.viewSize=" + this.viewport.getViewSize() + "\nviewport.viewRectangle=" + this.viewport.getViewRect() + "\nleadingTabIndex=" + this.leadingTabIndex + "\ntabViewPosition=" + this.tabViewPosition;
        }
    }
    
    private class ScrollableTabViewport extends JViewport implements UIResource
    {
        public ScrollableTabViewport() {
            this.setName("TabbedPane.scrollableViewport");
            this.setScrollMode(0);
            this.setOpaque(BasicTabbedPaneUI.this.tabPane.isOpaque());
            Color background = UIManager.getColor("TabbedPane.tabAreaBackground");
            if (background == null) {
                background = BasicTabbedPaneUI.this.tabPane.getBackground();
            }
            this.setBackground(background);
        }
    }
    
    private class ScrollableTabPanel extends JPanel implements UIResource
    {
        public ScrollableTabPanel() {
            super(null);
            this.setOpaque(BasicTabbedPaneUI.this.tabPane.isOpaque());
            Color background = UIManager.getColor("TabbedPane.tabAreaBackground");
            if (background == null) {
                background = BasicTabbedPaneUI.this.tabPane.getBackground();
            }
            this.setBackground(background);
        }
        
        public void paintComponent(final Graphics graphics) {
            super.paintComponent(graphics);
            BasicTabbedPaneUI.this.paintTabArea(graphics, BasicTabbedPaneUI.this.tabPane.getTabPlacement(), BasicTabbedPaneUI.this.tabPane.getSelectedIndex());
            if (BasicTabbedPaneUI.this.tabScroller.croppedEdge.isParamsSet() && BasicTabbedPaneUI.this.tabContainer == null) {
                final Rectangle rectangle = BasicTabbedPaneUI.this.rects[BasicTabbedPaneUI.this.tabScroller.croppedEdge.getTabIndex()];
                graphics.translate(rectangle.x, rectangle.y);
                BasicTabbedPaneUI.this.tabScroller.croppedEdge.paintComponent(graphics);
                graphics.translate(-rectangle.x, -rectangle.y);
            }
        }
        
        @Override
        public void doLayout() {
            if (this.getComponentCount() > 0) {
                this.getComponent(0).setBounds(0, 0, this.getWidth(), this.getHeight());
            }
        }
    }
    
    private class ScrollableTabButton extends BasicArrowButton implements UIResource, SwingConstants
    {
        public ScrollableTabButton(final int n) {
            super(n, UIManager.getColor("TabbedPane.selected"), UIManager.getColor("TabbedPane.shadow"), UIManager.getColor("TabbedPane.darkShadow"), UIManager.getColor("TabbedPane.highlight"));
        }
    }
    
    private class Handler implements ChangeListener, ContainerListener, FocusListener, MouseListener, MouseMotionListener, PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final JTabbedPane tabbedPane = (JTabbedPane)propertyChangeEvent.getSource();
            final String propertyName = propertyChangeEvent.getPropertyName();
            final boolean access$400 = BasicTabbedPaneUI.this.scrollableTabLayoutEnabled();
            if (propertyName == "mnemonicAt") {
                BasicTabbedPaneUI.this.updateMnemonics();
                tabbedPane.repaint();
            }
            else if (propertyName == "displayedMnemonicIndexAt") {
                tabbedPane.repaint();
            }
            else if (propertyName == "indexForTitle") {
                BasicTabbedPaneUI.this.calculatedBaseline = false;
                final Integer n = (Integer)propertyChangeEvent.getNewValue();
                if (BasicTabbedPaneUI.this.htmlViews != null) {
                    BasicTabbedPaneUI.this.htmlViews.removeElementAt(n);
                }
                this.updateHtmlViews(n);
            }
            else if (propertyName == "tabLayoutPolicy") {
                BasicTabbedPaneUI.this.uninstallUI(tabbedPane);
                BasicTabbedPaneUI.this.installUI(tabbedPane);
                BasicTabbedPaneUI.this.calculatedBaseline = false;
            }
            else if (propertyName == "tabPlacement") {
                if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
                    BasicTabbedPaneUI.this.tabScroller.createButtons();
                }
                BasicTabbedPaneUI.this.calculatedBaseline = false;
            }
            else if (propertyName == "opaque" && access$400) {
                final boolean booleanValue = (boolean)propertyChangeEvent.getNewValue();
                BasicTabbedPaneUI.this.tabScroller.tabPanel.setOpaque(booleanValue);
                BasicTabbedPaneUI.this.tabScroller.viewport.setOpaque(booleanValue);
            }
            else if (propertyName == "background" && access$400) {
                final Color color = (Color)propertyChangeEvent.getNewValue();
                BasicTabbedPaneUI.this.tabScroller.tabPanel.setBackground(color);
                BasicTabbedPaneUI.this.tabScroller.viewport.setBackground(color);
                final Color color2 = (BasicTabbedPaneUI.this.selectedColor == null) ? color : BasicTabbedPaneUI.this.selectedColor;
                BasicTabbedPaneUI.this.tabScroller.scrollForwardButton.setBackground(color2);
                BasicTabbedPaneUI.this.tabScroller.scrollBackwardButton.setBackground(color2);
            }
            else if (propertyName == "indexForTabComponent") {
                if (BasicTabbedPaneUI.this.tabContainer != null) {
                    BasicTabbedPaneUI.this.tabContainer.removeUnusedTabComponents();
                }
                final Component tabComponent = BasicTabbedPaneUI.this.tabPane.getTabComponentAt((int)propertyChangeEvent.getNewValue());
                if (tabComponent != null) {
                    if (BasicTabbedPaneUI.this.tabContainer == null) {
                        BasicTabbedPaneUI.this.installTabContainer();
                    }
                    else {
                        BasicTabbedPaneUI.this.tabContainer.add(tabComponent);
                    }
                }
                BasicTabbedPaneUI.this.tabPane.revalidate();
                BasicTabbedPaneUI.this.tabPane.repaint();
                BasicTabbedPaneUI.this.calculatedBaseline = false;
            }
            else if (propertyName == "indexForNullComponent") {
                BasicTabbedPaneUI.this.isRunsDirty = true;
                this.updateHtmlViews((int)propertyChangeEvent.getNewValue());
            }
            else if (propertyName == "font") {
                BasicTabbedPaneUI.this.calculatedBaseline = false;
            }
        }
        
        private void updateHtmlViews(final int n) {
            final String title = BasicTabbedPaneUI.this.tabPane.getTitleAt(n);
            if (BasicHTML.isHTMLString(title)) {
                if (BasicTabbedPaneUI.this.htmlViews == null) {
                    BasicTabbedPaneUI.this.htmlViews = BasicTabbedPaneUI.this.createHTMLVector();
                }
                else {
                    BasicTabbedPaneUI.this.htmlViews.insertElementAt(BasicHTML.createHTMLView(BasicTabbedPaneUI.this.tabPane, title), n);
                }
            }
            else if (BasicTabbedPaneUI.this.htmlViews != null) {
                BasicTabbedPaneUI.this.htmlViews.insertElementAt(null, n);
            }
            BasicTabbedPaneUI.this.updateMnemonics();
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            final JTabbedPane tabbedPane = (JTabbedPane)changeEvent.getSource();
            tabbedPane.revalidate();
            tabbedPane.repaint();
            BasicTabbedPaneUI.this.setFocusIndex(tabbedPane.getSelectedIndex(), false);
            if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
                BasicTabbedPaneUI.this.ensureCurrentLayout();
                final int selectedIndex = tabbedPane.getSelectedIndex();
                if (selectedIndex < BasicTabbedPaneUI.this.rects.length && selectedIndex != -1) {
                    BasicTabbedPaneUI.this.tabScroller.tabPanel.scrollRectToVisible((Rectangle)BasicTabbedPaneUI.this.rects[selectedIndex].clone());
                }
            }
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            BasicTabbedPaneUI.this.setRolloverTab(mouseEvent.getX(), mouseEvent.getY());
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            BasicTabbedPaneUI.this.setRolloverTab(-1);
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (!BasicTabbedPaneUI.this.tabPane.isEnabled()) {
                return;
            }
            final int tabForCoordinate = BasicTabbedPaneUI.this.tabForCoordinate(BasicTabbedPaneUI.this.tabPane, mouseEvent.getX(), mouseEvent.getY());
            if (tabForCoordinate >= 0 && BasicTabbedPaneUI.this.tabPane.isEnabledAt(tabForCoordinate)) {
                if (tabForCoordinate != BasicTabbedPaneUI.this.tabPane.getSelectedIndex()) {
                    BasicTabbedPaneUI.this.tabPane.setSelectedIndex(tabForCoordinate);
                }
                else if (BasicTabbedPaneUI.this.tabPane.isRequestFocusEnabled()) {
                    BasicTabbedPaneUI.this.tabPane.requestFocus();
                }
            }
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            BasicTabbedPaneUI.this.setRolloverTab(mouseEvent.getX(), mouseEvent.getY());
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            BasicTabbedPaneUI.this.setFocusIndex(BasicTabbedPaneUI.this.tabPane.getSelectedIndex(), true);
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            BasicTabbedPaneUI.this.repaintTab(BasicTabbedPaneUI.this.focusIndex);
        }
        
        @Override
        public void componentAdded(final ContainerEvent containerEvent) {
            final JTabbedPane tabbedPane = (JTabbedPane)containerEvent.getContainer();
            final Component child = containerEvent.getChild();
            if (child instanceof UIResource) {
                return;
            }
            BasicTabbedPaneUI.this.isRunsDirty = true;
            this.updateHtmlViews(tabbedPane.indexOfComponent(child));
        }
        
        @Override
        public void componentRemoved(final ContainerEvent containerEvent) {
            final JTabbedPane tabbedPane = (JTabbedPane)containerEvent.getContainer();
            if (containerEvent.getChild() instanceof UIResource) {
                return;
            }
            final Integer n = (Integer)tabbedPane.getClientProperty("__index_to_remove__");
            if (n != null) {
                final int intValue = n;
                if (BasicTabbedPaneUI.this.htmlViews != null && BasicTabbedPaneUI.this.htmlViews.size() > intValue) {
                    BasicTabbedPaneUI.this.htmlViews.removeElementAt(intValue);
                }
                tabbedPane.putClientProperty("__index_to_remove__", null);
            }
            BasicTabbedPaneUI.this.isRunsDirty = true;
            BasicTabbedPaneUI.this.updateMnemonics();
            BasicTabbedPaneUI.this.validateFocusIndex();
        }
    }
    
    public class PropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicTabbedPaneUI.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    public class TabSelectionHandler implements ChangeListener
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            BasicTabbedPaneUI.this.getHandler().stateChanged(changeEvent);
        }
    }
    
    public class MouseHandler extends MouseAdapter
    {
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            BasicTabbedPaneUI.this.getHandler().mousePressed(mouseEvent);
        }
    }
    
    public class FocusHandler extends FocusAdapter
    {
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            BasicTabbedPaneUI.this.getHandler().focusGained(focusEvent);
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            BasicTabbedPaneUI.this.getHandler().focusLost(focusEvent);
        }
    }
    
    private class TabContainer extends JPanel implements UIResource
    {
        private boolean notifyTabbedPane;
        
        public TabContainer() {
            super(null);
            this.notifyTabbedPane = true;
            this.setOpaque(false);
        }
        
        @Override
        public void remove(final Component component) {
            final int indexOfTabComponent = BasicTabbedPaneUI.this.tabPane.indexOfTabComponent(component);
            super.remove(component);
            if (this.notifyTabbedPane && indexOfTabComponent != -1) {
                BasicTabbedPaneUI.this.tabPane.setTabComponentAt(indexOfTabComponent, null);
            }
        }
        
        private void removeUnusedTabComponents() {
            for (final Component component : this.getComponents()) {
                if (!(component instanceof UIResource) && BasicTabbedPaneUI.this.tabPane.indexOfTabComponent(component) == -1) {
                    super.remove(component);
                }
            }
        }
        
        @Override
        public boolean isOptimizedDrawingEnabled() {
            return BasicTabbedPaneUI.this.tabScroller != null && !BasicTabbedPaneUI.this.tabScroller.croppedEdge.isParamsSet();
        }
        
        @Override
        public void doLayout() {
            if (BasicTabbedPaneUI.this.scrollableTabLayoutEnabled()) {
                BasicTabbedPaneUI.this.tabScroller.tabPanel.repaint();
                BasicTabbedPaneUI.this.tabScroller.updateView();
            }
            else {
                BasicTabbedPaneUI.this.tabPane.repaint(this.getBounds());
            }
        }
    }
    
    private class CroppedEdge extends JPanel implements UIResource
    {
        private Shape shape;
        private int tabIndex;
        private int cropline;
        private int cropx;
        private int cropy;
        
        public CroppedEdge() {
            this.setOpaque(false);
        }
        
        public void setParams(final int tabIndex, final int cropline, final int cropx, final int cropy) {
            this.tabIndex = tabIndex;
            this.cropline = cropline;
            this.cropx = cropx;
            this.cropy = cropy;
            final Rectangle bounds = BasicTabbedPaneUI.this.rects[tabIndex];
            this.setBounds(bounds);
            this.shape = createCroppedTabShape(BasicTabbedPaneUI.this.tabPane.getTabPlacement(), bounds, cropline);
            if (this.getParent() == null && BasicTabbedPaneUI.this.tabContainer != null) {
                BasicTabbedPaneUI.this.tabContainer.add(this, 0);
            }
        }
        
        public void resetParams() {
            this.shape = null;
            if (this.getParent() == BasicTabbedPaneUI.this.tabContainer && BasicTabbedPaneUI.this.tabContainer != null) {
                BasicTabbedPaneUI.this.tabContainer.remove(this);
            }
        }
        
        public boolean isParamsSet() {
            return this.shape != null;
        }
        
        public int getTabIndex() {
            return this.tabIndex;
        }
        
        public int getCropline() {
            return this.cropline;
        }
        
        public int getCroppedSideWidth() {
            return 3;
        }
        
        private Color getBgColor() {
            final Container parent = BasicTabbedPaneUI.this.tabPane.getParent();
            if (parent != null) {
                final Color background = parent.getBackground();
                if (background != null) {
                    return background;
                }
            }
            return UIManager.getColor("control");
        }
        
        @Override
        protected void paintComponent(final Graphics graphics) {
            super.paintComponent(graphics);
            if (this.isParamsSet() && graphics instanceof Graphics2D) {
                final Graphics2D graphics2D = (Graphics2D)graphics;
                graphics2D.clipRect(0, 0, this.getWidth(), this.getHeight());
                graphics2D.setColor(this.getBgColor());
                graphics2D.translate(this.cropx, this.cropy);
                graphics2D.fill(this.shape);
                BasicTabbedPaneUI.this.paintCroppedTabEdge(graphics);
                graphics2D.translate(-this.cropx, -this.cropy);
            }
        }
    }
}
