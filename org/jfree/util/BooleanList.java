package org.jfree.util;

public class BooleanList extends AbstractObjectList
{
    private static final long serialVersionUID = -8543170333219422042L;
    
    public boolean equals(final Object o) {
        return o instanceof BooleanList && super.equals(o);
    }
    
    public Boolean getBoolean(final int index) {
        return (Boolean)this.get(index);
    }
    
    public int hashCode() {
        return super.hashCode();
    }
    
    public void setBoolean(final int index, final Boolean b) {
        this.set(index, b);
    }
}
