package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndian;
import java.util.List;
import java.util.Iterator;
import org.apache.poi.ddf.EscherSerializationListener;
import org.apache.poi.ddf.NullEscherSerializationListener;
import org.apache.poi.ddf.EscherRecord;

public final class DrawingGroupRecord extends AbstractEscherHolderRecord
{
    public static final short sid = 235;
    static final int MAX_RECORD_SIZE = 8228;
    private static final int MAX_DATA_SIZE = 8224;
    
    public DrawingGroupRecord() {
    }
    
    public DrawingGroupRecord(final DrawingGroupRecord other) {
        super(other);
    }
    
    public DrawingGroupRecord(final RecordInputStream in) {
        super(in);
    }
    
    @Override
    protected String getRecordName() {
        return "MSODRAWINGGROUP";
    }
    
    @Override
    public short getSid() {
        return 235;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data) {
        final byte[] rawData = this.getRawData();
        if (this.getEscherRecords().size() == 0 && rawData != null) {
            return this.writeData(offset, data, rawData);
        }
        final byte[] buffer = new byte[this.getRawDataSize()];
        int pos = 0;
        for (final EscherRecord r : this.getEscherRecords()) {
            pos += r.serialize(pos, buffer, new NullEscherSerializationListener());
        }
        return this.writeData(offset, data, buffer);
    }
    
    public void processChildRecords() {
        this.convertRawBytesToEscherRecords();
    }
    
    @Override
    public int getRecordSize() {
        return grossSizeFromDataSize(this.getRawDataSize());
    }
    
    private int getRawDataSize() {
        final List<EscherRecord> escherRecords = this.getEscherRecords();
        final byte[] rawData = this.getRawData();
        if (escherRecords.size() == 0 && rawData != null) {
            return rawData.length;
        }
        int size = 0;
        for (final EscherRecord r : escherRecords) {
            size += r.getRecordSize();
        }
        return size;
    }
    
    static int grossSizeFromDataSize(final int dataSize) {
        return dataSize + ((dataSize - 1) / 8224 + 1) * 4;
    }
    
    private int writeData(int offset, final byte[] data, final byte[] rawData) {
        int writtenActualData = 0;
        int segmentLength;
        for (int writtenRawData = 0; writtenRawData < rawData.length; writtenRawData += segmentLength, writtenActualData += segmentLength) {
            segmentLength = Math.min(rawData.length - writtenRawData, 8224);
            if (writtenRawData / 8224 >= 2) {
                this.writeContinueHeader(data, offset, segmentLength);
            }
            else {
                this.writeHeader(data, offset, segmentLength);
            }
            writtenActualData += 4;
            offset += 4;
            System.arraycopy(rawData, writtenRawData, data, offset, segmentLength);
            offset += segmentLength;
        }
        return writtenActualData;
    }
    
    private void writeHeader(final byte[] data, final int offset, final int sizeExcludingHeader) {
        LittleEndian.putShort(data, 0 + offset, this.getSid());
        LittleEndian.putShort(data, 2 + offset, (short)sizeExcludingHeader);
    }
    
    private void writeContinueHeader(final byte[] data, final int offset, final int sizeExcludingHeader) {
        LittleEndian.putShort(data, 0 + offset, (short)60);
        LittleEndian.putShort(data, 2 + offset, (short)sizeExcludingHeader);
    }
    
    @Override
    public DrawingGroupRecord copy() {
        return new DrawingGroupRecord(this);
    }
}
