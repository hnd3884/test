package com.adventnet.db.persistence.metadata;

import org.xml.sax.SAXException;
import com.zoho.conf.AppResources;
import java.util.logging.Level;
import org.xml.sax.SAXParseException;
import java.util.logging.Logger;
import org.xml.sax.ErrorHandler;

public class MetaDataErrorHandler implements ErrorHandler
{
    private static final Logger OUT;
    
    @Override
    public void error(final SAXParseException exception) throws SAXException {
        final String id = (exception.getSystemId() == null) ? "SystemId Unknown" : exception.getSystemId();
        final String message = id + "; Line " + exception.getLineNumber() + "; Column " + exception.getColumnNumber() + "; ";
        System.err.print(message);
        MetaDataErrorHandler.OUT.log(Level.SEVERE, message);
        throw new SAXException(message + AppResources.getString("line.separator") + exception.getMessage());
    }
    
    @Override
    public void fatalError(final SAXParseException exception) throws SAXException {
        final String id = (exception.getSystemId() == null) ? "SystemId Unknown" : exception.getSystemId();
        final String message = id + "; Line " + exception.getLineNumber() + "; Column " + exception.getColumnNumber() + "; ";
        System.err.print(message);
        MetaDataErrorHandler.OUT.log(Level.SEVERE, message);
        throw new SAXException(message + AppResources.getString("line.separator") + exception.getMessage());
    }
    
    @Override
    public void warning(final SAXParseException exception) throws SAXException {
        final String id = (exception.getSystemId() == null) ? "SystemId Unknown" : exception.getSystemId();
        final String message = id + "; Line " + exception.getLineNumber() + "; Column " + exception.getColumnNumber() + "; ";
        MetaDataErrorHandler.OUT.log(Level.WARNING, message);
        MetaDataErrorHandler.OUT.log(Level.WARNING, "Parser warning: {0}", exception.getMessage());
    }
    
    static {
        OUT = Logger.getLogger(MetaDataErrorHandler.class.getName());
    }
}
