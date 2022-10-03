package org.apache.tika.metadata;

public final class PropertyTypeException extends IllegalArgumentException
{
    public PropertyTypeException(final String msg) {
        super(msg);
    }
    
    public PropertyTypeException(final Property.PropertyType expected, final Property.PropertyType found) {
        super("Expected a property of type " + expected + ", but received " + found);
    }
    
    public PropertyTypeException(final Property.ValueType expected, final Property.ValueType found) {
        super("Expected a property with a " + expected + " value, but received a " + found);
    }
    
    public PropertyTypeException(final Property.PropertyType unsupportedPropertyType) {
        super((unsupportedPropertyType != Property.PropertyType.COMPOSITE) ? (unsupportedPropertyType + " is not supported") : "Composite Properties must not include other Composite Properties as either Primary or Secondary");
    }
}
