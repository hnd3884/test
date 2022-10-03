package sun.print;

import java.awt.Rectangle;
import java.awt.image.DirectColorModel;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.GraphicsDevice;
import java.awt.image.ColorModel;
import java.awt.GraphicsConfiguration;

public class PrinterGraphicsConfig extends GraphicsConfiguration
{
    static ColorModel theModel;
    GraphicsDevice gd;
    int pageWidth;
    int pageHeight;
    AffineTransform deviceTransform;
    
    public PrinterGraphicsConfig(final String s, final AffineTransform deviceTransform, final int pageWidth, final int pageHeight) {
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.deviceTransform = deviceTransform;
        this.gd = new PrinterGraphicsDevice(this, s);
    }
    
    @Override
    public GraphicsDevice getDevice() {
        return this.gd;
    }
    
    @Override
    public ColorModel getColorModel() {
        if (PrinterGraphicsConfig.theModel == null) {
            PrinterGraphicsConfig.theModel = new BufferedImage(1, 1, 5).getColorModel();
        }
        return PrinterGraphicsConfig.theModel;
    }
    
    @Override
    public ColorModel getColorModel(final int n) {
        switch (n) {
            case 1: {
                return this.getColorModel();
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
        return new AffineTransform(this.deviceTransform);
    }
    
    @Override
    public AffineTransform getNormalizingTransform() {
        return new AffineTransform();
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle(0, 0, this.pageWidth, this.pageHeight);
    }
}
