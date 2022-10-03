package org.bouncycastle.pqc.math.linearalgebra;

public class GF2mMatrix extends Matrix
{
    protected GF2mField field;
    protected int[][] matrix;
    
    public GF2mMatrix(final GF2mField field, final byte[] array) {
        this.field = field;
        int n = 8;
        int n2 = 1;
        while (field.getDegree() > n) {
            ++n2;
            n += 8;
        }
        if (array.length < 5) {
            throw new IllegalArgumentException(" Error: given array is not encoded matrix over GF(2^m)");
        }
        this.numRows = ((array[3] & 0xFF) << 24 ^ (array[2] & 0xFF) << 16 ^ (array[1] & 0xFF) << 8 ^ (array[0] & 0xFF));
        final int n3 = n2 * this.numRows;
        if (this.numRows <= 0 || (array.length - 4) % n3 != 0) {
            throw new IllegalArgumentException(" Error: given array is not encoded matrix over GF(2^m)");
        }
        this.numColumns = (array.length - 4) / n3;
        this.matrix = new int[this.numRows][this.numColumns];
        int n4 = 4;
        for (int i = 0; i < this.numRows; ++i) {
            for (int j = 0; j < this.numColumns; ++j) {
                for (int k = 0; k < n; k += 8) {
                    final int[] array2 = this.matrix[i];
                    final int n5 = j;
                    array2[n5] ^= (array[n4++] & 0xFF) << k;
                }
                if (!this.field.isElementOfThisField(this.matrix[i][j])) {
                    throw new IllegalArgumentException(" Error: given array is not encoded matrix over GF(2^m)");
                }
            }
        }
    }
    
    public GF2mMatrix(final GF2mMatrix gf2mMatrix) {
        this.numRows = gf2mMatrix.numRows;
        this.numColumns = gf2mMatrix.numColumns;
        this.field = gf2mMatrix.field;
        this.matrix = new int[this.numRows][];
        for (int i = 0; i < this.numRows; ++i) {
            this.matrix[i] = IntUtils.clone(gf2mMatrix.matrix[i]);
        }
    }
    
    protected GF2mMatrix(final GF2mField field, final int[][] matrix) {
        this.field = field;
        this.matrix = matrix;
        this.numRows = matrix.length;
        this.numColumns = matrix[0].length;
    }
    
    @Override
    public byte[] getEncoded() {
        int n = 8;
        int n2 = 1;
        while (this.field.getDegree() > n) {
            ++n2;
            n += 8;
        }
        final byte[] array = new byte[this.numRows * this.numColumns * n2 + 4];
        array[0] = (byte)(this.numRows & 0xFF);
        array[1] = (byte)(this.numRows >>> 8 & 0xFF);
        array[2] = (byte)(this.numRows >>> 16 & 0xFF);
        array[3] = (byte)(this.numRows >>> 24 & 0xFF);
        int n3 = 4;
        for (int i = 0; i < this.numRows; ++i) {
            for (int j = 0; j < this.numColumns; ++j) {
                for (int k = 0; k < n; k += 8) {
                    array[n3++] = (byte)(this.matrix[i][j] >>> k);
                }
            }
        }
        return array;
    }
    
    @Override
    public boolean isZero() {
        for (int i = 0; i < this.numRows; ++i) {
            for (int j = 0; j < this.numColumns; ++j) {
                if (this.matrix[i][j] != 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public Matrix computeInverse() {
        if (this.numRows != this.numColumns) {
            throw new ArithmeticException("Matrix is not invertible.");
        }
        final int[][] array = new int[this.numRows][this.numRows];
        for (int i = this.numRows - 1; i >= 0; --i) {
            array[i] = IntUtils.clone(this.matrix[i]);
        }
        final int[][] array2 = new int[this.numRows][this.numRows];
        for (int j = this.numRows - 1; j >= 0; --j) {
            array2[j][j] = 1;
        }
        for (int k = 0; k < this.numRows; ++k) {
            if (array[k][k] == 0) {
                boolean b = false;
                for (int l = k + 1; l < this.numRows; ++l) {
                    if (array[l][k] != 0) {
                        b = true;
                        swapColumns(array, k, l);
                        swapColumns(array2, k, l);
                        l = this.numRows;
                    }
                }
                if (!b) {
                    throw new ArithmeticException("Matrix is not invertible.");
                }
            }
            final int inverse = this.field.inverse(array[k][k]);
            this.multRowWithElementThis(array[k], inverse);
            this.multRowWithElementThis(array2[k], inverse);
            for (int n = 0; n < this.numRows; ++n) {
                if (n != k) {
                    final int n2 = array[n][k];
                    if (n2 != 0) {
                        final int[] multRowWithElement = this.multRowWithElement(array[k], n2);
                        final int[] multRowWithElement2 = this.multRowWithElement(array2[k], n2);
                        this.addToRow(multRowWithElement, array[n]);
                        this.addToRow(multRowWithElement2, array2[n]);
                    }
                }
            }
        }
        return new GF2mMatrix(this.field, array2);
    }
    
    private static void swapColumns(final int[][] array, final int n, final int n2) {
        final int[] array2 = array[n];
        array[n] = array[n2];
        array[n2] = array2;
    }
    
    private void multRowWithElementThis(final int[] array, final int n) {
        for (int i = array.length - 1; i >= 0; --i) {
            array[i] = this.field.mult(array[i], n);
        }
    }
    
    private int[] multRowWithElement(final int[] array, final int n) {
        final int[] array2 = new int[array.length];
        for (int i = array.length - 1; i >= 0; --i) {
            array2[i] = this.field.mult(array[i], n);
        }
        return array2;
    }
    
    private void addToRow(final int[] array, final int[] array2) {
        for (int i = array2.length - 1; i >= 0; --i) {
            array2[i] = this.field.add(array[i], array2[i]);
        }
    }
    
    @Override
    public Matrix rightMultiply(final Matrix matrix) {
        throw new RuntimeException("Not implemented.");
    }
    
    @Override
    public Matrix rightMultiply(final Permutation permutation) {
        throw new RuntimeException("Not implemented.");
    }
    
    @Override
    public Vector leftMultiply(final Vector vector) {
        throw new RuntimeException("Not implemented.");
    }
    
    @Override
    public Vector rightMultiply(final Vector vector) {
        throw new RuntimeException("Not implemented.");
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof GF2mMatrix)) {
            return false;
        }
        final GF2mMatrix gf2mMatrix = (GF2mMatrix)o;
        if (!this.field.equals(gf2mMatrix.field) || gf2mMatrix.numRows != this.numColumns || gf2mMatrix.numColumns != this.numColumns) {
            return false;
        }
        for (int i = 0; i < this.numRows; ++i) {
            for (int j = 0; j < this.numColumns; ++j) {
                if (this.matrix[i][j] != gf2mMatrix.matrix[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int n = (this.field.hashCode() * 31 + this.numRows) * 31 + this.numColumns;
        for (int i = 0; i < this.numRows; ++i) {
            for (int j = 0; j < this.numColumns; ++j) {
                n = n * 31 + this.matrix[i][j];
            }
        }
        return n;
    }
    
    @Override
    public String toString() {
        String s = this.numRows + " x " + this.numColumns + " Matrix over " + this.field.toString() + ": \n";
        for (int i = 0; i < this.numRows; ++i) {
            for (int j = 0; j < this.numColumns; ++j) {
                s = s + this.field.elementToStr(this.matrix[i][j]) + " : ";
            }
            s += "\n";
        }
        return s;
    }
}
