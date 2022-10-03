package java.awt.image;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

public class MemoryImageSource implements ImageProducer
{
    int width;
    int height;
    ColorModel model;
    Object pixels;
    int pixeloffset;
    int pixelscan;
    Hashtable properties;
    Vector theConsumers;
    boolean animating;
    boolean fullbuffers;
    
    public MemoryImageSource(final int n, final int n2, final ColorModel colorModel, final byte[] array, final int n3, final int n4) {
        this.theConsumers = new Vector();
        this.initialize(n, n2, colorModel, array, n3, n4, null);
    }
    
    public MemoryImageSource(final int n, final int n2, final ColorModel colorModel, final byte[] array, final int n3, final int n4, final Hashtable<?, ?> hashtable) {
        this.theConsumers = new Vector();
        this.initialize(n, n2, colorModel, array, n3, n4, hashtable);
    }
    
    public MemoryImageSource(final int n, final int n2, final ColorModel colorModel, final int[] array, final int n3, final int n4) {
        this.theConsumers = new Vector();
        this.initialize(n, n2, colorModel, array, n3, n4, null);
    }
    
    public MemoryImageSource(final int n, final int n2, final ColorModel colorModel, final int[] array, final int n3, final int n4, final Hashtable<?, ?> hashtable) {
        this.theConsumers = new Vector();
        this.initialize(n, n2, colorModel, array, n3, n4, hashtable);
    }
    
    private void initialize(final int width, final int height, final ColorModel model, final Object pixels, final int pixeloffset, final int pixelscan, Hashtable properties) {
        this.width = width;
        this.height = height;
        this.model = model;
        this.pixels = pixels;
        this.pixeloffset = pixeloffset;
        this.pixelscan = pixelscan;
        if (properties == null) {
            properties = new Hashtable();
        }
        this.properties = properties;
    }
    
    public MemoryImageSource(final int n, final int n2, final int[] array, final int n3, final int n4) {
        this.theConsumers = new Vector();
        this.initialize(n, n2, ColorModel.getRGBdefault(), array, n3, n4, null);
    }
    
    public MemoryImageSource(final int n, final int n2, final int[] array, final int n3, final int n4, final Hashtable<?, ?> hashtable) {
        this.theConsumers = new Vector();
        this.initialize(n, n2, ColorModel.getRGBdefault(), array, n3, n4, hashtable);
    }
    
    @Override
    public synchronized void addConsumer(final ImageConsumer imageConsumer) {
        if (this.theConsumers.contains(imageConsumer)) {
            return;
        }
        this.theConsumers.addElement(imageConsumer);
        try {
            this.initConsumer(imageConsumer);
            this.sendPixels(imageConsumer, 0, 0, this.width, this.height);
            if (this.isConsumer(imageConsumer)) {
                imageConsumer.imageComplete(this.animating ? 2 : 3);
                if (!this.animating && this.isConsumer(imageConsumer)) {
                    imageConsumer.imageComplete(1);
                    this.removeConsumer(imageConsumer);
                }
            }
        }
        catch (final Exception ex) {
            if (this.isConsumer(imageConsumer)) {
                imageConsumer.imageComplete(1);
            }
        }
    }
    
    @Override
    public synchronized boolean isConsumer(final ImageConsumer imageConsumer) {
        return this.theConsumers.contains(imageConsumer);
    }
    
    @Override
    public synchronized void removeConsumer(final ImageConsumer imageConsumer) {
        this.theConsumers.removeElement(imageConsumer);
    }
    
    @Override
    public void startProduction(final ImageConsumer imageConsumer) {
        this.addConsumer(imageConsumer);
    }
    
    @Override
    public void requestTopDownLeftRightResend(final ImageConsumer imageConsumer) {
    }
    
    public synchronized void setAnimated(final boolean animating) {
        if (!(this.animating = animating)) {
            final Enumeration elements = this.theConsumers.elements();
            while (elements.hasMoreElements()) {
                final ImageConsumer imageConsumer = (ImageConsumer)elements.nextElement();
                imageConsumer.imageComplete(3);
                if (this.isConsumer(imageConsumer)) {
                    imageConsumer.imageComplete(1);
                }
            }
            this.theConsumers.removeAllElements();
        }
    }
    
    public synchronized void setFullBufferUpdates(final boolean fullbuffers) {
        if (this.fullbuffers == fullbuffers) {
            return;
        }
        this.fullbuffers = fullbuffers;
        if (this.animating) {
            final Enumeration elements = this.theConsumers.elements();
            while (elements.hasMoreElements()) {
                ((ImageConsumer)elements.nextElement()).setHints(fullbuffers ? 6 : 1);
            }
        }
    }
    
    public void newPixels() {
        this.newPixels(0, 0, this.width, this.height, true);
    }
    
    public synchronized void newPixels(final int n, final int n2, final int n3, final int n4) {
        this.newPixels(n, n2, n3, n4, true);
    }
    
    public synchronized void newPixels(int n, int n2, int width, int height, final boolean b) {
        if (this.animating) {
            if (this.fullbuffers) {
                n2 = (n = 0);
                width = this.width;
                height = this.height;
            }
            else {
                if (n < 0) {
                    width += n;
                    n = 0;
                }
                if (n + width > this.width) {
                    width = this.width - n;
                }
                if (n2 < 0) {
                    height += n2;
                    n2 = 0;
                }
                if (n2 + height > this.height) {
                    height = this.height - n2;
                }
            }
            if ((width <= 0 || height <= 0) && !b) {
                return;
            }
            final Enumeration elements = this.theConsumers.elements();
            while (elements.hasMoreElements()) {
                final ImageConsumer imageConsumer = (ImageConsumer)elements.nextElement();
                if (width > 0 && height > 0) {
                    this.sendPixels(imageConsumer, n, n2, width, height);
                }
                if (b && this.isConsumer(imageConsumer)) {
                    imageConsumer.imageComplete(2);
                }
            }
        }
    }
    
    public synchronized void newPixels(final byte[] pixels, final ColorModel model, final int pixeloffset, final int pixelscan) {
        this.pixels = pixels;
        this.model = model;
        this.pixeloffset = pixeloffset;
        this.pixelscan = pixelscan;
        this.newPixels();
    }
    
    public synchronized void newPixels(final int[] pixels, final ColorModel model, final int pixeloffset, final int pixelscan) {
        this.pixels = pixels;
        this.model = model;
        this.pixeloffset = pixeloffset;
        this.pixelscan = pixelscan;
        this.newPixels();
    }
    
    private void initConsumer(final ImageConsumer imageConsumer) {
        if (this.isConsumer(imageConsumer)) {
            imageConsumer.setDimensions(this.width, this.height);
        }
        if (this.isConsumer(imageConsumer)) {
            imageConsumer.setProperties(this.properties);
        }
        if (this.isConsumer(imageConsumer)) {
            imageConsumer.setColorModel(this.model);
        }
        if (this.isConsumer(imageConsumer)) {
            imageConsumer.setHints(this.animating ? (this.fullbuffers ? 6 : 1) : 30);
        }
    }
    
    private void sendPixels(final ImageConsumer imageConsumer, final int n, final int n2, final int n3, final int n4) {
        final int n5 = this.pixeloffset + this.pixelscan * n2 + n;
        if (this.isConsumer(imageConsumer)) {
            if (this.pixels instanceof byte[]) {
                imageConsumer.setPixels(n, n2, n3, n4, this.model, (byte[])this.pixels, n5, this.pixelscan);
            }
            else {
                imageConsumer.setPixels(n, n2, n3, n4, this.model, (int[])this.pixels, n5, this.pixelscan);
            }
        }
    }
}
