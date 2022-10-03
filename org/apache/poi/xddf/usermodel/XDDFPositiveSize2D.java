package org.apache.poi.xddf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;

public class XDDFPositiveSize2D
{
    private CTPositiveSize2D size;
    private long x;
    private long y;
    
    protected XDDFPositiveSize2D(final CTPositiveSize2D size) {
        this.size = size;
    }
    
    public XDDFPositiveSize2D(final long x, final long y) {
        if (x < 0L || y < 0L) {
            throw new IllegalArgumentException("x and y must be positive");
        }
        this.x = x;
        this.y = y;
    }
    
    public long getX() {
        if (this.size == null) {
            return this.x;
        }
        return this.size.getCx();
    }
    
    public long getY() {
        if (this.size == null) {
            return this.y;
        }
        return this.size.getCy();
    }
}
