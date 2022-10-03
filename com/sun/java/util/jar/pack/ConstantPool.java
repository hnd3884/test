package com.sun.java.util.jar.pack;

import java.util.AbstractList;
import java.util.ListIterator;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

abstract class ConstantPool
{
    protected static final Entry[] noRefs;
    protected static final ClassEntry[] noClassRefs;
    static final byte[] TAGS_IN_ORDER;
    static final byte[] TAG_ORDER;
    static final byte[] NUMBER_TAGS;
    static final byte[] EXTRA_TAGS;
    static final byte[] LOADABLE_VALUE_TAGS;
    static final byte[] ANY_MEMBER_TAGS;
    static final byte[] FIELD_SPECIFIC_TAGS;
    
    private ConstantPool() {
    }
    
    static int verbose() {
        return Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose");
    }
    
    public static synchronized Utf8Entry getUtf8Entry(final String s) {
        final Map<String, Utf8Entry> utf8Entries = Utils.getTLGlobals().getUtf8Entries();
        Utf8Entry utf8Entry = utf8Entries.get(s);
        if (utf8Entry == null) {
            utf8Entry = new Utf8Entry(s);
            utf8Entries.put(utf8Entry.stringValue(), utf8Entry);
        }
        return utf8Entry;
    }
    
    public static ClassEntry getClassEntry(final String s) {
        final Map<String, ClassEntry> classEntries = Utils.getTLGlobals().getClassEntries();
        ClassEntry classEntry = classEntries.get(s);
        if (classEntry == null) {
            classEntry = new ClassEntry(getUtf8Entry(s));
            assert s.equals(classEntry.stringValue());
            classEntries.put(classEntry.stringValue(), classEntry);
        }
        return classEntry;
    }
    
    public static LiteralEntry getLiteralEntry(final Comparable<?> comparable) {
        final Map<Object, LiteralEntry> literalEntries = Utils.getTLGlobals().getLiteralEntries();
        LiteralEntry literalEntry = literalEntries.get(comparable);
        if (literalEntry == null) {
            if (comparable instanceof String) {
                literalEntry = new StringEntry(getUtf8Entry((String)comparable));
            }
            else {
                literalEntry = new NumberEntry((Number)comparable);
            }
            literalEntries.put(comparable, literalEntry);
        }
        return literalEntry;
    }
    
    public static StringEntry getStringEntry(final String s) {
        return (StringEntry)getLiteralEntry(s);
    }
    
    public static SignatureEntry getSignatureEntry(final String s) {
        final Map<String, SignatureEntry> signatureEntries = Utils.getTLGlobals().getSignatureEntries();
        SignatureEntry signatureEntry = signatureEntries.get(s);
        if (signatureEntry == null) {
            signatureEntry = new SignatureEntry(s);
            assert signatureEntry.stringValue().equals(s);
            signatureEntries.put(s, signatureEntry);
        }
        return signatureEntry;
    }
    
    public static SignatureEntry getSignatureEntry(final Utf8Entry utf8Entry, final ClassEntry[] array) {
        return getSignatureEntry(SignatureEntry.stringValueOf(utf8Entry, array));
    }
    
    public static DescriptorEntry getDescriptorEntry(final Utf8Entry utf8Entry, final SignatureEntry signatureEntry) {
        final Map<String, DescriptorEntry> descriptorEntries = Utils.getTLGlobals().getDescriptorEntries();
        final String stringValue = DescriptorEntry.stringValueOf(utf8Entry, signatureEntry);
        DescriptorEntry descriptorEntry = descriptorEntries.get(stringValue);
        if (descriptorEntry == null) {
            descriptorEntry = new DescriptorEntry(utf8Entry, signatureEntry);
            assert descriptorEntry.stringValue().equals(stringValue) : descriptorEntry.stringValue() + " != " + stringValue;
            descriptorEntries.put(stringValue, descriptorEntry);
        }
        return descriptorEntry;
    }
    
    public static DescriptorEntry getDescriptorEntry(final Utf8Entry utf8Entry, final Utf8Entry utf8Entry2) {
        return getDescriptorEntry(utf8Entry, getSignatureEntry(utf8Entry2.stringValue()));
    }
    
    public static MemberEntry getMemberEntry(final byte b, final ClassEntry classEntry, final DescriptorEntry descriptorEntry) {
        final Map<String, MemberEntry> memberEntries = Utils.getTLGlobals().getMemberEntries();
        final String stringValue = MemberEntry.stringValueOf(b, classEntry, descriptorEntry);
        MemberEntry memberEntry = memberEntries.get(stringValue);
        if (memberEntry == null) {
            memberEntry = new MemberEntry(b, classEntry, descriptorEntry);
            assert memberEntry.stringValue().equals(stringValue) : memberEntry.stringValue() + " != " + stringValue;
            memberEntries.put(stringValue, memberEntry);
        }
        return memberEntry;
    }
    
    public static MethodHandleEntry getMethodHandleEntry(final byte b, final MemberEntry memberEntry) {
        final Map<String, MethodHandleEntry> methodHandleEntries = Utils.getTLGlobals().getMethodHandleEntries();
        final String stringValue = MethodHandleEntry.stringValueOf(b, memberEntry);
        MethodHandleEntry methodHandleEntry = methodHandleEntries.get(stringValue);
        if (methodHandleEntry == null) {
            methodHandleEntry = new MethodHandleEntry(b, memberEntry);
            assert methodHandleEntry.stringValue().equals(stringValue);
            methodHandleEntries.put(stringValue, methodHandleEntry);
        }
        return methodHandleEntry;
    }
    
    public static MethodTypeEntry getMethodTypeEntry(final SignatureEntry signatureEntry) {
        final Map<String, MethodTypeEntry> methodTypeEntries = Utils.getTLGlobals().getMethodTypeEntries();
        final String stringValue = signatureEntry.stringValue();
        MethodTypeEntry methodTypeEntry = methodTypeEntries.get(stringValue);
        if (methodTypeEntry == null) {
            methodTypeEntry = new MethodTypeEntry(signatureEntry);
            assert methodTypeEntry.stringValue().equals(stringValue);
            methodTypeEntries.put(stringValue, methodTypeEntry);
        }
        return methodTypeEntry;
    }
    
    public static MethodTypeEntry getMethodTypeEntry(final Utf8Entry utf8Entry) {
        return getMethodTypeEntry(getSignatureEntry(utf8Entry.stringValue()));
    }
    
    public static InvokeDynamicEntry getInvokeDynamicEntry(final BootstrapMethodEntry bootstrapMethodEntry, final DescriptorEntry descriptorEntry) {
        final Map<String, InvokeDynamicEntry> invokeDynamicEntries = Utils.getTLGlobals().getInvokeDynamicEntries();
        final String stringValue = InvokeDynamicEntry.stringValueOf(bootstrapMethodEntry, descriptorEntry);
        InvokeDynamicEntry invokeDynamicEntry = invokeDynamicEntries.get(stringValue);
        if (invokeDynamicEntry == null) {
            invokeDynamicEntry = new InvokeDynamicEntry(bootstrapMethodEntry, descriptorEntry);
            assert invokeDynamicEntry.stringValue().equals(stringValue);
            invokeDynamicEntries.put(stringValue, invokeDynamicEntry);
        }
        return invokeDynamicEntry;
    }
    
    public static BootstrapMethodEntry getBootstrapMethodEntry(final MethodHandleEntry methodHandleEntry, final Entry[] array) {
        final Map<String, BootstrapMethodEntry> bootstrapMethodEntries = Utils.getTLGlobals().getBootstrapMethodEntries();
        final String stringValue = BootstrapMethodEntry.stringValueOf(methodHandleEntry, array);
        BootstrapMethodEntry bootstrapMethodEntry = bootstrapMethodEntries.get(stringValue);
        if (bootstrapMethodEntry == null) {
            bootstrapMethodEntry = new BootstrapMethodEntry(methodHandleEntry, array);
            assert bootstrapMethodEntry.stringValue().equals(stringValue);
            bootstrapMethodEntries.put(stringValue, bootstrapMethodEntry);
        }
        return bootstrapMethodEntry;
    }
    
    static boolean isMemberTag(final byte b) {
        switch (b) {
            case 9:
            case 10:
            case 11: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static byte numberTagOf(final Number n) {
        if (n instanceof Integer) {
            return 3;
        }
        if (n instanceof Float) {
            return 4;
        }
        if (n instanceof Long) {
            return 5;
        }
        if (n instanceof Double) {
            return 6;
        }
        throw new RuntimeException("bad literal value " + n);
    }
    
    static boolean isRefKind(final byte b) {
        return 1 <= b && b <= 9;
    }
    
    static String qualifiedStringValue(final Entry entry, final Entry entry2) {
        return qualifiedStringValue(entry.stringValue(), entry2.stringValue());
    }
    
    static String qualifiedStringValue(final String s, final String s2) {
        assert s.indexOf(".") < 0;
        return s + "." + s2;
    }
    
    static int compareSignatures(final String s, final String s2) {
        return compareSignatures(s, s2, null, null);
    }
    
    static int compareSignatures(final String s, final String s2, String[] structureSignature, String[] structureSignature2) {
        final char char1 = s.charAt(0);
        final char char2 = s2.charAt(0);
        if (char1 != '(' && char2 == '(') {
            return -1;
        }
        if (char2 != '(' && char1 == '(') {
            return 1;
        }
        if (structureSignature == null) {
            structureSignature = structureSignature(s);
        }
        if (structureSignature2 == null) {
            structureSignature2 = structureSignature(s2);
        }
        if (structureSignature.length != structureSignature2.length) {
            return structureSignature.length - structureSignature2.length;
        }
        int length = structureSignature.length;
        while (--length >= 0) {
            final int compareTo = structureSignature[length].compareTo(structureSignature2[length]);
            if (compareTo != 0) {
                return compareTo;
            }
        }
        assert s.equals(s2);
        return 0;
    }
    
    static int countClassParts(final Utf8Entry utf8Entry) {
        int n = 0;
        final String stringValue = utf8Entry.stringValue();
        for (int i = 0; i < stringValue.length(); ++i) {
            if (stringValue.charAt(i) == 'L') {
                ++n;
            }
        }
        return n;
    }
    
    static String flattenSignature(final String[] array) {
        final String s = array[0];
        if (array.length == 1) {
            return s;
        }
        int length = s.length();
        for (int i = 1; i < array.length; ++i) {
            length += array[i].length();
        }
        final char[] array2 = new char[length];
        int n = 0;
        int n2 = 1;
        for (int j = 0; j < s.length(); ++j) {
            if ((array2[n++] = s.charAt(j)) == 'L') {
                final String s2 = array[n2++];
                s2.getChars(0, s2.length(), array2, n);
                n += s2.length();
            }
        }
        assert n == length;
        assert n2 == array.length;
        return new String(array2);
    }
    
    private static int skipTo(final char c, final String s, int index) {
        index = s.indexOf(c, index);
        return (index >= 0) ? index : s.length();
    }
    
    static String[] structureSignature(final String s) {
        final int index = s.indexOf(76);
        if (index < 0) {
            return new String[] { s };
        }
        char[] array = null;
        String[] array2 = null;
        for (int i = 0; i <= 1; ++i) {
            int n = 0;
            int n2 = 1;
            int skipTo = 0;
            int skipTo2 = 0;
            int n3 = 0;
            int n4;
            for (int j = index + 1; j > 0; j = s.indexOf(76, n4) + 1) {
                if (skipTo < j) {
                    skipTo = skipTo(';', s, j);
                }
                if (skipTo2 < j) {
                    skipTo2 = skipTo('<', s, j);
                }
                n4 = ((skipTo < skipTo2) ? skipTo : skipTo2);
                if (i != 0) {
                    s.getChars(n3, j, array, n);
                    array2[n2] = s.substring(j, n4);
                }
                n += j - n3;
                ++n2;
                n3 = n4;
            }
            if (i != 0) {
                s.getChars(n3, s.length(), array, n);
                break;
            }
            array = new char[n + (s.length() - n3)];
            array2 = new String[n2];
        }
        array2[0] = new String(array);
        return array2;
    }
    
    public static Index makeIndex(final String s, final Entry[] array) {
        return new Index(s, array);
    }
    
    public static Index makeIndex(final String s, final Collection<Entry> collection) {
        return new Index(s, collection);
    }
    
    public static void sort(final Index index) {
        index.clearIndex();
        Arrays.sort(index.cpMap);
        if (verbose() > 2) {
            System.out.println("sorted " + index.dumpString());
        }
    }
    
    public static Index[] partition(final Index index, final int[] array) {
        final ArrayList list = new ArrayList();
        final Entry[] cpMap = index.cpMap;
        assert array.length == cpMap.length;
        for (int i = 0; i < array.length; ++i) {
            final int j = array[i];
            if (j >= 0) {
                while (j >= list.size()) {
                    list.add(null);
                }
                List list2 = (List)list.get(j);
                if (list2 == null) {
                    list.set(j, list2 = new ArrayList());
                }
                list2.add(cpMap[i]);
            }
        }
        final Index[] array2 = new Index[list.size()];
        for (int k = 0; k < array2.length; ++k) {
            final List list3 = (List)list.get(k);
            if (list3 != null) {
                array2[k] = new Index(index.debugName + "/part#" + k, list3);
                assert array2[k].indexOf((Entry)list3.get(0)) == 0;
            }
        }
        return array2;
    }
    
    public static Index[] partitionByTag(final Index index) {
        final Entry[] cpMap = index.cpMap;
        final int[] array = new int[cpMap.length];
        for (int i = 0; i < array.length; ++i) {
            final Entry entry = cpMap[i];
            array[i] = ((entry == null) ? -1 : entry.tag);
        }
        Index[] partition = partition(index, array);
        for (int j = 0; j < partition.length; ++j) {
            if (partition[j] != null) {
                partition[j].debugName = tagName(j);
            }
        }
        if (partition.length < 19) {
            final Index[] array2 = new Index[19];
            System.arraycopy(partition, 0, array2, 0, partition.length);
            partition = array2;
        }
        return partition;
    }
    
    public static void completeReferencesIn(final Set<Entry> set, final boolean b) {
        completeReferencesIn(set, b, null);
    }
    
    public static void completeReferencesIn(final Set<Entry> set, final boolean b, final List<BootstrapMethodEntry> list) {
        set.remove(null);
        final ListIterator listIterator = new ArrayList(set).listIterator(set.size());
        while (listIterator.hasPrevious()) {
            Entry entry = (Entry)listIterator.previous();
            listIterator.remove();
            assert entry != null;
            if (b && entry.tag == 13) {
                final SignatureEntry signatureEntry = (SignatureEntry)entry;
                final Utf8Entry utf8Entry = signatureEntry.asUtf8Entry();
                set.remove(signatureEntry);
                set.add(utf8Entry);
                entry = utf8Entry;
            }
            if (list != null && entry.tag == 17) {
                final BootstrapMethodEntry bootstrapMethodEntry = (BootstrapMethodEntry)entry;
                set.remove(bootstrapMethodEntry);
                if (!list.contains(bootstrapMethodEntry)) {
                    list.add(bootstrapMethodEntry);
                }
            }
            int n = 0;
            while (true) {
                final Entry ref = entry.getRef(n);
                if (ref == null) {
                    break;
                }
                if (set.add(ref)) {
                    listIterator.add(ref);
                }
                ++n;
            }
        }
    }
    
    static double percent(final int n, final int n2) {
        return (int)(10000.0 * n / n2 + 0.5) / 100.0;
    }
    
    public static String tagName(final int n) {
        switch (n) {
            case 1: {
                return "Utf8";
            }
            case 3: {
                return "Integer";
            }
            case 4: {
                return "Float";
            }
            case 5: {
                return "Long";
            }
            case 6: {
                return "Double";
            }
            case 7: {
                return "Class";
            }
            case 8: {
                return "String";
            }
            case 9: {
                return "Fieldref";
            }
            case 10: {
                return "Methodref";
            }
            case 11: {
                return "InterfaceMethodref";
            }
            case 12: {
                return "NameandType";
            }
            case 15: {
                return "MethodHandle";
            }
            case 16: {
                return "MethodType";
            }
            case 18: {
                return "InvokeDynamic";
            }
            case 50: {
                return "**All";
            }
            case 0: {
                return "**None";
            }
            case 51: {
                return "**LoadableValue";
            }
            case 52: {
                return "**AnyMember";
            }
            case 53: {
                return "*FieldSpecific";
            }
            case 13: {
                return "*Signature";
            }
            case 17: {
                return "*BootstrapMethod";
            }
            default: {
                return "tag#" + n;
            }
        }
    }
    
    public static String refKindName(final int n) {
        switch (n) {
            case 1: {
                return "getField";
            }
            case 2: {
                return "getStatic";
            }
            case 3: {
                return "putField";
            }
            case 4: {
                return "putStatic";
            }
            case 5: {
                return "invokeVirtual";
            }
            case 6: {
                return "invokeStatic";
            }
            case 7: {
                return "invokeSpecial";
            }
            case 8: {
                return "newInvokeSpecial";
            }
            case 9: {
                return "invokeInterface";
            }
            default: {
                return "refKind#" + n;
            }
        }
    }
    
    private static boolean verifyTagOrder(final byte[] array) {
        byte b = -1;
        for (final byte b2 : array) {
            final byte b3 = ConstantPool.TAG_ORDER[b2];
            assert b3 > 0 : "tag not found: " + b2;
            assert ConstantPool.TAGS_IN_ORDER[b3 - 1] == b2 : "tag repeated: " + b2 + " => " + b3 + " => " + ConstantPool.TAGS_IN_ORDER[b3 - 1];
            assert b < b3 : "tags not in order: " + Arrays.toString(array) + " at " + b2;
            b = b3;
        }
        return true;
    }
    
    static {
        noRefs = new Entry[0];
        noClassRefs = new ClassEntry[0];
        TAGS_IN_ORDER = new byte[] { 1, 3, 4, 5, 6, 8, 7, 13, 12, 9, 10, 11, 15, 16, 17, 18 };
        TAG_ORDER = new byte[19];
        for (int i = 0; i < ConstantPool.TAGS_IN_ORDER.length; ++i) {
            ConstantPool.TAG_ORDER[ConstantPool.TAGS_IN_ORDER[i]] = (byte)(i + 1);
        }
        NUMBER_TAGS = new byte[] { 3, 4, 5, 6 };
        EXTRA_TAGS = new byte[] { 15, 16, 17, 18 };
        LOADABLE_VALUE_TAGS = new byte[] { 3, 4, 5, 6, 8, 7, 15, 16 };
        ANY_MEMBER_TAGS = new byte[] { 9, 10, 11 };
        FIELD_SPECIFIC_TAGS = new byte[] { 3, 4, 5, 6, 8 };
        assert verifyTagOrder(ConstantPool.TAGS_IN_ORDER) && verifyTagOrder(ConstantPool.NUMBER_TAGS) && verifyTagOrder(ConstantPool.EXTRA_TAGS) && verifyTagOrder(ConstantPool.LOADABLE_VALUE_TAGS) && verifyTagOrder(ConstantPool.ANY_MEMBER_TAGS) && verifyTagOrder(ConstantPool.FIELD_SPECIFIC_TAGS);
    }
    
    public abstract static class Entry implements Comparable<Object>
    {
        protected final byte tag;
        protected int valueHash;
        
        protected Entry(final byte tag) {
            this.tag = tag;
        }
        
        public final byte getTag() {
            return this.tag;
        }
        
        public final boolean tagEquals(final int n) {
            return this.getTag() == n;
        }
        
        public Entry getRef(final int n) {
            return null;
        }
        
        public boolean eq(final Entry entry) {
            assert entry != null;
            return this == entry || this.equals(entry);
        }
        
        @Override
        public abstract boolean equals(final Object p0);
        
        @Override
        public final int hashCode() {
            if (this.valueHash == 0) {
                this.valueHash = this.computeValueHash();
                if (this.valueHash == 0) {
                    this.valueHash = 1;
                }
            }
            return this.valueHash;
        }
        
        protected abstract int computeValueHash();
        
        @Override
        public abstract int compareTo(final Object p0);
        
        protected int superCompareTo(final Object o) {
            final Entry entry = (Entry)o;
            if (this.tag != entry.tag) {
                return ConstantPool.TAG_ORDER[this.tag] - ConstantPool.TAG_ORDER[entry.tag];
            }
            return 0;
        }
        
        public final boolean isDoubleWord() {
            return this.tag == 6 || this.tag == 5;
        }
        
        public final boolean tagMatches(final int n) {
            if (this.tag == n) {
                return true;
            }
            byte[] array = null;
            switch (n) {
                case 50: {
                    return true;
                }
                case 13: {
                    return this.tag == 1;
                }
                case 51: {
                    array = ConstantPool.LOADABLE_VALUE_TAGS;
                    break;
                }
                case 52: {
                    array = ConstantPool.ANY_MEMBER_TAGS;
                    break;
                }
                case 53: {
                    array = ConstantPool.FIELD_SPECIFIC_TAGS;
                    break;
                }
                default: {
                    return false;
                }
            }
            final byte[] array2 = array;
            for (int length = array2.length, i = 0; i < length; ++i) {
                if (array2[i] == this.tag) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public String toString() {
            String s = this.stringValue();
            if (ConstantPool.verbose() > 4) {
                if (this.valueHash != 0) {
                    s = s + " hash=" + this.valueHash;
                }
                s = s + " id=" + System.identityHashCode(this);
            }
            return ConstantPool.tagName(this.tag) + "=" + s;
        }
        
        public abstract String stringValue();
    }
    
    public static class Utf8Entry extends Entry
    {
        final String value;
        
        Utf8Entry(final String s) {
            super((byte)1);
            this.value = s.intern();
            this.hashCode();
        }
        
        @Override
        protected int computeValueHash() {
            return this.value.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            return o != null && o.getClass() == Utf8Entry.class && ((Utf8Entry)o).value.equals(this.value);
        }
        
        @Override
        public int compareTo(final Object o) {
            int n = this.superCompareTo(o);
            if (n == 0) {
                n = this.value.compareTo(((Utf8Entry)o).value);
            }
            return n;
        }
        
        @Override
        public String stringValue() {
            return this.value;
        }
    }
    
    public abstract static class LiteralEntry extends Entry
    {
        protected LiteralEntry(final byte b) {
            super(b);
        }
        
        public abstract Comparable<?> literalValue();
    }
    
    public static class NumberEntry extends LiteralEntry
    {
        final Number value;
        
        NumberEntry(final Number value) {
            super(ConstantPool.numberTagOf(value));
            this.value = value;
            this.hashCode();
        }
        
        @Override
        protected int computeValueHash() {
            return this.value.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            return o != null && o.getClass() == NumberEntry.class && ((NumberEntry)o).value.equals(this.value);
        }
        
        @Override
        public int compareTo(final Object o) {
            int n = this.superCompareTo(o);
            if (n == 0) {
                n = ((Comparable)this.value).compareTo(((NumberEntry)o).value);
            }
            return n;
        }
        
        public Number numberValue() {
            return this.value;
        }
        
        @Override
        public Comparable<?> literalValue() {
            return (Comparable)this.value;
        }
        
        @Override
        public String stringValue() {
            return this.value.toString();
        }
    }
    
    public static class StringEntry extends LiteralEntry
    {
        final Utf8Entry ref;
        
        @Override
        public Entry getRef(final int n) {
            return (n == 0) ? this.ref : null;
        }
        
        StringEntry(final Entry entry) {
            super((byte)8);
            this.ref = (Utf8Entry)entry;
            this.hashCode();
        }
        
        @Override
        protected int computeValueHash() {
            return this.ref.hashCode() + this.tag;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o != null && o.getClass() == StringEntry.class && ((StringEntry)o).ref.eq(this.ref);
        }
        
        @Override
        public int compareTo(final Object o) {
            int n = this.superCompareTo(o);
            if (n == 0) {
                n = this.ref.compareTo(((StringEntry)o).ref);
            }
            return n;
        }
        
        @Override
        public Comparable<?> literalValue() {
            return this.ref.stringValue();
        }
        
        @Override
        public String stringValue() {
            return this.ref.stringValue();
        }
    }
    
    public static class ClassEntry extends Entry
    {
        final Utf8Entry ref;
        
        @Override
        public Entry getRef(final int n) {
            return (n == 0) ? this.ref : null;
        }
        
        @Override
        protected int computeValueHash() {
            return this.ref.hashCode() + this.tag;
        }
        
        ClassEntry(final Entry entry) {
            super((byte)7);
            this.ref = (Utf8Entry)entry;
            this.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            return o != null && o.getClass() == ClassEntry.class && ((ClassEntry)o).ref.eq(this.ref);
        }
        
        @Override
        public int compareTo(final Object o) {
            int n = this.superCompareTo(o);
            if (n == 0) {
                n = this.ref.compareTo(((ClassEntry)o).ref);
            }
            return n;
        }
        
        @Override
        public String stringValue() {
            return this.ref.stringValue();
        }
    }
    
    public static class DescriptorEntry extends Entry
    {
        final Utf8Entry nameRef;
        final SignatureEntry typeRef;
        
        @Override
        public Entry getRef(final int n) {
            if (n == 0) {
                return this.nameRef;
            }
            if (n == 1) {
                return this.typeRef;
            }
            return null;
        }
        
        DescriptorEntry(final Entry entry, Entry signatureEntry) {
            super((byte)12);
            if (signatureEntry instanceof Utf8Entry) {
                signatureEntry = ConstantPool.getSignatureEntry(signatureEntry.stringValue());
            }
            this.nameRef = (Utf8Entry)entry;
            this.typeRef = (SignatureEntry)signatureEntry;
            this.hashCode();
        }
        
        @Override
        protected int computeValueHash() {
            final int hashCode = this.typeRef.hashCode();
            return this.nameRef.hashCode() + (hashCode << 8) ^ hashCode;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == null || o.getClass() != DescriptorEntry.class) {
                return false;
            }
            final DescriptorEntry descriptorEntry = (DescriptorEntry)o;
            return this.nameRef.eq(descriptorEntry.nameRef) && this.typeRef.eq(descriptorEntry.typeRef);
        }
        
        @Override
        public int compareTo(final Object o) {
            int n = this.superCompareTo(o);
            if (n == 0) {
                final DescriptorEntry descriptorEntry = (DescriptorEntry)o;
                n = this.typeRef.compareTo(descriptorEntry.typeRef);
                if (n == 0) {
                    n = this.nameRef.compareTo(descriptorEntry.nameRef);
                }
            }
            return n;
        }
        
        @Override
        public String stringValue() {
            return stringValueOf(this.nameRef, this.typeRef);
        }
        
        static String stringValueOf(final Entry entry, final Entry entry2) {
            return ConstantPool.qualifiedStringValue(entry2, entry);
        }
        
        public String prettyString() {
            return this.nameRef.stringValue() + this.typeRef.prettyString();
        }
        
        public boolean isMethod() {
            return this.typeRef.isMethod();
        }
        
        public byte getLiteralTag() {
            return this.typeRef.getLiteralTag();
        }
    }
    
    public static class MemberEntry extends Entry
    {
        final ClassEntry classRef;
        final DescriptorEntry descRef;
        
        @Override
        public Entry getRef(final int n) {
            if (n == 0) {
                return this.classRef;
            }
            if (n == 1) {
                return this.descRef;
            }
            return null;
        }
        
        @Override
        protected int computeValueHash() {
            final int hashCode = this.descRef.hashCode();
            return this.classRef.hashCode() + (hashCode << 8) ^ hashCode;
        }
        
        MemberEntry(final byte b, final ClassEntry classRef, final DescriptorEntry descRef) {
            super(b);
            assert ConstantPool.isMemberTag(b);
            this.classRef = classRef;
            this.descRef = descRef;
            this.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == null || o.getClass() != MemberEntry.class) {
                return false;
            }
            final MemberEntry memberEntry = (MemberEntry)o;
            return this.classRef.eq(memberEntry.classRef) && this.descRef.eq(memberEntry.descRef);
        }
        
        @Override
        public int compareTo(final Object o) {
            int n = this.superCompareTo(o);
            if (n == 0) {
                final MemberEntry memberEntry = (MemberEntry)o;
                if (Utils.SORT_MEMBERS_DESCR_MAJOR) {
                    n = this.descRef.compareTo(memberEntry.descRef);
                }
                if (n == 0) {
                    n = this.classRef.compareTo(memberEntry.classRef);
                }
                if (n == 0) {
                    n = this.descRef.compareTo(memberEntry.descRef);
                }
            }
            return n;
        }
        
        @Override
        public String stringValue() {
            return stringValueOf(this.tag, this.classRef, this.descRef);
        }
        
        static String stringValueOf(final byte b, final ClassEntry classEntry, final DescriptorEntry descriptorEntry) {
            assert ConstantPool.isMemberTag(b);
            String string = null;
            switch (b) {
                case 9: {
                    string = "Field:";
                    break;
                }
                case 10: {
                    string = "Method:";
                    break;
                }
                case 11: {
                    string = "IMethod:";
                    break;
                }
                default: {
                    string = b + "???";
                    break;
                }
            }
            return string + ConstantPool.qualifiedStringValue(classEntry, descriptorEntry);
        }
        
        public boolean isMethod() {
            return this.descRef.isMethod();
        }
    }
    
    public static class SignatureEntry extends Entry
    {
        final Utf8Entry formRef;
        final ClassEntry[] classRefs;
        String value;
        Utf8Entry asUtf8Entry;
        
        @Override
        public Entry getRef(final int n) {
            if (n == 0) {
                return this.formRef;
            }
            return (n - 1 < this.classRefs.length) ? this.classRefs[n - 1] : null;
        }
        
        SignatureEntry(String intern) {
            super((byte)13);
            intern = intern.intern();
            this.value = intern;
            final String[] structureSignature = ConstantPool.structureSignature(intern);
            this.formRef = ConstantPool.getUtf8Entry(structureSignature[0]);
            this.classRefs = new ClassEntry[structureSignature.length - 1];
            for (int i = 1; i < structureSignature.length; ++i) {
                this.classRefs[i - 1] = ConstantPool.getClassEntry(structureSignature[i]);
            }
            this.hashCode();
        }
        
        @Override
        protected int computeValueHash() {
            this.stringValue();
            return this.value.hashCode() + this.tag;
        }
        
        public Utf8Entry asUtf8Entry() {
            if (this.asUtf8Entry == null) {
                this.asUtf8Entry = ConstantPool.getUtf8Entry(this.stringValue());
            }
            return this.asUtf8Entry;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o != null && o.getClass() == SignatureEntry.class && ((SignatureEntry)o).value.equals(this.value);
        }
        
        @Override
        public int compareTo(final Object o) {
            int n = this.superCompareTo(o);
            if (n == 0) {
                n = ConstantPool.compareSignatures(this.value, ((SignatureEntry)o).value);
            }
            return n;
        }
        
        @Override
        public String stringValue() {
            if (this.value == null) {
                this.value = stringValueOf(this.formRef, this.classRefs);
            }
            return this.value;
        }
        
        static String stringValueOf(final Utf8Entry utf8Entry, final ClassEntry[] array) {
            final String[] array2 = new String[1 + array.length];
            array2[0] = utf8Entry.stringValue();
            for (int i = 1; i < array2.length; ++i) {
                array2[i] = array[i - 1].stringValue();
            }
            return ConstantPool.flattenSignature(array2).intern();
        }
        
        public int computeSize(final boolean b) {
            final String stringValue = this.formRef.stringValue();
            int n = 0;
            int index = 1;
            if (this.isMethod()) {
                n = 1;
                index = stringValue.indexOf(41);
            }
            int n2 = 0;
            for (int i = n; i < index; ++i) {
                switch (stringValue.charAt(i)) {
                    case 'D':
                    case 'J': {
                        if (b) {
                            ++n2;
                            break;
                        }
                        break;
                    }
                    case '[': {
                        while (stringValue.charAt(i) == '[') {
                            ++i;
                        }
                        break;
                    }
                    case ';': {
                        continue;
                    }
                    default: {
                        assert 0 <= "BSCIJFDZLV([".indexOf(stringValue.charAt(i));
                        break;
                    }
                }
                ++n2;
            }
            return n2;
        }
        
        public boolean isMethod() {
            return this.formRef.stringValue().charAt(0) == '(';
        }
        
        public byte getLiteralTag() {
            switch (this.formRef.stringValue().charAt(0)) {
                case 'I': {
                    return 3;
                }
                case 'J': {
                    return 5;
                }
                case 'F': {
                    return 4;
                }
                case 'D': {
                    return 6;
                }
                case 'B':
                case 'C':
                case 'S':
                case 'Z': {
                    return 3;
                }
                case 'L': {
                    return 8;
                }
                default: {
                    assert false;
                    return 0;
                }
            }
        }
        
        public String prettyString() {
            String s;
            if (this.isMethod()) {
                final String stringValue = this.formRef.stringValue();
                s = stringValue.substring(0, 1 + stringValue.indexOf(41));
            }
            else {
                s = "/" + this.formRef.stringValue();
            }
            int index;
            while ((index = s.indexOf(59)) >= 0) {
                s = s.substring(0, index) + s.substring(index + 1);
            }
            return s;
        }
    }
    
    public static class MethodHandleEntry extends Entry
    {
        final int refKind;
        final MemberEntry memRef;
        
        @Override
        public Entry getRef(final int n) {
            return (n == 0) ? this.memRef : null;
        }
        
        @Override
        protected int computeValueHash() {
            final int refKind = this.refKind;
            return this.memRef.hashCode() + (refKind << 8) ^ refKind;
        }
        
        MethodHandleEntry(final byte refKind, final MemberEntry memRef) {
            super((byte)15);
            assert ConstantPool.isRefKind(refKind);
            this.refKind = refKind;
            this.memRef = memRef;
            this.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == null || o.getClass() != MethodHandleEntry.class) {
                return false;
            }
            final MethodHandleEntry methodHandleEntry = (MethodHandleEntry)o;
            return this.refKind == methodHandleEntry.refKind && this.memRef.eq(methodHandleEntry.memRef);
        }
        
        @Override
        public int compareTo(final Object o) {
            int n = this.superCompareTo(o);
            if (n == 0) {
                final MethodHandleEntry methodHandleEntry = (MethodHandleEntry)o;
                if (Utils.SORT_HANDLES_KIND_MAJOR) {
                    n = this.refKind - methodHandleEntry.refKind;
                }
                if (n == 0) {
                    n = this.memRef.compareTo(methodHandleEntry.memRef);
                }
                if (n == 0) {
                    n = this.refKind - methodHandleEntry.refKind;
                }
            }
            return n;
        }
        
        public static String stringValueOf(final int n, final MemberEntry memberEntry) {
            return ConstantPool.refKindName(n) + ":" + memberEntry.stringValue();
        }
        
        @Override
        public String stringValue() {
            return stringValueOf(this.refKind, this.memRef);
        }
    }
    
    public static class MethodTypeEntry extends Entry
    {
        final SignatureEntry typeRef;
        
        @Override
        public Entry getRef(final int n) {
            return (n == 0) ? this.typeRef : null;
        }
        
        @Override
        protected int computeValueHash() {
            return this.typeRef.hashCode() + this.tag;
        }
        
        MethodTypeEntry(final SignatureEntry typeRef) {
            super((byte)16);
            this.typeRef = typeRef;
            this.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            return o != null && o.getClass() == MethodTypeEntry.class && this.typeRef.eq(((MethodTypeEntry)o).typeRef);
        }
        
        @Override
        public int compareTo(final Object o) {
            int n = this.superCompareTo(o);
            if (n == 0) {
                n = this.typeRef.compareTo(((MethodTypeEntry)o).typeRef);
            }
            return n;
        }
        
        @Override
        public String stringValue() {
            return this.typeRef.stringValue();
        }
    }
    
    public static class InvokeDynamicEntry extends Entry
    {
        final BootstrapMethodEntry bssRef;
        final DescriptorEntry descRef;
        
        @Override
        public Entry getRef(final int n) {
            if (n == 0) {
                return this.bssRef;
            }
            if (n == 1) {
                return this.descRef;
            }
            return null;
        }
        
        @Override
        protected int computeValueHash() {
            final int hashCode = this.descRef.hashCode();
            return this.bssRef.hashCode() + (hashCode << 8) ^ hashCode;
        }
        
        InvokeDynamicEntry(final BootstrapMethodEntry bssRef, final DescriptorEntry descRef) {
            super((byte)18);
            this.bssRef = bssRef;
            this.descRef = descRef;
            this.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == null || o.getClass() != InvokeDynamicEntry.class) {
                return false;
            }
            final InvokeDynamicEntry invokeDynamicEntry = (InvokeDynamicEntry)o;
            return this.bssRef.eq(invokeDynamicEntry.bssRef) && this.descRef.eq(invokeDynamicEntry.descRef);
        }
        
        @Override
        public int compareTo(final Object o) {
            int n = this.superCompareTo(o);
            if (n == 0) {
                final InvokeDynamicEntry invokeDynamicEntry = (InvokeDynamicEntry)o;
                if (Utils.SORT_INDY_BSS_MAJOR) {
                    n = this.bssRef.compareTo(invokeDynamicEntry.bssRef);
                }
                if (n == 0) {
                    n = this.descRef.compareTo(invokeDynamicEntry.descRef);
                }
                if (n == 0) {
                    n = this.bssRef.compareTo(invokeDynamicEntry.bssRef);
                }
            }
            return n;
        }
        
        @Override
        public String stringValue() {
            return stringValueOf(this.bssRef, this.descRef);
        }
        
        static String stringValueOf(final BootstrapMethodEntry bootstrapMethodEntry, final DescriptorEntry descriptorEntry) {
            return "Indy:" + bootstrapMethodEntry.stringValue() + "." + descriptorEntry.stringValue();
        }
    }
    
    public static class BootstrapMethodEntry extends Entry
    {
        final MethodHandleEntry bsmRef;
        final Entry[] argRefs;
        
        @Override
        public Entry getRef(final int n) {
            if (n == 0) {
                return this.bsmRef;
            }
            if (n - 1 < this.argRefs.length) {
                return this.argRefs[n - 1];
            }
            return null;
        }
        
        @Override
        protected int computeValueHash() {
            final int hashCode = this.bsmRef.hashCode();
            return Arrays.hashCode(this.argRefs) + (hashCode << 8) ^ hashCode;
        }
        
        BootstrapMethodEntry(final MethodHandleEntry bsmRef, final Entry[] array) {
            super((byte)17);
            this.bsmRef = bsmRef;
            this.argRefs = array.clone();
            this.hashCode();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == null || o.getClass() != BootstrapMethodEntry.class) {
                return false;
            }
            final BootstrapMethodEntry bootstrapMethodEntry = (BootstrapMethodEntry)o;
            return this.bsmRef.eq(bootstrapMethodEntry.bsmRef) && Arrays.equals(this.argRefs, bootstrapMethodEntry.argRefs);
        }
        
        @Override
        public int compareTo(final Object o) {
            int n = this.superCompareTo(o);
            if (n == 0) {
                final BootstrapMethodEntry bootstrapMethodEntry = (BootstrapMethodEntry)o;
                if (Utils.SORT_BSS_BSM_MAJOR) {
                    n = this.bsmRef.compareTo(bootstrapMethodEntry.bsmRef);
                }
                if (n == 0) {
                    n = compareArgArrays(this.argRefs, bootstrapMethodEntry.argRefs);
                }
                if (n == 0) {
                    n = this.bsmRef.compareTo(bootstrapMethodEntry.bsmRef);
                }
            }
            return n;
        }
        
        @Override
        public String stringValue() {
            return stringValueOf(this.bsmRef, this.argRefs);
        }
        
        static String stringValueOf(final MethodHandleEntry methodHandleEntry, final Entry[] array) {
            final StringBuffer sb = new StringBuffer(methodHandleEntry.stringValue());
            char c = '<';
            for (int length = array.length, i = 0; i < length; ++i) {
                sb.append(c).append(array[i].stringValue());
                c = ';';
            }
            if (c == '<') {
                sb.append(c);
            }
            sb.append('>');
            return sb.toString();
        }
        
        static int compareArgArrays(final Entry[] array, final Entry[] array2) {
            int compareTo = array.length - array2.length;
            if (compareTo != 0) {
                return compareTo;
            }
            for (int i = 0; i < array.length; ++i) {
                compareTo = array[i].compareTo(array2[i]);
                if (compareTo != 0) {
                    break;
                }
            }
            return compareTo;
        }
    }
    
    public static final class Index extends AbstractList<Entry>
    {
        protected String debugName;
        protected Entry[] cpMap;
        protected boolean flattenSigs;
        protected Entry[] indexKey;
        protected int[] indexValue;
        
        protected Entry[] getMap() {
            return this.cpMap;
        }
        
        protected Index(final String debugName) {
            this.debugName = debugName;
        }
        
        protected Index(final String s, final Entry[] map) {
            this(s);
            this.setMap(map);
        }
        
        protected void setMap(final Entry[] cpMap) {
            this.clearIndex();
            this.cpMap = cpMap;
        }
        
        protected Index(final String s, final Collection<Entry> map) {
            this(s);
            this.setMap(map);
        }
        
        protected void setMap(final Collection<Entry> collection) {
            collection.toArray(this.cpMap = new Entry[collection.size()]);
            this.setMap(this.cpMap);
        }
        
        @Override
        public int size() {
            return this.cpMap.length;
        }
        
        @Override
        public Entry get(final int n) {
            return this.cpMap[n];
        }
        
        public Entry getEntry(final int n) {
            return this.cpMap[n];
        }
        
        private int findIndexOf(final Entry entry) {
            if (this.indexKey == null) {
                this.initializeIndex();
            }
            final int indexLocation = this.findIndexLocation(entry);
            if (this.indexKey[indexLocation] != entry) {
                if (this.flattenSigs && entry.tag == 13) {
                    return this.findIndexOf(((SignatureEntry)entry).asUtf8Entry());
                }
                return -1;
            }
            else {
                final int n = this.indexValue[indexLocation];
                assert entry.equals(this.cpMap[n]);
                return n;
            }
        }
        
        public boolean contains(final Entry entry) {
            return this.findIndexOf(entry) >= 0;
        }
        
        public int indexOf(final Entry entry) {
            final int index = this.findIndexOf(entry);
            if (index < 0 && ConstantPool.verbose() > 0) {
                System.out.println("not found: " + entry);
                System.out.println("       in: " + this.dumpString());
                Thread.dumpStack();
            }
            assert index >= 0;
            return index;
        }
        
        public int lastIndexOf(final Entry entry) {
            return this.indexOf(entry);
        }
        
        public boolean assertIsSorted() {
            for (int i = 1; i < this.cpMap.length; ++i) {
                if (this.cpMap[i - 1].compareTo(this.cpMap[i]) > 0) {
                    System.out.println("Not sorted at " + (i - 1) + "/" + i + ": " + this.dumpString());
                    return false;
                }
            }
            return true;
        }
        
        protected void clearIndex() {
            this.indexKey = null;
            this.indexValue = null;
        }
        
        private int findIndexLocation(final Entry entry) {
            final int length = this.indexKey.length;
            final int hashCode = entry.hashCode();
            int n = hashCode & length - 1;
            final int n2 = (hashCode >>> 8 | 0x1) & length - 1;
            while (true) {
                final Entry entry2 = this.indexKey[n];
                if (entry2 == entry || entry2 == null) {
                    break;
                }
                n += n2;
                if (n < length) {
                    continue;
                }
                n -= length;
            }
            return n;
        }
        
        private void initializeIndex() {
            if (ConstantPool.verbose() > 2) {
                System.out.println("initialize Index " + this.debugName + " [" + this.size() + "]");
            }
            int n;
            int i;
            for (n = (int)((this.cpMap.length + 10) * 1.5), i = 1; i < n; i <<= 1) {}
            this.indexKey = new Entry[i];
            this.indexValue = new int[i];
            for (int j = 0; j < this.cpMap.length; ++j) {
                final Entry entry = this.cpMap[j];
                if (entry != null) {
                    final int indexLocation = this.findIndexLocation(entry);
                    assert this.indexKey[indexLocation] == null;
                    this.indexKey[indexLocation] = entry;
                    this.indexValue[indexLocation] = j;
                }
            }
        }
        
        public Entry[] toArray(final Entry[] array) {
            final int size = this.size();
            if (array.length < size) {
                return super.toArray(array);
            }
            System.arraycopy(this.cpMap, 0, array, 0, size);
            if (array.length > size) {
                array[size] = null;
            }
            return array;
        }
        
        @Override
        public Entry[] toArray() {
            return this.toArray(new Entry[this.size()]);
        }
        
        public Object clone() {
            return new Index(this.debugName, this.cpMap.clone());
        }
        
        @Override
        public String toString() {
            return "Index " + this.debugName + " [" + this.size() + "]";
        }
        
        public String dumpString() {
            String s = this.toString() + " {\n";
            for (int i = 0; i < this.cpMap.length; ++i) {
                s = s + "    " + i + ": " + this.cpMap[i] + "\n";
            }
            return s + "}";
        }
    }
    
    public static class IndexGroup
    {
        private Index[] indexByTag;
        private Index[] indexByTagGroup;
        private int[] untypedFirstIndexByTag;
        private int totalSizeQQ;
        private Index[][] indexByTagAndClass;
        static final /* synthetic */ boolean $assertionsDisabled;
        
        public IndexGroup() {
            this.indexByTag = new Index[19];
        }
        
        private Index makeTagGroupIndex(final byte b, final byte[] array) {
            if (this.indexByTagGroup == null) {
                this.indexByTagGroup = new Index[4];
            }
            final int n = b - 50;
            assert this.indexByTagGroup[n] == null;
            int n2 = 0;
            Entry[] array2 = null;
            for (int i = 1; i <= 2; ++i) {
                this.untypedIndexOf(null);
                for (final byte b2 : array) {
                    final Index index = this.indexByTag[b2];
                    if (index != null) {
                        final int length2 = index.cpMap.length;
                        if (length2 != 0) {
                            Label_0169: {
                                if (!IndexGroup.$assertionsDisabled) {
                                    if (b == 50) {
                                        if (n2 == this.untypedFirstIndexByTag[b2]) {
                                            break Label_0169;
                                        }
                                    }
                                    else if (n2 < this.untypedFirstIndexByTag[b2]) {
                                        break Label_0169;
                                    }
                                    throw new AssertionError();
                                }
                            }
                            if (array2 != null) {
                                assert array2[n2] == null;
                                assert array2[n2 + length2 - 1] == null;
                                System.arraycopy(index.cpMap, 0, array2, n2, length2);
                            }
                            n2 += length2;
                        }
                    }
                }
                if (array2 == null) {
                    assert i == 1;
                    array2 = new Entry[n2];
                    n2 = 0;
                }
            }
            return this.indexByTagGroup[n] = new Index(ConstantPool.tagName(b), array2);
        }
        
        public int untypedIndexOf(final Entry entry) {
            if (this.untypedFirstIndexByTag == null) {
                this.untypedFirstIndexByTag = new int[20];
                int n = 0;
                for (int i = 0; i < ConstantPool.TAGS_IN_ORDER.length; ++i) {
                    final byte b = ConstantPool.TAGS_IN_ORDER[i];
                    final Index index = this.indexByTag[b];
                    if (index != null) {
                        final int length = index.cpMap.length;
                        this.untypedFirstIndexByTag[b] = n;
                        n += length;
                    }
                }
                this.untypedFirstIndexByTag[19] = n;
            }
            if (entry == null) {
                return -1;
            }
            final byte tag = entry.tag;
            final Index index2 = this.indexByTag[tag];
            if (index2 == null) {
                return -1;
            }
            int access$000 = index2.findIndexOf(entry);
            if (access$000 >= 0) {
                access$000 += this.untypedFirstIndexByTag[tag];
            }
            return access$000;
        }
        
        public void initIndexByTag(final byte b, final Index index) {
            assert this.indexByTag[b] == null;
            final Entry[] cpMap = index.cpMap;
            for (int i = 0; i < cpMap.length; ++i) {
                assert cpMap[i].tag == b;
            }
            if (b == 1 && !IndexGroup.$assertionsDisabled && cpMap.length != 0 && !cpMap[0].stringValue().equals("")) {
                throw new AssertionError();
            }
            this.indexByTag[b] = index;
            this.untypedFirstIndexByTag = null;
            this.indexByTagGroup = null;
            if (this.indexByTagAndClass != null) {
                this.indexByTagAndClass[b] = null;
            }
        }
        
        public Index getIndexByTag(final byte b) {
            if (b >= 50) {
                return this.getIndexByTagGroup(b);
            }
            Index index = this.indexByTag[b];
            if (index == null) {
                index = new Index(ConstantPool.tagName(b), new Entry[0]);
                this.indexByTag[b] = index;
            }
            return index;
        }
        
        private Index getIndexByTagGroup(final byte b) {
            if (this.indexByTagGroup != null) {
                final Index index = this.indexByTagGroup[b - 50];
                if (index != null) {
                    return index;
                }
            }
            switch (b) {
                case 50: {
                    return this.makeTagGroupIndex((byte)50, ConstantPool.TAGS_IN_ORDER);
                }
                case 51: {
                    return this.makeTagGroupIndex((byte)51, ConstantPool.LOADABLE_VALUE_TAGS);
                }
                case 52: {
                    return this.makeTagGroupIndex((byte)52, ConstantPool.ANY_MEMBER_TAGS);
                }
                case 53: {
                    return null;
                }
                default: {
                    throw new AssertionError((Object)("bad tag group " + b));
                }
            }
        }
        
        public Index getMemberIndex(final byte b, final ClassEntry classEntry) {
            if (classEntry == null) {
                throw new RuntimeException("missing class reference for " + ConstantPool.tagName(b));
            }
            if (this.indexByTagAndClass == null) {
                this.indexByTagAndClass = new Index[19][];
            }
            final Index indexByTag = this.getIndexByTag((byte)7);
            Index[] partition = this.indexByTagAndClass[b];
            if (partition == null) {
                final Index indexByTag2 = this.getIndexByTag(b);
                final int[] array = new int[indexByTag2.size()];
                for (int i = 0; i < array.length; ++i) {
                    array[i] = indexByTag.indexOf(((MemberEntry)indexByTag2.get(i)).classRef);
                }
                partition = ConstantPool.partition(indexByTag2, array);
                for (int j = 0; j < partition.length; ++j) {
                    assert !(!partition[j].assertIsSorted());
                }
                this.indexByTagAndClass[b] = partition;
            }
            return partition[indexByTag.indexOf(classEntry)];
        }
        
        public int getOverloadingIndex(final MemberEntry memberEntry) {
            final Index memberIndex = this.getMemberIndex(memberEntry.tag, memberEntry.classRef);
            final Utf8Entry nameRef = memberEntry.descRef.nameRef;
            int n = 0;
            for (int i = 0; i < memberIndex.cpMap.length; ++i) {
                final MemberEntry memberEntry2 = (MemberEntry)memberIndex.cpMap[i];
                if (memberEntry2.equals(memberEntry)) {
                    return n;
                }
                if (memberEntry2.descRef.nameRef.equals(nameRef)) {
                    ++n;
                }
            }
            throw new RuntimeException("should not reach here");
        }
        
        public MemberEntry getOverloadingForIndex(final byte b, final ClassEntry classEntry, final String s, final int n) {
            assert s.equals(s.intern());
            final Index memberIndex = this.getMemberIndex(b, classEntry);
            int n2 = 0;
            for (int i = 0; i < memberIndex.cpMap.length; ++i) {
                final MemberEntry memberEntry = (MemberEntry)memberIndex.cpMap[i];
                if (memberEntry.descRef.nameRef.stringValue().equals(s)) {
                    if (n2 == n) {
                        return memberEntry;
                    }
                    ++n2;
                }
            }
            throw new RuntimeException("should not reach here");
        }
        
        public boolean haveNumbers() {
            final byte[] number_TAGS = ConstantPool.NUMBER_TAGS;
            for (int length = number_TAGS.length, i = 0; i < length; ++i) {
                if (this.getIndexByTag(number_TAGS[i]).size() > 0) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean haveExtraTags() {
            final byte[] extra_TAGS = ConstantPool.EXTRA_TAGS;
            for (int length = extra_TAGS.length, i = 0; i < length; ++i) {
                if (this.getIndexByTag(extra_TAGS[i]).size() > 0) {
                    return true;
                }
            }
            return false;
        }
    }
}
