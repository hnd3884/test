package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

public interface FieldMatrix<T extends FieldElement<T>> extends AnyMatrix
{
    Field<T> getField();
    
    FieldMatrix<T> createMatrix(final int p0, final int p1) throws NotStrictlyPositiveException;
    
    FieldMatrix<T> copy();
    
    FieldMatrix<T> add(final FieldMatrix<T> p0) throws MatrixDimensionMismatchException;
    
    FieldMatrix<T> subtract(final FieldMatrix<T> p0) throws MatrixDimensionMismatchException;
    
    FieldMatrix<T> scalarAdd(final T p0);
    
    FieldMatrix<T> scalarMultiply(final T p0);
    
    FieldMatrix<T> multiply(final FieldMatrix<T> p0) throws DimensionMismatchException;
    
    FieldMatrix<T> preMultiply(final FieldMatrix<T> p0) throws DimensionMismatchException;
    
    FieldMatrix<T> power(final int p0) throws NonSquareMatrixException, NotPositiveException;
    
    T[][] getData();
    
    FieldMatrix<T> getSubMatrix(final int p0, final int p1, final int p2, final int p3) throws NumberIsTooSmallException, OutOfRangeException;
    
    FieldMatrix<T> getSubMatrix(final int[] p0, final int[] p1) throws NoDataException, NullArgumentException, OutOfRangeException;
    
    void copySubMatrix(final int p0, final int p1, final int p2, final int p3, final T[][] p4) throws MatrixDimensionMismatchException, NumberIsTooSmallException, OutOfRangeException;
    
    void copySubMatrix(final int[] p0, final int[] p1, final T[][] p2) throws MatrixDimensionMismatchException, NoDataException, NullArgumentException, OutOfRangeException;
    
    void setSubMatrix(final T[][] p0, final int p1, final int p2) throws DimensionMismatchException, OutOfRangeException, NoDataException, NullArgumentException;
    
    FieldMatrix<T> getRowMatrix(final int p0) throws OutOfRangeException;
    
    void setRowMatrix(final int p0, final FieldMatrix<T> p1) throws MatrixDimensionMismatchException, OutOfRangeException;
    
    FieldMatrix<T> getColumnMatrix(final int p0) throws OutOfRangeException;
    
    void setColumnMatrix(final int p0, final FieldMatrix<T> p1) throws MatrixDimensionMismatchException, OutOfRangeException;
    
    FieldVector<T> getRowVector(final int p0) throws OutOfRangeException;
    
    void setRowVector(final int p0, final FieldVector<T> p1) throws MatrixDimensionMismatchException, OutOfRangeException;
    
    FieldVector<T> getColumnVector(final int p0) throws OutOfRangeException;
    
    void setColumnVector(final int p0, final FieldVector<T> p1) throws MatrixDimensionMismatchException, OutOfRangeException;
    
    T[] getRow(final int p0) throws OutOfRangeException;
    
    void setRow(final int p0, final T[] p1) throws MatrixDimensionMismatchException, OutOfRangeException;
    
    T[] getColumn(final int p0) throws OutOfRangeException;
    
    void setColumn(final int p0, final T[] p1) throws MatrixDimensionMismatchException, OutOfRangeException;
    
    T getEntry(final int p0, final int p1) throws OutOfRangeException;
    
    void setEntry(final int p0, final int p1, final T p2) throws OutOfRangeException;
    
    void addToEntry(final int p0, final int p1, final T p2) throws OutOfRangeException;
    
    void multiplyEntry(final int p0, final int p1, final T p2) throws OutOfRangeException;
    
    FieldMatrix<T> transpose();
    
    T getTrace() throws NonSquareMatrixException;
    
    T[] operate(final T[] p0) throws DimensionMismatchException;
    
    FieldVector<T> operate(final FieldVector<T> p0) throws DimensionMismatchException;
    
    T[] preMultiply(final T[] p0) throws DimensionMismatchException;
    
    FieldVector<T> preMultiply(final FieldVector<T> p0) throws DimensionMismatchException;
    
    T walkInRowOrder(final FieldMatrixChangingVisitor<T> p0);
    
    T walkInRowOrder(final FieldMatrixPreservingVisitor<T> p0);
    
    T walkInRowOrder(final FieldMatrixChangingVisitor<T> p0, final int p1, final int p2, final int p3, final int p4) throws OutOfRangeException, NumberIsTooSmallException;
    
    T walkInRowOrder(final FieldMatrixPreservingVisitor<T> p0, final int p1, final int p2, final int p3, final int p4) throws OutOfRangeException, NumberIsTooSmallException;
    
    T walkInColumnOrder(final FieldMatrixChangingVisitor<T> p0);
    
    T walkInColumnOrder(final FieldMatrixPreservingVisitor<T> p0);
    
    T walkInColumnOrder(final FieldMatrixChangingVisitor<T> p0, final int p1, final int p2, final int p3, final int p4) throws NumberIsTooSmallException, OutOfRangeException;
    
    T walkInColumnOrder(final FieldMatrixPreservingVisitor<T> p0, final int p1, final int p2, final int p3, final int p4) throws NumberIsTooSmallException, OutOfRangeException;
    
    T walkInOptimizedOrder(final FieldMatrixChangingVisitor<T> p0);
    
    T walkInOptimizedOrder(final FieldMatrixPreservingVisitor<T> p0);
    
    T walkInOptimizedOrder(final FieldMatrixChangingVisitor<T> p0, final int p1, final int p2, final int p3, final int p4) throws NumberIsTooSmallException, OutOfRangeException;
    
    T walkInOptimizedOrder(final FieldMatrixPreservingVisitor<T> p0, final int p1, final int p2, final int p3, final int p4) throws NumberIsTooSmallException, OutOfRangeException;
}
