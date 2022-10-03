package org.jfree.chart.encoders;

import java.io.OutputStream;
import java.io.IOException;
import java.awt.Image;
import com.keypoint.PngEncoder;
import java.awt.image.BufferedImage;

public class KeypointPNGEncoderAdapter implements ImageEncoder
{
    private int quality;
    private boolean encodingAlpha;
    
    public KeypointPNGEncoderAdapter() {
        this.quality = 9;
        this.encodingAlpha = false;
    }
    
    public float getQuality() {
        return (float)this.quality;
    }
    
    public void setQuality(final float quality) {
        this.quality = (int)quality;
    }
    
    public boolean isEncodingAlpha() {
        return this.encodingAlpha;
    }
    
    public void setEncodingAlpha(final boolean encodingAlpha) {
        this.encodingAlpha = encodingAlpha;
    }
    
    public byte[] encode(final BufferedImage bufferedImage) throws IOException {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Null 'image' argument.");
        }
        final PngEncoder encoder = new PngEncoder((Image)bufferedImage, this.encodingAlpha, 0, this.quality);
        return encoder.pngEncode();
    }
    
    public void encode(final BufferedImage bufferedImage, final OutputStream outputStream) throws IOException {
        if (bufferedImage == null) {
            throw new IllegalArgumentException("Null 'image' argument.");
        }
        if (outputStream == null) {
            throw new IllegalArgumentException("Null 'outputStream' argument.");
        }
        final PngEncoder encoder = new PngEncoder((Image)bufferedImage, this.encodingAlpha, 0, this.quality);
        outputStream.write(encoder.pngEncode());
    }
}
