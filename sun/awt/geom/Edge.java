package sun.awt.geom;

final class Edge
{
    static final int INIT_PARTS = 4;
    static final int GROW_PARTS = 10;
    Curve curve;
    int ctag;
    int etag;
    double activey;
    int equivalence;
    private Edge lastEdge;
    private int lastResult;
    private double lastLimit;
    
    public Edge(final Curve curve, final int n) {
        this(curve, n, 0);
    }
    
    public Edge(final Curve curve, final int ctag, final int etag) {
        this.curve = curve;
        this.ctag = ctag;
        this.etag = etag;
    }
    
    public Curve getCurve() {
        return this.curve;
    }
    
    public int getCurveTag() {
        return this.ctag;
    }
    
    public int getEdgeTag() {
        return this.etag;
    }
    
    public void setEdgeTag(final int etag) {
        this.etag = etag;
    }
    
    public int getEquivalence() {
        return this.equivalence;
    }
    
    public void setEquivalence(final int equivalence) {
        this.equivalence = equivalence;
    }
    
    public int compareTo(final Edge lastEdge, final double[] array) {
        if (lastEdge == this.lastEdge && array[0] < this.lastLimit) {
            if (array[1] > this.lastLimit) {
                array[1] = this.lastLimit;
            }
            return this.lastResult;
        }
        if (this == lastEdge.lastEdge && array[0] < lastEdge.lastLimit) {
            if (array[1] > lastEdge.lastLimit) {
                array[1] = lastEdge.lastLimit;
            }
            return 0 - lastEdge.lastResult;
        }
        final int compareTo = this.curve.compareTo(lastEdge.curve, array);
        this.lastEdge = lastEdge;
        this.lastLimit = array[1];
        return this.lastResult = compareTo;
    }
    
    public void record(final double activey, final int etag) {
        this.activey = activey;
        this.etag = etag;
    }
    
    public boolean isActiveFor(final double n, final int n2) {
        return this.etag == n2 && this.activey >= n;
    }
    
    @Override
    public String toString() {
        return "Edge[" + this.curve + ", " + ((this.ctag == 0) ? "L" : "R") + ", " + ((this.etag == 1) ? "I" : ((this.etag == -1) ? "O" : "N")) + "]";
    }
}
