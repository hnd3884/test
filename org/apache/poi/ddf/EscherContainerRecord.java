package org.apache.poi.ddf;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.GenericRecordUtil;
import java.util.function.Supplier;
import java.util.Map;
import java.io.PrintWriter;
import org.apache.poi.util.HexDump;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import org.apache.poi.util.LittleEndian;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.util.POILogger;

public final class EscherContainerRecord extends EscherRecord implements Iterable<EscherRecord>
{
    public static final short DGG_CONTAINER;
    public static final short BSTORE_CONTAINER;
    public static final short DG_CONTAINER;
    public static final short SPGR_CONTAINER;
    public static final short SP_CONTAINER;
    public static final short SOLVER_CONTAINER;
    private static final POILogger log;
    private int _remainingLength;
    private final List<EscherRecord> _childRecords;
    
    public EscherContainerRecord() {
        this._childRecords = new ArrayList<EscherRecord>();
    }
    
    public EscherContainerRecord(final EscherContainerRecord other) {
        super(other);
        this._childRecords = new ArrayList<EscherRecord>();
        this._remainingLength = other._remainingLength;
        other._childRecords.stream().map((Function<? super Object, ?>)EscherRecord::copy).forEach(this._childRecords::add);
    }
    
    @Override
    public int fillFields(final byte[] data, final int pOffset, final EscherRecordFactory recordFactory) {
        int bytesRemaining = this.readHeader(data, pOffset);
        int bytesWritten = 8;
        int offset = pOffset + 8;
        while (bytesRemaining > 0 && offset < data.length) {
            final EscherRecord child = recordFactory.createRecord(data, offset);
            final int childBytesWritten = child.fillFields(data, offset, recordFactory);
            bytesWritten += childBytesWritten;
            offset += childBytesWritten;
            bytesRemaining -= childBytesWritten;
            this.addChildRecord(child);
            if (offset >= data.length && bytesRemaining > 0) {
                this._remainingLength = bytesRemaining;
                if (!EscherContainerRecord.log.check(5)) {
                    continue;
                }
                EscherContainerRecord.log.log(5, "Not enough Escher data: " + bytesRemaining + " bytes remaining but no space left");
            }
        }
        return bytesWritten;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data, final EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        LittleEndian.putShort(data, offset, this.getOptions());
        LittleEndian.putShort(data, offset + 2, this.getRecordId());
        int remainingBytes = 0;
        for (final EscherRecord r : this) {
            remainingBytes += r.getRecordSize();
        }
        remainingBytes += this._remainingLength;
        LittleEndian.putInt(data, offset + 4, remainingBytes);
        int pos = offset + 8;
        for (final EscherRecord r2 : this) {
            pos += r2.serialize(pos, data, listener);
        }
        listener.afterRecordSerialize(pos, this.getRecordId(), pos - offset, this);
        return pos - offset;
    }
    
    @Override
    public int getRecordSize() {
        int childRecordsSize = 0;
        for (final EscherRecord r : this) {
            childRecordsSize += r.getRecordSize();
        }
        return 8 + childRecordsSize;
    }
    
    public boolean hasChildOfType(final short recordId) {
        return this._childRecords.stream().anyMatch(r -> r.getRecordId() == recordId);
    }
    
    @Override
    public EscherRecord getChild(final int index) {
        return this._childRecords.get(index);
    }
    
    @Override
    public List<EscherRecord> getChildRecords() {
        return new ArrayList<EscherRecord>(this._childRecords);
    }
    
    @Override
    public Iterator<EscherRecord> iterator() {
        return Collections.unmodifiableList((List<? extends EscherRecord>)this._childRecords).iterator();
    }
    
    @Override
    public void setChildRecords(final List<EscherRecord> childRecords) {
        if (childRecords == this._childRecords) {
            throw new IllegalStateException("Child records private data member has escaped");
        }
        this._childRecords.clear();
        this._childRecords.addAll(childRecords);
    }
    
    public boolean removeChildRecord(final EscherRecord toBeRemoved) {
        return this._childRecords.remove(toBeRemoved);
    }
    
    public List<EscherContainerRecord> getChildContainers() {
        final List<EscherContainerRecord> containers = new ArrayList<EscherContainerRecord>();
        for (final EscherRecord r : this) {
            if (r instanceof EscherContainerRecord) {
                containers.add((EscherContainerRecord)r);
            }
        }
        return containers;
    }
    
    @Override
    public String getRecordName() {
        final short id = this.getRecordId();
        final EscherRecordTypes t = EscherRecordTypes.forTypeID(id);
        return (t != EscherRecordTypes.UNKNOWN) ? t.recordName : ("Container 0x" + HexDump.toHex(id));
    }
    
    @Override
    public void display(final PrintWriter w, final int indent) {
        super.display(w, indent);
        for (final EscherRecord escherRecord : this) {
            escherRecord.display(w, indent + 1);
        }
    }
    
    public void addChildRecord(final EscherRecord record) {
        this._childRecords.add(record);
    }
    
    public void addChildBefore(final EscherRecord record, final int insertBeforeRecordId) {
        int idx = 0;
        for (final EscherRecord rec : this) {
            if (rec.getRecordId() == (short)insertBeforeRecordId) {
                break;
            }
            ++idx;
        }
        this._childRecords.add(idx, record);
    }
    
    public <T extends EscherRecord> T getChildById(final short recordId) {
        for (final EscherRecord childRecord : this) {
            if (childRecord.getRecordId() == recordId) {
                final T result = (T)childRecord;
                return result;
            }
        }
        return null;
    }
    
    public void getRecordsById(final short recordId, final List<EscherRecord> out) {
        for (final EscherRecord r : this) {
            if (r instanceof EscherContainerRecord) {
                final EscherContainerRecord c = (EscherContainerRecord)r;
                c.getRecordsById(recordId, out);
            }
            else {
                if (r.getRecordId() != recordId) {
                    continue;
                }
                out.add(r);
            }
        }
    }
    
    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "isContainer", this::isContainerRecord);
    }
    
    @Override
    public Enum getGenericRecordType() {
        return EscherRecordTypes.forTypeID(this.getRecordId());
    }
    
    @Override
    public EscherContainerRecord copy() {
        return new EscherContainerRecord(this);
    }
    
    static {
        DGG_CONTAINER = EscherRecordTypes.DGG_CONTAINER.typeID;
        BSTORE_CONTAINER = EscherRecordTypes.BSTORE_CONTAINER.typeID;
        DG_CONTAINER = EscherRecordTypes.DG_CONTAINER.typeID;
        SPGR_CONTAINER = EscherRecordTypes.SPGR_CONTAINER.typeID;
        SP_CONTAINER = EscherRecordTypes.SP_CONTAINER.typeID;
        SOLVER_CONTAINER = EscherRecordTypes.SOLVER_CONTAINER.typeID;
        log = POILogFactory.getLogger(EscherContainerRecord.class);
    }
}
