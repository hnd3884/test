package sun.awt.geom;

import java.awt.geom.Rectangle2D;

final class Order0 extends Curve
{
    private double x;
    private double y;
    
    public Order0(final double x, final double y) {
        super(1);
        this.x = x;
        this.y = y;
    }
    
    @Override
    public int getOrder() {
        return 0;
    }
    
    @Override
    public double getXTop() {
        return this.x;
    }
    
    @Override
    public double getYTop() {
        return this.y;
    }
    
    @Override
    public double getXBot() {
        return this.x;
    }
    
    @Override
    public double getYBot() {
        return this.y;
    }
    
    @Override
    public double getXMin() {
        return this.x;
    }
    
    @Override
    public double getXMax() {
        return this.x;
    }
    
    @Override
    public double getX0() {
        return this.x;
    }
    
    @Override
    public double getY0() {
        return this.y;
    }
    
    @Override
    public double getX1() {
        return this.x;
    }
    
    @Override
    public double getY1() {
        return this.y;
    }
    
    @Override
    public double XforY(final double n) {
        return n;
    }
    
    @Override
    public double TforY(final double n) {
        return 0.0;
    }
    
    @Override
    public double XforT(final double n) {
        return this.x;
    }
    
    @Override
    public double YforT(final double n) {
        return this.y;
    }
    
    @Override
    public double dXforT(final double n, final int n2) {
        return 0.0;
    }
    
    @Override
    public double dYforT(final double n, final int n2) {
        return 0.0;
    }
    
    @Override
    public double nextVertical(final double n, final double n2) {
        return n2;
    }
    
    @Override
    public int crossingsFor(final double n, final double n2) {
        return 0;
    }
    
    @Override
    public boolean accumulateCrossings(final Crossings crossings) {
        return this.x > crossings.getXLo() && this.x < crossings.getXHi() && this.y > crossings.getYLo() && this.y < crossings.getYHi();
    }
    
    @Override
    public void enlarge(final Rectangle2D rectangle2D) {
        rectangle2D.add(this.x, this.y);
    }
    
    @Override
    public Curve getSubCurve(final double n, final double n2, final int n3) {
        return this;
    }
    
    @Override
    public Curve getReversedCurve() {
        return this;
    }
    
    @Override
    public int getSegment(final double[] array) {
        array[0] = this.x;
        array[1] = this.y;
        return 0;
    }
}
