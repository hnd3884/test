package org.jfree.ui;

import java.io.Serializable;
import java.awt.geom.Dimension2D;

public class FloatDimension extends Dimension2D implements Serializable
{
    private static final long serialVersionUID = 5367882923248086744L;
    private float width;
    private float height;
    
    public FloatDimension() {
        this.width = 0.0f;
        this.height = 0.0f;
    }
    
    public FloatDimension(final float width, final float height) {
        this.width = width;
        this.height = height;
    }
    
    public FloatDimension(final FloatDimension fd) {
        this.width = fd.width;
        this.height = fd.height;
    }
    
    public Object clone() {
        return super.clone();
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FloatDimension)) {
            return false;
        }
        final FloatDimension floatDimension = (FloatDimension)o;
        return this.height == floatDimension.height && this.width == floatDimension.width;
    }
    
    public double getHeight() {
        return this.height;
    }
    
    public double getWidth() {
        return this.width;
    }
    
    public int hashCode() {
        int result = Float.floatToIntBits(this.width);
        result = 29 * result + Float.floatToIntBits(this.height);
        return result;
    }
    
    public void setHeight(final double height) {
        this.height = (float)height;
    }
    
    public void setSize(final double width, final double height) {
        this.setHeight((float)height);
        this.setWidth((float)width);
    }
    
    public void setWidth(final double width) {
        this.width = (float)width;
    }
    
    public String toString() {
        return String.valueOf(this.getClass().getName()) + ":={width=" + this.getWidth() + ", height=" + this.getHeight() + "}";
    }
}
