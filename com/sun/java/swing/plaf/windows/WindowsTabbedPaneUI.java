package com.sun.java.swing.plaf.windows;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.AWTKeyStroke;
import java.util.HashSet;
import javax.swing.UIManager;
import javax.swing.KeyStroke;
import java.util.Set;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class WindowsTabbedPaneUI extends BasicTabbedPaneUI
{
    private static Set<KeyStroke> managingFocusForwardTraversalKeys;
    private static Set<KeyStroke> managingFocusBackwardTraversalKeys;
    private boolean contentOpaque;
    
    public WindowsTabbedPaneUI() {
        this.contentOpaque = true;
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.contentOpaque = UIManager.getBoolean("TabbedPane.contentOpaque");
        if (WindowsTabbedPaneUI.managingFocusForwardTraversalKeys == null) {
            (WindowsTabbedPaneUI.managingFocusForwardTraversalKeys = new HashSet<KeyStroke>()).add(KeyStroke.getKeyStroke(9, 0));
        }
        this.tabPane.setFocusTraversalKeys(0, WindowsTabbedPaneUI.managingFocusForwardTraversalKeys);
        if (WindowsTabbedPaneUI.managingFocusBackwardTraversalKeys == null) {
            (WindowsTabbedPaneUI.managingFocusBackwardTraversalKeys = new HashSet<KeyStroke>()).add(KeyStroke.getKeyStroke(9, 1));
        }
        this.tabPane.setFocusTraversalKeys(1, WindowsTabbedPaneUI.managingFocusBackwardTraversalKeys);
    }
    
    @Override
    protected void uninstallDefaults() {
        this.tabPane.setFocusTraversalKeys(0, null);
        this.tabPane.setFocusTraversalKeys(1, null);
        super.uninstallDefaults();
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsTabbedPaneUI();
    }
    
    @Override
    protected void setRolloverTab(final int rolloverTab) {
        if (XPStyle.getXP() != null) {
            final int rolloverTab2 = this.getRolloverTab();
            super.setRolloverTab(rolloverTab);
            Rectangle tabBounds = null;
            Rectangle tabBounds2 = null;
            if (rolloverTab2 >= 0 && rolloverTab2 < this.tabPane.getTabCount()) {
                tabBounds = this.getTabBounds(this.tabPane, rolloverTab2);
            }
            if (rolloverTab >= 0) {
                tabBounds2 = this.getTabBounds(this.tabPane, rolloverTab);
            }
            if (tabBounds != null) {
                if (tabBounds2 != null) {
                    this.tabPane.repaint(tabBounds.union(tabBounds2));
                }
                else {
                    this.tabPane.repaint(tabBounds);
                }
            }
            else if (tabBounds2 != null) {
                this.tabPane.repaint(tabBounds2);
            }
        }
    }
    
    @Override
    protected void paintContentBorder(final Graphics graphics, final int n, final int n2) {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null && (this.contentOpaque || this.tabPane.isOpaque())) {
            final XPStyle.Skin skin = xp.getSkin(this.tabPane, TMSchema.Part.TABP_PANE);
            if (skin != null) {
                final Insets insets = this.tabPane.getInsets();
                final Insets insets2 = UIManager.getInsets("TabbedPane.tabAreaInsets");
                int left = insets.left;
                int top = insets.top;
                int n3 = this.tabPane.getWidth() - insets.right - insets.left;
                int n4 = this.tabPane.getHeight() - insets.top - insets.bottom;
                if (n == 2 || n == 4) {
                    final int calculateTabAreaWidth = this.calculateTabAreaWidth(n, this.runCount, this.maxTabWidth);
                    if (n == 2) {
                        left += calculateTabAreaWidth - insets2.bottom;
                    }
                    n3 -= calculateTabAreaWidth - insets2.bottom;
                }
                else {
                    final int calculateTabAreaHeight = this.calculateTabAreaHeight(n, this.runCount, this.maxTabHeight);
                    if (n == 1) {
                        top += calculateTabAreaHeight - insets2.bottom;
                    }
                    n4 -= calculateTabAreaHeight - insets2.bottom;
                }
                this.paintRotatedSkin(graphics, skin, n, left, top, n3, n4, null);
                return;
            }
        }
        super.paintContentBorder(graphics, n, n2);
    }
    
    @Override
    protected void paintTabBackground(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final boolean b) {
        if (XPStyle.getXP() == null) {
            super.paintTabBackground(graphics, n, n2, n3, n4, n5, n6, b);
        }
    }
    
    @Override
    protected void paintTabBorder(final Graphics graphics, final int n, final int n2, final int n3, final int n4, int n5, int n6, final boolean b) {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            final int tabCount = this.tabPane.getTabCount();
            final int runForTab = this.getRunForTab(tabCount, n2);
            TMSchema.Part part;
            if (this.tabRuns[runForTab] == n2) {
                part = TMSchema.Part.TABP_TABITEMLEFTEDGE;
            }
            else if (tabCount > 1 && this.lastTabInRun(tabCount, runForTab) == n2) {
                part = TMSchema.Part.TABP_TABITEMRIGHTEDGE;
                if (b) {
                    if (n == 1 || n == 3) {
                        ++n5;
                    }
                    else {
                        ++n6;
                    }
                }
            }
            else {
                part = TMSchema.Part.TABP_TABITEM;
            }
            TMSchema.State state = TMSchema.State.NORMAL;
            if (b) {
                state = TMSchema.State.SELECTED;
            }
            else if (n2 == this.getRolloverTab()) {
                state = TMSchema.State.HOT;
            }
            this.paintRotatedSkin(graphics, xp.getSkin(this.tabPane, part), n, n3, n4, n5, n6, state);
        }
        else {
            super.paintTabBorder(graphics, n, n2, n3, n4, n5, n6, b);
        }
    }
    
    private void paintRotatedSkin(final Graphics graphics, final XPStyle.Skin skin, final int n, final int n2, final int n3, final int n4, final int n5, final TMSchema.State state) {
        final Graphics2D graphics2D = (Graphics2D)graphics.create();
        graphics2D.translate(n2, n3);
        switch (n) {
            case 4: {
                graphics2D.translate(n4, 0);
                graphics2D.rotate(Math.toRadians(90.0));
                skin.paintSkin(graphics2D, 0, 0, n5, n4, state);
                break;
            }
            case 2: {
                graphics2D.scale(-1.0, 1.0);
                graphics2D.rotate(Math.toRadians(90.0));
                skin.paintSkin(graphics2D, 0, 0, n5, n4, state);
                break;
            }
            case 3: {
                graphics2D.translate(0, n5);
                graphics2D.scale(-1.0, 1.0);
                graphics2D.rotate(Math.toRadians(180.0));
                skin.paintSkin(graphics2D, 0, 0, n4, n5, state);
                break;
            }
            default: {
                skin.paintSkin(graphics2D, 0, 0, n4, n5, state);
                break;
            }
        }
        graphics2D.dispose();
    }
}
