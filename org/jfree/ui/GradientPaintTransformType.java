package org.jfree.ui;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class GradientPaintTransformType implements Serializable
{
    private static final long serialVersionUID = 8331561784933982450L;
    public static final GradientPaintTransformType VERTICAL;
    public static final GradientPaintTransformType HORIZONTAL;
    public static final GradientPaintTransformType CENTER_VERTICAL;
    public static final GradientPaintTransformType CENTER_HORIZONTAL;
    private String name;
    
    static {
        VERTICAL = new GradientPaintTransformType("GradientPaintTransformType.VERTICAL");
        HORIZONTAL = new GradientPaintTransformType("GradientPaintTransformType.HORIZONTAL");
        CENTER_VERTICAL = new GradientPaintTransformType("GradientPaintTransformType.CENTER_VERTICAL");
        CENTER_HORIZONTAL = new GradientPaintTransformType("GradientPaintTransformType.CENTER_HORIZONTAL");
    }
    
    private GradientPaintTransformType(final String name) {
        this.name = name;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GradientPaintTransformType)) {
            return false;
        }
        final GradientPaintTransformType t = (GradientPaintTransformType)o;
        return this.name.equals(t.name);
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    private Object readResolve() throws ObjectStreamException {
        GradientPaintTransformType result = null;
        if (this.equals(GradientPaintTransformType.HORIZONTAL)) {
            result = GradientPaintTransformType.HORIZONTAL;
        }
        else if (this.equals(GradientPaintTransformType.VERTICAL)) {
            result = GradientPaintTransformType.VERTICAL;
        }
        else if (this.equals(GradientPaintTransformType.CENTER_HORIZONTAL)) {
            result = GradientPaintTransformType.CENTER_HORIZONTAL;
        }
        else if (this.equals(GradientPaintTransformType.CENTER_VERTICAL)) {
            result = GradientPaintTransformType.CENTER_VERTICAL;
        }
        return result;
    }
    
    public String toString() {
        return this.name;
    }
}
