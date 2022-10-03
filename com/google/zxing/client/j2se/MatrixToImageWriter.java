package com.google.zxing.client.j2se;

import java.io.OutputStream;
import java.io.IOException;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import com.google.zxing.common.BitMatrix;

public final class MatrixToImageWriter
{
    private static final int BLACK = -16777216;
    private static final int WHITE = -1;
    
    private MatrixToImageWriter() {
    }
    
    public static BufferedImage toBufferedImage(final BitMatrix matrix) {
        final int width = matrix.getWidth();
        final int height = matrix.getHeight();
        final BufferedImage image = new BufferedImage(width, height, 1);
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                image.setRGB(x, y, matrix.get(x, y) ? -16777216 : -1);
            }
        }
        return image;
    }
    
    public static void writeToFile(final BitMatrix matrix, final String format, final File file) throws IOException {
        final BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }
    
    public static void writeToStream(final BitMatrix matrix, final String format, final OutputStream stream) throws IOException {
        final BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }
}
