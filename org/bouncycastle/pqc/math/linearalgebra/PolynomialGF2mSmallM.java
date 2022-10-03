package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

public class PolynomialGF2mSmallM
{
    private GF2mField field;
    private int degree;
    private int[] coefficients;
    public static final char RANDOM_IRREDUCIBLE_POLYNOMIAL = 'I';
    
    public PolynomialGF2mSmallM(final GF2mField field) {
        this.field = field;
        this.degree = -1;
        this.coefficients = new int[1];
    }
    
    public PolynomialGF2mSmallM(final GF2mField field, final int n, final char c, final SecureRandom secureRandom) {
        this.field = field;
        switch (c) {
            case 'I': {
                this.coefficients = this.createRandomIrreduciblePolynomial(n, secureRandom);
                this.computeDegree();
                return;
            }
            default: {
                throw new IllegalArgumentException(" Error: type " + c + " is not defined for GF2smallmPolynomial");
            }
        }
    }
    
    private int[] createRandomIrreduciblePolynomial(final int n, final SecureRandom secureRandom) {
        final int[] array = new int[n + 1];
        array[n] = 1;
        array[0] = this.field.getRandomNonZeroElement(secureRandom);
        for (int i = 1; i < n; ++i) {
            array[i] = this.field.getRandomElement(secureRandom);
        }
        while (!this.isIrreducible(array)) {
            final int nextInt = RandUtils.nextInt(secureRandom, n);
            if (nextInt == 0) {
                array[0] = this.field.getRandomNonZeroElement(secureRandom);
            }
            else {
                array[nextInt] = this.field.getRandomElement(secureRandom);
            }
        }
        return array;
    }
    
    public PolynomialGF2mSmallM(final GF2mField field, final int degree) {
        this.field = field;
        this.degree = degree;
        (this.coefficients = new int[degree + 1])[degree] = 1;
    }
    
    public PolynomialGF2mSmallM(final GF2mField field, final int[] array) {
        this.field = field;
        this.coefficients = normalForm(array);
        this.computeDegree();
    }
    
    public PolynomialGF2mSmallM(final GF2mField field, final byte[] array) {
        this.field = field;
        int n = 8;
        int n2 = 1;
        while (field.getDegree() > n) {
            ++n2;
            n += 8;
        }
        if (array.length % n2 != 0) {
            throw new IllegalArgumentException(" Error: byte array is not encoded polynomial over given finite field GF2m");
        }
        this.coefficients = new int[array.length / n2];
        int n3 = 0;
        for (int i = 0; i < this.coefficients.length; ++i) {
            for (int j = 0; j < n; j += 8) {
                final int[] coefficients = this.coefficients;
                final int n4 = i;
                coefficients[n4] ^= (array[n3++] & 0xFF) << j;
            }
            if (!this.field.isElementOfThisField(this.coefficients[i])) {
                throw new IllegalArgumentException(" Error: byte array is not encoded polynomial over given finite field GF2m");
            }
        }
        if (this.coefficients.length != 1 && this.coefficients[this.coefficients.length - 1] == 0) {
            throw new IllegalArgumentException(" Error: byte array is not encoded polynomial over given finite field GF2m");
        }
        this.computeDegree();
    }
    
    public PolynomialGF2mSmallM(final PolynomialGF2mSmallM polynomialGF2mSmallM) {
        this.field = polynomialGF2mSmallM.field;
        this.degree = polynomialGF2mSmallM.degree;
        this.coefficients = IntUtils.clone(polynomialGF2mSmallM.coefficients);
    }
    
    public PolynomialGF2mSmallM(final GF2mVector gf2mVector) {
        this(gf2mVector.getField(), gf2mVector.getIntArrayForm());
    }
    
    public int getDegree() {
        final int n = this.coefficients.length - 1;
        if (this.coefficients[n] == 0) {
            return -1;
        }
        return n;
    }
    
    public int getHeadCoefficient() {
        if (this.degree == -1) {
            return 0;
        }
        return this.coefficients[this.degree];
    }
    
    private static int headCoefficient(final int[] array) {
        final int computeDegree = computeDegree(array);
        if (computeDegree == -1) {
            return 0;
        }
        return array[computeDegree];
    }
    
    public int getCoefficient(final int n) {
        if (n < 0 || n > this.degree) {
            return 0;
        }
        return this.coefficients[n];
    }
    
    public byte[] getEncoded() {
        int n = 8;
        int n2 = 1;
        while (this.field.getDegree() > n) {
            ++n2;
            n += 8;
        }
        final byte[] array = new byte[this.coefficients.length * n2];
        int n3 = 0;
        for (int i = 0; i < this.coefficients.length; ++i) {
            for (int j = 0; j < n; j += 8) {
                array[n3++] = (byte)(this.coefficients[i] >>> j);
            }
        }
        return array;
    }
    
    public int evaluateAt(final int n) {
        int n2 = this.coefficients[this.degree];
        for (int i = this.degree - 1; i >= 0; --i) {
            n2 = (this.field.mult(n2, n) ^ this.coefficients[i]);
        }
        return n2;
    }
    
    public PolynomialGF2mSmallM add(final PolynomialGF2mSmallM polynomialGF2mSmallM) {
        return new PolynomialGF2mSmallM(this.field, this.add(this.coefficients, polynomialGF2mSmallM.coefficients));
    }
    
    public void addToThis(final PolynomialGF2mSmallM polynomialGF2mSmallM) {
        this.coefficients = this.add(this.coefficients, polynomialGF2mSmallM.coefficients);
        this.computeDegree();
    }
    
    private int[] add(final int[] array, final int[] array2) {
        int[] array3;
        int[] array4;
        if (array.length < array2.length) {
            array3 = new int[array2.length];
            System.arraycopy(array2, 0, array3, 0, array2.length);
            array4 = array;
        }
        else {
            array3 = new int[array.length];
            System.arraycopy(array, 0, array3, 0, array.length);
            array4 = array2;
        }
        for (int i = array4.length - 1; i >= 0; --i) {
            array3[i] = this.field.add(array3[i], array4[i]);
        }
        return array3;
    }
    
    public PolynomialGF2mSmallM addMonomial(final int n) {
        final int[] array = new int[n + 1];
        array[n] = 1;
        return new PolynomialGF2mSmallM(this.field, this.add(this.coefficients, array));
    }
    
    public PolynomialGF2mSmallM multWithElement(final int n) {
        if (!this.field.isElementOfThisField(n)) {
            throw new ArithmeticException("Not an element of the finite field this polynomial is defined over.");
        }
        return new PolynomialGF2mSmallM(this.field, this.multWithElement(this.coefficients, n));
    }
    
    public void multThisWithElement(final int n) {
        if (!this.field.isElementOfThisField(n)) {
            throw new ArithmeticException("Not an element of the finite field this polynomial is defined over.");
        }
        this.coefficients = this.multWithElement(this.coefficients, n);
        this.computeDegree();
    }
    
    private int[] multWithElement(final int[] array, final int n) {
        final int computeDegree = computeDegree(array);
        if (computeDegree == -1 || n == 0) {
            return new int[1];
        }
        if (n == 1) {
            return IntUtils.clone(array);
        }
        final int[] array2 = new int[computeDegree + 1];
        for (int i = computeDegree; i >= 0; --i) {
            array2[i] = this.field.mult(array[i], n);
        }
        return array2;
    }
    
    public PolynomialGF2mSmallM multWithMonomial(final int n) {
        return new PolynomialGF2mSmallM(this.field, multWithMonomial(this.coefficients, n));
    }
    
    private static int[] multWithMonomial(final int[] array, final int n) {
        final int computeDegree = computeDegree(array);
        if (computeDegree == -1) {
            return new int[1];
        }
        final int[] array2 = new int[computeDegree + n + 1];
        System.arraycopy(array, 0, array2, n, computeDegree + 1);
        return array2;
    }
    
    public PolynomialGF2mSmallM[] div(final PolynomialGF2mSmallM polynomialGF2mSmallM) {
        final int[][] div = this.div(this.coefficients, polynomialGF2mSmallM.coefficients);
        return new PolynomialGF2mSmallM[] { new PolynomialGF2mSmallM(this.field, div[0]), new PolynomialGF2mSmallM(this.field, div[1]) };
    }
    
    private int[][] div(final int[] array, final int[] array2) {
        final int i = computeDegree(array2);
        final int n = computeDegree(array) + 1;
        if (i == -1) {
            throw new ArithmeticException("Division by zero.");
        }
        final int[][] array3 = { new int[1], new int[n] };
        final int inverse = this.field.inverse(headCoefficient(array2));
        System.arraycopy(array, array3[0][0] = 0, array3[1], 0, array3[1].length);
        while (i <= computeDegree(array3[1])) {
            final int[] array4 = { this.field.mult(headCoefficient(array3[1]), inverse) };
            final int[] multWithElement = this.multWithElement(array2, array4[0]);
            final int n2 = computeDegree(array3[1]) - i;
            final int[] multWithMonomial = multWithMonomial(multWithElement, n2);
            array3[0] = this.add(multWithMonomial(array4, n2), array3[0]);
            array3[1] = this.add(multWithMonomial, array3[1]);
        }
        return array3;
    }
    
    public PolynomialGF2mSmallM gcd(final PolynomialGF2mSmallM polynomialGF2mSmallM) {
        return new PolynomialGF2mSmallM(this.field, this.gcd(this.coefficients, polynomialGF2mSmallM.coefficients));
    }
    
    private int[] gcd(final int[] array, final int[] array2) {
        int[] array3 = array;
        int[] array4 = array2;
        if (computeDegree(array3) == -1) {
            return array4;
        }
        while (computeDegree(array4) != -1) {
            final int[] mod = this.mod(array3, array4);
            array3 = new int[array4.length];
            System.arraycopy(array4, 0, array3, 0, array3.length);
            array4 = new int[mod.length];
            System.arraycopy(mod, 0, array4, 0, array4.length);
        }
        return this.multWithElement(array3, this.field.inverse(headCoefficient(array3)));
    }
    
    public PolynomialGF2mSmallM multiply(final PolynomialGF2mSmallM polynomialGF2mSmallM) {
        return new PolynomialGF2mSmallM(this.field, this.multiply(this.coefficients, polynomialGF2mSmallM.coefficients));
    }
    
    private int[] multiply(final int[] array, final int[] array2) {
        int[] array3;
        int[] array4;
        if (computeDegree(array) < computeDegree(array2)) {
            array3 = array2;
            array4 = array;
        }
        else {
            array3 = array;
            array4 = array2;
        }
        final int[] normalForm = normalForm(array3);
        final int[] normalForm2 = normalForm(array4);
        if (normalForm2.length == 1) {
            return this.multWithElement(normalForm, normalForm2[0]);
        }
        final int length = normalForm.length;
        final int length2 = normalForm2.length;
        final int[] array5 = new int[length + length2 - 1];
        int[] array8;
        if (length2 != length) {
            final int[] array6 = new int[length2];
            final int[] array7 = new int[length - length2];
            System.arraycopy(normalForm, 0, array6, 0, array6.length);
            System.arraycopy(normalForm, length2, array7, 0, array7.length);
            array8 = this.add(this.multiply(array6, normalForm2), multWithMonomial(this.multiply(array7, normalForm2), length2));
        }
        else {
            final int n = length + 1 >>> 1;
            final int n2 = length - n;
            final int[] array9 = new int[n];
            final int[] array10 = new int[n];
            final int[] array11 = new int[n2];
            final int[] array12 = new int[n2];
            System.arraycopy(normalForm, 0, array9, 0, array9.length);
            System.arraycopy(normalForm, n, array11, 0, array11.length);
            System.arraycopy(normalForm2, 0, array10, 0, array10.length);
            System.arraycopy(normalForm2, n, array12, 0, array12.length);
            final int[] add = this.add(array9, array11);
            final int[] add2 = this.add(array10, array12);
            final int[] multiply = this.multiply(array9, array10);
            final int[] multiply2 = this.multiply(add, add2);
            final int[] multiply3 = this.multiply(array11, array12);
            array8 = this.add(multWithMonomial(this.add(this.add(this.add(multiply2, multiply), multiply3), multWithMonomial(multiply3, n)), n), multiply);
        }
        return array8;
    }
    
    private boolean isIrreducible(final int[] array) {
        if (array[0] == 0) {
            return false;
        }
        final int n = computeDegree(array) >> 1;
        int[] array2 = { 0, 1 };
        final int[] array3 = { 0, 1 };
        final int degree = this.field.getDegree();
        for (int i = 0; i < n; ++i) {
            for (int j = degree - 1; j >= 0; --j) {
                array2 = this.modMultiply(array2, array2, array);
            }
            array2 = normalForm(array2);
            if (computeDegree(this.gcd(this.add(array2, array3), array)) != 0) {
                return false;
            }
        }
        return true;
    }
    
    public PolynomialGF2mSmallM mod(final PolynomialGF2mSmallM polynomialGF2mSmallM) {
        return new PolynomialGF2mSmallM(this.field, this.mod(this.coefficients, polynomialGF2mSmallM.coefficients));
    }
    
    private int[] mod(final int[] array, final int[] array2) {
        final int i = computeDegree(array2);
        if (i == -1) {
            throw new ArithmeticException("Division by zero");
        }
        int[] add = new int[array.length];
        final int inverse = this.field.inverse(headCoefficient(array2));
        System.arraycopy(array, 0, add, 0, add.length);
        while (i <= computeDegree(add)) {
            add = this.add(this.multWithElement(multWithMonomial(array2, computeDegree(add) - i), this.field.mult(headCoefficient(add), inverse)), add);
        }
        return add;
    }
    
    public PolynomialGF2mSmallM modMultiply(final PolynomialGF2mSmallM polynomialGF2mSmallM, final PolynomialGF2mSmallM polynomialGF2mSmallM2) {
        return new PolynomialGF2mSmallM(this.field, this.modMultiply(this.coefficients, polynomialGF2mSmallM.coefficients, polynomialGF2mSmallM2.coefficients));
    }
    
    public PolynomialGF2mSmallM modSquareMatrix(final PolynomialGF2mSmallM[] array) {
        final int length = array.length;
        final int[] array2 = new int[length];
        final int[] array3 = new int[length];
        for (int i = 0; i < this.coefficients.length; ++i) {
            array3[i] = this.field.mult(this.coefficients[i], this.coefficients[i]);
        }
        for (int j = 0; j < length; ++j) {
            for (int k = 0; k < length; ++k) {
                if (j < array[k].coefficients.length) {
                    array2[j] = this.field.add(array2[j], this.field.mult(array[k].coefficients[j], array3[k]));
                }
            }
        }
        return new PolynomialGF2mSmallM(this.field, array2);
    }
    
    private int[] modMultiply(final int[] array, final int[] array2, final int[] array3) {
        return this.mod(this.multiply(array, array2), array3);
    }
    
    public PolynomialGF2mSmallM modSquareRoot(final PolynomialGF2mSmallM polynomialGF2mSmallM) {
        int[] array = IntUtils.clone(this.coefficients);
        for (int[] array2 = this.modMultiply(array, array, polynomialGF2mSmallM.coefficients); !isEqual(array2, this.coefficients); array2 = this.modMultiply(array, array, polynomialGF2mSmallM.coefficients)) {
            array = normalForm(array2);
        }
        return new PolynomialGF2mSmallM(this.field, array);
    }
    
    public PolynomialGF2mSmallM modSquareRootMatrix(final PolynomialGF2mSmallM[] array) {
        final int length = array.length;
        final int[] array2 = new int[length];
        for (int i = 0; i < length; ++i) {
            for (int j = 0; j < length; ++j) {
                if (i < array[j].coefficients.length) {
                    if (j < this.coefficients.length) {
                        array2[i] = this.field.add(array2[i], this.field.mult(array[j].coefficients[i], this.coefficients[j]));
                    }
                }
            }
        }
        for (int k = 0; k < length; ++k) {
            array2[k] = this.field.sqRoot(array2[k]);
        }
        return new PolynomialGF2mSmallM(this.field, array2);
    }
    
    public PolynomialGF2mSmallM modDiv(final PolynomialGF2mSmallM polynomialGF2mSmallM, final PolynomialGF2mSmallM polynomialGF2mSmallM2) {
        return new PolynomialGF2mSmallM(this.field, this.modDiv(this.coefficients, polynomialGF2mSmallM.coefficients, polynomialGF2mSmallM2.coefficients));
    }
    
    private int[] modDiv(final int[] array, final int[] array2, final int[] array3) {
        int[] array4 = normalForm(array3);
        int[] array5 = this.mod(array2, array3);
        int[] normalForm = { 0 };
        int[][] div;
        int[] add;
        for (int[] array6 = this.mod(array, array3); computeDegree(array5) != -1; array5 = normalForm(div[1]), add = this.add(normalForm, this.modMultiply(div[0], array6, array3)), normalForm = normalForm(array6), array6 = normalForm(add)) {
            div = this.div(array4, array5);
            array4 = normalForm(array5);
        }
        return this.multWithElement(normalForm, this.field.inverse(headCoefficient(array4)));
    }
    
    public PolynomialGF2mSmallM modInverse(final PolynomialGF2mSmallM polynomialGF2mSmallM) {
        return new PolynomialGF2mSmallM(this.field, this.modDiv(new int[] { 1 }, this.coefficients, polynomialGF2mSmallM.coefficients));
    }
    
    public PolynomialGF2mSmallM[] modPolynomialToFracton(final PolynomialGF2mSmallM polynomialGF2mSmallM) {
        final int n = polynomialGF2mSmallM.degree >> 1;
        int[] normalForm = normalForm(polynomialGF2mSmallM.coefficients);
        int[] mod;
        int[] array;
        int[] array2;
        int[][] div;
        int[] add;
        for (mod = this.mod(this.coefficients, polynomialGF2mSmallM.coefficients), array = new int[] { 0 }, array2 = new int[] { 1 }; computeDegree(mod) > n; mod = div[1], add = this.add(array, this.modMultiply(div[0], array2, polynomialGF2mSmallM.coefficients)), array = array2, array2 = add) {
            div = this.div(normalForm, mod);
            normalForm = mod;
        }
        return new PolynomialGF2mSmallM[] { new PolynomialGF2mSmallM(this.field, mod), new PolynomialGF2mSmallM(this.field, array2) };
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof PolynomialGF2mSmallM)) {
            return false;
        }
        final PolynomialGF2mSmallM polynomialGF2mSmallM = (PolynomialGF2mSmallM)o;
        return this.field.equals(polynomialGF2mSmallM.field) && this.degree == polynomialGF2mSmallM.degree && isEqual(this.coefficients, polynomialGF2mSmallM.coefficients);
    }
    
    private static boolean isEqual(final int[] array, final int[] array2) {
        final int computeDegree = computeDegree(array);
        if (computeDegree != computeDegree(array2)) {
            return false;
        }
        for (int i = 0; i <= computeDegree; ++i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hashCode = this.field.hashCode();
        for (int i = 0; i < this.coefficients.length; ++i) {
            hashCode = hashCode * 31 + this.coefficients[i];
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        String s = " Polynomial over " + this.field.toString() + ": \n";
        for (int i = 0; i < this.coefficients.length; ++i) {
            s = s + this.field.elementToStr(this.coefficients[i]) + "Y^" + i + "+";
        }
        return s + ";";
    }
    
    private void computeDegree() {
        this.degree = this.coefficients.length - 1;
        while (this.degree >= 0 && this.coefficients[this.degree] == 0) {
            --this.degree;
        }
    }
    
    private static int computeDegree(final int[] array) {
        int n;
        for (n = array.length - 1; n >= 0 && array[n] == 0; --n) {}
        return n;
    }
    
    private static int[] normalForm(final int[] array) {
        final int computeDegree = computeDegree(array);
        if (computeDegree == -1) {
            return new int[1];
        }
        if (array.length == computeDegree + 1) {
            return IntUtils.clone(array);
        }
        final int[] array2 = new int[computeDegree + 1];
        System.arraycopy(array, 0, array2, 0, computeDegree + 1);
        return array2;
    }
}
