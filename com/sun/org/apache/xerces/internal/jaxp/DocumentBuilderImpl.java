package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import com.sun.org.apache.xerces.internal.dom.DOMImplementationImpl;
import org.w3c.dom.DOMImplementation;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import org.w3c.dom.Document;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import java.util.Iterator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.jaxp.validation.XSGrammarPoolContainer;
import com.sun.org.apache.xerces.internal.impl.Constants;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import java.util.Map;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import javax.xml.validation.Schema;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
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
    private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
    public static final String ACCESS_EXTERNAL_DTD = "http://javax.xml.XMLConstants/property/accessExternalDTD";
    public static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
    private final DOMParser domParser;
    private final Schema grammar;
    private final XMLComponent fSchemaValidator;
    private final XMLComponentManager fSchemaValidatorComponentManager;
    private final ValidationManager fSchemaValidationManager;
    private final UnparsedEntityHandler fUnparsedEntityHandler;
    private final ErrorHandler fInitErrorHandler;
    private final EntityResolver fInitEntityResolver;
    private XMLSecurityManager fSecurityManager;
    private XMLSecurityPropertyManager fSecurityPropertyMgr;
    
    DocumentBuilderImpl(final DocumentBuilderFactoryImpl dbf, final Map<String, Object> dbfAttrs, final Map<String, Boolean> features) throws SAXNotRecognizedException, SAXNotSupportedException {
        this(dbf, dbfAttrs, features, false);
    }
    
    DocumentBuilderImpl(final DocumentBuilderFactoryImpl dbf, final Map<String, Object> dbfAttrs, final Map<String, Boolean> features, final boolean secureProcessing) throws SAXNotRecognizedException, SAXNotSupportedException {
        this.domParser = new DOMParser();
        if (dbf.isValidating()) {
            this.setErrorHandler(this.fInitErrorHandler = new DefaultValidationErrorHandler(this.domParser.getXMLParserConfiguration().getLocale()));
        }
        else {
            this.fInitErrorHandler = this.domParser.getErrorHandler();
        }
        this.domParser.setFeature("http://xml.org/sax/features/validation", dbf.isValidating());
        this.domParser.setFeature("http://xml.org/sax/features/namespaces", dbf.isNamespaceAware());
        this.domParser.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", !dbf.isIgnoringElementContentWhitespace());
        this.domParser.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", !dbf.isExpandEntityReferences());
        this.domParser.setFeature("http://apache.org/xml/features/include-comments", !dbf.isIgnoringComments());
        this.domParser.setFeature("http://apache.org/xml/features/create-cdata-nodes", !dbf.isCoalescing());
        if (dbf.isXIncludeAware()) {
            this.domParser.setFeature("http://apache.org/xml/features/xinclude", true);
        }
        this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
        this.domParser.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
        this.fSecurityManager = new XMLSecurityManager(secureProcessing);
        this.domParser.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
        if (secureProcessing && features != null) {
            final Boolean temp = features.get("http://javax.xml.XMLConstants/feature/secure-processing");
            if (temp != null && temp && Constants.IS_JDK8_OR_ABOVE) {
                this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD, XMLSecurityPropertyManager.State.FSP, "");
                this.fSecurityPropertyMgr.setValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_SCHEMA, XMLSecurityPropertyManager.State.FSP, "");
            }
        }
        this.grammar = dbf.getSchema();
        if (this.grammar != null) {
            final XMLParserConfiguration config = this.domParser.getXMLParserConfiguration();
            XMLComponent validatorComponent = null;
            if (this.grammar instanceof XSGrammarPoolContainer) {
                validatorComponent = new XMLSchemaValidator();
                this.fSchemaValidationManager = new ValidationManager();
                config.setDTDHandler(this.fUnparsedEntityHandler = new UnparsedEntityHandler(this.fSchemaValidationManager));
                this.fUnparsedEntityHandler.setDTDHandler(this.domParser);
                this.domParser.setDTDSource(this.fUnparsedEntityHandler);
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
            this.setFeatures(features);
            config.setDocumentHandler((XMLDocumentHandler)validatorComponent);
            ((XMLDocumentSource)validatorComponent).setDocumentHandler(this.domParser);
            this.domParser.setDocumentSource((XMLDocumentSource)validatorComponent);
            this.fSchemaValidator = validatorComponent;
        }
        else {
            this.fSchemaValidationManager = null;
            this.fUnparsedEntityHandler = null;
            this.fSchemaValidatorComponentManager = null;
            this.fSchemaValidator = null;
            this.setFeatures(features);
        }
        this.setDocumentBuilderFactoryAttributes(dbfAttrs);
        this.fInitEntityResolver = this.domParser.getEntityResolver();
    }
    
    private void setFeatures(final Map<String, Boolean> features) throws SAXNotSupportedException, SAXNotRecognizedException {
        if (features != null) {
            for (final Map.Entry<String, Boolean> entry : features.entrySet()) {
                this.domParser.setFeature(entry.getKey(), entry.getValue());
            }
        }
    }
    
    private void setDocumentBuilderFactoryAttributes(final Map<String, Object> dbfAttrs) throws SAXNotSupportedException, SAXNotRecognizedException {
        if (dbfAttrs == null) {
            return;
        }
        for (final Map.Entry<String, Object> entry : dbfAttrs.entrySet()) {
            final String name = entry.getKey();
            final Object val = entry.getValue();
            if (val instanceof Boolean) {
                this.domParser.setFeature(name, (boolean)val);
            }
            else if ("http://java.sun.com/xml/jaxp/properties/schemaLanguage".equals(name)) {
                if (!"http://www.w3.org/2001/XMLSchema".equals(val) || !this.isValidating()) {
                    continue;
                }
                this.domParser.setFeature("http://apache.org/xml/features/validation/schema", true);
                this.domParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
            }
            else if ("http://java.sun.com/xml/jaxp/properties/schemaSource".equals(name)) {
                if (!this.isValidating()) {
                    continue;
                }
                final String value = dbfAttrs.get("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
                if (value == null || !"http://www.w3.org/2001/XMLSchema".equals(value)) {
                    throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "jaxp-order-not-supported", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://java.sun.com/xml/jaxp/properties/schemaSource" }));
                }
                this.domParser.setProperty(name, val);
            }
            else {
                if ((this.fSecurityManager != null && this.fSecurityManager.setLimit(name, XMLSecurityManager.State.APIPROPERTY, val)) || (this.fSecurityPropertyMgr != null && this.fSecurityPropertyMgr.setValue(name, XMLSecurityPropertyManager.State.APIPROPERTY, val))) {
                    continue;
                }
                this.domParser.setProperty(name, val);
            }
        }
    }
    
    @Override
    public Document newDocument() {
        return new DocumentImpl();
    }
    
    @Override
    public DOMImplementation getDOMImplementation() {
        return DOMImplementationImpl.getDOMImplementation();
    }
    
    @Override
    public Document parse(final InputSource is) throws SAXException, IOException {
        if (is == null) {
            throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "jaxp-null-input-source", null));
        }
        if (this.fSchemaValidator != null) {
            if (this.fSchemaValidationManager != null) {
                this.fSchemaValidationManager.reset();
                this.fUnparsedEntityHandler.reset();
            }
            this.resetSchemaValidator();
        }
        this.domParser.parse(is);
        final Document doc = this.domParser.getDocument();
        this.domParser.dropDocumentReferences();
        return doc;
    }
    
    @Override
    public boolean isNamespaceAware() {
        try {
            return this.domParser.getFeature("http://xml.org/sax/features/namespaces");
        }
        catch (final SAXException x) {
            throw new IllegalStateException(x.getMessage());
        }
    }
    
    @Override
    public boolean isValidating() {
        try {
            return this.domParser.getFeature("http://xml.org/sax/features/validation");
        }
        catch (final SAXException x) {
            throw new IllegalStateException(x.getMessage());
        }
    }
    
    @Override
    public boolean isXIncludeAware() {
        try {
            return this.domParser.getFeature("http://apache.org/xml/features/xinclude");
        }
        catch (final SAXException exc) {
            return false;
        }
    }
    
    @Override
    public void setEntityResolver(final EntityResolver er) {
        this.domParser.setEntityResolver(er);
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler eh) {
        this.domParser.setErrorHandler(eh);
    }
    
    @Override
    public Schema getSchema() {
        return this.grammar;
    }
    
    @Override
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
        catch (final XMLConfigurationException e) {
            throw new SAXException(e);
        }
    }
}
