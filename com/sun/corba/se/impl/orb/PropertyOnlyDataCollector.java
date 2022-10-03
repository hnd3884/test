package com.sun.corba.se.impl.orb;

import java.util.Properties;

public class PropertyOnlyDataCollector extends DataCollectorBase
{
    public PropertyOnlyDataCollector(final Properties properties, final String s, final String s2) {
        super(properties, s, s2);
    }
    
    @Override
    public boolean isApplet() {
        return false;
    }
    
    @Override
    protected void collect() {
        this.checkPropertyDefaults();
        this.findPropertiesFromProperties();
    }
}
