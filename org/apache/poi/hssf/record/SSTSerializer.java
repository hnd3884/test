package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.cont.ContinuableRecordOutput;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.util.IntMapper;

final class SSTSerializer
{
    private final int _numStrings;
    private final int _numUniqueStrings;
    private final IntMapper<UnicodeString> strings;
    private final int[] bucketAbsoluteOffsets;
    private final int[] bucketRelativeOffsets;
    
    public SSTSerializer(final IntMapper<UnicodeString> strings, final int numStrings, final int numUniqueStrings) {
        this.strings = strings;
        this._numStrings = numStrings;
        this._numUniqueStrings = numUniqueStrings;
        final int infoRecs = ExtSSTRecord.getNumberOfInfoRecsForStrings(strings.size());
        this.bucketAbsoluteOffsets = new int[infoRecs];
        this.bucketRelativeOffsets = new int[infoRecs];
    }
    
    public void serialize(final ContinuableRecordOutput out) {
        out.writeInt(this._numStrings);
        out.writeInt(this._numUniqueStrings);
        for (int k = 0; k < this.strings.size(); ++k) {
            if (k % 8 == 0) {
                final int rOff = out.getTotalSize();
                final int index = k / 8;
                if (index < 128) {
                    this.bucketAbsoluteOffsets[index] = rOff;
                    this.bucketRelativeOffsets[index] = rOff;
                }
            }
            final UnicodeString s = this.getUnicodeString(k);
            s.serialize(out);
        }
    }
    
    private UnicodeString getUnicodeString(final int index) {
        return getUnicodeString(this.strings, index);
    }
    
    private static UnicodeString getUnicodeString(final IntMapper<UnicodeString> strings, final int index) {
        return strings.get(index);
    }
    
    public int[] getBucketAbsoluteOffsets() {
        return this.bucketAbsoluteOffsets;
    }
    
    public int[] getBucketRelativeOffsets() {
        return this.bucketRelativeOffsets;
    }
}
