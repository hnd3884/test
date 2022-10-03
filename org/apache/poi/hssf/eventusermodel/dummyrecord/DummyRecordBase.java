package org.apache.poi.hssf.eventusermodel.dummyrecord;

import org.apache.poi.util.RecordFormatException;
import org.apache.poi.hssf.record.Record;

abstract class DummyRecordBase extends Record
{
    protected DummyRecordBase() {
    }
    
    @Override
    public final short getSid() {
        return -1;
    }
    
    @Override
    public int serialize(final int offset, final byte[] data) {
        throw new RecordFormatException("Cannot serialize a dummy record");
    }
    
    @Override
    public final int getRecordSize() {
        throw new RecordFormatException("Cannot serialize a dummy record");
    }
}
