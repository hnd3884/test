package org.apache.xerces.jaxp;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class DefaultValidationErrorHandler extends DefaultHandler
{
    private static int ERROR_COUNT_LIMIT;
    private int errorCount;
    
    DefaultValidationErrorHandler() {
        this.errorCount = 0;
    }
    
    public void error(final SAXParseException ex) throws SAXException {
        if (this.errorCount >= DefaultValidationErrorHandler.ERROR_COUNT_LIMIT) {
            return;
        }
        if (this.errorCount == 0) {
            System.err.println("Warning: validation was turned on but an org.xml.sax.ErrorHandler was not");
            System.err.println("set, which is probably not what is desired.  Parser will use a default");
            System.err.println("ErrorHandler to print the first " + DefaultValidationErrorHandler.ERROR_COUNT_LIMIT + " errors.  Please call");
            System.err.println("the 'setErrorHandler' method to fix this.");
        }
        String systemId = ex.getSystemId();
        if (systemId == null) {
            systemId = "null";
        }
        System.err.println("Error: URI=" + systemId + " Line=" + ex.getLineNumber() + ": " + ex.getMessage());
        ++this.errorCount;
    }
    
    static {
        DefaultValidationErrorHandler.ERROR_COUNT_LIMIT = 10;
    }
}
