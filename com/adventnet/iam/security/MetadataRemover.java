package com.adventnet.iam.security;

import org.apache.commons.imaging.ImageFormat;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.util.Map;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import com.zoho.security.util.CommonUtil;
import java.io.File;
import java.util.logging.Level;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import java.util.logging.Logger;

public class MetadataRemover
{
    private static final Logger LOGGER;
    private static final char EXTENSION_SEPARATOR = '.';
    private static final ThreadLocal<ExifRewriter> EXIF_REWRITER;
    
    public static ExifRewriter getExifRewriter() {
        try {
            if (MetadataRemover.EXIF_REWRITER.get() == null) {
                final ExifRewriter exifRewriter = new ExifRewriter();
                MetadataRemover.EXIF_REWRITER.set(exifRewriter);
            }
        }
        catch (final Exception e) {
            MetadataRemover.LOGGER.log(Level.WARNING, null, e);
        }
        return MetadataRemover.EXIF_REWRITER.get();
    }
    
    public static void removeExif(final File file, final String contentType) {
        BufferedOutputStream os = null;
        File temp = null;
        try {
            File parentFile = file.getParentFile();
            if (parentFile == null) {
                parentFile = SecurityUtil.getTemporaryDir();
            }
            temp = new File(parentFile, "SFEXIF_" + CommonUtil.getSecureRandomNumber() + "_" + System.currentTimeMillis() + "_" + removeExtension(file.getName()) + ".tmp");
            os = new BufferedOutputStream(new FileOutputStream(temp));
            if (contentType.equalsIgnoreCase("image/jpeg")) {
                getExifRewriter().removeExifMetadata(file, (OutputStream)os);
            }
            if (contentType.equalsIgnoreCase("image/tiff")) {
                final BufferedImage img = Imaging.getBufferedImage(file);
                final ImageFormat format = (ImageFormat)ImageFormats.TIFF;
                Imaging.writeImage(img, (OutputStream)os, format, (Map)null);
            }
        }
        catch (final Exception e) {
            MetadataRemover.LOGGER.log(Level.INFO, e.getMessage());
            try {
                if (os != null) {
                    os.close();
                }
                if (temp != null) {
                    try {
                        Files.copy(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    finally {
                        Files.delete(temp.toPath());
                    }
                }
            }
            catch (final Exception e) {
                MetadataRemover.LOGGER.log(Level.INFO, e.getMessage());
            }
        }
        finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (temp != null) {
                    try {
                        Files.copy(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    finally {
                        Files.delete(temp.toPath());
                    }
                }
            }
            catch (final Exception e2) {
                MetadataRemover.LOGGER.log(Level.INFO, e2.getMessage());
            }
        }
    }
    
    private static String removeExtension(final String filename) {
        if (filename == null) {
            return null;
        }
        final int index = filename.lastIndexOf(46);
        if (index == -1) {
            return filename;
        }
        return filename.substring(0, index);
    }
    
    static {
        LOGGER = Logger.getLogger(MetadataRemover.class.getName());
        EXIF_REWRITER = new ThreadLocal<ExifRewriter>();
    }
}
