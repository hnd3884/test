package javax.print.attribute;

import java.io.Serializable;

public abstract class Size2DSyntax implements Serializable, Cloneable
{
    private static final long serialVersionUID = 5584439964938660530L;
    private int x;
    private int y;
    public static final int INCH = 25400;
    public static final int MM = 1000;
    
    protected Size2DSyntax(final float n, final float n2, final int n3) {
        if (n < 0.0f) {
            throw new IllegalArgumentException("x < 0");
        }
        if (n2 < 0.0f) {
            throw new IllegalArgumentException("y < 0");
        }
        if (n3 < 1) {
            throw new IllegalArgumentException("units < 1");
        }
        this.x = (int)(n * n3 + 0.5f);
        this.y = (int)(n2 * n3 + 0.5f);
    }
    
    protected Size2DSyntax(final int n, final int n2, final int n3) {
        if (n < 0) {
            throw new IllegalArgumentException("x < 0");
        }
        if (n2 < 0) {
            throw new IllegalArgumentException("y < 0");
        }
        if (n3 < 1) {
            throw new IllegalArgumentException("units < 1");
        }
        this.x = n * n3;
        this.y = n2 * n3;
    }
    
    private static float convertFromMicrometers(final int n, final int n2) {
        if (n2 < 1) {
            throw new IllegalArgumentException("units is < 1");
        }
        return n / (float)n2;
    }
    
    public float[] getSize(final int n) {
        return new float[] { this.getX(n), this.getY(n) };
    }
    
    public float getX(final int n) {
        return convertFromMicrometers(this.x, n);
    }
    
    public float getY(final int n) {
        return convertFromMicrometers(this.y, n);
    }
    
    public String toString(final int n, final String s) {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getX(n));
        sb.append('x');
        sb.append(this.getY(n));
        if (s != null) {
            sb.append(' ');
            sb.append(s);
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof Size2DSyntax && this.x == ((Size2DSyntax)o).x && this.y == ((Size2DSyntax)o).y;
    }
    
    @Override
    public int hashCode() {
        return (this.x & 0xFFFF) | (this.y & 0xFFFF) << 16;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.x);
        sb.append('x');
        sb.append(this.y);
        sb.append(" um");
        return sb.toString();
    }
    
    protected int getXMicrometers() {
        return this.x;
    }
    
    protected int getYMicrometers() {
        return this.y;
    }
}
