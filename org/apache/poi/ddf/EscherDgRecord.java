package org.apache.poi.ddf;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.util.LittleEndian;

public class EscherDgRecord extends EscherRecord
{
    public static final short RECORD_ID;
    private int field_1_numShapes;
    private int field_2_lastMSOSPID;
    
    public EscherDgRecord() {
    }
    
    public EscherDgRecord(final EscherDgRecord other) {
        super(other);
        this.field_1_numShapes = other.field_1_numShapes;
        this.field_2_lastMSOSPID = other.field_2_lastMSOSPID;
    }
    
    @Override
    public int fillFields(final byte[] data, final int offset, final EscherRecordFactory recordFactory) {
        this.readHeader(data, offset);
        final int pos = offset + 8;
        int size = 0;
        this.field_1_numShapes = LittleEndian.getInt(data, pos + size);
        size += 4;
        this.field_2_lastMSOSPID = LittleEndian.getInt(data, pos + size);
        size += 4;
        return this.getRecordSize();
    }
    
    @Override
    public int serialize(final int offset, final byte[] data, final EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        LittleEndian.putShort(data, offset, this.getOptions());
        LittleEndian.putShort(data, offset + 2, this.getRecordId());
        LittleEndian.putInt(data, offset + 4, 8);
        LittleEndian.putInt(data, offset + 8, this.field_1_numShapes);
        LittleEndian.putInt(data, offset + 12, this.field_2_lastMSOSPID);
        listener.afterRecordSerialize(offset + 16, this.getRecordId(), this.getRecordSize(), this);
        return this.getRecordSize();
    }
    
    @Override
    public int getRecordSize() {
        return 16;
    }
    
    @Override
    public short getRecordId() {
        return EscherDgRecord.RECORD_ID;
    }
    
    @Override
    public String getRecordName() {
        return EscherRecordTypes.DG.recordName;
    }
    
    public int getNumShapes() {
        return this.field_1_numShapes;
    }
    
    public void setNumShapes(final int field_1_numShapes) {
        this.field_1_numShapes = field_1_numShapes;
    }
    
    public int getLastMSOSPID() {
        return this.field_2_lastMSOSPID;
    }
    
    public void setLastMSOSPID(final int field_2_lastMSOSPID) {
        this.field_2_lastMSOSPID = field_2_lastMSOSPID;
    }
    
    public short getDrawingGroupId() {
        return (short)(this.getOptions() >> 4);
    }
    
    public void incrementShapeCount() {
        ++this.field_1_numShapes;
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "numShapes", this::getNumShapes, "lastMSOSPID", this::getLastMSOSPID, "drawingGroupId", this::getDrawingGroupId);
    }
    
    @Override
    public Enum getGenericRecordType() {
        return EscherRecordTypes.DG;
    }
    
    @Override
    public EscherDgRecord copy() {
        return new EscherDgRecord(this);
    }
    
    static {
        RECORD_ID = EscherRecordTypes.DG.typeID;
    }
}
