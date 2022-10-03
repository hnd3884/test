package com.sun.java.util.jar.pack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.io.IOException;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.Map;
import java.io.DataInputStream;

class ClassReader
{
    int verbose;
    Package pkg;
    Package.Class cls;
    long inPos;
    long constantPoolLimit;
    DataInputStream in;
    Map<Attribute.Layout, Attribute> attrDefs;
    Map<Attribute.Layout, String> attrCommands;
    String unknownAttrCommand;
    private static final ConstantPool.Entry INVALID_ENTRY;
    boolean haveUnresolvedEntry;
    
    ClassReader(final Package.Class cls, final InputStream inputStream) throws IOException {
        this.constantPoolLimit = -1L;
        this.unknownAttrCommand = "error";
        this.pkg = cls.getPackage();
        this.cls = cls;
        this.verbose = this.pkg.verbose;
        this.in = new DataInputStream(new FilterInputStream(inputStream) {
            @Override
            public int read(final byte[] array, final int n, final int n2) throws IOException {
                final int read = super.read(array, n, n2);
                if (read >= 0) {
                    final ClassReader this$0 = ClassReader.this;
                    this$0.inPos += read;
                }
                return read;
            }
            
            @Override
            public int read() throws IOException {
                final int read = super.read();
                if (read >= 0) {
                    final ClassReader this$0 = ClassReader.this;
                    ++this$0.inPos;
                }
                return read;
            }
            
            @Override
            public long skip(final long n) throws IOException {
                final long skip = super.skip(n);
                if (skip >= 0L) {
                    final ClassReader this$0 = ClassReader.this;
                    this$0.inPos += skip;
                }
                return skip;
            }
        });
    }
    
    public void setAttrDefs(final Map<Attribute.Layout, Attribute> attrDefs) {
        this.attrDefs = attrDefs;
    }
    
    public void setAttrCommands(final Map<Attribute.Layout, String> attrCommands) {
        this.attrCommands = attrCommands;
    }
    
    private void skip(final int n, final String s) throws IOException {
        Utils.log.warning("skipping " + n + " bytes of " + s);
        long n2;
        long skip;
        for (n2 = 0L; n2 < n; n2 += skip) {
            skip = this.in.skip(n - n2);
            assert skip > 0L;
        }
        assert n2 == n;
    }
    
    private int readUnsignedShort() throws IOException {
        return this.in.readUnsignedShort();
    }
    
    private int readInt() throws IOException {
        return this.in.readInt();
    }
    
    private ConstantPool.Entry readRef() throws IOException {
        final int unsignedShort = this.in.readUnsignedShort();
        return (unsignedShort == 0) ? null : this.cls.cpMap[unsignedShort];
    }
    
    private ConstantPool.Entry readRef(final byte b) throws IOException {
        final ConstantPool.Entry ref = this.readRef();
        assert !(ref instanceof UnresolvedEntry);
        this.checkTag(ref, b);
        return ref;
    }
    
    private ConstantPool.Entry checkValid(final ConstantPool.Entry entry) {
        if (entry == ClassReader.INVALID_ENTRY) {
            throw new IllegalStateException("Invalid constant pool reference");
        }
        return entry;
    }
    
    private ConstantPool.Entry checkTag(final ConstantPool.Entry entry, final byte b) throws ClassFormatException {
        if (entry == null || !entry.tagMatches(b)) {
            throw new ClassFormatException("Bad constant, expected type=" + ConstantPool.tagName(b) + " got " + ((entry == null) ? "null CP index" : ("type=" + ConstantPool.tagName(entry.tag))) + ", in File: " + this.cls.file.nameString + ((this.inPos == this.constantPoolLimit) ? " in constant pool" : (" at pos: " + this.inPos)));
        }
        return entry;
    }
    
    private ConstantPool.Entry checkTag(final ConstantPool.Entry entry, final byte b, final boolean b2) throws ClassFormatException {
        return (b2 && entry == null) ? null : this.checkTag(entry, b);
    }
    
    private ConstantPool.Entry readRefOrNull(final byte b) throws IOException {
        final ConstantPool.Entry ref = this.readRef();
        this.checkTag(ref, b, true);
        return ref;
    }
    
    private ConstantPool.Utf8Entry readUtf8Ref() throws IOException {
        return (ConstantPool.Utf8Entry)this.readRef((byte)1);
    }
    
    private ConstantPool.ClassEntry readClassRef() throws IOException {
        return (ConstantPool.ClassEntry)this.readRef((byte)7);
    }
    
    private ConstantPool.ClassEntry readClassRefOrNull() throws IOException {
        return (ConstantPool.ClassEntry)this.readRefOrNull((byte)7);
    }
    
    private ConstantPool.SignatureEntry readSignatureRef() throws IOException {
        final ConstantPool.Entry ref = this.readRef((byte)13);
        return (ConstantPool.SignatureEntry)((ref != null && ref.getTag() == 1) ? ConstantPool.getSignatureEntry(ref.stringValue()) : ref);
    }
    
    void read() throws IOException {
        boolean b = false;
        try {
            this.readMagicNumbers();
            this.readConstantPool();
            this.readHeader();
            this.readMembers(false);
            this.readMembers(true);
            this.readAttributes(0, this.cls);
            this.fixUnresolvedEntries();
            this.cls.finishReading();
            assert 0 >= this.in.read(new byte[1]);
            b = true;
        }
        finally {
            if (!b && this.verbose > 0) {
                Utils.log.warning("Erroneous data at input offset " + this.inPos + " of " + this.cls.file);
            }
        }
    }
    
    void readMagicNumbers() throws IOException {
        this.cls.magic = this.in.readInt();
        if (this.cls.magic != -889275714) {
            throw new Attribute.FormatException("Bad magic number in class file " + Integer.toHexString(this.cls.magic), 0, "magic-number", "pass");
        }
        this.cls.version = Package.Version.of((short)this.readUnsignedShort(), (short)this.readUnsignedShort());
        final String checkVersion = this.checkVersion(this.cls.version);
        if (checkVersion != null) {
            throw new Attribute.FormatException("classfile version too " + checkVersion + ": " + this.cls.version + " in " + this.cls.file, 0, "version", "pass");
        }
    }
    
    private String checkVersion(final Package.Version version) {
        final short major = version.major;
        final short minor = version.minor;
        if (major < this.pkg.minClassVersion.major || (major == this.pkg.minClassVersion.major && minor < this.pkg.minClassVersion.minor)) {
            return "small";
        }
        if (major > this.pkg.maxClassVersion.major || (major == this.pkg.maxClassVersion.major && minor > this.pkg.maxClassVersion.minor)) {
            return "large";
        }
        return null;
    }
    
    void readConstantPool() throws IOException {
        final int unsignedShort = this.in.readUnsignedShort();
        final int[] array = new int[unsignedShort * 4];
        int i = 0;
        final ConstantPool.Entry[] cpMap = new ConstantPool.Entry[unsignedShort];
        cpMap[0] = ClassReader.INVALID_ENTRY;
        for (int j = 1; j < unsignedShort; ++j) {
            final byte byte1 = this.in.readByte();
            switch (byte1) {
                case 1: {
                    cpMap[j] = ConstantPool.getUtf8Entry(this.in.readUTF());
                    break;
                }
                case 3: {
                    cpMap[j] = ConstantPool.getLiteralEntry(this.in.readInt());
                    break;
                }
                case 4: {
                    cpMap[j] = ConstantPool.getLiteralEntry(this.in.readFloat());
                    break;
                }
                case 5: {
                    cpMap[j] = ConstantPool.getLiteralEntry(this.in.readLong());
                    cpMap[++j] = ClassReader.INVALID_ENTRY;
                    break;
                }
                case 6: {
                    cpMap[j] = ConstantPool.getLiteralEntry(this.in.readDouble());
                    cpMap[++j] = ClassReader.INVALID_ENTRY;
                    break;
                }
                case 7:
                case 8:
                case 16: {
                    array[i++] = j;
                    array[i++] = byte1;
                    array[i++] = this.in.readUnsignedShort();
                    array[i++] = -1;
                    break;
                }
                case 9:
                case 10:
                case 11:
                case 12: {
                    array[i++] = j;
                    array[i++] = byte1;
                    array[i++] = this.in.readUnsignedShort();
                    array[i++] = this.in.readUnsignedShort();
                    break;
                }
                case 18: {
                    array[i++] = j;
                    array[i++] = byte1;
                    array[i++] = (-1 ^ this.in.readUnsignedShort());
                    array[i++] = this.in.readUnsignedShort();
                    break;
                }
                case 15: {
                    array[i++] = j;
                    array[i++] = byte1;
                    array[i++] = (-1 ^ this.in.readUnsignedByte());
                    array[i++] = this.in.readUnsignedShort();
                    break;
                }
                default: {
                    throw new ClassFormatException("Bad constant pool tag " + byte1 + " in File: " + this.cls.file.nameString + " at pos: " + this.inPos);
                }
            }
        }
        this.constantPoolLimit = this.inPos;
        while (i > 0) {
            if (this.verbose > 3) {
                Utils.log.fine("CP fixups [" + i / 4 + "]");
            }
            final int n = i;
            i = 0;
            int k = 0;
            while (k < n) {
                final int n2 = array[k++];
                final int n3 = array[k++];
                final int n4 = array[k++];
                final int n5 = array[k++];
                if (this.verbose > 3) {
                    Utils.log.fine("  cp[" + n2 + "] = " + ConstantPool.tagName(n3) + "{" + n4 + "," + n5 + "}");
                }
                if ((n4 >= 0 && this.checkValid(cpMap[n4]) == null) || (n5 >= 0 && this.checkValid(cpMap[n5]) == null)) {
                    array[i++] = n2;
                    array[i++] = n3;
                    array[i++] = n4;
                    array[i++] = n5;
                }
                else {
                    switch (n3) {
                        case 7: {
                            cpMap[n2] = ConstantPool.getClassEntry(cpMap[n4].stringValue());
                            continue;
                        }
                        case 8: {
                            cpMap[n2] = ConstantPool.getStringEntry(cpMap[n4].stringValue());
                            continue;
                        }
                        case 9:
                        case 10:
                        case 11: {
                            cpMap[n2] = ConstantPool.getMemberEntry((byte)n3, (ConstantPool.ClassEntry)this.checkTag(cpMap[n4], (byte)7), (ConstantPool.DescriptorEntry)this.checkTag(cpMap[n5], (byte)12));
                            continue;
                        }
                        case 12: {
                            cpMap[n2] = ConstantPool.getDescriptorEntry((ConstantPool.Utf8Entry)this.checkTag(cpMap[n4], (byte)1), (ConstantPool.Utf8Entry)this.checkTag(cpMap[n5], (byte)13));
                            continue;
                        }
                        case 16: {
                            cpMap[n2] = ConstantPool.getMethodTypeEntry((ConstantPool.Utf8Entry)this.checkTag(cpMap[n4], (byte)13));
                            continue;
                        }
                        case 15: {
                            cpMap[n2] = ConstantPool.getMethodHandleEntry((byte)(-1 ^ n4), (ConstantPool.MemberEntry)this.checkTag(cpMap[n5], (byte)52));
                            continue;
                        }
                        case 18: {
                            cpMap[n2] = new UnresolvedEntry((byte)n3, new Object[] { -1 ^ n4, this.checkTag(cpMap[n5], (byte)12) });
                            continue;
                        }
                        default: {
                            assert false;
                            continue;
                        }
                    }
                }
            }
            assert i < n;
        }
        this.cls.cpMap = cpMap;
    }
    
    private void fixUnresolvedEntries() {
        if (!this.haveUnresolvedEntry) {
            return;
        }
        final ConstantPool.Entry[] cpMap = this.cls.getCPMap();
        for (int i = 0; i < cpMap.length; ++i) {
            final ConstantPool.Entry entry = cpMap[i];
            if (entry instanceof UnresolvedEntry) {
                final ConstantPool.Entry entry2 = cpMap[i] = ((UnresolvedEntry)entry).resolve();
                assert !(entry2 instanceof UnresolvedEntry);
            }
        }
        this.haveUnresolvedEntry = false;
    }
    
    void readHeader() throws IOException {
        this.cls.flags = this.readUnsignedShort();
        this.cls.thisClass = this.readClassRef();
        this.cls.superClass = this.readClassRefOrNull();
        final int unsignedShort = this.readUnsignedShort();
        this.cls.interfaces = new ConstantPool.ClassEntry[unsignedShort];
        for (int i = 0; i < unsignedShort; ++i) {
            this.cls.interfaces[i] = this.readClassRef();
        }
    }
    
    void readMembers(final boolean b) throws IOException {
        for (int unsignedShort = this.readUnsignedShort(), i = 0; i < unsignedShort; ++i) {
            this.readMember(b);
        }
    }
    
    void readMember(final boolean b) throws IOException {
        final int unsignedShort = this.readUnsignedShort();
        final ConstantPool.DescriptorEntry descriptorEntry = ConstantPool.getDescriptorEntry(this.readUtf8Ref(), this.readSignatureRef());
        Package.Class.Member member;
        if (!b) {
            member = this.cls.new Field(unsignedShort, descriptorEntry);
        }
        else {
            member = this.cls.new Method(unsignedShort, descriptorEntry);
        }
        this.readAttributes(b ? 2 : 1, member);
    }
    
    void readAttributes(final int n, final Attribute.Holder holder) throws IOException {
        final int unsignedShort = this.readUnsignedShort();
        if (unsignedShort == 0) {
            return;
        }
        if (this.verbose > 3) {
            Utils.log.fine("readAttributes " + holder + " [" + unsignedShort + "]");
        }
        for (int i = 0; i < unsignedShort; ++i) {
            final String stringValue = this.readUtf8Ref().stringValue();
            final int int1 = this.readInt();
            if (this.attrCommands != null) {
                final String s = this.attrCommands.get(Attribute.keyForLookup(n, stringValue));
                if (s != null) {
                    final String s2 = s;
                    switch (s2) {
                        case "pass": {
                            throw new Attribute.FormatException("passing attribute bitwise in " + holder, n, stringValue, s);
                        }
                        case "error": {
                            throw new Attribute.FormatException("attribute not allowed in " + holder, n, stringValue, s);
                        }
                        case "strip": {
                            this.skip(int1, stringValue + " attribute in " + holder);
                            continue;
                        }
                    }
                }
            }
            Attribute attribute = Attribute.lookup(Package.attrDefs, n, stringValue);
            if (this.verbose > 4 && attribute != null) {
                Utils.log.fine("pkg_attribute_lookup " + stringValue + " = " + attribute);
            }
            if (attribute == null) {
                attribute = Attribute.lookup(this.attrDefs, n, stringValue);
                if (this.verbose > 4 && attribute != null) {
                    Utils.log.fine("this " + stringValue + " = " + attribute);
                }
            }
            if (attribute == null) {
                attribute = Attribute.lookup(null, n, stringValue);
                if (this.verbose > 4 && attribute != null) {
                    Utils.log.fine("null_attribute_lookup " + stringValue + " = " + attribute);
                }
            }
            if (attribute == null && int1 == 0) {
                attribute = Attribute.find(n, stringValue, "");
            }
            final boolean b = n == 3 && (stringValue.equals("StackMap") || stringValue.equals("StackMapX"));
            if (b) {
                final Code code = (Code)holder;
                if (code.max_stack >= 65536 || code.max_locals >= 65536 || code.getLength() >= 65536 || stringValue.endsWith("X")) {
                    attribute = null;
                }
            }
            if (attribute == null) {
                if (b) {
                    throw new Attribute.FormatException("unsupported StackMap variant in " + holder, n, stringValue, "pass");
                }
                if (!"strip".equals(this.unknownAttrCommand)) {
                    throw new Attribute.FormatException(" is unknown attribute in class " + holder, n, stringValue, this.unknownAttrCommand);
                }
                this.skip(int1, "unknown " + stringValue + " attribute in " + holder);
            }
            else {
                final long inPos = this.inPos;
                if (attribute.layout() == Package.attrCodeEmpty) {
                    final Package.Class.Method method = (Package.Class.Method)holder;
                    method.code = new Code(method);
                    try {
                        this.readCode(method.code);
                    }
                    catch (final Instruction.FormatException ex) {
                        throw new ClassFormatException(ex.getMessage() + " in " + holder, ex);
                    }
                    assert int1 == this.inPos - inPos;
                }
                else if (attribute.layout() == Package.attrBootstrapMethodsEmpty) {
                    assert holder == this.cls;
                    this.readBootstrapMethods(this.cls);
                    assert int1 == this.inPos - inPos;
                    continue;
                }
                else if (attribute.layout() == Package.attrInnerClassesEmpty) {
                    assert holder == this.cls;
                    this.readInnerClasses(this.cls);
                    assert int1 == this.inPos - inPos;
                }
                else if (int1 > 0) {
                    final byte[] array = new byte[int1];
                    this.in.readFully(array);
                    attribute = attribute.addContent(array);
                }
                if (attribute.size() == 0 && !attribute.layout().isEmpty()) {
                    throw new ClassFormatException(stringValue + ": attribute length cannot be zero, in " + holder);
                }
                holder.addAttribute(attribute);
                if (this.verbose > 2) {
                    Utils.log.fine("read " + attribute);
                }
            }
        }
    }
    
    void readCode(final Code code) throws IOException {
        code.max_stack = this.readUnsignedShort();
        code.max_locals = this.readUnsignedShort();
        code.bytes = new byte[this.readInt()];
        this.in.readFully(code.bytes);
        Instruction.opcodeChecker(code.bytes, this.cls.getCPMap(), this.cls.version);
        final int unsignedShort = this.readUnsignedShort();
        code.setHandlerCount(unsignedShort);
        for (int i = 0; i < unsignedShort; ++i) {
            code.handler_start[i] = this.readUnsignedShort();
            code.handler_end[i] = this.readUnsignedShort();
            code.handler_catch[i] = this.readUnsignedShort();
            code.handler_class[i] = this.readClassRefOrNull();
        }
        this.readAttributes(3, code);
    }
    
    void readBootstrapMethods(final Package.Class class1) throws IOException {
        final ConstantPool.BootstrapMethodEntry[] array = new ConstantPool.BootstrapMethodEntry[this.readUnsignedShort()];
        for (int i = 0; i < array.length; ++i) {
            final ConstantPool.MethodHandleEntry methodHandleEntry = (ConstantPool.MethodHandleEntry)this.readRef((byte)15);
            final ConstantPool.Entry[] array2 = new ConstantPool.Entry[this.readUnsignedShort()];
            for (int j = 0; j < array2.length; ++j) {
                array2[j] = this.readRef((byte)51);
            }
            array[i] = ConstantPool.getBootstrapMethodEntry(methodHandleEntry, array2);
        }
        class1.setBootstrapMethods(Arrays.asList(array));
    }
    
    void readInnerClasses(final Package.Class class1) throws IOException {
        final int unsignedShort = this.readUnsignedShort();
        final ArrayList innerClasses = new ArrayList<Package.InnerClass>(unsignedShort);
        for (int i = 0; i < unsignedShort; ++i) {
            innerClasses.add(new Package.InnerClass(this.readClassRef(), this.readClassRefOrNull(), (ConstantPool.Utf8Entry)this.readRefOrNull((byte)1), this.readUnsignedShort()));
        }
        class1.innerClasses = (ArrayList<Package.InnerClass>)innerClasses;
    }
    
    static {
        INVALID_ENTRY = new ConstantPool.Entry((byte)(-1)) {
            @Override
            public boolean equals(final Object o) {
                throw new IllegalStateException("Should not call this");
            }
            
            @Override
            protected int computeValueHash() {
                throw new IllegalStateException("Should not call this");
            }
            
            @Override
            public int compareTo(final Object o) {
                throw new IllegalStateException("Should not call this");
            }
            
            @Override
            public String stringValue() {
                throw new IllegalStateException("Should not call this");
            }
        };
    }
    
    private class UnresolvedEntry extends ConstantPool.Entry
    {
        final Object[] refsOrIndexes;
        
        UnresolvedEntry(final byte b, final Object... refsOrIndexes) {
            super(b);
            this.refsOrIndexes = refsOrIndexes;
            ClassReader.this.haveUnresolvedEntry = true;
        }
        
        ConstantPool.Entry resolve() {
            final Package.Class cls = ClassReader.this.cls;
            switch (this.tag) {
                case 18: {
                    return ConstantPool.getInvokeDynamicEntry(cls.bootstrapMethods.get((int)this.refsOrIndexes[0]), (ConstantPool.DescriptorEntry)this.refsOrIndexes[1]);
                }
                default: {
                    throw new AssertionError();
                }
            }
        }
        
        private void unresolved() {
            throw new RuntimeException("unresolved entry has no string");
        }
        
        @Override
        public int compareTo(final Object o) {
            this.unresolved();
            return 0;
        }
        
        @Override
        public boolean equals(final Object o) {
            this.unresolved();
            return false;
        }
        
        @Override
        protected int computeValueHash() {
            this.unresolved();
            return 0;
        }
        
        @Override
        public String stringValue() {
            this.unresolved();
            return this.toString();
        }
        
        @Override
        public String toString() {
            return "(unresolved " + ConstantPool.tagName(this.tag) + ")";
        }
    }
    
    static class ClassFormatException extends IOException
    {
        private static final long serialVersionUID = -3564121733989501833L;
        
        public ClassFormatException(final String s) {
            super(s);
        }
        
        public ClassFormatException(final String s, final Throwable t) {
            super(s, t);
        }
    }
}
