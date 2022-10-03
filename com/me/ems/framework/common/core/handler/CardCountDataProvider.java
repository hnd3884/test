package com.me.ems.framework.common.core.handler;

import org.json.simple.JSONArray;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CardCountDataProvider
{
    private static CardCountDataProvider cardCountDataProvider;
    private static final Logger LOGGER;
    
    private static Object getCardCountDataProviderAPI(final String className) {
        try {
            if (className != null && className.trim().length() != 0) {
                return Class.forName(className).newInstance();
            }
        }
        catch (final ClassNotFoundException ce) {
            CardCountDataProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for" + className, ce);
        }
        catch (final InstantiationException ie) {
            CardCountDataProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for" + className, ie);
        }
        catch (final IllegalAccessException ie2) {
            CardCountDataProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for" + className, ie2);
        }
        catch (final Exception ex) {
            CardCountDataProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for" + className, ex);
        }
        return null;
    }
    
    public static CardCountDataProvider getInstance(final String className) {
        if (CardCountDataProvider.cardCountDataProvider == null) {
            CardCountDataProvider.cardCountDataProvider = (CardCountDataProvider)getCardCountDataProviderAPI(className);
        }
        return CardCountDataProvider.cardCountDataProvider;
    }
    
    public abstract JSONArray getCountDataArray(final String p0, final Properties p1);
    
    static {
        CardCountDataProvider.cardCountDataProvider = null;
        LOGGER = Logger.getLogger(CardCountDataProvider.class.getName());
    }
}
