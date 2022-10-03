package com.sun.java.swing.plaf.motif;

import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.plaf.basic.BasicArrowButton;

public class MotifScrollBarButton extends BasicArrowButton
{
    private Color darkShadow;
    private Color lightShadow;
    
    public MotifScrollBarButton(final int direction) {
        super(direction);
        this.darkShadow = UIManager.getColor("controlShadow");
        this.lightShadow = UIManager.getColor("controlLtHighlight");
        switch (direction) {
            case 1:
            case 3:
            case 5:
            case 7: {
                this.direction = direction;
                this.setRequestFocusEnabled(false);
                this.setOpaque(true);
                this.setBackground(UIManager.getColor("ScrollBar.background"));
                this.setForeground(UIManager.getColor("ScrollBar.foreground"));
                return;
            }
            default: {
                throw new IllegalArgumentException("invalid direction");
            }
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        switch (this.direction) {
            case 1:
            case 5: {
                return new Dimension(11, 12);
            }
            default: {
                return new Dimension(12, 11);
            }
        }
    }
    
    @Override
    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }
    
    @Override
    public Dimension getMaximumSize() {
        return this.getPreferredSize();
    }
    
    @Override
    public boolean isFocusTraversable() {
        return false;
    }
    
    @Override
    public void paint(final Graphics graphics) {
        final int width = this.getWidth();
        final int height = this.getHeight();
        if (this.isOpaque()) {
            graphics.setColor(this.getBackground());
            graphics.fillRect(0, 0, width, height);
        }
        final boolean pressed = this.getModel().isPressed();
        final Color color = pressed ? this.darkShadow : this.lightShadow;
        final Color color2 = pressed ? this.lightShadow : this.darkShadow;
        final Color background = this.getBackground();
        final int n = width / 2;
        final int n2 = height / 2;
        final int min = Math.min(width, height);
        switch (this.direction) {
            case 1: {
                graphics.setColor(color);
                graphics.drawLine(n, 0, n, 0);
                int n3 = n - 1;
                int i = 1;
                int n4 = 1;
                while (i <= min - 2) {
                    graphics.setColor(color);
                    graphics.drawLine(n3, i, n3, i);
                    if (i >= min - 2) {
                        graphics.drawLine(n3, i + 1, n3, i + 1);
                    }
                    graphics.setColor(background);
                    graphics.drawLine(n3 + 1, i, n3 + n4, i);
                    if (i < min - 2) {
                        graphics.drawLine(n3, i + 1, n3 + n4 + 1, i + 1);
                    }
                    graphics.setColor(color2);
                    graphics.drawLine(n3 + n4 + 1, i, n3 + n4 + 1, i);
                    if (i >= min - 2) {
                        graphics.drawLine(n3 + 1, i + 1, n3 + n4 + 1, i + 1);
                    }
                    n4 += 2;
                    --n3;
                    i += 2;
                }
                break;
            }
            case 5: {
                graphics.setColor(color2);
                graphics.drawLine(n, min, n, min);
                int n5 = n - 1;
                int j = min - 1;
                int n6 = 1;
                while (j >= 1) {
                    graphics.setColor(color);
                    graphics.drawLine(n5, j, n5, j);
                    if (j <= 2) {
                        graphics.drawLine(n5, j - 1, n5 + n6 + 1, j - 1);
                    }
                    graphics.setColor(background);
                    graphics.drawLine(n5 + 1, j, n5 + n6, j);
                    if (j > 2) {
                        graphics.drawLine(n5, j - 1, n5 + n6 + 1, j - 1);
                    }
                    graphics.setColor(color2);
                    graphics.drawLine(n5 + n6 + 1, j, n5 + n6 + 1, j);
                    n6 += 2;
                    --n5;
                    j -= 2;
                }
                break;
            }
            case 3: {
                graphics.setColor(color);
                graphics.drawLine(min, n2, min, n2);
                int n7 = n2 - 1;
                int k = min - 1;
                int n8 = 1;
                while (k >= 1) {
                    graphics.setColor(color);
                    graphics.drawLine(k, n7, k, n7);
                    if (k <= 2) {
                        graphics.drawLine(k - 1, n7, k - 1, n7 + n8 + 1);
                    }
                    graphics.setColor(background);
                    graphics.drawLine(k, n7 + 1, k, n7 + n8);
                    if (k > 2) {
                        graphics.drawLine(k - 1, n7, k - 1, n7 + n8 + 1);
                    }
                    graphics.setColor(color2);
                    graphics.drawLine(k, n7 + n8 + 1, k, n7 + n8 + 1);
                    n8 += 2;
                    --n7;
                    k -= 2;
                }
                break;
            }
            case 7: {
                graphics.setColor(color2);
                graphics.drawLine(0, n2, 0, n2);
                int n9 = n2 - 1;
                int l = 1;
                int n10 = 1;
                while (l <= min - 2) {
                    graphics.setColor(color);
                    graphics.drawLine(l, n9, l, n9);
                    if (l >= min - 2) {
                        graphics.drawLine(l + 1, n9, l + 1, n9);
                    }
                    graphics.setColor(background);
                    graphics.drawLine(l, n9 + 1, l, n9 + n10);
                    if (l < min - 2) {
                        graphics.drawLine(l + 1, n9, l + 1, n9 + n10 + 1);
                    }
                    graphics.setColor(color2);
                    graphics.drawLine(l, n9 + n10 + 1, l, n9 + n10 + 1);
                    if (l >= min - 2) {
                        graphics.drawLine(l + 1, n9 + 1, l + 1, n9 + n10 + 1);
                    }
                    n10 += 2;
                    --n9;
                    l += 2;
                }
                break;
            }
        }
    }
}
