package com.adventnet.persistence.internal;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.Serializable;

public class UniqueValueHolder implements Serializable, Cloneable, Comparable
{
    private static final long serialVersionUID = -5791770358146344773L;
    private static final Logger OUT;
    Object value;
    String generatorName;
    boolean generated;
    String tableName;
    
    public Object getValue() {
        return this.value;
    }
    
    public void setValue(final Object v) {
        this.value = v;
    }
    
    public String getGeneratorName() {
        return this.generatorName;
    }
    
    public void setGeneratorName(final String genName) {
        this.generatorName = genName;
    }
    
    @Override
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        if (this.value != null) {
            buff.append("[").append(this.value).append("]UVH@").append(super.hashCode());
        }
        else {
            buff.append("UVH@").append(super.hashCode());
        }
        return buff.toString();
    }
    
    public boolean isGenerated() {
        return this.generated;
    }
    
    public void setGenerated(final boolean flag) {
        this.generated = flag;
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void setTableName(final String name) {
        this.tableName = name;
    }
    
    public Object clone() {
        Object newUvh = null;
        try {
            newUvh = super.clone();
        }
        catch (final CloneNotSupportedException cnse) {
            UniqueValueHolder.OUT.log(Level.FINE, "Exception {0} occured while cloning UniqueValueHolder {1}", new Object[] { cnse.getMessage(), this });
        }
        return newUvh;
    }
    
    @Override
    public int compareTo(final Object o) {
        return (o instanceof UniqueValueHolder && o.equals(this)) ? 0 : -1;
    }
    
    static {
        OUT = Logger.getLogger(UniqueValueHolder.class.getName());
    }
}
