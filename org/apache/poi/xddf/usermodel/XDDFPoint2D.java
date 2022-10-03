package org.apache.poi.xddf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;

public class XDDFPoint2D
{
    private CTPoint2D point;
    private long x;
    private long y;
    
    protected XDDFPoint2D(final CTPoint2D point) {
        this.point = point;
    }
    
    public XDDFPoint2D(final long x, final long y) {
        this.x = x;
        this.y = y;
    }
    
    public long getX() {
        if (this.point == null) {
            return this.x;
        }
        return this.point.getX();
    }
    
    public long getY() {
        if (this.point == null) {
            return this.y;
        }
        return this.point.getY();
    }
}
