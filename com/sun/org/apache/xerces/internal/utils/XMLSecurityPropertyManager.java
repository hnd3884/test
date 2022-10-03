package com.sun.org.apache.xerces.internal.utils;

public final class XMLSecurityPropertyManager
{
    private final String[] values;
    private State[] states;
    
    public XMLSecurityPropertyManager() {
        this.states = new State[] { State.DEFAULT, State.DEFAULT };
        this.values = new String[Property.values().length];
        for (final Property property : Property.values()) {
            this.values[property.ordinal()] = property.defaultValue();
        }
        this.readSystemProperties();
    }
    
    public boolean setValue(final String propertyName, final State state, final Object value) {
        final int index = this.getIndex(propertyName);
        if (index > -1) {
            this.setValue(index, state, (String)value);
            return true;
        }
        return false;
    }
    
    public void setValue(final Property property, final State state, final String value) {
        if (state.compareTo(this.states[property.ordinal()]) >= 0) {
            this.values[property.ordinal()] = value;
            this.states[property.ordinal()] = state;
        }
    }
    
    public void setValue(final int index, final State state, final String value) {
        if (state.compareTo(this.states[index]) >= 0) {
            this.values[index] = value;
            this.states[index] = state;
        }
    }
    
    public String getValue(final String propertyName) {
        final int index = this.getIndex(propertyName);
        if (index > -1) {
            return this.getValueByIndex(index);
        }
        return null;
    }
    
    public String getValue(final Property property) {
        return this.values[property.ordinal()];
    }
    
    public String getValueByIndex(final int index) {
        return this.values[index];
    }
    
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
        this.getSystemProperty(Property.ACCESS_EXTERNAL_SCHEMA, "javax.xml.accessExternalSchema");
    }
    
    private void getSystemProperty(final Property property, final String systemProperty) {
        try {
            String value = SecuritySupport.getSystemProperty(systemProperty);
            if (value != null) {
                this.values[property.ordinal()] = value;
                this.states[property.ordinal()] = State.SYSTEMPROPERTY;
                return;
            }
            value = SecuritySupport.readJAXPProperty(systemProperty);
            if (value != null) {
                this.values[property.ordinal()] = value;
                this.states[property.ordinal()] = State.JAXPDOTPROPERTIES;
            }
        }
        catch (final NumberFormatException ex) {}
    }
    
    public enum State
    {
        DEFAULT, 
        FSP, 
        JAXPDOTPROPERTIES, 
        SYSTEMPROPERTY, 
        APIPROPERTY;
    }
    
    public enum Property
    {
        ACCESS_EXTERNAL_DTD("http://javax.xml.XMLConstants/property/accessExternalDTD", "all"), 
        ACCESS_EXTERNAL_SCHEMA("http://javax.xml.XMLConstants/property/accessExternalSchema", "all");
        
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
