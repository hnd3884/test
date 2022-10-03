package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.util.Status;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
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
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
import com.sun.org.apache.xerces.internal.impl.Constants;
import org.xml.sax.SAXException;
import java.util.Map;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import javax.xml.validation.Schema;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import javax.xml.parsers.SAXParser;

public class SAXParserImpl extends SAXParser implements JAXPConstants, PSVIProvider
{
    private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
    private static final String NAMESPACE_PREFIXES_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
    private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
    private static final String XMLSCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
    private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
    private final JAXPSAXParser xmlReader;
    private String schemaLanguage;
    private final Schema grammar;
    private final XMLComponent fSchemaValidator;
    private final XMLComponentManager fSchemaValidatorComponentManager;
    private final ValidationManager fSchemaValidationManager;
    private final UnparsedEntityHandler fUnparsedEntityHandler;
    private final ErrorHandler fInitErrorHandler;
    private final EntityResolver fInitEntityResolver;
    private final XMLSecurityManager fSecurityManager;
    private final XMLSecurityPropertyManager fSecurityPropertyMgr;
    
    SAXParserImpl(final SAXParserFactoryImpl spf, final Map<String, Boolean> features) throws SAXException {
        this(spf, features, false);
    }
    
    SAXParserImpl(final SAXParserFactoryImpl spf, final Map<String, Boolean> features, final boolean secureProcessing) throws SAXException {
        this.schemaLanguage = null;
        this.fSecurityManager = new XMLSecurityManager(secureProcessing);
        this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
        (this.xmlReader = new JAXPSAXParser(this, this.fSecurityPropertyMgr, this.fSecurityManager)).setFeature0("http://xml.org/sax/features/namespaces", spf.isNamespaceAware());
        this.xmlReader.setFeature0("http://xml.org/sax/features/namespace-prefixes", !spf.isNamespaceAware());
        if (spf.isXIncludeAware()) {
            this.xmlReader.setFeature0("http://apache.org/xml/features/xinclude", true);
        }
        this.xmlReader.setProperty0("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
        this.xmlReader.setProperty0("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
        if (secureProcessing && features != null) {
            final Boolean temp = features.get("http://javax.xml.XMLConstants/feature/secure-processing");
            if (temp != null && temp && Constants.IS_JDK8_OR_ABOVE) {
                this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
                this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
            }
        }
        this.setFeatures(features);
        if (spf.isValidating()) {
            this.fInitErrorHandler = new DefaultValidationErrorHandler(this.xmlReader.getLocale());
            this.xmlReader.setErrorHandler(this.fInitErrorHandler);
        }
        else {
            this.fInitErrorHandler = this.xmlReader.getErrorHandler();
        }
        this.xmlReader.setFeature0("http://xml.org/sax/features/validation", spf.isValidating());
        this.grammar = spf.getSchema();
        if (this.grammar != null) {
            final XMLParserConfiguration config = this.xmlReader.getXMLParserConfiguration();
            XMLComponent validatorComponent = null;
            if (this.grammar instanceof XSGrammarPoolContainer) {
                validatorComponent = new XMLSchemaValidator();
                this.fSchemaValidationManager = new ValidationManager();
                config.setDTDHandler(this.fUnparsedEntityHandler = new UnparsedEntityHandler(this.fSchemaValidationManager));
                this.fUnparsedEntityHandler.setDTDHandler(this.xmlReader);
                this.xmlReader.setDTDSource(this.fUnparsedEntityHandler);
                this.fSchemaValidatorComponentManager = new SchemaValidatorConfiguration(config, (XSGrammarPoolContainer)this.grammar, this.fSchemaValidationManager);
            }
            else {
                validatorComponent = new JAXPValidatorComponent(this.grammar.newValidatorHandler());
                this.fSchemaValidationManager = null;
                this.fUnparsedEntityHandler = null;
                this.fSchemaValidatorComponentManager = config;
            }
            config.addRecognizedFeatures(validatorComponent.getRecognizedFeatures());
            config.addRecognizedProperties(validatorComponent.getRecognizedProperties());
            config.setDocumentHandler((XMLDocumentHandler)validatorComponent);
            ((XMLDocumentSource)validatorComponent).setDocumentHandler(this.xmlReader);
            this.xmlReader.setDocumentSource((XMLDocumentSource)validatorComponent);
            this.fSchemaValidator = validatorComponent;
        }
        else {
            this.fSchemaValidationManager = null;
            this.fUnparsedEntityHandler = null;
            this.fSchemaValidatorComponentManager = null;
            this.fSchemaValidator = null;
        }
        this.fInitEntityResolver = this.xmlReader.getEntityResolver();
    }
    
    private void setFeatures(final Map<String, Boolean> features) throws SAXNotSupportedException, SAXNotRecognizedException {
        if (features != null) {
            for (final Map.Entry<String, Boolean> entry : features.entrySet()) {
                this.xmlReader.setFeature0(entry.getKey(), entry.getValue());
            }
        }
    }
    
    @Override
    public Parser getParser() throws SAXException {
        return this.xmlReader;
    }
    
    @Override
    public XMLReader getXMLReader() {
        return this.xmlReader;
    }
    
    @Override
    public boolean isNamespaceAware() {
        try {
            return this.xmlReader.getFeature("http://xml.org/sax/features/namespaces");
        }
        catch (final SAXException x) {
            throw new IllegalStateException(x.getMessage());
        }
    }
    
    @Override
    public boolean isValidating() {
        try {
            return this.xmlReader.getFeature("http://xml.org/sax/features/validation");
        }
        catch (final SAXException x) {
            throw new IllegalStateException(x.getMessage());
        }
    }
    
    @Override
    public boolean isXIncludeAware() {
        try {
            return this.xmlReader.getFeature("http://apache.org/xml/features/xinclude");
        }
        catch (final SAXException exc) {
            return false;
        }
    }
    
    @Override
    public void setProperty(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        this.xmlReader.setProperty(name, value);
    }
    
    @Override
    public Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.xmlReader.getProperty(name);
    }
    
    @Override
    public void parse(final InputSource is, final DefaultHandler dh) throws SAXException, IOException {
        if (is == null) {
            throw new IllegalArgumentException();
        }
        if (dh != null) {
            this.xmlReader.setContentHandler(dh);
            this.xmlReader.setEntityResolver(dh);
            this.xmlReader.setErrorHandler(dh);
            this.xmlReader.setDTDHandler(dh);
            this.xmlReader.setDocumentHandler(null);
        }
        this.xmlReader.parse(is);
    }
    
    @Override
    public void parse(final InputSource is, final HandlerBase hb) throws SAXException, IOException {
        if (is == null) {
            throw new IllegalArgumentException();
        }
        if (hb != null) {
            this.xmlReader.setDocumentHandler(hb);
            this.xmlReader.setEntityResolver(hb);
            this.xmlReader.setErrorHandler(hb);
            this.xmlReader.setDTDHandler(hb);
            this.xmlReader.setContentHandler(null);
        }
        this.xmlReader.parse(is);
    }
    
    @Override
    public Schema getSchema() {
        return this.grammar;
    }
    
    @Override
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
    
    @Override
    public ElementPSVI getElementPSVI() {
        return this.xmlReader.getElementPSVI();
    }
    
    @Override
    public AttributePSVI getAttributePSVI(final int index) {
        return this.xmlReader.getAttributePSVI(index);
    }
    
    @Override
    public AttributePSVI getAttributePSVIByName(final String uri, final String localname) {
        return this.xmlReader.getAttributePSVIByName(uri, localname);
    }
    
    public static class JAXPSAXParser extends com.sun.org.apache.xerces.internal.parsers.SAXParser
    {
        private final HashMap fInitFeatures;
        private final HashMap fInitProperties;
        private final SAXParserImpl fSAXParser;
        private XMLSecurityManager fSecurityManager;
        private XMLSecurityPropertyManager fSecurityPropertyMgr;
        
        public JAXPSAXParser() {
            this(null, null, null);
        }
        
        JAXPSAXParser(final SAXParserImpl saxParser, final XMLSecurityPropertyManager securityPropertyMgr, final XMLSecurityManager securityManager) {
            this.fInitFeatures = new HashMap();
            this.fInitProperties = new HashMap();
            this.fSAXParser = saxParser;
            this.fSecurityManager = securityManager;
            this.fSecurityPropertyMgr = securityPropertyMgr;
            if (this.fSecurityManager == null) {
                this.fSecurityManager = new XMLSecurityManager(true);
                try {
                    super.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
                }
                catch (final SAXException e) {
                    throw new UnsupportedOperationException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { "http://apache.org/xml/properties/security-manager" }), e);
                }
            }
            if (this.fSecurityPropertyMgr == null) {
                this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
                try {
                    super.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
                }
                catch (final SAXException e) {
                    throw new UnsupportedOperationException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { "http://apache.org/xml/properties/security-manager" }), e);
                }
            }
        }
        
        @Override
        public synchronized void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
            if (name == null) {
                throw new NullPointerException();
            }
            if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
                try {
                    this.fSecurityManager.setSecureProcessing(value);
                    this.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
                }
                catch (final SAXNotRecognizedException exc) {
                    if (value) {
                        throw exc;
                    }
                }
                catch (final SAXNotSupportedException exc2) {
                    if (value) {
                        throw exc2;
                    }
                }
                return;
            }
            if (!this.fInitFeatures.containsKey(name)) {
                final boolean current = super.getFeature(name);
                this.fInitFeatures.put(name, current ? Boolean.TRUE : Boolean.FALSE);
            }
            if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null) {
                this.setSchemaValidatorFeature(name, value);
            }
            super.setFeature(name, value);
        }
        
        @Override
        public synchronized boolean getFeature(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
            if (name == null) {
                throw new NullPointerException();
            }
            if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
                return this.fSecurityManager.isSecureProcessing();
            }
            return super.getFeature(name);
        }
        
        @Override
        public synchronized void setProperty(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
            if (name == null) {
                throw new NullPointerException();
            }
            if (this.fSAXParser != null) {
                if ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(name)) {
                    if (this.fSAXParser.grammar != null) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-already-specified", new Object[] { name }));
                    }
                    if ("http://www.w3.org/2001/XMLSchema".equals(value)) {
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
                        if (value != null) {
                            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-not-supported", null));
                        }
                        this.fSAXParser.schemaLanguage = null;
                        this.setFeature("http://apache.org/xml/features/validation/schema", false);
                    }
                    return;
                }
                else if ("http://java.sun.com/xml/jaxp/properties/schemaSource".equals(name)) {
                    if (this.fSAXParser.grammar != null) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "schema-already-specified", new Object[] { name }));
                    }
                    final String val = (String)this.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
                    if (val != null && "http://www.w3.org/2001/XMLSchema".equals(val)) {
                        if (!this.fInitProperties.containsKey("http://java.sun.com/xml/jaxp/properties/schemaSource")) {
                            this.fInitProperties.put("http://java.sun.com/xml/jaxp/properties/schemaSource", super.getProperty("http://java.sun.com/xml/jaxp/properties/schemaSource"));
                        }
                        super.setProperty(name, value);
                        return;
                    }
                    throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "jaxp-order-not-supported", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://java.sun.com/xml/jaxp/properties/schemaSource" }));
                }
            }
            if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null) {
                this.setSchemaValidatorProperty(name, value);
            }
            if ((this.fSecurityManager == null || !this.fSecurityManager.setLimit(name, XMLSecurityManager.State.APIPROPERTY, value)) && (this.fSecurityPropertyMgr == null || !this.fSecurityPropertyMgr.setValue(name, XMLSecurityPropertyManager.State.APIPROPERTY, value))) {
                if (!this.fInitProperties.containsKey(name)) {
                    this.fInitProperties.put(name, super.getProperty(name));
                }
                super.setProperty(name, value);
            }
        }
        
        @Override
        public synchronized Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
            if (name == null) {
                throw new NullPointerException();
            }
            if (this.fSAXParser != null && "http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(name)) {
                return this.fSAXParser.schemaLanguage;
            }
            String propertyValue = (this.fSecurityManager != null) ? this.fSecurityManager.getLimitAsString(name) : null;
            if (propertyValue != null) {
                return propertyValue;
            }
            propertyValue = ((this.fSecurityPropertyMgr != null) ? this.fSecurityPropertyMgr.getValue(name) : null);
            if (propertyValue != null) {
                return propertyValue;
            }
            return super.getProperty(name);
        }
        
        synchronized void restoreInitState() throws SAXNotRecognizedException, SAXNotSupportedException {
            if (!this.fInitFeatures.isEmpty()) {
                for (final Map.Entry entry : this.fInitFeatures.entrySet()) {
                    final String name = entry.getKey();
                    final boolean value = entry.getValue();
                    super.setFeature(name, value);
                }
                this.fInitFeatures.clear();
            }
            if (!this.fInitProperties.isEmpty()) {
                for (final Map.Entry entry : this.fInitProperties.entrySet()) {
                    final String name = entry.getKey();
                    final Object value2 = entry.getValue();
                    super.setProperty(name, value2);
                }
                this.fInitProperties.clear();
            }
        }
        
        @Override
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
        
        @Override
        public void parse(final String systemId) throws SAXException, IOException {
            if (this.fSAXParser != null && this.fSAXParser.fSchemaValidator != null) {
                if (this.fSAXParser.fSchemaValidationManager != null) {
                    this.fSAXParser.fSchemaValidationManager.reset();
                    this.fSAXParser.fUnparsedEntityHandler.reset();
                }
                this.resetSchemaValidator();
            }
            super.parse(systemId);
        }
        
        XMLParserConfiguration getXMLParserConfiguration() {
            return this.fConfiguration;
        }
        
        void setFeature0(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
            super.setFeature(name, value);
        }
        
        boolean getFeature0(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
            return super.getFeature(name);
        }
        
        void setProperty0(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
            super.setProperty(name, value);
        }
        
        Object getProperty0(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
            return super.getProperty(name);
        }
        
        Locale getLocale() {
            return this.fConfiguration.getLocale();
        }
        
        private void setSchemaValidatorFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
            try {
                this.fSAXParser.fSchemaValidator.setFeature(name, value);
            }
            catch (final XMLConfigurationException e) {
                final String identifier = e.getIdentifier();
                if (e.getType() == Status.NOT_RECOGNIZED) {
                    throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { identifier }));
                }
                throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { identifier }));
            }
        }
        
        private void setSchemaValidatorProperty(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
            try {
                this.fSAXParser.fSchemaValidator.setProperty(name, value);
            }
            catch (final XMLConfigurationException e) {
                final String identifier = e.getIdentifier();
                if (e.getType() == Status.NOT_RECOGNIZED) {
                    throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { identifier }));
                }
                throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { identifier }));
            }
        }
        
        private void resetSchemaValidator() throws SAXException {
            try {
                this.fSAXParser.fSchemaValidator.reset(this.fSAXParser.fSchemaValidatorComponentManager);
            }
            catch (final XMLConfigurationException e) {
                throw new SAXException(e);
            }
        }
    }
}
