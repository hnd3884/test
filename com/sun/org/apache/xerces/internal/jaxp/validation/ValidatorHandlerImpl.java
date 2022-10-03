package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import java.io.InputStream;
import org.w3c.dom.ls.LSInput;
import java.io.Reader;
import java.io.StringReader;
import org.xml.sax.ext.EntityResolver2;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.ItemPSVI;
import org.w3c.dom.TypeInfo;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.FactoryConfigurationError;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import jdk.xml.internal.JdkXmlUtils;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.xml.sax.ext.Attributes2;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.xml.sax.Locator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import org.xml.sax.Attributes;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import org.xml.sax.SAXNotSupportedException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import org.xml.sax.SAXNotRecognizedException;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import com.sun.org.apache.xerces.internal.util.Status;
import javax.xml.validation.TypeInfoProvider;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.util.AttributesProxy;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.HashMap;
import com.sun.org.apache.xerces.internal.util.SAXLocatorWrapper;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaValidator;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;
import com.sun.org.apache.xerces.internal.impl.validation.EntityState;
import org.xml.sax.DTDHandler;
import javax.xml.validation.ValidatorHandler;

final class ValidatorHandlerImpl extends ValidatorHandler implements DTDHandler, EntityState, PSVIProvider, ValidatorHelper, XMLDocumentHandler
{
    private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
    private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
    private XMLErrorReporter fErrorReporter;
    private NamespaceContext fNamespaceContext;
    private XMLSchemaValidator fSchemaValidator;
    private SymbolTable fSymbolTable;
    private ValidationManager fValidationManager;
    private XMLSchemaValidatorComponentManager fComponentManager;
    private final SAXLocatorWrapper fSAXLocatorWrapper;
    private boolean fNeedPushNSContext;
    private HashMap fUnparsedEntities;
    private boolean fStringsInternalized;
    private final QName fElementQName;
    private final QName fAttributeQName;
    private final XMLAttributesImpl fAttributes;
    private final AttributesProxy fAttrAdapter;
    private final XMLString fTempString;
    private ContentHandler fContentHandler;
    private final XMLSchemaTypeInfoProvider fTypeInfoProvider;
    private final ResolutionForwarder fResolutionForwarder;
    
    public ValidatorHandlerImpl(final XSGrammarPoolContainer grammarContainer) {
        this(new XMLSchemaValidatorComponentManager(grammarContainer));
        this.fComponentManager.addRecognizedFeatures(new String[] { "http://xml.org/sax/features/namespace-prefixes" });
        this.fComponentManager.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
        this.setErrorHandler(null);
        this.setResourceResolver(null);
    }
    
    public ValidatorHandlerImpl(final XMLSchemaValidatorComponentManager componentManager) {
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
        this.fTypeInfoProvider = new XMLSchemaTypeInfoProvider();
        this.fResolutionForwarder = new ResolutionForwarder(null);
        this.fComponentManager = componentManager;
        this.fErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fNamespaceContext = (NamespaceContext)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context");
        this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validator/schema");
        this.fSymbolTable = (SymbolTable)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fValidationManager = (ValidationManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
    }
    
    @Override
    public void setContentHandler(final ContentHandler receiver) {
        this.fContentHandler = receiver;
    }
    
    @Override
    public ContentHandler getContentHandler() {
        return this.fContentHandler;
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.fComponentManager.setErrorHandler(errorHandler);
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        return this.fComponentManager.getErrorHandler();
    }
    
    @Override
    public void setResourceResolver(final LSResourceResolver resourceResolver) {
        this.fComponentManager.setResourceResolver(resourceResolver);
    }
    
    @Override
    public LSResourceResolver getResourceResolver() {
        return this.fComponentManager.getResourceResolver();
    }
    
    @Override
    public TypeInfoProvider getTypeInfoProvider() {
        return this.fTypeInfoProvider;
    }
    
    @Override
    public boolean getFeature(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            return this.fComponentManager.getFeature(name);
        }
        catch (final XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = (e.getType() == Status.NOT_RECOGNIZED) ? "feature-not-recognized" : "feature-not-supported";
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[] { identifier }));
        }
    }
    
    @Override
    public void setFeature(final String name, final boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            this.fComponentManager.setFeature(name, value);
        }
        catch (final XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_ALLOWED) {
                throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "jaxp-secureprocessing-feature", null));
            }
            String key;
            if (e.getType() == Status.NOT_RECOGNIZED) {
                key = "feature-not-recognized";
            }
            else {
                key = "feature-not-supported";
            }
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[] { identifier }));
        }
    }
    
    @Override
    public Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            return this.fComponentManager.getProperty(name);
        }
        catch (final XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = (e.getType() == Status.NOT_RECOGNIZED) ? "property-not-recognized" : "property-not-supported";
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[] { identifier }));
        }
    }
    
    @Override
    public void setProperty(final String name, final Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name == null) {
            throw new NullPointerException();
        }
        try {
            this.fComponentManager.setProperty(name, object);
        }
        catch (final XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            final String key = (e.getType() == Status.NOT_RECOGNIZED) ? "property-not-recognized" : "property-not-supported";
            throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), key, new Object[] { identifier }));
        }
    }
    
    @Override
    public boolean isEntityDeclared(final String name) {
        return false;
    }
    
    @Override
    public boolean isEntityUnparsed(final String name) {
        return this.fUnparsedEntities != null && this.fUnparsedEntities.containsKey(name);
    }
    
    @Override
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext namespaceContext, final Augmentations augs) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fContentHandler.startDocument();
            }
            catch (final SAXException e) {
                throw new XNIException(e);
            }
        }
    }
    
    @Override
    public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void doctypeDecl(final String rootElement, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fContentHandler.processingInstruction(target, data.toString());
            }
            catch (final SAXException e) {
                throw new XNIException(e);
            }
        }
    }
    
    @Override
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fTypeInfoProvider.beginStartElement(augs, attributes);
                this.fContentHandler.startElement((element.uri != null) ? element.uri : XMLSymbols.EMPTY_STRING, element.localpart, element.rawname, this.fAttrAdapter);
            }
            catch (final SAXException e) {
                throw new XNIException(e);
            }
            finally {
                this.fTypeInfoProvider.finishStartElement();
            }
        }
    }
    
    @Override
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        this.startElement(element, attributes, augs);
        this.endElement(element, augs);
    }
    
    @Override
    public void startGeneralEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fContentHandler != null) {
            if (text.length == 0) {
                return;
            }
            try {
                this.fContentHandler.characters(text.ch, text.offset, text.length);
            }
            catch (final SAXException e) {
                throw new XNIException(e);
            }
        }
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fContentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
            }
            catch (final SAXException e) {
                throw new XNIException(e);
            }
        }
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fTypeInfoProvider.beginEndElement(augs);
                this.fContentHandler.endElement((element.uri != null) ? element.uri : XMLSymbols.EMPTY_STRING, element.localpart, element.rawname);
            }
            catch (final SAXException e) {
                throw new XNIException(e);
            }
            finally {
                this.fTypeInfoProvider.finishEndElement();
            }
        }
    }
    
    @Override
    public void startCDATA(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endCDATA(final Augmentations augs) throws XNIException {
    }
    
    @Override
    public void endDocument(final Augmentations augs) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fContentHandler.endDocument();
            }
            catch (final SAXException e) {
                throw new XNIException(e);
            }
        }
    }
    
    @Override
    public void setDocumentSource(final XMLDocumentSource source) {
    }
    
    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.fSchemaValidator;
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.fSAXLocatorWrapper.setLocator(locator);
        if (this.fContentHandler != null) {
            this.fContentHandler.setDocumentLocator(locator);
        }
    }
    
    @Override
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
        catch (final XMLParseException e) {
            throw Util.toSAXParseException(e);
        }
        catch (final XNIException e2) {
            throw Util.toSAXException(e2);
        }
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.fSAXLocatorWrapper.setLocator(null);
        try {
            this.fSchemaValidator.endDocument(null);
        }
        catch (final XMLParseException e) {
            throw Util.toSAXParseException(e);
        }
        catch (final XNIException e2) {
            throw Util.toSAXException(e2);
        }
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        String prefixSymbol;
        String uriSymbol;
        if (!this.fStringsInternalized) {
            prefixSymbol = ((prefix != null) ? this.fSymbolTable.addSymbol(prefix) : XMLSymbols.EMPTY_STRING);
            uriSymbol = ((uri != null && uri.length() > 0) ? this.fSymbolTable.addSymbol(uri) : null);
        }
        else {
            prefixSymbol = ((prefix != null) ? prefix : XMLSymbols.EMPTY_STRING);
            uriSymbol = ((uri != null && uri.length() > 0) ? uri : null);
        }
        if (this.fNeedPushNSContext) {
            this.fNeedPushNSContext = false;
            this.fNamespaceContext.pushContext();
        }
        this.fNamespaceContext.declarePrefix(prefixSymbol, uriSymbol);
        if (this.fContentHandler != null) {
            this.fContentHandler.startPrefixMapping(prefix, uri);
        }
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        if (this.fContentHandler != null) {
            this.fContentHandler.endPrefixMapping(prefix);
        }
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
        if (this.fNeedPushNSContext) {
            this.fNamespaceContext.pushContext();
        }
        this.fNeedPushNSContext = true;
        this.fillQName(this.fElementQName, uri, localName, qName);
        if (atts instanceof Attributes2) {
            this.fillXMLAttributes2((Attributes2)atts);
        }
        else {
            this.fillXMLAttributes(atts);
        }
        try {
            this.fSchemaValidator.startElement(this.fElementQName, this.fAttributes, null);
        }
        catch (final XMLParseException e) {
            throw Util.toSAXParseException(e);
        }
        catch (final XNIException e2) {
            throw Util.toSAXException(e2);
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        this.fillQName(this.fElementQName, uri, localName, qName);
        try {
            this.fSchemaValidator.endElement(this.fElementQName, null);
        }
        catch (final XMLParseException e) {
            throw Util.toSAXParseException(e);
        }
        catch (final XNIException e2) {
            throw Util.toSAXException(e2);
        }
        finally {
            this.fNamespaceContext.popContext();
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this.fTempString.setValues(ch, start, length);
            this.fSchemaValidator.characters(this.fTempString, null);
        }
        catch (final XMLParseException e) {
            throw Util.toSAXParseException(e);
        }
        catch (final XNIException e2) {
            throw Util.toSAXException(e2);
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        try {
            this.fTempString.setValues(ch, start, length);
            this.fSchemaValidator.ignorableWhitespace(this.fTempString, null);
        }
        catch (final XMLParseException e) {
            throw Util.toSAXParseException(e);
        }
        catch (final XNIException e2) {
            throw Util.toSAXException(e2);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (this.fContentHandler != null) {
            this.fContentHandler.processingInstruction(target, data);
        }
    }
    
    @Override
    public void skippedEntity(final String name) throws SAXException {
        if (this.fContentHandler != null) {
            this.fContentHandler.skippedEntity(name);
        }
    }
    
    @Override
    public void notationDecl(final String name, final String publicId, final String systemId) throws SAXException {
    }
    
    @Override
    public void unparsedEntityDecl(final String name, final String publicId, final String systemId, final String notationName) throws SAXException {
        if (this.fUnparsedEntities == null) {
            this.fUnparsedEntities = new HashMap();
        }
        this.fUnparsedEntities.put(name, name);
    }
    
    @Override
    public void validate(final Source source, final Result result) throws SAXException, IOException {
        if (result instanceof SAXResult || result == null) {
            final SAXSource saxSource = (SAXSource)source;
            final SAXResult saxResult = (SAXResult)result;
            if (result != null) {
                this.setContentHandler(saxResult.getHandler());
            }
            try {
                XMLReader reader = saxSource.getXMLReader();
                if (reader == null) {
                    reader = JdkXmlUtils.getXMLReader(this.fComponentManager.getFeature("jdk.xml.overrideDefaultParser"), this.fComponentManager.getFeature("http://javax.xml.XMLConstants/feature/secure-processing"));
                    try {
                        if (reader instanceof SAXParser) {
                            final XMLSecurityManager securityManager = (XMLSecurityManager)this.fComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
                            if (securityManager != null) {
                                try {
                                    reader.setProperty("http://apache.org/xml/properties/security-manager", securityManager);
                                }
                                catch (final SAXException ex) {}
                            }
                            try {
                                final XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)this.fComponentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
                                reader.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD));
                            }
                            catch (final SAXException exc) {
                                XMLSecurityManager.printWarning(reader.getClass().getName(), "http://javax.xml.XMLConstants/property/accessExternalDTD", exc);
                            }
                        }
                    }
                    catch (final Exception e) {
                        throw new FactoryConfigurationError(e);
                    }
                }
                try {
                    this.fStringsInternalized = reader.getFeature("http://xml.org/sax/features/string-interning");
                }
                catch (final SAXException exc2) {
                    this.fStringsInternalized = false;
                }
                final ErrorHandler errorHandler = this.fComponentManager.getErrorHandler();
                reader.setErrorHandler((errorHandler != null) ? errorHandler : DraconianErrorHandler.getInstance());
                reader.setEntityResolver(this.fResolutionForwarder);
                this.fResolutionForwarder.setEntityResolver(this.fComponentManager.getResourceResolver());
                reader.setContentHandler(this);
                reader.setDTDHandler(this);
                final InputSource is = saxSource.getInputSource();
                reader.parse(is);
            }
            finally {
                this.setContentHandler(null);
            }
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[] { source.getClass().getName(), result.getClass().getName() }));
    }
    
    @Override
    public ElementPSVI getElementPSVI() {
        return this.fTypeInfoProvider.getElementPSVI();
    }
    
    @Override
    public AttributePSVI getAttributePSVI(final int index) {
        return this.fTypeInfoProvider.getAttributePSVI(index);
    }
    
    @Override
    public AttributePSVI getAttributePSVIByName(final String uri, final String localname) {
        return this.fTypeInfoProvider.getAttributePSVIByName(uri, localname);
    }
    
    private void fillQName(final QName toFill, String uri, String localpart, String raw) {
        if (!this.fStringsInternalized) {
            uri = ((uri != null && uri.length() > 0) ? this.fSymbolTable.addSymbol(uri) : null);
            localpart = ((localpart != null) ? this.fSymbolTable.addSymbol(localpart) : XMLSymbols.EMPTY_STRING);
            raw = ((raw != null) ? this.fSymbolTable.addSymbol(raw) : XMLSymbols.EMPTY_STRING);
        }
        else {
            if (uri != null && uri.length() == 0) {
                uri = null;
            }
            if (localpart == null) {
                localpart = XMLSymbols.EMPTY_STRING;
            }
            if (raw == null) {
                raw = XMLSymbols.EMPTY_STRING;
            }
        }
        String prefix = XMLSymbols.EMPTY_STRING;
        final int prefixIdx = raw.indexOf(58);
        if (prefixIdx != -1) {
            prefix = this.fSymbolTable.addSymbol(raw.substring(0, prefixIdx));
        }
        toFill.setValues(prefix, localpart, raw, uri);
    }
    
    private void fillXMLAttributes(final Attributes att) {
        this.fAttributes.removeAllAttributes();
        for (int len = att.getLength(), i = 0; i < len; ++i) {
            this.fillXMLAttribute(att, i);
            this.fAttributes.setSpecified(i, true);
        }
    }
    
    private void fillXMLAttributes2(final Attributes2 att) {
        this.fAttributes.removeAllAttributes();
        for (int len = att.getLength(), i = 0; i < len; ++i) {
            this.fillXMLAttribute(att, i);
            this.fAttributes.setSpecified(i, att.isSpecified(i));
            if (att.isDeclared(i)) {
                this.fAttributes.getAugmentations(i).putItem("ATTRIBUTE_DECLARED", Boolean.TRUE);
            }
        }
    }
    
    private void fillXMLAttribute(final Attributes att, final int index) {
        this.fillQName(this.fAttributeQName, att.getURI(index), att.getLocalName(index), att.getQName(index));
        final String type = att.getType(index);
        this.fAttributes.addAttributeNS(this.fAttributeQName, (type != null) ? type : XMLSymbols.fCDATASymbol, att.getValue(index));
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
        
        void beginStartElement(final Augmentations elementAugs, final XMLAttributes attributes) {
            this.fInStartElement = true;
            this.fElementAugs = elementAugs;
            this.fAttributes = attributes;
        }
        
        void finishStartElement() {
            this.fInStartElement = false;
            this.fElementAugs = null;
            this.fAttributes = null;
        }
        
        void beginEndElement(final Augmentations elementAugs) {
            this.fInEndElement = true;
            this.fElementAugs = elementAugs;
        }
        
        void finishEndElement() {
            this.fInEndElement = false;
            this.fElementAugs = null;
        }
        
        private void checkState(final boolean forElementInfo) {
            if (!this.fInStartElement && (!this.fInEndElement || !forElementInfo)) {
                throw new IllegalStateException(JAXPValidationMessageFormatter.formatMessage(ValidatorHandlerImpl.this.fComponentManager.getLocale(), "TypeInfoProviderIllegalState", null));
            }
        }
        
        @Override
        public TypeInfo getAttributeTypeInfo(final int index) {
            this.checkState(false);
            return this.getAttributeType(index);
        }
        
        private TypeInfo getAttributeType(final int index) {
            this.checkState(false);
            if (index < 0 || this.fAttributes.getLength() <= index) {
                throw new IndexOutOfBoundsException(Integer.toString(index));
            }
            final Augmentations augs = this.fAttributes.getAugmentations(index);
            if (augs == null) {
                return null;
            }
            final AttributePSVI psvi = (AttributePSVI)augs.getItem("ATTRIBUTE_PSVI");
            return this.getTypeInfoFromPSVI(psvi);
        }
        
        public TypeInfo getAttributeTypeInfo(final String attributeUri, final String attributeLocalName) {
            this.checkState(false);
            return this.getAttributeTypeInfo(this.fAttributes.getIndex(attributeUri, attributeLocalName));
        }
        
        public TypeInfo getAttributeTypeInfo(final String attributeQName) {
            this.checkState(false);
            return this.getAttributeTypeInfo(this.fAttributes.getIndex(attributeQName));
        }
        
        @Override
        public TypeInfo getElementTypeInfo() {
            this.checkState(true);
            if (this.fElementAugs == null) {
                return null;
            }
            final ElementPSVI psvi = (ElementPSVI)this.fElementAugs.getItem("ELEMENT_PSVI");
            return this.getTypeInfoFromPSVI(psvi);
        }
        
        private TypeInfo getTypeInfoFromPSVI(final ItemPSVI psvi) {
            if (psvi == null) {
                return null;
            }
            if (psvi.getValidity() == 2) {
                final XSTypeDefinition t = psvi.getMemberTypeDefinition();
                if (t != null) {
                    return (t instanceof TypeInfo) ? t : null;
                }
            }
            final XSTypeDefinition t = psvi.getTypeDefinition();
            if (t != null) {
                return (t instanceof TypeInfo) ? t : null;
            }
            return null;
        }
        
        @Override
        public boolean isIdAttribute(final int index) {
            this.checkState(false);
            final XSSimpleType type = (XSSimpleType)this.getAttributeType(index);
            return type != null && type.isIDType();
        }
        
        @Override
        public boolean isSpecified(final int index) {
            this.checkState(false);
            return this.fAttributes.isSpecified(index);
        }
        
        ElementPSVI getElementPSVI() {
            return (this.fElementAugs != null) ? ((ElementPSVI)this.fElementAugs.getItem("ELEMENT_PSVI")) : null;
        }
        
        AttributePSVI getAttributePSVI(final int index) {
            if (this.fAttributes != null) {
                final Augmentations augs = this.fAttributes.getAugmentations(index);
                if (augs != null) {
                    return (AttributePSVI)augs.getItem("ATTRIBUTE_PSVI");
                }
            }
            return null;
        }
        
        AttributePSVI getAttributePSVIByName(final String uri, final String localname) {
            if (this.fAttributes != null) {
                final Augmentations augs = this.fAttributes.getAugmentations(uri, localname);
                if (augs != null) {
                    return (AttributePSVI)augs.getItem("ATTRIBUTE_PSVI");
                }
            }
            return null;
        }
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
        
        public void setEntityResolver(final LSResourceResolver entityResolver) {
            this.fEntityResolver = entityResolver;
        }
        
        public LSResourceResolver getEntityResolver() {
            return this.fEntityResolver;
        }
        
        @Override
        public InputSource getExternalSubset(final String name, final String baseURI) throws SAXException, IOException {
            return null;
        }
        
        @Override
        public InputSource resolveEntity(final String name, final String publicId, final String baseURI, final String systemId) throws SAXException, IOException {
            if (this.fEntityResolver != null) {
                final LSInput lsInput = this.fEntityResolver.resolveResource("http://www.w3.org/TR/REC-xml", null, publicId, systemId, baseURI);
                if (lsInput != null) {
                    final String pubId = lsInput.getPublicId();
                    final String sysId = lsInput.getSystemId();
                    final String baseSystemId = lsInput.getBaseURI();
                    final Reader charStream = lsInput.getCharacterStream();
                    final InputStream byteStream = lsInput.getByteStream();
                    final String data = lsInput.getStringData();
                    final String encoding = lsInput.getEncoding();
                    final InputSource inputSource = new InputSource();
                    inputSource.setPublicId(pubId);
                    inputSource.setSystemId((baseSystemId != null) ? this.resolveSystemId(systemId, baseSystemId) : systemId);
                    if (charStream != null) {
                        inputSource.setCharacterStream(charStream);
                    }
                    else if (byteStream != null) {
                        inputSource.setByteStream(byteStream);
                    }
                    else if (data != null && data.length() != 0) {
                        inputSource.setCharacterStream(new StringReader(data));
                    }
                    inputSource.setEncoding(encoding);
                    return inputSource;
                }
            }
            return null;
        }
        
        @Override
        public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException {
            return this.resolveEntity(null, publicId, null, systemId);
        }
        
        private String resolveSystemId(final String systemId, final String baseURI) {
            try {
                return XMLEntityManager.expandSystemId(systemId, baseURI, false);
            }
            catch (final URI.MalformedURIException ex) {
                return systemId;
            }
        }
    }
}
