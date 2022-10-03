package org.htmlparser.sax;

import org.htmlparser.util.ParserException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;
import org.xml.sax.ErrorHandler;
import org.htmlparser.util.ParserFeedback;

public class Feedback implements ParserFeedback
{
    protected ErrorHandler mErrorHandler;
    protected Locator mLocator;
    
    public Feedback(final ErrorHandler handler, final Locator locator) {
        this.mErrorHandler = handler;
        this.mLocator = locator;
    }
    
    public void info(final String message) {
    }
    
    public void warning(final String message) {
        try {
            this.mErrorHandler.warning(new SAXParseException(message, this.mLocator));
        }
        catch (final SAXException se) {
            se.printStackTrace();
        }
    }
    
    public void error(final String message, final ParserException e) {
        try {
            this.mErrorHandler.error(new SAXParseException(message, this.mLocator, e));
        }
        catch (final SAXException se) {
            se.printStackTrace();
        }
    }
}
