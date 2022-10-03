package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

public abstract class AbstractFieldMatrix<T extends FieldElement<T>> implements FieldMatrix<T>
{
    private final Field<T> field;
    
    protected AbstractFieldMatrix() {
        this.field = null;
    }
    
    protected AbstractFieldMatrix(final Field<T> field) {
        this.field = field;
    }
    
    protected AbstractFieldMatrix(final Field<T> field, final int rowDimension, final int columnDimension) throws NotStrictlyPositiveException {
        if (rowDimension <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.DIMENSION, rowDimension);
        }
        if (columnDimension <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.DIMENSION, columnDimension);
        }
        this.field = field;
    }
    
    protected static <T extends FieldElement<T>> Field<T> extractField(final T[][] d) throws NoDataException, NullArgumentException {
        if (d == null) {
            throw new NullArgumentException();
        }
        if (d.length == 0) {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
        }
        if (d[0].length == 0) {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
        }
        return d[0][0].getField();
    }
    
    protected static <T extends FieldElement<T>> Field<T> extractField(final T[] d) throws NoDataException {
        if (d.length == 0) {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
        }
        return d[0].getField();
    }
    
    @Deprecated
    protected static <T extends FieldElement<T>> T[][] buildArray(final Field<T> field, final int rows, final int columns) {
        return MathArrays.buildArray(field, rows, columns);
    }
    
    @Deprecated
    protected static <T extends FieldElement<T>> T[] buildArray(final Field<T> field, final int length) {
        return MathArrays.buildArray(field, length);
    }
    
    public Field<T> getField() {
        return this.field;
    }
    
    public abstract FieldMatrix<T> createMatrix(final int p0, final int p1) throws NotStrictlyPositiveException;
    
    public abstract FieldMatrix<T> copy();
    
    public FieldMatrix<T> add(final FieldMatrix<T> m) throws MatrixDimensionMismatchException {
        this.checkAdditionCompatible(m);
        final int rowCount = this.getRowDimension();
        final int columnCount = this.getColumnDimension();
        final FieldMatrix<T> out = this.createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, (T)this.getEntry(row, col).add((FieldElement<FieldElement<FieldElement<T>>>)m.getEntry(row, col)));
            }
        }
        return out;
    }
    
    public FieldMatrix<T> subtract(final FieldMatrix<T> m) throws MatrixDimensionMismatchException {
        this.checkSubtractionCompatible(m);
        final int rowCount = this.getRowDimension();
        final int columnCount = this.getColumnDimension();
        final FieldMatrix<T> out = this.createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, (T)this.getEntry(row, col).subtract((FieldElement<FieldElement<FieldElement<T>>>)m.getEntry(row, col)));
            }
        }
        return out;
    }
    
    public FieldMatrix<T> scalarAdd(final T d) {
        final int rowCount = this.getRowDimension();
        final int columnCount = this.getColumnDimension();
        final FieldMatrix<T> out = this.createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, (T)this.getEntry(row, col).add((FieldElement<FieldElement<FieldElement<T>>>)d));
            }
        }
        return out;
    }
    
    public FieldMatrix<T> scalarMultiply(final T d) {
        final int rowCount = this.getRowDimension();
        final int columnCount = this.getColumnDimension();
        final FieldMatrix<T> out = this.createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, (T)this.getEntry(row, col).multiply((FieldElement<FieldElement<FieldElement<T>>>)d));
            }
        }
        return out;
    }
    
    public FieldMatrix<T> multiply(final FieldMatrix<T> m) throws DimensionMismatchException {
        this.checkMultiplicationCompatible(m);
        final int nRows = this.getRowDimension();
        final int nCols = m.getColumnDimension();
        final int nSum = this.getColumnDimension();
        final FieldMatrix<T> out = this.createMatrix(nRows, nCols);
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                T sum = this.field.getZero();
                for (int i = 0; i < nSum; ++i) {
                    sum = sum.add((T)this.getEntry(row, i).multiply((FieldElement<FieldElement<FieldElement>>)m.getEntry(i, col)));
                }
                out.setEntry(row, col, sum);
            }
        }
        return out;
    }
    
    public FieldMatrix<T> preMultiply(final FieldMatrix<T> m) throws DimensionMismatchException {
        return m.multiply(this);
    }
    
    public FieldMatrix<T> power(final int p) throws NonSquareMatrixException, NotPositiveException {
        if (p < 0) {
            throw new NotPositiveException(p);
        }
        if (!this.isSquare()) {
            throw new NonSquareMatrixException(this.getRowDimension(), this.getColumnDimension());
        }
        if (p == 0) {
            return MatrixUtils.createFieldIdentityMatrix(this.getField(), this.getRowDimension());
        }
        if (p == 1) {
            return this.copy();
        }
        final int power = p - 1;
        final char[] binaryRepresentation = Integer.toBinaryString(power).toCharArray();
        final ArrayList<Integer> nonZeroPositions = new ArrayList<Integer>();
        for (int i = 0; i < binaryRepresentation.length; ++i) {
            if (binaryRepresentation[i] == '1') {
                final int pos = binaryRepresentation.length - i - 1;
                nonZeroPositions.add(pos);
            }
        }
        final ArrayList<FieldMatrix<T>> results = new ArrayList<FieldMatrix<T>>(binaryRepresentation.length);
        results.add(0, this.copy());
        for (int j = 1; j < binaryRepresentation.length; ++j) {
            final FieldMatrix<T> s = results.get(j - 1);
            final FieldMatrix<T> r = s.multiply(s);
            results.add(j, r);
        }
        FieldMatrix<T> result = this.copy();
        for (final Integer k : nonZeroPositions) {
            result = result.multiply(results.get(k));
        }
        return result;
    }
    
    public T[][] getData() {
        final T[][] data = MathArrays.buildArray(this.field, this.getRowDimension(), this.getColumnDimension());
        for (int i = 0; i < data.length; ++i) {
            final T[] dataI = data[i];
            for (int j = 0; j < dataI.length; ++j) {
                dataI[j] = this.getEntry(i, j);
            }
        }
        return data;
    }
    
    public FieldMatrix<T> getSubMatrix(final int startRow, final int endRow, final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        this.checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        final FieldMatrix<T> subMatrix = this.createMatrix(endRow - startRow + 1, endColumn - startColumn + 1);
        for (int i = startRow; i <= endRow; ++i) {
            for (int j = startColumn; j <= endColumn; ++j) {
                subMatrix.setEntry(i - startRow, j - startColumn, this.getEntry(i, j));
            }
        }
        return subMatrix;
    }
    
    public FieldMatrix<T> getSubMatrix(final int[] selectedRows, final int[] selectedColumns) throws NoDataException, NullArgumentException, OutOfRangeException {
        this.checkSubMatrixIndex(selectedRows, selectedColumns);
        final FieldMatrix<T> subMatrix = this.createMatrix(selectedRows.length, selectedColumns.length);
        subMatrix.walkInOptimizedOrder(new DefaultFieldMatrixChangingVisitor<T>((T)this.field.getZero()) {
            @Override
            public T visit(final int row, final int column, final T value) {
                return AbstractFieldMatrix.this.getEntry(selectedRows[row], selectedColumns[column]);
            }
        });
        return subMatrix;
    }
    
    public void copySubMatrix(final int startRow, final int endRow, final int startColumn, final int endColumn, final T[][] destination) throws MatrixDimensionMismatchException, NumberIsTooSmallException, OutOfRangeException {
        this.checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        final int rowsCount = endRow + 1 - startRow;
        final int columnsCount = endColumn + 1 - startColumn;
        if (destination.length < rowsCount || destination[0].length < columnsCount) {
            throw new MatrixDimensionMismatchException(destination.length, destination[0].length, rowsCount, columnsCount);
        }
        this.walkInOptimizedOrder(new DefaultFieldMatrixPreservingVisitor<T>((T)this.field.getZero()) {
            private int startRow;
            private int startColumn;
            
            @Override
            public void start(final int rows, final int columns, final int startRow, final int endRow, final int startColumn, final int endColumn) {
                this.startRow = startRow;
                this.startColumn = startColumn;
            }
            
            @Override
            public void visit(final int row, final int column, final T value) {
                destination[row - this.startRow][column - this.startColumn] = value;
            }
        }, startRow, endRow, startColumn, endColumn);
    }
    
    public void copySubMatrix(final int[] selectedRows, final int[] selectedColumns, final T[][] destination) throws MatrixDimensionMismatchException, NoDataException, NullArgumentException, OutOfRangeException {
        this.checkSubMatrixIndex(selectedRows, selectedColumns);
        if (destination.length < selectedRows.length || destination[0].length < selectedColumns.length) {
            throw new MatrixDimensionMismatchException(destination.length, destination[0].length, selectedRows.length, selectedColumns.length);
        }
        for (int i = 0; i < selectedRows.length; ++i) {
            final T[] destinationI = destination[i];
            for (int j = 0; j < selectedColumns.length; ++j) {
                destinationI[j] = this.getEntry(selectedRows[i], selectedColumns[j]);
            }
        }
    }
    
    public void setSubMatrix(final T[][] subMatrix, final int row, final int column) throws DimensionMismatchException, OutOfRangeException, NoDataException, NullArgumentException {
        if (subMatrix == null) {
            throw new NullArgumentException();
        }
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
        this.checkRowIndex(row);
        this.checkColumnIndex(column);
        this.checkRowIndex(nRows + row - 1);
        this.checkColumnIndex(nCols + column - 1);
        for (int i = 0; i < nRows; ++i) {
            for (int j = 0; j < nCols; ++j) {
                this.setEntry(row + i, column + j, subMatrix[i][j]);
            }
        }
    }
    
    public FieldMatrix<T> getRowMatrix(final int row) throws OutOfRangeException {
        this.checkRowIndex(row);
        final int nCols = this.getColumnDimension();
        final FieldMatrix<T> out = this.createMatrix(1, nCols);
        for (int i = 0; i < nCols; ++i) {
            out.setEntry(0, i, this.getEntry(row, i));
        }
        return out;
    }
    
    public void setRowMatrix(final int row, final FieldMatrix<T> matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        this.checkRowIndex(row);
        final int nCols = this.getColumnDimension();
        if (matrix.getRowDimension() != 1 || matrix.getColumnDimension() != nCols) {
            throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), 1, nCols);
        }
        for (int i = 0; i < nCols; ++i) {
            this.setEntry(row, i, matrix.getEntry(0, i));
        }
    }
    
    public FieldMatrix<T> getColumnMatrix(final int column) throws OutOfRangeException {
        this.checkColumnIndex(column);
        final int nRows = this.getRowDimension();
        final FieldMatrix<T> out = this.createMatrix(nRows, 1);
        for (int i = 0; i < nRows; ++i) {
            out.setEntry(i, 0, this.getEntry(i, column));
        }
        return out;
    }
    
    public void setColumnMatrix(final int column, final FieldMatrix<T> matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
        this.checkColumnIndex(column);
        final int nRows = this.getRowDimension();
        if (matrix.getRowDimension() != nRows || matrix.getColumnDimension() != 1) {
            throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), nRows, 1);
        }
        for (int i = 0; i < nRows; ++i) {
            this.setEntry(i, column, matrix.getEntry(i, 0));
        }
    }
    
    public FieldVector<T> getRowVector(final int row) throws OutOfRangeException {
        return new ArrayFieldVector<T>(this.field, this.getRow(row), false);
    }
    
    public void setRowVector(final int row, final FieldVector<T> vector) throws OutOfRangeException, MatrixDimensionMismatchException {
        this.checkRowIndex(row);
        final int nCols = this.getColumnDimension();
        if (vector.getDimension() != nCols) {
            throw new MatrixDimensionMismatchException(1, vector.getDimension(), 1, nCols);
        }
        for (int i = 0; i < nCols; ++i) {
            this.setEntry(row, i, vector.getEntry(i));
        }
    }
    
    public FieldVector<T> getColumnVector(final int column) throws OutOfRangeException {
        return new ArrayFieldVector<T>(this.field, this.getColumn(column), false);
    }
    
    public void setColumnVector(final int column, final FieldVector<T> vector) throws OutOfRangeException, MatrixDimensionMismatchException {
        this.checkColumnIndex(column);
        final int nRows = this.getRowDimension();
        if (vector.getDimension() != nRows) {
            throw new MatrixDimensionMismatchException(vector.getDimension(), 1, nRows, 1);
        }
        for (int i = 0; i < nRows; ++i) {
            this.setEntry(i, column, vector.getEntry(i));
        }
    }
    
    public T[] getRow(final int row) throws OutOfRangeException {
        this.checkRowIndex(row);
        final int nCols = this.getColumnDimension();
        final T[] out = MathArrays.buildArray(this.field, nCols);
        for (int i = 0; i < nCols; ++i) {
            out[i] = this.getEntry(row, i);
        }
        return out;
    }
    
    public void setRow(final int row, final T[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
        this.checkRowIndex(row);
        final int nCols = this.getColumnDimension();
        if (array.length != nCols) {
            throw new MatrixDimensionMismatchException(1, array.length, 1, nCols);
        }
        for (int i = 0; i < nCols; ++i) {
            this.setEntry(row, i, array[i]);
        }
    }
    
    public T[] getColumn(final int column) throws OutOfRangeException {
        this.checkColumnIndex(column);
        final int nRows = this.getRowDimension();
        final T[] out = MathArrays.buildArray(this.field, nRows);
        for (int i = 0; i < nRows; ++i) {
            out[i] = this.getEntry(i, column);
        }
        return out;
    }
    
    public void setColumn(final int column, final T[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
        this.checkColumnIndex(column);
        final int nRows = this.getRowDimension();
        if (array.length != nRows) {
            throw new MatrixDimensionMismatchException(array.length, 1, nRows, 1);
        }
        for (int i = 0; i < nRows; ++i) {
            this.setEntry(i, column, array[i]);
        }
    }
    
    public abstract T getEntry(final int p0, final int p1) throws OutOfRangeException;
    
    public abstract void setEntry(final int p0, final int p1, final T p2) throws OutOfRangeException;
    
    public abstract void addToEntry(final int p0, final int p1, final T p2) throws OutOfRangeException;
    
    public abstract void multiplyEntry(final int p0, final int p1, final T p2) throws OutOfRangeException;
    
    public FieldMatrix<T> transpose() {
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        final FieldMatrix<T> out = this.createMatrix(nCols, nRows);
        this.walkInOptimizedOrder(new DefaultFieldMatrixPreservingVisitor<T>((T)this.field.getZero()) {
            @Override
            public void visit(final int row, final int column, final T value) {
                out.setEntry(column, row, value);
            }
        });
        return out;
    }
    
    public boolean isSquare() {
        return this.getColumnDimension() == this.getRowDimension();
    }
    
    public abstract int getRowDimension();
    
    public abstract int getColumnDimension();
    
    public T getTrace() throws NonSquareMatrixException {
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        if (nRows != nCols) {
            throw new NonSquareMatrixException(nRows, nCols);
        }
        T trace = this.field.getZero();
        for (int i = 0; i < nRows; ++i) {
            trace = trace.add(this.getEntry(i, i));
        }
        return trace;
    }
    
    public T[] operate(final T[] v) throws DimensionMismatchException {
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        if (v.length != nCols) {
            throw new DimensionMismatchException(v.length, nCols);
        }
        final T[] out = MathArrays.buildArray(this.field, nRows);
        for (int row = 0; row < nRows; ++row) {
            T sum = this.field.getZero();
            for (int i = 0; i < nCols; ++i) {
                sum = sum.add((T)this.getEntry(row, i).multiply((FieldElement<FieldElement<FieldElement>>)v[i]));
            }
            out[row] = sum;
        }
        return out;
    }
    
    public FieldVector<T> operate(final FieldVector<T> v) throws DimensionMismatchException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: aload_0         /* this */
        //     5: getfield        org/apache/commons/math3/linear/AbstractFieldMatrix.field:Lorg/apache/commons/math3/Field;
        //     8: aload_0         /* this */
        //     9: aload_1         /* v */
        //    10: checkcast       Lorg/apache/commons/math3/linear/ArrayFieldVector;
        //    13: invokevirtual   org/apache/commons/math3/linear/ArrayFieldVector.getDataRef:()[Lorg/apache/commons/math3/FieldElement;
        //    16: invokevirtual   org/apache/commons/math3/linear/AbstractFieldMatrix.operate:([Lorg/apache/commons/math3/FieldElement;)[Lorg/apache/commons/math3/FieldElement;
        //    19: iconst_0       
        //    20: invokespecial   org/apache/commons/math3/linear/ArrayFieldVector.<init>:(Lorg/apache/commons/math3/Field;[Lorg/apache/commons/math3/FieldElement;Z)V
        //    23: areturn        
        //    24: astore_2        /* cce */
        //    25: aload_0         /* this */
        //    26: invokevirtual   org/apache/commons/math3/linear/AbstractFieldMatrix.getRowDimension:()I
        //    29: istore_3        /* nRows */
        //    30: aload_0         /* this */
        //    31: invokevirtual   org/apache/commons/math3/linear/AbstractFieldMatrix.getColumnDimension:()I
        //    34: istore          nCols
        //    36: aload_1         /* v */
        //    37: invokeinterface org/apache/commons/math3/linear/FieldVector.getDimension:()I
        //    42: iload           nCols
        //    44: if_icmpeq       63
        //    47: new             Lorg/apache/commons/math3/exception/DimensionMismatchException;
        //    50: dup            
        //    51: aload_1         /* v */
        //    52: invokeinterface org/apache/commons/math3/linear/FieldVector.getDimension:()I
        //    57: iload           nCols
        //    59: invokespecial   org/apache/commons/math3/exception/DimensionMismatchException.<init>:(II)V
        //    62: athrow         
        //    63: aload_0         /* this */
        //    64: getfield        org/apache/commons/math3/linear/AbstractFieldMatrix.field:Lorg/apache/commons/math3/Field;
        //    67: iload_3         /* nRows */
        //    68: invokestatic    org/apache/commons/math3/util/MathArrays.buildArray:(Lorg/apache/commons/math3/Field;I)[Ljava/lang/Object;
        //    71: checkcast       [Lorg/apache/commons/math3/FieldElement;
        //    74: astore          out
        //    76: iconst_0       
        //    77: istore          row
        //    79: iload           row
        //    81: iload_3         /* nRows */
        //    82: if_icmpge       161
        //    85: aload_0         /* this */
        //    86: getfield        org/apache/commons/math3/linear/AbstractFieldMatrix.field:Lorg/apache/commons/math3/Field;
        //    89: invokeinterface org/apache/commons/math3/Field.getZero:()Ljava/lang/Object;
        //    94: checkcast       Lorg/apache/commons/math3/FieldElement;
        //    97: astore          sum
        //    99: iconst_0       
        //   100: istore          i
        //   102: iload           i
        //   104: iload           nCols
        //   106: if_icmpge       148
        //   109: aload           sum
        //   111: aload_0         /* this */
        //   112: iload           row
        //   114: iload           i
        //   116: invokevirtual   org/apache/commons/math3/linear/AbstractFieldMatrix.getEntry:(II)Lorg/apache/commons/math3/FieldElement;
        //   119: aload_1         /* v */
        //   120: iload           i
        //   122: invokeinterface org/apache/commons/math3/linear/FieldVector.getEntry:(I)Lorg/apache/commons/math3/FieldElement;
        //   127: invokeinterface org/apache/commons/math3/FieldElement.multiply:(Ljava/lang/Object;)Ljava/lang/Object;
        //   132: invokeinterface org/apache/commons/math3/FieldElement.add:(Ljava/lang/Object;)Ljava/lang/Object;
        //   137: checkcast       Lorg/apache/commons/math3/FieldElement;
        //   140: astore          sum
        //   142: iinc            i, 1
        //   145: goto            102
        //   148: aload           out
        //   150: iload           row
        //   152: aload           sum
        //   154: aastore        
        //   155: iinc            row, 1
        //   158: goto            79
        //   161: new             Lorg/apache/commons/math3/linear/ArrayFieldVector;
        //   164: dup            
        //   165: aload_0         /* this */
        //   166: getfield        org/apache/commons/math3/linear/AbstractFieldMatrix.field:Lorg/apache/commons/math3/Field;
        //   169: aload           out
        //   171: iconst_0       
        //   172: invokespecial   org/apache/commons/math3/linear/ArrayFieldVector.<init>:(Lorg/apache/commons/math3/Field;[Lorg/apache/commons/math3/FieldElement;Z)V
        //   175: areturn        
        //    Exceptions:
        //  throws org.apache.commons.math3.exception.DimensionMismatchException
        //    Signature:
        //  (Lorg/apache/commons/math3/linear/FieldVector<TT;>;)Lorg/apache/commons/math3/linear/FieldVector<TT;>;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                          
        //  -----  -----  -----  -----  ------------------------------
        //  0      23     24     176    Ljava/lang/ClassCastException;
        // 
        // The error that occurred was:
        // 
        // java.lang.UnsupportedOperationException: The requested operation is not supported.
        //     at com.strobel.util.ContractUtils.unsupported(ContractUtils.java:27)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:284)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:279)
        //     at com.strobel.assembler.metadata.TypeReference.makeGenericType(TypeReference.java:154)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:225)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitArrayType(TypeSubstitutionVisitor.java:45)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitArrayType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ArrayType.accept(ArrayType.java:80)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitMethod(TypeSubstitutionVisitor.java:314)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2611)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2715)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:593)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:405)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public T[] preMultiply(final T[] v) throws DimensionMismatchException {
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        if (v.length != nRows) {
            throw new DimensionMismatchException(v.length, nRows);
        }
        final T[] out = MathArrays.buildArray(this.field, nCols);
        for (int col = 0; col < nCols; ++col) {
            T sum = this.field.getZero();
            for (int i = 0; i < nRows; ++i) {
                sum = sum.add((T)this.getEntry(i, col).multiply((FieldElement<FieldElement<FieldElement>>)v[i]));
            }
            out[col] = sum;
        }
        return out;
    }
    
    public FieldVector<T> preMultiply(final FieldVector<T> v) throws DimensionMismatchException {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: dup            
        //     4: aload_0         /* this */
        //     5: getfield        org/apache/commons/math3/linear/AbstractFieldMatrix.field:Lorg/apache/commons/math3/Field;
        //     8: aload_0         /* this */
        //     9: aload_1         /* v */
        //    10: checkcast       Lorg/apache/commons/math3/linear/ArrayFieldVector;
        //    13: invokevirtual   org/apache/commons/math3/linear/ArrayFieldVector.getDataRef:()[Lorg/apache/commons/math3/FieldElement;
        //    16: invokevirtual   org/apache/commons/math3/linear/AbstractFieldMatrix.preMultiply:([Lorg/apache/commons/math3/FieldElement;)[Lorg/apache/commons/math3/FieldElement;
        //    19: iconst_0       
        //    20: invokespecial   org/apache/commons/math3/linear/ArrayFieldVector.<init>:(Lorg/apache/commons/math3/Field;[Lorg/apache/commons/math3/FieldElement;Z)V
        //    23: areturn        
        //    24: astore_2        /* cce */
        //    25: aload_0         /* this */
        //    26: invokevirtual   org/apache/commons/math3/linear/AbstractFieldMatrix.getRowDimension:()I
        //    29: istore_3        /* nRows */
        //    30: aload_0         /* this */
        //    31: invokevirtual   org/apache/commons/math3/linear/AbstractFieldMatrix.getColumnDimension:()I
        //    34: istore          nCols
        //    36: aload_1         /* v */
        //    37: invokeinterface org/apache/commons/math3/linear/FieldVector.getDimension:()I
        //    42: iload_3         /* nRows */
        //    43: if_icmpeq       61
        //    46: new             Lorg/apache/commons/math3/exception/DimensionMismatchException;
        //    49: dup            
        //    50: aload_1         /* v */
        //    51: invokeinterface org/apache/commons/math3/linear/FieldVector.getDimension:()I
        //    56: iload_3         /* nRows */
        //    57: invokespecial   org/apache/commons/math3/exception/DimensionMismatchException.<init>:(II)V
        //    60: athrow         
        //    61: aload_0         /* this */
        //    62: getfield        org/apache/commons/math3/linear/AbstractFieldMatrix.field:Lorg/apache/commons/math3/Field;
        //    65: iload           nCols
        //    67: invokestatic    org/apache/commons/math3/util/MathArrays.buildArray:(Lorg/apache/commons/math3/Field;I)[Ljava/lang/Object;
        //    70: checkcast       [Lorg/apache/commons/math3/FieldElement;
        //    73: astore          out
        //    75: iconst_0       
        //    76: istore          col
        //    78: iload           col
        //    80: iload           nCols
        //    82: if_icmpge       160
        //    85: aload_0         /* this */
        //    86: getfield        org/apache/commons/math3/linear/AbstractFieldMatrix.field:Lorg/apache/commons/math3/Field;
        //    89: invokeinterface org/apache/commons/math3/Field.getZero:()Ljava/lang/Object;
        //    94: checkcast       Lorg/apache/commons/math3/FieldElement;
        //    97: astore          sum
        //    99: iconst_0       
        //   100: istore          i
        //   102: iload           i
        //   104: iload_3         /* nRows */
        //   105: if_icmpge       147
        //   108: aload           sum
        //   110: aload_0         /* this */
        //   111: iload           i
        //   113: iload           col
        //   115: invokevirtual   org/apache/commons/math3/linear/AbstractFieldMatrix.getEntry:(II)Lorg/apache/commons/math3/FieldElement;
        //   118: aload_1         /* v */
        //   119: iload           i
        //   121: invokeinterface org/apache/commons/math3/linear/FieldVector.getEntry:(I)Lorg/apache/commons/math3/FieldElement;
        //   126: invokeinterface org/apache/commons/math3/FieldElement.multiply:(Ljava/lang/Object;)Ljava/lang/Object;
        //   131: invokeinterface org/apache/commons/math3/FieldElement.add:(Ljava/lang/Object;)Ljava/lang/Object;
        //   136: checkcast       Lorg/apache/commons/math3/FieldElement;
        //   139: astore          sum
        //   141: iinc            i, 1
        //   144: goto            102
        //   147: aload           out
        //   149: iload           col
        //   151: aload           sum
        //   153: aastore        
        //   154: iinc            col, 1
        //   157: goto            78
        //   160: new             Lorg/apache/commons/math3/linear/ArrayFieldVector;
        //   163: dup            
        //   164: aload_0         /* this */
        //   165: getfield        org/apache/commons/math3/linear/AbstractFieldMatrix.field:Lorg/apache/commons/math3/Field;
        //   168: aload           out
        //   170: iconst_0       
        //   171: invokespecial   org/apache/commons/math3/linear/ArrayFieldVector.<init>:(Lorg/apache/commons/math3/Field;[Lorg/apache/commons/math3/FieldElement;Z)V
        //   174: areturn        
        //    Exceptions:
        //  throws org.apache.commons.math3.exception.DimensionMismatchException
        //    Signature:
        //  (Lorg/apache/commons/math3/linear/FieldVector<TT;>;)Lorg/apache/commons/math3/linear/FieldVector<TT;>;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                          
        //  -----  -----  -----  -----  ------------------------------
        //  0      23     24     175    Ljava/lang/ClassCastException;
        // 
        // The error that occurred was:
        // 
        // java.lang.UnsupportedOperationException: The requested operation is not supported.
        //     at com.strobel.util.ContractUtils.unsupported(ContractUtils.java:27)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:284)
        //     at com.strobel.assembler.metadata.TypeReference.getRawType(TypeReference.java:279)
        //     at com.strobel.assembler.metadata.TypeReference.makeGenericType(TypeReference.java:154)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:225)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:211)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitParameterizedType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitArrayType(TypeSubstitutionVisitor.java:45)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitArrayType(TypeSubstitutionVisitor.java:25)
        //     at com.strobel.assembler.metadata.ArrayType.accept(ArrayType.java:80)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visit(TypeSubstitutionVisitor.java:40)
        //     at com.strobel.assembler.metadata.TypeSubstitutionVisitor.visitMethod(TypeSubstitutionVisitor.java:314)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2611)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2715)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1040)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:815)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:684)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypesForVariables(TypeAnalysis.java:593)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:405)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:95)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public T walkInRowOrder(final FieldMatrixChangingVisitor<T> visitor) {
        final int rows = this.getRowDimension();
        final int columns = this.getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < columns; ++column) {
                final T oldValue = this.getEntry(row, column);
                final T newValue = visitor.visit(row, column, oldValue);
                this.setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }
    
    public T walkInRowOrder(final FieldMatrixPreservingVisitor<T> visitor) {
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
    
    public T walkInRowOrder(final FieldMatrixChangingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        this.checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(this.getRowDimension(), this.getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int row = startRow; row <= endRow; ++row) {
            for (int column = startColumn; column <= endColumn; ++column) {
                final T oldValue = this.getEntry(row, column);
                final T newValue = visitor.visit(row, column, oldValue);
                this.setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }
    
    public T walkInRowOrder(final FieldMatrixPreservingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        this.checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(this.getRowDimension(), this.getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int row = startRow; row <= endRow; ++row) {
            for (int column = startColumn; column <= endColumn; ++column) {
                visitor.visit(row, column, this.getEntry(row, column));
            }
        }
        return visitor.end();
    }
    
    public T walkInColumnOrder(final FieldMatrixChangingVisitor<T> visitor) {
        final int rows = this.getRowDimension();
        final int columns = this.getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int column = 0; column < columns; ++column) {
            for (int row = 0; row < rows; ++row) {
                final T oldValue = this.getEntry(row, column);
                final T newValue = visitor.visit(row, column, oldValue);
                this.setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }
    
    public T walkInColumnOrder(final FieldMatrixPreservingVisitor<T> visitor) {
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
    
    public T walkInColumnOrder(final FieldMatrixChangingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        this.checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(this.getRowDimension(), this.getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int column = startColumn; column <= endColumn; ++column) {
            for (int row = startRow; row <= endRow; ++row) {
                final T oldValue = this.getEntry(row, column);
                final T newValue = visitor.visit(row, column, oldValue);
                this.setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }
    
    public T walkInColumnOrder(final FieldMatrixPreservingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        this.checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(this.getRowDimension(), this.getColumnDimension(), startRow, endRow, startColumn, endColumn);
        for (int column = startColumn; column <= endColumn; ++column) {
            for (int row = startRow; row <= endRow; ++row) {
                visitor.visit(row, column, this.getEntry(row, column));
            }
        }
        return visitor.end();
    }
    
    public T walkInOptimizedOrder(final FieldMatrixChangingVisitor<T> visitor) {
        return this.walkInRowOrder(visitor);
    }
    
    public T walkInOptimizedOrder(final FieldMatrixPreservingVisitor<T> visitor) {
        return this.walkInRowOrder(visitor);
    }
    
    public T walkInOptimizedOrder(final FieldMatrixChangingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        return this.walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
    }
    
    public T walkInOptimizedOrder(final FieldMatrixPreservingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        return this.walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
    }
    
    @Override
    public String toString() {
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        final StringBuffer res = new StringBuffer();
        final String fullClassName = this.getClass().getName();
        final String shortClassName = fullClassName.substring(fullClassName.lastIndexOf(46) + 1);
        res.append(shortClassName).append("{");
        for (int i = 0; i < nRows; ++i) {
            if (i > 0) {
                res.append(",");
            }
            res.append("{");
            for (int j = 0; j < nCols; ++j) {
                if (j > 0) {
                    res.append(",");
                }
                res.append(this.getEntry(i, j));
            }
            res.append("}");
        }
        res.append("}");
        return res.toString();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof FieldMatrix)) {
            return false;
        }
        final FieldMatrix<?> m = (FieldMatrix<?>)object;
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        if (m.getColumnDimension() != nCols || m.getRowDimension() != nRows) {
            return false;
        }
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                if (!this.getEntry(row, col).equals(m.getEntry(row, col))) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int ret = 322562;
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        ret = ret * 31 + nRows;
        ret = ret * 31 + nCols;
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                ret = ret * 31 + (11 * (row + 1) + 17 * (col + 1)) * this.getEntry(row, col).hashCode();
            }
        }
        return ret;
    }
    
    protected void checkRowIndex(final int row) throws OutOfRangeException {
        if (row < 0 || row >= this.getRowDimension()) {
            throw new OutOfRangeException(LocalizedFormats.ROW_INDEX, row, 0, this.getRowDimension() - 1);
        }
    }
    
    protected void checkColumnIndex(final int column) throws OutOfRangeException {
        if (column < 0 || column >= this.getColumnDimension()) {
            throw new OutOfRangeException(LocalizedFormats.COLUMN_INDEX, column, 0, this.getColumnDimension() - 1);
        }
    }
    
    protected void checkSubMatrixIndex(final int startRow, final int endRow, final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        this.checkRowIndex(startRow);
        this.checkRowIndex(endRow);
        if (endRow < startRow) {
            throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW, endRow, startRow, true);
        }
        this.checkColumnIndex(startColumn);
        this.checkColumnIndex(endColumn);
        if (endColumn < startColumn) {
            throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_COLUMN_AFTER_FINAL_COLUMN, endColumn, startColumn, true);
        }
    }
    
    protected void checkSubMatrixIndex(final int[] selectedRows, final int[] selectedColumns) throws NoDataException, NullArgumentException, OutOfRangeException {
        if (selectedRows == null || selectedColumns == null) {
            throw new NullArgumentException();
        }
        if (selectedRows.length == 0 || selectedColumns.length == 0) {
            throw new NoDataException();
        }
        for (final int row : selectedRows) {
            this.checkRowIndex(row);
        }
        for (final int column : selectedColumns) {
            this.checkColumnIndex(column);
        }
    }
    
    protected void checkAdditionCompatible(final FieldMatrix<T> m) throws MatrixDimensionMismatchException {
        if (this.getRowDimension() != m.getRowDimension() || this.getColumnDimension() != m.getColumnDimension()) {
            throw new MatrixDimensionMismatchException(m.getRowDimension(), m.getColumnDimension(), this.getRowDimension(), this.getColumnDimension());
        }
    }
    
    protected void checkSubtractionCompatible(final FieldMatrix<T> m) throws MatrixDimensionMismatchException {
        if (this.getRowDimension() != m.getRowDimension() || this.getColumnDimension() != m.getColumnDimension()) {
            throw new MatrixDimensionMismatchException(m.getRowDimension(), m.getColumnDimension(), this.getRowDimension(), this.getColumnDimension());
        }
    }
    
    protected void checkMultiplicationCompatible(final FieldMatrix<T> m) throws DimensionMismatchException {
        if (this.getColumnDimension() != m.getRowDimension()) {
            throw new DimensionMismatchException(m.getRowDimension(), this.getColumnDimension());
        }
    }
}
