package org.apache.xmlbeans.impl.common;

import java.util.HashMap;
import java.util.Map;

public final class XBLogFactory
{
    private static final Map<String, XBLogger> _loggers;
    private static final XBLogger _nullLogger;
    static String _loggerClassName;
    
    private XBLogFactory() {
    }
    
    public static XBLogger getLogger(final Class<?> theclass) {
        return getLogger(theclass.getName());
    }
    
    public static XBLogger getLogger(final String cat) {
        if (XBLogFactory._loggerClassName == null) {
            try {
                XBLogFactory._loggerClassName = System.getProperty("org.apache.xmlbeans.impl.store.XBLogger");
            }
            catch (final Exception ex) {}
            if (XBLogFactory._loggerClassName == null) {
                XBLogFactory._loggerClassName = XBLogFactory._nullLogger.getClass().getName();
            }
        }
        if (XBLogFactory._loggerClassName.equals(XBLogFactory._nullLogger.getClass().getName())) {
            return XBLogFactory._nullLogger;
        }
        XBLogger logger = XBLogFactory._loggers.get(cat);
        if (logger == null) {
            try {
                final Class<? extends XBLogger> loggerClass = (Class<? extends XBLogger>)Class.forName(XBLogFactory._loggerClassName);
                logger = (XBLogger)loggerClass.newInstance();
                logger.initialize(cat);
            }
            catch (final Exception e) {
                logger = XBLogFactory._nullLogger;
                XBLogFactory._loggerClassName = XBLogFactory._nullLogger.getClass().getName();
            }
            XBLogFactory._loggers.put(cat, logger);
        }
        return logger;
    }
    
    static {
        _loggers = new HashMap<String, XBLogger>();
        _nullLogger = new NullLogger();
        XBLogFactory._loggerClassName = null;
    }
}
