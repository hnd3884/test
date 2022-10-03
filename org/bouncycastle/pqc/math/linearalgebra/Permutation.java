package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

public class Permutation
{
    private int[] perm;
    
    public Permutation(final int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("invalid length");
        }
        this.perm = new int[n];
        for (int i = n - 1; i >= 0; --i) {
            this.perm[i] = i;
        }
    }
    
    public Permutation(final int[] array) {
        if (!this.isPermutation(array)) {
            throw new IllegalArgumentException("array is not a permutation vector");
        }
        this.perm = IntUtils.clone(array);
    }
    
    public Permutation(final byte[] array) {
        if (array.length <= 4) {
            throw new IllegalArgumentException("invalid encoding");
        }
        final int os2IP = LittleEndianConversions.OS2IP(array, 0);
        final int ceilLog256 = IntegerFunctions.ceilLog256(os2IP - 1);
        if (array.length != 4 + os2IP * ceilLog256) {
            throw new IllegalArgumentException("invalid encoding");
        }
        this.perm = new int[os2IP];
        for (int i = 0; i < os2IP; ++i) {
            this.perm[i] = LittleEndianConversions.OS2IP(array, 4 + i * ceilLog256, ceilLog256);
        }
        if (!this.isPermutation(this.perm)) {
            throw new IllegalArgumentException("invalid encoding");
        }
    }
    
    public Permutation(final int n, final SecureRandom secureRandom) {
        if (n <= 0) {
            throw new IllegalArgumentException("invalid length");
        }
        this.perm = new int[n];
        final int[] array = new int[n];
        for (int i = 0; i < n; ++i) {
            array[i] = i;
        }
        int n2 = n;
        for (int j = 0; j < n; ++j) {
            final int nextInt = RandUtils.nextInt(secureRandom, n2);
            --n2;
            this.perm[j] = array[nextInt];
            array[nextInt] = array[n2];
        }
    }
    
    public byte[] getEncoded() {
        final int length = this.perm.length;
        final int ceilLog256 = IntegerFunctions.ceilLog256(length - 1);
        final byte[] array = new byte[4 + length * ceilLog256];
        LittleEndianConversions.I2OSP(length, array, 0);
        for (int i = 0; i < length; ++i) {
            LittleEndianConversions.I2OSP(this.perm[i], array, 4 + i * ceilLog256, ceilLog256);
        }
        return array;
    }
    
    public int[] getVector() {
        return IntUtils.clone(this.perm);
    }
    
    public Permutation computeInverse() {
        final Permutation permutation = new Permutation(this.perm.length);
        for (int i = this.perm.length - 1; i >= 0; --i) {
            permutation.perm[this.perm[i]] = i;
        }
        return permutation;
    }
    
    public Permutation rightMultiply(final Permutation permutation) {
        if (permutation.perm.length != this.perm.length) {
            throw new IllegalArgumentException("length mismatch");
        }
        final Permutation permutation2 = new Permutation(this.perm.length);
        for (int i = this.perm.length - 1; i >= 0; --i) {
            permutation2.perm[i] = this.perm[permutation.perm[i]];
        }
        return permutation2;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof Permutation && IntUtils.equals(this.perm, ((Permutation)o).perm);
    }
    
    @Override
    public String toString() {
        String s = "[" + this.perm[0];
        for (int i = 1; i < this.perm.length; ++i) {
            s = s + ", " + this.perm[i];
        }
        return s + "]";
    }
    
    @Override
    public int hashCode() {
        return this.perm.hashCode();
    }
    
    private boolean isPermutation(final int[] array) {
        final int length = array.length;
        final boolean[] array2 = new boolean[length];
        for (int i = 0; i < length; ++i) {
            if (array[i] < 0 || array[i] >= length || array2[array[i]]) {
                return false;
            }
            array2[array[i]] = true;
        }
        return true;
    }
}
