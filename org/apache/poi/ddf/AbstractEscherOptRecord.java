package org.apache.poi.ddf;

import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.util.Removal;
import java.util.Comparator;
import org.apache.poi.util.LittleEndian;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEscherOptRecord extends EscherRecord
{
    private final List<EscherProperty> properties;
    
    protected AbstractEscherOptRecord() {
        this.properties = new ArrayList<EscherProperty>();
    }
    
    protected AbstractEscherOptRecord(final AbstractEscherOptRecord other) {
        super(other);
        (this.properties = new ArrayList<EscherProperty>()).addAll(other.properties);
    }
    
    public void addEscherProperty(final EscherProperty prop) {
        this.properties.add(prop);
    }
    
    @Override
    public int fillFields(final byte[] data, final int offset, final EscherRecordFactory recordFactory) {
        final int bytesRemaining = this.readHeader(data, offset);
        final short propertiesCount = EscherRecord.readInstance(data, offset);
        final int pos = offset + 8;
        final EscherPropertyFactory f = new EscherPropertyFactory();
        this.properties.clear();
        this.properties.addAll(f.createProperties(data, pos, propertiesCount));
        return bytesRemaining + 8;
    }
    
    public List<EscherProperty> getEscherProperties() {
        return this.properties;
    }
    
    public EscherProperty getEscherProperty(final int index) {
        return this.properties.get(index);
    }
    
    private int getPropertiesSize() {
        int totalSize = 0;
        for (final EscherProperty property : this.properties) {
            totalSize += property.getPropertySize();
        }
        return totalSize;
    }
    
    @Override
    public int getRecordSize() {
        return 8 + this.getPropertiesSize();
    }
    
    public <T extends EscherProperty> T lookup(final EscherPropertyTypes propType) {
        return this.lookup(propType.propNumber);
    }
    
    public <T extends EscherProperty> T lookup(final int propId) {
        return (T)this.properties.stream().filter(p -> p.getPropertyNumber() == propId).findFirst().orElse(null);
    }
    
    @Override
    public int serialize(final int offset, final byte[] data, final EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        LittleEndian.putShort(data, offset, this.getOptions());
        LittleEndian.putShort(data, offset + 2, this.getRecordId());
        LittleEndian.putInt(data, offset + 4, this.getPropertiesSize());
        int pos = offset + 8;
        for (final EscherProperty property : this.properties) {
            pos += property.serializeSimplePart(data, pos);
        }
        for (final EscherProperty property : this.properties) {
            pos += property.serializeComplexPart(data, pos);
        }
        listener.afterRecordSerialize(pos, this.getRecordId(), pos - offset, this);
        return pos - offset;
    }
    
    public void sortProperties() {
        this.properties.sort(Comparator.comparingInt(EscherProperty::getPropertyNumber));
    }
    
    public void setEscherProperty(final EscherProperty value) {
        this.properties.removeIf(prop -> prop.getId() == value.getId());
        this.properties.add(value);
        this.sortProperties();
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public void removeEscherProperty(final int num) {
        this.properties.removeIf(prop -> prop.getPropertyNumber() == num);
    }
    
    public void removeEscherProperty(final EscherPropertyTypes type) {
        this.properties.removeIf(prop -> prop.getPropertyNumber() == type.propNumber);
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "isContainer", this::isContainerRecord, "properties", this::getEscherProperties);
    }
}
