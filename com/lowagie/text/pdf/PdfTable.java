package com.lowagie.text.pdf;

import java.util.Iterator;
import java.util.Collection;
import com.lowagie.text.Cell;
import com.lowagie.text.Row;
import com.lowagie.text.Table;
import java.util.ArrayList;
import com.lowagie.text.Rectangle;

public class PdfTable extends Rectangle
{
    private int columns;
    private ArrayList headercells;
    private ArrayList cells;
    protected Table table;
    protected float[] positions;
    
    PdfTable(final Table table, final float left, final float right, final float top) {
        super(left, top, right, top);
        (this.table = table).complete();
        this.cloneNonPositionParameters(table);
        this.columns = table.getColumns();
        this.positions = table.getWidths(left, right - left);
        this.setLeft(this.positions[0]);
        this.setRight(this.positions[this.positions.length - 1]);
        this.headercells = new ArrayList();
        this.cells = new ArrayList();
        this.updateRowAdditionsInternal();
    }
    
    void updateRowAdditions() {
        this.table.complete();
        this.updateRowAdditionsInternal();
        this.table.deleteAllRows();
    }
    
    private void updateRowAdditionsInternal() {
        final int prevRows = this.rows();
        int rowNumber = 0;
        int groupNumber = 0;
        final int firstDataRow = this.table.getLastHeaderRow() + 1;
        final ArrayList newCells = new ArrayList();
        final int rows = this.table.size() + 1;
        final float[] offsets = new float[rows];
        for (int i = 0; i < rows; ++i) {
            offsets[i] = this.getBottom();
        }
        final Iterator rowIterator = this.table.iterator();
        while (rowIterator.hasNext()) {
            boolean groupChange = false;
            final Row row = rowIterator.next();
            if (row.isEmpty()) {
                if (rowNumber < rows - 1 && offsets[rowNumber + 1] > offsets[rowNumber]) {
                    offsets[rowNumber + 1] = offsets[rowNumber];
                }
            }
            else {
                for (int j = 0; j < row.getColumns(); ++j) {
                    final Cell cell = (Cell)row.getCell(j);
                    if (cell != null) {
                        final PdfCell currentCell = new PdfCell(cell, rowNumber + prevRows, this.positions[j], this.positions[j + cell.getColspan()], offsets[rowNumber], this.cellspacing(), this.cellpadding());
                        if (rowNumber < firstDataRow) {
                            currentCell.setHeader();
                            this.headercells.add(currentCell);
                            if (!this.table.isNotAddedYet()) {
                                continue;
                            }
                        }
                        try {
                            if (offsets[rowNumber] - currentCell.getHeight() - this.cellpadding() < offsets[rowNumber + currentCell.rowspan()]) {
                                offsets[rowNumber + currentCell.rowspan()] = offsets[rowNumber] - currentCell.getHeight() - this.cellpadding();
                            }
                        }
                        catch (final ArrayIndexOutOfBoundsException aioobe) {
                            if (offsets[rowNumber] - currentCell.getHeight() < offsets[rows - 1]) {
                                offsets[rows - 1] = offsets[rowNumber] - currentCell.getHeight();
                            }
                        }
                        currentCell.setGroupNumber(groupNumber);
                        groupChange |= cell.getGroupChange();
                        newCells.add(currentCell);
                    }
                }
            }
            ++rowNumber;
            if (groupChange) {
                ++groupNumber;
            }
        }
        for (int n = newCells.size(), j = 0; j < n; ++j) {
            final PdfCell currentCell = newCells.get(j);
            try {
                currentCell.setBottom(offsets[currentCell.rownumber() - prevRows + currentCell.rowspan()]);
            }
            catch (final ArrayIndexOutOfBoundsException aioobe) {
                currentCell.setBottom(offsets[rows - 1]);
            }
        }
        this.cells.addAll(newCells);
        this.setBottom(offsets[rows - 1]);
    }
    
    int rows() {
        return this.cells.isEmpty() ? 0 : (this.cells.get(this.cells.size() - 1).rownumber() + 1);
    }
    
    @Override
    public int type() {
        return 22;
    }
    
    ArrayList getHeaderCells() {
        return this.headercells;
    }
    
    boolean hasHeader() {
        return !this.headercells.isEmpty();
    }
    
    ArrayList getCells() {
        return this.cells;
    }
    
    int columns() {
        return this.columns;
    }
    
    final float cellpadding() {
        return this.table.getPadding();
    }
    
    final float cellspacing() {
        return this.table.getSpacing();
    }
    
    public final boolean hasToFitPageTable() {
        return this.table.isTableFitsPage();
    }
    
    public final boolean hasToFitPageCells() {
        return this.table.isCellsFitPage();
    }
    
    public float getOffset() {
        return this.table.getOffset();
    }
}
