package org.apache.poi.ss.usermodel.helpers;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressBase;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import org.apache.poi.ss.util.CellRangeAddress;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;

public abstract class ColumnShifter extends BaseRowColShifter
{
    protected final Sheet sheet;
    
    public ColumnShifter(final Sheet sh) {
        this.sheet = sh;
    }
    
    @Override
    public List<CellRangeAddress> shiftMergedRegions(final int startColumn, final int endColumn, final int n) {
        final List<CellRangeAddress> shiftedRegions = new ArrayList<CellRangeAddress>();
        final Set<Integer> removedIndices = new HashSet<Integer>();
        for (int size = this.sheet.getNumMergedRegions(), i = 0; i < size; ++i) {
            final CellRangeAddress merged = this.sheet.getMergedRegion(i);
            if (this.removalNeeded(merged, startColumn, endColumn, n)) {
                removedIndices.add(i);
            }
            else {
                final boolean inStart = merged.getFirstColumn() >= startColumn || merged.getLastColumn() >= startColumn;
                final boolean inEnd = merged.getFirstColumn() <= endColumn || merged.getLastColumn() <= endColumn;
                if (inStart) {
                    if (inEnd) {
                        if (!merged.containsColumn(startColumn - 1) && !merged.containsColumn(endColumn + 1)) {
                            merged.setFirstColumn(merged.getFirstColumn() + n);
                            merged.setLastColumn(merged.getLastColumn() + n);
                            shiftedRegions.add(merged);
                            removedIndices.add(i);
                        }
                    }
                }
            }
        }
        if (!removedIndices.isEmpty()) {
            this.sheet.removeMergedRegions(removedIndices);
        }
        for (final CellRangeAddress region : shiftedRegions) {
            this.sheet.addMergedRegion(region);
        }
        return shiftedRegions;
    }
    
    private boolean removalNeeded(final CellRangeAddress merged, final int startColumn, final int endColumn, final int n) {
        final int movedColumns = endColumn - startColumn + 1;
        CellRangeAddress overwrite;
        if (n > 0) {
            final int firstCol = Math.max(endColumn + 1, endColumn + n - movedColumns);
            final int lastCol = endColumn + n;
            overwrite = new CellRangeAddress(0, 0, firstCol, lastCol);
        }
        else {
            final int firstCol = startColumn + n;
            final int lastCol = Math.min(startColumn - 1, startColumn + n + movedColumns);
            overwrite = new CellRangeAddress(0, 0, firstCol, lastCol);
        }
        return merged.intersects(overwrite);
    }
    
    public void shiftColumns(final int firstShiftColumnIndex, final int lastShiftColumnIndex, final int step) {
        if (step > 0) {
            for (final Row row : this.sheet) {
                if (row != null) {
                    row.shiftCellsRight(firstShiftColumnIndex, lastShiftColumnIndex, step);
                }
            }
        }
        else if (step < 0) {
            for (final Row row : this.sheet) {
                if (row != null) {
                    row.shiftCellsLeft(firstShiftColumnIndex, lastShiftColumnIndex, -step);
                }
            }
        }
    }
}
