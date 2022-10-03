package sun.awt.image;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import sun.awt.AppContext;
import java.awt.Image;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.LinkedHashMap;

public final class ImageCache
{
    private final LinkedHashMap<PixelsKey, ImageSoftReference> map;
    private final int maxPixelCount;
    private int currentPixelCount;
    private final ReadWriteLock lock;
    private final ReferenceQueue<Image> referenceQueue;
    
    public static ImageCache getInstance() {
        return AppContext.getSoftReferenceValue(ImageCache.class, () -> new ImageCache());
    }
    
    ImageCache(final int maxPixelCount) {
        this.map = new LinkedHashMap<PixelsKey, ImageSoftReference>(16, 0.75f, true);
        this.currentPixelCount = 0;
        this.lock = new ReentrantReadWriteLock();
        this.referenceQueue = new ReferenceQueue<Image>();
        this.maxPixelCount = maxPixelCount;
    }
    
    ImageCache() {
        this(2097152);
    }
    
    public void flush() {
        this.lock.writeLock().lock();
        try {
            this.map.clear();
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }
    
    public Image getImage(final PixelsKey pixelsKey) {
        this.lock.readLock().lock();
        ImageSoftReference imageSoftReference;
        try {
            imageSoftReference = this.map.get(pixelsKey);
        }
        finally {
            this.lock.readLock().unlock();
        }
        return (imageSoftReference == null) ? null : imageSoftReference.get();
    }
    
    public void setImage(final PixelsKey pixelsKey, final Image image) {
        this.lock.writeLock().lock();
        try {
            final ImageSoftReference imageSoftReference = this.map.get(pixelsKey);
            if (imageSoftReference != null) {
                if (imageSoftReference.get() != null) {
                    return;
                }
                this.currentPixelCount -= pixelsKey.getPixelCount();
                this.map.remove(pixelsKey);
            }
            this.currentPixelCount += pixelsKey.getPixelCount();
            if (this.currentPixelCount > this.maxPixelCount) {
                ImageSoftReference imageSoftReference2;
                while ((imageSoftReference2 = (ImageSoftReference)this.referenceQueue.poll()) != null) {
                    this.map.remove(imageSoftReference2.key);
                    this.currentPixelCount -= imageSoftReference2.key.getPixelCount();
                }
            }
            if (this.currentPixelCount > this.maxPixelCount) {
                final Iterator<Map.Entry<PixelsKey, ImageSoftReference>> iterator = this.map.entrySet().iterator();
                while (this.currentPixelCount > this.maxPixelCount && iterator.hasNext()) {
                    final Map.Entry entry = iterator.next();
                    iterator.remove();
                    final Image image2 = ((ImageSoftReference)entry.getValue()).get();
                    if (image2 != null) {
                        image2.flush();
                    }
                    this.currentPixelCount -= ((ImageSoftReference)entry.getValue()).key.getPixelCount();
                }
            }
            this.map.put(pixelsKey, new ImageSoftReference(pixelsKey, image, this.referenceQueue));
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }
    
    private static class ImageSoftReference extends SoftReference<Image>
    {
        final PixelsKey key;
        
        ImageSoftReference(final PixelsKey key, final Image image, final ReferenceQueue<? super Image> referenceQueue) {
            super(image, referenceQueue);
            this.key = key;
        }
    }
    
    public interface PixelsKey
    {
        int getPixelCount();
    }
}
