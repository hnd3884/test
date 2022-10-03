package com.sun.java.util.jar.pack;

import java.io.FilterInputStream;
import java.io.BufferedInputStream;
import java.util.ListIterator;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.EOFException;
import java.io.PrintStream;
import java.io.IOException;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Set;
import java.util.List;
import java.util.Comparator;
import java.util.Map;

class PackageReader extends BandStructure
{
    Package pkg;
    byte[] bytes;
    LimitedBuffer in;
    Package.Version packageVersion;
    int[] tagCount;
    int numFiles;
    int numAttrDefs;
    int numInnerClasses;
    int numClasses;
    static final int MAGIC_BYTES = 4;
    Map<ConstantPool.Utf8Entry, ConstantPool.SignatureEntry> utf8Signatures;
    static final int NO_FLAGS_YET = 0;
    Comparator<ConstantPool.Entry> entryOutputOrder;
    Code[] allCodes;
    List<Code> codesWithFlags;
    Map<Package.Class, Set<ConstantPool.Entry>> ldcRefMap;
    
    PackageReader(final Package pkg, final InputStream inputStream) throws IOException {
        this.tagCount = new int[19];
        this.entryOutputOrder = new Comparator<ConstantPool.Entry>() {
            @Override
            public int compare(final ConstantPool.Entry entry, final ConstantPool.Entry entry2) {
                final int access$000 = PackageReader.this.getOutputIndex(entry);
                final int access$2 = PackageReader.this.getOutputIndex(entry2);
                if (access$000 >= 0 && access$2 >= 0) {
                    return access$000 - access$2;
                }
                if (access$000 == access$2) {
                    return entry.compareTo(entry2);
                }
                return (access$000 >= 0) ? -1 : 1;
            }
        };
        this.ldcRefMap = new HashMap<Package.Class, Set<ConstantPool.Entry>>();
        this.pkg = pkg;
        this.in = new LimitedBuffer(inputStream);
    }
    
    void read() throws IOException {
        try {
            this.readFileHeader();
            this.readBandHeaders();
            this.readConstantPool();
            this.readAttrDefs();
            this.readInnerClasses();
            final Package.Class[] classes = this.readClasses();
            this.readByteCodes();
            this.readFiles();
            assert !(!this.in.atLimit());
            assert this.in.getBytesServed() == this.archiveSize0 + this.archiveSize1;
            this.all_bands.doneDisbursing();
            for (int i = 0; i < classes.length; ++i) {
                this.reconstructClass(classes[i]);
            }
        }
        catch (final Exception ex) {
            Utils.log.warning("Error on input: " + ex, ex);
            if (this.verbose > 0) {
                Utils.log.info("Stream offsets: served=" + this.in.getBytesServed() + " buffered=" + this.in.buffered + " limit=" + this.in.limit);
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            }
            throw new Error("error unpacking", ex);
        }
    }
    
    void readFileHeader() throws IOException {
        this.readArchiveMagic();
        this.readArchiveHeader();
    }
    
    private int getMagicInt32() throws IOException {
        int n = 0;
        for (int i = 0; i < 4; ++i) {
            n = (n << 8 | (this.archive_magic.getByte() & 0xFF));
        }
        return n;
    }
    
    void readArchiveMagic() throws IOException {
        this.in.setReadLimit(19L);
        this.archive_magic.expectLength(4);
        this.archive_magic.readFrom(this.in);
        final int magicInt32 = this.getMagicInt32();
        this.pkg.getClass();
        if (-889270259 != magicInt32) {
            final StringBuilder append = new StringBuilder().append("Unexpected package magic number: got ").append(magicInt32).append("; expected ");
            this.pkg.getClass();
            throw new IOException(append.append(-889270259).toString());
        }
        this.archive_magic.doneDisbursing();
    }
    
    void checkArchiveVersion() throws IOException {
        Package.Version version = null;
        for (final Package.Version version2 : new Package.Version[] { Constants.JAVA8_PACKAGE_VERSION, Constants.JAVA7_PACKAGE_VERSION, Constants.JAVA6_PACKAGE_VERSION, Constants.JAVA5_PACKAGE_VERSION }) {
            if (this.packageVersion.equals(version2)) {
                version = version2;
                break;
            }
        }
        if (version == null) {
            throw new IOException("Unexpected package minor version: got " + this.packageVersion.toString() + "; expected " + (Constants.JAVA8_PACKAGE_VERSION.toString() + "OR" + Constants.JAVA7_PACKAGE_VERSION.toString() + " OR " + Constants.JAVA6_PACKAGE_VERSION.toString() + " OR " + Constants.JAVA5_PACKAGE_VERSION.toString()));
        }
    }
    
    void readArchiveHeader() throws IOException {
        this.archive_header_0.expectLength(3);
        this.archive_header_0.readFrom(this.in);
        this.packageVersion = Package.Version.of(this.archive_header_0.getInt(), this.archive_header_0.getInt());
        this.checkArchiveVersion();
        this.initHighestClassVersion(Constants.JAVA7_MAX_CLASS_VERSION);
        this.archiveOptions = this.archive_header_0.getInt();
        this.archive_header_0.doneDisbursing();
        final boolean testBit = BandStructure.testBit(this.archiveOptions, 1);
        final boolean testBit2 = BandStructure.testBit(this.archiveOptions, 16);
        final boolean testBit3 = BandStructure.testBit(this.archiveOptions, 2);
        final boolean testBit4 = BandStructure.testBit(this.archiveOptions, 8);
        this.initAttrIndexLimit();
        this.archive_header_S.expectLength(testBit2 ? 2 : 0);
        this.archive_header_S.readFrom(this.in);
        if (testBit2) {
            this.archiveSize1 = ((long)this.archive_header_S.getInt() << 32) + ((long)this.archive_header_S.getInt() << 32 >>> 32);
            this.in.setReadLimit(this.archiveSize1);
        }
        else {
            this.archiveSize1 = 0L;
            this.in.setReadLimit(-1L);
        }
        this.archive_header_S.doneDisbursing();
        this.archiveSize0 = this.in.getBytesServed();
        int n = 10;
        if (testBit2) {
            n += 5;
        }
        if (testBit) {
            n += 2;
        }
        if (testBit3) {
            n += 4;
        }
        if (testBit4) {
            n += 4;
        }
        this.archive_header_1.expectLength(n);
        this.archive_header_1.readFrom(this.in);
        if (testBit2) {
            this.archiveNextCount = this.archive_header_1.getInt();
            this.pkg.default_modtime = this.archive_header_1.getInt();
            this.numFiles = this.archive_header_1.getInt();
        }
        else {
            this.archiveNextCount = 0;
            this.numFiles = 0;
        }
        if (testBit) {
            this.band_headers.expectLength(this.archive_header_1.getInt());
            this.numAttrDefs = this.archive_header_1.getInt();
        }
        else {
            this.band_headers.expectLength(0);
            this.numAttrDefs = 0;
        }
        this.readConstantPoolCounts(testBit3, testBit4);
        this.numInnerClasses = this.archive_header_1.getInt();
        this.pkg.defaultClassVersion = Package.Version.of((short)this.archive_header_1.getInt(), (short)this.archive_header_1.getInt());
        this.numClasses = this.archive_header_1.getInt();
        this.archive_header_1.doneDisbursing();
        if (BandStructure.testBit(this.archiveOptions, 32)) {
            final Package pkg = this.pkg;
            pkg.default_options |= 0x1;
        }
    }
    
    void readBandHeaders() throws IOException {
        this.band_headers.readFrom(this.in);
        this.bandHeaderBytePos = 1;
        this.bandHeaderBytes = new byte[this.bandHeaderBytePos + this.band_headers.length()];
        for (int i = this.bandHeaderBytePos; i < this.bandHeaderBytes.length; ++i) {
            this.bandHeaderBytes[i] = (byte)this.band_headers.getByte();
        }
        this.band_headers.doneDisbursing();
    }
    
    void readConstantPoolCounts(final boolean b, final boolean b2) throws IOException {
        for (int i = 0; i < ConstantPool.TAGS_IN_ORDER.length; ++i) {
            final byte b3 = ConstantPool.TAGS_IN_ORDER[i];
            if (!b) {
                switch (b3) {
                    case 3:
                    case 4:
                    case 5:
                    case 6: {
                        continue;
                    }
                }
            }
            if (!b2) {
                switch (b3) {
                    case 15:
                    case 16:
                    case 17:
                    case 18: {
                        continue;
                    }
                }
            }
            this.tagCount[b3] = this.archive_header_1.getInt();
        }
    }
    
    @Override
    protected ConstantPool.Index getCPIndex(final byte b) {
        return this.pkg.cp.getIndexByTag(b);
    }
    
    ConstantPool.Index initCPIndex(final byte b, final ConstantPool.Entry[] array) {
        if (this.verbose > 3) {
            for (int i = 0; i < array.length; ++i) {
                Utils.log.fine("cp.add " + array[i]);
            }
        }
        final ConstantPool.Index index = ConstantPool.makeIndex(ConstantPool.tagName(b), array);
        if (this.verbose > 1) {
            Utils.log.fine("Read " + index);
        }
        this.pkg.cp.initIndexByTag(b, index);
        return index;
    }
    
    void checkLegacy(final String s) {
        if (this.packageVersion.lessThan(Constants.JAVA7_PACKAGE_VERSION)) {
            throw new RuntimeException("unexpected band " + s);
        }
    }
    
    void readConstantPool() throws IOException {
        if (this.verbose > 0) {
            Utils.log.info("Reading CP");
        }
        for (int i = 0; i < ConstantPool.TAGS_IN_ORDER.length; ++i) {
            final byte b = ConstantPool.TAGS_IN_ORDER[i];
            final ConstantPool.Entry[] array = new ConstantPool.Entry[this.tagCount[b]];
            if (this.verbose > 0) {
                Utils.log.info("Reading " + array.length + " " + ConstantPool.tagName(b) + " entries...");
            }
            switch (b) {
                case 1: {
                    this.readUtf8Bands(array);
                    break;
                }
                case 3: {
                    this.cp_Int.expectLength(array.length);
                    this.cp_Int.readFrom(this.in);
                    for (int j = 0; j < array.length; ++j) {
                        array[j] = ConstantPool.getLiteralEntry(this.cp_Int.getInt());
                    }
                    this.cp_Int.doneDisbursing();
                    break;
                }
                case 4: {
                    this.cp_Float.expectLength(array.length);
                    this.cp_Float.readFrom(this.in);
                    for (int k = 0; k < array.length; ++k) {
                        array[k] = ConstantPool.getLiteralEntry(Float.intBitsToFloat(this.cp_Float.getInt()));
                    }
                    this.cp_Float.doneDisbursing();
                    break;
                }
                case 5: {
                    this.cp_Long_hi.expectLength(array.length);
                    this.cp_Long_hi.readFrom(this.in);
                    this.cp_Long_lo.expectLength(array.length);
                    this.cp_Long_lo.readFrom(this.in);
                    for (int l = 0; l < array.length; ++l) {
                        array[l] = ConstantPool.getLiteralEntry(((long)this.cp_Long_hi.getInt() << 32) + ((long)this.cp_Long_lo.getInt() << 32 >>> 32));
                    }
                    this.cp_Long_hi.doneDisbursing();
                    this.cp_Long_lo.doneDisbursing();
                    break;
                }
                case 6: {
                    this.cp_Double_hi.expectLength(array.length);
                    this.cp_Double_hi.readFrom(this.in);
                    this.cp_Double_lo.expectLength(array.length);
                    this.cp_Double_lo.readFrom(this.in);
                    for (int n = 0; n < array.length; ++n) {
                        array[n] = ConstantPool.getLiteralEntry(Double.longBitsToDouble(((long)this.cp_Double_hi.getInt() << 32) + ((long)this.cp_Double_lo.getInt() << 32 >>> 32)));
                    }
                    this.cp_Double_hi.doneDisbursing();
                    this.cp_Double_lo.doneDisbursing();
                    break;
                }
                case 8: {
                    this.cp_String.expectLength(array.length);
                    this.cp_String.readFrom(this.in);
                    this.cp_String.setIndex(this.getCPIndex((byte)1));
                    for (int n2 = 0; n2 < array.length; ++n2) {
                        array[n2] = ConstantPool.getLiteralEntry(this.cp_String.getRef().stringValue());
                    }
                    this.cp_String.doneDisbursing();
                    break;
                }
                case 7: {
                    this.cp_Class.expectLength(array.length);
                    this.cp_Class.readFrom(this.in);
                    this.cp_Class.setIndex(this.getCPIndex((byte)1));
                    for (int n3 = 0; n3 < array.length; ++n3) {
                        array[n3] = ConstantPool.getClassEntry(this.cp_Class.getRef().stringValue());
                    }
                    this.cp_Class.doneDisbursing();
                    break;
                }
                case 13: {
                    this.readSignatureBands(array);
                    break;
                }
                case 12: {
                    this.cp_Descr_name.expectLength(array.length);
                    this.cp_Descr_name.readFrom(this.in);
                    this.cp_Descr_name.setIndex(this.getCPIndex((byte)1));
                    this.cp_Descr_type.expectLength(array.length);
                    this.cp_Descr_type.readFrom(this.in);
                    this.cp_Descr_type.setIndex(this.getCPIndex((byte)13));
                    for (int n4 = 0; n4 < array.length; ++n4) {
                        array[n4] = ConstantPool.getDescriptorEntry((ConstantPool.Utf8Entry)this.cp_Descr_name.getRef(), (ConstantPool.SignatureEntry)this.cp_Descr_type.getRef());
                    }
                    this.cp_Descr_name.doneDisbursing();
                    this.cp_Descr_type.doneDisbursing();
                    break;
                }
                case 9: {
                    this.readMemberRefs(b, array, this.cp_Field_class, this.cp_Field_desc);
                    break;
                }
                case 10: {
                    this.readMemberRefs(b, array, this.cp_Method_class, this.cp_Method_desc);
                    break;
                }
                case 11: {
                    this.readMemberRefs(b, array, this.cp_Imethod_class, this.cp_Imethod_desc);
                    break;
                }
                case 15: {
                    if (array.length > 0) {
                        this.checkLegacy(this.cp_MethodHandle_refkind.name());
                    }
                    this.cp_MethodHandle_refkind.expectLength(array.length);
                    this.cp_MethodHandle_refkind.readFrom(this.in);
                    this.cp_MethodHandle_member.expectLength(array.length);
                    this.cp_MethodHandle_member.readFrom(this.in);
                    this.cp_MethodHandle_member.setIndex(this.getCPIndex((byte)52));
                    for (int n5 = 0; n5 < array.length; ++n5) {
                        array[n5] = ConstantPool.getMethodHandleEntry((byte)this.cp_MethodHandle_refkind.getInt(), (ConstantPool.MemberEntry)this.cp_MethodHandle_member.getRef());
                    }
                    this.cp_MethodHandle_refkind.doneDisbursing();
                    this.cp_MethodHandle_member.doneDisbursing();
                    break;
                }
                case 16: {
                    if (array.length > 0) {
                        this.checkLegacy(this.cp_MethodType.name());
                    }
                    this.cp_MethodType.expectLength(array.length);
                    this.cp_MethodType.readFrom(this.in);
                    this.cp_MethodType.setIndex(this.getCPIndex((byte)13));
                    for (int n6 = 0; n6 < array.length; ++n6) {
                        array[n6] = ConstantPool.getMethodTypeEntry((ConstantPool.SignatureEntry)this.cp_MethodType.getRef());
                    }
                    this.cp_MethodType.doneDisbursing();
                    break;
                }
                case 18: {
                    if (array.length > 0) {
                        this.checkLegacy(this.cp_InvokeDynamic_spec.name());
                    }
                    this.cp_InvokeDynamic_spec.expectLength(array.length);
                    this.cp_InvokeDynamic_spec.readFrom(this.in);
                    this.cp_InvokeDynamic_spec.setIndex(this.getCPIndex((byte)17));
                    this.cp_InvokeDynamic_desc.expectLength(array.length);
                    this.cp_InvokeDynamic_desc.readFrom(this.in);
                    this.cp_InvokeDynamic_desc.setIndex(this.getCPIndex((byte)12));
                    for (int n7 = 0; n7 < array.length; ++n7) {
                        array[n7] = ConstantPool.getInvokeDynamicEntry((ConstantPool.BootstrapMethodEntry)this.cp_InvokeDynamic_spec.getRef(), (ConstantPool.DescriptorEntry)this.cp_InvokeDynamic_desc.getRef());
                    }
                    this.cp_InvokeDynamic_spec.doneDisbursing();
                    this.cp_InvokeDynamic_desc.doneDisbursing();
                    break;
                }
                case 17: {
                    if (array.length > 0) {
                        this.checkLegacy(this.cp_BootstrapMethod_ref.name());
                    }
                    this.cp_BootstrapMethod_ref.expectLength(array.length);
                    this.cp_BootstrapMethod_ref.readFrom(this.in);
                    this.cp_BootstrapMethod_ref.setIndex(this.getCPIndex((byte)15));
                    this.cp_BootstrapMethod_arg_count.expectLength(array.length);
                    this.cp_BootstrapMethod_arg_count.readFrom(this.in);
                    this.cp_BootstrapMethod_arg.expectLength(this.cp_BootstrapMethod_arg_count.getIntTotal());
                    this.cp_BootstrapMethod_arg.readFrom(this.in);
                    this.cp_BootstrapMethod_arg.setIndex(this.getCPIndex((byte)51));
                    for (int n8 = 0; n8 < array.length; ++n8) {
                        final ConstantPool.MethodHandleEntry methodHandleEntry = (ConstantPool.MethodHandleEntry)this.cp_BootstrapMethod_ref.getRef();
                        final int int1 = this.cp_BootstrapMethod_arg_count.getInt();
                        final ConstantPool.Entry[] array2 = new ConstantPool.Entry[int1];
                        for (int n9 = 0; n9 < int1; ++n9) {
                            array2[n9] = this.cp_BootstrapMethod_arg.getRef();
                        }
                        array[n8] = ConstantPool.getBootstrapMethodEntry(methodHandleEntry, array2);
                    }
                    this.cp_BootstrapMethod_ref.doneDisbursing();
                    this.cp_BootstrapMethod_arg_count.doneDisbursing();
                    this.cp_BootstrapMethod_arg.doneDisbursing();
                    break;
                }
                default: {
                    throw new AssertionError((Object)"unexpected CP tag in package");
                }
            }
            final ConstantPool.Index initCPIndex = this.initCPIndex(b, array);
            if (this.optDumpBands) {
                try (final PrintStream printStream = new PrintStream(BandStructure.getDumpStream(initCPIndex, ".idx"))) {
                    BandStructure.printArrayTo(printStream, initCPIndex.cpMap, 0, initCPIndex.cpMap.length);
                }
            }
        }
        this.cp_bands.doneDisbursing();
        if (this.optDumpBands || this.verbose > 1) {
            for (byte b2 = 50; b2 < 54; ++b2) {
                final ConstantPool.Index indexByTag = this.pkg.cp.getIndexByTag(b2);
                if (indexByTag != null) {
                    if (!indexByTag.isEmpty()) {
                        final ConstantPool.Entry[] cpMap = indexByTag.cpMap;
                        if (this.verbose > 1) {
                            Utils.log.info("Index group " + ConstantPool.tagName(b2) + " contains " + cpMap.length + " entries.");
                        }
                        if (this.optDumpBands) {
                            try (final PrintStream printStream2 = new PrintStream(BandStructure.getDumpStream(indexByTag.debugName, b2, ".gidx", indexByTag))) {
                                BandStructure.printArrayTo(printStream2, cpMap, 0, cpMap.length, true);
                            }
                        }
                    }
                }
            }
        }
        this.setBandIndexes();
    }
    
    void readUtf8Bands(final ConstantPool.Entry[] array) throws IOException {
        final int length = array.length;
        if (length == 0) {
            return;
        }
        this.cp_Utf8_prefix.expectLength(Math.max(0, length - 2));
        this.cp_Utf8_prefix.readFrom(this.in);
        this.cp_Utf8_suffix.expectLength(Math.max(0, length - 1));
        this.cp_Utf8_suffix.readFrom(this.in);
        final char[][] array2 = new char[length][];
        int n = 0;
        this.cp_Utf8_chars.expectLength(this.cp_Utf8_suffix.getIntTotal());
        this.cp_Utf8_chars.readFrom(this.in);
        for (int i = 0; i < length; ++i) {
            final int n2 = (i < 1) ? 0 : this.cp_Utf8_suffix.getInt();
            if (n2 == 0 && i >= 1) {
                ++n;
            }
            else {
                array2[i] = new char[n2];
                for (int j = 0; j < n2; ++j) {
                    final int int1 = this.cp_Utf8_chars.getInt();
                    assert int1 == (char)int1;
                    array2[i][j] = (char)int1;
                }
            }
        }
        this.cp_Utf8_chars.doneDisbursing();
        int n3 = 0;
        this.cp_Utf8_big_suffix.expectLength(n);
        this.cp_Utf8_big_suffix.readFrom(this.in);
        this.cp_Utf8_suffix.resetForSecondPass();
        for (int k = 0; k < length; ++k) {
            int int2 = (k < 1) ? 0 : this.cp_Utf8_suffix.getInt();
            final int n4 = (k < 2) ? 0 : this.cp_Utf8_prefix.getInt();
            if (int2 == 0 && k >= 1) {
                assert array2[k] == null;
                int2 = this.cp_Utf8_big_suffix.getInt();
            }
            else {
                assert array2[k] != null;
            }
            if (n3 < n4 + int2) {
                n3 = n4 + int2;
            }
        }
        final char[] array3 = new char[n3];
        this.cp_Utf8_suffix.resetForSecondPass();
        this.cp_Utf8_big_suffix.resetForSecondPass();
        for (int l = 0; l < length; ++l) {
            if (l >= 1) {
                if (this.cp_Utf8_suffix.getInt() == 0) {
                    final int int3 = this.cp_Utf8_big_suffix.getInt();
                    array2[l] = new char[int3];
                    if (int3 != 0) {
                        final IntBand intBand = this.cp_Utf8_big_chars.newIntBand("(Utf8_big_" + l + ")");
                        intBand.expectLength(int3);
                        intBand.readFrom(this.in);
                        for (int n5 = 0; n5 < int3; ++n5) {
                            final int int4 = intBand.getInt();
                            assert int4 == (char)int4;
                            array2[l][n5] = (char)int4;
                        }
                        intBand.doneDisbursing();
                    }
                }
            }
        }
        this.cp_Utf8_big_chars.doneDisbursing();
        this.cp_Utf8_prefix.resetForSecondPass();
        this.cp_Utf8_suffix.resetForSecondPass();
        this.cp_Utf8_big_suffix.resetForSecondPass();
        for (int n6 = 0; n6 < length; ++n6) {
            final int n7 = (n6 < 2) ? 0 : this.cp_Utf8_prefix.getInt();
            int int5 = (n6 < 1) ? 0 : this.cp_Utf8_suffix.getInt();
            if (int5 == 0 && n6 >= 1) {
                int5 = this.cp_Utf8_big_suffix.getInt();
            }
            System.arraycopy(array2[n6], 0, array3, n7, int5);
            array[n6] = ConstantPool.getUtf8Entry(new String(array3, 0, n7 + int5));
        }
        this.cp_Utf8_prefix.doneDisbursing();
        this.cp_Utf8_suffix.doneDisbursing();
        this.cp_Utf8_big_suffix.doneDisbursing();
    }
    
    void readSignatureBands(final ConstantPool.Entry[] array) throws IOException {
        this.cp_Signature_form.expectLength(array.length);
        this.cp_Signature_form.readFrom(this.in);
        this.cp_Signature_form.setIndex(this.getCPIndex((byte)1));
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = ConstantPool.countClassParts((ConstantPool.Utf8Entry)this.cp_Signature_form.getRef());
        }
        this.cp_Signature_form.resetForSecondPass();
        this.cp_Signature_classes.expectLength(BandStructure.getIntTotal(array2));
        this.cp_Signature_classes.readFrom(this.in);
        this.cp_Signature_classes.setIndex(this.getCPIndex((byte)7));
        this.utf8Signatures = new HashMap<ConstantPool.Utf8Entry, ConstantPool.SignatureEntry>();
        for (int j = 0; j < array.length; ++j) {
            final ConstantPool.Utf8Entry utf8Entry = (ConstantPool.Utf8Entry)this.cp_Signature_form.getRef();
            final ConstantPool.ClassEntry[] array3 = new ConstantPool.ClassEntry[array2[j]];
            for (int k = 0; k < array3.length; ++k) {
                array3[k] = (ConstantPool.ClassEntry)this.cp_Signature_classes.getRef();
            }
            final ConstantPool.SignatureEntry signatureEntry = ConstantPool.getSignatureEntry(utf8Entry, array3);
            array[j] = signatureEntry;
            this.utf8Signatures.put(signatureEntry.asUtf8Entry(), signatureEntry);
        }
        this.cp_Signature_form.doneDisbursing();
        this.cp_Signature_classes.doneDisbursing();
    }
    
    void readMemberRefs(final byte b, final ConstantPool.Entry[] array, final CPRefBand cpRefBand, final CPRefBand cpRefBand2) throws IOException {
        cpRefBand.expectLength(array.length);
        cpRefBand.readFrom(this.in);
        cpRefBand.setIndex(this.getCPIndex((byte)7));
        cpRefBand2.expectLength(array.length);
        cpRefBand2.readFrom(this.in);
        cpRefBand2.setIndex(this.getCPIndex((byte)12));
        for (int i = 0; i < array.length; ++i) {
            array[i] = ConstantPool.getMemberEntry(b, (ConstantPool.ClassEntry)cpRefBand.getRef(), (ConstantPool.DescriptorEntry)cpRefBand2.getRef());
        }
        cpRefBand.doneDisbursing();
        cpRefBand2.doneDisbursing();
    }
    
    void readFiles() throws IOException {
        if (this.verbose > 0) {
            Utils.log.info("  ...building " + this.numFiles + " files...");
        }
        this.file_name.expectLength(this.numFiles);
        this.file_size_lo.expectLength(this.numFiles);
        final int archiveOptions = this.archiveOptions;
        final boolean testBit = BandStructure.testBit(archiveOptions, 256);
        final boolean testBit2 = BandStructure.testBit(archiveOptions, 64);
        final boolean testBit3 = BandStructure.testBit(archiveOptions, 128);
        if (testBit) {
            this.file_size_hi.expectLength(this.numFiles);
        }
        if (testBit2) {
            this.file_modtime.expectLength(this.numFiles);
        }
        if (testBit3) {
            this.file_options.expectLength(this.numFiles);
        }
        this.file_name.readFrom(this.in);
        this.file_size_hi.readFrom(this.in);
        this.file_size_lo.readFrom(this.in);
        this.file_modtime.readFrom(this.in);
        this.file_options.readFrom(this.in);
        this.file_bits.setInputStreamFrom(this.in);
        final Iterator<Package.Class> iterator = this.pkg.getClasses().iterator();
        long n = 0L;
        final long[] array = new long[this.numFiles];
        for (int i = 0; i < this.numFiles; ++i) {
            long n2 = (long)this.file_size_lo.getInt() << 32 >>> 32;
            if (testBit) {
                n2 += (long)this.file_size_hi.getInt() << 32;
            }
            array[i] = n2;
            n += n2;
        }
        assert this.in.getReadLimit() == n;
        final byte[] array2 = new byte[65536];
        for (int j = 0; j < this.numFiles; ++j) {
            final ConstantPool.Utf8Entry utf8Entry = (ConstantPool.Utf8Entry)this.file_name.getRef();
            final long n3 = array[j];
            final Package.File file = this.pkg.new File(utf8Entry);
            file.modtime = this.pkg.default_modtime;
            file.options = this.pkg.default_options;
            if (testBit2) {
                final Package.File file2 = file;
                file2.modtime += this.file_modtime.getInt();
            }
            if (testBit3) {
                final Package.File file3 = file;
                file3.options |= this.file_options.getInt();
            }
            if (this.verbose > 1) {
                Utils.log.fine("Reading " + n3 + " bytes of " + utf8Entry.stringValue());
            }
            int read;
            for (long n4 = n3; n4 > 0L; n4 -= read) {
                int length = array2.length;
                if (length > n4) {
                    length = (int)n4;
                }
                read = this.file_bits.getInputStream().read(array2, 0, length);
                if (read < 0) {
                    throw new EOFException();
                }
                file.addBytes(array2, 0, read);
            }
            this.pkg.addFile(file);
            if (file.isClassStub()) {
                assert file.getFileLength() == 0L;
                iterator.next().initFile(file);
            }
        }
        while (iterator.hasNext()) {
            final Package.Class class1 = iterator.next();
            class1.initFile(null);
            class1.file.modtime = this.pkg.default_modtime;
        }
        this.file_name.doneDisbursing();
        this.file_size_hi.doneDisbursing();
        this.file_size_lo.doneDisbursing();
        this.file_modtime.doneDisbursing();
        this.file_options.doneDisbursing();
        this.file_bits.doneDisbursing();
        this.file_bands.doneDisbursing();
        if (this.archiveSize1 != 0L && !this.in.atLimit()) {
            throw new RuntimeException("Predicted archive_size " + this.archiveSize1 + " != " + (this.in.getBytesServed() - this.archiveSize0));
        }
    }
    
    void readAttrDefs() throws IOException {
        this.attr_definition_headers.expectLength(this.numAttrDefs);
        this.attr_definition_name.expectLength(this.numAttrDefs);
        this.attr_definition_layout.expectLength(this.numAttrDefs);
        this.attr_definition_headers.readFrom(this.in);
        this.attr_definition_name.readFrom(this.in);
        this.attr_definition_layout.readFrom(this.in);
        try (final PrintStream printStream = this.optDumpBands ? new PrintStream(BandStructure.getDumpStream(this.attr_definition_headers, ".def")) : null) {
            for (int i = 0; i < this.numAttrDefs; ++i) {
                final int byte1 = this.attr_definition_headers.getByte();
                final ConstantPool.Utf8Entry utf8Entry = (ConstantPool.Utf8Entry)this.attr_definition_name.getRef();
                final ConstantPool.Utf8Entry utf8Entry2 = (ConstantPool.Utf8Entry)this.attr_definition_layout.getRef();
                final int n = byte1 & 0x3;
                final int n2 = (byte1 >> 2) - 1;
                final Attribute.Layout layout = new Attribute.Layout(n, utf8Entry.stringValue(), utf8Entry2.stringValue());
                if (!layout.layoutForClassVersion(this.getHighestClassVersion()).equals(layout.layout())) {
                    throw new IOException("Bad attribute layout in archive: " + layout.layout());
                }
                this.setAttributeLayoutIndex(layout, n2);
                if (printStream != null) {
                    printStream.println(n2 + " " + layout);
                }
            }
        }
        this.attr_definition_headers.doneDisbursing();
        this.attr_definition_name.doneDisbursing();
        this.attr_definition_layout.doneDisbursing();
        this.makeNewAttributeBands();
        this.attr_definition_bands.doneDisbursing();
    }
    
    void readInnerClasses() throws IOException {
        this.ic_this_class.expectLength(this.numInnerClasses);
        this.ic_this_class.readFrom(this.in);
        this.ic_flags.expectLength(this.numInnerClasses);
        this.ic_flags.readFrom(this.in);
        int n = 0;
        for (int i = 0; i < this.numInnerClasses; ++i) {
            if ((this.ic_flags.getInt() & 0x10000) != 0x0) {
                ++n;
            }
        }
        this.ic_outer_class.expectLength(n);
        this.ic_outer_class.readFrom(this.in);
        this.ic_name.expectLength(n);
        this.ic_name.readFrom(this.in);
        this.ic_flags.resetForSecondPass();
        final ArrayList allInnerClasses = new ArrayList(this.numInnerClasses);
        for (int j = 0; j < this.numInnerClasses; ++j) {
            final int int1 = this.ic_flags.getInt();
            final boolean b = (int1 & 0x10000) != 0x0;
            final int n2 = int1 & 0xFFFEFFFF;
            final ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry)this.ic_this_class.getRef();
            ConstantPool.ClassEntry classEntry2;
            ConstantPool.Utf8Entry utf8Entry;
            if (b) {
                classEntry2 = (ConstantPool.ClassEntry)this.ic_outer_class.getRef();
                utf8Entry = (ConstantPool.Utf8Entry)this.ic_name.getRef();
            }
            else {
                final String[] innerClassName = Package.parseInnerClassName(classEntry.stringValue());
                assert innerClassName != null;
                final String s = innerClassName[0];
                final String s2 = innerClassName[2];
                if (s == null) {
                    classEntry2 = null;
                }
                else {
                    classEntry2 = ConstantPool.getClassEntry(s);
                }
                if (s2 == null) {
                    utf8Entry = null;
                }
                else {
                    utf8Entry = ConstantPool.getUtf8Entry(s2);
                }
            }
            final Package.InnerClass innerClass = new Package.InnerClass(classEntry, classEntry2, utf8Entry, n2);
            assert b || innerClass.predictable;
            allInnerClasses.add(innerClass);
        }
        this.ic_flags.doneDisbursing();
        this.ic_this_class.doneDisbursing();
        this.ic_outer_class.doneDisbursing();
        this.ic_name.doneDisbursing();
        this.pkg.setAllInnerClasses(allInnerClasses);
        this.ic_bands.doneDisbursing();
    }
    
    void readLocalInnerClasses(final Package.Class class1) throws IOException {
        final int int1 = this.class_InnerClasses_N.getInt();
        final ArrayList innerClasses = new ArrayList(int1);
        for (int i = 0; i < int1; ++i) {
            final ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry)this.class_InnerClasses_RC.getRef();
            int int2 = this.class_InnerClasses_F.getInt();
            if (int2 == 0) {
                final Package.InnerClass globalInnerClass = this.pkg.getGlobalInnerClass(classEntry);
                assert globalInnerClass != null;
                innerClasses.add((Object)globalInnerClass);
            }
            else {
                if (int2 == 65536) {
                    int2 = 0;
                }
                innerClasses.add((Object)new Package.InnerClass(classEntry, (ConstantPool.ClassEntry)this.class_InnerClasses_outer_RCN.getRef(), (ConstantPool.Utf8Entry)this.class_InnerClasses_name_RUN.getRef(), int2));
            }
        }
        class1.setInnerClasses((Collection<Package.InnerClass>)innerClasses);
    }
    
    Package.Class[] readClasses() throws IOException {
        final Package.Class[] array = new Package.Class[this.numClasses];
        if (this.verbose > 0) {
            Utils.log.info("  ...building " + array.length + " classes...");
        }
        this.class_this.expectLength(this.numClasses);
        this.class_super.expectLength(this.numClasses);
        this.class_interface_count.expectLength(this.numClasses);
        this.class_this.readFrom(this.in);
        this.class_super.readFrom(this.in);
        this.class_interface_count.readFrom(this.in);
        this.class_interface.expectLength(this.class_interface_count.getIntTotal());
        this.class_interface.readFrom(this.in);
        for (int i = 0; i < array.length; ++i) {
            final ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry)this.class_this.getRef();
            ConstantPool.ClassEntry classEntry2 = (ConstantPool.ClassEntry)this.class_super.getRef();
            final ConstantPool.ClassEntry[] array2 = new ConstantPool.ClassEntry[this.class_interface_count.getInt()];
            for (int j = 0; j < array2.length; ++j) {
                array2[j] = (ConstantPool.ClassEntry)this.class_interface.getRef();
            }
            if (classEntry2 == classEntry) {
                classEntry2 = null;
            }
            array[i] = this.pkg.new Class(0, classEntry, classEntry2, array2);
        }
        this.class_this.doneDisbursing();
        this.class_super.doneDisbursing();
        this.class_interface_count.doneDisbursing();
        this.class_interface.doneDisbursing();
        this.readMembers(array);
        this.countAndReadAttrs(0, Arrays.asList(array));
        this.pkg.trimToSize();
        this.readCodeHeaders();
        return array;
    }
    
    private int getOutputIndex(final ConstantPool.Entry entry) {
        assert entry.tag != 13;
        final int untypedIndex = this.pkg.cp.untypedIndexOf(entry);
        if (untypedIndex >= 0) {
            return untypedIndex;
        }
        if (entry.tag == 1) {
            return this.pkg.cp.untypedIndexOf(this.utf8Signatures.get(entry));
        }
        return -1;
    }
    
    void reconstructClass(final Package.Class class1) {
        if (this.verbose > 1) {
            Utils.log.fine("reconstruct " + class1);
        }
        final Attribute attribute = class1.getAttribute(this.attrClassFileVersion);
        if (attribute != null) {
            class1.removeAttribute(attribute);
            class1.version = this.parseClassFileVersionAttr(attribute);
        }
        else {
            class1.version = this.pkg.defaultClassVersion;
        }
        class1.expandSourceFile();
        class1.setCPMap(this.reconstructLocalCPMap(class1));
    }
    
    ConstantPool.Entry[] reconstructLocalCPMap(final Package.Class class1) {
        final Set set = this.ldcRefMap.get(class1);
        final HashSet set2 = new HashSet();
        class1.visitRefs(0, set2);
        final ArrayList bootstrapMethods = new ArrayList();
        class1.addAttribute(Package.attrBootstrapMethodsEmpty.canonicalInstance());
        ConstantPool.completeReferencesIn(set2, true, bootstrapMethods);
        final int expandLocalICs = class1.expandLocalICs();
        if (expandLocalICs != 0) {
            if (expandLocalICs > 0) {
                class1.visitInnerClassRefs(0, set2);
            }
            else {
                set2.clear();
                class1.visitRefs(0, set2);
            }
            ConstantPool.completeReferencesIn(set2, true, bootstrapMethods);
        }
        if (bootstrapMethods.isEmpty()) {
            class1.attributes.remove(Package.attrBootstrapMethodsEmpty.canonicalInstance());
        }
        else {
            set2.add(Package.getRefString("BootstrapMethods"));
            Collections.sort((List<Comparable>)bootstrapMethods);
            class1.setBootstrapMethods(bootstrapMethods);
        }
        int n = 0;
        final Iterator iterator = set2.iterator();
        while (iterator.hasNext()) {
            if (((ConstantPool.Entry)iterator.next()).isDoubleWord()) {
                ++n;
            }
        }
        final ConstantPool.Entry[] array = new ConstantPool.Entry[1 + n + set2.size()];
        int n2 = 1;
        if (set != null) {
            assert set2.containsAll(set);
            final Iterator iterator2 = set.iterator();
            while (iterator2.hasNext()) {
                array[n2++] = (ConstantPool.Entry)iterator2.next();
            }
            assert n2 == 1 + set.size();
            set2.removeAll(set);
        }
        final HashSet set3 = set2;
        final int n3 = n2;
        final Iterator iterator3 = set3.iterator();
        while (iterator3.hasNext()) {
            array[n2++] = (ConstantPool.Entry)iterator3.next();
        }
        assert n2 == n3 + set3.size();
        Arrays.sort(array, 1, n3, this.entryOutputOrder);
        Arrays.sort(array, n3, n2, this.entryOutputOrder);
        if (this.verbose > 3) {
            Utils.log.fine("CP of " + this + " {");
            for (final ConstantPool.Entry entry : array) {
                Utils.log.fine("  " + ((entry == null) ? -1 : this.getOutputIndex(entry)) + " : " + entry);
            }
            Utils.log.fine("}");
        }
        int length = array.length;
        int n4 = n2;
        while (--n4 >= 1) {
            final ConstantPool.Entry entry2 = array[n4];
            if (entry2.isDoubleWord()) {
                array[--length] = null;
            }
            array[--length] = entry2;
        }
        assert length == 1;
        return array;
    }
    
    void readMembers(final Package.Class[] array) throws IOException {
        assert array.length == this.numClasses;
        this.class_field_count.expectLength(this.numClasses);
        this.class_method_count.expectLength(this.numClasses);
        this.class_field_count.readFrom(this.in);
        this.class_method_count.readFrom(this.in);
        final int intTotal = this.class_field_count.getIntTotal();
        final int intTotal2 = this.class_method_count.getIntTotal();
        this.field_descr.expectLength(intTotal);
        this.method_descr.expectLength(intTotal2);
        if (this.verbose > 1) {
            Utils.log.fine("expecting #fields=" + intTotal + " and #methods=" + intTotal2 + " in #classes=" + this.numClasses);
        }
        final ArrayList list = new ArrayList(intTotal);
        this.field_descr.readFrom(this.in);
        for (int i = 0; i < array.length; ++i) {
            final Package.Class class1 = array[i];
            for (int int1 = this.class_field_count.getInt(), j = 0; j < int1; ++j) {
                list.add((Object)class1.new Field(0, (ConstantPool.DescriptorEntry)this.field_descr.getRef()));
            }
        }
        this.class_field_count.doneDisbursing();
        this.field_descr.doneDisbursing();
        this.countAndReadAttrs(1, (Collection<? extends Attribute.Holder>)list);
        final ArrayList list2 = new ArrayList(intTotal2);
        this.method_descr.readFrom(this.in);
        for (int k = 0; k < array.length; ++k) {
            final Package.Class class2 = array[k];
            for (int int2 = this.class_method_count.getInt(), l = 0; l < int2; ++l) {
                list2.add((Object)class2.new Method(0, (ConstantPool.DescriptorEntry)this.method_descr.getRef()));
            }
        }
        this.class_method_count.doneDisbursing();
        this.method_descr.doneDisbursing();
        this.countAndReadAttrs(2, (Collection<? extends Attribute.Holder>)list2);
        this.allCodes = this.buildCodeAttrs((List<Package.Class.Method>)list2);
    }
    
    Code[] buildCodeAttrs(final List<Package.Class.Method> list) {
        final ArrayList list2 = new ArrayList(list.size());
        for (final Package.Class.Method method : list) {
            if (method.getAttribute(this.attrCodeEmpty) != null) {
                list2.add(method.code = new Code(method));
            }
        }
        final Code[] array = new Code[list2.size()];
        list2.toArray(array);
        return array;
    }
    
    void readCodeHeaders() throws IOException {
        final boolean testBit = BandStructure.testBit(this.archiveOptions, 4);
        this.code_headers.expectLength(this.allCodes.length);
        this.code_headers.readFrom(this.in);
        final ArrayList codesWithFlags = new ArrayList(this.allCodes.length / 10);
        for (int i = 0; i < this.allCodes.length; ++i) {
            final Code code = this.allCodes[i];
            final int byte1 = this.code_headers.getByte();
            assert byte1 == (byte1 & 0xFF);
            if (this.verbose > 2) {
                Utils.log.fine("codeHeader " + code + " = " + byte1);
            }
            if (byte1 == 0) {
                codesWithFlags.add(code);
            }
            else {
                code.setMaxStack(BandStructure.shortCodeHeader_max_stack(byte1));
                code.setMaxNALocals(BandStructure.shortCodeHeader_max_na_locals(byte1));
                code.setHandlerCount(BandStructure.shortCodeHeader_handler_count(byte1));
                assert BandStructure.shortCodeHeader(code) == byte1;
            }
        }
        this.code_headers.doneDisbursing();
        this.code_max_stack.expectLength(codesWithFlags.size());
        this.code_max_na_locals.expectLength(codesWithFlags.size());
        this.code_handler_count.expectLength(codesWithFlags.size());
        this.code_max_stack.readFrom(this.in);
        this.code_max_na_locals.readFrom(this.in);
        this.code_handler_count.readFrom(this.in);
        for (final Code code2 : codesWithFlags) {
            code2.setMaxStack(this.code_max_stack.getInt());
            code2.setMaxNALocals(this.code_max_na_locals.getInt());
            code2.setHandlerCount(this.code_handler_count.getInt());
        }
        this.code_max_stack.doneDisbursing();
        this.code_max_na_locals.doneDisbursing();
        this.code_handler_count.doneDisbursing();
        this.readCodeHandlers();
        if (testBit) {
            this.codesWithFlags = Arrays.asList(this.allCodes);
        }
        else {
            this.codesWithFlags = codesWithFlags;
        }
        this.countAttrs(3, this.codesWithFlags);
    }
    
    void readCodeHandlers() throws IOException {
        int n = 0;
        for (int i = 0; i < this.allCodes.length; ++i) {
            n += this.allCodes[i].getHandlerCount();
        }
        final ValueBand[] array = { this.code_handler_start_P, this.code_handler_end_PO, this.code_handler_catch_PO, this.code_handler_class_RCN };
        for (int j = 0; j < array.length; ++j) {
            array[j].expectLength(n);
            array[j].readFrom(this.in);
        }
        for (int k = 0; k < this.allCodes.length; ++k) {
            final Code code = this.allCodes[k];
            for (int l = 0; l < code.getHandlerCount(); ++l) {
                code.handler_class[l] = this.code_handler_class_RCN.getRef();
                code.handler_start[l] = this.code_handler_start_P.getInt();
                code.handler_end[l] = this.code_handler_end_PO.getInt();
                code.handler_catch[l] = this.code_handler_catch_PO.getInt();
            }
        }
        for (int n2 = 0; n2 < array.length; ++n2) {
            array[n2].doneDisbursing();
        }
    }
    
    void fixupCodeHandlers() {
        for (int i = 0; i < this.allCodes.length; ++i) {
            final Code code = this.allCodes[i];
            for (int j = 0; j < code.getHandlerCount(); ++j) {
                final int n = code.handler_start[j];
                code.handler_start[j] = code.decodeBCI(n);
                final int n2 = n + code.handler_end[j];
                code.handler_end[j] = code.decodeBCI(n2);
                code.handler_catch[j] = code.decodeBCI(n2 + code.handler_catch[j]);
            }
        }
    }
    
    void countAndReadAttrs(final int n, final Collection<? extends Attribute.Holder> collection) throws IOException {
        this.countAttrs(n, collection);
        this.readAttrs(n, collection);
    }
    
    void countAttrs(final int n, final Collection<? extends Attribute.Holder> collection) throws IOException {
        final MultiBand multiBand = this.attrBands[n];
        final long n2 = this.attrFlagMask[n];
        if (this.verbose > 1) {
            Utils.log.fine("scanning flags and attrs for " + Attribute.contextName(n) + "[" + collection.size() + "]");
        }
        final List list = this.attrDefs.get(n);
        final Attribute.Layout[] array = new Attribute.Layout[list.size()];
        list.toArray(array);
        final IntBand attrBand = BandStructure.getAttrBand(multiBand, 0);
        final IntBand attrBand2 = BandStructure.getAttrBand(multiBand, 1);
        final IntBand attrBand3 = BandStructure.getAttrBand(multiBand, 2);
        final IntBand attrBand4 = BandStructure.getAttrBand(multiBand, 3);
        final IntBand attrBand5 = BandStructure.getAttrBand(multiBand, 4);
        final int n3 = this.attrOverflowMask[n];
        int n4 = 0;
        final boolean haveFlagsHi = this.haveFlagsHi(n);
        attrBand.expectLength(haveFlagsHi ? collection.size() : 0);
        attrBand.readFrom(this.in);
        attrBand2.expectLength(collection.size());
        attrBand2.readFrom(this.in);
        assert (n2 & (long)n3) == n3;
        for (final Attribute.Holder holder : collection) {
            final int int1 = attrBand2.getInt();
            holder.flags = int1;
            if ((int1 & n3) != 0x0) {
                ++n4;
            }
        }
        attrBand3.expectLength(n4);
        attrBand3.readFrom(this.in);
        attrBand4.expectLength(attrBand3.getIntTotal());
        attrBand4.readFrom(this.in);
        final int[] array2 = new int[array.length];
        for (final Attribute.Holder holder2 : collection) {
            assert holder2.attributes == null;
            long n5 = ((long)holder2.flags & n2) << 32 >>> 32;
            final Attribute.Holder holder3 = holder2;
            holder3.flags -= (int)n5;
            assert holder2.flags == (char)holder2.flags;
            assert holder2.flags == 0;
            if (haveFlagsHi) {
                n5 += (long)attrBand.getInt() << 32;
            }
            if (n5 == 0L) {
                continue;
            }
            int i = 0;
            final long n6 = n5 & (long)n3;
            assert n6 >= 0L;
            final long n7 = n5 - n6;
            if (n6 != 0L) {
                i = attrBand3.getInt();
            }
            int n8 = 0;
            long n9 = n7;
            int n10 = 0;
            while (n9 != 0L) {
                if ((n9 & 1L << n10) != 0x0L) {
                    n9 -= 1L << n10;
                    ++n8;
                }
                ++n10;
            }
            final ArrayList attributes = new ArrayList<Attribute>(n8 + i);
            holder2.attributes = (List<Attribute>)attributes;
            long n11 = n7;
            int n12 = 0;
            while (n11 != 0L) {
                if ((n11 & 1L << n12) != 0x0L) {
                    n11 -= 1L << n12;
                    final int[] array3 = array2;
                    final int n13 = n12;
                    ++array3[n13];
                    if (array[n12] == null) {
                        this.badAttrIndex(n12, n);
                    }
                    attributes.add(array[n12].canonicalInstance());
                    --n8;
                }
                ++n12;
            }
            assert n8 == 0;
            while (i > 0) {
                final int int2 = attrBand4.getInt();
                final int[] array4 = array2;
                final int n14 = int2;
                ++array4[n14];
                if (array[int2] == null) {
                    this.badAttrIndex(int2, n);
                }
                attributes.add(array[int2].canonicalInstance());
                --i;
            }
        }
        attrBand.doneDisbursing();
        attrBand2.doneDisbursing();
        attrBand3.doneDisbursing();
        attrBand4.doneDisbursing();
        int n15 = 0;
        boolean b = true;
        while (true) {
            for (int j = 0; j < array.length; ++j) {
                final Attribute.Layout layout = array[j];
                if (layout != null) {
                    if (b == this.isPredefinedAttr(n, j)) {
                        if (array2[j] != 0) {
                            final Attribute.Layout.Element[] callables = layout.getCallables();
                            for (int k = 0; k < callables.length; ++k) {
                                assert callables[k].kind == 10;
                                if (callables[k].flagTest((byte)8)) {
                                    ++n15;
                                }
                            }
                        }
                    }
                }
            }
            if (!b) {
                attrBand5.expectLength(n15);
                attrBand5.readFrom(this.in);
                boolean b2 = true;
                while (true) {
                    for (int l = 0; l < array.length; ++l) {
                        final Attribute.Layout layout2 = array[l];
                        if (layout2 != null) {
                            if (b2 == this.isPredefinedAttr(n, l)) {
                                final int n16 = array2[l];
                                final Band[] array5 = this.attrBandTable.get(layout2);
                                if (layout2 == this.attrInnerClassesEmpty) {
                                    this.class_InnerClasses_N.expectLength(n16);
                                    this.class_InnerClasses_N.readFrom(this.in);
                                    final int intTotal = this.class_InnerClasses_N.getIntTotal();
                                    this.class_InnerClasses_RC.expectLength(intTotal);
                                    this.class_InnerClasses_RC.readFrom(this.in);
                                    this.class_InnerClasses_F.expectLength(intTotal);
                                    this.class_InnerClasses_F.readFrom(this.in);
                                    final int n17 = intTotal - this.class_InnerClasses_F.getIntCount(0);
                                    this.class_InnerClasses_outer_RCN.expectLength(n17);
                                    this.class_InnerClasses_outer_RCN.readFrom(this.in);
                                    this.class_InnerClasses_name_RUN.expectLength(n17);
                                    this.class_InnerClasses_name_RUN.readFrom(this.in);
                                }
                                else if (!this.optDebugBands && n16 == 0) {
                                    for (int n18 = 0; n18 < array5.length; ++n18) {
                                        array5[n18].doneWithUnusedBand();
                                    }
                                }
                                else {
                                    if (!layout2.hasCallables()) {
                                        this.readAttrBands(layout2.elems, n16, new int[0], array5);
                                    }
                                    else {
                                        final Attribute.Layout.Element[] callables2 = layout2.getCallables();
                                        final int[] array6 = new int[callables2.length];
                                        array6[0] = n16;
                                        for (int n19 = 0; n19 < callables2.length; ++n19) {
                                            assert callables2[n19].kind == 10;
                                            int n20 = array6[n19];
                                            array6[n19] = -1;
                                            if (n16 > 0 && callables2[n19].flagTest((byte)8)) {
                                                n20 += attrBand5.getInt();
                                            }
                                            this.readAttrBands(callables2[n19].body, n20, array6, array5);
                                        }
                                    }
                                    if (this.optDebugBands && n16 == 0) {
                                        for (int n21 = 0; n21 < array5.length; ++n21) {
                                            array5[n21].doneDisbursing();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!b2) {
                        attrBand5.doneDisbursing();
                        return;
                    }
                    b2 = false;
                }
            }
            else {
                b = false;
            }
        }
    }
    
    void badAttrIndex(final int n, final int n2) throws IOException {
        throw new IOException("Unknown attribute index " + n + " for " + Constants.ATTR_CONTEXT_NAME[n2] + " attribute");
    }
    
    void readAttrs(final int n, final Collection<? extends Attribute.Holder> collection) throws IOException {
        final HashSet set = new HashSet();
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (final Attribute.Holder holder : collection) {
            if (holder.attributes == null) {
                continue;
            }
            final ListIterator<Attribute> listIterator = holder.attributes.listIterator();
            while (listIterator.hasNext()) {
                final Attribute attribute = listIterator.next();
                final Attribute.Layout layout = attribute.layout();
                if (layout.bandCount == 0) {
                    if (layout != this.attrInnerClassesEmpty) {
                        continue;
                    }
                    this.readLocalInnerClasses((Package.Class)holder);
                }
                else {
                    set.add(layout);
                    final boolean b = n == 1 && layout == this.attrConstantValue;
                    if (b) {
                        this.setConstantValueIndex((Package.Class.Field)holder);
                    }
                    if (this.verbose > 2) {
                        Utils.log.fine("read " + attribute + " in " + holder);
                    }
                    final Band[] array = this.attrBandTable.get(layout);
                    byteArrayOutputStream.reset();
                    listIterator.set(attribute.addContent(byteArrayOutputStream.toByteArray(), attribute.unparse(new Attribute.ValueStream() {
                        @Override
                        public int getInt(final int n) {
                            return ((IntBand)array[n]).getInt();
                        }
                        
                        @Override
                        public ConstantPool.Entry getRef(final int n) {
                            return ((CPRefBand)array[n]).getRef();
                        }
                        
                        @Override
                        public int decodeBCI(final int n) {
                            return ((Code)holder).decodeBCI(n);
                        }
                    }, byteArrayOutputStream)));
                    if (!b) {
                        continue;
                    }
                    this.setConstantValueIndex(null);
                }
            }
        }
        for (final Attribute.Layout layout2 : set) {
            if (layout2 == null) {
                continue;
            }
            final Band[] array2 = this.attrBandTable.get(layout2);
            for (int i = 0; i < array2.length; ++i) {
                array2[i].doneDisbursing();
            }
        }
        if (n == 0) {
            this.class_InnerClasses_N.doneDisbursing();
            this.class_InnerClasses_RC.doneDisbursing();
            this.class_InnerClasses_F.doneDisbursing();
            this.class_InnerClasses_outer_RCN.doneDisbursing();
            this.class_InnerClasses_name_RUN.doneDisbursing();
        }
        final MultiBand multiBand = this.attrBands[n];
        for (int j = 0; j < multiBand.size(); ++j) {
            final Band value = multiBand.get(j);
            if (value instanceof MultiBand) {
                value.doneDisbursing();
            }
        }
        multiBand.doneDisbursing();
    }
    
    private void readAttrBands(final Attribute.Layout.Element[] array, final int n, final int[] array2, final Band[] array3) throws IOException {
        for (int i = 0; i < array.length; ++i) {
            final Attribute.Layout.Element element = array[i];
            Band band = null;
            if (element.hasBand()) {
                band = array3[element.bandIndex];
                band.expectLength(n);
                band.readFrom(this.in);
            }
            switch (element.kind) {
                case 5: {
                    this.readAttrBands(element.body, ((IntBand)band).getIntTotal(), array2, array3);
                    break;
                }
                case 7: {
                    int n2 = n;
                    for (int j = 0; j < element.body.length; ++j) {
                        int n3;
                        if (j == element.body.length - 1) {
                            n3 = n2;
                        }
                        else {
                            n3 = 0;
                            while (j == j || (j < element.body.length && element.body[j].flagTest((byte)8))) {
                                n3 += ((IntBand)band).getIntCount(element.body[j].value);
                                ++j;
                            }
                            --j;
                        }
                        n2 -= n3;
                        this.readAttrBands(element.body[j].body, n3, array2, array3);
                    }
                    assert n2 == 0;
                    break;
                }
                case 9: {
                    assert element.body.length == 1;
                    assert element.body[0].kind == 10;
                    if (element.flagTest((byte)8)) {
                        break;
                    }
                    assert array2[element.value] >= 0;
                    final int value = element.value;
                    array2[value] += n;
                    break;
                }
                case 10: {
                    assert false;
                    break;
                }
            }
        }
    }
    
    void readByteCodes() throws IOException {
        this.bc_codes.elementCountForDebug = this.allCodes.length;
        this.bc_codes.setInputStreamFrom(this.in);
        this.readByteCodeOps();
        this.bc_codes.doneDisbursing();
        final Band[] array = { this.bc_case_value, this.bc_byte, this.bc_short, this.bc_local, this.bc_label, this.bc_intref, this.bc_floatref, this.bc_longref, this.bc_doubleref, this.bc_stringref, this.bc_loadablevalueref, this.bc_classref, this.bc_fieldref, this.bc_methodref, this.bc_imethodref, this.bc_indyref, this.bc_thisfield, this.bc_superfield, this.bc_thismethod, this.bc_supermethod, this.bc_initref, this.bc_escref, this.bc_escrefsize, this.bc_escsize };
        for (int i = 0; i < array.length; ++i) {
            array[i].readFrom(this.in);
        }
        this.bc_escbyte.expectLength(this.bc_escsize.getIntTotal());
        this.bc_escbyte.readFrom(this.in);
        this.expandByteCodeOps();
        this.bc_case_count.doneDisbursing();
        for (int j = 0; j < array.length; ++j) {
            array[j].doneDisbursing();
        }
        this.bc_escbyte.doneDisbursing();
        this.bc_bands.doneDisbursing();
        this.readAttrs(3, this.codesWithFlags);
        this.fixupCodeHandlers();
        this.code_bands.doneDisbursing();
        this.class_bands.doneDisbursing();
    }
    
    private void readByteCodeOps() throws IOException {
        byte[] realloc = new byte[4096];
        final ArrayList list = new ArrayList();
        int i = 0;
    Label_0016:
        while (true) {
            while (i < this.allCodes.length) {
                final Code code = this.allCodes[i];
                int n = 0;
                while (true) {
                    int n2 = this.bc_codes.getByte();
                    if (n + 10 > realloc.length) {
                        realloc = BandStructure.realloc(realloc);
                    }
                    realloc[n] = (byte)n2;
                    boolean b = false;
                    if (n2 == 196) {
                        n2 = this.bc_codes.getByte();
                        realloc[++n] = (byte)n2;
                        b = true;
                    }
                    assert n2 == (0xFF & n2);
                    switch (n2) {
                        case 170:
                        case 171: {
                            this.bc_case_count.expectMoreLength(1);
                            list.add(n2);
                            break;
                        }
                        case 132: {
                            this.bc_local.expectMoreLength(1);
                            if (b) {
                                this.bc_short.expectMoreLength(1);
                                break;
                            }
                            this.bc_byte.expectMoreLength(1);
                            break;
                        }
                        case 17: {
                            this.bc_short.expectMoreLength(1);
                            break;
                        }
                        case 16: {
                            this.bc_byte.expectMoreLength(1);
                            break;
                        }
                        case 188: {
                            this.bc_byte.expectMoreLength(1);
                            break;
                        }
                        case 197: {
                            assert this.getCPRefOpBand(n2) == this.bc_classref;
                            this.bc_classref.expectMoreLength(1);
                            this.bc_byte.expectMoreLength(1);
                            break;
                        }
                        case 253: {
                            this.bc_escrefsize.expectMoreLength(1);
                            this.bc_escref.expectMoreLength(1);
                            break;
                        }
                        case 254: {
                            this.bc_escsize.expectMoreLength(1);
                            break;
                        }
                        default: {
                            if (Instruction.isInvokeInitOp(n2)) {
                                this.bc_initref.expectMoreLength(1);
                                break;
                            }
                            if (Instruction.isSelfLinkerOp(n2)) {
                                this.selfOpRefBand(n2).expectMoreLength(1);
                                break;
                            }
                            if (Instruction.isBranchOp(n2)) {
                                this.bc_label.expectMoreLength(1);
                                break;
                            }
                            if (Instruction.isCPRefOp(n2)) {
                                this.getCPRefOpBand(n2).expectMoreLength(1);
                                assert n2 != 197;
                                break;
                            }
                            else {
                                if (Instruction.isLocalSlotOp(n2)) {
                                    this.bc_local.expectMoreLength(1);
                                    break;
                                }
                                break;
                            }
                            break;
                        }
                        case 255: {
                            code.bytes = BandStructure.realloc(realloc, n);
                            ++i;
                            continue Label_0016;
                        }
                    }
                    ++n;
                }
            }
            break;
        }
        this.bc_case_count.readFrom(this.in);
        for (final int intValue : list) {
            final int int1 = this.bc_case_count.getInt();
            this.bc_label.expectMoreLength(1 + int1);
            this.bc_case_value.expectMoreLength((intValue == 170) ? 1 : int1);
        }
        this.bc_case_count.resetForSecondPass();
    }
    
    private void expandByteCodeOps() throws IOException {
        byte[] array = new byte[4096];
        int[] realloc = new int[4096];
        int[] realloc2 = new int[1024];
        final Fixups fixups = new Fixups();
        for (int i = 0; i < this.allCodes.length; ++i) {
            final Code code = this.allCodes[i];
            final byte[] bytes = code.bytes;
            code.bytes = null;
            final Package.Class thisClass = code.thisClass();
            Object o = this.ldcRefMap.get(thisClass);
            if (o == null) {
                this.ldcRefMap.put(thisClass, (Set<ConstantPool.Entry>)(o = new HashSet<ConstantPool.MemberEntry>()));
            }
            final ConstantPool.ClassEntry thisClass2 = thisClass.thisClass;
            final ConstantPool.ClassEntry superClass = thisClass.superClass;
            ConstantPool.ClassEntry classEntry = null;
            int j = 0;
            int n = 0;
            int n2 = 0;
            fixups.clear();
            for (int k = 0; k < bytes.length; ++k) {
                int n3 = Instruction.getByte(bytes, k);
                int n4 = j;
                realloc[n++] = n4;
                if (j + 10 > array.length) {
                    array = BandStructure.realloc(array);
                }
                if (n + 10 > realloc.length) {
                    realloc = BandStructure.realloc(realloc);
                }
                if (n2 + 10 > realloc2.length) {
                    realloc2 = BandStructure.realloc(realloc2);
                }
                boolean b = false;
                if (n3 == 196) {
                    array[j++] = (byte)n3;
                    n3 = Instruction.getByte(bytes, ++k);
                    b = true;
                }
                switch (n3) {
                    case 170:
                    case 171: {
                        int int1;
                        for (int1 = this.bc_case_count.getInt(); j + 30 + int1 * 8 > array.length; array = BandStructure.realloc(array)) {}
                        array[j++] = (byte)n3;
                        Arrays.fill(array, j, j + 30, (byte)0);
                        final Instruction.Switch switch1 = (Instruction.Switch)Instruction.at(array, n4);
                        switch1.setCaseCount(int1);
                        if (n3 == 170) {
                            switch1.setCaseValue(0, this.bc_case_value.getInt());
                        }
                        else {
                            for (int l = 0; l < int1; ++l) {
                                switch1.setCaseValue(l, this.bc_case_value.getInt());
                            }
                        }
                        realloc2[n2++] = n4;
                        j = switch1.getNextPC();
                        break;
                    }
                    case 132: {
                        array[j++] = (byte)n3;
                        final int int2 = this.bc_local.getInt();
                        if (b) {
                            final int int3 = this.bc_short.getInt();
                            Instruction.setShort(array, j, int2);
                            j += 2;
                            Instruction.setShort(array, j, int3);
                            j += 2;
                            break;
                        }
                        final byte b2 = (byte)this.bc_byte.getByte();
                        array[j++] = (byte)int2;
                        array[j++] = b2;
                        break;
                    }
                    case 17: {
                        final int int4 = this.bc_short.getInt();
                        array[j++] = (byte)n3;
                        Instruction.setShort(array, j, int4);
                        j += 2;
                        break;
                    }
                    case 16:
                    case 188: {
                        final int byte1 = this.bc_byte.getByte();
                        array[j++] = (byte)n3;
                        array[j++] = (byte)byte1;
                        break;
                    }
                    case 253: {
                        final int int5 = this.bc_escrefsize.getInt();
                        final ConstantPool.Entry ref = this.bc_escref.getRef();
                        if (int5 == 1) {
                            ((Set<ConstantPool.MemberEntry>)o).add((ConstantPool.MemberEntry)ref);
                        }
                        switch (int5) {
                            case 1: {
                                fixups.addU1(j, ref);
                                break;
                            }
                            case 2: {
                                fixups.addU2(j, ref);
                                break;
                            }
                            default: {
                                assert false;
                                break;
                            }
                        }
                        array[j + 0] = (array[j + 1] = 0);
                        j += int5;
                        break;
                    }
                    case 254: {
                        int int6;
                        for (int6 = this.bc_escsize.getInt(); j + int6 > array.length; array = BandStructure.realloc(array)) {}
                        while (int6-- > 0) {
                            array[j++] = (byte)this.bc_escbyte.getByte();
                        }
                        break;
                    }
                    default: {
                        if (Instruction.isInvokeInitOp(n3)) {
                            final int n5 = n3 - 230;
                            final int n6 = 183;
                            ConstantPool.ClassEntry classEntry2 = null;
                            switch (n5) {
                                case 0: {
                                    classEntry2 = thisClass2;
                                    break;
                                }
                                case 1: {
                                    classEntry2 = superClass;
                                    break;
                                }
                                default: {
                                    assert n5 == 2;
                                    classEntry2 = classEntry;
                                    break;
                                }
                            }
                            array[j++] = (byte)n6;
                            fixups.addU2(j, this.pkg.cp.getOverloadingForIndex((byte)10, classEntry2, "<init>", this.bc_initref.getInt()));
                            array[j + 0] = (array[j + 1] = 0);
                            j += 2;
                            assert Instruction.opLength(n6) == j - n4;
                            break;
                        }
                        else if (Instruction.isSelfLinkerOp(n3)) {
                            int n7 = n3 - 202;
                            final boolean b3 = n7 >= 14;
                            if (b3) {
                                n7 -= 14;
                            }
                            final boolean b4 = n7 >= 7;
                            if (b4) {
                                n7 -= 7;
                            }
                            final int n8 = 178 + n7;
                            final boolean fieldOp = Instruction.isFieldOp(n8);
                            final ConstantPool.ClassEntry classEntry3 = b3 ? superClass : thisClass2;
                            CPRefBand cpRefBand;
                            ConstantPool.Index index;
                            if (fieldOp) {
                                cpRefBand = (b3 ? this.bc_superfield : this.bc_thisfield);
                                index = this.pkg.cp.getMemberIndex((byte)9, classEntry3);
                            }
                            else {
                                cpRefBand = (b3 ? this.bc_supermethod : this.bc_thismethod);
                                index = this.pkg.cp.getMemberIndex((byte)10, classEntry3);
                            }
                            assert cpRefBand == this.selfOpRefBand(n3);
                            final ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)cpRefBand.getRef(index);
                            if (b4) {
                                array[j++] = 42;
                                n4 = j;
                                realloc[n++] = n4;
                            }
                            array[j++] = (byte)n8;
                            fixups.addU2(j, memberEntry);
                            array[j + 0] = (array[j + 1] = 0);
                            j += 2;
                            assert Instruction.opLength(n8) == j - n4;
                            break;
                        }
                        else if (Instruction.isBranchOp(n3)) {
                            array[j++] = (byte)n3;
                            assert !b;
                            final int n9 = n4 + Instruction.opLength(n3);
                            realloc2[n2++] = n4;
                            while (j < n9) {
                                array[j++] = 0;
                            }
                            break;
                        }
                        else if (Instruction.isCPRefOp(n3)) {
                            final CPRefBand cpRefOpBand = this.getCPRefOpBand(n3);
                            ConstantPool.Entry ref2 = cpRefOpBand.getRef();
                            if (ref2 == null) {
                                if (cpRefOpBand == this.bc_classref) {
                                    ref2 = thisClass2;
                                }
                                else {
                                    assert false;
                                }
                            }
                            int n10 = n3;
                            int n11 = 2;
                            switch (n3) {
                                case 243: {
                                    n10 = 184;
                                    break;
                                }
                                case 242: {
                                    n10 = 183;
                                    break;
                                }
                                case 18:
                                case 233:
                                case 234:
                                case 235:
                                case 240: {
                                    n10 = 18;
                                    n11 = 1;
                                    ((Set<ConstantPool.MemberEntry>)o).add((ConstantPool.MemberEntry)ref2);
                                    break;
                                }
                                case 19:
                                case 236:
                                case 237:
                                case 238:
                                case 241: {
                                    n10 = 19;
                                    break;
                                }
                                case 20:
                                case 239: {
                                    n10 = 20;
                                    break;
                                }
                                case 187: {
                                    classEntry = (ConstantPool.ClassEntry)ref2;
                                    break;
                                }
                            }
                            array[j++] = (byte)n10;
                            switch (n11) {
                                case 1: {
                                    fixups.addU1(j, ref2);
                                    break;
                                }
                                case 2: {
                                    fixups.addU2(j, ref2);
                                    break;
                                }
                                default: {
                                    assert false;
                                    break;
                                }
                            }
                            array[j + 0] = (array[j + 1] = 0);
                            j += n11;
                            if (n10 == 197) {
                                array[j++] = (byte)this.bc_byte.getByte();
                            }
                            else if (n10 == 185) {
                                array[j++] = (byte)(1 + ((ConstantPool.MemberEntry)ref2).descRef.typeRef.computeSize(true));
                                array[j++] = 0;
                            }
                            else if (n10 == 186) {
                                array[j++] = 0;
                                array[j++] = 0;
                            }
                            assert Instruction.opLength(n10) == j - n4;
                            break;
                        }
                        else if (Instruction.isLocalSlotOp(n3)) {
                            array[j++] = (byte)n3;
                            final int int7 = this.bc_local.getInt();
                            if (b) {
                                Instruction.setShort(array, j, int7);
                                j += 2;
                                if (n3 == 132) {
                                    Instruction.setShort(array, j, this.bc_short.getInt());
                                    j += 2;
                                }
                            }
                            else {
                                Instruction.setByte(array, j, int7);
                                ++j;
                                if (n3 == 132) {
                                    Instruction.setByte(array, j, this.bc_byte.getByte());
                                    ++j;
                                }
                            }
                            assert Instruction.opLength(n3) == j - n4;
                            break;
                        }
                        else {
                            if (n3 >= 202) {
                                Utils.log.warning("unrecognized bytescode " + n3 + " " + Instruction.byteName(n3));
                            }
                            assert n3 < 202;
                            array[j++] = (byte)n3;
                            assert Instruction.opLength(n3) == j - n4;
                            break;
                        }
                        break;
                    }
                }
            }
            code.setBytes(BandStructure.realloc(array, j));
            code.setInstructionMap(realloc, n);
            Instruction at = null;
            for (final int n13 : realloc2) {
                at = Instruction.at(code.bytes, n13, at);
                if (at instanceof Instruction.Switch) {
                    final Instruction.Switch switch2 = (Instruction.Switch)at;
                    switch2.setDefaultLabel(this.getLabel(this.bc_label, code, n13));
                    for (int caseCount = switch2.getCaseCount(), n14 = 0; n14 < caseCount; ++n14) {
                        switch2.setCaseLabel(n14, this.getLabel(this.bc_label, code, n13));
                    }
                }
                else {
                    at.setBranchLabel(this.getLabel(this.bc_label, code, n13));
                }
            }
            if (fixups.size() > 0) {
                if (this.verbose > 2) {
                    Utils.log.fine("Fixups in code: " + fixups);
                }
                code.addFixups(fixups);
            }
        }
    }
    
    static class LimitedBuffer extends BufferedInputStream
    {
        long served;
        int servedPos;
        long limit;
        long buffered;
        
        public boolean atLimit() {
            final boolean b = this.getBytesServed() == this.limit;
            assert this.limit == this.buffered;
            return b;
        }
        
        public long getBytesServed() {
            return this.served + (this.pos - this.servedPos);
        }
        
        public void setReadLimit(final long n) {
            if (n == -1L) {
                this.limit = -1L;
            }
            else {
                this.limit = this.getBytesServed() + n;
            }
        }
        
        public long getReadLimit() {
            if (this.limit == -1L) {
                return this.limit;
            }
            return this.limit - this.getBytesServed();
        }
        
        @Override
        public int read() throws IOException {
            if (this.pos < this.count) {
                return this.buf[this.pos++] & 0xFF;
            }
            this.served += this.pos - this.servedPos;
            final int read = super.read();
            this.servedPos = this.pos;
            if (read >= 0) {
                ++this.served;
            }
            assert this.limit == -1L;
            return read;
        }
        
        @Override
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            this.served += this.pos - this.servedPos;
            final int read = super.read(array, n, n2);
            this.servedPos = this.pos;
            if (read >= 0) {
                this.served += read;
            }
            return read;
        }
        
        @Override
        public long skip(final long n) throws IOException {
            throw new RuntimeException("no skipping");
        }
        
        LimitedBuffer(final InputStream inputStream) {
            super(null, 16384);
            this.servedPos = this.pos;
            super.in = new FilterInputStream(inputStream) {
                @Override
                public int read() throws IOException {
                    if (LimitedBuffer.this.buffered == LimitedBuffer.this.limit) {
                        return -1;
                    }
                    final LimitedBuffer this$0 = LimitedBuffer.this;
                    ++this$0.buffered;
                    return super.read();
                }
                
                @Override
                public int read(final byte[] array, final int n, int n2) throws IOException {
                    if (LimitedBuffer.this.buffered == LimitedBuffer.this.limit) {
                        return -1;
                    }
                    if (LimitedBuffer.this.limit != -1L) {
                        final long n3 = LimitedBuffer.this.limit - LimitedBuffer.this.buffered;
                        if (n2 > n3) {
                            n2 = (int)n3;
                        }
                    }
                    final int read = super.read(array, n, n2);
                    if (read >= 0) {
                        final LimitedBuffer this$0 = LimitedBuffer.this;
                        this$0.buffered += read;
                    }
                    return read;
                }
            };
        }
    }
}
