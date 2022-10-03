package com.sun.xml.internal.messaging.saaj.soap;

import java.awt.Graphics;
import java.awt.image.RenderedImage;
import java.awt.image.ImageObserver;
import java.awt.MediaTracker;
import java.awt.Image;
import java.io.IOException;
import java.io.OutputStream;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.activation.DataSource;
import javax.activation.ActivationDataFlavor;
import java.awt.datatransfer.DataFlavor;
import javax.activation.DataContentHandler;
import java.awt.Component;

public class JpegDataContentHandler extends Component implements DataContentHandler
{
    public static final String STR_SRC = "java.awt.Image";
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        final DataFlavor[] flavors = { null };
        try {
            flavors[0] = new ActivationDataFlavor(Class.forName("java.awt.Image"), "image/jpeg", "JPEG");
        }
        catch (final Exception e) {
            System.out.println(e);
        }
        return flavors;
    }
    
    @Override
    public Object getTransferData(final DataFlavor df, final DataSource ds) {
        if (df.getMimeType().startsWith("image/jpeg") && df.getRepresentationClass().getName().equals("java.awt.Image")) {
            InputStream inputStream = null;
            BufferedImage jpegLoadImage = null;
            try {
                inputStream = ds.getInputStream();
                jpegLoadImage = ImageIO.read(inputStream);
            }
            catch (final Exception e) {
                System.out.println(e);
            }
            return jpegLoadImage;
        }
        return null;
    }
    
    @Override
    public Object getContent(final DataSource ds) {
        InputStream inputStream = null;
        BufferedImage jpegLoadImage = null;
        try {
            inputStream = ds.getInputStream();
            jpegLoadImage = ImageIO.read(inputStream);
        }
        catch (final Exception ex) {}
        return jpegLoadImage;
    }
    
    @Override
    public void writeTo(final Object obj, final String mimeType, final OutputStream os) throws IOException {
        if (!mimeType.equals("image/jpeg")) {
            throw new IOException("Invalid content type \"" + mimeType + "\" for ImageContentHandler");
        }
        if (obj == null) {
            throw new IOException("Null object for ImageContentHandler");
        }
        try {
            BufferedImage bufImage = null;
            if (obj instanceof BufferedImage) {
                bufImage = (BufferedImage)obj;
            }
            else {
                final Image img = (Image)obj;
                final MediaTracker tracker = new MediaTracker(this);
                tracker.addImage(img, 0);
                tracker.waitForAll();
                if (tracker.isErrorAny()) {
                    throw new IOException("Error while loading image");
                }
                bufImage = new BufferedImage(img.getWidth(null), img.getHeight(null), 1);
                final Graphics g = bufImage.createGraphics();
                g.drawImage(img, 0, 0, null);
            }
            ImageIO.write(bufImage, "jpeg", os);
        }
        catch (final Exception ex) {
            throw new IOException("Unable to run the JPEG Encoder on a stream " + ex.getMessage());
        }
    }
}
