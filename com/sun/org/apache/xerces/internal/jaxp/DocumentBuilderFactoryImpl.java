package com.sun.org.apache.xerces.internal.jaxp;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import java.util.HashMap;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.Schema;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;

public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory
{
    private Map<String, Object> attributes;
    private Map<String, Boolean> features;
    private Schema grammar;
    private boolean isXIncludeAware;
    private boolean fSecureProcess;
    
    public DocumentBuilderFactoryImpl() {
        this.fSecureProcess = true;
    }
    
    @Override
    public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        if (this.grammar != null && this.attributes != null) {
            if (this.attributes.containsKey("http://java.sun.com/xml/jaxp/properties/schemaLanguage")) {
                throw new ParserConfigurationException(SAXMessageFormatter.formatMessage(null, "schema-already-specified", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage" }));
            }
            if (this.attributes.containsKey("http://java.sun.com/xml/jaxp/properties/schemaSource")) {
                throw new ParserConfigurationException(SAXMessageFormatter.formatMessage(null, "schema-already-specified", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaSource" }));
            }
        }
        try {
            return new DocumentBuilderImpl(this, this.attributes, this.features, this.fSecureProcess);
        }
        catch (final SAXException se) {
            throw new ParserConfigurationException(se.getMessage());
        }
    }
    
    @Override
    public void setAttribute(final String name, final Object value) throws IllegalArgumentException {
        if (value == null) {
            if (this.attributes != null) {
                this.attributes.remove(name);
            }
            return;
        }
        if (this.attributes == null) {
            this.attributes = new HashMap<String, Object>();
        }
        this.attributes.put(name, value);
        try {
            new DocumentBuilderImpl(this, this.attributes, this.features);
        }
        catch (final Exception e) {
            this.attributes.remove(name);
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    
    @Override
    public Object getAttribute(final String name) throws IllegalArgumentException {
        if (this.attributes != null) {
            final Object val = this.attributes.get(name);
            if (val != null) {
                return val;
            }
        }
        DOMParser domParser = null;
        try {
            domParser = new DocumentBuilderImpl(this, this.attributes, this.features).getDOMParser();
            return domParser.getProperty(name);
        }
        catch (final SAXException se1) {
            try {
                final boolean result = domParser.getFeature(name);
                return result ? Boolean.TRUE : Boolean.FALSE;
            }
            catch (final SAXException se2) {
                throw new IllegalArgumentException(se1.getMessage());
            }
        }
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
        return this.isXIncludeAware;
    }
    
    @Override
    public void setXIncludeAware(final boolean state) {
        this.isXIncludeAware = state;
    }
    
    @Override
    public boolean getFeature(final String name) throws ParserConfigurationException {
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            return this.fSecureProcess;
        }
        if (this.features != null) {
            final Boolean val = this.features.get(name);
            if (val != null) {
                return val;
            }
        }
        try {
            final DOMParser domParser = new DocumentBuilderImpl(this, this.attributes, this.features).getDOMParser();
            return domParser.getFeature(name);
        }
        catch (final SAXException e) {
            throw new ParserConfigurationException(e.getMessage());
        }
    }
    
    @Override
    public void setFeature(final String name, final boolean value) throws ParserConfigurationException {
        if (this.features == null) {
            this.features = new HashMap<String, Boolean>();
        }
        if (!name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            this.features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
            try {
                new DocumentBuilderImpl(this, this.attributes, this.features);
            }
            catch (final SAXNotSupportedException e) {
                this.features.remove(name);
                throw new ParserConfigurationException(e.getMessage());
            }
            catch (final SAXNotRecognizedException e2) {
                this.features.remove(name);
                throw new ParserConfigurationException(e2.getMessage());
            }
            return;
        }
        if (System.getSecurityManager() != null && !value) {
            throw new ParserConfigurationException(SAXMessageFormatter.formatMessage(null, "jaxp-secureprocessing-feature", null));
        }
        this.fSecureProcess = value;
        this.features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
    }
}
