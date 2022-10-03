package org.bouncycastle.pqc.crypto.rainbow.util;

public class ComputeInField
{
    private short[][] A;
    short[] x;
    
    public short[] solveEquation(final short[][] array, final short[] array2) {
        if (array.length != array2.length) {
            return null;
        }
        try {
            this.A = new short[array.length][array.length + 1];
            this.x = new short[array.length];
            for (int i = 0; i < array.length; ++i) {
                for (int j = 0; j < array[0].length; ++j) {
                    this.A[i][j] = array[i][j];
                }
            }
            for (int k = 0; k < array2.length; ++k) {
                this.A[k][array2.length] = GF2Field.addElem(array2[k], this.A[k][array2.length]);
            }
            this.computeZerosUnder(false);
            this.substitute();
            return this.x;
        }
        catch (final RuntimeException ex) {
            return null;
        }
    }
    
    public short[][] inverse(final short[][] array) {
        try {
            this.A = new short[array.length][2 * array.length];
            if (array.length != array[0].length) {
                throw new RuntimeException("The matrix is not invertible. Please choose another one!");
            }
            for (int i = 0; i < array.length; ++i) {
                for (int j = 0; j < array.length; ++j) {
                    this.A[i][j] = array[i][j];
                }
                for (int k = array.length; k < 2 * array.length; ++k) {
                    this.A[i][k] = 0;
                }
                this.A[i][i + this.A.length] = 1;
            }
            this.computeZerosUnder(true);
            for (int l = 0; l < this.A.length; ++l) {
                final short invElem = GF2Field.invElem(this.A[l][l]);
                for (int n = l; n < 2 * this.A.length; ++n) {
                    this.A[l][n] = GF2Field.multElem(this.A[l][n], invElem);
                }
            }
            this.computeZerosAbove();
            final short[][] array2 = new short[this.A.length][this.A.length];
            for (int n2 = 0; n2 < this.A.length; ++n2) {
                for (int length = this.A.length; length < 2 * this.A.length; ++length) {
                    array2[n2][length - this.A.length] = this.A[n2][length];
                }
            }
            return array2;
        }
        catch (final RuntimeException ex) {
            return null;
        }
    }
    
    private void computeZerosUnder(final boolean b) throws RuntimeException {
        int n;
        if (b) {
            n = 2 * this.A.length;
        }
        else {
            n = this.A.length + 1;
        }
        for (int i = 0; i < this.A.length - 1; ++i) {
            for (int j = i + 1; j < this.A.length; ++j) {
                final short n2 = this.A[j][i];
                final short invElem = GF2Field.invElem(this.A[i][i]);
                if (invElem == 0) {
                    throw new IllegalStateException("Matrix not invertible! We have to choose another one!");
                }
                for (int k = i; k < n; ++k) {
                    this.A[j][k] = GF2Field.addElem(this.A[j][k], GF2Field.multElem(n2, GF2Field.multElem(this.A[i][k], invElem)));
                }
            }
        }
    }
    
    private void computeZerosAbove() throws RuntimeException {
        for (int i = this.A.length - 1; i > 0; --i) {
            for (int j = i - 1; j >= 0; --j) {
                final short n = this.A[j][i];
                final short invElem = GF2Field.invElem(this.A[i][i]);
                if (invElem == 0) {
                    throw new RuntimeException("The matrix is not invertible");
                }
                for (int k = i; k < 2 * this.A.length; ++k) {
                    this.A[j][k] = GF2Field.addElem(this.A[j][k], GF2Field.multElem(n, GF2Field.multElem(this.A[i][k], invElem)));
                }
            }
        }
    }
    
    private void substitute() throws IllegalStateException {
        final short invElem = GF2Field.invElem(this.A[this.A.length - 1][this.A.length - 1]);
        if (invElem == 0) {
            throw new IllegalStateException("The equation system is not solvable");
        }
        this.x[this.A.length - 1] = GF2Field.multElem(this.A[this.A.length - 1][this.A.length], invElem);
        for (int i = this.A.length - 2; i >= 0; --i) {
            short addElem = this.A[i][this.A.length];
            for (int j = this.A.length - 1; j > i; --j) {
                addElem = GF2Field.addElem(addElem, GF2Field.multElem(this.A[i][j], this.x[j]));
            }
            final short invElem2 = GF2Field.invElem(this.A[i][i]);
            if (invElem2 == 0) {
                throw new IllegalStateException("Not solvable equation system");
            }
            this.x[i] = GF2Field.multElem(addElem, invElem2);
        }
    }
    
    public short[][] multiplyMatrix(final short[][] array, final short[][] array2) throws RuntimeException {
        if (array[0].length != array2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        this.A = new short[array.length][array2[0].length];
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < array2.length; ++j) {
                for (int k = 0; k < array2[0].length; ++k) {
                    this.A[i][k] = GF2Field.addElem(this.A[i][k], GF2Field.multElem(array[i][j], array2[j][k]));
                }
            }
        }
        return this.A;
    }
    
    public short[] multiplyMatrix(final short[][] array, final short[] array2) throws RuntimeException {
        if (array[0].length != array2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        final short[] array3 = new short[array.length];
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < array2.length; ++j) {
                array3[i] = GF2Field.addElem(array3[i], GF2Field.multElem(array[i][j], array2[j]));
            }
        }
        return array3;
    }
    
    public short[] addVect(final short[] array, final short[] array2) {
        if (array.length != array2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        final short[] array3 = new short[array.length];
        for (int i = 0; i < array3.length; ++i) {
            array3[i] = GF2Field.addElem(array[i], array2[i]);
        }
        return array3;
    }
    
    public short[][] multVects(final short[] array, final short[] array2) {
        if (array.length != array2.length) {
            throw new RuntimeException("Multiplication is not possible!");
        }
        final short[][] array3 = new short[array.length][array2.length];
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < array2.length; ++j) {
                array3[i][j] = GF2Field.multElem(array[i], array2[j]);
            }
        }
        return array3;
    }
    
    public short[] multVect(final short n, final short[] array) {
        final short[] array2 = new short[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = GF2Field.multElem(n, array[i]);
        }
        return array2;
    }
    
    public short[][] multMatrix(final short n, final short[][] array) {
        final short[][] array2 = new short[array.length][array[0].length];
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < array[0].length; ++j) {
                array2[i][j] = GF2Field.multElem(n, array[i][j]);
            }
        }
        return array2;
    }
    
    public short[][] addSquareMatrix(final short[][] array, final short[][] array2) {
        if (array.length != array2.length || array[0].length != array2[0].length) {
            throw new RuntimeException("Addition is not possible!");
        }
        final short[][] array3 = new short[array.length][array.length];
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < array2.length; ++j) {
                array3[i][j] = GF2Field.addElem(array[i][j], array2[i][j]);
            }
        }
        return array3;
    }
}
