package org.apache.poi.util;

import java.util.HashMap;
import java.util.Map;

@Internal
public final class POILogFactory
{
    private static final Map<String, POILogger> _loggers;
    private static final POILogger _nullLogger;
    static String _loggerClassName;
    
    private POILogFactory() {
    }
    
    public static POILogger getLogger(final Class<?> theclass) {
        return getLogger(theclass.getName());
    }
    
    public static POILogger getLogger(final String cat) {
        if (POILogFactory._loggerClassName == null) {
            try {
                POILogFactory._loggerClassName = System.getProperty("org.apache.poi.util.POILogger");
            }
            catch (final Exception ex) {}
            if (POILogFactory._loggerClassName == null) {
                POILogFactory._loggerClassName = POILogFactory._nullLogger.getClass().getName();
            }
        }
        if (POILogFactory._loggerClassName.equals(POILogFactory._nullLogger.getClass().getName())) {
            return POILogFactory._nullLogger;
        }
        POILogger logger = POILogFactory._loggers.get(cat);
        if (logger == null) {
            try {
                final Class<? extends POILogger> loggerClass = (Class<? extends POILogger>)Class.forName(POILogFactory._loggerClassName);
                logger = (POILogger)loggerClass.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                logger.initialize(cat);
            }
            catch (final Exception e) {
                logger = POILogFactory._nullLogger;
                POILogFactory._loggerClassName = POILogFactory._nullLogger.getClass().getName();
            }
            POILogFactory._loggers.put(cat, logger);
        }
        return logger;
    }
    
    static {
        _loggers = new HashMap<String, POILogger>();
        _nullLogger = new NullLogger();
    }
}
