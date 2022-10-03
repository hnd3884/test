package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.hssf.record.Record;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.hssf.record.MergeCellsRecord;
import org.apache.poi.hssf.model.RecordStream;
import java.util.ArrayList;
import org.apache.poi.ss.util.CellRangeAddress;
import java.util.List;

public final class MergedCellsTable extends RecordAggregate
{
    private static final int MAX_MERGED_REGIONS = 1027;
    private final List<CellRangeAddress> _mergedRegions;
    
    public MergedCellsTable() {
        this._mergedRegions = new ArrayList<CellRangeAddress>();
    }
    
    public void read(final RecordStream rs) {
        while (rs.peekNextClass() == MergeCellsRecord.class) {
            final MergeCellsRecord mcr = (MergeCellsRecord)rs.getNext();
            for (int nRegions = mcr.getNumAreas(), i = 0; i < nRegions; ++i) {
                final CellRangeAddress cra = mcr.getAreaAt(i);
                this._mergedRegions.add(cra);
            }
        }
    }
    
    @Override
    public int getRecordSize() {
        final int nRegions = this._mergedRegions.size();
        if (nRegions < 1) {
            return 0;
        }
        final int nMergedCellsRecords = nRegions / 1027;
        final int nLeftoverMergedRegions = nRegions % 1027;
        return nMergedCellsRecords * (4 + CellRangeAddressList.getEncodedSize(1027)) + 4 + CellRangeAddressList.getEncodedSize(nLeftoverMergedRegions);
    }
    
    @Override
    public void visitContainedRecords(final RecordVisitor rv) {
        final int nRegions = this._mergedRegions.size();
        if (nRegions < 1) {
            return;
        }
        final int nFullMergedCellsRecords = nRegions / 1027;
        final int nLeftoverMergedRegions = nRegions % 1027;
        final CellRangeAddress[] cras = new CellRangeAddress[nRegions];
        this._mergedRegions.toArray(cras);
        for (int i = 0; i < nFullMergedCellsRecords; ++i) {
            final int startIx = i * 1027;
            rv.visitRecord(new MergeCellsRecord(cras, startIx, 1027));
        }
        if (nLeftoverMergedRegions > 0) {
            final int startIx2 = nFullMergedCellsRecords * 1027;
            rv.visitRecord(new MergeCellsRecord(cras, startIx2, nLeftoverMergedRegions));
        }
    }
    
    public void addRecords(final MergeCellsRecord[] mcrs) {
        for (int i = 0; i < mcrs.length; ++i) {
            this.addMergeCellsRecord(mcrs[i]);
        }
    }
    
    private void addMergeCellsRecord(final MergeCellsRecord mcr) {
        for (int nRegions = mcr.getNumAreas(), i = 0; i < nRegions; ++i) {
            final CellRangeAddress cra = mcr.getAreaAt(i);
            this._mergedRegions.add(cra);
        }
    }
    
    public CellRangeAddress get(final int index) {
        this.checkIndex(index);
        return this._mergedRegions.get(index);
    }
    
    public void remove(final int index) {
        this.checkIndex(index);
        this._mergedRegions.remove(index);
    }
    
    private void checkIndex(final int index) {
        if (index < 0 || index >= this._mergedRegions.size()) {
            throw new IllegalArgumentException("Specified CF index " + index + " is outside the allowable range (0.." + (this._mergedRegions.size() - 1) + ")");
        }
    }
    
    public void addArea(final int rowFrom, final int colFrom, final int rowTo, final int colTo) {
        this._mergedRegions.add(new CellRangeAddress(rowFrom, rowTo, colFrom, colTo));
    }
    
    public int getNumberOfMergedRegions() {
        return this._mergedRegions.size();
    }
}
