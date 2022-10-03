package org.apache.xerces.jaxp;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.validation.Schema;
import java.util.Hashtable;
import javax.xml.parsers.SAXParserFactory;

public class SAXParserFactoryImpl extends SAXParserFactory
{
    private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
    private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
    private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
    private Hashtable features;
    private Schema grammar;
    private boolean isXIncludeAware;
    private boolean fSecureProcess;
    
    public SAXParserFactoryImpl() {
        this.fSecureProcess = false;
    }
    
    public SAXParser newSAXParser() throws ParserConfigurationException {
        SAXParserImpl saxParserImpl;
        try {
            saxParserImpl = new SAXParserImpl(this, this.features, this.fSecureProcess);
        }
        catch (final SAXException ex) {
            throw new ParserConfigurationException(ex.getMessage());
        }
        return saxParserImpl;
    }
    
    private SAXParserImpl newSAXParserImpl() throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        SAXParserImpl saxParserImpl;
        try {
            saxParserImpl = new SAXParserImpl(this, this.features);
        }
        catch (final SAXNotSupportedException ex) {
            throw ex;
        }
        catch (final SAXNotRecognizedException ex2) {
            throw ex2;
        }
        catch (final SAXException ex3) {
            throw new ParserConfigurationException(ex3.getMessage());
        }
        return saxParserImpl;
    }
    
    public void setFeature(final String s, final boolean b) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException();
        }
        if (s.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            this.fSecureProcess = b;
            return;
        }
        if (s.equals("http://xml.org/sax/features/namespaces")) {
            this.setNamespaceAware(b);
            return;
        }
        if (s.equals("http://xml.org/sax/features/validation")) {
            this.setValidating(b);
            return;
        }
        if (s.equals("http://apache.org/xml/features/xinclude")) {
            this.setXIncludeAware(b);
            return;
        }
        if (this.features == null) {
            this.features = new Hashtable();
        }
        this.features.put(s, b ? Boolean.TRUE : Boolean.FALSE);
        try {
            this.newSAXParserImpl();
        }
        catch (final SAXNotSupportedException ex) {
            this.features.remove(s);
            throw ex;
        }
        catch (final SAXNotRecognizedException ex2) {
            this.features.remove(s);
            throw ex2;
        }
    }
    
    public boolean getFeature(final String s) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException();
        }
        if (s.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            return this.fSecureProcess;
        }
        if (s.equals("http://xml.org/sax/features/namespaces")) {
            return this.isNamespaceAware();
        }
        if (s.equals("http://xml.org/sax/features/validation")) {
            return this.isValidating();
        }
        if (s.equals("http://apache.org/xml/features/xinclude")) {
            return this.isXIncludeAware();
        }
        return this.newSAXParserImpl().getXMLReader().getFeature(s);
    }
    
    public Schema getSchema() {
        return this.grammar;
    }
    
    public void setSchema(final Schema grammar) {
        this.grammar = grammar;
    }
    
    public boolean isXIncludeAware() {
        return this.isXIncludeAware;
    }
    
    public void setXIncludeAware(final boolean isXIncludeAware) {
        this.isXIncludeAware = isXIncludeAware;
    }
}
