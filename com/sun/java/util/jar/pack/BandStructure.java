package com.sun.java.util.jar.pack;

import java.io.FilterOutputStream;
import java.io.FilterInputStream;
import java.io.EOFException;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Array;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.util.LinkedList;
import java.io.File;
import java.util.List;
import java.util.Map;

abstract class BandStructure
{
    static final int MAX_EFFORT = 9;
    static final int MIN_EFFORT = 1;
    static final int DEFAULT_EFFORT = 5;
    PropMap p200;
    int verbose;
    int effort;
    boolean optDumpBands;
    boolean optDebugBands;
    boolean optVaryCodings;
    boolean optBigStrings;
    private Package.Version highestClassVersion;
    private final boolean isReader;
    static final Coding BYTE1;
    static final Coding CHAR3;
    static final Coding BCI5;
    static final Coding BRANCH5;
    static final Coding UNSIGNED5;
    static final Coding UDELTA5;
    static final Coding SIGNED5;
    static final Coding DELTA5;
    static final Coding MDELTA5;
    private static final Coding[] basicCodings;
    private static final Map<Coding, Integer> basicCodingIndexes;
    protected byte[] bandHeaderBytes;
    protected int bandHeaderBytePos;
    protected int bandHeaderBytePos0;
    static final int SHORT_BAND_HEURISTIC = 100;
    public static final int NO_PHASE = 0;
    public static final int COLLECT_PHASE = 1;
    public static final int FROZEN_PHASE = 3;
    public static final int WRITE_PHASE = 5;
    public static final int EXPECT_PHASE = 2;
    public static final int READ_PHASE = 4;
    public static final int DISBURSE_PHASE = 6;
    public static final int DONE_PHASE = 8;
    private final List<CPRefBand> allKQBands;
    private List<Object[]> needPredefIndex;
    private CodingChooser codingChooser;
    static final byte[] defaultMetaCoding;
    static final byte[] noMetaCoding;
    ByteCounter outputCounter;
    protected int archiveOptions;
    protected long archiveSize0;
    protected long archiveSize1;
    protected int archiveNextCount;
    static final int AH_LENGTH_0 = 3;
    static final int AH_LENGTH_MIN = 15;
    static final int AH_LENGTH_S = 2;
    static final int AH_ARCHIVE_SIZE_HI = 0;
    static final int AH_ARCHIVE_SIZE_LO = 1;
    static final int AH_FILE_HEADER_LEN = 5;
    static final int AH_SPECIAL_FORMAT_LEN = 2;
    static final int AH_CP_NUMBER_LEN = 4;
    static final int AH_CP_EXTRA_LEN = 4;
    static final int AB_FLAGS_HI = 0;
    static final int AB_FLAGS_LO = 1;
    static final int AB_ATTR_COUNT = 2;
    static final int AB_ATTR_INDEXES = 3;
    static final int AB_ATTR_CALLS = 4;
    private static final boolean NULL_IS_OK = true;
    MultiBand all_bands;
    ByteBand archive_magic;
    IntBand archive_header_0;
    IntBand archive_header_S;
    IntBand archive_header_1;
    ByteBand band_headers;
    MultiBand cp_bands;
    IntBand cp_Utf8_prefix;
    IntBand cp_Utf8_suffix;
    IntBand cp_Utf8_chars;
    IntBand cp_Utf8_big_suffix;
    MultiBand cp_Utf8_big_chars;
    IntBand cp_Int;
    IntBand cp_Float;
    IntBand cp_Long_hi;
    IntBand cp_Long_lo;
    IntBand cp_Double_hi;
    IntBand cp_Double_lo;
    CPRefBand cp_String;
    CPRefBand cp_Class;
    CPRefBand cp_Signature_form;
    CPRefBand cp_Signature_classes;
    CPRefBand cp_Descr_name;
    CPRefBand cp_Descr_type;
    CPRefBand cp_Field_class;
    CPRefBand cp_Field_desc;
    CPRefBand cp_Method_class;
    CPRefBand cp_Method_desc;
    CPRefBand cp_Imethod_class;
    CPRefBand cp_Imethod_desc;
    IntBand cp_MethodHandle_refkind;
    CPRefBand cp_MethodHandle_member;
    CPRefBand cp_MethodType;
    CPRefBand cp_BootstrapMethod_ref;
    IntBand cp_BootstrapMethod_arg_count;
    CPRefBand cp_BootstrapMethod_arg;
    CPRefBand cp_InvokeDynamic_spec;
    CPRefBand cp_InvokeDynamic_desc;
    MultiBand attr_definition_bands;
    ByteBand attr_definition_headers;
    CPRefBand attr_definition_name;
    CPRefBand attr_definition_layout;
    MultiBand ic_bands;
    CPRefBand ic_this_class;
    IntBand ic_flags;
    CPRefBand ic_outer_class;
    CPRefBand ic_name;
    MultiBand class_bands;
    CPRefBand class_this;
    CPRefBand class_super;
    IntBand class_interface_count;
    CPRefBand class_interface;
    IntBand class_field_count;
    IntBand class_method_count;
    CPRefBand field_descr;
    MultiBand field_attr_bands;
    IntBand field_flags_hi;
    IntBand field_flags_lo;
    IntBand field_attr_count;
    IntBand field_attr_indexes;
    IntBand field_attr_calls;
    CPRefBand field_ConstantValue_KQ;
    CPRefBand field_Signature_RS;
    MultiBand field_metadata_bands;
    MultiBand field_type_metadata_bands;
    CPRefBand method_descr;
    MultiBand method_attr_bands;
    IntBand method_flags_hi;
    IntBand method_flags_lo;
    IntBand method_attr_count;
    IntBand method_attr_indexes;
    IntBand method_attr_calls;
    IntBand method_Exceptions_N;
    CPRefBand method_Exceptions_RC;
    CPRefBand method_Signature_RS;
    MultiBand method_metadata_bands;
    IntBand method_MethodParameters_NB;
    CPRefBand method_MethodParameters_name_RUN;
    IntBand method_MethodParameters_flag_FH;
    MultiBand method_type_metadata_bands;
    MultiBand class_attr_bands;
    IntBand class_flags_hi;
    IntBand class_flags_lo;
    IntBand class_attr_count;
    IntBand class_attr_indexes;
    IntBand class_attr_calls;
    CPRefBand class_SourceFile_RUN;
    CPRefBand class_EnclosingMethod_RC;
    CPRefBand class_EnclosingMethod_RDN;
    CPRefBand class_Signature_RS;
    MultiBand class_metadata_bands;
    IntBand class_InnerClasses_N;
    CPRefBand class_InnerClasses_RC;
    IntBand class_InnerClasses_F;
    CPRefBand class_InnerClasses_outer_RCN;
    CPRefBand class_InnerClasses_name_RUN;
    IntBand class_ClassFile_version_minor_H;
    IntBand class_ClassFile_version_major_H;
    MultiBand class_type_metadata_bands;
    MultiBand code_bands;
    ByteBand code_headers;
    IntBand code_max_stack;
    IntBand code_max_na_locals;
    IntBand code_handler_count;
    IntBand code_handler_start_P;
    IntBand code_handler_end_PO;
    IntBand code_handler_catch_PO;
    CPRefBand code_handler_class_RCN;
    MultiBand code_attr_bands;
    IntBand code_flags_hi;
    IntBand code_flags_lo;
    IntBand code_attr_count;
    IntBand code_attr_indexes;
    IntBand code_attr_calls;
    MultiBand stackmap_bands;
    IntBand code_StackMapTable_N;
    IntBand code_StackMapTable_frame_T;
    IntBand code_StackMapTable_local_N;
    IntBand code_StackMapTable_stack_N;
    IntBand code_StackMapTable_offset;
    IntBand code_StackMapTable_T;
    CPRefBand code_StackMapTable_RC;
    IntBand code_StackMapTable_P;
    IntBand code_LineNumberTable_N;
    IntBand code_LineNumberTable_bci_P;
    IntBand code_LineNumberTable_line;
    IntBand code_LocalVariableTable_N;
    IntBand code_LocalVariableTable_bci_P;
    IntBand code_LocalVariableTable_span_O;
    CPRefBand code_LocalVariableTable_name_RU;
    CPRefBand code_LocalVariableTable_type_RS;
    IntBand code_LocalVariableTable_slot;
    IntBand code_LocalVariableTypeTable_N;
    IntBand code_LocalVariableTypeTable_bci_P;
    IntBand code_LocalVariableTypeTable_span_O;
    CPRefBand code_LocalVariableTypeTable_name_RU;
    CPRefBand code_LocalVariableTypeTable_type_RS;
    IntBand code_LocalVariableTypeTable_slot;
    MultiBand code_type_metadata_bands;
    MultiBand bc_bands;
    ByteBand bc_codes;
    IntBand bc_case_count;
    IntBand bc_case_value;
    ByteBand bc_byte;
    IntBand bc_short;
    IntBand bc_local;
    IntBand bc_label;
    CPRefBand bc_intref;
    CPRefBand bc_floatref;
    CPRefBand bc_longref;
    CPRefBand bc_doubleref;
    CPRefBand bc_stringref;
    CPRefBand bc_loadablevalueref;
    CPRefBand bc_classref;
    CPRefBand bc_fieldref;
    CPRefBand bc_methodref;
    CPRefBand bc_imethodref;
    CPRefBand bc_indyref;
    CPRefBand bc_thisfield;
    CPRefBand bc_superfield;
    CPRefBand bc_thismethod;
    CPRefBand bc_supermethod;
    IntBand bc_initref;
    CPRefBand bc_escref;
    IntBand bc_escrefsize;
    IntBand bc_escsize;
    ByteBand bc_escbyte;
    MultiBand file_bands;
    CPRefBand file_name;
    IntBand file_size_hi;
    IntBand file_size_lo;
    IntBand file_modtime;
    IntBand file_options;
    ByteBand file_bits;
    protected MultiBand[] metadataBands;
    protected MultiBand[] typeMetadataBands;
    public static final int ADH_CONTEXT_MASK = 3;
    public static final int ADH_BIT_SHIFT = 2;
    public static final int ADH_BIT_IS_LSB = 1;
    public static final int ATTR_INDEX_OVERFLOW = -1;
    public int[] attrIndexLimit;
    protected long[] attrFlagMask;
    protected long[] attrDefSeen;
    protected int[] attrOverflowMask;
    protected int attrClassFileVersionMask;
    protected Map<Attribute.Layout, Band[]> attrBandTable;
    protected final Attribute.Layout attrCodeEmpty;
    protected final Attribute.Layout attrInnerClassesEmpty;
    protected final Attribute.Layout attrClassFileVersion;
    protected final Attribute.Layout attrConstantValue;
    Map<Attribute.Layout, Integer> attrIndexTable;
    protected List<List<Attribute.Layout>> attrDefs;
    protected MultiBand[] attrBands;
    private static final int[][] shortCodeLimits;
    public final int shortCodeHeader_h_limit;
    static final int LONG_CODE_HEADER = 0;
    static int nextSeqForDebug;
    static File dumpDir;
    private Map<Band, Band> prevForAssertMap;
    static LinkedList<String> bandSequenceList;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    protected abstract ConstantPool.Index getCPIndex(final byte p0);
    
    public void initHighestClassVersion(final Package.Version highestClassVersion) throws IOException {
        if (this.highestClassVersion != null) {
            throw new IOException("Highest class major version is already initialized to " + this.highestClassVersion + "; new setting is " + highestClassVersion);
        }
        this.highestClassVersion = highestClassVersion;
        this.adjustToClassVersion();
    }
    
    public Package.Version getHighestClassVersion() {
        return this.highestClassVersion;
    }
    
    protected BandStructure() {
        this.p200 = Utils.currentPropMap();
        this.verbose = this.p200.getInteger("com.sun.java.util.jar.pack.verbose");
        this.effort = this.p200.getInteger("pack.effort");
        if (this.effort == 0) {
            this.effort = 5;
        }
        this.optDumpBands = this.p200.getBoolean("com.sun.java.util.jar.pack.dump.bands");
        this.optDebugBands = this.p200.getBoolean("com.sun.java.util.jar.pack.debug.bands");
        this.optVaryCodings = !this.p200.getBoolean("com.sun.java.util.jar.pack.no.vary.codings");
        this.optBigStrings = !this.p200.getBoolean("com.sun.java.util.jar.pack.no.big.strings");
        this.highestClassVersion = null;
        this.isReader = (this instanceof PackageReader);
        this.allKQBands = new ArrayList<CPRefBand>();
        this.needPredefIndex = new ArrayList<Object[]>();
        this.all_bands = (MultiBand)new MultiBand("(package)", BandStructure.UNSIGNED5).init();
        this.archive_magic = this.all_bands.newByteBand("archive_magic");
        this.archive_header_0 = this.all_bands.newIntBand("archive_header_0", BandStructure.UNSIGNED5);
        this.archive_header_S = this.all_bands.newIntBand("archive_header_S", BandStructure.UNSIGNED5);
        this.archive_header_1 = this.all_bands.newIntBand("archive_header_1", BandStructure.UNSIGNED5);
        this.band_headers = this.all_bands.newByteBand("band_headers");
        this.cp_bands = this.all_bands.newMultiBand("(constant_pool)", BandStructure.DELTA5);
        this.cp_Utf8_prefix = this.cp_bands.newIntBand("cp_Utf8_prefix");
        this.cp_Utf8_suffix = this.cp_bands.newIntBand("cp_Utf8_suffix", BandStructure.UNSIGNED5);
        this.cp_Utf8_chars = this.cp_bands.newIntBand("cp_Utf8_chars", BandStructure.CHAR3);
        this.cp_Utf8_big_suffix = this.cp_bands.newIntBand("cp_Utf8_big_suffix");
        this.cp_Utf8_big_chars = this.cp_bands.newMultiBand("(cp_Utf8_big_chars)", BandStructure.DELTA5);
        this.cp_Int = this.cp_bands.newIntBand("cp_Int", BandStructure.UDELTA5);
        this.cp_Float = this.cp_bands.newIntBand("cp_Float", BandStructure.UDELTA5);
        this.cp_Long_hi = this.cp_bands.newIntBand("cp_Long_hi", BandStructure.UDELTA5);
        this.cp_Long_lo = this.cp_bands.newIntBand("cp_Long_lo");
        this.cp_Double_hi = this.cp_bands.newIntBand("cp_Double_hi", BandStructure.UDELTA5);
        this.cp_Double_lo = this.cp_bands.newIntBand("cp_Double_lo");
        this.cp_String = this.cp_bands.newCPRefBand("cp_String", BandStructure.UDELTA5, (byte)1);
        this.cp_Class = this.cp_bands.newCPRefBand("cp_Class", BandStructure.UDELTA5, (byte)1);
        this.cp_Signature_form = this.cp_bands.newCPRefBand("cp_Signature_form", (byte)1);
        this.cp_Signature_classes = this.cp_bands.newCPRefBand("cp_Signature_classes", BandStructure.UDELTA5, (byte)7);
        this.cp_Descr_name = this.cp_bands.newCPRefBand("cp_Descr_name", (byte)1);
        this.cp_Descr_type = this.cp_bands.newCPRefBand("cp_Descr_type", BandStructure.UDELTA5, (byte)13);
        this.cp_Field_class = this.cp_bands.newCPRefBand("cp_Field_class", (byte)7);
        this.cp_Field_desc = this.cp_bands.newCPRefBand("cp_Field_desc", BandStructure.UDELTA5, (byte)12);
        this.cp_Method_class = this.cp_bands.newCPRefBand("cp_Method_class", (byte)7);
        this.cp_Method_desc = this.cp_bands.newCPRefBand("cp_Method_desc", BandStructure.UDELTA5, (byte)12);
        this.cp_Imethod_class = this.cp_bands.newCPRefBand("cp_Imethod_class", (byte)7);
        this.cp_Imethod_desc = this.cp_bands.newCPRefBand("cp_Imethod_desc", BandStructure.UDELTA5, (byte)12);
        this.cp_MethodHandle_refkind = this.cp_bands.newIntBand("cp_MethodHandle_refkind", BandStructure.DELTA5);
        this.cp_MethodHandle_member = this.cp_bands.newCPRefBand("cp_MethodHandle_member", BandStructure.UDELTA5, (byte)52);
        this.cp_MethodType = this.cp_bands.newCPRefBand("cp_MethodType", BandStructure.UDELTA5, (byte)13);
        this.cp_BootstrapMethod_ref = this.cp_bands.newCPRefBand("cp_BootstrapMethod_ref", BandStructure.DELTA5, (byte)15);
        this.cp_BootstrapMethod_arg_count = this.cp_bands.newIntBand("cp_BootstrapMethod_arg_count", BandStructure.UDELTA5);
        this.cp_BootstrapMethod_arg = this.cp_bands.newCPRefBand("cp_BootstrapMethod_arg", BandStructure.DELTA5, (byte)51);
        this.cp_InvokeDynamic_spec = this.cp_bands.newCPRefBand("cp_InvokeDynamic_spec", BandStructure.DELTA5, (byte)17);
        this.cp_InvokeDynamic_desc = this.cp_bands.newCPRefBand("cp_InvokeDynamic_desc", BandStructure.UDELTA5, (byte)12);
        this.attr_definition_bands = this.all_bands.newMultiBand("(attr_definition_bands)", BandStructure.UNSIGNED5);
        this.attr_definition_headers = this.attr_definition_bands.newByteBand("attr_definition_headers");
        this.attr_definition_name = this.attr_definition_bands.newCPRefBand("attr_definition_name", (byte)1);
        this.attr_definition_layout = this.attr_definition_bands.newCPRefBand("attr_definition_layout", (byte)1);
        this.ic_bands = this.all_bands.newMultiBand("(ic_bands)", BandStructure.DELTA5);
        this.ic_this_class = this.ic_bands.newCPRefBand("ic_this_class", BandStructure.UDELTA5, (byte)7);
        this.ic_flags = this.ic_bands.newIntBand("ic_flags", BandStructure.UNSIGNED5);
        this.ic_outer_class = this.ic_bands.newCPRefBand("ic_outer_class", BandStructure.DELTA5, (byte)7, true);
        this.ic_name = this.ic_bands.newCPRefBand("ic_name", BandStructure.DELTA5, (byte)1, true);
        this.class_bands = this.all_bands.newMultiBand("(class_bands)", BandStructure.DELTA5);
        this.class_this = this.class_bands.newCPRefBand("class_this", (byte)7);
        this.class_super = this.class_bands.newCPRefBand("class_super", (byte)7);
        this.class_interface_count = this.class_bands.newIntBand("class_interface_count");
        this.class_interface = this.class_bands.newCPRefBand("class_interface", (byte)7);
        this.class_field_count = this.class_bands.newIntBand("class_field_count");
        this.class_method_count = this.class_bands.newIntBand("class_method_count");
        this.field_descr = this.class_bands.newCPRefBand("field_descr", (byte)12);
        this.field_attr_bands = this.class_bands.newMultiBand("(field_attr_bands)", BandStructure.UNSIGNED5);
        this.field_flags_hi = this.field_attr_bands.newIntBand("field_flags_hi");
        this.field_flags_lo = this.field_attr_bands.newIntBand("field_flags_lo");
        this.field_attr_count = this.field_attr_bands.newIntBand("field_attr_count");
        this.field_attr_indexes = this.field_attr_bands.newIntBand("field_attr_indexes");
        this.field_attr_calls = this.field_attr_bands.newIntBand("field_attr_calls");
        this.field_ConstantValue_KQ = this.field_attr_bands.newCPRefBand("field_ConstantValue_KQ", (byte)53);
        this.field_Signature_RS = this.field_attr_bands.newCPRefBand("field_Signature_RS", (byte)13);
        this.field_metadata_bands = this.field_attr_bands.newMultiBand("(field_metadata_bands)", BandStructure.UNSIGNED5);
        this.field_type_metadata_bands = this.field_attr_bands.newMultiBand("(field_type_metadata_bands)", BandStructure.UNSIGNED5);
        this.method_descr = this.class_bands.newCPRefBand("method_descr", BandStructure.MDELTA5, (byte)12);
        this.method_attr_bands = this.class_bands.newMultiBand("(method_attr_bands)", BandStructure.UNSIGNED5);
        this.method_flags_hi = this.method_attr_bands.newIntBand("method_flags_hi");
        this.method_flags_lo = this.method_attr_bands.newIntBand("method_flags_lo");
        this.method_attr_count = this.method_attr_bands.newIntBand("method_attr_count");
        this.method_attr_indexes = this.method_attr_bands.newIntBand("method_attr_indexes");
        this.method_attr_calls = this.method_attr_bands.newIntBand("method_attr_calls");
        this.method_Exceptions_N = this.method_attr_bands.newIntBand("method_Exceptions_N");
        this.method_Exceptions_RC = this.method_attr_bands.newCPRefBand("method_Exceptions_RC", (byte)7);
        this.method_Signature_RS = this.method_attr_bands.newCPRefBand("method_Signature_RS", (byte)13);
        this.method_metadata_bands = this.method_attr_bands.newMultiBand("(method_metadata_bands)", BandStructure.UNSIGNED5);
        this.method_MethodParameters_NB = this.method_attr_bands.newIntBand("method_MethodParameters_NB", BandStructure.BYTE1);
        this.method_MethodParameters_name_RUN = this.method_attr_bands.newCPRefBand("method_MethodParameters_name_RUN", BandStructure.UNSIGNED5, (byte)1, true);
        this.method_MethodParameters_flag_FH = this.method_attr_bands.newIntBand("method_MethodParameters_flag_FH");
        this.method_type_metadata_bands = this.method_attr_bands.newMultiBand("(method_type_metadata_bands)", BandStructure.UNSIGNED5);
        this.class_attr_bands = this.class_bands.newMultiBand("(class_attr_bands)", BandStructure.UNSIGNED5);
        this.class_flags_hi = this.class_attr_bands.newIntBand("class_flags_hi");
        this.class_flags_lo = this.class_attr_bands.newIntBand("class_flags_lo");
        this.class_attr_count = this.class_attr_bands.newIntBand("class_attr_count");
        this.class_attr_indexes = this.class_attr_bands.newIntBand("class_attr_indexes");
        this.class_attr_calls = this.class_attr_bands.newIntBand("class_attr_calls");
        this.class_SourceFile_RUN = this.class_attr_bands.newCPRefBand("class_SourceFile_RUN", BandStructure.UNSIGNED5, (byte)1, true);
        this.class_EnclosingMethod_RC = this.class_attr_bands.newCPRefBand("class_EnclosingMethod_RC", (byte)7);
        this.class_EnclosingMethod_RDN = this.class_attr_bands.newCPRefBand("class_EnclosingMethod_RDN", BandStructure.UNSIGNED5, (byte)12, true);
        this.class_Signature_RS = this.class_attr_bands.newCPRefBand("class_Signature_RS", (byte)13);
        this.class_metadata_bands = this.class_attr_bands.newMultiBand("(class_metadata_bands)", BandStructure.UNSIGNED5);
        this.class_InnerClasses_N = this.class_attr_bands.newIntBand("class_InnerClasses_N");
        this.class_InnerClasses_RC = this.class_attr_bands.newCPRefBand("class_InnerClasses_RC", (byte)7);
        this.class_InnerClasses_F = this.class_attr_bands.newIntBand("class_InnerClasses_F");
        this.class_InnerClasses_outer_RCN = this.class_attr_bands.newCPRefBand("class_InnerClasses_outer_RCN", BandStructure.UNSIGNED5, (byte)7, true);
        this.class_InnerClasses_name_RUN = this.class_attr_bands.newCPRefBand("class_InnerClasses_name_RUN", BandStructure.UNSIGNED5, (byte)1, true);
        this.class_ClassFile_version_minor_H = this.class_attr_bands.newIntBand("class_ClassFile_version_minor_H");
        this.class_ClassFile_version_major_H = this.class_attr_bands.newIntBand("class_ClassFile_version_major_H");
        this.class_type_metadata_bands = this.class_attr_bands.newMultiBand("(class_type_metadata_bands)", BandStructure.UNSIGNED5);
        this.code_bands = this.class_bands.newMultiBand("(code_bands)", BandStructure.UNSIGNED5);
        this.code_headers = this.code_bands.newByteBand("code_headers");
        this.code_max_stack = this.code_bands.newIntBand("code_max_stack", BandStructure.UNSIGNED5);
        this.code_max_na_locals = this.code_bands.newIntBand("code_max_na_locals", BandStructure.UNSIGNED5);
        this.code_handler_count = this.code_bands.newIntBand("code_handler_count", BandStructure.UNSIGNED5);
        this.code_handler_start_P = this.code_bands.newIntBand("code_handler_start_P", BandStructure.BCI5);
        this.code_handler_end_PO = this.code_bands.newIntBand("code_handler_end_PO", BandStructure.BRANCH5);
        this.code_handler_catch_PO = this.code_bands.newIntBand("code_handler_catch_PO", BandStructure.BRANCH5);
        this.code_handler_class_RCN = this.code_bands.newCPRefBand("code_handler_class_RCN", BandStructure.UNSIGNED5, (byte)7, true);
        this.code_attr_bands = this.class_bands.newMultiBand("(code_attr_bands)", BandStructure.UNSIGNED5);
        this.code_flags_hi = this.code_attr_bands.newIntBand("code_flags_hi");
        this.code_flags_lo = this.code_attr_bands.newIntBand("code_flags_lo");
        this.code_attr_count = this.code_attr_bands.newIntBand("code_attr_count");
        this.code_attr_indexes = this.code_attr_bands.newIntBand("code_attr_indexes");
        this.code_attr_calls = this.code_attr_bands.newIntBand("code_attr_calls");
        this.stackmap_bands = this.code_attr_bands.newMultiBand("(StackMapTable_bands)", BandStructure.UNSIGNED5);
        this.code_StackMapTable_N = this.stackmap_bands.newIntBand("code_StackMapTable_N");
        this.code_StackMapTable_frame_T = this.stackmap_bands.newIntBand("code_StackMapTable_frame_T", BandStructure.BYTE1);
        this.code_StackMapTable_local_N = this.stackmap_bands.newIntBand("code_StackMapTable_local_N");
        this.code_StackMapTable_stack_N = this.stackmap_bands.newIntBand("code_StackMapTable_stack_N");
        this.code_StackMapTable_offset = this.stackmap_bands.newIntBand("code_StackMapTable_offset", BandStructure.UNSIGNED5);
        this.code_StackMapTable_T = this.stackmap_bands.newIntBand("code_StackMapTable_T", BandStructure.BYTE1);
        this.code_StackMapTable_RC = this.stackmap_bands.newCPRefBand("code_StackMapTable_RC", (byte)7);
        this.code_StackMapTable_P = this.stackmap_bands.newIntBand("code_StackMapTable_P", BandStructure.BCI5);
        this.code_LineNumberTable_N = this.code_attr_bands.newIntBand("code_LineNumberTable_N");
        this.code_LineNumberTable_bci_P = this.code_attr_bands.newIntBand("code_LineNumberTable_bci_P", BandStructure.BCI5);
        this.code_LineNumberTable_line = this.code_attr_bands.newIntBand("code_LineNumberTable_line");
        this.code_LocalVariableTable_N = this.code_attr_bands.newIntBand("code_LocalVariableTable_N");
        this.code_LocalVariableTable_bci_P = this.code_attr_bands.newIntBand("code_LocalVariableTable_bci_P", BandStructure.BCI5);
        this.code_LocalVariableTable_span_O = this.code_attr_bands.newIntBand("code_LocalVariableTable_span_O", BandStructure.BRANCH5);
        this.code_LocalVariableTable_name_RU = this.code_attr_bands.newCPRefBand("code_LocalVariableTable_name_RU", (byte)1);
        this.code_LocalVariableTable_type_RS = this.code_attr_bands.newCPRefBand("code_LocalVariableTable_type_RS", (byte)13);
        this.code_LocalVariableTable_slot = this.code_attr_bands.newIntBand("code_LocalVariableTable_slot");
        this.code_LocalVariableTypeTable_N = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_N");
        this.code_LocalVariableTypeTable_bci_P = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_bci_P", BandStructure.BCI5);
        this.code_LocalVariableTypeTable_span_O = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_span_O", BandStructure.BRANCH5);
        this.code_LocalVariableTypeTable_name_RU = this.code_attr_bands.newCPRefBand("code_LocalVariableTypeTable_name_RU", (byte)1);
        this.code_LocalVariableTypeTable_type_RS = this.code_attr_bands.newCPRefBand("code_LocalVariableTypeTable_type_RS", (byte)13);
        this.code_LocalVariableTypeTable_slot = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_slot");
        this.code_type_metadata_bands = this.code_attr_bands.newMultiBand("(code_type_metadata_bands)", BandStructure.UNSIGNED5);
        this.bc_bands = this.all_bands.newMultiBand("(byte_codes)", BandStructure.UNSIGNED5);
        this.bc_codes = this.bc_bands.newByteBand("bc_codes");
        this.bc_case_count = this.bc_bands.newIntBand("bc_case_count");
        this.bc_case_value = this.bc_bands.newIntBand("bc_case_value", BandStructure.DELTA5);
        this.bc_byte = this.bc_bands.newByteBand("bc_byte");
        this.bc_short = this.bc_bands.newIntBand("bc_short", BandStructure.DELTA5);
        this.bc_local = this.bc_bands.newIntBand("bc_local");
        this.bc_label = this.bc_bands.newIntBand("bc_label", BandStructure.BRANCH5);
        this.bc_intref = this.bc_bands.newCPRefBand("bc_intref", BandStructure.DELTA5, (byte)3);
        this.bc_floatref = this.bc_bands.newCPRefBand("bc_floatref", BandStructure.DELTA5, (byte)4);
        this.bc_longref = this.bc_bands.newCPRefBand("bc_longref", BandStructure.DELTA5, (byte)5);
        this.bc_doubleref = this.bc_bands.newCPRefBand("bc_doubleref", BandStructure.DELTA5, (byte)6);
        this.bc_stringref = this.bc_bands.newCPRefBand("bc_stringref", BandStructure.DELTA5, (byte)8);
        this.bc_loadablevalueref = this.bc_bands.newCPRefBand("bc_loadablevalueref", BandStructure.DELTA5, (byte)51);
        this.bc_classref = this.bc_bands.newCPRefBand("bc_classref", BandStructure.UNSIGNED5, (byte)7, true);
        this.bc_fieldref = this.bc_bands.newCPRefBand("bc_fieldref", BandStructure.DELTA5, (byte)9);
        this.bc_methodref = this.bc_bands.newCPRefBand("bc_methodref", (byte)10);
        this.bc_imethodref = this.bc_bands.newCPRefBand("bc_imethodref", BandStructure.DELTA5, (byte)11);
        this.bc_indyref = this.bc_bands.newCPRefBand("bc_indyref", BandStructure.DELTA5, (byte)18);
        this.bc_thisfield = this.bc_bands.newCPRefBand("bc_thisfield", (byte)0);
        this.bc_superfield = this.bc_bands.newCPRefBand("bc_superfield", (byte)0);
        this.bc_thismethod = this.bc_bands.newCPRefBand("bc_thismethod", (byte)0);
        this.bc_supermethod = this.bc_bands.newCPRefBand("bc_supermethod", (byte)0);
        this.bc_initref = this.bc_bands.newIntBand("bc_initref");
        this.bc_escref = this.bc_bands.newCPRefBand("bc_escref", (byte)50);
        this.bc_escrefsize = this.bc_bands.newIntBand("bc_escrefsize");
        this.bc_escsize = this.bc_bands.newIntBand("bc_escsize");
        this.bc_escbyte = this.bc_bands.newByteBand("bc_escbyte");
        this.file_bands = this.all_bands.newMultiBand("(file_bands)", BandStructure.UNSIGNED5);
        this.file_name = this.file_bands.newCPRefBand("file_name", (byte)1);
        this.file_size_hi = this.file_bands.newIntBand("file_size_hi");
        this.file_size_lo = this.file_bands.newIntBand("file_size_lo");
        this.file_modtime = this.file_bands.newIntBand("file_modtime", BandStructure.DELTA5);
        this.file_options = this.file_bands.newIntBand("file_options");
        this.file_bits = this.file_bands.newByteBand("file_bits");
        (this.metadataBands = new MultiBand[4])[0] = this.class_metadata_bands;
        this.metadataBands[1] = this.field_metadata_bands;
        this.metadataBands[2] = this.method_metadata_bands;
        (this.typeMetadataBands = new MultiBand[4])[0] = this.class_type_metadata_bands;
        this.typeMetadataBands[1] = this.field_type_metadata_bands;
        this.typeMetadataBands[2] = this.method_type_metadata_bands;
        this.typeMetadataBands[3] = this.code_type_metadata_bands;
        this.attrIndexLimit = new int[4];
        this.attrFlagMask = new long[4];
        this.attrDefSeen = new long[4];
        this.attrOverflowMask = new int[4];
        this.attrBandTable = new HashMap<Attribute.Layout, Band[]>();
        this.attrIndexTable = new HashMap<Attribute.Layout, Integer>();
        this.attrDefs = new FixedList<List<Attribute.Layout>>(4);
        for (int i = 0; i < 4; ++i) {
            assert this.attrIndexLimit[i] == 0;
            this.attrIndexLimit[i] = 32;
            this.attrDefs.set(i, new ArrayList<Attribute.Layout>(Collections.nCopies(this.attrIndexLimit[i], (Object)null)));
        }
        this.attrInnerClassesEmpty = this.predefineAttribute(23, 0, null, "InnerClasses", "");
        assert this.attrInnerClassesEmpty == Package.attrInnerClassesEmpty;
        this.predefineAttribute(17, 0, new Band[] { this.class_SourceFile_RUN }, "SourceFile", "RUNH");
        this.predefineAttribute(18, 0, new Band[] { this.class_EnclosingMethod_RC, this.class_EnclosingMethod_RDN }, "EnclosingMethod", "RCHRDNH");
        this.attrClassFileVersion = this.predefineAttribute(24, 0, new Band[] { this.class_ClassFile_version_minor_H, this.class_ClassFile_version_major_H }, ".ClassFile.version", "HH");
        this.predefineAttribute(19, 0, new Band[] { this.class_Signature_RS }, "Signature", "RSH");
        this.predefineAttribute(20, 0, null, "Deprecated", "");
        this.predefineAttribute(16, 0, null, ".Overflow", "");
        this.attrConstantValue = this.predefineAttribute(17, 1, new Band[] { this.field_ConstantValue_KQ }, "ConstantValue", "KQH");
        this.predefineAttribute(19, 1, new Band[] { this.field_Signature_RS }, "Signature", "RSH");
        this.predefineAttribute(20, 1, null, "Deprecated", "");
        this.predefineAttribute(16, 1, null, ".Overflow", "");
        this.attrCodeEmpty = this.predefineAttribute(17, 2, null, "Code", "");
        this.predefineAttribute(18, 2, new Band[] { this.method_Exceptions_N, this.method_Exceptions_RC }, "Exceptions", "NH[RCH]");
        this.predefineAttribute(26, 2, new Band[] { this.method_MethodParameters_NB, this.method_MethodParameters_name_RUN, this.method_MethodParameters_flag_FH }, "MethodParameters", "NB[RUNHFH]");
        assert this.attrCodeEmpty == Package.attrCodeEmpty;
        this.predefineAttribute(19, 2, new Band[] { this.method_Signature_RS }, "Signature", "RSH");
        this.predefineAttribute(20, 2, null, "Deprecated", "");
        this.predefineAttribute(16, 2, null, ".Overflow", "");
        for (int j = 0; j < 4; ++j) {
            final MultiBand multiBand = this.metadataBands[j];
            if (j != 3) {
                this.predefineAttribute(21, Constants.ATTR_CONTEXT_NAME[j] + "_RVA_", multiBand, Attribute.lookup(null, j, "RuntimeVisibleAnnotations"));
                this.predefineAttribute(22, Constants.ATTR_CONTEXT_NAME[j] + "_RIA_", multiBand, Attribute.lookup(null, j, "RuntimeInvisibleAnnotations"));
                if (j == 2) {
                    this.predefineAttribute(23, "method_RVPA_", multiBand, Attribute.lookup(null, j, "RuntimeVisibleParameterAnnotations"));
                    this.predefineAttribute(24, "method_RIPA_", multiBand, Attribute.lookup(null, j, "RuntimeInvisibleParameterAnnotations"));
                    this.predefineAttribute(25, "method_AD_", multiBand, Attribute.lookup(null, j, "AnnotationDefault"));
                }
            }
            final MultiBand multiBand2 = this.typeMetadataBands[j];
            this.predefineAttribute(27, Constants.ATTR_CONTEXT_NAME[j] + "_RVTA_", multiBand2, Attribute.lookup(null, j, "RuntimeVisibleTypeAnnotations"));
            this.predefineAttribute(28, Constants.ATTR_CONTEXT_NAME[j] + "_RITA_", multiBand2, Attribute.lookup(null, j, "RuntimeInvisibleTypeAnnotations"));
        }
        final Attribute.Layout layout = Attribute.lookup(null, 3, "StackMapTable").layout();
        this.predefineAttribute(0, 3, this.stackmap_bands.toArray(), layout.name(), layout.layout());
        this.predefineAttribute(1, 3, new Band[] { this.code_LineNumberTable_N, this.code_LineNumberTable_bci_P, this.code_LineNumberTable_line }, "LineNumberTable", "NH[PHH]");
        this.predefineAttribute(2, 3, new Band[] { this.code_LocalVariableTable_N, this.code_LocalVariableTable_bci_P, this.code_LocalVariableTable_span_O, this.code_LocalVariableTable_name_RU, this.code_LocalVariableTable_type_RS, this.code_LocalVariableTable_slot }, "LocalVariableTable", "NH[PHOHRUHRSHH]");
        this.predefineAttribute(3, 3, new Band[] { this.code_LocalVariableTypeTable_N, this.code_LocalVariableTypeTable_bci_P, this.code_LocalVariableTypeTable_span_O, this.code_LocalVariableTypeTable_name_RU, this.code_LocalVariableTypeTable_type_RS, this.code_LocalVariableTypeTable_slot }, "LocalVariableTypeTable", "NH[PHOHRUHRSHH]");
        this.predefineAttribute(16, 3, null, ".Overflow", "");
        for (int k = 0; k < 4; ++k) {
            this.attrDefSeen[k] = 0L;
        }
        for (int l = 0; l < 4; ++l) {
            this.attrOverflowMask[l] = 65536;
            this.attrIndexLimit[l] = 0;
        }
        this.attrClassFileVersionMask = 16777216;
        (this.attrBands = new MultiBand[4])[0] = this.class_attr_bands;
        this.attrBands[1] = this.field_attr_bands;
        this.attrBands[2] = this.method_attr_bands;
        this.attrBands[3] = this.code_attr_bands;
        this.shortCodeHeader_h_limit = BandStructure.shortCodeLimits.length;
    }
    
    public static Coding codingForIndex(final int n) {
        return (n < BandStructure.basicCodings.length) ? BandStructure.basicCodings[n] : null;
    }
    
    public static int indexOf(final Coding coding) {
        final Integer n = BandStructure.basicCodingIndexes.get(coding);
        if (n == null) {
            return 0;
        }
        return n;
    }
    
    public static Coding[] getBasicCodings() {
        return BandStructure.basicCodings.clone();
    }
    
    protected CodingMethod getBandHeader(final int n, final Coding coding) {
        final CodingMethod[] array = { null };
        this.bandHeaderBytes[--this.bandHeaderBytePos] = (byte)n;
        this.bandHeaderBytePos0 = this.bandHeaderBytePos;
        this.bandHeaderBytePos = parseMetaCoding(this.bandHeaderBytes, this.bandHeaderBytePos, coding, array);
        return array[0];
    }
    
    public static int parseMetaCoding(final byte[] array, final int n, final Coding coding, final CodingMethod[] array2) {
        if ((array[n] & 0xFF) == 0x0) {
            array2[0] = coding;
            return n + 1;
        }
        final int metaCoding = Coding.parseMetaCoding(array, n, coding, array2);
        if (metaCoding > n) {
            return metaCoding;
        }
        final int metaCoding2 = PopulationCoding.parseMetaCoding(array, n, coding, array2);
        if (metaCoding2 > n) {
            return metaCoding2;
        }
        final int metaCoding3 = AdaptiveCoding.parseMetaCoding(array, n, coding, array2);
        if (metaCoding3 > n) {
            return metaCoding3;
        }
        throw new RuntimeException("Bad meta-coding op " + (array[n] & 0xFF));
    }
    
    static boolean phaseIsRead(final int n) {
        return n % 2 == 0;
    }
    
    static int phaseCmp(final int n, final int n2) {
        assert n2 % 8 == 0;
        return n - n2;
    }
    
    static int getIntTotal(final int[] array) {
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            n += array[i];
        }
        return n;
    }
    
    int encodeRef(final ConstantPool.Entry entry, final ConstantPool.Index index) {
        if (index == null) {
            throw new RuntimeException("null index for " + entry.stringValue());
        }
        final int index2 = index.indexOf(entry);
        if (this.verbose > 2) {
            Utils.log.fine("putRef " + index2 + " => " + entry);
        }
        return index2;
    }
    
    ConstantPool.Entry decodeRef(final int n, final ConstantPool.Index index) {
        if (n < 0 || n >= index.size()) {
            Utils.log.warning("decoding bad ref " + n + " in " + index);
        }
        final ConstantPool.Entry entry = index.getEntry(n);
        if (this.verbose > 2) {
            Utils.log.fine("getRef " + n + " => " + entry);
        }
        return entry;
    }
    
    protected CodingChooser getCodingChooser() {
        if (this.codingChooser == null) {
            this.codingChooser = new CodingChooser(this.effort, BandStructure.basicCodings);
            if (this.codingChooser.stress != null && this instanceof PackageWriter) {
                final ArrayList<Package.Class> classes = ((PackageWriter)this).pkg.classes;
                if (!classes.isEmpty()) {
                    this.codingChooser.addStressSeed(((Package.Class)classes.get(0)).getName().hashCode());
                }
            }
        }
        return this.codingChooser;
    }
    
    public CodingMethod chooseCoding(final int[] array, final int n, final int n2, final Coding coding, final String s, final int[] array2) {
        assert this.optVaryCodings;
        if (this.effort <= 1) {
            return coding;
        }
        final CodingChooser codingChooser = this.getCodingChooser();
        if (this.verbose > 1 || codingChooser.verbose > 1) {
            Utils.log.fine("--- chooseCoding " + s);
        }
        return codingChooser.choose(array, n, n2, coding, array2);
    }
    
    protected static int decodeEscapeValue(final int n, final Coding coding) {
        if (coding.B() == 1 || coding.L() == 0) {
            return -1;
        }
        if (coding.S() != 0) {
            if (-256 <= n && n <= -1 && coding.min() <= -256) {
                final int n2 = -1 - n;
                assert n2 >= 0 && n2 < 256;
                return n2;
            }
        }
        else {
            final int l = coding.L();
            if (l <= n && n <= l + 255 && coding.max() >= l + 255) {
                final int n3 = n - l;
                assert n3 >= 0 && n3 < 256;
                return n3;
            }
        }
        return -1;
    }
    
    protected static int encodeEscapeValue(final int n, final Coding coding) {
        assert n >= 0 && n < 256;
        assert coding.B() > 1 && coding.L() > 0;
        int n2;
        if (coding.S() != 0) {
            assert coding.min() <= -256;
            n2 = -1 - n;
        }
        else {
            final int l = coding.L();
            assert coding.max() >= l + 255;
            n2 = n + l;
        }
        assert decodeEscapeValue(n2, coding) == n : coding + " XB=" + n + " X=" + n2;
        return n2;
    }
    
    void writeAllBandsTo(final OutputStream outputStream) throws IOException {
        this.outputCounter = new ByteCounter(outputStream);
        this.all_bands.writeTo(this.outputCounter);
        if (this.verbose > 0) {
            final long count = this.outputCounter.getCount();
            Utils.log.info("Wrote total of " + count + " bytes.");
            assert count == this.archiveSize0 + this.archiveSize1;
        }
        this.outputCounter = null;
    }
    
    static IntBand getAttrBand(final MultiBand multiBand, final int n) {
        final IntBand intBand = (IntBand)multiBand.get(n);
        switch (n) {
            case 0: {
                assert intBand.name().endsWith("_flags_hi");
                break;
            }
            case 1: {
                assert intBand.name().endsWith("_flags_lo");
                break;
            }
            case 2: {
                assert intBand.name().endsWith("_attr_count");
                break;
            }
            case 3: {
                assert intBand.name().endsWith("_attr_indexes");
                break;
            }
            case 4: {
                assert intBand.name().endsWith("_attr_calls");
                break;
            }
            default: {
                assert false;
                break;
            }
        }
        return intBand;
    }
    
    protected void setBandIndexes() {
        for (final Object[] array : this.needPredefIndex) {
            ((CPRefBand)array[0]).setIndex(this.getCPIndex((byte)array[1]));
        }
        this.needPredefIndex = null;
        if (this.verbose > 3) {
            printCDecl(this.all_bands);
        }
    }
    
    protected void setBandIndex(final CPRefBand cpRefBand, final byte b) {
        final Object[] array = { cpRefBand, b };
        if (b == 53) {
            this.allKQBands.add(cpRefBand);
        }
        else if (this.needPredefIndex != null) {
            this.needPredefIndex.add(array);
        }
        else {
            cpRefBand.setIndex(this.getCPIndex(b));
        }
    }
    
    protected void setConstantValueIndex(final Package.Class.Field field) {
        ConstantPool.Index cpIndex = null;
        if (field != null) {
            final byte literalTag = field.getLiteralTag();
            cpIndex = this.getCPIndex(literalTag);
            if (this.verbose > 2) {
                Utils.log.fine("setConstantValueIndex " + field + " " + ConstantPool.tagName(literalTag) + " => " + cpIndex);
            }
            assert cpIndex != null;
        }
        final Iterator<CPRefBand> iterator = this.allKQBands.iterator();
        while (iterator.hasNext()) {
            iterator.next().setIndex(cpIndex);
        }
    }
    
    private void adjustToClassVersion() throws IOException {
        if (this.getHighestClassVersion().lessThan(Constants.JAVA6_MAX_CLASS_VERSION)) {
            if (this.verbose > 0) {
                Utils.log.fine("Legacy package version");
            }
            this.undefineAttribute(0, 3);
        }
    }
    
    protected void initAttrIndexLimit() {
        for (int i = 0; i < 4; ++i) {
            assert this.attrIndexLimit[i] == 0;
            this.attrIndexLimit[i] = (this.haveFlagsHi(i) ? 63 : 32);
            final List list = this.attrDefs.get(i);
            assert list.size() == 32;
            list.addAll(Collections.nCopies(this.attrIndexLimit[i] - list.size(), (Object)null));
        }
    }
    
    protected boolean haveFlagsHi(final int n) {
        final int n2 = 1 << 9 + n;
        switch (n) {
            case 0: {
                assert n2 == 512;
                break;
            }
            case 1: {
                assert n2 == 1024;
                break;
            }
            case 2: {
                assert n2 == 2048;
                break;
            }
            case 3: {
                assert n2 == 4096;
                break;
            }
            default: {
                assert false;
                break;
            }
        }
        return testBit(this.archiveOptions, n2);
    }
    
    protected List<Attribute.Layout> getPredefinedAttrs(final int n) {
        assert this.attrIndexLimit[n] != 0;
        final ArrayList list = new ArrayList(this.attrIndexLimit[n]);
        for (int i = 0; i < this.attrIndexLimit[n]; ++i) {
            if (!testBit(this.attrDefSeen[n], 1L << i)) {
                final Attribute.Layout layout = this.attrDefs.get(n).get(i);
                if (layout != null) {
                    assert this.isPredefinedAttr(n, i);
                    list.add(layout);
                }
            }
        }
        return list;
    }
    
    protected boolean isPredefinedAttr(final int n, final int n2) {
        assert this.attrIndexLimit[n] != 0;
        return n2 < this.attrIndexLimit[n] && !testBit(this.attrDefSeen[n], 1L << n2) && this.attrDefs.get(n).get(n2) != null;
    }
    
    protected void adjustSpecialAttrMasks() {
        this.attrClassFileVersionMask = (int)((long)this.attrClassFileVersionMask & ~this.attrDefSeen[0]);
        for (int i = 0; i < 4; ++i) {
            final int[] attrOverflowMask = this.attrOverflowMask;
            final int n = i;
            attrOverflowMask[n] = (int)((long)attrOverflowMask[n] & ~this.attrDefSeen[i]);
        }
    }
    
    protected Attribute makeClassFileVersionAttr(final Package.Version version) {
        return this.attrClassFileVersion.addContent(version.asBytes());
    }
    
    protected Package.Version parseClassFileVersionAttr(final Attribute attribute) {
        assert attribute.layout() == this.attrClassFileVersion;
        assert attribute.size() == 4;
        return Package.Version.of(attribute.bytes());
    }
    
    private boolean assertBandOKForElems(final Band[] array, final Attribute.Layout.Element[] array2) {
        for (int i = 0; i < array2.length; ++i) {
            assert this.assertBandOKForElem(array, array2[i]);
        }
        return true;
    }
    
    private boolean assertBandOKForElem(final Band[] array, final Attribute.Layout.Element element) {
        Band band = null;
        if (element.bandIndex != -1) {
            band = array[element.bandIndex];
        }
        Coding coding = BandStructure.UNSIGNED5;
        boolean b = true;
        switch (element.kind) {
            case 1: {
                if (element.flagTest((byte)1)) {
                    coding = BandStructure.SIGNED5;
                    break;
                }
                if (element.len == 1) {
                    coding = BandStructure.BYTE1;
                    break;
                }
                break;
            }
            case 2: {
                if (!element.flagTest((byte)2)) {
                    coding = BandStructure.BCI5;
                    break;
                }
                coding = BandStructure.BRANCH5;
                break;
            }
            case 3: {
                coding = BandStructure.BRANCH5;
                break;
            }
            case 4: {
                if (element.len == 1) {
                    coding = BandStructure.BYTE1;
                    break;
                }
                break;
            }
            case 5: {
                if (element.len == 1) {
                    coding = BandStructure.BYTE1;
                }
                this.assertBandOKForElems(array, element.body);
                break;
            }
            case 7: {
                if (element.flagTest((byte)1)) {
                    coding = BandStructure.SIGNED5;
                }
                else if (element.len == 1) {
                    coding = BandStructure.BYTE1;
                }
                this.assertBandOKForElems(array, element.body);
                break;
            }
            case 8: {
                assert band == null;
                this.assertBandOKForElems(array, element.body);
                return true;
            }
            case 9: {
                assert band == null;
                return true;
            }
            case 10: {
                assert band == null;
                this.assertBandOKForElems(array, element.body);
                return true;
            }
            case 6: {
                b = false;
                assert band instanceof CPRefBand;
                assert ((CPRefBand)band).nullOK == element.flagTest((byte)4);
                break;
            }
            default: {
                assert false;
                break;
            }
        }
        assert band.regularCoding == coding : element + " // " + band;
        if (b && !BandStructure.$assertionsDisabled && !(band instanceof IntBand)) {
            throw new AssertionError();
        }
        return true;
    }
    
    private Attribute.Layout predefineAttribute(final int n, final int n2, Band[] array, final String s, final String s2) {
        final Attribute.Layout layout = Attribute.find(n2, s, s2).layout();
        if (n >= 0) {
            this.setAttributeLayoutIndex(layout, n);
        }
        if (array == null) {
            array = new Band[0];
        }
        assert this.attrBandTable.get(layout) == null;
        this.attrBandTable.put(layout, array);
        assert layout.bandCount == array.length : layout + " // " + Arrays.asList(array);
        assert this.assertBandOKForElems(array, layout.elems);
        return layout;
    }
    
    private Attribute.Layout predefineAttribute(final int n, final String s, final MultiBand multiBand, final Attribute attribute) {
        final Attribute.Layout layout = attribute.layout();
        return this.predefineAttribute(n, layout.ctype(), this.makeNewAttributeBands(s, layout, multiBand), layout.name(), layout.layout());
    }
    
    private void undefineAttribute(final int n, final int n2) {
        if (this.verbose > 1) {
            System.out.println("Removing predefined " + Constants.ATTR_CONTEXT_NAME[n2] + " attribute on bit " + n);
        }
        final List list = this.attrDefs.get(n2);
        final Attribute.Layout layout = (Attribute.Layout)list.get(n);
        assert layout != null;
        list.set(n, null);
        this.attrIndexTable.put(layout, null);
        assert n < 64;
        final long[] attrDefSeen = this.attrDefSeen;
        attrDefSeen[n2] &= ~(1L << n);
        final long[] attrFlagMask = this.attrFlagMask;
        attrFlagMask[n2] &= ~(1L << n);
        final Band[] array = this.attrBandTable.get(layout);
        for (int i = 0; i < array.length; ++i) {
            array[i].doneWithUnusedBand();
        }
    }
    
    void makeNewAttributeBands() {
        this.adjustSpecialAttrMasks();
        for (int i = 0; i < 4; ++i) {
            final String s = Constants.ATTR_CONTEXT_NAME[i];
            final MultiBand multiBand = this.attrBands[i];
            final long n = this.attrDefSeen[i];
            assert (n & ~this.attrFlagMask[i]) == 0x0L;
            for (int j = 0; j < this.attrDefs.get(i).size(); ++j) {
                final Attribute.Layout layout = this.attrDefs.get(i).get(j);
                if (layout != null) {
                    if (layout.bandCount != 0) {
                        if (j < this.attrIndexLimit[i] && !testBit(n, 1L << j)) {
                            assert this.attrBandTable.get(layout) != null;
                        }
                        else {
                            multiBand.size();
                            final String string = s + "_" + layout.name() + "_";
                            if (this.verbose > 1) {
                                Utils.log.fine("Making new bands for " + layout);
                            }
                            final Band[] newAttributeBands = this.makeNewAttributeBands(string, layout, multiBand);
                            assert newAttributeBands.length == layout.bandCount;
                            final Band[] array = this.attrBandTable.put(layout, newAttributeBands);
                            if (array != null) {
                                for (int k = 0; k < array.length; ++k) {
                                    array[k].doneWithUnusedBand();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private Band[] makeNewAttributeBands(final String s, final Attribute.Layout layout, final MultiBand multiBand) {
        final int size = multiBand.size();
        this.makeNewAttributeBands(s, layout.elems, multiBand);
        final int n = multiBand.size() - size;
        final Band[] array = new Band[n];
        for (int i = 0; i < n; ++i) {
            array[i] = multiBand.get(size + i);
        }
        return array;
    }
    
    private void makeNewAttributeBands(final String s, final Attribute.Layout.Element[] array, final MultiBand multiBand) {
        for (int i = 0; i < array.length; ++i) {
            final Attribute.Layout.Element element = array[i];
            String s2 = s + multiBand.size() + "_" + element.layout;
            final int index;
            if ((index = s2.indexOf(91)) > 0) {
                s2 = s2.substring(0, index);
            }
            final int index2;
            if ((index2 = s2.indexOf(40)) > 0) {
                s2 = s2.substring(0, index2);
            }
            if (s2.endsWith("H")) {
                s2 = s2.substring(0, s2.length() - 1);
            }
            Band band = null;
            switch (element.kind) {
                case 1: {
                    band = this.newElemBand(element, s2, multiBand);
                    break;
                }
                case 2: {
                    if (!element.flagTest((byte)2)) {
                        band = multiBand.newIntBand(s2, BandStructure.BCI5);
                        break;
                    }
                    band = multiBand.newIntBand(s2, BandStructure.BRANCH5);
                    break;
                }
                case 3: {
                    band = multiBand.newIntBand(s2, BandStructure.BRANCH5);
                    break;
                }
                case 4: {
                    assert !element.flagTest((byte)1);
                    band = this.newElemBand(element, s2, multiBand);
                    break;
                }
                case 5: {
                    assert !element.flagTest((byte)1);
                    band = this.newElemBand(element, s2, multiBand);
                    this.makeNewAttributeBands(s, element.body, multiBand);
                    break;
                }
                case 7: {
                    band = this.newElemBand(element, s2, multiBand);
                    this.makeNewAttributeBands(s, element.body, multiBand);
                    break;
                }
                case 8: {
                    if (!element.flagTest((byte)8)) {
                        this.makeNewAttributeBands(s, element.body, multiBand);
                    }
                    continue;
                }
                case 6: {
                    band = multiBand.newCPRefBand(s2, BandStructure.UNSIGNED5, element.refKind, element.flagTest((byte)4));
                    break;
                }
                case 9: {
                    continue;
                }
                case 10: {
                    this.makeNewAttributeBands(s, element.body, multiBand);
                    continue;
                }
                default: {
                    assert false;
                    continue;
                }
            }
            if (this.verbose > 1) {
                Utils.log.fine("New attribute band " + band);
            }
        }
    }
    
    private Band newElemBand(final Attribute.Layout.Element element, final String s, final MultiBand multiBand) {
        if (element.flagTest((byte)1)) {
            return multiBand.newIntBand(s, BandStructure.SIGNED5);
        }
        if (element.len == 1) {
            return multiBand.newIntBand(s, BandStructure.BYTE1);
        }
        return multiBand.newIntBand(s, BandStructure.UNSIGNED5);
    }
    
    protected int setAttributeLayoutIndex(final Attribute.Layout layout, int size) {
        final int ctype = layout.ctype;
        assert -1 <= size && size < this.attrIndexLimit[ctype];
        final List list = this.attrDefs.get(ctype);
        if (size == -1) {
            size = list.size();
            list.add(layout);
            if (this.verbose > 0) {
                Utils.log.info("Adding new attribute at " + layout + ": " + size);
            }
            this.attrIndexTable.put(layout, size);
            return size;
        }
        if (testBit(this.attrDefSeen[ctype], 1L << size)) {
            throw new RuntimeException("Multiple explicit definition at " + size + ": " + layout);
        }
        final long[] attrDefSeen = this.attrDefSeen;
        final int n = ctype;
        attrDefSeen[n] |= 1L << size;
        assert 0 <= size && size < this.attrIndexLimit[ctype];
        if (this.verbose > ((this.attrClassFileVersionMask == 0) ? 2 : 0)) {
            Utils.log.fine("Fixing new attribute at " + size + ": " + layout + ((list.get(size) == null) ? "" : ("; replacing " + list.get(size))));
        }
        final long[] attrFlagMask = this.attrFlagMask;
        final int n2 = ctype;
        attrFlagMask[n2] |= 1L << size;
        this.attrIndexTable.put((Attribute.Layout)list.get(size), null);
        list.set(size, layout);
        this.attrIndexTable.put(layout, size);
        return size;
    }
    
    static int shortCodeHeader(final Code code) {
        final int max_stack = code.max_stack;
        final int max_locals = code.max_locals;
        final int length = code.handler_class.length;
        if (length >= BandStructure.shortCodeLimits.length) {
            return 0;
        }
        final int argumentSize = code.getMethod().getArgumentSize();
        assert max_locals >= argumentSize;
        if (max_locals < argumentSize) {
            return 0;
        }
        final int n = max_locals - argumentSize;
        final int n2 = BandStructure.shortCodeLimits[length][0];
        final int n3 = BandStructure.shortCodeLimits[length][1];
        if (max_stack >= n2 || n >= n3) {
            return 0;
        }
        final int n4 = shortCodeHeader_h_base(length) + (max_stack + n2 * n);
        if (n4 > 255) {
            return 0;
        }
        assert shortCodeHeader_max_stack(n4) == max_stack;
        assert shortCodeHeader_max_na_locals(n4) == n;
        assert shortCodeHeader_handler_count(n4) == length;
        return n4;
    }
    
    static int shortCodeHeader_handler_count(final int i) {
        assert i > 0 && i <= 255;
        int n;
        for (n = 0; i >= shortCodeHeader_h_base(n + 1); ++n) {}
        return n;
    }
    
    static int shortCodeHeader_max_stack(final int n) {
        final int shortCodeHeader_handler_count = shortCodeHeader_handler_count(n);
        return (n - shortCodeHeader_h_base(shortCodeHeader_handler_count)) % BandStructure.shortCodeLimits[shortCodeHeader_handler_count][0];
    }
    
    static int shortCodeHeader_max_na_locals(final int n) {
        final int shortCodeHeader_handler_count = shortCodeHeader_handler_count(n);
        return (n - shortCodeHeader_h_base(shortCodeHeader_handler_count)) / BandStructure.shortCodeLimits[shortCodeHeader_handler_count][0];
    }
    
    private static int shortCodeHeader_h_base(final int n) {
        assert n <= BandStructure.shortCodeLimits.length;
        int n2 = 1;
        for (int i = 0; i < n; ++i) {
            n2 += BandStructure.shortCodeLimits[i][0] * BandStructure.shortCodeLimits[i][1];
        }
        return n2;
    }
    
    protected void putLabel(final IntBand intBand, final Code code, final int n, final int n2) {
        intBand.putInt(code.encodeBCI(n2) - code.encodeBCI(n));
    }
    
    protected int getLabel(final IntBand intBand, final Code code, final int n) {
        return code.decodeBCI(intBand.getInt() + code.encodeBCI(n));
    }
    
    protected CPRefBand getCPRefOpBand(final int n) {
        Label_0231: {
            switch (Instruction.getCPRefOpTag(n)) {
                case 7: {
                    return this.bc_classref;
                }
                case 9: {
                    return this.bc_fieldref;
                }
                case 10: {
                    return this.bc_methodref;
                }
                case 11: {
                    return this.bc_imethodref;
                }
                case 18: {
                    return this.bc_indyref;
                }
                case 51: {
                    switch (n) {
                        case 234:
                        case 237: {
                            return this.bc_intref;
                        }
                        case 235:
                        case 238: {
                            return this.bc_floatref;
                        }
                        case 20: {
                            return this.bc_longref;
                        }
                        case 239: {
                            return this.bc_doubleref;
                        }
                        case 18:
                        case 19: {
                            return this.bc_stringref;
                        }
                        case 233:
                        case 236: {
                            return this.bc_classref;
                        }
                        case 240:
                        case 241: {
                            return this.bc_loadablevalueref;
                        }
                        default: {
                            break Label_0231;
                        }
                    }
                    break;
                }
            }
        }
        assert false;
        return null;
    }
    
    protected CPRefBand selfOpRefBand(final int n) {
        assert Instruction.isSelfLinkerOp(n);
        int n2 = n - 202;
        final boolean b = n2 >= 14;
        if (b) {
            n2 -= 14;
        }
        if (n2 >= 7) {
            n2 -= 7;
        }
        final boolean fieldOp = Instruction.isFieldOp(178 + n2);
        if (!b) {
            return fieldOp ? this.bc_thisfield : this.bc_thismethod;
        }
        return fieldOp ? this.bc_superfield : this.bc_supermethod;
    }
    
    static OutputStream getDumpStream(final Band band, final String s) throws IOException {
        return getDumpStream(band.name, band.seqForDebug, s, band);
    }
    
    static OutputStream getDumpStream(final ConstantPool.Index index, final String s) throws IOException {
        if (index.size() == 0) {
            return new ByteArrayOutputStream();
        }
        return getDumpStream(index.debugName, ConstantPool.TAG_ORDER[index.cpMap[0].tag], s, index);
    }
    
    static OutputStream getDumpStream(String s, final int n, final String s2, final Object o) throws IOException {
        if (BandStructure.dumpDir == null) {
            (BandStructure.dumpDir = File.createTempFile("BD_", "", new File("."))).delete();
            if (BandStructure.dumpDir.mkdir()) {
                Utils.log.info("Dumping bands to " + BandStructure.dumpDir);
            }
        }
        s = s.replace('(', ' ').replace(')', ' ');
        s = s.replace('/', ' ');
        s = s.replace('*', ' ');
        s = s.trim().replace(' ', '_');
        s = (10000 + n + "_" + s).substring(1);
        final File file = new File(BandStructure.dumpDir, s + s2);
        Utils.log.info("Dumping " + o + " to " + file);
        return new BufferedOutputStream(new FileOutputStream(file));
    }
    
    static boolean assertCanChangeLength(final Band band) {
        switch (band.phase) {
            case 1:
            case 4: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static boolean assertPhase(final Band band, final int n) {
        if (band.phase() != n) {
            Utils.log.warning("phase expected " + n + " was " + band.phase() + " in " + band);
            return false;
        }
        return true;
    }
    
    static int verbose() {
        return Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose");
    }
    
    static boolean assertPhaseChangeOK(final Band band, final int n, final int n2) {
        switch (n * 10 + n2) {
            case 1: {
                assert !band.isReader();
                assert band.capacity() >= 0;
                assert band.length() == 0;
                return true;
            }
            case 13:
            case 33: {
                assert band.length() == 0;
                return true;
            }
            case 15:
            case 35: {
                return true;
            }
            case 58: {
                return true;
            }
            case 2: {
                assert band.isReader();
                assert band.capacity() < 0;
                return true;
            }
            case 24: {
                assert Math.max(0, band.capacity()) >= band.valuesExpected();
                assert band.length() <= 0;
                return true;
            }
            case 46: {
                assert band.valuesRemainingForDebug() == band.length();
                return true;
            }
            case 68: {
                assert assertDoneDisbursing(band);
                return true;
            }
            default: {
                if (n == n2) {
                    Utils.log.warning("Already in phase " + n);
                }
                else {
                    Utils.log.warning("Unexpected phase " + n + " -> " + n2);
                }
                return false;
            }
        }
    }
    
    private static boolean assertDoneDisbursing(final Band band) {
        if (band.phase != 6) {
            Utils.log.warning("assertDoneDisbursing: still in phase " + band.phase + ": " + band);
            if (verbose() <= 1) {
                return false;
            }
        }
        final int valuesRemainingForDebug = band.valuesRemainingForDebug();
        if (valuesRemainingForDebug > 0) {
            Utils.log.warning("assertDoneDisbursing: " + valuesRemainingForDebug + " values left in " + band);
            if (verbose() <= 1) {
                return false;
            }
        }
        if (band instanceof MultiBand) {
            final MultiBand multiBand = (MultiBand)band;
            for (int i = 0; i < multiBand.bandCount; ++i) {
                final Band band2 = multiBand.bands[i];
                if (band2.phase != 8) {
                    Utils.log.warning("assertDoneDisbursing: sub-band still in phase " + band2.phase + ": " + band2);
                    if (verbose() <= 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private static void printCDecl(final Band band) {
        if (band instanceof MultiBand) {
            final MultiBand multiBand = (MultiBand)band;
            for (int i = 0; i < multiBand.bandCount; ++i) {
                printCDecl(multiBand.bands[i]);
            }
            return;
        }
        String string = "NULL";
        if (band instanceof CPRefBand) {
            final ConstantPool.Index index = ((CPRefBand)band).index;
            if (index != null) {
                string = "INDEX(" + index.debugName + ")";
            }
        }
        final Coding[] array = { BandStructure.BYTE1, BandStructure.CHAR3, BandStructure.BCI5, BandStructure.BRANCH5, BandStructure.UNSIGNED5, BandStructure.UDELTA5, BandStructure.SIGNED5, BandStructure.DELTA5, BandStructure.MDELTA5 };
        final String[] array2 = { "BYTE1", "CHAR3", "BCI5", "BRANCH5", "UNSIGNED5", "UDELTA5", "SIGNED5", "DELTA5", "MDELTA5" };
        final Coding regularCoding = band.regularCoding;
        final int index2 = Arrays.asList(array).indexOf(regularCoding);
        String string2;
        if (index2 >= 0) {
            string2 = array2[index2];
        }
        else {
            string2 = "CODING" + regularCoding.keyString();
        }
        System.out.println("  BAND_INIT(\"" + band.name() + "\", " + string2 + ", " + string + "),");
    }
    
    boolean notePrevForAssert(final Band band, final Band band2) {
        if (this.prevForAssertMap == null) {
            this.prevForAssertMap = new HashMap<Band, Band>();
        }
        this.prevForAssertMap.put(band, band2);
        return true;
    }
    
    private boolean assertReadyToReadFrom(final Band band, final InputStream inputStream) throws IOException {
        final Band band2 = this.prevForAssertMap.get(band);
        if (band2 != null && phaseCmp(band2.phase(), 6) < 0) {
            Utils.log.warning("Previous band not done reading.");
            Utils.log.info("    Previous band: " + band2);
            Utils.log.info("        Next band: " + band);
            assert this.verbose > 0;
        }
        final String access$500 = band.name;
        if (this.optDebugBands && !access$500.startsWith("(")) {
            assert BandStructure.bandSequenceList != null;
            final String s = BandStructure.bandSequenceList.removeFirst();
            if (!s.equals(access$500)) {
                Utils.log.warning("Expected " + access$500 + " but read: " + s);
                return false;
            }
            Utils.log.info("Read band in sequence: " + access$500);
        }
        return true;
    }
    
    private boolean assertValidCPRefs(final CPRefBand cpRefBand) {
        if (cpRefBand.index == null) {
            return true;
        }
        final int n = cpRefBand.index.size() + 1;
        for (int i = 0; i < cpRefBand.length(); ++i) {
            final int valueAtForDebug = cpRefBand.valueAtForDebug(i);
            if (valueAtForDebug < 0 || valueAtForDebug >= n) {
                Utils.log.warning("CP ref out of range [" + i + "] = " + valueAtForDebug + " in " + cpRefBand);
                return false;
            }
        }
        return true;
    }
    
    private boolean assertReadyToWriteTo(final Band band, final OutputStream outputStream) throws IOException {
        final Band band2 = this.prevForAssertMap.get(band);
        if (band2 != null && phaseCmp(band2.phase(), 8) < 0) {
            Utils.log.warning("Previous band not done writing.");
            Utils.log.info("    Previous band: " + band2);
            Utils.log.info("        Next band: " + band);
            assert this.verbose > 0;
        }
        final String access$500 = band.name;
        if (this.optDebugBands && !access$500.startsWith("(")) {
            if (BandStructure.bandSequenceList == null) {
                BandStructure.bandSequenceList = new LinkedList<String>();
            }
            BandStructure.bandSequenceList.add(access$500);
        }
        return true;
    }
    
    protected static boolean testBit(final int n, final int n2) {
        return (n & n2) != 0x0;
    }
    
    protected static int setBit(final int n, final int n2, final boolean b) {
        return b ? (n | n2) : (n & ~n2);
    }
    
    protected static boolean testBit(final long n, final long n2) {
        return (n & n2) != 0x0L;
    }
    
    protected static long setBit(final long n, final long n2, final boolean b) {
        return b ? (n | n2) : (n & ~n2);
    }
    
    static void printArrayTo(final PrintStream printStream, final int[] array, final int n, final int n2) {
        for (int n3 = n2 - n, i = 0; i < n3; ++i) {
            if (i % 10 == 0) {
                printStream.println();
            }
            else {
                printStream.print(" ");
            }
            printStream.print(array[n + i]);
        }
        printStream.println();
    }
    
    static void printArrayTo(final PrintStream printStream, final ConstantPool.Entry[] array, final int n, final int n2) {
        printArrayTo(printStream, array, n, n2, false);
    }
    
    static void printArrayTo(final PrintStream printStream, final ConstantPool.Entry[] array, final int n, final int n2, final boolean b) {
        final StringBuffer sb = new StringBuffer();
        for (int n3 = n2 - n, i = 0; i < n3; ++i) {
            final ConstantPool.Entry entry = array[n + i];
            printStream.print(n + i);
            printStream.print("=");
            if (b) {
                printStream.print(entry.tag);
                printStream.print(":");
            }
            final String stringValue = entry.stringValue();
            sb.setLength(0);
            for (int j = 0; j < stringValue.length(); ++j) {
                final char char1 = stringValue.charAt(j);
                if (char1 >= ' ' && char1 <= '~' && char1 != '\\') {
                    sb.append(char1);
                }
                else if (char1 == '\\') {
                    sb.append("\\\\");
                }
                else if (char1 == '\n') {
                    sb.append("\\n");
                }
                else if (char1 == '\t') {
                    sb.append("\\t");
                }
                else if (char1 == '\r') {
                    sb.append("\\r");
                }
                else {
                    final String string = "000" + Integer.toHexString(char1);
                    sb.append("\\u").append(string.substring(string.length() - 4));
                }
            }
            printStream.println(sb);
        }
    }
    
    protected static Object[] realloc(final Object[] array, final int n) {
        final Object[] array2 = (Object[])Array.newInstance(array.getClass().getComponentType(), n);
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, n));
        return array2;
    }
    
    protected static Object[] realloc(final Object[] array) {
        return realloc(array, Math.max(10, array.length * 2));
    }
    
    protected static int[] realloc(final int[] array, final int n) {
        if (n == 0) {
            return Constants.noInts;
        }
        if (array == null) {
            return new int[n];
        }
        final int[] array2 = new int[n];
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, n));
        return array2;
    }
    
    protected static int[] realloc(final int[] array) {
        return realloc(array, Math.max(10, array.length * 2));
    }
    
    protected static byte[] realloc(final byte[] array, final int n) {
        if (n == 0) {
            return Constants.noBytes;
        }
        if (array == null) {
            return new byte[n];
        }
        final byte[] array2 = new byte[n];
        System.arraycopy(array, 0, array2, 0, Math.min(array.length, n));
        return array2;
    }
    
    protected static byte[] realloc(final byte[] array) {
        return realloc(array, Math.max(10, array.length * 2));
    }
    
    static {
        BYTE1 = Coding.of(1, 256);
        CHAR3 = Coding.of(3, 128);
        BCI5 = Coding.of(5, 4);
        BRANCH5 = Coding.of(5, 4, 2);
        UNSIGNED5 = Coding.of(5, 64);
        UDELTA5 = BandStructure.UNSIGNED5.getDeltaCoding();
        SIGNED5 = Coding.of(5, 64, 1);
        DELTA5 = BandStructure.SIGNED5.getDeltaCoding();
        MDELTA5 = Coding.of(5, 64, 2).getDeltaCoding();
        basicCodings = new Coding[] { null, Coding.of(1, 256, 0), Coding.of(1, 256, 1), Coding.of(1, 256, 0).getDeltaCoding(), Coding.of(1, 256, 1).getDeltaCoding(), Coding.of(2, 256, 0), Coding.of(2, 256, 1), Coding.of(2, 256, 0).getDeltaCoding(), Coding.of(2, 256, 1).getDeltaCoding(), Coding.of(3, 256, 0), Coding.of(3, 256, 1), Coding.of(3, 256, 0).getDeltaCoding(), Coding.of(3, 256, 1).getDeltaCoding(), Coding.of(4, 256, 0), Coding.of(4, 256, 1), Coding.of(4, 256, 0).getDeltaCoding(), Coding.of(4, 256, 1).getDeltaCoding(), Coding.of(5, 4, 0), Coding.of(5, 4, 1), Coding.of(5, 4, 2), Coding.of(5, 16, 0), Coding.of(5, 16, 1), Coding.of(5, 16, 2), Coding.of(5, 32, 0), Coding.of(5, 32, 1), Coding.of(5, 32, 2), Coding.of(5, 64, 0), Coding.of(5, 64, 1), Coding.of(5, 64, 2), Coding.of(5, 128, 0), Coding.of(5, 128, 1), Coding.of(5, 128, 2), Coding.of(5, 4, 0).getDeltaCoding(), Coding.of(5, 4, 1).getDeltaCoding(), Coding.of(5, 4, 2).getDeltaCoding(), Coding.of(5, 16, 0).getDeltaCoding(), Coding.of(5, 16, 1).getDeltaCoding(), Coding.of(5, 16, 2).getDeltaCoding(), Coding.of(5, 32, 0).getDeltaCoding(), Coding.of(5, 32, 1).getDeltaCoding(), Coding.of(5, 32, 2).getDeltaCoding(), Coding.of(5, 64, 0).getDeltaCoding(), Coding.of(5, 64, 1).getDeltaCoding(), Coding.of(5, 64, 2).getDeltaCoding(), Coding.of(5, 128, 0).getDeltaCoding(), Coding.of(5, 128, 1).getDeltaCoding(), Coding.of(5, 128, 2).getDeltaCoding(), Coding.of(2, 192, 0), Coding.of(2, 224, 0), Coding.of(2, 240, 0), Coding.of(2, 248, 0), Coding.of(2, 252, 0), Coding.of(2, 8, 0).getDeltaCoding(), Coding.of(2, 8, 1).getDeltaCoding(), Coding.of(2, 16, 0).getDeltaCoding(), Coding.of(2, 16, 1).getDeltaCoding(), Coding.of(2, 32, 0).getDeltaCoding(), Coding.of(2, 32, 1).getDeltaCoding(), Coding.of(2, 64, 0).getDeltaCoding(), Coding.of(2, 64, 1).getDeltaCoding(), Coding.of(2, 128, 0).getDeltaCoding(), Coding.of(2, 128, 1).getDeltaCoding(), Coding.of(2, 192, 0).getDeltaCoding(), Coding.of(2, 192, 1).getDeltaCoding(), Coding.of(2, 224, 0).getDeltaCoding(), Coding.of(2, 224, 1).getDeltaCoding(), Coding.of(2, 240, 0).getDeltaCoding(), Coding.of(2, 240, 1).getDeltaCoding(), Coding.of(2, 248, 0).getDeltaCoding(), Coding.of(2, 248, 1).getDeltaCoding(), Coding.of(3, 192, 0), Coding.of(3, 224, 0), Coding.of(3, 240, 0), Coding.of(3, 248, 0), Coding.of(3, 252, 0), Coding.of(3, 8, 0).getDeltaCoding(), Coding.of(3, 8, 1).getDeltaCoding(), Coding.of(3, 16, 0).getDeltaCoding(), Coding.of(3, 16, 1).getDeltaCoding(), Coding.of(3, 32, 0).getDeltaCoding(), Coding.of(3, 32, 1).getDeltaCoding(), Coding.of(3, 64, 0).getDeltaCoding(), Coding.of(3, 64, 1).getDeltaCoding(), Coding.of(3, 128, 0).getDeltaCoding(), Coding.of(3, 128, 1).getDeltaCoding(), Coding.of(3, 192, 0).getDeltaCoding(), Coding.of(3, 192, 1).getDeltaCoding(), Coding.of(3, 224, 0).getDeltaCoding(), Coding.of(3, 224, 1).getDeltaCoding(), Coding.of(3, 240, 0).getDeltaCoding(), Coding.of(3, 240, 1).getDeltaCoding(), Coding.of(3, 248, 0).getDeltaCoding(), Coding.of(3, 248, 1).getDeltaCoding(), Coding.of(4, 192, 0), Coding.of(4, 224, 0), Coding.of(4, 240, 0), Coding.of(4, 248, 0), Coding.of(4, 252, 0), Coding.of(4, 8, 0).getDeltaCoding(), Coding.of(4, 8, 1).getDeltaCoding(), Coding.of(4, 16, 0).getDeltaCoding(), Coding.of(4, 16, 1).getDeltaCoding(), Coding.of(4, 32, 0).getDeltaCoding(), Coding.of(4, 32, 1).getDeltaCoding(), Coding.of(4, 64, 0).getDeltaCoding(), Coding.of(4, 64, 1).getDeltaCoding(), Coding.of(4, 128, 0).getDeltaCoding(), Coding.of(4, 128, 1).getDeltaCoding(), Coding.of(4, 192, 0).getDeltaCoding(), Coding.of(4, 192, 1).getDeltaCoding(), Coding.of(4, 224, 0).getDeltaCoding(), Coding.of(4, 224, 1).getDeltaCoding(), Coding.of(4, 240, 0).getDeltaCoding(), Coding.of(4, 240, 1).getDeltaCoding(), Coding.of(4, 248, 0).getDeltaCoding(), Coding.of(4, 248, 1).getDeltaCoding(), null };
        assert BandStructure.basicCodings[0] == null;
        assert BandStructure.basicCodings[1] != null;
        assert BandStructure.basicCodings[115] != null;
        final HashMap basicCodingIndexes2 = new HashMap();
        for (int i = 0; i < BandStructure.basicCodings.length; ++i) {
            final Coding coding = BandStructure.basicCodings[i];
            if (coding != null) {
                assert i >= 1;
                assert i <= 115;
                basicCodingIndexes2.put(coding, i);
            }
        }
        basicCodingIndexes = basicCodingIndexes2;
        defaultMetaCoding = new byte[] { 0 };
        noMetaCoding = new byte[0];
        boolean b = false;
        assert b = true;
        if (b) {
            for (int j = 0; j < BandStructure.basicCodings.length; ++j) {
                final Coding coding2 = BandStructure.basicCodings[j];
                if (coding2 != null) {
                    if (coding2.B() != 1) {
                        if (coding2.L() != 0) {
                            for (int k = 0; k <= 255; ++k) {
                                encodeEscapeValue(k, coding2);
                            }
                        }
                    }
                }
            }
        }
        shortCodeLimits = new int[][] { { 12, 12 }, { 8, 8 }, { 7, 7 } };
        BandStructure.dumpDir = null;
        BandStructure.bandSequenceList = null;
    }
    
    abstract class Band
    {
        private int phase;
        private final String name;
        private int valuesExpected;
        protected long outputSize;
        public final Coding regularCoding;
        public final int seqForDebug;
        public int elementCountForDebug;
        protected int lengthForDebug;
        
        protected Band(final String name, final Coding regularCoding) {
            this.phase = 0;
            this.outputSize = -1L;
            this.lengthForDebug = -1;
            this.name = name;
            this.regularCoding = regularCoding;
            this.seqForDebug = ++BandStructure.nextSeqForDebug;
            if (BandStructure.this.verbose > 2) {
                Utils.log.fine("Band " + this.seqForDebug + " is " + name);
            }
        }
        
        public Band init() {
            if (BandStructure.this.isReader) {
                this.readyToExpect();
            }
            else {
                this.readyToCollect();
            }
            return this;
        }
        
        boolean isReader() {
            return BandStructure.this.isReader;
        }
        
        int phase() {
            return this.phase;
        }
        
        String name() {
            return this.name;
        }
        
        public abstract int capacity();
        
        protected abstract void setCapacity(final int p0);
        
        public abstract int length();
        
        protected abstract int valuesRemainingForDebug();
        
        public final int valuesExpected() {
            return this.valuesExpected;
        }
        
        public final void writeTo(final OutputStream outputStream) throws IOException {
            assert BandStructure.this.assertReadyToWriteTo(this, outputStream);
            this.setPhase(5);
            this.writeDataTo(outputStream);
            this.doneWriting();
        }
        
        abstract void chooseBandCodings() throws IOException;
        
        public final long outputSize() {
            if (this.outputSize < 0L) {
                return this.computeOutputSize();
            }
            final long outputSize = this.outputSize;
            assert outputSize == this.computeOutputSize();
            return outputSize;
        }
        
        protected abstract long computeOutputSize();
        
        protected abstract void writeDataTo(final OutputStream p0) throws IOException;
        
        void expectLength(final int valuesExpected) {
            assert BandStructure.assertPhase(this, 2);
            assert this.valuesExpected == 0;
            assert valuesExpected >= 0;
            this.valuesExpected = valuesExpected;
        }
        
        void expectMoreLength(final int n) {
            assert BandStructure.assertPhase(this, 2);
            this.valuesExpected += n;
        }
        
        private void readyToCollect() {
            this.setCapacity(1);
            this.setPhase(1);
        }
        
        protected void doneWriting() {
            assert BandStructure.assertPhase(this, 5);
            this.setPhase(8);
        }
        
        private void readyToExpect() {
            this.setPhase(2);
        }
        
        public final void readFrom(final InputStream inputStream) throws IOException {
            assert BandStructure.this.assertReadyToReadFrom(this, inputStream);
            this.setCapacity(this.valuesExpected());
            this.setPhase(4);
            this.readDataFrom(inputStream);
            this.readyToDisburse();
        }
        
        protected abstract void readDataFrom(final InputStream p0) throws IOException;
        
        protected void readyToDisburse() {
            if (BandStructure.this.verbose > 1) {
                Utils.log.fine("readyToDisburse " + this);
            }
            this.setPhase(6);
        }
        
        public void doneDisbursing() {
            assert BandStructure.assertPhase(this, 6);
            this.setPhase(8);
        }
        
        public final void doneWithUnusedBand() {
            if (BandStructure.this.isReader) {
                assert BandStructure.assertPhase(this, 2);
                assert this.valuesExpected() == 0;
                this.setPhase(4);
                this.setPhase(6);
                this.setPhase(8);
            }
            else {
                this.setPhase(3);
            }
        }
        
        protected void setPhase(final int phase) {
            assert BandStructure.assertPhaseChangeOK(this, this.phase, phase);
            this.phase = phase;
        }
        
        @Override
        public String toString() {
            final int n = (this.lengthForDebug != -1) ? this.lengthForDebug : this.length();
            String s = this.name;
            if (n != 0) {
                s = s + "[" + n + "]";
            }
            if (this.elementCountForDebug != 0) {
                s = s + "(" + this.elementCountForDebug + ")";
            }
            return s;
        }
    }
    
    class ValueBand extends Band
    {
        private int[] values;
        private int length;
        private int valuesDisbursed;
        private CodingMethod bandCoding;
        private byte[] metaCoding;
        static final /* synthetic */ boolean $assertionsDisabled;
        
        protected ValueBand(final String s, final Coding coding) {
            super(s, coding);
        }
        
        @Override
        public int capacity() {
            return (this.values == null) ? -1 : this.values.length;
        }
        
        @Override
        protected void setCapacity(final int n) {
            assert this.length <= n;
            if (n == -1) {
                this.values = null;
                return;
            }
            this.values = BandStructure.realloc(this.values, n);
        }
        
        @Override
        public int length() {
            return this.length;
        }
        
        @Override
        protected int valuesRemainingForDebug() {
            return this.length - this.valuesDisbursed;
        }
        
        protected int valueAtForDebug(final int n) {
            return this.values[n];
        }
        
        void patchValue(final int n, final int n2) {
            assert this == BandStructure.this.archive_header_S;
            assert n == 1;
            assert n < this.length;
            this.values[n] = n2;
            this.outputSize = -1L;
        }
        
        protected void initializeValues(final int[] values) {
            assert BandStructure.assertCanChangeLength(this);
            assert this.length == 0;
            this.values = values;
            this.length = values.length;
        }
        
        protected void addValue(final int n) {
            assert BandStructure.assertCanChangeLength(this);
            if (this.length == this.values.length) {
                this.setCapacity((this.length < 1000) ? (this.length * 10) : (this.length * 2));
            }
            this.values[this.length++] = n;
        }
        
        private boolean canVaryCoding() {
            return BandStructure.this.optVaryCodings && this.length != 0 && this != BandStructure.this.archive_header_0 && this != BandStructure.this.archive_header_S && this != BandStructure.this.archive_header_1 && (this.regularCoding.min() <= -256 || this.regularCoding.max() >= 256);
        }
        
        private boolean shouldVaryCoding() {
            assert this.canVaryCoding();
            return BandStructure.this.effort >= 9 || this.length >= 100;
        }
        
        protected void chooseBandCodings() throws IOException {
            final boolean canVaryCoding = this.canVaryCoding();
            if (!canVaryCoding || !this.shouldVaryCoding()) {
                if (this.regularCoding.canRepresent(this.values, 0, this.length)) {
                    this.bandCoding = this.regularCoding;
                }
                else {
                    assert canVaryCoding;
                    if (BandStructure.this.verbose > 1) {
                        Utils.log.fine("regular coding fails in band " + this.name());
                    }
                    this.bandCoding = BandStructure.UNSIGNED5;
                }
                this.outputSize = -1L;
            }
            else {
                final int[] array = { 0, 0 };
                this.bandCoding = BandStructure.this.chooseCoding(this.values, 0, this.length, this.regularCoding, this.name(), array);
                this.outputSize = array[0];
                if (this.outputSize == 0L) {
                    this.outputSize = -1L;
                }
            }
            if (this.bandCoding != this.regularCoding) {
                this.metaCoding = this.bandCoding.getMetaCoding(this.regularCoding);
                if (BandStructure.this.verbose > 1) {
                    Utils.log.fine("alternate coding " + this + " " + this.bandCoding);
                }
            }
            else if (canVaryCoding && BandStructure.decodeEscapeValue(this.values[0], this.regularCoding) >= 0) {
                this.metaCoding = BandStructure.defaultMetaCoding;
            }
            else {
                this.metaCoding = BandStructure.noMetaCoding;
            }
            if (this.metaCoding.length > 0 && (BandStructure.this.verbose > 2 || (BandStructure.this.verbose > 1 && this.metaCoding.length > 1))) {
                final StringBuffer sb = new StringBuffer();
                for (int i = 0; i < this.metaCoding.length; ++i) {
                    if (i == 1) {
                        sb.append(" /");
                    }
                    sb.append(" ").append(this.metaCoding[i] & 0xFF);
                }
                Utils.log.fine("   meta-coding " + (Object)sb);
            }
            assert this.outputSize == ((Coding)this.bandCoding).getLength(this.values, 0, this.length) : this.bandCoding + " : " + this.outputSize + " != " + ((Coding)this.bandCoding).getLength(this.values, 0, this.length) + " ?= " + BandStructure.this.getCodingChooser().computeByteSize(this.bandCoding, this.values, 0, this.length);
            if (this.metaCoding.length > 0) {
                if (this.outputSize >= 0L) {
                    this.outputSize += this.computeEscapeSize();
                }
                for (int j = 1; j < this.metaCoding.length; ++j) {
                    BandStructure.this.band_headers.putByte(this.metaCoding[j] & 0xFF);
                }
            }
        }
        
        @Override
        protected long computeOutputSize() {
            this.outputSize = BandStructure.this.getCodingChooser().computeByteSize(this.bandCoding, this.values, 0, this.length);
            assert this.outputSize < 2147483647L;
            return this.outputSize += this.computeEscapeSize();
        }
        
        protected int computeEscapeSize() {
            if (this.metaCoding.length == 0) {
                return 0;
            }
            return this.regularCoding.setD(0).getLength(BandStructure.encodeEscapeValue(this.metaCoding[0] & 0xFF, this.regularCoding));
        }
        
        @Override
        protected void writeDataTo(final OutputStream outputStream) throws IOException {
            if (this.length == 0) {
                return;
            }
            long count = 0L;
            if (outputStream == BandStructure.this.outputCounter) {
                count = BandStructure.this.outputCounter.getCount();
            }
            if (this.metaCoding.length > 0) {
                this.regularCoding.setD(0).writeTo(outputStream, BandStructure.encodeEscapeValue(this.metaCoding[0] & 0xFF, this.regularCoding));
            }
            this.bandCoding.writeArrayTo(outputStream, this.values, 0, this.length);
            if (outputStream == BandStructure.this.outputCounter && !ValueBand.$assertionsDisabled && this.outputSize != BandStructure.this.outputCounter.getCount() - count) {
                throw new AssertionError((Object)(this.outputSize + " != " + BandStructure.this.outputCounter.getCount() + "-" + count));
            }
            if (BandStructure.this.optDumpBands) {
                this.dumpBand();
            }
        }
        
        @Override
        protected void readDataFrom(final InputStream inputStream) throws IOException {
            this.length = this.valuesExpected();
            if (this.length == 0) {
                return;
            }
            if (BandStructure.this.verbose > 1) {
                Utils.log.fine("Reading band " + this);
            }
            if (!this.canVaryCoding()) {
                this.bandCoding = this.regularCoding;
                this.metaCoding = BandStructure.noMetaCoding;
            }
            else {
                assert inputStream.markSupported();
                inputStream.mark(5);
                final int from = this.regularCoding.setD(0).readFrom(inputStream);
                final int decodeEscapeValue = BandStructure.decodeEscapeValue(from, this.regularCoding);
                if (decodeEscapeValue < 0) {
                    inputStream.reset();
                    this.bandCoding = this.regularCoding;
                    this.metaCoding = BandStructure.noMetaCoding;
                }
                else if (decodeEscapeValue == 0) {
                    this.bandCoding = this.regularCoding;
                    this.metaCoding = BandStructure.defaultMetaCoding;
                }
                else {
                    if (BandStructure.this.verbose > 2) {
                        Utils.log.fine("found X=" + from + " => XB=" + decodeEscapeValue);
                    }
                    this.bandCoding = BandStructure.this.getBandHeader(decodeEscapeValue, this.regularCoding);
                    final int bandHeaderBytePos0 = BandStructure.this.bandHeaderBytePos0;
                    this.metaCoding = new byte[BandStructure.this.bandHeaderBytePos - bandHeaderBytePos0];
                    System.arraycopy(BandStructure.this.bandHeaderBytes, bandHeaderBytePos0, this.metaCoding, 0, this.metaCoding.length);
                }
            }
            if (this.bandCoding != this.regularCoding && BandStructure.this.verbose > 1) {
                Utils.log.fine(this.name() + ": irregular coding " + this.bandCoding);
            }
            this.bandCoding.readArrayFrom(inputStream, this.values, 0, this.length);
            if (BandStructure.this.optDumpBands) {
                this.dumpBand();
            }
        }
        
        @Override
        public void doneDisbursing() {
            super.doneDisbursing();
            this.values = null;
        }
        
        private void dumpBand() throws IOException {
            assert BandStructure.this.optDumpBands;
            try (final PrintStream printStream = new PrintStream(BandStructure.getDumpStream(this, ".txt"))) {
                printStream.print("# length=" + this.length + " size=" + this.outputSize() + ((this.bandCoding == this.regularCoding) ? "" : " irregular") + " coding=" + this.bandCoding);
                if (this.metaCoding != BandStructure.noMetaCoding) {
                    final StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < this.metaCoding.length; ++i) {
                        if (i == 1) {
                            sb.append(" /");
                        }
                        sb.append(" ").append(this.metaCoding[i] & 0xFF);
                    }
                    printStream.print(" //header: " + (Object)sb);
                }
                BandStructure.printArrayTo(printStream, this.values, 0, this.length);
            }
            try (final OutputStream dumpStream = BandStructure.getDumpStream(this, ".bnd")) {
                this.bandCoding.writeArrayTo(dumpStream, this.values, 0, this.length);
            }
        }
        
        protected int getValue() {
            assert this.phase() == 6;
            if (BandStructure.this.optDebugBands && this.length == 0 && this.valuesDisbursed == this.length) {
                return 0;
            }
            assert this.valuesDisbursed <= this.length;
            return this.values[this.valuesDisbursed++];
        }
        
        public void resetForSecondPass() {
            assert this.phase() == 6;
            assert this.valuesDisbursed == this.length();
            this.valuesDisbursed = 0;
        }
    }
    
    class ByteBand extends Band
    {
        private ByteArrayOutputStream bytes;
        private ByteArrayOutputStream bytesForDump;
        private InputStream in;
        
        public ByteBand(final String s) {
            super(s, BandStructure.BYTE1);
        }
        
        @Override
        public int capacity() {
            return (this.bytes == null) ? -1 : Integer.MAX_VALUE;
        }
        
        @Override
        protected void setCapacity(final int n) {
            assert this.bytes == null;
            this.bytes = new ByteArrayOutputStream(n);
        }
        
        public void destroy() {
            this.lengthForDebug = this.length();
            this.bytes = null;
        }
        
        @Override
        public int length() {
            return (this.bytes == null) ? -1 : this.bytes.size();
        }
        
        public void reset() {
            this.bytes.reset();
        }
        
        @Override
        protected int valuesRemainingForDebug() {
            return (this.bytes == null) ? -1 : ((ByteArrayInputStream)this.in).available();
        }
        
        protected void chooseBandCodings() throws IOException {
            assert BandStructure.decodeEscapeValue(this.regularCoding.min(), this.regularCoding) < 0;
            assert BandStructure.decodeEscapeValue(this.regularCoding.max(), this.regularCoding) < 0;
        }
        
        @Override
        protected long computeOutputSize() {
            return this.bytes.size();
        }
        
        public void writeDataTo(final OutputStream outputStream) throws IOException {
            if (this.length() == 0) {
                return;
            }
            this.bytes.writeTo(outputStream);
            if (BandStructure.this.optDumpBands) {
                this.dumpBand();
            }
            this.destroy();
        }
        
        private void dumpBand() throws IOException {
            assert BandStructure.this.optDumpBands;
            try (final OutputStream dumpStream = BandStructure.getDumpStream(this, ".bnd")) {
                if (this.bytesForDump != null) {
                    this.bytesForDump.writeTo(dumpStream);
                }
                else {
                    this.bytes.writeTo(dumpStream);
                }
            }
        }
        
        public void readDataFrom(final InputStream inputStream) throws IOException {
            int i = this.valuesExpected();
            if (i == 0) {
                return;
            }
            if (BandStructure.this.verbose > 1) {
                this.lengthForDebug = i;
                Utils.log.fine("Reading band " + this);
                this.lengthForDebug = -1;
            }
            final byte[] array = new byte[Math.min(i, 16384)];
            while (i > 0) {
                final int read = inputStream.read(array, 0, Math.min(i, array.length));
                if (read < 0) {
                    throw new EOFException();
                }
                this.bytes.write(array, 0, read);
                i -= read;
            }
            if (BandStructure.this.optDumpBands) {
                this.dumpBand();
            }
        }
        
        public void readyToDisburse() {
            this.in = new ByteArrayInputStream(this.bytes.toByteArray());
            super.readyToDisburse();
        }
        
        @Override
        public void doneDisbursing() {
            super.doneDisbursing();
            if (BandStructure.this.optDumpBands && this.bytesForDump != null && this.bytesForDump.size() > 0) {
                try {
                    this.dumpBand();
                }
                catch (final IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            this.in = null;
            this.bytes = null;
            this.bytesForDump = null;
        }
        
        public void setInputStreamFrom(final InputStream in) throws IOException {
            assert this.bytes == null;
            assert BandStructure.this.assertReadyToReadFrom(this, in);
            this.setPhase(4);
            this.in = in;
            if (BandStructure.this.optDumpBands) {
                this.bytesForDump = new ByteArrayOutputStream();
                this.in = new FilterInputStream(in) {
                    @Override
                    public int read() throws IOException {
                        final int read = this.in.read();
                        if (read >= 0) {
                            ByteBand.this.bytesForDump.write(read);
                        }
                        return read;
                    }
                    
                    @Override
                    public int read(final byte[] array, final int n, final int n2) throws IOException {
                        final int read = this.in.read(array, n, n2);
                        if (read >= 0) {
                            ByteBand.this.bytesForDump.write(array, n, read);
                        }
                        return read;
                    }
                };
            }
            super.readyToDisburse();
        }
        
        public OutputStream collectorStream() {
            assert this.phase() == 1;
            assert this.bytes != null;
            return this.bytes;
        }
        
        public InputStream getInputStream() {
            assert this.phase() == 6;
            assert this.in != null;
            return this.in;
        }
        
        public int getByte() throws IOException {
            final int read = this.getInputStream().read();
            if (read < 0) {
                throw new EOFException();
            }
            return read;
        }
        
        public void putByte(final int n) throws IOException {
            assert n == (n & 0xFF);
            this.collectorStream().write(n);
        }
        
        @Override
        public String toString() {
            return "byte " + super.toString();
        }
    }
    
    class IntBand extends ValueBand
    {
        public IntBand(final String s, final Coding coding) {
            super(s, coding);
        }
        
        public void putInt(final int n) {
            assert this.phase() == 1;
            this.addValue(n);
        }
        
        public int getInt() {
            return this.getValue();
        }
        
        public int getIntTotal() {
            assert this.phase() == 6;
            assert this.valuesRemainingForDebug() == this.length();
            int n = 0;
            for (int i = this.length(); i > 0; --i) {
                n += this.getInt();
            }
            this.resetForSecondPass();
            return n;
        }
        
        public int getIntCount(final int n) {
            assert this.phase() == 6;
            assert this.valuesRemainingForDebug() == this.length();
            int n2 = 0;
            for (int i = this.length(); i > 0; --i) {
                if (this.getInt() == n) {
                    ++n2;
                }
            }
            this.resetForSecondPass();
            return n2;
        }
    }
    
    class CPRefBand extends ValueBand
    {
        ConstantPool.Index index;
        boolean nullOK;
        
        public CPRefBand(final String s, final Coding coding, final byte b, final boolean nullOK) {
            super(s, coding);
            this.nullOK = nullOK;
            if (b != 0) {
                BandStructure.this.setBandIndex(this, b);
            }
        }
        
        public CPRefBand(final BandStructure bandStructure, final String s, final Coding coding, final byte b) {
            this(bandStructure, s, coding, b, false);
        }
        
        public CPRefBand(final BandStructure bandStructure, final String s, final Coding coding, final Object o) {
            this(bandStructure, s, coding, (byte)0, false);
        }
        
        public void setIndex(final ConstantPool.Index index) {
            this.index = index;
        }
        
        @Override
        protected void readDataFrom(final InputStream inputStream) throws IOException {
            super.readDataFrom(inputStream);
            assert BandStructure.this.assertValidCPRefs(this);
        }
        
        public void putRef(final ConstantPool.Entry entry) {
            this.addValue(this.encodeRefOrNull(entry, this.index));
        }
        
        public void putRef(final ConstantPool.Entry entry, final ConstantPool.Index index) {
            assert this.index == null;
            this.addValue(this.encodeRefOrNull(entry, index));
        }
        
        public void putRef(final ConstantPool.Entry entry, final byte b) {
            this.putRef(entry, BandStructure.this.getCPIndex(b));
        }
        
        public ConstantPool.Entry getRef() {
            if (this.index == null) {
                Utils.log.warning("No index for " + this);
            }
            assert this.index != null;
            return this.decodeRefOrNull(this.getValue(), this.index);
        }
        
        public ConstantPool.Entry getRef(final ConstantPool.Index index) {
            assert this.index == null;
            return this.decodeRefOrNull(this.getValue(), index);
        }
        
        public ConstantPool.Entry getRef(final byte b) {
            return this.getRef(BandStructure.this.getCPIndex(b));
        }
        
        private int encodeRefOrNull(final ConstantPool.Entry entry, final ConstantPool.Index index) {
            int encodeRef;
            if (entry == null) {
                encodeRef = -1;
            }
            else {
                encodeRef = BandStructure.this.encodeRef(entry, index);
            }
            return (this.nullOK ? 1 : 0) + encodeRef;
        }
        
        private ConstantPool.Entry decodeRefOrNull(final int n, final ConstantPool.Index index) {
            final int n2 = n - (this.nullOK ? 1 : 0);
            if (n2 == -1) {
                return null;
            }
            return BandStructure.this.decodeRef(n2, index);
        }
    }
    
    class MultiBand extends Band
    {
        Band[] bands;
        int bandCount;
        private int cap;
        
        MultiBand(final String s, final Coding coding) {
            super(s, coding);
            this.bands = new Band[10];
            this.bandCount = 0;
            this.cap = -1;
        }
        
        @Override
        public Band init() {
            super.init();
            this.setCapacity(0);
            if (this.phase() == 2) {
                this.setPhase(4);
                this.setPhase(6);
            }
            return this;
        }
        
        int size() {
            return this.bandCount;
        }
        
        Band get(final int n) {
            assert n < this.bandCount;
            return this.bands[n];
        }
        
        Band[] toArray() {
            return (Band[])BandStructure.realloc(this.bands, this.bandCount);
        }
        
        void add(final Band band) {
            assert !(!BandStructure.this.notePrevForAssert(band, this.bands[this.bandCount - 1]));
            if (this.bandCount == this.bands.length) {
                this.bands = (Band[])BandStructure.realloc(this.bands);
            }
            this.bands[this.bandCount++] = band;
        }
        
        ByteBand newByteBand(final String s) {
            final ByteBand byteBand = new ByteBand(s);
            byteBand.init();
            this.add(byteBand);
            return byteBand;
        }
        
        IntBand newIntBand(final String s) {
            final IntBand intBand = new IntBand(s, this.regularCoding);
            intBand.init();
            this.add(intBand);
            return intBand;
        }
        
        IntBand newIntBand(final String s, final Coding coding) {
            final IntBand intBand = new IntBand(s, coding);
            intBand.init();
            this.add(intBand);
            return intBand;
        }
        
        MultiBand newMultiBand(final String s, final Coding coding) {
            final MultiBand multiBand = new MultiBand(s, coding);
            multiBand.init();
            this.add(multiBand);
            return multiBand;
        }
        
        CPRefBand newCPRefBand(final String s, final byte b) {
            final CPRefBand cpRefBand = new CPRefBand(s, this.regularCoding, b);
            cpRefBand.init();
            this.add(cpRefBand);
            return cpRefBand;
        }
        
        CPRefBand newCPRefBand(final String s, final Coding coding, final byte b) {
            final CPRefBand cpRefBand = new CPRefBand(s, coding, b);
            cpRefBand.init();
            this.add(cpRefBand);
            return cpRefBand;
        }
        
        CPRefBand newCPRefBand(final String s, final Coding coding, final byte b, final boolean b2) {
            final CPRefBand cpRefBand = new CPRefBand(s, coding, b, b2);
            cpRefBand.init();
            this.add(cpRefBand);
            return cpRefBand;
        }
        
        int bandCount() {
            return this.bandCount;
        }
        
        @Override
        public int capacity() {
            return this.cap;
        }
        
        public void setCapacity(final int cap) {
            this.cap = cap;
        }
        
        @Override
        public int length() {
            return 0;
        }
        
        public int valuesRemainingForDebug() {
            return 0;
        }
        
        protected void chooseBandCodings() throws IOException {
            for (int i = 0; i < this.bandCount; ++i) {
                this.bands[i].chooseBandCodings();
            }
        }
        
        @Override
        protected long computeOutputSize() {
            long n = 0L;
            for (int i = 0; i < this.bandCount; ++i) {
                final Band band = this.bands[i];
                final long outputSize = band.outputSize();
                assert outputSize >= 0L : band;
                n += outputSize;
            }
            return n;
        }
        
        @Override
        protected void writeDataTo(final OutputStream outputStream) throws IOException {
            long count = 0L;
            if (BandStructure.this.outputCounter != null) {
                count = BandStructure.this.outputCounter.getCount();
            }
            for (int i = 0; i < this.bandCount; ++i) {
                final Band band = this.bands[i];
                band.writeTo(outputStream);
                if (BandStructure.this.outputCounter != null) {
                    final long count2 = BandStructure.this.outputCounter.getCount();
                    final long n = count2 - count;
                    count = count2;
                    if ((BandStructure.this.verbose > 0 && n > 0L) || BandStructure.this.verbose > 1) {
                        Utils.log.info("  ...wrote " + n + " bytes from " + band);
                    }
                }
            }
        }
        
        @Override
        protected void readDataFrom(final InputStream inputStream) throws IOException {
            assert false;
            for (int i = 0; i < this.bandCount; ++i) {
                final Band band = this.bands[i];
                band.readFrom(inputStream);
                if ((BandStructure.this.verbose > 0 && band.length() > 0) || BandStructure.this.verbose > 1) {
                    Utils.log.info("  ...read " + band);
                }
            }
        }
        
        @Override
        public String toString() {
            return "{" + this.bandCount() + " bands: " + super.toString() + "}";
        }
    }
    
    private static class ByteCounter extends FilterOutputStream
    {
        private long count;
        
        public ByteCounter(final OutputStream outputStream) {
            super(outputStream);
        }
        
        public long getCount() {
            return this.count;
        }
        
        public void setCount(final long count) {
            this.count = count;
        }
        
        @Override
        public void write(final int n) throws IOException {
            ++this.count;
            if (this.out != null) {
                this.out.write(n);
            }
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            this.count += n2;
            if (this.out != null) {
                this.out.write(array, n, n2);
            }
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.getCount());
        }
    }
}
