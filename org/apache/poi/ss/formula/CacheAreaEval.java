package org.apache.poi.ss.formula;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.ptg.AreaI;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.AreaEvalBase;

public final class CacheAreaEval extends AreaEvalBase
{
    private final ValueEval[] _values;
    
    public CacheAreaEval(final AreaI ptg, final ValueEval[] values) {
        super(ptg);
        this._values = values;
    }
    
    public CacheAreaEval(final int firstRow, final int firstColumn, final int lastRow, final int lastColumn, final ValueEval[] values) {
        super(firstRow, firstColumn, lastRow, lastColumn);
        this._values = values;
    }
    
    @Override
    public ValueEval getRelativeValue(final int relativeRowIndex, final int relativeColumnIndex) {
        return this.getRelativeValue(-1, relativeRowIndex, relativeColumnIndex);
    }
    
    @Override
    public ValueEval getRelativeValue(final int sheetIndex, final int relativeRowIndex, final int relativeColumnIndex) {
        final int oneDimensionalIndex = relativeRowIndex * this.getWidth() + relativeColumnIndex;
        return this._values[oneDimensionalIndex];
    }
    
    @Override
    public AreaEval offset(final int relFirstRowIx, final int relLastRowIx, final int relFirstColIx, final int relLastColIx) {
        final AreaI area = new AreaI.OffsetArea(this.getFirstRow(), this.getFirstColumn(), relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);
        final int height = area.getLastRow() - area.getFirstRow() + 1;
        final int width = area.getLastColumn() - area.getFirstColumn() + 1;
        final ValueEval[] newVals = new ValueEval[height * width];
        final int startRow = area.getFirstRow() - this.getFirstRow();
        final int startCol = area.getFirstColumn() - this.getFirstColumn();
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                ValueEval temp;
                if (startRow + j > this.getLastRow() || startCol + i > this.getLastColumn()) {
                    temp = BlankEval.instance;
                }
                else {
                    temp = this._values[(startRow + j) * this.getWidth() + (startCol + i)];
                }
                newVals[j * width + i] = temp;
            }
        }
        return new CacheAreaEval(area, newVals);
    }
    
    @Override
    public TwoDEval getRow(final int rowIndex) {
        if (rowIndex >= this.getHeight()) {
            throw new IllegalArgumentException("Invalid rowIndex " + rowIndex + ".  Allowable range is (0.." + this.getHeight() + ").");
        }
        final int absRowIndex = this.getFirstRow() + rowIndex;
        final ValueEval[] values = new ValueEval[this.getWidth()];
        for (int i = 0; i < values.length; ++i) {
            values[i] = this.getRelativeValue(rowIndex, i);
        }
        return new CacheAreaEval(absRowIndex, this.getFirstColumn(), absRowIndex, this.getLastColumn(), values);
    }
    
    @Override
    public TwoDEval getColumn(final int columnIndex) {
        if (columnIndex >= this.getWidth()) {
            throw new IllegalArgumentException("Invalid columnIndex " + columnIndex + ".  Allowable range is (0.." + this.getWidth() + ").");
        }
        final int absColIndex = this.getFirstColumn() + columnIndex;
        final ValueEval[] values = new ValueEval[this.getHeight()];
        for (int i = 0; i < values.length; ++i) {
            values[i] = this.getRelativeValue(i, columnIndex);
        }
        return new CacheAreaEval(this.getFirstRow(), absColIndex, this.getLastRow(), absColIndex, values);
    }
    
    @Override
    public String toString() {
        final CellReference crA = new CellReference(this.getFirstRow(), this.getFirstColumn());
        final CellReference crB = new CellReference(this.getLastRow(), this.getLastColumn());
        return this.getClass().getName() + "[" + crA.formatAsString() + ':' + crB.formatAsString() + "]";
    }
}
