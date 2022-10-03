package org.jfree.util;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class UnitType implements Serializable
{
    private static final long serialVersionUID = 6531925392288519884L;
    public static final UnitType ABSOLUTE;
    public static final UnitType RELATIVE;
    private String name;
    
    static {
        ABSOLUTE = new UnitType("UnitType.ABSOLUTE");
        RELATIVE = new UnitType("UnitType.RELATIVE");
    }
    
    private UnitType(final String name) {
        this.name = name;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UnitType)) {
            return false;
        }
        final UnitType that = (UnitType)obj;
        return this.name.equals(that.name);
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(UnitType.ABSOLUTE)) {
            return UnitType.ABSOLUTE;
        }
        if (this.equals(UnitType.RELATIVE)) {
            return UnitType.RELATIVE;
        }
        return null;
    }
    
    public String toString() {
        return this.name;
    }
}
