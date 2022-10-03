package java.awt.image;

import java.util.Hashtable;
import java.awt.Image;

public class PixelGrabber implements ImageConsumer
{
    ImageProducer producer;
    int dstX;
    int dstY;
    int dstW;
    int dstH;
    ColorModel imageModel;
    byte[] bytePixels;
    int[] intPixels;
    int dstOff;
    int dstScan;
    private boolean grabbing;
    private int flags;
    private static final int GRABBEDBITS = 48;
    private static final int DONEBITS = 112;
    
    public PixelGrabber(final Image image, final int n, final int n2, final int n3, final int n4, final int[] array, final int n5, final int n6) {
        this(image.getSource(), n, n2, n3, n4, array, n5, n6);
    }
    
    public PixelGrabber(final ImageProducer producer, final int dstX, final int dstY, final int dstW, final int dstH, final int[] intPixels, final int dstOff, final int dstScan) {
        this.producer = producer;
        this.dstX = dstX;
        this.dstY = dstY;
        this.dstW = dstW;
        this.dstH = dstH;
        this.dstOff = dstOff;
        this.dstScan = dstScan;
        this.intPixels = intPixels;
        this.imageModel = ColorModel.getRGBdefault();
    }
    
    public PixelGrabber(final Image image, final int dstX, final int dstY, final int dstW, final int dstH, final boolean b) {
        this.producer = image.getSource();
        this.dstX = dstX;
        this.dstY = dstY;
        this.dstW = dstW;
        this.dstH = dstH;
        if (b) {
            this.imageModel = ColorModel.getRGBdefault();
        }
    }
    
    public synchronized void startGrabbing() {
        if ((this.flags & 0x70) != 0x0) {
            return;
        }
        if (!this.grabbing) {
            this.grabbing = true;
            this.flags &= 0xFFFFFF7F;
            this.producer.startProduction(this);
        }
    }
    
    public synchronized void abortGrabbing() {
        this.imageComplete(4);
    }
    
    public boolean grabPixels() throws InterruptedException {
        return this.grabPixels(0L);
    }
    
    public synchronized boolean grabPixels(final long n) throws InterruptedException {
        if ((this.flags & 0x70) != 0x0) {
            return (this.flags & 0x30) != 0x0;
        }
        final long n2 = n + System.currentTimeMillis();
        if (!this.grabbing) {
            this.grabbing = true;
            this.flags &= 0xFFFFFF7F;
            this.producer.startProduction(this);
        }
        while (this.grabbing) {
            long n3;
            if (n == 0L) {
                n3 = 0L;
            }
            else {
                n3 = n2 - System.currentTimeMillis();
                if (n3 <= 0L) {
                    break;
                }
            }
            this.wait(n3);
        }
        return (this.flags & 0x30) != 0x0;
    }
    
    public synchronized int getStatus() {
        return this.flags;
    }
    
    public synchronized int getWidth() {
        return (this.dstW < 0) ? -1 : this.dstW;
    }
    
    public synchronized int getHeight() {
        return (this.dstH < 0) ? -1 : this.dstH;
    }
    
    public synchronized Object getPixels() {
        return (this.bytePixels == null) ? this.intPixels : this.bytePixels;
    }
    
    public synchronized ColorModel getColorModel() {
        return this.imageModel;
    }
    
    @Override
    public void setDimensions(final int n, final int n2) {
        if (this.dstW < 0) {
            this.dstW = n - this.dstX;
        }
        if (this.dstH < 0) {
            this.dstH = n2 - this.dstY;
        }
        if (this.dstW <= 0 || this.dstH <= 0) {
            this.imageComplete(3);
        }
        else if (this.intPixels == null && this.imageModel == ColorModel.getRGBdefault()) {
            this.intPixels = new int[this.dstW * this.dstH];
            this.dstScan = this.dstW;
            this.dstOff = 0;
        }
        this.flags |= 0x3;
    }
    
    @Override
    public void setHints(final int n) {
    }
    
    @Override
    public void setProperties(final Hashtable<?, ?> hashtable) {
    }
    
    @Override
    public void setColorModel(final ColorModel colorModel) {
    }
    
    private void convertToRGB() {
        final int n = this.dstW * this.dstH;
        final int[] intPixels = new int[n];
        if (this.bytePixels != null) {
            for (int i = 0; i < n; ++i) {
                intPixels[i] = this.imageModel.getRGB(this.bytePixels[i] & 0xFF);
            }
        }
        else if (this.intPixels != null) {
            for (int j = 0; j < n; ++j) {
                intPixels[j] = this.imageModel.getRGB(this.intPixels[j]);
            }
        }
        this.bytePixels = null;
        this.intPixels = intPixels;
        this.dstScan = this.dstW;
        this.dstOff = 0;
        this.imageModel = ColorModel.getRGBdefault();
    }
    
    @Override
    public void setPixels(int n, int n2, int n3, int n4, final ColorModel imageModel, final byte[] array, int n5, final int n6) {
        if (n2 < this.dstY) {
            final int n7 = this.dstY - n2;
            if (n7 >= n4) {
                return;
            }
            n5 += n6 * n7;
            n2 += n7;
            n4 -= n7;
        }
        if (n2 + n4 > this.dstY + this.dstH) {
            n4 = this.dstY + this.dstH - n2;
            if (n4 <= 0) {
                return;
            }
        }
        if (n < this.dstX) {
            final int n8 = this.dstX - n;
            if (n8 >= n3) {
                return;
            }
            n5 += n8;
            n += n8;
            n3 -= n8;
        }
        if (n + n3 > this.dstX + this.dstW) {
            n3 = this.dstX + this.dstW - n;
            if (n3 <= 0) {
                return;
            }
        }
        int n9 = this.dstOff + (n2 - this.dstY) * this.dstScan + (n - this.dstX);
        if (this.intPixels == null) {
            if (this.bytePixels == null) {
                this.bytePixels = new byte[this.dstW * this.dstH];
                this.dstScan = this.dstW;
                this.dstOff = 0;
                this.imageModel = imageModel;
            }
            else if (this.imageModel != imageModel) {
                this.convertToRGB();
            }
            if (this.bytePixels != null) {
                for (int i = n4; i > 0; --i) {
                    System.arraycopy(array, n5, this.bytePixels, n9, n3);
                    n5 += n6;
                    n9 += this.dstScan;
                }
            }
        }
        if (this.intPixels != null) {
            final int n10 = this.dstScan - n3;
            final int n11 = n6 - n3;
            for (int j = n4; j > 0; --j) {
                for (int k = n3; k > 0; --k) {
                    this.intPixels[n9++] = imageModel.getRGB(array[n5++] & 0xFF);
                }
                n5 += n11;
                n9 += n10;
            }
        }
        this.flags |= 0x8;
    }
    
    @Override
    public void setPixels(int n, int n2, int n3, int n4, final ColorModel imageModel, final int[] array, int n5, final int n6) {
        if (n2 < this.dstY) {
            final int n7 = this.dstY - n2;
            if (n7 >= n4) {
                return;
            }
            n5 += n6 * n7;
            n2 += n7;
            n4 -= n7;
        }
        if (n2 + n4 > this.dstY + this.dstH) {
            n4 = this.dstY + this.dstH - n2;
            if (n4 <= 0) {
                return;
            }
        }
        if (n < this.dstX) {
            final int n8 = this.dstX - n;
            if (n8 >= n3) {
                return;
            }
            n5 += n8;
            n += n8;
            n3 -= n8;
        }
        if (n + n3 > this.dstX + this.dstW) {
            n3 = this.dstX + this.dstW - n;
            if (n3 <= 0) {
                return;
            }
        }
        if (this.intPixels == null) {
            if (this.bytePixels == null) {
                this.intPixels = new int[this.dstW * this.dstH];
                this.dstScan = this.dstW;
                this.dstOff = 0;
                this.imageModel = imageModel;
            }
            else {
                this.convertToRGB();
            }
        }
        int n9 = this.dstOff + (n2 - this.dstY) * this.dstScan + (n - this.dstX);
        if (this.imageModel == imageModel) {
            for (int i = n4; i > 0; --i) {
                System.arraycopy(array, n5, this.intPixels, n9, n3);
                n5 += n6;
                n9 += this.dstScan;
            }
        }
        else {
            if (this.imageModel != ColorModel.getRGBdefault()) {
                this.convertToRGB();
            }
            final int n10 = this.dstScan - n3;
            final int n11 = n6 - n3;
            for (int j = n4; j > 0; --j) {
                for (int k = n3; k > 0; --k) {
                    this.intPixels[n9++] = imageModel.getRGB(array[n5++]);
                }
                n5 += n11;
                n9 += n10;
            }
        }
        this.flags |= 0x8;
    }
    
    @Override
    public synchronized void imageComplete(final int n) {
        this.grabbing = false;
        switch (n) {
            default: {
                this.flags |= 0xC0;
                break;
            }
            case 4: {
                this.flags |= 0x80;
                break;
            }
            case 3: {
                this.flags |= 0x20;
                break;
            }
            case 2: {
                this.flags |= 0x10;
                break;
            }
        }
        this.producer.removeConsumer(this);
        this.notifyAll();
    }
    
    public synchronized int status() {
        return this.flags;
    }
}
