package org.apache.poi.xssf.usermodel.helpers;

import org.apache.poi.ss.usermodel.CellStyle;
import java.util.Arrays;
import org.apache.poi.xssf.util.NumericRanges;
import java.util.NavigableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCols;
import java.util.Comparator;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;
import java.util.TreeSet;
import org.apache.poi.xssf.util.CTColComparator;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;

public class ColumnHelper
{
    private CTWorksheet worksheet;
    
    public ColumnHelper(final CTWorksheet worksheet) {
        this.worksheet = worksheet;
        this.cleanColumns();
    }
    
    public void cleanColumns() {
        final TreeSet<CTCol> trackedCols = new TreeSet<CTCol>(CTColComparator.BY_MIN_MAX);
        final CTCols newCols = CTCols.Factory.newInstance();
        CTCols[] colsArray;
        int i;
        CTCols cols;
        Iterator iterator;
        CTCol col;
        for (colsArray = this.worksheet.getColsArray(), i = 0; i < colsArray.length; ++i) {
            cols = colsArray[i];
            iterator = cols.getColList().iterator();
            while (iterator.hasNext()) {
                col = (CTCol)iterator.next();
                this.addCleanColIntoCols(newCols, col, trackedCols);
            }
        }
        for (int y = i - 1; y >= 0; --y) {
            this.worksheet.removeCols(y);
        }
        newCols.setColArray((CTCol[])trackedCols.toArray(new CTCol[0]));
        this.worksheet.addNewCols();
        this.worksheet.setColsArray(0, newCols);
    }
    
    public CTCols addCleanColIntoCols(final CTCols cols, final CTCol newCol) {
        final TreeSet<CTCol> trackedCols = new TreeSet<CTCol>(CTColComparator.BY_MIN_MAX);
        trackedCols.addAll(cols.getColList());
        this.addCleanColIntoCols(cols, newCol, trackedCols);
        cols.setColArray((CTCol[])trackedCols.toArray(new CTCol[0]));
        return cols;
    }
    
    private void addCleanColIntoCols(final CTCols cols, final CTCol newCol, final TreeSet<CTCol> trackedCols) {
        final List<CTCol> overlapping = this.getOverlappingCols(newCol, trackedCols);
        if (overlapping.isEmpty()) {
            trackedCols.add(this.cloneCol(cols, newCol));
            return;
        }
        trackedCols.removeAll(overlapping);
        for (final CTCol existing : overlapping) {
            final long[] overlap = this.getOverlap(newCol, existing);
            final CTCol overlapCol = this.cloneCol(cols, existing, overlap);
            this.setColumnAttributes(newCol, overlapCol);
            trackedCols.add(overlapCol);
            final CTCol beforeCol = (existing.getMin() < newCol.getMin()) ? existing : newCol;
            final long[] before = { Math.min(existing.getMin(), newCol.getMin()), overlap[0] - 1L };
            if (before[0] <= before[1]) {
                trackedCols.add(this.cloneCol(cols, beforeCol, before));
            }
            final CTCol afterCol = (existing.getMax() > newCol.getMax()) ? existing : newCol;
            final long[] after = { overlap[1] + 1L, Math.max(existing.getMax(), newCol.getMax()) };
            if (after[0] <= after[1]) {
                trackedCols.add(this.cloneCol(cols, afterCol, after));
            }
        }
    }
    
    private CTCol cloneCol(final CTCols cols, final CTCol col, final long[] newRange) {
        final CTCol cloneCol = this.cloneCol(cols, col);
        cloneCol.setMin(newRange[0]);
        cloneCol.setMax(newRange[1]);
        return cloneCol;
    }
    
    private long[] getOverlap(final CTCol col1, final CTCol col2) {
        return this.getOverlappingRange(col1, col2);
    }
    
    private List<CTCol> getOverlappingCols(final CTCol newCol, final TreeSet<CTCol> trackedCols) {
        final CTCol lower = trackedCols.lower(newCol);
        final NavigableSet<CTCol> potentiallyOverlapping = (lower == null) ? trackedCols : trackedCols.tailSet(lower, this.overlaps(lower, newCol));
        final List<CTCol> overlapping = new ArrayList<CTCol>();
        for (final CTCol existing : potentiallyOverlapping) {
            if (!this.overlaps(newCol, existing)) {
                break;
            }
            overlapping.add(existing);
        }
        return overlapping;
    }
    
    private boolean overlaps(final CTCol col1, final CTCol col2) {
        return NumericRanges.getOverlappingType(this.toRange(col1), this.toRange(col2)) != -1;
    }
    
    private long[] getOverlappingRange(final CTCol col1, final CTCol col2) {
        return NumericRanges.getOverlappingRange(this.toRange(col1), this.toRange(col2));
    }
    
    private long[] toRange(final CTCol col) {
        return new long[] { col.getMin(), col.getMax() };
    }
    
    public static void sortColumns(final CTCols newCols) {
        final CTCol[] colArray = newCols.getColArray();
        Arrays.sort(colArray, CTColComparator.BY_MIN_MAX);
        newCols.setColArray(colArray);
    }
    
    public CTCol cloneCol(final CTCols cols, final CTCol col) {
        final CTCol newCol = cols.addNewCol();
        newCol.setMin(col.getMin());
        newCol.setMax(col.getMax());
        this.setColumnAttributes(col, newCol);
        return newCol;
    }
    
    public CTCol getColumn(final long index, final boolean splitColumns) {
        return this.getColumn1Based(index + 1L, splitColumns);
    }
    
    public CTCol getColumn1Based(final long index1, final boolean splitColumns) {
        final CTCols cols = this.worksheet.getColsArray(0);
        final CTCol[] colArray2;
        final CTCol[] colArray = colArray2 = cols.getColArray();
        for (final CTCol col : colArray2) {
            final long colMin = col.getMin();
            final long colMax = col.getMax();
            if (colMin <= index1 && colMax >= index1) {
                if (splitColumns) {
                    if (colMin < index1) {
                        this.insertCol(cols, colMin, index1 - 1L, new CTCol[] { col });
                    }
                    if (colMax > index1) {
                        this.insertCol(cols, index1 + 1L, colMax, new CTCol[] { col });
                    }
                    col.setMin(index1);
                    col.setMax(index1);
                }
                return col;
            }
        }
        return null;
    }
    
    private CTCol insertCol(final CTCols cols, final long min, final long max, final CTCol[] colsWithAttributes) {
        return this.insertCol(cols, min, max, colsWithAttributes, false, null);
    }
    
    private CTCol insertCol(final CTCols cols, final long min, final long max, final CTCol[] colsWithAttributes, final boolean ignoreExistsCheck, final CTCol overrideColumn) {
        if (ignoreExistsCheck || !this.columnExists(cols, min, max)) {
            final CTCol newCol = cols.insertNewCol(0);
            newCol.setMin(min);
            newCol.setMax(max);
            for (final CTCol col : colsWithAttributes) {
                this.setColumnAttributes(col, newCol);
            }
            if (overrideColumn != null) {
                this.setColumnAttributes(overrideColumn, newCol);
            }
            return newCol;
        }
        return null;
    }
    
    public boolean columnExists(final CTCols cols, final long index) {
        return this.columnExists1Based(cols, index + 1L);
    }
    
    private boolean columnExists1Based(final CTCols cols, final long index1) {
        for (final CTCol col : cols.getColArray()) {
            if (col.getMin() == index1) {
                return true;
            }
        }
        return false;
    }
    
    public void setColumnAttributes(final CTCol fromCol, final CTCol toCol) {
        if (fromCol.isSetBestFit()) {
            toCol.setBestFit(fromCol.getBestFit());
        }
        if (fromCol.isSetCustomWidth()) {
            toCol.setCustomWidth(fromCol.getCustomWidth());
        }
        if (fromCol.isSetHidden()) {
            toCol.setHidden(fromCol.getHidden());
        }
        if (fromCol.isSetStyle()) {
            toCol.setStyle(fromCol.getStyle());
        }
        if (fromCol.isSetWidth()) {
            toCol.setWidth(fromCol.getWidth());
        }
        if (fromCol.isSetCollapsed()) {
            toCol.setCollapsed(fromCol.getCollapsed());
        }
        if (fromCol.isSetPhonetic()) {
            toCol.setPhonetic(fromCol.getPhonetic());
        }
        if (fromCol.isSetOutlineLevel()) {
            toCol.setOutlineLevel(fromCol.getOutlineLevel());
        }
        toCol.setCollapsed(fromCol.isSetCollapsed());
    }
    
    public void setColBestFit(final long index, final boolean bestFit) {
        final CTCol col = this.getOrCreateColumn1Based(index + 1L, false);
        col.setBestFit(bestFit);
    }
    
    public void setCustomWidth(final long index, final boolean bestFit) {
        final CTCol col = this.getOrCreateColumn1Based(index + 1L, true);
        col.setCustomWidth(bestFit);
    }
    
    public void setColWidth(final long index, final double width) {
        final CTCol col = this.getOrCreateColumn1Based(index + 1L, true);
        col.setWidth(width);
    }
    
    public void setColHidden(final long index, final boolean hidden) {
        final CTCol col = this.getOrCreateColumn1Based(index + 1L, true);
        col.setHidden(hidden);
    }
    
    protected CTCol getOrCreateColumn1Based(final long index1, final boolean splitColumns) {
        CTCol col = this.getColumn1Based(index1, splitColumns);
        if (col == null) {
            col = this.worksheet.getColsArray(0).addNewCol();
            col.setMin(index1);
            col.setMax(index1);
        }
        return col;
    }
    
    public void setColDefaultStyle(final long index, final CellStyle style) {
        this.setColDefaultStyle(index, style.getIndex());
    }
    
    public void setColDefaultStyle(final long index, final int styleId) {
        final CTCol col = this.getOrCreateColumn1Based(index + 1L, true);
        col.setStyle((long)styleId);
    }
    
    public int getColDefaultStyle(final long index) {
        if (this.getColumn(index, false) != null) {
            return (int)this.getColumn(index, false).getStyle();
        }
        return -1;
    }
    
    private boolean columnExists(final CTCols cols, final long min, final long max) {
        for (final CTCol col : cols.getColList()) {
            if (col.getMin() == min && col.getMax() == max) {
                return true;
            }
        }
        return false;
    }
    
    public int getIndexOfColumn(final CTCols cols, final CTCol searchCol) {
        if (cols == null || searchCol == null) {
            return -1;
        }
        int i = 0;
        for (final CTCol col : cols.getColList()) {
            if (col.getMin() == searchCol.getMin() && col.getMax() == searchCol.getMax()) {
                return i;
            }
            ++i;
        }
        return -1;
    }
}
