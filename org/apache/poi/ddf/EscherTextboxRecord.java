package org.apache.poi.ddf;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.IOUtils;

public final class EscherTextboxRecord extends EscherRecord
{
    private static final int MAX_RECORD_LENGTH = 100000;
    public static final short RECORD_ID;
    private static final byte[] NO_BYTES;
    private byte[] thedata;
    
    public EscherTextboxRecord() {
        this.thedata = EscherTextboxRecord.NO_BYTES;
    }
    
    public EscherTextboxRecord(final EscherTextboxRecord other) {
        super(other);
        this.thedata = EscherTextboxRecord.NO_BYTES;
        this.thedata = ((other.thedata == null) ? EscherTextboxRecord.NO_BYTES : other.thedata.clone());
    }
    
    @Override
    public int fillFields(final byte[] data, final int offset, final EscherRecordFactory recordFactory) {
        final int bytesRemaining = this.readHeader(data, offset);
        System.arraycopy(data, offset + 8, this.thedata = IOUtils.safelyAllocate(bytesRemaining, 100000), 0, bytesRemaining);
        return bytesRemaining + 8;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data, final EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        LittleEndian.putShort(data, offset, this.getOptions());
        LittleEndian.putShort(data, offset + 2, this.getRecordId());
        final int remainingBytes = this.thedata.length;
        LittleEndian.putInt(data, offset + 4, remainingBytes);
        System.arraycopy(this.thedata, 0, data, offset + 8, this.thedata.length);
        final int pos = offset + 8 + this.thedata.length;
        listener.afterRecordSerialize(pos, this.getRecordId(), pos - offset, this);
        final int size = pos - offset;
        if (size != this.getRecordSize()) {
            throw new RecordFormatException(size + " bytes written but getRecordSize() reports " + this.getRecordSize());
        }
        return size;
    }
    
    public byte[] getData() {
        return this.thedata;
    }
    
    public void setData(final byte[] b, final int start, final int length) {
        System.arraycopy(b, start, this.thedata = IOUtils.safelyAllocate(length, 100000), 0, length);
    }
    
    public void setData(final byte[] b) {
        this.setData(b, 0, b.length);
    }
    
    @Override
    public int getRecordSize() {
        return 8 + this.thedata.length;
    }
    
    @Override
    public String getRecordName() {
        return EscherRecordTypes.CLIENT_TEXTBOX.recordName;
    }
    
    @Override
    public Enum getGenericRecordType() {
        return EscherRecordTypes.CLIENT_TEXTBOX;
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "isContainer", this::isContainerRecord, "extraData", this::getData);
    }
    
    @Override
    public EscherTextboxRecord copy() {
        return new EscherTextboxRecord(this);
    }
    
    static {
        RECORD_ID = EscherRecordTypes.CLIENT_TEXTBOX.typeID;
        NO_BYTES = new byte[0];
    }
}
