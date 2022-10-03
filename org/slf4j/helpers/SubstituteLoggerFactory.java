package org.slf4j.helpers;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.ILoggerFactory;

public class SubstituteLoggerFactory implements ILoggerFactory
{
    final ConcurrentMap<String, SubstituteLogger> loggers;
    
    public SubstituteLoggerFactory() {
        this.loggers = new ConcurrentHashMap<String, SubstituteLogger>();
    }
    
    public Logger getLogger(final String name) {
        SubstituteLogger logger = this.loggers.get(name);
        if (logger == null) {
            logger = new SubstituteLogger(name);
            final SubstituteLogger oldLogger = this.loggers.putIfAbsent(name, logger);
            if (oldLogger != null) {
                logger = oldLogger;
            }
        }
        return logger;
    }
    
    public List<String> getLoggerNames() {
        return new ArrayList<String>((Collection<? extends String>)this.loggers.keySet());
    }
    
    public List<SubstituteLogger> getLoggers() {
        return new ArrayList<SubstituteLogger>(this.loggers.values());
    }
    
    public void clear() {
        this.loggers.clear();
    }
}
