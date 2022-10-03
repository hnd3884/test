package org.apache.poi.ddf;

import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import java.util.Arrays;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Removal;

public class EscherComplexProperty extends EscherProperty
{
    private static final int MAX_RECORD_LENGTH = 100000000;
    private byte[] complexData;
    
    @Deprecated
    @Removal(version = "5.0.0")
    public EscherComplexProperty(final short id, final byte[] complexData) {
        this(id, (complexData == null) ? 0 : complexData.length);
        this.setComplexData(complexData);
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public EscherComplexProperty(final short propertyNumber, final boolean isBlipId, final byte[] complexData) {
        this(propertyNumber, isBlipId, (complexData == null) ? 0 : complexData.length);
        this.setComplexData(complexData);
    }
    
    public EscherComplexProperty(final short id, final int complexSize) {
        super((short)(id | 0x8000));
        this.complexData = IOUtils.safelyAllocate(complexSize, 100000000);
    }
    
    public EscherComplexProperty(final short propertyNumber, final boolean isBlipId, final int complexSize) {
        this((short)(propertyNumber | (isBlipId ? 16384 : 0)), complexSize);
    }
    
    public EscherComplexProperty(final EscherPropertyTypes type, final boolean isBlipId, final int complexSize) {
        this((short)(type.propNumber | (isBlipId ? 16384 : 0)), complexSize);
    }
    
    @Override
    public int serializeSimplePart(final byte[] data, final int pos) {
        LittleEndian.putShort(data, pos, this.getId());
        LittleEndian.putInt(data, pos + 2, this.complexData.length);
        return 6;
    }
    
    @Override
    public int serializeComplexPart(final byte[] data, final int pos) {
        System.arraycopy(this.complexData, 0, data, pos, this.complexData.length);
        return this.complexData.length;
    }
    
    public byte[] getComplexData() {
        return this.complexData;
    }
    
    public int setComplexData(final byte[] complexData) {
        return this.setComplexData(complexData, 0);
    }
    
    public int setComplexData(final byte[] complexData, final int offset) {
        if (complexData == null) {
            return 0;
        }
        final int copySize = Math.max(0, Math.min(this.complexData.length, complexData.length - offset));
        System.arraycopy(complexData, offset, this.complexData, 0, copySize);
        return copySize;
    }
    
    protected void resizeComplexData(final int newSize) {
        this.resizeComplexData(newSize, Integer.MAX_VALUE);
    }
    
    protected void resizeComplexData(final int newSize, final int copyLen) {
        if (newSize == this.complexData.length) {
            return;
        }
        final byte[] newArray = IOUtils.safelyAllocate(newSize, 100000000);
        System.arraycopy(this.complexData, 0, newArray, 0, Math.min(Math.min(this.complexData.length, copyLen), newSize));
        this.complexData = newArray;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EscherComplexProperty)) {
            return false;
        }
        final EscherComplexProperty escherComplexProperty = (EscherComplexProperty)o;
        return Arrays.equals(this.complexData, escherComplexProperty.complexData);
    }
    
    @Override
    public int getPropertySize() {
        return 6 + this.complexData.length;
    }
    
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[] { this.complexData, this.getId() });
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "data", this::getComplexData);
    }
}
