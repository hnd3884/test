package org.bouncycastle.pqc.math.linearalgebra;

public class PolynomialRingGF2m
{
    private GF2mField field;
    private PolynomialGF2mSmallM p;
    protected PolynomialGF2mSmallM[] sqMatrix;
    protected PolynomialGF2mSmallM[] sqRootMatrix;
    
    public PolynomialRingGF2m(final GF2mField field, final PolynomialGF2mSmallM p2) {
        this.field = field;
        this.p = p2;
        this.computeSquaringMatrix();
        this.computeSquareRootMatrix();
    }
    
    public PolynomialGF2mSmallM[] getSquaringMatrix() {
        return this.sqMatrix;
    }
    
    public PolynomialGF2mSmallM[] getSquareRootMatrix() {
        return this.sqRootMatrix;
    }
    
    private void computeSquaringMatrix() {
        final int degree = this.p.getDegree();
        this.sqMatrix = new PolynomialGF2mSmallM[degree];
        for (int i = 0; i < degree >> 1; ++i) {
            final int[] array = new int[(i << 1) + 1];
            array[i << 1] = 1;
            this.sqMatrix[i] = new PolynomialGF2mSmallM(this.field, array);
        }
        for (int j = degree >> 1; j < degree; ++j) {
            final int[] array2 = new int[(j << 1) + 1];
            array2[j << 1] = 1;
            this.sqMatrix[j] = new PolynomialGF2mSmallM(this.field, array2).mod(this.p);
        }
    }
    
    private void computeSquareRootMatrix() {
        final int degree = this.p.getDegree();
        final PolynomialGF2mSmallM[] array = new PolynomialGF2mSmallM[degree];
        for (int i = degree - 1; i >= 0; --i) {
            array[i] = new PolynomialGF2mSmallM(this.sqMatrix[i]);
        }
        this.sqRootMatrix = new PolynomialGF2mSmallM[degree];
        for (int j = degree - 1; j >= 0; --j) {
            this.sqRootMatrix[j] = new PolynomialGF2mSmallM(this.field, j);
        }
        for (int k = 0; k < degree; ++k) {
            if (array[k].getCoefficient(k) == 0) {
                boolean b = false;
                for (int l = k + 1; l < degree; ++l) {
                    if (array[l].getCoefficient(k) != 0) {
                        b = true;
                        swapColumns(array, k, l);
                        swapColumns(this.sqRootMatrix, k, l);
                        l = degree;
                    }
                }
                if (!b) {
                    throw new ArithmeticException("Squaring matrix is not invertible.");
                }
            }
            final int inverse = this.field.inverse(array[k].getCoefficient(k));
            array[k].multThisWithElement(inverse);
            this.sqRootMatrix[k].multThisWithElement(inverse);
            for (int n = 0; n < degree; ++n) {
                if (n != k) {
                    final int coefficient = array[n].getCoefficient(k);
                    if (coefficient != 0) {
                        final PolynomialGF2mSmallM multWithElement = array[k].multWithElement(coefficient);
                        final PolynomialGF2mSmallM multWithElement2 = this.sqRootMatrix[k].multWithElement(coefficient);
                        array[n].addToThis(multWithElement);
                        this.sqRootMatrix[n].addToThis(multWithElement2);
                    }
                }
            }
        }
    }
    
    private static void swapColumns(final PolynomialGF2mSmallM[] array, final int n, final int n2) {
        final PolynomialGF2mSmallM polynomialGF2mSmallM = array[n];
        array[n] = array[n2];
        array[n2] = polynomialGF2mSmallM;
    }
}
