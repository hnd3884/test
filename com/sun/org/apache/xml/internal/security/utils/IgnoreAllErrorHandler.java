package com.sun.org.apache.xml.internal.security.utils;

import com.sun.org.slf4j.internal.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.security.AccessController;
import com.sun.org.slf4j.internal.Logger;
import org.xml.sax.ErrorHandler;

public class IgnoreAllErrorHandler implements ErrorHandler
{
    private static final Logger LOG;
    private static final boolean warnOnExceptions;
    private static final boolean throwExceptions;
    
    private static boolean getProperty(final String s) {
        return AccessController.doPrivileged(() -> Boolean.getBoolean(s2));
    }
    
    @Override
    public void warning(final SAXParseException ex) throws SAXException {
        if (IgnoreAllErrorHandler.warnOnExceptions) {
            IgnoreAllErrorHandler.LOG.warn("", ex);
        }
        if (IgnoreAllErrorHandler.throwExceptions) {
            throw ex;
        }
    }
    
    @Override
    public void error(final SAXParseException ex) throws SAXException {
        if (IgnoreAllErrorHandler.warnOnExceptions) {
            IgnoreAllErrorHandler.LOG.error("", ex);
        }
        if (IgnoreAllErrorHandler.throwExceptions) {
            throw ex;
        }
    }
    
    @Override
    public void fatalError(final SAXParseException ex) throws SAXException {
        if (IgnoreAllErrorHandler.warnOnExceptions) {
            IgnoreAllErrorHandler.LOG.warn("", ex);
        }
        if (IgnoreAllErrorHandler.throwExceptions) {
            throw ex;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(IgnoreAllErrorHandler.class);
        warnOnExceptions = getProperty("com.sun.org.apache.xml.internal.security.test.warn.on.exceptions");
        throwExceptions = getProperty("com.sun.org.apache.xml.internal.security.test.throw.exceptions");
    }
}
