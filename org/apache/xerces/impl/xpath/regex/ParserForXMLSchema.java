package org.apache.xerces.impl.xpath.regex;

import java.util.Locale;

class ParserForXMLSchema extends RegexParser
{
    private RangeTokenMap xmlMap;
    private short xmlVersion;
    
    public ParserForXMLSchema() {
        this.xmlMap = null;
        this.xmlVersion = 1;
    }
    
    public ParserForXMLSchema(final Locale locale) {
        super(locale);
        this.xmlMap = null;
        this.xmlVersion = 1;
    }
    
    public ParserForXMLSchema(final Locale locale, final short xmlVersion) {
        super(locale);
        this.xmlMap = null;
        this.xmlVersion = 1;
        this.xmlVersion = xmlVersion;
    }
    
    Token processCaret() throws ParseException {
        this.next();
        return Token.createChar(94);
    }
    
    Token processDollar() throws ParseException {
        this.next();
        return Token.createChar(36);
    }
    
    Token processLookahead() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processNegativelookahead() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processLookbehind() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processNegativelookbehind() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processBacksolidus_A() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processBacksolidus_Z() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processBacksolidus_z() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processBacksolidus_b() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processBacksolidus_B() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processBacksolidus_lt() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processBacksolidus_gt() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processStar(final Token token) throws ParseException {
        this.next();
        return Token.createClosure(token);
    }
    
    Token processPlus(final Token token) throws ParseException {
        this.next();
        return Token.createConcat(token, Token.createClosure(token));
    }
    
    Token processQuestion(final Token token) throws ParseException {
        this.next();
        final Token.UnionToken union = Token.createUnion();
        union.addChild(token);
        union.addChild(Token.createEmpty());
        return union;
    }
    
    boolean checkQuestion(final int n) {
        return false;
    }
    
    Token processParen() throws ParseException {
        this.next();
        final Token.ParenToken paren = Token.createParen(this.parseRegex(), 0);
        if (this.read() != 7) {
            throw this.ex("parser.factor.1", this.offset - 1);
        }
        this.next();
        return paren;
    }
    
    Token processParen2() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processCondition() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processModifiers() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processIndependent() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token processBacksolidus_c() throws ParseException {
        this.next();
        return this.getTokenForShorthand(99);
    }
    
    Token processBacksolidus_C() throws ParseException {
        this.next();
        return this.getTokenForShorthand(67);
    }
    
    Token processBacksolidus_i() throws ParseException {
        this.next();
        return this.getTokenForShorthand(105);
    }
    
    Token processBacksolidus_I() throws ParseException {
        this.next();
        return this.getTokenForShorthand(73);
    }
    
    Token processBacksolidus_g() throws ParseException {
        throw this.ex("parser.process.1", this.offset - 2);
    }
    
    Token processBacksolidus_X() throws ParseException {
        throw this.ex("parser.process.1", this.offset - 2);
    }
    
    Token processBackreference() throws ParseException {
        throw this.ex("parser.process.1", this.offset - 4);
    }
    
    int processCIinCharacterClass(final RangeToken rangeToken, final int n) {
        rangeToken.mergeRanges(this.getTokenForShorthand(n));
        return -1;
    }
    
    protected RangeToken parseCharacterClass(final boolean b) throws ParseException {
        final boolean set = this.isSet(4096);
        this.setContext(1);
        this.next();
        boolean b2 = false;
        RangeToken range = null;
        RangeToken rangeToken;
        if (this.read() == 0 && this.chardata == 94) {
            b2 = true;
            this.next();
            range = Token.createRange();
            range.addRange(0, 1114111);
            rangeToken = Token.createRange();
        }
        else {
            rangeToken = Token.createRange();
        }
        int n = 1;
        int read;
        while ((read = this.read()) != 1) {
            boolean b3 = false;
            if (read == 0 && this.chardata == 93 && n == 0) {
                if (b2) {
                    range.subtractRanges(rangeToken);
                    rangeToken = range;
                    break;
                }
                break;
            }
            else {
                int n2 = this.chardata;
                boolean b4 = false;
                if (read == 10) {
                    switch (n2) {
                        case 68:
                        case 83:
                        case 87:
                        case 100:
                        case 115:
                        case 119: {
                            rangeToken.mergeRanges(this.getTokenForShorthand(n2));
                            b4 = true;
                            break;
                        }
                        case 67:
                        case 73:
                        case 99:
                        case 105: {
                            n2 = this.processCIinCharacterClass(rangeToken, n2);
                            if (n2 < 0) {
                                b4 = true;
                                break;
                            }
                            break;
                        }
                        case 80:
                        case 112: {
                            final int offset = this.offset;
                            RangeToken rangeToken2 = this.processBacksolidus_pP(n2);
                            if (rangeToken2 == null) {
                                if (!this.isSet(2048)) {
                                    throw this.ex("parser.atom.5", offset);
                                }
                                rangeToken2 = Token.token_all;
                            }
                            rangeToken.mergeRanges(rangeToken2);
                            b4 = true;
                            break;
                        }
                        case 45: {
                            n2 = this.decodeEscaped();
                            b3 = true;
                            break;
                        }
                        default: {
                            n2 = this.decodeEscaped();
                            break;
                        }
                    }
                }
                else if (read == 24 && n == 0) {
                    if (b2) {
                        range.subtractRanges(rangeToken);
                        rangeToken = range;
                    }
                    rangeToken.subtractRanges(this.parseCharacterClass(false));
                    if (this.read() != 0 || this.chardata != 93) {
                        throw this.ex("parser.cc.5", this.offset);
                    }
                    break;
                }
                this.next();
                if (!b4) {
                    if (read == 0) {
                        if (n2 == 91) {
                            throw this.ex("parser.cc.6", this.offset - 2);
                        }
                        if (n2 == 93) {
                            throw this.ex("parser.cc.7", this.offset - 2);
                        }
                        if (!set && n2 == 45 && this.chardata != 93 && n == 0) {
                            throw this.ex("parser.cc.8", this.offset - 2);
                        }
                    }
                    if (this.read() != 0 || this.chardata != 45 || (!set && n2 == 45 && !b3 && n != 0)) {
                        if (!this.isSet(2) || n2 > 65535) {
                            rangeToken.addRange(n2, n2);
                        }
                        else {
                            RegexParser.addCaseInsensitiveChar(rangeToken, n2);
                        }
                    }
                    else {
                        this.next();
                        final int read2;
                        if ((read2 = this.read()) == 1) {
                            throw this.ex("parser.cc.2", this.offset);
                        }
                        if ((read2 == 0 && this.chardata == 93) || (set && read2 == 24)) {
                            if (!this.isSet(2) || n2 > 65535) {
                                rangeToken.addRange(n2, n2);
                            }
                            else {
                                RegexParser.addCaseInsensitiveChar(rangeToken, n2);
                            }
                            rangeToken.addRange(45, 45);
                        }
                        else {
                            if (read2 == 24) {
                                throw this.ex("parser.cc.8", this.offset - 1);
                            }
                            if (set && n2 == 45 && !b3) {
                                throw this.ex("parser.cc.4", this.offset - 2);
                            }
                            int n3 = this.chardata;
                            if (read2 == 0) {
                                if (n3 == 91) {
                                    throw this.ex("parser.cc.6", this.offset - 1);
                                }
                                if (n3 == 93) {
                                    throw this.ex("parser.cc.7", this.offset - 1);
                                }
                                if (n3 == 45) {
                                    throw this.ex("parser.cc.8", this.offset - 2);
                                }
                            }
                            else if (read2 == 10) {
                                n3 = this.decodeEscaped();
                            }
                            this.next();
                            if (n2 > n3) {
                                throw this.ex("parser.ope.3", this.offset - 1);
                            }
                            if (!this.isSet(2) || (n2 > 65535 && n3 > 65535)) {
                                rangeToken.addRange(n2, n3);
                            }
                            else {
                                RegexParser.addCaseInsensitiveCharRange(rangeToken, n2, n3);
                            }
                        }
                    }
                }
                n = 0;
            }
        }
        if (this.read() == 1) {
            throw this.ex("parser.cc.2", this.offset);
        }
        rangeToken.sortRanges();
        rangeToken.compactRanges();
        this.setContext(0);
        this.next();
        return rangeToken;
    }
    
    protected RangeToken parseSetOperations() throws ParseException {
        throw this.ex("parser.process.1", this.offset);
    }
    
    Token getTokenForShorthand(final int n) {
        if (this.xmlMap == null) {
            this.xmlMap = RangeTokenMapFactory.getXMLTokenMap(this.xmlVersion);
        }
        switch (n) {
            case 100: {
                return this.xmlMap.get("xml:isDigit", true);
            }
            case 68: {
                return this.xmlMap.get("xml:isDigit", false);
            }
            case 119: {
                return this.xmlMap.get("xml:isWord", true);
            }
            case 87: {
                return this.xmlMap.get("xml:isWord", false);
            }
            case 115: {
                return this.xmlMap.get("xml:isSpace", true);
            }
            case 83: {
                return this.xmlMap.get("xml:isSpace", false);
            }
            case 99: {
                return this.xmlMap.get("xml:isNameChar", true);
            }
            case 67: {
                return this.xmlMap.get("xml:isNameChar", false);
            }
            case 105: {
                return this.xmlMap.get("xml:isInitialNameChar", true);
            }
            case 73: {
                return this.xmlMap.get("xml:isInitialNameChar", false);
            }
            default: {
                throw new RuntimeException("Internal Error: shorthands: \\u" + Integer.toString(n, 16));
            }
        }
    }
    
    int decodeEscaped() throws ParseException {
        if (this.read() != 10) {
            throw this.ex("parser.next.1", this.offset - 1);
        }
        int chardata = this.chardata;
        switch (chardata) {
            case 110: {
                chardata = 10;
                break;
            }
            case 114: {
                chardata = 13;
                break;
            }
            case 116: {
                chardata = 9;
                break;
            }
            case 40:
            case 41:
            case 42:
            case 43:
            case 45:
            case 46:
            case 63:
            case 91:
            case 92:
            case 93:
            case 94:
            case 123:
            case 124:
            case 125: {
                break;
            }
            default: {
                throw this.ex("parser.process.1", this.offset - 2);
            }
        }
        return chardata;
    }
}
