package com.sun.corba.se.impl.orb;

import java.util.Properties;

public class NormalDataCollector extends DataCollectorBase
{
    private String[] args;
    
    public NormalDataCollector(final String[] args, final Properties properties, final String s, final String s2) {
        super(properties, s, s2);
        this.args = args;
    }
    
    @Override
    public boolean isApplet() {
        return false;
    }
    
    @Override
    protected void collect() {
        this.checkPropertyDefaults();
        this.findPropertiesFromFile();
        this.findPropertiesFromSystem();
        this.findPropertiesFromProperties();
        this.findPropertiesFromArgs(this.args);
    }
}
