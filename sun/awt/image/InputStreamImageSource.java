package sun.awt.image;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;

public abstract class InputStreamImageSource implements ImageProducer, ImageFetchable
{
    ImageConsumerQueue consumers;
    ImageDecoder decoder;
    ImageDecoder decoders;
    boolean awaitingFetch;
    
    public InputStreamImageSource() {
        this.awaitingFetch = false;
    }
    
    abstract boolean checkSecurity(final Object p0, final boolean p1);
    
    int countConsumers(ImageConsumerQueue next) {
        int n = 0;
        while (next != null) {
            ++n;
            next = next.next;
        }
        return n;
    }
    
    synchronized int countConsumers() {
        ImageDecoder imageDecoder = this.decoders;
        int countConsumers = this.countConsumers(this.consumers);
        while (imageDecoder != null) {
            countConsumers += this.countConsumers(imageDecoder.queue);
            imageDecoder = imageDecoder.next;
        }
        return countConsumers;
    }
    
    @Override
    public void addConsumer(final ImageConsumer imageConsumer) {
        this.addConsumer(imageConsumer, false);
    }
    
    synchronized void printQueue(ImageConsumerQueue next, final String s) {
        while (next != null) {
            System.out.println(s + next);
            next = next.next;
        }
    }
    
    synchronized void printQueues(final String s) {
        System.out.println(s + "[ -----------");
        this.printQueue(this.consumers, "  ");
        for (ImageDecoder imageDecoder = this.decoders; imageDecoder != null; imageDecoder = imageDecoder.next) {
            System.out.println("    " + imageDecoder);
            this.printQueue(imageDecoder.queue, "      ");
        }
        System.out.println("----------- ]" + s);
    }
    
    synchronized void addConsumer(final ImageConsumer imageConsumer, final boolean b) {
        this.checkSecurity(null, false);
        for (ImageDecoder imageDecoder = this.decoders; imageDecoder != null; imageDecoder = imageDecoder.next) {
            if (imageDecoder.isConsumer(imageConsumer)) {
                return;
            }
        }
        ImageConsumerQueue imageConsumerQueue;
        for (imageConsumerQueue = this.consumers; imageConsumerQueue != null && imageConsumerQueue.consumer != imageConsumer; imageConsumerQueue = imageConsumerQueue.next) {}
        if (imageConsumerQueue == null) {
            final ImageConsumerQueue consumers = new ImageConsumerQueue(this, imageConsumer);
            consumers.next = this.consumers;
            this.consumers = consumers;
        }
        else {
            if (!imageConsumerQueue.secure) {
                Object securityContext = null;
                final SecurityManager securityManager = System.getSecurityManager();
                if (securityManager != null) {
                    securityContext = securityManager.getSecurityContext();
                }
                if (imageConsumerQueue.securityContext == null) {
                    imageConsumerQueue.securityContext = securityContext;
                }
                else if (!imageConsumerQueue.securityContext.equals(securityContext)) {
                    this.errorConsumer(imageConsumerQueue, false);
                    throw new SecurityException("Applets are trading image data!");
                }
            }
            imageConsumerQueue.interested = true;
        }
        if (b && this.decoder == null) {
            this.startProduction();
        }
    }
    
    @Override
    public synchronized boolean isConsumer(final ImageConsumer imageConsumer) {
        for (ImageDecoder imageDecoder = this.decoders; imageDecoder != null; imageDecoder = imageDecoder.next) {
            if (imageDecoder.isConsumer(imageConsumer)) {
                return true;
            }
        }
        return ImageConsumerQueue.isConsumer(this.consumers, imageConsumer);
    }
    
    private void errorAllConsumers(ImageConsumerQueue next, final boolean b) {
        while (next != null) {
            if (next.interested) {
                this.errorConsumer(next, b);
            }
            next = next.next;
        }
    }
    
    private void errorConsumer(final ImageConsumerQueue imageConsumerQueue, final boolean b) {
        imageConsumerQueue.consumer.imageComplete(1);
        if (b && imageConsumerQueue.consumer instanceof ImageRepresentation) {
            ((ImageRepresentation)imageConsumerQueue.consumer).image.flush();
        }
        this.removeConsumer(imageConsumerQueue.consumer);
    }
    
    @Override
    public synchronized void removeConsumer(final ImageConsumer imageConsumer) {
        for (ImageDecoder imageDecoder = this.decoders; imageDecoder != null; imageDecoder = imageDecoder.next) {
            imageDecoder.removeConsumer(imageConsumer);
        }
        this.consumers = ImageConsumerQueue.removeConsumer(this.consumers, imageConsumer, false);
    }
    
    @Override
    public void startProduction(final ImageConsumer imageConsumer) {
        this.addConsumer(imageConsumer, true);
    }
    
    private synchronized void startProduction() {
        if (!this.awaitingFetch) {
            if (ImageFetcher.add(this)) {
                this.awaitingFetch = true;
            }
            else {
                final ImageConsumerQueue consumers = this.consumers;
                this.consumers = null;
                this.errorAllConsumers(consumers, false);
            }
        }
    }
    
    private synchronized void stopProduction() {
        if (this.awaitingFetch) {
            ImageFetcher.remove(this);
            this.awaitingFetch = false;
        }
    }
    
    @Override
    public void requestTopDownLeftRightResend(final ImageConsumer imageConsumer) {
    }
    
    protected abstract ImageDecoder getDecoder();
    
    protected ImageDecoder decoderForType(final InputStream inputStream, final String s) {
        return null;
    }
    
    protected ImageDecoder getDecoder(InputStream inputStream) {
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }
        try {
            inputStream.mark(8);
            final int read = inputStream.read();
            final int read2 = inputStream.read();
            final int read3 = inputStream.read();
            final int read4 = inputStream.read();
            final int read5 = inputStream.read();
            final int read6 = inputStream.read();
            final int read7 = inputStream.read();
            final int read8 = inputStream.read();
            inputStream.reset();
            inputStream.mark(-1);
            if (read == 71 && read2 == 73 && read3 == 70 && read4 == 56) {
                return new GifImageDecoder(this, inputStream);
            }
            if (read == 255 && read2 == 216 && read3 == 255) {
                return new JPEGImageDecoder(this, inputStream);
            }
            if (read == 35 && read2 == 100 && read3 == 101 && read4 == 102) {
                return new XbmImageDecoder(this, inputStream);
            }
            if (read == 137 && read2 == 80 && read3 == 78 && read4 == 71 && read5 == 13 && read6 == 10 && read7 == 26 && read8 == 10) {
                return new PNGImageDecoder(this, inputStream);
            }
        }
        catch (final IOException ex) {}
        return null;
    }
    
    @Override
    public void doFetch() {
        synchronized (this) {
            if (this.consumers == null) {
                this.awaitingFetch = false;
                return;
            }
        }
        final ImageDecoder decoder = this.getDecoder();
        if (decoder == null) {
            this.badDecoder();
        }
        else {
            this.setDecoder(decoder);
            try {
                decoder.produceImage();
            }
            catch (final IOException ex) {
                ex.printStackTrace();
            }
            catch (final ImageFormatException ex2) {
                ex2.printStackTrace();
            }
            finally {
                this.removeDecoder(decoder);
                if (Thread.currentThread().isInterrupted() || !Thread.currentThread().isAlive()) {
                    this.errorAllConsumers(decoder.queue, true);
                }
                else {
                    this.errorAllConsumers(decoder.queue, false);
                }
            }
        }
    }
    
    private void badDecoder() {
        final ImageConsumerQueue consumers;
        synchronized (this) {
            consumers = this.consumers;
            this.consumers = null;
            this.awaitingFetch = false;
        }
        this.errorAllConsumers(consumers, false);
    }
    
    private void setDecoder(final ImageDecoder imageDecoder) {
        ImageConsumerQueue queue;
        synchronized (this) {
            imageDecoder.next = this.decoders;
            this.decoders = imageDecoder;
            this.decoder = imageDecoder;
            queue = this.consumers;
            imageDecoder.queue = queue;
            this.consumers = null;
            this.awaitingFetch = false;
        }
        while (queue != null) {
            if (queue.interested && !this.checkSecurity(queue.securityContext, true)) {
                this.errorConsumer(queue, false);
            }
            queue = queue.next;
        }
    }
    
    private synchronized void removeDecoder(final ImageDecoder imageDecoder) {
        this.doneDecoding(imageDecoder);
        ImageDecoder imageDecoder2 = null;
        ImageDecoder imageDecoder3 = this.decoders;
        while (imageDecoder3 != null) {
            if (imageDecoder3 == imageDecoder) {
                if (imageDecoder2 == null) {
                    this.decoders = imageDecoder3.next;
                    break;
                }
                imageDecoder2.next = imageDecoder3.next;
                break;
            }
            else {
                imageDecoder2 = imageDecoder3;
                imageDecoder3 = imageDecoder3.next;
            }
        }
    }
    
    synchronized void doneDecoding(final ImageDecoder imageDecoder) {
        if (this.decoder == imageDecoder) {
            this.decoder = null;
            if (this.consumers != null) {
                this.startProduction();
            }
        }
    }
    
    void latchConsumers(final ImageDecoder imageDecoder) {
        this.doneDecoding(imageDecoder);
    }
    
    synchronized void flush() {
        this.decoder = null;
    }
}
