package org.apache.xerces.xinclude;

import java.io.UnsupportedEncodingException;
import org.apache.xerces.util.XMLChar;
import java.util.Enumeration;
import java.util.StringTokenizer;
import org.apache.xerces.util.XMLAttributesImpl;
import java.io.CharConversionException;
import org.apache.xerces.impl.io.MalformedByteSequenceException;
import org.apache.xerces.xpointer.XPointerHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import java.io.IOException;
import org.apache.xerces.util.HTTPInputSource;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.util.URI;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.util.AugmentationsImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.util.XMLResourceIdentifierImpl;
import java.util.ArrayList;
import org.apache.xerces.util.ParserConfigurationSettings;
import java.util.Stack;
import org.apache.xerces.util.IntStack;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLLocatorWrapper;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xpointer.XPointerProcessor;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.parser.XMLDTDFilter;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLComponent;

public class XIncludeHandler implements XMLComponent, XMLDocumentFilter, XMLDTDFilter
{
    public static final String XINCLUDE_DEFAULT_CONFIGURATION = "org.apache.xerces.parsers.XIncludeParserConfiguration";
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
    private static final String XINCLUDE_BASE;
    private static final QName XML_BASE_QNAME;
    private static final String XINCLUDE_LANG;
    private static final QName XML_LANG_QNAME;
    private static final QName NEW_NS_ATTR_QNAME;
    private static final int STATE_NORMAL_PROCESSING = 1;
    private static final int STATE_IGNORE = 2;
    private static final int STATE_EXPECT_FALLBACK = 3;
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
    protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
    protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
    protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
    protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
    protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    protected static final String BUFFER_SIZE = "http://apache.org/xml/properties/input-buffer-size";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
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
    protected XMLLocatorWrapper fXIncludeLocator;
    protected XIncludeMessageFormatter fXIncludeMessageFormatter;
    protected XIncludeNamespaceSupport fNamespaceContext;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityResolver fEntityResolver;
    protected SecurityManager fSecurityManager;
    protected XIncludeTextReader fXInclude10TextReader;
    protected XIncludeTextReader fXInclude11TextReader;
    protected final XMLResourceIdentifier fCurrentBaseURI;
    protected final IntStack fBaseURIScope;
    protected final Stack fBaseURI;
    protected final Stack fLiteralSystemID;
    protected final Stack fExpandedSystemID;
    protected final IntStack fLanguageScope;
    protected final Stack fLanguageStack;
    protected String fCurrentLanguage;
    protected String fHrefFromParent;
    protected ParserConfigurationSettings fSettings;
    private int fDepth;
    private int fResultDepth;
    private static final int INITIAL_SIZE = 8;
    private boolean[] fSawInclude;
    private boolean[] fSawFallback;
    private int[] fState;
    private final ArrayList fNotations;
    private final ArrayList fUnparsedEntities;
    private boolean fFixupBaseURIs;
    private boolean fFixupLanguage;
    private boolean fSendUEAndNotationEvents;
    private boolean fIsXML11;
    private boolean fInDTD;
    boolean fHasIncludeReportedContent;
    private boolean fSeenRootElement;
    private boolean fNeedCopyFeatures;
    private static final boolean[] gNeedEscaping;
    private static final char[] gAfterEscaping1;
    private static final char[] gAfterEscaping2;
    private static final char[] gHexChs;
    
    public XIncludeHandler() {
        this.fBufferSize = 2048;
        this.fXPtrProcessor = null;
        this.fXIncludeLocator = new XMLLocatorWrapper();
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
    
    public void reset(final XMLComponentManager xmlComponentManager) throws XNIException {
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
        for (int j = 0; j < this.fSawFallback.length; ++j) {
            this.fSawFallback[j] = false;
        }
        for (int k = 0; k < this.fSawInclude.length; ++k) {
            this.fSawInclude[k] = false;
        }
        try {
            if (!xmlComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings")) {
                return;
            }
        }
        catch (final XMLConfigurationException ex) {}
        this.fNeedCopyFeatures = true;
        try {
            this.fSendUEAndNotationEvents = xmlComponentManager.getFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD");
            if (this.fChildConfig != null) {
                this.fChildConfig.setFeature("http://xml.org/sax/features/allow-dtd-events-after-endDTD", this.fSendUEAndNotationEvents);
            }
        }
        catch (final XMLConfigurationException ex2) {}
        try {
            this.fFixupBaseURIs = xmlComponentManager.getFeature("http://apache.org/xml/features/xinclude/fixup-base-uris");
            if (this.fChildConfig != null) {
                this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", this.fFixupBaseURIs);
            }
        }
        catch (final XMLConfigurationException ex3) {
            this.fFixupBaseURIs = true;
        }
        try {
            this.fFixupLanguage = xmlComponentManager.getFeature("http://apache.org/xml/features/xinclude/fixup-language");
            if (this.fChildConfig != null) {
                this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-language", this.fFixupLanguage);
            }
        }
        catch (final XMLConfigurationException ex4) {
            this.fFixupLanguage = true;
        }
        try {
            final SymbolTable fSymbolTable = (SymbolTable)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
            if (fSymbolTable != null) {
                this.fSymbolTable = fSymbolTable;
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/symbol-table", fSymbolTable);
                }
            }
        }
        catch (final XMLConfigurationException ex5) {
            this.fSymbolTable = null;
        }
        try {
            final XMLErrorReporter errorReporter = (XMLErrorReporter)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
            if (errorReporter != null) {
                this.setErrorReporter(errorReporter);
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/error-reporter", errorReporter);
                }
            }
        }
        catch (final XMLConfigurationException ex6) {
            this.fErrorReporter = null;
        }
        try {
            final XMLEntityResolver fEntityResolver = (XMLEntityResolver)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
            if (fEntityResolver != null) {
                this.fEntityResolver = fEntityResolver;
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/entity-resolver", fEntityResolver);
                }
            }
        }
        catch (final XMLConfigurationException ex7) {
            this.fEntityResolver = null;
        }
        try {
            final SecurityManager fSecurityManager = (SecurityManager)xmlComponentManager.getProperty("http://apache.org/xml/properties/security-manager");
            if (fSecurityManager != null) {
                this.fSecurityManager = fSecurityManager;
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty("http://apache.org/xml/properties/security-manager", fSecurityManager);
                }
            }
        }
        catch (final XMLConfigurationException ex8) {
            this.fSecurityManager = null;
        }
        try {
            final Integer n = (Integer)xmlComponentManager.getProperty("http://apache.org/xml/properties/input-buffer-size");
            if (n != null && n > 0) {
                this.fBufferSize = n;
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty("http://apache.org/xml/properties/input-buffer-size", n);
                }
            }
            else {
                this.fBufferSize = (int)this.getPropertyDefault("http://apache.org/xml/properties/input-buffer-size");
            }
        }
        catch (final XMLConfigurationException ex9) {
            this.fBufferSize = (int)this.getPropertyDefault("http://apache.org/xml/properties/input-buffer-size");
        }
        if (this.fXInclude10TextReader != null) {
            this.fXInclude10TextReader.setBufferSize(this.fBufferSize);
        }
        if (this.fXInclude11TextReader != null) {
            this.fXInclude11TextReader.setBufferSize(this.fBufferSize);
        }
        this.copyFeatures(xmlComponentManager, this.fSettings = new ParserConfigurationSettings());
        try {
            if (xmlComponentManager.getFeature("http://apache.org/xml/features/validation/schema")) {
                this.fSettings.setFeature("http://apache.org/xml/features/validation/schema", false);
                if (Constants.NS_XMLSCHEMA.equals(xmlComponentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage"))) {
                    this.fSettings.setFeature("http://xml.org/sax/features/validation", false);
                }
                else if (xmlComponentManager.getFeature("http://xml.org/sax/features/validation")) {
                    this.fSettings.setFeature("http://apache.org/xml/features/validation/dynamic", true);
                }
            }
        }
        catch (final XMLConfigurationException ex10) {}
    }
    
    public String[] getRecognizedFeatures() {
        return XIncludeHandler.RECOGNIZED_FEATURES.clone();
    }
    
    public void setFeature(final String s, final boolean fSendUEAndNotationEvents) throws XMLConfigurationException {
        if (s.equals("http://xml.org/sax/features/allow-dtd-events-after-endDTD")) {
            this.fSendUEAndNotationEvents = fSendUEAndNotationEvents;
        }
        if (this.fSettings != null) {
            this.fNeedCopyFeatures = true;
            this.fSettings.setFeature(s, fSendUEAndNotationEvents);
        }
    }
    
    public String[] getRecognizedProperties() {
        return XIncludeHandler.RECOGNIZED_PROPERTIES.clone();
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        if (s.equals("http://apache.org/xml/properties/internal/symbol-table")) {
            this.fSymbolTable = (SymbolTable)o;
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(s, o);
            }
            return;
        }
        if (s.equals("http://apache.org/xml/properties/internal/error-reporter")) {
            this.setErrorReporter((XMLErrorReporter)o);
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(s, o);
            }
            return;
        }
        if (s.equals("http://apache.org/xml/properties/internal/entity-resolver")) {
            this.fEntityResolver = (XMLEntityResolver)o;
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(s, o);
            }
            return;
        }
        if (s.equals("http://apache.org/xml/properties/security-manager")) {
            this.fSecurityManager = (SecurityManager)o;
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(s, o);
            }
            return;
        }
        if (s.equals("http://apache.org/xml/properties/input-buffer-size")) {
            final Integer n = (Integer)o;
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(s, o);
            }
            if (n != null && n > 0) {
                this.fBufferSize = n;
                if (this.fXInclude10TextReader != null) {
                    this.fXInclude10TextReader.setBufferSize(this.fBufferSize);
                }
                if (this.fXInclude11TextReader != null) {
                    this.fXInclude11TextReader.setBufferSize(this.fBufferSize);
                }
            }
        }
    }
    
    public Boolean getFeatureDefault(final String s) {
        for (int i = 0; i < XIncludeHandler.RECOGNIZED_FEATURES.length; ++i) {
            if (XIncludeHandler.RECOGNIZED_FEATURES[i].equals(s)) {
                return XIncludeHandler.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public Object getPropertyDefault(final String s) {
        for (int i = 0; i < XIncludeHandler.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XIncludeHandler.RECOGNIZED_PROPERTIES[i].equals(s)) {
                return XIncludeHandler.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public void setDocumentHandler(final XMLDocumentHandler documentHandler) {
        if (this.fDocumentHandler != documentHandler) {
            this.fDocumentHandler = documentHandler;
            if (this.fXIncludeChildConfig != null) {
                this.fXIncludeChildConfig.setDocumentHandler(documentHandler);
            }
            if (this.fXPointerChildConfig != null) {
                this.fXPointerChildConfig.setDocumentHandler(documentHandler);
            }
        }
    }
    
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    public void startDocument(final XMLLocator xmlLocator, final String s, final NamespaceContext namespaceContext, Augmentations augmentations) throws XNIException {
        this.fErrorReporter.setDocumentLocator(xmlLocator);
        if (!(namespaceContext instanceof XIncludeNamespaceSupport)) {
            this.reportFatalError("IncompatibleNamespaceContext");
        }
        this.fNamespaceContext = (XIncludeNamespaceSupport)namespaceContext;
        this.fDocLocation = xmlLocator;
        this.fXIncludeLocator.setLocator(this.fDocLocation);
        this.setupCurrentBaseURI(xmlLocator);
        this.saveBaseURI();
        if (augmentations == null) {
            augmentations = new AugmentationsImpl();
        }
        augmentations.putItem("currentBaseURI", this.fCurrentBaseURI);
        if (!this.isRootDocument()) {
            this.fParentXIncludeHandler.fHasIncludeReportedContent = true;
            if (this.fParentXIncludeHandler.searchForRecursiveIncludes(this.fCurrentBaseURI.getExpandedSystemId())) {
                this.reportFatalError("RecursiveInclude", new Object[] { this.fCurrentBaseURI.getExpandedSystemId() });
            }
        }
        this.saveLanguage(this.fCurrentLanguage = XMLSymbols.EMPTY_STRING);
        if (this.isRootDocument() && this.fDocumentHandler != null) {
            this.fDocumentHandler.startDocument(this.fXIncludeLocator, s, namespaceContext, augmentations);
        }
    }
    
    public void xmlDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
        this.fIsXML11 = "1.1".equals(s);
        if (this.isRootDocument() && this.fDocumentHandler != null) {
            this.fDocumentHandler.xmlDecl(s, s2, s3, augmentations);
        }
    }
    
    public void doctypeDecl(final String s, final String s2, final String s3, final Augmentations augmentations) throws XNIException {
        if (this.isRootDocument() && this.fDocumentHandler != null) {
            this.fDocumentHandler.doctypeDecl(s, s2, s3, augmentations);
        }
    }
    
    public void comment(final XMLString xmlString, Augmentations modifyAugmentations) throws XNIException {
        if (!this.fInDTD) {
            if (this.fDocumentHandler != null && this.getState() == 1) {
                ++this.fDepth;
                modifyAugmentations = this.modifyAugmentations(modifyAugmentations);
                this.fDocumentHandler.comment(xmlString, modifyAugmentations);
                --this.fDepth;
            }
        }
        else if (this.fDTDHandler != null) {
            this.fDTDHandler.comment(xmlString, modifyAugmentations);
        }
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, Augmentations modifyAugmentations) throws XNIException {
        if (!this.fInDTD) {
            if (this.fDocumentHandler != null && this.getState() == 1) {
                ++this.fDepth;
                modifyAugmentations = this.modifyAugmentations(modifyAugmentations);
                this.fDocumentHandler.processingInstruction(s, xmlString, modifyAugmentations);
                --this.fDepth;
            }
        }
        else if (this.fDTDHandler != null) {
            this.fDTDHandler.processingInstruction(s, xmlString, modifyAugmentations);
        }
    }
    
    public void startElement(final QName qName, XMLAttributes xmlAttributes, Augmentations augmentations) throws XNIException {
        ++this.fDepth;
        final int state = this.getState(this.fDepth - 1);
        if (state == 3 && this.getState(this.fDepth - 2) == 3) {
            this.setState(2);
        }
        else {
            this.setState(state);
        }
        this.processXMLBaseAttributes(xmlAttributes);
        if (this.fFixupLanguage) {
            this.processXMLLangAttributes(xmlAttributes);
        }
        if (this.isIncludeElement(qName)) {
            if (this.handleIncludeElement(xmlAttributes)) {
                this.setState(2);
            }
            else {
                this.setState(3);
            }
        }
        else if (this.isFallbackElement(qName)) {
            this.handleFallbackElement();
        }
        else if (this.hasXIncludeNamespace(qName)) {
            if (this.getSawInclude(this.fDepth - 1)) {
                this.reportFatalError("IncludeChild", new Object[] { qName.rawname });
            }
            if (this.getSawFallback(this.fDepth - 1)) {
                this.reportFatalError("FallbackChild", new Object[] { qName.rawname });
            }
            if (this.getState() == 1) {
                if (this.fResultDepth++ == 0) {
                    this.checkMultipleRootElements();
                }
                if (this.fDocumentHandler != null) {
                    augmentations = this.modifyAugmentations(augmentations);
                    xmlAttributes = this.processAttributes(xmlAttributes);
                    this.fDocumentHandler.startElement(qName, xmlAttributes, augmentations);
                }
            }
        }
        else if (this.getState() == 1) {
            if (this.fResultDepth++ == 0) {
                this.checkMultipleRootElements();
            }
            if (this.fDocumentHandler != null) {
                augmentations = this.modifyAugmentations(augmentations);
                xmlAttributes = this.processAttributes(xmlAttributes);
                this.fDocumentHandler.startElement(qName, xmlAttributes, augmentations);
            }
        }
    }
    
    public void emptyElement(final QName qName, XMLAttributes xmlAttributes, Augmentations augmentations) throws XNIException {
        ++this.fDepth;
        final int state = this.getState(this.fDepth - 1);
        if (state == 3 && this.getState(this.fDepth - 2) == 3) {
            this.setState(2);
        }
        else {
            this.setState(state);
        }
        this.processXMLBaseAttributes(xmlAttributes);
        if (this.fFixupLanguage) {
            this.processXMLLangAttributes(xmlAttributes);
        }
        if (this.isIncludeElement(qName)) {
            if (this.handleIncludeElement(xmlAttributes)) {
                this.setState(2);
            }
            else {
                this.reportFatalError("NoFallback");
            }
        }
        else if (this.isFallbackElement(qName)) {
            this.handleFallbackElement();
        }
        else if (this.hasXIncludeNamespace(qName)) {
            if (this.getSawInclude(this.fDepth - 1)) {
                this.reportFatalError("IncludeChild", new Object[] { qName.rawname });
            }
            if (this.getSawFallback(this.fDepth - 1)) {
                this.reportFatalError("FallbackChild", new Object[] { qName.rawname });
            }
            if (this.getState() == 1) {
                if (this.fResultDepth == 0) {
                    this.checkMultipleRootElements();
                }
                if (this.fDocumentHandler != null) {
                    augmentations = this.modifyAugmentations(augmentations);
                    xmlAttributes = this.processAttributes(xmlAttributes);
                    this.fDocumentHandler.emptyElement(qName, xmlAttributes, augmentations);
                }
            }
        }
        else if (this.getState() == 1) {
            if (this.fResultDepth == 0) {
                this.checkMultipleRootElements();
            }
            if (this.fDocumentHandler != null) {
                augmentations = this.modifyAugmentations(augmentations);
                xmlAttributes = this.processAttributes(xmlAttributes);
                this.fDocumentHandler.emptyElement(qName, xmlAttributes, augmentations);
            }
        }
        this.setSawFallback(this.fDepth + 1, false);
        this.setSawInclude(this.fDepth, false);
        if (this.fBaseURIScope.size() > 0 && this.fDepth == this.fBaseURIScope.peek()) {
            this.restoreBaseURI();
        }
        --this.fDepth;
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
        if (this.isIncludeElement(qName) && this.getState() == 3 && !this.getSawFallback(this.fDepth + 1)) {
            this.reportFatalError("NoFallback");
        }
        if (this.isFallbackElement(qName)) {
            if (this.getState() == 1) {
                this.setState(2);
            }
        }
        else if (this.getState() == 1) {
            --this.fResultDepth;
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.endElement(qName, augmentations);
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
    
    public void startGeneralEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.getState() == 1) {
            if (this.fResultDepth == 0) {
                if (augmentations != null && Boolean.TRUE.equals(augmentations.getItem("ENTITY_SKIPPED"))) {
                    this.reportFatalError("UnexpandedEntityReferenceIllegal");
                }
            }
            else if (this.fDocumentHandler != null) {
                this.fDocumentHandler.startGeneralEntity(s, xmlResourceIdentifier, s2, augmentations);
            }
        }
    }
    
    public void textDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1) {
            this.fDocumentHandler.textDecl(s, s2, augmentations);
        }
    }
    
    public void endGeneralEntity(final String s, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
            this.fDocumentHandler.endGeneralEntity(s, augmentations);
        }
    }
    
    public void characters(final XMLString xmlString, Augmentations modifyAugmentations) throws XNIException {
        if (this.getState() == 1) {
            if (this.fResultDepth == 0) {
                this.checkWhitespace(xmlString);
            }
            else if (this.fDocumentHandler != null) {
                ++this.fDepth;
                modifyAugmentations = this.modifyAugmentations(modifyAugmentations);
                this.fDocumentHandler.characters(xmlString, modifyAugmentations);
                --this.fDepth;
            }
        }
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
            this.fDocumentHandler.ignorableWhitespace(xmlString, augmentations);
        }
    }
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
            this.fDocumentHandler.startCDATA(augmentations);
        }
    }
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
            this.fDocumentHandler.endCDATA(augmentations);
        }
    }
    
    public void endDocument(final Augmentations augmentations) throws XNIException {
        if (this.isRootDocument()) {
            if (!this.fSeenRootElement) {
                this.reportFatalError("RootElementRequired");
            }
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.endDocument(augmentations);
            }
        }
    }
    
    public void setDocumentSource(final XMLDocumentSource fDocumentSource) {
        this.fDocumentSource = fDocumentSource;
    }
    
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }
    
    public void attributeDecl(final String s, final String s2, final String s3, final String[] array, final String s4, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.attributeDecl(s, s2, s3, array, s4, xmlString, xmlString2, augmentations);
        }
    }
    
    public void elementDecl(final String s, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.elementDecl(s, s2, augmentations);
        }
    }
    
    public void endAttlist(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endAttlist(augmentations);
        }
    }
    
    public void endConditional(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endConditional(augmentations);
        }
    }
    
    public void endDTD(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endDTD(augmentations);
        }
        this.fInDTD = false;
    }
    
    public void endExternalSubset(final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endExternalSubset(augmentations);
        }
    }
    
    public void endParameterEntity(final String s, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endParameterEntity(s, augmentations);
        }
    }
    
    public void externalEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.externalEntityDecl(s, xmlResourceIdentifier, augmentations);
        }
    }
    
    public XMLDTDSource getDTDSource() {
        return this.fDTDSource;
    }
    
    public void ignoredCharacters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.ignoredCharacters(xmlString, augmentations);
        }
    }
    
    public void internalEntityDecl(final String s, final XMLString xmlString, final XMLString xmlString2, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.internalEntityDecl(s, xmlString, xmlString2, augmentations);
        }
    }
    
    public void notationDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        this.addNotation(s, xmlResourceIdentifier, augmentations);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.notationDecl(s, xmlResourceIdentifier, augmentations);
        }
    }
    
    public void setDTDSource(final XMLDTDSource fdtdSource) {
        this.fDTDSource = fdtdSource;
    }
    
    public void startAttlist(final String s, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startAttlist(s, augmentations);
        }
    }
    
    public void startConditional(final short n, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startConditional(n, augmentations);
        }
    }
    
    public void startDTD(final XMLLocator xmlLocator, final Augmentations augmentations) throws XNIException {
        this.fInDTD = true;
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startDTD(xmlLocator, augmentations);
        }
    }
    
    public void startExternalSubset(final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startExternalSubset(xmlResourceIdentifier, augmentations);
        }
    }
    
    public void startParameterEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startParameterEntity(s, xmlResourceIdentifier, s2, augmentations);
        }
    }
    
    public void unparsedEntityDecl(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        this.addUnparsedEntity(s, xmlResourceIdentifier, s2, augmentations);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.unparsedEntityDecl(s, xmlResourceIdentifier, s2, augmentations);
        }
    }
    
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }
    
    public void setDTDHandler(final XMLDTDHandler fdtdHandler) {
        this.fDTDHandler = fdtdHandler;
    }
    
    private void setErrorReporter(final XMLErrorReporter fErrorReporter) {
        this.fErrorReporter = fErrorReporter;
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
    
    protected boolean handleIncludeElement(final XMLAttributes xmlAttributes) throws XNIException {
        if (this.getSawInclude(this.fDepth - 1)) {
            this.reportFatalError("IncludeChild", new Object[] { XIncludeHandler.XINCLUDE_INCLUDE });
        }
        if (this.getState() == 2) {
            return true;
        }
        this.setSawInclude(this.fDepth, true);
        this.fNamespaceContext.setContextInvalid();
        String s = xmlAttributes.getValue(XIncludeHandler.XINCLUDE_ATTR_HREF);
        String s2 = xmlAttributes.getValue(XIncludeHandler.XINCLUDE_ATTR_PARSE);
        final String value = xmlAttributes.getValue("xpointer");
        String value2 = xmlAttributes.getValue(XIncludeHandler.XINCLUDE_ATTR_ACCEPT);
        String value3 = xmlAttributes.getValue(XIncludeHandler.XINCLUDE_ATTR_ACCEPT_LANGUAGE);
        if (s2 == null) {
            s2 = XIncludeHandler.XINCLUDE_PARSE_XML;
        }
        if (s == null) {
            s = XMLSymbols.EMPTY_STRING;
        }
        if (s.length() == 0 && XIncludeHandler.XINCLUDE_PARSE_XML.equals(s2)) {
            if (value != null) {
                this.reportResourceError("XMLResourceError", new Object[] { s, this.fXIncludeMessageFormatter.formatMessage((this.fErrorReporter != null) ? this.fErrorReporter.getLocale() : null, "XPointerStreamability", null) });
                return false;
            }
            this.reportFatalError("XpointerMissing");
        }
        try {
            if (new URI(s, true).getFragment() != null) {
                this.reportFatalError("HrefFragmentIdentifierIllegal", new Object[] { s });
            }
        }
        catch (final URI.MalformedURIException ex) {
            final String escapeHref = this.escapeHref(s);
            if (s != escapeHref) {
                s = escapeHref;
                try {
                    if (new URI(s, true).getFragment() != null) {
                        this.reportFatalError("HrefFragmentIdentifierIllegal", new Object[] { s });
                    }
                }
                catch (final URI.MalformedURIException ex2) {
                    this.reportFatalError("HrefSyntacticallyInvalid", new Object[] { s });
                }
            }
            else {
                this.reportFatalError("HrefSyntacticallyInvalid", new Object[] { s });
            }
        }
        if (value2 != null && !this.isValidInHTTPHeader(value2)) {
            this.reportFatalError("AcceptMalformed", null);
            value2 = null;
        }
        if (value3 != null && !this.isValidInHTTPHeader(value3)) {
            this.reportFatalError("AcceptLanguageMalformed", null);
            value3 = null;
        }
        XMLInputSource xmlInputSource = null;
        if (this.fEntityResolver != null) {
            try {
                xmlInputSource = this.fEntityResolver.resolveEntity(new XMLResourceIdentifierImpl(null, s, this.fCurrentBaseURI.getExpandedSystemId(), XMLEntityManager.expandSystemId(s, this.fCurrentBaseURI.getExpandedSystemId(), false)));
                if (xmlInputSource != null && !(xmlInputSource instanceof HTTPInputSource) && (value2 != null || value3 != null) && xmlInputSource.getCharacterStream() == null && xmlInputSource.getByteStream() == null) {
                    xmlInputSource = this.createInputSource(xmlInputSource.getPublicId(), xmlInputSource.getSystemId(), xmlInputSource.getBaseSystemId(), value2, value3);
                }
            }
            catch (final IOException ex3) {
                this.reportResourceError("XMLResourceError", new Object[] { s, ex3.getMessage() }, ex3);
                return false;
            }
        }
        if (xmlInputSource == null) {
            if (value2 != null || value3 != null) {
                xmlInputSource = this.createInputSource(null, s, this.fCurrentBaseURI.getExpandedSystemId(), value2, value3);
            }
            else {
                xmlInputSource = new XMLInputSource(null, s, this.fCurrentBaseURI.getExpandedSystemId());
            }
        }
        if (s2.equals(XIncludeHandler.XINCLUDE_PARSE_XML)) {
            if ((value != null && this.fXPointerChildConfig == null) || (value == null && this.fXIncludeChildConfig == null)) {
                String s3 = "org.apache.xerces.parsers.XIncludeParserConfiguration";
                if (value != null) {
                    s3 = "org.apache.xerces.parsers.XPointerParserConfiguration";
                }
                this.fChildConfig = (XMLParserConfiguration)ObjectFactory.newInstance(s3, ObjectFactory.findClassLoader(), true);
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
                this.fChildConfig.setProperty("http://apache.org/xml/properties/input-buffer-size", new Integer(this.fBufferSize));
                this.fNeedCopyFeatures = true;
                this.fChildConfig.setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNamespaceContext);
                this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", this.fFixupBaseURIs);
                this.fChildConfig.setFeature("http://apache.org/xml/features/xinclude/fixup-language", this.fFixupLanguage);
                if (value != null) {
                    final XPointerHandler fxPtrProcessor = (XPointerHandler)this.fChildConfig.getProperty("http://apache.org/xml/properties/internal/xpointer-handler");
                    this.fXPtrProcessor = fxPtrProcessor;
                    ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/properties/internal/namespace-context", this.fNamespaceContext);
                    ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/features/xinclude/fixup-base-uris", this.fFixupBaseURIs ? Boolean.TRUE : Boolean.FALSE);
                    ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/features/xinclude/fixup-language", this.fFixupLanguage ? Boolean.TRUE : Boolean.FALSE);
                    if (this.fErrorReporter != null) {
                        ((XPointerHandler)this.fXPtrProcessor).setProperty("http://apache.org/xml/properties/internal/error-reporter", this.fErrorReporter);
                    }
                    fxPtrProcessor.setParent(this);
                    fxPtrProcessor.setHref(s);
                    fxPtrProcessor.setXIncludeLocator(this.fXIncludeLocator);
                    fxPtrProcessor.setDocumentHandler(this.getDocumentHandler());
                    this.fXPointerChildConfig = this.fChildConfig;
                }
                else {
                    final XIncludeHandler xIncludeHandler = (XIncludeHandler)this.fChildConfig.getProperty("http://apache.org/xml/properties/internal/xinclude-handler");
                    xIncludeHandler.setParent(this);
                    xIncludeHandler.setHref(s);
                    xIncludeHandler.setXIncludeLocator(this.fXIncludeLocator);
                    xIncludeHandler.setDocumentHandler(this.getDocumentHandler());
                    this.fXIncludeChildConfig = this.fChildConfig;
                }
            }
            Label_1088: {
                if (value != null) {
                    this.fChildConfig = this.fXPointerChildConfig;
                    try {
                        this.fXPtrProcessor.parseXPointer(value);
                        break Label_1088;
                    }
                    catch (final XNIException ex4) {
                        this.reportResourceError("XMLResourceError", new Object[] { s, ex4.getMessage() });
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
                this.fHasIncludeReportedContent = false;
                this.fNamespaceContext.pushScope();
                this.fChildConfig.parse(xmlInputSource);
                this.fXIncludeLocator.setLocator(this.fDocLocation);
                if (this.fErrorReporter != null) {
                    this.fErrorReporter.setDocumentLocator(this.fDocLocation);
                }
                if (value != null && !this.fXPtrProcessor.isXPointerResolved()) {
                    this.reportResourceError("XMLResourceError", new Object[] { s, this.fXIncludeMessageFormatter.formatMessage((this.fErrorReporter != null) ? this.fErrorReporter.getLocale() : null, "XPointerResolutionUnsuccessful", null) });
                    return false;
                }
                return true;
            }
            catch (final XNIException ex5) {
                this.fXIncludeLocator.setLocator(this.fDocLocation);
                if (this.fErrorReporter != null) {
                    this.fErrorReporter.setDocumentLocator(this.fDocLocation);
                }
                this.reportFatalError("XMLParseError", new Object[] { s });
            }
            catch (final IOException ex6) {
                this.fXIncludeLocator.setLocator(this.fDocLocation);
                if (this.fErrorReporter != null) {
                    this.fErrorReporter.setDocumentLocator(this.fDocLocation);
                }
                if (this.fHasIncludeReportedContent) {
                    throw new XNIException(ex6);
                }
                this.reportResourceError("XMLResourceError", new Object[] { s, ex6.getMessage() }, ex6);
                return false;
            }
            finally {
                this.fNamespaceContext.popScope();
            }
        }
        if (s2.equals(XIncludeHandler.XINCLUDE_PARSE_TEXT)) {
            xmlInputSource.setEncoding(xmlAttributes.getValue(XIncludeHandler.XINCLUDE_ATTR_ENCODING));
            XIncludeTextReader xIncludeTextReader = null;
            try {
                this.fHasIncludeReportedContent = false;
                if (!this.fIsXML11) {
                    if (this.fXInclude10TextReader == null) {
                        this.fXInclude10TextReader = new XIncludeTextReader(xmlInputSource, this, this.fBufferSize);
                    }
                    else {
                        this.fXInclude10TextReader.setInputSource(xmlInputSource);
                    }
                    xIncludeTextReader = this.fXInclude10TextReader;
                }
                else {
                    if (this.fXInclude11TextReader == null) {
                        this.fXInclude11TextReader = new XInclude11TextReader(xmlInputSource, this, this.fBufferSize);
                    }
                    else {
                        this.fXInclude11TextReader.setInputSource(xmlInputSource);
                    }
                    xIncludeTextReader = this.fXInclude11TextReader;
                }
                xIncludeTextReader.setErrorReporter(this.fErrorReporter);
                xIncludeTextReader.parse();
            }
            catch (final MalformedByteSequenceException ex7) {
                this.fErrorReporter.reportError(ex7.getDomain(), ex7.getKey(), ex7.getArguments(), (short)2, ex7);
            }
            catch (final CharConversionException ex8) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2, ex8);
            }
            catch (final IOException ex9) {
                if (this.fHasIncludeReportedContent) {
                    throw new XNIException(ex9);
                }
                this.reportResourceError("TextResourceError", new Object[] { s, ex9.getMessage() }, ex9);
                return false;
            }
            finally {
                if (xIncludeTextReader != null) {
                    try {
                        xIncludeTextReader.close();
                    }
                    catch (final IOException ex10) {
                        this.reportResourceError("TextResourceError", new Object[] { s, ex10.getMessage() }, ex10);
                        return false;
                    }
                }
            }
        }
        this.reportFatalError("InvalidParseValue", new Object[] { s2 });
        return true;
    }
    
    protected boolean hasXIncludeNamespace(final QName qName) {
        return qName.uri == XIncludeHandler.XINCLUDE_NS_URI || this.fNamespaceContext.getURI(qName.prefix) == XIncludeHandler.XINCLUDE_NS_URI;
    }
    
    protected boolean isIncludeElement(final QName qName) {
        return qName.localpart.equals(XIncludeHandler.XINCLUDE_INCLUDE) && this.hasXIncludeNamespace(qName);
    }
    
    protected boolean isFallbackElement(final QName qName) {
        return qName.localpart.equals(XIncludeHandler.XINCLUDE_FALLBACK) && this.hasXIncludeNamespace(qName);
    }
    
    protected boolean sameBaseURIAsIncludeParent() {
        final String includeParentBaseURI = this.getIncludeParentBaseURI();
        final String expandedSystemId = this.fCurrentBaseURI.getExpandedSystemId();
        return includeParentBaseURI != null && includeParentBaseURI.equals(expandedSystemId);
    }
    
    protected boolean sameLanguageAsIncludeParent() {
        final String includeParentLanguage = this.getIncludeParentLanguage();
        return includeParentLanguage != null && includeParentLanguage.equalsIgnoreCase(this.fCurrentLanguage);
    }
    
    protected void setupCurrentBaseURI(final XMLLocator xmlLocator) {
        this.fCurrentBaseURI.setBaseSystemId(xmlLocator.getBaseSystemId());
        if (xmlLocator.getLiteralSystemId() != null) {
            this.fCurrentBaseURI.setLiteralSystemId(xmlLocator.getLiteralSystemId());
        }
        else {
            this.fCurrentBaseURI.setLiteralSystemId(this.fHrefFromParent);
        }
        String expandedSystemId = xmlLocator.getExpandedSystemId();
        if (expandedSystemId == null) {
            try {
                expandedSystemId = XMLEntityManager.expandSystemId(this.fCurrentBaseURI.getLiteralSystemId(), this.fCurrentBaseURI.getBaseSystemId(), false);
                if (expandedSystemId == null) {
                    expandedSystemId = this.fCurrentBaseURI.getLiteralSystemId();
                }
            }
            catch (final URI.MalformedURIException ex) {
                this.reportFatalError("ExpandedSystemId");
            }
        }
        this.fCurrentBaseURI.setExpandedSystemId(expandedSystemId);
    }
    
    protected boolean searchForRecursiveIncludes(final String s) {
        return s.equals(this.fCurrentBaseURI.getExpandedSystemId()) || (this.fParentXIncludeHandler != null && this.fParentXIncludeHandler.searchForRecursiveIncludes(s));
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
    
    protected XMLAttributes processAttributes(XMLAttributes xmlAttributes) {
        if (this.isTopLevelIncludedItem()) {
            if (this.fFixupBaseURIs && !this.sameBaseURIAsIncludeParent()) {
                if (xmlAttributes == null) {
                    xmlAttributes = new XMLAttributesImpl();
                }
                String s;
                try {
                    s = this.getRelativeBaseURI();
                }
                catch (final URI.MalformedURIException ex) {
                    s = this.fCurrentBaseURI.getExpandedSystemId();
                }
                xmlAttributes.setSpecified(xmlAttributes.addAttribute(XIncludeHandler.XML_BASE_QNAME, XMLSymbols.fCDATASymbol, s), true);
            }
            if (this.fFixupLanguage && !this.sameLanguageAsIncludeParent()) {
                if (xmlAttributes == null) {
                    xmlAttributes = new XMLAttributesImpl();
                }
                xmlAttributes.setSpecified(xmlAttributes.addAttribute(XIncludeHandler.XML_LANG_QNAME, XMLSymbols.fCDATASymbol, this.fCurrentLanguage), true);
            }
            final Enumeration allPrefixes = this.fNamespaceContext.getAllPrefixes();
            while (allPrefixes.hasMoreElements()) {
                final String localpart = allPrefixes.nextElement();
                final String uriFromIncludeParent = this.fNamespaceContext.getURIFromIncludeParent(localpart);
                final String uri = this.fNamespaceContext.getURI(localpart);
                if (uriFromIncludeParent != uri && xmlAttributes != null) {
                    if (localpart == XMLSymbols.EMPTY_STRING) {
                        if (xmlAttributes.getValue(NamespaceContext.XMLNS_URI, XMLSymbols.PREFIX_XMLNS) != null) {
                            continue;
                        }
                        if (xmlAttributes == null) {
                            xmlAttributes = new XMLAttributesImpl();
                        }
                        final QName qName = (QName)XIncludeHandler.NEW_NS_ATTR_QNAME.clone();
                        qName.prefix = null;
                        qName.localpart = XMLSymbols.PREFIX_XMLNS;
                        qName.rawname = XMLSymbols.PREFIX_XMLNS;
                        xmlAttributes.setSpecified(xmlAttributes.addAttribute(qName, XMLSymbols.fCDATASymbol, (uri != null) ? uri : XMLSymbols.EMPTY_STRING), true);
                        this.fNamespaceContext.declarePrefix(localpart, uri);
                    }
                    else {
                        if (xmlAttributes.getValue(NamespaceContext.XMLNS_URI, localpart) != null) {
                            continue;
                        }
                        if (xmlAttributes == null) {
                            xmlAttributes = new XMLAttributesImpl();
                        }
                        final QName qName2 = (QName)XIncludeHandler.NEW_NS_ATTR_QNAME.clone();
                        qName2.localpart = localpart;
                        final StringBuffer sb = new StringBuffer();
                        final QName qName3 = qName2;
                        qName3.rawname = sb.append(qName3.rawname).append(localpart).toString();
                        qName2.rawname = ((this.fSymbolTable != null) ? this.fSymbolTable.addSymbol(qName2.rawname) : qName2.rawname.intern());
                        xmlAttributes.setSpecified(xmlAttributes.addAttribute(qName2, XMLSymbols.fCDATASymbol, (uri != null) ? uri : XMLSymbols.EMPTY_STRING), true);
                        this.fNamespaceContext.declarePrefix(localpart, uri);
                    }
                }
            }
        }
        if (xmlAttributes != null) {
            for (int length = xmlAttributes.getLength(), i = 0; i < length; ++i) {
                final String type = xmlAttributes.getType(i);
                final String value = xmlAttributes.getValue(i);
                if (type == XMLSymbols.fENTITYSymbol) {
                    this.checkUnparsedEntity(value);
                }
                if (type == XMLSymbols.fENTITIESSymbol) {
                    final StringTokenizer stringTokenizer = new StringTokenizer(value);
                    while (stringTokenizer.hasMoreTokens()) {
                        this.checkUnparsedEntity(stringTokenizer.nextToken());
                    }
                }
                else if (type == XMLSymbols.fNOTATIONSymbol) {
                    this.checkNotation(value);
                }
            }
        }
        return xmlAttributes;
    }
    
    protected String getRelativeBaseURI() throws URI.MalformedURIException {
        final int includeParentDepth = this.getIncludeParentDepth();
        String s = this.getRelativeURI(includeParentDepth);
        if (this.isRootDocument()) {
            return s;
        }
        if (s.length() == 0) {
            s = this.fCurrentBaseURI.getLiteralSystemId();
        }
        if (includeParentDepth != 0) {
            return s;
        }
        if (this.fParentRelativeURI == null) {
            this.fParentRelativeURI = this.fParentXIncludeHandler.getRelativeBaseURI();
        }
        if (this.fParentRelativeURI.length() == 0) {
            return s;
        }
        final URI uri = new URI(this.fParentRelativeURI, true);
        final URI uri2 = new URI(uri, s);
        if (!this.isEqual(uri.getScheme(), uri2.getScheme())) {
            return s;
        }
        if (!this.isEqual(uri.getAuthority(), uri2.getAuthority())) {
            return uri2.getSchemeSpecificPart();
        }
        final String path = uri2.getPath();
        final String queryString = uri2.getQueryString();
        final String fragment = uri2.getFragment();
        if (queryString != null || fragment != null) {
            final StringBuffer sb = new StringBuffer();
            if (path != null) {
                sb.append(path);
            }
            if (queryString != null) {
                sb.append('?');
                sb.append(queryString);
            }
            if (fragment != null) {
                sb.append('#');
                sb.append(fragment);
            }
            return sb.toString();
        }
        return path;
    }
    
    private String getIncludeParentBaseURI() {
        final int includeParentDepth = this.getIncludeParentDepth();
        if (!this.isRootDocument() && includeParentDepth == 0) {
            return this.fParentXIncludeHandler.getIncludeParentBaseURI();
        }
        return this.getBaseURI(includeParentDepth);
    }
    
    private String getIncludeParentLanguage() {
        final int includeParentDepth = this.getIncludeParentDepth();
        if (!this.isRootDocument() && includeParentDepth == 0) {
            return this.fParentXIncludeHandler.getIncludeParentLanguage();
        }
        return this.getLanguage(includeParentDepth);
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
    
    protected Augmentations modifyAugmentations(final Augmentations augmentations) {
        return this.modifyAugmentations(augmentations, false);
    }
    
    protected Augmentations modifyAugmentations(Augmentations augmentations, final boolean b) {
        if (b || this.isTopLevelIncludedItem()) {
            if (augmentations == null) {
                augmentations = new AugmentationsImpl();
            }
            augmentations.putItem(XIncludeHandler.XINCLUDE_INCLUDED, Boolean.TRUE);
        }
        return augmentations;
    }
    
    protected int getState(final int n) {
        return this.fState[n];
    }
    
    protected int getState() {
        return this.fState[this.fDepth];
    }
    
    protected void setState(final int n) {
        if (this.fDepth >= this.fState.length) {
            final int[] fState = new int[this.fDepth * 2];
            System.arraycopy(this.fState, 0, fState, 0, this.fState.length);
            this.fState = fState;
        }
        this.fState[this.fDepth] = n;
    }
    
    protected void setSawFallback(final int n, final boolean b) {
        if (n >= this.fSawFallback.length) {
            final boolean[] fSawFallback = new boolean[n * 2];
            System.arraycopy(this.fSawFallback, 0, fSawFallback, 0, this.fSawFallback.length);
            this.fSawFallback = fSawFallback;
        }
        this.fSawFallback[n] = b;
    }
    
    protected boolean getSawFallback(final int n) {
        return n < this.fSawFallback.length && this.fSawFallback[n];
    }
    
    protected void setSawInclude(final int n, final boolean b) {
        if (n >= this.fSawInclude.length) {
            final boolean[] fSawInclude = new boolean[n * 2];
            System.arraycopy(this.fSawInclude, 0, fSawInclude, 0, this.fSawInclude.length);
            this.fSawInclude = fSawInclude;
        }
        this.fSawInclude[n] = b;
    }
    
    protected boolean getSawInclude(final int n) {
        return n < this.fSawInclude.length && this.fSawInclude[n];
    }
    
    protected void reportResourceError(final String s) {
        this.reportResourceError(s, null);
    }
    
    protected void reportResourceError(final String s, final Object[] array) {
        this.reportResourceError(s, array, null);
    }
    
    protected void reportResourceError(final String s, final Object[] array, final Exception ex) {
        this.reportError(s, array, (short)0, ex);
    }
    
    protected void reportFatalError(final String s) {
        this.reportFatalError(s, null);
    }
    
    protected void reportFatalError(final String s, final Object[] array) {
        this.reportFatalError(s, array, null);
    }
    
    protected void reportFatalError(final String s, final Object[] array, final Exception ex) {
        this.reportError(s, array, (short)2, ex);
    }
    
    private void reportError(final String s, final Object[] array, final short n, final Exception ex) {
        if (this.fErrorReporter != null) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/xinclude", s, array, n, ex);
        }
    }
    
    protected void setParent(final XIncludeHandler fParentXIncludeHandler) {
        this.fParentXIncludeHandler = fParentXIncludeHandler;
    }
    
    protected void setHref(final String fHrefFromParent) {
        this.fHrefFromParent = fHrefFromParent;
    }
    
    protected void setXIncludeLocator(final XMLLocatorWrapper fxIncludeLocator) {
        this.fXIncludeLocator = fxIncludeLocator;
    }
    
    protected boolean isRootDocument() {
        return this.fParentXIncludeHandler == null;
    }
    
    protected void addUnparsedEntity(final String name, final XMLResourceIdentifier xmlResourceIdentifier, final String notation, final Augmentations augmentations) {
        final UnparsedEntity unparsedEntity = new UnparsedEntity();
        unparsedEntity.name = name;
        unparsedEntity.systemId = xmlResourceIdentifier.getLiteralSystemId();
        unparsedEntity.publicId = xmlResourceIdentifier.getPublicId();
        unparsedEntity.baseURI = xmlResourceIdentifier.getBaseSystemId();
        unparsedEntity.expandedSystemId = xmlResourceIdentifier.getExpandedSystemId();
        unparsedEntity.notation = notation;
        unparsedEntity.augmentations = augmentations;
        this.fUnparsedEntities.add(unparsedEntity);
    }
    
    protected void addNotation(final String name, final XMLResourceIdentifier xmlResourceIdentifier, final Augmentations augmentations) {
        final Notation notation = new Notation();
        notation.name = name;
        notation.systemId = xmlResourceIdentifier.getLiteralSystemId();
        notation.publicId = xmlResourceIdentifier.getPublicId();
        notation.baseURI = xmlResourceIdentifier.getBaseSystemId();
        notation.expandedSystemId = xmlResourceIdentifier.getExpandedSystemId();
        notation.augmentations = augmentations;
        this.fNotations.add(notation);
    }
    
    protected void checkUnparsedEntity(final String name) {
        final UnparsedEntity unparsedEntity = new UnparsedEntity();
        unparsedEntity.name = name;
        final int index = this.fUnparsedEntities.indexOf(unparsedEntity);
        if (index != -1) {
            final UnparsedEntity unparsedEntity2 = this.fUnparsedEntities.get(index);
            this.checkNotation(unparsedEntity2.notation);
            this.checkAndSendUnparsedEntity(unparsedEntity2);
        }
    }
    
    protected void checkNotation(final String name) {
        final Notation notation = new Notation();
        notation.name = name;
        final int index = this.fNotations.indexOf(notation);
        if (index != -1) {
            this.checkAndSendNotation((Notation)this.fNotations.get(index));
        }
    }
    
    protected void checkAndSendUnparsedEntity(final UnparsedEntity unparsedEntity) {
        if (this.isRootDocument()) {
            final int index = this.fUnparsedEntities.indexOf(unparsedEntity);
            if (index == -1) {
                final XMLResourceIdentifierImpl xmlResourceIdentifierImpl = new XMLResourceIdentifierImpl(unparsedEntity.publicId, unparsedEntity.systemId, unparsedEntity.baseURI, unparsedEntity.expandedSystemId);
                this.addUnparsedEntity(unparsedEntity.name, xmlResourceIdentifierImpl, unparsedEntity.notation, unparsedEntity.augmentations);
                if (this.fSendUEAndNotationEvents && this.fDTDHandler != null) {
                    this.fDTDHandler.unparsedEntityDecl(unparsedEntity.name, xmlResourceIdentifierImpl, unparsedEntity.notation, unparsedEntity.augmentations);
                }
            }
            else if (!unparsedEntity.isDuplicate(this.fUnparsedEntities.get(index))) {
                this.reportFatalError("NonDuplicateUnparsedEntity", new Object[] { unparsedEntity.name });
            }
        }
        else {
            this.fParentXIncludeHandler.checkAndSendUnparsedEntity(unparsedEntity);
        }
    }
    
    protected void checkAndSendNotation(final Notation notation) {
        if (this.isRootDocument()) {
            final int index = this.fNotations.indexOf(notation);
            if (index == -1) {
                final XMLResourceIdentifierImpl xmlResourceIdentifierImpl = new XMLResourceIdentifierImpl(notation.publicId, notation.systemId, notation.baseURI, notation.expandedSystemId);
                this.addNotation(notation.name, xmlResourceIdentifierImpl, notation.augmentations);
                if (this.fSendUEAndNotationEvents && this.fDTDHandler != null) {
                    this.fDTDHandler.notationDecl(notation.name, xmlResourceIdentifierImpl, notation.augmentations);
                }
            }
            else if (!notation.isDuplicate(this.fNotations.get(index))) {
                this.reportFatalError("NonDuplicateNotation", new Object[] { notation.name });
            }
        }
        else {
            this.fParentXIncludeHandler.checkAndSendNotation(notation);
        }
    }
    
    private void checkWhitespace(final XMLString xmlString) {
        for (int n = xmlString.offset + xmlString.length, i = xmlString.offset; i < n; ++i) {
            if (!XMLChar.isSpace(xmlString.ch[i])) {
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
    
    private void setRootElementProcessed(final boolean b) {
        if (this.isRootDocument()) {
            this.fSeenRootElement = b;
            return;
        }
        this.fParentXIncludeHandler.setRootElementProcessed(b);
    }
    
    private boolean getRootElementProcessed() {
        return this.isRootDocument() ? this.fSeenRootElement : this.fParentXIncludeHandler.getRootElementProcessed();
    }
    
    protected void copyFeatures(final XMLComponentManager xmlComponentManager, final ParserConfigurationSettings parserConfigurationSettings) {
        this.copyFeatures1(Constants.getXercesFeatures(), "http://apache.org/xml/features/", xmlComponentManager, parserConfigurationSettings);
        this.copyFeatures1(Constants.getSAXFeatures(), "http://xml.org/sax/features/", xmlComponentManager, parserConfigurationSettings);
    }
    
    protected void copyFeatures(final XMLComponentManager xmlComponentManager, final XMLParserConfiguration xmlParserConfiguration) {
        this.copyFeatures1(Constants.getXercesFeatures(), "http://apache.org/xml/features/", xmlComponentManager, xmlParserConfiguration);
        this.copyFeatures1(Constants.getSAXFeatures(), "http://xml.org/sax/features/", xmlComponentManager, xmlParserConfiguration);
    }
    
    private void copyFeatures1(final Enumeration enumeration, final String s, final XMLComponentManager xmlComponentManager, final ParserConfigurationSettings parserConfigurationSettings) {
        while (enumeration.hasMoreElements()) {
            final String string = s + enumeration.nextElement();
            parserConfigurationSettings.addRecognizedFeatures(new String[] { string });
            try {
                parserConfigurationSettings.setFeature(string, xmlComponentManager.getFeature(string));
            }
            catch (final XMLConfigurationException ex) {}
        }
    }
    
    private void copyFeatures1(final Enumeration enumeration, final String s, final XMLComponentManager xmlComponentManager, final XMLParserConfiguration xmlParserConfiguration) {
        while (enumeration.hasMoreElements()) {
            final String string = s + enumeration.nextElement();
            final boolean feature = xmlComponentManager.getFeature(string);
            try {
                xmlParserConfiguration.setFeature(string, feature);
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
    
    protected void saveLanguage(final String s) {
        this.fLanguageScope.push(this.fDepth);
        this.fLanguageStack.push(s);
    }
    
    public String restoreLanguage() {
        this.fLanguageStack.pop();
        this.fLanguageScope.pop();
        return this.fLanguageStack.peek();
    }
    
    public String getBaseURI(final int n) {
        return (String)this.fExpandedSystemID.elementAt(this.scopeOfBaseURI(n));
    }
    
    public String getLanguage(final int n) {
        return (String)this.fLanguageStack.elementAt(this.scopeOfLanguage(n));
    }
    
    public String getRelativeURI(final int n) throws URI.MalformedURIException {
        final int n2 = this.scopeOfBaseURI(n) + 1;
        if (n2 == this.fBaseURIScope.size()) {
            return "";
        }
        URI uri = new URI("file", (String)this.fLiteralSystemID.elementAt(n2));
        for (int i = n2 + 1; i < this.fBaseURIScope.size(); ++i) {
            uri = new URI(uri, (String)this.fLiteralSystemID.elementAt(i));
        }
        return uri.getPath();
    }
    
    private int scopeOfBaseURI(final int n) {
        for (int i = this.fBaseURIScope.size() - 1; i >= 0; --i) {
            if (this.fBaseURIScope.elementAt(i) <= n) {
                return i;
            }
        }
        return -1;
    }
    
    private int scopeOfLanguage(final int n) {
        for (int i = this.fLanguageScope.size() - 1; i >= 0; --i) {
            if (this.fLanguageScope.elementAt(i) <= n) {
                return i;
            }
        }
        return -1;
    }
    
    protected void processXMLBaseAttributes(final XMLAttributes xmlAttributes) {
        final String value = xmlAttributes.getValue(NamespaceContext.XML_URI, "base");
        if (value != null) {
            try {
                final String expandSystemId = XMLEntityManager.expandSystemId(value, this.fCurrentBaseURI.getExpandedSystemId(), false);
                this.fCurrentBaseURI.setLiteralSystemId(value);
                this.fCurrentBaseURI.setBaseSystemId(this.fCurrentBaseURI.getExpandedSystemId());
                this.fCurrentBaseURI.setExpandedSystemId(expandSystemId);
                this.saveBaseURI();
            }
            catch (final URI.MalformedURIException ex) {}
        }
    }
    
    protected void processXMLLangAttributes(final XMLAttributes xmlAttributes) {
        final String value = xmlAttributes.getValue(NamespaceContext.XML_URI, "lang");
        if (value != null) {
            this.saveLanguage(this.fCurrentLanguage = value);
        }
    }
    
    private boolean isValidInHTTPHeader(final String s) {
        for (int i = s.length() - 1; i >= 0; --i) {
            final char char1 = s.charAt(i);
            if (char1 < ' ' || char1 > '~') {
                return false;
            }
        }
        return true;
    }
    
    private XMLInputSource createInputSource(final String s, final String s2, final String s3, final String s4, final String s5) {
        final HTTPInputSource httpInputSource = new HTTPInputSource(s, s2, s3);
        if (s4 != null && s4.length() > 0) {
            httpInputSource.setHTTPRequestProperty("Accept", s4);
        }
        if (s5 != null && s5.length() > 0) {
            httpInputSource.setHTTPRequestProperty("Accept-Language", s5);
        }
        return httpInputSource;
    }
    
    private boolean isEqual(final String s, final String s2) {
        return s == s2 || (s != null && s.equals(s2));
    }
    
    private String escapeHref(final String s) {
        int n = s.length();
        final StringBuffer sb = new StringBuffer(n * 3);
        int i;
        for (i = 0; i < n; ++i) {
            final char char1 = s.charAt(i);
            if (char1 > '~') {
                break;
            }
            if (char1 < ' ') {
                return s;
            }
            if (XIncludeHandler.gNeedEscaping[char1]) {
                sb.append('%');
                sb.append(XIncludeHandler.gAfterEscaping1[char1]);
                sb.append(XIncludeHandler.gAfterEscaping2[char1]);
            }
            else {
                sb.append(char1);
            }
        }
        if (i < n) {
            for (int j = i; j < n; ++j) {
                final char char2 = s.charAt(j);
                if ((char2 < ' ' || char2 > '~') && (char2 < ' ' || char2 > '\ud7ff') && (char2 < '\uf900' || char2 > '\ufdcf')) {
                    if (char2 < '\ufdf0' || char2 > '\uffef') {
                        if (XMLChar.isHighSurrogate(char2) && ++j < n) {
                            final char char3 = s.charAt(j);
                            if (XMLChar.isLowSurrogate(char3)) {
                                final int supplemental = XMLChar.supplemental(char2, char3);
                                if (supplemental < 983040 && (supplemental & 0xFFFF) <= 65533) {
                                    continue;
                                }
                            }
                        }
                        return s;
                    }
                }
            }
            byte[] bytes;
            try {
                bytes = s.substring(i).getBytes("UTF-8");
            }
            catch (final UnsupportedEncodingException ex) {
                return s;
            }
            n = bytes.length;
            for (final byte b : bytes) {
                if (b < 0) {
                    final int n2 = b + 256;
                    sb.append('%');
                    sb.append(XIncludeHandler.gHexChs[n2 >> 4]);
                    sb.append(XIncludeHandler.gHexChs[n2 & 0xF]);
                }
                else if (XIncludeHandler.gNeedEscaping[b]) {
                    sb.append('%');
                    sb.append(XIncludeHandler.gAfterEscaping1[b]);
                    sb.append(XIncludeHandler.gAfterEscaping2[b]);
                }
                else {
                    sb.append((char)b);
                }
            }
        }
        if (sb.length() != n) {
            return sb.toString();
        }
        return s;
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
        PROPERTY_DEFAULTS = new Object[] { null, null, null, new Integer(2048) };
        gNeedEscaping = new boolean[128];
        gAfterEscaping1 = new char[128];
        gAfterEscaping2 = new char[128];
        gHexChs = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        for (final char c : new char[] { ' ', '<', '>', '\"', '{', '}', '|', '\\', '^', '`' }) {
            XIncludeHandler.gNeedEscaping[c] = true;
            XIncludeHandler.gAfterEscaping1[c] = XIncludeHandler.gHexChs[c >> 4];
            XIncludeHandler.gAfterEscaping2[c] = XIncludeHandler.gHexChs[c & '\u000f'];
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
        
        public boolean equals(final Object o) {
            return o != null && o instanceof Notation && this.name.equals(((Notation)o).name);
        }
        
        public boolean isDuplicate(final Object o) {
            if (o != null && o instanceof Notation) {
                final Notation notation = (Notation)o;
                return this.name.equals(notation.name) && this.isEqual(this.publicId, notation.publicId) && this.isEqual(this.expandedSystemId, notation.expandedSystemId);
            }
            return false;
        }
        
        private boolean isEqual(final String s, final String s2) {
            return s == s2 || (s != null && s.equals(s2));
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
        
        public boolean equals(final Object o) {
            return o != null && o instanceof UnparsedEntity && this.name.equals(((UnparsedEntity)o).name);
        }
        
        public boolean isDuplicate(final Object o) {
            if (o != null && o instanceof UnparsedEntity) {
                final UnparsedEntity unparsedEntity = (UnparsedEntity)o;
                return this.name.equals(unparsedEntity.name) && this.isEqual(this.publicId, unparsedEntity.publicId) && this.isEqual(this.expandedSystemId, unparsedEntity.expandedSystemId) && this.isEqual(this.notation, unparsedEntity.notation);
            }
            return false;
        }
        
        private boolean isEqual(final String s, final String s2) {
            return s == s2 || (s != null && s.equals(s2));
        }
    }
}
