package sun.text.normalizer;

import java.util.Collection;
import java.util.Iterator;
import java.text.ParsePosition;
import java.util.TreeSet;

public class UnicodeSet implements UnicodeMatcher
{
    private static final int LOW = 0;
    private static final int HIGH = 1114112;
    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 1114111;
    private int len;
    private int[] list;
    private int[] rangeList;
    private int[] buffer;
    TreeSet<String> strings;
    private String pat;
    private static final int START_EXTRA = 16;
    private static final int GROW_EXTRA = 16;
    private static UnicodeSet[] INCLUSIONS;
    static final VersionInfo NO_VERSION;
    public static final int IGNORE_SPACE = 1;
    
    public UnicodeSet() {
        this.strings = new TreeSet<String>();
        this.pat = null;
        (this.list = new int[17])[this.len++] = 1114112;
    }
    
    public UnicodeSet(final int n, final int n2) {
        this();
        this.complement(n, n2);
    }
    
    public UnicodeSet(final String s) {
        this();
        this.applyPattern(s, null, null, 1);
    }
    
    public UnicodeSet set(final UnicodeSet set) {
        this.list = set.list.clone();
        this.len = set.len;
        this.pat = set.pat;
        this.strings = (TreeSet)set.strings.clone();
        return this;
    }
    
    public final UnicodeSet applyPattern(final String s) {
        return this.applyPattern(s, null, null, 1);
    }
    
    private static void _appendToPat(final StringBuffer sb, final String s, final boolean b) {
        for (int i = 0; i < s.length(); i += UTF16.getCharCount(i)) {
            _appendToPat(sb, UTF16.charAt(s, i), b);
        }
    }
    
    private static void _appendToPat(final StringBuffer sb, final int n, final boolean b) {
        if (b && Utility.isUnprintable(n) && Utility.escapeUnprintable(sb, n)) {
            return;
        }
        switch (n) {
            case 36:
            case 38:
            case 45:
            case 58:
            case 91:
            case 92:
            case 93:
            case 94:
            case 123:
            case 125: {
                sb.append('\\');
                break;
            }
            default: {
                if (UCharacterProperty.isRuleWhiteSpace(n)) {
                    sb.append('\\');
                    break;
                }
                break;
            }
        }
        UTF16.append(sb, n);
    }
    
    private StringBuffer _toPattern(final StringBuffer sb, final boolean b) {
        if (this.pat != null) {
            int n = 0;
            int i = 0;
            while (i < this.pat.length()) {
                final int char1 = UTF16.charAt(this.pat, i);
                i += UTF16.getCharCount(char1);
                if (b && Utility.isUnprintable(char1)) {
                    if (n % 2 == 1) {
                        sb.setLength(sb.length() - 1);
                    }
                    Utility.escapeUnprintable(sb, char1);
                    n = 0;
                }
                else {
                    UTF16.append(sb, char1);
                    if (char1 == 92) {
                        ++n;
                    }
                    else {
                        n = 0;
                    }
                }
            }
            return sb;
        }
        return this._generatePattern(sb, b, true);
    }
    
    public StringBuffer _generatePattern(final StringBuffer sb, final boolean b, final boolean b2) {
        sb.append('[');
        final int rangeCount = this.getRangeCount();
        if (rangeCount > 1 && this.getRangeStart(0) == 0 && this.getRangeEnd(rangeCount - 1) == 1114111) {
            sb.append('^');
            for (int i = 1; i < rangeCount; ++i) {
                final int n = this.getRangeEnd(i - 1) + 1;
                final int n2 = this.getRangeStart(i) - 1;
                _appendToPat(sb, n, b);
                if (n != n2) {
                    if (n + 1 != n2) {
                        sb.append('-');
                    }
                    _appendToPat(sb, n2, b);
                }
            }
        }
        else {
            for (int j = 0; j < rangeCount; ++j) {
                final int rangeStart = this.getRangeStart(j);
                final int rangeEnd = this.getRangeEnd(j);
                _appendToPat(sb, rangeStart, b);
                if (rangeStart != rangeEnd) {
                    if (rangeStart + 1 != rangeEnd) {
                        sb.append('-');
                    }
                    _appendToPat(sb, rangeEnd, b);
                }
            }
        }
        if (b2 && this.strings.size() > 0) {
            final Iterator<String> iterator = this.strings.iterator();
            while (iterator.hasNext()) {
                sb.append('{');
                _appendToPat(sb, iterator.next(), b);
                sb.append('}');
            }
        }
        return sb.append(']');
    }
    
    private UnicodeSet add_unchecked(final int n, final int n2) {
        if (n < 0 || n > 1114111) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(n, 6));
        }
        if (n2 < 0 || n2 > 1114111) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(n2, 6));
        }
        if (n < n2) {
            this.add(this.range(n, n2), 2, 0);
        }
        else if (n == n2) {
            this.add(n);
        }
        return this;
    }
    
    public final UnicodeSet add(final int n) {
        return this.add_unchecked(n);
    }
    
    private final UnicodeSet add_unchecked(final int n) {
        if (n < 0 || n > 1114111) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(n, 6));
        }
        final int codePoint = this.findCodePoint(n);
        if ((codePoint & 0x1) != 0x0) {
            return this;
        }
        if (n == this.list[codePoint] - 1) {
            if ((this.list[codePoint] = n) == 1114111) {
                this.ensureCapacity(this.len + 1);
                this.list[this.len++] = 1114112;
            }
            if (codePoint > 0 && n == this.list[codePoint - 1]) {
                System.arraycopy(this.list, codePoint + 1, this.list, codePoint - 1, this.len - codePoint - 1);
                this.len -= 2;
            }
        }
        else if (codePoint > 0 && n == this.list[codePoint - 1]) {
            final int[] list = this.list;
            final int n2 = codePoint - 1;
            ++list[n2];
        }
        else {
            if (this.len + 2 > this.list.length) {
                final int[] list2 = new int[this.len + 2 + 16];
                if (codePoint != 0) {
                    System.arraycopy(this.list, 0, list2, 0, codePoint);
                }
                System.arraycopy(this.list, codePoint, list2, codePoint + 2, this.len - codePoint);
                this.list = list2;
            }
            else {
                System.arraycopy(this.list, codePoint, this.list, codePoint + 2, this.len - codePoint);
            }
            this.list[codePoint] = n;
            this.list[codePoint + 1] = n + 1;
            this.len += 2;
        }
        this.pat = null;
        return this;
    }
    
    public final UnicodeSet add(final String s) {
        final int singleCP = getSingleCP(s);
        if (singleCP < 0) {
            this.strings.add(s);
            this.pat = null;
        }
        else {
            this.add_unchecked(singleCP, singleCP);
        }
        return this;
    }
    
    private static int getSingleCP(final String s) {
        if (s.length() < 1) {
            throw new IllegalArgumentException("Can't use zero-length strings in UnicodeSet");
        }
        if (s.length() > 2) {
            return -1;
        }
        if (s.length() == 1) {
            return s.charAt(0);
        }
        final int char1 = UTF16.charAt(s, 0);
        if (char1 > 65535) {
            return char1;
        }
        return -1;
    }
    
    public UnicodeSet complement(final int n, final int n2) {
        if (n < 0 || n > 1114111) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(n, 6));
        }
        if (n2 < 0 || n2 > 1114111) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(n2, 6));
        }
        if (n <= n2) {
            this.xor(this.range(n, n2), 2, 0);
        }
        this.pat = null;
        return this;
    }
    
    public UnicodeSet complement() {
        if (this.list[0] == 0) {
            System.arraycopy(this.list, 1, this.list, 0, this.len - 1);
            --this.len;
        }
        else {
            this.ensureCapacity(this.len + 1);
            System.arraycopy(this.list, 0, this.list, 1, this.len);
            this.list[0] = 0;
            ++this.len;
        }
        this.pat = null;
        return this;
    }
    
    public boolean contains(final int n) {
        if (n < 0 || n > 1114111) {
            throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(n, 6));
        }
        return (this.findCodePoint(n) & 0x1) != 0x0;
    }
    
    private final int findCodePoint(final int n) {
        if (n < this.list[0]) {
            return 0;
        }
        if (this.len >= 2 && n >= this.list[this.len - 2]) {
            return this.len - 1;
        }
        int n2 = 0;
        int n3 = this.len - 1;
        while (true) {
            final int n4 = n2 + n3 >>> 1;
            if (n4 == n2) {
                break;
            }
            if (n < this.list[n4]) {
                n3 = n4;
            }
            else {
                n2 = n4;
            }
        }
        return n3;
    }
    
    public UnicodeSet addAll(final UnicodeSet set) {
        this.add(set.list, set.len, 0);
        this.strings.addAll(set.strings);
        return this;
    }
    
    public UnicodeSet retainAll(final UnicodeSet set) {
        this.retain(set.list, set.len, 0);
        this.strings.retainAll(set.strings);
        return this;
    }
    
    public UnicodeSet removeAll(final UnicodeSet set) {
        this.retain(set.list, set.len, 2);
        this.strings.removeAll(set.strings);
        return this;
    }
    
    public UnicodeSet clear() {
        this.list[0] = 1114112;
        this.len = 1;
        this.pat = null;
        this.strings.clear();
        return this;
    }
    
    public int getRangeCount() {
        return this.len / 2;
    }
    
    public int getRangeStart(final int n) {
        return this.list[n * 2];
    }
    
    public int getRangeEnd(final int n) {
        return this.list[n * 2 + 1] - 1;
    }
    
    UnicodeSet applyPattern(final String s, ParsePosition parsePosition, final SymbolTable symbolTable, final int n) {
        final boolean b = parsePosition == null;
        if (b) {
            parsePosition = new ParsePosition(0);
        }
        final StringBuffer sb = new StringBuffer();
        final RuleCharacterIterator ruleCharacterIterator = new RuleCharacterIterator(s, symbolTable, parsePosition);
        this.applyPattern(ruleCharacterIterator, symbolTable, sb, n);
        if (ruleCharacterIterator.inVariable()) {
            syntaxError(ruleCharacterIterator, "Extra chars in variable value");
        }
        this.pat = sb.toString();
        if (b) {
            int n2 = parsePosition.getIndex();
            if ((n & 0x1) != 0x0) {
                n2 = Utility.skipWhitespace(s, n2);
            }
            if (n2 != s.length()) {
                throw new IllegalArgumentException("Parse of \"" + s + "\" failed at " + n2);
            }
        }
        return this;
    }
    
    void applyPattern(final RuleCharacterIterator ruleCharacterIterator, final SymbolTable symbolTable, final StringBuffer sb, final int n) {
        int n2 = 3;
        if ((n & 0x1) != 0x0) {
            n2 |= 0x4;
        }
        final StringBuffer sb2 = new StringBuffer();
        StringBuffer sb3 = null;
        boolean b = false;
        UnicodeSet set = null;
        Object pos = null;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        boolean b2 = false;
        this.clear();
        while (n5 != 2 && !ruleCharacterIterator.atEnd()) {
            int n7 = 0;
            int escaped = 0;
            UnicodeSet set2 = null;
            int n8 = 0;
            if (resemblesPropertyPattern(ruleCharacterIterator, n2)) {
                n8 = 2;
            }
            else {
                pos = ruleCharacterIterator.getPos(pos);
                n7 = ruleCharacterIterator.next(n2);
                escaped = (ruleCharacterIterator.isEscaped() ? 1 : 0);
                if (n7 == 91 && escaped == 0) {
                    if (n5 == 1) {
                        ruleCharacterIterator.setPos(pos);
                        n8 = 1;
                    }
                    else {
                        n5 = 1;
                        sb2.append('[');
                        pos = ruleCharacterIterator.getPos(pos);
                        n7 = ruleCharacterIterator.next(n2);
                        final boolean escaped2 = ruleCharacterIterator.isEscaped();
                        if (n7 == 94 && !escaped2) {
                            b2 = true;
                            sb2.append('^');
                            pos = ruleCharacterIterator.getPos(pos);
                            n7 = ruleCharacterIterator.next(n2);
                            ruleCharacterIterator.isEscaped();
                        }
                        if (n7 != 45) {
                            ruleCharacterIterator.setPos(pos);
                            continue;
                        }
                        escaped = 1;
                    }
                }
                else if (symbolTable != null) {
                    final UnicodeMatcher lookupMatcher = symbolTable.lookupMatcher(n7);
                    if (lookupMatcher != null) {
                        try {
                            set2 = (UnicodeSet)lookupMatcher;
                            n8 = 3;
                        }
                        catch (final ClassCastException ex) {
                            syntaxError(ruleCharacterIterator, "Syntax error");
                        }
                    }
                }
            }
            if (n8 != 0) {
                if (n3 == 1) {
                    if (n6 != 0) {
                        syntaxError(ruleCharacterIterator, "Char expected after operator");
                    }
                    this.add_unchecked(n4, n4);
                    _appendToPat(sb2, n4, false);
                    n6 = 0;
                }
                if (n6 == 45 || n6 == 38) {
                    sb2.append((char)n6);
                }
                if (set2 == null) {
                    if (set == null) {
                        set = new UnicodeSet();
                    }
                    set2 = set;
                }
                switch (n8) {
                    case 1: {
                        set2.applyPattern(ruleCharacterIterator, symbolTable, sb2, n);
                        break;
                    }
                    case 2: {
                        ruleCharacterIterator.skipIgnored(n2);
                        set2.applyPropertyPattern(ruleCharacterIterator, sb2, symbolTable);
                        break;
                    }
                    case 3: {
                        set2._toPattern(sb2, false);
                        break;
                    }
                }
                b = true;
                if (n5 == 0) {
                    this.set(set2);
                    n5 = 2;
                    break;
                }
                switch (n6) {
                    case '-': {
                        this.removeAll(set2);
                        break;
                    }
                    case '&': {
                        this.retainAll(set2);
                        break;
                    }
                    case '\0': {
                        this.addAll(set2);
                        break;
                    }
                }
                n6 = '\0';
                n3 = 2;
            }
            else {
                if (n5 == 0) {
                    syntaxError(ruleCharacterIterator, "Missing '['");
                }
                if (escaped == 0) {
                    switch (n7) {
                        case 93: {
                            if (n3 == 1) {
                                this.add_unchecked(n4, n4);
                                _appendToPat(sb2, n4, false);
                            }
                            if (n6 == '-') {
                                this.add_unchecked(n6, n6);
                                sb2.append((char)n6);
                            }
                            else if (n6 == '&') {
                                syntaxError(ruleCharacterIterator, "Trailing '&'");
                            }
                            sb2.append(']');
                            n5 = 2;
                            continue;
                        }
                        case 45: {
                            if (n6 == '\0') {
                                if (n3 != 0) {
                                    n6 = (char)n7;
                                    continue;
                                }
                                this.add_unchecked(n7, n7);
                                n7 = ruleCharacterIterator.next(n2);
                                final boolean escaped3 = ruleCharacterIterator.isEscaped();
                                if (n7 == 93 && !escaped3) {
                                    sb2.append("-]");
                                    n5 = 2;
                                    continue;
                                }
                            }
                            syntaxError(ruleCharacterIterator, "'-' not after char or set");
                            break;
                        }
                        case 38: {
                            if (n3 == 2 && n6 == '\0') {
                                n6 = (char)n7;
                                continue;
                            }
                            syntaxError(ruleCharacterIterator, "'&' not after set");
                            break;
                        }
                        case 94: {
                            syntaxError(ruleCharacterIterator, "'^' not after '['");
                            break;
                        }
                        case 123: {
                            if (n6 != '\0') {
                                syntaxError(ruleCharacterIterator, "Missing operand after operator");
                            }
                            if (n3 == 1) {
                                this.add_unchecked(n4, n4);
                                _appendToPat(sb2, n4, false);
                            }
                            n3 = 0;
                            if (sb3 == null) {
                                sb3 = new StringBuffer();
                            }
                            else {
                                sb3.setLength(0);
                            }
                            boolean b3 = false;
                            while (!ruleCharacterIterator.atEnd()) {
                                final int next = ruleCharacterIterator.next(n2);
                                final boolean escaped4 = ruleCharacterIterator.isEscaped();
                                if (next == 125 && !escaped4) {
                                    b3 = true;
                                    break;
                                }
                                UTF16.append(sb3, next);
                            }
                            if (sb3.length() < 1 || !b3) {
                                syntaxError(ruleCharacterIterator, "Invalid multicharacter string");
                            }
                            this.add(sb3.toString());
                            sb2.append('{');
                            _appendToPat(sb2, sb3.toString(), false);
                            sb2.append('}');
                            continue;
                        }
                        case 36: {
                            pos = ruleCharacterIterator.getPos(pos);
                            n7 = ruleCharacterIterator.next(n2);
                            final boolean escaped5 = ruleCharacterIterator.isEscaped();
                            final boolean b4 = n7 == 93 && !escaped5;
                            if (symbolTable == null && !b4) {
                                n7 = 36;
                                ruleCharacterIterator.setPos(pos);
                                break;
                            }
                            if (b4 && n6 == '\0') {
                                if (n3 == 1) {
                                    this.add_unchecked(n4, n4);
                                    _appendToPat(sb2, n4, false);
                                }
                                this.add_unchecked(65535);
                                b = true;
                                sb2.append('$').append(']');
                                n5 = 2;
                                continue;
                            }
                            syntaxError(ruleCharacterIterator, "Unquoted '$'");
                            break;
                        }
                    }
                }
                switch (n3) {
                    case 0: {
                        n3 = 1;
                        n4 = n7;
                        continue;
                    }
                    case 1: {
                        if (n6 == '-') {
                            if (n4 >= n7) {
                                syntaxError(ruleCharacterIterator, "Invalid range");
                            }
                            this.add_unchecked(n4, n7);
                            _appendToPat(sb2, n4, false);
                            sb2.append((char)n6);
                            _appendToPat(sb2, n7, false);
                            n6 = (n3 = 0);
                            continue;
                        }
                        this.add_unchecked(n4, n4);
                        _appendToPat(sb2, n4, false);
                        n4 = n7;
                        continue;
                    }
                    case 2: {
                        if (n6 != '\0') {
                            syntaxError(ruleCharacterIterator, "Set expected after operator");
                        }
                        n4 = n7;
                        n3 = 1;
                        continue;
                    }
                }
            }
        }
        if (n5 != 2) {
            syntaxError(ruleCharacterIterator, "Missing ']'");
        }
        ruleCharacterIterator.skipIgnored(n2);
        if (b2) {
            this.complement();
        }
        if (b) {
            sb.append(sb2.toString());
        }
        else {
            this._generatePattern(sb, false, true);
        }
    }
    
    private static void syntaxError(final RuleCharacterIterator ruleCharacterIterator, final String s) {
        throw new IllegalArgumentException("Error: " + s + " at \"" + Utility.escape(ruleCharacterIterator.toString()) + '\"');
    }
    
    private void ensureCapacity(final int n) {
        if (n <= this.list.length) {
            return;
        }
        final int[] list = new int[n + 16];
        System.arraycopy(this.list, 0, list, 0, this.len);
        this.list = list;
    }
    
    private void ensureBufferCapacity(final int n) {
        if (this.buffer != null && n <= this.buffer.length) {
            return;
        }
        this.buffer = new int[n + 16];
    }
    
    private int[] range(final int n, final int n2) {
        if (this.rangeList == null) {
            this.rangeList = new int[] { n, n2 + 1, 1114112 };
        }
        else {
            this.rangeList[0] = n;
            this.rangeList[1] = n2 + 1;
        }
        return this.rangeList;
    }
    
    private UnicodeSet xor(final int[] array, final int n, final int n2) {
        this.ensureBufferCapacity(this.len + n);
        int n3 = 0;
        int n4 = 0;
        int len = 0;
        int n5 = this.list[n3++];
        int n6;
        if (n2 == 1 || n2 == 2) {
            n6 = 0;
            if (array[n4] == 0) {
                ++n4;
                n6 = array[n4];
            }
        }
        else {
            n6 = array[n4++];
        }
        while (true) {
            if (n5 < n6) {
                this.buffer[len++] = n5;
                n5 = this.list[n3++];
            }
            else if (n6 < n5) {
                this.buffer[len++] = n6;
                n6 = array[n4++];
            }
            else {
                if (n5 == 1114112) {
                    break;
                }
                n5 = this.list[n3++];
                n6 = array[n4++];
            }
        }
        this.buffer[len++] = 1114112;
        this.len = len;
        final int[] list = this.list;
        this.list = this.buffer;
        this.buffer = list;
        this.pat = null;
        return this;
    }
    
    private UnicodeSet add(final int[] array, final int n, int n2) {
        this.ensureBufferCapacity(this.len + n);
        int n3 = 0;
        int n4 = 0;
        int len = 0;
        int n5 = this.list[n3++];
        int max = array[n4++];
    Label_0620:
        while (true) {
            switch (n2) {
                case 0: {
                    if (n5 < max) {
                        if (len > 0 && n5 <= this.buffer[len - 1]) {
                            n5 = max(this.list[n3], this.buffer[--len]);
                        }
                        else {
                            this.buffer[len++] = n5;
                            n5 = this.list[n3];
                        }
                        ++n3;
                        n2 ^= 0x1;
                        continue;
                    }
                    if (max < n5) {
                        if (len > 0 && max <= this.buffer[len - 1]) {
                            max = max(array[n4], this.buffer[--len]);
                        }
                        else {
                            this.buffer[len++] = max;
                            max = array[n4];
                        }
                        ++n4;
                        n2 ^= 0x2;
                        continue;
                    }
                    if (n5 == 1114112) {
                        break Label_0620;
                    }
                    if (len > 0 && n5 <= this.buffer[len - 1]) {
                        n5 = max(this.list[n3], this.buffer[--len]);
                    }
                    else {
                        this.buffer[len++] = n5;
                        n5 = this.list[n3];
                    }
                    ++n3;
                    n2 ^= 0x1;
                    max = array[n4++];
                    n2 ^= 0x2;
                    continue;
                }
                case 3: {
                    if (max <= n5) {
                        if (n5 == 1114112) {
                            break Label_0620;
                        }
                        this.buffer[len++] = n5;
                    }
                    else {
                        if (max == 1114112) {
                            break Label_0620;
                        }
                        this.buffer[len++] = max;
                    }
                    n5 = this.list[n3++];
                    n2 ^= 0x1;
                    max = array[n4++];
                    n2 ^= 0x2;
                    continue;
                }
                case 1: {
                    if (n5 < max) {
                        this.buffer[len++] = n5;
                        n5 = this.list[n3++];
                        n2 ^= 0x1;
                        continue;
                    }
                    if (max < n5) {
                        max = array[n4++];
                        n2 ^= 0x2;
                        continue;
                    }
                    if (n5 == 1114112) {
                        break Label_0620;
                    }
                    n5 = this.list[n3++];
                    n2 ^= 0x1;
                    max = array[n4++];
                    n2 ^= 0x2;
                    continue;
                }
                case 2: {
                    if (max < n5) {
                        this.buffer[len++] = max;
                        max = array[n4++];
                        n2 ^= 0x2;
                        continue;
                    }
                    if (n5 < max) {
                        n5 = this.list[n3++];
                        n2 ^= 0x1;
                        continue;
                    }
                    if (n5 == 1114112) {
                        break Label_0620;
                    }
                    n5 = this.list[n3++];
                    n2 ^= 0x1;
                    max = array[n4++];
                    n2 ^= 0x2;
                    continue;
                }
            }
        }
        this.buffer[len++] = 1114112;
        this.len = len;
        final int[] list = this.list;
        this.list = this.buffer;
        this.buffer = list;
        this.pat = null;
        return this;
    }
    
    private UnicodeSet retain(final int[] array, final int n, int n2) {
        this.ensureBufferCapacity(this.len + n);
        int n3 = 0;
        int n4 = 0;
        int len = 0;
        int n5 = this.list[n3++];
        int n6 = array[n4++];
    Label_0508:
        while (true) {
            switch (n2) {
                case 0: {
                    if (n5 < n6) {
                        n5 = this.list[n3++];
                        n2 ^= 0x1;
                        continue;
                    }
                    if (n6 < n5) {
                        n6 = array[n4++];
                        n2 ^= 0x2;
                        continue;
                    }
                    if (n5 == 1114112) {
                        break Label_0508;
                    }
                    this.buffer[len++] = n5;
                    n5 = this.list[n3++];
                    n2 ^= 0x1;
                    n6 = array[n4++];
                    n2 ^= 0x2;
                    continue;
                }
                case 3: {
                    if (n5 < n6) {
                        this.buffer[len++] = n5;
                        n5 = this.list[n3++];
                        n2 ^= 0x1;
                        continue;
                    }
                    if (n6 < n5) {
                        this.buffer[len++] = n6;
                        n6 = array[n4++];
                        n2 ^= 0x2;
                        continue;
                    }
                    if (n5 == 1114112) {
                        break Label_0508;
                    }
                    this.buffer[len++] = n5;
                    n5 = this.list[n3++];
                    n2 ^= 0x1;
                    n6 = array[n4++];
                    n2 ^= 0x2;
                    continue;
                }
                case 1: {
                    if (n5 < n6) {
                        n5 = this.list[n3++];
                        n2 ^= 0x1;
                        continue;
                    }
                    if (n6 < n5) {
                        this.buffer[len++] = n6;
                        n6 = array[n4++];
                        n2 ^= 0x2;
                        continue;
                    }
                    if (n5 == 1114112) {
                        break Label_0508;
                    }
                    n5 = this.list[n3++];
                    n2 ^= 0x1;
                    n6 = array[n4++];
                    n2 ^= 0x2;
                    continue;
                }
                case 2: {
                    if (n6 < n5) {
                        n6 = array[n4++];
                        n2 ^= 0x2;
                        continue;
                    }
                    if (n5 < n6) {
                        this.buffer[len++] = n5;
                        n5 = this.list[n3++];
                        n2 ^= 0x1;
                        continue;
                    }
                    if (n5 == 1114112) {
                        break Label_0508;
                    }
                    n5 = this.list[n3++];
                    n2 ^= 0x1;
                    n6 = array[n4++];
                    n2 ^= 0x2;
                    continue;
                }
            }
        }
        this.buffer[len++] = 1114112;
        this.len = len;
        final int[] list = this.list;
        this.list = this.buffer;
        this.buffer = list;
        this.pat = null;
        return this;
    }
    
    private static final int max(final int n, final int n2) {
        return (n > n2) ? n : n2;
    }
    
    private static synchronized UnicodeSet getInclusions(final int n) {
        if (UnicodeSet.INCLUSIONS == null) {
            UnicodeSet.INCLUSIONS = new UnicodeSet[9];
        }
        if (UnicodeSet.INCLUSIONS[n] == null) {
            final UnicodeSet set = new UnicodeSet();
            switch (n) {
                case 2: {
                    UCharacterProperty.getInstance().upropsvec_addPropertyStarts(set);
                    UnicodeSet.INCLUSIONS[n] = set;
                    break;
                }
                default: {
                    throw new IllegalStateException("UnicodeSet.getInclusions(unknown src " + n + ")");
                }
            }
        }
        return UnicodeSet.INCLUSIONS[n];
    }
    
    private UnicodeSet applyFilter(final Filter filter, final int n) {
        this.clear();
        int n2 = -1;
        final UnicodeSet inclusions = getInclusions(n);
        for (int rangeCount = inclusions.getRangeCount(), i = 0; i < rangeCount; ++i) {
            final int rangeStart = inclusions.getRangeStart(i);
            for (int rangeEnd = inclusions.getRangeEnd(i), j = rangeStart; j <= rangeEnd; ++j) {
                if (filter.contains(j)) {
                    if (n2 < 0) {
                        n2 = j;
                    }
                }
                else if (n2 >= 0) {
                    this.add_unchecked(n2, j - 1);
                    n2 = -1;
                }
            }
        }
        if (n2 >= 0) {
            this.add_unchecked(n2, 1114111);
        }
        return this;
    }
    
    private static String mungeCharName(final String s) {
        final StringBuffer sb = new StringBuffer();
        int i = 0;
        while (i < s.length()) {
            int char1 = UTF16.charAt(s, i);
            i += UTF16.getCharCount(char1);
            if (UCharacterProperty.isRuleWhiteSpace(char1)) {
                if (sb.length() == 0) {
                    continue;
                }
                if (sb.charAt(sb.length() - 1) == ' ') {
                    continue;
                }
                char1 = 32;
            }
            UTF16.append(sb, char1);
        }
        if (sb.length() != 0 && sb.charAt(sb.length() - 1) == ' ') {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    public UnicodeSet applyPropertyAlias(final String s, final String s2, final SymbolTable symbolTable) {
        if (s2.length() > 0 && s.equals("Age")) {
            this.applyFilter(new VersionFilter(VersionInfo.getInstance(mungeCharName(s2))), 2);
            return this;
        }
        throw new IllegalArgumentException("Unsupported property: " + s);
    }
    
    private static boolean resemblesPropertyPattern(final RuleCharacterIterator ruleCharacterIterator, int n) {
        boolean b = false;
        n &= 0xFFFFFFFD;
        final Object pos = ruleCharacterIterator.getPos(null);
        final int next = ruleCharacterIterator.next(n);
        if (next == 91 || next == 92) {
            final int next2 = ruleCharacterIterator.next(n & 0xFFFFFFFB);
            b = ((next == 91) ? (next2 == 58) : (next2 == 78 || next2 == 112 || next2 == 80));
        }
        ruleCharacterIterator.setPos(pos);
        return b;
    }
    
    private UnicodeSet applyPropertyPattern(final String s, final ParsePosition parsePosition, final SymbolTable symbolTable) {
        final int index = parsePosition.getIndex();
        if (index + 5 > s.length()) {
            return null;
        }
        boolean b = false;
        boolean b2 = false;
        int n = 0;
        int n2;
        if (s.regionMatches(index, "[:", 0, 2)) {
            b = true;
            n2 = Utility.skipWhitespace(s, index + 2);
            if (n2 < s.length() && s.charAt(n2) == '^') {
                ++n2;
                n = 1;
            }
        }
        else {
            if (!s.regionMatches(true, index, "\\p", 0, 2) && !s.regionMatches(index, "\\N", 0, 2)) {
                return null;
            }
            final char char1 = s.charAt(index + 1);
            n = ((char1 == 'P') ? 1 : 0);
            b2 = (char1 == 'N');
            n2 = Utility.skipWhitespace(s, index + 2);
            if (n2 == s.length() || s.charAt(n2++) != '{') {
                return null;
            }
        }
        final int index2 = s.indexOf(b ? ":]" : "}", n2);
        if (index2 < 0) {
            return null;
        }
        final int index3 = s.indexOf(61, n2);
        String s2;
        String substring;
        if (index3 >= 0 && index3 < index2 && !b2) {
            s2 = s.substring(n2, index3);
            substring = s.substring(index3 + 1, index2);
        }
        else {
            s2 = s.substring(n2, index2);
            substring = "";
            if (b2) {
                substring = s2;
                s2 = "na";
            }
        }
        this.applyPropertyAlias(s2, substring, symbolTable);
        if (n != 0) {
            this.complement();
        }
        parsePosition.setIndex(index2 + (b ? 2 : 1));
        return this;
    }
    
    private void applyPropertyPattern(final RuleCharacterIterator ruleCharacterIterator, final StringBuffer sb, final SymbolTable symbolTable) {
        final String lookahead = ruleCharacterIterator.lookahead();
        final ParsePosition parsePosition = new ParsePosition(0);
        this.applyPropertyPattern(lookahead, parsePosition, symbolTable);
        if (parsePosition.getIndex() == 0) {
            syntaxError(ruleCharacterIterator, "Invalid property pattern");
        }
        ruleCharacterIterator.jumpahead(parsePosition.getIndex());
        sb.append(lookahead.substring(0, parsePosition.getIndex()));
    }
    
    static {
        UnicodeSet.INCLUSIONS = null;
        NO_VERSION = VersionInfo.getInstance(0, 0, 0, 0);
    }
    
    private static class VersionFilter implements Filter
    {
        VersionInfo version;
        
        VersionFilter(final VersionInfo version) {
            this.version = version;
        }
        
        @Override
        public boolean contains(final int n) {
            final VersionInfo age = UCharacter.getAge(n);
            return age != UnicodeSet.NO_VERSION && age.compareTo(this.version) <= 0;
        }
    }
    
    private interface Filter
    {
        boolean contains(final int p0);
    }
}
