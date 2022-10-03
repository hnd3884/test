package com.sun.xml.internal.ws.encoding;

import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.awt.MediaTracker;
import javax.imageio.stream.ImageOutputStream;
import java.util.Iterator;
import java.awt.image.RenderedImage;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import javax.activation.DataSource;
import java.util.Arrays;
import javax.activation.ActivationDataFlavor;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.datatransfer.DataFlavor;
import java.util.logging.Logger;
import javax.activation.DataContentHandler;
import java.awt.Component;

public class ImageDataContentHandler extends Component implements DataContentHandler
{
    private static final Logger log;
    private final DataFlavor[] flavor;
    
    public ImageDataContentHandler() {
        final String[] mimeTypes = ImageIO.getReaderMIMETypes();
        this.flavor = new DataFlavor[mimeTypes.length];
        for (int i = 0; i < mimeTypes.length; ++i) {
            this.flavor[i] = new ActivationDataFlavor(Image.class, mimeTypes[i], "Image");
        }
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return Arrays.copyOf(this.flavor, this.flavor.length);
    }
    
    @Override
    public Object getTransferData(final DataFlavor df, final DataSource ds) throws IOException {
        for (final DataFlavor aFlavor : this.flavor) {
            if (aFlavor.equals(df)) {
                return this.getContent(ds);
            }
        }
        return null;
    }
    
    @Override
    public Object getContent(final DataSource ds) throws IOException {
        return ImageIO.read(new BufferedInputStream(ds.getInputStream()));
    }
    
    @Override
    public void writeTo(final Object obj, final String type, final OutputStream os) throws IOException {
        try {
            BufferedImage bufImage;
            if (obj instanceof BufferedImage) {
                bufImage = (BufferedImage)obj;
            }
            else {
                if (!(obj instanceof Image)) {
                    throw new IOException("ImageDataContentHandler requires Image object, was given object of type " + obj.getClass().toString());
                }
                bufImage = this.render((Image)obj);
            }
            ImageWriter writer = null;
            final Iterator<ImageWriter> i = ImageIO.getImageWritersByMIMEType(type);
            if (i.hasNext()) {
                writer = i.next();
            }
            if (writer == null) {
                throw new IOException("Unsupported mime type:" + type);
            }
            final ImageOutputStream stream = ImageIO.createImageOutputStream(os);
            writer.setOutput(stream);
            writer.write(bufImage);
            writer.dispose();
            stream.close();
        }
        catch (final Exception e) {
            throw new IOException("Unable to encode the image to a stream " + e.getMessage());
        }
    }
    
    private BufferedImage render(final Image img) throws InterruptedException {
        final MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(img, 0);
        tracker.waitForAll();
        final BufferedImage bufImage = new BufferedImage(img.getWidth(null), img.getHeight(null), 1);
        final Graphics g = bufImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return bufImage;
    }
    
    static {
        log = Logger.getLogger(ImageDataContentHandler.class.getName());
    }
}
