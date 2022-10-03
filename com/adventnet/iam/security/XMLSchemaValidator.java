package com.adventnet.iam.security;

import org.xml.sax.SAXException;
import java.util.logging.Level;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import java.util.logging.Logger;

public class XMLSchemaValidator
{
    private static final Logger LOGGER;
    static XMLReader reader;
    
    public static void validateXMLSchema(final String fileName) throws Exception {
        if (XMLSchemaValidator.reader == null) {
            (XMLSchemaValidator.reader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser")).setFeature("http://xml.org/sax/features/validation", true);
            XMLSchemaValidator.reader.setFeature("http://apache.org/xml/features/validation/schema", true);
            XMLSchemaValidator.reader.setErrorHandler(new ErrorHandler());
        }
        XMLSchemaValidator.reader.parse(fileName);
    }
    
    static {
        LOGGER = Logger.getLogger(XMLSchemaValidator.class.getName());
        XMLSchemaValidator.reader = null;
    }
    
    static class ErrorHandler extends DefaultHandler
    {
        @Override
        public void warning(final SAXParseException e) throws SAXException {
            XMLSchemaValidator.LOGGER.log(Level.WARNING, "\nPARSE_WARNING: " + this.getDetails(e));
            throw e;
        }
        
        @Override
        public void error(final SAXParseException e) throws SAXException {
            XMLSchemaValidator.LOGGER.log(Level.SEVERE, "\nPARSE_ERROR: " + this.getDetails(e));
            throw e;
        }
        
        @Override
        public void fatalError(final SAXParseException e) throws SAXException {
            XMLSchemaValidator.LOGGER.log(Level.SEVERE, "\nPARSE_FATAL_ERROR: " + this.getDetails(e));
            throw e;
        }
        
        String getDetails(final SAXParseException e) {
            return "File : " + e.getSystemId() + " ## Line No: " + e.getLineNumber() + " ## Column number: " + e.getColumnNumber() + " ## Message : " + e.getMessage();
        }
    }
}
