package org.bouncycastle.math.raw;

public abstract class Nat512
{
    public static void mul(final int[] array, final int[] array2, final int[] array3) {
        Nat256.mul(array, array2, array3);
        Nat256.mul(array, 8, array2, 8, array3, 16);
        final int addToEachOther = Nat256.addToEachOther(array3, 8, array3, 16);
        final int n = addToEachOther + Nat256.addTo(array3, 24, array3, 16, addToEachOther + Nat256.addTo(array3, 0, array3, 8, 0));
        final int[] create = Nat256.create();
        final int[] create2 = Nat256.create();
        final boolean b = Nat256.diff(array, 8, array, 0, create, 0) != Nat256.diff(array2, 8, array2, 0, create2, 0);
        final int[] ext = Nat256.createExt();
        Nat256.mul(create, create2, ext);
        Nat.addWordAt(32, n + (b ? Nat.addTo(16, ext, 0, array3, 8) : Nat.subFrom(16, ext, 0, array3, 8)), array3, 24);
    }
    
    public static void square(final int[] array, final int[] array2) {
        Nat256.square(array, array2);
        Nat256.square(array, 8, array2, 16);
        final int addToEachOther = Nat256.addToEachOther(array2, 8, array2, 16);
        final int n = addToEachOther + Nat256.addTo(array2, 24, array2, 16, addToEachOther + Nat256.addTo(array2, 0, array2, 8, 0));
        final int[] create = Nat256.create();
        Nat256.diff(array, 8, array, 0, create, 0);
        final int[] ext = Nat256.createExt();
        Nat256.square(create, ext);
        Nat.addWordAt(32, n + Nat.subFrom(16, ext, 0, array2, 8), array2, 24);
    }
}
