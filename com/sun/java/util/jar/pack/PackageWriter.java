package com.sun.java.util.jar.pack;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.OutputStream;

class PackageWriter extends BandStructure
{
    Package pkg;
    OutputStream finalOut;
    Package.Version packageVersion;
    Set<ConstantPool.Entry> requiredEntries;
    Map<Attribute.Layout, int[]> backCountTable;
    int[][] attrCounts;
    int[] maxFlags;
    List<Map<Attribute.Layout, int[]>> allLayouts;
    Attribute.Layout[] attrDefsWritten;
    private Code curCode;
    private Package.Class curClass;
    private ConstantPool.Entry[] curCPMap;
    int[] codeHist;
    int[] ldcHist;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    PackageWriter(final Package pkg, final OutputStream finalOut) throws IOException {
        this.codeHist = new int[256];
        this.ldcHist = new int[20];
        this.pkg = pkg;
        this.finalOut = finalOut;
        this.initHighestClassVersion(pkg.getHighestClassVersion());
    }
    
    void write() throws IOException {
        try {
            if (this.verbose > 0) {
                Utils.log.info("Setting up constant pool...");
            }
            this.setup();
            if (this.verbose > 0) {
                Utils.log.info("Packing...");
            }
            this.writeConstantPool();
            this.writeFiles();
            this.writeAttrDefs();
            this.writeInnerClasses();
            this.writeClassesAndByteCodes();
            this.writeAttrCounts();
            if (this.verbose > 1) {
                this.printCodeHist();
            }
            if (this.verbose > 0) {
                Utils.log.info("Coding...");
            }
            this.all_bands.chooseBandCodings();
            this.writeFileHeader();
            this.writeAllBandsTo(this.finalOut);
        }
        catch (final Exception ex) {
            Utils.log.warning("Error on output: " + ex, ex);
            if (this.verbose > 0) {
                this.finalOut.close();
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            }
            throw new Error("error packing", ex);
        }
    }
    
    void setup() {
        this.requiredEntries = new HashSet<ConstantPool.Entry>();
        this.setArchiveOptions();
        this.trimClassAttributes();
        this.collectAttributeLayouts();
        this.pkg.buildGlobalConstantPool(this.requiredEntries);
        this.setBandIndexes();
        this.makeNewAttributeBands();
        this.collectInnerClasses();
    }
    
    void chooseDefaultPackageVersion() throws IOException {
        if (this.pkg.packageVersion != null) {
            this.packageVersion = this.pkg.packageVersion;
            if (this.verbose > 0) {
                Utils.log.info("package version overridden with: " + this.packageVersion);
            }
            return;
        }
        final Package.Version highestClassVersion = this.getHighestClassVersion();
        if (highestClassVersion.lessThan(Constants.JAVA6_MAX_CLASS_VERSION)) {
            this.packageVersion = Constants.JAVA5_PACKAGE_VERSION;
        }
        else if (highestClassVersion.equals(Constants.JAVA6_MAX_CLASS_VERSION) || (highestClassVersion.equals(Constants.JAVA7_MAX_CLASS_VERSION) && !this.pkg.cp.haveExtraTags())) {
            this.packageVersion = Constants.JAVA6_PACKAGE_VERSION;
        }
        else if (highestClassVersion.equals(Constants.JAVA7_MAX_CLASS_VERSION)) {
            this.packageVersion = Constants.JAVA7_PACKAGE_VERSION;
        }
        else {
            this.packageVersion = Constants.JAVA8_PACKAGE_VERSION;
        }
        if (this.verbose > 0) {
            Utils.log.info("Highest version class file: " + highestClassVersion + " package version: " + this.packageVersion);
        }
    }
    
    void checkVersion() throws IOException {
        assert this.packageVersion != null;
        if (this.packageVersion.lessThan(Constants.JAVA7_PACKAGE_VERSION) && BandStructure.testBit(this.archiveOptions, 8)) {
            throw new IOException("Format bits for Java 7 must be zero in previous releases");
        }
        if (BandStructure.testBit(this.archiveOptions, -8192)) {
            throw new IOException("High archive option bits are reserved and must be zero: " + Integer.toHexString(this.archiveOptions));
        }
    }
    
    void setArchiveOptions() {
        int default_modtime = this.pkg.default_modtime;
        int default_modtime2 = this.pkg.default_modtime;
        int n = -1;
        int n2 = 0;
        this.archiveOptions |= this.pkg.default_options;
        for (final Package.File file : this.pkg.files) {
            final int modtime = file.modtime;
            final int options = file.options;
            if (default_modtime == 0) {
                default_modtime2 = (default_modtime = modtime);
            }
            else {
                if (default_modtime > modtime) {
                    default_modtime = modtime;
                }
                if (default_modtime2 < modtime) {
                    default_modtime2 = modtime;
                }
            }
            n &= options;
            n2 |= options;
        }
        if (this.pkg.default_modtime == 0) {
            this.pkg.default_modtime = default_modtime;
        }
        if (default_modtime != 0 && default_modtime != default_modtime2) {
            this.archiveOptions |= 0x40;
        }
        if (!BandStructure.testBit(this.archiveOptions, 32) && n != -1) {
            if (BandStructure.testBit(n, 1)) {
                this.archiveOptions |= 0x20;
                --n;
                --n2;
            }
            final Package pkg = this.pkg;
            pkg.default_options |= n;
            if (n != n2 || n != this.pkg.default_options) {
                this.archiveOptions |= 0x80;
            }
        }
        final HashMap hashMap = new HashMap();
        int n3 = 0;
        Package.Version java_MIN_CLASS_VERSION = null;
        final Iterator<Package.Class> iterator2 = this.pkg.classes.iterator();
        while (iterator2.hasNext()) {
            final Package.Version version = iterator2.next().getVersion();
            int[] array = (int[])hashMap.get(version);
            if (array == null) {
                array = new int[] { 0 };
                hashMap.put(version, array);
            }
            final int n4 = ++array[0];
            if (n3 < n4) {
                n3 = n4;
                java_MIN_CLASS_VERSION = version;
            }
        }
        hashMap.clear();
        if (java_MIN_CLASS_VERSION == null) {
            java_MIN_CLASS_VERSION = Constants.JAVA_MIN_CLASS_VERSION;
        }
        this.pkg.defaultClassVersion = java_MIN_CLASS_VERSION;
        if (this.verbose > 0) {
            Utils.log.info("Consensus version number in segment is " + java_MIN_CLASS_VERSION);
        }
        if (this.verbose > 0) {
            Utils.log.info("Highest version number in segment is " + this.pkg.getHighestClassVersion());
        }
        for (final Package.Class class1 : this.pkg.classes) {
            if (!class1.getVersion().equals(java_MIN_CLASS_VERSION)) {
                final Attribute classFileVersionAttr = this.makeClassFileVersionAttr(class1.getVersion());
                if (this.verbose > 1) {
                    Utils.log.fine("Version " + class1.getVersion() + " of " + class1 + " doesn't match package version " + java_MIN_CLASS_VERSION);
                }
                class1.addAttribute(classFileVersionAttr);
            }
        }
        for (final Package.File file2 : this.pkg.files) {
            final long fileLength = file2.getFileLength();
            if (fileLength != (int)fileLength) {
                this.archiveOptions |= 0x100;
                if (this.verbose > 0) {
                    Utils.log.info("Note: Huge resource file " + file2.getFileName() + " forces 64-bit sizing");
                    break;
                }
                break;
            }
        }
        int n5 = 0;
        int n6 = 0;
        final Iterator<Package.Class> iterator5 = this.pkg.classes.iterator();
        while (iterator5.hasNext()) {
            for (final Package.Class.Method method : iterator5.next().getMethods()) {
                if (method.code != null) {
                    if (method.code.attributeSize() == 0) {
                        ++n6;
                    }
                    else {
                        if (BandStructure.shortCodeHeader(method.code) == 0) {
                            continue;
                        }
                        n5 += 3;
                    }
                }
            }
        }
        if (n5 > n6) {
            this.archiveOptions |= 0x4;
        }
        if (this.verbose > 0) {
            Utils.log.info("archiveOptions = 0b" + Integer.toBinaryString(this.archiveOptions));
        }
    }
    
    void writeFileHeader() throws IOException {
        this.chooseDefaultPackageVersion();
        this.writeArchiveMagic();
        this.writeArchiveHeader();
    }
    
    private void putMagicInt32(final int n) throws IOException {
        int n2 = n;
        for (int i = 0; i < 4; ++i) {
            this.archive_magic.putByte(0xFF & n2 >>> 24);
            n2 <<= 8;
        }
    }
    
    void writeArchiveMagic() throws IOException {
        this.pkg.getClass();
        this.putMagicInt32(-889270259);
    }
    
    void writeArchiveHeader() throws IOException {
        int n = 15;
        boolean testBit = BandStructure.testBit(this.archiveOptions, 1);
        if (!testBit) {
            testBit = (testBit | this.band_headers.length() != 0 | this.attrDefsWritten.length != 0);
            if (testBit) {
                this.archiveOptions |= 0x1;
            }
        }
        if (testBit) {
            n += 2;
        }
        boolean testBit2 = BandStructure.testBit(this.archiveOptions, 16);
        if (!testBit2) {
            testBit2 = (testBit2 | this.archiveNextCount > 0 | this.pkg.default_modtime != 0);
            if (testBit2) {
                this.archiveOptions |= 0x10;
            }
        }
        if (testBit2) {
            n += 5;
        }
        boolean testBit3 = BandStructure.testBit(this.archiveOptions, 2);
        if (!testBit3) {
            testBit3 |= this.pkg.cp.haveNumbers();
            if (testBit3) {
                this.archiveOptions |= 0x2;
            }
        }
        if (testBit3) {
            n += 4;
        }
        boolean testBit4 = BandStructure.testBit(this.archiveOptions, 8);
        if (!testBit4) {
            testBit4 |= this.pkg.cp.haveExtraTags();
            if (testBit4) {
                this.archiveOptions |= 0x8;
            }
        }
        if (testBit4) {
            n += 4;
        }
        this.checkVersion();
        this.archive_header_0.putInt(this.packageVersion.minor);
        this.archive_header_0.putInt(this.packageVersion.major);
        if (this.verbose > 0) {
            Utils.log.info("Package Version for this segment:" + this.packageVersion);
        }
        this.archive_header_0.putInt(this.archiveOptions);
        assert this.archive_header_0.length() == 3;
        if (testBit2) {
            assert this.archive_header_S.length() == 0;
            this.archive_header_S.putInt(0);
            assert this.archive_header_S.length() == 1;
            this.archive_header_S.putInt(0);
            assert this.archive_header_S.length() == 2;
        }
        if (testBit2) {
            this.archive_header_1.putInt(this.archiveNextCount);
            this.archive_header_1.putInt(this.pkg.default_modtime);
            this.archive_header_1.putInt(this.pkg.files.size());
        }
        else {
            assert this.pkg.files.isEmpty();
        }
        if (testBit) {
            this.archive_header_1.putInt(this.band_headers.length());
            this.archive_header_1.putInt(this.attrDefsWritten.length);
        }
        else {
            assert this.band_headers.length() == 0;
            assert this.attrDefsWritten.length == 0;
        }
        this.writeConstantPoolCounts(testBit3, testBit4);
        this.archive_header_1.putInt(this.pkg.getAllInnerClasses().size());
        this.archive_header_1.putInt(this.pkg.defaultClassVersion.minor);
        this.archive_header_1.putInt(this.pkg.defaultClassVersion.major);
        this.archive_header_1.putInt(this.pkg.classes.size());
        assert this.archive_header_0.length() + this.archive_header_S.length() + this.archive_header_1.length() == n;
        this.archiveSize0 = 0L;
        this.archiveSize1 = this.all_bands.outputSize();
        this.archiveSize0 += this.archive_magic.outputSize();
        this.archiveSize0 += this.archive_header_0.outputSize();
        this.archiveSize0 += this.archive_header_S.outputSize();
        this.archiveSize1 -= this.archiveSize0;
        if (testBit2) {
            final int n2 = (int)(this.archiveSize1 >>> 32);
            final int n3 = (int)(this.archiveSize1 >>> 0);
            this.archive_header_S.patchValue(0, n2);
            this.archive_header_S.patchValue(1, n3);
            final int length = PackageWriter.UNSIGNED5.getLength(0);
            this.archiveSize0 += PackageWriter.UNSIGNED5.getLength(n2) - length;
            this.archiveSize0 += PackageWriter.UNSIGNED5.getLength(n3) - length;
        }
        if (this.verbose > 1) {
            Utils.log.fine("archive sizes: " + this.archiveSize0 + "+" + this.archiveSize1);
        }
        assert this.all_bands.outputSize() == this.archiveSize0 + this.archiveSize1;
    }
    
    void writeConstantPoolCounts(final boolean b, final boolean b2) throws IOException {
        for (final byte b3 : ConstantPool.TAGS_IN_ORDER) {
            final int size = this.pkg.cp.getIndexByTag(b3).size();
            Label_0226: {
                switch (b3) {
                    case 1: {
                        if (size > 0 && !PackageWriter.$assertionsDisabled && this.pkg.cp.getIndexByTag(b3).get(0) != ConstantPool.getUtf8Entry("")) {
                            throw new AssertionError();
                        }
                        break;
                    }
                    case 3:
                    case 4:
                    case 5:
                    case 6: {
                        if (b) {
                            break;
                        }
                        assert size == 0;
                        break Label_0226;
                    }
                    case 15:
                    case 16:
                    case 17:
                    case 18: {
                        if (b2) {
                            break;
                        }
                        assert size == 0;
                        break Label_0226;
                    }
                }
                this.archive_header_1.putInt(size);
            }
        }
    }
    
    @Override
    protected ConstantPool.Index getCPIndex(final byte b) {
        return this.pkg.cp.getIndexByTag(b);
    }
    
    void writeConstantPool() throws IOException {
        final ConstantPool.IndexGroup cp = this.pkg.cp;
        if (this.verbose > 0) {
            Utils.log.info("Writing CP");
        }
        for (final byte b : ConstantPool.TAGS_IN_ORDER) {
            final ConstantPool.Index indexByTag = cp.getIndexByTag(b);
            final ConstantPool.Entry[] cpMap = indexByTag.cpMap;
            if (this.verbose > 0) {
                Utils.log.info("Writing " + cpMap.length + " " + ConstantPool.tagName(b) + " entries...");
            }
            if (this.optDumpBands) {
                try (final PrintStream printStream = new PrintStream(BandStructure.getDumpStream(indexByTag, ".idx"))) {
                    BandStructure.printArrayTo(printStream, cpMap, 0, cpMap.length);
                }
            }
            switch (b) {
                case 1: {
                    this.writeUtf8Bands(cpMap);
                    break;
                }
                case 3: {
                    for (int j = 0; j < cpMap.length; ++j) {
                        this.cp_Int.putInt((int)((ConstantPool.NumberEntry)cpMap[j]).numberValue());
                    }
                    break;
                }
                case 4: {
                    for (int k = 0; k < cpMap.length; ++k) {
                        this.cp_Float.putInt(Float.floatToIntBits((float)((ConstantPool.NumberEntry)cpMap[k]).numberValue()));
                    }
                    break;
                }
                case 5: {
                    for (int l = 0; l < cpMap.length; ++l) {
                        final long longValue = (long)((ConstantPool.NumberEntry)cpMap[l]).numberValue();
                        this.cp_Long_hi.putInt((int)(longValue >>> 32));
                        this.cp_Long_lo.putInt((int)(longValue >>> 0));
                    }
                    break;
                }
                case 6: {
                    for (int n = 0; n < cpMap.length; ++n) {
                        final long doubleToLongBits = Double.doubleToLongBits((double)((ConstantPool.NumberEntry)cpMap[n]).numberValue());
                        this.cp_Double_hi.putInt((int)(doubleToLongBits >>> 32));
                        this.cp_Double_lo.putInt((int)(doubleToLongBits >>> 0));
                    }
                    break;
                }
                case 8: {
                    for (int n2 = 0; n2 < cpMap.length; ++n2) {
                        this.cp_String.putRef(((ConstantPool.StringEntry)cpMap[n2]).ref);
                    }
                    break;
                }
                case 7: {
                    for (int n3 = 0; n3 < cpMap.length; ++n3) {
                        this.cp_Class.putRef(((ConstantPool.ClassEntry)cpMap[n3]).ref);
                    }
                    break;
                }
                case 13: {
                    this.writeSignatureBands(cpMap);
                    break;
                }
                case 12: {
                    for (int n4 = 0; n4 < cpMap.length; ++n4) {
                        final ConstantPool.DescriptorEntry descriptorEntry = (ConstantPool.DescriptorEntry)cpMap[n4];
                        this.cp_Descr_name.putRef(descriptorEntry.nameRef);
                        this.cp_Descr_type.putRef(descriptorEntry.typeRef);
                    }
                    break;
                }
                case 9: {
                    this.writeMemberRefs(b, cpMap, this.cp_Field_class, this.cp_Field_desc);
                    break;
                }
                case 10: {
                    this.writeMemberRefs(b, cpMap, this.cp_Method_class, this.cp_Method_desc);
                    break;
                }
                case 11: {
                    this.writeMemberRefs(b, cpMap, this.cp_Imethod_class, this.cp_Imethod_desc);
                    break;
                }
                case 15: {
                    for (int n5 = 0; n5 < cpMap.length; ++n5) {
                        final ConstantPool.MethodHandleEntry methodHandleEntry = (ConstantPool.MethodHandleEntry)cpMap[n5];
                        this.cp_MethodHandle_refkind.putInt(methodHandleEntry.refKind);
                        this.cp_MethodHandle_member.putRef(methodHandleEntry.memRef);
                    }
                    break;
                }
                case 16: {
                    for (int n6 = 0; n6 < cpMap.length; ++n6) {
                        this.cp_MethodType.putRef(((ConstantPool.MethodTypeEntry)cpMap[n6]).typeRef);
                    }
                    break;
                }
                case 18: {
                    for (int n7 = 0; n7 < cpMap.length; ++n7) {
                        final ConstantPool.InvokeDynamicEntry invokeDynamicEntry = (ConstantPool.InvokeDynamicEntry)cpMap[n7];
                        this.cp_InvokeDynamic_spec.putRef(invokeDynamicEntry.bssRef);
                        this.cp_InvokeDynamic_desc.putRef(invokeDynamicEntry.descRef);
                    }
                    break;
                }
                case 17: {
                    for (int n8 = 0; n8 < cpMap.length; ++n8) {
                        final ConstantPool.BootstrapMethodEntry bootstrapMethodEntry = (ConstantPool.BootstrapMethodEntry)cpMap[n8];
                        this.cp_BootstrapMethod_ref.putRef(bootstrapMethodEntry.bsmRef);
                        this.cp_BootstrapMethod_arg_count.putInt(bootstrapMethodEntry.argRefs.length);
                        final ConstantPool.Entry[] argRefs = bootstrapMethodEntry.argRefs;
                        for (int length2 = argRefs.length, n9 = 0; n9 < length2; ++n9) {
                            this.cp_BootstrapMethod_arg.putRef(argRefs[n9]);
                        }
                    }
                    break;
                }
                default: {
                    throw new AssertionError((Object)"unexpected CP tag in package");
                }
            }
        }
        if (this.optDumpBands || this.verbose > 1) {
            for (byte b2 = 50; b2 < 54; ++b2) {
                final ConstantPool.Index indexByTag2 = cp.getIndexByTag(b2);
                if (indexByTag2 != null) {
                    if (!indexByTag2.isEmpty()) {
                        final ConstantPool.Entry[] cpMap2 = indexByTag2.cpMap;
                        if (this.verbose > 1) {
                            Utils.log.info("Index group " + ConstantPool.tagName(b2) + " contains " + cpMap2.length + " entries.");
                        }
                        if (this.optDumpBands) {
                            try (final PrintStream printStream2 = new PrintStream(BandStructure.getDumpStream(indexByTag2.debugName, b2, ".gidx", indexByTag2))) {
                                BandStructure.printArrayTo(printStream2, cpMap2, 0, cpMap2.length, true);
                            }
                        }
                    }
                }
            }
        }
    }
    
    void writeUtf8Bands(final ConstantPool.Entry[] array) throws IOException {
        if (array.length == 0) {
            return;
        }
        assert array[0].stringValue().equals("");
        final char[][] array2 = new char[array.length][];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = array[i].stringValue().toCharArray();
        }
        final int[] array3 = new int[array.length];
        char[] array4 = new char[0];
        for (int j = 0; j < array2.length; ++j) {
            int n;
            char[] array5;
            for (n = 0, array5 = array2[j]; n < Math.min(array5.length, array4.length) && array5[n] == array4[n]; ++n) {}
            array3[j] = n;
            if (j >= 2) {
                this.cp_Utf8_prefix.putInt(n);
            }
            else {
                assert n == 0;
            }
            array4 = array5;
        }
        for (int k = 0; k < array2.length; ++k) {
            final char[] array6 = array2[k];
            final int n2 = array3[k];
            final int n3 = array6.length - array3[k];
            boolean tryAlternateEncoding = false;
            if (n3 == 0) {
                tryAlternateEncoding = (k >= 1);
            }
            else if (this.optBigStrings && this.effort > 1 && n3 > 100) {
                int n4 = 0;
                for (int l = 0; l < n3; ++l) {
                    if (array6[n2 + l] > '\u007f') {
                        ++n4;
                    }
                }
                if (n4 > 100) {
                    tryAlternateEncoding = this.tryAlternateEncoding(k, n4, array6, n2);
                }
            }
            if (k < 1) {
                assert !tryAlternateEncoding;
                assert n3 == 0;
            }
            else if (tryAlternateEncoding) {
                this.cp_Utf8_suffix.putInt(0);
                this.cp_Utf8_big_suffix.putInt(n3);
            }
            else {
                assert n3 != 0;
                this.cp_Utf8_suffix.putInt(n3);
                for (int n5 = 0; n5 < n3; ++n5) {
                    this.cp_Utf8_chars.putInt(array6[n2 + n5]);
                }
            }
        }
        if (this.verbose > 0) {
            final int length = this.cp_Utf8_chars.length();
            final int length2 = this.cp_Utf8_big_chars.length();
            Utils.log.info("Utf8string #CHARS=" + (length + length2) + " #PACKEDCHARS=" + length2);
        }
    }
    
    private boolean tryAlternateEncoding(final int n, final int n2, final char[] array, final int n3) {
        final int n4 = array.length - n3;
        final int[] array2 = new int[n4];
        for (int i = 0; i < n4; ++i) {
            array2[i] = array[n3 + i];
        }
        final CodingChooser codingChooser = this.getCodingChooser();
        final Coding regularCoding = this.cp_Utf8_big_chars.regularCoding;
        final String string = "(Utf8_big_" + n + ")";
        final int[] array3 = { 0, 0 };
        if (this.verbose > 1 || codingChooser.verbose > 1) {
            Utils.log.fine("--- chooseCoding " + string);
        }
        final CodingMethod choose = codingChooser.choose(array2, regularCoding, array3);
        final Coding regularCoding2 = this.cp_Utf8_chars.regularCoding;
        if (this.verbose > 1) {
            Utils.log.fine("big string[" + n + "] len=" + n4 + " #wide=" + n2 + " size=" + array3[0] + "/z=" + array3[1] + " coding " + choose);
        }
        if (choose != regularCoding2) {
            final int n5 = array3[1];
            final int[] computeSize = codingChooser.computeSize(regularCoding2, array2);
            final int n6 = computeSize[1];
            final int max = Math.max(5, n6 / 1000);
            if (this.verbose > 1) {
                Utils.log.fine("big string[" + n + "] normalSize=" + computeSize[0] + "/z=" + computeSize[1] + " win=" + (n5 < n6 - max));
            }
            if (n5 < n6 - max) {
                this.cp_Utf8_big_chars.newIntBand(string).initializeValues(array2);
                return true;
            }
        }
        return false;
    }
    
    void writeSignatureBands(final ConstantPool.Entry[] array) throws IOException {
        for (int i = 0; i < array.length; ++i) {
            final ConstantPool.SignatureEntry signatureEntry = (ConstantPool.SignatureEntry)array[i];
            this.cp_Signature_form.putRef(signatureEntry.formRef);
            for (int j = 0; j < signatureEntry.classRefs.length; ++j) {
                this.cp_Signature_classes.putRef(signatureEntry.classRefs[j]);
            }
        }
    }
    
    void writeMemberRefs(final byte b, final ConstantPool.Entry[] array, final CPRefBand cpRefBand, final CPRefBand cpRefBand2) throws IOException {
        for (int i = 0; i < array.length; ++i) {
            final ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)array[i];
            cpRefBand.putRef(memberEntry.classRef);
            cpRefBand2.putRef(memberEntry.descRef);
        }
    }
    
    void writeFiles() throws IOException {
        final int size = this.pkg.files.size();
        if (size == 0) {
            return;
        }
        int archiveOptions = this.archiveOptions;
        final boolean testBit = BandStructure.testBit(archiveOptions, 256);
        final boolean testBit2 = BandStructure.testBit(archiveOptions, 64);
        int testBit3 = BandStructure.testBit(archiveOptions, 128) ? 1 : 0;
        if (testBit3 == 0) {
            final Iterator<Package.File> iterator = this.pkg.files.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().isClassStub()) {
                    testBit3 = 1;
                    archiveOptions |= 0x80;
                    this.archiveOptions = archiveOptions;
                    break;
                }
            }
        }
        if (testBit || testBit2 || testBit3 != 0 || !this.pkg.files.isEmpty()) {
            this.archiveOptions = (archiveOptions | 0x10);
        }
        for (final Package.File file : this.pkg.files) {
            this.file_name.putRef(file.name);
            final long fileLength = file.getFileLength();
            this.file_size_lo.putInt((int)fileLength);
            if (testBit) {
                this.file_size_hi.putInt((int)(fileLength >>> 32));
            }
            if (testBit2) {
                this.file_modtime.putInt(file.modtime - this.pkg.default_modtime);
            }
            if (testBit3 != 0) {
                this.file_options.putInt(file.options);
            }
            file.writeTo(this.file_bits.collectorStream());
            if (this.verbose > 1) {
                Utils.log.fine("Wrote " + fileLength + " bytes of " + file.name.stringValue());
            }
        }
        if (this.verbose > 0) {
            Utils.log.info("Wrote " + size + " resource files");
        }
    }
    
    void collectAttributeLayouts() {
        this.maxFlags = new int[4];
        this.allLayouts = new FixedList<Map<Attribute.Layout, int[]>>(4);
        for (int i = 0; i < 4; ++i) {
            this.allLayouts.set(i, new HashMap<Attribute.Layout, int[]>());
        }
        for (final Package.Class class1 : this.pkg.classes) {
            this.visitAttributeLayoutsIn(0, class1);
            final Iterator<Package.Class.Field> iterator2 = class1.getFields().iterator();
            while (iterator2.hasNext()) {
                this.visitAttributeLayoutsIn(1, iterator2.next());
            }
            for (final Package.Class.Method method : class1.getMethods()) {
                this.visitAttributeLayoutsIn(2, method);
                if (method.code != null) {
                    this.visitAttributeLayoutsIn(3, method.code);
                }
            }
        }
        for (int j = 0; j < 4; ++j) {
            final int size = this.allLayouts.get(j).size();
            boolean haveFlagsHi = this.haveFlagsHi(j);
            if (size >= 24) {
                this.archiveOptions |= 1 << 9 + j;
                haveFlagsHi = true;
                if (this.verbose > 0) {
                    Utils.log.info("Note: Many " + Attribute.contextName(j) + " attributes forces 63-bit flags");
                }
            }
            if (this.verbose > 1) {
                Utils.log.fine(Attribute.contextName(j) + ".maxFlags = 0x" + Integer.toHexString(this.maxFlags[j]));
                Utils.log.fine(Attribute.contextName(j) + ".#layouts = " + size);
            }
            assert this.haveFlagsHi(j) == haveFlagsHi;
        }
        this.initAttrIndexLimit();
        for (int k = 0; k < 4; ++k) {
            assert (this.attrFlagMask[k] & (long)this.maxFlags[k]) == 0x0L;
        }
        this.backCountTable = new HashMap<Attribute.Layout, int[]>();
        this.attrCounts = new int[4][];
        for (int l = 0; l < 4; ++l) {
            final long n = ~((long)this.maxFlags[l] | this.attrFlagMask[l]);
            assert this.attrIndexLimit[l] > 0;
            assert this.attrIndexLimit[l] < 64;
            long n2 = n & (1L << this.attrIndexLimit[l]) - 1L;
            int n3 = 0;
            final Map map = this.allLayouts.get(l);
            final Map.Entry[] array = new Map.Entry[map.size()];
            map.entrySet().toArray(array);
            Arrays.sort(array, new Comparator<Map.Entry<Attribute.Layout, int[]>>() {
                @Override
                public int compare(final Map.Entry<Attribute.Layout, int[]> entry, final Map.Entry<Attribute.Layout, int[]> entry2) {
                    final int n = -(entry.getValue()[0] - entry2.getValue()[0]);
                    if (n != 0) {
                        return n;
                    }
                    return entry.getKey().compareTo((Attribute.Layout)entry2.getKey());
                }
            });
            this.attrCounts[l] = new int[this.attrIndexLimit[l] + array.length];
            for (int n4 = 0; n4 < array.length; ++n4) {
                final Map.Entry entry = array[n4];
                final Attribute.Layout layout = entry.getKey();
                final int n5 = ((int[])entry.getValue())[0];
                final Integer n6 = this.attrIndexTable.get(layout);
                int n7;
                if (n6 != null) {
                    n7 = n6;
                }
                else if (n2 != 0L) {
                    while ((n2 & 0x1L) == 0x0L) {
                        n2 >>>= 1;
                        ++n3;
                    }
                    --n2;
                    n7 = this.setAttributeLayoutIndex(layout, n3);
                }
                else {
                    n7 = this.setAttributeLayoutIndex(layout, -1);
                }
                this.attrCounts[l][n7] = n5;
                final Attribute.Layout.Element[] callables = layout.getCallables();
                final int[] array2 = new int[callables.length];
                for (int n8 = 0; n8 < callables.length; ++n8) {
                    assert callables[n8].kind == 10;
                    if (!callables[n8].flagTest((byte)8)) {
                        array2[n8] = -1;
                    }
                }
                this.backCountTable.put(layout, array2);
                if (n6 == null) {
                    final ConstantPool.Utf8Entry utf8Entry = ConstantPool.getUtf8Entry(layout.name());
                    final ConstantPool.Utf8Entry utf8Entry2 = ConstantPool.getUtf8Entry(layout.layoutForClassVersion(this.getHighestClassVersion()));
                    this.requiredEntries.add(utf8Entry);
                    this.requiredEntries.add(utf8Entry2);
                    if (this.verbose > 0) {
                        if (n7 < this.attrIndexLimit[l]) {
                            Utils.log.info("Using free flag bit 1<<" + n7 + " for " + n5 + " occurrences of " + layout);
                        }
                        else {
                            Utils.log.info("Using overflow index " + n7 + " for " + n5 + " occurrences of " + layout);
                        }
                    }
                }
            }
        }
        this.maxFlags = null;
        this.allLayouts = null;
    }
    
    void visitAttributeLayoutsIn(final int n, final Attribute.Holder holder) {
        final int[] maxFlags = this.maxFlags;
        maxFlags[n] |= holder.flags;
        final Iterator<Attribute> iterator = holder.getAttributes().iterator();
        while (iterator.hasNext()) {
            final Attribute.Layout layout = iterator.next().layout();
            final Map map = this.allLayouts.get(n);
            int[] array = (int[])map.get(layout);
            if (array == null) {
                map.put(layout, array = new int[] { 0 });
            }
            if (array[0] < Integer.MAX_VALUE) {
                final int[] array2 = array;
                final int n2 = 0;
                ++array2[n2];
            }
        }
    }
    
    void writeAttrDefs() throws IOException {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < 4; ++i) {
            for (int size = this.attrDefs.get(i).size(), j = 0; j < size; ++j) {
                int n = i;
                if (j < this.attrIndexLimit[i]) {
                    n |= j + 1 << 2;
                    assert n < 256;
                    if (!BandStructure.testBit(this.attrDefSeen[i], 1L << j)) {
                        continue;
                    }
                }
                final Attribute.Layout layout = this.attrDefs.get(i).get(j);
                list.add(new Object[] { n, layout });
                assert Integer.valueOf(j).equals(this.attrIndexTable.get(layout));
            }
        }
        final int size2 = list.size();
        final Object[][] array = new Object[size2][];
        list.toArray(array);
        Arrays.sort(array, new Comparator<Object[]>() {
            @Override
            public int compare(final Object[] array, final Object[] array2) {
                final int compareTo = ((Comparable)array[0]).compareTo(array2[0]);
                if (compareTo != 0) {
                    return compareTo;
                }
                final Integer n = PackageWriter.this.attrIndexTable.get(array[1]);
                final Integer n2 = PackageWriter.this.attrIndexTable.get(array2[1]);
                assert n != null;
                assert n2 != null;
                return n.compareTo(n2);
            }
        });
        this.attrDefsWritten = new Attribute.Layout[size2];
        try (final PrintStream printStream = this.optDumpBands ? new PrintStream(BandStructure.getDumpStream(this.attr_definition_headers, ".def")) : null) {
            final int[] copy = Arrays.copyOf(this.attrIndexLimit, 4);
            for (int k = 0; k < array.length; ++k) {
                final int intValue = (int)array[k][0];
                final Attribute.Layout layout2 = (Attribute.Layout)array[k][1];
                this.attrDefsWritten[k] = layout2;
                assert (intValue & 0x3) == layout2.ctype();
                this.attr_definition_headers.putByte(intValue);
                this.attr_definition_name.putRef(ConstantPool.getUtf8Entry(layout2.name()));
                this.attr_definition_layout.putRef(ConstantPool.getUtf8Entry(layout2.layoutForClassVersion(this.getHighestClassVersion())));
                boolean b = false;
                assert b = true;
                if (b) {
                    int n2 = (intValue >> 2) - 1;
                    if (n2 < 0) {
                        n2 = copy[layout2.ctype()]++;
                    }
                    final int intValue2 = this.attrIndexTable.get(layout2);
                    assert n2 == intValue2;
                }
                if (printStream != null) {
                    printStream.println((intValue >> 2) - 1 + " " + layout2);
                }
            }
        }
    }
    
    void writeAttrCounts() throws IOException {
        for (int i = 0; i < 4; ++i) {
            final IntBand attrBand = BandStructure.getAttrBand(this.attrBands[i], 4);
            final Attribute.Layout[] array = new Attribute.Layout[this.attrDefs.get(i).size()];
            this.attrDefs.get(i).toArray(array);
            boolean b = true;
            while (true) {
                for (int j = 0; j < array.length; ++j) {
                    final Attribute.Layout layout = array[j];
                    if (layout != null) {
                        if (b == this.isPredefinedAttr(i, j)) {
                            if (this.attrCounts[i][j] != 0) {
                                final int[] array2 = this.backCountTable.get(layout);
                                for (int k = 0; k < array2.length; ++k) {
                                    if (array2[k] >= 0) {
                                        final int n = array2[k];
                                        array2[k] = -1;
                                        attrBand.putInt(n);
                                        assert layout.getCallables()[k].flagTest((byte)8);
                                    }
                                    else {
                                        assert !layout.getCallables()[k].flagTest((byte)8);
                                    }
                                }
                            }
                        }
                    }
                }
                if (!b) {
                    break;
                }
                b = false;
            }
        }
    }
    
    void trimClassAttributes() {
        for (final Package.Class class1 : this.pkg.classes) {
            class1.minimizeSourceFile();
            assert class1.getAttribute(Package.attrBootstrapMethodsEmpty) == null;
        }
    }
    
    void collectInnerClasses() {
        final HashMap hashMap = new HashMap();
        for (final Package.Class class1 : this.pkg.classes) {
            if (!class1.hasInnerClasses()) {
                continue;
            }
            for (final Package.InnerClass innerClass : class1.getInnerClasses()) {
                final Package.InnerClass innerClass2 = (Package.InnerClass)hashMap.put(innerClass.thisClass, innerClass);
                if (innerClass2 != null && !innerClass2.equals(innerClass) && innerClass2.predictable) {
                    hashMap.put(innerClass2.thisClass, innerClass2);
                }
            }
        }
        final Package.InnerClass[] array = new Package.InnerClass[hashMap.size()];
        hashMap.values().toArray(array);
        Arrays.sort(array);
        this.pkg.setAllInnerClasses(Arrays.asList(array));
        final Iterator<Package.Class> iterator3 = this.pkg.classes.iterator();
        while (iterator3.hasNext()) {
            iterator3.next().minimizeLocalICs();
        }
    }
    
    void writeInnerClasses() throws IOException {
        for (final Package.InnerClass innerClass : this.pkg.getAllInnerClasses()) {
            int flags = innerClass.flags;
            assert (flags & 0x10000) == 0x0;
            if (!innerClass.predictable) {
                flags |= 0x10000;
            }
            this.ic_this_class.putRef(innerClass.thisClass);
            this.ic_flags.putInt(flags);
            if (innerClass.predictable) {
                continue;
            }
            this.ic_outer_class.putRef(innerClass.outerClass);
            this.ic_name.putRef(innerClass.name);
        }
    }
    
    void writeLocalInnerClasses(final Package.Class class1) throws IOException {
        final List<Package.InnerClass> innerClasses = class1.getInnerClasses();
        this.class_InnerClasses_N.putInt(innerClasses.size());
        for (final Package.InnerClass innerClass : innerClasses) {
            this.class_InnerClasses_RC.putRef(innerClass.thisClass);
            if (innerClass.equals(this.pkg.getGlobalInnerClass(innerClass.thisClass))) {
                this.class_InnerClasses_F.putInt(0);
            }
            else {
                int flags = innerClass.flags;
                if (flags == 0) {
                    flags = 65536;
                }
                this.class_InnerClasses_F.putInt(flags);
                this.class_InnerClasses_outer_RCN.putRef(innerClass.outerClass);
                this.class_InnerClasses_name_RUN.putRef(innerClass.name);
            }
        }
    }
    
    void writeClassesAndByteCodes() throws IOException {
        final Package.Class[] array = new Package.Class[this.pkg.classes.size()];
        this.pkg.classes.toArray(array);
        if (this.verbose > 0) {
            Utils.log.info("  ...scanning " + array.length + " classes...");
        }
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            final Package.Class class1 = array[i];
            if (this.verbose > 1) {
                Utils.log.fine("Scanning " + class1);
            }
            final ConstantPool.ClassEntry thisClass = class1.thisClass;
            ConstantPool.ClassEntry superClass = class1.superClass;
            final ConstantPool.ClassEntry[] interfaces = class1.interfaces;
            assert superClass != thisClass;
            if (superClass == null) {
                superClass = thisClass;
            }
            this.class_this.putRef(thisClass);
            this.class_super.putRef(superClass);
            this.class_interface_count.putInt(class1.interfaces.length);
            for (int j = 0; j < interfaces.length; ++j) {
                this.class_interface.putRef(interfaces[j]);
            }
            this.writeMembers(class1);
            this.writeAttrs(0, class1, class1);
            ++n;
            if (this.verbose > 0 && n % 1000 == 0) {
                Utils.log.info("Have scanned " + n + " classes...");
            }
        }
    }
    
    void writeMembers(final Package.Class class1) throws IOException {
        final List<Package.Class.Field> fields = class1.getFields();
        this.class_field_count.putInt(fields.size());
        for (final Package.Class.Field field : fields) {
            this.field_descr.putRef(field.getDescriptor());
            this.writeAttrs(1, field, class1);
        }
        final List<Package.Class.Method> methods = class1.getMethods();
        this.class_method_count.putInt(methods.size());
        for (final Package.Class.Method method : methods) {
            this.method_descr.putRef(method.getDescriptor());
            this.writeAttrs(2, method, class1);
            assert method.code != null == (method.getAttribute(this.attrCodeEmpty) != null);
            if (method.code == null) {
                continue;
            }
            this.writeCodeHeader(method.code);
            this.writeByteCodes(method.code);
        }
    }
    
    void writeCodeHeader(final Code code) throws IOException {
        final boolean testBit = BandStructure.testBit(this.archiveOptions, 4);
        final int attributeSize = code.attributeSize();
        int shortCodeHeader = BandStructure.shortCodeHeader(code);
        if (!testBit && attributeSize > 0) {
            shortCodeHeader = 0;
        }
        if (this.verbose > 2) {
            Utils.log.fine("Code sizes info " + code.max_stack + " " + code.max_locals + " " + code.getHandlerCount() + " " + code.getMethod().getArgumentSize() + " " + attributeSize + ((shortCodeHeader > 0) ? (" SHORT=" + shortCodeHeader) : ""));
        }
        this.code_headers.putByte(shortCodeHeader);
        if (shortCodeHeader == 0) {
            this.code_max_stack.putInt(code.getMaxStack());
            this.code_max_na_locals.putInt(code.getMaxNALocals());
            this.code_handler_count.putInt(code.getHandlerCount());
        }
        else {
            assert attributeSize == 0;
            assert code.getHandlerCount() < this.shortCodeHeader_h_limit;
        }
        this.writeCodeHandlers(code);
        if (shortCodeHeader == 0 || testBit) {
            this.writeAttrs(3, code, code.thisClass());
        }
    }
    
    void writeCodeHandlers(final Code code) throws IOException {
        for (int i = 0; i < code.getHandlerCount(); ++i) {
            this.code_handler_class_RCN.putRef(code.handler_class[i]);
            final int encodeBCI = code.encodeBCI(code.handler_start[i]);
            this.code_handler_start_P.putInt(encodeBCI);
            final int n = code.encodeBCI(code.handler_end[i]) - encodeBCI;
            this.code_handler_end_PO.putInt(n);
            this.code_handler_catch_PO.putInt(code.encodeBCI(code.handler_catch[i]) - (encodeBCI + n));
        }
    }
    
    void writeAttrs(final int n, final Attribute.Holder holder, final Package.Class class1) throws IOException {
        final MultiBand multiBand = this.attrBands[n];
        final IntBand attrBand = BandStructure.getAttrBand(multiBand, 0);
        final IntBand attrBand2 = BandStructure.getAttrBand(multiBand, 1);
        final boolean haveFlagsHi = this.haveFlagsHi(n);
        assert this.attrIndexLimit[n] == (haveFlagsHi ? 63 : 32);
        if (holder.attributes == null) {
            attrBand2.putInt(holder.flags);
            if (haveFlagsHi) {
                attrBand.putInt(0);
            }
            return;
        }
        if (this.verbose > 3) {
            Utils.log.fine("Transmitting attrs for " + holder + " flags=" + Integer.toHexString(holder.flags));
        }
        long n2 = this.attrFlagMask[n];
        long n3 = 0L;
        int n4 = 0;
        for (final Attribute attribute : holder.attributes) {
            final Attribute.Layout layout = attribute.layout();
            final int intValue = this.attrIndexTable.get(layout);
            assert this.attrDefs.get(n).get(intValue) == layout;
            if (this.verbose > 3) {
                Utils.log.fine("add attr @" + intValue + " " + attribute + " in " + holder);
            }
            if (intValue < this.attrIndexLimit[n] && BandStructure.testBit(n2, 1L << intValue)) {
                if (this.verbose > 3) {
                    Utils.log.fine("Adding flag bit 1<<" + intValue + " in " + Long.toHexString(n2));
                }
                assert !BandStructure.testBit(holder.flags, 1L << intValue);
                n3 |= 1L << intValue;
                n2 -= 1L << intValue;
            }
            else {
                n3 |= 0x10000L;
                ++n4;
                if (this.verbose > 3) {
                    Utils.log.fine("Adding overflow attr #" + n4);
                }
                BandStructure.getAttrBand(multiBand, 3).putInt(intValue);
            }
            if (layout.bandCount == 0) {
                if (layout != this.attrInnerClassesEmpty) {
                    continue;
                }
                this.writeLocalInnerClasses((Package.Class)holder);
            }
            else {
                assert attribute.fixups == null;
                final Band[] array = this.attrBandTable.get(layout);
                assert array != null;
                assert array.length == layout.bandCount;
                final int[] array2 = this.backCountTable.get(layout);
                assert array2 != null;
                assert array2.length == layout.getCallables().length;
                if (this.verbose > 2) {
                    Utils.log.fine("writing " + attribute + " in " + holder);
                }
                final boolean b = n == 1 && layout == this.attrConstantValue;
                if (b) {
                    this.setConstantValueIndex((Package.Class.Field)holder);
                }
                attribute.parse(class1, attribute.bytes(), 0, attribute.size(), new Attribute.ValueStream() {
                    @Override
                    public void putInt(final int n, final int n2) {
                        ((IntBand)array[n]).putInt(n2);
                    }
                    
                    @Override
                    public void putRef(final int n, final ConstantPool.Entry entry) {
                        ((CPRefBand)array[n]).putRef(entry);
                    }
                    
                    @Override
                    public int encodeBCI(final int n) {
                        return ((Code)holder).encodeBCI(n);
                    }
                    
                    @Override
                    public void noteBackCall(final int n) {
                        assert array2[n] >= 0;
                        final int[] val$bc = array2;
                        ++val$bc[n];
                    }
                });
                if (!b) {
                    continue;
                }
                this.setConstantValueIndex(null);
            }
        }
        if (n4 > 0) {
            BandStructure.getAttrBand(multiBand, 2).putInt(n4);
        }
        attrBand2.putInt(holder.flags | (int)n3);
        if (haveFlagsHi) {
            attrBand.putInt((int)(n3 >>> 32));
        }
        else {
            assert n3 >>> 32 == 0L;
        }
        assert ((long)holder.flags & n3) == 0x0L : holder + ".flags=" + Integer.toHexString(holder.flags) + "^" + Long.toHexString(n3);
    }
    
    private void beginCode(final Code curCode) {
        assert this.curCode == null;
        this.curCode = curCode;
        this.curClass = curCode.m.thisClass();
        this.curCPMap = curCode.getCPMap();
    }
    
    private void endCode() {
        this.curCode = null;
        this.curClass = null;
        this.curCPMap = null;
    }
    
    private int initOpVariant(final Instruction instruction, final ConstantPool.Entry entry) {
        if (instruction.getBC() != 183) {
            return -1;
        }
        final ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)instruction.getCPRef(this.curCPMap);
        if (!"<init>".equals(memberEntry.descRef.nameRef.stringValue())) {
            return -1;
        }
        final ConstantPool.ClassEntry classRef = memberEntry.classRef;
        if (classRef == this.curClass.thisClass) {
            return 230;
        }
        if (classRef == this.curClass.superClass) {
            return 231;
        }
        if (classRef == entry) {
            return 232;
        }
        return -1;
    }
    
    private int selfOpVariant(final Instruction instruction) {
        final int bc = instruction.getBC();
        if (bc < 178 || bc > 184) {
            return -1;
        }
        final ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)instruction.getCPRef(this.curCPMap);
        if ((bc == 183 || bc == 184) && memberEntry.tagEquals(11)) {
            return -1;
        }
        final ConstantPool.ClassEntry classRef = memberEntry.classRef;
        final int n = 202 + (bc - 178);
        if (classRef == this.curClass.thisClass) {
            return n;
        }
        if (classRef == this.curClass.superClass) {
            return n + 14;
        }
        return -1;
    }
    
    void writeByteCodes(final Code code) throws IOException {
        this.beginCode(code);
        final ConstantPool.IndexGroup cp = this.pkg.cp;
        int n = 0;
        ConstantPool.Entry entry = null;
        for (Instruction instruction = code.instructionAt(0); instruction != null; instruction = instruction.next()) {
            if (this.verbose > 3) {
                Utils.log.fine(instruction.toString());
            }
            if (instruction.isNonstandard()) {
                final String string = code.getMethod() + " contains an unrecognized bytecode " + instruction + "; please use the pass-file option on this class.";
                Utils.log.warning(string);
                throw new IOException(string);
            }
            if (instruction.isWide()) {
                if (this.verbose > 1) {
                    Utils.log.fine("_wide opcode in " + code);
                    Utils.log.fine(instruction.toString());
                }
                this.bc_codes.putByte(196);
                final int[] codeHist = this.codeHist;
                final int n2 = 196;
                ++codeHist[n2];
            }
            final int bc = instruction.getBC();
            if (bc == 42 && this.selfOpVariant(code.instructionAt(instruction.getNextPC())) >= 0) {
                n = 1;
            }
            else {
                final int initOpVariant = this.initOpVariant(instruction, entry);
                if (initOpVariant >= 0) {
                    if (n != 0) {
                        this.bc_codes.putByte(42);
                        final int[] codeHist2 = this.codeHist;
                        final int n3 = 42;
                        ++codeHist2[n3];
                        n = 0;
                    }
                    this.bc_codes.putByte(initOpVariant);
                    final int[] codeHist3 = this.codeHist;
                    final int n4 = initOpVariant;
                    ++codeHist3[n4];
                    this.bc_initref.putInt(cp.getOverloadingIndex((ConstantPool.MemberEntry)instruction.getCPRef(this.curCPMap)));
                }
                else {
                    int selfOpVariant = this.selfOpVariant(instruction);
                    if (selfOpVariant >= 0) {
                        Instruction.isFieldOp(bc);
                        final boolean b = selfOpVariant >= 216;
                        final int n5 = n;
                        n = 0;
                        if (n5 != 0) {
                            selfOpVariant += 7;
                        }
                        this.bc_codes.putByte(selfOpVariant);
                        final int[] codeHist4 = this.codeHist;
                        final int n6 = selfOpVariant;
                        ++codeHist4[n6];
                        final ConstantPool.MemberEntry memberEntry = (ConstantPool.MemberEntry)instruction.getCPRef(this.curCPMap);
                        this.selfOpRefBand(selfOpVariant).putRef(memberEntry, cp.getMemberIndex(memberEntry.tag, memberEntry.classRef));
                    }
                    else {
                        assert n == 0;
                        final int[] codeHist5 = this.codeHist;
                        final int n7 = bc;
                        ++codeHist5[n7];
                        switch (bc) {
                            case 170:
                            case 171: {
                                this.bc_codes.putByte(bc);
                                final Instruction.Switch switch1 = (Instruction.Switch)instruction;
                                switch1.getAlignedPC();
                                switch1.getNextPC();
                                final int caseCount = switch1.getCaseCount();
                                this.bc_case_count.putInt(caseCount);
                                this.putLabel(this.bc_label, code, instruction.getPC(), switch1.getDefaultLabel());
                                for (int i = 0; i < caseCount; ++i) {
                                    this.putLabel(this.bc_label, code, instruction.getPC(), switch1.getCaseLabel(i));
                                }
                                if (bc == 170) {
                                    this.bc_case_value.putInt(switch1.getCaseValue(0));
                                    break;
                                }
                                for (int j = 0; j < caseCount; ++j) {
                                    this.bc_case_value.putInt(switch1.getCaseValue(j));
                                }
                                break;
                            }
                            default: {
                                final int branchLabel = instruction.getBranchLabel();
                                if (branchLabel >= 0) {
                                    this.bc_codes.putByte(bc);
                                    this.putLabel(this.bc_label, code, instruction.getPC(), branchLabel);
                                    break;
                                }
                                ConstantPool.Entry cpRef = instruction.getCPRef(this.curCPMap);
                                if (cpRef != null) {
                                    if (bc == 187) {
                                        entry = cpRef;
                                    }
                                    if (bc == 18) {
                                        final int[] ldcHist = this.ldcHist;
                                        final byte tag = cpRef.tag;
                                        ++ldcHist[tag];
                                    }
                                    int n8 = bc;
                                    CPRefBand cpRefBand = null;
                                    Label_1392: {
                                        switch (instruction.getCPTag()) {
                                            case 51: {
                                                switch (cpRef.tag) {
                                                    case 3: {
                                                        cpRefBand = this.bc_intref;
                                                        switch (bc) {
                                                            case 18: {
                                                                n8 = 234;
                                                                break Label_1392;
                                                            }
                                                            case 19: {
                                                                n8 = 237;
                                                                break Label_1392;
                                                            }
                                                            default: {
                                                                assert false;
                                                                break Label_1392;
                                                            }
                                                        }
                                                        break;
                                                    }
                                                    case 4: {
                                                        cpRefBand = this.bc_floatref;
                                                        switch (bc) {
                                                            case 18: {
                                                                n8 = 235;
                                                                break Label_1392;
                                                            }
                                                            case 19: {
                                                                n8 = 238;
                                                                break Label_1392;
                                                            }
                                                            default: {
                                                                assert false;
                                                                break Label_1392;
                                                            }
                                                        }
                                                        break;
                                                    }
                                                    case 5: {
                                                        cpRefBand = this.bc_longref;
                                                        assert bc == 20;
                                                        n8 = 20;
                                                        break Label_1392;
                                                    }
                                                    case 6: {
                                                        cpRefBand = this.bc_doubleref;
                                                        assert bc == 20;
                                                        n8 = 239;
                                                        break Label_1392;
                                                    }
                                                    case 8: {
                                                        cpRefBand = this.bc_stringref;
                                                        switch (bc) {
                                                            case 18: {
                                                                n8 = 18;
                                                                break Label_1392;
                                                            }
                                                            case 19: {
                                                                n8 = 19;
                                                                break Label_1392;
                                                            }
                                                            default: {
                                                                assert false;
                                                                break Label_1392;
                                                            }
                                                        }
                                                        break;
                                                    }
                                                    case 7: {
                                                        cpRefBand = this.bc_classref;
                                                        switch (bc) {
                                                            case 18: {
                                                                n8 = 233;
                                                                break Label_1392;
                                                            }
                                                            case 19: {
                                                                n8 = 236;
                                                                break Label_1392;
                                                            }
                                                            default: {
                                                                assert false;
                                                                break Label_1392;
                                                            }
                                                        }
                                                        break;
                                                    }
                                                    default: {
                                                        if (this.getHighestClassVersion().lessThan(Constants.JAVA7_MAX_CLASS_VERSION)) {
                                                            throw new IOException("bad class file major version for Java 7 ldc");
                                                        }
                                                        cpRefBand = this.bc_loadablevalueref;
                                                        switch (bc) {
                                                            case 18: {
                                                                n8 = 240;
                                                                break Label_1392;
                                                            }
                                                            case 19: {
                                                                n8 = 241;
                                                                break Label_1392;
                                                            }
                                                            default: {
                                                                assert false;
                                                                break Label_1392;
                                                            }
                                                        }
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                            case 7: {
                                                if (cpRef == this.curClass.thisClass) {
                                                    cpRef = null;
                                                }
                                                cpRefBand = this.bc_classref;
                                                break;
                                            }
                                            case 9: {
                                                cpRefBand = this.bc_fieldref;
                                                break;
                                            }
                                            case 10: {
                                                if (cpRef.tagEquals(11)) {
                                                    if (bc == 183) {
                                                        n8 = 242;
                                                    }
                                                    if (bc == 184) {
                                                        n8 = 243;
                                                    }
                                                    cpRefBand = this.bc_imethodref;
                                                    break;
                                                }
                                                cpRefBand = this.bc_methodref;
                                                break;
                                            }
                                            case 11: {
                                                cpRefBand = this.bc_imethodref;
                                                break;
                                            }
                                            case 18: {
                                                cpRefBand = this.bc_indyref;
                                                break;
                                            }
                                            default: {
                                                cpRefBand = null;
                                                assert false;
                                                break;
                                            }
                                        }
                                    }
                                    if (cpRef != null && cpRefBand.index != null && !cpRefBand.index.contains(cpRef)) {
                                        final String string2 = code.getMethod() + " contains a bytecode " + instruction + " with an unsupported constant reference; please use the pass-file option on this class.";
                                        Utils.log.warning(string2);
                                        throw new IOException(string2);
                                    }
                                    this.bc_codes.putByte(n8);
                                    cpRefBand.putRef(cpRef);
                                    if (bc == 197) {
                                        assert instruction.getConstant() == code.getByte(instruction.getPC() + 3);
                                        this.bc_byte.putByte(0xFF & instruction.getConstant());
                                        break;
                                    }
                                    else if (bc == 185) {
                                        assert instruction.getLength() == 5;
                                        assert instruction.getConstant() == 1 + ((ConstantPool.MemberEntry)cpRef).descRef.typeRef.computeSize(true) << 8;
                                        break;
                                    }
                                    else if (bc == 186) {
                                        if (this.getHighestClassVersion().lessThan(Constants.JAVA7_MAX_CLASS_VERSION)) {
                                            throw new IOException("bad class major version for Java 7 invokedynamic");
                                        }
                                        assert instruction.getLength() == 5;
                                        assert instruction.getConstant() == 0;
                                        break;
                                    }
                                    else {
                                        assert instruction.getLength() == ((bc == 18) ? 2 : 3);
                                        break;
                                    }
                                }
                                else {
                                    final int localSlot = instruction.getLocalSlot();
                                    if (localSlot >= 0) {
                                        this.bc_codes.putByte(bc);
                                        this.bc_local.putInt(localSlot);
                                        final int constant = instruction.getConstant();
                                        if (bc == 132) {
                                            if (!instruction.isWide()) {
                                                this.bc_byte.putByte(0xFF & constant);
                                                break;
                                            }
                                            this.bc_short.putInt(0xFFFF & constant);
                                            break;
                                        }
                                        else {
                                            assert constant == 0;
                                            break;
                                        }
                                    }
                                    else {
                                        this.bc_codes.putByte(bc);
                                        if (instruction.getPC() + 1 >= instruction.getNextPC()) {
                                            break;
                                        }
                                        switch (bc) {
                                            case 17: {
                                                this.bc_short.putInt(0xFFFF & instruction.getConstant());
                                                continue;
                                            }
                                            case 16: {
                                                this.bc_byte.putByte(0xFF & instruction.getConstant());
                                                continue;
                                            }
                                            case 188: {
                                                this.bc_byte.putByte(0xFF & instruction.getConstant());
                                                continue;
                                            }
                                            default: {
                                                assert false;
                                                continue;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        this.bc_codes.putByte(255);
        final ByteBand bc_codes = this.bc_codes;
        ++bc_codes.elementCountForDebug;
        final int[] codeHist6 = this.codeHist;
        final int n9 = 255;
        ++codeHist6[n9];
        this.endCode();
    }
    
    void printCodeHist() {
        assert this.verbose > 0;
        final String[] array = new String[this.codeHist.length];
        int n = 0;
        for (int i = 0; i < this.codeHist.length; ++i) {
            n += this.codeHist[i];
        }
        for (int j = 0; j < this.codeHist.length; ++j) {
            if (this.codeHist[j] == 0) {
                array[j] = "";
            }
            else {
                final String byteName = Instruction.byteName(j);
                final String string = "" + this.codeHist[j];
                final String string2 = "         ".substring(string.length()) + string;
                String s;
                for (s = "" + this.codeHist[j] * 10000 / n; s.length() < 4; s = "0" + s) {}
                array[j] = string2 + "  " + (s.substring(0, s.length() - 2) + "." + s.substring(s.length() - 2)) + "%  " + byteName;
            }
        }
        Arrays.sort(array);
        System.out.println("Bytecode histogram [" + n + "]");
        int length = array.length;
        while (--length >= 0) {
            if ("".equals(array[length])) {
                continue;
            }
            System.out.println(array[length]);
        }
        for (int k = 0; k < this.ldcHist.length; ++k) {
            final int n2 = this.ldcHist[k];
            if (n2 != 0) {
                System.out.println("ldc " + ConstantPool.tagName(k) + " " + n2);
            }
        }
    }
}
