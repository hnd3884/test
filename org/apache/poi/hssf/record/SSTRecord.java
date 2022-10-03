package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.cont.ContinuableRecordOutput;
import java.util.Iterator;
import org.apache.poi.util.IntMapper;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.hssf.record.cont.ContinuableRecord;

public final class SSTRecord extends ContinuableRecord
{
    public static final short sid = 252;
    private static final UnicodeString EMPTY_STRING;
    private int field_1_num_strings;
    private int field_2_num_unique_strings;
    private IntMapper<UnicodeString> field_3_strings;
    private SSTDeserializer deserializer;
    private int[] bucketAbsoluteOffsets;
    private int[] bucketRelativeOffsets;
    
    public SSTRecord() {
        this.field_1_num_strings = 0;
        this.field_2_num_unique_strings = 0;
        this.field_3_strings = new IntMapper<UnicodeString>();
        this.deserializer = new SSTDeserializer(this.field_3_strings);
    }
    
    public SSTRecord(final SSTRecord other) {
        super(other);
        this.field_1_num_strings = other.field_1_num_strings;
        this.field_2_num_unique_strings = other.field_2_num_unique_strings;
        this.field_3_strings = other.field_3_strings.copy();
        this.deserializer = new SSTDeserializer(this.field_3_strings);
        this.bucketAbsoluteOffsets = (int[])((other.bucketAbsoluteOffsets == null) ? null : ((int[])other.bucketAbsoluteOffsets.clone()));
        this.bucketRelativeOffsets = (int[])((other.bucketRelativeOffsets == null) ? null : ((int[])other.bucketRelativeOffsets.clone()));
    }
    
    public int addString(final UnicodeString string) {
        ++this.field_1_num_strings;
        final UnicodeString ucs = (string == null) ? SSTRecord.EMPTY_STRING : string;
        final int index = this.field_3_strings.getIndex(ucs);
        int rval;
        if (index != -1) {
            rval = index;
        }
        else {
            rval = this.field_3_strings.size();
            ++this.field_2_num_unique_strings;
            SSTDeserializer.addToStringTable(this.field_3_strings, ucs);
        }
        return rval;
    }
    
    public int getNumStrings() {
        return this.field_1_num_strings;
    }
    
    public int getNumUniqueStrings() {
        return this.field_2_num_unique_strings;
    }
    
    public UnicodeString getString(final int id) {
        return this.field_3_strings.get(id);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[SST]\n");
        buffer.append("    .numstrings     = ").append(Integer.toHexString(this.getNumStrings())).append("\n");
        buffer.append("    .uniquestrings  = ").append(Integer.toHexString(this.getNumUniqueStrings())).append("\n");
        for (int k = 0; k < this.field_3_strings.size(); ++k) {
            final UnicodeString s = this.field_3_strings.get(k);
            buffer.append("    .string_").append(k).append("      = ").append(s.getDebugInfo()).append("\n");
        }
        buffer.append("[/SST]\n");
        return buffer.toString();
    }
    
    @Override
    public short getSid() {
        return 252;
    }
    
    public SSTRecord(final RecordInputStream in) {
        this.field_1_num_strings = in.readInt();
        this.field_2_num_unique_strings = in.readInt();
        this.field_3_strings = new IntMapper<UnicodeString>();
        this.deserializer = new SSTDeserializer(this.field_3_strings);
        if (this.field_1_num_strings == 0) {
            this.field_2_num_unique_strings = 0;
            return;
        }
        this.deserializer.manufactureStrings(this.field_2_num_unique_strings, in);
    }
    
    Iterator<UnicodeString> getStrings() {
        return this.field_3_strings.iterator();
    }
    
    int countStrings() {
        return this.field_3_strings.size();
    }
    
    @Override
    protected void serialize(final ContinuableRecordOutput out) {
        final SSTSerializer serializer = new SSTSerializer(this.field_3_strings, this.getNumStrings(), this.getNumUniqueStrings());
        serializer.serialize(out);
        this.bucketAbsoluteOffsets = serializer.getBucketAbsoluteOffsets();
        this.bucketRelativeOffsets = serializer.getBucketRelativeOffsets();
    }
    
    SSTDeserializer getDeserializer() {
        return this.deserializer;
    }
    
    public ExtSSTRecord createExtSSTRecord(final int sstOffset) {
        if (this.bucketAbsoluteOffsets == null || this.bucketRelativeOffsets == null) {
            throw new IllegalStateException("SST record has not yet been serialized.");
        }
        final ExtSSTRecord extSST = new ExtSSTRecord();
        extSST.setNumStringsPerBucket((short)8);
        final int[] absoluteOffsets = this.bucketAbsoluteOffsets.clone();
        final int[] relativeOffsets = this.bucketRelativeOffsets.clone();
        for (int i = 0; i < absoluteOffsets.length; ++i) {
            final int[] array = absoluteOffsets;
            final int n = i;
            array[n] += sstOffset;
        }
        extSST.setBucketOffsets(absoluteOffsets, relativeOffsets);
        return extSST;
    }
    
    public int calcExtSSTRecordSize() {
        return ExtSSTRecord.getRecordSizeForStrings(this.field_3_strings.size());
    }
    
    @Override
    public SSTRecord copy() {
        return new SSTRecord(this);
    }
    
    static {
        EMPTY_STRING = new UnicodeString("");
    }
}
