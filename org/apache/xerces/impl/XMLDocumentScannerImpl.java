package org.apache.xerces.impl;

import org.apache.xerces.util.XMLChar;
import java.io.CharConversionException;
import org.apache.xerces.impl.io.MalformedByteSequenceException;
import java.io.EOFException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import java.io.IOException;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.impl.dtd.XMLDTDDescription;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.xni.parser.XMLDTDScanner;

public class XMLDocumentScannerImpl extends XMLDocumentFragmentScannerImpl
{
    protected static final int SCANNER_STATE_XML_DECL = 0;
    protected static final int SCANNER_STATE_PROLOG = 5;
    protected static final int SCANNER_STATE_TRAILING_MISC = 12;
    protected static final int SCANNER_STATE_DTD_INTERNAL_DECLS = 17;
    protected static final int SCANNER_STATE_DTD_EXTERNAL = 18;
    protected static final int SCANNER_STATE_DTD_EXTERNAL_DECLS = 19;
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
    protected boolean fScanningDTD;
    protected String fDoctypeName;
    protected String fDoctypePublicId;
    protected String fDoctypeSystemId;
    protected NamespaceContext fNamespaceContext;
    protected boolean fLoadExternalDTD;
    protected boolean fDisallowDoctype;
    protected boolean fSeenDoctypeDecl;
    protected final Dispatcher fXMLDeclDispatcher;
    protected final Dispatcher fPrologDispatcher;
    protected final Dispatcher fDTDDispatcher;
    protected final Dispatcher fTrailingMiscDispatcher;
    private final String[] fStrings;
    private final XMLString fString;
    private final XMLStringBuffer fStringBuffer;
    private XMLInputSource fExternalSubsetSource;
    private final XMLDTDDescription fDTDDescription;
    
    public XMLDocumentScannerImpl() {
        this.fNamespaceContext = new NamespaceSupport();
        this.fLoadExternalDTD = true;
        this.fDisallowDoctype = false;
        this.fXMLDeclDispatcher = new XMLDeclDispatcher();
        this.fPrologDispatcher = new PrologDispatcher();
        this.fDTDDispatcher = new DTDDispatcher();
        this.fTrailingMiscDispatcher = new TrailingMiscDispatcher();
        this.fStrings = new String[3];
        this.fString = new XMLString();
        this.fStringBuffer = new XMLStringBuffer();
        this.fExternalSubsetSource = null;
        this.fDTDDescription = new XMLDTDDescription(null, null, null, null, null);
    }
    
    public void setInputSource(final XMLInputSource xmlInputSource) throws IOException {
        this.fEntityManager.setEntityHandler(this);
        this.fEntityManager.startDocumentEntity(xmlInputSource);
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) throws XMLConfigurationException {
        super.reset(xmlComponentManager);
        this.fDoctypeName = null;
        this.fDoctypePublicId = null;
        this.fDoctypeSystemId = null;
        this.fSeenDoctypeDecl = false;
        this.fScanningDTD = false;
        this.fExternalSubsetSource = null;
        if (!this.fParserSettings) {
            this.fNamespaceContext.reset();
            this.setScannerState(0);
            this.setDispatcher(this.fXMLDeclDispatcher);
            return;
        }
        try {
            this.fLoadExternalDTD = xmlComponentManager.getFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd");
        }
        catch (final XMLConfigurationException ex) {
            this.fLoadExternalDTD = true;
        }
        try {
            this.fDisallowDoctype = xmlComponentManager.getFeature("http://apache.org/xml/features/disallow-doctype-decl");
        }
        catch (final XMLConfigurationException ex2) {
            this.fDisallowDoctype = false;
        }
        this.fDTDScanner = (XMLDTDScanner)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/dtd-scanner");
        try {
            this.fValidationManager = (ValidationManager)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/validation-manager");
        }
        catch (final XMLConfigurationException ex3) {
            this.fValidationManager = null;
        }
        try {
            this.fNamespaceContext = (NamespaceContext)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/namespace-context");
        }
        catch (final XMLConfigurationException ex4) {}
        if (this.fNamespaceContext == null) {
            this.fNamespaceContext = new NamespaceSupport();
        }
        this.fNamespaceContext.reset();
        this.setScannerState(0);
        this.setDispatcher(this.fXMLDeclDispatcher);
    }
    
    public String[] getRecognizedFeatures() {
        final String[] recognizedFeatures = super.getRecognizedFeatures();
        final int n = (recognizedFeatures != null) ? recognizedFeatures.length : 0;
        final String[] array = new String[n + XMLDocumentScannerImpl.RECOGNIZED_FEATURES.length];
        if (recognizedFeatures != null) {
            System.arraycopy(recognizedFeatures, 0, array, 0, recognizedFeatures.length);
        }
        System.arraycopy(XMLDocumentScannerImpl.RECOGNIZED_FEATURES, 0, array, n, XMLDocumentScannerImpl.RECOGNIZED_FEATURES.length);
        return array;
    }
    
    public void setFeature(final String s, final boolean b) throws XMLConfigurationException {
        super.setFeature(s, b);
        if (s.startsWith("http://apache.org/xml/features/")) {
            final int n = s.length() - "http://apache.org/xml/features/".length();
            if (n == "nonvalidating/load-external-dtd".length() && s.endsWith("nonvalidating/load-external-dtd")) {
                this.fLoadExternalDTD = b;
                return;
            }
            if (n == "disallow-doctype-decl".length() && s.endsWith("disallow-doctype-decl")) {
                this.fDisallowDoctype = b;
            }
        }
    }
    
    public String[] getRecognizedProperties() {
        final String[] recognizedProperties = super.getRecognizedProperties();
        final int n = (recognizedProperties != null) ? recognizedProperties.length : 0;
        final String[] array = new String[n + XMLDocumentScannerImpl.RECOGNIZED_PROPERTIES.length];
        if (recognizedProperties != null) {
            System.arraycopy(recognizedProperties, 0, array, 0, recognizedProperties.length);
        }
        System.arraycopy(XMLDocumentScannerImpl.RECOGNIZED_PROPERTIES, 0, array, n, XMLDocumentScannerImpl.RECOGNIZED_PROPERTIES.length);
        return array;
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        super.setProperty(s, o);
        if (s.startsWith("http://apache.org/xml/properties/")) {
            final int n = s.length() - "http://apache.org/xml/properties/".length();
            if (n == "internal/dtd-scanner".length() && s.endsWith("internal/dtd-scanner")) {
                this.fDTDScanner = (XMLDTDScanner)o;
            }
            if (n == "internal/namespace-context".length() && s.endsWith("internal/namespace-context") && o != null) {
                this.fNamespaceContext = (NamespaceContext)o;
            }
        }
    }
    
    public Boolean getFeatureDefault(final String s) {
        for (int i = 0; i < XMLDocumentScannerImpl.RECOGNIZED_FEATURES.length; ++i) {
            if (XMLDocumentScannerImpl.RECOGNIZED_FEATURES[i].equals(s)) {
                return XMLDocumentScannerImpl.FEATURE_DEFAULTS[i];
            }
        }
        return super.getFeatureDefault(s);
    }
    
    public Object getPropertyDefault(final String s) {
        for (int i = 0; i < XMLDocumentScannerImpl.RECOGNIZED_PROPERTIES.length; ++i) {
            if (XMLDocumentScannerImpl.RECOGNIZED_PROPERTIES[i].equals(s)) {
                return XMLDocumentScannerImpl.PROPERTY_DEFAULTS[i];
            }
        }
        return super.getPropertyDefault(s);
    }
    
    public void startEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        super.startEntity(s, xmlResourceIdentifier, s2, augmentations);
        if (!s.equals("[xml]") && this.fEntityScanner.isExternal()) {
            this.setScannerState(16);
        }
        if (this.fDocumentHandler != null && s.equals("[xml]")) {
            this.fDocumentHandler.startDocument(this.fEntityScanner, s2, this.fNamespaceContext, null);
        }
    }
    
    public void endEntity(final String s, final Augmentations augmentations) throws XNIException {
        super.endEntity(s, augmentations);
        if (this.fDocumentHandler != null && s.equals("[xml]")) {
            this.fDocumentHandler.endDocument(null);
        }
    }
    
    protected Dispatcher createContentDispatcher() {
        return new ContentDispatcher();
    }
    
    protected boolean scanDoctypeDecl() throws IOException, XNIException {
        if (!this.fEntityScanner.skipSpaces()) {
            this.reportFatalError("MSG_SPACE_REQUIRED_BEFORE_ROOT_ELEMENT_TYPE_IN_DOCTYPEDECL", null);
        }
        this.fDoctypeName = this.fEntityScanner.scanName();
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
        if (!this.fHasExternalDTD && this.fExternalSubsetResolver != null) {
            this.fDTDDescription.setValues(null, null, this.fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
            this.fDTDDescription.setRootName(this.fDoctypeName);
            this.fExternalSubsetSource = this.fExternalSubsetResolver.getExternalSubset(this.fDTDDescription);
            this.fHasExternalDTD = (this.fExternalSubsetSource != null);
        }
        if (this.fDocumentHandler != null) {
            if (this.fExternalSubsetSource == null) {
                this.fDocumentHandler.doctypeDecl(this.fDoctypeName, this.fDoctypePublicId, this.fDoctypeSystemId, null);
            }
            else {
                this.fDocumentHandler.doctypeDecl(this.fDoctypeName, this.fExternalSubsetSource.getPublicId(), this.fExternalSubsetSource.getSystemId(), null);
            }
        }
        boolean b = true;
        if (!this.fEntityScanner.skipChar(91)) {
            b = false;
            this.fEntityScanner.skipSpaces();
            if (!this.fEntityScanner.skipChar(62)) {
                this.reportFatalError("DoctypedeclUnterminated", new Object[] { this.fDoctypeName });
            }
            --this.fMarkupDepth;
        }
        return b;
    }
    
    protected String getScannerStateName(final int n) {
        switch (n) {
            case 0: {
                return "SCANNER_STATE_XML_DECL";
            }
            case 5: {
                return "SCANNER_STATE_PROLOG";
            }
            case 12: {
                return "SCANNER_STATE_TRAILING_MISC";
            }
            case 17: {
                return "SCANNER_STATE_DTD_INTERNAL_DECLS";
            }
            case 18: {
                return "SCANNER_STATE_DTD_EXTERNAL";
            }
            case 19: {
                return "SCANNER_STATE_DTD_EXTERNAL_DECLS";
            }
            default: {
                return super.getScannerStateName(n);
            }
        }
    }
    
    static {
        RECOGNIZED_FEATURES = new String[] { "http://apache.org/xml/features/nonvalidating/load-external-dtd", "http://apache.org/xml/features/disallow-doctype-decl" };
        FEATURE_DEFAULTS = new Boolean[] { Boolean.TRUE, Boolean.FALSE };
        RECOGNIZED_PROPERTIES = new String[] { "http://apache.org/xml/properties/internal/dtd-scanner", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/internal/namespace-context" };
        PROPERTY_DEFAULTS = new Object[] { null, null, null };
    }
    
    protected class ContentDispatcher extends FragmentContentDispatcher
    {
        protected boolean scanForDoctypeHook() throws IOException, XNIException {
            if (XMLDocumentScannerImpl.this.fEntityScanner.skipString("DOCTYPE")) {
                XMLDocumentScannerImpl.this.setScannerState(4);
                return true;
            }
            return false;
        }
        
        protected boolean elementDepthIsZeroHook() throws IOException, XNIException {
            XMLDocumentScannerImpl.this.setScannerState(12);
            XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fTrailingMiscDispatcher);
            return true;
        }
        
        protected boolean scanRootElementHook() throws IOException, XNIException {
            if (XMLDocumentScannerImpl.this.fExternalSubsetResolver != null && !XMLDocumentScannerImpl.this.fSeenDoctypeDecl && !XMLDocumentScannerImpl.this.fDisallowDoctype && (XMLDocumentScannerImpl.this.fValidation || XMLDocumentScannerImpl.this.fLoadExternalDTD)) {
                XMLDocumentScannerImpl.this.scanStartElementName();
                this.resolveExternalSubsetAndRead();
                if (XMLDocumentScannerImpl.this.scanStartElementAfterName()) {
                    XMLDocumentScannerImpl.this.setScannerState(12);
                    XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fTrailingMiscDispatcher);
                    return true;
                }
            }
            else if (XMLDocumentScannerImpl.this.scanStartElement()) {
                XMLDocumentScannerImpl.this.setScannerState(12);
                XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fTrailingMiscDispatcher);
                return true;
            }
            return false;
        }
        
        protected void endOfFileHook(final EOFException ex) throws IOException, XNIException {
            XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
        }
        
        protected void resolveExternalSubsetAndRead() throws IOException, XNIException {
            XMLDocumentScannerImpl.this.fDTDDescription.setValues(null, null, XMLDocumentScannerImpl.this.fEntityManager.getCurrentResourceIdentifier().getExpandedSystemId(), null);
            XMLDocumentScannerImpl.this.fDTDDescription.setRootName(XMLDocumentScannerImpl.this.fElementQName.rawname);
            final XMLInputSource externalSubset = XMLDocumentScannerImpl.this.fExternalSubsetResolver.getExternalSubset(XMLDocumentScannerImpl.this.fDTDDescription);
            if (externalSubset != null) {
                XMLDocumentScannerImpl.this.fDoctypeName = XMLDocumentScannerImpl.this.fElementQName.rawname;
                XMLDocumentScannerImpl.this.fDoctypePublicId = externalSubset.getPublicId();
                XMLDocumentScannerImpl.this.fDoctypeSystemId = externalSubset.getSystemId();
                if (XMLDocumentScannerImpl.this.fDocumentHandler != null) {
                    XMLDocumentScannerImpl.this.fDocumentHandler.doctypeDecl(XMLDocumentScannerImpl.this.fDoctypeName, XMLDocumentScannerImpl.this.fDoctypePublicId, XMLDocumentScannerImpl.this.fDoctypeSystemId, null);
                }
                try {
                    if (XMLDocumentScannerImpl.this.fValidationManager == null || !XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD()) {
                        XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(externalSubset);
                        while (XMLDocumentScannerImpl.this.fDTDScanner.scanDTDExternalSubset(true)) {}
                    }
                    else {
                        XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(null);
                    }
                }
                finally {
                    XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
                }
            }
        }
    }
    
    protected final class DTDDispatcher implements Dispatcher
    {
        public boolean dispatch(final boolean b) throws IOException, XNIException {
            XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(null);
            try {
                boolean b2;
                do {
                    b2 = false;
                    switch (XMLDocumentScannerImpl.this.fScannerState) {
                        case 17: {
                            final boolean b3 = true;
                            final boolean b4 = (XMLDocumentScannerImpl.this.fValidation || XMLDocumentScannerImpl.this.fLoadExternalDTD) && (XMLDocumentScannerImpl.this.fValidationManager == null || !XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD());
                            if (!XMLDocumentScannerImpl.this.fDTDScanner.scanDTDInternalSubset(b3, XMLDocumentScannerImpl.this.fStandalone, XMLDocumentScannerImpl.this.fHasExternalDTD && b4)) {
                                if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(93)) {
                                    XMLDocumentScannerImpl.this.reportFatalError("EXPECTED_SQUARE_BRACKET_TO_CLOSE_INTERNAL_SUBSET", null);
                                }
                                XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
                                if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(62)) {
                                    XMLDocumentScannerImpl.this.reportFatalError("DoctypedeclUnterminated", new Object[] { XMLDocumentScannerImpl.this.fDoctypeName });
                                }
                                final XMLDocumentScannerImpl this$0 = XMLDocumentScannerImpl.this;
                                --this$0.fMarkupDepth;
                                if (XMLDocumentScannerImpl.this.fDoctypeSystemId != null) {
                                    XMLDocumentScannerImpl.this.fIsEntityDeclaredVC = !XMLDocumentScannerImpl.this.fStandalone;
                                    if (b4) {
                                        XMLDocumentScannerImpl.this.setScannerState(18);
                                        continue;
                                    }
                                }
                                else if (XMLDocumentScannerImpl.this.fExternalSubsetSource != null) {
                                    XMLDocumentScannerImpl.this.fIsEntityDeclaredVC = !XMLDocumentScannerImpl.this.fStandalone;
                                    if (b4) {
                                        XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(XMLDocumentScannerImpl.this.fExternalSubsetSource);
                                        XMLDocumentScannerImpl.this.fExternalSubsetSource = null;
                                        XMLDocumentScannerImpl.this.setScannerState(19);
                                        continue;
                                    }
                                }
                                else {
                                    XMLDocumentScannerImpl.this.fIsEntityDeclaredVC = (XMLDocumentScannerImpl.this.fEntityManager.hasPEReferences() && !XMLDocumentScannerImpl.this.fStandalone);
                                }
                                XMLDocumentScannerImpl.this.setScannerState(5);
                                XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fPrologDispatcher);
                                XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
                                return true;
                            }
                            continue;
                        }
                        case 18: {
                            XMLDocumentScannerImpl.this.fDTDDescription.setValues(XMLDocumentScannerImpl.this.fDoctypePublicId, XMLDocumentScannerImpl.this.fDoctypeSystemId, null, null);
                            XMLDocumentScannerImpl.this.fDTDDescription.setRootName(XMLDocumentScannerImpl.this.fDoctypeName);
                            XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(XMLDocumentScannerImpl.this.fEntityManager.resolveEntity(XMLDocumentScannerImpl.this.fDTDDescription));
                            XMLDocumentScannerImpl.this.setScannerState(19);
                            b2 = true;
                            continue;
                        }
                        case 19: {
                            if (!XMLDocumentScannerImpl.this.fDTDScanner.scanDTDExternalSubset(true)) {
                                XMLDocumentScannerImpl.this.setScannerState(5);
                                XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fPrologDispatcher);
                                XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
                                return true;
                            }
                            continue;
                        }
                        default: {
                            throw new XNIException("DTDDispatcher#dispatch: scanner state=" + XMLDocumentScannerImpl.this.fScannerState + " (" + XMLDocumentScannerImpl.this.getScannerStateName(XMLDocumentScannerImpl.this.fScannerState) + ')');
                        }
                    }
                } while (b || b2);
            }
            catch (final MalformedByteSequenceException ex) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError(ex.getDomain(), ex.getKey(), ex.getArguments(), (short)2, ex);
                return false;
            }
            catch (final CharConversionException ex2) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2, ex2);
                return false;
            }
            catch (final EOFException ex3) {
                XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
                return false;
            }
            finally {
                XMLDocumentScannerImpl.this.fEntityManager.setEntityHandler(XMLDocumentScannerImpl.this);
            }
            return true;
        }
    }
    
    protected final class PrologDispatcher implements Dispatcher
    {
        public boolean dispatch(final boolean b) throws IOException, XNIException {
            try {
                boolean b2;
                do {
                    b2 = false;
                    switch (XMLDocumentScannerImpl.this.fScannerState) {
                        case 5: {
                            XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(60)) {
                                XMLDocumentScannerImpl.this.setScannerState(1);
                                b2 = true;
                                continue;
                            }
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(38)) {
                                XMLDocumentScannerImpl.this.setScannerState(8);
                                b2 = true;
                                continue;
                            }
                            XMLDocumentScannerImpl.this.setScannerState(7);
                            b2 = true;
                            continue;
                        }
                        case 1: {
                            final XMLDocumentScannerImpl this$0 = XMLDocumentScannerImpl.this;
                            ++this$0.fMarkupDepth;
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(33)) {
                                if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(45)) {
                                    if (!XMLDocumentScannerImpl.this.fEntityScanner.skipChar(45)) {
                                        XMLDocumentScannerImpl.this.reportFatalError("InvalidCommentStart", null);
                                    }
                                    XMLDocumentScannerImpl.this.setScannerState(2);
                                    b2 = true;
                                    continue;
                                }
                                if (XMLDocumentScannerImpl.this.fEntityScanner.skipString("DOCTYPE")) {
                                    XMLDocumentScannerImpl.this.setScannerState(4);
                                    b2 = true;
                                    continue;
                                }
                                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInProlog", null);
                                continue;
                            }
                            else {
                                if (XMLDocumentScannerImpl.this.isValidNameStartChar(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                                    XMLDocumentScannerImpl.this.setScannerState(6);
                                    XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fContentDispatcher);
                                    return true;
                                }
                                if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(63)) {
                                    XMLDocumentScannerImpl.this.setScannerState(3);
                                    b2 = true;
                                    continue;
                                }
                                if (XMLDocumentScannerImpl.this.isValidNameStartHighSurrogate(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                                    XMLDocumentScannerImpl.this.setScannerState(6);
                                    XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fContentDispatcher);
                                    return true;
                                }
                                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInProlog", null);
                                continue;
                            }
                            break;
                        }
                        case 4: {
                            if (XMLDocumentScannerImpl.this.fDisallowDoctype) {
                                XMLDocumentScannerImpl.this.reportFatalError("DoctypeNotAllowed", null);
                            }
                            if (XMLDocumentScannerImpl.this.fSeenDoctypeDecl) {
                                XMLDocumentScannerImpl.this.reportFatalError("AlreadySeenDoctype", null);
                            }
                            XMLDocumentScannerImpl.this.fSeenDoctypeDecl = true;
                            if (XMLDocumentScannerImpl.this.scanDoctypeDecl()) {
                                XMLDocumentScannerImpl.this.setScannerState(17);
                                XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fDTDDispatcher);
                                return true;
                            }
                            if (XMLDocumentScannerImpl.this.fDoctypeSystemId != null) {
                                XMLDocumentScannerImpl.this.fIsEntityDeclaredVC = !XMLDocumentScannerImpl.this.fStandalone;
                                if ((XMLDocumentScannerImpl.this.fValidation || XMLDocumentScannerImpl.this.fLoadExternalDTD) && (XMLDocumentScannerImpl.this.fValidationManager == null || !XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD())) {
                                    XMLDocumentScannerImpl.this.setScannerState(18);
                                    XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fDTDDispatcher);
                                    return true;
                                }
                            }
                            else if (XMLDocumentScannerImpl.this.fExternalSubsetSource != null) {
                                XMLDocumentScannerImpl.this.fIsEntityDeclaredVC = !XMLDocumentScannerImpl.this.fStandalone;
                                if ((XMLDocumentScannerImpl.this.fValidation || XMLDocumentScannerImpl.this.fLoadExternalDTD) && (XMLDocumentScannerImpl.this.fValidationManager == null || !XMLDocumentScannerImpl.this.fValidationManager.isCachedDTD())) {
                                    XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(XMLDocumentScannerImpl.this.fExternalSubsetSource);
                                    XMLDocumentScannerImpl.this.fExternalSubsetSource = null;
                                    XMLDocumentScannerImpl.this.setScannerState(19);
                                    XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fDTDDispatcher);
                                    return true;
                                }
                            }
                            XMLDocumentScannerImpl.this.fDTDScanner.setInputSource(null);
                            XMLDocumentScannerImpl.this.setScannerState(5);
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 2: {
                            XMLDocumentScannerImpl.this.scanComment();
                            XMLDocumentScannerImpl.this.setScannerState(5);
                            continue;
                        }
                        case 3: {
                            XMLDocumentScannerImpl.this.scanPI();
                            XMLDocumentScannerImpl.this.setScannerState(5);
                            continue;
                        }
                        case 7: {
                            XMLDocumentScannerImpl.this.reportFatalError("ContentIllegalInProlog", null);
                            XMLDocumentScannerImpl.this.fEntityScanner.scanChar();
                        }
                        case 8: {
                            XMLDocumentScannerImpl.this.reportFatalError("ReferenceIllegalInProlog", null);
                            continue;
                        }
                    }
                } while (b || b2);
                if (b) {
                    if (XMLDocumentScannerImpl.this.fEntityScanner.scanChar() != 60) {
                        XMLDocumentScannerImpl.this.reportFatalError("RootElementRequired", null);
                    }
                    XMLDocumentScannerImpl.this.setScannerState(6);
                    XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fContentDispatcher);
                }
            }
            catch (final MalformedByteSequenceException ex) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError(ex.getDomain(), ex.getKey(), ex.getArguments(), (short)2, ex);
                return false;
            }
            catch (final CharConversionException ex2) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2, ex2);
                return false;
            }
            catch (final EOFException ex3) {
                XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
                return false;
            }
            return true;
        }
    }
    
    protected final class TrailingMiscDispatcher implements Dispatcher
    {
        public boolean dispatch(final boolean b) throws IOException, XNIException {
            try {
                boolean b2;
                do {
                    b2 = false;
                    switch (XMLDocumentScannerImpl.this.fScannerState) {
                        case 12: {
                            XMLDocumentScannerImpl.this.fEntityScanner.skipSpaces();
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(60)) {
                                XMLDocumentScannerImpl.this.setScannerState(1);
                                b2 = true;
                                continue;
                            }
                            XMLDocumentScannerImpl.this.setScannerState(7);
                            b2 = true;
                            continue;
                        }
                        case 1: {
                            final XMLDocumentScannerImpl this$0 = XMLDocumentScannerImpl.this;
                            ++this$0.fMarkupDepth;
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(63)) {
                                XMLDocumentScannerImpl.this.setScannerState(3);
                                b2 = true;
                                continue;
                            }
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(33)) {
                                XMLDocumentScannerImpl.this.setScannerState(2);
                                b2 = true;
                                continue;
                            }
                            if (XMLDocumentScannerImpl.this.fEntityScanner.skipChar(47)) {
                                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                                b2 = true;
                                continue;
                            }
                            if (XMLDocumentScannerImpl.this.isValidNameStartChar(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                                XMLDocumentScannerImpl.this.scanStartElement();
                                XMLDocumentScannerImpl.this.setScannerState(7);
                                continue;
                            }
                            if (XMLDocumentScannerImpl.this.isValidNameStartHighSurrogate(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                                XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                                XMLDocumentScannerImpl.this.scanStartElement();
                                XMLDocumentScannerImpl.this.setScannerState(7);
                                continue;
                            }
                            XMLDocumentScannerImpl.this.reportFatalError("MarkupNotRecognizedInMisc", null);
                            continue;
                        }
                        case 7: {
                            if (XMLDocumentScannerImpl.this.fEntityScanner.peekChar() == -1) {
                                XMLDocumentScannerImpl.this.setScannerState(14);
                                return false;
                            }
                            XMLDocumentScannerImpl.this.reportFatalError("ContentIllegalInTrailingMisc", null);
                            XMLDocumentScannerImpl.this.fEntityScanner.scanChar();
                            XMLDocumentScannerImpl.this.setScannerState(12);
                            continue;
                        }
                        case 14: {
                            return false;
                        }
                        default: {
                            continue;
                        }
                        case 3: {
                            XMLDocumentScannerImpl.this.scanPI();
                            XMLDocumentScannerImpl.this.setScannerState(12);
                            continue;
                        }
                        case 2: {
                            if (!XMLDocumentScannerImpl.this.fEntityScanner.skipString("--")) {
                                XMLDocumentScannerImpl.this.reportFatalError("InvalidCommentStart", null);
                            }
                            XMLDocumentScannerImpl.this.scanComment();
                            XMLDocumentScannerImpl.this.setScannerState(12);
                            continue;
                        }
                        case 8: {
                            XMLDocumentScannerImpl.this.reportFatalError("ReferenceIllegalInTrailingMisc", null);
                            XMLDocumentScannerImpl.this.setScannerState(12);
                            continue;
                        }
                    }
                } while (b || b2);
            }
            catch (final MalformedByteSequenceException ex) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError(ex.getDomain(), ex.getKey(), ex.getArguments(), (short)2, ex);
                return false;
            }
            catch (final CharConversionException ex2) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2, ex2);
                return false;
            }
            catch (final EOFException ex3) {
                if (XMLDocumentScannerImpl.this.fMarkupDepth != 0) {
                    XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
                    return false;
                }
                XMLDocumentScannerImpl.this.setScannerState(14);
                return false;
            }
            return true;
        }
    }
    
    protected final class XMLDeclDispatcher implements Dispatcher
    {
        public boolean dispatch(final boolean b) throws IOException, XNIException {
            XMLDocumentScannerImpl.this.setScannerState(5);
            XMLDocumentScannerImpl.this.setDispatcher(XMLDocumentScannerImpl.this.fPrologDispatcher);
            try {
                if (XMLDocumentScannerImpl.this.fEntityScanner.skipString("<?xml")) {
                    final XMLDocumentScannerImpl this$0 = XMLDocumentScannerImpl.this;
                    ++this$0.fMarkupDepth;
                    if (XMLChar.isName(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                        XMLDocumentScannerImpl.this.fStringBuffer.clear();
                        XMLDocumentScannerImpl.this.fStringBuffer.append("xml");
                        if (XMLDocumentScannerImpl.this.fNamespaces) {
                            while (XMLChar.isNCName(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                                XMLDocumentScannerImpl.this.fStringBuffer.append((char)XMLDocumentScannerImpl.this.fEntityScanner.scanChar());
                            }
                        }
                        else {
                            while (XMLChar.isName(XMLDocumentScannerImpl.this.fEntityScanner.peekChar())) {
                                XMLDocumentScannerImpl.this.fStringBuffer.append((char)XMLDocumentScannerImpl.this.fEntityScanner.scanChar());
                            }
                        }
                        XMLDocumentScannerImpl.this.scanPIData(XMLDocumentScannerImpl.this.fSymbolTable.addSymbol(XMLDocumentScannerImpl.this.fStringBuffer.ch, XMLDocumentScannerImpl.this.fStringBuffer.offset, XMLDocumentScannerImpl.this.fStringBuffer.length), XMLDocumentScannerImpl.this.fString);
                    }
                    else {
                        XMLDocumentScannerImpl.this.scanXMLDeclOrTextDecl(false);
                    }
                }
                return XMLDocumentScannerImpl.this.fEntityManager.fCurrentEntity.mayReadChunks = true;
            }
            catch (final MalformedByteSequenceException ex) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError(ex.getDomain(), ex.getKey(), ex.getArguments(), (short)2, ex);
                return false;
            }
            catch (final CharConversionException ex2) {
                XMLDocumentScannerImpl.this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2, ex2);
                return false;
            }
            catch (final EOFException ex3) {
                XMLDocumentScannerImpl.this.reportFatalError("PrematureEOF", null);
                return false;
            }
        }
    }
}
