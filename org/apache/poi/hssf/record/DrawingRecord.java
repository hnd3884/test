package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import org.apache.poi.util.LittleEndianOutput;

public final class DrawingRecord extends StandardRecord
{
    public static final short sid = 236;
    private static final byte[] EMPTY_BYTE_ARRAY;
    private byte[] recordData;
    private byte[] contd;
    
    public DrawingRecord() {
        this.recordData = DrawingRecord.EMPTY_BYTE_ARRAY;
    }
    
    public DrawingRecord(final DrawingRecord other) {
        super(other);
        this.recordData = (byte[])((other.recordData == null) ? null : ((byte[])other.recordData.clone()));
        this.contd = (byte[])((other.contd == null) ? null : ((byte[])other.contd.clone()));
    }
    
    public DrawingRecord(final RecordInputStream in) {
        this.recordData = in.readRemainder();
    }
    
    public DrawingRecord(final byte[] data) {
        this.recordData = data.clone();
    }
    
    @Deprecated
    void processContinueRecord(final byte[] record) {
        this.contd = record;
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.write(this.recordData);
    }
    
    @Override
    protected int getDataSize() {
        return this.recordData.length;
    }
    
    @Override
    public short getSid() {
        return 236;
    }
    
    public byte[] getRecordData() {
        return this.recordData;
    }
    
    public void setData(final byte[] thedata) {
        if (thedata == null) {
            throw new IllegalArgumentException("data must not be null");
        }
        this.recordData = thedata;
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public DrawingRecord clone() {
        return this.copy();
    }
    
    @Override
    public DrawingRecord copy() {
        return new DrawingRecord(this);
    }
    
    @Override
    public String toString() {
        return "DrawingRecord[" + this.recordData.length + "]";
    }
    
    static {
        EMPTY_BYTE_ARRAY = new byte[0];
    }
}
