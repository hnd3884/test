package org.apache.poi.ddf;

public class EscherBoolProperty extends EscherSimpleProperty
{
    public EscherBoolProperty(final short propertyNumber, final int value) {
        super(propertyNumber, value);
    }
    
    public EscherBoolProperty(final EscherPropertyTypes propertyType, final int value) {
        super(propertyType.propNumber, value);
    }
    
    public boolean isTrue() {
        return this.getPropertyValue() != 0;
    }
}
