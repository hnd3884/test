package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

public final class GoppaCode
{
    private GoppaCode() {
    }
    
    public static GF2Matrix createCanonicalCheckMatrix(final GF2mField gf2mField, final PolynomialGF2mSmallM polynomialGF2mSmallM) {
        final int degree = gf2mField.getDegree();
        final int n = 1 << degree;
        final int degree2 = polynomialGF2mSmallM.getDegree();
        final int[][] array = new int[degree2][n];
        final int[][] array2 = new int[degree2][n];
        for (int i = 0; i < n; ++i) {
            array2[0][i] = gf2mField.inverse(polynomialGF2mSmallM.evaluateAt(i));
        }
        for (int j = 1; j < degree2; ++j) {
            for (int k = 0; k < n; ++k) {
                array2[j][k] = gf2mField.mult(array2[j - 1][k], k);
            }
        }
        for (int l = 0; l < degree2; ++l) {
            for (int n2 = 0; n2 < n; ++n2) {
                for (int n3 = 0; n3 <= l; ++n3) {
                    array[l][n2] = gf2mField.add(array[l][n2], gf2mField.mult(array2[n3][n2], polynomialGF2mSmallM.getCoefficient(degree2 + n3 - l)));
                }
            }
        }
        final int[][] array3 = new int[degree2 * degree][n + 31 >>> 5];
        for (int n4 = 0; n4 < n; ++n4) {
            final int n5 = n4 >>> 5;
            final int n6 = 1 << (n4 & 0x1F);
            for (int n7 = 0; n7 < degree2; ++n7) {
                final int n8 = array[n7][n4];
                for (int n9 = 0; n9 < degree; ++n9) {
                    if ((n8 >>> n9 & 0x1) != 0x0) {
                        final int[] array4 = array3[(n7 + 1) * degree - n9 - 1];
                        final int n10 = n5;
                        array4[n10] ^= n6;
                    }
                }
            }
        }
        return new GF2Matrix(n, array3);
    }
    
    public static MaMaPe computeSystematicForm(final GF2Matrix gf2Matrix, final SecureRandom secureRandom) {
        final int numColumns = gf2Matrix.getNumColumns();
        GF2Matrix gf2Matrix2 = null;
        boolean b;
        Permutation permutation;
        GF2Matrix gf2Matrix3;
        GF2Matrix leftSubMatrix;
        do {
            permutation = new Permutation(numColumns, secureRandom);
            gf2Matrix3 = (GF2Matrix)gf2Matrix.rightMultiply(permutation);
            leftSubMatrix = gf2Matrix3.getLeftSubMatrix();
            try {
                b = true;
                gf2Matrix2 = (GF2Matrix)leftSubMatrix.computeInverse();
            }
            catch (final ArithmeticException ex) {
                b = false;
            }
        } while (!b);
        return new MaMaPe(leftSubMatrix, ((GF2Matrix)gf2Matrix2.rightMultiply(gf2Matrix3)).getRightSubMatrix(), permutation);
    }
    
    public static GF2Vector syndromeDecode(final GF2Vector gf2Vector, final GF2mField gf2mField, final PolynomialGF2mSmallM polynomialGF2mSmallM, final PolynomialGF2mSmallM[] array) {
        final int n = 1 << gf2mField.getDegree();
        final GF2Vector gf2Vector2 = new GF2Vector(n);
        if (!gf2Vector.isZero()) {
            final PolynomialGF2mSmallM[] modPolynomialToFracton = new PolynomialGF2mSmallM(gf2Vector.toExtensionFieldVector(gf2mField)).modInverse(polynomialGF2mSmallM).addMonomial(1).modSquareRootMatrix(array).modPolynomialToFracton(polynomialGF2mSmallM);
            final PolynomialGF2mSmallM add = modPolynomialToFracton[0].multiply(modPolynomialToFracton[0]).add(modPolynomialToFracton[1].multiply(modPolynomialToFracton[1]).multWithMonomial(1));
            final PolynomialGF2mSmallM multWithElement = add.multWithElement(gf2mField.inverse(add.getHeadCoefficient()));
            for (int i = 0; i < n; ++i) {
                if (multWithElement.evaluateAt(i) == 0) {
                    gf2Vector2.setBit(i);
                }
            }
        }
        return gf2Vector2;
    }
    
    public static class MaMaPe
    {
        private GF2Matrix s;
        private GF2Matrix h;
        private Permutation p;
        
        public MaMaPe(final GF2Matrix s, final GF2Matrix h, final Permutation p3) {
            this.s = s;
            this.h = h;
            this.p = p3;
        }
        
        public GF2Matrix getFirstMatrix() {
            return this.s;
        }
        
        public GF2Matrix getSecondMatrix() {
            return this.h;
        }
        
        public Permutation getPermutation() {
            return this.p;
        }
    }
    
    public static class MatrixSet
    {
        private GF2Matrix g;
        private int[] setJ;
        
        public MatrixSet(final GF2Matrix g, final int[] setJ) {
            this.g = g;
            this.setJ = setJ;
        }
        
        public GF2Matrix getG() {
            return this.g;
        }
        
        public int[] getSetJ() {
            return this.setJ;
        }
    }
}
