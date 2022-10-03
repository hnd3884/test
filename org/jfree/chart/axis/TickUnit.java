package org.jfree.chart.axis;

import java.io.Serializable;

public abstract class TickUnit implements Comparable, Serializable
{
    private static final long serialVersionUID = 510179855057013974L;
    private double size;
    
    public TickUnit(final double size) {
        this.size = size;
    }
    
    public double getSize() {
        return this.size;
    }
    
    public String valueToString(final double value) {
        return String.valueOf(value);
    }
    
    public int compareTo(final Object object) {
        if (!(object instanceof TickUnit)) {
            return -1;
        }
        final TickUnit other = (TickUnit)object;
        if (this.size > other.getSize()) {
            return 1;
        }
        if (this.size < other.getSize()) {
            return -1;
        }
        return 0;
    }
    
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof TickUnit) {
            final TickUnit tu = (TickUnit)obj;
            return this.size == tu.size;
        }
        return false;
    }
    
    public int hashCode() {
        final long temp = (this.size != 0.0) ? Double.doubleToLongBits(this.size) : 0L;
        return (int)(temp ^ temp >>> 32);
    }
}
