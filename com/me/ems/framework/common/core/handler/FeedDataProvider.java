package com.me.ems.framework.common.core.handler;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class FeedDataProvider
{
    private static FeedDataProvider securityCardDataProvider;
    private static final Logger LOGGER;
    
    private static Object getSecurityCardDataProvider(final String className) {
        try {
            if (className != null && className.trim().length() != 0) {
                return Class.forName(className).newInstance();
            }
        }
        catch (final ClassNotFoundException ce) {
            FeedDataProvider.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for" + className, ce);
        }
        catch (final InstantiationException ie) {
            FeedDataProvider.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation  for" + className, ie);
        }
        catch (final IllegalAccessException ie2) {
            FeedDataProvider.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation  for" + className, ie2);
        }
        catch (final Exception ex) {
            FeedDataProvider.LOGGER.log(Level.SEVERE, "Exception During Instantiation  for" + className, ex);
        }
        return null;
    }
    
    public static FeedDataProvider getInstance(final String className) {
        if (FeedDataProvider.securityCardDataProvider == null) {
            FeedDataProvider.securityCardDataProvider = (FeedDataProvider)getSecurityCardDataProvider(className);
        }
        return FeedDataProvider.securityCardDataProvider;
    }
    
    public abstract List<HashMap> getFeeds();
    
    static {
        FeedDataProvider.securityCardDataProvider = null;
        LOGGER = Logger.getLogger(FeedDataProvider.class.getName());
    }
}
