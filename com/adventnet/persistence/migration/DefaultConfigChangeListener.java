package com.adventnet.persistence.migration;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultConfigChangeListener implements ConfigChangeListener
{
    private static Logger logger;
    
    @Override
    public boolean handleDBPropChanges(final DBParamsChanges object) {
        DefaultConfigChangeListener.logger.log(Level.INFO, "ConfigChangeListener for DB properties change is not implemented hence no handling is done here");
        return true;
    }
    
    @Override
    public boolean handlePersConfChanges(final PersistenceConfigChanges object) {
        DefaultConfigChangeListener.logger.log(Level.INFO, "ConfigChangeListener for persistence configuration is not implemented hence no handling is done here");
        return true;
    }
    
    static {
        DefaultConfigChangeListener.logger = Logger.getLogger(DefaultConfigChangeListener.class.getName());
    }
}
