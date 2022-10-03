package java.awt;

import java.io.Serializable;

public class Insets implements Cloneable, Serializable
{
    public int top;
    public int left;
    public int bottom;
    public int right;
    private static final long serialVersionUID = -2272572637695466749L;
    
    public Insets(final int top, final int left, final int bottom, final int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }
    
    public void set(final int top, final int left, final int bottom, final int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Insets) {
            final Insets insets = (Insets)o;
            return this.top == insets.top && this.left == insets.left && this.bottom == insets.bottom && this.right == insets.right;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final int n = this.left + this.bottom;
        final int n2 = this.right + this.top;
        final int n3 = n * (n + 1) / 2 + this.left;
        final int n4 = n2 * (n2 + 1) / 2 + this.top;
        final int n5 = n3 + n4;
        return n5 * (n5 + 1) / 2 + n4;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[top=" + this.top + ",left=" + this.left + ",bottom=" + this.bottom + ",right=" + this.right + "]";
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    private static native void initIDs();
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
    }
}
