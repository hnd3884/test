package org.apache.poi.ss.usermodel.helpers;

import org.apache.poi.util.LocaleUtil;
import org.apache.poi.ss.util.CellRangeAddressBase;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import org.apache.poi.ss.util.CellRangeAddress;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;

public abstract class RowShifter extends BaseRowColShifter
{
    protected final Sheet sheet;
    
    public RowShifter(final Sheet sh) {
        this.sheet = sh;
    }
    
    @Override
    public List<CellRangeAddress> shiftMergedRegions(final int startRow, final int endRow, final int n) {
        final List<CellRangeAddress> shiftedRegions = new ArrayList<CellRangeAddress>();
        final Set<Integer> removedIndices = new HashSet<Integer>();
        for (int size = this.sheet.getNumMergedRegions(), i = 0; i < size; ++i) {
            final CellRangeAddress merged = this.sheet.getMergedRegion(i);
            if (this.removalNeeded(merged, startRow, endRow, n)) {
                removedIndices.add(i);
            }
            else {
                final boolean inStart = merged.getFirstRow() >= startRow || merged.getLastRow() >= startRow;
                final boolean inEnd = merged.getFirstRow() <= endRow || merged.getLastRow() <= endRow;
                if (inStart) {
                    if (inEnd) {
                        if (!merged.containsRow(startRow - 1) && !merged.containsRow(endRow + 1)) {
                            merged.setFirstRow(merged.getFirstRow() + n);
                            merged.setLastRow(merged.getLastRow() + n);
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
    
    private boolean removalNeeded(final CellRangeAddress merged, final int startRow, final int endRow, final int n) {
        final int movedRows = endRow - startRow + 1;
        CellRangeAddress overwrite;
        if (n > 0) {
            final int firstRow = Math.max(endRow + 1, endRow + n - movedRows);
            final int lastRow = endRow + n;
            overwrite = new CellRangeAddress(firstRow, lastRow, 0, 0);
        }
        else {
            final int firstRow = startRow + n;
            final int lastRow = Math.min(startRow - 1, startRow + n + movedRows);
            overwrite = new CellRangeAddress(firstRow, lastRow, 0, 0);
        }
        return merged.intersects(overwrite);
    }
    
    public static void validateShiftParameters(final int firstShiftColumnIndex, final int lastShiftColumnIndex, final int step) {
        if (step < 0) {
            throw new IllegalArgumentException("Shifting step may not be negative, but had " + step);
        }
        if (firstShiftColumnIndex > lastShiftColumnIndex) {
            throw new IllegalArgumentException(String.format(LocaleUtil.getUserLocale(), "Incorrect shifting range : %d-%d", firstShiftColumnIndex, lastShiftColumnIndex));
        }
    }
    
    public static void validateShiftLeftParameters(final int firstShiftColumnIndex, final int lastShiftColumnIndex, final int step) {
        validateShiftParameters(firstShiftColumnIndex, lastShiftColumnIndex, step);
        if (firstShiftColumnIndex - step < 0) {
            throw new IllegalStateException("Column index less than zero: " + (firstShiftColumnIndex + step));
        }
    }
}
