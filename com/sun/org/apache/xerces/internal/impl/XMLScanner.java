package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;
import com.sun.xml.internal.stream.Entity;
import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import javax.xml.stream.events.XMLEvent;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.xml.internal.stream.XMLEntityStorage;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import java.util.ArrayList;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;

public abstract class XMLScanner implements XMLComponent
{
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    protected static final boolean DEBUG_ATTR_NORMALIZATION = false;
    private boolean fNeedNonNormalizedValue;
    protected ArrayList<XMLString> attributeValueCache;
    protected ArrayList<XMLStringBuffer> stringBufferCache;
    protected int fStringBufferIndex;
    protected boolean fAttributeCacheInitDone;
    protected int fAttributeCacheUsedCount;
    protected boolean fValidation;
    protected boolean fNamespaces;
    protected boolean fNotifyCharRefs;
    protected boolean fParserSettings;
    protected PropertyManager fPropertyManager;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityManager fEntityManager;
    protected XMLEntityStorage fEntityStore;
    protected XMLSecurityManager fSecurityManager;
    protected XMLLimitAnalyzer fLimitAnalyzer;
    protected XMLEvent fEvent;
    protected XMLEntityScanner fEntityScanner;
    protected int fEntityDepth;
    protected String fCharRefLiteral;
    protected boolean fScanningAttribute;
    protected boolean fReportEntity;
    protected static final String fVersionSymbol;
    protected static final String fEncodingSymbol;
    protected static final String fStandaloneSymbol;
    protected static final String fAmpSymbol;
    protected static final String fLtSymbol;
    protected static final String fGtSymbol;
    protected static final String fQuotSymbol;
    protected static final String fAposSymbol;
    private XMLString fString;
    private XMLStringBuffer fStringBuffer;
    private XMLStringBuffer fStringBuffer2;
    private XMLStringBuffer fStringBuffer3;
    protected XMLResourceIdentifierImpl fResourceIdentifier;
    int initialCacheCount;
    
    public XMLScanner() {
        this.fNeedNonNormalizedValue = false;
        this.attributeValueCache = new ArrayList<XMLString>();
        this.stringBufferCache = new ArrayList<XMLStringBuffer>();
        this.fStringBufferIndex = 0;
        this.fAttributeCacheInitDone = false;
        this.fAttributeCacheUsedCount = 0;
        this.fValidation = false;
        this.fNotifyCharRefs = false;
        this.fParserSettings = true;
        this.fPropertyManager = null;
        this.fEntityManager = null;
        this.fEntityStore = null;
        this.fSecurityManager = null;
        this.fLimitAnalyzer = null;
        this.fEntityScanner = null;
        this.fCharRefLiteral = null;
        this.fString = new XMLString();
        this.fStringBuffer = new XMLStringBuffer();
        this.fStringBuffer2 = new XMLStringBuffer();
        this.fStringBuffer3 = new XMLStringBuffer();
        this.fResourceIdentifier = new XMLResourceIdentifierImpl();
        this.initialCacheCount = 6;
    }
    
    @Override
    public void reset(final XMLComponentManager componentManager) throws XMLConfigurationException {
        if (!(this.fParserSettings = componentManager.getFeature("http://apache.org/xml/features/internal/parser-settings", true))) {
            this.init();
            return;
        }
        this.fSymbolTable = (SymbolTable)componentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fErrorReporter = (XMLErrorReporter)componentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fEntityManager = (XMLEntityManager)componentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
        this.fSecurityManager = (XMLSecurityManager)componentManager.getProperty("http://apache.org/xml/properties/security-manager");
        this.fEntityStore = this.fEntityManager.getEntityStore();
        this.fValidation = componentManager.getFeature("http://xml.org/sax/features/validation", false);
        this.fNamespaces = componentManager.getFeature("http://xml.org/sax/features/namespaces", true);
        this.fNotifyCharRefs = componentManager.getFeature("http://apache.org/xml/features/scanner/notify-char-refs", false);
        this.init();
    }
    
    protected void setPropertyManager(final PropertyManager propertyManager) {
        this.fPropertyManager = propertyManager;
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        if (propertyId.startsWith("http://apache.org/xml/properties/")) {
            final String property = propertyId.substring("http://apache.org/xml/properties/".length());
            if (property.equals("internal/symbol-table")) {
                this.fSymbolTable = (SymbolTable)value;
            }
            else if (property.equals("internal/error-reporter")) {
                this.fErrorReporter = (XMLErrorReporter)value;
            }
            else if (property.equals("internal/entity-manager")) {
                this.fEntityManager = (XMLEntityManager)value;
            }
        }
        if (propertyId.equals("http://apache.org/xml/properties/security-manager")) {
            this.fSecurityManager = (XMLSecurityManager)value;
        }
    }
    
    @Override
    public void setFeature(final String featureId, final boolean value) throws XMLConfigurationException {
        if ("http://xml.org/sax/features/validation".equals(featureId)) {
            this.fValidation = value;
        }
        else if ("http://apache.org/xml/features/scanner/notify-char-refs".equals(featureId)) {
            this.fNotifyCharRefs = value;
        }
    }
    
    public boolean getFeature(final String featureId) throws XMLConfigurationException {
        if ("http://xml.org/sax/features/validation".equals(featureId)) {
            return this.fValidation;
        }
        if ("http://apache.org/xml/features/scanner/notify-char-refs".equals(featureId)) {
            return this.fNotifyCharRefs;
        }
        throw new XMLConfigurationException(Status.NOT_RECOGNIZED, featureId);
    }
    
    protected void reset() {
        this.init();
        this.fValidation = true;
        this.fNotifyCharRefs = false;
    }
    
    public void reset(final PropertyManager propertyManager) {
        this.init();
        this.fSymbolTable = (SymbolTable)propertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fErrorReporter = (XMLErrorReporter)propertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fEntityManager = (XMLEntityManager)propertyManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
        this.fEntityStore = this.fEntityManager.getEntityStore();
        this.fEntityScanner = this.fEntityManager.getEntityScanner();
        this.fSecurityManager = (XMLSecurityManager)propertyManager.getProperty("http://apache.org/xml/properties/security-manager");
        this.fValidation = false;
        this.fNotifyCharRefs = false;
    }
    
    protected void scanXMLDeclOrTextDecl(final boolean scanningTextDecl, final String[] pseudoAttributeValues) throws IOException, XNIException {
        String version = null;
        String encoding = null;
        String standalone = null;
        final int STATE_VERSION = 0;
        final int STATE_ENCODING = 1;
        final int STATE_STANDALONE = 2;
        final int STATE_DONE = 3;
        int state = 0;
        boolean dataFoundForTarget = false;
        boolean sawSpace = this.fEntityScanner.skipSpaces();
        final Entity.ScannedEntity currEnt = this.fEntityManager.getCurrentEntity();
        final boolean currLiteral = currEnt.literal;
        currEnt.literal = false;
        while (this.fEntityScanner.peekChar() != 63) {
            dataFoundForTarget = true;
            final String name = this.scanPseudoAttribute(scanningTextDecl, this.fString);
            switch (state) {
                case 0: {
                    if (name.equals(XMLScanner.fVersionSymbol)) {
                        if (!sawSpace) {
                            this.reportFatalError(scanningTextDecl ? "SpaceRequiredBeforeVersionInTextDecl" : "SpaceRequiredBeforeVersionInXMLDecl", null);
                        }
                        version = this.fString.toString();
                        state = 1;
                        if (!this.versionSupported(version)) {
                            this.reportFatalError("VersionNotSupported", new Object[] { version });
                        }
                        if (version.equals("1.1")) {
                            final Entity.ScannedEntity top = this.fEntityManager.getTopLevelEntity();
                            if (top != null && (top.version == null || top.version.equals("1.0"))) {
                                this.reportFatalError("VersionMismatch", null);
                            }
                            this.fEntityManager.setScannerVersion((short)2);
                            break;
                        }
                        break;
                    }
                    else {
                        if (name.equals(XMLScanner.fEncodingSymbol)) {
                            if (!scanningTextDecl) {
                                this.reportFatalError("VersionInfoRequired", null);
                            }
                            if (!sawSpace) {
                                this.reportFatalError(scanningTextDecl ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", null);
                            }
                            encoding = this.fString.toString();
                            state = (scanningTextDecl ? 3 : 2);
                            break;
                        }
                        if (scanningTextDecl) {
                            this.reportFatalError("EncodingDeclRequired", null);
                            break;
                        }
                        this.reportFatalError("VersionInfoRequired", null);
                        break;
                    }
                    break;
                }
                case 1: {
                    if (name.equals(XMLScanner.fEncodingSymbol)) {
                        if (!sawSpace) {
                            this.reportFatalError(scanningTextDecl ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", null);
                        }
                        encoding = this.fString.toString();
                        state = (scanningTextDecl ? 3 : 2);
                        break;
                    }
                    if (scanningTextDecl || !name.equals(XMLScanner.fStandaloneSymbol)) {
                        this.reportFatalError("EncodingDeclRequired", null);
                        break;
                    }
                    if (!sawSpace) {
                        this.reportFatalError("SpaceRequiredBeforeStandalone", null);
                    }
                    standalone = this.fString.toString();
                    state = 3;
                    if (!standalone.equals("yes") && !standalone.equals("no")) {
                        this.reportFatalError("SDDeclInvalid", new Object[] { standalone });
                        break;
                    }
                    break;
                }
                case 2: {
                    if (!name.equals(XMLScanner.fStandaloneSymbol)) {
                        this.reportFatalError("SDDeclNameInvalid", null);
                        break;
                    }
                    if (!sawSpace) {
                        this.reportFatalError("SpaceRequiredBeforeStandalone", null);
                    }
                    standalone = this.fString.toString();
                    state = 3;
                    if (!standalone.equals("yes") && !standalone.equals("no")) {
                        this.reportFatalError("SDDeclInvalid", new Object[] { standalone });
                        break;
                    }
                    break;
                }
                default: {
                    this.reportFatalError("NoMorePseudoAttributes", null);
                    break;
                }
            }
            sawSpace = this.fEntityScanner.skipSpaces();
        }
        if (currLiteral) {
            currEnt.literal = true;
        }
        if (scanningTextDecl && state != 3) {
            this.reportFatalError("MorePseudoAttributes", null);
        }
        if (scanningTextDecl) {
            if (!dataFoundForTarget && encoding == null) {
                this.reportFatalError("EncodingDeclRequired", null);
            }
        }
        else if (!dataFoundForTarget && version == null) {
            this.reportFatalError("VersionInfoRequired", null);
        }
        if (!this.fEntityScanner.skipChar(63, null)) {
            this.reportFatalError("XMLDeclUnterminated", null);
        }
        if (!this.fEntityScanner.skipChar(62, null)) {
            this.reportFatalError("XMLDeclUnterminated", null);
        }
        pseudoAttributeValues[0] = version;
        pseudoAttributeValues[1] = encoding;
        pseudoAttributeValues[2] = standalone;
    }
    
    protected String scanPseudoAttribute(final boolean scanningTextDecl, final XMLString value) throws IOException, XNIException {
        final String name = this.scanPseudoAttributeName();
        if (name == null) {
            this.reportFatalError("PseudoAttrNameExpected", null);
        }
        this.fEntityScanner.skipSpaces();
        if (!this.fEntityScanner.skipChar(61, null)) {
            this.reportFatalError(scanningTextDecl ? "EqRequiredInTextDecl" : "EqRequiredInXMLDecl", new Object[] { name });
        }
        this.fEntityScanner.skipSpaces();
        final int quote = this.fEntityScanner.peekChar();
        if (quote != 39 && quote != 34) {
            this.reportFatalError(scanningTextDecl ? "QuoteRequiredInTextDecl" : "QuoteRequiredInXMLDecl", new Object[] { name });
        }
        this.fEntityScanner.scanChar(NameType.ATTRIBUTE);
        int c = this.fEntityScanner.scanLiteral(quote, value, false);
        if (c != quote) {
            this.fStringBuffer2.clear();
            do {
                this.fStringBuffer2.append(value);
                if (c != -1) {
                    if (c == 38 || c == 37 || c == 60 || c == 93) {
                        this.fStringBuffer2.append((char)this.fEntityScanner.scanChar(NameType.ATTRIBUTE));
                    }
                    else if (XMLChar.isHighSurrogate(c)) {
                        this.scanSurrogates(this.fStringBuffer2);
                    }
                    else if (this.isInvalidLiteral(c)) {
                        final String key = scanningTextDecl ? "InvalidCharInTextDecl" : "InvalidCharInXMLDecl";
                        this.reportFatalError(key, new Object[] { Integer.toString(c, 16) });
                        this.fEntityScanner.scanChar(null);
                    }
                }
                c = this.fEntityScanner.scanLiteral(quote, value, false);
            } while (c != quote);
            this.fStringBuffer2.append(value);
            value.setValues(this.fStringBuffer2);
        }
        if (!this.fEntityScanner.skipChar(quote, null)) {
            this.reportFatalError(scanningTextDecl ? "CloseQuoteMissingInTextDecl" : "CloseQuoteMissingInXMLDecl", new Object[] { name });
        }
        return name;
    }
    
    private String scanPseudoAttributeName() throws IOException, XNIException {
        final int ch = this.fEntityScanner.peekChar();
        switch (ch) {
            case 118: {
                if (this.fEntityScanner.skipString(XMLScanner.fVersionSymbol)) {
                    return XMLScanner.fVersionSymbol;
                }
                break;
            }
            case 101: {
                if (this.fEntityScanner.skipString(XMLScanner.fEncodingSymbol)) {
                    return XMLScanner.fEncodingSymbol;
                }
                break;
            }
            case 115: {
                if (this.fEntityScanner.skipString(XMLScanner.fStandaloneSymbol)) {
                    return XMLScanner.fStandaloneSymbol;
                }
                break;
            }
        }
        return null;
    }
    
    protected void scanPI(final XMLStringBuffer data) throws IOException, XNIException {
        this.fReportEntity = false;
        final String target = this.fEntityScanner.scanName(NameType.PI);
        if (target == null) {
            this.reportFatalError("PITargetRequired", null);
        }
        this.scanPIData(target, data);
        this.fReportEntity = true;
    }
    
    protected void scanPIData(final String target, final XMLStringBuffer data) throws IOException, XNIException {
        if (target.length() == 3) {
            final char c0 = Character.toLowerCase(target.charAt(0));
            final char c2 = Character.toLowerCase(target.charAt(1));
            final char c3 = Character.toLowerCase(target.charAt(2));
            if (c0 == 'x' && c2 == 'm' && c3 == 'l') {
                this.reportFatalError("ReservedPITarget", null);
            }
        }
        if (!this.fEntityScanner.skipSpaces()) {
            if (this.fEntityScanner.skipString("?>")) {
                return;
            }
            this.reportFatalError("SpaceRequiredInPI", null);
        }
        if (this.fEntityScanner.scanData("?>", data)) {
            do {
                final int c4 = this.fEntityScanner.peekChar();
                if (c4 != -1) {
                    if (XMLChar.isHighSurrogate(c4)) {
                        this.scanSurrogates(data);
                    }
                    else {
                        if (!this.isInvalidLiteral(c4)) {
                            continue;
                        }
                        this.reportFatalError("InvalidCharInPI", new Object[] { Integer.toHexString(c4) });
                        this.fEntityScanner.scanChar(null);
                    }
                }
            } while (this.fEntityScanner.scanData("?>", data));
        }
    }
    
    protected void scanComment(final XMLStringBuffer text) throws IOException, XNIException {
        text.clear();
        while (this.fEntityScanner.scanData("--", text)) {
            final int c = this.fEntityScanner.peekChar();
            if (c != -1) {
                if (XMLChar.isHighSurrogate(c)) {
                    this.scanSurrogates(text);
                }
                else {
                    if (!this.isInvalidLiteral(c)) {
                        continue;
                    }
                    this.reportFatalError("InvalidCharInComment", new Object[] { Integer.toHexString(c) });
                    this.fEntityScanner.scanChar(NameType.COMMENT);
                }
            }
        }
        if (!this.fEntityScanner.skipChar(62, NameType.COMMENT)) {
            this.reportFatalError("DashDashInComment", null);
        }
    }
    
    protected void scanAttributeValue(final XMLString value, final XMLString nonNormalizedValue, final String atName, final XMLAttributes attributes, final int attrIndex, final boolean checkEntities, final String eleName, final boolean isNSURI) throws IOException, XNIException {
        XMLStringBuffer stringBuffer = null;
        final int quote = this.fEntityScanner.peekChar();
        if (quote != 39 && quote != 34) {
            this.reportFatalError("OpenQuoteExpected", new Object[] { eleName, atName });
        }
        this.fEntityScanner.scanChar(NameType.ATTRIBUTE);
        final int entityDepth = this.fEntityDepth;
        int c = this.fEntityScanner.scanLiteral(quote, value, isNSURI);
        if (this.fNeedNonNormalizedValue) {
            this.fStringBuffer2.clear();
            this.fStringBuffer2.append(value);
        }
        if (this.fEntityScanner.whiteSpaceLen > 0) {
            this.normalizeWhitespace(value);
        }
        if (c != quote) {
            this.fScanningAttribute = true;
            stringBuffer = this.getStringBuffer();
            stringBuffer.clear();
            do {
                stringBuffer.append(value);
                if (c == 38) {
                    this.fEntityScanner.skipChar(38, NameType.REFERENCE);
                    if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                        this.fStringBuffer2.append('&');
                    }
                    if (this.fEntityScanner.skipChar(35, NameType.REFERENCE)) {
                        if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                            this.fStringBuffer2.append('#');
                        }
                        int ch;
                        if (this.fNeedNonNormalizedValue) {
                            ch = this.scanCharReferenceValue(stringBuffer, this.fStringBuffer2);
                        }
                        else {
                            ch = this.scanCharReferenceValue(stringBuffer, null);
                        }
                        if (ch != -1) {}
                    }
                    else {
                        final String entityName = this.fEntityScanner.scanName(NameType.ENTITY);
                        if (entityName == null) {
                            this.reportFatalError("NameRequiredInReference", null);
                        }
                        else if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                            this.fStringBuffer2.append(entityName);
                        }
                        if (!this.fEntityScanner.skipChar(59, NameType.REFERENCE)) {
                            this.reportFatalError("SemicolonRequiredInReference", new Object[] { entityName });
                        }
                        else if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                            this.fStringBuffer2.append(';');
                        }
                        if (this.resolveCharacter(entityName, stringBuffer)) {
                            this.checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, 1);
                        }
                        else if (this.fEntityStore.isExternalEntity(entityName)) {
                            this.reportFatalError("ReferenceToExternalEntity", new Object[] { entityName });
                        }
                        else {
                            if (!this.fEntityStore.isDeclaredEntity(entityName)) {
                                if (checkEntities) {
                                    if (this.fValidation) {
                                        this.fErrorReporter.reportError(this.fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { entityName }, (short)1);
                                    }
                                }
                                else {
                                    this.reportFatalError("EntityNotDeclared", new Object[] { entityName });
                                }
                            }
                            this.fEntityManager.startEntity(true, entityName, true);
                        }
                    }
                }
                else if (c == 60) {
                    this.reportFatalError("LessthanInAttValue", new Object[] { eleName, atName });
                    this.fEntityScanner.scanChar(null);
                    if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                        this.fStringBuffer2.append((char)c);
                    }
                }
                else if (c == 37 || c == 93) {
                    this.fEntityScanner.scanChar(null);
                    stringBuffer.append((char)c);
                    if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                        this.fStringBuffer2.append((char)c);
                    }
                }
                else if (c == 10 || c == 13) {
                    this.fEntityScanner.scanChar(null);
                    stringBuffer.append(' ');
                    if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                        this.fStringBuffer2.append('\n');
                    }
                }
                else if (c != -1 && XMLChar.isHighSurrogate(c)) {
                    this.fStringBuffer3.clear();
                    if (this.scanSurrogates(this.fStringBuffer3)) {
                        stringBuffer.append(this.fStringBuffer3);
                        if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                            this.fStringBuffer2.append(this.fStringBuffer3);
                        }
                    }
                }
                else if (c != -1 && this.isInvalidLiteral(c)) {
                    this.reportFatalError("InvalidCharInAttValue", new Object[] { eleName, atName, Integer.toString(c, 16) });
                    this.fEntityScanner.scanChar(null);
                    if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                        this.fStringBuffer2.append((char)c);
                    }
                }
                c = this.fEntityScanner.scanLiteral(quote, value, isNSURI);
                if (entityDepth == this.fEntityDepth && this.fNeedNonNormalizedValue) {
                    this.fStringBuffer2.append(value);
                }
                if (this.fEntityScanner.whiteSpaceLen > 0) {
                    this.normalizeWhitespace(value);
                }
            } while (c != quote || entityDepth != this.fEntityDepth);
            stringBuffer.append(value);
            value.setValues(stringBuffer);
            this.fScanningAttribute = false;
        }
        if (this.fNeedNonNormalizedValue) {
            nonNormalizedValue.setValues(this.fStringBuffer2);
        }
        final int cquote = this.fEntityScanner.scanChar(NameType.ATTRIBUTE);
        if (cquote != quote) {
            this.reportFatalError("CloseQuoteExpected", new Object[] { eleName, atName });
        }
    }
    
    protected boolean resolveCharacter(final String entityName, final XMLStringBuffer stringBuffer) {
        if (entityName == XMLScanner.fAmpSymbol) {
            stringBuffer.append('&');
            return true;
        }
        if (entityName == XMLScanner.fAposSymbol) {
            stringBuffer.append('\'');
            return true;
        }
        if (entityName == XMLScanner.fLtSymbol) {
            stringBuffer.append('<');
            return true;
        }
        if (entityName == XMLScanner.fGtSymbol) {
            this.checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, 1);
            stringBuffer.append('>');
            return true;
        }
        if (entityName == XMLScanner.fQuotSymbol) {
            this.checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, 1);
            stringBuffer.append('\"');
            return true;
        }
        return false;
    }
    
    protected void scanExternalID(final String[] identifiers, final boolean optionalSystemId) throws IOException, XNIException {
        String systemId = null;
        String publicId = null;
        if (this.fEntityScanner.skipString("PUBLIC")) {
            if (!this.fEntityScanner.skipSpaces()) {
                this.reportFatalError("SpaceRequiredAfterPUBLIC", null);
            }
            this.scanPubidLiteral(this.fString);
            publicId = this.fString.toString();
            if (!this.fEntityScanner.skipSpaces() && !optionalSystemId) {
                this.reportFatalError("SpaceRequiredBetweenPublicAndSystem", null);
            }
        }
        if (publicId != null || this.fEntityScanner.skipString("SYSTEM")) {
            if (publicId == null && !this.fEntityScanner.skipSpaces()) {
                this.reportFatalError("SpaceRequiredAfterSYSTEM", null);
            }
            final int quote = this.fEntityScanner.peekChar();
            if (quote != 39 && quote != 34) {
                if (publicId != null && optionalSystemId) {
                    identifiers[0] = null;
                    identifiers[1] = publicId;
                    return;
                }
                this.reportFatalError("QuoteRequiredInSystemID", null);
            }
            this.fEntityScanner.scanChar(null);
            XMLString ident = this.fString;
            if (this.fEntityScanner.scanLiteral(quote, ident, false) != quote) {
                this.fStringBuffer.clear();
                do {
                    this.fStringBuffer.append(ident);
                    final int c = this.fEntityScanner.peekChar();
                    if (XMLChar.isMarkup(c) || c == 93) {
                        this.fStringBuffer.append((char)this.fEntityScanner.scanChar(null));
                    }
                    else {
                        if (c == -1 || !this.isInvalidLiteral(c)) {
                            continue;
                        }
                        this.reportFatalError("InvalidCharInSystemID", new Object[] { Integer.toString(c, 16) });
                    }
                } while (this.fEntityScanner.scanLiteral(quote, ident, false) != quote);
                this.fStringBuffer.append(ident);
                ident = this.fStringBuffer;
            }
            systemId = ident.toString();
            if (!this.fEntityScanner.skipChar(quote, null)) {
                this.reportFatalError("SystemIDUnterminated", null);
            }
        }
        identifiers[0] = systemId;
        identifiers[1] = publicId;
    }
    
    protected boolean scanPubidLiteral(final XMLString literal) throws IOException, XNIException {
        final int quote = this.fEntityScanner.scanChar(null);
        if (quote != 39 && quote != 34) {
            this.reportFatalError("QuoteRequiredInPublicID", null);
            return false;
        }
        this.fStringBuffer.clear();
        boolean skipSpace = true;
        boolean dataok = true;
        while (true) {
            final int c = this.fEntityScanner.scanChar(null);
            if (c == 32 || c == 10 || c == 13) {
                if (skipSpace) {
                    continue;
                }
                this.fStringBuffer.append(' ');
                skipSpace = true;
            }
            else {
                if (c == quote) {
                    if (skipSpace) {
                        final XMLStringBuffer fStringBuffer = this.fStringBuffer;
                        --fStringBuffer.length;
                    }
                    literal.setValues(this.fStringBuffer);
                    return dataok;
                }
                if (XMLChar.isPubid(c)) {
                    this.fStringBuffer.append((char)c);
                    skipSpace = false;
                }
                else {
                    if (c == -1) {
                        this.reportFatalError("PublicIDUnterminated", null);
                        return false;
                    }
                    dataok = false;
                    this.reportFatalError("InvalidCharInPublicID", new Object[] { Integer.toHexString(c) });
                }
            }
        }
    }
    
    protected void normalizeWhitespace(final XMLString value) {
        int i = 0;
        int j = 0;
        final int[] buff = this.fEntityScanner.whiteSpaceLookup;
        final int buffLen = this.fEntityScanner.whiteSpaceLen;
        final int end = value.offset + value.length;
        while (i < buffLen) {
            j = buff[i];
            if (j < end) {
                value.ch[j] = ' ';
            }
            ++i;
        }
    }
    
    public void startEntity(final String name, final XMLResourceIdentifier identifier, final String encoding, final Augmentations augs) throws XNIException {
        ++this.fEntityDepth;
        this.fEntityScanner = this.fEntityManager.getEntityScanner();
        this.fEntityStore = this.fEntityManager.getEntityStore();
    }
    
    public void endEntity(final String name, final Augmentations augs) throws IOException, XNIException {
        if (this.fEntityDepth > 0) {
            --this.fEntityDepth;
        }
    }
    
    protected int scanCharReferenceValue(final XMLStringBuffer buf, final XMLStringBuffer buf2) throws IOException, XNIException {
        final int initLen = buf.length;
        boolean hex = false;
        if (this.fEntityScanner.skipChar(120, NameType.REFERENCE)) {
            if (buf2 != null) {
                buf2.append('x');
            }
            hex = true;
            this.fStringBuffer3.clear();
            boolean digit = true;
            int c = this.fEntityScanner.peekChar();
            digit = ((c >= 48 && c <= 57) || (c >= 97 && c <= 102) || (c >= 65 && c <= 70));
            if (digit) {
                if (buf2 != null) {
                    buf2.append((char)c);
                }
                this.fEntityScanner.scanChar(NameType.REFERENCE);
                this.fStringBuffer3.append((char)c);
                do {
                    c = this.fEntityScanner.peekChar();
                    digit = ((c >= 48 && c <= 57) || (c >= 97 && c <= 102) || (c >= 65 && c <= 70));
                    if (digit) {
                        if (buf2 != null) {
                            buf2.append((char)c);
                        }
                        this.fEntityScanner.scanChar(NameType.REFERENCE);
                        this.fStringBuffer3.append((char)c);
                    }
                } while (digit);
            }
            else {
                this.reportFatalError("HexdigitRequiredInCharRef", null);
            }
        }
        else {
            this.fStringBuffer3.clear();
            boolean digit = true;
            int c = this.fEntityScanner.peekChar();
            digit = (c >= 48 && c <= 57);
            if (digit) {
                if (buf2 != null) {
                    buf2.append((char)c);
                }
                this.fEntityScanner.scanChar(NameType.REFERENCE);
                this.fStringBuffer3.append((char)c);
                do {
                    c = this.fEntityScanner.peekChar();
                    digit = (c >= 48 && c <= 57);
                    if (digit) {
                        if (buf2 != null) {
                            buf2.append((char)c);
                        }
                        this.fEntityScanner.scanChar(NameType.REFERENCE);
                        this.fStringBuffer3.append((char)c);
                    }
                } while (digit);
            }
            else {
                this.reportFatalError("DigitRequiredInCharRef", null);
            }
        }
        if (!this.fEntityScanner.skipChar(59, NameType.REFERENCE)) {
            this.reportFatalError("SemicolonRequiredInCharRef", null);
        }
        if (buf2 != null) {
            buf2.append(';');
        }
        int value = -1;
        try {
            value = Integer.parseInt(this.fStringBuffer3.toString(), hex ? 16 : 10);
            if (this.isInvalid(value)) {
                final StringBuffer errorBuf = new StringBuffer(this.fStringBuffer3.length + 1);
                if (hex) {
                    errorBuf.append('x');
                }
                errorBuf.append(this.fStringBuffer3.ch, this.fStringBuffer3.offset, this.fStringBuffer3.length);
                this.reportFatalError("InvalidCharRef", new Object[] { errorBuf.toString() });
            }
        }
        catch (final NumberFormatException e) {
            final StringBuffer errorBuf2 = new StringBuffer(this.fStringBuffer3.length + 1);
            if (hex) {
                errorBuf2.append('x');
            }
            errorBuf2.append(this.fStringBuffer3.ch, this.fStringBuffer3.offset, this.fStringBuffer3.length);
            this.reportFatalError("InvalidCharRef", new Object[] { errorBuf2.toString() });
        }
        if (!XMLChar.isSupplemental(value)) {
            buf.append((char)value);
        }
        else {
            buf.append(XMLChar.highSurrogate(value));
            buf.append(XMLChar.lowSurrogate(value));
        }
        if (this.fNotifyCharRefs && value != -1) {
            final String literal = "#" + (hex ? "x" : "") + this.fStringBuffer3.toString();
            if (!this.fScanningAttribute) {
                this.fCharRefLiteral = literal;
            }
        }
        if (this.fEntityScanner.fCurrentEntity.isGE) {
            this.checkEntityLimit(false, this.fEntityScanner.fCurrentEntity.name, buf.length - initLen);
        }
        return value;
    }
    
    protected boolean isInvalid(final int value) {
        return XMLChar.isInvalid(value);
    }
    
    protected boolean isInvalidLiteral(final int value) {
        return XMLChar.isInvalid(value);
    }
    
    protected boolean isValidNameChar(final int value) {
        return XMLChar.isName(value);
    }
    
    protected boolean isValidNCName(final int value) {
        return XMLChar.isNCName(value);
    }
    
    protected boolean isValidNameStartChar(final int value) {
        return XMLChar.isNameStart(value);
    }
    
    protected boolean isValidNameStartHighSurrogate(final int value) {
        return false;
    }
    
    protected boolean versionSupported(final String version) {
        return version.equals("1.0") || version.equals("1.1");
    }
    
    protected boolean scanSurrogates(final XMLStringBuffer buf) throws IOException, XNIException {
        final int high = this.fEntityScanner.scanChar(null);
        final int low = this.fEntityScanner.peekChar();
        if (!XMLChar.isLowSurrogate(low)) {
            this.reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(high, 16) });
            return false;
        }
        this.fEntityScanner.scanChar(null);
        final int c = XMLChar.supplemental((char)high, (char)low);
        if (this.isInvalid(c)) {
            this.reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(c, 16) });
            return false;
        }
        buf.append((char)high);
        buf.append((char)low);
        return true;
    }
    
    protected void reportFatalError(final String msgId, final Object[] args) throws XNIException {
        this.fErrorReporter.reportError(this.fEntityScanner, "http://www.w3.org/TR/1998/REC-xml-19980210", msgId, args, (short)2);
    }
    
    private void init() {
        this.fEntityScanner = null;
        this.fEntityDepth = 0;
        this.fReportEntity = true;
        this.fResourceIdentifier.clear();
        if (!this.fAttributeCacheInitDone) {
            for (int i = 0; i < this.initialCacheCount; ++i) {
                this.attributeValueCache.add(new XMLString());
                this.stringBufferCache.add(new XMLStringBuffer());
            }
            this.fAttributeCacheInitDone = true;
        }
        this.fStringBufferIndex = 0;
        this.fAttributeCacheUsedCount = 0;
    }
    
    XMLStringBuffer getStringBuffer() {
        if (this.fStringBufferIndex < this.initialCacheCount || this.fStringBufferIndex < this.stringBufferCache.size()) {
            return this.stringBufferCache.get(this.fStringBufferIndex++);
        }
        final XMLStringBuffer tmpObj = new XMLStringBuffer();
        ++this.fStringBufferIndex;
        this.stringBufferCache.add(tmpObj);
        return tmpObj;
    }
    
    void checkEntityLimit(final boolean isPEDecl, final String entityName, final XMLString buffer) {
        this.checkEntityLimit(isPEDecl, entityName, buffer.length);
    }
    
    void checkEntityLimit(final boolean isPEDecl, final String entityName, final int len) {
        if (this.fLimitAnalyzer == null) {
            this.fLimitAnalyzer = this.fEntityManager.fLimitAnalyzer;
        }
        if (isPEDecl) {
            this.fLimitAnalyzer.addValue(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT, "%" + entityName, len);
            if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT, this.fLimitAnalyzer)) {
                this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
                this.reportFatalError("MaxEntitySizeLimit", new Object[] { "%" + entityName, this.fLimitAnalyzer.getValue(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT), this.fSecurityManager.getLimit(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT), this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT) });
            }
        }
        else {
            this.fLimitAnalyzer.addValue(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, entityName, len);
            if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, this.fLimitAnalyzer)) {
                this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
                this.reportFatalError("MaxEntitySizeLimit", new Object[] { entityName, this.fLimitAnalyzer.getValue(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT), this.fSecurityManager.getLimit(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT), this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT) });
            }
        }
        if (this.fSecurityManager.isOverLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT, this.fLimitAnalyzer)) {
            this.fSecurityManager.debugPrint(this.fLimitAnalyzer);
            this.reportFatalError("TotalEntitySizeLimit", new Object[] { this.fLimitAnalyzer.getTotalValue(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT), this.fSecurityManager.getLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT), this.fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT) });
        }
    }
    
    static {
        fVersionSymbol = "version".intern();
        fEncodingSymbol = "encoding".intern();
        fStandaloneSymbol = "standalone".intern();
        fAmpSymbol = "amp".intern();
        fLtSymbol = "lt".intern();
        fGtSymbol = "gt".intern();
        fQuotSymbol = "quot".intern();
        fAposSymbol = "apos".intern();
    }
    
    public enum NameType
    {
        ATTRIBUTE("attribute"), 
        ATTRIBUTENAME("attribute name"), 
        COMMENT("comment"), 
        DOCTYPE("doctype"), 
        ELEMENTSTART("startelement"), 
        ELEMENTEND("endelement"), 
        ENTITY("entity"), 
        NOTATION("notation"), 
        PI("pi"), 
        REFERENCE("reference");
        
        final String literal;
        
        private NameType(final String literal) {
            this.literal = literal;
        }
        
        String literal() {
            return this.literal;
        }
    }
}
