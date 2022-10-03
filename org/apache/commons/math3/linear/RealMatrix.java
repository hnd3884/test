package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public interface RealMatrix extends AnyMatrix
{
    RealMatrix createMatrix(final int p0, final int p1) throws NotStrictlyPositiveException;
    
    RealMatrix copy();
    
    RealMatrix add(final RealMatrix p0) throws MatrixDimensionMismatchException;
    
    RealMatrix subtract(final RealMatrix p0) throws MatrixDimensionMismatchException;
    
    RealMatrix scalarAdd(final double p0);
    
    RealMatrix scalarMultiply(final double p0);
    
    RealMatrix multiply(final RealMatrix p0) throws DimensionMismatchException;
    
    RealMatrix preMultiply(final RealMatrix p0) throws DimensionMismatchException;
    
    RealMatrix power(final int p0) throws NotPositiveException, NonSquareMatrixException;
    
    double[][] getData();
    
    double getNorm();
    
    double getFrobeniusNorm();
    
    RealMatrix getSubMatrix(final int p0, final int p1, final int p2, final int p3) throws OutOfRangeException, NumberIsTooSmallException;
    
    RealMatrix getSubMatrix(final int[] p0, final int[] p1) throws NullArgumentException, NoDataException, OutOfRangeException;
    
    void copySubMatrix(final int p0, final int p1, final int p2, final int p3, final double[][] p4) throws OutOfRangeException, NumberIsTooSmallException, MatrixDimensionMismatchException;
    
    void copySubMatrix(final int[] p0, final int[] p1, final double[][] p2) throws OutOfRangeException, NullArgumentException, NoDataException, MatrixDimensionMismatchException;
    
    void setSubMatrix(final double[][] p0, final int p1, final int p2) throws NoDataException, OutOfRangeException, DimensionMismatchException, NullArgumentException;
    
    RealMatrix getRowMatrix(final int p0) throws OutOfRangeException;
    
    void setRowMatrix(final int p0, final RealMatrix p1) throws OutOfRangeException, MatrixDimensionMismatchException;
    
    RealMatrix getColumnMatrix(final int p0) throws OutOfRangeException;
    
    void setColumnMatrix(final int p0, final RealMatrix p1) throws OutOfRangeException, MatrixDimensionMismatchException;
    
    RealVector getRowVector(final int p0) throws OutOfRangeException;
    
    void setRowVector(final int p0, final RealVector p1) throws OutOfRangeException, MatrixDimensionMismatchException;
    
    RealVector getColumnVector(final int p0) throws OutOfRangeException;
    
    void setColumnVector(final int p0, final RealVector p1) throws OutOfRangeException, MatrixDimensionMismatchException;
    
    double[] getRow(final int p0) throws OutOfRangeException;
    
    void setRow(final int p0, final double[] p1) throws OutOfRangeException, MatrixDimensionMismatchException;
    
    double[] getColumn(final int p0) throws OutOfRangeException;
    
    void setColumn(final int p0, final double[] p1) throws OutOfRangeException, MatrixDimensionMismatchException;
    
    double getEntry(final int p0, final int p1) throws OutOfRangeException;
    
    void setEntry(final int p0, final int p1, final double p2) throws OutOfRangeException;
    
    void addToEntry(final int p0, final int p1, final double p2) throws OutOfRangeException;
    
    void multiplyEntry(final int p0, final int p1, final double p2) throws OutOfRangeException;
    
    RealMatrix transpose();
    
    double getTrace() throws NonSquareMatrixException;
    
    double[] operate(final double[] p0) throws DimensionMismatchException;
    
    RealVector operate(final RealVector p0) throws DimensionMismatchException;
    
    double[] preMultiply(final double[] p0) throws DimensionMismatchException;
    
    RealVector preMultiply(final RealVector p0) throws DimensionMismatchException;
    
    double walkInRowOrder(final RealMatrixChangingVisitor p0);
    
    double walkInRowOrder(final RealMatrixPreservingVisitor p0);
    
    double walkInRowOrder(final RealMatrixChangingVisitor p0, final int p1, final int p2, final int p3, final int p4) throws OutOfRangeException, NumberIsTooSmallException;
    
    double walkInRowOrder(final RealMatrixPreservingVisitor p0, final int p1, final int p2, final int p3, final int p4) throws OutOfRangeException, NumberIsTooSmallException;
    
    double walkInColumnOrder(final RealMatrixChangingVisitor p0);
    
    double walkInColumnOrder(final RealMatrixPreservingVisitor p0);
    
    double walkInColumnOrder(final RealMatrixChangingVisitor p0, final int p1, final int p2, final int p3, final int p4) throws OutOfRangeException, NumberIsTooSmallException;
    
    double walkInColumnOrder(final RealMatrixPreservingVisitor p0, final int p1, final int p2, final int p3, final int p4) throws OutOfRangeException, NumberIsTooSmallException;
    
    double walkInOptimizedOrder(final RealMatrixChangingVisitor p0);
    
    double walkInOptimizedOrder(final RealMatrixPreservingVisitor p0);
    
    double walkInOptimizedOrder(final RealMatrixChangingVisitor p0, final int p1, final int p2, final int p3, final int p4) throws OutOfRangeException, NumberIsTooSmallException;
    
    double walkInOptimizedOrder(final RealMatrixPreservingVisitor p0, final int p1, final int p2, final int p3, final int p4) throws OutOfRangeException, NumberIsTooSmallException;
}
