package org.apache.poi.hssf.model;

import org.apache.poi.hssf.record.TableRecord;
import org.apache.poi.hssf.record.ArrayRecord;
import org.apache.poi.hssf.record.SharedFormulaRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.ss.util.CellReference;
import java.util.ArrayList;
import org.apache.poi.hssf.record.MergeCellsRecord;
import org.apache.poi.hssf.record.aggregates.SharedValueManager;
import org.apache.poi.hssf.record.Record;
import java.util.List;

public final class RowBlocksReader
{
    private final List<Record> _plainRecords;
    private final SharedValueManager _sfm;
    private final MergeCellsRecord[] _mergedCellsRecords;
    
    public RowBlocksReader(final RecordStream rs) {
        final List<Record> plainRecords = new ArrayList<Record>();
        final List<Record> shFrmRecords = new ArrayList<Record>();
        final List<CellReference> firstCellRefs = new ArrayList<CellReference>();
        final List<Record> arrayRecords = new ArrayList<Record>();
        final List<Record> tableRecords = new ArrayList<Record>();
        final List<Record> mergeCellRecords = new ArrayList<Record>();
        Record prevRec = null;
        while (!RecordOrderer.isEndOfRowBlock(rs.peekNextSid())) {
            if (!rs.hasNext()) {
                throw new RuntimeException("Failed to find end of row/cell records");
            }
            final Record rec = rs.getNext();
            List<Record> dest = null;
            switch (rec.getSid()) {
                case 229: {
                    dest = mergeCellRecords;
                    break;
                }
                case 1212: {
                    dest = shFrmRecords;
                    if (!(prevRec instanceof FormulaRecord)) {
                        throw new RuntimeException("Shared formula record should follow a FormulaRecord");
                    }
                    final FormulaRecord fr = (FormulaRecord)prevRec;
                    firstCellRefs.add(new CellReference(fr.getRow(), fr.getColumn()));
                    break;
                }
                case 545: {
                    dest = arrayRecords;
                    break;
                }
                case 566: {
                    dest = tableRecords;
                    break;
                }
                default: {
                    dest = plainRecords;
                    break;
                }
            }
            dest.add(rec);
            prevRec = rec;
        }
        final SharedFormulaRecord[] sharedFormulaRecs = new SharedFormulaRecord[shFrmRecords.size()];
        final CellReference[] firstCells = new CellReference[firstCellRefs.size()];
        final ArrayRecord[] arrayRecs = new ArrayRecord[arrayRecords.size()];
        final TableRecord[] tableRecs = new TableRecord[tableRecords.size()];
        shFrmRecords.toArray(sharedFormulaRecs);
        firstCellRefs.toArray(firstCells);
        arrayRecords.toArray(arrayRecs);
        tableRecords.toArray(tableRecs);
        this._plainRecords = plainRecords;
        this._sfm = SharedValueManager.create(sharedFormulaRecs, firstCells, arrayRecs, tableRecs);
        mergeCellRecords.toArray(this._mergedCellsRecords = new MergeCellsRecord[mergeCellRecords.size()]);
    }
    
    public MergeCellsRecord[] getLooseMergedCells() {
        return this._mergedCellsRecords;
    }
    
    public SharedValueManager getSharedFormulaManager() {
        return this._sfm;
    }
    
    public RecordStream getPlainRecordStream() {
        return new RecordStream(this._plainRecords, 0);
    }
}
