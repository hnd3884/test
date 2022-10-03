package java.awt;

import java.awt.geom.AffineTransform;
import sun.awt.image.SunVolatileImage;
import java.awt.image.VolatileImage;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import java.awt.image.BufferedImage;

public abstract class GraphicsConfiguration
{
    private static BufferCapabilities defaultBufferCaps;
    private static ImageCapabilities defaultImageCaps;
    
    protected GraphicsConfiguration() {
    }
    
    public abstract GraphicsDevice getDevice();
    
    public BufferedImage createCompatibleImage(final int n, final int n2) {
        final ColorModel colorModel = this.getColorModel();
        return new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(n, n2), colorModel.isAlphaPremultiplied(), null);
    }
    
    public BufferedImage createCompatibleImage(final int n, final int n2, final int n3) {
        if (this.getColorModel().getTransparency() == n3) {
            return this.createCompatibleImage(n, n2);
        }
        final ColorModel colorModel = this.getColorModel(n3);
        if (colorModel == null) {
            throw new IllegalArgumentException("Unknown transparency: " + n3);
        }
        return new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(n, n2), colorModel.isAlphaPremultiplied(), null);
    }
    
    public VolatileImage createCompatibleVolatileImage(final int n, final int n2) {
        VolatileImage compatibleVolatileImage = null;
        try {
            compatibleVolatileImage = this.createCompatibleVolatileImage(n, n2, null, 1);
        }
        catch (final AWTException ex) {
            assert false;
        }
        return compatibleVolatileImage;
    }
    
    public VolatileImage createCompatibleVolatileImage(final int n, final int n2, final int n3) {
        VolatileImage compatibleVolatileImage = null;
        try {
            compatibleVolatileImage = this.createCompatibleVolatileImage(n, n2, null, n3);
        }
        catch (final AWTException ex) {
            assert false;
        }
        return compatibleVolatileImage;
    }
    
    public VolatileImage createCompatibleVolatileImage(final int n, final int n2, final ImageCapabilities imageCapabilities) throws AWTException {
        return this.createCompatibleVolatileImage(n, n2, imageCapabilities, 1);
    }
    
    public VolatileImage createCompatibleVolatileImage(final int n, final int n2, final ImageCapabilities imageCapabilities, final int n3) throws AWTException {
        final SunVolatileImage sunVolatileImage = new SunVolatileImage(this, n, n2, n3, imageCapabilities);
        if (imageCapabilities != null && imageCapabilities.isAccelerated() && !sunVolatileImage.getCapabilities().isAccelerated()) {
            throw new AWTException("Supplied image capabilities could not be met by this graphics configuration.");
        }
        return sunVolatileImage;
    }
    
    public abstract ColorModel getColorModel();
    
    public abstract ColorModel getColorModel(final int p0);
    
    public abstract AffineTransform getDefaultTransform();
    
    public abstract AffineTransform getNormalizingTransform();
    
    public abstract Rectangle getBounds();
    
    public BufferCapabilities getBufferCapabilities() {
        if (GraphicsConfiguration.defaultBufferCaps == null) {
            GraphicsConfiguration.defaultBufferCaps = new DefaultBufferCapabilities(this.getImageCapabilities());
        }
        return GraphicsConfiguration.defaultBufferCaps;
    }
    
    public ImageCapabilities getImageCapabilities() {
        if (GraphicsConfiguration.defaultImageCaps == null) {
            GraphicsConfiguration.defaultImageCaps = new ImageCapabilities(false);
        }
        return GraphicsConfiguration.defaultImageCaps;
    }
    
    public boolean isTranslucencyCapable() {
        return false;
    }
    
    private static class DefaultBufferCapabilities extends BufferCapabilities
    {
        public DefaultBufferCapabilities(final ImageCapabilities imageCapabilities) {
            super(imageCapabilities, imageCapabilities, null);
        }
    }
}
