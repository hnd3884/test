package org.apache.xerces.impl.xpath.regex;

import java.io.Serializable;

final class RangeToken extends Token implements Serializable
{
    private static final long serialVersionUID = -553983121197679934L;
    int[] ranges;
    boolean sorted;
    boolean compacted;
    RangeToken icaseCache;
    int[] map;
    int nonMapIndex;
    private static final int MAPSIZE = 256;
    
    RangeToken(final int n) {
        super(n);
        this.icaseCache = null;
        this.map = null;
        this.setSorted(false);
    }
    
    protected void addRange(final int n, final int n2) {
        this.icaseCache = null;
        int n3;
        int n4;
        if (n <= n2) {
            n3 = n;
            n4 = n2;
        }
        else {
            n3 = n2;
            n4 = n;
        }
        if (this.ranges == null) {
            (this.ranges = new int[2])[0] = n3;
            this.ranges[1] = n4;
            this.setSorted(true);
        }
        else {
            int length = this.ranges.length;
            if (this.ranges[length - 1] + 1 == n3) {
                this.ranges[length - 1] = n4;
                return;
            }
            final int[] ranges = new int[length + 2];
            System.arraycopy(this.ranges, 0, ranges, 0, length);
            this.ranges = ranges;
            if (this.ranges[length - 1] >= n3) {
                this.setSorted(false);
            }
            this.ranges[length++] = n3;
            this.ranges[length] = n4;
            if (!this.sorted) {
                this.sortRanges();
            }
        }
    }
    
    private final boolean isSorted() {
        return this.sorted;
    }
    
    private final void setSorted(final boolean sorted) {
        if (!(this.sorted = sorted)) {
            this.compacted = false;
        }
    }
    
    private final boolean isCompacted() {
        return this.compacted;
    }
    
    private final void setCompacted() {
        this.compacted = true;
    }
    
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
                    final int n = this.ranges[j + 2];
                    this.ranges[j + 2] = this.ranges[j];
                    this.ranges[j] = n;
                    final int n2 = this.ranges[j + 3];
                    this.ranges[j + 3] = this.ranges[j + 1];
                    this.ranges[j + 1] = n2;
                }
            }
        }
        this.setSorted(true);
    }
    
    protected void compactRanges() {
        final boolean b = false;
        if (this.ranges == null || this.ranges.length <= 2) {
            return;
        }
        if (this.isCompacted()) {
            return;
        }
        int n = 0;
        int i = 0;
        while (i < this.ranges.length) {
            if (n != i) {
                this.ranges[n] = this.ranges[i++];
                this.ranges[n + 1] = this.ranges[i++];
            }
            else {
                i += 2;
            }
            for (int n2 = this.ranges[n + 1]; i < this.ranges.length && n2 + 1 >= this.ranges[i]; i += 2) {
                if (n2 + 1 == this.ranges[i]) {
                    if (b) {
                        System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[n] + ", " + this.ranges[n + 1] + "], [" + this.ranges[i] + ", " + this.ranges[i + 1] + "] -> [" + this.ranges[n] + ", " + this.ranges[i + 1] + "]");
                    }
                    this.ranges[n + 1] = this.ranges[i + 1];
                    n2 = this.ranges[n + 1];
                }
                else if (n2 >= this.ranges[i + 1]) {
                    if (b) {
                        System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[n] + ", " + this.ranges[n + 1] + "], [" + this.ranges[i] + ", " + this.ranges[i + 1] + "] -> [" + this.ranges[n] + ", " + this.ranges[n + 1] + "]");
                    }
                }
                else {
                    if (n2 >= this.ranges[i + 1]) {
                        throw new RuntimeException("Token#compactRanges(): Internel Error: [" + this.ranges[n] + "," + this.ranges[n + 1] + "] [" + this.ranges[i] + "," + this.ranges[i + 1] + "]");
                    }
                    if (b) {
                        System.err.println("Token#compactRanges(): Compaction: [" + this.ranges[n] + ", " + this.ranges[n + 1] + "], [" + this.ranges[i] + ", " + this.ranges[i + 1] + "] -> [" + this.ranges[n] + ", " + this.ranges[i + 1] + "]");
                    }
                    this.ranges[n + 1] = this.ranges[i + 1];
                    n2 = this.ranges[n + 1];
                }
            }
            n += 2;
        }
        if (n != this.ranges.length) {
            final int[] ranges = new int[n];
            System.arraycopy(this.ranges, 0, ranges, 0, n);
            this.ranges = ranges;
        }
        this.setCompacted();
    }
    
    protected void mergeRanges(final Token token) {
        final RangeToken rangeToken = (RangeToken)token;
        this.sortRanges();
        rangeToken.sortRanges();
        if (rangeToken.ranges == null) {
            return;
        }
        this.icaseCache = null;
        this.setSorted(true);
        if (this.ranges == null) {
            this.ranges = new int[rangeToken.ranges.length];
            System.arraycopy(rangeToken.ranges, 0, this.ranges, 0, rangeToken.ranges.length);
            return;
        }
        final int[] ranges = new int[this.ranges.length + rangeToken.ranges.length];
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        while (n < this.ranges.length || n2 < rangeToken.ranges.length) {
            if (n >= this.ranges.length) {
                ranges[n3++] = rangeToken.ranges[n2++];
                ranges[n3++] = rangeToken.ranges[n2++];
            }
            else if (n2 >= rangeToken.ranges.length) {
                ranges[n3++] = this.ranges[n++];
                ranges[n3++] = this.ranges[n++];
            }
            else if (rangeToken.ranges[n2] < this.ranges[n] || (rangeToken.ranges[n2] == this.ranges[n] && rangeToken.ranges[n2 + 1] < this.ranges[n + 1])) {
                ranges[n3++] = rangeToken.ranges[n2++];
                ranges[n3++] = rangeToken.ranges[n2++];
            }
            else {
                ranges[n3++] = this.ranges[n++];
                ranges[n3++] = this.ranges[n++];
            }
        }
        this.ranges = ranges;
    }
    
    protected void subtractRanges(final Token token) {
        if (token.type == 5) {
            this.intersectRanges(token);
            return;
        }
        final RangeToken rangeToken = (RangeToken)token;
        if (rangeToken.ranges == null || this.ranges == null) {
            return;
        }
        this.icaseCache = null;
        this.sortRanges();
        this.compactRanges();
        rangeToken.sortRanges();
        rangeToken.compactRanges();
        final int[] array = new int[this.ranges.length + rangeToken.ranges.length];
        int n = 0;
        int i = 0;
        int n2 = 0;
        while (i < this.ranges.length && n2 < rangeToken.ranges.length) {
            final int n3 = this.ranges[i];
            final int n4 = this.ranges[i + 1];
            final int n5 = rangeToken.ranges[n2];
            final int n6 = rangeToken.ranges[n2 + 1];
            if (n4 < n5) {
                array[n++] = this.ranges[i++];
                array[n++] = this.ranges[i++];
            }
            else if (n4 >= n5 && n3 <= n6) {
                if (n5 <= n3 && n4 <= n6) {
                    i += 2;
                }
                else if (n5 <= n3) {
                    this.ranges[i] = n6 + 1;
                    n2 += 2;
                }
                else if (n4 <= n6) {
                    array[n++] = n3;
                    array[n++] = n5 - 1;
                    i += 2;
                }
                else {
                    array[n++] = n3;
                    array[n++] = n5 - 1;
                    this.ranges[i] = n6 + 1;
                    n2 += 2;
                }
            }
            else {
                if (n6 >= n3) {
                    throw new RuntimeException("Token#subtractRanges(): Internal Error: [" + this.ranges[i] + "," + this.ranges[i + 1] + "] - [" + rangeToken.ranges[n2] + "," + rangeToken.ranges[n2 + 1] + "]");
                }
                n2 += 2;
            }
        }
        while (i < this.ranges.length) {
            array[n++] = this.ranges[i++];
            array[n++] = this.ranges[i++];
        }
        System.arraycopy(array, 0, this.ranges = new int[n], 0, n);
    }
    
    protected void intersectRanges(final Token token) {
        final RangeToken rangeToken = (RangeToken)token;
        if (rangeToken.ranges == null || this.ranges == null) {
            return;
        }
        this.icaseCache = null;
        this.sortRanges();
        this.compactRanges();
        rangeToken.sortRanges();
        rangeToken.compactRanges();
        final int[] array = new int[this.ranges.length + rangeToken.ranges.length];
        int n = 0;
        int i = 0;
        int n2 = 0;
        while (i < this.ranges.length && n2 < rangeToken.ranges.length) {
            final int n3 = this.ranges[i];
            final int n4 = this.ranges[i + 1];
            final int n5 = rangeToken.ranges[n2];
            final int n6 = rangeToken.ranges[n2 + 1];
            if (n4 < n5) {
                i += 2;
            }
            else if (n4 >= n5 && n3 <= n6) {
                if (n5 <= n3 && n4 <= n6) {
                    array[n++] = n3;
                    array[n++] = n4;
                    i += 2;
                }
                else if (n5 <= n3) {
                    array[n++] = n3;
                    array[n++] = n6;
                    this.ranges[i] = n6 + 1;
                    n2 += 2;
                }
                else if (n4 <= n6) {
                    array[n++] = n5;
                    array[n++] = n4;
                    i += 2;
                }
                else {
                    array[n++] = n5;
                    array[n++] = n6;
                    this.ranges[i] = n6 + 1;
                }
            }
            else {
                if (n6 >= n3) {
                    throw new RuntimeException("Token#intersectRanges(): Internal Error: [" + this.ranges[i] + "," + this.ranges[i + 1] + "] & [" + rangeToken.ranges[n2] + "," + rangeToken.ranges[n2 + 1] + "]");
                }
                n2 += 2;
            }
        }
        while (i < this.ranges.length) {
            array[n++] = this.ranges[i++];
            array[n++] = this.ranges[i++];
        }
        System.arraycopy(array, 0, this.ranges = new int[n], 0, n);
    }
    
    static Token complementRanges(final Token token) {
        if (token.type != 4 && token.type != 5) {
            throw new IllegalArgumentException("Token#complementRanges(): must be RANGE: " + token.type);
        }
        final RangeToken rangeToken = (RangeToken)token;
        rangeToken.sortRanges();
        rangeToken.compactRanges();
        int n = rangeToken.ranges.length + 2;
        if (rangeToken.ranges[0] == 0) {
            n -= 2;
        }
        final int n2 = rangeToken.ranges[rangeToken.ranges.length - 1];
        if (n2 == 1114111) {
            n -= 2;
        }
        final RangeToken range = Token.createRange();
        range.ranges = new int[n];
        int n3 = 0;
        if (rangeToken.ranges[0] > 0) {
            range.ranges[n3++] = 0;
            range.ranges[n3++] = rangeToken.ranges[0] - 1;
        }
        for (int i = 1; i < rangeToken.ranges.length - 2; i += 2) {
            range.ranges[n3++] = rangeToken.ranges[i] + 1;
            range.ranges[n3++] = rangeToken.ranges[i + 1] - 1;
        }
        if (n2 != 1114111) {
            range.ranges[n3++] = n2 + 1;
            range.ranges[n3] = 1114111;
        }
        range.setCompacted();
        return range;
    }
    
    synchronized RangeToken getCaseInsensitiveToken() {
        if (this.icaseCache != null) {
            return this.icaseCache;
        }
        final RangeToken rangeToken = (this.type == 4) ? Token.createRange() : Token.createNRange();
        for (int i = 0; i < this.ranges.length; i += 2) {
            for (int j = this.ranges[i]; j <= this.ranges[i + 1]; ++j) {
                if (j > 65535) {
                    rangeToken.addRange(j, j);
                }
                else {
                    final char upperCase = Character.toUpperCase((char)j);
                    rangeToken.addRange(upperCase, upperCase);
                }
            }
        }
        final RangeToken icaseCache = (this.type == 4) ? Token.createRange() : Token.createNRange();
        for (int k = 0; k < rangeToken.ranges.length; k += 2) {
            for (int l = rangeToken.ranges[k]; l <= rangeToken.ranges[k + 1]; ++l) {
                if (l > 65535) {
                    icaseCache.addRange(l, l);
                }
                else {
                    final char lowerCase = Character.toLowerCase((char)l);
                    icaseCache.addRange(lowerCase, lowerCase);
                }
            }
        }
        icaseCache.mergeRanges(rangeToken);
        icaseCache.mergeRanges(this);
        icaseCache.compactRanges();
        return this.icaseCache = icaseCache;
    }
    
    void dumpRanges() {
        System.err.print("RANGE: ");
        if (this.ranges == null) {
            System.err.println(" NULL");
            return;
        }
        for (int i = 0; i < this.ranges.length; i += 2) {
            System.err.print("[" + this.ranges[i] + "," + this.ranges[i + 1] + "] ");
        }
        System.err.println("");
    }
    
    boolean match(final int n) {
        if (this.map == null) {
            this.createMap();
        }
        boolean b;
        if (this.type == 4) {
            if (n < 256) {
                return (this.map[n / 32] & 1 << (n & 0x1F)) != 0x0;
            }
            b = false;
            for (int i = this.nonMapIndex; i < this.ranges.length; i += 2) {
                if (this.ranges[i] <= n && n <= this.ranges[i + 1]) {
                    return true;
                }
            }
        }
        else {
            if (n < 256) {
                return (this.map[n / 32] & 1 << (n & 0x1F)) == 0x0;
            }
            b = true;
            for (int j = this.nonMapIndex; j < this.ranges.length; j += 2) {
                if (this.ranges[j] <= n && n <= this.ranges[j + 1]) {
                    return false;
                }
            }
        }
        return b;
    }
    
    private void createMap() {
        final int n = 8;
        final int[] map = new int[n];
        int length = this.ranges.length;
        for (int i = 0; i < n; ++i) {
            map[i] = 0;
        }
        for (int j = 0; j < this.ranges.length; j += 2) {
            final int n2 = this.ranges[j];
            final int n3 = this.ranges[j + 1];
            if (n2 >= 256) {
                length = j;
                break;
            }
            for (int n4 = n2; n4 <= n3 && n4 < 256; ++n4) {
                final int[] array = map;
                final int n5 = n4 / 32;
                array[n5] |= 1 << (n4 & 0x1F);
            }
            if (n3 >= 256) {
                length = j;
                break;
            }
        }
        this.map = map;
        this.nonMapIndex = length;
    }
    
    public String toString(final int n) {
        String s;
        if (this.type == 4) {
            if (this == Token.token_dot) {
                s = ".";
            }
            else if (this == Token.token_0to9) {
                s = "\\d";
            }
            else if (this == Token.token_wordchars) {
                s = "\\w";
            }
            else if (this == Token.token_spaces) {
                s = "\\s";
            }
            else {
                final StringBuffer sb = new StringBuffer();
                sb.append('[');
                for (int i = 0; i < this.ranges.length; i += 2) {
                    if ((n & 0x400) != 0x0 && i > 0) {
                        sb.append(',');
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
                sb.append(']');
                s = sb.toString();
            }
        }
        else if (this == Token.token_not_0to9) {
            s = "\\D";
        }
        else if (this == Token.token_not_wordchars) {
            s = "\\W";
        }
        else if (this == Token.token_not_spaces) {
            s = "\\S";
        }
        else {
            final StringBuffer sb2 = new StringBuffer();
            sb2.append("[^");
            for (int j = 0; j < this.ranges.length; j += 2) {
                if ((n & 0x400) != 0x0 && j > 0) {
                    sb2.append(',');
                }
                if (this.ranges[j] == this.ranges[j + 1]) {
                    sb2.append(escapeCharInCharClass(this.ranges[j]));
                }
                else {
                    sb2.append(escapeCharInCharClass(this.ranges[j]));
                    sb2.append('-');
                    sb2.append(escapeCharInCharClass(this.ranges[j + 1]));
                }
            }
            sb2.append(']');
            s = sb2.toString();
        }
        return s;
    }
    
    private static String escapeCharInCharClass(final int n) {
        String s = null;
        switch (n) {
            case 44:
            case 45:
            case 91:
            case 92:
            case 93:
            case 94: {
                s = "\\" + (char)n;
                break;
            }
            case 12: {
                s = "\\f";
                break;
            }
            case 10: {
                s = "\\n";
                break;
            }
            case 13: {
                s = "\\r";
                break;
            }
            case 9: {
                s = "\\t";
                break;
            }
            case 27: {
                s = "\\e";
                break;
            }
            default: {
                if (n < 32) {
                    final String string = "0" + Integer.toHexString(n);
                    s = "\\x" + string.substring(string.length() - 2, string.length());
                    break;
                }
                if (n >= 65536) {
                    final String string2 = "0" + Integer.toHexString(n);
                    s = "\\v" + string2.substring(string2.length() - 6, string2.length());
                    break;
                }
                s = "" + (char)n;
                break;
            }
        }
        return s;
    }
}
