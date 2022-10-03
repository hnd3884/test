package com.adventnet.webclient.util;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;

public class ErrorHandlerImpl implements ErrorHandler
{
    public void warning(final SAXParseException e) throws SAXException {
        System.err.println("PARSING WARNING!");
        System.err.println(this.getString(e));
        throw e;
    }
    
    public void error(final SAXParseException e) throws SAXException {
        System.err.println("PARSING ERROR!");
        System.err.println(this.getString(e));
        throw e;
    }
    
    public void fatalError(final SAXParseException e) throws SAXException {
        System.err.println("PARSING FATAL ERROR!!");
        System.err.println(this.getString(e));
        throw e;
    }
    
    private String getString(final SAXParseException e) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Column: ");
        buffer.append(e.getColumnNumber());
        buffer.append("\t");
        buffer.append("Line: ");
        buffer.append(e.getLineNumber());
        buffer.append("\t");
        buffer.append("URI: ");
        buffer.append(e.getSystemId());
        buffer.append("\t");
        buffer.append("Message: ");
        buffer.append(e.getMessage());
        return buffer.toString();
    }
}
