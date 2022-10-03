package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

public class GF2Matrix extends Matrix
{
    private int[][] matrix;
    private int length;
    
    public GF2Matrix(final byte[] array) {
        if (array.length < 9) {
            throw new ArithmeticException("given array is not an encoded matrix over GF(2)");
        }
        this.numRows = LittleEndianConversions.OS2IP(array, 0);
        this.numColumns = LittleEndianConversions.OS2IP(array, 4);
        final int n = (this.numColumns + 7 >>> 3) * this.numRows;
        if (this.numRows <= 0 || n != array.length - 8) {
            throw new ArithmeticException("given array is not an encoded matrix over GF(2)");
        }
        this.length = this.numColumns + 31 >>> 5;
        this.matrix = new int[this.numRows][this.length];
        final int n2 = this.numColumns >> 5;
        final int n3 = this.numColumns & 0x1F;
        int n4 = 8;
        for (int i = 0; i < this.numRows; ++i) {
            for (int j = 0; j < n2; ++j, n4 += 4) {
                this.matrix[i][j] = LittleEndianConversions.OS2IP(array, n4);
            }
            for (int k = 0; k < n3; k += 8) {
                final int[] array2 = this.matrix[i];
                final int n5 = n2;
                array2[n5] ^= (array[n4++] & 0xFF) << k;
            }
        }
    }
    
    public GF2Matrix(final int numColumns, final int[][] matrix) {
        if (matrix[0].length != numColumns + 31 >> 5) {
            throw new ArithmeticException("Int array does not match given number of columns.");
        }
        this.numColumns = numColumns;
        this.numRows = matrix.length;
        this.length = matrix[0].length;
        final int n = numColumns & 0x1F;
        int n2;
        if (n == 0) {
            n2 = -1;
        }
        else {
            n2 = (1 << n) - 1;
        }
        for (int i = 0; i < this.numRows; ++i) {
            final int[] array = matrix[i];
            final int n3 = this.length - 1;
            array[n3] &= n2;
        }
        this.matrix = matrix;
    }
    
    public GF2Matrix(final int n, final char c) {
        this(n, c, new SecureRandom());
    }
    
    public GF2Matrix(final int n, final char c, final SecureRandom secureRandom) {
        if (n <= 0) {
            throw new ArithmeticException("Size of matrix is non-positive.");
        }
        switch (c) {
            case 'Z': {
                this.assignZeroMatrix(n, n);
                break;
            }
            case 'I': {
                this.assignUnitMatrix(n);
                break;
            }
            case 'L': {
                this.assignRandomLowerTriangularMatrix(n, secureRandom);
                break;
            }
            case 'U': {
                this.assignRandomUpperTriangularMatrix(n, secureRandom);
                break;
            }
            case 'R': {
                this.assignRandomRegularMatrix(n, secureRandom);
                break;
            }
            default: {
                throw new ArithmeticException("Unknown matrix type.");
            }
        }
    }
    
    public GF2Matrix(final GF2Matrix gf2Matrix) {
        this.numColumns = gf2Matrix.getNumColumns();
        this.numRows = gf2Matrix.getNumRows();
        this.length = gf2Matrix.length;
        this.matrix = new int[gf2Matrix.matrix.length][];
        for (int i = 0; i < this.matrix.length; ++i) {
            this.matrix[i] = IntUtils.clone(gf2Matrix.matrix[i]);
        }
    }
    
    private GF2Matrix(final int n, final int n2) {
        if (n2 <= 0 || n <= 0) {
            throw new ArithmeticException("size of matrix is non-positive");
        }
        this.assignZeroMatrix(n, n2);
    }
    
    private void assignZeroMatrix(final int numRows, final int numColumns) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.length = numColumns + 31 >>> 5;
        this.matrix = new int[this.numRows][this.length];
        for (int i = 0; i < this.numRows; ++i) {
            for (int j = 0; j < this.length; ++j) {
                this.matrix[i][j] = 0;
            }
        }
    }
    
    private void assignUnitMatrix(final int n) {
        this.numRows = n;
        this.numColumns = n;
        this.length = n + 31 >>> 5;
        this.matrix = new int[this.numRows][this.length];
        for (int i = 0; i < this.numRows; ++i) {
            for (int j = 0; j < this.length; ++j) {
                this.matrix[i][j] = 0;
            }
        }
        for (int k = 0; k < this.numRows; ++k) {
            this.matrix[k][k >>> 5] = 1 << (k & 0x1F);
        }
    }
    
    private void assignRandomLowerTriangularMatrix(final int n, final SecureRandom secureRandom) {
        this.numRows = n;
        this.numColumns = n;
        this.length = n + 31 >>> 5;
        this.matrix = new int[this.numRows][this.length];
        for (int i = 0; i < this.numRows; ++i) {
            final int n2 = i >>> 5;
            final int n3 = i & 0x1F;
            final int n4 = 31 - n3;
            final int n5 = 1 << n3;
            for (int j = 0; j < n2; ++j) {
                this.matrix[i][j] = secureRandom.nextInt();
            }
            this.matrix[i][n2] = (secureRandom.nextInt() >>> n4 | n5);
            for (int k = n2 + 1; k < this.length; ++k) {
                this.matrix[i][k] = 0;
            }
        }
    }
    
    private void assignRandomUpperTriangularMatrix(final int n, final SecureRandom secureRandom) {
        this.numRows = n;
        this.numColumns = n;
        this.length = n + 31 >>> 5;
        this.matrix = new int[this.numRows][this.length];
        final int n2 = n & 0x1F;
        int n3;
        if (n2 == 0) {
            n3 = -1;
        }
        else {
            n3 = (1 << n2) - 1;
        }
        for (int i = 0; i < this.numRows; ++i) {
            final int n4 = i >>> 5;
            final int n6;
            final int n5 = 1 << (n6 = (i & 0x1F));
            for (int j = 0; j < n4; ++j) {
                this.matrix[i][j] = 0;
            }
            this.matrix[i][n4] = (secureRandom.nextInt() << n6 | n5);
            for (int k = n4 + 1; k < this.length; ++k) {
                this.matrix[i][k] = secureRandom.nextInt();
            }
            final int[] array = this.matrix[i];
            final int n7 = this.length - 1;
            array[n7] &= n3;
        }
    }
    
    private void assignRandomRegularMatrix(final int n, final SecureRandom secureRandom) {
        this.numRows = n;
        this.numColumns = n;
        this.length = n + 31 >>> 5;
        this.matrix = new int[this.numRows][this.length];
        final GF2Matrix gf2Matrix = (GF2Matrix)new GF2Matrix(n, 'L', secureRandom).rightMultiply(new GF2Matrix(n, 'U', secureRandom));
        final int[] vector = new Permutation(n, secureRandom).getVector();
        for (int i = 0; i < n; ++i) {
            System.arraycopy(gf2Matrix.matrix[i], 0, this.matrix[vector[i]], 0, this.length);
        }
    }
    
    public static GF2Matrix[] createRandomRegularMatrixAndItsInverse(final int n, final SecureRandom secureRandom) {
        final GF2Matrix[] array = new GF2Matrix[2];
        final int n2 = n + 31 >> 5;
        final GF2Matrix gf2Matrix = new GF2Matrix(n, 'L', secureRandom);
        final GF2Matrix gf2Matrix2 = new GF2Matrix(n, 'U', secureRandom);
        final GF2Matrix gf2Matrix3 = (GF2Matrix)gf2Matrix.rightMultiply(gf2Matrix2);
        final Permutation permutation = new Permutation(n, secureRandom);
        final int[] vector = permutation.getVector();
        final int[][] array2 = new int[n][n2];
        for (int i = 0; i < n; ++i) {
            System.arraycopy(gf2Matrix3.matrix[vector[i]], 0, array2[i], 0, n2);
        }
        array[0] = new GF2Matrix(n, array2);
        final GF2Matrix gf2Matrix4 = new GF2Matrix(n, 'I');
        for (int j = 0; j < n; ++j) {
            final int n3 = j & 0x1F;
            final int n4 = j >>> 5;
            final int n5 = 1 << n3;
            for (int k = j + 1; k < n; ++k) {
                if ((gf2Matrix.matrix[k][n4] & n5) != 0x0) {
                    for (int l = 0; l <= n4; ++l) {
                        final int[] array3 = gf2Matrix4.matrix[k];
                        final int n6 = l;
                        array3[n6] ^= gf2Matrix4.matrix[j][l];
                    }
                }
            }
        }
        final GF2Matrix gf2Matrix5 = new GF2Matrix(n, 'I');
        for (int n7 = n - 1; n7 >= 0; --n7) {
            final int n8 = n7 & 0x1F;
            final int n9 = n7 >>> 5;
            final int n10 = 1 << n8;
            for (int n11 = n7 - 1; n11 >= 0; --n11) {
                if ((gf2Matrix2.matrix[n11][n9] & n10) != 0x0) {
                    for (int n12 = n9; n12 < n2; ++n12) {
                        final int[] array4 = gf2Matrix5.matrix[n11];
                        final int n13 = n12;
                        array4[n13] ^= gf2Matrix5.matrix[n7][n12];
                    }
                }
            }
        }
        array[1] = (GF2Matrix)gf2Matrix5.rightMultiply(gf2Matrix4.rightMultiply(permutation));
        return array;
    }
    
    public int[][] getIntArray() {
        return this.matrix;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public int[] getRow(final int n) {
        return this.matrix[n];
    }
    
    @Override
    public byte[] getEncoded() {
        int n = (this.numColumns + 7 >>> 3) * this.numRows;
        n += 8;
        final byte[] array = new byte[n];
        LittleEndianConversions.I2OSP(this.numRows, array, 0);
        LittleEndianConversions.I2OSP(this.numColumns, array, 4);
        final int n2 = this.numColumns >>> 5;
        final int n3 = this.numColumns & 0x1F;
        int n4 = 8;
        for (int i = 0; i < this.numRows; ++i) {
            for (int j = 0; j < n2; ++j, n4 += 4) {
                LittleEndianConversions.I2OSP(this.matrix[i][j], array, n4);
            }
            for (int k = 0; k < n3; k += 8) {
                array[n4++] = (byte)(this.matrix[i][n2] >>> k & 0xFF);
            }
        }
        return array;
    }
    
    public double getHammingWeight() {
        double n = 0.0;
        double n2 = 0.0;
        final int n3 = this.numColumns & 0x1F;
        int length;
        if (n3 == 0) {
            length = this.length;
        }
        else {
            length = this.length - 1;
        }
        for (int i = 0; i < this.numRows; ++i) {
            for (int j = 0; j < length; ++j) {
                final int n4 = this.matrix[i][j];
                for (int k = 0; k < 32; ++k) {
                    n += (n4 >>> k & 0x1);
                    ++n2;
                }
            }
            final int n5 = this.matrix[i][this.length - 1];
            for (int l = 0; l < n3; ++l) {
                n += (n5 >>> l & 0x1);
                ++n2;
            }
        }
        return n / n2;
    }
    
    @Override
    public boolean isZero() {
        for (int i = 0; i < this.numRows; ++i) {
            for (int j = 0; j < this.length; ++j) {
                if (this.matrix[i][j] != 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public GF2Matrix getLeftSubMatrix() {
        if (this.numColumns <= this.numRows) {
            throw new ArithmeticException("empty submatrix");
        }
        final int n = this.numRows + 31 >> 5;
        final int[][] array = new int[this.numRows][n];
        int n2 = (1 << (this.numRows & 0x1F)) - 1;
        if (n2 == 0) {
            n2 = -1;
        }
        for (int i = this.numRows - 1; i >= 0; --i) {
            System.arraycopy(this.matrix[i], 0, array[i], 0, n);
            final int[] array2 = array[i];
            final int n3 = n - 1;
            array2[n3] &= n2;
        }
        return new GF2Matrix(this.numRows, array);
    }
    
    public GF2Matrix extendLeftCompactForm() {
        final GF2Matrix gf2Matrix = new GF2Matrix(this.numRows, this.numColumns + this.numRows);
        for (int n = this.numRows - 1 + this.numColumns, i = this.numRows - 1; i >= 0; --i, --n) {
            System.arraycopy(this.matrix[i], 0, gf2Matrix.matrix[i], 0, this.length);
            final int[] array = gf2Matrix.matrix[i];
            final int n2 = n >> 5;
            array[n2] |= 1 << (n & 0x1F);
        }
        return gf2Matrix;
    }
    
    public GF2Matrix getRightSubMatrix() {
        if (this.numColumns <= this.numRows) {
            throw new ArithmeticException("empty submatrix");
        }
        final int n = this.numRows >> 5;
        final int n2 = this.numRows & 0x1F;
        final GF2Matrix gf2Matrix = new GF2Matrix(this.numRows, this.numColumns - this.numRows);
        for (int i = this.numRows - 1; i >= 0; --i) {
            if (n2 != 0) {
                int n3 = n;
                for (int j = 0; j < gf2Matrix.length - 1; ++j) {
                    gf2Matrix.matrix[i][j] = (this.matrix[i][n3++] >>> n2 | this.matrix[i][n3] << 32 - n2);
                }
                gf2Matrix.matrix[i][gf2Matrix.length - 1] = this.matrix[i][n3++] >>> n2;
                if (n3 < this.length) {
                    final int[] array = gf2Matrix.matrix[i];
                    final int n4 = gf2Matrix.length - 1;
                    array[n4] |= this.matrix[i][n3] << 32 - n2;
                }
            }
            else {
                System.arraycopy(this.matrix[i], n, gf2Matrix.matrix[i], 0, gf2Matrix.length);
            }
        }
        return gf2Matrix;
    }
    
    public GF2Matrix extendRightCompactForm() {
        final GF2Matrix gf2Matrix = new GF2Matrix(this.numRows, this.numRows + this.numColumns);
        final int n = this.numRows >> 5;
        final int n2 = this.numRows & 0x1F;
        for (int i = this.numRows - 1; i >= 0; --i) {
            final int[] array = gf2Matrix.matrix[i];
            final int n3 = i >> 5;
            array[n3] |= 1 << (i & 0x1F);
            if (n2 != 0) {
                int n4 = n;
                for (int j = 0; j < this.length - 1; ++j) {
                    final int n5 = this.matrix[i][j];
                    final int[] array2 = gf2Matrix.matrix[i];
                    final int n6 = n4++;
                    array2[n6] |= n5 << n2;
                    final int[] array3 = gf2Matrix.matrix[i];
                    final int n7 = n4;
                    array3[n7] |= n5 >>> 32 - n2;
                }
                final int n8 = this.matrix[i][this.length - 1];
                final int[] array4 = gf2Matrix.matrix[i];
                final int n9 = n4++;
                array4[n9] |= n8 << n2;
                if (n4 < gf2Matrix.length) {
                    final int[] array5 = gf2Matrix.matrix[i];
                    final int n10 = n4;
                    array5[n10] |= n8 >>> 32 - n2;
                }
            }
            else {
                System.arraycopy(this.matrix[i], 0, gf2Matrix.matrix[i], n, this.length);
            }
        }
        return gf2Matrix;
    }
    
    public Matrix computeTranspose() {
        final int[][] array = new int[this.numColumns][this.numRows + 31 >>> 5];
        for (int i = 0; i < this.numRows; ++i) {
            for (int j = 0; j < this.numColumns; ++j) {
                final int n = this.matrix[i][j >>> 5] >>> (j & 0x1F) & 0x1;
                final int n2 = i >>> 5;
                final int n3 = i & 0x1F;
                if (n == 1) {
                    final int[] array2 = array[j];
                    final int n4 = n2;
                    array2[n4] |= 1 << n3;
                }
            }
        }
        return new GF2Matrix(this.numRows, array);
    }
    
    @Override
    public Matrix computeInverse() {
        if (this.numRows != this.numColumns) {
            throw new ArithmeticException("Matrix is not invertible.");
        }
        final int[][] array = new int[this.numRows][this.length];
        for (int i = this.numRows - 1; i >= 0; --i) {
            array[i] = IntUtils.clone(this.matrix[i]);
        }
        final int[][] array2 = new int[this.numRows][this.length];
        for (int j = this.numRows - 1; j >= 0; --j) {
            array2[j][j >> 5] = 1 << (j & 0x1F);
        }
        for (int k = 0; k < this.numRows; ++k) {
            final int n = k >> 5;
            final int n2 = 1 << (k & 0x1F);
            if ((array[k][n] & n2) == 0x0) {
                boolean b = false;
                for (int l = k + 1; l < this.numRows; ++l) {
                    if ((array[l][n] & n2) != 0x0) {
                        b = true;
                        swapRows(array, k, l);
                        swapRows(array2, k, l);
                        l = this.numRows;
                    }
                }
                if (!b) {
                    throw new ArithmeticException("Matrix is not invertible.");
                }
            }
            for (int n3 = this.numRows - 1; n3 >= 0; --n3) {
                if (n3 != k && (array[n3][n] & n2) != 0x0) {
                    addToRow(array[k], array[n3], n);
                    addToRow(array2[k], array2[n3], 0);
                }
            }
        }
        return new GF2Matrix(this.numColumns, array2);
    }
    
    public Matrix leftMultiply(final Permutation permutation) {
        final int[] vector = permutation.getVector();
        if (vector.length != this.numRows) {
            throw new ArithmeticException("length mismatch");
        }
        final int[][] array = new int[this.numRows][];
        for (int i = this.numRows - 1; i >= 0; --i) {
            array[i] = IntUtils.clone(this.matrix[vector[i]]);
        }
        return new GF2Matrix(this.numRows, array);
    }
    
    @Override
    public Vector leftMultiply(final Vector vector) {
        if (!(vector instanceof GF2Vector)) {
            throw new ArithmeticException("vector is not defined over GF(2)");
        }
        if (vector.length != this.numRows) {
            throw new ArithmeticException("length mismatch");
        }
        final int[] vecArray = ((GF2Vector)vector).getVecArray();
        final int[] array = new int[this.length];
        final int n = this.numRows >> 5;
        final int n2 = 1 << (this.numRows & 0x1F);
        int n3 = 0;
        for (int i = 0; i < n; ++i) {
            int j = 1;
            do {
                if ((vecArray[i] & j) != 0x0) {
                    for (int k = 0; k < this.length; ++k) {
                        final int[] array2 = array;
                        final int n4 = k;
                        array2[n4] ^= this.matrix[n3][k];
                    }
                }
                ++n3;
                j <<= 1;
            } while (j != 0);
        }
        for (int l = 1; l != n2; l <<= 1) {
            if ((vecArray[n] & l) != 0x0) {
                for (int n5 = 0; n5 < this.length; ++n5) {
                    final int[] array3 = array;
                    final int n6 = n5;
                    array3[n6] ^= this.matrix[n3][n5];
                }
            }
            ++n3;
        }
        return new GF2Vector(array, this.numColumns);
    }
    
    public Vector leftMultiplyLeftCompactForm(final Vector vector) {
        if (!(vector instanceof GF2Vector)) {
            throw new ArithmeticException("vector is not defined over GF(2)");
        }
        if (vector.length != this.numRows) {
            throw new ArithmeticException("length mismatch");
        }
        final int[] vecArray = ((GF2Vector)vector).getVecArray();
        final int[] array = new int[this.numRows + this.numColumns + 31 >>> 5];
        final int n = this.numRows >>> 5;
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            int j = 1;
            do {
                if ((vecArray[i] & j) != 0x0) {
                    for (int k = 0; k < this.length; ++k) {
                        final int[] array2 = array;
                        final int n3 = k;
                        array2[n3] ^= this.matrix[n2][k];
                    }
                    final int n4 = this.numColumns + n2 >>> 5;
                    final int n5 = this.numColumns + n2 & 0x1F;
                    final int[] array3 = array;
                    final int n6 = n4;
                    array3[n6] |= 1 << n5;
                }
                ++n2;
                j <<= 1;
            } while (j != 0);
        }
        for (int n7 = 1 << (this.numRows & 0x1F), l = 1; l != n7; l <<= 1) {
            if ((vecArray[n] & l) != 0x0) {
                for (int n8 = 0; n8 < this.length; ++n8) {
                    final int[] array4 = array;
                    final int n9 = n8;
                    array4[n9] ^= this.matrix[n2][n8];
                }
                final int n10 = this.numColumns + n2 >>> 5;
                final int n11 = this.numColumns + n2 & 0x1F;
                final int[] array5 = array;
                final int n12 = n10;
                array5[n12] |= 1 << n11;
            }
            ++n2;
        }
        return new GF2Vector(array, this.numRows + this.numColumns);
    }
    
    @Override
    public Matrix rightMultiply(final Matrix matrix) {
        if (!(matrix instanceof GF2Matrix)) {
            throw new ArithmeticException("matrix is not defined over GF(2)");
        }
        if (matrix.numRows != this.numColumns) {
            throw new ArithmeticException("length mismatch");
        }
        final GF2Matrix gf2Matrix = (GF2Matrix)matrix;
        final GF2Matrix gf2Matrix2 = new GF2Matrix(this.numRows, matrix.numColumns);
        final int n = this.numColumns & 0x1F;
        int length;
        if (n == 0) {
            length = this.length;
        }
        else {
            length = this.length - 1;
        }
        for (int i = 0; i < this.numRows; ++i) {
            int n2 = 0;
            for (int j = 0; j < length; ++j) {
                final int n3 = this.matrix[i][j];
                for (int k = 0; k < 32; ++k) {
                    if ((n3 & 1 << k) != 0x0) {
                        for (int l = 0; l < gf2Matrix.length; ++l) {
                            final int[] array = gf2Matrix2.matrix[i];
                            final int n4 = l;
                            array[n4] ^= gf2Matrix.matrix[n2][l];
                        }
                    }
                    ++n2;
                }
            }
            final int n5 = this.matrix[i][this.length - 1];
            for (int n6 = 0; n6 < n; ++n6) {
                if ((n5 & 1 << n6) != 0x0) {
                    for (int n7 = 0; n7 < gf2Matrix.length; ++n7) {
                        final int[] array2 = gf2Matrix2.matrix[i];
                        final int n8 = n7;
                        array2[n8] ^= gf2Matrix.matrix[n2][n7];
                    }
                }
                ++n2;
            }
        }
        return gf2Matrix2;
    }
    
    @Override
    public Matrix rightMultiply(final Permutation permutation) {
        final int[] vector = permutation.getVector();
        if (vector.length != this.numColumns) {
            throw new ArithmeticException("length mismatch");
        }
        final GF2Matrix gf2Matrix = new GF2Matrix(this.numRows, this.numColumns);
        for (int i = this.numColumns - 1; i >= 0; --i) {
            final int n = i >>> 5;
            final int n2 = i & 0x1F;
            final int n3 = vector[i] >>> 5;
            final int n4 = vector[i] & 0x1F;
            for (int j = this.numRows - 1; j >= 0; --j) {
                final int[] array = gf2Matrix.matrix[j];
                final int n5 = n;
                array[n5] |= (this.matrix[j][n3] >>> n4 & 0x1) << n2;
            }
        }
        return gf2Matrix;
    }
    
    @Override
    public Vector rightMultiply(final Vector vector) {
        if (!(vector instanceof GF2Vector)) {
            throw new ArithmeticException("vector is not defined over GF(2)");
        }
        if (vector.length != this.numColumns) {
            throw new ArithmeticException("length mismatch");
        }
        final int[] vecArray = ((GF2Vector)vector).getVecArray();
        final int[] array = new int[this.numRows + 31 >>> 5];
        for (int i = 0; i < this.numRows; ++i) {
            int n = 0;
            for (int j = 0; j < this.length; ++j) {
                n ^= (this.matrix[i][j] & vecArray[j]);
            }
            int n2 = 0;
            for (int k = 0; k < 32; ++k) {
                n2 ^= (n >>> k & 0x1);
            }
            if (n2 == 1) {
                final int[] array2 = array;
                final int n3 = i >>> 5;
                array2[n3] |= 1 << (i & 0x1F);
            }
        }
        return new GF2Vector(array, this.numRows);
    }
    
    public Vector rightMultiplyRightCompactForm(final Vector vector) {
        if (!(vector instanceof GF2Vector)) {
            throw new ArithmeticException("vector is not defined over GF(2)");
        }
        if (vector.length != this.numColumns + this.numRows) {
            throw new ArithmeticException("length mismatch");
        }
        final int[] vecArray = ((GF2Vector)vector).getVecArray();
        final int[] array = new int[this.numRows + 31 >>> 5];
        final int n = this.numRows >> 5;
        final int n2 = this.numRows & 0x1F;
        for (int i = 0; i < this.numRows; ++i) {
            int n3 = vecArray[i >> 5] >>> (i & 0x1F) & 0x1;
            int n4 = n;
            if (n2 != 0) {
                for (int j = 0; j < this.length - 1; ++j) {
                    n3 ^= (this.matrix[i][j] & (vecArray[n4++] >>> n2 | vecArray[n4] << 32 - n2));
                }
                int n5 = vecArray[n4++] >>> n2;
                if (n4 < vecArray.length) {
                    n5 |= vecArray[n4] << 32 - n2;
                }
                n3 ^= (this.matrix[i][this.length - 1] & n5);
            }
            else {
                for (int k = 0; k < this.length; ++k) {
                    n3 ^= (this.matrix[i][k] & vecArray[n4++]);
                }
            }
            int n6 = 0;
            for (int l = 0; l < 32; ++l) {
                n6 ^= (n3 & 0x1);
                n3 >>>= 1;
            }
            if (n6 == 1) {
                final int[] array2 = array;
                final int n7 = i >> 5;
                array2[n7] |= 1 << (i & 0x1F);
            }
        }
        return new GF2Vector(array, this.numRows);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof GF2Matrix)) {
            return false;
        }
        final GF2Matrix gf2Matrix = (GF2Matrix)o;
        if (this.numRows != gf2Matrix.numRows || this.numColumns != gf2Matrix.numColumns || this.length != gf2Matrix.length) {
            return false;
        }
        for (int i = 0; i < this.numRows; ++i) {
            if (!IntUtils.equals(this.matrix[i], gf2Matrix.matrix[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int n = (this.numRows * 31 + this.numColumns) * 31 + this.length;
        for (int i = 0; i < this.numRows; ++i) {
            n = n * 31 + this.matrix[i].hashCode();
        }
        return n;
    }
    
    @Override
    public String toString() {
        final int n = this.numColumns & 0x1F;
        int length;
        if (n == 0) {
            length = this.length;
        }
        else {
            length = this.length - 1;
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.numRows; ++i) {
            sb.append(i + ": ");
            for (int j = 0; j < length; ++j) {
                final int n2 = this.matrix[i][j];
                for (int k = 0; k < 32; ++k) {
                    if ((n2 >>> k & 0x1) == 0x0) {
                        sb.append('0');
                    }
                    else {
                        sb.append('1');
                    }
                }
                sb.append(' ');
            }
            final int n3 = this.matrix[i][this.length - 1];
            for (int l = 0; l < n; ++l) {
                if ((n3 >>> l & 0x1) == 0x0) {
                    sb.append('0');
                }
                else {
                    sb.append('1');
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    
    private static void swapRows(final int[][] array, final int n, final int n2) {
        final int[] array2 = array[n];
        array[n] = array[n2];
        array[n2] = array2;
    }
    
    private static void addToRow(final int[] array, final int[] array2, final int n) {
        for (int i = array2.length - 1; i >= n; --i) {
            array2[i] ^= array[i];
        }
    }
}
