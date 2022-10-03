package org.apache.poi.xssf.streaming;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.TreeSet;
import java.util.SortedSet;
import org.apache.poi.ss.util.SheetUtil;
import java.util.HashSet;
import java.util.HashMap;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.Set;
import java.util.Map;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.Internal;

@Internal
class AutoSizeColumnTracker
{
    private final int defaultCharWidth;
    private final DataFormatter dataFormatter;
    private final Map<Integer, ColumnWidthPair> maxColumnWidths;
    private final Set<Integer> untrackedColumns;
    private boolean trackAllColumns;
    
    public AutoSizeColumnTracker(final Sheet sheet) {
        this.dataFormatter = new DataFormatter();
        this.maxColumnWidths = new HashMap<Integer, ColumnWidthPair>();
        this.untrackedColumns = new HashSet<Integer>();
        this.defaultCharWidth = SheetUtil.getDefaultCharWidth(sheet.getWorkbook());
    }
    
    public SortedSet<Integer> getTrackedColumns() {
        final SortedSet<Integer> sorted = new TreeSet<Integer>(this.maxColumnWidths.keySet());
        return Collections.unmodifiableSortedSet(sorted);
    }
    
    public boolean isColumnTracked(final int column) {
        return this.trackAllColumns || this.maxColumnWidths.containsKey(column);
    }
    
    public boolean isAllColumnsTracked() {
        return this.trackAllColumns;
    }
    
    public void trackAllColumns() {
        this.trackAllColumns = true;
        this.untrackedColumns.clear();
    }
    
    public void untrackAllColumns() {
        this.trackAllColumns = false;
        this.maxColumnWidths.clear();
        this.untrackedColumns.clear();
    }
    
    public void trackColumns(final Collection<Integer> columns) {
        for (final int column : columns) {
            this.trackColumn(column);
        }
    }
    
    public boolean trackColumn(final int column) {
        this.untrackedColumns.remove(column);
        if (!this.maxColumnWidths.containsKey(column)) {
            this.maxColumnWidths.put(column, new ColumnWidthPair());
            return true;
        }
        return false;
    }
    
    private boolean implicitlyTrackColumn(final int column) {
        if (!this.untrackedColumns.contains(column)) {
            this.trackColumn(column);
            return true;
        }
        return false;
    }
    
    public boolean untrackColumns(final Collection<Integer> columns) {
        this.untrackedColumns.addAll(columns);
        return this.maxColumnWidths.keySet().removeAll(columns);
    }
    
    public boolean untrackColumn(final int column) {
        this.untrackedColumns.add(column);
        return this.maxColumnWidths.keySet().remove(column);
    }
    
    public int getBestFitColumnWidth(final int column, final boolean useMergedCells) {
        if (!this.maxColumnWidths.containsKey(column)) {
            if (!this.trackAllColumns) {
                final Throwable reason = new IllegalStateException("Column was never explicitly tracked and isAllColumnsTracked() is false (trackAllColumns() was never called or untrackAllColumns() was called after trackAllColumns() was called).");
                throw new IllegalStateException("Cannot get best fit column width on untracked column " + column + ". Either explicitly track the column or track all columns.", reason);
            }
            if (!this.implicitlyTrackColumn(column)) {
                final Throwable reason = new IllegalStateException("Column was explicitly untracked after trackAllColumns() was called.");
                throw new IllegalStateException("Cannot get best fit column width on explicitly untracked column " + column + ". Either explicitly track the column or track all columns.", reason);
            }
        }
        final double width = this.maxColumnWidths.get(column).getMaxColumnWidth(useMergedCells);
        return Math.toIntExact(Math.round(256.0 * width));
    }
    
    public void updateColumnWidths(final Row row) {
        this.implicitlyTrackColumnsInRow(row);
        if (this.maxColumnWidths.size() < row.getPhysicalNumberOfCells()) {
            for (final Map.Entry<Integer, ColumnWidthPair> e : this.maxColumnWidths.entrySet()) {
                final int column = e.getKey();
                final Cell cell = row.getCell(column);
                if (cell != null) {
                    final ColumnWidthPair pair = e.getValue();
                    this.updateColumnWidth(cell, pair);
                }
            }
        }
        else {
            for (final Cell cell2 : row) {
                final int column = cell2.getColumnIndex();
                if (this.maxColumnWidths.containsKey(column)) {
                    final ColumnWidthPair pair2 = this.maxColumnWidths.get(column);
                    this.updateColumnWidth(cell2, pair2);
                }
            }
        }
    }
    
    private void implicitlyTrackColumnsInRow(final Row row) {
        if (this.trackAllColumns) {
            for (final Cell cell : row) {
                final int column = cell.getColumnIndex();
                this.implicitlyTrackColumn(column);
            }
        }
    }
    
    private void updateColumnWidth(final Cell cell, final ColumnWidthPair pair) {
        final double unmergedWidth = SheetUtil.getCellWidth(cell, this.defaultCharWidth, this.dataFormatter, false);
        final double mergedWidth = SheetUtil.getCellWidth(cell, this.defaultCharWidth, this.dataFormatter, true);
        pair.setMaxColumnWidths(unmergedWidth, mergedWidth);
    }
    
    private static class ColumnWidthPair
    {
        private double withSkipMergedCells;
        private double withUseMergedCells;
        
        public ColumnWidthPair() {
            this(-1.0, -1.0);
        }
        
        public ColumnWidthPair(final double columnWidthSkipMergedCells, final double columnWidthUseMergedCells) {
            this.withSkipMergedCells = columnWidthSkipMergedCells;
            this.withUseMergedCells = columnWidthUseMergedCells;
        }
        
        public double getMaxColumnWidth(final boolean useMergedCells) {
            return useMergedCells ? this.withUseMergedCells : this.withSkipMergedCells;
        }
        
        public void setMaxColumnWidths(final double unmergedWidth, final double mergedWidth) {
            this.withUseMergedCells = Math.max(this.withUseMergedCells, mergedWidth);
            this.withSkipMergedCells = Math.max(this.withUseMergedCells, unmergedWidth);
        }
    }
}
