package java.awt.image;

import java.awt.ImageCapabilities;
import java.awt.GraphicsConfiguration;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.Image;

public abstract class VolatileImage extends Image implements Transparency
{
    public static final int IMAGE_OK = 0;
    public static final int IMAGE_RESTORED = 1;
    public static final int IMAGE_INCOMPATIBLE = 2;
    protected int transparency;
    
    public VolatileImage() {
        this.transparency = 3;
    }
    
    public abstract BufferedImage getSnapshot();
    
    public abstract int getWidth();
    
    public abstract int getHeight();
    
    @Override
    public ImageProducer getSource() {
        return this.getSnapshot().getSource();
    }
    
    @Override
    public Graphics getGraphics() {
        return this.createGraphics();
    }
    
    public abstract Graphics2D createGraphics();
    
    public abstract int validate(final GraphicsConfiguration p0);
    
    public abstract boolean contentsLost();
    
    public abstract ImageCapabilities getCapabilities();
    
    @Override
    public int getTransparency() {
        return this.transparency;
    }
}
