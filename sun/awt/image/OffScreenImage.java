package sun.awt.image;

import java.awt.image.ImageProducer;
import java.awt.Color;
import sun.java2d.SunGraphics2D;
import java.awt.Image;
import sun.java2d.SurfaceData;
import java.awt.SystemColor;
import java.awt.GraphicsEnvironment;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.util.Hashtable;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.Font;
import java.awt.Component;
import java.awt.image.BufferedImage;

public class OffScreenImage extends BufferedImage
{
    protected Component c;
    private OffScreenImageSource osis;
    private Font defaultFont;
    
    public OffScreenImage(final Component c, final ColorModel colorModel, final WritableRaster writableRaster, final boolean b) {
        super(colorModel, writableRaster, b, null);
        this.c = c;
        this.initSurface(writableRaster.getWidth(), writableRaster.getHeight());
    }
    
    @Override
    public Graphics getGraphics() {
        return this.createGraphics();
    }
    
    @Override
    public Graphics2D createGraphics() {
        if (this.c == null) {
            return GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(this);
        }
        Color color = this.c.getBackground();
        if (color == null) {
            color = SystemColor.window;
        }
        Color color2 = this.c.getForeground();
        if (color2 == null) {
            color2 = SystemColor.windowText;
        }
        Font font = this.c.getFont();
        if (font == null) {
            if (this.defaultFont == null) {
                this.defaultFont = new Font("Dialog", 0, 12);
            }
            font = this.defaultFont;
        }
        return new SunGraphics2D(SurfaceData.getPrimarySurfaceData(this), color2, color, font);
    }
    
    private void initSurface(final int n, final int n2) {
        final Graphics2D graphics = this.createGraphics();
        try {
            graphics.clearRect(0, 0, n, n2);
        }
        finally {
            graphics.dispose();
        }
    }
    
    @Override
    public ImageProducer getSource() {
        if (this.osis == null) {
            this.osis = new OffScreenImageSource(this);
        }
        return this.osis;
    }
}
