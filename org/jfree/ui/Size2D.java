package org.jfree.ui;

import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class Size2D implements Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = 2558191683786418168L;
    public double width;
    public double height;
    
    public Size2D() {
        this(0.0, 0.0);
    }
    
    public Size2D(final double width, final double height) {
        this.width = width;
        this.height = height;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Size2D)) {
            return false;
        }
        final Size2D that = (Size2D)obj;
        return this.width == that.width && this.height == that.height;
    }
    
    public double getHeight() {
        return this.height;
    }
    
    public double getWidth() {
        return this.width;
    }
    
    public void setHeight(final double height) {
        this.height = height;
    }
    
    public void setWidth(final double width) {
        this.width = width;
    }
    
    public String toString() {
        return "Size2D[width=" + this.width + ", height=" + this.height + "]";
    }
}
