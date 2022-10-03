package javax.swing.plaf.basic;

import java.awt.Dimension;
import javax.swing.plaf.UIResource;
import java.awt.Graphics;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.JButton;

public class BasicArrowButton extends JButton implements SwingConstants
{
    protected int direction;
    private Color shadow;
    private Color darkShadow;
    private Color highlight;
    
    public BasicArrowButton(final int direction, final Color background, final Color shadow, final Color darkShadow, final Color highlight) {
        this.setRequestFocusEnabled(false);
        this.setDirection(direction);
        this.setBackground(background);
        this.shadow = shadow;
        this.darkShadow = darkShadow;
        this.highlight = highlight;
    }
    
    public BasicArrowButton(final int n) {
        this(n, UIManager.getColor("control"), UIManager.getColor("controlShadow"), UIManager.getColor("controlDkShadow"), UIManager.getColor("controlLtHighlight"));
    }
    
    public int getDirection() {
        return this.direction;
    }
    
    public void setDirection(final int direction) {
        this.direction = direction;
    }
    
    @Override
    public void paint(final Graphics graphics) {
        final int width = this.getSize().width;
        final int height = this.getSize().height;
        final Color color = graphics.getColor();
        final boolean pressed = this.getModel().isPressed();
        final boolean enabled = this.isEnabled();
        graphics.setColor(this.getBackground());
        graphics.fillRect(1, 1, width - 2, height - 2);
        if (this.getBorder() != null && !(this.getBorder() instanceof UIResource)) {
            this.paintBorder(graphics);
        }
        else if (pressed) {
            graphics.setColor(this.shadow);
            graphics.drawRect(0, 0, width - 1, height - 1);
        }
        else {
            graphics.drawLine(0, 0, 0, height - 1);
            graphics.drawLine(1, 0, width - 2, 0);
            graphics.setColor(this.highlight);
            graphics.drawLine(1, 1, 1, height - 3);
            graphics.drawLine(2, 1, width - 3, 1);
            graphics.setColor(this.shadow);
            graphics.drawLine(1, height - 2, width - 2, height - 2);
            graphics.drawLine(width - 2, 1, width - 2, height - 3);
            graphics.setColor(this.darkShadow);
            graphics.drawLine(0, height - 1, width - 1, height - 1);
            graphics.drawLine(width - 1, height - 1, width - 1, 0);
        }
        if (height < 5 || width < 5) {
            graphics.setColor(color);
            return;
        }
        if (pressed) {
            graphics.translate(1, 1);
        }
        final int max = Math.max(Math.min((height - 4) / 3, (width - 4) / 3), 2);
        this.paintTriangle(graphics, (width - max) / 2, (height - max) / 2, max, this.direction, enabled);
        if (pressed) {
            graphics.translate(-1, -1);
        }
        graphics.setColor(color);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(16, 16);
    }
    
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(5, 5);
    }
    
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    @Override
    public boolean isFocusTraversable() {
        return false;
    }
    
    public void paintTriangle(final Graphics graphics, final int n, final int n2, int max, final int n3, final boolean b) {
        final Color color = graphics.getColor();
        int n4 = 0;
        max = Math.max(max, 2);
        final int n5 = max / 2 - 1;
        graphics.translate(n, n2);
        if (b) {
            graphics.setColor(this.darkShadow);
        }
        else {
            graphics.setColor(this.shadow);
        }
        switch (n3) {
            case 1: {
                int i;
                for (i = 0; i < max; ++i) {
                    graphics.drawLine(n5 - i, i, n5 + i, i);
                }
                if (!b) {
                    graphics.setColor(this.highlight);
                    graphics.drawLine(n5 - i + 2, i, n5 + i, i);
                    break;
                }
                break;
            }
            case 5: {
                if (!b) {
                    graphics.translate(1, 1);
                    graphics.setColor(this.highlight);
                    for (int j = max - 1; j >= 0; --j) {
                        graphics.drawLine(n5 - j, n4, n5 + j, n4);
                        ++n4;
                    }
                    graphics.translate(-1, -1);
                    graphics.setColor(this.shadow);
                }
                int n6 = 0;
                for (int k = max - 1; k >= 0; --k) {
                    graphics.drawLine(n5 - k, n6, n5 + k, n6);
                    ++n6;
                }
                break;
            }
            case 7: {
                int l;
                for (l = 0; l < max; ++l) {
                    graphics.drawLine(l, n5 - l, l, n5 + l);
                }
                if (!b) {
                    graphics.setColor(this.highlight);
                    graphics.drawLine(l, n5 - l + 2, l, n5 + l);
                    break;
                }
                break;
            }
            case 3: {
                if (!b) {
                    graphics.translate(1, 1);
                    graphics.setColor(this.highlight);
                    for (int n7 = max - 1; n7 >= 0; --n7) {
                        graphics.drawLine(n4, n5 - n7, n4, n5 + n7);
                        ++n4;
                    }
                    graphics.translate(-1, -1);
                    graphics.setColor(this.shadow);
                }
                int n8 = 0;
                for (int n9 = max - 1; n9 >= 0; --n9) {
                    graphics.drawLine(n8, n5 - n9, n8, n5 + n9);
                    ++n8;
                }
                break;
            }
        }
        graphics.translate(-n, -n2);
        graphics.setColor(color);
    }
}
