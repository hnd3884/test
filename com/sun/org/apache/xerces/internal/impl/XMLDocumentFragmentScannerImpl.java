package com.sun.org.apache.xerces.internal.impl;

import java.io.EOFException;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.util.AugmentationsImpl;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.xml.internal.stream.dtd.DTDGrammarUtil;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.util.XMLAttributesIteratorImpl;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.xml.internal.stream.XMLEntityStorage;
import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;
import com.sun.xml.internal.stream.XMLBufferListener;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentScanner;

public class XMLDocumentFragmentScannerImpl extends XMLScanner implements XMLDocumentScanner, XMLComponent, XMLEntityHandler, XMLBufferListener
{
    protected int fElementAttributeLimit;
    protected int fXMLNameLimit;
    protected ExternalSubsetResolver fExternalSubsetResolver;
    protected static final int SCANNER_STATE_START_OF_MARKUP = 21;
    protected static final int SCANNER_STATE_CONTENT = 22;
    protected static final int SCANNER_STATE_PI = 23;
    protected static final int SCANNER_STATE_DOCTYPE = 24;
    protected static final int SCANNER_STATE_XML_DECL = 25;
    protected static final int SCANNER_STATE_ROOT_ELEMENT = 26;
    protected static final int SCANNER_STATE_COMMENT = 27;
    protected static final int SCANNER_STATE_REFERENCE = 28;
    protected static final int SCANNER_STATE_ATTRIBUTE = 29;
    protected static final int SCANNER_STATE_ATTRIBUTE_VALUE = 30;
    protected static final int SCANNER_STATE_END_OF_INPUT = 33;
    protected static final int SCANNER_STATE_TERMINATED = 34;
    protected static final int SCANNER_STATE_CDATA = 35;
    protected static final int SCANNER_STATE_TEXT_DECL = 36;
    protected static final int SCANNER_STATE_CHARACTER_DATA = 37;
    protected static final int SCANNER_STATE_START_ELEMENT_TAG = 38;
    protected static final int SCANNER_STATE_END_ELEMENT_TAG = 39;
    protected static final int SCANNER_STATE_CHAR_REFERENCE = 40;
    protected static final int SCANNER_STATE_BUILT_IN_REFS = 41;
    protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String STANDARD_URI_CONFORMANT = "http://apache.org/xml/features/standard-uri-conformant";
    private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
    static final String EXTERNAL_ACCESS_DEFAULT = "all";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] FEATURE_DEFAULTS;
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] PROPERTY_DEFAULTS;
    private static final char[] cdata;
    static final char[] xmlDecl;
    private static final boolean DEBUG_SCANNER_STATE = false;
    private static final boolean DEBUG_DISPATCHER = false;
    protected static final boolean DEBUG_START_END_ELEMENT = false;
    protected static final boolean DEBUG_NEXT = false;
    protected static final boolean DEBUG = false;
    protected static final boolean DEBUG_COALESCE = false;
    protected XMLDocumentHandler fDocumentHandler;
    protected int fScannerLastState;
    protected XMLEntityStorage fEntityStore;
    protected int[] fEntityStack;
    protected int fMarkupDepth;
    protected boolean fEmptyElement;
    protected boolean fReadingAttributes;
    protected int fScannerState;
    protected boolean fInScanContent;
    protected boolean fLastSectionWasCData;
    protected boolean fLastSectionWasEntityReference;
    protected boolean fLastSectionWasCharacterData;
    protected boolean fHasExternalDTD;
    protected boolean fStandaloneSet;
    protected boolean fStandalone;
    protected String fVersion;
    protected QName fCurrentElement;
    protected ElementStack fElementStack;
    protected ElementStack2 fElementStack2;
    protected String fPITarget;
    protected XMLString fPIData;
    protected boolean fNotifyBuiltInRefs;
    protected boolean fSupportDTD;
    protected boolean fReplaceEntityReferences;
    protected boolean fSupportExternalEntities;
    protected boolean fReportCdataEvent;
    protected boolean fIsCoalesce;
    protected String fDeclaredEncoding;
    protected boolean fDisallowDoctype;
    protected String fAccessExternalDTD;
    protected boolean fStrictURI;
    protected Driver fDriver;
    protected Driver fContentDriver;
    protected QName fElementQName;
    protected QName fAttributeQName;
    protected XMLAttributesIteratorImpl fAttributes;
    protected XMLString fTempString;
    protected XMLString fTempString2;
    private String[] fStrings;
    protected XMLStringBuffer fStringBuffer;
    protected XMLStringBuffer fStringBuffer2;
    protected XMLStringBuffer fContentBuffer;
    private final char[] fSingleChar;
    private String fCurrentEntityName;
    protected boolean fScanToEnd;
    protected DTDGrammarUtil dtdGrammarUtil;
    protected boolean fAddDefaultAttr;
    protected boolean foundBuiltInRefs;
    static final short MAX_DEPTH_LIMIT = 5;
    static final short ELEMENT_ARRAY_LENGTH = 200;
    static final short MAX_POINTER_AT_A_DEPTH = 4;
    static final boolean DEBUG_SKIP_ALGORITHM = false;
    String[] fElementArray;
    short fLastPointerLocation;
    short fElementPointer;
    short[][] fPointerInfo;
    protected String fElementRawname;
    protected boolean fShouldSkip;
    protected boolean fAdd;
    protected boolean fSkip;
    private Augmentations fTempAugmentations;
    protected boolean fUsebuffer;
    
    public XMLDocumentFragmentScannerImpl() {
        this.fEntityStack = new int[4];
        this.fReadingAttributes = false;
        this.fInScanContent = false;
        this.fLastSectionWasCData = false;
        this.fLastSectionWasEntityReference = false;
        this.fLastSectionWasCharacterData = false;
        this.fElementStack = new ElementStack();
        this.fElementStack2 = new ElementStack2();
        this.fPIData = new XMLString();
        this.fNotifyBuiltInRefs = false;
        this.fSupportDTD = true;
        this.fReplaceEntityReferences = true;
        this.fSupportExternalEntities = false;
        this.fReportCdataEvent = false;
        this.fIsCoalesce = false;
        this.fDeclaredEncoding = null;
        this.fDisallowDoctype = false;
        this.fAccessExternalDTD = "all";
        this.fContentDriver = this.createContentDriver();
        this.fElementQName = new QName();
        this.fAttributeQName = new QName();
        this.fAttributes = new XMLAttributesIteratorImpl();
        this.fTempString = new XMLString();
        this.fTempString2 = new XMLString();
        this.fStrings = new String[3];
        this.fStringBuffer = new XMLStringBuffer();
        this.fStringBuffer2 = new XMLStringBuffer();
        this.fContentBuffer = new XMLStringBuffer();
        this.fSingleChar = new char[1];
        this.fCurrentEntityName = null;
        this.fScanToEnd = false;
        this.dtdGrammarUtil = null;
        this.fAddDefaultAttr = false;
        this.foundBuiltInRefs = false;
        this.fElementArray = new String[200];
        this.fLastPointerLocation = 0;
        this.fElementPointer = 0;
        this.fPointerInfo = new short[5][4];
        this.fShouldSkip = false;
        this.fAdd = false;
        this.fSkip = false;
        this.fTempAugmentations = null;
    }
    
    @Override
    public void setInputSource(final XMLInputSource inputSource) throws IOException {
        this.fEntityManager.setEntityHandler(this);
        this.fEntityManager.startEntity(false, "$fragment$", inputSource, false, true);
    }
    
    @Override
    public boolean scanDocument(final boolean complete) throws IOException, XNIException {
        this.fEntityManager.setEntityHandler(this);
        int event = this.next();
        do {
            switch (event) {
                case 7: {
                    break;
                }
                case 1: {
                    break;
                }
                case 4: {
                    this.fEntityScanner.checkNodeCount(this.fEntityScanner.fCurrentEntity);
                    this.fDocumentHandler.characters(this.getCharacterData(), null);
                    break;
                }
                case 6: {
                    break;
                }
                case 9: {
                    this.fEntityScanner.checkNodeCount(this.fEntityScanner.fCurrentEntity);
                    break;
                }
                case 3: {
                    this.fEntityScanner.checkNodeCount(this.fEntityScanner.fCurrentEntity);
                    this.fDocumentHandler.processingInstruction(this.getPITarget(), this.getPIData(), null);
                    break;
                }
                case 5: {
                    this.fEntityScanner.checkNodeCount(this.fEntityScanner.fCurrentEntity);
                    this.fDocumentHandler.comment(this.getCharacterData(), null);
                    break;
                }
                case 11: {
                    break;
                }
                case 12: {
                    this.fEntityScanner.checkNodeCount(this.fEntityScanner.fCurrentEntity);
                    this.fDocumentHandler.startCDATA(null);
                    this.fDocumentHandler.characters(this.getCharacterData(), null);
                    this.fDocumentHandler.endCDATA(null);
                    break;
                }
                case 14: {
                    break;
                }
                case 15: {
                    break;
                }
                case 13: {
                    break;
                }
                case 10: {
                    break;
                }
                case 2: {
                    break;
                }
                default: {
                    throw new InternalError("processing event: " + event);
                }
            }
            event = this.next();
        } while (event != 8 && complete);
        if (event == 8) {
            this.fDocumentHandler.endDocument(null);
            return false;
        }
        return true;
    }
    
    public QName getElementQName() {
        if (this.fScannerLastState == 2) {
            this.fElementQName.setValues(this.fElementStack.getLastPoppedElement());
        }
        return this.fElementQName;
    }
    
    @Override
    public int next() throws IOException, XNIException {
        return this.fDriver.next();
    }
    
    @Override
    public void reset(final XMLComponentManager componentManager) throws XMLConfigurationException {
        super.reset(componentManager);
        this.fReportCdataEvent = componentManager.getFeature("report-cdata-event", true);
        this.fSecurityManager = (XMLSecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager", null);
        this.fNotifyBuiltInRefs = componentManager.getFeature("http://apache.org/xml/features/scanner/notify-builtin-refs", false);
        final Object resolver = componentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver", null);
        this.fExternalSubsetResolver = ((resolver instanceof ExternalSubsetResolver) ? ((ExternalSubsetResolver)resolver) : null);
        this.fReadingAttributes = false;
        this.fSupportExternalEntities = true;
        this.fReplaceEntityReferences = true;
        this.fIsCoalesce = false;
        this.setScannerState(22);
        this.setDriver(this.fContentDriver);
        final XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)componentManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", null);
        this.fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
        this.fStrictURI = componentManager.getFeature("http://apache.org/xml/features/standard-uri-conformant", false);
        this.resetCommon();
    }
    
    @Override
    public void reset(final PropertyManager propertyManager) {
        super.reset(propertyManager);
        this.fNamespaces = (boolean)propertyManager.getProperty("javax.xml.stream.isNamespaceAware");
        this.fNotifyBuiltInRefs = false;
        Boolean bo = (Boolean)propertyManager.getProperty("javax.xml.stream.isReplacingEntityReferences");
        this.fReplaceEntityReferences = bo;
        bo = (Boolean)propertyManager.getProperty("javax.xml.stream.isSupportingExternalEntities");
        this.fSupportExternalEntities = bo;
        final Boolean cdata = (Boolean)propertyManager.getProperty("http://java.sun.com/xml/stream/properties/report-cdata-event");
        if (cdata != null) {
            this.fReportCdataEvent = cdata;
        }
        final Boolean coalesce = (Boolean)propertyManager.getProperty("javax.xml.stream.isCoalescing");
        if (coalesce != null) {
            this.fIsCoalesce = coalesce;
        }
        this.fReportCdataEvent = (!this.fIsCoalesce && this.fReportCdataEvent);
        this.fReplaceEntityReferences = (this.fIsCoalesce || this.fReplaceEntityReferences);
        final XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)propertyManager.getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
        this.fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
        this.fSecurityManager = (XMLSecurityManager)propertyManager.getProperty("http://apache.org/xml/properties/security-manager");
        this.resetCommon();
    }
    
    void resetCommon() {
        this.fMarkupDepth = 0;
        this.fCurrentElement = null;
        this.fElementStack.clear();
        this.fHasExternalDTD = false;
        this.fStandaloneSet = false;
        this.fStandalone = false;
        this.fInScanContent = false;
        this.fShouldSkip = false;
        this.fAdd = false;
        this.fSkip = false;
        this.fEntityStore = this.fEntityManager.getEntityStore();
        this.dtdGrammarUtil = null;
        if (this.fSecurityManager != null) {
            this.fElementAttributeLimit = this.fSecurityManager.getLimit(XMLSecurityManager.Limit.ELEMENT_ATTRIBUTE_LIMIT);
            this.fXMLNameLimit = this.fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT);
        }
        else {
            this.fElementAttributeLimit = 0;
            this.fXMLNameLimit = XMLSecurityManager.Limit.MAX_NAME_LIMIT.defaultValue();
        }
        this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer;
    }
    
    @Override
    public String[] getRecognizedFeatures() {
        return XMLDocumentFragmentScannerImpl.RECOGNIZED_FEATURES.clone();
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        super.setFeature(featureId, state);
        if (featureId.startsWith("http://apache.org/xml/features/")) {
            final String feature = featureId.substring("http://apache.org/xml/features/".length());
            if (feature.equals("scanner/notify-builtin-refs")) {
                this.fNotifyBuiltInRefs = state;
            }
        }
    }
    
    @Override
    public String[] getRecognizedProperties() {
        return XMLDocumentFragmentScannerImpl.RECOGNIZED_PROPERTIES.clone();
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        super.setProperty(propertyId, value);
        if (propertyId.startsWith("http://apache.org/xml/properties/")) {
            final int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
            if (suffixLength == "internal/entity-manager".length() && propertyId.endsWith("internal/entity-manager")) {
                this.fEntityManager = (XMLEntityManager)value;
                return;
            }
            if (suffixLength == "internal/entity-resolver".length() && propertyId.endsWith("internal/entity-resolver")) {
                this.fExternalSubsetResolver = ((value instanceof ExternalSubsetResolver) ? ((ExternalSubsetResolver)value) : null);
                return;
            }
        }
        if (propertyId.startsWith("http://apache.org/xml/properties/")) {
            final String property = propertyId.substring("http://apache.org/xml/properties/".length());
            if (property.equals("internal/entity-manager")) {
                this.fEntityManager = (XMLEntityManager)value;
            }
            return;
        }
        if (propertyId.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
            final XMLSecurityPropertyManager spm = (XMLSecurityPropertyManager)value;
            this.fAccessExternalDTD = spm.getValue(XMLSecurityPropertyManager.Property.ACCESS_EXTERNAL_DTD);
        }
    }
    
    @Override
    public Boolean getFeatureDefault(final String featureId) {
        for (int i = 0; i < XMLDocumentFragmentScannerImpl.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLDocumentFragmentScannerImpl.RECOGNIZED_FEATURES[i].equals(featureId)) {
                return XMLDocumentFragmentScannerImpl.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    @Override
    public Object getPropertyDefault(final String propertyId) {
        for (int i = 0; i < XMLDocumentFragmentScannerImpl.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLDocumentFragmentScannerImpl.RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return XMLDocumentFragmentScannerImpl.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    @Override
    public void setDocumentHandler(final XMLDocumentHandler documentHandler) {
        this.fDocumentHandler = documentHandler;
    }
    
    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    @Override
    public void startEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
        if (this.fEntityDepth == this.fEntityStack.length) {
            final int[] entityarray = new int[this.fEntityStack.length * 2];
            System.arraycopy(this.fEntityStack, 0, entityarray, 0, this.fEntityStack.length);
            this.fEntityStack = entityarray;
        }
        this.fEntityStack[this.fEntityDepth] = this.fMarkupDepth;
        super.startEntity(name, identifier, encoding, augs);
        if (this.fStandalone && this.fEntityStore.isEntityDeclInExternalSubset(name)) {
            this.reportFatalError("MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[] { name });
        }
        if (this.fDocumentHandler != null && !this.fScanningAttribute && !name.equals("[xml]")) {
            this.fDocumentHandler.startGeneralEntity(name, identifier, encoding, augs);
        }
    }
    
    @Override
    public void endEntity(final String name, final Augmentations augs) throws IOException, XNIException {
        super.endEntity(name, augs);
        if (this.fMarkupDepth != this.fEntityStack[this.fEntityDepth]) {
            this.reportFatalError("MarkupEntityMismatch", null);
        }
        if (this.fDocumentHandler != null && !this.fScanningAttribute && !name.equals("[xml]")) {
            this.fDocumentHandler.endGeneralEntity(name, augs);
        }
    }
    
    protected Driver createContentDriver() {
        return new FragmentContentDriver();
    }
    
    protected void scanXMLDeclOrTextDecl(final boolean scanningTextDecl) throws IOException, XNIException {
        super.scanXMLDeclOrTextDecl(scanningTextDecl, this.fStrings);
        --this.fMarkupDepth;
        final String version = this.fStrings[0];
        final String encoding = this.fStrings[1];
        final String standalone = this.fStrings[2];
        this.fDeclaredEncoding = encoding;
        this.fStandaloneSet = (standalone != null);
        this.fStandalone = (this.fStandaloneSet && standalone.equals("yes"));
        this.fEntityManager.setStandalone(this.fStandalone);
        if (this.fDocumentHandler != null) {
            if (scanningTextDecl) {
                this.fDocumentHandler.textDecl(version, encoding, null);
            }
            else {
                this.fDocumentHandler.xmlDecl(version, encoding, standalone, null);
            }
        }
        if (version != null) {
            this.fEntityScanner.setVersion(version);
            this.fEntityScanner.setXMLVersion(version);
        }
        if (encoding != null && !this.fEntityScanner.getCurrentEntity().isEncodingExternallySpecified()) {
            this.fEntityScanner.setEncoding(encoding);
        }
    }
    
    public String getPITarget() {
        return this.fPITarget;
    }
    
    public XMLStringBuffer getPIData() {
        return this.fContentBuffer;
    }
    
    public XMLString getCharacterData() {
        if (this.fUsebuffer) {
            return this.fContentBuffer;
        }
        return this.fTempString;
    }
    
    @Override
    protected void scanPIData(final String target, final XMLStringBuffer data) throws IOException, XNIException {
        super.scanPIData(target, data);
        this.fPITarget = target;
        --this.fMarkupDepth;
    }
    
    protected void scanComment() throws IOException, XNIException {
        this.fContentBuffer.clear();
        this.scanComment(this.fContentBuffer);
        this.fUsebuffer = true;
        --this.fMarkupDepth;
    }
    
    public String getComment() {
        return this.fContentBuffer.toString();
    }
    
    void addElement(final String rawname) {
        if (this.fElementPointer < 200) {
            this.fElementArray[this.fElementPointer] = rawname;
            if (this.fElementStack.fDepth < 5) {
                final short column = this.storePointerForADepth(this.fElementPointer);
                if (column > 0) {
                    final short pointer = this.getElementPointer((short)this.fElementStack.fDepth, (short)(column - 1));
                    if (rawname == this.fElementArray[pointer]) {
                        this.fShouldSkip = true;
                        this.fLastPointerLocation = pointer;
                        this.resetPointer((short)this.fElementStack.fDepth, column);
                        this.fElementArray[this.fElementPointer] = null;
                        return;
                    }
                    this.fShouldSkip = false;
                }
            }
            ++this.fElementPointer;
        }
    }
    
    void resetPointer(final short depth, final short column) {
        this.fPointerInfo[depth][column] = 0;
    }
    
    short storePointerForADepth(final short elementPointer) {
        final short depth = (short)this.fElementStack.fDepth;
        for (short i = 0; i < 4; ++i) {
            if (this.canStore(depth, i)) {
                this.fPointerInfo[depth][i] = elementPointer;
                return i;
            }
        }
        return -1;
    }
    
    boolean canStore(final short depth, final short column) {
        return this.fPointerInfo[depth][column] == 0;
    }
    
    short getElementPointer(final short depth, final short column) {
        return this.fPointerInfo[depth][column];
    }
    
    boolean skipFromTheBuffer(final String rawname) throws IOException {
        if (!this.fEntityScanner.skipString(rawname)) {
            return false;
        }
        final char c = (char)this.fEntityScanner.peekChar();
        if (c == ' ' || c == '/' || c == '>') {
            this.fElementRawname = rawname;
            return true;
        }
        return false;
    }
    
    boolean skipQElement(final String rawname) throws IOException {
        final int c = this.fEntityScanner.getChar(rawname.length());
        return !XMLChar.isName(c) && this.fEntityScanner.skipString(rawname);
    }
    
    protected boolean skipElement() throws IOException {
        if (!this.fShouldSkip) {
            return false;
        }
        if (this.fLastPointerLocation != 0) {
            final String rawname = this.fElementArray[this.fLastPointerLocation + 1];
            if (rawname != null && this.skipFromTheBuffer(rawname)) {
                ++this.fLastPointerLocation;
                return true;
            }
            this.fLastPointerLocation = 0;
        }
        return this.fShouldSkip && this.skipElement((short)0);
    }
    
    boolean skipElement(final short column) throws IOException {
        final short depth = (short)this.fElementStack.fDepth;
        if (depth > 5) {
            return this.fShouldSkip = false;
        }
        for (short i = column; i < 4; ++i) {
            final short pointer = this.getElementPointer(depth, i);
            if (pointer == 0) {
                return this.fShouldSkip = false;
            }
            if (this.fElementArray[pointer] != null && this.skipFromTheBuffer(this.fElementArray[pointer])) {
                this.fLastPointerLocation = pointer;
                return this.fShouldSkip = true;
            }
        }
        return this.fShouldSkip = false;
    }
    
    protected boolean scanStartElement() throws IOException, XNIException {
        if (this.fSkip && !this.fAdd) {
            final QName name = this.fElementStack.getNext();
            this.fSkip = this.fEntityScanner.skipString(name.rawname);
            if (this.fSkip) {
                this.fElementStack.push();
                this.fElementQName = name;
            }
            else {
                this.fElementStack.reposition();
            }
        }
        if (!this.fSkip || this.fAdd) {
            this.fElementQName = this.fElementStack.nextElement();
            if (this.fNamespaces) {
                this.fEntityScanner.scanQName(this.fElementQName, NameType.ELEMENTSTART);
            }
            else {
                final String name2 = this.fEntityScanner.scanName(NameType.ELEMENTSTART);
                this.fElementQName.setValues(null, name2, name2, null);
            }
        }
        if (this.fAdd) {
            this.fElementStack.matchElement(this.fElementQName);
        }
        this.fCurrentElement = this.fElementQName;
        final String rawname = this.fElementQName.rawname;
        this.fEmptyElement = false;
        this.fAttributes.removeAllAttributes();
        this.checkDepth(rawname);
        if (!this.seekCloseOfStartTag()) {
            this.fReadingAttributes = true;
            this.fAttributeCacheUsedCount = 0;
            this.fStringBufferIndex = 0;
            this.fAddDefaultAttr = true;
            do {
                this.scanAttribute(this.fAttributes);
                if (this.fSecurityManager != null && !this.fSecurityManager.isNoLimit(this.fElementAttributeLimit) && this.fAttributes.getLength() > this.fElementAttributeLimit) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "ElementAttributeLimit", new Object[] { rawname, this.fElementAttributeLimit }, (short)2);
                }
            } while (!this.seekCloseOfStartTag());
            this.fReadingAttributes = false;
        }
        if (this.fEmptyElement) {
            --this.fMarkupDepth;
            if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
                this.reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname });
            }
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
            }
            this.fElementStack.popElement();
        }
        else {
            if (this.dtdGrammarUtil != null) {
                this.dtdGrammarUtil.startElement(this.fElementQName, this.fAttributes);
            }
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
            }
        }
        return this.fEmptyElement;
    }
    
    protected boolean seekCloseOfStartTag() throws IOException, XNIException {
        final boolean sawSpace = this.fEntityScanner.skipSpaces();
        final int c = this.fEntityScanner.peekChar();
        if (c == 62) {
            this.fEntityScanner.scanChar(null);
            return true;
        }
        if (c == 47) {
            this.fEntityScanner.scanChar(null);
            if (!this.fEntityScanner.skipChar(62, NameType.ELEMENTEND)) {
                this.reportFatalError("ElementUnterminated", new Object[] { this.fElementQName.rawname });
            }
            return this.fEmptyElement = true;
        }
        if ((!this.isValidNameStartChar(c) || !sawSpace) && (!this.isValidNameStartHighSurrogate(c) || !sawSpace)) {
            this.reportFatalError("ElementUnterminated", new Object[] { this.fElementQName.rawname });
        }
        return false;
    }
    
    public boolean hasAttributes() {
        return this.fAttributes.getLength() > 0;
    }
    
    public XMLAttributesIteratorImpl getAttributeIterator() {
        if (this.dtdGrammarUtil != null && this.fAddDefaultAttr) {
            this.dtdGrammarUtil.addDTDDefaultAttrs(this.fElementQName, this.fAttributes);
            this.fAddDefaultAttr = false;
        }
        return this.fAttributes;
    }
    
    public boolean standaloneSet() {
        return this.fStandaloneSet;
    }
    
    public boolean isStandAlone() {
        return this.fStandalone;
    }
    
    protected void scanAttribute(final XMLAttributes attributes) throws IOException, XNIException {
        if (this.fNamespaces) {
            this.fEntityScanner.scanQName(this.fAttributeQName, NameType.ATTRIBUTENAME);
        }
        else {
            final String name = this.fEntityScanner.scanName(NameType.ATTRIBUTENAME);
            this.fAttributeQName.setValues(null, name, name, null);
        }
        this.fEntityScanner.skipSpaces();
        if (!this.fEntityScanner.skipChar(61, NameType.ATTRIBUTE)) {
            this.reportFatalError("EqRequiredInAttribute", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
        }
        this.fEntityScanner.skipSpaces();
        int attIndex = 0;
        final boolean isVC = this.fHasExternalDTD && !this.fStandalone;
        final XMLString tmpStr = this.getString();
        this.scanAttributeValue(tmpStr, this.fTempString2, this.fAttributeQName.rawname, attributes, attIndex, isVC, this.fCurrentElement.rawname, false);
        final int oldLen = attributes.getLength();
        attIndex = attributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
        if (oldLen == attributes.getLength()) {
            this.reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
        }
        attributes.setValue(attIndex, null, tmpStr);
        attributes.setSpecified(attIndex, true);
    }
    
    protected int scanContent(final XMLStringBuffer content) throws IOException, XNIException {
        this.fTempString.length = 0;
        int c = this.fEntityScanner.scanContent(this.fTempString);
        content.append(this.fTempString);
        this.fTempString.length = 0;
        if (c == 13) {
            this.fEntityScanner.scanChar(null);
            content.append((char)c);
            c = -1;
        }
        else if (c == 93) {
            content.append((char)this.fEntityScanner.scanChar(null));
            this.fInScanContent = true;
            if (this.fEntityScanner.skipChar(93, null)) {
                content.append(']');
                while (this.fEntityScanner.skipChar(93, null)) {
                    content.append(']');
                }
                if (this.fEntityScanner.skipChar(62, null)) {
                    this.reportFatalError("CDEndInContent", null);
                }
            }
            this.fInScanContent = false;
            c = -1;
        }
        if (this.fDocumentHandler == null || content.length > 0) {}
        return c;
    }
    
    protected boolean scanCDATASection(final XMLStringBuffer contentBuffer, final boolean complete) throws IOException, XNIException {
        if (this.fDocumentHandler != null) {}
        while (this.fEntityScanner.scanData("]]>", contentBuffer)) {
            final int c = this.fEntityScanner.peekChar();
            if (c != -1 && this.isInvalidLiteral(c)) {
                if (XMLChar.isHighSurrogate(c)) {
                    this.scanSurrogates(contentBuffer);
                }
                else {
                    this.reportFatalError("InvalidCharInCDSect", new Object[] { Integer.toString(c, 16) });
                    this.fEntityScanner.scanChar(null);
                }
            }
            if (this.fDocumentHandler != null) {}
        }
        --this.fMarkupDepth;
        if (this.fDocumentHandler == null || contentBuffer.length > 0) {}
        if (this.fDocumentHandler != null) {}
        return true;
    }
    
    protected int scanEndElement() throws IOException, XNIException {
        final QName endElementName = this.fElementStack.popElement();
        final String rawname = endElementName.rawname;
        if (!this.fEntityScanner.skipString(endElementName.rawname)) {
            this.reportFatalError("ETagRequired", new Object[] { rawname });
        }
        this.fEntityScanner.skipSpaces();
        if (!this.fEntityScanner.skipChar(62, NameType.ELEMENTEND)) {
            this.reportFatalError("ETagUnterminated", new Object[] { rawname });
        }
        --this.fMarkupDepth;
        --this.fMarkupDepth;
        if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
            this.reportFatalError("ElementEntityMismatch", new Object[] { rawname });
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endElement(endElementName, null);
        }
        if (this.dtdGrammarUtil != null) {
            this.dtdGrammarUtil.endElement(endElementName);
        }
        return this.fMarkupDepth;
    }
    
    protected void scanCharReference() throws IOException, XNIException {
        this.fStringBuffer2.clear();
        final int ch = this.scanCharReferenceValue(this.fStringBuffer2, null);
        --this.fMarkupDepth;
        if (ch != -1 && this.fDocumentHandler != null) {
            if (this.fNotifyCharRefs) {
                this.fDocumentHandler.startGeneralEntity(this.fCharRefLiteral, null, null, null);
            }
            Augmentations augs = null;
            if (this.fValidation && ch <= 32) {
                if (this.fTempAugmentations != null) {
                    this.fTempAugmentations.removeAllItems();
                }
                else {
                    this.fTempAugmentations = new AugmentationsImpl();
                }
                augs = this.fTempAugmentations;
                augs.putItem("CHAR_REF_PROBABLE_WS", Boolean.TRUE);
            }
            if (this.fNotifyCharRefs) {
                this.fDocumentHandler.endGeneralEntity(this.fCharRefLiteral, null);
            }
        }
    }
    
    protected void scanEntityReference(final XMLStringBuffer content) throws IOException, XNIException {
        final String name = this.fEntityScanner.scanName(NameType.REFERENCE);
        if (name == null) {
            this.reportFatalError("NameRequiredInReference", null);
            return;
        }
        if (!this.fEntityScanner.skipChar(59, NameType.REFERENCE)) {
            this.reportFatalError("SemicolonRequiredInReference", new Object[] { name });
        }
        if (this.fEntityStore.isUnparsedEntity(name)) {
            this.reportFatalError("ReferenceToUnparsedEntity", new Object[] { name });
        }
        --this.fMarkupDepth;
        if ((this.fCurrentEntityName = name) == XMLDocumentFragmentScannerImpl.fAmpSymbol) {
            this.handleCharacter('&', XMLDocumentFragmentScannerImpl.fAmpSymbol, content);
            this.fScannerState = 41;
            return;
        }
        if (name == XMLDocumentFragmentScannerImpl.fLtSymbol) {
            this.handleCharacter('<', XMLDocumentFragmentScannerImpl.fLtSymbol, content);
            this.fScannerState = 41;
            return;
        }
        if (name == XMLDocumentFragmentScannerImpl.fGtSymbol) {
            this.handleCharacter('>', XMLDocumentFragmentScannerImpl.fGtSymbol, content);
            this.fScannerState = 41;
            return;
        }
        if (name == XMLDocumentFragmentScannerImpl.fQuotSymbol) {
            this.handleCharacter('\"', XMLDocumentFragmentScannerImpl.fQuotSymbol, content);
            this.fScannerState = 41;
            return;
        }
        if (name == XMLDocumentFragmentScannerImpl.fAposSymbol) {
            this.handleCharacter('\'', XMLDocumentFragmentScannerImpl.fAposSymbol, content);
            this.fScannerState = 41;
            return;
        }
        final boolean isEE = this.fEntityStore.isExternalEntity(name);
        if ((isEE && !this.fSupportExternalEntities) || (!isEE && !this.fReplaceEntityReferences) || this.foundBuiltInRefs) {
            this.fScannerState = 28;
            return;
        }
        if (!this.fEntityStore.isDeclaredEntity(name)) {
            if (!this.fSupportDTD && this.fReplaceEntityReferences) {
                this.reportFatalError("EntityNotDeclared", new Object[] { name });
                return;
            }
            if (this.fHasExternalDTD && !this.fStandalone) {
                if (this.fValidation) {
                    this.fErrorReporter.reportError(this.fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { name }, (short)1);
                }
            }
            else {
                this.reportFatalError("EntityNotDeclared", new Object[] { name });
            }
        }
        this.fEntityManager.startEntity(true, name, false);
    }
    
    void checkDepth(final String elementName) {
        this.fLimitAnalyzer.addValue(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT, elementName, this.fElementStack.fDepth);
        if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT, this.fLimitAnalyzer)) {
            this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
            this.reportFatalError("MaxElementDepthLimit", new Object[] { elementName, this.fLimitAnalyzer.getTotalValue(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT), this.fSecurityManager.getLimit(XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT), "maxElementDepth" });
        }
    }
    
    private void handleCharacter(final char c, final String entity, final XMLStringBuffer content) throws XNIException {
        this.foundBuiltInRefs = true;
        this.checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, 1);
        content.append(c);
        if (this.fDocumentHandler != null) {
            this.fSingleChar[0] = c;
            if (this.fNotifyBuiltInRefs) {
                this.fDocumentHandler.startGeneralEntity(entity, null, null, null);
            }
            this.fTempString.setValues(this.fSingleChar, 0, 1);
            if (this.fNotifyBuiltInRefs) {
                this.fDocumentHandler.endGeneralEntity(entity, null);
            }
        }
    }
    
    protected final void setScannerState(final int state) {
        this.fScannerState = state;
    }
    
    protected final void setDriver(final Driver driver) {
        this.fDriver = driver;
    }
    
    protected String getScannerStateName(final int state) {
        switch (state) {
            case 24: {
                return "SCANNER_STATE_DOCTYPE";
            }
            case 26: {
                return "SCANNER_STATE_ROOT_ELEMENT";
            }
            case 21: {
                return "SCANNER_STATE_START_OF_MARKUP";
            }
            case 27: {
                return "SCANNER_STATE_COMMENT";
            }
            case 23: {
                return "SCANNER_STATE_PI";
            }
            case 22: {
                return "SCANNER_STATE_CONTENT";
            }
            case 28: {
                return "SCANNER_STATE_REFERENCE";
            }
            case 33: {
                return "SCANNER_STATE_END_OF_INPUT";
            }
            case 34: {
                return "SCANNER_STATE_TERMINATED";
            }
            case 35: {
                return "SCANNER_STATE_CDATA";
            }
            case 36: {
                return "SCANNER_STATE_TEXT_DECL";
            }
            case 29: {
                return "SCANNER_STATE_ATTRIBUTE";
            }
            case 30: {
                return "SCANNER_STATE_ATTRIBUTE_VALUE";
            }
            case 38: {
                return "SCANNER_STATE_START_ELEMENT_TAG";
            }
            case 39: {
                return "SCANNER_STATE_END_ELEMENT_TAG";
            }
            case 37: {
                return "SCANNER_STATE_CHARACTER_DATA";
            }
            default: {
                return "??? (" + state + ')';
            }
        }
    }
    
    public String getEntityName() {
        return this.fCurrentEntityName;
    }
    
    public String getDriverName(final Driver driver) {
        return "null";
    }
    
    String checkAccess(final String systemId, final String allowedProtocols) throws IOException {
        final String baseSystemId = this.fEntityScanner.getBaseSystemId();
        final String expandedSystemId = XMLEntityManager.expandSystemId(systemId, baseSystemId, this.fStrictURI);
        return SecuritySupport.checkAccess(expandedSystemId, allowedProtocols, "all");
    }
    
    static void pr(final String str) {
        System.out.println(str);
    }
    
    protected XMLString getString() {
        if (this.fAttributeCacheUsedCount < this.initialCacheCount || this.fAttributeCacheUsedCount < this.attributeValueCache.size()) {
            return this.attributeValueCache.get(this.fAttributeCacheUsedCount++);
        }
        final XMLString str = new XMLString();
        ++this.fAttributeCacheUsedCount;
        this.attributeValueCache.add(str);
        return str;
    }
    
    @Override
    public void refresh() {
        this.refresh(0);
    }
    
    @Override
    public void refresh(final int refreshPosition) {
        if (this.fReadingAttributes) {
            this.fAttributes.refresh();
        }
        if (this.fScannerState == 37) {
            this.fContentBuffer.append(this.fTempString);
            this.fTempString.length = 0;
            this.fUsebuffer = true;
        }
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/validation", "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://apache.org/xml/features/scanner/notify-char-refs", "report-cdata-event" };
        FEATURE_DEFAULTS = new Boolean[] { Boolean.TRUE, null, Boolean.FALSE, Boolean.FALSE, Boolean.TRUE };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager" };
        PROPERTY_DEFAULTS = new Object[] { null, null, null, null };
        cdata = new char[] { '[', 'C', 'D', 'A', 'T', 'A', '[' };
        xmlDecl = new char[] { '<', '?', 'x', 'm', 'l' };
    }
    
    protected static final class Element
    {
        public QName qname;
        public char[] fRawname;
        public Element next;
        
        public Element(final QName qname, final Element next) {
            this.qname.setValues(qname);
            this.fRawname = qname.rawname.toCharArray();
            this.next = next;
        }
    }
    
    protected class ElementStack2
    {
        protected QName[] fQName;
        protected int fDepth;
        protected int fCount;
        protected int fPosition;
        protected int fMark;
        protected int fLastDepth;
        
        public ElementStack2() {
            this.fQName = new QName[20];
            for (int i = 0; i < this.fQName.length; ++i) {
                this.fQName[i] = new QName();
            }
            final int n = 1;
            this.fPosition = n;
            this.fMark = n;
        }
        
        public void resize() {
            final int oldLength = this.fQName.length;
            final QName[] tmp = new QName[oldLength * 2];
            System.arraycopy(this.fQName, 0, tmp, 0, oldLength);
            this.fQName = tmp;
            for (int i = oldLength; i < this.fQName.length; ++i) {
                this.fQName[i] = new QName();
            }
        }
        
        public boolean matchElement(final QName element) {
            boolean match = false;
            if (this.fLastDepth > this.fDepth && this.fDepth <= 2) {
                if (element.rawname == this.fQName[this.fDepth].rawname) {
                    XMLDocumentFragmentScannerImpl.this.fAdd = false;
                    this.fMark = this.fDepth - 1;
                    this.fPosition = this.fMark + 1;
                    match = true;
                    --this.fCount;
                }
                else {
                    XMLDocumentFragmentScannerImpl.this.fAdd = true;
                }
            }
            this.fLastDepth = this.fDepth++;
            return match;
        }
        
        public QName nextElement() {
            if (this.fCount == this.fQName.length) {
                XMLDocumentFragmentScannerImpl.this.fShouldSkip = false;
                XMLDocumentFragmentScannerImpl.this.fAdd = false;
                final QName[] fqName = this.fQName;
                final int fCount = this.fCount - 1;
                this.fCount = fCount;
                return fqName[fCount];
            }
            return this.fQName[this.fCount++];
        }
        
        public QName getNext() {
            if (this.fPosition == this.fCount) {
                this.fPosition = this.fMark;
            }
            return this.fQName[this.fPosition++];
        }
        
        public int popElement() {
            return this.fDepth--;
        }
        
        public void clear() {
            this.fLastDepth = 0;
            this.fDepth = 0;
            this.fCount = 0;
            final int n = 1;
            this.fMark = n;
            this.fPosition = n;
        }
    }
    
    protected class ElementStack
    {
        protected QName[] fElements;
        protected int[] fInt;
        protected int fDepth;
        protected int fCount;
        protected int fPosition;
        protected int fMark;
        protected int fLastDepth;
        
        public ElementStack() {
            this.fInt = new int[20];
            this.fElements = new QName[20];
            for (int i = 0; i < this.fElements.length; ++i) {
                this.fElements[i] = new QName();
            }
        }
        
        public QName pushElement(final QName element) {
            if (this.fDepth == this.fElements.length) {
                final QName[] array = new QName[this.fElements.length * 2];
                System.arraycopy(this.fElements, 0, array, 0, this.fDepth);
                this.fElements = array;
                for (int i = this.fDepth; i < this.fElements.length; ++i) {
                    this.fElements[i] = new QName();
                }
            }
            this.fElements[this.fDepth].setValues(element);
            return this.fElements[this.fDepth++];
        }
        
        public QName getNext() {
            if (this.fPosition == this.fCount) {
                this.fPosition = this.fMark;
            }
            return this.fElements[this.fPosition];
        }
        
        public void push() {
            this.fInt[++this.fDepth] = this.fPosition++;
        }
        
        public boolean matchElement(final QName element) {
            boolean match = false;
            if (this.fLastDepth > this.fDepth && this.fDepth <= 3) {
                if (element.rawname == this.fElements[this.fDepth - 1].rawname) {
                    XMLDocumentFragmentScannerImpl.this.fAdd = false;
                    this.fMark = this.fDepth - 1;
                    this.fPosition = this.fMark;
                    match = true;
                    --this.fCount;
                }
                else {
                    XMLDocumentFragmentScannerImpl.this.fAdd = true;
                }
            }
            if (match) {
                this.fInt[this.fDepth] = this.fPosition++;
            }
            else {
                this.fInt[this.fDepth] = this.fCount - 1;
            }
            if (this.fCount == this.fElements.length) {
                XMLDocumentFragmentScannerImpl.this.fSkip = false;
                XMLDocumentFragmentScannerImpl.this.fAdd = false;
                this.reposition();
                return false;
            }
            this.fLastDepth = this.fDepth;
            return match;
        }
        
        public QName nextElement() {
            if (XMLDocumentFragmentScannerImpl.this.fSkip) {
                ++this.fDepth;
                return this.fElements[this.fCount++];
            }
            if (this.fDepth == this.fElements.length) {
                final QName[] array = new QName[this.fElements.length * 2];
                System.arraycopy(this.fElements, 0, array, 0, this.fDepth);
                this.fElements = array;
                for (int i = this.fDepth; i < this.fElements.length; ++i) {
                    this.fElements[i] = new QName();
                }
            }
            return this.fElements[this.fDepth++];
        }
        
        public QName popElement() {
            if (XMLDocumentFragmentScannerImpl.this.fSkip || XMLDocumentFragmentScannerImpl.this.fAdd) {
                return this.fElements[this.fInt[this.fDepth--]];
            }
            final QName[] fElements = this.fElements;
            final int fDepth = this.fDepth - 1;
            this.fDepth = fDepth;
            return fElements[fDepth];
        }
        
        public void reposition() {
            for (int i = 2; i <= this.fDepth; ++i) {
                this.fElements[i - 1] = this.fElements[this.fInt[i]];
            }
        }
        
        public void clear() {
            this.fDepth = 0;
            this.fLastDepth = 0;
            this.fCount = 0;
            final int n = 1;
            this.fMark = n;
            this.fPosition = n;
        }
        
        public QName getLastPoppedElement() {
            return this.fElements[this.fDepth];
        }
    }
    
    protected class FragmentContentDriver implements Driver
    {
        private void startOfMarkup() throws IOException {
            final XMLDocumentFragmentScannerImpl this$0 = XMLDocumentFragmentScannerImpl.this;
            ++this$0.fMarkupDepth;
            final int ch = XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar();
            if (XMLDocumentFragmentScannerImpl.this.isValidNameStartChar(ch) || XMLDocumentFragmentScannerImpl.this.isValidNameStartHighSurrogate(ch)) {
                XMLDocumentFragmentScannerImpl.this.setScannerState(38);
            }
            else {
                switch (ch) {
                    case 63: {
                        XMLDocumentFragmentScannerImpl.this.setScannerState(23);
                        XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(ch, null);
                        break;
                    }
                    case 33: {
                        XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(ch, null);
                        if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(45, null)) {
                            if (!XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(45, NameType.COMMENT)) {
                                XMLDocumentFragmentScannerImpl.this.reportFatalError("InvalidCommentStart", null);
                            }
                            XMLDocumentFragmentScannerImpl.this.setScannerState(27);
                            break;
                        }
                        if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipString(XMLDocumentFragmentScannerImpl.cdata)) {
                            XMLDocumentFragmentScannerImpl.this.setScannerState(35);
                            break;
                        }
                        if (!this.scanForDoctypeHook()) {
                            XMLDocumentFragmentScannerImpl.this.reportFatalError("MarkupNotRecognizedInContent", null);
                            break;
                        }
                        break;
                    }
                    case 47: {
                        XMLDocumentFragmentScannerImpl.this.setScannerState(39);
                        XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(ch, NameType.ELEMENTEND);
                        break;
                    }
                    default: {
                        XMLDocumentFragmentScannerImpl.this.reportFatalError("MarkupNotRecognizedInContent", null);
                        break;
                    }
                }
            }
        }
        
        private void startOfContent() throws IOException {
            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(60, null)) {
                XMLDocumentFragmentScannerImpl.this.setScannerState(21);
            }
            else if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(38, NameType.REFERENCE)) {
                XMLDocumentFragmentScannerImpl.this.setScannerState(28);
            }
            else {
                XMLDocumentFragmentScannerImpl.this.setScannerState(37);
            }
        }
        
        public void decideSubState() throws IOException {
            while (XMLDocumentFragmentScannerImpl.this.fScannerState == 22 || XMLDocumentFragmentScannerImpl.this.fScannerState == 21) {
                switch (XMLDocumentFragmentScannerImpl.this.fScannerState) {
                    case 22: {
                        this.startOfContent();
                        continue;
                    }
                    case 21: {
                        this.startOfMarkup();
                        continue;
                    }
                }
            }
        }
        
        @Override
        public int next() throws IOException, XNIException {
            try {
                Label_2250: {
                    Label_2222: {
                        Block_55: {
                            Block_53: {
                                Label_1613: {
                                    Label_1447: {
                                        Label_1429: {
                                            Label_1349: {
                                                Label_1314: {
                                                Label_0000:
                                                    while (true) {
                                                    Label_1293_Outer:
                                                        while (true) {
                                                            switch (XMLDocumentFragmentScannerImpl.this.fScannerState) {
                                                                case 22: {
                                                                    final int ch = XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar();
                                                                    if (ch == 60) {
                                                                        XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(null);
                                                                        XMLDocumentFragmentScannerImpl.this.setScannerState(21);
                                                                    }
                                                                    if (ch == 38) {
                                                                        XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(NameType.REFERENCE);
                                                                        XMLDocumentFragmentScannerImpl.this.setScannerState(28);
                                                                        break;
                                                                    }
                                                                    XMLDocumentFragmentScannerImpl.this.setScannerState(37);
                                                                    break;
                                                                }
                                                                case 21: {
                                                                    this.startOfMarkup();
                                                                    break;
                                                                }
                                                            }
                                                            if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                                                                XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
                                                                if (XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData) {
                                                                    if (XMLDocumentFragmentScannerImpl.this.fScannerState != 35 && XMLDocumentFragmentScannerImpl.this.fScannerState != 28 && XMLDocumentFragmentScannerImpl.this.fScannerState != 37) {
                                                                        XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
                                                                        return 4;
                                                                    }
                                                                }
                                                                else if ((XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference) && XMLDocumentFragmentScannerImpl.this.fScannerState != 35 && XMLDocumentFragmentScannerImpl.this.fScannerState != 28 && XMLDocumentFragmentScannerImpl.this.fScannerState != 37) {
                                                                    XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = false;
                                                                    XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = false;
                                                                    return 4;
                                                                }
                                                            }
                                                            switch (XMLDocumentFragmentScannerImpl.this.fScannerState) {
                                                                case 7: {
                                                                    return 7;
                                                                }
                                                                case 38: {
                                                                    XMLDocumentFragmentScannerImpl.this.fEmptyElement = XMLDocumentFragmentScannerImpl.this.scanStartElement();
                                                                    if (XMLDocumentFragmentScannerImpl.this.fEmptyElement) {
                                                                        XMLDocumentFragmentScannerImpl.this.setScannerState(39);
                                                                    }
                                                                    else {
                                                                        XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                                                                    }
                                                                    return 1;
                                                                }
                                                                case 37: {
                                                                    XMLDocumentFragmentScannerImpl.this.fUsebuffer = (XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData);
                                                                    if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce && (XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData)) {
                                                                        XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = false;
                                                                        XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = false;
                                                                        XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = true;
                                                                        XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
                                                                    }
                                                                    else {
                                                                        XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
                                                                    }
                                                                    XMLDocumentFragmentScannerImpl.this.fTempString.length = 0;
                                                                    int c = XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanContent(XMLDocumentFragmentScannerImpl.this.fTempString);
                                                                    if (!XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(60, null)) {
                                                                        XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
                                                                        XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(XMLDocumentFragmentScannerImpl.this.fTempString);
                                                                        XMLDocumentFragmentScannerImpl.this.fTempString.length = 0;
                                                                        if (c == 13) {
                                                                            XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(null);
                                                                            XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
                                                                            XMLDocumentFragmentScannerImpl.this.fContentBuffer.append((char)c);
                                                                            c = -1;
                                                                        }
                                                                        else if (c == 93) {
                                                                            XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
                                                                            XMLDocumentFragmentScannerImpl.this.fContentBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(null));
                                                                            XMLDocumentFragmentScannerImpl.this.fInScanContent = true;
                                                                            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(93, null)) {
                                                                                XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(']');
                                                                                while (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(93, null)) {
                                                                                    XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(']');
                                                                                }
                                                                                if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(62, null)) {
                                                                                    XMLDocumentFragmentScannerImpl.this.reportFatalError("CDEndInContent", null);
                                                                                }
                                                                            }
                                                                            c = -1;
                                                                            XMLDocumentFragmentScannerImpl.this.fInScanContent = false;
                                                                        }
                                                                        while (true) {
                                                                            while (c != 60) {
                                                                                if (c == 38) {
                                                                                    XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(NameType.REFERENCE);
                                                                                    XMLDocumentFragmentScannerImpl.this.setScannerState(28);
                                                                                }
                                                                                else if (c != -1 && XMLDocumentFragmentScannerImpl.this.isInvalidLiteral(c)) {
                                                                                    if (XMLChar.isHighSurrogate(c)) {
                                                                                        XMLDocumentFragmentScannerImpl.this.scanSurrogates(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
                                                                                        XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                                                                                    }
                                                                                    else {
                                                                                        XMLDocumentFragmentScannerImpl.this.reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(c, 16) });
                                                                                        XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(null);
                                                                                    }
                                                                                }
                                                                                else {
                                                                                    c = XMLDocumentFragmentScannerImpl.this.scanContent(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
                                                                                    if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                                                                                        continue Label_1293_Outer;
                                                                                    }
                                                                                    XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                                                                                }
                                                                                if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                                                                                    XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = true;
                                                                                    continue Label_0000;
                                                                                }
                                                                                break Label_1314;
                                                                            }
                                                                            XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(null);
                                                                            XMLDocumentFragmentScannerImpl.this.setScannerState(21);
                                                                            continue;
                                                                        }
                                                                    }
                                                                    if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(47, NameType.ELEMENTEND)) {
                                                                        final XMLDocumentFragmentScannerImpl this$0 = XMLDocumentFragmentScannerImpl.this;
                                                                        ++this$0.fMarkupDepth;
                                                                        XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
                                                                        XMLDocumentFragmentScannerImpl.this.setScannerState(39);
                                                                        break Label_0000;
                                                                    }
                                                                    if (XMLChar.isNameStart(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                                                                        final XMLDocumentFragmentScannerImpl this$2 = XMLDocumentFragmentScannerImpl.this;
                                                                        ++this$2.fMarkupDepth;
                                                                        XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
                                                                        XMLDocumentFragmentScannerImpl.this.setScannerState(38);
                                                                        break Label_0000;
                                                                    }
                                                                    XMLDocumentFragmentScannerImpl.this.setScannerState(21);
                                                                    if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                                                                        XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
                                                                        XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = true;
                                                                        XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(XMLDocumentFragmentScannerImpl.this.fTempString);
                                                                        XMLDocumentFragmentScannerImpl.this.fTempString.length = 0;
                                                                        continue;
                                                                    }
                                                                    break Label_0000;
                                                                }
                                                                case 39: {
                                                                    break Label_1349;
                                                                }
                                                                case 27: {
                                                                    break Label_1429;
                                                                }
                                                                case 23: {
                                                                    break Label_1447;
                                                                }
                                                                case 35: {
                                                                    if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce && (XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData)) {
                                                                        XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = true;
                                                                        XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = false;
                                                                        XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
                                                                    }
                                                                    else {
                                                                        XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
                                                                    }
                                                                    XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
                                                                    XMLDocumentFragmentScannerImpl.this.scanCDATASection(XMLDocumentFragmentScannerImpl.this.fContentBuffer, true);
                                                                    XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                                                                    if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                                                                        XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = true;
                                                                        continue;
                                                                    }
                                                                    break Label_1613;
                                                                }
                                                                case 28: {
                                                                    final XMLDocumentFragmentScannerImpl this$3 = XMLDocumentFragmentScannerImpl.this;
                                                                    ++this$3.fMarkupDepth;
                                                                    XMLDocumentFragmentScannerImpl.this.foundBuiltInRefs = false;
                                                                    if (XMLDocumentFragmentScannerImpl.this.fIsCoalesce && (XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData || XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData)) {
                                                                        XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = true;
                                                                        XMLDocumentFragmentScannerImpl.this.fLastSectionWasCData = false;
                                                                        XMLDocumentFragmentScannerImpl.this.fLastSectionWasCharacterData = false;
                                                                    }
                                                                    else {
                                                                        XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
                                                                    }
                                                                    XMLDocumentFragmentScannerImpl.this.fUsebuffer = true;
                                                                    if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(35, NameType.REFERENCE)) {
                                                                        XMLDocumentFragmentScannerImpl.this.scanCharReferenceValue(XMLDocumentFragmentScannerImpl.this.fContentBuffer, null);
                                                                        final XMLDocumentFragmentScannerImpl this$4 = XMLDocumentFragmentScannerImpl.this;
                                                                        --this$4.fMarkupDepth;
                                                                        if (!XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                                                                            break Block_53;
                                                                        }
                                                                    }
                                                                    else {
                                                                        XMLDocumentFragmentScannerImpl.this.scanEntityReference(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
                                                                        if (XMLDocumentFragmentScannerImpl.this.fScannerState == 41 && !XMLDocumentFragmentScannerImpl.this.fIsCoalesce) {
                                                                            break Block_55;
                                                                        }
                                                                        if (XMLDocumentFragmentScannerImpl.this.fScannerState == 36) {
                                                                            XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = true;
                                                                            continue;
                                                                        }
                                                                        if (XMLDocumentFragmentScannerImpl.this.fScannerState == 28) {
                                                                            XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                                                                            if (XMLDocumentFragmentScannerImpl.this.fReplaceEntityReferences && XMLDocumentFragmentScannerImpl.this.fEntityStore.isDeclaredEntity(XMLDocumentFragmentScannerImpl.this.fCurrentEntityName)) {
                                                                                continue;
                                                                            }
                                                                            return 9;
                                                                        }
                                                                    }
                                                                    XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                                                                    XMLDocumentFragmentScannerImpl.this.fLastSectionWasEntityReference = true;
                                                                    continue;
                                                                }
                                                                case 36: {
                                                                    if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipString("<?xml")) {
                                                                        final XMLDocumentFragmentScannerImpl this$5 = XMLDocumentFragmentScannerImpl.this;
                                                                        ++this$5.fMarkupDepth;
                                                                        if (XMLDocumentFragmentScannerImpl.this.isValidNameChar(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                                                                            XMLDocumentFragmentScannerImpl.this.fStringBuffer.clear();
                                                                            XMLDocumentFragmentScannerImpl.this.fStringBuffer.append("xml");
                                                                            if (XMLDocumentFragmentScannerImpl.this.fNamespaces) {
                                                                                while (XMLDocumentFragmentScannerImpl.this.isValidNCName(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                                                                                    XMLDocumentFragmentScannerImpl.this.fStringBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(null));
                                                                                }
                                                                            }
                                                                            else {
                                                                                while (XMLDocumentFragmentScannerImpl.this.isValidNameChar(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                                                                                    XMLDocumentFragmentScannerImpl.this.fStringBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar(null));
                                                                                }
                                                                            }
                                                                            final String target = XMLDocumentFragmentScannerImpl.this.fSymbolTable.addSymbol(XMLDocumentFragmentScannerImpl.this.fStringBuffer.ch, XMLDocumentFragmentScannerImpl.this.fStringBuffer.offset, XMLDocumentFragmentScannerImpl.this.fStringBuffer.length);
                                                                            XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
                                                                            XMLDocumentFragmentScannerImpl.this.scanPIData(target, XMLDocumentFragmentScannerImpl.this.fContentBuffer);
                                                                        }
                                                                        else {
                                                                            XMLDocumentFragmentScannerImpl.this.scanXMLDeclOrTextDecl(true);
                                                                        }
                                                                    }
                                                                    XMLDocumentFragmentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
                                                                    XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                                                                    continue;
                                                                }
                                                                case 26: {
                                                                    break Label_2222;
                                                                }
                                                                case 40: {
                                                                    break Label_2250;
                                                                }
                                                                default: {
                                                                    throw new XNIException("Scanner State " + XMLDocumentFragmentScannerImpl.this.fScannerState + " not Recognized ");
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    }
                                                    if (XMLDocumentFragmentScannerImpl.this.fUsebuffer) {
                                                        XMLDocumentFragmentScannerImpl.this.fContentBuffer.append(XMLDocumentFragmentScannerImpl.this.fTempString);
                                                        XMLDocumentFragmentScannerImpl.this.fTempString.length = 0;
                                                    }
                                                    if (XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil != null && XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil.isIgnorableWhiteSpace(XMLDocumentFragmentScannerImpl.this.fContentBuffer)) {
                                                        return 6;
                                                    }
                                                    return 4;
                                                }
                                                if (XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil != null && XMLDocumentFragmentScannerImpl.this.dtdGrammarUtil.isIgnorableWhiteSpace(XMLDocumentFragmentScannerImpl.this.fContentBuffer)) {
                                                    return 6;
                                                }
                                                return 4;
                                            }
                                            if (XMLDocumentFragmentScannerImpl.this.fEmptyElement) {
                                                XMLDocumentFragmentScannerImpl.this.fEmptyElement = false;
                                                XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                                                return (XMLDocumentFragmentScannerImpl.this.fMarkupDepth == 0 && this.elementDepthIsZeroHook()) ? 2 : 2;
                                            }
                                            if (XMLDocumentFragmentScannerImpl.this.scanEndElement() == 0 && this.elementDepthIsZeroHook()) {
                                                return 2;
                                            }
                                            XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                                            return 2;
                                        }
                                        XMLDocumentFragmentScannerImpl.this.scanComment();
                                        XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                                        return 5;
                                    }
                                    XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
                                    XMLDocumentFragmentScannerImpl.this.scanPI(XMLDocumentFragmentScannerImpl.this.fContentBuffer);
                                    XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                                    return 3;
                                }
                                if (XMLDocumentFragmentScannerImpl.this.fReportCdataEvent) {
                                    return 12;
                                }
                                return 4;
                            }
                            XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                            return 4;
                        }
                        XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                        return 4;
                    }
                    if (this.scanRootElementHook()) {
                        XMLDocumentFragmentScannerImpl.this.fEmptyElement = true;
                        return 1;
                    }
                    XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                    return 1;
                }
                XMLDocumentFragmentScannerImpl.this.fContentBuffer.clear();
                XMLDocumentFragmentScannerImpl.this.scanCharReferenceValue(XMLDocumentFragmentScannerImpl.this.fContentBuffer, null);
                final XMLDocumentFragmentScannerImpl this$6 = XMLDocumentFragmentScannerImpl.this;
                --this$6.fMarkupDepth;
                XMLDocumentFragmentScannerImpl.this.setScannerState(22);
                return 4;
            }
            catch (final EOFException e) {
                this.endOfFileHook(e);
                return -1;
            }
        }
        
        protected boolean scanForDoctypeHook() throws IOException, XNIException {
            return false;
        }
        
        protected boolean elementDepthIsZeroHook() throws IOException, XNIException {
            return false;
        }
        
        protected boolean scanRootElementHook() throws IOException, XNIException {
            return false;
        }
        
        protected void endOfFileHook(final EOFException e) throws IOException, XNIException {
            if (XMLDocumentFragmentScannerImpl.this.fMarkupDepth != 0) {
                XMLDocumentFragmentScannerImpl.this.reportFatalError("PrematureEOF", null);
            }
        }
    }
    
    protected interface Driver
    {
        int next() throws IOException, XNIException;
    }
}
