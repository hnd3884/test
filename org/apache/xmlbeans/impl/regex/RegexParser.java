package org.apache.xmlbeans.impl.regex;

import java.util.MissingResourceException;
import java.util.Locale;
import java.util.Vector;
import java.util.ResourceBundle;

class RegexParser
{
    static final int T_CHAR = 0;
    static final int T_EOF = 1;
    static final int T_OR = 2;
    static final int T_STAR = 3;
    static final int T_PLUS = 4;
    static final int T_QUESTION = 5;
    static final int T_LPAREN = 6;
    static final int T_RPAREN = 7;
    static final int T_DOT = 8;
    static final int T_LBRACKET = 9;
    static final int T_BACKSOLIDUS = 10;
    static final int T_CARET = 11;
    static final int T_DOLLAR = 12;
    static final int T_LPAREN2 = 13;
    static final int T_LOOKAHEAD = 14;
    static final int T_NEGATIVELOOKAHEAD = 15;
    static final int T_LOOKBEHIND = 16;
    static final int T_NEGATIVELOOKBEHIND = 17;
    static final int T_INDEPENDENT = 18;
    static final int T_SET_OPERATIONS = 19;
    static final int T_POSIX_CHARCLASS_START = 20;
    static final int T_COMMENT = 21;
    static final int T_MODIFIERS = 22;
    static final int T_CONDITION = 23;
    static final int T_XMLSCHEMA_CC_SUBTRACTION = 24;
    int offset;
    String regex;
    int regexlen;
    int options;
    ResourceBundle resources;
    int chardata;
    int nexttoken;
    protected static final int S_NORMAL = 0;
    protected static final int S_INBRACKETS = 1;
    protected static final int S_INXBRACKETS = 2;
    int context;
    int parennumber;
    boolean hasBackReferences;
    Vector references;
    
    public RegexParser() {
        this.context = 0;
        this.parennumber = 1;
        this.references = null;
        this.setLocale(Locale.getDefault());
    }
    
    public RegexParser(final Locale locale) {
        this.context = 0;
        this.parennumber = 1;
        this.references = null;
        this.setLocale(locale);
    }
    
    public void setLocale(final Locale locale) {
        try {
            this.resources = ResourceBundle.getBundle("org.apache.xmlbeans.impl.regex.message", locale);
        }
        catch (final MissingResourceException mre) {
            throw new RuntimeException("Installation Problem???  Couldn't load messages: " + mre.getMessage());
        }
    }
    
    final ParseException ex(final String key, final int loc) {
        return new ParseException(this.resources.getString(key), loc);
    }
    
    private final boolean isSet(final int flag) {
        return (this.options & flag) == flag;
    }
    
    synchronized Token parse(final String regex, final int options) throws ParseException {
        this.options = options;
        this.setContext(this.offset = 0);
        this.parennumber = 1;
        this.hasBackReferences = false;
        this.regex = regex;
        if (this.isSet(16)) {
            this.regex = REUtil.stripExtendedComment(this.regex);
        }
        this.regexlen = this.regex.length();
        this.next();
        final Token ret = this.parseRegex();
        if (this.offset != this.regexlen) {
            throw this.ex("parser.parse.1", this.offset);
        }
        if (this.references != null) {
            for (int i = 0; i < this.references.size(); ++i) {
                final ReferencePosition position = this.references.elementAt(i);
                if (this.parennumber <= position.refNumber) {
                    throw this.ex("parser.parse.2", position.position);
                }
            }
            this.references.removeAllElements();
        }
        return ret;
    }
    
    protected final void setContext(final int con) {
        this.context = con;
    }
    
    final int read() {
        return this.nexttoken;
    }
    
    final void next() {
        if (this.offset >= this.regexlen) {
            this.chardata = -1;
            this.nexttoken = 1;
            return;
        }
        int ch = this.regex.charAt(this.offset++);
        this.chardata = ch;
        int ret = 0;
        if (this.context == 1) {
            Label_0309: {
                switch (ch) {
                    case 92: {
                        ret = 10;
                        if (this.offset >= this.regexlen) {
                            throw this.ex("parser.next.1", this.offset - 1);
                        }
                        this.chardata = this.regex.charAt(this.offset++);
                        break Label_0309;
                    }
                    case 45: {
                        if (this.isSet(512) && this.offset < this.regexlen && this.regex.charAt(this.offset) == '[') {
                            ++this.offset;
                            ret = 24;
                            break Label_0309;
                        }
                        ret = 0;
                        break Label_0309;
                    }
                    case 91: {
                        if (!this.isSet(512) && this.offset < this.regexlen && this.regex.charAt(this.offset) == ':') {
                            ++this.offset;
                            ret = 20;
                            break Label_0309;
                        }
                        break;
                    }
                }
                if (REUtil.isHighSurrogate(ch) && this.offset < this.regexlen) {
                    final int low = this.regex.charAt(this.offset);
                    if (REUtil.isLowSurrogate(low)) {
                        this.chardata = REUtil.composeFromSurrogates(ch, low);
                        ++this.offset;
                    }
                }
                ret = 0;
            }
            this.nexttoken = ret;
            return;
        }
        Label_0919: {
            switch (ch) {
                case 124: {
                    ret = 2;
                    break;
                }
                case 42: {
                    ret = 3;
                    break;
                }
                case 43: {
                    ret = 4;
                    break;
                }
                case 63: {
                    ret = 5;
                    break;
                }
                case 41: {
                    ret = 7;
                    break;
                }
                case 46: {
                    ret = 8;
                    break;
                }
                case 91: {
                    ret = 9;
                    break;
                }
                case 94: {
                    ret = 11;
                    break;
                }
                case 36: {
                    ret = 12;
                    break;
                }
                case 40: {
                    ret = 6;
                    if (this.offset >= this.regexlen) {
                        break;
                    }
                    if (this.regex.charAt(this.offset) != '?') {
                        break;
                    }
                    if (++this.offset >= this.regexlen) {
                        throw this.ex("parser.next.2", this.offset - 1);
                    }
                    ch = this.regex.charAt(this.offset++);
                    switch (ch) {
                        case 58: {
                            ret = 13;
                            break Label_0919;
                        }
                        case 61: {
                            ret = 14;
                            break Label_0919;
                        }
                        case 33: {
                            ret = 15;
                            break Label_0919;
                        }
                        case 91: {
                            ret = 19;
                            break Label_0919;
                        }
                        case 62: {
                            ret = 18;
                            break Label_0919;
                        }
                        case 60: {
                            if (this.offset >= this.regexlen) {
                                throw this.ex("parser.next.2", this.offset - 3);
                            }
                            ch = this.regex.charAt(this.offset++);
                            if (ch == 61) {
                                ret = 16;
                                break Label_0919;
                            }
                            if (ch == 33) {
                                ret = 17;
                                break Label_0919;
                            }
                            throw this.ex("parser.next.3", this.offset - 3);
                        }
                        case 35: {
                            while (this.offset < this.regexlen) {
                                ch = this.regex.charAt(this.offset++);
                                if (ch == 41) {
                                    break;
                                }
                            }
                            if (ch != 41) {
                                throw this.ex("parser.next.4", this.offset - 1);
                            }
                            ret = 21;
                            break Label_0919;
                        }
                        default: {
                            if (ch == 45 || (97 <= ch && ch <= 122) || (65 <= ch && ch <= 90)) {
                                --this.offset;
                                ret = 22;
                                break Label_0919;
                            }
                            if (ch == 40) {
                                ret = 23;
                                break Label_0919;
                            }
                            throw this.ex("parser.next.2", this.offset - 2);
                        }
                    }
                    break;
                }
                case 92: {
                    ret = 10;
                    if (this.offset >= this.regexlen) {
                        throw this.ex("parser.next.1", this.offset - 1);
                    }
                    this.chardata = this.regex.charAt(this.offset++);
                    break;
                }
                default: {
                    ret = 0;
                    break;
                }
            }
        }
        this.nexttoken = ret;
    }
    
    Token parseRegex() throws ParseException {
        Token tok = this.parseTerm();
        Token parent = null;
        while (this.read() == 2) {
            this.next();
            if (parent == null) {
                parent = Token.createUnion();
                parent.addChild(tok);
                tok = parent;
            }
            tok.addChild(this.parseTerm());
        }
        return tok;
    }
    
    Token parseTerm() throws ParseException {
        int ch = this.read();
        if (ch == 2 || ch == 7 || ch == 1) {
            return Token.createEmpty();
        }
        Token tok = this.parseFactor();
        Token concat = null;
        while ((ch = this.read()) != 2 && ch != 7 && ch != 1) {
            if (concat == null) {
                concat = Token.createConcat();
                concat.addChild(tok);
                tok = concat;
            }
            concat.addChild(this.parseFactor());
        }
        return tok;
    }
    
    Token processCaret() throws ParseException {
        this.next();
        return Token.token_linebeginning;
    }
    
    Token processDollar() throws ParseException {
        this.next();
        return Token.token_lineend;
    }
    
    Token processLookahead() throws ParseException {
        this.next();
        final Token tok = Token.createLook(20, this.parseRegex());
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return tok;
    }
    
    Token processNegativelookahead() throws ParseException {
        this.next();
        final Token tok = Token.createLook(21, this.parseRegex());
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return tok;
    }
    
    Token processLookbehind() throws ParseException {
        this.next();
        final Token tok = Token.createLook(22, this.parseRegex());
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return tok;
    }
    
    Token processNegativelookbehind() throws ParseException {
        this.next();
        final Token tok = Token.createLook(23, this.parseRegex());
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return tok;
    }
    
    Token processBacksolidus_A() throws ParseException {
        this.next();
        return Token.token_stringbeginning;
    }
    
    Token processBacksolidus_Z() throws ParseException {
        this.next();
        return Token.token_stringend2;
    }
    
    Token processBacksolidus_z() throws ParseException {
        this.next();
        return Token.token_stringend;
    }
    
    Token processBacksolidus_b() throws ParseException {
        this.next();
        return Token.token_wordedge;
    }
    
    Token processBacksolidus_B() throws ParseException {
        this.next();
        return Token.token_not_wordedge;
    }
    
    Token processBacksolidus_lt() throws ParseException {
        this.next();
        return Token.token_wordbeginning;
    }
    
    Token processBacksolidus_gt() throws ParseException {
        this.next();
        return Token.token_wordend;
    }
    
    Token processStar(final Token tok) throws ParseException {
        this.next();
        if (this.read() == 5) {
            this.next();
            return Token.createNGClosure(tok);
        }
        return Token.createClosure(tok);
    }
    
    Token processPlus(final Token tok) throws ParseException {
        this.next();
        if (this.read() == 5) {
            this.next();
            return Token.createConcat(tok, Token.createNGClosure(tok));
        }
        return Token.createConcat(tok, Token.createClosure(tok));
    }
    
    Token processQuestion(final Token tok) throws ParseException {
        this.next();
        final Token par = Token.createUnion();
        if (this.read() == 5) {
            this.next();
            par.addChild(Token.createEmpty());
            par.addChild(tok);
        }
        else {
            par.addChild(tok);
            par.addChild(Token.createEmpty());
        }
        return par;
    }
    
    boolean checkQuestion(final int off) {
        return off < this.regexlen && this.regex.charAt(off) == '?';
    }
    
    Token processParen() throws ParseException {
        this.next();
        final int p = this.parennumber++;
        final Token tok = Token.createParen(this.parseRegex(), p);
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return tok;
    }
    
    Token processParen2() throws ParseException {
        this.next();
        final Token tok = Token.createParen(this.parseRegex(), 0);
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return tok;
    }
    
    Token processCondition() throws ParseException {
        if (this.offset + 1 >= this.regexlen) {
            throw this.ex("parser.factor.4", this.offset);
        }
        int refno = -1;
        Token condition = null;
        final int ch = this.regex.charAt(this.offset);
        if (49 <= ch && ch <= 57) {
            refno = ch - 48;
            this.hasBackReferences = true;
            if (this.references == null) {
                this.references = new Vector();
            }
            this.references.addElement(new ReferencePosition(refno, this.offset));
            ++this.offset;
            if (this.regex.charAt(this.offset) != ')') {
                throw this.ex("parser.factor.1", this.offset);
            }
            ++this.offset;
        }
        else {
            if (ch == 63) {
                --this.offset;
            }
            this.next();
            condition = this.parseFactor();
            switch (condition.type) {
                case 20:
                case 21:
                case 22:
                case 23: {
                    break;
                }
                case 8: {
                    if (this.read() != 7) {
                        throw this.ex("parser.factor.1", this.offset - 1);
                    }
                    break;
                }
                default: {
                    throw this.ex("parser.factor.5", this.offset);
                }
            }
        }
        this.next();
        Token yesPattern = this.parseRegex();
        Token noPattern = null;
        if (yesPattern.type == 2) {
            if (yesPattern.size() != 2) {
                throw this.ex("parser.factor.6", this.offset);
            }
            noPattern = yesPattern.getChild(1);
            yesPattern = yesPattern.getChild(0);
        }
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return Token.createCondition(refno, condition, yesPattern, noPattern);
    }
    
    Token processModifiers() throws ParseException {
        int add = 0;
        int mask = 0;
        int ch = -1;
        while (this.offset < this.regexlen) {
            ch = this.regex.charAt(this.offset);
            final int v = REUtil.getOptionValue(ch);
            if (v == 0) {
                break;
            }
            add |= v;
            ++this.offset;
        }
        if (this.offset >= this.regexlen) {
            throw this.ex("parser.factor.2", this.offset - 1);
        }
        if (ch == 45) {
            ++this.offset;
            while (this.offset < this.regexlen) {
                ch = this.regex.charAt(this.offset);
                final int v = REUtil.getOptionValue(ch);
                if (v == 0) {
                    break;
                }
                mask |= v;
                ++this.offset;
            }
            if (this.offset >= this.regexlen) {
                throw this.ex("parser.factor.2", this.offset - 1);
            }
        }
        Token tok;
        if (ch == 58) {
            ++this.offset;
            this.next();
            tok = Token.createModifierGroup(this.parseRegex(), add, mask);
            if (this.read() != 7) {
                throw this.ex("parser.factor.1", this.offset - 1);
            }
            this.next();
        }
        else {
            if (ch != 41) {
                throw this.ex("parser.factor.3", this.offset);
            }
            ++this.offset;
            this.next();
            tok = Token.createModifierGroup(this.parseRegex(), add, mask);
        }
        return tok;
    }
    
    Token processIndependent() throws ParseException {
        this.next();
        final Token tok = Token.createLook(24, this.parseRegex());
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return tok;
    }
    
    Token processBacksolidus_c() throws ParseException {
        final int ch2;
        if (this.offset >= this.regexlen || ((ch2 = this.regex.charAt(this.offset++)) & 0xFFE0) != 0x40) {
            throw this.ex("parser.atom.1", this.offset - 1);
        }
        this.next();
        return Token.createChar(ch2 - 64);
    }
    
    Token processBacksolidus_C() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processBacksolidus_i() throws ParseException {
        final Token tok = Token.createChar(105);
        this.next();
        return tok;
    }
    
    Token processBacksolidus_I() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processBacksolidus_g() throws ParseException {
        this.next();
        return Token.getGraphemePattern();
    }
    
    Token processBacksolidus_X() throws ParseException {
        this.next();
        return Token.getCombiningCharacterSequence();
    }
    
    Token processBackreference() throws ParseException {
        final int refnum = this.chardata - 48;
        final Token tok = Token.createBackReference(refnum);
        this.hasBackReferences = true;
        if (this.references == null) {
            this.references = new Vector();
        }
        this.references.addElement(new ReferencePosition(refnum, this.offset - 2));
        this.next();
        return tok;
    }
    
    Token parseFactor() throws ParseException {
        int ch = this.read();
        Label_0211: {
            switch (ch) {
                case 11: {
                    return this.processCaret();
                }
                case 12: {
                    return this.processDollar();
                }
                case 14: {
                    return this.processLookahead();
                }
                case 15: {
                    return this.processNegativelookahead();
                }
                case 16: {
                    return this.processLookbehind();
                }
                case 17: {
                    return this.processNegativelookbehind();
                }
                case 21: {
                    this.next();
                    return Token.createEmpty();
                }
                case 10: {
                    switch (this.chardata) {
                        case 65: {
                            return this.processBacksolidus_A();
                        }
                        case 90: {
                            return this.processBacksolidus_Z();
                        }
                        case 122: {
                            return this.processBacksolidus_z();
                        }
                        case 98: {
                            return this.processBacksolidus_b();
                        }
                        case 66: {
                            return this.processBacksolidus_B();
                        }
                        case 60: {
                            return this.processBacksolidus_lt();
                        }
                        case 62: {
                            return this.processBacksolidus_gt();
                        }
                        default: {
                            break Label_0211;
                        }
                    }
                    break;
                }
            }
        }
        Token tok = this.parseAtom();
        ch = this.read();
        switch (ch) {
            case 3: {
                return this.processStar(tok);
            }
            case 4: {
                return this.processPlus(tok);
            }
            case 5: {
                return this.processQuestion(tok);
            }
            case 0: {
                if (this.chardata != 123 || this.offset >= this.regexlen) {
                    break;
                }
                int off = this.offset;
                int min = 0;
                int max = -1;
                if ((ch = this.regex.charAt(off++)) < 48 || ch > 57) {
                    throw this.ex("parser.quantifier.1", this.offset);
                }
                min = ch - 48;
                while (off < this.regexlen && (ch = this.regex.charAt(off++)) >= 48 && ch <= 57) {
                    min = min * 10 + ch - 48;
                    if (min < 0) {
                        throw this.ex("parser.quantifier.5", this.offset);
                    }
                }
                max = min;
                if (ch == 44) {
                    if (off >= this.regexlen) {
                        throw this.ex("parser.quantifier.3", this.offset);
                    }
                    if ((ch = this.regex.charAt(off++)) >= 48 && ch <= 57) {
                        max = ch - 48;
                        while (off < this.regexlen && (ch = this.regex.charAt(off++)) >= 48 && ch <= 57) {
                            max = max * 10 + ch - 48;
                            if (max < 0) {
                                throw this.ex("parser.quantifier.5", this.offset);
                            }
                        }
                        if (min > max) {
                            throw this.ex("parser.quantifier.4", this.offset);
                        }
                    }
                    else {
                        max = -1;
                    }
                }
                if (ch != 125) {
                    throw this.ex("parser.quantifier.2", this.offset);
                }
                if (this.checkQuestion(off)) {
                    tok = Token.createNGClosure(tok);
                    this.offset = off + 1;
                }
                else {
                    tok = Token.createClosure(tok);
                    this.offset = off;
                }
                tok.setMin(min);
                tok.setMax(max);
                this.next();
                break;
            }
        }
        return tok;
    }
    
    Token parseAtom() throws ParseException {
        final int ch = this.read();
        Token tok = null;
        switch (ch) {
            case 6: {
                return this.processParen();
            }
            case 13: {
                return this.processParen2();
            }
            case 23: {
                return this.processCondition();
            }
            case 22: {
                return this.processModifiers();
            }
            case 18: {
                return this.processIndependent();
            }
            case 8: {
                this.next();
                tok = Token.token_dot;
                break;
            }
            case 9: {
                return this.parseCharacterClass(true);
            }
            case 19: {
                return this.parseSetOperations();
            }
            case 10: {
                switch (this.chardata) {
                    case 68:
                    case 83:
                    case 87:
                    case 100:
                    case 115:
                    case 119: {
                        tok = this.getTokenForShorthand(this.chardata);
                        this.next();
                        return tok;
                    }
                    case 101:
                    case 102:
                    case 110:
                    case 114:
                    case 116:
                    case 117:
                    case 118:
                    case 120: {
                        final int ch2 = this.decodeEscaped();
                        if (ch2 < 65536) {
                            tok = Token.createChar(ch2);
                        }
                        else {
                            tok = Token.createString(REUtil.decomposeToSurrogates(ch2));
                        }
                        break;
                    }
                    case 99: {
                        return this.processBacksolidus_c();
                    }
                    case 67: {
                        return this.processBacksolidus_C();
                    }
                    case 105: {
                        return this.processBacksolidus_i();
                    }
                    case 73: {
                        return this.processBacksolidus_I();
                    }
                    case 103: {
                        return this.processBacksolidus_g();
                    }
                    case 88: {
                        return this.processBacksolidus_X();
                    }
                    case 49:
                    case 50:
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                    case 56:
                    case 57: {
                        return this.processBackreference();
                    }
                    case 80:
                    case 112: {
                        final int pstart = this.offset;
                        tok = this.processBacksolidus_pP(this.chardata);
                        if (tok == null) {
                            throw this.ex("parser.atom.5", pstart);
                        }
                        break;
                    }
                    default: {
                        tok = Token.createChar(this.chardata);
                        break;
                    }
                }
                this.next();
                break;
            }
            case 0: {
                if (this.chardata == 93 || this.chardata == 123 || this.chardata == 125) {
                    throw this.ex("parser.atom.4", this.offset - 1);
                }
                tok = Token.createChar(this.chardata);
                final int high = this.chardata;
                this.next();
                if (REUtil.isHighSurrogate(high) && this.read() == 0 && REUtil.isLowSurrogate(this.chardata)) {
                    final char[] sur = { (char)high, (char)this.chardata };
                    tok = Token.createParen(Token.createString(new String(sur)), 0);
                    this.next();
                    break;
                }
                break;
            }
            default: {
                throw this.ex("parser.atom.4", this.offset - 1);
            }
        }
        return tok;
    }
    
    protected RangeToken processBacksolidus_pP(final int c) throws ParseException {
        this.next();
        if (this.read() != 0 || this.chardata != 123) {
            throw this.ex("parser.atom.2", this.offset - 1);
        }
        final boolean positive = c == 112;
        final int namestart = this.offset;
        final int nameend = this.regex.indexOf(125, namestart);
        if (nameend < 0) {
            throw this.ex("parser.atom.3", this.offset);
        }
        final String pname = this.regex.substring(namestart, nameend);
        this.offset = nameend + 1;
        return Token.getRange(pname, positive, this.isSet(512));
    }
    
    int processCIinCharacterClass(final RangeToken tok, final int c) {
        return this.decodeEscaped();
    }
    
    protected RangeToken parseCharacterClass(final boolean useNrange) throws ParseException {
        this.setContext(1);
        this.next();
        boolean nrange = false;
        RangeToken base = null;
        RangeToken tok;
        if (this.read() == 0 && this.chardata == 94) {
            nrange = true;
            this.next();
            if (useNrange) {
                tok = Token.createNRange();
            }
            else {
                base = Token.createRange();
                base.addRange(0, 1114111);
                tok = Token.createRange();
            }
        }
        else {
            tok = Token.createRange();
        }
        boolean firstloop = true;
        int type;
        while ((type = this.read()) != 1 && (type != 0 || this.chardata != 93 || firstloop)) {
            firstloop = false;
            int c = this.chardata;
            boolean end = false;
            if (type == 10) {
                switch (c) {
                    case 68:
                    case 83:
                    case 87:
                    case 100:
                    case 115:
                    case 119: {
                        tok.mergeRanges(this.getTokenForShorthand(c));
                        end = true;
                        break;
                    }
                    case 67:
                    case 73:
                    case 99:
                    case 105: {
                        c = this.processCIinCharacterClass(tok, c);
                        if (c < 0) {
                            end = true;
                            break;
                        }
                        break;
                    }
                    case 80:
                    case 112: {
                        final int pstart = this.offset;
                        final RangeToken tok2 = this.processBacksolidus_pP(c);
                        if (tok2 == null) {
                            throw this.ex("parser.atom.5", pstart);
                        }
                        tok.mergeRanges(tok2);
                        end = true;
                        break;
                    }
                    default: {
                        c = this.decodeEscaped();
                        break;
                    }
                }
            }
            else if (type == 20) {
                final int nameend = this.regex.indexOf(58, this.offset);
                if (nameend < 0) {
                    throw this.ex("parser.cc.1", this.offset);
                }
                boolean positive = true;
                if (this.regex.charAt(this.offset) == '^') {
                    ++this.offset;
                    positive = false;
                }
                final String name = this.regex.substring(this.offset, nameend);
                final RangeToken range = Token.getRange(name, positive, this.isSet(512));
                if (range == null) {
                    throw this.ex("parser.cc.3", this.offset);
                }
                tok.mergeRanges(range);
                end = true;
                if (nameend + 1 >= this.regexlen || this.regex.charAt(nameend + 1) != ']') {
                    throw this.ex("parser.cc.1", nameend);
                }
                this.offset = nameend + 2;
            }
            this.next();
            if (!end) {
                if (this.read() != 0 || this.chardata != 45) {
                    tok.addRange(c, c);
                }
                else {
                    this.next();
                    if ((type = this.read()) == 1) {
                        throw this.ex("parser.cc.2", this.offset);
                    }
                    if (type == 0 && this.chardata == 93) {
                        tok.addRange(c, c);
                        tok.addRange(45, 45);
                    }
                    else {
                        int rangeend = this.chardata;
                        if (type == 10) {
                            rangeend = this.decodeEscaped();
                        }
                        this.next();
                        tok.addRange(c, rangeend);
                    }
                }
            }
            if (this.isSet(1024) && this.read() == 0 && this.chardata == 44) {
                this.next();
            }
        }
        if (this.read() == 1) {
            throw this.ex("parser.cc.2", this.offset);
        }
        if (!useNrange && nrange) {
            base.subtractRanges(tok);
            tok = base;
        }
        tok.sortRanges();
        tok.compactRanges();
        this.setContext(0);
        this.next();
        return tok;
    }
    
    protected RangeToken parseSetOperations() throws ParseException {
        final RangeToken tok = this.parseCharacterClass(false);
        int type;
        while ((type = this.read()) != 7) {
            final int ch = this.chardata;
            if ((type != 0 || (ch != 45 && ch != 38)) && type != 4) {
                throw this.ex("parser.ope.2", this.offset - 1);
            }
            this.next();
            if (this.read() != 9) {
                throw this.ex("parser.ope.1", this.offset - 1);
            }
            final RangeToken t2 = this.parseCharacterClass(false);
            if (type == 4) {
                tok.mergeRanges(t2);
            }
            else if (ch == 45) {
                tok.subtractRanges(t2);
            }
            else {
                if (ch != 38) {
                    throw new RuntimeException("ASSERT");
                }
                tok.intersectRanges(t2);
            }
        }
        this.next();
        return tok;
    }
    
    Token getTokenForShorthand(final int ch) {
        Token tok = null;
        switch (ch) {
            case 100: {
                tok = (this.isSet(32) ? Token.getRange("Nd", true) : Token.token_0to9);
                break;
            }
            case 68: {
                tok = (this.isSet(32) ? Token.getRange("Nd", false) : Token.token_not_0to9);
                break;
            }
            case 119: {
                tok = (this.isSet(32) ? Token.getRange("IsWord", true) : Token.token_wordchars);
                break;
            }
            case 87: {
                tok = (this.isSet(32) ? Token.getRange("IsWord", false) : Token.token_not_wordchars);
                break;
            }
            case 115: {
                tok = (this.isSet(32) ? Token.getRange("IsSpace", true) : Token.token_spaces);
                break;
            }
            case 83: {
                tok = (this.isSet(32) ? Token.getRange("IsSpace", false) : Token.token_not_spaces);
                break;
            }
            default: {
                throw new RuntimeException("Internal Error: shorthands: \\u" + Integer.toString(ch, 16));
            }
        }
        return tok;
    }
    
    int decodeEscaped() throws ParseException {
        if (this.read() != 10) {
            throw this.ex("parser.next.1", this.offset - 1);
        }
        int c = this.chardata;
        Label_0859: {
            switch (c) {
                case 101: {
                    c = 27;
                    break;
                }
                case 102: {
                    c = 12;
                    break;
                }
                case 110: {
                    c = 10;
                    break;
                }
                case 114: {
                    c = 13;
                    break;
                }
                case 116: {
                    c = 9;
                    break;
                }
                case 120: {
                    this.next();
                    if (this.read() != 0) {
                        throw this.ex("parser.descape.1", this.offset - 1);
                    }
                    if (this.chardata == 123) {
                        int v1 = 0;
                        int uv = 0;
                        while (true) {
                            this.next();
                            if (this.read() != 0) {
                                throw this.ex("parser.descape.1", this.offset - 1);
                            }
                            if ((v1 = hexChar(this.chardata)) < 0) {
                                if (this.chardata != 125) {
                                    throw this.ex("parser.descape.3", this.offset - 1);
                                }
                                if (uv > 1114111) {
                                    throw this.ex("parser.descape.4", this.offset - 1);
                                }
                                c = uv;
                                break Label_0859;
                            }
                            else {
                                if (uv > uv * 16) {
                                    throw this.ex("parser.descape.2", this.offset - 1);
                                }
                                uv = uv * 16 + v1;
                            }
                        }
                    }
                    else {
                        int v1 = 0;
                        if (this.read() != 0 || (v1 = hexChar(this.chardata)) < 0) {
                            throw this.ex("parser.descape.1", this.offset - 1);
                        }
                        int uv = v1;
                        this.next();
                        if (this.read() != 0 || (v1 = hexChar(this.chardata)) < 0) {
                            throw this.ex("parser.descape.1", this.offset - 1);
                        }
                        uv = (c = uv * 16 + v1);
                        break;
                    }
                    break;
                }
                case 117: {
                    int v1 = 0;
                    this.next();
                    if (this.read() != 0 || (v1 = hexChar(this.chardata)) < 0) {
                        throw this.ex("parser.descape.1", this.offset - 1);
                    }
                    int uv = v1;
                    this.next();
                    if (this.read() != 0 || (v1 = hexChar(this.chardata)) < 0) {
                        throw this.ex("parser.descape.1", this.offset - 1);
                    }
                    uv = uv * 16 + v1;
                    this.next();
                    if (this.read() != 0 || (v1 = hexChar(this.chardata)) < 0) {
                        throw this.ex("parser.descape.1", this.offset - 1);
                    }
                    uv = uv * 16 + v1;
                    this.next();
                    if (this.read() != 0 || (v1 = hexChar(this.chardata)) < 0) {
                        throw this.ex("parser.descape.1", this.offset - 1);
                    }
                    uv = (c = uv * 16 + v1);
                    break;
                }
                case 118: {
                    this.next();
                    int v1;
                    if (this.read() != 0 || (v1 = hexChar(this.chardata)) < 0) {
                        throw this.ex("parser.descape.1", this.offset - 1);
                    }
                    int uv = v1;
                    this.next();
                    if (this.read() != 0 || (v1 = hexChar(this.chardata)) < 0) {
                        throw this.ex("parser.descape.1", this.offset - 1);
                    }
                    uv = uv * 16 + v1;
                    this.next();
                    if (this.read() != 0 || (v1 = hexChar(this.chardata)) < 0) {
                        throw this.ex("parser.descape.1", this.offset - 1);
                    }
                    uv = uv * 16 + v1;
                    this.next();
                    if (this.read() != 0 || (v1 = hexChar(this.chardata)) < 0) {
                        throw this.ex("parser.descape.1", this.offset - 1);
                    }
                    uv = uv * 16 + v1;
                    this.next();
                    if (this.read() != 0 || (v1 = hexChar(this.chardata)) < 0) {
                        throw this.ex("parser.descape.1", this.offset - 1);
                    }
                    uv = uv * 16 + v1;
                    this.next();
                    if (this.read() != 0 || (v1 = hexChar(this.chardata)) < 0) {
                        throw this.ex("parser.descape.1", this.offset - 1);
                    }
                    uv = uv * 16 + v1;
                    if (uv > 1114111) {
                        throw this.ex("parser.descappe.4", this.offset - 1);
                    }
                    c = uv;
                    break;
                }
                case 65:
                case 90:
                case 122: {
                    throw this.ex("parser.descape.5", this.offset - 2);
                }
            }
        }
        return c;
    }
    
    private static final int hexChar(final int ch) {
        if (ch < 48) {
            return -1;
        }
        if (ch > 102) {
            return -1;
        }
        if (ch <= 57) {
            return ch - 48;
        }
        if (ch < 65) {
            return -1;
        }
        if (ch <= 70) {
            return ch - 65 + 10;
        }
        if (ch < 97) {
            return -1;
        }
        return ch - 97 + 10;
    }
    
    static class ReferencePosition
    {
        int refNumber;
        int position;
        
        ReferencePosition(final int n, final int pos) {
            this.refNumber = n;
            this.position = pos;
        }
    }
}
