package com.sun.org.apache.xalan.internal.utils;

public final class XMLSecurityPropertyManager extends FeaturePropertyBase
{
    public XMLSecurityPropertyManager() {
        this.values = new String[Property.values().length];
        for (final Property property : Property.values()) {
            this.values[property.ordinal()] = property.defaultValue();
        }
        this.readSystemProperties();
    }
    
    @Override
    public int getIndex(final String propertyName) {
        for (final Property property : Property.values()) {
            if (property.equalsName(propertyName)) {
                return property.ordinal();
            }
        }
        return -1;
    }
    
    private void readSystemProperties() {
        this.getSystemProperty(Property.ACCESS_EXTERNAL_DTD, "javax.xml.accessExternalDTD");
        this.getSystemProperty(Property.ACCESS_EXTERNAL_STYLESHEET, "javax.xml.accessExternalStylesheet");
    }
    
    public enum Property
    {
        ACCESS_EXTERNAL_DTD("http://javax.xml.XMLConstants/property/accessExternalDTD", "all"), 
        ACCESS_EXTERNAL_STYLESHEET("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "all");
        
        final String name;
        final String defaultValue;
        
        private Property(final String name, final String value) {
            this.name = name;
            this.defaultValue = value;
        }
        
        public boolean equalsName(final String propertyName) {
            return propertyName != null && this.name.equals(propertyName);
        }
        
        String defaultValue() {
            return this.defaultValue;
        }
    }
}
