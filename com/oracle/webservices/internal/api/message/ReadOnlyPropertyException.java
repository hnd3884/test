package com.oracle.webservices.internal.api.message;

public class ReadOnlyPropertyException extends IllegalArgumentException
{
    private final String propertyName;
    
    public ReadOnlyPropertyException(final String propertyName) {
        super(propertyName + " is a read-only property.");
        this.propertyName = propertyName;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
}
