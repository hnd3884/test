package org.apache.poi.hssf.eventusermodel.dummyrecord;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;

public final class MissingRowDummyRecord extends DummyRecordBase
{
    private final int rowNumber;
    
    public MissingRowDummyRecord(final int rowNumber) {
        this.rowNumber = rowNumber;
    }
    
    public int getRowNumber() {
        return this.rowNumber;
    }
    
    @Override
    public MissingRowDummyRecord copy() {
        return this;
    }
}
