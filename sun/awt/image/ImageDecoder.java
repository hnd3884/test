package sun.awt.image;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.awt.image.ColorModel;
import java.util.Hashtable;
import java.awt.image.ImageConsumer;
import java.io.InputStream;

public abstract class ImageDecoder
{
    InputStreamImageSource source;
    InputStream input;
    Thread feeder;
    protected boolean aborted;
    protected boolean finished;
    ImageConsumerQueue queue;
    ImageDecoder next;
    
    public ImageDecoder(final InputStreamImageSource source, final InputStream input) {
        this.source = source;
        this.input = input;
        this.feeder = Thread.currentThread();
    }
    
    public boolean isConsumer(final ImageConsumer imageConsumer) {
        return ImageConsumerQueue.isConsumer(this.queue, imageConsumer);
    }
    
    public void removeConsumer(final ImageConsumer imageConsumer) {
        this.queue = ImageConsumerQueue.removeConsumer(this.queue, imageConsumer, false);
        if (!this.finished && this.queue == null) {
            this.abort();
        }
    }
    
    protected ImageConsumerQueue nextConsumer(ImageConsumerQueue next) {
        synchronized (this.source) {
            if (this.aborted) {
                return null;
            }
            for (next = ((next == null) ? this.queue : next.next); next != null; next = next.next) {
                if (next.interested) {
                    return next;
                }
            }
        }
        return null;
    }
    
    protected int setDimensions(final int n, final int n2) {
        ImageConsumerQueue nextConsumer = null;
        int n3 = 0;
        while ((nextConsumer = this.nextConsumer(nextConsumer)) != null) {
            nextConsumer.consumer.setDimensions(n, n2);
            ++n3;
        }
        return n3;
    }
    
    protected int setProperties(final Hashtable properties) {
        ImageConsumerQueue nextConsumer = null;
        int n = 0;
        while ((nextConsumer = this.nextConsumer(nextConsumer)) != null) {
            nextConsumer.consumer.setProperties(properties);
            ++n;
        }
        return n;
    }
    
    protected int setColorModel(final ColorModel colorModel) {
        ImageConsumerQueue nextConsumer = null;
        int n = 0;
        while ((nextConsumer = this.nextConsumer(nextConsumer)) != null) {
            nextConsumer.consumer.setColorModel(colorModel);
            ++n;
        }
        return n;
    }
    
    protected int setHints(final int hints) {
        ImageConsumerQueue nextConsumer = null;
        int n = 0;
        while ((nextConsumer = this.nextConsumer(nextConsumer)) != null) {
            nextConsumer.consumer.setHints(hints);
            ++n;
        }
        return n;
    }
    
    protected void headerComplete() {
        this.feeder.setPriority(3);
    }
    
    protected int setPixels(final int n, final int n2, final int n3, final int n4, final ColorModel colorModel, final byte[] array, final int n5, final int n6) {
        this.source.latchConsumers(this);
        ImageConsumerQueue nextConsumer = null;
        int n7 = 0;
        while ((nextConsumer = this.nextConsumer(nextConsumer)) != null) {
            nextConsumer.consumer.setPixels(n, n2, n3, n4, colorModel, array, n5, n6);
            ++n7;
        }
        return n7;
    }
    
    protected int setPixels(final int n, final int n2, final int n3, final int n4, final ColorModel colorModel, final int[] array, final int n5, final int n6) {
        this.source.latchConsumers(this);
        ImageConsumerQueue nextConsumer = null;
        int n7 = 0;
        while ((nextConsumer = this.nextConsumer(nextConsumer)) != null) {
            nextConsumer.consumer.setPixels(n, n2, n3, n4, colorModel, array, n5, n6);
            ++n7;
        }
        return n7;
    }
    
    protected int imageComplete(final int n, final boolean b) {
        this.source.latchConsumers(this);
        if (b) {
            this.finished = true;
            this.source.doneDecoding(this);
        }
        ImageConsumerQueue nextConsumer = null;
        int n2 = 0;
        while ((nextConsumer = this.nextConsumer(nextConsumer)) != null) {
            nextConsumer.consumer.imageComplete(n);
            ++n2;
        }
        return n2;
    }
    
    public abstract void produceImage() throws IOException, ImageFormatException;
    
    public void abort() {
        this.aborted = true;
        this.source.doneDecoding(this);
        this.close();
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                ImageDecoder.this.feeder.interrupt();
                return null;
            }
        });
    }
    
    public synchronized void close() {
        if (this.input != null) {
            try {
                this.input.close();
            }
            catch (final IOException ex) {}
        }
    }
}
