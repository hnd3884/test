package com.octo.captcha.engine.image.utils;

import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageWriter;

public class ImageToFile
{
    private static ImageWriter writer;
    
    public static void serialize(final BufferedImage bufferedImage, final File file) throws IOException {
        file.createNewFile();
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        encodeJPG(fileOutputStream, bufferedImage);
        fileOutputStream.flush();
        fileOutputStream.close();
    }
    
    public static void encodeJPG(final OutputStream outputStream, final BufferedImage bufferedImage) throws IOException {
        ImageToFile.writer.setOutput(ImageIO.createImageOutputStream(outputStream));
        ImageToFile.writer.write(bufferedImage);
    }
    
    static {
        ImageToFile.writer = ImageIO.getImageWritersByFormatName("jpeg").next();
    }
}
