package org.apache.poi.ddf;

import java.util.List;
import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.util.GenericRecordXmlWriter;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.common.usermodel.GenericRecord;

public abstract class EscherProperty implements GenericRecord
{
    private final short id;
    static final int IS_BLIP = 16384;
    static final int IS_COMPLEX = 32768;
    private static final int[] FLAG_MASK;
    private static final String[] FLAG_NAMES;
    
    protected EscherProperty(final short id) {
        this.id = id;
    }
    
    protected EscherProperty(final short propertyNumber, final boolean isComplex, final boolean isBlipId) {
        this((short)(propertyNumber | (isComplex ? 32768 : 0) | (isBlipId ? 16384 : 0)));
    }
    
    protected EscherProperty(final EscherPropertyTypes type, final boolean isComplex, final boolean isBlipId) {
        this((short)(type.propNumber | (isComplex ? 32768 : 0) | (isBlipId ? 16384 : 0)));
    }
    
    public short getId() {
        return this.id;
    }
    
    public short getPropertyNumber() {
        return (short)(this.id & 0x3FFF);
    }
    
    public boolean isComplex() {
        return (this.id & 0x8000) != 0x0;
    }
    
    public boolean isBlipId() {
        return (this.id & 0x4000) != 0x0;
    }
    
    public String getName() {
        return EscherPropertyTypes.forPropertyID(this.getPropertyNumber()).propName;
    }
    
    public int getPropertySize() {
        return 6;
    }
    
    public abstract int serializeSimplePart(final byte[] p0, final int p1);
    
    public abstract int serializeComplexPart(final byte[] p0, final int p1);
    
    @Override
    public final String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }
    
    public final String toXml(final String tab) {
        return GenericRecordXmlWriter.marshal(this);
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("id", this::getId, "name", this::getName, "propertyNumber", this::getPropertyNumber, "propertySize", this::getPropertySize, "flags", GenericRecordUtil.getBitsAsString((Supplier<Number>)this::getId, EscherProperty.FLAG_MASK, EscherProperty.FLAG_NAMES));
    }
    
    @Override
    public List<? extends GenericRecord> getGenericChildren() {
        return null;
    }
    
    @Override
    public EscherPropertyTypes getGenericRecordType() {
        return EscherPropertyTypes.forPropertyID(this.id);
    }
    
    static {
        FLAG_MASK = new int[] { 16384, 32768 };
        FLAG_NAMES = new String[] { "IS_BLIP", "IS_COMPLEX" };
    }
}
