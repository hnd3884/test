package javax.swing.plaf.nimbus;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.awt.image.ImageObserver;
import java.awt.GraphicsConfiguration;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.awt.Image;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.LinkedHashMap;

class ImageCache
{
    private final LinkedHashMap<Integer, PixelCountSoftReference> map;
    private final int maxPixelCount;
    private final int maxSingleImagePixelSize;
    private int currentPixelCount;
    private ReadWriteLock lock;
    private ReferenceQueue<Image> referenceQueue;
    private static final ImageCache instance;
    
    static ImageCache getInstance() {
        return ImageCache.instance;
    }
    
    public ImageCache() {
        this.map = new LinkedHashMap<Integer, PixelCountSoftReference>(16, 0.75f, true);
        this.currentPixelCount = 0;
        this.lock = new ReentrantReadWriteLock();
        this.referenceQueue = new ReferenceQueue<Image>();
        this.maxPixelCount = 2097152;
        this.maxSingleImagePixelSize = 90000;
    }
    
    public ImageCache(final int maxPixelCount, final int maxSingleImagePixelSize) {
        this.map = new LinkedHashMap<Integer, PixelCountSoftReference>(16, 0.75f, true);
        this.currentPixelCount = 0;
        this.lock = new ReentrantReadWriteLock();
        this.referenceQueue = new ReferenceQueue<Image>();
        this.maxPixelCount = maxPixelCount;
        this.maxSingleImagePixelSize = maxSingleImagePixelSize;
    }
    
    public void flush() {
        this.lock.readLock().lock();
        try {
            this.map.clear();
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
    
    public boolean isImageCachable(final int n, final int n2) {
        return n * n2 < this.maxSingleImagePixelSize;
    }
    
    public Image getImage(final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final Object... array) {
        this.lock.readLock().lock();
        try {
            final PixelCountSoftReference pixelCountSoftReference = this.map.get(this.hash(graphicsConfiguration, n, n2, array));
            if (pixelCountSoftReference != null && pixelCountSoftReference.equals(graphicsConfiguration, n, n2, array)) {
                return pixelCountSoftReference.get();
            }
            return null;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
    
    public boolean setImage(final Image image, final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final Object... array) {
        if (!this.isImageCachable(n, n2)) {
            return false;
        }
        final int hash = this.hash(graphicsConfiguration, n, n2, array);
        this.lock.writeLock().lock();
        try {
            final PixelCountSoftReference pixelCountSoftReference = this.map.get(hash);
            if (pixelCountSoftReference != null && pixelCountSoftReference.get() == image) {
                return true;
            }
            if (pixelCountSoftReference != null) {
                this.currentPixelCount -= pixelCountSoftReference.pixelCount;
                this.map.remove(hash);
            }
            final int n3 = image.getWidth(null) * image.getHeight(null);
            this.currentPixelCount += n3;
            if (this.currentPixelCount > this.maxPixelCount) {
                PixelCountSoftReference pixelCountSoftReference2;
                while ((pixelCountSoftReference2 = (PixelCountSoftReference)this.referenceQueue.poll()) != null) {
                    this.map.remove(pixelCountSoftReference2.hash);
                    this.currentPixelCount -= pixelCountSoftReference2.pixelCount;
                }
            }
            if (this.currentPixelCount > this.maxPixelCount) {
                final Iterator<Map.Entry<Integer, PixelCountSoftReference>> iterator = this.map.entrySet().iterator();
                while (this.currentPixelCount > this.maxPixelCount && iterator.hasNext()) {
                    final Map.Entry entry = iterator.next();
                    iterator.remove();
                    final Image image2 = ((PixelCountSoftReference)entry.getValue()).get();
                    if (image2 != null) {
                        image2.flush();
                    }
                    this.currentPixelCount -= ((PixelCountSoftReference)entry.getValue()).pixelCount;
                }
            }
            this.map.put(hash, new PixelCountSoftReference(image, this.referenceQueue, n3, hash, graphicsConfiguration, n, n2, array));
            return true;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }
    
    private int hash(final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final Object... array) {
        return 31 * (31 * (31 * ((graphicsConfiguration != null) ? graphicsConfiguration.hashCode() : 0) + n) + n2) + Arrays.deepHashCode(array);
    }
    
    static {
        instance = new ImageCache();
    }
    
    private static class PixelCountSoftReference extends SoftReference<Image>
    {
        private final int pixelCount;
        private final int hash;
        private final GraphicsConfiguration config;
        private final int w;
        private final int h;
        private final Object[] args;
        
        public PixelCountSoftReference(final Image image, final ReferenceQueue<? super Image> referenceQueue, final int pixelCount, final int hash, final GraphicsConfiguration config, final int w, final int h, final Object[] args) {
            super(image, referenceQueue);
            this.pixelCount = pixelCount;
            this.hash = hash;
            this.config = config;
            this.w = w;
            this.h = h;
            this.args = args;
        }
        
        public boolean equals(final GraphicsConfiguration graphicsConfiguration, final int n, final int n2, final Object[] array) {
            return graphicsConfiguration == this.config && n == this.w && n2 == this.h && Arrays.equals(array, this.args);
        }
    }
}
