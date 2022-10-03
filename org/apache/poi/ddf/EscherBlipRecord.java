package org.apache.poi.ddf;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.IOUtils;

public class EscherBlipRecord extends EscherRecord
{
    private static final int MAX_RECORD_LENGTH = 104857600;
    public static final short RECORD_ID_START;
    public static final short RECORD_ID_END;
    private static final int HEADER_SIZE = 8;
    private byte[] field_pictureData;
    
    public EscherBlipRecord() {
    }
    
    public EscherBlipRecord(final EscherBlipRecord other) {
        super(other);
        this.field_pictureData = (byte[])((other.field_pictureData == null) ? null : ((byte[])other.field_pictureData.clone()));
    }
    
    @Override
    public int fillFields(final byte[] data, final int offset, final EscherRecordFactory recordFactory) {
        final int bytesAfterHeader = this.readHeader(data, offset);
        final int pos = offset + 8;
        System.arraycopy(data, pos, this.field_pictureData = IOUtils.safelyAllocate(bytesAfterHeader, 104857600), 0, bytesAfterHeader);
        return bytesAfterHeader + 8;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data, final EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        LittleEndian.putShort(data, offset, this.getOptions());
        LittleEndian.putShort(data, offset + 2, this.getRecordId());
        System.arraycopy(this.field_pictureData, 0, data, offset + 4, this.field_pictureData.length);
        listener.afterRecordSerialize(offset + 4 + this.field_pictureData.length, this.getRecordId(), this.field_pictureData.length + 4, this);
        return this.field_pictureData.length + 4;
    }
    
    @Override
    public int getRecordSize() {
        return this.field_pictureData.length + 8;
    }
    
    @Override
    public String getRecordName() {
        final EscherRecordTypes t = EscherRecordTypes.forTypeID(this.getRecordId());
        return ((t != EscherRecordTypes.UNKNOWN) ? t : EscherRecordTypes.BLIP_START).recordName;
    }
    
    public byte[] getPicturedata() {
        return this.field_pictureData;
    }
    
    public void setPictureData(final byte[] pictureData) {
        this.setPictureData(pictureData, 0, (pictureData == null) ? 0 : pictureData.length);
    }
    
    public void setPictureData(final byte[] pictureData, final int offset, final int length) {
        if (pictureData == null || offset < 0 || length < 0 || pictureData.length < offset + length) {
            throw new IllegalArgumentException("picture data can't be null");
        }
        System.arraycopy(pictureData, offset, this.field_pictureData = IOUtils.safelyAllocate(length, 104857600), 0, length);
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "pictureData", this::getPicturedata);
    }
    
    @Override
    public Enum getGenericRecordType() {
        final EscherRecordTypes t = EscherRecordTypes.forTypeID(this.getRecordId());
        return (t != EscherRecordTypes.UNKNOWN) ? t : EscherRecordTypes.BLIP_START;
    }
    
    @Override
    public EscherBlipRecord copy() {
        return new EscherBlipRecord(this);
    }
    
    static {
        RECORD_ID_START = EscherRecordTypes.BLIP_START.typeID;
        RECORD_ID_END = EscherRecordTypes.BLIP_END.typeID;
    }
}
