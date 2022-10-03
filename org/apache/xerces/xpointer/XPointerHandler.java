package org.apache.xerces.xpointer;

import java.util.HashMap;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xinclude.XIncludeNamespaceSupport;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.impl.XMLErrorReporter;
import java.util.ArrayList;
import org.apache.xerces.xinclude.XIncludeHandler;

public final class XPointerHandler extends XIncludeHandler implements XPointerProcessor
{
    protected ArrayList fXPointerParts;
    protected XPointerPart fXPointerPart;
    protected boolean fFoundMatchingPtrPart;
    protected XMLErrorReporter fXPointerErrorReporter;
    protected XMLErrorHandler fErrorHandler;
    protected SymbolTable fSymbolTable;
    private final String ELEMENT_SCHEME_NAME = "element";
    protected boolean fIsXPointerResolved;
    protected boolean fFixupBase;
    protected boolean fFixupLang;
    
    public XPointerHandler() {
        this.fXPointerParts = null;
        this.fXPointerPart = null;
        this.fFoundMatchingPtrPart = false;
        this.fSymbolTable = null;
        this.fIsXPointerResolved = false;
        this.fFixupBase = false;
        this.fFixupLang = false;
        this.fXPointerParts = new ArrayList();
        this.fSymbolTable = new SymbolTable();
    }
    
    public XPointerHandler(final SymbolTable fSymbolTable, final XMLErrorHandler fErrorHandler, final XMLErrorReporter fxPointerErrorReporter) {
        this.fXPointerParts = null;
        this.fXPointerPart = null;
        this.fFoundMatchingPtrPart = false;
        this.fSymbolTable = null;
        this.fIsXPointerResolved = false;
        this.fFixupBase = false;
        this.fFixupLang = false;
        this.fXPointerParts = new ArrayList();
        this.fSymbolTable = fSymbolTable;
        this.fErrorHandler = fErrorHandler;
        this.fXPointerErrorReporter = fxPointerErrorReporter;
    }
    
    public void setDocumentHandler(final XMLDocumentHandler fDocumentHandler) {
        this.fDocumentHandler = fDocumentHandler;
    }
    
    public void parseXPointer(final String s) throws XNIException {
        this.init();
        final Tokens tokens = new Tokens(this.fSymbolTable);
        if (!new Scanner(this.fSymbolTable) {
            protected void addToken(final Tokens tokens, final int n) throws XNIException {
                if (n == 0 || n == 1 || n == 3 || n == 4 || n == 2) {
                    super.addToken(tokens, n);
                    return;
                }
                XPointerHandler.this.reportError("InvalidXPointerToken", new Object[] { tokens.getTokenString(n) });
            }
        }.scanExpr(this.fSymbolTable, tokens, s, 0, s.length())) {
            this.reportError("InvalidXPointerExpression", new Object[] { s });
        }
        while (tokens.hasMore()) {
            switch (tokens.nextToken()) {
                case 2: {
                    final String access$200 = tokens.getTokenString(tokens.nextToken());
                    if (access$200 == null) {
                        this.reportError("InvalidXPointerExpression", new Object[] { s });
                    }
                    final ShortHandPointer shortHandPointer = new ShortHandPointer(this.fSymbolTable);
                    shortHandPointer.setSchemeName(access$200);
                    this.fXPointerParts.add(shortHandPointer);
                    continue;
                }
                case 3: {
                    final String string = tokens.getTokenString(tokens.nextToken()) + tokens.getTokenString(tokens.nextToken());
                    int n = 0;
                    int n2 = 0;
                    final int access$201 = tokens.nextToken();
                    if (tokens.getTokenString(access$201) != "XPTRTOKEN_OPEN_PAREN") {
                        if (access$201 == 2) {
                            this.reportError("MultipleShortHandPointers", new Object[] { s });
                        }
                        else {
                            this.reportError("InvalidXPointerExpression", new Object[] { s });
                        }
                    }
                    ++n;
                    while (tokens.hasMore() && tokens.getTokenString(tokens.nextToken()) == "XPTRTOKEN_OPEN_PAREN") {
                        ++n;
                    }
                    final String access$202 = tokens.getTokenString(tokens.nextToken());
                    if (tokens.getTokenString(tokens.nextToken()) != "XPTRTOKEN_CLOSE_PAREN") {
                        this.reportError("SchemeDataNotFollowedByCloseParenthesis", new Object[] { s });
                    }
                    ++n2;
                    while (tokens.hasMore() && tokens.getTokenString(tokens.peekToken()) == "XPTRTOKEN_OPEN_PAREN") {
                        ++n2;
                    }
                    if (n != n2) {
                        this.reportError("UnbalancedParenthesisInXPointerExpression", new Object[] { s, new Integer(n), new Integer(n2) });
                    }
                    if (string.equals("element")) {
                        final ElementSchemePointer elementSchemePointer = new ElementSchemePointer(this.fSymbolTable, this.fErrorReporter);
                        elementSchemePointer.setSchemeName(string);
                        elementSchemePointer.setSchemeData(access$202);
                        try {
                            elementSchemePointer.parseXPointer(access$202);
                            this.fXPointerParts.add(elementSchemePointer);
                            continue;
                        }
                        catch (final XNIException ex) {
                            throw new XNIException(ex);
                        }
                    }
                    this.reportWarning("SchemeUnsupported", new Object[] { string });
                    continue;
                }
                default: {
                    this.reportError("InvalidXPointerExpression", new Object[] { s });
                    continue;
                }
            }
        }
    }
    
    public boolean resolveXPointer(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations, final int n) throws XNIException {
        boolean fIsXPointerResolved = false;
        if (!this.fFoundMatchingPtrPart) {
            for (int i = 0; i < this.fXPointerParts.size(); ++i) {
                this.fXPointerPart = (XPointerPart)this.fXPointerParts.get(i);
                if (this.fXPointerPart.resolveXPointer(qName, xmlAttributes, augmentations, n)) {
                    this.fFoundMatchingPtrPart = true;
                    fIsXPointerResolved = true;
                }
            }
        }
        else if (this.fXPointerPart.resolveXPointer(qName, xmlAttributes, augmentations, n)) {
            fIsXPointerResolved = true;
        }
        if (!this.fIsXPointerResolved) {
            this.fIsXPointerResolved = fIsXPointerResolved;
        }
        return fIsXPointerResolved;
    }
    
    public boolean isFragmentResolved() throws XNIException {
        final boolean fIsXPointerResolved = this.fXPointerPart != null && this.fXPointerPart.isFragmentResolved();
        if (!this.fIsXPointerResolved) {
            this.fIsXPointerResolved = fIsXPointerResolved;
        }
        return fIsXPointerResolved;
    }
    
    public boolean isChildFragmentResolved() throws XNIException {
        return this.fXPointerPart != null && this.fXPointerPart.isChildFragmentResolved();
    }
    
    public boolean isXPointerResolved() throws XNIException {
        return this.fIsXPointerResolved;
    }
    
    public XPointerPart getXPointerPart() {
        return this.fXPointerPart;
    }
    
    private void reportError(final String s, final Object[] array) throws XNIException {
        throw new XNIException(this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/XPTR").formatMessage(this.fErrorReporter.getLocale(), s, array));
    }
    
    private void reportWarning(final String s, final Object[] array) throws XNIException {
        this.fXPointerErrorReporter.reportError("http://www.w3.org/TR/XPTR", s, array, (short)0);
    }
    
    protected void initErrorReporter() {
        if (this.fXPointerErrorReporter == null) {
            this.fXPointerErrorReporter = new XMLErrorReporter();
        }
        if (this.fErrorHandler == null) {
            this.fErrorHandler = new XPointerErrorHandler();
        }
        this.fXPointerErrorReporter.putMessageFormatter("http://www.w3.org/TR/XPTR", new XPointerMessageFormatter());
    }
    
    protected void init() {
        this.fXPointerParts.clear();
        this.fXPointerPart = null;
        this.fFoundMatchingPtrPart = false;
        this.fIsXPointerResolved = false;
        this.initErrorReporter();
    }
    
    public ArrayList getPointerParts() {
        return this.fXPointerParts;
    }
    
    public void comment(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.comment(xmlString, augmentations);
    }
    
    public void processingInstruction(final String s, final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.processingInstruction(s, xmlString, augmentations);
    }
    
    public void startElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        if (!this.resolveXPointer(qName, xmlAttributes, augmentations, 0)) {
            if (this.fFixupBase) {
                this.processXMLBaseAttributes(xmlAttributes);
            }
            if (this.fFixupLang) {
                this.processXMLLangAttributes(xmlAttributes);
            }
            this.fNamespaceContext.setContextInvalid();
            return;
        }
        super.startElement(qName, xmlAttributes, augmentations);
    }
    
    public void emptyElement(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations) throws XNIException {
        if (!this.resolveXPointer(qName, xmlAttributes, augmentations, 2)) {
            if (this.fFixupBase) {
                this.processXMLBaseAttributes(xmlAttributes);
            }
            if (this.fFixupLang) {
                this.processXMLLangAttributes(xmlAttributes);
            }
            this.fNamespaceContext.setContextInvalid();
            return;
        }
        super.emptyElement(qName, xmlAttributes, augmentations);
    }
    
    public void characters(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.characters(xmlString, augmentations);
    }
    
    public void ignorableWhitespace(final XMLString xmlString, final Augmentations augmentations) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.ignorableWhitespace(xmlString, augmentations);
    }
    
    public void endElement(final QName qName, final Augmentations augmentations) throws XNIException {
        if (!this.resolveXPointer(qName, null, augmentations, 1)) {
            return;
        }
        super.endElement(qName, augmentations);
    }
    
    public void startCDATA(final Augmentations augmentations) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.startCDATA(augmentations);
    }
    
    public void endCDATA(final Augmentations augmentations) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.endCDATA(augmentations);
    }
    
    public void setProperty(final String s, final Object o) throws XMLConfigurationException {
        if (s == "http://apache.org/xml/properties/internal/error-reporter") {
            if (o != null) {
                this.fXPointerErrorReporter = (XMLErrorReporter)o;
            }
            else {
                this.fXPointerErrorReporter = null;
            }
        }
        if (s == "http://apache.org/xml/properties/internal/error-handler") {
            if (o != null) {
                this.fErrorHandler = (XMLErrorHandler)o;
            }
            else {
                this.fErrorHandler = null;
            }
        }
        if (s == "http://apache.org/xml/features/xinclude/fixup-language") {
            if (o != null) {
                this.fFixupLang = (boolean)o;
            }
            else {
                this.fFixupLang = false;
            }
        }
        if (s == "http://apache.org/xml/features/xinclude/fixup-base-uris") {
            if (o != null) {
                this.fFixupBase = (boolean)o;
            }
            else {
                this.fFixupBase = false;
            }
        }
        if (s == "http://apache.org/xml/properties/internal/namespace-context") {
            this.fNamespaceContext = (XIncludeNamespaceSupport)o;
        }
        super.setProperty(s, o);
    }
    
    private class Scanner
    {
        private static final byte CHARTYPE_INVALID = 0;
        private static final byte CHARTYPE_OTHER = 1;
        private static final byte CHARTYPE_WHITESPACE = 2;
        private static final byte CHARTYPE_CARRET = 3;
        private static final byte CHARTYPE_OPEN_PAREN = 4;
        private static final byte CHARTYPE_CLOSE_PAREN = 5;
        private static final byte CHARTYPE_MINUS = 6;
        private static final byte CHARTYPE_PERIOD = 7;
        private static final byte CHARTYPE_SLASH = 8;
        private static final byte CHARTYPE_DIGIT = 9;
        private static final byte CHARTYPE_COLON = 10;
        private static final byte CHARTYPE_EQUAL = 11;
        private static final byte CHARTYPE_LETTER = 12;
        private static final byte CHARTYPE_UNDERSCORE = 13;
        private static final byte CHARTYPE_NONASCII = 14;
        private final byte[] fASCIICharMap;
        private SymbolTable fSymbolTable;
        
        private Scanner(final SymbolTable fSymbolTable) {
            this.fASCIICharMap = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 1, 1, 1, 1, 1, 4, 5, 1, 1, 1, 6, 7, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 1, 1, 11, 1, 1, 1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 1, 1, 1, 3, 13, 1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 1, 1, 1, 1, 1 };
            this.fSymbolTable = fSymbolTable;
        }
        
        private boolean scanExpr(final SymbolTable symbolTable, final Tokens tokens, final String s, int i, final int n) throws XNIException {
            int n2 = 0;
            int n3 = 0;
            String s2 = null;
            final StringBuffer sb = new StringBuffer();
            while (i != n) {
                char c;
                for (c = s.charAt(i); (c == ' ' || c == '\n' || c == '\t' || c == '\r') && ++i != n; c = s.charAt(i)) {}
                if (i == n) {
                    return true;
                }
                switch ((c >= '\u0080') ? 14 : this.fASCIICharMap[c]) {
                    case 4: {
                        this.addToken(tokens, 0);
                        ++n2;
                        ++i;
                        continue;
                    }
                    case 5: {
                        this.addToken(tokens, 1);
                        ++n3;
                        ++i;
                        continue;
                    }
                    case 1:
                    case 2:
                    case 3:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14: {
                        if (n2 == 0) {
                            final int n4 = i;
                            i = this.scanNCName(s, n, i);
                            if (i == n4) {
                                XPointerHandler.this.reportError("InvalidShortHandPointer", new Object[] { s });
                                return false;
                            }
                            int char1;
                            if (i < n) {
                                char1 = s.charAt(i);
                            }
                            else {
                                char1 = -1;
                            }
                            s2 = symbolTable.addSymbol(s.substring(n4, i));
                            String empty_STRING = XMLSymbols.EMPTY_STRING;
                            if (char1 == 58) {
                                if (++i == n) {
                                    return false;
                                }
                                s.charAt(i);
                                empty_STRING = s2;
                                final int n5 = i;
                                i = this.scanNCName(s, n, i);
                                if (i == n5) {
                                    return false;
                                }
                                if (i < n) {
                                    s.charAt(i);
                                }
                                s2 = symbolTable.addSymbol(s.substring(n5, i));
                            }
                            if (i != n) {
                                this.addToken(tokens, 3);
                                tokens.addToken(empty_STRING);
                                tokens.addToken(s2);
                            }
                            else if (i == n) {
                                this.addToken(tokens, 2);
                                tokens.addToken(s2);
                            }
                            n3 = 0;
                            continue;
                        }
                        else {
                            if (n2 <= 0 || n3 != 0 || s2 == null) {
                                return false;
                            }
                            final int n6 = i;
                            i = this.scanData(s, sb, n, i);
                            if (i == n6) {
                                XPointerHandler.this.reportError("InvalidSchemeDataInXPointer", new Object[] { s });
                                return false;
                            }
                            if (i < n) {
                                s.charAt(i);
                            }
                            final String addSymbol = symbolTable.addSymbol(sb.toString());
                            this.addToken(tokens, 4);
                            tokens.addToken(addSymbol);
                            n2 = 0;
                            sb.delete(0, sb.length());
                            continue;
                        }
                        break;
                    }
                }
            }
            return true;
        }
        
        private int scanNCName(final String s, final int n, int n2) {
            final char char1 = s.charAt(n2);
            if (char1 >= '\u0080') {
                if (!XMLChar.isNameStart(char1)) {
                    return n2;
                }
            }
            else {
                final byte b = this.fASCIICharMap[char1];
                if (b != 12 && b != 13) {
                    return n2;
                }
            }
            while (++n2 < n) {
                final char char2 = s.charAt(n2);
                if (char2 >= '\u0080') {
                    if (!XMLChar.isName(char2)) {
                        break;
                    }
                    continue;
                }
                else {
                    final byte b2 = this.fASCIICharMap[char2];
                    if (b2 != 12 && b2 != 9 && b2 != 7 && b2 != 6 && b2 != 13) {
                        break;
                    }
                    continue;
                }
            }
            return n2;
        }
        
        private int scanData(final String s, final StringBuffer sb, final int n, int i) {
            while (i != n) {
                final char char1 = s.charAt(i);
                final byte b = (byte)((char1 >= '\u0080') ? 14 : this.fASCIICharMap[char1]);
                if (b == 4) {
                    sb.append((int)char1);
                    i = this.scanData(s, sb, n, ++i);
                    if (i == n) {
                        return i;
                    }
                    final char char2 = s.charAt(i);
                    if (((char2 >= '\u0080') ? 14 : this.fASCIICharMap[char2]) != 5) {
                        return n;
                    }
                    sb.append(char2);
                    ++i;
                }
                else {
                    if (b == 5) {
                        return i;
                    }
                    if (b == 3) {
                        final char char3 = s.charAt(++i);
                        final byte b2 = (byte)((char3 >= '\u0080') ? 14 : this.fASCIICharMap[char3]);
                        if (b2 != 3 && b2 != 4 && b2 != 5) {
                            return i;
                        }
                        sb.append(char3);
                        ++i;
                    }
                    else {
                        sb.append(char1);
                        ++i;
                    }
                }
            }
            return i;
        }
        
        protected void addToken(final Tokens tokens, final int n) throws XNIException {
            tokens.addToken(n);
        }
    }
    
    private final class Tokens
    {
        private static final int XPTRTOKEN_OPEN_PAREN = 0;
        private static final int XPTRTOKEN_CLOSE_PAREN = 1;
        private static final int XPTRTOKEN_SHORTHAND = 2;
        private static final int XPTRTOKEN_SCHEMENAME = 3;
        private static final int XPTRTOKEN_SCHEMEDATA = 4;
        private final String[] fgTokenNames;
        private static final int INITIAL_TOKEN_COUNT = 256;
        private int[] fTokens;
        private int fTokenCount;
        private int fCurrentTokenIndex;
        private SymbolTable fSymbolTable;
        private HashMap fTokenNames;
        
        private Tokens(final SymbolTable fSymbolTable) {
            this.fgTokenNames = new String[] { "XPTRTOKEN_OPEN_PAREN", "XPTRTOKEN_CLOSE_PAREN", "XPTRTOKEN_SHORTHAND", "XPTRTOKEN_SCHEMENAME", "XPTRTOKEN_SCHEMEDATA" };
            this.fTokens = new int[256];
            this.fTokenCount = 0;
            this.fTokenNames = new HashMap();
            this.fSymbolTable = fSymbolTable;
            this.fTokenNames.put(new Integer(0), "XPTRTOKEN_OPEN_PAREN");
            this.fTokenNames.put(new Integer(1), "XPTRTOKEN_CLOSE_PAREN");
            this.fTokenNames.put(new Integer(2), "XPTRTOKEN_SHORTHAND");
            this.fTokenNames.put(new Integer(3), "XPTRTOKEN_SCHEMENAME");
            this.fTokenNames.put(new Integer(4), "XPTRTOKEN_SCHEMEDATA");
        }
        
        private String getTokenString(final int n) {
            return this.fTokenNames.get(new Integer(n));
        }
        
        private void addToken(final String s) {
            Integer n = this.fTokenNames.get(s);
            if (n == null) {
                n = new Integer(this.fTokenNames.size());
                this.fTokenNames.put(n, s);
            }
            this.addToken(n);
        }
        
        private void addToken(final int n) {
            try {
                this.fTokens[this.fTokenCount] = n;
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                System.arraycopy(this.fTokens, 0, this.fTokens = new int[this.fTokenCount << 1], 0, this.fTokenCount);
                this.fTokens[this.fTokenCount] = n;
            }
            ++this.fTokenCount;
        }
        
        private void rewind() {
            this.fCurrentTokenIndex = 0;
        }
        
        private boolean hasMore() {
            return this.fCurrentTokenIndex < this.fTokenCount;
        }
        
        private int nextToken() throws XNIException {
            if (this.fCurrentTokenIndex == this.fTokenCount) {
                XPointerHandler.this.reportError("XPointerProcessingError", null);
            }
            return this.fTokens[this.fCurrentTokenIndex++];
        }
        
        private int peekToken() throws XNIException {
            if (this.fCurrentTokenIndex == this.fTokenCount) {
                XPointerHandler.this.reportError("XPointerProcessingError", null);
            }
            return this.fTokens[this.fCurrentTokenIndex];
        }
        
        private String nextTokenAsString() throws XNIException {
            final String tokenString = this.getTokenString(this.nextToken());
            if (tokenString == null) {
                XPointerHandler.this.reportError("XPointerProcessingError", null);
            }
            return tokenString;
        }
    }
}
