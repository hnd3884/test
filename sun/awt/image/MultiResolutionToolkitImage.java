package sun.awt.image;

import sun.misc.SoftCache;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import java.util.List;
import java.awt.Image;

public class MultiResolutionToolkitImage extends ToolkitImage implements MultiResolutionImage
{
    Image resolutionVariant;
    private static final int BITS_INFO = 56;
    
    public MultiResolutionToolkitImage(final Image image, final Image resolutionVariant) {
        super(image.getSource());
        this.resolutionVariant = resolutionVariant;
    }
    
    @Override
    public Image getResolutionVariant(final int n, final int n2) {
        return (n <= this.getWidth() && n2 <= this.getHeight()) ? this : this.resolutionVariant;
    }
    
    public Image getResolutionVariant() {
        return this.resolutionVariant;
    }
    
    @Override
    public List<Image> getResolutionVariants() {
        return Arrays.asList(this, this.resolutionVariant);
    }
    
    public static ImageObserver getResolutionVariantObserver(final Image image, final ImageObserver imageObserver, final int n, final int n2, final int n3, final int n4) {
        return getResolutionVariantObserver(image, imageObserver, n, n2, n3, n4, false);
    }
    
    public static ImageObserver getResolutionVariantObserver(final Image image, final ImageObserver imageObserver, final int n, final int n2, final int n3, final int n4, final boolean b) {
        if (imageObserver == null) {
            return null;
        }
        synchronized (ObserverCache.INSTANCE) {
            ImageObserver imageObserver2 = (ImageObserver)ObserverCache.INSTANCE.get(imageObserver);
            if (imageObserver2 == null) {
                imageObserver2 = ((p3, n8, n10, n12, n13, n14) -> {
                    if ((n8 & 0x39) != 0x0) {
                        n13 = (n13 + 1) / 2;
                    }
                    if ((n8 & 0x3A) != 0x0) {
                        n14 = (n14 + 1) / 2;
                    }
                    if ((n8 & 0x38) != 0x0) {
                        n10 /= 2;
                        n12 /= 2;
                    }
                    if (b2) {
                        n8 &= ((ToolkitImage)image3).getImageRep().check(null);
                    }
                    return imageObserver3.imageUpdate(image3, n8, n10, n12, n13, n14);
                });
                ObserverCache.INSTANCE.put(imageObserver, imageObserver2);
            }
            return imageObserver2;
        }
    }
    
    private static class ObserverCache
    {
        static final SoftCache INSTANCE;
        
        static {
            INSTANCE = new SoftCache();
        }
    }
}
