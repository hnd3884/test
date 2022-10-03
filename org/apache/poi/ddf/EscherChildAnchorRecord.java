package org.apache.poi.ddf;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.util.LittleEndian;

public class EscherChildAnchorRecord extends EscherRecord
{
    public static final short RECORD_ID;
    private int field_1_dx1;
    private int field_2_dy1;
    private int field_3_dx2;
    private int field_4_dy2;
    
    public EscherChildAnchorRecord() {
    }
    
    public EscherChildAnchorRecord(final EscherChildAnchorRecord other) {
        super(other);
        this.field_1_dx1 = other.field_1_dx1;
        this.field_2_dy1 = other.field_2_dy1;
        this.field_3_dx2 = other.field_3_dx2;
        this.field_4_dy2 = other.field_4_dy2;
    }
    
    @Override
    public int fillFields(final byte[] data, final int offset, final EscherRecordFactory recordFactory) {
        final int bytesRemaining = this.readHeader(data, offset);
        final int pos = offset + 8;
        int size = 0;
        switch (bytesRemaining) {
            case 16: {
                this.field_1_dx1 = LittleEndian.getInt(data, pos + size);
                size += 4;
                this.field_2_dy1 = LittleEndian.getInt(data, pos + size);
                size += 4;
                this.field_3_dx2 = LittleEndian.getInt(data, pos + size);
                size += 4;
                this.field_4_dy2 = LittleEndian.getInt(data, pos + size);
                size += 4;
                break;
            }
            case 8: {
                this.field_1_dx1 = LittleEndian.getShort(data, pos + size);
                size += 2;
                this.field_2_dy1 = LittleEndian.getShort(data, pos + size);
                size += 2;
                this.field_3_dx2 = LittleEndian.getShort(data, pos + size);
                size += 2;
                this.field_4_dy2 = LittleEndian.getShort(data, pos + size);
                size += 2;
                break;
            }
            default: {
                throw new RuntimeException("Invalid EscherChildAnchorRecord - neither 8 nor 16 bytes.");
            }
        }
        return 8 + size;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data, final EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        int pos = offset;
        LittleEndian.putShort(data, pos, this.getOptions());
        pos += 2;
        LittleEndian.putShort(data, pos, this.getRecordId());
        pos += 2;
        LittleEndian.putInt(data, pos, this.getRecordSize() - 8);
        pos += 4;
        LittleEndian.putInt(data, pos, this.field_1_dx1);
        pos += 4;
        LittleEndian.putInt(data, pos, this.field_2_dy1);
        pos += 4;
        LittleEndian.putInt(data, pos, this.field_3_dx2);
        pos += 4;
        LittleEndian.putInt(data, pos, this.field_4_dy2);
        pos += 4;
        listener.afterRecordSerialize(pos, this.getRecordId(), pos - offset, this);
        return pos - offset;
    }
    
    @Override
    public int getRecordSize() {
        return 24;
    }
    
    @Override
    public short getRecordId() {
        return EscherChildAnchorRecord.RECORD_ID;
    }
    
    @Override
    public String getRecordName() {
        return EscherRecordTypes.CHILD_ANCHOR.recordName;
    }
    
    public int getDx1() {
        return this.field_1_dx1;
    }
    
    public void setDx1(final int field_1_dx1) {
        this.field_1_dx1 = field_1_dx1;
    }
    
    public int getDy1() {
        return this.field_2_dy1;
    }
    
    public void setDy1(final int field_2_dy1) {
        this.field_2_dy1 = field_2_dy1;
    }
    
    public int getDx2() {
        return this.field_3_dx2;
    }
    
    public void setDx2(final int field_3_dx2) {
        this.field_3_dx2 = field_3_dx2;
    }
    
    public int getDy2() {
        return this.field_4_dy2;
    }
    
    public void setDy2(final int field_4_dy2) {
        this.field_4_dy2 = field_4_dy2;
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "x1", this::getDx1, "y1", this::getDy1, "x2", this::getDx2, "y2", this::getDy2);
    }
    
    @Override
    public Enum getGenericRecordType() {
        return EscherRecordTypes.CHILD_ANCHOR;
    }
    
    @Override
    public EscherChildAnchorRecord copy() {
        return new EscherChildAnchorRecord(this);
    }
    
    static {
        RECORD_ID = EscherRecordTypes.CHILD_ANCHOR.typeID;
    }
}
