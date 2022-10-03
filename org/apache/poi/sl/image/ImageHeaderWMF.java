package org.apache.poi.sl.image;

import org.apache.poi.util.POILogFactory;
import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.util.LittleEndian;
import java.awt.Rectangle;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.Internal;

@Internal
public class ImageHeaderWMF
{
    public static final int APMHEADER_KEY = -1698247209;
    private static final POILogger LOG;
    private final int handle;
    private final int left;
    private final int top;
    private final int right;
    private final int bottom;
    private final int inch;
    private final int reserved;
    private int checksum;
    
    public ImageHeaderWMF(final Rectangle dim) {
        this.handle = 0;
        this.left = dim.x;
        this.top = dim.y;
        this.right = dim.x + dim.width;
        this.bottom = dim.y + dim.height;
        this.inch = 72;
        this.reserved = 0;
    }
    
    public ImageHeaderWMF(final byte[] data, final int off) {
        int offset = off;
        final int key = LittleEndian.getInt(data, offset);
        offset += 4;
        if (key != -1698247209) {
            ImageHeaderWMF.LOG.log(5, "WMF file doesn't contain a placeable header - ignore parsing");
            this.handle = 0;
            this.left = 0;
            this.top = 0;
            this.right = 200;
            this.bottom = 200;
            this.inch = 72;
            this.reserved = 0;
            return;
        }
        this.handle = LittleEndian.getUShort(data, offset);
        offset += 2;
        this.left = LittleEndian.getShort(data, offset);
        offset += 2;
        this.top = LittleEndian.getShort(data, offset);
        offset += 2;
        this.right = LittleEndian.getShort(data, offset);
        offset += 2;
        this.bottom = LittleEndian.getShort(data, offset);
        offset += 2;
        this.inch = LittleEndian.getUShort(data, offset);
        offset += 2;
        this.reserved = LittleEndian.getInt(data, offset);
        offset += 4;
        this.checksum = LittleEndian.getShort(data, offset);
        offset += 2;
        if (this.checksum != this.getChecksum()) {
            ImageHeaderWMF.LOG.log(5, "WMF checksum does not match the header data");
        }
    }
    
    public int getChecksum() {
        int cs = 0;
        cs ^= 0xCDD7;
        cs ^= 0xFFFF9AC6;
        cs ^= this.left;
        cs ^= this.top;
        cs ^= this.right;
        cs ^= this.bottom;
        cs ^= this.inch;
        return cs;
    }
    
    public void write(final OutputStream out) throws IOException {
        final byte[] header = new byte[22];
        int pos = 0;
        LittleEndian.putInt(header, pos, -1698247209);
        pos += 4;
        LittleEndian.putUShort(header, pos, 0);
        pos += 2;
        LittleEndian.putUShort(header, pos, this.left);
        pos += 2;
        LittleEndian.putUShort(header, pos, this.top);
        pos += 2;
        LittleEndian.putUShort(header, pos, this.right);
        pos += 2;
        LittleEndian.putUShort(header, pos, this.bottom);
        pos += 2;
        LittleEndian.putUShort(header, pos, this.inch);
        pos += 2;
        LittleEndian.putInt(header, pos, 0);
        pos += 4;
        LittleEndian.putUShort(header, pos, this.checksum = this.getChecksum());
        out.write(header);
    }
    
    public Dimension getSize() {
        final double coeff = 72.0 / this.inch;
        return new Dimension((int)Math.round((this.right - this.left) * coeff), (int)Math.round((this.bottom - this.top) * coeff));
    }
    
    public Rectangle getBounds() {
        return new Rectangle(this.left, this.top, this.right - this.left, this.bottom - this.top);
    }
    
    public int getLength() {
        return 22;
    }
    
    static {
        LOG = POILogFactory.getLogger(ImageHeaderWMF.class);
    }
}
