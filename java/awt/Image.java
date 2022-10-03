package java.awt;

import java.awt.image.ImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ReplicateScaleFilter;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.ImageProducer;
import java.awt.image.ImageObserver;
import sun.awt.image.SurfaceManager;

public abstract class Image
{
    private static ImageCapabilities defaultImageCaps;
    protected float accelerationPriority;
    public static final Object UndefinedProperty;
    public static final int SCALE_DEFAULT = 1;
    public static final int SCALE_FAST = 2;
    public static final int SCALE_SMOOTH = 4;
    public static final int SCALE_REPLICATE = 8;
    public static final int SCALE_AREA_AVERAGING = 16;
    SurfaceManager surfaceManager;
    
    public Image() {
        this.accelerationPriority = 0.5f;
    }
    
    public abstract int getWidth(final ImageObserver p0);
    
    public abstract int getHeight(final ImageObserver p0);
    
    public abstract ImageProducer getSource();
    
    public abstract Graphics getGraphics();
    
    public abstract Object getProperty(final String p0, final ImageObserver p1);
    
    public Image getScaledInstance(final int n, final int n2, final int n3) {
        ReplicateScaleFilter replicateScaleFilter;
        if ((n3 & 0x14) != 0x0) {
            replicateScaleFilter = new AreaAveragingScaleFilter(n, n2);
        }
        else {
            replicateScaleFilter = new ReplicateScaleFilter(n, n2);
        }
        return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(this.getSource(), replicateScaleFilter));
    }
    
    public void flush() {
        if (this.surfaceManager != null) {
            this.surfaceManager.flush();
        }
    }
    
    public ImageCapabilities getCapabilities(final GraphicsConfiguration graphicsConfiguration) {
        if (this.surfaceManager != null) {
            return this.surfaceManager.getCapabilities(graphicsConfiguration);
        }
        return Image.defaultImageCaps;
    }
    
    public void setAccelerationPriority(final float accelerationPriority) {
        if (accelerationPriority < 0.0f || accelerationPriority > 1.0f) {
            throw new IllegalArgumentException("Priority must be a value between 0 and 1, inclusive");
        }
        this.accelerationPriority = accelerationPriority;
        if (this.surfaceManager != null) {
            this.surfaceManager.setAccelerationPriority(this.accelerationPriority);
        }
    }
    
    public float getAccelerationPriority() {
        return this.accelerationPriority;
    }
    
    static {
        Image.defaultImageCaps = new ImageCapabilities(false);
        UndefinedProperty = new Object();
        SurfaceManager.setImageAccessor(new SurfaceManager.ImageAccessor() {
            @Override
            public SurfaceManager getSurfaceManager(final Image image) {
                return image.surfaceManager;
            }
            
            @Override
            public void setSurfaceManager(final Image image, final SurfaceManager surfaceManager) {
                image.surfaceManager = surfaceManager;
            }
        });
    }
}
