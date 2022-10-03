package org.jfree.chart.encoders;

import java.io.OutputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;

public interface ImageEncoder
{
    byte[] encode(final BufferedImage p0) throws IOException;
    
    void encode(final BufferedImage p0, final OutputStream p1) throws IOException;
    
    float getQuality();
    
    void setQuality(final float p0);
    
    boolean isEncodingAlpha();
    
    void setEncodingAlpha(final boolean p0);
}
