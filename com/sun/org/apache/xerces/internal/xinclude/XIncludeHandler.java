package com.sun.org.apache.xerces.internal.xinclude;

import java.io.UnsupportedEncodingException;
import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.util.Objects;
import java.util.Enumeration;
import java.util.StringTokenizer;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import java.util.Locale;
import java.io.CharConversionException;
import com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException;
import com.sun.org.apache.xerces.internal.xpointer.XPointerHandler;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.util.HTTPInputSource;
import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import java.util.ArrayList;
import com.sun.org.apache.xerces.internal.util.ParserConfigurationSettings;
import java.util.Stack;
import com.sun.org.apache.xerces.internal.util.IntStack;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLEntityResolver;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xpointer.XPointerProcessor;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDSource;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentSource;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;

public class XIncludeHandler implements XMLComponent, XMLDocumentFilter, XMLDTDFilter
{
    public static final String XINCLUDE_DEFAULT_CONFIGURATION = "com.sun.org.apache.xerces.internal.parsers.XIncludeParserConfiguration";
    public static final String HTTP_ACCEPT = "Accept";
    public static final String HTTP_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String XPOINTER = "xpointer";
    public static final String XINCLUDE_NS_URI;
    public static final String XINCLUDE_INCLUDE;
    public static final String XINCLUDE_FALLBACK;
    public static final String XINCLUDE_PARSE_XML;
    public static final String XINCLUDE_PARSE_TEXT;
    public static final String XINCLUDE_ATTR_HREF;
    public static final String XINCLUDE_ATTR_PARSE;
    public static final String XINCLUDE_ATTR_ENCODING;
    public static final String XINCLUDE_ATTR_ACCEPT;
    public static final String XINCLUDE_ATTR_ACCEPT_LANGUAGE;
    public static final String XINCLUDE_INCLUDED;
    public static final String CURRENT_BASE_URI = "currentBaseURI";
    public static final String XINCLUDE_BASE;
    public static final QName XML_BASE_QNAME;
    public static final String XINCLUDE_LANG;
    public static final QName XML_LANG_QNAME;
    public static final QName NEW_NS_ATTR_QNAME;
    private static final int STATE_NORMAL_PROCESSING = 1;
    private static final int STATE_IGNORE = 2;
    private static final int STATE_EXPECT_FALLBACK = 3;
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
    protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
    protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
    protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
    protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    public static final String BUFFER_SIZE = "http://apache.org/xml/properties/input-buffer-size";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] FEATURE_DEFAULTS;
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] PROPERTY_DEFAULTS;
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDocumentSource fDocumentSource;
    protected XMLDTDHandler fDTDHandler;
    protected XMLDTDSource fDTDSource;
    protected XIncludeHandler fParentXIncludeHandler;
    protected int fBufferSize;
    protected String fParentRelativeURI;
    protected XMLParserConfiguration fChildConfig;
    protected XMLParserConfiguration fXIncludeChildConfig;
    protected XMLParserConfiguration fXPointerChildConfig;
    protected XPointerProcessor fXPtrProcessor;
    protected XMLLocator fDocLocation;
    protected XIncludeMessageFormatter fXIncludeMessageFormatter;
    protected XIncludeNamespaceSupport fNamespaceContext;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityResolver fEntityResolver;
    protected XMLSecurityManager fSecurityManager;
    protected XMLSecurityPropertyManager fSecurityPropertyMgr;
    protected XIncludeTextReader fXInclude10TextReader;
    protected XIncludeTextReader fXInclude11TextReader;
    protected XMLResourceIdentifier fCurrentBaseURI;
    protected IntStack fBaseURIScope;
    protected Stack fBaseURI;
    protected Stack fLiteralSystemID;
    protected Stack fExpandedSystemID;
    protected IntStack fLanguageScope;
    protected Stack fLanguageStack;
    protected String fCurrentLanguage;
    protected ParserConfigurationSettings fSettings;
    private int fDepth;
    private int fResultDepth;
    private static final int INITIAL_SIZE = 8;
    private boolean[] fSawInclude;
    private boolean[] fSawFallback;
    private int[] fState;
    private ArrayList fNotations;
    private ArrayList fUnparsedEntities;
    private boolean fFixupBaseURIs;
    private boolean fFixupLanguage;
    private boolean fSendUEAndNotationEvents;
    private boolean fIsXML11;
    private boolean fInDTD;
    private boolean fSeenRootElement;
    private boolean fNeedCopyFeatures;
    private static final boolean[] gNeedEscaping;
    private static final char[] gAfterEscaping1;
    private static final char[] gAfterEscaping2;
    private static final char[] gHexChs;
    
    public XIncludeHandler() {
        this.fBufferSize = 8192;
        this.fXPtrProcessor = null;
        this.fXIncludeMessageFormatter = new XIncludeMessageFormatter();
        this.fSawInclude = new boolean[8];
        this.fSawFallback = new boolean[8];
        this.fState = new int[8];
        this.fFixupBaseURIs = true;
        this.fFixupLanguage = true;
        this.fNeedCopyFeatures = true;
        this.fDepth = 0;
        this.fSawFallback[this.fDepth] = false;
        this.fSawInclude[this.fDepth] = false;
        this.fState[this.fDepth] = 1;
        this.fNotations = new ArrayList();
        this.fUnparsedEntities = new ArrayList();
        this.fBaseURIScope = new IntStack();
        this.fBaseURI = new Stack();
        this.fLiteralSystemID = new Stack();
        this.fExpandedSystemID = new Stack();
        this.fCurrentBaseURI = new XMLResourceIdentifierImpl();
        this.fLanguageScope = new IntStack();
        this.fLanguageStack = new Stack();
        this.fCurrentLanguage = null;
    }
    
    @Override
    public void reset(final XMLComponentManager componentManager) throws XNIException {
        this.fNamespaceContext = null;
        this.fDepth = 0;
        this.fResultDepth = (this.isRootDocument() ? 0 : this.fParentXIncludeHandler.getResultDepth());
        this.fNotations.clear();
        this.fUnparsedEntities.clear();
        this.fParentRelativeURI = null;
        this.fIsXML11 = false;
        this.fInDTD = false;
        this.fSeenRootElement = false;
        this.fBaseURIScope.clear();
        this.fBaseURI.clear();
        this.fLiteralSystemID.clear();
        this.fExpandedSystemID.clear();
        this.fLanguageScope.clear();
        this.fLanguageStack.clear();
        for (int i = 0; i < this.fState.length; ++i) {
            this.fState[i] = 1;
        }
        for (int i = 0; i < this.fSawFallback.length; ++i) {
            this.fSawFallback[i] = false;
        }
        for (int i = 0; i < this.fSawInclude.length; ++i) {
            this.fSawInclude[i] = false;
        }
        try {
            if (!componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings")) {
                return;
            }
        }
        catch (final XMLConfigurationException ex) {}
        this.fNeedCopyFeatures = true;
        try {
            this.fSendUEAndNotationEvents = componentManager.getFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD");
            if (this.fChildConfig != null) {
                this.fChildConfig.setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", this.fSendUEAndNotationEvents);
            }
        }
        catch (final XMLConfigurationException ex2) {}
        try {
            this.fFixupBaseURIs = componentManager.getFeature("http://apache.org/xml/features/xinclude/fixup-base-uris");
            if (this.fChildConfig != null) {
                this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", this.fFixupBaseURIs);
            }
        }
        catch (final XMLConfigurationException e) {
            this.fFixupBaseURIs = true;
        }
        try {
            this.fFixupLanguage = componentManager.getFeature("http://apache.org/xml/features/xinclude/fixup-language");
            if (this.fChildConfig != null) {
                this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-language", this.fFixupLanguage);
            }
        }
        catch (final XMLConfigurationException e) {
            this.fFixupLanguage = true;
        }
        try {
            final SymbolTable value = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
            if (value != null) {
                this.fSymbolTable = value;
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/symbol-table", value);
                }
            }
        }
        catch (final XMLConfigurationException e) {
            this.fSymbolTable = null;
        }
        try {
            final XMLErrorReporter value2 = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
            if (value2 != null) {
                this.setErrorReporter(value2);
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/error-reporter", value2);
                }
            }
        }
        catch (final XMLConfigurationException e) {
            this.fErrorReporter = null;
        }
        try {
            final XMLEntityResolver value3 = (XMLEntityResolver)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
            if (value3 != null) {
                this.fEntityResolver = value3;
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/entity-resolver", value3);
                }
            }
        }
        catch (final XMLConfigurationException e) {
            this.fEntityResolver = null;
        }
        try {
            final XMLSecurityManager value4 = (XMLSecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager");
            if (value4 != null) {
                this.fSecurityManager = value4;
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty("http://apache.org/xml/properties/security-manager", value4);
                }
            }
        }
        catch (final XMLConfigurationException e) {
            this.fSecurityManager = null;
        }
        this.fSecurityPropertyMgr = (XMLSecurityPropertyManager)componentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
        try {
            final Integer value5 = (Integer)componentManager.getProperty("http://apache.org/xml/properties/input-buffer-size");
            if (value5 != null && value5 > 0) {
                this.fBufferSize = value5;
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty("http://apache.org/xml/properties/input-buffer-size", value5);
                }
            }
            else {
                this.fBufferSize = (int)this.getPropertyDefault("http://apache.org/xml/properties/input-buffer-size");
            }
        }
        catch (final XMLConfigurationException e) {
            this.fBufferSize = (int)this.getPropertyDefault("http://apache.org/xml/properties/input-buffer-size");
        }
        if (this.fXInclude10TextReader != null) {
            this.fXInclude10TextReader.setBufferSize(this.fBufferSize);
        }
        if (this.fXInclude11TextReader != null) {
            this.fXInclude11TextReader.setBufferSize(this.fBufferSize);
        }
        this.copyFeatures(componentManager, this.fSettings = new ParserConfigurationSettings());
        try {
            if (componentManager.getFeature("http://apache.org/xml/features/validation/schema")) {
                this.fSettings.setFeature("http://apache.org/xml/features/validation/schema", false);
                if (componentManager.getFeature("http://xml.org/sax/features/validation")) {
                    this.fSettings.setFeature("http://apache.org/xml/features/validation/dynamic", true);
                }
            }
        }
        catch (final XMLConfigurationException ex3) {}
    }
    
    @Override
    public String[] getRecognizedFeatures() {
        return XIncludeHandler.RECOGNIZED_FEATURES.clone();
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        if (featureId.equals("http://xml.org/sax/features/allow-dtd-events-after-endDTD")) {
            this.fSendUEAndNotationEvents = state;
        }
        if (this.fSettings != null) {
            this.fNeedCopyFeatures = true;
            this.fSettings.setFeature(featureId, state);
        }
    }
    
    @Override
    public String[] getRecognizedProperties() {
        return XIncludeHandler.RECOGNIZED_PROPERTIES.clone();
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        if (propertyId.equals("http://apache.org/xml/properties/internal/symbol-table")) {
            this.fSymbolTable = (SymbolTable)value;
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(propertyId, value);
            }
            return;
        }
        if (propertyId.equals("http://apache.org/xml/properties/internal/error-reporter")) {
            this.setErrorReporter((XMLErrorReporter)value);
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(propertyId, value);
            }
            return;
        }
        if (propertyId.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
            this.fEntityResolver = (XMLEntityResolver)value;
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(propertyId, value);
            }
            return;
        }
        if (propertyId.equals("http://apache.org/xml/properties/security-manager")) {
            this.fSecurityManager = (XMLSecurityManager)value;
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(propertyId, value);
            }
            return;
        }
        if (propertyId.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
            this.fSecurityPropertyMgr = (XMLSecurityPropertyManager)value;
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", value);
            }
            return;
        }
        if (propertyId.equals("http://apache.org/xml/properties/input-buffer-size")) {
            final Integer bufferSize = (Integer)value;
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(propertyId, value);
            }
            if (bufferSize != null && bufferSize > 0) {
                this.fBufferSize = bufferSize;
                if (this.fXInclude10TextReader != null) {
                    this.fXInclude10TextReader.setBufferSize(this.fBufferSize);
                }
                if (this.fXInclude11TextReader != null) {
                    this.fXInclude11TextReader.setBufferSize(this.fBufferSize);
                }
            }
        }
    }
    
    @Override
    public Boolean getFeatureDefault(final String featureId) {
        for (int i = 0; i < XIncludeHandler.RECOGNIZED_FEATURES.length; ++i) {
            if (XIncludeHandler.RECOGNIZED_FEATURES[i].equals(featureId)) {
                return XIncludeHandler.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    @Override
    public Object getPropertyDefault(final String propertyId) {
        for (int i = 0; i < XIncludeHandler.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XIncludeHandler.RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return XIncludeHandler.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    @Override
    public void setDocumentHandler(final XMLDocumentHandler handler) {
        this.fDocumentHandler = handler;
    }
    
    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    @Override
    public void startDocument(final XMLLocator locator, final String encoding, final NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
        this.fErrorReporter.setDocumentLocator(locator);
        if (!this.isRootDocument() && this.fParentXIncludeHandler.searchForRecursiveIncludes(locator)) {
            this.reportFatalError("RecursiveInclude", new Object[] { locator.getExpandedSystemId() });
        }
        if (!(namespaceContext instanceof XIncludeNamespaceSupport)) {
            this.reportFatalError("IncompatibleNamespaceContext");
        }
        this.fNamespaceContext = (XIncludeNamespaceSupport)namespaceContext;
        this.fDocLocation = locator;
        this.fCurrentBaseURI.setBaseSystemId(locator.getBaseSystemId());
        this.fCurrentBaseURI.setExpandedSystemId(locator.getExpandedSystemId());
        this.fCurrentBaseURI.setLiteralSystemId(locator.getLiteralSystemId());
        this.saveBaseURI();
        if (augs == null) {
            augs = new AugmentationsImpl();
        }
        augs.putItem("currentBaseURI", this.fCurrentBaseURI);
        this.saveLanguage(this.fCurrentLanguage = XMLSymbols.EMPTY_STRING);
        if (this.isRootDocument() && this.fDocumentHandler != null) {
            this.fDocumentHandler.startDocument(locator, encoding, namespaceContext, augs);
        }
    }
    
    @Override
    public void xmlDecl(final String version, final String encoding, final String standalone, final Augmentations augs) throws XNIException {
        this.fIsXML11 = "1.1".equals(version);
        if (this.isRootDocument() && this.fDocumentHandler != null) {
            this.fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
        }
    }
    
    @Override
    public void doctypeDecl(final String rootElement, final String publicId, final String systemId, final Augmentations augs) throws XNIException {
        if (this.isRootDocument() && this.fDocumentHandler != null) {
            this.fDocumentHandler.doctypeDecl(rootElement, publicId, systemId, augs);
        }
    }
    
    @Override
    public void comment(final XMLString text, Augmentations augs) throws XNIException {
        if (!this.fInDTD) {
            if (this.fDocumentHandler != null && this.getState() == 1) {
                ++this.fDepth;
                augs = this.modifyAugmentations(augs);
                this.fDocumentHandler.comment(text, augs);
                --this.fDepth;
            }
        }
        else if (this.fDTDHandler != null) {
            this.fDTDHandler.comment(text, augs);
        }
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, Augmentations augs) throws XNIException {
        if (!this.fInDTD) {
            if (this.fDocumentHandler != null && this.getState() == 1) {
                ++this.fDepth;
                augs = this.modifyAugmentations(augs);
                this.fDocumentHandler.processingInstruction(target, data, augs);
                --this.fDepth;
            }
        }
        else if (this.fDTDHandler != null) {
            this.fDTDHandler.processingInstruction(target, data, augs);
        }
    }
    
    @Override
    public void startElement(final QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        ++this.fDepth;
        final int lastState = this.getState(this.fDepth - 1);
        if (lastState == 3 && this.getState(this.fDepth - 2) == 3) {
            this.setState(2);
        }
        else {
            this.setState(lastState);
        }
        this.processXMLBaseAttributes(attributes);
        if (this.fFixupLanguage) {
            this.processXMLLangAttributes(attributes);
        }
        if (this.isIncludeElement(element)) {
            final boolean success = this.handleIncludeElement(attributes);
            if (success) {
                this.setState(2);
            }
            else {
                this.setState(3);
            }
        }
        else if (this.isFallbackElement(element)) {
            this.handleFallbackElement();
        }
        else if (this.hasXIncludeNamespace(element)) {
            if (this.getSawInclude(this.fDepth - 1)) {
                this.reportFatalError("IncludeChild", new Object[] { element.rawname });
            }
            if (this.getSawFallback(this.fDepth - 1)) {
                this.reportFatalError("FallbackChild", new Object[] { element.rawname });
            }
            if (this.getState() == 1) {
                if (this.fResultDepth++ == 0) {
                    this.checkMultipleRootElements();
                }
                if (this.fDocumentHandler != null) {
                    augs = this.modifyAugmentations(augs);
                    attributes = this.processAttributes(attributes);
                    this.fDocumentHandler.startElement(element, attributes, augs);
                }
            }
        }
        else if (this.getState() == 1) {
            if (this.fResultDepth++ == 0) {
                this.checkMultipleRootElements();
            }
            if (this.fDocumentHandler != null) {
                augs = this.modifyAugmentations(augs);
                attributes = this.processAttributes(attributes);
                this.fDocumentHandler.startElement(element, attributes, augs);
            }
        }
    }
    
    @Override
    public void emptyElement(final QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        ++this.fDepth;
        final int lastState = this.getState(this.fDepth - 1);
        if (lastState == 3 && this.getState(this.fDepth - 2) == 3) {
            this.setState(2);
        }
        else {
            this.setState(lastState);
        }
        this.processXMLBaseAttributes(attributes);
        if (this.fFixupLanguage) {
            this.processXMLLangAttributes(attributes);
        }
        if (this.isIncludeElement(element)) {
            final boolean success = this.handleIncludeElement(attributes);
            if (success) {
                this.setState(2);
            }
            else {
                this.reportFatalError("NoFallback", new Object[] { attributes.getValue(null, "href") });
            }
        }
        else if (this.isFallbackElement(element)) {
            this.handleFallbackElement();
        }
        else if (this.hasXIncludeNamespace(element)) {
            if (this.getSawInclude(this.fDepth - 1)) {
                this.reportFatalError("IncludeChild", new Object[] { element.rawname });
            }
            if (this.getSawFallback(this.fDepth - 1)) {
                this.reportFatalError("FallbackChild", new Object[] { element.rawname });
            }
            if (this.getState() == 1) {
                if (this.fResultDepth == 0) {
                    this.checkMultipleRootElements();
                }
                if (this.fDocumentHandler != null) {
                    augs = this.modifyAugmentations(augs);
                    attributes = this.processAttributes(attributes);
                    this.fDocumentHandler.emptyElement(element, attributes, augs);
                }
            }
        }
        else if (this.getState() == 1) {
            if (this.fResultDepth == 0) {
                this.checkMultipleRootElements();
            }
            if (this.fDocumentHandler != null) {
                augs = this.modifyAugmentations(augs);
                attributes = this.processAttributes(attributes);
                this.fDocumentHandler.emptyElement(element, attributes, augs);
            }
        }
        this.setSawFallback(this.fDepth + 1, false);
        this.setSawInclude(this.fDepth, false);
        if (this.fBaseURIScope.size() > 0 && this.fDepth == this.fBaseURIScope.peek()) {
            this.restoreBaseURI();
        }
        --this.fDepth;
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        if (this.isIncludeElement(element) && this.getState() == 3 && !this.getSawFallback(this.fDepth + 1)) {
            this.reportFatalError("NoFallback", new Object[] { "unknown" });
        }
        if (this.isFallbackElement(element)) {
            if (this.getState() == 1) {
                this.setState(2);
            }
        }
        else if (this.getState() == 1) {
            --this.fResultDepth;
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.endElement(element, augs);
            }
        }
        this.setSawFallback(this.fDepth + 1, false);
        this.setSawInclude(this.fDepth, false);
        if (this.fBaseURIScope.size() > 0 && this.fDepth == this.fBaseURIScope.peek()) {
            this.restoreBaseURI();
        }
        if (this.fLanguageScope.size() > 0 && this.fDepth == this.fLanguageScope.peek()) {
            this.fCurrentLanguage = this.restoreLanguage();
        }
        --this.fDepth;
    }
    
    @Override
    public void startGeneralEntity(final String name, final XMLResourceIdentifier resId, final String encoding, final Augmentations augs) throws XNIException {
        if (this.getState() == 1) {
            if (this.fResultDepth == 0) {
                if (augs != null && Boolean.TRUE.equals(augs.getItem("ENTITY_SKIPPED"))) {
                    this.reportFatalError("UnexpandedEntityReferenceIllegal");
                }
            }
            else if (this.fDocumentHandler != null) {
                this.fDocumentHandler.startGeneralEntity(name, resId, encoding, augs);
            }
        }
    }
    
    @Override
    public void textDecl(final String version, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1) {
            this.fDocumentHandler.textDecl(version, encoding, augs);
        }
    }
    
    @Override
    public void endGeneralEntity(final String name, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
            this.fDocumentHandler.endGeneralEntity(name, augs);
        }
    }
    
    @Override
    public void characters(final XMLString text, Augmentations augs) throws XNIException {
        if (this.getState() == 1) {
            if (this.fResultDepth == 0) {
                this.checkWhitespace(text);
            }
            else if (this.fDocumentHandler != null) {
                ++this.fDepth;
                augs = this.modifyAugmentations(augs);
                this.fDocumentHandler.characters(text, augs);
                --this.fDepth;
            }
        }
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
            this.fDocumentHandler.ignorableWhitespace(text, augs);
        }
    }
    
    @Override
    public void startCDATA(final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
            this.fDocumentHandler.startCDATA(augs);
        }
    }
    
    @Override
    public void endCDATA(final Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
            this.fDocumentHandler.endCDATA(augs);
        }
    }
    
    @Override
    public void endDocument(final Augmentations augs) throws XNIException {
        if (this.isRootDocument()) {
            if (!this.fSeenRootElement) {
                this.reportFatalError("RootElementRequired");
            }
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.endDocument(augs);
            }
        }
    }
    
    @Override
    public void setDocumentSource(final XMLDocumentSource source) {
        this.fDocumentSource = source;
    }
    
    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }
    
    @Override
    public void attributeDecl(final String elementName, final String attributeName, final String type, final String[] enumeration, final String defaultType, final XMLString defaultValue, final XMLString nonNormalizedDefaultValue, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.attributeDecl(elementName, attributeName, type, enumeration, defaultType, defaultValue, nonNormalizedDefaultValue, augmentations);
        }
    }
    
    @Override
    public void elementDecl(final String name, final String contentModel, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.elementDecl(name, contentModel, augmentations);
        }
    }
    
    @Override
    public void endAttlist(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endAttlist(augmentations);
        }
    }
    
    @Override
    public void endConditional(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endConditional(augmentations);
        }
    }
    
    @Override
    public void endDTD(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endDTD(augmentations);
        }
        this.fInDTD = false;
    }
    
    @Override
    public void endExternalSubset(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endExternalSubset(augmentations);
        }
    }
    
    @Override
    public void endParameterEntity(final String name, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endParameterEntity(name, augmentations);
        }
    }
    
    @Override
    public void externalEntityDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.externalEntityDecl(name, identifier, augmentations);
        }
    }
    
    @Override
    public XMLDTDSource getDTDSource() {
        return this.fDTDSource;
    }
    
    @Override
    public void ignoredCharacters(final XMLString text, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.ignoredCharacters(text, augmentations);
        }
    }
    
    @Override
    public void internalEntityDecl(final String name, final XMLString text, final XMLString nonNormalizedText, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.internalEntityDecl(name, text, nonNormalizedText, augmentations);
        }
    }
    
    @Override
    public void notationDecl(final String name, final XMLResourceIdentifier identifier, final Augmentations augmentations) throws XNIException {
        this.addNotation(name, identifier, augmentations);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.notationDecl(name, identifier, augmentations);
        }
    }
    
    @Override
    public void setDTDSource(final XMLDTDSource source) {
        this.fDTDSource = source;
    }
    
    @Override
    public void startAttlist(final String elementName, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startAttlist(elementName, augmentations);
        }
    }
    
    @Override
    public void startConditional(final short type, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startConditional(type, augmentations);
        }
    }
    
    @Override
    public void startDTD(final XMLLocator locator, final Augmentations augmentations) throws XNIException {
        this.fInDTD = true;
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startDTD(locator, augmentations);
        }
    }
    
    @Override
    public void startExternalSubset(final XMLResourceIdentifier identifier, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startExternalSubset(identifier, augmentations);
        }
    }
    
    @Override
    public void startParameterEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startParameterEntity(name, identifier, encoding, augmentations);
        }
    }
    
    @Override
    public void unparsedEntityDecl(final String name, final XMLResourceIdentifier identifier, final String notation, final Augmentations augmentations) throws XNIException {
        this.addUnparsedEntity(name, identifier, notation, augmentations);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.unparsedEntityDecl(name, identifier, notation, augmentations);
        }
    }
    
    @Override
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }
    
    @Override
    public void setDTDHandler(final XMLDTDHandler handler) {
        this.fDTDHandler = handler;
    }
    
    private void setErrorReporter(final XMLErrorReporter reporter) {
        this.fErrorReporter = reporter;
        if (this.fErrorReporter != null) {
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xinclude", this.fXIncludeMessageFormatter);
            if (this.fDocLocation != null) {
                this.fErrorReporter.setDocumentLocator(this.fDocLocation);
            }
        }
    }
    
    protected void handleFallbackElement() {
        if (!this.getSawInclude(this.fDepth - 1)) {
            if (this.getState() == 2) {
                return;
            }
            this.reportFatalError("FallbackParent");
        }
        this.setSawInclude(this.fDepth, false);
        this.fNamespaceContext.setContextInvalid();
        if (this.getSawFallback(this.fDepth)) {
            this.reportFatalError("MultipleFallbacks");
        }
        else {
            this.setSawFallback(this.fDepth, true);
        }
        if (this.getState() == 3) {
            this.setState(1);
        }
    }
    
    protected boolean handleIncludeElement(final XMLAttributes attributes) throws XNIException {
        if (this.getSawInclude(this.fDepth - 1)) {
            this.reportFatalError("IncludeChild", new Object[] { XIncludeHandler.XINCLUDE_INCLUDE });
        }
        if (this.getState() == 2) {
            return true;
        }
        this.setSawInclude(this.fDepth, true);
        this.fNamespaceContext.setContextInvalid();
        String href = attributes.getValue(XIncludeHandler.XINCLUDE_ATTR_HREF);
        String parse = attributes.getValue(XIncludeHandler.XINCLUDE_ATTR_PARSE);
        final String xpointer = attributes.getValue("xpointer");
        String accept = attributes.getValue(XIncludeHandler.XINCLUDE_ATTR_ACCEPT);
        String acceptLanguage = attributes.getValue(XIncludeHandler.XINCLUDE_ATTR_ACCEPT_LANGUAGE);
        if (parse == null) {
            parse = XIncludeHandler.XINCLUDE_PARSE_XML;
        }
        if (href == null) {
            href = XMLSymbols.EMPTY_STRING;
        }
        if (href.length() == 0 && XIncludeHandler.XINCLUDE_PARSE_XML.equals(parse)) {
            if (xpointer != null) {
                final Locale locale = (this.fErrorReporter != null) ? this.fErrorReporter.getLocale() : null;
                final String reason = this.fXIncludeMessageFormatter.formatMessage(locale, "XPointerStreamability", null);
                this.reportResourceError("XMLResourceError", new Object[] { href, reason });
                return false;
            }
            this.reportFatalError("XpointerMissing");
        }
        URI hrefURI = null;
        try {
            hrefURI = new URI(href, true);
            if (hrefURI.getFragment() != null) {
                this.reportFatalError("HrefFragmentIdentifierIllegal", new Object[] { href });
            }
        }
        catch (final URI.MalformedURIException exc) {
            final String newHref = this.escapeHref(href);
            if (href != newHref) {
                href = newHref;
                try {
                    hrefURI = new URI(href, true);
                    if (hrefURI.getFragment() != null) {
                        this.reportFatalError("HrefFragmentIdentifierIllegal", new Object[] { href });
                    }
                }
                catch (final URI.MalformedURIException exc2) {
                    this.reportFatalError("HrefSyntacticallyInvalid", new Object[] { href });
                }
            }
            else {
                this.reportFatalError("HrefSyntacticallyInvalid", new Object[] { href });
            }
        }
        if (accept != null && !this.isValidInHTTPHeader(accept)) {
            this.reportFatalError("AcceptMalformed", null);
            accept = null;
        }
        if (acceptLanguage != null && !this.isValidInHTTPHeader(acceptLanguage)) {
            this.reportFatalError("AcceptLanguageMalformed", null);
            acceptLanguage = null;
        }
        XMLInputSource includedSource = null;
        if (this.fEntityResolver != null) {
            try {
                final XMLResourceIdentifier resourceIdentifier = new XMLResourceIdentifierImpl(null, href, this.fCurrentBaseURI.getExpandedSystemId(), XMLEntityManager.expandSystemId(href, this.fCurrentBaseURI.getExpandedSystemId(), false));
                includedSource = this.fEntityResolver.resolveEntity(resourceIdentifier);
                if (includedSource != null && !(includedSource instanceof HTTPInputSource) && (accept != null || acceptLanguage != null) && includedSource.getCharacterStream() == null && includedSource.getByteStream() == null) {
                    includedSource = this.createInputSource(includedSource.getPublicId(), includedSource.getSystemId(), includedSource.getBaseSystemId(), accept, acceptLanguage);
                }
            }
            catch (final IOException e) {
                this.reportResourceError("XMLResourceError", new Object[] { href, e.getMessage() });
                return false;
            }
        }
        if (includedSource == null) {
            if (accept != null || acceptLanguage != null) {
                includedSource = this.createInputSource(null, href, this.fCurrentBaseURI.getExpandedSystemId(), accept, acceptLanguage);
            }
            else {
                includedSource = new XMLInputSource(null, href, this.fCurrentBaseURI.getExpandedSystemId());
            }
        }
        if (parse.equals(XIncludeHandler.XINCLUDE_PARSE_XML)) {
            if ((xpointer != null && this.fXPointerChildConfig == null) || (xpointer == null && this.fXIncludeChildConfig == null)) {
                String parserName = "com.sun.org.apache.xerces.internal.parsers.XIncludeParserConfiguration";
                if (xpointer != null) {
                    parserName = "com.sun.org.apache.xerces.internal.parsers.XPointerParserConfiguration";
                }
                this.fChildConfig = (XMLParserConfiguration)ObjectFactory.newInstance(parserName, true);
                if (this.fSymbolTable != null) {
                    this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/symbol-table", this.fSymbolTable);
                }
                if (this.fErrorReporter != null) {
                    this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
                }
                if (this.fEntityResolver != null) {
                    this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/entity-resolver", this.fEntityResolver);
                }
                this.fChildConfig.setProperty("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
                this.fChildConfig.setProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
                this.fChildConfig.setProperty("http://apache.org/xml/properties/input-buffer-size", new Integer(this.fBufferSize));
                this.fNeedCopyFeatures = true;
                this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNamespaceContext);
                this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", this.fFixupBaseURIs);
                this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-language", this.fFixupLanguage);
                if (xpointer != null) {
                    final XPointerHandler newHandler = (XPointerHandler)this.fChildConfig.getProperty("http://apache.org/xml/properties/internal/xpointer-handler");
                    this.fXPtrProcessor = newHandler;
                    ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNamespaceContext);
                    ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/features/xinclude/fixup-base-uris", this.fFixupBaseURIs);
                    ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/features/xinclude/fixup-language", this.fFixupLanguage);
                    if (this.fErrorReporter != null) {
                        ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
                    }
                    newHandler.setParent(this);
                    newHandler.setDocumentHandler(this.getDocumentHandler());
                    this.fXPointerChildConfig = this.fChildConfig;
                }
                else {
                    final XIncludeHandler newHandler2 = (XIncludeHandler)this.fChildConfig.getProperty("http://apache.org/xml/properties/internal/xinclude-handler");
                    newHandler2.setParent(this);
                    newHandler2.setDocumentHandler(this.getDocumentHandler());
                    this.fXIncludeChildConfig = this.fChildConfig;
                }
            }
            Label_1051: {
                if (xpointer != null) {
                    this.fChildConfig = this.fXPointerChildConfig;
                    try {
                        this.fXPtrProcessor.parseXPointer(xpointer);
                        break Label_1051;
                    }
                    catch (final XNIException ex) {
                        this.reportResourceError("XMLResourceError", new Object[] { href, ex.getMessage() });
                        return false;
                    }
                }
                this.fChildConfig = this.fXIncludeChildConfig;
            }
            if (this.fNeedCopyFeatures) {
                this.copyFeatures(this.fSettings, this.fChildConfig);
            }
            this.fNeedCopyFeatures = false;
            try {
                this.fNamespaceContext.pushScope();
                this.fChildConfig.parse(includedSource);
                if (this.fErrorReporter != null) {
                    this.fErrorReporter.setDocumentLocator(this.fDocLocation);
                }
                if (xpointer != null && !this.fXPtrProcessor.isXPointerResolved()) {
                    final Locale locale2 = (this.fErrorReporter != null) ? this.fErrorReporter.getLocale() : null;
                    final String reason2 = this.fXIncludeMessageFormatter.formatMessage(locale2, "XPointerResolutionUnsuccessful", null);
                    this.reportResourceError("XMLResourceError", new Object[] { href, reason2 });
                    return false;
                }
                return true;
            }
            catch (final XNIException e2) {
                if (this.fErrorReporter != null) {
                    this.fErrorReporter.setDocumentLocator(this.fDocLocation);
                }
                this.reportFatalError("XMLParseError", new Object[] { href, e2.getMessage() });
            }
            catch (final IOException e) {
                if (this.fErrorReporter != null) {
                    this.fErrorReporter.setDocumentLocator(this.fDocLocation);
                }
                this.reportResourceError("XMLResourceError", new Object[] { href, e.getMessage() });
                return false;
            }
            finally {
                this.fNamespaceContext.popScope();
            }
        }
        else if (parse.equals(XIncludeHandler.XINCLUDE_PARSE_TEXT)) {
            final String encoding = attributes.getValue(XIncludeHandler.XINCLUDE_ATTR_ENCODING);
            includedSource.setEncoding(encoding);
            XIncludeTextReader textReader = null;
            try {
                if (!this.fIsXML11) {
                    if (this.fXInclude10TextReader == null) {
                        this.fXInclude10TextReader = new XIncludeTextReader(includedSource, this, this.fBufferSize);
                    }
                    else {
                        this.fXInclude10TextReader.setInputSource(includedSource);
                    }
                    textReader = this.fXInclude10TextReader;
                }
                else {
                    if (this.fXInclude11TextReader == null) {
                        this.fXInclude11TextReader = new XInclude11TextReader(includedSource, this, this.fBufferSize);
                    }
                    else {
                        this.fXInclude11TextReader.setInputSource(includedSource);
                    }
                    textReader = this.fXInclude11TextReader;
                }
                textReader.setErrorReporter(this.fErrorReporter);
                textReader.parse();
                if (textReader == null) {
                    return true;
                }
                try {
                    textReader.close();
                }
                catch (final IOException e3) {
                    this.reportResourceError("TextResourceError", new Object[] { href, e3.getMessage() });
                    return false;
                }
            }
            catch (final MalformedByteSequenceException ex2) {
                this.fErrorReporter.reportError(ex2.getDomain(), ex2.getKey(), ex2.getArguments(), (short)2);
                if (textReader == null) {
                    return true;
                }
                try {
                    textReader.close();
                }
                catch (final IOException e3) {
                    this.reportResourceError("TextResourceError", new Object[] { href, e3.getMessage() });
                    return false;
                }
            }
            catch (final CharConversionException e4) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2);
                if (textReader == null) {
                    return true;
                }
                try {
                    textReader.close();
                }
                catch (final IOException e3) {
                    this.reportResourceError("TextResourceError", new Object[] { href, e3.getMessage() });
                    return false;
                }
            }
            catch (final IOException e3) {
                this.reportResourceError("TextResourceError", new Object[] { href, e3.getMessage() });
                return false;
            }
            finally {
                if (textReader != null) {
                    try {
                        textReader.close();
                    }
                    catch (final IOException e5) {
                        this.reportResourceError("TextResourceError", new Object[] { href, e5.getMessage() });
                        return false;
                    }
                }
            }
        }
        else {
            this.reportFatalError("InvalidParseValue", new Object[] { parse });
        }
        return true;
    }
    
    protected boolean hasXIncludeNamespace(final QName element) {
        return element.uri == XIncludeHandler.XINCLUDE_NS_URI || this.fNamespaceContext.getURI(element.prefix) == XIncludeHandler.XINCLUDE_NS_URI;
    }
    
    protected boolean isIncludeElement(final QName element) {
        return element.localpart.equals(XIncludeHandler.XINCLUDE_INCLUDE) && this.hasXIncludeNamespace(element);
    }
    
    protected boolean isFallbackElement(final QName element) {
        return element.localpart.equals(XIncludeHandler.XINCLUDE_FALLBACK) && this.hasXIncludeNamespace(element);
    }
    
    protected boolean sameBaseURIAsIncludeParent() {
        final String parentBaseURI = this.getIncludeParentBaseURI();
        final String baseURI = this.fCurrentBaseURI.getExpandedSystemId();
        return parentBaseURI != null && parentBaseURI.equals(baseURI);
    }
    
    protected boolean sameLanguageAsIncludeParent() {
        final String parentLanguage = this.getIncludeParentLanguage();
        return parentLanguage != null && parentLanguage.equalsIgnoreCase(this.fCurrentLanguage);
    }
    
    protected boolean searchForRecursiveIncludes(final XMLLocator includedSource) {
        String includedSystemId = includedSource.getExpandedSystemId();
        if (includedSystemId == null) {
            try {
                includedSystemId = XMLEntityManager.expandSystemId(includedSource.getLiteralSystemId(), includedSource.getBaseSystemId(), false);
            }
            catch (final URI.MalformedURIException e) {
                this.reportFatalError("ExpandedSystemId");
            }
        }
        return includedSystemId.equals(this.fCurrentBaseURI.getExpandedSystemId()) || (this.fParentXIncludeHandler != null && this.fParentXIncludeHandler.searchForRecursiveIncludes(includedSource));
    }
    
    protected boolean isTopLevelIncludedItem() {
        return this.isTopLevelIncludedItemViaInclude() || this.isTopLevelIncludedItemViaFallback();
    }
    
    protected boolean isTopLevelIncludedItemViaInclude() {
        return this.fDepth == 1 && !this.isRootDocument();
    }
    
    protected boolean isTopLevelIncludedItemViaFallback() {
        return this.getSawFallback(this.fDepth - 1);
    }
    
    protected XMLAttributes processAttributes(XMLAttributes attributes) {
        if (this.isTopLevelIncludedItem()) {
            if (this.fFixupBaseURIs && !this.sameBaseURIAsIncludeParent()) {
                if (attributes == null) {
                    attributes = new XMLAttributesImpl();
                }
                String uri = null;
                try {
                    uri = this.getRelativeBaseURI();
                }
                catch (final URI.MalformedURIException e) {
                    uri = this.fCurrentBaseURI.getExpandedSystemId();
                }
                final int index = attributes.addAttribute(XIncludeHandler.XML_BASE_QNAME, XMLSymbols.fCDATASymbol, uri);
                attributes.setSpecified(index, true);
            }
            if (this.fFixupLanguage && !this.sameLanguageAsIncludeParent()) {
                if (attributes == null) {
                    attributes = new XMLAttributesImpl();
                }
                final int index2 = attributes.addAttribute(XIncludeHandler.XML_LANG_QNAME, XMLSymbols.fCDATASymbol, this.fCurrentLanguage);
                attributes.setSpecified(index2, true);
            }
            final Enumeration inscopeNS = this.fNamespaceContext.getAllPrefixes();
            while (inscopeNS.hasMoreElements()) {
                final String prefix = inscopeNS.nextElement();
                final String parentURI = this.fNamespaceContext.getURIFromIncludeParent(prefix);
                final String uri2 = this.fNamespaceContext.getURI(prefix);
                if (parentURI != uri2 && attributes != null) {
                    if (prefix == XMLSymbols.EMPTY_STRING) {
                        if (attributes.getValue(NamespaceContext.XMLNS_URI, XMLSymbols.PREFIX_XMLNS) != null) {
                            continue;
                        }
                        if (attributes == null) {
                            attributes = new XMLAttributesImpl();
                        }
                        final QName ns = (QName)XIncludeHandler.NEW_NS_ATTR_QNAME.clone();
                        ns.prefix = null;
                        ns.localpart = XMLSymbols.PREFIX_XMLNS;
                        ns.rawname = XMLSymbols.PREFIX_XMLNS;
                        final int index3 = attributes.addAttribute(ns, XMLSymbols.fCDATASymbol, (uri2 != null) ? uri2 : XMLSymbols.EMPTY_STRING);
                        attributes.setSpecified(index3, true);
                        this.fNamespaceContext.declarePrefix(prefix, uri2);
                    }
                    else {
                        if (attributes.getValue(NamespaceContext.XMLNS_URI, prefix) != null) {
                            continue;
                        }
                        if (attributes == null) {
                            attributes = new XMLAttributesImpl();
                        }
                        final QName ns = (QName)XIncludeHandler.NEW_NS_ATTR_QNAME.clone();
                        ns.localpart = prefix;
                        final StringBuilder sb = new StringBuilder();
                        final QName qName = ns;
                        qName.rawname = sb.append(qName.rawname).append(prefix).toString();
                        ns.rawname = ((this.fSymbolTable != null) ? this.fSymbolTable.addSymbol(ns.rawname) : ns.rawname.intern());
                        final int index3 = attributes.addAttribute(ns, XMLSymbols.fCDATASymbol, (uri2 != null) ? uri2 : XMLSymbols.EMPTY_STRING);
                        attributes.setSpecified(index3, true);
                        this.fNamespaceContext.declarePrefix(prefix, uri2);
                    }
                }
            }
        }
        if (attributes != null) {
            for (int length = attributes.getLength(), i = 0; i < length; ++i) {
                final String type = attributes.getType(i);
                final String value = attributes.getValue(i);
                if (type == XMLSymbols.fENTITYSymbol) {
                    this.checkUnparsedEntity(value);
                }
                if (type == XMLSymbols.fENTITIESSymbol) {
                    final StringTokenizer st = new StringTokenizer(value);
                    while (st.hasMoreTokens()) {
                        final String entName = st.nextToken();
                        this.checkUnparsedEntity(entName);
                    }
                }
                else if (type == XMLSymbols.fNOTATIONSymbol) {
                    this.checkNotation(value);
                }
            }
        }
        return attributes;
    }
    
    protected String getRelativeBaseURI() throws URI.MalformedURIException {
        final int includeParentDepth = this.getIncludeParentDepth();
        String relativeURI = this.getRelativeURI(includeParentDepth);
        if (this.isRootDocument()) {
            return relativeURI;
        }
        if (relativeURI.equals("")) {
            relativeURI = this.fCurrentBaseURI.getLiteralSystemId();
        }
        if (includeParentDepth != 0) {
            return relativeURI;
        }
        if (this.fParentRelativeURI == null) {
            this.fParentRelativeURI = this.fParentXIncludeHandler.getRelativeBaseURI();
        }
        if (this.fParentRelativeURI.equals("")) {
            return relativeURI;
        }
        final URI base = new URI(this.fParentRelativeURI, true);
        final URI uri = new URI(base, relativeURI);
        final String baseScheme = base.getScheme();
        final String literalScheme = uri.getScheme();
        if (!Objects.equals(baseScheme, literalScheme)) {
            return relativeURI;
        }
        final String baseAuthority = base.getAuthority();
        final String literalAuthority = uri.getAuthority();
        if (!Objects.equals(baseAuthority, literalAuthority)) {
            return uri.getSchemeSpecificPart();
        }
        final String literalPath = uri.getPath();
        final String literalQuery = uri.getQueryString();
        final String literalFragment = uri.getFragment();
        if (literalQuery != null || literalFragment != null) {
            final StringBuilder buffer = new StringBuilder();
            if (literalPath != null) {
                buffer.append(literalPath);
            }
            if (literalQuery != null) {
                buffer.append('?');
                buffer.append(literalQuery);
            }
            if (literalFragment != null) {
                buffer.append('#');
                buffer.append(literalFragment);
            }
            return buffer.toString();
        }
        return literalPath;
    }
    
    private String getIncludeParentBaseURI() {
        final int depth = this.getIncludeParentDepth();
        if (!this.isRootDocument() && depth == 0) {
            return this.fParentXIncludeHandler.getIncludeParentBaseURI();
        }
        return this.getBaseURI(depth);
    }
    
    private String getIncludeParentLanguage() {
        final int depth = this.getIncludeParentDepth();
        if (!this.isRootDocument() && depth == 0) {
            return this.fParentXIncludeHandler.getIncludeParentLanguage();
        }
        return this.getLanguage(depth);
    }
    
    private int getIncludeParentDepth() {
        for (int i = this.fDepth - 1; i >= 0; --i) {
            if (!this.getSawInclude(i) && !this.getSawFallback(i)) {
                return i;
            }
        }
        return 0;
    }
    
    private int getResultDepth() {
        return this.fResultDepth;
    }
    
    protected Augmentations modifyAugmentations(final Augmentations augs) {
        return this.modifyAugmentations(augs, false);
    }
    
    protected Augmentations modifyAugmentations(Augmentations augs, final boolean force) {
        if (force || this.isTopLevelIncludedItem()) {
            if (augs == null) {
                augs = new AugmentationsImpl();
            }
            augs.putItem(XIncludeHandler.XINCLUDE_INCLUDED, Boolean.TRUE);
        }
        return augs;
    }
    
    protected int getState(final int depth) {
        return this.fState[depth];
    }
    
    protected int getState() {
        return this.fState[this.fDepth];
    }
    
    protected void setState(final int state) {
        if (this.fDepth >= this.fState.length) {
            final int[] newarray = new int[this.fDepth * 2];
            System.arraycopy(this.fState, 0, newarray, 0, this.fState.length);
            this.fState = newarray;
        }
        this.fState[this.fDepth] = state;
    }
    
    protected void setSawFallback(final int depth, final boolean val) {
        if (depth >= this.fSawFallback.length) {
            final boolean[] newarray = new boolean[depth * 2];
            System.arraycopy(this.fSawFallback, 0, newarray, 0, this.fSawFallback.length);
            this.fSawFallback = newarray;
        }
        this.fSawFallback[depth] = val;
    }
    
    protected boolean getSawFallback(final int depth) {
        return depth < this.fSawFallback.length && this.fSawFallback[depth];
    }
    
    protected void setSawInclude(final int depth, final boolean val) {
        if (depth >= this.fSawInclude.length) {
            final boolean[] newarray = new boolean[depth * 2];
            System.arraycopy(this.fSawInclude, 0, newarray, 0, this.fSawInclude.length);
            this.fSawInclude = newarray;
        }
        this.fSawInclude[depth] = val;
    }
    
    protected boolean getSawInclude(final int depth) {
        return depth < this.fSawInclude.length && this.fSawInclude[depth];
    }
    
    protected void reportResourceError(final String key) {
        this.reportFatalError(key, null);
    }
    
    protected void reportResourceError(final String key, final Object[] args) {
        this.reportError(key, args, (short)0);
    }
    
    protected void reportFatalError(final String key) {
        this.reportFatalError(key, null);
    }
    
    protected void reportFatalError(final String key, final Object[] args) {
        this.reportError(key, args, (short)2);
    }
    
    private void reportError(final String key, final Object[] args, final short severity) {
        if (this.fErrorReporter != null) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/xinclude", key, args, severity);
        }
    }
    
    protected void setParent(final XIncludeHandler parent) {
        this.fParentXIncludeHandler = parent;
    }
    
    protected boolean isRootDocument() {
        return this.fParentXIncludeHandler == null;
    }
    
    protected void addUnparsedEntity(final String name, final XMLResourceIdentifier identifier, final String notation, final Augmentations augmentations) {
        final UnparsedEntity ent = new UnparsedEntity();
        ent.name = name;
        ent.systemId = identifier.getLiteralSystemId();
        ent.publicId = identifier.getPublicId();
        ent.baseURI = identifier.getBaseSystemId();
        ent.expandedSystemId = identifier.getExpandedSystemId();
        ent.notation = notation;
        ent.augmentations = augmentations;
        this.fUnparsedEntities.add(ent);
    }
    
    protected void addNotation(final String name, final XMLResourceIdentifier identifier, final Augmentations augmentations) {
        final Notation not = new Notation();
        not.name = name;
        not.systemId = identifier.getLiteralSystemId();
        not.publicId = identifier.getPublicId();
        not.baseURI = identifier.getBaseSystemId();
        not.expandedSystemId = identifier.getExpandedSystemId();
        not.augmentations = augmentations;
        this.fNotations.add(not);
    }
    
    protected void checkUnparsedEntity(final String entName) {
        UnparsedEntity ent = new UnparsedEntity();
        ent.name = entName;
        final int index = this.fUnparsedEntities.indexOf(ent);
        if (index != -1) {
            ent = this.fUnparsedEntities.get(index);
            this.checkNotation(ent.notation);
            this.checkAndSendUnparsedEntity(ent);
        }
    }
    
    protected void checkNotation(final String notName) {
        Notation not = new Notation();
        not.name = notName;
        final int index = this.fNotations.indexOf(not);
        if (index != -1) {
            not = this.fNotations.get(index);
            this.checkAndSendNotation(not);
        }
    }
    
    protected void checkAndSendUnparsedEntity(final UnparsedEntity ent) {
        if (this.isRootDocument()) {
            final int index = this.fUnparsedEntities.indexOf(ent);
            if (index == -1) {
                final XMLResourceIdentifier id = new XMLResourceIdentifierImpl(ent.publicId, ent.systemId, ent.baseURI, ent.expandedSystemId);
                this.addUnparsedEntity(ent.name, id, ent.notation, ent.augmentations);
                if (this.fSendUEAndNotationEvents && this.fDTDHandler != null) {
                    this.fDTDHandler.unparsedEntityDecl(ent.name, id, ent.notation, ent.augmentations);
                }
            }
            else {
                final UnparsedEntity localEntity = this.fUnparsedEntities.get(index);
                if (!ent.isDuplicate(localEntity)) {
                    this.reportFatalError("NonDuplicateUnparsedEntity", new Object[] { ent.name });
                }
            }
        }
        else {
            this.fParentXIncludeHandler.checkAndSendUnparsedEntity(ent);
        }
    }
    
    protected void checkAndSendNotation(final Notation not) {
        if (this.isRootDocument()) {
            final int index = this.fNotations.indexOf(not);
            if (index == -1) {
                final XMLResourceIdentifier id = new XMLResourceIdentifierImpl(not.publicId, not.systemId, not.baseURI, not.expandedSystemId);
                this.addNotation(not.name, id, not.augmentations);
                if (this.fSendUEAndNotationEvents && this.fDTDHandler != null) {
                    this.fDTDHandler.notationDecl(not.name, id, not.augmentations);
                }
            }
            else {
                final Notation localNotation = this.fNotations.get(index);
                if (!not.isDuplicate(localNotation)) {
                    this.reportFatalError("NonDuplicateNotation", new Object[] { not.name });
                }
            }
        }
        else {
            this.fParentXIncludeHandler.checkAndSendNotation(not);
        }
    }
    
    private void checkWhitespace(final XMLString value) {
        for (int end = value.offset + value.length, i = value.offset; i < end; ++i) {
            if (!XMLChar.isSpace(value.ch[i])) {
                this.reportFatalError("ContentIllegalAtTopLevel");
                return;
            }
        }
    }
    
    private void checkMultipleRootElements() {
        if (this.getRootElementProcessed()) {
            this.reportFatalError("MultipleRootElements");
        }
        this.setRootElementProcessed(true);
    }
    
    private void setRootElementProcessed(final boolean seenRoot) {
        if (this.isRootDocument()) {
            this.fSeenRootElement = seenRoot;
            return;
        }
        this.fParentXIncludeHandler.setRootElementProcessed(seenRoot);
    }
    
    private boolean getRootElementProcessed() {
        return this.isRootDocument() ? this.fSeenRootElement : this.fParentXIncludeHandler.getRootElementProcessed();
    }
    
    protected void copyFeatures(final XMLComponentManager from, final ParserConfigurationSettings to) {
        Enumeration features = Constants.getXercesFeatures();
        this.copyFeatures1(features, "http://apache.org/xml/features/", from, to);
        features = Constants.getSAXFeatures();
        this.copyFeatures1(features, "http://xml.org/sax/features/", from, to);
    }
    
    protected void copyFeatures(final XMLComponentManager from, final XMLParserConfiguration to) {
        Enumeration features = Constants.getXercesFeatures();
        this.copyFeatures1(features, "http://apache.org/xml/features/", from, to);
        features = Constants.getSAXFeatures();
        this.copyFeatures1(features, "http://xml.org/sax/features/", from, to);
    }
    
    private void copyFeatures1(final Enumeration features, final String featurePrefix, final XMLComponentManager from, final ParserConfigurationSettings to) {
        while (features.hasMoreElements()) {
            final String featureId = featurePrefix + features.nextElement();
            to.addRecognizedFeatures(new String[] { featureId });
            try {
                to.setFeature(featureId, from.getFeature(featureId));
            }
            catch (final XMLConfigurationException ex) {}
        }
    }
    
    private void copyFeatures1(final Enumeration features, final String featurePrefix, final XMLComponentManager from, final XMLParserConfiguration to) {
        while (features.hasMoreElements()) {
            final String featureId = featurePrefix + features.nextElement();
            final boolean value = from.getFeature(featureId);
            try {
                to.setFeature(featureId, value);
            }
            catch (final XMLConfigurationException ex) {}
        }
    }
    
    protected void saveBaseURI() {
        this.fBaseURIScope.push(this.fDepth);
        this.fBaseURI.push(this.fCurrentBaseURI.getBaseSystemId());
        this.fLiteralSystemID.push(this.fCurrentBaseURI.getLiteralSystemId());
        this.fExpandedSystemID.push(this.fCurrentBaseURI.getExpandedSystemId());
    }
    
    protected void restoreBaseURI() {
        this.fBaseURI.pop();
        this.fLiteralSystemID.pop();
        this.fExpandedSystemID.pop();
        this.fBaseURIScope.pop();
        this.fCurrentBaseURI.setBaseSystemId(this.fBaseURI.peek());
        this.fCurrentBaseURI.setLiteralSystemId(this.fLiteralSystemID.peek());
        this.fCurrentBaseURI.setExpandedSystemId(this.fExpandedSystemID.peek());
    }
    
    protected void saveLanguage(final String language) {
        this.fLanguageScope.push(this.fDepth);
        this.fLanguageStack.push(language);
    }
    
    public String restoreLanguage() {
        this.fLanguageStack.pop();
        this.fLanguageScope.pop();
        return this.fLanguageStack.peek();
    }
    
    public String getBaseURI(final int depth) {
        final int scope = this.scopeOfBaseURI(depth);
        return (String)this.fExpandedSystemID.elementAt(scope);
    }
    
    public String getLanguage(final int depth) {
        final int scope = this.scopeOfLanguage(depth);
        return (String)this.fLanguageStack.elementAt(scope);
    }
    
    public String getRelativeURI(final int depth) throws URI.MalformedURIException {
        final int start = this.scopeOfBaseURI(depth) + 1;
        if (start == this.fBaseURIScope.size()) {
            return "";
        }
        URI uri = new URI("file", (String)this.fLiteralSystemID.elementAt(start));
        for (int i = start + 1; i < this.fBaseURIScope.size(); ++i) {
            uri = new URI(uri, (String)this.fLiteralSystemID.elementAt(i));
        }
        return uri.getPath();
    }
    
    private int scopeOfBaseURI(final int depth) {
        for (int i = this.fBaseURIScope.size() - 1; i >= 0; --i) {
            if (this.fBaseURIScope.elementAt(i) <= depth) {
                return i;
            }
        }
        return -1;
    }
    
    private int scopeOfLanguage(final int depth) {
        for (int i = this.fLanguageScope.size() - 1; i >= 0; --i) {
            if (this.fLanguageScope.elementAt(i) <= depth) {
                return i;
            }
        }
        return -1;
    }
    
    protected void processXMLBaseAttributes(final XMLAttributes attributes) {
        final String baseURIValue = attributes.getValue(NamespaceContext.XML_URI, "base");
        if (baseURIValue != null) {
            try {
                final String expandedValue = XMLEntityManager.expandSystemId(baseURIValue, this.fCurrentBaseURI.getExpandedSystemId(), false);
                this.fCurrentBaseURI.setLiteralSystemId(baseURIValue);
                this.fCurrentBaseURI.setBaseSystemId(this.fCurrentBaseURI.getExpandedSystemId());
                this.fCurrentBaseURI.setExpandedSystemId(expandedValue);
                this.saveBaseURI();
            }
            catch (final URI.MalformedURIException ex) {}
        }
    }
    
    protected void processXMLLangAttributes(final XMLAttributes attributes) {
        final String language = attributes.getValue(NamespaceContext.XML_URI, "lang");
        if (language != null) {
            this.saveLanguage(this.fCurrentLanguage = language);
        }
    }
    
    private boolean isValidInHTTPHeader(final String value) {
        for (int i = value.length() - 1; i >= 0; --i) {
            final char ch = value.charAt(i);
            if (ch < ' ' || ch > '~') {
                return false;
            }
        }
        return true;
    }
    
    private XMLInputSource createInputSource(final String publicId, final String systemId, final String baseSystemId, final String accept, final String acceptLanguage) {
        final HTTPInputSource httpSource = new HTTPInputSource(publicId, systemId, baseSystemId);
        if (accept != null && accept.length() > 0) {
            httpSource.setHTTPRequestProperty("Accept", accept);
        }
        if (acceptLanguage != null && acceptLanguage.length() > 0) {
            httpSource.setHTTPRequestProperty("Accept-Language", acceptLanguage);
        }
        return httpSource;
    }
    
    private String escapeHref(final String href) {
        final int len = href.length();
        final StringBuilder buffer = new StringBuilder(len * 3);
        int i;
        for (i = 0; i < len; ++i) {
            final int ch = href.charAt(i);
            if (ch > 126) {
                break;
            }
            if (ch < 32) {
                return href;
            }
            if (XIncludeHandler.gNeedEscaping[ch]) {
                buffer.append('%');
                buffer.append(XIncludeHandler.gAfterEscaping1[ch]);
                buffer.append(XIncludeHandler.gAfterEscaping2[ch]);
            }
            else {
                buffer.append((char)ch);
            }
        }
        if (i < len) {
            for (int j = i; j < len; ++j) {
                final int ch = href.charAt(j);
                if ((ch < 32 || ch > 126) && (ch < 160 || ch > 55295) && (ch < 63744 || ch > 64975)) {
                    if (ch < 65008 || ch > 65519) {
                        if (XMLChar.isHighSurrogate(ch) && ++j < len) {
                            int ch2 = href.charAt(j);
                            if (XMLChar.isLowSurrogate(ch2)) {
                                ch2 = XMLChar.supplemental((char)ch, (char)ch2);
                                if (ch2 < 983040 && (ch2 & 0xFFFF) <= 65533) {
                                    continue;
                                }
                            }
                        }
                        return href;
                    }
                }
            }
            byte[] bytes = null;
            try {
                bytes = href.substring(i).getBytes("UTF-8");
            }
            catch (final UnsupportedEncodingException e) {
                return href;
            }
            for (final byte b : bytes) {
                if (b < 0) {
                    final int ch = b + 256;
                    buffer.append('%');
                    buffer.append(XIncludeHandler.gHexChs[ch >> 4]);
                    buffer.append(XIncludeHandler.gHexChs[ch & 0xF]);
                }
                else if (XIncludeHandler.gNeedEscaping[b]) {
                    buffer.append('%');
                    buffer.append(XIncludeHandler.gAfterEscaping1[b]);
                    buffer.append(XIncludeHandler.gAfterEscaping2[b]);
                }
                else {
                    buffer.append((char)b);
                }
            }
        }
        if (buffer.length() != len) {
            return buffer.toString();
        }
        return href;
    }
    
    static {
        XINCLUDE_NS_URI = "http://www.w3.org/2001/XInclude".intern();
        XINCLUDE_INCLUDE = "include".intern();
        XINCLUDE_FALLBACK = "fallback".intern();
        XINCLUDE_PARSE_XML = "xml".intern();
        XINCLUDE_PARSE_TEXT = "text".intern();
        XINCLUDE_ATTR_HREF = "href".intern();
        XINCLUDE_ATTR_PARSE = "parse".intern();
        XINCLUDE_ATTR_ENCODING = "encoding".intern();
        XINCLUDE_ATTR_ACCEPT = "accept".intern();
        XINCLUDE_ATTR_ACCEPT_LANGUAGE = "accept-language".intern();
        XINCLUDE_INCLUDED = "[included]".intern();
        XINCLUDE_BASE = "base".intern();
        XML_BASE_QNAME = new QName(XMLSymbols.PREFIX_XML, XIncludeHandler.XINCLUDE_BASE, (XMLSymbols.PREFIX_XML + ":" + XIncludeHandler.XINCLUDE_BASE).intern(), NamespaceContext.XML_URI);
        XINCLUDE_LANG = "lang".intern();
        XML_LANG_QNAME = new QName(XMLSymbols.PREFIX_XML, XIncludeHandler.XINCLUDE_LANG, (XMLSymbols.PREFIX_XML + ":" + XIncludeHandler.XINCLUDE_LANG).intern(), NamespaceContext.XML_URI);
        NEW_NS_ATTR_QNAME = new QName(XMLSymbols.PREFIX_XMLNS, "", XMLSymbols.PREFIX_XMLNS + ":", NamespaceContext.XMLNS_URI);
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/allow-dtd-events-after-endDTD", "http://apache.org/xml/features/xinclude/fixup-base-uris", "http://apache.org/xml/features/xinclude/fixup-language" };
        FEATURE_DEFAULTS = new Boolean[] { Boolean.TRUE, Boolean.TRUE, Boolean.TRUE };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/security-manager", "http://apache.org/xml/properties/input-buffer-size" };
        PROPERTY_DEFAULTS = new Object[] { null, null, null, new Integer(8192) };
        gNeedEscaping = new boolean[128];
        gAfterEscaping1 = new char[128];
        gAfterEscaping2 = new char[128];
        gHexChs = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        for (final char ch : new char[] { ' ', '<', '>', '\"', '{', '}', '|', '\\', '^', '`' }) {
            XIncludeHandler.gNeedEscaping[ch] = true;
            XIncludeHandler.gAfterEscaping1[ch] = XIncludeHandler.gHexChs[ch >> 4];
            XIncludeHandler.gAfterEscaping2[ch] = XIncludeHandler.gHexChs[ch & '\u000f'];
        }
    }
    
    protected static class Notation
    {
        public String name;
        public String systemId;
        public String baseURI;
        public String publicId;
        public String expandedSystemId;
        public Augmentations augmentations;
        
        @Override
        public boolean equals(final Object obj) {
            return obj == this || (obj instanceof Notation && Objects.equals(this.name, ((Notation)obj).name));
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.name);
        }
        
        public boolean isDuplicate(final Object obj) {
            if (obj != null && obj instanceof Notation) {
                final Notation other = (Notation)obj;
                return Objects.equals(this.name, other.name) && Objects.equals(this.publicId, other.publicId) && Objects.equals(this.expandedSystemId, other.expandedSystemId);
            }
            return false;
        }
    }
    
    protected static class UnparsedEntity
    {
        public String name;
        public String systemId;
        public String baseURI;
        public String publicId;
        public String expandedSystemId;
        public String notation;
        public Augmentations augmentations;
        
        @Override
        public boolean equals(final Object obj) {
            return obj == this || (obj instanceof UnparsedEntity && Objects.equals(this.name, ((UnparsedEntity)obj).name));
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.name);
        }
        
        public boolean isDuplicate(final Object obj) {
            if (obj != null && obj instanceof UnparsedEntity) {
                final UnparsedEntity other = (UnparsedEntity)obj;
                return Objects.equals(this.name, other.name) && Objects.equals(this.publicId, other.publicId) && Objects.equals(this.expandedSystemId, other.expandedSystemId) && Objects.equals(this.notation, other.notation);
            }
            return false;
        }
    }
}
