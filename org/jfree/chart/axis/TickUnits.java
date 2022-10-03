package org.jfree.chart.axis;

import java.util.Collection;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class TickUnits implements TickUnitSource, Cloneable, Serializable
{
    private static final long serialVersionUID = 1134174035901467545L;
    private List tickUnits;
    
    public TickUnits() {
        this.tickUnits = new ArrayList();
    }
    
    public void add(final TickUnit unit) {
        if (unit == null) {
            throw new NullPointerException("Null 'unit' argument.");
        }
        this.tickUnits.add(unit);
        Collections.sort((List<Comparable>)this.tickUnits);
    }
    
    public int size() {
        return this.tickUnits.size();
    }
    
    public TickUnit get(final int pos) {
        return this.tickUnits.get(pos);
    }
    
    public TickUnit getLargerTickUnit(final TickUnit unit) {
        int index = Collections.binarySearch(this.tickUnits, unit);
        if (index >= 0) {
            ++index;
        }
        else {
            index = -index;
        }
        return this.tickUnits.get(Math.min(index, this.tickUnits.size() - 1));
    }
    
    public TickUnit getCeilingTickUnit(final TickUnit unit) {
        int index = Collections.binarySearch(this.tickUnits, unit);
        if (index >= 0) {
            return this.tickUnits.get(index);
        }
        index = -(index + 1);
        return this.tickUnits.get(Math.min(index, this.tickUnits.size() - 1));
    }
    
    public TickUnit getCeilingTickUnit(final double size) {
        return this.getCeilingTickUnit(new NumberTickUnit(size, NumberFormat.getInstance()));
    }
    
    public Object clone() throws CloneNotSupportedException {
        final TickUnits clone = (TickUnits)super.clone();
        clone.tickUnits = new ArrayList(this.tickUnits);
        return clone;
    }
    
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof TickUnits) {
            final TickUnits tu = (TickUnits)object;
            return tu.tickUnits.equals(this.tickUnits);
        }
        return false;
    }
}
