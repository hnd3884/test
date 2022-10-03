package org.apache.xerces.jaxp;

import org.apache.xerces.xni.parser.XMLConfigurationException;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.w3c.dom.DOMImplementation;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.apache.xerces.dom.DOMMessageFormatter;
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
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import java.util.Hashtable;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLComponent;
import javax.xml.validation.Schema;
import org.apache.xerces.parsers.DOMParser;
import javax.xml.parsers.DocumentBuilder;

public class DocumentBuilderImpl extends DocumentBuilder implements JAXPConstants
{
    private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
    private static final String INCLUDE_IGNORABLE_WHITESPACE = "http://apache.org/xml/features/dom/include-ignorable-whitespace";
    private static final String CREATE_ENTITY_REF_NODES_FEATURE = "http://apache.org/xml/features/dom/create-entity-ref-nodes";
    private static final String INCLUDE_COMMENTS_FEATURE = "http://apache.org/xml/features/include-comments";
    private static final String CREATE_CDATA_NODES_FEATURE = "http://apache.org/xml/features/create-cdata-nodes";
    private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
    private static final String XMLSCHEMA_VALIDATION_FEATURE = "http://apache.org/xml/features/validation/schema";
    private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String XML_SCHEMA_VERSION = "http://apache.org/xml/properties/validation/schema/version";
    private final DOMParser domParser;
    private final Schema grammar;
    private final XMLComponent fSchemaValidator;
    private final XMLComponentManager fSchemaValidatorComponentManager;
    private final ValidationManager fSchemaValidationManager;
    private final UnparsedEntityHandler fUnparsedEntityHandler;
    private final ErrorHandler fInitErrorHandler;
    private final EntityResolver fInitEntityResolver;
    
    DocumentBuilderImpl(final DocumentBuilderFactoryImpl documentBuilderFactoryImpl, final Hashtable hashtable, final Hashtable hashtable2) throws SAXNotRecognizedException, SAXNotSupportedException {
        this(documentBuilderFactoryImpl, hashtable, hashtable2, false);
    }
    
    DocumentBuilderImpl(final DocumentBuilderFactoryImpl documentBuilderFactoryImpl, final Hashtable documentBuilderFactoryAttributes, final Hashtable features, final boolean b) throws SAXNotRecognizedException, SAXNotSupportedException {
        this.domParser = new DOMParser();
        if (documentBuilderFactoryImpl.isValidating()) {
            this.setErrorHandler(this.fInitErrorHandler = new DefaultValidationErrorHandler());
        }
        else {
            this.fInitErrorHandler = this.domParser.getErrorHandler();
        }
        this.domParser.setFeature("http://xml.org/sax/features/validation", documentBuilderFactoryImpl.isValidating());
        this.domParser.setFeature("http://xml.org/sax/features/namespaces", documentBuilderFactoryImpl.isNamespaceAware());
        this.domParser.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", !documentBuilderFactoryImpl.isIgnoringElementContentWhitespace());
        this.domParser.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", !documentBuilderFactoryImpl.isExpandEntityReferences());
        this.domParser.setFeature("http://apache.org/xml/features/include-comments", !documentBuilderFactoryImpl.isIgnoringComments());
        this.domParser.setFeature("http://apache.org/xml/features/create-cdata-nodes", !documentBuilderFactoryImpl.isCoalescing());
        if (documentBuilderFactoryImpl.isXIncludeAware()) {
            this.domParser.setFeature("http://apache.org/xml/features/xinclude", true);
        }
        if (b) {
            this.domParser.setProperty("http://apache.org/xml/properties/security-manager", new SecurityManager());
        }
        this.grammar = documentBuilderFactoryImpl.getSchema();
        if (this.grammar != null) {
            final XMLParserConfiguration xmlParserConfiguration = this.domParser.getXMLParserConfiguration();
            XMLComponent fSchemaValidator;
            if (this.grammar instanceof XSGrammarPoolContainer) {
                final String xmlSchemaVersion = ((XSGrammarPoolContainer)this.grammar).getXMLSchemaVersion();
                fSchemaValidator = new XMLSchemaValidator();
                if (Constants.W3C_XML_SCHEMA11_NS_URI.equals(xmlSchemaVersion)) {
                    fSchemaValidator.setProperty("http://apache.org/xml/properties/validation/schema/version", Constants.W3C_XML_SCHEMA11_NS_URI);
                }
                this.fSchemaValidationManager = new ValidationManager();
                xmlParserConfiguration.setDTDHandler(this.fUnparsedEntityHandler = new UnparsedEntityHandler(this.fSchemaValidationManager));
                this.fUnparsedEntityHandler.setDTDHandler(this.domParser);
                this.domParser.setDTDSource(this.fUnparsedEntityHandler);
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
            ((XMLDocumentSource)fSchemaValidator).setDocumentHandler(this.domParser);
            this.domParser.setDocumentSource((XMLDocumentSource)fSchemaValidator);
            this.fSchemaValidator = fSchemaValidator;
        }
        else {
            this.fSchemaValidationManager = null;
            this.fUnparsedEntityHandler = null;
            this.fSchemaValidatorComponentManager = null;
            this.fSchemaValidator = null;
        }
        this.setFeatures(features);
        this.setDocumentBuilderFactoryAttributes(documentBuilderFactoryAttributes);
        this.fInitEntityResolver = this.domParser.getEntityResolver();
    }
    
    private void setFeatures(final Hashtable hashtable) throws SAXNotSupportedException, SAXNotRecognizedException {
        if (hashtable != null) {
            final Iterator iterator = hashtable.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry entry = (Map.Entry)iterator.next();
                this.domParser.setFeature((String)entry.getKey(), (boolean)entry.getValue());
            }
        }
    }
    
    private void setDocumentBuilderFactoryAttributes(final Hashtable hashtable) throws SAXNotSupportedException, SAXNotRecognizedException {
        if (hashtable == null) {
            return;
        }
        final Iterator iterator = hashtable.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry entry = (Map.Entry)iterator.next();
            final String s = (String)entry.getKey();
            final Object value = entry.getValue();
            if (value instanceof Boolean) {
                this.domParser.setFeature(s, (boolean)value);
            }
            else if ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(s)) {
                if (!"http://www.w3.org/2001/XMLSchema".equals(value) || !this.isValidating()) {
                    continue;
                }
                this.domParser.setFeature("http://apache.org/xml/features/validation/schema", true);
                this.domParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            }
            else if ("http://java.sun.com/xml/jaxp/properties/schemaSource".equals(s)) {
                if (!this.isValidating()) {
                    continue;
                }
                final String s2 = hashtable.get("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
                if (s2 == null || !"http://www.w3.org/2001/XMLSchema".equals(s2)) {
                    throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "jaxp-order-not-supported", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://java.sun.com/xml/jaxp/properties/schemaSource" }));
                }
                this.domParser.setProperty(s, value);
            }
            else {
                this.domParser.setProperty(s, value);
            }
        }
    }
    
    public Document newDocument() {
        return new DocumentImpl();
    }
    
    public DOMImplementation getDOMImplementation() {
        return DOMImplementationImpl.getDOMImplementation();
    }
    
    public Document parse(final InputSource inputSource) throws SAXException, IOException {
        if (inputSource == null) {
            throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "jaxp-null-input-source", null));
        }
        if (this.fSchemaValidator != null) {
            if (this.fSchemaValidationManager != null) {
                this.fSchemaValidationManager.reset();
                this.fUnparsedEntityHandler.reset();
            }
            this.resetSchemaValidator();
        }
        this.domParser.parse(inputSource);
        final Document document = this.domParser.getDocument();
        this.domParser.dropDocumentReferences();
        return document;
    }
    
    public boolean isNamespaceAware() {
        try {
            return this.domParser.getFeature("http://xml.org/sax/features/namespaces");
        }
        catch (final SAXException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
    
    public boolean isValidating() {
        try {
            return this.domParser.getFeature("http://xml.org/sax/features/validation");
        }
        catch (final SAXException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
    
    public boolean isXIncludeAware() {
        try {
            return this.domParser.getFeature("http://apache.org/xml/features/xinclude");
        }
        catch (final SAXException ex) {
            return false;
        }
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        this.domParser.setEntityResolver(entityResolver);
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.domParser.setErrorHandler(errorHandler);
    }
    
    public Schema getSchema() {
        return this.grammar;
    }
    
    public void reset() {
        if (this.domParser.getErrorHandler() != this.fInitErrorHandler) {
            this.domParser.setErrorHandler(this.fInitErrorHandler);
        }
        if (this.domParser.getEntityResolver() != this.fInitEntityResolver) {
            this.domParser.setEntityResolver(this.fInitEntityResolver);
        }
    }
    
    DOMParser getDOMParser() {
        return this.domParser;
    }
    
    private void resetSchemaValidator() throws SAXException {
        try {
            this.fSchemaValidator.reset(this.fSchemaValidatorComponentManager);
        }
        catch (final XMLConfigurationException ex) {
            throw new SAXException(ex);
        }
    }
}
