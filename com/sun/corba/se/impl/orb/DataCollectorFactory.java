package com.sun.corba.se.impl.orb;

import java.net.URL;
import com.sun.corba.se.spi.orb.DataCollector;
import java.util.Properties;
import java.applet.Applet;

public abstract class DataCollectorFactory
{
    private DataCollectorFactory() {
    }
    
    public static DataCollector create(final Applet applet, final Properties properties, final String s) {
        String host = s;
        if (applet != null) {
            final URL codeBase = applet.getCodeBase();
            if (codeBase != null) {
                host = codeBase.getHost();
            }
        }
        return new AppletDataCollector(applet, properties, s, host);
    }
    
    public static DataCollector create(final String[] array, final Properties properties, final String s) {
        return new NormalDataCollector(array, properties, s, s);
    }
    
    public static DataCollector create(final Properties properties, final String s) {
        return new PropertyOnlyDataCollector(properties, s, s);
    }
}
