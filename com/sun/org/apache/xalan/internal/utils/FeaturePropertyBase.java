package com.sun.org.apache.xalan.internal.utils;

public abstract class FeaturePropertyBase
{
    String[] values;
    State[] states;
    
    public FeaturePropertyBase() {
        this.values = null;
        this.states = new State[] { State.DEFAULT, State.DEFAULT };
    }
    
    public void setValue(final Enum property, final State state, final String value) {
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
    
    public boolean setValue(final String propertyName, final State state, final Object value) {
        final int index = this.getIndex(propertyName);
        if (index > -1) {
            this.setValue(index, state, (String)value);
            return true;
        }
        return false;
    }
    
    public boolean setValue(final String propertyName, final State state, final boolean value) {
        final int index = this.getIndex(propertyName);
        if (index > -1) {
            if (value) {
                this.setValue(index, state, "true");
            }
            else {
                this.setValue(index, state, "false");
            }
            return true;
        }
        return false;
    }
    
    public String getValue(final Enum property) {
        return this.values[property.ordinal()];
    }
    
    public String getValue(final String property) {
        final int index = this.getIndex(property);
        if (index > -1) {
            return this.getValueByIndex(index);
        }
        return null;
    }
    
    public String getValueAsString(final String propertyName) {
        final int index = this.getIndex(propertyName);
        if (index > -1) {
            return this.getValueByIndex(index);
        }
        return null;
    }
    
    public String getValueByIndex(final int index) {
        return this.values[index];
    }
    
    public abstract int getIndex(final String p0);
    
    public <E extends Enum<E>> int getIndex(final Class<E> property, final String propertyName) {
        for (final Enum<E> enumItem : property.getEnumConstants()) {
            if (enumItem.toString().equals(propertyName)) {
                return enumItem.ordinal();
            }
        }
        return -1;
    }
    
    void getSystemProperty(final Enum property, final String systemProperty) {
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
}
