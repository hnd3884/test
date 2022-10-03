package com.me.ems.framework.common.core.handler;

import org.json.simple.JSONArray;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MultiCardDataProvider
{
    private static MultiCardDataProvider multiCardDataProvider;
    private static final Logger LOGGER;
    
    private static Object getMultiCardDataProvider(final String className) {
        try {
            if (className != null && className.trim().length() != 0) {
                return Class.forName(className).newInstance();
            }
        }
        catch (final ClassNotFoundException ce) {
            MultiCardDataProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for" + className, ce);
        }
        catch (final InstantiationException ie) {
            MultiCardDataProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for" + className, ie);
        }
        catch (final IllegalAccessException ie2) {
            MultiCardDataProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for" + className, ie2);
        }
        catch (final Exception ex) {
            MultiCardDataProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for" + className, ex);
        }
        return null;
    }
    
    public static MultiCardDataProvider getInstance(final String className) {
        if (MultiCardDataProvider.multiCardDataProvider == null) {
            MultiCardDataProvider.multiCardDataProvider = (MultiCardDataProvider)getMultiCardDataProvider(className);
        }
        return MultiCardDataProvider.multiCardDataProvider;
    }
    
    public abstract JSONArray getMultiCardData(final String p0, final Properties p1, final Properties p2);
    
    static {
        MultiCardDataProvider.multiCardDataProvider = null;
        LOGGER = Logger.getLogger(MultiCardDataProvider.class.getName());
    }
}
