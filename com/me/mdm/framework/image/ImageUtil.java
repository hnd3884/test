package com.me.mdm.framework.image;

import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.awt.image.BufferedImage;
import com.dd.plist.NSData;
import java.util.logging.Logger;

public class ImageUtil
{
    private static Logger logger;
    
    public BufferedImage getImageFromData(final NSData data) {
        BufferedImage image = null;
        try {
            final byte[] bytes = data.bytes();
            final ByteArrayInputStream imageStream = new ByteArrayInputStream(bytes);
            image = ImageIO.read(imageStream);
        }
        catch (final Exception e) {
            ImageUtil.logger.log(Level.SEVERE, "Exception while changing the image from data", e);
        }
        return image;
    }
    
    public void saveImageFromData(final NSData data, final String folderPath, final String fileName) throws Exception {
        try {
            if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(folderPath)) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(folderPath);
            }
            final String filePath = folderPath + File.separator + fileName;
            final byte[] bytes = data.bytes();
            ApiFactoryProvider.getFileAccessAPI().writeFile(filePath, bytes);
        }
        catch (final Exception e) {
            ImageUtil.logger.log(Level.SEVERE, "Exception while saving the image from data", e);
            throw new Exception();
        }
    }
    
    public NSData getImageDataFromPath(final String fileName) {
        NSData data = null;
        try {
            final byte[] bytes = ApiFactoryProvider.getFileAccessAPI().readFileContentAsArray(fileName);
            if (bytes != null) {
                data = new NSData(bytes);
            }
        }
        catch (final Exception e) {
            ImageUtil.logger.log(Level.SEVERE, "Exception while getting image data from file path", e);
        }
        return data;
    }
    
    static {
        ImageUtil.logger = Logger.getLogger("MDMConfigLogger");
    }
}
