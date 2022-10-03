package sun.awt.image;

import sun.java2d.SurfaceDataProxy;
import java.awt.GraphicsEnvironment;
import java.awt.image.VolatileImage;
import java.util.Iterator;
import java.awt.ImageCapabilities;
import java.awt.GraphicsConfiguration;
import sun.java2d.SurfaceData;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SurfaceManager
{
    private static ImageAccessor imgaccessor;
    private ConcurrentHashMap<Object, Object> cacheMap;
    
    public static void setImageAccessor(final ImageAccessor imgaccessor) {
        if (SurfaceManager.imgaccessor != null) {
            throw new InternalError("Attempt to set ImageAccessor twice");
        }
        SurfaceManager.imgaccessor = imgaccessor;
    }
    
    public static SurfaceManager getManager(final Image image) {
        SurfaceManager surfaceManager = SurfaceManager.imgaccessor.getSurfaceManager(image);
        if (surfaceManager == null) {
            try {
                final BufferedImage bufferedImage = (BufferedImage)image;
                surfaceManager = new BufImgSurfaceManager(bufferedImage);
                setManager(bufferedImage, surfaceManager);
            }
            catch (final ClassCastException ex) {
                throw new IllegalArgumentException("Invalid Image variant");
            }
        }
        return surfaceManager;
    }
    
    public static void setManager(final Image image, final SurfaceManager surfaceManager) {
        SurfaceManager.imgaccessor.setSurfaceManager(image, surfaceManager);
    }
    
    public Object getCacheData(final Object o) {
        return (this.cacheMap == null) ? null : this.cacheMap.get(o);
    }
    
    public void setCacheData(final Object o, final Object o2) {
        if (this.cacheMap == null) {
            synchronized (this) {
                if (this.cacheMap == null) {
                    this.cacheMap = new ConcurrentHashMap<Object, Object>(2);
                }
            }
        }
        this.cacheMap.put(o, o2);
    }
    
    public abstract SurfaceData getPrimarySurfaceData();
    
    public abstract SurfaceData restoreContents();
    
    public void acceleratedSurfaceLost() {
    }
    
    public ImageCapabilities getCapabilities(final GraphicsConfiguration graphicsConfiguration) {
        return new ImageCapabilitiesGc(graphicsConfiguration);
    }
    
    public synchronized void flush() {
        this.flush(false);
    }
    
    synchronized void flush(final boolean b) {
        if (this.cacheMap != null) {
            final Iterator<Object> iterator = (Iterator<Object>)this.cacheMap.values().iterator();
            while (iterator.hasNext()) {
                final FlushableCacheData next = iterator.next();
                if (next instanceof FlushableCacheData && next.flush(b)) {
                    iterator.remove();
                }
            }
        }
    }
    
    public void setAccelerationPriority(final float n) {
        if (n == 0.0f) {
            this.flush(true);
        }
    }
    
    public static int getImageScale(final Image image) {
        if (!(image instanceof VolatileImage)) {
            return 1;
        }
        return getManager(image).getPrimarySurfaceData().getDefaultScale();
    }
    
    public abstract static class ImageAccessor
    {
        public abstract SurfaceManager getSurfaceManager(final Image p0);
        
        public abstract void setSurfaceManager(final Image p0, final SurfaceManager p1);
    }
    
    class ImageCapabilitiesGc extends ImageCapabilities
    {
        GraphicsConfiguration gc;
        
        public ImageCapabilitiesGc(final GraphicsConfiguration gc) {
            super(false);
            this.gc = gc;
        }
        
        @Override
        public boolean isAccelerated() {
            GraphicsConfiguration graphicsConfiguration = this.gc;
            if (graphicsConfiguration == null) {
                graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
            }
            if (graphicsConfiguration instanceof ProxiedGraphicsConfig) {
                final Object proxyKey = ((ProxiedGraphicsConfig)graphicsConfiguration).getProxyKey();
                if (proxyKey != null) {
                    final SurfaceDataProxy surfaceDataProxy = (SurfaceDataProxy)SurfaceManager.this.getCacheData(proxyKey);
                    return surfaceDataProxy != null && surfaceDataProxy.isAccelerated();
                }
            }
            return false;
        }
    }
    
    public interface FlushableCacheData
    {
        boolean flush(final boolean p0);
    }
    
    public interface ProxiedGraphicsConfig
    {
        Object getProxyKey();
    }
}
