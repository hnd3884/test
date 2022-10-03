package com.sun.xml.internal.org.jvnet.mimepull;

import java.util.concurrent.Executor;

public abstract class CleanUpExecutorFactory
{
    private static final String DEFAULT_PROPERTY_NAME;
    
    protected CleanUpExecutorFactory() {
    }
    
    public static CleanUpExecutorFactory newInstance() {
        try {
            return (CleanUpExecutorFactory)FactoryFinder.find(CleanUpExecutorFactory.DEFAULT_PROPERTY_NAME);
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public abstract Executor getExecutor();
    
    static {
        DEFAULT_PROPERTY_NAME = CleanUpExecutorFactory.class.getName();
    }
}
