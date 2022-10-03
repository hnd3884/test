package org.jfree.chart.encoders;

import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;

public class SunPNGEncoderAdapter implements ImageEncoder
{
    public float getQuality() {
        return 0.0f;
    }
    
    public void setQuality(final float quality) {
    }
    
    public boolean isEncodingAlpha() {
        return false;
    }
    
    public void setEncodingAlpha(final boolean encodingAlpha) {
    }
    
    public byte[] encode(final BufferedImage bufferedImage) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        this.encode(bufferedImage, outputStream);
        return outputStream.toByteArray();
    }
    
    public void encode(final BufferedImage bufferedImage, final OutputStream outputStream) throws IOException {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Null 'image' argument.");
        }
        if (outputStream == null) {
            throw new IllegalArgumentException("Null 'outputStream' argument.");
        }
        ImageIO.write(bufferedImage, "png", outputStream);
    }
}
