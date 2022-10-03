package org.apache.xerces.impl;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.xni.XNIException;
import java.io.IOException;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.util.XMLResourceIdentifierImpl;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLComponent;

public abstract class XMLScanner implements XMLComponent
{
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected static final boolean DEBUG_ATTR_NORMALIZATION = false;
    protected boolean fValidation;
    protected boolean fNamespaces;
    protected boolean fNotifyCharRefs;
    protected boolean fParserSettings;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityManager fEntityManager;
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
    private final XMLString fString;
    private final XMLStringBuffer fStringBuffer;
    private final XMLStringBuffer fStringBuffer2;
    private final XMLStringBuffer fStringBuffer3;
    protected final XMLResourceIdentifierImpl fResourceIdentifier;
    
    public XMLScanner() {
        this.fValidation = false;
        this.fNotifyCharRefs = false;
        this.fParserSettings = true;
        this.fCharRefLiteral = null;
        this.fString = new XMLString();
        this.fStringBuffer = new XMLStringBuffer();
        this.fStringBuffer2 = new XMLStringBuffer();
        this.fStringBuffer3 = new XMLStringBuffer();
        this.fResourceIdentifier = new XMLResourceIdentifierImpl();
    }
    
    public void reset(final XMLComponentManager xmlComponentManager) throws XMLConfigurationException {
        try {
            this.fParserSettings = xmlComponentManager.getFeature("http://apache.org/xml/features/internal/parser-settings");
        }
        catch (final XMLConfigurationException ex) {
            this.fParserSettings = true;
        }
        if (!this.fParserSettings) {
            this.init();
            return;
        }
        this.fSymbolTable = (SymbolTable)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table");
        this.fErrorReporter = (XMLErrorReporter)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter");
        this.fEntityManager = (XMLEntityManager)xmlComponentManager.getProperty("http://apache.org/xml/properties/internal/entity-manager");
        try {
            this.fValidation = xmlComponentManager.getFeature("http://xml.org/sax/features/validation");
        }
        catch (final XMLConfigurationException ex2) {
            this.fValidation = false;
        }
        try {
            this.fNamespaces = xmlComponentManager.getFeature("http://xml.org/sax/features/namespaces");
        }
        catch (final XMLConfigurationException ex3) {
            this.fNamespaces = true;
        }
        try {
            this.fNotifyCharRefs = xmlComponentManager.getFeature("http://apache.org/xml/features/scanner/notify-char-refs");
        }
        catch (final XMLConfigurationException ex4) {
            this.fNotifyCharRefs = false;
        }
        this.init();
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        if (s.startsWith("http://apache.org/xml/properties/")) {
            final int n = s.length() - "http://apache.org/xml/properties/".length();
            if (n == "internal/symbol-table".length() && s.endsWith("internal/symbol-table")) {
                this.fSymbolTable = (SymbolTable)o;
            }
            else if (n == "internal/error-reporter".length() && s.endsWith("internal/error-reporter")) {
                this.fErrorReporter = (XMLErrorReporter)o;
            }
            else if (n == "internal/entity-manager".length() && s.endsWith("internal/entity-manager")) {
                this.fEntityManager = (XMLEntityManager)o;
            }
        }
    }
    
    public void setFeature(final String s, final boolean b) throws XMLConfigurationException {
        if ("http://xml.org/sax/features/validation".equals(s)) {
            this.fValidation = b;
        }
        else if ("http://apache.org/xml/features/scanner/notify-char-refs".equals(s)) {
            this.fNotifyCharRefs = b;
        }
    }
    
    public boolean getFeature(final String s) throws XMLConfigurationException {
        if ("http://xml.org/sax/features/validation".equals(s)) {
            return this.fValidation;
        }
        if ("http://apache.org/xml/features/scanner/notify-char-refs".equals(s)) {
            return this.fNotifyCharRefs;
        }
        throw new XMLConfigurationException((short)0, s);
    }
    
    protected void reset() {
        this.init();
        this.fValidation = true;
        this.fNotifyCharRefs = false;
    }
    
    protected void scanXMLDeclOrTextDecl(final boolean b, final String[] array) throws IOException, XNIException {
        String string = null;
        String s = null;
        String s2 = null;
        int n = 0;
        boolean b2 = false;
        boolean b3 = this.fEntityScanner.skipDeclSpaces();
        final XMLEntityManager.ScannedEntity currentEntity = this.fEntityManager.getCurrentEntity();
        final boolean literal = currentEntity.literal;
        currentEntity.literal = false;
        while (this.fEntityScanner.peekChar() != 63) {
            b2 = true;
            final String scanPseudoAttribute = this.scanPseudoAttribute(b, this.fString);
            switch (n) {
                case 0: {
                    if (scanPseudoAttribute == XMLScanner.fVersionSymbol) {
                        if (!b3) {
                            this.reportFatalError(b ? "SpaceRequiredBeforeVersionInTextDecl" : "SpaceRequiredBeforeVersionInXMLDecl", null);
                        }
                        string = this.fString.toString();
                        n = 1;
                        if (!this.versionSupported(string)) {
                            this.reportFatalError(this.getVersionNotSupportedKey(), new Object[] { string });
                            break;
                        }
                        break;
                    }
                    else {
                        if (scanPseudoAttribute == XMLScanner.fEncodingSymbol) {
                            if (!b) {
                                this.reportFatalError("VersionInfoRequired", null);
                            }
                            if (!b3) {
                                this.reportFatalError(b ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", null);
                            }
                            s = this.fString.toString();
                            n = (b ? 3 : 2);
                            break;
                        }
                        if (b) {
                            this.reportFatalError("EncodingDeclRequired", null);
                            break;
                        }
                        this.reportFatalError("VersionInfoRequired", null);
                        break;
                    }
                    break;
                }
                case 1: {
                    if (scanPseudoAttribute == XMLScanner.fEncodingSymbol) {
                        if (!b3) {
                            this.reportFatalError(b ? "SpaceRequiredBeforeEncodingInTextDecl" : "SpaceRequiredBeforeEncodingInXMLDecl", null);
                        }
                        s = this.fString.toString();
                        n = (b ? 3 : 2);
                        break;
                    }
                    if (b || scanPseudoAttribute != XMLScanner.fStandaloneSymbol) {
                        this.reportFatalError("EncodingDeclRequired", null);
                        break;
                    }
                    if (!b3) {
                        this.reportFatalError("SpaceRequiredBeforeStandalone", null);
                    }
                    s2 = this.fString.toString();
                    n = 3;
                    if (!s2.equals("yes") && !s2.equals("no")) {
                        this.reportFatalError("SDDeclInvalid", new Object[] { s2 });
                        break;
                    }
                    break;
                }
                case 2: {
                    if (scanPseudoAttribute != XMLScanner.fStandaloneSymbol) {
                        this.reportFatalError("EncodingDeclRequired", null);
                        break;
                    }
                    if (!b3) {
                        this.reportFatalError("SpaceRequiredBeforeStandalone", null);
                    }
                    s2 = this.fString.toString();
                    n = 3;
                    if (!s2.equals("yes") && !s2.equals("no")) {
                        this.reportFatalError("SDDeclInvalid", new Object[] { s2 });
                        break;
                    }
                    break;
                }
                default: {
                    this.reportFatalError("NoMorePseudoAttributes", null);
                    break;
                }
            }
            b3 = this.fEntityScanner.skipDeclSpaces();
        }
        if (literal) {
            currentEntity.literal = true;
        }
        if (b && n != 3) {
            this.reportFatalError("MorePseudoAttributes", null);
        }
        if (b) {
            if (!b2 && s == null) {
                this.reportFatalError("EncodingDeclRequired", null);
            }
        }
        else if (!b2 && string == null) {
            this.reportFatalError("VersionInfoRequired", null);
        }
        if (!this.fEntityScanner.skipChar(63)) {
            this.reportFatalError("XMLDeclUnterminated", null);
        }
        if (!this.fEntityScanner.skipChar(62)) {
            this.reportFatalError("XMLDeclUnterminated", null);
        }
        array[0] = string;
        array[1] = s;
        array[2] = s2;
    }
    
    public String scanPseudoAttribute(final boolean b, final XMLString xmlString) throws IOException, XNIException {
        final String scanPseudoAttributeName = this.scanPseudoAttributeName();
        XMLEntityManager.print(this.fEntityManager.getCurrentEntity());
        if (scanPseudoAttributeName == null) {
            this.reportFatalError("PseudoAttrNameExpected", null);
        }
        this.fEntityScanner.skipDeclSpaces();
        if (!this.fEntityScanner.skipChar(61)) {
            this.reportFatalError(b ? "EqRequiredInTextDecl" : "EqRequiredInXMLDecl", new Object[] { scanPseudoAttributeName });
        }
        this.fEntityScanner.skipDeclSpaces();
        final int peekChar = this.fEntityScanner.peekChar();
        if (peekChar != 39 && peekChar != 34) {
            this.reportFatalError(b ? "QuoteRequiredInTextDecl" : "QuoteRequiredInXMLDecl", new Object[] { scanPseudoAttributeName });
        }
        this.fEntityScanner.scanChar();
        int i = this.fEntityScanner.scanLiteral(peekChar, xmlString);
        if (i != peekChar) {
            this.fStringBuffer2.clear();
            do {
                this.fStringBuffer2.append(xmlString);
                if (i != -1) {
                    if (i == 38 || i == 37 || i == 60 || i == 93) {
                        this.fStringBuffer2.append((char)this.fEntityScanner.scanChar());
                    }
                    else if (XMLChar.isHighSurrogate(i)) {
                        this.scanSurrogates(this.fStringBuffer2);
                    }
                    else if (this.isInvalidLiteral(i)) {
                        this.reportFatalError(b ? "InvalidCharInTextDecl" : "InvalidCharInXMLDecl", new Object[] { Integer.toString(i, 16) });
                        this.fEntityScanner.scanChar();
                    }
                }
                i = this.fEntityScanner.scanLiteral(peekChar, xmlString);
            } while (i != peekChar);
            this.fStringBuffer2.append(xmlString);
            xmlString.setValues(this.fStringBuffer2);
        }
        if (!this.fEntityScanner.skipChar(peekChar)) {
            this.reportFatalError(b ? "CloseQuoteMissingInTextDecl" : "CloseQuoteMissingInXMLDecl", new Object[] { scanPseudoAttributeName });
        }
        return scanPseudoAttributeName;
    }
    
    private String scanPseudoAttributeName() throws IOException, XNIException {
        switch (this.fEntityScanner.peekChar()) {
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
    
    protected void scanPI() throws IOException, XNIException {
        this.fReportEntity = false;
        String s;
        if (this.fNamespaces) {
            s = this.fEntityScanner.scanNCName();
        }
        else {
            s = this.fEntityScanner.scanName();
        }
        if (s == null) {
            this.reportFatalError("PITargetRequired", null);
        }
        this.scanPIData(s, this.fString);
        this.fReportEntity = true;
    }
    
    protected void scanPIData(final String s, final XMLString xmlString) throws IOException, XNIException {
        if (s.length() == 3) {
            final char lowerCase = Character.toLowerCase(s.charAt(0));
            final char lowerCase2 = Character.toLowerCase(s.charAt(1));
            final char lowerCase3 = Character.toLowerCase(s.charAt(2));
            if (lowerCase == 'x' && lowerCase2 == 'm' && lowerCase3 == 'l') {
                this.reportFatalError("ReservedPITarget", null);
            }
        }
        if (!this.fEntityScanner.skipSpaces()) {
            if (this.fEntityScanner.skipString("?>")) {
                xmlString.clear();
                return;
            }
            if (this.fNamespaces && this.fEntityScanner.peekChar() == 58) {
                this.fEntityScanner.scanChar();
                final XMLStringBuffer xmlStringBuffer = new XMLStringBuffer(s);
                xmlStringBuffer.append(':');
                final String scanName = this.fEntityScanner.scanName();
                if (scanName != null) {
                    xmlStringBuffer.append(scanName);
                }
                this.reportFatalError("ColonNotLegalWithNS", new Object[] { xmlStringBuffer.toString() });
                this.fEntityScanner.skipSpaces();
            }
            else {
                this.reportFatalError("SpaceRequiredInPI", null);
            }
        }
        this.fStringBuffer.clear();
        if (this.fEntityScanner.scanData("?>", this.fStringBuffer)) {
            do {
                final int peekChar = this.fEntityScanner.peekChar();
                if (peekChar != -1) {
                    if (XMLChar.isHighSurrogate(peekChar)) {
                        this.scanSurrogates(this.fStringBuffer);
                    }
                    else {
                        if (!this.isInvalidLiteral(peekChar)) {
                            continue;
                        }
                        this.reportFatalError("InvalidCharInPI", new Object[] { Integer.toHexString(peekChar) });
                        this.fEntityScanner.scanChar();
                    }
                }
            } while (this.fEntityScanner.scanData("?>", this.fStringBuffer));
        }
        xmlString.setValues(this.fStringBuffer);
    }
    
    protected void scanComment(final XMLStringBuffer xmlStringBuffer) throws IOException, XNIException {
        xmlStringBuffer.clear();
        while (this.fEntityScanner.scanData("--", xmlStringBuffer)) {
            final int peekChar = this.fEntityScanner.peekChar();
            if (peekChar != -1) {
                if (XMLChar.isHighSurrogate(peekChar)) {
                    this.scanSurrogates(xmlStringBuffer);
                }
                else {
                    if (!this.isInvalidLiteral(peekChar)) {
                        continue;
                    }
                    this.reportFatalError("InvalidCharInComment", new Object[] { Integer.toHexString(peekChar) });
                    this.fEntityScanner.scanChar();
                }
            }
        }
        if (!this.fEntityScanner.skipChar(62)) {
            this.reportFatalError("DashDashInComment", null);
        }
    }
    
    protected boolean scanAttributeValue(final XMLString values, final XMLString xmlString, final String s, final boolean b, final String s2) throws IOException, XNIException {
        final int peekChar = this.fEntityScanner.peekChar();
        if (peekChar != 39 && peekChar != 34) {
            this.reportFatalError("OpenQuoteExpected", new Object[] { s2, s });
        }
        this.fEntityScanner.scanChar();
        final int fEntityDepth = this.fEntityDepth;
        int n = this.fEntityScanner.scanLiteral(peekChar, values);
        int unchangedByNormalization = 0;
        if (n == peekChar && (unchangedByNormalization = this.isUnchangedByNormalization(values)) == -1) {
            xmlString.setValues(values);
            if (this.fEntityScanner.scanChar() != peekChar) {
                this.reportFatalError("CloseQuoteExpected", new Object[] { s2, s });
            }
            return true;
        }
        this.fStringBuffer2.clear();
        this.fStringBuffer2.append(values);
        this.normalizeWhitespace(values, unchangedByNormalization);
        if (n != peekChar) {
            this.fScanningAttribute = true;
            this.fStringBuffer.clear();
            do {
                this.fStringBuffer.append(values);
                if (n == 38) {
                    this.fEntityScanner.skipChar(38);
                    if (fEntityDepth == this.fEntityDepth) {
                        this.fStringBuffer2.append('&');
                    }
                    if (this.fEntityScanner.skipChar(35)) {
                        if (fEntityDepth == this.fEntityDepth) {
                            this.fStringBuffer2.append('#');
                        }
                        if (this.scanCharReferenceValue(this.fStringBuffer, this.fStringBuffer2) != -1) {}
                    }
                    else {
                        final String scanName = this.fEntityScanner.scanName();
                        if (scanName == null) {
                            this.reportFatalError("NameRequiredInReference", null);
                        }
                        else if (fEntityDepth == this.fEntityDepth) {
                            this.fStringBuffer2.append(scanName);
                        }
                        if (!this.fEntityScanner.skipChar(59)) {
                            this.reportFatalError("SemicolonRequiredInReference", new Object[] { scanName });
                        }
                        else if (fEntityDepth == this.fEntityDepth) {
                            this.fStringBuffer2.append(';');
                        }
                        if (scanName == XMLScanner.fAmpSymbol) {
                            this.fStringBuffer.append('&');
                        }
                        else if (scanName == XMLScanner.fAposSymbol) {
                            this.fStringBuffer.append('\'');
                        }
                        else if (scanName == XMLScanner.fLtSymbol) {
                            this.fStringBuffer.append('<');
                        }
                        else if (scanName == XMLScanner.fGtSymbol) {
                            this.fStringBuffer.append('>');
                        }
                        else if (scanName == XMLScanner.fQuotSymbol) {
                            this.fStringBuffer.append('\"');
                        }
                        else if (this.fEntityManager.isExternalEntity(scanName)) {
                            this.reportFatalError("ReferenceToExternalEntity", new Object[] { scanName });
                        }
                        else {
                            if (!this.fEntityManager.isDeclaredEntity(scanName)) {
                                if (b) {
                                    if (this.fValidation) {
                                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[] { scanName }, (short)1);
                                    }
                                }
                                else {
                                    this.reportFatalError("EntityNotDeclared", new Object[] { scanName });
                                }
                            }
                            this.fEntityManager.startEntity(scanName, true);
                        }
                    }
                }
                else if (n == 60) {
                    this.reportFatalError("LessthanInAttValue", new Object[] { s2, s });
                    this.fEntityScanner.scanChar();
                    if (fEntityDepth == this.fEntityDepth) {
                        this.fStringBuffer2.append((char)n);
                    }
                }
                else if (n == 37 || n == 93) {
                    this.fEntityScanner.scanChar();
                    this.fStringBuffer.append((char)n);
                    if (fEntityDepth == this.fEntityDepth) {
                        this.fStringBuffer2.append((char)n);
                    }
                }
                else if (n == 10 || n == 13) {
                    this.fEntityScanner.scanChar();
                    this.fStringBuffer.append(' ');
                    if (fEntityDepth == this.fEntityDepth) {
                        this.fStringBuffer2.append('\n');
                    }
                }
                else if (n != -1 && XMLChar.isHighSurrogate(n)) {
                    this.fStringBuffer3.clear();
                    if (this.scanSurrogates(this.fStringBuffer3)) {
                        this.fStringBuffer.append(this.fStringBuffer3);
                        if (fEntityDepth == this.fEntityDepth) {
                            this.fStringBuffer2.append(this.fStringBuffer3);
                        }
                    }
                }
                else if (n != -1 && this.isInvalidLiteral(n)) {
                    this.reportFatalError("InvalidCharInAttValue", new Object[] { s2, s, Integer.toString(n, 16) });
                    this.fEntityScanner.scanChar();
                    if (fEntityDepth == this.fEntityDepth) {
                        this.fStringBuffer2.append((char)n);
                    }
                }
                n = this.fEntityScanner.scanLiteral(peekChar, values);
                if (fEntityDepth == this.fEntityDepth) {
                    this.fStringBuffer2.append(values);
                }
                this.normalizeWhitespace(values);
            } while (n != peekChar || fEntityDepth != this.fEntityDepth);
            this.fStringBuffer.append(values);
            values.setValues(this.fStringBuffer);
            this.fScanningAttribute = false;
        }
        xmlString.setValues(this.fStringBuffer2);
        if (this.fEntityScanner.scanChar() != peekChar) {
            this.reportFatalError("CloseQuoteExpected", new Object[] { s2, s });
        }
        return xmlString.equals(values.ch, values.offset, values.length);
    }
    
    protected void scanExternalID(final String[] array, final boolean b) throws IOException, XNIException {
        String string = null;
        String string2 = null;
        if (this.fEntityScanner.skipString("PUBLIC")) {
            if (!this.fEntityScanner.skipSpaces()) {
                this.reportFatalError("SpaceRequiredAfterPUBLIC", null);
            }
            this.scanPubidLiteral(this.fString);
            string2 = this.fString.toString();
            if (!this.fEntityScanner.skipSpaces() && !b) {
                this.reportFatalError("SpaceRequiredBetweenPublicAndSystem", null);
            }
        }
        if (string2 != null || this.fEntityScanner.skipString("SYSTEM")) {
            if (string2 == null && !this.fEntityScanner.skipSpaces()) {
                this.reportFatalError("SpaceRequiredAfterSYSTEM", null);
            }
            final int peekChar = this.fEntityScanner.peekChar();
            if (peekChar != 39 && peekChar != 34) {
                if (string2 != null && b) {
                    array[0] = null;
                    array[1] = string2;
                    return;
                }
                this.reportFatalError("QuoteRequiredInSystemID", null);
            }
            this.fEntityScanner.scanChar();
            XMLString xmlString = this.fString;
            if (this.fEntityScanner.scanLiteral(peekChar, xmlString) != peekChar) {
                this.fStringBuffer.clear();
                do {
                    this.fStringBuffer.append(xmlString);
                    final int peekChar2 = this.fEntityScanner.peekChar();
                    if (XMLChar.isMarkup(peekChar2) || peekChar2 == 93) {
                        this.fStringBuffer.append((char)this.fEntityScanner.scanChar());
                    }
                    else if (XMLChar.isHighSurrogate(peekChar2)) {
                        this.scanSurrogates(this.fStringBuffer);
                    }
                    else {
                        if (!this.isInvalidLiteral(peekChar2)) {
                            continue;
                        }
                        this.reportFatalError("InvalidCharInSystemID", new Object[] { Integer.toHexString(peekChar2) });
                        this.fEntityScanner.scanChar();
                    }
                } while (this.fEntityScanner.scanLiteral(peekChar, xmlString) != peekChar);
                this.fStringBuffer.append(xmlString);
                xmlString = this.fStringBuffer;
            }
            string = xmlString.toString();
            if (!this.fEntityScanner.skipChar(peekChar)) {
                this.reportFatalError("SystemIDUnterminated", null);
            }
        }
        array[0] = string;
        array[1] = string2;
    }
    
    protected boolean scanPubidLiteral(final XMLString xmlString) throws IOException, XNIException {
        final int scanChar = this.fEntityScanner.scanChar();
        if (scanChar != 39 && scanChar != 34) {
            this.reportFatalError("QuoteRequiredInPublicID", null);
            return false;
        }
        this.fStringBuffer.clear();
        int n = 1;
        boolean b = true;
        while (true) {
            final int scanChar2 = this.fEntityScanner.scanChar();
            if (scanChar2 == 32 || scanChar2 == 10 || scanChar2 == 13) {
                if (n != 0) {
                    continue;
                }
                this.fStringBuffer.append(' ');
                n = 1;
            }
            else {
                if (scanChar2 == scanChar) {
                    if (n != 0) {
                        final XMLStringBuffer fStringBuffer = this.fStringBuffer;
                        --fStringBuffer.length;
                    }
                    xmlString.setValues(this.fStringBuffer);
                    return b;
                }
                if (XMLChar.isPubid(scanChar2)) {
                    this.fStringBuffer.append((char)scanChar2);
                    n = 0;
                }
                else {
                    if (scanChar2 == -1) {
                        this.reportFatalError("PublicIDUnterminated", null);
                        return false;
                    }
                    b = false;
                    this.reportFatalError("InvalidCharInPublicID", new Object[] { Integer.toHexString(scanChar2) });
                }
            }
        }
    }
    
    protected void normalizeWhitespace(final XMLString xmlString) {
        for (int n = xmlString.offset + xmlString.length, i = xmlString.offset; i < n; ++i) {
            if (xmlString.ch[i] < ' ') {
                xmlString.ch[i] = ' ';
            }
        }
    }
    
    protected void normalizeWhitespace(final XMLString xmlString, final int n) {
        for (int n2 = xmlString.offset + xmlString.length, i = xmlString.offset + n; i < n2; ++i) {
            if (xmlString.ch[i] < ' ') {
                xmlString.ch[i] = ' ';
            }
        }
    }
    
    protected int isUnchangedByNormalization(final XMLString xmlString) {
        for (int n = xmlString.offset + xmlString.length, i = xmlString.offset; i < n; ++i) {
            if (xmlString.ch[i] < ' ') {
                return i - xmlString.offset;
            }
        }
        return -1;
    }
    
    public void startEntity(final String s, final XMLResourceIdentifier xmlResourceIdentifier, final String s2, final Augmentations augmentations) throws XNIException {
        ++this.fEntityDepth;
        this.fEntityScanner = this.fEntityManager.getEntityScanner();
    }
    
    public void endEntity(final String s, final Augmentations augmentations) throws XNIException {
        --this.fEntityDepth;
    }
    
    protected int scanCharReferenceValue(final XMLStringBuffer xmlStringBuffer, final XMLStringBuffer xmlStringBuffer2) throws IOException, XNIException {
        boolean b = false;
        if (this.fEntityScanner.skipChar(120)) {
            if (xmlStringBuffer2 != null) {
                xmlStringBuffer2.append('x');
            }
            b = true;
            this.fStringBuffer3.clear();
            final int peekChar = this.fEntityScanner.peekChar();
            if ((peekChar >= 48 && peekChar <= 57) || (peekChar >= 97 && peekChar <= 102) || (peekChar >= 65 && peekChar <= 70)) {
                if (xmlStringBuffer2 != null) {
                    xmlStringBuffer2.append((char)peekChar);
                }
                this.fEntityScanner.scanChar();
                this.fStringBuffer3.append((char)peekChar);
                boolean b2;
                do {
                    final int peekChar2 = this.fEntityScanner.peekChar();
                    b2 = ((peekChar2 >= 48 && peekChar2 <= 57) || (peekChar2 >= 97 && peekChar2 <= 102) || (peekChar2 >= 65 && peekChar2 <= 70));
                    if (b2) {
                        if (xmlStringBuffer2 != null) {
                            xmlStringBuffer2.append((char)peekChar2);
                        }
                        this.fEntityScanner.scanChar();
                        this.fStringBuffer3.append((char)peekChar2);
                    }
                } while (b2);
            }
            else {
                this.reportFatalError("HexdigitRequiredInCharRef", null);
            }
        }
        else {
            this.fStringBuffer3.clear();
            final int peekChar3 = this.fEntityScanner.peekChar();
            if (peekChar3 >= 48 && peekChar3 <= 57) {
                if (xmlStringBuffer2 != null) {
                    xmlStringBuffer2.append((char)peekChar3);
                }
                this.fEntityScanner.scanChar();
                this.fStringBuffer3.append((char)peekChar3);
                boolean b3;
                do {
                    final int peekChar4 = this.fEntityScanner.peekChar();
                    b3 = (peekChar4 >= 48 && peekChar4 <= 57);
                    if (b3) {
                        if (xmlStringBuffer2 != null) {
                            xmlStringBuffer2.append((char)peekChar4);
                        }
                        this.fEntityScanner.scanChar();
                        this.fStringBuffer3.append((char)peekChar4);
                    }
                } while (b3);
            }
            else {
                this.reportFatalError("DigitRequiredInCharRef", null);
            }
        }
        if (!this.fEntityScanner.skipChar(59)) {
            this.reportFatalError("SemicolonRequiredInCharRef", null);
        }
        if (xmlStringBuffer2 != null) {
            xmlStringBuffer2.append(';');
        }
        int int1 = -1;
        try {
            int1 = Integer.parseInt(this.fStringBuffer3.toString(), b ? 16 : 10);
            if (this.isInvalid(int1)) {
                final StringBuffer sb = new StringBuffer(this.fStringBuffer3.length + 1);
                if (b) {
                    sb.append('x');
                }
                sb.append(this.fStringBuffer3.ch, this.fStringBuffer3.offset, this.fStringBuffer3.length);
                this.reportFatalError("InvalidCharRef", new Object[] { sb.toString() });
            }
        }
        catch (final NumberFormatException ex) {
            final StringBuffer sb2 = new StringBuffer(this.fStringBuffer3.length + 1);
            if (b) {
                sb2.append('x');
            }
            sb2.append(this.fStringBuffer3.ch, this.fStringBuffer3.offset, this.fStringBuffer3.length);
            this.reportFatalError("InvalidCharRef", new Object[] { sb2.toString() });
        }
        if (!XMLChar.isSupplemental(int1)) {
            xmlStringBuffer.append((char)int1);
        }
        else {
            xmlStringBuffer.append(XMLChar.highSurrogate(int1));
            xmlStringBuffer.append(XMLChar.lowSurrogate(int1));
        }
        if (this.fNotifyCharRefs && int1 != -1) {
            final String string = "#" + (b ? "x" : "") + this.fStringBuffer3.toString();
            if (!this.fScanningAttribute) {
                this.fCharRefLiteral = string;
            }
        }
        return int1;
    }
    
    protected boolean isInvalid(final int n) {
        return XMLChar.isInvalid(n);
    }
    
    protected boolean isInvalidLiteral(final int n) {
        return XMLChar.isInvalid(n);
    }
    
    protected boolean isValidNameChar(final int n) {
        return XMLChar.isName(n);
    }
    
    protected boolean isValidNameStartChar(final int n) {
        return XMLChar.isNameStart(n);
    }
    
    protected boolean isValidNCName(final int n) {
        return XMLChar.isNCName(n);
    }
    
    protected boolean isValidNameStartHighSurrogate(final int n) {
        return false;
    }
    
    protected boolean versionSupported(final String s) {
        return s.equals("1.0");
    }
    
    protected String getVersionNotSupportedKey() {
        return "VersionNotSupported";
    }
    
    protected boolean scanSurrogates(final XMLStringBuffer xmlStringBuffer) throws IOException, XNIException {
        final int scanChar = this.fEntityScanner.scanChar();
        final int peekChar = this.fEntityScanner.peekChar();
        if (!XMLChar.isLowSurrogate(peekChar)) {
            this.reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(scanChar, 16) });
            return false;
        }
        this.fEntityScanner.scanChar();
        final int supplemental = XMLChar.supplemental((char)scanChar, (char)peekChar);
        if (this.isInvalid(supplemental)) {
            this.reportFatalError("InvalidCharInContent", new Object[] { Integer.toString(supplemental, 16) });
            return false;
        }
        xmlStringBuffer.append((char)scanChar);
        xmlStringBuffer.append((char)peekChar);
        return true;
    }
    
    protected void reportFatalError(final String s, final Object[] array) throws XNIException {
        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", s, array, (short)2);
    }
    
    private void init() {
        this.fEntityScanner = null;
        this.fEntityDepth = 0;
        this.fReportEntity = true;
        this.fResourceIdentifier.clear();
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
}
