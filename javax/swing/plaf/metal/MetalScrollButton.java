package javax.swing.plaf.metal;

import java.awt.Dimension;
import javax.swing.plaf.ColorUIResource;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.plaf.basic.BasicArrowButton;

public class MetalScrollButton extends BasicArrowButton
{
    private static Color shadowColor;
    private static Color highlightColor;
    private boolean isFreeStanding;
    private int buttonWidth;
    
    public MetalScrollButton(final int n, final int buttonWidth, final boolean isFreeStanding) {
        super(n);
        this.isFreeStanding = false;
        MetalScrollButton.shadowColor = UIManager.getColor("ScrollBar.darkShadow");
        MetalScrollButton.highlightColor = UIManager.getColor("ScrollBar.highlight");
        this.buttonWidth = buttonWidth;
        this.isFreeStanding = isFreeStanding;
    }
    
    public void setFreeStanding(final boolean isFreeStanding) {
        this.isFreeStanding = isFreeStanding;
    }
    
    @Override
    public void paint(final Graphics graphics) {
        final boolean leftToRight = MetalUtils.isLeftToRight(this);
        final boolean enabled = this.getParent().isEnabled();
        final ColorUIResource colorUIResource = enabled ? MetalLookAndFeel.getControlInfo() : MetalLookAndFeel.getControlDisabled();
        final boolean pressed = this.getModel().isPressed();
        int width = this.getWidth();
        int height = this.getHeight();
        final int n = width;
        final int n2 = height;
        final int n3 = (height + 1) / 4;
        if (pressed) {
            graphics.setColor(MetalLookAndFeel.getControlShadow());
        }
        else {
            graphics.setColor(this.getBackground());
        }
        graphics.fillRect(0, 0, width, height);
        if (this.getDirection() == 1) {
            if (!this.isFreeStanding) {
                ++height;
                graphics.translate(0, -1);
                width += 2;
                if (!leftToRight) {
                    graphics.translate(-1, 0);
                }
            }
            graphics.setColor(colorUIResource);
            final int n4 = (n2 + 1 - n3) / 2;
            final int n5 = n / 2;
            for (int i = 0; i < n3; ++i) {
                graphics.drawLine(n5 - i, n4 + i, n5 + i + 1, n4 + i);
            }
            if (enabled) {
                graphics.setColor(MetalScrollButton.highlightColor);
                if (!pressed) {
                    graphics.drawLine(1, 1, width - 3, 1);
                    graphics.drawLine(1, 1, 1, height - 1);
                }
                graphics.drawLine(width - 1, 1, width - 1, height - 1);
                graphics.setColor(MetalScrollButton.shadowColor);
                graphics.drawLine(0, 0, width - 2, 0);
                graphics.drawLine(0, 0, 0, height - 1);
                graphics.drawLine(width - 2, 2, width - 2, height - 1);
            }
            else {
                MetalUtils.drawDisabledBorder(graphics, 0, 0, width, height + 1);
            }
            if (!this.isFreeStanding) {
                --height;
                graphics.translate(0, 1);
                width -= 2;
                if (!leftToRight) {
                    graphics.translate(1, 0);
                }
            }
        }
        else if (this.getDirection() == 5) {
            if (!this.isFreeStanding) {
                ++height;
                width += 2;
                if (!leftToRight) {
                    graphics.translate(-1, 0);
                }
            }
            graphics.setColor(colorUIResource);
            final int n6 = (n2 + 1 - n3) / 2 + n3 - 1;
            final int n7 = n / 2;
            for (int j = 0; j < n3; ++j) {
                graphics.drawLine(n7 - j, n6 - j, n7 + j + 1, n6 - j);
            }
            if (enabled) {
                graphics.setColor(MetalScrollButton.highlightColor);
                if (!pressed) {
                    graphics.drawLine(1, 0, width - 3, 0);
                    graphics.drawLine(1, 0, 1, height - 3);
                }
                graphics.drawLine(1, height - 1, width - 1, height - 1);
                graphics.drawLine(width - 1, 0, width - 1, height - 1);
                graphics.setColor(MetalScrollButton.shadowColor);
                graphics.drawLine(0, 0, 0, height - 2);
                graphics.drawLine(width - 2, 0, width - 2, height - 2);
                graphics.drawLine(2, height - 2, width - 2, height - 2);
            }
            else {
                MetalUtils.drawDisabledBorder(graphics, 0, -1, width, height + 1);
            }
            if (!this.isFreeStanding) {
                --height;
                width -= 2;
                if (!leftToRight) {
                    graphics.translate(1, 0);
                }
            }
        }
        else if (this.getDirection() == 3) {
            if (!this.isFreeStanding) {
                height += 2;
                ++width;
            }
            graphics.setColor(colorUIResource);
            final int n8 = (n + 1 - n3) / 2 + n3 - 1;
            final int n9 = n2 / 2;
            for (int k = 0; k < n3; ++k) {
                graphics.drawLine(n8 - k, n9 - k, n8 - k, n9 + k + 1);
            }
            if (enabled) {
                graphics.setColor(MetalScrollButton.highlightColor);
                if (!pressed) {
                    graphics.drawLine(0, 1, width - 3, 1);
                    graphics.drawLine(0, 1, 0, height - 3);
                }
                graphics.drawLine(width - 1, 1, width - 1, height - 1);
                graphics.drawLine(0, height - 1, width - 1, height - 1);
                graphics.setColor(MetalScrollButton.shadowColor);
                graphics.drawLine(0, 0, width - 2, 0);
                graphics.drawLine(width - 2, 2, width - 2, height - 2);
                graphics.drawLine(0, height - 2, width - 2, height - 2);
            }
            else {
                MetalUtils.drawDisabledBorder(graphics, -1, 0, width + 1, height);
            }
            if (!this.isFreeStanding) {
                height -= 2;
                --width;
            }
        }
        else if (this.getDirection() == 7) {
            if (!this.isFreeStanding) {
                height += 2;
                ++width;
                graphics.translate(-1, 0);
            }
            graphics.setColor(colorUIResource);
            final int n10 = (n + 1 - n3) / 2;
            final int n11 = n2 / 2;
            for (int l = 0; l < n3; ++l) {
                graphics.drawLine(n10 + l, n11 - l, n10 + l, n11 + l + 1);
            }
            if (enabled) {
                graphics.setColor(MetalScrollButton.highlightColor);
                if (!pressed) {
                    graphics.drawLine(1, 1, width - 1, 1);
                    graphics.drawLine(1, 1, 1, height - 3);
                }
                graphics.drawLine(1, height - 1, width - 1, height - 1);
                graphics.setColor(MetalScrollButton.shadowColor);
                graphics.drawLine(0, 0, width - 1, 0);
                graphics.drawLine(0, 0, 0, height - 2);
                graphics.drawLine(2, height - 2, width - 1, height - 2);
            }
            else {
                MetalUtils.drawDisabledBorder(graphics, 0, 0, width + 1, height);
            }
            if (!this.isFreeStanding) {
                height -= 2;
                --width;
                graphics.translate(1, 0);
            }
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        if (this.getDirection() == 1) {
            return new Dimension(this.buttonWidth, this.buttonWidth - 2);
        }
        if (this.getDirection() == 5) {
            return new Dimension(this.buttonWidth, this.buttonWidth - (this.isFreeStanding ? 1 : 2));
        }
        if (this.getDirection() == 3) {
            return new Dimension(this.buttonWidth - (this.isFreeStanding ? 1 : 2), this.buttonWidth);
        }
        if (this.getDirection() == 7) {
            return new Dimension(this.buttonWidth - 2, this.buttonWidth);
        }
        return new Dimension(0, 0);
    }
    
    @Override
    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }
    
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    public int getButtonWidth() {
        return this.buttonWidth;
    }
}
