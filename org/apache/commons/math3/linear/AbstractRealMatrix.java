package org.apache.commons.math3.linear;

import java.util.Locale;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public abstract class AbstractRealMatrix extends RealLinearOperator implements RealMatrix
{
    private static final RealMatrixFormat DEFAULT_FORMAT;
    
    protected AbstractRealMatrix() {
    }
    
    protected AbstractRealMatrix(final int rowDimension, final int columnDimension) throws NotStrictlyPositiveException {
        if (rowDimension < 1) {
            throw new NotStrictlyPositiveException(rowDimension);
        }
        if (columnDimension < 1) {
            throw new NotStrictlyPositiveException(columnDimension);
        }
    }
    
    public RealMatrix add(final RealMatrix m) throws MatrixDimensionMismatchException {
        MatrixUtils.checkAdditionCompatible(this, m);
        final int rowCount = this.getRowDimension();
        final int columnCount = this.getColumnDimension();
        final RealMatrix out = this.createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, this.getEntry(row, col) + m.getEntry(row, col));
            }
        }
        return out;
    }
    
    public RealMatrix subtract(final RealMatrix m) throws MatrixDimensionMismatchException {
        MatrixUtils.checkSubtractionCompatible(this, m);
        final int rowCount = this.getRowDimension();
        final int columnCount = this.getColumnDimension();
        final RealMatrix out = this.createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, this.getEntry(row, col) - m.getEntry(row, col));
            }
        }
        return out;
    }
    
    public RealMatrix scalarAdd(final double d) {
        final int rowCount = this.getRowDimension();
        final int columnCount = this.getColumnDimension();
        final RealMatrix out = this.createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, this.getEntry(row, col) + d);
            }
        }
        return out;
    }
    
    public RealMatrix scalarMultiply(final double d) {
        final int rowCount = this.getRowDimension();
        final int columnCount = this.getColumnDimension();
        final RealMatrix out = this.createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, this.getEntry(row, col) * d);
            }
        }
        return out;
    }
    
    public RealMatrix multiply(final RealMatrix m) throws DimensionMismatchException {
        MatrixUtils.checkMultiplicationCompatible(this, m);
        final int nRows = this.getRowDimension();
        final int nCols = m.getColumnDimension();
        final int nSum = this.getColumnDimension();
        final RealMatrix out = this.createMatrix(nRows, nCols);
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                double sum = 0.0;
                for (int i = 0; i < nSum; ++i) {
                    sum += this.getEntry(row, i) * m.getEntry(i, col);
                }
                out.setEntry(row, col, sum);
            }
        }
        return out;
    }
    
    public RealMatrix preMultiply(final RealMatrix m) throws DimensionMismatchException {
        return m.multiply(this);
    }
    
    public RealMatrix power(final int p) throws NotPositiveException, NonSquareMatrixException {
        if (p < 0) {
            throw new NotPositiveException(LocalizedFormats.NOT_POSITIVE_EXPONENT, p);
        }
        if (!this.isSquare()) {
            throw new NonSquareMatrixException(this.getRowDimension(), this.getColumnDimension());
        }
        if (p == 0) {
            return MatrixUtils.createRealIdentityMatrix(this.getRowDimension());
        }
        if (p == 1) {
            return this.copy();
        }
        final int power = p - 1;
        final char[] binaryRepresentation = Integer.toBinaryString(power).toCharArray();
        final ArrayList<Integer> nonZeroPositions = new ArrayList<Integer>();
        int maxI = -1;
        for (int i = 0; i < binaryRepresentation.length; ++i) {
            if (binaryRepresentation[i] == '1') {
                final int pos = binaryRepresentation.length - i - 1;
                nonZeroPositions.add(pos);
                if (maxI == -1) {
                    maxI = pos;
                }
            }
        }
        final RealMatrix[] results = new RealMatrix[maxI + 1];
        results[0] = this.copy();
        for (int j = 1; j <= maxI; ++j) {
            results[j] = results[j - 1].multiply(results[j - 1]);
        }
        RealMatrix result = this.copy();
        for (final Integer k : nonZeroPositions) {
            result = result.multiply(results[k]);
        }
        return result;
    }
    
    public double[][] getData() {
        final double[][] data = new double[this.getRowDimension()][this.getColumnDimension()];
        for (int i = 0; i < data.length; ++i) {
            final double[] dataI = data[i];
            for (int j = 0; j < dataI.length; ++j) {
                dataI[j] = this.getEntry(i, j);
            }
        }
        return data;
    }
    
    public double getNorm() {
        return this.walkInColumnOrder(new RealMatrixPreservingVisitor() {
            private double endRow;
            private double columnSum;
            private double maxColSum;
            
            public void start(final int rows, final int columns, final int startRow, final int endRow, final int startColumn, final int endColumn) {
                this.endRow = endRow;
                this.columnSum = 0.0;
                this.maxColSum = 0.0;
            }
            
            public void visit(final int row, final int column, final double value) {
                this.columnSum += FastMath.abs(value);
                if (row == this.endRow) {
                    this.maxColSum = FastMath.max(this.maxColSum, this.columnSum);
                    this.columnSum = 0.0;
                }
            }
            
            public double end() {
                return this.maxColSum;
            }
        });
    }
    
    public double getFrobeniusNorm() {
        return this.walkInOptimizedOrder(new RealMatrixPreservingVisitor() {
            private double sum;
            
            public void start(final int rows, final int columns, final int startRow, final int endRow, final int startColumn, final int endColumn) {
                this.sum = 0.0;
            }
            
            public void visit(final int row, final int column, final double value) {
                this.sum += value * value;
            }
            
            public double end() {
                return FastMath.sqrt(this.sum);
            }
        });
    }
    
    public RealMatrix getSubMatrix(final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        final RealMatrix subMatrix = this.createMatrix(endRow - startRow + 1, endColumn - startColumn + 1);
        for (int i = startRow; i <= endRow; ++i) {
            for (int j = startColumn; j <= endColumn; ++j) {
                subMatrix.setEntry(i - startRow, j - startColumn, this.getEntry(i, j));
            }
        }
        return subMatrix;
    }
    
    public RealMatrix getSubMatrix(final int[] selectedRows, final int[] selectedColumns) throws NullArgumentException, NoDataException, OutOfRangeException {
        MatrixUtils.checkSubMatrixIndex(this, selectedRows, selectedColumns);
        final RealMatrix subMatrix = this.createMatrix(selectedRows.length, selectedColumns.length);
        subMatrix.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {
            @Override
            public double visit(final int row, final int column, final double value) {
                return AbstractRealMatrix.this.getEntry(selectedRows[row], selectedColumns[column]);
            }
        });
        return subMatrix;
    }
    
    public void copySubMatrix(final int startRow, final int endRow, final int startColumn, final int endColumn, final double[][] destination) throws OutOfRangeException, NumberIsTooSmallException, MatrixDimensionMismatchException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        final int rowsCount = endRow + 1 - startRow;
        final int columnsCount = endColumn + 1 - startColumn;
        if (destination.length < rowsCount || destination[0].length < columnsCount) {
            throw new MatrixDimensionMismatchException(destination.length, destination[0].length, rowsCount, columnsCount);
        }
        for (int i = 1; i < rowsCount; ++i) {
            if (destination[i].length < columnsCount) {
                throw new MatrixDimensionMismatchException(destination.length, destination[i].length, rowsCount, columnsCount);
            }
        }
        this.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            private int startRow;
            private int startColumn;
            
            @Override
            public void start(final int rows, final int columns, final int startRow, final int endRow, final int startColumn, final int endColumn) {
                this.startRow = startRow;
                this.startColumn = startColumn;
            }
            
            @Override
            public void visit(final int row, final int column, final double value) {
                destination[row - this.startRow][column - this.startColumn] = value;
            }
        }, startRow, endRow, startColumn, endColumn);
    }
    
    public void copySubMatrix(final int[] selectedRows, final int[] selectedColumns, final double[][] destination) throws OutOfRangeException, NullArgumentException, NoDataException, MatrixDimensionMismatchException {
        MatrixUtils.checkSubMatrixIndex(this, selectedRows, selectedColumns);
        final int nCols = selectedColumns.length;
        if (destination.length < selectedRows.length || destination[0].length < nCols) {
            throw new MatrixDimensionMismatchException(destination.length, destination[0].length, selectedRows.length, selectedColumns.length);
        }
        for (int i = 0; i < selectedRows.length; ++i) {
            final double[] destinationI = destination[i];
            if (destinationI.length < nCols) {
                throw new MatrixDimensionMismatchException(destination.length, destinationI.length, selectedRows.length, selectedColumns.length);
            }
            for (int j = 0; j < selectedColumns.length; ++j) {
                destinationI[j] = this.getEntry(selectedRows[i], selectedColumns[j]);
            }
        }
    }
    
    public void setSubMatrix(final double[][] subMatrix, final int row, final int column) throws NoDataException, OutOfRangeException, DimensionMismatchException, NullArgumentException {
        MathUtils.checkNotNull(subMatrix);
        final int nRows = subMatrix.length;
        if (nRows == 0) {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
        }
        final int nCols = subMatrix[0].length;
        if (nCols == 0) {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
        }
        for (int r = 1; r < nRows; ++r) {
            if (subMatrix[r].length != nCols) {
                throw new DimensionMismatchException(nCols, subMatrix[r].length);
            }
        }
        MatrixUtils.checkRowIndex(this, row);
        MatrixUtils.checkColumnIndex(this, column);
        MatrixUtils.checkRowIndex(this, nRows + row - 1);
        MatrixUtils.checkColumnIndex(this, nCols + column - 1);
        for (int i = 0; i < nRows; ++i) {
            for (int j = 0; j < nCols; ++j) {
                this.setEntry(row + i, column + j, subMatrix[i][j]);
            }
        }
    }
    
    public RealMatrix getRowMatrix(final int row) throws OutOfRangeException {
        MatrixUtils.checkRowIndex(this, row);
        final int nCols = this.getColumnDimension();
        final RealMatrix out = this.createMatrix(1, nCols);
        for (int i = 0; i < nCols; ++i) {
            out.setEntry(0, i, this.getEntry(row, i));
        }
        return out;
    }
    
    public void setRowMatrix(final int row, final RealMatrix matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkRowIndex(this, row);
        final int nCols = this.getColumnDimension();
        if (matrix.getRowDimension() != 1 || matrix.getColumnDimension() != nCols) {
            throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), 1, nCols);
        }
        for (int i = 0; i < nCols; ++i) {
            this.setEntry(row, i, matrix.getEntry(0, i));
        }
    }
    
    public RealMatrix getColumnMatrix(final int column) throws OutOfRangeException {
        MatrixUtils.checkColumnIndex(this, column);
        final int nRows = this.getRowDimension();
        final RealMatrix out = this.createMatrix(nRows, 1);
        for (int i = 0; i < nRows; ++i) {
            out.setEntry(i, 0, this.getEntry(i, column));
        }
        return out;
    }
    
    public void setColumnMatrix(final int column, final RealMatrix matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkColumnIndex(this, column);
        final int nRows = this.getRowDimension();
        if (matrix.getRowDimension() != nRows || matrix.getColumnDimension() != 1) {
            throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), nRows, 1);
        }
        for (int i = 0; i < nRows; ++i) {
            this.setEntry(i, column, matrix.getEntry(i, 0));
        }
    }
    
    public RealVector getRowVector(final int row) throws OutOfRangeException {
        return new ArrayRealVector(this.getRow(row), false);
    }
    
    public void setRowVector(final int row, final RealVector vector) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkRowIndex(this, row);
        final int nCols = this.getColumnDimension();
        if (vector.getDimension() != nCols) {
            throw new MatrixDimensionMismatchException(1, vector.getDimension(), 1, nCols);
        }
        for (int i = 0; i < nCols; ++i) {
            this.setEntry(row, i, vector.getEntry(i));
        }
    }
    
    public RealVector getColumnVector(final int column) throws OutOfRangeException {
        return new ArrayRealVector(this.getColumn(column), false);
    }
    
    public void setColumnVector(final int column, final RealVector vector) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkColumnIndex(this, column);
        final int nRows = this.getRowDimension();
        if (vector.getDimension() != nRows) {
            throw new MatrixDimensionMismatchException(vector.getDimension(), 1, nRows, 1);
        }
        for (int i = 0; i < nRows; ++i) {
            this.setEntry(i, column, vector.getEntry(i));
        }
    }
    
    public double[] getRow(final int row) throws OutOfRangeException {
        MatrixUtils.checkRowIndex(this, row);
        final int nCols = this.getColumnDimension();
        final double[] out = new double[nCols];
        for (int i = 0; i < nCols; ++i) {
            out[i] = this.getEntry(row, i);
        }
        return out;
    }
    
    public void setRow(final int row, final double[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkRowIndex(this, row);
        final int nCols = this.getColumnDimension();
        if (array.length != nCols) {
            throw new MatrixDimensionMismatchException(1, array.length, 1, nCols);
        }
        for (int i = 0; i < nCols; ++i) {
            this.setEntry(row, i, array[i]);
        }
    }
    
    public double[] getColumn(final int column) throws OutOfRangeException {
        MatrixUtils.checkColumnIndex(this, column);
        final int nRows = this.getRowDimension();
        final double[] out = new double[nRows];
        for (int i = 0; i < nRows; ++i) {
            out[i] = this.getEntry(i, column);
        }
        return out;
    }
    
    public void setColumn(final int column, final double[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
        MatrixUtils.checkColumnIndex(this, column);
        final int nRows = this.getRowDimension();
        if (array.length != nRows) {
            throw new MatrixDimensionMismatchException(array.length, 1, nRows, 1);
        }
        for (int i = 0; i < nRows; ++i) {
            this.setEntry(i, column, array[i]);
        }
    }
    
    public void addToEntry(final int row, final int column, final double increment) throws OutOfRangeException {
        MatrixUtils.checkMatrixIndex(this, row, column);
        this.setEntry(row, column, this.getEntry(row, column) + increment);
    }
    
    public void multiplyEntry(final int row, final int column, final double factor) throws OutOfRangeException {
        MatrixUtils.checkMatrixIndex(this, row, column);
        this.setEntry(row, column, this.getEntry(row, column) * factor);
    }
    
    public RealMatrix transpose() {
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        final RealMatrix out = this.createMatrix(nCols, nRows);
        this.walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            @Override
            public void visit(final int row, final int column, final double value) {
                out.setEntry(column, row, value);
            }
        });
        return out;
    }
    
    public boolean isSquare() {
        return this.getColumnDimension() == this.getRowDimension();
    }
    
    @Override
    public abstract int getRowDimension();
    
    @Override
    public abstract int getColumnDimension();
    
    public double getTrace() throws NonSquareMatrixException {
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        if (nRows != nCols) {
            throw new NonSquareMatrixException(nRows, nCols);
        }
        double trace = 0.0;
        for (int i = 0; i < nRows; ++i) {
            trace += this.getEntry(i, i);
        }
        return trace;
    }
    
    public double[] operate(final double[] v) throws DimensionMismatchException {
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        if (v.length != nCols) {
            throw new DimensionMismatchException(v.length, nCols);
        }
        final double[] out = new double[nRows];
        for (int row = 0; row < nRows; ++row) {
            double sum = 0.0;
            for (int i = 0; i < nCols; ++i) {
                sum += this.getEntry(row, i) * v[i];
            }
            out[row] = sum;
        }
        return out;
    }
    
    @Override
    public RealVector operate(final RealVector v) throws DimensionMismatchException {
        try {
            return new ArrayRealVector(this.operate(((ArrayRealVector)v).getDataRef()), false);
        }
        catch (final ClassCastException cce) {
            final int nRows = this.getRowDimension();
            final int nCols = this.getColumnDimension();
            if (v.getDimension() != nCols) {
                throw new DimensionMismatchException(v.getDimension(), nCols);
            }
            final double[] out = new double[nRows];
            for (int row = 0; row < nRows; ++row) {
                double sum = 0.0;
                for (int i = 0; i < nCols; ++i) {
                    sum += this.getEntry(row, i) * v.getEntry(i);
                }
                out[row] = sum;
            }
            return new ArrayRealVector(out, false);
        }
    }
    
    public double[] preMultiply(final double[] v) throws DimensionMismatchException {
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        if (v.length != nRows) {
            throw new DimensionMismatchException(v.length, nRows);
        }
        final double[] out = new double[nCols];
        for (int col = 0; col < nCols; ++col) {
            double sum = 0.0;
            for (int i = 0; i < nRows; ++i) {
                sum += this.getEntry(i, col) * v[i];
            }
            out[col] = sum;
        }
        return out;
    }
    
    public RealVector preMultiply(final RealVector v) throws DimensionMismatchException {
        try {
            return new ArrayRealVector(this.preMultiply(((ArrayRealVector)v).getDataRef()), false);
        }
        catch (final ClassCastException cce) {
            final int nRows = this.getRowDimension();
            final int nCols = this.getColumnDimension();
            if (v.getDimension() != nRows) {
                throw new DimensionMismatchException(v.getDimension(), nRows);
            }
            final double[] out = new double[nCols];
            for (int col = 0; col < nCols; ++col) {
                double sum = 0.0;
                for (int i = 0; i < nRows; ++i) {
                    sum += this.getEntry(i, col) * v.getEntry(i);
                }
                out[col] = sum;
            }
            return new ArrayRealVector(out, false);
        }
    }
    
    public double walkInRowOrder(final RealMatrixChangingVisitor visitor) {
        final int rows = this.getRowDimension();
        final int columns = this.getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < columns; ++column) {
                final double oldValue = this.getEntry(row, column);
                final double newValue = visitor.visit(row, column, oldValue);
                this.setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }
    
    public double walkInRowOrder(final RealMatrixPreservingVisitor visitor) {
        final int rows = this.getRowDimension();
        final int columns = this.getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < columns; ++column) {
                visitor.visit(row, column, this.getEntry(row, column));
            }
        }
        return visitor.end();
    }
    
    public double walkInRowOrder(final RealMatrixChangingVisitor visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(this.getRowDimension(), this.getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int row = startRow; row <= endRow; ++row) {
            for (int column = startColumn; column <= endColumn; ++column) {
                final double oldValue = this.getEntry(row, column);
                final double newValue = visitor.visit(row, column, oldValue);
                this.setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }
    
    public double walkInRowOrder(final RealMatrixPreservingVisitor visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(this.getRowDimension(), this.getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int row = startRow; row <= endRow; ++row) {
            for (int column = startColumn; column <= endColumn; ++column) {
                visitor.visit(row, column, this.getEntry(row, column));
            }
        }
        return visitor.end();
    }
    
    public double walkInColumnOrder(final RealMatrixChangingVisitor visitor) {
        final int rows = this.getRowDimension();
        final int columns = this.getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int column = 0; column < columns; ++column) {
            for (int row = 0; row < rows; ++row) {
                final double oldValue = this.getEntry(row, column);
                final double newValue = visitor.visit(row, column, oldValue);
                this.setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }
    
    public double walkInColumnOrder(final RealMatrixPreservingVisitor visitor) {
        final int rows = this.getRowDimension();
        final int columns = this.getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int column = 0; column < columns; ++column) {
            for (int row = 0; row < rows; ++row) {
                visitor.visit(row, column, this.getEntry(row, column));
            }
        }
        return visitor.end();
    }
    
    public double walkInColumnOrder(final RealMatrixChangingVisitor visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(this.getRowDimension(), this.getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int column = startColumn; column <= endColumn; ++column) {
            for (int row = startRow; row <= endRow; ++row) {
                final double oldValue = this.getEntry(row, column);
                final double newValue = visitor.visit(row, column, oldValue);
                this.setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }
    
    public double walkInColumnOrder(final RealMatrixPreservingVisitor visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(this.getRowDimension(), this.getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int column = startColumn; column <= endColumn; ++column) {
            for (int row = startRow; row <= endRow; ++row) {
                visitor.visit(row, column, this.getEntry(row, column));
            }
        }
        return visitor.end();
    }
    
    public double walkInOptimizedOrder(final RealMatrixChangingVisitor visitor) {
        return this.walkInRowOrder(visitor);
    }
    
    public double walkInOptimizedOrder(final RealMatrixPreservingVisitor visitor) {
        return this.walkInRowOrder(visitor);
    }
    
    public double walkInOptimizedOrder(final RealMatrixChangingVisitor visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        return this.walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
    }
    
    public double walkInOptimizedOrder(final RealMatrixPreservingVisitor visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        return this.walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
    }
    
    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        final String fullClassName = this.getClass().getName();
        final String shortClassName = fullClassName.substring(fullClassName.lastIndexOf(46) + 1);
        res.append(shortClassName);
        res.append(AbstractRealMatrix.DEFAULT_FORMAT.format(this));
        return res.toString();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof RealMatrix)) {
            return false;
        }
        final RealMatrix m = (RealMatrix)object;
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        if (m.getColumnDimension() != nCols || m.getRowDimension() != nRows) {
            return false;
        }
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                if (this.getEntry(row, col) != m.getEntry(row, col)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int ret = 7;
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        ret = ret * 31 + nRows;
        ret = ret * 31 + nCols;
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                ret = ret * 31 + (11 * (row + 1) + 17 * (col + 1)) * MathUtils.hash(this.getEntry(row, col));
            }
        }
        return ret;
    }
    
    public abstract RealMatrix createMatrix(final int p0, final int p1) throws NotStrictlyPositiveException;
    
    public abstract RealMatrix copy();
    
    public abstract double getEntry(final int p0, final int p1) throws OutOfRangeException;
    
    public abstract void setEntry(final int p0, final int p1, final double p2) throws OutOfRangeException;
    
    static {
        DEFAULT_FORMAT = RealMatrixFormat.getInstance(Locale.US);
        AbstractRealMatrix.DEFAULT_FORMAT.getFormat().setMinimumFractionDigits(1);
    }
}
