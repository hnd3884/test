package java.awt;

import java.beans.Transient;
import java.io.Serializable;
import java.awt.geom.Dimension2D;

public class Dimension extends Dimension2D implements Serializable
{
    public int width;
    public int height;
    private static final long serialVersionUID = 4723952579491349524L;
    
    private static native void initIDs();
    
    public Dimension() {
        this(0, 0);
    }
    
    public Dimension(final Dimension dimension) {
        this(dimension.width, dimension.height);
    }
    
    public Dimension(final int width, final int height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double getWidth() {
        return this.width;
    }
    
    @Override
    public double getHeight() {
        return this.height;
    }
    
    @Override
    public void setSize(final double n, final double n2) {
        this.width = (int)Math.ceil(n);
        this.height = (int)Math.ceil(n2);
    }
    
    @Transient
    public Dimension getSize() {
        return new Dimension(this.width, this.height);
    }
    
    public void setSize(final Dimension dimension) {
        this.setSize(dimension.width, dimension.height);
    }
    
    public void setSize(final int width, final int height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Dimension) {
            final Dimension dimension = (Dimension)o;
            return this.width == dimension.width && this.height == dimension.height;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final int n = this.width + this.height;
        return n * (n + 1) / 2 + this.width;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[width=" + this.width + ",height=" + this.height + "]";
    }
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
    }
}
