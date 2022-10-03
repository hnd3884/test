package org.bouncycastle.math.raw;

import java.util.Random;

public abstract class Mod
{
    public static int inverse32(final int n) {
        final int n2 = n * (2 - n * n);
        final int n3 = n2 * (2 - n * n2);
        final int n4 = n3 * (2 - n * n3);
        return n4 * (2 - n * n4);
    }
    
    public static void invert(final int[] array, final int[] array2, final int[] array3) {
        final int length = array.length;
        if (Nat.isZero(length, array2)) {
            throw new IllegalArgumentException("'x' cannot be 0");
        }
        if (Nat.isOne(length, array2)) {
            System.arraycopy(array2, 0, array3, 0, length);
            return;
        }
        final int[] copy = Nat.copy(length, array2);
        final int[] create = Nat.create(length);
        create[0] = 1;
        int n = 0;
        if ((copy[0] & 0x1) == 0x0) {
            n = inversionStep(array, copy, length, create, n);
        }
        if (Nat.isOne(length, copy)) {
            inversionResult(array, n, create, array3);
            return;
        }
        final int[] copy2 = Nat.copy(length, array);
        final int[] create2 = Nat.create(length);
        int inversionStep = 0;
        int n2 = length;
        while (true) {
            if (copy[n2 - 1] == 0 && copy2[n2 - 1] == 0) {
                --n2;
            }
            else if (Nat.gte(n2, copy, copy2)) {
                Nat.subFrom(n2, copy2, copy);
                n = inversionStep(array, copy, n2, create, n + (Nat.subFrom(length, create2, create) - inversionStep));
                if (Nat.isOne(n2, copy)) {
                    inversionResult(array, n, create, array3);
                    return;
                }
                continue;
            }
            else {
                Nat.subFrom(n2, copy, copy2);
                inversionStep = inversionStep(array, copy2, n2, create2, inversionStep + (Nat.subFrom(length, create, create2) - n));
                if (Nat.isOne(n2, copy2)) {
                    inversionResult(array, inversionStep, create2, array3);
                    return;
                }
                continue;
            }
        }
    }
    
    public static int[] random(final int[] array) {
        final int length = array.length;
        final Random random = new Random();
        final int[] create = Nat.create(length);
        final int n = array[length - 1];
        final int n2 = n | n >>> 1;
        final int n3 = n2 | n2 >>> 2;
        final int n4 = n3 | n3 >>> 4;
        final int n5 = n4 | n4 >>> 8;
        final int n6 = n5 | n5 >>> 16;
        do {
            for (int i = 0; i != length; ++i) {
                create[i] = random.nextInt();
            }
            final int[] array2 = create;
            final int n7 = length - 1;
            array2[n7] &= n6;
        } while (Nat.gte(length, create, array));
        return create;
    }
    
    public static void add(final int[] array, final int[] array2, final int[] array3, final int[] array4) {
        final int length = array.length;
        if (Nat.add(length, array2, array3, array4) != 0) {
            Nat.subFrom(length, array, array4);
        }
    }
    
    public static void subtract(final int[] array, final int[] array2, final int[] array3, final int[] array4) {
        final int length = array.length;
        if (Nat.sub(length, array2, array3, array4) != 0) {
            Nat.addTo(length, array, array4);
        }
    }
    
    private static void inversionResult(final int[] array, final int n, final int[] array2, final int[] array3) {
        if (n < 0) {
            Nat.add(array.length, array2, array, array3);
        }
        else {
            System.arraycopy(array2, 0, array3, 0, array.length);
        }
    }
    
    private static int inversionStep(final int[] array, final int[] array2, final int n, final int[] array3, int n2) {
        final int length = array.length;
        int n3 = 0;
        while (array2[0] == 0) {
            Nat.shiftDownWord(n, array2, 0);
            n3 += 32;
        }
        final int trailingZeroes = getTrailingZeroes(array2[0]);
        if (trailingZeroes > 0) {
            Nat.shiftDownBits(n, array2, trailingZeroes, 0);
            n3 += trailingZeroes;
        }
        for (int i = 0; i < n3; ++i) {
            if ((array3[0] & 0x1) != 0x0) {
                if (n2 < 0) {
                    n2 += Nat.addTo(length, array, array3);
                }
                else {
                    n2 += Nat.subFrom(length, array, array3);
                }
            }
            Nat.shiftDownBit(length, array3, n2);
        }
        return n2;
    }
    
    private static int getTrailingZeroes(int n) {
        int n2;
        for (n2 = 0; (n & 0x1) == 0x0; n >>>= 1, ++n2) {}
        return n2;
    }
}
