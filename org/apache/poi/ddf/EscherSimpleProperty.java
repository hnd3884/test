package org.apache.poi.ddf;

import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.util.LittleEndian;

public class EscherSimpleProperty extends EscherProperty
{
    private int propertyValue;
    
    public EscherSimpleProperty(final short id, final int propertyValue) {
        super(id);
        this.propertyValue = propertyValue;
    }
    
    public EscherSimpleProperty(final EscherPropertyTypes type, final int propertyValue) {
        this(type, false, false, propertyValue);
    }
    
    public EscherSimpleProperty(final short propertyNumber, final boolean isComplex, final boolean isBlipId, final int propertyValue) {
        super(propertyNumber, isComplex, isBlipId);
        this.propertyValue = propertyValue;
    }
    
    public EscherSimpleProperty(final EscherPropertyTypes type, final boolean isComplex, final boolean isBlipId, final int propertyValue) {
        super(type, isComplex, isBlipId);
        this.propertyValue = propertyValue;
    }
    
    @Override
    public int serializeSimplePart(final byte[] data, final int offset) {
        LittleEndian.putShort(data, offset, this.getId());
        LittleEndian.putInt(data, offset + 2, this.propertyValue);
        return 6;
    }
    
    @Override
    public int serializeComplexPart(final byte[] data, final int pos) {
        return 0;
    }
    
    public int getPropertyValue() {
        return this.propertyValue;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EscherSimpleProperty)) {
            return false;
        }
        final EscherSimpleProperty escherSimpleProperty = (EscherSimpleProperty)o;
        return this.propertyValue == escherSimpleProperty.propertyValue && this.getId() == escherSimpleProperty.getId();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.propertyValue, this.getId());
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "value", this::getPropertyValue);
    }
}
