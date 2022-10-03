package org.apache.poi.ddf;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.IOUtils;

public class EscherClientDataRecord extends EscherRecord
{
    public static final short RECORD_ID;
    private static final int MAX_RECORD_LENGTH = 100000;
    private static final byte[] EMPTY;
    private byte[] remainingData;
    
    public EscherClientDataRecord() {
    }
    
    public EscherClientDataRecord(final EscherClientDataRecord other) {
        super(other);
        this.remainingData = (byte[])((other.remainingData == null) ? null : ((byte[])other.remainingData.clone()));
    }
    
    @Override
    public int fillFields(final byte[] data, final int offset, final EscherRecordFactory recordFactory) {
        final int bytesRemaining = this.readHeader(data, offset);
        final int pos = offset + 8;
        System.arraycopy(data, pos, this.remainingData = ((bytesRemaining == 0) ? EscherClientDataRecord.EMPTY : IOUtils.safelyAllocate(bytesRemaining, 100000)), 0, bytesRemaining);
        return 8 + bytesRemaining;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data, final EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        if (this.remainingData == null) {
            this.remainingData = EscherClientDataRecord.EMPTY;
        }
        LittleEndian.putShort(data, offset, this.getOptions());
        LittleEndian.putShort(data, offset + 2, this.getRecordId());
        LittleEndian.putInt(data, offset + 4, this.remainingData.length);
        System.arraycopy(this.remainingData, 0, data, offset + 8, this.remainingData.length);
        final int pos = offset + 8 + this.remainingData.length;
        listener.afterRecordSerialize(pos, this.getRecordId(), pos - offset, this);
        return pos - offset;
    }
    
    @Override
    public int getRecordSize() {
        return 8 + ((this.remainingData == null) ? 0 : this.remainingData.length);
    }
    
    @Override
    public short getRecordId() {
        return EscherClientDataRecord.RECORD_ID;
    }
    
    @Override
    public String getRecordName() {
        return EscherRecordTypes.CLIENT_DATA.recordName;
    }
    
    public byte[] getRemainingData() {
        return this.remainingData;
    }
    
    public void setRemainingData(final byte[] remainingData) {
        this.remainingData = ((remainingData == null) ? new byte[0] : remainingData.clone());
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "remainingData", this::getRemainingData);
    }
    
    @Override
    public Enum getGenericRecordType() {
        return EscherRecordTypes.CLIENT_DATA;
    }
    
    @Override
    public EscherClientDataRecord copy() {
        return new EscherClientDataRecord(this);
    }
    
    static {
        RECORD_ID = EscherRecordTypes.CLIENT_DATA.typeID;
        EMPTY = new byte[0];
    }
}
