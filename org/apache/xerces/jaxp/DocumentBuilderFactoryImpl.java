package org.apache.xerces.jaxp;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Locale;
import org.apache.xerces.util.SAXMessageFormatter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.Schema;
import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilderFactory;

public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory
{
    private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
    private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
    private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
    private static final String INCLUDE_IGNORABLE_WHITESPACE = "http://apache.org/xml/features/dom/include-ignorable-whitespace";
    private static final String CREATE_ENTITY_REF_NODES_FEATURE = "http://apache.org/xml/features/dom/create-entity-ref-nodes";
    private static final String INCLUDE_COMMENTS_FEATURE = "http://apache.org/xml/features/include-comments";
    private static final String CREATE_CDATA_NODES_FEATURE = "http://apache.org/xml/features/create-cdata-nodes";
    private Hashtable attributes;
    private Hashtable features;
    private Schema grammar;
    private boolean isXIncludeAware;
    private boolean fSecureProcess;
    
    public DocumentBuilderFactoryImpl() {
        this.fSecureProcess = false;
    }
    
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
        catch (final SAXException ex) {
            throw new ParserConfigurationException(ex.getMessage());
        }
    }
    
    public void setAttribute(final String s, final Object o) throws IllegalArgumentException {
        if (o == null) {
            if (this.attributes != null) {
                this.attributes.remove(s);
            }
            return;
        }
        if (this.attributes == null) {
            this.attributes = new Hashtable();
        }
        this.attributes.put(s, o);
        try {
            new DocumentBuilderImpl(this, this.attributes, this.features);
        }
        catch (final Exception ex) {
            this.attributes.remove(s);
            throw new IllegalArgumentException(ex.getMessage());
        }
    }
    
    public Object getAttribute(final String s) throws IllegalArgumentException {
        if (this.attributes != null) {
            final Object value = this.attributes.get(s);
            if (value != null) {
                return value;
            }
        }
        DOMParser domParser = null;
        try {
            domParser = new DocumentBuilderImpl(this, this.attributes, this.features).getDOMParser();
            return domParser.getProperty(s);
        }
        catch (final SAXException ex) {
            try {
                return domParser.getFeature(s) ? Boolean.TRUE : Boolean.FALSE;
            }
            catch (final SAXException ex2) {
                throw new IllegalArgumentException(ex.getMessage());
            }
        }
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
    
    public boolean getFeature(final String s) throws ParserConfigurationException {
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
        if (s.equals("http://apache.org/xml/features/dom/include-ignorable-whitespace")) {
            return !this.isIgnoringElementContentWhitespace();
        }
        if (s.equals("http://apache.org/xml/features/dom/create-entity-ref-nodes")) {
            return !this.isExpandEntityReferences();
        }
        if (s.equals("http://apache.org/xml/features/include-comments")) {
            return !this.isIgnoringComments();
        }
        if (s.equals("http://apache.org/xml/features/create-cdata-nodes")) {
            return !this.isCoalescing();
        }
        if (this.features != null) {
            final Boolean value = this.features.get(s);
            if (value != null) {
                return value;
            }
        }
        try {
            return new DocumentBuilderImpl(this, this.attributes, this.features).getDOMParser().getFeature(s);
        }
        catch (final SAXException ex) {
            throw new ParserConfigurationException(ex.getMessage());
        }
    }
    
    public void setFeature(final String s, final boolean b) throws ParserConfigurationException {
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
        if (s.equals("http://apache.org/xml/features/dom/include-ignorable-whitespace")) {
            this.setIgnoringElementContentWhitespace(!b);
            return;
        }
        if (s.equals("http://apache.org/xml/features/dom/create-entity-ref-nodes")) {
            this.setExpandEntityReferences(!b);
            return;
        }
        if (s.equals("http://apache.org/xml/features/include-comments")) {
            this.setIgnoringComments(!b);
            return;
        }
        if (s.equals("http://apache.org/xml/features/create-cdata-nodes")) {
            this.setCoalescing(!b);
            return;
        }
        if (this.features == null) {
            this.features = new Hashtable();
        }
        this.features.put(s, b ? Boolean.TRUE : Boolean.FALSE);
        try {
            new DocumentBuilderImpl(this, this.attributes, this.features);
        }
        catch (final SAXNotSupportedException ex) {
            this.features.remove(s);
            throw new ParserConfigurationException(ex.getMessage());
        }
        catch (final SAXNotRecognizedException ex2) {
            this.features.remove(s);
            throw new ParserConfigurationException(ex2.getMessage());
        }
    }
}
