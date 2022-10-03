package javax.swing.plaf.synth;

import javax.swing.plaf.UIResource;
import java.awt.LayoutManager;
import java.awt.Shape;
import javax.swing.text.View;
import java.awt.Font;
import sun.swing.SwingUtilities2;
import javax.swing.Icon;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.JButton;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.JTabbedPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class SynthTabbedPaneUI extends BasicTabbedPaneUI implements PropertyChangeListener, SynthUI
{
    private int tabOverlap;
    private boolean extendTabsToBase;
    private SynthContext tabAreaContext;
    private SynthContext tabContext;
    private SynthContext tabContentContext;
    private SynthStyle style;
    private SynthStyle tabStyle;
    private SynthStyle tabAreaStyle;
    private SynthStyle tabContentStyle;
    private Rectangle textRect;
    private Rectangle iconRect;
    private Rectangle tabAreaBounds;
    private boolean tabAreaStatesMatchSelectedTab;
    private boolean nudgeSelectedLabel;
    private boolean selectedTabIsPressed;
    
    public SynthTabbedPaneUI() {
        this.tabOverlap = 0;
        this.extendTabsToBase = false;
        this.textRect = new Rectangle();
        this.iconRect = new Rectangle();
        this.tabAreaBounds = new Rectangle();
        this.tabAreaStatesMatchSelectedTab = false;
        this.nudgeSelectedLabel = true;
        this.selectedTabIsPressed = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new SynthTabbedPaneUI();
    }
    
    private boolean scrollableTabLayoutEnabled() {
        return this.tabPane.getTabLayoutPolicy() == 1;
    }
    
    @Override
    protected void installDefaults() {
        this.updateStyle(this.tabPane);
    }
    
    private void updateStyle(final JTabbedPane tabbedPane) {
        final SynthContext context = this.getContext(tabbedPane, 1);
        final SynthStyle style = this.style;
        this.style = SynthLookAndFeel.updateStyle(context, this);
        if (this.style != style) {
            this.tabRunOverlay = this.style.getInt(context, "TabbedPane.tabRunOverlay", 0);
            this.tabOverlap = this.style.getInt(context, "TabbedPane.tabOverlap", 0);
            this.extendTabsToBase = this.style.getBoolean(context, "TabbedPane.extendTabsToBase", false);
            this.textIconGap = this.style.getInt(context, "TabbedPane.textIconGap", 0);
            this.selectedTabPadInsets = (Insets)this.style.get(context, "TabbedPane.selectedTabPadInsets");
            if (this.selectedTabPadInsets == null) {
                this.selectedTabPadInsets = new Insets(0, 0, 0, 0);
            }
            this.tabAreaStatesMatchSelectedTab = this.style.getBoolean(context, "TabbedPane.tabAreaStatesMatchSelectedTab", false);
            this.nudgeSelectedLabel = this.style.getBoolean(context, "TabbedPane.nudgeSelectedLabel", true);
            if (style != null) {
                this.uninstallKeyboardActions();
                this.installKeyboardActions();
            }
        }
        context.dispose();
        if (this.tabContext != null) {
            this.tabContext.dispose();
        }
        this.tabContext = this.getContext(tabbedPane, Region.TABBED_PANE_TAB, 1);
        this.tabStyle = SynthLookAndFeel.updateStyle(this.tabContext, this);
        this.tabInsets = this.tabStyle.getInsets(this.tabContext, null);
        if (this.tabAreaContext != null) {
            this.tabAreaContext.dispose();
        }
        this.tabAreaContext = this.getContext(tabbedPane, Region.TABBED_PANE_TAB_AREA, 1);
        this.tabAreaStyle = SynthLookAndFeel.updateStyle(this.tabAreaContext, this);
        this.tabAreaInsets = this.tabAreaStyle.getInsets(this.tabAreaContext, null);
        if (this.tabContentContext != null) {
            this.tabContentContext.dispose();
        }
        this.tabContentContext = this.getContext(tabbedPane, Region.TABBED_PANE_CONTENT, 1);
        this.tabContentStyle = SynthLookAndFeel.updateStyle(this.tabContentContext, this);
        this.contentBorderInsets = this.tabContentStyle.getInsets(this.tabContentContext, null);
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        this.tabPane.addPropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        this.tabPane.removePropertyChangeListener(this);
    }
    
    @Override
    protected void uninstallDefaults() {
        final SynthContext context = this.getContext(this.tabPane, 1);
        this.style.uninstallDefaults(context);
        context.dispose();
        this.style = null;
        this.tabStyle.uninstallDefaults(this.tabContext);
        this.tabContext.dispose();
        this.tabContext = null;
        this.tabStyle = null;
        this.tabAreaStyle.uninstallDefaults(this.tabAreaContext);
        this.tabAreaContext.dispose();
        this.tabAreaContext = null;
        this.tabAreaStyle = null;
        this.tabContentStyle.uninstallDefaults(this.tabContentContext);
        this.tabContentContext.dispose();
        this.tabContentContext = null;
        this.tabContentStyle = null;
    }
    
    @Override
    public SynthContext getContext(final JComponent component) {
        return this.getContext(component, SynthLookAndFeel.getComponentState(component));
    }
    
    private SynthContext getContext(final JComponent component, final int n) {
        return SynthContext.getContext(component, this.style, n);
    }
    
    private SynthContext getContext(final JComponent component, final Region region, final int n) {
        SynthStyle synthStyle = null;
        if (region == Region.TABBED_PANE_TAB) {
            synthStyle = this.tabStyle;
        }
        else if (region == Region.TABBED_PANE_TAB_AREA) {
            synthStyle = this.tabAreaStyle;
        }
        else if (region == Region.TABBED_PANE_CONTENT) {
            synthStyle = this.tabContentStyle;
        }
        return SynthContext.getContext(component, region, synthStyle, n);
    }
    
    @Override
    protected JButton createScrollButton(final int n) {
        if (UIManager.getBoolean("TabbedPane.useBasicArrows")) {
            final JButton scrollButton = super.createScrollButton(n);
            scrollButton.setBorder(BorderFactory.createEmptyBorder());
            return scrollButton;
        }
        return new SynthScrollableTabButton(n);
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (SynthLookAndFeel.shouldUpdateStyle(propertyChangeEvent)) {
            this.updateStyle(this.tabPane);
        }
    }
    
    @Override
    protected MouseListener createMouseListener() {
        final MouseListener mouseListener = super.createMouseListener();
        return new MouseListener() {
            final /* synthetic */ MouseMotionListener val$delegate2 = (MouseMotionListener)mouseListener;
            
            @Override
            public void mouseClicked(final MouseEvent mouseEvent) {
                mouseListener.mouseClicked(mouseEvent);
            }
            
            @Override
            public void mouseEntered(final MouseEvent mouseEvent) {
                mouseListener.mouseEntered(mouseEvent);
            }
            
            @Override
            public void mouseExited(final MouseEvent mouseEvent) {
                mouseListener.mouseExited(mouseEvent);
            }
            
            @Override
            public void mousePressed(final MouseEvent mouseEvent) {
                if (!SynthTabbedPaneUI.this.tabPane.isEnabled()) {
                    return;
                }
                final int tabForCoordinate = SynthTabbedPaneUI.this.tabForCoordinate(SynthTabbedPaneUI.this.tabPane, mouseEvent.getX(), mouseEvent.getY());
                if (tabForCoordinate >= 0 && SynthTabbedPaneUI.this.tabPane.isEnabledAt(tabForCoordinate) && tabForCoordinate == SynthTabbedPaneUI.this.tabPane.getSelectedIndex()) {
                    SynthTabbedPaneUI.this.selectedTabIsPressed = true;
                    SynthTabbedPaneUI.this.tabPane.repaint();
                }
                mouseListener.mousePressed(mouseEvent);
            }
            
            @Override
            public void mouseReleased(final MouseEvent mouseEvent) {
                if (SynthTabbedPaneUI.this.selectedTabIsPressed) {
                    SynthTabbedPaneUI.this.selectedTabIsPressed = false;
                    SynthTabbedPaneUI.this.tabPane.repaint();
                }
                mouseListener.mouseReleased(mouseEvent);
                this.val$delegate2.mouseMoved(mouseEvent);
            }
        };
    }
    
    @Override
    protected int getTabLabelShiftX(final int n, final int n2, final boolean b) {
        if (this.nudgeSelectedLabel) {
            return super.getTabLabelShiftX(n, n2, b);
        }
        return 0;
    }
    
    @Override
    protected int getTabLabelShiftY(final int n, final int n2, final boolean b) {
        if (this.nudgeSelectedLabel) {
            return super.getTabLabelShiftY(n, n2, b);
        }
        return 0;
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        SynthLookAndFeel.update(context, graphics);
        context.getPainter().paintTabbedPaneBackground(context, graphics, 0, 0, component.getWidth(), component.getHeight());
        this.paint(context, graphics);
        context.dispose();
    }
    
    @Override
    protected int getBaseline(final int n) {
        if (this.tabPane.getTabComponentAt(n) != null || this.getTextViewForTab(n) != null) {
            return super.getBaseline(n);
        }
        final String title = this.tabPane.getTitleAt(n);
        final FontMetrics fontMetrics = this.getFontMetrics(this.tabContext.getStyle().getFont(this.tabContext));
        final Icon iconForTab = this.getIconForTab(n);
        this.textRect.setBounds(0, 0, 0, 0);
        this.iconRect.setBounds(0, 0, 0, 0);
        this.calcRect.setBounds(0, 0, 32767, this.maxTabHeight);
        this.tabContext.getStyle().getGraphicsUtils(this.tabContext).layoutText(this.tabContext, fontMetrics, title, iconForTab, 0, 0, 10, 0, this.calcRect, this.iconRect, this.textRect, this.textIconGap);
        return this.textRect.y + fontMetrics.getAscent() + this.getBaselineOffset();
    }
    
    @Override
    public void paintBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        synthContext.getPainter().paintTabbedPaneBorder(synthContext, graphics, n, n2, n3, n4);
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final SynthContext context = this.getContext(component);
        this.paint(context, graphics);
        context.dispose();
    }
    
    protected void paint(final SynthContext synthContext, final Graphics graphics) {
        final int selectedIndex = this.tabPane.getSelectedIndex();
        final int tabPlacement = this.tabPane.getTabPlacement();
        this.ensureCurrentLayout();
        if (!this.scrollableTabLayoutEnabled()) {
            final Insets insets = this.tabPane.getInsets();
            int left = insets.left;
            int top = insets.top;
            int calculateTabAreaWidth = this.tabPane.getWidth() - insets.left - insets.right;
            int calculateTabAreaHeight = this.tabPane.getHeight() - insets.top - insets.bottom;
            switch (tabPlacement) {
                case 2: {
                    calculateTabAreaWidth = this.calculateTabAreaWidth(tabPlacement, this.runCount, this.maxTabWidth);
                    break;
                }
                case 4: {
                    final int calculateTabAreaWidth2 = this.calculateTabAreaWidth(tabPlacement, this.runCount, this.maxTabWidth);
                    left = left + calculateTabAreaWidth - calculateTabAreaWidth2;
                    calculateTabAreaWidth = calculateTabAreaWidth2;
                    break;
                }
                case 3: {
                    final int calculateTabAreaHeight2 = this.calculateTabAreaHeight(tabPlacement, this.runCount, this.maxTabHeight);
                    top = top + calculateTabAreaHeight - calculateTabAreaHeight2;
                    calculateTabAreaHeight = calculateTabAreaHeight2;
                    break;
                }
                default: {
                    calculateTabAreaHeight = this.calculateTabAreaHeight(tabPlacement, this.runCount, this.maxTabHeight);
                    break;
                }
            }
            this.tabAreaBounds.setBounds(left, top, calculateTabAreaWidth, calculateTabAreaHeight);
            if (graphics.getClipBounds().intersects(this.tabAreaBounds)) {
                this.paintTabArea(this.tabAreaContext, graphics, tabPlacement, selectedIndex, this.tabAreaBounds);
            }
        }
        this.paintContentBorder(this.tabContentContext, graphics, tabPlacement, selectedIndex);
    }
    
    @Override
    protected void paintTabArea(final Graphics graphics, final int n, final int n2) {
        final Insets insets = this.tabPane.getInsets();
        this.paintTabArea(this.tabAreaContext, graphics, n, n2, new Rectangle(insets.left, insets.top, this.tabPane.getWidth() - insets.left - insets.right, this.tabPane.getHeight() - insets.top - insets.bottom));
    }
    
    private void paintTabArea(final SynthContext synthContext, final Graphics graphics, final int n, final int n2, final Rectangle rectangle) {
        final Rectangle clipBounds = graphics.getClipBounds();
        if (this.tabAreaStatesMatchSelectedTab && n2 >= 0) {
            this.updateTabContext(n2, true, this.selectedTabIsPressed, this.getRolloverTab() == n2, this.getFocusIndex() == n2);
            synthContext.setComponentState(this.tabContext.getComponentState());
        }
        else {
            synthContext.setComponentState(1);
        }
        SynthLookAndFeel.updateSubregion(synthContext, graphics, rectangle);
        synthContext.getPainter().paintTabbedPaneTabAreaBackground(synthContext, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, n);
        synthContext.getPainter().paintTabbedPaneTabAreaBorder(synthContext, graphics, rectangle.x, rectangle.y, rectangle.width, rectangle.height, n);
        final int tabCount = this.tabPane.getTabCount();
        this.iconRect.setBounds(0, 0, 0, 0);
        this.textRect.setBounds(0, 0, 0, 0);
        for (int i = this.runCount - 1; i >= 0; --i) {
            final int n3 = this.tabRuns[i];
            final int n4 = this.tabRuns[(i == this.runCount - 1) ? 0 : (i + 1)];
            for (int n5 = (n4 != 0) ? (n4 - 1) : (tabCount - 1), j = n3; j <= n5; ++j) {
                if (this.rects[j].intersects(clipBounds) && n2 != j) {
                    this.paintTab(this.tabContext, graphics, n, this.rects, j, this.iconRect, this.textRect);
                }
            }
        }
        if (n2 >= 0 && this.rects[n2].intersects(clipBounds)) {
            this.paintTab(this.tabContext, graphics, n, this.rects, n2, this.iconRect, this.textRect);
        }
    }
    
    @Override
    protected void setRolloverTab(final int rolloverTab) {
        final int rolloverTab2 = this.getRolloverTab();
        super.setRolloverTab(rolloverTab);
        if (rolloverTab2 != rolloverTab && this.tabAreaStatesMatchSelectedTab) {
            this.tabPane.repaint();
        }
        else {
            if (rolloverTab2 >= 0 && rolloverTab2 < this.tabPane.getTabCount()) {
                final Rectangle tabBounds = this.getTabBounds(this.tabPane, rolloverTab2);
                if (tabBounds != null) {
                    this.tabPane.repaint(tabBounds);
                }
            }
            if (rolloverTab >= 0) {
                final Rectangle tabBounds2 = this.getTabBounds(this.tabPane, rolloverTab);
                if (tabBounds2 != null) {
                    this.tabPane.repaint(tabBounds2);
                }
            }
        }
    }
    
    private void paintTab(final SynthContext synthContext, final Graphics graphics, final int n, final Rectangle[] array, final int n2, final Rectangle rectangle, final Rectangle rectangle2) {
        final Rectangle rectangle3 = array[n2];
        final int selectedIndex = this.tabPane.getSelectedIndex();
        final boolean b = selectedIndex == n2;
        this.updateTabContext(n2, b, b && this.selectedTabIsPressed, this.getRolloverTab() == n2, this.getFocusIndex() == n2);
        SynthLookAndFeel.updateSubregion(synthContext, graphics, rectangle3);
        int x = rectangle3.x;
        int y = rectangle3.y;
        int height = rectangle3.height;
        int width = rectangle3.width;
        final int tabPlacement = this.tabPane.getTabPlacement();
        if (this.extendTabsToBase && this.runCount > 1 && selectedIndex >= 0) {
            final Rectangle rectangle4 = array[selectedIndex];
            switch (tabPlacement) {
                case 1: {
                    height = rectangle4.y + rectangle4.height - rectangle3.y;
                    break;
                }
                case 2: {
                    width = rectangle4.x + rectangle4.width - rectangle3.x;
                    break;
                }
                case 3: {
                    final int y2 = rectangle4.y;
                    height = rectangle3.y + rectangle3.height - y2;
                    y = y2;
                    break;
                }
                case 4: {
                    final int x2 = rectangle4.x;
                    width = rectangle3.x + rectangle3.width - x2;
                    x = x2;
                    break;
                }
            }
        }
        this.tabContext.getPainter().paintTabbedPaneTabBackground(this.tabContext, graphics, x, y, width, height, n2, tabPlacement);
        this.tabContext.getPainter().paintTabbedPaneTabBorder(this.tabContext, graphics, x, y, width, height, n2, tabPlacement);
        if (this.tabPane.getTabComponentAt(n2) == null) {
            final String title = this.tabPane.getTitleAt(n2);
            final Font font = synthContext.getStyle().getFont(synthContext);
            final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.tabPane, graphics, font);
            final Icon iconForTab = this.getIconForTab(n2);
            this.layoutLabel(synthContext, n, fontMetrics, n2, title, iconForTab, rectangle3, rectangle, rectangle2, b);
            this.paintText(synthContext, graphics, n, font, fontMetrics, n2, title, rectangle2, b);
            this.paintIcon(graphics, n, n2, iconForTab, rectangle, b);
        }
    }
    
    private void layoutLabel(final SynthContext synthContext, final int n, final FontMetrics fontMetrics, final int n2, final String s, final Icon icon, final Rectangle rectangle, final Rectangle rectangle2, final Rectangle rectangle3, final boolean b) {
        final View textViewForTab = this.getTextViewForTab(n2);
        if (textViewForTab != null) {
            this.tabPane.putClientProperty("html", textViewForTab);
        }
        final int n3 = 0;
        rectangle2.y = n3;
        rectangle2.x = n3;
        rectangle3.y = n3;
        rectangle3.x = n3;
        synthContext.getStyle().getGraphicsUtils(synthContext).layoutText(synthContext, fontMetrics, s, icon, 0, 0, 10, 0, rectangle, rectangle2, rectangle3, this.textIconGap);
        this.tabPane.putClientProperty("html", null);
        final int tabLabelShiftX = this.getTabLabelShiftX(n, n2, b);
        final int tabLabelShiftY = this.getTabLabelShiftY(n, n2, b);
        rectangle2.x += tabLabelShiftX;
        rectangle2.y += tabLabelShiftY;
        rectangle3.x += tabLabelShiftX;
        rectangle3.y += tabLabelShiftY;
    }
    
    private void paintText(final SynthContext synthContext, final Graphics graphics, final int n, final Font font, final FontMetrics fontMetrics, final int n2, final String s, final Rectangle rectangle, final boolean b) {
        graphics.setFont(font);
        final View textViewForTab = this.getTextViewForTab(n2);
        if (textViewForTab != null) {
            textViewForTab.paint(graphics, rectangle);
        }
        else {
            final int displayedMnemonicIndex = this.tabPane.getDisplayedMnemonicIndexAt(n2);
            graphics.setColor(synthContext.getStyle().getColor(synthContext, ColorType.TEXT_FOREGROUND));
            synthContext.getStyle().getGraphicsUtils(synthContext).paintText(synthContext, graphics, s, rectangle, displayedMnemonicIndex);
        }
    }
    
    private void paintContentBorder(final SynthContext synthContext, final Graphics graphics, final int n, final int n2) {
        final int width = this.tabPane.getWidth();
        final int height = this.tabPane.getHeight();
        final Insets insets = this.tabPane.getInsets();
        int left = insets.left;
        int top = insets.top;
        int n3 = width - insets.right - insets.left;
        int n4 = height - insets.top - insets.bottom;
        switch (n) {
            case 2: {
                left += this.calculateTabAreaWidth(n, this.runCount, this.maxTabWidth);
                n3 -= left - insets.left;
                break;
            }
            case 4: {
                n3 -= this.calculateTabAreaWidth(n, this.runCount, this.maxTabWidth);
                break;
            }
            case 3: {
                n4 -= this.calculateTabAreaHeight(n, this.runCount, this.maxTabHeight);
                break;
            }
            default: {
                top += this.calculateTabAreaHeight(n, this.runCount, this.maxTabHeight);
                n4 -= top - insets.top;
                break;
            }
        }
        SynthLookAndFeel.updateSubregion(synthContext, graphics, new Rectangle(left, top, n3, n4));
        synthContext.getPainter().paintTabbedPaneContentBackground(synthContext, graphics, left, top, n3, n4);
        synthContext.getPainter().paintTabbedPaneContentBorder(synthContext, graphics, left, top, n3, n4);
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
    protected int calculateMaxTabHeight(final int n) {
        final FontMetrics fontMetrics = this.getFontMetrics(this.tabContext.getStyle().getFont(this.tabContext));
        final int tabCount = this.tabPane.getTabCount();
        int max = 0;
        final int height = fontMetrics.getHeight();
        for (int i = 0; i < tabCount; ++i) {
            max = Math.max(this.calculateTabHeight(n, i, height), max);
        }
        return max;
    }
    
    @Override
    protected int calculateTabWidth(final int n, final int n2, final FontMetrics fontMetrics) {
        final Icon iconForTab = this.getIconForTab(n2);
        final Insets tabInsets = this.getTabInsets(n, n2);
        int n3 = tabInsets.left + tabInsets.right;
        final Component tabComponent = this.tabPane.getTabComponentAt(n2);
        int n4;
        if (tabComponent != null) {
            n4 = n3 + tabComponent.getPreferredSize().width;
        }
        else {
            if (iconForTab != null) {
                n3 += iconForTab.getIconWidth() + this.textIconGap;
            }
            final View textViewForTab = this.getTextViewForTab(n2);
            if (textViewForTab != null) {
                n4 = n3 + (int)textViewForTab.getPreferredSpan(0);
            }
            else {
                n4 = n3 + this.tabContext.getStyle().getGraphicsUtils(this.tabContext).computeStringWidth(this.tabContext, fontMetrics.getFont(), fontMetrics, this.tabPane.getTitleAt(n2));
            }
        }
        return n4;
    }
    
    @Override
    protected int calculateMaxTabWidth(final int n) {
        final FontMetrics fontMetrics = this.getFontMetrics(this.tabContext.getStyle().getFont(this.tabContext));
        final int tabCount = this.tabPane.getTabCount();
        int max = 0;
        for (int i = 0; i < tabCount; ++i) {
            max = Math.max(this.calculateTabWidth(n, i, fontMetrics), max);
        }
        return max;
    }
    
    @Override
    protected Insets getTabInsets(final int n, final int n2) {
        this.updateTabContext(n2, false, false, false, this.getFocusIndex() == n2);
        return this.tabInsets;
    }
    
    @Override
    protected FontMetrics getFontMetrics() {
        return this.getFontMetrics(this.tabContext.getStyle().getFont(this.tabContext));
    }
    
    private FontMetrics getFontMetrics(final Font font) {
        return this.tabPane.getFontMetrics(font);
    }
    
    private void updateTabContext(final int n, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        final int n2 = 0;
        int componentState;
        if (!this.tabPane.isEnabled() || !this.tabPane.isEnabledAt(n)) {
            componentState = (n2 | 0x8);
            if (b) {
                componentState |= 0x200;
            }
        }
        else if (b) {
            componentState = (n2 | 0x201);
            if (b3 && UIManager.getBoolean("TabbedPane.isTabRollover")) {
                componentState |= 0x2;
            }
        }
        else if (b3) {
            componentState = (n2 | 0x3);
        }
        else {
            componentState = (SynthLookAndFeel.getComponentState(this.tabPane) & 0xFFFFFEFF);
        }
        if (b4 && this.tabPane.hasFocus()) {
            componentState |= 0x100;
        }
        if (b2) {
            componentState |= 0x4;
        }
        this.tabContext.setComponentState(componentState);
    }
    
    @Override
    protected LayoutManager createLayoutManager() {
        if (this.tabPane.getTabLayoutPolicy() == 1) {
            return super.createLayoutManager();
        }
        return new TabbedPaneLayout() {
            @Override
            public void calculateLayoutInfo() {
                super.calculateLayoutInfo();
                if (SynthTabbedPaneUI.this.tabOverlap != 0) {
                    final int tabCount = SynthTabbedPaneUI.this.tabPane.getTabCount();
                    final boolean leftToRight = SynthTabbedPaneUI.this.tabPane.getComponentOrientation().isLeftToRight();
                    for (int i = SynthTabbedPaneUI.this.runCount - 1; i >= 0; --i) {
                        final int n = SynthTabbedPaneUI.this.tabRuns[i];
                        final int n2 = SynthTabbedPaneUI.this.tabRuns[(i == SynthTabbedPaneUI.this.runCount - 1) ? 0 : (i + 1)];
                        for (int n3 = (n2 != 0) ? (n2 - 1) : (tabCount - 1), j = n + 1; j <= n3; ++j) {
                            int n4 = 0;
                            int access$700 = 0;
                            switch (SynthTabbedPaneUI.this.tabPane.getTabPlacement()) {
                                case 1:
                                case 3: {
                                    n4 = (leftToRight ? SynthTabbedPaneUI.this.tabOverlap : (-SynthTabbedPaneUI.this.tabOverlap));
                                    break;
                                }
                                case 2:
                                case 4: {
                                    access$700 = SynthTabbedPaneUI.this.tabOverlap;
                                    break;
                                }
                            }
                            final Rectangle rectangle = SynthTabbedPaneUI.this.rects[j];
                            rectangle.x += n4;
                            final Rectangle rectangle2 = SynthTabbedPaneUI.this.rects[j];
                            rectangle2.y += access$700;
                            final Rectangle rectangle3 = SynthTabbedPaneUI.this.rects[j];
                            rectangle3.width += Math.abs(n4);
                            final Rectangle rectangle4 = SynthTabbedPaneUI.this.rects[j];
                            rectangle4.height += Math.abs(access$700);
                        }
                    }
                }
            }
        };
    }
    
    private class SynthScrollableTabButton extends SynthArrowButton implements UIResource
    {
        public SynthScrollableTabButton(final int n) {
            super(n);
            this.setName("TabbedPane.button");
        }
    }
}
