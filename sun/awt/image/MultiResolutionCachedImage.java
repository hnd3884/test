package sun.awt.image;

import java.awt.image.ImageObserver;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Arrays;
import java.awt.Dimension;
import java.awt.Image;
import java.util.function.BiFunction;
import java.awt.geom.Dimension2D;

public class MultiResolutionCachedImage extends AbstractMultiResolutionImage
{
    private final int baseImageWidth;
    private final int baseImageHeight;
    private final Dimension2D[] sizes;
    private final BiFunction<Integer, Integer, Image> mapper;
    private int availableInfo;
    
    public MultiResolutionCachedImage(final int n, final int n2, final BiFunction<Integer, Integer, Image> biFunction) {
        this(n, n2, new Dimension[] { new Dimension(n, n2) }, biFunction);
    }
    
    public MultiResolutionCachedImage(final int baseImageWidth, final int baseImageHeight, final Dimension2D[] array, final BiFunction<Integer, Integer, Image> mapper) {
        this.baseImageWidth = baseImageWidth;
        this.baseImageHeight = baseImageHeight;
        this.sizes = (Dimension2D[])((array == null) ? null : ((Dimension2D[])Arrays.copyOf(array, array.length)));
        this.mapper = mapper;
    }
    
    @Override
    public Image getResolutionVariant(final int n, final int n2) {
        final ImageCache instance = ImageCache.getInstance();
        final ImageCacheKey imageCacheKey = new ImageCacheKey(this, n, n2);
        Image image = instance.getImage(imageCacheKey);
        if (image == null) {
            image = this.mapper.apply(n, n2);
            instance.setImage(imageCacheKey, image);
        }
        preload(image, this.availableInfo);
        return image;
    }
    
    @Override
    public List<Image> getResolutionVariants() {
        return Arrays.stream(this.sizes).map(dimension2D -> this.getResolutionVariant((int)dimension2D.getWidth(), (int)dimension2D.getHeight())).collect((Collector<? super Object, ?, List<Image>>)Collectors.toList());
    }
    
    public MultiResolutionCachedImage map(final Function<Image, Image> function) {
        return new MultiResolutionCachedImage(this.baseImageWidth, this.baseImageHeight, this.sizes, (n3, n4) -> function2.apply(this.getResolutionVariant(n3, n4)));
    }
    
    @Override
    public int getWidth(final ImageObserver imageObserver) {
        this.updateInfo(imageObserver, 1);
        return super.getWidth(imageObserver);
    }
    
    @Override
    public int getHeight(final ImageObserver imageObserver) {
        this.updateInfo(imageObserver, 2);
        return super.getHeight(imageObserver);
    }
    
    @Override
    public Object getProperty(final String s, final ImageObserver imageObserver) {
        this.updateInfo(imageObserver, 4);
        return super.getProperty(s, imageObserver);
    }
    
    @Override
    protected Image getBaseImage() {
        return this.getResolutionVariant(this.baseImageWidth, this.baseImageHeight);
    }
    
    private void updateInfo(final ImageObserver imageObserver, final int n) {
        this.availableInfo |= ((imageObserver == null) ? 32 : n);
    }
    
    private static int getInfo(final Image image) {
        if (image instanceof ToolkitImage) {
            return ((ToolkitImage)image).getImageRep().check((p0, p1, p2, p3, p4, p5) -> false);
        }
        return 0;
    }
    
    private static void preload(final Image image, final int n) {
        if (n != 0 && image instanceof ToolkitImage) {
            ((ToolkitImage)image).preload(new ImageObserver() {
                int flags = n;
                
                @Override
                public boolean imageUpdate(final Image image, final int n, final int n2, final int n3, final int n4, final int n5) {
                    this.flags &= ~n;
                    return this.flags != 0 && (n & 0xC0) == 0x0;
                }
            });
        }
    }
    
    private static class ImageCacheKey implements ImageCache.PixelsKey
    {
        private final int pixelCount;
        private final int hash;
        private final int w;
        private final int h;
        private final Image baseImage;
        
        ImageCacheKey(final Image baseImage, final int w, final int h) {
            this.baseImage = baseImage;
            this.w = w;
            this.h = h;
            this.pixelCount = w * h;
            this.hash = this.hash();
        }
        
        @Override
        public int getPixelCount() {
            return this.pixelCount;
        }
        
        private int hash() {
            return 31 * (31 * this.baseImage.hashCode() + this.w) + this.h;
        }
        
        @Override
        public int hashCode() {
            return this.hash;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o instanceof ImageCacheKey) {
                final ImageCacheKey imageCacheKey = (ImageCacheKey)o;
                return this.baseImage == imageCacheKey.baseImage && this.w == imageCacheKey.w && this.h == imageCacheKey.h;
            }
            return false;
        }
    }
}
