package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.cont.ContinuableRecordOutput;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.poi.hssf.record.cont.ContinuableRecord;

public final class ExtSSTRecord extends ContinuableRecord
{
    public static final short sid = 255;
    public static final int DEFAULT_BUCKET_SIZE = 8;
    public static final int MAX_BUCKETS = 128;
    private short _stringsPerBucket;
    private InfoSubRecord[] _sstInfos;
    
    public ExtSSTRecord() {
        this._stringsPerBucket = 8;
        this._sstInfos = new InfoSubRecord[0];
    }
    
    public ExtSSTRecord(final ExtSSTRecord other) {
        this._stringsPerBucket = other._stringsPerBucket;
        this._sstInfos = (InfoSubRecord[])((other._sstInfos == null) ? null : ((InfoSubRecord[])Stream.of(other._sstInfos).map((Function<? super InfoSubRecord, ?>)InfoSubRecord::new).toArray(InfoSubRecord[]::new)));
    }
    
    public ExtSSTRecord(final RecordInputStream in) {
        this._stringsPerBucket = in.readShort();
        final int nInfos = in.remaining() / 8;
        final ArrayList<InfoSubRecord> lst = new ArrayList<InfoSubRecord>(nInfos);
        while (in.available() > 0) {
            final InfoSubRecord info = new InfoSubRecord(in);
            lst.add(info);
            if (in.available() == 0 && in.hasNextRecord() && in.getNextSid() == 60) {
                in.nextRecord();
            }
        }
        this._sstInfos = lst.toArray(new InfoSubRecord[0]);
    }
    
    public void setNumStringsPerBucket(final short numStrings) {
        this._stringsPerBucket = numStrings;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[EXTSST]\n");
        buffer.append("    .dsst           = ").append(Integer.toHexString(this._stringsPerBucket)).append("\n");
        buffer.append("    .numInfoRecords = ").append(this._sstInfos.length).append("\n");
        for (int k = 0; k < this._sstInfos.length; ++k) {
            buffer.append("    .inforecord     = ").append(k).append("\n");
            buffer.append("    .streampos      = ").append(Integer.toHexString(this._sstInfos[k].getStreamPos())).append("\n");
            buffer.append("    .sstoffset      = ").append(Integer.toHexString(this._sstInfos[k].getBucketSSTOffset())).append("\n");
        }
        buffer.append("[/EXTSST]\n");
        return buffer.toString();
    }
    
    public void serialize(final ContinuableRecordOutput out) {
        out.writeShort(this._stringsPerBucket);
        for (int k = 0; k < this._sstInfos.length; ++k) {
            this._sstInfos[k].serialize(out);
        }
    }
    
    protected int getDataSize() {
        return 2 + 8 * this._sstInfos.length;
    }
    
    protected InfoSubRecord[] getInfoSubRecords() {
        return this._sstInfos;
    }
    
    public static int getNumberOfInfoRecsForStrings(final int numStrings) {
        int infoRecs = numStrings / 8;
        if (numStrings % 8 != 0) {
            ++infoRecs;
        }
        if (infoRecs > 128) {
            infoRecs = 128;
        }
        return infoRecs;
    }
    
    public static int getRecordSizeForStrings(final int numStrings) {
        return 6 + getNumberOfInfoRecsForStrings(numStrings) * 8;
    }
    
    @Override
    public short getSid() {
        return 255;
    }
    
    public void setBucketOffsets(final int[] bucketAbsoluteOffsets, final int[] bucketRelativeOffsets) {
        this._sstInfos = new InfoSubRecord[bucketAbsoluteOffsets.length];
        for (int i = 0; i < bucketAbsoluteOffsets.length; ++i) {
            this._sstInfos[i] = new InfoSubRecord(bucketAbsoluteOffsets[i], bucketRelativeOffsets[i]);
        }
    }
    
    @Override
    public ExtSSTRecord copy() {
        return new ExtSSTRecord(this);
    }
    
    public static final class InfoSubRecord
    {
        public static final int ENCODED_SIZE = 8;
        private int field_1_stream_pos;
        private int field_2_bucket_sst_offset;
        private short field_3_zero;
        
        public InfoSubRecord(final int streamPos, final int bucketSstOffset) {
            this.field_1_stream_pos = streamPos;
            this.field_2_bucket_sst_offset = bucketSstOffset;
        }
        
        public InfoSubRecord(final InfoSubRecord other) {
            this.field_1_stream_pos = other.field_1_stream_pos;
            this.field_2_bucket_sst_offset = other.field_2_bucket_sst_offset;
            this.field_3_zero = other.field_3_zero;
        }
        
        public InfoSubRecord(final RecordInputStream in) {
            this.field_1_stream_pos = in.readInt();
            this.field_2_bucket_sst_offset = in.readShort();
            this.field_3_zero = in.readShort();
        }
        
        public int getStreamPos() {
            return this.field_1_stream_pos;
        }
        
        public int getBucketSSTOffset() {
            return this.field_2_bucket_sst_offset;
        }
        
        public void serialize(final LittleEndianOutput out) {
            out.writeInt(this.field_1_stream_pos);
            out.writeShort(this.field_2_bucket_sst_offset);
            out.writeShort(this.field_3_zero);
        }
    }
}
