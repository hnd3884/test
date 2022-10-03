package org.apache.xerces.parsers;

import org.xml.sax.ext.Locator2;
import org.xml.sax.ext.Attributes2;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.ElementPSVI;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.apache.xerces.util.SAXMessageFormatter;
import java.util.Locale;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.xml.sax.ErrorHandler;
import org.apache.xerces.util.EntityResolverWrapper;
import org.apache.xerces.util.EntityResolver2Wrapper;
import org.xml.sax.ext.EntityResolver2;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import java.io.IOException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.Locator2Impl;
import java.io.CharConversionException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.XMLString;
import org.xml.sax.Attributes;
import org.apache.xerces.util.XMLSymbols;
import org.xml.sax.AttributeList;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.xml.sax.SAXException;
import org.apache.xerces.xni.XNIException;
import org.xml.sax.Locator;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.DTDHandler;
import org.apache.xerces.xni.NamespaceContext;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.Parser;
import org.apache.xerces.xs.PSVIProvider;

public abstract class AbstractSAXParser extends AbstractXMLDocumentParser implements PSVIProvider, Parser, XMLReader
{
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
    protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
    private static final String[] RECOGNIZED_FEATURES;
    protected static final String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
    protected static final String DECLARATION_HANDLER = "http://xml.org/sax/properties/declaration-handler";
    protected static final String DOM_NODE = "http://xml.org/sax/properties/dom-node";
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
    protected final QName fQName;
    protected boolean fParseInProgress;
    protected String fVersion;
    private final AttributesProxy fAttributesProxy;
    private Augmentations fAugmentations;
    protected SymbolHash fDeclaredAttrs;
    
    protected AbstractSAXParser(final XMLParserConfiguration xmlParserConfiguration) {
        super(xmlParserConfiguration);
        this.fNamespacePrefixes = false;
        this.fLexicalHandlerParameterEntities = true;
        this.fResolveDTDURIs = true;
        this.fUseEntityResolver2 = true;
        this.fXMLNSURIs = false;
        this.fQName = new QName();
        this.fParseInProgress = false;
        this.fAttributesProxy = new AttributesProxy();
        this.fAugmentations = null;
        this.fDeclaredAttrs = null;
        xmlParserConfiguration.addRecognizedFeatures(AbstractSAXParser.RECOGNIZED_FEATURES);
        xmlParserConfiguration.addRecognizedProperties(AbstractSAXParser.RECOGNIZED_PROPERTIES);
        try {
            xmlParserConfiguration.setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", false);
        }
        catch (final XMLConfigurationException ex) {}
    }
    
    public void startDocument(final XMLLocator xmlLocator, final String s, final NamespaceContext fNamespaceContext, final Augmentations augmentations) throws XNIException {
        this.fNamespaceContext = fNamespaceContext;
        try {
            if (this.fDocumentHandler != null) {
                if (xmlLocator != null) {
                    this.fDocumentHandler.setDocumentLocator(new LocatorProxy(xmlLocator));
                }
                if (this.fDocumentHandler != null) {
                    this.fDocumentHandler.startDocument();
                }
            }
            if (this.fContentHandler != null) {
                if (xmlLocator != null) {
                    this.fContentHandler.setDocumentLocator(new LocatorProxy(xmlLocator));
                }
                if (this.fContentHandler != null) {
                    this.fContentHandler.startDocument();
                }
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void xmlDecl(final String fVersion, final String s, final String s2, final Augmentations augmentations) throws XNIException {
        this.fVersion = fVersion;
        this.fStandalone = "yes".equals(s2);
    }
    
    public void doctypeDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
        this.fInDTD = true;
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.startDTD(s, s2, s3);
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
        if (this.fDeclHandler != null) {
            this.fDeclaredAttrs = new SymbolHash(25);
        }
    }
    
    public void startGeneralEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        try {
            if (augmentations != null && Boolean.TRUE.equals(augmentations.getItem("ENTITY_SKIPPED"))) {
                if (this.fContentHandler != null) {
                    this.fContentHandler.skippedEntity(s);
                }
            }
            else if (this.fLexicalHandler != null) {
                this.fLexicalHandler.startEntity(s);
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void endGeneralEntity(final String s, final Augmentations augmentations) throws XNIException {
        try {
            if ((augmentations == null || !Boolean.TRUE.equals(augmentations.getItem("ENTITY_SKIPPED"))) && this.fLexicalHandler != null) {
                this.fLexicalHandler.endEntity(s);
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations fAugmentations) throws XNIException {
        try {
            if (this.fDocumentHandler != null) {
                this.fAttributesProxy.setAttributes(xmlAttributes);
                this.fDocumentHandler.startElement(qName.rawname, this.fAttributesProxy);
            }
            if (this.fContentHandler != null) {
                if (this.fNamespaces) {
                    this.startNamespaceMapping();
                    final int length = xmlAttributes.getLength();
                    if (!this.fNamespacePrefixes) {
                        for (int i = length - 1; i >= 0; --i) {
                            xmlAttributes.getName(i, this.fQName);
                            if (this.fQName.prefix == XMLSymbols.PREFIX_XMLNS || this.fQName.rawname == XMLSymbols.PREFIX_XMLNS) {
                                xmlAttributes.removeAttributeAt(i);
                            }
                        }
                    }
                    else if (!this.fXMLNSURIs) {
                        for (int j = length - 1; j >= 0; --j) {
                            xmlAttributes.getName(j, this.fQName);
                            if (this.fQName.prefix == XMLSymbols.PREFIX_XMLNS || this.fQName.rawname == XMLSymbols.PREFIX_XMLNS) {
                                this.fQName.prefix = "";
                                this.fQName.uri = "";
                                this.fQName.localpart = "";
                                xmlAttributes.setName(j, this.fQName);
                            }
                        }
                    }
                }
                this.fAugmentations = fAugmentations;
                final String s = (qName.uri != null) ? qName.uri : "";
                final String s2 = this.fNamespaces ? qName.localpart : "";
                this.fAttributesProxy.setAttributes(xmlAttributes);
                this.fContentHandler.startElement(s, s2, qName.rawname, this.fAttributesProxy);
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void characters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (xmlString.length == 0) {
            return;
        }
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.characters(xmlString.ch, xmlString.offset, xmlString.length);
            }
            if (this.fContentHandler != null) {
                this.fContentHandler.characters(xmlString.ch, xmlString.offset, xmlString.length);
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.ignorableWhitespace(xmlString.ch, xmlString.offset, xmlString.length);
            }
            if (this.fContentHandler != null) {
                this.fContentHandler.ignorableWhitespace(xmlString.ch, xmlString.offset, xmlString.length);
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void endElement(final QName qName, final Augmentations fAugmentations) throws XNIException {
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.endElement(qName.rawname);
            }
            if (this.fContentHandler != null) {
                this.fAugmentations = fAugmentations;
                this.fContentHandler.endElement((qName.uri != null) ? qName.uri : "", this.fNamespaces ? qName.localpart : "", qName.rawname);
                if (this.fNamespaces) {
                    this.endNamespaceMapping();
                }
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.startCDATA();
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.endCDATA();
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.comment(xmlString.ch, 0, xmlString.length);
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.processingInstruction(s, xmlString.toString());
            }
            if (this.fContentHandler != null) {
                this.fContentHandler.processingInstruction(s, xmlString.toString());
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void endDocument(final Augmentations augmentations) throws XNIException {
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.endDocument();
            }
            if (this.fContentHandler != null) {
                this.fContentHandler.endDocument();
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void startExternalSubset(final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        this.startParameterEntity("[dtd]", null, null, augmentations);
    }
    
    public void endExternalSubset(final Augmentations augmentations) throws XNIException {
        this.endParameterEntity("[dtd]", augmentations);
    }
    
    public void startParameterEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        try {
            if (augmentations != null && Boolean.TRUE.equals(augmentations.getItem("ENTITY_SKIPPED"))) {
                if (this.fContentHandler != null) {
                    this.fContentHandler.skippedEntity(s);
                }
            }
            else if (this.fLexicalHandler != null && this.fLexicalHandlerParameterEntities) {
                this.fLexicalHandler.startEntity(s);
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void endParameterEntity(final String s, final Augmentations augmentations) throws XNIException {
        try {
            if ((augmentations == null || !Boolean.TRUE.equals(augmentations.getItem("ENTITY_SKIPPED"))) && this.fLexicalHandler != null && this.fLexicalHandlerParameterEntities) {
                this.fLexicalHandler.endEntity(s);
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void elementDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
        try {
            if (this.fDeclHandler != null) {
                this.fDeclHandler.elementDecl(s, s2);
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void attributeDecl(final String s, final String s2, String string, final String[] array, final String s3, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
        try {
            if (this.fDeclHandler != null) {
                final String string2 = s + '<' + s2;
                if (this.fDeclaredAttrs.get(string2) != null) {
                    return;
                }
                this.fDeclaredAttrs.put(string2, Boolean.TRUE);
                if (string.equals("NOTATION") || string.equals("ENUMERATION")) {
                    final StringBuffer sb = new StringBuffer();
                    if (string.equals("NOTATION")) {
                        sb.append(string);
                        sb.append(" (");
                    }
                    else {
                        sb.append('(');
                    }
                    for (int i = 0; i < array.length; ++i) {
                        sb.append(array[i]);
                        if (i < array.length - 1) {
                            sb.append('|');
                        }
                    }
                    sb.append(')');
                    string = sb.toString();
                }
                this.fDeclHandler.attributeDecl(s, s2, string, s3, (xmlString == null) ? null : xmlString.toString());
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void internalEntityDecl(final String s, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
        try {
            if (this.fDeclHandler != null) {
                this.fDeclHandler.internalEntityDecl(s, xmlString.toString());
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void externalEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        try {
            if (this.fDeclHandler != null) {
                this.fDeclHandler.externalEntityDecl(s, xmlResourceIdentifier.getPublicId(), this.fResolveDTDURIs ? xmlResourceIdentifier.getExpandedSystemId() : xmlResourceIdentifier.getLiteralSystemId());
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void unparsedEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        try {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.unparsedEntityDecl(s, xmlResourceIdentifier.getPublicId(), this.fResolveDTDURIs ? xmlResourceIdentifier.getExpandedSystemId() : xmlResourceIdentifier.getLiteralSystemId(), s2);
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void notationDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        try {
            if (this.fDTDHandler != null) {
                this.fDTDHandler.notationDecl(s, xmlResourceIdentifier.getPublicId(), this.fResolveDTDURIs ? xmlResourceIdentifier.getExpandedSystemId() : xmlResourceIdentifier.getLiteralSystemId());
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
    }
    
    public void endDTD(final Augmentations augmentations) throws XNIException {
        this.fInDTD = false;
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.endDTD();
            }
        }
        catch (final SAXException ex) {
            throw new XNIException(ex);
        }
        if (this.fDeclaredAttrs != null) {
            this.fDeclaredAttrs.clear();
        }
    }
    
    public void parse(final String s) throws SAXException, IOException {
        final XMLInputSource xmlInputSource = new XMLInputSource(null, s, null);
        try {
            this.parse(xmlInputSource);
        }
        catch (final XMLParseException ex) {
            final Exception exception = ex.getException();
            if (exception == null || exception instanceof CharConversionException) {
                final Locator2Impl locator2Impl = new Locator2Impl();
                locator2Impl.setXMLVersion(this.fVersion);
                locator2Impl.setPublicId(ex.getPublicId());
                locator2Impl.setSystemId(ex.getExpandedSystemId());
                locator2Impl.setLineNumber(ex.getLineNumber());
                locator2Impl.setColumnNumber(ex.getColumnNumber());
                throw (exception == null) ? new SAXParseException(ex.getMessage(), locator2Impl) : new SAXParseException(ex.getMessage(), locator2Impl, exception);
            }
            if (exception instanceof SAXException) {
                throw (SAXException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new SAXException(exception);
        }
        catch (final XNIException ex2) {
            final Exception exception2 = ex2.getException();
            if (exception2 == null) {
                throw new SAXException(ex2.getMessage());
            }
            if (exception2 instanceof SAXException) {
                throw (SAXException)exception2;
            }
            if (exception2 instanceof IOException) {
                throw (IOException)exception2;
            }
            throw new SAXException(exception2);
        }
    }
    
    public void parse(final InputSource inputSource) throws SAXException, IOException {
        try {
            final XMLInputSource xmlInputSource = new XMLInputSource(inputSource.getPublicId(), inputSource.getSystemId(), null);
            xmlInputSource.setByteStream(inputSource.getByteStream());
            xmlInputSource.setCharacterStream(inputSource.getCharacterStream());
            xmlInputSource.setEncoding(inputSource.getEncoding());
            this.parse(xmlInputSource);
        }
        catch (final XMLParseException ex) {
            final Exception exception = ex.getException();
            if (exception == null || exception instanceof CharConversionException) {
                final Locator2Impl locator2Impl = new Locator2Impl();
                locator2Impl.setXMLVersion(this.fVersion);
                locator2Impl.setPublicId(ex.getPublicId());
                locator2Impl.setSystemId(ex.getExpandedSystemId());
                locator2Impl.setLineNumber(ex.getLineNumber());
                locator2Impl.setColumnNumber(ex.getColumnNumber());
                throw (exception == null) ? new SAXParseException(ex.getMessage(), locator2Impl) : new SAXParseException(ex.getMessage(), locator2Impl, exception);
            }
            if (exception instanceof SAXException) {
                throw (SAXException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new SAXException(exception);
        }
        catch (final XNIException ex2) {
            final Exception exception2 = ex2.getException();
            if (exception2 == null) {
                throw new SAXException(ex2.getMessage());
            }
            if (exception2 instanceof SAXException) {
                throw (SAXException)exception2;
            }
            if (exception2 instanceof IOException) {
                throw (IOException)exception2;
            }
            throw new SAXException(exception2);
        }
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        try {
            final XMLEntityResolver xmlEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
            if (this.fUseEntityResolver2 && entityResolver instanceof EntityResolver2) {
                if (xmlEntityResolver instanceof EntityResolver2Wrapper) {
                    ((EntityResolver2Wrapper)xmlEntityResolver).setEntityResolver((EntityResolver2)entityResolver);
                }
                else {
                    this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolver2Wrapper((EntityResolver2)entityResolver));
                }
            }
            else if (xmlEntityResolver instanceof EntityResolverWrapper) {
                ((EntityResolverWrapper)xmlEntityResolver).setEntityResolver(entityResolver);
            }
            else {
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolverWrapper(entityResolver));
            }
        }
        catch (final XMLConfigurationException ex) {}
    }
    
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
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        try {
            final XMLErrorHandler xmlErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
            if (xmlErrorHandler instanceof ErrorHandlerWrapper) {
                ((ErrorHandlerWrapper)xmlErrorHandler).setErrorHandler(errorHandler);
            }
            else {
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", new ErrorHandlerWrapper(errorHandler));
            }
        }
        catch (final XMLConfigurationException ex) {}
    }
    
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
    
    public void setLocale(final Locale locale) throws SAXException {
        this.fConfiguration.setLocale(locale);
    }
    
    public void setDTDHandler(final DTDHandler fdtdHandler) {
        this.fDTDHandler = fdtdHandler;
    }
    
    public void setDocumentHandler(final DocumentHandler fDocumentHandler) {
        this.fDocumentHandler = fDocumentHandler;
    }
    
    public void setContentHandler(final ContentHandler fContentHandler) {
        this.fContentHandler = fContentHandler;
    }
    
    public ContentHandler getContentHandler() {
        return this.fContentHandler;
    }
    
    public DTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }
    
    public void setFeature(final String s, final boolean b) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (s.startsWith("http://xml.org/sax/features/")) {
                final int n = s.length() - "http://xml.org/sax/features/".length();
                if (n == "namespaces".length() && s.endsWith("namespaces")) {
                    this.fConfiguration.setFeature(s, b);
                    this.fNamespaces = b;
                    return;
                }
                if (n == "namespace-prefixes".length() && s.endsWith("namespace-prefixes")) {
                    this.fNamespacePrefixes = b;
                    return;
                }
                if (n == "string-interning".length() && s.endsWith("string-interning")) {
                    if (!b) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "false-not-supported", new Object[] { s }));
                    }
                    return;
                }
                else {
                    if (n == "lexical-handler/parameter-entities".length() && s.endsWith("lexical-handler/parameter-entities")) {
                        this.fLexicalHandlerParameterEntities = b;
                        return;
                    }
                    if (n == "resolve-dtd-uris".length() && s.endsWith("resolve-dtd-uris")) {
                        this.fResolveDTDURIs = b;
                        return;
                    }
                    if (n == "unicode-normalization-checking".length() && s.endsWith("unicode-normalization-checking")) {
                        if (b) {
                            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "true-not-supported", new Object[] { s }));
                        }
                        return;
                    }
                    else {
                        if (n == "xmlns-uris".length() && s.endsWith("xmlns-uris")) {
                            this.fXMLNSURIs = b;
                            return;
                        }
                        if (n == "use-entity-resolver2".length() && s.endsWith("use-entity-resolver2")) {
                            if (b != this.fUseEntityResolver2) {
                                this.fUseEntityResolver2 = b;
                                this.setEntityResolver(this.getEntityResolver());
                            }
                            return;
                        }
                        if ((n == "is-standalone".length() && s.endsWith("is-standalone")) || (n == "use-attributes2".length() && s.endsWith("use-attributes2")) || (n == "use-locator2".length() && s.endsWith("use-locator2")) || (n == "xml-1.1".length() && s.endsWith("xml-1.1"))) {
                            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-read-only", new Object[] { s }));
                        }
                    }
                }
            }
            this.fConfiguration.setFeature(s, b);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { identifier }));
        }
    }
    
    public boolean getFeature(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (s.startsWith("http://xml.org/sax/features/")) {
                final int n = s.length() - "http://xml.org/sax/features/".length();
                if (n == "namespace-prefixes".length() && s.endsWith("namespace-prefixes")) {
                    return this.fNamespacePrefixes;
                }
                if (n == "string-interning".length() && s.endsWith("string-interning")) {
                    return true;
                }
                if (n == "is-standalone".length() && s.endsWith("is-standalone")) {
                    return this.fStandalone;
                }
                if (n == "xml-1.1".length() && s.endsWith("xml-1.1")) {
                    return this.fConfiguration instanceof XML11Configurable;
                }
                if (n == "lexical-handler/parameter-entities".length() && s.endsWith("lexical-handler/parameter-entities")) {
                    return this.fLexicalHandlerParameterEntities;
                }
                if (n == "resolve-dtd-uris".length() && s.endsWith("resolve-dtd-uris")) {
                    return this.fResolveDTDURIs;
                }
                if (n == "xmlns-uris".length() && s.endsWith("xmlns-uris")) {
                    return this.fXMLNSURIs;
                }
                if (n == "unicode-normalization-checking".length() && s.endsWith("unicode-normalization-checking")) {
                    return false;
                }
                if (n == "use-entity-resolver2".length() && s.endsWith("use-entity-resolver2")) {
                    return this.fUseEntityResolver2;
                }
                if ((n == "use-attributes2".length() && s.endsWith("use-attributes2")) || (n == "use-locator2".length() && s.endsWith("use-locator2"))) {
                    return true;
                }
            }
            return this.fConfiguration.getFeature(s);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[] { identifier }));
        }
    }
    
    public void setProperty(final String s, final Object o) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (s.startsWith("http://xml.org/sax/properties/")) {
                final int n = s.length() - "http://xml.org/sax/properties/".length();
                if (n == "lexical-handler".length() && s.endsWith("lexical-handler")) {
                    try {
                        this.setLexicalHandler((LexicalHandler)o);
                    }
                    catch (final ClassCastException ex) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "incompatible-class", new Object[] { s, "org.xml.sax.ext.LexicalHandler" }));
                    }
                    return;
                }
                if (n == "declaration-handler".length() && s.endsWith("declaration-handler")) {
                    try {
                        this.setDeclHandler((DeclHandler)o);
                    }
                    catch (final ClassCastException ex2) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "incompatible-class", new Object[] { s, "org.xml.sax.ext.DeclHandler" }));
                    }
                    return;
                }
                if ((n == "dom-node".length() && s.endsWith("dom-node")) || (n == "document-xml-version".length() && s.endsWith("document-xml-version"))) {
                    throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-read-only", new Object[] { s }));
                }
            }
            this.fConfiguration.setProperty(s, o);
        }
        catch (final XMLConfigurationException ex3) {
            final String identifier = ex3.getIdentifier();
            if (ex3.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { identifier }));
        }
    }
    
    public Object getProperty(final String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (s.startsWith("http://xml.org/sax/properties/")) {
                final int n = s.length() - "http://xml.org/sax/properties/".length();
                if (n == "document-xml-version".length() && s.endsWith("document-xml-version")) {
                    return this.fVersion;
                }
                if (n == "lexical-handler".length() && s.endsWith("lexical-handler")) {
                    return this.getLexicalHandler();
                }
                if (n == "declaration-handler".length() && s.endsWith("declaration-handler")) {
                    return this.getDeclHandler();
                }
                if (n == "dom-node".length() && s.endsWith("dom-node")) {
                    throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "dom-node-read-not-supported", null));
                }
            }
            return this.fConfiguration.getProperty(s);
        }
        catch (final XMLConfigurationException ex) {
            final String identifier = ex.getIdentifier();
            if (ex.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[] { identifier }));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[] { identifier }));
        }
    }
    
    protected void setDeclHandler(final DeclHandler fDeclHandler) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (this.fParseInProgress) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-parsing-supported", new Object[] { "http://xml.org/sax/properties/declaration-handler" }));
        }
        this.fDeclHandler = fDeclHandler;
    }
    
    protected DeclHandler getDeclHandler() throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.fDeclHandler;
    }
    
    protected void setLexicalHandler(final LexicalHandler fLexicalHandler) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (this.fParseInProgress) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-parsing-supported", new Object[] { "http://xml.org/sax/properties/lexical-handler" }));
        }
        this.fLexicalHandler = fLexicalHandler;
    }
    
    protected LexicalHandler getLexicalHandler() throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.fLexicalHandler;
    }
    
    protected final void startNamespaceMapping() throws SAXException {
        final int declaredPrefixCount = this.fNamespaceContext.getDeclaredPrefixCount();
        if (declaredPrefixCount > 0) {
            for (int i = 0; i < declaredPrefixCount; ++i) {
                final String declaredPrefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
                final String uri = this.fNamespaceContext.getURI(declaredPrefix);
                this.fContentHandler.startPrefixMapping(declaredPrefix, (uri == null) ? "" : uri);
            }
        }
    }
    
    protected final void endNamespaceMapping() throws SAXException {
        final int declaredPrefixCount = this.fNamespaceContext.getDeclaredPrefixCount();
        if (declaredPrefixCount > 0) {
            for (int i = 0; i < declaredPrefixCount; ++i) {
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
        this.fAugmentations = null;
        this.fDeclaredAttrs = null;
    }
    
    public ElementPSVI getElementPSVI() {
        return (this.fAugmentations != null) ? ((ElementPSVI)this.fAugmentations.getItem("ELEMENT_PSVI")) : null;
    }
    
    public AttributePSVI getAttributePSVI(final int n) {
        return (AttributePSVI)this.fAttributesProxy.fAttributes.getAugmentations(n).getItem("ATTRIBUTE_PSVI");
    }
    
    public AttributePSVI getAttributePSVIByName(final String s, final String s2) {
        return (AttributePSVI)this.fAttributesProxy.fAttributes.getAugmentations(s, s2).getItem("ATTRIBUTE_PSVI");
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/string-interning" };
        RECOGNIZED_PROPERTIES = new String[] { "http://xml.org/sax/properties/lexical-handler", "http://xml.org/sax/properties/declaration-handler", "http://xml.org/sax/properties/dom-node" };
    }
    
    protected static final class AttributesProxy implements AttributeList, Attributes2
    {
        protected XMLAttributes fAttributes;
        
        public void setAttributes(final XMLAttributes fAttributes) {
            this.fAttributes = fAttributes;
        }
        
        public int getLength() {
            return this.fAttributes.getLength();
        }
        
        public String getName(final int n) {
            return this.fAttributes.getQName(n);
        }
        
        public String getQName(final int n) {
            return this.fAttributes.getQName(n);
        }
        
        public String getURI(final int n) {
            final String uri = this.fAttributes.getURI(n);
            return (uri != null) ? uri : "";
        }
        
        public String getLocalName(final int n) {
            return this.fAttributes.getLocalName(n);
        }
        
        public String getType(final int n) {
            return this.fAttributes.getType(n);
        }
        
        public String getType(final String s) {
            return this.fAttributes.getType(s);
        }
        
        public String getType(final String s, final String s2) {
            return (s.length() == 0) ? this.fAttributes.getType(null, s2) : this.fAttributes.getType(s, s2);
        }
        
        public String getValue(final int n) {
            return this.fAttributes.getValue(n);
        }
        
        public String getValue(final String s) {
            return this.fAttributes.getValue(s);
        }
        
        public String getValue(final String s, final String s2) {
            return (s.length() == 0) ? this.fAttributes.getValue(null, s2) : this.fAttributes.getValue(s, s2);
        }
        
        public int getIndex(final String s) {
            return this.fAttributes.getIndex(s);
        }
        
        public int getIndex(final String s, final String s2) {
            return (s.length() == 0) ? this.fAttributes.getIndex(null, s2) : this.fAttributes.getIndex(s, s2);
        }
        
        public boolean isDeclared(final int n) {
            if (n < 0 || n >= this.fAttributes.getLength()) {
                throw new ArrayIndexOutOfBoundsException(n);
            }
            return Boolean.TRUE.equals(this.fAttributes.getAugmentations(n).getItem("ATTRIBUTE_DECLARED"));
        }
        
        public boolean isDeclared(final String s) {
            final int index = this.getIndex(s);
            if (index == -1) {
                throw new IllegalArgumentException(s);
            }
            return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
        }
        
        public boolean isDeclared(final String s, final String s2) {
            final int index = this.getIndex(s, s2);
            if (index == -1) {
                throw new IllegalArgumentException(s2);
            }
            return Boolean.TRUE.equals(this.fAttributes.getAugmentations(index).getItem("ATTRIBUTE_DECLARED"));
        }
        
        public boolean isSpecified(final int n) {
            if (n < 0 || n >= this.fAttributes.getLength()) {
                throw new ArrayIndexOutOfBoundsException(n);
            }
            return this.fAttributes.isSpecified(n);
        }
        
        public boolean isSpecified(final String s) {
            final int index = this.getIndex(s);
            if (index == -1) {
                throw new IllegalArgumentException(s);
            }
            return this.fAttributes.isSpecified(index);
        }
        
        public boolean isSpecified(final String s, final String s2) {
            final int index = this.getIndex(s, s2);
            if (index == -1) {
                throw new IllegalArgumentException(s2);
            }
            return this.fAttributes.isSpecified(index);
        }
    }
    
    protected static final class LocatorProxy implements Locator2
    {
        protected XMLLocator fLocator;
        
        public LocatorProxy(final XMLLocator fLocator) {
            this.fLocator = fLocator;
        }
        
        public String getPublicId() {
            return this.fLocator.getPublicId();
        }
        
        public String getSystemId() {
            return this.fLocator.getExpandedSystemId();
        }
        
        public int getLineNumber() {
            return this.fLocator.getLineNumber();
        }
        
        public int getColumnNumber() {
            return this.fLocator.getColumnNumber();
        }
        
        public String getXMLVersion() {
            return this.fLocator.getXMLVersion();
        }
        
        public String getEncoding() {
            return this.fLocator.getEncoding();
        }
    }
}
