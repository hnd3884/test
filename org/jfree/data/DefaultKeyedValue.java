package org.jfree.data;

import java.io.Serializable;
import org.jfree.util.PublicCloneable;

public class DefaultKeyedValue implements KeyedValue, Cloneable, PublicCloneable, Serializable
{
    private static final long serialVersionUID = -7388924517460437712L;
    private Comparable key;
    private Number value;
    
    public DefaultKeyedValue(final Comparable key, final Number value) {
        this.key = key;
        this.value = value;
    }
    
    public Comparable getKey() {
        return this.key;
    }
    
    public Number getValue() {
        return this.value;
    }
    
    public synchronized void setValue(final Number value) {
        this.value = value;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultKeyedValue)) {
            return false;
        }
        final DefaultKeyedValue that = (DefaultKeyedValue)obj;
        Label_0054: {
            if (this.key != null) {
                if (this.key.equals(that.key)) {
                    break Label_0054;
                }
            }
            else if (that.key == null) {
                break Label_0054;
            }
            return false;
        }
        if (this.value != null) {
            if (this.value.equals(that.value)) {
                return true;
            }
        }
        else if (that.value == null) {
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        int result = (this.key != null) ? this.key.hashCode() : 0;
        result = 29 * result + ((this.value != null) ? this.value.hashCode() : 0);
        return result;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final DefaultKeyedValue clone = (DefaultKeyedValue)super.clone();
        return clone;
    }
}
