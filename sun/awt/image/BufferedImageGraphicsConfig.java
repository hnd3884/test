package sun.awt.image;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.DirectColorModel;
import java.util.Hashtable;
import java.awt.Graphics2D;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.ColorModel;
import java.awt.GraphicsDevice;
import java.awt.GraphicsConfiguration;

public class BufferedImageGraphicsConfig extends GraphicsConfiguration
{
    private static final int numconfigs = 12;
    private static BufferedImageGraphicsConfig[] configs;
    GraphicsDevice gd;
    ColorModel model;
    Raster raster;
    int width;
    int height;
    
    public static BufferedImageGraphicsConfig getConfig(final BufferedImage bufferedImage) {
        final int type = bufferedImage.getType();
        if (type > 0 && type < 12) {
            final BufferedImageGraphicsConfig bufferedImageGraphicsConfig = BufferedImageGraphicsConfig.configs[type];
            if (bufferedImageGraphicsConfig != null) {
                return bufferedImageGraphicsConfig;
            }
        }
        final BufferedImageGraphicsConfig bufferedImageGraphicsConfig2 = new BufferedImageGraphicsConfig(bufferedImage, null);
        if (type > 0 && type < 12) {
            BufferedImageGraphicsConfig.configs[type] = bufferedImageGraphicsConfig2;
        }
        return bufferedImageGraphicsConfig2;
    }
    
    public BufferedImageGraphicsConfig(final BufferedImage bufferedImage, final Component component) {
        if (component == null) {
            this.gd = new BufferedImageDevice(this);
        }
        else {
            this.gd = ((Graphics2D)component.getGraphics()).getDeviceConfiguration().getDevice();
        }
        this.model = bufferedImage.getColorModel();
        this.raster = bufferedImage.getRaster().createCompatibleWritableRaster(1, 1);
        this.width = bufferedImage.getWidth();
        this.height = bufferedImage.getHeight();
    }
    
    @Override
    public GraphicsDevice getDevice() {
        return this.gd;
    }
    
    @Override
    public BufferedImage createCompatibleImage(final int n, final int n2) {
        return new BufferedImage(this.model, this.raster.createCompatibleWritableRaster(n, n2), this.model.isAlphaPremultiplied(), null);
    }
    
    @Override
    public ColorModel getColorModel() {
        return this.model;
    }
    
    @Override
    public ColorModel getColorModel(final int n) {
        if (this.model.getTransparency() == n) {
            return this.model;
        }
        switch (n) {
            case 1: {
                return new DirectColorModel(24, 16711680, 65280, 255);
            }
            case 2: {
                return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
            }
            case 3: {
                return ColorModel.getRGBdefault();
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public AffineTransform getDefaultTransform() {
        return new AffineTransform();
    }
    
    @Override
    public AffineTransform getNormalizingTransform() {
        return new AffineTransform();
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(0, 0, this.width, this.height);
    }
    
    static {
        BufferedImageGraphicsConfig.configs = new BufferedImageGraphicsConfig[12];
    }
}
