package org.apache.xerces.jaxp;

import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.util.SAXMessageFormatter;
import java.util.HashMap;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.ElementPSVI;
import org.xml.sax.HandlerBase;
import java.io.IOException;
import org.xml.sax.DocumentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.Parser;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import java.util.Iterator;
import java.util.Map;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.xs.XMLSchemaValidator;
import org.apache.xerces.jaxp.validation.XSGrammarPoolContainer;
import org.apache.xerces.util.SecurityManager;
import org.xml.sax.SAXException;
import java.util.Hashtable;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLComponent;
import javax.xml.validation.Schema;
import org.apache.xerces.xs.PSVIProvider;
import javax.xml.parsers.SAXParser;

public class SAXParserImpl extends SAXParser implements JAXPConstants, PSVIProvider
{
    private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
    private static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
    private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
    private static final String XMLSCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
    private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String XML_SCHEMA_VERSION = "http://apache.org/xml/properties/validation/schema/version";
    private final JAXPSAXParser xmlReader;
    private String schemaLanguage;
    private final Schema grammar;
    private final XMLComponent fSchemaValidator;
    private final XMLComponentManager fSchemaValidatorComponentManager;
    private final ValidationManager fSchemaValidationManager;
    private final UnparsedEntityHandler fUnparsedEntityHandler;
    private final ErrorHandler fInitErrorHandler;
    private final EntityResolver fInitEntityResolver;
    
    SAXParserImpl(final SAXParserFactoryImpl saxParserFactoryImpl, final Hashtable hashtable) throws SAXException {
        this(saxParserFactoryImpl, hashtable, false);
    }
    
    SAXParserImpl(final SAXParserFactoryImpl saxParserFactoryImpl, final Hashtable features, final boolean b) throws SAXException {
        this.schemaLanguage = null;
        (this.xmlReader = new JAXPSAXParser(this)).setFeature0("http://xml.org/sax/features/namespaces", saxParserFactoryImpl.isNamespaceAware());
        this.xmlReader.setFeature0("http://xml.org/sax/features/namespace-prefixes", !saxParserFactoryImpl.isNamespaceAware());
        if (saxParserFactoryImpl.isXIncludeAware()) {
            this.xmlReader.setFeature0("http://apache.org/xml/features/xinclude", true);
        }
        if (b) {
            this.xmlReader.setProperty0("http://apache.org/xml/properties/security-manager", new SecurityManager());
        }
        this.setFeatures(features);
        if (saxParserFactoryImpl.isValidating()) {
            this.fInitErrorHandler = new DefaultValidationErrorHandler();
            this.xmlReader.setErrorHandler(this.fInitErrorHandler);
        }
        else {
            this.fInitErrorHandler = this.xmlReader.getErrorHandler();
        }
        this.xmlReader.setFeature0("http://xml.org/sax/features/validation", saxParserFactoryImpl.isValidating());
        this.grammar = saxParserFactoryImpl.getSchema();
        if (this.grammar != null) {
            final XMLParserConfiguration xmlParserConfiguration = this.xmlReader.getXMLParserConfiguration();
            XMLComponent fSchemaValidator;
            if (this.grammar instanceof XSGrammarPoolContainer) {
                final String xmlSchemaVersion = ((XSGrammarPoolContainer)this.grammar).getXMLSchemaVersion();
                fSchemaValidator = new XMLSchemaValidator();
                if (Constants.W3C_XML_SCHEMA11_NS_URI.equals(xmlSchemaVersion)) {
                    fSchemaValidator.setProperty("http://apache.org/xml/properties/validation/schema/version", Constants.W3C_XML_SCHEMA11_NS_URI);
                }
                this.fSchemaValidationManager = new ValidationManager();
                xmlParserConfiguration.setDTDHandler(this.fUnparsedEntityHandler = new UnparsedEntityHandler(this.fSchemaValidationManager));
                this.fUnparsedEntityHandler.setDTDHandler(this.xmlReader);
                this.xmlReader.setDTDSource(this.fUnparsedEntityHandler);
                this.fSchemaValidatorComponentManager = new SchemaValidatorConfiguration(xmlParserConfiguration, (XSGrammarPoolContainer)this.grammar, this.fSchemaValidationManager);
            }
            else {
                fSchemaValidator = new JAXPValidatorComponent(this.grammar.newValidatorHandler());
                this.fSchemaValidationManager = null;
                this.fUnparsedEntityHandler = null;
                this.fSchemaValidatorComponentManager = xmlParserConfiguration;
            }
            xmlParserConfiguration.addRecognizedFeatures(fSchemaValidator.getRecognizedFeatures());
            xmlParserConfiguration.addRecognizedProperties(fSchemaValidator.getRecognizedProperties());
            xmlParserConfiguration.setDocumentHandler((XMLDocumentHandler)fSchemaValidator);
            ((XMLDocumentSource)fSchemaValidator).setDocumentHandler(this.xmlReader);
            this.xmlReader.setDocumentSource((XMLDocumentSource)fSchemaValidator);
            this.fSchemaValidator = fSchemaValidator;
        }
        else {
            this.fSchemaValidationManager = null;
            this.fUnparsedEntityHandler = null;
            this.fSchemaValidatorComponentManager = null;
            this.fSchemaValidator = null;
        }
        this.fInitEntityResolver = this.xmlReader.getEntityResolver();
    }
    
    private void setFeatures(final Hashtable hashtable) throws SAXNotSupportedException, SAXNotRecognizedException {
        if (hashtable != null) {
            final Iterator iterator = hashtable.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry)iterator.next();
                this.xmlReader.setFeature0((String)entry.getKey(), (boolean)entry.getValue());
            }
        }
    }
    
    public Parser getParser() throws SAXException {
        return this.xmlReader;
    }
    
    public XMLReader getXMLReader() {
        return this.xmlReader;
    }
    
    public boolean isNamespaceAware() {
        try {
            return this.xmlReader.getFeature("http://xml.org/sax/features/namespaces");
        }
        catch (final SAXException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
    
    public boolean isValidating() {
        try {
            return this.xmlReader.getFeature("http://xml.org/sax/features/validation");
        }
        catch (final SAXException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
    
    public boolean isXIncludeAware() {
        try {
            return this.xmlReader.getFeature("http://apache.org/xml/features/xinclude");
        }
        catch (final SAXException ex) {
            return false;
        }
    }
    
    public void setProperty(final String s, final Object o) throws SAXNotRecognizedException, SAXNotSupportedException {
        this.xmlReader.setProperty(s, o);
    }
    
    public Object getProperty(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.xmlReader.getProperty(s);
    }
    
    public void parse(final InputSource inputSource, final DefaultHandler defaultHandler) throws SAXException, IOException {
        if (inputSource == null) {
            throw new IllegalArgumentException();
        }
        if (defaultHandler != null) {
            this.xmlReader.setContentHandler(defaultHandler);
            this.xmlReader.setEntityResolver(defaultHandler);
            this.xmlReader.setErrorHandler(defaultHandler);
            this.xmlReader.setDTDHandler(defaultHandler);
            this.xmlReader.setDocumentHandler(null);
        }
        this.xmlReader.parse(inputSource);
    }
    
    public void parse(final InputSource inputSource, final HandlerBase handlerBase) throws SAXException, IOException {
        if (inputSource == null) {
            throw new IllegalArgumentException();
        }
        if (handlerBase != null) {
            this.xmlReader.setDocumentHandler(handlerBase);
            this.xmlReader.setEntityResolver(handlerBase);
            this.xmlReader.setErrorHandler(handlerBase);
            this.xmlReader.setDTDHandler(handlerBase);
            this.xmlReader.setContentHandler(null);
        }
        this.xmlReader.parse(inputSource);
    }
    
    public Schema getSchema() {
        return this.grammar;
    }
    
    public void reset() {
        try {
            this.xmlReader.restoreInitState();
        }
        catch (final SAXException ex) {}
        this.xmlReader.setContentHandler(null);
        this.xmlReader.setDTDHandler(null);
        if (this.xmlReader.getErrorHandler() != this.fInitErrorHandler) {
            this.xmlReader.setErrorHandler(this.fInitErrorHandler);
        }
        if (this.xmlReader.getEntityResolver() != this.fInitEntityResolver) {
            this.xmlReader.setEntityResolver(this.fInitEntityResolver);
        }
    }
    
    public ElementPSVI getElementPSVI() {
        return this.xmlReader.getElementPSVI();
    }
    
    public AttributePSVI getAttributePSVI(final int n) {
        return this.xmlReader.getAttributePSVI(n);
    }
    
    public AttributePSVI getAttributePSVIByName(final String s, final String s2) {
        return this.xmlReader.getAttributePSVIByName(s, s2);
    }
    
    public static class JAXPSAXParser extends org.apache.xerces.parsers.SAXParser
    {
        private final HashMap fInitFeatures;
        private final HashMap fInitProperties;
        private final SAXParserImpl fSAXParser;
        
        public JAXPSAXParser() {
            this((SAXParserImpl)null);
        }
        
        JAXPSAXParser(final SAXParserImpl fsaxParser) {
            this.fInitFeatures = new HashMap();
            this.fInitProperties = new HashMap();
            this.fSAXParser = fsaxParser;
        }
        
        public synchronized void setFeature(final String s, final boolean b) throws SAXNotRecognizedException, SAXNotSupportedException {
            if (s == null) {
                throw new NullPointerException();
            }
            if (s.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
                try {
                    this.setProperty("http://apache.org/xml/properties/security-manager", b ? new SecurityManager() : null);
                }
                catch (final SAXNotRecognizedException ex) {
                    if (b) {
                        throw ex;
                    }
                }
                catch (final SAXNotSupportedException ex2) {
                    if (b) {
                        throw ex2;
                    }
                }
                return;
            }
            if (!this.fInitFeatures.containsKey(s)) {
                this.fInitFeatures.put(s, super.getFeature(s) ? Boolean.TRUE : Boolean.FALSE);
            }
            if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null) {
                this.setSchemaValidatorFeature(s, b);
            }
            super.setFeature(s, b);
        }
        
        public synchronized boolean getFeature(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
            if (s == null) {
                throw new NullPointerException();
            }
            if (s.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
                try {
                    return super.getProperty("http://apache.org/xml/properties/security-manager") != null;
                }
                catch (final SAXException ex) {
                    return false;
                }
            }
            return super.getFeature(s);
        }
        
        public synchronized void setProperty(final String s, final Object o) throws SAXNotRecognizedException, SAXNotSupportedException {
            if (s == null) {
                throw new NullPointerException();
            }
            if (this.fSAXParser != null) {
                if ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(s)) {
                    if (this.fSAXParser.grammar != null) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-already-specified", new Object[] { s }));
                    }
                    if ("http://www.w3.org/2001/XMLSchema".equals(o)) {
                        if (this.fSAXParser.isValidating()) {
                            this.fSAXParser.schemaLanguage = "http://www.w3.org/2001/XMLSchema";
                            this.setFeature("http://apache.org/xml/features/validation/schema", true);
                            if (!this.fInitProperties.containsKey("http://java.sun.com/xml/jaxp/properties/schemaLanguage")) {
                                this.fInitProperties.put("http://java.sun.com/xml/jaxp/properties/schemaLanguage", super.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage"));
                            }
                            super.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
                        }
                    }
                    else {
                        if (o != null) {
                            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-not-supported", null));
                        }
                        this.fSAXParser.schemaLanguage = null;
                        this.setFeature("http://apache.org/xml/features/validation/schema", false);
                    }
                    return;
                }
                else if ("http://java.sun.com/xml/jaxp/properties/schemaSource".equals(s)) {
                    if (this.fSAXParser.grammar != null) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-already-specified", new Object[] { s }));
                    }
                    final String s2 = (String)this.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
                    if (s2 != null && "http://www.w3.org/2001/XMLSchema".equals(s2)) {
                        if (!this.fInitProperties.containsKey("http://java.sun.com/xml/jaxp/properties/schemaSource")) {
                            this.fInitProperties.put("http://java.sun.com/xml/jaxp/properties/schemaSource", super.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource"));
                        }
                        super.setProperty(s, o);
                        return;
                    }
                    throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "jaxp-order-not-supported", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://java.sun.com/xml/jaxp/properties/schemaSource" }));
                }
            }
            if (!this.fInitProperties.containsKey(s)) {
                this.fInitProperties.put(s, super.getProperty(s));
            }
            if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null) {
                this.setSchemaValidatorProperty(s, o);
            }
            super.setProperty(s, o);
        }
        
        public synchronized Object getProperty(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
            if (s == null) {
                throw new NullPointerException();
            }
            if (this.fSAXParser != null && "http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(s)) {
                return this.fSAXParser.schemaLanguage;
            }
            return super.getProperty(s);
        }
        
        synchronized void restoreInitState() throws SAXNotRecognizedException, SAXNotSupportedException {
            if (!this.fInitFeatures.isEmpty()) {
                final Iterator iterator = this.fInitFeatures.entrySet().iterator();
                while (iterator.hasNext()) {
                    final Map.Entry entry = (Map.Entry)iterator.next();
                    super.setFeature((String)entry.getKey(), (boolean)entry.getValue());
                }
                this.fInitFeatures.clear();
            }
            if (!this.fInitProperties.isEmpty()) {
                final Iterator iterator2 = this.fInitProperties.entrySet().iterator();
                while (iterator2.hasNext()) {
                    final Map.Entry entry2 = (Map.Entry)iterator2.next();
                    super.setProperty((String)entry2.getKey(), entry2.getValue());
                }
                this.fInitProperties.clear();
            }
        }
        
        public void parse(final InputSource inputSource) throws SAXException, IOException {
            if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null) {
                if (this.fSAXParser.fSchemaValidationManager != null) {
                    this.fSAXParser.fSchemaValidationManager.reset();
                    this.fSAXParser.fUnparsedEntityHandler.reset();
                }
                this.resetSchemaValidator();
            }
            super.parse(inputSource);
        }
        
        public void parse(final String s) throws SAXException, IOException {
            if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null) {
                if (this.fSAXParser.fSchemaValidationManager != null) {
                    this.fSAXParser.fSchemaValidationManager.reset();
                    this.fSAXParser.fUnparsedEntityHandler.reset();
                }
                this.resetSchemaValidator();
            }
            super.parse(s);
        }
        
        XMLParserConfiguration getXMLParserConfiguration() {
            return this.fConfiguration;
        }
        
        void setFeature0(final String s, final boolean b) throws SAXNotRecognizedException, SAXNotSupportedException {
            super.setFeature(s, b);
        }
        
        boolean getFeature0(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
            return super.getFeature(s);
        }
        
        void setProperty0(final String s, final Object o) throws SAXNotRecognizedException, SAXNotSupportedException {
            super.setProperty(s, o);
        }
        
        Object getProperty0(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
            return super.getProperty(s);
        }
        
        private void setSchemaValidatorFeature(final String s, final boolean b) throws SAXNotRecognizedException, SAXNotSupportedException {
            try {
                this.fSAXParser.fSchemaValidator.setFeature(s, b);
            }
            catch (final XMLConfigurationException ex) {
                final String identifier = ex.getIdentifier();
                if (ex.getType() == 0) {
                    throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { identifier }));
                }
                throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { identifier }));
            }
        }
        
        private void setSchemaValidatorProperty(final String s, final Object o) throws SAXNotRecognizedException, SAXNotSupportedException {
            try {
                this.fSAXParser.fSchemaValidator.setProperty(s, o);
            }
            catch (final XMLConfigurationException ex) {
                final String identifier = ex.getIdentifier();
                if (ex.getType() == 0) {
                    throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { identifier }));
                }
                throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { identifier }));
            }
        }
        
        private void resetSchemaValidator() throws SAXException {
            try {
                this.fSAXParser.fSchemaValidator.reset(this.fSAXParser.fSchemaValidatorComponentManager);
            }
            catch (final XMLConfigurationException ex) {
                throw new SAXException(ex);
            }
        }
    }
}
