package org.apache.xerces.xpointer;

import java.util.HashMap;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.impl.XMLErrorReporter;

final class ElementSchemePointer implements XPointerPart
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
    
    public ElementSchemePointer(final SymbolTable fSymbolTable) {
        this.fIsResolveElement = false;
        this.fIsElementFound = false;
        this.fWasOnlyEmptyElementFound = false;
        this.fIsShortHand = false;
        this.fFoundDepth = 0;
        this.fCurrentChildPosition = 1;
        this.fCurrentChildDepth = 0;
        this.fIsFragmentResolved = false;
        this.fSymbolTable = fSymbolTable;
    }
    
    public ElementSchemePointer(final SymbolTable fSymbolTable, final XMLErrorReporter fErrorReporter) {
        this.fIsResolveElement = false;
        this.fIsElementFound = false;
        this.fWasOnlyEmptyElementFound = false;
        this.fIsShortHand = false;
        this.fFoundDepth = 0;
        this.fCurrentChildPosition = 1;
        this.fCurrentChildDepth = 0;
        this.fIsFragmentResolved = false;
        this.fSymbolTable = fSymbolTable;
        this.fErrorReporter = fErrorReporter;
    }
    
    public void parseXPointer(final String s) throws XNIException {
        this.init();
        final Tokens tokens = new Tokens(this.fSymbolTable);
        if (!new Scanner(this.fSymbolTable) {
            protected void addToken(final Tokens tokens, final int n) throws XNIException {
                if (n == 1 || n == 0) {
                    super.addToken(tokens, n);
                    return;
                }
                ElementSchemePointer.this.reportError("InvalidElementSchemeToken", new Object[] { tokens.getTokenString(n) });
            }
        }.scanExpr(this.fSymbolTable, tokens, s, 0, s.length())) {
            this.reportError("InvalidElementSchemeXPointer", new Object[] { s });
        }
        final int[] array = new int[tokens.getTokenCount() / 2 + 1];
        int n = 0;
        while (tokens.hasMore()) {
            switch (tokens.nextToken()) {
                case 0: {
                    this.fShortHandPointerName = tokens.getTokenString(tokens.nextToken());
                    (this.fShortHandPointer = new ShortHandPointer(this.fSymbolTable)).setSchemeName(this.fShortHandPointerName);
                    continue;
                }
                case 1: {
                    array[n] = tokens.nextToken();
                    ++n;
                    continue;
                }
                default: {
                    this.reportError("InvalidElementSchemeXPointer", new Object[] { s });
                    continue;
                }
            }
        }
        this.fChildSequence = new int[n];
        this.fCurrentChildSequence = new int[n];
        System.arraycopy(array, 0, this.fChildSequence, 0, n);
    }
    
    public String getSchemeName() {
        return this.fSchemeName;
    }
    
    public String getSchemeData() {
        return this.fSchemeData;
    }
    
    public void setSchemeName(final String fSchemeName) {
        this.fSchemeName = fSchemeName;
    }
    
    public void setSchemeData(final String fSchemeData) {
        this.fSchemeData = fSchemeData;
    }
    
    public boolean resolveXPointer(final QName qName, final XMLAttributes xmlAttributes, final Augmentations augmentations, final int n) throws XNIException {
        boolean resolveXPointer = false;
        if (this.fShortHandPointerName != null) {
            resolveXPointer = this.fShortHandPointer.resolveXPointer(qName, xmlAttributes, augmentations, n);
            if (resolveXPointer) {
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
            this.fIsFragmentResolved = this.matchChildSequence(qName, n);
        }
        else if (resolveXPointer && this.fChildSequence.length <= 0) {
            this.fIsFragmentResolved = resolveXPointer;
        }
        else {
            this.fIsFragmentResolved = false;
        }
        return this.fIsFragmentResolved;
    }
    
    protected boolean matchChildSequence(final QName qName, final int n) throws XNIException {
        if (this.fCurrentChildDepth >= this.fCurrentChildSequence.length) {
            final int[] array = new int[this.fCurrentChildSequence.length];
            System.arraycopy(this.fCurrentChildSequence, 0, array, 0, this.fCurrentChildSequence.length);
            System.arraycopy(array, 0, this.fCurrentChildSequence = new int[this.fCurrentChildDepth * 2], 0, array.length);
        }
        if (this.fIsResolveElement) {
            if (n == 0) {
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
            else if (n == 1) {
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
            else if (n == 2) {
                this.fCurrentChildSequence[this.fCurrentChildDepth] = this.fCurrentChildPosition;
                ++this.fCurrentChildPosition;
                if (this.checkMatch()) {
                    if (!this.fIsElementFound) {
                        this.fWasOnlyEmptyElementFound = true;
                    }
                    else {
                        this.fWasOnlyEmptyElementFound = false;
                    }
                    this.fIsElementFound = true;
                }
                else {
                    this.fIsElementFound = false;
                    this.fWasOnlyEmptyElementFound = false;
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
            for (int j = 0; j < this.fChildSequence.length; ++j) {
                if (this.fCurrentChildSequence.length < j + 2) {
                    return false;
                }
                if (this.fChildSequence[j] != this.fCurrentChildSequence[j + 1]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isFragmentResolved() throws XNIException {
        return this.fIsFragmentResolved;
    }
    
    public boolean isChildFragmentResolved() {
        if (this.fIsShortHand && this.fShortHandPointer != null && this.fChildSequence.length <= 0) {
            return this.fShortHandPointer.isChildFragmentResolved();
        }
        return this.fWasOnlyEmptyElementFound ? (!this.fWasOnlyEmptyElementFound) : (this.fIsFragmentResolved && this.fCurrentChildDepth >= this.fFoundDepth);
    }
    
    protected void reportError(final String s, final Object[] array) throws XNIException {
        throw new XNIException(this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/XPTR").formatMessage(this.fErrorReporter.getLocale(), s, array));
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
        
        private Scanner(final SymbolTable fSymbolTable) {
            this.fASCIICharMap = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1, 1, 1, 1, 1, 1, 1, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 1, 1, 1, 1, 7, 1, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 1, 1, 1, 1, 1 };
            this.fSymbolTable = fSymbolTable;
        }
        
        private boolean scanExpr(final SymbolTable symbolTable, final Tokens tokens, final String s, int i, final int n) throws XNIException {
            while (i != n) {
                final char char1 = s.charAt(i);
                switch ((char1 >= '\u0080') ? 8 : this.fASCIICharMap[char1]) {
                    case 4: {
                        if (++i == n) {
                            return false;
                        }
                        this.addToken(tokens, 1);
                        char c = s.charAt(i);
                        int n2 = 0;
                        while (c >= '0' && c <= '9') {
                            n2 = n2 * 10 + (c - '0');
                            if (++i == n) {
                                break;
                            }
                            c = s.charAt(i);
                        }
                        if (n2 == 0) {
                            ElementSchemePointer.this.reportError("InvalidChildSequenceCharacter", new Object[] { new Character(c) });
                            return false;
                        }
                        tokens.addToken(n2);
                        continue;
                    }
                    case 1:
                    case 2:
                    case 3:
                    case 5:
                    case 6:
                    case 7:
                    case 8: {
                        final int n3 = i;
                        i = this.scanNCName(s, n, i);
                        if (i == n3) {
                            ElementSchemePointer.this.reportError("InvalidNCNameInElementSchemeData", new Object[] { s });
                            return false;
                        }
                        if (i < n) {
                            s.charAt(i);
                        }
                        final String addSymbol = symbolTable.addSymbol(s.substring(n3, i));
                        this.addToken(tokens, 0);
                        tokens.addToken(addSymbol);
                        continue;
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
                if (b != 6 && b != 7) {
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
                    if (b2 != 6 && b2 != 5 && b2 != 3 && b2 != 2 && b2 != 7) {
                        break;
                    }
                    continue;
                }
            }
            return n2;
        }
        
        protected void addToken(final Tokens tokens, final int n) throws XNIException {
            tokens.addToken(n);
        }
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
        private HashMap fTokenNames;
        
        private Tokens(final SymbolTable fSymbolTable) {
            this.fgTokenNames = new String[] { "XPTRTOKEN_ELEM_NCNAME", "XPTRTOKEN_ELEM_CHILD" };
            this.fTokens = new int[256];
            this.fTokenCount = 0;
            this.fTokenNames = new HashMap();
            this.fSymbolTable = fSymbolTable;
            this.fTokenNames.put(new Integer(0), "XPTRTOKEN_ELEM_NCNAME");
            this.fTokenNames.put(new Integer(1), "XPTRTOKEN_ELEM_CHILD");
        }
        
        private String getTokenString(final int n) {
            return this.fTokenNames.get(new Integer(n));
        }
        
        private Integer getToken(final int n) {
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
            final String tokenString = this.getTokenString(this.nextToken());
            if (tokenString == null) {
                ElementSchemePointer.this.reportError("XPointerElementSchemeProcessingError", null);
            }
            return tokenString;
        }
        
        private int getTokenCount() {
            return this.fTokenCount;
        }
    }
}
