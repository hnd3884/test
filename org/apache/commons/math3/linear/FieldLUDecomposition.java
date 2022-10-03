package org.apache.commons.math3.linear;

import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

public class FieldLUDecomposition<T extends FieldElement<T>>
{
    private final Field<T> field;
    private T[][] lu;
    private int[] pivot;
    private boolean even;
    private boolean singular;
    private FieldMatrix<T> cachedL;
    private FieldMatrix<T> cachedU;
    private FieldMatrix<T> cachedP;
    
    public FieldLUDecomposition(final FieldMatrix<T> matrix) {
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }
        final int m = matrix.getColumnDimension();
        this.field = matrix.getField();
        this.lu = matrix.getData();
        this.pivot = new int[m];
        this.cachedL = null;
        this.cachedU = null;
        this.cachedP = null;
        for (int row = 0; row < m; ++row) {
            this.pivot[row] = row;
        }
        this.even = true;
        this.singular = false;
        for (int col = 0; col < m; ++col) {
            T sum = this.field.getZero();
            for (int row2 = 0; row2 < col; ++row2) {
                final T[] luRow = this.lu[row2];
                sum = luRow[col];
                for (int i = 0; i < row2; ++i) {
                    sum = sum.subtract(luRow[i].multiply(this.lu[i][col]));
                }
                luRow[col] = sum;
            }
            int nonZero = col;
            for (int row3 = col; row3 < m; ++row3) {
                final T[] luRow2 = this.lu[row3];
                sum = luRow2[col];
                for (int j = 0; j < col; ++j) {
                    sum = sum.subtract(luRow2[j].multiply(this.lu[j][col]));
                }
                luRow2[col] = sum;
                if (this.lu[nonZero][col].equals(this.field.getZero())) {
                    ++nonZero;
                }
            }
            if (nonZero >= m) {
                this.singular = true;
                return;
            }
            if (nonZero != col) {
                T tmp = this.field.getZero();
                for (int i = 0; i < m; ++i) {
                    tmp = this.lu[nonZero][i];
                    this.lu[nonZero][i] = this.lu[col][i];
                    this.lu[col][i] = tmp;
                }
                final int temp = this.pivot[nonZero];
                this.pivot[nonZero] = this.pivot[col];
                this.pivot[col] = temp;
                this.even = !this.even;
            }
            final T luDiag = this.lu[col][col];
            for (int row4 = col + 1; row4 < m; ++row4) {
                final T[] luRow3 = this.lu[row4];
                luRow3[col] = luRow3[col].divide(luDiag);
            }
        }
    }
    
    public FieldMatrix<T> getL() {
        if (this.cachedL == null && !this.singular) {
            final int m = this.pivot.length;
            this.cachedL = new Array2DRowFieldMatrix<T>(this.field, m, m);
            for (int i = 0; i < m; ++i) {
                final T[] luI = this.lu[i];
                for (int j = 0; j < i; ++j) {
                    this.cachedL.setEntry(i, j, luI[j]);
                }
                this.cachedL.setEntry(i, i, this.field.getOne());
            }
        }
        return this.cachedL;
    }
    
    public FieldMatrix<T> getU() {
        if (this.cachedU == null && !this.singular) {
            final int m = this.pivot.length;
            this.cachedU = new Array2DRowFieldMatrix<T>(this.field, m, m);
            for (int i = 0; i < m; ++i) {
                final T[] luI = this.lu[i];
                for (int j = i; j < m; ++j) {
                    this.cachedU.setEntry(i, j, luI[j]);
                }
            }
        }
        return this.cachedU;
    }
    
    public FieldMatrix<T> getP() {
        if (this.cachedP == null && !this.singular) {
            final int m = this.pivot.length;
            this.cachedP = new Array2DRowFieldMatrix<T>(this.field, m, m);
            for (int i = 0; i < m; ++i) {
                this.cachedP.setEntry(i, this.pivot[i], this.field.getOne());
            }
        }
        return this.cachedP;
    }
    
    public int[] getPivot() {
        return this.pivot.clone();
    }
    
    public T getDeterminant() {
        if (this.singular) {
            return this.field.getZero();
        }
        final int m = this.pivot.length;
        T determinant = this.even ? this.field.getOne() : this.field.getZero().subtract(this.field.getOne());
        for (int i = 0; i < m; ++i) {
            determinant = determinant.multiply(this.lu[i][i]);
        }
        return determinant;
    }
    
    public FieldDecompositionSolver<T> getSolver() {
        return new Solver<T>((Field)this.field, (FieldElement[][])this.lu, this.pivot, this.singular);
    }
    
    private static class Solver<T extends FieldElement<T>> implements FieldDecompositionSolver<T>
    {
        private final Field<T> field;
        private final T[][] lu;
        private final int[] pivot;
        private final boolean singular;
        
        private Solver(final Field<T> field, final T[][] lu, final int[] pivot, final boolean singular) {
            this.field = field;
            this.lu = lu;
            this.pivot = pivot;
            this.singular = singular;
        }
        
        public boolean isNonSingular() {
            return !this.singular;
        }
        
        public FieldVector<T> solve(final FieldVector<T> b) {
            try {
                return this.solve((ArrayFieldVector)b);
            }
            catch (final ClassCastException cce) {
                final int m = this.pivot.length;
                if (b.getDimension() != m) {
                    throw new DimensionMismatchException(b.getDimension(), m);
                }
                if (this.singular) {
                    throw new SingularMatrixException();
                }
                final T[] bp = MathArrays.buildArray(this.field, m);
                for (int row = 0; row < m; ++row) {
                    bp[row] = b.getEntry(this.pivot[row]);
                }
                for (int col = 0; col < m; ++col) {
                    final T bpCol = bp[col];
                    for (int i = col + 1; i < m; ++i) {
                        bp[i] = bp[i].subtract(bpCol.multiply(this.lu[i][col]));
                    }
                }
                for (int col = m - 1; col >= 0; --col) {
                    bp[col] = bp[col].divide(this.lu[col][col]);
                    final T bpCol = bp[col];
                    for (int i = 0; i < col; ++i) {
                        bp[i] = bp[i].subtract(bpCol.multiply(this.lu[i][col]));
                    }
                }
                return new ArrayFieldVector<T>(this.field, bp, false);
            }
        }
        
        public ArrayFieldVector<T> solve(final ArrayFieldVector<T> b) {
            final int m = this.pivot.length;
            final int length = b.getDimension();
            if (length != m) {
                throw new DimensionMismatchException(length, m);
            }
            if (this.singular) {
                throw new SingularMatrixException();
            }
            final T[] bp = MathArrays.buildArray(this.field, m);
            for (int row = 0; row < m; ++row) {
                bp[row] = b.getEntry(this.pivot[row]);
            }
            for (int col = 0; col < m; ++col) {
                final T bpCol = bp[col];
                for (int i = col + 1; i < m; ++i) {
                    bp[i] = bp[i].subtract(bpCol.multiply(this.lu[i][col]));
                }
            }
            for (int col = m - 1; col >= 0; --col) {
                bp[col] = bp[col].divide(this.lu[col][col]);
                final T bpCol = bp[col];
                for (int i = 0; i < col; ++i) {
                    bp[i] = bp[i].subtract(bpCol.multiply(this.lu[i][col]));
                }
            }
            return new ArrayFieldVector<T>(bp, false);
        }
        
        public FieldMatrix<T> solve(final FieldMatrix<T> b) {
            final int m = this.pivot.length;
            if (b.getRowDimension() != m) {
                throw new DimensionMismatchException(b.getRowDimension(), m);
            }
            if (this.singular) {
                throw new SingularMatrixException();
            }
            final int nColB = b.getColumnDimension();
            final T[][] bp = MathArrays.buildArray(this.field, m, nColB);
            for (int row = 0; row < m; ++row) {
                final T[] bpRow = bp[row];
                final int pRow = this.pivot[row];
                for (int col = 0; col < nColB; ++col) {
                    bpRow[col] = b.getEntry(pRow, col);
                }
            }
            for (int col2 = 0; col2 < m; ++col2) {
                final T[] bpCol = bp[col2];
                for (int i = col2 + 1; i < m; ++i) {
                    final T[] bpI = bp[i];
                    final T luICol = this.lu[i][col2];
                    for (int j = 0; j < nColB; ++j) {
                        bpI[j] = bpI[j].subtract(bpCol[j].multiply(luICol));
                    }
                }
            }
            for (int col2 = m - 1; col2 >= 0; --col2) {
                final T[] bpCol = bp[col2];
                final T luDiag = this.lu[col2][col2];
                for (int k = 0; k < nColB; ++k) {
                    bpCol[k] = bpCol[k].divide(luDiag);
                }
                for (int l = 0; l < col2; ++l) {
                    final T[] bpI2 = bp[l];
                    final T luICol2 = this.lu[l][col2];
                    for (int j2 = 0; j2 < nColB; ++j2) {
                        bpI2[j2] = bpI2[j2].subtract(bpCol[j2].multiply(luICol2));
                    }
                }
            }
            return new Array2DRowFieldMatrix<T>(this.field, bp, false);
        }
        
        public FieldMatrix<T> getInverse() {
            final int m = this.pivot.length;
            final T one = this.field.getOne();
            final FieldMatrix<T> identity = new Array2DRowFieldMatrix<T>(this.field, m, m);
            for (int i = 0; i < m; ++i) {
                identity.setEntry(i, i, one);
            }
            return this.solve(identity);
        }
    }
}
