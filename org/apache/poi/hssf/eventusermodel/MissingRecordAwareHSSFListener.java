package org.apache.poi.hssf.eventusermodel;

import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.MulRKRecord;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.record.MulBlankRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingRowDummyRecord;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.Record;

public final class MissingRecordAwareHSSFListener implements HSSFListener
{
    private HSSFListener childListener;
    private int lastRowRow;
    private int lastCellRow;
    private int lastCellColumn;
    
    public MissingRecordAwareHSSFListener(final HSSFListener listener) {
        this.resetCounts();
        this.childListener = listener;
    }
    
    @Override
    public void processRecord(final Record record) {
        CellValueRecordInterface[] expandedRecords = null;
        int thisRow;
        int thisColumn;
        if (record instanceof CellValueRecordInterface) {
            final CellValueRecordInterface valueRec = (CellValueRecordInterface)record;
            thisRow = valueRec.getRow();
            thisColumn = valueRec.getColumn();
        }
        else {
            if (record instanceof StringRecord) {
                this.childListener.processRecord(record);
                return;
            }
            thisRow = -1;
            thisColumn = -1;
            switch (record.getSid()) {
                case 2057: {
                    final BOFRecord bof = (BOFRecord)record;
                    if (bof.getType() == 5 || bof.getType() == 16) {
                        this.resetCounts();
                        break;
                    }
                    break;
                }
                case 520: {
                    final RowRecord rowrec = (RowRecord)record;
                    if (this.lastRowRow + 1 < rowrec.getRowNumber()) {
                        for (int i = this.lastRowRow + 1; i < rowrec.getRowNumber(); ++i) {
                            final MissingRowDummyRecord dr = new MissingRowDummyRecord(i);
                            this.childListener.processRecord(dr);
                        }
                    }
                    this.lastRowRow = rowrec.getRowNumber();
                    this.lastCellColumn = -1;
                    break;
                }
                case 1212: {
                    this.childListener.processRecord(record);
                    return;
                }
                case 190: {
                    final MulBlankRecord mbr = (MulBlankRecord)record;
                    expandedRecords = RecordFactory.convertBlankRecords(mbr);
                    break;
                }
                case 189: {
                    final MulRKRecord mrk = (MulRKRecord)record;
                    expandedRecords = RecordFactory.convertRKRecords(mrk);
                    break;
                }
                case 28: {
                    final NoteRecord nrec = (NoteRecord)record;
                    thisRow = nrec.getRow();
                    thisColumn = nrec.getColumn();
                    break;
                }
            }
        }
        if (expandedRecords != null && expandedRecords.length > 0) {
            thisRow = expandedRecords[0].getRow();
            thisColumn = expandedRecords[0].getColumn();
        }
        if (thisRow != this.lastCellRow && thisRow > 0) {
            if (this.lastCellRow == -1) {
                this.lastCellRow = 0;
            }
            for (int j = this.lastCellRow; j < thisRow; ++j) {
                int cols = -1;
                if (j == this.lastCellRow) {
                    cols = this.lastCellColumn;
                }
                this.childListener.processRecord(new LastCellOfRowDummyRecord(j, cols));
            }
        }
        if (this.lastCellRow != -1 && this.lastCellColumn != -1 && thisRow == -1) {
            this.childListener.processRecord(new LastCellOfRowDummyRecord(this.lastCellRow, this.lastCellColumn));
            this.lastCellRow = -1;
            this.lastCellColumn = -1;
        }
        if (thisRow != this.lastCellRow) {
            this.lastCellColumn = -1;
        }
        if (this.lastCellColumn != thisColumn - 1) {
            for (int j = this.lastCellColumn + 1; j < thisColumn; ++j) {
                this.childListener.processRecord(new MissingCellDummyRecord(thisRow, j));
            }
        }
        if (expandedRecords != null && expandedRecords.length > 0) {
            thisColumn = expandedRecords[expandedRecords.length - 1].getColumn();
        }
        if (thisColumn != -1) {
            this.lastCellColumn = thisColumn;
            this.lastCellRow = thisRow;
        }
        if (expandedRecords != null && expandedRecords.length > 0) {
            for (final CellValueRecordInterface r : expandedRecords) {
                this.childListener.processRecord((Record)r);
            }
        }
        else {
            this.childListener.processRecord(record);
        }
    }
    
    private void resetCounts() {
        this.lastRowRow = -1;
        this.lastCellRow = -1;
        this.lastCellColumn = -1;
    }
}
