package org.apache.poi.ddf;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import org.apache.poi.util.HexDump;
import java.util.Collection;
import java.util.Iterator;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.IOUtils;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;

public final class UnknownEscherRecord extends EscherRecord
{
    private static final int MAX_RECORD_LENGTH = 100000000;
    private static final byte[] NO_BYTES;
    private byte[] thedata;
    private final List<EscherRecord> _childRecords;
    
    public UnknownEscherRecord() {
        this.thedata = UnknownEscherRecord.NO_BYTES;
        this._childRecords = new ArrayList<EscherRecord>();
    }
    
    public UnknownEscherRecord(final UnknownEscherRecord other) {
        super(other);
        this.thedata = UnknownEscherRecord.NO_BYTES;
        this._childRecords = new ArrayList<EscherRecord>();
        other._childRecords.stream().map((Function<? super Object, ?>)EscherRecord::copy).forEach(this._childRecords::add);
    }
    
    @Override
    public int fillFields(final byte[] data, int offset, final EscherRecordFactory recordFactory) {
        int bytesRemaining = this.readHeader(data, offset);
        final int available = data.length - (offset + 8);
        if (bytesRemaining > available) {
            bytesRemaining = available;
        }
        if (this.isContainerRecord()) {
            int bytesWritten = 0;
            this.thedata = new byte[0];
            offset += 8;
            bytesWritten += 8;
            while (bytesRemaining > 0) {
                final EscherRecord child = recordFactory.createRecord(data, offset);
                final int childBytesWritten = child.fillFields(data, offset, recordFactory);
                bytesWritten += childBytesWritten;
                offset += childBytesWritten;
                bytesRemaining -= childBytesWritten;
                this.getChildRecords().add(child);
            }
            return bytesWritten;
        }
        if (bytesRemaining < 0) {
            bytesRemaining = 0;
        }
        System.arraycopy(data, offset + 8, this.thedata = IOUtils.safelyAllocate(bytesRemaining, 100000000), 0, bytesRemaining);
        return bytesRemaining + 8;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data, final EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        LittleEndian.putShort(data, offset, this.getOptions());
        LittleEndian.putShort(data, offset + 2, this.getRecordId());
        int remainingBytes = this.thedata.length;
        for (final EscherRecord r : this._childRecords) {
            remainingBytes += r.getRecordSize();
        }
        LittleEndian.putInt(data, offset + 4, remainingBytes);
        System.arraycopy(this.thedata, 0, data, offset + 8, this.thedata.length);
        int pos = offset + 8 + this.thedata.length;
        for (final EscherRecord r2 : this._childRecords) {
            pos += r2.serialize(pos, data, listener);
        }
        listener.afterRecordSerialize(pos, this.getRecordId(), pos - offset, this);
        return pos - offset;
    }
    
    public byte[] getData() {
        return this.thedata;
    }
    
    @Override
    public int getRecordSize() {
        return 8 + this.thedata.length;
    }
    
    @Override
    public List<EscherRecord> getChildRecords() {
        return this._childRecords;
    }
    
    @Override
    public void setChildRecords(final List<EscherRecord> childRecords) {
        if (childRecords == this._childRecords) {
            return;
        }
        this._childRecords.clear();
        this._childRecords.addAll(childRecords);
    }
    
    @Override
    public String getRecordName() {
        return "Unknown 0x" + HexDump.toHex(this.getRecordId());
    }
    
    public void addChildRecord(final EscherRecord childRecord) {
        this.getChildRecords().add(childRecord);
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "data", this::getData);
    }
    
    @Override
    public Enum getGenericRecordType() {
        return EscherRecordTypes.UNKNOWN;
    }
    
    @Override
    public UnknownEscherRecord copy() {
        return new UnknownEscherRecord(this);
    }
    
    static {
        NO_BYTES = new byte[0];
    }
}
