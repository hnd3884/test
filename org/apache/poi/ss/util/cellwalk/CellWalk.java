package org.apache.poi.ss.util.cellwalk;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Sheet;

public class CellWalk
{
    private Sheet sheet;
    private CellRangeAddress range;
    private boolean traverseEmptyCells;
    
    public CellWalk(final Sheet sheet, final CellRangeAddress range) {
        this.sheet = sheet;
        this.range = range;
        this.traverseEmptyCells = false;
    }
    
    public boolean isTraverseEmptyCells() {
        return this.traverseEmptyCells;
    }
    
    public void setTraverseEmptyCells(final boolean traverseEmptyCells) {
        this.traverseEmptyCells = traverseEmptyCells;
    }
    
    public void traverse(final CellHandler handler) {
        final int firstRow = this.range.getFirstRow();
        final int lastRow = this.range.getLastRow();
        final int firstColumn = this.range.getFirstColumn();
        final int lastColumn = this.range.getLastColumn();
        final int width = lastColumn - firstColumn + 1;
        final SimpleCellWalkContext ctx = new SimpleCellWalkContext();
        Row currentRow = null;
        Cell currentCell = null;
        ctx.rowNumber = firstRow;
        while (ctx.rowNumber <= lastRow) {
            currentRow = this.sheet.getRow(ctx.rowNumber);
            if (currentRow != null) {
                ctx.colNumber = firstColumn;
                while (ctx.colNumber <= lastColumn) {
                    currentCell = currentRow.getCell(ctx.colNumber);
                    if (currentCell != null) {
                        if (!this.isEmpty(currentCell) || this.traverseEmptyCells) {
                            final long rowSize = ArithmeticUtils.mulAndCheck((long)ArithmeticUtils.subAndCheck(ctx.rowNumber, firstRow), (long)width);
                            ctx.ordinalNumber = ArithmeticUtils.addAndCheck(rowSize, (long)(ctx.colNumber - firstColumn + 1));
                            handler.onCell(currentCell, ctx);
                        }
                    }
                    final SimpleCellWalkContext simpleCellWalkContext = ctx;
                    ++simpleCellWalkContext.colNumber;
                }
            }
            final SimpleCellWalkContext simpleCellWalkContext2 = ctx;
            ++simpleCellWalkContext2.rowNumber;
        }
    }
    
    private boolean isEmpty(final Cell cell) {
        return cell.getCellType() == CellType.BLANK;
    }
    
    private static class SimpleCellWalkContext implements CellWalkContext
    {
        public long ordinalNumber;
        public int rowNumber;
        public int colNumber;
        
        @Override
        public long getOrdinalNumber() {
            return this.ordinalNumber;
        }
        
        @Override
        public int getRowNumber() {
            return this.rowNumber;
        }
        
        @Override
        public int getColumnNumber() {
            return this.colNumber;
        }
    }
}
