package org.apache.poi.hpsf;

import java.util.Objects;

public class CustomProperty extends Property
{
    private String name;
    
    public CustomProperty() {
        this.name = null;
    }
    
    public CustomProperty(final Property property) {
        this(property, null);
    }
    
    public CustomProperty(final Property property, final String name) {
        super(property);
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public boolean equalsContents(final Object o) {
        final CustomProperty c = (CustomProperty)o;
        final String name1 = c.getName();
        final String name2 = this.getName();
        boolean equalNames = true;
        if (name1 == null) {
            equalNames = (name2 == null);
        }
        else {
            equalNames = name1.equals(name2);
        }
        return equalNames && c.getID() == this.getID() && c.getType() == this.getType() && c.getValue().equals(this.getValue());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.getID());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof CustomProperty && this.equalsContents(o);
    }
}
