package javax.swing.plaf.metal;

import javax.swing.Icon;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.plaf.UIResource;
import java.awt.Rectangle;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.UIManager;
import java.awt.LayoutManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class MetalTabbedPaneUI extends BasicTabbedPaneUI
{
    protected int minTabWidth;
    private Color unselectedBackground;
    protected Color tabAreaBackground;
    protected Color selectColor;
    protected Color selectHighlight;
    private boolean tabsOpaque;
    private boolean ocean;
    private Color oceanSelectedBorderColor;
    
    public MetalTabbedPaneUI() {
        this.minTabWidth = 40;
        this.tabsOpaque = true;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new MetalTabbedPaneUI();
    }
    
    @Override
    protected LayoutManager createLayoutManager() {
        if (this.tabPane.getTabLayoutPolicy() == 1) {
            return super.createLayoutManager();
        }
        return new TabbedPaneLayout();
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.tabAreaBackground = UIManager.getColor("TabbedPane.tabAreaBackground");
        this.selectColor = UIManager.getColor("TabbedPane.selected");
        this.selectHighlight = UIManager.getColor("TabbedPane.selectHighlight");
        this.tabsOpaque = UIManager.getBoolean("TabbedPane.tabsOpaque");
        this.unselectedBackground = UIManager.getColor("TabbedPane.unselectedBackground");
        this.ocean = MetalLookAndFeel.usingOcean();
        if (this.ocean) {
            this.oceanSelectedBorderColor = UIManager.getColor("TabbedPane.borderHightlightColor");
        }
    }
    
    @Override
    protected void paintTabBorder(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final boolean b) {
        final int n7 = n4 + (n6 - 1);
        final int n8 = n3 + (n5 - 1);
        switch (n) {
            case 2: {
                this.paintLeftTabBorder(n2, graphics, n3, n4, n5, n6, n7, n8, b);
                break;
            }
            case 3: {
                this.paintBottomTabBorder(n2, graphics, n3, n4, n5, n6, n7, n8, b);
                break;
            }
            case 4: {
                this.paintRightTabBorder(n2, graphics, n3, n4, n5, n6, n7, n8, b);
                break;
            }
            default: {
                this.paintTopTabBorder(n2, graphics, n3, n4, n5, n6, n7, n8, b);
                break;
            }
        }
    }
    
    protected void paintTopTabBorder(final int n, final Graphics graphics, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final boolean b) {
        final int runForTab = this.getRunForTab(this.tabPane.getTabCount(), n);
        final int lastTabInRun = this.lastTabInRun(this.tabPane.getTabCount(), runForTab);
        final int n8 = this.tabRuns[runForTab];
        final boolean leftToRight = MetalUtils.isLeftToRight(this.tabPane);
        final int selectedIndex = this.tabPane.getSelectedIndex();
        final int n9 = n5 - 1;
        final int n10 = n4 - 1;
        if (this.shouldFillGap(runForTab, n, n2, n3)) {
            graphics.translate(n2, n3);
            if (leftToRight) {
                graphics.setColor(this.getColorForGap(runForTab, n2, n3 + 1));
                graphics.fillRect(1, 0, 5, 3);
                graphics.fillRect(1, 3, 2, 2);
            }
            else {
                graphics.setColor(this.getColorForGap(runForTab, n2 + n4 - 1, n3 + 1));
                graphics.fillRect(n10 - 5, 0, 5, 3);
                graphics.fillRect(n10 - 2, 3, 2, 2);
            }
            graphics.translate(-n2, -n3);
        }
        graphics.translate(n2, n3);
        if (this.ocean && b) {
            graphics.setColor(this.oceanSelectedBorderColor);
        }
        else {
            graphics.setColor(this.darkShadow);
        }
        if (leftToRight) {
            graphics.drawLine(1, 5, 6, 0);
            graphics.drawLine(6, 0, n10, 0);
            if (n == lastTabInRun) {
                graphics.drawLine(n10, 1, n10, n9);
            }
            if (this.ocean && n - 1 == selectedIndex && runForTab == this.getRunForTab(this.tabPane.getTabCount(), selectedIndex)) {
                graphics.setColor(this.oceanSelectedBorderColor);
            }
            if (n != this.tabRuns[this.runCount - 1]) {
                if (this.ocean && b) {
                    graphics.drawLine(0, 6, 0, n9);
                    graphics.setColor(this.darkShadow);
                    graphics.drawLine(0, 0, 0, 5);
                }
                else {
                    graphics.drawLine(0, 0, 0, n9);
                }
            }
            else {
                graphics.drawLine(0, 6, 0, n9);
            }
        }
        else {
            graphics.drawLine(n10 - 1, 5, n10 - 6, 0);
            graphics.drawLine(n10 - 6, 0, 0, 0);
            if (n == lastTabInRun) {
                graphics.drawLine(0, 1, 0, n9);
            }
            if (this.ocean && n - 1 == selectedIndex && runForTab == this.getRunForTab(this.tabPane.getTabCount(), selectedIndex)) {
                graphics.setColor(this.oceanSelectedBorderColor);
                graphics.drawLine(n10, 0, n10, n9);
            }
            else if (this.ocean && b) {
                graphics.drawLine(n10, 6, n10, n9);
                if (n != 0) {
                    graphics.setColor(this.darkShadow);
                    graphics.drawLine(n10, 0, n10, 5);
                }
            }
            else if (n != this.tabRuns[this.runCount - 1]) {
                graphics.drawLine(n10, 0, n10, n9);
            }
            else {
                graphics.drawLine(n10, 6, n10, n9);
            }
        }
        graphics.setColor(b ? this.selectHighlight : this.highlight);
        if (leftToRight) {
            graphics.drawLine(1, 6, 6, 1);
            graphics.drawLine(6, 1, (n == lastTabInRun) ? (n10 - 1) : n10, 1);
            graphics.drawLine(1, 6, 1, n9);
            if (n == n8 && n != this.tabRuns[this.runCount - 1]) {
                if (this.tabPane.getSelectedIndex() == this.tabRuns[runForTab + 1]) {
                    graphics.setColor(this.selectHighlight);
                }
                else {
                    graphics.setColor(this.highlight);
                }
                graphics.drawLine(1, 0, 1, 4);
            }
        }
        else {
            graphics.drawLine(n10 - 1, 6, n10 - 6, 1);
            graphics.drawLine(n10 - 6, 1, 1, 1);
            if (n == lastTabInRun) {
                graphics.drawLine(1, 1, 1, n9);
            }
            else {
                graphics.drawLine(0, 1, 0, n9);
            }
        }
        graphics.translate(-n2, -n3);
    }
    
    protected boolean shouldFillGap(final int n, final int n2, final int n3, final int n4) {
        boolean b = false;
        if (!this.tabsOpaque) {
            return false;
        }
        if (n == this.runCount - 2) {
            final Rectangle tabBounds = this.getTabBounds(this.tabPane, this.tabPane.getTabCount() - 1);
            final Rectangle tabBounds2 = this.getTabBounds(this.tabPane, n2);
            if (MetalUtils.isLeftToRight(this.tabPane)) {
                if (tabBounds.x + tabBounds.width - 1 > tabBounds2.x + 2) {
                    return true;
                }
            }
            else if (tabBounds.x < tabBounds2.x + tabBounds2.width - 1 - 2) {
                return true;
            }
        }
        else {
            b = (n != this.runCount - 1);
        }
        return b;
    }
    
    protected Color getColorForGap(final int n, final int n2, final int n3) {
        final int selectedIndex = this.tabPane.getSelectedIndex();
        final int n4 = this.tabRuns[n + 1];
        for (int lastTabInRun = this.lastTabInRun(this.tabPane.getTabCount(), n + 1), i = n4; i <= lastTabInRun; ++i) {
            final Rectangle tabBounds = this.getTabBounds(this.tabPane, i);
            final int x = tabBounds.x;
            final int n5 = tabBounds.x + tabBounds.width - 1;
            if (MetalUtils.isLeftToRight(this.tabPane)) {
                if (x <= n2 && n5 - 4 > n2) {
                    return (selectedIndex == i) ? this.selectColor : this.getUnselectedBackgroundAt(i);
                }
            }
            else if (x + 4 < n2 && n5 >= n2) {
                return (selectedIndex == i) ? this.selectColor : this.getUnselectedBackgroundAt(i);
            }
        }
        return this.tabPane.getBackground();
    }
    
    protected void paintLeftTabBorder(final int n, final Graphics graphics, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final boolean b) {
        final int tabCount = this.tabPane.getTabCount();
        final int runForTab = this.getRunForTab(tabCount, n);
        final int lastTabInRun = this.lastTabInRun(tabCount, runForTab);
        final int n8 = this.tabRuns[runForTab];
        graphics.translate(n2, n3);
        final int n9 = n5 - 1;
        final int n10 = n4 - 1;
        if (n != n8 && this.tabsOpaque) {
            graphics.setColor((this.tabPane.getSelectedIndex() == n - 1) ? this.selectColor : this.getUnselectedBackgroundAt(n - 1));
            graphics.fillRect(2, 0, 4, 3);
            graphics.drawLine(2, 3, 2, 3);
        }
        if (this.ocean) {
            graphics.setColor(b ? this.selectHighlight : MetalLookAndFeel.getWhite());
        }
        else {
            graphics.setColor(b ? this.selectHighlight : this.highlight);
        }
        graphics.drawLine(1, 6, 6, 1);
        graphics.drawLine(1, 6, 1, n9);
        graphics.drawLine(6, 1, n10, 1);
        if (n != n8) {
            if (this.tabPane.getSelectedIndex() == n - 1) {
                graphics.setColor(this.selectHighlight);
            }
            else {
                graphics.setColor(this.ocean ? MetalLookAndFeel.getWhite() : this.highlight);
            }
            graphics.drawLine(1, 0, 1, 4);
        }
        if (this.ocean) {
            if (b) {
                graphics.setColor(this.oceanSelectedBorderColor);
            }
            else {
                graphics.setColor(this.darkShadow);
            }
        }
        else {
            graphics.setColor(this.darkShadow);
        }
        graphics.drawLine(1, 5, 6, 0);
        graphics.drawLine(6, 0, n10, 0);
        if (n == lastTabInRun) {
            graphics.drawLine(0, n9, n10, n9);
        }
        if (this.ocean) {
            if (this.tabPane.getSelectedIndex() == n - 1) {
                graphics.drawLine(0, 5, 0, n9);
                graphics.setColor(this.oceanSelectedBorderColor);
                graphics.drawLine(0, 0, 0, 5);
            }
            else if (b) {
                graphics.drawLine(0, 6, 0, n9);
                if (n != 0) {
                    graphics.setColor(this.darkShadow);
                    graphics.drawLine(0, 0, 0, 5);
                }
            }
            else if (n != n8) {
                graphics.drawLine(0, 0, 0, n9);
            }
            else {
                graphics.drawLine(0, 6, 0, n9);
            }
        }
        else if (n != n8) {
            graphics.drawLine(0, 0, 0, n9);
        }
        else {
            graphics.drawLine(0, 6, 0, n9);
        }
        graphics.translate(-n2, -n3);
    }
    
    protected void paintBottomTabBorder(final int n, final Graphics graphics, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final boolean b) {
        final int tabCount = this.tabPane.getTabCount();
        final int runForTab = this.getRunForTab(tabCount, n);
        final int lastTabInRun = this.lastTabInRun(tabCount, runForTab);
        final int n8 = this.tabRuns[runForTab];
        final boolean leftToRight = MetalUtils.isLeftToRight(this.tabPane);
        final int n9 = n5 - 1;
        final int n10 = n4 - 1;
        if (this.shouldFillGap(runForTab, n, n2, n3)) {
            graphics.translate(n2, n3);
            if (leftToRight) {
                graphics.setColor(this.getColorForGap(runForTab, n2, n3));
                graphics.fillRect(1, n9 - 4, 3, 5);
                graphics.fillRect(4, n9 - 1, 2, 2);
            }
            else {
                graphics.setColor(this.getColorForGap(runForTab, n2 + n4 - 1, n3));
                graphics.fillRect(n10 - 3, n9 - 3, 3, 4);
                graphics.fillRect(n10 - 5, n9 - 1, 2, 2);
                graphics.drawLine(n10 - 1, n9 - 4, n10 - 1, n9 - 4);
            }
            graphics.translate(-n2, -n3);
        }
        graphics.translate(n2, n3);
        if (this.ocean && b) {
            graphics.setColor(this.oceanSelectedBorderColor);
        }
        else {
            graphics.setColor(this.darkShadow);
        }
        if (leftToRight) {
            graphics.drawLine(1, n9 - 5, 6, n9);
            graphics.drawLine(6, n9, n10, n9);
            if (n == lastTabInRun) {
                graphics.drawLine(n10, 0, n10, n9);
            }
            if (this.ocean && b) {
                graphics.drawLine(0, 0, 0, n9 - 6);
                if ((runForTab == 0 && n != 0) || (runForTab > 0 && n != this.tabRuns[runForTab - 1])) {
                    graphics.setColor(this.darkShadow);
                    graphics.drawLine(0, n9 - 5, 0, n9);
                }
            }
            else {
                if (this.ocean && n == this.tabPane.getSelectedIndex() + 1) {
                    graphics.setColor(this.oceanSelectedBorderColor);
                }
                if (n != this.tabRuns[this.runCount - 1]) {
                    graphics.drawLine(0, 0, 0, n9);
                }
                else {
                    graphics.drawLine(0, 0, 0, n9 - 6);
                }
            }
        }
        else {
            graphics.drawLine(n10 - 1, n9 - 5, n10 - 6, n9);
            graphics.drawLine(n10 - 6, n9, 0, n9);
            if (n == lastTabInRun) {
                graphics.drawLine(0, 0, 0, n9);
            }
            if (this.ocean && n == this.tabPane.getSelectedIndex() + 1) {
                graphics.setColor(this.oceanSelectedBorderColor);
                graphics.drawLine(n10, 0, n10, n9);
            }
            else if (this.ocean && b) {
                graphics.drawLine(n10, 0, n10, n9 - 6);
                if (n != n8) {
                    graphics.setColor(this.darkShadow);
                    graphics.drawLine(n10, n9 - 5, n10, n9);
                }
            }
            else if (n != this.tabRuns[this.runCount - 1]) {
                graphics.drawLine(n10, 0, n10, n9);
            }
            else {
                graphics.drawLine(n10, 0, n10, n9 - 6);
            }
        }
        graphics.setColor(b ? this.selectHighlight : this.highlight);
        if (leftToRight) {
            graphics.drawLine(1, n9 - 6, 6, n9 - 1);
            graphics.drawLine(1, 0, 1, n9 - 6);
            if (n == n8 && n != this.tabRuns[this.runCount - 1]) {
                if (this.tabPane.getSelectedIndex() == this.tabRuns[runForTab + 1]) {
                    graphics.setColor(this.selectHighlight);
                }
                else {
                    graphics.setColor(this.highlight);
                }
                graphics.drawLine(1, n9 - 4, 1, n9);
            }
        }
        else if (n == lastTabInRun) {
            graphics.drawLine(1, 0, 1, n9 - 1);
        }
        else {
            graphics.drawLine(0, 0, 0, n9 - 1);
        }
        graphics.translate(-n2, -n3);
    }
    
    protected void paintRightTabBorder(final int n, final Graphics graphics, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final boolean b) {
        final int tabCount = this.tabPane.getTabCount();
        final int runForTab = this.getRunForTab(tabCount, n);
        final int lastTabInRun = this.lastTabInRun(tabCount, runForTab);
        final int n8 = this.tabRuns[runForTab];
        graphics.translate(n2, n3);
        final int n9 = n5 - 1;
        final int n10 = n4 - 1;
        if (n != n8 && this.tabsOpaque) {
            graphics.setColor((this.tabPane.getSelectedIndex() == n - 1) ? this.selectColor : this.getUnselectedBackgroundAt(n - 1));
            graphics.fillRect(n10 - 5, 0, 5, 3);
            graphics.fillRect(n10 - 2, 3, 2, 2);
        }
        graphics.setColor(b ? this.selectHighlight : this.highlight);
        graphics.drawLine(n10 - 6, 1, n10 - 1, 6);
        graphics.drawLine(0, 1, n10 - 6, 1);
        if (!b) {
            graphics.drawLine(0, 1, 0, n9);
        }
        if (this.ocean && b) {
            graphics.setColor(this.oceanSelectedBorderColor);
        }
        else {
            graphics.setColor(this.darkShadow);
        }
        if (n == lastTabInRun) {
            graphics.drawLine(0, n9, n10, n9);
        }
        if (this.ocean && this.tabPane.getSelectedIndex() == n - 1) {
            graphics.setColor(this.oceanSelectedBorderColor);
        }
        graphics.drawLine(n10 - 6, 0, n10, 6);
        graphics.drawLine(0, 0, n10 - 6, 0);
        if (this.ocean && b) {
            graphics.drawLine(n10, 6, n10, n9);
            if (n != n8) {
                graphics.setColor(this.darkShadow);
                graphics.drawLine(n10, 0, n10, 5);
            }
        }
        else if (this.ocean && this.tabPane.getSelectedIndex() == n - 1) {
            graphics.setColor(this.oceanSelectedBorderColor);
            graphics.drawLine(n10, 0, n10, 6);
            graphics.setColor(this.darkShadow);
            graphics.drawLine(n10, 6, n10, n9);
        }
        else if (n != n8) {
            graphics.drawLine(n10, 0, n10, n9);
        }
        else {
            graphics.drawLine(n10, 6, n10, n9);
        }
        graphics.translate(-n2, -n3);
    }
    
    @Override
    public void update(final Graphics graphics, final JComponent component) {
        if (component.isOpaque()) {
            graphics.setColor(this.tabAreaBackground);
            graphics.fillRect(0, 0, component.getWidth(), component.getHeight());
        }
        this.paint(graphics, component);
    }
    
    @Override
    protected void paintTabBackground(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final boolean b) {
        if (b) {
            graphics.setColor(this.selectColor);
        }
        else {
            graphics.setColor(this.getUnselectedBackgroundAt(n2));
        }
        if (MetalUtils.isLeftToRight(this.tabPane)) {
            switch (n) {
                case 2: {
                    graphics.fillRect(n3 + 5, n4 + 1, n5 - 5, n6 - 1);
                    graphics.fillRect(n3 + 2, n4 + 4, 3, n6 - 4);
                    break;
                }
                case 3: {
                    graphics.fillRect(n3 + 2, n4, n5 - 2, n6 - 4);
                    graphics.fillRect(n3 + 5, n4 + (n6 - 1) - 3, n5 - 5, 3);
                    break;
                }
                case 4: {
                    graphics.fillRect(n3, n4 + 2, n5 - 4, n6 - 2);
                    graphics.fillRect(n3 + (n5 - 1) - 3, n4 + 5, 3, n6 - 5);
                    break;
                }
                default: {
                    graphics.fillRect(n3 + 4, n4 + 2, n5 - 1 - 3, n6 - 1 - 1);
                    graphics.fillRect(n3 + 2, n4 + 5, 2, n6 - 5);
                    break;
                }
            }
        }
        else {
            switch (n) {
                case 2: {
                    graphics.fillRect(n3 + 5, n4 + 1, n5 - 5, n6 - 1);
                    graphics.fillRect(n3 + 2, n4 + 4, 3, n6 - 4);
                    break;
                }
                case 3: {
                    graphics.fillRect(n3, n4, n5 - 5, n6 - 1);
                    graphics.fillRect(n3 + (n5 - 1) - 4, n4, 4, n6 - 5);
                    graphics.fillRect(n3 + (n5 - 1) - 4, n4 + (n6 - 1) - 4, 2, 2);
                    break;
                }
                case 4: {
                    graphics.fillRect(n3 + 1, n4 + 1, n5 - 5, n6 - 1);
                    graphics.fillRect(n3 + (n5 - 1) - 3, n4 + 5, 3, n6 - 5);
                    break;
                }
                default: {
                    graphics.fillRect(n3, n4 + 2, n5 - 1 - 3, n6 - 1 - 1);
                    graphics.fillRect(n3 + (n5 - 1) - 3, n4 + 5, 3, n6 - 3);
                    break;
                }
            }
        }
    }
    
    @Override
    protected int getTabLabelShiftX(final int n, final int n2, final boolean b) {
        return 0;
    }
    
    @Override
    protected int getTabLabelShiftY(final int n, final int n2, final boolean b) {
        return 0;
    }
    
    @Override
    protected int getBaselineOffset() {
        return 0;
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final int tabPlacement = this.tabPane.getTabPlacement();
        final Insets insets = component.getInsets();
        final Dimension size = component.getSize();
        if (this.tabPane.isOpaque()) {
            final Color background = component.getBackground();
            if (background instanceof UIResource && this.tabAreaBackground != null) {
                graphics.setColor(this.tabAreaBackground);
            }
            else {
                graphics.setColor(background);
            }
            switch (tabPlacement) {
                case 2: {
                    graphics.fillRect(insets.left, insets.top, this.calculateTabAreaWidth(tabPlacement, this.runCount, this.maxTabWidth), size.height - insets.bottom - insets.top);
                    break;
                }
                case 3: {
                    final int calculateTabAreaHeight = this.calculateTabAreaHeight(tabPlacement, this.runCount, this.maxTabHeight);
                    graphics.fillRect(insets.left, size.height - insets.bottom - calculateTabAreaHeight, size.width - insets.left - insets.right, calculateTabAreaHeight);
                    break;
                }
                case 4: {
                    final int calculateTabAreaWidth = this.calculateTabAreaWidth(tabPlacement, this.runCount, this.maxTabWidth);
                    graphics.fillRect(size.width - insets.right - calculateTabAreaWidth, insets.top, calculateTabAreaWidth, size.height - insets.top - insets.bottom);
                    break;
                }
                default: {
                    graphics.fillRect(insets.left, insets.top, size.width - insets.right - insets.left, this.calculateTabAreaHeight(tabPlacement, this.runCount, this.maxTabHeight));
                    this.paintHighlightBelowTab();
                    break;
                }
            }
        }
        super.paint(graphics, component);
    }
    
    protected void paintHighlightBelowTab() {
    }
    
    @Override
    protected void paintFocusIndicator(final Graphics graphics, final int n, final Rectangle[] array, final int n2, final Rectangle rectangle, final Rectangle rectangle2, final boolean b) {
        if (this.tabPane.hasFocus() && b) {
            final Rectangle rectangle3 = array[n2];
            final boolean lastInRun = this.isLastInRun(n2);
            graphics.setColor(this.focus);
            graphics.translate(rectangle3.x, rectangle3.y);
            final int n3 = rectangle3.width - 1;
            final int n4 = rectangle3.height - 1;
            final boolean leftToRight = MetalUtils.isLeftToRight(this.tabPane);
            switch (n) {
                case 4: {
                    graphics.drawLine(n3 - 6, 2, n3 - 2, 6);
                    graphics.drawLine(1, 2, n3 - 6, 2);
                    graphics.drawLine(n3 - 2, 6, n3 - 2, n4);
                    graphics.drawLine(1, 2, 1, n4);
                    graphics.drawLine(1, n4, n3 - 2, n4);
                    break;
                }
                case 3: {
                    if (leftToRight) {
                        graphics.drawLine(2, n4 - 6, 6, n4 - 2);
                        graphics.drawLine(6, n4 - 2, n3, n4 - 2);
                        graphics.drawLine(2, 0, 2, n4 - 6);
                        graphics.drawLine(2, 0, n3, 0);
                        graphics.drawLine(n3, 0, n3, n4 - 2);
                        break;
                    }
                    graphics.drawLine(n3 - 2, n4 - 6, n3 - 6, n4 - 2);
                    graphics.drawLine(n3 - 2, 0, n3 - 2, n4 - 6);
                    if (lastInRun) {
                        graphics.drawLine(2, n4 - 2, n3 - 6, n4 - 2);
                        graphics.drawLine(2, 0, n3 - 2, 0);
                        graphics.drawLine(2, 0, 2, n4 - 2);
                        break;
                    }
                    graphics.drawLine(1, n4 - 2, n3 - 6, n4 - 2);
                    graphics.drawLine(1, 0, n3 - 2, 0);
                    graphics.drawLine(1, 0, 1, n4 - 2);
                    break;
                }
                case 2: {
                    graphics.drawLine(2, 6, 6, 2);
                    graphics.drawLine(2, 6, 2, n4 - 1);
                    graphics.drawLine(6, 2, n3, 2);
                    graphics.drawLine(n3, 2, n3, n4 - 1);
                    graphics.drawLine(2, n4 - 1, n3, n4 - 1);
                    break;
                }
                default: {
                    if (leftToRight) {
                        graphics.drawLine(2, 6, 6, 2);
                        graphics.drawLine(2, 6, 2, n4 - 1);
                        graphics.drawLine(6, 2, n3, 2);
                        graphics.drawLine(n3, 2, n3, n4 - 1);
                        graphics.drawLine(2, n4 - 1, n3, n4 - 1);
                        break;
                    }
                    graphics.drawLine(n3 - 2, 6, n3 - 6, 2);
                    graphics.drawLine(n3 - 2, 6, n3 - 2, n4 - 1);
                    if (lastInRun) {
                        graphics.drawLine(n3 - 6, 2, 2, 2);
                        graphics.drawLine(2, 2, 2, n4 - 1);
                        graphics.drawLine(n3 - 2, n4 - 1, 2, n4 - 1);
                        break;
                    }
                    graphics.drawLine(n3 - 6, 2, 1, 2);
                    graphics.drawLine(1, 2, 1, n4 - 1);
                    graphics.drawLine(n3 - 2, n4 - 1, 1, n4 - 1);
                    break;
                }
            }
            graphics.translate(-rectangle3.x, -rectangle3.y);
        }
    }
    
    @Override
    protected void paintContentBorderTopEdge(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final boolean leftToRight = MetalUtils.isLeftToRight(this.tabPane);
        final int n7 = n3 + n5 - 1;
        final Rectangle rectangle = (n2 < 0) ? null : this.getTabBounds(n2, this.calcRect);
        if (this.ocean) {
            graphics.setColor(this.oceanSelectedBorderColor);
        }
        else {
            graphics.setColor(this.selectHighlight);
        }
        if (n != 1 || n2 < 0 || rectangle.y + rectangle.height + 1 < n4 || rectangle.x < n3 || rectangle.x > n3 + n5) {
            graphics.drawLine(n3, n4, n3 + n5 - 2, n4);
            if (this.ocean && n == 1) {
                graphics.setColor(MetalLookAndFeel.getWhite());
                graphics.drawLine(n3, n4 + 1, n3 + n5 - 2, n4 + 1);
            }
        }
        else {
            final boolean lastInRun = this.isLastInRun(n2);
            if (leftToRight || lastInRun) {
                graphics.drawLine(n3, n4, rectangle.x + 1, n4);
            }
            else {
                graphics.drawLine(n3, n4, rectangle.x, n4);
            }
            if (rectangle.x + rectangle.width < n7 - 1) {
                if (leftToRight && !lastInRun) {
                    graphics.drawLine(rectangle.x + rectangle.width, n4, n7 - 1, n4);
                }
                else {
                    graphics.drawLine(rectangle.x + rectangle.width - 1, n4, n7 - 1, n4);
                }
            }
            else {
                graphics.setColor(this.shadow);
                graphics.drawLine(n3 + n5 - 2, n4, n3 + n5 - 2, n4);
            }
            if (this.ocean) {
                graphics.setColor(MetalLookAndFeel.getWhite());
                if (leftToRight || lastInRun) {
                    graphics.drawLine(n3, n4 + 1, rectangle.x + 1, n4 + 1);
                }
                else {
                    graphics.drawLine(n3, n4 + 1, rectangle.x, n4 + 1);
                }
                if (rectangle.x + rectangle.width < n7 - 1) {
                    if (leftToRight && !lastInRun) {
                        graphics.drawLine(rectangle.x + rectangle.width, n4 + 1, n7 - 1, n4 + 1);
                    }
                    else {
                        graphics.drawLine(rectangle.x + rectangle.width - 1, n4 + 1, n7 - 1, n4 + 1);
                    }
                }
                else {
                    graphics.setColor(this.shadow);
                    graphics.drawLine(n3 + n5 - 2, n4 + 1, n3 + n5 - 2, n4 + 1);
                }
            }
        }
    }
    
    @Override
    protected void paintContentBorderBottomEdge(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final boolean leftToRight = MetalUtils.isLeftToRight(this.tabPane);
        final int n7 = n4 + n6 - 1;
        final int n8 = n3 + n5 - 1;
        final Rectangle rectangle = (n2 < 0) ? null : this.getTabBounds(n2, this.calcRect);
        graphics.setColor(this.darkShadow);
        if (n != 3 || n2 < 0 || rectangle.y - 1 > n6 || rectangle.x < n3 || rectangle.x > n3 + n5) {
            if (this.ocean && n == 3) {
                graphics.setColor(this.oceanSelectedBorderColor);
            }
            graphics.drawLine(n3, n4 + n6 - 1, n3 + n5 - 1, n4 + n6 - 1);
        }
        else {
            final boolean lastInRun = this.isLastInRun(n2);
            if (this.ocean) {
                graphics.setColor(this.oceanSelectedBorderColor);
            }
            if (leftToRight || lastInRun) {
                graphics.drawLine(n3, n7, rectangle.x, n7);
            }
            else {
                graphics.drawLine(n3, n7, rectangle.x - 1, n7);
            }
            if (rectangle.x + rectangle.width < n3 + n5 - 2) {
                if (leftToRight && !lastInRun) {
                    graphics.drawLine(rectangle.x + rectangle.width, n7, n8, n7);
                }
                else {
                    graphics.drawLine(rectangle.x + rectangle.width - 1, n7, n8, n7);
                }
            }
        }
    }
    
    @Override
    protected void paintContentBorderLeftEdge(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final Rectangle rectangle = (n2 < 0) ? null : this.getTabBounds(n2, this.calcRect);
        if (this.ocean) {
            graphics.setColor(this.oceanSelectedBorderColor);
        }
        else {
            graphics.setColor(this.selectHighlight);
        }
        if (n != 2 || n2 < 0 || rectangle.x + rectangle.width + 1 < n3 || rectangle.y < n4 || rectangle.y > n4 + n6) {
            graphics.drawLine(n3, n4 + 1, n3, n4 + n6 - 2);
            if (this.ocean && n == 2) {
                graphics.setColor(MetalLookAndFeel.getWhite());
                graphics.drawLine(n3 + 1, n4, n3 + 1, n4 + n6 - 2);
            }
        }
        else {
            graphics.drawLine(n3, n4, n3, rectangle.y + 1);
            if (rectangle.y + rectangle.height < n4 + n6 - 2) {
                graphics.drawLine(n3, rectangle.y + rectangle.height + 1, n3, n4 + n6 + 2);
            }
            if (this.ocean) {
                graphics.setColor(MetalLookAndFeel.getWhite());
                graphics.drawLine(n3 + 1, n4 + 1, n3 + 1, rectangle.y + 1);
                if (rectangle.y + rectangle.height < n4 + n6 - 2) {
                    graphics.drawLine(n3 + 1, rectangle.y + rectangle.height + 1, n3 + 1, n4 + n6 + 2);
                }
            }
        }
    }
    
    @Override
    protected void paintContentBorderRightEdge(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final Rectangle rectangle = (n2 < 0) ? null : this.getTabBounds(n2, this.calcRect);
        graphics.setColor(this.darkShadow);
        if (n != 4 || n2 < 0 || rectangle.x - 1 > n5 || rectangle.y < n4 || rectangle.y > n4 + n6) {
            if (this.ocean && n == 4) {
                graphics.setColor(this.oceanSelectedBorderColor);
            }
            graphics.drawLine(n3 + n5 - 1, n4, n3 + n5 - 1, n4 + n6 - 1);
        }
        else {
            if (this.ocean) {
                graphics.setColor(this.oceanSelectedBorderColor);
            }
            graphics.drawLine(n3 + n5 - 1, n4, n3 + n5 - 1, rectangle.y);
            if (rectangle.y + rectangle.height < n4 + n6 - 2) {
                graphics.drawLine(n3 + n5 - 1, rectangle.y + rectangle.height, n3 + n5 - 1, n4 + n6 - 2);
            }
        }
    }
    
    @Override
    protected int calculateMaxTabHeight(final int n) {
        final int height = this.getFontMetrics().getHeight();
        boolean b = false;
        for (int i = 0; i < this.tabPane.getTabCount(); ++i) {
            final Icon icon = this.tabPane.getIconAt(i);
            if (icon != null && icon.getIconHeight() > height) {
                b = true;
                break;
            }
        }
        return super.calculateMaxTabHeight(n) - (b ? (this.tabInsets.top + this.tabInsets.bottom) : 0);
    }
    
    @Override
    protected int getTabRunOverlay(final int n) {
        if (n == 2 || n == 4) {
            return this.calculateMaxTabHeight(n) / 2;
        }
        return 0;
    }
    
    protected boolean shouldRotateTabRuns(final int n, final int n2) {
        return false;
    }
    
    @Override
    protected boolean shouldPadTabRun(final int n, final int n2) {
        return this.runCount > 1 && n2 < this.runCount - 1;
    }
    
    private boolean isLastInRun(final int n) {
        return n == this.lastTabInRun(this.tabPane.getTabCount(), this.getRunForTab(this.tabPane.getTabCount(), n));
    }
    
    private Color getUnselectedBackgroundAt(final int n) {
        final Color background = this.tabPane.getBackgroundAt(n);
        if (background instanceof UIResource && this.unselectedBackground != null) {
            return this.unselectedBackground;
        }
        return background;
    }
    
    int getRolloverTabIndex() {
        return this.getRolloverTab();
    }
    
    public class TabbedPaneLayout extends BasicTabbedPaneUI.TabbedPaneLayout
    {
        @Override
        protected void normalizeTabRuns(final int n, final int n2, final int n3, final int n4) {
            if (n == 1 || n == 3) {
                super.normalizeTabRuns(n, n2, n3, n4);
            }
        }
        
        @Override
        protected void rotateTabRuns(final int n, final int n2) {
        }
        
        @Override
        protected void padSelectedTab(final int n, final int n2) {
        }
    }
}
