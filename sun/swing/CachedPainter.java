package sun.swing;

import java.util.HashMap;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.GraphicsConfiguration;
import java.awt.image.VolatileImage;
import java.awt.Graphics;
import java.awt.Component;
import java.util.Map;

public abstract class CachedPainter
{
    private static final Map<Object, ImageCache> cacheMap;
    
    private static ImageCache getCache(final Object o) {
        synchronized (CachedPainter.class) {
            ImageCache imageCache = CachedPainter.cacheMap.get(o);
            if (imageCache == null) {
                imageCache = new ImageCache(1);
                CachedPainter.cacheMap.put(o, imageCache);
            }
            return imageCache;
        }
    }
    
    public CachedPainter(final int maxCount) {
        getCache(this.getClass()).setMaxCount(maxCount);
    }
    
    public void paint(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final Object... array) {
        if (n3 <= 0 || n4 <= 0) {
            return;
        }
        synchronized (CachedPainter.class) {
            this.paint0(component, graphics, n, n2, n3, n4, array);
        }
    }
    
    private void paint0(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final Object... array) {
        final Class<? extends CachedPainter> class1 = this.getClass();
        final GraphicsConfiguration graphicsConfiguration = this.getGraphicsConfiguration(component);
        final ImageCache cache = getCache(class1);
        Image image = cache.getImage(class1, graphicsConfiguration, n3, n4, array);
        int n5 = 0;
        do {
            boolean b = false;
            if (image instanceof VolatileImage) {
                switch (((VolatileImage)image).validate(graphicsConfiguration)) {
                    case 2: {
                        ((VolatileImage)image).flush();
                        image = null;
                        break;
                    }
                    case 1: {
                        b = true;
                        break;
                    }
                }
            }
            if (image == null) {
                image = this.createImage(component, n3, n4, graphicsConfiguration, array);
                cache.setImage(class1, graphicsConfiguration, n3, n4, array, image);
                b = true;
            }
            if (b) {
                final Graphics graphics2 = image.getGraphics();
                this.paintToImage(component, image, graphics2, n3, n4, array);
                graphics2.dispose();
            }
            this.paintImage(component, graphics, n, n2, n3, n4, image, array);
        } while (image instanceof VolatileImage && ((VolatileImage)image).contentsLost() && ++n5 < 3);
    }
    
    protected abstract void paintToImage(final Component p0, final Image p1, final Graphics p2, final int p3, final int p4, final Object[] p5);
    
    protected void paintImage(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final Image image, final Object[] array) {
        graphics.drawImage(image, n, n2, null);
    }
    
    protected Image createImage(final Component component, final int n, final int n2, final GraphicsConfiguration graphicsConfiguration, final Object[] array) {
        if (graphicsConfiguration == null) {
            return new BufferedImage(n, n2, 1);
        }
        return graphicsConfiguration.createCompatibleVolatileImage(n, n2);
    }
    
    protected void flush() {
        synchronized (CachedPainter.class) {
            getCache(this.getClass()).flush();
        }
    }
    
    private GraphicsConfiguration getGraphicsConfiguration(final Component component) {
        if (component == null) {
            return null;
        }
        return component.getGraphicsConfiguration();
    }
    
    static {
        cacheMap = new HashMap<Object, ImageCache>();
    }
}
