package org.apache.poi.ddf;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.util.LittleEndian;

public class EscherBitmapBlip extends EscherBlipRecord
{
    public static final short RECORD_ID_JPEG;
    public static final short RECORD_ID_PNG;
    public static final short RECORD_ID_DIB;
    private static final int HEADER_SIZE = 8;
    private final byte[] field_1_UID;
    private byte field_2_marker;
    
    public EscherBitmapBlip() {
        this.field_1_UID = new byte[16];
        this.field_2_marker = -1;
    }
    
    public EscherBitmapBlip(final EscherBitmapBlip other) {
        super(other);
        this.field_1_UID = new byte[16];
        this.field_2_marker = -1;
        System.arraycopy(other.field_1_UID, 0, this.field_1_UID, 0, this.field_1_UID.length);
        this.field_2_marker = other.field_2_marker;
    }
    
    @Override
    public int fillFields(final byte[] data, final int offset, final EscherRecordFactory recordFactory) {
        final int bytesAfterHeader = this.readHeader(data, offset);
        int pos = offset + 8;
        System.arraycopy(data, pos, this.field_1_UID, 0, 16);
        pos += 16;
        this.field_2_marker = data[pos];
        ++pos;
        this.setPictureData(data, pos, bytesAfterHeader - 17);
        return bytesAfterHeader + 8;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data, final EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        LittleEndian.putShort(data, offset, this.getOptions());
        LittleEndian.putShort(data, offset + 2, this.getRecordId());
        LittleEndian.putInt(data, offset + 4, this.getRecordSize() - 8);
        final int pos = offset + 8;
        System.arraycopy(this.field_1_UID, 0, data, pos, 16);
        data[pos + 16] = this.field_2_marker;
        final byte[] pd = this.getPicturedata();
        System.arraycopy(pd, 0, data, pos + 17, pd.length);
        listener.afterRecordSerialize(offset + this.getRecordSize(), this.getRecordId(), this.getRecordSize(), this);
        return 25 + pd.length;
    }
    
    @Override
    public int getRecordSize() {
        return 25 + this.getPicturedata().length;
    }
    
    public byte[] getUID() {
        return this.field_1_UID;
    }
    
    public void setUID(final byte[] field_1_UID) {
        if (field_1_UID == null || field_1_UID.length != 16) {
            throw new IllegalArgumentException("field_1_UID must be byte[16]");
        }
        System.arraycopy(field_1_UID, 0, this.field_1_UID, 0, 16);
    }
    
    public byte getMarker() {
        return this.field_2_marker;
    }
    
    public void setMarker(final byte field_2_marker) {
        this.field_2_marker = field_2_marker;
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "uid", this::getUID, "marker", this::getMarker);
    }
    
    @Override
    public EscherBitmapBlip copy() {
        return new EscherBitmapBlip(this);
    }
    
    static {
        RECORD_ID_JPEG = EscherRecordTypes.BLIP_JPEG.typeID;
        RECORD_ID_PNG = EscherRecordTypes.BLIP_PNG.typeID;
        RECORD_ID_DIB = EscherRecordTypes.BLIP_DIB.typeID;
    }
}
