package org.apache.poi.sl.usermodel;

public final class Insets2D implements Cloneable
{
    public double top;
    public double left;
    public double bottom;
    public double right;
    
    public Insets2D(final double top, final double left, final double bottom, final double right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }
    
    public void set(final double top, final double left, final double bottom, final double right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Insets2D) {
            final Insets2D insets = (Insets2D)obj;
            return this.top == insets.top && this.left == insets.left && this.bottom == insets.bottom && this.right == insets.right;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final double sum1 = this.left + this.bottom;
        final double sum2 = this.right + this.top;
        final double val1 = sum1 * (sum1 + 1.0) / 2.0 + this.left;
        final double val2 = sum2 * (sum2 + 1.0) / 2.0 + this.top;
        final double sum3 = val1 + val2;
        return (int)(sum3 * (sum3 + 1.0) / 2.0 + val2);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[top=" + this.top + ",left=" + this.left + ",bottom=" + this.bottom + ",right=" + this.right + "]";
    }
    
    public Insets2D clone() {
        return new Insets2D(this.top, this.left, this.bottom, this.right);
    }
}
