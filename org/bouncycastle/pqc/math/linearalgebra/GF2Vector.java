package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

public class GF2Vector extends Vector
{
    private int[] v;
    
    public GF2Vector(final int length) {
        if (length < 0) {
            throw new ArithmeticException("Negative length.");
        }
        this.length = length;
        this.v = new int[length + 31 >> 5];
    }
    
    public GF2Vector(final int length, final SecureRandom secureRandom) {
        this.length = length;
        final int n = length + 31 >> 5;
        this.v = new int[n];
        for (int i = n - 1; i >= 0; --i) {
            this.v[i] = secureRandom.nextInt();
        }
        final int n2 = length & 0x1F;
        if (n2 != 0) {
            final int[] v = this.v;
            final int n3 = n - 1;
            v[n3] &= (1 << n2) - 1;
        }
    }
    
    public GF2Vector(final int length, final int n, final SecureRandom secureRandom) {
        if (n > length) {
            throw new ArithmeticException("The hamming weight is greater than the length of vector.");
        }
        this.length = length;
        this.v = new int[length + 31 >> 5];
        final int[] array = new int[length];
        for (int i = 0; i < length; ++i) {
            array[i] = i;
        }
        int n2 = length;
        for (int j = 0; j < n; ++j) {
            final int nextInt = RandUtils.nextInt(secureRandom, n2);
            this.setBit(array[nextInt]);
            --n2;
            array[nextInt] = array[n2];
        }
    }
    
    public GF2Vector(final int length, final int[] array) {
        if (length < 0) {
            throw new ArithmeticException("negative length");
        }
        this.length = length;
        final int n = length + 31 >> 5;
        if (array.length != n) {
            throw new ArithmeticException("length mismatch");
        }
        this.v = IntUtils.clone(array);
        final int n2 = length & 0x1F;
        if (n2 != 0) {
            final int[] v = this.v;
            final int n3 = n - 1;
            v[n3] &= (1 << n2) - 1;
        }
    }
    
    public GF2Vector(final GF2Vector gf2Vector) {
        this.length = gf2Vector.length;
        this.v = IntUtils.clone(gf2Vector.v);
    }
    
    protected GF2Vector(final int[] v, final int length) {
        this.v = v;
        this.length = length;
    }
    
    public static GF2Vector OS2VP(final int n, final byte[] array) {
        if (n < 0) {
            throw new ArithmeticException("negative length");
        }
        if (array.length > n + 7 >> 3) {
            throw new ArithmeticException("length mismatch");
        }
        return new GF2Vector(n, LittleEndianConversions.toIntArray(array));
    }
    
    @Override
    public byte[] getEncoded() {
        return LittleEndianConversions.toByteArray(this.v, this.length + 7 >> 3);
    }
    
    public int[] getVecArray() {
        return this.v;
    }
    
    public int getHammingWeight() {
        int n = 0;
        for (int i = 0; i < this.v.length; ++i) {
            int n2 = this.v[i];
            for (int j = 0; j < 32; ++j) {
                if ((n2 & 0x1) != 0x0) {
                    ++n;
                }
                n2 >>>= 1;
            }
        }
        return n;
    }
    
    @Override
    public boolean isZero() {
        for (int i = this.v.length - 1; i >= 0; --i) {
            if (this.v[i] != 0) {
                return false;
            }
        }
        return true;
    }
    
    public int getBit(final int n) {
        if (n >= this.length) {
            throw new IndexOutOfBoundsException();
        }
        final int n2 = n >> 5;
        final int n3 = n & 0x1F;
        return (this.v[n2] & 1 << n3) >>> n3;
    }
    
    public void setBit(final int n) {
        if (n >= this.length) {
            throw new IndexOutOfBoundsException();
        }
        final int[] v = this.v;
        final int n2 = n >> 5;
        v[n2] |= 1 << (n & 0x1F);
    }
    
    @Override
    public Vector add(final Vector vector) {
        if (!(vector instanceof GF2Vector)) {
            throw new ArithmeticException("vector is not defined over GF(2)");
        }
        if (this.length != ((GF2Vector)vector).length) {
            throw new ArithmeticException("length mismatch");
        }
        final int[] clone = IntUtils.clone(((GF2Vector)vector).v);
        for (int i = clone.length - 1; i >= 0; --i) {
            final int[] array = clone;
            final int n = i;
            array[n] ^= this.v[i];
        }
        return new GF2Vector(this.length, clone);
    }
    
    @Override
    public Vector multiply(final Permutation permutation) {
        final int[] vector = permutation.getVector();
        if (this.length != vector.length) {
            throw new ArithmeticException("length mismatch");
        }
        final GF2Vector gf2Vector = new GF2Vector(this.length);
        for (int i = 0; i < vector.length; ++i) {
            if ((this.v[vector[i] >> 5] & 1 << (vector[i] & 0x1F)) != 0x0) {
                final int[] v = gf2Vector.v;
                final int n = i >> 5;
                v[n] |= 1 << (i & 0x1F);
            }
        }
        return gf2Vector;
    }
    
    public GF2Vector extractVector(final int[] array) {
        final int length = array.length;
        if (array[length - 1] > this.length) {
            throw new ArithmeticException("invalid index set");
        }
        final GF2Vector gf2Vector = new GF2Vector(length);
        for (int i = 0; i < length; ++i) {
            if ((this.v[array[i] >> 5] & 1 << (array[i] & 0x1F)) != 0x0) {
                final int[] v = gf2Vector.v;
                final int n = i >> 5;
                v[n] |= 1 << (i & 0x1F);
            }
        }
        return gf2Vector;
    }
    
    public GF2Vector extractLeftVector(final int n) {
        if (n > this.length) {
            throw new ArithmeticException("invalid length");
        }
        if (n == this.length) {
            return new GF2Vector(this);
        }
        final GF2Vector gf2Vector = new GF2Vector(n);
        final int n2 = n >> 5;
        final int n3 = n & 0x1F;
        System.arraycopy(this.v, 0, gf2Vector.v, 0, n2);
        if (n3 != 0) {
            gf2Vector.v[n2] = (this.v[n2] & (1 << n3) - 1);
        }
        return gf2Vector;
    }
    
    public GF2Vector extractRightVector(final int n) {
        if (n > this.length) {
            throw new ArithmeticException("invalid length");
        }
        if (n == this.length) {
            return new GF2Vector(this);
        }
        final GF2Vector gf2Vector = new GF2Vector(n);
        final int n2 = this.length - n >> 5;
        final int n3 = this.length - n & 0x1F;
        final int n4 = n + 31 >> 5;
        int n5 = n2;
        if (n3 != 0) {
            for (int i = 0; i < n4 - 1; ++i) {
                gf2Vector.v[i] = (this.v[n5++] >>> n3 | this.v[n5] << 32 - n3);
            }
            gf2Vector.v[n4 - 1] = this.v[n5++] >>> n3;
            if (n5 < this.v.length) {
                final int[] v = gf2Vector.v;
                final int n6 = n4 - 1;
                v[n6] |= this.v[n5] << 32 - n3;
            }
        }
        else {
            System.arraycopy(this.v, n2, gf2Vector.v, 0, n4);
        }
        return gf2Vector;
    }
    
    public GF2mVector toExtensionFieldVector(final GF2mField gf2mField) {
        final int degree = gf2mField.getDegree();
        if (this.length % degree != 0) {
            throw new ArithmeticException("conversion is impossible");
        }
        final int n = this.length / degree;
        final int[] array = new int[n];
        int n2 = 0;
        for (int i = n - 1; i >= 0; --i) {
            for (int j = gf2mField.getDegree() - 1; j >= 0; --j) {
                if ((this.v[n2 >>> 5] >>> (n2 & 0x1F) & 0x1) == 0x1) {
                    final int[] array2 = array;
                    final int n3 = i;
                    array2[n3] ^= 1 << j;
                }
                ++n2;
            }
        }
        return new GF2mVector(gf2mField, array);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof GF2Vector)) {
            return false;
        }
        final GF2Vector gf2Vector = (GF2Vector)o;
        return this.length == gf2Vector.length && IntUtils.equals(this.v, gf2Vector.v);
    }
    
    @Override
    public int hashCode() {
        return this.length * 31 + this.v.hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.length; ++i) {
            if (i != 0 && (i & 0x1F) == 0x0) {
                sb.append(' ');
            }
            if ((this.v[i >> 5] & 1 << (i & 0x1F)) == 0x0) {
                sb.append('0');
            }
            else {
                sb.append('1');
            }
        }
        return sb.toString();
    }
}
