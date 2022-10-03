package org.apache.poi.hssf.record.aggregates;

import java.util.function.Consumer;
import org.apache.poi.hssf.record.Record;
import java.util.ArrayList;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.DVRecord;
import java.util.List;
import org.apache.poi.hssf.record.DVALRecord;

public final class DataValidityTable extends RecordAggregate
{
    private final DVALRecord _headerRec;
    private final List<DVRecord> _validationList;
    
    public DataValidityTable(final RecordStream rs) {
        this._headerRec = (DVALRecord)rs.getNext();
        final List<DVRecord> temp = new ArrayList<DVRecord>();
        while (rs.peekNextClass() == DVRecord.class) {
            temp.add((DVRecord)rs.getNext());
        }
        this._validationList = temp;
    }
    
    public DataValidityTable() {
        this._headerRec = new DVALRecord();
        this._validationList = new ArrayList<DVRecord>();
    }
    
    @Override
    public void visitContainedRecords(final RecordVisitor rv) {
        if (this._validationList.isEmpty()) {
            return;
        }
        rv.visitRecord(this._headerRec);
        this._validationList.forEach(rv::visitRecord);
    }
    
    public void addDataValidation(final DVRecord dvRecord) {
        this._validationList.add(dvRecord);
        this._headerRec.setDVRecNo(this._validationList.size());
    }
}
