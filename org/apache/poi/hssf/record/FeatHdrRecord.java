package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.hssf.record.common.FtrHeader;

public final class FeatHdrRecord extends StandardRecord
{
    public static final int SHAREDFEATURES_ISFPROTECTION = 2;
    public static final int SHAREDFEATURES_ISFFEC2 = 3;
    public static final int SHAREDFEATURES_ISFFACTOID = 4;
    public static final int SHAREDFEATURES_ISFLIST = 5;
    public static final short sid = 2151;
    private final FtrHeader futureHeader;
    private int isf_sharedFeatureType;
    private byte reserved;
    private long cbHdrData;
    private byte[] rgbHdrData;
    
    public FeatHdrRecord() {
        (this.futureHeader = new FtrHeader()).setRecordType((short)2151);
    }
    
    public FeatHdrRecord(final FeatHdrRecord other) {
        super(other);
        this.futureHeader = other.futureHeader.copy();
        this.isf_sharedFeatureType = other.isf_sharedFeatureType;
        this.reserved = other.reserved;
        this.cbHdrData = other.cbHdrData;
        this.rgbHdrData = (byte[])((other.rgbHdrData == null) ? null : ((byte[])other.rgbHdrData.clone()));
    }
    
    public FeatHdrRecord(final RecordInputStream in) {
        this.futureHeader = new FtrHeader(in);
        this.isf_sharedFeatureType = in.readShort();
        this.reserved = in.readByte();
        this.cbHdrData = in.readInt();
        this.rgbHdrData = in.readRemainder();
    }
    
    @Override
    public short getSid() {
        return 2151;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[FEATURE HEADER]\n");
        buffer.append("[/FEATURE HEADER]\n");
        return buffer.toString();
    }
    
    public void serialize(final LittleEndianOutput out) {
        this.futureHeader.serialize(out);
        out.writeShort(this.isf_sharedFeatureType);
        out.writeByte(this.reserved);
        out.writeInt((int)this.cbHdrData);
        out.write(this.rgbHdrData);
    }
    
    @Override
    protected int getDataSize() {
        return 19 + this.rgbHdrData.length;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public FeatHdrRecord clone() {
        return this.copy();
    }
    
    @Override
    public FeatHdrRecord copy() {
        return new FeatHdrRecord(this);
    }
}
