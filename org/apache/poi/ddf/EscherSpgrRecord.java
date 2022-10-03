package org.apache.poi.ddf;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.LittleEndian;

public class EscherSpgrRecord extends EscherRecord
{
    public static final short RECORD_ID;
    private int field_1_rectX1;
    private int field_2_rectY1;
    private int field_3_rectX2;
    private int field_4_rectY2;
    
    public EscherSpgrRecord() {
    }
    
    public EscherSpgrRecord(final EscherSpgrRecord other) {
        super(other);
        this.field_1_rectX1 = other.field_1_rectX1;
        this.field_2_rectY1 = other.field_2_rectY1;
        this.field_3_rectX2 = other.field_3_rectX2;
        this.field_4_rectY2 = other.field_4_rectY2;
    }
    
    @Override
    public int fillFields(final byte[] data, final int offset, final EscherRecordFactory recordFactory) {
        int bytesRemaining = this.readHeader(data, offset);
        final int pos = offset + 8;
        int size = 0;
        this.field_1_rectX1 = LittleEndian.getInt(data, pos + size);
        size += 4;
        this.field_2_rectY1 = LittleEndian.getInt(data, pos + size);
        size += 4;
        this.field_3_rectX2 = LittleEndian.getInt(data, pos + size);
        size += 4;
        this.field_4_rectY2 = LittleEndian.getInt(data, pos + size);
        size += 4;
        bytesRemaining -= size;
        if (bytesRemaining != 0) {
            throw new RecordFormatException("Expected no remaining bytes but got " + bytesRemaining);
        }
        return 8 + size + bytesRemaining;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data, final EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        LittleEndian.putShort(data, offset, this.getOptions());
        LittleEndian.putShort(data, offset + 2, this.getRecordId());
        final int remainingBytes = 16;
        LittleEndian.putInt(data, offset + 4, remainingBytes);
        LittleEndian.putInt(data, offset + 8, this.field_1_rectX1);
        LittleEndian.putInt(data, offset + 12, this.field_2_rectY1);
        LittleEndian.putInt(data, offset + 16, this.field_3_rectX2);
        LittleEndian.putInt(data, offset + 20, this.field_4_rectY2);
        listener.afterRecordSerialize(offset + this.getRecordSize(), this.getRecordId(), offset + this.getRecordSize(), this);
        return 24;
    }
    
    @Override
    public int getRecordSize() {
        return 24;
    }
    
    @Override
    public short getRecordId() {
        return EscherSpgrRecord.RECORD_ID;
    }
    
    @Override
    public String getRecordName() {
        return EscherRecordTypes.SPGR.recordName;
    }
    
    public int getRectX1() {
        return this.field_1_rectX1;
    }
    
    public void setRectX1(final int x1) {
        this.field_1_rectX1 = x1;
    }
    
    public int getRectY1() {
        return this.field_2_rectY1;
    }
    
    public void setRectY1(final int y1) {
        this.field_2_rectY1 = y1;
    }
    
    public int getRectX2() {
        return this.field_3_rectX2;
    }
    
    public void setRectX2(final int x2) {
        this.field_3_rectX2 = x2;
    }
    
    public int getRectY2() {
        return this.field_4_rectY2;
    }
    
    public void setRectY2(final int rectY2) {
        this.field_4_rectY2 = rectY2;
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "rectX1", this::getRectX1, "rectY1", this::getRectY1, "rectX2", this::getRectX2, "rectY2", this::getRectY2);
    }
    
    @Override
    public Enum getGenericRecordType() {
        return EscherRecordTypes.SPGR;
    }
    
    @Override
    public EscherSpgrRecord copy() {
        return new EscherSpgrRecord(this);
    }
    
    static {
        RECORD_ID = EscherRecordTypes.SPGR.typeID;
    }
}
