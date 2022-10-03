package org.jfree.chart.axis;

import java.text.NumberFormat;
import java.io.Serializable;

public class NumberTickUnit extends TickUnit implements Serializable
{
    private static final long serialVersionUID = 3849459506627654442L;
    private NumberFormat formatter;
    
    public NumberTickUnit(final double size) {
        this(size, NumberFormat.getNumberInstance());
    }
    
    public NumberTickUnit(final double size, final NumberFormat formatter) {
        super(size);
        if (formatter == null) {
            throw new IllegalArgumentException("Null 'formatter' argument.");
        }
        this.formatter = formatter;
    }
    
    public String valueToString(final double value) {
        return this.formatter.format(value);
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NumberTickUnit)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final NumberTickUnit that = (NumberTickUnit)obj;
        return this.formatter.equals(that.formatter);
    }
    
    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + ((this.formatter != null) ? this.formatter.hashCode() : 0);
        return result;
    }
}
