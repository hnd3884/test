package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.util.Removal;
import org.apache.poi.ddf.EscherSerializationListener;
import org.apache.poi.ddf.NullEscherSerializationListener;
import org.apache.poi.util.LittleEndian;
import java.util.Iterator;
import org.apache.poi.ddf.EscherRecordFactory;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.ArrayList;
import org.apache.poi.hssf.util.LazilyConcatenatedByteArray;
import org.apache.poi.ddf.EscherRecord;
import java.util.List;

public abstract class AbstractEscherHolderRecord extends Record
{
    private static boolean DESERIALISE;
    private final List<EscherRecord> escherRecords;
    private final LazilyConcatenatedByteArray rawDataContainer;
    
    public AbstractEscherHolderRecord() {
        this.escherRecords = new ArrayList<EscherRecord>();
        this.rawDataContainer = new LazilyConcatenatedByteArray();
    }
    
    public AbstractEscherHolderRecord(final AbstractEscherHolderRecord other) {
        this.escherRecords = new ArrayList<EscherRecord>();
        this.rawDataContainer = new LazilyConcatenatedByteArray();
        other.escherRecords.stream().map((Function<? super Object, ?>)EscherRecord::copy).forEach(this.escherRecords::add);
        this.rawDataContainer.concatenate(other.rawDataContainer);
    }
    
    public AbstractEscherHolderRecord(final RecordInputStream in) {
        this.escherRecords = new ArrayList<EscherRecord>();
        this.rawDataContainer = new LazilyConcatenatedByteArray();
        if (!AbstractEscherHolderRecord.DESERIALISE) {
            this.rawDataContainer.concatenate(in.readRemainder());
        }
        else {
            final byte[] data = in.readAllContinuedRemainder();
            this.convertToEscherRecords(0, data.length, data);
        }
    }
    
    protected void convertRawBytesToEscherRecords() {
        if (!AbstractEscherHolderRecord.DESERIALISE) {
            final byte[] rawData = this.getRawData();
            this.convertToEscherRecords(0, rawData.length, rawData);
        }
    }
    
    private void convertToEscherRecords(final int offset, final int size, final byte[] data) {
        this.escherRecords.clear();
        final EscherRecordFactory recordFactory = new DefaultEscherRecordFactory();
        int bytesRead;
        for (int pos = offset; pos < offset + size; pos += bytesRead) {
            final EscherRecord r = recordFactory.createRecord(data, pos);
            bytesRead = r.fillFields(data, pos, recordFactory);
            this.escherRecords.add(r);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        final String nl = System.getProperty("line.separator");
        buffer.append('[' + this.getRecordName() + ']' + nl);
        if (this.escherRecords.size() == 0) {
            buffer.append("No Escher Records Decoded" + nl);
        }
        for (final EscherRecord r : this.escherRecords) {
            buffer.append(r);
        }
        buffer.append("[/" + this.getRecordName() + ']' + nl);
        return buffer.toString();
    }
    
    protected abstract String getRecordName();
    
    @Override
    public int serialize(final int offset, final byte[] data) {
        LittleEndian.putShort(data, 0 + offset, this.getSid());
        LittleEndian.putShort(data, 2 + offset, (short)(this.getRecordSize() - 4));
        final byte[] rawData = this.getRawData();
        if (this.escherRecords.size() == 0 && rawData != null) {
            LittleEndian.putShort(data, 0 + offset, this.getSid());
            LittleEndian.putShort(data, 2 + offset, (short)(this.getRecordSize() - 4));
            System.arraycopy(rawData, 0, data, 4 + offset, rawData.length);
            return rawData.length + 4;
        }
        LittleEndian.putShort(data, 0 + offset, this.getSid());
        LittleEndian.putShort(data, 2 + offset, (short)(this.getRecordSize() - 4));
        int pos = offset + 4;
        for (final EscherRecord r : this.escherRecords) {
            pos += r.serialize(pos, data, new NullEscherSerializationListener());
        }
        return this.getRecordSize();
    }
    
    @Override
    public int getRecordSize() {
        final byte[] rawData = this.getRawData();
        if (this.escherRecords.size() == 0 && rawData != null) {
            return rawData.length;
        }
        int size = 0;
        for (final EscherRecord r : this.escherRecords) {
            size += r.getRecordSize();
        }
        return size;
    }
    
    @Override
    public abstract short getSid();
    
    @Deprecated
    @Removal(version = "5.0.0")
    public AbstractEscherHolderRecord clone() {
        return this.copy();
    }
    
    @Override
    public abstract AbstractEscherHolderRecord copy();
    
    public void addEscherRecord(final int index, final EscherRecord element) {
        this.escherRecords.add(index, element);
    }
    
    public boolean addEscherRecord(final EscherRecord element) {
        return this.escherRecords.add(element);
    }
    
    public List<EscherRecord> getEscherRecords() {
        return this.escherRecords;
    }
    
    public void clearEscherRecords() {
        this.escherRecords.clear();
    }
    
    public EscherContainerRecord getEscherContainer() {
        for (final EscherRecord er : this.escherRecords) {
            if (er instanceof EscherContainerRecord) {
                return (EscherContainerRecord)er;
            }
        }
        return null;
    }
    
    public EscherRecord findFirstWithId(final short id) {
        return this.findFirstWithId(id, this.getEscherRecords());
    }
    
    private EscherRecord findFirstWithId(final short id, final List<EscherRecord> records) {
        for (final EscherRecord r : records) {
            if (r.getRecordId() == id) {
                return r;
            }
        }
        for (final EscherRecord r : records) {
            if (r.isContainerRecord()) {
                final EscherRecord found = this.findFirstWithId(id, r.getChildRecords());
                if (found != null) {
                    return found;
                }
                continue;
            }
        }
        return null;
    }
    
    public EscherRecord getEscherRecord(final int index) {
        return this.escherRecords.get(index);
    }
    
    public void join(final AbstractEscherHolderRecord record) {
        this.rawDataContainer.concatenate(record.getRawData());
    }
    
    public void processContinueRecord(final byte[] record) {
        this.rawDataContainer.concatenate(record);
    }
    
    public byte[] getRawData() {
        return this.rawDataContainer.toArray();
    }
    
    public void setRawData(final byte[] rawData) {
        this.rawDataContainer.clear();
        this.rawDataContainer.concatenate(rawData);
    }
    
    public void decode() {
        if (null == this.escherRecords || 0 == this.escherRecords.size()) {
            final byte[] rawData = this.getRawData();
            this.convertToEscherRecords(0, rawData.length, rawData);
        }
    }
    
    static {
        try {
            AbstractEscherHolderRecord.DESERIALISE = (System.getProperty("poi.deserialize.escher") != null);
        }
        catch (final SecurityException e) {
            AbstractEscherHolderRecord.DESERIALISE = false;
        }
    }
}
