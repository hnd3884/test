package org.apache.poi.ddf;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordXmlWriter;
import java.io.PrintWriter;
import org.apache.poi.util.Removal;
import java.util.Collections;
import java.util.List;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.BitField;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.common.Duplicatable;

public abstract class EscherRecord implements Duplicatable, GenericRecord
{
    private static final BitField fInstance;
    private static final BitField fVersion;
    private short _options;
    private short _recordId;
    
    public EscherRecord() {
    }
    
    protected EscherRecord(final EscherRecord other) {
        this._options = other._options;
        this._recordId = other._recordId;
    }
    
    protected int fillFields(final byte[] data, final EscherRecordFactory f) {
        return this.fillFields(data, 0, f);
    }
    
    public abstract int fillFields(final byte[] p0, final int p1, final EscherRecordFactory p2);
    
    protected int readHeader(final byte[] data, final int offset) {
        this._options = LittleEndian.getShort(data, offset);
        this._recordId = LittleEndian.getShort(data, offset + 2);
        return LittleEndian.getInt(data, offset + 4);
    }
    
    protected static short readInstance(final byte[] data, final int offset) {
        final short options = LittleEndian.getShort(data, offset);
        return EscherRecord.fInstance.getShortValue(options);
    }
    
    public boolean isContainerRecord() {
        return this.getVersion() == 15;
    }
    
    @Internal
    public short getOptions() {
        return this._options;
    }
    
    @Internal
    public void setOptions(final short options) {
        this.setVersion(EscherRecord.fVersion.getShortValue(options));
        this.setInstance(EscherRecord.fInstance.getShortValue(options));
        this._options = options;
    }
    
    public byte[] serialize() {
        final byte[] retval = new byte[this.getRecordSize()];
        this.serialize(0, retval);
        return retval;
    }
    
    public int serialize(final int offset, final byte[] data) {
        return this.serialize(offset, data, new NullEscherSerializationListener());
    }
    
    public abstract int serialize(final int p0, final byte[] p1, final EscherSerializationListener p2);
    
    public abstract int getRecordSize();
    
    public short getRecordId() {
        return this._recordId;
    }
    
    public void setRecordId(final short recordId) {
        this._recordId = recordId;
    }
    
    public List<EscherRecord> getChildRecords() {
        return Collections.emptyList();
    }
    
    public void setChildRecords(final List<EscherRecord> childRecords) {
        throw new UnsupportedOperationException("This record does not support child records.");
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public final EscherRecord clone() {
        return this.copy();
    }
    
    public EscherRecord getChild(final int index) {
        return this.getChildRecords().get(index);
    }
    
    public void display(final PrintWriter w, final int indent) {
        for (int i = 0; i < indent * 4; ++i) {
            w.print(' ');
        }
        w.println(this.getRecordName());
    }
    
    public abstract String getRecordName();
    
    public short getInstance() {
        return EscherRecord.fInstance.getShortValue(this._options);
    }
    
    public void setInstance(final short value) {
        this._options = EscherRecord.fInstance.setShortValue(this._options, value);
    }
    
    public short getVersion() {
        return EscherRecord.fVersion.getShortValue(this._options);
    }
    
    public void setVersion(final short value) {
        this._options = EscherRecord.fVersion.setShortValue(this._options, value);
    }
    
    public String toXml() {
        return this.toXml("");
    }
    
    public final String toXml(final String tab) {
        return GenericRecordXmlWriter.marshal(this);
    }
    
    @Override
    public final String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }
    
    @Override
    public List<? extends GenericRecord> getGenericChildren() {
        return this.getChildRecords();
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("recordId", this::getRecordId, "version", this::getVersion, "instance", this::getInstance, "options", this::getOptions, "recordSize", this::getRecordSize);
    }
    
    @Override
    public abstract EscherRecord copy();
    
    static {
        fInstance = BitFieldFactory.getInstance(65520);
        fVersion = BitFieldFactory.getInstance(15);
    }
}
