package com.adventnet.persistence.migration;

public interface ConfigChangeListener
{
    public static final int OVER_WRITE = 1;
    public static final int IGNORE = 2;
    
    boolean handleDBPropChanges(final DBParamsChanges p0);
    
    boolean handlePersConfChanges(final PersistenceConfigChanges p0);
}
