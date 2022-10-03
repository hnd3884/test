package org.apache.poi.sl.image;

import org.apache.poi.util.POILogFactory;
import java.awt.image.BufferedImage;
import org.apache.poi.util.Units;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.awt.Dimension;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;

@Internal
public class ImageHeaderBitmap
{
    private static final POILogger LOG;
    private final Dimension size;
    
    public ImageHeaderBitmap(final byte[] data, final int offset) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new ByteArrayInputStream(data, offset, data.length - offset));
        }
        catch (final IOException e) {
            ImageHeaderBitmap.LOG.log(5, "Can't determine image dimensions", e);
        }
        this.size = ((img == null) ? new Dimension(200, 200) : new Dimension((int)Units.pixelToPoints(img.getWidth()), (int)Units.pixelToPoints(img.getHeight())));
    }
    
    public Dimension getSize() {
        return this.size;
    }
    
    static {
        LOG = POILogFactory.getLogger(ImageHeaderBitmap.class);
    }
}
