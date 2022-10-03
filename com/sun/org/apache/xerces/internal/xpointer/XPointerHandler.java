package com.sun.org.apache.xerces.internal.xpointer;

import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import java.util.Hashtable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeNamespaceSupport;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import java.util.Vector;
import com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler;

public final class XPointerHandler extends XIncludeHandler implements XPointerProcessor
{
    protected Vector fXPointerParts;
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
        this.fXPointerParts = new Vector();
        this.fSymbolTable = new SymbolTable();
    }
    
    public XPointerHandler(final SymbolTable symbolTable, final XMLErrorHandler errorHandler, final XMLErrorReporter errorReporter) {
        this.fXPointerParts = null;
        this.fXPointerPart = null;
        this.fFoundMatchingPtrPart = false;
        this.fSymbolTable = null;
        this.fIsXPointerResolved = false;
        this.fFixupBase = false;
        this.fFixupLang = false;
        this.fXPointerParts = new Vector();
        this.fSymbolTable = symbolTable;
        this.fErrorHandler = errorHandler;
        this.fXPointerErrorReporter = errorReporter;
    }
    
    @Override
    public void parseXPointer(final String xpointer) throws XNIException {
        this.init();
        final Tokens tokens = new Tokens(this.fSymbolTable);
        final Scanner scanner = new Scanner(this.fSymbolTable) {
            @Override
            protected void addToken(final Tokens tokens, final int token) throws XNIException {
                if (token == 0 || token == 1 || token == 3 || token == 4 || token == 2) {
                    super.addToken(tokens, token);
                    return;
                }
                XPointerHandler.this.reportError("InvalidXPointerToken", new Object[] { tokens.getTokenString(token) });
            }
        };
        final int length = xpointer.length();
        final boolean success = scanner.scanExpr(this.fSymbolTable, tokens, xpointer, 0, length);
        if (!success) {
            this.reportError("InvalidXPointerExpression", new Object[] { xpointer });
        }
        while (tokens.hasMore()) {
            int token = tokens.nextToken();
            switch (token) {
                case 2: {
                    token = tokens.nextToken();
                    final String shortHandPointerName = tokens.getTokenString(token);
                    if (shortHandPointerName == null) {
                        this.reportError("InvalidXPointerExpression", new Object[] { xpointer });
                    }
                    final XPointerPart shortHandPointer = new ShortHandPointer(this.fSymbolTable);
                    shortHandPointer.setSchemeName(shortHandPointerName);
                    this.fXPointerParts.add(shortHandPointer);
                    continue;
                }
                case 3: {
                    token = tokens.nextToken();
                    final String prefix = tokens.getTokenString(token);
                    token = tokens.nextToken();
                    final String localName = tokens.getTokenString(token);
                    final String schemeName = prefix + localName;
                    int openParenCount = 0;
                    int closeParenCount = 0;
                    token = tokens.nextToken();
                    final String openParen = tokens.getTokenString(token);
                    if (openParen != "XPTRTOKEN_OPEN_PAREN") {
                        if (token == 2) {
                            this.reportError("MultipleShortHandPointers", new Object[] { xpointer });
                        }
                        else {
                            this.reportError("InvalidXPointerExpression", new Object[] { xpointer });
                        }
                    }
                    ++openParenCount;
                    String schemeData = null;
                    while (tokens.hasMore()) {
                        token = tokens.nextToken();
                        schemeData = tokens.getTokenString(token);
                        if (schemeData != "XPTRTOKEN_OPEN_PAREN") {
                            break;
                        }
                        ++openParenCount;
                    }
                    token = tokens.nextToken();
                    schemeData = tokens.getTokenString(token);
                    token = tokens.nextToken();
                    final String closeParen = tokens.getTokenString(token);
                    if (closeParen != "XPTRTOKEN_CLOSE_PAREN") {
                        this.reportError("SchemeDataNotFollowedByCloseParenthesis", new Object[] { xpointer });
                    }
                    ++closeParenCount;
                    while (tokens.hasMore() && tokens.getTokenString(tokens.peekToken()) == "XPTRTOKEN_OPEN_PAREN") {
                        ++closeParenCount;
                    }
                    if (openParenCount != closeParenCount) {
                        this.reportError("UnbalancedParenthesisInXPointerExpression", new Object[] { xpointer, new Integer(openParenCount), new Integer(closeParenCount) });
                    }
                    if (schemeName.equals("element")) {
                        final XPointerPart elementSchemePointer = new ElementSchemePointer(this.fSymbolTable, this.fErrorReporter);
                        elementSchemePointer.setSchemeName(schemeName);
                        elementSchemePointer.setSchemeData(schemeData);
                        try {
                            elementSchemePointer.parseXPointer(schemeData);
                            this.fXPointerParts.add(elementSchemePointer);
                        }
                        catch (final XNIException e) {
                            throw new XNIException(e);
                        }
                        continue;
                    }
                    this.reportWarning("SchemeUnsupported", new Object[] { schemeName });
                    continue;
                }
                default: {
                    this.reportError("InvalidXPointerExpression", new Object[] { xpointer });
                    continue;
                }
            }
        }
    }
    
    @Override
    public boolean resolveXPointer(final QName element, final XMLAttributes attributes, final Augmentations augs, final int event) throws XNIException {
        boolean resolved = false;
        if (!this.fFoundMatchingPtrPart) {
            for (int i = 0; i < this.fXPointerParts.size(); ++i) {
                this.fXPointerPart = this.fXPointerParts.get(i);
                if (this.fXPointerPart.resolveXPointer(element, attributes, augs, event)) {
                    this.fFoundMatchingPtrPart = true;
                    resolved = true;
                }
            }
        }
        else if (this.fXPointerPart.resolveXPointer(element, attributes, augs, event)) {
            resolved = true;
        }
        if (!this.fIsXPointerResolved) {
            this.fIsXPointerResolved = resolved;
        }
        return resolved;
    }
    
    @Override
    public boolean isFragmentResolved() throws XNIException {
        final boolean resolved = this.fXPointerPart != null && this.fXPointerPart.isFragmentResolved();
        if (!this.fIsXPointerResolved) {
            this.fIsXPointerResolved = resolved;
        }
        return resolved;
    }
    
    public boolean isChildFragmentResolved() throws XNIException {
        final boolean resolved = this.fXPointerPart != null && this.fXPointerPart.isChildFragmentResolved();
        return resolved;
    }
    
    @Override
    public boolean isXPointerResolved() throws XNIException {
        return this.fIsXPointerResolved;
    }
    
    public XPointerPart getXPointerPart() {
        return this.fXPointerPart;
    }
    
    private void reportError(final String key, final Object[] arguments) throws XNIException {
        throw new XNIException(this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/XPTR").formatMessage(this.fErrorReporter.getLocale(), key, arguments));
    }
    
    private void reportWarning(final String key, final Object[] arguments) throws XNIException {
        this.fXPointerErrorReporter.reportError("http://www.w3.org/TR/XPTR", key, arguments, (short)0);
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
    
    public Vector getPointerParts() {
        return this.fXPointerParts;
    }
    
    @Override
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.comment(text, augs);
    }
    
    @Override
    public void processingInstruction(final String target, final XMLString data, final Augmentations augs) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.processingInstruction(target, data, augs);
    }
    
    @Override
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (!this.resolveXPointer(element, attributes, augs, 0)) {
            if (this.fFixupBase) {
                this.processXMLBaseAttributes(attributes);
            }
            if (this.fFixupLang) {
                this.processXMLLangAttributes(attributes);
            }
            this.fNamespaceContext.setContextInvalid();
            return;
        }
        super.startElement(element, attributes, augs);
    }
    
    @Override
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (!this.resolveXPointer(element, attributes, augs, 2)) {
            if (this.fFixupBase) {
                this.processXMLBaseAttributes(attributes);
            }
            if (this.fFixupLang) {
                this.processXMLLangAttributes(attributes);
            }
            this.fNamespaceContext.setContextInvalid();
            return;
        }
        super.emptyElement(element, attributes, augs);
    }
    
    @Override
    public void characters(final XMLString text, final Augmentations augs) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.characters(text, augs);
    }
    
    @Override
    public void ignorableWhitespace(final XMLString text, final Augmentations augs) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.ignorableWhitespace(text, augs);
    }
    
    @Override
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        if (!this.resolveXPointer(element, null, augs, 1)) {
            return;
        }
        super.endElement(element, augs);
    }
    
    @Override
    public void startCDATA(final Augmentations augs) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.startCDATA(augs);
    }
    
    @Override
    public void endCDATA(final Augmentations augs) throws XNIException {
        if (!this.isChildFragmentResolved()) {
            return;
        }
        super.endCDATA(augs);
    }
    
    @Override
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        if (propertyId == "http://apache.org/xml/properties/internal/error-reporter") {
            if (value != null) {
                this.fXPointerErrorReporter = (XMLErrorReporter)value;
            }
            else {
                this.fXPointerErrorReporter = null;
            }
        }
        if (propertyId == "http://apache.org/xml/properties/internal/error-handler") {
            if (value != null) {
                this.fErrorHandler = (XMLErrorHandler)value;
            }
            else {
                this.fErrorHandler = null;
            }
        }
        if (propertyId == "http://apache.org/xml/features/xinclude/fixup-language") {
            if (value != null) {
                this.fFixupLang = (boolean)value;
            }
            else {
                this.fFixupLang = false;
            }
        }
        if (propertyId == "http://apache.org/xml/features/xinclude/fixup-base-uris") {
            if (value != null) {
                this.fFixupBase = (boolean)value;
            }
            else {
                this.fFixupBase = false;
            }
        }
        if (propertyId == "http://apache.org/xml/properties/internal/namespace-context") {
            this.fNamespaceContext = (XIncludeNamespaceSupport)value;
        }
        super.setProperty(propertyId, value);
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
        private Hashtable fTokenNames;
        
        private Tokens(final SymbolTable symbolTable) {
            this.fgTokenNames = new String[] { "XPTRTOKEN_OPEN_PAREN", "XPTRTOKEN_CLOSE_PAREN", "XPTRTOKEN_SHORTHAND", "XPTRTOKEN_SCHEMENAME", "XPTRTOKEN_SCHEMEDATA" };
            this.fTokens = new int[256];
            this.fTokenCount = 0;
            this.fTokenNames = new Hashtable();
            this.fSymbolTable = symbolTable;
            this.fTokenNames.put(new Integer(0), "XPTRTOKEN_OPEN_PAREN");
            this.fTokenNames.put(new Integer(1), "XPTRTOKEN_CLOSE_PAREN");
            this.fTokenNames.put(new Integer(2), "XPTRTOKEN_SHORTHAND");
            this.fTokenNames.put(new Integer(3), "XPTRTOKEN_SCHEMENAME");
            this.fTokenNames.put(new Integer(4), "XPTRTOKEN_SCHEMEDATA");
        }
        
        private String getTokenString(final int token) {
            return this.fTokenNames.get(new Integer(token));
        }
        
        private void addToken(final String tokenStr) {
            Integer tokenInt = this.fTokenNames.get(tokenStr);
            if (tokenInt == null) {
                tokenInt = new Integer(this.fTokenNames.size());
                this.fTokenNames.put(tokenInt, tokenStr);
            }
            this.addToken(tokenInt);
        }
        
        private void addToken(final int token) {
            try {
                this.fTokens[this.fTokenCount] = token;
            }
            catch (final ArrayIndexOutOfBoundsException ex) {
                final int[] oldList = this.fTokens;
                System.arraycopy(oldList, 0, this.fTokens = new int[this.fTokenCount << 1], 0, this.fTokenCount);
                this.fTokens[this.fTokenCount] = token;
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
            final String tokenStrint = this.getTokenString(this.nextToken());
            if (tokenStrint == null) {
                XPointerHandler.this.reportError("XPointerProcessingError", null);
            }
            return tokenStrint;
        }
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
        
        private Scanner(final SymbolTable symbolTable) {
            this.fASCIICharMap = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 1, 1, 1, 1, 1, 4, 5, 1, 1, 1, 6, 7, 8, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 1, 1, 11, 1, 1, 1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 1, 1, 1, 3, 13, 1, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 1, 1, 1, 1, 1 };
            this.fSymbolTable = symbolTable;
        }
        
        private boolean scanExpr(final SymbolTable symbolTable, final Tokens tokens, final String data, int currentOffset, final int endOffset) throws XNIException {
            int openParen = 0;
            int closeParen = 0;
            boolean isQName = false;
            String name = null;
            String prefix = null;
            String schemeData = null;
            final StringBuffer schemeDataBuff = new StringBuffer();
            while (currentOffset != endOffset) {
                int ch;
                for (ch = data.charAt(currentOffset); (ch == 32 || ch == 10 || ch == 9 || ch == 13) && ++currentOffset != endOffset; ch = data.charAt(currentOffset)) {}
                if (currentOffset == endOffset) {
                    return true;
                }
                final byte chartype = (byte)((ch >= 128) ? 14 : this.fASCIICharMap[ch]);
                switch (chartype) {
                    case 4: {
                        this.addToken(tokens, 0);
                        ++openParen;
                        ++currentOffset;
                        continue;
                    }
                    case 5: {
                        this.addToken(tokens, 1);
                        ++closeParen;
                        ++currentOffset;
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
                        if (openParen == 0) {
                            int nameOffset = currentOffset;
                            currentOffset = this.scanNCName(data, endOffset, currentOffset);
                            if (currentOffset == nameOffset) {
                                XPointerHandler.this.reportError("InvalidShortHandPointer", new Object[] { data });
                                return false;
                            }
                            if (currentOffset < endOffset) {
                                ch = data.charAt(currentOffset);
                            }
                            else {
                                ch = -1;
                            }
                            name = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
                            prefix = XMLSymbols.EMPTY_STRING;
                            if (ch == 58) {
                                if (++currentOffset == endOffset) {
                                    return false;
                                }
                                ch = data.charAt(currentOffset);
                                prefix = name;
                                nameOffset = currentOffset;
                                currentOffset = this.scanNCName(data, endOffset, currentOffset);
                                if (currentOffset == nameOffset) {
                                    return false;
                                }
                                if (currentOffset < endOffset) {
                                    ch = data.charAt(currentOffset);
                                }
                                else {
                                    ch = -1;
                                }
                                isQName = true;
                                name = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
                            }
                            if (currentOffset != endOffset) {
                                this.addToken(tokens, 3);
                                tokens.addToken(prefix);
                                tokens.addToken(name);
                                isQName = false;
                            }
                            else if (currentOffset == endOffset) {
                                this.addToken(tokens, 2);
                                tokens.addToken(name);
                                isQName = false;
                            }
                            closeParen = 0;
                            continue;
                        }
                        else {
                            if (openParen <= 0 || closeParen != 0 || name == null) {
                                return false;
                            }
                            final int dataOffset = currentOffset;
                            currentOffset = this.scanData(data, schemeDataBuff, endOffset, currentOffset);
                            if (currentOffset == dataOffset) {
                                XPointerHandler.this.reportError("InvalidSchemeDataInXPointer", new Object[] { data });
                                return false;
                            }
                            if (currentOffset < endOffset) {
                                ch = data.charAt(currentOffset);
                            }
                            else {
                                ch = -1;
                            }
                            schemeData = symbolTable.addSymbol(schemeDataBuff.toString());
                            this.addToken(tokens, 4);
                            tokens.addToken(schemeData);
                            openParen = 0;
                            schemeDataBuff.delete(0, schemeDataBuff.length());
                            continue;
                        }
                        break;
                    }
                }
            }
            return true;
        }
        
        private int scanNCName(final String data, final int endOffset, int currentOffset) {
            int ch = data.charAt(currentOffset);
            if (ch >= 128) {
                if (!XMLChar.isNameStart(ch)) {
                    return currentOffset;
                }
            }
            else {
                final byte chartype = this.fASCIICharMap[ch];
                if (chartype != 12 && chartype != 13) {
                    return currentOffset;
                }
            }
            while (++currentOffset < endOffset) {
                ch = data.charAt(currentOffset);
                if (ch >= 128) {
                    if (!XMLChar.isName(ch)) {
                        break;
                    }
                    continue;
                }
                else {
                    final byte chartype = this.fASCIICharMap[ch];
                    if (chartype != 12 && chartype != 9 && chartype != 7 && chartype != 6 && chartype != 13) {
                        break;
                    }
                    continue;
                }
            }
            return currentOffset;
        }
        
        private int scanData(final String data, final StringBuffer schemeData, final int endOffset, int currentOffset) {
            while (currentOffset != endOffset) {
                int ch = data.charAt(currentOffset);
                byte chartype = (byte)((ch >= 128) ? 14 : this.fASCIICharMap[ch]);
                if (chartype == 4) {
                    schemeData.append(ch);
                    currentOffset = this.scanData(data, schemeData, endOffset, ++currentOffset);
                    if (currentOffset == endOffset) {
                        return currentOffset;
                    }
                    ch = data.charAt(currentOffset);
                    chartype = (byte)((ch >= 128) ? 14 : this.fASCIICharMap[ch]);
                    if (chartype != 5) {
                        return endOffset;
                    }
                    schemeData.append((char)ch);
                    ++currentOffset;
                }
                else {
                    if (chartype == 5) {
                        return currentOffset;
                    }
                    if (chartype == 3) {
                        ch = data.charAt(++currentOffset);
                        chartype = (byte)((ch >= 128) ? 14 : this.fASCIICharMap[ch]);
                        if (chartype != 3 && chartype != 4 && chartype != 5) {
                            return currentOffset;
                        }
                        schemeData.append((char)ch);
                        ++currentOffset;
                    }
                    else {
                        schemeData.append((char)ch);
                        ++currentOffset;
                    }
                }
            }
            return currentOffset;
        }
        
        protected void addToken(final Tokens tokens, final int token) throws XNIException {
            tokens.addToken(token);
        }
    }
}
