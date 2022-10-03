package org.apache.poi.hssf.eventusermodel.dummyrecord;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;

public final class MissingCellDummyRecord extends DummyRecordBase
{
    private final int row;
    private final int column;
    
    public MissingCellDummyRecord(final int row, final int column) {
        this.row = row;
        this.column = column;
    }
    
    public int getRow() {
        return this.row;
    }
    
    public int getColumn() {
        return this.column;
    }
    
    @Override
    public MissingCellDummyRecord copy() {
        return this;
    }
}
