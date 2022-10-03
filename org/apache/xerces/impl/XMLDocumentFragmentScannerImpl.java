package org.apache.xerces.impl;

import java.io.EOFException;
import java.io.CharConversionException;
import org.apache.xerces.impl.io.MalformedByteSequenceException;
import org.apache.xerces.util.AugmentationsImpl;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.XNIException;
import java.io.IOException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLDocumentScanner;

public class XMLDocumentFragmentScannerImpl extends XMLScanner implements XMLDocumentScanner, XMLComponent, XMLEntityHandler
{
    protected static final int SCANNER_STATE_START_OF_MARKUP = 1;
    protected static final int SCANNER_STATE_COMMENT = 2;
    protected static final int SCANNER_STATE_PI = 3;
    protected static final int SCANNER_STATE_DOCTYPE = 4;
    protected static final int SCANNER_STATE_ROOT_ELEMENT = 6;
    protected static final int SCANNER_STATE_CONTENT = 7;
    protected static final int SCANNER_STATE_REFERENCE = 8;
    protected static final int SCANNER_STATE_END_OF_INPUT = 13;
    protected static final int SCANNER_STATE_TERMINATED = 14;
    protected static final int SCANNER_STATE_CDATA = 15;
    protected static final int SCANNER_STATE_TEXT_DECL = 16;
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] FEATURE_DEFAULTS;
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] PROPERTY_DEFAULTS;
    private static final boolean DEBUG_SCANNER_STATE = false;
    private static final boolean DEBUG_DISPATCHER = false;
    protected static final boolean DEBUG_CONTENT_SCANNING = false;
    protected XMLDocumentHandler fDocumentHandler;
    protected int[] fEntityStack;
    protected int fMarkupDepth;
    protected int fScannerState;
    protected boolean fInScanContent;
    protected boolean fHasExternalDTD;
    protected boolean fStandalone;
    protected boolean fIsEntityDeclaredVC;
    protected ExternalSubsetResolver fExternalSubsetResolver;
    protected QName fCurrentElement;
    protected final ElementStack fElementStack;
    protected boolean fNotifyBuiltInRefs;
    protected Dispatcher fDispatcher;
    protected final Dispatcher fContentDispatcher;
    protected final QName fElementQName;
    protected final QName fAttributeQName;
    protected final XMLAttributesImpl fAttributes;
    protected final XMLString fTempString;
    protected final XMLString fTempString2;
    private final String[] fStrings;
    private final XMLStringBuffer fStringBuffer;
    private final XMLStringBuffer fStringBuffer2;
    private final QName fQName;
    private final char[] fSingleChar;
    private boolean fSawSpace;
    private Augmentations fTempAugmentations;
    
    public XMLDocumentFragmentScannerImpl() {
        this.fEntityStack = new int[4];
        this.fInScanContent = false;
        this.fElementStack = new ElementStack();
        this.fNotifyBuiltInRefs = false;
        this.fContentDispatcher = this.createContentDispatcher();
        this.fElementQName = new QName();
        this.fAttributeQName = new QName();
        this.fAttributes = new XMLAttributesImpl();
        this.fTempString = new XMLString();
        this.fTempString2 = new XMLString();
        this.fStrings = new String[3];
        this.fStringBuffer = new XMLStringBuffer();
        this.fStringBuffer2 = new XMLStringBuffer();
        this.fQName = new QName();
        this.fSingleChar = new char[1];
        this.fTempAugmentations = null;
    }
    
    public void setInputSource(final XMLInputSource xmlInputSource) throws IOException {
        this.fEntityManager.setEntityHandler(this);
        this.fEntityManager.startEntity("$fragment$", xmlInputSource, false, true);
    }
    
    public boolean scanDocument(final boolean b) throws IOException, XNIException {
        this.fEntityScanner = this.fEntityManager.getEntityScanner();
        this.fEntityManager.setEntityHandler(this);
        while (this.fDispatcher.dispatch(b)) {
            if (!b) {
                return true;
            }
        }
        return false;
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) throws XMLConfigurationException {
        super.reset(xmlComponentManager);
        this.fAttributes.setNamespaces(this.fNamespaces);
        this.fMarkupDepth = 0;
        this.fCurrentElement = null;
        this.fElementStack.clear();
        this.fHasExternalDTD = false;
        this.fStandalone = false;
        this.fIsEntityDeclaredVC = false;
        this.fInScanContent = false;
        this.setScannerState(7);
        this.setDispatcher(this.fContentDispatcher);
        if (this.fParserSettings) {
            try {
                this.fNotifyBuiltInRefs = xmlComponentManager.getFeature("http://apache.org/xml/features/scanner/notify-builtin-refs");
            }
            catch (final XMLConfigurationException ex) {
                this.fNotifyBuiltInRefs = false;
            }
            try {
                final Object property = xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
                this.fExternalSubsetResolver = ((property instanceof ExternalSubsetResolver) ? ((ExternalSubsetResolver)property) : null);
            }
            catch (final XMLConfigurationException ex2) {
                this.fExternalSubsetResolver = null;
            }
        }
    }
    
    public String[] getRecognizedFeatures() {
        return XMLDocumentFragmentScannerImpl.RECOGNIZED_FEATURES.clone();
    }
    
    public void setFeature(final String s, final boolean fNotifyBuiltInRefs) throws XMLConfigurationException {
        super.setFeature(s, fNotifyBuiltInRefs);
        if (s.startsWith("http://apache.org/xml/features/") && s.length() - "http://apache.org/xml/features/".length() == "scanner/notify-builtin-refs".length() && s.endsWith("scanner/notify-builtin-refs")) {
            this.fNotifyBuiltInRefs = fNotifyBuiltInRefs;
        }
    }
    
    public String[] getRecognizedProperties() {
        return XMLDocumentFragmentScannerImpl.RECOGNIZED_PROPERTIES.clone();
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        super.setProperty(s, o);
        if (s.startsWith("http://apache.org/xml/properties/")) {
            final int n = s.length() - "http://apache.org/xml/properties/".length();
            if (n == "internal/entity-manager".length() && s.endsWith("internal/entity-manager")) {
                this.fEntityManager = (XMLEntityManager)o;
                return;
            }
            if (n == "internal/entity-resolver".length() && s.endsWith("internal/entity-resolver")) {
                this.fExternalSubsetResolver = ((o instanceof ExternalSubsetResolver) ? ((ExternalSubsetResolver)o) : null);
            }
        }
    }
    
    public Boolean getFeatureDefault(final String s) {
        for (int i = 0; i < XMLDocumentFragmentScannerImpl.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLDocumentFragmentScannerImpl.RECOGNIZED_FEATURES[i].equals(s)) {
                return XMLDocumentFragmentScannerImpl.FEATURE_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public Object getPropertyDefault(final String s) {
        for (int i = 0; i < XMLDocumentFragmentScannerImpl.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLDocumentFragmentScannerImpl.RECOGNIZED_PROPERTIES[i].equals(s)) {
                return XMLDocumentFragmentScannerImpl.PROPERTY_DEFAULTS[i];
            }
        }
        return null;
    }
    
    public void setDocumentHandler(final XMLDocumentHandler fDocumentHandler) {
        this.fDocumentHandler = fDocumentHandler;
    }
    
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }
    
    public void startEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        if (this.fEntityDepth == this.fEntityStack.length) {
            final int[] fEntityStack = new int[this.fEntityStack.length * 2];
            System.arraycopy(this.fEntityStack, 0, fEntityStack, 0, this.fEntityStack.length);
            this.fEntityStack = fEntityStack;
        }
        this.fEntityStack[this.fEntityDepth] = this.fMarkupDepth;
        super.startEntity(s, xmlResourceIdentifier, s2, augmentations);
        if (this.fStandalone && this.fEntityManager.isEntityDeclInExternalSubset(s)) {
            this.reportFatalError("MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[] { s });
        }
        if (this.fDocumentHandler != null && !this.fScanningAttribute && !s.equals("[xml]")) {
            this.fDocumentHandler.startGeneralEntity(s, xmlResourceIdentifier, s2, augmentations);
        }
    }
    
    public void endEntity(final String s, final Augmentations augmentations) throws XNIException {
        if (this.fInScanContent && this.fStringBuffer.length != 0 && this.fDocumentHandler != null) {
            this.fDocumentHandler.characters(this.fStringBuffer, null);
            this.fStringBuffer.length = 0;
        }
        super.endEntity(s, augmentations);
        if (this.fMarkupDepth != this.fEntityStack[this.fEntityDepth]) {
            this.reportFatalError("MarkupEntityMismatch", null);
        }
        if (this.fDocumentHandler != null && !this.fScanningAttribute && !s.equals("[xml]")) {
            this.fDocumentHandler.endGeneralEntity(s, augmentations);
        }
    }
    
    protected Dispatcher createContentDispatcher() {
        return new FragmentContentDispatcher();
    }
    
    protected void scanXMLDeclOrTextDecl(final boolean b) throws IOException, XNIException {
        super.scanXMLDeclOrTextDecl(b, this.fStrings);
        --this.fMarkupDepth;
        final String xmlVersion = this.fStrings[0];
        final String encoding = this.fStrings[1];
        final String s = this.fStrings[2];
        this.fStandalone = (s != null && s.equals("yes"));
        this.fEntityManager.setStandalone(this.fStandalone);
        this.fEntityScanner.setXMLVersion(xmlVersion);
        if (this.fDocumentHandler != null) {
            if (b) {
                this.fDocumentHandler.textDecl(xmlVersion, encoding, null);
            }
            else {
                this.fDocumentHandler.xmlDecl(xmlVersion, encoding, s, null);
            }
        }
        if (encoding != null && !this.fEntityScanner.fCurrentEntity.isEncodingExternallySpecified()) {
            this.fEntityScanner.setEncoding(encoding);
        }
    }
    
    protected void scanPIData(final String s, final XMLString xmlString) throws IOException, XNIException {
        super.scanPIData(s, xmlString);
        --this.fMarkupDepth;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.processingInstruction(s, xmlString, null);
        }
    }
    
    protected void scanComment() throws IOException, XNIException {
        this.scanComment(this.fStringBuffer);
        --this.fMarkupDepth;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.comment(this.fStringBuffer, null);
        }
    }
    
    protected boolean scanStartElement() throws IOException, XNIException {
        if (this.fNamespaces) {
            this.fEntityScanner.scanQName(this.fElementQName);
        }
        else {
            final String scanName = this.fEntityScanner.scanName();
            this.fElementQName.setValues(null, scanName, scanName, null);
        }
        final String rawname = this.fElementQName.rawname;
        this.fCurrentElement = this.fElementStack.pushElement(this.fElementQName);
        boolean b = false;
        this.fAttributes.removeAllAttributes();
        while (true) {
            final boolean skipSpaces = this.fEntityScanner.skipSpaces();
            final int peekChar = this.fEntityScanner.peekChar();
            if (peekChar == 62) {
                this.fEntityScanner.scanChar();
                break;
            }
            if (peekChar == 47) {
                this.fEntityScanner.scanChar();
                if (!this.fEntityScanner.skipChar(62)) {
                    this.reportFatalError("ElementUnterminated", new Object[] { rawname });
                }
                b = true;
                break;
            }
            if ((!this.isValidNameStartChar(peekChar) || !skipSpaces) && (!this.isValidNameStartHighSurrogate(peekChar) || !skipSpaces)) {
                this.reportFatalError("ElementUnterminated", new Object[] { rawname });
            }
            this.scanAttribute(this.fAttributes);
        }
        if (this.fDocumentHandler != null) {
            if (b) {
                --this.fMarkupDepth;
                if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
                    this.reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname });
                }
                this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
                this.fElementStack.popElement(this.fElementQName);
            }
            else {
                this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
            }
        }
        return b;
    }
    
    protected void scanStartElementName() throws IOException, XNIException {
        if (this.fNamespaces) {
            this.fEntityScanner.scanQName(this.fElementQName);
        }
        else {
            final String scanName = this.fEntityScanner.scanName();
            this.fElementQName.setValues(null, scanName, scanName, null);
        }
        this.fSawSpace = this.fEntityScanner.skipSpaces();
    }
    
    protected boolean scanStartElementAfterName() throws IOException, XNIException {
        final String rawname = this.fElementQName.rawname;
        this.fCurrentElement = this.fElementStack.pushElement(this.fElementQName);
        boolean b = false;
        this.fAttributes.removeAllAttributes();
        while (true) {
            final int peekChar = this.fEntityScanner.peekChar();
            if (peekChar == 62) {
                this.fEntityScanner.scanChar();
                break;
            }
            if (peekChar == 47) {
                this.fEntityScanner.scanChar();
                if (!this.fEntityScanner.skipChar(62)) {
                    this.reportFatalError("ElementUnterminated", new Object[] { rawname });
                }
                b = true;
                break;
            }
            if ((!this.isValidNameStartChar(peekChar) || !this.fSawSpace) && (!this.isValidNameStartHighSurrogate(peekChar) || !this.fSawSpace)) {
                this.reportFatalError("ElementUnterminated", new Object[] { rawname });
            }
            this.scanAttribute(this.fAttributes);
            this.fSawSpace = this.fEntityScanner.skipSpaces();
        }
        if (this.fDocumentHandler != null) {
            if (b) {
                --this.fMarkupDepth;
                if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
                    this.reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname });
                }
                this.fDocumentHandler.emptyElement(this.fElementQName, this.fAttributes, null);
                this.fElementStack.popElement(this.fElementQName);
            }
            else {
                this.fDocumentHandler.startElement(this.fElementQName, this.fAttributes, null);
            }
        }
        return b;
    }
    
    protected void scanAttribute(final XMLAttributes xmlAttributes) throws IOException, XNIException {
        if (this.fNamespaces) {
            this.fEntityScanner.scanQName(this.fAttributeQName);
        }
        else {
            final String scanName = this.fEntityScanner.scanName();
            this.fAttributeQName.setValues(null, scanName, scanName, null);
        }
        this.fEntityScanner.skipSpaces();
        if (!this.fEntityScanner.skipChar(61)) {
            this.reportFatalError("EqRequiredInAttribute", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
        }
        this.fEntityScanner.skipSpaces();
        final int length = xmlAttributes.getLength();
        final int addAttribute = xmlAttributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, null);
        if (length == xmlAttributes.getLength()) {
            this.reportFatalError("AttributeNotUnique", new Object[] { this.fCurrentElement.rawname, this.fAttributeQName.rawname });
        }
        final boolean scanAttributeValue = this.scanAttributeValue(this.fTempString, this.fTempString2, this.fAttributeQName.rawname, this.fIsEntityDeclaredVC, this.fCurrentElement.rawname);
        xmlAttributes.setValue(addAttribute, this.fTempString.toString());
        if (!scanAttributeValue) {
            xmlAttributes.setNonNormalizedValue(addAttribute, this.fTempString2.toString());
        }
        xmlAttributes.setSpecified(addAttribute, true);
    }
    
    protected int scanContent() throws IOException, XNIException {
        XMLString xmlString = this.fTempString;
        int scanContent = this.fEntityScanner.scanContent(xmlString);
        if (scanContent == 13) {
            this.fEntityScanner.scanChar();
            this.fStringBuffer.clear();
            this.fStringBuffer.append(this.fTempString);
            this.fStringBuffer.append((char)scanContent);
            xmlString = this.fStringBuffer;
            scanContent = -1;
        }
        if (this.fDocumentHandler != null && xmlString.length > 0) {
            this.fDocumentHandler.characters(xmlString, null);
        }
        if (scanContent == 93 && this.fTempString.length == 0) {
            this.fStringBuffer.clear();
            this.fStringBuffer.append((char)this.fEntityScanner.scanChar());
            this.fInScanContent = true;
            if (this.fEntityScanner.skipChar(93)) {
                this.fStringBuffer.append(']');
                while (this.fEntityScanner.skipChar(93)) {
                    this.fStringBuffer.append(']');
                }
                if (this.fEntityScanner.skipChar(62)) {
                    this.reportFatalError("CDEndInContent", null);
                }
            }
            if (this.fDocumentHandler != null && this.fStringBuffer.length != 0) {
                this.fDocumentHandler.characters(this.fStringBuffer, null);
            }
            this.fInScanContent = false;
            scanContent = -1;
        }
        return scanContent;
    }
    
    protected boolean scanCDATASection(final boolean b) throws IOException, XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startCDATA(null);
        }
        while (true) {
            this.fStringBuffer.clear();
            if (!this.fEntityScanner.scanData("]]", this.fStringBuffer)) {
                if (this.fDocumentHandler != null && this.fStringBuffer.length > 0) {
                    this.fDocumentHandler.characters(this.fStringBuffer, null);
                }
                int n = 0;
                while (this.fEntityScanner.skipChar(93)) {
                    ++n;
                }
                if (this.fDocumentHandler != null && n > 0) {
                    this.fStringBuffer.clear();
                    if (n > 2048) {
                        final int n2 = n / 2048;
                        final int length = n % 2048;
                        for (int i = 0; i < 2048; ++i) {
                            this.fStringBuffer.append(']');
                        }
                        for (int j = 0; j < n2; ++j) {
                            this.fDocumentHandler.characters(this.fStringBuffer, null);
                        }
                        if (length != 0) {
                            this.fStringBuffer.length = length;
                            this.fDocumentHandler.characters(this.fStringBuffer, null);
                        }
                    }
                    else {
                        for (int k = 0; k < n; ++k) {
                            this.fStringBuffer.append(']');
                        }
                        this.fDocumentHandler.characters(this.fStringBuffer, null);
                    }
                }
                if (this.fEntityScanner.skipChar(62)) {
                    break;
                }
                if (this.fDocumentHandler == null) {
                    continue;
                }
                this.fStringBuffer.clear();
                this.fStringBuffer.append("]]");
                this.fDocumentHandler.characters(this.fStringBuffer, null);
            }
            else {
                if (this.fDocumentHandler != null) {
                    this.fDocumentHandler.characters(this.fStringBuffer, null);
                }
                final int peekChar = this.fEntityScanner.peekChar();
                if (peekChar == -1 || !this.isInvalidLiteral(peekChar)) {
                    continue;
                }
                if (XMLChar.isHighSurrogate(peekChar)) {
                    this.fStringBuffer.clear();
                    this.scanSurrogates(this.fStringBuffer);
                    if (this.fDocumentHandler == null) {
                        continue;
                    }
                    this.fDocumentHandler.characters(this.fStringBuffer, null);
                }
                else {
                    this.reportFatalError("InvalidCharInCDSect", new Object[] { Integer.toString(peekChar, 16) });
                    this.fEntityScanner.scanChar();
                }
            }
        }
        --this.fMarkupDepth;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endCDATA(null);
        }
        return true;
    }
    
    protected int scanEndElement() throws IOException, XNIException {
        this.fElementStack.popElement(this.fElementQName);
        if (!this.fEntityScanner.skipString(this.fElementQName.rawname)) {
            this.reportFatalError("ETagRequired", new Object[] { this.fElementQName.rawname });
        }
        this.fEntityScanner.skipSpaces();
        if (!this.fEntityScanner.skipChar(62)) {
            this.reportFatalError("ETagUnterminated", new Object[] { this.fElementQName.rawname });
        }
        --this.fMarkupDepth;
        --this.fMarkupDepth;
        if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
            this.reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname });
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endElement(this.fElementQName, null);
        }
        return this.fMarkupDepth;
    }
    
    protected void scanCharReference() throws IOException, XNIException {
        this.fStringBuffer2.clear();
        final int scanCharReferenceValue = this.scanCharReferenceValue(this.fStringBuffer2, null);
        --this.fMarkupDepth;
        if (scanCharReferenceValue != -1 && this.fDocumentHandler != null) {
            if (this.fNotifyCharRefs) {
                this.fDocumentHandler.startGeneralEntity(this.fCharRefLiteral, null, null, null);
            }
            Augmentations fTempAugmentations = null;
            if (this.fValidation && scanCharReferenceValue <= 32) {
                if (this.fTempAugmentations != null) {
                    this.fTempAugmentations.removeAllItems();
                }
                else {
                    this.fTempAugmentations = new AugmentationsImpl();
                }
                fTempAugmentations = this.fTempAugmentations;
                fTempAugmentations.putItem("CHAR_REF_PROBABLE_WS", Boolean.TRUE);
            }
            this.fDocumentHandler.characters(this.fStringBuffer2, fTempAugmentations);
            if (this.fNotifyCharRefs) {
                this.fDocumentHandler.endGeneralEntity(this.fCharRefLiteral, null);
            }
        }
    }
    
    protected void scanEntityReference() throws IOException, XNIException {
        final String scanName = this.fEntityScanner.scanName();
        if (scanName == null) {
            this.reportFatalError("NameRequiredInReference", null);
            return;
        }
        if (!this.fEntityScanner.skipChar(59)) {
            this.reportFatalError("SemicolonRequiredInReference", new Object[] { scanName });
        }
        --this.fMarkupDepth;
        if (scanName == XMLDocumentFragmentScannerImpl.fAmpSymbol) {
            this.handleCharacter('&', XMLDocumentFragmentScannerImpl.fAmpSymbol);
        }
        else if (scanName == XMLDocumentFragmentScannerImpl.fLtSymbol) {
            this.handleCharacter('<', XMLDocumentFragmentScannerImpl.fLtSymbol);
        }
        else if (scanName == XMLDocumentFragmentScannerImpl.fGtSymbol) {
            this.handleCharacter('>', XMLDocumentFragmentScannerImpl.fGtSymbol);
        }
        else if (scanName == XMLDocumentFragmentScannerImpl.fQuotSymbol) {
            this.handleCharacter('\"', XMLDocumentFragmentScannerImpl.fQuotSymbol);
        }
        else if (scanName == XMLDocumentFragmentScannerImpl.fAposSymbol) {
            this.handleCharacter('\'', XMLDocumentFragmentScannerImpl.fAposSymbol);
        }
        else if (this.fEntityManager.isUnparsedEntity(scanName)) {
            this.reportFatalError("ReferenceToUnparsedEntity", new Object[] { scanName });
        }
        else {
            if (!this.fEntityManager.isDeclaredEntity(scanName)) {
                if (this.fIsEntityDeclaredVC) {
                    if (this.fValidation) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { scanName }, (short)1);
                    }
                }
                else {
                    this.reportFatalError("EntityNotDeclared", new Object[] { scanName });
                }
            }
            this.fEntityManager.startEntity(scanName, false);
        }
    }
    
    private void handleCharacter(final char c, final String s) throws XNIException {
        if (this.fDocumentHandler != null) {
            if (this.fNotifyBuiltInRefs) {
                this.fDocumentHandler.startGeneralEntity(s, null, null, null);
            }
            this.fSingleChar[0] = c;
            this.fTempString.setValues(this.fSingleChar, 0, 1);
            this.fDocumentHandler.characters(this.fTempString, null);
            if (this.fNotifyBuiltInRefs) {
                this.fDocumentHandler.endGeneralEntity(s, null);
            }
        }
    }
    
    protected int handleEndElement(final QName qName, final boolean b) throws XNIException {
        --this.fMarkupDepth;
        if (this.fMarkupDepth < this.fEntityStack[this.fEntityDepth - 1]) {
            this.reportFatalError("ElementEntityMismatch", new Object[] { this.fCurrentElement.rawname });
        }
        final QName fqName = this.fQName;
        this.fElementStack.popElement(fqName);
        if (qName.rawname != fqName.rawname) {
            this.reportFatalError("ETagRequired", new Object[] { fqName.rawname });
        }
        if (this.fNamespaces) {
            qName.uri = fqName.uri;
        }
        if (this.fDocumentHandler != null && !b) {
            this.fDocumentHandler.endElement(qName, null);
        }
        return this.fMarkupDepth;
    }
    
    protected final void setScannerState(final int fScannerState) {
        this.fScannerState = fScannerState;
    }
    
    protected final void setDispatcher(final Dispatcher fDispatcher) {
        this.fDispatcher = fDispatcher;
    }
    
    protected String getScannerStateName(final int n) {
        switch (n) {
            case 4: {
                return "SCANNER_STATE_DOCTYPE";
            }
            case 6: {
                return "SCANNER_STATE_ROOT_ELEMENT";
            }
            case 1: {
                return "SCANNER_STATE_START_OF_MARKUP";
            }
            case 2: {
                return "SCANNER_STATE_COMMENT";
            }
            case 3: {
                return "SCANNER_STATE_PI";
            }
            case 7: {
                return "SCANNER_STATE_CONTENT";
            }
            case 8: {
                return "SCANNER_STATE_REFERENCE";
            }
            case 13: {
                return "SCANNER_STATE_END_OF_INPUT";
            }
            case 14: {
                return "SCANNER_STATE_TERMINATED";
            }
            case 15: {
                return "SCANNER_STATE_CDATA";
            }
            case 16: {
                return "SCANNER_STATE_TEXT_DECL";
            }
            default: {
                return "??? (" + n + ')';
            }
        }
    }
    
    public String getDispatcherName(final Dispatcher dispatcher) {
        return "null";
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/validation", "http://apache.org/xml/features/scanner/notify-builtin-refs", "http://apache.org/xml/features/scanner/notify-char-refs" };
        FEATURE_DEFAULTS = new Boolean[] { null, null, Boolean.FALSE, Boolean.FALSE };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/entity-resolver" };
        PROPERTY_DEFAULTS = new Object[] { null, null, null, null };
    }
    
    protected interface Dispatcher
    {
        boolean dispatch(final boolean p0) throws IOException, XNIException;
    }
    
    protected static class ElementStack
    {
        protected QName[] fElements;
        protected int fSize;
        
        public ElementStack() {
            this.fElements = new QName[10];
            for (int i = 0; i < this.fElements.length; ++i) {
                this.fElements[i] = new QName();
            }
        }
        
        public QName pushElement(final QName values) {
            if (this.fSize == this.fElements.length) {
                final QName[] fElements = new QName[this.fElements.length * 2];
                System.arraycopy(this.fElements, 0, fElements, 0, this.fSize);
                this.fElements = fElements;
                for (int i = this.fSize; i < this.fElements.length; ++i) {
                    this.fElements[i] = new QName();
                }
            }
            this.fElements[this.fSize].setValues(values);
            return this.fElements[this.fSize++];
        }
        
        public void popElement(final QName qName) {
            final QName[] fElements = this.fElements;
            final int fSize = this.fSize - 1;
            this.fSize = fSize;
            qName.setValues(fElements[fSize]);
        }
        
        public void clear() {
            this.fSize = 0;
        }
    }
    
    protected class FragmentContentDispatcher implements Dispatcher
    {
        public boolean dispatch(final boolean b) throws IOException, XNIException {
            try {
                boolean b2;
                do {
                    b2 = false;
                    switch (XMLDocumentFragmentScannerImpl.this.fScannerState) {
                        case 7: {
                            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(60)) {
                                XMLDocumentFragmentScannerImpl.this.setScannerState(1);
                                b2 = true;
                                continue;
                            }
                            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(38)) {
                                XMLDocumentFragmentScannerImpl.this.setScannerState(8);
                                b2 = true;
                                continue;
                            }
                            do {
                                final int scanContent = XMLDocumentFragmentScannerImpl.this.scanContent();
                                if (scanContent == 60) {
                                    XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
                                    XMLDocumentFragmentScannerImpl.this.setScannerState(1);
                                    break;
                                }
                                if (scanContent == 38) {
                                    XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
                                    XMLDocumentFragmentScannerImpl.this.setScannerState(8);
                                    break;
                                }
                                if (scanContent == -1 || !XMLDocumentFragmentScannerImpl.this.isInvalidLiteral(scanContent)) {
                                    continue;
                                }
                                if (XMLChar.isHighSurrogate(scanContent)) {
                                    XMLDocumentFragmentScannerImpl.this.fStringBuffer.clear();
                                    if (!XMLDocumentFragmentScannerImpl.this.scanSurrogates(XMLDocumentFragmentScannerImpl.this.fStringBuffer) || XMLDocumentFragmentScannerImpl.this.fDocumentHandler == null) {
                                        continue;
                                    }
                                    XMLDocumentFragmentScannerImpl.this.fDocumentHandler.characters(XMLDocumentFragmentScannerImpl.this.fStringBuffer, null);
                                }
                                else {
                                    XMLDocumentFragmentScannerImpl.this.reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(scanContent, 16) });
                                    XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar();
                                }
                            } while (b);
                            continue;
                        }
                        case 1: {
                            final XMLDocumentFragmentScannerImpl this$0 = XMLDocumentFragmentScannerImpl.this;
                            ++this$0.fMarkupDepth;
                            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(47)) {
                                if (XMLDocumentFragmentScannerImpl.this.scanEndElement() == 0 && this.elementDepthIsZeroHook()) {
                                    return true;
                                }
                                XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                                continue;
                            }
                            else {
                                if (XMLDocumentFragmentScannerImpl.this.isValidNameStartChar(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                                    XMLDocumentFragmentScannerImpl.this.scanStartElement();
                                    XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                                    continue;
                                }
                                if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(33)) {
                                    if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(45)) {
                                        if (!XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(45)) {
                                            XMLDocumentFragmentScannerImpl.this.reportFatalError("InvalidCommentStart", null);
                                        }
                                        XMLDocumentFragmentScannerImpl.this.setScannerState(2);
                                        b2 = true;
                                        continue;
                                    }
                                    if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipString("[CDATA[")) {
                                        XMLDocumentFragmentScannerImpl.this.setScannerState(15);
                                        b2 = true;
                                        continue;
                                    }
                                    if (!this.scanForDoctypeHook()) {
                                        XMLDocumentFragmentScannerImpl.this.reportFatalError("MarkupNotRecognizedInContent", null);
                                        continue;
                                    }
                                    continue;
                                }
                                else {
                                    if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(63)) {
                                        XMLDocumentFragmentScannerImpl.this.setScannerState(3);
                                        b2 = true;
                                        continue;
                                    }
                                    if (XMLDocumentFragmentScannerImpl.this.isValidNameStartHighSurrogate(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                                        XMLDocumentFragmentScannerImpl.this.scanStartElement();
                                        XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                                        continue;
                                    }
                                    XMLDocumentFragmentScannerImpl.this.reportFatalError("MarkupNotRecognizedInContent", null);
                                    XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                                    continue;
                                }
                            }
                            break;
                        }
                        case 8: {
                            final XMLDocumentFragmentScannerImpl this$2 = XMLDocumentFragmentScannerImpl.this;
                            ++this$2.fMarkupDepth;
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipChar(35)) {
                                XMLDocumentFragmentScannerImpl.this.scanCharReference();
                                continue;
                            }
                            XMLDocumentFragmentScannerImpl.this.scanEntityReference();
                            continue;
                        }
                        case 6: {
                            if (this.scanRootElementHook()) {
                                return true;
                            }
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 2: {
                            XMLDocumentFragmentScannerImpl.this.scanComment();
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                            continue;
                        }
                        case 3: {
                            XMLDocumentFragmentScannerImpl.this.scanPI();
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                            continue;
                        }
                        case 15: {
                            XMLDocumentFragmentScannerImpl.this.scanCDATASection(b);
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                            continue;
                        }
                        case 16: {
                            if (XMLDocumentFragmentScannerImpl.this.fEntityScanner.skipString("<?xml")) {
                                final XMLDocumentFragmentScannerImpl this$3 = XMLDocumentFragmentScannerImpl.this;
                                ++this$3.fMarkupDepth;
                                if (XMLDocumentFragmentScannerImpl.this.isValidNameChar(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                                    XMLDocumentFragmentScannerImpl.this.fStringBuffer.clear();
                                    XMLDocumentFragmentScannerImpl.this.fStringBuffer.append("xml");
                                    if (XMLDocumentFragmentScannerImpl.this.fNamespaces) {
                                        while (XMLDocumentFragmentScannerImpl.this.isValidNCName(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                                            XMLDocumentFragmentScannerImpl.this.fStringBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar());
                                        }
                                    }
                                    else {
                                        while (XMLDocumentFragmentScannerImpl.this.isValidNameChar(XMLDocumentFragmentScannerImpl.this.fEntityScanner.peekChar())) {
                                            XMLDocumentFragmentScannerImpl.this.fStringBuffer.append((char)XMLDocumentFragmentScannerImpl.this.fEntityScanner.scanChar());
                                        }
                                    }
                                    XMLDocumentFragmentScannerImpl.this.scanPIData(XMLDocumentFragmentScannerImpl.this.fSymbolTable.addSymbol(XMLDocumentFragmentScannerImpl.this.fStringBuffer.ch, XMLDocumentFragmentScannerImpl.this.fStringBuffer.offset, XMLDocumentFragmentScannerImpl.this.fStringBuffer.length), XMLDocumentFragmentScannerImpl.this.fTempString);
                                }
                                else {
                                    XMLDocumentFragmentScannerImpl.this.scanXMLDeclOrTextDecl(true);
                                }
                            }
                            XMLDocumentFragmentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                            continue;
                        }
                        case 4: {
                            XMLDocumentFragmentScannerImpl.this.reportFatalError("DoctypeIllegalInContent", null);
                            XMLDocumentFragmentScannerImpl.this.setScannerState(7);
                            continue;
                        }
                    }
                } while (b || b2);
            }
            catch (final MalformedByteSequenceException ex) {
                XMLDocumentFragmentScannerImpl.this.fErrorReporter.reportError(ex.getDomain(), ex.getKey(), ex.getArguments(), (short)2, ex);
                return false;
            }
            catch (final CharConversionException ex2) {
                XMLDocumentFragmentScannerImpl.this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2, ex2);
                return false;
            }
            catch (final EOFException ex3) {
                this.endOfFileHook(ex3);
                return false;
            }
            return true;
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
        
        protected void endOfFileHook(final EOFException ex) throws IOException, XNIException {
            if (XMLDocumentFragmentScannerImpl.this.fMarkupDepth != 0) {
                XMLDocumentFragmentScannerImpl.this.reportFatalError("PrematureEOF", null);
            }
        }
    }
}
