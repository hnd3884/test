package org.jfree.util;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class Rotation implements Serializable
{
    private static final long serialVersionUID = -4662815260201591676L;
    public static final Rotation CLOCKWISE;
    public static final Rotation ANTICLOCKWISE;
    private String name;
    private double factor;
    
    static {
        CLOCKWISE = new Rotation("Rotation.CLOCKWISE", -1.0);
        ANTICLOCKWISE = new Rotation("Rotation.ANTICLOCKWISE", 1.0);
    }
    
    private Rotation(final String name, final double factor) {
        this.name = name;
        this.factor = factor;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rotation)) {
            return false;
        }
        final Rotation rotation = (Rotation)o;
        return this.factor == rotation.factor;
    }
    
    public double getFactor() {
        return this.factor;
    }
    
    public int hashCode() {
        final long temp = Double.doubleToLongBits(this.factor);
        return (int)(temp ^ temp >>> 32);
    }
    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(Rotation.CLOCKWISE)) {
            return Rotation.CLOCKWISE;
        }
        if (this.equals(Rotation.ANTICLOCKWISE)) {
            return Rotation.ANTICLOCKWISE;
        }
        return null;
    }
    
    public String toString() {
        return this.name;
    }
}
