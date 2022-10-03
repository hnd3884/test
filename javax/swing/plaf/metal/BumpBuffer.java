package javax.swing.plaf.metal;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Color;
import java.awt.Image;

class BumpBuffer
{
    static final int IMAGE_SIZE = 64;
    transient Image image;
    Color topColor;
    Color shadowColor;
    Color backColor;
    private GraphicsConfiguration gc;
    
    public BumpBuffer(final GraphicsConfiguration gc, final Color topColor, final Color shadowColor, final Color backColor) {
        this.gc = gc;
        this.topColor = topColor;
        this.shadowColor = shadowColor;
        this.backColor = backColor;
        this.createImage();
        this.fillBumpBuffer();
    }
    
    public boolean hasSameConfiguration(final GraphicsConfiguration graphicsConfiguration, final Color color, final Color color2, final Color color3) {
        if (this.gc != null) {
            if (!this.gc.equals(graphicsConfiguration)) {
                return false;
            }
        }
        else if (graphicsConfiguration != null) {
            return false;
        }
        return this.topColor.equals(color) && this.shadowColor.equals(color2) && this.backColor.equals(color3);
    }
    
    public Image getImage() {
        return this.image;
    }
    
    private void fillBumpBuffer() {
        final Graphics graphics = this.image.getGraphics();
        graphics.setColor(this.backColor);
        graphics.fillRect(0, 0, 64, 64);
        graphics.setColor(this.topColor);
        for (int i = 0; i < 64; i += 4) {
            for (int j = 0; j < 64; j += 4) {
                graphics.drawLine(i, j, i, j);
                graphics.drawLine(i + 2, j + 2, i + 2, j + 2);
            }
        }
        graphics.setColor(this.shadowColor);
        for (int k = 0; k < 64; k += 4) {
            for (int l = 0; l < 64; l += 4) {
                graphics.drawLine(k + 1, l + 1, k + 1, l + 1);
                graphics.drawLine(k + 3, l + 3, k + 3, l + 3);
            }
        }
        graphics.dispose();
    }
    
    private void createImage() {
        if (this.gc != null) {
            this.image = this.gc.createCompatibleImage(64, 64, (this.backColor != MetalBumps.ALPHA) ? 1 : 2);
        }
        else {
            this.image = new BufferedImage(64, 64, 13, new IndexColorModel(8, 3, new int[] { this.backColor.getRGB(), this.topColor.getRGB(), this.shadowColor.getRGB() }, 0, false, (this.backColor == MetalBumps.ALPHA) ? 0 : -1, 0));
        }
    }
}
