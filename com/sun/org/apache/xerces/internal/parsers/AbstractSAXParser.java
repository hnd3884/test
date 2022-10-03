package com.sun.org.apache.xerces.internal.parsers;

import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.Locator2;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import org.xml.sax.SAXNotRecognizedException;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import org.xml.sax.SAXNotSupportedException;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.ErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import org.xml.sax.ErrorHandler;
import com.sun.org.apache.xerces.internal.util.EntityResolverWrapper;
import com.sun.org.apache.xerces.internal.util.EntityResolver2Wrapper;
import org.xml.sax.ext.EntityResolver2;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import java.io.IOException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import org.xml.sax.Attributes;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import org.xml.sax.AttributeList;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import org.xml.sax.SAXException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import org.xml.sax.Locator;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.QName;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.DTDHandler;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.Parser;
import com.sun.org.apache.xerces.internal.xs.PSVIProvider;

public abstract class AbstractSAXParser extends AbstractXMLDocumentParser implements PSVIProvider, Parser, XMLReader
{
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
    protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
    private static final String[] RECOGNIZED_FEATURES;
    protected static final String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
    protected static final String DECLARATION_HANDLER = "http://xml.org/sax/properties/declaration-handler";
    protected static final String DOM_NODE = "http://xml.org/sax/properties/dom-node";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String[] RECOGNIZED_PROPERTIES;
    protected boolean fNamespaces;
    protected boolean fNamespacePrefixes;
    protected boolean fLexicalHandlerParameterEntities;
    protected boolean fStandalone;
    protected boolean fResolveDTDURIs;
    protected boolean fUseEntityResolver2;
    protected boolean fXMLNSURIs;
    protected ContentHandler fContentHandler;
    protected DocumentHandler fDocumentHandler;
    protected NamespaceContext fNamespaceContext;
    protected DTDHandler fDTDHandler;
    protected DeclHandler fDeclHandler;
    protected LexicalHandler fLexicalHandler;
    protected QName fQName;
    protected boolean fParseInProgress;
    protected String fVersion;
    private final AttributesProxy fAttributesProxy;
    private Augmentations fAugmentations;
    private static final int BUFFER_SIZE = 20;
    private char[] fCharBuffer;
    protected SymbolHash fDeclaredAttrs;
    
    protected AbstractSAXParser(final XMLParserConfiguration config) {
        super(config);
        this.fNamespacePrefixes = false;
        this.fLexicalHandlerParameterEntities = true;
        this.fResolveDTDURIs = true;
        this.fUseEntityResolver2 = true;
        this.fXMLNSURIs = false;
        this.fQName = new QName();
        this.fParseInProgress = false;
        this.fAttributesProxy = new AttributesProxy();
        this.fAugmentations = null;
        this.fCharBuffer = new char[20];
        this.fDeclaredAttrs = null;
        config.addRecognizedFeatures(AbstractSAXParser.RECOGNIZED_FEATURES);
        config.addRecognizedProperties(AbstractSAXParser.RECOGNIZED_PROPERTIES);
        try {
            config.setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", false);
        }
        catch (final XMLConfigurationException ex) {}
    }
    
    @Override
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext namespaceContext, final Augmentations augs) throws XNIException {
        this.fNamespaceContext = namespaceContext;
        try {
            if (this.fDocumentHandler != null) {
                if (locator != null) {
                    this.fDocumentHandler.setDocumentLocator(new LocatorProxy(locator));
                }
                this.fDocumentHandler.startDocument();
            }
            if (this.fContentHandler != null) {
                if (locator != null) {
                    this.fContentHandler.setDocumentLocator(new LocatorProxy(locator));
                }
                this.fContentHandler.startDocument();
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
        this.fVersion = version;
        this.fStandalone = "yes".equals(standalone);
    }
    
    @Override
    public void doctypeDecl(final String rootElement, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
        this.fInDTD = true;
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.startDTD(rootElement, publicId, systemId);
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
        if (this.fDeclHandler != null) {
            this.fDeclaredAttrs = new SymbolHash();
        }
    }
    
    @Override
    public void startGeneralEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
        try {
            if (augs != null && Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))) {
                if (this.fContentHandler != null) {
                    this.fContentHandler.skippedEntity(name);
                }
            }
            else if (this.fLexicalHandler != null) {
                this.fLexicalHandler.startEntity(name);
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
        try {
            if ((augs == null || !Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))) && this.fLexicalHandler != null) {
                this.fLexicalHandler.endEntity(name);
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        try {
            if (this.fDocumentHandler != null) {
                this.fAttributesProxy.setAttributes(attributes);
                this.fDocumentHandler.startElement(element.rawname, this.fAttributesProxy);
            }
            if (this.fContentHandler != null) {
                if (this.fNamespaces) {
                    this.startNamespaceMapping();
                    final int len = attributes.getLength();
                    if (!this.fNamespacePrefixes) {
                        for (int i = len - 1; i >= 0; --i) {
                            attributes.getName(i, this.fQName);
                            if (this.fQName.prefix == XMLSymbols.PREFIX_XMLNS || this.fQName.rawname == XMLSymbols.PREFIX_XMLNS) {
                                attributes.removeAttributeAt(i);
                            }
                        }
                    }
                    else if (!this.fXMLNSURIs) {
                        for (int i = len - 1; i >= 0; --i) {
                            attributes.getName(i, this.fQName);
                            if (this.fQName.prefix == XMLSymbols.PREFIX_XMLNS || this.fQName.rawname == XMLSymbols.PREFIX_XMLNS) {
                                this.fQName.prefix = "";
                                this.fQName.uri = "";
                                this.fQName.localpart = "";
                                attributes.setName(i, this.fQName);
                            }
                        }
                    }
                }
                this.fAugmentations = augs;
                final String uri = (element.uri != null) ? element.uri : "";
                final String localpart = this.fNamespaces ? element.localpart : "";
                this.fAttributesProxy.setAttributes(attributes);
                this.fContentHandler.startElement(uri, localpart, element.rawname, this.fAttributesProxy);
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        if (text.length == 0) {
            return;
        }
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.characters(text.ch, text.offset, text.length);
            }
            if (this.fContentHandler != null) {
                this.fContentHandler.characters(text.ch, text.offset, text.length);
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
            }
            if (this.fContentHandler != null) {
                this.fContentHandler.ignorableWhitespace(text.ch, text.offset, text.length);
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.endElement(element.rawname);
            }
            if (this.fContentHandler != null) {
                this.fAugmentations = augs;
                final String uri = (element.uri != null) ? element.uri : "";
                final String localpart = this.fNamespaces ? element.localpart : "";
                this.fContentHandler.endElement(uri, localpart, element.rawname);
                if (this.fNamespaces) {
                    this.endNamespaceMapping();
                }
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void startCDATA(final Augmentations augs) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.startCDATA();
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void endCDATA(final Augmentations augs) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.endCDATA();
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.comment(text.ch, 0, text.length);
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.processingInstruction(target, data.toString());
            }
            if (this.fContentHandler != null) {
                this.fContentHandler.processingInstruction(target, data.toString());
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void endDocument(final Augmentations augs) throws XNIException {
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.endDocument();
            }
            if (this.fContentHandler != null) {
                this.fContentHandler.endDocument();
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void startExternalSubset(final XMLResourceIdentifier identifier, final Augmentations augs) throws XNIException {
        this.startParameterEntity("[dtd]", null, null, augs);
    }
    
    @Override
    public void endExternalSubset(final Augmentations augs) throws XNIException {
        this.endParameterEntity("[dtd]", augs);
    }
    
    @Override
    public void startParameterEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
        try {
            if (augs != null && Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))) {
                if (this.fContentHandler != null) {
                    this.fContentHandler.skippedEntity(name);
                }
            }
            else if (this.fLexicalHandler != null && this.fLexicalHandlerParameterEntities) {
                this.fLexicalHandler.startEntity(name);
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void endParameterEntity(final String name, final Augmentations augs) throws XNIException {
        try {
            if ((augs == null || !Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))) && this.fLexicalHandler != null && this.fLexicalHandlerParameterEntities) {
                this.fLexicalHandler.endEntity(name);
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void elementDecl(final String name, final String contentModel, final Augmentations augs) throws XNIException {
        try {
            if (this.fDeclHandler != null) {
                this.fDeclHandler.elementDecl(name, contentModel);
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void attributeDecl(final String elementName, final String attributeName, String type, final String[] enumeration, final String defaultType, final XMLString defaultValue, final XMLString nonNormalizedDefaultValue, final Augmentations augs) throws XNIException {
        try {
            if (this.fDeclHandler != null) {
                final String elemAttr = new StringBuffer(elementName).append("<").append(attributeName).toString();
                if (this.fDeclaredAttrs.get(elemAttr) != null) {
                    return;
                }
                this.fDeclaredAttrs.put(elemAttr, Boolean.TRUE);
                if (type.equals("NOTATION") || type.equals("ENUMERATION")) {
                    final StringBuffer str = new StringBuffer();
                    if (type.equals("NOTATION")) {
                        str.append(type);
                        str.append(" (");
                    }
                    else {
                        str.append("(");
                    }
                    for (int i = 0; i < enumeration.length; ++i) {
                        str.append(enumeration[i]);
                        if (i < enumeration.length - 1) {
                            str.append('|');
                        }
                    }
                    str.append(')');
                    type = str.toString();
                }
                final String value = (defaultValue == null) ? null : defaultValue.toString();
                this.fDeclHandler.attributeDecl(elementName, attributeName, type, defaultType, value);
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void internalEntityDecl(final String name, final XMLString text, final XMLString nonNormalizedText, final Augmentations augs) throws XNIException {
        try {
            if (this.fDeclHandler != null) {
                this.fDeclHandler.internalEntityDecl(name, text.toString());
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void externalEntityDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augs) throws XNIException {
        try {
            if (this.fDeclHandler != null) {
                final String publicId = identifier.getPublicId();
                final String systemId = this.fResolveDTDURIs ? identifier.getExpandedSystemId() : identifier.getLiteralSystemId();
                this.fDeclHandler.externalEntityDecl(name, publicId, systemId);
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void unparsedEntityDecl(final String name, final XMLResourceIdentifier identifier, final String notation, final Augmentations augs) throws XNIException {
        try {
            if (this.fDTDHandler != null) {
                final String publicId = identifier.getPublicId();
                final String systemId = this.fResolveDTDURIs ? identifier.getExpandedSystemId() : identifier.getLiteralSystemId();
                this.fDTDHandler.unparsedEntityDecl(name, publicId, systemId, notation);
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void notationDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augs) throws XNIException {
        try {
            if (this.fDTDHandler != null) {
                final String publicId = identifier.getPublicId();
                final String systemId = this.fResolveDTDURIs ? identifier.getExpandedSystemId() : identifier.getLiteralSystemId();
                this.fDTDHandler.notationDecl(name, publicId, systemId);
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
    }
    
    @Override
    public void endDTD(final Augmentations augs) throws XNIException {
        this.fInDTD = false;
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.endDTD();
            }
        }
        catch (final SAXException e) {
            throw new XNIException(e);
        }
        if (this.fDeclaredAttrs != null) {
            this.fDeclaredAttrs.clear();
        }
    }
    
    @Override
    public void parse(final String systemId) throws SAXException, IOException {
        final XMLInputSource source = new XMLInputSource(null, systemId, null);
        try {
            this.parse(source);
        }
        catch (final XMLParseException e) {
            final Exception ex = e.getException();
            if (ex == null) {
                final LocatorImpl locatorImpl = new LocatorImpl() {
                    public String getXMLVersion() {
                        return AbstractSAXParser.this.fVersion;
                    }
                    
                    public String getEncoding() {
                        return null;
                    }
                };
                locatorImpl.setPublicId(e.getPublicId());
                locatorImpl.setSystemId(e.getExpandedSystemId());
                locatorImpl.setLineNumber(e.getLineNumber());
                locatorImpl.setColumnNumber(e.getColumnNumber());
                throw new SAXParseException(e.getMessage(), locatorImpl);
            }
            if (ex instanceof SAXException) {
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }
        catch (final XNIException e2) {
            final Exception ex = e2.getException();
            if (ex == null) {
                throw new SAXException(e2.getMessage());
            }
            if (ex instanceof SAXException) {
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }
    }
    
    @Override
    public void parse(final InputSource inputSource) throws SAXException, IOException {
        try {
            final XMLInputSource xmlInputSource = new XMLInputSource(inputSource.getPublicId(), inputSource.getSystemId(), null);
            xmlInputSource.setByteStream(inputSource.getByteStream());
            xmlInputSource.setCharacterStream(inputSource.getCharacterStream());
            xmlInputSource.setEncoding(inputSource.getEncoding());
            this.parse(xmlInputSource);
        }
        catch (final XMLParseException e) {
            final Exception ex = e.getException();
            if (ex == null) {
                final LocatorImpl locatorImpl = new LocatorImpl() {
                    public String getXMLVersion() {
                        return AbstractSAXParser.this.fVersion;
                    }
                    
                    public String getEncoding() {
                        return null;
                    }
                };
                locatorImpl.setPublicId(e.getPublicId());
                locatorImpl.setSystemId(e.getExpandedSystemId());
                locatorImpl.setLineNumber(e.getLineNumber());
                locatorImpl.setColumnNumber(e.getColumnNumber());
                throw new SAXParseException(e.getMessage(), locatorImpl);
            }
            if (ex instanceof SAXException) {
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }
        catch (final XNIException e2) {
            final Exception ex = e2.getException();
            if (ex == null) {
                throw new SAXException(e2.getMessage());
            }
            if (ex instanceof SAXException) {
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }
    }
    
    @Override
    public void setEntityResolver(final EntityResolver resolver) {
        try {
            final XMLEntityResolver xer = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
            if (this.fUseEntityResolver2 && resolver instanceof EntityResolver2) {
                if (xer instanceof EntityResolver2Wrapper) {
                    final EntityResolver2Wrapper er2w = (EntityResolver2Wrapper)xer;
                    er2w.setEntityResolver((EntityResolver2)resolver);
                }
                else {
                    this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolver2Wrapper((EntityResolver2)resolver));
                }
            }
            else if (xer instanceof EntityResolverWrapper) {
                final EntityResolverWrapper erw = (EntityResolverWrapper)xer;
                erw.setEntityResolver(resolver);
            }
            else {
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolverWrapper(resolver));
            }
        }
        catch (final XMLConfigurationException ex) {}
    }
    
    @Override
    public EntityResolver getEntityResolver() {
        EntityResolver entityResolver = null;
        try {
            final XMLEntityResolver xmlEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
            if (xmlEntityResolver != null) {
                if (xmlEntityResolver instanceof EntityResolverWrapper) {
                    entityResolver = ((EntityResolverWrapper)xmlEntityResolver).getEntityResolver();
                }
                else if (xmlEntityResolver instanceof EntityResolver2Wrapper) {
                    entityResolver = ((EntityResolver2Wrapper)xmlEntityResolver).getEntityResolver();
                }
            }
        }
        catch (final XMLConfigurationException ex) {}
        return entityResolver;
    }
    
    @Override
    public void setErrorHandler(final ErrorHandler errorHandler) {
        try {
            final XMLErrorHandler xeh = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
            if (xeh instanceof ErrorHandlerWrapper) {
                final ErrorHandlerWrapper ehw = (ErrorHandlerWrapper)xeh;
                ehw.setErrorHandler(errorHandler);
            }
            else {
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", new ErrorHandlerWrapper(errorHandler));
            }
        }
        catch (final XMLConfigurationException ex) {}
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        ErrorHandler errorHandler = null;
        try {
            final XMLErrorHandler xmlErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
            if (xmlErrorHandler != null && xmlErrorHandler instanceof ErrorHandlerWrapper) {
                errorHandler = ((ErrorHandlerWrapper)xmlErrorHandler).getErrorHandler();
            }
        }
        catch (final XMLConfigurationException ex) {}
        return errorHandler;
    }
    
    @Override
    public void setLocale(final Locale locale) throws SAXException {
        this.fConfiguration.setLocale(locale);
    }
    
    @Override
    public void setDTDHandler(final DTDHandler dtdHandler) {
        this.fDTDHandler = dtdHandler;
    }
    
    @Override
    public void setDocumentHandler(final DocumentHandler documentHandler) {
        this.fDocumentHandler = documentHandler;
    }
    
    @Override
    public void setContentHandler(final ContentHandler contentHandler) {
        this.fContentHandler = contentHandler;
    }
    
    @Override
    public ContentHandler getContentHandler() {
        return this.fContentHandler;
    }
    
    @Override
    public DTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (featureId.startsWith("http://xml.org/sax/features/")) {
                final int suffixLength = featureId.length() - "http://xml.org/sax/features/".length();
                if (suffixLength == "namespaces".length() && featureId.endsWith("namespaces")) {
                    this.fConfiguration.setFeature(featureId, state);
                    this.fNamespaces = state;
                    return;
                }
                if (suffixLength == "namespace-prefixes".length() && featureId.endsWith("namespace-prefixes")) {
                    this.fConfiguration.setFeature(featureId, state);
                    this.fNamespacePrefixes = state;
                    return;
                }
                if (suffixLength == "string-interning".length() && featureId.endsWith("string-interning")) {
                    if (!state) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "false-not-supported", new Object[] { featureId }));
                    }
                    return;
                }
                else {
                    if (suffixLength == "lexical-handler/parameter-entities".length() && featureId.endsWith("lexical-handler/parameter-entities")) {
                        this.fLexicalHandlerParameterEntities = state;
                        return;
                    }
                    if (suffixLength == "resolve-dtd-uris".length() && featureId.endsWith("resolve-dtd-uris")) {
                        this.fResolveDTDURIs = state;
                        return;
                    }
                    if (suffixLength == "unicode-normalization-checking".length() && featureId.endsWith("unicode-normalization-checking")) {
                        if (state) {
                            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "true-not-supported", new Object[] { featureId }));
                        }
                        return;
                    }
                    else {
                        if (suffixLength == "xmlns-uris".length() && featureId.endsWith("xmlns-uris")) {
                            this.fXMLNSURIs = state;
                            return;
                        }
                        if (suffixLength == "use-entity-resolver2".length() && featureId.endsWith("use-entity-resolver2")) {
                            if (state != this.fUseEntityResolver2) {
                                this.fUseEntityResolver2 = state;
                                this.setEntityResolver(this.getEntityResolver());
                            }
                            return;
                        }
                        if ((suffixLength == "is-standalone".length() && featureId.endsWith("is-standalone")) || (suffixLength == "use-attributes2".length() && featureId.endsWith("use-attributes2")) || (suffixLength == "use-locator2".length() && featureId.endsWith("use-locator2")) || (suffixLength == "xml-1.1".length() && featureId.endsWith("xml-1.1"))) {
                            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-read-only", new Object[] { featureId }));
                        }
                    }
                }
            }
            else if (featureId.equals("http://javax.xml.XMLConstants/feature/secure-processing") && state && this.fConfiguration.getProperty("http://apache.org/xml/properties/security-manager") == null) {
                this.fConfiguration.setProperty("http://apache.org/xml/properties/security-manager", new XMLSecurityManager());
            }
            this.fConfiguration.setFeature(featureId, state);
        }
        catch (final XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { identifier }));
        }
    }
    
    @Override
    public boolean getFeature(final String featureId) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (featureId.startsWith("http://xml.org/sax/features/")) {
                final int suffixLength = featureId.length() - "http://xml.org/sax/features/".length();
                if (suffixLength == "namespace-prefixes".length() && featureId.endsWith("namespace-prefixes")) {
                    final boolean state = this.fConfiguration.getFeature(featureId);
                    return state;
                }
                if (suffixLength == "string-interning".length() && featureId.endsWith("string-interning")) {
                    return true;
                }
                if (suffixLength == "is-standalone".length() && featureId.endsWith("is-standalone")) {
                    return this.fStandalone;
                }
                if (suffixLength == "xml-1.1".length() && featureId.endsWith("xml-1.1")) {
                    return this.fConfiguration instanceof XML11Configurable;
                }
                if (suffixLength == "lexical-handler/parameter-entities".length() && featureId.endsWith("lexical-handler/parameter-entities")) {
                    return this.fLexicalHandlerParameterEntities;
                }
                if (suffixLength == "resolve-dtd-uris".length() && featureId.endsWith("resolve-dtd-uris")) {
                    return this.fResolveDTDURIs;
                }
                if (suffixLength == "xmlns-uris".length() && featureId.endsWith("xmlns-uris")) {
                    return this.fXMLNSURIs;
                }
                if (suffixLength == "unicode-normalization-checking".length() && featureId.endsWith("unicode-normalization-checking")) {
                    return false;
                }
                if (suffixLength == "use-entity-resolver2".length() && featureId.endsWith("use-entity-resolver2")) {
                    return this.fUseEntityResolver2;
                }
                if ((suffixLength == "use-attributes2".length() && featureId.endsWith("use-attributes2")) || (suffixLength == "use-locator2".length() && featureId.endsWith("use-locator2"))) {
                    return true;
                }
            }
            return this.fConfiguration.getFeature(featureId);
        }
        catch (final XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { identifier }));
        }
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (propertyId.startsWith("http://xml.org/sax/properties/")) {
                final int suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length();
                if (suffixLength == "lexical-handler".length() && propertyId.endsWith("lexical-handler")) {
                    try {
                        this.setLexicalHandler((LexicalHandler)value);
                    }
                    catch (final ClassCastException e) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "incompatible-class", new Object[] { propertyId, "org.xml.sax.ext.LexicalHandler" }));
                    }
                    return;
                }
                if (suffixLength == "declaration-handler".length() && propertyId.endsWith("declaration-handler")) {
                    try {
                        this.setDeclHandler((DeclHandler)value);
                    }
                    catch (final ClassCastException e) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "incompatible-class", new Object[] { propertyId, "org.xml.sax.ext.DeclHandler" }));
                    }
                    return;
                }
                if ((suffixLength == "dom-node".length() && propertyId.endsWith("dom-node")) || (suffixLength == "document-xml-version".length() && propertyId.endsWith("document-xml-version"))) {
                    throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-read-only", new Object[] { propertyId }));
                }
            }
            this.fConfiguration.setProperty(propertyId, value);
        }
        catch (final XMLConfigurationException e2) {
            final String identifier = e2.getIdentifier();
            if (e2.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { identifier }));
        }
    }
    
    @Override
    public Object getProperty(final String propertyId) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (propertyId.startsWith("http://xml.org/sax/properties/")) {
                final int suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length();
                if (suffixLength == "document-xml-version".length() && propertyId.endsWith("document-xml-version")) {
                    return this.fVersion;
                }
                if (suffixLength == "lexical-handler".length() && propertyId.endsWith("lexical-handler")) {
                    return this.getLexicalHandler();
                }
                if (suffixLength == "declaration-handler".length() && propertyId.endsWith("declaration-handler")) {
                    return this.getDeclHandler();
                }
                if (suffixLength == "dom-node".length() && propertyId.endsWith("dom-node")) {
                    throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "dom-node-read-not-supported", null));
                }
            }
            return this.fConfiguration.getProperty(propertyId);
        }
        catch (final XMLConfigurationException e) {
            final String identifier = e.getIdentifier();
            if (e.getType() == Status.NOT_RECOGNIZED) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { identifier }));
        }
    }
    
    protected void setDeclHandler(final DeclHandler handler) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (this.fParseInProgress) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-parsing-supported", new Object[] { "http://xml.org/sax/properties/declaration-handler" }));
        }
        this.fDeclHandler = handler;
    }
    
    protected DeclHandler getDeclHandler() throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.fDeclHandler;
    }
    
    protected void setLexicalHandler(final LexicalHandler handler) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (this.fParseInProgress) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-parsing-supported", new Object[] { "http://xml.org/sax/properties/lexical-handler" }));
        }
        this.fLexicalHandler = handler;
    }
    
    protected LexicalHandler getLexicalHandler() throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.fLexicalHandler;
    }
    
    protected final void startNamespaceMapping() throws SAXException {
        final int count = this.fNamespaceContext.getDeclaredPrefixCount();
        if (count > 0) {
            String prefix = null;
            String uri = null;
            for (int i = 0; i < count; ++i) {
                prefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
                uri = this.fNamespaceContext.getURI(prefix);
                this.fContentHandler.startPrefixMapping(prefix, (uri == null) ? "" : uri);
            }
        }
    }
    
    protected final void endNamespaceMapping() throws SAXException {
        final int count = this.fNamespaceContext.getDeclaredPrefixCount();
        if (count > 0) {
            for (int i = 0; i < count; ++i) {
                this.fContentHandler.endPrefixMapping(this.fNamespaceContext.getDeclaredPrefixAt(i));
            }
        }
    }
    
    public void reset() throws XNIException {
        super.reset();
        this.fInDTD = false;
        this.fVersion = "1.0";
        this.fStandalone = false;
        this.fNamespaces = this.fConfiguration.getFeature("http://xml.org/sax/features/namespaces");
        this.fNamespacePrefixes = this.fConfiguration.getFeature("http://xml.org/sax/features/namespace-prefixes");
        this.fAugmentations = null;
        this.fDeclaredAttrs = null;
    }
    
    @Override
    public ElementPSVI getElementPSVI() {
        return (this.fAugmentations != null) ? ((ElementPSVI)this.fAugmentations.getItem("ELEMENT_PSVI")) : null;
    }
    
    @Override
    public AttributePSVI getAttributePSVI(final int index) {
        return (AttributePSVI)this.fAttributesProxy.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_PSVI");
    }
    
    @Override
    public AttributePSVI getAttributePSVIByName(final String uri, final String localname) {
        return (AttributePSVI)this.fAttributesProxy.fAttributes.getAugmentations(uri, localname).getItem("ATTRIBUTE_PSVI");
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/namespace-prefixes", "http://xml.org/sax/features/string-interning" };
        RECOGNIZED_PROPERTIES = new String[] { "http://xml.org/sax/properties/lexical-handler", "http://xml.org/sax/properties/declaration-handler", "http://xml.org/sax/properties/dom-node" };
    }
    
    protected class LocatorProxy implements Locator2
    {
        protected XMLLocator fLocator;
        
        public LocatorProxy(final XMLLocator locator) {
            this.fLocator = locator;
        }
        
        @Override
        public String getPublicId() {
            return this.fLocator.getPublicId();
        }
        
        @Override
        public String getSystemId() {
            return this.fLocator.getExpandedSystemId();
        }
        
        @Override
        public int getLineNumber() {
            return this.fLocator.getLineNumber();
        }
        
        @Override
        public int getColumnNumber() {
            return this.fLocator.getColumnNumber();
        }
        
        @Override
        public String getXMLVersion() {
            return this.fLocator.getXMLVersion();
        }
        
        @Override
        public String getEncoding() {
            return this.fLocator.getEncoding();
        }
    }
    
    protected static final class AttributesProxy implements AttributeList, Attributes2
    {
        protected XMLAttributes fAttributes;
        
        public void setAttributes(final XMLAttributes attributes) {
            this.fAttributes = attributes;
        }
        
        @Override
        public int getLength() {
            return this.fAttributes.getLength();
        }
        
        @Override
        public String getName(final int i) {
            return this.fAttributes.getQName(i);
        }
        
        @Override
        public String getQName(final int index) {
            return this.fAttributes.getQName(index);
        }
        
        @Override
        public String getURI(final int index) {
            final String uri = this.fAttributes.getURI(index);
            return (uri != null) ? uri : "";
        }
        
        @Override
        public String getLocalName(final int index) {
            return this.fAttributes.getLocalName(index);
        }
        
        @Override
        public String getType(final int i) {
            return this.fAttributes.getType(i);
        }
        
        @Override
        public String getType(final String name) {
            return this.fAttributes.getType(name);
        }
        
        @Override
        public String getType(final String uri, final String localName) {
            return uri.equals("") ? this.fAttributes.getType(null, localName) : this.fAttributes.getType(uri, localName);
        }
        
        @Override
        public String getValue(final int i) {
            return this.fAttributes.getValue(i);
        }
        
        @Override
        public String getValue(final String name) {
            return this.fAttributes.getValue(name);
        }
        
        @Override
        public String getValue(final String uri, final String localName) {
            return uri.equals("") ? this.fAttributes.getValue(null, localName) : this.fAttributes.getValue(uri, localName);
        }
        
        @Override
        public int getIndex(final String qName) {
            return this.fAttributes.getIndex(qName);
        }
        
        @Override
        public int getIndex(final String uri, final String localPart) {
            return uri.equals("") ? this.fAttributes.getIndex(null, localPart) : this.fAttributes.getIndex(uri, localPart);
        }
        
        @Override
        public boolean isDeclared(final int index) {
            if (index < 0 || index >= this.fAttributes.getLength()) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
        }
        
        @Override
        public boolean isDeclared(final String qName) {
            final int index = this.getIndex(qName);
            if (index == -1) {
                throw new IllegalArgumentException(qName);
            }
            return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
        }
        
        @Override
        public boolean isDeclared(final String uri, final String localName) {
            final int index = this.getIndex(uri, localName);
            if (index == -1) {
                throw new IllegalArgumentException(localName);
            }
            return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
        }
        
        @Override
        public boolean isSpecified(final int index) {
            if (index < 0 || index >= this.fAttributes.getLength()) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return this.fAttributes.isSpecified(index);
        }
        
        @Override
        public boolean isSpecified(final String qName) {
            final int index = this.getIndex(qName);
            if (index == -1) {
                throw new IllegalArgumentException(qName);
            }
            return this.fAttributes.isSpecified(index);
        }
        
        @Override
        public boolean isSpecified(final String uri, final String localName) {
            final int index = this.getIndex(uri, localName);
            if (index == -1) {
                throw new IllegalArgumentException(localName);
            }
            return this.fAttributes.isSpecified(index);
        }
    }
}
