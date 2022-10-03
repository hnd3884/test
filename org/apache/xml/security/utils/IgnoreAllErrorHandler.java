package org.apache.xml.security.utils;

import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.apache.commons.logging.Log;
import org.xml.sax.ErrorHandler;

public class IgnoreAllErrorHandler implements ErrorHandler
{
    static Log log;
    static final boolean warnOnExceptions;
    static final boolean throwExceptions;
    
    public void warning(final SAXParseException ex) throws SAXException {
        if (IgnoreAllErrorHandler.warnOnExceptions) {
            IgnoreAllErrorHandler.log.warn((Object)"", (Throwable)ex);
        }
        if (IgnoreAllErrorHandler.throwExceptions) {
            throw ex;
        }
    }
    
    public void error(final SAXParseException ex) throws SAXException {
        if (IgnoreAllErrorHandler.warnOnExceptions) {
            IgnoreAllErrorHandler.log.error((Object)"", (Throwable)ex);
        }
        if (IgnoreAllErrorHandler.throwExceptions) {
            throw ex;
        }
    }
    
    public void fatalError(final SAXParseException ex) throws SAXException {
        if (IgnoreAllErrorHandler.warnOnExceptions) {
            IgnoreAllErrorHandler.log.warn((Object)"", (Throwable)ex);
        }
        if (IgnoreAllErrorHandler.throwExceptions) {
            throw ex;
        }
    }
    
    static {
        IgnoreAllErrorHandler.log = LogFactory.getLog(IgnoreAllErrorHandler.class.getName());
        warnOnExceptions = System.getProperty("org.apache.xml.security.test.warn.on.exceptions", "false").equals("true");
        throwExceptions = System.getProperty("org.apache.xml.security.test.throw.exceptions", "false").equals("true");
    }
}
