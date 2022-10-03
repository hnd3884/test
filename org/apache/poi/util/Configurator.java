package org.apache.poi.util;

public class Configurator
{
    private static POILogger logger;
    
    public static int getIntValue(final String systemProperty, final int defaultValue) {
        int result = defaultValue;
        final String property = System.getProperty(systemProperty);
        try {
            result = Integer.parseInt(property);
        }
        catch (final Exception e) {
            Configurator.logger.log(7, "System property -D" + systemProperty + " do not contains a valid integer " + property);
        }
        return result;
    }
    
    static {
        Configurator.logger = POILogFactory.getLogger(Configurator.class);
    }
}
