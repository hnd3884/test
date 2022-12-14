package org.bouncycastle.pqc.math.linearalgebra;

public class GF2mVector extends Vector
{
    private GF2mField field;
    private int[] vector;
    
    public GF2mVector(final GF2mField gf2mField, final byte[] array) {
        this.field = new GF2mField(gf2mField);
        int n = 8;
        int n2 = 1;
        while (gf2mField.getDegree() > n) {
            ++n2;
            n += 8;
        }
        if (array.length % n2 != 0) {
            throw new IllegalArgumentException("Byte array is not an encoded vector over the given finite field.");
        }
        this.length = array.length / n2;
        this.vector = new int[this.length];
        int n3 = 0;
        for (int i = 0; i < this.vector.length; ++i) {
            for (int j = 0; j < n; j += 8) {
                final int[] vector = this.vector;
                final int n4 = i;
                vector[n4] |= (array[n3++] & 0xFF) << j;
            }
            if (!gf2mField.isElementOfThisField(this.vector[i])) {
                throw new IllegalArgumentException("Byte array is not an encoded vector over the given finite field.");
            }
        }
    }
    
    public GF2mVector(final GF2mField field, final int[] array) {
        this.field = field;
        this.length = array.length;
        for (int i = array.length - 1; i >= 0; --i) {
            if (!field.isElementOfThisField(array[i])) {
                throw new ArithmeticException("Element array is not specified over the given finite field.");
            }
        }
        this.vector = IntUtils.clone(array);
    }
    
    public GF2mVector(final GF2mVector gf2mVector) {
        this.field = new GF2mField(gf2mVector.field);
        this.length = gf2mVector.length;
        this.vector = IntUtils.clone(gf2mVector.vector);
    }
    
    public GF2mField getField() {
        return this.field;
    }
    
    public int[] getIntArrayForm() {
        return IntUtils.clone(this.vector);
    }
    
    @Override
    public byte[] getEncoded() {
        int n = 8;
        int n2 = 1;
        while (this.field.getDegree() > n) {
            ++n2;
            n += 8;
        }
        final byte[] array = new byte[this.vector.length * n2];
        int n3 = 0;
        for (int i = 0; i < this.vector.length; ++i) {
            for (int j = 0; j < n; j += 8) {
                array[n3++] = (byte)(this.vector[i] >>> j);
            }
        }
        return array;
    }
    
    @Override
    public boolean isZero() {
        for (int i = this.vector.length - 1; i >= 0; --i) {
            if (this.vector[i] != 0) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Vector add(final Vector vector) {
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public Vector multiply(final Permutation permutation) {
        final int[] vector = permutation.getVector();
        if (this.length != vector.length) {
            throw new ArithmeticException("permutation size and vector size mismatch");
        }
        final int[] array = new int[this.length];
        for (int i = 0; i < vector.length; ++i) {
            array[i] = this.vector[vector[i]];
        }
        return new GF2mVector(this.field, array);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof GF2mVector)) {
            return false;
        }
        final GF2mVector gf2mVector = (GF2mVector)o;
        return this.field.equals(gf2mVector.field) && IntUtils.equals(this.vector, gf2mVector.vector);
    }
    
    @Override
    public int hashCode() {
        return this.field.hashCode() * 31 + this.vector.hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.vector.length; ++i) {
            for (int j = 0; j < this.field.getDegree(); ++j) {
                if ((this.vector[i] & 1 << (j & 0x1F)) != 0x0) {
                    sb.append('1');
                }
                else {
                    sb.append('0');
                }
            }
            sb.append(' ');
        }
        return sb.toString();
    }
}
