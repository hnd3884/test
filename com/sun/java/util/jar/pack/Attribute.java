package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Attribute implements Comparable<Attribute>
{
    Layout def;
    byte[] bytes;
    Object fixups;
    private static final Map<List<Attribute>, List<Attribute>> canonLists;
    private static final Map<Layout, Attribute> attributes;
    private static final Map<Layout, Attribute> standardDefs;
    static final byte EK_INT = 1;
    static final byte EK_BCI = 2;
    static final byte EK_BCO = 3;
    static final byte EK_FLAG = 4;
    static final byte EK_REPL = 5;
    static final byte EK_REF = 6;
    static final byte EK_UN = 7;
    static final byte EK_CASE = 8;
    static final byte EK_CALL = 9;
    static final byte EK_CBLE = 10;
    static final byte EF_SIGN = 1;
    static final byte EF_DELTA = 2;
    static final byte EF_NULL = 4;
    static final byte EF_BACK = 8;
    static final int NO_BAND_INDEX = -1;
    
    public String name() {
        return this.def.name();
    }
    
    public Layout layout() {
        return this.def;
    }
    
    public byte[] bytes() {
        return this.bytes;
    }
    
    public int size() {
        return this.bytes.length;
    }
    
    public ConstantPool.Entry getNameRef() {
        return this.def.getNameRef();
    }
    
    private Attribute(final Attribute attribute) {
        this.def = attribute.def;
        this.bytes = attribute.bytes;
        this.fixups = attribute.fixups;
    }
    
    public Attribute(final Layout def, final byte[] bytes, final Object fixups) {
        this.def = def;
        this.bytes = bytes;
        Fixups.setBytes(this.fixups = fixups, bytes);
    }
    
    public Attribute(final Layout layout, final byte[] array) {
        this(layout, array, null);
    }
    
    public Attribute addContent(final byte[] bytes, final Object fixups) {
        assert this.isCanonical();
        if (bytes.length == 0 && fixups == null) {
            return this;
        }
        final Attribute attribute = new Attribute(this);
        attribute.bytes = bytes;
        Fixups.setBytes(attribute.fixups = fixups, bytes);
        return attribute;
    }
    
    public Attribute addContent(final byte[] array) {
        return this.addContent(array, null);
    }
    
    public void finishRefs(final ConstantPool.Index index) {
        if (this.fixups != null) {
            Fixups.finishRefs(this.fixups, this.bytes, index);
            this.fixups = null;
        }
    }
    
    public boolean isCanonical() {
        return this == this.def.canon;
    }
    
    @Override
    public int compareTo(final Attribute attribute) {
        return this.def.compareTo(attribute.def);
    }
    
    public static List<Attribute> getCanonList(final List<Attribute> list) {
        synchronized (Attribute.canonLists) {
            Object unmodifiableList = Attribute.canonLists.get(list);
            if (unmodifiableList == null) {
                final ArrayList list2 = new ArrayList(list.size());
                list2.addAll(list);
                unmodifiableList = Collections.unmodifiableList((List<?>)list2);
                Attribute.canonLists.put(list, (List<Attribute>)unmodifiableList);
            }
            return (List<Attribute>)unmodifiableList;
        }
    }
    
    public static Attribute find(final int n, final String s, final String s2) {
        final Layout key = Layout.makeKey(n, s, s2);
        synchronized (Attribute.attributes) {
            Attribute canonicalInstance = Attribute.attributes.get(key);
            if (canonicalInstance == null) {
                canonicalInstance = new Layout(n, s, s2).canonicalInstance();
                Attribute.attributes.put(key, canonicalInstance);
            }
            return canonicalInstance;
        }
    }
    
    public static Layout keyForLookup(final int n, final String s) {
        return Layout.makeKey(n, s);
    }
    
    public static Attribute lookup(Map<Layout, Attribute> standardDefs, final int n, final String s) {
        if (standardDefs == null) {
            standardDefs = Attribute.standardDefs;
        }
        return standardDefs.get(Layout.makeKey(n, s));
    }
    
    public static Attribute define(final Map<Layout, Attribute> map, final int n, final String s, final String s2) {
        final Attribute find = find(n, s, s2);
        map.put(Layout.makeKey(n, s), find);
        return find;
    }
    
    public static String contextName(final int n) {
        switch (n) {
            case 0: {
                return "class";
            }
            case 1: {
                return "field";
            }
            case 2: {
                return "method";
            }
            case 3: {
                return "code";
            }
            default: {
                return null;
            }
        }
    }
    
    void visitRefs(final Holder holder, final int n, final Collection<ConstantPool.Entry> collection) {
        if (n == 0) {
            collection.add(this.getNameRef());
        }
        if (this.bytes.length == 0) {
            return;
        }
        if (!this.def.hasRefs) {
            return;
        }
        if (this.fixups != null) {
            Fixups.visitRefs(this.fixups, collection);
            return;
        }
        this.def.parse(holder, this.bytes, 0, this.bytes.length, new ValueStream() {
            @Override
            public void putInt(final int n, final int n2) {
            }
            
            @Override
            public void putRef(final int n, final ConstantPool.Entry entry) {
                collection.add(entry);
            }
            
            @Override
            public int encodeBCI(final int n) {
                return n;
            }
        });
    }
    
    public void parse(final Holder holder, final byte[] array, final int n, final int n2, final ValueStream valueStream) {
        this.def.parse(holder, array, n, n2, valueStream);
    }
    
    public Object unparse(final ValueStream valueStream, final ByteArrayOutputStream byteArrayOutputStream) {
        return this.def.unparse(valueStream, byteArrayOutputStream);
    }
    
    @Override
    public String toString() {
        return this.def + "{" + ((this.bytes == null) ? -1 : this.size()) + "}" + ((this.fixups == null) ? "" : this.fixups.toString());
    }
    
    public static String normalizeLayoutString(final String s) {
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        final int length = s.length();
        while (i < length) {
            final char char1 = s.charAt(i++);
            if (char1 <= ' ') {
                continue;
            }
            if (char1 == '#') {
                int index = s.indexOf(10, i);
                int index2 = s.indexOf(13, i);
                if (index < 0) {
                    index = length;
                }
                if (index2 < 0) {
                    index2 = length;
                }
                i = Math.min(index, index2);
            }
            else if (char1 == '\\') {
                sb.append((int)s.charAt(i++));
            }
            else if (char1 == '0' && s.startsWith("0x", i - 1)) {
                final int n = i - 1;
                int j;
                for (j = n + 2; j < length; ++j) {
                    final char char2 = s.charAt(j);
                    if ((char2 < '0' || char2 > '9') && (char2 < 'a' || char2 > 'f')) {
                        break;
                    }
                }
                if (j > n) {
                    sb.append(Integer.decode(s.substring(n, j)));
                    i = j;
                }
                else {
                    sb.append(char1);
                }
            }
            else {
                sb.append(char1);
            }
        }
        return sb.toString();
    }
    
    static Layout.Element[] tokenizeLayout(final Layout layout, final int n, final String s) {
        final ArrayList list = new ArrayList(s.length());
        tokenizeLayout(layout, n, s, list);
        final Layout.Element[] array = new Layout.Element[list.size()];
        list.toArray(array);
        return array;
    }
    
    static void tokenizeLayout(final Layout layout, final int n, final String s, final List<Layout.Element> list) {
        boolean b = false;
        final int length = s.length();
        int i = 0;
    Label_0889_Outer:
        while (i < length) {
            final int n2 = i;
            final Layout.Element element = layout.new Element();
            byte b2 = 0;
            Label_1617: {
                switch (s.charAt(i++)) {
                    case 'B':
                    case 'H':
                    case 'I':
                    case 'V': {
                        b2 = 1;
                        --i;
                        i = tokenizeUInt(element, s, i);
                        break;
                    }
                    case 'S': {
                        b2 = 1;
                        --i;
                        i = tokenizeSInt(element, s, i);
                        break;
                    }
                    case 'P': {
                        b2 = 2;
                        if (s.charAt(i++) == 'O') {
                            final Layout.Element element2 = element;
                            element2.flags |= 0x2;
                            if (!b) {
                                i = -i;
                                continue;
                            }
                            ++i;
                        }
                        --i;
                        i = tokenizeUInt(element, s, i);
                        break;
                    }
                    case 'O': {
                        b2 = 3;
                        final Layout.Element element3 = element;
                        element3.flags |= 0x2;
                        if (!b) {
                            i = -i;
                            continue;
                        }
                        i = tokenizeSInt(element, s, i);
                        break;
                    }
                    case 'F': {
                        b2 = 4;
                        i = tokenizeUInt(element, s, i);
                        break;
                    }
                    case 'N': {
                        b2 = 5;
                        int tokenizeUInt = tokenizeUInt(element, s, i);
                        if (s.charAt(tokenizeUInt++) != '[') {
                            i = -tokenizeUInt;
                            continue;
                        }
                        final int n3;
                        i = skipBody(s, n3 = tokenizeUInt);
                        element.body = tokenizeLayout(layout, n, s.substring(n3, i++));
                        break;
                    }
                    case 'T': {
                        b2 = 7;
                        i = tokenizeSInt(element, s, i);
                        final ArrayList list2 = new ArrayList();
                        while (true) {
                            while (s.charAt(i++) == '(') {
                                final int n4 = i;
                                i = s.indexOf(41, i);
                                final String substring = s.substring(n4, i++);
                                final int length2 = substring.length();
                                if (s.charAt(i++) != '[') {
                                    i = -i;
                                }
                                else {
                                    int n5;
                                    if (s.charAt(i) == ']') {
                                        n5 = i;
                                    }
                                    else {
                                        i = skipBody(s, n5 = i);
                                    }
                                    final Layout.Element[] tokenizeLayout = tokenizeLayout(layout, n, s.substring(n5, i++));
                                    if (length2 != 0) {
                                        int n6 = 1;
                                        int n7 = 0;
                                        while (true) {
                                            int index = substring.indexOf(44, n7);
                                            if (index < 0) {
                                                index = length2;
                                            }
                                            String substring2 = substring.substring(n7, index);
                                            if (substring2.length() == 0) {
                                                substring2 = "empty";
                                            }
                                            final int caseDash = findCaseDash(substring2, 0);
                                            int value;
                                            int intAfter;
                                            if (caseDash >= 0) {
                                                value = parseIntBefore(substring2, caseDash);
                                                intAfter = parseIntAfter(substring2, caseDash);
                                                if (value >= intAfter) {
                                                    i = -i;
                                                    break;
                                                }
                                            }
                                            else {
                                                intAfter = (value = Integer.parseInt(substring2));
                                            }
                                            while (true) {
                                                final Layout.Element element4 = layout.new Element();
                                                element4.body = tokenizeLayout;
                                                element4.kind = 8;
                                                element4.removeBand();
                                                if (n6 == 0) {
                                                    final Layout.Element element5 = element4;
                                                    element5.flags |= 0x8;
                                                }
                                                n6 = 0;
                                                element4.value = value;
                                                list2.add(element4);
                                                if (value == intAfter) {
                                                    break;
                                                }
                                                ++value;
                                            }
                                            if (index == length2) {
                                                break;
                                            }
                                            n7 = index + 1;
                                        }
                                        continue Label_0889_Outer;
                                    }
                                    final Layout.Element element6 = layout.new Element();
                                    element6.body = tokenizeLayout;
                                    element6.kind = 8;
                                    element6.removeBand();
                                    list2.add(element6);
                                }
                                list2.toArray(element.body = new Layout.Element[list2.size()]);
                                element.kind = b2;
                                for (int j = 0; j < element.body.length - 1; ++j) {
                                    final Layout.Element element7 = element.body[j];
                                    if (matchCase(element, element7.value) != element7) {
                                        i = -i;
                                        break;
                                    }
                                }
                                break Label_1617;
                            }
                            i = -i;
                            continue;
                        }
                    }
                    case '(': {
                        b2 = 9;
                        element.removeBand();
                        i = s.indexOf(41, i);
                        final String substring3 = s.substring(n2 + 1, i++);
                        final int int1 = Integer.parseInt(substring3);
                        final int value2 = n + int1;
                        if (!(int1 + "").equals(substring3) || layout.elems == null || value2 < 0 || value2 >= layout.elems.length) {
                            i = -i;
                            continue;
                        }
                        final Layout.Element element8 = layout.elems[value2];
                        assert element8.kind == 10;
                        element.value = value2;
                        element.body = new Layout.Element[] { element8 };
                        if (int1 <= 0) {
                            final Layout.Element element9 = element;
                            element9.flags |= 0x8;
                            final Layout.Element element10 = element8;
                            element10.flags |= 0x8;
                            break;
                        }
                        break;
                    }
                    case 'K': {
                        b2 = 6;
                        switch (s.charAt(i++)) {
                            case 'I': {
                                element.refKind = 3;
                                break Label_1617;
                            }
                            case 'J': {
                                element.refKind = 5;
                                break Label_1617;
                            }
                            case 'F': {
                                element.refKind = 4;
                                break Label_1617;
                            }
                            case 'D': {
                                element.refKind = 6;
                                break Label_1617;
                            }
                            case 'S': {
                                element.refKind = 8;
                                break Label_1617;
                            }
                            case 'Q': {
                                element.refKind = 53;
                                break Label_1617;
                            }
                            case 'M': {
                                element.refKind = 15;
                                break Label_1617;
                            }
                            case 'T': {
                                element.refKind = 16;
                                break Label_1617;
                            }
                            case 'L': {
                                element.refKind = 51;
                                break Label_1617;
                            }
                            default: {
                                i = -i;
                                continue;
                            }
                        }
                        break;
                    }
                    case 'R': {
                        b2 = 6;
                        switch (s.charAt(i++)) {
                            case 'C': {
                                element.refKind = 7;
                                break Label_1617;
                            }
                            case 'S': {
                                element.refKind = 13;
                                break Label_1617;
                            }
                            case 'D': {
                                element.refKind = 12;
                                break Label_1617;
                            }
                            case 'F': {
                                element.refKind = 9;
                                break Label_1617;
                            }
                            case 'M': {
                                element.refKind = 10;
                                break Label_1617;
                            }
                            case 'I': {
                                element.refKind = 11;
                                break Label_1617;
                            }
                            case 'U': {
                                element.refKind = 1;
                                break Label_1617;
                            }
                            case 'Q': {
                                element.refKind = 50;
                                break Label_1617;
                            }
                            case 'Y': {
                                element.refKind = 18;
                                break Label_1617;
                            }
                            case 'B': {
                                element.refKind = 17;
                                break Label_1617;
                            }
                            case 'N': {
                                element.refKind = 52;
                                break Label_1617;
                            }
                            default: {
                                i = -i;
                                continue;
                            }
                        }
                        break;
                    }
                    default: {
                        i = -i;
                        continue;
                    }
                }
            }
            if (b2 == 6) {
                if (s.charAt(i++) == 'N') {
                    final Layout.Element element11 = element;
                    element11.flags |= 0x4;
                    ++i;
                }
                --i;
                i = tokenizeUInt(element, s, i);
                layout.hasRefs = true;
            }
            b = (b2 == 2);
            element.kind = b2;
            element.layout = s.substring(n2, i);
            list.add(element);
        }
    }
    
    static String[] splitBodies(final String s) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i++) != '[') {
                s.charAt(-i);
            }
            final int n;
            i = skipBody(s, n = i);
            list.add(s.substring(n, i));
        }
        final String[] array = new String[list.size()];
        list.toArray(array);
        return array;
    }
    
    private static int skipBody(final String s, int n) {
        assert s.charAt(n - 1) == '[';
        if (s.charAt(n) == ']') {
            return -n;
        }
        int i = 1;
        while (i > 0) {
            switch (s.charAt(n++)) {
                case '[': {
                    ++i;
                    continue;
                }
                case ']': {
                    --i;
                    continue;
                }
            }
        }
        --n;
        assert s.charAt(n) == ']';
        return n;
    }
    
    private static int tokenizeUInt(final Layout.Element element, final String s, int n) {
        switch (s.charAt(n++)) {
            case 'V': {
                element.len = 0;
                break;
            }
            case 'B': {
                element.len = 1;
                break;
            }
            case 'H': {
                element.len = 2;
                break;
            }
            case 'I': {
                element.len = 4;
                break;
            }
            default: {
                return -n;
            }
        }
        return n;
    }
    
    private static int tokenizeSInt(final Layout.Element element, final String s, int n) {
        if (s.charAt(n) == 'S') {
            element.flags |= 0x1;
            ++n;
        }
        return tokenizeUInt(element, s, n);
    }
    
    private static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }
    
    static int findCaseDash(final String s, int n) {
        if (n <= 0) {
            n = 1;
        }
        final int n2 = s.length() - 2;
        while (true) {
            final int index = s.indexOf(45, n);
            if (index < 0 || index > n2) {
                return -1;
            }
            if (isDigit(s.charAt(index - 1))) {
                char c = s.charAt(index + 1);
                if (c == '-' && index + 2 < s.length()) {
                    c = s.charAt(index + 2);
                }
                if (isDigit(c)) {
                    return index;
                }
            }
            n = index + 1;
        }
    }
    
    static int parseIntBefore(final String s, final int n) {
        int n2;
        for (n2 = n; n2 > 0 && isDigit(s.charAt(n2 - 1)); --n2) {}
        if (n2 == n) {
            return Integer.parseInt("empty");
        }
        if (n2 >= 1 && s.charAt(n2 - 1) == '-') {
            --n2;
        }
        assert !isDigit(s.charAt(n2 - 1));
        return Integer.parseInt(s.substring(n2, n));
    }
    
    static int parseIntAfter(final String s, final int n) {
        int n3;
        final int n2 = n3 = n + 1;
        final int length = s.length();
        if (n3 < length && s.charAt(n3) == '-') {
            ++n3;
        }
        while (n3 < length && isDigit(s.charAt(n3))) {
            ++n3;
        }
        if (n2 == n3) {
            return Integer.parseInt("empty");
        }
        return Integer.parseInt(s.substring(n2, n3));
    }
    
    static String expandCaseDashNotation(final String s) {
        int n = findCaseDash(s, 0);
        if (n < 0) {
            return s;
        }
        final StringBuilder sb = new StringBuilder(s.length() * 3);
        int n2 = 0;
        while (true) {
            sb.append(s.substring(n2, n));
            n2 = n + 1;
            final int intBefore = parseIntBefore(s, n);
            final int intAfter = parseIntAfter(s, n);
            assert intBefore < intAfter;
            sb.append(",");
            for (int i = intBefore + 1; i < intAfter; ++i) {
                sb.append(i);
                sb.append(",");
            }
            n = findCaseDash(s, n2);
            if (n < 0) {
                sb.append(s.substring(n2));
                return sb.toString();
            }
        }
    }
    
    static int parseUsing(final Layout.Element[] array, final Holder holder, final byte[] array2, int n, final int n2, final ValueStream valueStream) {
        int n3 = 0;
        int n4 = 0;
        final int n5 = n + n2;
        final int[] array3 = { 0 };
        for (int i = 0; i < array.length; ++i) {
            final Layout.Element element = array[i];
            final int bandIndex = element.bandIndex;
            switch (element.kind) {
                case 1: {
                    n = parseInt(element, array2, n, array3);
                    valueStream.putInt(bandIndex, array3[0]);
                    break;
                }
                case 2: {
                    n = parseInt(element, array2, n, array3);
                    final int n6 = array3[0];
                    final int encodeBCI = valueStream.encodeBCI(n6);
                    int n7;
                    if (!element.flagTest((byte)2)) {
                        n7 = encodeBCI;
                    }
                    else {
                        n7 = encodeBCI - n4;
                    }
                    n3 = n6;
                    n4 = encodeBCI;
                    valueStream.putInt(bandIndex, n7);
                    break;
                }
                case 3: {
                    assert element.flagTest((byte)2);
                    n = parseInt(element, array2, n, array3);
                    final int n8 = n3 + array3[0];
                    final int encodeBCI2 = valueStream.encodeBCI(n8);
                    final int n9 = encodeBCI2 - n4;
                    n3 = n8;
                    n4 = encodeBCI2;
                    valueStream.putInt(bandIndex, n9);
                    break;
                }
                case 4: {
                    n = parseInt(element, array2, n, array3);
                    valueStream.putInt(bandIndex, array3[0]);
                    break;
                }
                case 5: {
                    n = parseInt(element, array2, n, array3);
                    final int n10 = array3[0];
                    valueStream.putInt(bandIndex, n10);
                    for (int j = 0; j < n10; ++j) {
                        n = parseUsing(element.body, holder, array2, n, n5 - n, valueStream);
                    }
                    break;
                }
                case 7: {
                    n = parseInt(element, array2, n, array3);
                    final int n11 = array3[0];
                    valueStream.putInt(bandIndex, n11);
                    n = parseUsing(matchCase(element, n11).body, holder, array2, n, n5 - n, valueStream);
                    break;
                }
                case 9: {
                    assert element.body.length == 1;
                    assert element.body[0].kind == 10;
                    if (element.flagTest((byte)8)) {
                        valueStream.noteBackCall(element.value);
                    }
                    n = parseUsing(element.body[0].body, holder, array2, n, n5 - n, valueStream);
                    break;
                }
                case 6: {
                    n = parseInt(element, array2, n, array3);
                    final int n12 = array3[0];
                    ConstantPool.Entry signatureEntry;
                    if (n12 == 0) {
                        signatureEntry = null;
                    }
                    else {
                        final ConstantPool.Entry[] cpMap = holder.getCPMap();
                        signatureEntry = ((n12 >= 0 && n12 < cpMap.length) ? cpMap[n12] : null);
                        final byte refKind = element.refKind;
                        if (signatureEntry != null && refKind == 13 && signatureEntry.getTag() == 1) {
                            signatureEntry = ConstantPool.getSignatureEntry(signatureEntry.stringValue());
                        }
                        final String s = (signatureEntry == null) ? "invalid CP index" : ("type=" + ConstantPool.tagName(signatureEntry.tag));
                        if (signatureEntry == null || !signatureEntry.tagMatches(refKind)) {
                            throw new IllegalArgumentException("Bad constant, expected type=" + ConstantPool.tagName(refKind) + " got " + s);
                        }
                    }
                    valueStream.putRef(bandIndex, signatureEntry);
                    break;
                }
                default: {
                    assert false;
                    break;
                }
            }
        }
        return n;
    }
    
    static Layout.Element matchCase(final Layout.Element element, final int n) {
        assert element.kind == 7;
        final int n2 = element.body.length - 1;
        for (int i = 0; i < n2; ++i) {
            final Layout.Element element2 = element.body[i];
            assert element2.kind == 8;
            if (n == element2.value) {
                return element2;
            }
        }
        return element.body[n2];
    }
    
    private static int parseInt(final Layout.Element element, final byte[] array, int n, final int[] array2) {
        int n2 = 0;
        int n4;
        final int n3 = n4 = element.len * 8;
        while (true) {
            n4 -= 8;
            if (n4 < 0) {
                break;
            }
            n2 += (array[n++] & 0xFF) << n4;
        }
        if (n3 < 32 && element.flagTest((byte)1)) {
            final int n5 = 32 - n3;
            n2 = n2 << n5 >> n5;
        }
        array2[0] = n2;
        return n;
    }
    
    static void unparseUsing(final Layout.Element[] array, final Object[] array2, final ValueStream valueStream, final ByteArrayOutputStream byteArrayOutputStream) {
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < array.length; ++i) {
            final Layout.Element element = array[i];
            final int bandIndex = element.bandIndex;
            switch (element.kind) {
                case 1: {
                    unparseInt(element, valueStream.getInt(bandIndex), byteArrayOutputStream);
                    break;
                }
                case 2: {
                    final int int1 = valueStream.getInt(bandIndex);
                    int n3;
                    if (!element.flagTest((byte)2)) {
                        n3 = int1;
                    }
                    else {
                        n3 = n2 + int1;
                    }
                    assert n == valueStream.decodeBCI(n2);
                    final int decodeBCI = valueStream.decodeBCI(n3);
                    unparseInt(element, decodeBCI, byteArrayOutputStream);
                    n = decodeBCI;
                    n2 = n3;
                    break;
                }
                case 3: {
                    final int int2 = valueStream.getInt(bandIndex);
                    assert element.flagTest((byte)2);
                    assert n == valueStream.decodeBCI(n2);
                    final int n4 = n2 + int2;
                    final int decodeBCI2 = valueStream.decodeBCI(n4);
                    unparseInt(element, decodeBCI2 - n, byteArrayOutputStream);
                    n = decodeBCI2;
                    n2 = n4;
                    break;
                }
                case 4: {
                    unparseInt(element, valueStream.getInt(bandIndex), byteArrayOutputStream);
                    break;
                }
                case 5: {
                    final int int3 = valueStream.getInt(bandIndex);
                    unparseInt(element, int3, byteArrayOutputStream);
                    for (int j = 0; j < int3; ++j) {
                        unparseUsing(element.body, array2, valueStream, byteArrayOutputStream);
                    }
                    break;
                }
                case 7: {
                    final int int4 = valueStream.getInt(bandIndex);
                    unparseInt(element, int4, byteArrayOutputStream);
                    unparseUsing(matchCase(element, int4).body, array2, valueStream, byteArrayOutputStream);
                    break;
                }
                case 9: {
                    assert element.body.length == 1;
                    assert element.body[0].kind == 10;
                    unparseUsing(element.body[0].body, array2, valueStream, byteArrayOutputStream);
                    break;
                }
                case 6: {
                    final ConstantPool.Entry ref = valueStream.getRef(bandIndex);
                    int n5;
                    if (ref != null) {
                        array2[0] = Fixups.addRefWithLoc(array2[0], byteArrayOutputStream.size(), ref);
                        n5 = 0;
                    }
                    else {
                        n5 = 0;
                    }
                    unparseInt(element, n5, byteArrayOutputStream);
                    break;
                }
                default: {
                    assert false;
                    break;
                }
            }
        }
    }
    
    private static void unparseInt(final Layout.Element element, final int n, final ByteArrayOutputStream byteArrayOutputStream) {
        final int n2 = element.len * 8;
        if (n2 == 0) {
            return;
        }
        if (n2 < 32) {
            final int n3 = 32 - n2;
            int n4;
            if (element.flagTest((byte)1)) {
                n4 = n << n3 >> n3;
            }
            else {
                n4 = n << n3 >>> n3;
            }
            if (n4 != n) {
                throw new InternalError("cannot code in " + element.len + " bytes: " + n);
            }
        }
        int n5 = n2;
        while (true) {
            n5 -= 8;
            if (n5 < 0) {
                break;
            }
            byteArrayOutputStream.write((byte)(n >>> n5));
        }
    }
    
    static {
        canonLists = new HashMap<List<Attribute>, List<Attribute>>();
        attributes = new HashMap<Layout, Attribute>();
        standardDefs = new HashMap<Layout, Attribute>();
        final Map<Layout, Attribute> standardDefs2 = Attribute.standardDefs;
        define(standardDefs2, 0, "Signature", "RSH");
        define(standardDefs2, 0, "Synthetic", "");
        define(standardDefs2, 0, "Deprecated", "");
        define(standardDefs2, 0, "SourceFile", "RUH");
        define(standardDefs2, 0, "EnclosingMethod", "RCHRDNH");
        define(standardDefs2, 0, "InnerClasses", "NH[RCHRCNHRUNHFH]");
        define(standardDefs2, 0, "BootstrapMethods", "NH[RMHNH[KLH]]");
        define(standardDefs2, 1, "Signature", "RSH");
        define(standardDefs2, 1, "Synthetic", "");
        define(standardDefs2, 1, "Deprecated", "");
        define(standardDefs2, 1, "ConstantValue", "KQH");
        define(standardDefs2, 2, "Signature", "RSH");
        define(standardDefs2, 2, "Synthetic", "");
        define(standardDefs2, 2, "Deprecated", "");
        define(standardDefs2, 2, "Exceptions", "NH[RCH]");
        define(standardDefs2, 2, "MethodParameters", "NB[RUNHFH]");
        define(standardDefs2, 3, "StackMapTable", "[NH[(1)]][TB(64-127)[(2)](247)[(1)(2)](248-251)[(1)](252)[(1)(2)](253)[(1)(2)(2)](254)[(1)(2)(2)(2)](255)[(1)NH[(2)]NH[(2)]]()[]][H][TB(7)[RCH](8)[PH]()[]]");
        define(standardDefs2, 3, "LineNumberTable", "NH[PHH]");
        define(standardDefs2, 3, "LocalVariableTable", "NH[PHOHRUHRSHH]");
        define(standardDefs2, 3, "LocalVariableTypeTable", "NH[PHOHRUHRSHH]");
        final String[] array = { normalizeLayoutString("\n  # parameter_annotations :=\n  [ NB[(1)] ]     # forward call to annotations"), normalizeLayoutString("\n  # annotations :=\n  [ NH[(1)] ]     # forward call to annotation\n  "), normalizeLayoutString("\n  # annotation :=\n  [RSH\n    NH[RUH (1)]   # forward call to value\n    ]"), normalizeLayoutString("\n  # value :=\n  [TB # Callable 2 encodes one tagged value.\n    (\\B,\\C,\\I,\\S,\\Z)[KIH]\n    (\\D)[KDH]\n    (\\F)[KFH]\n    (\\J)[KJH]\n    (\\c)[RSH]\n    (\\e)[RSH RUH]\n    (\\s)[RUH]\n    (\\[)[NH[(0)]] # backward self-call to value\n    (\\@)[RSH NH[RUH (0)]] # backward self-call to value\n    ()[] ]") };
        final String[] array2 = { normalizeLayoutString("\n # type-annotations :=\n  [ NH[(1)(2)(3)] ]     # forward call to type-annotations"), normalizeLayoutString("\n  # type-annotation :=\n  [TB\n    (0-1) [B] # {CLASS, METHOD}_TYPE_PARAMETER\n    (16) [FH] # CLASS_EXTENDS\n    (17-18) [BB] # {CLASS, METHOD}_TYPE_PARAMETER_BOUND\n    (19-21) [] # FIELD, METHOD_RETURN, METHOD_RECEIVER\n    (22) [B] # METHOD_FORMAL_PARAMETER\n    (23) [H] # THROWS\n    (64-65) [NH[PHOHH]] # LOCAL_VARIABLE, RESOURCE_VARIABLE\n    (66) [H] # EXCEPTION_PARAMETER\n    (67-70) [PH] # INSTANCEOF, NEW, {CONSTRUCTOR, METHOD}_REFERENCE_RECEIVER\n    (71-75) [PHB] # CAST, {CONSTRUCTOR,METHOD}_INVOCATION_TYPE_ARGUMENT, {CONSTRUCTOR, METHOD}_REFERENCE_TYPE_ARGUMENT\n    ()[] ]"), normalizeLayoutString("\n # type-path\n [ NB[BB] ]") };
        final Map<Layout, Attribute> standardDefs3 = Attribute.standardDefs;
        final String s = array[3];
        final String string = array[1] + array[2] + array[3];
        final String string2 = array[0] + string;
        final String string3 = array2[0] + array2[1] + array2[2] + array[2] + array[3];
        for (int i = 0; i < 4; ++i) {
            if (i != 3) {
                define(standardDefs3, i, "RuntimeVisibleAnnotations", string);
                define(standardDefs3, i, "RuntimeInvisibleAnnotations", string);
                if (i == 2) {
                    define(standardDefs3, i, "RuntimeVisibleParameterAnnotations", string2);
                    define(standardDefs3, i, "RuntimeInvisibleParameterAnnotations", string2);
                    define(standardDefs3, i, "AnnotationDefault", s);
                }
            }
            define(standardDefs3, i, "RuntimeVisibleTypeAnnotations", string3);
            define(standardDefs3, i, "RuntimeInvisibleTypeAnnotations", string3);
        }
        assert expandCaseDashNotation("1-5").equals("1,2,3,4,5");
        assert expandCaseDashNotation("-2--1").equals("-2,-1");
        assert expandCaseDashNotation("-2-1").equals("-2,-1,0,1");
        assert expandCaseDashNotation("-1-0").equals("-1,0");
    }
    
    public abstract static class Holder
    {
        protected int flags;
        protected List<Attribute> attributes;
        static final List<Attribute> noAttributes;
        
        protected abstract ConstantPool.Entry[] getCPMap();
        
        public int attributeSize() {
            return (this.attributes == null) ? 0 : this.attributes.size();
        }
        
        public void trimToSize() {
            if (this.attributes == null) {
                return;
            }
            if (this.attributes.isEmpty()) {
                this.attributes = null;
                return;
            }
            if (this.attributes instanceof ArrayList) {
                final ArrayList list = (ArrayList)this.attributes;
                list.trimToSize();
                boolean b = true;
                for (final Attribute attribute : list) {
                    if (!attribute.isCanonical()) {
                        b = false;
                    }
                    if (attribute.fixups != null) {
                        assert !attribute.isCanonical();
                        attribute.fixups = Fixups.trimToSize(attribute.fixups);
                    }
                }
                if (b) {
                    this.attributes = Attribute.getCanonList(list);
                }
            }
        }
        
        public void addAttribute(final Attribute attribute) {
            if (this.attributes == null) {
                this.attributes = new ArrayList<Attribute>(3);
            }
            else if (!(this.attributes instanceof ArrayList)) {
                this.attributes = new ArrayList<Attribute>(this.attributes);
            }
            this.attributes.add(attribute);
        }
        
        public Attribute removeAttribute(final Attribute attribute) {
            if (this.attributes == null) {
                return null;
            }
            if (!this.attributes.contains(attribute)) {
                return null;
            }
            if (!(this.attributes instanceof ArrayList)) {
                this.attributes = new ArrayList<Attribute>(this.attributes);
            }
            this.attributes.remove(attribute);
            return attribute;
        }
        
        public Attribute getAttribute(final int n) {
            return this.attributes.get(n);
        }
        
        protected void visitRefs(final int n, final Collection<ConstantPool.Entry> collection) {
            if (this.attributes == null) {
                return;
            }
            final Iterator<Attribute> iterator = this.attributes.iterator();
            while (iterator.hasNext()) {
                iterator.next().visitRefs(this, n, collection);
            }
        }
        
        public List<Attribute> getAttributes() {
            if (this.attributes == null) {
                return Holder.noAttributes;
            }
            return this.attributes;
        }
        
        public void setAttributes(final List<Attribute> attributes) {
            if (attributes.isEmpty()) {
                this.attributes = null;
            }
            else {
                this.attributes = attributes;
            }
        }
        
        public Attribute getAttribute(final String s) {
            if (this.attributes == null) {
                return null;
            }
            for (final Attribute attribute : this.attributes) {
                if (attribute.name().equals(s)) {
                    return attribute;
                }
            }
            return null;
        }
        
        public Attribute getAttribute(final Layout layout) {
            if (this.attributes == null) {
                return null;
            }
            for (final Attribute attribute : this.attributes) {
                if (attribute.layout() == layout) {
                    return attribute;
                }
            }
            return null;
        }
        
        public Attribute removeAttribute(final String s) {
            return this.removeAttribute(this.getAttribute(s));
        }
        
        public Attribute removeAttribute(final Layout layout) {
            return this.removeAttribute(this.getAttribute(layout));
        }
        
        public void strip(final String s) {
            this.removeAttribute(this.getAttribute(s));
        }
        
        static {
            noAttributes = Arrays.asList(new Attribute[0]);
        }
    }
    
    public abstract static class ValueStream
    {
        public int getInt(final int n) {
            throw this.undef();
        }
        
        public void putInt(final int n, final int n2) {
            throw this.undef();
        }
        
        public ConstantPool.Entry getRef(final int n) {
            throw this.undef();
        }
        
        public void putRef(final int n, final ConstantPool.Entry entry) {
            throw this.undef();
        }
        
        public int decodeBCI(final int n) {
            throw this.undef();
        }
        
        public int encodeBCI(final int n) {
            throw this.undef();
        }
        
        public void noteBackCall(final int n) {
        }
        
        private RuntimeException undef() {
            return new UnsupportedOperationException("ValueStream method");
        }
    }
    
    public static class Layout implements Comparable<Layout>
    {
        int ctype;
        String name;
        boolean hasRefs;
        String layout;
        int bandCount;
        Element[] elems;
        Attribute canon;
        private static final Element[] noElems;
        
        public int ctype() {
            return this.ctype;
        }
        
        public String name() {
            return this.name;
        }
        
        public String layout() {
            return this.layout;
        }
        
        public Attribute canonicalInstance() {
            return this.canon;
        }
        
        public ConstantPool.Entry getNameRef() {
            return ConstantPool.getUtf8Entry(this.name());
        }
        
        public boolean isEmpty() {
            return this.layout.isEmpty();
        }
        
        public Layout(final int ctype, final String s, final String s2) {
            this.ctype = ctype;
            this.name = s.intern();
            this.layout = s2.intern();
            assert ctype < 4;
            final boolean startsWith = s2.startsWith("[");
            try {
                if (!startsWith) {
                    this.elems = Attribute.tokenizeLayout(this, -1, s2);
                }
                else {
                    final String[] splitBodies = Attribute.splitBodies(s2);
                    final Element[] elems = new Element[splitBodies.length];
                    this.elems = elems;
                    for (int i = 0; i < elems.length; ++i) {
                        final Element element = new Element();
                        element.kind = 10;
                        element.removeBand();
                        element.bandIndex = -1;
                        element.layout = splitBodies[i];
                        elems[i] = element;
                    }
                    for (int j = 0; j < elems.length; ++j) {
                        elems[j].body = Attribute.tokenizeLayout(this, j, splitBodies[j]);
                    }
                }
            }
            catch (final StringIndexOutOfBoundsException ex) {
                throw new RuntimeException("Bad attribute layout: " + s2, ex);
            }
            this.canon = new Attribute(this, Constants.noBytes);
        }
        
        private Layout() {
        }
        
        static Layout makeKey(final int ctype, final String s, final String s2) {
            final Layout layout = new Layout();
            layout.ctype = ctype;
            layout.name = s.intern();
            layout.layout = s2.intern();
            assert ctype < 4;
            return layout;
        }
        
        static Layout makeKey(final int n, final String s) {
            return makeKey(n, s, "");
        }
        
        public Attribute addContent(final byte[] array, final Object o) {
            return this.canon.addContent(array, o);
        }
        
        public Attribute addContent(final byte[] array) {
            return this.canon.addContent(array, null);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o != null && o.getClass() == Layout.class && this.equals((Layout)o);
        }
        
        public boolean equals(final Layout layout) {
            return this.name.equals(layout.name) && this.layout.equals(layout.layout) && this.ctype == layout.ctype;
        }
        
        @Override
        public int hashCode() {
            return ((17 + this.name.hashCode()) * 37 + this.layout.hashCode()) * 37 + this.ctype;
        }
        
        @Override
        public int compareTo(final Layout layout) {
            final int compareTo = this.name.compareTo(layout.name);
            if (compareTo != 0) {
                return compareTo;
            }
            final int compareTo2 = this.layout.compareTo(layout.layout);
            if (compareTo2 != 0) {
                return compareTo2;
            }
            return this.ctype - layout.ctype;
        }
        
        @Override
        public String toString() {
            String s = Attribute.contextName(this.ctype) + "." + this.name + "[" + this.layout + "]";
            assert (s = this.stringForDebug()) != null;
            return s;
        }
        
        private String stringForDebug() {
            return Attribute.contextName(this.ctype) + "." + this.name + Arrays.asList(this.elems);
        }
        
        public boolean hasCallables() {
            return this.elems.length > 0 && this.elems[0].kind == 10;
        }
        
        public Element[] getCallables() {
            if (this.hasCallables()) {
                return Arrays.copyOf(this.elems, this.elems.length);
            }
            return Layout.noElems;
        }
        
        public Element[] getEntryPoint() {
            if (this.hasCallables()) {
                return this.elems[0].body;
            }
            return Arrays.copyOf(this.elems, this.elems.length);
        }
        
        public void parse(final Holder holder, final byte[] array, final int n, final int n2, final ValueStream valueStream) {
            final int using = Attribute.parseUsing(this.getEntryPoint(), holder, array, n, n2, valueStream);
            if (using != n + n2) {
                throw new InternalError("layout parsed " + (using - n) + " out of " + n2 + " bytes");
            }
        }
        
        public Object unparse(final ValueStream valueStream, final ByteArrayOutputStream byteArrayOutputStream) {
            final Object[] array = { null };
            Attribute.unparseUsing(this.getEntryPoint(), array, valueStream, byteArrayOutputStream);
            return array[0];
        }
        
        public String layoutForClassVersion(final Package.Version version) {
            if (version.lessThan(Constants.JAVA6_MAX_CLASS_VERSION)) {
                return Attribute.expandCaseDashNotation(this.layout);
            }
            return this.layout;
        }
        
        static {
            noElems = new Element[0];
        }
        
        public class Element
        {
            String layout;
            byte flags;
            byte kind;
            byte len;
            byte refKind;
            int bandIndex;
            int value;
            Element[] body;
            
            boolean flagTest(final byte b) {
                return (this.flags & b) != 0x0;
            }
            
            Element() {
                this.bandIndex = Layout.this.bandCount++;
            }
            
            void removeBand() {
                final Layout this$0 = Layout.this;
                --this$0.bandCount;
                assert this.bandIndex == Layout.this.bandCount;
                this.bandIndex = -1;
            }
            
            public boolean hasBand() {
                return this.bandIndex >= 0;
            }
            
            @Override
            public String toString() {
                String s = this.layout;
                assert (s = this.stringForDebug()) != null;
                return s;
            }
            
            private String stringForDebug() {
                Element[] body = this.body;
                switch (this.kind) {
                    case 9: {
                        body = null;
                        break;
                    }
                    case 8: {
                        if (this.flagTest((byte)8)) {
                            body = null;
                            break;
                        }
                        break;
                    }
                }
                return this.layout + (this.hasBand() ? ("#" + this.bandIndex) : "") + "<" + ((this.flags == 0) ? "" : ("" + this.flags)) + this.kind + this.len + ((this.refKind == 0) ? "" : ("" + this.refKind)) + ">" + ((this.value == 0) ? "" : ("(" + this.value + ")")) + ((body == null) ? "" : ("" + Arrays.asList(body)));
            }
        }
    }
    
    public static class FormatException extends IOException
    {
        private static final long serialVersionUID = -2542243830788066513L;
        private int ctype;
        private String name;
        String layout;
        
        public FormatException(final String s, final int ctype, final String name, final String layout) {
            super(Constants.ATTR_CONTEXT_NAME[ctype] + " attribute \"" + name + "\"" + ((s == null) ? "" : (": " + s)));
            this.ctype = ctype;
            this.name = name;
            this.layout = layout;
        }
        
        public FormatException(final String s, final int n, final String s2) {
            this(s, n, s2, null);
        }
    }
}
