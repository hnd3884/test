package org.apache.commons.math3.linear;

import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.util.Precision;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.Arrays;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.Field;

public class MatrixUtils
{
    public static final RealMatrixFormat DEFAULT_FORMAT;
    public static final RealMatrixFormat OCTAVE_FORMAT;
    
    private MatrixUtils() {
    }
    
    public static RealMatrix createRealMatrix(final int rows, final int columns) {
        return (RealMatrix)((rows * columns <= 4096) ? new Array2DRowRealMatrix(rows, columns) : new BlockRealMatrix(rows, columns));
    }
    
    public static <T extends FieldElement<T>> FieldMatrix<T> createFieldMatrix(final Field<T> field, final int rows, final int columns) {
        return (FieldMatrix<T>)((rows * columns <= 4096) ? new Array2DRowFieldMatrix<Object>(field, rows, columns) : new BlockFieldMatrix<Object>(field, rows, columns));
    }
    
    public static RealMatrix createRealMatrix(final double[][] data) throws NullArgumentException, DimensionMismatchException, NoDataException {
        if (data == null || data[0] == null) {
            throw new NullArgumentException();
        }
        return (RealMatrix)((data.length * data[0].length <= 4096) ? new Array2DRowRealMatrix(data) : new BlockRealMatrix(data));
    }
    
    public static <T extends FieldElement<T>> FieldMatrix<T> createFieldMatrix(final T[][] data) throws DimensionMismatchException, NoDataException, NullArgumentException {
        if (data == null || data[0] == null) {
            throw new NullArgumentException();
        }
        return (FieldMatrix<T>)((data.length * data[0].length <= 4096) ? new Array2DRowFieldMatrix<Object>((Object[][])data) : new BlockFieldMatrix<Object>((Object[][])data));
    }
    
    public static RealMatrix createRealIdentityMatrix(final int dimension) {
        final RealMatrix m = createRealMatrix(dimension, dimension);
        for (int i = 0; i < dimension; ++i) {
            m.setEntry(i, i, 1.0);
        }
        return m;
    }
    
    public static <T extends FieldElement<T>> FieldMatrix<T> createFieldIdentityMatrix(final Field<T> field, final int dimension) {
        final T zero = field.getZero();
        final T one = field.getOne();
        final T[][] d = MathArrays.buildArray(field, dimension, dimension);
        for (int row = 0; row < dimension; ++row) {
            final T[] dRow = d[row];
            Arrays.fill(dRow, zero);
            dRow[row] = one;
        }
        return new Array2DRowFieldMatrix<T>(field, d, false);
    }
    
    public static RealMatrix createRealDiagonalMatrix(final double[] diagonal) {
        final RealMatrix m = createRealMatrix(diagonal.length, diagonal.length);
        for (int i = 0; i < diagonal.length; ++i) {
            m.setEntry(i, i, diagonal[i]);
        }
        return m;
    }
    
    public static <T extends FieldElement<T>> FieldMatrix<T> createFieldDiagonalMatrix(final T[] diagonal) {
        final FieldMatrix<T> m = createFieldMatrix(diagonal[0].getField(), diagonal.length, diagonal.length);
        for (int i = 0; i < diagonal.length; ++i) {
            m.setEntry(i, i, diagonal[i]);
        }
        return m;
    }
    
    public static RealVector createRealVector(final double[] data) throws NoDataException, NullArgumentException {
        if (data == null) {
            throw new NullArgumentException();
        }
        return new ArrayRealVector(data, true);
    }
    
    public static <T extends FieldElement<T>> FieldVector<T> createFieldVector(final T[] data) throws NoDataException, NullArgumentException, ZeroException {
        if (data == null) {
            throw new NullArgumentException();
        }
        if (data.length == 0) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
        }
        return new ArrayFieldVector<T>(data[0].getField(), data, true);
    }
    
    public static RealMatrix createRowRealMatrix(final double[] rowData) throws NoDataException, NullArgumentException {
        if (rowData == null) {
            throw new NullArgumentException();
        }
        final int nCols = rowData.length;
        final RealMatrix m = createRealMatrix(1, nCols);
        for (int i = 0; i < nCols; ++i) {
            m.setEntry(0, i, rowData[i]);
        }
        return m;
    }
    
    public static <T extends FieldElement<T>> FieldMatrix<T> createRowFieldMatrix(final T[] rowData) throws NoDataException, NullArgumentException {
        if (rowData == null) {
            throw new NullArgumentException();
        }
        final int nCols = rowData.length;
        if (nCols == 0) {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
        }
        final FieldMatrix<T> m = createFieldMatrix(rowData[0].getField(), 1, nCols);
        for (int i = 0; i < nCols; ++i) {
            m.setEntry(0, i, rowData[i]);
        }
        return m;
    }
    
    public static RealMatrix createColumnRealMatrix(final double[] columnData) throws NoDataException, NullArgumentException {
        if (columnData == null) {
            throw new NullArgumentException();
        }
        final int nRows = columnData.length;
        final RealMatrix m = createRealMatrix(nRows, 1);
        for (int i = 0; i < nRows; ++i) {
            m.setEntry(i, 0, columnData[i]);
        }
        return m;
    }
    
    public static <T extends FieldElement<T>> FieldMatrix<T> createColumnFieldMatrix(final T[] columnData) throws NoDataException, NullArgumentException {
        if (columnData == null) {
            throw new NullArgumentException();
        }
        final int nRows = columnData.length;
        if (nRows == 0) {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
        }
        final FieldMatrix<T> m = createFieldMatrix(columnData[0].getField(), nRows, 1);
        for (int i = 0; i < nRows; ++i) {
            m.setEntry(i, 0, columnData[i]);
        }
        return m;
    }
    
    private static boolean isSymmetricInternal(final RealMatrix matrix, final double relativeTolerance, final boolean raiseException) {
        final int rows = matrix.getRowDimension();
        if (rows == matrix.getColumnDimension()) {
            for (int i = 0; i < rows; ++i) {
                int j = i + 1;
                while (j < rows) {
                    final double mij = matrix.getEntry(i, j);
                    final double mji = matrix.getEntry(j, i);
                    if (FastMath.abs(mij - mji) > FastMath.max(FastMath.abs(mij), FastMath.abs(mji)) * relativeTolerance) {
                        if (raiseException) {
                            throw new NonSymmetricMatrixException(i, j, relativeTolerance);
                        }
                        return false;
                    }
                    else {
                        ++j;
                    }
                }
            }
            return true;
        }
        if (raiseException) {
            throw new NonSquareMatrixException(rows, matrix.getColumnDimension());
        }
        return false;
    }
    
    public static void checkSymmetric(final RealMatrix matrix, final double eps) {
        isSymmetricInternal(matrix, eps, true);
    }
    
    public static boolean isSymmetric(final RealMatrix matrix, final double eps) {
        return isSymmetricInternal(matrix, eps, false);
    }
    
    public static void checkMatrixIndex(final AnyMatrix m, final int row, final int column) throws OutOfRangeException {
        checkRowIndex(m, row);
        checkColumnIndex(m, column);
    }
    
    public static void checkRowIndex(final AnyMatrix m, final int row) throws OutOfRangeException {
        if (row < 0 || row >= m.getRowDimension()) {
            throw new OutOfRangeException(LocalizedFormats.ROW_INDEX, row, 0, m.getRowDimension() - 1);
        }
    }
    
    public static void checkColumnIndex(final AnyMatrix m, final int column) throws OutOfRangeException {
        if (column < 0 || column >= m.getColumnDimension()) {
            throw new OutOfRangeException(LocalizedFormats.COLUMN_INDEX, column, 0, m.getColumnDimension() - 1);
        }
    }
    
    public static void checkSubMatrixIndex(final AnyMatrix m, final int startRow, final int endRow, final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
        checkRowIndex(m, startRow);
        checkRowIndex(m, endRow);
        if (endRow < startRow) {
            throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW, endRow, startRow, false);
        }
        checkColumnIndex(m, startColumn);
        checkColumnIndex(m, endColumn);
        if (endColumn < startColumn) {
            throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_COLUMN_AFTER_FINAL_COLUMN, endColumn, startColumn, false);
        }
    }
    
    public static void checkSubMatrixIndex(final AnyMatrix m, final int[] selectedRows, final int[] selectedColumns) throws NoDataException, NullArgumentException, OutOfRangeException {
        if (selectedRows == null) {
            throw new NullArgumentException();
        }
        if (selectedColumns == null) {
            throw new NullArgumentException();
        }
        if (selectedRows.length == 0) {
            throw new NoDataException(LocalizedFormats.EMPTY_SELECTED_ROW_INDEX_ARRAY);
        }
        if (selectedColumns.length == 0) {
            throw new NoDataException(LocalizedFormats.EMPTY_SELECTED_COLUMN_INDEX_ARRAY);
        }
        for (final int row : selectedRows) {
            checkRowIndex(m, row);
        }
        for (final int column : selectedColumns) {
            checkColumnIndex(m, column);
        }
    }
    
    public static void checkAdditionCompatible(final AnyMatrix left, final AnyMatrix right) throws MatrixDimensionMismatchException {
        if (left.getRowDimension() != right.getRowDimension() || left.getColumnDimension() != right.getColumnDimension()) {
            throw new MatrixDimensionMismatchException(left.getRowDimension(), left.getColumnDimension(), right.getRowDimension(), right.getColumnDimension());
        }
    }
    
    public static void checkSubtractionCompatible(final AnyMatrix left, final AnyMatrix right) throws MatrixDimensionMismatchException {
        if (left.getRowDimension() != right.getRowDimension() || left.getColumnDimension() != right.getColumnDimension()) {
            throw new MatrixDimensionMismatchException(left.getRowDimension(), left.getColumnDimension(), right.getRowDimension(), right.getColumnDimension());
        }
    }
    
    public static void checkMultiplicationCompatible(final AnyMatrix left, final AnyMatrix right) throws DimensionMismatchException {
        if (left.getColumnDimension() != right.getRowDimension()) {
            throw new DimensionMismatchException(left.getColumnDimension(), right.getRowDimension());
        }
    }
    
    public static Array2DRowRealMatrix fractionMatrixToRealMatrix(final FieldMatrix<Fraction> m) {
        final FractionMatrixConverter converter = new FractionMatrixConverter();
        m.walkInOptimizedOrder(converter);
        return converter.getConvertedMatrix();
    }
    
    public static Array2DRowRealMatrix bigFractionMatrixToRealMatrix(final FieldMatrix<BigFraction> m) {
        final BigFractionMatrixConverter converter = new BigFractionMatrixConverter();
        m.walkInOptimizedOrder(converter);
        return converter.getConvertedMatrix();
    }
    
    public static void serializeRealVector(final RealVector vector, final ObjectOutputStream oos) throws IOException {
        final int n = vector.getDimension();
        oos.writeInt(n);
        for (int i = 0; i < n; ++i) {
            oos.writeDouble(vector.getEntry(i));
        }
    }
    
    public static void deserializeRealVector(final Object instance, final String fieldName, final ObjectInputStream ois) throws ClassNotFoundException, IOException {
        try {
            final int n = ois.readInt();
            final double[] data = new double[n];
            for (int i = 0; i < n; ++i) {
                data[i] = ois.readDouble();
            }
            final RealVector vector = new ArrayRealVector(data, false);
            final java.lang.reflect.Field f = instance.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(instance, vector);
        }
        catch (final NoSuchFieldException nsfe) {
            final IOException ioe = new IOException();
            ioe.initCause(nsfe);
            throw ioe;
        }
        catch (final IllegalAccessException iae) {
            final IOException ioe = new IOException();
            ioe.initCause(iae);
            throw ioe;
        }
    }
    
    public static void serializeRealMatrix(final RealMatrix matrix, final ObjectOutputStream oos) throws IOException {
        final int n = matrix.getRowDimension();
        final int m = matrix.getColumnDimension();
        oos.writeInt(n);
        oos.writeInt(m);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                oos.writeDouble(matrix.getEntry(i, j));
            }
        }
    }
    
    public static void deserializeRealMatrix(final Object instance, final String fieldName, final ObjectInputStream ois) throws ClassNotFoundException, IOException {
        try {
            final int n = ois.readInt();
            final int m = ois.readInt();
            final double[][] data = new double[n][m];
            for (final double[] dataI : data) {
                for (int j = 0; j < m; ++j) {
                    dataI[j] = ois.readDouble();
                }
            }
            final RealMatrix matrix = new Array2DRowRealMatrix(data, false);
            final java.lang.reflect.Field f = instance.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(instance, matrix);
        }
        catch (final NoSuchFieldException nsfe) {
            final IOException ioe = new IOException();
            ioe.initCause(nsfe);
            throw ioe;
        }
        catch (final IllegalAccessException iae) {
            final IOException ioe = new IOException();
            ioe.initCause(iae);
            throw ioe;
        }
    }
    
    public static void solveLowerTriangularSystem(final RealMatrix rm, final RealVector b) throws DimensionMismatchException, MathArithmeticException, NonSquareMatrixException {
        if (rm == null || b == null || rm.getRowDimension() != b.getDimension()) {
            throw new DimensionMismatchException((rm == null) ? 0 : rm.getRowDimension(), (b == null) ? 0 : b.getDimension());
        }
        if (rm.getColumnDimension() != rm.getRowDimension()) {
            throw new NonSquareMatrixException(rm.getRowDimension(), rm.getColumnDimension());
        }
        for (int rows = rm.getRowDimension(), i = 0; i < rows; ++i) {
            final double diag = rm.getEntry(i, i);
            if (FastMath.abs(diag) < Precision.SAFE_MIN) {
                throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
            }
            final double bi = b.getEntry(i) / diag;
            b.setEntry(i, bi);
            for (int j = i + 1; j < rows; ++j) {
                b.setEntry(j, b.getEntry(j) - bi * rm.getEntry(j, i));
            }
        }
    }
    
    public static void solveUpperTriangularSystem(final RealMatrix rm, final RealVector b) throws DimensionMismatchException, MathArithmeticException, NonSquareMatrixException {
        if (rm == null || b == null || rm.getRowDimension() != b.getDimension()) {
            throw new DimensionMismatchException((rm == null) ? 0 : rm.getRowDimension(), (b == null) ? 0 : b.getDimension());
        }
        if (rm.getColumnDimension() != rm.getRowDimension()) {
            throw new NonSquareMatrixException(rm.getRowDimension(), rm.getColumnDimension());
        }
        final int rows = rm.getRowDimension();
        for (int i = rows - 1; i > -1; --i) {
            final double diag = rm.getEntry(i, i);
            if (FastMath.abs(diag) < Precision.SAFE_MIN) {
                throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
            }
            final double bi = b.getEntry(i) / diag;
            b.setEntry(i, bi);
            for (int j = i - 1; j > -1; --j) {
                b.setEntry(j, b.getEntry(j) - bi * rm.getEntry(j, i));
            }
        }
    }
    
    public static RealMatrix blockInverse(final RealMatrix m, final int splitIndex) {
        final int n = m.getRowDimension();
        if (m.getColumnDimension() != n) {
            throw new NonSquareMatrixException(m.getRowDimension(), m.getColumnDimension());
        }
        final int splitIndex2 = splitIndex + 1;
        final RealMatrix a = m.getSubMatrix(0, splitIndex, 0, splitIndex);
        final RealMatrix b = m.getSubMatrix(0, splitIndex, splitIndex2, n - 1);
        final RealMatrix c = m.getSubMatrix(splitIndex2, n - 1, 0, splitIndex);
        final RealMatrix d = m.getSubMatrix(splitIndex2, n - 1, splitIndex2, n - 1);
        final SingularValueDecomposition aDec = new SingularValueDecomposition(a);
        final DecompositionSolver aSolver = aDec.getSolver();
        if (!aSolver.isNonSingular()) {
            throw new SingularMatrixException();
        }
        final RealMatrix aInv = aSolver.getInverse();
        final SingularValueDecomposition dDec = new SingularValueDecomposition(d);
        final DecompositionSolver dSolver = dDec.getSolver();
        if (!dSolver.isNonSingular()) {
            throw new SingularMatrixException();
        }
        final RealMatrix dInv = dSolver.getInverse();
        final RealMatrix tmp1 = a.subtract(b.multiply(dInv).multiply(c));
        final SingularValueDecomposition tmp1Dec = new SingularValueDecomposition(tmp1);
        final DecompositionSolver tmp1Solver = tmp1Dec.getSolver();
        if (!tmp1Solver.isNonSingular()) {
            throw new SingularMatrixException();
        }
        final RealMatrix result00 = tmp1Solver.getInverse();
        final RealMatrix tmp2 = d.subtract(c.multiply(aInv).multiply(b));
        final SingularValueDecomposition tmp2Dec = new SingularValueDecomposition(tmp2);
        final DecompositionSolver tmp2Solver = tmp2Dec.getSolver();
        if (!tmp2Solver.isNonSingular()) {
            throw new SingularMatrixException();
        }
        final RealMatrix result2 = tmp2Solver.getInverse();
        final RealMatrix result3 = aInv.multiply(b).multiply(result2).scalarMultiply(-1.0);
        final RealMatrix result4 = dInv.multiply(c).multiply(result00).scalarMultiply(-1.0);
        final RealMatrix result5 = new Array2DRowRealMatrix(n, n);
        result5.setSubMatrix(result00.getData(), 0, 0);
        result5.setSubMatrix(result3.getData(), 0, splitIndex2);
        result5.setSubMatrix(result4.getData(), splitIndex2, 0);
        result5.setSubMatrix(result2.getData(), splitIndex2, splitIndex2);
        return result5;
    }
    
    public static RealMatrix inverse(final RealMatrix matrix) throws NullArgumentException, SingularMatrixException, NonSquareMatrixException {
        return inverse(matrix, 0.0);
    }
    
    public static RealMatrix inverse(final RealMatrix matrix, final double threshold) throws NullArgumentException, SingularMatrixException, NonSquareMatrixException {
        MathUtils.checkNotNull(matrix);
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }
        if (matrix instanceof DiagonalMatrix) {
            return ((DiagonalMatrix)matrix).inverse(threshold);
        }
        final QRDecomposition decomposition = new QRDecomposition(matrix, threshold);
        return decomposition.getSolver().getInverse();
    }
    
    static {
        DEFAULT_FORMAT = RealMatrixFormat.getInstance();
        OCTAVE_FORMAT = new RealMatrixFormat("[", "]", "", "", "; ", ", ");
    }
    
    private static class FractionMatrixConverter extends DefaultFieldMatrixPreservingVisitor<Fraction>
    {
        private double[][] data;
        
        FractionMatrixConverter() {
            super(Fraction.ZERO);
        }
        
        @Override
        public void start(final int rows, final int columns, final int startRow, final int endRow, final int startColumn, final int endColumn) {
            this.data = new double[rows][columns];
        }
        
        @Override
        public void visit(final int row, final int column, final Fraction value) {
            this.data[row][column] = value.doubleValue();
        }
        
        Array2DRowRealMatrix getConvertedMatrix() {
            return new Array2DRowRealMatrix(this.data, false);
        }
    }
    
    private static class BigFractionMatrixConverter extends DefaultFieldMatrixPreservingVisitor<BigFraction>
    {
        private double[][] data;
        
        BigFractionMatrixConverter() {
            super(BigFraction.ZERO);
        }
        
        @Override
        public void start(final int rows, final int columns, final int startRow, final int endRow, final int startColumn, final int endColumn) {
            this.data = new double[rows][columns];
        }
        
        @Override
        public void visit(final int row, final int column, final BigFraction value) {
            this.data[row][column] = value.doubleValue();
        }
        
        Array2DRowRealMatrix getConvertedMatrix() {
            return new Array2DRowRealMatrix(this.data, false);
        }
    }
}
