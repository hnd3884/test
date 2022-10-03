package org.apache.poi.sl.image;

import org.apache.poi.util.POILogFactory;
import java.awt.Dimension;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.LittleEndian;
import java.awt.Rectangle;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;

@Internal
public class ImageHeaderEMF
{
    private static final POILogger LOG;
    private static final String EMF_SIGNATURE = " EMF";
    private final Rectangle deviceBounds;
    
    public ImageHeaderEMF(final byte[] data, final int off) {
        int offset = off;
        final int type = (int)LittleEndian.getUInt(data, offset);
        offset += 4;
        if (type != 1) {
            ImageHeaderEMF.LOG.log(5, "Invalid EMF picture - invalid type");
            this.deviceBounds = new Rectangle(0, 0, 200, 200);
            return;
        }
        offset += 4;
        final int left = LittleEndian.getInt(data, offset);
        offset += 4;
        final int top = LittleEndian.getInt(data, offset);
        offset += 4;
        final int right = LittleEndian.getInt(data, offset);
        offset += 4;
        final int bottom = LittleEndian.getInt(data, offset);
        offset += 4;
        this.deviceBounds = new Rectangle(left, top, right - left, bottom - top);
        offset += 16;
        final String signature = new String(data, offset, " EMF".length(), LocaleUtil.CHARSET_1252);
        if (!" EMF".equals(signature)) {
            ImageHeaderEMF.LOG.log(5, "Invalid EMF picture - invalid signature");
        }
    }
    
    public Dimension getSize() {
        return this.deviceBounds.getSize();
    }
    
    public Rectangle getBounds() {
        return this.deviceBounds;
    }
    
    static {
        LOG = POILogFactory.getLogger(ImageHeaderEMF.class);
    }
}
