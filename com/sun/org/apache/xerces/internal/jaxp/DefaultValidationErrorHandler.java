package com.sun.org.apache.xerces.internal.jaxp;

import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import org.xml.sax.SAXParseException;
import java.util.Locale;
import org.xml.sax.helpers.DefaultHandler;

class DefaultValidationErrorHandler extends DefaultHandler
{
    private static int ERROR_COUNT_LIMIT;
    private int errorCount;
    private Locale locale;
    
    public DefaultValidationErrorHandler(final Locale locale) {
        this.errorCount = 0;
        this.locale = Locale.getDefault();
        this.locale = locale;
    }
    
    @Override
    public void error(final SAXParseException e) throws SAXException {
        if (this.errorCount >= DefaultValidationErrorHandler.ERROR_COUNT_LIMIT) {
            return;
        }
        if (this.errorCount == 0) {
            System.err.println(SAXMessageFormatter.formatMessage(this.locale, "errorHandlerNotSet", new Object[] { this.errorCount }));
        }
        String systemId = e.getSystemId();
        if (systemId == null) {
            systemId = "null";
        }
        final String message = "Error: URI=" + systemId + " Line=" + e.getLineNumber() + ": " + e.getMessage();
        System.err.println(message);
        ++this.errorCount;
    }
    
    static {
        DefaultValidationErrorHandler.ERROR_COUNT_LIMIT = 10;
    }
}
