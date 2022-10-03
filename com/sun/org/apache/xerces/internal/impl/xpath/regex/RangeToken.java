package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.io.Serializable;

final class RangeToken extends Token implements Serializable
{
    private static final long serialVersionUID = 3257568399592010545L;
    int[] ranges;
    boolean sorted;
    boolean compacted;
    RangeToken icaseCache;
    int[] map;
    int nonMapIndex;
    private static final int MAPSIZE = 256;
    
    RangeToken(final int type) {
        super(type);
        this.icaseCache = null;
        this.map = null;
        this.setSorted(false);
    }
    
    @Override
    protected void addRange(final int start, final int end) {
        this.icaseCache = null;
        int r1;
        int r2;
        if (start <= end) {
            r1 = start;
            r2 = end;
        }
        else {
            r1 = end;
            r2 = start;
        }
        int pos = 0;
        if (this.ranges == null) {
            (this.ranges = new int[2])[0] = r1;
            this.ranges[1] = r2;
            this.setSorted(true);
        }
        else {
            pos = this.ranges.length;
            if (this.ranges[pos - 1] + 1 == r1) {
                this.ranges[pos - 1] = r2;
                return;
            }
            final int[] temp = new int[pos + 2];
            System.arraycopy(this.ranges, 0, temp, 0, pos);
            this.ranges = temp;
            if (this.ranges[pos - 1] >= r1) {
                this.setSorted(false);
            }
            this.ranges[pos++] = r1;
            this.ranges[pos] = r2;
            if (!this.sorted) {
                this.sortRanges();
            }
        }
    }
    
    private final boolean isSorted() {
        return this.sorted;
    }
    
    private final void setSorted(final boolean sort) {
        if (!(this.sorted = sort)) {
            this.compacted = false;
        }
    }
    
    private final boolean isCompacted() {
        return this.compacted;
    }
    
    private final void setCompacted() {
        this.compacted = true;
    }
    
    @Override
    protected void sortRanges() {
        if (this.isSorted()) {
            return;
        }
        if (this.ranges == null) {
            return;
        }
        for (int i = this.ranges.length - 4; i >= 0; i -= 2) {
            for (int j = 0; j <= i; j += 2) {
                if (this.ranges[j] > this.ranges[j + 2] || (this.ranges[j] == this.ranges[j + 2] && this.ranges[j + 1] > this.ranges[j + 3])) {
                    int tmp = this.ranges[j + 2];
                    this.ranges[j + 2] = this.ranges[j];
                    this.ranges[j] = tmp;
                    tmp = this.ranges[j + 3];
                    this.ranges[j + 3] = this.ranges[j + 1];
                    this.ranges[j + 1] = tmp;
                }
            }
        }
        this.setSorted(true);
    }
    
    @Override
    protected void compactRanges() {
        final boolean DEBUG = false;
        if (this.ranges == null || this.ranges.length <= 2) {
            return;
        }
        if (this.isCompacted()) {
            return;
        }
        int base = 0;
        int target = 0;
        while (target < this.ranges.length) {
            if (base != target) {
                this.ranges[base] = this.ranges[target++];
                this.ranges[base + 1] = this.ranges[target++];
            }
            else {
                target += 2;
            }
            for (int baseend = this.ranges[base + 1]; target < this.ranges.length && baseend + 1 >= this.ranges[target]; target += 2) {
                if (baseend + 1 == this.ranges[target]) {
                    if (DEBUG) {
                        System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[base] + ", " + this.ranges[base + 1] + "], [" + this.ranges[target] + ", " + this.ranges[target + 1] + "] -> [" + this.ranges[base] + ", " + this.ranges[target + 1] + "]");
                    }
                    this.ranges[base + 1] = this.ranges[target + 1];
                    baseend = this.ranges[base + 1];
                }
                else if (baseend >= this.ranges[target + 1]) {
                    if (DEBUG) {
                        System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[base] + ", " + this.ranges[base + 1] + "], [" + this.ranges[target] + ", " + this.ranges[target + 1] + "] -> [" + this.ranges[base] + ", " + this.ranges[base + 1] + "]");
                    }
                }
                else {
                    if (baseend >= this.ranges[target + 1]) {
                        throw new RuntimeException("Token#compactRanges(): Internel Error: [" + this.ranges[base] + "," + this.ranges[base + 1] + "] [" + this.ranges[target] + "," + this.ranges[target + 1] + "]");
                    }
                    if (DEBUG) {
                        System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[base] + ", " + this.ranges[base + 1] + "], [" + this.ranges[target] + ", " + this.ranges[target + 1] + "] -> [" + this.ranges[base] + ", " + this.ranges[target + 1] + "]");
                    }
                    this.ranges[base + 1] = this.ranges[target + 1];
                    baseend = this.ranges[base + 1];
                }
            }
            base += 2;
        }
        if (base != this.ranges.length) {
            final int[] result = new int[base];
            System.arraycopy(this.ranges, 0, result, 0, base);
            this.ranges = result;
        }
        this.setCompacted();
    }
    
    @Override
    protected void mergeRanges(final Token token) {
        final RangeToken tok = (RangeToken)token;
        this.sortRanges();
        tok.sortRanges();
        if (tok.ranges == null) {
            return;
        }
        this.icaseCache = null;
        this.setSorted(true);
        if (this.ranges == null) {
            this.ranges = new int[tok.ranges.length];
            System.arraycopy(tok.ranges, 0, this.ranges, 0, tok.ranges.length);
            return;
        }
        final int[] result = new int[this.ranges.length + tok.ranges.length];
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < this.ranges.length || j < tok.ranges.length) {
            if (i >= this.ranges.length) {
                result[k++] = tok.ranges[j++];
                result[k++] = tok.ranges[j++];
            }
            else if (j >= tok.ranges.length) {
                result[k++] = this.ranges[i++];
                result[k++] = this.ranges[i++];
            }
            else if (tok.ranges[j] < this.ranges[i] || (tok.ranges[j] == this.ranges[i] && tok.ranges[j + 1] < this.ranges[i + 1])) {
                result[k++] = tok.ranges[j++];
                result[k++] = tok.ranges[j++];
            }
            else {
                result[k++] = this.ranges[i++];
                result[k++] = this.ranges[i++];
            }
        }
        this.ranges = result;
    }
    
    @Override
    protected void subtractRanges(final Token token) {
        if (token.type == 5) {
            this.intersectRanges(token);
            return;
        }
        final RangeToken tok = (RangeToken)token;
        if (tok.ranges == null || this.ranges == null) {
            return;
        }
        this.icaseCache = null;
        this.sortRanges();
        this.compactRanges();
        tok.sortRanges();
        tok.compactRanges();
        final int[] result = new int[this.ranges.length + tok.ranges.length];
        int wp = 0;
        int src = 0;
        int sub = 0;
        while (src < this.ranges.length && sub < tok.ranges.length) {
            final int srcbegin = this.ranges[src];
            final int srcend = this.ranges[src + 1];
            final int subbegin = tok.ranges[sub];
            final int subend = tok.ranges[sub + 1];
            if (srcend < subbegin) {
                result[wp++] = this.ranges[src++];
                result[wp++] = this.ranges[src++];
            }
            else if (srcend >= subbegin && srcbegin <= subend) {
                if (subbegin <= srcbegin && srcend <= subend) {
                    src += 2;
                }
                else if (subbegin <= srcbegin) {
                    this.ranges[src] = subend + 1;
                    sub += 2;
                }
                else if (srcend <= subend) {
                    result[wp++] = srcbegin;
                    result[wp++] = subbegin - 1;
                    src += 2;
                }
                else {
                    result[wp++] = srcbegin;
                    result[wp++] = subbegin - 1;
                    this.ranges[src] = subend + 1;
                    sub += 2;
                }
            }
            else {
                if (subend >= srcbegin) {
                    throw new RuntimeException("Token#subtractRanges(): Internal Error: [" + this.ranges[src] + "," + this.ranges[src + 1] + "] - [" + tok.ranges[sub] + "," + tok.ranges[sub + 1] + "]");
                }
                sub += 2;
            }
        }
        while (src < this.ranges.length) {
            result[wp++] = this.ranges[src++];
            result[wp++] = this.ranges[src++];
        }
        System.arraycopy(result, 0, this.ranges = new int[wp], 0, wp);
    }
    
    @Override
    protected void intersectRanges(final Token token) {
        final RangeToken tok = (RangeToken)token;
        if (tok.ranges == null || this.ranges == null) {
            return;
        }
        this.icaseCache = null;
        this.sortRanges();
        this.compactRanges();
        tok.sortRanges();
        tok.compactRanges();
        final int[] result = new int[this.ranges.length + tok.ranges.length];
        int wp = 0;
        int src1 = 0;
        int src2 = 0;
        while (src1 < this.ranges.length && src2 < tok.ranges.length) {
            final int src1begin = this.ranges[src1];
            final int src1end = this.ranges[src1 + 1];
            final int src2begin = tok.ranges[src2];
            final int src2end = tok.ranges[src2 + 1];
            if (src1end < src2begin) {
                src1 += 2;
            }
            else if (src1end >= src2begin && src1begin <= src2end) {
                if (src2begin <= src2begin && src1end <= src2end) {
                    result[wp++] = src1begin;
                    result[wp++] = src1end;
                    src1 += 2;
                }
                else if (src2begin <= src1begin) {
                    result[wp++] = src1begin;
                    result[wp++] = src2end;
                    this.ranges[src1] = src2end + 1;
                    src2 += 2;
                }
                else if (src1end <= src2end) {
                    result[wp++] = src2begin;
                    result[wp++] = src1end;
                    src1 += 2;
                }
                else {
                    result[wp++] = src2begin;
                    result[wp++] = src2end;
                    this.ranges[src1] = src2end + 1;
                }
            }
            else {
                if (src2end >= src1begin) {
                    throw new RuntimeException("Token#intersectRanges(): Internal Error: [" + this.ranges[src1] + "," + this.ranges[src1 + 1] + "] & [" + tok.ranges[src2] + "," + tok.ranges[src2 + 1] + "]");
                }
                src2 += 2;
            }
        }
        while (src1 < this.ranges.length) {
            result[wp++] = this.ranges[src1++];
            result[wp++] = this.ranges[src1++];
        }
        System.arraycopy(result, 0, this.ranges = new int[wp], 0, wp);
    }
    
    static Token complementRanges(final Token token) {
        if (token.type != 4 && token.type != 5) {
            throw new IllegalArgumentException("Token#complementRanges(): must be RANGE: " + token.type);
        }
        final RangeToken tok = (RangeToken)token;
        tok.sortRanges();
        tok.compactRanges();
        int len = tok.ranges.length + 2;
        if (tok.ranges[0] == 0) {
            len -= 2;
        }
        final int last = tok.ranges[tok.ranges.length - 1];
        if (last == 1114111) {
            len -= 2;
        }
        final RangeToken ret = Token.createRange();
        ret.ranges = new int[len];
        int wp = 0;
        if (tok.ranges[0] > 0) {
            ret.ranges[wp++] = 0;
            ret.ranges[wp++] = tok.ranges[0] - 1;
        }
        for (int i = 1; i < tok.ranges.length - 2; i += 2) {
            ret.ranges[wp++] = tok.ranges[i] + 1;
            ret.ranges[wp++] = tok.ranges[i + 1] - 1;
        }
        if (last != 1114111) {
            ret.ranges[wp++] = last + 1;
            ret.ranges[wp] = 1114111;
        }
        ret.setCompacted();
        return ret;
    }
    
    synchronized RangeToken getCaseInsensitiveToken() {
        if (this.icaseCache != null) {
            return this.icaseCache;
        }
        final RangeToken uppers = (this.type == 4) ? Token.createRange() : Token.createNRange();
        for (int i = 0; i < this.ranges.length; i += 2) {
            for (int ch = this.ranges[i]; ch <= this.ranges[i + 1]; ++ch) {
                if (ch > 65535) {
                    uppers.addRange(ch, ch);
                }
                else {
                    final char uch = Character.toUpperCase((char)ch);
                    uppers.addRange(uch, uch);
                }
            }
        }
        final RangeToken lowers = (this.type == 4) ? Token.createRange() : Token.createNRange();
        for (int j = 0; j < uppers.ranges.length; j += 2) {
            for (int ch2 = uppers.ranges[j]; ch2 <= uppers.ranges[j + 1]; ++ch2) {
                if (ch2 > 65535) {
                    lowers.addRange(ch2, ch2);
                }
                else {
                    final char uch2 = Character.toUpperCase((char)ch2);
                    lowers.addRange(uch2, uch2);
                }
            }
        }
        lowers.mergeRanges(uppers);
        lowers.mergeRanges(this);
        lowers.compactRanges();
        return this.icaseCache = lowers;
    }
    
    void dumpRanges() {
        System.err.print("RANGE: ");
        if (this.ranges == null) {
            System.err.println(" NULL");
        }
        for (int i = 0; i < this.ranges.length; i += 2) {
            System.err.print("[" + this.ranges[i] + "," + this.ranges[i + 1] + "] ");
        }
        System.err.println("");
    }
    
    @Override
    boolean match(final int ch) {
        if (this.map == null) {
            this.createMap();
        }
        boolean ret;
        if (this.type == 4) {
            if (ch < 256) {
                return (this.map[ch / 32] & 1 << (ch & 0x1F)) != 0x0;
            }
            ret = false;
            for (int i = this.nonMapIndex; i < this.ranges.length; i += 2) {
                if (this.ranges[i] <= ch && ch <= this.ranges[i + 1]) {
                    return true;
                }
            }
        }
        else {
            if (ch < 256) {
                return (this.map[ch / 32] & 1 << (ch & 0x1F)) == 0x0;
            }
            ret = true;
            for (int i = this.nonMapIndex; i < this.ranges.length; i += 2) {
                if (this.ranges[i] <= ch && ch <= this.ranges[i + 1]) {
                    return false;
                }
            }
        }
        return ret;
    }
    
    private void createMap() {
        final int asize = 8;
        final int[] map = new int[asize];
        int nonMapIndex = this.ranges.length;
        for (int i = 0; i < asize; ++i) {
            map[i] = 0;
        }
        for (int i = 0; i < this.ranges.length; i += 2) {
            final int s = this.ranges[i];
            final int e = this.ranges[i + 1];
            if (s >= 256) {
                nonMapIndex = i;
                break;
            }
            for (int j = s; j <= e && j < 256; ++j) {
                final int[] array = map;
                final int n = j / 32;
                array[n] |= 1 << (j & 0x1F);
            }
            if (e >= 256) {
                nonMapIndex = i;
                break;
            }
        }
        this.map = map;
        this.nonMapIndex = nonMapIndex;
    }
    
    @Override
    public String toString(final int options) {
        String ret;
        if (this.type == 4) {
            if (this == Token.token_dot) {
                ret = ".";
            }
            else if (this == Token.token_0to9) {
                ret = "\\d";
            }
            else if (this == Token.token_wordchars) {
                ret = "\\w";
            }
            else if (this == Token.token_spaces) {
                ret = "\\s";
            }
            else {
                final StringBuffer sb = new StringBuffer();
                sb.append("[");
                for (int i = 0; i < this.ranges.length; i += 2) {
                    if ((options & 0x400) != 0x0 && i > 0) {
                        sb.append(",");
                    }
                    if (this.ranges[i] == this.ranges[i + 1]) {
                        sb.append(escapeCharInCharClass(this.ranges[i]));
                    }
                    else {
                        sb.append(escapeCharInCharClass(this.ranges[i]));
                        sb.append('-');
                        sb.append(escapeCharInCharClass(this.ranges[i + 1]));
                    }
                }
                sb.append("]");
                ret = sb.toString();
            }
        }
        else if (this == Token.token_not_0to9) {
            ret = "\\D";
        }
        else if (this == Token.token_not_wordchars) {
            ret = "\\W";
        }
        else if (this == Token.token_not_spaces) {
            ret = "\\S";
        }
        else {
            final StringBuffer sb = new StringBuffer();
            sb.append("[^");
            for (int i = 0; i < this.ranges.length; i += 2) {
                if ((options & 0x400) != 0x0 && i > 0) {
                    sb.append(",");
                }
                if (this.ranges[i] == this.ranges[i + 1]) {
                    sb.append(escapeCharInCharClass(this.ranges[i]));
                }
                else {
                    sb.append(escapeCharInCharClass(this.ranges[i]));
                    sb.append('-');
                    sb.append(escapeCharInCharClass(this.ranges[i + 1]));
                }
            }
            sb.append("]");
            ret = sb.toString();
        }
        return ret;
    }
    
    private static String escapeCharInCharClass(final int ch) {
        String ret = null;
        switch (ch) {
            case 44:
            case 45:
            case 91:
            case 92:
            case 93:
            case 94: {
                ret = "\\" + (char)ch;
                break;
            }
            case 12: {
                ret = "\\f";
                break;
            }
            case 10: {
                ret = "\\n";
                break;
            }
            case 13: {
                ret = "\\r";
                break;
            }
            case 9: {
                ret = "\\t";
                break;
            }
            case 27: {
                ret = "\\e";
                break;
            }
            default: {
                if (ch < 32) {
                    final String pre = "0" + Integer.toHexString(ch);
                    ret = "\\x" + pre.substring(pre.length() - 2, pre.length());
                    break;
                }
                if (ch >= 65536) {
                    final String pre = "0" + Integer.toHexString(ch);
                    ret = "\\v" + pre.substring(pre.length() - 6, pre.length());
                    break;
                }
                ret = "" + (char)ch;
                break;
            }
        }
        return ret;
    }
}
