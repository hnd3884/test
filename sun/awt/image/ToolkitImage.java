package sun.awt.image;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.util.Hashtable;
import java.awt.image.ImageProducer;
import java.awt.Image;

public class ToolkitImage extends Image
{
    ImageProducer source;
    InputStreamImageSource src;
    ImageRepresentation imagerep;
    private int width;
    private int height;
    private Hashtable properties;
    private int availinfo;
    
    protected ToolkitImage() {
        this.width = -1;
        this.height = -1;
    }
    
    public ToolkitImage(final ImageProducer source) {
        this.width = -1;
        this.height = -1;
        this.source = source;
        if (source instanceof InputStreamImageSource) {
            this.src = (InputStreamImageSource)source;
        }
    }
    
    @Override
    public ImageProducer getSource() {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        return this.source;
    }
    
    public int getWidth() {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        if ((this.availinfo & 0x1) == 0x0) {
            this.reconstruct(1);
        }
        return this.width;
    }
    
    @Override
    public synchronized int getWidth(final ImageObserver imageObserver) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        if ((this.availinfo & 0x1) == 0x0) {
            this.addWatcher(imageObserver, true);
            if ((this.availinfo & 0x1) == 0x0) {
                return -1;
            }
        }
        return this.width;
    }
    
    public int getHeight() {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        if ((this.availinfo & 0x2) == 0x0) {
            this.reconstruct(2);
        }
        return this.height;
    }
    
    @Override
    public synchronized int getHeight(final ImageObserver imageObserver) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        if ((this.availinfo & 0x2) == 0x0) {
            this.addWatcher(imageObserver, true);
            if ((this.availinfo & 0x2) == 0x0) {
                return -1;
            }
        }
        return this.height;
    }
    
    @Override
    public Object getProperty(final String s, final ImageObserver imageObserver) {
        if (s == null) {
            throw new NullPointerException("null property name is not allowed");
        }
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        if (this.properties == null) {
            this.addWatcher(imageObserver, true);
            if (this.properties == null) {
                return null;
            }
        }
        Object o = this.properties.get(s);
        if (o == null) {
            o = Image.UndefinedProperty;
        }
        return o;
    }
    
    public boolean hasError() {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        return (this.availinfo & 0x40) != 0x0;
    }
    
    public int check(final ImageObserver imageObserver) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        if ((this.availinfo & 0x40) == 0x0 && (~this.availinfo & 0x7) != 0x0) {
            this.addWatcher(imageObserver, false);
        }
        return this.availinfo;
    }
    
    public void preload(final ImageObserver imageObserver) {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        if ((this.availinfo & 0x20) == 0x0) {
            this.addWatcher(imageObserver, true);
        }
    }
    
    private synchronized void addWatcher(final ImageObserver imageObserver, final boolean b) {
        if ((this.availinfo & 0x40) != 0x0) {
            if (imageObserver != null) {
                imageObserver.imageUpdate(this, 192, -1, -1, -1, -1);
            }
            return;
        }
        final ImageRepresentation imageRep = this.getImageRep();
        imageRep.addWatcher(imageObserver);
        if (b) {
            imageRep.startProduction();
        }
    }
    
    private synchronized void reconstruct(final int n) {
        if ((n & ~this.availinfo) != 0x0) {
            if ((this.availinfo & 0x40) != 0x0) {
                return;
            }
            this.getImageRep().startProduction();
            while ((n & ~this.availinfo) != 0x0) {
                try {
                    this.wait();
                }
                catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
                if ((this.availinfo & 0x40) != 0x0) {
                    return;
                }
            }
        }
    }
    
    synchronized void addInfo(final int n) {
        this.availinfo |= n;
        this.notifyAll();
    }
    
    void setDimensions(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.addInfo(3);
    }
    
    void setProperties(Hashtable properties) {
        if (properties == null) {
            properties = new Hashtable();
        }
        this.properties = properties;
        this.addInfo(4);
    }
    
    synchronized void infoDone(final int n) {
        if (n == 1 || (~this.availinfo & 0x3) != 0x0) {
            this.addInfo(64);
        }
        else if ((this.availinfo & 0x4) == 0x0) {
            this.setProperties(null);
        }
    }
    
    @Override
    public void flush() {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        final ImageRepresentation imagerep;
        synchronized (this) {
            this.availinfo &= 0xFFFFFFBF;
            imagerep = this.imagerep;
            this.imagerep = null;
        }
        if (imagerep != null) {
            imagerep.abort();
        }
        if (this.src != null) {
            this.src.flush();
        }
    }
    
    protected ImageRepresentation makeImageRep() {
        return new ImageRepresentation(this, ColorModel.getRGBdefault(), false);
    }
    
    public synchronized ImageRepresentation getImageRep() {
        if (this.src != null) {
            this.src.checkSecurity(null, false);
        }
        if (this.imagerep == null) {
            this.imagerep = this.makeImageRep();
        }
        return this.imagerep;
    }
    
    @Override
    public Graphics getGraphics() {
        throw new UnsupportedOperationException("getGraphics() not valid for images created with createImage(producer)");
    }
    
    public ColorModel getColorModel() {
        return this.getImageRep().getColorModel();
    }
    
    public BufferedImage getBufferedImage() {
        return this.getImageRep().getBufferedImage();
    }
    
    @Override
    public void setAccelerationPriority(final float accelerationPriority) {
        super.setAccelerationPriority(accelerationPriority);
        this.getImageRep().setAccelerationPriority(this.accelerationPriority);
    }
    
    static {
        NativeLibLoader.loadLibraries();
    }
}
