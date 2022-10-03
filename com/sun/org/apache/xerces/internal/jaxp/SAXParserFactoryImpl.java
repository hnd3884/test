package com.sun.org.apache.xerces.internal.jaxp;

import java.util.HashMap;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.validation.Schema;
import java.util.Map;
import javax.xml.parsers.SAXParserFactory;

public class SAXParserFactoryImpl extends SAXParserFactory
{
    private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
    private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
    private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
    private Map<String, Boolean> features;
    private Schema grammar;
    private boolean isXIncludeAware;
    private boolean fSecureProcess;
    
    public SAXParserFactoryImpl() {
        this.fSecureProcess = true;
    }
    
    @Override
    public SAXParser newSAXParser() throws ParserConfigurationException {
        SAXParser saxParserImpl;
        try {
            saxParserImpl = new SAXParserImpl(this, this.features, this.fSecureProcess);
        }
        catch (final SAXException se) {
            throw new ParserConfigurationException(se.getMessage());
        }
        return saxParserImpl;
    }
    
    private SAXParserImpl newSAXParserImpl() throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        SAXParserImpl saxParserImpl;
        try {
            saxParserImpl = new SAXParserImpl(this, this.features);
        }
        catch (final SAXNotSupportedException e) {
            throw e;
        }
        catch (final SAXNotRecognizedException e2) {
            throw e2;
        }
        catch (final SAXException se) {
            throw new ParserConfigurationException(se.getMessage());
        }
        return saxParserImpl;
    }
    
    @Override
    public void setFeature(final String name, final boolean value) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        if (!name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            this.putInFeatures(name, value);
            try {
                this.newSAXParserImpl();
            }
            catch (final SAXNotSupportedException e) {
                this.features.remove(name);
                throw e;
            }
            catch (final SAXNotRecognizedException e2) {
                this.features.remove(name);
                throw e2;
            }
            return;
        }
        if (System.getSecurityManager() != null && !value) {
            throw new ParserConfigurationException(SAXMessageFormatter.formatMessage(null, "jaxp-secureprocessing-feature", null));
        }
        this.putInFeatures(name, this.fSecureProcess = value);
    }
    
    @Override
    public boolean getFeature(final String name) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            return this.fSecureProcess;
        }
        return this.newSAXParserImpl().getXMLReader().getFeature(name);
    }
    
    @Override
    public Schema getSchema() {
        return this.grammar;
    }
    
    @Override
    public void setSchema(final Schema grammar) {
        this.grammar = grammar;
    }
    
    @Override
    public boolean isXIncludeAware() {
        return this.getFromFeatures("http://apache.org/xml/features/xinclude");
    }
    
    @Override
    public void setXIncludeAware(final boolean state) {
        this.putInFeatures("http://apache.org/xml/features/xinclude", state);
    }
    
    @Override
    public void setValidating(final boolean validating) {
        this.putInFeatures("http://xml.org/sax/features/validation", validating);
    }
    
    @Override
    public boolean isValidating() {
        return this.getFromFeatures("http://xml.org/sax/features/validation");
    }
    
    private void putInFeatures(final String name, final boolean value) {
        if (this.features == null) {
            this.features = new HashMap<String, Boolean>();
        }
        this.features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
    }
    
    private boolean getFromFeatures(final String name) {
        if (this.features == null) {
            return false;
        }
        final Boolean value = this.features.get(name);
        return value != null && value;
    }
    
    @Override
    public boolean isNamespaceAware() {
        return this.getFromFeatures("http://xml.org/sax/features/namespaces");
    }
    
    @Override
    public void setNamespaceAware(final boolean awareness) {
        this.putInFeatures("http://xml.org/sax/features/namespaces", awareness);
    }
}
