package org.jfree.chart.axis;

import org.jfree.ui.TextAnchor;

public abstract class ValueTick extends Tick
{
    private double value;
    
    public ValueTick(final double value, final String label, final TextAnchor textAnchor, final TextAnchor rotationAnchor, final double angle) {
        super(label, textAnchor, rotationAnchor, angle);
        this.value = value;
    }
    
    public double getValue() {
        return this.value;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ValueTick && super.equals(obj)) {
            final ValueTick vt = (ValueTick)obj;
            return this.value == vt.value;
        }
        return false;
    }
}
