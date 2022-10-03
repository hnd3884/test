package org.jfree.data.xy;

import org.jfree.util.ObjectUtilities;
import java.io.Serializable;

public class XYDataItem implements Cloneable, Comparable, Serializable
{
    private static final long serialVersionUID = 2751513470325494890L;
    private Number x;
    private Number y;
    
    public XYDataItem(final Number x, final Number y) {
        if (x == null) {
            throw new IllegalArgumentException("Null 'x' argument.");
        }
        this.x = x;
        this.y = y;
    }
    
    public XYDataItem(final double x, final double y) {
        this(new Double(x), new Double(y));
    }
    
    public Number getX() {
        return this.x;
    }
    
    public Number getY() {
        return this.y;
    }
    
    public void setY(final double y) {
        this.setY(new Double(y));
    }
    
    public void setY(final Number y) {
        this.y = y;
    }
    
    public int compareTo(final Object o1) {
        int result;
        if (o1 instanceof XYDataItem) {
            final XYDataItem dataItem = (XYDataItem)o1;
            final double compare = this.x.doubleValue() - dataItem.getX().doubleValue();
            if (compare > 0.0) {
                result = 1;
            }
            else if (compare < 0.0) {
                result = -1;
            }
            else {
                result = 0;
            }
        }
        else {
            result = 1;
        }
        return result;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYDataItem)) {
            return false;
        }
        final XYDataItem that = (XYDataItem)obj;
        return this.x.equals(that.x) && ObjectUtilities.equal((Object)this.y, (Object)that.y);
    }
    
    public int hashCode() {
        int result = this.x.hashCode();
        result = 29 * result + ((this.y != null) ? this.y.hashCode() : 0);
        return result;
    }
}
