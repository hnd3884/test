package com.sun.org.apache.xerces.internal.impl;

import java.util.NoSuchElementException;
import com.sun.xml.internal.stream.StaxXMLInputSource;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.xml.internal.stream.dtd.DTDGrammarUtil;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.xml.internal.stream.Entity;
import java.io.EOFException;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.xml.internal.stream.XMLBufferListener;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import java.io.IOException;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.impl.dtd.XMLDTDDescription;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.impl.validation.ValidationManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDScanner;

public class XMLDocumentScannerImpl extends XMLDocumentFragmentScannerImpl
{
    protected static final int SCANNER_STATE_XML_DECL = 42;
    protected static final int SCANNER_STATE_PROLOG = 43;
    protected static final int SCANNER_STATE_TRAILING_MISC = 44;
    protected static final int SCANNER_STATE_DTD_INTERNAL_DECLS = 45;
    protected static final int SCANNER_STATE_DTD_EXTERNAL = 46;
    protected static final int SCANNER_STATE_DTD_EXTERNAL_DECLS = 47;
    protected static final int SCANNER_STATE_NO_SUCH_ELEMENT_EXCEPTION = 48;
    protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
    protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    protected static final String DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
    protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    protected static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
    private static final String[] RECOGNIZED_FEATURES;
    private static final Boolean[] FEATURE_DEFAULTS;
    private static final String[] RECOGNIZED_PROPERTIES;
    private static final Object[] PROPERTY_DEFAULTS;
    protected XMLDTDScanner fDTDScanner;
    protected ValidationManager fValidationManager;
    protected XMLStringBuffer fDTDDecl;
    protected boolean fReadingDTD;
    protected boolean fAddedListener;
    protected String fDoctypeName;
    protected String fDoctypePublicId;
    protected String fDoctypeSystemId;
    protected NamespaceContext fNamespaceContext;
    protected boolean fLoadExternalDTD;
    protected boolean fSeenDoctypeDecl;
    protected boolean fScanEndElement;
    protected Driver fXMLDeclDriver;
    protected Driver fPrologDriver;
    protected Driver fDTDDriver;
    protected Driver fTrailingMiscDriver;
    protected int fStartPos;
    protected int fEndPos;
    protected boolean fSeenInternalSubset;
    private String[] fStrings;
    private XMLInputSource fExternalSubsetSource;
    private final XMLDTDDescription fDTDDescription;
    private static final char[] DOCTYPE;
    private static final char[] COMMENTSTRING;
    
    public XMLDocumentScannerImpl() {
        this.fDTDScanner = null;
        this.fDTDDecl = null;
        this.fReadingDTD = false;
        this.fAddedListener = false;
        this.fNamespaceContext = new NamespaceSupport();
        this.fLoadExternalDTD = true;
        this.fXMLDeclDriver = new XMLDeclDriver();
        this.fPrologDriver = new PrologDriver();
        this.fDTDDriver = null;
        this.fTrailingMiscDriver = new TrailingMiscDriver();
        this.fStartPos = 0;
        this.fEndPos = 0;
        this.fSeenInternalSubset = false;
        this.fStrings = new String[3];
        this.fExternalSubsetSource = null;
        this.fDTDDescription = new XMLDTDDescription(null, null, null, null, null);
    }
    
    @Override
    public void setInputSource(final XMLInputSource inputSource) throws IOException {
        this.fEntityManager.setEntityHandler(this);
        this.fEntityManager.startDocumentEntity(inputSource);
        this.setScannerState(7);
    }
    
    public int getScannetState() {
        return this.fScannerState;
    }
    
    @Override
    public void reset(final PropertyManager propertyManager) {
        super.reset(propertyManager);
        this.fDoctypeName = null;
        this.fDoctypePublicId = null;
        this.fDoctypeSystemId = null;
        this.fSeenDoctypeDecl = false;
        this.fNamespaceContext.reset();
        this.fSupportDTD = (boolean)propertyManager.getProperty("javax.xml.stream.supportDTD");
        this.fLoadExternalDTD = !(boolean)propertyManager.getProperty("http://java.sun.com/xml/stream/properties/ignore-external-dtd");
        this.setScannerState(7);
        this.setDriver(this.fXMLDeclDriver);
        this.fSeenInternalSubset = false;
        if (this.fDTDScanner != null) {
            ((XMLDTDScannerImpl)this.fDTDScanner).reset(propertyManager);
        }
        this.fEndPos = 0;
        this.fStartPos = 0;
        if (this.fDTDDecl != null) {
            this.fDTDDecl.clear();
        }
    }
    
    @Override
    public void reset(final XMLComponentManager componentManager) throws XMLConfigurationException {
        super.reset(componentManager);
        this.fDoctypeName = null;
        this.fDoctypePublicId = null;
        this.fDoctypeSystemId = null;
        this.fSeenDoctypeDecl = false;
        this.fExternalSubsetSource = null;
        this.fLoadExternalDTD = componentManager.getFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
        this.fDisallowDoctype = componentManager.getFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
        this.fNamespaces = componentManager.getFeature("http://xml.org/sax/features/namespaces", true);
        this.fSeenInternalSubset = false;
        this.fDTDScanner = (XMLDTDScanner)componentManager.getProperty("http://apache.org/xml/properties/internal/dtd-scanner");
        this.fValidationManager = (ValidationManager)componentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager", null);
        try {
            this.fNamespaceContext = (NamespaceContext)componentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context");
        }
        catch (final XMLConfigurationException ex) {}
        if (this.fNamespaceContext == null) {
            this.fNamespaceContext = new NamespaceSupport();
        }
        this.fNamespaceContext.reset();
        this.fEndPos = 0;
        this.fStartPos = 0;
        if (this.fDTDDecl != null) {
            this.fDTDDecl.clear();
        }
        this.setScannerState(42);
        this.setDriver(this.fXMLDeclDriver);
    }
    
    @Override
    public String[] getRecognizedFeatures() {
        final String[] featureIds = super.getRecognizedFeatures();
        final int length = (featureIds != null) ? featureIds.length : 0;
        final String[] combinedFeatureIds = new String[length + XMLDocumentScannerImpl.RECOGNIZED_FEATURES.length];
        if (featureIds != null) {
            System.arraycopy(featureIds, 0, combinedFeatureIds, 0, featureIds.length);
        }
        System.arraycopy(XMLDocumentScannerImpl.RECOGNIZED_FEATURES, 0, combinedFeatureIds, length, XMLDocumentScannerImpl.RECOGNIZED_FEATURES.length);
        return combinedFeatureIds;
    }
    
    @Override
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        super.setFeature(featureId, state);
        if (featureId.startsWith("http://apache.org/xml/features/")) {
            final int suffixLength = featureId.length() - "http://apache.org/xml/features/".length();
            if (suffixLength == "nonvalidating/load-external-dtd".length() && featureId.endsWith("nonvalidating/load-external-dtd")) {
                this.fLoadExternalDTD = state;
                return;
            }
            if (suffixLength == "disallow-doctype-decl".length() && featureId.endsWith("disallow-doctype-decl")) {
                this.fDisallowDoctype = state;
            }
        }
    }
    
    @Override
    public String[] getRecognizedProperties() {
        final String[] propertyIds = super.getRecognizedProperties();
        final int length = (propertyIds != null) ? propertyIds.length : 0;
        final String[] combinedPropertyIds = new String[length + XMLDocumentScannerImpl.RECOGNIZED_PROPERTIES.length];
        if (propertyIds != null) {
            System.arraycopy(propertyIds, 0, combinedPropertyIds, 0, propertyIds.length);
        }
        System.arraycopy(XMLDocumentScannerImpl.RECOGNIZED_PROPERTIES, 0, combinedPropertyIds, length, XMLDocumentScannerImpl.RECOGNIZED_PROPERTIES.length);
        return combinedPropertyIds;
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        super.setProperty(propertyId, value);
        if (propertyId.startsWith("http://apache.org/xml/properties/")) {
            final int suffixLength = propertyId.length() - "http://apache.org/xml/properties/".length();
            if (suffixLength == "internal/dtd-scanner".length() && propertyId.endsWith("internal/dtd-scanner")) {
                this.fDTDScanner = (XMLDTDScanner)value;
            }
            if (suffixLength == "internal/namespace-context".length() && propertyId.endsWith("internal/namespace-context") && value != null) {
                this.fNamespaceContext = (NamespaceContext)value;
            }
        }
    }
    
    @Override
    public Boolean getFeatureDefault(final String featureId) {
        for (int i = 0; i < XMLDocumentScannerImpl.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLDocumentScannerImpl.RECOGNIZED_FEATURES[i].equals(featureId)) {
                return XMLDocumentScannerImpl.FEATURE_DEFAULTS[i];
            }
        }
        return super.getFeatureDefault(featureId);
    }
    
    @Override
    public Object getPropertyDefault(final String propertyId) {
        for (int i = 0; i < XMLDocumentScannerImpl.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLDocumentScannerImpl.RECOGNIZED_PROPERTIES[i].equals(propertyId)) {
                return XMLDocumentScannerImpl.PROPERTY_DEFAULTS[i];
            }
        }
        return super.getPropertyDefault(propertyId);
    }
    
    @Override
    public void startEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
        super.startEntity(name, identifier, encoding, augs);
        this.fEntityScanner.registerListener(this);
        if (!name.equals("[xml]") && this.fEntityScanner.isExternal() && (augs == null || !(boolean)augs.getItem("ENTITY_SKIPPED"))) {
            this.setScannerState(36);
        }
        if (this.fDocumentHandler != null && name.equals("[xml]")) {
            this.fDocumentHandler.startDocument(this.fEntityScanner, encoding, this.fNamespaceContext, null);
        }
    }
    
    @Override
    public void endEntity(final String name, final Augmentations augs) throws IOException, XNIException {
        super.endEntity(name, augs);
        if (name.equals("[xml]")) {
            if (this.fMarkupDepth != 0 || this.fDriver != this.fTrailingMiscDriver) {
                throw new EOFException();
            }
            this.setScannerState(34);
        }
    }
    
    public XMLStringBuffer getDTDDecl() {
        final Entity entity = this.fEntityScanner.getCurrentEntity();
        this.fDTDDecl.append(((Entity.ScannedEntity)entity).ch, this.fStartPos, this.fEndPos - this.fStartPos);
        if (this.fSeenInternalSubset) {
            this.fDTDDecl.append("]>");
        }
        return this.fDTDDecl;
    }
    
    public String getCharacterEncodingScheme() {
        return this.fDeclaredEncoding;
    }
    
    @Override
    public int next() throws IOException, XNIException {
        return this.fDriver.next();
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.fNamespaceContext;
    }
    
    @Override
    protected Driver createContentDriver() {
        return new ContentDriver();
    }
    
    protected boolean scanDoctypeDecl(final boolean supportDTD) throws IOException, XNIException {
        if (!this.fEntityScanner.skipSpaces()) {
            this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ROOT_ELEMENT_TYPE_IN_DOCTYPEDECL", null);
        }
        this.fDoctypeName = this.fEntityScanner.scanName(NameType.DOCTYPE);
        if (this.fDoctypeName == null) {
            this.reportFatalError("MSG_ROOT_ELEMENT_TYPE_REQUIRED", null);
        }
        if (this.fEntityScanner.skipSpaces()) {
            this.scanExternalID(this.fStrings, false);
            this.fDoctypeSystemId = this.fStrings[0];
            this.fDoctypePublicId = this.fStrings[1];
            this.fEntityScanner.skipSpaces();
        }
        this.fHasExternalDTD = (this.fDoctypeSystemId != null);
        if (supportDTD && !this.fHasExternalDTD && this.fExternalSubsetResolver != null) {
            this.fDTDDescription.setValues(null, null, this.fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
            this.fDTDDescription.setRootName(this.fDoctypeName);
            this.fExternalSubsetSource = this.fExternalSubsetResolver.getExternalSubset(this.fDTDDescription);
            this.fHasExternalDTD = (this.fExternalSubsetSource != null);
        }
        if (supportDTD && this.fDocumentHandler != null) {
            if (this.fExternalSubsetSource == null) {
                this.fDocumentHandler.doctypeDecl(this.fDoctypeName, this.fDoctypePublicId, this.fDoctypeSystemId, null);
            }
            else {
                this.fDocumentHandler.doctypeDecl(this.fDoctypeName, this.fExternalSubsetSource.getPublicId(), this.fExternalSubsetSource.getSystemId(), null);
            }
        }
        boolean internalSubset = true;
        if (!this.fEntityScanner.skipChar(91, null)) {
            internalSubset = false;
            this.fEntityScanner.skipSpaces();
            if (!this.fEntityScanner.skipChar(62, null)) {
                this.reportFatalError("DoctypedeclUnterminated", new Object[] { this.fDoctypeName });
            }
            --this.fMarkupDepth;
        }
        return internalSubset;
    }
    
    protected void setEndDTDScanState() {
        this.setScannerState(43);
        this.setDriver(this.fPrologDriver);
        this.fEntityManager.setEntityHandler(this);
        this.fReadingDTD = false;
    }
    
    @Override
    protected String getScannerStateName(final int state) {
        switch (state) {
            case 42: {
                return "SCANNER_STATE_XML_DECL";
            }
            case 43: {
                return "SCANNER_STATE_PROLOG";
            }
            case 44: {
                return "SCANNER_STATE_TRAILING_MISC";
            }
            case 45: {
                return "SCANNER_STATE_DTD_INTERNAL_DECLS";
            }
            case 46: {
                return "SCANNER_STATE_DTD_EXTERNAL";
            }
            case 47: {
                return "SCANNER_STATE_DTD_EXTERNAL_DECLS";
            }
            default: {
                return super.getScannerStateName(state);
            }
        }
    }
    
    @Override
    public void refresh(final int refreshPosition) {
        super.refresh(refreshPosition);
        if (this.fReadingDTD) {
            final Entity entity = this.fEntityScanner.getCurrentEntity();
            if (entity instanceof Entity.ScannedEntity) {
                this.fEndPos = ((Entity.ScannedEntity)entity).position;
            }
            this.fDTDDecl.append(((Entity.ScannedEntity)entity).ch, this.fStartPos, this.fEndPos - this.fStartPos);
            this.fStartPos = refreshPosition;
        }
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://apache.org/xml/features/disallow-doctype-decl" };
        FEATURE_DEFAULTS = new Boolean[] { Boolean.TRUE, Boolean.FALSE };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/validation-manager" };
        PROPERTY_DEFAULTS = new Object[] { null, null };
        DOCTYPE = new char[] { 'D', 'O', 'C', 'T', 'Y', 'P', 'E' };
        COMMENTSTRING = new char[] { '-', '-' };
    }
    
    protected final class XMLDeclDriver implements Driver
    {
        @Override
        public int next() throws IOException, XNIException {
            XMLDocumentScannerImpl.this.setScannerState(43);
            XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fPrologDriver);
            try {
                if (!XMLDocumentScannerImpl.this.fEntityScanner.skipString(XMLDocumentFragmentScannerImpl.xmlDecl)) {
                    XMLDocumentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
                    return 7;
                }
                final XMLDocumentScannerImpl this$0 = XMLDocumentScannerImpl.this;
                ++this$0.fMarkupDepth;
                if (XMLChar.isName(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                    XMLDocumentScannerImpl.this.fStringBuffer.clear();
                    XMLDocumentScannerImpl.this.fStringBuffer.append("xml");
                    while (XMLChar.isName(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                        XMLDocumentScannerImpl.this.fStringBuffer.append((char)XMLDocumentScannerImpl.this.fEntityScanner.scanChar(null));
                    }
                    final String target = XMLDocumentScannerImpl.this.fSymbolTable.addSymbol(XMLDocumentScannerImpl.this.fStringBuffer.ch, XMLDocumentScannerImpl.this.fStringBuffer.offset, XMLDocumentScannerImpl.this.fStringBuffer.length);
                    XMLDocumentScannerImpl.this.fContentBuffer.clear();
                    XMLDocumentScannerImpl.this.scanPIData(target, XMLDocumentScannerImpl.this.fContentBuffer);
                    XMLDocumentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
                    return 3;
                }
                XMLDocumentScannerImpl.this.scanXMLDeclOrTextDecl(false);
                XMLDocumentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
                return 7;
            }
            catch (final EOFException e) {
                XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
                return -1;
            }
        }
    }
    
    protected final class PrologDriver implements Driver
    {
        @Override
        public int next() throws IOException, XNIException {
            try {
                do {
                    switch (XMLDocumentScannerImpl.this.fScannerState) {
                        case 43: {
                            XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(60, null)) {
                                XMLDocumentScannerImpl.this.setScannerState(21);
                                continue;
                            }
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(38, NameType.REFERENCE)) {
                                XMLDocumentScannerImpl.this.setScannerState(28);
                                continue;
                            }
                            XMLDocumentScannerImpl.this.setScannerState(22);
                            continue;
                        }
                        case 21: {
                            final XMLDocumentScannerImpl this$0 = XMLDocumentScannerImpl.this;
                            ++this$0.fMarkupDepth;
                            if (XMLDocumentScannerImpl.this.isValidNameStartChar(XMLDocumentScannerImpl.this.fEntityScanner.peekChar()) || XMLDocumentScannerImpl.this.isValidNameStartHighSurrogate(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                                XMLDocumentScannerImpl.this.setScannerState(26);
                                XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
                                return XMLDocumentScannerImpl.this.fContentDriver.next();
                            }
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(33, null)) {
                                if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(45, null)) {
                                    if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(45, null)) {
                                        XMLDocumentScannerImpl.this.reportFatalError("InvalidCommentStart", null);
                                    }
                                    XMLDocumentScannerImpl.this.setScannerState(27);
                                    continue;
                                }
                                if (XMLDocumentScannerImpl.this.fEntityScanner.skipString(XMLDocumentScannerImpl.DOCTYPE)) {
                                    XMLDocumentScannerImpl.this.setScannerState(24);
                                    final Entity entity = XMLDocumentScannerImpl.this.fEntityScanner.getCurrentEntity();
                                    if (entity instanceof Entity.ScannedEntity) {
                                        XMLDocumentScannerImpl.this.fStartPos = ((Entity.ScannedEntity)entity).position;
                                    }
                                    XMLDocumentScannerImpl.this.fReadingDTD = true;
                                    if (XMLDocumentScannerImpl.this.fDTDDecl == null) {
                                        XMLDocumentScannerImpl.this.fDTDDecl = new XMLStringBuffer();
                                    }
                                    XMLDocumentScannerImpl.this.fDTDDecl.append("<!DOCTYPE");
                                    continue;
                                }
                                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInProlog", null);
                                continue;
                            }
                            else {
                                if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(63, null)) {
                                    XMLDocumentScannerImpl.this.setScannerState(23);
                                    continue;
                                }
                                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInProlog", null);
                                continue;
                            }
                            break;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (XMLDocumentScannerImpl.this.fScannerState == 43 || XMLDocumentScannerImpl.this.fScannerState == 21);
                switch (XMLDocumentScannerImpl.this.fScannerState) {
                    case 27: {
                        XMLDocumentScannerImpl.this.scanComment();
                        XMLDocumentScannerImpl.this.setScannerState(43);
                        return 5;
                    }
                    case 23: {
                        XMLDocumentScannerImpl.this.fContentBuffer.clear();
                        XMLDocumentScannerImpl.this.scanPI(XMLDocumentScannerImpl.this.fContentBuffer);
                        XMLDocumentScannerImpl.this.setScannerState(43);
                        return 3;
                    }
                    case 24: {
                        if (XMLDocumentScannerImpl.this.fDisallowDoctype) {
                            XMLDocumentScannerImpl.this.reportFatalError("DoctypeNotAllowed", null);
                        }
                        if (XMLDocumentScannerImpl.this.fSeenDoctypeDecl) {
                            XMLDocumentScannerImpl.this.reportFatalError("AlreadySeenDoctype", null);
                        }
                        XMLDocumentScannerImpl.this.fSeenDoctypeDecl = true;
                        if (XMLDocumentScannerImpl.this.scanDoctypeDecl(XMLDocumentScannerImpl.this.fSupportDTD)) {
                            XMLDocumentScannerImpl.this.setScannerState(45);
                            XMLDocumentScannerImpl.this.fSeenInternalSubset = true;
                            if (XMLDocumentScannerImpl.this.fDTDDriver == null) {
                                XMLDocumentScannerImpl.this.fDTDDriver = new DTDDriver();
                            }
                            XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
                            return XMLDocumentScannerImpl.this.fDTDDriver.next();
                        }
                        if (XMLDocumentScannerImpl.this.fSeenDoctypeDecl) {
                            final Entity entity = XMLDocumentScannerImpl.this.fEntityScanner.getCurrentEntity();
                            if (entity instanceof Entity.ScannedEntity) {
                                XMLDocumentScannerImpl.this.fEndPos = ((Entity.ScannedEntity)entity).position;
                            }
                            XMLDocumentScannerImpl.this.fReadingDTD = false;
                        }
                        if (XMLDocumentScannerImpl.this.fDoctypeSystemId != null) {
                            if ((XMLDocumentScannerImpl.this.fValidation || XMLDocumentScannerImpl.this.fLoadExternalDTD) && (XMLDocumentScannerImpl.this.fValidationManager == null || !XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD())) {
                                if (XMLDocumentScannerImpl.this.fSupportDTD) {
                                    XMLDocumentScannerImpl.this.setScannerState(46);
                                }
                                else {
                                    XMLDocumentScannerImpl.this.setScannerState(43);
                                }
                                XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
                                if (XMLDocumentScannerImpl.this.fDTDDriver == null) {
                                    XMLDocumentScannerImpl.this.fDTDDriver = new DTDDriver();
                                }
                                return XMLDocumentScannerImpl.this.fDTDDriver.next();
                            }
                        }
                        else if (XMLDocumentScannerImpl.this.fExternalSubsetSource != null && (XMLDocumentScannerImpl.this.fValidation || XMLDocumentScannerImpl.this.fLoadExternalDTD) && (XMLDocumentScannerImpl.this.fValidationManager == null || !XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD())) {
                            XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(XMLDocumentScannerImpl.this.fExternalSubsetSource);
                            XMLDocumentScannerImpl.this.fExternalSubsetSource = null;
                            if (XMLDocumentScannerImpl.this.fSupportDTD) {
                                XMLDocumentScannerImpl.this.setScannerState(47);
                            }
                            else {
                                XMLDocumentScannerImpl.this.setScannerState(43);
                            }
                            XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fContentDriver);
                            if (XMLDocumentScannerImpl.this.fDTDDriver == null) {
                                XMLDocumentScannerImpl.this.fDTDDriver = new DTDDriver();
                            }
                            return XMLDocumentScannerImpl.this.fDTDDriver.next();
                        }
                        if (XMLDocumentScannerImpl.this.fDTDScanner != null) {
                            XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(null);
                        }
                        XMLDocumentScannerImpl.this.setScannerState(43);
                        return 11;
                    }
                    case 22: {
                        XMLDocumentScannerImpl.this.reportFatalError("ContentIllegalInProlog", null);
                        XMLDocumentScannerImpl.this.fEntityScanner.scanChar(null);
                    }
                    case 28: {
                        XMLDocumentScannerImpl.this.reportFatalError("ReferenceIllegalInProlog", null);
                        break;
                    }
                }
            }
            catch (final EOFException e) {
                XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
                return -1;
            }
            return -1;
        }
    }
    
    protected final class DTDDriver implements Driver
    {
        @Override
        public int next() throws IOException, XNIException {
            this.dispatch(true);
            if (XMLDocumentScannerImpl.this.fPropertyManager != null) {
                XMLDocumentScannerImpl.this.dtdGrammarUtil = new DTDGrammarUtil(((XMLDTDScannerImpl)XMLDocumentScannerImpl.this.fDTDScanner).getGrammar(), XMLDocumentScannerImpl.this.fSymbolTable, XMLDocumentScannerImpl.this.fNamespaceContext);
            }
            return 11;
        }
        
        public boolean dispatch(final boolean complete) throws IOException, XNIException {
            XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(null);
            try {
                final XMLResourceIdentifierImpl resourceIdentifier = new XMLResourceIdentifierImpl();
                if (XMLDocumentScannerImpl.this.fDTDScanner == null) {
                    if (XMLDocumentScannerImpl.this.fEntityManager.getEntityScanner() instanceof XML11EntityScanner) {
                        XMLDocumentScannerImpl.this.fDTDScanner = new XML11DTDScannerImpl();
                    }
                    else {
                        XMLDocumentScannerImpl.this.fDTDScanner = new XMLDTDScannerImpl();
                    }
                    ((XMLDTDScannerImpl)XMLDocumentScannerImpl.this.fDTDScanner).reset(XMLDocumentScannerImpl.this.fPropertyManager);
                }
                XMLDocumentScannerImpl.this.fDTDScanner.setLimitAnalyzer(XMLDocumentScannerImpl.this.fLimitAnalyzer);
                boolean again;
                do {
                    again = false;
                    switch (XMLDocumentScannerImpl.this.fScannerState) {
                        case 45: {
                            boolean moreToScan = false;
                            if (!XMLDocumentScannerImpl.this.fDTDScanner.skipDTD(XMLDocumentScannerImpl.this.fSupportDTD)) {
                                final boolean completeDTD = true;
                                moreToScan = XMLDocumentScannerImpl.this.fDTDScanner.scanDTDInternalSubset(completeDTD, XMLDocumentScannerImpl.this.fStandalone, XMLDocumentScannerImpl.this.fHasExternalDTD && XMLDocumentScannerImpl.this.fLoadExternalDTD);
                            }
                            final Entity entity = XMLDocumentScannerImpl.this.fEntityScanner.getCurrentEntity();
                            if (entity instanceof Entity.ScannedEntity) {
                                XMLDocumentScannerImpl.this.fEndPos = ((Entity.ScannedEntity)entity).position;
                            }
                            XMLDocumentScannerImpl.this.fReadingDTD = false;
                            if (!moreToScan) {
                                if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(93, null)) {
                                    XMLDocumentScannerImpl.this.reportFatalError("DoctypedeclNotClosed", new Object[] { XMLDocumentScannerImpl.this.fDoctypeName });
                                }
                                XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
                                if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(62, null)) {
                                    XMLDocumentScannerImpl.this.reportFatalError("DoctypedeclUnterminated", new Object[] { XMLDocumentScannerImpl.this.fDoctypeName });
                                }
                                final XMLDocumentScannerImpl this$0 = XMLDocumentScannerImpl.this;
                                --this$0.fMarkupDepth;
                                if (!XMLDocumentScannerImpl.this.fSupportDTD) {
                                    (XMLDocumentScannerImpl.this.fEntityStore = XMLDocumentScannerImpl.this.fEntityManager.getEntityStore()).reset();
                                }
                                else if (XMLDocumentScannerImpl.this.fDoctypeSystemId != null && (XMLDocumentScannerImpl.this.fValidation || XMLDocumentScannerImpl.this.fLoadExternalDTD)) {
                                    XMLDocumentScannerImpl.this.setScannerState(46);
                                    continue;
                                }
                                XMLDocumentScannerImpl.this.setEndDTDScanState();
                                return true;
                            }
                            continue;
                        }
                        case 46: {
                            resourceIdentifier.setValues(XMLDocumentScannerImpl.this.fDoctypePublicId, XMLDocumentScannerImpl.this.fDoctypeSystemId, null, null);
                            XMLInputSource xmlInputSource = null;
                            final StaxXMLInputSource staxInputSource = XMLDocumentScannerImpl.this.fEntityManager.resolveEntityAsPerStax(resourceIdentifier);
                            if (!staxInputSource.hasResolver()) {
                                final String accessError = XMLDocumentScannerImpl.this.checkAccess(XMLDocumentScannerImpl.this.fDoctypeSystemId, XMLDocumentScannerImpl.this.fAccessExternalDTD);
                                if (accessError != null) {
                                    XMLDocumentScannerImpl.this.reportFatalError("AccessExternalDTD", new Object[] { SecuritySupport.sanitizePath(XMLDocumentScannerImpl.this.fDoctypeSystemId), accessError });
                                }
                            }
                            xmlInputSource = staxInputSource.getXMLInputSource();
                            XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(xmlInputSource);
                            if (XMLDocumentScannerImpl.this.fEntityScanner.fCurrentEntity != null) {
                                XMLDocumentScannerImpl.this.setScannerState(47);
                            }
                            else {
                                XMLDocumentScannerImpl.this.setScannerState(43);
                            }
                            again = true;
                            continue;
                        }
                        case 47: {
                            final boolean completeDTD2 = true;
                            final boolean moreToScan2 = XMLDocumentScannerImpl.this.fDTDScanner.scanDTDExternalSubset(completeDTD2);
                            if (!moreToScan2) {
                                XMLDocumentScannerImpl.this.setEndDTDScanState();
                                return true;
                            }
                            continue;
                        }
                        case 43: {
                            XMLDocumentScannerImpl.this.setEndDTDScanState();
                            return true;
                        }
                        default: {
                            throw new XNIException("DTDDriver#dispatch: scanner state=" + XMLDocumentScannerImpl.this.fScannerState + " (" + XMLDocumentScannerImpl.this.getScannerStateName(XMLDocumentScannerImpl.this.fScannerState) + ')');
                        }
                    }
                } while (complete || again);
            }
            catch (final EOFException e) {
                e.printStackTrace();
                XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
                return false;
            }
            finally {
                XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
            }
            return true;
        }
    }
    
    protected class ContentDriver extends FragmentContentDriver
    {
        @Override
        protected boolean scanForDoctypeHook() throws IOException, XNIException {
            if (XMLDocumentScannerImpl.this.fEntityScanner.skipString(XMLDocumentScannerImpl.DOCTYPE)) {
                XMLDocumentScannerImpl.this.setScannerState(24);
                return true;
            }
            return false;
        }
        
        @Override
        protected boolean elementDepthIsZeroHook() throws IOException, XNIException {
            XMLDocumentScannerImpl.this.setScannerState(44);
            XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fTrailingMiscDriver);
            return true;
        }
        
        @Override
        protected boolean scanRootElementHook() throws IOException, XNIException {
            if (XMLDocumentScannerImpl.this.scanStartElement()) {
                XMLDocumentScannerImpl.this.setScannerState(44);
                XMLDocumentScannerImpl.this.setDriver(XMLDocumentScannerImpl.this.fTrailingMiscDriver);
                return true;
            }
            return false;
        }
        
        @Override
        protected void endOfFileHook(final EOFException e) throws IOException, XNIException {
            XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
        }
        
        protected void resolveExternalSubsetAndRead() throws IOException, XNIException {
            XMLDocumentScannerImpl.this.fDTDDescription.setValues(null, null, XMLDocumentScannerImpl.this.fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
            XMLDocumentScannerImpl.this.fDTDDescription.setRootName(XMLDocumentScannerImpl.this.fElementQName.rawname);
            final XMLInputSource src = XMLDocumentScannerImpl.this.fExternalSubsetResolver.getExternalSubset(XMLDocumentScannerImpl.this.fDTDDescription);
            if (src != null) {
                XMLDocumentScannerImpl.this.fDoctypeName = XMLDocumentScannerImpl.this.fElementQName.rawname;
                XMLDocumentScannerImpl.this.fDoctypePublicId = src.getPublicId();
                XMLDocumentScannerImpl.this.fDoctypeSystemId = src.getSystemId();
                if (XMLDocumentScannerImpl.this.fDocumentHandler != null) {
                    XMLDocumentScannerImpl.this.fDocumentHandler.doctypeDecl(XMLDocumentScannerImpl.this.fDoctypeName, XMLDocumentScannerImpl.this.fDoctypePublicId, XMLDocumentScannerImpl.this.fDoctypeSystemId, null);
                }
                try {
                    XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(src);
                    while (XMLDocumentScannerImpl.this.fDTDScanner.scanDTDExternalSubset(true)) {}
                }
                finally {
                    XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
                }
            }
        }
    }
    
    protected final class TrailingMiscDriver implements Driver
    {
        @Override
        public int next() throws IOException, XNIException {
            if (XMLDocumentScannerImpl.this.fEmptyElement) {
                XMLDocumentScannerImpl.this.fEmptyElement = false;
                return 2;
            }
            try {
                if (XMLDocumentScannerImpl.this.fScannerState == 34) {
                    return 8;
                }
                do {
                    switch (XMLDocumentScannerImpl.this.fScannerState) {
                        case 44: {
                            XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
                            if (XMLDocumentScannerImpl.this.fScannerState == 34) {
                                return 8;
                            }
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(60, null)) {
                                XMLDocumentScannerImpl.this.setScannerState(21);
                                continue;
                            }
                            XMLDocumentScannerImpl.this.setScannerState(22);
                            continue;
                        }
                        case 21: {
                            final XMLDocumentScannerImpl this$0 = XMLDocumentScannerImpl.this;
                            ++this$0.fMarkupDepth;
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(63, null)) {
                                XMLDocumentScannerImpl.this.setScannerState(23);
                                continue;
                            }
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(33, null)) {
                                XMLDocumentScannerImpl.this.setScannerState(27);
                                continue;
                            }
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(47, null)) {
                                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                                continue;
                            }
                            if (XMLDocumentScannerImpl.this.isValidNameStartChar(XMLDocumentScannerImpl.this.fEntityScanner.peekChar()) || XMLDocumentScannerImpl.this.isValidNameStartHighSurrogate(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                                XMLDocumentScannerImpl.this.scanStartElement();
                                XMLDocumentScannerImpl.this.setScannerState(22);
                                continue;
                            }
                            XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (XMLDocumentScannerImpl.this.fScannerState == 21 || XMLDocumentScannerImpl.this.fScannerState == 44);
                switch (XMLDocumentScannerImpl.this.fScannerState) {
                    case 23: {
                        XMLDocumentScannerImpl.this.fContentBuffer.clear();
                        XMLDocumentScannerImpl.this.scanPI(XMLDocumentScannerImpl.this.fContentBuffer);
                        XMLDocumentScannerImpl.this.setScannerState(44);
                        return 3;
                    }
                    case 27: {
                        if (!XMLDocumentScannerImpl.this.fEntityScanner.skipString(XMLDocumentScannerImpl.COMMENTSTRING)) {
                            XMLDocumentScannerImpl.this.reportFatalError("InvalidCommentStart", null);
                        }
                        XMLDocumentScannerImpl.this.scanComment();
                        XMLDocumentScannerImpl.this.setScannerState(44);
                        return 5;
                    }
                    case 22: {
                        final int ch = XMLDocumentScannerImpl.this.fEntityScanner.peekChar();
                        if (ch == -1) {
                            XMLDocumentScannerImpl.this.setScannerState(34);
                            return 8;
                        }
                        XMLDocumentScannerImpl.this.reportFatalError("ContentIllegalInTrailingMisc", null);
                        XMLDocumentScannerImpl.this.fEntityScanner.scanChar(null);
                        XMLDocumentScannerImpl.this.setScannerState(44);
                        return 4;
                    }
                    case 28: {
                        XMLDocumentScannerImpl.this.reportFatalError("ReferenceIllegalInTrailingMisc", null);
                        XMLDocumentScannerImpl.this.setScannerState(44);
                        return 9;
                    }
                    case 34: {
                        XMLDocumentScannerImpl.this.setScannerState(48);
                        return 8;
                    }
                    case 48: {
                        throw new NoSuchElementException("No more events to be parsed");
                    }
                    default: {
                        throw new XNIException("Scanner State " + XMLDocumentScannerImpl.this.fScannerState + " not Recognized ");
                    }
                }
            }
            catch (final EOFException e) {
                if (XMLDocumentScannerImpl.this.fMarkupDepth != 0) {
                    XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
                    return -1;
                }
                XMLDocumentScannerImpl.this.setScannerState(34);
                return 8;
            }
        }
    }
}
