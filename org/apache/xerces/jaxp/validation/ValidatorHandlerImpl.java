package org.apache.xerces.jaxp.validation;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.ItemPSVI;
import org.w3c.dom.TypeInfo;
import org.apache.xerces.util.URI;
import org.apache.xerces.impl.XMLEntityManager;
import java.io.InputStream;
import org.w3c.dom.ls.LSInput;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.InputSource;
import org.xml.sax.ext.EntityResolver2;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.ElementPSVI;
import java.io.IOException;
import org.xml.sax.XMLReader;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.FactoryConfigurationError;
import org.apache.xerces.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.xml.sax.ext.Attributes2;
import org.apache.xerces.xni.parser.XMLParseException;
import org.xml.sax.Locator;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.xml.sax.Attributes;
import org.apache.xerces.util.XMLSymbols;
import org.xml.sax.SAXException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.apache.xerces.util.SAXMessageFormatter;
import javax.xml.validation.TypeInfoProvider;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.xni.XMLAttributes;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.util.AttributesProxy;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import java.util.HashMap;
import org.apache.xerces.util.SAXLocatorWrapper;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.xs.XMLSchemaValidator;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xs.PSVIProvider;
import org.xml.sax.ext.LexicalHandler;
import org.apache.xerces.impl.validation.EntityState;
import org.xml.sax.DTDHandler;
import javax.xml.validation.ValidatorHandler;

final class ValidatorHandlerImpl extends ValidatorHandler implements DTDHandler, EntityState, LexicalHandler, PSVIProvider, ValidatorHelper, XMLDocumentHandler
{
    private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    private static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
    private static final String STRINGS_INTERNED = "http://apache.org/xml/features/internal/strings-interned";
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
    private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
    private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private static final String XML_SCHEMA_VERSION = "http://apache.org/xml/properties/validation/schema/version";
    private final XMLErrorReporter fErrorReporter;
    private final NamespaceContext fNamespaceContext;
    private final XMLSchemaValidator fSchemaValidator;
    private final SymbolTable fSymbolTable;
    private final ValidationManager fValidationManager;
    private final XMLSchemaValidatorComponentManager fComponentManager;
    private final SAXLocatorWrapper fSAXLocatorWrapper;
    private boolean fNeedPushNSContext;
    private HashMap fUnparsedEntities;
    private boolean fStringsInternalized;
    private final QName fElementQName;
    private final QName fAttributeQName;
    private final XMLAttributesImpl fAttributes;
    private final AttributesProxy fAttrAdapter;
    private final XMLString fTempString;
    private final boolean fIsXSD11;
    private ContentHandler fContentHandler;
    private LexicalHandler fLexicalHandler;
    private final XMLSchemaTypeInfoProvider fTypeInfoProvider;
    private final ResolutionForwarder fResolutionForwarder;
    
    public ValidatorHandlerImpl(final XSGrammarPoolContainer xsGrammarPoolContainer) {
        this(new XMLSchemaValidatorComponentManager(xsGrammarPoolContainer));
        this.fComponentManager.addRecognizedFeatures(new String[] { "http://xml.org/sax/features/namespace-prefixes" });
        this.fComponentManager.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
        this.setErrorHandler(null);
        this.setResourceResolver(null);
    }
    
    public ValidatorHandlerImpl(final XMLSchemaValidatorComponentManager fComponentManager) {
        this.fSAXLocatorWrapper = new SAXLocatorWrapper();
        this.fNeedPushNSContext = true;
        this.fUnparsedEntities = null;
        this.fStringsInternalized = false;
        this.fElementQName = new QName();
        this.fAttributeQName = new QName();
        this.fAttributes = new XMLAttributesImpl();
        this.fAttrAdapter = new AttributesProxy(this.fAttributes);
        this.fTempString = new XMLString();
        this.fContentHandler = null;
        this.fLexicalHandler = null;
        this.fTypeInfoProvider = new XMLSchemaTypeInfoProvider();
        this.fResolutionForwarder = new ResolutionForwarder(null);
        this.fComponentManager = fComponentManager;
        this.fErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fNamespaceContext = (NamespaceContext)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context");
        this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema");
        this.fSymbolTable = (SymbolTable)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fValidationManager = (ValidationManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
        this.fIsXSD11 = Constants.W3C_XML_SCHEMA11_NS_URI.equals(this.fComponentManager.getProperty("http://apache.org/xml/properties/validation/schema/version"));
    }
    
    public void setContentHandler(final ContentHandler fContentHandler) {
        this.fContentHandler = fContentHandler;
    }
    
    public ContentHandler getContentHandler() {
        return this.fContentHandler;
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.fComponentManager.setErrorHandler(errorHandler);
    }
    
    public ErrorHandler getErrorHandler() {
        return this.fComponentManager.getErrorHandler();
    }
    
    public void setResourceResolver(final LSResourceResolver resourceResolver) {
        this.fComponentManager.setResourceResolver(resourceResolver);
    }
    
    public LSResourceResolver getResourceResolver() {
        return this.fComponentManager.getResourceResolver();
    }
    
    public TypeInfoProvider getTypeInfoProvider() {
        return this.fTypeInfoProvider;
    }
    
    public boolean getFeature(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "FeatureNameNull", null));
        }
        if ("http://apache.org/xml/features/internal/strings-interned".equals(s)) {
            return this.fStringsInternalized;
        }
        try {
            return this.fComponentManager.getFeature(s);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-supported", new Object[] { identifier }));
        }
    }
    
    public void setFeature(final String s, final boolean fStringsInternalized) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "FeatureNameNull", null));
        }
        if ("http://apache.org/xml/features/internal/strings-interned".equals(s)) {
            this.fStringsInternalized = fStringsInternalized;
            return;
        }
        try {
            this.fComponentManager.setFeature(s, fStringsInternalized);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-supported", new Object[] { identifier }));
        }
    }
    
    public Object getProperty(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "ProperyNameNull", null));
        }
        try {
            return this.fComponentManager.getProperty(s);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-supported", new Object[] { identifier }));
        }
    }
    
    public void setProperty(final String s, final Object o) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (s == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "ProperyNameNull", null));
        }
        if ("http://apache.org/xml/properties/validation/schema/version".equals(s)) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-read-only", new Object[] { s }));
        }
        try {
            this.fComponentManager.setProperty(s, o);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-supported", new Object[] { identifier }));
        }
    }
    
    public boolean isEntityDeclared(final String s) {
        return false;
    }
    
    public boolean isEntityUnparsed(final String s) {
        return this.fUnparsedEntities != null && this.fUnparsedEntities.containsKey(s);
    }
    
    public void startDocument(final XMLLocator xmlLocator, final String s, final NamespaceContext namespaceContext, final Augmentations augmentations) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fContentHandler.startDocument();
            }
            catch (final SAXException ex) {
                throw new XNIException(ex);
            }
        }
    }
    
    public void xmlDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
    }
    
    public void doctypeDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fTypeInfoProvider.beginStartElement(augmentations, xmlAttributes);
                this.fContentHandler.startElement((qName.uri != null) ? qName.uri : XMLSymbols.EMPTY_STRING, qName.localpart, qName.rawname, this.fAttrAdapter);
            }
            catch (final SAXException ex) {
                throw new XNIException(ex);
            }
            finally {
                this.fTypeInfoProvider.finishStartElement();
            }
        }
    }
    
    public void emptyElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        this.startElement(qName, xmlAttributes, augmentations);
        this.endElement(qName, augmentations);
    }
    
    public void startGeneralEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
    }
    
    public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
    }
    
    public void endGeneralEntity(final String s, final Augmentations augmentations) throws XNIException {
    }
    
    public void characters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fContentHandler != null) {
            if (xmlString.length == 0) {
                return;
            }
            try {
                this.fContentHandler.characters(xmlString.ch, xmlString.offset, xmlString.length);
            }
            catch (final SAXException ex) {
                throw new XNIException(ex);
            }
        }
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fContentHandler.ignorableWhitespace(xmlString.ch, xmlString.offset, xmlString.length);
            }
            catch (final SAXException ex) {
                throw new XNIException(ex);
            }
        }
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fTypeInfoProvider.beginEndElement(augmentations);
                this.fContentHandler.endElement((qName.uri != null) ? qName.uri : XMLSymbols.EMPTY_STRING, qName.localpart, qName.rawname);
            }
            catch (final SAXException ex) {
                throw new XNIException(ex);
            }
            finally {
                this.fTypeInfoProvider.finishEndElement();
            }
        }
    }
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
    }
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
    }
    
    public void endDocument(final Augmentations augmentations) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fContentHandler.endDocument();
            }
            catch (final SAXException ex) {
                throw new XNIException(ex);
            }
        }
    }
    
    public void setDocumentSource(final XMLDocumentSource xmlDocumentSource) {
    }
    
    public XMLDocumentSource getDocumentSource() {
        return this.fSchemaValidator;
    }
    
    public void setDocumentLocator(final Locator locator) {
        this.fSAXLocatorWrapper.setLocator(locator);
        if (this.fContentHandler != null) {
            this.fContentHandler.setDocumentLocator(locator);
        }
    }
    
    public void startDocument() throws SAXException {
        this.fComponentManager.reset();
        this.fSchemaValidator.setDocumentHandler(this);
        this.fValidationManager.setEntityState(this);
        this.fTypeInfoProvider.finishStartElement();
        this.fNeedPushNSContext = true;
        if (this.fUnparsedEntities != null && !this.fUnparsedEntities.isEmpty()) {
            this.fUnparsedEntities.clear();
        }
        this.fErrorReporter.setDocumentLocator(this.fSAXLocatorWrapper);
        try {
            this.fSchemaValidator.startDocument(this.fSAXLocatorWrapper, this.fSAXLocatorWrapper.getEncoding(), this.fNamespaceContext, null);
        }
        catch (final XMLParseException ex) {
            throw Util.toSAXParseException(ex);
        }
        catch (final XNIException ex2) {
            throw Util.toSAXException(ex2);
        }
    }
    
    public void endDocument() throws SAXException {
        this.fSAXLocatorWrapper.setLocator(null);
        try {
            this.fSchemaValidator.endDocument(null);
        }
        catch (final XMLParseException ex) {
            throw Util.toSAXParseException(ex);
        }
        catch (final XNIException ex2) {
            throw Util.toSAXException(ex2);
        }
    }
    
    public void startPrefixMapping(final String s, final String s2) throws SAXException {
        String s3;
        String s4;
        if (!this.fStringsInternalized) {
            s3 = ((s != null) ? this.fSymbolTable.addSymbol(s) : XMLSymbols.EMPTY_STRING);
            s4 = ((s2 != null && s2.length() > 0) ? this.fSymbolTable.addSymbol(s2) : null);
        }
        else {
            s3 = ((s != null) ? s : XMLSymbols.EMPTY_STRING);
            s4 = ((s2 != null && s2.length() > 0) ? s2 : null);
        }
        if (this.fNeedPushNSContext) {
            this.fNeedPushNSContext = false;
            this.fNamespaceContext.pushContext();
        }
        this.fNamespaceContext.declarePrefix(s3, s4);
        if (this.fContentHandler != null) {
            this.fContentHandler.startPrefixMapping(s, s2);
        }
    }
    
    public void endPrefixMapping(final String s) throws SAXException {
        if (this.fContentHandler != null) {
            this.fContentHandler.endPrefixMapping(s);
        }
    }
    
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
        if (this.fNeedPushNSContext) {
            this.fNamespaceContext.pushContext();
        }
        this.fNeedPushNSContext = true;
        this.fillQName(this.fElementQName, s, s2, s3);
        if (attributes instanceof Attributes2) {
            this.fillXMLAttributes2((Attributes2)attributes);
        }
        else {
            this.fillXMLAttributes(attributes);
        }
        try {
            this.fSchemaValidator.startElement(this.fElementQName, this.fAttributes, null);
        }
        catch (final XMLParseException ex) {
            throw Util.toSAXParseException(ex);
        }
        catch (final XNIException ex2) {
            throw Util.toSAXException(ex2);
        }
    }
    
    public void endElement(final String s, final String s2, final String s3) throws SAXException {
        this.fillQName(this.fElementQName, s, s2, s3);
        try {
            this.fSchemaValidator.endElement(this.fElementQName, null);
        }
        catch (final XMLParseException ex) {
            throw Util.toSAXParseException(ex);
        }
        catch (final XNIException ex2) {
            throw Util.toSAXException(ex2);
        }
        finally {
            this.fNamespaceContext.popContext();
        }
    }
    
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
        try {
            this.fTempString.setValues(array, n, n2);
            this.fSchemaValidator.characters(this.fTempString, null);
        }
        catch (final XMLParseException ex) {
            throw Util.toSAXParseException(ex);
        }
        catch (final XNIException ex2) {
            throw Util.toSAXException(ex2);
        }
    }
    
    public void ignorableWhitespace(final char[] array, final int n, final int n2) throws SAXException {
        try {
            this.fTempString.setValues(array, n, n2);
            this.fSchemaValidator.ignorableWhitespace(this.fTempString, null);
        }
        catch (final XMLParseException ex) {
            throw Util.toSAXParseException(ex);
        }
        catch (final XNIException ex2) {
            throw Util.toSAXException(ex2);
        }
    }
    
    public void processingInstruction(final String s, final String s2) throws SAXException {
        if (this.fIsXSD11) {
            if (s2 != null) {
                this.fTempString.setValues(s2.toCharArray(), 0, s2.length());
            }
            else {
                this.fTempString.setValues(new char[0], 0, 0);
            }
            this.fSchemaValidator.processingInstruction(s, this.fTempString, null);
        }
        if (this.fContentHandler != null) {
            this.fContentHandler.processingInstruction(s, s2);
        }
    }
    
    public void skippedEntity(final String s) throws SAXException {
        if (this.fContentHandler != null) {
            this.fContentHandler.skippedEntity(s);
        }
    }
    
    public void notationDecl(final String s, final String s2, final String s3) throws SAXException {
    }
    
    public void unparsedEntityDecl(final String s, final String s2, final String s3, final String s4) throws SAXException {
        if (this.fUnparsedEntities == null) {
            this.fUnparsedEntities = new HashMap();
        }
        this.fUnparsedEntities.put(s, s);
    }
    
    public void comment(final char[] array, final int n, final int n2) throws SAXException {
        if (this.fIsXSD11) {
            this.fTempString.setValues(array, n, n2);
            this.fSchemaValidator.comment(this.fTempString, null);
        }
        if (this.fLexicalHandler != null) {
            this.fLexicalHandler.comment(array, n, n2);
        }
    }
    
    public void endCDATA() throws SAXException {
        if (this.fLexicalHandler != null) {
            this.fLexicalHandler.endCDATA();
        }
    }
    
    public void endDTD() throws SAXException {
        if (this.fLexicalHandler != null) {
            this.fLexicalHandler.endDTD();
        }
    }
    
    public void endEntity(final String s) throws SAXException {
        if (this.fLexicalHandler != null) {
            this.fLexicalHandler.endEntity(s);
        }
    }
    
    public void startCDATA() throws SAXException {
        if (this.fLexicalHandler != null) {
            this.fLexicalHandler.startCDATA();
        }
    }
    
    public void startDTD(final String s, final String s2, final String s3) throws SAXException {
        if (this.fLexicalHandler != null) {
            this.fLexicalHandler.startDTD(s, s2, s3);
        }
    }
    
    public void startEntity(final String s) throws SAXException {
        if (this.fLexicalHandler != null) {
            this.fLexicalHandler.startEntity(s);
        }
    }
    
    public void validate(final Source source, final Result result) throws SAXException, IOException {
        if (result instanceof SAXResult || result == null) {
            final SAXSource saxSource = (SAXSource)source;
            final SAXResult saxResult = (SAXResult)result;
            LexicalHandler lexicalHandler = null;
            if (result != null) {
                final ContentHandler handler = saxResult.getHandler();
                lexicalHandler = saxResult.getLexicalHandler();
                if (lexicalHandler == null && handler instanceof LexicalHandler) {
                    lexicalHandler = (LexicalHandler)handler;
                }
                this.setContentHandler(handler);
                this.fLexicalHandler = lexicalHandler;
            }
            XMLReader xmlReader = null;
            try {
                xmlReader = saxSource.getXMLReader();
                if (xmlReader == null) {
                    final SAXParserFactory instance = SAXParserFactory.newInstance();
                    instance.setNamespaceAware(true);
                    try {
                        xmlReader = instance.newSAXParser().getXMLReader();
                        if (xmlReader instanceof SAXParser) {
                            final Object property = this.fComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
                            if (property != null) {
                                try {
                                    xmlReader.setProperty("http://apache.org/xml/properties/security-manager", property);
                                }
                                catch (final SAXException ex) {}
                            }
                        }
                    }
                    catch (final Exception ex2) {
                        throw new FactoryConfigurationError(ex2);
                    }
                }
                try {
                    this.fStringsInternalized = xmlReader.getFeature("http://xml.org/sax/features/string-interning");
                }
                catch (final SAXException ex3) {
                    this.fStringsInternalized = false;
                }
                final ErrorHandler errorHandler = this.fComponentManager.getErrorHandler();
                xmlReader.setErrorHandler((errorHandler != null) ? errorHandler : DraconianErrorHandler.getInstance());
                xmlReader.setEntityResolver(this.fResolutionForwarder);
                this.fResolutionForwarder.setEntityResolver(this.fComponentManager.getResourceResolver());
                xmlReader.setContentHandler(this);
                xmlReader.setDTDHandler(this);
                try {
                    xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", this.fIsXSD11 ? this : lexicalHandler);
                }
                catch (final SAXException ex4) {}
                xmlReader.parse(saxSource.getInputSource());
            }
            finally {
                this.setContentHandler(null);
                this.fLexicalHandler = null;
                if (xmlReader != null) {
                    try {
                        xmlReader.setContentHandler(null);
                        xmlReader.setDTDHandler(null);
                        xmlReader.setErrorHandler(null);
                        xmlReader.setEntityResolver(null);
                        this.fResolutionForwarder.setEntityResolver(null);
                        xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", null);
                    }
                    catch (final Exception ex5) {}
                }
            }
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { source.getClass().getName(), result.getClass().getName() }));
    }
    
    public ElementPSVI getElementPSVI() {
        return this.fTypeInfoProvider.getElementPSVI();
    }
    
    public AttributePSVI getAttributePSVI(final int n) {
        return this.fTypeInfoProvider.getAttributePSVI(n);
    }
    
    public AttributePSVI getAttributePSVIByName(final String s, final String s2) {
        return this.fTypeInfoProvider.getAttributePSVIByName(s, s2);
    }
    
    private void fillQName(final QName qName, String s, String empty_STRING, String empty_STRING2) {
        if (!this.fStringsInternalized) {
            s = ((s != null && s.length() > 0) ? this.fSymbolTable.addSymbol(s) : null);
            empty_STRING = ((empty_STRING != null) ? this.fSymbolTable.addSymbol(empty_STRING) : XMLSymbols.EMPTY_STRING);
            empty_STRING2 = ((empty_STRING2 != null) ? this.fSymbolTable.addSymbol(empty_STRING2) : XMLSymbols.EMPTY_STRING);
        }
        else {
            if (s != null && s.length() == 0) {
                s = null;
            }
            if (empty_STRING == null) {
                empty_STRING = XMLSymbols.EMPTY_STRING;
            }
            if (empty_STRING2 == null) {
                empty_STRING2 = XMLSymbols.EMPTY_STRING;
            }
        }
        String s2 = XMLSymbols.EMPTY_STRING;
        final int index = empty_STRING2.indexOf(58);
        if (index != -1) {
            s2 = this.fSymbolTable.addSymbol(empty_STRING2.substring(0, index));
        }
        qName.setValues(s2, empty_STRING, empty_STRING2, s);
    }
    
    private void fillXMLAttributes(final Attributes attributes) {
        this.fAttributes.removeAllAttributes();
        for (int length = attributes.getLength(), i = 0; i < length; ++i) {
            this.fillXMLAttribute(attributes, i);
            this.fAttributes.setSpecified(i, true);
        }
    }
    
    private void fillXMLAttributes2(final Attributes2 attributes2) {
        this.fAttributes.removeAllAttributes();
        for (int length = attributes2.getLength(), i = 0; i < length; ++i) {
            this.fillXMLAttribute(attributes2, i);
            this.fAttributes.setSpecified(i, attributes2.isSpecified(i));
            if (attributes2.isDeclared(i)) {
                this.fAttributes.getAugmentations(i).putItem("ATTRIBUTE_DECLARED", Boolean.TRUE);
            }
        }
    }
    
    private void fillXMLAttribute(final Attributes attributes, final int n) {
        this.fillQName(this.fAttributeQName, attributes.getURI(n), attributes.getLocalName(n), attributes.getQName(n));
        final String type = attributes.getType(n);
        this.fAttributes.addAttributeNS(this.fAttributeQName, (type != null) ? type : XMLSymbols.fCDATASymbol, attributes.getValue(n));
    }
    
    static final class ResolutionForwarder implements EntityResolver2
    {
        private static final String XML_TYPE = "http://www.w3.org/TR/REC-xml";
        protected LSResourceResolver fEntityResolver;
        
        public ResolutionForwarder() {
        }
        
        public ResolutionForwarder(final LSResourceResolver entityResolver) {
            this.setEntityResolver(entityResolver);
        }
        
        public void setEntityResolver(final LSResourceResolver fEntityResolver) {
            this.fEntityResolver = fEntityResolver;
        }
        
        public LSResourceResolver getEntityResolver() {
            return this.fEntityResolver;
        }
        
        public InputSource getExternalSubset(final String s, final String s2) throws SAXException, IOException {
            return null;
        }
        
        public InputSource resolveEntity(final String s, final String s2, final String s3, final String s4) throws SAXException, IOException {
            if (this.fEntityResolver != null) {
                final LSInput resolveResource = this.fEntityResolver.resolveResource("http://www.w3.org/TR/REC-xml", null, s2, s4, s3);
                if (resolveResource != null) {
                    final String publicId = resolveResource.getPublicId();
                    final String systemId = resolveResource.getSystemId();
                    final String baseURI = resolveResource.getBaseURI();
                    final Reader characterStream = resolveResource.getCharacterStream();
                    final InputStream byteStream = resolveResource.getByteStream();
                    final String stringData = resolveResource.getStringData();
                    final String encoding = resolveResource.getEncoding();
                    final InputSource inputSource = new InputSource();
                    inputSource.setPublicId(publicId);
                    inputSource.setSystemId((baseURI != null) ? this.resolveSystemId(systemId, baseURI) : systemId);
                    if (characterStream != null) {
                        inputSource.setCharacterStream(characterStream);
                    }
                    else if (byteStream != null) {
                        inputSource.setByteStream(byteStream);
                    }
                    else if (stringData != null && stringData.length() != 0) {
                        inputSource.setCharacterStream(new StringReader(stringData));
                    }
                    inputSource.setEncoding(encoding);
                    return inputSource;
                }
            }
            return null;
        }
        
        public InputSource resolveEntity(final String s, final String s2) throws SAXException, IOException {
            return this.resolveEntity(null, s, null, s2);
        }
        
        private String resolveSystemId(final String s, final String s2) {
            try {
                return XMLEntityManager.expandSystemId(s, s2, false);
            }
            catch (final URI.MalformedURIException ex) {
                return s;
            }
        }
    }
    
    private class XMLSchemaTypeInfoProvider extends TypeInfoProvider
    {
        private Augmentations fElementAugs;
        private XMLAttributes fAttributes;
        private boolean fInStartElement;
        private boolean fInEndElement;
        
        private XMLSchemaTypeInfoProvider() {
            this.fInStartElement = false;
            this.fInEndElement = false;
        }
        
        void beginStartElement(final Augmentations fElementAugs, final XMLAttributes fAttributes) {
            this.fInStartElement = true;
            this.fElementAugs = fElementAugs;
            this.fAttributes = fAttributes;
        }
        
        void finishStartElement() {
            this.fInStartElement = false;
            this.fElementAugs = null;
            this.fAttributes = null;
        }
        
        void beginEndElement(final Augmentations fElementAugs) {
            this.fInEndElement = true;
            this.fElementAugs = fElementAugs;
        }
        
        void finishEndElement() {
            this.fInEndElement = false;
            this.fElementAugs = null;
        }
        
        private void checkStateAttribute() {
            if (!this.fInStartElement) {
                throw new IllegalStateException(JAXPValidationMessageFormatter.formatMessage(ValidatorHandlerImpl.this.fComponentManager.getLocale(), "TypeInfoProviderIllegalStateAttribute", null));
            }
        }
        
        private void checkStateElement() {
            if (!this.fInStartElement && !this.fInEndElement) {
                throw new IllegalStateException(JAXPValidationMessageFormatter.formatMessage(ValidatorHandlerImpl.this.fComponentManager.getLocale(), "TypeInfoProviderIllegalStateElement", null));
            }
        }
        
        public TypeInfo getAttributeTypeInfo(final int n) {
            this.checkStateAttribute();
            return this.getAttributeType(n);
        }
        
        private TypeInfo getAttributeType(final int n) {
            this.checkStateAttribute();
            if (n < 0 || this.fAttributes.getLength() <= n) {
                throw new IndexOutOfBoundsException(Integer.toString(n));
            }
            final Augmentations augmentations = this.fAttributes.getAugmentations(n);
            if (augmentations == null) {
                return null;
            }
            return this.getTypeInfoFromPSVI((ItemPSVI)augmentations.getItem("ATTRIBUTE_PSVI"));
        }
        
        public TypeInfo getAttributeTypeInfo(final String s, final String s2) {
            this.checkStateAttribute();
            return this.getAttributeTypeInfo(this.fAttributes.getIndex(s, s2));
        }
        
        public TypeInfo getAttributeTypeInfo(final String s) {
            this.checkStateAttribute();
            return this.getAttributeTypeInfo(this.fAttributes.getIndex(s));
        }
        
        public TypeInfo getElementTypeInfo() {
            this.checkStateElement();
            if (this.fElementAugs == null) {
                return null;
            }
            return this.getTypeInfoFromPSVI((ItemPSVI)this.fElementAugs.getItem("ELEMENT_PSVI"));
        }
        
        private TypeInfo getTypeInfoFromPSVI(final ItemPSVI itemPSVI) {
            if (itemPSVI == null) {
                return null;
            }
            if (itemPSVI.getValidity() == 2) {
                final XSSimpleTypeDefinition memberTypeDefinition = itemPSVI.getMemberTypeDefinition();
                if (memberTypeDefinition != null) {
                    return (memberTypeDefinition instanceof TypeInfo) ? memberTypeDefinition : null;
                }
            }
            final XSTypeDefinition typeDefinition = itemPSVI.getTypeDefinition();
            if (typeDefinition != null) {
                return (typeDefinition instanceof TypeInfo) ? typeDefinition : null;
            }
            return null;
        }
        
        public boolean isIdAttribute(final int n) {
            this.checkStateAttribute();
            final XSSimpleType xsSimpleType = (XSSimpleType)this.getAttributeType(n);
            return xsSimpleType != null && xsSimpleType.isIDType();
        }
        
        public boolean isSpecified(final int n) {
            this.checkStateAttribute();
            return this.fAttributes.isSpecified(n);
        }
        
        ElementPSVI getElementPSVI() {
            return (this.fElementAugs != null) ? ((ElementPSVI)this.fElementAugs.getItem("ELEMENT_PSVI")) : null;
        }
        
        AttributePSVI getAttributePSVI(final int n) {
            if (this.fAttributes != null) {
                final Augmentations augmentations = this.fAttributes.getAugmentations(n);
                if (augmentations != null) {
                    return (AttributePSVI)augmentations.getItem("ATTRIBUTE_PSVI");
                }
            }
            return null;
        }
        
        AttributePSVI getAttributePSVIByName(final String s, final String s2) {
            if (this.fAttributes != null) {
                final Augmentations augmentations = this.fAttributes.getAugmentations(s, s2);
                if (augmentations != null) {
                    return (AttributePSVI)augmentations.getItem("ATTRIBUTE_PSVI");
                }
            }
            return null;
        }
    }
}
