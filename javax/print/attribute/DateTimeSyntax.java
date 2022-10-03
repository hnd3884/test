package javax.print.attribute;

import java.util.Date;
import java.io.Serializable;

public abstract class DateTimeSyntax implements Serializable, Cloneable
{
    private static final long serialVersionUID = -1400819079791208582L;
    private Date value;
    
    protected DateTimeSyntax(final Date value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        this.value = value;
    }
    
    public Date getValue() {
        return new Date(this.value.getTime());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof DateTimeSyntax && this.value.equals(((DateTimeSyntax)o).value);
    }
    
    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
    
    @Override
    public String toString() {
        return "" + this.value;
    }
}
