package com.sun.java.swing.plaf.motif;

import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Color;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class MotifTabbedPaneUI extends BasicTabbedPaneUI
{
    protected Color unselectedTabBackground;
    protected Color unselectedTabForeground;
    protected Color unselectedTabShadow;
    protected Color unselectedTabHighlight;
    
    public static ComponentUI createUI(final JComponent component) {
        return new MotifTabbedPaneUI();
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.unselectedTabBackground = UIManager.getColor("TabbedPane.unselectedTabBackground");
        this.unselectedTabForeground = UIManager.getColor("TabbedPane.unselectedTabForeground");
        this.unselectedTabShadow = UIManager.getColor("TabbedPane.unselectedTabShadow");
        this.unselectedTabHighlight = UIManager.getColor("TabbedPane.unselectedTabHighlight");
    }
    
    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
        this.unselectedTabBackground = null;
        this.unselectedTabForeground = null;
        this.unselectedTabShadow = null;
        this.unselectedTabHighlight = null;
    }
    
    @Override
    protected void paintContentBorderTopEdge(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final Rectangle rectangle = (n2 < 0) ? null : this.getTabBounds(n2, this.calcRect);
        graphics.setColor(this.lightHighlight);
        if (n != 1 || n2 < 0 || rectangle.x < n3 || rectangle.x > n3 + n5) {
            graphics.drawLine(n3, n4, n3 + n5 - 2, n4);
        }
        else {
            graphics.drawLine(n3, n4, rectangle.x - 1, n4);
            if (rectangle.x + rectangle.width < n3 + n5 - 2) {
                graphics.drawLine(rectangle.x + rectangle.width, n4, n3 + n5 - 2, n4);
            }
        }
    }
    
    @Override
    protected void paintContentBorderBottomEdge(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final Rectangle rectangle = (n2 < 0) ? null : this.getTabBounds(n2, this.calcRect);
        graphics.setColor(this.shadow);
        if (n != 3 || n2 < 0 || rectangle.x < n3 || rectangle.x > n3 + n5) {
            graphics.drawLine(n3 + 1, n4 + n6 - 1, n3 + n5 - 1, n4 + n6 - 1);
        }
        else {
            graphics.drawLine(n3 + 1, n4 + n6 - 1, rectangle.x - 1, n4 + n6 - 1);
            if (rectangle.x + rectangle.width < n3 + n5 - 2) {
                graphics.drawLine(rectangle.x + rectangle.width, n4 + n6 - 1, n3 + n5 - 2, n4 + n6 - 1);
            }
        }
    }
    
    @Override
    protected void paintContentBorderRightEdge(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final Rectangle rectangle = (n2 < 0) ? null : this.getTabBounds(n2, this.calcRect);
        graphics.setColor(this.shadow);
        if (n != 4 || n2 < 0 || rectangle.y < n4 || rectangle.y > n4 + n6) {
            graphics.drawLine(n3 + n5 - 1, n4 + 1, n3 + n5 - 1, n4 + n6 - 1);
        }
        else {
            graphics.drawLine(n3 + n5 - 1, n4 + 1, n3 + n5 - 1, rectangle.y - 1);
            if (rectangle.y + rectangle.height < n4 + n6 - 2) {
                graphics.drawLine(n3 + n5 - 1, rectangle.y + rectangle.height, n3 + n5 - 1, n4 + n6 - 2);
            }
        }
    }
    
    @Override
    protected void paintTabBackground(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final boolean b) {
        graphics.setColor(b ? this.tabPane.getBackgroundAt(n2) : this.unselectedTabBackground);
        switch (n) {
            case 2: {
                graphics.fillRect(n3 + 1, n4 + 1, n5 - 1, n6 - 2);
                break;
            }
            case 4: {
                graphics.fillRect(n3, n4 + 1, n5 - 1, n6 - 2);
                break;
            }
            case 3: {
                graphics.fillRect(n3 + 1, n4, n5 - 2, n6 - 3);
                graphics.drawLine(n3 + 2, n4 + n6 - 3, n3 + n5 - 3, n4 + n6 - 3);
                graphics.drawLine(n3 + 3, n4 + n6 - 2, n3 + n5 - 4, n4 + n6 - 2);
                break;
            }
            default: {
                graphics.fillRect(n3 + 1, n4 + 3, n5 - 2, n6 - 3);
                graphics.drawLine(n3 + 2, n4 + 2, n3 + n5 - 3, n4 + 2);
                graphics.drawLine(n3 + 3, n4 + 1, n3 + n5 - 4, n4 + 1);
                break;
            }
        }
    }
    
    @Override
    protected void paintTabBorder(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final boolean b) {
        graphics.setColor(b ? this.lightHighlight : this.unselectedTabHighlight);
        switch (n) {
            case 2: {
                graphics.drawLine(n3, n4 + 2, n3, n4 + n6 - 3);
                graphics.drawLine(n3 + 1, n4 + 1, n3 + 1, n4 + 2);
                graphics.drawLine(n3 + 2, n4, n3 + 2, n4 + 1);
                graphics.drawLine(n3 + 3, n4, n3 + n5 - 1, n4);
                graphics.setColor(b ? this.shadow : this.unselectedTabShadow);
                graphics.drawLine(n3 + 1, n4 + n6 - 3, n3 + 1, n4 + n6 - 2);
                graphics.drawLine(n3 + 2, n4 + n6 - 2, n3 + 2, n4 + n6 - 1);
                graphics.drawLine(n3 + 3, n4 + n6 - 1, n3 + n5 - 1, n4 + n6 - 1);
                break;
            }
            case 4: {
                graphics.drawLine(n3, n4, n3 + n5 - 3, n4);
                graphics.setColor(b ? this.shadow : this.unselectedTabShadow);
                graphics.drawLine(n3 + n5 - 3, n4, n3 + n5 - 3, n4 + 1);
                graphics.drawLine(n3 + n5 - 2, n4 + 1, n3 + n5 - 2, n4 + 2);
                graphics.drawLine(n3 + n5 - 1, n4 + 2, n3 + n5 - 1, n4 + n6 - 3);
                graphics.drawLine(n3 + n5 - 2, n4 + n6 - 3, n3 + n5 - 2, n4 + n6 - 2);
                graphics.drawLine(n3 + n5 - 3, n4 + n6 - 2, n3 + n5 - 3, n4 + n6 - 1);
                graphics.drawLine(n3, n4 + n6 - 1, n3 + n5 - 3, n4 + n6 - 1);
                break;
            }
            case 3: {
                graphics.drawLine(n3, n4, n3, n4 + n6 - 3);
                graphics.drawLine(n3 + 1, n4 + n6 - 3, n3 + 1, n4 + n6 - 2);
                graphics.drawLine(n3 + 2, n4 + n6 - 2, n3 + 2, n4 + n6 - 1);
                graphics.setColor(b ? this.shadow : this.unselectedTabShadow);
                graphics.drawLine(n3 + 3, n4 + n6 - 1, n3 + n5 - 4, n4 + n6 - 1);
                graphics.drawLine(n3 + n5 - 3, n4 + n6 - 2, n3 + n5 - 3, n4 + n6 - 1);
                graphics.drawLine(n3 + n5 - 2, n4 + n6 - 3, n3 + n5 - 2, n4 + n6 - 2);
                graphics.drawLine(n3 + n5 - 1, n4, n3 + n5 - 1, n4 + n6 - 3);
                break;
            }
            default: {
                graphics.drawLine(n3, n4 + 2, n3, n4 + n6 - 1);
                graphics.drawLine(n3 + 1, n4 + 1, n3 + 1, n4 + 2);
                graphics.drawLine(n3 + 2, n4, n3 + 2, n4 + 1);
                graphics.drawLine(n3 + 3, n4, n3 + n5 - 4, n4);
                graphics.setColor(b ? this.shadow : this.unselectedTabShadow);
                graphics.drawLine(n3 + n5 - 3, n4, n3 + n5 - 3, n4 + 1);
                graphics.drawLine(n3 + n5 - 2, n4 + 1, n3 + n5 - 2, n4 + 2);
                graphics.drawLine(n3 + n5 - 1, n4 + 2, n3 + n5 - 1, n4 + n6 - 1);
                break;
            }
        }
    }
    
    @Override
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
                    n5 = rectangle3.width - 6;
                    n6 = rectangle3.height - 7;
                    break;
                }
                case 4: {
                    n3 = rectangle3.x + 2;
                    n4 = rectangle3.y + 3;
                    n5 = rectangle3.width - 6;
                    n6 = rectangle3.height - 7;
                    break;
                }
                case 3: {
                    n3 = rectangle3.x + 3;
                    n4 = rectangle3.y + 2;
                    n5 = rectangle3.width - 7;
                    n6 = rectangle3.height - 6;
                    break;
                }
                default: {
                    n3 = rectangle3.x + 3;
                    n4 = rectangle3.y + 3;
                    n5 = rectangle3.width - 7;
                    n6 = rectangle3.height - 6;
                    break;
                }
            }
            graphics.drawRect(n3, n4, n5, n6);
        }
    }
    
    @Override
    protected int getTabRunIndent(final int n, final int n2) {
        return n2 * 3;
    }
    
    @Override
    protected int getTabRunOverlay(final int n) {
        this.tabRunOverlay = ((n == 2 || n == 4) ? ((int)Math.round((float)this.maxTabWidth * 0.1)) : ((int)Math.round((float)this.maxTabHeight * 0.22)));
        switch (n) {
            case 2: {
                if (this.tabRunOverlay > this.tabInsets.right - 2) {
                    this.tabRunOverlay = this.tabInsets.right - 2;
                    break;
                }
                break;
            }
            case 4: {
                if (this.tabRunOverlay > this.tabInsets.left - 2) {
                    this.tabRunOverlay = this.tabInsets.left - 2;
                    break;
                }
                break;
            }
            case 1: {
                if (this.tabRunOverlay > this.tabInsets.bottom - 2) {
                    this.tabRunOverlay = this.tabInsets.bottom - 2;
                    break;
                }
                break;
            }
            case 3: {
                if (this.tabRunOverlay > this.tabInsets.top - 2) {
                    this.tabRunOverlay = this.tabInsets.top - 2;
                    break;
                }
                break;
            }
        }
        return this.tabRunOverlay;
    }
}
