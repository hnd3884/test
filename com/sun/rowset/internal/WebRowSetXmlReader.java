package com.sun.rowset.internal;

import java.io.ObjectInputStream;
import javax.sql.RowSetInternal;
import java.io.InputStream;
import org.xml.sax.XMLReader;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.sql.SQLException;
import java.text.MessageFormat;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.SAXParserFactory;
import javax.sql.RowSet;
import org.xml.sax.InputSource;
import java.io.Reader;
import javax.sql.rowset.WebRowSet;
import java.io.IOException;
import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.Serializable;
import javax.sql.rowset.spi.XmlReader;

public class WebRowSetXmlReader implements XmlReader, Serializable
{
    private JdbcRowSetResourceBundle resBundle;
    static final long serialVersionUID = -9127058392819008014L;
    
    public WebRowSetXmlReader() {
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public void readXML(final WebRowSet set, final Reader characterStream) throws SQLException {
        try {
            final InputSource inputSource = new InputSource(characterStream);
            final XmlErrorHandler errorHandler = new XmlErrorHandler();
            final XmlReaderContentHandler contentHandler = new XmlReaderContentHandler(set);
            final SAXParserFactory instance = SAXParserFactory.newInstance();
            instance.setNamespaceAware(true);
            instance.setValidating(true);
            final SAXParser saxParser = instance.newSAXParser();
            saxParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            final XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setEntityResolver(new XmlResolver());
            xmlReader.setContentHandler(contentHandler);
            xmlReader.setErrorHandler(errorHandler);
            xmlReader.parse(inputSource);
        }
        catch (final SAXParseException ex) {
            System.out.println(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlreader.parseerr").toString(), ex.getMessage(), ex.getLineNumber(), ex.getSystemId()));
            ex.printStackTrace();
            throw new SQLException(ex.getMessage());
        }
        catch (final SAXException ex2) {
            Exception exception = ex2;
            if (ex2.getException() != null) {
                exception = ex2.getException();
            }
            exception.printStackTrace();
            throw new SQLException(exception.getMessage());
        }
        catch (final ArrayIndexOutOfBoundsException ex3) {
            throw new SQLException(this.resBundle.handleGetObject("wrsxmlreader.invalidcp").toString());
        }
        catch (final Throwable t) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlreader.readxml").toString(), t.getMessage()));
        }
    }
    
    public void readXML(final WebRowSet set, final InputStream byteStream) throws SQLException {
        try {
            final InputSource inputSource = new InputSource(byteStream);
            final XmlErrorHandler errorHandler = new XmlErrorHandler();
            final XmlReaderContentHandler contentHandler = new XmlReaderContentHandler(set);
            final SAXParserFactory instance = SAXParserFactory.newInstance();
            instance.setNamespaceAware(true);
            instance.setValidating(true);
            final SAXParser saxParser = instance.newSAXParser();
            saxParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            final XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setEntityResolver(new XmlResolver());
            xmlReader.setContentHandler(contentHandler);
            xmlReader.setErrorHandler(errorHandler);
            xmlReader.parse(inputSource);
        }
        catch (final SAXParseException ex) {
            System.out.println(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlreader.parseerr").toString(), ex.getLineNumber(), ex.getSystemId()));
            System.out.println("   " + ex.getMessage());
            ex.printStackTrace();
            throw new SQLException(ex.getMessage());
        }
        catch (final SAXException ex2) {
            Exception exception = ex2;
            if (ex2.getException() != null) {
                exception = ex2.getException();
            }
            exception.printStackTrace();
            throw new SQLException(exception.getMessage());
        }
        catch (final ArrayIndexOutOfBoundsException ex3) {
            throw new SQLException(this.resBundle.handleGetObject("wrsxmlreader.invalidcp").toString());
        }
        catch (final Throwable t) {
            throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlreader.readxml").toString(), t.getMessage()));
        }
    }
    
    @Override
    public void readData(final RowSetInternal rowSetInternal) {
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
