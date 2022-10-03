package org.jfree.ui;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class LengthAdjustmentType implements Serializable
{
    private static final long serialVersionUID = -6097408511380545010L;
    public static final LengthAdjustmentType NO_CHANGE;
    public static final LengthAdjustmentType EXPAND;
    public static final LengthAdjustmentType CONTRACT;
    private String name;
    
    static {
        NO_CHANGE = new LengthAdjustmentType("NO_CHANGE");
        EXPAND = new LengthAdjustmentType("EXPAND");
        CONTRACT = new LengthAdjustmentType("CONTRACT");
    }
    
    private LengthAdjustmentType(final String name) {
        this.name = name;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LengthAdjustmentType)) {
            return false;
        }
        final LengthAdjustmentType that = (LengthAdjustmentType)obj;
        return this.name.equals(that.name);
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    private Object readResolve() throws ObjectStreamException {
        if (this.equals(LengthAdjustmentType.NO_CHANGE)) {
            return LengthAdjustmentType.NO_CHANGE;
        }
        if (this.equals(LengthAdjustmentType.EXPAND)) {
            return LengthAdjustmentType.EXPAND;
        }
        if (this.equals(LengthAdjustmentType.CONTRACT)) {
            return LengthAdjustmentType.CONTRACT;
        }
        return null;
    }
    
    public String toString() {
        return this.name;
    }
}
