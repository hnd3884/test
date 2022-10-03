package com.sun.corba.se.impl.orb;

import java.util.Properties;
import java.applet.Applet;

public class AppletDataCollector extends DataCollectorBase
{
    private Applet applet;
    
    AppletDataCollector(final Applet applet, final Properties properties, final String s, final String s2) {
        super(properties, s, s2);
        this.applet = applet;
    }
    
    @Override
    public boolean isApplet() {
        return true;
    }
    
    @Override
    protected void collect() {
        this.checkPropertyDefaults();
        this.findPropertiesFromFile();
        this.findPropertiesFromProperties();
        this.findPropertiesFromApplet(this.applet);
    }
}
