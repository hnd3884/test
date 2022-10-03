package javax.management;

import java.io.Serializable;

public class Attribute implements Serializable
{
    private static final long serialVersionUID = 2484220110589082382L;
    private String name;
    private Object value;
    
    public Attribute(final String name, final Object value) {
        this.value = null;
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null "));
        }
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Attribute)) {
            return false;
        }
        final Attribute attribute = (Attribute)o;
        if (this.value == null) {
            return attribute.getValue() == null && this.name.equals(attribute.getName());
        }
        return this.name.equals(attribute.getName()) && this.value.equals(attribute.getValue());
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode() ^ ((this.value == null) ? 0 : this.value.hashCode());
    }
    
    @Override
    public String toString() {
        return this.getName() + " = " + this.getValue();
    }
}
