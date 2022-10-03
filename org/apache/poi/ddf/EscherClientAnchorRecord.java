package org.apache.poi.ddf;

import org.apache.poi.common.Duplicatable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public class EscherClientAnchorRecord extends EscherRecord
{
    private static final int MAX_RECORD_LENGTH = 100000;
    public static final short RECORD_ID;
    private short field_1_flag;
    private short field_2_col1;
    private short field_3_dx1;
    private short field_4_row1;
    private short field_5_dy1;
    private short field_6_col2;
    private short field_7_dx2;
    private short field_8_row2;
    private short field_9_dy2;
    private byte[] remainingData;
    private boolean shortRecord;
    
    public EscherClientAnchorRecord() {
        this.remainingData = new byte[0];
    }
    
    public EscherClientAnchorRecord(final EscherClientAnchorRecord other) {
        super(other);
        this.remainingData = new byte[0];
        this.field_1_flag = other.field_1_flag;
        this.field_2_col1 = other.field_2_col1;
        this.field_3_dx1 = other.field_3_dx1;
        this.field_4_row1 = other.field_4_row1;
        this.field_5_dy1 = other.field_5_dy1;
        this.field_6_col2 = other.field_6_col2;
        this.field_7_dx2 = other.field_7_dx2;
        this.field_8_row2 = other.field_8_row2;
        this.field_9_dy2 = other.field_9_dy2;
        this.remainingData = (byte[])((other.remainingData == null) ? null : ((byte[])other.remainingData.clone()));
        this.shortRecord = other.shortRecord;
    }
    
    @Override
    public int fillFields(final byte[] data, final int offset, final EscherRecordFactory recordFactory) {
        int bytesRemaining = this.readHeader(data, offset);
        final int pos = offset + 8;
        int size = 0;
        if (bytesRemaining != 4) {
            this.field_1_flag = LittleEndian.getShort(data, pos + size);
            size += 2;
            this.field_2_col1 = LittleEndian.getShort(data, pos + size);
            size += 2;
            this.field_3_dx1 = LittleEndian.getShort(data, pos + size);
            size += 2;
            this.field_4_row1 = LittleEndian.getShort(data, pos + size);
            size += 2;
            if (bytesRemaining >= 18) {
                this.field_5_dy1 = LittleEndian.getShort(data, pos + size);
                size += 2;
                this.field_6_col2 = LittleEndian.getShort(data, pos + size);
                size += 2;
                this.field_7_dx2 = LittleEndian.getShort(data, pos + size);
                size += 2;
                this.field_8_row2 = LittleEndian.getShort(data, pos + size);
                size += 2;
                this.field_9_dy2 = LittleEndian.getShort(data, pos + size);
                size += 2;
                this.shortRecord = false;
            }
            else {
                this.shortRecord = true;
            }
        }
        bytesRemaining -= size;
        System.arraycopy(data, pos + size, this.remainingData = IOUtils.safelyAllocate(bytesRemaining, 100000), 0, bytesRemaining);
        return 8 + size + bytesRemaining;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data, final EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        if (this.remainingData == null) {
            this.remainingData = new byte[0];
        }
        LittleEndian.putShort(data, offset, this.getOptions());
        LittleEndian.putShort(data, offset + 2, this.getRecordId());
        final int remainingBytes = this.remainingData.length + (this.shortRecord ? 8 : 18);
        LittleEndian.putInt(data, offset + 4, remainingBytes);
        LittleEndian.putShort(data, offset + 8, this.field_1_flag);
        LittleEndian.putShort(data, offset + 10, this.field_2_col1);
        LittleEndian.putShort(data, offset + 12, this.field_3_dx1);
        LittleEndian.putShort(data, offset + 14, this.field_4_row1);
        if (!this.shortRecord) {
            LittleEndian.putShort(data, offset + 16, this.field_5_dy1);
            LittleEndian.putShort(data, offset + 18, this.field_6_col2);
            LittleEndian.putShort(data, offset + 20, this.field_7_dx2);
            LittleEndian.putShort(data, offset + 22, this.field_8_row2);
            LittleEndian.putShort(data, offset + 24, this.field_9_dy2);
        }
        System.arraycopy(this.remainingData, 0, data, offset + (this.shortRecord ? 16 : 26), this.remainingData.length);
        final int pos = offset + 8 + (this.shortRecord ? 8 : 18) + this.remainingData.length;
        listener.afterRecordSerialize(pos, this.getRecordId(), pos - offset, this);
        return pos - offset;
    }
    
    @Override
    public int getRecordSize() {
        return 8 + (this.shortRecord ? 8 : 18) + ((this.remainingData == null) ? 0 : this.remainingData.length);
    }
    
    @Override
    public short getRecordId() {
        return EscherClientAnchorRecord.RECORD_ID;
    }
    
    @Override
    public String getRecordName() {
        return EscherRecordTypes.CLIENT_ANCHOR.recordName;
    }
    
    public short getFlag() {
        return this.field_1_flag;
    }
    
    public void setFlag(final short field_1_flag) {
        this.field_1_flag = field_1_flag;
    }
    
    public short getCol1() {
        return this.field_2_col1;
    }
    
    public void setCol1(final short field_2_col1) {
        this.field_2_col1 = field_2_col1;
    }
    
    public short getDx1() {
        return this.field_3_dx1;
    }
    
    public void setDx1(final short field_3_dx1) {
        this.field_3_dx1 = field_3_dx1;
    }
    
    public short getRow1() {
        return this.field_4_row1;
    }
    
    public void setRow1(final short field_4_row1) {
        this.field_4_row1 = field_4_row1;
    }
    
    public short getDy1() {
        return this.field_5_dy1;
    }
    
    public void setDy1(final short field_5_dy1) {
        this.shortRecord = false;
        this.field_5_dy1 = field_5_dy1;
    }
    
    public short getCol2() {
        return this.field_6_col2;
    }
    
    public void setCol2(final short field_6_col2) {
        this.shortRecord = false;
        this.field_6_col2 = field_6_col2;
    }
    
    public short getDx2() {
        return this.field_7_dx2;
    }
    
    public void setDx2(final short field_7_dx2) {
        this.shortRecord = false;
        this.field_7_dx2 = field_7_dx2;
    }
    
    public short getRow2() {
        return this.field_8_row2;
    }
    
    public void setRow2(final short field_8_row2) {
        this.shortRecord = false;
        this.field_8_row2 = field_8_row2;
    }
    
    public short getDy2() {
        return this.field_9_dy2;
    }
    
    public void setDy2(final short field_9_dy2) {
        this.shortRecord = false;
        this.field_9_dy2 = field_9_dy2;
    }
    
    public byte[] getRemainingData() {
        return this.remainingData;
    }
    
    public void setRemainingData(final byte[] remainingData) {
        if (remainingData == null) {
            this.remainingData = new byte[0];
        }
        else {
            this.remainingData = remainingData.clone();
        }
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        final Map<String, Supplier<?>> m = new LinkedHashMap<String, Supplier<?>>(super.getGenericProperties());
        m.put("flag", this::getFlag);
        m.put("col1", this::getCol1);
        m.put("dx1", this::getDx1);
        m.put("row1", this::getRow1);
        m.put("dy1", this::getDy1);
        m.put("col2", this::getCol2);
        m.put("dx2", this::getDx2);
        m.put("row2", this::getRow2);
        m.put("dy2", this::getDy2);
        m.put("remainingData", this::getRemainingData);
        return Collections.unmodifiableMap((Map<? extends String, ? extends Supplier<?>>)m);
    }
    
    @Override
    public Enum getGenericRecordType() {
        return EscherRecordTypes.CLIENT_ANCHOR;
    }
    
    @Override
    public EscherClientAnchorRecord copy() {
        return new EscherClientAnchorRecord(this);
    }
    
    static {
        RECORD_ID = EscherRecordTypes.CLIENT_ANCHOR.typeID;
    }
}
