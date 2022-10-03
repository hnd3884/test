package org.owasp.esapi.reference;

import org.apache.log4j.LogManager;
import org.owasp.esapi.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.owasp.esapi.LogFactory;

public class Log4JLogFactory implements LogFactory
{
    private static volatile LogFactory singletonInstance;
    LoggerFactory factory;
    
    public static LogFactory getInstance() {
        if (Log4JLogFactory.singletonInstance == null) {
            synchronized (Log4JLogFactory.class) {
                if (Log4JLogFactory.singletonInstance == null) {
                    Log4JLogFactory.singletonInstance = new Log4JLogFactory();
                }
            }
        }
        return Log4JLogFactory.singletonInstance;
    }
    
    protected Log4JLogFactory() {
        this.factory = (LoggerFactory)new Log4JLoggerFactory();
    }
    
    @Override
    public Logger getLogger(final Class clazz) {
        return (Logger)LogManager.getLogger(clazz.getName(), this.factory);
    }
    
    @Override
    public Logger getLogger(final String moduleName) {
        return (Logger)LogManager.getLogger(moduleName, this.factory);
    }
}
