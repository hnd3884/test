package com.sun.org.apache.xerces.internal.xpointer;

import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.util.Hashtable;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;

class ElementSchemePointer implements XPointerPart
{
    private String fSchemeName;
    private String fSchemeData;
    private String fShortHandPointerName;
    private boolean fIsResolveElement;
    private boolean fIsElementFound;
    private boolean fWasOnlyEmptyElementFound;
    boolean fIsShortHand;
    int fFoundDepth;
    private int[] fChildSequence;
    private int fCurrentChildPosition;
    private int fCurrentChildDepth;
    private int[] fCurrentChildSequence;
    private boolean fIsFragmentResolved;
    private ShortHandPointer fShortHandPointer;
    protected XMLErrorReporter fErrorReporter;
    protected XMLErrorHandler fErrorHandler;
    private SymbolTable fSymbolTable;
    
    public ElementSchemePointer() {
        this.fIsResolveElement = false;
        this.fIsElementFound = false;
        this.fWasOnlyEmptyElementFound = false;
        this.fIsShortHand = false;
        this.fFoundDepth = 0;
        this.fCurrentChildPosition = 1;
        this.fCurrentChildDepth = 0;
        this.fIsFragmentResolved = false;
    }
    
    public ElementSchemePointer(final SymbolTable symbolTable) {
        this.fIsResolveElement = false;
        this.fIsElementFound = false;
        this.fWasOnlyEmptyElementFound = false;
        this.fIsShortHand = false;
        this.fFoundDepth = 0;
        this.fCurrentChildPosition = 1;
        this.fCurrentChildDepth = 0;
        this.fIsFragmentResolved = false;
        this.fSymbolTable = symbolTable;
    }
    
    public ElementSchemePointer(final SymbolTable symbolTable, final XMLErrorReporter errorReporter) {
        this.fIsResolveElement = false;
        this.fIsElementFound = false;
        this.fWasOnlyEmptyElementFound = false;
        this.fIsShortHand = false;
        this.fFoundDepth = 0;
        this.fCurrentChildPosition = 1;
        this.fCurrentChildDepth = 0;
        this.fIsFragmentResolved = false;
        this.fSymbolTable = symbolTable;
        this.fErrorReporter = errorReporter;
    }
    
    @Override
    public void parseXPointer(final String xpointer) throws XNIException {
        this.init();
        final Tokens tokens = new Tokens(this.fSymbolTable);
        final Scanner scanner = new Scanner(this.fSymbolTable) {
            @Override
            protected void addToken(final Tokens tokens, final int token) throws XNIException {
                if (token == 1 || token == 0) {
                    super.addToken(tokens, token);
                    return;
                }
                ElementSchemePointer.this.reportError("InvalidElementSchemeToken", new Object[] { tokens.getTokenString(token) });
            }
        };
        final int length = xpointer.length();
        final boolean success = scanner.scanExpr(this.fSymbolTable, tokens, xpointer, 0, length);
        if (!success) {
            this.reportError("InvalidElementSchemeXPointer", new Object[] { xpointer });
        }
        final int[] tmpChildSequence = new int[tokens.getTokenCount() / 2 + 1];
        int i = 0;
        while (tokens.hasMore()) {
            int token = tokens.nextToken();
            switch (token) {
                case 0: {
                    token = tokens.nextToken();
                    this.fShortHandPointerName = tokens.getTokenString(token);
                    (this.fShortHandPointer = new ShortHandPointer(this.fSymbolTable)).setSchemeName(this.fShortHandPointerName);
                    continue;
                }
                case 1: {
                    tmpChildSequence[i] = tokens.nextToken();
                    ++i;
                    continue;
                }
                default: {
                    this.reportError("InvalidElementSchemeXPointer", new Object[] { xpointer });
                    continue;
                }
            }
        }
        this.fChildSequence = new int[i];
        this.fCurrentChildSequence = new int[i];
        System.arraycopy(tmpChildSequence, 0, this.fChildSequence, 0, i);
    }
    
    @Override
    public String getSchemeName() {
        return this.fSchemeName;
    }
    
    @Override
    public String getSchemeData() {
        return this.fSchemeData;
    }
    
    @Override
    public void setSchemeName(final String schemeName) {
        this.fSchemeName = schemeName;
    }
    
    @Override
    public void setSchemeData(final String schemeData) {
        this.fSchemeData = schemeData;
    }
    
    @Override
    public boolean resolveXPointer(final QName element, final XMLAttributes attributes, final Augmentations augs, final int event) throws XNIException {
        boolean isShortHandPointerResolved = false;
        if (this.fShortHandPointerName != null) {
            isShortHandPointerResolved = this.fShortHandPointer.resolveXPointer(element, attributes, augs, event);
            if (isShortHandPointerResolved) {
                this.fIsResolveElement = true;
                this.fIsShortHand = true;
            }
            else {
                this.fIsResolveElement = false;
            }
        }
        else {
            this.fIsResolveElement = true;
        }
        if (this.fChildSequence.length > 0) {
            this.fIsFragmentResolved = this.matchChildSequence(element, event);
        }
        else if (isShortHandPointerResolved && this.fChildSequence.length <= 0) {
            this.fIsFragmentResolved = isShortHandPointerResolved;
        }
        else {
            this.fIsFragmentResolved = false;
        }
        return this.fIsFragmentResolved;
    }
    
    protected boolean matchChildSequence(final QName element, final int event) throws XNIException {
        if (this.fCurrentChildDepth >= this.fCurrentChildSequence.length) {
            final int[] tmpCurrentChildSequence = new int[this.fCurrentChildSequence.length];
            System.arraycopy(this.fCurrentChildSequence, 0, tmpCurrentChildSequence, 0, this.fCurrentChildSequence.length);
            System.arraycopy(tmpCurrentChildSequence, 0, this.fCurrentChildSequence = new int[this.fCurrentChildDepth * 2], 0, tmpCurrentChildSequence.length);
        }
        if (this.fIsResolveElement) {
            this.fWasOnlyEmptyElementFound = false;
            if (event == 0) {
                this.fCurrentChildSequence[this.fCurrentChildDepth] = this.fCurrentChildPosition;
                ++this.fCurrentChildDepth;
                this.fCurrentChildPosition = 1;
                if (this.fCurrentChildDepth <= this.fFoundDepth || this.fFoundDepth == 0) {
                    if (this.checkMatch()) {
                        this.fIsElementFound = true;
                        this.fFoundDepth = this.fCurrentChildDepth;
                    }
                    else {
                        this.fIsElementFound = false;
                        this.fFoundDepth = 0;
                    }
                }
            }
            else if (event == 1) {
                if (this.fCurrentChildDepth == this.fFoundDepth) {
                    this.fIsElementFound = true;
                }
                else if ((this.fCurrentChildDepth < this.fFoundDepth && this.fFoundDepth != 0) || (this.fCurrentChildDepth > this.fFoundDepth && this.fFoundDepth == 0)) {
                    this.fIsElementFound = false;
                }
                this.fCurrentChildSequence[this.fCurrentChildDepth] = 0;
                --this.fCurrentChildDepth;
                this.fCurrentChildPosition = this.fCurrentChildSequence[this.fCurrentChildDepth] + 1;
            }
            else if (event == 2) {
                this.fCurrentChildSequence[this.fCurrentChildDepth] = this.fCurrentChildPosition;
                ++this.fCurrentChildPosition;
                if (this.checkMatch()) {
                    this.fIsElementFound = true;
                    this.fWasOnlyEmptyElementFound = true;
                }
                else {
                    this.fIsElementFound = false;
                }
            }
        }
        return this.fIsElementFound;
    }
    
    protected boolean checkMatch() {
        if (!this.fIsShortHand) {
            if (this.fChildSequence.length > this.fCurrentChildDepth + 1) {
                return false;
            }
            for (int i = 0; i < this.fChildSequence.length; ++i) {
                if (this.fChildSequence[i] != this.fCurrentChildSequence[i]) {
                    return false;
                }
            }
        }
        else {
            if (this.fChildSequence.length > this.fCurrentChildDepth + 1) {
                return false;
            }
            for (int i = 0; i < this.fChildSequence.length; ++i) {
                if (this.fCurrentChildSequence.length < i + 2) {
                    return false;
                }
                if (this.fChildSequence[i] != this.fCurrentChildSequence[i + 1]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean isFragmentResolved() throws XNIException {
        return this.fIsFragmentResolved;
    }
    
    @Override
    public boolean isChildFragmentResolved() {
        if (this.fIsShortHand && this.fShortHandPointer != null && this.fChildSequence.length <= 0) {
            return this.fShortHandPointer.isChildFragmentResolved();
        }
        return this.fWasOnlyEmptyElementFound ? (!this.fWasOnlyEmptyElementFound) : (this.fIsFragmentResolved && this.fCurrentChildDepth >= this.fFoundDepth);
    }
    
    protected void reportError(final String key, final Object[] arguments) throws XNIException {
        throw new XNIException(this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/XPTR").formatMessage(this.fErrorReporter.getLocale(), key, arguments));
    }
    
    protected void initErrorReporter() {
        if (this.fErrorReporter == null) {
            this.fErrorReporter = new XMLErrorReporter();
        }
        if (this.fErrorHandler == null) {
            this.fErrorHandler = new XPointerErrorHandler();
        }
        this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/XPTR", new XPointerMessageFormatter());
    }
    
    protected void init() {
        this.fSchemeName = null;
        this.fSchemeData = null;
        this.fShortHandPointerName = null;
        this.fIsResolveElement = false;
        this.fIsElementFound = false;
        this.fWasOnlyEmptyElementFound = false;
        this.fFoundDepth = 0;
        this.fCurrentChildPosition = 1;
        this.fCurrentChildDepth = 0;
        this.fIsFragmentResolved = false;
        this.fShortHandPointer = null;
        this.initErrorReporter();
    }
    
    private final class Tokens
    {
        private static final int XPTRTOKEN_ELEM_NCNAME = 0;
        private static final int XPTRTOKEN_ELEM_CHILD = 1;
        private final String[] fgTokenNames;
        private static final int INITIAL_TOKEN_COUNT = 256;
        private int[] fTokens;
        private int fTokenCount;
        private int fCurrentTokenIndex;
        private SymbolTable fSymbolTable;
        private Hashtable fTokenNames;
        
        private Tokens(final SymbolTable symbolTable) {
            this.fgTokenNames = new String[] { "XPTRTOKEN_ELEM_NCNAME", "XPTRTOKEN_ELEM_CHILD" };
            this.fTokens = new int[256];
            this.fTokenCount = 0;
            this.fTokenNames = new Hashtable();
            this.fSymbolTable = symbolTable;
            this.fTokenNames.put(new Integer(0), "XPTRTOKEN_ELEM_NCNAME");
            this.fTokenNames.put(new Integer(1), "XPTRTOKEN_ELEM_CHILD");
        }
        
        private String getTokenString(final int token) {
            return this.fTokenNames.get(new Integer(token));
        }
        
        private Integer getToken(final int token) {
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
                ElementSchemePointer.this.reportError("XPointerElementSchemeProcessingError", null);
            }
            return this.fTokens[this.fCurrentTokenIndex++];
        }
        
        private int peekToken() throws XNIException {
            if (this.fCurrentTokenIndex == this.fTokenCount) {
                ElementSchemePointer.this.reportError("XPointerElementSchemeProcessingError", null);
            }
            return this.fTokens[this.fCurrentTokenIndex];
        }
        
        private String nextTokenAsString() throws XNIException {
            final String s = this.getTokenString(this.nextToken());
            if (s == null) {
                ElementSchemePointer.this.reportError("XPointerElementSchemeProcessingError", null);
            }
            return s;
        }
        
        private int getTokenCount() {
            return this.fTokenCount;
        }
    }
    
    private class Scanner
    {
        private static final byte CHARTYPE_INVALID = 0;
        private static final byte CHARTYPE_OTHER = 1;
        private static final byte CHARTYPE_MINUS = 2;
        private static final byte CHARTYPE_PERIOD = 3;
        private static final byte CHARTYPE_SLASH = 4;
        private static final byte CHARTYPE_DIGIT = 5;
        private static final byte CHARTYPE_LETTER = 6;
        private static final byte CHARTYPE_UNDERSCORE = 7;
        private static final byte CHARTYPE_NONASCII = 8;
        private final byte[] fASCIICharMap;
        private SymbolTable fSymbolTable;
        
        private Scanner(final SymbolTable symbolTable) {
            this.fASCIICharMap = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1, 1, 1, 1, 1, 1, 1, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 1, 1, 1, 1, 7, 1, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 1, 1, 1, 1, 1 };
            this.fSymbolTable = symbolTable;
        }
        
        private boolean scanExpr(final SymbolTable symbolTable, final Tokens tokens, final String data, int currentOffset, final int endOffset) throws XNIException {
            String nameHandle = null;
            while (currentOffset != endOffset) {
                int ch = data.charAt(currentOffset);
                final byte chartype = (byte)((ch >= 128) ? 8 : this.fASCIICharMap[ch]);
                switch (chartype) {
                    case 4: {
                        if (++currentOffset == endOffset) {
                            return false;
                        }
                        this.addToken(tokens, 1);
                        ch = data.charAt(currentOffset);
                        int child = 0;
                        while (ch >= 48 && ch <= 57) {
                            child = child * 10 + (ch - 48);
                            if (++currentOffset == endOffset) {
                                break;
                            }
                            ch = data.charAt(currentOffset);
                        }
                        if (child == 0) {
                            ElementSchemePointer.this.reportError("InvalidChildSequenceCharacter", new Object[] { new Character((char)ch) });
                            return false;
                        }
                        tokens.addToken(child);
                        continue;
                    }
                    case 1:
                    case 2:
                    case 3:
                    case 5:
                    case 6:
                    case 7:
                    case 8: {
                        final int nameOffset = currentOffset;
                        currentOffset = this.scanNCName(data, endOffset, currentOffset);
                        if (currentOffset == nameOffset) {
                            ElementSchemePointer.this.reportError("InvalidNCNameInElementSchemeData", new Object[] { data });
                            return false;
                        }
                        if (currentOffset < endOffset) {
                            ch = data.charAt(currentOffset);
                        }
                        else {
                            ch = -1;
                        }
                        nameHandle = symbolTable.addSymbol(data.substring(nameOffset, currentOffset));
                        this.addToken(tokens, 0);
                        tokens.addToken(nameHandle);
                        continue;
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
                if (chartype != 6 && chartype != 7) {
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
                    if (chartype != 6 && chartype != 5 && chartype != 3 && chartype != 2 && chartype != 7) {
                        break;
                    }
                    continue;
                }
            }
            return currentOffset;
        }
        
        protected void addToken(final Tokens tokens, final int token) throws XNIException {
            tokens.addToken(token);
        }
    }
}
