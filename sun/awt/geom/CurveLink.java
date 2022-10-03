package sun.awt.geom;

final class CurveLink
{
    Curve curve;
    double ytop;
    double ybot;
    int etag;
    CurveLink next;
    
    public CurveLink(final Curve curve, final double ytop, final double ybot, final int etag) {
        this.curve = curve;
        this.ytop = ytop;
        this.ybot = ybot;
        this.etag = etag;
        if (this.ytop < curve.getYTop() || this.ybot > curve.getYBot()) {
            throw new InternalError("bad curvelink [" + this.ytop + "=>" + this.ybot + "] for " + curve);
        }
    }
    
    public boolean absorb(final CurveLink curveLink) {
        return this.absorb(curveLink.curve, curveLink.ytop, curveLink.ybot, curveLink.etag);
    }
    
    public boolean absorb(final Curve curve, final double n, final double n2, final int n3) {
        if (this.curve != curve || this.etag != n3 || this.ybot < n || this.ytop > n2) {
            return false;
        }
        if (n < curve.getYTop() || n2 > curve.getYBot()) {
            throw new InternalError("bad curvelink [" + n + "=>" + n2 + "] for " + curve);
        }
        this.ytop = Math.min(this.ytop, n);
        this.ybot = Math.max(this.ybot, n2);
        return true;
    }
    
    public boolean isEmpty() {
        return this.ytop == this.ybot;
    }
    
    public Curve getCurve() {
        return this.curve;
    }
    
    public Curve getSubCurve() {
        if (this.ytop == this.curve.getYTop() && this.ybot == this.curve.getYBot()) {
            return this.curve.getWithDirection(this.etag);
        }
        return this.curve.getSubCurve(this.ytop, this.ybot, this.etag);
    }
    
    public Curve getMoveto() {
        return new Order0(this.getXTop(), this.getYTop());
    }
    
    public double getXTop() {
        return this.curve.XforY(this.ytop);
    }
    
    public double getYTop() {
        return this.ytop;
    }
    
    public double getXBot() {
        return this.curve.XforY(this.ybot);
    }
    
    public double getYBot() {
        return this.ybot;
    }
    
    public double getX() {
        return this.curve.XforY(this.ytop);
    }
    
    public int getEdgeTag() {
        return this.etag;
    }
    
    public void setNext(final CurveLink next) {
        this.next = next;
    }
    
    public CurveLink getNext() {
        return this.next;
    }
}
