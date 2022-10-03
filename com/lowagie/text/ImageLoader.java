package com.lowagie.text;

import java.io.ByteArrayInputStream;
import org.apache.commons.io.IOUtils;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.net.URL;

public class ImageLoader
{
    public static Image getPngImage(final URL url) {
        try {
            final InputStream is = url.openStream();
            final BufferedImage bufferedImage = ImageIO.read(is);
            is.close();
            return Image.getInstance(bufferedImage, null, false);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static Image getGifImage(final URL url) {
        try {
            final InputStream is = url.openStream();
            final BufferedImage bufferedImage = ImageIO.read(is);
            is.close();
            return Image.getInstance(bufferedImage, null, false);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static Image getTiffImage(final URL url) {
        try {
            final InputStream is = url.openStream();
            final BufferedImage bufferedImage = ImageIO.read(is);
            is.close();
            return Image.getInstance(bufferedImage, null, false);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static Image getBmpImage(final URL url) {
        try {
            final InputStream is = url.openStream();
            final BufferedImage bufferedImage = ImageIO.read(is);
            is.close();
            return Image.getInstance(bufferedImage, null, false);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static Image getJpegImage(final URL url) {
        try {
            final InputStream is = url.openStream();
            final byte[] imageBytes = IOUtils.toByteArray(is);
            is.close();
            return new Jpeg(imageBytes);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static Image getJpeg2000Image(final URL url) {
        try {
            final InputStream is = url.openStream();
            final byte[] imageBytes = IOUtils.toByteArray(is);
            is.close();
            return new Jpeg2000(imageBytes);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static Image getGifImage(final byte[] imageData) {
        try {
            final BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
            return Image.getInstance(bufferedImage, null, false);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static Image getPngImage(final byte[] imageData) {
        try {
            final BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
            return Image.getInstance(bufferedImage, null, false);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static Image getBmpImage(final byte[] imageData) {
        try {
            final BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
            return Image.getInstance(bufferedImage, null, false);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static Image getTiffImage(final byte[] imageData) {
        try {
            final BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
            return Image.getInstance(bufferedImage, null, false);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static Image getJpegImage(final byte[] imageData) {
        try {
            return new Jpeg(imageData);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
    
    public static Image getJpeg2000Image(final byte[] imageData) {
        try {
            return new Jpeg2000(imageData);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
}
