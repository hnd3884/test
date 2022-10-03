package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.io.ObjectStreamField;
import java.util.List;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.io.Serializable;

class Token implements Serializable
{
    private static final long serialVersionUID = 8484976002585487481L;
    static final boolean COUNTTOKENS = true;
    static int tokens;
    static final int CHAR = 0;
    static final int DOT = 11;
    static final int CONCAT = 1;
    static final int UNION = 2;
    static final int CLOSURE = 3;
    static final int RANGE = 4;
    static final int NRANGE = 5;
    static final int PAREN = 6;
    static final int EMPTY = 7;
    static final int ANCHOR = 8;
    static final int NONGREEDYCLOSURE = 9;
    static final int STRING = 10;
    static final int BACKREFERENCE = 12;
    static final int LOOKAHEAD = 20;
    static final int NEGATIVELOOKAHEAD = 21;
    static final int LOOKBEHIND = 22;
    static final int NEGATIVELOOKBEHIND = 23;
    static final int INDEPENDENT = 24;
    static final int MODIFIERGROUP = 25;
    static final int CONDITION = 26;
    static final int UTF16_MAX = 1114111;
    final int type;
    static Token token_dot;
    static Token token_0to9;
    static Token token_wordchars;
    static Token token_not_0to9;
    static Token token_not_wordchars;
    static Token token_spaces;
    static Token token_not_spaces;
    static Token token_empty;
    static Token token_linebeginning;
    static Token token_linebeginning2;
    static Token token_lineend;
    static Token token_stringbeginning;
    static Token token_stringend;
    static Token token_stringend2;
    static Token token_wordedge;
    static Token token_not_wordedge;
    static Token token_wordbeginning;
    static Token token_wordend;
    static final int FC_CONTINUE = 0;
    static final int FC_TERMINAL = 1;
    static final int FC_ANY = 2;
    private static final Map<String, Token> categories;
    private static final Map<String, Token> categories2;
    private static final String[] categoryNames;
    static final int CHAR_INIT_QUOTE = 29;
    static final int CHAR_FINAL_QUOTE = 30;
    static final int CHAR_LETTER = 31;
    static final int CHAR_MARK = 32;
    static final int CHAR_NUMBER = 33;
    static final int CHAR_SEPARATOR = 34;
    static final int CHAR_OTHER = 35;
    static final int CHAR_PUNCTUATION = 36;
    static final int CHAR_SYMBOL = 37;
    private static final String[] blockNames;
    static final String blockRanges = "\u0000\u007f\u0080\u00ff\u0100\u017f\u0180\u024f\u0250\u02af\u02b0\u02ff\u0300\u036f\u0370\u03ff\u0400\u04ff\u0530\u058f\u0590\u05ff\u0600\u06ff\u0700\u074f\u0780\u07bf\u0900\u097f\u0980\u09ff\u0a00\u0a7f\u0a80\u0aff\u0b00\u0b7f\u0b80\u0bff\u0c00\u0c7f\u0c80\u0cff\u0d00\u0d7f\u0d80\u0dff\u0e00\u0e7f\u0e80\u0eff\u0f00\u0fff\u1000\u109f\u10a0\u10ff\u1100\u11ff\u1200\u137f\u13a0\u13ff\u1400\u167f\u1680\u169f\u16a0\u16ff\u1780\u17ff\u1800\u18af\u1e00\u1eff\u1f00\u1fff\u2000\u206f\u2070\u209f\u20a0\u20cf\u20d0\u20ff\u2100\u214f\u2150\u218f\u2190\u21ff\u2200\u22ff\u2300\u23ff\u2400\u243f\u2440\u245f\u2460\u24ff\u2500\u257f\u2580\u259f\u25a0\u25ff\u2600\u26ff\u2700\u27bf\u2800\u28ff\u2e80\u2eff\u2f00\u2fdf\u2ff0\u2fff\u3000\u303f\u3040\u309f\u30a0\u30ff\u3100\u312f\u3130\u318f\u3190\u319f\u31a0\u31bf\u3200\u32ff\u3300\u33ff\u3400\u4db5\u4e00\u9fff\ua000\ua48f\ua490\ua4cf\uac00\ud7a3\ue000\uf8ff\uf900\ufaff\ufb00\ufb4f\ufb50\ufdff\ufe20\ufe2f\ufe30\ufe4f\ufe50\ufe6f\ufe70\ufefe\ufeff\ufeff\uff00\uffef";
    static final int[] nonBMPBlockRanges;
    private static final int NONBMP_BLOCK_START = 84;
    static final Set<String> nonxs;
    static final String viramaString = "\u094d\u09cd\u0a4d\u0acd\u0b4d\u0bcd\u0c4d\u0ccd\u0d4d\u0e3a\u0f84";
    private static Token token_grapheme;
    private static Token token_ccs;
    
    static ParenToken createLook(final int type, final Token child) {
        ++Token.tokens;
        return new ParenToken(type, child, 0);
    }
    
    static ParenToken createParen(final Token child, final int pnumber) {
        ++Token.tokens;
        return new ParenToken(6, child, pnumber);
    }
    
    static ClosureToken createClosure(final Token tok) {
        ++Token.tokens;
        return new ClosureToken(3, tok);
    }
    
    static ClosureToken createNGClosure(final Token tok) {
        ++Token.tokens;
        return new ClosureToken(9, tok);
    }
    
    static ConcatToken createConcat(final Token tok1, final Token tok2) {
        ++Token.tokens;
        return new ConcatToken(tok1, tok2);
    }
    
    static UnionToken createConcat() {
        ++Token.tokens;
        return new UnionToken(1);
    }
    
    static UnionToken createUnion() {
        ++Token.tokens;
        return new UnionToken(2);
    }
    
    static Token createEmpty() {
        return Token.token_empty;
    }
    
    static RangeToken createRange() {
        ++Token.tokens;
        return new RangeToken(4);
    }
    
    static RangeToken createNRange() {
        ++Token.tokens;
        return new RangeToken(5);
    }
    
    static CharToken createChar(final int ch) {
        ++Token.tokens;
        return new CharToken(0, ch);
    }
    
    private static CharToken createAnchor(final int ch) {
        ++Token.tokens;
        return new CharToken(8, ch);
    }
    
    static StringToken createBackReference(final int refno) {
        ++Token.tokens;
        return new StringToken(12, null, refno);
    }
    
    static StringToken createString(final String str) {
        ++Token.tokens;
        return new StringToken(10, str, 0);
    }
    
    static ModifierToken createModifierGroup(final Token child, final int add, final int mask) {
        ++Token.tokens;
        return new ModifierToken(child, add, mask);
    }
    
    static ConditionToken createCondition(final int refno, final Token condition, final Token yespat, final Token nopat) {
        ++Token.tokens;
        return new ConditionToken(refno, condition, yespat, nopat);
    }
    
    protected Token(final int type) {
        this.type = type;
    }
    
    int size() {
        return 0;
    }
    
    Token getChild(final int index) {
        return null;
    }
    
    void addChild(final Token tok) {
        throw new RuntimeException("Not supported.");
    }
    
    protected void addRange(final int start, final int end) {
        throw new RuntimeException("Not supported.");
    }
    
    protected void sortRanges() {
        throw new RuntimeException("Not supported.");
    }
    
    protected void compactRanges() {
        throw new RuntimeException("Not supported.");
    }
    
    protected void mergeRanges(final Token tok) {
        throw new RuntimeException("Not supported.");
    }
    
    protected void subtractRanges(final Token tok) {
        throw new RuntimeException("Not supported.");
    }
    
    protected void intersectRanges(final Token tok) {
        throw new RuntimeException("Not supported.");
    }
    
    static Token complementRanges(final Token tok) {
        return RangeToken.complementRanges(tok);
    }
    
    void setMin(final int min) {
    }
    
    void setMax(final int max) {
    }
    
    int getMin() {
        return -1;
    }
    
    int getMax() {
        return -1;
    }
    
    int getReferenceNumber() {
        return 0;
    }
    
    String getString() {
        return null;
    }
    
    int getParenNumber() {
        return 0;
    }
    
    int getChar() {
        return -1;
    }
    
    @Override
    public String toString() {
        return this.toString(0);
    }
    
    public String toString(final int options) {
        return (this.type == 11) ? "." : "";
    }
    
    final int getMinLength() {
        switch (this.type) {
            case 1: {
                int sum = 0;
                for (int i = 0; i < this.size(); ++i) {
                    sum += this.getChild(i).getMinLength();
                }
                return sum;
            }
            case 2:
            case 26: {
                if (this.size() == 0) {
                    return 0;
                }
                int ret = this.getChild(0).getMinLength();
                for (int j = 1; j < this.size(); ++j) {
                    final int min = this.getChild(j).getMinLength();
                    if (min < ret) {
                        ret = min;
                    }
                }
                return ret;
            }
            case 3:
            case 9: {
                if (this.getMin() >= 0) {
                    return this.getMin() * this.getChild(0).getMinLength();
                }
                return 0;
            }
            case 7:
            case 8: {
                return 0;
            }
            case 0:
            case 4:
            case 5:
            case 11: {
                return 1;
            }
            case 6:
            case 24:
            case 25: {
                return this.getChild(0).getMinLength();
            }
            case 12: {
                return 0;
            }
            case 10: {
                return this.getString().length();
            }
            case 20:
            case 21:
            case 22:
            case 23: {
                return 0;
            }
            default: {
                throw new RuntimeException("Token#getMinLength(): Invalid Type: " + this.type);
            }
        }
    }
    
    final int getMaxLength() {
        switch (this.type) {
            case 1: {
                int sum = 0;
                for (int i = 0; i < this.size(); ++i) {
                    final int d = this.getChild(i).getMaxLength();
                    if (d < 0) {
                        return -1;
                    }
                    sum += d;
                }
                return sum;
            }
            case 2:
            case 26: {
                if (this.size() == 0) {
                    return 0;
                }
                int ret = this.getChild(0).getMaxLength();
                for (int j = 1; ret >= 0 && j < this.size(); ++j) {
                    final int max = this.getChild(j).getMaxLength();
                    if (max < 0) {
                        ret = -1;
                        break;
                    }
                    if (max > ret) {
                        ret = max;
                    }
                }
                return ret;
            }
            case 3:
            case 9: {
                if (this.getMax() >= 0) {
                    return this.getMax() * this.getChild(0).getMaxLength();
                }
                return -1;
            }
            case 7:
            case 8: {
                return 0;
            }
            case 0: {
                return 1;
            }
            case 4:
            case 5:
            case 11: {
                return 2;
            }
            case 6:
            case 24:
            case 25: {
                return this.getChild(0).getMaxLength();
            }
            case 12: {
                return -1;
            }
            case 10: {
                return this.getString().length();
            }
            case 20:
            case 21:
            case 22:
            case 23: {
                return 0;
            }
            default: {
                throw new RuntimeException("Token#getMaxLength(): Invalid Type: " + this.type);
            }
        }
    }
    
    private static final boolean isSet(final int options, final int flag) {
        return (options & flag) == flag;
    }
    
    final int analyzeFirstCharacter(final RangeToken result, int options) {
        switch (this.type) {
            case 1: {
                int ret = 0;
                for (int i = 0; i < this.size() && (ret = this.getChild(i).analyzeFirstCharacter(result, options)) == 0; ++i) {}
                return ret;
            }
            case 2: {
                if (this.size() == 0) {
                    return 0;
                }
                int ret2 = 0;
                boolean hasEmpty = false;
                for (int j = 0; j < this.size(); ++j) {
                    ret2 = this.getChild(j).analyzeFirstCharacter(result, options);
                    if (ret2 == 2) {
                        break;
                    }
                    if (ret2 == 0) {
                        hasEmpty = true;
                    }
                }
                return hasEmpty ? 0 : ret2;
            }
            case 26: {
                final int ret3 = this.getChild(0).analyzeFirstCharacter(result, options);
                if (this.size() == 1) {
                    return 0;
                }
                if (ret3 == 2) {
                    return ret3;
                }
                final int ret4 = this.getChild(1).analyzeFirstCharacter(result, options);
                if (ret4 == 2) {
                    return ret4;
                }
                return (ret3 != 0 && ret4 != 0) ? 1 : 0;
            }
            case 3:
            case 9: {
                this.getChild(0).analyzeFirstCharacter(result, options);
                return 0;
            }
            case 7:
            case 8: {
                return 0;
            }
            case 0: {
                int ch = this.getChar();
                result.addRange(ch, ch);
                if (ch < 65536 && isSet(options, 2)) {
                    ch = Character.toUpperCase((char)ch);
                    result.addRange(ch, ch);
                    ch = Character.toLowerCase((char)ch);
                    result.addRange(ch, ch);
                }
                return 1;
            }
            case 11: {
                return 2;
            }
            case 4: {
                result.mergeRanges(this);
                return 1;
            }
            case 5: {
                result.mergeRanges(complementRanges(this));
                return 1;
            }
            case 6:
            case 24: {
                return this.getChild(0).analyzeFirstCharacter(result, options);
            }
            case 25: {
                options |= ((ModifierToken)this).getOptions();
                options &= ~((ModifierToken)this).getOptionsMask();
                return this.getChild(0).analyzeFirstCharacter(result, options);
            }
            case 12: {
                result.addRange(0, 1114111);
                return 2;
            }
            case 10: {
                int cha = this.getString().charAt(0);
                final int ch2;
                if (REUtil.isHighSurrogate(cha) && this.getString().length() >= 2 && REUtil.isLowSurrogate(ch2 = this.getString().charAt(1))) {
                    cha = REUtil.composeFromSurrogates(cha, ch2);
                }
                result.addRange(cha, cha);
                if (cha < 65536 && isSet(options, 2)) {
                    cha = Character.toUpperCase((char)cha);
                    result.addRange(cha, cha);
                    cha = Character.toLowerCase((char)cha);
                    result.addRange(cha, cha);
                }
                return 1;
            }
            case 20:
            case 21:
            case 22:
            case 23: {
                return 0;
            }
            default: {
                throw new RuntimeException("Token#analyzeHeadCharacter(): Invalid Type: " + this.type);
            }
        }
    }
    
    private final boolean isShorterThan(final Token tok) {
        if (tok == null) {
            return false;
        }
        if (this.type != 10) {
            throw new RuntimeException("Internal Error: Illegal type: " + this.type);
        }
        final int mylength = this.getString().length();
        if (tok.type == 10) {
            final int otherlength = tok.getString().length();
            return mylength < otherlength;
        }
        throw new RuntimeException("Internal Error: Illegal type: " + tok.type);
    }
    
    final void findFixedString(final FixedStringContainer container, int options) {
        switch (this.type) {
            case 1: {
                Token prevToken = null;
                int prevOptions = 0;
                for (int i = 0; i < this.size(); ++i) {
                    this.getChild(i).findFixedString(container, options);
                    if (prevToken == null || prevToken.isShorterThan(container.token)) {
                        prevToken = container.token;
                        prevOptions = container.options;
                    }
                }
                container.token = prevToken;
                container.options = prevOptions;
                return;
            }
            case 2:
            case 3:
            case 4:
            case 5:
            case 7:
            case 8:
            case 9:
            case 11:
            case 12:
            case 20:
            case 21:
            case 22:
            case 23:
            case 26: {
                container.token = null;
                return;
            }
            case 0: {
                container.token = null;
                return;
            }
            case 10: {
                container.token = this;
                container.options = options;
                return;
            }
            case 6:
            case 24: {
                this.getChild(0).findFixedString(container, options);
                return;
            }
            case 25: {
                options |= ((ModifierToken)this).getOptions();
                options &= ~((ModifierToken)this).getOptionsMask();
                this.getChild(0).findFixedString(container, options);
                return;
            }
            default: {
                throw new RuntimeException("Token#findFixedString(): Invalid Type: " + this.type);
            }
        }
    }
    
    boolean match(final int ch) {
        throw new RuntimeException("NFAArrow#match(): Internal error: " + this.type);
    }
    
    protected static RangeToken getRange(final String name, final boolean positive) {
        if (Token.categories.size() == 0) {
            synchronized (Token.categories) {
                final Token[] ranges = new Token[Token.categoryNames.length];
                for (int i = 0; i < ranges.length; ++i) {
                    ranges[i] = createRange();
                }
                for (int j = 0; j < 65536; ++j) {
                    int type = Character.getType((char)j);
                    if (type == 21 || type == 22) {
                        if (j == 171 || j == 8216 || j == 8219 || j == 8220 || j == 8223 || j == 8249) {
                            type = 29;
                        }
                        if (j == 187 || j == 8217 || j == 8221 || j == 8250) {
                            type = 30;
                        }
                    }
                    ranges[type].addRange(j, j);
                    switch (type) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5: {
                            type = 31;
                            break;
                        }
                        case 6:
                        case 7:
                        case 8: {
                            type = 32;
                            break;
                        }
                        case 9:
                        case 10:
                        case 11: {
                            type = 33;
                            break;
                        }
                        case 12:
                        case 13:
                        case 14: {
                            type = 34;
                            break;
                        }
                        case 0:
                        case 15:
                        case 16:
                        case 18:
                        case 19: {
                            type = 35;
                            break;
                        }
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                        case 24:
                        case 29:
                        case 30: {
                            type = 36;
                            break;
                        }
                        case 25:
                        case 26:
                        case 27:
                        case 28: {
                            type = 37;
                            break;
                        }
                        default: {
                            throw new RuntimeException("org.apache.xerces.utils.regex.Token#getRange(): Unknown Unicode category: " + type);
                        }
                    }
                    ranges[type].addRange(j, j);
                }
                ranges[0].addRange(65536, 1114111);
                for (int j = 0; j < ranges.length; ++j) {
                    if (Token.categoryNames[j] != null) {
                        if (j == 0) {
                            ranges[j].addRange(65536, 1114111);
                        }
                        Token.categories.put(Token.categoryNames[j], ranges[j]);
                        Token.categories2.put(Token.categoryNames[j], complementRanges(ranges[j]));
                    }
                }
                final StringBuilder buffer = new StringBuilder(50);
                for (int k = 0; k < Token.blockNames.length; ++k) {
                    final Token r1 = createRange();
                    if (k < 84) {
                        final int location = k * 2;
                        final int rstart = "\u0000\u007f\u0080\u00ff\u0100\u017f\u0180\u024f\u0250\u02af\u02b0\u02ff\u0300\u036f\u0370\u03ff\u0400\u04ff\u0530\u058f\u0590\u05ff\u0600\u06ff\u0700\u074f\u0780\u07bf\u0900\u097f\u0980\u09ff\u0a00\u0a7f\u0a80\u0aff\u0b00\u0b7f\u0b80\u0bff\u0c00\u0c7f\u0c80\u0cff\u0d00\u0d7f\u0d80\u0dff\u0e00\u0e7f\u0e80\u0eff\u0f00\u0fff\u1000\u109f\u10a0\u10ff\u1100\u11ff\u1200\u137f\u13a0\u13ff\u1400\u167f\u1680\u169f\u16a0\u16ff\u1780\u17ff\u1800\u18af\u1e00\u1eff\u1f00\u1fff\u2000\u206f\u2070\u209f\u20a0\u20cf\u20d0\u20ff\u2100\u214f\u2150\u218f\u2190\u21ff\u2200\u22ff\u2300\u23ff\u2400\u243f\u2440\u245f\u2460\u24ff\u2500\u257f\u2580\u259f\u25a0\u25ff\u2600\u26ff\u2700\u27bf\u2800\u28ff\u2e80\u2eff\u2f00\u2fdf\u2ff0\u2fff\u3000\u303f\u3040\u309f\u30a0\u30ff\u3100\u312f\u3130\u318f\u3190\u319f\u31a0\u31bf\u3200\u32ff\u3300\u33ff\u3400\u4db5\u4e00\u9fff\ua000\ua48f\ua490\ua4cf\uac00\ud7a3\ue000\uf8ff\uf900\ufaff\ufb00\ufb4f\ufb50\ufdff\ufe20\ufe2f\ufe30\ufe4f\ufe50\ufe6f\ufe70\ufefe\ufeff\ufeff\uff00\uffef".charAt(location);
                        final int rend = "\u0000\u007f\u0080\u00ff\u0100\u017f\u0180\u024f\u0250\u02af\u02b0\u02ff\u0300\u036f\u0370\u03ff\u0400\u04ff\u0530\u058f\u0590\u05ff\u0600\u06ff\u0700\u074f\u0780\u07bf\u0900\u097f\u0980\u09ff\u0a00\u0a7f\u0a80\u0aff\u0b00\u0b7f\u0b80\u0bff\u0c00\u0c7f\u0c80\u0cff\u0d00\u0d7f\u0d80\u0dff\u0e00\u0e7f\u0e80\u0eff\u0f00\u0fff\u1000\u109f\u10a0\u10ff\u1100\u11ff\u1200\u137f\u13a0\u13ff\u1400\u167f\u1680\u169f\u16a0\u16ff\u1780\u17ff\u1800\u18af\u1e00\u1eff\u1f00\u1fff\u2000\u206f\u2070\u209f\u20a0\u20cf\u20d0\u20ff\u2100\u214f\u2150\u218f\u2190\u21ff\u2200\u22ff\u2300\u23ff\u2400\u243f\u2440\u245f\u2460\u24ff\u2500\u257f\u2580\u259f\u25a0\u25ff\u2600\u26ff\u2700\u27bf\u2800\u28ff\u2e80\u2eff\u2f00\u2fdf\u2ff0\u2fff\u3000\u303f\u3040\u309f\u30a0\u30ff\u3100\u312f\u3130\u318f\u3190\u319f\u31a0\u31bf\u3200\u32ff\u3300\u33ff\u3400\u4db5\u4e00\u9fff\ua000\ua48f\ua490\ua4cf\uac00\ud7a3\ue000\uf8ff\uf900\ufaff\ufb00\ufb4f\ufb50\ufdff\ufe20\ufe2f\ufe30\ufe4f\ufe50\ufe6f\ufe70\ufefe\ufeff\ufeff\uff00\uffef".charAt(location + 1);
                        r1.addRange(rstart, rend);
                    }
                    else {
                        final int location = (k - 84) * 2;
                        r1.addRange(Token.nonBMPBlockRanges[location], Token.nonBMPBlockRanges[location + 1]);
                    }
                    final String n = Token.blockNames[k];
                    if (n.equals("Specials")) {
                        r1.addRange(65520, 65533);
                    }
                    if (n.equals("Private Use")) {
                        r1.addRange(983040, 1048573);
                        r1.addRange(1048576, 1114109);
                    }
                    Token.categories.put(n, r1);
                    Token.categories2.put(n, complementRanges(r1));
                    buffer.setLength(0);
                    buffer.append("Is");
                    if (n.indexOf(32) >= 0) {
                        for (int ci = 0; ci < n.length(); ++ci) {
                            if (n.charAt(ci) != ' ') {
                                buffer.append(n.charAt(ci));
                            }
                        }
                    }
                    else {
                        buffer.append(n);
                    }
                    setAlias(buffer.toString(), n, true);
                }
                setAlias("ASSIGNED", "Cn", false);
                setAlias("UNASSIGNED", "Cn", true);
                final Token all = createRange();
                all.addRange(0, 1114111);
                Token.categories.put("ALL", all);
                Token.categories2.put("ALL", complementRanges(all));
                registerNonXS("ASSIGNED");
                registerNonXS("UNASSIGNED");
                registerNonXS("ALL");
                final Token isalpha = createRange();
                isalpha.mergeRanges(ranges[1]);
                isalpha.mergeRanges(ranges[2]);
                isalpha.mergeRanges(ranges[5]);
                Token.categories.put("IsAlpha", isalpha);
                Token.categories2.put("IsAlpha", complementRanges(isalpha));
                registerNonXS("IsAlpha");
                final Token isalnum = createRange();
                isalnum.mergeRanges(isalpha);
                isalnum.mergeRanges(ranges[9]);
                Token.categories.put("IsAlnum", isalnum);
                Token.categories2.put("IsAlnum", complementRanges(isalnum));
                registerNonXS("IsAlnum");
                final Token isspace = createRange();
                isspace.mergeRanges(Token.token_spaces);
                isspace.mergeRanges(ranges[34]);
                Token.categories.put("IsSpace", isspace);
                Token.categories2.put("IsSpace", complementRanges(isspace));
                registerNonXS("IsSpace");
                final Token isword = createRange();
                isword.mergeRanges(isalnum);
                isword.addRange(95, 95);
                Token.categories.put("IsWord", isword);
                Token.categories2.put("IsWord", complementRanges(isword));
                registerNonXS("IsWord");
                final Token isascii = createRange();
                isascii.addRange(0, 127);
                Token.categories.put("IsASCII", isascii);
                Token.categories2.put("IsASCII", complementRanges(isascii));
                registerNonXS("IsASCII");
                final Token isnotgraph = createRange();
                isnotgraph.mergeRanges(ranges[35]);
                isnotgraph.addRange(32, 32);
                Token.categories.put("IsGraph", complementRanges(isnotgraph));
                Token.categories2.put("IsGraph", isnotgraph);
                registerNonXS("IsGraph");
                final Token isxdigit = createRange();
                isxdigit.addRange(48, 57);
                isxdigit.addRange(65, 70);
                isxdigit.addRange(97, 102);
                Token.categories.put("IsXDigit", complementRanges(isxdigit));
                Token.categories2.put("IsXDigit", isxdigit);
                registerNonXS("IsXDigit");
                setAlias("IsDigit", "Nd", true);
                setAlias("IsUpper", "Lu", true);
                setAlias("IsLower", "Ll", true);
                setAlias("IsCntrl", "C", true);
                setAlias("IsPrint", "C", false);
                setAlias("IsPunct", "P", true);
                registerNonXS("IsDigit");
                registerNonXS("IsUpper");
                registerNonXS("IsLower");
                registerNonXS("IsCntrl");
                registerNonXS("IsPrint");
                registerNonXS("IsPunct");
                setAlias("alpha", "IsAlpha", true);
                setAlias("alnum", "IsAlnum", true);
                setAlias("ascii", "IsASCII", true);
                setAlias("cntrl", "IsCntrl", true);
                setAlias("digit", "IsDigit", true);
                setAlias("graph", "IsGraph", true);
                setAlias("lower", "IsLower", true);
                setAlias("print", "IsPrint", true);
                setAlias("punct", "IsPunct", true);
                setAlias("space", "IsSpace", true);
                setAlias("upper", "IsUpper", true);
                setAlias("word", "IsWord", true);
                setAlias("xdigit", "IsXDigit", true);
                registerNonXS("alpha");
                registerNonXS("alnum");
                registerNonXS("ascii");
                registerNonXS("cntrl");
                registerNonXS("digit");
                registerNonXS("graph");
                registerNonXS("lower");
                registerNonXS("print");
                registerNonXS("punct");
                registerNonXS("space");
                registerNonXS("upper");
                registerNonXS("word");
                registerNonXS("xdigit");
            }
        }
        final RangeToken tok = positive ? Token.categories.get(name) : Token.categories2.get(name);
        return tok;
    }
    
    protected static RangeToken getRange(final String name, final boolean positive, final boolean xs) {
        RangeToken range = getRange(name, positive);
        if (xs && range != null && isRegisterNonXS(name)) {
            range = null;
        }
        return range;
    }
    
    protected static void registerNonXS(final String name) {
        Token.nonxs.add(name);
    }
    
    protected static boolean isRegisterNonXS(final String name) {
        return Token.nonxs.contains(name);
    }
    
    private static void setAlias(final String newName, final String name, final boolean positive) {
        final Token t1 = Token.categories.get(name);
        final Token t2 = Token.categories2.get(name);
        if (positive) {
            Token.categories.put(newName, t1);
            Token.categories2.put(newName, t2);
        }
        else {
            Token.categories2.put(newName, t1);
            Token.categories.put(newName, t2);
        }
    }
    
    static synchronized Token getGraphemePattern() {
        if (Token.token_grapheme != null) {
            return Token.token_grapheme;
        }
        final Token base_char = createRange();
        base_char.mergeRanges(getRange("ASSIGNED", true));
        base_char.subtractRanges(getRange("M", true));
        base_char.subtractRanges(getRange("C", true));
        final Token virama = createRange();
        for (int i = 0; i < "\u094d\u09cd\u0a4d\u0acd\u0b4d\u0bcd\u0c4d\u0ccd\u0d4d\u0e3a\u0f84".length(); ++i) {
            virama.addRange(i, i);
        }
        final Token combiner_wo_virama = createRange();
        combiner_wo_virama.mergeRanges(getRange("M", true));
        combiner_wo_virama.addRange(4448, 4607);
        combiner_wo_virama.addRange(65438, 65439);
        final Token left = createUnion();
        left.addChild(base_char);
        left.addChild(Token.token_empty);
        Token foo = createUnion();
        foo.addChild(createConcat(virama, getRange("L", true)));
        foo.addChild(combiner_wo_virama);
        foo = createClosure(foo);
        foo = createConcat(left, foo);
        return Token.token_grapheme = foo;
    }
    
    static synchronized Token getCombiningCharacterSequence() {
        if (Token.token_ccs != null) {
            return Token.token_ccs;
        }
        Token foo = createClosure(getRange("M", true));
        foo = createConcat(getRange("M", false), foo);
        return Token.token_ccs = foo;
    }
    
    static {
        Token.tokens = 0;
        Token.token_empty = new Token(7);
        Token.token_linebeginning = createAnchor(94);
        Token.token_linebeginning2 = createAnchor(64);
        Token.token_lineend = createAnchor(36);
        Token.token_stringbeginning = createAnchor(65);
        Token.token_stringend = createAnchor(122);
        Token.token_stringend2 = createAnchor(90);
        Token.token_wordedge = createAnchor(98);
        Token.token_not_wordedge = createAnchor(66);
        Token.token_wordbeginning = createAnchor(60);
        Token.token_wordend = createAnchor(62);
        Token.token_dot = new Token(11);
        (Token.token_0to9 = createRange()).addRange(48, 57);
        (Token.token_wordchars = createRange()).addRange(48, 57);
        Token.token_wordchars.addRange(65, 90);
        Token.token_wordchars.addRange(95, 95);
        Token.token_wordchars.addRange(97, 122);
        (Token.token_spaces = createRange()).addRange(9, 9);
        Token.token_spaces.addRange(10, 10);
        Token.token_spaces.addRange(12, 12);
        Token.token_spaces.addRange(13, 13);
        Token.token_spaces.addRange(32, 32);
        Token.token_not_0to9 = complementRanges(Token.token_0to9);
        Token.token_not_wordchars = complementRanges(Token.token_wordchars);
        Token.token_not_spaces = complementRanges(Token.token_spaces);
        categories = new HashMap<String, Token>();
        categories2 = new HashMap<String, Token>();
        categoryNames = new String[] { "Cn", "Lu", "Ll", "Lt", "Lm", "Lo", "Mn", "Me", "Mc", "Nd", "Nl", "No", "Zs", "Zl", "Zp", "Cc", "Cf", null, "Co", "Cs", "Pd", "Ps", "Pe", "Pc", "Po", "Sm", "Sc", "Sk", "So", "Pi", "Pf", "L", "M", "N", "Z", "C", "P", "S" };
        blockNames = new String[] { "Basic Latin", "Latin-1 Supplement", "Latin Extended-A", "Latin Extended-B", "IPA Extensions", "Spacing Modifier Letters", "Combining Diacritical Marks", "Greek", "Cyrillic", "Armenian", "Hebrew", "Arabic", "Syriac", "Thaana", "Devanagari", "Bengali", "Gurmukhi", "Gujarati", "Oriya", "Tamil", "Telugu", "Kannada", "Malayalam", "Sinhala", "Thai", "Lao", "Tibetan", "Myanmar", "Georgian", "Hangul Jamo", "Ethiopic", "Cherokee", "Unified Canadian Aboriginal Syllabics", "Ogham", "Runic", "Khmer", "Mongolian", "Latin Extended Additional", "Greek Extended", "General Punctuation", "Superscripts and Subscripts", "Currency Symbols", "Combining Marks for Symbols", "Letterlike Symbols", "Number Forms", "Arrows", "Mathematical Operators", "Miscellaneous Technical", "Control Pictures", "Optical Character Recognition", "Enclosed Alphanumerics", "Box Drawing", "Block Elements", "Geometric Shapes", "Miscellaneous Symbols", "Dingbats", "Braille Patterns", "CJK Radicals Supplement", "Kangxi Radicals", "Ideographic Description Characters", "CJK Symbols and Punctuation", "Hiragana", "Katakana", "Bopomofo", "Hangul Compatibility Jamo", "Kanbun", "Bopomofo Extended", "Enclosed CJK Letters and Months", "CJK Compatibility", "CJK Unified Ideographs Extension A", "CJK Unified Ideographs", "Yi Syllables", "Yi Radicals", "Hangul Syllables", "Private Use", "CJK Compatibility Ideographs", "Alphabetic Presentation Forms", "Arabic Presentation Forms-A", "Combining Half Marks", "CJK Compatibility Forms", "Small Form Variants", "Arabic Presentation Forms-B", "Specials", "Halfwidth and Fullwidth Forms", "Old Italic", "Gothic", "Deseret", "Byzantine Musical Symbols", "Musical Symbols", "Mathematical Alphanumeric Symbols", "CJK Unified Ideographs Extension B", "CJK Compatibility Ideographs Supplement", "Tags" };
        nonBMPBlockRanges = new int[] { 66304, 66351, 66352, 66383, 66560, 66639, 118784, 119039, 119040, 119295, 119808, 120831, 131072, 173782, 194560, 195103, 917504, 917631 };
        nonxs = Collections.synchronizedSet(new HashSet<String>());
        Token.token_grapheme = null;
        Token.token_ccs = null;
    }
    
    static class FixedStringContainer
    {
        Token token;
        int options;
        
        FixedStringContainer() {
            this.token = null;
            this.options = 0;
        }
    }
    
    static class StringToken extends Token implements Serializable
    {
        private static final long serialVersionUID = -4614366944218504172L;
        String string;
        final int refNumber;
        
        StringToken(final int type, final String str, final int n) {
            super(type);
            this.string = str;
            this.refNumber = n;
        }
        
        @Override
        int getReferenceNumber() {
            return this.refNumber;
        }
        
        @Override
        String getString() {
            return this.string;
        }
        
        @Override
        public String toString(final int options) {
            if (this.type == 12) {
                return "\\" + this.refNumber;
            }
            return REUtil.quoteMeta(this.string);
        }
    }
    
    static class ConcatToken extends Token implements Serializable
    {
        private static final long serialVersionUID = 8717321425541346381L;
        final Token child;
        final Token child2;
        
        ConcatToken(final Token t1, final Token t2) {
            super(1);
            this.child = t1;
            this.child2 = t2;
        }
        
        @Override
        int size() {
            return 2;
        }
        
        @Override
        Token getChild(final int index) {
            return (index == 0) ? this.child : this.child2;
        }
        
        @Override
        public String toString(final int options) {
            String ret;
            if (this.child2.type == 3 && this.child2.getChild(0) == this.child) {
                ret = this.child.toString(options) + "+";
            }
            else if (this.child2.type == 9 && this.child2.getChild(0) == this.child) {
                ret = this.child.toString(options) + "+?";
            }
            else {
                ret = this.child.toString(options) + this.child2.toString(options);
            }
            return ret;
        }
    }
    
    static class CharToken extends Token implements Serializable
    {
        private static final long serialVersionUID = -4394272816279496989L;
        final int chardata;
        
        CharToken(final int type, final int ch) {
            super(type);
            this.chardata = ch;
        }
        
        @Override
        int getChar() {
            return this.chardata;
        }
        
        @Override
        public String toString(final int options) {
            String ret = null;
            Label_0394: {
                switch (this.type) {
                    case 0: {
                        switch (this.chardata) {
                            case 40:
                            case 41:
                            case 42:
                            case 43:
                            case 46:
                            case 63:
                            case 91:
                            case 92:
                            case 123:
                            case 124: {
                                ret = "\\" + (char)this.chardata;
                                break Label_0394;
                            }
                            case 12: {
                                ret = "\\f";
                                break Label_0394;
                            }
                            case 10: {
                                ret = "\\n";
                                break Label_0394;
                            }
                            case 13: {
                                ret = "\\r";
                                break Label_0394;
                            }
                            case 9: {
                                ret = "\\t";
                                break Label_0394;
                            }
                            case 27: {
                                ret = "\\e";
                                break Label_0394;
                            }
                            default: {
                                if (this.chardata >= 65536) {
                                    final String pre = "0" + Integer.toHexString(this.chardata);
                                    ret = "\\v" + pre.substring(pre.length() - 6, pre.length());
                                    break Label_0394;
                                }
                                ret = "" + (char)this.chardata;
                                break Label_0394;
                            }
                        }
                        break;
                    }
                    case 8: {
                        if (this == Token.token_linebeginning || this == Token.token_lineend) {
                            ret = "" + (char)this.chardata;
                            break;
                        }
                        ret = "\\" + (char)this.chardata;
                        break;
                    }
                    default: {
                        ret = null;
                        break;
                    }
                }
            }
            return ret;
        }
        
        @Override
        boolean match(final int ch) {
            if (this.type == 0) {
                return ch == this.chardata;
            }
            throw new RuntimeException("NFAArrow#match(): Internal error: " + this.type);
        }
    }
    
    static class ClosureToken extends Token implements Serializable
    {
        private static final long serialVersionUID = 1308971930673997452L;
        int min;
        int max;
        final Token child;
        
        ClosureToken(final int type, final Token tok) {
            super(type);
            this.child = tok;
            this.setMin(-1);
            this.setMax(-1);
        }
        
        @Override
        int size() {
            return 1;
        }
        
        @Override
        Token getChild(final int index) {
            return this.child;
        }
        
        @Override
        final void setMin(final int min) {
            this.min = min;
        }
        
        @Override
        final void setMax(final int max) {
            this.max = max;
        }
        
        @Override
        final int getMin() {
            return this.min;
        }
        
        @Override
        final int getMax() {
            return this.max;
        }
        
        @Override
        public String toString(final int options) {
            String ret;
            if (this.type == 3) {
                if (this.getMin() < 0 && this.getMax() < 0) {
                    ret = this.child.toString(options) + "*";
                }
                else if (this.getMin() == this.getMax()) {
                    ret = this.child.toString(options) + "{" + this.getMin() + "}";
                }
                else if (this.getMin() >= 0 && this.getMax() >= 0) {
                    ret = this.child.toString(options) + "{" + this.getMin() + "," + this.getMax() + "}";
                }
                else {
                    if (this.getMin() < 0 || this.getMax() >= 0) {
                        throw new RuntimeException("Token#toString(): CLOSURE " + this.getMin() + ", " + this.getMax());
                    }
                    ret = this.child.toString(options) + "{" + this.getMin() + ",}";
                }
            }
            else if (this.getMin() < 0 && this.getMax() < 0) {
                ret = this.child.toString(options) + "*?";
            }
            else if (this.getMin() == this.getMax()) {
                ret = this.child.toString(options) + "{" + this.getMin() + "}?";
            }
            else if (this.getMin() >= 0 && this.getMax() >= 0) {
                ret = this.child.toString(options) + "{" + this.getMin() + "," + this.getMax() + "}?";
            }
            else {
                if (this.getMin() < 0 || this.getMax() >= 0) {
                    throw new RuntimeException("Token#toString(): NONGREEDYCLOSURE " + this.getMin() + ", " + this.getMax());
                }
                ret = this.child.toString(options) + "{" + this.getMin() + ",}?";
            }
            return ret;
        }
    }
    
    static class ParenToken extends Token implements Serializable
    {
        private static final long serialVersionUID = -5938014719827987704L;
        final Token child;
        final int parennumber;
        
        ParenToken(final int type, final Token tok, final int paren) {
            super(type);
            this.child = tok;
            this.parennumber = paren;
        }
        
        @Override
        int size() {
            return 1;
        }
        
        @Override
        Token getChild(final int index) {
            return this.child;
        }
        
        @Override
        int getParenNumber() {
            return this.parennumber;
        }
        
        @Override
        public String toString(final int options) {
            String ret = null;
            switch (this.type) {
                case 6: {
                    if (this.parennumber == 0) {
                        ret = "(?:" + this.child.toString(options) + ")";
                        break;
                    }
                    ret = "(" + this.child.toString(options) + ")";
                    break;
                }
                case 20: {
                    ret = "(?=" + this.child.toString(options) + ")";
                    break;
                }
                case 21: {
                    ret = "(?!" + this.child.toString(options) + ")";
                    break;
                }
                case 22: {
                    ret = "(?<=" + this.child.toString(options) + ")";
                    break;
                }
                case 23: {
                    ret = "(?<!" + this.child.toString(options) + ")";
                    break;
                }
                case 24: {
                    ret = "(?>" + this.child.toString(options) + ")";
                    break;
                }
            }
            return ret;
        }
    }
    
    static class ConditionToken extends Token implements Serializable
    {
        private static final long serialVersionUID = 4353765277910594411L;
        final int refNumber;
        final Token condition;
        final Token yes;
        final Token no;
        
        ConditionToken(final int refno, final Token cond, final Token yespat, final Token nopat) {
            super(26);
            this.refNumber = refno;
            this.condition = cond;
            this.yes = yespat;
            this.no = nopat;
        }
        
        @Override
        int size() {
            return (this.no == null) ? 1 : 2;
        }
        
        @Override
        Token getChild(final int index) {
            if (index == 0) {
                return this.yes;
            }
            if (index == 1) {
                return this.no;
            }
            throw new RuntimeException("Internal Error: " + index);
        }
        
        @Override
        public String toString(final int options) {
            String ret;
            if (this.refNumber > 0) {
                ret = "(?(" + this.refNumber + ")";
            }
            else if (this.condition.type == 8) {
                ret = "(?(" + this.condition + ")";
            }
            else {
                ret = "(?" + this.condition;
            }
            if (this.no == null) {
                ret = ret + this.yes + ")";
            }
            else {
                ret = ret + this.yes + "|" + this.no + ")";
            }
            return ret;
        }
    }
    
    static class ModifierToken extends Token implements Serializable
    {
        private static final long serialVersionUID = -9114536559696480356L;
        final Token child;
        final int add;
        final int mask;
        
        ModifierToken(final Token tok, final int add, final int mask) {
            super(25);
            this.child = tok;
            this.add = add;
            this.mask = mask;
        }
        
        @Override
        int size() {
            return 1;
        }
        
        @Override
        Token getChild(final int index) {
            return this.child;
        }
        
        int getOptions() {
            return this.add;
        }
        
        int getOptionsMask() {
            return this.mask;
        }
        
        @Override
        public String toString(final int options) {
            return "(?" + ((this.add == 0) ? "" : REUtil.createOptionString(this.add)) + ((this.mask == 0) ? "" : REUtil.createOptionString(this.mask)) + ":" + this.child.toString(options) + ")";
        }
    }
    
    static class UnionToken extends Token implements Serializable
    {
        private static final long serialVersionUID = -2568843945989489861L;
        List<Token> children;
        private static final ObjectStreamField[] serialPersistentFields;
        
        UnionToken(final int type) {
            super(type);
        }
        
        @Override
        void addChild(final Token tok) {
            if (tok == null) {
                return;
            }
            if (this.children == null) {
                this.children = new ArrayList<Token>();
            }
            if (this.type == 2) {
                this.children.add(tok);
                return;
            }
            if (tok.type == 1) {
                for (int i = 0; i < tok.size(); ++i) {
                    this.addChild(tok.getChild(i));
                }
                return;
            }
            final int size = this.children.size();
            if (size == 0) {
                this.children.add(tok);
                return;
            }
            Token previous = this.children.get(size - 1);
            if ((previous.type != 0 && previous.type != 10) || (tok.type != 0 && tok.type != 10)) {
                this.children.add(tok);
                return;
            }
            final int nextMaxLength = (tok.type == 0) ? 2 : tok.getString().length();
            StringBuilder buffer;
            if (previous.type == 0) {
                buffer = new StringBuilder(2 + nextMaxLength);
                final int ch = previous.getChar();
                if (ch >= 65536) {
                    buffer.append(REUtil.decomposeToSurrogates(ch));
                }
                else {
                    buffer.append((char)ch);
                }
                previous = Token.createString(null);
                this.children.set(size - 1, previous);
            }
            else {
                buffer = new StringBuilder(previous.getString().length() + nextMaxLength);
                buffer.append(previous.getString());
            }
            if (tok.type == 0) {
                final int ch = tok.getChar();
                if (ch >= 65536) {
                    buffer.append(REUtil.decomposeToSurrogates(ch));
                }
                else {
                    buffer.append((char)ch);
                }
            }
            else {
                buffer.append(tok.getString());
            }
            ((StringToken)previous).string = new String(buffer);
        }
        
        @Override
        int size() {
            return (this.children == null) ? 0 : this.children.size();
        }
        
        @Override
        Token getChild(final int index) {
            return this.children.get(index);
        }
        
        @Override
        public String toString(final int options) {
            if (this.type == 1) {
                String ret;
                if (this.children.size() == 2) {
                    final Token ch = this.getChild(0);
                    final Token ch2 = this.getChild(1);
                    if (ch2.type == 3 && ch2.getChild(0) == ch) {
                        ret = ch.toString(options) + "+";
                    }
                    else if (ch2.type == 9 && ch2.getChild(0) == ch) {
                        ret = ch.toString(options) + "+?";
                    }
                    else {
                        ret = ch.toString(options) + ch2.toString(options);
                    }
                }
                else {
                    final StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < this.children.size(); ++i) {
                        sb.append(this.children.get(i).toString(options));
                    }
                    ret = new String(sb);
                }
                return ret;
            }
            String ret;
            if (this.children.size() == 2 && this.getChild(1).type == 7) {
                ret = this.getChild(0).toString(options) + "?";
            }
            else if (this.children.size() == 2 && this.getChild(0).type == 7) {
                ret = this.getChild(1).toString(options) + "??";
            }
            else {
                final StringBuilder sb = new StringBuilder();
                sb.append(this.children.get(0).toString(options));
                for (int i = 1; i < this.children.size(); ++i) {
                    sb.append('|');
                    sb.append(this.children.get(i).toString(options));
                }
                ret = new String(sb);
            }
            return ret;
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            final Vector<Token> vChildren = (this.children == null) ? null : new Vector<Token>(this.children);
            final ObjectOutputStream.PutField pf = out.putFields();
            pf.put("children", vChildren);
            out.writeFields();
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            final ObjectInputStream.GetField gf = in.readFields();
            final Vector<Token> vChildren = (Vector<Token>)gf.get("children", null);
            if (vChildren != null) {
                this.children = new ArrayList<Token>(vChildren);
            }
        }
        
        static {
            serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("children", Vector.class) };
        }
    }
}
