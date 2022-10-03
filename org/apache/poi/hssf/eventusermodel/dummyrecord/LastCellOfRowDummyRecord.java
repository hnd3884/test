package org.apache.poi.hssf.eventusermodel.dummyrecord;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.Record;

public final class LastCellOfRowDummyRecord extends DummyRecordBase
{
    private final int row;
    private final int lastColumnNumber;
    
    public LastCellOfRowDummyRecord(final int row, final int lastColumnNumber) {
        this.row = row;
        this.lastColumnNumber = lastColumnNumber;
    }
    
    public int getRow() {
        return this.row;
    }
    
    public int getLastColumnNumber() {
        return this.lastColumnNumber;
    }
    
    @Override
    public String toString() {
        return "End-of-Row for Row=" + this.row + " at Column=" + this.lastColumnNumber;
    }
    
    @Override
    public LastCellOfRowDummyRecord copy() {
        return this;
    }
}
