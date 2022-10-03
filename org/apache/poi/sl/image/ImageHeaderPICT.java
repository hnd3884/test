package org.apache.poi.sl.image;

import java.awt.Dimension;
import java.awt.Rectangle;
import org.apache.poi.util.Internal;

@Internal
public class ImageHeaderPICT
{
    public static final int PICT_HEADER_OFFSET = 512;
    public static final double DEFAULT_RESOLUTION = 72.0;
    private static final byte[] V2_HEADER;
    private final Rectangle bounds;
    private final double hRes;
    private final double vRes;
    
    public ImageHeaderPICT(final byte[] data, final int off) {
        int offset = off;
        offset += 2;
        final int y1 = readUnsignedShort(data, offset);
        offset += 2;
        final int x1 = readUnsignedShort(data, offset);
        offset += 2;
        final int y2 = readUnsignedShort(data, offset);
        offset += 2;
        final int x2 = readUnsignedShort(data, offset);
        offset += 2;
        boolean isV2 = true;
        for (final byte b : ImageHeaderPICT.V2_HEADER) {
            if (b != data[offset++]) {
                isV2 = false;
                break;
            }
        }
        if (isV2) {
            this.hRes = readFixedPoint(data, offset);
            offset += 4;
            this.vRes = readFixedPoint(data, offset);
            offset += 4;
        }
        else {
            this.hRes = 72.0;
            this.vRes = 72.0;
        }
        this.bounds = new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }
    
    public Dimension getSize() {
        final int height = (int)Math.round(this.bounds.height * 72.0 / this.vRes);
        final int width = (int)Math.round(this.bounds.width * 72.0 / this.hRes);
        return new Dimension(width, height);
    }
    
    public Rectangle getBounds() {
        return this.bounds;
    }
    
    private static int readUnsignedShort(final byte[] data, final int offset) {
        final int b0 = data[offset] & 0xFF;
        final int b2 = data[offset + 1] & 0xFF;
        return b0 << 8 | b2;
    }
    
    private static double readFixedPoint(final byte[] data, final int offset) {
        final int b0 = data[offset] & 0xFF;
        final int b2 = data[offset + 1] & 0xFF;
        final int b3 = data[offset + 2] & 0xFF;
        final int b4 = data[offset + 3] & 0xFF;
        final int i = b0 << 24 | b2 << 16 | b3 << 8 | b4;
        return i / 65536.0;
    }
    
    static {
        V2_HEADER = new byte[] { 0, 17, 2, -1, 12, 0, -1, -2, 0, 0 };
    }
}
