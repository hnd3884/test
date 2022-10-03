package org.apache.poi.ddf;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.util.LittleEndian;

public class EscherSpRecord extends EscherRecord
{
    public static final short RECORD_ID;
    public static final int FLAG_GROUP = 1;
    public static final int FLAG_CHILD = 2;
    public static final int FLAG_PATRIARCH = 4;
    public static final int FLAG_DELETED = 8;
    public static final int FLAG_OLESHAPE = 16;
    public static final int FLAG_HAVEMASTER = 32;
    public static final int FLAG_FLIPHORIZ = 64;
    public static final int FLAG_FLIPVERT = 128;
    public static final int FLAG_CONNECTOR = 256;
    public static final int FLAG_HAVEANCHOR = 512;
    public static final int FLAG_BACKGROUND = 1024;
    public static final int FLAG_HASSHAPETYPE = 2048;
    private static final int[] FLAGS_MASKS;
    private static final String[] FLAGS_NAMES;
    private int field_1_shapeId;
    private int field_2_flags;
    
    public EscherSpRecord() {
    }
    
    public EscherSpRecord(final EscherSpRecord other) {
        super(other);
        this.field_1_shapeId = other.field_1_shapeId;
        this.field_2_flags = other.field_2_flags;
    }
    
    @Override
    public int fillFields(final byte[] data, final int offset, final EscherRecordFactory recordFactory) {
        this.readHeader(data, offset);
        final int pos = offset + 8;
        int size = 0;
        this.field_1_shapeId = LittleEndian.getInt(data, pos + size);
        size += 4;
        this.field_2_flags = LittleEndian.getInt(data, pos + size);
        size += 4;
        return this.getRecordSize();
    }
    
    @Override
    public int serialize(final int offset, final byte[] data, final EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        LittleEndian.putShort(data, offset, this.getOptions());
        LittleEndian.putShort(data, offset + 2, this.getRecordId());
        final int remainingBytes = 8;
        LittleEndian.putInt(data, offset + 4, remainingBytes);
        LittleEndian.putInt(data, offset + 8, this.field_1_shapeId);
        LittleEndian.putInt(data, offset + 12, this.field_2_flags);
        listener.afterRecordSerialize(offset + this.getRecordSize(), this.getRecordId(), this.getRecordSize(), this);
        return 16;
    }
    
    @Override
    public int getRecordSize() {
        return 16;
    }
    
    @Override
    public short getRecordId() {
        return EscherSpRecord.RECORD_ID;
    }
    
    @Override
    public String getRecordName() {
        return EscherRecordTypes.SP.recordName;
    }
    
    private String decodeFlags(final int flags) {
        final StringBuilder result = new StringBuilder();
        result.append(((flags & 0x1) != 0x0) ? "|GROUP" : "");
        result.append(((flags & 0x2) != 0x0) ? "|CHILD" : "");
        result.append(((flags & 0x4) != 0x0) ? "|PATRIARCH" : "");
        result.append(((flags & 0x8) != 0x0) ? "|DELETED" : "");
        result.append(((flags & 0x10) != 0x0) ? "|OLESHAPE" : "");
        result.append(((flags & 0x20) != 0x0) ? "|HAVEMASTER" : "");
        result.append(((flags & 0x40) != 0x0) ? "|FLIPHORIZ" : "");
        result.append(((flags & 0x80) != 0x0) ? "|FLIPVERT" : "");
        result.append(((flags & 0x100) != 0x0) ? "|CONNECTOR" : "");
        result.append(((flags & 0x200) != 0x0) ? "|HAVEANCHOR" : "");
        result.append(((flags & 0x400) != 0x0) ? "|BACKGROUND" : "");
        result.append(((flags & 0x800) != 0x0) ? "|HASSHAPETYPE" : "");
        if (result.length() > 0) {
            result.deleteCharAt(0);
        }
        return result.toString();
    }
    
    public int getShapeId() {
        return this.field_1_shapeId;
    }
    
    public void setShapeId(final int field_1_shapeId) {
        this.field_1_shapeId = field_1_shapeId;
    }
    
    public int getFlags() {
        return this.field_2_flags;
    }
    
    public void setFlags(final int field_2_flags) {
        this.field_2_flags = field_2_flags;
    }
    
    public short getShapeType() {
        return this.getInstance();
    }
    
    public void setShapeType(final short value) {
        this.setInstance(value);
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "shapeType", this::getShapeType, "shapeId", this::getShapeId, "flags", GenericRecordUtil.getBitsAsString((Supplier<Number>)this::getFlags, EscherSpRecord.FLAGS_MASKS, EscherSpRecord.FLAGS_NAMES));
    }
    
    @Override
    public Enum getGenericRecordType() {
        return EscherRecordTypes.SP;
    }
    
    @Override
    public EscherSpRecord copy() {
        return new EscherSpRecord(this);
    }
    
    static {
        RECORD_ID = EscherRecordTypes.SP.typeID;
        FLAGS_MASKS = new int[] { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048 };
        FLAGS_NAMES = new String[] { "GROUP", "CHILD", "PATRIARCH", "DELETED", "OLESHAPE", "HAVEMASTER", "FLIPHORIZ", "FLIPVERT", "CONNECTOR", "HAVEANCHOR", "BACKGROUND", "HASSHAPETYPE" };
    }
}
