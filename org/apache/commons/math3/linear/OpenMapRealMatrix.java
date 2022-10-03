package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.util.OpenIntToDoubleHashMap;
import java.io.Serializable;

public class OpenMapRealMatrix extends AbstractRealMatrix implements SparseRealMatrix, Serializable
{
    private static final long serialVersionUID = -5962461716457143437L;
    private final int rows;
    private final int columns;
    private final OpenIntToDoubleHashMap entries;
    
    public OpenMapRealMatrix(final int rowDimension, final int columnDimension) throws NotStrictlyPositiveException, NumberIsTooLargeException {
        super(rowDimension, columnDimension);
        final long lRow = rowDimension;
        final long lCol = columnDimension;
        if (lRow * lCol >= 2147483647L) {
            throw new NumberIsTooLargeException(lRow * lCol, Integer.MAX_VALUE, false);
        }
        this.rows = rowDimension;
        this.columns = columnDimension;
        this.entries = new OpenIntToDoubleHashMap(0.0);
    }
    
    public OpenMapRealMatrix(final OpenMapRealMatrix matrix) {
        this.rows = matrix.rows;
        this.columns = matrix.columns;
        this.entries = new OpenIntToDoubleHashMap(matrix.entries);
    }
    
    @Override
    public OpenMapRealMatrix copy() {
        return new OpenMapRealMatrix(this);
    }
    
    @Override
    public OpenMapRealMatrix createMatrix(final int rowDimension, final int columnDimension) throws NotStrictlyPositiveException, NumberIsTooLargeException {
        return new OpenMapRealMatrix(rowDimension, columnDimension);
    }
    
    @Override
    public int getColumnDimension() {
        return this.columns;
    }
    
    public OpenMapRealMatrix add(final OpenMapRealMatrix m) throws MatrixDimensionMismatchException {
        MatrixUtils.checkAdditionCompatible(this, m);
        final OpenMapRealMatrix out = new OpenMapRealMatrix(this);
        final OpenIntToDoubleHashMap.Iterator iterator = m.entries.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            final int row = iterator.key() / this.columns;
            final int col = iterator.key() - row * this.columns;
            out.setEntry(row, col, this.getEntry(row, col) + iterator.value());
        }
        return out;
    }
    
    @Override
    public OpenMapRealMatrix subtract(final RealMatrix m) throws MatrixDimensionMismatchException {
        try {
            return this.subtract((OpenMapRealMatrix)m);
        }
        catch (final ClassCastException cce) {
            return (OpenMapRealMatrix)super.subtract(m);
        }
    }
    
    public OpenMapRealMatrix subtract(final OpenMapRealMatrix m) throws MatrixDimensionMismatchException {
        MatrixUtils.checkAdditionCompatible(this, m);
        final OpenMapRealMatrix out = new OpenMapRealMatrix(this);
        final OpenIntToDoubleHashMap.Iterator iterator = m.entries.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            final int row = iterator.key() / this.columns;
            final int col = iterator.key() - row * this.columns;
            out.setEntry(row, col, this.getEntry(row, col) - iterator.value());
        }
        return out;
    }
    
    @Override
    public RealMatrix multiply(final RealMatrix m) throws DimensionMismatchException, NumberIsTooLargeException {
        try {
            return this.multiply((OpenMapRealMatrix)m);
        }
        catch (final ClassCastException cce) {
            MatrixUtils.checkMultiplicationCompatible(this, m);
            final int outCols = m.getColumnDimension();
            final BlockRealMatrix out = new BlockRealMatrix(this.rows, outCols);
            final OpenIntToDoubleHashMap.Iterator iterator = this.entries.iterator();
            while (iterator.hasNext()) {
                iterator.advance();
                final double value = iterator.value();
                final int key = iterator.key();
                final int i = key / this.columns;
                final int k = key % this.columns;
                for (int j = 0; j < outCols; ++j) {
                    out.addToEntry(i, j, value * m.getEntry(k, j));
                }
            }
            return out;
        }
    }
    
    public OpenMapRealMatrix multiply(final OpenMapRealMatrix m) throws DimensionMismatchException, NumberIsTooLargeException {
        MatrixUtils.checkMultiplicationCompatible(this, m);
        final int outCols = m.getColumnDimension();
        final OpenMapRealMatrix out = new OpenMapRealMatrix(this.rows, outCols);
        final OpenIntToDoubleHashMap.Iterator iterator = this.entries.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            final double value = iterator.value();
            final int key = iterator.key();
            final int i = key / this.columns;
            final int k = key % this.columns;
            for (int j = 0; j < outCols; ++j) {
                final int rightKey = m.computeKey(k, j);
                if (m.entries.containsKey(rightKey)) {
                    final int outKey = out.computeKey(i, j);
                    final double outValue = out.entries.get(outKey) + value * m.entries.get(rightKey);
                    if (outValue == 0.0) {
                        out.entries.remove(outKey);
                    }
                    else {
                        out.entries.put(outKey, outValue);
                    }
                }
            }
        }
        return out;
    }
    
    @Override
    public double getEntry(final int row, final int column) throws OutOfRangeException {
        MatrixUtils.checkRowIndex(this, row);
        MatrixUtils.checkColumnIndex(this, column);
        return this.entries.get(this.computeKey(row, column));
    }
    
    @Override
    public int getRowDimension() {
        return this.rows;
    }
    
    @Override
    public void setEntry(final int row, final int column, final double value) throws OutOfRangeException {
        MatrixUtils.checkRowIndex(this, row);
        MatrixUtils.checkColumnIndex(this, column);
        if (value == 0.0) {
            this.entries.remove(this.computeKey(row, column));
        }
        else {
            this.entries.put(this.computeKey(row, column), value);
        }
    }
    
    @Override
    public void addToEntry(final int row, final int column, final double increment) throws OutOfRangeException {
        MatrixUtils.checkRowIndex(this, row);
        MatrixUtils.checkColumnIndex(this, column);
        final int key = this.computeKey(row, column);
        final double value = this.entries.get(key) + increment;
        if (value == 0.0) {
            this.entries.remove(key);
        }
        else {
            this.entries.put(key, value);
        }
    }
    
    @Override
    public void multiplyEntry(final int row, final int column, final double factor) throws OutOfRangeException {
        MatrixUtils.checkRowIndex(this, row);
        MatrixUtils.checkColumnIndex(this, column);
        final int key = this.computeKey(row, column);
        final double value = this.entries.get(key) * factor;
        if (value == 0.0) {
            this.entries.remove(key);
        }
        else {
            this.entries.put(key, value);
        }
    }
    
    private int computeKey(final int row, final int column) {
        return row * this.columns + column;
    }
}
