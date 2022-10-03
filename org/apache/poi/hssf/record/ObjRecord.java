package org.apache.poi.hssf.record;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.Removal;
import java.io.IOException;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import java.util.Iterator;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.LittleEndian;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;

public final class ObjRecord extends Record
{
    public static final short sid = 93;
    private static final int NORMAL_PAD_ALIGNMENT = 2;
    private static int MAX_PAD_ALIGNMENT;
    private final List<SubRecord> subrecords;
    private final byte[] _uninterpretedData;
    private boolean _isPaddedToQuadByteMultiple;
    
    public ObjRecord() {
        this.subrecords = new ArrayList<SubRecord>();
        this._uninterpretedData = null;
    }
    
    public ObjRecord(final ObjRecord other) {
        this.subrecords = new ArrayList<SubRecord>();
        other.subrecords.stream().map((Function<? super Object, ?>)SubRecord::copy).forEach(this.subrecords::add);
        this._uninterpretedData = (byte[])((other._uninterpretedData == null) ? null : ((byte[])other._uninterpretedData.clone()));
        this._isPaddedToQuadByteMultiple = other._isPaddedToQuadByteMultiple;
    }
    
    public ObjRecord(final RecordInputStream in) {
        this.subrecords = new ArrayList<SubRecord>();
        final byte[] subRecordData = in.readRemainder();
        if (LittleEndian.getUShort(subRecordData, 0) != 21) {
            this._uninterpretedData = subRecordData;
            return;
        }
        final LittleEndianByteArrayInputStream subRecStream = new LittleEndianByteArrayInputStream(subRecordData);
        final CommonObjectDataSubRecord cmo = (CommonObjectDataSubRecord)SubRecord.createSubRecord(subRecStream, 0);
        this.subrecords.add(cmo);
        SubRecord subRecord;
        do {
            subRecord = SubRecord.createSubRecord(subRecStream, cmo.getObjectType());
            this.subrecords.add(subRecord);
        } while (!subRecord.isTerminating());
        final int nRemainingBytes = subRecordData.length - subRecStream.getReadIndex();
        if (nRemainingBytes > 0) {
            this._isPaddedToQuadByteMultiple = (subRecordData.length % ObjRecord.MAX_PAD_ALIGNMENT == 0);
            if (nRemainingBytes >= (this._isPaddedToQuadByteMultiple ? ObjRecord.MAX_PAD_ALIGNMENT : 2)) {
                if (!canPaddingBeDiscarded(subRecordData, nRemainingBytes)) {
                    final String msg = "Leftover " + nRemainingBytes + " bytes in subrecord data " + HexDump.toHex(subRecordData);
                    throw new RecordFormatException(msg);
                }
                this._isPaddedToQuadByteMultiple = false;
            }
        }
        else {
            this._isPaddedToQuadByteMultiple = false;
        }
        this._uninterpretedData = null;
    }
    
    private static boolean canPaddingBeDiscarded(final byte[] data, final int nRemainingBytes) {
        for (int i = data.length - nRemainingBytes; i < data.length; ++i) {
            if (data[i] != 0) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[OBJ]\n");
        for (final SubRecord record : this.subrecords) {
            sb.append("SUBRECORD: ").append(record);
        }
        sb.append("[/OBJ]\n");
        return sb.toString();
    }
    
    @Override
    public int getRecordSize() {
        if (this._uninterpretedData != null) {
            return this._uninterpretedData.length + 4;
        }
        int size = 0;
        for (final SubRecord record : this.subrecords) {
            size += record.getDataSize() + 4;
        }
        if (this._isPaddedToQuadByteMultiple) {
            while (size % ObjRecord.MAX_PAD_ALIGNMENT != 0) {
                ++size;
            }
        }
        else {
            while (size % 2 != 0) {
                ++size;
            }
        }
        return size + 4;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data) {
        final int recSize = this.getRecordSize();
        final int dataSize = recSize - 4;
        try (final LittleEndianByteArrayOutputStream out = new LittleEndianByteArrayOutputStream(data, offset, recSize)) {
            out.writeShort(93);
            out.writeShort(dataSize);
            if (this._uninterpretedData == null) {
                for (int i = 0; i < this.subrecords.size(); ++i) {
                    final SubRecord record = this.subrecords.get(i);
                    record.serialize(out);
                }
                final int expectedEndIx = offset + dataSize;
                while (out.getWriteIndex() < expectedEndIx) {
                    out.writeByte(0);
                }
            }
            else {
                out.write(this._uninterpretedData);
            }
        }
        catch (final IOException ioe) {
            throw new IllegalStateException(ioe);
        }
        return recSize;
    }
    
    @Override
    public short getSid() {
        return 93;
    }
    
    public List<SubRecord> getSubRecords() {
        return this.subrecords;
    }
    
    public void clearSubRecords() {
        this.subrecords.clear();
    }
    
    public void addSubRecord(final int index, final SubRecord element) {
        this.subrecords.add(index, element);
    }
    
    public boolean addSubRecord(final SubRecord o) {
        return this.subrecords.add(o);
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public ObjRecord clone() {
        return this.copy();
    }
    
    @Override
    public ObjRecord copy() {
        return new ObjRecord(this);
    }
    
    static {
        ObjRecord.MAX_PAD_ALIGNMENT = 4;
    }
}
