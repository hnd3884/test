package org.jfree.ui;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.Icon;

public class BevelArrowIcon implements Icon
{
    public static final int UP = 0;
    public static final int DOWN = 1;
    private static final int DEFAULT_SIZE = 11;
    private Color edge1;
    private Color edge2;
    private Color fill;
    private int size;
    private int direction;
    
    public BevelArrowIcon(final int direction, final boolean isRaisedView, final boolean isPressedView) {
        if (isRaisedView) {
            if (isPressedView) {
                this.init(UIManager.getColor("controlLtHighlight"), UIManager.getColor("controlDkShadow"), UIManager.getColor("controlShadow"), 11, direction);
            }
            else {
                this.init(UIManager.getColor("controlHighlight"), UIManager.getColor("controlShadow"), UIManager.getColor("control"), 11, direction);
            }
        }
        else if (isPressedView) {
            this.init(UIManager.getColor("controlDkShadow"), UIManager.getColor("controlLtHighlight"), UIManager.getColor("controlShadow"), 11, direction);
        }
        else {
            this.init(UIManager.getColor("controlShadow"), UIManager.getColor("controlHighlight"), UIManager.getColor("control"), 11, direction);
        }
    }
    
    public BevelArrowIcon(final Color edge1, final Color edge2, final Color fill, final int size, final int direction) {
        this.init(edge1, edge2, fill, size, direction);
    }
    
    private void drawDownArrow(final Graphics g, final int xo, final int yo) {
        g.setColor(this.edge1);
        g.drawLine(xo, yo, xo + this.size - 1, yo);
        g.drawLine(xo, yo + 1, xo + this.size - 3, yo + 1);
        g.setColor(this.edge2);
        g.drawLine(xo + this.size - 2, yo + 1, xo + this.size - 1, yo + 1);
        int x = xo + 1;
        for (int y = yo + 2, dx = this.size - 6; y + 1 < yo + this.size; y += 2, dx -= 2) {
            g.setColor(this.edge1);
            g.drawLine(x, y, x + 1, y);
            g.drawLine(x, y + 1, x + 1, y + 1);
            if (dx > 0) {
                g.setColor(this.fill);
                g.drawLine(x + 2, y, x + 1 + dx, y);
                g.drawLine(x + 2, y + 1, x + 1 + dx, y + 1);
            }
            g.setColor(this.edge2);
            g.drawLine(x + dx + 2, y, x + dx + 3, y);
            g.drawLine(x + dx + 2, y + 1, x + dx + 3, y + 1);
            ++x;
        }
        g.setColor(this.edge1);
        g.drawLine(xo + this.size / 2, yo + this.size - 1, xo + this.size / 2, yo + this.size - 1);
    }
    
    private void drawUpArrow(final Graphics g, final int xo, final int yo) {
        g.setColor(this.edge1);
        int x = xo + this.size / 2;
        g.drawLine(x, yo, x, yo);
        --x;
        for (int y = yo + 1, dx = 0; y + 3 < yo + this.size; y += 2, dx += 2) {
            g.setColor(this.edge1);
            g.drawLine(x, y, x + 1, y);
            g.drawLine(x, y + 1, x + 1, y + 1);
            if (dx > 0) {
                g.setColor(this.fill);
                g.drawLine(x + 2, y, x + 1 + dx, y);
                g.drawLine(x + 2, y + 1, x + 1 + dx, y + 1);
            }
            g.setColor(this.edge2);
            g.drawLine(x + dx + 2, y, x + dx + 3, y);
            g.drawLine(x + dx + 2, y + 1, x + dx + 3, y + 1);
            --x;
        }
        g.setColor(this.edge1);
        g.drawLine(xo, yo + this.size - 3, xo + 1, yo + this.size - 3);
        g.setColor(this.edge2);
        g.drawLine(xo + 2, yo + this.size - 2, xo + this.size - 1, yo + this.size - 2);
        g.drawLine(xo, yo + this.size - 1, xo + this.size, yo + this.size - 1);
    }
    
    public int getIconHeight() {
        return this.size;
    }
    
    public int getIconWidth() {
        return this.size;
    }
    
    private void init(final Color edge1, final Color edge2, final Color fill, final int size, final int direction) {
        this.edge1 = edge1;
        this.edge2 = edge2;
        this.fill = fill;
        this.size = size;
        this.direction = direction;
    }
    
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        switch (this.direction) {
            case 1: {
                this.drawDownArrow(g, x, y);
                break;
            }
            case 0: {
                this.drawUpArrow(g, x, y);
                break;
            }
        }
    }
}
