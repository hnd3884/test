package org.apache.commons.compress.harmony.pack200;

import java.io.IOException;
import java.io.OutputStream;

public class SegmentHeader extends BandSet
{
    private static final int[] magic;
    private static final int archive_minver = 7;
    private static final int archive_majver = 150;
    private int archive_options;
    private int cp_Utf8_count;
    private int cp_Int_count;
    private int cp_Float_count;
    private int cp_Long_count;
    private int cp_Double_count;
    private int cp_String_count;
    private int cp_Class_count;
    private int cp_Signature_count;
    private int cp_Descr_count;
    private int cp_Field_count;
    private int cp_Method_count;
    private int cp_Imethod_count;
    private int attribute_definition_count;
    private final IntList band_headers;
    private boolean have_all_code_flags;
    private int archive_size_hi;
    private int archive_size_lo;
    private int archive_next_count;
    private int archive_modtime;
    private int file_count;
    private boolean deflate_hint;
    private final boolean have_file_modtime = true;
    private final boolean have_file_options = true;
    private boolean have_file_size_hi;
    private boolean have_class_flags_hi;
    private boolean have_field_flags_hi;
    private boolean have_method_flags_hi;
    private boolean have_code_flags_hi;
    private int ic_count;
    private int class_count;
    private final Counter majverCounter;
    
    public SegmentHeader() {
        super(1, null);
        this.band_headers = new IntList();
        this.have_all_code_flags = true;
        this.majverCounter = new Counter();
    }
    
    @Override
    public void pack(final OutputStream out) throws IOException, Pack200Exception {
        out.write(this.encodeScalar(SegmentHeader.magic, Codec.BYTE1));
        out.write(this.encodeScalar(7, Codec.UNSIGNED5));
        out.write(this.encodeScalar(150, Codec.UNSIGNED5));
        this.calculateArchiveOptions();
        out.write(this.encodeScalar(this.archive_options, Codec.UNSIGNED5));
        this.writeArchiveFileCounts(out);
        this.writeArchiveSpecialCounts(out);
        this.writeCpCounts(out);
        this.writeClassCounts(out);
        if (this.band_headers.size() > 0) {
            out.write(this.encodeScalar(this.band_headers.toArray(), Codec.BYTE1));
        }
    }
    
    private void calculateArchiveOptions() {
        if (this.attribute_definition_count > 0 || this.band_headers.size() > 0) {
            this.archive_options |= 0x1;
        }
        if (this.cp_Int_count > 0 || this.cp_Float_count > 0 || this.cp_Long_count > 0 || this.cp_Double_count > 0) {
            this.archive_options |= 0x2;
        }
        if (this.have_all_code_flags) {
            this.archive_options |= 0x4;
        }
        if (this.file_count > 0) {
            this.archive_options |= 0x10;
        }
        if (this.deflate_hint) {
            this.archive_options |= 0x20;
        }
        this.archive_options |= 0x40;
        this.archive_options |= 0x80;
        if (this.have_file_size_hi) {
            this.archive_options |= 0x100;
        }
        if (this.have_class_flags_hi) {
            this.archive_options |= 0x200;
        }
        if (this.have_field_flags_hi) {
            this.archive_options |= 0x400;
        }
        if (this.have_method_flags_hi) {
            this.archive_options |= 0x800;
        }
        if (this.have_code_flags_hi) {
            this.archive_options |= 0x1000;
        }
    }
    
    public void setCp_Utf8_count(final int count) {
        this.cp_Utf8_count = count;
    }
    
    public void setCp_Int_count(final int count) {
        this.cp_Int_count = count;
    }
    
    public void setCp_Float_count(final int count) {
        this.cp_Float_count = count;
    }
    
    public void setCp_Long_count(final int count) {
        this.cp_Long_count = count;
    }
    
    public void setCp_Double_count(final int count) {
        this.cp_Double_count = count;
    }
    
    public void setCp_String_count(final int count) {
        this.cp_String_count = count;
    }
    
    public void setCp_Class_count(final int count) {
        this.cp_Class_count = count;
    }
    
    public void setCp_Signature_count(final int count) {
        this.cp_Signature_count = count;
    }
    
    public void setCp_Descr_count(final int count) {
        this.cp_Descr_count = count;
    }
    
    public void setCp_Field_count(final int count) {
        this.cp_Field_count = count;
    }
    
    public void setCp_Method_count(final int count) {
        this.cp_Method_count = count;
    }
    
    public void setCp_Imethod_count(final int count) {
        this.cp_Imethod_count = count;
    }
    
    public void setAttribute_definition_count(final int attribute_definition_count) {
        this.attribute_definition_count = attribute_definition_count;
    }
    
    public void setHave_all_code_flags(final boolean have_all_code_flags) {
        this.have_all_code_flags = have_all_code_flags;
    }
    
    public int getArchive_modtime() {
        return this.archive_modtime;
    }
    
    public void setFile_count(final int file_count) {
        this.file_count = file_count;
    }
    
    public void setDeflate_hint(final boolean deflate_hint) {
        this.deflate_hint = deflate_hint;
    }
    
    public void setHave_class_flags_hi(final boolean have_class_flags_hi) {
        this.have_class_flags_hi = have_class_flags_hi;
    }
    
    public void setHave_field_flags_hi(final boolean have_field_flags_hi) {
        this.have_field_flags_hi = have_field_flags_hi;
    }
    
    public void setHave_method_flags_hi(final boolean have_method_flags_hi) {
        this.have_method_flags_hi = have_method_flags_hi;
    }
    
    public void setHave_code_flags_hi(final boolean have_code_flags_hi) {
        this.have_code_flags_hi = have_code_flags_hi;
    }
    
    public boolean have_class_flags_hi() {
        return this.have_class_flags_hi;
    }
    
    public boolean have_field_flags_hi() {
        return this.have_field_flags_hi;
    }
    
    public boolean have_method_flags_hi() {
        return this.have_method_flags_hi;
    }
    
    public boolean have_code_flags_hi() {
        return this.have_code_flags_hi;
    }
    
    public void setIc_count(final int ic_count) {
        this.ic_count = ic_count;
    }
    
    public void setClass_count(final int class_count) {
        this.class_count = class_count;
    }
    
    private void writeCpCounts(final OutputStream out) throws IOException, Pack200Exception {
        out.write(this.encodeScalar(this.cp_Utf8_count, Codec.UNSIGNED5));
        if ((this.archive_options & 0x2) != 0x0) {
            out.write(this.encodeScalar(this.cp_Int_count, Codec.UNSIGNED5));
            out.write(this.encodeScalar(this.cp_Float_count, Codec.UNSIGNED5));
            out.write(this.encodeScalar(this.cp_Long_count, Codec.UNSIGNED5));
            out.write(this.encodeScalar(this.cp_Double_count, Codec.UNSIGNED5));
        }
        out.write(this.encodeScalar(this.cp_String_count, Codec.UNSIGNED5));
        out.write(this.encodeScalar(this.cp_Class_count, Codec.UNSIGNED5));
        out.write(this.encodeScalar(this.cp_Signature_count, Codec.UNSIGNED5));
        out.write(this.encodeScalar(this.cp_Descr_count, Codec.UNSIGNED5));
        out.write(this.encodeScalar(this.cp_Field_count, Codec.UNSIGNED5));
        out.write(this.encodeScalar(this.cp_Method_count, Codec.UNSIGNED5));
        out.write(this.encodeScalar(this.cp_Imethod_count, Codec.UNSIGNED5));
    }
    
    private void writeClassCounts(final OutputStream out) throws IOException, Pack200Exception {
        final int default_class_minver = 0;
        final int default_class_majver = this.majverCounter.getMostCommon();
        out.write(this.encodeScalar(this.ic_count, Codec.UNSIGNED5));
        out.write(this.encodeScalar(0, Codec.UNSIGNED5));
        out.write(this.encodeScalar(default_class_majver, Codec.UNSIGNED5));
        out.write(this.encodeScalar(this.class_count, Codec.UNSIGNED5));
    }
    
    private void writeArchiveSpecialCounts(final OutputStream out) throws IOException, Pack200Exception {
        if ((this.archive_options & 0x1) > 0) {
            out.write(this.encodeScalar(this.band_headers.size(), Codec.UNSIGNED5));
            out.write(this.encodeScalar(this.attribute_definition_count, Codec.UNSIGNED5));
        }
    }
    
    private void writeArchiveFileCounts(final OutputStream out) throws IOException, Pack200Exception {
        if ((this.archive_options & 0x10) > 0) {
            out.write(this.encodeScalar(this.archive_size_hi, Codec.UNSIGNED5));
            out.write(this.encodeScalar(this.archive_size_lo, Codec.UNSIGNED5));
            out.write(this.encodeScalar(this.archive_next_count, Codec.UNSIGNED5));
            out.write(this.encodeScalar(this.archive_modtime, Codec.UNSIGNED5));
            out.write(this.encodeScalar(this.file_count, Codec.UNSIGNED5));
        }
    }
    
    public void addMajorVersion(final int major) {
        this.majverCounter.add(major);
    }
    
    public int getDefaultMajorVersion() {
        return this.majverCounter.getMostCommon();
    }
    
    public boolean have_file_size_hi() {
        return this.have_file_size_hi;
    }
    
    public boolean have_file_modtime() {
        return true;
    }
    
    public boolean have_file_options() {
        return true;
    }
    
    public boolean have_all_code_flags() {
        return this.have_all_code_flags;
    }
    
    public void appendBandCodingSpecifier(final int specifier) {
        this.band_headers.add(specifier);
    }
    
    static {
        magic = new int[] { 202, 254, 208, 13 };
    }
    
    private class Counter
    {
        private final int[] objs;
        private final int[] counts;
        private int length;
        
        private Counter() {
            this.objs = new int[8];
            this.counts = new int[8];
        }
        
        public void add(final int obj) {
            boolean found = false;
            for (int i = 0; i < this.length; ++i) {
                if (this.objs[i] == obj) {
                    final int[] counts = this.counts;
                    final int n = i;
                    ++counts[n];
                    found = true;
                }
            }
            if (!found) {
                this.objs[this.length] = obj;
                this.counts[this.length] = 1;
                ++this.length;
                if (this.length > this.objs.length - 1) {
                    final Object[] newArray = new Object[this.objs.length + 8];
                    System.arraycopy(this.objs, 0, newArray, 0, this.length);
                }
            }
        }
        
        public int getMostCommon() {
            int returnIndex = 0;
            for (int i = 0; i < this.length; ++i) {
                if (this.counts[i] > this.counts[returnIndex]) {
                    returnIndex = i;
                }
            }
            return this.objs[returnIndex];
        }
    }
}
