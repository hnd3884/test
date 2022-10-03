package org.apache.xerces.impl.xpath.regex;

import java.util.Vector;
import java.util.Hashtable;
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
    static RangeToken token_all;
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
    private static final Hashtable categories;
    private static final Hashtable categories2;
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
    static Hashtable nonxs;
    static final String viramaString = "\u094d\u09cd\u0a4d\u0acd\u0b4d\u0bcd\u0c4d\u0ccd\u0d4d\u0e3a\u0f84";
    private static Token token_grapheme;
    private static Token token_ccs;
    
    static ParenToken createLook(final int n, final Token token) {
        ++Token.tokens;
        return new ParenToken(n, token, 0);
    }
    
    static ParenToken createParen(final Token token, final int n) {
        ++Token.tokens;
        return new ParenToken(6, token, n);
    }
    
    static ClosureToken createClosure(final Token token) {
        ++Token.tokens;
        return new ClosureToken(3, token);
    }
    
    static ClosureToken createNGClosure(final Token token) {
        ++Token.tokens;
        return new ClosureToken(9, token);
    }
    
    static ConcatToken createConcat(final Token token, final Token token2) {
        ++Token.tokens;
        return new ConcatToken(token, token2);
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
    
    static CharToken createChar(final int n) {
        ++Token.tokens;
        return new CharToken(0, n);
    }
    
    private static CharToken createAnchor(final int n) {
        ++Token.tokens;
        return new CharToken(8, n);
    }
    
    static StringToken createBackReference(final int n) {
        ++Token.tokens;
        return new StringToken(12, null, n);
    }
    
    static StringToken createString(final String s) {
        ++Token.tokens;
        return new StringToken(10, s, 0);
    }
    
    static ModifierToken createModifierGroup(final Token token, final int n, final int n2) {
        ++Token.tokens;
        return new ModifierToken(token, n, n2);
    }
    
    static ConditionToken createCondition(final int n, final Token token, final Token token2, final Token token3) {
        ++Token.tokens;
        return new ConditionToken(n, token, token2, token3);
    }
    
    protected Token(final int type) {
        this.type = type;
    }
    
    int size() {
        return 0;
    }
    
    Token getChild(final int n) {
        return null;
    }
    
    void addChild(final Token token) {
        throw new RuntimeException("Not supported.");
    }
    
    protected void addRange(final int n, final int n2) {
        throw new RuntimeException("Not supported.");
    }
    
    protected void sortRanges() {
        throw new RuntimeException("Not supported.");
    }
    
    protected void compactRanges() {
        throw new RuntimeException("Not supported.");
    }
    
    protected void mergeRanges(final Token token) {
        throw new RuntimeException("Not supported.");
    }
    
    protected void subtractRanges(final Token token) {
        throw new RuntimeException("Not supported.");
    }
    
    protected void intersectRanges(final Token token) {
        throw new RuntimeException("Not supported.");
    }
    
    static Token complementRanges(final Token token) {
        return RangeToken.complementRanges(token);
    }
    
    void setMin(final int n) {
    }
    
    void setMax(final int n) {
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
    
    public String toString() {
        return this.toString(0);
    }
    
    public String toString(final int n) {
        return (this.type == 11) ? "." : "";
    }
    
    final int getMinLength() {
        switch (this.type) {
            case 1: {
                int n = 0;
                for (int i = 0; i < this.size(); ++i) {
                    n += this.getChild(i).getMinLength();
                }
                return n;
            }
            case 2:
            case 26: {
                if (this.size() == 0) {
                    return 0;
                }
                int minLength = this.getChild(0).getMinLength();
                for (int j = 1; j < this.size(); ++j) {
                    final int minLength2 = this.getChild(j).getMinLength();
                    if (minLength2 < minLength) {
                        minLength = minLength2;
                    }
                }
                return minLength;
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
                int n = 0;
                for (int i = 0; i < this.size(); ++i) {
                    final int maxLength = this.getChild(i).getMaxLength();
                    if (maxLength < 0) {
                        return -1;
                    }
                    n += maxLength;
                }
                return n;
            }
            case 2:
            case 26: {
                if (this.size() == 0) {
                    return 0;
                }
                int maxLength2 = this.getChild(0).getMaxLength();
                for (int n2 = 1; maxLength2 >= 0 && n2 < this.size(); ++n2) {
                    final int maxLength3 = this.getChild(n2).getMaxLength();
                    if (maxLength3 < 0) {
                        maxLength2 = -1;
                        break;
                    }
                    if (maxLength3 > maxLength2) {
                        maxLength2 = maxLength3;
                    }
                }
                return maxLength2;
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
    
    private static final boolean isSet(final int n, final int n2) {
        return (n & n2) == n2;
    }
    
    final int analyzeFirstCharacter(final RangeToken rangeToken, int n) {
        switch (this.type) {
            case 1: {
                int analyzeFirstCharacter = 0;
                for (int n2 = 0; n2 < this.size() && (analyzeFirstCharacter = this.getChild(n2).analyzeFirstCharacter(rangeToken, n)) == 0; ++n2) {}
                return analyzeFirstCharacter;
            }
            case 2: {
                if (this.size() == 0) {
                    return 0;
                }
                int analyzeFirstCharacter2 = 0;
                boolean b = false;
                for (int i = 0; i < this.size(); ++i) {
                    analyzeFirstCharacter2 = this.getChild(i).analyzeFirstCharacter(rangeToken, n);
                    if (analyzeFirstCharacter2 == 2) {
                        break;
                    }
                    if (analyzeFirstCharacter2 == 0) {
                        b = true;
                    }
                }
                return b ? 0 : analyzeFirstCharacter2;
            }
            case 26: {
                final int analyzeFirstCharacter3 = this.getChild(0).analyzeFirstCharacter(rangeToken, n);
                if (this.size() == 1) {
                    return 0;
                }
                if (analyzeFirstCharacter3 == 2) {
                    return analyzeFirstCharacter3;
                }
                final int analyzeFirstCharacter4 = this.getChild(1).analyzeFirstCharacter(rangeToken, n);
                if (analyzeFirstCharacter4 == 2) {
                    return analyzeFirstCharacter4;
                }
                return (analyzeFirstCharacter3 != 0 && analyzeFirstCharacter4 != 0) ? 1 : 0;
            }
            case 3:
            case 9: {
                this.getChild(0).analyzeFirstCharacter(rangeToken, n);
                return 0;
            }
            case 7:
            case 8: {
                return 0;
            }
            case 0: {
                final int char1 = this.getChar();
                rangeToken.addRange(char1, char1);
                if (char1 < 65536 && isSet(n, 2)) {
                    final char upperCase = Character.toUpperCase((char)char1);
                    rangeToken.addRange(upperCase, upperCase);
                    final char lowerCase = Character.toLowerCase(upperCase);
                    rangeToken.addRange(lowerCase, lowerCase);
                }
                return 1;
            }
            case 11: {
                return 2;
            }
            case 4: {
                rangeToken.mergeRanges(this);
                return 1;
            }
            case 5: {
                rangeToken.mergeRanges(complementRanges(this));
                return 1;
            }
            case 6:
            case 24: {
                return this.getChild(0).analyzeFirstCharacter(rangeToken, n);
            }
            case 25: {
                n |= ((ModifierToken)this).getOptions();
                n &= ~((ModifierToken)this).getOptionsMask();
                return this.getChild(0).analyzeFirstCharacter(rangeToken, n);
            }
            case 12: {
                rangeToken.addRange(0, 1114111);
                return 2;
            }
            case 10: {
                int n3 = this.getString().charAt(0);
                final char char2;
                if (REUtil.isHighSurrogate(n3) && this.getString().length() >= 2 && REUtil.isLowSurrogate(char2 = this.getString().charAt(1))) {
                    n3 = REUtil.composeFromSurrogates(n3, char2);
                }
                rangeToken.addRange(n3, n3);
                if (n3 < 65536 && isSet(n, 2)) {
                    final char upperCase2 = Character.toUpperCase((char)n3);
                    rangeToken.addRange(upperCase2, upperCase2);
                    final char lowerCase2 = Character.toLowerCase(upperCase2);
                    rangeToken.addRange(lowerCase2, lowerCase2);
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
    
    private final boolean isShorterThan(final Token token) {
        if (token == null) {
            return false;
        }
        if (this.type != 10) {
            throw new RuntimeException("Internal Error: Illegal type: " + this.type);
        }
        final int length = this.getString().length();
        if (token.type == 10) {
            return length < token.getString().length();
        }
        throw new RuntimeException("Internal Error: Illegal type: " + token.type);
    }
    
    final void findFixedString(final FixedStringContainer fixedStringContainer, int options) {
        switch (this.type) {
            case 1: {
                Token token = null;
                int options2 = 0;
                for (int i = 0; i < this.size(); ++i) {
                    this.getChild(i).findFixedString(fixedStringContainer, options);
                    if (token == null || token.isShorterThan(fixedStringContainer.token)) {
                        token = fixedStringContainer.token;
                        options2 = fixedStringContainer.options;
                    }
                }
                fixedStringContainer.token = token;
                fixedStringContainer.options = options2;
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
                fixedStringContainer.token = null;
                return;
            }
            case 0: {
                fixedStringContainer.token = null;
                return;
            }
            case 10: {
                fixedStringContainer.token = this;
                fixedStringContainer.options = options;
                return;
            }
            case 6:
            case 24: {
                this.getChild(0).findFixedString(fixedStringContainer, options);
                return;
            }
            case 25: {
                options |= ((ModifierToken)this).getOptions();
                options &= ~((ModifierToken)this).getOptionsMask();
                this.getChild(0).findFixedString(fixedStringContainer, options);
                return;
            }
            default: {
                throw new RuntimeException("Token#findFixedString(): Invalid Type: " + this.type);
            }
        }
    }
    
    boolean match(final int n) {
        throw new RuntimeException("NFAArrow#match(): Internal error: " + this.type);
    }
    
    protected static RangeToken getRange(final String s, final boolean b) {
        if (Token.categories.size() == 0) {
            synchronized (Token.categories) {
                final Token[] array = new Token[Token.categoryNames.length];
                for (int i = 0; i < array.length; ++i) {
                    array[i] = createRange();
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
                    array[type].addRange(j, j);
                    int n = 0;
                    switch (type) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5: {
                            n = 31;
                            break;
                        }
                        case 6:
                        case 7:
                        case 8: {
                            n = 32;
                            break;
                        }
                        case 9:
                        case 10:
                        case 11: {
                            n = 33;
                            break;
                        }
                        case 12:
                        case 13:
                        case 14: {
                            n = 34;
                            break;
                        }
                        case 0:
                        case 15:
                        case 16:
                        case 18:
                        case 19: {
                            n = 35;
                            break;
                        }
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                        case 24:
                        case 29:
                        case 30: {
                            n = 36;
                            break;
                        }
                        case 25:
                        case 26:
                        case 27:
                        case 28: {
                            n = 37;
                            break;
                        }
                        default: {
                            throw new RuntimeException("org.apache.xerces.utils.regex.Token#getRange(): Unknown Unicode category: " + type);
                        }
                    }
                    array[n].addRange(j, j);
                }
                array[0].addRange(65536, 1114111);
                array[35].addRange(65536, 1114111);
                for (int k = 0; k < array.length; ++k) {
                    if (Token.categoryNames[k] != null) {
                        if (k == 0) {
                            array[k].addRange(65536, 1114111);
                        }
                        Token.categories.put(Token.categoryNames[k], array[k]);
                        Token.categories2.put(Token.categoryNames[k], complementRanges(array[k]));
                    }
                }
                final StringBuffer sb = new StringBuffer(50);
                for (int l = 0; l < Token.blockNames.length; ++l) {
                    final RangeToken range = createRange();
                    if (l < 84) {
                        final int n2 = l * 2;
                        range.addRange("\u0000\u007f\u0080\u00ff\u0100\u017f\u0180\u024f\u0250\u02af\u02b0\u02ff\u0300\u036f\u0370\u03ff\u0400\u04ff\u0530\u058f\u0590\u05ff\u0600\u06ff\u0700\u074f\u0780\u07bf\u0900\u097f\u0980\u09ff\u0a00\u0a7f\u0a80\u0aff\u0b00\u0b7f\u0b80\u0bff\u0c00\u0c7f\u0c80\u0cff\u0d00\u0d7f\u0d80\u0dff\u0e00\u0e7f\u0e80\u0eff\u0f00\u0fff\u1000\u109f\u10a0\u10ff\u1100\u11ff\u1200\u137f\u13a0\u13ff\u1400\u167f\u1680\u169f\u16a0\u16ff\u1780\u17ff\u1800\u18af\u1e00\u1eff\u1f00\u1fff\u2000\u206f\u2070\u209f\u20a0\u20cf\u20d0\u20ff\u2100\u214f\u2150\u218f\u2190\u21ff\u2200\u22ff\u2300\u23ff\u2400\u243f\u2440\u245f\u2460\u24ff\u2500\u257f\u2580\u259f\u25a0\u25ff\u2600\u26ff\u2700\u27bf\u2800\u28ff\u2e80\u2eff\u2f00\u2fdf\u2ff0\u2fff\u3000\u303f\u3040\u309f\u30a0\u30ff\u3100\u312f\u3130\u318f\u3190\u319f\u31a0\u31bf\u3200\u32ff\u3300\u33ff\u3400\u4db5\u4e00\u9fff\ua000\ua48f\ua490\ua4cf\uac00\ud7a3\ue000\uf8ff\uf900\ufaff\ufb00\ufb4f\ufb50\ufdff\ufe20\ufe2f\ufe30\ufe4f\ufe50\ufe6f\ufe70\ufefe\ufeff\ufeff\uff00\uffef".charAt(n2), "\u0000\u007f\u0080\u00ff\u0100\u017f\u0180\u024f\u0250\u02af\u02b0\u02ff\u0300\u036f\u0370\u03ff\u0400\u04ff\u0530\u058f\u0590\u05ff\u0600\u06ff\u0700\u074f\u0780\u07bf\u0900\u097f\u0980\u09ff\u0a00\u0a7f\u0a80\u0aff\u0b00\u0b7f\u0b80\u0bff\u0c00\u0c7f\u0c80\u0cff\u0d00\u0d7f\u0d80\u0dff\u0e00\u0e7f\u0e80\u0eff\u0f00\u0fff\u1000\u109f\u10a0\u10ff\u1100\u11ff\u1200\u137f\u13a0\u13ff\u1400\u167f\u1680\u169f\u16a0\u16ff\u1780\u17ff\u1800\u18af\u1e00\u1eff\u1f00\u1fff\u2000\u206f\u2070\u209f\u20a0\u20cf\u20d0\u20ff\u2100\u214f\u2150\u218f\u2190\u21ff\u2200\u22ff\u2300\u23ff\u2400\u243f\u2440\u245f\u2460\u24ff\u2500\u257f\u2580\u259f\u25a0\u25ff\u2600\u26ff\u2700\u27bf\u2800\u28ff\u2e80\u2eff\u2f00\u2fdf\u2ff0\u2fff\u3000\u303f\u3040\u309f\u30a0\u30ff\u3100\u312f\u3130\u318f\u3190\u319f\u31a0\u31bf\u3200\u32ff\u3300\u33ff\u3400\u4db5\u4e00\u9fff\ua000\ua48f\ua490\ua4cf\uac00\ud7a3\ue000\uf8ff\uf900\ufaff\ufb00\ufb4f\ufb50\ufdff\ufe20\ufe2f\ufe30\ufe4f\ufe50\ufe6f\ufe70\ufefe\ufeff\ufeff\uff00\uffef".charAt(n2 + 1));
                    }
                    else {
                        final int n3 = (l - 84) * 2;
                        range.addRange(Token.nonBMPBlockRanges[n3], Token.nonBMPBlockRanges[n3 + 1]);
                    }
                    final String s2 = Token.blockNames[l];
                    if (s2.equals("Specials")) {
                        range.addRange(65520, 65533);
                    }
                    if (s2.equals("Private Use")) {
                        range.addRange(983040, 1048573);
                        range.addRange(1048576, 1114109);
                    }
                    Token.categories.put(s2, range);
                    Token.categories2.put(s2, complementRanges(range));
                    sb.setLength(0);
                    sb.append("Is");
                    if (s2.indexOf(32) >= 0) {
                        for (int n4 = 0; n4 < s2.length(); ++n4) {
                            if (s2.charAt(n4) != ' ') {
                                sb.append(s2.charAt(n4));
                            }
                        }
                    }
                    else {
                        sb.append(s2);
                    }
                    setAlias(sb.toString(), s2, true);
                }
                setAlias("ASSIGNED", "Cn", false);
                setAlias("UNASSIGNED", "Cn", true);
                final RangeToken range2 = createRange();
                range2.addRange(0, 1114111);
                Token.categories.put("ALL", range2);
                Token.categories2.put("ALL", complementRanges(range2));
                registerNonXS("ASSIGNED");
                registerNonXS("UNASSIGNED");
                registerNonXS("ALL");
                final RangeToken range3 = createRange();
                range3.mergeRanges(array[1]);
                range3.mergeRanges(array[2]);
                range3.mergeRanges(array[5]);
                Token.categories.put("IsAlpha", range3);
                Token.categories2.put("IsAlpha", complementRanges(range3));
                registerNonXS("IsAlpha");
                final RangeToken range4 = createRange();
                range4.mergeRanges(range3);
                range4.mergeRanges(array[9]);
                Token.categories.put("IsAlnum", range4);
                Token.categories2.put("IsAlnum", complementRanges(range4));
                registerNonXS("IsAlnum");
                final RangeToken range5 = createRange();
                range5.mergeRanges(Token.token_spaces);
                range5.mergeRanges(array[34]);
                Token.categories.put("IsSpace", range5);
                Token.categories2.put("IsSpace", complementRanges(range5));
                registerNonXS("IsSpace");
                final RangeToken range6 = createRange();
                range6.mergeRanges(range4);
                range6.addRange(95, 95);
                Token.categories.put("IsWord", range6);
                Token.categories2.put("IsWord", complementRanges(range6));
                registerNonXS("IsWord");
                final RangeToken range7 = createRange();
                range7.addRange(0, 127);
                Token.categories.put("IsASCII", range7);
                Token.categories2.put("IsASCII", complementRanges(range7));
                registerNonXS("IsASCII");
                final RangeToken range8 = createRange();
                range8.mergeRanges(array[35]);
                range8.addRange(32, 32);
                Token.categories.put("IsGraph", complementRanges(range8));
                Token.categories2.put("IsGraph", range8);
                registerNonXS("IsGraph");
                final RangeToken range9 = createRange();
                range9.addRange(48, 57);
                range9.addRange(65, 70);
                range9.addRange(97, 102);
                Token.categories.put("IsXDigit", complementRanges(range9));
                Token.categories2.put("IsXDigit", range9);
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
        return b ? Token.categories.get(s) : Token.categories2.get(s);
    }
    
    protected static RangeToken getRange(final String s, final boolean b, final boolean b2) {
        RangeToken range = getRange(s, b);
        if (b2 && range != null && isRegisterNonXS(s)) {
            range = null;
        }
        return range;
    }
    
    protected static void registerNonXS(final String s) {
        if (Token.nonxs == null) {
            Token.nonxs = new Hashtable();
        }
        Token.nonxs.put(s, s);
    }
    
    protected static boolean isRegisterNonXS(final String s) {
        return Token.nonxs != null && Token.nonxs.containsKey(s);
    }
    
    private static void setAlias(final String s, final String s2, final boolean b) {
        final Token token = Token.categories.get(s2);
        final Token token2 = Token.categories2.get(s2);
        if (b) {
            Token.categories.put(s, token);
            Token.categories2.put(s, token2);
        }
        else {
            Token.categories2.put(s, token);
            Token.categories.put(s, token2);
        }
    }
    
    static synchronized Token getGraphemePattern() {
        if (Token.token_grapheme != null) {
            return Token.token_grapheme;
        }
        final RangeToken range = createRange();
        range.mergeRanges(getRange("ASSIGNED", true));
        range.subtractRanges(getRange("M", true));
        range.subtractRanges(getRange("C", true));
        final RangeToken range2 = createRange();
        for (int i = 0; i < "\u094d\u09cd\u0a4d\u0acd\u0b4d\u0bcd\u0c4d\u0ccd\u0d4d\u0e3a\u0f84".length(); ++i) {
            range2.addRange(i, i);
        }
        final RangeToken range3 = createRange();
        range3.mergeRanges(getRange("M", true));
        range3.addRange(4448, 4607);
        range3.addRange(65438, 65439);
        final UnionToken union = createUnion();
        union.addChild(range);
        union.addChild(Token.token_empty);
        final UnionToken union2 = createUnion();
        union2.addChild(createConcat(range2, getRange("L", true)));
        union2.addChild(range3);
        return Token.token_grapheme = createConcat(union, createClosure(union2));
    }
    
    static synchronized Token getCombiningCharacterSequence() {
        if (Token.token_ccs != null) {
            return Token.token_ccs;
        }
        return Token.token_ccs = createConcat(getRange("M", false), createClosure(getRange("M", true)));
    }
    
    static {
        Token.tokens = 0;
        (Token.token_all = createRange()).addRange(0, 1114111);
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
        categories = new Hashtable();
        categories2 = new Hashtable();
        categoryNames = new String[] { "Cn", "Lu", "Ll", "Lt", "Lm", "Lo", "Mn", "Me", "Mc", "Nd", "Nl", "No", "Zs", "Zl", "Zp", "Cc", "Cf", null, "Co", "Cs", "Pd", "Ps", "Pe", "Pc", "Po", "Sm", "Sc", "Sk", "So", "Pi", "Pf", "L", "M", "N", "Z", "C", "P", "S" };
        blockNames = new String[] { "Basic Latin", "Latin-1 Supplement", "Latin Extended-A", "Latin Extended-B", "IPA Extensions", "Spacing Modifier Letters", "Combining Diacritical Marks", "Greek", "Cyrillic", "Armenian", "Hebrew", "Arabic", "Syriac", "Thaana", "Devanagari", "Bengali", "Gurmukhi", "Gujarati", "Oriya", "Tamil", "Telugu", "Kannada", "Malayalam", "Sinhala", "Thai", "Lao", "Tibetan", "Myanmar", "Georgian", "Hangul Jamo", "Ethiopic", "Cherokee", "Unified Canadian Aboriginal Syllabics", "Ogham", "Runic", "Khmer", "Mongolian", "Latin Extended Additional", "Greek Extended", "General Punctuation", "Superscripts and Subscripts", "Currency Symbols", "Combining Marks for Symbols", "Letterlike Symbols", "Number Forms", "Arrows", "Mathematical Operators", "Miscellaneous Technical", "Control Pictures", "Optical Character Recognition", "Enclosed Alphanumerics", "Box Drawing", "Block Elements", "Geometric Shapes", "Miscellaneous Symbols", "Dingbats", "Braille Patterns", "CJK Radicals Supplement", "Kangxi Radicals", "Ideographic Description Characters", "CJK Symbols and Punctuation", "Hiragana", "Katakana", "Bopomofo", "Hangul Compatibility Jamo", "Kanbun", "Bopomofo Extended", "Enclosed CJK Letters and Months", "CJK Compatibility", "CJK Unified Ideographs Extension A", "CJK Unified Ideographs", "Yi Syllables", "Yi Radicals", "Hangul Syllables", "Private Use", "CJK Compatibility Ideographs", "Alphabetic Presentation Forms", "Arabic Presentation Forms-A", "Combining Half Marks", "CJK Compatibility Forms", "Small Form Variants", "Arabic Presentation Forms-B", "Specials", "Halfwidth and Fullwidth Forms", "Old Italic", "Gothic", "Deseret", "Byzantine Musical Symbols", "Musical Symbols", "Mathematical Alphanumeric Symbols", "CJK Unified Ideographs Extension B", "CJK Compatibility Ideographs Supplement", "Tags" };
        nonBMPBlockRanges = new int[] { 66304, 66351, 66352, 66383, 66560, 66639, 118784, 119039, 119040, 119295, 119808, 120831, 131072, 173782, 194560, 195103, 917504, 917631 };
        Token.nonxs = null;
        Token.token_grapheme = null;
        Token.token_ccs = null;
    }
    
    static class CharToken extends Token implements Serializable
    {
        private static final long serialVersionUID = -4394272816279496989L;
        final int chardata;
        
        CharToken(final int n, final int chardata) {
            super(n);
            this.chardata = chardata;
        }
        
        int getChar() {
            return this.chardata;
        }
        
        public String toString(final int n) {
            String s = null;
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
                                s = "\\" + (char)this.chardata;
                                break Label_0394;
                            }
                            case 12: {
                                s = "\\f";
                                break Label_0394;
                            }
                            case 10: {
                                s = "\\n";
                                break Label_0394;
                            }
                            case 13: {
                                s = "\\r";
                                break Label_0394;
                            }
                            case 9: {
                                s = "\\t";
                                break Label_0394;
                            }
                            case 27: {
                                s = "\\e";
                                break Label_0394;
                            }
                            default: {
                                if (this.chardata >= 65536) {
                                    final String string = "0" + Integer.toHexString(this.chardata);
                                    s = "\\v" + string.substring(string.length() - 6, string.length());
                                    break Label_0394;
                                }
                                s = "" + (char)this.chardata;
                                break Label_0394;
                            }
                        }
                        break;
                    }
                    case 8: {
                        if (this == Token.token_linebeginning || this == Token.token_lineend) {
                            s = "" + (char)this.chardata;
                            break;
                        }
                        s = "\\" + (char)this.chardata;
                        break;
                    }
                    default: {
                        s = null;
                        break;
                    }
                }
            }
            return s;
        }
        
        boolean match(final int n) {
            if (this.type == 0) {
                return n == this.chardata;
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
        
        ClosureToken(final int n, final Token child) {
            super(n);
            this.child = child;
            this.setMin(-1);
            this.setMax(-1);
        }
        
        int size() {
            return 1;
        }
        
        Token getChild(final int n) {
            return this.child;
        }
        
        final void setMin(final int min) {
            this.min = min;
        }
        
        final void setMax(final int max) {
            this.max = max;
        }
        
        final int getMin() {
            return this.min;
        }
        
        final int getMax() {
            return this.max;
        }
        
        public String toString(final int n) {
            String s;
            if (this.type == 3) {
                if (this.getMin() < 0 && this.getMax() < 0) {
                    s = this.child.toString(n) + "*";
                }
                else if (this.getMin() == this.getMax()) {
                    s = this.child.toString(n) + "{" + this.getMin() + "}";
                }
                else if (this.getMin() >= 0 && this.getMax() >= 0) {
                    s = this.child.toString(n) + "{" + this.getMin() + "," + this.getMax() + "}";
                }
                else {
                    if (this.getMin() < 0 || this.getMax() >= 0) {
                        throw new RuntimeException("Token#toString(): CLOSURE " + this.getMin() + ", " + this.getMax());
                    }
                    s = this.child.toString(n) + "{" + this.getMin() + ",}";
                }
            }
            else if (this.getMin() < 0 && this.getMax() < 0) {
                s = this.child.toString(n) + "*?";
            }
            else if (this.getMin() == this.getMax()) {
                s = this.child.toString(n) + "{" + this.getMin() + "}?";
            }
            else if (this.getMin() >= 0 && this.getMax() >= 0) {
                s = this.child.toString(n) + "{" + this.getMin() + "," + this.getMax() + "}?";
            }
            else {
                if (this.getMin() < 0 || this.getMax() >= 0) {
                    throw new RuntimeException("Token#toString(): NONGREEDYCLOSURE " + this.getMin() + ", " + this.getMax());
                }
                s = this.child.toString(n) + "{" + this.getMin() + ",}?";
            }
            return s;
        }
    }
    
    static class ConcatToken extends Token implements Serializable
    {
        private static final long serialVersionUID = 8717321425541346381L;
        final Token child;
        final Token child2;
        
        ConcatToken(final Token child, final Token child2) {
            super(1);
            this.child = child;
            this.child2 = child2;
        }
        
        int size() {
            return 2;
        }
        
        Token getChild(final int n) {
            return (n == 0) ? this.child : this.child2;
        }
        
        public String toString(final int n) {
            String s;
            if (this.child2.type == 3 && this.child2.getChild(0) == this.child) {
                s = this.child.toString(n) + "+";
            }
            else if (this.child2.type == 9 && this.child2.getChild(0) == this.child) {
                s = this.child.toString(n) + "+?";
            }
            else {
                s = this.child.toString(n) + this.child2.toString(n);
            }
            return s;
        }
    }
    
    static class ConditionToken extends Token implements Serializable
    {
        private static final long serialVersionUID = 4353765277910594411L;
        final int refNumber;
        final Token condition;
        final Token yes;
        final Token no;
        
        ConditionToken(final int refNumber, final Token condition, final Token yes, final Token no) {
            super(26);
            this.refNumber = refNumber;
            this.condition = condition;
            this.yes = yes;
            this.no = no;
        }
        
        int size() {
            return (this.no == null) ? 1 : 2;
        }
        
        Token getChild(final int n) {
            if (n == 0) {
                return this.yes;
            }
            if (n == 1) {
                return this.no;
            }
            throw new RuntimeException("Internal Error: " + n);
        }
        
        public String toString(final int n) {
            String s;
            if (this.refNumber > 0) {
                s = "(?(" + this.refNumber + ")";
            }
            else if (this.condition.type == 8) {
                s = "(?(" + this.condition + ")";
            }
            else {
                s = "(?" + this.condition;
            }
            String s2;
            if (this.no == null) {
                s2 = s + this.yes + ")";
            }
            else {
                s2 = s + this.yes + "|" + this.no + ")";
            }
            return s2;
        }
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
    
    static class ModifierToken extends Token implements Serializable
    {
        private static final long serialVersionUID = -9114536559696480356L;
        final Token child;
        final int add;
        final int mask;
        
        ModifierToken(final Token child, final int add, final int mask) {
            super(25);
            this.child = child;
            this.add = add;
            this.mask = mask;
        }
        
        int size() {
            return 1;
        }
        
        Token getChild(final int n) {
            return this.child;
        }
        
        int getOptions() {
            return this.add;
        }
        
        int getOptionsMask() {
            return this.mask;
        }
        
        public String toString(final int n) {
            return "(?" + ((this.add == 0) ? "" : REUtil.createOptionString(this.add)) + ((this.mask == 0) ? "" : REUtil.createOptionString(this.mask)) + ":" + this.child.toString(n) + ")";
        }
    }
    
    static class ParenToken extends Token implements Serializable
    {
        private static final long serialVersionUID = -5938014719827987704L;
        final Token child;
        final int parennumber;
        
        ParenToken(final int n, final Token child, final int parennumber) {
            super(n);
            this.child = child;
            this.parennumber = parennumber;
        }
        
        int size() {
            return 1;
        }
        
        Token getChild(final int n) {
            return this.child;
        }
        
        int getParenNumber() {
            return this.parennumber;
        }
        
        public String toString(final int n) {
            String s = null;
            switch (this.type) {
                case 6: {
                    if (this.parennumber == 0) {
                        s = "(?:" + this.child.toString(n) + ")";
                        break;
                    }
                    s = "(" + this.child.toString(n) + ")";
                    break;
                }
                case 20: {
                    s = "(?=" + this.child.toString(n) + ")";
                    break;
                }
                case 21: {
                    s = "(?!" + this.child.toString(n) + ")";
                    break;
                }
                case 22: {
                    s = "(?<=" + this.child.toString(n) + ")";
                    break;
                }
                case 23: {
                    s = "(?<!" + this.child.toString(n) + ")";
                    break;
                }
                case 24: {
                    s = "(?>" + this.child.toString(n) + ")";
                    break;
                }
            }
            return s;
        }
    }
    
    static class StringToken extends Token implements Serializable
    {
        private static final long serialVersionUID = -4614366944218504172L;
        String string;
        final int refNumber;
        
        StringToken(final int n, final String string, final int refNumber) {
            super(n);
            this.string = string;
            this.refNumber = refNumber;
        }
        
        int getReferenceNumber() {
            return this.refNumber;
        }
        
        String getString() {
            return this.string;
        }
        
        public String toString(final int n) {
            if (this.type == 12) {
                return "\\" + this.refNumber;
            }
            return REUtil.quoteMeta(this.string);
        }
    }
    
    static class UnionToken extends Token implements Serializable
    {
        private static final long serialVersionUID = -2568843945989489861L;
        Vector children;
        
        UnionToken(final int n) {
            super(n);
        }
        
        void addChild(final Token token) {
            if (token == null) {
                return;
            }
            if (this.children == null) {
                this.children = new Vector();
            }
            if (this.type == 2) {
                this.children.addElement(token);
                return;
            }
            if (token.type == 1) {
                for (int i = 0; i < token.size(); ++i) {
                    this.addChild(token.getChild(i));
                }
                return;
            }
            final int size = this.children.size();
            if (size == 0) {
                this.children.addElement(token);
                return;
            }
            Token string = this.children.elementAt(size - 1);
            if ((string.type != 0 && string.type != 10) || (token.type != 0 && token.type != 10)) {
                this.children.addElement(token);
                return;
            }
            final int n = (token.type == 0) ? 2 : token.getString().length();
            StringBuffer sb;
            if (string.type == 0) {
                sb = new StringBuffer(2 + n);
                final int char1 = string.getChar();
                if (char1 >= 65536) {
                    sb.append(REUtil.decomposeToSurrogates(char1));
                }
                else {
                    sb.append((char)char1);
                }
                string = Token.createString(null);
                this.children.setElementAt(string, size - 1);
            }
            else {
                sb = new StringBuffer(string.getString().length() + n);
                sb.append(string.getString());
            }
            if (token.type == 0) {
                final int char2 = token.getChar();
                if (char2 >= 65536) {
                    sb.append(REUtil.decomposeToSurrogates(char2));
                }
                else {
                    sb.append((char)char2);
                }
            }
            else {
                sb.append(token.getString());
            }
            ((StringToken)string).string = new String(sb);
        }
        
        int size() {
            return (this.children == null) ? 0 : this.children.size();
        }
        
        Token getChild(final int n) {
            return this.children.elementAt(n);
        }
        
        public String toString(final int n) {
            if (this.type == 1) {
                String s;
                if (this.children.size() == 2) {
                    final Token child = this.getChild(0);
                    final Token child2 = this.getChild(1);
                    if (child2.type == 3 && child2.getChild(0) == child) {
                        s = child.toString(n) + "+";
                    }
                    else if (child2.type == 9 && child2.getChild(0) == child) {
                        s = child.toString(n) + "+?";
                    }
                    else {
                        s = child.toString(n) + child2.toString(n);
                    }
                }
                else {
                    final StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < this.children.size(); ++i) {
                        sb.append(((Token)this.children.elementAt(i)).toString(n));
                    }
                    s = new String(sb);
                }
                return s;
            }
            String s2;
            if (this.children.size() == 2 && this.getChild(1).type == 7) {
                s2 = this.getChild(0).toString(n) + "?";
            }
            else if (this.children.size() == 2 && this.getChild(0).type == 7) {
                s2 = this.getChild(1).toString(n) + "??";
            }
            else {
                final StringBuffer sb2 = new StringBuffer();
                sb2.append(this.children.elementAt(0).toString(n));
                for (int j = 1; j < this.children.size(); ++j) {
                    sb2.append('|');
                    sb2.append(((Token)this.children.elementAt(j)).toString(n));
                }
                s2 = new String(sb2);
            }
            return s2;
        }
    }
}
