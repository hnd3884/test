package org.apache.poi.xssf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;

public final class XSSFChildAnchor extends XSSFAnchor
{
    private CTTransform2D t2d;
    
    public XSSFChildAnchor(final int x, final int y, final int cx, final int cy) {
        this.t2d = CTTransform2D.Factory.newInstance();
        final CTPoint2D off = this.t2d.addNewOff();
        final CTPositiveSize2D ext = this.t2d.addNewExt();
        off.setX((long)x);
        off.setY((long)y);
        ext.setCx((long)Math.abs(cx - x));
        ext.setCy((long)Math.abs(cy - y));
        if (x > cx) {
            this.t2d.setFlipH(true);
        }
        if (y > cy) {
            this.t2d.setFlipV(true);
        }
    }
    
    public XSSFChildAnchor(final CTTransform2D t2d) {
        this.t2d = t2d;
    }
    
    @Internal
    public CTTransform2D getCTTransform2D() {
        return this.t2d;
    }
    
    public int getDx1() {
        return (int)this.t2d.getOff().getX();
    }
    
    public void setDx1(final int dx1) {
        this.t2d.getOff().setX((long)dx1);
    }
    
    public int getDy1() {
        return (int)this.t2d.getOff().getY();
    }
    
    public void setDy1(final int dy1) {
        this.t2d.getOff().setY((long)dy1);
    }
    
    public int getDy2() {
        return (int)(this.getDy1() + this.t2d.getExt().getCy());
    }
    
    public void setDy2(final int dy2) {
        this.t2d.getExt().setCy((long)(dy2 - this.getDy1()));
    }
    
    public int getDx2() {
        return (int)(this.getDx1() + this.t2d.getExt().getCx());
    }
    
    public void setDx2(final int dx2) {
        this.t2d.getExt().setCx((long)(dx2 - this.getDx1()));
    }
}
