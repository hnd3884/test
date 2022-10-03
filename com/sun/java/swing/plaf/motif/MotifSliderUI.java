package com.sun.java.swing.plaf.motif;

import java.awt.Rectangle;
import sun.swing.SwingUtilities2;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.JSlider;
import java.awt.Dimension;
import javax.swing.plaf.basic.BasicSliderUI;

public class MotifSliderUI extends BasicSliderUI
{
    static final Dimension PREFERRED_HORIZONTAL_SIZE;
    static final Dimension PREFERRED_VERTICAL_SIZE;
    static final Dimension MINIMUM_HORIZONTAL_SIZE;
    static final Dimension MINIMUM_VERTICAL_SIZE;
    
    public MotifSliderUI(final JSlider slider) {
        super(slider);
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new MotifSliderUI((JSlider)component);
    }
    
    @Override
    public Dimension getPreferredHorizontalSize() {
        return MotifSliderUI.PREFERRED_HORIZONTAL_SIZE;
    }
    
    @Override
    public Dimension getPreferredVerticalSize() {
        return MotifSliderUI.PREFERRED_VERTICAL_SIZE;
    }
    
    @Override
    public Dimension getMinimumHorizontalSize() {
        return MotifSliderUI.MINIMUM_HORIZONTAL_SIZE;
    }
    
    @Override
    public Dimension getMinimumVerticalSize() {
        return MotifSliderUI.MINIMUM_VERTICAL_SIZE;
    }
    
    @Override
    protected Dimension getThumbSize() {
        if (this.slider.getOrientation() == 0) {
            return new Dimension(30, 15);
        }
        return new Dimension(15, 30);
    }
    
    @Override
    public void paintFocus(final Graphics graphics) {
    }
    
    @Override
    public void paintTrack(final Graphics graphics) {
    }
    
    @Override
    public void paintThumb(final Graphics graphics) {
        final Rectangle thumbRect = this.thumbRect;
        final int x = thumbRect.x;
        final int y = thumbRect.y;
        final int width = thumbRect.width;
        final int height = thumbRect.height;
        if (this.slider.isEnabled()) {
            graphics.setColor(this.slider.getForeground());
        }
        else {
            graphics.setColor(this.slider.getForeground().darker());
        }
        if (this.slider.getOrientation() == 0) {
            graphics.translate(x, thumbRect.y - 1);
            graphics.fillRect(0, 1, width, height - 1);
            graphics.setColor(this.getHighlightColor());
            SwingUtilities2.drawHLine(graphics, 0, width - 1, 1);
            SwingUtilities2.drawVLine(graphics, 0, 1, height);
            SwingUtilities2.drawVLine(graphics, width / 2, 2, height - 1);
            graphics.setColor(this.getShadowColor());
            SwingUtilities2.drawHLine(graphics, 0, width - 1, height);
            SwingUtilities2.drawVLine(graphics, width - 1, 1, height);
            SwingUtilities2.drawVLine(graphics, width / 2 - 1, 2, height);
            graphics.translate(-x, -(thumbRect.y - 1));
        }
        else {
            graphics.translate(thumbRect.x - 1, 0);
            graphics.fillRect(1, y, width - 1, height);
            graphics.setColor(this.getHighlightColor());
            SwingUtilities2.drawHLine(graphics, 1, width, y);
            SwingUtilities2.drawVLine(graphics, 1, y + 1, y + height - 1);
            SwingUtilities2.drawHLine(graphics, 2, width - 1, y + height / 2);
            graphics.setColor(this.getShadowColor());
            SwingUtilities2.drawHLine(graphics, 2, width, y + height - 1);
            SwingUtilities2.drawVLine(graphics, width, y + height - 1, y);
            SwingUtilities2.drawHLine(graphics, 2, width - 1, y + height / 2 - 1);
            graphics.translate(-(thumbRect.x - 1), 0);
        }
    }
    
    static {
        PREFERRED_HORIZONTAL_SIZE = new Dimension(164, 15);
        PREFERRED_VERTICAL_SIZE = new Dimension(15, 164);
        MINIMUM_HORIZONTAL_SIZE = new Dimension(43, 15);
        MINIMUM_VERTICAL_SIZE = new Dimension(15, 43);
    }
}
