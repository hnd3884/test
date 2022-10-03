package org.apache.poi.sl.draw;

import org.apache.poi.common.usermodel.GenericRecord;
import java.awt.Insets;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.apache.poi.util.Dimension2DDouble;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;

public interface ImageRenderer
{
    boolean canRender(final String p0);
    
    void loadImage(final InputStream p0, final String p1) throws IOException;
    
    void loadImage(final byte[] p0, final String p1) throws IOException;
    
    Rectangle2D getNativeBounds();
    
    Rectangle2D getBounds();
    
    default Dimension2D getDimension() {
        final Rectangle2D r = this.getBounds();
        return new Dimension2DDouble(Math.abs(r.getWidth()), Math.abs(r.getHeight()));
    }
    
    void setAlpha(final double p0);
    
    BufferedImage getImage();
    
    BufferedImage getImage(final Dimension2D p0);
    
    boolean drawImage(final Graphics2D p0, final Rectangle2D p1);
    
    boolean drawImage(final Graphics2D p0, final Rectangle2D p1, final Insets p2);
    
    default GenericRecord getGenericRecord() {
        return null;
    }
}
